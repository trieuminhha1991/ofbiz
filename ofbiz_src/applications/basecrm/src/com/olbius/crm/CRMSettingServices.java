package com.olbius.crm;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.administration.util.CrabEntity;
import com.olbius.administration.util.UniqueUtil;

import javolution.util.FastList;

public class CRMSettingServices {

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCommunicationSubject(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("enumCode");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			listAllConditions.add(EntityCondition.makeCondition("enumTypeId", "COMM_SUBJECT"));
			EntityListIterator listIterator = delegator.find("Enumeration",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> checkEnumerationCode(DispatchContext ctx, Map<String, ? extends Object> context)
			throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			UniqueUtil.checkEnumerationCode(delegator, context.get("enumCode"), context.get("enumId"));
			result.put("check", "true");
		} catch (Exception e) {
			result.put("check", "false");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCommunicationResult(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("parentTypeId");
			listSortFields.add("enumTypeId");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("parentTypeId", "CONTACTED"));
			conditions.add(EntityCondition.makeCondition("parentTypeId", "UNCONTACTED"));
			listAllConditions.add(EntityCondition.makeCondition(conditions, EntityJoinOperator.OR));
			EntityListIterator listIterator = delegator.find("EnumerationType",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> createEnumerationType(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		try {
			GenericValue enumerationType = delegator.makeValidValue("EnumerationType", context);
			enumerationType.create();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> updateEnumerationType(DispatchContext dcpt, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dcpt.getDelegator();
		try {
			GenericValue enumerationType = delegator.makeValidValue("EnumerationType", context);
			enumerationType.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> deleteEnumerationType(DispatchContext dcpt, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dcpt.getDelegator();
		try {
			GenericValue enumerationType = delegator.makeValidValue("EnumerationType", context);
			enumerationType.remove();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listCommunicationReason(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			listSortFields.add("enumTypeId");
			listSortFields.add("enumCode");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition("parentEnumTypeId", "CONTACTED"));
			conditions.add(EntityCondition.makeCondition("parentEnumTypeId", "UNCONTACTED"));
			listAllConditions.add(EntityCondition.makeCondition(conditions, EntityJoinOperator.OR));
			EntityListIterator listIterator = delegator.find("EnumTypeChildAndEnum",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listRivals(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			EntityListIterator listIterator = delegator.find("PartyRivals",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> createRival(DispatchContext dctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		try {
			UniqueUtil.checkPartyCode(delegator, context.get("partyId"), context.get("partyCode"));
			Map<String, Object> partyGroup = dispatcher.runSync("createPartyGroup",
					CrabEntity.fastMaking(delegator, "PartyGroup", context));
			String partyId = (String) partyGroup.get("partyId");
			delegator.storeByCondition("Party", UtilMisc.toMap("partyCode", context.get("partyCode")),
					EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)));
			dispatcher.runSync("createPartyRole", UtilMisc.toMap("partyId", partyId, "roleTypeId", "COM_SUPPLIER",
					"userLogin", context.get("userLogin")));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductsOfRivals(DispatchContext ctx, Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");
			EntityListIterator listIterator = delegator.find("ProductsOfRivals",
					EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
			result.put("listIterator", listIterator);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> createRivalsProduct(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		try {
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyId = (String) context.get("partyId");
			String productName = (String) context.get("productName");
			// createProduct
			String productId = delegator.getNextSeqId("Product");
			Map<String, Object> mapCreateProduct = dispatcher.runSync("createProduct",
					UtilMisc.toMap("productId", productId, "productCode", productId, "productTypeId", "FINISHED_GOOD",
							"internalName", productName, "productName", productName, "numRival", "1", "userLogin",
							userLogin)); // MARKETING_PKG
			if (ServiceUtil.isError(mapCreateProduct)) {
				return ServiceUtil.returnError(
						UtilProperties.getMessage("BaseSalesErrorUiLabels", "BSErrorWhenProcessing", locale));
			}
			productId = (String) mapCreateProduct.get("productId");
			// createSupplierProduct
			dispatcher.runSync("createSupplierProduct",
					UtilMisc.toMap("productId", productId, "partyId", partyId, "availableFromDate",
							new Timestamp(System.currentTimeMillis()), "minimumOrderQuantity", BigDecimal.ZERO,
							"currencyUomId", "VND", "lastPrice", BigDecimal.ZERO, "userLogin", userLogin));
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError("error");
		}
		return result;
	}

	public static Map<String, Object> getProductsInSubject(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<GenericValue> products = delegator.findList(
					"EnumerationRelProductDetail", EntityCondition.makeCondition(UtilMisc.toMap("enumId",
							context.get("enumId"), "enumRelTypeId", context.get("enumRelTypeId"))),
					null, UtilMisc.toList("sequenceNum"), null, false);
			result.put("products", products);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public static Map<String, Object> createEnumerationRelProduct(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue enumerationRelProduct = delegator.makeValidValue("EnumerationRelProduct", context);
			enumerationRelProduct.create();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> updateEnumerationRelProduct(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue enumerationRelProduct = delegator.makeValidValue("EnumerationRelProduct", context);
			enumerationRelProduct.store();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	public static Map<String, Object> deleteEnumerationRelProduct(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			GenericValue enumerationRelProduct = delegator.makeValidValue("EnumerationRelProduct", context);
			enumerationRelProduct.remove();
		} catch (Exception e) {
			e.printStackTrace();
			result = ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductNotInSubject(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		try {
			List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
			List<String> listSortFields = (List<String>) context.get("listSortFields");
			EntityFindOptions opts = (EntityFindOptions) context.get("opts");

			Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
			if (parameters.containsKey("enumId")) {
				String enumId = parameters.get("enumId")[0];
				List<GenericValue> products = delegator.findList("EnumerationRelProduct",
						EntityCondition.makeCondition(UtilMisc.toMap("enumId", enumId, "enumRelTypeId", "RECOMMENDED")),
						null, UtilMisc.toList("sequenceNum"), null, false);
				listAllConditions.add(EntityCondition.makeCondition("productId", EntityJoinOperator.NOT_IN,
						EntityUtil.getFieldListFromEntityList(products, "productId", true)));
				listAllConditions.add(
						EntityCondition.makeCondition("productTypeId", EntityJoinOperator.EQUALS, "FINISHED_GOOD"));
				EntityListIterator listIterator = delegator.find("Product",
						EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
				result.put("listIterator", listIterator);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
