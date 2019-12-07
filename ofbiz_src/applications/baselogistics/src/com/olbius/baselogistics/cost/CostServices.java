package com.olbius.baselogistics.cost;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;


import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.baselogistics.LogisticsServices;

public class CostServices {
	
	public static final String module = LogisticsServices.class.getName();
	public static final String resource = "BaseLogisticsUiLabels";
	public static final String resourceError = "BaseLogisticsUiLabels";
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCostAccounting(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String invoiceItemTypeId = null;
    	if (parameters.get("invoiceItemTypeId") != null && parameters.get("invoiceItemTypeId").length > 0){
    		invoiceItemTypeId = (String)parameters.get("invoiceItemTypeId")[0];
    	}
    	
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = userLogin.getString("userLoginId");
		String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
    	/*if (parameters.get("organizationPartyId") != null && parameters.get("organizationPartyId").length > 0){
    		organizationPartyId = (String)parameters.get("organizationPartyId")[0];
    	}*/
    	
    	String departmentId = null;
    	if (parameters.get("departmentId") != null && parameters.get("departmentId").length > 0){
    		departmentId = (String)parameters.get("departmentId")[0];
    	}
    	
    	String yearStr = null;
    	int year = 0;
    	if (parameters.get("year") != null && parameters.get("year").length > 0){
    		yearStr = (String)parameters.get("year")[0];
    	}
    	if (yearStr != null && !"".equals(yearStr)){
    		year = Integer.parseInt(yearStr);
    	} else {
    		year = Calendar.getInstance().get(Calendar.YEAR);
    	}
    	
    	if (UtilValidate.isNotEmpty(departmentId)){
    		mapCondition = new HashMap<String, String>();
    		mapCondition.put("departmentId", departmentId);
    		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(tmpConditon);
    	}
    	
    	if (UtilValidate.isNotEmpty(organizationPartyId)){ 
    		mapCondition = new HashMap<String, String>();
    		mapCondition.put("organizationPartyId", organizationPartyId);
    		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(tmpConditon);
    	}
    	
    	List<Map<String, Object>> listCosts = new ArrayList<Map<String, Object>>();
    	BigDecimal maxDeep = BigDecimal.ZERO;
    	try {
    		List<GenericValue> listCostsAccounting = new ArrayList<GenericValue>();
    		listCostsAccounting = delegator.findList("CostAccountingDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    		List<Map<String, Object>> listInvoiceItemTypes = new ArrayList<Map<String, Object>>();
    		listInvoiceItemTypes = getAllChildInvoiceItemTypeId(delegator, invoiceItemTypeId, organizationPartyId, departmentId, listInvoiceItemTypes);
    		if (!listInvoiceItemTypes.isEmpty()){
    			for (Map<String, Object> type : listInvoiceItemTypes){
    				BigDecimal deep = BigDecimal.ZERO;
    				deep = getDepthOfRelation(delegator, invoiceItemTypeId, (String)type.get("invoiceItemTypeId"), deep);
    				if (maxDeep.compareTo(deep) < 1){
    					maxDeep = deep;
    				}
    				Map<String, Object> row = FastMap.newInstance();
    				row.put("invoiceItemTypeId", type.get("invoiceItemTypeId"));
    				row.put("organizationPartyId", organizationPartyId);
    				row.put("departmentId", departmentId);
    				GenericValue invoiceIype = delegator.findOne("InvoiceItemType", false, UtilMisc.toMap("invoiceItemTypeId", type.get("invoiceItemTypeId")));
    				row.put("description", invoiceIype.getString("description"));
    				row.put("isParent", type.get("isParent"));
    				row.put("deep", deep);
    				BigDecimal costMonth01 = BigDecimal.ZERO;
        			BigDecimal costMonth02 = BigDecimal.ZERO;
        			BigDecimal costMonth03 = BigDecimal.ZERO;
        			BigDecimal costMonth04 = BigDecimal.ZERO;
        			BigDecimal costMonth05 = BigDecimal.ZERO;
        			BigDecimal costMonth06 = BigDecimal.ZERO;
        			BigDecimal costMonth07 = BigDecimal.ZERO;
        			BigDecimal costMonth08 = BigDecimal.ZERO;
        			BigDecimal costMonth09 = BigDecimal.ZERO;
        			BigDecimal costMonth10 = BigDecimal.ZERO;
        			BigDecimal costMonth11 = BigDecimal.ZERO;
        			BigDecimal costMonth12 = BigDecimal.ZERO;
        			if (!listCostsAccounting.isEmpty()){
	    				for (GenericValue cost : listCostsAccounting){
	    					Timestamp tmp = cost.getTimestamp("costAccDate");
	    					long timestamp = tmp.getTime();
	    					Calendar cal = Calendar.getInstance();
	    					cal.setTimeInMillis(timestamp);
	    					int yearTmp = cal.get(Calendar.YEAR);
	    					if (cost.getString("invoiceItemTypeId").equals(type.get("invoiceItemTypeId")) && year == yearTmp && !"COST_ACC_CANCELLED".equals(cost.getString("statusId"))){
	    						int month = cal.get(Calendar.MONTH);
	    						switch (month + 1) {
								case 1:
									costMonth01 = costMonth01.add(cost.getBigDecimal("costPriceActual"));
									break;
								case 2:
									costMonth02 = costMonth02.add(cost.getBigDecimal("costPriceActual"));
									break;
								case 3:
									costMonth03 = costMonth03.add(cost.getBigDecimal("costPriceActual"));
									break;
								case 4:
									costMonth04 = costMonth04.add(cost.getBigDecimal("costPriceActual"));
									break;
								case 5:
									costMonth05 = costMonth05.add(cost.getBigDecimal("costPriceActual"));
									break;
								case 6:
									costMonth06 = costMonth06.add(cost.getBigDecimal("costPriceActual"));
									break;
								case 7:
									costMonth07 = costMonth07.add(cost.getBigDecimal("costPriceActual"));
									break;
								case 8:
									costMonth08 = costMonth08.add(cost.getBigDecimal("costPriceActual"));
									break;
								case 9:
									costMonth09 = costMonth09.add(cost.getBigDecimal("costPriceActual"));
									break;
								case 10:
									costMonth10 = costMonth10.add(cost.getBigDecimal("costPriceActual"));
									break;
								case 11:
									costMonth11 = costMonth11.add(cost.getBigDecimal("costPriceActual"));
									break;
								case 12:
									costMonth12 = costMonth12.add(cost.getBigDecimal("costPriceActual"));
									break;
									
								default:
									break;
								}
	    					}
	    				}
        			}
    				row.put("month01", costMonth01);
    				row.put("month02", costMonth02);
    				row.put("month03", costMonth03);
    				row.put("month04", costMonth04);
    				row.put("month05", costMonth05);
    				row.put("month06", costMonth06);
    				row.put("month07", costMonth07);
    				row.put("month08", costMonth08);
    				row.put("month09", costMonth09);
    				row.put("month10", costMonth10);
    				row.put("month11", costMonth11);
    				row.put("month12", costMonth12);
    				
    				listCosts.add(row);
    			}
    		}
    		
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling getCostAccounting service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	if (!listCosts.isEmpty()){
    		for (Map<String, Object> row : listCosts){
    			row.put("maxDeep", maxDeep);
    		}
    	}
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	successResult.put("listIterator", listCosts);
    	return successResult;
    }
	
	public static List<Map<String, Object>> getAllChildInvoiceItemTypeId(Delegator delegator, String rootTypeId, String organizationPartyId, String departmentId, List<Map<String, Object>> listChilds) throws GenericEntityException{
		List<GenericValue> listTmp = new ArrayList<GenericValue>();
		listTmp = delegator.findList("InvoiceItemType", EntityCondition.makeCondition(UtilMisc.toMap("parentTypeId", rootTypeId)), null, null, null, false);
		if (listTmp.isEmpty()){
			GenericValue itemType = delegator.findOne("InvoiceItemType", false, UtilMisc.toMap("invoiceItemTypeId", rootTypeId));
			List<GenericValue> listAccBaseByInvoiceType = delegator.findList("CostAccBase", EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", rootTypeId)), null, null, null, false);
			listAccBaseByInvoiceType = EntityUtil.filterByDate(listAccBaseByInvoiceType);
			if (itemType != null && itemType.get("defaultGlAccountId") != null && !listAccBaseByInvoiceType.isEmpty()){
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("invoiceItemTypeId", rootTypeId);
				mapTmp.put("isParent", false);
				listChilds.add(mapTmp);
			}
		} else {
			List<GenericValue> listAccBaseByInvoiceType = delegator.findList("CostAccBase", EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", rootTypeId)), null, null, null, false);
			GenericValue itemType = delegator.findOne("InvoiceItemType", false, UtilMisc.toMap("invoiceItemTypeId", rootTypeId));
			listAccBaseByInvoiceType = EntityUtil.filterByDate(listAccBaseByInvoiceType);
			if (itemType != null && itemType.get("defaultGlAccountId") != null && !listAccBaseByInvoiceType.isEmpty()){
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("invoiceItemTypeId", rootTypeId);
				mapTmp.put("isParent", true);
				listChilds.add(mapTmp);
			}
			for (GenericValue item : listTmp){
				listChilds = getAllChildInvoiceItemTypeId(delegator, item.getString("invoiceItemTypeId"), organizationPartyId, departmentId, listChilds);
			}
		}
		return listChilds;
	}
	
	public static BigDecimal getDepthOfRelation(Delegator delegator, String rootTypeId, String childTypeId, BigDecimal deep) throws GenericEntityException{
		if (deep == null){
			deep = BigDecimal.ZERO;
		}
		GenericValue invoiceItemType = delegator.findOne("InvoiceItemType", false, UtilMisc.toMap("invoiceItemTypeId", childTypeId));
		if (invoiceItemType != null){
			if (invoiceItemType.getString("parentTypeId") != null && !rootTypeId.equals(childTypeId)){
				if (invoiceItemType.getString("parentTypeId").equals(rootTypeId)){
					deep = deep.add(BigDecimal.ONE);
				} else {
					deep = deep.add(BigDecimal.ONE);
					deep = getDepthOfRelation(delegator, rootTypeId, invoiceItemType.getString("parentTypeId"), deep);
				}
			}
		}
		return deep;
	}
	
	public static Map<String, Object> updateLogCostAccouting(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String costAccBaseId = (String)context.get("costAccBaseId");
		String costPriceActual = (String)context.get("costPriceActual");
		String costAccountingId = (String)context.get("costAccountingId");
		String costAccDateStr = (String)context.get("costAccDate");
		String currencyUomId = (String)context.get("currencyUomId");
		String partyId = (String)context.get("partyId");
		Long costAccDateLong = new Long(costAccDateStr);
		Timestamp costAccDate = new Timestamp(costAccDateLong);
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String createdByUserLogin = userLogin.getString("partyId");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		
		ByteBuffer fileBytes = (ByteBuffer) context.get("uploadedFile");
		String fileName = (String) context.get("_uploadedFile_fileName");
		String mimeType = (String) context.get("_uploadedFile_contentType");
		String folder = (String) context.get("folder");
		
		Map<String, Object> resultMap = FastMap.newInstance();
		if(UtilValidate.isNotEmpty(fileBytes) && UtilValidate.isNotEmpty(fileName) && UtilValidate.isNotEmpty(mimeType) && UtilValidate.isNotEmpty(folder)){
			Map<String, Object> map = UtilMisc.toMap("userLogin", userLogin, "uploadedFile", fileBytes, "folder", folder, "_uploadedFile_fileName", fileName, "_uploadedFile_contentType", mimeType);
			try {
				resultMap = dispatcher.runSync("jackrabbitUploadFile", map);
			} catch (GenericServiceException e) {
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		
		String path = (String)resultMap.get("path");
		
		/*if (costAccountingId != null){
			GenericValue costAcc = delegator.findOne("CostAccounting", false, UtilMisc.toMap("costAccountingId", costAccountingId));
			if (costAcc != null){
				costAcc.put("costAccBaseId", costAccBaseId);
				costAcc.put("costPriceActual", new BigDecimal(costPriceActual));
				costAcc.put("costAccDate", costAccDate);
				costAcc.put("currencyUomId", currencyUomId);
				costAcc.put("changedByUserLogin", createdByUserLogin);
				costAcc.put("partyId", partyId);
				costAcc.put("pathScanFile", path);
				delegator.store(costAcc);
			} else {
				costAcc = delegator.makeValue("CostAccounting");
				costAcc.put("costAccountingId", costAccountingId);
				costAcc.put("costAccBaseId", costAccBaseId);
				costAcc.put("costPriceActual", new BigDecimal(costPriceActual));
				costAcc.put("costAccDate", costAccDate);
				costAcc.put("currencyUomId", currencyUomId);
				costAcc.put("statusId", "COST_ACC_CREATED");
				costAcc.put("createdByUserLogin", createdByUserLogin);
				costAcc.put("changedByUserLogin", createdByUserLogin);
				costAcc.put("partyId", partyId);
				costAcc.put("pathScanFile", path);
				delegator.createOrStore(costAcc);
			}
		} else {
			GenericValue costAcc = delegator.makeValue("CostAccounting");
			costAccountingId = delegator.getNextSeqId("CostAccounting");
			costAcc.put("costAccountingId", costAccountingId);
			costAcc.put("costAccBaseId", costAccBaseId);
			costAcc.put("costPriceActual", new BigDecimal(costPriceActual));
			costAcc.put("costAccDate", costAccDate);
			costAcc.put("currencyUomId", currencyUomId);
			costAcc.put("statusId", "COST_ACC_CREATED");
			costAcc.put("createdByUserLogin", createdByUserLogin);
			costAcc.put("changedByUserLogin", createdByUserLogin);
			costAcc.put("partyId", partyId);
			costAcc.put("pathScanFile", path);
			delegator.createOrStore(costAcc);
		}*/
		
		if(UtilValidate.isNotEmpty(costAccountingId)){
			GenericValue costAccDepart = delegator.findOne("CostAccDepartment", false, UtilMisc.toMap("costAccDepId", costAccountingId));
			if(UtilValidate.isNotEmpty(costAccDepart)){
				costAccDepart.put("costAccMapDepId", costAccBaseId);
				costAccDepart.put("costPriceActual", new BigDecimal(costPriceActual));
				costAccDepart.put("costAccDate", costAccDate);
				costAccDepart.put("currencyUomId", currencyUomId);
				costAccDepart.put("changedByUserLogin", createdByUserLogin);
				costAccDepart.put("partyId", partyId);
				costAccDepart.put("pathScanFile", path);
				delegator.store(costAccDepart);
			}else{
				costAccDepart = delegator.makeValue("CostAccDepartment");
				costAccDepart.put("costAccDepId", costAccountingId);
				costAccDepart.put("costAccMapDepId", costAccBaseId);
				costAccDepart.put("costPriceActual", new BigDecimal(costPriceActual));
				costAccDepart.put("costAccDate", costAccDate);
				costAccDepart.put("currencyUomId", currencyUomId);
				costAccDepart.put("statusId", "COST_ACC_CREATED");
				costAccDepart.put("createdByUserLogin", createdByUserLogin);
				costAccDepart.put("changedByUserLogin", createdByUserLogin);
				costAccDepart.put("partyId", partyId);
				costAccDepart.put("pathScanFile", path);
				delegator.createOrStore(costAccDepart);
			}
		}else{
			GenericValue costAccDepart = delegator.makeValue("CostAccDepartment");
			costAccountingId = delegator.getNextSeqId("CostAccDepartment");
			costAccDepart.put("costAccDepId", costAccountingId);
			costAccDepart.put("costAccBaseId", costAccBaseId);
			costAccDepart.put("costPriceActual", new BigDecimal(costPriceActual));
			costAccDepart.put("costAccDate", costAccDate);
			costAccDepart.put("currencyUomId", currencyUomId);
			costAccDepart.put("statusId", "COST_ACC_CREATED");
			costAccDepart.put("createdByUserLogin", createdByUserLogin);
			costAccDepart.put("changedByUserLogin", createdByUserLogin);
			costAccDepart.put("partyId", partyId);
			costAccDepart.put("pathScanFile", path);
			delegator.createOrStore(costAccDepart);
		}
		
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("costAccountingId", costAccountingId);
		return result;
	}
	
	public static Map<String, Object> getCostAccoutingDetail(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		String invoiceItemTypeId = (String)context.get("invoiceItemTypeId");
		String organizationPartyId = (String)context.get("organizationPartyId");
		String departmentId = (String)context.get("departmentId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> costAccBase = delegator.findList("CostAccBaseAndInvoiceItem", EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", invoiceItemTypeId, "organizationPartyId", organizationPartyId, "departmentId", departmentId)), null, null, null, false);
		costAccBase = EntityUtil.filterByDate(costAccBase);
		GenericValue base = null;
		if (!costAccBase.isEmpty()){
			base = costAccBase.get(0);
		}
		Map<String, Object> mapReturn = FastMap.newInstance();
		if (base != null){
			String costAccBaseId = base.getString("costAccBaseId");
			mapReturn.put("costAccBaseId", base.get("costAccBaseId"));
			mapReturn.put("invoiceItemTypeId", invoiceItemTypeId);
			mapReturn.put("organizationPartyId", organizationPartyId);
			mapReturn.put("departmentId", departmentId);
			mapReturn.put("fromDate", base.get("fromDate"));
			mapReturn.put("thruDate", base.get("thruDate"));
			mapReturn.put("organizationName", base.get("organizationName"));
			mapReturn.put("departmentName", base.get("departmentName"));
			mapReturn.put("description", base.get("description"));
			
			List<String> listOrderBy = new ArrayList<String>();
			listOrderBy.add("-costAccDate");
			List<EntityCondition> listCond = new ArrayList<EntityCondition>();
			EntityCondition costBaseCond = EntityCondition.makeCondition(UtilMisc.toMap("costAccBaseId", costAccBaseId));
			listCond.add(costBaseCond);
			EntityCondition statusCond = EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "COST_ACC_CANCELLED");
			listCond.add(statusCond);
			List<GenericValue> listCostAccs = delegator.findList("CostAccounting", EntityCondition.makeCondition(listCond, EntityJoinOperator.AND), null, listOrderBy, null, false);
			mapReturn.put("listCostAccs", listCostAccs);
		}
		
		return mapReturn;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateMultiCostAccounting(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		List<Map<String, String>> listCostAccounting = new ArrayList<Map<String,String>>();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String changedByUserLogin = userLogin.getString("partyId");
		List<Object> listItemTmp = (List<Object>)context.get("listCostAccounting");
    	Boolean isJson = false;
    	if (!listItemTmp.isEmpty()){
    		if (listItemTmp.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	if (isJson){
    		String stringJson = "["+(String)listItemTmp.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				HashMap<String, String> mapItems = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("costAccountingId")){
					mapItems.put("costAccountingId", item.getString("costAccountingId"));
				}
				if (item.containsKey("costPriceActual")){
					mapItems.put("costPriceActual", item.getString("costPriceActual"));
				}
				if (item.containsKey("description")){
					String description = item.getString("description");
					if(description.equals("null")){
						description = null;
					}
					mapItems.put("description", description);
				}
				listCostAccounting.add(mapItems);
			}
    	} else {
    		listCostAccounting = (List<Map<String, String>>)context.get("listCostAccounting");
    	}
    	
    	if (!listCostAccounting.isEmpty()){
    		for (Map<String, String> item : listCostAccounting){
    			GenericValue costAcc = delegator.findOne("CostAccounting", false, UtilMisc.toMap("costAccountingId",item.get("costAccountingId")));
    			if (costAcc != null && "COST_ACC_CREATED".equals(costAcc.getString("statusId"))){
    				String description = item.get("description");
    				if (item.get("costPriceActual") != null && !"".equals(item.get("costPriceActual"))){
    					costAcc.put("costPriceActual", new BigDecimal(item.get("costPriceActual")));
    				}
    				if (item.get("description") != null && !"".equals(item.get("description"))){
    					if(description.equals("null")){
    						description = null;
    					}
    					costAcc.put("description", description);
    				}
    				costAcc.put("changedByUserLogin", changedByUserLogin);
    				delegator.store(costAcc);
    			}
    		}
    	}
    	Map<String, Object> result = new FastMap<String, Object>();
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> deleteMultiCostAccounting(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException, GenericServiceException{
		List<Map<String, String>> listCostAccounting = new ArrayList<Map<String,String>>();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String changedByUserLogin = userLogin.getString("partyId");
		List<Object> listItemTmp = (List<Object>)context.get("listCostAccounting");
    	Boolean isJson = false;
    	if (!listItemTmp.isEmpty()){
    		if (listItemTmp.get(0) instanceof String){
    			isJson = true;
    		}
    	}
    	if (isJson){
    		String stringJson = "["+(String)listItemTmp.get(0)+"]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++){
				HashMap<String, String> mapItems = new HashMap<String, String>();
				JSONObject item = lists.getJSONObject(i);
				if (item.containsKey("costAccountingId")){
					mapItems.put("costAccountingId", item.getString("costAccountingId"));
				}
				listCostAccounting.add(mapItems);
			}
    	} else {
    		listCostAccounting = (List<Map<String, String>>)context.get("listCostAccounting");
    	}
    	
    	if (!listCostAccounting.isEmpty()){
    		for (Map<String, String> item : listCostAccounting){
    			GenericValue costAcc = delegator.findOne("CostAccounting", false, UtilMisc.toMap("costAccountingId",item.get("costAccountingId")));
    			if (costAcc != null && "COST_ACC_CREATED".equals(costAcc.getString("statusId"))){
					costAcc.put("statusId", "COST_ACC_CANCELLED");
    				costAcc.put("changedByUserLogin", changedByUserLogin);
    				delegator.store(costAcc);
    			}
    		}
    	}
    	Map<String, Object> result = new FastMap<String, Object>();
		return result;
	}
	
	public static List<String> getAllTotalChildInvoiceItemTypeId(Delegator delegator, String rootTypeId, List<String> listChilds) throws GenericEntityException{
		List<GenericValue> listTmp = new ArrayList<GenericValue>();
		listTmp = delegator.findList("InvoiceItemType", EntityCondition.makeCondition(UtilMisc.toMap("parentTypeId", rootTypeId)), null, null, null, false);
		if (listTmp.isEmpty()){
			GenericValue itemType = delegator.findOne("InvoiceItemType", false, UtilMisc.toMap("invoiceItemTypeId", rootTypeId));
			if (UtilValidate.isNotEmpty(itemType) && itemType.get("defaultGlAccountId") != null){
				listChilds.add(rootTypeId);
			}
		} else {
			GenericValue itemType = delegator.findOne("InvoiceItemType", false, UtilMisc.toMap("invoiceItemTypeId", rootTypeId));
			if (UtilValidate.isNotEmpty(itemType) && itemType.get("defaultGlAccountId") != null){
				listChilds.add(rootTypeId);
			}
			for (GenericValue item : listTmp){
				getAllTotalChildInvoiceItemTypeId(delegator, item.getString("invoiceItemTypeId"), listChilds);
			}
		}
		return listChilds;
	}
	
	public static List<Map<String, Object>> getCostAccBase(Delegator delegator, String invoiceItemTypeId) throws GenericEntityException {
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
		if (invoiceItemTypeId != null){
			List<String> listInvoiceItemTypes = new ArrayList<String>();
			listInvoiceItemTypes = getAllTotalChildInvoiceItemTypeId(delegator, "PITM_LOGIS_ADJ", listInvoiceItemTypes);
			if (!listInvoiceItemTypes.isEmpty()){
				for (String invoiceType : listInvoiceItemTypes){
					List<GenericValue> listAccBases = delegator.findList("CostAccBaseAndInvoiceItem", EntityCondition.makeCondition(UtilMisc.toMap("invoiceItemTypeId", invoiceType)), null, null, null, false);
					listAccBases = EntityUtil.filterByDate(listAccBases);
					if (!listAccBases.isEmpty()){
						GenericValue tmp = listAccBases.get(0);
						Map<String, Object> row = FastMap.newInstance();
						row.put("costAccBaseId", tmp.get("costAccBaseId"));
						row.put("invoiceItemTypeId", tmp.get("invoiceItemTypeId"));
						row.put("description", tmp.get("description"));
						listData.add(row);
					}
				}
			}
		}
		return listData;
	}
}
