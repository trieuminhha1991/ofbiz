package com.olbius.obb;

import java.util.List;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

public class ConfigUtils {
	//How many page load each time
	public static int CATEGORY_DETAIL_PAGINATION_SIZE = 4;
	//How many product load each time
	public static int CATEGORY_DETAIL_PAGE_SIZE = 18;
	//How many article load each time
	public static int ARTICLE_CATEGORY_PAGE_SIZE = 6;
	//How many relate article load each time
	public static int RELATE_ARTICLE_CATEGORY_PAGE_SIZE = 5;
	//How many top new article load each time
	public static int TOP_RIGHT_ARTICLE_CATEGORY_PAGE_SIZE = 5;
	//How many top comment post load each time
	public static int TOP_COMMENT_ARTICLE_CATEGORY_PAGE_SIZE = 5;
	//How many top comment post load each time
	public static int LATEST_ARTICLE_CATEGORY_PAGE_SIZE = 5;
	//How many related product can be loaded
	public static int RELATED_PRODUCT_PAGE_SIZE = 6;
	
	public static int CATEGORY_INTRO_CONTENT_PAGE_SIZE = 5;
	
	public static String getZopimConfig(Delegator delegator, String websiteId){
		return getConfigContent(delegator, websiteId, "ZOPIM_CONFIG");
	}
	public static String getGoogleAnalyticConfig(Delegator delegator, String websiteId){
		return getConfigContent(delegator, websiteId, "GOOGLE_CONFIG");
	}
	public static String getConfigContent(Delegator delegator, String websiteId, String contentTypeId){
		try {
			List<GenericValue> res = delegator.findList("WebSiteContentDetail", EntityCondition.makeCondition(UtilMisc.toList(
															EntityCondition.makeCondition("webSiteId", websiteId),
															EntityUtil.getFilterByDateExpr(),
															EntityCondition.makeCondition("webSiteContentTypeId", contentTypeId))), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(res)){
				GenericValue zo = EntityUtil.getFirst(res);
				return zo.getString("description");
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	public static GenericValue getConfig(Delegator delegator, String websiteId, String contentTypeId){
		try {
			List<GenericValue> res = delegator.findList("WebSiteContentDetail", EntityCondition.makeCondition(UtilMisc.toList(
															EntityCondition.makeCondition("webSiteId", websiteId),
															EntityUtil.getFilterByDateExpr(),
															EntityCondition.makeCondition("webSiteContentTypeId", contentTypeId))), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(res)){
				GenericValue zo = EntityUtil.getFirst(res);
				return zo;
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static List<GenericValue> getConfigs(Delegator delegator, String websiteId, String contentTypeId){
		List<GenericValue> res = null;
		try {
			res = delegator.findList("WebSiteContentDetail", EntityCondition.makeCondition(UtilMisc.toList(
															EntityCondition.makeCondition("webSiteId", websiteId),
															EntityUtil.getFilterByDateExpr(),
															EntityCondition.makeCondition("webSiteContentTypeId", contentTypeId))), null, UtilMisc.toList("-fromDate"), null, false);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}
}
