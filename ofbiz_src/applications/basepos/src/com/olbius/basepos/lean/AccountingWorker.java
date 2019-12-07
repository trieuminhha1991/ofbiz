package com.olbius.basepos.lean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class AccountingWorker {
	
	private static final int DECIMALS = UtilNumber.getBigDecimalScale("ledger.decimals");
	private static final int ROUNDING = UtilNumber.getBigDecimalRoundingMode("ledger.rounding");
	private static final int ACCTG_TRANS_ENTRY_SEQUENCE_ID_DIGITS = 5; // this is the number of digits used for acctgTransEntrySeqId: 00001, 00002...

	private String organizationPartyId;
	private GenericValue aggregatedPartyAcctgPref = null;
	private List<GenericValue> customTimePeriodList = null;
	private DataWorker dataWorker;
	
	public AccountingWorker(DataWorker dataWorker, String organizationPartyId) throws Exception{
		this.dataWorker = dataWorker;
		if (DECIMALS == -1 || ROUNDING == -1) {
            throw new Exception("Missing ledger.decimals OR ledger.rounding");
        }
		this.organizationPartyId = organizationPartyId;
		validateCustomTimePeriods();
		getPartyAccountingPreferences();
	}
	
	public void createAcctgTransForSalesInvoice(String invoiceId) throws Exception{
		GenericValue invoice = dataWorker.delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
		this.organizationPartyId = invoice.getString("partyIdFrom");
		List<EntityExpr> exprs = FastList.newInstance();
        exprs.add(EntityCondition.makeCondition("invoiceId", EntityOperator.EQUALS, invoiceId));
        exprs.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "INV_SALES_TAX"));
        exprs.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityOperator.NOT_EQUAL, "ITM_SALES_TAX"));
		List<GenericValue> invoiceItems = dataWorker.delegator.findList("InvoiceItem", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, UtilMisc.toList("amount"), null, false);
		String removeInvoiceItemSeqId = "";
		long reciprocalItemSeqId = 0;
		int reciprocalItemSeqDigit = 5;
		List<GenericValue> acctgTransEntries = new ArrayList<GenericValue>();
		for (GenericValue invoiceItem : invoiceItems) {
			if(invoiceItem.getBigDecimal("amount").signum() < 0 && invoiceItem.getString("parentInvoiceItemSeqId") != null){
				BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
				BigDecimal origAmount = quantity.multiply(invoiceItem.getBigDecimal("amount")).setScale(DECIMALS, ROUNDING);
				reciprocalItemSeqId++;
				String acctgReciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalItemSeqId, reciprocalItemSeqDigit);
				GenericValue creditEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
				creditEntry.set("debitCreditFlag", "C");
				creditEntry.set("glAccountTypeId", invoiceItem.get("invoiceItemTypeId"));
				creditEntry.set("productId", invoiceItem.get("productId"));
				creditEntry.set("glAccountId", invoiceItem.get("overrideGlAccountId"));
				creditEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
				creditEntry.set("organizationPartyId", this.organizationPartyId);
				creditEntry.set("origAmount", origAmount);
				creditEntry.set("invoiceItemSeqId", invoiceItem.get("invoiceItemSeqId"));
				creditEntry.set("reciprocalSeqId", acctgReciprocalItemSeqId);
				if(invoiceItem.get("taxAuthPartyId") != null && invoiceItem.getString("taxAuthPartyId") != ""){
					creditEntry.set("partyId", invoiceItem.get("taxAuthPartyId"));
					creditEntry.set("roleTypeId", "TAX_AUTHORITY");
				}
				acctgTransEntries.add(creditEntry);
				
				GenericValue invoiceItemEntry = findParentInvoiceItem(invoiceItems, invoiceItem.getString("parentInvoiceItemSeqId"));
				quantity = invoiceItemEntry.getBigDecimal("quantity"); 
				BigDecimal origAmountEntry = quantity.multiply(invoiceItemEntry.getBigDecimal("amount")).setScale(DECIMALS, ROUNDING);
				BigDecimal absOrigAmount = null;
				if(origAmount.signum() < 0){
					absOrigAmount = origAmountEntry.negate();
				}
				if(absOrigAmount.compareTo(origAmountEntry) == 0){
					removeInvoiceItemSeqId = removeInvoiceItemSeqId + ";" + invoiceItem.getString("parentInvoiceItemSeqId");
					creditEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
					creditEntry.set("debitCreditFlag", "C");
					creditEntry.set("glAccountTypeId", invoiceItemEntry.get("invoiceItemTypeId"));
					creditEntry.set("organizationPartyId", this.organizationPartyId);
					creditEntry.set("productId", invoiceItemEntry.get("productId"));
					creditEntry.set("origAmount", origAmountEntry);
					creditEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
					creditEntry.set("glAccountId", invoiceItemEntry.get("overrideGlAccountId"));
					creditEntry.set("invoiceItemSeqId", invoiceItemEntry.get("invoiceItemSeqId"));
					creditEntry.set("reciprocalSeqId", acctgReciprocalItemSeqId);
					if(invoiceItem.get("taxAuthPartyId") != null && invoiceItem.getString("taxAuthPartyId") != ""){
						creditEntry.set("partyId", invoiceItemEntry.get("taxAuthPartyId"));
						creditEntry.set("roleTypeId", "TAX_AUTHORITY");
					}
					acctgTransEntries.add(creditEntry);
				}else{
					GenericValue debitEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
					debitEntry.set("debitCreditFlag", "D");
					debitEntry.set("glAccountTypeId", "ACCOUNTS_RECEIVABLE");
					debitEntry.set("organizationPartyId", this.organizationPartyId);
					debitEntry.set("roleTypeId", "BILL_TO_CUSTOMER");
					debitEntry.set("origAmount", origAmount);
					debitEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
					debitEntry.set("partyId", invoice.get("partyId"));
					debitEntry.set("productId", invoiceItem.get("productId"));
					debitEntry.set("invoiceItemSeqId", invoiceItem.get("invoiceItemSeqId"));
					debitEntry.set("reciprocalSeqId", acctgReciprocalItemSeqId);
					acctgTransEntries.add(debitEntry);
				}
			}else{
				if(!removeInvoiceItemSeqId.contains(invoiceItem.getString("invoiceItemSeqId"))){
					reciprocalItemSeqId++;
					String acctgReciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalItemSeqId, reciprocalItemSeqDigit);
					BigDecimal quantity = invoiceItem.getBigDecimal("quantity");
					BigDecimal origAmount = quantity.multiply(invoiceItem.getBigDecimal("amount")).setScale(DECIMALS, ROUNDING);
					GenericValue creditEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
					creditEntry.set("debitCreditFlag", "C");
					creditEntry.set("glAccountTypeId", invoiceItem.get("invoiceItemTypeId"));
					creditEntry.set("organizationPartyId", this.organizationPartyId);
					creditEntry.set("productId", invoiceItem.get("productId"));
					creditEntry.set("origAmount", origAmount);
					creditEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
					creditEntry.set("glAccountId", invoiceItem.get("overrideGlAccountId"));
					creditEntry.set("invoiceItemSeqId", invoiceItem.get("invoiceItemSeqId"));
					creditEntry.set("reciprocalSeqId", acctgReciprocalItemSeqId);
					if(invoiceItem.get("taxAuthPartyId") != null && invoiceItem.getString("taxAuthPartyId") != ""){
						creditEntry.set("partyId", invoiceItem.get("taxAuthPartyId"));
						creditEntry.set("roleTypeId", "TAX_AUTHORITY");
					}
					acctgTransEntries.add(creditEntry);
					GenericValue debitEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
					debitEntry.set("debitCreditFlag", "D");
					debitEntry.set("glAccountTypeId", "ACCOUNTS_RECEIVABLE");
					debitEntry.set("organizationPartyId", this.organizationPartyId);
					debitEntry.set("roleTypeId", "BILL_TO_CUSTOMER");
					debitEntry.set("partyId", invoice.get("partyId"));
					debitEntry.set("origAmount", origAmount);
					debitEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
					debitEntry.set("productId", invoiceItem.get("productId"));
					debitEntry.set("invoiceItemSeqId", invoiceItem.get("invoiceItemSeqId"));					
					debitEntry.set("reciprocalSeqId", acctgReciprocalItemSeqId);
					acctgTransEntries.add(debitEntry);
				}
			}
		}
		Map<String, Set<String>> taxAuthPartyAndGeos = org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceTaxAuthPartyAndGeos(invoice);
		for (String taxAuthPartyId : taxAuthPartyAndGeos.keySet()) {
			for (String taxAuthGeoId : taxAuthPartyAndGeos.get(taxAuthPartyId)) {
				List<GenericValue> invoiceItemTaxAuthPartyAndGeos = com.olbius.accounting.invoice.InvoiceWorker.getInvoiceItemTaxAuthPartyAndGeos(invoice, taxAuthPartyId, taxAuthGeoId);
				if(!invoiceItemTaxAuthPartyAndGeos.isEmpty()){
					for (GenericValue invoiceItemTax : invoiceItemTaxAuthPartyAndGeos) {
						GenericValue creditEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
						reciprocalItemSeqId++;
						String acctgReciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalItemSeqId, reciprocalItemSeqDigit);
						// Credit
						creditEntry.set("debitCreditFlag", "C");
						creditEntry.set("organizationPartyId", this.organizationPartyId);
						BigDecimal quantity = invoiceItemTax.getBigDecimal("quantity");
						BigDecimal taxAmount = quantity.multiply(invoiceItemTax.getBigDecimal("amount")).setScale(DECIMALS, ROUNDING);
						creditEntry.set("origAmount", taxAmount);
						creditEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
						creditEntry.set("partyId", taxAuthPartyId);
						creditEntry.set("roleTypeId", "TAX_AUTHORITY");
						GenericValue taxAuthorityRateType = dataWorker.delegator.findOne("TaxAuthorityRateProduct", UtilMisc.toMap("taxAuthorityRateSeqId", invoiceItemTax.getString("taxAuthorityRateSeqId")), true);
						GenericValue product = dataWorker.delegator.findOne("Product", UtilMisc.toMap("productId", invoiceItemTax.getString("productId")), true);
						String strProductTypeId = null;
						if(product != null){
							strProductTypeId = product.getString("productTypeId");
						}
						// FIXME fixed-code, take attention about VAT_OUT_ACCOUNT
						if(strProductTypeId == null){
							strProductTypeId = "FINISHED_GOOD";
						}
						GenericValue taxAuthorityRateTypeGlAccount = dataWorker.delegator.findOne("TaxAuthorityRateTypeGlAccount", 
								UtilMisc.toMap("taxAuthorityRateTypeId", taxAuthorityRateType.getString("taxAuthorityRateTypeId"),
										"organizationPartyId", creditEntry.getString("organizationPartyId"),
										"invoiceTypeId", invoice.getString("invoiceTypeId"),
										"productTypeId", strProductTypeId), true);
						if(taxAuthorityRateTypeGlAccount != null){
							creditEntry.set("glAccountId", taxAuthorityRateTypeGlAccount.getString("glAccountId"));
						}else{
							creditEntry.set("glAccountTypeId", "VAT_OUT_ACCOUNT");
						}
						creditEntry.set("productId", invoiceItemTax.get("productId"));
						creditEntry.set("invoiceItemSeqId", invoiceItemTax.get("invoiceItemSeqId"));
						creditEntry.set("reciprocalSeqId", acctgReciprocalItemSeqId);
						acctgTransEntries.add(creditEntry);
						
						// Debit
						GenericValue debitEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
						debitEntry.set("debitCreditFlag", "D");
						debitEntry.set("glAccountTypeId", "ACCOUNTS_RECEIVABLE");
						debitEntry.set("organizationPartyId", this.organizationPartyId);
						debitEntry.set("origAmount", taxAmount);
						debitEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
						debitEntry.set("partyId", invoice.get("partyId"));
						debitEntry.set("roleTypeId", "BILL_TO_CUSTOMER");
						debitEntry.set("productId", invoiceItemTax.get("productId"));
						debitEntry.set("invoiceItemSeqId", invoiceItemTax.get("invoiceItemSeqId"));						
						debitEntry.set("reciprocalSeqId", acctgReciprocalItemSeqId);
						acctgTransEntries.add(debitEntry);
					}
				}
			}
		}
		BigDecimal taxAmount = org.ofbiz.accounting.invoice.InvoiceWorker.getInvoiceUnattributedTaxTotal(invoice);
		if(taxAmount.signum() != 0){
			reciprocalItemSeqId++;
			String acctgReciprocalItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalItemSeqId, reciprocalItemSeqDigit);
			// Credit
			GenericValue creditEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
			creditEntry.set("debitCreditFlag", "C");
			creditEntry.set("organizationPartyId", this.organizationPartyId);
			creditEntry.set("origAmount", taxAmount);
			creditEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
			creditEntry.set("glAccountTypeId", "TAX_ACCOUNT");
			creditEntry.set("reciprocalSeqId", acctgReciprocalItemSeqId);
			acctgTransEntries.add(creditEntry);
			// Debit
			GenericValue debitEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
			debitEntry.set("debitCreditFlag", "D");
			debitEntry.set("glAccountTypeId", "ACCOUNTS_RECEIVABLE");
			debitEntry.set("organizationPartyId", this.organizationPartyId);
			debitEntry.set("origAmount", taxAmount);
			debitEntry.set("origCurrencyUomId", invoice.get("currencyUomId"));
			debitEntry.set("partyId", invoice.get("partyId"));
			debitEntry.set("roleTypeId", "BILL_TO_CUSTOMER");
			debitEntry.set("reciprocalSeqId", acctgReciprocalItemSeqId);
			acctgTransEntries.add(debitEntry);
		}
		createAcctgTransAndEntries(acctgTransEntries, "SALES_INVOICE", invoice.getString("partyId"), "BILL_TO_CUSTOMER", invoiceId, null, null, null);
	}
	
	public void createAcctgTransForSalesShipmentIssuance(GenericValue itemIssuance, GenericValue inventoryItem) throws Exception{
//		GenericValue itemIssuance = dataWorker.delegator.findOne("ItemIssuance", UtilMisc.toMap("itemIssuanceId", itemIssuanceId), false);
//		GenericValue inventoryItem = dataWorker.delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", itemIssuance.getString("inventoryItemId")), true);
		List<GenericValue> acctgTransEntries = new ArrayList<GenericValue>();
		List<EntityExpr> exprs = FastList.newInstance();
        exprs.add(EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, itemIssuance.getString("orderId")));
        exprs.add(EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER"));
		List<GenericValue> billToCustomers = this.dataWorker.delegator.findList("OrderRole", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, true);
		GenericValue billToCustomer = null;
		if(billToCustomers != null && !billToCustomers.isEmpty()){
			billToCustomer = billToCustomers.get(0);
		}
		BigDecimal totalAmount = new BigDecimal(0);
		BigDecimal totalPurAmount = new BigDecimal(0);
		long reciprocalItemSeqId = 0;
		int reciprocalItemSeqDigit = 5;
		if(aggregatedPartyAcctgPref.getString("cogsMethodId").equals("COGS_INV_COST") || aggregatedPartyAcctgPref.getString("cogsMethodId").equals("COGS_AVG_COST")){
			BigDecimal unitCost = null;
			BigDecimal purCost = null;
			if(aggregatedPartyAcctgPref.getString("cogsMethodId").equals("COGS_AVG_COST")){
				Map<String, Object> getProdAvgCostMap = FastMap.newInstance();
				getProdAvgCostMap.put("inventoryItem", inventoryItem);
				getProdAvgCostMap.put("userLogin", dataWorker.posSession.getUserLogin());
				Map<String, Object> result = dataWorker.dispatcher.runSync("getProductAverageCost", getProdAvgCostMap);
				unitCost = (BigDecimal) result.get("unitCost");
				purCost = (BigDecimal) result.get("purCost");
			}else{
				unitCost = inventoryItem.getBigDecimal("unitCost");
				purCost = inventoryItem.getBigDecimal("purCost");
			}
			if(itemIssuance.getBigDecimal("weight") != null && itemIssuance.getBigDecimal("weight").signum() > 0){
				totalAmount = unitCost.multiply(itemIssuance.getBigDecimal("weight")).setScale(DECIMALS, ROUNDING);
				totalPurAmount = purCost.multiply(itemIssuance.getBigDecimal("weight")).setScale(DECIMALS, ROUNDING);
			}else{
				totalAmount = unitCost.multiply(itemIssuance.getBigDecimal("quantity")).setScale(DECIMALS, ROUNDING);
				totalPurAmount = purCost.multiply(itemIssuance.getBigDecimal("quantity")).setScale(DECIMALS, ROUNDING);
			}
			reciprocalItemSeqId++;
			String invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalItemSeqId, reciprocalItemSeqDigit);
			// Credit
			GenericValue creditEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
			creditEntry.set("debitCreditFlag", "C");
			creditEntry.set("glAccountTypeId", "INVENTORY_ACCOUNT");
			creditEntry.set("organizationPartyId", inventoryItem.get("ownerPartyId"));
			creditEntry.set("productId", inventoryItem.get("productId"));
			creditEntry.set("inventoryItemId", inventoryItem.get("inventoryItemId"));
			creditEntry.set("origAmount", totalAmount);
			creditEntry.set("origCurrencyUomId", inventoryItem.get("currencyUomId"));
			if(billToCustomer != null){
				creditEntry.set("partyId", billToCustomer.get("partyId"));
				creditEntry.set("roleTypeId", billToCustomer.get("roleTypeId"));
			}
			creditEntry.set("reciprocalSeqId", invoiceItemSeqId);
			acctgTransEntries.add(creditEntry);
			// Debit
			GenericValue debitEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
			debitEntry.set("debitCreditFlag", "D");
			debitEntry.set("glAccountTypeId", "COGS_ACCOUNT");
			debitEntry.set("organizationPartyId", inventoryItem.get("ownerPartyId"));
			debitEntry.set("productId", inventoryItem.get("productId"));
			debitEntry.set("origCurrencyUomId", inventoryItem.get("currencyUomId"));
			debitEntry.set("origAmount", totalAmount);
			if(billToCustomer != null){
				debitEntry.set("partyId", billToCustomer.get("partyId"));
				debitEntry.set("roleTypeId", billToCustomer.get("roleTypeId"));
			}
			debitEntry.set("reciprocalSeqId", invoiceItemSeqId);
			acctgTransEntries.add(debitEntry);
			// Purchase cost
			if (totalPurAmount.compareTo(BigDecimal.ZERO) != 0){
				reciprocalItemSeqId++;
				invoiceItemSeqId = UtilFormatOut.formatPaddedNumber(reciprocalItemSeqId, reciprocalItemSeqDigit);
				// Credit
				creditEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
				creditEntry.set("debitCreditFlag", "C");
				creditEntry.set("glAccountTypeId", "INV_PUR_ACCOUNT");
				creditEntry.set("organizationPartyId", inventoryItem.get("ownerPartyId"));
				creditEntry.set("productId", inventoryItem.get("productId"));
				creditEntry.set("inventoryItemId", inventoryItem.get("inventoryItemId"));
				creditEntry.set("origAmount", totalPurAmount);
				creditEntry.set("origCurrencyUomId", inventoryItem.get("currencyUomId"));
				if(billToCustomer != null){
					creditEntry.set("partyId", billToCustomer.get("partyId"));
					creditEntry.set("roleTypeId", billToCustomer.get("roleTypeId"));
				}
				creditEntry.set("reciprocalSeqId", invoiceItemSeqId);
				acctgTransEntries.add(creditEntry);
				// Debit
				debitEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
				debitEntry.set("debitCreditFlag", "D");
				debitEntry.set("glAccountTypeId", "COGS_ACCOUNT");
				debitEntry.set("organizationPartyId", inventoryItem.get("ownerPartyId"));
				debitEntry.set("productId", inventoryItem.get("productId"));
				debitEntry.set("origCurrencyUomId", inventoryItem.get("currencyUomId"));
				debitEntry.set("origAmount", totalPurAmount);
				if(billToCustomer != null){
					debitEntry.set("partyId", billToCustomer.get("partyId"));
					debitEntry.set("roleTypeId", billToCustomer.get("roleTypeId"));
				}
				debitEntry.set("reciprocalSeqId", invoiceItemSeqId);
				acctgTransEntries.add(debitEntry);
			}
			
		}else{
			String orderByString = null;
			if(aggregatedPartyAcctgPref.getString("cogsMethodId").equals("COGS_FIFO")){
				orderByString = "+datetimeReceived";
			}
			if(aggregatedPartyAcctgPref.getString("cogsMethodId").equals("COGS_LIFO")){
				orderByString = "-datetimeReceived";
			}
			if(orderByString == null){
				throw new Exception("Accounting COGS Costing Method is not supported");
			}
			// FIXME implement for COGS_FIFO and COGS_LIFO, view createAcctgTransForSalesShipmentIssuance service
		}
		createAcctgTransAndEntries(acctgTransEntries, "SALES_SHIPMENT", null, null, null, null, inventoryItem.getString("inventoryItemId"), itemIssuance.getString("shipmentId"));
	}
	
	public void createAcctgTransAndEntriesForIncomingPayment(String paymentId) throws Exception{
		GenericValue payment = dataWorker.delegator.findOne("Payment", UtilMisc.toMap("paymentId", paymentId), false);
		boolean isReceiptValue = org.ofbiz.accounting.util.UtilAccounting.isReceipt(payment);
		if(isReceiptValue){
			long reciprocalItemSeqId = 0;
			int reciprocalItemSeqDigit = 5;
			List<GenericValue> acctgTransEntries = new ArrayList<GenericValue>();
			BigDecimal amount = payment.getBigDecimal("amount");
			BigDecimal origAmount = payment.getBigDecimal("actualCurrencyAmount");
			String origCurrencyUomId = payment.getString("actualCurrencyUomId");
			String currencyUomId = payment.getString("currencyUomId");
			String organizationPartyId = payment.getString("partyIdTo");
			String partyId = payment.getString("partyIdFrom");
			reciprocalItemSeqId++;
			String reciprocalSeqId = UtilFormatOut.formatPaddedNumber(reciprocalItemSeqId, reciprocalItemSeqDigit);
			// Credit
			GenericValue debitEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
			debitEntry.set("debitCreditFlag", "D");
			debitEntry.set("amount", amount);
			debitEntry.set("currencyUomId", currencyUomId);
			debitEntry.set("origAmount", origAmount);
			debitEntry.set("origCurrencyUomId", origCurrencyUomId);
			debitEntry.set("organizationPartyId", organizationPartyId);
			debitEntry.set("reciprocalSeqId", reciprocalSeqId);
			acctgTransEntries.add(debitEntry);
			// Debit
			GenericValue paymentGlAccountTypeMap = dataWorker.delegator.findOne("PaymentGlAccountTypeMap", UtilMisc.toMap("paymentTypeId", payment.get("paymentTypeId"), "organizationPartyId", organizationPartyId), true);
			String creditGlAccountTypeId = paymentGlAccountTypeMap.getString("glAccountTypeId");
			GenericValue creditEntry = dataWorker.delegator.makeValue("AcctgTransEntry");
			creditEntry.set("debitCreditFlag", "C");
			creditEntry.set("amount", amount);
			creditEntry.set("currencyUomId", currencyUomId);
			creditEntry.set("origAmount", origAmount);
			creditEntry.set("origCurrencyUomId", origCurrencyUomId);
			creditEntry.set("glAccountId", payment.get("overrideGlAccountId"));
			creditEntry.set("glAccountTypeId", creditGlAccountTypeId);
			creditEntry.set("organizationPartyId", organizationPartyId);
			creditEntry.set("reciprocalSeqId", reciprocalSeqId);
			acctgTransEntries.add(creditEntry);
			createAcctgTransAndEntries(acctgTransEntries, "INCOMING_PAYMENT", partyId, "BILL_TO_CUSTOMER", null, paymentId, null, null);
			List<GenericValue> paymentApplications = payment.getRelated("PaymentApplication", null, null, false);
			for (GenericValue payapp : paymentApplications) {
				Map<String, Object> createAcctgTransAndEntriesForPaymentApplicationInMap = FastMap.newInstance();
				createAcctgTransAndEntriesForPaymentApplicationInMap.put("paymentApplicationId", payapp.get("paymentApplicationId"));
				createAcctgTransAndEntriesForPaymentApplicationInMap.put("userLogin", dataWorker.posSession.getUserLogin());
				// TODO Improve for returning case
				if(payment.getString("paymentTypeId").equals("VENDOR_REFUND")){
					dataWorker.dispatcher.runSync("createAcctgTransAndEntriesForVendorRefundPaymentApplication", createAcctgTransAndEntriesForPaymentApplicationInMap);
				} else {
					dataWorker.dispatcher.runSync("createAcctgTransAndEntriesForPaymentApplication", createAcctgTransAndEntriesForPaymentApplicationInMap);
				}
			}
		}
	}
	
	private void postAcctgTrans(GenericValue acctgTrans, List<GenericValue> acctgTransEntries) throws Exception{
		Map<String, Object> trialBalanceCallMap = FastMap.newInstance();
		trialBalanceCallMap.put("acctgTransId", acctgTrans.get("acctgTransId"));
		trialBalanceCallMap.put("userLogin", dataWorker.posSession.getUserLogin());
		Map<String, Object> result = dataWorker.dispatcher.runSync("calculateAcctgTransTrialBalance", trialBalanceCallMap);
		BigDecimal debitCreditDifference = (BigDecimal) result.get("debitCreditDifference");
		BigDecimal debitTotal = (BigDecimal) result.get("debitTotal");
		BigDecimal creditTotal = (BigDecimal) result.get("creditTotal");
		if(debitCreditDifference.compareTo(new BigDecimal(0.10)) > 0){
			throw new Exception("Accounting not posting GlAccount Transaction Trial Balance Failed");
		}
		if(debitCreditDifference.compareTo(new BigDecimal(-0.10)) < 0){
			throw new Exception("Accounting not posting GlAccount Transaction Trial Balance Failed");
		}
		if(debitTotal.signum() == 0 && creditTotal.signum() != 0){
			throw new Exception("Accounting not Posting GlAccount Transaction Debit Zero");
		}
		if(debitTotal.signum() != 0 && creditTotal.signum() == 0){
			throw new Exception("Accounting not Posting GlAccount Transaction Debit Zero");
		}
		for (GenericValue acctgTransEntry : acctgTransEntries) {
			if(acctgTransEntry.getString("glAccountId") == null){
				throw new Exception("AccountingGlAccountNotSetForAccountType");
			}
			if(acctgTransEntry.getString("amount") == null){
				throw new Exception("AccountingGlAccountAmountNotSet");
			}
		}
//		acctgTrans.set("glJournalId", this.aggregatedPartyAcctgPref.get("errorGlJournalId"));
		acctgTrans.set("lastModifiedByUserLogin", dataWorker.posSession.getUserLoginId());
		acctgTrans.set("isPosted", "Y");
		acctgTrans.set("postedDate", UtilDateTime.nowTimestamp());
		acctgTrans.store();
	}
	
	// TODO Execute this when logging
	@SuppressWarnings("unchecked")
	private void validateCustomTimePeriods() throws Exception{
		if(this.customTimePeriodList != null){
			return;
		}
		List<String> onlyIncludePeriodTypeIdList = Arrays.asList("FISCAL_YEAR", "FISCAL_QUARTER", "FISCAL_MONTH", "FISCAL_WEEK", "FISCAL_BIWEEK");
		Map<String, Object> findCustomTimePeriodCallMap = FastMap.newInstance();
		findCustomTimePeriodCallMap.put("onlyIncludePeriodTypeIdList", onlyIncludePeriodTypeIdList);
		findCustomTimePeriodCallMap.put("userLogin", dataWorker.posSession.getUserLogin());
		findCustomTimePeriodCallMap.put("organizationPartyId", this.organizationPartyId);
		findCustomTimePeriodCallMap.put("excludeNoOrganizationPeriods", "Y");
		findCustomTimePeriodCallMap.put("findDate", UtilDateTime.nowTimestamp());
		Map<String, Object> result = dataWorker.dispatcher.runSync("findCustomTimePeriods", findCustomTimePeriodCallMap);
		this.customTimePeriodList = (List<GenericValue>) result.get("customTimePeriodList");
		if(this.customTimePeriodList == null){
			throw new Exception("Accounting no customTimePeriod found for Transaction Date");
		}
		for (GenericValue customTimePeriod : this.customTimePeriodList) {
			if(customTimePeriod.getString("isClosed").equals("Y")){
				throw new Exception("Accounting CustomTimePeriod is Closed");
			}
		}
	}
	
	public String createAcctgTransAndEntries(List<GenericValue> acctgTransEntries, String acctgTransTypeId, String partyId, String roleTypeId, String invoiceId, String paymentId, String inventoryItemId, String shipmentId) throws Exception{
		List<GenericValue> normalizedAcctgTransEntries =  new ArrayList<GenericValue>();
		for (GenericValue acctgTransEntry : acctgTransEntries) {
			if(acctgTransEntry.get("amount") == null){
				if(acctgTransEntry.get("origAmount") != null){
					if(acctgTransEntry.get("origCurrencyUomId") == null){
						acctgTransEntry.set("origCurrencyUomId", aggregatedPartyAcctgPref.get("baseCurrencyUomId"));
					}
					acctgTransEntry.set("currencyUomId", aggregatedPartyAcctgPref.get("baseCurrencyUomId"));
					if(!acctgTransEntry.getString("origCurrencyUomId").equals(acctgTransEntry.getString("currencyUomId"))){
						Map<String, Object> convertUomInMap = FastMap.newInstance();
						convertUomInMap.put("originalValue", acctgTransEntry.get("origAmount"));
						convertUomInMap.put("uomId", acctgTransEntry.get("origCurrencyUomId"));
						convertUomInMap.put("uomIdTo", acctgTransEntry.get("currencyUomId"));
						Map<String, Object> result = dataWorker.dispatcher.runSync("convertUom", convertUomInMap);
						acctgTransEntry.set("amount", result.get("convertedValue"));
					}else{
						acctgTransEntry.set("amount", acctgTransEntry.get("origAmount"));
					}
				}
			}
			
			if(acctgTransEntry.get("glAccountId") == null){
				Map<String, Object> getGlAccountFromAccountTypeInMap = FastMap.newInstance();
				getGlAccountFromAccountTypeInMap.put("organizationPartyId", acctgTransEntry.get("organizationPartyId"));
				getGlAccountFromAccountTypeInMap.put("acctgTransTypeId", acctgTransTypeId);
				getGlAccountFromAccountTypeInMap.put("glAccountTypeId", acctgTransEntry.get("glAccountTypeId"));
				getGlAccountFromAccountTypeInMap.put("debitCreditFlag", acctgTransEntry.get("debitCreditFlag"));
				getGlAccountFromAccountTypeInMap.put("productId", acctgTransEntry.get("productId"));
				getGlAccountFromAccountTypeInMap.put("partyId", partyId);
				getGlAccountFromAccountTypeInMap.put("roleTypeId", roleTypeId);
				getGlAccountFromAccountTypeInMap.put("invoiceId", invoiceId);
				getGlAccountFromAccountTypeInMap.put("userLogin", dataWorker.posSession.getUserLogin());
				getGlAccountFromAccountTypeInMap.put("paymentId", paymentId);
				// TODO Improve the following method
				Map<String, Object> result = dataWorker.dispatcher.runSync("getGlAccountFromAccountType", getGlAccountFromAccountTypeInMap);
				acctgTransEntry.set("glAccountId", result.get("glAccountId"));
			}
			if(acctgTransEntry.get("origAmount") == null){
				acctgTransEntry.set("origAmount", acctgTransEntry.get("amount"));
			}
			GenericValue glAccountType = dataWorker.delegator.findOne("GlAccountType", UtilMisc.toMap("glAccountTypeId", acctgTransEntry.get("glAccountTypeId")), true);
			if(glAccountType == null){
				acctgTransEntry.set("glAccountTypeId", null);
			}
			normalizedAcctgTransEntries.add(acctgTransEntry);
		}
		if(!normalizedAcctgTransEntries.isEmpty()){
			// Create AcctgTrans
			GenericValue acctgTran = dataWorker.delegator.makeValue("AcctgTrans");
			acctgTran.set("acctgTransId", dataWorker.delegator.getNextSeqId("AcctgTrans"));
			acctgTran.set("transactionDate", UtilDateTime.nowTimestamp());
			acctgTran.set("isPosted", "N");
			acctgTran.set("glFiscalTypeId", "ACTUAL");
			acctgTran.set("invoiceId", invoiceId);
			acctgTran.set("paymentId", paymentId);
			acctgTran.set("partyId", partyId);
			acctgTran.set("shipmentId", shipmentId);
			acctgTran.set("inventoryItemId", inventoryItemId);
			acctgTran.set("acctgTransTypeId", acctgTransTypeId);
			acctgTran.set("roleTypeId", "BILL_TO_CUSTOMER");
			acctgTran.set("lastModifiedByUserLogin", dataWorker.posSession.getUserLoginId());
			acctgTran.set("createdByUserLogin", dataWorker.posSession.getUserLoginId());
			acctgTran.create();
			int invoiceItemSeqNum = 1;
			for (GenericValue acctgEntry : normalizedAcctgTransEntries) {
				if(acctgEntry.getBigDecimal("origAmount").signum() < 0){
					acctgEntry.set("origAmount", acctgEntry.getBigDecimal("origAmount").negate());
					acctgEntry.set("amount", acctgEntry.getBigDecimal("amount").negate());
					if(acctgEntry.getString("debitCreditFlag").equals("D")){
						acctgEntry.set("debitCreditFlag", "C");
					}else{
						acctgEntry.set("debitCreditFlag", "D");
					}
				}
				acctgEntry.set("acctgTransId", acctgTran.get("acctgTransId"));
				String acctgTransEntrySeqId = UtilFormatOut.formatPaddedNumber(invoiceItemSeqNum++, ACCTG_TRANS_ENTRY_SEQUENCE_ID_DIGITS);
				acctgEntry.set("acctgTransEntrySeqId", acctgTransEntrySeqId);
				acctgEntry.set("reconcileStatusId", "AES_NOT_RECONCILED");
				acctgEntry.set("currencyUomId", aggregatedPartyAcctgPref.get("baseCurrencyUomId"));
				acctgEntry.create();
			}
			postAcctgTrans(acctgTran, acctgTransEntries);
			return acctgTran.getString("acctgTransId");
		}
		return null;
	}
	// TODO store in session
	private void getPartyAccountingPreferences() throws Exception{
		if(aggregatedPartyAcctgPref != null){
			return;
		}
		boolean containsEmptyFields = true;
		String currentOrganizationPartyId = new String(this.organizationPartyId);
		aggregatedPartyAcctgPref = dataWorker.delegator.makeValue("PartyAcctgPreference");
		while(containsEmptyFields && currentOrganizationPartyId != null && !currentOrganizationPartyId.isEmpty()){
			GenericValue currentPartyAcctgPref = dataWorker.delegator.findOne("PartyAcctgPreference", UtilMisc.toMap("partyId", currentOrganizationPartyId), true);
			containsEmptyFields = false;
			if(currentPartyAcctgPref != null){
				for (String papKey : currentPartyAcctgPref.keySet()) {
					if(aggregatedPartyAcctgPref.get(papKey) == null){
						if(currentPartyAcctgPref.get(papKey) != null){
							aggregatedPartyAcctgPref.set(papKey, currentPartyAcctgPref.get(papKey));
						}else{
							containsEmptyFields = true;
						}
					}
				}
			}else{
				containsEmptyFields = true;
			}
			List<EntityExpr> exprs = FastList.newInstance();
	        exprs.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, currentOrganizationPartyId));
	        exprs.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP"));
	        exprs.add(EntityCondition.makeCondition("roleTypeIdFrom", EntityOperator.EQUALS, "_NA_"));
	        exprs.add(EntityCondition.makeCondition("roleTypeIdTo", EntityOperator.EQUALS, "_NA_"));
			List<GenericValue> parentPartyRelationships = EntityUtil.filterByDate(this.dataWorker.delegator.findList("PartyRelationship", EntityCondition.makeCondition(exprs, EntityOperator.AND), null, null, null, true));
			if(parentPartyRelationships == null || parentPartyRelationships.isEmpty()){
				currentOrganizationPartyId = null;
			}else{
				currentOrganizationPartyId = parentPartyRelationships.get(0).getString("partyIdFrom");
			}
		}
		aggregatedPartyAcctgPref.set("partyId", this.organizationPartyId);
		if(aggregatedPartyAcctgPref.getString("errorGlJournalId") == null){
			throw new Exception("errorGlJournalId is empty");
		}
		
	}
	
	private GenericValue findParentInvoiceItem(List<GenericValue> invoiceItems, String parentId){
		for (GenericValue invoiceItem : invoiceItems) {
			if(invoiceItem.getString("invoiceItemSeqId") != null && invoiceItem.getString("invoiceItemSeqId").equals(parentId)){
				return invoiceItem;
			}
		}
		return null;
	}
}
