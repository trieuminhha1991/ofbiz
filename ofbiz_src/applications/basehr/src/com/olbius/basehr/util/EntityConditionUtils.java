package com.olbius.basehr.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.olbius.common.util.EntityMiscUtil;
import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.ModelEntity;

public class EntityConditionUtils {
	public static EntityCondition makeDateConds(Timestamp fromDate, Timestamp thruDate){
		return makeDateConds(fromDate, thruDate, "fromDate", "thruDate");
	}

	public static EntityCondition makeDateConds(Timestamp fromDate, Timestamp thruDate, String fromDateStr, String thruDateStr){
		List<EntityCondition> conditions = FastList.newInstance();
		if(thruDate != null){
			conditions.add(EntityCondition.makeCondition(fromDateStr, EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
		}
		if(fromDate != null){
			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition(thruDateStr, null),
					EntityJoinOperator.OR,
					EntityCondition.makeCondition(thruDateStr, EntityOperator.GREATER_THAN_EQUAL_TO, fromDate)));
		}
		return EntityCondition.makeCondition(conditions);
	}
	
	public static List<GenericValue> doFilterGenericValue(List<GenericValue> listData, List<EntityCondition> listAllConditions) {
		List<GenericValue> listReturn = FastList.newInstance();
		List<Map<String, Object>> listConditions = makeListConditions(listAllConditions);
		if(UtilValidate.isNotEmpty(listConditions)){
			for (GenericValue x : listData) {
				boolean pass = true;
				for (Map<String, Object> m : listConditions) {
					String fieldName = (String) m.get("fieldName");
					String operator = (String) m.get("operator");
					String value = (String) m.get("value");

                    fieldName = cleanFieldNameUPPER(fieldName);
                    value = cleanValueUPPER(value).toUpperCase();
					Object fieldValue = x.get(fieldName);
					if ((UtilValidate.isEmpty(fieldValue) || UtilValidate.isEmpty(operator) || UtilValidate.isEmpty(value.trim())) && (!value.trim().equals("null"))) {
						pass = false;
						break;
					}
					
					if(operator.equalsIgnoreCase("LIKE")){
						if (!fieldValue.toString().toUpperCase().contains(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("NOT_LIKE")){
						if (fieldValue.toString().toUpperCase().contains(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("EQUAL")){
						if (!fieldValue.toString().toUpperCase().equals(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("NOT_EQUAL")){
						if (fieldValue.toString().toUpperCase().equals(value)) {
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
						}else if (fieldValue instanceof Float){
							Float valueFloat = Float.valueOf(value);
							Float fieldValueFloat = (Float) fieldValue;
							if(fieldValueFloat.equals(valueFloat)){
								pass = false;
							}
						}else if (fieldValue instanceof Long){
							Long valueLong = Long.valueOf(value);
							Long fieldValueLong = (Long) fieldValue;
							if(!fieldValueLong.equals(valueLong)){
								pass = false;
							}
						}else if (fieldValue instanceof Double){
							Double valueDouble = Double.valueOf(value);
							Double fieldValueDouble = (Double) fieldValue;
							if(!fieldValueDouble.equals(valueDouble)){
								pass = false;
							}
						}else if(UtilValidate.isEmpty(fieldValue)){
							String fieldValueNull = "null";
							if(!fieldValueNull.equals(value.trim())){
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
						}else if (fieldValue instanceof Float){
							Float valueFloat = Float.valueOf(value);
							Float fieldValueFloat = (Float) fieldValue;
							if(fieldValueFloat.compareTo(valueFloat) < 0){
								pass = false;
							}
						}else if (fieldValue instanceof Long){
							Long valueLong = Long.valueOf(value);
							Long fieldValueLong = (Long) fieldValue;
							if(fieldValueLong.compareTo(valueLong) < 0){
								pass = false;
							}
						}else if (fieldValue instanceof Double){
							Double valueDouble = Double.valueOf(value);
							Double fieldValueDouble = (Double) fieldValue;
							if(fieldValueDouble.compareTo(valueDouble) < 0){
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
						}else if (fieldValue instanceof Float){
							Float valueFloat = Float.valueOf(value);
							Float fieldValueFloat = (Float) fieldValue;
							if(fieldValueFloat.compareTo(valueFloat) > 0){
								pass = false;
							}
						}else if (fieldValue instanceof Long){
							Long valueLong = Long.valueOf(value);
							Long fieldValueLong = (Long) fieldValue;
							if(fieldValueLong.compareTo(valueLong) > 0){
								pass = false;
							}
						}else if (fieldValue instanceof Double){
							Double valueDouble = Double.valueOf(value);
							Double fieldValueDouble = (Double) fieldValue;
							if(fieldValueDouble.compareTo(valueDouble) > 0){
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
						else if (fieldValue instanceof Float){
							Float valueFloat = Float.valueOf(value);
							Float fieldValueFloat = (Float) fieldValue;
							if(fieldValueFloat.compareTo(valueFloat) < 0 || fieldValueFloat.equals(valueFloat)){
								pass = false;
							}
						}else if (fieldValue instanceof Long){
							Long valueLong = Long.valueOf(value);
							Long fieldValueLong = (Long) fieldValue;
							if(fieldValueLong.compareTo(valueLong) < 0 || fieldValueLong.equals(valueLong)){
								pass = false;
							}
						}else if (fieldValue instanceof Double){
							Double valueDouble = Double.valueOf(value);
							Double fieldValueDouble = (Double) fieldValue;
							if(fieldValueDouble.compareTo(valueDouble) < 0 || fieldValueDouble.equals(valueDouble)){
								pass = false;
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
						}else if (fieldValue instanceof Float){
							Float valueFloat = Float.valueOf(value);
							Float fieldValueFloat = (Float) fieldValue;
							if(fieldValueFloat.compareTo(valueFloat) > 0 || fieldValueFloat.equals(valueFloat)){
								pass = false;
							}
						}else if (fieldValue instanceof Long){
							Long valueLong = Long.valueOf(value);
							Long fieldValueLong = (Long) fieldValue;
							if(fieldValueLong.compareTo(valueLong) > 0 || fieldValueLong.equals(valueLong)){
								pass = false;
							}
						}else if (fieldValue instanceof Double){
							Double valueDouble = Double.valueOf(value);
							Double fieldValueDouble = (Double) fieldValue;
							if(fieldValueDouble.compareTo(valueDouble) > 0 || fieldValueDouble.equals(valueDouble)){
								pass = false;
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
					listReturn.add(x);
				}
			}
		}else {
			return listData;
		}
		return listReturn;
	}
	
	public static List<Map<String, Object>> doFilter(List<Map<String, Object>> listData, List<EntityCondition> listAllConditions) {
		List<Map<String, Object>> listReturn = FastList.newInstance();
		List<Map<String, Object>> listConditions = makeListConditions(listAllConditions);
		if(UtilValidate.isNotEmpty(listConditions)){
			for (Map<String, Object> x : listData) {
				boolean pass = true;
				for (Map<String, Object> m : listConditions) {
					String fieldName = (String) m.get("fieldName");
					String operator = (String) m.get("operator");
					String value = (String) m.get("value");

                    fieldName = cleanFieldNameUPPER(fieldName);
                    value = cleanValueUPPER(value).toUpperCase();
					Object fieldValue = x.get(fieldName);
					
					if (UtilValidate.isEmpty(fieldValue) || UtilValidate.isEmpty(operator) || UtilValidate.isEmpty(value.trim())) {
						pass = false;
						break;
					}
					if(operator.equalsIgnoreCase("LIKE")){
						if (!fieldValue.toString().toUpperCase().contains(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("NOT_LIKE")){
						if (fieldValue.toString().toUpperCase().contains(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("EQUAL")){
						if (!fieldValue.toString().toUpperCase().equals(value)) {
							pass = false;
						}
					}else if(operator.equalsIgnoreCase("NOT_EQUAL")){
						if (fieldValue.toString().toUpperCase().equals(value)) {
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
						} if(fieldValue instanceof Long){
							long fieldValuelong = (long) fieldValue;
							Date fieldValueDate = new Date(fieldValuelong);
							Date valueFromDate = Date.valueOf(valueFrom);
							Date valueToDate = Date.valueOf(valueTo);
							if(fieldValueDate.compareTo(valueFromDate) < 0){
								pass = false;
							}
							if(fieldValueDate.compareTo(valueToDate) > 0){
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
						} else if (fieldValue instanceof Float){
							Float ValueFloat = Float.valueOf(value);
							Float fieldValueFloat = (Float) fieldValue;
							if(!fieldValueFloat.equals(ValueFloat)){
								pass = false;
							}
						}else if (fieldValue instanceof Long){
							Long ValueLong = Long.valueOf(value);
							Long fieldValueLong = (Long) fieldValue;
							if(!fieldValueLong.equals(ValueLong)){
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
						else if (fieldValue instanceof Float){
							Float ValueFloat = Float.valueOf(value);
							Float fieldValueFloat = (Float) fieldValue;
							if(fieldValueFloat > ValueFloat){
								pass = false;
							}
						}else if(fieldValue instanceof Integer){
							Integer ValueInt = Integer.valueOf(value);
							Integer fieldValueInt = (Integer) fieldValue;
							if(fieldValueInt.compareTo(ValueInt) < 0){
								pass = false;
							}
						}else if(fieldValue instanceof Long){
							Long valueLong = Long.valueOf(value);
							Long fieldValueLong = (Long) fieldValue;
							if(fieldValueLong.compareTo(valueLong) < 0){
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
						}else if (fieldValue instanceof Float){
							Float ValueFloat = Float.valueOf(value);
							Float fieldValueFloat = (Float) fieldValue;
							if(fieldValueFloat < ValueFloat){
								pass = false;
							}
						}else if(fieldValue instanceof Integer){
							Integer ValueInt = Integer.valueOf(value);
							Integer fieldValueInt = (Integer) fieldValue;
							if(fieldValueInt.compareTo(ValueInt) > 0){
								pass = false;
							}
						}else if(fieldValue instanceof Long){
							Long valueLong = Long.valueOf(value);
							Long fieldValueLong = (Long) fieldValue;
							if(fieldValueLong.compareTo(valueLong) > 0){
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
						else if (fieldValue instanceof Float){
							Float ValueFloat = Float.valueOf(value);
							Float fieldValueFloat = (Float) fieldValue;
							if(fieldValueFloat.compareTo(ValueFloat) < 0 || fieldValueFloat == ValueFloat){
								pass = false;
							}
						}else if(fieldValue instanceof Integer){
							Integer ValueInt = Integer.valueOf(value);
							Integer fieldValueInt = (Integer) fieldValue;
							if(fieldValueInt.compareTo(ValueInt) < 0 || fieldValueInt.equals(ValueInt)){
								pass = false;
							}
						}else if(fieldValue instanceof Long){
							Long valueLong = Long.valueOf(value);
							Long fieldValueLong = (Long) fieldValue;
							if(fieldValueLong.compareTo(valueLong) < 0 || fieldValueLong.equals(valueLong)){
								pass = false;
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
						else if (fieldValue instanceof Float){
							Float ValueFloat = Float.valueOf(value);
							Float fieldValueFloat = (Float) fieldValue;
							if(fieldValueFloat.compareTo(ValueFloat) > 0 || fieldValueFloat == ValueFloat){
								pass = false;
							}
						}else if(fieldValue instanceof Integer){
							Integer ValueInt = Integer.valueOf(value);
							Integer fieldValueInt = (Integer) fieldValue;
							if(fieldValueInt.compareTo(ValueInt) > 0 || fieldValueInt.equals(ValueInt)){
								pass = false;
							}
						}else if(fieldValue instanceof Long){
							Long valueLong = Long.valueOf(value);
							Long fieldValueLong = (Long) fieldValue;
							if(fieldValueLong.compareTo(valueLong) > 0 || fieldValueLong.equals(valueLong)){
								pass = false;
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
					listReturn.add(x);
				}
			}
		}else {
			return listData;
		}
		return listReturn;
	}
    public static String cleanValueUPPER(String value) {
        if (UtilValidate.isEmpty(value)) return value;

        if(value.contains("UPPER")){
            value = value.replace("UPPER", "");
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
    public static String cleanFieldNameUPPER(String fieldName) {
        if(fieldName.contains("UPPER")){
            fieldName = fieldName.replace("UPPER", "");
        } else if(fieldName.contains("(")){
            fieldName = fieldName.replace("(", "");
        }
        if(fieldName.contains(")")){
            fieldName = fieldName.replace(")", "");
        }
        return fieldName;
    }
	
	private static List<Map<String, Object>> makeListConditions(List<EntityCondition> listConditions) {
		List<Map<String, Object>> listMapConditions = FastList.newInstance();
		for (EntityCondition condition : listConditions) {
			String cond = condition.toString();
			if(UtilValidate.isNotEmpty(cond)){
				String[] conditionSplit = cond.split(" ");
				Map<String, Object> condMap = FastMap.newInstance();
				String fieldName = (String) conditionSplit[0];
				String operator = (String) conditionSplit[1];
				String value = (String) conditionSplit[2].trim();
				
				if (conditionSplit.length > 4) {
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
				
				fieldName = cleanFieldName(fieldName);
				value = cleanValue(value);
				
				condMap.put("fieldName", fieldName);
				condMap.put("operator",operator );
				condMap.put("value", value);
				listMapConditions.add(condMap);
			}
		}
		return listMapConditions;
	}
	
	private static String cleanValue(String value) {
		if(value.contains("(")){
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
	
	private static String cleanFieldName(String fieldName) {
		if(fieldName.contains("(")){
			fieldName = fieldName.replace("(", "");
		}
		if(fieldName.contains(")")){
			fieldName = fieldName.replace(")", "");
		}
		return fieldName;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> sortList(List<Map<String,Object>> list, List<String> listSortFields){
		if (UtilValidate.isEmpty(listSortFields)) return list;
		
		String sortField = listSortFields.get(0);
    	if(sortField.contains("[")){
    		sortField = StringUtil.replaceString(sortField, "[", "");
    	}
    	if(sortField.contains("]")){
    		sortField = StringUtil.replaceString(sortField, "]", "");
    	}
    	
    	CompareObjAdvance objCompare = new CompareObjAdvance();
		objCompare.setSortField(sortField);
		
		Collections.sort(list, objCompare);
		return list;
		
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> sortList(List<Map<String,Object>> list, List<String> listSortFields, boolean isReverse){
		if (UtilValidate.isEmpty(listSortFields)) return list;
		
		String sortField = listSortFields.toString();
    	if(sortField.contains("[")){
    		sortField = StringUtil.replaceString(sortField, "[", "");
    	}
    	if(sortField.contains("]")){
    		sortField = StringUtil.replaceString(sortField, "]", "");
    	}
    	
    	CompareObjAdvance objCompare = new CompareObjAdvance();
		objCompare.setSortField(sortField);
		objCompare.setReverse(isReverse);
		
		Collections.sort(list, objCompare);
		return list;
		
	}

	public static void splitFilterListCondition(List<EntityCondition> listAllConditions, List<String> listFieldInEntity, 
			List<EntityCondition> condsForFieldInEntity, List<EntityCondition> condsForFieldNotInEntity) {
		for(EntityCondition condition: listAllConditions){
			String cond = condition.toString();
			if(isCondForFieldInEntity(cond, listFieldInEntity)){
				condsForFieldInEntity.add(condition);
			}else{
				condsForFieldNotInEntity.add(condition);
			}
		}
	}

	private static boolean isCondForFieldInEntity(String cond,
			List<String> listFieldInEntity) {
		for(String field: listFieldInEntity){
			if(cond.contains(field)){
				return true;
			}
		}
		return false;
	}

	public static void splitSortedList(List<String> listSortFields, List<String> listFieldInEntity, 
			List<String> sortedFieldInEntity, List<String> sortedFieldNotInEntity) {
		for(String sortedField: listSortFields){
			String tempSortedField = sortedField;
			if(sortedField.contains("-")){
				tempSortedField = sortedField.substring(sortedField.indexOf("-") + 1);
			}
			if(listFieldInEntity.contains(tempSortedField)){
				sortedFieldInEntity.add(sortedField);
			}else{
				sortedFieldNotInEntity.add(sortedField);
			}
		}
	}

	public static List<String> getFieldListInEntity(Delegator delegator, String entityName) {
		GenericValue entity = delegator.makeValue(entityName);
		ModelEntity modelEntity = entity.getModelEntity();
		return modelEntity.getAllFieldNames();
	}
	
	/*public static List<Map<String, Object>> sortListMap(List<Map<String, Object>> listProductCaculateds,
			String sortField) {
		POUtil poUtil = new POUtil();
		poUtil.setSortField(sortField);
		Collections.sort(listProductCaculateds, poUtil);
		return listProductCaculateds;
	}*/
}

