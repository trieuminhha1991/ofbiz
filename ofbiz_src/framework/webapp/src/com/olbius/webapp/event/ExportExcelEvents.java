package com.olbius.webapp.event;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;

public class ExportExcelEvents {
	public static final String module = ExportExcelEvents.class.getClass().getName();
	public static final String resource = "CommonUiLabels";
	public static final String RESULT_TIMEOUT = "TIMEOUT";
	public static final String RESULT_SUCCESS = "SUCCESS";
	public static final String RESULT_ERROR = "ERROR";
	public static final String FILE_CONTENT_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	public static final String FILE_EXE_TYPE = ".xlsx";
	public static final String EXPORT_TYPE_OLAP = "OLAP";
	public static final String EXPORT_TYPE_ACTIVITY = "ACTIVITY";
	public static final int MAX_TIME_TIME_OUT = 10000;
	
	public static String exportExcel(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		Locale locale = UtilHttp.getLocale(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
		
		List<Object> errMsgList = FastList.newInstance();
		try {
	     	String eventPath = (String) request.getAttribute("eventPath");
	     	
	     	@SuppressWarnings("unchecked")
			Map<String, String[]> parameters = request.getParameterMap();
			List<EntityCondition> listAllConditions = getListAllConditions(parameters);
			List<String> listSortFields = getListSortFields(parameters);
	     	
			Map<String, Object> exportCtx = UtilMisc.toMap("eventPath", eventPath, "parameters", parameters, 
					"listAllConditions", listAllConditions, "listSortFields", listSortFields, "userLogin", userLogin, "locale", locale);
			Map<String, Object> exportResult = dispatcher.runSync("exportExcelBigData", exportCtx);
			if (ServiceUtil.isError(exportResult)) {
				errMsgList.add(ServiceUtil.getErrorMessage(exportResult));
				request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
				return "error";
			}
			
			String resultCode = (String) exportResult.get("resultCode");
			String fileName = (String) exportResult.get("fileName");
			if (resultCode.equals(RESULT_ERROR)) {
				errMsgList.add(ServiceUtil.getErrorMessage(exportResult));
				request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
				return "error";
			} else if (resultCode.equals(RESULT_TIMEOUT)) {
				errMsgList.add(UtilProperties.getMessage(resource, "CommonSystemIsProcessingData", locale));
				request.setAttribute("_EVENT_MESSAGE_LIST_", errMsgList);
				return "success";
			} else {
				// return stream
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				try {
					SXSSFWorkbook wb = (SXSSFWorkbook) exportResult.get("workBook");
					wb.write(baos);
					byte[] bytes = baos.toByteArray();
					response.setHeader("content-disposition", "attachment;filename=" + fileName + FILE_EXE_TYPE);
					response.setContentType(FILE_CONTENT_TYPE);
					response.setContentLength(baos.size()); 
					response.getOutputStream().write(bytes);
				} catch (Exception e) {
					Debug.logError(e, module);
					errMsgList.add(UtilProperties.getMessage(resource, "CommonErrorWhenResponseData", locale));
					request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
					return "error";
				} finally {
					if (baos != null) {
						try {
							baos.close();
						} catch (IOException e) {
							Debug.logError(e, module);
						}
					}
				}
			}
		} catch (Exception e) {
			Debug.logError(e, module);
			errMsgList.add(UtilProperties.getMessage(resource, "CommonErrorWhenProcessing", locale));
			request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
			return "error";
		}
		return "success";
	}
	
	private static List<String> getListSortFields(Map<String, String[]> parameters) {
		String strSortOrder = ((String[]) parameters.get("sortorder") != null) ? ((String) parameters.get("sortorder")[0]) : ("");
		String strSortDataField = ((String[]) parameters.get("sortdatafield") != null) ? ((String) parameters.get("sortdatafield")[0]) : ("");
		String isNullLast = parameters.get("nullLast") != null ? (String)parameters.get("nullLast")[0] : "N";
		
		// Check sortdatafield
		List<String> listSortFields = new ArrayList<String>();
		if (strSortDataField != null && !strSortDataField.isEmpty()) {
			if (!"asc".equals(strSortOrder)) {
				// strSortDataField = "-" + strSortDataField;
				String[] arrSortDataField = strSortDataField.split(";");
				for (int i = 0; i < arrSortDataField.length; i++) {
					arrSortDataField[i] = "-" + arrSortDataField[i];
					if("Y".equals(isNullLast)){
						listSortFields.add(arrSortDataField[i] + " NULLS LAST");
					}else{
						listSortFields.add(arrSortDataField[i]);
					}
				}
			} else {
				String[] arrSortDataField = strSortDataField.split(";");
				for (int i = 0; i < arrSortDataField.length; i++) {
					if("Y".equals(isNullLast)){
						listSortFields.add(arrSortDataField[i] + " NULLS LAST");
					}else{
						listSortFields.add(arrSortDataField[i]);
					}
				}
			}
		}
		return listSortFields;
	}
	
	private static List<EntityCondition> getListAllConditions(Map<String, String[]> parameters) {
		String strFilterListFields = ((String[]) parameters.get("filterListFields") != null) ? ((String) parameters.get("filterListFields")[0]) : ("");
		
		String strNoConditionsFind = parameters.get("noConditionFind") != null ? (String)parameters.get("noConditionFind")[0] : "N";
		String strConditionsFind = parameters.get("conditionsFind") != null ? (String) parameters.get("conditionsFind")[0] : "Y";
		
		List<EntityCondition> listAllConditions = new ArrayList<EntityCondition>();
		
		if ((strFilterListFields != null && !strFilterListFields.isEmpty()) || strNoConditionsFind.equals("N")) {
			List<EntityCondition> listTmpCondition = new ArrayList<EntityCondition>();
			String tmpFieldName = "";
			if (strFilterListFields == null || strFilterListFields.isEmpty()) strFilterListFields = strConditionsFind;
			String[] arrField = strFilterListFields.split("\\|OLBIUS\\|");
			String tmpGO = "0";
			String tmpG1 = "0";
			for (int i = 1; i < arrField.length; i++) {
				String[] arrTmp = arrField[i].split("\\|SUIBLO\\|");
				SqlOperator so = SqlOperator.valueOf(arrTmp[2]); // Filter condition
				if(so.toString().equals("EMPTY")){	
					continue;
				}
				EntityComparisonOperator<?, ?> fieldOp = null;
				tmpGO = arrTmp[3]; // Filter Operator
				Object fieldValue = arrTmp[1].toString(); // Filter value
				String fieldName = arrTmp[0]; // Filter name
				/*if(parameters.containsKey(fieldName)){
					String[] existsArr = parameters.get(fieldName);
					List<String> tempList = FastList.newInstance();
					tempList.addAll(Arrays.asList(existsArr));
					tempList.add((String)fieldValue);
					String[] tempArr = new String[tempList.size()]; 
					tempList.toArray(tempArr);
					parameters.put(fieldName, tempArr);
				}else{
					parameters.put(fieldName, new String[]{fieldValue.toString()});
				}*/
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
					try {
						fieldValue = new java.sql.Date(((java.util.Date) sdf.parse((String) fieldValue)).getTime());
					} catch (ParseException e) {
						Debug.logWarning(e, module);
					}
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
						for(DateRegexPattern tmpDRP : listPatterns){
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
						Debug.logWarning(e, module);
					}
					fieldName = fieldName.substring(0, fieldName.indexOf("("));
				}
				if (listTmpCondition.isEmpty()) {
					listTmpCondition.add(EntityCondition.makeCondition(fieldName, fieldOp, fieldValue));
					tmpFieldName = fieldName;
				} else {
					// Check for the same field listFieldName
					if (tmpFieldName.equals(fieldName)) { // same field
						listTmpCondition.add(EntityCondition.makeCondition(fieldName, fieldOp, fieldValue));
						tmpG1 = tmpGO;
					} else {
						// listAllConditions.addAll(listTmpCondition); // add all
						if (tmpG1.equals("1")) {
							listAllConditions.add(EntityCondition.makeCondition(listTmpCondition, EntityJoinOperator.OR));
						} else {
							listAllConditions.add(EntityCondition.makeCondition(listTmpCondition, EntityJoinOperator.AND));
						}
						tmpFieldName = fieldName;
						listTmpCondition = new ArrayList<EntityCondition>(); // reset list
						listTmpCondition.add(EntityCondition.makeCondition(fieldName, fieldOp, fieldValue));
					}
				}
			}
			// add last
			if (listTmpCondition.size() > 1) {
				if (tmpG1.equals("1")) {
					listAllConditions.add(EntityCondition.makeCondition(listTmpCondition, EntityJoinOperator.OR));
				} else {
					listAllConditions.add(EntityCondition.makeCondition(listTmpCondition, EntityJoinOperator.AND));
				}
			} else {
				listAllConditions.addAll(listTmpCondition);
			}
		}
		return listAllConditions;
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