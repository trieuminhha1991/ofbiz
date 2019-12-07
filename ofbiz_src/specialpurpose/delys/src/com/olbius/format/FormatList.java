package com.olbius.format;

import java.util.ArrayList;
import java.util.List;

import org.ofbiz.entity.GenericValue;

public class FormatList {
	
	public static List<String> formatListGenericValueToListString (List<GenericValue> listGenericValue, String fieldSelect) {
		List<String> returnValue = new ArrayList<String>();
		for (GenericValue item : listGenericValue) {
			if (item.get(fieldSelect) != null) {
				String itemValue = item.get(fieldSelect).toString();
				returnValue.add(itemValue);
			}
		}
		return returnValue;
	}
}
