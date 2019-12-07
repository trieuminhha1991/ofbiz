/*
* Prepare for Delivery Note
*/
import java.util.ArrayList;
import com.olbius.accounting.appr.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import com.olbius.util.*
import java.util.Calendar;
import org.ofbiz.base.util.UtilValidate;

deliveryId = parameters.deliveryId;
delivery = delegator.findOne("Delivery", UtilMisc.toMap("deliveryId",deliveryId), false);

listItem = new ArrayList<DeliveryItemEntity>();
listDeliveryItem = delegator.findList("DeliveryItem", EntityCondition.makeCondition([deliveryId : deliveryId]), null, null, null, false);
listConfig = new ArrayList<ConfigEntity>();
total = 0;
for(GenericValue item: listDeliveryItem ){
	orderItem = delegator.findOne("OrderItem", UtilMisc.toMap("orderId",item.getString("fromOrderId"), "orderItemSeqId", item.getString("fromOrderItemSeqId")), false);
	deliveryItem = new DeliveryItemEntity();
	deliveryItem.setActualExportedQuantity(item.getBigDecimal("actualExportedQuantity"));
	deliveryItem.setProductName(orderItem.getString("itemDescription"));
	//Check Uom
	if(UtilValidate.isNotEmpty(orderItem.getString("quantityUomId")) && UtilValidate.isNotEmpty(orderItem.getString("alternativeQuantity")) && UtilValidate.isNotEmpty(orderItem.getString("alternativeUnitPrice"))){
		product = delegator.findOne("Product", UtilMisc.toMap("productId",orderItem.getString("productId")), false);
		uom = delegator.findOne("Uom", UtilMisc.toMap("uomId",orderItem.getString("quantityUomId")), false);
		deliveryItem.setUnit(uom.getString("description"));
		deliveryItem.setUnitPrice(orderItem.getBigDecimal("alternativeUnitPrice"));
		deliveryItem.setTotal(CurrencyUtil.formatCurrency((item.getBigDecimal("actualExportedQuantity") * orderItem.getBigDecimal("alternativeUnitPrice")).toString(),delivery.getString("currencyUomId")));
		total += item.getBigDecimal("actualExportedQuantity") * orderItem.getBigDecimal("alternativeUnitPrice");
		if (orderItem.getString("quantityUomId").equals(product.getString("quantityUomId"))){
			uomFrom = delegator.findOne("Uom", UtilMisc.toMap("uomId",orderItem.getString("quantityUomId")), false);
			uomTo = uomFrom;
			configPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId",orderItem.getString("productId"), "uomFromId", orderItem.getString("quantityUomId"), "uomToId", product.getString("quantityUomId")), false);
			if (configPacking == null){
				newConfig = delegator.makeValue("ConfigPacking");
				newConfig.put("productId", orderItem.getString("productId"));
				newConfig.put("uomFromId", orderItem.getString("quantityUomId"));
				newConfig.put("uomToId", orderItem.getString("quantityUomId"));
				newConfig.put("quantityConvert", BigDecimal.ONE);
				delegator.createOrStore(newConfig);
				configPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId",orderItem.getString("productId"), "uomFromId", orderItem.getString("quantityUomId"), "uomToId", product.getString("quantityUomId")), false);
			}
		} else {
			configPacking = delegator.findOne("ConfigPacking", UtilMisc.toMap("productId",orderItem.getString("productId"), "uomFromId", orderItem.getString("quantityUomId"), "uomToId", product.getString("quantityUomId")), false);
			uomFrom = delegator.findOne("Uom", UtilMisc.toMap("uomId",configPacking.getString("uomFromId")), false);
			uomTo = delegator.findOne("Uom", UtilMisc.toMap("uomId",configPacking.getString("uomToId")), false);
		}
		config = new ConfigEntity();
		config.setUomFrom(uomFrom.getString("description"));
		config.setUomTo(uomTo.getString("description"));
		config.setQuantityConvert(configPacking.getBigDecimal("quantityConvert"));
		config.setProductName(product.getString("productName"));
		listConfig.add(config);
	}else{
		product = delegator.findOne("Product", UtilMisc.toMap("productId",orderItem.getString("productId")), false);
		uom = delegator.findOne("Uom", UtilMisc.toMap("uomId",product.getString("quantityUomId")), false);
		deliveryItem.setUnit(uom.getString("description"));
		deliveryItem.setUnitPrice(orderItem.getBigDecimal("unitPrice"));
		deliveryItem.setTotal(CurrencyUtil.formatCurrency((item.getBigDecimal("actualExportedQuantity") * orderItem.getBigDecimal("unitPrice")).toString(),delivery.getString("currencyUomId")));
		total += item.getBigDecimal("actualExportedQuantity") * orderItem.getBigDecimal("unitPrice");
	}
	deliveryItem.setCode(orderItem.getString("productId"));
	listItem.add(deliveryItem);
}
cal = Calendar.getInstance();
year = cal.get(Calendar.YEAR);
month = cal.get(Calendar.MONTH);
day = cal.get(Calendar.DAY_OF_MONTH);

context.listConfig = listConfig;
context.year = year;
context.month = month;
context.day = day;
context.listItem = listItem;
context.total = CurrencyUtil.formatCurrency(total.toString(), delivery.getString("currencyUomId"));