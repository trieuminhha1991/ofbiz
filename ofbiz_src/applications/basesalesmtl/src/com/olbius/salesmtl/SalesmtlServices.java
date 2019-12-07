package com.olbius.salesmtl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basesales.util.SalesPartyUtil;

import javolution.util.FastMap;

public class SalesmtlServices {
	public static Map<String, Object> getGeneralInformationOfParty(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	String partyId = (String) context.get("partyId");
       	GenericValue partyNameView = null;
       	List<GenericValue> postalAddress = new ArrayList<GenericValue>();
       	String phone = null;
       	String email = null;
   		try {
   			partyNameView = EntityUtil.getFirst(delegator.findByAnd("PartyNameView", UtilMisc.toMap("partyId", partyId), null, false));
   			List<GenericValue> listPartyContactMech = EntityUtil.filterByDate(delegator.findByAnd("PartyContactMech", UtilMisc.toMap("partyId", partyId), null, false));
			if (UtilValidate.isNotEmpty(listPartyContactMech)) {
				for (GenericValue partyContactMech : listPartyContactMech) {
					GenericValue contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", partyContactMech.get("contactMechId")), false);
					if (UtilValidate.isNotEmpty(contactMech)) {
						String contactMechTypeId = contactMech.getString("contactMechTypeId");
						if ("POSTAL_ADDRESS".equals(contactMechTypeId)) {
							GenericValue postalAddress1 = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", contactMech.get("contactMechId")), false);
							if (UtilValidate.isNotEmpty(postalAddress1)) {
								postalAddress.add(postalAddress1);
							}
						} else if ("EMAIL_ADDRESS".equals(contactMechTypeId)) {
							email = contactMech.getString("infoString");
						} else if ("TELECOM_NUMBER".equals(contactMechTypeId)) {
							GenericValue telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechTypeId), false);
							if (UtilValidate.isNotEmpty(telecomNumber)) {
								phone = "(" + telecomNumber.getString("countryCode") + " - " + telecomNumber.getString("areaCode") + ") " + telecomNumber.getString("contactNumber");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	successResult.put("partyId", partyId);
    	successResult.put("partyNameView", partyNameView);
    	successResult.put("postalAddress", postalAddress);
    	successResult.put("phone", phone);
    	successResult.put("email", email);
    	return successResult;
	}
	
	/*@SuppressWarnings("unchecked")
   	public static Map<String, Object> listSalesmanByDist(DispatchContext ctx, Map<String, ? extends Object> context) {
       	Delegator delegator = ctx.getDelegator();
       	Map<String, Object> successResult = ServiceUtil.returnSuccess();
       	EntityListIterator listIterator = null;
   		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
       	List<String> listSortFields = (List<String>) context.get("listSortFields");
       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
       	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
       	opts.setDistinct(true);
   		try {
   			String partyId = null;
   			if (UtilValidate.isNotEmpty(parameters) && parameters.containsKey("partyId") && UtilValidate.isNotEmpty(parameters.get("partyId")[0])) {
				partyId = parameters.get("partyId")[0];
   			}
   			if (partyId != null) {
   				listIterator = SalesmtlUtil.getIteratorSalesmanPersonByDis(delegator, partyId, listSortFields, listAllConditions, opts);
   			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}*/
	
	public static Map<String, Object> departmentOfEmployee(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<String> departmentIds = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()));
		List<GenericValue> listDepartment = delegator.findList("PartyAndGroup",
				EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, departmentIds), null, null, null, false);
		result.put("listDepartment", listDepartment);
		return result;
	}
	@SuppressWarnings("unchecked")
   	public static Map<String, Object> listDepartmentOfEmployee(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
   		try {
   	   		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
   	       	List<String> listSortFields = (List<String>) context.get("listSortFields");
   	       	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
   	       	
	   	    GenericValue userLogin = (GenericValue)context.get("userLogin");
	   	    List<String> departmentIds = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()));
   	       	listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, departmentIds));
	   	    EntityListIterator listIterator = delegator.find("PartyAndGroup",
	 				EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
	   	    result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getCostByDepartment(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
	    	List<String> listSortFields = (List<String>) context.get("listSortFields");
	    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
	    	GenericValue userLogin = (GenericValue) context.get("userLogin");
	    	String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
	    	listAllConditions.add(EntityCondition.makeCondition("organizationPartyId", EntityJoinOperator.EQUALS, organizationId));
	    	List<String> departmentIds = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()));
   	       	listAllConditions.add(EntityCondition.makeCondition("departmentId", EntityJoinOperator.IN, departmentIds));
   	       	listSortFields.add("costAccDate");
	    	EntityListIterator listIterator = delegator.find("CostAccDepartmentDetail",
	 				EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
	   	    result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
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
	
	public static Map<String, Object> getProductStatusByDepartment(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		try {
			Locale locale = (Locale) context.get("locale");
			String status = "";
			if (SalesPartyUtil.isSalesEmployee(delegator, userLogin.getString("partyId"))) {
				//	checkProductSaleable
				result = dispatcher.runSync("checkProductSaleable", context);
				if (ServiceUtil.isSuccess(result)) {
					boolean saleable = (boolean) result.get("saleable");
					status = saleable?UtilProperties.getMessage("BasePOUiLabels", "ProductCanBeSale", locale):
									UtilProperties.getMessage("BasePOUiLabels", "ProductCanNotBeSale", locale);
				}
			} else if (SalesPartyUtil.hasRole(delegator, userLogin.getString("partyId"), "PO_EMPLOYEE")) {
				//	checkProductSaleable
				result = dispatcher.runSync("checkProductSaleable", context);
				if (ServiceUtil.isSuccess(result)) {
					boolean saleable = (boolean) result.get("saleable");
					status = saleable?UtilProperties.getMessage("BasePOUiLabels", "ProductCanBePurchased", locale):
									UtilProperties.getMessage("BasePOUiLabels", "ProductCanNotBePurchased", locale);
				}
			}
			result.clear();
			result.put("status", status);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> rejectPartyRelationDms(DispatchContext ctx, Map<String, ? extends Object> context) throws Exception {
	    Delegator delegator = ctx.getDelegator();
	    Map<String, Object> successResult = ServiceUtil.returnSuccess();
	    String partyId = (String) context.get("partyId");
        List<EntityCondition> conditions = FastList.newInstance();
        conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
        conditions.add(EntityCondition.makeCondition(UtilMisc.toList(
                EntityCondition.makeCondition("partyIdFrom", EntityJoinOperator.EQUALS, partyId),
                EntityCondition.makeCondition("partyIdTo", EntityJoinOperator.EQUALS, partyId)
        ), EntityJoinOperator.OR));
        conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.NOT_EQUAL, "OWNER"));
        List<GenericValue> partyRelationships = delegator.findList("PartyRelationDmsLog",
                EntityCondition.makeCondition(conditions), null, null, null, false);
        for (GenericValue x : partyRelationships) {
            x.set("thruDate", new Timestamp(System.currentTimeMillis()));
            x.store();
        }

        GenericValue partyCustomer = delegator.findOne("PartyCustomer", UtilMisc.toMap("partyId", partyId), false);
        GenericValue partyDistributor = delegator.findOne("PartyDistributor", UtilMisc.toMap("partyId", partyId), false);
        GenericValue partySalesman = delegator.findOne("PartySalesman", UtilMisc.toMap("partyId", partyId), false);
        Map<String, Object> mapInput = UtilMisc.toMap("partyId", partyId, "statusId", "PARTY_DISABLED", "userLogin", context.get("userLogin"));
        LocalDispatcher dispatcher = ctx.getDispatcher();
        dispatcher.runSync("setPartyStatus", mapInput);
        if (partyCustomer != null) {
            partyCustomer.set("statusId", "PARTY_DISABLED");
            partyCustomer.store();
        } else if (partyDistributor != null) {
            partyDistributor.set("statusId", "PARTY_DISABLED");
            partyDistributor.store();
        } else if (partySalesman != null) {
            partySalesman.set("statusId", "PARTY_DISABLED");
            partySalesman.store();
        }
        successResult.put("partyId", partyId);
        return successResult;
    }
}