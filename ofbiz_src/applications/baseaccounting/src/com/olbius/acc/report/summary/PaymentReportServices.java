package com.olbius.acc.report.summary;

import com.olbius.acc.report.balancetrial.BalanceWorker;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.acc.utils.accounts.Account;
import com.olbius.acc.utils.accounts.AccountBuilder;
import com.olbius.acc.utils.accounts.AccountEntity;
import com.olbius.basehr.util.MultiOrganizationUtil;
import javolution.util.FastList;
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
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PaymentReportServices {

    public static final String MODULE = PaymentReportServices.class.getName();
    public BigDecimal creditBalAmount = BigDecimal.ZERO;
    public BigDecimal debitBalAmount = BigDecimal.ZERO;

    @SuppressWarnings("unchecked")
    public Map<String, Object> getPaymentDetailBook(DispatchContext dctx, Map<String, Object> context) {
        Delegator delegator = dctx.getDelegator();
        List<Map<String, Object>> listPayDetailBooks = new ArrayList<Map<String, Object>>();
        List<Object> listAccountCode = FastList.newInstance();
        //Get parameters
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        String glAccountId = (String) parameters.get("glAccountId")[0];
        String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
        String customTimePeriodId = (String) parameters.get("customTimePeriodId")[0];
        String partyId = null;
        if (parameters.get("partyId") != null) partyId = (String) parameters.get("partyId")[0];

        List<EntityCondition> listConds = (List<EntityCondition>) context.get("listAllConditions");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");

        List<String> listSortFields = (List<String>) context.get("listSortFields");
        listSortFields.add("transactionDate ASC");
        EntityListIterator listAccTransFacts = null;
        try {
            Map<String, Object> firstPDB = new HashMap<String, Object>();
            String openingBalMess = UtilProperties.getMessage("BaseAccountingUiLabels", "BACCOpeningBalance", (Locale) context.get("locale"));
            firstPDB.put("voucherDescription", openingBalMess);
            Map<String, BigDecimal> openingBal = BalanceWorker.getOpeningBalance(glAccountId, customTimePeriodId, organizationPartyId, delegator);

            firstPDB.put("creditBalAmount", openingBal.get(BalanceWorker.DEBIT));
            firstPDB.put("debitBalAmount", openingBal.get(BalanceWorker.CREDIT));
            if (BigDecimal.ZERO.compareTo(openingBal.get(BalanceWorker.DEBIT)) != 0) {
                debitBalAmount = openingBal.get(BalanceWorker.DEBIT);
            } else if (BigDecimal.ZERO.compareTo(openingBal.get(BalanceWorker.CREDIT)) != 0) {
                creditBalAmount = openingBal.get(BalanceWorker.CREDIT);
            }
            listPayDetailBooks.add(firstPDB);

            //Get Cash Detail Book
            //Get GlAccount
            Account acc = AccountBuilder.buildAccount(glAccountId, delegator);
            List<Account> listAccount = acc.getListChild();
            for (Account item : listAccount) {
                AccountEntity accEntity = item.getAcc();
                listAccountCode.add(accEntity.getGlAccountId());
            }
            listAccountCode.add(glAccountId);
            listConds.add(EntityCondition.makeCondition("organizationPartyId", organizationPartyId));
            if (partyId != null) listConds.add(EntityCondition.makeCondition("partyId", partyId));
            listConds.add(EntityCondition.makeCondition("glAccountCode", EntityOperator.IN, listAccountCode));
            GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
            listConds.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN, customTimePeriod.getDate("thruDate")));
            listConds.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN, customTimePeriod.getDate("fromDate")));

            //Get Analysis Report
            listAccTransFacts = delegator.find("AcctgTransFactRecip", EntityCondition.makeCondition(listConds, EntityJoinOperator.AND), null, null, listSortFields, opts);
            GenericValue item = null;
            while ((item = listAccTransFacts.next()) != null) {
                Map<String, Object> payDetailBook = new HashMap<String, Object>();
                payDetailBook.put("transDate", item.getDate("transactionDate"));
                payDetailBook.put("glAccountCode", item.getString("glAccountCode"));
                payDetailBook.put("recipGlAccountCode", item.getString("recipGlAccountCode"));
                payDetailBook.put("currencyId", item.getString("currencyId"));
                payDetailBook.put("voucherDate", item.getTimestamp("voucherDate"));
                payDetailBook.put("voucherID", item.getString("voucherId"));
                String parentType = item.getString("voucherParentType") == null ? "" : item.getString("voucherParentType");
                switch (parentType) {
                    case VoucherType.PAYMENT_TYPE:
                        GenericValue voucherType = delegator.findOne("PaymentType", UtilMisc.toMap("paymentTypeId", item.getString("voucherType")), false);
                        if (UtilValidate.isEmpty(item.getString("voucherDescription"))) {
                            payDetailBook.put("voucherDescription", voucherType.getString("description"));
                        } else {
                            payDetailBook.put("voucherDescription", item.getString("voucherDescription") + "[" + voucherType.getString("description") + "]");
                        }
                        break;
                    case VoucherType.INVOICE_TYPE:
                        voucherType = delegator.findOne("InvoiceType", UtilMisc.toMap("invoiceTypeId", item.getString("voucherType")), false);
                        if (UtilValidate.isEmpty(item.getString("voucherDescription"))) {
                            payDetailBook.put("voucherDescription", voucherType.getString("description"));
                        } else {
                            payDetailBook.put("voucherDescription", item.getString("voucherDescription") + "[" + voucherType.getString("description") + "]");
                        }
                        break;
                    case VoucherType.DELIVERY_TYPE:
                        voucherType = delegator.findOne("DeliveryType", UtilMisc.toMap("deliveryTypeId", item.getString("voucherType")), false);
                        if (UtilValidate.isEmpty(item.getString("voucherDescription"))) {
                            payDetailBook.put("voucherDescription", voucherType.getString("description"));
                        } else {
                            payDetailBook.put("voucherDescription", item.getString("voucherDescription") + "[" + voucherType.getString("description") + "]");
                        }
                        break;
                    default:
                        break;
                }
                if (item.getString("debitCreditFlag").equals("C")) {
                    payDetailBook.put("creditAmount", item.getBigDecimal("amount").abs());
                    payDetailBook.put("debitAmount", BigDecimal.ZERO);
                    if (debitBalAmount.compareTo(BigDecimal.ZERO) != 0) {
                        debitBalAmount = debitBalAmount.subtract(item.getBigDecimal("amount").abs());
                        if (debitBalAmount.compareTo(BigDecimal.ZERO) > 0) {
                            creditBalAmount = BigDecimal.ZERO;
                        } else {
                            creditBalAmount = debitBalAmount.abs();
                            debitBalAmount = BigDecimal.ZERO;
                        }
                        payDetailBook.put("debitBalAmount", debitBalAmount);
                        payDetailBook.put("creditBalAmount", creditBalAmount);
                    } else {
                        creditBalAmount = creditBalAmount.add(item.getBigDecimal("amount").abs());
                        debitBalAmount = BigDecimal.ZERO;
                        payDetailBook.put("creditBalAmount", creditBalAmount);
                        payDetailBook.put("debitBalAmount", debitBalAmount);
                    }
                } else {
                    payDetailBook.put("debitAmount", item.getBigDecimal("amount"));
                    payDetailBook.put("creditAmount", BigDecimal.ZERO);
                    if (creditBalAmount.compareTo(BigDecimal.ZERO) != 0) {
                        creditBalAmount = creditBalAmount.subtract(item.getBigDecimal("amount").abs());
                        debitBalAmount = BigDecimal.ZERO;
                        if (creditBalAmount.compareTo(BigDecimal.ZERO) > 0) {
                            debitBalAmount = BigDecimal.ZERO;
                        } else {
                            debitBalAmount = creditBalAmount.abs();
                            creditBalAmount = BigDecimal.ZERO;
                        }
                        payDetailBook.put("debitBalAmount", debitBalAmount);
                        payDetailBook.put("creditBalAmount", creditBalAmount);
                    } else {
                        debitBalAmount = debitBalAmount.add(item.getBigDecimal("amount").abs());
                        creditBalAmount = BigDecimal.ZERO;
                        payDetailBook.put("creditBalAmount", creditBalAmount);
                        payDetailBook.put("debitBalAmount", debitBalAmount);
                    }
                }

                //FIXME
                //Description, voucherID, voucherDate
                listPayDetailBooks.add(payDetailBook);

				/*//Get total row
				listAccTransFacts = delegator.find("AcctgTransFactRecip", EntityCondition.makeCondition(listConds, EntityJoinOperator.AND), null, null, listSortFields, null);
				size = listAccTransFacts.getResultsTotalSize();*/
            }
        } catch (GenericEntityException | NoSuchAlgorithmException e) {
            ErrorUtils.processException(e, e.getMessage());
            return ServiceUtil.returnError(e.getMessage());
        } finally {
            try {
                if (listAccTransFacts != null) {
                    listAccTransFacts.close();
                }
            } catch (GenericEntityException e) {
                ErrorUtils.processException(e, e.getMessage());
                return ServiceUtil.returnError(e.getMessage());
            }
        }

        Map<String, Object> result = ServiceUtil.returnSuccess();
        result.put("listIterator", listPayDetailBooks);
		/*result.put("TotalRows", size);*/
        return result;
    }
}
