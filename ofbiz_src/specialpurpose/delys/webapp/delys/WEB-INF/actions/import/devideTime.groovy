import java.text.SimpleDateFormat;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

String productPlanHeader = (String)parameters.productPlanHeader;
//String customTimePeriodId = (String)parameters.customTimePeriodId;

GenericValue productPlanHeaderGe = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanHeader), false);
String customTimePeriodId = (String)productPlanHeaderGe.customTimePeriodId;
GenericValue year = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);


//

fieldOrders = ["productId"];
fieldHeader = ["productPlanId"];
List<GenericValue> listItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeader)), null, fieldOrders, null, false);

//BigDecimal palletTotal = BigDecimal.ZERO;
int palletTotal = 0;

//List<Map> result = new ArrayList<Map>();
List<Map> listContainer = new ArrayList<Map>();
List<Map> listRemainCont = new ArrayList<Map>();
List<Map> listOtherContainer = new ArrayList<Map>();
List<Map> listGreaterCont = new ArrayList<Map>();
List<Map> listLessCont = new ArrayList<Map>();
int totalRemain = 0;
List<Map> listProduct = new ArrayList<Map>();
int sizelist = 0;
List<Map> listProductAndQuantity = new ArrayList<Map>();
int totalPallet = 0;
for(GenericValue x : listItem){

	productQuantity = [:];
	productQuantity.productId = (String)x.productId;
	productQuantity.quantityUomId = (String)x.quantityUomId;
	BigDecimal planQuantityBig = (BigDecimal)x.planQuantity;
	int planQuantityInt = planQuantityBig.intValue();
	productQuantity.quantity = planQuantityInt;
	

	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", (String)x.productId), false);
	String uomOfProduct = (String)product.quantityUomId;
	//	Set<String> fieldToSelects = FastSet.newInstance();
	//	fieldToSelects.add("quantityConvert");
	GenericValue cfPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId", (String)x.productId, "uomFromId", "PALLET", "uomToId", uomOfProduct), false);
	int quantityPallet = planQuantityInt;
	int quantityConvert = 1;
	//	listProductAndQuantity.add(productQuantity);
	if(cfPacking != null){
		quantityConvert = cfPacking.quantityConvert;
		productQuantity.quantityConvert = quantityConvert;
		quantityPallet = planQuantityInt/(quantityConvert);
		productQuantity.quantityPallet = quantityPallet;
	}
	
	totalPallet = totalPallet + quantityPallet;
	
	listProductAndQuantity.add(productQuantity);
	
}
//System.out.println("aaaa:"+listProductAndQuantity);




//

Date fromDateYear = (Date)year.getDate("fromDate");

Calendar calendar = Calendar.getInstance();
calendar.setTime(fromDateYear);
String yearStr = calendar.get(Calendar.YEAR).toString();

//System.out.println("aa: "+yearStr);
orderField = ["customTimePeriodId"];


List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
GenericValue listCustomTimeMonth = delegator.findOne("CustomTimePeriod", UtilMisc.toMap("customTimePeriodId", customTimePeriodId), false);

//for(GenericValue customTime : listCustomTimeMonth){
Map resultMap = [:];
if(listCustomTimeMonth != null){
	List<Map<String, Object>> listWeek = new ArrayList<Map<String, Object>>();
	List<GenericValue> listCustomTimeWeek = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", (String)listCustomTimeMonth.get("customTimePeriodId"), "periodTypeId", "IMPORT_WEEK")), null, orderField, null, false);
	for(GenericValue customTimeWeek : listCustomTimeWeek){

		calendar.setTime((Date)customTimeWeek.getDate("fromDate"));
		String weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR).toString();
		List<GenericValue> productPlanItemChild = new ArrayList<GenericValue>();
		List<GenericValue> childProductPlanHeader = delegator.findList("ProductPlanHeader", EntityCondition.makeCondition(UtilMisc.toMap("parentProductPlanId", productPlanHeader, "customTimePeriodId",(String)customTimeWeek.customTimePeriodId)), null, fieldHeader, null, false);
		GenericValue child = EntityUtil.getFirst(childProductPlanHeader);
		if(child != null){
			productPlanItemChild = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", (String)child.get("productPlanId"))), null, fieldOrders, null, false);
//			for(GenericValue childItem : productPlanItemChild){
//				productItem.add(childItem);
//			}
			
		}
		 
		mapWeek = [:];
		mapWeek.timeWeek = customTimeWeek;
		mapWeek.weekOfYear = weekOfYear;
		mapWeek.product = productPlanItemChild;
		listWeek.add(mapWeek);

	}

	map = [:];
	int contQuantity = totalPallet/33;
	map.month = listCustomTimeMonth;
	map.week = listWeek;
	map.product = listProductAndQuantity;
	map.container = contQuantity;
//	result.add(map);
	resultMap = map;
}
//}

context.result = resultMap;
context.productPlanHeader = productPlanHeader;
//System.out.println("bb:" +resultMap);

