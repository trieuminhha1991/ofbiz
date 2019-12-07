package com.olbius.organization.services;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GeneralServiceException;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;

import com.olbius.organization.utils.OrganizationUtils;
import com.olbius.util.CommonUtil;
import com.olbius.util.MultiOrganizationUtil;
import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

public class OrganizationServices {

	public static String module = OrganizationServices.class.getName();
	public static String resource = "hrolbiusUiLabels";
	public static String resourceNoti = "NotificationUiLabels";

	/**
	 * Create a Organization
	 * 
	 * @param dpctx
	 * @param context
	 * @return
	 */

	public static Map<String, Object> createOrgUnit(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Locale locale = (Locale)context.get("locale");
		TimeZone timeZone = (TimeZone)context.get("timeZone");
		String partyId = (String)context.get("partyId");
		try {
			if(partyId != null){
				GenericValue checkEntt = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
				if(checkEntt != null){
					return ServiceUtil.returnError(UtilProperties.getMessage("DirectoryUiLabels", "PartyGroupExists", UtilMisc.toMap("partyId", partyId), locale));
				}
			}
			Map<String, Object> resultService = dispatcher.runSync("createPartyGroup", ServiceUtil.setServiceFields(dispatcher, "createPartyGroup", context, userLogin, timeZone, locale));
			if(ServiceUtil.isSuccess(resultService)){
				partyId = (String)resultService.get("partyId");
				//create partyRole
				String roleTypeId = (String)context.get("partyRoleTypeId");
				Map<String, Object> partyRoleMap = FastMap.newInstance();
				partyRoleMap.put("partyId", partyId);
				partyRoleMap.put("roleTypeId", roleTypeId);
				partyRoleMap.put("userLogin", userLogin);
				dispatcher.runSync("createPartyRole", partyRoleMap);
				partyRoleMap.clear();
				partyRoleMap.put("partyId", partyId);
				partyRoleMap.put("roleTypeId", "INTERNAL_ORGANIZATIO");
				partyRoleMap.put("userLogin", userLogin);
				dispatcher.runSync("createPartyRole", partyRoleMap);
				//create party relationship
				String parentPartyId = (String)context.get("parentPartyGroupId");
				String roleTypeIdParent = PartyUtil.getPartyGroupRoleTypeDept(delegator, parentPartyId);
				if(roleTypeIdParent == null){
					return ServiceUtil.returnError(UtilProperties.getMessage("DirectoryUiLabels", "ParentPartyHaveNoRoleType", locale));
				}
				Map<String, Object> partyRelRoleTypeMap = FastMap.newInstance();
				partyRelRoleTypeMap.put("partyIdFrom", parentPartyId);
				partyRelRoleTypeMap.put("roleTypeIdFrom", roleTypeIdParent);
				partyRelRoleTypeMap.put("partyIdTo", partyId);
				partyRelRoleTypeMap.put("roleTypeIdTo", roleTypeId);
				partyRelRoleTypeMap.put("partyRelationshipTypeId", "GROUP_ROLLUP");
				partyRelRoleTypeMap.put("userLogin", userLogin);
				dispatcher.runSync("createPartyRelationship", partyRelRoleTypeMap);
				
				//create partyPostal Address
				OrganizationUtils.createPartyPostalAddress(dctx, context, partyId);
				String contentId = OrganizationUtils.createPartyContent(delegator, dispatcher, context);
				if(contentId != null){
					Map<String, Object> partyContentMap = FastMap.newInstance();
					partyContentMap.put("partyId", partyId);
					partyContentMap.put("contentId", contentId);
					partyContentMap.put("partyContentTypeId", "LGOIMGURL");
					partyContentMap.put("userLogin", userLogin);
					dispatcher.runSync("createPartyContent", partyContentMap);	
				}
			}else {
				return ServiceUtil.returnError((String)resultService.get(ModelService.ERROR_MESSAGE));
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
			ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GeneralServiceException e) {
			e.printStackTrace();
			ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericEntityException e) {
			e.printStackTrace();
			ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("CommonUiLabels", "CommonSuccessfullyCreated", locale));
	}
	
	public static Map<String, Object> assignSecGroupManager(DispatchContext dctx, Map<String, Object> context){
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String managerId = (String)context.get("managerId");
		String role = (String)context.get("title");
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyId", managerId));
		conditions.add(EntityCondition.makeCondition("enabled", "Y"));
		try {
			List<GenericValue> userLoginList = delegator.findList("UserLogin", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
			List<String> securityGroups = FastList.newInstance();
			if(role.equals("CEO")){
				securityGroups.add("HEADOFCOM");
				securityGroups.add("HEADOFDEPT");
				securityGroups.add("HRMADMIN");
			}else if(role.equals("DHOD")){
				securityGroups.add("HEADOFDEPT");
			}else if(role.equals("HOD")){
				securityGroups.add("HEADOFDEPT");	
			}else if(role.equals("HRMADMIN")){
				securityGroups.add("HEADOFDEPT");
				securityGroups.add("HRMADMIN");
			}
			for(String securityGroup: securityGroups){
				for(GenericValue userLogin: userLoginList){
					if(!checkUserLoginSecurityGroup(delegator, securityGroup, userLogin.getString("userLoginId"))){
						dispatcher.runSync("addUserLoginToSecurityGroup", 
										UtilMisc.toMap("userLoginId", userLogin.getString("userLoginId"),
														"groupId", securityGroup,
														"fromDate", UtilDateTime.nowTimestamp(),
														"userLogin", context.get("userLogin")));
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	private static boolean checkUserLoginSecurityGroup(Delegator delegator, String groupId, String userLoginId) throws GenericEntityException{
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("userLoginId", userLoginId));
		conditions.add(EntityCondition.makeCondition("groupId", groupId));
		conditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> userLoginGroup = delegator.findList("UserLoginSecurityGroup", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
		if(UtilValidate.isNotEmpty(userLoginGroup)){
			return true;
		}else{
			return false;
		}
	}
	
	public static Map<String, Object> CreateMgrForOrg(DispatchContext dctx, Map<String, Object> context){
		String managerId = (String) context.get("managerId");
		String roleTypeId = (String) context.get("roleTypeId");
		LocalDispatcher dispatcher = dctx.getDispatcher();
		//String mgrRoleType = (String) context.get("title");
		String orgId = (String) context.get("orgId");
		
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("partyIdFrom", orgId));
		conditions.add(EntityCondition.makeCondition("partyIdTo", managerId));
		conditions.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
		conditions.add(EntityUtil.getFilterByDateExpr());
		try {
			List<GenericValue> empl = dctx.getDelegator().findList("Employment", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
			if(UtilValidate.isEmpty(empl)){
				ServiceUtil.returnError(UtilProperties.getMessage("HrCommonUiLabels", "EmployeeNotBelongDept", (Locale)context.get("locale")));
			}
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		}
		
		Map<String, Object> ctx = FastMap.newInstance();
		ctx.put("partyId", managerId);
		ctx.put("roleTypeId", "MANAGER");
		ctx.put("partyIdFrom", managerId);
		ctx.put("partyIdTo", orgId);
		ctx.put("roleTypeIdFrom", "MANAGER");
		ctx.put("roleTypeIdTo", roleTypeId);
		ctx.put("partyRelationshipTypeId", "MANAGER");
		ctx.put("userLogin", context.get("userLogin"));
		
		try {			
			dispatcher.runSync("createPartyRelationshipAndRole", ctx);			
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> getOrganizationUnit(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> retMap = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String rootPartyId = (String)context.get("partyId");
		List<Map<String, Object>> listReturn = FastList.newInstance();
		retMap.put("listReturn", listReturn);
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		if(rootPartyId == null){
			rootPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator);
		}
		try {
			Map<String, Object> tempMap = FastMap.newInstance();
			Map<String, Object> resultService = dispatcher.runSync("getPartyPostalAddress", 
					UtilMisc.toMap("partyId", rootPartyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION", "userLogin", userLogin));
			String contactMechId = (String)resultService.get("contactMechId");
			Organization buildOrg = PartyUtil.buildOrg(delegator, rootPartyId, false, false);
			int rootNbrEmpl = OrganizationUtils.getNbrEmplOfOrganization(dispatcher, delegator, userLogin, buildOrg, listReturn);
			
			tempMap.put("partyId", rootPartyId);
			tempMap.put("partyName", PartyHelper.getPartyName(delegator, rootPartyId, false));
			tempMap.put("partyIdFrom", "-1");
			tempMap.put("totalEmployee", rootNbrEmpl);
			tempMap.put("expanded", true);
			if(contactMechId != null){
				tempMap.put("contactMechId", contactMechId);
				String contactMechDetails = CommonUtil.getPostalAddressDetails(delegator, contactMechId);
				tempMap.put("postalAddress", contactMechDetails);
			}
			listReturn.add(tempMap);
		} catch(GenericEntityException e) {
			e.printStackTrace();
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		return retMap;
	}
}
