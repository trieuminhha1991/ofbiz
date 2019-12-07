package com.olbius.sales;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.SalesPartyUtil;
import com.olbius.util.SecurityUtil;

public class SalesEvents {
	
	public static String module = SalesEvents.class.getName();
    public static final String resource = "DelysAdminUiLabels";
    public static final String resource_error = "DelysAdminErrorUiLabels";
    public static final String RESOURCE_PPT_DELYS = "delys.properties";
    
    public static final String MULTI_ROW_DELIMITER = "_";
    public static final String MULTI_ROW_DELIMITER_SEQ = "_r_";
    public static final String MULTI_ROW_DELIMITER_SEQ_COND = "_c_";
    public static final String MULTI_ROW_DELIMITER_SEQ_ACT = "_a_";
    /*private static final String NO_ERROR = "noerror";
    private static final String NON_CRITICAL_ERROR = "noncritical";
    private static final String ERROR = "error";*/
    
    public static List<Map<String, Object>> getListEmployeeDSAHalf(HttpServletRequest request, Delegator delegator) {
        String roleTypeIdDeptSales = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "role.type.id.dept.sales", delegator);
        if (roleTypeIdDeptSales != null) {
        	List<String> roleAllFind = new ArrayList<String>();
        	roleAllFind.add(roleTypeIdDeptSales);
        	String nbdRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "party.role.nbd.delys", delegator);
        	String csmRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "party.role.csm.delys", delegator);
        	String rsmRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "party.role.rsm.delys", delegator);
        	String asmRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "party.role.asm.delys", delegator);
        	String supRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "party.role.sup.delys", delegator);
        	String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "party.role.salesman.delys", delegator);
        	List<String> roleTypeIdSearched = SalesPartyUtil.getListDescendantRoleInclude(nbdRoleId, delegator);
        	if (roleTypeIdSearched != null) roleAllFind.addAll(roleTypeIdSearched);
        	roleTypeIdSearched.clear();
        	roleTypeIdSearched = SalesPartyUtil.getListDescendantRoleInclude(csmRoleId, delegator);
        	if (roleTypeIdSearched != null) roleAllFind.addAll(roleTypeIdSearched);
        	roleTypeIdSearched.clear();
        	roleTypeIdSearched = SalesPartyUtil.getListDescendantRoleInclude(rsmRoleId, delegator);
        	if (roleTypeIdSearched != null) roleAllFind.addAll(roleTypeIdSearched);
        	roleTypeIdSearched.clear();
        	roleTypeIdSearched = SalesPartyUtil.getListDescendantRoleInclude(asmRoleId, delegator);
        	if (roleTypeIdSearched != null) roleAllFind.addAll(roleTypeIdSearched);
        	roleTypeIdSearched.clear();
        	roleTypeIdSearched = SalesPartyUtil.getListDescendantRoleInclude(supRoleId, delegator);
        	if (roleTypeIdSearched != null) roleAllFind.addAll(roleTypeIdSearched);
        	roleTypeIdSearched.clear();
        	roleTypeIdSearched = SalesPartyUtil.getListDescendantRoleInclude(salesmanRoleId, delegator);
        	if (roleTypeIdSearched != null) roleAllFind.addAll(roleTypeIdSearched);
        	
        	List<String> rootIds = SecurityUtil.getPartiesByRoles(roleTypeIdDeptSales, delegator);
        	SalesEmployeeEntity employee = new SalesEmployeeEntity();
        	SalesPartyUtil.buildDataEmployeeTree(delegator, rootIds, employee, roleAllFind);
        	
        	List<Map<String, Object>> returnValue = new ArrayList<Map<String,Object>>();
        	returnValue = SalesPartyUtil.convertEmployeeEntityTreeToListMap(employee, null, 0);
        	return returnValue;
        }
        
        // Determine where to send the browser
        return null;
	}
    
    public static String getListEmployeeDSA(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        
        String roleTypeIdDeptSales = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "role.type.id.dept.sales", delegator);
        if (roleTypeIdDeptSales != null) {
        	List<String> roleAllFind = new ArrayList<String>();
        	roleAllFind.add(roleTypeIdDeptSales);
        	String nbdRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "party.role.nbd.delys", delegator);
        	String csmRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "party.role.csm.delys", delegator);
        	String rsmRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "party.role.rsm.delys", delegator);
        	String asmRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "party.role.asm.delys", delegator);
        	String supRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "party.role.sup.delys", delegator);
        	String salesmanRoleId = EntityUtilProperties.getPropertyValue(RESOURCE_PPT_DELYS, "party.role.salesman.delys", delegator);
        	List<String> roleTypeIdSearched = SalesPartyUtil.getListDescendantRoleInclude(nbdRoleId, delegator);
        	if (roleTypeIdSearched != null) roleAllFind.addAll(roleTypeIdSearched);
        	roleTypeIdSearched.clear();
        	roleTypeIdSearched = SalesPartyUtil.getListDescendantRoleInclude(csmRoleId, delegator);
        	if (roleTypeIdSearched != null) roleAllFind.addAll(roleTypeIdSearched);
        	roleTypeIdSearched.clear();
        	roleTypeIdSearched = SalesPartyUtil.getListDescendantRoleInclude(rsmRoleId, delegator);
        	if (roleTypeIdSearched != null) roleAllFind.addAll(roleTypeIdSearched);
        	roleTypeIdSearched.clear();
        	roleTypeIdSearched = SalesPartyUtil.getListDescendantRoleInclude(asmRoleId, delegator);
        	if (roleTypeIdSearched != null) roleAllFind.addAll(roleTypeIdSearched);
        	roleTypeIdSearched.clear();
        	roleTypeIdSearched = SalesPartyUtil.getListDescendantRoleInclude(supRoleId, delegator);
        	if (roleTypeIdSearched != null) roleAllFind.addAll(roleTypeIdSearched);
        	roleTypeIdSearched.clear();
        	roleTypeIdSearched = SalesPartyUtil.getListDescendantRoleInclude(salesmanRoleId, delegator);
        	if (roleTypeIdSearched != null) roleAllFind.addAll(roleTypeIdSearched);
        	
        	List<String> rootIds = SecurityUtil.getPartiesByRoles(roleTypeIdDeptSales, delegator);
        	SalesEmployeeEntity employee = new SalesEmployeeEntity();
        	SalesPartyUtil.buildDataEmployeeTree(delegator, rootIds, employee, roleAllFind);
        	
        	List<Map<String, Object>> returnValue = new ArrayList<Map<String,Object>>();
        	returnValue = SalesPartyUtil.convertEmployeeEntityTreeToListMap(employee, null, 0);
        	toJsonObjectList(returnValue, response);
        }
        
        // Determine where to send the browser
        return "success";
	}
    public static void toJsonObjectList(List<Map<String, Object>> attrList, HttpServletResponse response){
        //String jsonStr = "[";
        JSONArray jsonarr= new JSONArray();
        JSONObject object= new JSONObject();
        
        JsonConfig config = new JsonConfig();
    	config.registerJsonValueProcessor(java.sql.Date.class, new JsonValueProcessor() {
    	    @Override
    	    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
    	    	Date[] dates = (Date[])value;
    	        Long[] result = new Long[dates.length];
    	        for (int index = 0; index < dates.length; index++) {
    	            result[index] = dates[index].getTime();
    	        }
    	        return result;
    	    }
    	    @Override
    	    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
    	    	Date date = (Date)value;
    	        return date.getTime();
    	    }
    	});
    	
        for (Object attrMap : attrList) {
            JSONObject json = JSONObject.fromObject(attrMap, config);
            //jsonStr = jsonStr + json.toString() + ',';
            jsonarr.add(json);
        }
        String js= jsonarr.toString();
        object.put("result", jsonarr);
        //jsonStr = jsonStr + "{ } ]";
        if (UtilValidate.isEmpty(js)) {
            Debug.logError("JSON Object was empty; fatal error!",module);
        }
        // set the X-JSON content type
        response.setContentType("application/json");
        // jsonStr.length is not reliable for unicode characters
        try {
            response.setContentLength(js.getBytes("UTF8").length);
        } catch (UnsupportedEncodingException e) {
            Debug.logError("Problems with Json encoding",module);
        }
        // return the JSON String
        Writer out;
        try {
            out = response.getWriter();
            out.write(js);
            out.flush();
        } catch (IOException e) {
            Debug.logError("Unable to get response writer",module);
        }
    }
    /*private static void writeJSONtoResponse(JSON json, HttpServletResponse response) {
        String jsonStr = json.toString();
        if (jsonStr == null) {
            Debug.logError("JSON Object was empty; fatal error!", module);
            return;
        }

        // set the X-JSON content type
        response.setContentType("application/x-json");
        // jsonStr.length is not reliable for unicode characters
        try {
            response.setContentLength(jsonStr.getBytes("UTF8").length);
        } catch (UnsupportedEncodingException e) {
            Debug.logError("Problems with Json encoding: " + e, module);
        }

        // return the JSON String
        Writer out;
        try {
            out = response.getWriter();
            out.write(jsonStr);
            out.flush();
        } catch (IOException e) {
            Debug.logError(e, module);
        }
    }
    
    //@SuppressWarnings("unchecked")
	public static String jqxTreeEvent(HttpServletRequest request, HttpServletResponse response) {
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        TimeZone timeZone = (TimeZone) request.getSession().getAttribute("timeZone");
        String serviceName = request.getParameter("sname");
        //Map<String, String[]> params = request.getParameterMap();
        Locale locale = UtilHttp.getLocale(request);
        Map<String,Object> context = new HashMap<String,Object>();
        //context.put("parameters", params);
        context.put("userLogin", userLogin);
        context.put("timeZone", timeZone);
        context.put("locale", locale);
        if(params.containsKey("hasrequest")){
        	context.put("request", request); 
        }
        
        try {
	        Map<String,Object> results = dispatcher.runSync(serviceName, context);
	        JsonConfig config = new JsonConfig();
        	config.registerJsonValueProcessor(java.sql.Date.class, new JsonValueProcessor() {
        	    @Override
        	    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        	    	Date[] dates = (Date[])value;
        	        Long[] result = new Long[dates.length];
        	        for (int index = 0; index < dates.length; index++) {
        	            result[index] = dates[index].getTime();
        	        }
        	        return result;
        	    }
        	    @Override
        	    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        	    	Date date = (Date)value;
        	        return date.getTime();
        	    }
        	});
        	
        	config.registerJsonValueProcessor(java.sql.Timestamp.class, new JsonValueProcessor() {
        		@Override
        		public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        			Timestamp[] datetimes = (Timestamp[])value;
        			Long[] result = new Long[datetimes.length];
        			for (int index = 0; index < datetimes.length; index++) {
        				result[index] = datetimes[index].getTime();
        			}
        			return result;
        		}
        		@Override
        		public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        			Timestamp datetime = (Timestamp)value;
        			return datetime.getTime();
        		}
        	});
        	config.registerJsonValueProcessor(java.sql.Time.class, new JsonValueProcessor() {
        	    @Override
        	    public Object processArrayValue(Object value, JsonConfig jsonConfig) {
        	    	Time[] datetimes = (Time[])value;
        	        Long[] result = new Long[datetimes.length];
        	        for (int index = 0; index < datetimes.length; index++) {
        	            result[index] = datetimes[index].getTime();
        	        }
        	        return result;
        	    }
        	    @Override
        	    public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
        	    	Time time = (Time)value;
        	        return time.getTime();
        	    }
        	});
	        JSONObject json = JSONObject.fromObject(results, config);
	        writeJSONtoResponse(json, response);
		} catch (Exception e) {
		    e.printStackTrace(); // TODO remove this line when go to production mode.
			Debug.logError("Problems with jqxEventProcessor: " + e.toString(), module);
			Map<String, Object> mapError = new HashMap<String, Object>();
			mapError.put("responseMessage", "error");
			mapError.put("errorMessage", e.toString());
			JSONObject json = JSONObject.fromObject(mapError);
			writeJSONtoResponse(json, response);
		}
        return "success";
    }*/
	
	public static String createUpdateSalesForecastAdvance(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        
        // Get the parameters as a MAP, remove the productId and quantity params.
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);

        String customTimePeriodId = null;
    	String tabCurrent = null;
    	
        Map<String, Object> colAndRow = getMultiFormRowCount(paramMap);
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
        
    	GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
    	
        // The number of multi form rows is retrieved
        int rowCount = rowIds.size();
        if (rowCount < 1) {
            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
        } else {
            for (int i = 0; i < rowCount; i++) {
            	if (paramMap.containsKey("customTimePeriodId_" + rowIds.get(i))) {
            		customTimePeriodId = (String) paramMap.remove("customTimePeriodId_" + rowIds.get(i));
        		}
            	for (int j = 0; j < colCount; j++) {
            		String salesForecastId = null;
                	String salesForecastDetailId = null;
                	String productId = null;
                	String quantityOldStr = null;
                	String quantityNewStr = null;
                	BigDecimal quantityOld = BigDecimal.ZERO;
                	BigDecimal quantityNew = BigDecimal.ZERO;
                	
            		String thisSuffix = "_" + rowIds.get(i) + "_" + j;
            		/*get //ayoSalesForecastId_ 
            		 *    //ayoSalesForecastDetailId_ 
            		 * ayoProductId_ 
            		 * salesForecastId_
            		 * salesForecastDetailId_
            		 * productId_
            		 * quantity_
            		 * forecastInput_
            		 * */
            		
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
            				if (UtilValidate.isNotEmpty(salesForecastDetailId)) {
                				// update sales forecast detail with id = (salesForecastId, salesForecastDetailId)
                				Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "salesForecastId", salesForecastId, "salesForecastDetailId", salesForecastDetailId, "productId", productId, "quantity", quantityNew);
                                try {
                                    dispatcher.runSync("updateSalesForecastDetail", input);
                                } catch (GenericServiceException e) {
                                    Debug.logError(e, module);
                                    return "error";
                                }
                			} else {
                				// create new a sales forecast detail
                				Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "salesForecastId", salesForecastId, "productId", productId, "quantity", quantityNew);
                                try {
                                    dispatcher.runSync("createSalesForecastDetail", input);
                                } catch (GenericServiceException e) {
                                    Debug.logError(e, module);
                                    return "error";
                                }
                			}
            			} else {
            				//create sales forecast, then create sales forecast detail
            				if (UtilValidate.isNotEmpty(customTimePeriodId) && UtilValidate.isNotEmpty(internalPartyId) && UtilValidate.isNotEmpty(customTimePeriodId) 
            						&& UtilValidate.isNotEmpty(organizationPartyId) && UtilValidate.isNotEmpty(currencyUomId)) {
            					Map<String, Object> inputSalesForecast = UtilMisc.toMap("userLogin", userLogin, "internalPartyId", 
                						internalPartyId, "customTimePeriodId", customTimePeriodId, "organizationPartyId", organizationPartyId, "currencyUomId", currencyUomId);
                				try {
                					List<GenericValue> listSalesForecast = delegator.findByAnd("SalesForecast", UtilMisc.toMap("internalPartyId", internalPartyId, "customTimePeriodId", customTimePeriodId, "organizationPartyId", organizationPartyId, "currencyUomId", currencyUomId), null, false);
                					if (UtilValidate.isNotEmpty(listSalesForecast)) {
                						GenericValue salesForecastFirst = listSalesForecast.get(0);
                						salesForecastId = salesForecastFirst.getString("salesForecastId");
                					} else {
                						Map<String, Object> resultService = dispatcher.runSync("createSalesForecast", inputSalesForecast);
                                        if (!ServiceUtil.isError(resultService)) {
                                        	salesForecastId = (String) resultService.get("salesForecastId");
                                        }
                					}
                                    if (UtilValidate.isNotEmpty(salesForecastId)) {
                                    	if (UtilValidate.isNotEmpty(salesForecastDetailId)) {
                            				// update sales forecast detail with id = (salesForecastId, salesForecastDetailId)
                            				Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "salesForecastId", salesForecastId, "salesForecastDetailId", salesForecastDetailId, "productId", productId, "quantity", quantityNew);
                                            try {
                                                dispatcher.runSync("updateSalesForecastDetail", input);
                                            } catch (GenericServiceException e) {
                                                Debug.logError(e, module);
                                                return "error";
                                            }
                            			} else {
                            				// create new a sales forecast detail
                            				Map<String, Object> input = UtilMisc.toMap("userLogin", userLogin, "salesForecastId", salesForecastId, "productId", productId, "quantity", quantityNew);
                                            try {
                                                dispatcher.runSync("createSalesForecastDetail", input);
                                            } catch (GenericServiceException e) {
                                                Debug.logError(e, module);
                                                return "error";
                                            }
                            			}
                                    }
                                } catch (GenericServiceException e) {
                                    Debug.logError(e, module);
                                    return "error";
                                } catch (GenericEntityException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
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
    
    public static String updateReturnProductReqs(HttpServletRequest request, HttpServletResponse response) {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		Locale locale = UtilHttp.getLocale(request);
		String listData = request.getParameter("listData");
		Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");  
		JSONArray json = new JSONArray();
		if(UtilValidate.isNotEmpty(listData)){
			 json = JSONArray.fromObject(listData);
		}
		if (json != null && json.size() > 0) {
			List<GenericValue> toBeStored = new LinkedList<GenericValue>();
			for (int i = 0; i < json.size(); i++) {
				JSONObject groupItem = json.getJSONObject(i);
				String requirementId = groupItem.getString("requirementId");
				String statusId = groupItem.getString("statusId");
				if (UtilValidate.isEmpty(statusId)) {
					request.setAttribute("responseMessage", "error");
					request.setAttribute("errorMessage", UtilProperties.getMessage(resource_error, "DAStatusCannotEmpty", locale));
				}
				if (UtilValidate.isNotEmpty(requirementId)) {
					try {
						GenericValue itemData = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", requirementId), false);
						if (itemData != null) {
							GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
							if (UtilValidate.isNotEmpty(statusItem)) {
								itemData.put("statusId", statusId);
								toBeStored.add(itemData);
								Map<String, Object> contextMap = UtilMisc.<String, Object>toMap("requirementId", itemData.get("requirementId"), "statusId", statusId, "statusDate", nowTimestamp, "statusUserLogin", userLogin.get("userLoginId"));
								GenericValue requirementStatus = delegator.makeValue("RequirementStatus", contextMap);
								toBeStored.add(requirementStatus);
							}
							List<GenericValue> listRequirementItem = delegator.findByAnd("RequirementItem", UtilMisc.toMap("requirementId", itemData.get("requirementId")), null, false);
							if (UtilValidate.isNotEmpty(listRequirementItem)) {
								for (GenericValue requirementItem : listRequirementItem) {
									if (UtilValidate.isEmpty(requirementItem.get("quantityAccepted")) && UtilValidate.isNotEmpty(requirementItem.get("quantity"))) {
										requirementItem.put("quantityAccepted", requirementItem.get("quantity"));
										toBeStored.add(requirementItem);
									}
								}
							}
						}
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
	            // store line items, etc so that they will be there for the foreign key checks
	            delegator.storeAll(toBeStored);
	        } catch (GenericEntityException e) {
	            Debug.logError(e, "Problem with store all data updateReturnProductReqs event", module);
	            return "error";
	        }
		}
		return "success";
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
    private static String processResult(Map<String, Object> result, HttpServletRequest request) {
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
    
    @SuppressWarnings("unchecked")
	public static String createProductPromoAdvance(HttpServletRequest request, HttpServletResponse response) {
        //Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        
        if (!(security.hasPermission("DELYS_PROMOS_CREATE", session) || security.hasPermission("DELYS_PROMOS_ADMIN", session))) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAYouHavenotCreatePermission", locale));
        	return "error";
        }
        
        // Get parameter information general
        String productPromoId = request.getParameter("productPromoId");
        String promoName = request.getParameter("promoName");
        String productPromoTypeId = request.getParameter("productPromoTypeId");
        String[] productStoreIds = request.getParameterValues("productStoreIds");
        String[] roleTypeIdsStr = request.getParameterValues("roleTypeIds");
        String policyText = request.getParameter("policyText");
        String promoSalesTargets = request.getParameter("promoSalesTargets");
        String fromDateStr = request.getParameter("fromDate");
        String thruDateStr = request.getParameter("thruDate");
        String showToCustomer = request.getParameter("showToCustomer");
        String budgetId = request.getParameter("budgetId");
        String miniRevenueId = request.getParameter("miniRevenueId");
        String paymentMethod = request.getParameter("paymentMethod");
        String useLimitPerOrderStr = request.getParameter("useLimitPerOrder");
        String useLimitPerCustomerStr = request.getParameter("useLimitPerCustomer");
        String useLimitPerPromotionStr = request.getParameter("useLimitPerPromotion");
        Long useLimitPerOrder = null;
        Long useLimitPerCustomer = null;
        Long useLimitPerPromotion = null;
        
        Timestamp fromDate = null;
        Timestamp thruDate = null;
        try {
	        if (UtilValidate.isNotEmpty(fromDateStr)) {
	        	Long fromDateL = Long.parseLong(fromDateStr);
	        	fromDate = new Timestamp(fromDateL);
	        }
	        if (UtilValidate.isNotEmpty(thruDateStr)) {
	        	Long thruDateL = Long.parseLong(thruDateStr);
	        	thruDate = new Timestamp(thruDateL);
	        }
	        if (UtilValidate.isNotEmpty(useLimitPerOrderStr)) useLimitPerOrder = Long.parseLong(useLimitPerOrderStr);
	        if (UtilValidate.isNotEmpty(useLimitPerCustomerStr)) useLimitPerCustomer = Long.parseLong(useLimitPerCustomerStr);
	        if (UtilValidate.isNotEmpty(useLimitPerPromotionStr)) useLimitPerPromotion = Long.parseLong(useLimitPerPromotionStr);
        } catch (Exception e) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAErrorWhenFormatDateTime", locale));
        	return "error";
        }
        
        List<Object> errMsgList = FastList.newInstance();
        
        boolean beganTx = false;
        String productPromoIdSuccess = "";
        try {
        	// begin the transaction
        	beganTx = TransactionUtil.begin(7200);
        	String controlDirective = null;
        	List<String> roleTypeIds = Arrays.asList(roleTypeIdsStr);
        	/*if (UtilValidate.isNotEmpty(roleTypeIdsStr)) {
        		for (String roleTypeId : roleTypeIdsStr) {
        			roleTypeIds.add(roleTypeId);
				}
        	}*/
        	Map<String, Object> contextMap = FastMap.newInstance();
        	contextMap.put("productPromoId", productPromoId);
        	contextMap.put("promoName", promoName);
        	contextMap.put("productPromoTypeId", productPromoTypeId);
        	contextMap.put("productStoreIds", productStoreIds);
        	contextMap.put("roleTypeIds", roleTypeIds);
        	contextMap.put("policyText", policyText);
        	contextMap.put("promoSalesTargets", promoSalesTargets);
        	contextMap.put("fromDate", fromDate);
        	contextMap.put("thruDate", thruDate);
        	contextMap.put("showToCustomer", showToCustomer);
        	contextMap.put("budgetId", budgetId);
        	contextMap.put("miniRevenueId", miniRevenueId);
        	contextMap.put("paymentMethod", paymentMethod);
        	contextMap.put("useLimitPerOrder", useLimitPerOrder);
        	contextMap.put("useLimitPerCustomer", useLimitPerCustomer);
        	contextMap.put("useLimitPerPromotion", useLimitPerPromotion);
        	contextMap.put("userLogin", userLogin);
        	contextMap.put("locale", locale);
        	Map<String, Object> result0 = dispatcher.runSync("createProductPromoDelys", contextMap);
        	// no values for price and paramMap (a context for adding attributes)
            controlDirective = processResult(result0, request);
            if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
            	try {
                    TransactionUtil.rollback(beganTx, "Failure in processing Create product promo callback", null);
                } catch (Exception e1) {
                    Debug.logError(e1, module);
                }
                return "error";
            }
            
            productPromoIdSuccess = (String) result0.get("productPromoId");
            
	        // Get the parameters as a MAP, remove the productId and quantity params.
	        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	        
	        // The number of multi form rows is retrieved
	        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	        if (rowCount < 1) {
	            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
	        } else {
	            for (int i = 0; i < rowCount; i++) {
	            	// process list rule (condition, action)
	    	        String ruleName = null;
	    	        
	    	        controlDirective = null;                // re-initialize each time
	                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	                
	                // get the productId
	                if (paramMap.containsKey("ruleName" + thisSuffix)) {
	                	ruleName = (String) paramMap.remove("ruleName" + thisSuffix);
	                }
	                
	                if (UtilValidate.isEmpty(ruleName)) {
	                	continue;
	                }
	                
	                String productPromoRuleId = "";
	                Map<String, Object> result1 = dispatcher.runSync("createProductPromoRuleDelys", 
	                		UtilMisc.<String, Object>toMap("productPromoId", productPromoIdSuccess, "ruleName", ruleName, "userLogin", userLogin, "locale", locale));
	                // no values for price and paramMap (a context for adding attributes)
	                controlDirective = processResult(result1, request);
	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	                	try {
	                        TransactionUtil.rollback(beganTx, "Failure in processing Create product promo rule callback", null);
	                    } catch (Exception e1) {
	                        Debug.logError(e1, module);
	                    }
	                    return "error";
	                }
	                productPromoRuleId = (String) result1.get("productPromoRuleId");
	                
	                // The number of multi form rows is retrieved: Condition
	    	        int rowCountSeqCond = getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_COND);
	    	        if (rowCountSeqCond < 1) {
	    	            Debug.logWarning("No rows seq to process, as rowCountSeqCondition = " + rowCountSeqCond, module);
	    	        } else {
	    	        	for (int j = 0; j < rowCountSeqCond; j++) {
	    	        		String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_COND + j + UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	    	        		
	    	        		List<String> productIdListCond = FastList.newInstance();
	    	    	        String productPromoApplEnumId = null;
	    	    	        String includeSubCategories = null;
	    	    	        List<String> productCatIdListCond = FastList.newInstance();
	    	    	        String inputParamEnumId = null;
	    	    	        String operatorEnumId = null;
	    	    	        String condValue = null;
	    	    	        String condExhibited = null;
	    	    	        String notes = null;
	    	    	        String isRemoveCond = "N";
	    	    	        
	    	    	        if (paramMap.containsKey("isRemoveCond" + thisSuffixSeq)) {
	    	    	        	isRemoveCond = (String) paramMap.remove("isRemoveCond" + thisSuffixSeq);
	    	                }
	    	    	        if ("Y".equals(isRemoveCond)) {
	    	    	        	continue;
	    	    	        }
	    	    	        
	    	    	        if (paramMap.containsKey("productIdListCond" + thisSuffixSeq)) {
	    	                	Object productIdListCondObj = (Object) paramMap.remove("productIdListCond" + thisSuffixSeq);
	    	                	if (productIdListCondObj instanceof String) {
	    	                		productIdListCond.add(productIdListCondObj.toString());
	    	                	} else if (productIdListCondObj instanceof List) {
	    	                		productIdListCond = (List<String>) productIdListCondObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("productPromoApplEnumId" + thisSuffixSeq)) {
	    	                	productPromoApplEnumId = (String) paramMap.remove("productPromoApplEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("includeSubCategories" + thisSuffixSeq)) {
	    	                	includeSubCategories = (String) paramMap.remove("includeSubCategories" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("productCatIdListCond" + thisSuffixSeq)) {
	    	                	Object productCatIdListCondObj = (Object) paramMap.remove("productCatIdListCond" + thisSuffixSeq);
	    	                	if (productCatIdListCondObj instanceof String) {
	    	                		productCatIdListCond.add(productCatIdListCondObj.toString());
	    	                	} else if (productCatIdListCondObj instanceof List) {
	    	                		productCatIdListCond = (List<String>) productCatIdListCondObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("inputParamEnumId" + thisSuffixSeq)) {
	    	                	inputParamEnumId = (String) paramMap.remove("inputParamEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("operatorEnumId" + thisSuffixSeq)) {
	    	                	operatorEnumId = (String) paramMap.remove("operatorEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("condValue" + thisSuffixSeq)) {
	    	                	condValue = (String) paramMap.remove("condValue" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("condExhibited" + thisSuffixSeq)) {
	    	                	condExhibited = (String) paramMap.remove("condExhibited" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("notes" + thisSuffixSeq)) {
	    	                	notes = (String) paramMap.remove("notes" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (UtilValidate.isNotEmpty(condValue)) {
	    	                	Map<String, Object> result2 = dispatcher.runSync("createProductPromoCondDelys", 
		    	                		UtilMisc.<String, Object>toMap("productPromoId", productPromoIdSuccess, 
		    	                				"productPromoRuleId", productPromoRuleId, 
		    	                				"productIdListCond", productIdListCond, "productPromoApplEnumId", productPromoApplEnumId, 
		    	                				"includeSubCategories", includeSubCategories, "productCatIdListCond", productCatIdListCond, 
		    	                				"inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId, 
		    	                				"condValue", condValue, "condExhibited", condExhibited, "notes", notes, 
		    	                				"userLogin", userLogin, "locale", locale));
		    	                // no values for price and paramMap (a context for adding attributes)
		    	                controlDirective = processResult(result2, request);
		    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
		    	                	try {
		    	                        TransactionUtil.rollback(beganTx, "Failure in processing Create product promo condition callback", null);
		    	                    } catch (Exception e1) {
		    	                        Debug.logError(e1, module);
		    	                    }
		    	                    return "error";
		    	                }
	    	                } else {
    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"DAConditionValueOfConditionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoCondSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
	    	                }
	    	        	}
	    	        }
	    	        
	    	        // The number of multi form rows is retrieved: Action
	    	        int rowCountSeqAction = getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_ACT);
	    	        if (rowCountSeqAction < 1) {
	    	            Debug.logWarning("No rows seq to process, as rowCountSeqAction = " + rowCountSeqAction, module);
	    	        } else {
	    	        	for (int j = 0; j < rowCountSeqAction; j++) {
	    	        		String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_ACT + j + UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	    	    	        
	    	    	        String orderAdjustmentTypeId = null;
	    	    	        List<String> productIdListAction = FastList.newInstance();
	    	    	        String productPromoApplEnumIdAction = null;
	    	    	        String includeSubCategoriesAction = null;
	    	    	        List<String> productCatIdListAction = FastList.newInstance();
	    	    	        String productPromoActionEnumId = null;
	    	    	        BigDecimal quantity = null;
	    	    	        String quantityStr = null;
	    	    	        BigDecimal amount = null;
	    	    	        String amountStr = null;
    	    	        	String isRemoveAction = "N";
	    	    	        
	    	    	        if (paramMap.containsKey("isRemoveAction" + thisSuffixSeq)) {
	    	    	        	isRemoveAction = (String) paramMap.remove("isRemoveAction" + thisSuffixSeq);
	    	                }
	    	    	        if ("Y".equals(isRemoveAction)) {
	    	    	        	continue;
	    	    	        }
	    	    	        
	    	                if (paramMap.containsKey("orderAdjustmentTypeId" + thisSuffixSeq)) {
	    	                	orderAdjustmentTypeId = (String) paramMap.remove("orderAdjustmentTypeId" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (paramMap.containsKey("productIdListAction" + thisSuffixSeq)) {
	    	                	Object productIdListActionObj = (Object) paramMap.remove("productIdListAction" + thisSuffixSeq);
	    	                	if (productIdListActionObj instanceof String) {
	    	                		productIdListAction.add(productIdListActionObj.toString());
	    	                	} else if (productIdListActionObj instanceof List) {
	    	                		productIdListAction = (List<String>) productIdListActionObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("productCatIdListAction" + thisSuffixSeq)) {
	    	                	Object productCatIdListActionObj = (Object) paramMap.remove("productCatIdListAction" + thisSuffixSeq);
	    	                	if (productCatIdListActionObj instanceof String) {
	    	                		productCatIdListAction.add(productCatIdListActionObj.toString());
	    	                	} else if (productCatIdListActionObj instanceof List) {
	    	                		productCatIdListAction = (List<String>) productCatIdListActionObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("productPromoApplEnumIdAction" + thisSuffixSeq)) {
	    	                	productPromoApplEnumIdAction = (String) paramMap.remove("productPromoApplEnumIdAction" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("includeSubCategoriesAction" + thisSuffixSeq)) {
	    	                	includeSubCategoriesAction = (String) paramMap.remove("includeSubCategoriesAction" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("productPromoActionEnumId" + thisSuffixSeq)) {
	    	                	productPromoActionEnumId = (String) paramMap.remove("productPromoActionEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("quantity" + thisSuffixSeq)) {
	    	                    quantityStr = (String) paramMap.remove("quantity" + thisSuffixSeq);
	    	                }
	    	                if (UtilValidate.isNotEmpty(quantityStr)) {
	    	                	// parse the quantity
	    	                    try {
	    	                        quantity = new BigDecimal(quantityStr);
	    	                    } catch (Exception e) {
	    	                        Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
	    	                        quantity = BigDecimal.ZERO;
	    	                    }
	    	                }
	    	                
	    	                // get the selected amount
	    	                if (paramMap.containsKey("amount" + thisSuffixSeq)) {
	    	                	amountStr = (String) paramMap.remove("amount" + thisSuffixSeq);
	    	                }
	    	
	    	                // parse the amount
	    	                if (UtilValidate.isNotEmpty(amountStr)) {
	    	                    try {
	    	                        amount = new BigDecimal(amountStr);
	    	                    } catch (Exception e) {
	    	                        Debug.logWarning(e, "Problem parsing amount string: " + amountStr, module);
	    	                        amount = null;
	    	                    }
	    	                }
	    	                
	    	                if (quantity != null || amount != null) {
	    	                    //Debug.logInfo("Attempting to add to cart with productId = ", module);
	    	                	Map<String, Object> result3 = dispatcher.runSync("createProductPromoActionDelys", 
	    	                    		UtilMisc.<String, Object>toMap("productPromoId", productPromoIdSuccess, 
	    		                				"productPromoRuleId", productPromoRuleId, "productIdListAction", productIdListAction, 
	    		                				"orderAdjustmentTypeId", orderAdjustmentTypeId, 
	    	                    				"productPromoApplEnumId", productPromoApplEnumIdAction, "includeSubCategories", includeSubCategoriesAction, 
	    	                    				"productCatIdListAction", productCatIdListAction, "productPromoActionEnumId", productPromoActionEnumId, 
	    	                    				"quantity", quantity, "amount", amount, "userLogin", userLogin, "locale", locale));
	    	                    // no values for price and paramMap (a context for adding attributes)
	    	                    controlDirective = processResult(result3, request);
	    	                    if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	    	                    	try {
	    	                            TransactionUtil.rollback(beganTx, "Failure in processing Create product promo action callback", null);
	    	                        } catch (Exception e1) {
	    	                            Debug.logError(e1, module);
	    	                        }
	    	                        return "error";
	    	                    }
	    	                } else {
    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"DAApplyValueOfActionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoActionSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
	    	                }
	    	        	}
	    	        }
	            }
	        }
        } catch (Exception e) {
            try {
                TransactionUtil.rollback(beganTx, e.getMessage(), e);
            } catch (Exception e1) {
                Debug.logError(e1, module);
            }
            errMsgList.add(UtilProperties.getMessage(resource_error, "DAErrorWhenProcessing", locale));
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        } catch (Throwable t) {
            Debug.logError(t, module);
            request.setAttribute("_ERROR_MESSAGE_", t.getMessage());
            try {
                TransactionUtil.rollback(beganTx, t.getMessage(), t);
            } catch (Exception e2) {
                Debug.logError(e2, module);
            }
            errMsgList.add(UtilProperties.getMessage(resource_error, "DAErrorWhenProcessing", locale));
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        } finally {
        	if (UtilValidate.isNotEmpty(errMsgList)) {
        		try {
                    TransactionUtil.rollback(beganTx, "Have error when process", null);
                } catch (Exception e2) {
                    Debug.logError(e2, module);
                }
            	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            	return "error";
            } else {
            	// commit the transaction
                try {
                    TransactionUtil.commit(beganTx);
                } catch (Exception e) {
                    Debug.logError(e, module);
                }
            }
        }
        request.setAttribute("productPromoId", productPromoIdSuccess);
        return "success";
    }
    
    /** Returns the number or rows submitted by a multi form. Seq in row "_r_"
     */
    public static int getMultiFormRowCountSeq(Map<String, ?> requestMap, String seq_id) {
        // The number of multi form rows is computed selecting the maximum index
        int rowCount = 0;
        String maxRowIndex = "";
        int rowDelimiterLength = MULTI_ROW_DELIMITER_SEQ.length();
        for (String parameterName: requestMap.keySet()) {
            int rowDelimiterIndex = (parameterName != null? parameterName.indexOf(MULTI_ROW_DELIMITER_SEQ): -1);
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
    
    @SuppressWarnings("unchecked")
	public static String updateProductPromoAdvance(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        
        if (!(security.hasPermission("DELYS_PROMOS_CREATE", session) || security.hasPermission("DELYS_PROMOS_ADMIN", session))) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAYouHavenotUpdatePermission", locale));
        	return "error";
        }
        
        // Get parameter information general
        String productPromoId = request.getParameter("productPromoId");
        String promoName = request.getParameter("promoName");
        String productPromoTypeId = request.getParameter("productPromoTypeId");
        String[] productStoreIdsStr = request.getParameterValues("productStoreIds");
        String[] roleTypeIdsStr = request.getParameterValues("roleTypeIds");
        String policyText = request.getParameter("policyText");
        String promoSalesTargets = request.getParameter("promoSalesTargets");
        String fromDateStr = request.getParameter("fromDate");
        String thruDateStr = request.getParameter("thruDate");
        String showToCustomer = request.getParameter("showToCustomer");
        String budgetId = request.getParameter("budgetId");
        String miniRevenueId = request.getParameter("miniRevenueId");
        String paymentMethod = request.getParameter("paymentMethod");
        
        Timestamp fromDate = null;
        Timestamp thruDate = null;
        try {
	        if (UtilValidate.isNotEmpty(fromDateStr)) {
	        	Long fromDateL = Long.parseLong(fromDateStr);
	        	fromDate = new Timestamp(fromDateL);
	        }
	        if (UtilValidate.isNotEmpty(thruDateStr)) {
	        	Long thruDateL = Long.parseLong(thruDateStr);
	        	thruDate = new Timestamp(thruDateL);
	        }
        } catch (Exception e) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAErrorWhenFormatDateTime", locale));
        	return "error";
        }
        
        if (UtilValidate.isEmpty(productPromoId)) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAPromotionIdMustNotBeEmpty", locale));
        	return "error";
        }
        
        List<Object> errMsgList = FastList.newInstance();
        
        boolean beganTx = false;
        try {
        	GenericValue productPromo = delegator.findOne("ProductPromo", UtilMisc.toMap("productPromoId", productPromoId), false);
        	if (productPromo == null) {
        		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAProductPromotionNotFoundHasId", locale) + productPromoId);
                return "error";
        	}
        	
        	// begin the transaction
        	beganTx = TransactionUtil.begin(7200);
        	String controlDirective = null;
        	List<String> roleTypeIds = Arrays.asList(roleTypeIdsStr);
        	List<String> productStoreIds = Arrays.asList(productStoreIdsStr);
        	
        	Map<String, Object> contextMap = FastMap.newInstance();
        	contextMap.put("productPromoId", productPromoId);
        	contextMap.put("promoName", promoName);
        	contextMap.put("productPromoTypeId", productPromoTypeId);
        	contextMap.put("policyText", policyText);
        	contextMap.put("promoSalesTargets", promoSalesTargets);
        	contextMap.put("fromDate", fromDate);
        	contextMap.put("thruDate", thruDate);
        	contextMap.put("showToCustomer", showToCustomer);
        	contextMap.put("budgetId", budgetId);
        	contextMap.put("miniRevenueId", miniRevenueId);
        	contextMap.put("paymentMethod", paymentMethod);
        	contextMap.put("userLogin", userLogin);
        	contextMap.put("locale", locale);
        	Map<String, Object> result0 = dispatcher.runSync("updateProductPromoDelys", contextMap);
        	// no values for price and paramMap (a context for adding attributes)
            controlDirective = processResult(result0, request);
            if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
            	try {
                    TransactionUtil.rollback(beganTx, "Failure in processing Create product promo callback", null);
                } catch (Exception e1) {
                    Debug.logError(e1, module);
                }
                return "error";
            }
            
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            
            List<GenericValue> tobeStored = new LinkedList<GenericValue>();
        	// update list product store promo appl
            List<GenericValue> listProductStorePromoAppl = delegator.findByAnd("ProductStorePromoAppl", 
            		UtilMisc.toMap("productPromoId", productPromoId), null, false);
            if (listProductStorePromoAppl != null) {
            	List<String> productStoreIdAppls = EntityUtil.getFieldListFromEntityList(listProductStorePromoAppl, "productStoreId", true);
            	for (GenericValue productStorePromoAppl : listProductStorePromoAppl) {
            		if (productStoreIds.contains(productStorePromoAppl.getString("productStoreId"))) {
            			if (fromDate.equals(productStorePromoAppl.getTimestamp("fromDate"))) {
            				// no action
            			} else {
            				// thruDate this productStorePromoAppl record + create new record with fromDate
            				productStorePromoAppl.put("thruDate", nowTimestamp);
            				tobeStored.add(productStorePromoAppl);
            				GenericValue newProductStorePromoAppl = delegator.makeValue("ProductStorePromoAppl");
            				newProductStorePromoAppl.put("productStoreId", productStorePromoAppl.get("productStoreId"));
            				newProductStorePromoAppl.put("productPromoId", productStorePromoAppl.get("productPromoId"));
            				newProductStorePromoAppl.put("fromDate", fromDate);
            				tobeStored.add(newProductStorePromoAppl);
            			}
            		} else {
            			// this record was deleted
            			productStorePromoAppl.put("thruDate", nowTimestamp);
        				tobeStored.add(productStorePromoAppl);
            		}
            	}
            	for (String productStoreId : productStoreIds) {
            		if (productStoreIdAppls.contains(productStoreId)) {
            			// no action
            		} else {
            			// create new
            			GenericValue newProductStorePromoAppl = delegator.makeValue("ProductStorePromoAppl");
        				newProductStorePromoAppl.put("productStoreId", productStoreId);
        				newProductStorePromoAppl.put("productPromoId", productPromoId);
        				newProductStorePromoAppl.put("fromDate", fromDate);
        				tobeStored.add(newProductStorePromoAppl);
            		}
            	}
            }
            
            // update list role type promo appl
            List<GenericValue> listRoleTypePromoAppl = delegator.findByAnd("ProductPromoRoleTypeAppl", 
            		UtilMisc.toMap("productPromoId", productPromoId), null, false);
            if (listRoleTypePromoAppl != null) {
            	List<String> roleTypeIdAppls = EntityUtil.getFieldListFromEntityList(listRoleTypePromoAppl, "roleTypeId", true);
            	for (GenericValue roleTypePromoAppl : listRoleTypePromoAppl) {
            		if (roleTypeIds.contains(roleTypePromoAppl.getString("roleTypeId"))) {
            			if (fromDate.equals(roleTypePromoAppl.getTimestamp("fromDate"))) {
            				// no action
            			} else {
            				// thruDate this productPromoRoleTypeAppl record + create new record with fromDate
            				roleTypePromoAppl.put("thruDate", nowTimestamp);
            				tobeStored.add(roleTypePromoAppl);
            				GenericValue newProductPromoRoleTypeAppl = delegator.makeValue("ProductPromoRoleTypeAppl");
            				newProductPromoRoleTypeAppl.put("roleTypeId", roleTypePromoAppl.get("roleTypeId"));
            				newProductPromoRoleTypeAppl.put("productPromoId", roleTypePromoAppl.get("productPromoId"));
            				newProductPromoRoleTypeAppl.put("fromDate", fromDate);
            				tobeStored.add(newProductPromoRoleTypeAppl);
            			}
            		} else {
            			// this record was deleted
            			roleTypePromoAppl.put("thruDate", nowTimestamp);
        				tobeStored.add(roleTypePromoAppl);
            		}
            	}
            	for (String roleTypeId : roleTypeIds) {
            		if (roleTypeIdAppls.contains(roleTypeId)) {
            			// no action
            		} else {
            			// create new
            			GenericValue newProductPromoRoleTypeAppl = delegator.makeValue("ProductPromoRoleTypeAppl");
        				newProductPromoRoleTypeAppl.put("roleTypeId", roleTypeId);
        				newProductPromoRoleTypeAppl.put("productPromoId", productPromoId);
        				newProductPromoRoleTypeAppl.put("fromDate", fromDate);
        				tobeStored.add(newProductPromoRoleTypeAppl);
            		}
            	}
            }
            delegator.storeAll(tobeStored);
            
            
            List<GenericValue> listProductPromoProduct = delegator.findByAnd("ProductPromoProduct", UtilMisc.toMap("productPromoId", productPromoId), null, false);
            List<GenericValue> listProductPromoCategory = delegator.findByAnd("ProductPromoCategory", UtilMisc.toMap("productPromoId", productPromoId), null, false);
            
	        // Get the parameters as a MAP, remove the productId and quantity params.
	        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	        
	        // The number of multi form rows is retrieved
	        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	        if (rowCount < 1) {
	            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
	        } else {
	            for (int i = 0; i < rowCount; i++) {
	            	// process list rule (condition, action)
	    	        String ruleName = null;
	    	        String productPromoRuleId = "";
	    	        boolean isRuleExisted = false;
	    	        String isRemoveRule = "N";
	    	        
	    	        controlDirective = null;                // re-initialize each time
	                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	                
	                // get the ruleName
	                if (paramMap.containsKey("ruleName" + thisSuffix)) {
	                	ruleName = (String) paramMap.remove("ruleName" + thisSuffix);
	                }
	                // get the ruleId
	                if (paramMap.containsKey("productPromoRuleId" + thisSuffix)) {
	                	productPromoRuleId = (String) paramMap.remove("productPromoRuleId" + thisSuffix);
	                }
	                if (paramMap.containsKey("isRemoveRule" + thisSuffix)) {
	                	isRemoveRule = (String) paramMap.remove("isRemoveRule" + thisSuffix);
	                }

	                GenericValue productPromoRule = delegator.findOne("ProductPromoRule", UtilMisc.toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId), false);
	                if (productPromoRule != null) {
	                	isRuleExisted = true;
	                }
	                
	                if ("Y".equals(isRemoveRule)) {
	                	if (isRuleExisted) {
	                		// delete condition
	    	        		Map<String, Object> result2 = dispatcher.runSync("deleteProductPromoRuleDelys", UtilMisc.<String, Object>toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "userLogin", userLogin, "locale", locale));
	    	                // no values for price and paramMap (a context for adding attributes)
	    	                controlDirective = processResult(result2, request);
	    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	    	                	try {
	    	                        TransactionUtil.rollback(beganTx, "Failure in processing Create product promo condition callback", null);
	    	                    } catch (Exception e1) {
	    	                        Debug.logError(e1, module);
	    	                    }
	    	                    return "error";
	    	                }
	    	                continue;
	                	} else {
	                		continue;
	                	}
	                }
	                
	                if (UtilValidate.isEmpty(ruleName)) {
	                	continue;
	                }
	                
	                Map<String, Object> result1 = null;
	                if (isRuleExisted) {
	                	result1 = dispatcher.runSync("updateProductPromoRuleDelys", 
		                		UtilMisc.<String, Object>toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, 
		                				"ruleName", ruleName, "userLogin", userLogin, "locale", locale));
	                } else {
		                result1 = dispatcher.runSync("createProductPromoRuleDelys", 
		                		UtilMisc.<String, Object>toMap("productPromoId", productPromoId, "ruleName", ruleName, "userLogin", userLogin, "locale", locale));
		                if (result1 != null) productPromoRuleId = (String) result1.get("productPromoRuleId");
	                }
	                // no values for price and paramMap (a context for adding attributes)
	                controlDirective = processResult(result1, request);
	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	                	try {
	                        TransactionUtil.rollback(beganTx, "Failure in processing Create product promo rule callback", null);
	                    } catch (Exception e1) {
	                        Debug.logError(e1, module);
	                    }
	                    return "error";
	                }
	                
	                // The number of multi form rows is retrieved: Condition
	    	        int rowCountSeqCond = getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_COND);
	    	        if (rowCountSeqCond < 1) {
	    	            Debug.logWarning("No rows seq to process, as rowCountSeqCondition = " + rowCountSeqCond, module);
	    	        } else {
	    	        	for (int j = 0; j < rowCountSeqCond; j++) {
	    	        		String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_COND + j + UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	    	        		String productPromoCondSeqId = null;
	    	        		List<String> productIdListCond = FastList.newInstance();
	    	    	        String productPromoApplEnumId = null;
	    	    	        String includeSubCategories = null;
	    	    	        List<String> productCatIdListCond = FastList.newInstance();
	    	    	        String inputParamEnumId = null;
	    	    	        String operatorEnumId = null;
	    	    	        String condValue = null;
	    	    	        String condExhibited = null;
	    	    	        String notes = null;
    	    	        	String isRemoveCond = "N";
    	    	        	
	    	    	        if (paramMap.containsKey("isRemoveCond" + thisSuffixSeq)) {
	    	    	        	isRemoveCond = (String) paramMap.remove("isRemoveCond" + thisSuffixSeq);
	    	                }
	    	    	        if (paramMap.containsKey("productPromoCondSeqId" + thisSuffixSeq)) {
	    	    	        	productPromoCondSeqId = (String) paramMap.remove("productPromoCondSeqId" + thisSuffixSeq);
	    	                }
	    	    	        
	    	    	        boolean isCondExisted = false;
	    	                GenericValue productPromoCond = delegator.findOne("ProductPromoCond", UtilMisc.toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "productPromoCondSeqId", productPromoCondSeqId), false);
	    	                if (productPromoCond != null) isCondExisted = true;
	    	    	        
	    	    	        if ("Y".equals(isRemoveCond)) {
	    	    	        	if (isRuleExisted && isCondExisted) {
		    	                	// delete condition
	    	    	        		Map<String, Object> result2 = dispatcher.runSync("deleteProductPromoCondDelys", UtilMisc.<String, Object>toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "productPromoCondSeqId", productPromoCondSeqId, "userLogin", userLogin, "locale", locale));
			    	                // no values for price and paramMap (a context for adding attributes)
			    	                controlDirective = processResult(result2, request);
			    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
			    	                	try {
			    	                        TransactionUtil.rollback(beganTx, "Failure in processing Create product promo condition callback", null);
			    	                    } catch (Exception e1) {
			    	                        Debug.logError(e1, module);
			    	                    }
			    	                    return "error";
			    	                }
			    	                continue;
		    	                } else {
		    	                	continue;
		    	                }
	    	    	        }
	    	    	        
	    	    	        if (paramMap.containsKey("productIdListCond" + thisSuffixSeq)) {
	    	                	Object productIdListCondObj = (Object) paramMap.remove("productIdListCond" + thisSuffixSeq);
	    	                	if (productIdListCondObj instanceof String) {
	    	                		productIdListCond.add(productIdListCondObj.toString());
	    	                	} else if (productIdListCondObj instanceof List) {
	    	                		productIdListCond = (List<String>) productIdListCondObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("productPromoApplEnumId" + thisSuffixSeq)) {
	    	                	productPromoApplEnumId = (String) paramMap.remove("productPromoApplEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("includeSubCategories" + thisSuffixSeq)) {
	    	                	includeSubCategories = (String) paramMap.remove("includeSubCategories" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("productCatIdListCond" + thisSuffixSeq)) {
	    	                	Object productCatIdListCondObj = (Object) paramMap.remove("productCatIdListCond" + thisSuffixSeq);
	    	                	if (productCatIdListCondObj instanceof String) {
	    	                		productCatIdListCond.add(productCatIdListCondObj.toString());
	    	                	} else if (productCatIdListCondObj instanceof List) {
	    	                		productCatIdListCond = (List<String>) productCatIdListCondObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("inputParamEnumId" + thisSuffixSeq)) {
	    	                	inputParamEnumId = (String) paramMap.remove("inputParamEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("operatorEnumId" + thisSuffixSeq)) {
	    	                	operatorEnumId = (String) paramMap.remove("operatorEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("condValue" + thisSuffixSeq)) {
	    	                	condValue = (String) paramMap.remove("condValue" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("condExhibited" + thisSuffixSeq)) {
	    	                	condExhibited = (String) paramMap.remove("condExhibited" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("notes" + thisSuffixSeq)) {
	    	                	notes = (String) paramMap.remove("notes" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (isRuleExisted && isCondExisted) {
	    	                	if (UtilValidate.isNotEmpty(condValue)) {
	    	                		Map<String, Object> result2 = dispatcher.runSync("updateProductPromoCondDelys", 
			    	                		UtilMisc.<String, Object>toMap("productPromoId", productPromoId, 
			    	                				"productPromoRuleId", productPromoRuleId, "productPromoCondSeqId", productPromoCondSeqId, 
			    	                				"productIdListCond", productIdListCond, "productPromoApplEnumId", productPromoApplEnumId, 
			    	                				"includeSubCategories", includeSubCategories, "productCatIdListCond", productCatIdListCond, 
			    	                				"inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId, 
			    	                				"condValue", condValue, "condExhibited", condExhibited, "notes", notes, 
			    	                				"userLogin", userLogin, "locale", locale));
			    	                // no values for price and paramMap (a context for adding attributes)
			    	                controlDirective = processResult(result2, request);
			    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
			    	                	try {
			    	                        TransactionUtil.rollback(beganTx, "Failure in processing delete product promo condition callback", null);
			    	                    } catch (Exception e1) {
			    	                        Debug.logError(e1, module);
			    	                    }
			    	                    return "error";
			    	                }
			    	                
			    	                List<GenericValue> tobeStoredCond = new LinkedList<GenericValue>();
			    	                List<GenericValue> tobeDeletedCond = new LinkedList<GenericValue>();
			    	                List<GenericValue> listProductPromoProductCond = EntityUtil.filterByAnd(listProductPromoProduct, 
			    	                		UtilMisc.toMap("productPromoRuleId", productPromoRuleId, "productPromoCondSeqId", productPromoCondSeqId));
			    	                if (listProductPromoProductCond != null) {
			    	                	List<String> productIdAppls = EntityUtil.getFieldListFromEntityList(listProductPromoProductCond, "productId", true);
			    	                	for (GenericValue productAppl : listProductPromoProductCond) {
			    	                		if (productIdListCond.contains(productAppl.getString("productId"))) {
		    	                				// no action
			    	                		} else {
			    	                			// this record was deleted
			    	                			tobeDeletedCond.add(productAppl);
			    	                		}
			    	                	}
			    	                	for (String productId : productIdListCond) {
			    	                		if (productIdAppls.contains(productId)) {
			    	                			// no action
			    	                		} else {
			    	                			// create new
			    	                			GenericValue newProductPromoProduct = delegator.makeValue("ProductPromoProduct");
			    	                			newProductPromoProduct.put("productPromoId", productPromoId);
			    	                			newProductPromoProduct.put("productPromoRuleId", productPromoRuleId);
			    	                			newProductPromoProduct.put("productPromoActionSeqId", "_NA_");
			    	                			newProductPromoProduct.put("productPromoCondSeqId", productPromoCondSeqId);
			    	                			newProductPromoProduct.put("productId", productId);
			    	                			newProductPromoProduct.put("productPromoApplEnumId", productPromoApplEnumId);
			    	            				tobeStoredCond.add(newProductPromoProduct);
			    	                		}
			    	                	}
			    	                }
			    	                List<GenericValue> listProductPromoCategoryCond = EntityUtil.filterByAnd(listProductPromoCategory, 
			    	                		UtilMisc.toMap("productPromoRuleId", productPromoRuleId, "productPromoCondSeqId", productPromoCondSeqId));
			    	                if (listProductPromoCategoryCond != null) {
			    	                	List<String> categoryIdAppls = EntityUtil.getFieldListFromEntityList(listProductPromoCategoryCond, "productCategoryId", true);
			    	                	for (GenericValue categoryAppl : listProductPromoCategoryCond) {
			    	                		if (productCatIdListCond.contains(categoryAppl.getString("productCategoryId"))) {
		    	                				// no action
			    	                		} else {
			    	                			// this record was deleted
			    	                			tobeDeletedCond.add(categoryAppl);
			    	                		}
			    	                	}
			    	                	for (String productCategoryId : productCatIdListCond) {
			    	                		if (categoryIdAppls.contains(productCategoryId)) {
			    	                			// no action
			    	                		} else {
			    	                			// create new
			    	                			GenericValue newProductPromoCategory = delegator.makeValue("ProductPromoCategory");
			    	                			newProductPromoCategory.put("productPromoId", productPromoId);
			    	                			newProductPromoCategory.put("productPromoRuleId", productPromoRuleId);
			    	                			newProductPromoCategory.put("productPromoActionSeqId", "_NA_");
			    	                			newProductPromoCategory.put("productPromoCondSeqId", productPromoCondSeqId);
			    	                			newProductPromoCategory.put("productCategoryId", productCategoryId);
			    	                			newProductPromoCategory.put("andGroupId", "_NA_");
			    	                			newProductPromoCategory.put("productPromoApplEnumId", productPromoApplEnumId);
			    	                			newProductPromoCategory.put("includeSubCategories", includeSubCategories);
			    	            				tobeStoredCond.add(newProductPromoCategory);
			    	                		}
			    	                	}
			    	                }
			    	                delegator.storeAll(tobeStoredCond);
			    	                delegator.removeAll(tobeDeletedCond);
	    	                	} else {
	    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"DAConditionValueOfConditionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoCondSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
		    	                }
	    	                } else {
	    	                	if (UtilValidate.isNotEmpty(condValue)) {
	    	                		Map<String, Object> result2 = dispatcher.runSync("createProductPromoCondDelys", 
		 	    	                		UtilMisc.<String, Object>toMap("productPromoId", productPromoId, 
		 	    	                				"productPromoRuleId", productPromoRuleId, 
		 	    	                				"productIdListCond", productIdListCond, "productPromoApplEnumId", productPromoApplEnumId, 
		 	    	                				"includeSubCategories", includeSubCategories, "productCatIdListCond", productCatIdListCond, 
		 	    	                				"inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId, 
		 	    	                				"condValue", condValue, "condExhibited", condExhibited, "notes", notes, 
		 	    	                				"userLogin", userLogin, "locale", locale));
		 	    	                // no values for price and paramMap (a context for adding attributes)
		 	    	                controlDirective = processResult(result2, request);
		 	    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
		 	    	                	try {
		 	    	                        TransactionUtil.rollback(beganTx, "Failure in processing Create product promo condition callback", null);
		 	    	                    } catch (Exception e1) {
		 	    	                        Debug.logError(e1, module);
		 	    	                    }
		 	    	                    return "error";
		 	    	                }
	    	                	} else {
	    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"DAConditionValueOfConditionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoCondSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
		    	                }
	    	                }
	    	        	}
	    	        }
	    	        
	    	        // The number of multi form rows is retrieved: Action
	    	        int rowCountSeqAction = getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_ACT);
	    	        if (rowCountSeqAction < 1) {
	    	            Debug.logWarning("No rows seq to process, as rowCountSeqAction = " + rowCountSeqAction, module);
	    	        } else {
	    	        	for (int j = 0; j < rowCountSeqAction; j++) {
	    	        		String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_ACT + j + UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	    	    	        String productPromoActionSeqId = null;
	    	    	        String orderAdjustmentTypeId = null;
	    	    	        List<String> productIdListAction = FastList.newInstance();
	    	    	        String productPromoApplEnumIdAction = null;
	    	    	        String includeSubCategoriesAction = null;
	    	    	        List<String> productCatIdListAction = FastList.newInstance();
	    	    	        String productPromoActionEnumId = null;
	    	    	        BigDecimal quantity = null;
	    	    	        String quantityStr = null;
	    	    	        BigDecimal amount = null;
	    	    	        String amountStr = null;
	    	    	        String isRemoveAction = "N";
    	    	        	
	    	    	        if (paramMap.containsKey("isRemoveAction" + thisSuffixSeq)) {
	    	    	        	isRemoveAction = (String) paramMap.remove("isRemoveAction" + thisSuffixSeq);
	    	                }
	    	    	        if (paramMap.containsKey("productPromoActionSeqId" + thisSuffixSeq)) {
	    	    	        	productPromoActionSeqId = (String) paramMap.remove("productPromoActionSeqId" + thisSuffixSeq);
	    	                }
	    	    	        
	    	    	        boolean isActionExisted = false;
	    	                GenericValue productPromoAction = delegator.findOne("ProductPromoAction", UtilMisc.toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "productPromoActionSeqId", productPromoActionSeqId), false);
	    	                if (productPromoAction != null) isActionExisted = true;
	    	                
	    	    	        if ("Y".equals(isRemoveAction)) {
	    	    	        	if (isRuleExisted && isActionExisted) {
		    	                	// delete condition
	    	    	        		Map<String, Object> result2 = dispatcher.runSync("deleteProductPromoActionDelys", UtilMisc.<String, Object>toMap("productPromoId", productPromoId, "productPromoRuleId", productPromoRuleId, "productPromoActionSeqId", productPromoActionSeqId, "userLogin", userLogin, "locale", locale));
			    	                // no values for price and paramMap (a context for adding attributes)
			    	                controlDirective = processResult(result2, request);
			    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
			    	                	try {
			    	                        TransactionUtil.rollback(beganTx, "Failure in processing delete product promo action callback", null);
			    	                    } catch (Exception e1) {
			    	                        Debug.logError(e1, module);
			    	                    }
			    	                    return "error";
			    	                }
			    	                continue;
		    	                } else {
		    	                	continue;
		    	                }
	    	    	        }
	    	    	        
	    	                if (paramMap.containsKey("orderAdjustmentTypeId" + thisSuffixSeq)) {
	    	                	orderAdjustmentTypeId = (String) paramMap.remove("orderAdjustmentTypeId" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (paramMap.containsKey("productIdListAction" + thisSuffixSeq)) {
	    	                	Object productIdListActionObj = (Object) paramMap.remove("productIdListAction" + thisSuffixSeq);
	    	                	if (productIdListActionObj instanceof String) {
	    	                		productIdListAction.add(productIdListActionObj.toString());
	    	                	} else if (productIdListActionObj instanceof List) {
	    	                		productIdListAction = (List<String>) productIdListActionObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("productCatIdListAction" + thisSuffixSeq)) {
	    	                	Object productCatIdListActionObj = (Object) paramMap.remove("productCatIdListAction" + thisSuffixSeq);
	    	                	if (productCatIdListActionObj instanceof String) {
	    	                		productCatIdListAction.add(productCatIdListActionObj.toString());
	    	                	} else if (productCatIdListActionObj instanceof List) {
	    	                		productCatIdListAction = (List<String>) productCatIdListActionObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("productPromoApplEnumIdAction" + thisSuffixSeq)) {
	    	                	productPromoApplEnumIdAction = (String) paramMap.remove("productPromoApplEnumIdAction" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("includeSubCategoriesAction" + thisSuffixSeq)) {
	    	                	includeSubCategoriesAction = (String) paramMap.remove("includeSubCategoriesAction" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("productPromoActionEnumId" + thisSuffixSeq)) {
	    	                	productPromoActionEnumId = (String) paramMap.remove("productPromoActionEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("quantity" + thisSuffixSeq)) {
	    	                    quantityStr = (String) paramMap.remove("quantity" + thisSuffixSeq);
	    	                }
	    	                if (UtilValidate.isNotEmpty(quantityStr)) {
	    	                	// parse the quantity
	    	                    try {
	    	                        quantity = new BigDecimal(quantityStr);
	    	                    } catch (Exception e) {
	    	                        Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
	    	                        quantity = BigDecimal.ZERO;
	    	                    }
	    	                }
	    	                
	    	                // get the selected amount
	    	                if (paramMap.containsKey("amount" + thisSuffixSeq)) {
	    	                	amountStr = (String) paramMap.remove("amount" + thisSuffixSeq);
	    	                }
	    	
	    	                // parse the amount
	    	                if (UtilValidate.isNotEmpty(amountStr)) {
	    	                    try {
	    	                        amount = new BigDecimal(amountStr);
	    	                    } catch (Exception e) {
	    	                        Debug.logWarning(e, "Problem parsing amount string: " + amountStr, module);
	    	                        amount = null;
	    	                    }
	    	                }
	    	                
	    	                if (isRuleExisted && isActionExisted) {
		    	                if (quantity != null || amount != null) {
		    	                    //Debug.logInfo("Attempting to add to cart with productId = ", module);
		    	                	Map<String, Object> result3 = dispatcher.runSync("updateProductPromoActionDelys", 
		    	                    		UtilMisc.<String, Object>toMap("productPromoId", productPromoId, 
		    		                				"productPromoRuleId", productPromoRuleId, "productPromoActionSeqId", productPromoActionSeqId, 
		    		                				"productIdListAction", productIdListAction, "orderAdjustmentTypeId", orderAdjustmentTypeId, 
		    	                    				"productPromoApplEnumId", productPromoApplEnumIdAction, "includeSubCategories", includeSubCategoriesAction, 
		    	                    				"productCatIdListAction", productCatIdListAction, "productPromoActionEnumId", productPromoActionEnumId, 
		    	                    				"quantity", quantity, "amount", amount, "userLogin", userLogin, "locale", locale));
		    	                    // no values for price and paramMap (a context for adding attributes)
		    	                    controlDirective = processResult(result3, request);
		    	                    if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
		    	                    	try {
		    	                            TransactionUtil.rollback(beganTx, "Failure in processing Create product promo action callback", null);
		    	                        } catch (Exception e1) {
		    	                            Debug.logError(e1, module);
		    	                        }
		    	                        return "error";
		    	                    }
		    	                } else {
	    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"DAApplyValueOfActionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoActionSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
		    	                }
		    	                
		    	                List<GenericValue> tobeStoredAction = new LinkedList<GenericValue>();
		    	                List<GenericValue> tobeDeletedAction = new LinkedList<GenericValue>();
		    	                List<GenericValue> listProductPromoProductCond = EntityUtil.filterByAnd(listProductPromoProduct, 
		    	                		UtilMisc.toMap("productPromoRuleId", productPromoRuleId, "productPromoActionSeqId", productPromoActionSeqId));
		    	                if (listProductPromoProductCond != null) {
		    	                	List<String> productIdAppls = EntityUtil.getFieldListFromEntityList(listProductPromoProductCond, "productId", true);
		    	                	for (GenericValue productAppl : listProductPromoProductCond) {
		    	                		if (productIdListAction.contains(productAppl.getString("productId"))) {
	    	                				// no action
		    	                		} else {
		    	                			// this record was deleted
		    	                			tobeDeletedAction.add(productAppl);
		    	                		}
		    	                	}
		    	                	for (String productId : productIdListAction) {
		    	                		if (productIdAppls.contains(productId)) {
		    	                			// no action
		    	                		} else {
		    	                			// create new
		    	                			GenericValue newProductPromoProduct = delegator.makeValue("ProductPromoProduct");
		    	                			newProductPromoProduct.put("productPromoId", productPromoId);
		    	                			newProductPromoProduct.put("productPromoRuleId", productPromoRuleId);
		    	                			newProductPromoProduct.put("productPromoActionSeqId", productPromoActionSeqId);
		    	                			newProductPromoProduct.put("productPromoCondSeqId", "_NA_");
		    	                			newProductPromoProduct.put("productId", productId);
		    	                			newProductPromoProduct.put("productPromoApplEnumId", productPromoApplEnumIdAction);
		    	            				tobeStoredAction.add(newProductPromoProduct);
		    	                		}
		    	                	}
		    	                }
		    	                List<GenericValue> listProductPromoCategoryCond = EntityUtil.filterByAnd(listProductPromoCategory, 
		    	                		UtilMisc.toMap("productPromoRuleId", productPromoRuleId, "productPromoActionSeqId", productPromoActionSeqId));
		    	                if (listProductPromoCategoryCond != null) {
		    	                	List<String> categoryIdAppls = EntityUtil.getFieldListFromEntityList(listProductPromoCategoryCond, "productCategoryId", true);
		    	                	for (GenericValue categoryAppl : listProductPromoCategoryCond) {
		    	                		if (productCatIdListAction.contains(categoryAppl.getString("productCategoryId"))) {
	    	                				// no action
		    	                		} else {
		    	                			// this record was deleted
		    	                			tobeDeletedAction.add(categoryAppl);
		    	                		}
		    	                	}
		    	                	for (String productCategoryId : productCatIdListAction) {
		    	                		if (categoryIdAppls.contains(productCategoryId)) {
		    	                			// no action
		    	                		} else {
		    	                			// create new
		    	                			GenericValue newProductPromoCategory = delegator.makeValue("ProductPromoCategory");
		    	                			newProductPromoCategory.put("productPromoId", productPromoId);
		    	                			newProductPromoCategory.put("productPromoRuleId", productPromoRuleId);
		    	                			newProductPromoCategory.put("productPromoActionSeqId", productPromoActionSeqId);
		    	                			newProductPromoCategory.put("productPromoCondSeqId", "_NA_");
		    	                			newProductPromoCategory.put("productCategoryId", productCategoryId);
		    	                			newProductPromoCategory.put("andGroupId", "_NA_");
		    	                			newProductPromoCategory.put("productPromoApplEnumId", productPromoApplEnumIdAction);
		    	                			newProductPromoCategory.put("includeSubCategories", includeSubCategoriesAction);
		    	            				tobeStoredAction.add(newProductPromoCategory);
		    	                		}
		    	                	}
		    	                }
		    	                delegator.storeAll(tobeStoredAction);
		    	                delegator.removeAll(tobeDeletedAction);
	    	                } else {
	    	                	if (quantity != null || amount != null) {
		    	                    //Debug.logInfo("Attempting to add to cart with productId = ", module);
		    	                	Map<String, Object> result3 = dispatcher.runSync("createProductPromoActionDelys", 
		    	                    		UtilMisc.<String, Object>toMap("productPromoId", productPromoId, 
		    		                				"productPromoRuleId", productPromoRuleId, "productIdListAction", productIdListAction, 
		    		                				"orderAdjustmentTypeId", orderAdjustmentTypeId, 
		    	                    				"productPromoApplEnumId", productPromoApplEnumIdAction, "includeSubCategories", includeSubCategoriesAction, 
		    	                    				"productCatIdListAction", productCatIdListAction, "productPromoActionEnumId", productPromoActionEnumId, 
		    	                    				"quantity", quantity, "amount", amount, "userLogin", userLogin, "locale", locale));
		    	                    // no values for price and paramMap (a context for adding attributes)
		    	                    controlDirective = processResult(result3, request);
		    	                    if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
		    	                    	try {
		    	                            TransactionUtil.rollback(beganTx, "Failure in processing Create product promo action callback", null);
		    	                        } catch (Exception e1) {
		    	                            Debug.logError(e1, module);
		    	                        }
		    	                        return "error";
		    	                    }
		    	                } else {
	    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"DAApplyValueOfActionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoActionSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
		    	                }
	    	                }
	    	        	}
	    	        }
	            }
	        }
        } catch (Exception e) {
            try {
                TransactionUtil.rollback(beganTx, e.getMessage(), e);
            } catch (Exception e1) {
                Debug.logError(e1, module);
            }
            errMsgList.add(UtilProperties.getMessage(resource_error, "DAErrorWhenProcessing", locale));
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        } catch (Throwable t) {
            Debug.logError(t, module);
            request.setAttribute("_ERROR_MESSAGE_", t.getMessage());
            try {
                TransactionUtil.rollback(beganTx, t.getMessage(), t);
            } catch (Exception e2) {
                Debug.logError(e2, module);
            }
            errMsgList.add(UtilProperties.getMessage(resource_error, "DAErrorWhenProcessing", locale));
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        } finally {
            // commit the transaction
            try {
                TransactionUtil.commit(beganTx);
            } catch (Exception e) {
                Debug.logError(e, module);
            }
        }
        if (UtilValidate.isNotEmpty(errMsgList)) {
        	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
        	return "error";
        }
        request.setAttribute("productPromoId", productPromoId);
        return "success";
    }
    
    @SuppressWarnings("unchecked")
	public static String createSalesPolicyAdvance(HttpServletRequest request, HttpServletResponse response) {
        //Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        
        if (!security.hasPermission("SALES_POLICY_CREATE", session) && !security.hasPermission("SALES_POLICY_ADMIN", session)) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAYouHavenotCreatePermission", locale));
        	return "error";
        }
        
        // Get parameter information general
        String salesPolicyId = request.getParameter("salesPolicyId");
        String policyName = request.getParameter("policyName");
        String[] roleTypeIdsStr = request.getParameterValues("roleTypeIds");
        String policyText = request.getParameter("policyText");
        String fromDateStr = request.getParameter("fromDate");
        String thruDateStr = request.getParameter("thruDate");
        String showToCustomer = request.getParameter("showToCustomer");
        String[] geoIdsIncludeStr = request.getParameterValues("geoIdsInclude");
        String[] geoIdsExcludeStr = request.getParameterValues("geoIdsExclude");
        /*
        String paymentMethod = request.getParameter("paymentMethod");
        String[] productStoreIds = request.getParameterValues("productStoreIds");
        String promoSalesTargets = request.getParameter("promoSalesTargets");
        String budgetId = request.getParameter("budgetId");
        String miniRevenueId = request.getParameter("miniRevenueId");
        */
        
        Timestamp fromDate = null;
        Timestamp thruDate = null;
        try {
	        if (UtilValidate.isNotEmpty(fromDateStr)) {
	        	Long fromDateL = Long.parseLong(fromDateStr);
	        	fromDate = new Timestamp(fromDateL);
	        }
	        if (UtilValidate.isNotEmpty(thruDateStr)) {
	        	Long thruDateL = Long.parseLong(thruDateStr);
	        	thruDate = new Timestamp(thruDateL);
	        }
        } catch (Exception e) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAErrorWhenFormatDateTime", locale));
        	return "error";
        }
        
        List<String> geoIdsInclude = null;
        List<String> geoIdsExclude = null;
        if (UtilValidate.isNotEmpty(geoIdsIncludeStr)) {
        	geoIdsInclude = Arrays.asList(geoIdsIncludeStr);
        }
        if (UtilValidate.isNotEmpty(geoIdsExcludeStr)) {
        	geoIdsExclude = Arrays.asList(geoIdsExcludeStr);
        }
        
        List<Object> errMsgList = FastList.newInstance();
        
        boolean beganTx = false;
        String salesPolicyIdSuccess = "";
        try {
        	// begin the transaction
        	beganTx = TransactionUtil.begin(7200);
        	String controlDirective = null;
        	List<String> roleTypeIds = Arrays.asList(roleTypeIdsStr);
        	/*if (UtilValidate.isNotEmpty(roleTypeIdsStr)) {
        		for (String roleTypeId : roleTypeIdsStr) {
        			roleTypeIds.add(roleTypeId);
				}
        	}*/
        	Map<String, Object> contextMap = FastMap.newInstance();
        	contextMap.put("salesPolicyId", salesPolicyId);
        	contextMap.put("policyName", policyName);
        	contextMap.put("roleTypeIds", roleTypeIds);
        	contextMap.put("policyText", policyText);
        	contextMap.put("fromDate", fromDate);
        	contextMap.put("thruDate", thruDate);
        	contextMap.put("showToCustomer", showToCustomer);
        	contextMap.put("geoIdsInclude", geoIdsInclude);
        	contextMap.put("geoIdsExclude", geoIdsExclude);
        	contextMap.put("userLogin", userLogin);
        	contextMap.put("locale", locale);
        	/*
        	contextMap.put("paymentMethod", paymentMethod);
        	contextMap.put("productStoreIds", productStoreIds);
        	contextMap.put("promoSalesTargets", promoSalesTargets);
        	contextMap.put("budgetId", budgetId);
        	contextMap.put("miniRevenueId", miniRevenueId);
        	*/
        	Map<String, Object> result0 = dispatcher.runSync("createSalesPolicy", contextMap);
        	// no values for price and paramMap (a context for adding attributes)
            controlDirective = processResult(result0, request);
            if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
            	try {
                    TransactionUtil.rollback(beganTx, "Failure in processing Create sales policy callback", null);
                } catch (Exception e1) {
                    Debug.logError(e1, module);
                }
                return "error";
            }
            
            salesPolicyIdSuccess = (String) result0.get("salesPolicyId");
            
	        // Get the parameters as a MAP, remove the productId and quantity params.
	        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	        
	        // The number of multi form rows is retrieved
	        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	        if (rowCount < 1) {
	            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
	        } else {
	            for (int i = 0; i < rowCount; i++) {
	            	// process list rule (condition, action)
	    	        String ruleName = null;
	    	        
	    	        controlDirective = null;                // re-initialize each time
	                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	                
	                // get the productId
	                if (paramMap.containsKey("ruleName" + thisSuffix)) {
	                	ruleName = (String) paramMap.remove("ruleName" + thisSuffix);
	                }
	                
	                String salesStatementTypeId = null;
	                String paymentParty = null;
	                if (paramMap.containsKey("salesStatementTypeId" + thisSuffix)) {
	                	salesStatementTypeId = (String) paramMap.remove("salesStatementTypeId" + thisSuffix);
	                }
	                if (paramMap.containsKey("paymentParty" + thisSuffix)) {
	                	paymentParty = (String) paramMap.remove("paymentParty" + thisSuffix);
	                }
	                
	                if (UtilValidate.isEmpty(ruleName) || UtilValidate.isEmpty(salesStatementTypeId) || UtilValidate.isEmpty(paymentParty)) {
	                	continue;
	                }
	                
	                String salesPolicyRuleId = "";
	                Map<String, Object> result1 = dispatcher.runSync("createSalesPolicyRule", 
	                		UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyIdSuccess, "ruleName", ruleName, "salesStatementTypeId", salesStatementTypeId, "paymentParty", paymentParty, "userLogin", userLogin, "locale", locale));
	                // no values for price and paramMap (a context for adding attributes)
	                controlDirective = processResult(result1, request);
	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	                	try {
	                        TransactionUtil.rollback(beganTx, "Failure in processing Create sales policy rule callback", null);
	                    } catch (Exception e1) {
	                        Debug.logError(e1, module);
	                    }
	                    return "error";
	                }
	                salesPolicyRuleId = (String) result1.get("salesPolicyRuleId");
	                
	                // The number of multi form rows is retrieved: Condition
	    	        int rowCountSeqCond = getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_COND);
	    	        if (rowCountSeqCond < 1) {
	    	            Debug.logWarning("No rows seq to process, as rowCountSeqCondition = " + rowCountSeqCond, module);
	    	        } else {
	    	        	for (int j = 0; j < rowCountSeqCond; j++) {
	    	        		String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_COND + j + UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	    	        		
	    	        		List<String> productIdListCond = FastList.newInstance();
	    	    	        String salesPolicyApplEnumId = null;
	    	    	        String includeSubCategories = null;
	    	    	        List<String> productCatIdListCond = FastList.newInstance();
	    	    	        String inputParamEnumId = null;
	    	    	        String operatorEnumId = null;
	    	    	        String condValue = null;
	    	    	        String isRemoveCond = "N";
	    	    	        
	    	    	        if (paramMap.containsKey("isRemoveCond" + thisSuffixSeq)) {
	    	    	        	isRemoveCond = (String) paramMap.remove("isRemoveCond" + thisSuffixSeq);
	    	                }
	    	    	        if ("Y".equals(isRemoveCond)) {
	    	    	        	continue;
	    	    	        }
	    	    	        
	    	    	        if (paramMap.containsKey("productIdListCond" + thisSuffixSeq)) {
	    	                	Object productIdListCondObj = (Object) paramMap.remove("productIdListCond" + thisSuffixSeq);
	    	                	if (productIdListCondObj instanceof String) {
	    	                		productIdListCond.add(productIdListCondObj.toString());
	    	                	} else if (productIdListCondObj instanceof List) {
	    	                		productIdListCond = (List<String>) productIdListCondObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("salesPolicyApplEnumId" + thisSuffixSeq)) {
	    	                	salesPolicyApplEnumId = (String) paramMap.remove("salesPolicyApplEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("includeSubCategories" + thisSuffixSeq)) {
	    	                	includeSubCategories = (String) paramMap.remove("includeSubCategories" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("productCatIdListCond" + thisSuffixSeq)) {
	    	                	Object productCatIdListCondObj = (Object) paramMap.remove("productCatIdListCond" + thisSuffixSeq);
	    	                	if (productCatIdListCondObj instanceof String) {
	    	                		productCatIdListCond.add(productCatIdListCondObj.toString());
	    	                	} else if (productCatIdListCondObj instanceof List) {
	    	                		productCatIdListCond = (List<String>) productCatIdListCondObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("inputParamEnumId" + thisSuffixSeq)) {
	    	                	inputParamEnumId = (String) paramMap.remove("inputParamEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("operatorEnumId" + thisSuffixSeq)) {
	    	                	operatorEnumId = (String) paramMap.remove("operatorEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("condValue" + thisSuffixSeq)) {
	    	                	condValue = (String) paramMap.remove("condValue" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (UtilValidate.isNotEmpty(condValue)) {
	    	                	Map<String, Object> result2 = dispatcher.runSync("createSalesPolicyCond", 
		    	                		UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyIdSuccess, 
		    	                				"salesPolicyRuleId", salesPolicyRuleId, 
		    	                				"productIdListCond", productIdListCond, "salesPolicyApplEnumId", salesPolicyApplEnumId, 
		    	                				"includeSubCategories", includeSubCategories, "productCatIdListCond", productCatIdListCond, 
		    	                				"inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId, 
		    	                				"condValue", condValue, "userLogin", userLogin, "locale", locale));
		    	                // no values for price and paramMap (a context for adding attributes)
		    	                controlDirective = processResult(result2, request);
		    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
		    	                	try {
		    	                        TransactionUtil.rollback(beganTx, "Failure in processing Create product promo condition callback", null);
		    	                    } catch (Exception e1) {
		    	                        Debug.logError(e1, module);
		    	                    }
		    	                    return "error";
		    	                }
	    	                } else {
    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"DAConditionValueOfConditionInRuleMustBeNotEmpty", UtilMisc.toMap("salesPolicyCondSeqId", (j + 1), "salesPolicyRuleId", (i + 1)), locale));
	    	                }
	    	        	}
	    	        }
	    	        
	    	        // The number of multi form rows is retrieved: Action
	    	        int rowCountSeqAction = getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_ACT);
	    	        if (rowCountSeqAction < 1) {
	    	            Debug.logWarning("No rows seq to process, as rowCountSeqAction = " + rowCountSeqAction, module);
	    	        } else {
	    	        	for (int j = 0; j < rowCountSeqAction; j++) {
	    	        		String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_ACT + j + UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	    	    	        
	    	    	        //String orderAdjustmentTypeId = null;
	    	    	        List<String> productIdListAction = FastList.newInstance();
	    	    	        String salesPolicyApplEnumIdAction = null;
	    	    	        String includeSubCategoriesAction = null;
	    	    	        List<String> productCatIdListAction = FastList.newInstance();
	    	    	        String salesPolicyActionEnumId = null;
	    	    	        BigDecimal quantity = null;
	    	    	        String quantityStr = null;
	    	    	        BigDecimal amount = null;
	    	    	        String amountStr = null;
    	    	        	String isRemoveAction = "N";
	    	    	        
	    	    	        if (paramMap.containsKey("isRemoveAction" + thisSuffixSeq)) {
	    	    	        	isRemoveAction = (String) paramMap.remove("isRemoveAction" + thisSuffixSeq);
	    	                }
	    	    	        if ("Y".equals(isRemoveAction)) {
	    	    	        	continue;
	    	    	        }
	    	    	        
	    	                /*if (paramMap.containsKey("orderAdjustmentTypeId" + thisSuffixSeq)) {
	    	                	orderAdjustmentTypeId = (String) paramMap.remove("orderAdjustmentTypeId" + thisSuffixSeq);
	    	                }*/
	    	                
	    	                if (paramMap.containsKey("productIdListAction" + thisSuffixSeq)) {
	    	                	Object productIdListActionObj = (Object) paramMap.remove("productIdListAction" + thisSuffixSeq);
	    	                	if (productIdListActionObj instanceof String) {
	    	                		productIdListAction.add(productIdListActionObj.toString());
	    	                	} else if (productIdListActionObj instanceof List) {
	    	                		productIdListAction = (List<String>) productIdListActionObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("productCatIdListAction" + thisSuffixSeq)) {
	    	                	Object productCatIdListActionObj = (Object) paramMap.remove("productCatIdListAction" + thisSuffixSeq);
	    	                	if (productCatIdListActionObj instanceof String) {
	    	                		productCatIdListAction.add(productCatIdListActionObj.toString());
	    	                	} else if (productCatIdListActionObj instanceof List) {
	    	                		productCatIdListAction = (List<String>) productCatIdListActionObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("salesPolicyApplEnumIdAction" + thisSuffixSeq)) {
	    	                	salesPolicyApplEnumIdAction = (String) paramMap.remove("salesPolicyApplEnumIdAction" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("includeSubCategoriesAction" + thisSuffixSeq)) {
	    	                	includeSubCategoriesAction = (String) paramMap.remove("includeSubCategoriesAction" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("salesPolicyActionEnumId" + thisSuffixSeq)) {
	    	                	salesPolicyActionEnumId = (String) paramMap.remove("salesPolicyActionEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("quantity" + thisSuffixSeq)) {
	    	                    quantityStr = (String) paramMap.remove("quantity" + thisSuffixSeq);
	    	                }
	    	                if (UtilValidate.isNotEmpty(quantityStr)) {
	    	                	// parse the quantity
	    	                    try {
	    	                        quantity = new BigDecimal(quantityStr);
	    	                    } catch (Exception e) {
	    	                        Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
	    	                        quantity = BigDecimal.ZERO;
	    	                    }
	    	                }
	    	                
	    	                // get the selected amount
	    	                if (paramMap.containsKey("amount" + thisSuffixSeq)) {
	    	                	amountStr = (String) paramMap.remove("amount" + thisSuffixSeq);
	    	                }
	    	
	    	                // parse the amount
	    	                if (UtilValidate.isNotEmpty(amountStr)) {
	    	                    try {
	    	                        amount = new BigDecimal(amountStr);
	    	                    } catch (Exception e) {
	    	                        Debug.logWarning(e, "Problem parsing amount string: " + amountStr, module);
	    	                        amount = null;
	    	                    }
	    	                }
	    	                
	    	                if (quantity != null || amount != null) {
	    	                    //Debug.logInfo("Attempting to add to cart with productId = ", module);
	    	                	Map<String, Object> result3 = dispatcher.runSync("createSalesPolicyAction", 
	    	                    		UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyIdSuccess, 
	    		                				"salesPolicyRuleId", salesPolicyRuleId, "productIdListAction", productIdListAction, 
	    	                    				"salesPolicyApplEnumId", salesPolicyApplEnumIdAction, "includeSubCategories", includeSubCategoriesAction, 
	    	                    				"productCatIdListAction", productCatIdListAction, "salesPolicyActionEnumId", salesPolicyActionEnumId, 
	    	                    				"quantity", quantity, "amount", amount, "userLogin", userLogin, "locale", locale));
	    	                	//"orderAdjustmentTypeId", orderAdjustmentTypeId, 
	    	                    // no values for price and paramMap (a context for adding attributes)
	    	                    controlDirective = processResult(result3, request);
	    	                    if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	    	                    	try {
	    	                            TransactionUtil.rollback(beganTx, "Failure in processing Create product promo action callback", null);
	    	                        } catch (Exception e1) {
	    	                            Debug.logError(e1, module);
	    	                        }
	    	                        return "error";
	    	                    }
	    	                } else {
    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"DAApplyValueOfActionInRuleMustBeNotEmpty", UtilMisc.toMap("salesPolicyActionSeqId", (j + 1), "salesPolicyRuleId", (i + 1)), locale));
	    	                }
	    	        	}
	    	        }
	            }
	        }
        } catch (Exception e) {
            try {
                TransactionUtil.rollback(beganTx, e.getMessage(), e);
            } catch (Exception e1) {
                Debug.logError(e1, module);
            }
            errMsgList.add(UtilProperties.getMessage(resource_error, "DAErrorWhenProcessing", locale));
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        } catch (Throwable t) {
            Debug.logError(t, module);
            request.setAttribute("_ERROR_MESSAGE_", t.getMessage());
            try {
                TransactionUtil.rollback(beganTx, t.getMessage(), t);
            } catch (Exception e2) {
                Debug.logError(e2, module);
            }
            errMsgList.add(UtilProperties.getMessage(resource_error, "DAErrorWhenProcessing", locale));
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        } finally {
            // commit the transaction
            try {
                TransactionUtil.commit(beganTx);
            } catch (Exception e) {
                Debug.logError(e, module);
            }
        }
        if (UtilValidate.isNotEmpty(errMsgList)) {
        	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
        	return "error";
        }
        request.setAttribute("salesPolicyId", salesPolicyIdSuccess);
        return "success";
    }
    
    @SuppressWarnings("unchecked")
	public static String updateSalesPolicyAdvance(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        HttpSession session = request.getSession();
        Security security = (Security) request.getAttribute("security");
        GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
        
        if (!security.hasPermission("SALES_POLICY_UPDATE", session) && !security.hasPermission("SALES_POLICY_ADMIN", session)) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAYouHavenotUpdatePermission", locale));
        	return "error";
        }
        
        // Get parameter information general
        String salesPolicyId = request.getParameter("salesPolicyId");
        String policyName = request.getParameter("policyName");
        String[] roleTypeIdsStr = request.getParameterValues("roleTypeIds");
        String policyText = request.getParameter("policyText");
        String fromDateStr = request.getParameter("fromDate");
        String thruDateStr = request.getParameter("thruDate");
        String showToCustomer = request.getParameter("showToCustomer");
        String[] geoIdsIncludeStr = request.getParameterValues("geoIdsInclude");
        String[] geoIdsExcludeStr = request.getParameterValues("geoIdsExclude");
        
        Timestamp fromDate = null;
        Timestamp thruDate = null;
        try {
	        if (UtilValidate.isNotEmpty(fromDateStr)) {
	        	Long fromDateL = Long.parseLong(fromDateStr);
	        	fromDate = new Timestamp(fromDateL);
	        }
	        if (UtilValidate.isNotEmpty(thruDateStr)) {
	        	Long thruDateL = Long.parseLong(thruDateStr);
	        	thruDate = new Timestamp(thruDateL);
	        }
        } catch (Exception e) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAErrorWhenFormatDateTime", locale));
        	return "error";
        }
        
        if (UtilValidate.isEmpty(salesPolicyId)) {
        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DAPolicyIdMustNotBeEmpty", locale));
        	return "error";
        }
        
        List<String> geoIdsInclude = null;
        List<String> geoIdsExclude = null;
        if (UtilValidate.isNotEmpty(geoIdsIncludeStr)) {
        	geoIdsInclude = Arrays.asList(geoIdsIncludeStr);
        }
        if (UtilValidate.isNotEmpty(geoIdsExcludeStr)) {
        	geoIdsExclude = Arrays.asList(geoIdsExcludeStr);
        }
        
        List<Object> errMsgList = FastList.newInstance();
        
        boolean beganTx = false;
        try {
        	GenericValue salesPolicy = delegator.findOne("SalesPolicy", UtilMisc.toMap("salesPolicyId", salesPolicyId), false);
        	if (salesPolicy == null) {
        		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource_error, "DASalesPolicyNotFoundHasId", locale) + salesPolicyId);
                return "error";
        	}
        	
        	// begin the transaction
        	beganTx = TransactionUtil.begin(7200);
        	String controlDirective = null;
        	List<String> roleTypeIds = Arrays.asList(roleTypeIdsStr);
        	
        	Map<String, Object> contextMap = FastMap.newInstance();
        	contextMap.put("salesPolicyId", salesPolicyId);
        	contextMap.put("policyName", policyName);
        	//contextMap.put("roleTypeIds", roleTypeIds);
        	contextMap.put("policyText", policyText);
        	contextMap.put("fromDate", fromDate);
        	contextMap.put("thruDate", thruDate);
        	contextMap.put("showToCustomer", showToCustomer);
        	contextMap.put("userLogin", userLogin);
        	contextMap.put("locale", locale);
        	
        	Map<String, Object> result0 = dispatcher.runSync("updateSalesPolicy", contextMap);
        	// no values for price and paramMap (a context for adding attributes)
            controlDirective = processResult(result0, request);
            if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
            	try {
                    TransactionUtil.rollback(beganTx, "Failure in processing Create sales policy callback", null);
                } catch (Exception e1) {
                    Debug.logError(e1, module);
                }
                return "error";
            }
            
            Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
            
            List<GenericValue> tobeStored = new LinkedList<GenericValue>();
        	// contextMap.put("geoIdsInclude", geoIdsInclude);
        	// contextMap.put("geoIdsExclude", geoIdsExclude);
            List<GenericValue> listSalesPolicyGeoAppl = delegator.findByAnd("SalesPolicyGeoAppl", UtilMisc.toMap("salesPolicyId", salesPolicyId), null, false);
            List<GenericValue> listSalesPolicyGeoApplInclude = EntityUtil.filterByAnd(listSalesPolicyGeoAppl, UtilMisc.toMap("salesPolicyGeoApplEnumId", "SPPA_INCLUDE"));
            List<GenericValue> listSalesPolicyGeoApplExclude = EntityUtil.filterByAnd(listSalesPolicyGeoAppl, UtilMisc.toMap("salesPolicyGeoApplEnumId", "SPPA_EXCLUDE"));
            if (listSalesPolicyGeoApplInclude != null) {
            	List<String> productStoreIdAppls = EntityUtil.getFieldListFromEntityList(listSalesPolicyGeoApplInclude, "geoId", true);
            	for (GenericValue salesPolicyGeoAppl : listSalesPolicyGeoApplInclude) {
            		if (geoIdsInclude.contains(salesPolicyGeoAppl.getString("geoId"))) {
            			if (fromDate.equals(salesPolicyGeoAppl.getTimestamp("fromDate"))) {
            				// no action
            			} else {
            				// thruDate this productStorePromoAppl record + create new record with fromDate
            				salesPolicyGeoAppl.put("thruDate", nowTimestamp);
            				tobeStored.add(salesPolicyGeoAppl);
            				GenericValue newSalesPolicyGeoAppl = delegator.makeValue("SalesPolicyGeoAppl");
            				newSalesPolicyGeoAppl.put("geoId", salesPolicyGeoAppl.get("geoId"));
            				newSalesPolicyGeoAppl.put("salesPolicyId", salesPolicyGeoAppl.get("salesPolicyId"));
            				newSalesPolicyGeoAppl.put("fromDate", fromDate);
            				tobeStored.add(newSalesPolicyGeoAppl);
            			}
            		} else {
            			// this record was deleted
            			salesPolicyGeoAppl.put("thruDate", nowTimestamp);
        				tobeStored.add(salesPolicyGeoAppl);
            		}
            	}
            	if (UtilValidate.isNotEmpty(geoIdsInclude)) {
            		for (String geoId : geoIdsInclude) {
                		if (productStoreIdAppls.contains(geoId)) {
                			// no action
                		} else {
                			// create new
                			GenericValue newProductStorePromoAppl = delegator.makeValue("SalesPolicyGeoAppl");
            				newProductStorePromoAppl.put("geoId", geoId);
            				newProductStorePromoAppl.put("salesPolicyId", salesPolicyId);
            				newProductStorePromoAppl.put("fromDate", fromDate);
            				tobeStored.add(newProductStorePromoAppl);
                		}
                	}
            	}
            }
            if (listSalesPolicyGeoApplExclude != null) {
            	List<String> productStoreIdAppls = EntityUtil.getFieldListFromEntityList(listSalesPolicyGeoApplExclude, "geoId", true);
            	for (GenericValue salesPolicyGeoAppl : listSalesPolicyGeoApplExclude) {
            		if (geoIdsExclude.contains(salesPolicyGeoAppl.getString("geoId"))) {
            			if (fromDate.equals(salesPolicyGeoAppl.getTimestamp("fromDate"))) {
            				// no action
            			} else {
            				// thruDate this productStorePromoAppl record + create new record with fromDate
            				salesPolicyGeoAppl.put("thruDate", nowTimestamp);
            				tobeStored.add(salesPolicyGeoAppl);
            				GenericValue newSalesPolicyGeoAppl = delegator.makeValue("SalesPolicyGeoAppl");
            				newSalesPolicyGeoAppl.put("geoId", salesPolicyGeoAppl.get("geoId"));
            				newSalesPolicyGeoAppl.put("salesPolicyId", salesPolicyGeoAppl.get("salesPolicyId"));
            				newSalesPolicyGeoAppl.put("fromDate", fromDate);
            				tobeStored.add(newSalesPolicyGeoAppl);
            			}
            		} else {
            			// this record was deleted
            			salesPolicyGeoAppl.put("thruDate", nowTimestamp);
        				tobeStored.add(salesPolicyGeoAppl);
            		}
            	}
            	if (UtilValidate.isNotEmpty(geoIdsExclude)) {
            		for (String geoId : geoIdsExclude) {
                		if (productStoreIdAppls.contains(geoId)) {
                			// no action
                		} else {
                			// create new
                			GenericValue newProductStorePromoAppl = delegator.makeValue("SalesPolicyGeoAppl");
            				newProductStorePromoAppl.put("geoId", geoId);
            				newProductStorePromoAppl.put("salesPolicyId", salesPolicyId);
            				newProductStorePromoAppl.put("fromDate", fromDate);
            				tobeStored.add(newProductStorePromoAppl);
                		}
                	}
            	}
            }
            
            // update list role type promo appl
            List<GenericValue> listRoleTypePolicyAppl = delegator.findByAnd("SalesPolicyRoleTypeApply", UtilMisc.toMap("salesPolicyId", salesPolicyId), null, false);
            if (listRoleTypePolicyAppl != null) {
            	List<String> roleTypeIdAppls = EntityUtil.getFieldListFromEntityList(listRoleTypePolicyAppl, "roleTypeId", true);
            	for (GenericValue roleTypePolicyAppl : listRoleTypePolicyAppl) {
            		if (roleTypeIds.contains(roleTypePolicyAppl.getString("roleTypeId"))) {
            			if (fromDate.equals(roleTypePolicyAppl.getTimestamp("fromDate"))) {
            				// no action
            			} else {
            				// thruDate this salesPolicyRoleTypeAppl record + create new record with fromDate
            				roleTypePolicyAppl.put("thruDate", nowTimestamp);
            				tobeStored.add(roleTypePolicyAppl);
            				GenericValue newSalesPolicyRoleTypeAppl = delegator.makeValue("SalesPolicyRoleTypeApply");
            				newSalesPolicyRoleTypeAppl.put("roleTypeId", roleTypePolicyAppl.get("roleTypeId"));
            				newSalesPolicyRoleTypeAppl.put("salesPolicyId", roleTypePolicyAppl.get("salesPolicyId"));
            				newSalesPolicyRoleTypeAppl.put("fromDate", fromDate);
            				tobeStored.add(newSalesPolicyRoleTypeAppl);
            			}
            		} else {
            			// this record was deleted
            			roleTypePolicyAppl.put("thruDate", nowTimestamp);
        				tobeStored.add(roleTypePolicyAppl);
            		}
            	}
            	for (String roleTypeId : roleTypeIds) {
            		if (roleTypeIdAppls.contains(roleTypeId)) {
            			// no action
            		} else {
            			// create new
            			GenericValue newProductPromoRoleTypeAppl = delegator.makeValue("SalesPolicyRoleTypeApply");
        				newProductPromoRoleTypeAppl.put("roleTypeId", roleTypeId);
        				newProductPromoRoleTypeAppl.put("salesPolicyId", salesPolicyId);
        				newProductPromoRoleTypeAppl.put("fromDate", fromDate);
        				tobeStored.add(newProductPromoRoleTypeAppl);
            		}
            	}
            }
            delegator.storeAll(tobeStored);
            
            
            List<GenericValue> listSalesPolicyProduct = delegator.findByAnd("SalesPolicyProduct", UtilMisc.toMap("salesPolicyId", salesPolicyId), null, false);
            List<GenericValue> listSalesPolicyCategory = delegator.findByAnd("SalesPolicyCategory", UtilMisc.toMap("salesPolicyId", salesPolicyId), null, false);
            
	        // Get the parameters as a MAP, remove the productId and quantity params.
	        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
	        
	        // The number of multi form rows is retrieved
	        int rowCount = UtilHttp.getMultiFormRowCount(paramMap);
	        if (rowCount < 1) {
	            Debug.logWarning("No rows to process, as rowCount = " + rowCount, module);
	        } else {
	            for (int i = 0; i < rowCount; i++) {
	            	// process list rule (condition, action)
	    	        String ruleName = null;
	    	        String salesPolicyRuleId = "";
	    	        boolean isRuleExisted = false;
	    	        String isRemoveRule = "N";
	    	        
	    	        controlDirective = null;                // re-initialize each time
	                String thisSuffix = UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	                
	                // get the ruleName
	                if (paramMap.containsKey("ruleName" + thisSuffix)) {
	                	ruleName = (String) paramMap.remove("ruleName" + thisSuffix);
	                }
	                // get the ruleId
	                if (paramMap.containsKey("salesPolicyRuleId" + thisSuffix)) {
	                	salesPolicyRuleId = (String) paramMap.remove("salesPolicyRuleId" + thisSuffix);
	                }
	                if (paramMap.containsKey("isRemoveRule" + thisSuffix)) {
	                	isRemoveRule = (String) paramMap.remove("isRemoveRule" + thisSuffix);
	                }

	                GenericValue salesPolicyRule = delegator.findOne("SalesPolicyRule", UtilMisc.toMap("salesPolicyId", salesPolicyId, "salesPolicyRuleId", salesPolicyRuleId), false);
	                if (salesPolicyRule != null) {
	                	isRuleExisted = true;
	                }
	                
	                if ("Y".equals(isRemoveRule)) {
	                	if (isRuleExisted) {
	                		// delete condition
	    	        		Map<String, Object> result2 = dispatcher.runSync("deleteSalesPolicyRule", UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyId, "salesPolicyRuleId", salesPolicyRuleId, "userLogin", userLogin, "locale", locale));
	    	                // no values for price and paramMap (a context for adding attributes)
	    	                controlDirective = processResult(result2, request);
	    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	    	                	try {
	    	                        TransactionUtil.rollback(beganTx, "Failure in processing Create product promo condition callback", null);
	    	                    } catch (Exception e1) {
	    	                        Debug.logError(e1, module);
	    	                    }
	    	                    return "error";
	    	                }
	    	                continue;
	                	} else {
	                		continue;
	                	}
	                }
	                
	                if (UtilValidate.isEmpty(ruleName)) {
	                	continue;
	                }
	                
	                String salesStatementTypeId = null;
	                String paymentParty = null;
	                if (paramMap.containsKey("salesStatementTypeId" + thisSuffix)) {
	                	salesStatementTypeId = (String) paramMap.remove("salesStatementTypeId" + thisSuffix);
	                }
	                if (paramMap.containsKey("paymentParty" + thisSuffix)) {
	                	paymentParty = (String) paramMap.remove("paymentParty" + thisSuffix);
	                }
	                
	                if (UtilValidate.isEmpty(ruleName) || UtilValidate.isEmpty(salesStatementTypeId) || UtilValidate.isEmpty(paymentParty)) {
	                	continue;
	                }
	                
	                Map<String, Object> result1 = null;
	                if (isRuleExisted) {
	                	result1 = dispatcher.runSync("updateSalesPolicyRule", 
		                		UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyId, "salesPolicyRuleId", salesPolicyRuleId, 
		                				"ruleName", ruleName, "salesStatementTypeId", salesStatementTypeId, "paymentParty", paymentParty, "userLogin", userLogin, "locale", locale));
	                } else {
		                result1 = dispatcher.runSync("createSalesPolicyRule", 
		                		UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyId, "ruleName", ruleName, "salesStatementTypeId", salesStatementTypeId, "paymentParty", paymentParty, "userLogin", userLogin, "locale", locale));
		                if (result1 != null) salesPolicyRuleId = (String) result1.get("salesPolicyRuleId");
	                }
	                // no values for price and paramMap (a context for adding attributes)
	                controlDirective = processResult(result1, request);
	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
	                	try {
	                        TransactionUtil.rollback(beganTx, "Failure in processing Create sales policy rule callback", null);
	                    } catch (Exception e1) {
	                        Debug.logError(e1, module);
	                    }
	                    return "error";
	                }
	                
	                // The number of multi form rows is retrieved: Condition
	    	        int rowCountSeqCond = getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_COND);
	    	        if (rowCountSeqCond < 1) {
	    	            Debug.logWarning("No rows seq to process, as rowCountSeqCondition = " + rowCountSeqCond, module);
	    	        } else {
	    	        	for (int j = 0; j < rowCountSeqCond; j++) {
	    	        		String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_COND + j + UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	    	        		String salesPolicyCondSeqId = null;
	    	        		List<String> productIdListCond = FastList.newInstance();
	    	    	        String salesPolicyApplEnumId = null;
	    	    	        String includeSubCategories = null;
	    	    	        List<String> productCatIdListCond = FastList.newInstance();
	    	    	        String inputParamEnumId = null;
	    	    	        String operatorEnumId = null;
	    	    	        String condValue = null;
    	    	        	String isRemoveCond = "N";
    	    	        	
	    	    	        if (paramMap.containsKey("isRemoveCond" + thisSuffixSeq)) {
	    	    	        	isRemoveCond = (String) paramMap.remove("isRemoveCond" + thisSuffixSeq);
	    	                }
	    	    	        if (paramMap.containsKey("salesPolicyCondSeqId" + thisSuffixSeq)) {
	    	    	        	salesPolicyCondSeqId = (String) paramMap.remove("salesPolicyCondSeqId" + thisSuffixSeq);
	    	                }
	    	    	        
	    	    	        boolean isCondExisted = false;
	    	                GenericValue salesPolicyCond = delegator.findOne("SalesPolicyCond", UtilMisc.toMap("salesPolicyId", salesPolicyId, "salesPolicyRuleId", salesPolicyRuleId, "salesPolicyCondSeqId", salesPolicyCondSeqId), false);
	    	                if (salesPolicyCond != null) isCondExisted = true;
	    	    	        
	    	    	        if ("Y".equals(isRemoveCond)) {
	    	    	        	if (isRuleExisted && isCondExisted) {
		    	                	// delete condition
	    	    	        		Map<String, Object> result2 = dispatcher.runSync("deleteSalesPolicyCond", UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyId, "salesPolicyRuleId", salesPolicyRuleId, "salesPolicyCondSeqId", salesPolicyCondSeqId, "userLogin", userLogin, "locale", locale));
			    	                // no values for price and paramMap (a context for adding attributes)
			    	                controlDirective = processResult(result2, request);
			    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
			    	                	try {
			    	                        TransactionUtil.rollback(beganTx, "Failure in processing Create sales policy condition callback", null);
			    	                    } catch (Exception e1) {
			    	                        Debug.logError(e1, module);
			    	                    }
			    	                    return "error";
			    	                }
			    	                continue;
		    	                } else {
		    	                	continue;
		    	                }
	    	    	        }
	    	    	        
	    	    	        if (paramMap.containsKey("productIdListCond" + thisSuffixSeq)) {
	    	                	Object productIdListCondObj = (Object) paramMap.remove("productIdListCond" + thisSuffixSeq);
	    	                	if (productIdListCondObj instanceof String) {
	    	                		productIdListCond.add(productIdListCondObj.toString());
	    	                	} else if (productIdListCondObj instanceof List) {
	    	                		productIdListCond = (List<String>) productIdListCondObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("salesPolicyApplEnumId" + thisSuffixSeq)) {
	    	                	salesPolicyApplEnumId = (String) paramMap.remove("salesPolicyApplEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("includeSubCategories" + thisSuffixSeq)) {
	    	                	includeSubCategories = (String) paramMap.remove("includeSubCategories" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("productCatIdListCond" + thisSuffixSeq)) {
	    	                	Object productCatIdListCondObj = (Object) paramMap.remove("productCatIdListCond" + thisSuffixSeq);
	    	                	if (productCatIdListCondObj instanceof String) {
	    	                		productCatIdListCond.add(productCatIdListCondObj.toString());
	    	                	} else if (productCatIdListCondObj instanceof List) {
	    	                		productCatIdListCond = (List<String>) productCatIdListCondObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("inputParamEnumId" + thisSuffixSeq)) {
	    	                	inputParamEnumId = (String) paramMap.remove("inputParamEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("operatorEnumId" + thisSuffixSeq)) {
	    	                	operatorEnumId = (String) paramMap.remove("operatorEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("condValue" + thisSuffixSeq)) {
	    	                	condValue = (String) paramMap.remove("condValue" + thisSuffixSeq);
	    	                }
	    	                
	    	                if (isRuleExisted && isCondExisted) {
	    	                	if (UtilValidate.isNotEmpty(condValue)) {
	    	                		Map<String, Object> result2 = dispatcher.runSync("updateSalesPolicyCond", 
			    	                		UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyId, 
			    	                				"salesPolicyRuleId", salesPolicyRuleId, "salesPolicyCondSeqId", salesPolicyCondSeqId, 
			    	                				"inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId, 
			    	                				"condValue", condValue, "userLogin", userLogin, "locale", locale));
	    	                		//"salesPolicyApplEnumId", salesPolicyApplEnumId, "productIdListCond", productIdListCond, "includeSubCategories", includeSubCategories, "productCatIdListCond", productCatIdListCond, 
			    	                // no values for price and paramMap (a context for adding attributes)
			    	                controlDirective = processResult(result2, request);
			    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
			    	                	try {
			    	                        TransactionUtil.rollback(beganTx, "Failure in processing delete sales policy condition callback", null);
			    	                    } catch (Exception e1) {
			    	                        Debug.logError(e1, module);
			    	                    }
			    	                    return "error";
			    	                }
			    	                
			    	                List<GenericValue> tobeStoredCond = new LinkedList<GenericValue>();
			    	                List<GenericValue> tobeDeletedCond = new LinkedList<GenericValue>();
			    	                List<GenericValue> listSalesPolicyProductCond = EntityUtil.filterByAnd(listSalesPolicyProduct, 
			    	                		UtilMisc.toMap("salesPolicyRuleId", salesPolicyRuleId, "salesPolicyCondSeqId", salesPolicyCondSeqId));
			    	                if (listSalesPolicyProductCond != null) {
			    	                	List<String> productIdAppls = EntityUtil.getFieldListFromEntityList(listSalesPolicyProductCond, "productId", true);
			    	                	for (GenericValue productAppl : listSalesPolicyProductCond) {
			    	                		if (productIdListCond.contains(productAppl.getString("productId"))) {
		    	                				// no action
			    	                		} else {
			    	                			// this record was deleted
			    	                			tobeDeletedCond.add(productAppl);
			    	                		}
			    	                	}
			    	                	for (String productId : productIdListCond) {
			    	                		if (productIdAppls.contains(productId)) {
			    	                			// no action
			    	                		} else {
			    	                			// create new
			    	                			GenericValue newSalesPolicyProduct = delegator.makeValue("SalesPolicyProduct");
			    	                			newSalesPolicyProduct.put("salesPolicyId", salesPolicyId);
			    	                			newSalesPolicyProduct.put("salesPolicyRuleId", salesPolicyRuleId);
			    	                			newSalesPolicyProduct.put("salesPolicyActionSeqId", "_NA_");
			    	                			newSalesPolicyProduct.put("salesPolicyCondSeqId", salesPolicyCondSeqId);
			    	                			newSalesPolicyProduct.put("productId", productId);
			    	                			newSalesPolicyProduct.put("salesPolicyApplEnumId", salesPolicyApplEnumId);
			    	            				tobeStoredCond.add(newSalesPolicyProduct);
			    	                		}
			    	                	}
			    	                }
			    	                List<GenericValue> listSalesPolicyCategoryCond = EntityUtil.filterByAnd(listSalesPolicyCategory, 
			    	                		UtilMisc.toMap("salesPolicyRuleId", salesPolicyRuleId, "salesPolicyCondSeqId", salesPolicyCondSeqId));
			    	                if (listSalesPolicyCategoryCond != null) {
			    	                	List<String> categoryIdAppls = EntityUtil.getFieldListFromEntityList(listSalesPolicyCategoryCond, "productCategoryId", true);
			    	                	for (GenericValue categoryAppl : listSalesPolicyCategoryCond) {
			    	                		if (productCatIdListCond.contains(categoryAppl.getString("productCategoryId"))) {
		    	                				// no action
			    	                		} else {
			    	                			// this record was deleted
			    	                			tobeDeletedCond.add(categoryAppl);
			    	                		}
			    	                	}
			    	                	for (String productCategoryId : productCatIdListCond) {
			    	                		if (categoryIdAppls.contains(productCategoryId)) {
			    	                			// no action
			    	                		} else {
			    	                			// create new
			    	                			GenericValue newSalesPolicyCategory = delegator.makeValue("SalesPolicyCategory");
			    	                			newSalesPolicyCategory.put("salesPolicyId", salesPolicyId);
			    	                			newSalesPolicyCategory.put("salesPolicyRuleId", salesPolicyRuleId);
			    	                			newSalesPolicyCategory.put("salesPolicyActionSeqId", "_NA_");
			    	                			newSalesPolicyCategory.put("salesPolicyCondSeqId", salesPolicyCondSeqId);
			    	                			newSalesPolicyCategory.put("productCategoryId", productCategoryId);
			    	                			newSalesPolicyCategory.put("andGroupId", "_NA_");
			    	                			newSalesPolicyCategory.put("salesPolicyApplEnumId", salesPolicyApplEnumId);
			    	                			newSalesPolicyCategory.put("includeSubCategories", includeSubCategories);
			    	            				tobeStoredCond.add(newSalesPolicyCategory);
			    	                		}
			    	                	}
			    	                }
			    	                delegator.storeAll(tobeStoredCond);
			    	                delegator.removeAll(tobeDeletedCond);
	    	                	} else {
	    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"DAConditionValueOfConditionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoCondSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
		    	                }
	    	                } else {
	    	                	if (UtilValidate.isNotEmpty(condValue)) {
	    	                		Map<String, Object> result2 = dispatcher.runSync("createSalesPolicyCond", 
		 	    	                		UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyId, 
		 	    	                				"salesPolicyRuleId", salesPolicyRuleId, 
		 	    	                				"productIdListCond", productIdListCond, "salesPolicyApplEnumId", salesPolicyApplEnumId, 
		 	    	                				"includeSubCategories", includeSubCategories, "productCatIdListCond", productCatIdListCond, 
		 	    	                				"inputParamEnumId", inputParamEnumId, "operatorEnumId", operatorEnumId, 
		 	    	                				"condValue", condValue, "userLogin", userLogin, "locale", locale));
		 	    	                // no values for price and paramMap (a context for adding attributes)
		 	    	                controlDirective = processResult(result2, request);
		 	    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
		 	    	                	try {
		 	    	                        TransactionUtil.rollback(beganTx, "Failure in processing Create sales policy condition callback", null);
		 	    	                    } catch (Exception e1) {
		 	    	                        Debug.logError(e1, module);
		 	    	                    }
		 	    	                    return "error";
		 	    	                }
	    	                	} else {
	    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"DAConditionValueOfConditionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoCondSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
		    	                }
	    	                }
	    	        	}
	    	        }
	    	        
	    	        // The number of multi form rows is retrieved: Action
	    	        int rowCountSeqAction = getMultiFormRowCountSeq(paramMap, String.valueOf(i), MULTI_ROW_DELIMITER_SEQ_ACT);
	    	        if (rowCountSeqAction < 1) {
	    	            Debug.logWarning("No rows seq to process, as rowCountSeqAction = " + rowCountSeqAction, module);
	    	        } else {
	    	        	for (int j = 0; j < rowCountSeqAction; j++) {
	    	        		String thisSuffixSeq = MULTI_ROW_DELIMITER_SEQ_ACT + j + UtilHttp.MULTI_ROW_DELIMITER + i;        // current suffix after each field id
	    	    	        String salesPolicyActionSeqId = null;
	    	    	        List<String> productIdListAction = FastList.newInstance();
	    	    	        String salesPolicyApplEnumIdAction = null;
	    	    	        String includeSubCategoriesAction = null;
	    	    	        List<String> productCatIdListAction = FastList.newInstance();
	    	    	        String salesPolicyActionEnumId = null;
	    	    	        BigDecimal quantity = null;
	    	    	        String quantityStr = null;
	    	    	        BigDecimal amount = null;
	    	    	        String amountStr = null;
	    	    	        String isRemoveAction = "N";
    	    	        	
	    	    	        if (paramMap.containsKey("isRemoveAction" + thisSuffixSeq)) {
	    	    	        	isRemoveAction = (String) paramMap.remove("isRemoveAction" + thisSuffixSeq);
	    	                }
	    	    	        if (paramMap.containsKey("salesPolicyActionSeqId" + thisSuffixSeq)) {
	    	    	        	salesPolicyActionSeqId = (String) paramMap.remove("salesPolicyActionSeqId" + thisSuffixSeq);
	    	                }
	    	    	        
	    	    	        boolean isActionExisted = false;
	    	                GenericValue salesPolicyAction = delegator.findOne("SalesPolicyAction", UtilMisc.toMap("salesPolicyId", salesPolicyId, "salesPolicyRuleId", salesPolicyRuleId, "salesPolicyActionSeqId", salesPolicyActionSeqId), false);
	    	                if (salesPolicyAction != null) isActionExisted = true;
	    	                
	    	    	        if ("Y".equals(isRemoveAction)) {
	    	    	        	if (isRuleExisted && isActionExisted) {
		    	                	// delete condition
	    	    	        		Map<String, Object> result2 = dispatcher.runSync("deleteSalesPolicyAction", UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyId, "salesPolicyRuleId", salesPolicyRuleId, "salesPolicyActionSeqId", salesPolicyActionSeqId, "userLogin", userLogin, "locale", locale));
			    	                // no values for price and paramMap (a context for adding attributes)
			    	                controlDirective = processResult(result2, request);
			    	                if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
			    	                	try {
			    	                        TransactionUtil.rollback(beganTx, "Failure in processing delete sales policy action callback", null);
			    	                    } catch (Exception e1) {
			    	                        Debug.logError(e1, module);
			    	                    }
			    	                    return "error";
			    	                }
			    	                continue;
		    	                } else {
		    	                	continue;
		    	                }
	    	    	        }
	    	    	        
	    	                /*if (paramMap.containsKey("orderAdjustmentTypeId" + thisSuffixSeq)) {
	    	                	orderAdjustmentTypeId = (String) paramMap.remove("orderAdjustmentTypeId" + thisSuffixSeq);
	    	                }*/
	    	                
	    	                if (paramMap.containsKey("productIdListAction" + thisSuffixSeq)) {
	    	                	Object productIdListActionObj = (Object) paramMap.remove("productIdListAction" + thisSuffixSeq);
	    	                	if (productIdListActionObj instanceof String) {
	    	                		productIdListAction.add(productIdListActionObj.toString());
	    	                	} else if (productIdListActionObj instanceof List) {
	    	                		productIdListAction = (List<String>) productIdListActionObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("productCatIdListAction" + thisSuffixSeq)) {
	    	                	Object productCatIdListActionObj = (Object) paramMap.remove("productCatIdListAction" + thisSuffixSeq);
	    	                	if (productCatIdListActionObj instanceof String) {
	    	                		productCatIdListAction.add(productCatIdListActionObj.toString());
	    	                	} else if (productCatIdListActionObj instanceof List) {
	    	                		productCatIdListAction = (List<String>) productCatIdListActionObj;
	    	                	}
	    	                }
	    	                
	    	                if (paramMap.containsKey("salesPolicyApplEnumIdAction" + thisSuffixSeq)) {
	    	                	salesPolicyApplEnumIdAction = (String) paramMap.remove("salesPolicyApplEnumIdAction" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("includeSubCategoriesAction" + thisSuffixSeq)) {
	    	                	includeSubCategoriesAction = (String) paramMap.remove("includeSubCategoriesAction" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("salesPolicyActionEnumId" + thisSuffixSeq)) {
	    	                	salesPolicyActionEnumId = (String) paramMap.remove("salesPolicyActionEnumId" + thisSuffixSeq);
	    	                }
	    	                if (paramMap.containsKey("quantity" + thisSuffixSeq)) {
	    	                    quantityStr = (String) paramMap.remove("quantity" + thisSuffixSeq);
	    	                }
	    	                if (UtilValidate.isNotEmpty(quantityStr)) {
	    	                	// parse the quantity
	    	                    try {
	    	                        quantity = new BigDecimal(quantityStr);
	    	                    } catch (Exception e) {
	    	                        Debug.logWarning(e, "Problems parsing quantity string: " + quantityStr, module);
	    	                        quantity = BigDecimal.ZERO;
	    	                    }
	    	                }
	    	                
	    	                // get the selected amount
	    	                if (paramMap.containsKey("amount" + thisSuffixSeq)) {
	    	                	amountStr = (String) paramMap.remove("amount" + thisSuffixSeq);
	    	                }
	    	
	    	                // parse the amount
	    	                if (UtilValidate.isNotEmpty(amountStr)) {
	    	                    try {
	    	                        amount = new BigDecimal(amountStr);
	    	                    } catch (Exception e) {
	    	                        Debug.logWarning(e, "Problem parsing amount string: " + amountStr, module);
	    	                        amount = null;
	    	                    }
	    	                }
	    	                
	    	                if (isRuleExisted && isActionExisted) {
		    	                if (quantity != null || amount != null) {
		    	                    //Debug.logInfo("Attempting to add to cart with productId = ", module);
		    	                	Map<String, Object> result3 = dispatcher.runSync("updateSalesPolicyAction", 
		    	                    		UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyId, 
		    		                				"salesPolicyRuleId", salesPolicyRuleId, "salesPolicyActionSeqId", salesPolicyActionSeqId, 
		    	                    				"salesPolicyActionEnumId", salesPolicyActionEnumId, 
		    	                    				"quantity", quantity, "amount", amount, "userLogin", userLogin, "locale", locale));
		    	                	//"salesPolicyApplEnumId", salesPolicyApplEnumIdAction, "orderAdjustmentTypeId", orderAdjustmentTypeId, "productIdListAction", productIdListAction, "includeSubCategories", includeSubCategoriesAction, "productCatIdListAction", productCatIdListAction, 
		    	                    // no values for price and paramMap (a context for adding attributes)
		    	                    controlDirective = processResult(result3, request);
		    	                    if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
		    	                    	try {
		    	                            TransactionUtil.rollback(beganTx, "Failure in processing Create sales policy action callback", null);
		    	                        } catch (Exception e1) {
		    	                            Debug.logError(e1, module);
		    	                        }
		    	                        return "error";
		    	                    }
		    	                } else {
	    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"DAApplyValueOfActionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoActionSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
		    	                }
		    	                
		    	                List<GenericValue> tobeStoredAction = new LinkedList<GenericValue>();
		    	                List<GenericValue> tobeDeletedAction = new LinkedList<GenericValue>();
		    	                List<GenericValue> listSalesPolicyProductCond = EntityUtil.filterByAnd(listSalesPolicyProduct, 
		    	                		UtilMisc.toMap("salesPolicyRuleId", salesPolicyRuleId, "salesPolicyActionSeqId", salesPolicyActionSeqId));
		    	                if (listSalesPolicyProductCond != null) {
		    	                	List<String> productIdAppls = EntityUtil.getFieldListFromEntityList(listSalesPolicyProductCond, "productId", true);
		    	                	for (GenericValue productAppl : listSalesPolicyProductCond) {
		    	                		if (productIdListAction.contains(productAppl.getString("productId"))) {
	    	                				// no action
		    	                		} else {
		    	                			// this record was deleted
		    	                			tobeDeletedAction.add(productAppl);
		    	                		}
		    	                	}
		    	                	for (String productId : productIdListAction) {
		    	                		if (productIdAppls.contains(productId)) {
		    	                			// no action
		    	                		} else {
		    	                			// create new
		    	                			GenericValue newSalesPolicyProduct = delegator.makeValue("SalesPolicyProduct");
		    	                			newSalesPolicyProduct.put("salesPolicyId", salesPolicyId);
		    	                			newSalesPolicyProduct.put("salesPolicyRuleId", salesPolicyRuleId);
		    	                			newSalesPolicyProduct.put("salesPolicyActionSeqId", salesPolicyActionSeqId);
		    	                			newSalesPolicyProduct.put("salesPolicyCondSeqId", "_NA_");
		    	                			newSalesPolicyProduct.put("productId", productId);
		    	                			newSalesPolicyProduct.put("salesPolicyApplEnumId", salesPolicyApplEnumIdAction);
		    	            				tobeStoredAction.add(newSalesPolicyProduct);
		    	                		}
		    	                	}
		    	                }
		    	                List<GenericValue> listSalesPolicyCategoryCond = EntityUtil.filterByAnd(listSalesPolicyCategory, 
		    	                		UtilMisc.toMap("salesPolicyRuleId", salesPolicyRuleId, "salesPolicyActionSeqId", salesPolicyActionSeqId));
		    	                if (listSalesPolicyCategoryCond != null) {
		    	                	List<String> categoryIdAppls = EntityUtil.getFieldListFromEntityList(listSalesPolicyCategoryCond, "productCategoryId", true);
		    	                	for (GenericValue categoryAppl : listSalesPolicyCategoryCond) {
		    	                		if (productCatIdListAction.contains(categoryAppl.getString("productCategoryId"))) {
	    	                				// no action
		    	                		} else {
		    	                			// this record was deleted
		    	                			tobeDeletedAction.add(categoryAppl);
		    	                		}
		    	                	}
		    	                	for (String productCategoryId : productCatIdListAction) {
		    	                		if (categoryIdAppls.contains(productCategoryId)) {
		    	                			// no action
		    	                		} else {
		    	                			// create new
		    	                			GenericValue newSalesPolicyCategory = delegator.makeValue("SalesPolicyCategory");
		    	                			newSalesPolicyCategory.put("salesPolicyId", salesPolicyId);
		    	                			newSalesPolicyCategory.put("salesPolicyRuleId", salesPolicyRuleId);
		    	                			newSalesPolicyCategory.put("salesPolicyActionSeqId", salesPolicyActionSeqId);
		    	                			newSalesPolicyCategory.put("salesPolicyCondSeqId", "_NA_");
		    	                			newSalesPolicyCategory.put("productCategoryId", productCategoryId);
		    	                			newSalesPolicyCategory.put("andGroupId", "_NA_");
		    	                			newSalesPolicyCategory.put("salesPolicyApplEnumId", salesPolicyApplEnumIdAction);
		    	                			newSalesPolicyCategory.put("includeSubCategories", includeSubCategoriesAction);
		    	            				tobeStoredAction.add(newSalesPolicyCategory);
		    	                		}
		    	                	}
		    	                }
		    	                delegator.storeAll(tobeStoredAction);
		    	                delegator.removeAll(tobeDeletedAction);
	    	                } else {
	    	                	if (quantity != null || amount != null) {
		    	                    //Debug.logInfo("Attempting to add to cart with productId = ", module);
		    	                	Map<String, Object> result3 = dispatcher.runSync("createSalesPolicyAction", 
		    	                    		UtilMisc.<String, Object>toMap("salesPolicyId", salesPolicyId, 
		    		                				"salesPolicyRuleId", salesPolicyRuleId, "productIdListAction", productIdListAction, 
		    	                    				"salesPolicyApplEnumId", salesPolicyApplEnumIdAction, "includeSubCategories", includeSubCategoriesAction, 
		    	                    				"productCatIdListAction", productCatIdListAction, "salesPolicyActionEnumId", salesPolicyActionEnumId, 
		    	                    				"quantity", quantity, "amount", amount, "userLogin", userLogin, "locale", locale));
		    	                    // no values for price and paramMap (a context for adding attributes)
		    	                    controlDirective = processResult(result3, request);
		    	                    if (controlDirective.equals("error")) {    // if the add to cart failed, then get out of this loop right away
		    	                    	try {
		    	                            TransactionUtil.rollback(beganTx, "Failure in processing Create sales policy action callback", null);
		    	                        } catch (Exception e1) {
		    	                            Debug.logError(e1, module);
		    	                        }
		    	                        return "error";
		    	                    }
		    	                } else {
	    	                    	errMsgList.add(UtilProperties.getMessage(resource_error,"DAApplyValueOfActionInRuleMustBeNotEmpty", UtilMisc.toMap("productPromoActionSeqId", (j + 1), "productPromoRuleId", (i + 1)), locale));
		    	                }
	    	                }
	    	        	}
	    	        }
	            }
	        }
        } catch (Exception e) {
            try {
                TransactionUtil.rollback(beganTx, e.getMessage(), e);
            } catch (Exception e1) {
                Debug.logError(e1, module);
            }
            errMsgList.add(UtilProperties.getMessage(resource_error, "DAErrorWhenProcessing", locale));
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            Debug.logError(e, module);
            return "error";
        } catch (Throwable t) {
            Debug.logError(t, module);
            request.setAttribute("_ERROR_MESSAGE_", t.getMessage());
            try {
                TransactionUtil.rollback(beganTx, t.getMessage(), t);
            } catch (Exception e2) {
                Debug.logError(e2, module);
            }
            errMsgList.add(UtilProperties.getMessage(resource_error, "DAErrorWhenProcessing", locale));
            request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
            return "error";
        } finally {
            // commit the transaction
            try {
                TransactionUtil.commit(beganTx);
            } catch (Exception e) {
                Debug.logError(e, module);
            }
        }
        if (UtilValidate.isNotEmpty(errMsgList)) {
        	request.setAttribute("_ERROR_MESSAGE_LIST_", errMsgList);
        	return "error";
        }
        request.setAttribute("salesPolicyId", salesPolicyId);
        return "success";
    }
    
    @SuppressWarnings("unchecked")
	public static String jqGetListSalesChannelGroupMember(HttpServletRequest request, HttpServletResponse response) {
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
		Map<String, Object> parameterMap = UtilHttp.getParameterMap(request);
		List<Map<String, Object>> listReturn = FastList.newInstance();
		String roleTypeGroupId = (String)parameterMap.get("roleTypeGroupId");
		String totalRow = "0";
		if(roleTypeGroupId != null){
			try {
				Map<String, Object> resultService = dispatcher.runSync("jqGetListSalesChannelGroupMember", ServiceUtil.setServiceFields(dispatcher, "jqGetListSalesChannelGroupMember", parameterMap, userLogin, UtilHttp.getTimeZone(request), UtilHttp.getLocale(request)));
				listReturn = (List<Map<String,Object>>)resultService.get("listReturn");
				totalRow = (String)resultService.get("TotalRows");
			} catch (GeneralServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (GenericServiceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		request.setAttribute("listReturn", listReturn);
		request.setAttribute("TotalRows", totalRow);
		return "success";
	}
}
