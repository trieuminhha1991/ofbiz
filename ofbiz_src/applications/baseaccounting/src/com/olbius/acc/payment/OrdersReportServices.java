package com.olbius.acc.payment;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.ServiceUtil;


public class OrdersReportServices{
	public static final String module = OrdersReportServices.class.getName();
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListReceiptPayment(DispatchContext dpct,Map<String,Object> context){
		//Variables
		Delegator delegator = (Delegator) dpct.getDelegator();
		Map<String,Object> result = ServiceUtil.returnSuccess();
		List<GenericValue> listPayment = FastList.newInstance();
		List<Map<String,Object>> listIterator = FastList.newInstance();
		Locale locale =  (Locale) context.get("locale");
		try {
				String orderId = (String) context.get("orderId");
				GenericValue userLogin = (GenericValue) context.get("userLogin");
				Map<String,Object> mapRunSync = dpct.getDispatcher().runSync("getListPaymentMethodOrders", UtilMisc.toMap("userLogin", userLogin,"orderId",orderId));
				if(ServiceUtil.isSuccess(mapRunSync)){
					if(mapRunSync.containsKey("paymentMethods")){
						listPayment = (List<GenericValue>) mapRunSync.get("paymentMethods");
						if(UtilValidate.isNotEmpty(listPayment)){
							for(GenericValue payment : listPayment){
								Map<String,Object> mapTcx = FastMap.newInstance();
								if(payment.getString("paymentMethodTypeId").equals("COMPANY_CHECK")){
									GenericValue eftAccount = delegator.findOne("EftAccount",UtilMisc.toMap("paymentMethodId", payment.getString("paymentMethodId")), false); 
									GenericValue bankType = delegator.findOne("Enumeration",UtilMisc.toMap("enumId", payment.getString("partyPaymentTypeId")), false);
									if(eftAccount != null){
										mapTcx.put("bankName", eftAccount != null ? eftAccount.get("bankName", locale) : eftAccount.getString("paymentMethodId"));
									}else mapTcx.put("bankName", " ");
									mapTcx.put("bankOwner", eftAccount != null ? eftAccount.get("companyNameOnAccount",locale) + " " : " ");
									mapTcx.put("bankCode", eftAccount != null ? eftAccount.get("accountNumber",locale) + " " : " ");
									mapTcx.put("bankType", bankType != null ? bankType.get("description",locale) + " " : " ");
								}else if(payment.getString("paymentMethodTypeId").equals("CREDIT_CARD")){
									GenericValue creditCard = delegator.findOne("CreditCard",UtilMisc.toMap("paymentMethodId", payment.getString("paymentMethodId")), false);
									GenericValue creditType = delegator.findOne("Enumeration",UtilMisc.toMap("enumId", payment.getString("partyPaymentTypeId")), false);  
									mapTcx.put("bankName", creditCard.getString("titleOnCard").isEmpty() ? " " : creditCard.getString("titleOnCard") + " ");
									mapTcx.put("bankOwner", (creditCard != null ? creditCard.getString("companyNameOnCard") + "( " : " (") 
											+ (creditCard != null && creditCard.getString("lastNameOnCard") != null ? creditCard.getString("lastNameOnCard") + " " : "N/A ") + (creditCard != null && creditCard.getString("middleNameOnCard") != null ? creditCard.getString("middleNameOnCard") + " " : " ") 
											+ (creditCard != null && creditCard.getString("firstNameOnCard") != null ? creditCard.getString("firstNameOnCard") + " )" : "N/A" + ")")
									);
									mapTcx.put("bankCode", creditCard != null ? creditCard.get("cardNumber",locale) + " " : " ");
									mapTcx.put("bankType", creditType !=null ? creditType.get("description",locale) + " " : " ");
								}else if(payment.getString("paymentMethodTypeId").equals("CASH")){
									GenericValue partyName = delegator.findOne("PartyNameView",UtilMisc.toMap("partyId", payment.getString("partyId")), false);
									mapTcx.put("bankName", "N/A");
									mapTcx.put("bankOwner",( partyName != null && partyName.getString("groupName") != null ?  partyName.getString("groupName") + "( " : " (" ) + 
											(partyName != null && partyName.getString("lastName") != null ? partyName.getString("lastName") + " " : " " ) + 
											(partyName != null && partyName.getString("middleName") != null ? partyName.getString("middleName") + " " : " ") + 
											(partyName != null && partyName.getString("lastName") != null ? partyName.getString("firstName") + " )" : " )" )
									);
									mapTcx.put("bankCode","N/A");
									mapTcx.put("bankType", payment != null && payment.get("description",locale) != null ? payment.get("description",locale) : " ");
								}
								mapTcx.put(payment.getString("paymentMethodId") + "_amount", "");
								mapTcx.put(payment.getString("paymentMethodId") + "_amount", "");
								mapTcx.put(payment.getString("paymentMethodId") + "_reference", "");
								listIterator.add(mapTcx);
							}
						}
					}
					result.put("listIterator", listIterator);
				}else return ServiceUtil.returnError("An error when get list payment method orders");
		  	} catch (Exception e) {
		  		Debug.logError(e, module);
		  		return ServiceUtil.returnError("Error when call service getListReceiptPayment" + e.getMessage());
		  	}
		return result;
	 }
}
