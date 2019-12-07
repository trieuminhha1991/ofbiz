package com.olbius.util;

import java.util.HashSet;
import java.util.List;

public class SetUtil {
	// remove duplicate roles
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
