package com.olbius.delys.accounting.jqservices;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;

import org.ofbiz.accounting.invoice.InvoiceWorker;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

public class FixedAssetsJQServices {
	public static final String module = FixedAssetsJQServices.class.getName();
	public static final String resource = "widgetUiLabels";
    public static final String resourceError = "widgetErrorUiLabels";
    private static int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
    private static int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
    private static int taxDecimals = UtilNumber.getBigDecimalScale("salestax.calc.decimals");
    private static int taxRounding = UtilNumber.getBigDecimalRoundingMode("salestax.rounding");
    private static final int INVOICE_ITEM_SEQUENCE_ID_DIGITS = 5;
    
	@SuppressWarnings("unchecked")
    public static Map<String, Object> listFixedAssetsJqx(DispatchContext ctx, Map<String, Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	EntityCondition tmpConditon ;
    	try {
    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
    		listIterator = delegator.find("FixedAsset", tmpConditon, null, null, listSortFields, opts);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListFixedAssetsJqx service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	@SuppressWarnings("unchecked")
    public static Map<String, Object> getLocationFacility(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	String facilityId= (String)context.get("facilityId");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("locationId"); 
		fieldToSelects.add("description");
    	try {    		
    		listIterator = delegator.find("LocationFacility", EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId), null, fieldToSelects, null, null);
		} catch (Exception e) {
			String errMsg = "Fatal error calling jqGetListLocationFacilityJqx service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator.getCompleteList());
    	return successResult;
    }	
	
	public static Map<String, Object> fixedAssetableList(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();		
		List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
		String strKeySearch = (String)context.get("searchKey");

		if(!"".equals(strKeySearch)){
			List<EntityCondition> tmpInputCond = new ArrayList<EntityCondition>();
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fixedAssetId"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fixedAssetName"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
			tmpListCond.add(EntityCondition.makeCondition(tmpInputCond,EntityJoinOperator.OR));
		}
		List<GenericValue> listData;
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			listData = delegator.findList("FixedAsset", EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.AND), null, null, null, false);
			successResult.put("listFixedAssets", listData);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling fixedAssetableList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
	}
	
	public static Map<String, Object> fixedAssetOwnerableList(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
		String strKeySearch = (String)context.get("searchKey");
		tmpListCond.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "INTERNAL_ORGANIZATIO"));

		if(!"".equals(strKeySearch)){
			List<EntityCondition> tmpInputCond = new ArrayList<EntityCondition>();
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
			tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));
			tmpListCond.add(EntityCondition.makeCondition(tmpInputCond,EntityJoinOperator.OR));
		}
		List<GenericValue> listData;
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		try {
			listData = delegator.findList("PartyRelationShipWithPartyGroup", EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.AND), null, null, null, false);
			successResult.put("listParties", listData);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling fixedAssetOwnerableList service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	return successResult;
	}	
	
	 @SuppressWarnings("unchecked")
		public static Map<String, Object> listPartyFixedAssetsAssignmentsJqx(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("fixedAssetId", (String)parameters.get("fixedAssetId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("PartyFixedAssetAssignment", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling listPartyFixedAssetsAssignmentsJqx service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }	
	 
		public static Map<String, Object> fixedAssetManagerableList(DispatchContext ctx, Map<String, ? extends Object> context) {
			Delegator delegator = ctx.getDelegator();
			List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
			String strKeySearch = (String)context.get("searchKey");
			//tmpListCond.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, strCurrentOrganization));
			//tmpListCond.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "EMPLOYEE")); // FIXME update role to Logistics department role
			if(!"".equals(strKeySearch)){
				List<EntityCondition> tmpInputCond = new ArrayList<EntityCondition>();
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("fullName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstMiddle"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstLast"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("middleLast"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("groupName"), EntityOperator.LIKE, "%" + strKeySearch.toUpperCase() + "%"));				
				tmpListCond.add(EntityCondition.makeCondition(tmpInputCond,EntityJoinOperator.OR));
			}
			List<GenericValue> listData;
			Map<String, Object> successResult = ServiceUtil.returnSuccess();
			try {
				listData = delegator.findList("PartyNameAllView", EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.AND), null, null, null, false);
				successResult.put("listParties", listData);
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error calling facilityManagerableList service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
			return successResult;
		}
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> listFixedAssetProductJqx(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("fixedAssetId", (String)parameters.get("fixedAssetId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("FixedAssetProduct", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling listFixedAssetProductTypeJqx service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
		
		public static Map<String, Object> fixedAssetProductableList(DispatchContext ctx, Map<String, ? extends Object> context) {
			Delegator delegator = ctx.getDelegator();
			List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
			String strKeySearch = (String)context.get("searchKey");
			List<EntityCondition> tmpCond = new ArrayList<EntityCondition>();
			tmpCond.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "ASSET_USAGE")); 
			tmpCond.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "ASSET_USAGE_OUT_IN"));
			tmpListCond.add(EntityCondition.makeCondition(tmpCond,EntityJoinOperator.OR));

			if(!"".equals(strKeySearch)){
				List<EntityCondition> tmpInputCond = new ArrayList<EntityCondition>();
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productId"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("internalName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("brandName"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpInputCond.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("description"), EntityOperator.LIKE, EntityFunction.UPPER("%" + strKeySearch + "%")));
				tmpListCond.add(EntityCondition.makeCondition(tmpInputCond,EntityJoinOperator.OR));
			}
			List<GenericValue> listData;
			Map<String, Object> successResult = ServiceUtil.returnSuccess();
			try {
				listData = delegator.findList("Product", EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.AND), null, null, null, false);
				successResult.put("listProducts", listData);
			} catch (GenericEntityException e) {
				String errMsg = "Fatal error calling facilityManagerableList service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
			return successResult;
		}	
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> listFixedAssetIdentificationJqx(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("fixedAssetId", (String)parameters.get("fixedAssetId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("FixedAssetIdent", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling listFixedAssetIdentificationJqx service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }	
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> listFixedAssetMaintenancesJqx(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("fixedAssetId", (String)parameters.get("fixedAssetId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("FixedAssetMaint", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling listFixedAssetMaintenancesJqx service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }	

		@SuppressWarnings("unchecked")
		public static Map<String, Object> listFixedAssetDepreciationMethodJqx(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("fixedAssetId", (String)parameters.get("fixedAssetId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);	    
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("FixedAssetDepMethod", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling listFixedAssetMaintenancesJqx service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> listFixedAssetTypeyGLAccountJqx(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("fixedAssetId", (String)parameters.get("fixedAssetId")[0]);
	    	mapCondition.put("organizationPartyId", (String)parameters.get("organizationPartyId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("FixedAssetTypeGlAccount", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling listFixedAssetTypeyGLAccountJqx service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }	
				
		@SuppressWarnings("unchecked")
		public static Map<String, Object> listGlobalFixedAssetTypeyGLAccountJqx(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	
	    	List<EntityCondition> tmpListCond = new ArrayList<EntityCondition>();
	    	tmpListCond.add(EntityCondition.makeCondition("fixedAssetId", EntityOperator.EQUALS, "_NA_"));
			List<EntityCondition> tmpCond = new ArrayList<EntityCondition>();
			tmpCond.add(EntityCondition.makeCondition("fixedAssetTypeId", EntityOperator.EQUALS, (String)parameters.get("fixedAssetTypeId")[0])); 
			tmpCond.add(EntityCondition.makeCondition("fixedAssetTypeId", EntityOperator.EQUALS, "_NA_"));
			tmpListCond.add(EntityCondition.makeCondition(tmpCond,EntityJoinOperator.OR));
			EntityCondition tmpConditon = EntityCondition.makeCondition(tmpListCond,EntityJoinOperator.AND);
			listAllConditions.add(tmpConditon);	    	
			
	    	try {
	    		//listItems = delegator.findByAnd("InvoiceItem", map, new ArrayList<String>(), false);
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("FixedAssetTypeGlAccount", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling listFixedAssetTypeyGLAccountJqx service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }	
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> listFixedAssetDepreciationReportJqx(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("fixedAssetId", (String)parameters.get("fixedAssetId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);	

	    	LocalDispatcher dispatcher = ctx.getDispatcher();
			
			 Map<String, Object> calFADepContext = FastMap.newInstance();
			 calFADepContext.put("fixedAssetId", (String)parameters.get("fixedAssetId")[0]);
			 calFADepContext.put("userLogin", context.get("userLogin"));			 

             Map<String, Object> calcFADepResult = null;
             try {
            	 calcFADepResult = dispatcher.runSync("calculateFixedAssetDepreciation", calFADepContext);
             } catch (GenericServiceException e) {
            	 String errMsg = "Fatal error calling listFixedAssetDepreciationReportJqx service: " + e.toString();
 					Debug.logError(e, errMsg, module);
             }
             List<GenericValue> assetDepreciationInfoList = UtilGenerics.checkList(calcFADepResult.get("assetDepreciationInfoList"));
             	    
	    	successResult.put("listIterator", assetDepreciationInfoList);
	    	return successResult;
	    }
		
		@SuppressWarnings("unchecked")
		public static Map<String, Object> listFixedAssetTransactionsJqx(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	listSortFields.add("transactionDate");
	    	listSortFields.add("debitCreditFlag");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("fixedAssetId", (String)parameters.get("fixedAssetId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("AcctgTransAndEntries", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling listFixedAssetTransactionsJqx service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }		
		public static Map<String, Object> jqListFixedAssetRegistration(DispatchContext ctx, Map<String, ? extends Object> context) {
	    	Delegator delegator = ctx.getDelegator();
	    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    	EntityListIterator listIterator = null;
	    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
	    	Map<String, String> mapCondition = new HashMap<String, String>();
	    	mapCondition.put("fixedAssetId", (String)parameters.get("fixedAssetId")[0]);
	    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
	    	listAllConditions.add(tmpConditon);
	    	try {
	    		tmpConditon = EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND);
	    		listIterator = delegator.find("FixedAssetRegistration", tmpConditon, null, null, listSortFields, opts);
			} catch (Exception e) {
				String errMsg = "Fatal error calling jqListFixedAssetRegistration service: " + e.toString();
				Debug.logError(e, errMsg, module);
			}
	    	successResult.put("listIterator", listIterator);
	    	return successResult;
	    }	
}
