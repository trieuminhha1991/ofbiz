package com.olbius.salesmtl.util;


import org.ofbiz.base.util.Debug;

public class CurrencyUtil {
	enum Currency{
		VND, USD, EUR;
	}
	
	public static String  formatCurrency(String num, String uom){
		String[] numSeq = null;
		String result = "";
		switch (Currency.valueOf(uom)) {
		case VND:
			num = num.replaceAll(",", "");
			numSeq = num.split("\\.");
			if (numSeq.length > 2) {
				try {
					throw new Exception("Illegal Number");
				} catch (Exception e) {
					Debug.log(e.getStackTrace().toString());
				}
			}else if (numSeq.length == 2 ){
				StringBuilder strTmp = new StringBuilder(numSeq[0]);
				strTmp.reverse();
				strTmp.insert(3, '.');
				if(strTmp.charAt(strTmp.length()-1) == '.'){
					strTmp.deleteCharAt(strTmp.length()-1);
				}
				strTmp.reverse();
				result =  strTmp.toString() + "," + numSeq[1].substring(0, 1) + " VNĐ";
			}else{
				StringBuilder strTmp = new StringBuilder(numSeq[0]);
				strTmp.reverse();
				strTmp.insert(3, '.');
				if(strTmp.charAt(strTmp.length()-1) == '.'){
					strTmp.deleteCharAt(strTmp.length()-1);
				}
				strTmp.reverse();
				result = strTmp.toString() + " VNĐ";
			}
			break;
		case USD:
			num = num.replaceAll(",", "");
			numSeq = num.split("\\.");
			if (numSeq.length > 2) {
				try {
					throw new Exception("Illegal Number");
				} catch (Exception e) {
					Debug.log(e.getStackTrace().toString());
				}
			}else if (numSeq.length == 2 ){
				StringBuilder strTmp = new StringBuilder(numSeq[0]);
				strTmp.reverse();
				strTmp.insert(3, ',');
				if(strTmp.charAt(strTmp.length()-1) == '.'){
					strTmp.deleteCharAt(strTmp.length()-1);
				}
				strTmp.reverse();
				result = strTmp.toString() + "." + numSeq[1].substring(0, 1) + " USD";
			}else{
				StringBuilder strTmp = new StringBuilder(numSeq[0]);
				strTmp.reverse();
				strTmp.insert(3, ',');
				if(strTmp.charAt(strTmp.length()-1) == ','){
					strTmp.deleteCharAt(strTmp.length()-1);
				}
				strTmp.reverse();
				result = strTmp.toString() + " USD";
			}
			break;
		default:
			break;
		}
		return result;
	}
}
