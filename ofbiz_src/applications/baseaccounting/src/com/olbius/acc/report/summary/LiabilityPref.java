package com.olbius.acc.report.summary;

import com.olbius.acc.report.balancetrial.BalanceWorker;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.acc.utils.PeriodUtils;
import com.olbius.acc.utils.accounts.AccountUtils;
import javolution.util.FastMap;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.model.ModelEntity;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiabilityPref {
	
	public static final String MODULE = LiabilityPref.class.getName();

    public static BigDecimal getOpeningBalLiability(String partyId, String organizationPartyId , String dateStr, Delegator delegator, String glAccountId) {
        try {
            SimpleDateFormat format = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
            format.applyPattern("dd/MM/yyyy");
            Date date = new Date(format.parse(dateStr).getTime());

            Map<String, BigDecimal> balMap = BalanceWorker.getOpeningByPartyFromTo(glAccountId, partyId, date, null, organizationPartyId, delegator);
            BigDecimal credit = balMap.get(BalanceWorker.CREDIT);
            BigDecimal debit = balMap.get(BalanceWorker.DEBIT);

            if(credit.compareTo(BigDecimal.ZERO) != 0) {
                return credit;
            } else {
                return debit;
            }
        } catch (ParseException | GenericEntityException | NoSuchAlgorithmException e) {
            ErrorUtils.processException(e, MODULE);
            return BigDecimal.ZERO;
        }
    }

    public static BigDecimal getPaidLiability(String partyId, String organizationPartyId , String dateStr, Delegator delegator){
        BigDecimal result = BigDecimal.ZERO;
        try {
            SimpleDateFormat format = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
            format.applyPattern("dd/MM/yyyy");
            Date date = new Date(format.parse(dateStr).getTime());
            GenericValue customTimePeriod = PeriodUtils.getGenCustomTimePeriod(date, delegator);
            List<EntityCondition> listConds = new ArrayList<>();
            listConds.add(EntityCondition.makeCondition("partyIdFrom", organizationPartyId));
            listConds.add(EntityCondition.makeCondition("partyId", partyId));
            listConds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.LESS_THAN, new Timestamp(date.getTime())));
            if(customTimePeriod != null)
                listConds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime())));
            listConds.add(EntityCondition.makeCondition("statusId", "INVOICE_PAID"));
            List<GenericValue> listPaymentAppl = delegator.findList("PaymentApplicationInvoice", EntityCondition.makeCondition(listConds, EntityJoinOperator.AND), null, null, null, false);
            for(GenericValue item: listPaymentAppl){
                result = result.add(item.getBigDecimal("amountApplied"));
            }
        } catch (ParseException | GenericEntityException e) {
            ErrorUtils.processException(e, MODULE);
            return BigDecimal.ZERO;
        }
        return result;
    }

	
	public static BigDecimal getNotPaidLiability(String partyId, String organizationPartyId , String dateStr, Delegator delegator){
		BigDecimal result = BigDecimal.ZERO;
		BigDecimal liabilityTotal = getLiabilityTotal(partyId, organizationPartyId, dateStr, delegator);
		BigDecimal paidLiability = getPaidLiability(partyId, organizationPartyId, dateStr, delegator);
		result = liabilityTotal.subtract(paidLiability);
		return result;
	}
	
	public static BigDecimal getLiabilityTotal(String partyId, String organizationPartyId , String dateStr, Delegator delegator){
		BigDecimal result = BigDecimal.ZERO;
		try {
			//Get parameters
			List<EntityCondition> listConds = new ArrayList<EntityCondition>();
			SimpleDateFormat format = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
			format.applyPattern("dd/MM/yyyy");
			Date date = new Date(format.parse(dateStr).getTime());
			GenericValue customTimePeriod = PeriodUtils.getGenCustomTimePeriod(date, delegator);
			listConds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.LESS_THAN, new Timestamp(date.getTime())));
			if(customTimePeriod != null)
    			listConds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime())));
			listConds.add(EntityCondition.makeCondition("partyIdFrom", organizationPartyId));
			/*listConds.add(EntityCondition.makeCondition("statusId", "INVOICE_READY"));*/
			listConds.add(EntityCondition.makeCondition("partyId", partyId));
			List<GenericValue> listInvoices = delegator.findList("InvoiceItemView", EntityCondition.makeCondition(listConds, EntityJoinOperator.AND), null, null, null, false);
			for(GenericValue item: listInvoices){
				result = result.add(item.getBigDecimal("quantity").multiply(item.getBigDecimal("amount")));
			}
		} catch (GenericEntityException | ParseException e) {
			ErrorUtils.processException(e, MODULE);
			return BigDecimal.ZERO;
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getAccruedLiabilityPref(DispatchContext dctx, Map<String, Object> context) {
		List<Map<String, Object>> listInvoiceMaps = new ArrayList<>();
		try {
			Delegator delegator = dctx.getDelegator();
			Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
			String dateStr = (String)parameters.get("dateStr")[0];
			SimpleDateFormat format = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
			format.applyPattern("dd-MM-yyyy");
			Date date = new Date(format.parse(dateStr).getTime());
			GenericValue customTimePeriod = PeriodUtils.getGenCustomTimePeriod(date, delegator);
			String organizationPartyId = (String)parameters.get("organizationPartyId")[0];
			String partyId = (String)parameters.get("partyId")[0];
			
			List<EntityCondition> listConds = new ArrayList<EntityCondition>();
			listConds.add(EntityCondition.makeCondition("partyIdFrom", organizationPartyId));
			/*listConds.add(EntityCondition.makeCondition("statusId", "INVOICE_READY"));*/
			listConds.add(EntityCondition.makeCondition("partyId", partyId));
			if(customTimePeriod != null)
    			listConds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime())));
			listConds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.LESS_THAN, new Timestamp(date.getTime())));
			List<GenericValue> listInvoices = delegator.findList("InvoiceItemView", EntityCondition.makeCondition(listConds, EntityJoinOperator.AND), null, null, null, false);
			int seqNum = 1;
			for(GenericValue item: listInvoices){
				Map<String, Object> invoice = new HashMap<>();
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", item.get("productId")), false);
				invoice.put("productId", item.getString("productId"));
				invoice.put("seqId", seqNum++);
				invoice.put("productName", product.getString("productName"));
				invoice.put("uomId", product.getString("quantityUomId"));
				invoice.put("quantity", item.getBigDecimal("quantity"));
				invoice.put("unitCost", item.getBigDecimal("amount"));
				invoice.put("amount", item.getBigDecimal("quantity").multiply(item.getBigDecimal("amount")));
				listInvoiceMaps.add(invoice);
			}
		} catch (ParseException | GenericEntityException e) {
			ErrorUtils.processException(e, MODULE);
			return ServiceUtil.returnError(e.getMessage());
		}
		
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("listIterator", listInvoiceMaps);
		return result;
	}
	
	public static List<Map<String, Object>> getAccruedLiabilityPref(String partyId, String organizationPartyId , String dateStr, Delegator delegator) {
		List<Map<String, Object>> listInvoiceMaps = new ArrayList<>();
		try {
			//Get parameters
			SimpleDateFormat format = (SimpleDateFormat)SimpleDateFormat.getDateInstance();
			format.applyPattern("dd/MM/yyyy");
			Date date = new Date(format.parse(dateStr).getTime());
			GenericValue customTimePeriod = PeriodUtils.getGenCustomTimePeriod(date, delegator);
			List<EntityCondition> listConds = new ArrayList<EntityCondition>();
			listConds.add(EntityCondition.makeCondition("partyIdFrom", organizationPartyId));
			/*listConds.add(EntityCondition.makeCondition("statusId", "INVOICE_READY"));*/
			listConds.add(EntityCondition.makeCondition("partyId", partyId));
            if(customTimePeriod != null)
    			listConds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.GREATER_THAN, new Timestamp(customTimePeriod.getDate("fromDate").getTime())));
			listConds.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.LESS_THAN, new Timestamp(date.getTime())));
			List<GenericValue> listInvoices = delegator.findList("InvoiceItemView", EntityCondition.makeCondition(listConds, EntityJoinOperator.AND), null, null, null, false);
			int seqNum = 1;
			for(GenericValue item: listInvoices){
				Map<String, Object> invoice = new HashMap<>();
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", item.get("productId")), false);
				invoice.put("productId", item.getString("productId"));
				invoice.put("seqId", seqNum++);
				invoice.put("productName", product.getString("productName"));
				GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", product.getString("quantityUomId")), false);
				invoice.put("uomId", uom.getString("description"));
				invoice.put("quantity", item.getBigDecimal("quantity"));
				invoice.put("unitCost", item.getBigDecimal("amount"));
				invoice.put("amount", item.getBigDecimal("quantity").multiply(item.getBigDecimal("amount")));
				listInvoiceMaps.add(invoice);
			}
		} catch (ParseException | GenericEntityException e) {
			ErrorUtils.processException(e, MODULE);
		}
		return listInvoiceMaps;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListProductLiabilityPrefJQ(DispatchContext dctx, Map<String, Object> context){
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		Delegator delegator = dctx.getDelegator();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		EntityListIterator listIterator = null;
		Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String partyIdFrom = parameters.get("partyIdFrom") != null? parameters.get("partyIdFrom")[0] : null;
		String partyIdTo = parameters.get("partyIdTo") != null? parameters.get("partyIdTo")[0] : null;
		String fromDateStr = parameters.get("fromDate") != null? parameters.get("fromDate")[0] : null;
		String thruDateStr = parameters.get("thruDate") != null? parameters.get("thruDate")[0] : null;
		Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
		Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
		listAllConditions.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.GREATER_THAN_EQUAL_TO, fromDate));
		listAllConditions.add(EntityCondition.makeCondition("invoiceDate", EntityJoinOperator.LESS_THAN_EQUAL_TO, thruDate));
		listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
		listAllConditions.add(EntityCondition.makeCondition("partyId", partyIdTo));
		try {
			ModelEntity modelEntity = delegator.getModelEntity("InvoiceAndItemAndProductGroupBy");
			List<String> selectedField = modelEntity.getAllFieldNames();
			selectedField.remove("invoiceDate");
			selectedField.remove("partyIdFrom");
			selectedField.remove("partyId");
			listIterator = delegator.find("InvoiceAndItemAndProductGroupBy", EntityCondition.makeCondition(listAllConditions), null, UtilMisc.toSet(selectedField), listSortFields, opts);
			successResult.put("listIterator", listIterator);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
		return successResult;
	}
}