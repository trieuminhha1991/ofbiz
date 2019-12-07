package com.olbius.accounting.jqservices;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.StringUtil.StringWrapper;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.util.SalesPartyUtil;

public class ForeignExchangeRates {
	public static final String module = ForeignExchangeRates.class.getName();
	public static final String resource = "widgetUiLabels";
    public static final String resourceError = "widgetErrorUiLabels";
	
    public static Map<String, Object> jqGetListConversions(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(tmpConditon);
    	Locale locale = (Locale) context.get("locale");
    	List<Map<String,Object>> listRs = FastList.newInstance();
    	Map<String,Object> resultSuccess = ServiceUtil.returnSuccess();
    	try {
    		int pagenum = Integer.parseInt((String) parameters.get("pagenum")[0]);
    		int pagesize = Integer.parseInt((String) parameters.get("pagesize")[0]);
    		int start = pagenum*pagesize;
    		int end = start + pagesize;
    		listIterator = delegator.find("UomConversionDatedView", EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("uomTypeId","CURRENCY_MEASURE"),EntityCondition.makeCondition("uomTypeToId","CURRENCY_MEASURE")),EntityJoinOperator.AND), null, null, null, opts);
    		if(end > listIterator.getResultsTotalSize()){
    			end = 	listIterator.getResultsTotalSize();
    		}
    		List<GenericValue> listGv = listIterator.getPartialList(start, pagesize);
    		if(UtilValidate.isNotEmpty(listGv)){
    			for(GenericValue gv : listGv){
    				Map<String,Object> mapGv = FastMap.newInstance();
    				mapGv.put("uomId", gv.getString("uomId"));
    				mapGv.put("uomIdTo", gv.getString("uomIdTo"));
    				mapGv.put("purposeEnumId", gv.getString("purposeEnumId"));
    				GenericValue cost = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", gv.getString("uomId")));
    				mapGv.put("uomIdDes",(UtilValidate.isNotEmpty(cost) ? cost.get("description",locale) : ""));
    				GenericValue off = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", gv.getString("uomIdTo")));
    				mapGv.put("uomIdToDes",(UtilValidate.isNotEmpty(off) ? off.get("description",locale) : "") );
    				mapGv.put("fromDate", gv.getTimestamp("fromDate"));
    				mapGv.put("thruDate", gv.getTimestamp("thruDate"));
    				mapGv.put("conversionFactor", gv.getDouble("conversionFactor"));
    				listRs.add(mapGv);
    			}
    		}
    		listRs = SalesPartyUtil.filterMap(listRs, listAllConditions);
    		listRs = SalesPartyUtil.sortList(listRs, listSortFields);
    		resultSuccess.put("listIterator", listRs);
    		if(UtilValidate.isNotEmpty(listAllConditions) && listAllConditions.size() > 1 ){
    			resultSuccess.put("TotalRows", String.valueOf(listRs.size()));
    		}else {
    			resultSuccess.put("TotalRows", String.valueOf(listIterator.getCompleteList().size()));
    		}
    		listIterator.close();
    	} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListConversions service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return resultSuccess;
    }
}
