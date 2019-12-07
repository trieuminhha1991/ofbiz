package com.olbius.basesales.util;

import static org.ofbiz.base.util.UtilGenerics.checkList;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericPK;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.webapp.event.EventHandlerException;
import org.ofbiz.webapp.event.FileUploadProgressListener;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.common.util.EntityMiscUtil;

import javax.servlet.http.HttpServletRequest;

public class SalesUtil {
	public static String module = SalesUtil.class.getName();
	public static String RESOURCE_PROPERTIES = "basesales.properties";
	public static final String MULTI_ROW_DELIMITER = "_";
	public static Map<String, Object> mapPropertiesValue = new HashMap<String, Object>();
	public static Map<String, Object> mapSystemConfigValue = new HashMap<String, Object>();
	
	public static List<String> processKeyProperty(String key) {
		List<String> returnValue = new ArrayList<String>();
		if (UtilValidate.isEmpty(key)) return returnValue;
		String[] listKey = key.split(";");
		if (listKey != null) {
			returnValue = Arrays.asList(listKey);
		}
		return returnValue;
	}
	public static List<String> processKeyProperty(String key, String separatorChar) {
		List<String> returnValue = new ArrayList<String>();
		if (UtilValidate.isEmpty(key)) return returnValue;
		String[] listKey = key.split(separatorChar);
		if (listKey != null) {
			returnValue = Arrays.asList(listKey);
		}
		return returnValue;
	}
	public static String getPropertyValue(String key) {
		String returnValue = null;
		if (mapPropertiesValue.containsKey(key)) 
			returnValue = (String) mapPropertiesValue.get(key);
		if (UtilValidate.isEmpty(returnValue)) {
			returnValue = UtilProperties.getPropertyValue(RESOURCE_PROPERTIES, key);
			mapPropertiesValue.put(key, returnValue);
		}
		return returnValue;
	}
	public static String getPropertyValue(Delegator delegator, String key) {
		String returnValue = null;
		if (mapPropertiesValue.containsKey(key)) 
			returnValue = (String) mapPropertiesValue.get(key);
		if (UtilValidate.isEmpty(returnValue)) {
			returnValue = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, key, delegator);
			mapPropertiesValue.put(key, returnValue);
		}
		return returnValue;
	}
	
	public static boolean propertyValueEqualsIgnoreCase(String key, String compareString){
		return UtilProperties.propertyValueEqualsIgnoreCase(RESOURCE_PROPERTIES, key, compareString);
	}
	public static List<String> getPropertyProcessedMultiKey(String key){
		return processKeyProperty(getPropertyValue(key));
	}
	public static List<String> getPropertyProcessedMultiKey(Delegator delegator, String key){
		return processKeyProperty(getPropertyValue(delegator, key));
	}
	
	public static String getSystemConfigValue(Delegator delegator, String key) {
		String returnValue = null;
		if (mapSystemConfigValue.containsKey(key)) {
			returnValue = (String) mapSystemConfigValue.get(key);
		} else {
			try {
				GenericValue sysConf = delegator.findOne("SystemConfig", UtilMisc.toMap("systemConfigId", key), true);
				if (sysConf != null) returnValue = sysConf.getString("systemValue");
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error get system config: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
		}
		return returnValue;
	}
	
	public static String getCurrentCurrencyUom(Delegator delegator) {
		if (delegator == null) return null;
		return EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
	}
	public static String getCurrentCountryGeo(Delegator delegator) {
		if (delegator == null) return null;
		return EntityUtilProperties.getPropertyValue("general.properties", "country.geo.id.default", "VNM", delegator);
	}
	
	public static String getCurrentOrganization(Delegator delegator) {
		if (delegator == null) return null;
		return EntityUtilProperties.getPropertyValue("general.properties", "ORGANIZATION_PARTY", "", delegator);
		//return MultiOrganizationUtil.getCurrentOrganization(delegator);
	}
	
	public static String getCurrentOrganization(Delegator delegator, String userLoginId) {
		if (delegator == null) return null;
		//return getCurrentOrganization(delegator);
		return MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
	}
	public static String getCurrentOrganization(Delegator delegator, GenericValue userLogin) {
		if (delegator == null || userLogin == null) return null;
		//return getCurrentOrganization(delegator);
		return MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
	}
	
	/**
	 * This method moved to com.olbius.common.util.EntityMiscUtil
	 * @param listIterator
	 * @param parameters
	 * @param successResult
	 * @return
	 */
	public static String getParameter(Map<String, String[]> parameters, String key) {
		String resultValue = null;
		if (UtilValidate.isEmpty(key) || UtilValidate.isEmpty(parameters)) return resultValue;
		if (parameters.containsKey(key) && parameters.get(key).length > 0) {
			resultValue = parameters.get(key)[0]; 
		}
		return resultValue;
	}
	
	public static List<String> getCurrentCustomTimePeriodTypeSales(Delegator delegator) {
		if (delegator == null) return null;
		return SalesUtil.getPropertyProcessedMultiKey(delegator, "period.type.id.sales");
	}
	
	public static List<GenericValue> getListSalesMethodChannelEnum(Delegator delegator) throws GenericEntityException {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		if (delegator == null) return returnValue;
		String salesMethodChannelEnumTypeId = EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "sales.method.channel.enum.type.id", delegator);
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		returnValue = delegator.findList("Enumeration", EntityCondition.makeCondition("enumTypeId", salesMethodChannelEnumTypeId),null, null, opts, true);
		if (returnValue == null)
			returnValue = new ArrayList<GenericValue>();
		return returnValue;
	}
	
	public static List<GenericValue> getListSalesPeriodType(Delegator delegator) throws GenericEntityException {
		List<GenericValue> returnValue = new ArrayList<GenericValue>();
		if (delegator == null) return returnValue;
		List<String> periodTypeIds = SalesUtil.getPropertyProcessedMultiKey(delegator, "period.type.id.sales");
		returnValue = delegator.findList("PeriodType", EntityCondition.makeCondition("periodTypeId", EntityOperator.IN, periodTypeIds), null, null, null, false);
		return returnValue;
	}
	
	public static String getProductCatalogDefault(Delegator delegator) {
		if (delegator == null) return null;
		return EntityUtilProperties.getPropertyValue(RESOURCE_PROPERTIES, "product.catalog.default", delegator);
	}
	
	/**
	 * This method moved to com.olbius.common.util.EntityMiscUtil.processIteratorToList
	 * @param listIterator
	 * @param parameters
	 * @param successResult
	 * @return
	 */
	public static List<GenericValue> processIterator(EntityListIterator listIterator, Map<String, String[]> parameters, Map<String, Object> successResult) {
    	List<GenericValue> returnValue = null;
    	String viewIndexStr = (String) parameters.get("pagenum")[0];
    	String viewSizeStr = (String) parameters.get("pagesize")[0];
    	int viewIndex = viewIndexStr == null ? 0 : new Integer(viewIndexStr);
    	int viewSize = viewSizeStr == null ? 0 : new Integer(viewSizeStr);
    	try {
    		if (UtilValidate.isNotEmpty(listIterator)) {
    			if (viewSize != 0) {
    				if (viewIndex == 0) {
    					returnValue = listIterator.getPartialList(0, viewSize);
    				} else {
    					returnValue = listIterator.getPartialList(viewIndex * viewSize + 1, viewSize);
    				}
    			} else {
    				returnValue = listIterator.getCompleteList();
    			}
    		}
    	} catch (Exception e) {
			String errMsg = "Fatal error calling processIterator method: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	/*finally {
			if (listIterator != null) {
				try {
					listIterator.close();
				} catch (GenericEntityException e) {
					Debug.logError(e, "Error when close iterator", module);
				}
			}
		}*/
		
    	if (listIterator != null) {
			try {
				//int totalRows = listIterator.getResultsSizeAfterPartialList();
				int totalRows = listIterator.getResultsSizeAfterPartialList();
				successResult.put("TotalRows", String.valueOf(totalRows));
			} catch (GenericEntityException e) {
				Debug.logError(e, "Error when get size of list iterator", module);
			} finally {
				try {
					listIterator.close();
				} catch (GenericEntityException e) {
					Debug.logError(e, "Error when close iterator", module);
				}
			}
    	}
		
    	return returnValue;
    }
	
	/*public static List<String> getAllCategoryTree(Delegator delegator, String parentCategoryId) {
		return getAllCategoryTree(delegator, parentCategoryId, true);
	}
	public static List<String> getAllCategoryTree(Delegator delegator, String parentCategoryId, boolean filterByDate) {
		List<String> listCategories = FastList.newInstance();
		if (delegator == null) return listCategories;
		try {
			List<EntityCondition> listConds = FastList.newInstance();
			listConds.add(EntityCondition.makeCondition("parentProductCategoryId", parentCategoryId));
			if (filterByDate) listConds.add(EntityUtil.getFilterByDateExpr());
			List<GenericValue> preCatChilds = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(listConds), null, UtilMisc.toList("sequenceNum", "productCategoryId"), null, false);
			List<GenericValue> catChilds = EntityUtil.getRelated("CurrentProductCategory", null, preCatChilds, false);
			if (catChilds != null) {
				for (GenericValue catChild : catChilds) {
					listCategories.addAll(getAllCategoryTree(delegator, catChild.getString("productCategoryId"), filterByDate));
				}
				listCategories.add(parentCategoryId);
			}
		} catch (Exception e) {
			String errMsg = "Fatal error calling getAllCategoryTree service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		return listCategories;
	}*/
	
	/**
	 * Get product variant by product virtual
	 * @param virtualProduct
	 * @return list product variant.
	 * productId is product virtual id.
	 * productIdTo is product variant id (this need get)
	 * @throws GenericEntityException
	 */
	public static List<GenericValue> getVirtualVariantAssocs(GenericValue virtualProduct) throws GenericEntityException {
        if (virtualProduct != null && "Y".equals(virtualProduct.getString("isVirtual"))) {
            List<GenericValue> productAssocs = EntityUtil.filterByDate(virtualProduct.getRelated("MainProductAssoc", UtilMisc.toMap("productAssocTypeId", "PRODUCT_VARIANT"), null, true));
            return productAssocs;
        }
        return null;
    }
	
	/**
	 * Get product virtual by product variant
	 * @param variantProduct
	 * @return list product virtual.
	 * productId is product virtual id (this need get).
	 * productIdTo is product variant id
	 * @throws GenericEntityException
	 */
	public static List<GenericValue> getVariantVirtualAssocs(GenericValue variantProduct) throws GenericEntityException {
		if (variantProduct != null && "Y".equals(variantProduct.getString("isVariant"))) {
			List<GenericValue> productAssocs = EntityUtil.filterByDate(variantProduct.getRelated("AssocProductAssoc", UtilMisc.toMap("productAssocTypeId", "PRODUCT_VARIANT"), null, true));
			return productAssocs;
		}
		return null;
	}
	
	public static Set<String> getCurrentBusinessMenus(Delegator delegator, GenericValue userLogin){
		Set<String> businessMenus = new HashSet<String>();
		if (UtilValidate.isEmpty(userLogin.getString("partyId"))) return businessMenus;
		try {
			List<GenericValue> roleTypeList = delegator.findByAnd("PartyRole", UtilMisc.toMap("partyId", userLogin.getString("partyId")), null, true);
			if (roleTypeList != null) {
				for (GenericValue roleType : roleTypeList) {
					String roleTypeId = roleType.getString("roleTypeId");
					GenericValue roleTypeAttr = delegator.findOne("RoleTypeAttr" ,UtilMisc.toMap("roleTypeId", roleTypeId, "attrName", "BusinessMenu"), true);
					if (roleTypeAttr != null){
						businessMenus.add(roleTypeAttr.getString("attrValue"));
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.log(e, module);
		}
		return businessMenus;
	}
	
	public static boolean isColumnsOfTable(Delegator delegator, List<String> listSortFields, String tableName){
		boolean isColumns = true;
		if (UtilValidate.isEmpty(listSortFields)) return isColumns;
		ModelEntity tableModel = delegator.getModelEntity(tableName);
		for (String item : listSortFields) {
			item = item.replace("-", "");
			if (!tableModel.isField(item)) {
				isColumns = false;
			}
		}
		
		return isColumns;
	}
	
	public static boolean isColumnOfTable(Delegator delegator, String columnName, String tableName){
		boolean isColumns = true;
		if (UtilValidate.isEmpty(columnName)) return isColumns;
		ModelEntity tableModel = delegator.getModelEntity(tableName);
		columnName = columnName.replace("-", "");
		if (!tableModel.isField(columnName)) {
			isColumns = false;
		}
		
		return isColumns;
	}
	public static boolean isColumnOfTable(String columnName, ModelEntity tableModel){
		boolean isColumns = true;
		if (UtilValidate.isEmpty(columnName) || tableModel == null) return isColumns;
		columnName = columnName.replace("-", "");
		if (!tableModel.isField(columnName)) {
			isColumns = false;
		}
		return isColumns;
	}
	
	/** Returns the number or rows submitted by a multi form.
     */
    public static Map<String, Object> getMultiFormRowCount(Map<String, ?> requestMap) {
        // The number of multi form rows is computed selecting the maximum index
    	Map<String, Object> mapResult = FastMap.newInstance();
    	List<String> rowIds = new ArrayList<String>();
    	String tabCurrent = null;
    	int colCount = 0;
    	int colValue = 0;
        for (String parameterName : requestMap.keySet()) {
        	String[] parameterNameSplit = parameterName.split(MULTI_ROW_DELIMITER);
        	if (parameterNameSplit.length == 2) {
        		if ("internalPartyId".equals(parameterNameSplit[0])) {
        			tabCurrent = parameterNameSplit[1];
        		}
        	} else if (parameterNameSplit.length == 4) {
        		String preId = parameterNameSplit[1] + "_" + parameterNameSplit[2];
            	String afterId = parameterNameSplit[3];
            	if (!rowIds.contains(preId)) {
            		rowIds.add(preId);
            	}
            	try {
            		colValue = Integer.parseInt(afterId);
                } catch (NumberFormatException e) {
                    Debug.logWarning("Invalid value for row index found: " + afterId, module);
                }
            	
            	if (colCount < colValue) {
            		colCount = colValue;
            	}
        	}
        }
        if (UtilValidate.isNotEmpty(colCount)) {
        	colCount++; // row indexes are zero based
        }
        mapResult.put("colCount", colCount);
        mapResult.put("rowIds", rowIds);
        mapResult.put("tabCurrent", tabCurrent);
        return mapResult;
    }
    
    /** Returns the number or rows submitted by a multi form. Seq in row
     */
    public static int getMultiFormRowCountSeq(Map<String, ?> requestMap, String seq_id, String delimiter) {
        // The number of multi form rows is computed selecting the maximum index
        int rowCount = 0;
        String maxRowIndex = "";
        int rowDelimiterLength = delimiter.length();
        for (String parameterName: requestMap.keySet()) {
            int rowDelimiterIndex = (parameterName != null? parameterName.indexOf(delimiter): -1);
            int rowDelimiterEndIndex = (parameterName != null? parameterName.indexOf(UtilHttp.MULTI_ROW_DELIMITER + seq_id): -1);
            if (rowDelimiterIndex > 0 && rowDelimiterEndIndex >= rowDelimiterIndex) {
                String thisRowIndex = parameterName.substring(rowDelimiterIndex + rowDelimiterLength, rowDelimiterEndIndex);
                if (thisRowIndex.indexOf("_") > -1) {
                    thisRowIndex = thisRowIndex.substring(0, thisRowIndex.indexOf("_"));
                }
                if (maxRowIndex.length() < thisRowIndex.length()) {
                    maxRowIndex = thisRowIndex;
                } else if (maxRowIndex.length() == thisRowIndex.length() && maxRowIndex.compareTo(thisRowIndex) < 0) {
                    maxRowIndex = thisRowIndex;
                }
            }
        }
        if (UtilValidate.isNotEmpty(maxRowIndex)) {
            try {
                rowCount = Integer.parseInt(maxRowIndex);
                rowCount++; // row indexes are zero based
            } catch (NumberFormatException e) {
                Debug.logWarning("Invalid value for row index found: " + maxRowIndex, module);
            }
        }
        return rowCount;
    }
    
    /**
     * This should be called to translate the error messages of the
     * <code>ShoppingCartHelper</code> to an appropriately formatted
     * <code>String</code> in the request object and indicate whether
     * the result was an error or not and whether the errors were
     * critical or not
     *
     * @param result    The result returned from the
     * <code>ShoppingCartHelper</code>
     * @param request The servlet request instance to set the error messages
     * in
     * @return one of NON_CRITICAL_ERROR, ERROR or NO_ERROR.
     */
    public static String processResult(Map<String, Object> result, HttpServletRequest request) {
        //Check for errors
        StringBuilder errMsg = new StringBuilder();
        if (result.containsKey(ModelService.ERROR_MESSAGE_LIST)) {
            List<String> errorMsgs = UtilGenerics.checkList(result.get(ModelService.ERROR_MESSAGE_LIST));
            Iterator<String> iterator = errorMsgs.iterator();
            errMsg.append("<ul>");
            while (iterator.hasNext()) {
                errMsg.append("<li>");
                errMsg.append(iterator.next());
                errMsg.append("</li>");
            }
            errMsg.append("</ul>");
        } else if (result.containsKey(ModelService.ERROR_MESSAGE)) {
            errMsg.append(result.get(ModelService.ERROR_MESSAGE));
            request.setAttribute("_ERROR_MESSAGE_", errMsg.toString());
        }

        //See whether there was an error
        if (errMsg.length() > 0) {
            request.setAttribute("_ERROR_MESSAGE_", errMsg.toString());
            if (result.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
                return "error";
            } else {
                return "error";
            }
        } else {
            return "success";
        }
    }
	
	/** Return map contains 5 list map
	 *  listMapConditions; 
		listMapConditionOnIn; 
		listMapConditionOutOf; 
		listAllConditionOnIn; 
		listAllConditionOutOf; 
	 * @param delegator
	 * @param listAllConditions
	 * @param tableName
	 * @return
	 */
	public static Map<String, Object> processSplitListAllCondition(Delegator delegator, List<EntityCondition> listAllConditions, String tableName){
		Map<String, Object> mapConditionPro = FastMap.newInstance();
		List<Map<String, Object>> listMapConditionOnIn = FastList.newInstance();
		List<Map<String, Object>> listMapConditionOutOf = FastList.newInstance();
		List<EntityCondition> listAllConditionOnIn = FastList.newInstance();
		List<EntityCondition> listAllConditionOutOf = FastList.newInstance();
		
		// process list all condition
		List<Map<String, Object>> listMapConditions = FastList.newInstance();
		// [(birthDate >= '2015-08-18 00:00:00.0' AND birthDate <= '2015-08-31 23:59:59.0')]
		if (listAllConditions != null) {
			ModelEntity tableModel = delegator.getModelEntity(tableName);
			for (EntityCondition condition : listAllConditions) {
				String cond = condition.toString();
				if(UtilValidate.isNotEmpty(cond)){
					String[] conditionSplit = cond.split(" ");
					Map<String, Object> condMap = FastMap.newInstance();
					String fieldName = (String) conditionSplit[0];
					String operator = (String) conditionSplit[1];
					String value = (String) conditionSplit[2].trim();
					
					if (conditionSplit.length > 4) {
						if (UtilValidate.isNotEmpty(conditionSplit[4].trim())) {
							if ("AND".equals(conditionSplit[4].trim())) {
								operator = "RANGE";
								String valueFrom = (String) conditionSplit[2].trim();
								String valueTo = (String) conditionSplit[7].trim();
								valueFrom = EntityMiscUtil.cleanValue(valueFrom);
								valueTo = EntityMiscUtil.cleanValue(valueTo);
								
								condMap.put("valueFrom", valueFrom);
								condMap.put("valueTo", valueTo);
							}
						}
					}
					
					fieldName = EntityMiscUtil.cleanFieldName(fieldName);
					value = EntityMiscUtil.cleanValue(value);
					
					condMap.put("fieldName", fieldName);
					condMap.put("operator",operator );
					condMap.put("value", value);
					listMapConditions.add(condMap);
					
					// check field in table
					if (tableModel != null) {
						if (UtilValidate.isNotEmpty(fieldName)) {
							if (SalesUtil.isColumnOfTable(fieldName, tableModel)) {
								listAllConditionOnIn.add(condition);
								listMapConditionOnIn.add(condMap);
							} else {
								listAllConditionOutOf.add(condition);
								listMapConditionOutOf.add(condMap);
							}
						} else {
							listAllConditionOutOf.add(condition);
							listMapConditionOutOf.add(condMap);
						}
					}
				}
			}
		}
		mapConditionPro.put("listMapConditions", listMapConditions);
		mapConditionPro.put("listMapConditionOnIn", listMapConditionOnIn);
		mapConditionPro.put("listMapConditionOutOf", listMapConditionOutOf);
		mapConditionPro.put("listAllConditionOnIn", listAllConditionOnIn);
		mapConditionPro.put("listAllConditionOutOf", listAllConditionOutOf);
    	return mapConditionPro;
	}
	
	public static List<Map<String, Object>> processSplitListAllCondition(List<EntityCondition> listAllConditions){
		// process list all condition
		List<Map<String, Object>> listMapConditions = FastList.newInstance();
		// [(birthDate >= '2015-08-18 00:00:00.0' AND birthDate <= '2015-08-31 23:59:59.0')]
		for (EntityCondition condition : listAllConditions) {
			String cond = condition.toString();
			if(UtilValidate.isNotEmpty(cond)){
				String[] conditionSplit = cond.split(" ");
				Map<String, Object> condMap = FastMap.newInstance();
				String fieldName = (String) conditionSplit[0];
				String operator = (String) conditionSplit[1];
				String value = (String) conditionSplit[2].trim();
				
				if (conditionSplit.length > 4) {
					if (UtilValidate.isNotEmpty(conditionSplit[4].trim())) {
						if ("AND".equals(conditionSplit[4].trim())) {
							operator = "RANGE";
							String valueFrom = (String) conditionSplit[2].trim();
							String valueTo = (String) conditionSplit[7].trim();
							valueFrom = EntityMiscUtil.cleanValue(valueFrom);
							valueTo = EntityMiscUtil.cleanValue(valueTo);
							
							condMap.put("valueFrom", valueFrom);
							condMap.put("valueTo", valueTo);
						}
					}
				}
				
				fieldName = EntityMiscUtil.cleanFieldName(fieldName);
				value = EntityMiscUtil.cleanValue(value);
				
				condMap.put("fieldName", fieldName);
				condMap.put("operator",operator );
				condMap.put("value", value);
				listMapConditions.add(condMap);
			}
		}
    	return listMapConditions;
	}
	
	public static GenericPK processStringGenericPK(Delegator delegator, String strGenericPK){
		GenericPK returnValue = null;
		
		String[] arrColumn = strGenericPK.split("]");
		for (int i = 0; i < arrColumn.length; i++) {
			String arrItem = arrColumn[i];
			arrItem = arrItem.replace("[", "");
			if (i == 0) {
				String[] keyValue = arrItem.split(":");
				String key = keyValue[0];
				String value = keyValue[1];
				if ("GenericEntity".equals(key)) {
					returnValue = delegator.makePK(value);
					continue;
				} else break;
			}
			if (arrItem.contains("(")) {
				String strTmpKeyValue = arrItem.substring(0, arrItem.indexOf("("));
				//String strTmpType = arrItem.substring(arrItem.indexOf("(") + 1, arrItem.length() - 1);
				String[] keyValue = strTmpKeyValue.split(",");
				returnValue.put(keyValue[0], keyValue[1]);
			} else {
				String[] keyValue = arrItem.split(",");
				returnValue.put(keyValue[0], keyValue[1]);
			}
		}
		return returnValue;
	}
	
	public static Map<String, Object> getParameterMapFileUpload(HttpServletRequest request) throws EventHandlerException {
		// make sure we have a valid reference to the Service Engine
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        if (dispatcher == null) {
            throw new EventHandlerException("The local service dispatcher is null");
        }
        DispatchContext dctx = dispatcher.getDispatchContext();
        if (dctx == null) {
            throw new EventHandlerException("Dispatch context cannot be found");
        }
        
        // get the http upload configuration
        String maxSizeStr = EntityUtilProperties.getPropertyValue("general.properties", "http.upload.max.size", "-1", dctx.getDelegator());
        long maxUploadSize = -1;
        try {
            maxUploadSize = Long.parseLong(maxSizeStr);
        } catch (NumberFormatException e) {
            Debug.logError(e, "Unable to obtain the max upload size from general.properties; using default -1", module);
            maxUploadSize = -1;
        }
        // get the http size threshold configuration - files bigger than this will be
        // temporarly stored on disk during upload
        String sizeThresholdStr = EntityUtilProperties.getPropertyValue("general.properties", "http.upload.max.sizethreshold", "10240", dctx.getDelegator());
        int sizeThreshold = 10240; // 10K
        try {
            sizeThreshold = Integer.parseInt(sizeThresholdStr);
        } catch (NumberFormatException e) {
            Debug.logError(e, "Unable to obtain the threshold size from general.properties; using default 10K", module);
            sizeThreshold = -1;
        }
		// directory used to temporarily store files that are larger than the configured size threshold
        String tmpUploadRepository = EntityUtilProperties.getPropertyValue("general.properties", "http.upload.tmprepository", "runtime/tmp", dctx.getDelegator());
        String encoding = request.getCharacterEncoding();
        // check for multipart content types which may have uploaded items
		boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
        Map<String, Object> multiPartMap = FastMap.newInstance();
        if (isMultiPart) {
        	
        	File tmpFile = new File(tmpUploadRepository);
        	
        	if(!tmpFile.exists()) {
        		tmpFile.mkdirs();
        	}
        	
            ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory(sizeThreshold, tmpFile));

            // create the progress listener and add it to the session
            FileUploadProgressListener listener = new FileUploadProgressListener();
            upload.setProgressListener(listener);
            //session.setAttribute("uploadProgressListener", listener);

            if (encoding != null) {
                upload.setHeaderEncoding(encoding);
            }
            upload.setSizeMax(maxUploadSize);

            List<FileItem> uploadedItems = null;
            try {
                uploadedItems = UtilGenerics.<FileItem>checkList(upload.parseRequest(request));
            } catch (FileUploadException e) {
                throw new EventHandlerException("Problems reading uploaded data", e);
            }
            if (uploadedItems != null) {
                for (FileItem item: uploadedItems) {
                    String fieldName = item.getFieldName();
                    //byte[] itemBytes = item.get();
                    /*
                    Debug.logInfo("Item Info [" + fieldName + "] : " + item.getName() + " / " + item.getSize() + " / " +
                            item.getContentType() + " FF: " + item.isFormField(), module);
                    */
                    if (item.isFormField() || item.getName() == null) {
                        if (multiPartMap.containsKey(fieldName)) {
                            Object mapValue = multiPartMap.get(fieldName);
                            if (mapValue instanceof List<?>) {
                                checkList(mapValue, Object.class).add(item.getString());
                            } else if (mapValue instanceof String) {
                                List<String> newList = FastList.newInstance();
                                newList.add((String) mapValue);
                                newList.add(item.getString());
                                multiPartMap.put(fieldName, newList);
                            } else {
                                Debug.logWarning("Form field found [" + fieldName + "] which was not handled!", module);
                            }
                        } else {
                            if (encoding != null) {
                                try {
                                    multiPartMap.put(fieldName, item.getString(encoding));
                                } catch (java.io.UnsupportedEncodingException uee) {
                                    Debug.logError(uee, "Unsupported Encoding, using deafault", module);
                                    multiPartMap.put(fieldName, item.getString());
                                }
                            } else {
                                multiPartMap.put(fieldName, item.getString());
                            }
                        }
                    } else {
                        String fileName = item.getName();
                        if (fileName.indexOf('\\') > -1 || fileName.indexOf('/') > -1) {
                            // get just the file name IE and other browsers also pass in the local path
                            int lastIndex = fileName.lastIndexOf('\\');
                            if (lastIndex == -1) {
                                lastIndex = fileName.lastIndexOf('/');
                            }
                            if (lastIndex > -1) {
                                fileName = fileName.substring(lastIndex + 1);
                            }
                        }
                        multiPartMap.put(fieldName, ByteBuffer.wrap(item.get()));
                        multiPartMap.put("_" + fieldName + "_size", Long.valueOf(item.getSize()));
                        multiPartMap.put("_" + fieldName + "_fileName", fileName);
                        multiPartMap.put("_" + fieldName + "_contentType", item.getContentType());
                    }
                }
            }
        }

        // store the multi-part map as an attribute so we can access the parameters
        //request.setAttribute("multiPartMap", multiPartMap);
        return multiPartMap;
	}
}
