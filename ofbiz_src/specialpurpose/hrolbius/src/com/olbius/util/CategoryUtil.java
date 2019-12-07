package com.olbius.util;

import java.util.List;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;

public class CategoryUtil{
	
	public static List<GenericValue> getCategoryChild(String categoryId, Delegator delegator){
		FastMap<String, Object> condition = FastMap.newInstance();
		condition.put("parentCategoryId", categoryId);
		List<GenericValue> childList = FastList.newInstance();
		try {
			childList =  delegator.findByAnd("DataCategory", condition, null, false);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		return childList;
	}
	
	public static List<GenericValue> getDataReSource(String categoryId, Delegator delegator){
		FastMap<String, Object> condition = FastMap.newInstance();
		condition.put("dataCategoryId", categoryId);
		List<GenericValue> dataResourceList = FastList.newInstance();
		try {
			dataResourceList =  delegator.findByAnd("DataResource", condition, null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			Debug.log(e.getMessage());
		}
		return dataResourceList;
	}
	
	public static boolean hasChild(String categoryId, Delegator delegator){
		if(UtilValidate.isEmpty(getCategoryChild(categoryId, delegator)) && UtilValidate.isEmpty(getDataReSource(categoryId, delegator))){
			return false;
		}else{
			return true;
		}
	}
}
