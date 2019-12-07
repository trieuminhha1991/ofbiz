package com.olbius.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transaction;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.GeneralException;
import org.ofbiz.base.util.GeneralRuntimeException;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilHttp;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.jdbc.ConnectionFactory;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.stats.VisitHandler;
import org.ofbiz.webapp.website.WebSiteWorker;

import com.olbius.order.OrderReadHelper;
import com.olbius.order.ShoppingCartEvents;
import com.olbius.order.ShoppingCartHelper;
import com.olbius.util.SecurityUtil;

import org.ofbiz.marketing.tracking.TrackingCodeEvents;
import org.ofbiz.order.order.OrderServices;
import org.ofbiz.order.shoppingcart.*;
import org.ofbiz.party.contact.*;

public class DelysServices {
	public static Role ROLE = null;
	public static GenericValue USER_LOGIN = null;
	public static String PARTY_ID = null;

	public enum Role {
		DELYS_ADMIN, DELYS_ROUTE, DELYS_ASM_GT, DELYS_RSM_GT, DELYS_CSM_GT, DELYS_CUSTOMER_GT, DELYS_SALESSUP_GT;
	}

	public static final String module = DelysServices.class.getName();
	public static final String resource = "DelysUiLabels";
	public static final String resourceError = "DelysErrorUiLabels";

	public static Map<String, Object> createPersonDelys(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		// in most cases userLogin will be null, but get anyway so we can keep
		// track of that info if it is available
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> mapTemp = new HashMap<String, Object>();
		Map<String, Object> person = UtilMisc.toMap("firstName",
				context.get("firstName"), "middleName",
				context.get("midleName"), "lastName", context.get("lastName"),
				"preferredCurrencyUomId",
				context.get("preferredCurrencyUomId"), "statusId",
				"PARTY_ENABLED", "userLogin", userLogin);
		try {
			mapTemp = dispatcher.runSync("createPerson", person);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());

		}
		String partyId = (String) mapTemp.get("partyId");
		java.sql.Date fromDate = (java.sql.Date) context.get("fromDate");
		java.sql.Date thruDate = (java.sql.Date) context.get("thruDate");

		Map<String, String> serviceTwoCtx = UtilMisc.toMap("partyId", partyId,
				"partyIdFrom", partyId, "roleTypeIdFrom", "MANAGER",
				"statusId", "", "groupId", "", "comments", "", "fromDate",
				fromDate, "thruDate", thruDate, "userLogin", userLogin);
		// StoreRole
		Map<String, Object> mapStoreRole = UtilMisc.toMap("partyId", partyId,
				"userLogin", userLogin);
		// PartyRole
		Map<String, Object> mapPartyRole = UtilMisc.toMap("partyId", partyId,
				"userLogin", userLogin);
		// DELYS_SALESMAN
		String XsmPosition = (String) context.get("csmPosition");
		if (XsmPosition != null && !XsmPosition.isEmpty()) {
			serviceTwoCtx.put("roleTypeIdTo", "DELYS_CSM");
			mapPartyRole.put("roleTypeId", "MANAGER");
		}
		if (XsmPosition == null || XsmPosition.isEmpty()) {
			XsmPosition = (String) context.get("rsmPosition");
			serviceTwoCtx.put("roleTypeIdTo", "DELYS_RSM");
			mapPartyRole.put("roleTypeId", "MANAGER");
		}
		if (XsmPosition == null || XsmPosition.isEmpty()) {
			XsmPosition = (String) context.get("asmPosition");
			serviceTwoCtx.put("roleTypeIdTo", "DELYS_ASM");
			mapPartyRole.put("roleTypeId", "MANAGER");
		}
		if (XsmPosition == null || XsmPosition.isEmpty()) {
			XsmPosition = (String) context.get("asmPositionParent");
			serviceTwoCtx.put("roleTypeIdTo", "DELYS_ASM");
			serviceTwoCtx.put("roleTypeIdFrom", "DELYS_SALESMAN");
			// assign store
			mapStoreRole.put("roleTypeId", "EMPLOYEE");
			mapPartyRole.put("roleTypeId", "EMPLOYEE");
			mapStoreRole.put("productStoreId",
					(String) context.get("productStoreId"));
			try {
				// createProductStoreRole
				dispatcher.runSync("createProductStoreRole", mapStoreRole);
			} catch (Exception ex) {
				System.out.println(ex);
			}
		}
		if (XsmPosition == null || XsmPosition.isEmpty()) {
			XsmPosition = (String) context.get("customerPosition");
			serviceTwoCtx.put("roleTypeIdTo", "MANAGER");
			serviceTwoCtx.put("roleTypeIdFrom", "DELYS_CUSTOMER");
		}

		serviceTwoCtx.put("partyRelationshipTypeId", "GROUP_ROLLUP");
		serviceTwoCtx.put("partyIdTo", XsmPosition);

		try {
			dispatcher.runSync("createPartyRole", mapPartyRole);
			dispatcher.runSync("createPartyRelationship", serviceTwoCtx);
		} catch (Exception ex) {
			System.out.println(ex);
		}
		result.put("partyId", partyId);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);

		return result;
	}

	public static Map<String, Object> initCreateCSM(DispatchContext ctx,
			Map<String, ? extends Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("roleTypeId",
				EntityOperator.EQUALS, "DELYS_CSM"));
		List<GenericValue> listCSMGroup = null;
		try {
			listCSMGroup = delegator.findList(
					"PartyGroupByPartyRelationShipTypeView",
					EntityCondition.makeCondition(exprs, EntityOperator.AND),
					null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());

		}
		result.put("listCSMGroup", listCSMGroup);
		return result;
	}

	public static Map<String, Object> getAllCSM(DispatchContext ctx,
			Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("roleTypeId",
				EntityOperator.EQUALS, "DELYS_CSM"));
		List<GenericValue> listAll = null;
		try {
			listAll = delegator.findList("PartyGroupAndRole",
					EntityCondition.makeCondition(exprs, EntityOperator.AND),
					null, null, null, false);
			listAll = EntityUtil.filterByDate(listAll);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());

		}
		result.put("listAllCSM", listAll);
		return result;
	}

	public static List<GenericValue> getListPartyRelFrom(DispatchContext ctx,
			String partyIdFrom, String roleTypeIdFrom, String roleTypeIdTo)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("partyIdFrom",
				EntityOperator.EQUALS, partyIdFrom));
		exprs.add(EntityCondition.makeCondition("roleTypeIdFrom",
				EntityOperator.EQUALS, roleTypeIdFrom));
		exprs.add(EntityCondition.makeCondition("roleTypeIdTo",
				EntityOperator.EQUALS, roleTypeIdTo));
		List<GenericValue> listAll = null;
		listAll = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(exprs, EntityOperator.AND), null,
				null, null, false);
		listAll = EntityUtil.filterByDate(listAll);
		return listAll;
	}

	public static List<GenericValue> getListPartyRelTo(DispatchContext ctx,
			String partyIdTo, String roleTypeIdFrom, String roleTypeIdTo)
			throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("partyIdTo",
				EntityOperator.EQUALS, partyIdTo));
		exprs.add(EntityCondition.makeCondition("roleTypeIdFrom",
				EntityOperator.EQUALS, roleTypeIdFrom));
		exprs.add(EntityCondition.makeCondition("roleTypeIdTo",
				EntityOperator.EQUALS, roleTypeIdTo));
		List<GenericValue> listAll = null;
		listAll = delegator.findList("PartyRelationshipAndDetail",
				EntityCondition.makeCondition(exprs, EntityOperator.AND), null,
				null, null, false);
		return listAll;
	}

	public static Map<String, Object> getListEmpl(DispatchContext ctx,
			Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listAllSalesTemp = getListPartyRelTo(ctx,
				(String) context.get("asmId"), "DELYS_SALESMAN", "DELYS_ASM");
		List<GenericValue> listAllSalesMan = new ArrayList<GenericValue>();
		for (GenericValue item : listAllSalesTemp) {
			List<EntityExpr> exprs = FastList.newInstance();
			exprs.add(EntityCondition.makeCondition("partyId",
					EntityOperator.EQUALS, item.get("partyIdFrom")));
			List<GenericValue> listAll = new ArrayList<GenericValue>();
			;
			try {
				listAll = delegator.findList("Person", EntityCondition
						.makeCondition(exprs, EntityOperator.AND), null, null,
						null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());

			}

			if (!(listAll == null || listAll.isEmpty())) {
				listAllSalesMan.addAll(listAll);
			}
		}
		result.put("listAllSalesMan", listAllSalesMan);
		return result;
	}

	/*
	 * get all RSMs when knew csmId
	 */

	public static Map<String, Object> getRSMsEmpl(DispatchContext dcxt,
			Map<String, Object> context) {

		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dcxt.getDelegator();
		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("partyIdFrom",
				EntityOperator.EQUALS, (String) context.get("csmId")));
		List<GenericValue> listAllRSM = null;
		try {
			listAllRSM = delegator.findList("PartyRelationshipAndDetail",
					EntityCondition.makeCondition(exprs, EntityOperator.AND),
					null, null, null, false);
			listAllRSM = EntityUtil.filterByDate(listAllRSM);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());

		}

		result.put("listRSMsEmpl", listAllRSM);
		return result;
	}

	/*
	 * get all ASMs when knew rsmId
	 */

	public static Map<String, Object> getASMsEmpl(DispatchContext dcxt,
			Map<String, ?> context) {

		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dcxt.getDelegator();
		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("partyIdFrom",
				EntityOperator.EQUALS, (String) context.get("rsmId")));
		List<GenericValue> listAll = null;
		try {
			listAll = delegator.findList("PartyRelationshipAndDetail",
					EntityCondition.makeCondition(exprs, EntityOperator.AND),
					null, null, null, false);
			listAll = EntityUtil.filterByDate(listAll);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());

		}
		result.put("listASMsEmpl", listAll);
		return result;
	}

	/*
	 * get all Routes when knew asmId
	 */
	public static Map<String, Object> getRoutesEmpl(DispatchContext dcxt,
			Map<String, ?> context) {

		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dcxt.getDelegator();
		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("partyIdFrom",
				EntityOperator.EQUALS, (String) context.get("asmId")));
		List<GenericValue> listAll = null;
		try {
			listAll = delegator.findList("PartyRelationshipAndDetail",
					EntityCondition.makeCondition(exprs, EntityOperator.AND),
					null, null, null, false);
			listAll = EntityUtil.filterByDate(listAll);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());

		}
		result.put("listRoutesEmpl", listAll);
		return result;
	}

	/*
	 * 
	 * Create partyGroup for Delys
	 */

	public static Map<String, Object> createPartyGroupDelys(
			DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {

		Map<String, Object> result = FastMap.newInstance();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> mapTemp = new HashMap<String, Object>();
		String description = (String) context.get("description");
		Map<String, Object> partyGroup = UtilMisc.toMap(
				"preferredCurrencyUomId",
				context.get("preferredCurrencyUomId"), "groupName",
				context.get("groupName"), "comments", description, "userLogin",
				userLogin, "statusId", "PARTY_ENABLED");
		try {
			mapTemp = dispatcher.runSync("createPartyGroup", partyGroup);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}

		String partyId = (String) mapTemp.get("partyId");
		String roleType = (String) context.get("roleTypeId");
		java.sql.Date fromDate = (java.sql.Date) context.get("fromDate");
		Map<String, Object> serviceTwoCtx = null;
		// Insert role
		Map<String, Object> mapPartyRole = new HashMap<String, Object>();
		mapPartyRole.put("partyId", partyId);
		mapPartyRole.put("userLogin", userLogin);
		String status = "1";

		String role = (String) context.get("role");
		Role highestRole = Role.valueOf(role);
		String partyIdFrom = null;

		if (("DELYS_ADMIN").equals(roleType)) {
			partyIdFrom = EntityUtilProperties.getPropertyValue(
					"general.properties", "ORGANIZATION_PARTY", delegator);

			serviceTwoCtx = UtilMisc.toMap("partyIdFrom", partyIdFrom,
					"partyId", partyId, "partyIdTo", partyId, "roleTypeIdTo",
					"DELYS_CSM", "roleTypeIdFrom", "DELYS_ADMIN", "statusId",
					"", "groupId", "", "comments", description, "fromDate",
					fromDate, "thruDate", "", "userLogin", userLogin,
					"partyRelationshipTypeId", "GROUP_ROLLUP");
			mapPartyRole.put("roleTypeId", "DELYS_CSM");
		} else {
			if (("DELYS_CSM").equals(roleType)) {
				partyIdFrom = (String) context.get("csmId");
				serviceTwoCtx = UtilMisc.toMap("partyIdFrom", partyIdFrom,
						"partyId", partyId, "partyIdTo", partyId,
						"roleTypeIdTo", "DELYS_RSM", "roleTypeIdFrom",
						"DELYS_CSM", "statusId", "", "groupId", "", "comments",
						description, "fromDate", fromDate, "thruDate", "",
						"userLogin", userLogin, "partyRelationshipTypeId",
						"GROUP_ROLLUP");
				mapPartyRole.put("roleTypeId", "DELYS_RSM");
			} else {
				if (("DELYS_RSM").equals(roleType)) {
					partyIdFrom = (String) context.get("rsmId");
					serviceTwoCtx = UtilMisc.toMap("partyIdFrom", partyIdFrom,
							"partyId", partyId, "partyIdTo", partyId,
							"roleTypeIdTo", "DELYS_ASM", "roleTypeIdFrom",
							"DELYS_RSM", "statusId", "", "groupId", "",
							"comments", description, "fromDate", fromDate,
							"thruDate", "", "userLogin", userLogin,
							"partyRelationshipTypeId", "GROUP_ROLLUP");
					mapPartyRole.put("roleTypeId", "DELYS_ASM");
				} else {
					if (("DELYS_ASM").equals(roleType)) {
						partyIdFrom = (String) context.get("asmId");
						serviceTwoCtx = UtilMisc.toMap("partyIdFrom",
								partyIdFrom, "partyId", partyId, "partyIdTo",
								partyId, "roleTypeIdTo", "DELYS_ROUTE",
								"roleTypeIdFrom", "DELYS_ASM", "statusId", "",
								"groupId", "", "comments", description,
								"fromDate", fromDate, "thruDate", "",
								"userLogin", userLogin,
								"partyRelationshipTypeId", "GROUP_ROLLUP");
						mapPartyRole.put("roleTypeId", "DELYS_ROUTE");
					} else {
						if (("DELYS_CUSTOMER").equals(roleType)) {
							partyIdFrom = (String) context.get("routeId");
							serviceTwoCtx = UtilMisc.toMap("partyIdFrom",
									partyIdFrom, "partyId", partyId,
									"partyIdTo", partyId, "roleTypeIdTo",
									"DELYS_CUSTOMER", "roleTypeIdFrom",
									"DELYS_ROUTE", "statusId", "", "groupId",
									"", "comments", description, "fromDate",
									fromDate, "thruDate", "", "userLogin",
									userLogin, "partyRelationshipTypeId",
									"GROUP_ROLLUP");
							mapPartyRole.put("roleTypeId", "DELYS_CUSTOMER");
						}
					}
				}
			}
		}

		status = "0";
		result.put("status", status);
		try {
			dispatcher.runSync("createPartyRole", mapPartyRole);
		} catch (GenericServiceException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		try {
			dispatcher.runSync("createPartyRelationship", serviceTwoCtx);

			if ("DELYS_ADMIN".equals(role)) {
				Map<String, Object> initMap = FastMap.newInstance();
				initMap = initCreateGroupEmpl(ctx, context);
				result.put("listCSM", initMap.get("listCSM"));
				result.put("listRSM", initMap.get("listRSM"));
				result.put("listASM", initMap.get("listASM"));
				result.put("listROUTE", initMap.get("listROUTE"));
				result.put("listCUSTOMER", initMap.get("listCUSTOMER"));
			} else {
				if ("DELYS_CSM".equals(role)) {
					Map<String, Object> initMap = FastMap.newInstance();
					initMap = initCreateGroupEmpl(ctx, context);
					result.put("listRSM", initMap.get("listRSM"));
					result.put("listASM", initMap.get("listASM"));
					result.put("listROUTE", initMap.get("listROUTE"));
					result.put("listCUSTOMER", initMap.get("listCUSTOMER"));
				} else {
					if ("DELYS_RSM".equals(role)) {
						Map<String, Object> initMap = FastMap.newInstance();
						initMap = initCreateGroupEmpl(ctx, context);
						result.put("listASM", initMap.get("listASM"));
						result.put("listROUTE", initMap.get("listROUTE"));
						result.put("listCUSTOMER", initMap.get("listCUSTOMER"));
					} else {
						if ("DELYS_ASM".equals(role)) {
							Map<String, Object> initMap = FastMap.newInstance();
							initMap = initCreateGroupEmpl(ctx, context);
							result.put("listROUTE", initMap.get("listROUTE"));
							result.put("listCUSTOMER",
									initMap.get("listCUSTOMER"));
						} else {
							if ("DELYS_CUSTOMER".equals(role)) {
								Map<String, Object> initMap = FastMap
										.newInstance();
								initMap = initCreateGroupEmpl(ctx, context);
								result.put("listCUSTOMER",
										initMap.get("listCUSTOMER"));
							}
						}
					}
				}
			}

		} catch (Exception ex) {
			Debug.logError(ex, module);
			return ServiceUtil.returnError(ex.getMessage());
		}
		result.put("csmId", (String) context.get("csmId"));
		result.put("rsmId", (String) context.get("rsmId"));
		result.put("asmId", (String) context.get("asmId"));
		result.put("routeId", (String) context.get("routeId"));
		result.put("partyId", partyId);
		result.put("role", highestRole.toString());
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		return result;
	}

	/*
	 * 
	 * get list of party in PartyRole by roleTypeId
	 */
	public static List<GenericValue> getListEmplByRole(DispatchContext dcxt,
			Map<String, Object> context, String roleFrom, String roleTo)
			throws GenericEntityException {
		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("roleTypeIdFrom",
				EntityOperator.EQUALS, roleFrom));
		exprs.add(EntityCondition.makeCondition("roleTypeIdTo",
				EntityOperator.EQUALS, roleTo));
		List<GenericValue> listAll = null;
		Delegator delegator = dcxt.getDelegator();
		listAll = delegator.findList("PartyRelationshipAndDetail",
				EntityCondition.makeCondition(exprs, EntityOperator.AND), null,
				null, null, false);
		return listAll;
	}

	/*
	 * 
	 * get list party is partyTo in partyRelationship when knew partyFrom
	 */
	public static List<GenericValue> getListEmplByParentId(
			DispatchContext dcxt, Map<String, Object> context, String parentId,
			String childType) throws GenericEntityException {
		List<EntityExpr> exprs = FastList.newInstance();

		exprs.add(EntityCondition.makeCondition("partyIdFrom",
				EntityOperator.EQUALS, parentId));
		exprs.add(EntityCondition.makeCondition("roleTypeIdTo",
				EntityOperator.EQUALS, childType));
		List<GenericValue> listAll = null;

		Delegator delegator = dcxt.getDelegator();
		listAll = delegator.findList("PartyRelationshipAndDetail",
				EntityCondition.makeCondition(exprs, EntityOperator.AND), null,
				null, null, false);
		listAll = EntityUtil.filterByDate(listAll);
		return listAll;
	}

	/*
	 * get PartyRelationship Object by partyIdFrom, roleTypeIdFrom and
	 * roleRypeIdTo
	 */

	public static List<GenericValue> getPartyRelByPartyIdRoleFromAndTo(
			DispatchContext dcxt, Map<String, Object> context,
			String partyIdFrom, String roleTypeFrom, String roleTypeTo)
			throws GenericEntityException {
		Delegator delegator = dcxt.getDelegator();
		List<GenericValue> listReturn = new ArrayList<GenericValue>();
		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("partyIdFrom",
				EntityOperator.EQUALS, partyIdFrom));
		exprs.add(EntityCondition.makeCondition("roleTypeIdFrom",
				EntityOperator.EQUALS, "MANAGER"));
		exprs.add(EntityCondition.makeCondition("roleTypeIdTo",
				EntityOperator.EQUALS, "DELYS_CSM"));
		listReturn = delegator.findList("PartyRelationshipAndDetail",
				EntityCondition.makeCondition(exprs, EntityOperator.AND), null,
				null, null, false);
		return listReturn;
	}

	/*
	 * 
	 * check party had role but not have relationship, will be add to
	 * relationship DELYS_ADMIN with DELYS_ (this party will be managed by
	 * DELYS_ADMIN)
	 */

	public static List<GenericValue> createRelationshipForFreeParty(
			List<GenericValue> listRel, List<GenericValue> listRole,
			DispatchContext ctx, Map<String, Object> context)
			throws GenericEntityException {

		if (!listRole.isEmpty()) {
			LocalDispatcher dispatcher = ctx.getDispatcher();
			Delegator delegator = ctx.getDelegator();
			java.sql.Date fromDate = (java.sql.Date) context.get("fromDate");
			GenericValue userLogin = (GenericValue) context.get("userLogin");
			String partyIdFrom = EntityUtilProperties.getPropertyValue(
					"general.properties", "ORGANIZATION_PARTY", delegator);
			if (listRel.isEmpty()) {
				for (GenericValue item : listRole) {
					// create new relationship with DELYS_ADMIN
					Map<String, Object> serviceTwoCtx = null;
					serviceTwoCtx = UtilMisc.toMap("partyIdFrom", partyIdFrom,
							"partyId", item.get("partyId"), "partyIdTo",
							item.get("partyId"), "roleTypeIdTo",
							item.get("roleTypeId"), "roleTypeIdFrom",
							"DELYS_ADMIN", "statusId", "", "groupId", "",
							"comments", "", "fromDate", fromDate, "thruDate",
							"", "userLogin", userLogin,
							"partyRelationshipTypeId", "GROUP_ROLLUP");
					try {
						dispatcher.runSync("createPartyRelationship",
								serviceTwoCtx);
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
					listRel.add(item);
				}
			} else {
				if (listRel.size() != listRole.size()) {
					for (GenericValue role : listRole) {
						boolean check = false;
						for (GenericValue rel : listRel) {
							if (!role.get("partyId").equals(
									rel.get("partyIdTo"))) {
								check = true;
							} else {
								check = false;
							}
						}
						if (check) {
							// create new relationship with DELYS_ADMIN
							Map<String, Object> serviceTwoCtx = null;
							serviceTwoCtx = UtilMisc.toMap("partyIdFrom",
									partyIdFrom, "partyId",
									role.get("partyId"), "partyIdTo",
									role.get("partyId"), "roleTypeIdTo",
									role.get("roleTypeId"), "roleTypeIdFrom",
									"DELYS_ADMIN", "statusId", "", "groupId",
									"", "comments", "", "fromDate", fromDate,
									"thruDate", "", "userLogin", userLogin,
									"partyRelationshipTypeId", "GROUP_ROLLUP");
							try {
								dispatcher.runSync("createPartyRelationship",
										serviceTwoCtx);
							} catch (GenericServiceException e) {
								e.printStackTrace();
							}
							GenericValue gen = null;
							gen = delegator.findOne(
									"PartyRelationshipAndDetail", UtilMisc
											.toMap("partyIdFrom", partyIdFrom,
													"partyId",
													role.get("partyId"),
													"partyIdTo",
													role.get("partyId"),
													"roleTypeIdTo",
													role.get("roleTypeId"),
													"roleTypeIdFrom",
													"DELYS_ADMIN", "fromDate",
													fromDate), false);
							listRel.add(gen);
						}
					}
				}
			}
		}
		return listRel;
	}

	/*
	 * 
	 * prepare initial data for create an group of employee - DELYS_CSM,
	 * DELYS_RSM, DELYS_ASM, ADMIN
	 */

	public static Map<String, Object> initCreateGroupEmpl(DispatchContext dcxt,
			Map<String, Object> context) throws GenericEntityException {
		String status = "1";
		String roleTemp = (String) context.get("role");
		String roleTypeId = (String) context.get("roleTypeId");
		if (roleTemp == null) {
			Map<String, Object> roleMap = ServiceUtil.returnSuccess();
			roleMap = checkRoleForDelysEmpl(dcxt, context);
			roleTemp = (String) roleMap.get("role");
		}
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) userLogin.get("partyId");
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = dcxt.getDelegator();
		Role role = Role.valueOf(roleTemp);
		switch (role) {
		case DELYS_ADMIN: {
			List<GenericValue> listCsmTemp = new ArrayList<GenericValue>();
			List<EntityExpr> exprs = FastList.newInstance();
			exprs.add(EntityCondition.makeCondition("roleTypeIdFrom",
					EntityOperator.EQUALS, "DELYS_ADMIN"));
			exprs.add(EntityCondition.makeCondition("roleTypeIdTo",
					EntityOperator.EQUALS, "DELYS_CSM"));
			listCsmTemp = delegator.findList("PartyRelationshipAndDetail",
					EntityCondition.makeCondition(exprs, EntityOperator.AND),
					null, null, null, false);
			listCsmTemp = EntityUtil.filterByDate(listCsmTemp);

			result.put("listCSM", listCsmTemp);
			List<GenericValue> listRsmTemp = new ArrayList<GenericValue>();
			if (!listCsmTemp.isEmpty()) {
				for (GenericValue value : listCsmTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"), "DELYS_RSM");
					if (!listTemp.isEmpty()) {
						listRsmTemp.addAll(listTemp);
					}
				}
			}
			result.put("listRSM", listRsmTemp);
			List<GenericValue> listAsmTemp = new ArrayList<GenericValue>();
			if (!listRsmTemp.isEmpty()) {
				for (GenericValue value : listRsmTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"), "DELYS_ASM");
					if (!listTemp.isEmpty()) {
						listAsmTemp.addAll(listTemp);
					}
				}
			}
			result.put("listASM", listAsmTemp);

			List<GenericValue> listRouteTemp = new ArrayList<GenericValue>();
			if (!listAsmTemp.isEmpty()) {
				for (GenericValue value : listAsmTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"),
							"DELYS_ROUTE");
					if (!listTemp.isEmpty()) {
						listRouteTemp.addAll(listTemp);
					}
				}
			}
			result.put("listROUTE", listRouteTemp);

			List<GenericValue> listCustTemp = new ArrayList<GenericValue>();
			if (!listRouteTemp.isEmpty()) {
				for (GenericValue value : listRouteTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"),
							"DELYS_CUSTOMER");
					if (!listTemp.isEmpty()) {
						listCustTemp.addAll(listTemp);
					}
				}
			}
			result.put("listCUSTOMER", listCustTemp);
			break;
		}
		case DELYS_CSM_GT: {
			List<EntityExpr> exprs = FastList.newInstance();
			exprs.add(EntityCondition.makeCondition("partyIdFrom",
					EntityOperator.EQUALS, partyId));
			exprs.add(EntityCondition.makeCondition("roleTypeIdFrom",
					EntityOperator.EQUALS, "MANAGER"));
			exprs.add(EntityCondition.makeCondition("roleTypeIdTo",
					EntityOperator.EQUALS, "DELYS_CSM"));
			List<GenericValue> listCsmTemp = new ArrayList<GenericValue>();
			try {
				listCsmTemp = delegator.findList("PartyRelationshipAndDetail",
						EntityCondition
								.makeCondition(exprs, EntityOperator.AND),
						null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());

			}
			result.put("listCSM", listCsmTemp);
			List<GenericValue> listRsmTemp = new ArrayList<GenericValue>();
			if (!listCsmTemp.isEmpty()) {
				for (GenericValue value : listCsmTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"), "DELYS_RSM");
					if (!listTemp.isEmpty()) {
						listRsmTemp.addAll(listTemp);
					}
				}
			}
			result.put("listRSM", listRsmTemp);
			List<GenericValue> listAsmTemp = new ArrayList<GenericValue>();
			if (!listRsmTemp.isEmpty()) {
				for (GenericValue value : listRsmTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"), "DELYS_ASM");
					if (!listTemp.isEmpty()) {
						listAsmTemp.addAll(listTemp);
					}
				}
			}
			result.put("listASM", listAsmTemp);

			List<GenericValue> listRouteTemp = new ArrayList<GenericValue>();
			if (!listAsmTemp.isEmpty()) {
				for (GenericValue value : listAsmTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"),
							"DELYS_ROUTE");
					if (!listTemp.isEmpty()) {
						listRouteTemp.addAll(listTemp);
					}
				}
			}
			result.put("listROUTE", listRouteTemp);

			List<GenericValue> listCustTemp = new ArrayList<GenericValue>();
			if (!listRouteTemp.isEmpty()) {
				for (GenericValue value : listRouteTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"),
							"DELYS_CUSTOMER");
					if (!listTemp.isEmpty()) {
						listCustTemp.addAll(listTemp);
					}
				}
			}
			result.put("listCUSTOMER", listCustTemp);
			break;
		}
		case DELYS_RSM_GT: {
			List<EntityExpr> exprs = FastList.newInstance();
			exprs.add(EntityCondition.makeCondition("partyIdFrom",
					EntityOperator.EQUALS, partyId));
			exprs.add(EntityCondition.makeCondition("roleTypeIdFrom",
					EntityOperator.EQUALS, "MANAGER"));
			exprs.add(EntityCondition.makeCondition("roleTypeIdTo",
					EntityOperator.EQUALS, "DELYS_RSM"));
			List<GenericValue> listRsmTemp = new ArrayList<GenericValue>();
			try {
				listRsmTemp = delegator.findList("PartyRelationshipAndDetail",
						EntityCondition
								.makeCondition(exprs, EntityOperator.AND),
						null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());

			}
			result.put("listRSM", listRsmTemp);
			List<GenericValue> listAsmTemp = new ArrayList<GenericValue>();
			if (!listRsmTemp.isEmpty()) {
				for (GenericValue value : listRsmTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"), "DELYS_ASM");
					if (!listTemp.isEmpty()) {
						listAsmTemp.addAll(listTemp);
					}
				}
			}
			result.put("listASM", listAsmTemp);

			List<GenericValue> listRouteTemp = new ArrayList<GenericValue>();
			if (!listAsmTemp.isEmpty()) {
				for (GenericValue value : listAsmTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"),
							"DELYS_ROUTE");
					if (!listTemp.isEmpty()) {
						listRouteTemp.addAll(listTemp);
					}
				}
			}
			result.put("listROUTE", listRouteTemp);

			List<GenericValue> listCustTemp = new ArrayList<GenericValue>();
			if (!listRouteTemp.isEmpty()) {
				for (GenericValue value : listRouteTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"),
							"DELYS_CUSTOMER");
					if (!listTemp.isEmpty()) {
						listCustTemp.addAll(listTemp);
					}
				}
			}
			result.put("listCUSTOMER", listCustTemp);
		}
			break;
		case DELYS_ASM_GT: {
			List<EntityExpr> exprs = FastList.newInstance();
			exprs.add(EntityCondition.makeCondition("partyIdFrom",
					EntityOperator.EQUALS, partyId));
			exprs.add(EntityCondition.makeCondition("roleTypeIdFrom",
					EntityOperator.EQUALS, "MANAGER"));
			exprs.add(EntityCondition.makeCondition("roleTypeIdTo",
					EntityOperator.EQUALS, "DELYS_ASM"));
			List<GenericValue> listAsmTemp = new ArrayList<GenericValue>();
			try {
				listAsmTemp = delegator.findList("PartyRelationshipAndDetail",
						EntityCondition
								.makeCondition(exprs, EntityOperator.AND),
						null, null, null, false);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());

			}
			result.put("listASM", listAsmTemp);
			List<GenericValue> listRouteTemp = new ArrayList<GenericValue>();
			if (!listAsmTemp.isEmpty()) {
				for (GenericValue value : listAsmTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"),
							"DELYS_ROUTE");
					if (!listTemp.isEmpty()) {
						listRouteTemp.addAll(listTemp);
					}
				}
			}
			result.put("listROUTE", listRouteTemp);

			List<GenericValue> listCustTemp = new ArrayList<GenericValue>();
			if (!listRouteTemp.isEmpty()) {
				for (GenericValue value : listRouteTemp) {
					List<GenericValue> listTemp = getListEmplByParentId(dcxt,
							context, (String) value.get("partyId"),
							"DELYS_CUSTOMER");
					if (!listTemp.isEmpty()) {
						listCustTemp.addAll(listTemp);
					}
				}
			}
			result.put("listCUSTOMER", listCustTemp);
		}
			break;
		default:
			break;
		}
		result.put("status", status);
		result.put("roleTypeId", roleTypeId);
		result.put("role", role.toString());
		return result;
	}

	/*
	 * 
	 * check roles of an employee, if an employee has more than one role in the
	 * same time, role will be is highest role
	 */
	public static Map<String, Object> checkRoleForDelysEmpl(
			DispatchContext dcxt, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) userLogin.get("partyId");
		String roleTypeId = (String) context.get("roleTypeId");
		String role = (String) context.get("role");
		Delegator delegator = dcxt.getDelegator();
		List<EntityExpr> exprs = FastList.newInstance();
		exprs.add(EntityCondition.makeCondition("partyIdFrom",
				EntityOperator.EQUALS, partyId));
		exprs.add(EntityCondition.makeCondition("roleTypeIdFrom",
				EntityOperator.EQUALS, "MANAGER"));
		List<GenericValue> listAllRel = new ArrayList<GenericValue>();
		try {
			listAllRel = EntityUtil.filterByDate(delegator.findList(
					"PartyRelationship",
					EntityCondition.makeCondition(exprs, EntityOperator.AND),
					null, null, null, false));
			listAllRel = EntityUtil.filterByDate(listAllRel);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());

		}
		Integer checkLv = 0;
		if (!listAllRel.isEmpty()) {
			for (GenericValue item : listAllRel) {
				if (("DELYS_ADMIN").equals((String) item.get("roleTypeIdTo"))
						|| "DELYS_ADMIN".equals(role)) {
					checkLv = 1;
				}
				if (("DELYS_CSM").equals((String) item.get("roleTypeIdTo"))
						|| "DELYS_CSM".equals(role)) {
					if (checkLv >= 2 || checkLv == 0) {
						checkLv = 2;
					}
				}
				if (("DELYS_RSM").equals((String) item.get("roleTypeIdTo"))
						|| "DELYS_RSM".equals(role)) {
					if (checkLv >= 3 || checkLv == 0) {
						checkLv = 3;
					}
				}
				if (("DELYS_ASM").equals((String) item.get("roleTypeIdTo"))
						|| "DELYS_ASM".equals(role)) {
					if (checkLv >= 4 || checkLv == 0) {
						checkLv = 4;
					}
				}
			}
		}
		switch (checkLv) {
		case 1: {
			role = "DELYS_ADMIN";
			break;
		}
		case 2: {
			role = "DELYS_CSM";
			break;
		}
		case 3: {
			role = "DELYS_RSM";
			break;
		}
		case 4: {
			role = "DELYS_ASM";
			break;
		}
		}

		result.put("role", role);
		result.put("userLogin", userLogin);
		result.put("partyId", partyId);
		result.put("roleTypeId", roleTypeId);
		return result;
	}

	/*
	 * 
	 * Find list of stores are managed by partyId had an userLogin is current
	 * userLogin
	 */
	public static Map<String, Object> findListStore(DispatchContext dcxt,
			Map<String, Object> context) {

		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = dcxt.getDelegator();
		List<GenericValue> listAllStoreManaged = new ArrayList<GenericValue>();
		try {
			GenericValue gen = null;
			gen = delegator.findOne("PartyRole",
				UtilMisc.toMap("partyId", userLogin.get("partyId"),"roleTypeId", "DELYS_SALESSUP_GT"), false);
			if (gen != null){
				List<EntityExpr> exprs1 = FastList.newInstance();
				exprs1.add(EntityCondition.makeCondition("partyIdTo",
						EntityOperator.EQUALS, userLogin.get("partyId")));
				exprs1.add(EntityCondition.makeCondition("roleTypeIdFrom",
						EntityOperator.EQUALS, "DELYS_SALESSUP_GT"));
				exprs1.add(EntityCondition.makeCondition("roleTypeIdTo",
						EntityOperator.EQUALS, "MANAGER"));
				List<GenericValue> listAllGroupManaged = null;
				listAllGroupManaged = delegator.findList("PartyRelationship",
						EntityCondition.makeCondition(exprs1, EntityOperator.AND),
						null, null, null, false);
				listAllGroupManaged = EntityUtil.filterByDate(listAllGroupManaged);
				if (!listAllGroupManaged.isEmpty()){
					for (GenericValue rel : listAllGroupManaged){
						List<EntityExpr> exprs2 = FastList.newInstance();
						exprs2.add(EntityCondition.makeCondition("partyIdFrom",
								EntityOperator.EQUALS, rel.get("partyIdFrom")));
						exprs2.add(EntityCondition.makeCondition("roleTypeIdFrom",
								EntityOperator.EQUALS, "DELYS_SALESSUP_GT"));
						exprs2.add(EntityCondition.makeCondition("roleTypeIdTo",
								EntityOperator.EQUALS, "DELYS_DISTRIBUTOR"));
						
						List<GenericValue> listAllRelationship = null;
						listAllRelationship = delegator.findList("PartyRelationship",
								EntityCondition.makeCondition(exprs2, EntityOperator.AND),
								null, null, null, false);
						listAllRelationship = EntityUtil.filterByDate(listAllRelationship);
						
						if (!listAllRelationship.isEmpty()){
							for (GenericValue item : listAllRelationship){
								List<GenericValue> ListStoresTemp = getListStoreByParty(dcxt, item.get("partyIdTo").toString(), context);
								if (!ListStoresTemp.isEmpty()){
									listAllStoreManaged.addAll(ListStoresTemp);
								}
							}
							List<GenericValue> ListStoresTemp = getListStoreByParty(dcxt, userLogin.get("partyId").toString(), context);
							if (!ListStoresTemp.isEmpty()){
								listAllStoreManaged.addAll(ListStoresTemp);
							}
						} else {
						}
					}
				} else {
				}
			} else {
				List<GenericValue> ListStoresTemp = getListStoreByParty(dcxt, userLogin.get("partyId").toString(), context);
				if (!ListStoresTemp.isEmpty()){
					listAllStoreManaged.addAll(ListStoresTemp);
				}
			}
		} catch (GenericEntityException e){
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		if (!listAllStoreManaged.isEmpty()){
			List<GenericValue> listFilter = new ArrayList<GenericValue>();
			for (int i=0; i<listAllStoreManaged.size() - 1; i++) {
				for (int j= i+1; j<listAllStoreManaged.size() ; j++) {
					if (listAllStoreManaged.get(i).get("productStoreId").equals(listAllStoreManaged.get(j).get("productStoreId"))){
						listFilter.add(listAllStoreManaged.get(i));
					}
				}
			}
			if (!listFilter.isEmpty()){
				listAllStoreManaged.removeAll(listFilter);
			}
		}
		result.put("listAllStore", listAllStoreManaged);
		return result;
	}
	
	/* Service method
	 * Find list of stores are managed by userLogin, that has role type equal "MANAGER" with product store.
	 */
	public static Map<String, Object> findListStoreAdvance(DispatchContext dcxt, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = dcxt.getDelegator();
		List<GenericValue> listAllStoreManaged = new ArrayList<GenericValue>();
		try {
			GenericValue partyLoginRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", userLogin.get("partyId"), "roleTypeId", "DELYS_SALESSUP_GT"), false);
			if (partyLoginRole != null){
				// if userLogin is Sales SUP
				List<GenericValue> listAllGroupManaged = delegator.findByAnd("PartyRelationship", 
						UtilMisc.toMap("partyIdTo", userLogin.get("partyId"), "roleTypeIdFrom", "DELYS_SALESSUP_GT", "roleTypeIdTo", "MANAGER"), null, false);
				listAllGroupManaged = EntityUtil.filterByDate(listAllGroupManaged);
				
				if (UtilValidate.isNotEmpty(listAllGroupManaged)){
					for (GenericValue rel : listAllGroupManaged){
						List<GenericValue> listAllRelationship = delegator.findByAnd("PartyRelationship", 
								UtilMisc.toMap("partyIdFrom", rel.get("partyIdFrom"), "roleTypeIdFrom", "DELYS_SALESSUP_GT", "roleTypeIdTo", "DELYS_DISTRIBUTOR"), null, false);
						listAllRelationship = EntityUtil.filterByDate(listAllRelationship);
						
						if (UtilValidate.isNotEmpty(listAllRelationship)){
							for (GenericValue item : listAllRelationship){
								List<GenericValue> ListStoresTemp = getListStoreByParty(dcxt, item.get("partyIdTo").toString(), context);
								if (!ListStoresTemp.isEmpty()){
									listAllStoreManaged.addAll(ListStoresTemp);
								}
							}
							List<GenericValue> listStoresTemp = getListStoreByParty(dcxt, userLogin.get("partyId").toString(), context);
							if (UtilValidate.isNotEmpty(listStoresTemp)){
								listAllStoreManaged.addAll(listStoresTemp);
							}
						}
					}
				}
			} else {
				List<GenericValue> listStoresTemp = getListStoreByParty(dcxt, userLogin.get("partyId").toString(), context);
				if (UtilValidate.isNotEmpty(listStoresTemp)){
					listAllStoreManaged.addAll(listStoresTemp);
				}
			}
		} catch (GenericEntityException e){
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		// remove items duplicate
		if (UtilValidate.isNotEmpty(listAllStoreManaged)){
			List<GenericValue> listFilter = new ArrayList<GenericValue>();
			for (int i = 0; i < (listAllStoreManaged.size() - 1); i++) {
				for (int j = i+1; j < listAllStoreManaged.size(); j++) {
					if (listAllStoreManaged.get(i).get("productStoreId").equals(listAllStoreManaged.get(j).get("productStoreId"))){
						listFilter.add(listAllStoreManaged.get(i));
					}
				}
			}
			if (!listFilter.isEmpty()){
				listAllStoreManaged.removeAll(listFilter);
			}
		}
		result.put("listAllStore", listAllStoreManaged);
		return result;
	}
	
	/* Service method
	 * Find list of stores are managed by roleTypeId and userLogin, that has role type equal "MANAGER" with product store.
	 */
	public static Map<String, Object> getListStoreByRoleTypeAndUserLogin(DispatchContext dcxt, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String roleTypeId = (String) context.get("roleTypeId");
		Delegator delegator = dcxt.getDelegator();
		List<GenericValue> listStore = new ArrayList<GenericValue>();
		try {
			List<EntityExpr> exprs = FastList.newInstance();
			exprs.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, userLogin.getString("partyId")));
			exprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, roleTypeId));
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setDistinct(true);
			listStore = delegator.findList("ProductStoreRoleDetail", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, findOptions, false);
			listStore = EntityUtil.filterByDate(listStore);
		} catch (GenericEntityException e){
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		result.put("listAllStore", listStore);
		return result;
	}
	
	public static Map<String, Object> listDistributor(DispatchContext dcxt,
			Map<String, Object> context) {

		Map<String, Object> result = ServiceUtil.returnSuccess();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Delegator delegator = dcxt.getDelegator();
		List<GenericValue> listAllStoreManaged = new ArrayList<GenericValue>();
		List<GenericValue> listAllDistributor = new ArrayList<GenericValue>();
		try {
			GenericValue gen = null;
			gen = delegator.findOne("PartyRole",
				UtilMisc.toMap("partyId", userLogin.get("partyId"),"roleTypeId", "DELYS_SALESSUP_GT"), false);
			if (gen != null){
				List<EntityExpr> exprs1 = FastList.newInstance();
				exprs1.add(EntityCondition.makeCondition("partyIdTo",
						EntityOperator.EQUALS, userLogin.get("partyId")));
				exprs1.add(EntityCondition.makeCondition("roleTypeIdFrom",
						EntityOperator.EQUALS, "DELYS_SALESSUP_GT"));
				exprs1.add(EntityCondition.makeCondition("roleTypeIdTo",
						EntityOperator.EQUALS, "MANAGER"));
				List<GenericValue> listAllGroupManaged = null;
				listAllGroupManaged = delegator.findList("PartyRelationship",
						EntityCondition.makeCondition(exprs1, EntityOperator.AND),
						null, null, null, false);
				listAllGroupManaged = EntityUtil.filterByDate(listAllGroupManaged);
				if (!listAllGroupManaged.isEmpty()){
					for (GenericValue rel : listAllGroupManaged){
						List<EntityExpr> exprs2 = FastList.newInstance();
						exprs2.add(EntityCondition.makeCondition("partyIdFrom",
								EntityOperator.EQUALS, rel.get("partyIdFrom")));
						exprs2.add(EntityCondition.makeCondition("roleTypeIdFrom",
								EntityOperator.EQUALS, "DELYS_SALESSUP_GT"));
						exprs2.add(EntityCondition.makeCondition("roleTypeIdTo",
								EntityOperator.EQUALS, "DELYS_DISTRIBUTOR"));
						
						List<GenericValue> listAllRelationship = null;
						listAllRelationship = delegator.findList("PartyRelationship",
								EntityCondition.makeCondition(exprs2, EntityOperator.AND),
								null, null, null, false);
						listAllRelationship = EntityUtil.filterByDate(listAllRelationship);
						
						if (!listAllRelationship.isEmpty()){
							for (GenericValue item : listAllRelationship){
								List<GenericValue> ListStoresTemp = getListStoreByParty(dcxt, item.get("partyIdTo").toString(), context);
								if (!ListStoresTemp.isEmpty()){
									listAllStoreManaged.addAll(ListStoresTemp);
									listAllDistributor.addAll(ListStoresTemp);
								}
							}
							List<GenericValue> ListStoresTemp = getListStoreByParty(dcxt, userLogin.get("partyId").toString(), context);
							if (!ListStoresTemp.isEmpty()){
								listAllStoreManaged.addAll(ListStoresTemp);
							}
						} else {
						}
					}
				} else {
				}
			} else {
				List<GenericValue> ListStoresTemp = getListStoreByParty(dcxt, userLogin.get("partyId").toString(), context);
				if (!ListStoresTemp.isEmpty()){
					listAllStoreManaged.addAll(ListStoresTemp);
				}
			}
		} catch (GenericEntityException e){
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		if (!listAllStoreManaged.isEmpty()){
			List<GenericValue> listFilter = new ArrayList<GenericValue>();
			for (int i=0; i<listAllStoreManaged.size() - 1; i++) {
				for (int j= i+1; j<listAllStoreManaged.size() ; j++) {
					if (listAllStoreManaged.get(i).get("productStoreId").equals(listAllStoreManaged.get(j).get("productStoreId"))){
						listFilter.add(listAllStoreManaged.get(i));
					}
				}
			}
			if (!listFilter.isEmpty()){
				listAllStoreManaged.removeAll(listFilter);
			}
		}
		result.put("listAllStore", listAllStoreManaged);
		result.put("listAllDistributor", listAllDistributor);
		return result;
	}
	/*
	 * get list store are managed by partyId
	 * 
	 */
	public static List<GenericValue> getListStoreByParty(DispatchContext dcxt,
			String partyId, Map<String, Object> context) {
		List<GenericValue> listStore = new ArrayList<GenericValue>();
		List<EntityExpr> exprs = FastList.newInstance();
		Delegator delegator = dcxt.getDelegator();
		exprs.add(EntityCondition.makeCondition("partyId",
				EntityOperator.EQUALS, partyId));
		exprs.add(EntityCondition.makeCondition("roleTypeId",
				EntityOperator.EQUALS, "MANAGER"));
		try {
			listStore = delegator.findList("ProductStoreRoleDetail",
					EntityCondition.makeCondition(exprs, EntityOperator.AND),
					null, null, null, false);
			
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
		}
		listStore = EntityUtil.filterByDate(listStore);
		return listStore;
	}
	
	/*
	 * 
	 * get list of Order by productStoreid
	 */
	public static List<GenericValue> getListOrderByStore(
			List<GenericValue> listOrder, Map<String, Object> context) {
		List<GenericValue> listFilter = new ArrayList<GenericValue>();
		for (GenericValue value : listOrder) {
			if (((String) value.get("productStoreId")).equals((String) context
					.get("productStoreId"))) {
			} else {
				listFilter.add(value);
			}
		}
		listOrder.removeAll(listFilter);
		return listOrder;
	}

	/**
	 * Service check sale manager role
	 */
	public static Map<String, Object> filterBySaleManagerRole(
			DispatchContext dctx, Map<String, ?> context) {

		// variables
		Boolean rsmRole = false;
		Boolean asmRole = false;
		Boolean salessupRole = false;
		Boolean routeRole = false;
		List<GenericValue> rsmList = new ArrayList<GenericValue>();
		List<GenericValue> asmList = new ArrayList<GenericValue>();
		List<GenericValue> routeList = new ArrayList<GenericValue>();
		List<GenericValue> salessupList = new ArrayList<GenericValue>();
		Map<String, Object> result = FastMap.newInstance();
		// get login user information
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) userLogin.get("partyId");

		Role role = Role.DELYS_ROUTE;
		List<GenericValue> roleIdFrom = FastList.newInstance();
		Delegator delegator = dctx.getDelegator();

		String helperName = delegator.getGroupHelperName("org.ofbiz");
		Connection conn = null;
		Statement statement = null;

		try {
			conn = ConnectionFactory.getConnection(helperName);
			statement = conn.createStatement();
		} catch (GenericEntityException e1) {

			e1.printStackTrace();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		try {
			String sql = "select distinct ROLE_TYPE_ID_FROM from PARTY_RELATIONSHIP where ROLE_TYPE_ID_TO = 'MANAGER' and PARTY_ID_TO = '"
					+ partyId + "'";
			statement.execute(sql);
			ResultSet results = statement.getResultSet();
			while (results.next()) {
				GenericValue v = GenericValue.create(delegator
						.getModelEntity("PartyRelationship"));
				v.set("roleTypeIdFrom", results.getString(1));
				roleIdFrom.add(v);
			}
		} catch (SQLException e) {

			e.printStackTrace();

		}
		role = Role.valueOf((String) roleIdFrom.get(0).get("roleTypeIdFrom"));

		switch (role) {
		case DELYS_CSM_GT:
			rsmRole = true;
			asmRole = true;
			routeRole = true;
			salessupRole = true;

			try {
				String sql = "select distinct PARTY_ID, GROUP_NAME from PARTY_RELATIONSHIP, PARTY_GROUP "
						+ "where PARTY_RELATIONSHIP.PARTY_ID_TO = PARTY_GROUP.PARTY_ID and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_CSM_GT' "
						+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'DELYS_RSM_GT' and PARTY_RELATIONSHIP.PARTY_ID_FROM IN "
						+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
						+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_CSM_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
						+ partyId + "')";
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", results.getString(1));
					v.set("groupName", results.getString(2));
					rsmList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
			break;
		case DELYS_RSM_GT:
			rsmRole = false;
			asmRole = true;
			routeRole = true;
			salessupRole = true;
			try {
				String sql = "select distinct PARTY_ID, GROUP_NAME from PARTY_RELATIONSHIP, PARTY_GROUP "
						+ "where PARTY_RELATIONSHIP.PARTY_ID_TO = PARTY_GROUP.PARTY_ID and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' "
						+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'DELYS_ASM_GT' and PARTY_RELATIONSHIP.PARTY_ID_FROM IN "
						+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
						+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
						+ partyId + "')";

				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", results.getString(1));
					v.set("groupName", results.getString(2));
					asmList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
			break;

		case DELYS_ASM_GT:
			rsmRole = false;
			asmRole = false;
			routeRole = true;
			salessupRole = true;
			try {
				String sql = "select distinct PARTY_ID, GROUP_NAME from PARTY_RELATIONSHIP, PARTY_GROUP "
						+ "where PARTY_RELATIONSHIP.PARTY_ID_TO = PARTY_GROUP.PARTY_ID and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' "
						+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_RELATIONSHIP.PARTY_ID_FROM IN "
						+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
						+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
						+ partyId + "')";
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", results.getString(1));
					v.set("groupName", results.getString(2));
					routeList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
			break;

		case DELYS_SALESSUP_GT:
			rsmRole = false;
			asmRole = false;
			routeRole = true;
			salessupRole = false;
			try {
				String sql = "select distinct PARTY_ID, GROUP_NAME from PARTY_RELATIONSHIP, PARTY_GROUP "
						+ "where PARTY_RELATIONSHIP.PARTY_ID_TO = PARTY_GROUP.PARTY_ID and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' "
						+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'DELYS_ROUTE' and PARTY_RELATIONSHIP.PARTY_ID_FROM IN "
						+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
						+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
						+ partyId + "')";
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", results.getString(1));
					v.set("groupName", results.getString(2));
					routeList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
			break;
		default:
			rsmRole = false;
			asmRole = false;
			routeRole = false;
			salessupRole = false;
			break;
		}

		result.put("rsmRole", rsmRole);
		result.put("asmRole", asmRole);
		result.put("routeRole", routeRole);
		result.put("salessupRole", salessupRole);
		result.put("rsmList", rsmList);
		result.put("asmList", asmList);
		result.put("routeList", routeList);
		result.put("salessupList", salessupList);

		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Filter customer by sale managers
	 */

	public static Map<String, Object> filterCustomer(DispatchContext dctx,
			Map<String, ?> context) {

		// init variable
		List<GenericValue> customerList = new ArrayList<GenericValue>();
		Map<String, Object> result = FastMap.newInstance();
		Delegator delegator = dctx.getDelegator();
		String helperName = delegator.getGroupHelperName("org.ofbiz");
		Connection conn = null;
		Statement statement = null;

		String asmID = null;
		String rsmID = null;
		String routeID = null;
		String salessupID = null;

		try {
			conn = ConnectionFactory.getConnection(helperName);
			statement = conn.createStatement();
		} catch (GenericEntityException e1) {

			e1.printStackTrace();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}
		// get person information
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) userLogin.get("partyId");
		Role role = Role.DELYS_ROUTE;
		List<GenericValue> roleIdFrom = FastList.newInstance();

		// get parameters
		asmID = (String) context.get("asmID");
		rsmID = (String) context.get("rsmID");
		salessupID = (String) context.get("salessupID");
		routeID = (String) context.get("routeID");

		if (routeID != null) {
			try {
				String sql = "select * from PARTY_GROUP where PARTY_ID IN "
						+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ROUTE' and ROLE_TYPE_ID_TO = 'DELYS_CUSTOMER_GT' and PARTY_ID_FROM = '"
						+ routeID + "')";
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", results.getString(1));
					v.set("groupName", results.getString(2));
					customerList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		} else if (salessupID != null) {
			try {
				String sql = "select * from PARTY_GROUP where PARTY_ID IN "
						+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ROUTE' and ROLE_TYPE_ID_TO = 'DELYS_CUSTOMER_GT' and PARTY_ID_FROM IN "
						+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_ROUTE' and PARTY_ID_FROM = '"
						+ salessupID + "'))";

				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", results.getString(1));
					v.set("groupName", results.getString(2));
					customerList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		} else if (asmID != null) {
			try {
				String sql = "select * from PARTY_GROUP where PARTY_ID IN "
						+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ROUTE' and ROLE_TYPE_ID_TO = 'DELYS_CUSTOMER_GT' and PARTY_ID_FROM IN "
						+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_ROUTE' and PARTY_ID_FROM IN "
						+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM = '"
						+ asmID + "')))";

				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", results.getString(1));
					v.set("groupName", results.getString(2));
					customerList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		} else if (rsmID != null) {
			try {
				String sql = "select * from PARTY_GROUP where PARTY_ID IN "
						+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ROUTE' and ROLE_TYPE_ID_TO = 'DELYS_CUSTOMER_GT' and PARTY_ID_FROM IN "
						+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_ROUTE' and PARTY_ID_FROM IN "
						+ "(select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM IN "
						+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and ROLE_TYPE_ID_TO = 'DELYS_ASM_GT' and PARTY_ID_FROM = '"
						+ rsmID + " '))))";
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", results.getString(1));
					v.set("groupName", results.getString(2));
					customerList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		} else {
			try {
				String sql = "select distinct ROLE_TYPE_ID_FROM from PARTY_RELATIONSHIP where ROLE_TYPE_ID_TO = 'MANAGER' and PARTY_ID_TO = '"
						+ partyId + "'";
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyRelationship"));
					v.set("roleTypeIdFrom", results.getString(1));
					roleIdFrom.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();

			}
			role = Role.valueOf((String) roleIdFrom.get(0)
					.get("roleTypeIdFrom"));

			switch (role) {
			case DELYS_CSM_GT:
				try {
					String sql = "select * from PARTY_GROUP where PARTY_ID IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ROUTE' and ROLE_TYPE_ID_TO = 'DELYS_CUSTOMER_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_ROUTE' and PARTY_ID_FROM IN "
							+ "(select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and ROLE_TYPE_ID_TO = 'DELYS_ASM_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_CSM_GT' and ROLE_TYPE_ID_TO = 'DELYS_RSM_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
							+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_CSM_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
							+ partyId + "'))))))";
					statement.execute(sql);
					ResultSet results = statement.getResultSet();
					while (results.next()) {
						GenericValue v = GenericValue.create(delegator
								.getModelEntity("PartyGroup"));
						v.set("partyId", results.getString(1));
						v.set("groupName", results.getString(2));
						customerList.add(v);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case DELYS_RSM_GT:
				try {
					String sql = "select * from PARTY_GROUP where PARTY_ID IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ROUTE' and ROLE_TYPE_ID_TO = 'DELYS_CUSTOMER_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_ROUTE' and PARTY_ID_FROM IN "
							+ "(select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and ROLE_TYPE_ID_TO = 'DELYS_ASM_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
							+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
							+ partyId + "')))))";
					statement.execute(sql);
					ResultSet results = statement.getResultSet();
					while (results.next()) {
						GenericValue v = GenericValue.create(delegator
								.getModelEntity("PartyGroup"));
						v.set("partyId", results.getString(1));
						v.set("groupName", results.getString(2));
						customerList.add(v);
					}
				} catch (SQLException e) {
		
					e.printStackTrace();
				}
				break;
			case DELYS_ASM_GT:
				try {
					String sql = "select * from PARTY_GROUP where PARTY_ID IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ROUTE' and ROLE_TYPE_ID_TO = 'DELYS_CUSTOMER_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_ROUTE' and PARTY_ID_FROM IN "
							+ "(select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
							+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
							+ partyId + "'))))";
					statement.execute(sql);
					ResultSet results = statement.getResultSet();
					while (results.next()) {
						GenericValue v = GenericValue.create(delegator
								.getModelEntity("PartyGroup"));
						v.set("partyId", results.getString(1));
						v.set("groupName", results.getString(2));
						customerList.add(v);
					}
				} catch (SQLException e) {
		
					e.printStackTrace();
				}
				break;
			case DELYS_SALESSUP_GT:
				try {
					String sql = "select * from PARTY_GROUP where PARTY_ID IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ROUTE' and ROLE_TYPE_ID_TO = 'DELYS_CUSTOMER_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_ROUTE' and PARTY_ID_FROM IN "
							+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
							+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
							+ partyId + "')))";
					statement.execute(sql);
					ResultSet results = statement.getResultSet();
					while (results.next()) {
						GenericValue v = GenericValue.create(delegator
								.getModelEntity("PartyGroup"));
						v.set("partyId", results.getString(1));
						v.set("groupName", results.getString(2));
						customerList.add(v);
					}
				} catch (SQLException e) {
		
					e.printStackTrace();
				}
				break;
			default:
				break;

			}
		}
		result.put("customerList", customerList);
		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return result;
	}

	/**
	 * get ASMs
	 */

	public static Map<String, Object> getASMs(DispatchContext dcxt,
			Map<String, ?> context) {

		List<GenericValue> asmList = FastList.newInstance();
		Map<String, Object> result = FastMap.newInstance();
		String rsmID = (String) context.get("rsmID");
		Delegator delegator = dcxt.getDelegator();
		String helperName = delegator.getGroupHelperName("org.ofbiz");
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ConnectionFactory.getConnection(helperName);
			statement = conn.createStatement();
		} catch (GenericEntityException e1) {

			e1.printStackTrace();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}
		if (rsmID != null && !rsmID.isEmpty()) {
			try {
				String sql = "select distinct PARTY_ID, GROUP_NAME from PARTY_RELATIONSHIP, PARTY_GROUP "
						+ "where PARTY_RELATIONSHIP.PARTY_ID_TO = PARTY_GROUP.PARTY_ID and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' "
						+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'DELYS_ASM_GT' and PARTY_RELATIONSHIP.PARTY_ID_FROM = '"
						+ rsmID + "'";
				statement.execute(sql);
				ResultSet rs = statement.getResultSet();
				while (rs.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", rs.getString(1));
					v.set("groupName", rs.getString(2));
					asmList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		}
		result.put("asmList", asmList);
		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return result;
	}

	/**
	 * get Salessups
	 * 
	 */
	public static Map<String, Object> getSalessups(DispatchContext dcxt,
			Map<String, ?> context) {

		Delegator delegator = dcxt.getDelegator();
		String helperName = delegator.getGroupHelperName("org.ofbiz");
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ConnectionFactory.getConnection(helperName);
			statement = conn.createStatement();
		} catch (GenericEntityException e1) {

			e1.printStackTrace();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		List<GenericValue> salessupList = FastList.newInstance();
		Map<String, Object> result = FastMap.newInstance();
		String asmID = (String) context.get("asmID");

		if (asmID != null && !asmID.isEmpty()) {
			try {
				String sql = "select distinct PARTY_ID, GROUP_NAME from PARTY_RELATIONSHIP, PARTY_GROUP "
						+ "where PARTY_RELATIONSHIP.PARTY_ID_TO = PARTY_GROUP.PARTY_ID and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' "
						+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_RELATIONSHIP.PARTY_ID_FROM = ' "
						+ asmID + "'";
				statement.execute(sql);
				ResultSet rs = statement.getResultSet();

				while (rs.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", rs.getString(1));
					v.set("groupName", rs.getString(2));
					salessupList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		result.put("salessupList", salessupList);
		return result;
	}
	
	public static Map<String, Object> getRoutes(DispatchContext dcxt,
			Map<String, ?> context) {

		Delegator delegator = dcxt.getDelegator();
		String helperName = delegator.getGroupHelperName("org.ofbiz");
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ConnectionFactory.getConnection(helperName);
			statement = conn.createStatement();
		} catch (GenericEntityException e1) {

			e1.printStackTrace();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		List<GenericValue> routeList = FastList.newInstance();
		Map<String, Object> result = FastMap.newInstance();
		String salessupID = (String) context.get("salessupID");

		if (salessupID != null && !salessupID.isEmpty()) {
			try {
				String sql = "select distinct PARTY_ID, GROUP_NAME from PARTY_RELATIONSHIP, PARTY_GROUP "
						+ "where PARTY_RELATIONSHIP.PARTY_ID_TO = PARTY_GROUP.PARTY_ID and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' "
						+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'DELYS_ROUTE' and PARTY_RELATIONSHIP.PARTY_ID_FROM = '"
						+ salessupID + "'";
				statement.execute(sql);
				ResultSet rs = statement.getResultSet();

				while (rs.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", rs.getString(1));
					v.set("groupName", rs.getString(2));
					routeList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		}

		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		result.put("routeList", routeList);
		return result;
	}
	
	public static Map<String, Object> filterSalesman(DispatchContext dctx,
			Map<String, ?> context) {
		Delegator delegator = dctx.getDelegator();
		String helperName = delegator.getGroupHelperName("org.ofbiz");
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ConnectionFactory.getConnection(helperName);
			statement = conn.createStatement();
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		Map<String, Object> result = new HashMap<String, Object>();
		List<GenericValue> salesmanList = new ArrayList<GenericValue>();
		
		Role role = Role.DELYS_ROUTE;
		List<GenericValue> roleIdFrom = FastList.newInstance();
		
		// Get input
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) userLogin.get("partyId");
		String sql = "";
		String asmID = (String) context.get("asmID");
		String rsmID = (String) context.get("rsmID");
		String salessupID = (String) context.get("salessupID");
		// Statement
		if (salessupID != null) {
			sql = "select * from PERSON where PARTY_ID IN "
					+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESMAN_GT' and PARTY_ID_FROM = '" 
					+ salessupID + "')";
			try {
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("Person"));
					v.set("partyId", results.getString(1));
					v.set("firstName", results.getString(3));
					v.set("middleName", results.getString(4));
					v.set("lastName", results.getString(5));
					salesmanList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		} else if (asmID != null) {
			sql = "select * from PERSON where PARTY_ID IN "
					+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESMAN_GT' and PARTY_ID_FROM IN " 
					+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM = '"
					+ asmID + "'))";
			try {
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("Person"));
					v.set("partyId", results.getString(1));
					v.set("firstName", results.getString(3));
					v.set("middleName", results.getString(4));
					v.set("lastName", results.getString(5));
					salesmanList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		} else if (rsmID != null) {
			sql = "select * from PERSON where PARTY_ID IN "
					+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESMAN_GT' and PARTY_ID_FROM IN " 
					+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM IN "
					+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and ROLE_TYPE_ID_TO = 'DELYS_ASM_GT' and PARTY_ID_FROM = '"
					+ rsmID + "')))";
			try {
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("Person"));
					v.set("partyId", results.getString(1));
					v.set("firstName", results.getString(3));
					v.set("middleName", results.getString(4));
					v.set("lastName", results.getString(5));
					salesmanList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		} else {
			try {
				sql = "select distinct ROLE_TYPE_ID_FROM from PARTY_RELATIONSHIP where ROLE_TYPE_ID_TO = 'MANAGER' and PARTY_ID_TO = '"
						+ partyId + "'";
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyRelationship"));
					v.set("roleTypeIdFrom", results.getString(1));
					roleIdFrom.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();

			}
			role = Role.valueOf((String) roleIdFrom.get(0)
					.get("roleTypeIdFrom"));

			switch (role) {
			case DELYS_CSM_GT:
				try {
					sql = "select * from PERSON where PARTY_ID IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESMAN_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and ROLE_TYPE_ID_TO = 'DELYS_ASM_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_CSM_GT' and ROLE_TYPE_ID_TO = 'DELYS_RSM_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
							+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_CSM_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
							+ partyId + "')))))";
					statement.execute(sql);
					ResultSet results = statement.getResultSet();
					while (results.next()) {
						GenericValue v = GenericValue.create(delegator
								.getModelEntity("Person"));
						v.set("partyId", results.getString(1));
						v.set("firstName", results.getString(3));
						v.set("middleName", results.getString(4));
						v.set("lastName", results.getString(5));
						salesmanList.add(v);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case DELYS_RSM_GT:
				try {
					sql = "select * from PERSON where PARTY_ID IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESMAN_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and ROLE_TYPE_ID_TO = 'DELYS_ASM_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
							+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
							+ partyId + "'))))";
					statement.execute(sql);
					ResultSet results = statement.getResultSet();
					while (results.next()) {
						GenericValue v = GenericValue.create(delegator
								.getModelEntity("Person"));
						v.set("partyId", results.getString(1));
						v.set("firstName", results.getString(3));
						v.set("middleName", results.getString(4));
						v.set("lastName", results.getString(5));
						salesmanList.add(v);
					}
				} catch (SQLException e) {
		
					e.printStackTrace();
				}
				break;
			case DELYS_ASM_GT:
				try {
					sql = "select * from PERSON where PARTY_ID IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESMAN_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
							+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
							+ partyId + "')))";
					statement.execute(sql);
					ResultSet results = statement.getResultSet();
					while (results.next()) {
						GenericValue v = GenericValue.create(delegator
								.getModelEntity("Person"));
						v.set("partyId", results.getString(1));
						v.set("firstName", results.getString(3));
						v.set("middleName", results.getString(4));
						v.set("lastName", results.getString(5));
						salesmanList.add(v);
					}
				} catch (SQLException e) {
		
					e.printStackTrace();
				}
				break;
			case DELYS_SALESSUP_GT:
				try {
					sql = "select * from PERSON where PARTY_ID IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESMAN_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
							+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
							+ partyId + "'))";
					statement.execute(sql);
					ResultSet results = statement.getResultSet();
					while (results.next()) {
						GenericValue v = GenericValue.create(delegator
								.getModelEntity("Person"));
						v.set("partyId", results.getString(1));
						v.set("firstName", results.getString(3));
						v.set("middleName", results.getString(4));
						v.set("lastName", results.getString(5));
						salesmanList.add(v);
					}
				} catch (SQLException e) {
		
					e.printStackTrace();
				}
				break;
			default:
				break;

			}
		}

		// Close connection
		try {
			conn.close();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		result.put("salesmanList", salesmanList);
		return result;
	}
	
	public static Map<String, Object> filterDistributor(DispatchContext dctx,
			Map<String, ?> context) {
		Delegator delegator = dctx.getDelegator();
		String helperName = delegator.getGroupHelperName("org.ofbiz");
		Connection conn = null;
		Statement statement = null;
		try {
			conn = ConnectionFactory.getConnection(helperName);
			statement = conn.createStatement();
		} catch (GenericEntityException e1) {

			e1.printStackTrace();
		} catch (SQLException e1) {

			e1.printStackTrace();
		}

		Map<String, Object> result = new HashMap<String, Object>();
		List<GenericValue> distributorList = new ArrayList<GenericValue>();
		
		Role role = Role.DELYS_ROUTE;
		List<GenericValue> roleIdFrom = FastList.newInstance();
		
		// Get input
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String) userLogin.get("partyId");
		String sql = "";
		String asmID = (String) context.get("asmID");
		String rsmID = (String) context.get("rsmID");
		String salessupID = (String) context.get("salessupID");
		// Statement
		if (salessupID != null) {
			sql = "select * from PARTY_GROUP where PARTY_ID IN "
					+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_DISTRIBUTOR' and PARTY_ID_FROM = '" 
					+ salessupID + "')";
			try {
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", results.getString(1));
					v.set("groupName", results.getString(2));
					distributorList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		} else if (asmID != null) {
			sql = "select * from PARTY_GROUP where PARTY_ID IN "
					+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_DISTRIBUTOR' and PARTY_ID_FROM IN " 
					+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM = '"
					+ asmID + "'))";
			try {
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", results.getString(1));
					v.set("groupName", results.getString(2));
					distributorList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		} else if (rsmID != null) {
			sql = "select * from PARTY_GROUP where PARTY_ID IN "
					+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_DISTRIBUTOR' and PARTY_ID_FROM IN " 
					+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM IN "
					+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and ROLE_TYPE_ID_TO = 'DELYS_ASM_GT' and PARTY_ID_FROM = '"
					+ rsmID + "')))";
			try {
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyGroup"));
					v.set("partyId", results.getString(1));
					v.set("groupName", results.getString(2));
					distributorList.add(v);
				}
			} catch (SQLException e) {
	
				e.printStackTrace();
			}
		} else {
			try {
				sql = "select distinct ROLE_TYPE_ID_FROM from PARTY_RELATIONSHIP where ROLE_TYPE_ID_TO = 'MANAGER' and PARTY_ID_TO = '"
						+ partyId + "'";
				statement.execute(sql);
				ResultSet results = statement.getResultSet();
				while (results.next()) {
					GenericValue v = GenericValue.create(delegator
							.getModelEntity("PartyRelationship"));
					v.set("roleTypeIdFrom", results.getString(1));
					roleIdFrom.add(v);
				}
			} catch (SQLException e) {
				e.printStackTrace();

			}
			role = Role.valueOf((String) roleIdFrom.get(0)
					.get("roleTypeIdFrom"));

			switch (role) {
			case DELYS_CSM_GT:
				try {
					sql = "select * from PARTY_GROUP where PARTY_ID IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_DISTRIBUTOR' and PARTY_ID_FROM IN "
							+ "(select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and ROLE_TYPE_ID_TO = 'DELYS_ASM_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_CSM_GT' and ROLE_TYPE_ID_TO = 'DELYS_RSM_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
							+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_CSM_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
							+ partyId + "')))))";
					statement.execute(sql);
					ResultSet results = statement.getResultSet();
					while (results.next()) {
						GenericValue v = GenericValue.create(delegator
								.getModelEntity("PartyGroup"));
						v.set("partyId", results.getString(1));
						v.set("groupName", results.getString(2));
						distributorList.add(v);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case DELYS_RSM_GT:
				try {
					sql = "select * from PARTY_GROUP where PARTY_ID IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_DISTRIBUTOR' and PARTY_ID_FROM IN "
							+ "(select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and ROLE_TYPE_ID_TO = 'DELYS_ASM_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
							+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_RSM_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
							+ partyId + "'))))";
					statement.execute(sql);
					ResultSet results = statement.getResultSet();
					while (results.next()) {
						GenericValue v = GenericValue.create(delegator
								.getModelEntity("PartyGroup"));
						v.set("partyId", results.getString(1));
						v.set("groupName", results.getString(2));
						distributorList.add(v);
					}
				} catch (SQLException e) {
		
					e.printStackTrace();
				}
				break;
			case DELYS_ASM_GT:
				try {
					sql = "select * from PARTY_GROUP where PARTY_ID IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_DISTRIBUTOR' and PARTY_ID_FROM IN "
							+ "(select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and ROLE_TYPE_ID_TO = 'DELYS_SALESSUP_GT' and PARTY_ID_FROM IN "
							+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
							+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_ASM_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
							+ partyId + "')))";
					statement.execute(sql);
					ResultSet results = statement.getResultSet();
					while (results.next()) {
						GenericValue v = GenericValue.create(delegator
								.getModelEntity("PartyGroup"));
						v.set("partyId", results.getString(1));
						v.set("groupName", results.getString(2));
						distributorList.add(v);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			case DELYS_SALESSUP_GT:
				try {
					sql = "select * from PARTY_GROUP where PARTY_ID IN "
							+ "(Select PARTY_ID_TO from PARTY_RELATIONSHIP where ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and ROLE_TYPE_ID_TO = 'DELYS_DISTRIBUTOR' and PARTY_ID_FROM IN "
							+ "(select PARTY_RELATIONSHIP.PARTY_ID_FROM from PARTY_RELATIONSHIP where PARTY_RELATIONSHIP.ROLE_TYPE_ID_TO = 'MANAGER' "
							+ "and PARTY_RELATIONSHIP.ROLE_TYPE_ID_FROM = 'DELYS_SALESSUP_GT' and PARTY_RELATIONSHIP.PARTY_ID_TO = '"
							+ partyId + "'))";
					statement.execute(sql);
					ResultSet results = statement.getResultSet();
					while (results.next()) {
						GenericValue v = GenericValue.create(delegator
								.getModelEntity("PartyGroup"));
						v.set("partyId", results.getString(1));
						v.set("groupName", results.getString(2));
						distributorList.add(v);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				break;
			default:
				break;

			}
		}

		// Close connection
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		result.put("distributorList", distributorList);
		return result;
	}
	
	public static Map<String,Object> UpdateProductConfigPacking(DispatchContext ctx,
		Map<String, Object> context) throws GenericEntityException {
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	String productId = (String)context.get("productId");
    	String uomFromId = (String)context.get("uomFromId");
    	String uomToId = (String)context.get("uomToId");
    	String description = (String)context.get("description");
    	String quantityConvertTmp = (String)context.get("quantityConvert");
    	Locale locale = (Locale)context.get("locale");
    	NumberFormat nf = NumberFormat.getInstance(locale);
    	BigDecimal quantityConvert = BigDecimal.ZERO;
        try {
        	quantityConvert = new BigDecimal(nf.parse(quantityConvertTmp).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        GenericValue configPackingTmp = delegator.findOne("ConfigPacking", false, UtilMisc.toMap("productId", productId, "uomFromId", uomFromId, "uomToId", uomToId));
        if (configPackingTmp != null){
        	BigDecimal oldQuantityConvert = configPackingTmp.getBigDecimal("quantityConvert");
        	List<String> listUomPrs = new ArrayList<String>();
        	if (oldQuantityConvert.compareTo(quantityConvert) != 0){
        		listUomPrs = getAllParentUoms(delegator, productId, uomFromId, listUomPrs);
        		if (!listUomPrs.isEmpty()){
        			for (String uomPr : listUomPrs){
        				GenericValue cfTmp = delegator.findOne("ConfigPacking", false, UtilMisc.toMap("productId", productId, "uomFromId", uomPr, "uomToId", uomToId));
        				if (cfTmp != null){
        					BigDecimal prConvert = BigDecimal.ONE;
        					prConvert = getConvertNumber(delegator, prConvert, productId, uomPr, uomFromId);
        					BigDecimal newConvert = prConvert.multiply(quantityConvert);
        					cfTmp.put("quantityConvert", newConvert);
        					delegator.store(cfTmp);
        				} else {
        					List<String> listUomToConvertFrom = getListUomToConvert(delegator, productId, uomPr, uomToId);
        					listUomToConvertFrom.remove(uomPr);
        					listUomToConvertFrom.remove(uomToId);
        					List<String> listUomToConvertTo = getListUomToConvert(delegator, productId, uomPr, uomFromId);
        					listUomToConvertTo.remove(uomPr);
        					listUomToConvertTo.remove(uomFromId);
        					List<String> listUomTmp = new ArrayList<String>();
        					for (String uomId : listUomToConvertFrom){
        						if (listUomToConvertTo.contains(uomId)){
        							listUomTmp.add(uomId);
        						}
        					}
        					listUomToConvertFrom.removeAll(listUomTmp);
        					listUomToConvertTo.removeAll(listUomTmp);
        					
        					if (!listUomToConvertFrom.isEmpty() || !listUomToConvertTo.isEmpty()){
	        					Map<String, Object> returnErr = ServiceUtil.returnError(UtilProperties.getMessage(resource,
	            	                    "CannotChangeConvert", locale));
	            				return returnErr;
        					}
        				}
        			}
        		}
        	}
        }
    	GenericValue configPacking = delegator.makeValue("ConfigPacking");
    	if (context.get("fromDate") != null){
    		java.sql.Timestamp fromDate = (Timestamp)(context.get("fromDate"));
    		configPacking.put("fromDate", fromDate);
    	}
    	if (context.get("thruDate") != null){
    		java.sql.Timestamp thruDate = (Timestamp)(context.get("thruDate"));
    		configPacking.put("thruDate", thruDate);
    	}
    	if (quantityConvert.equals(BigDecimal.ZERO)){
    		return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "CannotEqualsZero", locale));
    	}
    	configPacking.put("quantityConvert", quantityConvert);
    	configPacking.put("productId", productId);
    	if (uomToId == null){
    		GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
    		if (product != null){
    			uomToId = (String)product.get("quantityUomId");
    			if (uomToId == null){
    				Map<String, Object> returnErr = ServiceUtil.returnError(UtilProperties.getMessage(resource,
    	                    "NoQuantityUom", locale));
    				return returnErr;
    			}
    		}
    	}
    	configPacking.put("uomToId", uomToId);
    	configPacking.put("uomFromId", uomFromId);
    	configPacking.put("description", description);
    	
    	try {
			delegator.createOrStore(configPacking);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return result;
	}
	
	public static List<String> getAllParentUoms(Delegator delegator, String productId, String uomToId, List<String> listAllParentUoms){
		try {
			List<GenericValue> listConfigs = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomToId", uomToId)), null, null, null, false);
			if (!listConfigs.isEmpty()){
				for (GenericValue cf : listConfigs){
					if (!listAllParentUoms.contains((String)cf.get("uomFromId"))){
						listAllParentUoms.add((String)cf.get("uomFromId"));
					}
					getAllParentUoms(delegator, productId, (String)cf.get("uomFromId"), listAllParentUoms);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return listAllParentUoms;
	}
	
	public static Map<String,Object> updateDelysProduct(DispatchContext ctx,
			Map<String, Object> context) throws GenericEntityException {
	    	Map<String, Object> result = new FastMap<String, Object>();
	    	Delegator delegator = ctx.getDelegator();
	    	String facilityId = (String)context.get("facilityId");
	    	String supplierId = (String)context.get("supplierId");
	    	if (supplierId != null){
	    		if (supplierId.contains(",")){
	    			supplierId = supplierId.replace(",", "");
	    		}
	    	}
	    	String productName = (String)context.get("productName");
	    	String brandName = (String)context.get("brandName");
	    	String productId = (String)context.get("productId");
	    	String productTypeId = (String)context.get("productTypeId");
	    	String description = (String)context.get("description");
	    	String quantityUomId = (String)context.get("quantityUomId");
	    	String productWeight = (String)context.get("productWeight");
	    	String weightUomId = (String)context.get("weightUomId");
	    	GenericValue product = delegator.makeValue("Product");
	    	product.put("productId", productId);
	    	product.put("facilityId", facilityId);
	    	product.put("productName", productName);
	    	product.put("internalName", productName);
	    	product.put("brandName", brandName);
	    	product.put("productTypeId", productTypeId);
	    	product.put("description", description);
	    	product.put("quantityUomId", quantityUomId);
	    	product.put("productWeight",new BigDecimal(productWeight));
	    	product.put("weightUomId", weightUomId);
	    	try {
				delegator.store(product);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return result;
	}
	public static Map<String,Object> updateUomPacking(DispatchContext ctx,
			Map<String, Object> context) throws GenericEntityException {
	    	Map<String, Object> result = new FastMap<String, Object>();
	    	Delegator delegator = ctx.getDelegator();
	    	String uomTypeId = (String)context.get("uomTypeId");
	    	String description = (String)context.get("description");
	    	String abbreviation = (String)context.get("abbreviation");
	    	String uomId = delegator.getNextSeqId("ShippingContract");
	    	
	    	GenericValue uom = delegator.makeValue("Uom");
	    	uom.put("uomTypeId", uomTypeId);
	    	uom.put("description", description);
	    	uom.put("abbreviation", abbreviation);
	    	uom.put("uomId", uomId);
	    	
	    	delegator.createOrStore(uom);
	    	return result;
	}
	    	
	public static Map<String,Object> UpdateShippingContract(DispatchContext ctx,
			Map<String, Object> context) throws GenericEntityException {
	    	Map<String, Object> result = new FastMap<String, Object>();
	    	Delegator delegator = ctx.getDelegator();
	    	LocalDispatcher dispatcher = ctx.getDispatcher();
	    	GenericValue ShippingContract = delegator.makeValue("ShippingContract");
	    	String contractId = (String)context.get("contractId");
	    	if (contractId == null){
	    		contractId = delegator.getNextSeqId("ShippingContract");
	    	}
	    	String contractName = (String)context.get("contractName");
	    	
	    	String newFromParty = (String)context.get("newFromParty");
	    	String newToParty = (String)context.get("newToParty");
	    	String newFromPartyGroup = (String)context.get("newFromPartyGroup");
	    	String newToPartyGroup = (String)context.get("newToPartyGroup");
	    	
	    	String fromPartyId = (String)context.get("fromPartyId");
	    	String fromPartyGroupId = (String)context.get("fromPartyGroupId");
	    	String fromContactMechPhone = (String)context.get("fromContactMechPhoneId");
	    	String fromContactMechFax = (String)context.get("fromContactMechFaxId");
	    	try {
	    		String fromContactMechPhoneId = setContactInfo(ctx, context, fromPartyId, "PHONE_WORK", fromContactMechPhone);
	    		String fromContactMechFaxId = setContactInfo(ctx, context, fromPartyId, "FAX_NUMBER", fromContactMechFax);
	    		ShippingContract.put("fromContactMechPhoneId", fromContactMechPhoneId);
	    		ShippingContract.put("fromContactMechFaxId", fromContactMechFaxId);
			} catch (GeneralException e1) {
				e1.printStackTrace();
			}
	    	String fromGeoId = (String)context.get("fromGeoId");
	    	String fromTaxCodeId = (String)context.get("fromTaxCodeId");
	    	String fromFinAccountId = (String)context.get("fromFinAccountId");
	    	String fromFinAccountTypeId = (String)context.get("fromFinAccountTypeId");
	    	
	    	GenericValue taxAuthorityFrom = delegator.makeValue("TaxAuthority");
	    	taxAuthorityFrom.put("taxAuthGeoId", fromGeoId);
	    	taxAuthorityFrom.put("taxAuthPartyId", fromPartyGroupId);
	    	taxAuthorityFrom.put("includeTaxInPrice", "Y");
	    	delegator.createOrStore(taxAuthorityFrom);
	    	
	    	String toPartyId = (String)context.get("toPartyId");
	    	String toPartyGroupId = (String)context.get("toPartyGroupId");
	    	String toContactMechPhone = (String)context.get("toContactMechPhoneId");
	    	String toContactMechFax = (String)context.get("toContactMechFaxId");
	    	try {
				String toContactMechPhoneId = setContactInfo(ctx, context, toPartyId, "PHONE_WORK", toContactMechPhone);
				String toContactMechFaxId = setContactInfo(ctx, context, toPartyId, "FAX_NUMBER", toContactMechFax);
		    	ShippingContract.put("toContactMechPhoneId", toContactMechPhoneId);
		    	ShippingContract.put("toContactMechFaxId", toContactMechFaxId);
			} catch (GeneralException e1) {
				e1.printStackTrace();
			}
	    	String toGeoId = (String)context.get("toGeoId");
	    	String toTaxCodeId = (String)context.get("toTaxCodeId");
	    	String toFinAccountId = (String)context.get("toFinAccountId");
	    	String toFinAccountTypeId = (String)context.get("toFinAccountTypeId");
	    	
	    	GenericValue taxAuthorityTo = delegator.makeValue("TaxAuthority");
	    	taxAuthorityTo.put("taxAuthGeoId", toGeoId);
	    	taxAuthorityTo.put("taxAuthPartyId", toPartyGroupId);
	    	taxAuthorityTo.put("includeTaxInPrice", "Y");
	    	delegator.createOrStore(taxAuthorityTo);
	    	
	    	String statusId = (String)context.get("statusId");
	    	String description = (String)context.get("description");
	    	
	    	if (context.get("fromDate") != null){
	    		Timestamp fromDate = (Timestamp)(context.get("fromDate"));
	    		ShippingContract.put("fromDate", fromDate);
	    	}
	    	if (context.get("thruDate") != null){
	    		Timestamp thruDate = (Timestamp)(context.get("thruDate"));
	    		ShippingContract.put("thruDate", thruDate);
	    	}
	    	if (context.get("createdDate") != null){
	    		Timestamp createdDate = (Timestamp)(context.get("createdDate"));
	    		ShippingContract.put("createdDate", createdDate);
	    	} else {
	    		Timestamp createdDate = UtilDateTime.nowTimestamp();
	    		ShippingContract.put("createdDate", createdDate);
	    	}
	    	
	    	if (toPartyId == null){
	    		if (newToParty != null){
	    			try {
	    				Map<String, Object> person = new HashMap<String, Object>();
	    				person.put("lastName", newToParty);
	    				Map<String, Object> resultTmp = dispatcher.runSync("createPerson", person);
	    				toPartyId = (String)resultTmp.get("partyId");
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
	    		} else {
	    			return ServiceUtil.returnError("error");
	    		}
	    	}
	    	
	    	if (fromPartyId == null){
	    		if (newFromParty != null){
	    			try {
	    				Map<String, Object> person = new HashMap<String, Object>();
	    				person.put("lastName", newFromParty);
	    				Map<String, Object> resultTmp = dispatcher.runSync("createPerson", person);
	    				fromPartyId = (String)resultTmp.get("partyId");
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
	    		} else {
	    			return ServiceUtil.returnError("error");
	    		}
	    	}
	    	
	    	if (fromPartyGroupId == null){
	    		if (newFromPartyGroup != null){
	    			try {
	    				Map<String, Object> partyGroup = new HashMap<String, Object>();
	    				partyGroup.put("groupName", newFromPartyGroup);
	    				Map<String, Object> resultTmp = dispatcher.runSync("createPartyGroup", partyGroup);
	    				fromPartyGroupId = (String)resultTmp.get("partyId");;
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
	    		} else {
	    			return ServiceUtil.returnError("error");
	    		}
	    	}
	    	
	    	if (toPartyGroupId == null){
	    		if (newToPartyGroup != null){
	    			try {
	    				Map<String, Object> partyGroup = new HashMap<String, Object>();
	    				partyGroup.put("groupName", newToPartyGroup);
	    				Map<String, Object> resultTmp = dispatcher.runSync("createPartyGroup", partyGroup);
	    				toPartyGroupId = (String)resultTmp.get("partyId");;
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
	    		} else {
	    			return ServiceUtil.returnError("error");
	    		}
	    	}
	    	
	    	GenericValue partyRoleTo = delegator.makeValue("PartyRole");
	    	partyRoleTo.put("partyId", toPartyGroupId);
	    	partyRoleTo.put("roleTypeId", "CARRIER");
	    	delegator.createOrStore(partyRoleTo);
	    	
	    	GenericValue TaxAuthorityFrom = delegator.makeValue("TaxAuthority");
	    	TaxAuthorityFrom.put("taxAuthGeoId", fromGeoId);
	    	TaxAuthorityFrom.put("taxAuthPartyId", fromPartyId);
	    	delegator.createOrStore(TaxAuthorityFrom);
	    	
	    	GenericValue TaxAuthorityTo = delegator.makeValue("TaxAuthority");
	    	TaxAuthorityTo.put("taxAuthGeoId", toGeoId);
	    	TaxAuthorityTo.put("taxAuthPartyId", toPartyId);
	    	delegator.createOrStore(TaxAuthorityTo);
	    	
	    	GenericValue FinAccountFrom = delegator.makeValue("FinAccount");
	    	String finAccountIdFrom = delegator.getNextSeqId("FinAccount");
	    	FinAccountFrom.put("finAccountId", finAccountIdFrom);
	    	FinAccountFrom.put("finAccountCode", fromFinAccountId);
	    	FinAccountFrom.put("finAccountTypeId", fromFinAccountTypeId);
	    	FinAccountFrom.put("organizationPartyId", fromPartyGroupId);
	    	delegator.createOrStore(FinAccountFrom);
	    	
	    	GenericValue FinAccountTo = delegator.makeValue("FinAccount");
	    	String finAccountIdTo = delegator.getNextSeqId("FinAccount");
	    	FinAccountTo.put("finAccountId", finAccountIdTo);
	    	FinAccountTo.put("finAccountCode", toFinAccountId);
	    	FinAccountTo.put("finAccountTypeId", toFinAccountTypeId);
	    	FinAccountTo.put("organizationPartyId", toPartyGroupId);
	    	delegator.createOrStore(FinAccountTo);
	    	
	    	ShippingContract.put("contractId", contractId);
	    	ShippingContract.put("contractName", contractName);
	    	ShippingContract.put("statusId", statusId);
	    	ShippingContract.put("description", description);
	    	ShippingContract.put("toPartyId", toPartyId);
	    	ShippingContract.put("fromPartyId", fromPartyId);
	    	ShippingContract.put("fromPartyGroupId", fromPartyGroupId);
	    	ShippingContract.put("toPartyGroupId", toPartyGroupId);
	    	ShippingContract.put("fromGeoId", fromGeoId);
	    	ShippingContract.put("toGeoId", toGeoId);
	    	ShippingContract.put("fromTaxCodeId", fromTaxCodeId);
	    	ShippingContract.put("toTaxCodeId", toTaxCodeId);
	    	ShippingContract.put("fromFinAccountId", finAccountIdFrom);
	    	ShippingContract.put("toFinAccountId", finAccountIdTo);
	    	
	    	try {
				delegator.createOrStore(ShippingContract);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	result.put("contractId", contractId);
	    	return result;
	}
	
	public static String setContactInfo(DispatchContext ctx, Map<String, Object> context, String partyId, String contactMechPurposeTypeId, String infoString) throws GeneralException {
        Delegator delegator = ctx.getDelegator();
        GenericValue contactMech = delegator.makeValue("ContactMech");
        String contactMechId = delegator.getNextSeqId("ContactMech");
        contactMech.put("contactMechTypeId", "TELECOM_NUMBER");
        contactMech.put("contactMechId", contactMechId);
        try {
        	delegator.create(contactMech);
        	GenericValue partyContactMech = delegator.makeValue("PartyContactMech");
        	partyContactMech.put("partyId", partyId);
        	partyContactMech.put("contactMechId", contactMechId);
        	partyContactMech.put("fromDate", UtilDateTime.nowTimestamp());
        	delegator.createOrStore(partyContactMech);
        	GenericValue partyContactMechPurpose = delegator.makeValue("PartyContactMechPurpose");
        	partyContactMechPurpose.put("contactMechPurposeTypeId", contactMechPurposeTypeId);
        	partyContactMechPurpose.put("partyId", partyId);
        	partyContactMechPurpose.put("contactMechId", contactMechId);
        	partyContactMechPurpose.put("fromDate", UtilDateTime.nowTimestamp());
        	delegator.createOrStore(partyContactMechPurpose);
        	GenericValue mapTelecom = delegator.makeValue("TelecomNumber");
        	mapTelecom.put("contactMechId", contactMechId);
        	mapTelecom.put("contactNumber", infoString);
        	delegator.createOrStore(mapTelecom);
        } catch (GeneralException e) {
            Debug.logError(e, module);
            throw e;
        }
        return contactMechId;
    }

	public static Map<String,Object> deleteShippingContract(DispatchContext ctx,
			Map<String, Object> context) throws GenericEntityException {
	    	Map<String, Object> result = new FastMap<String, Object>();
	    	Delegator delegator = ctx.getDelegator();
	    	String contractId = (String)context.get("contractId");
	    	
	    	Map<String, Object> ShippingContract = new HashMap<String, Object>();
	    	ShippingContract.put("contractId", contractId);
	    	try {
				delegator.removeByAnd("ShippingContract", ShippingContract);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return result;
	}
	public static Map<String,Object> deleteShipment(DispatchContext ctx,
			Map<String, Object> context) throws GenericEntityException {
	    	Map<String, Object> result = new FastMap<String, Object>();
	    	Delegator delegator = ctx.getDelegator();
	    	String shipmentId = (String)context.get("shipmentId");
	    	
	    	Map<String, Object> Shipment = new HashMap<String, Object>();
	    	Shipment.put("shipmentId", shipmentId);
	    	try {
				delegator.removeByAnd("Shipment", Shipment);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return result;
	}
	
	public static Map<String,Object> updateShipment(DispatchContext ctx,
		Map<String, Object> context) throws GenericEntityException {
    	Map<String, Object> result = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String partyId = (String)userLogin.get("partyId");
    	GenericValue Shipment = delegator.makeValue("Shipment");
    	Shipment.setNonPKFields(context);
    	Shipment.setPKFields(context);
    	String originFacilityId = (String)context.get("originFacilityId");
    	String shipmentId = (String)context.get("shipmentId");
    	if (shipmentId == null){
    		shipmentId = delegator.getNextSeqId("Shipment");
    		Shipment.put("shipmentId", shipmentId);
    		Shipment.put("createdDate", UtilDateTime.nowTimestamp());
    		Shipment.put("createdByUserLogin", partyId);
    	}
    	Shipment.put("lastModifiedDate", UtilDateTime.nowTimestamp());
		Shipment.put("lastModifiedByUserLogin", partyId);
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String orderId = (String)context.get("primaryOrderId");
    	if (orderId != null){
    		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
    		if (orderHeader != null){
    			originFacilityId = (String)orderHeader.get("originFacilityId");
    			OrderReadHelper orderHelper = new OrderReadHelper(orderHeader);
    			@SuppressWarnings("deprecation")
				GenericValue postalAddr = orderHelper.getShippingAddress();
    			if (postalAddr != null){
    				Shipment.put("destinationContactMechId", postalAddr.get("contactMechId"));
    			}
    		}
    	}
    	Shipment.put("originFacilityId", originFacilityId);
    	try {
			delegator.createOrStore(Shipment);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	List<GenericValue> listShipmentRouteSegments = delegator.findList("ShipmentRouteSegment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId)), null, null, null, false);
    	if (listShipmentRouteSegments.isEmpty()){
    		Map<String, Object> routeSegment = new FastMap<String, Object>();
    		routeSegment.put("shipmentId", shipmentId);
    		routeSegment.put("originFacilityId", (String)Shipment.get("originFacilityId"));
    		routeSegment.put("destFacilityId", (String)Shipment.get("destinationFacilityId"));
    		routeSegment.put("originContactMechId", (String)Shipment.get("originContactMechId"));
    		routeSegment.put("destContactMechId", (String)Shipment.get("destinationContactMechId"));
    		routeSegment.put("currencyUomId", (String)Shipment.get("currencyUomId"));
    		routeSegment.put("estimatedStartDate", Shipment.getTimestamp("estimatedShipDate"));
    		routeSegment.put("estimatedArrivalDate", Shipment.getTimestamp("estimatedArrivalDate"));
    		routeSegment.put("updatedByUserLoginId", partyId);
    		routeSegment.put("lastUpdatedDate", UtilDateTime.nowTimestamp());
    		routeSegment.put("userLogin", userLogin);
    		try {
				dispatcher.runSync("createShipmentRouteSegment", routeSegment);
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
    	}
    	if (orderId != null){
    		List<GenericValue> listOrderByShipments = delegator.findList("OrderShipment", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId, "orderId", orderId)), null, null, null, false);
        	if (listOrderByShipments.isEmpty()){
	    		List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
	    		if (!listOrderItems.isEmpty()){
	    			for (GenericValue item : listOrderItems){
	    				String orderItemSeqId = (String)item.get("orderItemSeqId");
	    				List<GenericValue> listOrderItemShipGroupAssoc = delegator.findList("OrderItemShipGroupAssoc", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId)), null, null, null, false);
	    				Map<String, Object> OrderShipment = new FastMap<String, Object>();
	    				OrderShipment.put("orderId", orderId);
	    				OrderShipment.put("orderItemSeqId", orderItemSeqId);
	    				if(!listOrderItemShipGroupAssoc.isEmpty()){
	    					for (GenericValue group : listOrderItemShipGroupAssoc){
	    						OrderShipment.put("shipGroupSeqId", group.get("shipGroupSeqId"));
			    				OrderShipment.put("shipmentId", shipmentId);
			    				try {
			    					OrderShipment.put("quantity", group.getBigDecimal("quantity"));
			    					OrderShipment.put("userLogin", userLogin);
									dispatcher.runSync("AddItemToShipmentPlan", OrderShipment);
								} catch (GenericServiceException e) {
									e.printStackTrace();
								}
	    					}
	    				} else {
	    					Map<String, Object> resultTmp = new FastMap<String, Object>();
	    					Map<String, Object> OrderShipGroup = new FastMap<String, Object>();
	    					OrderShipGroup.put("orderId", orderId);
	    					OrderShipGroup.put("userLogin", userLogin);
	    					try {
								resultTmp = dispatcher.runSync("createOrderItemShipGroup", OrderShipGroup);
								OrderShipment.put("shipGroupSeqId", resultTmp.get("shipGroupSeqId"));
							} catch (GenericServiceException e) {
								e.printStackTrace();
							}
	    					OrderShipment.put("shipmentItemSeqId", item.get("orderItemSeqId"));
		    				OrderShipment.put("shipmentId", shipmentId);
		    				GenericValue orderItemShipGroupAssoc = delegator.makeValue("OrderItemShipGroupAssoc");
		    				orderItemShipGroupAssoc.put("orderId", orderId);
		    				orderItemShipGroupAssoc.put("orderItemSeqId", orderItemSeqId);
		    				orderItemShipGroupAssoc.put("shipGroupSeqId", resultTmp.get("shipGroupSeqId"));
		    				orderItemShipGroupAssoc.put("quantity", item.getBigDecimal("quantity"));
		    				delegator.createOrStore(orderItemShipGroupAssoc);
		    				
		    				OrderShipment.put("quantity", item.getBigDecimal("quantity"));
		    				OrderShipment.put("userLogin", userLogin);
		    				try {
								dispatcher.runSync("AddItemToShipmentPlan", OrderShipment);
							} catch (GenericServiceException e) {
								e.printStackTrace();
							}
	    				}
	    				
	    			}
	    		}
        	}
    	}
    	result.put("shipmentId", shipmentId);
    	return result;
	}
	
	public static Map<String,Object> updateVehicle(DispatchContext ctx,
			Map<String, Object> context) throws GenericEntityException {
	    	Map<String, Object> result = new FastMap<String, Object>();
	    	Delegator delegator = ctx.getDelegator();
	    	GenericValue Vehicle = delegator.makeValue("Vehicle");
	    	String vehicleId = (String)context.get("vehicleId");
	    	if (vehicleId == null){
	    		vehicleId = delegator.getNextSeqId("Vehicle");
	    	}
	    	String shipmentMethodTypeId = (String)context.get("shipmentMethodTypeId");
	    	String vehicleTypeId = (String)context.get("vehicleTypeId");
	    	String partyCarrierId = (String)context.get("partyCarrierId");
	    	String vehicleName = (String)context.get("vehicleName");
	    	String contractId = (String)context.get("contractId");
	    	Locale locale = (Locale)context.get("locale");
	    	
	    	String maxWeightTmp = (String)context.get("maxWeight");
	    	BigDecimal maxWeight =  BigDecimal.ZERO;
	    	BigDecimal minWeight =  BigDecimal.ZERO;
	    	BigDecimal unitCost =  BigDecimal.ZERO;
	    	NumberFormat fm = NumberFormat.getInstance(locale);
	    	if (maxWeightTmp != null){
	    		try {
					maxWeight = new BigDecimal(fm.parse(maxWeightTmp).toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
	    	}
	    	
	    	String minWeightTmp = (String)context.get("minWeight");
			if (minWeightTmp != null){
				try {
					minWeight = new BigDecimal(fm.parse(minWeightTmp).toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
	    	}
			String unitCostTmp = (String)context.get("unitCost");
			if (unitCostTmp != null){
				try {
					unitCost = new BigDecimal(fm.parse(unitCostTmp).toString());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
	    	
	    	String weightUomId = (String)context.get("weightUomId");
	    	String lengthUomId = (String)context.get("lengthUomId");
	    	String currencyUomId = (String)context.get("currencyUomId");
	    	String statusId = (String)context.get("statusId");
	    	String description = (String)context.get("description");
	    	
	    	if (context.get("thruDate") != null){
	    		Timestamp thruDate = (Timestamp)(context.get("thruDate"));
	    		Vehicle.put("thruDate", thruDate);
	    	}
	    	if (context.get("fromDate") != null){
	    		Timestamp fromDate = (Timestamp)(context.get("fromDate"));
	    		Vehicle.put("fromDate", fromDate);
	    	} else {
	    		Timestamp fromDate = UtilDateTime.nowTimestamp();
	    		Vehicle.put("fromDate", fromDate);
	    	}
	    	
	    	Vehicle.put("vehicleId", vehicleId);
	    	Vehicle.put("vehicleTypeId", vehicleTypeId);
	    	Vehicle.put("shipmentMethodTypeId", shipmentMethodTypeId);
	    	Vehicle.put("partyCarrierId", partyCarrierId);
	    	Vehicle.put("vehicleName", vehicleName);
	    	Vehicle.put("maxWeight", maxWeight);
	    	Vehicle.put("minWeight", minWeight);
	    	Vehicle.put("weightUomId", weightUomId);
	    	Vehicle.put("currencyUomId", currencyUomId);
	    	Vehicle.put("lengthUomId", lengthUomId);
	    	Vehicle.put("statusId", statusId);
	    	Vehicle.put("description", description);
	    	Vehicle.put("unitCost", unitCost);
	    	
	    	try {
				delegator.createOrStore(Vehicle);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	result.put("contractId", contractId);
	    	result.put("vehicleId", vehicleId);
	    	return result;
	}
	
	public static Map<String,Object> deleteVehicle(DispatchContext ctx,
			Map<String, Object> context) throws GenericEntityException {
	    	Map<String, Object> result = new FastMap<String, Object>();
	    	Delegator delegator = ctx.getDelegator();
	    	String vehicleId = (String)context.get("vehicleId");
	    	String shipmentMethodTypeId = (String)context.get("shipmentMethodTypeId");
	    	
	    	Map<String, Object> Vehicle = new HashMap<String, Object>();
	    	Vehicle.put("vehicleId", vehicleId);
	    	Vehicle.put("shipmentMethodTypeId", shipmentMethodTypeId);
	    	try {
				delegator.removeByAnd("Vehicle", Vehicle);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return result;
	}
	
	public static Map<String,Object> deleteSupplierProduct(DispatchContext ctx,
			Map<String, Object> context) throws GenericEntityException {
	    	Map<String, Object> result = new FastMap<String, Object>();
	    	Delegator delegator = ctx.getDelegator();
	    	String productId = (String)context.get("productId");
	    	String partyId = (String)context.get("partyId");
	    	String currencyUomId = (String)context.get("currencyUomId");
	    	String minimumOrderQuantity = (String)context.get("minimumOrderQuantity");
	    	String availableFromDate = (String)context.get("availableFromDate");
	    	
	    	Map<String, Object> SupplierProduct = new HashMap<String, Object>();
	    	SupplierProduct.put("productId", productId);
	    	SupplierProduct.put("partyId", partyId);
	    	SupplierProduct.put("currencyUomId", currencyUomId);
	    	SupplierProduct.put("minimumOrderQuantity", minimumOrderQuantity);
	    	SupplierProduct.put("availableFromDate", availableFromDate);
	    	try {
				delegator.removeByAnd("SupplierProduct", SupplierProduct);
			} catch (Exception e) {
				e.printStackTrace();
			}
	    	return result;
	}
	
	public static Map<String,Object> createDelysProduct(DispatchContext ctx,
		Map<String, Object> context) throws GenericEntityException {
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	Map<String, Object> result = new FastMap<String, Object>();
    	Map<String, Object> resultPr = new FastMap<String, Object>();
    	Map<String, Object> resultInv = new FastMap<String, Object>();
    	Delegator delegator = ctx.getDelegator();
    	String facilityId = (String)context.get("facilityId");
    	String supplierId = (String)context.get("supplierId");
    	if (supplierId != null){
    		if (supplierId.contains(",")){
    			supplierId = supplierId.replace(",", "");
    		}
    	}
    	String productName = (String)context.get("productName");
    	String brandName = (String)context.get("brandName");
    	String productId = (String)context.get("productId");
    	String productTypeId = (String)context.get("productTypeId");
//    	String unitCost = (String)context.get("unitCost");
    	String quantityOnHand = (String)context.get("quantityOnHandTotal");
    	String description = (String)context.get("description");
//    	String currencyUomId = (String)context.get("currencyUomId");
    	String quantityUomId = (String)context.get("quantityUomId");
//    	String productHeight  = (String)context.get("productHeight");
//    	String heightUomId = (String)context.get("heightUomId");
//    	String productWidth = (String)context.get("productWidth");
//    	String widthUomId = (String)context.get("widthUomId");
    	String productWeight = (String)context.get("productWeight");
    	String weightUomId = (String)context.get("weightUomId");
//    	String lastPrice = (String)context.get("lastPrice");
//    	String minimumOrderQuantity = (String)context.get("minimumOrderQuantity");
    	String inventoryItemTypeId = (String)context.get("inventoryItemTypeId");
   	 	Timestamp availableFromDate = UtilDateTime.nowTimestamp();
    	if (inventoryItemTypeId == null || "".equals(inventoryItemTypeId)){
    		inventoryItemTypeId = "GOOD_FOR_SALES";
    	}
    	Map<String, Object> product = new HashMap<String, Object>();
    	if (productId != null){
    		product.put("productId", productId);
    	}
    	product.put("userLogin", userLogin);
    	product.put("facilityId", facilityId);
    	product.put("productName", productName);
    	product.put("internalName", productName);
    	product.put("brandName", brandName);
    	product.put("productTypeId", productTypeId);
    	product.put("description", description);
    	product.put("quantityUomId", quantityUomId);
//    	product.put("productHeight",new BigDecimal(productHeight));
//    	product.put("heightUomId", heightUomId);
//    	product.put("productWidth",new BigDecimal(productWidth));
//    	product.put("widthUomId", widthUomId);
    	product.put("productWeight",new BigDecimal(productWeight));
    	product.put("weightUomId", weightUomId);
    	
    	try {
			resultPr = dispatcher.runSync("createProduct", product);
			String error = (String)resultPr.get(ModelService.RESPONSE_MESSAGE);
			if ("error".equals(error)){
				return ServiceUtil.returnError("error");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	Map<String, Object> inventoryItem = new HashMap<String, Object>();
    	String productIdTemp = (String)resultPr.get("productId");
    	inventoryItem.put("productId", productIdTemp);
    	inventoryItem.put("facilityId", facilityId);
    	inventoryItem.put("inventoryItemTypeId", inventoryItemTypeId);
    	inventoryItem.put("unitCost", BigDecimal.ZERO);
    	inventoryItem.put("currencyUomId", "VND");
    	inventoryItem.put("userLogin", userLogin);
    	inventoryItem.put("datetimeReceived", availableFromDate);
    	try {
    		resultInv = dispatcher.runSync("createInventoryItem", inventoryItem);
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
    	String inventoryItemId = (String)resultInv.get("inventoryItemId");
    	
    	GenericValue invUpdate = delegator.makeValue("InventoryItem");
    	invUpdate.put("productId", productIdTemp);
    	invUpdate.put("facilityId", facilityId);
    	invUpdate.put("inventoryItemTypeId", inventoryItemTypeId);
    	invUpdate.put("unitCost", BigDecimal.ZERO);
    	invUpdate.put("quantityOnHandTotal",new BigDecimal(quantityOnHand));
    	invUpdate.put("availableToPromiseTotal",new BigDecimal(quantityOnHand));
    	invUpdate.put("currencyUomId", "VND");
    	invUpdate.put("inventoryItemId", inventoryItemId);
    	delegator.store(invUpdate);
    	
   	 	Map<String, Object> productSupplier = new HashMap<String, Object>();
    	productSupplier.put("productId", productIdTemp);
    	productSupplier.put("partyId", supplierId);
    	productSupplier.put("supplierProductId", supplierId);
    	productSupplier.put("currencyUomId", "VND");
    	productSupplier.put("availableFromDate", availableFromDate);
    	productSupplier.put("lastPrice", BigDecimal.ZERO);
    	productSupplier.put("minimumOrderQuantity", BigDecimal.ZERO);
    	productSupplier.put("userLogin", userLogin);
    	try {
			dispatcher.runSync("createSupplierProduct", productSupplier);
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError("error");
		}
    	result.put("productId", resultPr.get("productId"));
		return result;
    }
	public static Map<String, Object> updateRequirementLogistics(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		
		//Init var
		String targetLink = "";
		String header = "";
		String notiToPartyId = "logspecialist";
		String action = "";
		String state="open";
		Timestamp dateTime = new Timestamp(new Date().getTime());
		
		
		// Get parameters
		String requestor = (String)context.get("requestor");
		String requirementTypeId = (String)context.get("requirementTypeId");
		String requirementId = (String)context.get("requirementId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		String partyId = (String)userLogin.get("partyId");
		Timestamp requiredByDate = (Timestamp)context.get("requiredByDate");
		Timestamp requirementStartDate = (Timestamp)context.get("requirementStartDate"); 
		String productStoreId = (String)context.get("productStoreId");
		String currencyUomId = (String)context.get("currencyUomId");
		String facilityId = (String)context.get("facilityId");
		String originContactMechId = (String)context.get("originContactMechId");
		String destContactMechId = (String)context.get("destContactMechId");
		Locale locale = (Locale)context.get("locale");
		if (originContactMechId != null){
			if (requiredByDate == null){                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           
				requiredByDate = UtilDateTime.nowTimestamp();
			}
			if (context.get("requirementId") == null){
				requirementId = delegator.getNextSeqId("Requirement");
				context.put("requirementId", requirementId);
			}
			
			GenericValue requirement = delegator.makeValue("Requirement");
			requirement.setNonPKFields(context);
			requirement.setPKFields(context);
			requirement.put("productStoreId", productStoreId);
			requirement.put("currencyUomId", currencyUomId);
			requirement.put("facilityId", facilityId);
			requirement.put("contactMechId", originContactMechId);
			requirement.put("requirementStartDate", requirementStartDate);
			requirement.put("requiredByDate", requiredByDate);
			delegator.createOrStore(requirement);
			requirementId = (String)requirementId;
			
			List<String> listOrderBy = new ArrayList<String>();
			listOrderBy.add("-fromDate");
			try {
				List<GenericValue> requirementRoles = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("partyId", requestor, "requirementId", requirementId, "roleTypeId", "OWNER")), null, listOrderBy, null, false);
				requirementRoles = EntityUtil.filterByDate(requirementRoles);
				if (requirementRoles.isEmpty()){
					GenericValue requirementRole = delegator.makeValue("RequirementRole");
					requirementRole.put("requirementId", requirementId);
					requirementRole.put("roleTypeId", "OWNER");
					requirementRole.put("fromDate", requiredByDate);
					GenericValue partyRole = delegator.makeValue("PartyRole");
					partyRole.put("roleTypeId", "OWNER");
					if (requestor != null){
						requirementRole.put("partyId", requestor);
						partyRole.put("partyId", requestor);
					} else {
						requirementRole.put("partyId", partyId);
						partyRole.put("partyId", partyId);
					}
					delegator.createOrStore(partyRole);
					delegator.createOrStore(requirementRole);
				}
			} catch (GenericEntityException e1) {
				Debug.logError(e1, module);
				return ServiceUtil.returnError(e1.getMessage());
			}
			if ("INTERNAL_SALES_REQ".equals(requirementTypeId)){
				List<GenericValue> requirementRoles = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("partyId", requestor, "requirementId", requirementId, "roleTypeId", "CUSTOMER")), null, listOrderBy, null, false);	
				List<GenericValue> findAmountLimit = delegator.findList("FindAmountLimitIntenalPurchaseLimit", EntityCondition.makeCondition(UtilMisc.toMap("PartyID", requestor)), null, null, null, false);
				
				if(findAmountLimit.isEmpty() == true){
					return ServiceUtil.returnError(UtilProperties.getMessage("InternalPurchaseUiLables", "checkIntenalPurchaseLimitIsExits", locale));
					
				}
				for (GenericValue listAmount : findAmountLimit) {
					String amountLimitRemain = (String) listAmount.get("AmountLimitRemain");
					if(Integer.parseInt(amountLimitRemain) == 0){
						return ServiceUtil.returnError(UtilProperties.getMessage("InternalPurchaseUiLables", "checkAmountLimitRemain", locale));
					}
				}
				
				requirementRoles = EntityUtil.filterByDate(requirementRoles);
				if (requirementRoles.isEmpty()){
					GenericValue reqCustomerRole = delegator.makeValue("RequirementRole");
					GenericValue custPartyRole = delegator.makeValue("PartyRole");
					custPartyRole.put("partyId", requestor);
					custPartyRole.put("roleTypeId", "CUSTOMER");
					delegator.createOrStore(custPartyRole);
					reqCustomerRole.put("partyId", requestor);
					reqCustomerRole.put("requirementId", requirementId);
					reqCustomerRole.put("roleTypeId", "CUSTOMER");
					reqCustomerRole.put("fromDate", requiredByDate);
					delegator.createOrStore(reqCustomerRole);
				}
				action = "EditRequirement?requirementId="+requirementId+"&"+"requirementTypeId="+requirementTypeId;
			}
			
			if ("SALES_REQ".equals(requirementTypeId) || "CHANGE_DATE_REQ".equals(requirementTypeId)){
				List<GenericValue> requirementRoles = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("partyId", requestor, "requirementId", requirementId, "roleTypeId", "CUSTOMER")), null, listOrderBy, null, false);
				requirementRoles = EntityUtil.filterByDate(requirementRoles);
				if (requirementRoles.isEmpty()){
					String customerParty = (String)context.get("customerParty");
					GenericValue reqCustomerRole = delegator.makeValue("RequirementRole");
					GenericValue custPartyRole = delegator.makeValue("PartyRole");
					custPartyRole.put("partyId", customerParty);
					custPartyRole.put("roleTypeId", "CUSTOMER");
					delegator.createOrStore(custPartyRole);
					reqCustomerRole.put("partyId", customerParty);
					reqCustomerRole.put("requirementId", requirementId);
					reqCustomerRole.put("roleTypeId", "CUSTOMER");
					reqCustomerRole.put("fromDate", requiredByDate);
					delegator.createOrStore(reqCustomerRole);
				}
			}
			if ("GIFT_REQ".equals(requirementTypeId)){
				String receiverParty = (String)context.get("receiverParty");
				if (receiverParty != null){
					List<GenericValue> requirementRoles = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("partyId", requestor, "requirementId", requirementId, "roleTypeId", "RECEIVER")), null, listOrderBy, null, false);
					requirementRoles = EntityUtil.filterByDate(requirementRoles);
					if (requirementRoles.isEmpty()){
						GenericValue reqReceiveGiftRole = delegator.makeValue("RequirementRole");
						GenericValue receiverPartyRole = delegator.makeValue("PartyRole");
						receiverPartyRole.put("partyId", receiverParty);
						receiverPartyRole.put("roleTypeId", "RECEIVER");
						delegator.createOrStore(receiverPartyRole);
						reqReceiveGiftRole.put("partyId", receiverParty);
						reqReceiveGiftRole.put("requirementId", requirementId);
						reqReceiveGiftRole.put("roleTypeId", "RECEIVER");
						reqReceiveGiftRole.put("fromDate", requiredByDate);
						delegator.createOrStore(reqReceiveGiftRole);
					}
				}
			}
			if ("TRANSFER_REQ".equals(requirementTypeId) || "TRANS_INTERNAL_REQ".equals(requirementTypeId) || "TRANS_CHANNEL_REQ".equals(requirementTypeId)){
				String facilityTo = (String)context.get("facilityTo");
				String productStoreIdTo = (String)context.get("productStoreIdTo");
				GenericValue reqFacility = delegator.makeValue("RequirementFacility");
				reqFacility.put("facilityIdTo", facilityTo);
				reqFacility.put("originContactMechId", originContactMechId);
				reqFacility.put("destContactMechId", destContactMechId);
				reqFacility.put("facilityIdFrom", facilityId);
				reqFacility.put("productStoreIdFrom", productStoreId);
				reqFacility.put("productStoreIdTo", productStoreIdTo);
				reqFacility.put("requirementId", requirementId);
				reqFacility.put("description", context.get("description"));
				delegator.createOrStore(reqFacility);
			}
			if ("RECEIVE_ORDER_REQ".equals(requirementTypeId)){
				String purchaseOrderId = (String)context.get("orderId");
				List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", purchaseOrderId)), null, null, null, false);
	    		if (!listOrderItems.isEmpty()){
	    			for (GenericValue item : listOrderItems){
	    				GenericValue orderReqCommitment = delegator.makeValue("OrderRequirementCommitment");
	    				BigDecimal quantity = item.getBigDecimal("quantity");
	    				orderReqCommitment.put("requirementId", requirementId);
	    				orderReqCommitment.put("orderItemSeqId", item.get("orderItemSeqId"));
	    				orderReqCommitment.put("orderId", purchaseOrderId);
	    				orderReqCommitment.put("quantity", quantity);
	    				delegator.createOrStore(orderReqCommitment);
	    			}
	    		}
	    		result.put("selectedSubMenuItem", "ReceiveFromOrder");
			}
			if ("RECEIVE_PRODUCT_REQ".equals(requirementTypeId)){
				String statusId = (String)context.get("statusId");
				List<GenericValue> listReqItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
				if (listReqItems.isEmpty() && ("REQ_APPROVED".equals(statusId) || "REQ_COMPLETED".equals(statusId))){
					return ServiceUtil.returnError(UtilProperties.getMessage(resource,
		                    "CannotApproveWithNoProduct", (Locale)context.get("locale")));
				} else {
					result.put("selectedSubMenuItem", "ReceiveByProduct");
				}
			}
		} else {
			Map<String, Object> mapError = ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "FacilityNotHaveContactMech", locale));
			mapError.put("requirementId", requirementId);
			return mapError;
		}
		String ntfId = (String)context.get("requirementId");
		result.put("requirementId", requirementId);
		result.put("header", header);
		result.put("notiToId", notiToPartyId);
		result.put("state", state);
		result.put("dateTime", dateTime);
		result.put("targetLink", targetLink);
		result.put("action", action);
		result.put("ntfId", ntfId);
		result.put(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_SUCCESS);
		result.put(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("InternalPurchaseUiLables", "createSuccessfully", locale));
		return result;
	}
	public static Map<String, Object> approvedRequirement(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String requirementId = (String)context.get("requirementId");
		Delegator delegator = ctx.getDelegator();
		GenericValue requirement = delegator.makeValue("Requirement");
		requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
		if (requirement != null){
			if ("RECEIVE_ORDER_REQ".equals((String)requirement.get("requirementTypeId"))){
				List<GenericValue> orderReq = delegator.findList("OrderRequirement", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
				if (!orderReq.isEmpty()){
					for (GenericValue req : orderReq){
						List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", req.get("orderId"))), null, null, null, false);
						if (!orderItems.isEmpty()){
							for (GenericValue item : orderItems){
								if (!"ITEM_APPROVED".equals((String)item.get("statusId")) && !"ITEM_COMPLETED".equals((String)item.get("statusId")) && !"ITEM_REJECTED".equals((String)item.get("statusId"))){
									item.put("statusId", "ITEM_APPROVED");
									delegator.store(item);
								}
							}
						}
					}
				}
			}
			requirement.put("statusId", "REQ_APPROVED");
			delegator.createOrStore(requirement);
		}
		result.put("requirementId", requirementId);
		return result;
	}
	public static String updateChangeDateOrder(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		String currencyUom = (String)context.get("uomId");
		Locale locale = UtilHttp.getLocale(request);
		String requirementId = (String)context.get("requirementId");
		String returnId = (String)context.get("returnId");
		if (requirementId != null){
	    	GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
	    	List<String> listOrderBy = new ArrayList<String>();
	    	listOrderBy.add("-fromDate");
	    	List<GenericValue> listReqRole = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId, "roleTypeId", "CUSTOMER")), null, listOrderBy, null, false);
	    	listReqRole = EntityUtil.filterByDate(listReqRole);
	    	if (listReqRole.isEmpty()){
	    		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
	                    "RequirementDoNotHadCustomerOrNotAvailableInThisTime", locale));
	    		return "error";
	    	} else {
		    	GenericValue requirementRole = listReqRole.get(0);
		    	GenericValue custParty = delegator.findOne("Party", false, UtilMisc.toMap("partyId", requirementRole.get("partyId")));
		    	List<GenericValue> listReqItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
		    	String facilityId = (String)requirement.get("facilityId");
		    	String productStoreId = (String)requirement.get("productStoreId");
		    	session.setAttribute("shoppingCart", null);
				ShoppingCart cart = ShoppingCartEvents.getCartObject(request, locale, currencyUom);
				try {
					cart.setProductStoreId(productStoreId);
					cart.setAllShipmentMethodTypeId("NO_SHIPPING");
					cart.setCurrency(dispatcher, currencyUom);
					cart.setUserLogin(userLogin, dispatcher);
					cart.setFacilityId(facilityId);
					cart.setLocale(locale);
					cart.setPlacingCustomerPartyId((String)custParty.get("partyId"));
					List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
					Collection<GenericValue> listContacts = ContactHelper.getContactMech(custParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
					if (listContactMechs instanceof List)
						listContactMechs = (List<GenericValue>)listContacts;
					else
						listContactMechs = new ArrayList<GenericValue>(listContacts);
					Collections.sort(listContactMechs);
					if (listContactMechs.isEmpty()){
	    				request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
	    	                    "NoCustomerShippingLocation", locale));
	    				return "error";
	    			} else {
	    				GenericValue contactMech = listContactMechs.get(0);
	    				cart.setAllShippingContactMechId((String)contactMech.get("contactMechId"));
	    				request.setAttribute("shipping_contact_mech_id", (String)contactMech.get("contactMechId"));
	    			}
				} catch (CartItemModifyException e) {
					e.printStackTrace();
				}
				ShoppingCartHelper helper = new ShoppingCartHelper(delegator, dispatcher, cart);
				if (!listReqItems.isEmpty()){
		    		for (GenericValue item : listReqItems){
		    			BigDecimal quantity = BigDecimal.ONE;
		    			GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", (String)item.get("productId")));
		    			String uomBase = (String)product.get("quantityUomId");
		    			quantity = (item.getBigDecimal("quantity")).multiply(getConvertNumber(delegator, quantity, (String)item.get("productId"), (String)item.get("quantityUomId"), uomBase));
		    			Timestamp shipBeforeDate = (Timestamp)item.get("shipBeforeDate");
		    			Timestamp shipAfterDate = (Timestamp)item.get("shipAfterDate");
		    			String catalogId = (String)item.get("prodCatalogId");
		    			String productId = (String)product.get("productId");
		    			String productCategoryId = (String)item.get("prodCategoryId");
		    			helper.addToCart(catalogId, null, null, productId, productCategoryId, "CHANGE_DATE_ITEM", null, null, null, quantity, null, null, null, null, null, shipBeforeDate, shipAfterDate, null, null, context, null);
		    		}
				} else {
	    			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
	                        "RequirementNotHasItem", locale));
	        		return "error";
	    		}
				String[] paymentMethods = new String[1];
	    		paymentMethods[0] = "EXT_OFFLINE";
	    		request.setAttribute("checkOutPaymentId", paymentMethods);
	            request.setAttribute("shipping_method", "Default");
	            request.setAttribute("is_gift", "false");
	            request.setAttribute("may_split", "false");
	            CheckOutEvents.setCheckOutOptions(request, response);
				Map<String, Object> mapResult = createOrder(request, response);
				String orderId = (String)mapResult.get("orderId");
				String customerId = (String)custParty.get("partyId");
	    		String orderName = (String)context.get("orderName");
	    		if (orderId != null){
	    			GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
	    			orderHeader.put("orderTypeId", "SALES_ORDER");
	    			orderHeader.put("orderName", orderName);
	    			String salesMethodChannelEnumId = (String)context.get("salesMethodChannelEnumId");
	    			orderHeader.put("salesMethodChannelEnumId", salesMethodChannelEnumId);
	    			delegator.store(orderHeader);
	    			GenericValue orderRole = delegator.makeValue("OrderRole");
	    			GenericValue partyRole = delegator.makeValue("PartyRole");
	    			partyRole.put("partyId", customerId);
	    			partyRole.put("roleTypeId", "PLACING_CUSTOMER");
	    			delegator.createOrStore(partyRole);
	        		orderRole.put("partyId", customerId);
	        		orderRole.put("orderId", orderId);
	        		orderRole.put("roleTypeId", "PLACING_CUSTOMER");
	        		delegator.createOrStore(orderRole);
	        		partyRole.put("roleTypeId", "END_USER_CUSTOMER");
	    			delegator.createOrStore(partyRole);
	        		orderRole.put("roleTypeId", "END_USER_CUSTOMER");
	        		delegator.createOrStore(orderRole);
	        		partyRole.put("roleTypeId", "BILL_TO_CUSTOMER");
	    			delegator.createOrStore(partyRole);
	        		orderRole.put("roleTypeId", "BILL_TO_CUSTOMER");
	        		delegator.createOrStore(orderRole);
	        		partyRole.put("roleTypeId", "SHIP_TO_CUSTOMER");
	    			delegator.createOrStore(partyRole);
	        		orderRole.put("roleTypeId", "SHIP_TO_CUSTOMER");
	        		delegator.createOrStore(orderRole);
	        		orderRole.put("partyId", "company");
	        		orderRole.put("roleTypeId", "BILL_FROM_VENDOR");
	        		delegator.createOrStore(orderRole);
	
	        		GenericValue orderPaymentRef = delegator.makeValue("OrderPaymentPreference");
	        		String orderPaymentRefId = delegator.getNextSeqId("OrderPaymentPreference");
	        		orderPaymentRef.put("paymentMethodTypeId", "CASH");
	        		orderPaymentRef.put("orderId", orderId);
	        		orderPaymentRef.put("orderPaymentPreferenceId", orderPaymentRefId);
	        		delegator.createOrStore(orderPaymentRef);
	        		
	        		List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
	        		if (!listOrderItems.isEmpty()){
	        			for (GenericValue item : listOrderItems){
	        				GenericValue orderReqCommitment = delegator.makeValue("OrderRequirementCommitment");
	        				BigDecimal quantity = item.getBigDecimal("quantity");
	        				orderReqCommitment.put("requirementId", requirementId);
	        				orderReqCommitment.put("orderItemSeqId", item.get("orderItemSeqId"));
	        				orderReqCommitment.put("orderId", orderId);
	        				orderReqCommitment.put("quantity", quantity);
	        				delegator.createOrStore(orderReqCommitment);
	        			}
	        		}
	    		}
	    		requirement.put("statusId", "REQ_ORDERED");
	    		delegator.store(requirement);
	    	}
		} else {
			if (returnId != null){
				GenericValue returnHeader = delegator.findOne("ReturnHeader", UtilMisc.toMap("returnId", returnId), false);
				List<GenericValue> listReturnItems = delegator.findList("ReturnItem", EntityCondition.makeCondition(UtilMisc.toMap("returnId", returnId, "returnReasonId", "RTN_CHANGE_DATE", "returnTypeId", "RTN_REPLACE")), null, null, null, false);
				session.setAttribute("shoppingCart", null);
				ShoppingCart cart = ShoppingCartEvents.getCartObject(request, locale, currencyUom);
		    	String facilityId = (String)returnHeader.get("destinationFacilityId");
		    	String productStoreId = (String)context.get("productStoreId");
		    	try {
			    	cart.setProductStoreId(productStoreId);
					cart.setAllShipmentMethodTypeId("NO_SHIPPING");
					cart.setCurrency(dispatcher, currencyUom);
					cart.setUserLogin(userLogin, dispatcher);
					cart.setFacilityId(facilityId);
					cart.setLocale(locale);
				} catch (CartItemModifyException e) {
					e.printStackTrace();
				}
				String custPartyId = (String)returnHeader.get("fromPartyId");
				cart.setPlacingCustomerPartyId(custPartyId);
				List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
				GenericValue custParty = delegator.findOne("Party", UtilMisc.toMap("partyId", custPartyId), false);
				Collection<GenericValue> listContacts = ContactHelper.getContactMech(custParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
				if (listContactMechs instanceof List)
					listContactMechs = (List<GenericValue>)listContacts;
				else
					listContactMechs = new ArrayList<GenericValue>(listContacts);
				Collections.sort(listContactMechs);
				if (listContactMechs.isEmpty()){
    				request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
    	                    "NoCustomerShippingLocation", locale));
    				return "error";
    			} else {
    				GenericValue contactMech = listContactMechs.get(0);
    				cart.setAllShippingContactMechId((String)contactMech.get("contactMechId"));
    				request.setAttribute("shipping_contact_mech_id", (String)contactMech.get("contactMechId"));
    			}
				ShoppingCartHelper helper = new ShoppingCartHelper(delegator, dispatcher, cart);
				if (!listReturnItems.isEmpty()){
					for (GenericValue item : listReturnItems){
						GenericValue orderItem = null;
						String orderId = null;
						orderId = (String)item.get("orderId");
						String orderItemSeqId = (String)item.get("orderItemSeqId");
						if (orderId != null) {
							if (orderItemSeqId != null){
								orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId), false);
							}
						}
		    			BigDecimal quantity = BigDecimal.ONE;
		    			GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", (String)item.get("productId")));
		    			quantity = item.getBigDecimal("returnQuantity");
		    			String catalogId = (String)orderItem.get("prodCatalogId");
		    			String productId = (String)product.get("productId");
		    			String productCategoryId = (String)orderItem.get("prodCategoryId");
		    			helper.addToCart(catalogId, null, null, productId, productCategoryId, "CHANGE_DATE_ITEM", null, null, null, quantity, null, null, null, null, null, null, null, null, null, context, null);
					}
					String[] paymentMethods = new String[1];
		    		paymentMethods[0] = "EXT_OFFLINE";
		    		request.setAttribute("checkOutPaymentId", paymentMethods);
		            request.setAttribute("shipping_method", "Default");
		            request.setAttribute("is_gift", "false");
		            request.setAttribute("may_split", "false");
		            CheckOutEvents.setCheckOutOptions(request, response);
					Map<String, Object> mapResult = createOrder(request, response);
					String newOrderId = (String)mapResult.get("orderId");
					String customerId = (String)custParty.get("partyId");
		    		String orderName = (String)context.get("orderName");
		    		if (newOrderId != null){
		    			GenericValue newOrderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", newOrderId));
		    			newOrderHeader.put("orderTypeId", "SALES_ORDER");
		    			newOrderHeader.put("orderName", orderName);
		    			delegator.store(newOrderHeader);
		    			GenericValue orderRole = delegator.makeValue("OrderRole");
		    			GenericValue partyRole = delegator.makeValue("PartyRole");
		    			partyRole.put("partyId", customerId);
		    			partyRole.put("roleTypeId", "PLACING_CUSTOMER");
		    			delegator.createOrStore(partyRole);
		        		orderRole.put("partyId", customerId);
		        		orderRole.put("orderId", newOrderId);
		        		orderRole.put("roleTypeId", "PLACING_CUSTOMER");
		        		delegator.createOrStore(orderRole);
		        		partyRole.put("roleTypeId", "END_USER_CUSTOMER");
		    			delegator.createOrStore(partyRole);
		        		orderRole.put("roleTypeId", "END_USER_CUSTOMER");
		        		delegator.createOrStore(orderRole);
		        		partyRole.put("roleTypeId", "BILL_TO_CUSTOMER");
		    			delegator.createOrStore(partyRole);
		        		orderRole.put("roleTypeId", "BILL_TO_CUSTOMER");
		        		delegator.createOrStore(orderRole);
		        		partyRole.put("roleTypeId", "SHIP_TO_CUSTOMER");
		    			delegator.createOrStore(partyRole);
		        		orderRole.put("roleTypeId", "SHIP_TO_CUSTOMER");
		        		delegator.createOrStore(orderRole);
		        		orderRole.put("partyId", "company");
		        		orderRole.put("roleTypeId", "BILL_FROM_VENDOR");
		        		delegator.createOrStore(orderRole);
		
		        		GenericValue orderPaymentRef = delegator.makeValue("OrderPaymentPreference");
		        		String orderPaymentRefId = delegator.getNextSeqId("OrderPaymentPreference");
		        		orderPaymentRef.put("paymentMethodTypeId", "CASH");
		        		orderPaymentRef.put("orderId", newOrderId);
		        		orderPaymentRef.put("orderPaymentPreferenceId", orderPaymentRefId);
		        		delegator.createOrStore(orderPaymentRef);
		    		}
				} else {
					request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage(resource,
		                    "ThisReturnNotHadChangeDateItemOrNotIncludeReplaceType", locale));
		    		return "error";
				}
			}
		}
		return "sucess";
	}
	public static String updateMarketingOrder(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		String currencyUom = (String)context.get("uomId");
		Locale locale = UtilHttp.getLocale(request);
		String requirementId = (String)context.get("requirementId");
    	GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
    	List<String> listOrderBy = new ArrayList<String>();
    	listOrderBy.add("-fromDate");
    	List<GenericValue> listReqRole = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId, "roleTypeId", "OWNER")), null, listOrderBy, null, false);
    	listReqRole = EntityUtil.filterByDate(listReqRole);
    	if (listReqRole.isEmpty()){
    		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
                    "RequirementDoNotHadCustomerOrNotAvailableInThisTime", locale));
    		return "error";
    	} else {
	    	GenericValue requirementRole = listReqRole.get(0);
	    	GenericValue custParty = delegator.findOne("Party", false, UtilMisc.toMap("partyId", requirementRole.get("partyId")));
	    	List<GenericValue> listReqItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
	    	String facilityId = (String)requirement.get("facilityId");
	    	String productStoreId = (String)requirement.get("productStoreId");
	    	session.setAttribute("shoppingCart", null);
			ShoppingCart cart = ShoppingCartEvents.getCartObject(request, locale, currencyUom);
			try {
				cart.setProductStoreId(productStoreId);
				cart.setAllShipmentMethodTypeId("NO_SHIPPING");
				cart.setCurrency(dispatcher, currencyUom);
				cart.setUserLogin(userLogin, dispatcher);
				cart.setFacilityId(facilityId);
				cart.setLocale(locale);
				cart.setPlacingCustomerPartyId((String)custParty.get("partyId"));
				List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
				Collection<GenericValue> listContacts = ContactHelper.getContactMech(custParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
				if (listContactMechs instanceof List)
					listContactMechs = (List<GenericValue>)listContacts;
				else
					listContactMechs = new ArrayList<GenericValue>(listContacts);
				Collections.sort(listContactMechs);
				if (listContactMechs.isEmpty()){
    				request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
    	                    "NoCustomerShippingLocation", locale));
    				return "error";
    			} else {
    				GenericValue contactMech = listContactMechs.get(0);
    				cart.setAllShippingContactMechId((String)contactMech.get("contactMechId"));
    				request.setAttribute("shipping_contact_mech_id", (String)contactMech.get("contactMechId"));
    			}
			} catch (CartItemModifyException e) {
				e.printStackTrace();
			}
			ShoppingCartHelper helper = new ShoppingCartHelper(delegator, dispatcher, cart);
			if (!listReqItems.isEmpty()){
	    		for (GenericValue item : listReqItems){
	    			BigDecimal quantity = BigDecimal.ONE;
	    			GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", (String)item.get("productId")));
	    			String uomBase = (String)product.get("quantityUomId");
	    			quantity = (item.getBigDecimal("quantity")).multiply(getConvertNumber(delegator, quantity, (String)item.get("productId"), (String)item.get("quantityUomId"), uomBase));
	    			Timestamp shipBeforeDate = (Timestamp)item.get("shipBeforeDate");
	    			Timestamp shipAfterDate = (Timestamp)item.get("shipBeforeDate");
	    			String catalogId = (String)item.get("prodCatalogId");
	    			String productId = (String)product.get("productId");
	    			String productCategoryId = (String)item.get("prodCategoryId");
	    			helper.addToCart(catalogId, null, null, productId, productCategoryId, "SAMPLING_ITEM", null, null, null, quantity, null, null, null, null, null, shipBeforeDate, shipAfterDate, null, null, context, null);
	    		}
			} else {
    			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
                        "RequirementNotHasItem", locale));
        		return "error";
    		}
			String[] paymentMethods = new String[1];
    		paymentMethods[0] = "EXT_OFFLINE";
    		request.setAttribute("checkOutPaymentId", paymentMethods);
            request.setAttribute("shipping_method", "Default");
            request.setAttribute("is_gift", "false");
            request.setAttribute("may_split", "false");

    		CheckOutEvents.setCheckOutOptions(request, response);
			Map<String, Object> mapResult = createOrder(request, response);
			String orderId = (String)mapResult.get("orderId");
			String customerId = (String)custParty.get("partyId");
    		String orderName = (String)context.get("orderName");
    		if (orderId != null){
    			GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
    			orderHeader.put("orderTypeId", "SALES_ORDER");
    			orderHeader.put("orderName", orderName);
    			String salesMethodChannelEnumId = (String)context.get("salesMethodChannelEnumId");
    			orderHeader.put("salesMethodChannelEnumId", salesMethodChannelEnumId);
    			delegator.store(orderHeader);
    			GenericValue orderRole = delegator.makeValue("OrderRole");
    			GenericValue partyRole = delegator.makeValue("PartyRole");
    			partyRole.put("partyId", customerId);
    			partyRole.put("roleTypeId", "PLACING_CUSTOMER");
    			delegator.createOrStore(partyRole);
        		orderRole.put("partyId", customerId);
        		orderRole.put("orderId", orderId);
        		orderRole.put("roleTypeId", "PLACING_CUSTOMER");
        		delegator.createOrStore(orderRole);
        		partyRole.put("roleTypeId", "END_USER_CUSTOMER");
    			delegator.createOrStore(partyRole);
        		orderRole.put("roleTypeId", "END_USER_CUSTOMER");
        		delegator.createOrStore(orderRole);
        		partyRole.put("roleTypeId", "BILL_TO_CUSTOMER");
    			delegator.createOrStore(partyRole);
        		orderRole.put("roleTypeId", "BILL_TO_CUSTOMER");
        		delegator.createOrStore(orderRole);
        		partyRole.put("roleTypeId", "SHIP_TO_CUSTOMER");
    			delegator.createOrStore(partyRole);
        		orderRole.put("roleTypeId", "SHIP_TO_CUSTOMER");
        		delegator.createOrStore(orderRole);
        		orderRole.put("partyId", "company");
        		orderRole.put("roleTypeId", "BILL_FROM_VENDOR");
        		delegator.createOrStore(orderRole);
        		
        		GenericValue orderPaymentRef = delegator.makeValue("OrderPaymentPreference");
        		String orderPaymentRefId = delegator.getNextSeqId("OrderPaymentPreference");
        		orderPaymentRef.put("paymentMethodTypeId", "CASH");
        		orderPaymentRef.put("orderId", orderId);
        		orderPaymentRef.put("orderPaymentPreferenceId", orderPaymentRefId);
        		delegator.createOrStore(orderPaymentRef);
        		
        		List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
        		if (!listOrderItems.isEmpty()){
        			for (GenericValue item : listOrderItems){
        				GenericValue orderReqCommitment = delegator.makeValue("OrderRequirementCommitment");
        				BigDecimal quantity = item.getBigDecimal("quantity");
        				orderReqCommitment.put("requirementId", requirementId);
        				orderReqCommitment.put("orderItemSeqId", item.get("orderItemSeqId"));
        				orderReqCommitment.put("orderId", orderId);
        				orderReqCommitment.put("quantity", quantity);
        				delegator.createOrStore(orderReqCommitment);
        			}
        		}
    		}
    		requirement.put("statusId", "REQ_ORDERED");
    		delegator.store(requirement);
    	}
		return "sucess";
	}
	
	public static String updateTransferOrder(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
        HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		String currencyUom = (String)context.get("uomId");
		Locale locale = UtilHttp.getLocale(request);
		String requirementId = (String)context.get("requirementId");
    	GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
    	List<String> listOrderBy = new ArrayList<String>();
    	listOrderBy.add("-fromDate");
    	List<GenericValue> listReqRole = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId, "roleTypeId", "OWNER")), null, listOrderBy, null, false);
    	listReqRole = EntityUtil.filterByDate(listReqRole);
    	if (listReqRole.isEmpty()){
    		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
                    "RequirementDoNotHadCustomerOrNotAvailableInThisTime", locale));
    		return "error";
    	} else {
    		GenericValue requirementRole = listReqRole.get(0);
    		GenericValue custParty = delegator.findOne("Party", false, UtilMisc.toMap("partyId", requirementRole.get("partyId")));
        	List<GenericValue> listReqItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
        	String facilityId = (String)requirement.get("facilityId");
        	String productStoreId = (String)requirement.get("productStoreId");
        	session.setAttribute("shoppingCart", null);
    		ShoppingCart cart = ShoppingCartEvents.getCartObject(request, locale, currencyUom);
    		try {
    			cart.setProductStoreId(productStoreId);
    			cart.setAllShipmentMethodTypeId("NO_SHIPPING");
    			cart.setCurrency(dispatcher, currencyUom);
    			cart.setUserLogin(userLogin, dispatcher);
    			cart.setFacilityId(facilityId);
    			cart.setLocale(locale);
    			cart.setPlacingCustomerPartyId((String)custParty.get("partyId"));
    			List<GenericValue> listContactMechs = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechPurposeTypeId", "SHIPPING_LOCATION")), null, listOrderBy, null, false);
    			listContactMechs = EntityUtil.filterByDate(listContactMechs);
    			if (listContactMechs.isEmpty()){
    				request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
    	                    "NoDestinationFacilityAddress", locale));
    				return "error";
    			} else {
    				GenericValue facilityContactMech = listContactMechs.get(0);
    				cart.setAllShippingContactMechId((String)facilityContactMech.get("contactMechId"));
    			}
    		} catch (CartItemModifyException e) {
    			e.printStackTrace();
    		}
    		ShoppingCartHelper helper = new ShoppingCartHelper(delegator, dispatcher, cart);
    		if (!listReqItems.isEmpty()){
        		for (GenericValue item : listReqItems){
        			BigDecimal quantity = BigDecimal.ONE;
        			GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", (String)item.get("productId")));
        			String uomBase = (String)product.get("quantityUomId");
        			quantity = (item.getBigDecimal("quantity")).multiply(getConvertNumber(delegator, quantity, (String)item.get("productId"), (String)item.get("quantityUomId"), uomBase));
        			Timestamp shipBeforeDate = (Timestamp)item.get("shipBeforeDate");
        			Timestamp shipAfterDate = (Timestamp)item.get("shipBeforeDate");
        			String catalogId = (String)item.get("prodCatalogId");
        			String productId = (String)product.get("productId");
        			String productCategoryId = (String)item.get("prodCategoryId");
        			helper.addToCart(catalogId, null, null, productId, productCategoryId, "TRANSFER_ITEM", null, null, null, quantity, null, null, null, null, null, shipBeforeDate, shipAfterDate, null, null, context, null);
        		}
    		} else {
    			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
                        "RequirementNotHasItem", locale));
        		return "error";
    		}
    		GenericValue reqFacility = delegator.findOne("RequirementFacility", false, UtilMisc.toMap("requirementId", requirementId));
    		String contactMechId = null;
    		if (reqFacility != null){
    			String facilityIdTo = (String)reqFacility.get("facilityIdTo");
    			List<GenericValue> listContactMechPurs = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityIdTo, "contactMechPurposeTypeId", "SHIPPING_LOCATION")), null, listOrderBy, null, false);
    			if (!listContactMechPurs.isEmpty()){
    				contactMechId = (String)listContactMechPurs.get(0).get("contactMechId");
    			} else {
    				request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
                            "NotFoundContactMechFacilityTo", locale));
            		return "error";
    			}
    		} else {
    			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
                        "TransferRequirementNotExists", locale));
        		return "error";
    		}
    		String[] paymentMethods = new String[1];
    		paymentMethods[0] = "EXT_OFFLINE";
    		request.setAttribute("checkOutPaymentId", paymentMethods);
            request.setAttribute("shipping_method", "Default");
            request.setAttribute("shipping_contact_mech_id", contactMechId);
            request.setAttribute("is_gift", "false");
            request.setAttribute("may_split", "false");

    		CheckOutEvents.setCheckOutOptions(request, response);
    		Map<String, Object> mapResult = createOrder(request, response);
    		String orderId = (String)mapResult.get("orderId");
    		String customerId = (String)custParty.get("partyId");
    		String orderName = (String)context.get("orderName");
    		if (orderId != null){
    			GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
    			orderHeader.put("orderTypeId", "SALES_ORDER");
    			orderHeader.put("orderName", orderName);
    			delegator.store(orderHeader);
    			GenericValue orderRole = delegator.makeValue("OrderRole");
    			GenericValue partyRole = delegator.makeValue("PartyRole");
    			partyRole.put("partyId", customerId);
    			partyRole.put("roleTypeId", "PLACING_CUSTOMER");
    			delegator.createOrStore(partyRole);
        		orderRole.put("partyId", customerId);
        		orderRole.put("orderId", orderId);
        		orderRole.put("roleTypeId", "PLACING_CUSTOMER");
        		delegator.createOrStore(orderRole);
        		partyRole.put("roleTypeId", "END_USER_CUSTOMER");
    			delegator.createOrStore(partyRole);
        		orderRole.put("roleTypeId", "END_USER_CUSTOMER");
        		delegator.createOrStore(orderRole);
        		partyRole.put("roleTypeId", "BILL_TO_CUSTOMER");
    			delegator.createOrStore(partyRole);
        		orderRole.put("roleTypeId", "BILL_TO_CUSTOMER");
        		delegator.createOrStore(orderRole);
        		partyRole.put("roleTypeId", "SHIP_TO_CUSTOMER");
    			delegator.createOrStore(partyRole);
        		orderRole.put("roleTypeId", "SHIP_TO_CUSTOMER");
        		delegator.createOrStore(orderRole);
        		orderRole.put("partyId", "company");
        		orderRole.put("roleTypeId", "BILL_FROM_VENDOR");
        		delegator.createOrStore(orderRole);
        		
        		List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
        		if (!listOrderItems.isEmpty()){
        			for (GenericValue item : listOrderItems){
        				GenericValue orderPaymentRef = delegator.makeValue("OrderPaymentPreference");
                		String orderPaymentRefId = delegator.getNextSeqId("OrderPaymentPreference");
                		orderPaymentRef.put("paymentMethodTypeId", "CASH");
                		orderPaymentRef.put("orderId", orderId);
                		orderPaymentRef.put("orderItemSeqId", (String)item.get("orderItemSeqId"));
                		orderPaymentRef.put("orderPaymentPreferenceId", orderPaymentRefId);
                		delegator.createOrStore(orderPaymentRef);
                		List<GenericValue> listOrderItemStatus = delegator.findList("OrderStatus", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_NOT_RECEIVED", "orderItemSeqId", (String)item.get("orderItemSeqId"))), null, null, null, false);
                		if (!listOrderItemStatus.isEmpty()){
                			GenericValue orderStatus = delegator.makeValue("OrderStatus");
                    		orderStatus.put("statusId", "PAYMENT_NOT_RECEIVED");
                    		orderStatus.put("orderId", orderId);
                    		orderStatus.put("orderItemSeqId", orderStatus.put("orderId", orderId));
                    		delegator.store(orderStatus);
                		}
                		GenericValue orderReqCommitment = delegator.makeValue("OrderRequirementCommitment");
        				BigDecimal quantity = item.getBigDecimal("quantity");
        				orderReqCommitment.put("requirementId", requirementId);
        				orderReqCommitment.put("orderItemSeqId", item.get("orderItemSeqId"));
        				orderReqCommitment.put("orderId", orderId);
        				orderReqCommitment.put("quantity", quantity);
        				delegator.createOrStore(orderReqCommitment);
        			}
        		}
    		}
    		requirement.put("statusId", "REQ_ORDERED");
    		delegator.store(requirement);
    	}
        return "succes";
	}
	public static String updateInternalSalesOrder(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
		HttpSession session = request.getSession();
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		String currencyUom = (String)context.get("uomId");
		Locale locale = UtilHttp.getLocale(request);
		String requirementId = (String)context.get("requirementId");
    	GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
    	List<String> listOrderBy = new ArrayList<String>();
    	listOrderBy.add("-fromDate");
    	List<GenericValue> listReqRole = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId, "roleTypeId", "CUSTOMER")), null, listOrderBy, null, false);
    	listReqRole = EntityUtil.filterByDate(listReqRole);
    	if (listReqRole.isEmpty()){
    		request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
                    "RequirementDoNotHadCustomerOrNotAvailableInThisTime", locale));
    		return "error";
    	} else {
    		GenericValue requirementRole = listReqRole.get(0);
    		GenericValue custParty = delegator.findOne("Party", false, UtilMisc.toMap("partyId", requirementRole.get("partyId")));
        	List<GenericValue> listReqItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
        	String facilityId = (String)requirement.get("facilityId");
        	String productStoreId = (String)requirement.get("productStoreId");
        	session.setAttribute("shoppingCart", null);
    		ShoppingCart cart = ShoppingCartEvents.getCartObject(request, locale, currencyUom);
    		try {
    			cart.setProductStoreId(productStoreId);
    			cart.setAllShipmentMethodTypeId("NO_SHIPPING");
    			cart.setCurrency(dispatcher, currencyUom);
    			cart.setUserLogin(userLogin, dispatcher);
    			cart.setFacilityId(facilityId);
    			cart.setLocale(locale);
    			cart.setPlacingCustomerPartyId((String)custParty.get("partyId"));
    			List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
    			Collection<GenericValue> listContacts = ContactHelper.getContactMech(custParty, "SHIPPING_LOCATION", "POSTAL_ADDRESS", false);
    			if (listContactMechs instanceof List)
    				listContactMechs = (List<GenericValue>)listContacts;
    			else
    				listContactMechs = new ArrayList<GenericValue>(listContacts);
    			Collections.sort(listContactMechs);
    			if (listContactMechs.isEmpty()){
    				request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
    	                    "NoRequestorAddress", locale));
    				return "error";
    			} else {
    				GenericValue contactMech = listContactMechs.get(0);
    				cart.setAllShippingContactMechId((String)contactMech.get("contactMechId"));
    				request.setAttribute("shipping_contact_mech_id", (String)contactMech.get("contactMechId"));
    			}
    		} catch (CartItemModifyException e) {
    			e.printStackTrace();
    		}
    		ShoppingCartHelper helper = new ShoppingCartHelper(delegator, dispatcher, cart);
    		if (!listReqItems.isEmpty()){
        		for (GenericValue item : listReqItems){
        			BigDecimal quantity = BigDecimal.ONE;
        			GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", (String)item.get("productId")));
        			String uomBase = (String)product.get("quantityUomId");
        			quantity = (item.getBigDecimal("quantity")).multiply(getConvertNumber(delegator, quantity, (String)item.get("productId"), (String)item.get("quantityUomId"), uomBase));
        			Timestamp shipBeforeDate = (Timestamp)item.get("shipBeforeDate");
        			Timestamp shipAfterDate = (Timestamp)item.get("shipBeforeDate");
        			String catalogId = (String)item.get("prodCatalogId");
        			String productId = (String)product.get("productId");
        			String productCategoryId = (String)item.get("prodCategoryId");
        			helper.addToCart(catalogId, null, null, productId, productCategoryId, "SALES_ITEM", null, null, null, quantity, null, null, null, null, null, shipBeforeDate, shipAfterDate, null, null, context, null);
        		}
    		} else {
    			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
                        "RequirementNotHasItem", locale));
        		return "error";
    		}
    		String[] paymentMethods = new String[1];
    		paymentMethods[0] = "PAY_BY_SALARY";
    		request.setAttribute("checkOutPaymentId", paymentMethods);
    		request.setAttribute("shipping_method", "Default");
         	request.setAttribute("is_gift", "false");
         	request.setAttribute("may_split", "false");
    		CheckOutEvents.setCheckOutOptions(request, response);
    		Map<String, Object> mapResult = createOrder(request, response);
    		String orderId = (String)mapResult.get("orderId");
    		String customerId = (String)custParty.get("partyId");
    		String orderName = (String)context.get("orderName");
    		if (orderId != null){
    			String salesMethodChannelEnumId = (String)context.get("salesMethodChannelEnumId");
    			GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
    			orderHeader.put("orderTypeId", "SALES_ORDER");
    			orderHeader.put("orderName", orderName);
    			orderHeader.put("salesMethodChannelEnumId", salesMethodChannelEnumId);
    			delegator.store(orderHeader);
    			GenericValue orderRole = delegator.makeValue("OrderRole");
    			GenericValue partyRole = delegator.makeValue("PartyRole");
    			partyRole.put("partyId", customerId);
    			partyRole.put("roleTypeId", "PLACING_CUSTOMER");
    			delegator.createOrStore(partyRole);
        		orderRole.put("partyId", customerId);
        		orderRole.put("orderId", orderId);
        		orderRole.put("roleTypeId", "PLACING_CUSTOMER");
        		delegator.createOrStore(orderRole);
        		partyRole.put("roleTypeId", "END_USER_CUSTOMER");
    			delegator.createOrStore(partyRole);
        		orderRole.put("roleTypeId", "END_USER_CUSTOMER");
        		delegator.createOrStore(orderRole);
        		partyRole.put("roleTypeId", "BILL_TO_CUSTOMER");
    			delegator.createOrStore(partyRole);
        		orderRole.put("roleTypeId", "BILL_TO_CUSTOMER");
        		delegator.createOrStore(orderRole);
        		partyRole.put("roleTypeId", "SHIP_TO_CUSTOMER");
    			delegator.createOrStore(partyRole);
        		orderRole.put("roleTypeId", "SHIP_TO_CUSTOMER");
        		delegator.createOrStore(orderRole);
        		orderRole.put("partyId", "company");
        		orderRole.put("roleTypeId", "BILL_FROM_VENDOR");
        		delegator.createOrStore(orderRole);
        		
        		List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
        		if (!listOrderItems.isEmpty()){
        			for (GenericValue item : listOrderItems){
        				GenericValue orderPaymentRef = delegator.makeValue("OrderPaymentPreference");
                		String orderPaymentRefId = delegator.getNextSeqId("OrderPaymentPreference");
                		orderPaymentRef.put("paymentMethodTypeId", "PAY_BY_SALARY");
                		orderPaymentRef.put("orderId", orderId);
                		orderPaymentRef.put("orderItemSeqId", (String)item.get("orderItemSeqId"));
                		orderPaymentRef.put("orderPaymentPreferenceId", orderPaymentRefId);
                		delegator.createOrStore(orderPaymentRef);
                		List<GenericValue> listOrderItemStatus = delegator.findList("OrderStatus", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "statusId", "PAYMENT_NOT_RECEIVED", "orderItemSeqId", (String)item.get("orderItemSeqId"))), null, null, null, false);
                		if (!listOrderItemStatus.isEmpty()){
                			GenericValue orderStatus = delegator.makeValue("OrderStatus");
                    		orderStatus.put("statusId", "PAYMENT_NOT_RECEIVED");
                    		orderStatus.put("orderId", orderId);
                    		orderStatus.put("orderItemSeqId", orderStatus.put("orderId", orderId));
                    		delegator.store(orderStatus);
                		}
                		GenericValue orderReqCommitment = delegator.makeValue("OrderRequirementCommitment");
        				BigDecimal quantity = item.getBigDecimal("quantity");
        				orderReqCommitment.put("requirementId", requirementId);
        				orderReqCommitment.put("orderItemSeqId", item.get("orderItemSeqId"));
        				orderReqCommitment.put("orderId", orderId);
        				orderReqCommitment.put("quantity", quantity);
        				delegator.createOrStore(orderReqCommitment);
        			}
        		}
    		}
    		requirement.put("statusId", "REQ_ORDERED");
    		delegator.store(requirement);
    	}
		return "success";
	}
	 public static Map<String, Object> createOrder(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
        Map<String, Object> callResult;

        if (UtilValidate.isEmpty(userLogin)) {
            userLogin = cart.getUserLogin();
            session.setAttribute("userLogin", userLogin);
        }
        // remove this whenever creating an order so quick reorder cache will refresh/recalc
        session.removeAttribute("_QUICK_REORDER_PRODUCTS_");

        boolean areOrderItemsExploded = CheckOutEvents.explodeOrderItems(delegator, cart);

        //get the TrackingCodeOrder List
        List<GenericValue> trackingCodeOrders = TrackingCodeEvents.makeTrackingCodeOrders(request);
        String distributorId = (String) session.getAttribute("_DISTRIBUTOR_ID_");
        String affiliateId = (String) session.getAttribute("_AFFILIATE_ID_");
        String visitId = VisitHandler.getVisitId(session);
        String webSiteId = WebSiteWorker.getWebSiteId(request);

        callResult = checkOutHelper.createOrder(userLogin, distributorId, affiliateId, trackingCodeOrders, areOrderItemsExploded, visitId, webSiteId);
        if (callResult != null) {
            ServiceUtil.getMessages(request, callResult, null);
            if (ServiceUtil.isError(callResult)) {
                // messages already setup with the getMessages call, just return the error response code
                return ServiceUtil.returnError("error");
            }
            if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
                // set the orderId for use by chained events
                String orderId = cart.getOrderId();
                request.setAttribute("orderId", orderId);
                request.setAttribute("orderAdditionalEmails", cart.getOrderAdditionalEmails());
            }
        }
        
        String issuerId = request.getParameter("issuerId");
        if (UtilValidate.isNotEmpty(issuerId)) {
            request.setAttribute("issuerId", issuerId);
        }
        return callResult;
    }
	public static Map<String, Object> getFacilities (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String productStoreId = (String)context.get("productStoreId");
		String productStoreIdTo = (String)context.get("productStoreIdTo");
		String facilityId = (String)context.get("facilityId");
		String facilityIdTo = (String)context.get("facilityIdTo");
		String contactMechPurposeTypeIdFrom = (String)context.get("contactMechPurposeTypeIdFrom");
		String contactMechPurposeTypeIdTo = (String)context.get("contactMechPurposeTypeIdTo");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listProdStoreFacilites = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId)), null, null, null, false);
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
		List<GenericValue> listFacilitiesTo = new ArrayList<GenericValue>();
		List<GenericValue> listProdStoreFacilitesTo = new ArrayList<GenericValue>();
		listProdStoreFacilites = EntityUtil.filterByDate(listProdStoreFacilites);
		if (!listProdStoreFacilites.isEmpty()){
			for (GenericValue item : listProdStoreFacilites){
				GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", (String)item.get("facilityId")));
				if (facility != null){
					listFacilities.add(facility);
				}
			}
		}
		if (productStoreIdTo != null){
			listProdStoreFacilitesTo = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreIdTo)), null, null, null, false);
			if (!listProdStoreFacilitesTo.isEmpty()){
				for (GenericValue item : listProdStoreFacilitesTo){
					GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", (String)item.get("facilityId")));
					if (facility != null){
						listFacilitiesTo.add(facility);
					}
				}
			}
			if (productStoreIdTo.equals(productStoreId)){
				if (!listFacilities.isEmpty()){
					if (facilityId != null){
						GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
						if (facility != null && listFacilities.contains(facility)){
							if (facilityIdTo != null){
								if (((String)listFacilities.get(0).get("facilityId")).equals(facilityIdTo)){
									listFacilities.remove(facility);
									listFacilities.add(facility);
								} else {
									if (listFacilitiesTo.size() > 1){
										listFacilitiesTo.remove(facility);
										listFacilitiesTo.add(facility);
									} else {
										listFacilitiesTo.remove(facility);
									}
								}
							} else {
								listFacilitiesTo.remove(facility);
							}
						} else {
							if (facilityIdTo != null){
								if (((String)listFacilities.get(0).get("facilityId")).equals(facilityIdTo)){
									if (listFacilities.size() > 1){
										GenericValue facilityTmp = listFacilities.get(0); 
										listFacilities.remove(facilityTmp);
										listFacilities.add(facilityTmp);
									} else {
										listFacilities.remove(listFacilities.get(0));
									}
								} else {
									if (listFacilitiesTo.size() > 1){
										GenericValue facilityTmp = listFacilities.get(0); 
										listFacilitiesTo.remove(facilityTmp);
										listFacilitiesTo.add(facilityTmp);
									} else {
										listFacilitiesTo.remove(listFacilities.get(0));
									}
								}
							} else {
								if (listFacilitiesTo.size() > 1){
									GenericValue facilityTmp = listFacilities.get(0); 
									listFacilitiesTo.remove(facilityTmp);
									listFacilitiesTo.add(facilityTmp);
								} else {
									listFacilitiesTo.remove(listFacilities.get(0));
								}
							}
						}
					} else {
						if (listFacilitiesTo.size() > 1){
							GenericValue facilityTmp = listFacilities.get(0); 
							listFacilitiesTo.remove(facilityTmp);
							listFacilitiesTo.add(facilityTmp);
						} else {
							listFacilitiesTo.remove(listFacilities.get(0));
						}
					}
				}
			} else {
				GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityIdTo));
				if (facility != null && listFacilitiesTo.contains(facility)){
					listFacilitiesTo.remove(facility);
					listFacilitiesTo.add(0, facility);
				}
			}
		}
		
		result.put("listFacilities", listFacilities);
		result.put("listFacilitiesTo", listFacilitiesTo);
		result.put("productStoreId", productStoreId);
		result.put("productStoreIdTo", productStoreIdTo);
		List<GenericValue> listContactMechsFrom = new ArrayList<GenericValue>();
		List<GenericValue> listContactMechsTo = new ArrayList<GenericValue>();
		if (!listFacilitiesTo.isEmpty()){
			try {
				List<GenericValue> listContactMechPurpose = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", listFacilitiesTo.get(0).get("facilityId"), "contactMechPurposeTypeId", contactMechPurposeTypeIdTo)), null, null, null, false);
				listContactMechPurpose = EntityUtil.filterByDate(listContactMechPurpose);
				if (!listContactMechPurpose.isEmpty()){
					for (GenericValue contact : listContactMechPurpose){
						List<GenericValue> listPostalAddress = delegator.findList("PostalAddress", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contact.get("contactMechId"))), null, null, null, false);
						for (GenericValue pa : listPostalAddress){
							if (!listContactMechsTo.contains(pa)){
								listContactMechsTo.add(pa);
							}
						}
					}
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		if (!listFacilities.isEmpty()){
			try {
				List<GenericValue> listContactMechPurpose = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", listFacilities.get(0).get("facilityId"), "contactMechPurposeTypeId", contactMechPurposeTypeIdFrom)), null, null, null, false);
				listContactMechPurpose = EntityUtil.filterByDate(listContactMechPurpose);
				if (!listContactMechPurpose.isEmpty()){
					for (GenericValue contact : listContactMechPurpose){
						List<GenericValue> listPostalAddress = delegator.findList("PostalAddress", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contact.get("contactMechId"))), null, null, null, false);
						for (GenericValue pa : listPostalAddress){
							if (!listContactMechsFrom.contains(pa)){
								listContactMechsFrom.add(pa);
							}
						}
					}
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		result.put("listContactMechsFrom", listContactMechsFrom);
		result.put("listContactMechsTo", listContactMechsTo);
		return result;
	}
	
	public static Map<String, Object> getFacilitiesByStore (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String productStoreId = (String)context.get("productStoreId");
		String facilityId = (String)context.get("facilityId");
		Delegator delegator = ctx.getDelegator();
		
		List<GenericValue> listProdStoreFacilites = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId)), null, null, null, false);
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
		if (!listProdStoreFacilites.isEmpty()){
			for (GenericValue item : listProdStoreFacilites){
				GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", (String)item.get("facilityId")));
				if (facility != null){
					listFacilities.add(facility);
				}
			}
		}
		if (facilityId != null){
			GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityId));
			if (listFacilities.size() >= 1){
				listFacilities.remove(facility);
				listFacilities.add(0, facility);
			}
		}
		
		result.put("listFacilities", listFacilities);
		result.put("productStoreId", productStoreId);
		return result;
	}
	public static Map<String, Object> getDiffFacilities (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String productStoreId = (String)context.get("productStoreId");
		String facilityIdTo = (String)context.get("facilityIdTo");
		Delegator delegator = ctx.getDelegator();
		String contactMechPurposeTypeIdFrom = (String)context.get("contactMechPurposeTypeIdFrom");
		String contactMechPurposeTypeIdTo = (String)context.get("contactMechPurposeTypeIdTo");
		List<GenericValue> listProdStoreFacilites = delegator.findList("ProductStoreFacility", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", productStoreId)), null, null, null, false);
		List<GenericValue> listFacilities = new ArrayList<GenericValue>();
		if (!listProdStoreFacilites.isEmpty()){
			for (GenericValue item : listProdStoreFacilites){
				GenericValue facility = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", (String)item.get("facilityId")));
				if (facility != null){
					listFacilities.add(facility);
				}
			}
		}
		GenericValue facilityTo = delegator.findOne("Facility", false, UtilMisc.toMap("facilityId", facilityIdTo));
		if (facilityTo != null){
			if (listFacilities.size() > 1){
				listFacilities.remove(facilityTo);
				listFacilities.add(facilityTo);
			} else {
				listFacilities.remove(facilityTo);
			}
		}
		List<GenericValue> listContactMechsFrom = new ArrayList<GenericValue>();
		List<GenericValue> listContactMechsTo = new ArrayList<GenericValue>();
		if (!listFacilities.isEmpty()){
			try {
				List<GenericValue> listContactMechPurpose = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", listFacilities.get(0).get("facilityId"), "contactMechPurposeTypeId", contactMechPurposeTypeIdTo)), null, null, null, false);
				listContactMechPurpose = EntityUtil.filterByDate(listContactMechPurpose);
				if (!listContactMechPurpose.isEmpty()){
					for (GenericValue contact : listContactMechPurpose){
						List<GenericValue> listPostalAddress = delegator.findList("PostalAddress", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contact.get("contactMechId"))), null, null, null, false);
						for (GenericValue pa : listPostalAddress){
							if (!listContactMechsTo.contains(pa)){
								listContactMechsTo.add(pa);
							}
						}
					}
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		try {
			List<GenericValue> listContactMechPurpose = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityIdTo, "contactMechPurposeTypeId", contactMechPurposeTypeIdFrom)), null, null, null, false);
			listContactMechPurpose = EntityUtil.filterByDate(listContactMechPurpose);
			if (!listContactMechPurpose.isEmpty()){
				for (GenericValue contact : listContactMechPurpose){
					List<GenericValue> listPostalAddress = delegator.findList("PostalAddress", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contact.get("contactMechId"))), null, null, null, false);
					for (GenericValue pa : listPostalAddress){
						if (!listContactMechsFrom.contains(pa)){
							listContactMechsFrom.add(pa);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("listContactMechsFrom", listContactMechsFrom);
		result.put("listContactMechsTo", listContactMechsTo);
		result.put("listFacilities", listFacilities);
		result.put("productStoreId", productStoreId);
		return result;
	}
	public static Map<String, Object> getCategories (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String prodCatalogId = (String)context.get("prodCatalogId");
		String productId = (String)context.get("productId");
		Delegator delegator = ctx.getDelegator();
		
		List<GenericValue> listCategories = new ArrayList<GenericValue>();
		if ("_NA_".equals(prodCatalogId) || (prodCatalogId == null)){
			GenericValue userLogin = (GenericValue)context.get("userLogin");
			String partyId = (String)userLogin.get("partyId");
			List<EntityExpr> exprList = FastList.newInstance();
			EntityExpr expr = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyId);
			exprList.add(expr);
			expr = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "MANAGER");
			exprList.add(expr);
			EntityCondition Cond = EntityCondition.makeCondition(exprList, EntityOperator.AND);
			List<String> orderBy = new ArrayList<String>();
			orderBy.add("partyId");
			List<GenericValue> listProductStoresParty = delegator.findList("ProductStoreRole", Cond, null, orderBy, null, false);
			listProductStoresParty = EntityUtil.filterByDate(listProductStoresParty);

			List<GenericValue> listProductStores = new ArrayList<GenericValue>();
			List<GenericValue> listCatalogs = new ArrayList<GenericValue>();
			if (!listProductStoresParty.isEmpty()){
				for (GenericValue item : listProductStoresParty) {
					GenericValue prStore = null;
					String prStoreId = (String)item.get("productStoreId");
					prStore = delegator.findOne("ProductStore", UtilMisc.toMap("productStoreId",prStoreId), false);
					listProductStores.add(prStore);
					List<GenericValue> listCatalogTmp = delegator.findList("ProductStoreCatalog", EntityCondition.makeCondition(UtilMisc.toMap("productStoreId", prStoreId)), null, null, null, false);
					if (!listCatalogTmp.isEmpty()){
						for (GenericValue catalog : listCatalogTmp){
							GenericValue prodCatalog = delegator.findOne("ProdCatalog", false, UtilMisc.toMap("prodCatalogId", (String)catalog.get("prodCatalogId")));
							if (!listCatalogs.contains(prodCatalog)){
								listCatalogs.add(prodCatalog);
							}
						}
					}
				}
				if (!listCatalogs.isEmpty()){
					List<GenericValue> listTmpCategories = new ArrayList<GenericValue>();
					for (GenericValue catalog : listCatalogs) {
						List<GenericValue> listCategoryTmp = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(UtilMisc.toMap("prodCatalogId", (String)catalog.get("prodCatalogId"))), null, null, null, false);
						if (!listCategoryTmp.isEmpty()){
							for (GenericValue item : listCategoryTmp){
								GenericValue category = delegator.findOne("ProductCategory", false, UtilMisc.toMap("productCategoryId", (String)item.get("productCategoryId")));
								if (!listTmpCategories.contains(category)){
									listTmpCategories.add(category);
								}
							}
						}
						if (!listTmpCategories.isEmpty()){
							for (GenericValue item : listTmpCategories){
								List<GenericValue> categoryChilds = delegator.findList("ProductCategory", EntityCondition.makeCondition(UtilMisc.toMap("primaryParentCategoryId", item.get("primaryParentCategoryId"))), null, null, null, false);
								if (!categoryChilds.isEmpty()){
									for (GenericValue child : categoryChilds){
										if (!listCategories.contains(child)){
											listCategories.add(child);
										}
									}
								}
							}
						}
					}
				}
				
			}
		} else {
			List<GenericValue> listTmpCategories = new ArrayList<GenericValue>();
			List<GenericValue> listProdCatalogCategories = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(UtilMisc.toMap("prodCatalogId", prodCatalogId)), null, null, null, false);
			if (!listProdCatalogCategories.isEmpty()){
				for (GenericValue item : listProdCatalogCategories){
					GenericValue category = delegator.findOne("ProductCategory", false, UtilMisc.toMap("productCategoryId", (String)item.get("productCategoryId")));
					if (category != null){
						listTmpCategories.add(category);
						listCategories.add(category);
					}
				}
			}
			if (!listTmpCategories.isEmpty()){
				for (GenericValue item : listTmpCategories){
					List<GenericValue> categoryChilds = delegator.findList("ProductCategory", EntityCondition.makeCondition(UtilMisc.toMap("primaryParentCategoryId", item.get("productCategoryId"))), null, null, null, false);
					if (!categoryChilds.isEmpty()){
						for (GenericValue child : categoryChilds){
							if (!listCategories.contains(child)){
								listCategories.add(child);
							}
						}
					}
				}
			}
		}
		if (productId != null && !listCategories.isEmpty()){
			List<GenericValue> listCategoriesByProduct = new ArrayList<GenericValue>();
			for (GenericValue item : listCategories){
				List<GenericValue> listProductMembers = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", item.get("productCategoryId"), "productId", productId)), null, null, null, false);
				if (!listProductMembers.isEmpty()){
					for (GenericValue prCategory : listProductMembers){
						GenericValue category = delegator.findOne("ProductCategory", false, UtilMisc.toMap("productCategoryId", (String)prCategory.get("productCategoryId")));
						if (category != null){
							listCategoriesByProduct.add(category);
						}
					}
				}
			}
			listCategories = new ArrayList<GenericValue>();
			if (!listCategoriesByProduct.isEmpty()){
				listCategories.addAll(listCategoriesByProduct);
			}
		}
		result.put("listCategories", listCategories);
		result.put("prodCatalogId", prodCatalogId);
		return result;
	}
	public static List<GenericValue> listCategoriesByParent (DispatchContext ctx, GenericValue primaryCategory, List<GenericValue> listReturns){
		List<GenericValue> listChilds = new ArrayList<GenericValue>();
		Delegator delegator = ctx.getDelegator();
		
		try {
			listChilds = delegator.findList("ProductCategory", EntityCondition.makeCondition(UtilMisc.toMap("primaryParentCategoryId", primaryCategory.get("primaryParentCategoryId"))), null, null, null, false);
			listChilds = EntityUtil.filterByDate(listChilds);
			if (!listChilds.isEmpty()){
				for (GenericValue category : listChilds){
					if (!listReturns.contains(category)){
						listReturns.add(category);
					}
					listCategoriesByParent(ctx, category, listReturns);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return listReturns;
	}
	public static Map<String, Object> addProductToRequirement(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String requirementTypeId = (String)context.get("requirementTypeId"); 
		String prodCatalogId = (String)context.get("prodCatalogId");
		String productId = (String)context.get("productId");
		String prodCategoryId = (String)context.get("prodCategoryId");
		String statusId = (String)context.get("statusId");
		BigDecimal unitCost = null;
		Timestamp receiveDate = null;
		String currencyUomId = null;
		if ("RECEIVE_PRODUCT_REQ".equals(requirementTypeId)){
			unitCost = new BigDecimal((String)context.get("unitCost"));
			receiveDate = (Timestamp)context.get("receiveDate");
			currencyUomId = (String)context.get("currencyUomId");
		}
		Timestamp expireDate = (Timestamp)context.get("expireDate");
		Timestamp shipBeforeDate = (Timestamp)context.get("shipBeforeDate");
		Timestamp shipAfterDate = (Timestamp)context.get("shipAfterDate");
		BigDecimal quantity = new BigDecimal((String)context.get("quantity"));
		if (quantity.compareTo(BigDecimal.ZERO) == 1){	
			String quantityUomId = (String)context.get("quantityUomId");
			Locale locale = (Locale)context.get("locale");
			if (quantityUomId == null){
				Map<String, Object> mapError = ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                    "NoConfigPacking", locale));
				mapError.put("requirementId", requirementId);
				return mapError;
			} else {
				Map<String, Object> requirementItem = new FastMap<String, Object>();
				requirementItem.put("requirementId", requirementId);
				requirementItem.put("prodCatalogId", prodCatalogId);
				requirementItem.put("prodCategoryId", prodCategoryId);
				requirementItem.put("productId", productId);
				requirementItem.put("expireDate", expireDate);
				requirementItem.put("shipBeforeDate", shipBeforeDate);
				requirementItem.put("shipAfterDate", shipAfterDate);
				requirementItem.put("statusId", statusId);
				if ("RECEIVE_PRODUCT_REQ".equals(requirementTypeId)){
					requirementItem.put("receiveDate", receiveDate);
					requirementItem.put("unitCost", unitCost);
					requirementItem.put("currencyUomId", currencyUomId);
				}
				List<String> orderBy = new ArrayList<String>();
				orderBy.add("-createDate");
				GenericValue product = null;
				try {
					product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
				} catch (GenericEntityException e1) {
					e1.printStackTrace();
				}
				if (product.get("quantityUomId") != null){
					try {
						List<GenericValue> listReqItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(requirementItem), null, orderBy, null, false);
						if (!listReqItems.isEmpty()){
							GenericValue firstProduct = null;
							for (GenericValue reqItem : listReqItems){
								if (((String)reqItem.get("quantityUomId")).equals(quantityUomId)){
									firstProduct = reqItem;
								}
							}
							if (firstProduct != null){
								BigDecimal oldQuantity = firstProduct.getBigDecimal("quantity");
								if (oldQuantity != null){
									BigDecimal quantityNew = oldQuantity.add(quantity);
									firstProduct.put("quantity", quantityNew);
								} else {
									BigDecimal quantityNew = quantity;
									firstProduct.put("quantity", quantityNew);
								}
								delegator.store(firstProduct);
							} else {
								List<String> listOrderBy = new ArrayList<String>();
								listOrderBy.add("-reqItemSeqId");
								List<GenericValue> listItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, listOrderBy, null, false);
								if (!listItems.isEmpty()){
									Integer nextReqItemSeqId = Integer.parseInt((String)listItems.get(0).get("reqItemSeqId")) + 1;
									String reqItemSeqId = String.format("%05d", nextReqItemSeqId);
									requirementItem.put("reqItemSeqId", reqItemSeqId);
								} else {
									String reqItemSeqId = String.format("%05d", 1);
									requirementItem.put("reqItemSeqId", reqItemSeqId);
								}
								requirementItem.put("quantity", quantity);
								requirementItem.put("quantityUomId", quantityUomId);
								Timestamp createDate = UtilDateTime.nowTimestamp();
								requirementItem.put("createDate", createDate);
								GenericValue reqItem = delegator.makeValue("RequirementItem", requirementItem);
								delegator.createOrStore(reqItem);
							}
						} else {
							GenericValue reqItem = delegator.makeValue("RequirementItem");
							List<String> listOrderBy = new ArrayList<String>();
							listOrderBy.add("-reqItemSeqId");
							List<GenericValue> listItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, listOrderBy, null, false);
							if (!listItems.isEmpty()){
								Integer nextReqItemSeqId = Integer.parseInt((String)listItems.get(0).get("reqItemSeqId")) + 1;
								String reqItemSeqId = String.format("%05d", nextReqItemSeqId);
								reqItem.put("reqItemSeqId", reqItemSeqId);
							} else {
								String reqItemSeqId = String.format("%05d", 1);
								reqItem.put("reqItemSeqId", reqItemSeqId);
							}
							Timestamp createDate = UtilDateTime.nowTimestamp();
							reqItem.put("quantity", quantity);
							reqItem.put("quantityUomId", quantityUomId);
							reqItem.put("createDate", createDate);
							reqItem.put("requirementId", requirementId);
							reqItem.put("prodCatalogId", prodCatalogId);
							reqItem.put("prodCategoryId", prodCategoryId);
							reqItem.put("productId", productId);
							reqItem.put("expireDate", expireDate);
							reqItem.put("shipBeforeDate", shipBeforeDate);
							reqItem.put("shipAfterDate", shipAfterDate);
							reqItem.put("statusId", statusId);
							if ("RECEIVE_PRODUCT_REQ".equals(requirementTypeId)){
								reqItem.put("receiveDate", receiveDate);
								reqItem.put("unitCost", unitCost);
								reqItem.put("currencyUomId", currencyUomId);
							}
							delegator.createOrStore(reqItem);
						}
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
				} else {
					Map<String, Object> mapError = ServiceUtil.returnError(UtilProperties.getMessage(resource,
		                    "NoQuantityUom", locale));
					mapError.put("requirementId", requirementId);
					return mapError;
				}
			}
		}
		result.put("requirementTypeId", requirementTypeId);
		result.put("requirementId", requirementId);
		return result;
	}
	public static BigDecimal getConvertNumber(Delegator delegator, BigDecimal convert, String productId, String uomFromId, String uomToId){
			List<String> listUomToConvert = getListUomToConvert(delegator, productId, uomFromId, uomToId);
			convert = getProductConvertNumber(delegator, convert, productId, uomFromId, uomToId, listUomToConvert);
		return convert;
	}
	
	public static Map<String, Object> deleteRequirementItems(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String reqItemSeqId = (String)context.get("reqItemSeqId");
		String requirementTypeId = null;
		try {
			GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
			GenericValue reqItem = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId));
			requirementTypeId = (String)requirement.get("requirementTypeId");
			if (reqItem != null){
				delegator.removeValue(reqItem);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("requirementTypeId", requirementTypeId);
		result.put("requirementId", requirementId);
		return result;
	}
	public static Map<String, Object> approvedOrders(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String orderId = (String)context.get("orderId");
		context.put("statusId", "ORDER_APPROVED");
		
		try {
			GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
			if (orderHeader != null){
				context.put("oldStatusId", (String)orderHeader.get("statusId"));
				OrderServices.setOrderStatus(ctx, context);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("orderId", orderId);
		return result;
	}
	public static Map<String, Object> checkQuantityUomProduct(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale)context.get("locale");
		String productId = (String)context.get("productId");
		GenericValue product = null;
		try {
			product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		String quantityUomId = (String)product.get("quantityUomId");
		if (quantityUomId != null){
			result = ServiceUtil.returnSuccess();
		} else {
			result = ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "NoQuantityUom", locale));
		}
		result.put("productId", productId);
		return result;
	}
	public static List<GenericValue> getParentConfigPacking (DispatchContext ctx, List<GenericValue> list, String productId, List<GenericValue> listUomPrs){
		Delegator delegator = ctx.getDelegator();
		for (GenericValue packing : list){
			try {
				List<GenericValue> listUomPrTmp = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomToId", packing.get("uomFromId"))), null, null, null, false);
				if (!listUomPrTmp.isEmpty()){
					getParentConfigPacking(ctx,listUomPrTmp,productId,listUomPrs);
				} else {
					GenericValue uomPr = null;
					try {
						uomPr = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", packing.get("uomFromId")));
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
					if (uomPr != null && !listUomPrs.contains(uomPr)){
						listUomPrs.add(uomPr);
					}
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		return listUomPrs;
	}
	public static Map<String, Object> receiveRequirementItems(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String reqItemSeqId = (String)context.get("reqItemSeqId");
		try {
			GenericValue item = delegator.findOne("RequirementItem", false, UtilMisc.toMap("reqItemSeqId", reqItemSeqId, "requirementId", requirementId));
			String productId = (String)item.get("productId");
			GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
			GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
			BigDecimal quantity = getConvertNumber(delegator, item.getBigDecimal("quantity"), (String)item.get("productId"), (String)item.get("quantityUomId"), (String)product.get("quantityUomId"));
			if (item != null){
				String inventoryItemId = delegator.getNextSeqId("InventoryItem");
				GenericValue invItem = delegator.makeValue("InventoryItem");
				invItem.put("inventoryItemId", inventoryItemId);
				invItem.put("productId", (String)item.get("productId"));
				invItem.put("ownerPartyId", "company");
				invItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				invItem.put("statusId", "ITEM_CREATED");
				invItem.put("datetimeReceived", UtilDateTime.nowTimestamp());
				invItem.put("expireDate", (Timestamp)item.get("expireDate"));
				invItem.put("facilityId", (String)requirement.get("facilityId"));
				invItem.put("quantityOnHandTotal", quantity);
				invItem.put("availableToPromiseTotal", quantity);
				invItem.put("unitCost", item.getBigDecimal("unitCost"));
				invItem.put("currencyUomId", (String)item.get("currencyUomId"));
				
				delegator.createOrStore(invItem);
				item.put("statusId", "REQ_ITEM_RECEIVED");
				delegator.store(item);
				
				String reqTypeId = (String)requirement.get("requirementTypeId");
				if ("RECEIVE_PRODUCT_REQ".equals(reqTypeId)){
					result.put("selectedSubMenuItem", "ReceiveByProduct");
				}
				if ("RECEIVE_ORDER_REQ".equals(reqTypeId)){
					result.put("selectedSubMenuItem", "ReceiveFromOrder");
				}
				result.put("requirementTypeId", reqTypeId);
			}
			List<GenericValue> listItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
			if (!listItems.isEmpty()){
				boolean test = false;
				for (GenericValue itemTemp : listItems){
					if (!"REQ_ITEM_RECEIVED".equals((String)itemTemp.get("statusId"))){
						test = true;
						break;
					}
				}
				if (test == false){
					requirement.put("statusId", "REQ_COMPLETED");
					delegator.store(requirement);
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("requirementId", requirementId);
		return result;
	}
	public static Map<String, Object> updateRequirementItem(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String reqItemSeqId = (String)context.get("reqItemSeqId");
		String requirementTypeId = (String)context.get("requirementTypeId");
		result.put("requirementTypeId", requirementTypeId);
		try {
			GenericValue item = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId));
			if (item != null){
				item.setNonPKFields(context);
				delegator.store(item);
				result.put("requirementId", requirementId);
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String, Object> approvesRequirementItem(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String reqItemSeqId = (String)context.get("reqItemSeqId");
		String requirementTypeId = (String)context.get("requirementTypeId");
		result.put("requirementTypeId", requirementTypeId);
		String selectedSubMenuItem = (String)context.get("selectedSubMenuItem");
		result.put("selectedSubMenuItem", selectedSubMenuItem);
		try {
			GenericValue item = delegator.findOne("RequirementItem", false, UtilMisc.toMap("requirementId", requirementId, "reqItemSeqId", reqItemSeqId));
			if (item != null){
				item.setNonPKFields(context);
				item.put("statusId", "REQ_ITEM_APPROVED");
				delegator.store(item);
				result.put("requirementId", requirementId);
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String, Object> receiveRequirements(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String facilityId = (String)context.get("facilityId");
		String requirementTypeId = (String)context.get("requirementTypeId");
		try {
			List<GenericValue> listReqItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
			if (!listReqItems.isEmpty()){
				for (GenericValue item : listReqItems){
					if ("REQ_ITEM_APPROVED".equals((String)item.get("statusId"))){
						String productId = (String)item.get("productId");
						GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
						BigDecimal quantity = getConvertNumber(delegator, item.getBigDecimal("quantity"), (String)item.get("productId"), (String)item.get("quantityUomId"), (String)product.get("quantityUomId"));
						String inventoryItemId = delegator.getNextSeqId("InventoryItem");
						GenericValue invItem = delegator.makeValue("InventoryItem");
						invItem.put("inventoryItemId", inventoryItemId);
						invItem.put("productId", (String)item.get("productId"));
						invItem.put("ownerPartyId", "company");
						invItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
						invItem.put("statusId", "ITEM_CREATED");
						invItem.put("datetimeReceived", UtilDateTime.nowTimestamp());
						invItem.put("expireDate", (Timestamp)item.get("expireDate"));
						invItem.put("facilityId", facilityId);
						invItem.put("quantityOnHandTotal", quantity);
						invItem.put("availableToPromiseTotal", quantity);
						invItem.put("unitCost", item.getBigDecimal("unitCost"));
						invItem.put("currencyUomId", (String)item.get("currencyUomId"));
						
						delegator.createOrStore(invItem);
						item.put("statusId", "REQ_ITEM_RECEIVED");
						delegator.store(item);
						
						String reqTypeId = requirementTypeId;
						if ("RECEIVE_PRODUCT_REQ".equals(reqTypeId)){
							result.put("selectedSubMenuItem", "ReceiveByProduct");
						}
						if ("RECEIVE_ORDER_REQ".equals(reqTypeId)){
							result.put("selectedSubMenuItem", "ReceiveFromOrder");
						}
						result.put("requirementTypeId", reqTypeId);
					}
				}
			}
			List<GenericValue> listItems = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
			if (!listItems.isEmpty()){
				boolean test = false;
				for (GenericValue itemTemp : listItems){
					if (!"REQ_ITEM_RECEIVED".equals((String)itemTemp.get("statusId"))){
						test = true;
						break;
					}
				}
				if (test == false){
					delegator.storeByCondition("Requirement", UtilMisc.toMap("statusId", "REQ_COMPLETED"), EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)));
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String, Object> updateReceiveRequirementsFromPO(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		if (requirementId == null){
			requirementId = delegator.getNextSeqId("Requirement");
		}
		String orderId = (String)context.get("orderId");
		String requestor = (String)context.get("requestor");
		String requirementTypeId = (String)context.get("requirementTypeId");
		String selectedSubMenuItem = (String)context.get("selectedSubMenuItem");
		String productStoreId = (String)context.get("productStoreId");
		String facilityId = (String)context.get("facilityId");
		Timestamp requiredByDate = null;
		if (context.get("requiredByDate") != null){
			requiredByDate = (Timestamp)context.get("requiredByDate");
		} else {
			requiredByDate = UtilDateTime.nowTimestamp();
		}
		Timestamp requirementStartDate = (Timestamp)context.get("requirementStartDate");
		String statusId = (String)context.get("statusId");
		String description = (String)context.get("description");
		result.put("requirementId", requirementId);
		result.put("requirementTypeId", requirementTypeId);
		result.put("orderId", orderId);
		result.put("selectedSubMenuItem", selectedSubMenuItem);
		
		GenericValue requirement = delegator.makeValue("Requirement");
		requirement.put("requiredByDate", requiredByDate);
		requirement.put("requirementId", requirementId);
		requirement.put("requirementTypeId", requirementTypeId);
		requirement.put("productStoreId", productStoreId);
		requirement.put("facilityId", facilityId);
		requirement.put("statusId", statusId);
		requirement.put("description", description);
		requirement.put("requirementStartDate", requirementStartDate);
		try {
			delegator.createOrStore(requirement);
			List<String> listOrderBy = new ArrayList<String>();
			listOrderBy.add("-fromDate");
			List<GenericValue> requirementRoles = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("partyId", requestor, "requirementId", requirementId, "roleTypeId", "OWNER")), null, listOrderBy, null, false);
			if (requirementRoles.isEmpty()){
				GenericValue partyRole = delegator.makeValue("PartyRole");
				partyRole.put("partyId", requestor);
				partyRole.put("roleTypeId", "OWNER");
				delegator.createOrStore(partyRole);
				GenericValue requirementRole = delegator.makeValue("RequirementRole");
				requirementRole.put("partyId", requestor);
				requirementRole.put("requirementId", requirementId);
				requirementRole.put("roleTypeId", "OWNER");
				requirementRole.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.createOrStore(requirementRole);
			}
		} catch (GenericEntityException e1) {
			Debug.logError(e1, module);
			return ServiceUtil.returnError(e1.getMessage());
		}
		
		GenericValue OrderReq = delegator.makeValue("OrderRequirement");
		OrderReq.put("orderId", orderId);
		OrderReq.put("requirementId", requirementId);
		
		GenericValue orderHeader;
		try {
			orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
			String originFacilityId = (String)orderHeader.get("originFacilityId");
			String orderStoreId = (String)orderHeader.get("productStoreId");
			if (originFacilityId == null && facilityId != null){
				orderHeader.put("originFacilityId", facilityId);
			}
			if (orderStoreId == null && productStoreId != null){
				orderHeader.put("productStoreId", productStoreId);
			}
			delegator.store(orderHeader);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		try {
			delegator.createOrStore(OrderReq);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	public static Map<String,Object> quickReceiveRequirementFromPO(DispatchContext ctx, Map<String,Object> context){
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String orderId = (String)context.get("orderId");
		String facilityId = (String)context.get("facilityId");
		String selectedSubMenuItem = (String)context.get("selectedSubMenuItem");
		try {
			GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
			String currencyUomId = (String)orderHeader.get("currencyUom");
			List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
			if (!orderItems.isEmpty()){
				for (GenericValue item : orderItems){
					if ("ITEM_APPROVED".equals((String)item.get("statusId"))){
						BigDecimal quantity = item.getBigDecimal("quantity");
						String inventoryItemId = delegator.getNextSeqId("InventoryItem");
						GenericValue invItem = delegator.makeValue("InventoryItem");
						invItem.put("inventoryItemId", inventoryItemId);
						invItem.put("productId", (String)item.get("productId"));
						invItem.put("ownerPartyId", "company");
						invItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
						invItem.put("statusId", "ITEM_CREATED");
						invItem.put("datetimeReceived", UtilDateTime.nowTimestamp());
						invItem.put("expireDate", (Timestamp)item.get("expireDate"));
						invItem.put("facilityId", facilityId);
						invItem.put("quantityOnHandTotal", quantity);
						invItem.put("availableToPromiseTotal", quantity);
						invItem.put("unitCost", item.getBigDecimal("unitPrice"));
						invItem.put("currencyUomId", currencyUomId);
						delegator.createOrStore(invItem);
						item.put("statusId", "ITEM_COMPLETED");
						delegator.store(item);
					}
				}
			}
			orderItems = new ArrayList<GenericValue>();
			orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
			if (!orderItems.isEmpty()){
				boolean test = false;
				for (GenericValue item : orderItems){
					if (!"ITEM_COMPLETED".equals((String)item.get("statusId")) && !"ITEM_REJECTED".equals((String)item.get("statusId"))){
						test = true;
						break;
					}
				}
				if (!test){
					GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
					requirement.put("statusId", "REQ_COMPLETED");
					delegator.store(requirement);
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("selectedSubMenuItem", selectedSubMenuItem);
		
		return result;
	}
	
	public static Map<String,Object> updateReceiptPORequirementStatus(DispatchContext ctx, Map<String,Object> context){
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		result.put("requirementId", requirementId);
		List<GenericValue> orderByRequirements;
		try {
			orderByRequirements = delegator.findList("OrderRequirement", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
			if (!orderByRequirements.isEmpty()){
				List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderByRequirements.get(0).get("orderId"))), null, null, null, false);
				if (requirementId != null){
					if (!orderItems.isEmpty()){
						boolean test = false;
						for (GenericValue item : orderItems){
							if (!"ITEM_COMPLETED".equals((String)item.get("statusId")) && !"ITEM_REJECTED".equals((String)item.get("statusId"))){
								test = true;
								break;
							}
						}
						if (!test){
							GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
							requirement.put("statusId", "REQ_COMPLETED");
							delegator.store(requirement);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Map<String,Object> addInventoryItem(DispatchContext ctx, Map<String,Object> context){
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String partyId = (String)userLogin.get("partyId");
		String orderId = (String)context.get("orderId");
		String orderItemSeqId = (String)context.get("orderItemSeqId");
		String facilityId = (String)context.get("facilityId");
		String selectedSubMenuItem = (String)context.get("selectedSubMenuItem");
		String productId = (String)context.get("productId");
		String requirementId = (String)context.get("requirementId");
		String shipmentId = (String)context.get("shipmentId");
		String rejectionId = (String)context.get("rejectionId");
		Timestamp expireDate = (Timestamp)context.get("expireDate");
		Timestamp fromDate = UtilDateTime.nowTimestamp();
		String lotId = (String)context.get("lotId");
		String newLotId = (String)context.get("newLotId");
		String containerId = (String)context.get("containerId");
		String unitCost = (String)context.get("unitPrice");
		try {
			if (lotId != null){
				GenericValue lot = delegator.findOne("Lot", false, UtilMisc.toMap("lotId", lotId));
				if (lot == null){
					lot = delegator.makeValue("Lot");
					lot.put("lotId", lotId);
					lot.put("creationDate", fromDate);
					delegator.create(lot);
				} 			
			} else {
				if (newLotId != null) {
					lotId = newLotId;
				} else {
					lotId = delegator.getNextSeqId("Lot");
				}
				GenericValue lot = delegator.makeValue("Lot");
				lot.put("lotId", lotId);
				lot.put("creationDate", fromDate);
				delegator.create(lot);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		GenericValue lotPO = delegator.makeValue("PurchaseOrderAndLot");
		lotPO.put("orderId", orderId);
		lotPO.put("lotId", lotId);
		lotPO.put("fromDate", fromDate);
		try {
			delegator.createOrStore(lotPO);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		if (unitCost.contains(",")){
			unitCost = unitCost.replace(",", ".");
		}
		BigDecimal quantityReceived = new BigDecimal((String)context.get("quantityReceived"));
		BigDecimal quantityRejected = new BigDecimal((String)context.get("quantityRejected"));
		BigDecimal quantityWillByRejected = new BigDecimal((String)context.get("quantityWillByRejected"));
		BigDecimal quantityWillBeReceived = new BigDecimal((String)context.get("quantityWillBeReceived"));
		BigDecimal quantityOrdered = new BigDecimal((String)context.get("quantityOrdered"));
		BigDecimal quantityReceivedTotal = quantityReceived.add(quantityRejected);
		int res = quantityOrdered.subtract(quantityReceivedTotal).compareTo(quantityWillBeReceived.add(quantityWillByRejected)); 
		if (res == -1){
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "CannotReceiveQuantityGreaterThanQuantityOrdered", (Locale)context.get("locale")));
		} else {
			GenericValue orderHeader = null;
			GenericValue orderItem = null;
			try {
				orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
				
				orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
				String inventoryItemId = delegator.getNextSeqId("InventoryItem");
				GenericValue invItem = delegator.makeValue("InventoryItem");
				invItem.put("inventoryItemId", inventoryItemId);
				invItem.put("productId", productId);
				invItem.put("ownerPartyId", "company");
				invItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				invItem.put("statusId", "ITEM_CREATED");
				invItem.put("datetimeReceived", UtilDateTime.nowTimestamp());
				invItem.put("expireDate", expireDate);
				invItem.put("facilityId", facilityId);
				invItem.put("containerId", containerId);
				invItem.put("lotId", lotId);
				invItem.put("quantityOnHandTotal", quantityWillBeReceived);
				invItem.put("availableToPromiseTotal", quantityWillBeReceived);
				invItem.put("unitCost", new BigDecimal(unitCost));
				invItem.put("currencyUomId", orderHeader.get("currencyUom"));
				delegator.createOrStore(invItem);
				
				GenericValue shipmentReceipt = delegator.makeValue("ShipmentReceipt");
				String receiptId = delegator.getNextSeqId("ShipmentReceipt");
				shipmentReceipt.put("receiptId", receiptId);
				shipmentReceipt.put("inventoryItemId", inventoryItemId);
				shipmentReceipt.put("productId", productId);
				shipmentReceipt.put("orderId", orderId);
				shipmentReceipt.put("orderItemSeqId", orderItemSeqId);
				shipmentReceipt.put("quantityAccepted", quantityWillBeReceived);
				shipmentReceipt.put("quantityRejected", quantityWillByRejected);
				shipmentReceipt.put("receivedByUserLoginId", partyId);
				shipmentReceipt.put("datetimeReceived", UtilDateTime.nowTimestamp());
				shipmentReceipt.put("rejectionId", rejectionId);
				delegator.createOrStore(shipmentReceipt);
				
				quantityReceivedTotal = quantityReceivedTotal.add(quantityWillBeReceived.add(quantityWillByRejected));
				if (quantityOrdered.subtract(quantityReceivedTotal).equals(BigDecimal.ZERO)){
					orderItem.put("statusId", "ITEM_COMPLETED");
					delegator.store(orderItem);
				}
				
				List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
				if (requirementId != null){
					if (!orderItems.isEmpty()){
						boolean test = false;
						for (GenericValue item : orderItems){
							if (!"ITEM_COMPLETED".equals((String)item.get("statusId")) && !"ITEM_REJECTED".equals((String)item.get("statusId"))){
								test = true;
								break;
							}
						}
						if (!test){
							GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
							requirement.put("statusId", "REQ_COMPLETED");
							delegator.store(requirement);
						}
					}
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
			result.put("orderId", orderId);
			result.put("requirementId", requirementId);
			result.put("shipmentId", shipmentId);
			result.put("facilityId", facilityId);
			result.put("selectedSubMenuItem", selectedSubMenuItem);
		}
		return result;
	}
	public static Map<String,Object> updateFacilityGroup(DispatchContext ctx, Map<String,Object> context){
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String facilityGroupId = (String)context.get("facilityGroupId");
		if (facilityGroupId == null){
			facilityGroupId = delegator.getNextSeqId("FacilityGroup");
		}
		String facilityGroupName = (String)context.get("facilityGroupName");
		String facilityGroupTypeId = (String)context.get("facilityGroupTypeId");
		String description = (String)context.get("description");
		
		GenericValue facilityGroup = delegator.makeValue("FacilityGroup");
		facilityGroup.put("facilityGroupId", facilityGroupId);
		facilityGroup.put("facilityGroupName", facilityGroupName);
		facilityGroup.put("facilityGroupTypeId", facilityGroupTypeId);
		facilityGroup.put("description", description);
		
		try {
			delegator.createOrStore(facilityGroup);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		return result;
	}
	
	public static Map<String,Object> updateFacility(DispatchContext ctx, Map<String,Object> context){
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispathcher = ctx.getDispatcher();
		String facilityId = (String)context.get("facilityId");
		String managerPartyId = (String)context.get("managerPartyId");
		
		if (facilityId == null){
			Map<String, Object> resultTemp = new FastMap<String, Object>();
			try {
				resultTemp = dispathcher.runSync("createFacility", context);
				facilityId = (String)resultTemp.get("facilityId"); 
			} catch (GenericServiceException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		} else {
			try {
				dispathcher.runSync("updateFacility", context);
			} catch (GenericServiceException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		String facilityGroupId = (String)context.get("facilityGroupId");
		
		
		if (facilityGroupId != null){
			GenericValue facilityGroup = delegator.makeValue("FacilityGroupMember");
			facilityGroup.put("facilityId", facilityId);
			facilityGroup.put("facilityGroupId", facilityGroupId);
			facilityGroup.put("fromDate", UtilDateTime.nowTimestamp());
			try {
				delegator.createOrStore(facilityGroup);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		if (managerPartyId != null){
			Timestamp fromDate = null;
			Timestamp thruDate = null;
			
			if ((Timestamp)context.get("fromDate") != null){
				fromDate = (Timestamp)context.get("fromDate");
			} else {
				fromDate = UtilDateTime.nowTimestamp();
			}
			if ((Timestamp)context.get("thruDate") != null){
				thruDate = (Timestamp)context.get("thruDate");
			}
			GenericValue facilityRole = delegator.makeValue("FacilityParty");
			facilityRole.put("facilityId", facilityId);
			facilityRole.put("roleTypeId", "MANAGER");
			facilityRole.put("partyId", managerPartyId);
			facilityRole.put("fromDate", fromDate);
			facilityRole.put("thruDate", thruDate);
			try {
				delegator.createOrStore(facilityRole);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		result.put("facilityId", facilityId);
		return result;
	}
	public static Map<String, Object> getFacilityContactMechs(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityId = (String)context.get("facilityId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
		if (facilityId != null && !"".equals(facilityId)){
			try {
				List<GenericValue> listFacilityContactMech = delegator.findList("FacilityContactMech", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
				listFacilityContactMech = EntityUtil.filterByDate(listFacilityContactMech);
				if (!listFacilityContactMech.isEmpty()){
					for (GenericValue ctm : listFacilityContactMech){
						List<GenericValue> listContactMechPurpose = delegator.findList("FacilityContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "contactMechPurposeTypeId", contactMechPurposeTypeId, "contactMechId", (String)ctm.get("contactMechId"))), null, null, null, false);
						listContactMechPurpose = EntityUtil.filterByDate(listContactMechPurpose);
						if (!listContactMechPurpose.isEmpty()){
							for (GenericValue contact : listContactMechPurpose){
								List<GenericValue> listPostalAddress = delegator.findList("PostalAddress", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contact.get("contactMechId"))), null, null, null, false);
								for (GenericValue pa : listPostalAddress){
									if (!listContactMechs.contains(pa)){
										listContactMechs.add(pa);
									}
								}
							}
						}
					}
				}
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
		}
		result.put("facilityId", facilityId);
		result.put("listFacilityContactMechs", listContactMechs);
		return result;
	}
	public static Map<String, Object> getVehicles(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		String shipmentMethodTypeId = (String)context.get("shipmentMethodTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listVehicles = new ArrayList<GenericValue>();
		try {
			listVehicles = delegator.findList("Vehicle", EntityCondition.makeCondition(UtilMisc.toMap("shipmentMethodTypeId", shipmentMethodTypeId)), null, null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("listVehicles", listVehicles);
		return result;
	}
	public static Map<String, Object> getTotalShipmentItem(DispatchContext ctx, Map<String, Object> context) {
		String shipmentId = (String)context.get("shipmentId");
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		GenericValue shipment;
		try {
			shipment = delegator.findOne("Shipment", false, UtilMisc.toMap("shipmentId", shipmentId));
			BigDecimal totalWeight = BigDecimal.ZERO;
			String weightUomDefault = (String)shipment.get("defaultWeightUomId");
			if (weightUomDefault == null){
				shipment.put("defaultWeightUomId", "WT_kg");
				delegator.store(shipment);
			}
			List<GenericValue> listShipmentItems = delegator.findList("ShipmentItem", EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipment.get("shipmentId"))), null, null, null, false);
			if (!listShipmentItems.isEmpty()){
				for (GenericValue item : listShipmentItems){
					Double convertNumber = 1.0;
					BigDecimal quantity = BigDecimal.ZERO;
					BigDecimal weight = BigDecimal.ZERO;
					GenericValue product = item.getRelatedOne("Product", false);
					if (item.getBigDecimal("quantity") != null) {
						quantity = item.getBigDecimal("quantity");
					}
					if (product.getBigDecimal("weight") != null) {
						weight = product.getBigDecimal("weight").multiply(quantity);
					}
					List<String> listUom = new ArrayList<String>();
					List<String> listResult = new ArrayList<String>();
					listUom = getConvertUom(delegator, listUom, (String)shipment.get("defaultWeightUomId"), (String)product.get("weightUomId"));
					if (listUom.contains(product.get("weightUomId"))){
						listUom.remove(product.get("weightUomId"));
					}
					if (listUom.contains(shipment.get("defaultWeightUomId"))){
						listUom.remove(shipment.get("defaultWeightUomId"));
					}
					listResult = getListUom(delegator, listUom, listResult, (String)shipment.get("defaultWeightUomId"));
					if (!listResult.contains(shipment.get("defaultWeightUomId"))){
						listResult.add((String)shipment.get("defaultWeightUomId"));
					}
					convertNumber = getConvertNumber(delegator, convertNumber, listResult, (String)shipment.get("defaultWeightUomId"), (String)product.get("weightUomId"));
					BigDecimal convert = new BigDecimal(convertNumber);
					if ((convert.compareTo(BigDecimal.ONE) == 0) && (!shipment.get("defaultWeightUomId").equals(product.get("weightUomId")))){
						listUom = new ArrayList<String>();
						listResult = new ArrayList<String>();
						listUom = getConvertUom(delegator, listUom, (String)product.get("weightUomId"), (String)shipment.get("defaultWeightUomId"));
						listResult = getListUom(delegator, listUom, listResult, (String)product.get("weightUomId"));
						convertNumber = getConvertNumber(delegator, convertNumber, listResult, (String)product.get("weightUomId"), (String)shipment.get("defaultWeightUomId"));
						BigDecimal convert2 = new BigDecimal(convertNumber);
						BigDecimal itemWeight = weight.multiply(convert2);
						totalWeight = totalWeight.add(itemWeight);
					} else {
						if (!shipment.get("defaultWeightUomId").equals(product.get("weightUomId"))){
							BigDecimal itemWeight = weight.divide(convert, 0, RoundingMode.HALF_UP);
							totalWeight = totalWeight.add(itemWeight);
						} else {
							BigDecimal itemWeight = weight.multiply(convert);
							totalWeight = totalWeight.add(itemWeight);
						} 
					}
				}
			}
			result.put("totalWeight", totalWeight);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Double getConvertNumber(Delegator delegator, Double convert, List<String> listUoms, String uomId, String uomIdTo){
		try {
			if (!listUoms.isEmpty()){
				List<GenericValue> listUomPrs = new ArrayList<GenericValue>();
				List<GenericValue> listConfigs = delegator.findList("UomConversion", EntityCondition.makeCondition(UtilMisc.toMap("uomIdTo", uomIdTo)), null, null, null, false);
				for (GenericValue uomItem : listConfigs){
					if (listUoms.contains((String)uomItem.get("uomId"))){
						listUomPrs.add(uomItem);
					}
				}
				if (!listUomPrs.isEmpty()){
					if (uomId.equals((String)listUomPrs.get(0).get("uomId"))){
						convert = convert * listUomPrs.get(0).getDouble("conversionFactor");
					} else {
						convert = convert * listUomPrs.get(0).getDouble("conversionFactor");
						listUoms.removeAll(listUomPrs);
						convert = getConvertNumber(delegator, convert, listUoms, uomId,(String)listUomPrs.get(0).get("uomId"));
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return convert;
	}

	public static List<String> getListUom(Delegator delegator, List<String> listUom, List<String> listUomResult, String uomId){
		List<String> listPr = new ArrayList<String>();
		if (!listUom.isEmpty()){
			for (String uomIdTo : listUom){
				GenericValue uomConvert;
				try {
					uomConvert = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", uomId, "uomIdTo", uomIdTo));
					if (uomConvert != null){
						listPr.add(uomIdTo);
					}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
			listUomResult.addAll(listPr);
			listUom.removeAll(listPr);
		}
		if (!listPr.isEmpty() && !listUom.isEmpty()){
			for (String uomIdTo : listPr){
				getListUom(delegator, listUom, listUomResult, uomIdTo);
			}
		}
		return listUomResult;
	}
	public static List<String> getConvertUom(Delegator delegator, List<String> listReturn, String uomId, String uomIdTo){
		try {
			if (uomId != null){
				if (!uomId.equals(uomIdTo)){
					GenericValue uomConvert = delegator.findOne("UomConversion", false, UtilMisc.toMap("uomId", uomId, "uomIdTo", uomIdTo));
					if (uomConvert != null){
					} else {
						List<GenericValue> listConvers = delegator.findList("UomConversion", EntityCondition.makeCondition(UtilMisc.toMap("uomIdTo", uomIdTo)), null, null, null, false);
						List<String> listUomId = new ArrayList<String>();
						if (!listConvers.isEmpty()){
							for (GenericValue tmp : listConvers){
								if (!listReturn.contains(tmp.get("uomId"))){
									listReturn.add((String)tmp.get("uomId"));
									listUomId.add((String)tmp.get("uomId"));
								}
							}
						}
						if (!listUomId.isEmpty()){
							for (String id : listUomId){
								getConvertUom(delegator, listReturn, uomId, id);
							}
						}
					}
				} else {
					listReturn.add(uomId);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return listReturn;
	}
	public static Map<String, Object> addShipmentMethodToContract(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contractId = (String)context.get("contractId");
		String partyGroupId = (String)context.get("carrierPartyId");
		String shipmentMethodTypeId = (String)context.get("shipmentMethodTypeId");
		
		GenericValue carrierShipment = delegator.makeValue("CarrierShipmentMethod");
		carrierShipment.put("partyId", partyGroupId);
		carrierShipment.put("shipmentMethodTypeId", shipmentMethodTypeId);
		carrierShipment.put("roleTypeId", "CARRIER");
		
		delegator.createOrStore(carrierShipment);
		
		result.put("contractId", contractId);
		return result;
	}
	public static Map<String, Object> deleteShipmentMethodFromContract(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String contractId = (String)context.get("contractId");
		String partyGroupId = (String)context.get("carrierPartyId");
		String shipmentMethodTypeId = (String)context.get("shipmentMethodTypeId");
		delegator.removeByCondition("CarrierShipmentMethod", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyGroupId, "shipmentMethodTypeId", shipmentMethodTypeId, "roleTypeId", "CARRIER")));
		result.put("contractId", contractId);
		return result;
	}
	
	
	// function of physical data
	public static Map<String, Object> createPhysicalVariances1(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		
		Map<String, Object> result = new FastMap<String, Object>();
		String facilityId = (String)context.get("facilityId");
		String temp = (String)context.get("quantityReason");
		GenericValue userlogin = (GenericValue)context.get("userLogin");
		String inventoryItemId = (String)context.get("inventoryItemId");
		String partyId = (String)userlogin.get("partyId");
		Timestamp dateCheck = (Timestamp)context.get("dateCheck");
		String personCheck = (String)context.get("personCheck");
		Timestamp currentDate = UtilDateTime.nowTimestamp();
		BigDecimal quantityReason = new BigDecimal(temp);
		String physicalInvId = (String)context.get("physicalInventoryId");
		//String physicalInvId = delegator.getNextSeqId("PhysicalInventory");
		String variance = (String)context.get("varianceReasonId");
		
		//create data physical Inventory
		
		GenericValue createPhysInv = delegator.makeValue("PhysicalInventory");
		createPhysInv.put("physicalInventoryId", physicalInvId);
		if(dateCheck != null){
		createPhysInv.put("physicalInventoryDate", dateCheck);
		}else {
			createPhysInv.put("physicalInventoryDate", currentDate);
		}
		
		if(personCheck != null){
		createPhysInv.put("partyId", personCheck);
		}else {
			createPhysInv.put("partyId", partyId);
		}
		try{
			delegator.createOrStore(createPhysInv);
		}catch(Exception e){}
		
		//update InventoryItem and inventory item variance
//		List<GenericValue> listInventoryItem = delegator.findList("InventoryItem", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "productId", productId, "expireDate", expireDate)), null, null, null, false);
		// check error's user if exist update db
//		for(GenericValue item : listInventoryItem){
//			String itemId = (String)item.get("inventoryItemId");
//			GenericValue InvItemVar = delegator.findOne("InventoryItemVariance",UtilMisc.toMap("inventoryItemId", inventoryItemId,"physicalInventoryId",physicalInvId), false);
////			GenericValue InvItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
//			BigDecimal quantityVar = BigDecimal.ZERO;
//			
//			if(InvItemVar != null){
//			quantityVar = InvItemVar.getBigDecimal("quantityOnHandVar");
//			//delete this physical inventory item id if exitst because user not remember this inventoryItemId is checked
//			try{
//				delegator.removeValue(InvItemVar);
//			}catch(Exception e){}
//			//store
////			BigDecimal ATP = InvItem.getBigDecimal("availableToPromiseTotal");
////			BigDecimal QOH = InvItem.getBigDecimal("quantityOnHandTotal");
////			BigDecimal QOHUpdate = QOH.add(quantityVar);
////			BigDecimal ATPUpdate = ATP.add(quantityVar);
////			InvItem.put("availableToPromiseTotal", ATPUpdate);
////			InvItem.put("quantityOnHandTotal", QOHUpdate);
////			//store InventoryItem
////			try{
////				delegator.store(InvItem);
////				
////			}catch(Exception e){}
//			}
			//end if
//		}
		//main
//			String itemId = (String)item.get("inventoryItemId");
			
			// Update InventoryItem with QOH, ATP
//			GenericValue InvItem2 = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
//
//			BigDecimal quantityAtp = InvItem2.getBigDecimal("availableToPromiseTotal");
//			BigDecimal quantityQoh = InvItem2.getBigDecimal("quantityOnHandTotal");
//
//				BigDecimal quantityAtpUpdate = quantityAtp.subtract(quantityReason);
//				BigDecimal quantityQohUpdate = quantityQoh.subtract(quantityReason);
//
//				InvItem2.put("availableToPromiseTotal", quantityAtpUpdate);
//				InvItem2.put("quantityOnHandTotal", quantityQohUpdate);
//
//				// update inventory item
//				try{
//				delegator.store(InvItem2);
//				}catch(Exception e){}
				
				//create inventory item variancee
				
				GenericValue createVari = delegator.makeValue("InventoryItemVariance");
				
				createVari.put("inventoryItemId", inventoryItemId);
				createVari.put("physicalInventoryId", physicalInvId);
				createVari.put("varianceReasonId", variance);
				createVari.put("quantityOnHandVar", quantityReason);
				try{
					delegator.createOrStore(createVari);
					
				}catch(Exception e){}
		result.put("facilityId", facilityId);
		result.put("physicalInventoryId", physicalInvId);
		
//		return ServiceUtil.returnSuccess();
		return result;
}
	
	public static Map<String, Object> createPhysicalInvId(DispatchContext dpx, Map<String, ?extends Object> context){
			Delegator delegator = dpx.getDelegator();
			Map<String, Object> result = new FastMap<String, Object>();
			String physicalInventoryId = delegator.getNextSeqId("PhysicalInventory");
			result.put("physicalInventoryId", physicalInventoryId);
			return result;
	}
	public static Map<String, Object> deleteKiem(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String physicalInventoryId = (String)context.get("physicalInventoryId");
		String facilityId = (String)context.get("facilityId");
		String productId = (String)context.get("productId");
//		String temp = (String)context.get("QOHV");
		Timestamp expireDate = (Timestamp)context.get("expireDate");
//		BigDecimal QOHV = new BigDecimal(temp);
		
		List<GenericValue> listInventoryItem = delegator.findList("InventoryItem", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "productId", productId, "expireDate", expireDate)), null, null, null, false);
		
		for(GenericValue item : listInventoryItem){
			String itemId = (String)item.get("inventoryItemId");
//		String inventoryItemId = (String)context.get("inventoryItemId");
//		BigDecimal QOHV = new BigDecimal(temp);
			GenericValue InvItemVar = delegator.findOne("InventoryItemVariance",UtilMisc.toMap("inventoryItemId", itemId,"physicalInventoryId",physicalInventoryId), false);
			if(InvItemVar != null){
			//delete
			try{
				delegator.removeValue(InvItemVar);
			}catch(Exception e){}
			}
		}
		result.put("physicalInventoryId", physicalInventoryId);
		result.put("facilityId", facilityId);
		return result;
		
}
	
	
	public static Map<String, Object> EditPhysicalInv(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String physicalInventoryId = (String)context.get("physicalInventoryId");
		String facilityId = (String)context.get("facilityId");
		String temp = (String)context.get("QOHV");
		String variance = (String)context.get("varianceReasonId");
		String inventoryItemId = (String)context.get("inventoryItemId");
		BigDecimal quantityReason = new BigDecimal(temp);
		
				GenericValue createVari = delegator.makeValue("InventoryItemVariance");
				
				createVari.put("inventoryItemId", inventoryItemId);
				createVari.put("physicalInventoryId", physicalInventoryId);
				createVari.put("varianceReasonId", variance);
				createVari.put("quantityOnHandVar", quantityReason);
				try{
					delegator.createOrStore(createVari);
					
				}catch(Exception e){}
		result.put("facilityId", facilityId);
		result.put("physicalInventoryId", physicalInventoryId);
		
//		return ServiceUtil.returnSuccess();
		return result;
	}
	
	public static Map<String, Object> deletePhysFacility(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String physicalInventoryId = (String)context.get("physicalInventoryId");
		String facilityId = (String)context.get("facilityId");
		List<GenericValue> listInventoryItemVar = delegator.findList("InventoryItemVariance", EntityCondition.makeCondition(UtilMisc.toMap("physicalInventoryId", physicalInventoryId)), null, null, null, false);
		
		for(GenericValue item : listInventoryItemVar){
			//delete
			try{
				delegator.removeValue(item);
			}catch(Exception e){}
		}
		GenericValue Phys = delegator.findOne("PhysicalInventory",UtilMisc.toMap("physicalInventoryId", physicalInventoryId), false);
		
		try{
			delegator.removeValue(Phys);
		}catch(Exception e){}
		result.put("physicalInventoryId", physicalInventoryId);
		result.put("facilityId", facilityId);
		return result;
}
	
	public static Map<String, Object> createOrderHeaderId(DispatchContext dpx, Map<String, ?extends Object> context){
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String orderId = delegator.getNextSeqId("OrderHeader");
		result.put("orderId", orderId);
		return result;
}
	public static Map<String, Object> LapHoaDonHangHuy(DispatchContext dpx, Map<String,?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		
		Map<String, Object> result = new FastMap<String, Object>();
		String orderId =(String)context.get("orderId");
		String orderName =(String)context.get("orderName");
		String productStoreId = (String)context.get("productStoreId");
		String facilityId = (String)context.get("facilityId");
		String productId = (String)context.get("productId");
		Timestamp orderDate = (Timestamp)context.get("orderDate");
		String orderTypeId = (String)context.get("orderTypeId");
		String inventoryItemId = (String)context.get("inventoryItemId");
//		String physicalInvId = (String)context.get("physicalInventoryId");
		String temp = (String)context.get("quantityDestroy");
		BigDecimal quantityDestroy = new BigDecimal(temp);
		
//		String varianceReasonId =(String)context.get("varianceReasonId");
		String orderItemSeqId = delegator.getNextSeqId("OrderItem");
//		GenericValue userlogin = (GenericValue)context.get("userLogin");
//		String partyId = (String)userlogin.get("partyId");
//		Timestamp dateCheck = (Timestamp)context.get("dateCheck");
//		String personCheck = (String)context.get("personCheck");
//		Timestamp currentDate = UtilDateTime.nowTimestamp();
		
		//String physicalInvId = delegator.getNextSeqId("PhysicalInventory");
//		String variance = (String)context.get("varianceReasonId");
		
		//create data physical Inventory
		
		GenericValue createorderHeader = delegator.makeValue("OrderHeader");
		createorderHeader.put("orderId", orderId);
		createorderHeader.put("orderTypeId", orderTypeId);
		createorderHeader.put("orderName", orderName);
		createorderHeader.put("orderDate", orderDate);
		createorderHeader.put("productStoreId", productStoreId);
		createorderHeader.put("originFacilityId", facilityId);
//		
//		if(dateCheck != null){
//		createPhysInv.put("physicalInventoryDate", dateCheck);
//		}else {
//			createPhysInv.put("physicalInventoryDate", currentDate);
//		}
//		
//		if(personCheck != null){
//		createPhysInv.put("partyId", personCheck);
//		}else {
//			createPhysInv.put("partyId", partyId);
//		}
		try{
			delegator.createOrStore(createorderHeader);
		}catch(Exception e){}
		
		
		GenericValue createOrderItem = delegator.makeValue("OrderItem");
		createOrderItem.put("orderId", orderId);
		createOrderItem.put("orderItemSeqId", orderItemSeqId);
		createOrderItem.put("fromInventoryItemId", inventoryItemId);
		createOrderItem.put("productId", productId);
		createOrderItem.put("quantity", quantityDestroy);
		try{
			delegator.createOrStore(createOrderItem);
		}catch(Exception e){}
		
		//update InventoryItem va inventory item variance
//		List<GenericValue> listInventoryItem = delegator.findList("InventoryItem", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "productId", productId, "expireDate", expireDate)), null, null, null, false);
//		// check error's user if exist update db
//		for(GenericValue item : listInventoryItem){
//			String itemId = (String)item.get("inventoryItemId");
//			GenericValue InvItemVar = delegator.findOne("InventoryItemVariance",UtilMisc.toMap("inventoryItemId", itemId,"physicalInventoryId",physicalInvId), false);
//			
//			BigDecimal quantityVar = BigDecimal.ZERO;
//			if(InvItemVar != null){
//			quantityVar = InvItemVar.getBigDecimal("quantityOnHandVar");
//			//delete
//			try{
//				delegator.removeValue(InvItemVar);
//			}catch(Exception e){}
//			//store
//			BigDecimal QOH = item.getBigDecimal("quantityOnHandTotal");
//			BigDecimal QOHUpdate = QOH.add(quantityVar);
//			item.put("quantityOnHandTotal", QOHUpdate);
//			try{
//				delegator.store(item);
//				
//			}catch(Exception e){}
//			}
//		}
//		//main
//		for(GenericValue item : listInventoryItem){
//			String itemId = (String)item.get("inventoryItemId");
//
//			BigDecimal quantity = item.getBigDecimal("quantityOnHandTotal");
//			
//			if(quantity.compareTo(quantityReason)!=-1){
//				quantity = quantity.subtract(quantityReason);
//			
//				item.put("quantityOnHandTotal", quantity);
//				// update inventory item
//				try{
//				delegator.store(item);
//				}catch(Exception e){}
//				
//				//create inventory item variancee
//				
//				GenericValue createVari = delegator.makeValue("InventoryItemVariance");
//				
//				createVari.put("inventoryItemId", itemId);
//				createVari.put("physicalInventoryId", physicalInvId);
//				createVari.put("varianceReasonId", variance);
//				createVari.put("quantityOnHandVar", quantityReason);
//				try{
//					delegator.createOrStore(createVari);
//					
//				}catch(Exception e){}
//				
//				break;
//			}else{
//				
//				
//				//else update zero inventory item
//				if(quantity.compareTo(BigDecimal.ZERO) !=0){
//				quantityReason = quantityReason.subtract(quantity);
//				item.put("quantityOnHandTotal", BigDecimal.ZERO);
//				try{
//				delegator.store(item);
//				}catch(Exception e){}
//				
//				// else update inventory item variance
//
//				GenericValue createVari = delegator.makeValue("InventoryItemVariance");
//				
//				createVari.put("inventoryItemId", itemId);
//				createVari.put("physicalInventoryId", physicalInvId);
//				createVari.put("varianceReasonId", variance);
//				createVari.put("quantityOnHandVar", quantity);
//				try{
//					delegator.createOrStore(createVari);
//				}catch(Exception e){}
//				}
//			}
//		}
		result.put("facilityId", facilityId);
//		result.put("physicalInventoryId", physicalInvId);
		result.put("orderId", orderId);
		result.put("orderName", orderName);
		result.put("productStoreId", productStoreId);
		result.put("orderDate", orderDate);
		result.put("orderTypeId",orderTypeId);
//		return ServiceUtil.returnSuccess();
		return result;
}
	
	public static Map<String, Object> DeleteOrderItemDestroy(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String orderId = (String)context.get("orderId");
		String orderItemSeqId = (String)context.get("orderItemSeqId");
		
		GenericValue orderItem = delegator.findOne("OrderItem",UtilMisc.toMap("orderId", orderId,"orderItemSeqId",orderItemSeqId), false);
		
		try{
			delegator.removeValue(orderItem);
		}catch(Exception e){}
		
		GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		String orderName = (String)orderHeader.get("orderName");
		String productStoreId = (String)orderHeader.get("productStoreId");
		String facilityId = (String)orderHeader.get("originFacilityId");
		Timestamp orderDate = (Timestamp)orderHeader.getTimestamp("orderDate");
		String orderTypeId = (String)orderHeader.get("orderTypeId");
		
		result.put("orderId", orderId);
		result.put("facilityId", facilityId);
		result.put("orderName", orderName);
		result.put("productStoreId", productStoreId);
		result.put("orderDate", orderDate);
		result.put("orderTypeId", orderTypeId);
		return result;
}

public static Map<String, Object> UpdateDetailDestroy(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
	Delegator delegator = dpx.getDelegator();
	Map<String, Object> result = new FastMap<String, Object>();
	String orderId = (String)context.get("orderId");
	String orderItemSeqId = (String)context.get("orderItemSeqId");
	String temp = (String)context.get("quantityDestroy");
	BigDecimal quantityDestroy = new BigDecimal(temp);
	GenericValue orderItem = delegator.findOne("OrderItem",UtilMisc.toMap("orderId", orderId,"orderItemSeqId",orderItemSeqId), false);
	orderItem.put("quantity", quantityDestroy);
	try{
		delegator.store(orderItem);
	}catch(Exception e){}
	
	GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
	String orderName = (String)orderHeader.get("orderName");
	String productStoreId = (String)orderHeader.get("productStoreId");
	String facilityId = (String)orderHeader.get("originFacilityId");
	Timestamp orderDate = (Timestamp)orderHeader.getTimestamp("orderDate");
	String orderTypeId = (String)orderHeader.get("orderTypeId");
	
	result.put("orderId", orderId);
	result.put("facilityId", facilityId);
	result.put("orderName", orderName);
	result.put("productStoreId", productStoreId);
	result.put("orderDate", orderDate);
	result.put("orderTypeId", orderTypeId);
	return result;
}

public static Map<String, Object> ApproveDestroy(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
	Delegator delegator = dpx.getDelegator();
	Map<String, Object> result = new FastMap<String, Object>();
	String orderId = (String)context.get("orderId");
	String orderItemSeqId = (String)context.get("orderItemSeqId");
	String tempCancel = (String)context.get("cancelQuantity");
	BigDecimal quantityCancel = BigDecimal.ZERO;
	if(tempCancel != null){
	quantityCancel = new BigDecimal(tempCancel);
	}
	GenericValue orderItem = delegator.findOne("OrderItem",UtilMisc.toMap("orderId", orderId,"orderItemSeqId",orderItemSeqId), false);
	orderItem.put("cancelQuantity", quantityCancel);
	try{
		delegator.store(orderItem);
	}catch(Exception e){}
	
	GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
	String orderName = (String)orderHeader.get("orderName");
	String productStoreId = (String)orderHeader.get("productStoreId");
	String facilityId = (String)orderHeader.get("originFacilityId");
	Timestamp orderDate = (Timestamp)orderHeader.getTimestamp("orderDate");
	String orderTypeId = (String)orderHeader.get("orderTypeId");
	
	result.put("orderId", orderId);
	result.put("facilityId", facilityId);
	result.put("orderName", orderName);
	result.put("productStoreId", productStoreId);
	result.put("orderDate", orderDate);
	result.put("orderTypeId", orderTypeId);
	result.put("Approve", "1");
	return result;
}

	public static Map<String, Object> addProductToQuota(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		
		String quotaId = (String)context.get("quotaId");
		GenericValue quotaItem = delegator.makeValue("QuotaItem");
		quotaItem.setNonPKFields(context);
		quotaItem.setPKFields(context);
		BigDecimal quotaQuantity = (BigDecimal)context.get("quotaQuantity");
		quotaItem.put("quotaQuantity", quotaQuantity);
		quotaItem.put("quantityAvailable", quotaQuantity);
		List<String> listOrderBy = new ArrayList<String>();
		listOrderBy.add("-quotaItemSeqId");
		List<GenericValue> listItems = new ArrayList<GenericValue>();
		try {
			listItems = delegator.findList("QuotaItem", EntityCondition.makeCondition(UtilMisc.toMap("quotaId", quotaId)), null, listOrderBy, null, false);
			if (!listItems.isEmpty()){
				Integer nextQuotaItemSeqId = Integer.parseInt((String)listItems.get(0).get("quotaItemSeqId")) + 1;
				String quotaItemSeqId = String.format("%05d", nextQuotaItemSeqId);
				quotaItem.put("quotaItemSeqId", quotaItemSeqId);
			} else {
				String quotaItemSeqId = String.format("%05d", 1);
				quotaItem.put("quotaItemSeqId", quotaItemSeqId);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		try {
			delegator.createOrStore(quotaItem);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("quotaId", quotaId);
		return result;
	}
public static Map<String, Object> createTargetCompany(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
		Delegator dele = dpx.getDelegator();
		String targetHeaderId = (String)context.get("targetHeaderId");
		String targetHeaderName = (String)context.get("targetHeaderName");
		String targetTypeId = (String)context.get("targetTypeId");
		String ofYear = (String)context.get("ofYear");
		String createBy = (String)context.get("createBy");
//		Timestamp createDate = (Timestamp)context.get("createDate");
		String primaryProductCategoryId = (String)context.get("primaryProductCategoryId");
		String productId = (String)context.get("productId");
		String quan = (String)context.get("quantity");
		BigDecimal quantity = BigDecimal.ZERO;
		if(quan != null){
		quantity = new BigDecimal(quan);
		}
		String targetItemSeqId = dele.getNextSeqId("TargetItem");

		GenericValue createTargetHeader = dele.makeValue("TargetHeader");
		createTargetHeader.put("targetHeaderId", targetHeaderId);
		createTargetHeader.put("targetHeaderName", targetHeaderName);
		createTargetHeader.put("targetTypeId", targetTypeId);
		createTargetHeader.put("ofYear", ofYear);
		createTargetHeader.put("createBy", createBy);
//		createTargetHeader.put("createDate", createDate);
		
		try{
			dele.createOrStore(createTargetHeader);
		}catch(Exception e){
		}

		GenericValue createTargetItem = dele.makeValue("TargetItem");
		createTargetItem.put("targetHeaderId", targetHeaderId);
		createTargetItem.put("targetItemSeqId", targetItemSeqId);
		createTargetItem.put("productId", productId);
		createTargetItem.put("productCategoryId", primaryProductCategoryId);
		createTargetItem.put("quantity", quantity);
		try{
			dele.create(createTargetItem);	
		}catch(Exception e){}
		
		Map<String,Object> result = new FastMap<String, Object>();
		result.put("targetTypeId", targetTypeId);
		return result;
}

public static Map<String, Object> editTargetItemCompany(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
	Delegator dele = dpx.getDelegator();
	String targetHeaderId = (String)context.get("targetHeaderId");
	String targetItemSeqId = (String)context.get("targetItemSeqId");
	String quan = (String)context.get("quantity");
	BigDecimal quantity = BigDecimal.ZERO;
	if(quan != null){
	quantity = new BigDecimal(quan);
	}

	GenericValue targetItem = dele.findOne("TargetItem", UtilMisc.toMap("targetHeaderId", targetHeaderId,"targetItemSeqId",targetItemSeqId), false);
//	createTargetHeader.put("createDate", createDate);
	targetItem.put("quantity", quantity);
	dele.createOrStore(targetItem);
	Map<String, Object> result = new FastMap<String, Object>();
	result.put("targetHeaderId", targetHeaderId);
	return result;
}

	//end physical dat


//BEGIN IMPORT PLAN BY DATNV

public static int CreateCustomTimePeriod(GenericDelegator delegator, Map<String, String[]> params, String organizationPartyId,
		String customTimePeriod, int loop) throws GenericEntityException, ParseException{
	int result = 0;

    GenericValue yearCustomTimePeriod = delegator.makeValue("CustomTimePeriod");
    String periodNameYear = params.get("year")[0];
    String yearTimePeriodId = params.get("yearSeq")[0];
    if(customTimePeriod.equals("IMPORT_YEAR")){
        yearCustomTimePeriod.put("customTimePeriodId", yearTimePeriodId);
        yearCustomTimePeriod.put("periodName", "Nam:"+periodNameYear+"");
        String thru = (String)params.get("thruDate")[0];
        String from = (String)params.get("fromDate")[0];
        SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fromFormat = (Date)yearMonthDayFormat.parse(from);
        Date thruFormat = (Date)yearMonthDayFormat.parse(thru);
        Calendar calendar = Calendar.getInstance();
	    calendar.setTime(fromFormat);
	    calendar.set(Calendar.HOUR, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    java.sql.Date fromDate = new java.sql.Date(calendar.getTimeInMillis());
	    Calendar calendarThru = Calendar.getInstance();
	    calendarThru.setTime(thruFormat);
	    calendarThru.set(Calendar.HOUR, 0);
	    calendarThru.set(Calendar.MINUTE, 0);
	    calendarThru.set(Calendar.SECOND, 0);
	    calendarThru.set(Calendar.MILLISECOND, 0);
	    java.sql.Date thruDate = new java.sql.Date(calendarThru.getTimeInMillis());
        yearCustomTimePeriod.put("fromDate", fromDate);
        yearCustomTimePeriod.put("thruDate", thruDate);
    }else if(customTimePeriod.equals("IMPORT_MONTH")){
        String[] month = params.get("monthSeq");
        yearCustomTimePeriod.put("customTimePeriodId", yearTimePeriodId);
    	//add time
    	
    	//add parent custom time
    	yearCustomTimePeriod.put("parentPeriodId",yearTimePeriodId);
        yearCustomTimePeriod.put("periodName", "thang:"+loop+"");
        yearCustomTimePeriod.put("customTimePeriodId", month[loop-1]);
    }
    yearCustomTimePeriod.put("periodTypeId", customTimePeriod);
    Long periodId = new Long("1");
    yearCustomTimePeriod.put("periodNum", periodId);
    yearCustomTimePeriod.put("organizationPartyId", organizationPartyId);
    delegator.createOrStore(yearCustomTimePeriod);
    result = 1;
	return result;
}



public static void CreateImportPlanItem(HttpServletRequest request, HttpServletResponse response) throws ParseException, GenericEntityException, IOException{
	GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
//    LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
    GenericValue userLoginImport = (GenericValue) request.getSession().getAttribute("userLogin");
    String userLoginId = (String)userLoginImport.get("userLoginId");
    Map<String, String[]> params = request.getParameterMap();
    String organizationPartyId = params.get("organizationPartyId")[0];
    String periodNameYear = params.get("year")[0];
    String yearTimePeriodId = params.get("yearSeq")[0];
//Create Year Custom time period for Import planning
    int createCustomTimePeriodYear = CreateCustomTimePeriod(delegator, params, organizationPartyId, "IMPORT_YEAR", 0);
    
//Create import planning product header of year
    GenericValue productPlanHeader = delegator.makeValue("ProductPlanHeader");
    String productPlanId = delegator.getNextSeqId("ProductPlanHeader");
    productPlanHeader.put("productPlanId", productPlanId);
    String productPlanTypeId = "IMPORT_PLAN";
    productPlanHeader.put("productPlanTypeId", productPlanTypeId);
    productPlanHeader.put("customTimePeriodId", yearTimePeriodId);
    productPlanHeader.put("createByUserLoginId", userLoginId);
    productPlanHeader.put("productPlanName", periodNameYear);
    productPlanHeader.put("organizationPartyId", organizationPartyId);
    String internalPartyId = params.get("internalPartyId")[0];
    productPlanHeader.put("internalPartyId", internalPartyId);
    productPlanHeader.put("statusId", "PLAN_CREATED");
    delegator.create(productPlanHeader);
//Create import planning item
    String[] month = params.get("monthSeq");
    ArrayList<String> monthPlanSeq = new ArrayList<String>();
    for(int i=0; i<month.length; i++){
    	int j = i+1;
//Create Month Custom time period for Import planning
        int createCustomTimePeriodMonth = CreateCustomTimePeriod(delegator, params, organizationPartyId, "IMPORT_MONTH", j);
        
//Create import planning product header of month
        GenericValue productPlanHeaderMonth = delegator.makeValue("ProductPlanHeader");
        String productPlanIdMonth = delegator.getNextSeqId("ProductPlanHeader");
        productPlanHeaderMonth.put("productPlanId", productPlanIdMonth);
        productPlanHeaderMonth.put("parentProductPlanId", productPlanId);
        productPlanHeaderMonth.put("productPlanTypeId", productPlanTypeId);
        String monthTimePeriodId = month[i];
        productPlanHeaderMonth.put("customTimePeriodId", monthTimePeriodId);
        productPlanHeaderMonth.put("createByUserLoginId", userLoginId);
        productPlanHeaderMonth.put("productPlanName", "tháng:"+j+"");
        productPlanHeaderMonth.put("organizationPartyId", organizationPartyId);
        productPlanHeaderMonth.put("internalPartyId", internalPartyId);
        productPlanHeaderMonth.put("statusId", "PLAN_CREATED");
        
        delegator.create(productPlanHeaderMonth);
        monthPlanSeq.add(i, productPlanIdMonth);
    }
    
//   loop key of Map params
    
    Set<String> keys = params.keySet();
    for(String key:keys){
    	if(!(key.equals("internalPartyId")) && !(key.equals("organizationPartyId")) && !(key.equals("monthSeq")) && !(key.equals("fromDate")) &&!(key.equals("thruDate")) && !(key.endsWith("year")) && !(key.equals("yearSeq")) ){
        	
    		String[] product = params.get(key);
            String localeObj = getLocale(request);
            Locale locale = new Locale(localeObj);

        	//convert string to locale number
            NumberFormat num = NumberFormat.getInstance(locale);
            for(int i=0; i < product.length; i++){
            	String quantityStr = product[i];
            	double mynb = num.parse(quantityStr).doubleValue();
                BigDecimal quantity = new BigDecimal(mynb);
//create product plan item
                GenericValue productPlanItem = delegator.makeValue("ProductPlanItem");
                productPlanItem.put("productPlanId", monthPlanSeq.get(i));
                String productPlanItemSeqId = delegator.getNextSeqId("ProductPlanItem");
                productPlanItem.put("productPlanItemSeqId", productPlanItemSeqId);
                productPlanItem.put("productId", key);
                GenericValue productCateId = delegator.findOne("Product", UtilMisc.toMap("productId", key), false);
                if(productCateId != null){
                	productPlanItem.put("productCategoryId", (String)productCateId.get("primaryProductCategoryId"));
                }
                BigDecimal recentQuantity = BigDecimal.ZERO;
                productPlanItem.put("recentPlanQuantity", recentQuantity);
                GenericValue quantityUom = delegator.findOne("Product", UtilMisc.toMap("productId", key), false); 
                String productQuantityUom = (String)quantityUom.get("quantityUomId");
                if(productQuantityUom != null){
                	productPlanItem.put("quantityUomId", productQuantityUom);
                }
                productPlanItem.put("planQuantity", quantity);
                productPlanItem.put("statusId", "PLAN_ITEM_CREATED");
                delegator.create(productPlanItem);
            }
    	}
    	
    }

    response.sendRedirect("/delys/control/getImportPlans");
}
	//end physical dat

	public static String addProductsToCart(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		HttpSession session = request.getSession();
		Delegator delegator = (Delegator) request.getAttribute("delegator");
		LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
		GenericValue userLogin = (GenericValue)session.getAttribute("userLogin");
		Map<String, Object> context = UtilHttp.getCombinedMap(request);
		
		String localeObj = getLocale(request);
        Locale locale = new Locale(localeObj);
		
		String facilityId = (String)context.get("facilityId_o_0");
		String productPlanId = (String)context.get("productPlanId_o_0");
		String productStoreId = (String)context.get("productStoreId_o_0");
		session.setAttribute("productPlanId", productPlanId);
		if (facilityId == null){
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,"NoFacilityForPlan", locale));
			return "error";
		}
		String contactMechId = (String)context.get("contactMechId_o_0");
		if (contactMechId == null){
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,"NoFacilityAddress", locale));
			return "error";
		}
		
		GenericValue planHeader = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
		String internalPartyId = (String)planHeader.get("internalPartyId");
		if (internalPartyId != null){
		} else {
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,"NoInternalPartyInPlan", locale));
			return "error";
		}
		String currencyUom = (String)planHeader.get("currencyUomId");
		ShoppingCart cart = ShoppingCartEvents.getCartObject(request, locale, currencyUom);
		try {
			cart.setProductStoreId(productStoreId);
			cart.setCurrency(dispatcher, currencyUom);
			cart.setUserLogin(userLogin, dispatcher);
			cart.setFacilityId(facilityId);
			cart.setLocale(locale);
			cart.setPlacingCustomerPartyId((String)planHeader.get("organizationPartyId"));
			cart.setBillToCustomerPartyId((String)planHeader.get("organizationPartyId"));
			cart.setAllShippingContactMechId(contactMechId);
			cart.setAllIsGift(false);
		} catch (CartItemModifyException e) {
			e.printStackTrace();
		}
		List<GenericValue> listPlanItems = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
		if (!listPlanItems.isEmpty()){
			for (int i = 0; i < listPlanItems.size(); i ++){
				String rowSubmit = (String)context.get("_rowSubmit_o_"+i);
				 SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss MMM d, yyyy z");
				if ("Y".equals(rowSubmit)){
					Timestamp shipBeforeDate = null;
					Timestamp shipAfterDate = null;
					try {
						String shipBeforeDateStr = (String)context.get("shipBeforeDate_o_"+i);
						if (!"".equals(shipBeforeDateStr) && shipBeforeDateStr != null){
							shipBeforeDate = new Timestamp(sdf.parse(shipBeforeDateStr).getTime());
						}
						String shipAfterDateStr = (String)context.get("shipAfterDate_o_"+i);
						if (!"".equals(shipAfterDateStr) && shipAfterDateStr != null){
							shipAfterDate = new Timestamp(sdf.parse(shipAfterDateStr).getTime());
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
					String quantityStr = (String)context.get("orderQuantity_o_"+i);
					NumberFormat nb = NumberFormat.getInstance(locale);
			        double mynb = 0;
					try {
						mynb = nb.parse(quantityStr).doubleValue();
					} catch (ParseException e) {
						e.printStackTrace();
					}
			        BigDecimal orderQuantity = new BigDecimal(mynb);
			        if (orderQuantity.compareTo(BigDecimal.ZERO) < 0){
			        	request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,"EnterCorrectQuantity", locale));
						return "error";
			        }
			        if (orderQuantity.compareTo(BigDecimal.ZERO) > 0){
						String prodCatalogId = (String)context.get("prodCatalogId_o_"+i);
						String productCategoryId = (String)context.get("productCategoryId_o_"+i);
						String productId = (String)context.get("productId_o_"+i);
						ShoppingCartHelper helper = new ShoppingCartHelper(delegator, dispatcher, cart);
						helper.addToCart(prodCatalogId, null, null, productId, productCategoryId, "PRODUCT_ORDER_ITEM", null, null, null, orderQuantity, null, null, null, null, null, shipBeforeDate, shipAfterDate, null, null, context, null);
			        }
				}
			}
		}
		return "succes";
	}
	
	public static String createPurchaseOrder(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession();
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
        Map<String, Object> callResult;

        if (UtilValidate.isEmpty(userLogin)) {
            userLogin = cart.getUserLogin();
            session.setAttribute("userLogin", userLogin);
        }
        // remove this whenever creating an order so quick reorder cache will refresh/recalc
        session.removeAttribute("_QUICK_REORDER_PRODUCTS_");

        boolean areOrderItemsExploded = CheckOutEvents.explodeOrderItems(delegator, cart);

        //get the TrackingCodeOrder List
        List<GenericValue> trackingCodeOrders = TrackingCodeEvents.makeTrackingCodeOrders(request);
        String distributorId = (String) session.getAttribute("_DISTRIBUTOR_ID_");
        String affiliateId = (String) session.getAttribute("_AFFILIATE_ID_");
        String visitId = VisitHandler.getVisitId(session);
        String webSiteId = WebSiteWorker.getWebSiteId(request);

        callResult = checkOutHelper.createOrder(userLogin, distributorId, affiliateId, trackingCodeOrders, areOrderItemsExploded, visitId, webSiteId);
        if (callResult != null) {
            ServiceUtil.getMessages(request, callResult, null);
            if (ServiceUtil.isError(callResult)) {
                // messages already setup with the getMessages call, just return the error response code
                return "error";
            }
            if (callResult.get(ModelService.RESPONSE_MESSAGE).equals(ModelService.RESPOND_SUCCESS)) {
                // set the orderId for use by chained events
                String orderId = cart.getOrderId();
                request.setAttribute("orderId", orderId);
                request.setAttribute("orderAdditionalEmails", cart.getOrderAdditionalEmails());
            }
        }
        
        String issuerId = request.getParameter("issuerId");
        if (UtilValidate.isNotEmpty(issuerId)) {
            request.setAttribute("issuerId", issuerId);
        }
        
        String productPlanId = (String)session.getAttribute("productPlanId");
        if (productPlanId != null){
        	try {
        		String orderId = (String)callResult.get("orderId");
				GenericValue planAndOrder = delegator.makeValue("ProductPlanAndOrder");
				planAndOrder.put("orderId", orderId);
				planAndOrder.put("productPlanId", productPlanId);
				planAndOrder.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.createOrStore(planAndOrder);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
        }
        session.removeAttribute("productPlanId");
        return cart.getOrderType().toLowerCase();
    }
	
	public static Map<String, Object> updateProductPlanItem(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String orderId = (String)context.get("orderId");
		Locale locale = (Locale)context.get("locale");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
		if ("PURCHASE_ORDER".equals((String)orderHeader.get("orderTypeId"))){
			List<String> orderBy = new ArrayList<String>();
			orderBy.add("-fromDate");
			List<GenericValue> listPlanByOrders = delegator.findList("ProductPlanAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, orderBy, null, false);
			listPlanByOrders = EntityUtil.filterByDate(listPlanByOrders);
			String currentOrderStatus = (String)orderHeader.get("statusId");
			if (!listPlanByOrders.isEmpty()){
				String productPlanId = (String)listPlanByOrders.get(0).get("productPlanId");
				
				List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
				if (!orderItems.isEmpty()){
					if("ORDER_COMPLETED".equals(currentOrderStatus)){
						for (GenericValue orderItem : orderItems){
							String productId = (String)orderItem.get("productId");
							//cap nhat so lieu cho 1 tuan
							List<GenericValue> planItems = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
							if(!planItems.isEmpty()){
								Boolean check = false;
								for (GenericValue planItem : planItems){
									if (productId.equals(planItem.get("productId"))){
										check = true;
										BigDecimal recentPlanQuantity = new BigDecimal(0);
										if (planItem.getBigDecimal("recentPlanQuantity") != null){
											recentPlanQuantity = planItem.getBigDecimal("recentPlanQuantity");
										}
										BigDecimal quantity = orderItem.getBigDecimal("quantity");
										Map<String,Object> contextTmp = new HashMap<String, Object>();
										contextTmp.put("productPlanId", productPlanId);
										contextTmp.put("productPlanItemSeqId", (String)planItem.get("productPlanItemSeqId"));
										contextTmp.put("recentPlanQuantity", quantity.add(recentPlanQuantity));
										contextTmp.put("statusId", "PLAN_ITEM_MODIFIED");
										contextTmp.put("userLogin", userLogin);
										try {
											dispatcher.runSync("createOrUpdateProductPlanItem", contextTmp);
										} catch (GenericServiceException e) {
											return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
										}
									}
								}
								//note1:dat: else tao moi them planItem ... note: viet service tao moi planItem thi eca goi service luu vet chinh sua
								//note2: ko nen thuc hien note1, chuyen sang thuc hien khi approved order, neu thuc hien buoc nay
								//thi truowng hop hanng ve kho xuat hien sp moi
								if(!check){
									Map<String,Object> contextTmp = new HashMap<String, Object>();
									contextTmp.put("productPlanId", productPlanId);
									contextTmp.put("productId", productId);
									contextTmp.put("recentPlanQuantity", orderItem.getBigDecimal("quantity"));
									contextTmp.put("planQuantity", new BigDecimal(0));
									contextTmp.put("orderedQuantity", new BigDecimal(0));
									contextTmp.put("inventoryForecast", new BigDecimal(0));
									contextTmp.put("statusId", "PLAN_ITEM_CREATED");
									contextTmp.put("userLogin", userLogin);
									try {
										dispatcher.runSync("createOrUpdateProductPlanItem", contextTmp);
									} catch (GenericServiceException e) {
										return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
									}
								}
							}
							//tiep tuc thuc hien update cac so lieu tren cho thang'
							
						}
					} else {
						if("ORDER_CANCELLED".equals(currentOrderStatus)){
								for (GenericValue orderItem : orderItems){
									String productId = (String)orderItem.get("productId");
									List<GenericValue> planItems = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
									if(!UtilValidate.isEmpty(planItems)){
										for(GenericValue planItem: planItems){
											if (productId.equals(planItem.get("productId"))){
												BigDecimal quantity = orderItem.getBigDecimal("quantity");
												BigDecimal orderedQuantity = new BigDecimal(0);
												if(planItem.getBigDecimal("orderedQuantity") != null){
													orderedQuantity = planItem.getBigDecimal("orderedQuantity");
												}
												Map<String,Object> contextTmp = new HashMap<String, Object>();
												contextTmp.put("productPlanId", productPlanId);
												contextTmp.put("productPlanItemSeqId", (String)planItem.get("productPlanItemSeqId"));
												contextTmp.put("orderedQuantity", orderedQuantity.subtract(quantity));
												contextTmp.put("statusId", "PLAN_ITEM_MODIFIED");
												contextTmp.put("userLogin", userLogin);
												try {
													dispatcher.runSync("createOrUpdateProductPlanItem", contextTmp);
												} catch (GenericServiceException e) {
													return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
												}
												
											}
										}
									}
								}
						} else {
							if ("ORDER_APPROVED".equals(currentOrderStatus)){
									for (GenericValue orderItem : orderItems){
										String productId = (String)orderItem.get("productId");
										List<GenericValue> planItems = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
										if(!UtilValidate.isEmpty(planItems)){
											Boolean check = false;
											for(GenericValue planItem : planItems){
												if (productId.equals(planItem.get("productId"))){
													check = true;
													BigDecimal quantity = orderItem.getBigDecimal("quantity");
													BigDecimal orderedQuantity = new BigDecimal(0);
													if(planItem.getBigDecimal("orderedQuantity") != null){
														orderedQuantity = planItem.getBigDecimal("orderedQuantity");
													}
													Map<String,Object> contextTmp = new HashMap<String, Object>();
													contextTmp.put("productPlanId", productPlanId);
													contextTmp.put("productPlanItemSeqId", (String)planItem.get("productPlanItemSeqId"));
													contextTmp.put("orderedQuantity", orderedQuantity.add(quantity));
													contextTmp.put("statusId", "PLAN_ITEM_MODIFIED");
													contextTmp.put("userLogin", userLogin);
													try {
														dispatcher.runSync("createOrUpdateProductPlanItem", contextTmp);
													} catch (GenericServiceException e) {
														return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
													}
												}
											}
											if(!check){
												Map<String,Object> contextTmp = new HashMap<String, Object>();
												contextTmp.put("productPlanId", productPlanId);
												contextTmp.put("productId", productId);
												contextTmp.put("recentPlanQuantity", new BigDecimal(0));
												contextTmp.put("planQuantity", new BigDecimal(0));
												contextTmp.put("orderedQuantity", orderItem.getBigDecimal("quantity"));
												contextTmp.put("inventoryForecast", new BigDecimal(0));
												contextTmp.put("statusId", "PLAN_ITEM_CREATED");
												contextTmp.put("userLogin", userLogin);
												try {
													dispatcher.runSync("createOrUpdateProductPlanItem", contextTmp);
												} catch (GenericServiceException e) {
													return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
												}
											}
										}
									}
							}
						}
					}
				}
//				planItems = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
//				Boolean check = true;
//				for (GenericValue planItem : planItems){
//					if (!"PLAN_ITEM_COMPLETED".equals((planItem.get("statusId")))){
//						check = false;
//					}
//				}
//				if (check){
//					GenericValue planHeader = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
//					planHeader.put("statusId", "PLAN_COMPLETED");
//					delegator.store(planHeader);
//				}
			}
		}
		result.put("orderId", orderId);
		return result;
	}

public static Map<String, Object> CreateImportPlanPeriodTime(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
	Delegator delegator = dpx.getDelegator();
	Map<String, Object> result = new FastMap<String, Object>();
	String CustomTimePeriodYearId = delegator.getNextSeqId("CustomTimePeriod");
	result.put("yearSeq", CustomTimePeriodYearId);
	Map<String, Object> mm = new FastMap<String, Object>();
	for(int i=1; i < 13; i++){
		String monthSeq = delegator.getNextSeqId("CustomTimePeriod");
		mm.put(Integer.toString(i), monthSeq);
	}
	result.put("monthMapSeq", mm);
	return result;
}

public static int[] CutString(String[] check){
	int[] arrcheck = null;
	if(check != null){
    	arrcheck = new int[check.length];
    for(int i = 0; i < check.length; i++){
    	String checkStr = check[i];
    	String numCheckStr = checkStr.substring(checkStr.length()-1);
    	int num = Integer.parseInt(numCheckStr);
    	arrcheck[i]=num-1;
    }
    }
	return arrcheck;
}

public static String getLocale(HttpServletRequest request){
	String localeObj = (String) (request.getSession() != null ? request.getSession().getAttribute("locale") : null);	
    
    if (localeObj == null) {
    	
        Map<?, ?> userLogin = (Map<?, ?>) request.getSession().getAttribute("userLogin");
        if (userLogin == null) {
            userLogin = (Map<?,?>) request.getSession().getAttribute("autoUserLogin");
        }
        if (userLogin != null) {
            localeObj = (String)userLogin.get("lastLocale");
        }
    }
    return localeObj;
}
public static void EditImportPlanItem(HttpServletRequest request, HttpServletResponse response) throws ParseException, GenericEntityException, IOException{
	GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
    GenericValue userLoginImport = (GenericValue) request.getSession().getAttribute("userLogin");
    String userLoginId = (String)userLoginImport.get("userLoginId");
    Map<String, String[]> params = request.getParameterMap();
    String[] month = params.get("month");
    String[] seq = params.get("Seq");
    String[] quantity = params.get("quantity");
    String customTime = params.get("customTime")[0];
    String[] check = params.get("check");
    //cut sting to get last string
    int[] arrcheck = CutString(check);
    //get locale to convert string
    String localeObj = getLocale(request);	
    Locale locale = new Locale(localeObj);
	//convert string to locale number
    NumberFormat num = NumberFormat.getInstance(locale);
    int sizeMonth = month.length;
    int sizeSeq = seq.length;
    int sizeProduct = sizeSeq/sizeMonth;
    UpdatePlanItem(arrcheck, month, seq, quantity, sizeProduct, num, delegator);
    response.sendRedirect("/delys/control/getDetailImportPlanToEdit?customTimePeriodId="+customTime+"");
}

public static void UpdatePlanItem(int[] arrcheck, String[] month, String seq[], String[] quantity, int sizeProduct, NumberFormat num, GenericDelegator delegator) throws GenericEntityException, ParseException{
	if(arrcheck != null){
    	for(int k = 0; k < arrcheck.length; k++){
    		int out = arrcheck[k];
    		int tempFirst = sizeProduct*(out);
    		int tempLast = sizeProduct*(out+1);
    			for(int j = tempFirst; j < tempLast; j++){
    				GenericValue productPlanItem = delegator.findOne("ProductPlanItem", UtilMisc.toMap("productPlanId", month[out], "productPlanItemSeqId", seq[j]), false);
    				String quantityStr = quantity[j];
    				double myNumber = num.parse(quantityStr).doubleValue();
    				BigDecimal planQuantity = new BigDecimal(myNumber);
    				productPlanItem.put("planQuantity", planQuantity);
    				delegator.store(productPlanItem);
    			}
    	}
    }
}
//END IMPORT PLAN BY DATNV

//BEGIN HISTORY INVENTORY 
public static Map<String, Object> GetHistoryInventory(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
	Delegator delegator = dpx.getDelegator();
	Map<String, Object> result = new FastMap<String, Object>();
	String CustomTimePeriodYearId = delegator.getNextSeqId("CustomTimePeriod");
	result.put("yearSeq", CustomTimePeriodYearId);
//	Map<Object, String> month = new FastMap<Object,String>();
	Map<String, Object> mm = new FastMap<String, Object>();
	for(int i=1; i < 13; i++){
		String monthSeq = delegator.getNextSeqId("CustomTimePeriod");
		mm.put(Integer.toString(i), monthSeq);
	}
	result.put("monthMapSeq", mm);
//	String a="";
	return result;
}
//END IMPORT PLAN BY DATNV
	public static Map<String, Object> getPartyContactMechs(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String)context.get("partyId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
		try {
			List<GenericValue> listPartyContactMechs = delegator.findList("PartyContactMech", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)), null, null, null, false);
			listPartyContactMechs = EntityUtil.filterByDate(listPartyContactMechs);
			if (!listPartyContactMechs.isEmpty()){
				for (GenericValue ctm : listPartyContactMechs){
					List<GenericValue> listContactMechPurpose = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechId", (String)ctm.get("contactMechId"), "contactMechPurposeTypeId", contactMechPurposeTypeId)), null, null, null, false);
					listContactMechPurpose = EntityUtil.filterByDate(listContactMechPurpose);
					if (!listContactMechPurpose.isEmpty()){
						for (GenericValue contact : listContactMechPurpose){
							List<GenericValue> listPostalAddress = delegator.findList("PostalAddress", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contact.get("contactMechId"))), null, null, null, false);
							for (GenericValue pa : listPostalAddress){
								if (!listContactMechs.contains(pa)){
									listContactMechs.add(pa);
								}
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("partyId", partyId);
		result.put("listPartyContactMechs", listContactMechs);
		return result;
	}
	public static Map<String, Object> getPartyTelecomNumbers(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String)context.get("partyId");
		String contactMechPurposeTypeId = (String)context.get("contactMechPurposeTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
		try {
			List<GenericValue> listPartyContactMechs = delegator.findList("PartyContactMech", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)), null, null, null, false);
			listPartyContactMechs = EntityUtil.filterByDate(listPartyContactMechs);
			if (!listPartyContactMechs.isEmpty()){
				for (GenericValue ctm : listPartyContactMechs){
					List<GenericValue> listContactMechPurpose = delegator.findList("PartyContactMechPurpose", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "contactMechId", (String)ctm.get("contactMechId"), "contactMechPurposeTypeId", contactMechPurposeTypeId)), null, null, null, false);
					listContactMechPurpose = EntityUtil.filterByDate(listContactMechPurpose);
					if (!listContactMechPurpose.isEmpty()){
						for (GenericValue contact : listContactMechPurpose){
							List<GenericValue> listPostalAddress = delegator.findList("TelecomNumber", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contact.get("contactMechId"))), null, null, null, false);
							for (GenericValue pa : listPostalAddress){
								if (!listContactMechs.contains(pa)){
									listContactMechs.add(pa);
								}
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("partyId", partyId);
		result.put("listPartyTelecomNumbers", listContactMechs);
		return result;
	}
	public static Map<String, Object> getPartyFinAccounts(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String)context.get("partyId");
		String finAccountTypeId = (String)context.get("finAccountTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listFinAccounts = new ArrayList<GenericValue>();
		try {
			listFinAccounts = delegator.findList("FinAccount", EntityCondition.makeCondition(UtilMisc.toMap("organizationPartyId", partyId, "finAccountTypeId", finAccountTypeId)), null, null, null, false);
			listFinAccounts = EntityUtil.filterByDate(listFinAccounts);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("partyId", partyId);
		result.put("listPartyFinAccounts", listFinAccounts);
		return result;
	}
	
	public static Map<String, Object> getPartyPrimaryEmails(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String)context.get("partyId");
		String contactMechTypeId = (String)context.get("contactMechTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listContactMechs = new ArrayList<GenericValue>();
		try {
			List<GenericValue> listPartyContactMechs = delegator.findList("PartyContactMech", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)), null, null, null, false);
			listPartyContactMechs = EntityUtil.filterByDate(listPartyContactMechs);
			if (!listPartyContactMechs.isEmpty()){
				for (GenericValue contact : listPartyContactMechs){
					List<GenericValue> contactMechs = delegator.findList("ContactMech", EntityCondition.makeCondition(UtilMisc.toMap("contactMechId", contact.get("contactMechId"), "contactMechTypeId", contactMechTypeId)), null, null, null, false);
					for (GenericValue pa : contactMechs){
						if (!listContactMechs.contains(pa)){
							listContactMechs.add(pa);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("partyId", partyId);
		result.put("listPartyPrimaryEmails", listContactMechs);
		return result;
	}
	public static Map<String, Object> getPartyRepresents(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String)context.get("partyId");
		String roleTypeIdFrom = (String)context.get("roleTypeIdFrom");
		String roleTypeIdTo = (String)context.get("roleTypeIdTo");
		String partyRelationshipTypeId = (String)context.get("partyRelationshipTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listPartyRepresents = new ArrayList<GenericValue>();
		try {
			List<GenericValue> listRelationships = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyId, "roleTypeIdFrom", roleTypeIdFrom, "partyRelationshipTypeId", partyRelationshipTypeId)), null, null, null, false);
			listRelationships = EntityUtil.filterByDate(listRelationships);
			if (!listRelationships.isEmpty()){
				for (GenericValue relation : listRelationships){
					List<GenericValue> listPartyRoles = delegator.findList("PartyPersonPartyRole", EntityCondition.makeCondition(UtilMisc.toMap("partyId", relation.get("partyIdTo"), "roleTypeId", roleTypeIdTo)), null, null, null, false);
					for (GenericValue pa : listPartyRoles){
						if (!listPartyRepresents.contains(pa)){
							listPartyRepresents.add(pa);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("partyId", partyId);
		result.put("listPartyRepresents", listPartyRepresents);
		return result;
	}
	public static Map<String, Object> updatePurchaseAgreement(DispatchContext ctx, Map<String, Object> context) throws ParseException{
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispathcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin"); 
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale)context.get("locale");
//		Security security = (Security)context.get("security");
		Security security = ctx.getSecurity();
		String agreementId = (String)context.get("agreementId");
		GenericValue agreementAndOrder = null;
		String listOrderItems = (String) context.get("orderItems");
		String productPlanId = (String)context.get("productPlanId");
		JSONArray arrOrderItems = JSONArray.fromObject(listOrderItems);
		
		String lotId = (String)context.get("lotId");
		String portOfDischargeId = (String)context.get("portOfDischargeId");
		String partyIdFrom = (String)context.get("partyIdFrom");
		String representPartyIdFrom = (String)context.get("representPartyIdFrom");
		String addressIdFrom = (String)context.get("addressIdFrom");
		String telephoneIdFrom = (String)context.get("telephoneIdFrom");
		String faxNumberIdFrom = (String)context.get("faxNumberIdFrom");
//		String productStoreId = (String)context.get("productStoreId");
//		String facilityId = (String)context.get("facilityId");
		String contactMechId = (String)context.get("contactMechId");
		String transshipment = (String)context.get("transshipment");
		String partialShipment = (String)context.get("partialShipment");
		@SuppressWarnings("unchecked")
		List<String> finAccountIdFroms = (List<String>)context.get("finAccountIdFroms");
		@SuppressWarnings("unchecked")
		List<String> currencyUomIds = (List<String>)context.get("currencyUomIds");
		String partyIdTo = (String)context.get("partyIdTo");
		String addressIdTo = (String)context.get("addressIdTo");
		@SuppressWarnings("unchecked")
		List<String> finAccountIdTos = (List<String>)context.get("finAccountIdTos");
		String emailAddressIdTo = (String)context.get("emailAddressIdTo");
		Map<String, Object> mapParameters = new FastMap<String, Object>();
		Timestamp agreementDate = new Timestamp((Long)context.get("agreementDate"));
		String agreementName = (String)context.get("agreementName");
		Timestamp fromDate = null;
		ShoppingCart cart = (ShoppingCart)context.get("shoppingCart");
//		cart.setProductStoreId(productStoreId);
		GenericValue productPlan = null;
		String currencyUomId = currencyUomIds.get(0);
		String parentProductPlanHeader = "";
		try {
			productPlan = delegator.findOne("ProductPlanHeader", false, UtilMisc.toMap("productPlanId", productPlanId));
			parentProductPlanHeader = (String)productPlan.get("parentProductPlanId");
//			currencyUomId = (String)productPlan.get("currencyUomId");
		} catch (GenericEntityException e2) {
			e2.printStackTrace();
		}
		try {
			cart.setCurrency(dispatcher, currencyUomId);
			cart.setUserLogin(userLogin, dispatcher);
//			cart.setFacilityId(facilityId);
			cart.setLocale(locale);
			cart.setPlacingCustomerPartyId(partyIdFrom);
			cart.setBillToCustomerPartyId(partyIdFrom);
			cart.setAllShippingContactMechId(contactMechId);
			cart.setAllIsGift(false);
			if ((String)cart.getAttribute("supplierPartyId") == null){
				cart.setAttribute("supplierPartyId", partyIdTo);
			}
			cart.setOrderPartyId(partyIdTo);
		} catch (CartItemModifyException e3) {
			e3.printStackTrace();
		}
		if (agreementDate == null){
			agreementDate = UtilDateTime.nowTimestamp();
		} 
		context.put("agreementDate", agreementDate);
		fromDate = agreementDate;
		Timestamp shipBeforeDate = null;
		Timestamp shipAfterDate = null;
		String weekETD = (String)context.get("weekETD");
		String weekETA = null;
		try {
			if (productPlan != null){
				weekETA = (String)productPlan.get("customTimePeriodId");
			}
			if (weekETD == null){
				GenericValue customTimeETA = delegator.findOne("CustomTimePeriod", false, UtilMisc.toMap("customTimePeriodId", weekETA));
				java.sql.Date fromDateETA = (java.sql.Date)customTimeETA.get("fromDate");
				shipBeforeDate = new Timestamp(fromDateETA.getTime());
				Calendar calendar = Calendar.getInstance();
			    calendar.setTime(fromDateETA);
			    calendar.add(Calendar.DATE, -35);
			    java.sql.Date fromDateETD = new java.sql.Date(calendar.getTimeInMillis());
			    GenericValue customTimeETD = EntityUtil.getFirst(delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("fromDate", fromDateETD, "periodTypeId", "IMPORT_WEEK")), null, null, null, false));
			    if (customTimeETD != null){
			    	weekETD = (String)customTimeETD.get("customTimePeriodId");
			    	shipAfterDate = new Timestamp(fromDateETD.getTime());
			    } else {
			    	GenericValue newCustomTimeETD = delegator.makeValue("CustomTimePeriod");
			    	newCustomTimeETD.put("customTimePeriodId", delegator.getNextSeqId("CustomTimePeriod"));
			    	newCustomTimeETD.put("periodTypeId", "IMPORT_WEEK");
			    	newCustomTimeETD.put("fromDate", fromDateETD);
			    	// get week by fromDate to create new customPeriodName
			    	delegator.createOrStore(newCustomTimeETD);
			    	weekETD = (String)newCustomTimeETD.get("customTimePeriodId");
			    	shipAfterDate = new Timestamp(fromDateETD.getTime());
			    }
			}else{
				GenericValue customTimeETD = delegator.findOne("CustomTimePeriod", false, UtilMisc.toMap("customTimePeriodId", weekETD));
				shipAfterDate = new Timestamp(customTimeETD.getDate("fromDate").getTime());
				
				GenericValue customTimeETA = delegator.findOne("CustomTimePeriod", false, UtilMisc.toMap("customTimePeriodId", weekETA));
				java.sql.Date fromDateETA = (java.sql.Date)customTimeETA.get("fromDate");
				shipBeforeDate = new Timestamp(fromDateETA.getTime());
			}
		} catch (GenericEntityException e1) {
			e1.printStackTrace();
		} 
		
		if (agreementId != null){
			List<GenericValue> listAgreementAndOrder = FastList.newInstance();
			try {
				listAgreementAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
			} catch (GenericEntityException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			List<GenericValue> listFilterAgreementAndOrder = EntityUtil.filterByDate(listAgreementAndOrder);
			if(UtilValidate.isNotEmpty(listFilterAgreementAndOrder)){
				agreementAndOrder = EntityUtil.getFirst(listFilterAgreementAndOrder);
			}
			try {
				dispathcher.runSync("updateAgreement", context);
				
				mapParameters.put("agreementId", agreementId);
				mapParameters.put("partyId", partyIdFrom);
				mapParameters.put("contactMechId", addressIdFrom);
				mapParameters.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				try {
					List<GenericValue> fromAddress = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(mapParameters), null, null, null, false);
					fromAddress = EntityUtil.filterByDate(fromAddress);
					if (fromAddress.isEmpty()){
						List<GenericValue> fromOldAddress = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "partyId", partyIdFrom, "contactMechPurposeTypeId", "PRIMARY_LOCATION")), null, null, null, false);
						if (!fromOldAddress.isEmpty()){
							for (GenericValue add : fromOldAddress){
								add.put("thruDate", UtilDateTime.nowTimestamp());
								delegator.store(add);
							}
						}
						mapParameters.put("userLogin", userLogin);
						dispathcher.runSync("createAgreementPartyCTMPurpose", mapParameters);
					}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				
				mapParameters = new FastMap<String, Object>();
				mapParameters.put("agreementId", agreementId);
				mapParameters.put("partyId", partyIdFrom);
				mapParameters.put("contactMechId", telephoneIdFrom);
				mapParameters.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
				try {
					List<GenericValue> fromAddress = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(mapParameters), null, null, null, false);
					fromAddress = EntityUtil.filterByDate(fromAddress);
					if (fromAddress.isEmpty()){
						List<GenericValue> fromOldAddress = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "partyId", partyIdFrom, "contactMechPurposeTypeId", "PRIMARY_PHONE")), null, null, null, false);
						if (!fromOldAddress.isEmpty()){
							for (GenericValue add : fromOldAddress){
								add.put("thruDate", UtilDateTime.nowTimestamp());
								delegator.store(add);
							}
						}
						mapParameters.put("userLogin", userLogin);
						dispathcher.runSync("createAgreementPartyCTMPurpose", mapParameters);
					}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}

				mapParameters = new FastMap<String, Object>();
				mapParameters.put("agreementId", agreementId);
				mapParameters.put("partyId", partyIdFrom);
				mapParameters.put("contactMechId", faxNumberIdFrom);
				mapParameters.put("contactMechPurposeTypeId", "FAX_NUMBER");
				mapParameters.put("fromDate", fromDate);
				try {
					List<GenericValue> fromAddress = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(mapParameters), null, null, null, false);
					fromAddress = EntityUtil.filterByDate(fromAddress);
					if (fromAddress.isEmpty()){
						List<GenericValue> fromOldAddress = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "partyId", partyIdFrom, "contactMechPurposeTypeId", "FAX_NUMBER")), null, null, null, false);
						if (!fromOldAddress.isEmpty()){
							for (GenericValue add : fromOldAddress){
								add.put("thruDate", UtilDateTime.nowTimestamp());
								delegator.store(add);
							}
						}
						mapParameters.put("userLogin", userLogin);
						dispathcher.runSync("createAgreementPartyCTMPurpose", mapParameters);
					}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}

				mapParameters = new FastMap<String, Object>();
				mapParameters.put("agreementId", agreementId);
				mapParameters.put("partyId", partyIdTo);
				mapParameters.put("contactMechId", addressIdTo);
				mapParameters.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				mapParameters.put("fromDate", fromDate);
				try {
					List<GenericValue> fromAddress = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(mapParameters), null, null, null, false);
					fromAddress = EntityUtil.filterByDate(fromAddress);
					if (fromAddress.isEmpty()){
						List<GenericValue> fromOldAddress = delegator.findList("AgreementPartyCTMPurpose", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "partyId", partyIdTo, "contactMechPurposeTypeId", "PRIMARY_LOCATION")), null, null, null, false);
						if (!fromOldAddress.isEmpty()){
							for (GenericValue add : fromOldAddress){
								add.put("thruDate", UtilDateTime.nowTimestamp());
								delegator.store(add);
							}
						}
						mapParameters.put("userLogin", userLogin);
						dispathcher.runSync("createAgreementPartyCTMPurpose", mapParameters);
					}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}

				mapParameters = new FastMap<String, Object>();
				mapParameters.put("agreementId", agreementId);
				mapParameters.put("partyId", partyIdTo);
				mapParameters.put("contactMechId", emailAddressIdTo);
				mapParameters.put("contactMechTypeId", "EMAIL_ADDRESS");
				mapParameters.put("fromDate", fromDate);
				try {
					List<GenericValue> fromAddress = delegator.findList("AgreementPartyContactMech", EntityCondition.makeCondition(mapParameters), null, null, null, false);
					fromAddress = EntityUtil.filterByDate(fromAddress);
					if (fromAddress.isEmpty()){
						List<GenericValue> fromOldAddress = delegator.findList("AgreementPartyContactMech", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "partyId", partyIdFrom, "contactMechTypeId", "EMAIL_ADDRESS")), null, null, null, false);
						if (!fromOldAddress.isEmpty()){
							for (GenericValue add : fromOldAddress){
								add.put("thruDate", UtilDateTime.nowTimestamp());
								delegator.store(add);
							}
						}
						mapParameters.put("userLogin", userLogin);
						dispathcher.runSync("createAgreementPartyContactMech", mapParameters);
					}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
				
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Map<String, Object> resultTmp = dispathcher.runSync("createAgreement", context);
				agreementId = (String)resultTmp.get("agreementId");
				
				mapParameters.put("agreementId", agreementId);
				mapParameters.put("partyId", partyIdFrom);
				mapParameters.put("contactMechId", addressIdFrom);
				mapParameters.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				mapParameters.put("userLogin", userLogin);
				mapParameters.put("userLogin", userLogin);
				mapParameters.put("fromDate", fromDate);
				dispathcher.runSync("createAgreementPartyCTMPurpose", mapParameters);
				
				mapParameters = new FastMap<String, Object>();
				mapParameters.put("agreementId", agreementId);
				mapParameters.put("partyId", partyIdFrom);
				mapParameters.put("contactMechId", telephoneIdFrom);
				mapParameters.put("contactMechPurposeTypeId", "PRIMARY_PHONE");
				mapParameters.put("userLogin", userLogin);
				mapParameters.put("fromDate", fromDate);
				dispathcher.runSync("createAgreementPartyCTMPurpose", mapParameters);
				
				mapParameters = new FastMap<String, Object>();
				mapParameters.put("agreementId", agreementId);
				mapParameters.put("partyId", partyIdFrom);
				mapParameters.put("contactMechId", faxNumberIdFrom);
				mapParameters.put("contactMechPurposeTypeId", "FAX_NUMBER");
				mapParameters.put("userLogin", userLogin);
				mapParameters.put("fromDate", fromDate);
				dispathcher.runSync("createAgreementPartyCTMPurpose", mapParameters);
				
				mapParameters = new FastMap<String, Object>();
				mapParameters.put("agreementId", agreementId);
				mapParameters.put("partyId", partyIdTo);
				mapParameters.put("contactMechId", addressIdTo);
				mapParameters.put("contactMechPurposeTypeId", "PRIMARY_LOCATION");
				mapParameters.put("userLogin", userLogin);
				mapParameters.put("fromDate", fromDate);
				dispathcher.runSync("createAgreementPartyCTMPurpose", mapParameters);
				
				mapParameters = new FastMap<String, Object>();
				mapParameters.put("agreementId", agreementId);
				mapParameters.put("partyId", partyIdTo);
				mapParameters.put("contactMechId", emailAddressIdTo);
				mapParameters.put("contactMechTypeId", "EMAIL_ADDRESS");
				mapParameters.put("userLogin", userLogin);
				mapParameters.put("fromDate", fromDate);
				dispathcher.runSync("createAgreementPartyContactMech", mapParameters);
			} catch (GenericServiceException e) {
				e.printStackTrace();
			}
		}
		
		updateAgreementTerm(delegator, agreementId, "TRANS_SHIPMENT", transshipment);
		updateAgreementTerm(delegator, agreementId, "PARTIAL_SHIPMENT", partialShipment);
		
		if (finAccountIdFroms != null && !finAccountIdFroms.isEmpty()){
			try {
				List<GenericValue> listFinAccTerm = delegator.findList("AgreementTerm", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "FIN_ACC_TERM")), null, null, null, false);
				listFinAccTerm = EntityUtil.filterByDate(listFinAccTerm);
				if(!listFinAccTerm.isEmpty()){
					for(GenericValue term : listFinAccTerm){
						GenericValue finAcc = delegator.findOne("FinAccount", false, UtilMisc.toMap("finAccountId", term.getString("textValue")));
						if (finAcc != null){
							String organizationPartyId = (String)finAcc.get("organizationPartyId");
							if(partyIdFrom.equals(organizationPartyId) && !finAccountIdFroms.contains(term.getString("textValue"))){
								term.put("thruDate", UtilDateTime.nowTimestamp());
								delegator.store(term);
							}else if(partyIdFrom.equals(organizationPartyId) && finAccountIdFroms.contains(term.getString("textValue"))){
								finAccountIdFroms.remove(term.getString("textValue"));
							}
						}
					}
				}
				if(!finAccountIdFroms.isEmpty()){
					for (String finAccId : finAccountIdFroms){
						GenericValue currentTerm = delegator.makeValue("AgreementTerm");
						currentTerm.put("agreementTermId", delegator.getNextSeqId("AgreementTerm"));
						currentTerm.put("termTypeId", "FIN_ACC_TERM");
						currentTerm.put("agreementId", agreementId);
						currentTerm.put("textValue", finAccId);
						currentTerm.put("fromDate", UtilDateTime.nowTimestamp());
						delegator.createOrStore(currentTerm);
					}
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Map<String, Object> mapReturnErr = ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "BankAccountNotFoundOrNotSelectedYet", locale));
			return mapReturnErr;
		}
		
		if (finAccountIdTos != null && !finAccountIdTos.isEmpty()){
			try {
				List<GenericValue> listFinAccTerm = delegator.findList("AgreementTerm", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", "FIN_ACC_TERM")), null, null, null, false);
				listFinAccTerm = EntityUtil.filterByDate(listFinAccTerm);
				if(!listFinAccTerm.isEmpty()){
					for(GenericValue term : listFinAccTerm){
						GenericValue finAcc = delegator.findOne("FinAccount", false, UtilMisc.toMap("finAccountId", term.getString("textValue")));
						if (finAcc != null){
							String organizationPartyId = (String)finAcc.get("organizationPartyId");
							if(partyIdTo.equals(organizationPartyId) && !finAccountIdTos.contains(term.getString("textValue"))){
								term.put("thruDate", UtilDateTime.nowTimestamp());
								delegator.store(term);
							}else if(partyIdTo.equals(organizationPartyId) && finAccountIdTos.contains(term.getString("textValue"))){
								finAccountIdTos.remove(term.getString("textValue"));
							}
						}
					}
				}
				if(!finAccountIdTos.isEmpty()){
					for (String finAccId : finAccountIdTos){
						GenericValue currentTerm = delegator.makeValue("AgreementTerm");
						currentTerm.put("agreementTermId", delegator.getNextSeqId("AgreementTerm"));
						currentTerm.put("termTypeId", "FIN_ACC_TERM");
						currentTerm.put("agreementId", agreementId);
						currentTerm.put("textValue", finAccId);
						currentTerm.put("fromDate", UtilDateTime.nowTimestamp());
						delegator.createOrStore(currentTerm);
					}
				}
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			Map<String, Object> mapReturnErr = ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "BankAccountNotFoundOrNotSelectedYet", locale));
			return mapReturnErr;
		}
		
		if (agreementName != null){
			GenericValue agrName = delegator.makeValue("AgreementAttribute");
			agrName.put("agreementId", agreementId);
			agrName.put("attrName", "AGREEMENT_NAME");
			agrName.put("attrValue", agreementName);
			try {
				delegator.createOrStore(agrName);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		}
		if (representPartyIdFrom != null){
			GenericValue agreementRole = delegator.makeValue("AgreementRole");
			agreementRole.put("partyId", representPartyIdFrom);
			agreementRole.put("agreementId", agreementId);
			agreementRole.put("roleTypeId", "REPRESENT_LEGAL");
			try {
				delegator.createOrStore(agreementRole);
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		} else {
			Map<String, Object> mapReturnErr = ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "RepresentNotFound", locale));
			return mapReturnErr;
		}
		if (portOfDischargeId != null){
			updateAgreementTerm(delegator, agreementId, "PORT_OF_CHARGE", portOfDischargeId);
		} else {
			Map<String, Object> mapReturnErr = ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "PortOfDischargeNotFound", locale));
			return mapReturnErr;
		}
		if (weekETA != null){
			updateAgreementTerm(delegator, agreementId, "ETA_AGREEMENT_TERM", weekETA);
		}
		if (weekETD != null){
			updateAgreementTerm(delegator, agreementId, "ETD_AGREEMENT_TERM", weekETD);
		}
		
		if (currencyUomId != null){
			updateAgreementTerm(delegator, agreementId, "DEFAULT_PAY_CURRENCY", currencyUomId);
			if (!currencyUomIds.isEmpty()){
				for (String uomId : currencyUomIds){
					if (!uomId.equals(currencyUomId)){
						updateAgreementTerm(delegator, agreementId, "OTHER_PAY_CURRENCY", uomId);
					}
				}
			}
		} else {
			if (!currencyUomIds.isEmpty()){
				updateAgreementTerm(delegator, agreementId, "DEFAULT_PAY_CURRENCY", currencyUomIds.get(0));
				currencyUomIds.remove(0);
				if (!currencyUomIds.isEmpty()){
					for (String uomId : currencyUomIds){
						updateAgreementTerm(delegator, agreementId, "OTHER_PAY_CURRENCY", uomId);
					}
				}
			}
		}
		
		try {
			if(agreementAndOrder == null){
				ShoppingCartHelper helper = new ShoppingCartHelper(delegator, dispatcher, cart);
//				List<GenericValue> productPlanItemByLots = delegator.findList("ProductPlanAndLot", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId, "lotId", lotId)), null, null, null, false);
				Map<String, Object> contextMap = FastMap.newInstance();
				int size = arrOrderItems.size();
				for(int i=0; i < size; i++){
					JSONObject orderItem = arrOrderItems.getJSONObject(i);
//				}
					BigDecimal price = new BigDecimal(0);
					BigDecimal amount = new BigDecimal(0);
					String priceStr = orderItem.getString("unitPrice");
					NumberFormat num = NumberFormat.getInstance(locale);
					double mynb = num.parse(priceStr).doubleValue();
					price = new BigDecimal(mynb);
					price = price.setScale(2, BigDecimal.ROUND_HALF_UP);
					String amountStr = orderItem.getString("amount");
					double amountnb = num.parse(amountStr).doubleValue();
					amount = new BigDecimal(amountnb);
					amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
	    			String quantityStr = (String)orderItem.get("lotQuantity");
	    			BigDecimal quantity = new BigDecimal(Integer.parseInt(quantityStr));
//		    			GenericValue planItem = delegator.findOne("ProductPlanItem", false, UtilMisc.toMap("productPlanId", (String)item.get("productPlanId"), "productPlanItemSeqId", (String)item.get("productPlanItemSeqId")));
	    			String productId = (String)orderItem.get("productId");
	    			GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
	    			String productCategoryId = null;
	    			String catalogId = null;
	    			List<String> orderBy = new ArrayList<String>();
    				orderBy.add("-fromDate");
	    			if ((String)product.get("primaryProductCategoryId") != null){
	    				productCategoryId = (String)product.get("primaryProductCategoryId");
	    			} else {
	    				List<GenericValue> listCategoryByProducts = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, orderBy, null, false);
	    				listCategoryByProducts = EntityUtil.filterByDate(listCategoryByProducts);
	    				if (!listCategoryByProducts.isEmpty()){
	    					productCategoryId = (String)EntityUtil.getFirst(listCategoryByProducts).get("productCategoryId");
	    				} else {
	    					// to do 
	    				}
	    			}
	    			List<GenericValue> listCatalogCategorys = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryId)), null, orderBy, null, false);
	    			listCatalogCategorys = EntityUtil.filterByDate(listCatalogCategorys);
	    			if (!listCatalogCategorys.isEmpty()){
	    				catalogId = (String)EntityUtil.getFirst(listCatalogCategorys).get("prodCatalogId");
	    			} else {
	    				String productCategoryIdTmp = productCategoryId;
	    				while (listCatalogCategorys.isEmpty()){
	    					listCatalogCategorys = new ArrayList<GenericValue>();
	    					List<GenericValue> listCategoryParents = delegator.findList("ProductCategoryRollup", EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryIdTmp)), null, orderBy, null, false);
	    					listCategoryParents = EntityUtil.filterByDate(listCategoryParents);
	    	    			if (!listCategoryParents.isEmpty()){
	    	    				productCategoryIdTmp = (String)EntityUtil.getFirst(listCategoryParents).get("parentProductCategoryId");
	    	    				listCatalogCategorys = delegator.findList("ProdCatalogCategory", EntityCondition.makeCondition(UtilMisc.toMap("productCategoryId", productCategoryIdTmp)), null, orderBy, null, false);
	    	    				listCatalogCategorys = EntityUtil.filterByDate(listCatalogCategorys);
	    	    			} else {
	    	    				// to do
	    	    				break;
	    	    			}
	    				}
	    			}
	    			if (!listCatalogCategorys.isEmpty()){
	    				catalogId = (String)EntityUtil.getFirst(listCatalogCategorys).get("prodCatalogId");
	    			}
	    			helper.addToCart(catalogId, null, null, productId, productCategoryId, "PRODUCT_ORDER_ITEM", null, null, amount, quantity, null, null, null, null, null, shipBeforeDate, shipAfterDate, null, null, context, null);
//		    			item.put("statusId", "LOT_ORDERED");
//		    			delegator.store(item);
	    			contextMap.put("update_"+(size-i-1), quantity.toString());
	    			contextMap.put("price_"+(size-i-1), priceStr);
	    			contextMap.put("itemType_"+(size-i-1), "PRODUCT_ORDER_ITEM");
				}
					contextMap.put("finalizeReqAdditionalParty", false);
					contextMap.put("finalizeReqOptions", false);
					contextMap.put("removeSelected", false);
					contextMap.put("finalizeReqPayInfo", false);
					helper.modifyCart(security, userLogin, contextMap, false, null, locale);
		    		CheckOutHelper checkOutHelper = new CheckOutHelper(dispatcher, delegator, cart);
	    	        Map<String, Object> callResult;
	    	        checkOutHelper.finalizeOrderEntryShip(0, contactMechId, partyIdTo);
	    	        checkOutHelper.finalizeOrderEntryOptions(0, "STANDARD@_NA_", null, "false", null, "false", null, shipBeforeDate.toString(), shipAfterDate.toString(), null, null);
	    	        try {
	    	        	//tinh thue
						checkOutHelper.calcAndAddTax();
					} catch (GeneralException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	    	        boolean areOrderItemsExploded = CheckOutEvents.explodeOrderItems(delegator, cart);
	    	        
	    	        callResult = checkOutHelper.createOrderChangeSuppPrice(userLogin, null, null, null, areOrderItemsExploded, null, null, "false");
	                String orderId = (String)callResult.get("orderId");
	    	        if (productPlanId != null){
	    	        	try {
	//    	        		thruDate chua co, doan sau viet lai 1 service rieng dde thuc hien viec update bang ProductPlanAndOrder
	    					GenericValue planAndOrder = delegator.makeValue("ProductPlanAndOrder");
	    					planAndOrder.put("orderId", orderId);
	    					planAndOrder.put("productPlanId", productPlanId);
	    					planAndOrder.put("fromDate", UtilDateTime.nowTimestamp());
	//    					planAndOrder.put("thruDate", thruDate);
	    					planAndOrder.put("agreementId", agreementId);
	    					planAndOrder.put("lotId", lotId);
	    					delegator.createOrStore(planAndOrder);
	    				} catch (GenericEntityException e) {
	    					e.printStackTrace();
	    				}
	    	        }
	    	        try {
						GenericValue agreementAndOrderNew = delegator.makeValue("AgreementAndOrder");
						agreementAndOrderNew.put("orderId", orderId);
						agreementAndOrderNew.put("agreementId", agreementId);
						agreementAndOrderNew.put("fromDate", UtilDateTime.nowTimestamp());
						delegator.createOrStore(agreementAndOrderNew);
					} catch (GenericEntityException e) {
						e.printStackTrace();
					}
//				}
				cart.clear();
			}else{//update order
				int size = arrOrderItems.size();
				for(int i=0; i < size; i++){
					JSONObject orderItem = arrOrderItems.getJSONObject(i);
					String purchaseOrderId = orderItem.getString("orderId");
					String orderItemSeqId = orderItem.getString("orderItemSeqId");
					String quantityStr = (String)orderItem.get("lotQuantity");
	    			BigDecimal quantity = new BigDecimal(Integer.parseInt(quantityStr));
	    			String productId = (String)orderItem.get("productId");
	    			String priceStr = orderItem.getString("unitPrice");
//					String amountStr = orderItem.getString("amount");
					Map<String,Object> contextTmpOrderItem = new HashMap<String, Object>();
					contextTmpOrderItem.put("orderId", purchaseOrderId);
					contextTmpOrderItem.put("orderItemSeqId", orderItemSeqId);
					contextTmpOrderItem.put("quantity", quantity);
					contextTmpOrderItem.put("productId", productId);
					contextTmpOrderItem.put("userLogin", userLogin);
					contextTmpOrderItem.put("overidePriceMap", "Y");
					contextTmpOrderItem.put("itemPriceMap", priceStr);
					try {
						Map<String, Object> orderItemMap = dispatcher.runSync("updateOrderItemFromPackingList", contextTmpOrderItem);
					} catch (GenericServiceException e) {
		//				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("agreementId", agreementId);
		result.put("productPlanId", productPlanId);
		return result;
	}

	public static void updateProductPlanItem(Delegator delegator, GenericValue planItem, BigDecimal quantity){
		BigDecimal orderedQuantity = quantity;
		BigDecimal recentPlanQuantity = quantity;
		if(planItem.getBigDecimal("orderedQuantity") !=null){
			orderedQuantity = planItem.getBigDecimal("orderedQuantity").add(quantity);
		}
//		if(planItem.getBigDecimal("recentPlanQuantity") != null){
//			recentPlanQuantity = planItem.getBigDecimal("recentPlanQuantity").add(quantity);
//		}
		
		planItem.put("orderedQuantity", orderedQuantity);
//		planItem.put("recentPlanQuantity", recentPlanQuantity);
		try {
			delegator.store(planItem);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static Map<String, Object> loadCustomTimePeriod(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
	    Delegator delegator = ctx.getDelegator();
	    
	//    Date fromDate = (Date) context.get("fromDate");
	//    Date thruDate = (Date) context.get("thruDate");
	
	//    SimpleDateFormat monthNameFormat = new SimpleDateFormat("MMMM");
	//    SimpleDateFormat dayNameFormat = new SimpleDateFormat("EEEE");
	//    SimpleDateFormat dayDescriptionFormat = new SimpleDateFormat("MMMM d, yyyy");
	    SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd");
	//    SimpleDateFormat yearMonthFormat = new SimpleDateFormat("yyyy-MM");
	    
	    Date fromDate = (Date)yearMonthDayFormat.parse("2014-01-01 00:00:00.000");
	    Date thruDate = (Date)yearMonthDayFormat.parse("2015-01-01 00:00:00.000");
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(fromDate);
	    calendar.set(Calendar.HOUR, 0);
	    calendar.set(Calendar.MINUTE, 0);
	    calendar.set(Calendar.SECOND, 0);
	    calendar.set(Calendar.MILLISECOND, 0);
	    java.sql.Date currentDate = new java.sql.Date(calendar.getTimeInMillis());
	    Calendar calendarThru = Calendar.getInstance();
	    calendarThru.setTime(thruDate);
	    calendarThru.set(Calendar.HOUR, 0);
	    calendarThru.set(Calendar.MINUTE, 0);
	    calendarThru.set(Calendar.SECOND, 0);
	    calendarThru.set(Calendar.MILLISECOND, 0);
	    java.sql.Date thru = new java.sql.Date(calendarThru.getTimeInMillis());
	    //year custom time
	    int year = 0;
	//    java.sql.Date firstYear = currentDate;
	    int month = 100;
	//    java.sql.Date firstMonth = currentDate;
	    int week = calendar.get(Calendar.WEEK_OF_YEAR);
	    java.sql.Date firstWeek = currentDate;
	    String yearIdSeq="";
	    int w = 0;
	    String lastYearSeq = "";
	    while (currentDate.compareTo(thru) <= 0) {
	        if(year != calendar.get(Calendar.YEAR)){
	             GenericValue yearValue = delegator.makeValue("CustomTimePeriod");
	             lastYearSeq = yearIdSeq;
	             yearIdSeq = delegator.getNextSeqId("CustomTimePeriod");
	             yearValue.set("customTimePeriodId", yearIdSeq);
	             yearValue.set("periodTypeId", "IMPORT_YEAR");
	             yearValue.set("periodNum", new Long("1"));
	             yearValue.set("periodName", Integer.toString(calendar.get(Calendar.YEAR)));
	             yearValue.set("fromDate", currentDate);
	             calendar.add(Calendar.YEAR, 1);
	             currentDate = new java.sql.Date(calendar.getTimeInMillis());
	             yearValue.set("thruDate", currentDate);
	             yearValue.set("organizationPartyId", "company");
	             try {
	            	 yearValue.create();
	             } catch (GenericEntityException gee) {
	                 return ServiceUtil.returnError(gee.getMessage());
	             }
	             calendar.add(Calendar.YEAR, -1);
	             currentDate = new java.sql.Date(calendar.getTimeInMillis());
	             year = calendar.get(Calendar.YEAR);
	//             firstYear = currentDate;
	        }
	        if(month != calendar.get(Calendar.MONTH)){
	            GenericValue monthValue = delegator.makeValue("CustomTimePeriod");
	            monthValue.set("customTimePeriodId", delegator.getNextSeqId("CustomTimePeriod"));
	            monthValue.set("parentPeriodId", yearIdSeq);
	            monthValue.set("periodTypeId", "IMPORT_MONTH");
	            monthValue.set("periodNum", new Long("1"));
	            monthValue.set("periodName", Integer.toString(calendar.get(Calendar.MONTH)+1));
	            monthValue.set("fromDate", currentDate);
	            calendar.add(Calendar.MONTH, 1);
	            currentDate = new java.sql.Date(calendar.getTimeInMillis());
	            monthValue.set("thruDate", currentDate);
	            monthValue.set("organizationPartyId", "company");
	            try {
	            	monthValue.create();
	            } catch (GenericEntityException gee) {
	                return ServiceUtil.returnError(gee.getMessage());
	            }
	            calendar.add(Calendar.MONTH, -1);
	            currentDate = new java.sql.Date(calendar.getTimeInMillis());
	            month = calendar.get(Calendar.MONTH);
	//            firstMonth = currentDate;
	       }
	        
	        if(week != calendar.get(Calendar.WEEK_OF_YEAR) || (currentDate.compareTo(thru) >=0)){
	            GenericValue weekValue = delegator.makeValue("CustomTimePeriod");
	            weekValue.set("customTimePeriodId", delegator.getNextSeqId("CustomTimePeriod"));
	            weekValue.set("periodTypeId", "IMPORT_WEEK");
	            weekValue.set("periodNum", new Long("1"));
	            if(currentDate.compareTo(thru) == 0){
	                weekValue.set("parentPeriodId", lastYearSeq);
	            	int tem = w + 1;
	            	weekValue.set("periodName", Integer.toString(tem));
	            }else{
	                weekValue.set("parentPeriodId", yearIdSeq);
	            weekValue.set("periodName", Integer.toString(week));
	            }
	            weekValue.set("fromDate", firstWeek);
	            weekValue.set("thruDate", currentDate);
	            weekValue.set("organizationPartyId", "company");
	            try {
	            	weekValue.create();
	            } catch (GenericEntityException gee) {
	                return ServiceUtil.returnError(gee.getMessage());
	            }
	            w = week;
	            week = calendar.get(Calendar.WEEK_OF_YEAR);
	            firstWeek = currentDate;
	       }
	        calendar.add(Calendar.DATE, 1);
	        currentDate = new java.sql.Date(calendar.getTimeInMillis());
	    }
	    
	    return ServiceUtil.returnSuccess();
	}
//END HISTORY INVENTORY
	public static Map<String, Object> getChildUoms(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		String uomId = (String)context.get("uomId");
		String uomTypeId = (String)context.get("uomTypeId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listChildUoms = new ArrayList<GenericValue>();
		List<GenericValue> listUomConverts = new ArrayList<GenericValue>();
		try {
			listUomConverts = delegator.findList("UomConversionDated", EntityCondition.makeCondition(UtilMisc.toMap("uomId", uomId)), null, null, null, false);
			listUomConverts = EntityUtil.filterByDate(listUomConverts);
			if (!listUomConverts.isEmpty()){
				for (GenericValue uomItem : listUomConverts){
					GenericValue uom = delegator.findOne("Uom", false, UtilMisc.toMap("uomId", uomItem.get("uomIdTo")));
					if (uom != null){
						String uomTypeStr = (String)uom.get("uomTypeId");
						if (uomTypeStr.equals(uomTypeId) && !listChildUoms.contains(uom)){
							listChildUoms.add(uom);
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("uomId", uomId);
		result.put("listChildUoms", listChildUoms);
		return result;
	}
	public static Map<String, Object> divideProductPlanToLot(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		String productPlanId = (String)context.get("productPlanId");
		String shipmentUomId = (String)context.get("shipmentUomId");
		String productPackingUomId = (String)context.get("productPackingUomId");
		Locale locale = (Locale)context.get("locale");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listPlanItems = new ArrayList<GenericValue>();
		try {
			List<GenericValue> listUomConverTmp = delegator.findList("UomConversionDated", EntityCondition.makeCondition(UtilMisc.toMap("uomId", shipmentUomId, "uomIdTo", productPackingUomId)), null, null, null, false);
			listUomConverTmp = EntityUtil.filterByDate(listUomConverTmp);
			BigDecimal conversionFactor = BigDecimal.ZERO; 
			if (!listUomConverTmp.isEmpty()){
				GenericValue conversionDated = EntityUtil.getFirst(listUomConverTmp);
				conversionFactor = new BigDecimal(conversionDated.getDouble("conversionFactor"));
			} else {
				Map<String, Object> mapReturnErr = ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                    "NoConversionFound", locale));
				return mapReturnErr;
			}
			listPlanItems = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId, "statusId", "PLAN_ITEM_APPROVED")), null, null, null, false);
			if (!listPlanItems.isEmpty()){
				Map<String, Map<String, BigDecimal>> mapProductConvertNumber = new FastMap<String, Map<String, BigDecimal>>();
				List<GenericValue> listItemImported = new ArrayList<GenericValue>();
				for (GenericValue item : listPlanItems){
					String productId = (String)item.get("productId");
					GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					String uomBase = (String)product.get("quantityUomId");
					BigDecimal quantityToImport = item.getBigDecimal("planQuantity").subtract(item.getBigDecimal("recentPlanQuantity").subtract(item.getBigDecimal("orderedQuantity")));
					if (quantityToImport.compareTo(BigDecimal.ZERO) == 1){
						List<String> listUomToConvert = new ArrayList<String>();
						listUomToConvert = getListUomToConvert(delegator, productId, productPackingUomId, uomBase);
						BigDecimal convert = BigDecimal.ONE;
						try {
							List<GenericValue> listUomPackingProducts = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
							listUomPackingProducts = EntityUtil.filterByDate(listUomPackingProducts, null, "fromDate", "thruDate", false);
							if (listUomPackingProducts.isEmpty()){
								Map<String, Object> mapReturnErr = ServiceUtil.returnError(UtilProperties.getMessage(resource, "NoConfigPacking", locale));
								return mapReturnErr;
							}
							List<GenericValue> listConfigByProducts = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomFromId", productPackingUomId)), null, null, null, false);
							listConfigByProducts = EntityUtil.filterByDate(listConfigByProducts, UtilDateTime.nowTimestamp(), "fromDate", "thruDate", true);
							if (!listConfigByProducts.isEmpty()){
								// get convert in ConfigPacking
								convert = getProductConvertNumber(delegator, convert, productId, productPackingUomId, uomBase, listUomToConvert);
							} else {
								convert = BigDecimal.ZERO;
							}
							if (convert.compareTo(BigDecimal.ZERO) == 1){
								BigDecimal numberOfProductPacking = quantityToImport.divide(convert, 0, RoundingMode.HALF_UP);
								Map<String, BigDecimal> mapTemp = new FastMap<String, BigDecimal>();
								mapTemp.put("convertNumber", convert);
								mapTemp.put("numberOfProductPacking", numberOfProductPacking);
								mapProductConvertNumber.put(productId, mapTemp);
							} else {
								// get convert in UomConversionDated
							}
						} catch (GenericEntityException e) {
							e.printStackTrace();
						}
					} else {
						listItemImported.add(item);
					}
				}
				listPlanItems.removeAll(listItemImported);
				for (GenericValue item : listPlanItems){
					Map<String, BigDecimal> mapTemp = mapProductConvertNumber.get((String)item.get("productId"));
					if (mapTemp != null){
						while (mapTemp.get("numberOfProductPacking").compareTo(conversionFactor) >= 0){
							BigDecimal lotQuantity = conversionFactor.multiply(mapTemp.get("convertNumber"));
							String lotId = delegator.getNextSeqId("Lot");
							GenericValue lot = delegator.makeValue("Lot");
							lot.put("lotId", lotId);
							lot.put("creationDate", UtilDateTime.nowTimestamp());
							delegator.createOrStore(lot);
							GenericValue lotPlan = delegator.makeValue("ProductPlanAndLot");
							lotPlan.put("lotId", lotId);
							lotPlan.put("productPlanId", item.get("productPlanId"));
							lotPlan.put("productPlanItemSeqId", item.get("productPlanItemSeqId"));
							lotPlan.put("lotQuantity", lotQuantity);
							lotPlan.put("shipmentUomId", shipmentUomId);
							lotPlan.put("productPackingUomId", productPackingUomId);
							lotPlan.put("statusId", "LOT_CREATED");
							delegator.createOrStore(lotPlan);
							BigDecimal oldQty = mapTemp.get("numberOfProductPacking"); 
							mapTemp.put("numberOfProductPacking", oldQty.subtract(conversionFactor));
						}
						mapProductConvertNumber.put((String)item.get("productId"), mapTemp);
					}
				}
				List<GenericValue> listItemCompleted = new ArrayList<GenericValue>();
				for (GenericValue item : listPlanItems){
					Map<String, BigDecimal> mapTemp = mapProductConvertNumber.get((String)item.get("productId"));
					if (mapTemp != null){
						if (mapTemp.get("numberOfProductPacking").compareTo(BigDecimal.ZERO) == 0){
							mapProductConvertNumber.remove((String)item.get("productId"));
							listItemCompleted.add(item);
						}
					}
				}
				listPlanItems.removeAll(listItemCompleted);
				while(!mapProductConvertNumber.isEmpty()){
					for (GenericValue item : listPlanItems){
						String lotId = delegator.getNextSeqId("Lot");
						GenericValue lot = delegator.makeValue("Lot");
						lot.put("lotId", lotId);
						lot.put("creationDate", UtilDateTime.nowTimestamp());
						delegator.createOrStore(lot);
						String productIdTmp = (String)item.get("productId");
						Map<String, BigDecimal> mapTemp = mapProductConvertNumber.get(productIdTmp);
						if (mapTemp != null){
							BigDecimal numberPacking = mapTemp.get("numberOfProductPacking");
							if (conversionFactor.compareTo(numberPacking) == 1 && numberPacking.compareTo(BigDecimal.ZERO) == 1){
								GenericValue lotPlan = delegator.makeValue("ProductPlanAndLot");
								lotPlan.put("lotId", lotId);
								lotPlan.put("productPlanId", item.get("productPlanId"));
								lotPlan.put("productPlanItemSeqId", item.get("productPlanItemSeqId"));
								lotPlan.put("lotQuantity", numberPacking.multiply(mapTemp.get("convertNumber")));
								lotPlan.put("shipmentUomId", shipmentUomId);
								lotPlan.put("productPackingUomId", productPackingUomId);
								lotPlan.put("statusId", "LOT_CREATED");
								delegator.createOrStore(lotPlan);
								mapProductConvertNumber.remove(productIdTmp);
							}
							if (!mapProductConvertNumber.isEmpty()){
								for (GenericValue itemTmp : listPlanItems){
									String prId = (String)itemTmp.get("productId");
									if (!prId.equals(productIdTmp)){
										if (conversionFactor.compareTo(numberPacking) == 1){
											if (mapProductConvertNumber.get(prId) != null){
												if (conversionFactor.compareTo(numberPacking.add(mapProductConvertNumber.get(prId).get("numberOfProductPacking"))) == 1){
													numberPacking = numberPacking.add(mapProductConvertNumber.get(prId).get("numberOfProductPacking"));
													GenericValue lotPlan = delegator.makeValue("ProductPlanAndLot");
													lotPlan.put("lotId", lotId);
													lotPlan.put("productPlanId", itemTmp.get("productPlanId"));
													lotPlan.put("productPlanItemSeqId", itemTmp.get("productPlanItemSeqId"));
													lotPlan.put("lotQuantity", mapProductConvertNumber.get(prId).get("numberOfProductPacking").multiply(mapProductConvertNumber.get(prId).get("convertNumber")));
													lotPlan.put("shipmentUomId", shipmentUomId);
													lotPlan.put("productPackingUomId", productPackingUomId);
													lotPlan.put("statusId", "LOT_CREATED");
													delegator.createOrStore(lotPlan);
													mapProductConvertNumber.remove(prId);
												} else {
													GenericValue lotPlan = delegator.makeValue("ProductPlanAndLot");
													lotPlan.put("lotId", lotId);
													lotPlan.put("productPlanId", itemTmp.get("productPlanId"));
													lotPlan.put("productPlanItemSeqId", itemTmp.get("productPlanItemSeqId"));
													lotPlan.put("lotQuantity", conversionFactor.subtract(numberPacking).multiply(mapProductConvertNumber.get(prId).get("convertNumber")));
													lotPlan.put("shipmentUomId", shipmentUomId);
													lotPlan.put("productPackingUomId", productPackingUomId);
													lotPlan.put("statusId", "LOT_CREATED");
													delegator.createOrStore(lotPlan);
													
													Map<String, BigDecimal> mapTmp = mapProductConvertNumber.get(prId);
													mapTmp.put("numberOfProductPacking", mapProductConvertNumber.get(prId).get("numberOfProductPacking").subtract(conversionFactor.subtract(numberPacking)));
													mapProductConvertNumber.put(prId, mapTmp);
													numberPacking = conversionFactor;
												}
											}
										} 
									} 
								}
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		result.put("productPlanId", productPlanId);
		return result;
	}
	
	public static BigDecimal getProductConvertNumber(Delegator delegator, BigDecimal convert, String productId, String uomFromId, String uomToId, List<String> listUomToConvert){
		try {
			List<GenericValue> listConfigs = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId",productId, "uomToId", uomToId)), null, null, null, false);
			listConfigs = EntityUtil.filterByDate(listConfigs, UtilDateTime.nowTimestamp(), "fromDate", "thruDate", true);
			if (!listConfigs.isEmpty()){
				boolean check = false;
				String uomParentCur = null;
				for (GenericValue cf : listConfigs){
					if (listUomToConvert.contains((String)cf.get("uomFromId"))){
						convert = convert.multiply(cf.getBigDecimal("quantityConvert"));
						uomParentCur = (String)cf.get("uomFromId");
						check = true;
						break;
					}
				}
				if (check){
					if (!uomFromId.equals(uomParentCur)){
						convert = getProductConvertNumber(delegator, convert, productId, uomFromId, uomParentCur, listUomToConvert);
					} else {
						return convert;
					}
				} else {
					return BigDecimal.ONE;
				}
			} else {
				return BigDecimal.ONE;
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return convert;
	}
	
	public static List<String> getListUomToConvert(Delegator delegator, String productId, String uomFromId, String uomToId){
		Queue<String> listConfigs = new LinkedList<String>() ;
		List<String> listUomToConvert = new ArrayList<String>();
		listUomToConvert.add(uomToId);
		listConfigs.clear();
		listConfigs.add(uomToId);
        while(!listConfigs.isEmpty()) {
        	String uomCur = listConfigs.remove();
			try {
				if (!uomFromId.equals(uomCur)){
					List<GenericValue> listConfigParents = delegator.findList("ConfigPacking", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomToId", uomCur)), null, null, null, false);
		            if (!listConfigParents.isEmpty()){
						for(GenericValue cf : listConfigParents) {
			            	String uomParentId = (String)cf.get("uomFromId");
			                if(!listUomToConvert.contains(uomParentId)) {
			                	listConfigs.add(uomParentId);
			                	listUomToConvert.add(uomParentId);
			                }
			            }
		            } else {
		            	listUomToConvert.remove(uomCur);
		            }
				} else {
					break;
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
        }
        return listUomToConvert;
    }
	
	public static Map<String, Object> getConvertPackingNumber(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String productId = (String)context.get("productId");
		String uomFromId = (String)context.get("uomFromId");
		String uomToId = (String)context.get("uomToId");
		BigDecimal convert = BigDecimal.ONE;
		try {
			if (uomFromId.equals(uomToId)){
				result.put("convertNumber", convert);
				return result;
			} else {
				GenericValue config = delegator.findOne("ConfigPacking", false, UtilMisc.toMap("productId", productId, "uomFromId", uomFromId, "uomToId", uomToId));
				if (config != null){
					convert = config.getBigDecimal("quantityConvert");
					result.put("convertNumber", convert);
					return result;
				} else {
					GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
					String uomBase = (String)product.get("quantityUomId");
					convert = getConvertNumber(delegator, convert, productId, uomFromId, uomToId);
					if (convert.compareTo(BigDecimal.ZERO) == 0){
						List<String> listUomConvertFrom = getListUomToConvert(delegator, productId, uomFromId, uomBase);
						List<String> listUomConvertTo = getListUomToConvert(delegator, productId, uomToId, uomBase);
						listUomConvertFrom.remove(uomBase);
						listUomConvertTo.remove(uomBase);
						String uomChild = null;
						if (!listUomConvertFrom.isEmpty()){
							for (String uomId : listUomConvertFrom){
								if (listUomConvertTo.contains(uomId)){
									uomChild = uomId;
									break;
								}
							}
						}
						if (uomChild != null){
							BigDecimal convertFrom = BigDecimal.ONE;
							BigDecimal convertTo = BigDecimal.ONE;
							convertFrom = getConvertNumber(delegator, convertFrom, productId, uomFromId, uomChild);
							convertTo = getConvertNumber(delegator, convertTo, productId, uomToId, uomChild);
							if (convertTo.compareTo(BigDecimal.ZERO) == 1 && convertFrom.compareTo(BigDecimal.ZERO) == 1 && convertFrom.compareTo(convertTo) == 1){
								convert = convertFrom.divide(convertTo);
							} else {
								convert = new BigDecimal(-1);
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("convertNumber", convert);
		return result;
	}
	public static Map<String, Object> getProductByCategory (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String productCategoryId = (String)context.get("productCategoryId");
		Delegator delegator = ctx.getDelegator();
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("productId");
		List<GenericValue> listProduct = delegator.findList("Product", EntityCondition.makeCondition(UtilMisc.toMap("primaryProductCategoryId", productCategoryId)), null, orderBy, null, false);
		result.put("listProducts", listProduct);
		return result;
	}
	public static Map<String, Object> acceptReceiptRequirement(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String orderId = (String)context.get("orderId");
		String facilityId = (String)context.get("facilityId");
		String contactMechId = (String)context.get("contactMechId");
		
		try {
			GenericValue orderRequirement = delegator.findOne("OrderRequirement", false, UtilMisc.toMap("requirementId", requirementId, "orderId", orderId));
			if (orderRequirement != null){
				orderRequirement.put("statusId", "REQ_ACCEPTED");
				orderRequirement.put("facilityId", facilityId);
				orderRequirement.put("contactMechId", contactMechId);
				delegator.createOrStore(orderRequirement);
			}
			Map<String, Object> mapReq = new HashMap<String, Object>();
			mapReq.put("requirementId", requirementId);
			mapReq.put("statusId", "REQ_ACCEPTED");
			mapReq.put("userLogin", (GenericValue)context.get("userLogin"));
			LocalDispatcher dispatcher = ctx.getDispatcher();
			try {
				dispatcher.runSync("updateRequirement", mapReq);
			} catch (GenericServiceException e) {
				ServiceUtil.returnError("updateRequirement error");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("requirementId", requirementId);
		return result;
	}
	public static Map<String, Object> approveReceiptRequirement(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String orderId = (String)context.get("orderId");
		String action = (String)context.get("action");
		String sendMessage = (String)context.get("sendMessage");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue orderRequirement = delegator.findOne("OrderRequirement", false, UtilMisc.toMap("requirementId", requirementId, "orderId", orderId));
			if (orderRequirement != null){
				orderRequirement.put("statusId", "REQ_APPROVED");
				delegator.createOrStore(orderRequirement);
				Map<String, Object> mapContext = new HashMap<String, Object>();
				mapContext.put("requirementId", requirementId);
				mapContext.put("orderId", orderId);
				mapContext.put("action", action);
				mapContext.put("sendMessage", sendMessage);
				mapContext.put("userLogin", userLogin);
				try {
					dispatcher.runSync("sendReceiptRequirement", mapContext);
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}
				Map<String,Object> contextTmpOrder = new HashMap<String, Object>();
				contextTmpOrder.put("statusId", "ORDER_APPROVED");
				contextTmpOrder.put("setItemStatus", "Y");
				contextTmpOrder.put("orderId", orderId);
				contextTmpOrder.put("userLogin", userLogin);
				try {
					dispatcher.runSync("changeOrderStatus", contextTmpOrder);
				} catch (GenericServiceException e) {
					
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("requirementId", requirementId);
		return result;
	}
	public static Map<String, Object> sendReceiptRequirement(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String requirementId = (String)context.get("requirementId");
		String orderId = (String)context.get("orderId");
		String sendMessage = (String)context.get("sendMessage");
		String action = (String)context.get("action");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue orderRequirement = delegator.findOne("OrderRequirement", false, UtilMisc.toMap("requirementId", requirementId, "orderId", orderId));
			if (orderRequirement != null){
				orderRequirement.put("statusId", "REQ_PROPOSED");
				delegator.createOrStore(orderRequirement);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		try {
			List<String> listLogSpecialists = new ArrayList<String>();
			List<String> listPartyGroups = SecurityUtil.getPartiesByRoles("LOG_SPECIALIST", delegator);
			if (!listPartyGroups.isEmpty()){
				for (String group : listPartyGroups){
					try {
						List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", "LOG_SPECIALIST")), null, null, null, false);
						listManagers = EntityUtil.filterByDate(listManagers);
						if (!listManagers.isEmpty()){
							for (GenericValue manager : listManagers){
								listLogSpecialists.add(manager.getString("partyIdFrom"));
							}
						}
					} catch (GenericEntityException e) {
						ServiceUtil.returnError("get Party relationship error!");
					}
				}
			}
			if(!listLogSpecialists.isEmpty()){
				for (String managerParty : listLogSpecialists){
					String sendToPartyId = managerParty;
					Map<String, Object> mapContext = new HashMap<String, Object>();
					String targetLink = "statusId=REQ_PROPOSED";
					mapContext.put("partyId", sendToPartyId);
					mapContext.put("action", action);
					mapContext.put("targetLink", targetLink);
					mapContext.put("header", sendMessage);
					mapContext.put("userLogin", userLogin);
					dispatcher.runSync("createNotification", mapContext);
				}
			}
		} catch (GenericServiceException e) {
			ServiceUtil.returnError("createNotification Error");
		}
		result.put("requirementId", requirementId);
		return result;
	}
	public static Map<String, Object> updateReceiptRequirement(DispatchContext ctx, Map<String, Object> context) throws GenericTransactionException{
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String requirementId = (String)context.get("requirementId");
		String facilityId = (String)context.get("facilityId");
		String productStoreId = (String)context.get("productStoreId");
		String agreementId = (String)context.get("agreementId");
		String orderId = (String)context.get("orderId");
		Timestamp requirementDate = (Timestamp)context.get("requirementDate");
		GenericValue requestor = (GenericValue)context.get("userLogin");
		String requestorId = (String)requestor.get("partyId");
		if (requirementId == null){
			requirementId = delegator.getNextSeqId("Requirement");
		}
		boolean beganTx = TransactionUtil.begin(7200);
		try {
			GenericValue requirement = delegator.makeValue("Requirement");
			requirement.put("requirementId", requirementId);
			requirement.put("requirementTypeId", "RECEIVE_ORDER_REQ");
			requirement.put("statusId", "REQ_CREATED");
			requirement.put("requirementStartDate", UtilDateTime.nowTimestamp());
			requirement.put("requiredByDate", UtilDateTime.nowTimestamp());
			delegator.createOrStore(requirement);
			
			GenericValue agreementRequirement = delegator.makeValue("AgreementRequirement");
			agreementRequirement.put("requirementId", requirementId);
			agreementRequirement.put("agreementId", agreementId);
			delegator.createOrStore(agreementRequirement);
			
			if (orderId == null){
				List<GenericValue> listOrderByAgreements = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
				if (!listOrderByAgreements.isEmpty()){
					for (GenericValue order : listOrderByAgreements){
						GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", (String)order.get("orderId")));
						GenericValue orderRequirement = delegator.makeValue("OrderRequirement");
						orderRequirement.put("orderId", (String)order.get("orderId"));
						orderRequirement.put("requirementId", requirementId);
						orderRequirement.put("facilityId", facilityId);
						orderRequirement.put("agreementId", agreementId);
						orderRequirement.put("productStoreId", (String)orderHeader.get("productStoreId"));
						orderRequirement.put("statusId", "REQ_CREATED");
						orderRequirement.put("requirementDate", requirementDate);
						orderRequirement.put("fromDate", UtilDateTime.nowTimestamp());
						delegator.createOrStore(orderRequirement);
					}
				} else {
					TransactionUtil.rollback();
					return ServiceUtil.returnError(UtilProperties.getMessage(resource,
		                    "OrderNotFound", (Locale)context.get("locale")));
				}
			} else {
				GenericValue orderRequirement = delegator.makeValue("OrderRequirement");
				orderRequirement.put("orderId", orderId);
				orderRequirement.put("requirementId", requirementId);
				orderRequirement.put("facilityId", facilityId);
				orderRequirement.put("agreementId", agreementId);
				orderRequirement.put("productStoreId", productStoreId);
				orderRequirement.put("statusId", "REQ_CREATED");
				orderRequirement.put("requirementDate", requirementDate);
				orderRequirement.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.createOrStore(orderRequirement);
			}
			List<GenericValue> requirementRoles = delegator.findList("RequirementRole", EntityCondition.makeCondition(UtilMisc.toMap("partyId", requestorId, "requirementId", requirementId, "roleTypeId", "OWNER")), null, null, null, false);
			requirementRoles = EntityUtil.filterByDate(requirementRoles);
			if (requirementRoles.isEmpty()){
				GenericValue partyRole = delegator.makeValue("PartyRole");
				partyRole.put("partyId", requestorId);
				partyRole.put("roleTypeId", "OWNER");
				delegator.createOrStore(partyRole);
				GenericValue requirementRole = delegator.makeValue("RequirementRole");
				requirementRole.put("partyId", requestorId);
				requirementRole.put("requirementId", requirementId);
				requirementRole.put("roleTypeId", "OWNER");
				requirementRole.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.createOrStore(requirementRole);
			}
		} catch (GenericEntityException e) {
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			e.printStackTrace();
		}
		//sent notify to import admin before create requirement import
		LocalDispatcher dispatcher = ctx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> notifyParam = FastMap.newInstance();
		notifyParam.put("userLogin", userLogin);
		notifyParam.put("partyId", "importadmin");
		notifyParam.put("action", "getListReceiptRequirements");
		notifyParam.put("header", UtilProperties.getMessage(resource, "NewReceiptRequirement", (Locale)(context.get("locale"))));
        try {
			dispatcher.runSync("createNotification", notifyParam);
		} catch (GenericServiceException e) {
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			e.printStackTrace();
		}
        TransactionUtil.commit(beganTx);
		result.put("requirementId", requirementId);
		return result;
	}
	public static Map<String, Object> updateReceipt(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String receiptId = (String)context.get("receiptId");
		String requirementId = (String)context.get("requirementId");
		String agreementId = (String)context.get("agreementId");
		String orderId = (String)context.get("orderId");
		String productStoreId = (String)context.get("productStoreId");
		String facilityId = (String)context.get("facilityId");
		String contactMechId = (String)context.get("contactMechId");
		Long receiptDateLong = Long.parseLong((String)context.get("requirementDate"));
		Timestamp receiptDate = new Timestamp(receiptDateLong);
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = (String)userLogin.get("userLoginId");
		if (receiptId == null){
			receiptId = delegator.getNextSeqId("Receipt");
		}
		if (agreementId != null){
			try {
				GenericValue receipt = delegator.makeValue("Receipt");
				receipt.put("receiptId", receiptId);
				receipt.put("productStoreId", productStoreId);
				receipt.put("facilityId", facilityId);
				receipt.put("contactMechId", contactMechId);
				receipt.put("receiptDate", receiptDate);
				receipt.put("createDate", UtilDateTime.nowTimestamp());
				receipt.put("agreementId", agreementId);
				receipt.put("orderId", orderId);
				receipt.put("statusId", "RECEIPT_CREATED");
				receipt.put("createdByUserLogin", (String)userLogin.get("userLoginId"));
				delegator.createOrStore(receipt);
				
				List<GenericValue> receiptStatus = delegator.findList("ReceiptStatus", EntityCondition.makeCondition(UtilMisc.toMap("statusId", "RECEIPT_CREATED", "receiptId", receiptId)), null, null, null, false);
				if (!receiptStatus.isEmpty()){
					GenericValue oldReceiptStatus = receiptStatus.get(0);
					oldReceiptStatus.put("statusId", "RECEIPT_CREATED");
					oldReceiptStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
					oldReceiptStatus.put("statusUserLogin", userLoginId);
					delegator.store(oldReceiptStatus);
				} else {
					GenericValue newReceiptStatus = delegator.makeValue("ReceiptStatus");
					newReceiptStatus.put("receiptStatusId", delegator.getNextSeqId("ReceiptStatus"));
					newReceiptStatus.put("receiptId", receiptId);
					newReceiptStatus.put("statusId", "RECEIPT_CREATED");
					newReceiptStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
					newReceiptStatus.put("statusUserLogin", userLoginId);
					delegator.create(newReceiptStatus);
				}
				List<GenericValue> listOrderByAgreements = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
				listOrderByAgreements = EntityUtil.filterByDate(listOrderByAgreements);
				if (!listOrderByAgreements.isEmpty()){
					for (GenericValue order : listOrderByAgreements){
						GenericValue orderRequirement = delegator.findOne("OrderRequirement", false, UtilMisc.toMap("orderId", (String)order.get("orderId"), "requirementId", requirementId));
						if (orderRequirement != null){
							orderRequirement.put("statusId", "REQ_COMPLETED");
							delegator.store(orderRequirement);
						}
						GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", (String)order.get("orderId")));
						String currencyUomId = (String)orderHeader.get("currencyUom");
						List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String)order.get("orderId"))), null, null, null, false);
						if (!listOrderItems.isEmpty()){
							for (GenericValue item : listOrderItems){
								GenericValue receiptItem = delegator.makeValue("ReceiptItem");
								receiptItem.put("receiptId", receiptId);
								receiptItem.put("fromOrderId", (String)item.get("orderId"));
								receiptItem.put("fromOrderItemSeqId", (String)item.get("orderItemSeqId"));
								List<String> listOrderBy = new ArrayList<String>();
								listOrderBy.add("-receiptItemSeqId");
								List<GenericValue> listReceiptItems = delegator.findList("ReceiptItem", EntityCondition.makeCondition(UtilMisc.toMap("receiptId", receiptId)), null, listOrderBy, null, false);
								String receiptItemSeqId = null;
								String productId = (String)item.get("productId");
								if (!listReceiptItems.isEmpty()){
									Integer nextItemSeqId = Integer.parseInt((String)listReceiptItems.get(0).get("receiptItemSeqId")) + 1;
									receiptItemSeqId = String.format("%05d", nextItemSeqId);
								} else {
									receiptItemSeqId = String.format("%05d", 1);
								}
								BigDecimal quantity = item.getBigDecimal("alternativeQuantity");
								if ((quantity == null) || (quantity.compareTo(BigDecimal.ZERO) == 0)){
									quantity = item.getBigDecimal("quantity");
								}
								String quantityUomId = (String)item.get("quantityUomId");
								if ((quantityUomId == null)){
									GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
									quantityUomId = (String)product.get("quantityUomId");
								}
								BigDecimal unitPrice = item.getBigDecimal("alternativeUnitPrice");
								if ((unitPrice == null) || (unitPrice.compareTo(BigDecimal.ZERO) == 0)){
									unitPrice = item.getBigDecimal("unitPrice");
								}
								BigDecimal subTotal = BigDecimal.ZERO;
								if ((unitPrice.compareTo(BigDecimal.ZERO) == 1) && (quantity.compareTo(BigDecimal.ZERO) == 1)){
									subTotal = unitPrice.multiply(quantity);
								}
								receiptItem.put("receiptItemSeqId", receiptItemSeqId);
								receiptItem.put("productId", productId);
								receiptItem.put("expireDate", (Timestamp)item.get("expireDate"));
								receiptItem.put("orderedQuantity", quantity);
								receiptItem.put("quantityUomId", quantityUomId);
								receiptItem.put("unitPrice", unitPrice);
								receiptItem.put("subTotal", subTotal);
								receiptItem.put("currencyUomId", currencyUomId);
								receiptItem.put("actualQuantity", BigDecimal.ZERO);
								receiptItem.put("testQuantity", BigDecimal.ZERO);
								receiptItem.put("sampleQuantity", BigDecimal.ZERO);
								receiptItem.put("inspectQuantity", BigDecimal.ZERO);
								receiptItem.put("lackQuantity", BigDecimal.ZERO);
								delegator.createOrStore(receiptItem);
							}
						}
					}
				} else {
					return ServiceUtil.returnError(UtilProperties.getMessage(resource,
		                    "OrderNotFound", (Locale)context.get("locale")));
				}
				List<GenericValue> listLogManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", "LOG_SPECIALIST", "roleTypeIdTo", "INTERNAL_ORGANIZATIO", "roleTypeIdFrom", "MANAGER")), null, null, null, false);
				if(!listLogManagers.isEmpty()){
					for (GenericValue managerParty : listLogManagers){
						String tagetLink = "receiptId="+receiptId;
						String sendToPartyId = (String)managerParty.get("partyIdFrom");
						Map<String, Object> mapContext = new HashMap<String, Object>();
						mapContext.put("partyId", sendToPartyId);
						mapContext.put("action", "getDetailReceipts");
						mapContext.put("targetLink", tagetLink);
						mapContext.put("header", UtilProperties.getMessage(resource, "NewReceiptIncoming", (Locale)context.get("locale")));
						mapContext.put("userLogin", userLogin);
						try {
							dispatcher.runSync("createNotification", mapContext);
						} catch (GenericServiceException e) {
							e.printStackTrace();
						}
					}
				}
				List<GenericValue> listQAManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", "QA_QUALITY_MANAGER", "roleTypeIdTo", "INTERNAL_ORGANIZATIO", "roleTypeIdFrom", "MANAGER")), null, null, null, false);
				if(!listQAManagers.isEmpty()){
					for (GenericValue managerParty : listQAManagers){
						String tagetLink = "receiptId="+receiptId;
						String sendToPartyId = (String)managerParty.get("partyIdFrom");
						Map<String, Object> mapContext = new HashMap<String, Object>();
						mapContext.put("partyId", sendToPartyId);
						mapContext.put("action", "getQAListReceipts");
						mapContext.put("targetLink", tagetLink);
						mapContext.put("header", UtilProperties.getMessage(resource, "NewReceiptIncoming", (Locale)context.get("locale")));
						mapContext.put("userLogin", userLogin);
						try {
							dispatcher.runSync("createNotification", mapContext);
						} catch (GenericServiceException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (GenericEntityException e) {
				e.printStackTrace();
			}
		} else {
			if (orderId != null){
				try {
					GenericValue receipt = delegator.makeValue("Receipt");
					receipt.put("receiptId", receiptId);
					receipt.put("productStoreId", productStoreId);
					receipt.put("facilityId", facilityId);
					receipt.put("contactMechId", contactMechId);
					receipt.put("receiptDate", receiptDate);
					receipt.put("createDate", UtilDateTime.nowTimestamp());
					receipt.put("orderId", orderId);
					receipt.put("statusId", "RECEIPT_CREATED");
					receipt.put("createdByUserLogin", (String)userLogin.get("partyId"));
					delegator.createOrStore(receipt);
					List<GenericValue> receiptStatus = delegator.findList("ReceiptStatus", EntityCondition.makeCondition(UtilMisc.toMap("statusId", "RECEIPT_CREATED", "receiptId", receiptId)), null, null, null, false);
					if (!receiptStatus.isEmpty()){
						GenericValue oldReceiptStatus = receiptStatus.get(0);
						oldReceiptStatus.put("statusId", "RECEIPT_CREATED");
						oldReceiptStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
						oldReceiptStatus.put("statusUserLogin", userLoginId);
						delegator.store(oldReceiptStatus);
					} else {
						GenericValue newReceiptStatus = delegator.makeValue("ReceiptStatus");
						newReceiptStatus.put("receiptStatusId", delegator.getNextSeqId("ReceiptStatus"));
						newReceiptStatus.put("receiptId", receiptId);
						newReceiptStatus.put("statusId", "RECEIPT_CREATED");
						newReceiptStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
						newReceiptStatus.put("statusUserLogin", userLoginId);
						delegator.create(newReceiptStatus);
					}
					GenericValue orderRequirement = delegator.findOne("OrderRequirement", false, UtilMisc.toMap("orderId", orderId, "requirementId", requirementId));
					if (orderRequirement != null){
						orderRequirement.put("statusId", "REQ_COMPLETED");
						delegator.store(orderRequirement);
					}
					GenericValue orderHeader = delegator.findOne("OrderHeader", false, UtilMisc.toMap("orderId", orderId));
					String currencyUomId = (String)orderHeader.get("currencyUom");
					List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
					if (!listOrderItems.isEmpty()){
						for (GenericValue item : listOrderItems){
							GenericValue receiptItem = delegator.makeValue("ReceiptItem");
							receiptItem.put("receiptId", receiptId);
							receiptItem.put("fromOrderId", (String)item.get("orderId"));
							receiptItem.put("fromOrderItemSeqId", (String)item.get("orderItemSeqId"));
							List<String> listOrderBy = new ArrayList<String>();
							listOrderBy.add("-receiptItemSeqId");
							List<GenericValue> listReceiptItems = delegator.findList("ReceiptItem", EntityCondition.makeCondition(UtilMisc.toMap("receiptId", receiptId)), null, listOrderBy, null, false);
							String receiptItemSeqId = null;
							String productId = (String)item.get("productId");
							if (!listReceiptItems.isEmpty()){
								Integer nextItemSeqId = Integer.parseInt((String)listReceiptItems.get(0).get("receiptItemSeqId")) + 1;
								receiptItemSeqId = String.format("%05d", nextItemSeqId);
							} else {
								receiptItemSeqId = String.format("%05d", 1);
							}
							BigDecimal quantity = item.getBigDecimal("alternativeQuantity");
							if ((quantity == null) || (quantity.compareTo(BigDecimal.ZERO) == 0)){
								quantity = item.getBigDecimal("quantity");
							}
							String quantityUomId = (String)item.get("quantityUomId");
							if ((quantityUomId == null)){
								GenericValue product = delegator.findOne("Product", false, UtilMisc.toMap("productId", productId));
								quantityUomId = (String)product.get("quantityUomId");
							}
							BigDecimal unitPrice = item.getBigDecimal("alternativeUnitPrice");
							if ((unitPrice == null) || (unitPrice.compareTo(BigDecimal.ZERO) == 0)){
								unitPrice = item.getBigDecimal("unitPrice");
							}
							BigDecimal subTotal = BigDecimal.ZERO;
							if ((unitPrice.compareTo(BigDecimal.ZERO) == 1) && (quantity.compareTo(BigDecimal.ZERO) == 1)){
								subTotal = unitPrice.multiply(quantity);
							}
							receiptItem.put("receiptItemSeqId", receiptItemSeqId);
							receiptItem.put("productId", productId);
							receiptItem.put("expireDate", (Timestamp)item.get("expireDate"));
							receiptItem.put("orderedQuantity", quantity);
							receiptItem.put("quantityUomId", quantityUomId);
							receiptItem.put("unitPrice", unitPrice);
							receiptItem.put("subTotal", subTotal);
							receiptItem.put("currencyUomId", currencyUomId);
							receiptItem.put("actualQuantity", BigDecimal.ZERO);
							receiptItem.put("testQuantity", BigDecimal.ZERO);
							receiptItem.put("sampleQuantity", BigDecimal.ZERO);
							receiptItem.put("inspectQuantity", BigDecimal.ZERO);
							receiptItem.put("lackQuantity", BigDecimal.ZERO);
							delegator.createOrStore(receiptItem);
						}
					} else {
						return ServiceUtil.returnError(UtilProperties.getMessage(resource,
			                    "OrderItemNotFound", (Locale)context.get("locale")));
					}
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		result.put("receiptId", receiptId);
		return result;
	}
	public static Map<String, Object> updateReceiptItem(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String receiptId = (String)context.get("receiptId");
		String receiptItemSeqId = (String)context.get("receiptItemSeqId");
		String receiptStatusId = (String)context.get("statusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = (String)userLogin.get("userLoginId");
		BigDecimal testQuantity = (BigDecimal)context.get("testQuantity");
		BigDecimal sampleQuantity = (BigDecimal)context.get("sampleQuantity");
		BigDecimal inspectQuantity = (BigDecimal)context.get("inspectQuantity");
		BigDecimal lackQuantity = (BigDecimal)context.get("lackQuantity");
		BigDecimal actualQuantity = (BigDecimal)context.get("actualQuantity");
		BigDecimal quantityRejected = (BigDecimal)context.get("quantityRejected");
		if (quantityRejected == null){
			quantityRejected = BigDecimal.ZERO;
		}
		String rejectionId = (String)context.get("rejectionId");
		String comment = (String)context.get("comment");
		try {
			GenericValue receipt = delegator.findOne("Receipt", false, UtilMisc.toMap("receiptId", receiptId));
			Timestamp receiptDate = (Timestamp)receipt.get("receiptDate");
			DateTimeComparator comparator = DateTimeComparator.getDateOnlyInstance();
			if (comparator.compare(receiptDate, UtilDateTime.nowTimestamp()) == 0){
				GenericValue receiptItem = delegator.findOne("ReceiptItem", false, UtilMisc.toMap("receiptId", receiptId, "receiptItemSeqId", receiptItemSeqId));
				if (receiptItem != null){
					receiptItem.put("actualQuantity", actualQuantity);
					receiptItem.put("testQuantity", testQuantity);
					receiptItem.put("sampleQuantity", sampleQuantity);
					receiptItem.put("inspectQuantity", inspectQuantity);
					receiptItem.put("lackQuantity", lackQuantity);
					receiptItem.put("quantityRejected", quantityRejected);
					receiptItem.put("rejectionId", rejectionId);
					receiptItem.put("comment", comment);
					delegator.createOrStore(receiptItem);
				}
				receipt.put("statusId", receiptStatusId);
				delegator.store(receipt);
				List<GenericValue> receiptStatus = delegator.findList("ReceiptStatus", EntityCondition.makeCondition(UtilMisc.toMap("statusId", receiptStatusId, "receiptId", receiptId)), null, null, null, false);
				if (!receiptStatus.isEmpty()){
					GenericValue oldReceiptStatus = receiptStatus.get(0);
					oldReceiptStatus.put("statusId", receiptStatusId);
					oldReceiptStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
					oldReceiptStatus.put("statusUserLogin", userLoginId);
					delegator.store(oldReceiptStatus);
				} else {
					GenericValue newReceiptStatus = delegator.makeValue("ReceiptStatus");
					newReceiptStatus.put("receiptStatusId", delegator.getNextSeqId("ReceiptStatus"));
					newReceiptStatus.put("receiptId", receiptId);
					newReceiptStatus.put("statusId", receiptStatusId);
					newReceiptStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
					newReceiptStatus.put("statusUserLogin", userLoginId);
					delegator.create(newReceiptStatus);
				}
			} else {
				return ServiceUtil.returnError(UtilProperties.getMessage(resource,
	                    "NotTimeToUpdate", (Locale)context.get("locale")));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		String partyId = (String)context.get("partyId");
		String action = (String)context.get("action");
		String roleTypeId = (String)context.get("roleTypeId");
		String targetLink = (String)context.get("targetLink");
		String header = (String)context.get("header");
		Timestamp openTime  = (Timestamp)context.get("openTime");
		Timestamp dateTime  = (Timestamp)context.get("dateTime");
		result.put("partyId", partyId);
		result.put("action", action);
		result.put("roleTypeId", roleTypeId);
		result.put("targetLink", targetLink);
		result.put("header", UtilProperties.getMessage(resource, header, (Locale)context.get("locale")));
		result.put("openTime", openTime);
		result.put("dateTime", dateTime);
		result.put("receiptId", receiptId);
		
		return result;
	}
	public static Map<String,Object> addInventoryItemFromReceipt(DispatchContext ctx, Map<String,Object> context){
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String receiptId = (String)context.get("receiptId");
		String receiptItemSeqId = (String)context.get("receiptItemSeqId");
		Timestamp fromDate = UtilDateTime.nowTimestamp();
		String lotId = (String)context.get("lotId");
		String unitCost = (String)context.get("unitPrice");
		try {
			if (lotId != null){
				GenericValue lot = delegator.findOne("Lot", false, UtilMisc.toMap("lotId", lotId));
				if (lot == null){
					lot = delegator.makeValue("Lot");
					lot.put("lotId", lotId);
					lot.put("creationDate", fromDate);
					delegator.create(lot);
				} 			
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		if (unitCost.contains(",")){
			unitCost = unitCost.replace(",", ".");
		}
		GenericValue receipt = null;
		GenericValue receiptItem = null;
		try {
			receiptItem = delegator.findOne("ReceiptItem", false, UtilMisc.toMap("receiptId", receiptId, "receiptItemSeqId", receiptItemSeqId));
			receipt = delegator.findOne("Receipt", false, UtilMisc.toMap("receiptId", receiptId));
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(e.getMessage());
		}
		BigDecimal orderedQuantity = receiptItem.getBigDecimal("orderedQuantity");
		BigDecimal testQuantity = receiptItem.getBigDecimal("testQuantity");
		BigDecimal sampleQuantity = receiptItem.getBigDecimal("sampleQuantity");
		BigDecimal inspectQuantity = receiptItem.getBigDecimal("inspectQuantity");
		BigDecimal lackQuantity = receiptItem.getBigDecimal("lackQuantity");
		BigDecimal actualQuantity = new BigDecimal((String)context.get("actualQuantity"));;
		
		if (orderedQuantity.compareTo((actualQuantity.add(lackQuantity.add(inspectQuantity.add(sampleQuantity.add(testQuantity)))))) == -1){
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
                    "CannotReceiveQuantityGreaterThanQuantityOrdered", (Locale)context.get("locale")));
		} else {
			try {
				receiptItem.put("actualQuantity", actualQuantity);
				delegator.store(receiptItem);
				
				String inventoryItemId = delegator.getNextSeqId("InventoryItem");
				GenericValue invItem = delegator.makeValue("InventoryItem");
				String productId = (String)receiptItem.get("productId");
				String facilityId = (String)receipt.get("facilityId");
				Timestamp expireDate = (Timestamp)receiptItem.get("expireDate");
				invItem.put("inventoryItemId", inventoryItemId);
				invItem.put("productId", productId);
				invItem.put("ownerPartyId", "company");
				invItem.put("inventoryItemTypeId", "NON_SERIAL_INV_ITEM");
				invItem.put("statusId", "ITEM_CREATED");
				invItem.put("datetimeReceived", UtilDateTime.nowTimestamp());
				invItem.put("expireDate", expireDate);
				invItem.put("facilityId", facilityId);
				invItem.put("lotId", lotId);
				invItem.put("quantityOnHandTotal", actualQuantity);
				invItem.put("availableToPromiseTotal", actualQuantity);
				invItem.put("unitCost", new BigDecimal(unitCost));
				invItem.put("currencyUomId", (String)receiptItem.get("currencyUomId"));
				delegator.createOrStore(invItem);
				
//				GenericValue shipmentReceipt = delegator.makeValue("ShipmentReceipt");
//				String receiptId = delegator.getNextSeqId("ShipmentReceipt");
//				shipmentReceipt.put("receiptId", receiptId);
//				shipmentReceipt.put("inventoryItemId", inventoryItemId);
//				shipmentReceipt.put("productId", productId);
//				shipmentReceipt.put("orderId", orderId);
//				shipmentReceipt.put("orderItemSeqId", orderItemSeqId);
//				shipmentReceipt.put("quantityAccepted", quantityWillBeReceived);
//				shipmentReceipt.put("quantityRejected", quantityWillByRejected);
//				shipmentReceipt.put("receivedByUserLoginId", partyId);
//				shipmentReceipt.put("datetimeReceived", UtilDateTime.nowTimestamp());
//				shipmentReceipt.put("rejectionId", rejectionId);
//				delegator.createOrStore(shipmentReceipt);
//				
				receipt.put("statusId", "RECEIPT_STK_ACCEPTED");
				delegator.store(receipt);
			} catch (GenericEntityException e) {
				Debug.logError(e, module);
				return ServiceUtil.returnError(e.getMessage());
			}
			result.put("orderId", receiptId);
		}
		return result;
	}
	
	public static String CreateImportPlanHeader(Delegator delegator, String parentId, String productPlanType, String customTimePeriodId,String currencyUomId, String createByUser, String modifiedBy, String namePlan, String organizationId, String internalId, String supplierId, String statusId, String customTimePeriodSales) throws GenericEntityException{
		int result=0;
		GenericValue productPlanHeader = delegator.makeValue("ProductPlanHeader");
	    String productPlanId = delegator.getNextSeqId("ProductPlanHeader");
	    productPlanHeader.put("productPlanId", productPlanId);
	    productPlanHeader.put("parentProductPlanId", parentId);
	    productPlanHeader.put("productPlanTypeId", productPlanType);
	    productPlanHeader.put("customTimePeriodId", customTimePeriodId);
	    productPlanHeader.put("currencyUomId", currencyUomId);
	    productPlanHeader.put("createByUserLoginId", createByUser);
	    productPlanHeader.put("modifiedByUserLoginId", modifiedBy);
	    productPlanHeader.put("productPlanName", namePlan);
	    productPlanHeader.put("organizationPartyId", organizationId);
	    productPlanHeader.put("internalPartyId", internalId);
	    productPlanHeader.put("supplierPartyId", supplierId);
	    productPlanHeader.put("statusId", statusId);
	    productPlanHeader.put("customTimePeriodIdOfSales", customTimePeriodSales);
	    delegator.createOrStore(productPlanHeader);
//	    result = 1;
	    return productPlanId;
//	    return result;
}

	public static Map<String, Object> createNewImportPlan(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String customTimePeriodImport = (String)context.get("customTimePeriodImport");
//		String customTimePeriodSales = (String)context.get("customTimePeriodSales");
		String customTimePeriodSales = "";
		String internalId = (String)context.get("areaId");
		String productPlanId = null;
		String namePlan = (String)context.get("namePlan");
		
		GenericValue thisCustomTimeImport = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodImport), false);
		Date fromDate = null;
		String thisYear = "";
		String errorMessage = "";
		if(UtilValidate.isNotEmpty(thisCustomTimeImport)) 
		{
		fromDate = thisCustomTimeImport.getDate("fromDate");
		thisYear  = String.valueOf(fromDate.getYear() + 1900);
		}
	
		List<GenericValue> thisCustomTimeSales = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("fromDate", fromDate, "periodTypeId", "SALES_YEAR")), null, null, null, false);
//		Check if customTimePeriodSales exists
		if(UtilValidate.isNotEmpty(thisCustomTimeSales))
		{
			customTimePeriodSales = (String) EntityUtil.getFirst(thisCustomTimeSales).get("customTimePeriodId");
			List<GenericValue> listCustomTimeSalesQuarter = new ArrayList<GenericValue>();
			List<GenericValue> test = new ArrayList<GenericValue>();
			List<GenericValue> checkImportPlan = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodImport,"internalPartyId", internalId, "customTimePeriodIdOfSales", customTimePeriodSales)), null, null, null, false);
			if(UtilValidate.isEmpty(checkImportPlan)){
				
				String parentId = null;
				String productPlanType = "IMPORT_PLAN";
				String currencyUomId = "USD";
				GenericValue createByUserGe = (GenericValue)context.get("userLogin");
				String createByUser = (String)createByUserGe.get("userLoginId");
				String modifiedBy = null;
				String organizationId = "company";
				String supplierId = "ZOTT_COMPANY";
				String statusId = "PLAN_CREATED";
				List<String> oderBy = new ArrayList<String>();
				oderBy.add("thruDate");
				String parentOfMonth = CreateImportPlanHeader(delegator, parentId, productPlanType, customTimePeriodImport, currencyUomId, createByUser, modifiedBy, namePlan, organizationId, internalId, supplierId, statusId, customTimePeriodSales);
				
				List<GenericValue> listPeriodImportMonth = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodImport,"periodTypeId", "IMPORT_MONTH")), null, oderBy, null, false);
				listCustomTimeSalesQuarter = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodSales)), null, oderBy, null, false);
				
				for (GenericValue gx : listCustomTimeSalesQuarter) {
					String CustomTimeSalesQuarterId = (String) gx.get("customTimePeriodId");
					List<GenericValue> listCustomTimeSalesMonthInQt = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", CustomTimeSalesQuarterId)), null, oderBy, null, false);
					test.addAll(listCustomTimeSalesMonthInQt);
				}
				int myCount = 0;
				for(GenericValue x : test){
					String customTimeMonth = (String) x.get("customTimePeriodId");
					GenericValue myTempt = listPeriodImportMonth.get(myCount);
					String customTimeMonthImport = (String) myTempt.get("customTimePeriodId");
					myCount += 1;
					String namePlanOfMonth = (String)myTempt.get("periodName");
					CreateImportPlanHeader(delegator, parentOfMonth, productPlanType, customTimeMonthImport, currencyUomId, createByUser, modifiedBy, namePlanOfMonth, organizationId, internalId, supplierId, statusId, customTimeMonth);
				}
				productPlanId = parentOfMonth;
			}else{
				GenericValue checkImportPlanIsTrue = EntityUtil.getFirst(checkImportPlan);
				productPlanId = (String)checkImportPlanIsTrue.get("productPlanId");
				checkImportPlanIsTrue.put("productPlanName", namePlan);
				delegator.store(checkImportPlanIsTrue);
			}
			result.put("productPlanId", productPlanId);
	//		result.put("customTimePeriodImport", customTimePeriodImport);
			return result;
	}
		else
			errorMessage = UtilProperties.getMessage("DelysUiLabels", "noSalesForecastData", (Locale)context.get("locale"));
			errorMessage = errorMessage + " " + thisYear + " !";
			return ServiceUtil.returnError(errorMessage);
	}
	public static Map<String,Object> updateAgreementStatus(DispatchContext ctx, Map<String,Object> context){
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String agreementId = (String)context.get("agreementId");
		GenericValue agreement = null;
		try {
			agreement = delegator.findOne("Agreement", false, UtilMisc.toMap("agreementId", agreementId));
			if (agreement != null){
				String curStatus = (String)agreement.get("statusId");
				if ("PURCHASE_AGREEMENT".equals((String)agreement.get("agreementTypeId"))){
					if (!"AGREEMENT_COMPLETED".equals(curStatus)){
						// Cancel all purchase order not completed yet of this agreement
						List<GenericValue> listOrderByAgreements = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
						if (!listOrderByAgreements.isEmpty()){
							for (GenericValue order : listOrderByAgreements){
								GenericValue orderHeader = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", (String)order.get("orderId")), false); 
								if (!"ORDER_COMPLETED".equals((String)orderHeader.get("statusId"))){
									Date nowDate = new Date();
									Timestamp nowTimestamp = new Timestamp(nowDate.getTime());
									order.put("thruDate", nowTimestamp);
									delegator.store(order);
									orderHeader.put("statusId", "ORDER_CANCELLED");
									delegator.store(orderHeader);
								}
							}
						}
					}
				}
				agreement.setNonPKFields(context);
				delegator.store(agreement);
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("agreementId", agreementId);
		return result;
	}
	public static Map<String,Object> updateAgreementFromDocuments(DispatchContext ctx, Map<String,Object> context) throws ParseException{
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String agreementId = (String)context.get("agreementId");
		String containerNumber = (String)context.get("containerNumber");
		String billNumber = (String)context.get("billNumber");
		String statusId = (String)context.get("statusId");
		String orderId = (String)context.get("orderId");
		
//		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
		Timestamp arrivalDate = (Timestamp)context.get("arrivalDate");
//		Date arrivalDateFor = (Date)formatDate.parse(arrivalDateStr);
////		Timestamp arrivalDate = new Timestamp(arrivalDateFor.getTime());
//		
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(arrivalDateFor);
//    	calendar.set(Calendar.HOUR, 0);
//    	calendar.set(Calendar.MINUTE, 0);
//    	calendar.set(Calendar.SECOND, 0);
//    	calendar.set(Calendar.MILLISECOND, 0);
//		java.sql.Timestamp aaa = new java.sql.Timestamp(calendar.getTimeInMillis());
		
		Timestamp departureDate = (Timestamp)context.get("departureDate");
		
		String shippingLineId = (String)context.get("shippingLineId");
		String partyRentId = (String)context.get("partyRentId");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		GenericValue agreement = null;
		try {
			agreement = delegator.findOne("Agreement", false, UtilMisc.toMap("agreementId", agreementId));
			if (agreement != null){
				List<GenericValue> listOrderByAgreements = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
				if (listOrderByAgreements.isEmpty()){
					return ServiceUtil.returnError(UtilProperties.getMessage(resource, "OrderNotFound", (Locale)context.get("locale")));
				} else {
					String curStatus = (String)agreement.get("statusId");
					if ("AGREEMENT_SENT".equals(curStatus)){
						if ("AGREEMENT_CANCELLED".equals(statusId)){
							if (orderId != null){
								GenericValue order = delegator.findOne("Order", false, UtilMisc.toMap("orderId", orderId));
								order.put("statusId", "ORDER_CANCELLED");
								delegator.store(order);
							}
							boolean checkOrder = true;
							for (GenericValue order : listOrderByAgreements){
								if (!"ORDER_CANCELLED".equals((String)order.get("statusId"))){
									checkOrder = false;
									break;
								}
							}
							if (checkOrder){
								agreement.put("statusId", "AGREEMENT_CANCELLED");
								delegator.store(agreement);
							}
						} else {
							if ("AGREEMENT_PROCESSING".equals(statusId)){
								try {
									Map<String, Object> mapContext = new HashMap<String, Object>();
									mapContext.put("orderId", orderId);
									mapContext.put("statusId", "ORDER_APPROVED");
									mapContext.put("setItemStatus", "Y");
									mapContext.put("userLogin", userLogin);
									dispatcher.runSync("changeOrderStatus", mapContext);
								} catch (GenericServiceException e) {
									e.printStackTrace();
								}
								agreement.put("statusId", "AGREEMENT_PROCESSING");
								delegator.store(agreement);
								String billId = null;
								List<GenericValue> listBills = delegator.findList("BillOfLading", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", shippingLineId, "billNumber", billNumber)), null, null, null, false);
								//Dat: sau khi billoflading nay hoan thanh do các order complete, thì sẽ thrudate cho BillOfLading này
								//do vậy listBills phải tiếp tục filterByDate, tránh trường hợp sau này trùng hãng tàu, cùng số bill do 1 lý do khách quan
								//lúc này sẽ tiếp tục sinh ra billId mới, và billId cũ ko bị update nên thong tin của bill cũ đc bảo toàn
								if (listBills.isEmpty()){
									GenericValue bill = delegator.makeValue("BillOfLading");
									billId = delegator.getNextSeqId("BillOfLading");
									bill.put("billId", billId);
									bill.put("billNumber", billNumber);
									bill.put("partyIdFrom", partyRentId);
									bill.put("partyIdTo", shippingLineId);
									bill.put("billTypeId", "INCOMING_BILL");
									bill.put("arrivalDate", arrivalDate);
									bill.put("departureDate", departureDate);
									bill.put("fromDate", UtilDateTime.nowTimestamp());
									delegator.createOrStore(bill);
									
								} else {
									billId = (String)listBills.get(0).get("billId");
									GenericValue bill = EntityUtil.getFirst(listBills);
									bill.put("billNumber", billNumber);
									bill.put("arrivalDate", arrivalDate);
									bill.put("departureDate", departureDate);
									bill.put("fromDate", UtilDateTime.nowTimestamp());
									delegator.store(bill);
								}
								GenericValue cont = delegator.makeValue("Container");
								String containerId = delegator.getNextSeqId("Container");
								cont.put("containerId", containerId);
								cont.put("containerNumber", containerNumber);
								cont.put("containerTypeId", "STANDARD_CONTAINER");
								delegator.createOrStore(cont);
								
								GenericValue orderAndCont = delegator.makeValue("OrderAndContainer");
								orderAndCont.put("orderId", orderId);
								orderAndCont.put("billId", billId);
								orderAndCont.put("containerId", containerId);
								orderAndCont.put("fromDate", UtilDateTime.nowTimestamp());
								delegator.createOrStore(orderAndCont);
								
								List<GenericValue> orderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
								if (!orderItems.isEmpty()){
									for (GenericValue item : orderItems){
										GenericValue contItem = delegator.makeValue("ContainerItem");
										contItem.put("containerId", containerId);
										contItem.put("quantity", item.get("quantity"));
										contItem.put("expireDate", item.get("expireDate"));
//										contItem.put("datetimeManufactured", item.get("datetimeManufactured"));
										contItem.put("fromOrderId", orderId);
										contItem.put("fromOrderItemSeqId", item.get("orderItemSeqId"));
										
										List<String> listOrderBy = new ArrayList<String>();
										listOrderBy.add("-containerItemSeqId");
										List<GenericValue> listContItems = delegator.findList("ContainerItem", EntityCondition.makeCondition(UtilMisc.toMap("containerId", containerId)), null, listOrderBy, null, false);
										if (!listContItems.isEmpty()){
											Integer nextContainerItemSeqId = Integer.parseInt((String)listContItems.get(0).get("containerItemSeqId")) + 1;
											String contItemSeqId = String.format("%05d", nextContainerItemSeqId);
											contItem.put("containerItemSeqId", contItemSeqId);
										} else {
											String contItemSeqId = String.format("%05d", 1);
											contItem.put("containerItemSeqId", contItemSeqId);
										}
										delegator.createOrStore(contItem);
									}
								}
							} else {
								return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					                    "ChangeStatusToUpdate", (Locale)context.get("locale")));
							}
						}
					} else {
						if ("AGREEMENT_COMPLETED".equals(statusId)){
							try {
								Map<String, Object> mapContext = new HashMap<String, Object>();
								mapContext.put("orderId", orderId);
								mapContext.put("statusId", "ORDER_COMPLETED");
								mapContext.put("setItemStatus", "Y");
								mapContext.put("userLogin", userLogin);
								dispatcher.runSync("changeOrderStatus", mapContext);
							} catch (GenericServiceException e) {
								e.printStackTrace();
							}
						}
						agreement.put("statusId", statusId);
						delegator.store(agreement);
						String curBillId = (String)context.get("billId");
						String curContId = (String)context.get("containerId");
						if (curBillId != null){
							GenericValue curBill = delegator.findOne("BillOfLading", false, UtilMisc.toMap("billId", curBillId));
							if (curBill != null){
								curBill.put("billNumber", billNumber);
								curBill.put("arrivalDate", arrivalDate);
								curBill.put("departureDate", departureDate);
								delegator.store(curBill);
							}
						}
						if (curContId != null){
							GenericValue curCont = delegator.findOne("Container", false, UtilMisc.toMap("containerId", curContId));
							if (curCont != null){
								curCont.put("containerNumber", containerNumber);
								delegator.store(curCont);
							}
						}
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("agreementId", agreementId);
		return result;
	}
	public static Map<String,Object> updateOrderItemFromDocuments(DispatchContext ctx, Map<String,Object> context){
		Map<String,Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		String orderId = (String)context.get("orderId");
		String orderItemSeqId = (String)context.get("orderItemSeqId");
		String quantityStr = (String)context.get("quantity");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		int quantityInt = 0;
		if(!"".equals(quantityStr)){
			quantityInt = Integer.parseInt(quantityStr);
		}
		BigDecimal documentQuantity = new BigDecimal(quantityInt);
		String expireDateStr = (String)context.get("expireDate");
		String datetimeManufacturedStr = (String)context.get("datetimeManufactured");
		String productId = (String)context.get("productId");
		Long dateManuLong = Long.parseLong(datetimeManufacturedStr);
		Long expireDateLong = Long.parseLong(expireDateStr);
		java.sql.Timestamp datetimeManufactured = new java.sql.Timestamp(dateManuLong);
		java.sql.Timestamp expireDate = new java.sql.Timestamp(expireDateLong);
		List<String> listOrderItemSeq = new ArrayList<String>();
		try {
			List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
			if(!UtilValidate.isEmpty(listOrderItems)){
				for(GenericValue orItem : listOrderItems){
					String orderItemSeqIdCheck = (String)orItem.get("orderItemSeqId");
					listOrderItemSeq.add(orderItemSeqIdCheck);
				}
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			List<GenericValue> listShipGroupSeqId;
			GenericValue shipGroupId = null;
				listShipGroupSeqId = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId",orderId)), null, null, null, false);
				if(!UtilValidate.isEmpty(listShipGroupSeqId)){
					shipGroupId = EntityUtil.getFirst(listShipGroupSeqId);
				}
			GenericValue orderItem = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
			//update order item if exi
			if (orderItem != null){
				BigDecimal quantity = orderItem.getBigDecimal("alternativeQuantity");
				if ((quantity == null) || (quantity.compareTo(BigDecimal.ZERO) == 0)){
					if(shipGroupId != null){
				        Map<String, String> itemDescriptionMap = FastMap.newInstance();
				        Map<String, String> itemReasonMap = FastMap.newInstance();
				        Map<String, String> itemCommentMap = FastMap.newInstance();
				        Map<String, String> itemAttributesMap = FastMap.newInstance();
				        Map<String, String> itemEstimatedShipDateMap = FastMap.newInstance();
				        Map<String, String> itemEstimatedDeliveryDateMap = FastMap.newInstance();
						String shipGroupSeqId = (String)shipGroupId.get("shipGroupSeqId");
						Map<String, String> itemQtyMap = FastMap.newInstance();
						Map<String, String> itemPriceMap = FastMap.newInstance();
						Map<String, String> overridePriceMap = FastMap.newInstance();
						itemQtyMap.put(orderItemSeqId + ":" + shipGroupSeqId, quantityStr);
						itemPriceMap.put(orderItemSeqId, "0");
//						overridePriceMap.put(orderItemSeqId, "N");
						Map<String,Object> contextTmp = new HashMap<String, Object>();
						contextTmp.put("orderId", orderId);
						contextTmp.put("orderTypeId", "PURCHASE_ORDER");
						contextTmp.put("itemQtyMap", itemQtyMap);
						contextTmp.put("itemPriceMap", itemPriceMap);
						contextTmp.put("itemDescriptionMap", itemDescriptionMap);
						contextTmp.put("itemReasonMap", itemReasonMap);
						contextTmp.put("itemCommentMap", itemCommentMap);
						contextTmp.put("itemAttributesMap", itemAttributesMap);
						contextTmp.put("itemShipDateMap", itemEstimatedShipDateMap);
						contextTmp.put("itemDeliveryDateMap", itemEstimatedDeliveryDateMap);
						contextTmp.put("overridePriceMap", overridePriceMap);
						contextTmp.put("userLogin", userLogin);
						try {
							dispatcher.runSync("updateOrderItemsPOImport", contextTmp);
						} catch (GenericServiceException e) {
							e.printStackTrace();
						}
					}
					GenericValue orderItemUpdateDate = delegator.findOne("OrderItem", false, UtilMisc.toMap("orderId", orderId, "orderItemSeqId", orderItemSeqId));
					orderItemUpdateDate.put("productId", productId);
					orderItemUpdateDate.put("expireDate", expireDate);
					orderItemUpdateDate.put("datetimeManufactured", datetimeManufactured);
					delegator.store(orderItemUpdateDate);
//					orderItem.put("quantity", documentQuantity);
				} else {
					orderItem.put("alternativeQuantity", documentQuantity);
				}
			}
			else{// append item order if seqItem not exist
				if(shipGroupId != null){
					String shipGroupSeqId = (String)shipGroupId.get("shipGroupSeqId");
					Map<String,Object> contextTmp = new HashMap<String, Object>();
					contextTmp.put("orderId", orderId);
					contextTmp.put("shipGroupSeqId", shipGroupSeqId);
					contextTmp.put("productId", productId);
					contextTmp.put("quantity", documentQuantity);
					contextTmp.put("expireDate", expireDate);
					contextTmp.put("userLogin", userLogin);
					try {
						dispatcher.runSync("appendOrderItem", contextTmp);
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
				}
				
				List<GenericValue> listOrderItems2 = delegator.findList("OrderItem", EntityCondition.makeCondition("orderId", orderId), null, null, null, false);
				if(!UtilValidate.isEmpty(listOrderItems2)){
					for(GenericValue orItem : listOrderItems2){
						String orderItemSeqIdCheck = (String)orItem.get("orderItemSeqId");
						if(!listOrderItemSeq.contains(orderItemSeqIdCheck)){
							orderItemSeqId = orderItemSeqIdCheck;
							orItem.put("datetimeManufactured", datetimeManufactured);
							delegator.store(orItem);
						}
					}
				}
				
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		
		result.put("orderId", orderId);
		result.put("orderItemSeqId", orderItemSeqId);
		return result;
	}
	
	public static void updateRecentPlanQuantity(Delegator delegator, GenericValue planItem, BigDecimal quantity){
		BigDecimal recentPlanQuantity = quantity;
		if(planItem.getBigDecimal("recentPlanQuantity") != null){
			recentPlanQuantity = planItem.getBigDecimal("recentPlanQuantity").add(quantity);
		}
		planItem.put("recentPlanQuantity", recentPlanQuantity);
		try {
			delegator.store(planItem);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Map<String,Object> updateReceiptStatus(DispatchContext ctx, Map<String,Object> context){
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String receiptId = (String)context.get("curReceiptId");
		String statusId = (String)context.get("receiptStatusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = (String)userLogin.get("userLoginId");
		try {
			List<GenericValue> listReceiptItems = delegator.findList("ReceiptItem", EntityCondition.makeCondition(UtilMisc.toMap("receiptId", receiptId)), null, null, null, false);
			if (!listReceiptItems.isEmpty()){
				Boolean check = false;
				for (GenericValue item : listReceiptItems){
//					BigDecimal orderedQty = (BigDecimal)item.get("orderedQuantity");
					BigDecimal actualQty = (BigDecimal)item.get("actualQuantity");
//					BigDecimal lackQty = (BigDecimal)item.get("lackQuantity");
//					BigDecimal rejectedQty = (BigDecimal)item.get("quantityRejected");
//					BigDecimal testQty = (BigDecimal)item.get("testQuantity");
//					BigDecimal sampleQty = (BigDecimal)item.get("sampleQuantity");
//					BigDecimal inspectQty = (BigDecimal)item.get("inspectQuantity");
//					if (orderedQty.subtract(actualQty.add(lackQty.add(rejectedQty.add(testQty.add(sampleQty.add(inspectQty)))))).compareTo(BigDecimal.ZERO) != 1){
//						check = true;
//					} else {
//						check = false;
//					}
					if (actualQty == null || (actualQty.compareTo(BigDecimal.ZERO) == 0)){
						check = false;
					} else {
						check = true;
					}
				}
				if (check){
					statusId = "RECEIPT_INV_RECEIPT";
					GenericValue receipt = delegator.findOne("Receipt", false, UtilMisc.toMap("receiptId", receiptId));
					receipt.put("statusId", statusId);
					delegator.store(receipt);
					
					List<GenericValue> receiptStatus = delegator.findList("ReceiptStatus", EntityCondition.makeCondition(UtilMisc.toMap("statusId", statusId, "receiptId", receiptId)), null, null, null, false);
					if (!receiptStatus.isEmpty()){
						GenericValue oldReceiptStatus = receiptStatus.get(0);
						oldReceiptStatus.put("statusId", statusId);
						oldReceiptStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
						oldReceiptStatus.put("statusUserLogin", userLoginId);
						delegator.store(oldReceiptStatus);
					} else {
						GenericValue newReceiptStatus = delegator.makeValue("ReceiptStatus");
						newReceiptStatus.put("receiptStatusId", delegator.getNextSeqId("ReceiptStatus"));
						newReceiptStatus.put("receiptId", receiptId);
						newReceiptStatus.put("statusId", statusId);
						newReceiptStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
						newReceiptStatus.put("statusUserLogin", userLoginId);
						delegator.create(newReceiptStatus);
					}	
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		result.put("receiptId", receiptId);
		return result;
	}

	public static Map<String, Object> createProductPlanAndLot(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException, ParseException{
		Delegator delegator = ctx.getDelegator();
		Map<String,Object> result = new FastMap<String, Object>();
		String json =(String)context.get("pro");
		String planId = (String)context.get("planId");
		JSONArray jsonArr = new JSONArray().fromObject(json);
		
		for(int i = 0; i < jsonArr.size(); i++){
			
			JSONObject jsonObj = jsonArr.getJSONObject(i);
			JSONArray jsonArr2 = jsonObj.getJSONArray("pro");
//create Lot
			String lotId = delegator.getNextSeqId("Lot");
			GenericValue lot = delegator.makeValue("Lot");
			lot.put("lotId", lotId);
			lot.put("creationDate", UtilDateTime.nowTimestamp());
			delegator.createOrStore(lot);
			
			for(int j = 0; j < jsonArr2.size(); j++){
				JSONObject jsonObj2 = jsonArr2.getJSONObject(j);
				
				//get du lieu cua tung san pham trong bang html
				String quantity = jsonObj2.getString("quantity");
//				BigDecimal quantityBig = BigDecimal
				DecimalFormat decimalFormat = new DecimalFormat();
				decimalFormat.setParseBigDecimal(true);
				BigDecimal quantityBig = (BigDecimal) decimalFormat.parse(quantity);
				String productId = jsonObj2.getString("productId");
				String seqItemId = jsonObj2.getString("seqItemId");

//create product plan and lot
				
				if(!quantityBig.equals(BigDecimal.ZERO)){
				GenericValue lotPlan = delegator.makeValue("ProductPlanAndLot");
				lotPlan.put("lotId", lotId);
				lotPlan.put("productPlanId", planId);
				lotPlan.put("productPlanItemSeqId", seqItemId);
				lotPlan.put("lotQuantity", quantityBig);
//Fix
				lotPlan.put("shipmentUomId", "CONTAINER");
				lotPlan.put("productPackingUomId", "PALLET");
//				lotPlan.put("statusId", "LOT_CREATED");
				delegator.createOrStore(lotPlan);
				
				}
				
				
				
				
			}
		}
		return ServiceUtil.returnSuccess();
		
	}
	
	public static void createCustomtimePeriod2(Delegator delegator, String customTimePeriodId, String parentPeriodId, String periodTypeId, String periodName, Date fromDate, Date thruDate, String organizationPartyId) throws GenericEntityException{
		GenericValue customTime = delegator.makeValue("CustomTimePeriod");
		customTime.put("customTimePeriodId", customTimePeriodId);
		customTime.put("parentPeriodId", parentPeriodId);
		customTime.put("periodTypeId", periodTypeId);
		customTime.put("periodName", periodName);
		customTime.put("fromDate", fromDate);
		customTime.put("thruDate", thruDate);
		customTime.put("organizationPartyId", organizationPartyId);
		
		delegator.create(customTime);
	}
	
	public static Map<String, Object> customTimePeriodWeekOfMonth(DispatchContext ctx, Map<String,Object> context) throws ParseException, GenericEntityException {
	    Delegator delegator = ctx.getDelegator();
	    String strFromYear = (String)context.get("fromYear");
	    String strThruYear = (String)context.get("thruYear");
	    int fromYear = Integer.parseInt(strFromYear);
	    int thruYear = Integer.parseInt(strThruYear);
	    SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd");
	    
	    Calendar calendar = Calendar.getInstance();
	    Date date = new Date();
	    calendar.setTime(date);
	    
	    for(int i = fromYear; i <= thruYear; i++){
	    calendar.set(Calendar.YEAR, i);
	    calendar.set(Calendar.MONTH, 11);
    	calendar.set(Calendar.DAY_OF_MONTH, 31);
    	calendar.set(Calendar.HOUR, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	java.sql.Date endYear = new java.sql.Date(calendar.getTimeInMillis());
    	calendar.set(Calendar.MONTH, 0);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	java.sql.Date newYear = new java.sql.Date(calendar.getTimeInMillis());
    	String customTimePeriodIdYear = delegator.getNextSeqId("CustomTimePeriod");
//	create custom time year
    	//fix company...
    	createCustomtimePeriod2(delegator, customTimePeriodIdYear, null, "IMPORT_YEAR", "year: " +i, newYear, endYear, "company");
    	
	    for(int k = 0; k < 12; k++){
	    	calendar.set(Calendar.MONTH, k);
	    	calendar.set(Calendar.DAY_OF_MONTH, 1);
	    	java.sql.Date firstDayOfMonth = new java.sql.Date(calendar.getTimeInMillis());
	    	int dateEndMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	    	calendar.set(Calendar.DAY_OF_MONTH, dateEndMonth);
	    	java.sql.Date endDayOfMonth = new java.sql.Date(calendar.getTimeInMillis());
	    	//create new customTimeperiod of month
	    	String customTimePeriodIdMonth = delegator.getNextSeqId("CustomTimePeriod");
	    	createCustomtimePeriod2(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "IMPORT_MONTH", "month: " +(k+1), firstDayOfMonth, endDayOfMonth, "company");
	    	
	    	//fix ngay 26
	    	calendar.set(Calendar.DAY_OF_MONTH, 26);
	    	java.sql.Date curMonth = new java.sql.Date(calendar.getTimeInMillis());

	    	calendar.add(Calendar.MONTH, -1);
	    	java.sql.Date prevMonth = new java.sql.Date(calendar.getTimeInMillis());
	    	int counti = 0;
//	    	String dater = "";
	    	while(!curMonth.equals(prevMonth)){
//	    		calendar.setTime(prevMonth);
	    		calendar.add(Calendar.DATE, 1);
	    		int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
	    		prevMonth = new java.sql.Date(calendar.getTimeInMillis());
	    		//fix thu 5
	    		if(dayWeek == 5){
	    			counti++;
	    			String customTimePeriodIdWeek = delegator.getNextSeqId("CustomTimePeriod");
	    			//create custom time week
	    			createCustomtimePeriod2(delegator, customTimePeriodIdWeek, customTimePeriodIdMonth, "IMPORT_WEEK", "week: " +counti, prevMonth, prevMonth, "company");
//	    			dater = + "" +(yearMonthDayFormat.format(prevMonth)) + "--";
	    		}
	    	}
	    	

	    }
	}
	    return ServiceUtil.returnSuccess();
	}
//	<condition-list combine="and">
//	<condition-expr field-name="agreementTypeId" value="PURCHASE_AGREEMENT"></condition-expr>
//	<condition-list combine="or">
//		<condition-expr field-name="statusId" value="AGREEMENT_PROCESSING"></condition-expr>
//		<condition-expr field-name="statusId" value="AGREEMENT_SENT"></condition-expr>
//	</condition-list>
//</condition-list>
//	public static Map<String, Object> JQGetAgreeToUpdate(DispatchContext ctx, Map<String, ? extends Object> context) {
//    	Delegator delegator = ctx.getDelegator();
//    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
//    	EntityListIterator listIterator = null;
//    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
//    	List<String> listSortFields = (List<String>) context.get("listSortFields");
//    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
//    	Map<String, String> mapCondition = new HashMap<String, String>();
//    	
////    	List<EntityExpr> expr1 = FastList.newInstance();
////    	expr1.add(EntityCondition.makeCondition("agreementTypeId", EntityOperator.EQUALS, "PURCHASE_AGREEMENT"));
//    	List<EntityExpr> expr2 = FastList.newInstance();
//    	expr2.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_PROCESSING"));
//    	expr2.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_SENT"));
//    	EntityCondition condition1 = EntityCondition.makeCondition(expr2, EntityOperator.OR);
//    	EntityCondition cond = EntityCondition.makeCondition("agreementTypeId", EntityOperator.EQUALS, "PURCHASE_AGREEMENT");
//    	EntityCondition finalCond = EntityCondition.makeCondition(condition1, EntityOperator.AND, cond);    	
//    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
//    	listAllConditions.add(finalCond);
//    	try {
//    		listIterator = delegator.find("AgreementAndOrderDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
//		} catch (GenericEntityException e) {
//			String errMsg = "Fatal error calling jqGetListProduct service: " + e.toString();
//			Debug.logError(e, errMsg, module);
//		}
//    	successResult.put("listIterator", listIterator);
//    	return successResult;
//    }
	
	public static Map<String, Object> JQGetAgreeToUpdate(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	SimpleDateFormat yearMonthDayFormat2 = new SimpleDateFormat("dd/MM/yyyy");
//    	List<EntityExpr> expr1 = FastList.newInstance();
//    	expr1.add(EntityCondition.makeCondition("agreementTypeId", EntityOperator.EQUALS, "PURCHASE_AGREEMENT"));
    	List<EntityExpr> expr2 = FastList.newInstance();
    	expr2.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_PROCESSING"));
    	expr2.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "AGREEMENT_SENT"));
    	EntityCondition condition1 = EntityCondition.makeCondition(expr2, EntityOperator.OR);
    	EntityCondition cond = EntityCondition.makeCondition("agreementTypeId", EntityOperator.EQUALS, "PURCHASE_AGREEMENT");
    	EntityCondition finalCond = EntityCondition.makeCondition(condition1, EntityOperator.AND, cond);    	
    	EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
    	listAllConditions.add(finalCond);
    	List<GenericValue> listGe = new ArrayList<GenericValue>();
    	try {
    		listGe = delegator.findList("AgreementAndOrderDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    		if(!UtilValidate.isEmpty(listGe)){
//    			row.put("rowDetail", value);
    			for(GenericValue x : listGe){
    				Map<String, Object> row = new HashMap<String, Object>();
    				
    				row.put("agreementId", (String)x.get("agreementId"));
    				row.put("orderId", (String)x.get("orderId"));
    				
    				List<GenericValue> listOrderAndCont = delegator.findList("OrderAndContainer", EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String)x.get("orderId"))), null, null, null, false);
    				GenericValue orderAndCont = EntityUtil.getFirst(listOrderAndCont);
    				if(orderAndCont != null){
    					String containerId = (String)orderAndCont.get("containerId");
    					if(containerId != null){
    						GenericValue container = delegator.findOne("Container", UtilMisc.toMap("containerId", containerId), false);
    						row.put("containerId", (String)container.get("containerId"));
    						row.put("containerNumber", (String)container.get("containerNumber"));
    					}
    					
    					String billId = (String)orderAndCont.get("billId");
    					if(billId != null){
    						GenericValue bill = delegator.findOne("BillOfLading", UtilMisc.toMap("billId", billId), false);
    						row.put("billNumber", (String)bill.get("billNumber"));
    						row.put("billId", (String)bill.get("billId"));
    						Timestamp deparDate = (Timestamp)bill.get("departureDate");
    						if(deparDate != null){
    							String departureDate = yearMonthDayFormat2.format(new Date(deparDate.getTime()));
    							row.put("departureDate", deparDate);
    						}
    						
    						Timestamp arrivalDateStamp = (Timestamp)bill.get("arrivalDate");
    						if(arrivalDateStamp != null){
    							String arrivalDate = yearMonthDayFormat2.format(new Date(arrivalDateStamp.getTime()));
    							row.put("arrivalDate", arrivalDateStamp);
    						}
    					}
    				}
    				Timestamp agreeDate = (Timestamp)x.get("agreementDate");
//    				String dateStr = agreeDate.toString();
    				Date date = new Date(agreeDate.getTime());

    			    
    				String dateStr2 = yearMonthDayFormat2.format(date);
    				row.put("agreementDate", agreeDate);
//    				List<GenericValue> listStatus = delegator.findList("StatusItem", EntityCondition.makeCondition(UtilMisc.toMap("statusTypeId", "AGREEMENT_STATUS")), null, null, null, false);
    				
//    				row.put("listStatusId", listStatus);
    				row.put("statusId", (String)x.get("statusId"));
    				row.put("partyRentId", "company");
//    				row.put("", value);
    				
    				List<GenericValue> listDetail = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String)x.get("orderId"))), null, null, null, false);
    				List<Map<String, String>> rowDetail = new ArrayList<Map<String,String>>();
    				if(!UtilValidate.isEmpty(listDetail)){
    					for(GenericValue detail : listDetail){
    						Map<String, String> childDetail = new HashMap<String, String>(); 
//    						childDetail.ad
    						childDetail.put("orderId", (String)detail.get("orderId"));
    						childDetail.put("orderItemSeqId", (String)detail.get("orderItemSeqId"));
    						childDetail.put("productId", (String)detail.get("productId"));
    						childDetail.put("quantity", ((BigDecimal)detail.getBigDecimal("quantity")).toString());
    						Timestamp expireDateShowTimestamp = (Timestamp)detail.get("expireDate");
    						if(expireDateShowTimestamp != null){
    							String exprDateString = yearMonthDayFormat2.format(new Date(expireDateShowTimestamp.getTime()));
    							childDetail.put("expireDate", exprDateString);
    						}
    						Timestamp manufacTimestamp = (Timestamp)detail.get("datetimeManufactured");
    						if(manufacTimestamp != null){
    							String manufacStr = yearMonthDayFormat2.format(new Date(manufacTimestamp.getTime()));
    							childDetail.put("datetimeManufactured", manufacStr);
    						}
    						rowDetail.add(childDetail);
    						
    					}
    				}
    				row.put("rowDetail", rowDetail);
    				listIterator.add(row);
    			}
    		}
    		
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling jqGetListProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
		
	public static Map<String,Object> sendNotifyToStorekeepers(DispatchContext ctx, Map<String,Object> context){
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String facilityId = (String)context.get("facilityId");
		String action = (String)context.get("action");
		String targetLink = (String)context.get("targetLink");
		String header = (String)context.get("header");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		Timestamp openTime = (Timestamp)context.get("openTime");
		Timestamp dateTime = (Timestamp)context.get("dateTime");
		try {
			List<GenericValue> listLogStorekeepers = delegator.findList("FacilityParty", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId, "roleTypeId", "LOG_STOREKEEPER")), null, null, null, false);
			if (!listLogStorekeepers.isEmpty()){
				for(GenericValue keeper : listLogStorekeepers){
					String partyId = (String)keeper.get("partyId");
					Map<String,Object> contextTmp = new HashMap<String, Object>();
					contextTmp.put("partyId", partyId);
					contextTmp.put("action", action);
					contextTmp.put("targetLink", targetLink);
					contextTmp.put("header", header);
					contextTmp.put("openTime", openTime);
					contextTmp.put("dateTime", dateTime);
					contextTmp.put("userLogin", userLogin);
					try {
						dispatcher.runSync("createNotification", contextTmp);
					} catch (GenericServiceException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}
	public static String updateDamagedQuantity(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
        Locale locale = UtilHttp.getLocale(request);
		List<GenericValue> listReasons = delegator.findList("VarianceReason", null, null, null, null, false);
		String receiptId = (String)context.get("receiptId_o_0");
		GenericValue receipt = delegator.findOne("Receipt", false, UtilMisc.toMap("receiptId", receiptId));
		Timestamp receiptDate = (Timestamp)receipt.get("receiptDate");
		DateTimeComparator comparator = DateTimeComparator.getDateOnlyInstance();
		if (comparator.compare(receiptDate, UtilDateTime.nowTimestamp()) == 0){
			List<GenericValue> listItemToReceives = delegator.findList("ReceiptItem", EntityCondition.makeCondition(UtilMisc.toMap("receiptId", receiptId)), null, null, null, false);
			if (!listItemToReceives.isEmpty()){
				int i = 0;
				for (GenericValue item : listItemToReceives){
					BigDecimal totalRejected = BigDecimal.ZERO;
					if (!listReasons.isEmpty()){
						for (GenericValue reason : listReasons){
							String qtyTmp = (String)context.get(reason.get("varianceReasonId")+"_o_"+i);
							String partyId = (String)context.get(reason.get("varianceReasonId")+"_partyId_o_"+i);
							BigDecimal quantity = BigDecimal.ZERO;
							if (qtyTmp != null){
								quantity = new BigDecimal(qtyTmp);
							}
							if (quantity.compareTo(BigDecimal.ZERO) == 1){
								GenericValue receiptItemAndVariance = delegator.makeValue("ReceiptItemAndVariance");
								receiptItemAndVariance.put("receiptId", receiptId);
								receiptItemAndVariance.put("receiptItemSeqId", item.get("receiptItemSeqId"));
								receiptItemAndVariance.put("quantity", quantity);
								receiptItemAndVariance.put("varianceReasonId", reason.get("varianceReasonId"));
								receiptItemAndVariance.put("partyId", partyId);
								delegator.createOrStore(receiptItemAndVariance);
							}
							totalRejected = totalRejected.add(quantity);
						}
					}
					item.put("quantityRejected", totalRejected);
					delegator.store(item);
					i = i + 1;
				}
			}
		} else {
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
                    "NotTimeToUpdate", locale));
		}
		request.setAttribute("receiptId", receiptId);
		return "sucess";
	}
	
	public static Map<String, Object> removeOrderItem(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String orderId = (String)context.get("orderId");
//		String orderItemSeqId = (String)context.get("orderItemSeqId");
		
		List<GenericValue> listOrderItem = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		for(GenericValue orderItem : listOrderItem){
			GenericValue orderItemGe = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", (String)orderItem.get("orderId"), "orderItemSeqId", (String)orderItem.get("orderItemSeqId")), false);
			
//			orderItem
			BigDecimal a = new BigDecimal(200);
			orderItemGe.put("cancelQuantity", a);
			try{
				delegator.store(orderItemGe);
			}catch(Exception e){}
		}
		return ServiceUtil.returnSuccess();
	}
	
	public static Map<String, Object> createImportPlanWeek(DispatchContext ctx, Map<String,Object> context) throws ParseException{
		Delegator delegator = ctx.getDelegator();
		Map<String,Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		String json =(String)context.get("json");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
//		String planId = (String)context.get("planId");
		JSONArray jsonArrTotal = new JSONArray().fromObject(json);
	
		for(int h = 0; h < jsonArrTotal.size(); h++){
			
			JSONObject jsonObjTotal = jsonArrTotal.getJSONObject(h);
			JSONArray jsonArr = jsonObjTotal.getJSONArray("total");
			
			boolean created = false;
			for(int i = 0; i < jsonArr.size(); i++){
				
				JSONObject jsonObj = jsonArr.getJSONObject(i);
				JSONArray jsonArr2 = jsonObj.getJSONArray("product");
	//create Lot
				String productPlanHeader = jsonObj.getString("productPlanHeader");
				String customTimePeriodId = jsonObj.getString("customTimePeriodId");
				String productPlanName = jsonObj.getString("productPlanName");
				String internalPartyId = jsonObj.getString("internalPartyId");
				List<GenericValue> listPlanTimePeriod = new ArrayList<GenericValue>();
				try {
					//thieu dieu kien vung
					//tim plan tuan
					listPlanTimePeriod = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "internalPartyId", internalPartyId, "productPlanTypeId", "IMPORT_PLAN")), null, null, null, false);
				} catch (GenericEntityException e1) {
					// TODO Auto-generated catch block
					return ServiceUtil.returnError(UtilProperties.getMessage(resource,
							"CommonNoteCannotBeUpdated",
							UtilMisc.toMap("errorString", e1.getMessage()), locale));
				}
				
				if(UtilValidate.isEmpty(listPlanTimePeriod)){
					created = true;
				}
				if(created == true){
					GenericValue productPlanHeaderGe = null;
					try {
						productPlanHeaderGe = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanHeader), false);
					} catch (GenericEntityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String statusId = "PLAN_CREATED";
					String sttCurent = (String)productPlanHeaderGe.get("statusId");
					if(sttCurent.equals("PLAN_CREATED")){
						productPlanHeaderGe.put("statusId", "PLAN_APPORTION");
					}else{
						productPlanHeaderGe.put("statusId", "PLAN_MODIFIED");
					}
					try {
						delegator.store(productPlanHeaderGe);
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError(UtilProperties.getMessage(resource,
								"CommonNoteCannotBeUpdated",
								UtilMisc.toMap("errorString", e.getMessage()), locale));
					}
					GenericValue createByUserGe = (GenericValue)context.get("userLogin");
					String createByUser = (String)createByUserGe.get("userLoginId");
					String modifiedByUserLoginId = null;
					String parentOfWeek;
					try {
						parentOfWeek = CreateImportPlanHeader(delegator, productPlanHeader, (String)productPlanHeaderGe.get("productPlanTypeId"), customTimePeriodId, (String)productPlanHeaderGe.get("currencyUomId"), createByUser, modifiedByUserLoginId, productPlanName, (String)productPlanHeaderGe.get("organizationPartyId"), (String)productPlanHeaderGe.get("internalPartyId"), (String)productPlanHeaderGe.get("supplierPartyId"), statusId, (String)productPlanHeaderGe.get("customTimePeriodIdOfSales"));
					} catch (GenericEntityException e) {
						return ServiceUtil.returnError(UtilProperties.getMessage(resource,
								"CommonNoteCannotBeUpdated",
								UtilMisc.toMap("errorString", e.getMessage()), locale));
					}
				
				
					for(int j = 0; j < jsonArr2.size(); j++){
						JSONObject jsonObj2 = jsonArr2.getJSONObject(j);
						
						//get du lieu cua tung san pham trong bang html
						String quantity = jsonObj2.getString("quantity");
		//				BigDecimal quantityBig = BigDecimal
						DecimalFormat decimalFormat = new DecimalFormat();
						decimalFormat.setParseBigDecimal(true);
						BigDecimal quantityBig = (BigDecimal) decimalFormat.parse(quantity);
						String productId = jsonObj2.getString("productId");
						String quantityUomId = jsonObj2.getString("quantityUomId");
		//				String productPlanType = "IMPORT_PLAN";
						GenericValue productPlanItem = delegator.makeValue("ProductPlanItem");
						productPlanItem.put("productPlanId", parentOfWeek);
						delegator.setNextSubSeqId(productPlanItem, "productPlanItemSeqId", 5, 1);
						productPlanItem.put("productId", productId);
						productPlanItem.put("planQuantity", quantityBig);
						productPlanItem.put("quantityUomId", quantityUomId);
						productPlanItem.put("statusId", statusId);
						try {
							delegator.create(productPlanItem);
						} catch (GenericEntityException e) {
							return ServiceUtil.returnError(UtilProperties.getMessage(resource,
									"CommonNoteCannotBeUpdated",
									UtilMisc.toMap("errorString", e.getMessage()), locale));
						}
						
		//create product plan and lot
						
					}
				}
				else{
					
					try {
						GenericValue productPlanHeaderMonth = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanHeader), false);
						String stt = (String)productPlanHeaderMonth.get("statusId");
						if(!stt.equals("PLAN_COMPLETED") && !stt.equals("PLAN_PROCESSING")){
							Map<String, Object> contextPlanMonth = new HashMap<String, Object>();
							contextPlanMonth.put("statusId", "PLAN_MODIFIED");
							contextPlanMonth.put("productPlanId", productPlanHeader);
							contextPlanMonth.put("userLogin", userLogin);
							try {
								dispatcher.runSync("updateSttPlanHeader", contextPlanMonth);
							} catch (GenericServiceException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (GenericEntityException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					
					GenericValue planTime = EntityUtil.getFirst(listPlanTimePeriod);
					String curStt = (String)planTime.get("statusId");
//					if("PLAN_MODIFIED".equals(curStt) || "PLAN_CREATED".equals(curStt)){
					if(!curStt.equals("PLAN_COMPLETED") && !curStt.equals("PLAN_PROCESSING") && !curStt.equals("PLAN_ORDERED") && !curStt.equals("PLAN_CONT_CREATED")){
						Map<String, Object> contextPlan = new HashMap<String, Object>();
						contextPlan.put("statusId", "PLAN_MODIFIED");
						contextPlan.put("productPlanId", (String)planTime.get("productPlanId"));
						contextPlan.put("userLogin", userLogin);
						try {
							dispatcher.runSync("updateSttPlanHeader", contextPlan);
						} catch (GenericServiceException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
							return ServiceUtil.returnError(e1.getMessage());
						}
						for(int j = 0; j < jsonArr2.size(); j++){
							JSONObject jsonObj2 = jsonArr2.getJSONObject(j);
							
							//get du lieu cua tung san pham trong bang html
							String quantity = jsonObj2.getString("quantity");
			//				BigDecimal quantityBig = BigDecimal
							DecimalFormat decimalFormat = new DecimalFormat();
							decimalFormat.setParseBigDecimal(true);
							BigDecimal quantityBig = (BigDecimal) decimalFormat.parse(quantity);
							String productId = jsonObj2.getString("productId");
							String quantityUomId = jsonObj2.getString("quantityUomId");
							
							
							List<GenericValue> listPlanItem = new ArrayList<GenericValue>();
							try {
								listPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", (String)planTime.get("productPlanId"), "productId", productId)),  null, null, null, false);
							} catch (GenericEntityException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			//				String productPlanType = "IMPORT_PLAN";
							if(!UtilValidate.isEmpty(listPlanItem)){
								GenericValue planItem = EntityUtil.getFirst(listPlanItem);
								planItem.put("planQuantity", quantityBig);
								try {
									delegator.store(planItem);
								} catch (GenericEntityException e) {
									return ServiceUtil.returnError(UtilProperties.getMessage(resource,
											"CommonNoteCannotBeUpdated",
											UtilMisc.toMap("errorString", e.getMessage()), locale));
								}
							}else{
								GenericValue productPlanItem = delegator.makeValue("ProductPlanItem");
								productPlanItem.put("productPlanId", (String)planTime.get("productPlanId"));
								delegator.setNextSubSeqId(productPlanItem, "productPlanItemSeqId", 5, 1);
								productPlanItem.put("productId", productId);
								productPlanItem.put("planQuantity", quantityBig);
								productPlanItem.put("quantityUomId", quantityUomId);
								productPlanItem.put("statusId", "PLAN_CREATED");
								try {
									delegator.create(productPlanItem);
								} catch (GenericEntityException e) {
									return ServiceUtil.returnError(UtilProperties.getMessage(resource,
											"CommonNoteCannotBeUpdated",
											UtilMisc.toMap("errorString", e.getMessage()), locale));
								}
							}
						}
					}
				}
			}
	}
		return ServiceUtil.returnSuccess();
		
	}
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> resultPlanOfYear(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> retunrMap = new FastMap<String, Object>();
		Locale locale = (Locale)context.get("locale");
		List<Map<String, Object>> resultPro = new ArrayList<Map<String,Object>>();
		String productPlanHeaderId = (String)context.get("productPlanHeaderId");
		GenericValue prPlanHeader = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanHeaderId), false);
		List<String> fieldHeader = new ArrayList<String>();
		fieldHeader.add("productPlanId");
		List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", productPlanHeaderId)), null, fieldHeader, null, false);
		List<String> listProductId = new ArrayList<String>();
		List<Map<String, Object>> listProductIdName = new ArrayList<Map<String,Object>>();
		for(GenericValue planGe : listProductPlanHeader){
			String productPlanHeader = (String)planGe.get("productPlanId");

			GenericValue productPlanHeaderGe = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanHeader), false);
			String customTimePeriodId = (String)productPlanHeaderGe.get("customTimePeriodId");
			String customTimePeriodIdOfSales = (String)productPlanHeaderGe.get("customTimePeriodIdOfSales");
			GenericValue year = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);

			List<String> fieldOrders = new ArrayList<String>();
			fieldOrders.add("productId");
			
			List<GenericValue> listItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeader)), null, fieldOrders, null, false);
			//BigDecimal palletTotal = BigDecimal.ZERO;

			//List<Map> result = new ArrayList<Map>();
			List<Map> listProductAndQuantity = new ArrayList<Map>();
			int totalPallet = 0;
			for(GenericValue x : listItem){
				
				Map<String, Object> productQuantity = new FastMap<String, Object>();
				String productId = (String)x.get("productId");
				productQuantity.put("productId", productId);
				productQuantity.put("quantityUomId", (String)x.get("quantityUomId"));
				BigDecimal planQuantityBig = (BigDecimal)x.getBigDecimal("planQuantity");
				int planQuantityInt = planQuantityBig.intValue();
				productQuantity.put("quantity", planQuantityInt);

				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", (String)x.get("productId")), false);
				String uomOfProduct = (String)product.get("quantityUomId");
				String nameProduct = (String)product.get("internalName");
				//	Set<String> fieldToSelects = FastSet.newInstance();
				//	fieldToSelects.add("quantityConvert");
				GenericValue cfPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId", (String)x.get("productId"), "uomFromId", "PALLET", "uomToId", uomOfProduct), false);
				int quantityPallet = planQuantityInt;
				int quantityConvert = 1;
				//	listProductAndQuantity.add(productQuantity);
				if(cfPacking != null){
					quantityConvert = ((BigDecimal)cfPacking.getBigDecimal("quantityConvert")).intValue();
//					productQuantity.put("quantityConvert", quantityConvert);
					quantityPallet = planQuantityInt/(quantityConvert);
//					productQuantity.put("quantityPallet", quantityPallet);
				}
				productQuantity.put("quantityConvert", quantityConvert);
				productQuantity.put("quantityPallet", quantityPallet);
				totalPallet = totalPallet + quantityPallet;
				
				listProductAndQuantity.add(productQuantity);

				if(!listProductId.contains((String)x.get("productId"))){
						listProductId.add((String)x.get("productId"));
						Map<String, Object> nameProMap = new FastMap<String, Object>();
						nameProMap.put("productId", (String)x.get("productId"));
						nameProMap.put("productName", nameProduct);
						nameProMap.put("quantityConvert", quantityConvert);
						nameProMap.put("inventory", getInventoryOfProductInMonth(delegator,(String)x.get("productId"), (Date)year.getDate("thruDate")));
						listProductIdName.add(nameProMap);
				}
//				listProdut
			}

			Date fromDateYear = (Date)year.getDate("fromDate");

			Calendar calendar = Calendar.getInstance();
			calendar.setTime(fromDateYear);
			int yearInt = calendar.get(Calendar.YEAR);
			String yearStr = String.valueOf(yearInt);
//			String yearStr = calendar.get(Calendar.YEAR).toString();

			//System.out.println("aa: "+yearStr);
			
			List<String> orderField = new ArrayList<String>();
			orderField.add("customTimePeriodId");

			List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
			GenericValue listCustomTimeMonth = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);

			Map<String, Object> resultMap = new FastMap<String, Object>();
			if(listCustomTimeMonth != null){
				List<Map<String, Object>> listWeek = new ArrayList<Map<String, Object>>();
				Set<String> fieldSl = FastSet.newInstance();
				fieldSl.add("customTimePeriodId");
				fieldSl.add("periodName");
				fieldSl.add("fromDate");
				List<GenericValue> listCustomTimeWeek = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", (String)listCustomTimeMonth.get("customTimePeriodId"), "periodTypeId", "IMPORT_WEEK")), fieldSl, orderField, null, false);
				for(GenericValue customTimeWeek : listCustomTimeWeek){

					calendar.setTime((Date)customTimeWeek.getDate("fromDate"));
					String weekOfYear = String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
					List<GenericValue> productPlanItemChild = new ArrayList<GenericValue>();
					List<GenericValue> childProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", productPlanHeader, "customTimePeriodId",(String)customTimeWeek.get("customTimePeriodId"))), null, fieldHeader, null, false);
					GenericValue child = EntityUtil.getFirst(childProductPlanHeader);
					String productPlanHeaderWeek = "";
					String productPlanStatusId = "";
					String thisStatusWeek = "";
					if(child != null){
						productPlanItemChild = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", (String)child.get("productPlanId"))), null, fieldOrders, null, false);
						productPlanHeaderWeek = (String)child.get("productPlanId");
						productPlanStatusId = (String)child.get("statusId");
						GenericValue statusWeek = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", productPlanStatusId), false);
						thisStatusWeek = (String) statusWeek.get("description", locale);
					}
					Map<String, Object> mapWeek = new FastMap<String, Object>();
					customTimeWeek.set("periodName", customTimeWeek.get("periodName").toString().split(" ")[1]);
					mapWeek.put("timeWeek", customTimeWeek);
					mapWeek.put("productPlanHeaderWeek", productPlanHeaderWeek);
					mapWeek.put("weekOfYear", weekOfYear);
					mapWeek.put("product", productPlanItemChild);
					mapWeek.put("productPlanHeader", productPlanHeader);
					mapWeek.put("statusId", productPlanStatusId);
					mapWeek.put("thisStatusWeek", thisStatusWeek);
					listWeek.add(mapWeek);

				}
				listCustomTimeMonth.set("periodName", listCustomTimeMonth.get("periodName").toString().split(" ")[1]);
				Map<String, Object> map = new FastMap<String, Object>();
				int contQuantity = totalPallet/33;
				map.put("month", listCustomTimeMonth);
				map.put("week", listWeek);
				map.put("product", listProductAndQuantity);
				map.put("container", contQuantity);
				map.put("productPlanHeader", (String)planGe.get("productPlanId"));
				String statusId = (String)planGe.get("statusId");
				GenericValue status = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				String thisStatus = (String) status.get("description", locale);
				map.put("StatusId", statusId);
				map.put("thisStatus", thisStatus);
				resultMap = map;
				
			}
			resultPro.add(resultMap);
			
		}
		retunrMap.put("resultPro", resultPro);
		retunrMap.put("productId", listProductIdName);
		retunrMap.put("internalPartyId", (String)prPlanHeader.get("internalPartyId"));
		retunrMap.put("productPlanName", (String)prPlanHeader.get("productPlanName"));
		retunrMap.put("customTimePeriodId", (String)prPlanHeader.get("customTimePeriodId"));
		return retunrMap;
	}
	private static BigDecimal getInventoryOfProductInMonth(Delegator delegator, String productId, Date thruDate) throws GenericEntityException {
		BigDecimal inventory = BigDecimal.ZERO;
		List<GenericValue> listDateDimension = delegator.findList("DateDimension", EntityCondition.makeCondition("dateValue", thruDate), null, null, null, false);
		List<GenericValue> listProductDimension = delegator.findList("ProductDimension", EntityCondition.makeCondition("productId", productId), null, null, null, false);
		if (UtilValidate.isNotEmpty(listProductDimension) && UtilValidate.isNotEmpty(listDateDimension)) {
			GenericValue dateDimension = EntityUtil.getFirst(listDateDimension);
			GenericValue productDimension = EntityUtil.getFirst(listProductDimension);
			long dateDimId = (Long) dateDimension.get("dimensionId");
			long productDimId = (Long) productDimension.get("dimensionId");
			long facilityDimIdTemp = 8000;
			List<GenericValue> listFacilityFact = delegator.findList("FacilityFact", EntityCondition.makeCondition(UtilMisc.toMap("facilityDimId", facilityDimIdTemp, "productDimId", productDimId, "dateDimId", dateDimId)), null, null, null, false);
			if (UtilValidate.isNotEmpty(listFacilityFact)) {
				GenericValue facilityFact = EntityUtil.getFirst(listFacilityFact);
				inventory = facilityFact.getBigDecimal("inventoryTotal");
			}
		}
		return inventory;
	}
	public static Map<String,Object> updateDeliveryStatus(DispatchContext ctx, Map<String,Object> context){
		Map<String,Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String deliveryId = (String)context.get("deliveryId");
		String newStatusId = (String)context.get("newStatusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = (String)userLogin.get("userLoginId");
		try {
			GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
			if (newStatusId != null && delivery != null){
				delivery.put("statusId", newStatusId);
				delegator.store(delivery);
			}
			GenericValue deliveryStatus = delegator.makeValue("DeliveryStatus");
			String deliveryStatusId = delegator.getNextSeqId("DeliveryStatus");
			deliveryStatus.put("deliveryStatusId", deliveryStatusId);
			deliveryStatus.put("deliveryId", deliveryId);
			deliveryStatus.put("statusId", newStatusId);
			deliveryStatus.put("statusDatetime", UtilDateTime.nowTimestamp());
			deliveryStatus.put("statusUserLogin", userLoginId);
			delegator.create(deliveryStatus);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static String updateDeliveryItem(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException {
		Delegator delegator = (Delegator) request.getAttribute("delegator");
        Map<String, Object> context = UtilHttp.getCombinedMap(request);
        Locale locale = UtilHttp.getLocale(request);
		String deliveryId = (String)context.get("deliveryId_o_0");
		GenericValue delivery = delegator.findOne("Delivery", false, UtilMisc.toMap("deliveryId", deliveryId));
		Timestamp deliveryDate = (Timestamp)delivery.get("deliveryDate");
		DateTimeComparator comparator = DateTimeComparator.getDateOnlyInstance();
		if (comparator.compare(deliveryDate, UtilDateTime.nowTimestamp()) == 0){
			List<GenericValue> listDeliveryItems = delegator.findList("DeliveryItem", EntityCondition.makeCondition(UtilMisc.toMap("deliveryId", deliveryId)), null, null, null, false);
			int i = 0;
			for (GenericValue item : listDeliveryItems){
				String actualQty = (String)context.get("actualQuantity_o_"+i);
				if (actualQty != null){
					BigDecimal actualQuantity = new BigDecimal(actualQty);
					if (actualQuantity.compareTo(BigDecimal.ZERO) == 1){
						item.put("actualQuantity", actualQuantity);
						delegator.store(item);
					}
				}
				i = i++;
			}
		} else {
			request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage(resource,
                "NotTimeToUpdate", locale));
		}
		request.setAttribute("deliveryId", deliveryId);
		return "sucess";
	}
	
	public static Map<String, Object> getProductPlanAndLot(Delegator delegator, String productPlanHeaderId) throws GenericEntityException{
		Set<String> field = new FastSet<String>();
		field.add("lotId");
		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);

		List<Map<String, Object>> productId = new ArrayList<Map<String, Object>>();
		Set<String> fieldPro = new FastSet<String>();
		fieldPro.add("productId");
		fieldPro.add("internalName");
		fieldPro.add("description");
		List<String> orderField = new ArrayList<String>();
		orderField.add("productId");
		List<GenericValue> listProductId = delegator.findList("LotAndPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeaderId)), fieldPro, orderField, options, false);
		for(GenericValue pp: listProductId){
			Map<String, Object> mapPro = new FastMap<String, Object>();
			mapPro.put("productId", (String)pp.get("productId"));
			mapPro.put("internalName", (String)pp.get("internalName"));
			mapPro.put("description", (String)pp.get("description"));
			productId.add(mapPro);
		}

		List<GenericValue> listLot = delegator.findList("ProductPlanAndLot", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeaderId)), field, null, options, false);

		List<Map<String, Object>> listLotToDoAgreement = new ArrayList<Map<String,Object>>();
		for(GenericValue x: listLot){
			List<GenericValue> listProductLot = delegator.findList("LotAndPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("lotId", (String)x.get("lotId"))), null, orderField, null, false);
			Map<String, Object> map = new FastMap<String, Object>();
			map.put("listProductLot", listProductLot);
			map.put("lot", (String)x.get("lotId"));
			listLotToDoAgreement.add(map);
		}
		
		Map<String, Object> context = new FastMap<String, Object>();
		context.put("listLotToDoAgreement", listLotToDoAgreement);
		context.put("listProductIdHeader", productId);
		return context;
		
	}
	
	public static Map<String, Object> getProductPlanAndLotJQX(Delegator delegator, String productPlanHeaderId) throws GenericEntityException{
		Set<String> field = new FastSet<String>();
		field.add("lotId");
		EntityFindOptions options = new EntityFindOptions();
		options.setDistinct(true);
		List<String> orderField = new ArrayList<String>();
		orderField.add("productId");
		List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeaderId)), null, null, null, false);

		List<GenericValue> listLot = delegator.findList("ProductPlanAndLot", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeaderId)), field, null, options, false);

		List<Map<String, Object>> listLotToDoAgreement = new ArrayList<Map<String,Object>>();
		for(GenericValue x: listLot){
			Map<String, Object> row = FastMap.newInstance();
			row.put("lotId", (String)x.get("lotId"));
			
			List<GenericValue> listProductPlanAndOrder = delegator.findList("ProductPlanAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("lotId", x.getString("lotId"))), null, null, null, false);
			List<GenericValue> listFiltered = EntityUtil.filterByDate(listProductPlanAndOrder);
			GenericValue productPlanAndOrder = null;
			if(UtilValidate.isNotEmpty(listFiltered)){
				productPlanAndOrder = EntityUtil.getFirst(listFiltered);
			}
			String agreementId = "";
			String orderId = "";
			String agreementName = "";
			String statusId = "";
			String statusDescription = "";
			if(productPlanAndOrder != null){
				agreementId = productPlanAndOrder.getString("agreementId");
				orderId = productPlanAndOrder.getString("orderId");
			}
			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
			if(agreement != null){
				statusId = agreement.getString("statusId");
				GenericValue statusItem = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
				statusDescription = statusItem.getString("description");
			}
			row.put("statusId", statusId);
			row.put("statusDescription", statusDescription);
			
			row.put("agreementId", agreementId);
			GenericValue agreementAttr = delegator.findOne("AgreementAttribute", UtilMisc.toMap("agreementId", agreementId, "attrName", "AGREEMENT_NAME"), false);
			if(agreementAttr != null){
				agreementName = agreementAttr.getString("attrValue");
			}
			row.put("agreementName", agreementName);
			row.put("orderId", orderId);
			row.put("productPlanId", productPlanHeaderId);
			for(GenericValue productPlanItem:listProductPlanItem){
				BigDecimal quantityPlan = (BigDecimal)productPlanItem.getBigDecimal("planQuantity");
				String productIdItem = (String)productPlanItem.get("productId");
				if(quantityPlan.compareTo(new BigDecimal(0))==1){
					BigDecimal quantity = new BigDecimal(0);
					List<GenericValue> listProductLot = delegator.findList("LotAndPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("lotId", (String)x.get("lotId"), "productPlanId", productPlanHeaderId, "productId", productIdItem)), null, orderField, null, false);
					if(!UtilValidate.isEmpty(listProductLot)){
						GenericValue productLot = EntityUtil.getFirst(listProductLot);
						quantity = (BigDecimal)productLot.get("lotQuantity");
					}
					row.put("quantity_"+productIdItem, quantity);
				}
			}
			listLotToDoAgreement.add(row);
		}
		
		Map<String, Object> context = new FastMap<String, Object>();
		context.put("listLotToDoAgreement", listLotToDoAgreement);
//		context.put("listProductIdHeader", productId);
		return context;
		
	}

	public static void devidePlanToContainer(Delegator delegator, String productPlanHeader) throws GenericEntityException{

		List<String> fieldOrders = new ArrayList<String>();
		fieldOrders.add("productId");
		List<GenericValue> listItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeader)), null, fieldOrders, null, false);
		List<Map<String, Object>> listGreaterCont = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> listLessCont = new ArrayList<Map<String, Object>>();
		for(GenericValue x : listItem){
			Map<String, Object> productQuantity = new FastMap<String, Object>();
			productQuantity.put("productId", (String)x.get("productId"));
			productQuantity.put("productPlanId", productPlanHeader);
			productQuantity.put("productPlanItemSeqId", (String)x.get("productPlanItemSeqId"));
			BigDecimal planQuantityBig = (BigDecimal)x.get("planQuantity");
			int planQuantityInt = planQuantityBig.intValue();
			productQuantity.put("quantity",planQuantityInt);
			
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", (String)x.get("productId")), false);
			String uomOfProduct = (String)product.get("quantityUomId");
			GenericValue cfPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId", (String)x.get("productId"), "uomFromId", "PALLET", "uomToId", uomOfProduct), false);
			int quantityPallet = planQuantityInt;
			int quantityConvert = 1;
			if(cfPacking != null){
				quantityConvert = ((BigDecimal)cfPacking.get("quantityConvert")).intValue();
				productQuantity.put("quantityConvert", quantityConvert);
				quantityPallet = planQuantityInt/(quantityConvert);
				productQuantity.put("quantityPallet", quantityPallet);
			}

			if(quantityPallet < 33 && quantityPallet > 0){
				listLessCont.add(productQuantity);
			}else{
				listGreaterCont.add(productQuantity);
			}
		}

		List<List<Map<String, Object>>> resultDevide = new ArrayList<List<Map<String, Object>>>();
		int rePalletLess = 0;
		List<Map<String, Object>> oneContLess = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> less : listLessCont){
//			oneCont.add(less);
			int quantityLess = (Integer)less.get("quantityPallet");
			rePalletLess = rePalletLess + quantityLess;
			if(rePalletLess < 33 && rePalletLess > 0){
				oneContLess.add(less);
				
			}else{
				int re = rePalletLess%33;
				less.put("quantityPallet", quantityLess-re);
				Map<String, Object> lessTemp = new FastMap<String, Object>();
				lessTemp.putAll(less);
				oneContLess.add(lessTemp);
				List<Map<String, Object>> oneContLessTemp = new ArrayList<Map<String,Object>>();
				oneContLessTemp.addAll(oneContLess);
				oneContLess.clear();
				resultDevide.add(oneContLessTemp);
				rePalletLess = re;
				if(rePalletLess > 0){
					less.put("quantityPallet", rePalletLess);
					List<Map<String, Object>> otherTemp = new ArrayList<Map<String,Object>>();
					otherTemp.add(less);
					oneContLess = otherTemp;
				}
			}
			
		}
		
		if(!oneContLess.isEmpty()){
			int flag = 0;
			for(Map<String, Object> greater : listGreaterCont){
				if(flag == 0){
					int quantityGreater = (Integer)greater.get("quantityPallet");
					int remainPL = 33 - rePalletLess;
					if(quantityGreater < remainPL){
						greater.put("quantityPallet", quantityGreater);
						Map<String,Object> greaterTemp = new FastMap<String, Object>();
						greaterTemp.putAll(greater);
						oneContLess.add(greaterTemp);
						rePalletLess = rePalletLess + quantityGreater;
						greater.put("quantityPallet", 0);
					}else{
						greater.put("quantityPallet", remainPL);
						Map<String,Object> greaterTemp = new FastMap<String, Object>();
						greaterTemp.putAll(greater);
						oneContLess.add(greaterTemp);
						resultDevide.add(oneContLess);
						greater.put("quantityPallet", quantityGreater - remainPL);
						flag = 1;
					}
				}
			}
			
		}

		int addRemainPallet = 0;
		List<Map<String, Object>> oneContRe = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> greater : listGreaterCont){
			int realCont = ((Integer)greater.get("quantityPallet"))/33;
			int reCont = ((Integer)greater.get("quantityPallet"))%33;
			addRemainPallet = addRemainPallet + reCont;
			if(realCont > 0){
				for(int i = 0; i < realCont; i++){
					List<Map<String, Object>> oneCont = new ArrayList<Map<String, Object>>();
					greater.put("quantityPallet", 33);
					Map<String, Object> greatTemp = new FastMap<String, Object>();
					greatTemp.putAll(greater);
					oneCont.add(greatTemp);
					resultDevide.add(oneCont);
				}
			}
			
			if(addRemainPallet < 33){
				if(reCont > 0){
					greater.put("quantityPallet", reCont);
					Map<String,Object> greaterTemp = new FastMap<String, Object>();
					greaterTemp.putAll(greater);
					oneContRe.add(greaterTemp);
				}
			}else{
				greater.put("quantityPallet", reCont - (addRemainPallet%33));
				Map<String,Object> greaterTemp = new FastMap<String, Object>();
				greaterTemp.putAll(greater);
				oneContRe.add(greaterTemp);
				List<Map<String, Object>> oneTemp = new ArrayList<Map<String,Object>>();
				oneTemp.addAll(oneContRe);
				resultDevide.add(oneTemp);
				oneContRe.clear();
				addRemainPallet = addRemainPallet%33;
				greater.put("quantityPallet", addRemainPallet);
				if(addRemainPallet > 0){
					oneContRe.add(greater);
				}
			}
		}
		if(oneContRe.size() > 0){
			resultDevide.add(oneContRe);
		}
		//thuc hien lap trong resultDevide de insert CSDL
		insertProductPlanAndLot(delegator, resultDevide);
		
		updateSttPlanHeader(delegator, productPlanHeader, "CONTCREATED");
		
	}
	public static void updateSttPlanHeader(Delegator delegator, String productPlanId, String task) throws GenericEntityException{
		GenericValue productPlHeader = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId), false);
		if(task != null){
			if(task == "CONTCREATED"){
				productPlHeader.put("statusId", "PLAN_CONT_CREATED");
				delegator.store(productPlHeader);
			}
		}
	}
	
	public static void insertProductPlanAndLot(Delegator delegator, List<List<Map<String, Object>>> listResult) throws GenericEntityException{
		
		for(List<Map<String, Object>> listResultMap : listResult){
			String lotId = delegator.getNextSeqId("Lot");
			GenericValue lot = delegator.makeValue("Lot");
			lot.put("lotId", lotId);
			lot.put("creationDate", UtilDateTime.nowTimestamp());
			delegator.createOrStore(lot);
			for(Map<String, Object> resultMap : listResultMap){
		//create Lot
						String productPlanId = (String)resultMap.get("productPlanId");
						int quantityPallet = (Integer)resultMap.get("quantityPallet");
						int quantityConvert = (Integer)resultMap.get("quantityConvert");
						int quantity = quantityPallet * quantityConvert;
						BigDecimal quantityBig = new BigDecimal(quantity);
						String productPlanItemSeqId = (String)resultMap.get("productPlanItemSeqId");
		//create product plan and lot
						GenericValue lotPlan = delegator.makeValue("ProductPlanAndLot");
						lotPlan.put("lotId", lotId);
						lotPlan.put("productPlanId", productPlanId);
						lotPlan.put("productPlanItemSeqId", productPlanItemSeqId);
						lotPlan.put("lotQuantity", quantityBig);
		//Fix shipmentUomId va productPackingUomId
						lotPlan.put("shipmentUomId", "CONTAINER");
						lotPlan.put("productPackingUomId", "PALLET");
//						lotPlan.put("statusId", "LOT_CREATED");
						delegator.createOrStore(lotPlan);
			}
			
		}
		
		
	}
	public static Map<String,Object> devideContainer(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
        Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
//		String internalPartyId = (String)context.get("internalPartyId");
//		String customTimePeriodId = (String)context.get("customTimePeriodId");
		String productPlanId = (String)context.get("productPlanId");
		List<Map<String, Object>> listIterator = FastList.newInstance();
		GenericValue listProductPlanHeader = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId), false);
			String sttPlan = (String)listProductPlanHeader.get("statusId");
			// kiem tra STT plan week neu da tao container cho tuan roi thi STT la PLAN_CONT_CREATED va chi viec lay du lieu cont cua tuan do len
			//nguoc lai thi tao moiw container cho tuan sau do tra lai va hien thi
			if(sttPlan.equals("PLAN_CONT_CREATED") || sttPlan.equals("PLAN_ORDERED") || sttPlan.equals("PLAN_PROCESSING") || sttPlan.equals("PLAN_COMPLETED")){
//				result.put("contCreated", "container is created");
				result = getProductPlanAndLotJQX(delegator, productPlanId);
//				result.put("productPlanHeader", productPlanId);
				
//			}else if(sttPlan.equals("PLAN_APPROVED") || sttPlan.equals("PLAN_MODIFIED")){//sau do chuyen trang thai cua plan thanh PLAN_CONT_CREATED
			}else if(sttPlan.equals("PLAN_APPROVED")){//sau do chuyen trang thai cua plan thanh PLAN_CONT_CREATED
//				result.put("contCreated" , "container is not created");
				devidePlanToContainer(delegator, productPlanId);
				result = getProductPlanAndLotJQX(delegator, productPlanId);
//				result.put("productPlanHeader", productPlanWeekId);
			}else{
				result.put("contCreated", "Plan not have container because it wasn't approved, modified");
			}
			
		if(result.containsKey("listLotToDoAgreement")){
			listIterator = (List<Map<String, Object>>)result.get("listLotToDoAgreement");
		}
		Map<String, Object> resultSuccess = FastMap.newInstance();
		resultSuccess.put("listIterator", listIterator);
//		
        return resultSuccess;
	}
	
	public static Map<String, Object> JQGetAgreementFollowTime(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException, GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	SimpleDateFormat yearMonthDayFormat2 = new SimpleDateFormat("dd-MM-yyyy");
//    	List<EntityExpr> expr1 = FastList.newInstance();
//    	expr1.add(EntityCondition.makeCondition("agreementTypeId", EntityOperator.EQUALS, "PURCHASE_AGREEMENT"));
    	List<EntityExpr> expr2 = FastList.newInstance();
    	Map<String, String[]> param = (Map<String, String[]>)context.get("parameters");
    	String productPlanId = (String)param.get("prHeaderId")[0];
    	EntityCondition condAgree = EntityCondition.makeCondition("productPlanId", EntityOperator.EQUALS, productPlanId);
    	listAllConditions.add(condAgree);
    	//doan duoi day dung view-entity cua cac bang: ProductPlanAndOrder, AgreementAndOrder, AgreementAttributed
    	List<GenericValue> listProductPlanAndOrder = delegator.findList("ProductPlanOrderAgree", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, listSortFields, opts, false);
    	if(!UtilValidate.isEmpty(listProductPlanAndOrder)){
    		
    		for(GenericValue x : listProductPlanAndOrder){
    		
        				Map<String, Object> row = new HashMap<String, Object>();
        				
        				row.put("agreementId", (String)x.get("agreementId"));
        				row.put("orderId", (String)x.get("orderId"));
        				
        				List<GenericValue> listOrderAndCont = delegator.findList("OrderAndContainer", EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String)x.get("orderId"))), null, null, null, false);
        				GenericValue orderAndCont = EntityUtil.getFirst(listOrderAndCont);
        				if(orderAndCont != null){
        					String containerId = (String)orderAndCont.get("containerId");
        					if(containerId != null){
        						GenericValue container = delegator.findOne("Container", UtilMisc.toMap("containerId", containerId), false);
        						row.put("containerId", (String)container.get("containerId"));
        						row.put("containerNumber", (String)container.get("containerNumber"));
        					}
        					
        					String billId = (String)orderAndCont.get("billId");
        					if(billId != null){
        						GenericValue bill = delegator.findOne("BillOfLading", UtilMisc.toMap("billId", billId), false);
        						row.put("billNumber", (String)bill.get("billNumber"));
        						row.put("billId", (String)bill.get("billId"));
        						Timestamp deparDate = (Timestamp)bill.get("departureDate");
        						if(deparDate != null){
        							String departureDate = yearMonthDayFormat2.format(new Date(deparDate.getTime()));
        							row.put("departureDate", deparDate);
        						}
        						
        						Timestamp arrivalDateStamp = (Timestamp)bill.get("arrivalDate");
        						if(arrivalDateStamp != null){
        							String arrivalDate = yearMonthDayFormat2.format(new Date(arrivalDateStamp.getTime()));
        							row.put("arrivalDate", arrivalDateStamp);
        						}
        					}
        				}
        				Timestamp agreeDate = (Timestamp)x.get("agreementDate");
//        				String dateStr = agreeDate.toString();
        				Date date = new Date(agreeDate.getTime());

        			    
        				String dateStr2 = yearMonthDayFormat2.format(date);
        				row.put("agreementDate", agreeDate);
//        				List<GenericValue> listStatus = delegator.findList("StatusItem", EntityCondition.makeCondition(UtilMisc.toMap("statusTypeId", "AGREEMENT_STATUS")), null, null, null, false);
//        				
//        				row.put("statusId", listStatus);
        				row.put("statusId", (String)x.get("statusId"));
        				row.put("partyRentId", "company");
//        				row.put("", value);
        				
        				List<GenericValue> listDetail = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String)x.get("orderId"))), null, null, null, false);
        				List<Map<String, Object>> rowDetail = new ArrayList<Map<String,Object>>();
        				if(!UtilValidate.isEmpty(listDetail)){
        					for(GenericValue detail : listDetail){
        						Map<String, Object> childDetail = new HashMap<String, Object>(); 
//        						childDetail.ad
        						childDetail.put("orderId", (String)detail.get("orderId"));
        						childDetail.put("orderItemSeqId", (String)detail.get("orderItemSeqId"));
        						childDetail.put("productId", (String)detail.get("productId"));
        						childDetail.put("quantity", ((BigDecimal)detail.getBigDecimal("quantity")).toString());
        						Timestamp expireDateShowTimestamp = (Timestamp)detail.get("expireDate");
        						if(expireDateShowTimestamp != null){
        							String exprDateString = yearMonthDayFormat2.format(new Date(expireDateShowTimestamp.getTime()));
        							childDetail.put("expireDate", expireDateShowTimestamp);
        						}
        						Timestamp manufacTimestamp = (Timestamp)detail.get("datetimeManufactured");
        						if(manufacTimestamp != null){
        							String manufacStr = yearMonthDayFormat2.format(new Date(manufacTimestamp.getTime()));
        							childDetail.put("datetimeManufactured", manufacTimestamp);
        						}
        						rowDetail.add(childDetail);
        						
        					}
        				}
        				row.put("rowDetail", rowDetail);
        				listIterator.add(row);
        		}
    			
    		
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	public static void updateAgreementTerm(Delegator delegator, String agreementId, String termTypeIdValue, String textValue){
		try {
			List<GenericValue> listTerm = delegator.findList("AgreementTerm", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId, "termTypeId", termTypeIdValue)), null, null, null, false);
			listTerm = EntityUtil.filterByDate(listTerm);
			Boolean check = false;
			if(!listTerm.isEmpty()){
				for(GenericValue term : listTerm){
					if(!textValue.equals((String)term.get("textValue"))){
						term.put("thruDate", UtilDateTime.nowTimestamp());
						delegator.store(term);
					}else check = true;
				}
			}
			if(!check){
				GenericValue currentTerm = delegator.makeValue("AgreementTerm");
				currentTerm.put("agreementTermId", delegator.getNextSeqId("AgreementTerm"));
				currentTerm.put("termTypeId", termTypeIdValue);
				currentTerm.put("agreementId", agreementId);
				currentTerm.put("textValue", textValue);
				currentTerm.put("fromDate", UtilDateTime.nowTimestamp());
				delegator.createOrStore(currentTerm);
			}
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
