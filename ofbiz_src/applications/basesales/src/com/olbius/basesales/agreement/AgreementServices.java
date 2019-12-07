package com.olbius.basesales.agreement;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;

import com.olbius.basehr.util.MultiOrganizationUtil;
import com.olbius.basehr.util.PartyHelper;
import com.olbius.basesales.util.CRMUtils;

import javolution.util.FastList;
import javolution.util.FastMap;

public class AgreementServices {
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> listAgreementsOfPartner(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Locale locale = (Locale) context.get("locale");
		List<EntityCondition> conditions = FastList.newInstance();
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts = (EntityFindOptions) context.get("opts");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		
		String organizationId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));
		String partyIdFrom = (String) parameters.get("partyIdFrom")[0];
		if (UtilValidate.isEmpty(partyIdFrom)) {
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("agreementTypeId", "SALES_AGREEMENT", "partyIdTo", organizationId)));
		} else {
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdFrom", partyIdFrom, "agreementTypeId", "SALES_AGREEMENT", "partyIdTo", organizationId)));
		}
		String agreementId = "";
		if (parameters.containsKey("agreementId")) {
			agreementId = (String) parameters.get("agreementId")[0];
			if (UtilValidate.isNotEmpty(agreementId)) {
				conditions.add(EntityCondition.makeCondition("agreementId", EntityJoinOperator.EQUALS, agreementId));
			}
		}
		conditions.add(EntityCondition.makeCondition(CRMUtils.makeCondition(context, delegator, true)));
		EntityListIterator dummyAgreement = null;
		int pagesize = Integer.parseInt(parameters.get("pagesize")[0]);
	    int pageNum = Integer.parseInt(parameters.get("pagenum")[0]);
	    int start = pageNum * pagesize + 1;
	    String TotalRows = "0";
	    List<Map<String, Object>> listAgreementPartner = FastList.newInstance();
	    
	    listSortFields.add("-fromDate");
		try {
			conditions = ConditionSortUtil.processAgreementListCondition(conditions);
			dummyAgreement = delegator.find("AgreementAndPartyFromDetail", EntityCondition.makeCondition(conditions), null, null, listSortFields, opts);
			List<GenericValue> listAgreement = dummyAgreement.getPartialList(start, pagesize);
			for (GenericValue x : listAgreement) {
				Map<String, Object> mapAgreement = FastMap.newInstance();
				mapAgreement.putAll(x);
				mapAgreement.put("partyIdFrom", x.getString("partyIdFrom"));
				mapAgreement.put("partyFrom", PartyHelper.getPartyName(delegator, x.getString("partyIdFrom"), true, true));
				
				if (parameters.containsKey("ia") && parameters.get("ia").length > 0) {
	    			String ia = parameters.get("ia")[0];
	    			if ("Y".equals(ia)) {
	    				String partyRepresentId = getPartyRepresentative(x.getString("partyIdFrom"), delegator);
	    				if (UtilValidate.isNotEmpty(partyRepresentId)) {
	    					mapAgreement.put("representativeMember", PartyHelper.getPartyName(delegator, partyRepresentId, true, true));
	    				}
	    			}
				}
				
				mapAgreement.put("statusId", x.getString("statusId"));
				String remainDays = "0";
				if (UtilValidate.isNotEmpty(x.getTimestamp("thruDate"))) {
					Calendar currentDate = Calendar.getInstance();
					Calendar thruDate = Calendar.getInstance();
					thruDate.setTimeInMillis(x.getTimestamp("thruDate").getTime());
					remainDays = getRemainDays(currentDate, thruDate);
				}
				mapAgreement.put("remainDays", new BigDecimal(remainDays));
				
				agreementId = x.getString("agreementId");
				String deliverDateFrequen = "";
				int months = 0;
				int grandAgreement = 0;
				List<GenericValue> listAgreementTerm = delegator.findList("AgreementTerm",
						EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, UtilMisc.toList("createdStamp"), null, false);
				for (GenericValue z : listAgreementTerm) {
					String termTypeId = z.getString("termTypeId");
					String textValue = "";
					textValue = z.getString("textValue");
					switch (termTypeId) {
					case "SHIPPING_ADDRESS":
						
						break;
					case "USE_PACKAGE_NETDAYS":
						try {
							months = Integer.parseInt(textValue);
						} catch (NumberFormatException e) {
							if (UtilValidate.isNotEmpty(textValue)) {
								try {
									if (textValue.split("\\.").length > 1) {
										months = Integer.parseInt(textValue.split("\\.")[0]);
									}
								} catch (Exception e2) {
									months = 0;
								}
							}
						}
						break;
					case "FIN_PAY_TOTAL_MIN":
						try {
							grandAgreement = Integer.parseInt(textValue);
						} catch (NumberFormatException e) {
							if (UtilValidate.isNotEmpty(textValue)) {
								try {
									if (textValue.split("\\.").length > 1) {
										grandAgreement = Integer.parseInt(textValue.split("\\.")[0]);
									}
								} catch (Exception e2) {
									grandAgreement = 0;
								}
							}
						}
						if (UtilValidate.isEmpty(textValue)) {
							mapAgreement.put("agrementValue", BigDecimal.ZERO);
						} else {
							mapAgreement.put("agrementValue", new BigDecimal(textValue));
						}
						break;
					case "FIN_PAYMENT_METHOD":
						if (UtilValidate.isNotEmpty(textValue)) {
							GenericValue paymentMethodType = delegator.findOne("PaymentMethodType",
									UtilMisc.toMap("paymentMethodTypeId", textValue), false);
							if (UtilValidate.isNotEmpty(paymentMethodType)) {
								mapAgreement.put("paymentMethod", paymentMethodType.get("description", locale));
							}
						}
						break;
					case "FIN_PAYMENT_FREQUEN":
						mapAgreement.put("paymentFrequen", textValue);
						break;
					case "DELIVER_DATE_FREQUEN":
						if (UtilValidate.isNotEmpty(textValue)) {
							if (textValue.matches("\\d+")) {
								// Ngay hang thang
								// deliverDateFrequen.replace(UtilProperties.getMessage("DmsUiLabels", "DmsMonthly", locale), "");
								deliverDateFrequen += UtilProperties.getMessage("DmsUiLabels", "DmsDay", locale) + " " + textValue + " "
										+ UtilProperties.getMessage("DmsUiLabels", "DmsMonthly", locale) + " "
										 + " " + UtilProperties.getMessage("DmsUiLabels", "DmsAnd", locale) + " ";
							} else {
								// Thu hang tuan
								// deliverDateFrequen.replace(UtilProperties.getMessage("DmsUiLabels", "DmsWeekly", locale), "");
								deliverDateFrequen +=  UtilProperties.getMessage("DmsUiLabels", textValue, locale) + " " +
										UtilProperties.getMessage("DmsUiLabels", "DmsWeekly", locale) + " "
										+ UtilProperties.getMessage("DmsUiLabels", "DmsAnd", locale) + " ";
							}
						}
						break;
					case "SOLICITATION_METHOD":
						break;
					default:
						break;
					}
				}
				if (deliverDateFrequen.length() > UtilProperties.getMessage("DmsUiLabels", "DmsAnd", locale).length() - 2) {
					deliverDateFrequen = deliverDateFrequen.substring(0, deliverDateFrequen.length() -
							UtilProperties.getMessage("DmsUiLabels", "DmsAnd", locale).length() - 2);
				}
				mapAgreement.put("deliverDateFrequen", deliverDateFrequen);
				mapAgreement.put("grandTotalOrder", getGrandTotalOrder(delegator, agreementId));
				mapAgreement.put("grandTotalAgreement", new BigDecimal(months*grandAgreement));
				mapAgreement.put("moneyRemain", new BigDecimal(months*grandAgreement - getGrandTotalOrder(delegator, agreementId).intValue()));
				listAgreementPartner.add(mapAgreement);
			}
			TotalRows = String.valueOf(dummyAgreement.getResultsTotalSize());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (dummyAgreement != null) {
				dummyAgreement.close();
			}
		}
		result.put("TotalRows", TotalRows);
		result.put("listIterator", listAgreementPartner);
		return result;
	}
	public static String getPartyRepresentative(String partyPartnerBusinessesId, Delegator delegator) throws GenericEntityException {
		String partyId = "";
		List<EntityCondition> listConditions = FastList.newInstance();
		listConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
		listConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", partyPartnerBusinessesId, "roleTypeIdFrom", "REPRESENTATIVE")));
		List<GenericValue> listPartyRepresentative = delegator.findList("PartyRelationship",
				EntityCondition.makeCondition(listConditions), null, null, null, false);
		if (UtilValidate.isNotEmpty(listPartyRepresentative)) {
			GenericValue partyRepresentative = EntityUtil.getFirst(listPartyRepresentative);
			partyId = partyRepresentative.getString("partyIdFrom");
		}
		return partyId;
	}
	private static String getRemainDays(Calendar startDate, Calendar endDate) {
		Calendar date = (Calendar) startDate.clone();
		long daysBetween = 0;
		while (date.before(endDate)) {
			date.add(Calendar.DAY_OF_MONTH, 1);
			daysBetween++;
		}
		return String.valueOf(daysBetween);
	}
	private static BigDecimal getGrandTotalOrder(Delegator delegator, String agreementId) throws GenericEntityException {
		BigDecimal grandTotalOrder = BigDecimal.ZERO;
		List<EntityCondition> conditions = FastList.newInstance();
		conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.NOT_EQUAL, "CANCELLED"));
		conditions.add(EntityCondition.makeCondition("agreementId", EntityJoinOperator.EQUALS, agreementId));
		List<GenericValue> listOrderHeader = delegator.findList("OrderHeader",
				EntityCondition.makeCondition(conditions), null, null, null, false);
		for (GenericValue x : listOrderHeader) {
			grandTotalOrder = grandTotalOrder.add(x.getBigDecimal("grandTotal"));
		}
		return grandTotalOrder;
	}
}
