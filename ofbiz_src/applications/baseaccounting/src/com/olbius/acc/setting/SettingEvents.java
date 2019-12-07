package com.olbius.acc.setting;

import com.olbius.acc.report.AccountingReportUtil;
import com.olbius.acc.utils.ErrorUtils;
import com.olbius.basehr.util.Organization;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.security.util.SecurityUtil;
import javolution.util.FastList;
import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntity;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map.Entry;

public class SettingEvents {
	public static final String module = SettingEvents.class.getName();
	
	public static String getGlAccountIdCostInfo(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        //LocalDispatcher dispatcher = (LocalDispatcher)request.getAttribute("dispatcher");
        //Locale locale = UtilHttp.getLocale(request);
        //TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String fromDateStr = request.getParameter("fromDate");
        String thruDateStr = request.getParameter("thruDate");
        Timestamp fromDate = UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fromDateStr)));
        Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
        String allocationCostTypeId = request.getParameter("allocationCostTypeId");
        try {
            String orgId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
            Organization buildOrg = PartyUtil.buildOrg(delegator, orgId, true, false);
            List<GenericValue> listOrg = buildOrg.getDirectChildList(delegator);
            if (UtilValidate.isEmpty(listOrg)) {
                listOrg = delegator.findByAnd("PartyGroup", UtilMisc.toMap("partyId", orgId), null, false);
            }
            Map<String, Map<String, BigDecimal>> partyGroupData = FastMap.newInstance();
            int size = listOrg.size();
            if ("REVENUE_ALLOCATED".equals(allocationCostTypeId)) {
                //TODO su dung service de lay ve doanh so theo partyId
                //long totalSubPercent = 0;
                for (int i = 0; i < size; i++) {
                    GenericValue partyGroup = listOrg.get(i);
                    Map<String, BigDecimal> tempMap = FastMap.newInstance();
                    partyGroupData.put(partyGroup.getString("partyId"), tempMap);
                    tempMap.put("quantity", BigDecimal.ZERO);
                    if (i == size - 1) {
                        tempMap.put("percent", new BigDecimal(100));
                    } else {
                        tempMap.put("percent", BigDecimal.ZERO);
                    }
                }
            } else if ("EMPL_ALLOCATED".equals(allocationCostTypeId)) {
                List<GenericValue> totalEmplList = buildOrg.getEmployeeInOrg(delegator, fromDate, thruDate, null, null);
                int totalEmpl = totalEmplList.size();
                if (listOrg.size() == 1) {
                    Map<String, BigDecimal> tempMap = FastMap.newInstance();
                    tempMap.put("quantity", new BigDecimal(totalEmpl));
                    tempMap.put("percent", new BigDecimal(100));
                    partyGroupData.put(listOrg.get(0).getString("partyId"), tempMap);
                } else {
                    long totalSubPercent = 0;
                    for (int i = 0; i < size; i++) {
                        GenericValue partyGroup = listOrg.get(i);
                        String partyGroupId = partyGroup.getString("partyId");
                        Organization tempBuildOrg = PartyUtil.buildOrg(delegator, partyGroupId, true, false);
                        List<GenericValue> tempEmplList = tempBuildOrg.getEmployeeInOrg(delegator, fromDate, thruDate, null, null);
                        double tempSubTotalEmpl = tempEmplList.size() * 100;
                        Map<String, BigDecimal> tempMap = FastMap.newInstance();
                        tempMap.put("quantity", new BigDecimal(tempSubTotalEmpl));
                        if (i == size - 1) {
                            tempMap.put("percent", new BigDecimal(100l - totalSubPercent));
                        } else {
                            long tempPercent = Math.round((tempSubTotalEmpl / totalEmpl));
                            totalSubPercent += tempPercent;
                            tempMap.put("percent", new BigDecimal(tempPercent));
                        }
                        partyGroupData.put(partyGroupId, tempMap);
                    }
                }
            }
            //TODO use service get list cost account instead of use delegator.find
            List<GenericValue> listCostAccount = delegator.findList("GlAccount", EntityCondition.makeCondition("accountCode", EntityJoinOperator.LIKE, "6%"), null, UtilMisc.toList("accountCode"), null, false);
            List<Map<String, Object>> listReturn = FastList.newInstance();
            for (GenericValue costAccount : listCostAccount) {
                Map<String, Object> tempMap = costAccount.getFields(UtilMisc.toList("accountCode", "accountName", "glAccountId"));
                //TODO need get value from service
                tempMap.put("totalCost", 0);
                tempMap.put("allocationCostTypeId", allocationCostTypeId);
                tempMap.put("totalPercent", 100);
                for (Entry<String, Map<String, BigDecimal>> entry : partyGroupData.entrySet()) {
                    String tempPartyId = entry.getKey();
                    tempMap.put(tempPartyId + "_quantity", entry.getValue().get("quantity"));
                    tempMap.put(tempPartyId + "_percent", entry.getValue().get("percent"));
                }
                listReturn.add(tempMap);
            }
            request.setAttribute("listReturn", listReturn);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            return "error";
        }
        return "success";
    }

    public static String createAllocationCostPeriod(HttpServletRequest request, HttpServletResponse response) throws GenericTransactionException {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String fromDateStr = request.getParameter("fromDate");
        String thruDateStr = request.getParameter("thruDate");
        Timestamp fromDate = UtilDateTime.getDayStart(new Timestamp(Long.parseLong(fromDateStr)));
        Timestamp thruDate = UtilDateTime.getDayEnd(new Timestamp(Long.parseLong(thruDateStr)));
        String allocationCostPeriodItem = request.getParameter("allocationCostPeriodItem");
        Map<String, Object> context = FastMap.newInstance();
        context.put("userLogin", userLogin);
        context.put("locale", locale);
        context.put("timeZone", timeZone);
        context.put("fromDate", fromDate);
        context.put("thruDate", thruDate);
        context.put("allocCostPeriodCode", request.getParameter("allocCostPeriodCode"));
        context.put("allocCostPeriodName", request.getParameter("allocCostPeriodName"));
        context.put("allocationCostTypeId", request.getParameter("allocationCostTypeId"));
        try {
            TransactionUtil.begin();
            try {
                Map<String, Object> resultService = dispatcher.runSync("createAllocationCostPeriod", context);
                if (!ServiceUtil.isSuccess(resultService)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    return "error";
                }
                String allocCostPeriodId = (String) resultService.get("allocCostPeriodId");
                Map<String, Object> allocCostItemMap = FastMap.newInstance();
                allocCostItemMap.put("userLogin", userLogin);
                allocCostItemMap.put("locale", locale);
                allocCostItemMap.put("timeZone", timeZone);
                allocCostItemMap.put("allocCostPeriodId", allocCostPeriodId);
                JSONArray allocCostPeriodItemJsonArr = JSONArray.fromObject(allocationCostPeriodItem);
                for (int i = 0; i < allocCostPeriodItemJsonArr.size(); i++) {
                    JSONObject allocCostPeriodItemJson = allocCostPeriodItemJsonArr.getJSONObject(i);
                    String allocationRateStr = allocCostPeriodItemJson.getString("allocationRate");
                    allocCostItemMap.put("partyId", allocCostPeriodItemJson.get("partyId"));
                    allocCostItemMap.put("glAccountId", allocCostPeriodItemJson.get("glAccountId"));
                    allocCostItemMap.put("allocationCostTypeId", allocCostPeriodItemJson.get("allocationCostTypeId"));
                    allocCostItemMap.put("allocationRate", new BigDecimal(allocationRateStr));
                    resultService = dispatcher.runSync("createAllocationCostPeriodItem", allocCostItemMap);
                    if (!ServiceUtil.isSuccess(resultService)) {
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                        return "error";
                    }
                }
                TransactionUtil.commit();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
                request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
            } catch (GenericServiceException e) {
                e.printStackTrace();
                TransactionUtil.rollback();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
            }
        } catch (GenericTransactionException e) {
            e.printStackTrace();
            TransactionUtil.rollback();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String editAllocationCostPeriodItem(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String allocationCostPeriodItem = request.getParameter("allocationCostPeriodItem");
        Map<String, Object> context = FastMap.newInstance();
        context.put("userLogin", userLogin);
        context.put("locale", locale);
        context.put("timeZone", timeZone);
        String allocCostPeriodId = request.getParameter("allocCostPeriodId");
        try {
            TransactionUtil.begin();
            Map<String, Object> allocCostItemMap = FastMap.newInstance();
            allocCostItemMap.put("userLogin", userLogin);
            allocCostItemMap.put("locale", locale);
            allocCostItemMap.put("timeZone", timeZone);
            allocCostItemMap.put("allocCostPeriodId", allocCostPeriodId);
            JSONArray allocCostPeriodItemJsonArr = JSONArray.fromObject(allocationCostPeriodItem);
            Map<String, Object> resultService = null;
            try {
                for (int i = 0; i < allocCostPeriodItemJsonArr.size(); i++) {
                    JSONObject allocCostPeriodItemJson = allocCostPeriodItemJsonArr.getJSONObject(i);
                    String allocationRateStr = allocCostPeriodItemJson.getString("allocationRate");
                    allocCostItemMap.put("partyId", allocCostPeriodItemJson.get("partyId"));
                    allocCostItemMap.put("allocCostPeriodSeqId", allocCostPeriodItemJson.get("allocCostPeriodSeqId"));
                    allocCostItemMap.put("glAccountId", allocCostPeriodItemJson.get("glAccountId"));
                    allocCostItemMap.put("allocationCostTypeId", allocCostPeriodItemJson.get("allocationCostTypeId"));
                    allocCostItemMap.put("allocationRate", new BigDecimal(allocationRateStr));
                    resultService = dispatcher.runSync("updateAllocationCostPeriodItem", allocCostItemMap);
                    if (!ServiceUtil.isSuccess(resultService)) {
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                        return "error";
                    }
                }
                TransactionUtil.commit();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
                request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
            } catch (GenericServiceException e) {
                e.printStackTrace();
                TransactionUtil.rollback();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                return "error";
            }
        } catch (GenericTransactionException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String getAllocationCostPeriodItemData(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        String allocCostPeriodId = request.getParameter("allocCostPeriodId");
        try {
            List<GenericValue> listParty = delegator.findByAnd("AllocCostPeriodItemAndParty", UtilMisc.toMap("allocCostPeriodId", allocCostPeriodId), null, false);
            List<GenericValue> listGlAccount = delegator.findByAnd("AllocCostPeriodItemAndGlAccount", UtilMisc.toMap("allocCostPeriodId", allocCostPeriodId), null, false);
            List<Map<String, Object>> listReturn = FastList.newInstance();
            for (GenericValue glAccount : listGlAccount) {
                Map<String, Object> tempMap = glAccount.getAllFields();
                //TODO need use service is supplied by VietTB to get amount of GlAccount in time period
                tempMap.put("totalCost", BigDecimal.ZERO);
                tempMap.put("totalPercent", new BigDecimal(100));
                for (GenericValue party : listParty) {
                    String partyId = party.getString("partyId");
                    List<GenericValue> allocationCostPeriodItems = delegator.findByAnd("AllocationCostPeriodItem", UtilMisc.toMap("allocCostPeriodId", allocCostPeriodId, "partyId", partyId, "glAccountId", glAccount.get("glAccountId")), null, false);
                    GenericValue allocationCostPeriodItem = allocationCostPeriodItems.get(0);
                    tempMap.put("allocationCostTypeId", allocationCostPeriodItem.get("allocationCostTypeId"));
                    tempMap.put(partyId + "_percent", allocationCostPeriodItem.get("allocationRate"));
                    tempMap.put(partyId + "_seqId", allocationCostPeriodItem.get("allocCostPeriodSeqId"));
                }
                listReturn.add(tempMap);
            }
            request.setAttribute("listParty", listParty);
            request.setAttribute("listData", listReturn);
        } catch (GenericEntityException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String updateInvoiceItemType(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        String parentTypeId = (String) paramMap.get("parentTypeId");
        String invoiceTypeId = (String) paramMap.get("invoiceTypeId");
        String defaultGlAccountId = (String) paramMap.get("defaultGlAccountId");
        try {
            Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateInvoiceItemType", paramMap, userLogin, timeZone, locale);
            context.put("userLogin", userLogin);
            context.put("parentTypeId", parentTypeId);
            context.put("invoiceTypeId", invoiceTypeId);
            context.put("defaultGlAccountId", defaultGlAccountId);
            Map<String, Object> resultService = dispatcher.runSync("updateInvoiceItemType", context);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute("_ERROR_MESSAGE_", ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            request.setAttribute("_EVENT_MESSAGE_", UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
        } catch (GeneralServiceException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", e.getLocalizedMessage());
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute("_ERROR_MESSAGE_", e.getLocalizedMessage());
        }
        return "success";
    }

    public static String createPaymentType(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        String isAppliedInvoice = (String) paramMap.get("isAppliedInvoice");
        try {
            TransactionUtil.begin();
            try {
                Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createPaymentType", paramMap, userLogin, timeZone, locale);
                if ("Y".equals(isAppliedInvoice)) {
                    context.put("isAppliedInvoice", Boolean.TRUE);
                } else {
                    context.put("isAppliedInvoice", Boolean.FALSE);
                }
                Map<String, Object> resultService = dispatcher.runSync("createPaymentType", context);
                if (!ServiceUtil.isSuccess(resultService)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    TransactionUtil.rollback();
                    return "error";
                }
                String glAccountTypeId = (String) paramMap.get("glAccountTypeId");
                if (glAccountTypeId != null) {
                    context = ServiceUtil.setServiceFields(dispatcher, "createOrStorePaymentTypeGlAccountType", paramMap, userLogin, timeZone, locale);
                    String organizationPartyId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
                    context.put("organizationPartyId", organizationPartyId);
                    resultService = dispatcher.runSync("createOrStorePaymentTypeGlAccountType", context);
                    if (!ServiceUtil.isSuccess(resultService)) {
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                        TransactionUtil.rollback();
                        return "error";
                    }
                }
                TransactionUtil.commit();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
                request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
            } catch (GeneralServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
                return "error";
            } catch (GenericServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
                return "error";
            } catch (GenericEntityException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
                return "error";
            }
        } catch (GenericTransactionException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String updatePaymentType(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        String isAppliedInvoice = (String) paramMap.get("isAppliedInvoice");
        try {
            TransactionUtil.begin();
            try {
                Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updatePaymentType", paramMap, userLogin, timeZone, locale);
                context.put("parentTypeId", paramMap.get("parentTypeId"));
                if ("Y".equals(isAppliedInvoice)) {
                    context.put("isAppliedInvoice", Boolean.TRUE);
                } else {
                    context.put("isAppliedInvoice", Boolean.FALSE);
                }
                Map<String, Object> resultService = dispatcher.runSync("updatePaymentType", context);
                if (!ServiceUtil.isSuccess(resultService)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    TransactionUtil.rollback();
                    return "error";
                }
                String glAccountTypeId = (String) paramMap.get("glAccountTypeId");
                String organizationPartyId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
                if (glAccountTypeId != null) {
                    context = ServiceUtil.setServiceFields(dispatcher, "createOrStorePaymentTypeGlAccountType", paramMap, userLogin, timeZone, locale);
                    context.put("organizationPartyId", organizationPartyId);
                    resultService = dispatcher.runSync("createOrStorePaymentTypeGlAccountType", context);
                    if (!ServiceUtil.isSuccess(resultService)) {
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                        TransactionUtil.rollback();
                        return "error";
                    }
                } else {
                    GenericValue paymentTypeGlAccountType = delegator.findOne("PaymentGlAccountTypeMap", UtilMisc.toMap("paymentTypeId", paramMap.get("paymentTypeId"), "organizationPartyId", organizationPartyId), false);
                    if (paymentTypeGlAccountType != null) {
                        paymentTypeGlAccountType.remove();
                    }
                }
                TransactionUtil.commit();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
                request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
            } catch (GeneralServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
                return "error";
            } catch (GenericServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
                return "error";
            } catch (GenericEntityException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
                return "error";
            }
        } catch (GenericTransactionException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String createGlAccountType(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        try {
            TransactionUtil.begin();
            try {
                Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "createGlAccountType", paramMap, userLogin, timeZone, locale);
                Map<String, Object> resultService = dispatcher.runSync("createGlAccountType", context);
                if (!ServiceUtil.isSuccess(resultService)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    TransactionUtil.rollback();
                    return "error";
                }
                String glAccountId = (String) paramMap.get("glAccountId");
                if (glAccountId != null) {
                    String organizationPartyId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
                    context = ServiceUtil.setServiceFields(dispatcher, "createOrStoreGlAccountTypeDefault", paramMap, userLogin, timeZone, locale);
                    context.put("organizationPartyId", organizationPartyId);
                    resultService = dispatcher.runSync("createOrStoreGlAccountTypeDefault", context);
                    if (!ServiceUtil.isSuccess(resultService)) {
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                        TransactionUtil.rollback();
                        return "error";
                    }
                }
                TransactionUtil.commit();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
                request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgaddsuccess", locale));
            } catch (GeneralServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
                return "error";
            } catch (GenericServiceException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
                return "error";
            } catch (GenericEntityException e) {
                e.printStackTrace();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                TransactionUtil.rollback();
                return "error";
            }
        } catch (GenericTransactionException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    /**
     * @function: đóng kỳ kế toán
     * @param request customTimePeriodId
     * thêm bản ghi đóng kỳ kế toán vào bảng: acctg_Trans, acctg_Trans_entry
     * cập nhật trạng thái is_closed = 'Y' trong bảng custom_time_period
     * @throws: GenericServiceException, GenericTransactionException, GenericEntityException
     * @author: ThaiNT
     */
    public static String closedCustomTimePeriod(HttpServletRequest request, HttpServletResponse response) throws GenericTransactionException {
        Long reciprocalItemSeqId = Long.valueOf(0);
        Integer reciprocalItemSeqDigit = 5;
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        String customTimePeriodId = request.getParameter("customTimePeriodId");
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        Boolean isCreatedAcctgTransAndEntriesSucceded = false;
        try {
        	Debug.log("SettingEvents::closedCustomTimePeriod START");
        			
        	/*
        	 * check and return error if there exists a not-closed child custom-time-period
        	 */
            GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
            List<GenericValue> openChildTimePeriods = customTimePeriod.getRelated("ChildCustomTimePeriod", UtilMisc.toMap("isClosed", "N"), null, false);
            if (openChildTimePeriods != null && !openChildTimePeriods.isEmpty()) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCNoCustomTimePeriodClosedChild", UtilMisc.toMap("customTimePeriodId", customTimePeriod.get("customTimePeriodId"), "periodName", openChildTimePeriods.get(0).get("periodName"), "openChildTimePeriodId", openChildTimePeriods.get(0).get("customTimePeriodId")), locale));
                TransactionUtil.rollback();
                return "error";
            }

            GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
            String organizationPartyId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
            Date fromDate = (Date) customTimePeriod.get("fromDate");

            Map<String, Object> context = FastMap.newInstance();
            context.put("userLogin", userLogin);
            context.put("locale", locale);
            context.put("timeZone", timeZone);
            context.put("fromDate", fromDate);
            context.put("organizationPartyId", organizationPartyId);
            /*
             * find the last closed custom-time-period to obtain last closed time period (ngay dong ky gan nhat) and isFirst (true,
             * neu ky ke toan hien tai la ky dau tien, dong thoi luc nay lastClosedDate = fromDate cua ky te toan hien tai)
             */
            Map<String, Object> resultService = dispatcher.runSync("findPreviewCustomtimePeriodClosed", context);
            GenericValue lastClosedTimePeriod = (GenericValue) resultService.get("lastClosedTimePeriod");
            Boolean isFirst = (Boolean) resultService.get("isFirst");
            Timestamp lastClosedDate = (Timestamp) resultService.get("lastClosedDate");

            if (lastClosedTimePeriod == null && !isFirst) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCNoPreviewCustomTimePeriodClosedForClosedDate", UtilMisc.toMap("customTimePeriodId", customTimePeriod.get("customTimePeriodId"), "organizationPartyId", organizationPartyId), locale));
                TransactionUtil.rollback();
                return "error";
            }

            if (lastClosedDate == null) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCNoCustomTimePeriodClosedForClosedDate", UtilMisc.toMap("customTimePeriodId", customTimePeriod.get("customTimePeriodId"), "organizationPartyId", organizationPartyId), locale));
                TransactionUtil.rollback();
                return "error";
            }

            /*
             * Tim tat ca cac giao dich (acctg_trans_entry) lien quan den tai khoan EXPENSE (6), REVENUE (5), INCOME (7)
             * trong do gl_fiscal_type_id = "ACTUAL", ngay giao dich (transactionDate nam trong ky ke toan hien tai)
             * va isPosted = 'Y' (da ghi so) 
             */
            GenericValue expenseGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "EXPENSE"), false);
            List<String> expenseAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(expenseGlAccountClass);
            GenericValue revenueGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "REVENUE"), false);
            List<String> revenueAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(revenueGlAccountClass);
            GenericValue incomeGlAccountClass = delegator.findOne("GlAccountClass", UtilMisc.toMap("glAccountClassId", "INCOME"), true);
            List<String> incomeAccountClassIds = UtilAccounting.getDescendantGlAccountClassIds(incomeGlAccountClass);

            Debug.log("SettingEvents::closedCustomTimePeriod expenseAccountClassIds = ");
            for(String c: expenseAccountClassIds) Debug.log(c + "\n");
            
            Debug.log("SettingEvents::closedCustomTimePeriod revenueAccountClassIds = ");
            for(String c: revenueAccountClassIds) Debug.log(c + "\n");
            
            Debug.log("SettingEvents::closedCustomTimePeriod incomeAccountClassIds = ");
            for(String c: incomeAccountClassIds) Debug.log(c + "\n");
            
            List<EntityCondition> listCondition1 = FastList.newInstance();
            List<EntityCondition> listCondition2 = FastList.newInstance();

            EntityCondition tempCondition = null;
            listCondition1.add(EntityCondition.makeCondition("organizationPartyId", customTimePeriod.get("organizationPartyId")));
            listCondition1.add(EntityCondition.makeCondition("isPosted", "Y"));
            listCondition1.add(EntityCondition.makeCondition("glFiscalTypeId", "ACTUAL"));
            listCondition1.add(EntityCondition.makeCondition("transactionDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toTimestamp((Date) customTimePeriod.get("fromDate"))));
            listCondition1.add(EntityCondition.makeCondition("transactionDate", EntityOperator.LESS_THAN_EQUAL_TO, UtilDateTime.toTimestamp((Date) customTimePeriod.get("thruDate"))));
            listCondition1.add(EntityCondition.makeCondition("acctgTransTypeId", EntityOperator.NOT_EQUAL, "PERIOD_CLOSING"));

            listCondition2.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, expenseAccountClassIds));
            listCondition2.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, revenueAccountClassIds));
            listCondition2.add(EntityCondition.makeCondition("glAccountClassId", EntityOperator.IN, incomeAccountClassIds));
            tempCondition = EntityCondition.makeCondition(EntityCondition.makeCondition(listCondition1), EntityOperator.AND, EntityCondition.makeCondition(listCondition2, EntityOperator.OR));

            List<GenericValue> acctgTransAndEntries = delegator.findList("AcctgTransAndEntries", tempCondition, null, UtilMisc.toList("acctgTransId", "acctgTransEntrySeqId"), null, false);

            /*
             * tinh tong so phat sinh, phat sinh co, phat sinh no
             */
            BigDecimal totalAmount = BigDecimal.ZERO;// tong so phat sinh
            BigDecimal totalCreditAmount = BigDecimal.ZERO;// tong phat sinh co
            BigDecimal totalDebitAmount = BigDecimal.ZERO;// tong phat sinh no

            for (GenericValue acctgTransAndEntry : acctgTransAndEntries) {
                BigDecimal amount = acctgTransAndEntry.getBigDecimal("amount");
                
                if (acctgTransAndEntry.get("debitCreditFlag").equals("D")) {
                    totalDebitAmount = totalDebitAmount.add(amount);
                } else {
                    totalCreditAmount = totalCreditAmount.add(amount);
                }
                
                Debug.log(module + "::closedCustomTimePeriod, accumulate amount gl_account = " + acctgTransAndEntry.get("glAccountId")
                		+ ", acctgTransId = " + acctgTransAndEntry.get("acctgTransId")
                		+ ", acctgTransEntryId = " + acctgTransAndEntry.get("acctgTransEntrySeqId")
                		+ ", amount = " + acctgTransAndEntry.get("amount")
                		+ ", DebitCredit = " + acctgTransAndEntry.get("debitCreditFlag")
                		+ ", totalDebitAmount = " + totalDebitAmount
                		+ ", totalCreditAmount = " + totalCreditAmount);
                
            }
            totalAmount = totalCreditAmount.subtract(totalDebitAmount);

            Debug.log("SettingEvents::closedCustomTimePeriod, totalAmount = " + totalAmount);
            
            context = FastMap.newInstance();
            context.put("userLogin", userLogin);
            context.put("locale", locale);
            context.put("organizationPartyId", customTimePeriod.get("organizationPartyId"));
            Map<String, Object> partyAccountingPreferencesCallMap = dispatcher.runSync("getPartyAccountingPreferences", context);
            GenericValue partyAcctgPreference = (GenericValue) partyAccountingPreferencesCallMap.get("partyAccountingPreference");

            /*
             * Lay cac tai khoan de thuc hien ket chuyen (bang acc_closing_entry luu cac cau hinh TK thu hien ket chuyen)
             */
            List<GenericValue> listAccClosingEntry = delegator.findList("AccClosingEntry", 
            		EntityCondition.makeCondition(EntityCondition.makeCondition("organizationPartyId", 
            				customTimePeriod.get("organizationPartyId")), 
            				EntityCondition.makeCondition("closeTimePeriod", "N")), 
            				null, UtilMisc.toList("orderIndex"), null, false);

            //List<GenericEntity> acctgTransEntries = FastList.newInstance(); PQD move this into the LOOP below
            for (GenericValue accClosingEntry : listAccClosingEntry) {
            	List<GenericEntity> acctgTransEntries = FastList.newInstance();
            	
                BigDecimal formulaValue = AccountingReportUtil.buildAndCalculate(accClosingEntry.getString("formula"), 
                		customTimePeriod.getString("customTimePeriodId"), delegator, 
                		customTimePeriod.getString("organizationPartyId"));
                reciprocalItemSeqId = reciprocalItemSeqId + 1;
                String formatPadded = UtilFormatOut.formatPaddedNumber(reciprocalItemSeqId, reciprocalItemSeqDigit);
                
                Debug.log("SettingEvents::closedCustomTimePeriod, name = " + (String)accClosingEntry.get("name") +
                	(String)accClosingEntry.get("formula") +	
                	", formuleValue = " + formulaValue);
                
                GenericValue creditEntry = delegator.makeValue("AcctgTransEntry", UtilMisc.toMap(
                        "debitCreditFlag", "C",
                        "glAccountTypeId", accClosingEntry.get("glAccountIdC"),
                        "organizationPartyId", customTimePeriod.get("organizationPartyId"),
                        "origAmount", formulaValue,
                        "origCurrencyUomId", partyAcctgPreference.get("baseCurrencyUomId"),
                        "reciprocalSeqId", formatPadded,
                        "currencyUomId", partyAcctgPreference.get("baseCurrencyUomId")));
                GenericValue debitEntry = delegator.makeValue("AcctgTransEntry", UtilMisc.toMap(
                        "currencyUomId", partyAcctgPreference.get("baseCurrencyUomId"),
                        "debitCreditFlag", "D",
                        "glAccountTypeId", accClosingEntry.get("glAccountIdD"),
                        "organizationPartyId", customTimePeriod.get("organizationPartyId"),
                        "origAmount", formulaValue,
                        "origCurrencyUomId", partyAcctgPreference.get("baseCurrencyUomId"),
                        "reciprocalSeqId", formatPadded));
                acctgTransEntries.add(creditEntry);
                acctgTransEntries.add(debitEntry);
                
                context = FastMap.newInstance();
                context.put("userLogin", userLogin);
                context.put("glFiscalTypeId", "ACTUAL");
                context.put("transactionDate", customTimePeriod.get("thruDate"));
                context.put("acctgTransEntries", acctgTransEntries);
                context.put("acctgTransTypeId", "CLOSING_ENTRY");
                context.put("organizationPartyId", organizationPartyId);
                Debug.log(module + "::closedCustomTimePeriod, START create acctg_trans closing entry");
                dispatcher.runSync("createAcctgTransAndEntries", context);
                Debug.log(module + "::closedCustomTimePeriod, FINISH create acctg_trans closing entry");
            }
            /* PQD move this into the LOOP above
            context = FastMap.newInstance();
            context.put("userLogin", userLogin);
            context.put("glFiscalTypeId", "ACTUAL");
            context.put("transactionDate", customTimePeriod.get("thruDate"));
            context.put("acctgTransEntries", acctgTransEntries);
            context.put("acctgTransTypeId", "CLOSING_ENTRY");
            context.put("organizationPartyId", organizationPartyId);
            Debug.log(module + "::closedCustomTimePeriod, START create acctg_trans closing entry");
            dispatcher.runSync("createAcctgTransAndEntries", context);
            Debug.log(module + "::closedCustomTimePeriod, FINISH create acctg_trans closing entry");
            */
            
            /*
             * tim tai khoan 421 RETAINED_EARNINGS (loi nhuan sau thue chua phan phoi)
             */
            GenericValue profitLossAccount = delegator.findOne("GlAccountTypeDefault",
                    UtilMisc.toMap(
                            "organizationPartyId", customTimePeriod.get("organizationPartyId"),
                            "glAccountTypeId", "RETAINED_EARNINGS"), true);
            GenericValue profitLossAccountHistory = delegator.findOne("GlAccountHistory", UtilMisc.toMap(
                    "organizationPartyId", customTimePeriod.get("organizationPartyId"),
                    "customTimePeriodId", customTimePeriod.get("customTimePeriodId"),
                    "glAccountId", profitLossAccount.get("glAccountId")), false);
            /*
             * neu TK 421 trong gl_account_history da ton tai so du cuoi ky va khac 0 thi ERROR
             */
            if (profitLossAccountHistory != null && !profitLossAccountHistory.isEmpty()) {
                if (profitLossAccountHistory.getBigDecimal("endingBalance").compareTo(totalAmount) != 0) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, UtilProperties.getMessage("BaseAccountingUiLabels", "BACCPostedBalanceAlreadyPresent", UtilMisc.toMap("postedBalance", profitLossAccountHistory.getBigDecimal("endingBalance"), "totalAmount", totalAmount), locale));
                    TransactionUtil.rollback();
                    return "error";
                } 
            }else {
                    reciprocalItemSeqId = reciprocalItemSeqId + 1;
                    String formatPadded = UtilFormatOut.formatPaddedNumber(reciprocalItemSeqId, reciprocalItemSeqDigit);
                    List<GenericEntity> acctgTransEntries = FastList.newInstance();
                    /*
                     * ghi nhan tong so du phat sinh totalAmount vao ben Co cua TK 421 (loi nhuan sau thue chua phan phoi
                     *  va vao ben No cua TK 911 (xac dinh ket qua kinh doanh)
                     */
                    GenericValue creditEntry = delegator.makeValue("AcctgTransEntry",
                            UtilMisc.toMap("debitCreditFlag", "C",
                                    "glAccountTypeId", "RETAINED_EARNINGS",// 421 (loi nhuan sau thue chua phan phoi)
                                    "organizationPartyId", customTimePeriod.get("organizationPartyId"),
                                    "origAmount", totalAmount, "origCurrencyUomId",
                                    partyAcctgPreference.get("baseCurrencyUomId"),
                                    "reciprocalSeqId", formatPadded));

                    GenericValue debitEntry = delegator.makeValue("AcctgTransEntry",
                            UtilMisc.toMap("debitCreditFlag", "D",
                                    "glAccountTypeId", "PROFIT_LOSS_ACCOUNT",//911 (xac dinh ket qua kinh doanh)
                                    "organizationPartyId", customTimePeriod.get("organizationPartyId"),
                                    "origAmount", totalAmount,
                                    "origCurrencyUomId", partyAcctgPreference.get("baseCurrencyUomId"),
                                    "reciprocalSeqId", formatPadded));
                    /*
                     * tao giao dich dong ky ke toan PERIOD_CLOSING, voi ngay giao dich la ngay cuoi cung cua ky ke toan hien tai
                     */
                    acctgTransEntries.add(creditEntry);
                    acctgTransEntries.add(debitEntry);
                    context = FastMap.newInstance();
                    context.put("userLogin", userLogin);
                    context.put("glFiscalTypeId", "ACTUAL");
                    context.put("acctgTransTypeId", "PERIOD_CLOSING");
                    context.put("transactionDate", customTimePeriod.get("thruDate"));
                    context.put("acctgTransEntries", acctgTransEntries);
                    Debug.log(module + "::closedCustomTimePeriod, START PERIOD_CLOSING, totalAmount = " + totalAmount);
                    dispatcher.runSync("createAcctgTransAndEntries", context);
                    Debug.log(module + "::closedCustomTimePeriod, FINISHED PERIOD_CLOSING, totalAmount = " + totalAmount);
                    
                }
            //}

            /*
             * duyet qua cac TK cua cty, tinh toan cac khoan so du dau ky, cuoi ky va phat sinh trong ky,
             * ghi vao gl_account_history
             */
            List<GenericValue> organizationGlAccounts = delegator.findList("GlAccountOrganization",
                    EntityCondition.makeCondition (
                            EntityCondition.makeCondition("organizationPartyId", customTimePeriod.get("organizationPartyId")),
                            EntityCondition.makeCondition("fromDate", EntityOperator.LESS_THAN, UtilDateTime.toTimestamp((Date) customTimePeriod.get("thruDate"))),
                            EntityCondition.makeCondition(
                                    EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN_EQUAL_TO, UtilDateTime.toTimestamp((Date) customTimePeriod.get("fromDate"))),
                                    EntityOperator.OR,
                                    EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null))),
                    null, null, null, false);
            
            
            for (GenericValue organizationGlAccount : organizationGlAccounts) {
            	Debug.log("SettingEvents::closedCustomTimePeriod, gl_account_organization = " + (String)organizationGlAccount.get("glAccountId"));
            }
            
            for (GenericValue organizationGlAccount : organizationGlAccounts) {
            	GenericValue glAccountHistory = delegator.findOne("GlAccountHistory",
                        UtilMisc.toMap("customTimePeriodId", customTimePeriod.get("customTimePeriodId"),
                                "organizationPartyId", customTimePeriod.get("organizationPartyId"),
                                "glAccountId", organizationGlAccount.get("glAccountId")), false);
                if (glAccountHistory == null) {
                    glAccountHistory = delegator.makeValue("GlAccountHistory",
                            UtilMisc.toMap("customTimePeriodId", customTimePeriod.get("customTimePeriodId"),
                                    "organizationPartyId", customTimePeriod.get("organizationPartyId"),
                                    "glAccountId", organizationGlAccount.get("glAccountId")));
                    delegator.create(glAccountHistory);
                }

                context = FastMap.newInstance();
                context.put("userLogin", userLogin);
                context.put("isFirst", isFirst);
                context.put("lastClosedTimePeriod", lastClosedTimePeriod);
                context.put("organizationPartyId", glAccountHistory.get("organizationPartyId"));
                context.put("glAccountId", glAccountHistory.get("glAccountId"));
                context.put("customTimePeriodId", customTimePeriod.get("customTimePeriodId"));

                dispatcher.runSync("computeAndStoreGlAccountHistoryBalanceFromHistory", context);
            }

            /*
             * Cap nhat thong tin vao ban gi custom-time-period set isClosed = Y de xac dinh ky ke toan da dong
             */
            context = FastMap.newInstance();
            context.put("userLogin", userLogin);
            context.put("customTimePeriodId", customTimePeriod.get("customTimePeriodId"));
            context.put("organizationPartyId", customTimePeriod.get("organizationPartyId"));
            context.put("isClosed", "Y");
            dispatcher.runSync("updateCustomTimePeriod", context);

            /*
             * Neu tat ca cac ky ke toan con cua 1 ky ke toan cha da dong thi ky ke toan cha cung phai dong, thiet lap isClosed = Y
             */
            context = FastMap.newInstance();
            context.put("userLogin", userLogin);
            context.put("customTimePeriodId", customTimePeriod.get("customTimePeriodId"));
            context.put("organizationPartyId", customTimePeriod.get("organizationPartyId"));
            dispatcher.runSync("updateParentCustomTimePeriod", context);
        } catch (GenericServiceException e) {
            TransactionUtil.rollback();
            return "error";
        } catch (GenericTransactionException e) {
            TransactionUtil.rollback();
            e.printStackTrace();
            return "error";
        } catch (GenericEntityException e) {
            TransactionUtil.rollback();
            e.printStackTrace();
            return "error";
        } catch (Exception e) {
            TransactionUtil.rollback();
            e.printStackTrace();
            return "error";
        }
        return "success";
    }

    public static String updateGlAccountType(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        Locale locale = UtilHttp.getLocale(request);
        TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
        try {
            TransactionUtil.begin();
            try {
                Map<String, Object> context = ServiceUtil.setServiceFields(dispatcher, "updateGlAccountType", paramMap, userLogin, timeZone, locale);
                context.put("parentTypeId", paramMap.get("parentTypeId"));
                Map<String, Object> resultService = dispatcher.runSync("updateGlAccountType", context);
                if (!ServiceUtil.isSuccess(resultService)) {
                    request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                    request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                    TransactionUtil.rollback();
                    return "error";
                }

                String glAccountId = (String) paramMap.get("glAccountId");
                String organizationPartyId = PartyUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
                if (glAccountId != null) {
                    context = ServiceUtil.setServiceFields(dispatcher, "createOrStoreGlAccountTypeDefault", paramMap, userLogin, timeZone, locale);
                    context.put("organizationPartyId", organizationPartyId);
                    resultService = dispatcher.runSync("createOrStoreGlAccountTypeDefault", context);
                    if (!ServiceUtil.isSuccess(resultService)) {
                        request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                        request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                        TransactionUtil.rollback();
                        return "error";
                    }
                } else {
                    GenericValue glAccountTypeDefault = delegator.findOne("GlAccountTypeDefault", UtilMisc.toMap("glAccountTypeId", paramMap.get("glAccountTypeId"), "organizationPartyId", organizationPartyId), false);
                    if (glAccountTypeDefault != null) {
                        glAccountTypeDefault.remove();
                    }
                }
                TransactionUtil.commit();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
                request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
            } catch (GeneralServiceException e) {
                e.printStackTrace();
                TransactionUtil.rollback();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                return "error";
            } catch (GenericServiceException e) {
                e.printStackTrace();
                TransactionUtil.rollback();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                return "error";
            } catch (GenericEntityException e) {
                e.printStackTrace();
                TransactionUtil.rollback();
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
                return "error";
            }
        } catch (GenericTransactionException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        }
        return "success";
    }

    public static String getPartyContact(HttpServletRequest request, HttpServletResponse response) {
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        //Locale locale = UtilHttp.getLocale(request);
        //TimeZone timeZone = UtilHttp.getTimeZone(request);
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        String partyId = request.getParameter("partyId");
        try {
            Map<String, Object> resultService = dispatcher.runSync("getPartyPostalAddress", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_LOCATION"));
            String contactMechId = (String) resultService.get("contactMechId");
            if (contactMechId != null) {
                String address = (String) resultService.get("address1");
                String districtGeoId = (String) resultService.get("districtGeoId");
                String stateProvinceGeoId = (String) resultService.get("stateProvinceGeoId");
                String countryGeoId = (String) resultService.get("countryGeoId");
                GenericValue districtGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", districtGeoId), false);
                GenericValue stateProvinceGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", stateProvinceGeoId), false);
                GenericValue countryGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", countryGeoId), false);
                if (districtGeo != null) {
                    address += ", " + districtGeo.getString("geoName");
                }
                if (stateProvinceGeo != null) {
                    address += ", " + stateProvinceGeo.getString("geoName");
                }
                if (countryGeo != null) {
                    address += ", " + countryGeo.getString("geoName");
                }
                request.setAttribute("address", address);
            }
            resultService = dispatcher.runSync("getPartyTelephone", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "contactMechPurposeTypeId", "PRIMARY_PHONE"));
            if (resultService.get("contactMechId") != null) {
                String contactNumber = (String) resultService.get("contactNumber");
                request.setAttribute("phoneNbr", contactNumber);
            }
            resultService = dispatcher.runSync("getPartyTelephone", UtilMisc.toMap("userLogin", userLogin, "partyId", partyId, "contactMechPurposeTypeId", "FAX_NUMBER"));
            if (resultService.get("contactMechId") != null) {
                String contactNumber = (String) resultService.get("contactNumber");
                request.setAttribute("faxNbr", contactNumber);
            }
        } catch (GenericServiceException e) {
            e.printStackTrace();
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getLocalizedMessage());
        } catch (GenericEntityException e) {
            e.printStackTrace();
        }

        return "success";
    }

    public static String createCustomerTimePayment(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");
        Locale locale = UtilHttp.getLocale(request);
        // Get parameter information general
        String dueDayStr = request.getParameter("dueDay");
        String fromDateStr = request.getParameter("fromDate");
        String partyId = request.getParameter("partyId");

        Map<String, Object> contextService = FastMap.newInstance();
        contextService.put("userLogin", userLogin);
        contextService.put("dueDay", new Long(dueDayStr));
        contextService.put("fromDate", new Timestamp(Long.valueOf(fromDateStr)));
        contextService.put("partyId", partyId);
        try {
            Map<String, Object> resultService = dispatcher.runSync("createCustomerTimePayment", contextService);
            if (!ServiceUtil.isSuccess(resultService)) {
                request.setAttribute(ModelService.RESPONSE_MESSAGE, "error");
                request.setAttribute(ModelService.ERROR_MESSAGE, ErrorUtils.getErrorMessageFromService(resultService));
                return "error";
            }
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
            request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
        } catch (GenericServiceException e) {
            request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
            return "error";
        }
        return "success";
    }

    public static String updateCustomerTimePayment(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        Locale locale = UtilHttp.getLocale(request);

        // Get parameter information general
        String dueDayStr = request.getParameter("dueDay");
        String fromDateStr = request.getParameter("fromDate");
        String partyId = request.getParameter("partyId");
        String customerTimePaymentId = request.getParameter("customerTimePaymentId");

        Map<String, Object> contextService = FastMap.newInstance();
        contextService.put("userLogin", userLogin);
        contextService.put("dueDay", new Long(dueDayStr));
        contextService.put("fromDate", new Timestamp(Long.valueOf(fromDateStr)));
        contextService.put("partyId", partyId);
        contextService.put("customerTimePaymentId", customerTimePaymentId);
        try {
            dispatcher.runSync("updateCustomerTimePayment", contextService);
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
            request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
        } catch (GenericServiceException e) {
            request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
            return "error";
        }
        return "success";
    }

    public static String expireCustomerTimePayment(HttpServletRequest request, HttpServletResponse response) {
        LocalDispatcher dispatcher = (LocalDispatcher) request.getAttribute("dispatcher");
        HttpSession session = request.getSession();
        GenericValue userLogin = (GenericValue) session.getAttribute("userLogin");

        Locale locale = UtilHttp.getLocale(request);

        // Get parameter information general
        String customerTimePaymentId = request.getParameter("customerTimePaymentId");

        Map<String, Object> contextService = FastMap.newInstance();
        contextService.put("userLogin", userLogin);
        contextService.put("thruDate", UtilDateTime.nowTimestamp());
        contextService.put("customerTimePaymentId", customerTimePaymentId);
        try {
            dispatcher.runSync("updateCustomerTimePayment", contextService);
            request.setAttribute(ModelService.RESPONSE_MESSAGE, "success");
            request.setAttribute(ModelService.SUCCESS_MESSAGE, UtilProperties.getMessage("WidgetUiLabels", "wgupdatesuccess", locale));
        } catch (GenericServiceException e) {
            request.setAttribute(ModelService.RESPONSE_MESSAGE, ModelService.RESPOND_ERROR);
            request.setAttribute(ModelService.ERROR_MESSAGE, e.getMessage());
            return "error";
        }
        return "success";
    }


}
