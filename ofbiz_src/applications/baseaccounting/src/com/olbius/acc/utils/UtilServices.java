package com.olbius.acc.utils;

import com.olbius.acc.utils.accounts.Account;
import com.olbius.acc.utils.accounts.AccountBuilder;
import com.olbius.acc.utils.accounts.AccountEntity;
import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.SecurityUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UtilServices {

    public static final String MODULE = UtilServices.class.getName();
    public static final String CASH_ACC = "111";
    public static final String MONEY_ACC = "112";
    public static final String PAY_ACC = "331";
    public static final String RECEI_ACC = "131";
    public static final String OTHER_ACC = "138";

    public static final int scale = 2;// UtilNumber.getBigDecimalScale("order.decimals");
    public static final int rounding = UtilNumber.getBigDecimalRoundingMode("order.rounding");

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListParties(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        List<GenericValue> listIterator = null;
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = userLogin.getString("userLoginId");
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        String enumId = parameters.get("enumId") != null ? parameters.get("enumId")[0] : null;
        try {
            if (enumId != null) {
                listAllConditions.add(EntityUtil.getFilterByDateExpr());
                if ("SUPPLIER_PTY_TYPE".equals(enumId)) {
                    listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "SUPPLIER_REL"));
                    listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", "SUPPLIER"));
                    listIterator = delegator.findList("PartyRelationshipAndPartyTo", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
                } else if ("CUSTOMER_PTY_TYPE".equals(enumId)) {
                    String organizationId = PartyUtil.getCurrentOrganization(delegator, userLoginId);
                    listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, UtilMisc.toList("CUSTOMER_REL", "CONTACT_REL", "DISTRIBUTOR_REL")));
                    listAllConditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, organizationId));
                    listIterator = delegator.findList("PartyRelationshipAndPartyFrom", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
                } else if ("EMPLOYEE_PTY_TYPE".equals(enumId)) {
                    listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
                    listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
                    listIterator = delegator.findList("PartyRelationshipAndPartyTo", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
                } else if ("OTHER_PTY_TYPE".equals(enumId)) {
                    listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
                    listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", "SUBSIDIARY"));
                    listIterator = delegator.findList("PartyRelationshipAndPartyTo", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
                    GenericValue anonymousPty = delegator.findOne("PartyFullNameDetailSimple", UtilMisc.toMap("partyId", "_NA_"), false);
                    if (anonymousPty != null) {
                        listIterator.add(anonymousPty);
                    }
                }
            }
        } catch (Exception e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    public static Map<String, Object> getListRoleByParty(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        String partyId = (String) context.get("partyId");
        Map<String, Object> result = null;
        try {
            List<GenericValue> listRoleTypes = delegator.findByAnd("PartyRoleDetailAndPartyDetail", UtilMisc.toMap("partyId", partyId), null, false);
            result = ServiceUtil.returnSuccess();
            result.put("listRoles", listRoleTypes);
        } catch (GenericEntityException e) {
            ErrorUtils.processException(e, MODULE);
            result = ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListCustomer(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        try {
            listAllConditions.add(EntityUtil.getFilterByDateExpr());
            listAllConditions.add(EntityCondition.makeCondition(EntityCondition.makeCondition("partyRelationshipTypeId", "CUSTOMER_REL"), EntityJoinOperator.OR, EntityCondition.makeCondition("partyRelationshipTypeId", "CONTACT_REL")));
            if (UtilValidate.isEmpty(listSortFields)) {
                listSortFields.add("firstName");
            }
            listIterator = delegator.find("PartyRelationshipAndPartyFrom", EntityCondition.makeCondition(listAllConditions), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListCustomer service: " + e.toString();
            Debug.logError(e, errMsg, MODULE);
            return ServiceUtil.returnError(e.getLocalizedMessage());
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListOrganizations(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        List<Party> listResults = FastList.newInstance();
        try {
            List<GenericValue> listPrefParties = delegator.findList("PartyAcctgPreference", null, UtilMisc.toSet("partyId"), null, null, false);
            List<String> listPrefPartyIds = FastList.newInstance();
            for (GenericValue item : listPrefParties) {
                listPrefPartyIds.add(item.getString("partyId"));
            }
            List<EntityCondition> listCond = FastList.newInstance();
            listCond.add(EntityCondition.makeCondition("partyTypeId", EntityJoinOperator.NOT_EQUAL, null));
            listCond.add(EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, UtilMisc.toSet(listPrefPartyIds)));
            listAllConditions.add(EntityCondition.makeCondition(listCond));
            listIterator = delegator.find("PartyNameView", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
            for (GenericValue partyGen : listIterator.getCompleteList()) {
                Party party = new Party(partyGen);
                listResults.add(party);
            }
            listIterator.close();
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListParties service: " + e.toString();
            Debug.logError(e, errMsg, MODULE);
        }
        successResult.put("listIterator", listResults);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListProducts(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        try {
            if (UtilValidate.isEmpty(listSortFields)) {
                listSortFields.add("productName");
            }
            listIterator = delegator.find("Product", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListProducts service: " + e.toString();
            Debug.logError(e, errMsg, MODULE);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListGlAccounts(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        try {
            listIterator = delegator.find("GlAccount", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListGlAccounts service: " + e.toString();
            Debug.logError(e, errMsg, MODULE);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListGlAccountByClassJQ(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        Map<String, String[]> parameters = (Map<String, String[]>) context.get("parameters");
        String glAccountClassId = parameters.get("glAccountClassId") != null ? parameters.get("glAccountClassId")[0] : null;
        try {
            if (glAccountClassId != null) {
                if (UtilValidate.isEmpty(listSortFields)) {
                    listSortFields.add("accountCode");
                }
                listAllConditions.add(EntityCondition.makeCondition("glAccountClassId", glAccountClassId));
                listIterator = delegator.find("GlAccount", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
            }
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListGlAccounts service: " + e.toString();
            Debug.logError(e, errMsg, MODULE);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListGlAccountsLiability(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");

        //Get GLAccountId
        List<Object> listAccountCode = FastList.newInstance();
        Account acc = AccountBuilder.buildAccount(PAY_ACC, delegator);
        List<Account> listAccount = acc.getListChild();
        for (Account item : listAccount) {
            AccountEntity accEntity = item.getAcc();
            listAccountCode.add(accEntity.getGlAccountId());
        }
        listAccountCode.add(PAY_ACC);

        Account acc1 = AccountBuilder.buildAccount("338", delegator);
        List<Account> listAccount1 = acc1.getListChild();
        for (Account item : listAccount1) {
            AccountEntity accEntity = item.getAcc();
            listAccountCode.add(accEntity.getGlAccountId());
        }
        listAccountCode.add("338");

        listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountCode));
        try {
            listIterator = delegator.find("GlAccount", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListGlAccountsLiability service: " + e.toString();
            Debug.logError(e, errMsg, MODULE);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListCashGlAccounts(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");

        // Get GLAccountId
        List<Object> listAccountCode = FastList.newInstance();
        Account acc = AccountBuilder.buildAccount(CASH_ACC, delegator);
        List<Account> listAccount = acc.getListChild();
        for (Account item : listAccount) {
            AccountEntity accEntity = item.getAcc();
            listAccountCode.add(accEntity.getGlAccountId());
        }
        listAccountCode.add(CASH_ACC);
        listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountCode));
        try {
            listIterator = delegator.find("GlAccount", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListCashGlAccounts service: " + e.toString();
            Debug.logError(e, errMsg, MODULE);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListPayReceiveGlAccounts(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");

        // Get GLAccountId
        List<Object> listAccountCode = FastList.newInstance();
        Account acc = AccountBuilder.buildAccount(PAY_ACC, delegator);
        List<Account> listAccount = acc.getListChild();
        for (Account item : listAccount) {
            AccountEntity accEntity = item.getAcc();
            listAccountCode.add(accEntity.getGlAccountId());
        }
        listAccountCode.add(PAY_ACC);

        acc = AccountBuilder.buildAccount(RECEI_ACC, delegator);
        listAccount = acc.getListChild();
        for (Account item : listAccount) {
            AccountEntity accEntity = item.getAcc();
            listAccountCode.add(accEntity.getGlAccountId());
        }
        listAccountCode.add(RECEI_ACC);

        listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountCode));
        try {
            listIterator = delegator.find("GlAccount", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListCashGlAccounts service: " + e.toString();
            Debug.logError(e, errMsg, MODULE);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListMoneyGlAccounts(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");

        // Get GLAccountId
        List<Object> listAccountCode = FastList.newInstance();
        Account acc = AccountBuilder.buildAccount(MONEY_ACC, delegator);
        List<Account> listAccount = acc.getListChild();
        for (Account item : listAccount) {
            AccountEntity accEntity = item.getAcc();
            listAccountCode.add(accEntity.getGlAccountId());
        }
        listAccountCode.add(MONEY_ACC);
        listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountCode));
        try {
            listIterator = delegator.find("GlAccount", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListMoneyGlAccounts service: " + e.toString();
            Debug.logError(e, errMsg, MODULE);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    public static String getAccountType(String glAccountId, Delegator delegator) {
        String accType = "";
        try {
            GenericValue glAcc = delegator.findOne("GlAccount", UtilMisc.toMap("glAccountId", glAccountId), false);
            String glAccountClassId = glAcc.getString("glAccountClassId");
            accType = getAccountTypeByClass(glAccountClassId, delegator);
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return accType;
    }

    private static String getAccountTypeByClass(String glAccountClassId, Delegator delegator) {
        String accType = "";
        try {
            GenericValue accountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", glAccountClassId), false);
            if (UtilValidate.isEmpty(accountClass.getString("parentClassId"))) {
                accType = glAccountClassId;
            } else {
                accType = getAccountTypeByClass(accountClass.getString("parentClassId"), delegator);
            }
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }
        return accType;
    }

    public static void getAllPaymentType(Delegator delegator, List<String> listPaymentType, String parentType) {
        try {
            if (parentType == null || parentType.equals("")) return;
            List<GenericValue> listPayment = delegator.findByAnd("PaymentType", UtilMisc.toMap("parentTypeId", parentType), null, false);
            if (UtilValidate.isNotEmpty(listPayment)) {
                for (GenericValue payment : listPayment) {
                    listPaymentType.add(payment.getString("paymentTypeId"));
                    getAllPaymentType(delegator, listPaymentType, payment.getString("paymentTypeId"));
                }
            } else return;
        } catch (Exception e) {
            ErrorUtils.processException(e, MODULE);
        }
    }

    public static void getAllInvoiceType(Delegator delegator, List<String> listInvoiceType, String parentType) {
        try {
            if (parentType == null || parentType.equals("")) return;
            List<GenericValue> listInvoice = delegator.findByAnd("InvoiceType", UtilMisc.toMap("parentTypeId", parentType), null, false);
            if (UtilValidate.isNotEmpty(listInvoice)) {
                for (GenericValue invoice : listInvoice) {
                    listInvoiceType.add(invoice.getString("invoiceTypeId"));
                    if (!invoice.getString("invoiceTypeId").equals(parentType)) {
                        getAllInvoiceType(delegator, listInvoiceType, invoice.getString("invoiceTypeId"));
                    }
                }
            } else return;
        } catch (Exception e) {
            ErrorUtils.processException(e, MODULE);
        }
    }

    public static boolean positive(String strNum) {
        boolean positive = false;
        for (int i = 0; i < strNum.length(); i++)
            if (strNum.charAt(i) != '0') return true;
        return positive;
    }

    public static String numberMoney2String(String str) {
		/*
		 * str is under format ab.cde,xyz, example 12.345,78 (muoi hai nghin ba
		 * tram bon muoi lam PHAY bay muoi tam dong)
		 */
        if (str == null || str.equals("") || str.equals("undefined")) return "";
        //Debug.log("UtilServices::numberMoney2String, start str = " + str);

        // standardize the number str for ensuring to have at most 2 digit after
        // ","
        // because the method call to UtilNumberAcc._dochangchuc(...)
        while (str.indexOf(".") >= 0) {
            str = str.replace(".", "");
        }
        if (str.indexOf(",") >= 0) {
            str = str.replace(",", ".");
        }
        //Debug.log("UtilServices::numberMoney2String, after-replacing str = "
        // + str);
        try {
            BigDecimal N = new BigDecimal(str);

            N = N.setScale(scale, rounding);

            str = N + "";
        } catch (Exception ex) {
            return "";
        }
        if (!(str instanceof java.lang.String)) str = str.toString();
        String phannguyen = "";
        String phanle = "";
        String rs = "";
        if (str.indexOf(",") != -1 || str.indexOf(".") != -1) {
            int index = (str.indexOf(",") != -1 ? str.indexOf(",") : (str.indexOf(".") != -1 ? str.indexOf(".") : null));
            phannguyen = str.substring(0, index);
            phanle = str.substring(index + 1, str.length());
            // Debug.log("UtilServices::numberMoney2String, phannguyen = " +
            // phannguyen + ", phanle = " + phanle);
            boolean pos = positive(phanle);
            if (pos) {
                rs = UtilNumberAcc.prepareFirstElement(phannguyen) + " phẩy " + UtilNumberAcc._dochangchuc(Double.valueOf(phanle), true) + " đồng";
            } else {
                rs = UtilNumberAcc.prepareFirstElement(phannguyen) + " đồng";
            }

        } else {
            rs = UtilNumberAcc.prepareFirstElement(str) + " đồng";
        }

        return rs;
    }

    public static String countNumberMoney(String str) {
        if (str == null || str.equals("") || str.equals("undefined")) return "";

        if (!(str instanceof java.lang.String)) str = str.toString();
        String phannguyen = "";
        String phanle = "";
        String rs = "";
        if (str.indexOf(",") != -1 || str.indexOf(".") != -1) {
            int index = (str.indexOf(",") != -1 ? str.indexOf(",") : (str.indexOf(".") != -1 ? str.indexOf(".") : null));
            phannguyen = str.substring(0, index);
            phanle = str.substring(index + 1, str.length());

            rs = UtilNumberAcc.prepareFirstElement(phannguyen) + " phẩy " + UtilNumberAcc._dochangchuc(Double.valueOf(phanle), true) + " đồng";
        } else {
            rs = UtilNumberAcc.prepareFirstElement(str) + " đồng";
        }

        return rs;
    }

    public static String countNumberMoney(String str, String currencyUomId) {
        if (str == null || str.equals("") || str.equals("undefined")) return "";

        if (!(str instanceof java.lang.String)) str = str.toString();
        String phannguyen = "";
        String phanle = "";
        String rs = "";
        if (str.indexOf(",") != -1 || str.indexOf(".") != -1) {
            int index = (str.indexOf(",") != -1 ? str.indexOf(",") : (str.indexOf(".") != -1 ? str.indexOf(".") : null));
            phannguyen = str.substring(0, index);
            phanle = str.substring(index + 1, str.length());

            rs = UtilNumberAcc.prepareFirstElement(phannguyen) + " phẩy " + UtilNumberAcc._dochangchuc(Double.valueOf(phanle), true) + " đồng";
        } else {
            if("USD".equals(currencyUomId))
                rs = UtilNumberAcc.prepareFirstElement(str) + " đô la mỹ";
            else if("EURO".equals(currencyUomId))
                rs = UtilNumberAcc.prepareFirstElement(str) + " euro";
            else
                rs = UtilNumberAcc.prepareFirstElement(str) + " đồng";
        }

        return rs;
    }

    public static Map<String, Object> listOrganizations(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        try {
            List<String> orgs = SecurityUtil.getPartiesByRoles("SUBSIDIARY", delegator);
            List<GenericValue> listOrganizations = delegator.findList("PersonAndPartyGroupSimple", EntityCondition.makeCondition("partyId", EntityJoinOperator.IN, orgs), null, null, null, false);
            result.put("listOrganizations", listOrganizations);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Object> listDepartments(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        try {
            String organizationPartyId = (String) context.get("organizationPartyId");
            Organization buildOrg = PartyUtil.buildOrg(delegator, organizationPartyId, true, false);
            result.put("listDepartments", buildOrg.getAllDepartmentList(delegator));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Object> listInvoiceItemTypes(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        try {
            String parentTypeId = (String) context.get("parentTypeId");
            List<EntityCondition> conditions = FastList.newInstance();
            if (UtilValidate.isNotEmpty(parentTypeId)) {
                conditions.add(EntityCondition.makeCondition("parentTypeId", EntityJoinOperator.EQUALS, parentTypeId));
            }
            List<GenericValue> listInvoiceItemTypes = delegator.findList("InvoiceItemType", EntityCondition.makeCondition(conditions), null, null, null, false);
            result.put("listInvoiceItemTypes", listInvoiceItemTypes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Object> loadInvoiceItemTypeByDepartment(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        try {
            Object invoiceItemTypeId = context.get("invoiceItemTypeId");
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            String userLoginId = userLogin.getString("userLoginId");
            String organizationPartyId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);
            List<String> departmentIds = PartyUtil.getDepartmentOfEmployee(delegator, userLogin.getString("partyId"), new Timestamp(System.currentTimeMillis()));
            List<EntityCondition> conditions = FastList.newInstance();
            if (UtilValidate.isNotEmpty(invoiceItemTypeId)) {
                conditions.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityJoinOperator.EQUALS, invoiceItemTypeId));
            }
            conditions.add(EntityCondition.makeCondition("organizationPartyId", EntityJoinOperator.EQUALS, organizationPartyId));
            conditions.add(EntityCondition.makeCondition("departmentId", EntityJoinOperator.IN, departmentIds));
            List<GenericValue> listInvoiceItemTypes = delegator.findList("CostAccMapDepartmentDetail", EntityCondition.makeCondition(conditions), null, null, null, false);
            result.put("listInvoiceItemTypes", listInvoiceItemTypes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Map<String, Object> updateCostAccDepartmentCustom(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        try {
            Long costAccDateL = (Long) context.get("costAccDate");
            if (UtilValidate.isNotEmpty(costAccDateL)) {
                context.put("costAccDate", new Timestamp(costAccDateL));
            }
            result = dispatcher.runSync("updateCostAccDepartment", context);
        } catch (Exception e) {
            e.printStackTrace();
            result = ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }

    public static Map<String, Object> createCostAccDepartmentCustom(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        LocalDispatcher dispatcher = ctx.getDispatcher();
        try {
            Long costAccDateL = (Long) context.get("costAccDate");
            if (UtilValidate.isNotEmpty(costAccDateL)) {
                context.put("costAccDate", new Timestamp(costAccDateL));
            }
            result = dispatcher.runSync("createCostAccDepartment", context);
        } catch (Exception e) {
            e.printStackTrace();
            result = ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }

    public static Map<String, Object> createCostAccDepartment(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        try {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            GenericValue costAccDepartment = delegator.makeValidValue("CostAccDepartment", context);
            Object costAccDepId = delegator.getNextSeqId("CostAccDepartment");
            costAccDepartment.set("costAccDepId", costAccDepId);
            costAccDepartment.set("createdByUserLogin", userLogin.get("userLoginId"));
            costAccDepartment.create();

            result.put("costAccDepId", costAccDepId);
        } catch (Exception e) {
            e.printStackTrace();
            result = ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }

    public static Map<String, Object> updateCostAccDepartment(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        try {
            GenericValue userLogin = (GenericValue) context.get("userLogin");
            GenericValue costAccDepartment = delegator.makeValidValue("CostAccDepartment", context);
            costAccDepartment.set("changedByUserLogin", userLogin.get("userLoginId"));
            costAccDepartment.store();

            result.put("costAccDepId", costAccDepartment.get("costAccDepId"));
        } catch (Exception e) {
            e.printStackTrace();
            result = ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }

    public static String formatAmount(double amount, Locale locale) {
        com.ibm.icu.text.NumberFormat nf = com.ibm.icu.text.NumberFormat.getInstance(locale);
        if(isInteger(amount)) {
            nf.setMaximumFractionDigits(0);
        } else {
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);
        }
        return nf.format(amount);
    }

    public static boolean isInteger(double number) {
        return number % 1 == 0;
    }

    public static Map<String, Object> createCostAccMapDepartment(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        try {
            GenericValue costAccMapDepartment = delegator.makeValidValue("CostAccMapDepartment", context);
            Object costAccMapDepId = delegator.getNextSeqId("CostAccMapDepartment");
            costAccMapDepartment.set("costAccMapDepId", costAccMapDepId);
            costAccMapDepartment.create();

            result.put("costAccMapDepId", costAccMapDepId);
        } catch (Exception e) {
            e.printStackTrace();
            result = ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }

    public static Map<String, Object> deleteCostAccMapDepartment(DispatchContext ctx, Map<String, Object> context) {
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Delegator delegator = ctx.getDelegator();
        try {
            GenericValue costAccMapDepartment = delegator.makeValidValue("CostAccMapDepartment", context);
            costAccMapDepartment.set("thruDate", new Timestamp(System.currentTimeMillis()));
            costAccMapDepartment.store();

            result.put("costAccMapDepId", costAccMapDepartment.get("costAccMapDepId"));
        } catch (Exception e) {
            e.printStackTrace();
            result = ServiceUtil.returnError(e.getMessage());
        }
        return result;
    }

    public static Map<String, BigDecimal> getOpeningGlAccountBalance(Delegator delegator, String glAccountId, GenericValue glAccountBalance, String organizationPartyId, Map<String, Map<String, BigDecimal>> cache) throws GenericEntityException {
        if (cache.get(glAccountId) != null) {
            return cache.get(glAccountId);
        }
        BigDecimal openingCrBalance = glAccountBalance != null ? glAccountBalance.getBigDecimal("openingCrBalance") : null;
        BigDecimal openingDrBalance = glAccountBalance != null ? glAccountBalance.getBigDecimal("openingDrBalance") : null;
        Map<String, BigDecimal> retMap = FastMap.newInstance();
        List<GenericValue> glAccountChildList = delegator.findByAnd("GlAccount", UtilMisc.toMap("parentGlAccountId", glAccountId), null, false);
        for (GenericValue glAccountChild : glAccountChildList) {
            String childGlAccountId = glAccountChild.getString("glAccountId");
            GenericValue tempGlAccountBalance = delegator.findOne("GlAccountBalance", UtilMisc.toMap("glAccountId", childGlAccountId, "organizationPartyId", organizationPartyId), false);
            Map<String, BigDecimal> tempOpeningGlAccountBalance = getOpeningGlAccountBalance(delegator, childGlAccountId, tempGlAccountBalance, organizationPartyId, cache);
            BigDecimal tempOpeningCrbalance = tempOpeningGlAccountBalance.get("openingCrBalance");
            BigDecimal tempOpeningDrbalance = tempOpeningGlAccountBalance.get("openingDrBalance");
            if (tempOpeningCrbalance != null) {
                openingCrBalance = openingCrBalance != null ? openingCrBalance.add(tempOpeningCrbalance) : tempOpeningCrbalance;
            }
            if (tempOpeningDrbalance != null) {
                openingDrBalance = openingDrBalance != null ? openingDrBalance.add(tempOpeningDrbalance) : tempOpeningDrbalance;
            }
        }
        retMap.put("openingCrBalance", openingCrBalance);
        retMap.put("openingDrBalance", openingDrBalance);
        cache.put(glAccountId, retMap);
        return retMap;
    }

    public static String truncateStringCase(String theString, int length) {
        theString = StringUtil.makeStringWrapper(theString).toString();
        if (theString.length() > length) {
            String truncated = "";
            truncated = theString.substring(0, length);
            return truncated;
        }
        return theString;
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListGlAccountsLiabilityReceivable(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");

        //Get GLAccountId
        List<Object> listAccountCode = FastList.newInstance();
        Account acc = AccountBuilder.buildAccount(RECEI_ACC, delegator);
        List<Account> listAccount = acc.getListChild();
        for (Account item : listAccount) {
            AccountEntity accEntity = item.getAcc();
            listAccountCode.add(accEntity.getGlAccountId());
        }
        listAccountCode.add(RECEI_ACC);

        Account acc1 = AccountBuilder.buildAccount(OTHER_ACC, delegator);
        List<Account> listAccount1 = acc1.getListChild();
        for (Account item : listAccount1) {
            AccountEntity accEntity = item.getAcc();
            listAccountCode.add(accEntity.getGlAccountId());
        }
        listAccountCode.add(OTHER_ACC);

        listAllConditions.add(EntityCondition.makeCondition("glAccountId", EntityOperator.IN, listAccountCode));
        try {
            listIterator = delegator.find("GlAccount", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListGlAccountsLiabilityReceivable service: " + e.toString();
            Debug.logError(e, errMsg, MODULE);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

    public static Map<String, Object> getPartyTypeFromPartyId(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        String userLoginId = userLogin.getString("userLoginId");
        String partyId = (String) context.get("partyId");
        List<EntityCondition> listAllConditions = FastList.newInstance();
        Delegator delegator = ctx.getDelegator();
        List<GenericValue> listIterator;

        listAllConditions.add(EntityUtil.getFilterByDateExpr());
        listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "SUPPLIER_REL"));
        listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", "SUPPLIER"));
        listAllConditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
        listIterator = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
        if (UtilValidate.isNotEmpty(listIterator)) {
            successResult.put("partyTypeId", "SUPPLIER_PTY_TYPE");
        }

        listAllConditions.clear();
        listAllConditions.add(EntityUtil.getFilterByDateExpr());
        String organizationId = PartyUtil.getCurrentOrganization(delegator, userLoginId);
        listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.IN, UtilMisc.toList("CUSTOMER_REL", "CONTACT_REL", "DISTRIBUTOR_REL")));
        listAllConditions.add(EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, organizationId));
        listAllConditions.add(EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyId));
        listIterator = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
        if (UtilValidate.isNotEmpty(listIterator)) {
            successResult.put("partyTypeId", "CUSTOMER_PTY_TYPE");
        }

        listAllConditions.clear();
        listAllConditions.add(EntityUtil.getFilterByDateExpr());
        listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "EMPLOYMENT"));
        listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
        listAllConditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
        listIterator = delegator.findList("PartyRelationship", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
        if (UtilValidate.isNotEmpty(listIterator)) {
            successResult.put("partyTypeId", "EMPLOYEE_PTY_TYPE");
        }

        listAllConditions.add(EntityCondition.makeCondition("partyRelationshipTypeId", "GROUP_ROLLUP"));
        listAllConditions.add(EntityCondition.makeCondition("roleTypeIdTo", "SUBSIDIARY"));
        listAllConditions.add(EntityCondition.makeCondition("partyIdTo", partyId));
        listIterator = delegator.findList("PartyRelationshipAndPartyTo", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
        if (UtilValidate.isNotEmpty(listIterator)) {
            successResult.put("partyTypeId", "OTHER_PTY_TYPE");
        }
        return successResult;
    }

    public final static String PRODUCT_TYPE_TAX = "PRD_TAX_INV_MANL";

    @SuppressWarnings("unchecked")
    public static Map<String, Object> getListTaxProducts(DispatchContext ctx, Map<String, Object> context) {
        Delegator delegator = ctx.getDelegator();
        Map<String, Object> successResult = ServiceUtil.returnSuccess();
        EntityListIterator listIterator = null;
        List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
        List<String> listSortFields = (List<String>) context.get("listSortFields");
        EntityFindOptions opts = (EntityFindOptions) context.get("opts");
        try {
            if (UtilValidate.isEmpty(listSortFields)) {
                listSortFields.add("productName");
            }
//            listAllConditions.add(EntityCondition.makeCondition("productTypeId", EntityJoinOperator.EQUALS, "PRD_TAX_INV_MANL"));
            listAllConditions.add(EntityCondition.makeCondition("productTypeId", "PRODUCT_TAX"));
            listIterator = delegator.find("ProductTaxAndCategoryTax", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
        } catch (Exception e) {
            String errMsg = "Fatal error calling getListProducts service: " + e.toString();
            Debug.logError(e, errMsg, MODULE);
        }
        successResult.put("listIterator", listIterator);
        return successResult;
    }

}

class UtilNumberAcc {
    public static final String[] arrNumber = {"không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín"};

    public static String prepareFirstElement(String str) {
        String first = docso(Double.valueOf(str));
        String[] arrFirst = first.trim().split(" ");
        arrFirst[0] = arrFirst[0].substring(0, 1).toUpperCase() + arrFirst[0].substring(1);
        first = strJoin(arrFirst, " ");
        return first;
    }

    public static String strJoin(String[] aArr, String sSep) {
        StringBuilder sbStr = new StringBuilder();
        for (int i = 0, il = aArr.length; i < il; i++) {
            if (i > 0) sbStr.append(sSep);
            sbStr.append(aArr[i]);
        }
        return sbStr.toString();
    }

    public static String _dochangchuc(double number, Boolean full) {
        String chuoi = "";
        Double chuc = Math.floor(number / 10);
        Double donvi = number % 10;
        if (chuc > 1) {
            chuoi += " " + arrNumber[chuc.intValue()] + " mươi";
            if (donvi == 1) {
                chuoi += " mốt";
            }
        } else if (chuc == 1) {
            chuoi = " mười";
            if (donvi == 1) {
                chuoi += " một";
            }
        } else if (full && donvi > 0) {
            chuoi = " lẻ";
        }
        if (donvi == 5 && chuc > 1) {
            chuoi += " lăm";
        } else if (donvi > 1 || (donvi == 1 && chuc == 0)) {
            chuoi += " " + arrNumber[donvi.intValue()];
        }
        return chuoi;
    }

    public static String dochangtrieu(Double so, Boolean daydu) {
        String chuoi = "";
        Double trieu = Math.floor(so / 1000000);
        so = so % 1000000;
        if (trieu > 0) {
            chuoi = docblock(trieu, daydu) + " triệu";
            daydu = true;
        }
        Double nghin = Math.floor(so / 1000);
        so = so % 1000;
        if (nghin > 0) {
            chuoi += docblock(nghin, daydu) + " nghìn";
            daydu = true;
        }
        if (so > 0) {
            chuoi += docblock(so, daydu);
        }
        return chuoi;
    }

    public static String docso(Double so) {
        if (so == 0) return arrNumber[0];
        String chuoi = "", hauto = "";
        do {
            Double ty = so % 1000000000;
            so = Math.floor(so / 1000000000);
            if (so > 0) {
                chuoi = dochangtrieu(ty, true) + hauto + chuoi;
            } else {
                chuoi = dochangtrieu(ty, false) + hauto + chuoi;
            }
            hauto = " tỷ";
        } while (so > 0);
        return chuoi;
    }

    public static String docblock(Double so, Boolean daydu) {
        String chuoi = "";
        Double tram = Math.floor(so / 100);
        so = so % 100;
        if (daydu || tram > 0) {
            chuoi = " " + arrNumber[tram.intValue()] + " trăm";
            chuoi += _dochangchuc(so, true);
        } else {
            chuoi = _dochangchuc(so, false);
        }
        return chuoi;
    }

	/*
	 * public static void main(String[] args){ String s =
	 * UtilServices.countNumberMoney("66.818"); System.out.println(s); }
	 */
}