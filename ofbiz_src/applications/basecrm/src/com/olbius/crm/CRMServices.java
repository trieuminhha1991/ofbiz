package com.olbius.crm;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.SecurityUtil;
import com.olbius.basesales.util.CRMUtils;
import com.olbius.basesales.util.SalesPartyUtil;
import com.olbius.basesales.util.SalesUtil;
import com.olbius.crm.util.PartyHelper;

import javolution.util.FastList;
import javolution.util.FastMap;

public class CRMServices {
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getPartyInformation(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		try {
			Locale locale = (Locale) context.get("locale");
			String partyId = (String) context.get("partyId");
			GenericValue partyInfo = null;
			Map<String, Object> resultParty = FastMap.newInstance();
			String partyFullName = "";
			GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
			String partyTypeId = party.getString("partyTypeId");
			if (partyTypeId.equals("PERSON")) {
				partyInfo = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				if (UtilValidate.isNotEmpty(partyInfo)) {
					partyFullName = getPartyName(delegator, partyInfo.getString("partyId"));
					String idIssuePlace = partyInfo.getString("idIssuePlace");
					resultParty.put("issuePlace", getGeoName(delegator, idIssuePlace));
					resultParty.putAll(partyInfo);
				}
			} else {
				partyInfo = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				if (UtilValidate.isNotEmpty(partyInfo)) {
					partyFullName = partyInfo.getString("groupName");
				}
			}
			List<EntityCondition> EntityConditions = FastList.newInstance();
			EntityConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
			EntityConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			List<GenericValue> listPartyContactMech = delegator.findList("PartyContactMechPurpose",
					EntityCondition.makeCondition(EntityConditions), null, null, null, false);
			List<Map> listAddress = FastList.newInstance();
			String contactNumber = "";
			if (UtilValidate.isNotEmpty(listPartyContactMech)) {
				for (GenericValue x : listPartyContactMech) {
					String contactMechId = x.getString("contactMechId");
					GenericValue contactMech = delegator.findOne("ContactMech", UtilMisc.toMap("contactMechId", contactMechId), false);
					if (UtilValidate.isEmpty(contactMech)) {
						continue;
					}
					String contactMechTypeId = contactMech.getString("contactMechTypeId");
					switch (contactMechTypeId) {
					case "TELECOM_NUMBER":
						if ("PRIMARY_PHONE".equals(x.getString("contactMechPurposeTypeId"))) {
						    if (UtilValidate.isEmpty(contactNumber)) {
                                GenericValue telecomNumber = delegator.findOne("TelecomNumber", UtilMisc.toMap("contactMechId", contactMechId), false);
                                if (UtilValidate.isNotEmpty(telecomNumber)) {
                                    contactNumber = telecomNumber.getString("contactNumber");
                                }
                            }
						}
						break;
					case "EMAIL_ADDRESS":
						if ("PRIMARY_EMAIL".equals(x.getString("contactMechPurposeTypeId"))) {
							resultParty.put("emailAddress", contactMech.getString("infoString"));
						}
						break;
					case "POSTAL_ADDRESS":
						GenericValue postalAddress = delegator.findOne("PostalAddressAndGeo", UtilMisc.toMap("contactMechId", contactMechId), false);
						if (UtilValidate.isEmpty(postalAddress)) {
							break;
						}
						List<EntityCondition> conditions = FastList.newInstance();
						conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechId", contactMechId,
								"contactMechPurposeTypeId", "PRIMARY_LOCATION")));
						conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
						List<GenericValue> listPartyContactMechPurpose = delegator.findList("PartyContactMechPurpose",
								EntityCondition.makeCondition(conditions), null, null, null, false);
						if (UtilValidate.isNotEmpty(listPartyContactMechPurpose)) {
							for (GenericValue z : listPartyContactMechPurpose) {
								Map<String, Object> mapAddress = FastMap.newInstance();
								String contactMechPurposeTypeId = z.getString("contactMechPurposeTypeId");
								GenericValue contactMechPurposeType = delegator.findOne("ContactMechPurposeType",
										UtilMisc.toMap("contactMechPurposeTypeId", contactMechPurposeTypeId), false);
								if (UtilValidate.isNotEmpty(contactMechPurposeType)) {
									String contactMechPurposeTypeStr = (String) contactMechPurposeType.get("description", locale);
									mapAddress.put("contactMechPurposeTypeId", contactMechPurposeTypeStr);
									mapAddress.put("contactMechPurposeType", contactMechPurposeTypeId);
									mapAddress.put("contactMechId", contactMechId);
									String address1 = "";
									if (UtilValidate.isNotEmpty(postalAddress)) {
										address1 = postalAddress.getString("address1") + ", " + 
												postalAddress.getString("wardGeoName") + ", " + 
												postalAddress.getString("districtGeoName") + ", " + 
												postalAddress.getString("stateProvinceGeoName") + ", " + 
												postalAddress.getString("countryGeoName");
									}
									if (UtilValidate.isNotEmpty(address1)) {
										address1 = address1.replaceAll(", null, ", ", ");
										address1 = address1.replaceAll(", null,", ", ");
									}
									mapAddress.put("address1", address1);
									mapAddress.put("districtGeoName", postalAddress.getString("districtGeoName"));
									listAddress.add(mapAddress);
								}
							}
						}
						break;
					default:
						break;
					}
				}
			}
            resultParty.put("contactNumber", contactNumber);
			resultParty.put("partyFullName", partyFullName);
			resultParty.put("listAddress", listAddress);
			result.put("partyInfo", resultParty);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> listContactFamily(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listContactFamily = FastList.newInstance();
		String TotalRows = "0";
		EntityListIterator dummyParty = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Locale locale = (Locale) context.get("locale");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", organizationId, "roleTypeIdFrom", "CONTACT", "roleTypeIdTo", "INTERNAL_ORGANIZATIO")));
			conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator)));
			Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pageNum * pagesize + 1;
			dummyParty = delegator.find("PartyRelationShipAndPerson",
							EntityCondition.makeCondition(conditions), null, null, UtilMisc.toList("-fromDate"), opts);
			List<GenericValue> listParty = dummyParty.getPartialList(start, pagesize);
			for (GenericValue x : listParty) {
				Map<String, Object> person = FastMap.newInstance();
				person.putAll(x);
				Map<String, Object> getPartyInformation = dispatcher.runSync("getPartyInformation",
				UtilMisc.toMap("partyId", x.getString("partyIdFrom"), "userLogin", userLogin));
				Map<String, Object> partyInfo = (Map<String, Object>) getPartyInformation.get("partyInfo");
				person.put("partyFullName", (String) partyInfo.get("partyFullName"));
				person.put("contactNumber", (String) partyInfo.get("contactNumber"));
				person.put("emailAddress", (String) partyInfo.get("emailAddress"));
				List<String> listPartyFamilyId = getFamilyOfPerson(x.getString("partyIdFrom"), delegator);
				if (UtilValidate.isNotEmpty(listPartyFamilyId)) {
					person.put("familyId", listPartyFamilyId.get(0));
					person.put("familyName", PartyHelper.getPartyName(delegator, listPartyFamilyId.get(0), true, true));
				}
				List<Map> listAddress = (List<Map>) partyInfo.get("listAddress");
				if (UtilValidate.isNotEmpty(listAddress)) {
					for (Map<String, Object> m : listAddress) {
						if ("PRIMARY_LOCATION".equals(m.get("contactMechPurposeType"))) {
							person.put("address1", (String) m.get("address1"));
							person.put("districtGeoName", (String) m.get("districtGeoName"));
							break;
						}
					}
				}
				List<Map> listMember = FastList.newInstance();
				for (String s : listPartyFamilyId) {
					List<Map<String, Object>> listMemberId = getMemberOfFamily(s, delegator);
					for (Map<String, Object> m : listMemberId) {
						String partyIdFrom = (String) m.get("partyIdFrom");
						Map<String, Object> getInformationMember = dispatcher.runSync("getPartyInformation",
						UtilMisc.toMap("partyId", partyIdFrom, "userLogin", userLogin));
						Map<String, Object> memberInfo = (Map<String, Object>) getInformationMember.get("partyInfo");
						GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", m.get("roleTypeIdFrom")), false);
						memberInfo.put("roleTypeFrom", roleType.get("description", locale));
						memberInfo.put("roleTypeIdFrom", m.get("roleTypeIdFrom"));
						memberInfo.put("familyId", s);
						listMember.add(memberInfo);
					}
				}
				person.put("resultEnumTypeId", getResultCall(delegator, x.getString("partyIdFrom")));
				person.put("rowDetail", listMember);
				listContactFamily.add(person);
			}
			TotalRows = String.valueOf(dummyParty.getResultsTotalSize());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				dummyParty.close();
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		result.put("listIterator", listContactFamily);
		result.put("TotalRows", TotalRows);
		return result;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> listContactFamilyByCampaign(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<Map<String, Object>> listContactFamily = FastList.newInstance();
		String TotalRows = "0";
		EntityListIterator dummyParty = null;
		try {
			Locale locale = (Locale) context.get("locale");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
			String partyId = "";
			if (parameters.containsKey("partyId")) {
				if (parameters.get("partyId").length > 0) {
					partyId = parameters.get("partyId")[0];
				}
			}
			String campaignId = "";
			if (parameters.containsKey("campaignId")) {
				if (parameters.get("campaignId").length > 0) {
					campaignId = parameters.get("campaignId")[0];
				}
			}
			if (UtilValidate.isEmpty(partyId)) {
				partyId = userLogin.getString("partyId");
			}
			context.put("partyId", partyId);
			Security se = ctx.getSecurity();
			List<EntityCondition> conditions = FastList.newInstance();
			if ("any".equals(campaignId)) {
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "partyTypeId", "PERSON")));
			} else {
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "marketingCampaignId", campaignId, "partyTypeId", "PERSON")));
			}
			conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "CONTACT_REJECTED"));
			if (!se.hasPermission("CALLCAMPAIGN_ADMIN", userLogin)) {
				conditions.add(EntityCondition.makeCondition("campaignStatusId", EntityJoinOperator.EQUALS, "MKTG_CAMP_INPROGRESS"));
			}
			conditions.add(EntityCondition.makeCondition("partyIdToPTR", organizationId));
			conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator)));
			conditions.add(EntityCondition.makeCondition(
			UtilMisc.toList(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(System.currentTimeMillis())),
							EntityCondition.makeCondition("thruDate", null)), EntityOperator.OR));
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr("fromDatePTR", "thruDatePTR")));
			conditions.add(EntityCondition.makeCondition("partyType", EntityJoinOperator.IN, UtilMisc.toList("CUSTOMER", "CONTACT", "INDIVIDUAL_CUSTOMER")));
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pageNum * pagesize + 1;
			dummyParty = delegator.find("PartyCampaignRelationshipAndPerson",
			EntityCondition.makeCondition(conditions), null, null, UtilMisc.toList("partyIdTo", "marketingCampaignId"), opts);
			List<GenericValue> listParty = dummyParty.getPartialList(start, pagesize);
			for (GenericValue x : listParty) {
				Map<String, Object> person = FastMap.newInstance();
				person.putAll(x);
				Map<String, Object> getPartyInformation = dispatcher.runSync("getPartyInformation",
				UtilMisc.toMap("partyId", x.getString("partyId"), "userLogin", userLogin));
				Map<String, Object> partyInfo = (Map<String, Object>) getPartyInformation.get("partyInfo");
				person.put("partyFullName", (String) partyInfo.get("partyFullName"));
				person.put("contactNumber", (String) partyInfo.get("contactNumber"));
				person.put("emailAddress", (String) partyInfo.get("emailAddress"));
				List<String> listPartyFamilyId = getFamilyOfPerson(x.getString("partyId"), delegator);
				if (UtilValidate.isNotEmpty(listPartyFamilyId)) {
					person.put("familyId", listPartyFamilyId.get(0));
					person.put("familyName", getPartyName(delegator, listPartyFamilyId.get(0)));
				}
				List<Map> listAddress = (List<Map>) partyInfo.get("listAddress");
				if (UtilValidate.isNotEmpty(listAddress)) {
					for (Map<String, Object> m : listAddress) {
						if ("PRIMARY_LOCATION".equals(m.get("contactMechPurposeType"))) {
							person.put("address1", (String) m.get("address1"));
							person.put("districtGeoName", (String) m.get("districtGeoName"));
							break;
						}
					}
				}
				List<Map> listMember = FastList.newInstance();
				for (String s : listPartyFamilyId) {
					List<Map<String, Object>> listMemberId = getMemberOfFamily(s, delegator);
					for (Map<String, Object> m : listMemberId) {
						String partyIdFrom = (String) m.get("partyIdFrom");
						Map<String, Object> getInformationMember = dispatcher.runSync("getPartyInformation",
						UtilMisc.toMap("partyId", partyIdFrom, "userLogin", userLogin));
						Map<String, Object> memberInfo = (Map<String, Object>) getInformationMember.get("partyInfo");
						GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", m.get("roleTypeIdFrom")), false);
						memberInfo.put("roleTypeFrom", roleType.get("description", locale));
						memberInfo.put("roleTypeIdFrom", m.get("roleTypeIdFrom"));
						memberInfo.put("familyId", s);
						listMember.add(memberInfo);
					}
				}
				String strResultEnumTypeId = x.getString("resultEnumTypeId");
				String strEnumTypeId = "";
				if (UtilValidate.isNotEmpty(strResultEnumTypeId))
				{
					String aResultEnumTypeId[] = strResultEnumTypeId.split(",");
					int iLength = 3;
					if (iLength > aResultEnumTypeId.length) iLength = aResultEnumTypeId.length;
					for (int i = 0; i < iLength; i++)
					{
						strEnumTypeId += aResultEnumTypeId[i] + ", "; 
					}
					if (strEnumTypeId.length() > 2) {
						strEnumTypeId = strEnumTypeId.substring(0, strEnumTypeId.length() - 2);
					}
				}
				person.put("resultEnumTypeId",strEnumTypeId);
				person.put("entryDate", getNextCallSchedule(context, delegator, partyId, x.getString("partyId")));
				person.put("rowDetail", listMember);
				listContactFamily.add(person);
			}
			TotalRows = String.valueOf(dummyParty.getResultsTotalSize());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(dummyParty != null) {
					dummyParty.close();
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		result.put("listIterator", listContactFamily);
		result.put("TotalRows", TotalRows);
		return result;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> listContactBusinessesByCampaign(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		List<Map<String, Object>> listContactFamily = FastList.newInstance();
		String TotalRows = "0";
		EntityListIterator dummyParty = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
			String partyId = "";
			if (parameters.containsKey("partyId")) {
				if (parameters.get("partyId").length > 0) {
					partyId = parameters.get("partyId")[0];
				}
			}
			String campaignId = "";
			if (parameters.containsKey("campaignId")) {
				if (parameters.get("campaignId").length > 0) {
					campaignId = parameters.get("campaignId")[0];
				}
			}
			String partyTypeId = "";
			if (parameters.containsKey("partyTypeId")) {
				if (parameters.get("partyTypeId").length > 0) {
					partyTypeId = parameters.get("partyTypeId")[0];
				}
			}
			if (UtilValidate.isEmpty(partyId)) {
				partyId = userLogin.getString("partyId");
			}
			context.put("partyId", partyId);
			Security se = ctx.getSecurity();
			List<EntityCondition> conditions = FastList.newInstance();
			if ("any".equals(campaignId)) {
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "partyTypeId", partyTypeId)));
			} else {
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "marketingCampaignId", campaignId, "partyTypeId", partyTypeId)));
			}
			conditions.add(EntityCondition.makeCondition("partyIdToPTR", organizationId));
			conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "CONTACT_REJECTED"));
			if (!se.hasPermission("CALLCAMPAIGN_ADMIN", userLogin)) {
				conditions.add(EntityCondition.makeCondition("campaignStatusId", EntityJoinOperator.EQUALS, "MKTG_CAMP_INPROGRESS"));
			}
			conditions.add(EntityCondition.makeCondition(
			UtilMisc.toList(EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, new Timestamp(System.currentTimeMillis())),
			EntityCondition.makeCondition("thruDate", null)), EntityOperator.OR));

			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr("fromDatePTR", "thruDatePTR")));
			conditions.add(EntityCondition.makeCondition("partyType", EntityJoinOperator.IN, UtilMisc.toList("CUSTOMER", "CONTACT", "INDIVIDUAL_CUSTOMER")));
			conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator)));

			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pageNum * pagesize + 1;

			dummyParty = delegator.find("PartyCampaignRelationshipAndPartyGroup",
						EntityCondition.makeCondition(conditions), null, null, UtilMisc.toList("marketingCampaignId"), opts);
			List<GenericValue> listParty = dummyParty.getPartialList(start, pagesize);
			for (GenericValue x : listParty) {
				Map<String, Object> person = FastMap.newInstance();
				person.putAll(x);
				Map<String, Object> getPartyInformation = dispatcher.runSync("getPartyInformation",
				UtilMisc.toMap("partyId", x.getString("partyId"), "userLogin", userLogin));
				Map<String, Object> partyInfo = (Map<String, Object>) getPartyInformation.get("partyInfo");
				person.put("contactNumber", (String) partyInfo.get("contactNumber"));
				person.put("emailAddress", (String) partyInfo.get("emailAddress"));
				String partyRepresentId = getPartyRepresentative(x.getString("partyId"), delegator);
				if (UtilValidate.isNotEmpty(partyRepresentId)) {
					person.put("representativeMember", PartyHelper.getPartyName(delegator, partyRepresentId, true, true));
				}
				List<Map> listAddress = (List<Map>) partyInfo.get("listAddress");
				if (UtilValidate.isNotEmpty(listAddress)) {
					for (Map<String, Object> m : listAddress) {
						if ("PRIMARY_LOCATION".equals(m.get("contactMechPurposeType"))) {
							person.put("address1", (String) m.get("address1"));
							break;
						}
					}
				}
				if ("SCHOOL".equals(partyTypeId)) {
					GenericValue listPartyAttrT = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", x.getString("partyId"), "attrName", "Teacher"), false);
					GenericValue listPartyAttrS = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", x.getString("partyId"), "attrName", "Student"), false);
					if(UtilValidate.isNotEmpty(listPartyAttrT)) {
						person.put("numberTeacher", (String) listPartyAttrT.get("attrValue"));
					}
					if(UtilValidate.isNotEmpty(listPartyAttrS)) {
						person.put("numberStudent", (String) listPartyAttrS.get("attrValue"));
					}
				}
				String strResultEnumTypeId = x.getString("resultEnumTypeId");
				String strEnumTypeId = "";
				if (UtilValidate.isNotEmpty(strResultEnumTypeId))
				{
					String aResultEnumTypeId[] = strResultEnumTypeId.split(",");
					int iLength = 3;
					if (iLength > aResultEnumTypeId.length) iLength = aResultEnumTypeId.length;
					for (int i = 0; i < iLength; i++)
					{
						strEnumTypeId += aResultEnumTypeId[i] + ", "; 
					}
					if (strEnumTypeId.length() > 2) {
						strEnumTypeId = strEnumTypeId.substring(0, strEnumTypeId.length() - 2);
					}
				}
				person.put("entryDate", getNextCallSchedule(context, delegator, partyId, x.getString("partyId")));
				listContactFamily.add(person);
			}
			TotalRows = String.valueOf(dummyParty.getResultsTotalSize());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if(dummyParty != null) {
					dummyParty.close();
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		result.put("listIterator", listContactFamily);
		result.put("TotalRows", TotalRows);
		return result;
	}
	@SuppressWarnings("unchecked")
	private static Timestamp getNextCallSchedule(Map<String, ? extends Object> context, Delegator delegator, String partyId, String partyIdTo) {
		Timestamp entryDate = null;
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			String condition = listAllConditions.toString();
			//      get only today schedule
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			Timestamp entryDateStartDay = new Timestamp(cal.getTimeInMillis());
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("communicationEventTypeId", "PHONE_COMMUNICATION", 
									"partyIdFrom", partyId, "partyIdTo", partyIdTo, "statusId", "COM_SCHEDULED")));
			if (!condition.contains("entryDate")) {
				listAllConditions.add(EntityCondition.makeCondition("entryDate", EntityOperator.GREATER_THAN_EQUAL_TO, entryDateStartDay));
			} else {
				for (EntityCondition e : listAllConditions) {
					if (e.toString().contains("entryDate")) {
						conditions.add(e);
					}
				}
			}
			List<String> listSortFields = FastList.newInstance();
			listSortFields.add("+entryDate");
			List<GenericValue> communicationEvents = delegator.findList("CommunicationEvent",
			EntityCondition.makeCondition(conditions), UtilMisc.toSet("entryDate"), listSortFields, null, false);
			if (UtilValidate.isNotEmpty(communicationEvents)) {
				GenericValue communicationEvent = EntityUtil.getFirst(communicationEvents);
				entryDate = communicationEvent.getTimestamp("entryDate");
			}
		} catch (GenericEntityException e) {
			entryDate = null;
		}
		return entryDate;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> listContactBusinesses(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listContactBusinesses = FastList.newInstance();
		String TotalRows = "0";
		EntityListIterator dummyParty = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			LocalDispatcher dispatcher = ctx.getDispatcher();
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pageNum * pagesize + 1;
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", organizationId,
						"roleTypeIdFrom", "CONTACT", "roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyTypeId", "BUSINESSES")));
			conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator)));
			dummyParty = delegator.find("PartyRelationShipAndGroup",
									EntityCondition.makeCondition(conditions), null, null, UtilMisc.toList("-fromDate"), opts);
			List<GenericValue> listPartyGroup = dummyParty.getPartialList(start, pagesize);
			for (GenericValue x : listPartyGroup) {
				Map<String, Object> group = FastMap.newInstance();
				group.putAll(x);
				String partyId = x.getString("partyId");
				String partyRepresentId = getPartyRepresentative(partyId, delegator);
				if (UtilValidate.isNotEmpty(partyRepresentId)) {
					group.put("representativeMember", getPartyName(delegator, partyRepresentId));
				}
				Map<String, Object> getPartyInformation = dispatcher.runSync("getPartyInformation",
				UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
				Map<String, Object> partyInfo = (Map<String, Object>) getPartyInformation.get("partyInfo");
				group.put("contactNumber", (String) partyInfo.get("contactNumber"));
				group.put("emailAddress", (String) partyInfo.get("emailAddress"));
				List<Map> listAddress = (List<Map>) partyInfo.get("listAddress");
				if (UtilValidate.isNotEmpty(listAddress)) {
					for (Map<String, Object> m : listAddress) {
						if ("PRIMARY_LOCATION".equals(m.get("contactMechPurposeType"))) {
							group.put("address1", (String) m.get("address1"));
							break;
						}
					}
				}
				listContactBusinesses.add(group);
			}
			TotalRows = String.valueOf(dummyParty.getResultsTotalSize());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dummyParty.close();
		}
		result.put("TotalRows", TotalRows);
		result.put("listIterator", listContactBusinesses);
		return result;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> listContactSchool(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listContactBusinesses = FastList.newInstance();
		String TotalRows = "0";
		EntityListIterator dummyParty = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			LocalDispatcher dispatcher = ctx.getDispatcher();
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pageNum * pagesize + 1;
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", organizationId, "roleTypeIdFrom", "CONTACT", "roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyTypeId", "SCHOOL")));
			conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator)));
			dummyParty = delegator.find("PartyRelationShipAndGroup",
							EntityCondition.makeCondition(conditions), null, null, UtilMisc.toList("-fromDate"), opts);
			List<GenericValue> listPartyGroup = dummyParty.getPartialList(start, pagesize);
			for (GenericValue x : listPartyGroup) {
				Map<String, Object> group = FastMap.newInstance();
				group.putAll(x);
				String partyId = x.getString("partyId");
				String partyRepresentId = getPartyRepresentative(partyId, delegator);
				if (UtilValidate.isNotEmpty(partyRepresentId)) {
					group.put("representativeMember", getPartyName(delegator, partyRepresentId));
				}
				Map<String, Object> getPartyInformation = dispatcher.runSync("getPartyInformation",
				UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
				Map<String, Object> partyInfo = (Map<String, Object>) getPartyInformation.get("partyInfo");
				group.put("contactNumber", (String) partyInfo.get("contactNumber"));
				group.put("emailAddress", (String) partyInfo.get("emailAddress"));
				List<Map> listAddress = (List<Map>) partyInfo.get("listAddress");
				if (UtilValidate.isNotEmpty(listAddress)) {
					for (Map<String, Object> m : listAddress) {
						if ("PRIMARY_LOCATION".equals(m.get("contactMechPurposeType"))) {
							group.put("address1", (String) m.get("address1"));
							break;
						}
					}
				}
				GenericValue listPartyAttrT = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", partyId, "attrName", "Teacher"), false);
				GenericValue listPartyAttrS = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", partyId, "attrName", "Student"), false);
				if(UtilValidate.isNotEmpty(listPartyAttrT)) {
					group.put("numberTeacher", (String) listPartyAttrT.get("attrValue"));
				}
				if(UtilValidate.isNotEmpty(listPartyAttrS)) {
					group.put("numberStudent", (String) listPartyAttrS.get("attrValue"));
				}
				listContactBusinesses.add(group);
			}
			TotalRows = String.valueOf(dummyParty.getResultsTotalSize());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dummyParty.close();
		}
		result.put("TotalRows", TotalRows);
		result.put("listIterator", listContactBusinesses);
		return result;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> listCustomersFamily(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listContactFamily = FastList.newInstance();
		String TotalRows = "0";
		EntityListIterator dummyParty = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Locale locale = (Locale) context.get("locale");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.IN, UtilMisc.toList("CUSTOMER", "INDIVIDUAL_CUSTOMER")));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", organizationId, "roleTypeIdTo", "INTERNAL_ORGANIZATIO")));
			conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator)));
			Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pageNum * pagesize + 1;
			dummyParty = delegator.find("PartyRelationShipAndPerson",
						EntityCondition.makeCondition(conditions), null, null, UtilMisc.toList("partyId"), opts);
			List<GenericValue> listParty = dummyParty.getPartialList(start, pagesize);
			for (GenericValue x : listParty) {
				Map<String, Object> person = FastMap.newInstance();
				person.putAll(x);
				Map<String, Object> getPartyInformation = dispatcher.runSync("getPartyInformation",
				UtilMisc.toMap("partyId", x.getString("partyId"), "userLogin", userLogin));
				Map<String, Object> partyInfo = (Map<String, Object>) getPartyInformation.get("partyInfo");
				person.put("partyFullName", (String) partyInfo.get("partyFullName"));
				person.put("contactNumber", (String) partyInfo.get("contactNumber"));
				person.put("emailAddress", (String) partyInfo.get("emailAddress"));
				List<String> listPartyFamilyId = getFamilyOfPerson(x.getString("partyId"), delegator);
				if (UtilValidate.isNotEmpty(listPartyFamilyId)) {
					person.put("familyId", listPartyFamilyId.get(0));
					person.put("familyName", getPartyName(delegator, listPartyFamilyId.get(0)));
				}
				List<Map> listAddress = (List<Map>) partyInfo.get("listAddress");
				if (UtilValidate.isNotEmpty(listAddress)) {
					for (Map<String, Object> m : listAddress) {
						if ("PRIMARY_LOCATION".equals(m.get("contactMechPurposeType"))) {
							person.put("address1", (String) m.get("address1"));
							break;
						}
					}
				}
				List<Map> listMember = FastList.newInstance();
				for (String s : listPartyFamilyId) {
					List<Map<String, Object>> listMemberId = getMemberOfFamily(s, delegator);
					for (Map<String, Object> m : listMemberId) {
						String partyIdFrom = (String) m.get("partyIdFrom");
						Map<String, Object> getInformationMember = dispatcher.runSync("getPartyInformation",
						UtilMisc.toMap("partyId", partyIdFrom, "userLogin", userLogin));
						Map<String, Object> memberInfo = (Map<String, Object>) getInformationMember.get("partyInfo");
						GenericValue roleType = delegator.findOne("RoleType", UtilMisc.toMap("roleTypeId", m.get("roleTypeIdFrom")), false);
						memberInfo.put("roleTypeFrom", roleType.get("description", locale));
						memberInfo.put("roleTypeIdFrom", m.get("roleTypeIdFrom"));
						memberInfo.put("familyId", s);
						listMember.add(memberInfo);
					}
				}
				person.put("rowDetail", listMember);
				person.putAll(getSaler(delegator, x.getString("partyId")));
				listContactFamily.add(person);
			}
			TotalRows = String.valueOf(dummyParty.getResultsTotalSize());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dummyParty.close();
		}
		result.put("listIterator", listContactFamily);
		result.put("TotalRows", TotalRows);
		return result;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> listCustomersBusinesses(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listContactBusinesses = FastList.newInstance();
		String TotalRows = "0";
		EntityListIterator dummyParty = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			LocalDispatcher dispatcher = ctx.getDispatcher();
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pageNum * pagesize + 1;
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.IN, UtilMisc.toList("CUSTOMER", "INDIVIDUAL_CUSTOMER")));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", organizationId,
													"roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyTypeId", "BUSINESSES")));
			conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator)));
			dummyParty = delegator.find("PartyRelationShipAndGroup",
							EntityCondition.makeCondition(conditions), null, null, UtilMisc.toList("partyId"), opts);
			List<GenericValue> listPartyGroup = dummyParty.getPartialList(start, pagesize);
			for (GenericValue x : listPartyGroup) {
				Map<String, Object> group = FastMap.newInstance();
				group.putAll(x);
				String partyId = x.getString("partyId");
				String partyRepresentId = getPartyRepresentative(partyId, delegator);
				if (UtilValidate.isNotEmpty(partyRepresentId)) {
					group.put("representativeMember", getPartyName(delegator, partyRepresentId));
				}
				Map<String, Object> getPartyInformation = dispatcher.runSync("getPartyInformation",
				UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
				Map<String, Object> partyInfo = (Map<String, Object>) getPartyInformation.get("partyInfo");
				group.put("contactNumber", (String) partyInfo.get("contactNumber"));
				group.put("emailAddress", (String) partyInfo.get("emailAddress"));
				List<Map> listAddress = (List<Map>) partyInfo.get("listAddress");
				if (UtilValidate.isNotEmpty(listAddress)) {
					for (Map<String, Object> m : listAddress) {
						if ("PRIMARY_LOCATION".equals(m.get("contactMechPurposeType"))) {
							group.put("address1", (String) m.get("address1"));
							break;
						}
					}
				}
				group.putAll(getSaler(delegator, x.getString("partyId")));
				listContactBusinesses.add(group);
			}
			TotalRows = String.valueOf(dummyParty.getResultsTotalSize());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dummyParty.close();
		}
		result.put("TotalRows", TotalRows);
		result.put("listIterator", listContactBusinesses);
		return result;
	}
	private static Map<String, Object> getSaler(Delegator delegator, String partyIdTo)
			throws GenericEntityException {
		Map<String, Object> saler = FastMap.newInstance();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyIdTo, "roleTypeIdFrom", "SALES_EXECUTIVE", "partyRelationshipTypeId", "SALES_REP_REL")));
		List<GenericValue> partyRelationships = delegator.findList("PartyRelationship",
						EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), null, null, false);
		if (UtilValidate.isNotEmpty(partyRelationships)) {
			String partyIdFrom = EntityUtil.getFirst(partyRelationships).getString("partyIdFrom");
			saler.put("salerId", partyIdFrom);
			saler.put("saler", getPartyName(delegator, partyIdFrom));
		}
		return saler;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> listCustomersSchool(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException, GenericServiceException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<Map<String, Object>> listContactBusinesses = FastList.newInstance();
		String TotalRows = "0";
		EntityListIterator dummyParty = null;
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
			LocalDispatcher dispatcher = ctx.getDispatcher();
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			Map<String, String[]> parameters= (Map<String,String[]>)context.get("parameters");
			int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
			int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
			int start = pageNum * pagesize + 1;
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.IN, UtilMisc.toList("CUSTOMER", "INDIVIDUAL_CUSTOMER")));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", organizationId, "roleTypeIdTo", "INTERNAL_ORGANIZATIO", "partyTypeId", "SCHOOL")));
			conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator)));
			dummyParty = delegator.find("PartyRelationShipAndGroup",
								EntityCondition.makeCondition(conditions), null, null, UtilMisc.toList("partyId"), opts);
			List<GenericValue> listPartyGroup = dummyParty.getPartialList(start, pagesize);
			for (GenericValue x : listPartyGroup) {
				Map<String, Object> group = FastMap.newInstance();
				group.putAll(x);
				String partyId = x.getString("partyId");
				String partyRepresentId = getPartyRepresentative(partyId, delegator);
				if (UtilValidate.isNotEmpty(partyRepresentId)) {
					group.put("representativeMember", getPartyName(delegator, partyRepresentId));
				}
				Map<String, Object> getPartyInformation = dispatcher.runSync("getPartyInformation",
				UtilMisc.toMap("partyId", partyId, "userLogin", userLogin));
				Map<String, Object> partyInfo = (Map<String, Object>) getPartyInformation.get("partyInfo");
				group.put("contactNumber", (String) partyInfo.get("contactNumber"));
				group.put("emailAddress", (String) partyInfo.get("emailAddress"));
				List<Map> listAddress = (List<Map>) partyInfo.get("listAddress");
				if (UtilValidate.isNotEmpty(listAddress)) {
					for (Map<String, Object> m : listAddress) {
						if ("PRIMARY_LOCATION".equals(m.get("contactMechPurposeType"))) {
							group.put("address1", (String) m.get("address1"));
							break;
						}
					}
				}
				GenericValue listPartyAttrT = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", partyId, "attrName", "Teacher"), false);
				GenericValue listPartyAttrS = delegator.findOne("PartyAttribute", UtilMisc.toMap("partyId", partyId, "attrName", "Student"), false);
				if(UtilValidate.isNotEmpty(listPartyAttrT)) {
					group.put("numberTeacher", (String) listPartyAttrT.get("attrValue"));
				}
				if(UtilValidate.isNotEmpty(listPartyAttrS)) {
					group.put("numberStudent", (String) listPartyAttrS.get("attrValue"));
				}
				group.putAll(getSaler(delegator, x.getString("partyId")));
				listContactBusinesses.add(group);
			}
			TotalRows = String.valueOf(dummyParty.getResultsTotalSize());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dummyParty.close();
		}
		result.put("TotalRows", TotalRows);
		result.put("listIterator", listContactBusinesses);
		return result;
	}
	
	public static String getPartyRepresentative(String partyPartnerBusinessesId, Delegator delegator) throws GenericEntityException {
		String partyId = "";
		List<EntityCondition> listConditions = FastList.newInstance();
		listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyPartnerBusinessesId, "roleTypeIdFrom", "REPRESENTATIVE")));
		List<GenericValue> listPartyRepresentative = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(listConditions), null, null, null, false);
		if (UtilValidate.isNotEmpty(listPartyRepresentative)) {
			GenericValue partyRepresentative = EntityUtil.getFirst(listPartyRepresentative);
			partyId = partyRepresentative.getString("partyIdFrom");
		}
		return partyId;
	}
	public static List<String> getFamilyOfPerson(String partyId, Delegator delegator) throws GenericEntityException {
		List<String> listPartyFamilyId = FastList.newInstance();
		List<EntityCondition> listConditions = FastList.newInstance();
		listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId,
				"roleTypeIdFrom", "REPRESENTATIVE", "roleTypeIdTo", "HOUSEHOLD")));
		List<GenericValue> listFamily = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(listConditions), null, UtilMisc.toList("-fromDate"), null, false);
		listPartyFamilyId = EntityUtil.getFieldListFromEntityList(listFamily, "partyIdTo", true);
		return listPartyFamilyId;
	}
	public static List<Map<String, Object>> getMemberOfFamily(String partyId, Delegator delegator) throws GenericEntityException {
		List<Map<String, Object>> listMember = FastList.newInstance();
		List<EntityCondition> listConditions = FastList.newInstance();
		listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyId, "roleTypeIdTo", "HOUSEHOLD")));
		listConditions.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityJoinOperator.NOT_EQUAL, "REPRESENTATIVE"));
		List<GenericValue> listFamily = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(listConditions), null, null, null, false);
		for (GenericValue x : listFamily) {
			Map<String, Object> member = FastMap.newInstance();
			member.put("partyIdFrom", x.getString("partyIdFrom"));
			member.put("roleTypeIdFrom", x.getString("roleTypeIdFrom"));
			listMember.add(member);
		}
		return listMember;
	}
	private static String getResultCall(Delegator delegator, String partyId) throws GenericEntityException {
		String resultEnumTypeId = "";
		EntityFindOptions opts = new EntityFindOptions();
		opts.setResultSetType(EntityFindOptions.TYPE_SCROLL_SENSITIVE);
		EntityListIterator communicationEvents = null;
		try {
			communicationEvents = delegator.find("CommunicationEvent",
					EntityCondition.makeCondition(UtilMisc.toMap("communicationEventTypeId", "PHONE_COMMUNICATION", "statusId", "COM_COMPLETE",
							"partyIdTo", partyId)), null, null, UtilMisc.toList("-entryDate"), opts);
			List<GenericValue> res = communicationEvents.getPartialList(0, 3);
			for(GenericValue x : res){
				resultEnumTypeId += x.getString("resultEnumTypeId") + ", ";
			}
			if (resultEnumTypeId.length() > 2) {
				resultEnumTypeId = resultEnumTypeId.substring(0, resultEnumTypeId.length() - 2);
			}
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		} finally {
			if(communicationEvents != null){
				communicationEvents.close();
			}
		}
		return resultEnumTypeId;
	}
	public static String getPartyName(Delegator delegator, String partyId) throws GenericEntityException {
		GenericValue party = delegator.findOne("Party", UtilMisc.toMap("partyId", partyId), false);
		if (UtilValidate.isNotEmpty(party)) {
			String partyTypeId = party.getString("partyTypeId");
			switch (partyTypeId) {
			case "PERSON":
				GenericValue person = delegator.findOne("Person", UtilMisc.toMap("partyId", partyId), false);
				if (UtilValidate.isNotEmpty(person)) {
					String partyFullName = "";
					if (UtilValidate.isNotEmpty(person.getString("lastName"))) {
						partyFullName = person.getString("lastName");
					}
					if (UtilValidate.isNotEmpty(person.getString("middleName"))) {
						partyFullName = partyFullName + " " + person.getString("middleName");
					}
					if (UtilValidate.isNotEmpty(person.getString("firstName"))) {
						partyFullName = partyFullName + " " + person.getString("firstName");
					}
					return partyFullName;
				}
				break;
			default:
				GenericValue group = delegator.findOne("PartyGroup", UtilMisc.toMap("partyId", partyId), false);
				if (UtilValidate.isNotEmpty(group)) {
					return group.getString("groupName");
				}
				break;
			}
		}
		return null;
	}
	private static String getGeoName(Delegator delegator, String geoId) throws GenericEntityException {
		String geoName = "";
		if (UtilValidate.isNotEmpty(geoId)) {
			GenericValue geo = delegator.findOne("Geo", UtilMisc.toMap("geoId", geoId), false);
			if (UtilValidate.isNotEmpty(geo)) {
				geoName = geo.getString("geoName");
			}
		}
		return geoName;
	}
	
	public static Map<String, Object> autoCompleteGeoAjax(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String geoTypeId = (String) context.get("geoTypeId");
		String geoId = (String) context.get("geoId");
		List<EntityCondition> conditions = FastList.newInstance();
		
		// check condition by GEO type same level
		if ("PROVINCE".equals(geoTypeId)) {
			conditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("geoTypeId", geoTypeId), 
					EntityOperator.OR, EntityCondition.makeCondition("geoTypeId", "STATE")));
		} else {
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("geoTypeId", geoTypeId)));
		}
		
		List<GenericValue> listGeo = null;
		if (UtilValidate.isNotEmpty(geoId)) {
			conditions.add(EntityCondition.makeCondition("geoIdFrom", geoId));
			listGeo = delegator.findList("GeoAssocAndGeoTo", EntityCondition.makeCondition(conditions), UtilMisc.toSet("geoId", "geoName"), UtilMisc.toList("geoId ASC"), null, false);
		} else {
			listGeo = delegator.findList("Geo", EntityCondition.makeCondition(conditions), UtilMisc.toSet("geoId", "geoName"), UtilMisc.toList("geoId ASC"), null, false);
		}
		result.put("listGeo", listGeo);
		return result;
	}
	
	public static Map<String, Object> getCustomerByPartyIdTo(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException{
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		String partyIdTo = (String) context.get("partyIdTo");
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyIdTo,
				"roleTypeIdFrom", "CUSTOMER", "roleTypeIdTo", "INTERNAL_ORGANIZATIO")));
		List<GenericValue> listCustomer = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("partyIdFrom"), null, null, false);
		List<String> listPartyIdFrom = EntityUtil.getFieldListFromEntityList(listCustomer, "partyIdFrom", true);
		result.put("listPartyIdFrom", listPartyIdFrom);
		return result;
	}
	public static Map<String, Object> getCustomerRole(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException{
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", context.get("partyIdFrom"),
				"partyIdTo", context.get("partyIdTo"), "roleTypeIdTo", context.get("roleTypeIdTo"))));
		List<GenericValue> listCustomer = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(conditions), UtilMisc.toSet("roleTypeIdFrom"), null, null, false);
		List<String> listRoleTypeIdFrom = EntityUtil.getFieldListFromEntityList(listCustomer, "roleTypeIdFrom", true);
		String partyRole = "";
		if (UtilValidate.isNotEmpty(listRoleTypeIdFrom)) {
			partyRole = listRoleTypeIdFrom.get(0);
		}
		result.put("partyRole", partyRole);
		return result;
	}
	
	public static Map<String, Object> createOrStorePartyAttribute(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = ctx.getDelegator();
		GenericValue partyAttribute = delegator.makeValidValue("PartyAttribute", context);
		delegator.createOrStore(partyAttribute);
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listStaffContract(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator listIterator = null;
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<String> listCustomerId = SecurityUtil.getPartiesByRoles("SALES_EXECUTIVE", delegator);
			listAllConditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, listCustomerId));
			listIterator = delegator.find("PartyAndPerson", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("listIterator", listIterator);
		return result;
	}
	public static Map<String, Object> checkAndUpdateRelSalesExecutive(DispatchContext dctx, Map<String, ? extends Object> context) {
    	//LocalDispatcher dispatcher = dctx.getDispatcher();
    	Delegator delegator = dctx.getDelegator();
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	//Locale locale = (Locale) context.get("locale");
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	
    	String customerId = (String) context.get("partyId");
    	String salesExecutiveId = (String) context.get("salesExecutiveId");
    	try {
    		// check has relationship?
    		String currencyOrigizationId = SalesUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
    		checkAndUpdateRelSalesExecutive(delegator, currencyOrigizationId, salesExecutiveId, customerId);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return result;
    }
	public static void checkAndUpdateRelSalesExecutive(Delegator delegator, String organizationId, String salesExecutiveId, String customerId) {
    	Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
    	String partyRelationshipTypeId = SalesUtil.getPropertyValue(delegator, "party.rel.sales.rep.to.customer");
    	String salesExecutiveTypeId = SalesUtil.getPropertyValue(delegator, "role.sales.executive");
    	String orgRoleTypeId = SalesUtil.getPropertyValue(delegator, "role.org.to.customer");
    	List<String> customerTypeIds = SalesPartyUtil.getDescendantRoleTypeIds(SalesUtil.getPropertyValue(delegator, "role.customer"), delegator);
    	try {
    		List<EntityCondition> listAllCondition = FastList.newInstance();
    		listAllCondition.add(EntityCondition.makeCondition("partyIdTo", customerId));
    		listAllCondition.add(EntityCondition.makeCondition("partyRelationshipTypeId", partyRelationshipTypeId));
    		listAllCondition.add(EntityCondition.makeCondition("roleTypeIdFrom", salesExecutiveTypeId));
    		listAllCondition.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.IN, customerTypeIds));
    		List<GenericValue> tmpRel = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllCondition, EntityOperator.AND), null, null, null, false);
    		if (UtilValidate.isNotEmpty(tmpRel) && tmpRel.size() > 0) {
    			// Find and thru lien ket cu
				for (GenericValue executiveRel : tmpRel) {
					executiveRel.put("thruDate", nowTimestamp);
				}
				delegator.storeAll(tmpRel);
    		}
    		
    		//String retailRoleTypeId = SalesUtil.getPropertyValue(delegator, "role.individual.customer");
			List<GenericValue> customerRels = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", 
					UtilMisc.toMap("partyIdFrom", customerId, "partyIdTo", organizationId, "roleTypeIdTo", orgRoleTypeId), null, false));
			if (customerRels != null) {
				List<String> customerRoleIds = EntityUtil.getFieldListFromEntityList(customerRels, "roleTypeIdFrom", true);
				if (customerRoleIds != null && customerRoleIds.size() > 0) {
					String customerRoleId = customerRoleIds.get(0);
					String contactRoleId = SalesUtil.getPropertyValue(delegator, "role.contact.customer");
					String retailRoleId = SalesUtil.getPropertyValue(delegator, "role.individual.customer");
					if (customerRoleId.equals(contactRoleId)) {
						customerRoleId = retailRoleId;
					}
					GenericValue partyRel = delegator.makeValue("PartyRelationship");
					partyRel.put("partyIdFrom", salesExecutiveId);
					partyRel.put("partyIdTo", customerId);
					partyRel.put("roleTypeIdFrom", salesExecutiveTypeId);
					partyRel.put("roleTypeIdTo", customerRoleId);
					partyRel.put("fromDate", nowTimestamp);
					partyRel.put("partyIdFrom", salesExecutiveId);
					partyRel.put("partyRelationshipTypeId", partyRelationshipTypeId);
					delegator.create(partyRel);
				}
			}
    	} catch (GenericEntityException e) {
    		e.printStackTrace();
    	}
    }
}
