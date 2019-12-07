package com.olbius.accounting.utils;

import java.util.List;
import javolution.util.FastList;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.DispatchContext;

public class AccountingPaymentUtilss{
		public static final String module = AccountingPaymentUtilss.class.getName();
		
		/* check payment parent type AP or AR
		 * 
		 * @param delegator
		 * @param paymentId
		 * @return type of Payment(AP,AR)
		 * 
		 * */
		public static String checkPaymentType(Delegator delegator,String paymentId){
			List<String> listParentTypeAP = FastList.newInstance();
			List<String> listParentTypeAR = FastList.newInstance();
			String typeOfPayment = "";
			try {
				//init list parent Type is AP
				listParentTypeAP.add("DISBURSEMENT");
				listParentTypeAP.add("TAX_PAYMENT");
				//init list parent Type is AR
				listParentTypeAR.add("RECEIPT");
				if(UtilValidate.isNotEmpty(paymentId)){
					GenericValue Payment = delegator.findOne("Payment", false, UtilMisc.toMap("paymentId", paymentId));
					if(UtilValidate.isNotEmpty(Payment) && UtilValidate.isNotEmpty(Payment.getString("paymentTypeId"))){
						GenericValue PaymentType = delegator.findOne("PaymentType", false, UtilMisc.toMap("paymentTypeId", Payment.getString("paymentTypeId")));
						if(UtilValidate.isNotEmpty(PaymentType)){
							String parentTypeId = (PaymentType.getString("parentTypeId") != null) ? PaymentType.getString("parentTypeId") : "";
							typeOfPayment = ((listParentTypeAP.contains(parentTypeId)) ?  "PAYMENT_IS_AP" : ((listParentTypeAR.contains(parentTypeId)) ? "PAYMENT_IS_AR" : "OTHER"));
						}
					}
				}
				
			} catch (Exception e) {
				String erMsg = "Fatal error call method checkPaymentType cause : " + e.getMessage();
				Debug.log(e,erMsg,module);
			}
			return typeOfPayment;
		}
	
}