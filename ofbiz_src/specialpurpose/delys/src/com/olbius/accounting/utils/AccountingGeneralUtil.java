package com.olbius.accounting.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;

public class AccountingGeneralUtil {
	public static final String module = AccountingGeneralUtil.class.getName();
	public static List<Map<String, Object>> getAllGlAccountChildren(String parentTypeId, Delegator delegator)
			throws GenericEntityException {
		List<Map<String, Object>> listData = new ArrayList<Map<String, Object>>();
		List<GenericValue> listTmp = delegator.findList("GlAccount",
				EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, parentTypeId),
				UtilMisc.toSet("glAccountId", "parentGlAccountId", "description"), null, null, false);
		if (listTmp != null) {
			Map<String, Object> tmp = FastMap.newInstance();
			String itt = "";
			String par = "";
			String des = "";
			for (GenericValue genericValue : listTmp) {
				tmp = FastMap.newInstance();
				itt = genericValue.getString("glAccountId");
				par = genericValue.getString("parentGlAccountId");
				des = genericValue.getString("description");
				tmp.put("glAccountId", itt);
				tmp.put("parentGlAccountId", par);
				tmp.put("description", des);
				listData.add(tmp);
				listData.addAll(getAllGlAccountChildren(itt, delegator));
			}
		}
		return listData;
	}
	
}
