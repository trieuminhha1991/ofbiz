
import java.util.*;

import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.basesales.util.SalesUtil

import javolution.util.FastMap;;

String parentProductId = parameters.parentProductId;
String productId = parameters.productId;
if (parentProductId) {
	GenericValue featureTypesGV = delegator.findOne("ProductAttribute", ["productId" : parentProductId, "attrName": "featureTypes"], false);
	if (featureTypesGV) {
		String featureTypesStr = featureTypesGV.attrValue;
		if (featureTypesStr) {
			List<String> featureTypeIds = SalesUtil.processKeyProperty(featureTypesStr);
			if (featureTypeIds) {
				List<GenericValue> featureTypes = delegator.findList("ProductFeatureType", EntityCondition.makeCondition("productFeatureTypeId", EntityOperator.IN, featureTypeIds), null, null, null, false);
				context.featureTypes = featureTypes;
			}
		}
	}
}
if (productId) {
	List<EntityCondition> conds = new ArrayList<GenericValue>();
	conds.add(EntityCondition.makeCondition("productId", productId));
	conds.add(EntityUtil.getFilterByDateExpr());
	List<String> productFeatureIds = EntityUtil.getFieldListFromEntityList(delegator.findList("ProductFeatureAppl", EntityCondition.makeCondition(conds), null, null, null, false), "productFeatureId", true);
	if (productFeatureIds) {
		Map<String, Object> productFeatureIdsMap = FastMap.newInstance();
		for (String productFeatureId : productFeatureIds) {
			GenericValue productFeature = delegator.findOne("ProductFeature", ["productFeatureId": productFeatureId], false);
			if (productFeature) {
				productFeatureIdsMap.put(productFeature.productFeatureTypeId, productFeature.productFeatureId);
			}
		}
		context.productFeatureIdsMap = productFeatureIdsMap;
	}
}
