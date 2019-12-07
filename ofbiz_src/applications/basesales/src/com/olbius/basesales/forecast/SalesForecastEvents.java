package com.olbius.basesales.forecast;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileUploadException;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.importExport.ImportExportExcel;
import com.olbius.basehr.importExport.ImportExportWorker;
import com.olbius.basehr.timekeeping.helper.TimekeepingHelper;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basesales.util.SalesUtil;

public class SalesForecastEvents {
	public static final String module = SalesForecastEvents.class.getName();
	public static final String resource = "BaseSalesUiLabels";
    public static final String resource_error = "BaseSalesErrorUiLabels";
	
	public static String createUpdateSalesForecastAdvance(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
        // Get the parameters as a MAP, remove the productId and quantity params.
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);

        /* Horizontal 
        String customTimePeriodId = null;
        */
    	String tabCurrent = null;
    	
        Map<String, Object> colAndRow = SalesUtil.getMultiFormRowCount(paramMap);
        @SuppressWarnings("unchecked")
		List<String> rowIds = (List<String>) colAndRow.get("rowIds");
        int colCount = (Integer) colAndRow.get("colCount");
        tabCurrent = (String) colAndRow.get("tabCurrent");
        String internalPartyId = null;
        String organizationPartyId = null;
        String currencyUomId = null;
        if (paramMap.containsKey("internalPartyId_" + tabCurrent)) {
        	internalPartyId = (String) paramMap.remove("internalPartyId_" + tabCurrent);
		}
        if (paramMap.containsKey("organizationPartyId_" + tabCurrent)) {
        	organizationPartyId = (String) paramMap.remove("organizationPartyId_" + tabCurrent);
		}
        if (paramMap.containsKey("currencyUomId_" + tabCurrent)) {
        	currencyUomId = (String) paramMap.remove("currencyUomId_" + tabCurrent);
		}
        String parentSalesForecastId = null;
        if (paramMap.containsKey("parentSalesForecastId_" + tabCurrent)) {
        	parentSalesForecastId = (String) paramMap.remove("parentSalesForecastId_" + tabCurrent);
		}
        
        // The number of multi form rows is retrieved
        int rowCount = rowIds.size();
        if (rowCount < 1) {
            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
        } else {
            for (int i = 0; i < rowCount; i++) {
            	/* Horizontal
            	if (paramMap.containsKey("customTimePeriodId_" + rowIds.get(i))) {
            		customTimePeriodId = (String) paramMap.remove("customTimePeriodId_" + rowIds.get(i));
        		}*/
            	
            	GenericValue salesForecast = null;
            	boolean isCheck = false;
            	for (int j = 0; j < colCount; j++) {
            		String customTimePeriodId = null;
            		String salesForecastId = null;
                	String salesForecastDetailId = null;
                	String productId = null;
                	String quantityOldStr = null;
                	String quantityNewStr = null;
                	BigDecimal quantityOld = BigDecimal.ZERO;
                	BigDecimal quantityNew = BigDecimal.ZERO;
                	
            		String thisSuffix = "_" + rowIds.get(i) + "_" + j;
            		/* ayoSalesForecastId_, ayoSalesForecastDetailId_, ayoProductId_ 
            		 * salesForecastId_, salesForecastDetailId_, productId_, quantity_, forecastInput_
            		 */
            		if (paramMap.containsKey("customTimePeriodId" + thisSuffix)) {
                		customTimePeriodId = (String) paramMap.remove("customTimePeriodId" + thisSuffix);
            		}
            		if (paramMap.containsKey("salesForecastId" + thisSuffix)) {
            			salesForecastId = (String) paramMap.remove("salesForecastId" + thisSuffix);
            		}
            		if (paramMap.containsKey("salesForecastDetailId" + thisSuffix)) {
            			salesForecastDetailId = (String) paramMap.remove("salesForecastDetailId" + thisSuffix);
            		}
            		if (paramMap.containsKey("ayoProductId" + thisSuffix)) {
            			productId = (String) paramMap.remove("ayoProductId" + thisSuffix);
            		}
            		if (UtilValidate.isEmpty(productId)) {
            			if (paramMap.containsKey("productId" + thisSuffix)) {
                			productId = (String) paramMap.remove("productId" + thisSuffix);
                		}
            		}
            		if (paramMap.containsKey("quantity" + thisSuffix)) {
            			quantityOldStr = (String) paramMap.remove("quantity" + thisSuffix);
            		}
            		if ((quantityOldStr == null) || (quantityOldStr.equals(""))) {
            			quantityOldStr = "-1";
            		}
            		if (paramMap.containsKey("forecastInput" + thisSuffix)) {
            			quantityNewStr = (String) paramMap.remove("forecastInput" + thisSuffix);
            		}
            		if ((quantityNewStr == null) || (quantityNewStr.equals(""))) {
            			quantityNewStr = "-1";
            		}
            		try {
            			quantityOld = new BigDecimal(quantityOldStr);
                    } catch (Exception e) {
                        Debug.logWarning(e, "Problems parsing quantity string: " + quantityOldStr, module);
                        quantityOld = new BigDecimal("-1");
                    }
            		try {
                        quantityNew = new BigDecimal(quantityNewStr);
                    } catch (Exception e) {
                        Debug.logWarning(e, "Problems parsing quantity string: " + quantityNewStr, module);
                        quantityNew = new BigDecimal("-1");
                    }
            		
            		if ((quantityNew.compareTo(quantityOld) != 0) && (quantityNew.compareTo(BigDecimal.ZERO) >= 0) && UtilValidate.isNotEmpty(productId)) {
            			if (UtilValidate.isNotEmpty(salesForecastId)) {
            				try {
								salesForecast = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId), false);
							} catch (GenericEntityException e1) {
								Debug.logError(e1, module);
							}
            			}
            			if (UtilValidate.isEmpty(salesForecast)) {
            				//create sales forecast, then create sales forecast detail
            				if (UtilValidate.isEmpty(currencyUomId)) 
            					currencyUomId = SalesUtil.getCurrentCurrencyUom(delegator);
            				if (UtilValidate.isNotEmpty(customTimePeriodId) && UtilValidate.isNotEmpty(organizationPartyId)) {
            					Map<String, Object> inputSalesForecast = UtilMisc.<String, Object>toMap(
            							"parentSalesForecastId", parentSalesForecastId, 
            							"customTimePeriodId", customTimePeriodId, "organizationPartyId", organizationPartyId, 
            							"currencyUomId", currencyUomId, "userLogin", userLogin);
                				try {
                					if (UtilValidate.isNotEmpty(internalPartyId)) {
                						GenericValue internalParty = delegator.findOne("Party", UtilMisc.toMap("partyId", internalPartyId), false);
                    					if (internalParty != null) {
                    						inputSalesForecast.put("internalPartyId", internalPartyId);
                    					} else {
                    						internalPartyId = null;
                    					}
                					}
                					if (!isCheck) {
                						isCheck = true;
                						// find sales forecast when first loop column
	                					List<GenericValue> listSalesForecast = delegator.findByAnd("SalesForecast", UtilMisc.toMap("internalPartyId", internalPartyId, "customTimePeriodId", customTimePeriodId, "organizationPartyId", organizationPartyId, "currencyUomId", currencyUomId), null, false);
	                					if (UtilValidate.isNotEmpty(listSalesForecast)) {
	                						salesForecast = listSalesForecast.get(0);
	                						salesForecastId = salesForecast.getString("salesForecastId");
	                					}
	                					if (salesForecast == null) {
	                						Map<String, Object> resultService = dispatcher.runSync("createSalesForecast", inputSalesForecast);
	                                        if (!ServiceUtil.isError(resultService)) {
	                                        	salesForecastId = (String) resultService.get("salesForecastId");
	                                        	salesForecast = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId), false);
	                                        } else {
	                                        	request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultService));
	                                        	return "error";
	                                        }
	                					}
                					}
                                } catch (GenericServiceException e) {
                                    Debug.logError(e, module);
                                    return "error";
                                } catch (GenericEntityException e1) {
                                	Debug.logError(e1, module);
                                    return "error";
								}
            				}
            			}
            			
            			// Create list sales forecast detail
            			if (salesForecast != null) {
                        	if (UtilValidate.isNotEmpty(salesForecastDetailId)) {
                				// update sales forecast detail with id = (salesForecastId, salesForecastDetailId)
                				Map<String, Object> inputSalesForecastDetail = UtilMisc.<String, Object>toMap("userLogin", userLogin, 
                						"salesForecastId", salesForecast.get("salesForecastId"), "salesForecastDetailId", salesForecastDetailId, 
                						"productId", productId, "quantity", quantityNew);
                                try {
                                    dispatcher.runSync("updateSalesForecastDetail", inputSalesForecastDetail);
                                } catch (GenericServiceException e) {
                                    Debug.logError(e, module);
                                    return "error";
                                }
                			} else {
                				// create new a sales forecast detail
                				Map<String, Object> inputSalesForecastDetail = UtilMisc.<String, Object>toMap("userLogin", userLogin, 
                						"salesForecastId", salesForecast.get("salesForecastId"), "productId", productId, "quantity", quantityNew);
                                try {
                                    dispatcher.runSync("createSalesForecastDetail", inputSalesForecastDetail);
                                } catch (GenericServiceException e) {
                                    Debug.logError(e, module);
                                    return "error";
                                }
                			}
                        }
            		}
            	}
            }
        }

        // Determine where to send the browser
        return "success";
    }
	
	@SuppressWarnings("unchecked")
	public static String createUpdateSalesForecastAdvanceJson(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        
        String internalPartyId = request.getParameter("internalPartyId");
        String organizationPartyId = request.getParameter("organizationPartyId");
        String currencyUomId = request.getParameter("currencyUomId");
        String parentSalesForecastId = request.getParameter("parentSalesForecastId");
        String customTimePeriodListStr = request.getParameter("customTimePeriodList");
        String productListStr = request.getParameter("productList");
        
        JSONArray jsonCustomTimePeriodArray = new JSONArray();
		if(UtilValidate.isNotEmpty(customTimePeriodListStr)){
			jsonCustomTimePeriodArray = JSONArray.fromObject(customTimePeriodListStr);
		}
		List<String> customTimePeriodIds = FastList.newInstance();
		if (jsonCustomTimePeriodArray != null && jsonCustomTimePeriodArray.size() > 0) {
			for (int i = 0; i < jsonCustomTimePeriodArray.size(); i++) {
				customTimePeriodIds.add(jsonCustomTimePeriodArray.getString(i));
			}
		}
		
		JSONArray jsonProductArray = new JSONArray();
		if(UtilValidate.isNotEmpty(productListStr)){
			jsonProductArray = JSONArray.fromObject(productListStr);
		}
		List<Map<String, Object>> listProduct = FastList.newInstance();
		if (jsonProductArray != null && jsonProductArray.size() > 0) {
			for (int i = 0; i < jsonProductArray.size(); i++) {
				JSONObject prodItem = jsonProductArray.getJSONObject(i);
				Set<String> prodKeySet = prodItem.keySet();
				for (String key : prodKeySet) {
					String m_productId = prodItem.getString("productId");
					String m_quantity = null;
					String m_quantityOld = null;
					String m_salesForecastId = null;
					String m_salesForecastDetailId = null;
					
					if (customTimePeriodIds.contains(key)) {
						m_quantity = prodItem.getString(key);
						if (prodItem.containsKey(key + "_old")) m_quantityOld = prodItem.getString(key + "_old");
						if (prodItem.containsKey(key + "_sf")) m_salesForecastId = prodItem.getString(key + "_sf");
						if (prodItem.containsKey(key + "_sfi")) m_salesForecastDetailId = prodItem.getString(key + "_sfi");
						Map<String, Object> itemMap = FastMap.newInstance();
						itemMap.put("productId", m_productId);
						itemMap.put("quantity", m_quantity);
						itemMap.put("quantityOld", m_quantityOld);
						itemMap.put("customTimePeriodId", key);
						itemMap.put("salesForecastId", m_salesForecastId);
						itemMap.put("salesForecastDetailId", m_salesForecastDetailId);
						listProduct.add(itemMap);
					}
				}
			}
		}
        
		if (UtilValidate.isNotEmpty(listProduct)) {
			for (Map<String, Object> prodItem : listProduct) {
				String customTimePeriodId = (String) prodItem.get("customTimePeriodId");
        		String salesForecastId = (String) prodItem.get("salesForecastId");
            	String salesForecastDetailId = (String) prodItem.get("salesForecastDetailId");
            	String productId = (String) prodItem.get("productId");
            	String quantityOldStr = (String) prodItem.get("quantityOld");
            	String quantityNewStr = (String) prodItem.get("quantity");
            	BigDecimal quantityOld = BigDecimal.ZERO;
            	BigDecimal quantityNew = BigDecimal.ZERO;
            	
            	if (quantityOldStr != null) {
            		try {
            			quantityOld = new BigDecimal(quantityOldStr);
                    } catch (Exception e) {
                        Debug.logWarning(e, "Problems parsing quantity string: " + quantityOldStr, module);
                        quantityOld = new BigDecimal("-1");
                    }
            	}
            	if (quantityNewStr != null) {
            		try {
                        quantityNew = new BigDecimal(quantityNewStr);
                    } catch (Exception e) {
                        Debug.logWarning(e, "Problems parsing quantity string: " + quantityNewStr, module);
                        quantityNew = new BigDecimal("-1");
                    }
            	}
        		GenericValue salesForecast = null;
        		boolean isCheck = false;
            	if ((quantityNew.compareTo(quantityOld) != 0) && (quantityNew.compareTo(BigDecimal.ZERO) >= 0) && UtilValidate.isNotEmpty(productId)) {
        			if (UtilValidate.isNotEmpty(salesForecastId)) {
        				try {
							salesForecast = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId), false);
						} catch (GenericEntityException e1) {
							Debug.logError(e1, module);
						}
        			}
        			if (UtilValidate.isEmpty(salesForecast)) {
        				//create sales forecast, then create sales forecast detail
        				if (UtilValidate.isEmpty(currencyUomId)) 
        					currencyUomId = SalesUtil.getCurrentCurrencyUom(delegator);
        				if (UtilValidate.isNotEmpty(customTimePeriodId) && UtilValidate.isNotEmpty(organizationPartyId)) {
        					Map<String, Object> inputSalesForecast = UtilMisc.<String, Object>toMap(
        							"parentSalesForecastId", parentSalesForecastId, 
        							"customTimePeriodId", customTimePeriodId, "organizationPartyId", organizationPartyId, 
        							"currencyUomId", currencyUomId, "userLogin", userLogin);
            				try {
            					if (UtilValidate.isNotEmpty(internalPartyId)) {
            						GenericValue internalParty = delegator.findOne("Party", UtilMisc.toMap("partyId", internalPartyId), false);
                					if (internalParty != null) {
                						inputSalesForecast.put("internalPartyId", internalPartyId);
                					} else {
                						internalPartyId = null;
                					}
            					}
            					if (!isCheck) {
            						isCheck = true;
            						// find sales forecast when first loop column
                					List<GenericValue> listSalesForecast = delegator.findByAnd("SalesForecast", UtilMisc.toMap("internalPartyId", internalPartyId, "customTimePeriodId", customTimePeriodId, "organizationPartyId", organizationPartyId, "currencyUomId", currencyUomId), null, false);
                					if (UtilValidate.isNotEmpty(listSalesForecast)) {
                						salesForecast = listSalesForecast.get(0);
                						salesForecastId = salesForecast.getString("salesForecastId");
                					}
                					if (salesForecast == null) {
                						Map<String, Object> resultService = dispatcher.runSync("createSalesForecast", inputSalesForecast);
                                        if (!ServiceUtil.isError(resultService)) {
                                        	salesForecastId = (String) resultService.get("salesForecastId");
                                        	salesForecast = delegator.findOne("SalesForecast", UtilMisc.toMap("salesForecastId", salesForecastId), false);
                                        } else {
                                        	request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultService));
                                        	return "error";
                                        }
                					}
            					}
                            } catch (GenericServiceException e) {
                                Debug.logError(e, module);
                                return "error";
                            } catch (GenericEntityException e1) {
                            	Debug.logError(e1, module);
                                return "error";
							}
        				}
        			}
        			
        			// Create list sales forecast detail
        			if (salesForecast != null) {
                    	if (UtilValidate.isNotEmpty(salesForecastDetailId)) {
            				// update sales forecast detail with id = (salesForecastId, salesForecastDetailId)
            				Map<String, Object> inputSalesForecastDetail = UtilMisc.<String, Object>toMap("userLogin", userLogin, 
            						"salesForecastId", salesForecast.get("salesForecastId"), "salesForecastDetailId", salesForecastDetailId, 
            						"productId", productId, "quantity", quantityNew);
                            try {
                                dispatcher.runSync("updateSalesForecastDetail", inputSalesForecastDetail);
                            } catch (GenericServiceException e) {
                                Debug.logError(e, module);
                                return "error";
                            }
            			} else {
            				// create new a sales forecast detail
            				Map<String, Object> inputSalesForecastDetail = UtilMisc.<String, Object>toMap("userLogin", userLogin, 
            						"salesForecastId", salesForecast.get("salesForecastId"), "productId", productId, "quantity", quantityNew);
                            try {
                                dispatcher.runSync("createSalesForecastDetail", inputSalesForecastDetail);
                            } catch (GenericServiceException e) {
                                Debug.logError(e, module);
                                return "error";
                            }
            			}
                    }
            	}
			}
		}

        // Determine where to send the browser
        return "success";
    }
	@SuppressWarnings("unchecked")
	public static String createSaleForecastDetail(HttpServletRequest request, HttpServletResponse response) {
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Map<String, Object> successReturn = ServiceUtil.returnSuccess();
		Locale locale = UtilHttp.getLocale(request);
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		TimeZone timeZone = UtilHttp.getTimeZone(request);
		List<EntityCondition> conds = FastList.newInstance();
		try {
			Map<String, Object> parametersMap = CommonUtil.getParameterMapWithFileUploaded(request);
			String sheetIndexStr = (String) parametersMap.get("sheetIndex");
			String startLineStr = (String) parametersMap.get("startLine");
			String columnMapJson = (String) parametersMap.get("columnMap");

			String internalPartyId = (String) parametersMap.get("internalPartyId");
			String organizationPartyId = (String) parametersMap.get("organizationPartyId");
			String currencyUomId = (String) parametersMap.get("currencyUomId");
			String parentSalesForecastId = (String) parametersMap.get("parentSalesForecastId");
			String customTimePeriodList = (String) parametersMap.get("customTimePeriodList");

			JSONArray jsonCustomTimePeriodArray = new JSONArray();
			if (UtilValidate.isNotEmpty(customTimePeriodList)) {
				jsonCustomTimePeriodArray = JSONArray.fromObject(customTimePeriodList);
			}
			List<String> customTimePeriodIds = FastList.newInstance();
			if (jsonCustomTimePeriodArray != null && jsonCustomTimePeriodArray.size() > 0) {
				for (int i = 0; i < jsonCustomTimePeriodArray.size(); i++) {
					JSONObject jObj = JSONObject.fromObject(jsonCustomTimePeriodArray.get(i));
					customTimePeriodIds.add(jObj.getString("customTimePeriodId"));
				}
			}

			if (UtilValidate.isNotEmpty(customTimePeriodIds) && parentSalesForecastId != null) {
				//doing

				//kiem tra SalesForcast, chua ton tai thi them moi.
				List<String> customTimePeriodIdNotExist = FastList.newInstance();
				//conds.add(EntityCondition.makeCondition("parentSalesForecastId", EntityOperator.NOT_EQUAL, null));
				conds.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, customTimePeriodIds));
				List<GenericValue> salesForecasts = delegator.findList("SalesForecast",
						EntityCondition.makeCondition(conds), null, null, null, false);
				List<String> idExisteds = EntityUtil.getFieldListFromEntityList(salesForecasts, "customTimePeriodId", true);
				for (String customTimePeriodId: customTimePeriodIds) {
					if (!idExisteds.contains(customTimePeriodId)){
						customTimePeriodIdNotExist.add(customTimePeriodId);
					}
				}
				for (String id : customTimePeriodIdNotExist) {
					if (UtilValidate.isEmpty(currencyUomId))
						currencyUomId = SalesUtil.getCurrentCurrencyUom(delegator);
					if (UtilValidate.isNotEmpty(id) && UtilValidate.isNotEmpty(organizationPartyId)) {
						Map<String, Object> inputSalesForecast = UtilMisc.<String, Object>toMap(
								"parentSalesForecastId", parentSalesForecastId,
								"customTimePeriodId", id, "organizationPartyId", organizationPartyId,
								"currencyUomId", currencyUomId, "userLogin", userLogin);
						if (UtilValidate.isNotEmpty(internalPartyId)) {
							GenericValue internalParty = delegator.findOne("Party", UtilMisc.toMap("partyId", internalPartyId), false);
							if (internalParty != null) {
								inputSalesForecast.put("internalPartyId", internalPartyId);
							} else {
								internalPartyId = null;
							}
						}
						Map<String, Object> resultService = dispatcher.runSync("createSalesForecast", inputSalesForecast);
						if (ServiceUtil.isError(resultService)) {
							request.setAttribute("_ERROR_MESSAGE_", ServiceUtil.getErrorMessage(resultService));
							return "error";
						}
					}
				}

				//lay lai tat ca salesForcast
				conds.clear();
				conds.add(EntityCondition.makeCondition("parentSalesForecastId", EntityOperator.NOT_EQUAL, null));
				conds.add(EntityCondition.makeCondition("customTimePeriodId", EntityOperator.IN, customTimePeriodIds));
				salesForecasts = delegator.findList("SalesForecast",
						EntityCondition.makeCondition(conds), null, null, null, false);
				List<String> salesForecastIds = FastList.newInstance();
				salesForecastIds = EntityUtil.getFieldListFromEntityList(salesForecasts, "salesForecastId", true);

				//Boc du lieu.
				int startLine = 0;
				try {
					startLine = Integer.parseInt(startLineStr);
				} catch (NumberFormatException e) {
					startLine = 0;
				}
				TransactionUtil.begin();

				List<Map<String, Object>> listFileUploaded = (List<Map<String, Object>>) parametersMap.get("listFileUploaded");
				if (UtilValidate.isEmpty(listFileUploaded)) {
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "CannotNotFindExcelFileToImport", locale));
					TransactionUtil.rollback();
					return "error";
				}
				Map<String, Object> fileUploadedMap = listFileUploaded.get(0);
				String _uploadedFile_contentType = (String) fileUploadedMap.get("_uploadedFile_contentType");
				ByteBuffer uploadedFile = (ByteBuffer) fileUploadedMap.get("uploadedFile");
				if (!ImportExportExcel.isExcelFile(_uploadedFile_contentType)) {
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "OnlyAccpetXLSXFile", locale));
					TransactionUtil.rollback();
					return "error";
				}
				TransactionUtil.commit();
				Map<Integer, Object> columnExcelMap = ImportExportWorker.readColumnMapFromJson(columnMapJson);
				List<Map<String, Object>> salesFCDetails = SalesForecastWorker.getSalesFCSheetDetail(dispatcher, delegator, userLogin, locale,
						uploadedFile, columnExcelMap, Integer.parseInt(sheetIndexStr), startLine);
				if (UtilValidate.isEmpty(salesFCDetails)) {
					request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
					request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseHRUiLabels", "NoDataFromExcelImported", locale));
					return "error";
				}
                //get products
                List<String> productCodes = FastList.newInstance();
                for (Map<String,Object> aSalesFcDetail : salesFCDetails) {
                    productCodes.add((String) aSalesFcDetail.get("productCode"));
                }
                conds.clear();
                conds.add(EntityCondition.makeCondition("productCode", EntityOperator.IN, productCodes));
                List<GenericValue> products = delegator.findList("Product", EntityCondition.makeCondition(conds),UtilMisc.toSet("productId","productCode"),null,null,false);
                Map<String, Object> productMap = FastMap.newInstance();
                for (GenericValue aProduct: products) {
                    productMap.put(aProduct.getString("productCode"), aProduct.getString("productId"));
                }
				//import data to db  salesFCDetails from file, salesForecastDetails from db
                String forcastId = "";
                String parentForcastId = "";
                String timePeriodId = "";
                String productId = "";
                BigDecimal quantity = BigDecimal.ZERO;
                List<GenericValue> salesForecastDetails = FastList.newInstance();
                GenericValue aSalesForecastDetail = null;

                for (Map<String,Object> aSalesFcDetail : salesFCDetails) {
                	try{
						productId = (String)productMap.get(aSalesFcDetail.get("productCode"));
					}catch (Exception e){
						request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
						request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage(resource_error, "BSErrorNotAutoMapData", locale));
						TransactionUtil.rollback();
						return "error";
					}
                    for (GenericValue aSalesForecast: salesForecasts) {
                        parentForcastId = (String)aSalesForecast.get("parentSalesForecastId");
                        forcastId = aSalesForecast.getString("salesForecastId");
                        timePeriodId = aSalesForecast.getString("customTimePeriodId");
						try {
							Double quantityDouble = (Double)aSalesFcDetail.get(timePeriodId);
							quantity = new BigDecimal(quantityDouble);
						} catch (Exception e) {
							request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
							request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage(resource_error, "BSErrorInputType", locale));
							TransactionUtil.rollback();
							return "error";
						}

                        conds.clear();
                        conds.add(EntityCondition.makeCondition("salesForecastId", forcastId));
                        conds.add(EntityCondition.makeCondition("productId", productId));
                        salesForecastDetails = delegator.findList("SalesForecastDetail", EntityCondition.makeCondition(conds),
                                null, null, null, false);
                        if (UtilValidate.isNotEmpty(salesForecastDetails)) {
                            aSalesForecastDetail = salesForecastDetails.get(0);
                            if (!quantity.equals(aSalesForecastDetail.getBigDecimal("quantity"))){
                                // update sales forecast detail with id = (salesForecastId, salesForecastDetailId)
                                Map<String, Object> inputSalesForecastDetail = UtilMisc.<String, Object>toMap("userLogin", userLogin,
                                        "salesForecastId", forcastId, "salesForecastDetailId", aSalesForecastDetail.getString("salesForecastDetailId"),
                                        "productId", productId, "quantity", quantity);
                                try {
                                    dispatcher.runSync("updateSalesForecastDetail", inputSalesForecastDetail);
                                } catch (GenericServiceException e) {
                                    Debug.logError(e, module);
                                    return "error";
                                }
                            }
                        } else {
                            // create new a sales forecast detail
                            Map<String, Object> inputSalesForecastDetail = UtilMisc.<String, Object>toMap("userLogin", userLogin,
                                    "salesForecastId", forcastId, "productId", productId, "quantity", quantity);
                            try {
                                dispatcher.runSync("createSalesForecastDetail", inputSalesForecastDetail);
                            } catch (GenericServiceException e) {
                                Debug.logError(e, module);
                                return "error";
                            }
                        }

                    }
                }
				//end import data to db
				request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
			}
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
			request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
		}
		return "success";
	}
}
