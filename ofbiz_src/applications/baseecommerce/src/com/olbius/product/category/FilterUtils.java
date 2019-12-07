package com.olbius.product.category;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.StringUtil.StringWrapper;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.util.EntityUtilProperties;
import org.ofbiz.product.category.CategoryContentWrapper;
import org.ofbiz.service.DispatchContext;

import com.olbius.product.catalog.NewCatalogWorker;

import javolution.util.FastList;
import javolution.util.FastMap;

public class FilterUtils {

	public final static String module = FilterUtils.class.getName();
	public static final String resource = "EcommerceBackendUiLabels";

	public static Map<String, Object> getBrandAndProductNumber(Delegator delegator, String productCategoryId, Locale locale){
		Map<String, Object> res = FastMap.newInstance();
		List<String> categories = FastList.newInstance();
		categories.add(productCategoryId);
		CategoryUtils.flatternCategories(delegator, categories, productCategoryId);
		List<Map<String, Object>> brandRes = FastList.newInstance();
		int brTotalSize = 0;
		try {
			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			List<GenericValue> brands = delegator.findList("ProductCategoryMemberAndProductBrand",
					EntityCondition.makeCondition(
						UtilMisc.toList(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categories),
								EntityCondition.makeCondition("productTypeId", "FINISHED_GOOD"),
								EntityUtil.getFilterByDateExpr())
							), UtilMisc.toSet("brandName", "groupName"), UtilMisc.toList("brandName"), opts, true);
			List<GenericValue> productData = delegator.findList("ProductCategoryMemberAndProductBrand",
					EntityCondition.makeCondition(
						UtilMisc.toList(EntityCondition.makeCondition("productCategoryId", EntityOperator.IN, categories),
								EntityCondition.makeCondition("productTypeId", "FINISHED_GOOD"),
								EntityCondition.makeCondition("productName", EntityOperator.NOT_EQUAL, null),
								EntityUtil.getFilterByDateExpr())
							), UtilMisc.toSet("productId"), null, opts, true);
			long count = 0;
			List<String> products = FastList.newInstance();
			for(GenericValue e : productData){
				products.add(e.getString("productId"));
			}
			String defaultOtherBrand = EntityUtilProperties.getPropertyValue("ecommerce.properties", "category.brand.other.default", "OTHER_BRAND", delegator);
			for(GenericValue e : brands){
				Map<String, Object> o = FastMap.newInstance();
				String brandName = e.getString("brandName");
				if(UtilValidate.isEmpty(brandName)){
					o.put("brandName", defaultOtherBrand);
					o.put("groupName", UtilProperties.getMessage(resource, "BEOtherBrands", locale));
				}else{
					o.put("brandName", brandName);
					o.put("groupName", e.getString("groupName"));
				}
				count = getBrandTotalProduct(delegator, products, brandName);
				brTotalSize += count;
				o.put("count", count);
				brandRes.add(o);
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		res.put("brands", brandRes);
		res.put("brTotalSize", brTotalSize);
		return res;
	}
	public static int getBrandTotalProduct(Delegator delegator, List<String> products, String brandName){
		try {
			List<EntityExpr> listcond = UtilMisc.toList(
					EntityCondition.makeCondition("productId", EntityOperator.IN, products),
					EntityCondition.makeCondition("brandName", brandName));
			EntityCondition cond = EntityCondition.makeCondition(listcond);
			EntityFindOptions opts = new EntityFindOptions();
			opts.setDistinct(true);
			List<GenericValue> list = delegator.findList("ProductCacheDataBrand", cond, null, null, opts, false);
			int tmp = list.size();
			return tmp;
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		return 0;
	}
}
