package com.olbius.acc.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EntityUtils {
	public static String getNextSeqId (String currentItemSeqId) {
		int padLength = currentItemSeqId.length();
		String pad = "";
		for(int i = 0; i < padLength; i++) {
			pad += "0";
		}
		char[] charArray = currentItemSeqId.toCharArray();
		int index = 0;
		for(int i = 0; i < charArray.length; i++) {
			if(charArray[i] != '0') {
				index = i;
				break;
			}
		}
		String seq = currentItemSeqId.substring(index);
		String nextSeq = (Long.parseLong(seq) + 1)+ ""; 
		return pad.substring(0, padLength - nextSeq.length()) + nextSeq;
	}
	
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> convertEntityToMap(Object obj) throws Exception {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Class> listClass = getClass(obj);
		List<Field> listField = new ArrayList<Field>();
		for(Class item: listClass) {
			listField.addAll(castToList(item.getDeclaredFields()));
		}
		for(Field item: listField) {
			Method[] methodGet = obj.getClass().getMethods();
			for(Method method : methodGet) {
				if(method.getName().toLowerCase().contains("get" + item.getName().toLowerCase())) {
					result.put(item.getName(), method.invoke(obj));
				}
			}
		}
		return result;
	}
	
	@SuppressWarnings("rawtypes")
	private static List<Class> getClass(Object obj) throws InstantiationException, IllegalAccessException{
		List<Class> listClass = new ArrayList<Class>();
		listClass.add(obj.getClass());
		if(!obj.getClass().getName().equals("java.lang.Object")) {
			listClass.addAll(getClass(obj.getClass().getSuperclass().newInstance()));
		}
		return listClass;
	}
	
	private static List<Field> castToList(Field[] arrs) {
		List<Field> listField = new ArrayList<Field>();
		for(Field item : arrs) {
			listField.add(item);
		}
		return listField;
	}
}
