package com.olbius.basehr.payroll.services;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javolution.util.FastList;
import javolution.util.FastMap;

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
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.party.party.PartyHelper;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ModelService;
import org.ofbiz.service.ServiceAuthException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.service.ServiceValidationException;
import org.ofbiz.service.calendar.RecurrenceRule;

import com.ibm.icu.text.DateFormat;
import com.olbius.accounting.invoice.InvoiceWorker;
import com.olbius.basehr.payroll.worker.PayrollWorker;
import com.olbius.basehr.util.CommonUtil;
import com.olbius.basehr.util.PartyUtil;
import com.olbius.basehr.util.PropertiesUtil;
import com.olbius.basehr.util.ReportUtils;

public class PayrollInvoiceServices {
	public static final String module = PayrollInvoiceServices.class.getName();
    public static final String resource = "hrolbiusUiLabels";
    public static final String resourceNoti = "NotificationUiLabels";
	
    
    public static Map<String, Object> activeCreatePayrollInvoiceAndPayment(DispatchContext ctx, Map<String, ? extends Object> context) {
    	LocalDispatcher localDispatcher = ctx.getDispatcher();
    	Delegator delegator = ctx.getDelegator();
    	Locale locale = (Locale)context.get("locale");
    	String payrollTableId = (String)context.get("payrollTableId");
    	//Get paid employee
    	EntityCondition condition1 = EntityCondition.makeCondition("statusId", "PAYR_APP");
    	//FIXME code "LUONG" is now hard fix, need use global setting
    	//EntityCondition condition2 = EntityCondition.makeCondition("code", "LUONG");
    	EntityCondition condition2 = EntityCondition.makeCondition("payrollTableId", payrollTableId);
    	String fromDateStr = (String)context.get("fromDate");
    	Timestamp fromDate = new Timestamp(Long.parseLong(fromDateStr));
    	List<EntityCondition> conditions = FastList.newInstance();
    	conditions.add(condition1);
    	conditions.add(condition2);
    	conditions.add(EntityCondition.makeCondition("fromDate", fromDate));
    	List<GenericValue> paidEmployeeList = FastList.newInstance();
    	try {
    		paidEmployeeList = delegator.findList("PayrollTableGroupBy", EntityCondition.makeCondition(conditions, EntityOperator.AND), UtilMisc.toSet("payrollTableId", "partyId", "fromDate", "thruDate"), null, null, false);
		} catch (GenericEntityException e) {
			Debug.logError(e, module);
			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                    "generateInvoiceAndPaymentError", new Object[] { e.getMessage() }, locale));
		}
    	if(UtilValidate.isEmpty(paidEmployeeList)){
    		ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels.xml", "NoEmplCalcPayrollSalary", locale));
    	}
    	for(GenericValue paidEmployee : paidEmployeeList){
    		String partyId = paidEmployee.getString("partyId");
    		Map<String, Object> contextTmp = FastMap.newInstance();
    		contextTmp.put("partyId", partyId);
    		contextTmp.put("userLogin", context.get("userLogin"));
    		contextTmp.put("locale", context.get("locale"));
    		contextTmp.put("currencyUomId", context.get("currencyUomId"));
    		contextTmp.put("fromDate", paidEmployee.getTimestamp("fromDate"));
    		contextTmp.put("thruDate", paidEmployee.getTimestamp("thruDate"));
    		contextTmp.put("payrollTableId", payrollTableId);
    		try {
    			//String code = paidEmployee.getString("code");
    			//if("LUONG".equals(code)){
    				//contextTmp.put("code", code);
    				//localDispatcher.runAsync("createPayrollInvoiceAndPayment", contextTmp);
    				localDispatcher.schedule("pool", "createPayrollInvoiceAndPayment", contextTmp, UtilDateTime.nowTimestamp().getTime(), RecurrenceRule.DAILY, 1, 1, -1, 0);
    			//}
			}catch (ServiceAuthException e) {
				Debug.logError(e, module);
    			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                        "generateInvoiceAndPaymentError", new Object[] { e.getMessage() }, locale));
			} catch (ServiceValidationException e) {
				Debug.logError(e, module);
    			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                        "generateInvoiceAndPaymentError", new Object[] { e.getMessage() }, locale));
			} catch (GenericServiceException e) {
				Debug.logError(e, module);
    			return ServiceUtil.returnError(UtilProperties.getMessage(resourceNoti, 
                        "generateInvoiceAndPaymentError", new Object[] { e.getMessage() }, locale));
			}
    	}
    	return ServiceUtil.returnSuccess(UtilProperties.getMessage(resourceNoti, "generateInvoiceAndPaymentActived", locale));
    }
    
    public static Map<String, Object> notifyErrCreateInvoicePayment(DispatchContext dctx, Map<String, Object> context){
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Delegator delegator = dctx.getDelegator();
    	String payrollTableId = (String)context.get("payrollTableId");
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
    	Calendar calFromDate = Calendar.getInstance();
    	calFromDate.setTime(fromDate);
    	Calendar calThruDate = Calendar.getInstance();
    	calThruDate.setTime(thruDate);
    	String partyId = (String)context.get("partyId");
    	String displayFromDate = calFromDate.get(Calendar.YEAR) + "-" + calFromDate.get(Calendar.MONTH) + "-" + calFromDate.get(Calendar.DATE);
    	String displayThruDate = calThruDate.get(Calendar.YEAR) + "-" + calThruDate.get(Calendar.MONTH) + "-" + calThruDate.get(Calendar.DATE);
    	Map<String, Object> ntfCtx = FastMap.newInstance();
    		
		ntfCtx.put("partyId", userLogin.getString("partyId"));
		ntfCtx.put("header", "Xảy ra lỗi khi tạo hóa đơn tính lương từ ngày " + displayFromDate + "đến ngày " + displayThruDate +" cho " + PartyHelper.getPartyName(delegator, partyId, false));
		ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
		ntfCtx.put("targetLink", "payrollTableId=" + payrollTableId);
		ntfCtx.put("state", "open");
		ntfCtx.put("ntfType", "ONE");
		ntfCtx.put("action", "ApprovalPayrollTable");
		ntfCtx.put("userLogin", userLogin);
		try {
			dispatcher.runSync("createNotification", ntfCtx);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
    	return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> createNtfAndEmailPartyPayroll(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
    	GenericValue userLogin = (GenericValue) context.get("userLogin");
    	String invoiceId = (String) context.get("invoiceId");
    	try {
			GenericValue invoiceGv = delegator.findOne("Invoice", UtilMisc.toMap("invoiceId", invoiceId), false);
			String emplPartyId = invoiceGv.getString("partyIdFrom");
			String companyId = invoiceGv.getString("partyId");
			Locale locale = (Locale) context.get("locale");
			Properties generalProp = UtilProperties.getProperties("general");
			String email = generalProp.getProperty("mail.smtp.auth.user");
			String password = generalProp.getProperty("lbqiacdmftrmdiad");
			//TimeZone timeZone = (TimeZone) context.get("timeZone");
			DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT, locale);
			String header = UtilProperties.getMessage("PayrollUiLabels", "HRPayrollInformation", locale);
			String commonFromDate = UtilProperties.getMessage("HrCommonUiLabels", "CommonFromDate", locale);
			String commonThruDate = UtilProperties.getMessage("HrCommonUiLabels", "CommonThruDate", locale);
			Map<String, Object> ntfCtx = FastMap.newInstance();
			ntfCtx.put("header", header + " " + commonFromDate + " " + df.format(new Date(fromDate.getTime())) + " " + commonThruDate + " " + df.format(new Date(thruDate.getTime())));
			ntfCtx.put("partyId", emplPartyId);
			ntfCtx.put("dateTime", UtilDateTime.nowTimestamp());
			ntfCtx.put("state", "open");
			ntfCtx.put("userLogin", context.get("userLogin"));
			ntfCtx.put("ntfType", "ONE");
			ntfCtx.put("targetLink", "partyId=" + emplPartyId + ";fromDate=" + fromDate + ";thruDate=" + thruDate + ";statusId=PAYR_PAID");
			ntfCtx.put("action", "PayrollTablePartyHistory");
			dispatcher.runSync("createNotification", ntfCtx);
			
			//send email to employee
			Map<String, Object> emailAddress = dispatcher.runSync("getPartyEmail", UtilMisc.toMap("partyId", emplPartyId, "userLogin", context.get("userLogin"), "locale",context.get("userLogin") ));
			Map<String, Object> emailCtx = FastMap.newInstance();
			Map<String, Object> bodyParameters = FastMap.newInstance();
			List<GenericValue> emplPosition = PartyUtil.getCurrPositionTypeOfEmpl(delegator, emplPartyId);
			String emplPositionStr = "";
			if(UtilValidate.isNotEmpty(emplPosition)){
				GenericValue emplPos = EntityUtil.getFirst(emplPosition);
				String emplPositionTypeId = emplPos.getString("emplPositionTypeId");
				GenericValue emplPosType = delegator.findOne("EmplPositionType", UtilMisc.toMap("emplPositionTypeId", emplPositionTypeId), false);
				if(UtilValidate.isNotEmpty(emplPosType)){
					emplPositionStr = emplPosType.getString("description");
				}
			}			
			bodyParameters.put("companyName", PartyHelper.getPartyName(delegator, companyId, false));
			//bodyParameters.put("companyAddress", ContactMechWorker.getPartyPostalAddresses(request, partyId, curContactMechId));
			//bodyParameters.put("title", "Thông tin lương");
			bodyParameters.put("employeeId", emplPartyId);
			bodyParameters.put("employeeName", PartyHelper.getPartyName(delegator, emplPartyId, false));
			List<String> deptId = PartyUtil.getDepartmentOfEmployee(delegator, emplPartyId, UtilDateTime.nowTimestamp(), UtilDateTime.nowTimestamp());
			
			bodyParameters.put("emplDept", CommonUtil.convertListValueMemberToListDesMember(delegator, "PartyGroup", deptId, "partyId", "groupName"));
			bodyParameters.put("fromDate", fromDate);
			bodyParameters.put("thruDate", thruDate);
			bodyParameters.put("emplPosition", emplPositionStr);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("contactMechTypeId", EntityOperator.EQUALS, "POSTAL_ADDRESS"));
			conditions.add(EntityCondition.makeCondition("contactMechPurposeTypeId", EntityOperator.EQUALS, "PRIMARY_LOCATION"));
			conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, companyId));
			GenericValue companyContactMech = EntityUtil.getFirst(delegator.findList("PartyContactMechPurposeView", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, false));
			String companyAddressDetails = "";
			if(UtilValidate.isNotEmpty(companyContactMech)){
				GenericValue companyPostallAddr = delegator.findOne("PostalAddress", UtilMisc.toMap("contactMechId", companyContactMech.getString("contactMechId")), false);
				//List<GenericValue> companyAddr = d			
				String companyCountry = delegator.findOne("Geo", UtilMisc.toMap("geoId", companyPostallAddr.getString("countryGeoId")), false).getString("geoName");	
				String companyStateProvince = delegator.findOne("Geo", UtilMisc.toMap("geoId", companyPostallAddr.getString("stateProvinceGeoId")), false).getString("geoName");
				
				GenericValue companyDistrictGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", companyPostallAddr.getString("districtGeoId")), false);
				String companyDistrictGeoId = "";
				if(UtilValidate.isNotEmpty(companyDistrictGeo)){
					companyDistrictGeoId = companyDistrictGeo.getString("geoName"); 
				}
				GenericValue companyWardGeo = delegator.findOne("Geo", UtilMisc.toMap("geoId", companyPostallAddr.getString("wardGeoId")), false);
				String companyWardGeoId = "";
				if(UtilValidate.isNotEmpty(companyWardGeo)){
					companyWardGeoId = companyWardGeo.getString("geoName");
				}
				companyAddressDetails = companyPostallAddr.getString("address1") + ", " + companyWardGeoId + ", " + companyDistrictGeoId + ", " + companyStateProvince + ", " + companyCountry;
			}else{
				companyAddressDetails = UtilProperties.getMessage("HrCommonUiLabels", "AddressNotExists", locale);
			}
			
			conditions.clear();
			conditions.add(EntityUtil.getFilterByDateExpr());
			conditions.add(EntityCondition.makeCondition("partyId", companyId));
			List<GenericValue> partyTelecomNbr = delegator.findList("PartyAndTelecomNumber", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, UtilMisc.toList("-fromDate"), null, false);
			
			List<EntityCondition> incomeConditions = FastList.newInstance();
			incomeConditions.add(EntityCondition.makeCondition("invoiceId", invoiceId));
			incomeConditions.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, UtilMisc.toList("PAYROL_EARN_HOURS")));
			List<GenericValue> payrollIncomes = delegator.findList("InvoiceItemAndType", EntityCondition.makeCondition(incomeConditions, EntityOperator.AND),null, null, null, false);
			
			List<EntityCondition> deductionConditions = FastList.newInstance();
			deductionConditions.add(EntityCondition.makeCondition("invoiceId", invoiceId));
			deductionConditions.add(EntityCondition.makeCondition("parentTypeId", EntityOperator.IN, UtilMisc.toList("PAYROL_DD_FROM_GROSS", "PAYROL_TAXES")));
			List<GenericValue> payrollDeduction = delegator.findList("InvoiceItemAndType", EntityCondition.makeCondition(deductionConditions, EntityOperator.AND),null, null, null, false);
			
			
			bodyParameters.put("payrollIncomes", payrollIncomes);
			//bodyParameters.put("dateJoin", dept.getTimestamp("fromDate"));
			bodyParameters.put("payrollDeductions", payrollDeduction);
			bodyParameters.put("uomId", invoiceGv.getString("currencyUomId"));
			bodyParameters.put("companyAddress", companyAddressDetails);
			bodyParameters.put("phoneNumber", partyTelecomNbr);
			bodyParameters.put("currencyUomId", invoiceGv.get("currencyUomId"));
			bodyParameters.put("actualReceipt", InvoiceWorker.getInvoiceTotal(delegator,invoiceId).multiply(InvoiceWorker.getInvoiceCurrencyConversionRate(delegator,invoiceId)));
			emailCtx.put("userLogin", context.get("userLogin"));
			emailCtx.put("locale", context.get("locale"));
			emailCtx.put("sendTo", emailAddress.get("emailAddress"));//emailAddress.get("emailAddress")
			emailCtx.put("partyIdTo", emplPartyId);
			emailCtx.put("bodyParameters", bodyParameters);
			emailCtx.put("authUser", email);
			emailCtx.put("authPass", password);
			emailCtx.put("sendFrom", email);
			//emailCtx.put("subject", subject);
			emailCtx.put("emailTemplateSettingId", "PARTY_PAYROLL_NOTIFY");
		    Map<String, Object> results = dispatcher.runSync("sendMailFromTemplateSetting", emailCtx);
		    if(ServiceUtil.isError(results)){
		    	ntfCtx.put("partyId", userLogin.getString("partyId"));
		    	ntfCtx.put("header", "Xảy ra lỗi khi gửi email phiếu lương đến " + PartyHelper.getPartyName(delegator, emplPartyId, false));
		    	dispatcher.runSync("createNotification", ntfCtx);
		    }
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}	
    	return ServiceUtil.returnSuccess();
    }
    
    public static Map<String, Object> createPartyInvoiceItemSalary(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	String partyId = (String)context.get("partyId");
    	String customTimePeriodId = (String)context.get("customTimePeriodId");
    	//GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String currencyUomId = (String)context.get("currencyUomId");
		if(currencyUomId == null){
			currencyUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
		}
    	try {
    		List<EntityCondition> conds = FastList.newInstance();
    		conds.add(EntityCondition.makeCondition("partyIdTo", partyId));
    		conds.add(EntityCondition.makeCondition("customTimePeriodId", customTimePeriodId));
    		List<GenericValue> paySalaryHistory = delegator.findList("PaySalaryHistory",EntityCondition.makeCondition(
    						EntityCondition.makeCondition("invoiceId", null), EntityJoinOperator.AND, 
    						EntityCondition.makeCondition(conds)), null, null, null, false);
    		conds.add(EntityCondition.makeCondition("invoiceItemTypeId", EntityJoinOperator.NOT_EQUAL, null));
			List<GenericValue> paySalaryItemHistoryPartyList = delegator.findList("PaySalaryItemHistoryAndFormula", EntityCondition.makeCondition(conds), null, UtilMisc.toList("partyIdFrom"), null, false);
			GenericValue systemUserLogin = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
			for(GenericValue partyFrom: paySalaryHistory){
				String partyIdFrom = partyFrom.getString("partyIdFrom");
				Map<String, Object> invoiceMap = FastMap.newInstance();
				invoiceMap.put("partyId", partyIdFrom);
				invoiceMap.put("statusId","INVOICE_IN_PROCESS");
				invoiceMap.put("currencyUomId", currencyUomId);
				invoiceMap.put("partyIdFrom", partyId);
				invoiceMap.put("invoiceTypeId", "PAYROL_INVOICE");
				invoiceMap.put("userLogin", systemUserLogin);
				Map<String, Object> resultService = dispatcher.runSync("createInvoice", invoiceMap);
				if(ServiceUtil.isSuccess(resultService)){
					String invoiceId = (String)resultService.get("invoiceId");
					partyFrom.set("invoiceId", invoiceId);
					partyFrom.set("statusId", "PAYR_INVOICE_CREATED");
					partyFrom.store();
					List<GenericValue> tempPaySalaryItemHistoryPartyList = EntityUtil.filterByCondition(paySalaryItemHistoryPartyList, EntityCondition.makeCondition("partyIdFrom", partyIdFrom));
					for(GenericValue paySalaryItem: tempPaySalaryItemHistoryPartyList){
						Map<String, Object> invoiceItemMap = FastMap.newInstance();
						invoiceItemMap.put("invoiceId", invoiceId);
						invoiceItemMap.put("userLogin", systemUserLogin);
						BigDecimal amount = paySalaryItem.getBigDecimal("amount");
						String invoiceItemTypeId = paySalaryItem.getString("invoiceItemTypeId");
						invoiceItemMap.put("invoiceItemTypeId", invoiceItemTypeId);
						//ERNS AND HOURS
						boolean isErnsAndHours = true;
						isErnsAndHours = ReportUtils.checkInvoiceItemType(PropertiesUtil.INVOICE_ITEM_TYPE_PAYROL_EARN_HOURS, invoiceItemTypeId, delegator);
						if(!isErnsAndHours){
							amount = amount.negate();
						}
						invoiceItemMap.put("amount", amount);
						invoiceItemMap.put("quantity", BigDecimal.ONE);
						invoiceItemMap.put("description", paySalaryItem.getString("name"));
						resultService = dispatcher.runSync("createInvoiceItem", invoiceItemMap);						
					}
				}else{
					return ServiceUtil.returnError((String)resultService.get(ModelService.ERROR_MESSAGE));
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return ServiceUtil.returnSuccess();
    }
    public static Map<String, Object> createPayrollTablePartyInvoice(DispatchContext dctx, Map<String, Object> context){
    	Delegator delegator = dctx.getDelegator();
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	String partyId = (String)context.get("partyId");
    	String payrollTableId = (String)context.get("payrollTableId");
    	Locale locale = (Locale)context.get("locale");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Map<String, Object> mapConds = UtilMisc.toMap("partyId", partyId, "payrollTableId", payrollTableId);
    	try {
    		GenericValue payrollTableRecord = delegator.findOne("PayrollTableRecord", UtilMisc.toMap("payrollTableId", payrollTableId), false);
    		if(payrollTableRecord == null){
				return ServiceUtil.returnError("Cannot find payroll table ");
			}
			GenericValue payrollTableRecordParty = delegator.findOne("PayrollTableRecordParty", mapConds, false);
			if(payrollTableRecordParty == null){
				return ServiceUtil.returnError("Cannot find payroll table of employee");
			}
			String statusId = payrollTableRecordParty.getString("statusId");
			if("PYRLL_TABLE_INVOICED".equals(statusId)){
				return ServiceUtil.returnError(UtilProperties.getMessage("PayrollUiLabels", "EmployeeIsCreatedInvocie", locale));
			}
			List<GenericValue> payrollTableAmountList = delegator.findByAnd("PayrollTableRecordPartyAmount", mapConds, null, false);
			String orgId = payrollTableRecord.getString("orgId");
			String currencyUomId = EntityUtilProperties.getPropertyValue("general.properties", "currency.uom.id.default", "VND", delegator);
			Timestamp fromDate = payrollTableRecord.getTimestamp("fromDate");
			Timestamp thruDate = payrollTableRecord.getTimestamp("thruDate");
			String partyGroupId = payrollTableRecordParty.getString("partyGroupId");
			if(partyGroupId == null){
				List<String> departmentOfEmpl = PartyUtil.getDepartmentOfEmployee(delegator, partyId, fromDate, thruDate);
				if(UtilValidate.isNotEmpty(departmentOfEmpl)){
					partyGroupId = departmentOfEmpl.get(0);
					payrollTableRecordParty.set("partyGroupId", partyGroupId);
				}else{
					return ServiceUtil.returnError("Cannot find department of employee to create invoice");
				}
			}
			//create invoice
			Map<String, Object> invoiceMap = FastMap.newInstance();
			invoiceMap.put("partyId", orgId);
			invoiceMap.put("statusId", "INVOICE_IN_PROCESS");
			invoiceMap.put("currencyUomId", currencyUomId);
			invoiceMap.put("partyIdFrom", partyId);
			invoiceMap.put("invoiceTypeId", "PAYROL_INVOICE");
			invoiceMap.put("userLogin", userLogin);
			Map<String, Object> resultService = dispatcher.runSync("createInvoice", invoiceMap);
			if(!ServiceUtil.isSuccess(resultService)){
				return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
			}
			String invoiceId = (String)resultService.get("invoiceId");
			payrollTableRecordParty.set("invoiceId", invoiceId);
			payrollTableRecordParty.set("statusId", "PYRLL_TABLE_INVOICED");
			//create invoice item for employee
			Map<String, Object> invoiceItemMap = FastMap.newInstance();
			invoiceItemMap.put("invoiceId", invoiceId);
			invoiceItemMap.put("userLogin", userLogin);
			invoiceItemMap.put("locale", locale);
			invoiceItemMap.put("quantity", BigDecimal.ONE);
			for(GenericValue payrollTableAmount: payrollTableAmountList){
				String code = payrollTableAmount.getString("code");
				String invoiceItemTypeId = PayrollWorker.getInvoiceItemTypeByPartyAndCode(delegator, code, partyGroupId, fromDate, thruDate, userLogin.getString("userLoginId"));
				if(invoiceItemTypeId != null){
					GenericValue formula = delegator.findOne("PayrollFormula", UtilMisc.toMap("code", code), false);
					BigDecimal amount = payrollTableAmount.getBigDecimal("amount");
					boolean isErnsAndHours = ReportUtils.checkInvoiceItemType(PropertiesUtil.INVOICE_ITEM_TYPE_PAYROL_EARN_HOURS, invoiceItemTypeId, delegator);;
					if(!isErnsAndHours){
						amount = amount.negate();
					}
					invoiceItemMap.put("invoiceItemTypeId", invoiceItemTypeId);
					invoiceItemMap.put("amount", amount);
					invoiceItemMap.put("description", formula.getString("name"));
					resultService = dispatcher.runSync("createInvoiceItem", invoiceItemMap);
					if(!ServiceUtil.isSuccess(resultService)){
						return ServiceUtil.returnError(CommonUtil.getErrorMessageFromService(resultService));
					}
					payrollTableAmount.set("invoiceItemTypeId", invoiceItemTypeId);
					payrollTableAmount.store();
				}
			}
			payrollTableRecordParty.store();
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		} catch (GenericServiceException e) {
			e.printStackTrace();
			return ServiceUtil.returnError(e.getLocalizedMessage());
		}
    	return ServiceUtil.returnSuccess();
    }
}
