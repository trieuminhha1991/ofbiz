import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityFindOptions;

String productPlanHeaderId = (String)parameters.productPlanHeader;

//System.out.println ("II:" + productPlanHeaderId);

field = ["lotId"] as Set;
EntityFindOptions options = new EntityFindOptions();
options.setDistinct(true);

List<Map> productId = new ArrayList<Map>();
fieldPro = ["productId"] as Set;
orderField = ["productId"];
List<GenericValue> listProductId = delegator.findList("LotAndPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeaderId)), fieldPro, orderField, options, false); 
productId.addAll(listProductId);

List<GenericValue> listLot = delegator.findList("ProductPlanAndLot", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanHeaderId)), field, null, options, false);

List<Map> listLotToDoAgreement = new ArrayList<Map>();
for(GenericValue x: listLot){
//	System.out.println("DD :" +x.lotId);
	List<GenericValue> listProductLot = delegator.findList("LotAndPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("lotId", x.lotId)), null, orderField, null, false);
	map = [:];
	map.listProductLot = listProductLot;
	map.lot = x.lotId;
	listLotToDoAgreement.addAll(map);
}
System.out.println ("FFFF:" +listLotToDoAgreement);


context.listLotToDoAgreement = listLotToDoAgreement;
context.productId = productId;


