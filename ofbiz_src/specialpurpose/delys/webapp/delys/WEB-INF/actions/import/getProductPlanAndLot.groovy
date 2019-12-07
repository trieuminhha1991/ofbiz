import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

String productPlanId = parameters.productPlanId;
if (productPlanId != null){
	List<GenericValue> listPlanItems = delegator.findList("ProductPlanItem", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
	List<GenericValue> listLotByPlans = delegator.findList("ProductPlanAndLot", EntityCondition.makeCondition(UtilMisc.toMap("productPlanId", productPlanId)), null, null, null, false);
	List<Map> listLots = new ArrayList<Map>();
	List<String> listLotIds = new ArrayList<String>();
	if (!listPlanItems.isEmpty() && !listLotByPlans.isEmpty()){
		for (GenericValue lotItem : listLotByPlans){
			if (!listLotIds.contains((String)lotItem.get("lotId"))){
				listLotIds.add((String)lotItem.get("lotId"));
			}
		}
		for (String lotId : listLotIds){
			List<Map> listLotTmp = new ArrayList<Map>();
			Map mapLotTmp = [:];
			String statusId = null;
			for (GenericValue planItem : listPlanItems){
				Map mapProduct = [:];
				BigDecimal quantity = BigDecimal.ZERO;
				String productPackingUomId = null;
				String shipmentUomId = null;
				String productId = (String)planItem.get("productId");
				GenericValue planLot = delegator.findOne("ProductPlanAndLot", false, UtilMisc.toMap("lotId", lotId, "productPlanId", planItem.get("productPlanId"), "productPlanItemSeqId", planItem.get("productPlanItemSeqId")));
				if (planLot != null){
					quantity = planLot.getBigDecimal("lotQuantity");
					statusId = (String)planLot.get("statusId");
					productPackingUomId = (String)planLot.get("productPackingUomId");
					shipmentUomId = (String)planLot.get("shipmentUomId");
				}
				mapProduct.productId = productId; 
				mapProduct.quantity = quantity;
				mapProduct.productPackingUomId = productPackingUomId;
				mapProduct.shipmentUomId = shipmentUomId;
				listLotTmp.add(mapProduct);
			}
			mapLotTmp.lotId = lotId;
			mapLotTmp.statusId = statusId;
			mapLotTmp.productPlanId = productPlanId;
			mapLotTmp.listProductByLot = listLotTmp;
			listLots.add(mapLotTmp);
		}
	}
	context.listLots = listLots;
	context.listPlanItems = listPlanItems;
}