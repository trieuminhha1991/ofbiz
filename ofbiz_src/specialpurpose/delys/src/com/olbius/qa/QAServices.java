package com.olbius.qa;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.joda.time.DateTimeComparator;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
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
import org.ofbiz.service.ServiceUtil;

import com.olbius.services.DelysServices;

public class QAServices {

	public static Role ROLE = null;
	public static GenericValue USER_LOGIN = null;
	public static String PARTY_ID = null;
	public enum Role {
		DELYS_ADMIN, DELYS_ROUTE, DELYS_ASM_GT, DELYS_RSM_GT, DELYS_CSM_GT, DELYS_CUSTOMER_GT, DELYS_SALESSUP_GT;
	}
	public static final String module = DelysServices.class.getName();
	public static final String resource = "DelysUiLabels";
	public static final String resourceError = "DelysErrorUiLabels";
	
	public static Map<String, Object> JQGetListProductByReceipt(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<GenericValue> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> param = (Map<String, String[]>)context.get("parameters");
    	String receiptId = (String)param.get("receiptId")[0];
    	EntityCondition cond = EntityCondition.makeCondition("receiptId", EntityOperator.EQUALS, receiptId);
    	listAllConditions.add(cond);
    		try {
				listIterator = delegator.findList("ReceiptItem", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				return ServiceUtil.returnError(e.getMessage());
			}
		
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	
	public static Map<String, Object> updateReceiptItemAjax(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String receiptId = (String)context.get("receiptId");
		String receiptItemSeqId = (String)context.get("receiptItemSeqId");
		String receiptStatusId = (String)context.get("statusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		String userLoginId = (String)userLogin.get("userLoginId");
		BigDecimal testQuantity = new BigDecimal(Integer.parseInt((String)context.get("testQuantity")));
		BigDecimal sampleQuantity = new BigDecimal(Integer.parseInt((String)context.get("sampleQuantity")));
		BigDecimal inspectQuantity = new BigDecimal(Integer.parseInt((String)context.get("inspectQuantity")));
		BigDecimal lackQuantity = new BigDecimal(Integer.parseInt((String)context.get("lackQuantity")));
		BigDecimal actualQuantity = new BigDecimal(Integer.parseInt((String)context.get("actualQuantity")));
		BigDecimal quantityRejected = new BigDecimal(0);
		String quantityRejectedStr = (String)context.get("quantityRejected");
		if(quantityRejectedStr != null){
			quantityRejected = new BigDecimal(Integer.parseInt((String)context.get("quantityRejected")));
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
//		String partyId = (String)context.get("partyId");
//		String action = (String)context.get("action");
//		String roleTypeId = (String)context.get("roleTypeId");
//		String targetLink = (String)context.get("targetLink");
//		String header = (String)context.get("header");
//		Timestamp openTime  = (Timestamp)context.get("openTime");
//		Timestamp dateTime  = (Timestamp)context.get("dateTime");
//		result.put("partyId", partyId);
//		result.put("action", action);
//		result.put("roleTypeId", roleTypeId);
//		result.put("targetLink", targetLink);
//		result.put("header", header);
//		result.put("openTime", openTime);
//		result.put("dateTime", dateTime);
//		result.put("receiptId", receiptId);
		
		return ServiceUtil.returnSuccess();
	}
}
