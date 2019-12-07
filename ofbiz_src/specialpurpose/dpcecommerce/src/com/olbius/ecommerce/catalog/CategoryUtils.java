package com.olbius.ecommerce.catalog;

import java.util.List;
import java.util.Map;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;

import javolution.util.FastList;
import javolution.util.FastMap;

public class CategoryUtils {

	public final static String module = CategoryUtils.class.getName();
	public static final String resource = "CommonUiLabels";

	public static Map<String, Object> getProductInCategoryExceptOne(
			Delegator delegator, String productCategoryId, String productId,
			int viewIndex, int pagesize) {
		Map<String, Object> res = FastMap.newInstance();
		EntityListIterator lstIte = null;
		try {
			EntityFindOptions findOptions = new EntityFindOptions(true,
					EntityFindOptions.TYPE_SCROLL_INSENSITIVE,
					EntityFindOptions.CONCUR_READ_ONLY, true);
			EntityCondition cond = EntityCondition.makeCondition(UtilMisc
					.toList(EntityCondition.makeCondition("primaryProductCategoryId",
							productCategoryId), EntityCondition.makeCondition(
							"productId", EntityOperator.NOT_EQUAL, productId)));
			lstIte = delegator.find("Product", cond, null, null,
					UtilMisc.toList("-productId"), findOptions);
			int arraySize = lstIte.getResultsSizeAfterPartialList();
			res.put("totalrows", arraySize);
			List<GenericValue> contents = FastList.newInstance();
			int start = viewIndex * pagesize;
			int end = start + pagesize;
			if (arraySize < end) {
				contents = lstIte.getPartialList(start, arraySize);
			} else {
				contents = lstIte.getPartialList(start, pagesize);
			}
			res.put("products", contents);
		} catch (Exception e) {
			e.printStackTrace();
			res.put("totalrows", 0);
			res.put("products", FastList.newInstance());
		} finally {
			if (lstIte != null) {
				try {
					lstIte.close();
				} catch (GenericEntityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return res;
	}
}
