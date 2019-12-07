package com.olbius.services;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;


/*
 * TODO: seperate UI and functionality matters ! 
 Seperate business logic, do not access entity directly, please do it under a business logic service
 */
public class JqxWidgetSevices {
	public static final String module = JqxWidgetSevices.class.getName();
	public static final String resource = "widgetUiLabels";
	public static final String resourceError = "widgetErrorUiLabels";

	public static Map<String, Object> jqxGridServicerUpdateMultiples(
			DispatchContext ctx, Map<String, ? extends Object> context) {
		String strEntityName = (String) context.get("entityName");
		String strConumnName = (String) context.get("columnName");
		String strOldValue = (String) context.get("oldValue");
		String strNewValue = (String) context.get("newValue");
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			EntityExpr expr = new EntityExpr();
			expr.init(strConumnName, EntityOperator.EQUALS, strOldValue);

			delegator.storeByCondition(strEntityName,
					UtilMisc.toMap(strConumnName, strNewValue), expr);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> gridServicer(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String strViewIndex = (String) context.get("pagenum");
		String strViewSize = (String) context.get("pagesize");
		String strSortOrder = (String) context.get("sortorder");
		String strSortDataField = (String) context.get("sortdatafield");
		String strFilterListFields = (String) context.get("filterListFields");
		String strEntityName = (String) context.get("entityName");
		String strNoConditionsFind = (String) context.get("noConditionFind");
		String strConditionsFind = (String) context.get("conditionsFind");
		String strDictionaryColumns = (String) context.get("dictionaryColumns");

		// Check sortdatafield
		List<String> listSortFields = null;
		if (strSortDataField != null && !strSortDataField.isEmpty()) {
			if (!"asc".equals(strSortOrder)) {
				// strSortDataField = "-" + strSortDataField;
				String[] arrSortDataField = strSortDataField.split(";");
				listSortFields = new ArrayList<String>();
				for (int i = 0; i < arrSortDataField.length; i++) {
					arrSortDataField[i] = "-" + arrSortDataField[i];
					listSortFields.add(arrSortDataField[i]);
				}
			} else {
				String[] arrSortDataField = strSortDataField.split(";");
				listSortFields = new ArrayList<String>();
				for (int i = 0; i < arrSortDataField.length; i++) {
					listSortFields.add(arrSortDataField[i]);
				}
			}
		}
		EntityFindOptions opts = new EntityFindOptions(true,
				EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
				EntityFindOptions.CONCUR_READ_ONLY, false);
		// FIXME consider to use OFFSET and LIMIT
		
		Integer iSize = 0;
		Integer iIndex = 0;
		if (strViewSize != null && !strViewSize.isEmpty()) {
			iSize = new Integer(strViewSize);
			iIndex = new Integer(strViewIndex);
			// opts.setFetchSize(iSize);
			// if(strViewIndex != null && !strViewIndex.isEmpty()){
			// opts.setOffset(iSize*iIndex);
			// }
			// opts.setMaxRows(iSize);
			opts.setLimit(iSize);
			opts.setOffset(iIndex);
		}
		// EntityCondition.makeCondition(lhs, operator, rhs);
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		// create one true condition
		/*
		 * EntityCondition ecTmp1 = EntityCondition.makeCondition("fromDate",
		 * EntityOperator.EQUALS, null); EntityCondition ecTmp2 =
		 * EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL,
		 * null);
		 * listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList
		 * (ecTmp1, ecTmp2), EntityJoinOperator.AND));
		 */

		if ((strFilterListFields != null && !strFilterListFields.isEmpty())
				|| strNoConditionsFind.equals("N")) {
			List<EntityCondition> listTmpCondition = new ArrayList<EntityCondition>();
			String tmpFieldName = "";
			if (strFilterListFields == null || strFilterListFields.isEmpty())
				strFilterListFields = strConditionsFind;
			else
				strFilterListFields = strConditionsFind + strFilterListFields;
			String[] arrField = strFilterListFields.split("\\|OLBIUS\\|");
			String tmpGO = "0";
			for (int i = 1; i < arrField.length; i++) {
				String[] arrTmp = arrField[i].split("\\|SUIBLO\\|");
				SqlOperator so = SqlOperator.valueOf(arrTmp[2]); // Filter
																	// condition
				EntityComparisonOperator<?, ?> fieldOp = null;
				tmpGO = arrTmp[3]; // Filter Operator
				String fieldValue = arrTmp[1].toString(); // Filter value
				String fieldName = arrTmp[0]; // Filter name
				switch (so) {
				case CONTAINS: {
					fieldOp = EntityOperator.LIKE;
					fieldValue = "%" + fieldValue + "%";
					break;
				}
				case DOES_NOT_CONTAIN: {
					fieldOp = EntityOperator.NOT_LIKE;
					fieldValue = "%" + fieldValue + "%";
					break;
				}
				case EQUAL: {
					fieldOp = EntityOperator.EQUALS;
					break;
				}
				case NOT_EQUAL: {
					fieldOp = EntityOperator.NOT_EQUAL;
					break;
				}
				case GREATER_THAN: {
					fieldOp = EntityOperator.GREATER_THAN;
					break;
				}
				case LESS_THAN: {
					fieldOp = EntityOperator.LESS_THAN;
					break;
				}
				case GREATER_THAN_OR_EQUAL: {
					fieldOp = EntityOperator.GREATER_THAN_EQUAL_TO;
					break;
				}
				case LESS_THAN_OR_EQUAL: {
					fieldOp = EntityOperator.LESS_THAN_EQUAL_TO;
					break;
				}
				case STARTS_WITH: {
					fieldOp = EntityOperator.LIKE;
					fieldValue = fieldValue + "%";
					break;
				}
				case ENDS_WITH: {
					fieldOp = EntityOperator.LIKE;
					fieldValue = "%" + fieldValue;
					break;
				}
				case NULL: {
					fieldOp = EntityOperator.EQUALS;
					fieldValue = null;
					break;
				}
				case NOT_NULL: {
					fieldOp = EntityOperator.NOT_EQUAL;
					fieldValue = null;
					break;
				}
				default:
					break;
				}
				if (listTmpCondition.isEmpty()) {
					listTmpCondition.add(EntityCondition.makeCondition(
							fieldName, fieldOp, fieldValue));
					tmpFieldName = fieldName;
				} else {
					// Check for the same field listFieldName
					if (tmpFieldName.equals(fieldName)) { // same field
						listTmpCondition.add(EntityCondition.makeCondition(
								fieldName, fieldOp, fieldValue));
					} else {
						// listAllConditions.addAll(listTmpCondition); // add
						// all
						if (tmpGO.equals("1")) {
							listAllConditions.add(EntityCondition
									.makeCondition(listTmpCondition,
											EntityJoinOperator.OR));
						} else {
							listAllConditions.add(EntityCondition
									.makeCondition(listTmpCondition,
											EntityJoinOperator.AND));
						}
						tmpFieldName = fieldName;
						listTmpCondition = new ArrayList<EntityCondition>(); // reset
																				// list
						listTmpCondition.add(EntityCondition.makeCondition(
								fieldName, fieldOp, fieldValue));
					}
				}
			}
			// add last
			if (listTmpCondition.size() > 1) {
				if (tmpGO.equals("1")) {
					listAllConditions.add(EntityCondition.makeCondition(
							listTmpCondition, EntityJoinOperator.OR));
				} else {
					listAllConditions.add(EntityCondition.makeCondition(
							listTmpCondition, EntityJoinOperator.AND));
				}
			} else {
				listAllConditions.addAll(listTmpCondition);
			}
		}
		try {
			EntityListIterator tmpList = delegator.find(strEntityName,
					EntityCondition.makeCondition(listAllConditions,
							EntityJoinOperator.AND), null, null,
					listSortFields, opts);

			if (iSize != 0) {
				if (iIndex == 0) {
					List<GenericValue> listGeneralValue = null;
					GenericValue nextValue = null;
					listGeneralValue = tmpList.getPartialList(0, iSize);
					for (int i = 0; i < listGeneralValue.size(); i++) {
						nextValue = listGeneralValue.get(i);
						String[] arrDictionaryColumns = strDictionaryColumns
								.split(";");
						for (int iDic = 0; iDic < arrDictionaryColumns.length; iDic++) {
							nextValue.set(arrDictionaryColumns[iDic], nextValue
									.get(arrDictionaryColumns[iDic], locale));
						}
						listGeneralValue.set(i, nextValue);
					}
					result.put("results", listGeneralValue);

				} else {
					List<GenericValue> listGeneralValue = null;
					GenericValue nextValue = null;
					listGeneralValue = tmpList.getPartialList(iIndex * iSize
							+ 1, iSize);
					for (int i = 0; i < listGeneralValue.size(); i++) {
						nextValue = listGeneralValue.get(i);
						String[] arrDictionaryColumns = strDictionaryColumns
								.split(";");
						for (int iDic = 0; iDic < arrDictionaryColumns.length; iDic++) {
							nextValue.set(arrDictionaryColumns[iDic], nextValue
									.get(arrDictionaryColumns[iDic], locale));
						}
						listGeneralValue.set(i, nextValue);
					}
					result.put("results", listGeneralValue);
					// result.put("results", tmpList.getPartialList(iIndex*iSize
					// + 1, iSize));
				}
			} else {
				List<GenericValue> listGeneralValue = null;
				GenericValue nextValue = null;
				listGeneralValue = tmpList.getCompleteList();
				for (int i = 0; i < listGeneralValue.size(); i++) {
					nextValue = listGeneralValue.get(i);
					String[] arrDictionaryColumns = strDictionaryColumns
							.split(";");
					for (int iDic = 0; iDic < arrDictionaryColumns.length; iDic++) {
						nextValue.set(arrDictionaryColumns[iDic], nextValue
								.get(arrDictionaryColumns[iDic], locale));
					}
					listGeneralValue.set(i, nextValue);
				}
				result.put("results", listGeneralValue);
				// result.put("results", tmpList.getCompleteList());
			}
			result.put("TotalRows", String.valueOf(delegator
					.findCountByCondition(strEntityName, EntityCondition
							.makeCondition(listAllConditions,
									EntityJoinOperator.AND), null, null)));
			tmpList.close();

		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> gridGeneralServicer(DispatchContext ctx,
			Map<String, ? extends Object> context) throws Exception {
		Map<String, Object> result = FastMap.newInstance();
		Map<String, String[]> parametersPrime = (Map<String, String[]>) context.get("parameters");
		Map<String, String[]> parameters = new HashMap<String, String[]>();
		parameters.putAll(parametersPrime);
		LocalDispatcher dispatcher = ctx.getDispatcher();
		// Locale locale = (Locale) context.get("locale");
		String strViewIndex = "0";
		if(parameters.get("pagenum") != null){
		    strViewIndex = (String) parameters.get("pagenum")[0];
		}
		String strViewSize = "20";
		if(parameters.get("pagesize") != null){
		    strViewSize = (String) parameters.get("pagesize")[0];
		}
		
		String strSortOrder = ((String[]) parameters.get("sortorder") != null) ? ((String) parameters
				.get("sortorder")[0]) : ("");
		String strSortDataField = ((String[]) parameters.get("sortdatafield") != null) ? ((String) parameters
				.get("sortdatafield")[0]) : ("");
		String strFilterListFields = ((String[]) parameters
				.get("filterListFields") != null) ? ((String) parameters
				.get("filterListFields")[0]) : ("");
		String strDictionaryColumns = ((String[]) parameters
				.get("dictionaryColumns") != null) ? ((String) parameters
				.get("dictionaryColumns")[0]) : ("");
		// String strEntityName =
		// (String)parameters.get("entityName")[0];dictionaryColumns
		String strNoConditionsFind = parameters.get("noConditionFind") != null ? (String)parameters.get("noConditionFind")[0] : "N";
		String strConditionsFind = parameters.get("conditionsFind") != null ? (String) parameters.get("conditionsFind")[0] : "Y";
		// String strDictionaryColumns = (String)
		// parameters.get("dictionaryColumns")[0];
		if(parameters.get("sname") == null){
		    Map<String, Object> tmpReturn = new HashMap<String, Object>();
		    tmpReturn.put("results", new ArrayList<String>());
		    tmpReturn.put("TotalRows", "0");
		    return tmpReturn;
		}
		String strServiceName = (String) parameters.get("sname")[0];

		// Check sortdatafield
		List<String> listSortFields = new ArrayList<String>();
		if (strSortDataField != null && !strSortDataField.isEmpty()) {
			if (!"asc".equals(strSortOrder)) {
				// strSortDataField = "-" + strSortDataField;
				String[] arrSortDataField = strSortDataField.split(";");
				for (int i = 0; i < arrSortDataField.length; i++) {
					arrSortDataField[i] = "-" + arrSortDataField[i];
					listSortFields.add(arrSortDataField[i] + " NULLS LAST");
				}
			} else {
				String[] arrSortDataField = strSortDataField.split(";");
				for (int i = 0; i < arrSortDataField.length; i++) {
					listSortFields.add(arrSortDataField[i] + " NULLS LAST");
				}
			}
		}
		EntityFindOptions opts = new EntityFindOptions(true,
				EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
				EntityFindOptions.CONCUR_READ_ONLY, false);
		Integer iSize = 0;
		Integer iIndex = 0;
		if (strViewIndex != null && !strViewIndex.isEmpty()) {
			iIndex = new Integer(strViewIndex);
			//opts.setOffset(iIndex);
		}
		if (strViewSize != null && !strViewSize.isEmpty()) {
			iSize = new Integer(strViewSize);
			// opts.setFetchSize(iSize);
			// if(strViewIndex != null && !strViewIndex.isEmpty()){
			// opts.setOffset(iSize*iIndex);
			// }
			// opts.setMaxRows(iSize);
		}
		// EntityCondition.makeCondition(lhs, operator, rhs);
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		// create one true condition
		/*
		 * EntityCondition ecTmp1 = EntityCondition.makeCondition("fromDate",
		 * EntityOperator.EQUALS, null); EntityCondition ecTmp2 =
		 * EntityCondition.makeCondition("fromDate", EntityOperator.NOT_EQUAL,
		 * null);
		 * listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toList
		 * (ecTmp1, ecTmp2), EntityJoinOperator.AND));
		 */

		if ((strFilterListFields != null && !strFilterListFields.isEmpty())
				|| strNoConditionsFind.equals("N")) {
			List<EntityCondition> listTmpCondition = new ArrayList<EntityCondition>();
			String tmpFieldName = "";
			if (strFilterListFields == null || strFilterListFields.isEmpty())
				strFilterListFields = strConditionsFind;
			// else strFilterListFields = strConditionsFind +
			// strFilterListFields;
			String[] arrField = strFilterListFields.split("\\|OLBIUS\\|");
			String tmpGO = "0";
			String tmpG1 = "0";
			// List<String> tmpList = new ArrayList<String>();
			for (int i = 1; i < arrField.length; i++) {
				String[] arrTmp = arrField[i].split("\\|SUIBLO\\|");
				SqlOperator so = SqlOperator.valueOf(arrTmp[2]); // Filter
																	// condition
				if(so.toString().equals("EMPTY")){	
					continue;
				}
				EntityComparisonOperator<?, ?> fieldOp = null;
				tmpGO = arrTmp[3]; // Filter Operator
				Object fieldValue = arrTmp[1].toString(); // Filter value
				String fieldName = arrTmp[0]; // Filter name
				String orgFieldName = fieldName;
				if(parameters.containsKey(fieldName)){
					String[] existsArr = parameters.get(fieldName);
					List<String> tempList = FastList.newInstance();
					tempList.addAll(Arrays.asList(existsArr));
					tempList.add((String)fieldValue);
					String[] tempArr = new String[tempList.size()]; 
					tempList.toArray(tempArr);
					parameters.put(fieldName, tempArr);
				}else{
					parameters.put(fieldName, new String[]{fieldValue.toString()});
				}
				boolean isUpper=false;
				switch (so) {
				case CONTAINS: {
					fieldOp = EntityOperator.LIKE;
/*					fieldValue = "%" + fieldValue + "%";*/
					fieldValue = EntityFunction.UPPER_FIELD("'%" + fieldValue + "%'");
					isUpper=true;
					break;
				}
				case DOES_NOT_CONTAIN: {
					fieldOp = EntityOperator.NOT_LIKE;
/*					fieldValue = "%" + fieldValue + "%";*/
					fieldValue = EntityFunction.UPPER_FIELD("'%" + fieldValue + "%'");
					isUpper=true;
					break;
				}
				case EQUAL: {
					fieldOp = EntityOperator.EQUALS;
					break;
				}
				case NOT_EQUAL: {
					fieldOp = EntityOperator.NOT_EQUAL;
					break;
				}
				case GREATER_THAN: {
					fieldOp = EntityOperator.GREATER_THAN;
					break;
				}
				case LESS_THAN: {
					fieldOp = EntityOperator.LESS_THAN;
					break;
				}
				case GREATER_THAN_OR_EQUAL: {
					fieldOp = EntityOperator.GREATER_THAN_EQUAL_TO;
					break;
				}
				case LESS_THAN_OR_EQUAL: {
					fieldOp = EntityOperator.LESS_THAN_EQUAL_TO;
					break;
				}
				case STARTS_WITH: {
					fieldOp = EntityOperator.LIKE;
/*					fieldValue = fieldValue + "%";*/
					fieldValue = EntityFunction.UPPER_FIELD("'" + fieldValue + "%'");
					isUpper=true;
					break;
				}
				case ENDS_WITH: {
					fieldOp = EntityOperator.LIKE;
/*					fieldValue = "%" + fieldValue;*/
					fieldValue = EntityFunction.UPPER_FIELD("'%" + fieldValue + "'");
					isUpper=true;
					break;
				}
				case NULL: {
					fieldOp = EntityOperator.EQUALS;
					fieldValue = null;
					break;
				}
				case NOT_NULL: {
					fieldOp = EntityOperator.NOT_EQUAL;
					fieldValue = null;
					break;
				}
				case EMPTY: {
					fieldOp = EntityOperator.EQUALS;
					fieldValue = null;
					break;
				}
				case NOT_EMPTY: {
					fieldOp = EntityOperator.NOT_EQUAL;
					fieldValue = null;
					break;
				}
				default:
					break;
				}
				if (fieldName.contains("(BigDecimal)")) {
					fieldName = fieldName.substring(0, fieldName.indexOf("("));
					DecimalFormatSymbols symbols = new DecimalFormatSymbols();
					symbols.setGroupingSeparator(',');
					symbols.setDecimalSeparator('.');
					String pattern = "#,##0.0#";
					DecimalFormat decimalFormat = new DecimalFormat(pattern,
							symbols);
					decimalFormat.setParseBigDecimal(true);
					// parse the string
					try {
						fieldValue = (BigDecimal) decimalFormat
								.parse((String) fieldValue);
					} catch (ParseException e) {
						Debug.logError(e, module);
					}
				}
				if(fieldName.contains("Long")){
					fieldName = fieldName.substring(0, fieldName.indexOf("("));
					try {
						if(UtilValidate.isNotEmpty(fieldValue)){
							fieldValue = new java.lang.Long((String) fieldValue);
						}	
					} catch (Exception e) {
						Debug.logError(e, module);
					}	
				}
				
				if(fieldName.contains("Double")){
					fieldName = fieldName.substring(0, fieldName.indexOf("("));
					try {
						if(UtilValidate.isNotEmpty(fieldValue)){
							fieldValue = new java.lang.Double((String) fieldValue);	
						}
					} catch (Exception e) {
						Debug.logError(e, module);
					}	
				}
				
				if (fieldName.contains("(Date)")) {
					fieldName = fieldName.substring(0, fieldName.indexOf("("));
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					fieldValue = new java.sql.Date(
							((java.util.Date) sdf.parse((String) fieldValue))
									.getTime());
				}
				if (fieldName.contains("(Timestamp)")) {
					SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
					if (fieldName.contains("[")) {
						dateFormat = new SimpleDateFormat(fieldName.substring(fieldName.indexOf("[") + 1, fieldName.indexOf("]")));
					}else{
						// define list of patterns
						List<DateRegexPattern> listPatterns = new ArrayList<DateRegexPattern>();
						listPatterns.add(new DateRegexPattern("HH:mm:ss dd-MM-yyyy", "^[0-9]{2}:[0-9]{2}:[0-9]{2}\\s[0-9]{2}-[0-9]{2}-[0-9]{4}$")); //HH:mm:ss dd-MM-yyyy
						listPatterns.add(new DateRegexPattern("HH:mm:ss dd/MM/yyyy", "^[0-9]{2}:[0-9]{2}:[0-9]{2}\\s[0-9]{2}/[0-9]{2}/[0-9]{4}$")); //HH:mm:ss dd/MM/yyyy
						// Iterate to get missing pattern
						for(DateRegexPattern tmpDRP:listPatterns){
							Pattern r = Pattern.compile(tmpDRP.getRegexPattern());
							Matcher m = r.matcher(fieldValue.toString());
							if (m.find()) {
								dateFormat = new SimpleDateFormat(tmpDRP.getDatePattern());
								break;
							}
						}
					}
					try {
						Date parsedDate = new java.sql.Date(dateFormat.parse((String) fieldValue).getTime());
						fieldValue = new java.sql.Timestamp(parsedDate.getTime());
					} catch (ParseException e) {
						Debug.logError(e, module);
						List<Map<String, Object>> res = FastList.newInstance();
						result.put("results", res);
						result.put("TotalRows", "0");
						return result;
					}
					fieldName = fieldName.substring(0, fieldName.indexOf("("));
				}
				if (listTmpCondition.isEmpty()) {
					if (isUpper && !orgFieldName.contains("(BigDecimal)") && !orgFieldName.contains("Long") && !orgFieldName.contains("Double") && !orgFieldName.contains("(Date)") && !orgFieldName.contains("(Timestamp)"))
						listTmpCondition.add(EntityCondition.makeCondition(
								EntityFunction.UPPER_FIELD(fieldName), fieldOp, fieldValue));
					else listTmpCondition.add(EntityCondition.makeCondition(
							fieldName, fieldOp, fieldValue));
					tmpFieldName = fieldName;
				} else {
					// Check for the same field listFieldName
					if (tmpFieldName.equals(fieldName)) { // same field
						if (isUpper && !orgFieldName.contains("(BigDecimal)") && !orgFieldName.contains("Long") && !orgFieldName.contains("Double") && !orgFieldName.contains("(Date)") && !orgFieldName.contains("(Timestamp)"))
							listTmpCondition.add(EntityCondition.makeCondition(
									EntityFunction.UPPER_FIELD(fieldName), fieldOp, fieldValue));
						else
							listTmpCondition.add(EntityCondition.makeCondition(
									fieldName, fieldOp, fieldValue));
						tmpG1 = tmpGO;
					} else {
						// listAllConditions.addAll(listTmpCondition); // add
						// all
						if (tmpG1.equals("1")) {
							listAllConditions.add(EntityCondition
									.makeCondition(listTmpCondition,
											EntityJoinOperator.OR));
						} else {
							listAllConditions.add(EntityCondition
									.makeCondition(listTmpCondition,
											EntityJoinOperator.AND));
						}
						tmpFieldName = fieldName;
						listTmpCondition = new ArrayList<EntityCondition>(); // reset
																				// list
						if (isUpper && !orgFieldName.contains("(BigDecimal)") && !orgFieldName.contains("Long") && !orgFieldName.contains("Double") && !orgFieldName.contains("(Date)") && !orgFieldName.contains("(Timestamp)"))
							listTmpCondition.add(EntityCondition.makeCondition(
									EntityFunction.UPPER_FIELD(fieldName), fieldOp, fieldValue));
						else
							listTmpCondition.add(EntityCondition.makeCondition(
									fieldName, fieldOp, fieldValue));
					}
				}
			}
			// add last
			if (listTmpCondition.size() > 1) {
				if (tmpG1.equals("1")) {
					listAllConditions.add(EntityCondition.makeCondition(
							listTmpCondition, EntityJoinOperator.OR));
				} else {
					listAllConditions.add(EntityCondition.makeCondition(
							listTmpCondition, EntityJoinOperator.AND));
				}
			} else {
				listAllConditions.addAll(listTmpCondition);
			}
		}
		try {
			// listAllConditions
			Map<String, Object> mapTmp = new HashMap<String, Object>();
			mapTmp.put("listAllConditions", listAllConditions);
			mapTmp.put("listSortFields", listSortFields);
			mapTmp.put("opts", opts);
			mapTmp.put("userLogin", context.get("userLogin"));
			mapTmp.put("parameters", parameters);
			mapTmp.put("locale", context.get("locale"));
			if (context.containsKey("request")) {
				mapTmp.put("request", context.get("request"));
			}
			mapTmp.put("timeZone", context.get("timeZone"));
			Map<String, Object> resultMap = dispatcher.runSync(strServiceName,
					mapTmp);
			Object resultList = resultMap.get("listIterator");
			String totalRows = (String) resultMap.get("TotalRows");
			if (resultList == null) {
				result.put("results", new ArrayList<String>());
				result.put("TotalRows", "0");
			} else {
				if (resultList instanceof EntityListIterator) {
					EntityListIterator tmpList = (EntityListIterator) resultList;
					List<GenericValue> listGenericValue = null;
					if (UtilValidate.isEmpty(totalRows)) {
						if (iSize != 0) {
							if (iIndex == 0) {
								listGenericValue = tmpList.getPartialList(0, iSize);
							} else {
								listGenericValue = tmpList.getPartialList(iIndex
										* iSize + 1, iSize);
							}
						} else {
							listGenericValue = tmpList.getCompleteList();
						}
					} else {
						listGenericValue = tmpList.getCompleteList();
					}
					// get Translated value
					Locale locale = (Locale) context.get("locale");
					if(!listGenericValue.isEmpty()){
					    Collection<String> listKeys = listGenericValue.get(0).getAllKeys();
    					Iterator itr = listKeys.iterator();
    					for(int k = 0; k < listGenericValue.size(); k++){
						String modelEntity = listGenericValue.get(k).getModelEntity().toString();
						if (UtilValidate.isEmpty(listGenericValue.get(k).getModelEntity().getDefaultResourceName()) || modelEntity.contains("ModelViewEntity")) {
	    					    while(itr.hasNext()){
	    					        String strTmp = itr.next().toString();
	    					        if(listGenericValue.get(k).get(strTmp) != null){
	    					        	listGenericValue.get(k).put(strTmp, listGenericValue.get(k).get(strTmp));
	    					        }
	    					    }
    						} else {
    							while(itr.hasNext()){
	    					        String strTmp = itr.next().toString();
	    					        if(listGenericValue.get(k).get(strTmp) != null){
	    					        	listGenericValue.get(k).put(strTmp, listGenericValue.get(k).get(strTmp, locale));
	    					        }
	    					    }
    						}
    					}
					}
					/*
					 * update locale: for (int i=0; i < listGeneralValue.size();
					 * i++ ) + { + nextValue = listGeneralValue.get(i); +
					 * String[] arrDictionaryColumns =
					 * strDictionaryColumns.split(";"); + for (int iDic = 0;
					 * iDic < arrDictionaryColumns.length; iDic++) + { +
					 * nextValue.set(arrDictionaryColumns[iDic],
					 * nextValue.get(arrDictionaryColumns[iDic], locale)); + } +
					 * nextValue.set("total", invoiceTotal); +
					 * listGeneralValue.set(i, nextValue); + }
					 */
					if (!"".equals(strDictionaryColumns)) {
						String[] arrDic = strDictionaryColumns.split(";", -1);
						for (int i = 0; i < listGenericValue.size(); i++) {
							GenericValue nextValue = listGenericValue.get(i);
							for (int iDic = 0; iDic < arrDic.length; iDic++) {
								nextValue.set(arrDic[iDic],
										nextValue.get(arrDic[iDic], locale));
							}
							listGenericValue.set(i, nextValue);
						}
					}
					result.put("results", listGenericValue);
					// get total record
					// tmpList.last();
					if (UtilValidate.isEmpty(totalRows)) {
						//result.put("TotalRows", String.valueOf(tmpList.getResultsTotalSize()));
						result.put("TotalRows", String.valueOf(tmpList.getResultsSizeAfterPartialList()));
					} else {
						result.put("TotalRows", totalRows);
					}
					//
					tmpList.close();
				} else if (resultList instanceof java.util.LinkedList) {
					java.util.LinkedList tmpList = (java.util.LinkedList) resultList;
					if (tmpList.size() > 0) {
						List<Object> list = null;
						if (iSize != 0 && (tmpList.size() > iSize)) {
							if (iIndex == 0) {
								list = tmpList.subList(0, iSize);
							} else {
								if(tmpList.size() > ((iIndex + 1) *iSize)){
									list = tmpList.subList(iIndex * iSize,
											((iIndex + 1) *iSize));
								}else{
									list = tmpList.subList(iIndex * iSize,
											tmpList.size());
								}
							}
						} else {
							list = tmpList;
						}
						result.put("results", list);
						if(totalRows != null){
							result.put("TotalRows", totalRows);
						}else{
							result.put("TotalRows", String.valueOf(tmpList.size()));
						}
					} else {
						result.put("results", tmpList);
						result.put("TotalRows", "0");
					}
				} else { // if
					List<Object> list = (List<Object>) resultList;
					result.put("TotalRows", String.valueOf(list.size()));
					if (iSize != 0 && (list.size() > iSize)) {
						if (iIndex == 0) {
							list = list.subList(0, iSize);
						} else {
							int toIndex = iIndex * iSize + iSize;
							if (list.size() > toIndex) {
								list = list.subList(iIndex * iSize, iIndex
										* iSize + iSize);
							} else {
								list = list.subList(iIndex * iSize, list.size());
							}
							result.put("results", list);
						}
					}

					if (totalRows != null) {
						result.put("TotalRows", totalRows);
					}
					result.put("results", list);
				}
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			List<Map<String, Object>> res = FastList.newInstance();
			result.put("results", res);
			result.put("TotalRows", "0");
			
		}
		return result; 
	}

	// TODO Consider to merge update delete and create to one method
	@SuppressWarnings("unchecked")
	public static Map<String, Object> gridGeneralServicerUpdate(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws Exception {
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String strServiceName = (String) parameters.get("sname")[0];
		Integer iRL = Integer.valueOf((String) parameters.get("rl")[0]);
		Map<String, Object> inputMap = new HashMap<String, Object>();
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		for (int j = 0; j < iRL; j++) {
			String strColumnList = (String) parameters.get("columnList" + j)[0];
			String strColumnValues = (String) parameters
					.get("columnValues" + j)[0];

			String[] arrColumn = strColumnList.split(";");
			String[] arrValues = strColumnValues.split("#;",-1);

			for (int i = 0; i < arrColumn.length; i++) {
				if ("undefined".equals(arrValues[i])
						|| "null".equals(arrValues[i])) {
					continue;
				}
				if (arrColumn[i].contains("(")) {
					String strTmpKey = arrColumn[i].substring(0,
							arrColumn[i].indexOf("("));
					String strTmpValue = arrColumn[i].substring(
							arrColumn[i].indexOf("(") + 1,
							arrColumn[i].length() - 1);
					inputMap.put(strTmpKey, convert(strTmpValue, arrValues[i]));
				} else {
					inputMap.put(arrColumn[i], arrValues[i]);
				}
			}
			listData.add(inputMap);
			inputMap = new HashMap<String, Object>();
		}
		if (parameters.get("updatelist") == null) {
			for (String key : listData.get(0).keySet()) {
				inputMap.put(key, listData.get(0).get(key));
			}
		} else {
			inputMap.put((String) parameters.get("ulistname")[0], listData);
		}
		if(parameters.get("passParameters") != null && "true".equals(parameters.get("passParameters")[0])){
			inputMap.put("parameters", parameters);
		}
		
		inputMap.put("userLogin", context.get("userLogin"));
		inputMap.put("locale", locale);
		inputMap.put("timeZone", timeZone);
		Map<String, Object> results = new HashMap<String, Object>();
		try {
			results = dispatcher.runSync(strServiceName, inputMap);
		} catch (Exception e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		Map<String, Object> returnM = ServiceUtil.returnSuccess();
		if(ServiceUtil.isSuccess(results)){
			returnM = ServiceUtil.returnSuccess();
			returnM.put("results", results);
		}else{
			returnM = results;
		}
		return returnM;
	}
	// TODO Consider to merge update delete and create to one method
		@SuppressWarnings("unchecked")
		public static Map<String, Object> gridGeneralServicerUpdateLocal(
				DispatchContext ctx, Map<String, ? extends Object> context)
				throws Exception {
			Map<String, String[]> parameters = (Map<String, String[]>) context
					.get("parameters");
			LocalDispatcher dispatcher = ctx.getDispatcher();
			String strServiceName = (String) parameters.get("sname")[0];
			String dataString = (String) parameters.get("data")[0];
			JSONArray data = JSONArray.fromObject(dataString);
			Map<String, Object> inputMap = new HashMap<String, Object>();
			Map<String, Object> results = new HashMap<String, Object>();
			try {
				for (int j = 0; j < data.size(); j++) {
					JSONObject o = data.getJSONObject(j);				
					Iterator<?> i = o.keys();
					while(i.hasNext()){
						String key = (String) i.next();
						JSONObject objkey = o.getJSONObject(key);
						String value = objkey.getString("value");
						if(objkey.has("type")){
							String type = objkey.getString("type");
							inputMap.put(key, convert(type, value));
						}else{
							inputMap.put(key, value);
						}
					}
					inputMap.put("userLogin", (GenericValue) context.get("userLogin"));
					results = dispatcher.runSync(strServiceName, inputMap);
					inputMap = new HashMap<String, Object>();
				}
			} catch (Exception e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
			Map<String, Object> returnM = ServiceUtil.returnSuccess();
			if(ServiceUtil.isSuccess(results)){
				returnM = ServiceUtil.returnSuccess();
				returnM.put("results", results);
			}else{
				returnM = results;
			}
			return returnM;
		}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> gridGeneralServicerCreate(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws Exception {
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String strServiceName = (String) parameters.get("sname")[0];
		ModelService createAccountService = ctx.getModelService(strServiceName);
        Map<String, Object> inputMap = createAccountService.makeValid(context, ModelService.IN_PARAM);
		if (parameters.get("columnList") != null) {
			String strColumnList = (String) parameters.get("columnList")[0];
			String strColumnValues = (String) parameters.get("columnValues")[0];

			String[] arrColumn = strColumnList.split(";");
			String[] arrValues = strColumnValues.split("#;", -1);

			for (int i = 0; i < arrColumn.length; i++) {
				if ("undefined".equals(arrValues[i])
						|| "null".equals(arrValues[i])) {
					continue;
				}
				if (arrColumn[i].contains("(")) {
					String strTmpKey = arrColumn[i].substring(0,
							arrColumn[i].indexOf("("));
					String strTmpValue = arrColumn[i].substring(
							arrColumn[i].indexOf("(") + 1,
							arrColumn[i].length() - 1);
					if (arrValues[i].isEmpty()) {
						continue;
					} else {
						inputMap.put(strTmpKey, convert(strTmpValue, arrValues[i]));
					}
				} else {
					inputMap.put(arrColumn[i], arrValues[i]);
				}
			}
		} else {
			String strPrimaryColumn = (String) parameters.get("primaryColumn")[0];
			String strPrimaryValue = (String) parameters.get("primaryKey")[0];
			inputMap.put(strPrimaryColumn, strPrimaryValue);
		}
		inputMap.put("timeZone", context.get("timeZone"));
		inputMap.put("userLogin", context.get("userLogin"));
		/*Map<String, Object> returnM = new HashMap<String, Object>();*/
		Map<String, Object> tmpHM = new HashMap<String, Object>();
		Map<String, Object> results = new HashMap<String, Object>();
		try {
			  results = dispatcher.runSync(strServiceName,
					inputMap);
			/*
			 * if(tmpHM.containsKey("responseMessage")){
			 * tmpHM.remove("responseMessage"); }
			 */
			/*returnM.put("results", tmpHM);*/
		} catch (Exception e) {
			Debug.logError(e, module);
			String msg = e.getLocalizedMessage();
			return ServiceUtil.returnError(msg);
		}
		if(ServiceUtil.isSuccess(results)){
			tmpHM = ServiceUtil.returnSuccess();
			tmpHM.put("results", results);
		}else{
			tmpHM = results;
		}
		return tmpHM;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> gridGeneralServicerDelete(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws ParseException {
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String strServiceName = (String) parameters.get("sname")[0];
		Map<String, Object> inputMap = new HashMap<String, Object>();
		if (parameters.get("columnList") != null) {
			String strColumnList = (String) parameters.get("columnList")[0];
			String strColumnValues = (String) parameters.get("columnValues")[0];

			String[] arrColumn = strColumnList.split(";");
			String[] arrValues = strColumnValues.split("#;");

			for (int i = 0; i < arrColumn.length; i++) {
				if ("undefined".equals(arrValues[i])
						|| "null".equals(arrValues[i])) {
					continue;
				}
				if (arrColumn[i].contains("(")) {
					String strTmpKey = arrColumn[i].substring(0,
							arrColumn[i].indexOf("("));
					String strTmpValue = arrColumn[i].substring(
							arrColumn[i].indexOf("(") + 1,
							arrColumn[i].length() - 1);
					if (arrValues[i].isEmpty()) {
						continue;
					} else {
						inputMap.put(strTmpKey,
								convert(strTmpValue, arrValues[i]));
					}
				} else {
					inputMap.put(arrColumn[i], arrValues[i]);
				}
			}
		} else {
			String strPrimaryColumn = (String) parameters.get("primaryColumn")[0];
			String strPrimaryValue = (String) parameters.get("primaryKey")[0];
			inputMap.put(strPrimaryColumn, strPrimaryValue);
		}
		inputMap.put("userLogin", context.get("userLogin"));
		Map<String, Object> results = new HashMap<String, Object>();
		try {
			results = dispatcher.runSync(strServiceName,
					inputMap);
			if (results.containsKey("responseMessage")) {
				results.remove("responseMessage");
			}
			/*returnM.put("results", tmpHM);*/
		} catch (Exception e) {
			Debug.logError(e, module);
			/*Map<String, Object> tmpHM = new HashMap<String, Object>();*/
			Map<String, Object> tmpHM2 = ServiceUtil.returnError(e.getMessage());
			/*tmpHM.put("results", tmpHM2);*/
			return tmpHM2;
		}
		Map<String, Object> tmpHM = ServiceUtil.returnSuccess();
		if(ServiceUtil.isSuccess(results)){
			tmpHM = ServiceUtil.returnSuccess();
			tmpHM.put("results", results);
		}else{
			tmpHM = results;
		}
		return tmpHM;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> gridGeneralServicerDeleteLocal(
			DispatchContext ctx, Map<String, ? extends Object> context)
			throws ParseException {
		Map<String, String[]> parameters = (Map<String, String[]>) context
				.get("parameters");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String strServiceName = (String) parameters.get("sname")[0];
		String dataString = (String) parameters.get("data")[0];
		JSONArray data = JSONArray.fromObject(dataString);
		Map<String, Object> inputMap = new HashMap<String, Object>();
		Map<String, Object> results = new HashMap<String, Object>();
		try {
			for (int j = 0; j < data.size(); j++) {
				JSONObject o = data.getJSONObject(j);				
				Iterator<?> i = o.keys();
				while(i.hasNext()){
					String key = (String) i.next();
					JSONObject objkey = o.getJSONObject(key);
					String value = objkey.getString("value");
					if(objkey.has("type")){
						String type = objkey.getString("type");
						if (type.contains("(")) {
							String strTmpValue = type.substring(
									type.indexOf("(") + 1,
									type.length() - 1);
							if (type.isEmpty()) {
								continue;
							} else {
								inputMap.put(key,
										convert(strTmpValue, value));
							}
						} else {
							inputMap.put(key, value);
						}
					}else{
						inputMap.put(key, value);
					}
				}
				inputMap.put("userLogin", (GenericValue) context.get("userLogin"));
				results = dispatcher.runSync(strServiceName,
						inputMap);
				if (results.containsKey("responseMessage")) {
					results.remove("responseMessage");
				}
				/*returnM.put("results", tmpHM);*/
				inputMap = new HashMap<String, Object>();
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			/*Map<String, Object> tmpHM = new HashMap<String, Object>();*/
			Map<String, Object> tmpHM2 = ServiceUtil.returnError(e.getMessage());
			/*tmpHM.put("results", tmpHM2);*/
			return tmpHM2;
		}
		Map<String, Object> tmpHM = ServiceUtil.returnSuccess();
		if(ServiceUtil.isSuccess(results)){
			tmpHM = ServiceUtil.returnSuccess();
			tmpHM.put("results", results);
		}else{
			tmpHM = results;
		}
		return tmpHM;
	}

	public static Object convert(String inputTarget, String s)
			throws ParseException {
		Class<?> target = null;
		try {
			target = Class.forName(inputTarget);
		} catch (ClassNotFoundException e) {
			Debug.logError(e, module);
		}
		if (target == Object.class || target == String.class || s == null) {
			return s;
		}
		if (target == Character.class || target == char.class) {
			return s.charAt(0);
		}
		if (target == Byte.class || target == byte.class) {
			return Byte.parseByte(s);
		}
		if(target == ByteBuffer.class){
			try {
				Charset charset = Charset.forName("UTF-8");
				 return ByteBuffer.wrap(s.getBytes(charset));
			} catch (Exception e) {
				Debug.log(e,module);
			}
		}
		if (target == Short.class || target == short.class) {
			return Short.parseShort(s);
		}
		if (target == Integer.class || target == int.class) {
			return Integer.parseInt(s);
		}
		if (target == Long.class || target == long.class) {
			return Long.parseLong(s);
		}
		if (target == Float.class || target == float.class) {
			return Float.parseFloat(s);
		}
		if (target == Double.class || target == double.class) {
			return Double.parseDouble(s);
		}
		if (target == java.sql.Date.class) {
			if("".equals(s) || s == null){
				return null;
			}
			if (s.contains("(")) {
				s = s.substring(s.indexOf("(") + 1, s.indexOf(")") - 3);
			}
			java.util.Date date = new java.util.Date(Long.valueOf(s));
			return new java.sql.Date(date.getTime());
		}
		if (target == java.sql.Time.class) {
			if("".equals(s) || s == null){
				return null;
			}
			if (s.contains("(")) {
				s = s.substring(s.indexOf("(") + 1, s.indexOf(")") - 3);
			}
			return new java.sql.Time(Long.valueOf(s));
		}
		if (target == java.sql.Timestamp.class) {
			if("".equals(s) || s == null){
				return null;
			}
			if (s.contains("(")) {
				s = s.substring(s.indexOf("(") + 1, s.indexOf(")") - 3);
			}
			Long tm = Long.valueOf(s);
			java.util.Date date = new java.util.Date(tm);
			return new java.sql.Timestamp(
					(new java.sql.Date(date.getTime())).getTime());
		}
		if (target == BigDecimal.class) {
			if("".equals(s)){
				return null;
			}
			return new BigDecimal(s.replaceAll(",", ""));
		}
		if (target == java.lang.Boolean.class) {
			return java.lang.Boolean.parseBoolean(s);
		}
		try {
			if (target == Class.forName("java.util.List")) {
				s = s.replaceAll("\\[|\\]", "");
				if(UtilValidate.isEmpty(s)){
					//If empty
					return new ArrayList<Map<String, String>>();
				}
				int tm = s.indexOf("},");
				if(tm != -1){
					String[] items = s.split("\\{");
					List<Map<String, String>> tmpList = new ArrayList<Map<String, String>>();
					for (int i = 0; i < items.length; i++){
						String item = items[i];
						if (!"".equals(item)){
							item = item.replaceAll("\\},", "");
							item = item.replaceAll("\\}", "");
							String[] tmpArr = item.split(",\"");
							Map<String, String> tmpMap = new HashMap<String, String>();
							for(int j = 0; j < tmpArr.length; j++){
								String[] arrKeyValue = tmpArr[j].replaceAll("\"", "").split(":"); 
								if(arrKeyValue.length == 1) {
									tmpMap.put(arrKeyValue[0], "");
								}else {
									tmpMap.put(arrKeyValue[0], arrKeyValue[1]);
								}
							}
							tmpList.add(tmpMap);
						}
					}
					return tmpList;
				}else{
					s = s.replaceAll("\\[|\\]", "");
					s = s.replaceAll("\\{|\\}", "");
					String[] items = s.split(",");
					tm = s.indexOf(":");
					if(tm != -1){
						List<Map<String, String>> tmpList = new ArrayList<Map<String, String>>();
						Map<String, String> tmpMap = new HashMap<String, String>();
						for (int i = 0; i < items.length; i++){
							String[] tmpKV = items[i].replaceAll("\"", "").split(":");
							if(tmpKV.length == 1) {
								tmpMap.put(tmpKV[0], "");
							}else {
								tmpMap.put(tmpKV[0], tmpKV[1]);
							}
						}
						tmpList.add(tmpMap);
						return tmpList;
					}else{
						List<String> tmpList = new ArrayList<String>();
						for (int i = 0; i < items.length; i++){
							tmpList.add(items[i]);
						}
						return tmpList;
					}
				}
			}
		} catch (ClassNotFoundException e) {	
			Debug.logError(e, module);
		}
		throw new IllegalArgumentException("There are no defined conversion to convert this type: "
				+ target);
	}
}
class DateRegexPattern{
	String datePattern;
	String regexPattern;
	public DateRegexPattern(String datePattern,String regexPattern){
		this.datePattern = datePattern;
		this.regexPattern = regexPattern;
	}
	public String getDatePattern() {
		return datePattern;
	}
	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
	}
	public String getRegexPattern() {
		return regexPattern;
	}
	public void setRegexPattern(String regexPattern) {
		this.regexPattern = regexPattern;
	}
	
}
enum SqlOperator {
	CONTAINS, DOES_NOT_CONTAIN, NOT_EMPTY, EMPTY, EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL, STARTS_WITH, ENDS_WITH, NULL, NOT_NULL
}
