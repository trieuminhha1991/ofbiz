import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javolution.util.FastSet;

import org.apache.lucene.search.FieldCache.IntParser;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;


String productPlanHeader = (String)parameters.productPlanHeader;

//System.out.println("Id:" +productPlanHeader);
fieldOrders = ["productId"];
List<GenericValue> listItem = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeader)), null, fieldOrders, null, false);

//BigDecimal palletTotal = BigDecimal.ZERO;
int palletTotal = 0;

List<Map> result = new ArrayList<Map>();
List<Map> listContainer = new ArrayList<Map>();
List<Map> listRemainCont = new ArrayList<Map>();
List<Map> listOtherContainer = new ArrayList<Map>();
int totalRemain = 0;
List<Map> listProduct = new ArrayList<Map>();
int sizelist = 0;
for(GenericValue x : listItem){

	mapProduct = [:];
	mapProduct.productId = (String)x.productId;
	mapProduct.seqItemId = (String)x.productPlanItemSeqId;

	BigDecimal planQuantity = x.planQuantity;
	int quantityInt = planQuantity.intValue();
	//	System.out.println("QQ: " +quantityInt);
	sizelist = sizelist +1;
	//get ProductId and get convert pallet to product
	GenericValue product = delegator.findOne("Product", UtilMisc.toMap("productId", (String)x.productId), false);
	String uomOfProduct = (String)product.quantityUomId;
	//	Set<String> fieldToSelects = FastSet.newInstance();
	//	fieldToSelects.add("quantityConvert");
	GenericValue cfPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId", (String)x.productId, "uomFromId", "PALLET", "uomToId", uomOfProduct), false);
	int quantityPallet = quantityInt;
	int quantityConvert = 1;
	if(cfPacking != null){
		quantityConvert = cfPacking.quantityConvert
		quantityPallet = quantityInt/(quantityConvert);
	
	//fix 33, 1 container chua 33 pallet
	int realCon = quantityPallet/33;

	for(int i = 1; i <= realCon; i++){
		container = [:];
		container.productId = (String)x.productId;
		//fix 33 of container
		container.quantity = 33*(quantityConvert);
		listContainer.addAll(container);

	}
	
	mapProduct.quantityConvert = quantityConvert;
	listProduct.addAll(mapProduct);

	int remainPallet = quantityPallet % 33;
	remainMap = [:];
	totalRemain = totalRemain + remainPallet;
	//fix 33 pallet of container
	//	System.out.println("SSSS:" +listItem.size());

	//	if(size < listItem.size()){
	if(totalRemain < 33){
		remainMap.productId = (String)x.productId;
		remainMap.quantity = remainPallet*(quantityConvert);
		remainMap.remainPallet = remainPallet;
		listRemainCont.addAll(remainMap);
	}else{
		remainMap.productId = (String)x.productId;
		
		int remain = remainPallet-(totalRemain % 33);
		remainMap.quantity = remain*(quantityConvert);
		remainMap.remainPallet = remain;
		//		System.out.println("ff:" +kk);
		listRemainCont.addAll(remainMap);
		map = [:];
		map.qq = listRemainCont;
		listOtherContainer.addAll(map);
		listRemainCont = new ArrayList<Map>();
		//		listRemainCont.clear();
		totalRemain = totalRemain % 33;
		remainMap2 = [:];
		remainMap2.productId = (String)x.productId;
		remainMap2.quantity = totalRemain*(quantityConvert);
		remainMap2.remainPallet = totalRemain;
		listRemainCont.addAll(remainMap2);
	}
	//	}
	if(sizelist == listItem.size()){
		//		System.out.println("ll:" +sizelist);
		map2 = [:];
		map2.qq = listRemainCont;
		listOtherContainer.addAll(map2);
	}
	
	}

}
resultMap = [:];
resultMap.only = listContainer;
resultMap.more = listOtherContainer;
context.result = resultMap;
context.listProduct = listProduct;

