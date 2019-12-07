package com.olbius.salesmtl;

import java.beans.BeanInfo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.olbius.basehr.importExport.ImportExportExcel;
import com.olbius.basehr.importExport.ImportExportWorker;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basesales.forecast.SalesForecastWorker;
import com.olbius.basesales.util.SalesUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

public class SalesStatementEvents {
	public static final String module = SalesStatementEvents.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
    public static int AMOUNT_DECIMAL = UtilNumber.getBigDecimalScale("salestax.final.decimals");
	
	@SuppressWarnings("unchecked")
	public static String createUpdateSalesStatementAdvanceJson(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        String parentSalesStatementId = request.getParameter("salesStatementId");
        String productListStr = request.getParameter("productList");
		try {
			dispatcher.runSync("removeStatementOfDisabledNPP", UtilMisc.toMap("salesStatementId", parentSalesStatementId, "userLogin", userLogin));
		} catch (GenericServiceException e2) {
            Debug.logError(e2, module);
            return "error";
		}
        boolean beganTx = false;
        try {
        	// begin the transaction
        	beganTx = TransactionUtil.begin(7200);
        	
        	GenericValue parentSalesStatement = delegator.findOne("SalesStatement", UtilMisc.toMap("salesStatementId", parentSalesStatementId), false);
            if (parentSalesStatement == null) {
            	request.setAttribute("_ERROR_MESSAGE_", "Sales statement not found");
            	try {
                    TransactionUtil.rollback(beganTx, "Failure in processing query select sales statement callback", null);
                } catch (Exception e1) {
                    Debug.logError(e1, module);
                }
            	return "error";
            }
            
            String currentStatusId = parentSalesStatement.getString("statusId");
            if ("SALES_SM_CANCELLED".equals(currentStatusId)) {
            	parentSalesStatement.set("statusId", "SALES_SM_CREATED");
            	delegator.store(parentSalesStatement);
            	
            	GenericValue salesStatementStatus = delegator.makeValue("SalesStatementStatus");
                salesStatementStatus.put("salesStatementStatusId", delegator.getNextSeqId("SalesStatementStatus"));
                salesStatementStatus.put("statusId", "SALES_SM_CREATED");
                salesStatementStatus.put("salesStatementId", parentSalesStatement.get("salesStatementId"));
                salesStatementStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
                salesStatementStatus.put("statusUserLogin", userLogin.getString("userLoginId"));
                salesStatementStatus.create();
            }
            
            String salesStatementTypeId = parentSalesStatement.getString("salesStatementTypeId");
            String currencyUomId = parentSalesStatement.getString("currencyUomId");
            String statusId = parentSalesStatement.getString("statusId");
            String salesForecastId = parentSalesStatement.getString("salesForecastId");
            //String parentOrganizationPartyId = parentSalesStatement.getString("organizationPartyId");
            String parentInternalPartyId = parentSalesStatement.getString("internalPartyId");
            String customTimePeriodId = parentSalesStatement.getString("customTimePeriodId");
            
    		JSONArray jsonProductArray = new JSONArray();
    		if(UtilValidate.isNotEmpty(productListStr)){
    			jsonProductArray = JSONArray.fromObject(productListStr);
    		}
    		Map<String, Object> partyAndProducts = FastMap.newInstance();
    		//List<Map<String, Object>> listProduct = FastList.newInstance();
    		List<String> listProductIds = FastList.newInstance();
    		if (jsonProductArray != null && jsonProductArray.size() > 0) {
    			for (int i = 0; i < jsonProductArray.size(); i++) {
    				JSONObject prodItem = jsonProductArray.getJSONObject(i);
    				String m_productId = prodItem.getString("productId");
    				String m_quantity = prodItem.getString("quantity");
    				String m_partyId = prodItem.getString("partyId");
    				if (m_partyId != null) {
    					Map<String, Object> itemList = null;
    					if (partyAndProducts.containsKey(m_partyId)) {
    						itemList = (Map<String, Object>) partyAndProducts.get(m_partyId);
    					} else {
    						itemList = FastMap.newInstance();
    						partyAndProducts.put(m_partyId, itemList);
    					}
    					//itemMap.put("productId", m_productId);
    					//itemMap.put("quantity", m_quantity);
    					//itemList.add(itemMap);
    					itemList.put(m_productId, m_quantity);
    				}
    				if (UtilValidate.isNotEmpty(m_productId) && !listProductIds.contains(m_productId)) {
    					listProductIds.add(m_productId);
    				}
    			}
    		}
            
    		if (UtilValidate.isNotEmpty(partyAndProducts)) {
    			// find sales forecast of month
    			boolean isUpdateS4C = false;
    			GenericValue salesForecast = null;
    			GenericValue salesForecastMonth = null;
    			if ("SALES_IN".equals(salesStatementTypeId)) {
    				if (UtilValidate.isNotEmpty(salesForecastId)) {
        				salesForecast = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId), false);
        				if (salesForecast != null) {
        					isUpdateS4C = true;
        					salesForecastMonth = EntityUtil.getFirst(delegator.findByAnd("SalesForecast", UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "parentSalesForecastId", salesForecastId), null, false));
        				}
        			}
    			}
    			
    			List<GenericValue> tobeStored = new LinkedList<GenericValue>();
    			
    			// delete sales statement detail all
    			Set<String> partyIdsStore = partyAndProducts.keySet();
    			/*List<EntityCondition> condsRemove = FastList.newInstance();
    			condsRemove.add(EntityCondition.makeCondition("organizationPartyId", parentInternalPartyId));
    			condsRemove.add(EntityCondition.makeCondition("salesStatementTypeId", salesStatementTypeId));
    			condsRemove.add(EntityCondition.makeCondition("parentSalesStatementId", parentSalesStatementId));
    			condsRemove.add(EntityCondition.makeCondition("internalPartyId", EntityOperator.NOT_IN, partyIdsStore));
    			List<String> salesStatementIdsRemove = EntityUtil.getFieldListFromEntityList(delegator.findList("SalesStatement", EntityCondition.makeCondition(condsRemove, EntityOperator.AND), null, null, null, false), "salesStatementId", true);
    			if (UtilValidate.isNotEmpty(salesStatementIdsRemove)) {
    				List<GenericValue> listSalesStatementRemove = delegator.findList("SalesStatementDetail", 
									EntityCondition.makeCondition("salesStatementId", EntityOperator.IN, salesStatementIdsRemove), null, null, null, false);
					if (listSalesStatementRemove != null) {
						delegator.removeAll(listSalesStatementRemove);
					}
    			}*/
    			
    			List<EntityCondition> listCond = FastList.newInstance();
    			listCond.add(EntityCondition.makeCondition(UtilMisc.toMap("organizationPartyId", parentInternalPartyId, "salesStatementTypeId", salesStatementTypeId, "parentSalesStatementId", parentSalesStatementId)));
    			listCond.add(EntityCondition.makeCondition("internalPartyId", EntityOperator.IN, partyIdsStore));
    			List<GenericValue> listSalesStatement = delegator.findList("SalesStatement", EntityCondition.makeCondition(listCond), null, null, null, false);
    			// create or update
    			for (Map.Entry<String, Object> partyAndProductsItem : partyAndProducts.entrySet()) {
    				String partyId = (String) partyAndProductsItem.getKey();
    				long nextSeqNum = 1;
    				try {
    					GenericValue salesStatementDetailTmpNextSeq = delegator.makeValue("SalesStatementDetail");
            			delegator.setNextSubSeqId(salesStatementDetailTmpNextSeq, "salesStatementDetailSeqId", 5, 1);
            			String nextSeqNumStr = salesStatementDetailTmpNextSeq.getString("salesStatementDetailSeqId");
            			nextSeqNum = Long.parseLong(nextSeqNumStr);
    				} catch (Exception e) {
    					Debug.logWarning("Error when parse salesStatementDetailSeqId to long", module);
    					nextSeqNum = 1;
    				}
    				if (nextSeqNum < 1) nextSeqNum = 1;
    				
    				// salesStatement
            		GenericValue salesStatement = EntityUtil.getFirst(EntityUtil.filterByAnd(listSalesStatement, UtilMisc.toMap("internalPartyId", partyId)));
            		if (salesStatement != null) {
            			// is existed -> update
            		} else {
            			// create
            			String salesStatementId = delegator.getNextSeqId("SalesStatement");
            			GenericValue salesStatementNew = delegator.makeValue("SalesStatement");
            			salesStatementNew.put("salesStatementId", salesStatementId);
            			salesStatementNew.put("salesStatementTypeId", salesStatementTypeId);
            			salesStatementNew.put("parentSalesStatementId", parentSalesStatementId);
            			salesStatementNew.put("organizationPartyId", parentInternalPartyId);
            			salesStatementNew.put("internalPartyId", partyId);
            			salesStatementNew.put("customTimePeriodId", customTimePeriodId);
            			salesStatementNew.put("statusId", statusId);
            			salesStatementNew.put("currencyUomId", currencyUomId);
            			salesStatementNew.put("salesForecastId", salesForecastId);
            			salesStatementNew.put("createdBy", userLogin.getString("userLoginId"));
            			salesStatementNew.put("modifiedBy", userLogin.getString("userLoginId"));
            			delegator.create(salesStatementNew);
            			salesStatement = salesStatementNew;
            		}
            		
    				if (salesStatement != null) {
    					Map<String, Object> productMap = (Map<String, Object>) partyAndProductsItem.getValue();
    					if (productMap != null) {
    						/*Set<String> productIdsStore = productMap.keySet();
    						// delete sales statement detail
    						List<GenericValue> listSalesStatementRemove = delegator.findList("SalesStatementDetail", 
											EntityCondition.makeCondition(EntityCondition.makeCondition("salesStatementId", salesStatement.getString("salesStatementId")), 
    										EntityOperator.AND, 
											EntityCondition.makeCondition("productId", EntityOperator.NOT_IN, productIdsStore)), null, null, null, false);
    						if (listSalesStatementRemove != null) {
    							delegator.removeAll(listSalesStatementRemove);
    						}*/
    						// create and update
    						for (Map.Entry<String, Object> prodEntry : productMap.entrySet()) {
    			        		String productId = (String) prodEntry.getKey();
    			            	String quantityNewStr = (String) prodEntry.getValue();
    			            	BigDecimal quantityNew = BigDecimal.ZERO;
    			            	
    			            	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
    			            	if (product == null) continue;
    			            	String quantityUomId = product.getString("quantityUomId");
    			            	
    			            	if (quantityNewStr != null) {
    			            		try {
    			                        quantityNew = new BigDecimal(quantityNewStr);
    			                    } catch (Exception e) {
    			                        Debug.logWarning(e, "Problems parsing quantity string: " + quantityNewStr, module);
    			                        quantityNew = new BigDecimal("-1");
    			                    }
    			            	}
    			        		
    			        		// get sales statement detail
    			        		GenericValue salesStatementDetail = EntityUtil.getFirst(delegator.findByAnd("SalesStatementDetail", 
    			        				UtilMisc.toMap("salesStatementId", salesStatement.getString("salesStatementId"), "productId", productId), null, false));
    			        		BigDecimal quantityOld = BigDecimal.ZERO;
    			        		if (salesStatementDetail != null) {
    			        			// update quantity
    			        			quantityOld = salesStatementDetail.getBigDecimal("quantity");
    			        			if (quantityNew.compareTo(quantityOld) != 0) {
    			        				// calculate product price
    			        				Map<String, Object> resultProdPrice = dispatcher.runSync("calculateProductPriceCustom", 
    			        						UtilMisc.toMap("product", product, "userLogin", userLogin));
    			        				BigDecimal productPrice = BigDecimal.ZERO;
    			        				if (ServiceUtil.isSuccess(resultProdPrice)) {
    			        					productPrice = (BigDecimal) resultProdPrice.get("price");
    			        				}
    			        				BigDecimal amount = productPrice.multiply(quantityNew).setScale(AMOUNT_DECIMAL, RoundingMode.HALF_UP);
    			        				salesStatementDetail.put("quantity", quantityNew);
    			        				salesStatementDetail.put("amount", amount);
    			        				tobeStored.add(salesStatementDetail);
    			        			}
    			        		} else {
    			        			// calculate product price
			        				Map<String, Object> resultProdPrice = dispatcher.runSync("calculateProductPriceCustom", 
			        						UtilMisc.toMap("product", product, "userLogin", userLogin));
			        				BigDecimal productPrice = BigDecimal.ZERO;
			        				if (ServiceUtil.isSuccess(resultProdPrice)) {
			        					productPrice = (BigDecimal) resultProdPrice.get("price");
			        				}
			        				BigDecimal amount = productPrice.multiply(quantityNew).setScale(AMOUNT_DECIMAL, RoundingMode.HALF_UP);
			        				
    			        			salesStatementDetail = delegator.makeValue("SalesStatementDetail");
    			        			salesStatementDetail.put("salesStatementId", salesStatement.get("salesStatementId"));
    			        			salesStatementDetail.put("productId", productId);
    			        			salesStatementDetail.put("quantityUomId", quantityUomId);
    			        			salesStatementDetail.put("quantity", quantityNew);
    			        			salesStatementDetail.put("amount", amount);
    			        			String salesStatementDetailSeqId = UtilFormatOut.formatPaddedNumber(nextSeqNum, 5);
			                        salesStatementDetail.put("salesStatementDetailSeqId", salesStatementDetailSeqId);
			                        nextSeqNum++;
    			        			tobeStored.add(salesStatementDetail);
    			        		}
    						}
    					}
    				}
    			}
    			delegator.storeAll(tobeStored);
    			
    			if (UtilValidate.isNotEmpty(listProductIds)) {
					// update sales forecast
	        		if (isUpdateS4C) {
	        			if (salesForecastMonth == null) {
	        				// create sales forecast month
	        				Map<String, Object> salesForecastMonthCtx = FastMap.newInstance();
	        				salesForecastMonthCtx.put("salesForecastId", delegator.getNextSeqId("SalesForecast"));
	        				salesForecastMonthCtx.put("parentSalesForecastId", salesForecast.get("salesForecastId"));
	        				salesForecastMonthCtx.put("organizationPartyId", salesForecast.get("organizationPartyId"));
	        				salesForecastMonthCtx.put("customTimePeriodId", customTimePeriodId);
	        				salesForecastMonthCtx.put("currencyUomId", salesForecast.get("currencyUomId"));
	        				salesForecastMonthCtx.put("userLogin", userLogin);
	        				
                            try {
                                Map<String, Object> resultS4CNew = dispatcher.runSync("createSalesForecastCustom", salesForecastMonthCtx);
                                if (ServiceUtil.isError(resultS4CNew)) {
                                	Debug.logError(ServiceUtil.getErrorMessage(resultS4CNew), module);
                                	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
                                    try {
                                        TransactionUtil.rollback(beganTx, "Failure in processing create or update callback", null);
                                    } catch (Exception e1) {
                                        Debug.logError(e1, module);
                                    }
                                    return "error";
                                }
                                String s4cIdNew = (String) resultS4CNew.get("salesForecastId");
                                salesForecastMonth = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", s4cIdNew), false);
                            } catch (GenericServiceException e) {
                                Debug.logError(e, module);
                                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
                                try {
                                    TransactionUtil.rollback(beganTx, "Failure in processing create or update callback", null);
                                } catch (Exception e1) {
                                    Debug.logError(e1, module);
                                }
                                return "error";
                            }
	        			}
	        			
	        			if (salesForecastMonth != null) {
	        				List<String> salesStatementIdsAfter = EntityUtil.getFieldListFromEntityList(
	        						delegator.findList("SalesStatement", EntityCondition.makeCondition(UtilMisc.toMap("organizationPartyId", parentInternalPartyId, "salesStatementTypeId", salesStatementTypeId, "parentSalesStatementId", parentSalesStatementId)), null, null, null, false), "salesStatementId", false);
	        				if (UtilValidate.isNotEmpty(salesStatementIdsAfter)) {
	        					List<GenericValue> tobeStored2 = new LinkedList<GenericValue>();
	        					for (String productId : listProductIds) {
		        					// get list sales statement detail by product id
	        						List<GenericValue> salesStatementDetails = delegator.findList("SalesStatementDetail", 
		        							EntityCondition.makeCondition(EntityCondition.makeCondition("salesStatementId", EntityOperator.IN, salesStatementIdsAfter), 
		        									EntityOperator.AND, EntityCondition.makeCondition("productId", productId)), null, null, null, false);
	        						BigDecimal quantitySum = BigDecimal.ZERO;
	        						if (UtilValidate.isNotEmpty(salesStatementDetails)) {
        								for (GenericValue salesStatementDetail : salesStatementDetails) {
        									quantitySum = quantitySum.add(salesStatementDetail.getBigDecimal("quantity"));
        								}
        							}
	        						GenericValue salesForecastDetailMonth = EntityUtil.getFirst(delegator.findByAnd("SalesForecastDetail", 
				        					UtilMisc.toMap("salesForecastId", salesForecastMonth.get("salesForecastId"), "productId", productId), null, false));
	        						if (salesForecastDetailMonth != null) {
	        							salesForecastDetailMonth.set("quantity", quantitySum);
	        						} else {
	        							salesForecastDetailMonth = delegator.makeValue("SalesForecastDetail");
	        							salesForecastDetailMonth.set("salesForecastId", salesForecastMonth.get("salesForecastId"));
	        							salesForecastDetailMonth.set("salesForecastDetailId", delegator.getNextSeqId("SalesForecastDetail"));
	        							salesForecastDetailMonth.set("productId", productId);
	        							salesForecastDetailMonth.set("quantity", quantitySum);
	        						}
        							tobeStored2.add(salesForecastDetailMonth);
		        				}
	        					delegator.storeAll(tobeStored2);
	        				}
	        			}
	        		}
	        		// end update sales forecast
    			}
    		}
        } catch (Exception e) {
             request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
             try {
                 TransactionUtil.rollback(beganTx, "Failure in processing create or update callback", null);
             } catch (Exception e1) {
                 Debug.logError(e1, module);
             }
             return "error";
        } finally {
        	// commit the transaction
            try {
                TransactionUtil.commit(beganTx);
            } catch (Exception e) {
                Debug.logError(e, module);
            }
        }

        // Determine where to send the browser
        return "success";
    }
	public static String createSalesStatementDetailByExcel(HttpServletRequest request, HttpServletResponse response) {
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map<String, Object> successReturn = ServiceUtil.returnSuccess();
        Locale locale = UtilHttp.getLocale(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        List<EntityCondition> conds = FastList.newInstance();
        boolean beganTx = false;
        try {
            beganTx = TransactionUtil.begin(7200);
            Map<String, Object> parametersMap = CommonUtil.getParameterMapWithFileUploaded(request);
            String sheetIndexStr = (String) parametersMap.get("sheetIndex");
            String startLineStr = (String) parametersMap.get("startLine");
            String columnMapJson = (String) parametersMap.get("columnMap");

            String parentSalesStatementId = (String) parametersMap.get("salesStatementId");
            GenericValue parentSalesStatement = delegator.findOne("SalesStatement", UtilMisc.toMap("salesStatementId", parentSalesStatementId), false);
            if (parentSalesStatement == null) {
                request.setAttribute("_ERROR_MESSAGE_", "Sales statement not found");
                try {
                    TransactionUtil.rollback(beganTx, "Failure in processing query select sales statement callback", null);
                } catch (Exception e1) {
                    Debug.logError(e1, module);
                }
                return "error";
            }
            String currentStatusId = parentSalesStatement.getString("statusId");
            if ("SALES_SM_CANCELLED".equals(currentStatusId)) {
                parentSalesStatement.set("statusId", "SALES_SM_CREATED");
                delegator.store(parentSalesStatement);

                GenericValue salesStatementStatus = delegator.makeValue("SalesStatementStatus");
                salesStatementStatus.put("salesStatementStatusId", delegator.getNextSeqId("SalesStatementStatus"));
                salesStatementStatus.put("statusId", "SALES_SM_CREATED");
                salesStatementStatus.put("salesStatementId", parentSalesStatement.get("salesStatementId"));
                salesStatementStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
                salesStatementStatus.put("statusUserLogin", userLogin.getString("userLoginId"));
                salesStatementStatus.create();
            }
            String salesStatementTypeId = parentSalesStatement.getString("salesStatementTypeId");
            String currencyUomId = parentSalesStatement.getString("currencyUomId");
            String statusId = parentSalesStatement.getString("statusId");
            String salesForecastId = parentSalesStatement.getString("salesForecastId");
            String parentInternalPartyId = parentSalesStatement.getString("internalPartyId");
            String customTimePeriodId = parentSalesStatement.getString("customTimePeriodId");

            //Boc du lieu.
            int startLine = 0;
            try {
                startLine = Integer.parseInt(startLineStr);
            } catch (NumberFormatException e) {
                startLine = 0;
            }


            List<Map<String, Object>> listFileUploaded = (List<Map<String, Object>>) parametersMap.get("listFileUploaded");
            if (UtilValidate.isEmpty(listFileUploaded)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "CannotNotFindExcelFileToImport", locale));
                //TransactionUtil.rollback();
                TransactionUtil.rollback(beganTx, "Failure in processing query read File SalesStatement roll back", null);
                return "error";
            }
            Map<String, Object> fileUploadedMap = listFileUploaded.get(0);
            String _uploadedFile_contentType = (String) fileUploadedMap.get("_uploadedFile_contentType");
            ByteBuffer uploadedFile = (ByteBuffer) fileUploadedMap.get("uploadedFile");
            if (!ImportExportExcel.isExcelFile(_uploadedFile_contentType)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "OnlyAccpetXLSXFile", locale));
                //TransactionUtil.rollback();
                TransactionUtil.rollback(beganTx, "Failure in processing query read File SalesStatement OnlyAccpetXLSXFile roll back", null);
                return "error";
            }
            TransactionUtil.commit();
            Map<Integer, Object> columnExcelMap = ImportExportWorker.readColumnMapFromJson(columnMapJson);
            List<Map<String, Object>> salesStatementDetailsData = SalesStatementWorker.getSalesStatementSheetDetail(dispatcher, delegator, userLogin, locale,
                    uploadedFile, columnExcelMap, Integer.parseInt(sheetIndexStr), startLine);
            if (UtilValidate.isEmpty(salesStatementDetailsData)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "NoDataFromExcelImported", locale));
                return "error";
            }

            //get products
            List<String> partyCodes = FastList.newInstance();
            for (Map<String, Object> aSalesStatementDetail : salesStatementDetailsData) {
                partyCodes.add((String) aSalesStatementDetail.get("partyCode"));
            }
            conds.clear();
            conds.add(EntityCondition.makeCondition("partyCode", EntityOperator.IN, partyCodes));
            List<GenericValue> partys = delegator.findList("Party", EntityCondition.makeCondition(conds), UtilMisc.toSet("partyId", "partyCode"), null, null, false);
            Map<String, Object> partyMap = FastMap.newInstance();
            for (GenericValue aParty : partys) {
                partyMap.put(aParty.getString("partyCode"), aParty.getString("partyId"));
            }

            //convert salesStatementDetailsData
            //productId, quantity, partyId
            List<Map<String, Object>> salesStatementDetailConverteds = FastList.newInstance();
            Map<String, Object> tmpMap = FastMap.newInstance();
            for (Map<String, Object> salesStatementDetail : salesStatementDetailsData) {
                if (salesStatementDetail.size() > 1) { // ensure: partyCode, productId, quantity
                    for (String key : salesStatementDetail.keySet()) {
                        if (key.contains("prodCode")) {
                            tmpMap = FastMap.newInstance();
                            tmpMap.put("partyId", partyMap.get(salesStatementDetail.get("partyCode")));
                            tmpMap.put("productId", key.substring("prodCode_".length()));
                            tmpMap.put("quantity", salesStatementDetail.get(key));
                            salesStatementDetailConverteds.add(tmpMap);
                        }
                    }
                }
            }

            //import data to db  salesFCDetails from file, salesForecastDetails from db
            Map<String, Object> partyAndProducts = FastMap.newInstance();
            //List<Map<String, Object>> listProduct = FastList.newInstance();
            List<String> listProductIds = FastList.newInstance();
            if (salesStatementDetailConverteds != null && salesStatementDetailConverteds.size() > 0) {
                for (int i = 0; i < salesStatementDetailConverteds.size(); i++) {
                    Map<String, Object> prodItem = salesStatementDetailConverteds.get(i);
                    String m_productId = (String) prodItem.get("productId");
                    Double m_quantity = (Double) prodItem.get("quantity");
                    String m_partyId = (String) prodItem.get("partyId");
                    if (m_partyId != null) {
                        Map<String, Object> itemList = null;
                        if (partyAndProducts.containsKey(m_partyId)) {
                            itemList = (Map<String, Object>) partyAndProducts.get(m_partyId);
                        } else {
                            itemList = FastMap.newInstance();
                            partyAndProducts.put(m_partyId, itemList);
                        }
                        //itemMap.put("productId", m_productId);
                        //itemMap.put("quantity", m_quantity);
                        //itemList.add(itemMap);
                        itemList.put(m_productId, m_quantity);
                    }
                    if (UtilValidate.isNotEmpty(m_productId) && !listProductIds.contains(m_productId)) {
                        listProductIds.add(m_productId);
                    }
                }
            }

            if (UtilValidate.isNotEmpty(partyAndProducts)) {
                // find sales forecast of month
                boolean isUpdateS4C = false;
                GenericValue salesForecast = null;
                GenericValue salesForecastMonth = null;
                if ("SALES_IN".equals(salesStatementTypeId)) {
                    if (UtilValidate.isNotEmpty(salesForecastId)) {
                        salesForecast = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId), false);
                        if (salesForecast != null) {
                            isUpdateS4C = true;
                            salesForecastMonth = EntityUtil.getFirst(delegator.findByAnd("SalesForecast", UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "parentSalesForecastId", salesForecastId), null, false));
                        }
                    }
                }

                List<GenericValue> tobeStored = new LinkedList<GenericValue>();

                // delete sales statement detail all
                Set<String> partyIdsStore = partyAndProducts.keySet();
                List<EntityCondition> listCond = FastList.newInstance();
                listCond.add(EntityCondition.makeCondition(UtilMisc.toMap("organizationPartyId", parentInternalPartyId, "salesStatementTypeId", salesStatementTypeId, "parentSalesStatementId", parentSalesStatementId)));
                listCond.add(EntityCondition.makeCondition("internalPartyId", EntityOperator.IN, partyIdsStore));
                List<GenericValue> listSalesStatement = delegator.findList("SalesStatement", EntityCondition.makeCondition(listCond), null, null, null, false);
                // create or update
                for (Map.Entry<String, Object> partyAndProductsItem : partyAndProducts.entrySet()) {
                    String partyId = (String) partyAndProductsItem.getKey();
                    long nextSeqNum = 1;
                    try {
                        GenericValue salesStatementDetailTmpNextSeq = delegator.makeValue("SalesStatementDetail");
                        delegator.setNextSubSeqId(salesStatementDetailTmpNextSeq, "salesStatementDetailSeqId", 5, 1);
                        String nextSeqNumStr = salesStatementDetailTmpNextSeq.getString("salesStatementDetailSeqId");
                        nextSeqNum = Long.parseLong(nextSeqNumStr);
                    } catch (Exception e) {
                        Debug.logWarning("Error when parse salesStatementDetailSeqId to long", module);
                        nextSeqNum = 1;
                    }
                    if (nextSeqNum < 1) nextSeqNum = 1;

                    // salesStatement
                    GenericValue salesStatement = EntityUtil.getFirst(EntityUtil.filterByAnd(listSalesStatement, UtilMisc.toMap("internalPartyId", partyId)));
                    if (salesStatement != null) {
                        // is existed -> update
                    } else {
                        // create
                        String salesStatementId = delegator.getNextSeqId("SalesStatement");
                        GenericValue salesStatementNew = delegator.makeValue("SalesStatement");
                        salesStatementNew.put("salesStatementId", salesStatementId);
                        salesStatementNew.put("salesStatementTypeId", salesStatementTypeId);
                        salesStatementNew.put("parentSalesStatementId", parentSalesStatementId);
                        salesStatementNew.put("organizationPartyId", parentInternalPartyId);
                        salesStatementNew.put("internalPartyId", partyId);
                        salesStatementNew.put("customTimePeriodId", customTimePeriodId);
                        salesStatementNew.put("statusId", statusId);
                        salesStatementNew.put("currencyUomId", currencyUomId);
                        salesStatementNew.put("salesForecastId", salesForecastId);
                        salesStatementNew.put("createdBy", userLogin.getString("userLoginId"));
                        salesStatementNew.put("modifiedBy", userLogin.getString("userLoginId"));
                        delegator.create(salesStatementNew);
                        salesStatement = salesStatementNew;
                    }

                    if (salesStatement != null) {
                        Map<String, Object> productMap = (Map<String, Object>) partyAndProductsItem.getValue();
                        if (productMap != null) {
                            // create and update
                            for (Map.Entry<String, Object> prodEntry : productMap.entrySet()) {
                                String productId = (String) prodEntry.getKey();
                                Double quantityNewDbl = (Double) prodEntry.getValue();
                                BigDecimal quantityNew = BigDecimal.ZERO;

                                GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
                                if (product == null) continue;
                                String quantityUomId = product.getString("quantityUomId");

                                if (quantityNewDbl != null && quantityNewDbl != 0) {
                                    try {
                                        quantityNew = new BigDecimal(quantityNewDbl);
                                    } catch (Exception e) {
                                        Debug.logWarning(e, "Problems parsing quantity string: " + quantityNewDbl, module);
                                        quantityNew = new BigDecimal("-1");
                                    }
                                } else if (quantityNewDbl == 0) {
                                    quantityNew = null;
                                }

                                // get sales statement detail
                                GenericValue salesStatementDetail = EntityUtil.getFirst(delegator.findByAnd("SalesStatementDetail",
                                        UtilMisc.toMap("salesStatementId", salesStatement.getString("salesStatementId"), "productId", productId), null, false));
                                BigDecimal quantityOld = BigDecimal.ZERO;
                                if (quantityNew != null) {
                                    if (salesStatementDetail != null) {
                                        // update quantity
                                        quantityOld = salesStatementDetail.getBigDecimal("quantity");
                                        if(quantityOld !=null) {
                                            if (quantityNew.compareTo(quantityOld) != 0) {
                                                // calculate product price
                                                Map<String, Object> resultProdPrice = dispatcher.runSync("calculateProductPriceCustom",
                                                        UtilMisc.toMap("product", product, "userLogin", userLogin));
                                                BigDecimal productPrice = BigDecimal.ZERO;
                                                if (ServiceUtil.isSuccess(resultProdPrice)) {
                                                    productPrice = (BigDecimal) resultProdPrice.get("price");
                                                }
                                                BigDecimal amount = productPrice.multiply(quantityNew).setScale(AMOUNT_DECIMAL, RoundingMode.HALF_UP);
                                                salesStatementDetail.put("quantity", quantityNew);
                                                salesStatementDetail.put("amount", amount);
                                                tobeStored.add(salesStatementDetail);
                                            }
                                        }else{
                                            Map<String, Object> resultProdPrice = dispatcher.runSync("calculateProductPriceCustom",
                                                    UtilMisc.toMap("product", product, "userLogin", userLogin));
                                            BigDecimal productPrice = BigDecimal.ZERO;
                                            if (ServiceUtil.isSuccess(resultProdPrice)) {
                                                productPrice = (BigDecimal) resultProdPrice.get("price");
                                            }
                                            BigDecimal amount = productPrice.multiply(quantityNew).setScale(AMOUNT_DECIMAL, RoundingMode.HALF_UP);
                                            salesStatementDetail.put("quantity", quantityNew);
                                            salesStatementDetail.put("amount", amount);
                                            tobeStored.add(salesStatementDetail);
                                        }
                                    } else {
                                        // calculate product price
                                        Map<String, Object> resultProdPrice = dispatcher.runSync("calculateProductPriceCustom",
                                                UtilMisc.toMap("product", product, "userLogin", userLogin));
                                        BigDecimal productPrice = BigDecimal.ZERO;
                                        if (ServiceUtil.isSuccess(resultProdPrice)) {
                                            productPrice = (BigDecimal) resultProdPrice.get("price");
                                        }
                                        BigDecimal amount = productPrice.multiply(quantityNew).setScale(AMOUNT_DECIMAL, RoundingMode.HALF_UP);

                                        salesStatementDetail = delegator.makeValue("SalesStatementDetail");
                                        salesStatementDetail.put("salesStatementId", salesStatement.get("salesStatementId"));
                                        salesStatementDetail.put("productId", productId);
                                        salesStatementDetail.put("quantityUomId", quantityUomId);
                                        salesStatementDetail.put("quantity", quantityNew);
                                        salesStatementDetail.put("amount", amount);
                                        String salesStatementDetailSeqId = UtilFormatOut.formatPaddedNumber(nextSeqNum, 5);
                                        salesStatementDetail.put("salesStatementDetailSeqId", salesStatementDetailSeqId);
                                        nextSeqNum++;
                                        tobeStored.add(salesStatementDetail);
                                    }
                                } else if (quantityNew == null) {
                                    if (salesStatementDetail != null) {
                                        // update quantity
                                        //quantityOld = salesStatementDetail.getBigDecimal("quantity");
                                        //if (quantityNew.compareTo(quantityOld) != 0) {
                                            // calculate product price
                                            Map<String, Object> resultProdPrice = dispatcher.runSync("calculateProductPriceCustom",
                                                    UtilMisc.toMap("product", product, "userLogin", userLogin));
                                            BigDecimal productPrice = BigDecimal.ZERO;
                                            if (ServiceUtil.isSuccess(resultProdPrice)) {
                                                productPrice = (BigDecimal) resultProdPrice.get("price");
                                            }
                                            //BigDecimal amount = productPrice.multiply(quantityNew).setScale(AMOUNT_DECIMAL, RoundingMode.HALF_UP);
                                            BigDecimal amount = null;
                                            salesStatementDetail.put("quantity", quantityNew);
                                            salesStatementDetail.put("amount", amount);
                                            tobeStored.add(salesStatementDetail);
                                        //}
                                    } else {
                                        // calculate product price
                                        Map<String, Object> resultProdPrice = dispatcher.runSync("calculateProductPriceCustom",
                                                UtilMisc.toMap("product", product, "userLogin", userLogin));
                                        BigDecimal productPrice = BigDecimal.ZERO;
                                        if (ServiceUtil.isSuccess(resultProdPrice)) {
                                            productPrice = (BigDecimal) resultProdPrice.get("price");
                                        }
                                        //BigDecimal amount = productPrice.multiply(quantityNew).setScale(AMOUNT_DECIMAL, RoundingMode.HALF_UP);
                                        BigDecimal amount = null;

                                        salesStatementDetail = delegator.makeValue("SalesStatementDetail");
                                        salesStatementDetail.put("salesStatementId", salesStatement.get("salesStatementId"));
                                        salesStatementDetail.put("productId", productId);
                                        salesStatementDetail.put("quantityUomId", quantityUomId);
                                        salesStatementDetail.put("quantity", quantityNew);
                                        salesStatementDetail.put("amount", amount);
                                        String salesStatementDetailSeqId = UtilFormatOut.formatPaddedNumber(nextSeqNum, 5);
                                        salesStatementDetail.put("salesStatementDetailSeqId", salesStatementDetailSeqId);
                                        nextSeqNum++;
                                        tobeStored.add(salesStatementDetail);
                                    }
                                }

                            }
                        }
                    }
                }
                delegator.storeAll(tobeStored);

                if (UtilValidate.isNotEmpty(listProductIds)) {
                    // update sales forecast
                    if (isUpdateS4C) {
                        if (salesForecastMonth == null) {
                            // create sales forecast month
                            Map<String, Object> salesForecastMonthCtx = FastMap.newInstance();
                            salesForecastMonthCtx.put("salesForecastId", delegator.getNextSeqId("SalesForecast"));
                            salesForecastMonthCtx.put("parentSalesForecastId", salesForecast.get("salesForecastId"));
                            salesForecastMonthCtx.put("organizationPartyId", salesForecast.get("organizationPartyId"));
                            salesForecastMonthCtx.put("customTimePeriodId", customTimePeriodId);
                            salesForecastMonthCtx.put("currencyUomId", salesForecast.get("currencyUomId"));
                            salesForecastMonthCtx.put("userLogin", userLogin);

                            try {
                                Map<String, Object> resultS4CNew = dispatcher.runSync("createSalesForecastCustom", salesForecastMonthCtx);
                                if (ServiceUtil.isError(resultS4CNew)) {
                                    Debug.logError(ServiceUtil.getErrorMessage(resultS4CNew), module);
                                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
                                    try {
                                        TransactionUtil.rollback(beganTx, "Failure in processing create or update callback", null);
                                    } catch (Exception e1) {
                                        Debug.logError(e1, module);
                                    }
                                    return "error";
                                }
                                String s4cIdNew = (String) resultS4CNew.get("salesForecastId");
                                salesForecastMonth = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", s4cIdNew), false);
                            } catch (GenericServiceException e) {
                                Debug.logError(e, module);
                                request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "BSErrorWhenProcessing", locale));
                                try {
                                    TransactionUtil.rollback(beganTx, "Failure in processing create or update callback", null);
                                } catch (Exception e1) {
                                    Debug.logError(e1, module);
                                }
                                return "error";
                            }
                        }

                        if (salesForecastMonth != null) {
                            List<String> salesStatementIdsAfter = EntityUtil.getFieldListFromEntityList(
                                    delegator.findList("SalesStatement", EntityCondition.makeCondition(UtilMisc.toMap("organizationPartyId", parentInternalPartyId, "salesStatementTypeId", salesStatementTypeId, "parentSalesStatementId", parentSalesStatementId)), null, null, null, false), "salesStatementId", false);
                            if (UtilValidate.isNotEmpty(salesStatementIdsAfter)) {
                                List<GenericValue> tobeStored2 = new LinkedList<GenericValue>();
                                for (String productId : listProductIds) {
                                    // get list sales statement detail by product id
                                    List<GenericValue> salesStatementDetails = delegator.findList("SalesStatementDetail",
                                            EntityCondition.makeCondition(EntityCondition.makeCondition("salesStatementId", EntityOperator.IN, salesStatementIdsAfter),
                                                    EntityOperator.AND, EntityCondition.makeCondition("productId", productId)), null, null, null, false);
                                    BigDecimal quantitySum = BigDecimal.ZERO;
                                    if (UtilValidate.isNotEmpty(salesStatementDetails)) {
                                        for (GenericValue salesStatementDetail : salesStatementDetails) {
                                            if(salesStatementDetail.getBigDecimal("quantity")!= null) {
                                                quantitySum = quantitySum.add(salesStatementDetail.getBigDecimal("quantity"));
                                            }
                                        }
                                    }
                                    GenericValue salesForecastDetailMonth = EntityUtil.getFirst(delegator.findByAnd("SalesForecastDetail",
                                            UtilMisc.toMap("salesForecastId", salesForecastMonth.get("salesForecastId"), "productId", productId), null, false));
                                    if (salesForecastDetailMonth != null) {
                                        salesForecastDetailMonth.set("quantity", quantitySum);
                                    } else {
                                        salesForecastDetailMonth = delegator.makeValue("SalesForecastDetail");
                                        salesForecastDetailMonth.set("salesForecastId", salesForecastMonth.get("salesForecastId"));
                                        salesForecastDetailMonth.set("salesForecastDetailId", delegator.getNextSeqId("SalesForecastDetail"));
                                        salesForecastDetailMonth.set("productId", productId);
                                        salesForecastDetailMonth.set("quantity", quantitySum);
                                    }
                                    tobeStored2.add(salesForecastDetailMonth);
                                }
                                delegator.storeAll(tobeStored2);
                            }
                        }
                    }
                    // end update sales forecast
                }
            }


            //end import data to db
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
            //}
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

}
