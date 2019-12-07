package com.olbius.baseecommerce.backend;

import java.sql.Timestamp;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.webapp.website.WebSiteWorker;

import com.olbius.basehr.util.DateUtil;

import javolution.util.FastList;
import javolution.util.FastMap;

public class ContentUtils {

    public final static String module = ContentUtils.class.getName();
    public static final String resource = "CommonUiLabels";

    public static List<GenericValue> getLatestNews(Delegator delegator, int viewIndex, int viewSize) {
		List<GenericValue> contents = FastList.newInstance();
		EntityListIterator lstIte = null;
		try{
			EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", "CTNT_PUBLISHED"),
					EntityCondition.makeCondition("contentTypeId", "NEWS_ARTICLE")));
			EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
			lstIte = delegator.find("Content", cond, null, null, UtilMisc.toList("-createdDate"), findOptions);
			int arraySize = lstIte.getResultsTotalSize();
			if(arraySize < viewSize){
				contents = lstIte.getPartialList(0,arraySize);
			} else{
				contents = lstIte.getPartialList(0,4);
			}
		} catch (Exception e){
			e.printStackTrace();
			contents = FastList.newInstance();
		} finally {
			if(lstIte != null){
				try {
					lstIte.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		return contents;
    }
    public static List<Map<String, Object>> processListContent(Delegator delegator, List<GenericValue> contents, Locale locale){
		List<Map<String, Object>> endContents = FastList.newInstance();
		for(GenericValue e : contents){
			Map<String, Object> o = FastMap.newInstance();
			o.put("contentId", e.getString("contentId"));
			o.put("contentName", e.getString("contentName"));
			o.put("description", ContentUtils.getSubstract(delegator, e.getString("description")));
			o.put("createdDate", e.getString("createdDate"));
			String img = e.getString("originalImageUrl");
			o.put("images", img);
			int comment = getTotalComment(delegator, e.getString("contentId"));
			o.put("totalComment", comment);
			Timestamp createdDate = e.getTimestamp("createdDate");
			if(createdDate != null){
				String ago = getTimeAgo(locale, createdDate);
				o.put("ago", ago);
			} else{
				o.put("ago", "");
			}
			endContents.add(o);
		}
		return endContents;
    }
    public static Map<String, Object> getContentDataByCategory(Delegator delegator, String webSiteId, String contentCategoryId, int viewIndex, int pagesize){
		EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", "CTNT_PUBLISHED"),
				EntityCondition.makeCondition("contentCategoryId", contentCategoryId),
				EntityUtil.getFilterByDateExpr(),
				EntityUtil.getFilterByDateExpr("CategoryFromDate", "CategoryThruDate"),
				EntityCondition.makeCondition("webSiteId", webSiteId)));
		Map<String, Object> res = getContentByCondition(delegator, cond, viewIndex, pagesize);
		return res;
    }
    public static Map<String, Object> getNewestContent(Delegator delegator, int viewIndex, int pagesize){
		Map<String, Object> res = FastMap.newInstance();
		try{
			List<GenericValue> categories = delegator.findList("ContentType", EntityCondition.makeCondition("parentTypeId",  "NEWS_ARTICLE"), null, null, null, false);
			List<EntityCondition> catecon = FastList.newInstance();
			for(GenericValue e : categories){
				catecon.add(EntityCondition.makeCondition("contentTypeId", e.getString("contentTypeId")));
			}
			EntityCondition cond = EntityCondition.makeCondition(UtilMisc.toList(EntityCondition.makeCondition("statusId", "CTNT_PUBLISHED"),
					EntityCondition.makeCondition(catecon, EntityOperator.OR)));
			res = getContentByCondition(delegator, cond, viewIndex, pagesize);
		} catch (Exception e){
			e.printStackTrace();
		}
		return res;
    }
    public static Map<String, Object> getContentByCondition(Delegator delegator, EntityCondition cond, int viewIndex, int pagesize){
		Map<String, Object> res = FastMap.newInstance();
		EntityListIterator lstIte = null;
		try{
			EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
			lstIte = delegator.find("WebSiteContentCategoryContentDetail", cond, null, null, UtilMisc.toList("-lastUpdatedStamp"), findOptions);
			int arraySize = lstIte.getResultsTotalSize();
			res.put("totalrows", arraySize);
			List<GenericValue> contents = FastList.newInstance();
			int start = viewIndex * pagesize + 1;
			int end = start + pagesize;
			if(arraySize < end){
				contents = lstIte.getPartialList(start,arraySize);
			} else{
				contents = lstIte.getPartialList(start, pagesize);
			}
			res.put("contents", contents);
		} catch (Exception e){
			e.printStackTrace();
			res.put("totalrows", 0);
			res.put("contents", FastList.newInstance());
		} finally {
			if(lstIte != null){
				try {
					lstIte.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		return res;
    }
    public static List<GenericValue> getListContentByCategory(Delegator delegator, String contentTypeId, int viewIndex, int pagesize){
		List<GenericValue> contents = FastList.newInstance();
		EntityListIterator lstIte = null;
		try{
			EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
			EntityCondition cond = EntityCondition.makeCondition("contentTypeId", contentTypeId);
			lstIte = delegator.find("Content", cond, null, null, UtilMisc.toList("-createdDate"), findOptions);
			int arraySize = lstIte.getResultsTotalSize();
			int start = viewIndex * pagesize + 1;
			int end = start + pagesize;
			if(arraySize < end){
				contents = lstIte.getPartialList(start,arraySize);
			} else{
				contents = lstIte.getPartialList(start, pagesize);
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if(lstIte != null){
				try {
					lstIte.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		return contents;
    }
    public static List<GenericValue> getPostTopComment(Delegator delegator, int viewIndex, int pagesize) throws GenericEntityException{
		List<GenericValue> contents = FastList.newInstance();
		EntityListIterator tmp = null;
		try{
			EntityFindOptions findOptions = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, true);
			tmp = delegator.find("ContentAssocCommentCount", null, null, null, UtilMisc.toList("-total"), findOptions);
			int start = viewIndex * pagesize + 1;
			int end = start + pagesize;
			int size = tmp.getResultsTotalSize();
			if(end >= size){
				contents = tmp.getPartialList(start, size);
			}else{
				contents = tmp.getPartialList(start, end);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(tmp != null){
				tmp.close();
			}
		}
		return contents;
    }
    public static Map<String, Object> getContent(Delegator delegator, HttpServletRequest request, String contentId, Locale locale){
		String webSiteId = WebSiteWorker.getWebSiteId(request);
		return getContent(delegator, contentId, webSiteId, locale);
    }
    public static Map<String, Object> getContent(Delegator delegator, String contentId,  String webSiteId, Locale locale){
		Map<String, Object> res = FastMap.newInstance();
		try{
			List<GenericValue> tmp = delegator.findList("WebSiteContentDetail", EntityCondition.makeCondition(
															UtilMisc.toList(
																	EntityCondition.makeCondition("contentId", contentId),
																	EntityCondition.makeCondition("webSiteId", webSiteId)
															), EntityOperator.AND
														), null, UtilMisc.toList("-fromDate"), null, false);
			if(UtilValidate.isNotEmpty(tmp)){
				GenericValue e = EntityUtil.getFirst(tmp);
				if(e != null){
					res.put("contentId", e.getString("contentId"));
					res.put("contentName", e.getString("contentName"));
					res.put("description", ContentUtils.getSubstract(delegator, e.getString("description")));
					res.put("createdDate", e.getString("createdDate"));
					String img = e.getString("originalImageUrl");
					res.put("images", img);
					int comment = getTotalComment(delegator, e.getString("contentId"));
					res.put("totalComment", comment);
					Timestamp createdDate = e.getTimestamp("createdDate");
					if(createdDate != null){
						String ago = getTimeAgo(locale, createdDate);
						res.put("ago", ago);
					}else{
						res.put("ago", "");
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
    }
    public static String getThumbnail(Delegator delgator, String contentId, String width, String height) {
		String res = "";
		try {
			List<GenericValue> img = delgator.findList("ContentImageView", EntityCondition.makeCondition(
									UtilMisc.toList(EntityCondition.makeCondition("contentId", contentId),
											EntityCondition.makeCondition("width", width),
											EntityCondition.makeCondition("height", height))), null, UtilMisc.toList("-imgCreatedDate"), null, false);
			if(UtilValidate.isNotEmpty(img)){
				GenericValue e = img.get(0);
				res = e.getString("imgSrc");
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return res;
	}
    public static Map<String, Object> getThumbnail(Delegator delgator, String contentId) {
		Map<String, Object> res = FastMap.newInstance();
		try {
			List<GenericValue> img = delgator.findList("ContentImageView", EntityCondition.makeCondition(
					UtilMisc.toList(EntityCondition.makeCondition("contentId", contentId))), null, UtilMisc.toList("-imgCreatedDate"), null, false);
			if(UtilValidate.isNotEmpty(img)){
				GenericValue e = img.get(0);
				res.put("imgSrc", e.getString("imgSrc"));
				res.put("width", e.getString("width"));
				res.put("height", e.getString("height"));
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		return res;
	}

    public static String getSubstract(Delegator delgator, String content) {
		return getSubstract(delgator, content, 200);
	}
    public static String getSubstract(Delegator delgator, String content, int length) {
		String brk = "<div style=\"page-break-after: always\"><span style=\"display:none\">&nbsp;</span></div>";
		if(content.indexOf(brk) != -1){
			String[] tmp = content.split(brk);
			if(UtilValidate.isNotEmpty(tmp)){
				return tmp[0];
			}
		}
		String after = content.replaceAll("\\<.*?>","");
		String[] tmp2 = after.split(" ");
		int size = tmp2.length;
		int end = size < length ? size : length;
		StringBuilder res = new StringBuilder();
		for(int i = 0; i < end; i++){
			res.append(tmp2[i]);
			res.append(" ");
		}
		return res.toString();
	}
    public static int getTotalComment(Delegator delegator, String contentId){
		int res = 0;
		EntityListIterator list = null;
		try{
			list = delegator.find("ContentAssoc", EntityCondition.makeCondition("contentIdTo", contentId), null, null, null, null);
			res = list.getResultsTotalSize();
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			if(list != null){
				try {
					list.close();
				} catch (GenericEntityException e) {
					e.printStackTrace();
				}
			}
		}
		return res;
    }
    public static String getTimeAgo(Locale locale, Timestamp createdStamp) {
		Long minute = (long) (1000*60);
		Long hour = minute*60;
		Long day = hour*24;
		String timeAgo = "";
		Long current = System.currentTimeMillis();
		Long lastUpdated = createdStamp.getTime();
		Long ago = current - lastUpdated;
		if (ago > day*2) {
			timeAgo = DateUtil.convertDate(createdStamp);
		} else{
			if (ago > day) {
				timeAgo = analysTime(locale, ago, day, "BSDaysAgo");
			} else{
				if (ago > hour) {
					timeAgo = analysTime(locale, ago, hour, "BSHoursAgo");
				} else{
					timeAgo = analysTime(locale, ago, minute, "BSMinutesAgo");
				}
			}
		}
		return timeAgo;
	}
	private static String analysTime(Locale locale, Long ago, Long time, String timeUnit) {
		ago = (long) Math.ceil(ago/time);
		if (ago == 0) {
			ago = (long) 1;
		}
		return ago + " " + UtilProperties.getMessage("DpcEcommerceBackendUiLabels", timeUnit, locale);
	}

	public static String getFooter(Delegator delegator, Locale locale)
			throws GenericEntityException {
		String contentId = "";
		if ("vi".equals(locale.getDisplayLanguage())) {
			contentId = "FOOTER_VI_DPC";
		} else{
			//	TODO get footer by another language but now we hard code vi
			contentId = "FOOTER_VI_DPC";
		}
		GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
		String footer = "";
		if (UtilValidate.isNotEmpty(content)) {
			footer = content.getString("longDescription");
		}
		return footer;
	}
	public static String getHeader(Delegator delegator, Locale locale)
			throws GenericEntityException {
		String contentId = "";
		if ("vi".equals(locale.getDisplayLanguage())) {
			contentId = "HEADER_VI_DPC";
		} else{
			//	TODO get header by another language but now we hard code vi
			contentId = "HEADER_VI_DPC";
		}
		GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
		String header = "";
		if (UtilValidate.isNotEmpty(content)) {
			header = content.getString("longDescription");
		}
		return header;
	}
	public static String getStores(Delegator delegator, Locale locale)
			throws GenericEntityException {
		String contentId = "";
		if ("vi".equals(locale.getDisplayLanguage())) {
			contentId = "STORES_VI_DPC";
		} else{
			//	TODO get stores by another language but now we hard code vi
			contentId = "STORES_VI_DPC";
		}
		GenericValue content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), false);
		String stores = "";
		if (UtilValidate.isNotEmpty(content)) {
			stores = content.getString("longDescription");
		}
		return stores;
	}
	
	public static List<GenericValue> slideOfProduct(Delegator delegator, String productId, boolean isBackend)
			throws GenericEntityException {
		List<GenericValue> productContents = FastList.newInstance();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			if (isBackend) {
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productContentTypeId", "SLIDE")));
			} else{
				conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "productContentTypeId", "SLIDE", "statusId", "CTNT_PUBLISHED")));
			}
			productContents = delegator.findList("ProductAndContent",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId", "originalImageUrl", "description", "statusId"),
					UtilMisc.toList("createdStamp"), null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return productContents;
	}
	public static List<GenericValue> getHotContent(Delegator delegator)
			throws GenericEntityException {
		List<GenericValue> contents = FastList.newInstance();
		try {
			List<GenericValue> contentTypes = delegator.findList("ContentType",
					EntityCondition.makeCondition("parentTypeId", EntityJoinOperator.EQUALS, "HOT_ARTICLE"), UtilMisc.toSet("contentTypeId"), null, null, false);
			List<String> hotContentTypeId = EntityUtil.getFieldListFromEntityList(contentTypes, "contentTypeId", true);
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition("contentTypeId", EntityJoinOperator.IN, hotContentTypeId));
			List<GenericValue> contentTypeMembers = delegator.findList("ContentTypeMember",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId"), UtilMisc.toList("-lastUpdatedStamp"), null, false);
			List<String> contentIds = EntityUtil.getFieldListFromEntityList(contentTypeMembers, "contentId", true);
			conditions.clear();
			conditions.add(EntityCondition.makeCondition("contentId", EntityJoinOperator.IN, contentIds));
			conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "CTNT_PUBLISHED"));
			EntityFindOptions findOptions = new EntityFindOptions();
			findOptions.setMaxRows(4);
			contents = delegator.findList("ContentAndContentType",
					EntityCondition.makeCondition(conditions), null, UtilMisc.toList("-lastUpdatedStamp"), findOptions, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents;
	}
	
	public static List<GenericValue> mainSlide(Delegator delegator, String webSiteId, boolean isBackend)
			throws GenericEntityException {
		List<GenericValue> contents = FastList.newInstance();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contentIdTo", "MAIN_SLIDE", "contentAssocTypeId", "LIST_ENTRY")));
			List<GenericValue> contentAssocs = delegator.findList("ContentAssoc",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId"), null, null, false);
			List<String> contentIds = EntityUtil.getFieldListFromEntityList(contentAssocs, "contentId", true);
			conditions.clear();
			conditions.add(EntityCondition.makeCondition("contentId", EntityJoinOperator.IN, contentIds));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contentTypeId", "SLIDE", "webSiteId", webSiteId)));
			if (!isBackend) {
				conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "CTNT_PUBLISHED"));
			}
			contents = delegator.findList("WebSiteContentDetail",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("contentId", "statusId", "originalImageUrl", "url", "author", "description"),
					UtilMisc.toList("-lastUpdatedStamp"), null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return contents;
	}
	
	public static List<GenericValue> horizontalBanners(Delegator delegator, String webSiteId, boolean isBackend)
			throws GenericEntityException {
		List<GenericValue> listBanners = FastList.newInstance();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contentTypeId", "HORIZONTALBANNER", "webSiteId", webSiteId)));
			if (!isBackend) {
				conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "CTNT_PUBLISHED"));
			}
			listBanners = delegator.findList("WebSiteContentDetail",
					EntityCondition.makeCondition(conditions),
					UtilMisc.toSet("contentId", "statusId", "url", "originalImageUrl"), UtilMisc.toList("-lastUpdatedStamp"), null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listBanners;
	}
	public static List<GenericValue> verticalBanners(Delegator delegator, String webSiteId, boolean isBackend)
			throws GenericEntityException {
		List<GenericValue> listBanners = FastList.newInstance();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contentTypeId", "VERTICALBANNER", "webSiteId", webSiteId)));
			if (!isBackend) {
				conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "CTNT_PUBLISHED"));
			}
			listBanners = delegator.findList("WebSiteContentDetail",
					EntityCondition.makeCondition(conditions),
					UtilMisc.toSet("contentId", "statusId", "url", "originalImageUrl"), UtilMisc.toList("-lastUpdatedStamp"), null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listBanners;
	}
	public static List<GenericValue> partnerBanners(Delegator delegator, String webSiteId, boolean isBackend)
			throws GenericEntityException {
		List<GenericValue> listBanners = FastList.newInstance();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("contentTypeId", "PARTNERBANNER", "webSiteId", webSiteId)));
			if (!isBackend) {
				conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "CTNT_PUBLISHED"));
			}
			listBanners = delegator.findList("WebSiteContentDetail",
					EntityCondition.makeCondition(conditions),
					UtilMisc.toSet("contentId", "statusId", "url", "originalImageUrl"), UtilMisc.toList("-lastUpdatedStamp"), null, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listBanners;
	}
	
	public static GenericValue getBannerByCategoryId(Delegator delegator, String webSiteId, String productCategoryId) {
		GenericValue banner = new GenericValue();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("webSiteId", webSiteId, "prodCatContentTypeId", "CATEGORY_IMAGE",
					"statusId", "CTNT_PUBLISHED", "productCategoryId", productCategoryId)));
			List<GenericValue> listBanners = delegator.findList("ProductCategoryContentDetail",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("originalImageUrl", "url"), null, null, false);
			if (UtilValidate.isNotEmpty(listBanners)) {
				banner = EntityUtil.getFirst(listBanners);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return banner;
	}
	
	public static Map<String, Object> webSiteBackGround(Delegator delegator, String webSiteId, boolean isBackend) {
		Map<String, Object> backGround = FastMap.newInstance();
		try {
			List<EntityCondition> conditions = FastList.newInstance();
			conditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
			conditions.add(EntityCondition.makeCondition(UtilMisc.toMap("webSiteContentTypeId", "BACKGROUND", "webSiteId", webSiteId)));
			if (!isBackend) {
				conditions.add(EntityCondition.makeCondition("statusId", EntityJoinOperator.EQUALS, "CTNT_PUBLISHED"));
			}
			List<GenericValue> contents = delegator.findList("WebSiteContentDetail",
					EntityCondition.makeCondition(conditions), UtilMisc.toSet("originalImageUrl", "contentTypeId", "statusId"), null, null, false);
			for (GenericValue x : contents) {
				String contentTypeId = x.getString("contentTypeId");
				String originalImageUrl = x.getString("originalImageUrl");
				String statusId = x.getString("statusId");
				switch (contentTypeId) {
				case "FOOTER_BACKGROUND":
					backGround.put("FOOTER_BACKGROUND", originalImageUrl);
					backGround.put("FOOTER_BACKGROUND_S", statusId);
					break;
				case "HEADER_BACKGROUND":
					backGround.put("HEADER_BACKGROUND", originalImageUrl);
					backGround.put("HEADER_BACKGROUND_S", statusId);
					break;
				case "INFO_BACKGROUND":
					backGround.put("INFO_BACKGROUND", originalImageUrl);
					backGround.put("INFO_BACKGROUND_S", statusId);
					break;
				default:
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return backGround;
	}
	
	public static GenericValue getContent(Delegator delegator, String contentId, boolean cache){
		GenericValue content = null;
		try {
			content = delegator.findOne("Content", UtilMisc.toMap("contentId", contentId), cache);
		} catch (GenericEntityException e) {
			Debug.log(e.getMessage());
		}
		return content;
	}

	public static String getCookie(HttpServletRequest request, String key){
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(key)) {
					return cookie.getValue();
				}
			}
        }
		return "";
	}
}