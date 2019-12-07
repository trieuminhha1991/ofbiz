import groovy.xml.Entity;

import java.util.Calendar;
import java.util.Set;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

import javolution.util.FastSet;

String productPlanId = (String)parameters.get("productPlanId");
//String productId = (String)context.get("productId");
String customTimePeriodId = (String)parameters.yearPlan;
String productId = (String)parameters.productId;
def packing = parameters.productUom;
String areaPlan = (String)parameters.areaPlan;
List<GenericValue> listMonth = new ArrayList<GenericValue>();

GenericValue product = delegator.findOne("Product",
	UtilMisc.toMap("productId", productId), false);

GenericValue year = delegator.findOne("CustomTimePeriod",
	UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);

Set<String> fieldToSelects1 = FastSet.newInstance();
fieldToSelects1.add("quantityConvert");
//Sua config_packing voi dieu kien uom_from_id = "pallet" va uom_to_id cua productId do
List<GenericValue> cfpacking = delegator.findList("ConfigPacking", EntityCondition.makeCondition("productId", productId), fieldToSelects1, null, null, false);

BigDecimal palet = cfpacking[0].get("quantityConvert");
orderFields = ["productPlanId"];
//
//System.out.println ("plan:" +productPlanId);
//System.out.println ("prod:" +productId);
//System.out.println ("area:" +areaPlan);
////
List<GenericValue> listProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", productPlanId)), null, orderFields, null, false);

List<Map> listProductSalesForcast = new ArrayList<Map>();
BigDecimal tempInventoryForecast = BigDecimal.ZERO;

for(GenericValue x : listProductPlanHeader){
	BigDecimal quantity=  BigDecimal.ZERO;
	BigDecimal planImport=  BigDecimal.ZERO;
	BigDecimal tonCuoiThang=  BigDecimal.ZERO;
	BigDecimal banCuaNgay=  BigDecimal.ZERO;
	BigDecimal max=  BigDecimal.ZERO;
	BigDecimal min=  BigDecimal.ZERO;
	BigDecimal tonTruoc = BigDecimal.ZERO;
	BigDecimal inventoryOfMonth = BigDecimal.ZERO;
	BigDecimal inventoryForecast = BigDecimal.ZERO;

//Tinh ton kho thang truoc

	GenericValue customTimePeriodOfSales = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", (String)x.customTimePeriodIdOfSales), false);
	listMonth.addAll(customTimePeriodOfSales);
	
	System.out.println ("aa:" +customTimePeriodOfSales.fromDate);
	Calendar calendar = Calendar.getInstance();
	calendar.setTime(customTimePeriodOfSales.fromDate);
	calendar.add(Calendar.DATE, -1);
	java.sql.Date curentDate = new java.sql.Date(calendar.getTimeInMillis());
	
//	GenericValue customTimePeriodOfImport = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", (String)x.customTimePeriodId), false);
//	System.out.println ("bb:" +customTimePeriodOfSales.fromDate);
	
	
		fieldDateDim = ["dimensionId"] as Set;
		List<GenericValue> listDateDim = delegator.findList("DateDimension", EntityCondition.makeCondition("dateValue", curentDate), fieldDateDim, null, null, false);
		GenericValue dateDim = EntityUtil.getFirst(listDateDim);
	
		List<GenericValue> listProductDim = delegator.findList("ProductDimension", EntityCondition.makeCondition("productId", productId), null, null, null, false);
		GenericValue productDim = EntityUtil.getFirst(listProductDim);
		
		if(dateDim != null && productDim != null){
// fix my dinh
			List<GenericValue> listFacilityFact = delegator.findList("FacilityFact", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", "mydinh", "productDimId", productDim.dimensionId, "dateDimId", dateDim.dimensionId)), null, null, null, false);
			GenericValue facilityFact = EntityUtil.getFirst(listFacilityFact);
			if(facilityFact != null){
				tonTruoc = facilityFact.inventoryTotal;
			}
		else{
			tonTruoc = new BigDecimal(-1);
		}
//		System.out.println("tontruoc:" +tonTruoc);
	
		System.out.println("aaa:" +tonTruoc);
		}
	//end Tinh ton kho thang truoc
		
//Tinh ton kho uoc luong thang lien truoc
		
		calendar.setTime(customTimePeriodOfSales.fromDate);
		calendar.add(Calendar.MONTH, -1);
		java.sql.Date preMonthIm = new java.sql.Date(calendar.getTimeInMillis());
		
		// Lay ID cua CustomTimePeriod cua thang lien truoc
		List<GenericValue> listCustomTimePreIm = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("fromDate", preMonthIm, "periodTypeId", "IMPORT_MONTH")), null, null, null, false);
		GenericValue customTimePreIm = EntityUtil.getFirst(listCustomTimePreIm);
		if(customTimePreIm != null){
			
			//Lay ProductPlanHeader cua thang truoc
			List<GenericValue> listProductPlanHeaderPre = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePreIm.customTimePeriodId, "internalPartyId", areaPlan)), null, null, null, false);
			GenericValue productPlanHeaderPre = EntityUtil.getFirst(listProductPlanHeaderPre);
			if(productPlanHeaderPre != null){
				
				//lay Ton kho du kien cua thang truoc dc luu trong bang ProductPlanItem
				List<GenericValue> listPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeaderPre.productPlanId, "productId", productId)), null, null, null, false);
				GenericValue planItem = EntityUtil.getFirst(listPlanItem);
				if(planItem != null){
					inventoryForecast = planItem.inventoryForecast;
//					tempInventoryForecast = inventoryForecast;
				}else{
					// inventoryForecast = ZERO;
				}
			}else{
				//inventoryForecast = ZERO;	
			}
		}else{
			//inventoryForecast = ZERO;	
		}
		System.out.println("kk: " +inventoryForecast);
	
				
//End tinh ton kho uoc luong
	
	// Tinh ton kho thang nay
	
		calendar.setTime(customTimePeriodOfSales.fromDate);
		calendar.add(Calendar.MONTH,1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		calendar.add(Calendar.DATE,-1);
		java.sql.Date lastDateMonth = new java.sql.Date(calendar.getTimeInMillis());
		List<GenericValue> listDateDimNew = delegator.findList("DateDimension", EntityCondition.makeCondition("dateValue", lastDateMonth), fieldDateDim, null, null, false)
		GenericValue dateDimNew = EntityUtil.getFirst(listDateDimNew);
	
		List<GenericValue> listFacilityFactNew = delegator.findList("FacilityFact", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", "mydinh", "productDimId", productDim.dimensionId, "dateDimId", dateDimNew.dimensionId)), null, null, null, false);
		GenericValue facilityFactNew = EntityUtil.getFirst(listFacilityFactNew);
		if(facilityFactNew != null){
			inventoryOfMonth = facilityFactNew.inventoryTotal;
		}else{
		inventoryOfMonth = new BigDecimal(-1);
		}
		System.out.println("thangnay:" +inventoryOfMonth);
	
	
	//end tinh ton kho thang nay
		
		String customTimePeriodId2 = x.customTimePeriodIdOfSales;
	
		Set<String> fieldToSelects2 = FastSet.newInstance();
		fieldToSelects2.add("salesForecastId");
		//	fieldToSelects2.add("fromDate");
		//sua them dieu kien vung by dat
		List<GenericValue> listSalesForecast = delegator.findList("SalesForecast", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", customTimePeriodId2, "internalPartyId", areaPlan)), fieldToSelects2, null, null, false);
		GenericValue r = EntityUtil.getFirst(listSalesForecast);
			if(r != null){
				String salesForecastId = r.salesForecastId;
				Set<String> fieldToSelects3 = FastSet.newInstance();
				fieldToSelects3.add("salesForecastId");
				fieldToSelects3.add("quantity");
				List<GenericValue> listSalesForecastDetail = delegator.findList("SalesForecastDetail", EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", salesForecastId, "productId", productId)), fieldToSelects3, null, null, false);
				GenericValue salesForecastDetail = EntityUtil.getFirst(listSalesForecastDetail);
				if(salesForecastDetail != null){
					quantity = salesForecastDetail.quantity;
				}
			}
		String periodName = x.productPlanName;
		
		List<GenericValue> listExistProductPlanItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", x.productPlanId, "productId", productId)), null, null, null, false);
		GenericValue existProductPlanItem = EntityUtil.getFirst(listExistProductPlanItem);
		
		if(existProductPlanItem != null){
			planImport = existProductPlanItem.planQuantity;
			// cho nay tiep tuc kiem tra ton thang truoc, Neu ton tai thi ton du bao thang nay = ton truoc + ke hoach - Sales, neu chua thi ton du bao thang nay = ton du bao truoc + KH - Sales
			// 
			// Sau do thuc hien update ton du bao vao ke hoach tuong ung neeus nhu ton thang nay chua co
//			BigDecimal temp = new BigDecimal(-1);
			
			//hiện tại thì đang cộng thêm số planImport(mới chỉ là plan thôi) nhưng thực nhập sẽ khác, lúc đó khi có số thực nhập thì sẽ cộng số thực nhâp này mới chính xác được các con số dự báo
			if(tonTruoc.compareTo(new BigDecimal(-1)) == 0){
				tonCuoiThang = inventoryForecast + planImport - quantity;
				//tiep tuc kiem tra xem co ton thang nay chua thi update ton du bao thang này
			}else{
				tonCuoiThang = tonTruoc + planImport - quantity;
				//tiep tuc kiem tra xem co ton thang nay chua thi update ton du bao thang này
			}
			
//			tonCuoiThang = planImport - quantity;
			banCuaNgay = (tonCuoiThang / (quantity/30));
			
//			System.out.println("hhh:" +planImport);
		}else if (!quantity.equals(BigDecimal.ZERO)) {
			min = Math.ceil(quantity + (quantity/30)*7);
			def result = Math.ceil(min.toInteger() / palet.toInteger());
			planImport = result*palet;
			tonCuoiThang = planImport - quantity;
			banCuaNgay = (tonCuoiThang / (quantity/30));
		}
		details = [:];
		details.periodName = periodName;
		details.planImport = planImport;
		details.tonCuoiThang = tonCuoiThang;
		details.banCuaNgay = banCuaNgay;
		details.tonTruoc = tonTruoc;
		details.quantity = quantity;
		details.inventoryOfMonth = inventoryOfMonth;
		listProductSalesForcast.addAll(details);
}


context.listProductSalesForcast = listProductSalesForcast;
context.listMonth = listMonth;
context.productName = product.get("internalName");
context.packing = packing;
context.year = year.periodName;
context.pallet = palet;
context.productId = productId;
