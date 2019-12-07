package com.olbius.administration.util;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityFunction;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class UniqueUtil {
	public static void checkPartyCode(Delegator delegator, Object partyId, Object partyCode) throws Exception {
		List<EntityCondition> conditions = FastList.newInstance();
		EntityListIterator party = null;
		try {
			if (UtilValidate.isNotEmpty(partyId)) {
				conditions.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.NOT_EQUAL, partyId));
			}
			if (UtilValidate.isNotEmpty(partyCode)) {
				conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("partyCode"),
						EntityJoinOperator.EQUALS, partyCode.toString().toUpperCase()));
				EntityFindOptions findOptions = new EntityFindOptions();
				findOptions.setMaxRows(1);
				findOptions.setLimit(1);
				party = delegator.find("Party", EntityCondition.makeCondition(conditions), null,
						UtilMisc.toSet("partyId"), null, findOptions);
				if (party.getResultsTotalSize() != 0) {
					throw new Exception("partyCode exists");
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (party != null) {
				party.close();
			}
		}
	}

	public static void checkProductStoreId(Delegator delegator, Object productStoreId) throws Exception {
		List<EntityCondition> conditions = FastList.newInstance();
		EntityListIterator productStore = null;
		EntityListIterator facility = null;
		try {
			if (UtilValidate.isNotEmpty(productStoreId)) {
				conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productStoreId"),
						EntityJoinOperator.EQUALS, productStoreId.toString().toUpperCase()));
				EntityFindOptions findOptions = new EntityFindOptions();
				findOptions.setMaxRows(1);
				findOptions.setLimit(1);
				productStore = delegator.find("ProductStore", EntityCondition.makeCondition(conditions), null,
						UtilMisc.toSet("productStoreId"), null, findOptions);
				if (productStore.getResultsTotalSize() != 0) {
					throw new Exception("productStoreId exists");
				}
				conditions.clear();
				conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productStoreId"),
						EntityJoinOperator.EQUALS, ("FA" + productStoreId).toUpperCase()));
				facility = delegator.find("Facility", EntityCondition.makeCondition(conditions), null,
						UtilMisc.toSet("productStoreId"), null, findOptions);
				if (facility.getResultsTotalSize() != 0) {
					throw new Exception("facilityId exists");
				}
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (productStore != null) {
				productStore.close();
			}
			if (facility != null) {
				facility.close();
			}
		}
	}

	public static boolean checkContactNumber(Delegator delegator, Object contactNumber) {
		if (UtilValidate.isNotEmpty(contactNumber)) {
			EntityListIterator telecomNumber = null;
			try {
				EntityFindOptions findOptions = new EntityFindOptions();
				findOptions.setMaxRows(1);
				findOptions.setLimit(1);
				telecomNumber = delegator.find("TelecomNumber",
						EntityCondition.makeCondition("contactNumber", EntityJoinOperator.EQUALS, contactNumber), null,
						UtilMisc.toSet("contactMechId"), null, findOptions);
				if (telecomNumber.getResultsTotalSize() != 0) {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (telecomNumber != null) {
					try {
						telecomNumber.close();
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return true;
	}

	public static void checkEnumerationCode(Delegator delegator, Object enumCode, Object enumId) throws Exception {
		EntityListIterator enumeration = null;
		try {
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(1);
			findOptions.setLimit(1);
			List<EntityCondition> conditions = FastList.newInstance();
			if (UtilValidate.isNotEmpty(enumId)) {
				conditions.add(EntityCondition.makeCondition("enumId", EntityJoinOperator.NOT_EQUAL, enumId));
			}
			conditions.add(EntityCondition.makeCondition("enumCode", EntityJoinOperator.EQUALS, enumCode));
			enumeration = delegator.find("Enumeration", EntityCondition.makeCondition(conditions), null,
					UtilMisc.toSet("enumCode"), null, findOptions);
			if (enumeration.getResultsTotalSize() != 0) {
				throw new Exception("enumCode exists");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (enumeration != null) {
				enumeration.close();
			}
		}
	}

	public static Map<String, Object> checkEnumerationTypeId(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		EntityListIterator enumerationType = null;
		try {
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(1);
			findOptions.setLimit(1);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(
					EntityCondition.makeCondition("enumTypeId", EntityJoinOperator.EQUALS, context.get("enumTypeId")));
			enumerationType = delegator.find("EnumerationType", EntityCondition.makeCondition(conditions), null,
					UtilMisc.toSet("enumTypeId"), null, findOptions);
			if (enumerationType.getResultsTotalSize() != 0) {
				throw new Exception("enumTypeId exists");
			}
			result.put("check", "true");
		} catch (Exception e) {
			result.put("check", "false");
		} finally {
			if (enumerationType != null) {
				enumerationType.close();
			}
		}
		return result;
	}

	public static void checkProductCode(Delegator delegator, Object productCode, Object productId) throws Exception {
		try {
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(1);
			findOptions.setLimit(1);
			List<EntityCondition> conditions = FastList.newInstance();
			if (UtilValidate.isNotEmpty(productId)) {
				conditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.NOT_EQUAL, productId));
			}
			conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("productCode"),
					EntityJoinOperator.EQUALS, productCode.toString().toUpperCase()));
			List<GenericValue> product = delegator.findList("Product", EntityCondition.makeCondition(conditions), UtilMisc.toSet("productId"), null, findOptions, false);
			if (UtilValidate.isNotEmpty(product)) {
				throw new Exception("productCode exists");
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public static void checkAgreementCode(Delegator delegator, Object agreementCode, Object agreementId)
			throws Exception {
		EntityListIterator agreement = null;
		try {
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(1);
			findOptions.setLimit(1);
			List<EntityCondition> conditions = FastList.newInstance();
			if (UtilValidate.isNotEmpty(agreementId)) {
				conditions.add(EntityCondition.makeCondition("agreementId", EntityJoinOperator.NOT_EQUAL, agreementId));
			}
			conditions.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("agreementCode"),
					EntityJoinOperator.EQUALS, agreementCode.toString().toUpperCase()));
			agreement = delegator.find("Agreement", EntityCondition.makeCondition(conditions), null,
					UtilMisc.toSet("agreementCode"), null, findOptions);
			if (agreement.getResultsTotalSize() != 0) {
				throw new Exception("agreementCode exists");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (agreement != null) {
				agreement.close();
			}
		}
	}

	public static void checkUserLoginId(Delegator delegator, Object userLoginId) throws Exception {
		EntityListIterator userLogin = null;
		try {
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(1);
			findOptions.setLimit(1);
			userLogin = delegator.find("UserLogin",
					EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("userLoginId"), EntityJoinOperator.EQUALS,
							userLoginId.toString().toUpperCase()),
					null, UtilMisc.toSet("userLoginId"), null, findOptions);
			if (userLogin.getResultsTotalSize() != 0) {
				throw new Exception("userLoginId exists");
			}
		} catch (Exception e) {
			throw e;
		} finally {
			if (userLogin != null) {
				userLogin.close();
			}
		}
	}

	public static Map<String, Object> demarcatePersonName(String partyFullName) {
		Map<String, Object> result = FastMap.newInstance();
		if (UtilValidate.isNotEmpty(partyFullName)) {
			String[] arrayName = partyFullName.split("\\s+");
			switch (arrayName.length) {
			case 1:
				result.put("firstName", arrayName[0].trim());
				result.put("middleName", null);
				result.put("lastName", null);
				break;
			case 2:
				result.put("lastName", arrayName[0].trim());
				result.put("middleName", null);
				result.put("firstName", arrayName[1].trim());
				break;
			case 3:
				result.put("lastName", arrayName[0].trim());
				result.put("middleName", arrayName[1].trim());
				result.put("firstName", arrayName[2].trim());
				break;
			default:
				if (arrayName.length > 3) {
					result.put("lastName", arrayName[0].trim());
					result.put("middleName", arrayName[1].trim());
					String firstName = "";
					for (int i = 2; i < arrayName.length; i++) {
						firstName += arrayName[i] + " ";
					}
					result.put("firstName", firstName.trim());
				}
				break;
			}
		}
		return result;
	}
}
