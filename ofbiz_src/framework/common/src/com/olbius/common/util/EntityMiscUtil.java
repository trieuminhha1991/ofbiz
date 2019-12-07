package com.olbius.common.util;

import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.*;

public class EntityMiscUtil {
	public final static String module = EntityMiscUtil.class.getName();
	public final static Integer JQGRID_VIEW_SIZE = 20;

	public static String getParameter(Map<String, String[]> parameters, String key) {
		String resultValue = null;
		if (UtilValidate.isEmpty(key) || UtilValidate.isEmpty(parameters)) return resultValue;
		if (parameters.containsKey(key) && parameters.get(key).length > 0) {
			resultValue = parameters.get(key)[0]; 
		}
		return resultValue;
	}
	
	public static List<GenericValue> processIteratorToList(Map<String, String[]> parameters, Map<String, Object> successResult, 
			Delegator delegator, String tableName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, 
			Set<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException {
    	List<GenericValue> returnValue = FastList.newInstance();
    	
    	EntityListIterator listIterator = null;
    	try {
    		listIterator = processIterator(parameters, successResult, delegator, tableName, 
	    			whereEntityCondition, havingEntityCondition, fieldsToSelect, orderBy, findOptions);
	    	String totalRows = (String) successResult.get("TotalRows");
	    	if (UtilValidate.isNotEmpty(listIterator)) {
	    		String viewIndexStr = getParameter(parameters, "pagenum");
	        	String viewSizeStr = getParameter(parameters, "pagesize");
	        	int viewIndex = (viewIndexStr == null || viewIndexStr.isEmpty()) ? 0 : new Integer(viewIndexStr);
	        	int viewSize = (viewSizeStr == null || viewSizeStr.isEmpty()) ? JQGRID_VIEW_SIZE : new Integer(viewSizeStr);
	        	//int viewOffset = viewIndex * viewSize;
	    		if (UtilValidate.isEmpty(totalRows)) {
	    			if (viewSize != 0) {
	    				if (viewIndex == 0) {
	    					returnValue = listIterator.getPartialList(0, viewSize);
	    				} else {
	    					returnValue = listIterator.getPartialList(viewIndex * viewSize + 1, viewSize);
	    				}
	    			} else {
	    				returnValue = listIterator.getCompleteList();
	    			}
	    		} else {
	    			returnValue = listIterator.getCompleteList();
	    		}
	    	}
    	} catch (GenericEntityException e) {
    		throw e;
    	} finally {
    		if (listIterator != null) {
    			try {
    				listIterator.close();
    			} catch (GenericEntityException e) {
    				Debug.logError(e, "Error when close iterator", module);
    			}
    		}
		}
		
    	return returnValue;
    }
	
	/**
	 * Process EntityListIterator before response service, which is called from event "jqxGridGeneralServicer".<br/>
	 * <b>Require: </b> The service, which call this method, have to implement service "jqxGetListInterface" 
	 * @param parameters
	 * @param successResult
	 * @param delegator
	 * @param tableName
	 * @param whereEntityCondition
	 * @param havingEntityCondition
	 * @param fieldsToSelect
	 * @param orderBy
	 * @param findOptions
	 * @return A entityListIterator
	 * @throws GenericEntityException
	 */
	public static EntityListIterator processIterator(Map<String, String[]> parameters, Map<String, Object> successResult, 
			Delegator delegator, String tableName, EntityCondition whereEntityCondition, EntityCondition havingEntityCondition, 
			Set<String> fieldsToSelect, List<String> orderBy, EntityFindOptions findOptions) throws GenericEntityException {
		EntityListIterator listIterator = null;
		String isInitStr = getParameter(parameters, "isInit");
		Boolean isInit = "false".equals(isInitStr) ? false : true;
		if (isInit) {
			// calculate and return TotalRows
			Long totalRows = delegator.findCountByCondition(tableName, whereEntityCondition, null, fieldsToSelect, findOptions);
			successResult.put("TotalRows", totalRows.toString());
		} else {
			successResult.put("TotalRows", "0");
		}
		String viewIndexStr = getParameter(parameters, "pagenum");
    	String viewSizeStr = getParameter(parameters, "pagesize");
    	int viewIndex = (viewIndexStr == null || viewIndexStr.isEmpty()) ? 0 : new Integer(viewIndexStr);
    	int viewSize = (viewSizeStr == null || viewSizeStr.isEmpty()) ? JQGRID_VIEW_SIZE : new Integer(viewSizeStr);
    	int viewOffset = viewIndex * viewSize;
    	
    	if (viewSize != 0) {
    		findOptions.setLimit(viewSize);
    		findOptions.setOffset(viewOffset);
    	}
		listIterator = delegator.find(tableName, whereEntityCondition, havingEntityCondition, fieldsToSelect, orderBy, findOptions);
		
		return listIterator;
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> sortList(List<Map<String,Object>> listProductCaculateds, List<String> listSortFields){
		if (UtilValidate.isEmpty(listSortFields)) return listProductCaculateds;
		
		String sortField = listSortFields.toString();
    	if(sortField.contains("[")){
    		sortField = StringUtil.replaceString(sortField, "[", "");
    	}
    	if(sortField.contains("]")){
    		sortField = StringUtil.replaceString(sortField, "]", "");
    	}
    	
    	CompareObj objCompare = new CompareObj();
		objCompare.setSortField(sortField);
		
		Collections.sort(listProductCaculateds, objCompare);
		return listProductCaculateds;
		
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> sortList(List<Map<String,Object>> listProductCaculateds, List<String> listSortFields, boolean isReverse){
		if (UtilValidate.isEmpty(listSortFields)) return listProductCaculateds;
		
		String sortField = listSortFields.toString();
    	if(sortField.contains("[")){
    		sortField = StringUtil.replaceString(sortField, "[", "");
    	}
    	if(sortField.contains("]")){
    		sortField = StringUtil.replaceString(sortField, "]", "");
    	}
    	
    	CompareObj objCompare = new CompareObj();
		objCompare.setSortField(sortField);
		objCompare.setReverse(isReverse);
		
		Collections.sort(listProductCaculateds, objCompare);
		return listProductCaculateds;
		
	}
	
	public static List<EntityCondition> processMergeListAllCondition(List<Map<String, Object>> listAllCondMap){
		// process list all condition
		List<EntityCondition> listAllConditions = FastList.newInstance();
    	for (EntityCondition condition : listAllConditions){
			String cond = condition.toString();
			if(UtilValidate.isNotEmpty(cond)){
				String[] conditionSplit = cond.split(" ");
				Map<String, Object> condMap = new HashMap<String, Object>();
				String fieldName = (String) conditionSplit[0];
				String operator = (String) conditionSplit[1];
				String value = (String) conditionSplit[2].trim();
				if(fieldName.contains("(")) fieldName = StringUtil.replaceString(fieldName, "(", "");
				if(fieldName.contains(")")) fieldName = StringUtil.replaceString(fieldName, ")", "");
				if(value.contains("(")) value = StringUtil.replaceString(value, "(", "");
				if(value.contains(")")) value = StringUtil.replaceString(value, ")", "");
				if(value.contains("'")) value = StringUtil.replaceString(value, "'", "");
				if(value.contains("%")) value = StringUtil.replaceString(value, "%", "");
				condMap.put("fieldName", fieldName);
				condMap.put("operator",operator );
				condMap.put("value", value);
				listAllCondMap.add(condMap);
			}
		}
    	return listAllConditions;
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
				
				if (conditionSplit.length > 3) {
					if (UtilValidate.isNotEmpty(conditionSplit[3].trim())) {
						if ("AND".equals(conditionSplit[3].trim())) {
							operator = "RANGE";
							String valueFrom = (String) conditionSplit[2].trim();
							String valueTo = (String) conditionSplit[6].trim();
							valueFrom = cleanValue(valueFrom);
							valueTo = cleanValue(valueTo);
							
							condMap.put("valueFrom", valueFrom);
							condMap.put("valueTo", valueTo);
						}
					}
				}

                boolean hasUpper = fieldName.contains("UPPER(");
				fieldName = cleanFieldName(fieldName);
				value = cleanValue(value);
				
				condMap.put("fieldName", fieldName);
				condMap.put("operator",operator );
				condMap.put("value", value);
                condMap.put("hasUpper", hasUpper);
				listMapConditions.add(condMap);
			}
		}
    	return listMapConditions;
	}
	public static String cleanValue(String value) {
		if (UtilValidate.isEmpty(value)) return value;

        if(value.contains("UPPER(")){
            value = value.replace("UPPER(", "");
        } else if(value.contains("(")){
			value = value.replace("(", "");
		}
		if(value.contains(")")){
			value = value.replace(")", "");
		}
		if(value.contains("'")){
			value = value.replace("'", "");
		}
		if(value.contains("%")){
			value = value.replace("%", "");
		}
		return value;
	}
	public static String cleanFieldName(String fieldName) {
        if(fieldName.contains("UPPER(")){
            fieldName = fieldName.replace("UPPER(", "");
        } else if(fieldName.contains("(")){
			fieldName = fieldName.replace("(", "");
		}
		if(fieldName.contains(")")){
			fieldName = fieldName.replace(")", "");
		}
		return fieldName;
	}
	
	public static List<Map<String, Object>> filterMap(List<Map<String, Object>> listProductCaculateds, List<EntityCondition> listAllConditions) throws ParseException{
		if (UtilValidate.isEmpty(listAllConditions)) return listProductCaculateds;
		
		// process list all condition
		List<Map<String, Object>> listAllCondMap = processSplitListAllCondition(listAllConditions);
		return filterMapFromMapCond(listProductCaculateds, listAllCondMap);
	}
	
	public static List<Map<String, Object>> filterMapFromMapCond(List<Map<String, Object>> listProductCaculateds, List<Map<String, Object>> listAllCondMap) throws ParseException{
		List<Map<String, Object>> returnResult = FastList.newInstance();
		if(UtilValidate.isEmpty(listAllCondMap)){
			return listProductCaculateds;
		} else {
			for (Map<String, Object> x : listProductCaculateds) {
				boolean pass = true;
				for (Map<String, Object> m : listAllCondMap) {
					String fieldName = (String) m.get("fieldName");
					String operator = (String) m.get("operator");
					String value = (String) m.get("value");
					
					Object fieldValue = x.get(fieldName);
					
					if (UtilValidate.isEmpty(fieldValue) || UtilValidate.isEmpty(operator) || UtilValidate.isEmpty(value.trim())) {
						pass = false;
						break;
					}
					if(operator.equalsIgnoreCase("LIKE")){
						if (!fieldValue.toString().contains(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("NOT_LIKE")){
						if (fieldValue.toString().contains(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("EQUAL")){
						if (!fieldValue.toString().equals(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("NOT_EQUAL")){
						if (fieldValue.toString().equals(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("RANGE")){
						String valueFrom = (String) m.get("valueFrom");
						String valueTo = (String) m.get("valueTo");
						if (UtilValidate.isEmpty(valueFrom.trim()) || UtilValidate.isEmpty(valueTo.trim())) {
							pass = false;
							break;
						}
						if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueFromTs = Timestamp.valueOf(valueFrom + " 00:00:00.0");
							Timestamp valueToTs = Timestamp.valueOf(valueTo + " 23:59:59.0");
							if(fieldValueTs.before(valueFromTs)){
								pass = false;
							}
							if(fieldValueTs.after(valueToTs)){
								pass = false;
							}
						} if (fieldValue instanceof Date) {
							Date fieldValueDate = (Date) fieldValue;
							Date valueFromDate = Date.valueOf(valueFrom);
							Date valueToDate = Date.valueOf(valueTo);
							if (fieldValueDate.compareTo(valueFromDate) < 0) {
								pass = false;
							}
							if (fieldValueDate.compareTo(valueToDate) > 0) {
								pass = false;
							}
						}
					}else if(operator.equalsIgnoreCase("=")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) != 0){
								pass = false;
							}
						} else  if (fieldValue instanceof Integer) {
							Integer fieldValueInt = (Integer) fieldValue;
							Integer valueInt = Integer.valueOf(value);
							if (fieldValueInt != valueInt) {
								pass = false;
							}
						} else if (fieldValue instanceof String) {
							if (!fieldValue.toString().equals(value)) {
								pass = false;
							}
						}
					}else if(operator.equalsIgnoreCase(">=")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) < 0){
								pass = false;
							}
						}else if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueTs = Timestamp.valueOf(value + " 00:00:00.0");
							if(fieldValueTs.before(valueTs)){
								pass = false;
							}
						}else if (fieldValue instanceof Date) {
							Date fieldValueDate = (Date) fieldValue;
							Date valueDate = Date.valueOf(value);
							if (fieldValueDate.compareTo(valueDate) < 0) {
								pass = false;
							}
						}
					}else if(operator.equalsIgnoreCase("<=")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) > 0){
								pass = false;
								break;
							}
						}else if (fieldValue instanceof Timestamp) {
							Timestamp fieldValueTs = (Timestamp) fieldValue;
							Timestamp valueTs = Timestamp.valueOf(value + " 00:00:00.0");
							if(fieldValueTs.after(valueTs)){
								pass = false;
							}
						}else if (fieldValue instanceof Date) {
							Date fieldValueDate = (Date) fieldValue;
							Date valueDate = Date.valueOf(value);
							if (fieldValueDate.compareTo(valueDate) > 0) {
								pass = false;
							}
						}
					}else if(operator.equalsIgnoreCase(">")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) <= 0){
								pass = false;
								break;
							}
						}
					}else if(operator.equalsIgnoreCase("<")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) >= 0){
								pass = false;
								break;
							}
						}
					}else if(operator.equalsIgnoreCase("<>")){
						if (fieldValue instanceof BigDecimal) {
							BigDecimal fieldValueBd = (BigDecimal) fieldValue;
							BigDecimal valueBd = new BigDecimal(value);
							if(fieldValueBd.compareTo(valueBd) == 0){
								pass = false;
								break;
							}
						}
					}
				}
				if (pass) {
					returnResult.add(x);
				}
			}
		}
		return returnResult;
 	}
}
