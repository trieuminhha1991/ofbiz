package com.olbius.acc.payment;

public class PaymentListFactory implements PaymentType{
	public static PaymentList getPaymentList(String paymentType) {
		switch (paymentType) {
		case AR:
			return new ArPaymentList();
		case AP:
			return new ApPaymentList();
		default:
			return new ArPaymentList();
		}
	}
}
