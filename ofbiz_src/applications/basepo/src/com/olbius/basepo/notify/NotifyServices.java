package com.olbius.basepo.notify;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastMap;

import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.security.Security;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;

import com.olbius.basehr.util.SecurityUtil;

public class NotifyServices {
	public static final String module = NotifyServices.class.getName();
	public static final String resource = "BasePOUiLabels";
	public static final String LOGISTICS_PROPERTIES = "baselogistics.properties";

	public static Map<String, Object> sendNotifyToLog(DispatchContext dpx, Map<String, ? extends Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String orderId = (String) context.get("orderId");
		Long shipByDate = (Long) context.get("shipByDate");
		LocalDispatcher dispatcher = dpx.getDispatcher();
		GenericValue oldUserLogin = (GenericValue) context.get("oldUserLogin");
		Timestamp shipByDateTsm = new Timestamp(shipByDate);
		Date shipDate = new Date(shipByDate);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(shipDate);
		List<String> listPartyGroups = SecurityUtil.getPartiesByRolesWithCurrentOrg(oldUserLogin,
				EntityUtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.manager.specialist", delegator),
				delegator);
		if (!listPartyGroups.isEmpty()) {
			for (String managerParty : listPartyGroups) {
				String sendMessage = "" + shipByDateTsm + " "
						+ UtilProperties.getMessage(resource, "BPONewPurchaseOrder", (Locale) context.get("locale")) + " ["
						+ orderId + "]";
//				String targetLink = "orderId=" + orderId;
				String sendToPartyId = managerParty;
				Map<String, Object> mapContext = new HashMap<String, Object>();
				mapContext.put("partyId", sendToPartyId);
				mapContext.put("action", "viewDetailPO?orderId=" + orderId);
				mapContext.put("targetLink", "");
				mapContext.put("header", sendMessage);
				mapContext.put("userLogin", oldUserLogin);
				mapContext.put("ntfType", "ONE");
				try {
					dispatcher.runSync("createNotification", mapContext);
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		result.put("userLogin", oldUserLogin);
		return result;
	}

	public static Map<String, Object> sendNotifyFromPOToLogReturn(DispatchContext dpx,
			Map<String, ? extends Object> context) {
		Delegator delegator = dpx.getDelegator();
		Security security = dpx.getSecurity();
		Map<String, Object> result = FastMap.newInstance();
		String returnId = (String) context.get("returnId");
		LocalDispatcher dispatcher = dpx.getDispatcher();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		boolean hasPermission = com.olbius.security.util.SecurityUtil.getOlbiusSecurity(security)
				.olbiusHasPermission(userLogin, null, "MODULE", "RETURNPO_APPROVE");
		if (hasPermission) {
			List<String> listPartyGroups = SecurityUtil.getPartiesByRolesWithCurrentOrg(userLogin,
					EntityUtilProperties.getPropertyValue(LOGISTICS_PROPERTIES, "role.manager.specialist", delegator),
					delegator);
			if (!listPartyGroups.isEmpty()) {
				for (String managerParty : listPartyGroups) {
					String sendMessage = UtilProperties.getMessage(resource, "NotiReturnToLog",
							(Locale) context.get("locale")) + ": " + " [" + returnId + "]";
					String targetLink = "returnId=" + returnId;
					String sendToPartyId = managerParty;
					Map<String, Object> mapContext = new HashMap<String, Object>();
					mapContext.put("partyId", sendToPartyId);
					mapContext.put("action", "getDetailVendorReturn");
					mapContext.put("targetLink", targetLink);
					mapContext.put("header", sendMessage);
					mapContext.put("userLogin", userLogin);
					mapContext.put("ntfType", "ONE");
					try {
						dispatcher.runSync("createNotification", mapContext);
					} catch (GenericServiceException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			result.put("userLogin", userLogin);
		}

		return result;
	}

	public static Map<String, Object> sendNotifyToAcc(DispatchContext dpx, Map<String, ? extends Object> context) {
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		String orderId = (String) context.get("orderId");
		String isEdit = (String) context.get("isEdit");
		LocalDispatcher dispatcher = dpx.getDispatcher();
		GenericValue oldUserLogin = (GenericValue) context.get("oldUserLogin");
		if (oldUserLogin == null)
			oldUserLogin = (GenericValue) context.get("userLogin");
		List<String> listPartyGroups = SecurityUtil.getPartiesByRolesWithCurrentOrg((oldUserLogin), "ACC_SALES_EMP",
				delegator);
		if (!listPartyGroups.isEmpty()) {
			for (String managerParty : listPartyGroups) {
				String sendMessage = ""
						+ UtilProperties.getMessage(resource, "BPONewPurchaseOrder", (Locale) context.get("locale")) + " ["
						+ orderId +"]";
				if ("Y".equals(isEdit)) {
					sendMessage = ""
							+ UtilProperties.getMessage(resource, "purchaseOrderEdit", (Locale) context.get("locale"))
							+ " [" + orderId + "]";
				}
				String targetLink = "orderId=" + orderId;
				String sendToPartyId = managerParty;
				Map<String, Object> mapContext = new HashMap<String, Object>();
				mapContext.put("partyId", sendToPartyId);
				mapContext.put("action", "viewDetailPO?" + targetLink);
				mapContext.put("header", sendMessage);
				mapContext.put("userLogin", oldUserLogin);
				mapContext.put("ntfType", "ONE");
				try {
					dispatcher.runSync("createNotification", mapContext);
				} catch (GenericServiceException e) {
					e.printStackTrace();
				}
			}
		}
		result.put("userLogin", oldUserLogin);
		return result;
	}
}
