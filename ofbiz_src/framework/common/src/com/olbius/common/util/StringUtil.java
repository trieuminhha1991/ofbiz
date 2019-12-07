package com.olbius.common.util;

import javolution.util.FastMap;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.ofbiz.base.util.UtilNumber;
import org.ofbiz.base.util.UtilValidate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class StringUtil {
	private static int decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
	private static int rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
	public static String ConvertDecimalToString(BigDecimal number) {
		number = number.setScale(decimals, rounding);
		if (number.compareTo(BigDecimal.ZERO) == 0){
			return "Không đồng";
		}
		String input = number.toString();
		String[] tmp = input.split(Pattern.quote("."));
		String s = "";
		String d = "";
		if (tmp.length > 1){
			s = tmp[0];
			d = tmp[1];
		} else {
			s = tmp[0];
		}
		String dv = "";
		int v = new Integer(d);
		if (d.length() > 0 && v > 0){
			int z = d.length();
			while (z > 0) {
                int x = new Integer(d.substring(z - 1, z));
                if (x == 0){
                	d = d.substring(0, z - 1);
                } else {
                	z = 0;
                }
                z --;
            }
			int i = d.length();
			int max = d.length();
			String dStr = d;
			int rank = 0;
            while (i > 0) {
                int x = new Integer(d.substring(0, i));
                if (x > 0){
                	if (i < max-1){
                		dStr = d.substring(i, max - 1);
                	} else {
                		dStr = String.valueOf(x);
                		rank = max - dStr.length(); 
                	}
                	i = 0;
                }
                i --;
            }
            if (rank > 0){
            	String k = "";
            	for (int t = 0; t < rank; t ++){
            		k = k + " không";
            	}
            	dv = "" + " phảy " + k + " " + readNumber(dStr, true) + " đồng";
            } else {
            	dv = "" + " phảy " + readNumber(dStr, true) + " đồng";
            }
		}
		
        String str = readNumber(s, false);
        if (number.compareTo(BigDecimal.ZERO) < 0){
        	str = "Âm " + str;
        }
        if (!"".equals(dv)){
        	str = str + dv;
        } else {
        	str = str + " đồng chẵn";
        }
        
        if (str.toCharArray()[0] == ' '){
        	Character upper = Character.toUpperCase(str.toCharArray()[1]);
            StringBuilder test = new StringBuilder(str);
            test.setCharAt(1, upper);
            str = test.toString();
        } else {
        	Character upper = Character.toUpperCase(str.toCharArray()[0]);
            StringBuilder test = new StringBuilder(str);
            test.setCharAt(0, upper);
            str = test.toString();
        }
        return str;
    }
		
	public static String readNumber (String s, Boolean odd){
		List<String> so = new ArrayList<String>();
		so.add("không");
		so.add("một");
		so.add("hai");
		so.add("ba");
		so.add("bốn");
		so.add("năm");
		so.add("sáu");
		so.add("bảy");
		so.add("tám");
		so.add("chín");
		List<String> hang = new ArrayList<String>(); 
		hang.add("");
		hang.add("nghìn");
		hang.add("triệu");
		hang.add("tỷ");
		int i, j, donvi, chuc, tram;
        String str = " ";
        i = s.length();
        if (i == 0) {
            str = so.get(0) + str;
        } else {
            j = 0;
            while (i > 0) {
                donvi = new Integer(s.substring(i - 1 , i));
                i--;
                if (i > 0)
                    chuc = new Integer(s.substring(i - 1 , i));
                else
                    chuc = -1;
                i--;
                if (i > 0)
                    tram = new Integer(s.substring(i - 1 , i));
                else
                    tram = -1;
                i--;
                if ((donvi > 0) || (chuc > 0) || (tram > 0) || (j == 3))
            		str = hang.get(j) + str;
                j++;
                if (j > 3) j = 1;
                if ((donvi == 1) && (chuc > 1))
                    str = "mốt " + str;
                else {
                    if ((donvi == 5) && (chuc > 0))
                        str = "lăm " + str;
                    else if (donvi > 0)
                        str = so.get(donvi) + " " + str;
                }
                if (chuc < 0)
                    break;
                else {
                    if ((chuc == 0) && (donvi > 0)) {
                    	if (odd){
                    		str = "không " + str;
                    	} else {
                    		str = "lẻ " + str;
                    	}
                    }
                    if (chuc == 1) str = "mười " + str;
                    if (chuc > 1) str = so.get(chuc) + " mươi " + str;
                }
                if (tram < 0) break;
                else {
            		if ((tram > 0) || (chuc > 0) || (donvi > 0)) str = so.get(tram) + " trăm " + str;
                }
                str = " " + str;
            }
        }
        return str;
	}
	
	public static String changeToWords(String numb, Boolean isCurrency) {
		String val = "", wholeNo = numb, points = "", andStr = "", pointStr="";
		String endStr = (isCurrency) ? ("only") : ("");
		int decimalPlace = numb.indexOf(".");
		if (decimalPlace > 0) {
			wholeNo = numb.substring(0, decimalPlace);
			points = numb.substring(decimalPlace + 1);
			if (new Integer(points) > 0) {
				andStr = (isCurrency)?("and"):("point");// just to separate whole numbers from points/cents
				endStr = (isCurrency) ? ("Cents "+endStr) : ("");
				pointStr = translateCents(points);
			}
		}
		val = translateWholeNumber(wholeNo).trim() + " " + andStr + pointStr + " " + endStr;
		if (val.toCharArray()[0] == ' '){
        	Character upper = Character.toUpperCase(val.toCharArray()[1]);
            StringBuilder test = new StringBuilder(val);
            test.setCharAt(1, upper);
            val = test.toString();
        } else {
        	Character upper = Character.toUpperCase(val.toCharArray()[0]);
            StringBuilder test = new StringBuilder(val);
            test.setCharAt(0, upper);
            val = test.toString();
        }
		return val;
	}
	
	private static String translateWholeNumber(String number) {
		String word = "";
		Boolean beginsZero = false;//tests for 0XX
		Boolean isDone = false;//test if already translated
		double dblAmt = (new Double(number));
		//if ((dblAmt > 0) && number.StartsWith("0"))
		if (dblAmt > 0) {//test for zero or digit zero in a nuemric
			beginsZero = number.startsWith("0");
			int numDigits = number.length();
			int pos = 0;//store digit grouping
			String place = "";//digit grouping name:hundres,thousand,etc...
			switch (numDigits){
			case 1://ones' range
				word = ones(number);
				isDone = true;
				break;
			case 2://tens' range
				word = tens(number);
				isDone = true;
				break;
			case 3://hundreds' range
				pos = (numDigits % 3) + 1;
				place = " hundred ";
				break;
			case 4://thousands' range
			case 5:
			case 6:
				pos = (numDigits % 4) + 1;
				place = " thousand ";
				break;
			case 7://millions' range
			case 8:
			case 9:
				pos = (numDigits % 7) + 1;
				place = " million ";
				break;
			case 10://Billions's range
				pos = (numDigits % 10) + 1;
				place = " billion ";
				break;
				//add extra case options for anything above Billion...
			default:
				isDone = true;
				break;
			}
			if (!isDone) {//if transalation is not done, continue...(Recursion comes in now!!)
				word = translateWholeNumber(number.substring(0, pos)) + place + translateWholeNumber(number.substring(pos));
				//check for trailing zeros
				if (beginsZero) word = " and " + word.trim(); 
			}
			//ignore digit grouping names
			if (word.trim().equals(place.trim())) word = "";
		}
		return word.trim();
	}
		
	private static String tens(String digit) {
		int digt = new Integer(digit);
		String name = null;
		switch (digt) {
			case 10:
				name = "ten";
				break;
			case 11:
				name = "eleven";
				break;
			case 12:
				name = "twelve";
				break;
			case 13:
				name = "thirteen";
				break;
			case 14:
				name = "fourteen";
				break;
			case 15:
				name = "fifteen";
				break;
			case 16:
				name = "sixteen";
				break;
			case 17:
				name = "seventeen";
				break;
			case 18:
				name = "eighteen";
				break;
			case 19:
				name = "nineteen";
				break;
			case 20:
				name = "twenty";
				break;
			case 30:
				name = "thirty";
				break;
			case 40:
				name = "fourty";
				break;
			case 50:
				name = "fifty";
				break;
			case 60:
				name = "sixty";
				break;
			case 70:
				name = "seventy";
				break;
			case 80:
				name = "eighty";
				break;
			case 90:
				name = "ninety";
				break;
			default:
			if (digt > 0) {
				name = tens(digit.substring(0, 1) + "0") + " " + ones(digit.substring(1));
			}
			break;
		}
		return name;
	}
		
	private static String ones(String digit) {
		int digt = new Integer(digit);
		String name = "";
		switch (digt) {
			case 1:
				name = "one";
				break;
			case 2:
				name = "two";
				break;
			case 3:
				name = "three";
				break;
			case 4:
				name = "four";
				break;
			case 5:
				name = "five";
			break;
			case 6:
				name = "six";
				break;
			case 7:
				name = "seven";
				break;
			case 8:
				name = "eight";
			break;
			case 9:
				name = "nine";
				break;
		}
		return name;
	}
		
	private static String translateCents(String cents) {
		String cts = "", digit = "", engOne = "";
		for (int i = 0; i < cents.length(); i++) {
			digit = cents.substring(i, i);
			if (digit.equals("0")) {
				engOne = "zero";
			} else {
				engOne = ones(digit);
			}
			cts += " " + engOne;
		}
		return cts;
	}
	
	public static List<String> splitKeyProperty(String key) {
		List<String> returnValue = new ArrayList<String>();
		if (UtilValidate.isEmpty(key)) return returnValue;
		String[] listKey = key.split(";");
		if (listKey != null) {
			returnValue = Arrays.asList(listKey);
		}
		return returnValue;
	}
	
	public static List<Map<String, Object>> convertListJsonObjectToListMap(List<Object> listObjects, List<String> listKeys){
		List<Map<String, Object>> listMaps = new ArrayList<Map<String, Object>>();
		Boolean isJson = false;
		if (!listObjects.isEmpty()) {
			if (listObjects.get(0) instanceof String) {
				isJson = true;
			}
		}
		if (isJson) {
			String stringJson = "[" + (String) listObjects.get(0) + "]";
			JSONArray lists = JSONArray.fromObject(stringJson);
			for (int i = 0; i < lists.size(); i++) {
				Map<String, Object> mapItems = FastMap.newInstance();
				JSONObject item = lists.getJSONObject(i);
				for (String key : listKeys){
					if (item.containsKey(key)) {
						mapItems.put(key, item.getString(key));
					}
				}
				listMaps.add(mapItems);
			}
		}
		return listMaps;
	}
}