package com.olbius.basehr.util;

import java.util.HashSet;
import java.util.List;

public class SetUtil {
	// remove duplicate data in list
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<?> removeDuplicateElementInList(List<?> listDatas){
		if(!listDatas.isEmpty()){
			HashSet hs = new HashSet();
			hs.addAll(listDatas);
			listDatas.clear();
			listDatas.addAll(hs);
		}
		return listDatas;
	}
}
