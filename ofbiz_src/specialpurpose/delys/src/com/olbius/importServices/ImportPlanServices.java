package com.olbius.importServices;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastList;
import javolution.util.FastMap;
import javolution.util.FastSet;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericEntityException;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityListIterator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.minilang.method.envops.While;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;

import com.olbius.services.DelysServices;

public class ImportPlanServices {
	public static Role ROLE = null;
	public static GenericValue USER_LOGIN = null;
	public static String PARTY_ID = null;
	public enum Role {
		DELYS_ADMIN, DELYS_ROUTE, DELYS_ASM_GT, DELYS_RSM_GT, DELYS_CSM_GT, DELYS_CUSTOMER_GT, DELYS_SALESSUP_GT;
	}
	public static final String module = DelysServices.class.getName();
	public static final String resource = "DelysUiLabels";
	public static final String resourceError = "DelysErrorUiLabels";
	
	
	public static Map<String, Object> resetDevideContainer(DispatchContext ctx, Map<String,Object> context){
		Delegator delegator = ctx.getDelegator();
		Map<String,Object> result = new FastMap<String, Object>();
		String productPlanId =(String)context.get("productPlanId");
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		//neu stt # processing thi thuc hien
		
		GenericValue productPlan;
		String sttPlan = "";
		try {
			productPlan = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanId), false);
			sttPlan = (String)productPlan.get("statusId");
			if(!sttPlan.equals("PLAN_COMPLETED") && !sttPlan.equals("PLAN_PROCESSING") && !sttPlan.equals("PLAN_ORDERED")){
				List<GenericValue> listProductPlanAndLot;
				List<GenericValue> listProductPlanAndOrder;
				listProductPlanAndOrder = delegator.findList("ProductPlanAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
				if(!UtilValidate.isEmpty(listProductPlanAndOrder)){
					for(GenericValue y : listProductPlanAndOrder){
						GenericValue temp;
						String orderId = (String)y.get("orderId");
						String agreementId = (String)y.get("agreementId");
						Map<String, Object> contextAgree = new HashMap<String, Object>();
						contextAgree.put("agreementId", agreementId);
						contextAgree.put("statusId", "AGREEMENT_CANCELLED");
						contextAgree.put("userLogin", userLogin);
						Map<String,Object> contextTmp = new HashMap<String, Object>();
						contextTmp.put("statusId", "ORDER_CANCELLED");
						contextTmp.put("setItemStatus", "Y");
						contextTmp.put("orderId", orderId);
						contextTmp.put("userLogin", userLogin);
						try {
							dispatcher.runSync("changeOrderStatus", contextTmp);
							dispatcher.runSync("cancelAgreement", contextAgree);
							delegator.removeValue(y);
//							return ServiceUtil.returnSuccess();
						} catch (GenericServiceException e) {
							e.printStackTrace();
							return ServiceUtil.returnError(e.getMessage());
						}
						//xoa productplanAndOrder
						//tim order Id, agreementId
						//cancel order
					}
				}
				
				Map<String, Object> contextPlan = new HashMap<String, Object>();
				contextPlan.put("statusId", "PLAN_APPROVED");
				contextPlan.put("productPlanId", productPlanId);
				contextPlan.put("userLogin", userLogin);
				try {
					dispatcher.runSync("updateSttPlanHeader", contextPlan);
				} catch (GenericServiceException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					return ServiceUtil.returnError(e1.getMessage());
				}
					Set<String> listLotId = FastSet.newInstance();
					listProductPlanAndLot = delegator.findList("ProductPlanAndLot", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
					for(GenericValue x : listProductPlanAndLot){
//						lotIdGe = delegator.findOne("Lot", UtilMisc.toMap("lotId", (String)x.getString("lotId")), false);
						listLotId.add((String)x.get("lotId"));
							delegator.removeValue(x);
					}
					for(String lotId : listLotId){
						GenericValue lotIdGe = delegator.findOne("Lot", UtilMisc.toMap("lotId", lotId), false);
						delegator.removeValue(lotIdGe);
					}
					
			}else{
				//bo sung thong bao khong the reset
			}
			
		} catch (GenericEntityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return ServiceUtil.returnError(e1.getMessage());
		}
		return ServiceUtil.returnSuccess();
		
	}
	
	public static int divideToWeek(DispatchContext ctx, Map<String, ? extends Object> context, String customTimePeriodId,
			String customTimePeriodIdOfSales, String internalPartyId, String productId) throws GenericEntityException{
		
		Delegator delegator = ctx.getDelegator();
		List<GenericValue> listCustomTimePeriod = new ArrayList<GenericValue>();
		int ofWeek = 0;
//		Map<String, Object> result = new FastMap<String, Object>();
			listCustomTimePeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodId)), null, null, null, false);
		
		if(!UtilValidate.isEmpty(listCustomTimePeriod)){
			int quantityInventoryOfMonth = 0;
			//lay ton kho cuoi thang nay
				quantityInventoryOfMonth = getInventoryOfMonth(customTimePeriodId, internalPartyId, productId, delegator);
			//get salesFC
			int quantitySalesFC = getSalesFC(customTimePeriodIdOfSales, internalPartyId, productId, delegator);
			int salesPerWeek = quantitySalesFC*7/30;
			if(salesPerWeek > 0){
				ofWeek = quantityInventoryOfMonth/salesPerWeek;
			}
		}
		return ofWeek;
	}
	
	public static int getSalesFC(String customTimePeriodIdOfSales, String internalPartyId, String productId, Delegator delegator){
		List<GenericValue> listSalesFC = new ArrayList<GenericValue>(); 
		BigDecimal quantitySalesFC = new BigDecimal(1);
		try {
			listSalesFC = delegator.findList("SalesForecast", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodIdOfSales, "internalPartyId", internalPartyId)), null, null, null, false);
		} catch (GenericEntityException e) {
			return 0;
		}
		if(!UtilValidate.isEmpty(listSalesFC)){
			GenericValue salesFC = EntityUtil.getFirst(listSalesFC);
			String salesFCId = (String)salesFC.get("salesForecastId");
			List<GenericValue> listSalesFCDetails;
			try {
				listSalesFCDetails = delegator.findList("SalesForecastDetail", EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", salesFCId, "productId", productId)), null, null, null, false);
			} catch (GenericEntityException e) {
				// TODO Auto-generated catch block
				return 0;
			}
			
			if(!UtilValidate.isEmpty(listSalesFCDetails)){
				GenericValue salesFCDetail = EntityUtil.getFirst(listSalesFCDetails);
				quantitySalesFC = (BigDecimal)salesFCDetail.get("quantity");
			}
			
			
		}
		int quantity = quantitySalesFC.intValue();
		return quantity;
		
	}
	
	public static int getInventoryOfMonth(String customTimePeriodId, String internalPartyId, String productId, Delegator delegator) throws GenericEntityException{
		List<GenericValue> listInventory = new ArrayList<GenericValue>(); 
		BigDecimal quantityInventory = new BigDecimal(0);
		
		GenericValue customTimePeriod = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		Date thruDate = (Date)customTimePeriod.get("thruDate");
		List<GenericValue> listProductDimension = delegator.findList("ProductDimension", EntityCondition.makeCondition(UtilMisc.toMap("productId", productId)), null, null, null, false);
		List<GenericValue> listDatedimension = delegator.findList("DateDimension", EntityCondition.makeCondition(UtilMisc.toMap("dateValue", thruDate)), null, null, null, false);
		if(!UtilValidate.isEmpty(listDatedimension) && !UtilValidate.isEmpty(listProductDimension)){
			GenericValue datedimension = EntityUtil.getFirst(listDatedimension);
			Long dateDimId = (Long)datedimension.get("dimensionId");
			GenericValue productDimension = EntityUtil.getFirst(listProductDimension);
			Long productDimId = (Long)productDimension.get("dimensionId");
			Long facilityDimId = Long.parseLong("8000");
			//fix facilityDimId = 8000
			GenericValue facilityFact = delegator.findOne("FacilityFact", UtilMisc.toMap("facilityDimId", facilityDimId, "productDimId", productDimId, "dateDimId", dateDimId), false);
			if(facilityFact != null){
				quantityInventory = (BigDecimal)facilityFact.get("inventoryTotal");
			}
		}
		
		int quantity = quantityInventory.intValue();
		return quantity;
		
	}
	
	private static BigDecimal getInventoryOfProductInMonth(String customTimePeriodId, Date thruDate) {
		BigDecimal inventory = BigDecimal.ONE;
		
		return inventory;
	}
	public static List<List<Map<String, Object>>> resultProductToWeek(Map<String, Object> mapMainProduct, List<List<Map<String, Object>>> listWeekPro, int size){
		if(!mapMainProduct.isEmpty() || !listWeekPro.isEmpty()){
		
			List<Map<String, Object>> listMvOfWeek = new ArrayList<Map<String, Object>>();
			//init listMvOfWeek
			for(int i=0; i<size; i++){
				Map<String, Object> emptyMap = FastMap.newInstance();
				listMvOfWeek.add(i, emptyMap);
			}
			//ghep them so luong monte vani voi cac san pham khac trong 1 tuan kq co the bang 0, <33, = 33
			for(int i = 0; i < size; i ++){
	//			int index = listWeekPro.indexOf(listMapPro);
				int palletTotal = 0;
				List<Map<String, Object>> listMapPro = listWeekPro.get(i);
				if(!listMapPro.isEmpty()){
					for(Map<String, Object> mapPro : listMapPro){
						palletTotal = palletTotal + (Integer)mapPro.get("pallet");
					}
				}
				int quantityMvInMonth = (Integer)mapMainProduct.get("pallet");
				String productId = (String)mapMainProduct.get("productId");
				int quantityConvert = (Integer) mapMainProduct.get("quantityConvert");
				int quantityOfMvInWeek = 33 - palletTotal%33;
				if(quantityMvInMonth >= quantityOfMvInWeek){
					mapMainProduct.put("pallet", quantityMvInMonth - quantityOfMvInWeek);
					listMvOfWeek.get(i).put("productId", productId);
					listMvOfWeek.get(i).put("planQuantity", quantityOfMvInWeek*quantityConvert);
					listMvOfWeek.get(i).put("pallet", quantityOfMvInWeek);
					listMvOfWeek.get(i).put("quantityConvert", quantityConvert);
				}else if(quantityMvInMonth < quantityOfMvInWeek && quantityMvInMonth >= 0){
					mapMainProduct.put("pallet", quantityMvInMonth - quantityOfMvInWeek);
					listMvOfWeek.get(i).put("productId", productId);
					listMvOfWeek.get(i).put("planQuantity", quantityMvInMonth*quantityConvert);
					listMvOfWeek.get(i).put("pallet", quantityMvInMonth);
					listMvOfWeek.get(i).put("quantityConvert", quantityConvert);
				}else if(quantityMvInMonth < quantityOfMvInWeek && quantityMvInMonth < 0){
					listMvOfWeek.get(i).put("productId", productId);
					listMvOfWeek.get(i).put("planQuantity", 0);
					listMvOfWeek.get(i).put("pallet", 0);
					listMvOfWeek.get(i).put("quantityConvert", quantityConvert);
				}
			}
			
			int rePalletOfMv = (Integer)mapMainProduct.get("pallet");
			int quantiyConvertOfMv = (Integer)mapMainProduct.get("quantityConvert");
			String productIdMv = (String)mapMainProduct.get("productId");
			// DQ(size, rePalletOfMv, listMvOfWeek)
			while(rePalletOfMv > 0){
				for(int i = 0; i < size; i++){
					if(rePalletOfMv > 0){
		//				int index = listMvOfWeek.indexOf(mvOfWeek);
						Map<String, Object> mvOfWeek = listMvOfWeek.get(i);
						int quantityMvOfWeek = (Integer)mvOfWeek.get("pallet");
						if(rePalletOfMv < 33){
//							listMvOfWeek.get(i).put("productId", productIdMv);
							listMvOfWeek.get(i).put("pallet", rePalletOfMv + quantityMvOfWeek);
							listMvOfWeek.get(i).put("planQuantity", (rePalletOfMv + quantityMvOfWeek)*quantiyConvertOfMv);
						}else{
//							listMvOfWeek.get(i).put("productId", productIdMv);
							listMvOfWeek.get(i).put("pallet", 33 + quantityMvOfWeek);
							listMvOfWeek.get(i).put("planQuantity", (33 + quantityMvOfWeek)*quantiyConvertOfMv);
						}
						rePalletOfMv = rePalletOfMv - 33;
					}
				}
			}
			for(int i = 0; i< size; i++){
	//			int index = listWeekPro.indexOf(listMapPro);
				listWeekPro.get(i).add(listMvOfWeek.get(i));
			}
		}
		return listWeekPro;
	}
	
	public static List<List<Map<String, Object>>> divideProductToWeek(DispatchContext dpx,Map<String, ?extends Object> context, String productPlanHeader) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		List<List<Map<String, Object>>> listResultWeek = new ArrayList<List<Map<String, Object>>>();
//		String productPlanHeader = (String)context.get("productPlanId");
		GenericValue productPlanHeaderGe = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanHeader), false);
		String customTimePeriodId = (String)productPlanHeaderGe.get("customTimePeriodId");
		String customTimePeriodIdOfSales = (String)productPlanHeaderGe.get("customTimePeriodIdOfSales");
		String internalPartyId = (String)productPlanHeaderGe.get("internalPartyId");
		List<String> fieldOrders = new ArrayList<String>();
		List<String> orderField = new ArrayList<String>();
		orderField.add("customTimePeriodId");
		GenericValue listCustomTimeMonth = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);
		if(listCustomTimeMonth != null){
			
			Set<String> fieldSl = FastSet.newInstance();
			fieldSl.add("customTimePeriodId");
			fieldSl.add("periodName");
			fieldSl.add("fromDate");
			List<GenericValue> listCustomTimeWeek = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", (String)listCustomTimeMonth.get("customTimePeriodId"), "periodTypeId", "IMPORT_WEEK")), fieldSl, orderField, null, false);
			fieldOrders.add("productId");
			
			List<GenericValue> listItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeader)), null, fieldOrders, null, false);
			//lap tung item trong 1 plan thang
			List<List<Map<String, Object>>> listWeekPro = new ArrayList<List<Map<String,Object>>>();
			for(int i =0; i < listCustomTimeWeek.size(); i++){
				List<Map<String, Object>> emptyList = FastList.newInstance();
				listWeekPro.add(i, emptyList);
			}
			Map<String, Object> mapMv = new FastMap<String, Object>();
			mapMv.put("productId", "monte_vani_55g_4");
			mapMv.put("pallet", 0);
			mapMv.put("planQuantity", 0);
			mapMv.put("quantityConvert",1);
			for(GenericValue x : listItem){
				
				String productId = (String)x.get("productId");
				BigDecimal planQuantityBig = (BigDecimal)x.getBigDecimal("planQuantity");
				int planQuantityInt = planQuantityBig.intValue();
	
				GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", (String)x.get("productId")), false);
				String uomOfProduct = (String)product.get("quantityUomId");
				GenericValue cfPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId", (String)x.get("productId"), "uomFromId", "PALLET", "uomToId", uomOfProduct), false);
				int quantityPallet = planQuantityInt;
				int quantityConvert = 1;
				if(cfPacking != null){
					quantityConvert = ((BigDecimal)cfPacking.getBigDecimal("quantityConvert")).intValue();
					quantityPallet = planQuantityInt/(quantityConvert);
				}
				
				if(!productId.equals("monte_vani_55g_4")){
					int ofWeek = divideToWeek(dpx, context, customTimePeriodId, customTimePeriodIdOfSales, internalPartyId, productId);
	//				List<Map<String, ?>> listMapProductAndPallet = new ArrayList<Map<String, ?>>();
					Map<String, Object> mapProductAndPallet = new FastMap<String, Object>();
					mapProductAndPallet.put("productId", productId);
					mapProductAndPallet.put("pallet", quantityPallet);
					mapProductAndPallet.put("planQuantity", planQuantityInt);
					mapProductAndPallet.put("quantityConvert",quantityConvert);
					listWeekPro.get(ofWeek).add(mapProductAndPallet);
				}else{
	//				mapMv.put("productId", productId);
					mapMv.put("pallet", quantityPallet);
					mapMv.put("planQuantity", planQuantityInt);
					mapMv.put("quantityConvert",quantityConvert);
				}
			}
			
				//Thuc hien lay ket qua chia plan
				listResultWeek = resultProductToWeek(mapMv, listWeekPro, listCustomTimeWeek.size());
		}
		return listResultWeek;
	}
	
	public static Map<String, Object> JQResultDivideOfWeek (DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new HashMap<String, Object>();
		Map<String, String[]> param = (Map<String, String[]>)context.get("parameters");
    	String productPlanHeader = (String)param.get("productPlanId")[0];
    	List<Map<String, Object>> listReturn = FastList.newInstance();
		List<Map<String, Object>> listWeek = FastList.newInstance();
		Map<String, Object> rowMonth = FastMap.newInstance();
		List<List<Map<String, Object>>> listResultWeek = divideProductToWeek(dpx, context, productPlanHeader);
		
		GenericValue productPlan = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanHeader), false);
		String internalPartyId = (String)productPlan.get("internalPartyId");
		String customTimePeriodIdOfMonth = (String)productPlan.get("customTimePeriodId");
		
		List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeader)), null, null, null, false);
		List<String> orderField = new ArrayList<String>();
		orderField.add("customTimePeriodId");
		Set<String> fieldSl = FastSet.newInstance();
		fieldSl.add("customTimePeriodId");
		fieldSl.add("periodName");
		fieldSl.add("fromDate");
		List<GenericValue> listCustomTimeWeek = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodIdOfMonth, "periodTypeId", "IMPORT_WEEK")), fieldSl, orderField, null, false);
		rowMonth.put("week", (String)productPlan.get("productPlanName"));
		rowMonth.put("customTimePeriodId",customTimePeriodIdOfMonth);
		rowMonth.put("internalPartyId", internalPartyId);
		rowMonth.put("editPlanWeek", true);
		rowMonth.put("productPlanIdMonth", productPlanHeader);
		rowMonth.put("productPlanIdWeek", "");
		Map<String, Object> mapQuantityConvert = FastMap.newInstance();
		for(GenericValue productPlanItemMonth : listProductPlanItem){
			String productIdMonth = (String)productPlanItemMonth.get("productId");
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productIdMonth), false);
			GenericValue cfPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId", productIdMonth, "uomFromId", "PALLET", "uomToId", (String)product.get("quantityUomId")), false);
			int convertQuantityPro = ((BigDecimal)cfPacking.getBigDecimal("quantityConvert")).intValue();
			int palletMonth = ((BigDecimal)productPlanItemMonth.get("planQuantity")).intValue();
			rowMonth.put("palletNew_"+productIdMonth, palletMonth/convertQuantityPro);
			rowMonth.put("palletOld_"+productIdMonth, palletMonth/convertQuantityPro);
			mapQuantityConvert.put(productIdMonth, convertQuantityPro);
		}
		listWeek.add(rowMonth);
		for(int i = 0; i < listResultWeek.size(); i++){
			List<Map<String, Object>> resultOneWeek = listResultWeek.get(i);
			Map<String, Object> row = new HashMap<String, Object>();
			//check stt cua tuan va put them 1 dieu kien dc edit
			row.put("week", i+1);
			String customTimeIdWeek = (String)listCustomTimeWeek.get(i).get("customTimePeriodId");
			row.put("customTimePeriodId", customTimeIdWeek);
			row.put("internalPartyId", internalPartyId);
			List<GenericValue> listProductPlanHeaderWeek = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", productPlanHeader, "productPlanTypeId", "IMPORT_PLAN", "customTimePeriodId", customTimeIdWeek)), null, null, null, false);
			String productPlanIdWeek= "";
			String statusIdWeek = "";
			if(!UtilValidate.isEmpty(listProductPlanHeaderWeek)){
				GenericValue productPlanHeaderWeek = EntityUtil.getFirst(listProductPlanHeaderWeek);
				productPlanIdWeek = (String)productPlanHeaderWeek.get("productPlanId");
				statusIdWeek = (String)productPlanHeaderWeek.get("statusId");
			}
			if(statusIdWeek.equals("PLAN_ORDERED") || statusIdWeek.equals("PLAN_PROCESSING") || statusIdWeek.equals("PLAN_COMPLETED") || statusIdWeek.equals("PLAN_SENT")){
				row.put("editPlanWeek", false);
			}else{
				row.put("editPlanWeek",  true);
			}
			row.put("productPlanIdMonth", productPlanHeader);
			row.put("productPlanIdWeek", productPlanIdWeek);
			for(GenericValue productPlanItemMonth : listProductPlanItem){
				String productIdPlanMonth = (String)productPlanItemMonth.get("productId");
				int pallet = 0;
				int palletOld = 0;
				for(Map<String, Object> resultProductOfWeek : resultOneWeek){
					String productId = (String)resultProductOfWeek.get("productId");
					if(productId.equals(productIdPlanMonth)){
						pallet = (Integer)resultProductOfWeek.get("pallet");
						break;
					}
				}
				//add them palletOld
				int quantityConvert = (Integer)mapQuantityConvert.get(productIdPlanMonth);
				List<GenericValue> listProductPlanItemWeek = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanIdWeek, "productId", productIdPlanMonth)), null, null, null, false);
				if(!UtilValidate.isEmpty(listProductPlanItemWeek)){
					GenericValue productPlanItemWeek = EntityUtil.getFirst(listProductPlanItemWeek);
					int planQuantity = ((BigDecimal)productPlanItemWeek.get("planQuantity")).intValue();
					palletOld = planQuantity/quantityConvert;
				}
				row.put("palletNew_"+productIdPlanMonth, pallet);
				row.put("palletOld_"+productIdPlanMonth, palletOld);
			}
			listWeek.add(row);
		}
		result.put("listIterator", listWeek);
		return result;
	}
	
	public static Map<String, Object> getProductOfMonthPlan(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
		
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String productPlanHeader = (String)context.get("productPlanId");
		List<String> listProductId = new ArrayList<String>();
		List<Map<String, Object>> listProductIdName = new ArrayList<Map<String, Object>>();
		List<String> fieldOrders = new ArrayList<String>();
		fieldOrders.add("productId");
		
		List<GenericValue> listItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeader)), null, fieldOrders, null, false);
		for(GenericValue x : listItem){
			String productId = (String)x.get("productId");
			GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
			String nameProduct = (String)product.get("internalName");
			
			if(!listProductId.contains(productId)){
					listProductId.add(productId);
					Map<String, Object> nameProMap = new FastMap<String, Object>();
					GenericValue cfPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId", productId, "uomFromId", "PALLET", "uomToId", (String)product.get("quantityUomId")), false);
					nameProMap.put("productId", productId);
					nameProMap.put("productName", nameProduct);
					nameProMap.put("quantityUomId", (String)x.get("quantityUomId"));
					nameProMap.put("quantityConvert", ((BigDecimal)cfPacking.getBigDecimal("quantityConvert")).intValue());
					listProductIdName.add(nameProMap);
			}
		}
		result.put("listProductId", listProductIdName);
		result.put("productPlanId", productPlanHeader);
		return result;
	}
	
public static Map<String, Object> getProductOfPlan(DispatchContext dpx, Map<String, ?extends Object> context) throws GenericEntityException{
		
		Delegator delegator = dpx.getDelegator();
		Map<String, Object> result = new FastMap<String, Object>();
		String internalPartyId = (String)context.get("internalPartyId");
		String customTimePeriodId = (String)context.get("customTimePeriodId");
		List<Map<String, Object>> listProduct = FastList.newInstance();
		String productPlanWeekId = null;
		List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("internalPartyId", internalPartyId, "customTimePeriodId", customTimePeriodId)), null, null, null, false);
		if(!UtilValidate.isEmpty(listProductPlanHeader)){
			GenericValue productPlanWeekGe = EntityUtil.getFirst(listProductPlanHeader);
			productPlanWeekId = (String)productPlanWeekGe.get("productPlanId");
		}
		result.put("productPlanId", productPlanWeekId);
		if(productPlanWeekId != null && !productPlanWeekId.equals("")){
			List<GenericValue> listProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanWeekId)), null, null, null, false);
			for(GenericValue productPlanItem : listProductPlanItem){
				String productId = (String)productPlanItem.get("productId");
				BigDecimal quantity = (BigDecimal)productPlanItem.get("planQuantity");
				Map<String, Object> map = FastMap.newInstance();
				if(quantity.compareTo(new BigDecimal(0))==1){
					map.put("productId", productId);
					GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", productId), false);
					map.put("internalName", (String)product.get("internalName"));
					GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", (String)product.get("quantityUomId")), false);
					map.put("uomName", (String)uom.get("description"));
					listProduct.add(map);
				}
			}
		}
		result.put("listProduct", listProduct);
		
		return result;
	}

	public static Map<String,Object> devideContainerJqx(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String productPlanId = (String)parameters.get("productPlanId")[0];
		List<Map<String, Object>> listIterator = FastList.newInstance();
		Map<String,Object> resultReturn = FastMap.newInstance();
		GenericValue userLogin = (GenericValue)context.get("userLogin");
		LocalDispatcher dispatcher = ctx.getDispatcher();
		if(!productPlanId.equals("") && productPlanId != null){
			Map<String,Object> contextTmp = new HashMap<String, Object>();
			contextTmp.put("productPlanId", productPlanId);
			contextTmp.put("userLogin", userLogin);
				try {
					resultReturn = dispatcher.runSync("devideContainer", contextTmp);
				} catch (GenericServiceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
		}
		if(resultReturn.containsKey("listIterator")){
			listIterator = (List<Map<String, Object>>) resultReturn.get("listIterator");
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	public static Map<String,Object> jqxGetProductForAgreement(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		Delegator delegator = ctx.getDelegator();
    	List<EntityCondition> listAllConditions = (List<EntityCondition>) context.get("listAllConditions");
    	List<String> listSortFields = (List<String>) context.get("listSortFields");
		Map<String,String[]> parameters = (Map<String, String[]>) context.get("parameters");
		String productPlanId = (String)parameters.get("productPlanId")[0];
		String lotId = (String)parameters.get("lotId")[0];
		String agreementId = (String)parameters.get("agreementId")[0];
		List<Map<String, Object>> listIterator = FastList.newInstance();
		if(agreementId.equals("0")){
			List<GenericValue> listProductPlanAndLot = delegator.findList("ProductPlanAndLot", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId, "lotId", lotId)), null, null, null, false);
			for(GenericValue productPlanAndLot: listProductPlanAndLot){
				Map<String, Object> map = FastMap.newInstance();
				String productPlanItemSeqId = (String)productPlanAndLot.get("productPlanItemSeqId");
				GenericValue productPlanItem = delegator.findOne("ProductPlanItem", UtilMisc.toMap("productPlanId", productPlanId, "productPlanItemSeqId", productPlanItemSeqId), false);
				map.put("productPlanId", productPlanId);
				map.put("lotId", lotId);
				map.put("productId", (String)productPlanItem.get("productId"));
				BigDecimal lotQuantity = (BigDecimal)productPlanAndLot.get("lotQuantity");
				map.put("lotQuantity", lotQuantity);
				//fix ZOTT_COMPANY
				List<GenericValue> listSupplierProduct = delegator.findList("SupplierProduct", EntityCondition.makeCondition(UtilMisc.toMap("productId", (String)productPlanItem.get("productId"), "partyId", "ZOTT_COMPANY")), null, null, null, false);
				if(!listSupplierProduct.isEmpty()){
					GenericValue supplierProduct = EntityUtil.getFirst(listSupplierProduct);
					map.put("productName", (String)supplierProduct.get("supplierProductName"));
					map.put("supplierProductId", (String)supplierProduct.get("supplierProductId"));
					map.put("currencyUomId", (String)supplierProduct.get("currencyUomId"));
					BigDecimal lastPrice = (BigDecimal)supplierProduct.get("lastPrice");
					map.put("unitPrice", lastPrice);
					map.put("quantityUomId", (String)supplierProduct.get("quantityUomId"));
					GenericValue uom = delegator.findOne("Uom", UtilMisc.toMap("uomId", (String)supplierProduct.get("quantityUomId")), false);
					map.put("quantityUomName", (String)uom.get("description"));
					map.put("goodValue", lastPrice.multiply(lotQuantity));
				}
				listIterator.add(map);
			}
		}else{
			List<GenericValue> listAgreementAndOrder = delegator.findList("AgreementAndOrder", EntityCondition.makeCondition(UtilMisc.toMap("agreementId", agreementId)), null, null, null, false);
			listAgreementAndOrder = EntityUtil.filterByDate(listAgreementAndOrder);
			if(UtilValidate.isNotEmpty(listAgreementAndOrder)){
				GenericValue agreementAndOrder = EntityUtil.getFirst(listAgreementAndOrder);
				String orderId = agreementAndOrder.getString("orderId");
				GenericValue order = delegator.findOne("OrderHeader", UtilMisc.toMap("orderId", orderId), false);
				String currencyUom = order.getString("currencyUom");
				List<GenericValue> listOrderRole = delegator.findList("OrderRole", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId, "roleTypeId", "SUPPLIER_AGENT")), null, null, null, false);
				String supp = (EntityUtil.getFirst(listOrderRole)).getString("partyId");
				List<GenericValue> listOrderItems = delegator.findList("OrderItem", EntityCondition.makeCondition(UtilMisc.toMap("orderId", orderId)), null, null, null, false);
				listOrderItems = EntityUtil.filterByCondition(listOrderItems, EntityCondition.makeCondition("statusId", EntityOperator.NOT_EQUAL, "ITEM_CANCELLED"));
				for(GenericValue item : listOrderItems){
					Map<String, Object> map = FastMap.newInstance();
					map.put("orderId", orderId);
					map.put("orderItemSeqId", item.getString("orderItemSeqId"));
					map.put("productId", item.getString("productId"));
					map.put("lotQuantity", item.getBigDecimal("quantity"));
					map.put("productName", item.getString("itemDescription"));
					map.put("supplierProductId", item.getString("supplierProductId"));
					map.put("currencyUomId", currencyUom);
					map.put("unitPrice", item.getBigDecimal("unitPrice"));
					List<GenericValue> listSuppProduct = delegator.findList("SupplierProduct", EntityCondition.makeCondition(UtilMisc.toMap("productId",item.getString("productId"), "partyId", supp, "currencyUomId", currencyUom)), null, null, null, false);
					//FIXME: getfirst
//					listSuppProduct = EntityUtil.filterByDate(listSuppProduct);
					String quantityUomId = (EntityUtil.getFirst(listSuppProduct)).getString("quantityUomId");
					map.put("quantityUomId", quantityUomId);
					GenericValue uomGe = delegator.findOne("Uom", UtilMisc.toMap("uomId", quantityUomId), false);
					map.put("quantityUomName", (String)uomGe.get("description"));
					map.put("goodValue", item.getBigDecimal("quantity").multiply(item.getBigDecimal("unitPrice")));
					listIterator.add(map);
				}
				
			}
			
		}
		result.put("listIterator", listIterator);
		return result;
	}
	
	public static Map<String,Object> getCurrencyUomIdBySupplier(DispatchContext ctx, Map<String,Object> context) throws GenericEntityException{
		Map<String, Object> result = new FastMap<String, Object>();
		String partyId = (String)context.get("partyId");
		EntityFindOptions opts = new EntityFindOptions();
		opts.setDistinct(true);
		Delegator delegator = ctx.getDelegator();
		Set<String> fieldsToSelect = FastSet.newInstance();
		fieldsToSelect.add("currencyUomId");
		List<GenericValue> listSupp = delegator.findList("SupplierProduct", EntityCondition.makeCondition(UtilMisc.toMap("partyId", partyId)), fieldsToSelect, null, opts, false);
		result.put("listgetCurrencyUomIdBySupplier", listSupp);
		return result;
	}
}
