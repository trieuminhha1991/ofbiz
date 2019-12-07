
import org.ofbiz.base.util.*;

import javolution.util.FastMap;
import javolution.util.FastList;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

// Put the result of CategoryWorker.getRelatedCategories into the separateRootType function as attribute.
// The separateRootType function will return the list of category of given catalog.
// PLEASE NOTE : The structure of the list of separateRootType function is according to the JSON_DATA plugin of the jsTree.
EntityCondition type = EntityCondition.makeCondition("uomTypeId", "PRODUCT_PACKING");
uom = delegator.findList("Uom", type, null, null, null, true);

context.uoms = uom;

EntityCondition gtype = EntityCondition.makeCondition("geoTypeId", "PROVINCE");
EntityCondition geoIdFrom = EntityCondition.makeCondition("geoIdFrom", "VNM");
List<EntityCondition> condLis = FastList.newInstance();
condLis.add(gtype);
condLis.add(geoIdFrom);
province = delegator.findList("GeoAssocAndGeoTo", EntityCondition.makeCondition(condLis, EntityOperator.AND), null, null, null, true);

context.province = province; 

mkId = parameters.id;
if(mkId != null){
	Map<String, Object> tmp = FastMap.newInstance();
	tmp.put("userLogin", userLogin);
	tmp.put("marketingCampaignId", mkId);
	res = dispatcher.runSync("getMarketingSampling", tmp);
	System.out.println("sampling" + res);
	context.marketingDetail = res.get("result");
}
