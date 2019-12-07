import java.math.BigDecimal;
import java.util.*;
import java.security.Policy.Parameters;
import java.sql.Timestamp;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.entity.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.order.order.*;
import org.ofbiz.party.contact.*;
import org.ofbiz.product.inventory.InventoryWorker;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.accounting.payment.*;
import com.lowagie.text.pdf.PRAcroForm;

String firstYear = (String)parameters.get("firstYear");
String lastYear = (String)parameters.get("lastYear");
String detail = (String)parameters.get("detail");
String facilityId = (String)parameters.get("facility");
String select = (String)parameters.get("select");
String selectWeek = (String)parameters.get("selectWeek");
String select_product = (String)parameters.get("selectProduct");
// create array contain week from selectWeek
String[] arrWeek = null;
if(selectWeek != null && selectWeek != "other"){
	arrWeek = selectWeek.split(",");
}

//create array contain product from string select_product
String[] arrProduct = null;
if(select_product != null){
arrProduct = select_product.split(",");
}
//get array of month from select
String[] selectAll = null;
if(select != null && select != "other"){
	selectAll = select.split(",");
//	System.out.println("in:" +selectAll);
}


if(detail != null){
	
}
int firstYearInt = 0;
if(firstYear != null && facilityId != null && facilityId != "" && firstYear != ""){
firstYearInt = firstYear.toInteger();

int lastYearInt = firstYearInt;
if(lastYear != null && lastYear != ""){
	lastYearInt = lastYear.toInteger();
}

System.out.println("kk:" +lastYearInt);

List<Map> resultHis = new ArrayList<Map>();
//begin get history with param years
for(int i = firstYearInt; i < lastYearInt +1; i++){
	List<Map> resultProItemYear = new ArrayList<Map>();
	Long y = i.toLong();
//begin get history and arrProduct
	for(int k = 0; k < arrProduct.size(); k++){
		String productDimId = arrProduct[k];
		if(productDimId == "all"){continue;}
		
		orderFields = ["productDimId"];
		Conds = [];
		Conds.add(EntityCondition.makeCondition("yearName", EntityOperator.EQUALS, y));
		Conds.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
		Conds.add(EntityCondition.makeCondition("productDimId", EntityOperator.EQUALS, productDimId));
		yearCond = EntityCondition.makeCondition(Conds, EntityOperator.AND);
		List<GenericValue> listYearAll = delegator.findList("HistoryInventoryOfYear", yearCond, null, null, null, false);
	if(listYearAll != null){	
		GenericValue listYear = EntityUtil.getFirst(listYearAll);
		if(listYear != null){
		List<GenericValue> listOut = delegator.findList("FacilityFact", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId,"dateDimId", listYear.dateDimId, "productDimId", productDimId)),null, null, null, false);
		GenericValue listOut1 = EntityUtil.getFirst(listOut);
		listYear.put("inventoryTotal", listOut1.inventoryTotal);
		listYear.put("availableToPromiseTotal", listOut1.availableToPromiseTotal);
		resultProItemYear.addAll(listYear);
		}
	}
	}

// create history month of year
List<Map> listMonthMap = new ArrayList<Map>();
if(select != "other"){
for(int j = 0; j < selectAll.size(); j++){
String getSelect = selectAll[j];
if(getSelect == "all"){
	continue;	
}
Long x = getSelect.toLong();
List<Map> resultProItemMonth = new ArrayList<Map>();
for(int k = 0; k < arrProduct.size(); k++){
	String productDimId = arrProduct[k];
	if(productDimId == "all"){continue;}

CondsMonth = [];
CondsMonth.add(EntityCondition.makeCondition("yearName", EntityOperator.EQUALS, y));
CondsMonth.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
CondsMonth.add(EntityCondition.makeCondition("monthOfYear", EntityOperator.EQUALS, x));
CondsMonth.add(EntityCondition.makeCondition("productDimId", EntityOperator.EQUALS, productDimId));
monthCond = EntityCondition.makeCondition(CondsMonth, EntityOperator.AND);
List<GenericValue> listMonth = delegator.findList("HistoryInventoryOfMonth", monthCond, null, orderFields, null, false);

//get inventoryTotal and ATP of month then update into listMonth
if(listMonth != null){
	GenericValue listMonthPro = EntityUtil.getFirst(listMonth);
	if(listMonthPro != null){
		List<GenericValue> listOut = delegator.findList("FacilityFact", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId,"dateDimId", listMonthPro.dateDimId, "productDimId", listMonthPro.productDimId)), null, null, null, false);
		GenericValue listOut1 = EntityUtil.getFirst(listOut);
		listMonthPro.put("inventoryTotal", listOut1.inventoryTotal);
		listMonthPro.put("availableToPromiseTotal", listOut1.availableToPromiseTotal);
		resultProItemMonth.addAll(listMonthPro);
	}
}
}
	mapMonth = [:];
	mapMonth.month = x;
	mapMonth.monthHis = resultProItemMonth;
	listMonthMap.addAll(mapMonth);
}
}

//Create history week of year

List<Map> listWeekMap = new ArrayList<Map>();
if(selectWeek != "other"){
for(int j = 0; j < arrWeek.size(); j++){
String getSelectWeek = arrWeek[j];
if(getSelectWeek == "all"){
	continue;
}
Long x = getSelectWeek.toLong();
List<Map> resultProItemWeek = new ArrayList<Map>();
for(int k = 0; k < arrProduct.size(); k++){
	String productDimId = arrProduct[k];
	if(productDimId == "all"){continue;}

CondsWeek = [];
CondsWeek.add(EntityCondition.makeCondition("yearName", EntityOperator.EQUALS, y));
CondsWeek.add(EntityCondition.makeCondition("facilityId", EntityOperator.EQUALS, facilityId));
CondsWeek.add(EntityCondition.makeCondition("weekOfYear", EntityOperator.EQUALS, x));
CondsWeek.add(EntityCondition.makeCondition("productDimId", EntityOperator.EQUALS, productDimId));
weekCond = EntityCondition.makeCondition(CondsWeek, EntityOperator.AND);
List<GenericValue> listWeekAll = delegator.findList("HistoryInventoryOfWeek", weekCond, null, orderFields, null, false);

//get inventoryTotal and ATP of month then update into listMonth
if(listWeekAll != null){
	GenericValue listWeek = EntityUtil.getFirst(listWeekAll);
	if(listWeek != null){
		List<GenericValue> listOut = delegator.findList("FacilityFact", EntityCondition.makeCondition(UtilMisc.toMap("facilityId", facilityId,"dateDimId", listWeek.dateDimId, "productDimId", productDimId)), null, null, null, false);
		GenericValue listOut1 = EntityUtil.getFirst(listOut);
		listWeek.put("inventoryTotal", listOut1.inventoryTotal);
		listWeek.put("availableToPromiseTotal", listOut1.availableToPromiseTotal);
		resultProItemWeek.addAll(listWeek);
	}
}
}
	mapWeek = [:];
	mapWeek.week = x;
	mapWeek.weekHis = resultProItemWeek;
	listWeekMap.addAll(mapWeek);
}
}

// create End History
mapHis = [:];
mapHis.year = y;
mapHis.history = resultProItemYear;
mapHis.historyMonth = listMonthMap;
mapHis.historyWeek = listWeekMap;
resultHis.addAll(mapHis);
}
//
//System.out.println("aa:" +resultHis);
context.facilityFacts = resultHis;
for(facility in resultHis){
	if(facility.history){
//	System.out.println("aa:");
		context.products = facility;
		break;
	}
}
//context.products = resultHis.get(0);
System.out.println("dd:" +resultHis);
}
//System.out.println("dd:" +resultHis.get(0));