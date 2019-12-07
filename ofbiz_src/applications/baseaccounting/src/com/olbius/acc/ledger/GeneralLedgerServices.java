package com.olbius.acc.ledger;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by user on 1/18/18.
 */
public class GeneralLedgerServices {
    public static final String MODULE = GeneralLedgerServices.class.getName();

    @SuppressWarnings("unchecked")
    public Map<String, Object> getPartyAccountingPreferences(DispatchContext dispatcher, Map<String, Object> context) {
        //Get delegator
        Delegator delegator = dispatcher.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        GenericEntity aggregatedPartyAcctgPref = delegator.makeValue("PartyAcctgPreference");
        String currentOrganizationPartyId = (String) context.get("organizationPartyId");
        Boolean containsEmptyFields = true;
        try {
            while (UtilValidate.isNotEmpty(currentOrganizationPartyId) && containsEmptyFields) {
                GenericValue parentPartyRelationship = null;
                String entityKey = null;
                Object entityValue = null;
                GenericEntity currentPartyAcctgPref = delegator.findOne("PartyAcctgPreference", UtilMisc.toMap("partyId", currentOrganizationPartyId), false);
                containsEmptyFields = true;
                if (currentPartyAcctgPref != null && !currentPartyAcctgPref.isEmpty()) {
                    Iterator<String> iterator = currentPartyAcctgPref.getAllKeys().iterator();
                    while (iterator.hasNext()) {
                        entityKey = iterator.next();
                        entityValue = currentPartyAcctgPref.get(entityKey);
                        if (aggregatedPartyAcctgPref.get(entityKey) == null) {
                            if (entityValue != null)
                                aggregatedPartyAcctgPref.set(entityKey, entityValue);
                            else {
                                containsEmptyFields = true;
                            }
                        }
                    }
                } else {
                    containsEmptyFields = false;
                }
                EntityCondition entityCondition = EntityCondition.makeCondition(EntityCondition.makeCondition("partyIdTo", currentOrganizationPartyId), EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"), EntityCondition.makeCondition("roleTypeIdFrom", "_NA_"), EntityCondition.makeCondition("roleTypeIdTo", "_NA_"));
                List<GenericValue> parentPartyRelationships = delegator.findList("PartyRelationship", entityCondition, null, null, null, false);
                if (!parentPartyRelationships.isEmpty()) {
                    parentPartyRelationship = parentPartyRelationships.get(0);
                    currentOrganizationPartyId = (String) parentPartyRelationship.get("partyIdFrom");
                } else currentOrganizationPartyId = null;
            }
            if (!aggregatedPartyAcctgPref.isEmpty()) {
                aggregatedPartyAcctgPref.set("partyId", context.get("organizationPartyId"));
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        successResult.put("partyAccountingPreference", aggregatedPartyAcctgPref);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> computeAndStoreGlAccountHistoryBalanceFromHistory(DispatchContext dispatcher, Map<String, Object> context) {
        //Get delegator
        Delegator delegator = dispatcher.getDelegator();
        LocalDispatcher localDispatcher = dispatcher.getDispatcher();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String glAccountId = (String) context.get("glAccountId");
        String organizationPartyId = (String) context.get("organizationPartyId");
        String customTimePeriodId = (String) context.get("customTimePeriodId");
        Boolean isFirst = (Boolean) context.get("isFirst");
        GenericValue lastClosedTimePeriod = (GenericValue) context.get("lastClosedTimePeriod");
        try {
            GenericValue glAccountHistory = delegator.findOne("GlAccountHistory", UtilMisc.toMap(
                    "glAccountId", glAccountId,
                    "organizationPartyId", organizationPartyId,
                    "customTimePeriodId", customTimePeriodId), false);

            Map<String, Object> inMap = FastMap.newInstance();
            inMap.put("customTimePeriodId", glAccountHistory.get("customTimePeriodId"));
            inMap.put("isFirst", isFirst);
            inMap.put("lastClosedTimePeriod", lastClosedTimePeriod);
            inMap.put("organizationPartyId", glAccountHistory.get("organizationPartyId"));
            inMap.put("glAccountId", glAccountHistory.get("glAccountId"));
            inMap.put("userLogin", context.get("userLogin"));
            Map<String, Object> resultService = localDispatcher.runSync("computeGlAccountBalanceForTimePeriodFromHistory", inMap);
            BigDecimal openingBalance = (BigDecimal) resultService.get("openingBalance");
            BigDecimal openingDrBalance =(BigDecimal) resultService.get("openingDrBalance");
            BigDecimal openingCrBalance = (BigDecimal) resultService.get("openingCrBalance");
            BigDecimal endingBalance = (BigDecimal)  resultService.get("endingBalance");
            BigDecimal endingDrBalance = (BigDecimal) resultService.get("endingDrBalance");
            BigDecimal endingCrBalance = (BigDecimal) resultService.get("endingCrBalance");
            BigDecimal postedDebits = (BigDecimal) resultService.get("postedDebits");
            BigDecimal postedCredits = (BigDecimal) resultService.get("postedCredits");

            glAccountHistory.set("openingBalance", openingBalance);
            glAccountHistory.set("openingDrBalance", openingDrBalance);
            glAccountHistory.set("openingCrBalance", openingCrBalance);
            glAccountHistory.set("endingBalance", endingBalance);
            glAccountHistory.set("endingDrBalance", endingDrBalance);
            glAccountHistory.set("endingCrBalance", endingCrBalance);
            glAccountHistory.set("postedDebits", postedDebits);
            glAccountHistory.set("postedCredits", postedCredits);
            delegator.store(glAccountHistory);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        } catch (GenericServiceException e) {
            e.printStackTrace();
        }
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> computeGlAccountBalanceForTimePeriodFromHistory(DispatchContext dispatcher, Map<String, Object> context) {
        //Get delegator
        Delegator delegator = dispatcher.getDelegator();
        LocalDispatcher localDispatcher = dispatcher.getDispatcher();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        String customTimePeriodId = (String) context.get("customTimePeriodId");
        String glAccountId = (String) context.get("glAccountId");
        Boolean isFirst = (Boolean) context.get("isFirst");
        String organizationPartyId = (String) context.get("organizationPartyId");

        GenericValue lastClosedTimePeriod = (GenericValue) context.get("lastClosedTimePeriod");

        try {
            GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
            GenericValue glAccount = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", glAccountId), false);

            BigDecimal openingBalance = BigDecimal.ZERO;
            BigDecimal openingDrBalance =BigDecimal.ZERO;
            BigDecimal openingCrBalance = BigDecimal.ZERO;
            BigDecimal endingBalance = BigDecimal.ZERO;
            BigDecimal endingDrBalance = BigDecimal.ZERO;
            BigDecimal endingCrBalance = BigDecimal.ZERO;
            BigDecimal defaultValue = BigDecimal.ZERO;

            Boolean isDebit = UtilAccounting.isDebitAccount(glAccount);
            if(isFirst != null) {
                if(isFirst) {
                    GenericValue glAccountBalance = delegator.findOne("GlAccountBalance", UtilMisc.toMap(
                            "glAccountId", glAccount.get("glAccountId"),
                            "organizationPartyId", customTimePeriod.get("organizationPartyId")
                    ), false);
                    if(glAccountBalance != null) {
                        openingCrBalance =  (glAccountBalance.get("openingCrBalance") == null) ? BigDecimal.ZERO : glAccountBalance.getBigDecimal("openingCrBalance");
                        openingDrBalance =  (glAccountBalance.get("openingDrBalance") == null) ? BigDecimal.ZERO : glAccountBalance.getBigDecimal("openingDrBalance");
                        
                        Debug.log("GeneralLedgerServices + ::computeGlAccountBalanceForTimePeriodFromHistory, gl_account "
                        		+ (String)glAccount.get("glAccountId") + ", openingCrBalance = " + openingCrBalance
                        		+ ", openingDrBalance = " + openingDrBalance);
                        
                    }
                    if(isDebit) {
                        openingBalance = openingDrBalance.subtract(openingCrBalance);
                    }
                    else {
                        openingBalance = openingCrBalance.subtract(openingDrBalance);
                    }
                }
            }

            if(UtilValidate.isNotEmpty(lastClosedTimePeriod)) {
                GenericValue glAccountHistory = delegator.findOne("GlAccountHistory",
                        UtilMisc.toMap("glAccountId", glAccount.get("glAccountId"),
                                "organizationPartyId", customTimePeriod.get("organizationPartyId"),
                                "customTimePeriodId", lastClosedTimePeriod.get("customTimePeriodId")
                        ), false);
                openingCrBalance = glAccountHistory.getBigDecimal("endingCrBalance") == null ? BigDecimal.ZERO : glAccountHistory.getBigDecimal("endingCrBalance");
                openingDrBalance = glAccountHistory.getBigDecimal("endingDrBalance") == null ? BigDecimal.ZERO : glAccountHistory.getBigDecimal("endingDrBalance");
                openingBalance = glAccountHistory.getBigDecimal("endingBalance") == null ? BigDecimal.ZERO : glAccountHistory.getBigDecimal("endingBalance");
            }
            
            List<GenericValue> totalDebitsToOpeningDates = delegator.findList("AcctgTransEntrySums",
                    EntityCondition.makeCondition(
                            EntityCondition.makeCondition("organizationPartyId", organizationPartyId),
                            EntityCondition.makeCondition("glAccountId", glAccountId),
                            EntityCondition.makeCondition("isPosted", "Y"),
                            EntityCondition.makeCondition("debitCreditFlag", "D"),
                            EntityCondition.makeCondition("glFiscalTypeId", "ACTUAL"),
                            EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toTimestamp((Date) customTimePeriod.get("thruDate"))),
                            EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")))
                    ), UtilMisc.toSet("amount"), null, null, false);
            GenericValue totalDebitsInTimePeriod = totalDebitsToOpeningDates.get(0);
            List<GenericValue> totalCreditsToOpeningDates = delegator.findList("AcctgTransEntrySums",
                    EntityCondition.makeCondition(
                            EntityCondition.makeCondition("organizationPartyId", organizationPartyId),
                            EntityCondition.makeCondition("glAccountId", glAccountId),
                            EntityCondition.makeCondition("isPosted", "Y"),
                            EntityCondition.makeCondition("debitCreditFlag", "C"),
                            EntityCondition.makeCondition("glFiscalTypeId", "ACTUAL"),
                            EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toTimestamp((Date) customTimePeriod.get("thruDate"))),
                            EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toTimestamp(customTimePeriod.getDate("fromDate")))
                    ), UtilMisc.toSet("amount"), null, null, false);
            GenericValue totalCreditsInTimePeriod = totalCreditsToOpeningDates.get(0);
            BigDecimal totalDebitInTime = (totalDebitsInTimePeriod.get("amount")  == null)? BigDecimal.ZERO : totalDebitsInTimePeriod.getBigDecimal("amount");
            BigDecimal totalCreditInTime = (totalCreditsInTimePeriod.get("amount")  == null)? BigDecimal.ZERO : totalCreditsInTimePeriod.getBigDecimal("amount");
            if(isDebit) {
                endingDrBalance = openingDrBalance.add(totalDebitInTime.subtract(totalCreditInTime));
                endingBalance = endingDrBalance.subtract(endingCrBalance);
            }
            else {
                endingCrBalance = openingCrBalance.add(totalCreditInTime.subtract(totalDebitInTime));
                endingBalance = endingCrBalance.subtract(endingDrBalance);
            }

            successResult.put("openingBalance", openingBalance);
            successResult.put("openingDrBalance", openingDrBalance);
            successResult.put("openingCrBalance", openingCrBalance);
            successResult.put("endingBalance", endingBalance);
            successResult.put("endingDrBalance", endingDrBalance);
            successResult.put("endingCrBalance", endingCrBalance);
            if(totalDebitsInTimePeriod.get("amount") != null) {
                successResult.put("postedDebits", totalDebitsInTimePeriod.get("amount"));
            }
            else {
                successResult.put("postedDebits", defaultValue);
            }
            if (totalCreditsInTimePeriod.get("amount") != null) {
                successResult.put("postedCredits", totalCreditsInTimePeriod.get("amount"));
            }
            else {
                successResult.put("postedCredits", defaultValue);
            }

            Debug.log("GeneralLedgerServices + ::computeGlAccountBalanceForTimePeriodFromHistory"
            		+ "\n openingBalance = " + successResult.get("openingBalance")
            		+ "\n openingDrBalance = " + successResult.get("openingDrBalance")
            		+ "\n openingCrBalance = " + successResult.get("openingCrBalance")
            		+ "\n endingBalance = " + successResult.get("endingBalance")
            		+ "\n endingDrBalance = " + successResult.get("endingDrBalance")
            		+ "\n endingCrBalance = " + successResult.get("endingCrBalance")
            		+ "\n postedCredits = " + successResult.get("postedCredits")
            		+ "\n postedDebits = " + successResult.get("postedDebits")
            		);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError("error");
        }
        return successResult;
    }

    /*
    if createAcctgTransAndEntries succeeded -> run eca createAcctgTransAndEntries in file secas_ledger.xml
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> createAcctgTransAndEntries(DispatchContext dispatcher, Map<String, Object> context) {
        //Get delegator
        Delegator delegator = dispatcher.getDelegator();
        LocalDispatcher localDispatcher = dispatcher.getDispatcher();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<GenericValue> acctgTransEntries = (List<GenericValue>) context.get("acctgTransEntries");
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Locale locale = (Locale) context.get("locale");
        String organizationPartyId = null;
        List<GenericValue> normalizedAcctgTransEntries = FastList.newInstance();
        try {
            for (GenericValue acctgTransEntry : acctgTransEntries) {
                organizationPartyId = acctgTransEntry.getString("organizationPartyId");
                GenericValue partyRole = delegator.findOne("PartyRole", UtilMisc.toMap("partyId", organizationPartyId, "roleTypeId", "INTERNAL_ORGANIZATIO"), true);
                if(partyRole.isEmpty()) {
                    return ServiceUtil.returnError("error");
                }
                Map<String, Object> partyAccountingPreferencesCallMap = FastMap.newInstance();
                partyAccountingPreferencesCallMap.put("organizationPartyId", organizationPartyId);
                partyAccountingPreferencesCallMap.put("userLogin", userLogin);
                partyAccountingPreferencesCallMap.put("locale", locale);
                Map<String, Object> resultService = localDispatcher.runSync("getPartyAccountingPreferences", partyAccountingPreferencesCallMap);
                GenericValue partyAcctgPreference = (GenericValue) resultService.get("partyAccountingPreference");
                if(partyAcctgPreference.isEmpty()) {
                    return ServiceUtil.returnError("error");
                }
                if(acctgTransEntry.getBigDecimal("amount") == null) {
                    if(acctgTransEntry.get("origAmount") != null) {
                        if(acctgTransEntry.get("origCurrencyUomId") == null)
                            acctgTransEntry.set("origCurrencyUomId", partyAcctgPreference.get("baseCurrencyUomId"));
                        if(!acctgTransEntry.get("origCurrencyUomId").equals(acctgTransEntry.get("currencyUomId"))) {
                            Map<String, Object> convertUomInMap = FastMap.newInstance();
                            convertUomInMap.put("originalValue", acctgTransEntry.get("origAmount"));
                            convertUomInMap.put("uomId", acctgTransEntry.get("origCurrencyUomId"));
                            convertUomInMap.put("uomIdTo", acctgTransEntry.get("currencyUomId") == null ? acctgTransEntry.get("origCurrencyUomId"): acctgTransEntry.get("currencyUomId"));
                            resultService = localDispatcher.runSync("convertUom", convertUomInMap);
                            acctgTransEntry.set("amount", resultService.get("convertedValue"));
                        }
                        else {
                            acctgTransEntry.set("amount", acctgTransEntry.get("origAmount"));
                        }
                    }
                }
                if(UtilValidate.isEmpty(acctgTransEntry.getString("glAccountId"))) {
                    Map<String, Object> getGlAccountFromAccountTypeInMap = FastMap.newInstance();
                    getGlAccountFromAccountTypeInMap.put("organizationPartyId", acctgTransEntry.get("organizationPartyId"));
                    getGlAccountFromAccountTypeInMap.put("acctgTransTypeId", context.get("acctgTransTypeId"));
                    getGlAccountFromAccountTypeInMap.put("glAccountTypeId", acctgTransEntry.get("glAccountTypeId"));
                    getGlAccountFromAccountTypeInMap.put("debitCreditFlag", acctgTransEntry.get("debitCreditFlag"));
                    getGlAccountFromAccountTypeInMap.put("productId", acctgTransEntry.get("productId"));
                    getGlAccountFromAccountTypeInMap.put("partyId", context.get("partyId"));
                    getGlAccountFromAccountTypeInMap.put("roleTypeId", context.get("roleTypeId"));
                    getGlAccountFromAccountTypeInMap.put("invoiceId", context.get("invoiceId"));
                    getGlAccountFromAccountTypeInMap.put("paymentId", context.get("paymentId"));
                    getGlAccountFromAccountTypeInMap.put("userLogin", userLogin);
                    resultService = localDispatcher.runSync("getGlAccountFromAccountType", getGlAccountFromAccountTypeInMap);
                    acctgTransEntry.set("glAccountId", resultService.get("glAccountId"));
                }
                if(acctgTransEntry.get("origAmount") == null) {
                    acctgTransEntry.set("origAmount", acctgTransEntry.get("amount"));
                }
                GenericValue glAccountType = delegator.findOne("GlAccountType", UtilMisc.toMap("glAccountTypeId", acctgTransEntry.get("glAccountTypeId")), true);
                if(glAccountType == null) {
                    acctgTransEntry.set("glAccountTypeId", null);
                }
                normalizedAcctgTransEntries.add(acctgTransEntry);
            }

            if(!normalizedAcctgTransEntries.isEmpty()) {
                Map<String, Object> createAcctgTransParams = FastMap.newInstance();
                createAcctgTransParams.putAll(context);
                if(createAcctgTransParams.get("transactionDate") == null)
                    createAcctgTransParams.put("transactionDate", UtilDateTime.nowTimestamp());
                String acctgTransId = (String) localDispatcher.runSync("createAcctgTrans", createAcctgTransParams).get("acctgTransId");

                for(GenericValue acctgTransEntry: normalizedAcctgTransEntries) {
                    if(acctgTransEntry.getBigDecimal("origAmount").compareTo(BigDecimal.ZERO) < 0) {
                        acctgTransEntry.set("origAmount", acctgTransEntry.getBigDecimal("origAmount").negate());
                        acctgTransEntry.set("amount", acctgTransEntry.getBigDecimal("amount").negate());
                        if(acctgTransEntry.getString("debitCreditFlag").equals("D")) {
                            acctgTransEntry.set("debitCreditFlag", "C");
                        }
                        else if (acctgTransEntry.getString("debitCreditFlag").equals("C")){
                            acctgTransEntry.set("debitCreditFlag", "D");
                        }
                    }
                    Map<String, Object> createAcctgTransEntryParams = FastMap.newInstance();
                    createAcctgTransEntryParams.putAll(acctgTransEntry);
                    createAcctgTransEntryParams.put("acctgTransId", acctgTransId);
                    createAcctgTransEntryParams.put("organizationPartyId", organizationPartyId);
                    createAcctgTransEntryParams.put("userLogin", userLogin);
                    createAcctgTransEntryParams.put("locale", locale);
                    localDispatcher.runSync("createAcctgTransEntry", createAcctgTransEntryParams);
                }

                successResult.put("acctgTransId", acctgTransId);
                Map<String, Object> postTransCtx = FastMap.newInstance();
                postTransCtx.put("acctgTransId", acctgTransId);
                postTransCtx.put("userLogin", context.get("userLogin"));
                postTransCtx.put("organizationPartyId", organizationPartyId);
                localDispatcher.runSync("postAcctgTrans", postTransCtx);
            }
            else {
                return ServiceUtil.returnError("error");
            }
        }
        catch (GenericEntityException e) {
            e.printStackTrace();
            return ServiceUtil.returnError("error");
        } catch (GenericServiceException e) {
            e.printStackTrace();
            return ServiceUtil.returnError("error");
        }
        return successResult;
    }
}
