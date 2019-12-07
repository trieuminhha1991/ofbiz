
import java.util.List;

import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.product.catalog.CatalogWorker;
import org.ofbiz.base.util.UtilMisc;

import com.olbius.basesales.util.SalesUtil;

import javolution.util.FastList
import javolution.util.FastMap;
import javolution.util.FastSet;;

productId = parameters.productId;
List<Map<String, Object>> listProductGoodIds = FastList.newInstance();
Map<String, Object> productInfo = FastMap.newInstance();

if (productId) {
	GenericValue product = delegator.findOne("Product", ["productId": productId], false);
	if (product) {
		productInfo = product.getAllFields();
		
		// price
		BigDecimal unitPrice = null;
		GenericValue productTemp = delegator.findOne("ProductTempData", ["productId": productId], false);
		if (productTemp) {
			unitPrice = productTemp.getBigDecimal("unitPrice");
			productInfo.put("unitPrice", unitPrice);
			productInfo.put("currencyUomId", productTemp.currencyUomId);
		}
		
		Set<String> listSelectFields = FastSet.newInstance();
		listSelectFields.add("productId");
		listSelectFields.add("productCode");
		listSelectFields.add("measureUomId");
		listSelectFields.add("measureValue");
		listSelectFields.add("idValue");
		listSelectFields.add("goodIdentificationTypeId");
		List<String> listSortFields = UtilMisc.toList("idValue");
		List<GenericValue> listGoodIds = delegator.findList("GoodIdentificationMeasureAndProduct", EntityCondition.makeCondition("productId", productId), listSelectFields, listSortFields, null, false);
		if (UtilValidate.isNotEmpty(listGoodIds)) {
			for (GenericValue item : listGoodIds) {
				Map<String, Object> itemMap = item.getAllFields();
				
				// price of package
				BigDecimal weight = item.measureValue;
				if (unitPrice != null && weight != null) {
					itemMap.put("productPrice", weight.multiply(unitPrice));
				}
				
				// packing date
				itemMap.put("packingDate", nowTimestamp);
				
				listProductGoodIds.add(itemMap);
			}
		}
	}
}
context.listProductGoodIds = listProductGoodIds;
context.productInfo = productInfo;
