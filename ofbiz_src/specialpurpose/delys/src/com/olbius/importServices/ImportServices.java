package com.olbius.importServices;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamSource;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.batik.util.EventDispatcher.Dispatcher;
import org.apache.commons.lang.StringUtils;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;
import org.codehaus.groovy.classgen.genArrayAccess;
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilFormatOut;
import org.ofbiz.base.util.UtilGenerics;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.base.util.collections.MapStack;
import org.ofbiz.base.util.string.FlexibleStringExpander;
import org.ofbiz.common.email.NotificationServices;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityComparisonOperator;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.transaction.GenericTransactionException;
import org.ofbiz.entity.transaction.TransactionUtil;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.webapp.view.ApacheFopWorker;
import org.ofbiz.widget.fo.FoScreenRenderer;
import org.ofbiz.widget.html.HtmlScreenRenderer;
import org.ofbiz.widget.screen.ScreenRenderer;

import com.ibm.icu.util.BytesTrie.Iterator;
import com.olbius.accounting.jqservices.UtilJQServices;
import com.olbius.globalSetting.GlobalSettingServices;
import com.olbius.util.SecurityUtil;
import com.sun.mail.imap.Utility.Condition;

@SuppressWarnings({ "unused", "deprecation" })
public class ImportServices {
	
	public static Role ROLE = null;
	public static GenericValue USER_LOGIN = null;
	public static String PARTY_ID = null;
	public enum Role {
		DELYS_ADMIN, DELYS_ROUTE, DELYS_ASM_GT, DELYS_RSM_GT, DELYS_CSM_GT, DELYS_CUSTOMER_GT, DELYS_SALESSUP_GT;
	}
	public static final String module = ImportServices.class.getName();
	public static final String resource = "DelysUiLabels";
	public static final String resourceError = "DelysErrorUiLabels";
//	hoanmStart
	public static Map<String, Object> getOriginalQuantityOnHandTotalAjax(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String inventoryItemId = (String) context.get("inventoryItemId");
		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		GenericValue inventoryItem = delegator.findOne("InventoryItem", UtilMisc.toMap("inventoryItemId", inventoryItemId), false);
		if (UtilValidate.isNotEmpty(inventoryItem)) {
			quantityOnHandTotal = inventoryItem.getBigDecimal("quantityOnHandTotal");
		}
		result.put("quantityOnHandTotal", quantityOnHandTotal);
		return result;
	}
	public static Map<String, Object> getListProductConfigPackingAjax(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String productId = (String)context.get("productId");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("uomFromId");
		fieldToSelects.add("uomToId");
		List<EntityCondition> listAllConditions = FastList.newInstance();
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)));
		listAllConditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> listProductConfigPacking = delegator.findList("ConfigPacking", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), fieldToSelects, null, null, false);
		Set<String> setUomFromId = FastSet.newInstance();
		for (GenericValue genericValue : listProductConfigPacking) {
			setUomFromId.add(genericValue.getString("uomFromId"));
		}
		Map<String, Object> mapRalationPacking = FastMap.newInstance();
		for (String string : setUomFromId) {
			List<String> listUomToId = FastList.newInstance();
			for (GenericValue genericValue : listProductConfigPacking) {
				String uomFromId = genericValue.getString("uomFromId");
				if (string.equals(uomFromId)) {
					listUomToId.add(genericValue.getString("uomToId"));
				}
			}
			mapRalationPacking.put(string, listUomToId);
		}
		result.put("mapRalationPacking", mapRalationPacking);
		return result;
	}
	public static Map<String, Object> addProductToCategoryCustom(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Delegator delegator = ctx.getDelegator();
		String productCategoryId = (String) context.get("productCategoryId");
		String productId = (String) context.get("productId");
		java.sql.Timestamp fromDate = (Timestamp) context.get("fromDate");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		GenericValue productCategoryMember = delegator.findOne("ProductCategoryMember", UtilMisc.toMap("productCategoryId", productCategoryId, "productId", productId, "fromDate", fromDate), false);
		boolean beganTx = TransactionUtil.begin(7200);
		if (UtilValidate.isNotEmpty(productCategoryMember)) {
			try {
				dispatcher.runSync("updateProductToCategory", context);
			} catch (GenericServiceException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				return ServiceUtil.returnError("error");
			}
		} else {
			try {
				List<GenericValue> listProductCategory = delegator.findList("ProductCategory", EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", "TAX_CATEGORY")), null, null, null, false);
				List<EntityCondition> listAllConditions = FastList.newInstance();
				if (UtilValidate.isNotEmpty(listProductCategory)) {
					for (GenericValue x : listProductCategory) {
						EntityCondition tmpConditon = EntityCondition.makeCondition("productCategoryId", EntityOperator.NOT_EQUAL ,x.getString("productCategoryId"));
						listAllConditions.add(tmpConditon);
					}
				}
				EntityCondition tmpConditon2 = EntityCondition.makeCondition(UtilMisc.toMap("productId", productId));
				listAllConditions.add(tmpConditon2);
				List<GenericValue> listProductCategoryMember = delegator.findList("ProductCategoryMember", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, null, false);
				if (UtilValidate.isEmpty(listProductCategoryMember)) {
					dispatcher.runSync("addProductToCategory", context);
				}else {
					GenericValue productCategoryMemberRepulse = EntityUtil.getFirst(listProductCategoryMember);
					Map<String, Object> mapProductCategoryMemberRepulse = FastMap.newInstance();
					Map<String, Object> mapRepulseThruDate = FastMap.newInstance();
					Timestamp thruDate = new Timestamp(System.currentTimeMillis());
					mapProductCategoryMemberRepulse.put("thruDate", thruDate);
					mapProductCategoryMemberRepulse.put("productCategoryId", productCategoryMemberRepulse.getString("productCategoryId"));
					mapProductCategoryMemberRepulse.put("productId", productId);
					mapProductCategoryMemberRepulse.put("fromDate", productCategoryMemberRepulse.getTimestamp("fromDate"));
					mapProductCategoryMemberRepulse.put("userLogin", userLogin);
					dispatcher.runSync("updateProductToCategory", mapProductCategoryMemberRepulse);
					dispatcher.runSync("addProductToCategory", context);
				}
			} catch (GenericServiceException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				return ServiceUtil.returnError("error");
			}
		}
		TransactionUtil.commit(beganTx);
		result.put("Info", "success");
		return result;
	}
	public static Map<String, Object> getMapConfigCapacityGeneralsAjax(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("uomId");
		fieldToSelects.add("uomIdTo");
		List<EntityCondition> listAllConditions = FastList.newInstance();
		EntityCondition tmpConditon = EntityCondition.makeCondition(UtilMisc.toMap("uomTypeId", "SHIPMENT_PACKING"));
		listAllConditions.add(tmpConditon);
		listAllConditions.add(EntityUtil.getFilterByDateExpr());
		List<GenericValue> listProductConfigPacking = delegator.findList("UomTypeAndConversionDated", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), fieldToSelects, null, null, false);
		Set<String> setUomFromId = FastSet.newInstance();
		for (GenericValue genericValue : listProductConfigPacking) {
			setUomFromId.add(genericValue.getString("uomId"));
		}
		Map<String, Object> mapRalationPacking = FastMap.newInstance();
		for (String string : setUomFromId) {
			List<String> listUomToId = FastList.newInstance();
			for (GenericValue genericValue : listProductConfigPacking) {
				String uomFromId = genericValue.getString("uomId");
				if (string.equals(uomFromId)) {
					listUomToId.add(genericValue.getString("uomIdTo"));
				}
			}
			mapRalationPacking.put(string, listUomToId);
		}
		result.put("mapRalationPacking", mapRalationPacking);
		return result;
	}
	public static Map<String, Object> getlistQuotaItemsAjax(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String quotaId = (String)context.get("quotaId");
		Delegator delegator = ctx.getDelegator();
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("quotaItemSeqId");
		fieldToSelects.add("productName");
		fieldToSelects.add("quotaQuantity");
		fieldToSelects.add("quantityAvailable");
		fieldToSelects.add("quantityUomId");
		fieldToSelects.add("fromDate");
		fieldToSelects.add("thruDate");
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("quotaItemSeqId");
		List<GenericValue> listQuotaItems = delegator.findList("QuotaItem", EntityCondition.makeCondition("quotaId", quotaId), fieldToSelects, orderBy, null, false);
		for (GenericValue genericValue : listQuotaItems) {
			Date fromDate = (Date) genericValue.get("fromDate");
			String StrfromDate = fromDate.toString();
			genericValue.set("fromDate", StrfromDate);
			Date thruDate = (Date) genericValue.get("thruDate");
			String StrthruDate = thruDate.toString();
			genericValue.set("thruDate", StrthruDate);
		}
		result.put("listQuotaItems", listQuotaItems);
		return result;
	}
	public static Map<String, Object> getConfigPackingAjax(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String productId = (String)context.get("productId");
		String uomToId = (String)context.get("uomToId");
		GenericValue thisConfigPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId", productId, "uomToId", uomToId, "uomFromId", "PALLET"), false);
		Map<String, Object> configPackingInfo = FastMap.newInstance();
		if(UtilValidate.isNotEmpty(thisConfigPacking))configPackingInfo = thisConfigPacking.getAllFields();
		result.put("configPackingInfo", configPackingInfo);
		return result;
	}
	public static Map<String, Object> getMapProductsWithCategoryAjax(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Map<String, Object> mapProductsWithCategory = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		Set<String> fieldSelects = FastSet.newInstance();
		fieldSelects.add("productId");
		fieldSelects.add("primaryProductCategoryId");
		fieldSelects.add("internalName");
		List<GenericValue> listProducts = delegator.findList("Product",EntityCondition.makeCondition(UtilMisc.toMap("productTypeId", "FINISHED_GOOD")), fieldSelects, null, null, false);
		Set<String> categoryId = new HashSet<String>();
		for (GenericValue x : listProducts) {
			String primaryProductCategoryId = (String) x.get("primaryProductCategoryId");
			if (UtilValidate.isEmpty(primaryProductCategoryId)) {
				continue;
			}
			categoryId.add(primaryProductCategoryId);
		}
		for (String s : categoryId) {
			List<GenericValue> listProduct = FastList.newInstance();
			for (GenericValue x : listProducts) {
				String primaryProductCategoryId = (String) x.get("primaryProductCategoryId");
				if (s.equals(primaryProductCategoryId)) {
					listProduct.add(x);
				}
			}
			mapProductsWithCategory.put(s, listProduct);
		}
		result.put("mapProductsWithCategory", mapProductsWithCategory);
		return result;
	}
	public static Map<String, Object> loadPlanOfProductAjax(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		List<Map<String, Object>> listPlanOfProduct = FastList.newInstance();
		Delegator delegator = ctx.getDelegator();
		String productId = (String)context.get("productId");
		String productPlanId = (String)context.get("productPlanId");
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("fromDate");
		List<GenericValue> listProductPlanHeaders = delegator.findList("ProductPlanAndCustomTimePeriodForGroup",EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", productPlanId)), null, orderBy, null, false);
		long facilityDimIdTemp = 8000;
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		String uomToId = "";
		int quantityConvert = 0;
		if (UtilValidate.isNotEmpty(product)) {
			uomToId = (String) product.get("quantityUomId");
			GenericValue configPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId", productId, "uomFromId", "PALLET", "uomToId", uomToId), false);
			if (UtilValidate.isNotEmpty(configPacking)) {
				quantityConvert = configPacking.getBigDecimal("quantityConvert").intValue();
			} else {
				result.put("quantityConvert", String.valueOf(quantityConvert));
				result.put("uomToId", uomToId);
				return result;
			}
		}else {
			return ServiceUtil.returnError("Can not find product");
		}
		String productPlanItemSeqId = "";
		int lastInventoryForecast = 0;
		for (GenericValue x : listProductPlanHeaders) {
			GenericValue salesForecast = null;
			List<GenericValue> listSalesForecast = null;
			GenericValue salesForecastDetail = null;
			List<GenericValue> listSalesForecastDetail = null;
			int quantity = 0;
			int importQuantityRecommend = 0;
			int palletQuantity = 0;
			int inventoryForecast = 0;
			int inventoryReality = 0;
			double salesInventoryFocastDays = 0;
			String status = "";
			Map<String, Object> planMonthDetails = new FastMap<String, Object>();
			
			String productPlanName = (String) x.get("productPlanName");
			productPlanName = productPlanName.split(" ")[1];
			String customTimePeriodIdOfSales = (String) x.get("customTimePeriodIdOfSales");
			String internalPartyId = (String) x.get("internalPartyId");
			String thisProductPlanId = (String) x.get("productPlanId");
			String statusId = (String) x.get("statusId");
			
//			lay du bao ban hang
			listSalesForecast = delegator.findList("SalesForecast",EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodIdOfSales, "internalPartyId", internalPartyId )), null, null, null, false);
			if (UtilValidate.isNotEmpty(listSalesForecast)) {
				salesForecast = listSalesForecast.get(0);
				String salesForecastId = (String) salesForecast.get("salesForecastId");
				listSalesForecastDetail = delegator.findList("SalesForecastDetail",EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", salesForecastId, "productId", productId )), null, null, null, false);
				if (UtilValidate.isNotEmpty(listSalesForecastDetail)) {
					salesForecastDetail = listSalesForecastDetail.get(0);
					quantity = salesForecastDetail.getBigDecimal("quantity").intValue();
				}
			}else {
				status = "SalesForecastNotAvalible";
			}
//			lay du bao ban hang end
			
//			lay ton kho thong ke duoc cua thang truoc
			Calendar cal = Calendar.getInstance();
			cal.setTime((java.sql.Date) x.get("fromDate"));
			cal.add(Calendar.DATE, -1);
			java.sql.Date dateLastMonth = new java.sql.Date(cal.getTimeInMillis());
			List<GenericValue> listDateDim = delegator.findList("DateDimension", EntityCondition.makeCondition("dateValue", dateLastMonth), null, null, null, false);
			List<GenericValue> listProductDim = delegator.findList("ProductDimension", EntityCondition.makeCondition("productId", productId), null, null, null, false);
			if (UtilValidate.isNotEmpty(listDateDim) && UtilValidate.isNotEmpty(listProductDim)) {
				GenericValue dateDim = EntityUtil.getFirst(listDateDim);
				GenericValue productDim = EntityUtil.getFirst(listProductDim);
				long productDimId = (Long) productDim.get("dimensionId");
				long dateDimId = (Long) dateDim.get("dimensionId");
				List<GenericValue> listFacilityFact = delegator.findList("FacilityFact", EntityCondition.makeCondition(UtilMisc.toMap("facilityDimId", facilityDimIdTemp, "productDimId", productDimId, "dateDimId", dateDimId)), null, null, null, false);
				if (UtilValidate.isNotEmpty(listFacilityFact)) {
					GenericValue facilityFact = EntityUtil.getFirst(listFacilityFact);
					inventoryReality = facilityFact.getBigDecimal("inventoryTotal").intValue();
					status = "HasInventoryReality";
				}
			}
//			lay ton kho cua thang truoc end
			
			if (statusId.equals("PLAN_COMPLETED") || statusId.equals("PLAN_ORDERED") || statusId.equals("PLAN_PROCESSING")) {
				List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", thisProductPlanId, "productId", productId)), null, null, null, false);
				if (UtilValidate.isNotEmpty(listProductPlanItem)) {
					GenericValue productPlanItem = EntityUtil.getFirst(listProductPlanItem);
					importQuantityRecommend = productPlanItem.getBigDecimal("planQuantity").intValue();
					inventoryForecast = productPlanItem.getBigDecimal("inventoryForecast").intValue();
					productPlanItemSeqId = (String) productPlanItem.get("productPlanItemSeqId");
					palletQuantity = importQuantityRecommend/quantityConvert;
//					inventoryForecast = importQuantityRecommend - quantity + inventoryReality + lastInventoryForecast;
					try {
						salesInventoryFocastDays = (double)inventoryForecast/((double)quantity/30);
						String strSalesInventoryFocastDays = String.valueOf(salesInventoryFocastDays);
						int strLength = strSalesInventoryFocastDays.length();
						int end = 5;
						if (strLength < 5) {
							end = strLength;
						}
						salesInventoryFocastDays = Double.parseDouble((String) strSalesInventoryFocastDays.subSequence(0, end));
					} catch (NumberFormatException e) {
						salesInventoryFocastDays = 0;
						inventoryForecast = 0;
					}
					if (salesInventoryFocastDays != (double)salesInventoryFocastDays){
						salesInventoryFocastDays = 0;
						inventoryForecast = 0;
					}
					status = "Imported";
				}else {
					status = "NotImport";
				}
			}else {
				//dua ra goi y nhap khau
				List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", thisProductPlanId, "productId", productId)), null, null, null, false);
				if (UtilValidate.isNotEmpty(listProductPlanItem)) {
					GenericValue productPlanItem = EntityUtil.getFirst(listProductPlanItem);
					importQuantityRecommend = productPlanItem.getBigDecimal("planQuantity").intValue();
					inventoryForecast = productPlanItem.getBigDecimal("inventoryForecast").intValue();
					productPlanItemSeqId = (String) productPlanItem.get("productPlanItemSeqId");
					palletQuantity = importQuantityRecommend/quantityConvert;
//					inventoryForecast = importQuantityRecommend - quantity + inventoryReality + lastInventoryForecast;
					try {
						salesInventoryFocastDays = (double)inventoryForecast/((double)quantity/30);
						String strSalesInventoryFocastDays = String.valueOf(salesInventoryFocastDays);
						int strLength = strSalesInventoryFocastDays.length();
						int end = 5;
						if (strLength < 5) {
							end = strLength;
						}
						salesInventoryFocastDays = Double.parseDouble((String) strSalesInventoryFocastDays.subSequence(0, end));
					} catch (NumberFormatException e) {
						salesInventoryFocastDays = 0;
						inventoryForecast = 0;
					}
					if (salesInventoryFocastDays != (double)salesInventoryFocastDays){
						salesInventoryFocastDays = 0;
						inventoryForecast = 0;
					}
					status = "Stored";
				} else {
					if (quantity > 0) {
						double redundantQuantity = Math.ceil(((double)quantity/30)*8);
						int minQuantity = quantity + (int)redundantQuantity - inventoryReality - lastInventoryForecast;
						if (minQuantity <= 0) {
							palletQuantity = 0;
							importQuantityRecommend = 0;
							inventoryForecast = inventoryReality + lastInventoryForecast - quantity;
						}else {
							palletQuantity = minQuantity/quantityConvert;
							palletQuantity = minQuantity/quantityConvert;
							importQuantityRecommend = palletQuantity*quantityConvert;
							inventoryForecast = importQuantityRecommend - quantity + inventoryReality + lastInventoryForecast;
						}
						try {
							salesInventoryFocastDays = (double)inventoryForecast/((double)quantity/30);
							String strSalesInventoryFocastDays = String.valueOf(salesInventoryFocastDays);
							int strLength = strSalesInventoryFocastDays.length();
							int end = 5;
							if (strLength < 5) {
								end = strLength;
							}
							salesInventoryFocastDays = Double.parseDouble((String) strSalesInventoryFocastDays.subSequence(0, end));
						} catch (NumberFormatException e) {
							salesInventoryFocastDays = 0;
							inventoryForecast = 0;
							importQuantityRecommend = 0;
						}
						if (salesInventoryFocastDays != (double)salesInventoryFocastDays){
							salesInventoryFocastDays = 0;
							inventoryForecast = 0;
							importQuantityRecommend = 0;
						}
					}
					status = "Recommend";
				}
//				if (quantity == 0) {
//					status = "Recommend";
//				}
				//dua ra goi y nhap khau end
			}
			
			planMonthDetails.put("productPlanName", productPlanName);
			planMonthDetails.put("statusId", statusId);
			planMonthDetails.put("salesForecastQuantity", quantity);
			planMonthDetails.put("importQuantityRecommend", importQuantityRecommend);
			planMonthDetails.put("palletQuantity", palletQuantity);
			planMonthDetails.put("inventoryForecast", inventoryForecast);
			planMonthDetails.put("inventoryReality", inventoryReality);
			planMonthDetails.put("salesInventoryFocastDays", salesInventoryFocastDays);
			planMonthDetails.put("lastInventoryForecast", lastInventoryForecast);
			planMonthDetails.put("status", status);
			planMonthDetails.put("productPlanId", thisProductPlanId);
			planMonthDetails.put("productPlanItemSeqId", productPlanItemSeqId);
			listPlanOfProduct.add(planMonthDetails);
			lastInventoryForecast = inventoryForecast;
		}
		result.put("planOfProduct", listPlanOfProduct);
		result.put("quantityConvert", String.valueOf(quantityConvert));
		result.put("uomToId", uomToId);
		return result;
	}
	public static Map<String, Object> getlistQuotaAjax(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String quotaTypeId = (String)context.get("quotaTypeId");
		Delegator delegator = ctx.getDelegator();
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("quotaId");
		fieldToSelects.add("quotaName");
		fieldToSelects.add("quotaTypeId");
		fieldToSelects.add("description");
		fieldToSelects.add("fromDate");
		fieldToSelects.add("thruDate");
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("quotaId");
		List<GenericValue> listQuotas = delegator.findList("QuotaHeader", EntityCondition.makeCondition("quotaTypeId", quotaTypeId), fieldToSelects, orderBy, null, false);
		for (GenericValue genericValue : listQuotas) {
			Date fromDate = (Date) genericValue.get("fromDate");
			String StrfromDate = fromDate.toString();
			genericValue.set("fromDate", StrfromDate);
			Date thruDate = (Date) genericValue.get("thruDate");
			String StrthruDate = thruDate.toString();
			genericValue.set("thruDate", StrthruDate);
		}
		result.put("listQuotas", listQuotas);
		return result;
	}
	public static Map<String, Object> getFromAndThruDateAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String customTimePeriodId = (String) context.get("customTimePeriodId");
		Delegator delegator = ctx.getDelegator();
		GenericValue genericValue = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		genericValue.set("fromDate", String.valueOf(genericValue.get("fromDate")));
		genericValue.set("thruDate", String.valueOf(genericValue.get("thruDate")));
		result.put("fromAndThruDate", genericValue);
		return result;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> getPlanInThisMonthAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		List<String> customTimePeriodId = (List<String>)context.get("customTimePeriodId[]");
		String currencyUomId = "";
		List<GenericValue> listProducts = new ArrayList<GenericValue>();
		List<String> orderBy = new ArrayList<String>();
		String supplierPartyId = (String)context.get("supplierPartyId");
		Set<String> fieldToSelects = FastSet.newInstance();
		for (String string : customTimePeriodId) {
			supplierPartyId = "";
			orderBy = new ArrayList<String>();
			orderBy.add("customTimePeriodId");
			fieldToSelects.clear();
			fieldToSelects.add("productPlanId");
			fieldToSelects.add("parentProductPlanId");
			fieldToSelects.add("customTimePeriodId");
			fieldToSelects.add("internalPartyId");
			fieldToSelects.add("supplierPartyId");
			fieldToSelects.add("currencyUomId");
//			List<GenericValue> listProductPlanHeaders = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", string, "supplierPartyId", supplierPartyId)), fieldToSelects, orderBy, null, false);
			List<GenericValue> listProductPlanHeaders = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", string)), fieldToSelects, orderBy, null, false);
			orderBy = new ArrayList<String>();
			orderBy.add("productPlanId");
			fieldToSelects.clear();
			fieldToSelects.add("productPlanId");
			fieldToSelects.add("productPlanItemSeqId");
			fieldToSelects.add("productId");
			fieldToSelects.add("planQuantity");
			fieldToSelects.add("productWeight");
			fieldToSelects.add("weight");
			fieldToSelects.add("partyId");
			fieldToSelects.add("lastPrice");
			fieldToSelects.add("internalName");
			fieldToSelects.add("productPackingUomId");
			fieldToSelects.add("weightUomId");
			fieldToSelects.add("primaryProductCategoryId");
			for (GenericValue genericValue : listProductPlanHeaders) {
				String productPlanId = (String) genericValue.get("productPlanId");
				List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItemAndSupplierProductAndProductAndUom", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), fieldToSelects, orderBy, null, false);
				listProducts.addAll(listProductPlanItem);
				currencyUomId = (String) genericValue.get("currencyUomId");
			}
		}
		Set<String> listProductId = new HashSet<String>();
		for (GenericValue gv : listProducts) {
			String productId = (String) gv.get("productId");
			listProductId.add(productId);
		}
		List<String> listProductIds = new ArrayList<String>();
		listProductIds.addAll(listProductId);
		Collections.sort(listProductIds);
		double totalWeightAll = 0;
		double totalPriceAll = 0;
		List<Map> listProductRelease = new ArrayList<Map>();
		for (String stProductId : listProductIds) {
			Map<String, Object> resultProduct = new FastMap<String, Object>();
			String internalName = "";
			String productPackingUomId = "";
			BigDecimal lastPrice = BigDecimal.ZERO;
			int quantityImport = 0;
			double totalWeight = 0;
			String weightUomId = "";
			String quantityUomId = "";
			StringBuilder messageInfo = new StringBuilder();
			String primaryProductCategoryId = "";
			double totalPrice = 0;
			String productIdRS = "";
			for (GenericValue gv : listProducts) {
				String productId = (String) gv.get("productId");
				if (stProductId.equals(productId)) {
					productIdRS = productId;
					primaryProductCategoryId = (String) gv.get("primaryProductCategoryId");
					internalName = (String) gv.get("internalName");
					productPackingUomId = (String) gv.get("productPackingUomId");
					GenericValue packing = delegator.findOne("Uom", UtilMisc.toMap("uomId", productPackingUomId), false);
					productPackingUomId = (String) packing.get("description");
					BigDecimal quantityImportBd = BigDecimal.ZERO;
					quantityImportBd = (BigDecimal) gv.get("planQuantity");
					int quantityImportInt = quantityImportBd.intValueExact();
					quantityImport += quantityImportInt;
					//them 10% tong so luong san pham theo yeu cau cua delys
					quantityImport = quantityImport + (quantityImport/10);
					lastPrice = (BigDecimal) gv.get("lastPrice");
					double lastPriceInt = lastPrice.doubleValue();
					BigDecimal productWeight = (BigDecimal) gv.get("weight");
					if (productWeight != null) {
						double productWeightInt = productWeight.doubleValue();
						totalWeight = productWeightInt * quantityImport;
					}else {
						messageInfo.append("Data of ").append(internalName).append(" not find value productWeight");
					}
					weightUomId = (String) gv.get("weightUomId");
					quantityUomId = weightUomId;
					GenericValue weight = delegator.findOne("Uom", UtilMisc.toMap("uomId", weightUomId), false);
					weightUomId = (String) weight.get("abbreviation");
					totalPrice = quantityImport * lastPriceInt;
				}
			}
			int totalWeightInt = (int) totalWeight;
			int totalPriceInt = (int) totalPrice;
			totalWeightAll += totalWeightInt;
			totalPriceAll += totalPriceInt;
			resultProduct.put("internalName", internalName);
			resultProduct.put("productId", productIdRS);
			resultProduct.put("quantityUomId", quantityUomId);
			resultProduct.put("primaryProductCategoryId", primaryProductCategoryId);
			resultProduct.put("productPackingUomId", productPackingUomId);
			resultProduct.put("quantityImport", quantityImport);
			resultProduct.put("lastPrice", lastPrice);
			resultProduct.put("totalWeight", totalWeightInt);
			resultProduct.put("weightUomId", weightUomId);
			resultProduct.put("totalPrice", totalPriceInt);
			resultProduct.put("messageInfo",  messageInfo.toString());
			listProductRelease.add(resultProduct);
		}
		result.put("totalWeightAll", totalWeightAll);
		result.put("totalPriceAll", totalPriceAll);
		result.put("listProductInMonths", listProductRelease);
		result.put("currencyUomId", currencyUomId);
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getMonthInPlanHeaderAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		List<String> customTimePeriodId = (List<String>)context.get("customTimePeriodId[]");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("customTimePeriodId");
		fieldToSelects.add("parentPeriodId");
		fieldToSelects.add("periodName");
		Delegator delegator = ctx.getDelegator();
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("customTimePeriodId");
		GenericValue thisYear = new GenericValue();
		List<GenericValue> listMonths = new ArrayList<GenericValue>();
		for (String string : customTimePeriodId) {
			List<GenericValue> listMonth = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", string, "periodTypeId", "IMPORT_MONTH")), fieldToSelects, orderBy, null, false);
			thisYear = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", string), false);
			String yearName = (String) thisYear.get("periodName");
			yearName = yearName.split(":")[1];
			for (GenericValue genericValue : listMonth) {
				String monthName = (String) genericValue.get("periodName");
				monthName = monthName.split(":")[1];
				monthName = monthName + "-" + yearName;
				genericValue.set("periodName", monthName);
			}
			listMonths.addAll(listMonth);
		}
		result.put("listMonths", listMonths);
		return result;
	}
	public static Map<String, Object> getYearInPlanHeaderAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String periodTypeId = (String)context.get("periodTypeId");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("customTimePeriodId");
		fieldToSelects.add("parentPeriodId");
		fieldToSelects.add("periodTypeId");
		fieldToSelects.add("periodName");
		Delegator delegator = ctx.getDelegator();
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("customTimePeriodId");
		List<GenericValue> listYears = delegator.findList("CustomTimePeriod",EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", periodTypeId)), fieldToSelects, orderBy, null, false);	
		result.put("listYears", listYears);
		return result;
	}
	public static Map<String, Object> getLocationFacilityAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String)context.get("facilityId");
		
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("createdStamp");
		List<GenericValue> listlocationFacility = delegator.findList("LocationFacility",EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, orderBy, null, false);	
		result.put("listlocationFacility", listlocationFacility);
		return result;
	}
	public static Map<String, Object> getGeneralQuantityAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String)context.get("facilityId");
		Map<String, Integer> mapTotalQuantity = FastMap.newInstance();
		Map<String, Integer> mapTotalQuantityOriginal = FastMap.newInstance();
		List<GenericValue> listLocationFacility = delegator.findList("LocationFacility",EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		List<GenericValue> listLocationFacilityQuantity = delegator.findList("LocationFacilityAndInventoryItemLocation",EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
		for (GenericValue x : listLocationFacility) {
			String locationId = (String) x.get("locationId");
			int quantity = 0;
			for (GenericValue g : listLocationFacilityQuantity) {
				String locationIdQuantity = (String) g.get("locationId");
				if (locationId.equals(locationIdQuantity)) {
					quantity += g.getBigDecimal("quantity").intValue();
				}
			}
			mapTotalQuantity.put(locationId, quantity);
			mapTotalQuantityOriginal.put(locationId, quantity);
		}
		for (GenericValue g : listLocationFacility) {
			String parentLocationId = (String) g.get("parentLocationId");
			if (UtilValidate.isEmpty(parentLocationId)) {
				String locationId = (String) g.get("locationId");
				if (checkHasChild(listLocationFacility, locationId)) {
					mapTotalQuantity.put(locationId, increasingFromChilds(mapTotalQuantity, locationId, listLocationFacility));
				}
			}
		}
		for (GenericValue x : listLocationFacility) {
			String locationId = (String) x.get("locationId");
			if (checkHasChild(listLocationFacility, locationId)) {
				mapTotalQuantity.put(locationId, mapTotalQuantity.get(locationId) + mapTotalQuantityOriginal.get(locationId));
			}
		}
		result.put("totalQuantity", mapTotalQuantity);
		return result;
	}
	private static Integer increasingFromChilds( Map<String, Integer> mapTotalQuantity, String parents, List<GenericValue> listLocationFacility) {
		int result = 0;
		for (GenericValue x : listLocationFacility) {
			String parentLocationId = (String) x.get("parentLocationId");
			if (parents.equals(parentLocationId)) {
				String locationId = (String) x.get("locationId");
				if (checkHasChild(listLocationFacility, locationId)) {
					mapTotalQuantity.put(locationId, increasingFromChilds(mapTotalQuantity, locationId, listLocationFacility));
				}
				int quantity = mapTotalQuantity.get(locationId);
				result += quantity;
			}
		}
		return result;
	}
	private static Boolean checkHasChild(List<GenericValue> listLocationFacility, String locationId) {
		for (GenericValue x : listLocationFacility) {
			String parentLocationId = (String) x.get("parentLocationId");
			if (locationId.equals(parentLocationId)) {
				return true;
			}
		}
		return false;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> getListInventoryItemInLocation (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> mapInventoryItemInLocation = new FastMap<String, Object>();
		if (clearInventoryItemInLocation(delegator)) {
			List<String> locationId = (List<String>) context.get("arrayLocationId[]");
			List<String> orderBy = new ArrayList<String>();
			orderBy.add("expireDate");
			for (String string : locationId) {
				List<GenericValue> listInventoryItemInLocation = delegator.findList("InventoryItemLocationAndInventoryItem",EntityCondition.makeCondition(UtilMisc.toMap("locationId", string)), null, orderBy, null, false);
				mapInventoryItemInLocation.put(string, listInventoryItemInLocation);
			}
		}
		result.put("mapInventoryItemInLocation", mapInventoryItemInLocation);
		return result;
	}
	public static Map<String, Object> deleteProduct(DispatchContext ctx, Map<String, Object> context) {
		Delegator delegator = ctx.getDelegator();
		String productId = (String) context.get("productId");
		try {
			delegator.removeByAnd("Product", UtilMisc.toMap("productId", productId));
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("has used");
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> deleteLocationAjax (DispatchContext ctx, Map<String, Object> context) throws GenericTransactionException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> mapInventoryItemInLocation = new FastMap<String, Object>();
		List<String> locationId = (List<String>) context.get("arrayLocationId[]");
		String facilityId = (String) context.get("facilityId");
		List<GenericValue> listInventoryItemInLocation = FastList.newInstance();
		boolean beganTx = TransactionUtil.begin(7200);
		for (String string : locationId) {
			try {
				listInventoryItemInLocation = delegator.findList("InventoryItemLocation",EntityCondition.makeCondition(UtilMisc.toMap("locationId", string)), null, null, null, false);
				delegator.removeAll(listInventoryItemInLocation);
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				e.printStackTrace();
				return ServiceUtil.returnError("error");
			}
			try {
				List<GenericValue> listLocation = delegator.findList("LocationFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, null, null, false);
				Map<String, String> mapLocation = FastMap.newInstance();
				for (GenericValue x : listLocation) {
					String locationIdKey = (String) x.get("locationId");
					String parentLocationId = (String) x.get("parentLocationId");
					mapLocation.put(locationIdKey, parentLocationId);
				}
				List<GenericValue> listLocationParents = FastList.newInstance();
				for (GenericValue x : listInventoryItemInLocation) {
					String thisLocationId = (String) x.get("locationId");
					String parentLocationId = mapLocation.get(thisLocationId);
					if (UtilValidate.isEmpty(parentLocationId)) {
						continue;
					}
					String inventoryItemId = (String) x.get("inventoryItemId");
					x.set("locationId", parentLocationId);
					listLocationParents = delegator.findList("InventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("locationId", parentLocationId)), null, null, null, false);
					if (UtilValidate.isNotEmpty(listLocationParents)) {
						for (GenericValue g : listLocationParents) {
							String parentsInventoryItemId = (String) g.get("inventoryItemId");
							if (inventoryItemId.equals(parentsInventoryItemId)) {
								BigDecimal oldQuantity = g.getBigDecimal("quantity"); 
								BigDecimal newQuantity = x.getBigDecimal("quantity"); 
								x.set("quantity", oldQuantity.add(newQuantity));
							}
						}
					}
//					delegator.createOrStore(x);
				}
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				e.printStackTrace();
				return ServiceUtil.returnError("error");
			}
			try {
				delegator.removeByAnd("LocationFacility", UtilMisc.toMap("locationId", string));
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				e.printStackTrace();
				return ServiceUtil.returnError("error");
			}
		}
		TransactionUtil.commit(beganTx);
		result.put("result", "success");
		return result;
	}
	public static Map<String, Object> getListProductAvalibleAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listProductAvalible = FastList.newInstance();
		if (clearInventoryItemInLocation(delegator)) {
			String productId =  (String) context.get("productId");
			String facilityId =  (String) context.get("facilityId");
			Set<String> fieldSelect = FastSet.newInstance();
			fieldSelect.add("locationId");
			List<GenericValue> listLocationFacility = delegator.findList("LocationFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), fieldSelect, null, null, false);
			List<String> listLocationId = FastList.newInstance();
			for (GenericValue g : listLocationFacility) {
				listLocationId.add(g.getString("locationId"));
			}
			List<String> orderBy = new ArrayList<String>();
			orderBy.add("expireDate");
			for (String s : listLocationId) {
				List<GenericValue> listInventoryItemLocation = FastList.newInstance();
				if (UtilValidate.isEmpty(productId)) {
					listInventoryItemLocation = delegator.findList("InventoryItemLocationAndInventoryItem",EntityCondition.makeCondition(UtilMisc.toMap("locationId", s)), null, orderBy, null, false);
				} else {
					listInventoryItemLocation = delegator.findList("InventoryItemLocationAndInventoryItem",EntityCondition.makeCondition(UtilMisc.toMap("locationId", s, "productId", productId)), null, orderBy, null, false);
				}
				
				listProductAvalible.addAll(listInventoryItemLocation);
			}
		}
		result.put("listProductAvalible", listProductAvalible);
		return result;
	}
	public static Map<String, Object> deliveryInLocationAjax (DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("info", "success");
		Delegator delegator = ctx.getDelegator();
		String inventoryItemId = (String) context.get("inventoryItemId");
		String locationId = (String) context.get("locationId");
		String uomId = (String) context.get("uomId");
		BigDecimal quantity = (BigDecimal) context.get("quantity");
		try {
			GenericValue inventoryItemLocation = delegator.findOne("InventoryItemLocation", UtilMisc.toMap("inventoryItemId", inventoryItemId, "locationId", locationId, "uomId", uomId), false);
			BigDecimal oldQuantity = inventoryItemLocation.getBigDecimal("quantity");
			if (oldQuantity.intValue() >= quantity.intValue()) {
				try {
					delegator.storeByCondition("InventoryItemLocation", UtilMisc.toMap("quantity", quantity), EntityCondition.makeCondition(UtilMisc.toMap("inventoryItemId", inventoryItemId, "locationId", locationId, "uomId", uomId)));
				} catch (GenericEntityException e) {
					result.put("info", "error");
				}
			}else {
				result.put("info", "error");
			}
		} catch (GenericEntityException e1) {
			result.put("info", "error");
		}
		return result;
	}
	public static Map<String, Object> checkHasInventoryInLocationAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String) context.get("facilityId");
		Set<String> field = FastSet.newInstance();
		field.add("locationId");
		List<GenericValue> listProductInventoryItem = delegator.findList("LocationFacility", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), field, null, null, false);
		Map<String, Boolean> mapResult = FastMap.newInstance();
		List<GenericValue> listInventoryItemLocation = FastList.newInstance();
		for (GenericValue x : listProductInventoryItem) {
			boolean boolResult = true;
			String locationId = (String) x.get("locationId");
			listInventoryItemLocation = delegator.findList("InventoryItemLocation",EntityCondition.makeCondition(UtilMisc.toMap("locationId", locationId)), null, null, null, false);
			if (UtilValidate.isEmpty(listInventoryItemLocation)) {
				boolResult = false;
			}
			mapResult.put(locationId, boolResult);
		}
		result.put("result", mapResult);
		return result;
	}
	
	public static Map<String, Object> dropAllAjax (DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String strResult = "Ok";
		try {
			List<GenericValue> listInventoryItemLocation = delegator.findList("InventoryItemLocation", null, null, null, null, false);
			List<GenericValue> listLocationFacility = delegator.findList("LocationFacility", null, null, null, null, false);
			List<GenericValue> listLocationFacilityType = delegator.findList("LocationFacilityType", null, null, null, null, false);
			delegator.removeAll(listInventoryItemLocation);
			delegator.removeAll(listLocationFacility);
			delegator.removeAll(listLocationFacilityType);
		} catch (Exception e) {
			strResult = "false";
			e.printStackTrace();
		}
		result.put("result", strResult);
		return result;
	}
	private static boolean clearInventoryItemInLocation(Delegator delegator) {
		BigDecimal zezo = BigDecimal.ZERO;
		try {
			List<GenericValue> listInventoryItemInLocationClear = delegator.findList("InventoryItemLocation",EntityCondition.makeCondition(UtilMisc.toMap("quantity", zezo)), null, null, null, false);
			if (UtilValidate.isEmpty(listInventoryItemInLocationClear)) {
				return true;
			}
			delegator.removeAll(listInventoryItemInLocationClear);
		} catch (GenericEntityException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public static void addToLocationEventAjax(HttpServletRequest request, HttpServletResponse response) throws GenericTransactionException{
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
		JSONArray jsonData = JSONArray.fromObject(request.getParameter("totalRecord"));
		boolean success = true;
		boolean beganTx = TransactionUtil.begin(7200);
		for (int i = 0; i < jsonData.size(); i++) {
			JSONObject thisRow = jsonData.getJSONObject(i);
			String inventoryItemId = (String) thisRow.get("inventoryItemId");
			String productId = (String) thisRow.get("productId");
			String locationId = (String) thisRow.get("locationId");
			String uomId = (String) thisRow.get("uomId");
			Object quantity = (Integer)thisRow.get("quantity");
			
			if (UtilValidate.isEmpty(quantity)) {
				continue;
			}
			DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			symbols.setGroupingSeparator(',');
			symbols.setDecimalSeparator('.');
			String pattern = "#,##0.0#";
			DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
			decimalFormat.setParseBigDecimal(true);
			try {
				quantity = (BigDecimal) decimalFormat.parse(quantity.toString());
			} catch (ParseException e) {
				Debug.logError(e, module);
			}
			Long strExpireDate = (Long) thisRow.get("expireDate");
			java.util.Date date = new java.util.Date(strExpireDate);
			Timestamp expireDate =  new java.sql.Timestamp((new java.sql.Date(date.getTime())).getTime());
			
			GenericValue inventoryItemLocation = delegator.makeValue("InventoryItemLocation", UtilMisc.toMap("inventoryItemId", inventoryItemId, "productId", productId, "locationId", locationId, "uomId", uomId, "quantity", quantity, "expireDate", expireDate));
			try {
				delegator.createOrStore(inventoryItemLocation);
			} catch (Exception e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				success = false;
				break;
			}
		}
		TransactionUtil.commit(beganTx);
		if (clearInventoryItemInLocation(delegator)) {
			if (success) {
				request.setAttribute("RESULT_MESSAGE", "SUSSESS");
			} else {
				request.setAttribute("RESULT_MESSAGE", "ERROR");
			}
		}
	}
	public static Map<String, Object> getAllProductNotLocationAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String)context.get("facilityId");
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("inventoryItemId");
		
		List<GenericValue> listProductInventoryItem = delegator.findList("InventoryItem", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, orderBy, null, false);
		List<GenericValue> listProductHasLocation = delegator.findList("LocationFacilityAndInventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, orderBy, null, false);
		
		if(UtilValidate.isEmpty(listProductInventoryItem)){
			result.put("error", "NotHaveInventoryItem");
			return result;
		}
		if(UtilValidate.isEmpty(listProductHasLocation)){
			result.put("listProductNotLocation", listProductInventoryItem);
			return result;
		}
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		BigDecimal remain = BigDecimal.ZERO;
		for (GenericValue x : listProductInventoryItem) {
			String inventoryItemId = (String) x.get("inventoryItemId");
			remain = x.getBigDecimal("quantityOnHandTotal");
			for (GenericValue z : listProductHasLocation) {
				if (z.containsValue(inventoryItemId)) {
					quantity = z.getBigDecimal("quantity");
					remain = remain.subtract(quantity);
				}
			}
			x.set("quantityOnHandTotal", remain);
		}
		List<GenericValue> noQuantity = FastList.newInstance();
		for (GenericValue f : listProductInventoryItem) {
			BigDecimal thisQuantityOnHandTotal = (BigDecimal) f.get("quantityOnHandTotal");
			if (thisQuantityOnHandTotal.intValue() <= 0) {
				noQuantity.add(f);
			}
		}
		listProductInventoryItem.removeAll(noQuantity);
		result.put("listProductNotLocation", listProductInventoryItem);
		return result;
	}
	public static Map<String, Object> checkHasDataSupplierProuductAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String partyId = (String)context.get("partyId");
		String productId = (String)context.get("productId");
		Boolean hasData = true;
		List<GenericValue> listSupplierProduct = delegator.findList("SupplierProduct", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId, "productId", productId)), null, null, null, false);
		if (UtilValidate.isEmpty(listSupplierProduct)) {
			hasData = false;
		}
		result.put("hasData", hasData);
		return result;
	}
	public static Map<String, Object> checkProductNotLocationAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String facilityId = (String)context.get("facilityId");
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("inventoryItemId");
		
		List<GenericValue> listProductInventoryItem = delegator.findList("InventoryItem", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, orderBy, null, false);
		List<GenericValue> listProductHasLocation = delegator.findList("LocationFacilityAndInventoryItemLocation", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId)), null, orderBy, null, false);
		
		if(UtilValidate.isEmpty(listProductInventoryItem)){
			result.put("result", false);
			return result;
		}
		if(UtilValidate.isEmpty(listProductHasLocation)){
			result.put("result", true);
			return result;
		}
		BigDecimal quantity = BigDecimal.ZERO;
		BigDecimal quantityOnHandTotal = BigDecimal.ZERO;
		BigDecimal remain = BigDecimal.ZERO;
		for (GenericValue x : listProductInventoryItem) {
			String inventoryItemId = (String) x.get("inventoryItemId");
			remain = x.getBigDecimal("quantityOnHandTotal");
			for (GenericValue z : listProductHasLocation) {
				if (z.containsValue(inventoryItemId)) {
					quantity = z.getBigDecimal("quantity");
					remain = remain.subtract(quantity);
				}
			}
			x.set("quantityOnHandTotal", remain);
		}
		List<GenericValue> noQuantity = FastList.newInstance();
		for (GenericValue f : listProductInventoryItem) {
			BigDecimal thisQuantityOnHandTotal = (BigDecimal) f.get("quantityOnHandTotal");
			if (thisQuantityOnHandTotal.intValue() <= 0) {
				noQuantity.add(f);
			}
		}
		listProductInventoryItem.removeAll(noQuantity);
		boolean boolresult = true;
		if (UtilValidate.isEmpty(listProductInventoryItem)) {
			boolresult = false;
		}
		result.put("result", boolresult);
		return result;
	}
	public static Map<String, Object> getUomUnit (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String productId = (String)context.get("productId");
		if(productId != null){
			String uomId = null;
			Delegator delegator = ctx.getDelegator();
			GenericValue gen = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			if(UtilValidate.isNotEmpty(gen)){
				uomId = (String)gen.get("quantityUomId");
			}
			List<String> orderBy = new ArrayList<String>();
			orderBy.add("uomId");
			List<GenericValue> listUom = delegator.findList("Uom", EntityCondition.makeCondition(UtilMisc.toMap("uomId", uomId)), null, orderBy, null, false);
			result.put("listUom", listUom);
		}
		return result;
	}
	public static Map<String, Object> getTimeAndSalesForcast (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("customTimePeriodId");
		fieldToSelects.add("parentPeriodId");
		fieldToSelects.add("periodTypeId");
		fieldToSelects.add("periodName");
		fieldToSelects.add("fromDate");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listTime = delegator.findList("CustomTimePeriod", null, fieldToSelects, null, null, false);
		result.put("listTimeAndSalesForcast", listTime);
		return result;
	}
	public static Map<String, Object> getYear (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String periodTypeId = (String)context.get("periodTypeId");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("customTimePeriodId");
		fieldToSelects.add("parentPeriodId");
		fieldToSelects.add("periodTypeId");
		fieldToSelects.add("periodName");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listYears = delegator.findList("CustomTimePeriod",EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", periodTypeId)), fieldToSelects, null, null, false);	
		result.put("listYears", listYears);
		return result;
	}
	public static Map<String, Object> getMonth (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		Delegator delegator = ctx.getDelegator();
		GenericValue thisYear = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		String year = (String) thisYear.get("periodName");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("customTimePeriodId");
		fieldToSelects.add("parentPeriodId");
		fieldToSelects.add("periodTypeId");
		fieldToSelects.add("periodName");
		List<GenericValue> listQuarter = delegator.findList("CustomTimePeriod",EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodId)), fieldToSelects, null, null, false);
		List<GenericValue> listMonths = new ArrayList<GenericValue>();
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("customTimePeriodId");
		for (GenericValue q : listQuarter) {
			String thisId = (String) q.get("customTimePeriodId");
			List<GenericValue> listTempMonth = delegator.findList("CustomTimePeriod",EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", thisId)), fieldToSelects, orderBy, null, false);
			for (GenericValue m : listTempMonth) {
				listMonths.add(m);
			}
		}
		for (GenericValue t : listMonths) {
			String month = (String) t.get("periodName");
			String yearAndMonth = month + "-" + year;
			t.setString("periodName", yearAndMonth);			
		}
		result.put("listMonths", listMonths);
		return result;
	}
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getSalesForcast (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		String productId = (String)context.get("productId");
		String thisMonth = (String)context.get("thisMonth");
		Integer quantityConvert = (Integer)context.get("quantityConvert");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("salesForecastId");
		fieldToSelects.add("customTimePeriodId");		
		fieldToSelects.add("parentSalesForecastId");
		fieldToSelects.add("internalPartyId");
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listSalesForecast = delegator.findList("SalesForecast",EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "internalPartyId", "RSM_GT_VUNG1")), fieldToSelects, null, null, false);	
		List<GenericValue> listSalesForecastDetails = new ArrayList<GenericValue>();
		fieldToSelects.clear();
		fieldToSelects.add("salesForecastId");
		fieldToSelects.add("salesForecastDetailId");		
		fieldToSelects.add("quantity");
		fieldToSelects.add("productId");
		for (GenericValue f : listSalesForecast) {
			String salesForecastId = (String) f.get("salesForecastId");
			List<GenericValue> listTempSalesForecastDetails = delegator.findList("SalesForecastDetail",EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", salesForecastId, "productId", productId)), fieldToSelects, null, null, false);
			for (GenericValue t : listTempSalesForecastDetails) {
				listSalesForecastDetails.add(t);
			}
		}
		List<Map> listMonthAndQuantity = new ArrayList<Map>();
		Map<String, Object> details = FastMap.newInstance();
		BigDecimal quantity = BigDecimal.ZERO;
		int importVolume = 0;
		int tonCuoiThang = 0;
		double ngayTon = 0;
		int quantt = 0;
		for (GenericValue rm : listSalesForecastDetails) {
			quantity = (BigDecimal) rm.get("quantity");
		}
		if (!quantity.equals(BigDecimal.ZERO)) {
			quantt = quantity.intValue();
			int min = (int) Math.ceil(quantt + ((double)quantt/30)*7);
			int result1 = (int) Math.ceil((double)min / quantityConvert);
			importVolume = result1*quantityConvert;
			tonCuoiThang = importVolume - quantt;
			ngayTon = ((double)tonCuoiThang / ((double)quantt/30));
			DecimalFormat twoDForm = new DecimalFormat("#.##"); 
			ngayTon =  Double.valueOf(twoDForm.format(ngayTon));
		}else {
			
		}
		details.put("productId", productId);
		details.put("quantity", quantity);
		details.put("thisMonth", thisMonth);
		details.put("importVolume", importVolume);
		details.put("tonCuoiThang", tonCuoiThang);
		details.put("ngayTon", ngayTon);
		listMonthAndQuantity.add(details);
		result.put("listSalesForcasts", listMonthAndQuantity);
		return result;
	}
	public static Map<String, Object> getThisquantityConvert (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String productId = (String)context.get("productId");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("quantityConvert");
		Delegator delegator = ctx.getDelegator();
		BigDecimal quantityConvert = BigDecimal.ZERO;
		List<GenericValue> listPackings = delegator.findList("ConfigPacking",EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), fieldToSelects, null, null, false);	
		for (GenericValue p : listPackings) {
			quantityConvert = (BigDecimal) p.get("quantityConvert");
		}
		result.put("quantityConvert", quantityConvert);
		return result;
	}
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getMonthSalesForcast (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		String thisMonth = (String)context.get("thisMonth");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("salesForecastId");
		fieldToSelects.add("internalPartyId");
		fieldToSelects.add("customTimePeriodId");		
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listSalesForecast = delegator.findList("SalesForecast",EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "internalPartyId", "RSM_GT_VUNG1")), fieldToSelects, null, null, false);
		List<GenericValue> listSalesForecastDetails = new ArrayList<GenericValue>();
		fieldToSelects.clear();
		fieldToSelects.add("salesForecastId");
		fieldToSelects.add("salesForecastDetailId");		
		fieldToSelects.add("quantity");
		fieldToSelects.add("productId");
		for (GenericValue f : listSalesForecast) {
			String salesForecastId = (String) f.get("salesForecastId");
			List<GenericValue> listTempSalesForecastDetails = delegator.findList("SalesForecastDetail",EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", salesForecastId)), fieldToSelects, null, null, false);
			for (GenericValue t : listTempSalesForecastDetails) {
				listSalesForecastDetails.add(t);
			}
		}
		Map<String, Object> quantityAndMonth = new FastMap<String, Object>();
		List<Map> listMonthAndQuantity = new ArrayList<Map>();
		quantityAndMonth.put("thisMonth", thisMonth);
		quantityAndMonth.put("thisSalesForecastDetails", listSalesForecastDetails);
		listMonthAndQuantity.add(quantityAndMonth);
		result.put("month", listMonthAndQuantity);
		return result;
	}
	public static Map<String, Object> getProductCategory (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String productCategoryTypeId = (String)context.get("productCategoryTypeId");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("productCategoryId");
		fieldToSelects.add("categoryName");		
		Delegator delegator = ctx.getDelegator();
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("productCategoryId");
		List<GenericValue> listProductCategorys = delegator.findList("ProductCategory",EntityCondition.makeCondition(UtilMisc.toMap("productCategoryTypeId", productCategoryTypeId)), fieldToSelects, orderBy, null, false);
		result.put("listProductCategorys", listProductCategorys);
		return result;
	}
	public static Map<String, Object> getListOrderItemsAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String orderId = (String)context.get("orderId");
		Delegator delegator = ctx.getDelegator();
		List<String> sortBy = FastList.newInstance();
		sortBy.add("orderItemSeqId");
		List<EntityExpr> exprs = UtilMisc.toList(
	        EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
	        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED")
        );
        EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(exprs, EntityOperator.AND);
		List<GenericValue> listOrderItems = delegator.findList("OrderItem",ecl, null, sortBy, null, false);
		result.put("listOrderItems", listOrderItems);
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> jqxGetListOrderItemsAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
//		String orderId = (String)context.get("orderId");
		Delegator delegator = ctx.getDelegator();
//		EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String agreementId = (String)parameters.get("agreementId")[0];
		List<GenericValue> listAgreeAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
		String orderId="";
		if(!UtilValidate.isEmpty(listAgreeAndOrder)){
			GenericValue agreementAndOrder = EntityUtil.getFirst(listAgreeAndOrder);
			orderId = (String)agreementAndOrder.get("orderId");
		}
//		String orderId = (String)parameters.get("orderId")[0];
		List<String> sortBy = FastList.newInstance();
		sortBy.add("orderItemSeqId");
		List<EntityExpr> exprs = UtilMisc.toList(
	        EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId),
	        EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED")
        );
        EntityConditionList<EntityExpr> ecl = EntityCondition.makeCondition(exprs, EntityOperator.AND);
        listAllConditions.add(ecl);
		List<GenericValue> listOrderItem = delegator.findList("OrderItem",EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, null, false);
		List<Map<String, Object>> listIterator = FastList.newInstance();
		for(GenericValue orderItem : listOrderItem){
			Map<String, Object> map = FastMap.newInstance();
			map.putAll(orderItem);
			map.put("originOrderUnit", (BigDecimal)orderItem.getBigDecimal("quantity"));
			map.put("packingUnit", 0);
			map.put("orderUnit", (BigDecimal)orderItem.getBigDecimal("quantity"));
			listIterator.add(map);
		}
		result.put("listIterator", listIterator);
		return result;
	}
	@SuppressWarnings({ "rawtypes" })
	public static Map<String, Object> getAllMonthSalesForcast (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String productPlanId = (String)context.get("productPlanId");
		String productId2 = (String)context.get("productId");
		String uomToId = (String)context.get("uomToId");
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("quantityConvert");
		fieldToSelects.add("internalName");
		fieldToSelects.add("quantityUomId");
		List<GenericValue> listProducthn = delegator.findList("ConfigPackingAndProduct",EntityCondition.makeCondition(UtilMisc.toMap("productId", productId2, "quantityUomId", uomToId, "uomFromId", "PALLET", "uomToId", uomToId)), fieldToSelects, null, null, false);
		fieldToSelects.clear();
		fieldToSelects.add("productPlanId");
		fieldToSelects.add("parentProductPlanId");
		fieldToSelects.add("customTimePeriodId");
		fieldToSelects.add("productPlanName");
		fieldToSelects.add("customTimePeriodIdOfSales");
		fieldToSelects.add("internalPartyId");
		String areaPlan = "";
		String configEmpy = "";
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("fromDate");
		BigDecimal palet = BigDecimal.ZERO;
		List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanAndCustomTimePeriodForGroup",EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", productPlanId)), fieldToSelects, orderBy, null, false);
		List<GenericValue> listSalesForecast = new ArrayList<GenericValue>();
		fieldToSelects.clear();
		fieldToSelects.add("salesForecastId");
		fieldToSelects.add("internalPartyId");
		fieldToSelects.add("customTimePeriodId");
		List<List<Map>> listMonth = new ArrayList<List<Map>>();
		Set<String> fieldToSelects2 = FastSet.newInstance();
		fieldToSelects2.add("salesForecastId");
		fieldToSelects2.add("salesForecastDetailId");
		fieldToSelects2.add("quantity");
		fieldToSelects2.add("productId");
		Set<String> fieldToSelects1 = FastSet.newInstance();
		fieldToSelects1.add("quantityConvert");
		String isUpdate = "New";
		List<String> orderBy2 = new ArrayList<String>();
		orderBy2.add("salesForecastDetailId");
		int inventoryFocast = 0;
		int FirstInventoryFocast = 0;
		for (GenericValue xc : listProductPlanHeader) {
			String salesId = (String) xc.get("customTimePeriodIdOfSales");
			String thisMonth = (String) xc.get("productPlanName");
			String thisProductPlanId = (String) xc.get("productPlanId");
			areaPlan = (String) xc.get("internalPartyId");
			listSalesForecast = delegator.findList("SalesForecast",EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", salesId, "internalPartyId", areaPlan)), fieldToSelects, null, null, false);
			GenericValue customTimePeriodOfSales = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", salesId), false);
			List<GenericValue> listSalesForecastDetails = new ArrayList<GenericValue>();
			List<Map> listSalesForecastDetails2 = new ArrayList<Map>();
			for (GenericValue f : listSalesForecast) {
				String salesForecastId = (String) f.get("salesForecastId");
				List<GenericValue> listTempSalesForecastDetails = new ArrayList<GenericValue>();
				if (productId2 == null) {
					listTempSalesForecastDetails = delegator.findList("SalesForecastDetail",EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", salesForecastId)), fieldToSelects2, orderBy2, null, false);
				}else {
					listTempSalesForecastDetails = delegator.findList("SalesForecastDetail",EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", salesForecastId, "productId", productId2)), fieldToSelects2, orderBy2, null, false);
				}
				if (listTempSalesForecastDetails.size() > 1) {
					for (int i = listTempSalesForecastDetails.size(); i > 1; i--) {
						int r = i - 1;
						listTempSalesForecastDetails.remove(r);
					}
				}
				for (GenericValue t : listTempSalesForecastDetails) {
					listSalesForecastDetails.add(t);
					String productId = (String)t.get("productId");
					BigDecimal thisQuantity = (BigDecimal)t.get("quantity");
					Calendar calendar = Calendar.getInstance();
					calendar.set(Calendar.HOUR, 0);
				    calendar.set(Calendar.MINUTE, 0);
				    calendar.set(Calendar.SECOND, 0);
				    calendar.set(Calendar.MILLISECOND, 0);
				    calendar.setTime((java.sql.Date) customTimePeriodOfSales.get("fromDate"));
					calendar.add(Calendar.DATE, -1);
					//lay ton kho thang truoc lien ke thang nay
					java.sql.Date curentDate = new java.sql.Date(calendar.getTimeInMillis());
					Set<String> fieldDateDim = FastSet.newInstance();
					fieldDateDim.add("dimensionId");
					List<GenericValue> listDateDim = delegator.findList("DateDimension", EntityCondition.makeCondition("dateValue", curentDate), fieldDateDim, null, null, false);
					GenericValue dateDim = EntityUtil.getFirst(listDateDim);
					List<GenericValue> listProductDim = delegator.findList("ProductDimension", EntityCondition.makeCondition("productId", productId), null, null, null, false);
					GenericValue productDim = EntityUtil.getFirst(listProductDim);
					BigDecimal tonTruoc = BigDecimal.ZERO;
					long tempt = 8000;
					if(dateDim != null && productDim != null){
						long productDimId = (Long) productDim.get("dimensionId");
						long dateDimId = (Long) dateDim.get("dimensionId");
						List<GenericValue> listFacilityFact = delegator.findList("FacilityFact", EntityCondition.makeCondition(UtilMisc.toMap("facilityDimId", tempt, "productDimId", productDimId, "dateDimId", dateDimId)), null, null, null, false);
						GenericValue facilityFact = EntityUtil.getFirst(listFacilityFact);
						if(facilityFact != null){
							tonTruoc = (BigDecimal) facilityFact.get("inventoryTotal");
						}else{
							tonTruoc = BigDecimal.ZERO;
						}
					}
					GenericValue cfpacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId", productId, "uomFromId", "PALLET", "uomToId", uomToId), false);
					boolean aCheck = true;
					if (cfpacking == null) {
						aCheck = false;
						configEmpy = "isEmpty";
					} else {
						palet = (BigDecimal)cfpacking.get("quantityConvert");
						if (palet.equals(BigDecimal.ZERO)) {
							configEmpy = "isEmpty";
						}
					}
					int planImport = 0;
					int tonCuoiThang = 0;
					double ngayTon = 0;
					int pallet = 0;
					String ngayTonS = "";
					String hoanm = "Save";
					List<GenericValue> listPlanItem1 = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", thisProductPlanId, "productId", productId2)), null, null, null, false);
					GenericValue planItem1 = new GenericValue();
					for (GenericValue grv : listPlanItem1) {
						if (grv != null) {
							planItem1 = grv;
						}else {
							aCheck = false;
						}
					}
					if (aCheck) {
						if(tonTruoc != BigDecimal.ZERO){
//							neu co ton thuc te cua thang truoc
							if (!planItem1.isEmpty()) {
//								neu da co du lieu ProductPlanItem
								BigDecimal planImportTemp = (BigDecimal) planItem1.get("planQuantity");
								String productPlanItemSeqId = (String) planItem1.get("productPlanItemSeqId");
//								delegator.storeByCondition("ProductPlanItem", UtilMisc.toMap("inventoryForecast", BigDecimal.ZERO), EntityCondition.makeCondition("productPlanItemSeqId", productPlanItemSeqId));
								planImport = planImportTemp.intValue() + tonTruoc.intValue();
								int thisQuantity2 = thisQuantity.intValue();
								tonCuoiThang = planImport - thisQuantity2;
								int quantityConvert2 = palet.intValue();
								ngayTon = ((double)tonCuoiThang / ((double)thisQuantity2/30));
								BigDecimal thisIventory = (BigDecimal) planItem1.get("inventoryForecast");
								FirstInventoryFocast = inventoryFocast;
								inventoryFocast = tonCuoiThang;
								planImport = planImportTemp.intValue();
								pallet = planImport / quantityConvert2;
								isUpdate = "StoredAndHaveInventory";
							}else {
//								neu chua co du lieu ProductPlanItem
								int quantityConvert = palet.intValue();
								int thisQuantity2 = thisQuantity.intValue();
								double min = (double) thisQuantity2 + ((double)thisQuantity2/30)*7;
								min = min - tonTruoc.doubleValue();
								int result2 = (int) Math.ceil((double)min / quantityConvert);
								planImport = result2*quantityConvert;
								tonCuoiThang = planImport - thisQuantity2 + tonTruoc.intValue();
								ngayTon = ((double)tonCuoiThang / ((double)thisQuantity2/30));
								FirstInventoryFocast = inventoryFocast;
								inventoryFocast = tonCuoiThang;
//								planImport = planImport - tonTruoc.intValue();
								isUpdate = "NewAndHaveInventory";
								pallet = planImport / quantityConvert;
							}
						}else {
//							neu khong co ton thuc te
							if (!planItem1.isEmpty()) {
//								neu da co du lieu ProductPlanItem
								BigDecimal planImportTemp = (BigDecimal) planItem1.get("planQuantity");
								planImport = planImportTemp.intValue()  + inventoryFocast;
								int thisQuantity2 = thisQuantity.intValue();
								tonCuoiThang = planImport - thisQuantity2;
								int quantityConvert2 = palet.intValue();
								ngayTon = ((double)tonCuoiThang / ((double)thisQuantity2/30));
								BigDecimal thisIventory = (BigDecimal) planItem1.get("inventoryForecast");
								FirstInventoryFocast = inventoryFocast;
								inventoryFocast = tonCuoiThang;
								planImport = planImportTemp.intValue();
								pallet = planImport / quantityConvert2;
								hoanm = "Save";
								isUpdate = "Stored";
							}else {
//								neu chua co du lieu ProductPlanItem
								if (palet != BigDecimal.ZERO && thisQuantity != BigDecimal.ZERO) {
									int quantityConvert = palet.intValue();
									int thisQuantity2 = thisQuantity.intValue();
									double min = (double) Math.ceil(thisQuantity2 + ((double)thisQuantity2/30)*7);
 									min = min - inventoryFocast;
									int result2 = (int) Math.ceil((double)min / quantityConvert);
									planImport = result2*quantityConvert;
									tonCuoiThang = planImport - thisQuantity2 + inventoryFocast;
									ngayTon = ((double)tonCuoiThang / ((double)thisQuantity2/30));
									isUpdate = "New";
//									planImport = planImport - inventoryFocast;
									pallet = planImport / quantityConvert;
									FirstInventoryFocast = inventoryFocast;
									inventoryFocast = tonCuoiThang;
								}
							}
						}
					}
					ngayTonS = String.valueOf(ngayTon);
					if (ngayTonS.length() > 4) {
						ngayTonS = ngayTonS.substring(0, 4);
					}
					Map<String, Object> render = new FastMap<String, Object>();
					render.put("inventoryOfMonth", tonTruoc);
					render.put("thisQuantity", thisQuantity);
					render.put("hoanm", hoanm);
					render.put("planImport", planImport);
					render.put("pallet", pallet);
					render.put("tonCuoiThang", tonCuoiThang);
					render.put("ngayTon", ngayTonS);
					render.put("inventoryFocast", FirstInventoryFocast);
					listSalesForecastDetails2.add(render);
				}
			}
			BigDecimal quantityConvertRS = BigDecimal.ZERO;
			String internalName = "";
			String quantityUomId = "";
			GenericValue thisProd = new GenericValue();
			if (listProducthn.isEmpty()) {
				configEmpy = "isEmpty";
			}else {
				thisProd = listProducthn.get(0);
				quantityConvertRS = (BigDecimal) thisProd.get("quantityConvert");
				internalName = (String) thisProd.get("internalName");
				quantityUomId = (String) thisProd.get("quantityUomId");
			}
			Map<String, Object> findUom = new FastMap<String, Object>();
			findUom.put("uomId", quantityUomId);
			GenericValue uom = delegator.findByPrimaryKey("Uom", findUom);
			if (uom != null) {
				quantityUomId = (String) uom.get("description");
			}
			Map<String, Object> quantityAndMonth = new FastMap<String, Object>();
			List<Map> listMonthAndQuantity = new ArrayList<Map>();
			quantityAndMonth.put("quantityConvertRS", palet);
			quantityAndMonth.put("internalName", internalName);
			quantityAndMonth.put("quantityUomId", quantityUomId);
			quantityAndMonth.put("thisMonth", thisMonth);
			quantityAndMonth.put("isUpdate", isUpdate);
			quantityAndMonth.put("thisProductPlanId", thisProductPlanId);
			quantityAndMonth.put("thisSalesForecastDetails", listSalesForecastDetails2);
			listMonthAndQuantity.add(quantityAndMonth);
			listMonth.add(listMonthAndQuantity);
		}
		Map<String, Object> listAndId = new FastMap<String, Object>();
		listAndId.put("listMonth", listMonth);
		listAndId.put("productId", productId2);
		listAndId.put("uomToId", uomToId);
		listAndId.put("configEmpy", configEmpy);
		result.put("month", listAndId);
		return result;
	}
	public static void saveProductPlanItemEventAjax(HttpServletRequest request, HttpServletResponse response) throws GenericEntityException{
		GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
		LocalDispatcher dispatcher = org.ofbiz.service.ServiceDispatcher.getLocalDispatcher("default", delegator);
		JSONArray jsonData = JSONArray.fromObject(request.getParameter("dataProductPlan"));
		String quantityUomId = (String) request.getParameter("quantityUomId");
		String productId = (String) request.getParameter("productId");
		boolean result = true;
		boolean beganTx = TransactionUtil.begin(7200);
		for (int i = 0; i < jsonData.size(); i++) {
			JSONObject thisRow = jsonData.getJSONObject(i);
			String productPlanId = (String) thisRow.get("productPlanId");
			GenericValue productPlanHeader = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId), false);
			String statusProductPlanHeader = "";
			if (UtilValidate.isNotEmpty(productPlanHeader)) {
				statusProductPlanHeader = productPlanHeader.getString("statusId");
				if (statusProductPlanHeader.equals("PLAN_COMPLETED") || statusProductPlanHeader.equals("PLAN_ORDERED") || statusProductPlanHeader.equals("PLAN_PROCESSING")) {
					continue;
				}
			}
			String productPlanItemSeqId = (String) thisRow.get("productPlanItemSeqId");
			String statusId = "PLAN_ITEM_CREATED";
			Object planQuantity = (Integer)thisRow.get("importQuantityRecommend");
			Object inventoryForecast = (Integer)thisRow.get("inventoryForecast");
			
			DecimalFormatSymbols symbols = new DecimalFormatSymbols();
			symbols.setGroupingSeparator(',');
			symbols.setDecimalSeparator('.');
			String pattern = "#,##0.0#";
			DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
			decimalFormat.setParseBigDecimal(true);
			
			try {
				planQuantity = (BigDecimal) decimalFormat.parse(planQuantity.toString());
			} catch (ParseException e) {
				planQuantity = 0;
			}
			try {
				inventoryForecast = (BigDecimal) decimalFormat.parse(inventoryForecast.toString());
			} catch (ParseException e) {
				inventoryForecast = 0;
			}
			if (UtilValidate.isEmpty(productPlanItemSeqId)) {
				productPlanItemSeqId = delegator.getNextSeqId("ProductPlanItem");
			}
			GenericValue productPlanItem = delegator.makeValue("ProductPlanItem", UtilMisc.toMap("productPlanItemSeqId", productPlanItemSeqId, "productPlanId", productPlanId, "productId", productId, "planQuantity", planQuantity, "statusId", statusId, "inventoryForecast", inventoryForecast, "quantityUomId", quantityUomId));
			try {
				delegator.createOrStore(productPlanItem);
				if ((!statusProductPlanHeader.equals("PLAN_CREATED")) || (!statusProductPlanHeader.equals(""))) {
					delegator.storeByCondition("ProductPlanHeader", UtilMisc.toMap("statusId", "PLAN_MODIFIED"), EntityCondition.makeCondition("productPlanId", productPlanId));
				}
			} catch (Exception e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				result = false;
				break;
			}
		}
		TransactionUtil.commit(beganTx);
		if (result) {
			request.setAttribute("RESULT_MESSAGE", "SUSSESS");
		} else {
			request.setAttribute("RESULT_MESSAGE", "ERROR");
		}
	}
	public static Map<String, Object> createPlanItemToDatabaseAjax(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String productPlanId = (String)context.get("productPlanId");
		String	productPlanItemSeqId = delegator.getNextSeqId("ProductPlanItem");
		String productId = (String)context.get("productId");
		BigDecimal planQuantity = (BigDecimal) context.get("planQuantity");
		BigDecimal inventoryForecast = (BigDecimal) context.get("inventoryForecast");
		if (planQuantity == null || inventoryForecast == null) {
			return ServiceUtil.returnError("Error");
		}
		String statusId = (String) context.get("statusId");
		String quantityUomId = (String)context.get("quantityUomId");
		Locale locale = (Locale) context.get("locale");
		Map<String, Object> fields = UtilMisc.toMap("productPlanItemSeqId", productPlanItemSeqId, "productPlanId", productPlanId, "productId", productId, "planQuantity", planQuantity, "statusId", statusId, "inventoryForecast", inventoryForecast, "quantityUomId", quantityUomId);
		Map<String, Object> fields2 = UtilMisc.toMap("planQuantity", planQuantity, "statusId", statusId, "inventoryForecast", inventoryForecast);
		List<GenericValue> checkInList = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId, "productId", productId)), null, null, null, false);
		GenericValue planItem = EntityUtil.getFirst(checkInList);
		Map<String, String> fields41 =  UtilMisc.toMap("productPlanId", productPlanId);
		GenericValue thisPlanHeader = delegator.findOne("ProductPlanHeader", fields41, false);
		String thisPlanStatus = (String) thisPlanHeader.get("statusId");
		boolean beganTx = TransactionUtil.begin(7200);
		if ((!thisPlanStatus.equals("PLAN_CREATED")) || (!thisPlanStatus.equals(""))) {
			//sua plan modified datnv
			Map<String,String> fields4 = UtilMisc.toMap("statusId", "PLAN_MODIFIED");
			try {
				delegator.storeByCondition("ProductPlanHeader", fields4, EntityCondition.makeCondition("productPlanId", productPlanId));
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
			}
		}
		if (planItem == null) {
			try {
				GenericValue newValue = delegator.makeValue("ProductPlanItem", fields);
				delegator.create(newValue);
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
			}
		}else {
			for (GenericValue genericValue : checkInList) {
				String productPlanItemSeqId2 = (String) genericValue.get("productPlanItemSeqId");
				try {
					delegator.storeByCondition("ProductPlanItem", fields2, EntityCondition.makeCondition("productPlanItemSeqId", productPlanItemSeqId2));
				} catch (GenericEntityException e) {
					TransactionUtil.rollback(beganTx, e.getMessage(), e);
					return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
				}
			}
		}
		TransactionUtil.commit(beganTx);
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("mess", "Data Save Done!!");
		return result;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> loadPlanAvailableAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String productPlanId = (String)context.get("productPlanId");
		Set<String> fieldToSelects = FastSet.newInstance();
		List<String> orderBy = new ArrayList<String>();
		orderBy.add("productPlanId");
		fieldToSelects.add("productPlanId");
		fieldToSelects.add("parentProductPlanId");
		fieldToSelects.add("customTimePeriodId");
		fieldToSelects.add("productPlanName");
		fieldToSelects.add("internalPartyId");
		List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanHeader",EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", productPlanId)), fieldToSelects, orderBy, null, false);
		fieldToSelects.clear();
		fieldToSelects.add("productPlanId");
		fieldToSelects.add("productPlanItemSeqId");
		fieldToSelects.add("productId");
		fieldToSelects.add("recentPlanQuantity");
		fieldToSelects.add("planQuantity");
		fieldToSelects.add("primaryProductCategoryId");
		fieldToSelects.add("internalName");
		fieldToSelects.add("quantityUomId");
		fieldToSelects.add("productPackingUomId");
		fieldToSelects.add("productUomId");
		fieldToSelects.add("quantityConvert");
		orderBy.clear();
		orderBy.add("primaryProductCategoryId");
		orderBy.add("internalName");
		List<Map> listMonth = new ArrayList<Map>();
		List<List<GenericValue>> listHoanm = new ArrayList<List<GenericValue>>();
		for (GenericValue gen : listProductPlanHeader) {
			String productPlanId2 = (String)gen.get("productPlanId");
			String productPlanName = (String)gen.get("productPlanName");
			Map<String, Object> listMonthAndId = new FastMap<String, Object>();
			listMonthAndId.put("thisMonth", productPlanName);
			listMonthAndId.put("thisPlanId", productPlanId2);
			listMonth.add(listMonthAndId);
			List<GenericValue> listProduct = delegator.findList("ProductAndProductPlanItem",EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId2)), fieldToSelects, orderBy, null, false);
			listHoanm.add(listProduct);
		}
		List<GenericValue> listProducts = new ArrayList<GenericValue>();
		if (listHoanm != null) {
			listProducts = (List<GenericValue>) listHoanm.get(0);
			int max = listProducts.size();
			for (List<?> f : listHoanm) {
				int thisLength = f.size();
				if (max < thisLength) {
					listProducts = (List<GenericValue>) f;
					max = listProducts.size();
				}
			}
		}
		List<String> maxProducts = new ArrayList<String>();
		Set<String> listCatagory = new HashSet<String>();
		for (GenericValue d : listProducts) {
			String category = (String) d.get("primaryProductCategoryId");
			String maxProduct = (String) d.get("productId");
			listCatagory.add(category);
			maxProducts.add(maxProduct);
		}
		List<String> listCatagorys = new ArrayList<String>();
		List<Map> listProductInThisCatagorys2 = new ArrayList<Map>();
		listCatagorys.addAll(listCatagory);
		Collections.sort(listCatagorys);
		for (String str : listCatagorys) {
			List<Map> listProductInCatagory = new ArrayList<Map>();
			for (GenericValue gv : listProducts) {
				String thisCatalog = (String) gv.get("primaryProductCategoryId");
				if (thisCatalog.equals(str)) {
					String internalName = (String) gv.get("internalName");
					String productPlanIdS = productPlanId;
					String productIdS = (String) gv.get("productId");
					String quantityUomIdS = (String) gv.get("quantityUomId");
					Map<String, Object> ctlg = new FastMap<String, Object>();
					ctlg.put("internalName", internalName);
					ctlg.put("productPlanIdS", productPlanIdS);
					ctlg.put("productIdS", productIdS);
					ctlg.put("quantityUomIdS", quantityUomIdS);
					listProductInCatagory.add(ctlg);
				}
			}
			Map<String, Object> mapInThisCategory = new FastMap<String, Object>();
			mapInThisCategory.put("catagory", str);
			mapInThisCategory.put("listProductInCatagorys", listProductInCatagory);
			listProductInThisCatagorys2.add(mapInThisCategory);
		}
		Map<String, Object> listProductCategoryAndMonth = new FastMap<String, Object>();
		listProductCategoryAndMonth.put("listMonth", listMonth);
		listProductCategoryAndMonth.put("listProducts2", listHoanm);
		listProductCategoryAndMonth.put("listProductCategory", listProductInThisCatagorys2);
		result.put("listPlanAvailable", listProductCategoryAndMonth);
		return result;
	}
	public static Map<String, Object> StoreProductQuality(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String	contentId = delegator.getNextSeqId("Content");
		Locale locale = (Locale) context.get("locale");
		String contentTypeId = "DOCUMENT_QA_PUBLISH";
		String statusId = "CTNT_AVAILABLE";
		String contentName =  (String)context.get("productQualityName");
		//data for insert to DataResource
		String dataResourceId = delegator.getNextSeqId("DataResource");
		String dataResourceTypeId = "IMAGE_OBJECT";
		String dataResourceName = "demo";
		String objectInfo = "ofbiz/imageDocumet/scanfile.jpg"; // this link just for fun.
		//check has data in ContentType
		GenericValue contentType = delegator.findOne("ContentType", UtilMisc.toMap("contentTypeId", contentTypeId), false);
		boolean beganTx = TransactionUtil.begin(7200);
		if (contentType == null) {
			Map<String, String> fieldsContentType = UtilMisc.toMap("contentTypeId", contentTypeId, "description", "ContentType for QA Quantity product publish");
			try {
				GenericValue newContentType = delegator.makeValue("ContentType", fieldsContentType);
				delegator.create(newContentType);
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
			}
		}
		Map<String, String> fields = UtilMisc.toMap("dataResourceId", dataResourceId, "dataResourceTypeId", dataResourceTypeId, "statusId", statusId, "dataResourceName", dataResourceName, "objectInfo", objectInfo);
		Map<String, String> fieldsContent = UtilMisc.toMap("contentId", contentId, "contentTypeId", contentTypeId, "dataResourceId", dataResourceId, "statusId", statusId, "contentName", contentName);
		try {
			GenericValue newValue = delegator.makeValue("DataResource",	fields);
			delegator.create(newValue);
			GenericValue newContent = delegator.makeValue("Content", fieldsContent);
			delegator.create(newContent);
		} catch (GenericEntityException e) {
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource,
					"CommonNoteCannotBeUpdated",
					UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		List<String> needSave = Arrays.asList("productId", "shelfLife", "fromDate", "thruDate", "shelfLifeUnit");
		for (String string : needSave) {
			String fiel = (String)context.get(string);
			Map<String, String> fieldsContentAttribute = UtilMisc.toMap("contentId", contentId, "attrName", string, "attrValue", fiel);
			try {
				GenericValue newContentAttribute = delegator.makeValue("ContentAttribute", fieldsContentAttribute);
				delegator.create(newContentAttribute);
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
			}
		}
		TransactionUtil.commit(beganTx);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("contentId", contentId);
		result.put("dataResourceId", dataResourceId);
		return result;
	}
	public static Map<String, Object> createContentForQualityPublication(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String	contentId = delegator.getNextSeqId("Content");
		Map<String, String> fieldsContent = UtilMisc.toMap("contentId", contentId);
		try {
			GenericValue newContent = delegator.makeValue("Content", fieldsContent);
			delegator.create(newContent);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("contentId", contentId);
		return result;
	}
	public static Map<String, Object> createLocationFacilityAjax(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String locationId = delegator.getNextSeqId("LocationFacility");
		String facilityId = (String)context.get("facilityId");
		String parentLocationId = (String)context.get("parentLocationId");
		String locationCode = (String)context.get("locationCode");
		String locationFacilityTypeId = (String)context.get("locationFacilityTypeId");
		String description = (String)context.get("description");
		Map<String, String> fieldsLocationFacility = UtilMisc.toMap("locationId", locationId, "facilityId", facilityId, "parentLocationId", parentLocationId, "locationCode", locationCode, "locationFacilityTypeId", locationFacilityTypeId, "description", description);
		try {
			GenericValue newLocationFacility = delegator.makeValue("LocationFacility", fieldsLocationFacility);
			delegator.create(newLocationFacility);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		Map<String, Object> result = FastMap.newInstance();
		result.put("locationId", locationId);
		return result;
	}
	public static Map<String, Object> updateLocationFacilityAjax(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> result = FastMap.newInstance();
		Locale locale = (Locale) context.get("locale");
		String locationId = (String)context.get("locationId");
		String locationCode = (String)context.get("locationCode");
		String description = (String)context.get("description");
		Map<String, String> fieldsLocationFacility = UtilMisc.toMap("locationId", locationId, "locationCode", locationCode, "description", description);
		boolean success = true;
		try {
			GenericValue newLocationFacility = delegator.makeValue("LocationFacility", fieldsLocationFacility);
			delegator.store(newLocationFacility);
		} catch (GenericEntityException e) {
			success = false;
		} finally {
			result.put("success", success);
		}
		return result;
	}
//	public static Map<String, Object> createBillOfLading(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
//		Delegator delegator = ctx.getDelegator();
//		Map<String, Object> result = FastMap.newInstance();
//		Locale locale = (Locale) context.get("locale");
//		String billNumber = (String)context.get("billNumber");
//		List<GenericValue> listBillOfLading = delegator.findList("BillOfLading",EntityCondition.makeCondition(UtilMisc.toMap("billNumber", billNumber)), null, null, null, false);
//		if (UtilValidate.isNotEmpty(listBillOfLading)) {
//			GenericValue thisBillOfLading = EntityUtil.getFirst(listBillOfLading);
//			result.put("billId", (String)thisBillOfLading.get("billId"));
//			return result;
//		}
//		String billId = delegator.getNextSeqId("BillOfLading");
//		String partyIdFrom = (String)context.get("partyIdFrom");
//		String partyIdTo = (String)context.get("partyIdTo");
//		Timestamp departureDate = (Timestamp) context.get("departureDate");
//		Timestamp arrivalDate = (Timestamp) context.get("arrivalDate");
//		Map<String, Object> fieldsBillOfLading = UtilMisc.toMap("billId", billId, "billNumber", billNumber, "partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "departureDate", departureDate, "arrivalDate", arrivalDate);
//		try {
//			GenericValue newContent = delegator.makeValue("BillOfLading", fieldsBillOfLading);
//			delegator.create(newContent);
//		} catch (GenericEntityException e) {
//			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
//		}
//		result.put("billId", billId);
//		return result;
//	}
//	public static Map<String, Object> createBillOfLading(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
//		Delegator delegator = ctx.getDelegator();
//		Map<String, Object> result = FastMap.newInstance();
//		Locale locale = (Locale) context.get("locale");
//		String billNumber = (String)context.get("billNumber");
//		String partyIdFrom = (String)context.get("partyIdFrom");
//		String partyIdTo = (String)context.get("partyIdTo");
//		Timestamp departureDate = (Timestamp) context.get("departureDate");
//		Timestamp arrivalDate = (Timestamp) context.get("arrivalDate");
//		
//		List<GenericValue> listBillOfLading = delegator.findList("BillOfLading",EntityCondition.makeCondition(UtilMisc.toMap("billNumber", billNumber,"partyIdFrom", partyIdFrom, "departureDate", departureDate)), null, null, null, false);
//		if (UtilValidate.isNotEmpty(listBillOfLading)) {
//			GenericValue thisBillOfLading = EntityUtil.getFirst(listBillOfLading);
//			result.put("billId", (String)thisBillOfLading.get("billId"));
//			return result;
//		}else{
//			String billId = delegator.getNextSeqId("BillOfLading");
//			Map<String, Object> fieldsBillOfLading = UtilMisc.toMap("billId", billId, "billNumber", billNumber, "partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "departureDate", departureDate, "arrivalDate", arrivalDate);
//			try {
//				GenericValue newContent = delegator.makeValue("BillOfLading", fieldsBillOfLading);
//				delegator.create(newContent);
//			} catch (GenericEntityException e) {
//				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
//			}
//			result.put("billId", billId);
//			return result;
//		}
//	}
	public static Map<String, Object> updateBillOfLading(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String billNumber = (String)context.get("billNumber");
		String billId = (String)context.get("billId");
		String partyIdFrom = (String)context.get("partyIdFrom");
		String partyIdTo = (String)context.get("partyIdTo");
		Timestamp departureDate = (Timestamp) context.get("departureDate");
		Timestamp arrivalDate = (Timestamp) context.get("arrivalDate");
		Map<String, ?> fieldsBillOfLading = UtilMisc.toMap("billNumber", billNumber, "partyIdFrom", partyIdFrom, "partyIdTo", partyIdTo, "departureDate", departureDate, "arrivalDate", arrivalDate);
		try {
			delegator.storeByCondition("BillOfLading", fieldsBillOfLading, EntityCondition.makeCondition("billId", billId));
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	public static Map<String, Object> updateQuotaItemAjax(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String quotaId = (String)context.get("quotaId");
		String quotaItemSeqId = (String)context.get("quotaItemSeqId");
		BigDecimal quantityAvailable = (BigDecimal)context.get("quantityAvailable");
		Map<String, ?> fieldsQuotaItem = UtilMisc.toMap("quantityAvailable", quantityAvailable);
		try {
			delegator.storeByCondition("QuotaItem", fieldsQuotaItem, EntityCondition.makeCondition(UtilMisc.toMap("quotaId", quotaId, "quotaItemSeqId", quotaItemSeqId)));
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateStatusListAgreements(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String statusId = (String)context.get("statusId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		List<String> listAgreementId = (List<String>) context.get("agreementId[]");
		Map<String, ?> fieldsAgreement = UtilMisc.toMap("statusId", statusId);
		boolean beganTx = TransactionUtil.begin(7200);
		for (String strAgreementId : listAgreementId) {
			Map<String,Object> contextTmp = new HashMap<String, Object>();
			contextTmp.put("agreementId", strAgreementId);
			contextTmp.put("statusId", statusId);
			contextTmp.put("userLogin", userLogin);
			LocalDispatcher dispatcher = ctx.getDispatcher();
			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", strAgreementId), false);
			String curSttAgreement = (String)agreement.get("statusId");
			if(curSttAgreement.equals("AGREEMENT_CREATED") && statusId.equals("AGREEMENT_APPROVED")){
				try {
					dispatcher.runSync("updateAgreement", contextTmp);
				} catch (GenericServiceException e) {
					TransactionUtil.rollback(beganTx, e.getMessage(), e);
					return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
				}
//					List<GenericValue> listAgreementAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", strAgreementId)), null, null, null, false);
//					List<GenericValue> listAgreementAndOrderFilter = EntityUtil.filterByDate(listAgreementAndOrder);
//					for(GenericValue agreementAndOrder : listAgreementAndOrderFilter){
//						String orderId = (String)agreementAndOrder.get("orderId");
//						Map<String,Object> contextTmpOrder = new HashMap<String, Object>();
//						contextTmpOrder.put("statusId", "ORDER_APPROVED");
//						contextTmpOrder.put("setItemStatus", "Y");
//						contextTmpOrder.put("orderId", orderId);
//						contextTmpOrder.put("userLogin", userLogin);
//						try {
//							dispatcher.runSync("changeOrderStatus", contextTmpOrder);
//						} catch (GenericServiceException e) {
//							return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
//						}
//					}
			}
			
		}
		TransactionUtil.commit(beganTx);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateStatusListAgreementsJava(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String statusId = (String)context.get("statusId");
		List<String> listAgreementId = (List<String>) context.get("agreementId");
		Map<String, ?> fieldsAgreement = UtilMisc.toMap("statusId", statusId);
		try {
			for (String strAgreementId : listAgreementId) {
				delegator.storeByCondition("Agreement", fieldsAgreement, EntityCondition.makeCondition("agreementId", strAgreementId));
			}
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	public static Map<String, Object> saveQuotaHeaderAjax(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String	quotaId = delegator.getNextSeqId("QuotaHeader");
		Locale locale = (Locale) context.get("locale");
		String quotaName = (String)context.get("quotaName");
		Timestamp fromDate =  (Timestamp)context.get("fromDate");
		Timestamp thruDate =  (Timestamp)context.get("thruDate");
		String quotaTypeId = "IMPORT_QUOTA";
		Map<String, Object> fields = UtilMisc.toMap("quotaId", quotaId, "quotaName", quotaName, "quotaTypeId", quotaTypeId, "description", quotaName, "fromDate", fromDate, "thruDate", thruDate);
		try {
			GenericValue newQuotaHeader = delegator.makeValue("QuotaHeader", fields);
			delegator.create(newQuotaHeader);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("quotaId", quotaId);
		return result;
	}
	public static Map<String, Object> saveQuotaItemAjax(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String	quotaItemSeqId = delegator.getNextSeqId("QuotaItem");
		Locale locale = (Locale) context.get("locale");
		String quotaId = (String)context.get("quotaId");
		String productId = (String)context.get("productId");
		String productName = (String)context.get("productName");
		BigDecimal quotaQuantity = (BigDecimal)context.get("quotaQuantity");
		String quantityUomId = (String)context.get("quantityUomId");
		Timestamp fromDate =  (Timestamp)context.get("fromDate");
		Timestamp thruDate =  (Timestamp)context.get("thruDate");
		BigDecimal quantityAvailable = BigDecimal.ZERO;
		Map<String, Object> fields = UtilMisc.toMap("quotaId", quotaId, "quotaItemSeqId", quotaItemSeqId, "productId", productId, "productName", productName, "quotaQuantity", quotaQuantity, "quantityUomId", quantityUomId, "fromDate", fromDate, "thruDate", thruDate, "quantityAvailable", quantityAvailable);
		try {
			GenericValue newQuotaItem = delegator.makeValue("QuotaItem", fields);
			delegator.create(newQuotaItem);
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	public static Map<String, Object> updateDataSourceAjax(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String dataResourceId = (String)context.get("dataResourceId");
		String dataResourceName = (String)context.get("dataResourceName");
		String objectInfo = (String)context.get("objectInfo");
		Map<String, ?> fields = UtilMisc.toMap("dataResourceName", dataResourceName, "objectInfo", objectInfo);
		try {
			delegator.storeByCondition("DataResource", fields, EntityCondition.makeCondition("dataResourceId", dataResourceId));
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	public static Map<String, Object> createAgreementToBillAjax(DispatchContext ctx, Map<String, ?> context) throws GenericServiceException, GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		Map<String, Object> result = FastMap.newInstance();
		String billId = (String)context.get("billId");
		String agreementId = (String)context.get("agreementId");
		String billNumber= (String)context.get("billNumber");
		GenericValue billAndAgreement = delegator.makeValue("BillOfLadingAndAgreement");
		String billAgreementId = delegator.getNextSeqId("BillOfLadingAndAgreement");
		billAndAgreement.put("billAgreementId", billAgreementId);
		billAndAgreement.put("billId", billId);
		billAndAgreement.put("agreementId", agreementId);
		boolean beganTx = TransactionUtil.begin(7200);
		try {
			delegator.create(billAndAgreement);
		} catch (GenericEntityException e) {
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
		}
		
		GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
		agreement.put("statusId", "AGREEMENT_PROCESSING");
		try {
			delegator.store(agreement);
		} catch (GenericEntityException e) {
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
		}
		Timestamp agreementDate = (Timestamp)agreement.get("agreementDate");
		Long agreementDateLong = agreementDate.getTime();
//		result.put("billAgreementId", billAgreementId);
		//get containerId, containerNumber, orderId
		List<GenericValue> listAgreementAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
		List<Map<String, Object>> listNewAgreementAdded = FastList.newInstance();
		for(GenericValue agreementAndOrder : listAgreementAndOrder){
			Map<String, Object> mapRow = new FastMap<String, Object>();
			String orderId = (String)agreementAndOrder.get("orderId");
			//create new container
			GenericValue container = delegator.makeValue("Container");
			String containerIdNew = delegator.getNextSeqId("Container");
			container.put("containerId", containerIdNew);
			container.put("containerTypeId", "STANDARD_CONTAINER");
			try {
				delegator.create(container);
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				break;
			}
			//create new orderandcontainer
			GenericValue newOrderAndContainer = delegator.makeValue("OrderAndContainer");
   			newOrderAndContainer.put("orderId", orderId);
   			newOrderAndContainer.put("containerId", containerIdNew);
   			newOrderAndContainer.put("billId", billId);
   			Timestamp fromDate = new Timestamp(System.currentTimeMillis());
   			newOrderAndContainer.put("fromDate", fromDate);
   			try {
				delegator.create(newOrderAndContainer);
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				break;
			}
			
   			mapRow.put("containerId", containerIdNew);
			mapRow.put("billAgreementId", billAgreementId);
			mapRow.put("orderId", orderId);
			mapRow.put("agreementDate", agreementDateLong);
			listNewAgreementAdded.add(mapRow);
		}
		TransactionUtil.commit(beganTx);
		result.put("resultListAgreement", listNewAgreementAdded);
		Set<String> fieldToSelects = FastSet.newInstance();
    	fieldToSelects.add("agreementId");
    	fieldToSelects.add("attrValue");
    	List<GenericValue> listAgreementNotBill = delegator.findList("AgreementAndAgreementAttribute", EntityCondition.makeCondition(UtilMisc.toMap("statusId", "AGREEMENT_SENT", "agreementTypeId", "PURCHASE_AGREEMENT","attrName", "AGREEMENT_NAME")), fieldToSelects, null, null, false);
    	result.put("listAgreementNotBill", listAgreementNotBill);
		return result;
	}
	
	@SuppressWarnings("static-access")
	public static Map<String, Object> deleteAgreementFromBillAjax(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		String jsonData = (String)context.get("data");
		JSONArray jsonArr = new JSONArray().fromObject(jsonData);
		boolean beganTx = TransactionUtil.begin(7200);
		for(int i = 0; i < jsonArr.size(); i++){
			JSONObject rowData = jsonArr.getJSONObject(i);
			String agreementId = (String)rowData.get("agreementId");
			String billAgreementId = (String)rowData.get("billAgreementId");
			String billId = (String)rowData.get("billId");
			String orderId = (String)rowData.get("orderId");
			String containerId = (String)rowData.get("containerId");
			String statusId = (String)rowData.get("statusId");
			
			GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
			String curSttAgreement = (String)agreement.get("statusId");
			if(curSttAgreement.equals("AGREEMENT_PROCESSING")){
				agreement.put("statusId", "AGREEMENT_SENT");
				try {
					delegator.store(agreement);
				} catch (GenericEntityException e) {
					TransactionUtil.rollback(beganTx, e.getMessage(), e);
					break;
				}
			}
			GenericValue bOLAndAgreement = delegator.findOne("BillOfLadingAndAgreement", UtilMisc.toMap("billAgreementId", billAgreementId), false);
			if(bOLAndAgreement != null){
				try {
					delegator.removeValue(bOLAndAgreement);
				} catch (GenericEntityException e) {
					TransactionUtil.rollback(beganTx, e.getMessage(), e);
					break;
				}
			}
			//remove orderandcontainer, container
			GenericValue orderAndContainer = delegator.findOne("OrderAndContainer", UtilMisc.toMap("orderId", orderId, "billId", billId), false);
			if(orderAndContainer != null){
				try {
					delegator.removeValue(orderAndContainer);
				} catch (GenericEntityException e) {
					TransactionUtil.rollback(beganTx, e.getMessage(), e);
					break;
				}
			}
			GenericValue container = delegator.findOne("Container", UtilMisc.toMap("containerId", containerId), false);
			if(container != null){
				try {
					delegator.removeValue(container);
				} catch (GenericEntityException e) {
					TransactionUtil.rollback(beganTx, e.getMessage(), e);
					break;
				}
			}
			
		}
		TransactionUtil.commit(beganTx);
		Map<String, Object> result = FastMap.newInstance();
		Set<String> fieldToSelects = FastSet.newInstance();
    	fieldToSelects.add("agreementId");
    	fieldToSelects.add("attrValue");
    	List<GenericValue> listAgreementNotBill = delegator.findList("AgreementAndAgreementAttribute", EntityCondition.makeCondition(UtilMisc.toMap("statusId", "AGREEMENT_SENT", "agreementTypeId", "PURCHASE_AGREEMENT","attrName", "AGREEMENT_NAME")), fieldToSelects, null, null, false);
    	result.put("listAgreementNotBill", listAgreementNotBill);
		return result;
	}
	@SuppressWarnings("rawtypes")
	public static Map<String, Object> getlistProductQualityAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String contentTypeId = (String)context.get("contentTypeId");
		String contentId0 = (String)context.get("contentId");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("contentId");
		fieldToSelects.add("dataResourceId");
		fieldToSelects.add("statusId");
		fieldToSelects.add("contentName");
		Delegator delegator = ctx.getDelegator();
		List<String> oderBy = new ArrayList<String>();
		oderBy.add("contentId");
		List<Map> listProductQualitys = new ArrayList<Map>();
		List<GenericValue> listProductQuality = new ArrayList<GenericValue>();
		if (contentId0.equals("nodata")) {
			listProductQuality = delegator.findList("Content",EntityCondition.makeCondition(UtilMisc.toMap("contentTypeId", contentTypeId)), fieldToSelects, oderBy, null, false);
		} else {
			listProductQuality = delegator.findList("Content",EntityCondition.makeCondition(UtilMisc.toMap("contentTypeId", contentTypeId, "contentId", contentId0)), fieldToSelects, oderBy, null, false);
		}
		List<GenericValue> listContentAttribute = new ArrayList<GenericValue>();
		for (GenericValue g : listProductQuality) {
			Map<String, Object> map = new FastMap<String, Object>();
			String contentId = (String) g.get("contentId");
			String dataResourceId = (String) g.get("dataResourceId");
			String statusId = (String) g.get("statusId");
			GenericValue temp1 = delegator.findOne("StatusItem", UtilMisc.toMap("statusId", statusId), false);
			statusId = (String) temp1.get("description");
			String contentName = (String) g.get("contentName");
			listContentAttribute = delegator.findList("ContentAttribute",EntityCondition.makeCondition(UtilMisc.toMap("contentId", contentId)), null, null, null, false);
			for (GenericValue r : listContentAttribute) {
				String key = (String) r.get("attrName");
				String value = (String) r.get("attrValue");
				if (key.equals("productId")) {
					GenericValue temp2 = delegator.findOne("Product", UtilMisc.toMap("productId", value), false);
					value = (String) temp2.get("internalName");
				}
				map.put(key, value);
			}
			map.put("contentId", contentId);
			map.put("dataResourceId", dataResourceId);
			map.put("statusId", statusId);
			map.put("contentName", contentName);
			listProductQualitys.add(map);
		}
		result.put("listProductQuality", listProductQualitys);
		return result;
	}
	public static Map<String, Object> getPathFileScanAjax (DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String dataResourceId = (String)context.get("dataResourceId");
		Delegator delegator = ctx.getDelegator();
		GenericValue dataResource = delegator.findOne("DataResource", UtilMisc.toMap("dataResourceId", dataResourceId), false);
		String objectInfo = (String) dataResource.get("objectInfo");
		result.put("objectInfo", objectInfo);
		return result;
	}
	public static Map<String, Object> SaveQuotaService(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		String	contentId = delegator.getNextSeqId("Content");
		Locale locale = (Locale) context.get("locale");
		String contentTypeId = "DOCUMENT_QUOTA";
		String statusId = "CTNT_AVAILABLE";
		String contentName =  (String)context.get("quotaName");
		//data for insert to DataResource
		String dataResourceId = delegator.getNextSeqId("DataResource");
		String dataResourceTypeId = "IMAGE_OBJECT";
		String dataResourceName = "demo";
		String objectInfo = "ofbiz/imageDocumet/scanfile.jpg"; // this link just for fun.
		//check has data in ContentType
		GenericValue contentType = delegator.findOne("ContentType", UtilMisc.toMap("contentTypeId", contentTypeId), false);
		boolean beganTx = TransactionUtil.begin(7200);
		if (contentType == null) {
			Map<String, ?> fieldsContentType = UtilMisc.toMap("contentTypeId", contentTypeId, "description", "ContentType for QA Quantity product publish");
			try {
				GenericValue newContentType = delegator.makeValue("ContentType", fieldsContentType);
				delegator.create(newContentType);
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
			}
		}
		Map<String, ?> fields = UtilMisc.toMap("dataResourceId", dataResourceId, "dataResourceTypeId", dataResourceTypeId, "statusId", statusId, "dataResourceName", dataResourceName, "objectInfo", objectInfo);
		Map<String, ?> fieldsContent = UtilMisc.toMap("contentId", contentId, "contentTypeId", contentTypeId, "dataResourceId", dataResourceId, "statusId", statusId, "contentName", contentName);
		try {
			GenericValue newValue = delegator.makeValue("DataResource",	fields);
			delegator.create(newValue);
			GenericValue newContent = delegator.makeValue("Content", fieldsContent);
			delegator.create(newContent);
		} catch (GenericEntityException e) {
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		List<String> needSave = Arrays.asList("supplier", "fromDate", "thruDate");
		for (String string : needSave) {
			String fiel = (String)context.get(string);
			Map<String, ?> fieldsContentAttribute = UtilMisc.toMap("contentId", contentId, "attrName", string, "attrValue", fiel);
			try {
				GenericValue newContentAttribute = delegator.makeValue("ContentAttribute", fieldsContentAttribute);
				delegator.create(newContentAttribute);
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
			}
		}
		TransactionUtil.commit(beganTx);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		result.put("contentId", contentId);
		result.put("dataResourceId", dataResourceId);
		return result;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> updateStatusPlanAjax(DispatchContext ctx, Map<String, ?> context) throws GenericTransactionException {
		Delegator delegator = ctx.getDelegator();
		Locale locale = (Locale) context.get("locale");
		List<String> productPlanId = (List<String>) context.get("productPlanId[]");
		String check = (String) context.get("check");
		boolean check2 = Boolean.parseBoolean(check);
		String statusId = (String)context.get("statusId");
		Map<String, ?> fields = UtilMisc.toMap("statusId", statusId);
		boolean beganTx = TransactionUtil.begin(7200);
		for (String strProductPlanId : productPlanId) {
			List<EntityCondition> condition = new ArrayList<EntityCondition>();
			condition.add(EntityCondition.makeCondition("parentProductPlanId", EntityOperator.EQUALS,strProductPlanId));
			condition.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "PLAN_ORDERED"));
			condition.add(EntityCondition.makeCondition("statusId",EntityOperator.NOT_EQUAL, "PLAN_PROCESSING"));
			condition.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"PLAN_COMPLETED"));
			condition.add(EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL,"PLAN_CONT_CREATED"));
			
			try {
				delegator.storeByCondition("ProductPlanHeader", fields, EntityCondition.makeCondition("productPlanId", strProductPlanId));
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				break;
			}
			try {
				delegator.storeByCondition("ProductPlanHeader", fields, EntityCondition.makeCondition(condition, EntityOperator.AND));
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				break;
			}
		}
		TransactionUtil.commit(beganTx);
		Map<String, Object> result = new FastMap<String, Object>();
		result.put("mess", "Data Save Done!!");
		return result;
	}
	public static Map<String, Object> getAgreementName(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String agreementId = (String)context.get("agreementId");
		Delegator delegator = ctx.getDelegator();
		GenericValue thisAgreement = delegator.findOne("AgreementAndAgreementAttribute", UtilMisc.toMap("agreementId", agreementId, "attrName", "AGREEMENT_NAME"), false);
		String agreementName = (String) thisAgreement.get("attrValue");
		result.put("agreementName", agreementName);
		return result;
	}
	public static Map<String, Object> createEmailAgrrement(DispatchContext dctx, Map<String, Object> context) throws GenericServiceException{
    	LocalDispatcher dispatcher = dctx.getDispatcher();
    	Timestamp fromDate = (Timestamp) context.get("fromDate");
    	Timestamp thruDate = (Timestamp) context.get("thruDate");
			Map<String, Object> bodyParameters = FastMap.newInstance();
			Map<String, Object> emailCtx = FastMap.newInstance();
			bodyParameters.put("fromDate", fromDate);
			bodyParameters.put("thruDate", thruDate);
			emailCtx.put("userLogin", context.get("userLogin"));
			emailCtx.put("locale", context.get("locale"));
			emailCtx.put("sendTo", "hoaminhit@gmail.com");
			emailCtx.put("partyIdTo", "importadmin");
			emailCtx.put("bodyParameters", bodyParameters);
			emailCtx.put("authUser", "hoaminhit@gmail.com");
			emailCtx.put("authPass", "abcdef");
			emailCtx.put("sendFrom", "hoaminhit@gmail.com");
			emailCtx.put("emailTemplateSettingId", "IMPORT_AGREEMENT");
		    dispatcher.runSync("sendMailFromTemplateSetting", emailCtx);
    	return ServiceUtil.returnSuccess();
    }
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> createBirtEmailAgrrement(DispatchContext dctx, Map<String, Object> context) throws GenericServiceException{
		 LocalDispatcher dispatcher = dctx.getDispatcher();
	        GenericValue userLogin = (GenericValue) context.get("userLogin");
//	        String emailType = (String) context.get("emailType");
	        List<String> agreementIdList = (List<String>) context.get("agreementId[]");
	        String bodyText = (String) context.get("bodyText");
	        String sendTo = (String) context.get("sendTo");
	        String subject = (String) context.get("subject");
	        String authUser = (String) context.get("authUser");
	        String authPass = (String) context.get("authPass");
	        List<String> listName = new ArrayList<String>();
	        List<Map> bodyParametersList = new ArrayList<Map>();
	        Map<String, Object> sendMap = FastMap.newInstance();
	        for (String string : agreementIdList) {
				String agreementId = string;
				listName.add("Agrrement_" + agreementId);
		        Map<String, Object> bodyParameters = UtilMisc.<String, Object>toMap("agreementId", agreementId, "userLogin", userLogin, "partyId", "importadmin");
		        bodyParametersList.add(bodyParameters);
			}
	        sendMap.put("xslfoAttachScreenLocation", "component://delys/widget/import/ImportScreens.xml#PrintPurchaseAgreementENG");
	        sendMap.put("attachmentNameList", listName);
	        sendMap.put("bodyParametersList", bodyParametersList);
	        sendMap.put("bodyText", bodyText);
	        sendMap.put("userLogin", userLogin);
	        sendMap.put("subject", subject);
	        sendMap.put("sendFrom", authUser);
	        sendMap.put("sendTo", sendTo);
	        sendMap.put("authUser", authUser);
	        sendMap.put("authPass", authPass);
	        Map<String, Object> sendResp = new FastMap<String, Object>();
	        try {
	        	sendResp = dispatcher.runSync("sendMailFromScreenCustom", sendMap);
	        } catch (Exception e) {
	        	return ServiceUtil.returnError("Can not sent Email");
	        }
	        if (sendResp != null && !ServiceUtil.isError(sendResp)) {
//	            sendResp.put("emailType", emailType);
	        }
//	        List<String> paramAgreemntUpdate = new ArrayList<String>();
//	        paramAgreemntUpdate.addAll(agreementIdList);
	        Map<String, Object> agreementParam = FastMap.newInstance();
	        agreementParam.put("userLogin", userLogin);
	        agreementParam.put("agreementId", agreementIdList);
	        agreementParam.put("statusId", "AGREEMENT_SENT");
	        Map<String, Object> updateAgreementStatus = new FastMap<String, Object>();
	        updateAgreementStatus = dispatcher.runSync("updateStatusListAgreementsJava", agreementParam);
	        return sendResp;
   }
	public static Map<String, Object> updateUom(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String uomId = (String)context.get("uomId");
		String abbreviation = (String)context.get("abbreviation");
		String description = (String)context.get("description");
		try {
			delegator.storeByCondition("Uom", UtilMisc.toMap("abbreviation", abbreviation, "description", description), EntityCondition.makeCondition(UtilMisc.toMap("uomId", uomId)));
		} catch (GenericEntityException e) {
			result = ServiceUtil.returnError("update Uom error");
		}
		return result;
	}
	public static Map<String, Object> createUom(DispatchContext ctx, Map<String, Object> context) {
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		String uomId = (String)context.get("uomId");
		String uomTypeId = (String)context.get("uomTypeId");
		String abbreviation = (String)context.get("abbreviation");
		String description = (String)context.get("description");
		try {
			delegator.create("Uom", UtilMisc.toMap("uomId", uomId, "uomTypeId", uomTypeId, "abbreviation", abbreviation, "description", description));
		} catch (GenericEntityException e) {
			result = ServiceUtil.returnError("update Uom error");
		}
		return result;
	}
	public static Map<String, Object> getContainerID(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String orderId = (String)context.get("orderId");
		Delegator delegator = ctx.getDelegator();
		String containerId = "";
		List<GenericValue> listContainer = delegator.findList("OrderAndContainer",EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		if (UtilValidate.isNotEmpty(listContainer)) {
			GenericValue thisContainer = listContainer.get(0);
			containerId = (String) thisContainer.get("containerId");
		}
		result.put("containerId", containerId);
		return result;
	}
	public static Map<String, Object> getProductStoreID(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		String orderId = (String)context.get("orderId");
		Delegator delegator = ctx.getDelegator();
		String productStoreId = "";
		GenericValue thisProductStore = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
		if (UtilValidate.isNotEmpty(thisProductStore)) {
			productStoreId = (String) thisProductStore.get("productStoreId");
		}
		result.put("productStoreId", productStoreId);
		return result;
	}
	public static Map<String, Object> getImportPlanAjax(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String date = (String)context.get("date");
		String productPlanId = "";
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("productPlanId");
		fieldToSelects.add("fromDate");
		List<GenericValue> listPlanHeader = delegator.findList("ProductPlanAndCustomTimePeriod", EntityCondition.makeCondition("parentProductPlanId", EntityOperator.EQUALS, null), fieldToSelects, null, null, false);
		for (GenericValue x : listPlanHeader) {
			java.sql.Date thisDate = (java.sql.Date) x.get("fromDate");
			String thisDateStr = String.valueOf(thisDate);
			String thisYear = thisDateStr.split("-")[0];
			if (date.equals(thisYear)) {
				productPlanId = (String) x.get("productPlanId");
				result.put("productPlanId", productPlanId);
				return result;
			}
		}
		result.put("productPlanId", "PlanNotFound");
		return result;
	}
	public static Map<String, Object> getInfoConfigPackingAjax(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		String productId = (String)context.get("productId");
		String uomFromId = (String)context.get("uomFromId");
		GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
		String uomToId = product.getString("quantityUomId");
		GenericValue uomTypeAndConfigPacking = delegator.findOne("UomTypeAndConfigPacking", UtilMisc.toMap("uomId", uomFromId, "uomToId", uomToId, "productId", productId, "uomFromId", uomFromId), false);
		result.put("Info", uomTypeAndConfigPacking);
		return result;
	}
	public static Map<String, Object> saveQualityPublicationAjax(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		String productId = (String)context.get("productId");
		String qualityPublicationName = (String)context.get("qualityPublicationName");
		Long expireDate = (Long) context.get("expireDate");
		Timestamp fromDate =  (Timestamp)context.get("fromDate");
		Timestamp thruDate =  (Timestamp)context.get("thruDate");
		List<GenericValue> listQualityPublications = delegator.findList("QualityPublication", EntityCondition.makeCondition("productId", productId), null, null, null, false);
		boolean beganTx = TransactionUtil.begin(7200);
		if (UtilValidate.isNotEmpty(listQualityPublications)) {
			GenericValue qualityPublication = listQualityPublications.get(0);
			fromDate = (Timestamp) qualityPublication.get("fromDate");
			expireDate = (Long) qualityPublication.get("expireDate");
			Map<String, Object> fields = UtilMisc.toMap("qualityPublicationName", qualityPublicationName, "thruDate", thruDate);
			try {
				delegator.storeByCondition("QualityPublication", fields, EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "fromDate", fromDate, "expireDate", expireDate)));
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
				return ServiceUtil.returnError("Can not update QualityPublication");
			}
			return ServiceUtil.returnSuccess();
		}
		Map<String, Object> res = FastMap.newInstance();
		Map<String, Object> req = FastMap.newInstance();
		req.put("userLogin", context.get("userLogin"));
		try {
			res = dispatcher.runSync("createContentForQualityPublication", req);
		} catch (GenericServiceException e) {
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			return ServiceUtil.returnError("Can not create Content");
		}
		String contentId = (String) res.get("contentId");
		Map<String, Object> fields = UtilMisc.toMap("productId", productId, "qualityPublicationName", qualityPublicationName, "expireDate", expireDate, "fromDate", fromDate, "thruDate", thruDate, "contentId", contentId);
		try {
			GenericValue newQualityPublication = delegator.makeValue("QualityPublication", fields);
			delegator.create(newQualityPublication);
		} catch (GenericEntityException e) {
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonNoteCannotBeUpdated", UtilMisc.toMap("errorString", e.getMessage()), locale));
		}
		TransactionUtil.commit(beganTx);
		Map<String, Object> result = ServiceUtil.returnSuccess();
		return result;
	}
	public static Map<String, Object> listProductShelfLife(DispatchContext ctx, Map<String, ?> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		Locale locale = (Locale) context.get("locale");
		Set<String> fieldToSelects = FastSet.newInstance();
		fieldToSelects.add("productId");
		fieldToSelects.add("qualityPublicationName");
		fieldToSelects.add("fromDate");
		fieldToSelects.add("thruDate");
		fieldToSelects.add("expireDate");
		List<GenericValue> listQualityPublications = delegator.findList("QualityPublication", null, fieldToSelects, null, null, false);
		Map<String, Object> result = FastMap.newInstance();
		result.put("listProductShelfLife", listQualityPublications);
		return result;
	}
//	EmailService
	protected static final FoScreenRenderer foScreenRenderer = new FoScreenRenderer();
	protected static final HtmlScreenRenderer htmlScreenRenderer = new HtmlScreenRenderer();
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> sendMailFromScreenCustom(DispatchContext dctx, Map<String, ? extends Object> rServiceContext) {
        Map<String, Object> serviceContext = UtilMisc.makeMapWritable(rServiceContext);
        LocalDispatcher dispatcher = dctx.getDispatcher();
        String webSiteId = (String) serviceContext.remove("webSiteId");
        String bodyText = (String) serviceContext.remove("bodyText");
        String xslfoAttachScreenLocationParam = (String) serviceContext.remove("xslfoAttachScreenLocation");
        String attachmentNameParam = (String) serviceContext.remove("attachmentName");
        List<String> attachmentNameListParam = UtilGenerics.checkList(serviceContext.remove("attachmentNameList"));
        List<String> attachmentNameList = FastList.newInstance();
        if (UtilValidate.isNotEmpty(attachmentNameParam)) attachmentNameList.add(attachmentNameParam);
        if (UtilValidate.isNotEmpty(attachmentNameListParam)) attachmentNameList.addAll(attachmentNameListParam);
        Locale locale = (Locale) serviceContext.get("locale");
        MapStack<String> screenContext = MapStack.create();
        String partyId = "";
        String orderId = "";
        boolean isMultiPart = false;
        String custRequestId = "";
        StringWriter bodyWriter = new StringWriter();
        Map<String, Object> bodyParameters = new FastMap<String, Object>();
        List<Map<String, ? extends Object>> bodyParts = FastList.newInstance();
        List<Map> bodyParametersList = UtilGenerics.checkList(serviceContext.remove("bodyParametersList"));
        if (bodyText != null) {
            bodyText = FlexibleStringExpander.expandString(bodyText, screenContext,  locale);
            bodyParts.add(UtilMisc.<String, Object>toMap("content", bodyText, "type", "text/html"));
        } else {
            bodyParts.add(UtilMisc.<String, Object>toMap("content", bodyWriter.toString(), "type", "text/html"));
        }
        for (int i = 0; i < bodyParametersList.size(); i++) {
        	bodyParameters = (Map) bodyParametersList.get(i);
        	if (bodyParameters == null) {
                bodyParameters = MapStack.create();
            }
            bodyParameters.put("locale", locale);
            partyId = (String) serviceContext.get("partyId");
            if (partyId == null) {
                partyId = (String) bodyParameters.get("partyId");
            }
            orderId = (String) bodyParameters.get("orderId");
            custRequestId = (String) bodyParameters.get("custRequestId");
            bodyParameters.put("communicationEventId", serviceContext.get("communicationEventId"));
            NotificationServices.setBaseUrl(dctx.getDelegator(), webSiteId, bodyParameters);
            String contentType = (String) serviceContext.remove("contentType");
            screenContext.put("locale", locale);
            ScreenRenderer screens = new ScreenRenderer(bodyWriter, screenContext, htmlScreenRenderer);
            screens.populateContextForService(dctx, bodyParameters);
            screenContext.putAll(bodyParameters);
            if (!xslfoAttachScreenLocationParam.isEmpty()) {
                    String xslfoAttachScreenLocation = xslfoAttachScreenLocationParam;
                    String attachmentName = "Details.pdf";
                    if (UtilValidate.isNotEmpty(attachmentNameList) && attachmentNameList.size() >= i) {
                        attachmentName = attachmentNameList.get(i);
                    }
                    isMultiPart = true;
                    try {
                        Writer writer = new StringWriter();
                        MapStack<String> screenContextAtt = MapStack.create();
                        ScreenRenderer screensAtt = new ScreenRenderer(writer, screenContext, foScreenRenderer);
                        screensAtt.populateContextForService(dctx, bodyParameters);
                        screenContextAtt.putAll(bodyParameters);
                        screensAtt.render(xslfoAttachScreenLocation);
                        StreamSource src = new StreamSource(new StringReader(writer.toString()));
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        Fop fop = ApacheFopWorker.createFopInstance(baos, MimeConstants.MIME_PDF);
                        ApacheFopWorker.transform(src, null, fop);
                        baos.flush();
                        baos.close();
                        bodyParts.add(UtilMisc.<String, Object> toMap("content", baos.toByteArray(), "type", "application/pdf", "filename", attachmentName));
                    } catch (Exception e) {
                        Debug.logError(e, "Error rendering PDF attachment for email: " + e.toString(), module);
                        return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonEmailSendRenderingScreenPdfError", UtilMisc.toMap("errorString", e.toString()), locale));
                    }
                    serviceContext.put("bodyParts", bodyParts);
            } else {
                isMultiPart = false;
                if (bodyText != null) {
                    bodyText = FlexibleStringExpander.expandString(bodyText, screenContext,  locale);
                    serviceContext.put("body", bodyText);
                } else {
                    serviceContext.put("body", bodyWriter.toString());
                }
                if (contentType != null && contentType.equalsIgnoreCase("text/plain")) {
                    serviceContext.put("contentType", "text/plain");
                } else {
                    serviceContext.put("contentType", "text/html");
                }
            }
		}
        String subject = (String) serviceContext.remove("subject");
        subject = FlexibleStringExpander.expandString(subject, screenContext, locale);
        Debug.logInfo("Expanded email subject to: " + subject, module);
        serviceContext.put("subject", subject);
        serviceContext.put("partyId", partyId);
        if (UtilValidate.isNotEmpty(orderId)) {
            serviceContext.put("orderId", orderId);
        }            
        if (UtilValidate.isNotEmpty(custRequestId)) {
            serviceContext.put("custRequestId", custRequestId);
        }            
        if (Debug.verboseOn()) Debug.logVerbose("sendMailFromScreen sendMail context: " + serviceContext, module);
        Map<String, Object> result = ServiceUtil.returnSuccess();
        Map<String, Object> sendMailResult;
        Boolean hideInLog = (Boolean) serviceContext.get("hideInLog");
        hideInLog = hideInLog == null ? false : hideInLog;
        try {
            if (!hideInLog) {
                if (isMultiPart) {
                    sendMailResult = dispatcher.runSync("sendMailMultiPart", serviceContext);
                } else {
                    sendMailResult = dispatcher.runSync("sendMail", serviceContext);
                }
            } else {
                if (isMultiPart) {
                    sendMailResult = dispatcher.runSync("sendMailMultiPartHiddenInLog", serviceContext);
                } else {
                    sendMailResult = dispatcher.runSync("sendMailHiddenInLog", serviceContext);
                }
            }
        } catch (Exception e) {
            Debug.logError(e, "Error send email:" + e.toString(), module);
            return ServiceUtil.returnError(UtilProperties.getMessage(resource, "CommonEmailSendError", UtilMisc.toMap("errorString", e.toString()), locale));
        }
        if (ServiceUtil.isError(sendMailResult)) {
            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(sendMailResult));
        }
//        result.put("messageWrapper", sendMailResult.get("messageWrapper"));
//        result.put("body", bodyWriter.toString());
//        result.put("subject", subject);
//        result.put("communicationEventId", sendMailResult.get("communicationEventId"));
//        if (UtilValidate.isNotEmpty(orderId)) {
//            result.put("orderId", orderId);
//        }            
//        if (UtilValidate.isNotEmpty(custRequestId)) {
//            result.put("custRequestId", custRequestId);
//        }
        return result;
    }
//	EmailService
	
//	JQXServices
	@SuppressWarnings("unchecked")
    public static Map<String, Object> listReceiptRequirements(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	EntityCondition tmpConditon = EntityCondition.makeCondition("agreementId", EntityOperator.NOT_EQUAL, null);
    	listAllConditions.add(tmpConditon);
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	Map<String, String> mapCondition = new HashMap<String, String>();
    	String statusId = null;
    	if (parameters.get("statusId") != null && parameters.get("statusId").length > 0){
    		statusId = (String)parameters.get("statusId")[0];
    	}
    	if (statusId != null && !"".equals(statusId)){
    		mapCondition.put("statusId", statusId);
    		EntityCondition statusConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(statusConditon);
    	}
    	listSortFields.add("-requirementDate");
    	String requirementTypeId = null;
    	if (parameters.get("requirementTypeId") != null && parameters.get("requirementTypeId").length > 0){
    		requirementTypeId = (String)parameters.get("requirementTypeId")[0];
    	}
    	if (requirementTypeId != null && !"".equals(requirementTypeId)){
    		mapCondition = new HashMap<String, String>();
    		mapCondition.put("requirementTypeId", requirementTypeId);
    		EntityCondition reqTypeConditon = EntityCondition.makeCondition(mapCondition);
        	listAllConditions.add(reqTypeConditon);
    	}
    	listSortFields.add("requirementId");
    	String countryGeoId = null;
    	if (parameters.get("countryGeoId") != null && parameters.get("countryGeoId").length > 0){
    		countryGeoId = (String)parameters.get("countryGeoId")[0];
    	}
    	String listAll = null;
    	if (parameters.get("listAll") != null && parameters.get("listAll").length > 0){
    		listAll = (String)parameters.get("listAll")[0];
    	}
    	List<GenericValue> listRequirements = new ArrayList<GenericValue>();
    	try {
    		List<GenericValue> listTmp = new ArrayList<GenericValue>();
    		listIterator = delegator.find("OrderRequirementDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
    		listTmp = listIterator.getCompleteList();
    		listIterator.close();
			if (!listTmp.isEmpty()) {
				if (countryGeoId != null && !("").equals(countryGeoId)){
					for (GenericValue orderReq : listTmp){
						List<GenericValue> listVendorAndGeo = delegator.findList("PartyOrderAddressPurpose", EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String)orderReq.get("orderId"), "roleTypeId", "BILL_FROM_VENDOR", "contactMechPurposeTypeId", "PRIMARY_LOCATION", "countryGeoId", countryGeoId)), null, null, null, false);
						if (!listVendorAndGeo.isEmpty()){
							listRequirements.add(orderReq);
						}
					}
	    		} else {
	    			if (listAll != null && !"".equals(listAll)){
	    				listRequirements.addAll(listTmp);
	    			} else {
		    			for (GenericValue orderReq : listTmp){
							List<GenericValue> listVendorAndGeo = delegator.findList("PartyOrderAddressPurpose", EntityCondition.makeCondition(UtilMisc.toMap("orderId", (String)orderReq.get("orderId"), "roleTypeId", "BILL_FROM_VENDOR", "contactMechPurposeTypeId", "PRIMARY_LOCATION", "countryGeoId", "VNM")), null, null, null, false);
							if (listVendorAndGeo.isEmpty()){
								listRequirements.add(orderReq);
							}
						}
	    			}
	    		}
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listReceiptRequirements service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listRequirements);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listQualityPublication(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		try {
			listIterator = delegator.find("QualityPublication", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listReceiptRequirements service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listFacilityByOwnerParty(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		String ownerPartyId = parameters.get("ownerPartyId")[0];
       	EntityCondition tmpConditon = EntityCondition.makeCondition(UtilMisc.toMap("ownerPartyId", ownerPartyId));
       	listAllConditions.add(tmpConditon);
		try {
			listIterator = delegator.find("Facility", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listReceiptRequirements service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listPackingUnit(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		EntityCondition tmpConditon = EntityCondition.makeCondition(UtilMisc.toMap("uomTypeId", "PRODUCT_PACKING"));
		listAllConditions.add(tmpConditon);
		try {
			listIterator = delegator.find("Uom", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listReceiptRequirements service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductQA(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = FastMap.newInstance();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		listSortFields.add("productId");
		try {
			listIterator = delegator.find("QualityPublicationAndProductImport", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling QualityPublicationAndProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listConfigGeneralCapacitys(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = FastMap.newInstance();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("uomTypeId", "SHIPMENT_PACKING"));
		listAllConditions.add(condition);
		try {
			listIterator = delegator.find("UomTypeAndConversionDated", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling UomTypeAndConversionDated service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listDocumentCustoms(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
//		listSortFields.add("registerDate");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		try {
			listIterator = delegator.find("DocumentCustoms", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling UomTypeAndConversionDated service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listUomTypeAndConfigPacking(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		EntityCondition condition = EntityCondition.makeCondition(UtilMisc.toMap("uomTypeId", "SHIPMENT_PACKING"));
		listAllConditions.add(condition);
		try {
			listIterator = delegator.find("UomTypeAndConfigPacking", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling UomTypeAndConversionDated service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listProductsSupplier(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		try {
			listIterator = delegator.find("SupplierProduct", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling QualityPublicationAndProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listImportPlan(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String customTimePeriodId = null;
    	if(parameters.containsKey("customTimePeriodId") && parameters.get("customTimePeriodId").length > 0){
    		customTimePeriodId = (String)parameters.get("customTimePeriodId")[0];
    	}
    	Map<String, String> mapCondition = new HashMap<String, String>();
       	mapCondition.put("parentProductPlanId", null);
       	mapCondition.put("productPlanTypeId", "IMPORT_PLAN");
       	if(!"".equals(customTimePeriodId) && customTimePeriodId != null){
       		mapCondition.put("customTimePeriodId", customTimePeriodId);
       	}
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
    	try {
    		listIterator = delegator.find("ProductPlanHeader", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listReceiptRequirements service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> listProductCategory(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
		mapCondition.put("productCategoryTypeId", "CATALOG_CATEGORY");
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
		List<GenericValue> listProductCategory = delegator.findList("ProductCategory", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, null, false);
		List<Map> listMapProductCategory = FastList.newInstance();
		for (GenericValue x : listProductCategory) {
			Map<String, Object> mapProductCategory = FastMap.newInstance();
			mapProductCategory.putAll(x);
			String productCategoryId = x.getString("productCategoryId");
			List<GenericValue> listProduct = delegator.findList("Product", EntityCondition.makeCondition(UtilMisc.toMap("primaryProductCategoryId", productCategoryId)), null, null, null, false);
			mapProductCategory.put("rowDetail", listProduct);
			listMapProductCategory.add(mapProductCategory);
		}
		successResult.put("listIterator", listMapProductCategory);
		return successResult;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Map<String, Object> listProductPrepareImport(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		String strProductPlanId = parameters.get("strProductPlanId")[0];
		String productPlanIdParam = "";
		if (UtilValidate.isNotEmpty(parameters.get("productPlanId"))) {
			productPlanIdParam = parameters.get("productPlanId")[0];
		}
		if (UtilValidate.isNotEmpty(strProductPlanId)) {
			List<String> listProductPlanId = FastList.newInstance();
			for (String s : strProductPlanId.split("AND")) {
				if (UtilValidate.isNotEmpty(s)) {
					listProductPlanId.add(s);
				}
			}
			listAllConditions.add(EntityCondition.makeCondition("productPlanId", EntityOperator.IN, listProductPlanId));
		}else {
			listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", productPlanIdParam)));
		}
		listSortFields.add("productPlanId");
		List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, null, false);
		List<String> orderBy = FastList.newInstance();
		orderBy.add("productPlanItemSeqId");
		GenericValue productPlanHeader = EntityUtil.getFirst(listProductPlanHeader);
		String parentProductPlanId = productPlanHeader.getString("parentProductPlanId");
		GenericValue productPlanYear = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", parentProductPlanId), false);
		String customTimePeriodId = productPlanYear.getString("customTimePeriodId");
		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		String periodName = customTimePeriod.getString("periodName");
		List<Map> listMapProductPlan = FastList.newInstance();
		for (GenericValue x : listProductPlanHeader) {
			Map<String, Object> mapProductPlan = FastMap.newInstance();
			mapProductPlan.putAll(x);
			String productPlanId = x.getString("productPlanId");
			List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, orderBy, null, false);
			List<Map> listMapProductPlanItem = FastList.newInstance();
			if (UtilValidate.isNotEmpty(listProductPlanItem)) {
				for (GenericValue z : listProductPlanItem) {
					String productId = z.getString("productId");
					List<GenericValue> listProducts = delegator.findList("QualityPublicationAndProductAndUom", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
					BigDecimal planQuantity = z.getBigDecimal("planQuantity");
					if (planQuantity.intValue() > 0) {
						Map<String, Object> mapProductPlanItem = FastMap.newInstance();
						mapProductPlanItem.put("productId", productId);
						mapProductPlanItem.put("planQuantity", planQuantity);
						if (UtilValidate.isNotEmpty(listProducts)) {
							GenericValue product = EntityUtil.getFirst(listProducts);
							mapProductPlanItem.put("internalName", product.getString("internalName"));
							mapProductPlanItem.put("thruDateQA", product.getTimestamp("thruDateQA"));
							mapProductPlanItem.put("expireDateQA", product.getLong("expireDateQA"));
						}
						listMapProductPlanItem.add(mapProductPlanItem);
					}
				}
			}
			mapProductPlan.put("rowDetail", listMapProductPlanItem);
			mapProductPlan.put("periodName", periodName);
			listMapProductPlan.add(mapProductPlan);
		}
		successResult.put("listIterator", listMapProductPlan);
		return successResult;
	}
	@SuppressWarnings({ "unchecked" })
	public static Map<String, Object> listEditReceiptRequirement(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String> mapCondition = new HashMap<String, String>();
       	mapCondition.put("statusId", "AGREEMENT_PROCESSING");
       	mapCondition.put("agreementTypeId", "PURCHASE_AGREEMENT");
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition, EntityOperator.AND);
		listAllConditions.add(tmpConditon);
		try {
			listIterator = delegator.find("AgreementAndOrderDetail", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listEditReceiptRequirement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		List<GenericValue> listOrderRequirementDetail = new ArrayList<GenericValue>();
		try {
			listOrderRequirementDetail = delegator.findList("OrderRequirementDetail", EntityCondition.makeCondition("agreementId",EntityOperator.NOT_EQUAL, null), null, null, null, false);
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		List<GenericValue> listAgreementAndOrderDetail = new ArrayList<GenericValue>();
		try {
			listAgreementAndOrderDetail = listIterator.getCompleteList();
			listIterator.close();
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		List<GenericValue> listAgreementAndOrderDetailTemp = new ArrayList<GenericValue>();
		if (UtilValidate.isNotEmpty(listAgreementAndOrderDetail) && UtilValidate.isNotEmpty(listOrderRequirementDetail)) {
			for (GenericValue x : listAgreementAndOrderDetail) {
				String xAgreementId = (String) x.get("agreementId");
				for (GenericValue z : listOrderRequirementDetail) {
					String zAgreementId = (String) z.get("agreementId");
					if (xAgreementId.equals(zAgreementId)) {
						listAgreementAndOrderDetailTemp.add(x);
					}
				}
			}
		}
		listAgreementAndOrderDetail.removeAll(listAgreementAndOrderDetailTemp);
		successResult.put("listIterator", listAgreementAndOrderDetail);
		return successResult;
	}
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listReceiveAgreement(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<GenericValue> listGe = new ArrayList<GenericValue>();
    	List<String> orderBy = FastList.newInstance();
    	orderBy.add("+billId");
    	try {
    		Set<String> fieldSl = FastSet.newInstance();
			fieldSl.add("billId");
			fieldSl.add("billNumber");
			fieldSl.add("partyIdFrom");
			fieldSl.add("departureDate");
			fieldSl.add("arrivalDate");
    		listGe = delegator.findList("BillOfLading", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), fieldSl, listSortFields, opts, false);
    		List<GenericValue> listBL = EntityUtil.filterByDate(listGe);
//    		List<GenericValue> listDetailsHasBill = delegator.findList("OrderAndContainerAndAgreementAndOrderDetail", EntityCondition.makeCondition("billId",EntityOperator.NOT_EQUAL, null), null, orderBy, null, false);
    		if(!UtilValidate.isEmpty(listBL)){
    			for(GenericValue x : listBL){
    				Map<String, Object> row = new HashMap<String, Object>();
    				List<Map<String, Object>> ListDetailEqualBillId = new ArrayList<Map<String, Object>>();
    				row.putAll(x);
    				String billId = (String) x.get("billId");
    				String billNumber = (String)x.get("billNumber");
    				List<GenericValue> listAgreement = delegator.findList("BillOfLadingAndAgreement", EntityCondition.makeCondition(UtilMisc.toMap("billId", billId)), null, null, null, false);
    				for(GenericValue agreement : listAgreement){
    					String agreementId = (String)agreement.get("agreementId");
    					String billAgreementId = (String)agreement.get("billAgreementId");
    					GenericValue listAgreementAttr = delegator.findOne("AgreementAndAgreementAttribute", UtilMisc.toMap("agreementId", agreementId, "attrName", "AGREEMENT_NAME"), false);
    					Set<String> fieldSlAgree = FastSet.newInstance();
    					fieldSlAgree.add("orderId");
//    					fieldSlAgree.add("billNumber");
    					List<GenericValue> listAgreementAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), fieldSlAgree, null, null, false);
    					for(GenericValue agreementAndOrder : listAgreementAndOrder){
    						Map<String, Object> rowDetail = FastMap.newInstance();
    						rowDetail.put("agreementId", agreementId);
        					rowDetail.put("agreementName",(String)listAgreementAttr.get("attrValue"));
        					rowDetail.put("agreementDate",(Timestamp)listAgreementAttr.get("agreementDate"));
        					rowDetail.put("billId", billId);
        					rowDetail.put("billNumber", billNumber);
        					rowDetail.put("statusId", (String)listAgreementAttr.get("statusId"));
        					rowDetail.put("billAgreementId", billAgreementId);
        					String orderId = (String)agreementAndOrder.get("orderId");
        					rowDetail.put("orderId", orderId);
        					GenericValue orderAndContainer = delegator.findOne("OrderAndContainer", UtilMisc.toMap("orderId", orderId, "billId", billId), false);
        					String containerId = "";
        					String containerNumber = "";
        					if(orderAndContainer != null){
        						containerId = (String)orderAndContainer.get("containerId");
        						GenericValue container = delegator.findOne("Container", UtilMisc.toMap("containerId", containerId), false);
        						containerNumber = (String)container.get("containerNumber");
        					}
        					rowDetail.put("containerId", containerId);
        					rowDetail.put("containerNumber", containerNumber);
        					ListDetailEqualBillId.add(rowDetail);
    					}
    				}
    				
    				
    				row.put("rowDetail", ListDetailEqualBillId);
    				listIterator.add(row);
    			}
    		}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listReceiveAgreement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
	@SuppressWarnings("unchecked")
	public static Map<String, Object> listQuotas(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	listSortFields.add("quotaId");
    	Map<String, String> mapCondition = new HashMap<String, String>();
       	mapCondition.put("quotaTypeId", "IMPORT_QUOTA");
		EntityCondition tmpConditon = EntityCondition.makeCondition(mapCondition);
		listAllConditions.add(tmpConditon);
    	try {
    		List<GenericValue> listQuotasHeader = delegator.findList("QuotaHeader", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, listSortFields, opts, false);
    		List<String> orderBy = FastList.newInstance();
    		orderBy.add("quotaItemSeqId");
    		for (GenericValue quotasHeader : listQuotasHeader) {
    			Map<String, Object> mapResult = FastMap.newInstance();
    			String quotaId = (String) quotasHeader.get("quotaId");
    			mapResult.putAll(quotasHeader);
    			List<GenericValue> listQuotaItem = delegator.findList("QuotaItem", EntityCondition.makeCondition(UtilMisc.toMap("quotaId", quotaId)), null, orderBy, null, false);
    			mapResult.put("rowDetail", listQuotaItem);
    			listIterator.add(mapResult);
			}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listReceiveAgreement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    public static Map<String, Object> updateContainerNumber(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException, GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> result = FastMap.newInstance();
   		String containerId = (String)context.get("containerId");
   		String containerNumber = (String)context.get("containerNumber");
   		GenericValue container = delegator.findOne("Container", UtilMisc.toMap("containerId", containerId), false);
   		if(container != null){
	   		container.put("containerNumber", containerNumber);
	   		delegator.store(container);
   		}
    	return result;
    }
    public static Map<String, Object> getAgreementNotBill(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> result = FastMap.newInstance();
    	Set<String> fieldToSelects = FastSet.newInstance();
    	fieldToSelects.add("agreementId");
    	fieldToSelects.add("attrValue");
    	List<GenericValue> listAgreementNotBill = delegator.findList("AgreementAndAgreementAttribute", EntityCondition.makeCondition(UtilMisc.toMap("statusId", "AGREEMENT_SENT", "agreementTypeId", "PURCHASE_AGREEMENT","attrName", "AGREEMENT_NAME")), fieldToSelects, null, null, false);
    	result.put("listAgreementNotBill", listAgreementNotBill);
    	return result;
    }
    public static Map<String, Object> removePurchaseOrderItem(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException{
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> result = FastMap.newInstance();
    	String orderId = (String)context.get("orderId");
    	String orderItemSeqId = (String)context.get("orderItemSeqId");
    	String quantity = (String)context.get("quantity");
    	int quantityInt = Integer.parseInt(quantity);
    	BigDecimal quantityBig = new BigDecimal(-quantityInt);
    	String productId = (String)context.get("productId");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	Map<String, String> itemReasonMap = FastMap.newInstance();
    	Map<String, String> itemCommentMap = FastMap.newInstance();
    	List<GenericValue> listShipGroupSeqId;
		GenericValue shipGroupId = null;
		String shipGroupSeqId = null;
			listShipGroupSeqId = delegator.findList("OrderItemShipGroup", EntityCondition.makeCondition(UtilMisc.toMap("orderId",orderId)), null, null, null, false);
			if(!UtilValidate.isEmpty(listShipGroupSeqId)){
				shipGroupId = EntityUtil.getFirst(listShipGroupSeqId);
				shipGroupSeqId = (String)shipGroupId.get("shipGroupSeqId");
			}
    	Map<String,Object> contextTmp = new HashMap<String, Object>();
		contextTmp.put("orderId", orderId);
		contextTmp.put("orderItemSeqId", orderItemSeqId);
		contextTmp.put("shipGroupSeqId", shipGroupSeqId);
		contextTmp.put("userLogin", userLogin);
		LocalDispatcher dispatcher = ctx.getDispatcher();
		try {
			dispatcher.runSync("cancelOrderItem", contextTmp);
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
    	
		List<GenericValue> listProductPlanAndOrder = delegator.findList("ProductPlanAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
		GenericValue productPlanItem = null;
		GenericValue productPlanItemParent = null;
		if(!UtilValidate.isEmpty(listProductPlanAndOrder)){
			GenericValue productPlanAndOrder = EntityUtil.getFirst(listProductPlanAndOrder);
			String productPlanId = (String)productPlanAndOrder.get("productPlanId");
			List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId, "productId", productId)), null, null, null, false);
			if(!UtilValidate.isEmpty(listProductPlanItem)){
				productPlanItem = EntityUtil.getFirst(listProductPlanItem);
			}
			GenericValue productPlan = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId), false);
			String parentProductPlanId = (String)productPlan.get("parentProductPlanId");
			List<GenericValue> listProductPlanItemParent = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", parentProductPlanId, "productId", productId)), null, null, null, false);
			if(!UtilValidate.isEmpty(listProductPlanItemParent)){
				productPlanItemParent = EntityUtil.getFirst(listProductPlanItemParent);
			}
		}
		
		if(productPlanItem != null){
			updateRecentPlanQuantity(delegator, productPlanItem, quantityBig);
		}
		if(productPlanItemParent != null){
			updateRecentPlanQuantity(delegator, productPlanItemParent, quantityBig);
		}
		
    	return result;
    }
    public static void updateRecentPlanQuantity(Delegator delegator, GenericValue planItem, BigDecimal quantity){
		BigDecimal recentPlanQuantity = quantity;
		if(planItem.getBigDecimal("recentPlanQuantity") != null){
			recentPlanQuantity = planItem.getBigDecimal("recentPlanQuantity").add(quantity);
		}
		planItem.put("recentPlanQuantity", recentPlanQuantity);
		try {
			delegator.store(planItem);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    @SuppressWarnings("static-access")
	public static Map<String, Object> updateOrderRequirement(DispatchContext ctx, Map<String, ? extends Object> context){
    	
    	Delegator delegator = ctx.getDelegator();
    	LocalDispatcher dispatcher = ctx.getDispatcher();
    	String orderId = (String)context.get("orderId");
    	String requirementId = (String)context.get("requirementId");
    	GenericValue userLogin = (GenericValue)context.get("userLogin");
    	String requirementDateStr = (String)context.get("requirementDate");
    	String headerConfirmFacility = (String)context.get("headerConfirmFacility");
    	String headerConfirmDate = (String)context.get("headerConfirmDate");
    	String facilityId = (String)context.get("facilityId");
    	String facilityName = (String)context.get("facilityName");
    	String txtDate = (String)context.get("txtDate");
    	Long requirementDateLong = Long.parseLong(requirementDateStr);
    	Timestamp requirementDate = new Timestamp(requirementDateLong);
    	Date newDate = new Date(requirementDateLong);
    	Calendar calendar = Calendar.getInstance();
	    calendar.setTime(newDate);
	    calendar.add(calendar.DATE, -1);
	    java.sql.Timestamp dateAlert = new java.sql.Timestamp(calendar.getTimeInMillis());
    	GenericValue orderRequirement = null;
    	try {
			orderRequirement = delegator.findOne("OrderRequirement", UtilMisc.toMap("orderId", orderId, "requirementId", requirementId), false);
			orderRequirement.put("requirementDate", requirementDate);
			orderRequirement.put("statusId", "REQ_CONFIRMED");
			delegator.store(orderRequirement);
			List<String> listLogSpecialists = new ArrayList<String>();
			List<String> listPartyGroups = SecurityUtil.getPartiesByRoles("LOG_SPECIALIST", delegator);
			if (!listPartyGroups.isEmpty()){
				for (String group : listPartyGroups){
					try {
						List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", "LOG_SPECIALIST")), null, null, null, false);
						listManagers = EntityUtil.filterByDate(listManagers);
						if (!listManagers.isEmpty()){
							for (GenericValue manager : listManagers){
								listLogSpecialists.add(manager.getString("partyIdFrom"));
							}
						}
					} catch (GenericEntityException e) {
						ServiceUtil.returnError("get Party relationship error!");
					}
				}
			}
			if(!listLogSpecialists.isEmpty()){
				for (String managerParty : listLogSpecialists){
					String sendToPartyId = managerParty;
					String header = ""+headerConfirmDate+ ": " +txtDate+" "+headerConfirmFacility+": "+facilityName;
					String headerLast = ""+txtDate+" "+headerConfirmFacility+": "+facilityName;
					Map<String, Object> mapContext = new HashMap<String, Object>();
					String targetLink = "statusId=REQ_CONFIRMED";
					mapContext.put("partyId", sendToPartyId);
					mapContext.put("action", "getReceiptRequirements");
					mapContext.put("targetLink", targetLink);
					mapContext.put("header", header);
					mapContext.put("userLogin", userLogin);
					Map<String, Object> mapContextLast = new HashMap<String, Object>();
					mapContextLast.put("partyId", sendToPartyId);
					mapContextLast.put("action", "getReceiptRequirements");
					mapContextLast.put("targetLink", targetLink);
					mapContextLast.put("header", headerLast);
					mapContextLast.put("openTime", dateAlert);
					mapContextLast.put("userLogin", userLogin);
					try {
						dispatcher.runSync("createNotification", mapContext);
						dispatcher.runSync("createNotification", mapContextLast);
					} catch (GenericServiceException e) {
						ServiceUtil.returnError(UtilProperties.getMessage(resource, "CreateNotificationError", (Locale)context.get("locale")));
					}
				}
			}
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			return ServiceUtil.returnError(e.getMessage());
		}
    	return ServiceUtil.returnSuccess();
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> listBillAndContainer(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String billIdCtx = null;
		if(parameters.containsKey("billId") && parameters.get("billId").length > 0){
			billIdCtx = (String)parameters.get("billId")[0];
    	}
		if(!"".equals(billIdCtx) && billIdCtx != null){
			EntityCondition billCond = EntityCondition.makeCondition(UtilMisc.toMap("billId", billIdCtx));
			listAllConditions.add(billCond);
		}
    	
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	List<GenericValue> listBL = new ArrayList<GenericValue>();
    	List<String> orderBy = FastList.newInstance();
    	orderBy.add("+billId");
    	try {
    		Set<String> fieldSl = FastSet.newInstance();
			fieldSl.add("billId");
			fieldSl.add("billNumber");
			fieldSl.add("partyIdFrom");
			fieldSl.add("departureDate");
			fieldSl.add("arrivalDate");
			if(listSortFields.isEmpty()){
				listSortFields.add("-billId");
			}
    		listBL = delegator.findList("BillOfLading", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), fieldSl, listSortFields, opts, false);
//    		List<GenericValue> listBL = EntityUtil.filterByDate(listGe);
//    		List<GenericValue> listDetailsHasBill = delegator.findList("OrderAndContainerAndAgreementAndOrderDetail", EntityCondition.makeCondition("billId",EntityOperator.NOT_EQUAL, null), null, orderBy, null, false);
    		if(!UtilValidate.isEmpty(listBL)){
    			for(GenericValue x : listBL){
    				Map<String, Object> row = new HashMap<String, Object>();
    				List<Map<String, Object>> listDetailEqualBillId = new ArrayList<Map<String, Object>>();
    				row.putAll(x);
    				String billId = (String) x.get("billId");
    				String billNumber = (String)x.get("billNumber");
    				List<GenericValue> listContainer = delegator.findList("Container", EntityCondition.makeCondition(UtilMisc.toMap("billId", billId, "containerTypeId", "STANDARD_CONTAINER")), null, null, null, false);
    				if(!UtilValidate.isEmpty(listContainer)){
	    				for(GenericValue container : listContainer){
	    					String containerId = (String)container.get("containerId");
	    					String containerNumber = (String)container.get("containerNumber");
	    					String sealNumber = (String)container.get("sealNumber");
	    					String externalOrderNumber = "";
	    					String agreementName = null;
	    					String agreementId = null;
	    					BigDecimal netWeightTotal = new BigDecimal(0);
	    					BigDecimal grossWeightTotal = new BigDecimal(0);
	    					Long packingUnitTotal = new Long(0);
	    					List<GenericValue> listPackingListHeader = delegator.findList("PackingListHeader", EntityCondition.makeCondition(UtilMisc.toMap("containerId", containerId)), null, null, null, false);
	    					if(!UtilValidate.isEmpty(listPackingListHeader)){
	    						int checkSize = listPackingListHeader.size();
	    						for(GenericValue packingList : listPackingListHeader){
	    							//get order of supplier
	    							checkSize--;
	    							externalOrderNumber += "SAP-"+(String)packingList.get("externalOrderNumber");
	    							if(checkSize != 0){
	    								externalOrderNumber += " & ";
	    							}else{
	    								// get agreement of container
	    								String purchaseOrderId = (String)packingList.get("purchaseOrderId");
	    								List<GenericValue> listAgreementAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("orderId", purchaseOrderId)), null, null, null, false);
	    								if(!UtilValidate.isEmpty(listAgreementAndOrder)){
	    									GenericValue agreement = EntityUtil.getFirst(listAgreementAndOrder);
	    									GenericValue agreementAttr = delegator.findOne("AgreementAndAgreementAttribute", UtilMisc.toMap("agreementId", (String)agreement.get("agreementId"), "attrName", "AGREEMENT_NAME"), false);
	    									agreementName = (String)agreementAttr.get("attrValue");
	    									agreementId =  (String)agreement.get("agreementId");
	    								}
	    							}
	    							//Sum Karton
	    							GenericValue packingListDetailSum = delegator.findOne("PackingListDetailSum", UtilMisc.toMap("packingListId", (String)packingList.get("packingListId")), false);
	    							if(packingListDetailSum != null && packingListDetailSum.containsKey("packingUnit") && packingListDetailSum.get("packingUnit")!= null){
	    								packingUnitTotal += (Long)packingListDetailSum.getLong("packingUnit");
	    							}
	    							//Sum netWeight
	    							netWeightTotal = netWeightTotal.add((BigDecimal)packingList.get("netWeightTotal"));
	    							//Sum gross Weight
	    							grossWeightTotal = grossWeightTotal.add((BigDecimal)packingList.get("grossWeightTotal"));
	    						}
	    					}
	    					
	    					Map<String, Object> rowDetail = FastMap.newInstance();
	    					rowDetail.put("containerId", containerId);
	    					rowDetail.put("containerNumber", containerNumber);
	    					rowDetail.put("sealNumber", sealNumber);
	    					rowDetail.put("externalOrderNumber", externalOrderNumber);
	    					rowDetail.put("agreementName", agreementName);
	    					rowDetail.put("netWeightTotal", netWeightTotal);
	    					rowDetail.put("grossWeightTotal", grossWeightTotal);
	    					rowDetail.put("packingUnitTotal", packingUnitTotal);
	    					rowDetail.put("agreementId", agreementId);
	    					listDetailEqualBillId.add(rowDetail);
	    				}
    				}
    				row.put("rowDetail", listDetailEqualBillId);
    				listIterator.add(row);
    			}
    		}
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listReceiveAgreement service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListLableItemByQA(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		listAllConditions.add(EntityCondition.makeCondition("productTypeId", EntityOperator.EQUALS, "RAW_MATERIAL"));
		try {
			listIterator = delegator.find("Product", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling QualityPublicationAndProduct service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
    public static Map<String, Object> getDataAgreementAjax(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
    	String productPlanId = (String) context.get("productPlanId");
    	if (UtilValidate.isEmpty(productPlanId)) {
    		result.put("listAgreement", null);
        	return result;
		}
    	Set<String> fieldSelect = FastSet.newInstance();
    	fieldSelect.add("agreementId");
    	List<GenericValue> listProductPlanAndOrder = delegator.findList("ProductPlanAndOrder", EntityCondition.makeCondition("productPlanId", productPlanId), null, null, null, false);
    	List<GenericValue> listAgreement = FastList.newInstance();
    	for (GenericValue x : listProductPlanAndOrder) {
    		String agreementId = x.getString("agreementId");
    		GenericValue agreement = delegator.findOne("Agreement", UtilMisc.toMap("agreementId", agreementId), false);
    		GenericValue agreementAttribute = delegator.findOne("AgreementAttribute", UtilMisc.toMap("agreementId", agreementId, "attrName", "AGREEMENT_NAME"), false);
    		String agreementName = agreementAttribute.getString("attrValue");
    		String statusId = agreement.getString("statusId");
			if (!statusId.equals("AGREEMENT_CANCELLED")) {
				agreement.set("description", agreementName);
				listAgreement.add(agreement);
			}
		}
    	result.put("listAgreement", listAgreement);
    	return result;
    }
    public static Map<String, Object> getInfoImportPlanAjax(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
    	String customTimePeriodId = (String) context.get("customTimePeriodId");
    	String internalPartyId = (String) context.get("internalPartyId");
    	GenericValue importPlan = null;
    	List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodId, "internalPartyId", internalPartyId)), null, null, null, false);
    	if (UtilValidate.isNotEmpty(listProductPlanHeader)) {
    		importPlan = EntityUtil.getFirst(listProductPlanHeader);
		}
    	result.put("importPlan", importPlan);
    	return result;
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListRequirementPurchaseLableByStatus(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String statusId = parameters.get("statusId")[0];
    	listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
    	List<GenericValue> listRequirement = delegator.findList("Requirement", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    	List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	for (GenericValue requirement : listRequirement) {
    		String requirementIdTotal = requirement.getString("requirementId");
    		Map<String, Object> row = new HashMap<String, Object>();
    		List<GenericValue> listRowDetails = FastList.newInstance();
    		row.putAll(requirement);
			for (GenericValue requirementItem : listRequirementItem) {
				String requirementId = (String) requirementItem.get("requirementId");
				if (requirementIdTotal.equals(requirementId)) {
					listRowDetails.add(requirementItem);
				}
			}
			List<Map<String, Object>> listToResult = FastList.newInstance();
			
			for (GenericValue rowDetail : listRowDetails){
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("requirementId", rowDetail.getString("requirementId"));
				mapTmp.put("reqItemSeqId", rowDetail.getString("reqItemSeqId"));
				mapTmp.put("productId", rowDetail.getString("productId"));
				mapTmp.put("quantity", rowDetail.getBigDecimal("quantity"));
				mapTmp.put("quantityUomId", rowDetail.get("quantityUomId"));
				mapTmp.put("statusId", rowDetail.getString("statusId"));
				listToResult.add(mapTmp);
			}
			row.put("rowDetail", listToResult);
			listIterator.add(row);
    	}
    	successResult.put("listIterator", listIterator);
		return successResult;
    	
    	/*Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String statusId = parameters.get("statusId")[0];
    	try {
    		listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
    		EntityCondition cond = EntityCondition.makeCondition(listAllConditions, EntityOperator.AND);
	    	listIterator = delegator.find("Requirement", cond, null, null, listSortFields, opts);
	    } catch (Exception e) {
			String errMsg = "Fatal error calling jqGetLocationByProductId service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;*/
    }
    
    @SuppressWarnings("unchecked")
	public static Map<String, Object> createRequirementToPOByPurchaseLabelItem(DispatchContext ctx, Map<String, ? extends Object> context) throws ParseException, GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> result = FastMap.newInstance();
   		List<String> productIdDataList = (List<String>)context.get("productIdData[]");
   		List<String> quantityDataList = (List<String>)context.get("quantityData[]");
   		List<String> quantityUomIdDataList = (List<String>)context.get("quantityUomIdData[]");
   		List<String> requirementIdDataList = (List<String>)context.get("requirementIdData[]");
   		List<String> reqItemSeqIdDataList = (List<String>)context.get("reqItemSeqIdData[]");
   		GenericValue requirement = delegator.makeValue("Requirement");
   		String requirementId = delegator.getNextSeqId("Requirement");
   		
   		List<String> listArrayDuplicate = new ArrayList<String>(new LinkedHashSet<String>(requirementIdDataList));
   		
   		GenericValue userLogin = (GenericValue)context.get("userLogin");
   		String requirementTypeId = "PURCHASING_LABEL_REQ";
   		String statusId = "LABEL_ITEM_SEND_PO";
   		Date date = new Date();
		long dateLong = date.getTime();
		Timestamp createDate = new Timestamp(dateLong);
		
		requirement.put("requirementId", requirementId);
		requirement.put("requirementTypeId", requirementTypeId);
		requirement.put("statusId", statusId);
		requirement.put("createdByUserLogin", userLogin.get("partyId"));
		requirement.put("createdDate", createDate);
		try {
			delegator.create(requirement);
		} catch (GenericEntityException e) {
		    return ServiceUtil.returnError(e.getStackTrace().toString());
		}
		
		int nextSeqId = 1; 
		GenericValue requirementItem = delegator.makeValue("RequirementItem");
    	for(int i = 0; i < productIdDataList.size(); i++){
    		String productId = productIdDataList.get(i);
    		String quantity = quantityDataList.get(i);
    		String quantityUomIdToTransfer = quantityUomIdDataList.get(i);
    		BigDecimal quantityBig = new BigDecimal(quantity);
    		requirementItem.put("requirementId", requirementId);
    		requirementItem.put("reqItemSeqId", UtilFormatOut.formatPaddedNumber(nextSeqId++, 5));
    		requirementItem.put("productId", productId);
    		requirementItem.put("quantity", quantityBig);
    		requirementItem.put("quantityUomId", quantityUomIdToTransfer);
    		requirementItem.put("createDate", createDate);
    		requirementItem.put("statusId", statusId);
    		try {
    			delegator.create(requirementItem);
    		} catch (GenericEntityException e) {
    		    return ServiceUtil.returnError(e.getStackTrace().toString());
    		}
    	}
    	
    	for (int j = 0; j < requirementIdDataList.size(); j++) {
    		String requirementIdEdit = requirementIdDataList.get(j);
    		String reqItemSeqIdEdit = reqItemSeqIdDataList.get(j);
			GenericValue requirementItemDataEdit = delegator.findOne("RequirementItem", UtilMisc.toMap("requirementId", requirementIdEdit, "reqItemSeqId", reqItemSeqIdEdit), false);
			if(requirementItemDataEdit != null){
				requirementItemDataEdit.put("statusId", "LABEL_ITEM_PUR_PO");
				try {
	    			delegator.store(requirementItemDataEdit);
	    		} catch (GenericEntityException e) {
	    		    return ServiceUtil.returnError(e.getStackTrace().toString());
	    		}
			}
		}
    	
    	for(int x = 0; x < listArrayDuplicate.size(); x++){
    		List<GenericValue> listRequirementItemCheck = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", listArrayDuplicate.get(x))), null, null, null, false);
    		int checkDataRequirement = 0;
    		if(!listRequirementItemCheck.isEmpty()){
    			for (GenericValue requirementItemCheck : listRequirementItemCheck) {
    				String statusIdCheck = requirementItemCheck.getString("statusId");
    				if(statusIdCheck.equals("LABEL_ITEM_PROPOSAL")){
    					checkDataRequirement = 0;
    					break;
    				}else{
    					checkDataRequirement = 1;
    				}
    			}
    			if(checkDataRequirement == 1){
    				GenericValue requirementEdit = delegator.findOne("Requirement", UtilMisc.toMap("requirementId", listArrayDuplicate.get(x)), false);
    				requirementEdit.put("statusId", "LABEL_ITEM_PUR_PO");
    				try {
    	    			delegator.store(requirementEdit);
    	    		} catch (GenericEntityException e) {
    	    		    return ServiceUtil.returnError(e.getStackTrace().toString());
    	    		}
    			}
    		}
    	 }
    	
    	result.put("requirementId", requirementId);
    	return result;
    }
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqGetListLabelItemPackingUnit(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		EntityCondition tmpConditon = EntityCondition.makeCondition(UtilMisc.toMap("uomTypeId", "PRODUCT_LABEL_ITEM"));
		listAllConditions.add(tmpConditon);
		try {
			listIterator = delegator.find("Uom", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listReceiptRequirements service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
    @SuppressWarnings("unchecked")
	public static Map<String, Object> jqListPurchaseRequiremetLabelItemToTalSendPO(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
    	String statusId = parameters.get("statusId")[0];
    	listAllConditions.add(EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, statusId));
    	List<GenericValue> listRequirement = delegator.findList("Requirement", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, listSortFields, opts, false);
    	List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(listAllConditions, EntityOperator.AND), null, null, null, false);
    	List<Map<String, Object>> listIterator = FastList.newInstance();
    	for (GenericValue requirement : listRequirement) {
    		String requirementIdTotal = requirement.getString("requirementId");
    		Map<String, Object> row = new HashMap<String, Object>();
    		List<GenericValue> listRowDetails = FastList.newInstance();
    		row.putAll(requirement);
			for (GenericValue requirementItem : listRequirementItem) {
				String requirementId = (String) requirementItem.get("requirementId");
				if (requirementIdTotal.equals(requirementId)) {
					listRowDetails.add(requirementItem);
				}
			}
			List<Map<String, Object>> listToResult = FastList.newInstance();
			
			for (GenericValue rowDetail : listRowDetails){
				Map<String, Object> mapTmp = FastMap.newInstance();
				mapTmp.put("requirementId", rowDetail.getString("requirementId"));
				mapTmp.put("reqItemSeqId", rowDetail.getString("reqItemSeqId"));
				mapTmp.put("productId", rowDetail.getString("productId"));
				mapTmp.put("quantity", rowDetail.getBigDecimal("quantity"));
				mapTmp.put("quantityUomId", rowDetail.get("quantityUomId"));
				mapTmp.put("statusId", rowDetail.getString("statusId"));
				listToResult.add(mapTmp);
			}
			row.put("rowDetail", listToResult);
			listIterator.add(row);
    	}
    	successResult.put("listIterator", listIterator);
		return successResult;
    }
    public static Map<String, Object> sendRequestPurchaseLabelItemTotalToPO(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String requirementId = (String)context.get("requirementId");
		String roleTypeId = (String)context.get("roleTypeId");
		String sendMessage = (String)context.get("sendMessage");
		String action = (String)context.get("action");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		try {
			GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
			List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
			if (requirement != null){
				requirement.put("statusId", "LABEL_ITEM_PRO_PO");
				delegator.createOrStore(requirement);
			}
			if(!listRequirementItem.isEmpty()){
				for (GenericValue requirementItem : listRequirementItem) {
					requirementItem.put("statusId", "LABEL_ITEM_PRO_PO");
					delegator.createOrStore(requirementItem);
				}
			}
		} catch (GenericEntityException e) {
			e.printStackTrace();
		}
		try {
			List<String> listQaAdmin = new ArrayList<String>();
			List<String> listPartyGroups = SecurityUtil.getPartiesByRoles(roleTypeId, delegator);
			if (!listPartyGroups.isEmpty()){
				for (String group : listPartyGroups){
					try {
						List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", roleTypeId)), null, null, null, false);
						listManagers = EntityUtil.filterByDate(listManagers);
						if (!listManagers.isEmpty()){
							for (GenericValue manager : listManagers){
								listQaAdmin.add(manager.getString("partyIdFrom"));
							}
						}
					} catch (GenericEntityException e) {
						ServiceUtil.returnError("get Party relationship error!");
					}
				}
			}
			if(!listQaAdmin.isEmpty()){
				for (String managerParty : listQaAdmin){
					String targetLink = "statusId=LABEL_ITEM_PRO_PO";
					String sendToPartyId = managerParty;
					Map<String, Object> mapContext = new HashMap<String, Object>();
					mapContext.put("partyId", sendToPartyId);
					mapContext.put("action", action);
					mapContext.put("targetLink", targetLink);
					mapContext.put("header", UtilProperties.getMessage(resource, sendMessage, (Locale)context.get("locale")));
					mapContext.put("userLogin", userLogin);
					dispatcher.runSync("createNotification", mapContext);
				}
			}
		} catch (GenericServiceException e) {
			e.printStackTrace();
		}
		result.put("requirementId", requirementId);
		return result;
	}

    @SuppressWarnings("unchecked")
   	public static Map<String, Object> sendPurchaseRequirementTotalToPO(DispatchContext ctx, Map<String, Object> context){
   		Map<String, Object> result = new FastMap<String, Object>();
   		Delegator delegator = ctx.getDelegator();
   		LocalDispatcher dispatcher = ctx.getDispatcher();
   		List<String> listRequirementId = (List<String>)context.get("requirementData[]");
   		String roleTypeId = (String)context.get("roleTypeId");
   		String sendMessage = (String)context.get("sendMessage");
   		String action = (String)context.get("action");
   		GenericValue userLogin = (GenericValue)context.get("userLogin");
   		try {
   			for (int i = 0; i < listRequirementId.size(); i++) {
   				String requirementId = listRequirementId.get(i);
   				GenericValue requirement = delegator.findOne("Requirement", false, UtilMisc.toMap("requirementId", requirementId));
   				List<GenericValue> listRequirementItem = delegator.findList("RequirementItem", EntityCondition.makeCondition(UtilMisc.toMap("requirementId", requirementId)), null, null, null, false);
   				if (requirement != null){
   					requirement.put("statusId", "LABEL_ITEM_PROPOSAL");
   					delegator.createOrStore(requirement);
   				}
   				if(!listRequirementItem.isEmpty()){
   					for (GenericValue requirementItem : listRequirementItem) {
   						requirementItem.put("statusId", "LABEL_ITEM_PROPOSAL");
   						delegator.createOrStore(requirementItem);
   					}
   				}
   			}
   		} catch (GenericEntityException e) {
   			e.printStackTrace();
   		}
   		try {
   			List<String> listQaAdmin = new ArrayList<String>();
   			List<String> listPartyGroups = SecurityUtil.getPartiesByRoles(roleTypeId, delegator);
   			if (!listPartyGroups.isEmpty()){
   				for (String group : listPartyGroups){
   					try {
   						List<GenericValue> listManagers = delegator.findList("PartyRelationship", EntityCondition.makeCondition(UtilMisc.toMap("partyIdTo", group, "roleTypeIdFrom", "MANAGER", "roleTypeIdTo", roleTypeId)), null, null, null, false);
   						listManagers = EntityUtil.filterByDate(listManagers);
   						if (!listManagers.isEmpty()){
   							for (GenericValue manager : listManagers){
   								listQaAdmin.add(manager.getString("partyIdFrom"));
   							}
   						}
   					} catch (GenericEntityException e) {
   						ServiceUtil.returnError("get Party relationship error!");
   					}
   				}
   			}
   			if(!listQaAdmin.isEmpty()){
   				for (String managerParty : listQaAdmin){
   					String targetLink = "statusId=LABEL_ITEM_PROPOSAL";
   					String sendToPartyId = managerParty;
   					Map<String, Object> mapContext = new HashMap<String, Object>();
   					mapContext.put("partyId", sendToPartyId);
   					mapContext.put("action", action);
   					mapContext.put("targetLink", targetLink);
   					mapContext.put("header", UtilProperties.getMessage(resource, sendMessage, (Locale)context.get("locale")));
   					mapContext.put("userLogin", userLogin);
   					dispatcher.runSync("createNotification", mapContext);
   				}
   			}
   		} catch (GenericServiceException e) {
   			e.printStackTrace();
   		}
   		return result;
   	}
    public static Map<String, Object> updateProductPlanHeader(DispatchContext ctx, Map<String, Object> context){
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String productPlanId = (String)context.get("productPlanId");
		String productPlanName = (String)context.get("productPlanName");
		try {
			delegator.storeByCondition("ProductPlanHeader", UtilMisc.toMap("productPlanName", productPlanName), EntityCondition.makeCondition("productPlanId", productPlanId));
		} catch (GenericEntityException e) {
			return ServiceUtil.returnError("Failure when update ProductPlanHeader");
		}
		return result;
    }
    @SuppressWarnings("unchecked")
	public static Map<String, Object> listTargetCompany(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
		String targetTypeId = (String)parameters.get("targetTypeId")[0];
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("targetTypeId", targetTypeId)));
		listAllConditions.add(EntityCondition.makeCondition("ofYear", EntityOperator.NOT_EQUAL, "" ));
		listSortFields.add("-ofYear");
		try {
			listIterator = delegator.find("TargetHeader", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listReceiptRequirements service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
    @SuppressWarnings("unchecked")
    public static Map<String, Object> listTargetItem(DispatchContext ctx, Map<String, ? extends Object> context) {
    	Delegator delegator = ctx.getDelegator();
    	Map<String, Object> successResult = ServiceUtil.returnSuccess();
    	EntityListIterator listIterator = null;
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
    	EntityFindOptions opts =(EntityFindOptions) context.get("opts");
    	Map<String, String[]> parameters = (Map<String, String[]>)context.get("parameters");
    	String targetHeaderId = (String)parameters.get("targetHeaderId")[0];
    	listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("targetHeaderId", targetHeaderId)));
    	listSortFields.add("productId");
    	try {
    		listIterator = delegator.find("TargetItem", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, listSortFields, opts);
    	} catch (GenericEntityException e) {
    		String errMsg = "Fatal error calling listReceiptRequirements service: " + e.toString();
    		Debug.logError(e, errMsg, module);
    	}
    	successResult.put("listIterator", listIterator);
    	return successResult;
    }
    public static Map<String, Object> createTargetHeader(DispatchContext ctx, Map<String, Object> context) throws GenericTransactionException{
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
		LocalDispatcher dispatcher = ctx.getDispatcher();
		String targetHeaderId = delegator.getNextSeqId("TargetHeader");
		String targetTypeId = (String)context.get("targetTypeId");
		String targetHeaderName = (String)context.get("targetHeaderName");
		String ofYear = (String)context.get("ofYear");
		String createBy = (String)context.get("createBy");
		Timestamp createDate = new Timestamp(System.currentTimeMillis());
		GenericValue userLogin = (GenericValue) context.get("userLogin");
		GenericValue targetHeader = delegator.makeValue("TargetHeader",
			UtilMisc.toMap("targetHeaderId", targetHeaderId, "targetTypeId", targetTypeId, "targetHeaderName", targetHeaderName, "ofYear", ofYear, "createBy", createBy, "createDate", createDate));
		boolean beganTx = TransactionUtil.begin(7200);
		try {
			delegator.create(targetHeader);
		} catch (GenericEntityException e) {
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			result = ServiceUtil.returnError("Error when createTargetHeader");
		}
		try {
			dispatcher.runSync("createTargetItem", UtilMisc.toMap("targetHeaderId", targetHeaderId, "userLogin", userLogin));
		} catch (GenericServiceException e) {
			TransactionUtil.rollback(beganTx, e.getMessage(), e);
			result = ServiceUtil.returnError("Error when createTargetHeader");
		} finally {
			TransactionUtil.commit(beganTx);
		}
		return result;
    }
    public static Map<String, Object> updateTargetHeader(DispatchContext ctx, Map<String, Object> context){
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	context.remove("userLogin");
    	context.remove("locale");
    	GenericValue targetHeader = delegator.makeValue("TargetHeader", context);
    	try {
			delegator.store(targetHeader);
		} catch (GenericEntityException e) {
			result = ServiceUtil.returnError("Error when updateTargetHeader");
		}
    	return result;
    }
    public static Map<String, Object> createTargetItem(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
		Map<String, Object> result = ServiceUtil.returnSuccess();
		Delegator delegator = ctx.getDelegator();
    	String targetHeaderId = (String) context.get("targetHeaderId");
    	List<EntityCondition> listEntityConditions = FastList.newInstance();
    	listEntityConditions.add(EntityCondition.makeCondition(EntityUtil.getFilterByDateExpr()));
    	listEntityConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productTypeId", "FINISHED_GOOD")));
    	List<GenericValue> listProducts = delegator.findList("ProductAndProductCategoryMember", EntityCondition.makeCondition(listEntityConditions, EntityJoinOperator.AND), UtilMisc.toSet("productId", "primaryProductCategoryId"), UtilMisc.toList("productId"), null, false);
    	boolean beganTx = TransactionUtil.begin(7200);
    	for (GenericValue x : listProducts) {
    		String targetItemSeqId = delegator.getNextSeqId("TargetItem");
			String productId = x.getString("productId");
			String productCategoryId = x.getString("primaryProductCategoryId");
			GenericValue targetHeader = delegator.makeValue("TargetItem",
					UtilMisc.toMap("targetHeaderId", targetHeaderId, "targetItemSeqId", targetItemSeqId, "productId", productId, "productCategoryId", productCategoryId, "quantity", BigDecimal.ZERO));
			try {
				delegator.create(targetHeader);
			} catch (GenericEntityException e) {
				TransactionUtil.rollback(beganTx, e.getMessage(), e);
			}
		}
    	TransactionUtil.commit(beganTx);
		return result;
    }
    public static Map<String, Object> updateTargetItem(DispatchContext ctx, Map<String, Object> context){
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	context.remove("userLogin");
    	context.remove("locale");
    	GenericValue targetHeader = delegator.makeValue("TargetItem", context);
    	try {
			delegator.store(targetHeader);
		} catch (GenericEntityException e) {
			result = ServiceUtil.returnError("Error when updateTargetHeader");
		}
    	return result;
    }

    @SuppressWarnings("rawtypes")
	public static Map<String, Object> listAgreementAjax(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Map<String, Object> result = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
    	String productPlanId = (String) context.get("productPlanId");
    	List<Map> listAgreements = FastList.newInstance();
    	GenericValue productPlanYear = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId), false);
    	String yearName = productPlanYear.getString("productPlanName");
    	Calendar calendar = Calendar.getInstance();
    	
    	List<GenericValue> listProductPlanMonth = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition("parentProductPlanId", productPlanId), null, null, null, false);
    	for (GenericValue x : listProductPlanMonth) {
			String productPlanMonthId = x.getString("productPlanId");
			List<GenericValue> listProductPlanWeek = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition("parentProductPlanId", productPlanMonthId), null, null, null, false);
			if (UtilValidate.isNotEmpty(listProductPlanWeek)) {
				for (GenericValue z : listProductPlanWeek) {
					String productPlanWeekId = z.getString("productPlanId");
					List<GenericValue> listAgreementInWeek = delegator.findList("ProductPlanAndOrderAndProductPlanHeader", EntityCondition.makeCondition("productPlanId", productPlanWeekId), null, UtilMisc.toList("fromDate"), null, false);
					if (UtilValidate.isNotEmpty(listAgreementInWeek)) {
						for (GenericValue v : listAgreementInWeek) {
							String agreementId = v.getString("agreementId");
							GenericValue agreement = delegator.findOne("AgreementAndAgreementAttribute", UtilMisc.toMap("agreementId", agreementId, "attrName", "AGREEMENT_NAME"), false);
							Map<String, Object> mapAgreement = FastMap.newInstance();
							mapAgreement.putAll(agreement);
							GenericValue customTimePeriodMonth = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", x.getString("customTimePeriodId")), false);
							calendar.setTime((Date)customTimePeriodMonth.getDate("fromDate"));
							mapAgreement.put("month", String.valueOf(calendar.get(Calendar.MONTH) + 1));
							
							GenericValue customTimePeriodWeek = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", z.getString("customTimePeriodId")), false);
							calendar.setTime((Date)customTimePeriodWeek.getDate("fromDate"));
							mapAgreement.put("week", String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR)));
							listAgreements.add(mapAgreement);
						}
					}
				}
			}
		}
    	result.put("listAgreements", listAgreements);
    	result.put("yearName", yearName);
    	return result;
    }
    public static Map<String, Object> removeConfigPacking(DispatchContext ctx, Map<String, Object> context){
    	Map<String, Object> result = ServiceUtil.returnSuccess();
    	Delegator delegator = ctx.getDelegator();
    	String productId = (String) context.get("productId");
    	String uomFromId = (String) context.get("uomFromId");
    	String uomToId = (String) context.get("uomToId");
    	Timestamp thruDate = new Timestamp(System.currentTimeMillis());
    	try {
			delegator.storeByCondition("ConfigPacking", UtilMisc.toMap("thruDate", thruDate),
					EntityCondition.makeCondition(UtilMisc.toMap("productId", productId, "uomFromId", uomFromId, "uomToId", uomToId)));
		} catch (GenericEntityException e) {
			result = ServiceUtil.returnError("Error when removeConfigPacking");
		}
    	return result;
    }
    public static Map<String, Object> getMapHasSupplierProduct(DispatchContext ctx, Map<String, Object> context) throws GenericEntityException{
    	Map<String, Object> result = FastMap.newInstance();
    	Map<String, Object> mapHasSupplierProduct = FastMap.newInstance();
    	Delegator delegator = ctx.getDelegator();
    	List<GenericValue> listProducts = delegator.findList("Product", null, null, null, null, false);
    	for (GenericValue x : listProducts) {
			String productId = x.getString("productId");
			List<GenericValue> listSupplierProducts = delegator.findList("SupplierProduct", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
			if (UtilValidate.isEmpty(listSupplierProducts)) {
				mapHasSupplierProduct.put(productId, false);
			} else {
				mapHasSupplierProduct.put(productId, true);
			}
		}
    	result.put("mapHasSupplierProduct", mapHasSupplierProduct);
    	return result;
    }
    
    public static Map<String, Object> JQgetListContainerLookup(DispatchContext ctx, Map<String, ? extends Object> context) {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		try {
			listIterator = delegator.find("ContainerLookup", EntityCondition.makeCondition(listAllConditions,EntityJoinOperator.AND), null, null, listSortFields, opts);
		} catch (GenericEntityException e) {
			String errMsg = "Fatal error calling listReceiptRequirements service: " + e.toString();
			Debug.logError(e, errMsg, module);
		}
		successResult.put("listIterator", listIterator);
		return successResult;
	}
    
    public static Map<String, Object> createImportPeriodExe(DispatchContext ctx, Map<String,Object> context) throws ParseException, GenericEntityException {
	    Delegator delegator = ctx.getDelegator();
	    
//	    Get import period month and week data from Database - default value: month = 26, week =5.
		long monthInDatabase = 0 ;
		long weekInDatabase  = 0 ;
		
		List<GenericValue> monthList;
		List<EntityCondition> listAllConditionsMonth = FastList.newInstance();
		listAllConditionsMonth.add(EntityCondition.makeCondition("periodConfigType", EntityOperator.EQUALS , "IMPORT_PERIOD_MONTH"));
		monthList = delegator.findList("ImportPeriodConfig", EntityCondition.makeCondition(listAllConditionsMonth), null, null, null, false);
		monthList = EntityUtil.filterByDate(monthList);
		GenericValue monthListGv = EntityUtil.getFirst(monthList);
		monthInDatabase = monthListGv.getLong("value");
		
		List<GenericValue> weekList;
		List<EntityCondition> listAllConditionsWeek = FastList.newInstance();
		listAllConditionsWeek.add(EntityCondition.makeCondition("periodConfigType", EntityOperator.EQUALS , "IMPORT_PERIOD_WEEK"));
		weekList = delegator.findList("ImportPeriodConfig", EntityCondition.makeCondition(listAllConditionsWeek), null, null, null, false);
		weekList = EntityUtil.filterByDate(weekList);
		GenericValue weekListGv = EntityUtil.getFirst(weekList);
		weekInDatabase = weekListGv.getLong("value");
		
		int monthTime = 26;
		int weekTime  = 5;
		
		if(monthInDatabase > 0 && monthInDatabase < 29) monthTime = (int) monthInDatabase;
		if(weekInDatabase  > 0 && weekInDatabase  < 8 ) weekTime  = (int) weekInDatabase;
		
//		Mr Dat's process code
		
	    String yearPeriodRaw = (String)context.get("yearPeriod");
//	    String strThruYear = (String)context.get("thruYear");
	    int yearPeriod = Integer.parseInt(yearPeriodRaw);
	    int state = 1;
//	    int thruYear = Integer.parseInt(strThruYear);
	    SimpleDateFormat yearMonthDayFormat = new SimpleDateFormat("yyyy-MM-dd");
	    
	    List<GenericValue> listCreatedYear = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition("periodTypeId", "IMPORT_YEAR"), null, null, null, false);
    	for (GenericValue y : listCreatedYear) {
			Date fromYearValidate = y.getDate("fromDate");
			Date thruYearValidate = y.getDate("thruDate");
			
			int fromYearValidateValue = fromYearValidate.getYear();
			int thruYearValidateValue = thruYearValidate.getYear();
			
			if((yearPeriod- 1900) == fromYearValidateValue &&  (yearPeriod- 1900) ==thruYearValidateValue )
				{
				state = 0;
				break;
				}
    	}
    	
	    if (state == 0)
	    {
	    	return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysUiLabels", "importYearCoincidence", (Locale)context.get("locale")));
	    } 
	    else 
	    {
	    Calendar calendar = Calendar.getInstance();
	    Date date = new Date();
	    calendar.setTime(date);
	    
	    calendar.set(Calendar.YEAR, yearPeriod);
	    calendar.set(Calendar.MONTH, 11);
    	calendar.set(Calendar.DAY_OF_MONTH, 31);
    	calendar.set(Calendar.HOUR, 0);
    	calendar.set(Calendar.MINUTE, 0);
    	calendar.set(Calendar.SECOND, 0);
    	calendar.set(Calendar.MILLISECOND, 0);
    	java.sql.Date endYear = new java.sql.Date(calendar.getTimeInMillis());
    	calendar.set(Calendar.MONTH, 0);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	java.sql.Date newYear = new java.sql.Date(calendar.getTimeInMillis());
    	String customTimePeriodIdYear = delegator.getNextSeqId("CustomTimePeriod");
//	create custom time year
    	//fix company...
    	createCustomtimePeriod2(delegator, customTimePeriodIdYear, null, "IMPORT_YEAR", "year: " +yearPeriod, newYear, endYear, "company");
    	
	    for(int k = 0; k < 12; k++){
	    	calendar.set(Calendar.MONTH, k);
	    	calendar.set(Calendar.DAY_OF_MONTH, 1);
	    	java.sql.Date firstDayOfMonth = new java.sql.Date(calendar.getTimeInMillis());
	    	int dateEndMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	    	calendar.set(Calendar.DAY_OF_MONTH, dateEndMonth);
	    	java.sql.Date endDayOfMonth = new java.sql.Date(calendar.getTimeInMillis());
	    	//create new customTimeperiod of month
	    	String customTimePeriodIdMonth = delegator.getNextSeqId("CustomTimePeriod");
	    	createCustomtimePeriod2(delegator, customTimePeriodIdMonth, customTimePeriodIdYear, "IMPORT_MONTH", "month: " +(k+1), firstDayOfMonth, endDayOfMonth, "company");
	    	
//			fix ngay 26 
//	    	calendar.set(Calendar.DAY_OF_MONTH, 26);
	    	
//	    	fix 26 --> monthTime from database
	    	calendar.set(Calendar.DAY_OF_MONTH, monthTime);
	    	
	    	java.sql.Date curMonth = new java.sql.Date(calendar.getTimeInMillis());

	    	calendar.add(Calendar.MONTH, -1);
	    	java.sql.Date prevMonth = new java.sql.Date(calendar.getTimeInMillis());
	    	int counti = 0;
//	    	String dater = "";
	    	while(!curMonth.equals(prevMonth)){
//	    		calendar.setTime(prevMonth);
	    		calendar.add(Calendar.DATE, 1);
	    		int dayWeek = calendar.get(Calendar.DAY_OF_WEEK);
	    		prevMonth = new java.sql.Date(calendar.getTimeInMillis());
	    		//fix thu 5
//	    		if(dayWeek == 5){
	    		
//	    		fix thu 5 --> weekTime
	    		if(dayWeek == weekTime){
	    			counti++;
	    			String customTimePeriodIdWeek = delegator.getNextSeqId("CustomTimePeriod");
	    			//create custom time week
	    			createCustomtimePeriod2(delegator, customTimePeriodIdWeek, customTimePeriodIdMonth, "IMPORT_WEEK", "week: " +counti, prevMonth, prevMonth, "company");
//	    			dater = + "" +(yearMonthDayFormat.format(prevMonth)) + "--";
	    		}
	    	}
	    }
	    return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysUiLabels", "importYearSuccess", (Locale)context.get("locale")));
	    }
	}
    
	public static void createCustomtimePeriod2(Delegator delegator, String customTimePeriodId, String parentPeriodId, String periodTypeId, String periodName, Date fromDate, Date thruDate, String organizationPartyId) throws GenericEntityException{
		GenericValue customTime = delegator.makeValue("CustomTimePeriod");
		customTime.put("customTimePeriodId", customTimePeriodId);
		customTime.put("parentPeriodId", parentPeriodId);
		customTime.put("periodTypeId", periodTypeId);
		customTime.put("periodName", periodName);
		customTime.put("fromDate", fromDate);
		customTime.put("thruDate", thruDate);
		customTime.put("organizationPartyId", organizationPartyId);
		delegator.create(customTime);
	}
	
    public static Map<String, Object> JQgetImportPeriodConfig(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		EntityListIterator listIterator = null;
		
		
		// Get data from database
		String  monthInDatabase = "";
		String weekInDatabase  = "";
		
		List<GenericValue> monthList;
		List<EntityCondition> listAllConditionsMonth = FastList.newInstance();
		listAllConditionsMonth.add(EntityCondition.makeCondition("periodConfigType", EntityOperator.EQUALS , "IMPORT_PERIOD_MONTH"));
		monthList = delegator.findList("ImportPeriodConfig", EntityCondition.makeCondition(listAllConditionsMonth), null, null, null, false);
		monthList = EntityUtil.filterByDate(monthList);
		if(!monthList.isEmpty())
		{
			GenericValue monthListGv = EntityUtil.getFirst(monthList);
			monthInDatabase = monthListGv.getString("value");
		}
		
		List<GenericValue> weekList;
		List<EntityCondition> listAllConditionsWeek = FastList.newInstance();
		listAllConditionsWeek.add(EntityCondition.makeCondition("periodConfigType", EntityOperator.EQUALS , "IMPORT_PERIOD_WEEK"));
		listAllConditionsWeek.add(EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS , null));	
		weekList = delegator.findList("ImportPeriodConfig", EntityCondition.makeCondition(listAllConditionsWeek), null, null, null, false);
		for(GenericValue weekListGv: weekList){
			weekInDatabase = weekListGv.getString("value");
		}
		
		successResult.put("monthValue", monthInDatabase);
		successResult.put("weekValue", weekInDatabase);
		return successResult;
	}
	
    public Map<String, Object> createImportPeriodConfig(DispatchContext dctx,
			Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();

		// Get inserted data from front-end page 
		Long insertedMonthTime = Long.parseLong((String) context.get("monthTime"));
		Long insertedWeekTime =  Long.parseLong((String) context.get("weekTime"));
		
		// Data to be updated		
		Timestamp fromDate = UtilDateTime.nowTimestamp();
		String periodConfigId = "";
		int monthToBeUpdated;
		int weekToBeUpdated;
		
		// Get data from database
		long monthInDatabase = 0;
		long weekInDatabase  = 0;
		
		List<GenericValue> monthList;
		List<EntityCondition> listAllConditionsMonth = FastList.newInstance();
		listAllConditionsMonth.add(EntityCondition.makeCondition("periodConfigType", EntityOperator.EQUALS , "IMPORT_PERIOD_MONTH"));
		monthList = delegator.findList("ImportPeriodConfig", EntityCondition.makeCondition(listAllConditionsMonth), null, null, null, false);
		monthList = EntityUtil.filterByDate(monthList);
		if(!monthList.isEmpty())
		{
			GenericValue monthListFirstValueGv = EntityUtil.getFirst(monthList);
			monthInDatabase =  monthListFirstValueGv.getLong("value");
		}
		
		List<GenericValue> weekList;
		List<EntityCondition> listAllConditionsWeek = FastList.newInstance();
		listAllConditionsWeek.add(EntityCondition.makeCondition("periodConfigType", EntityOperator.EQUALS , "IMPORT_PERIOD_WEEK"));
		weekList = delegator.findList("ImportPeriodConfig", EntityCondition.makeCondition(listAllConditionsWeek), null, null, null, false);
		weekList = EntityUtil.filterByDate(weekList);
		if(!weekList.isEmpty())
		{
			GenericValue weekListGvFirstValue = EntityUtil.getFirst(weekList);
			weekInDatabase = weekListGvFirstValue.getLong("value");
		}
		
		// Now process by case
		
		// Case 1: no data => create new
		
		if( (monthInDatabase <= 0) || (monthInDatabase > 28))
		{
			GenericValue month = delegator.makeValue("ImportPeriodConfig");
			periodConfigId = delegator.getNextSeqId("periodConfigId");
			month.put("periodConfigId", periodConfigId);
			month.put("periodConfigType", "IMPORT_PERIOD_MONTH");
			month.put("value", insertedMonthTime);
			month.put("fromDate", fromDate);
			delegator.create(month);
		}
		if( (weekInDatabase <= 0) || (weekInDatabase > 7))
		{
			GenericValue week = delegator.makeValue("ImportPeriodConfig");
			periodConfigId = delegator.getNextSeqId("periodConfigId");
			week.put("periodConfigId", periodConfigId);
			week.put("periodConfigType", "IMPORT_PERIOD_WEEK");
			week.put("value", insertedWeekTime);
			week.put("fromDate", fromDate);
			delegator.create(week);
		}
		
		// Case 2: old data is different from new data => Create new AND ThruDate the old one - one of these two
		if( (insertedMonthTime != monthInDatabase) &&  (monthInDatabase > 0) && (monthInDatabase <= 28) )
		{
			GenericValue month = delegator.makeValue("ImportPeriodConfig");
			periodConfigId = delegator.getNextSeqId("periodConfigId");
			month.put("periodConfigId", periodConfigId);
			month.put("periodConfigType", "IMPORT_PERIOD_MONTH");
			month.put("value", insertedMonthTime);
			month.put("fromDate", fromDate);
			delegator.create(month);
			for(GenericValue monthListGv: monthList){
				monthListGv.put("thruDate", fromDate);
				delegator.store(monthListGv);
			}
		}
		if( (insertedWeekTime != weekInDatabase) &&  (weekInDatabase > 0) && (weekInDatabase <= 7) )
		{
			GenericValue week = delegator.makeValue("ImportPeriodConfig");
			periodConfigId = delegator.getNextSeqId("periodConfigId");
			week.put("periodConfigId", periodConfigId);
			week.put("periodConfigType", "IMPORT_PERIOD_WEEK");
			week.put("value", insertedWeekTime);
			week.put("fromDate", fromDate);
			delegator.create(week);
			for(GenericValue weekListGv: weekList){
				weekListGv.put("thruDate", fromDate);
				delegator.store(weekListGv);
			}
		}
		
		// Case 3: old data equals new data => No change was made
		if(insertedMonthTime == monthInDatabase && insertedWeekTime == weekInDatabase)
		{
			return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysUiLabels", "importPeriodConfigNoChange", (Locale)context.get("locale")));
		}
		return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysUiLabels", "importPeriodConfigSuccess", (Locale)context.get("locale")));
	}
    
    public static Map<String, Object> jqGetListMainProductConfig(DispatchContext ctx, Map<String, ? extends Object> context) throws GenericEntityException {
		Delegator delegator = ctx.getDelegator();
		Map<String, Object> successResult = ServiceUtil.returnSuccess();
		
		List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
		List<String> listSortFields = (List<String>) context.get("listSortFields");
		EntityFindOptions opts =(EntityFindOptions) context.get("opts");
		
		List<GenericValue> dataList;
		dataList = delegator.findList("MainProductConfigAndProduct", EntityCondition.makeCondition(listAllConditions), null, listSortFields, opts, false);
		dataList = EntityUtil.filterByDate(dataList);
		
		successResult.put("listIterator", dataList);
		return successResult;
	}
    
	public Map<String, Object> jqDeleteMainProductConfig(DispatchContext dctx,
			Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String mainProductId = (String) context.get("mainProductId");
		GenericValue temp = delegator.findOne("MainProductConfig", UtilMisc.toMap("mainProductId", mainProductId), false);
		Timestamp nowDate = UtilDateTime.nowTimestamp();
		temp.put("thruDate", nowDate);
		try {
			delegator.store(temp);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public Map<String, Object> jqUpdateMainProductConfig(DispatchContext dctx,
			Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		String mainProductId = (String) context.get("mainProductId");
		java.sql.Timestamp fromDate = (Timestamp) context.get("fromDate");
		java.sql.Timestamp thruDate = (Timestamp) context.get("thruDate");
		GenericValue temp = delegator.findOne("MainProductConfig", UtilMisc.toMap("mainProductId", mainProductId), false);
		temp.put("fromDate", fromDate);
		if(thruDate != null )
		{
			temp.put("thruDate", thruDate);
		}
		try {
			delegator.store(temp);
		} catch (GenericEntityException e) {
			// TODO Auto-generated catch block
			return ServiceUtil.returnError(e.getMessage());
		}
		return ServiceUtil.returnSuccess();
	}
	
	public Map<String, Object> jqCreateMainProductConfig(DispatchContext dctx,
			Map<String, Object> context) throws GenericEntityException {
		Delegator delegator = dctx.getDelegator();
		LocalDispatcher dispatcher = dctx.getDispatcher();
		
		String mainProductId = "";
		String productId = (String) context.get("productId");
		java.sql.Timestamp fromDate = (Timestamp) context.get("fromDate");
		java.sql.Timestamp thruDate = (Timestamp) context.get("thruDate");
		
		
		List<EntityCondition> listAllConditions = FastList.newInstance();
		listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)));
		List<GenericValue> dataList;
		dataList = delegator.findList("MainProductConfig", EntityCondition.makeCondition(listAllConditions), null, null, null, false);
		dataList = EntityUtil.filterByDate(dataList);
		
		if(dataList.isEmpty() || dataList.size() == 0)
		{
			GenericValue temp = delegator.makeValue("MainProductConfig");
			mainProductId = delegator.getNextSeqId("mainProductId");
			temp.put("mainProductId", mainProductId);
			temp.put("productId", productId);
			temp.put("fromDate", fromDate);
			if(thruDate != null )
			{
				temp.put("thruDate", thruDate);
			}
			delegator.create(temp);
			
			return ServiceUtil.returnSuccess();
		}
		else
		{
			for (GenericValue gv : dataList) {
				mainProductId = (String) gv.get("mainProductId");
			}
			GenericValue temp = delegator.findOne("MainProductConfig", UtilMisc.toMap("mainProductId", mainProductId), false);
			temp.put("fromDate", fromDate);
			if(thruDate != null )
			{
				temp.put("thruDate", thruDate);
			}
			try {
				delegator.store(temp);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				return ServiceUtil.returnError(e.getMessage());
			}
			return ServiceUtil.returnSuccess(UtilProperties.getMessage("DelysUiLabels", "importPeriodConfigSuccess", (Locale)context.get("locale")));
		}
	}

}