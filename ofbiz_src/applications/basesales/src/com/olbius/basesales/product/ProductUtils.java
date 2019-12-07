package com.olbius.basesales.product;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.ObjectType;
import org.ofbiz.base.util.UtilValidate;

public class ProductUtils {
	public static final String module = ProductUtils.class.getName();
	
	public static char calcUpcChecksum(String upc) {
        return calcChecksum(upc, 12);
    }
	public static char calcEanChecksum(String ean) {
		return calcChecksum(ean, 13);
	}
	public static char calcChecksum(String value, int length) {
        if (value != null && value.length() == length) {
            value = value.substring(0, length - 1);
        }
        if (value == null || value.length() != length - 1) {
            throw new IllegalArgumentException("Illegal size of value; must be either " + (length - 1) + " or " + length + " characters");
        }
        int oddsum = 0;
        int evensum = 0;
        for (int i = value.length() - 1; i >= 0; i--) {
            if ((value.length() - i) % 2 == 0) {
                evensum += Character.digit(value.charAt(i), 10);
            } else {
                oddsum += Character.digit(value.charAt(i), 10);
            }
        }
        int check = 10 - ((evensum + 3 * oddsum) % 10);
        if (check >= 10) check = 0;
        return Character.forDigit(check, 10);
    }
	public static String makeUpcId(String pluCode, String measureUomId, BigDecimal measureValue) {
		if (UtilValidate.isEmpty(measureUomId) || measureValue == null) {
			return null;
		}
		
		// idValue: left code
		String idValue = "2" + pluCode;
		if ("CURRENCY_MEASURE".equals(measureUomId)) {
			idValue += "1"; // RRRR is price
		} else {
			idValue += "0"; // RRRR is weight
		}
		
		// idValue: right code
		measureValue = measureValue.setScale(2, RoundingMode.HALF_UP);
		BigDecimal tmpValue = measureValue.multiply(new BigDecimal(100));
		tmpValue = tmpValue.setScale(0, RoundingMode.HALF_UP);
		String tmpValueStr = tmpValue.toString();
		if (tmpValueStr.length() == 3) tmpValueStr = "0" + tmpValueStr;
		idValue += tmpValueStr;
		
		// idValue: checksum
		char checksum = calcUpcChecksum(idValue);
		if (UtilValidate.isEmpty(checksum)) {
			return null;
		}
		idValue += checksum;
		
		return idValue;
	}
	public static String makeEanId(String pluCode, BigDecimal weightValue, String prefixCode, String patternCode, Integer decimalsInWeight) {
		if (weightValue == null || UtilValidate.isEmpty(prefixCode) || UtilValidate.isEmpty(patternCode)) {
			return null;
		}
		
		// idValue: left code
		StringBuilder idValue = new StringBuilder();
		if (prefixCode.length() != 2) {
			if (prefixCode.length() < 2) {
				StringBuilder tmpPrefix = new StringBuilder(prefixCode);
				int tmpScale = 2 - prefixCode.length();
				for (int i = 0; i < tmpScale; i++) {
					tmpPrefix.insert(0, '0');
				}
				prefixCode = tmpPrefix.toString();
			} else {
				prefixCode = prefixCode.substring(0, 2);
			}
		}
		idValue.append(prefixCode);
		int weightLength = 0;
		if ("IIIIIWWWWW".equals(patternCode)) {
			idValue.append(pluCode);
			weightLength = 5;
		}
		
		if (weightLength <= 0) {
			return null;
		}
		
		// idValue: right code
		//measureValue = measureValue.setScale(decimalsInWeight, RoundingMode.HALF_UP);
		BigDecimal tmpPow = new BigDecimal(Math.pow(10, decimalsInWeight));
		BigDecimal tmpValue = weightValue.multiply(tmpPow);
		tmpValue = tmpValue.setScale(0, RoundingMode.HALF_UP);
		StringBuilder tmpValueStr = new StringBuilder(tmpValue.toString());
		if (tmpValueStr.length() != weightLength) {
			if (tmpValueStr.length() < weightLength) {
				int tmpScale = weightLength - tmpValueStr.length();
				for (int i = 0; i < tmpScale; i++) {
					tmpValueStr.insert(0, '0');
				}
			} else {
				int tmpScale = weightLength - tmpValueStr.length();
				for (int i = 0; i < tmpScale; i++) {
					tmpValueStr.deleteCharAt(0);
				}
			}
		}
		idValue.append(tmpValueStr);
		
		// idValue: checksum
		char checksum = calcEanChecksum(idValue.toString());
		if (UtilValidate.isEmpty(checksum)) {
			return null;
		}
		idValue.append(checksum);
		
		return idValue.toString();
	}
	public static boolean isValidUpc(String upc) {
        if (upc == null || upc.length() != 12) {
        	return false;
            //throw new IllegalArgumentException("Invalid UPC length; must be 12 characters");
        }

        char csum = upc.charAt(11);
        char calcSum = calcUpcChecksum(upc);
        return csum == calcSum;
    }
	public static boolean isValidEan(String ean) {
		if (ean == null || ean.length() != 13) {
			return false;
			//throw new IllegalArgumentException("Invalid UPC length; must be 12 characters");
		}
		
		char csum = ean.charAt(12);
		char calcSum = calcEanChecksum(ean);
		return csum == calcSum;
	}
	public static String getPluCodeInUpcId(String upcId){
		String pluCode = null;
		boolean isUpc = isValidUpc(upcId);
		if (isUpc) {
			pluCode = upcId.substring(1, 6);
		}
		return pluCode;
	}
	public static String getPluCodeInEanId(String eanId){
		String pluCode = null;
		boolean isEan = isValidEan(eanId);
		if (isEan) {
			pluCode = eanId.substring(2, 7);
		}
		return pluCode;
	}
	public static BigDecimal getPriceInUpcId(String upcId, Locale locale){
		BigDecimal price = null;
		boolean isUpc = isValidUpc(upcId);
		if (isUpc) {
			String checkDigit = upcId.substring(6, 7);
			if ("0".equals(checkDigit)) {
				// RRRR is weight
				
			} else if ("1".equals(checkDigit)) {
				// RRRR is price
				String priceStr = upcId.substring(7, 11);
				try {
		            price = (BigDecimal) ObjectType.simpleTypeConvert(priceStr, "BigDecimal", null, locale);
		        } catch (Exception e) {
		            Debug.logWarning(e, "Problems parsing price string: " + priceStr, module);
		            price = null;
		        }
			}
		}
		if (price != null) {
			price = price.divide(new BigDecimal(100));
		}
		return price;
	}
	public static BigDecimal getWeightInUpcId(String upcId, Locale locale){
		BigDecimal weight = null;
		boolean isUpc = isValidUpc(upcId);
		if (isUpc) {
			String checkDigit = upcId.substring(6, 7);
			if ("0".equals(checkDigit)) {
				// RRRR is weight
				String weightStr = upcId.substring(7, 11);
				try {
		            weight = (BigDecimal) ObjectType.simpleTypeConvert(weightStr, "BigDecimal", null, locale);
		        } catch (Exception e) {
		            Debug.logWarning(e, "Problems parsing price string: " + weightStr, module);
		            weight = null;
		        }
			} else if ("1".equals(checkDigit)) {
				// RRRR is price
				
			}
		}
		if (weight != null) {
			weight = weight.divide(new BigDecimal(100));
		}
		return weight;
	}
	public static BigDecimal getWeightInEanId(String eanId, Locale locale, Integer decimalsInWeight){
		BigDecimal weight = null;
		boolean isEan = isValidEan(eanId);
		if (isEan) {
			String weightStr = eanId.substring(7, 12);
			try {
				weight = (BigDecimal) ObjectType.simpleTypeConvert(weightStr, "BigDecimal", null, locale);
			} catch (Exception e) {
				Debug.logWarning(e, "Problems parsing price string: " + weightStr, module);
				weight = null;
			}
		}
		if (weight != null) {
			if (decimalsInWeight == null) decimalsInWeight = 0;
			BigDecimal tmpPow = new BigDecimal(Math.pow(10, decimalsInWeight));
			weight = weight.divide(tmpPow);
		}
		return weight;
	}
}
