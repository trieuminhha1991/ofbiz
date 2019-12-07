import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javolution.util.FastSet;

import org.apache.lucene.search.FieldCache.IntParser;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;


String productPlanHeader = (String)parameters.productPlanHeader;


GenericValue listProductPlanHeader = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanHeader), false);
String customTimePeriodIdOfPlan = (String)listProductPlanHeader.customTimePeriodId;

List<GenericValue> customTimePeriodWeek = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("parentPeriodId", customTimePeriodIdOfPlan, "periodTypeId", "IMPORT_WEEK")), null, null, null, false);
int countWeek = 0;
for(GenericValue timeWeek : customTimePeriodWeek){
	countWeek++;
}

fieldOrders = ["productId"];
List<GenericValue> listItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeader)), null, fieldOrders, null, false);

//BigDecimal palletTotal = BigDecimal.ZERO;
int palletTotal = 0;

List<Map> result = new ArrayList<Map>();
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

	GenericValue timePeriodOfSales = delegator.findOne("ProductPlanHeader", UtilMisc.toMap("productPlanId", productPlanHeader), false);
	fieldSaleFC = ["salesForecastId"] as Set;
	List<GenericValue> listSFCs = delegator.findList("SalesForecast", EntityCondition.makeCondition(UtilMisc.toMap("customTimePeriodId", (String)timePeriodOfSales.customTimePeriodIdOfSales)), fieldSaleFC, null, null, false);
	GenericValue sFs = EntityUtil.getFirst(listSFCs);
	if(sFs != null){
		List<GenericValue> listSFCDetail = delegator.findList("SalesForecastDetail", EntityCondition.makeCondition(UtilMisc.toMap("salesForecastId", (String)sFs.salesForecastId, "productId", (String)x.productId)), null, null, null, false);
		GenericValue SFCs = EntityUtil.getFirst(listSFCDetail);
		if(SFCs != null){
			int quantitySalesFC = SFCs.quantity;
			int sumDayForSales = (quantitySalesFC*8)/30;
			int inventoryFC = ((BigDecimal)x.inventoryForecast).intValue();

			if(inventoryFC <= sumDayForSales && quantityPallet < 33){
				productQuantity.week = 1;
			}else if(inventoryFC > sumDayForSales && quantityPallet < 33){
				productQuantity.week = 2;
			}else{
				productQuantity.week = 0;
			}

		}

	}

	if(quantityPallet < 33){
		listLessCont.add(productQuantity);
	}else{
		listGreaterCont.add(productQuantity);
	}
	listProductAndQuantity.add(productQuantity);
	
	
	
	
}

System.out.println("aaa:" +listProductAndQuantity.size());




int[] week = new int[countWeek];
int totalCont = totalPallet/33;
int container = totalCont/countWeek;
int remainCont = totalCont % countWeek;

for(int i =0; i < week.size(); i++){
	week[i] = container;
}

for(int j = 0; j < remainCont; j++){
	week[j] += 1;
	//	System.out.println ("bb:" +week[j]);
}

for(Map y : listProductAndQuantity){
	//	System.out.println ("mm:");
	if(y.quantityPallet < 33){


	}else{



	}

}
List<List> resultDevide = new ArrayList<List>();
for(Map less : listLessCont){
	if(less.week == 1){
		
		
	}else(less.week == 2){
		
	}
	
	devideMap = [:];
	List<Map> oneCont = new ArrayList<Map>();
	oneCont.add(less);
	int quantityLess = less.quantityPallet;

	int flag = 0;
	for(Map greater : listGreaterCont){
		if(flag == 0){
			int quantityGreater = greater.quantityPallet;
			int remainPL = 33 - quantityLess;
			if(quantityGreater < remainPL){
				greater.quantityPallet = quantityGreater;
				oneCont.add(greater);
				quantityLess = quantityLess + quantityGreater;
			}else{
				greater.quantityPallet = remainPL;
				oneCont.add(greater);
				greater.quantityPallet = quantityGreater - remainPL;
				flag = 1;
			}

//			greater.quantityPallet = 3;
//			oneCont.add(greater);
		}
	}
	resultDevide.add(oneCont);
	//	System.out.println("hhh:" +oneCont);
}

int addRemainPallet = 0;
List<Map> oneContRe = new ArrayList<Map>();
for(Map greater : listGreaterCont){
	
	int realCont = (greater.quantityPallet)/33;
	int reCont = (greater.quantityPallet)%33;
	System.out.println("RR:" + reCont);
	
	
	addRemainPallet = addRemainPallet + reCont;
	if(realCont > 0){
		for(int i = 0; i < realCont; i++){
			List<Map> oneCont = new ArrayList<Map>();
			greater.quantityPallet = 33;
			oneCont.add(greater);
			resultDevide.add(oneCont);
		}
	}
	
	if(addRemainPallet < 33){
//		List<Map> oneCont = new ArrayList<Map>();
		greater.quantityPallet = reCont;
		oneContRe.add(greater);
//		greater.quantityPallet = 33;
	}else{
		greater.quantityPallet = reCont - (addRemainPallet%33);
		oneContRe.add(greater);
		resultDevide.add(oneContRe);
		oneContRe.clear();
		addRemainPallet = addRemainPallet%33;
		greater.quantityPallet = addRemainPallet%33;
		oneContRe.add(greater);
	}
	System.out.println("kk:" +greater.week);
}

System.out.println("kk:" +resultDevide.size());
//System.out.println("hh:" +listGreaterCont);

resultMap = [:];
resultMap.only = listContainer;
resultMap.more = listOtherContainer;
context.result = resultMap;
context.listProduct = listProduct;


