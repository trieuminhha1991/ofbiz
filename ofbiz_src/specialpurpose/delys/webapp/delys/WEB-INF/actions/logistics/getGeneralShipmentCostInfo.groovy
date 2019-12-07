import java.util.List;

import org.ofbiz.base.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.model.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.DispatchContext;

List<GenericValue> listShipmentRoutes = delegator.findList("ShipmentRouteSegment",  EntityCondition.makeCondition(UtilMisc.toMap("shipmentId", shipmentId)), null, null, null, false);
actualTransportCost = BigDecimal.ZERO;
actualServiceCost = BigDecimal.ZERO;
actualOtherCost = BigDecimal.ZERO;
actualCost = BigDecimal.ZERO;
if (!listShipmentRoutes.isEmpty()){
	for (GenericValue route : listShipmentRoutes){
		if (route.getBigDecimal("actualCost") != null){
			actualCost = actualCost.add(route.getBigDecimal("actualCost"));
		}
		if (route.getBigDecimal("actualTransportCost") != null){
			actualTransportCost = actualTransportCost.add(route.getBigDecimal("actualTransportCost"));
		}
		if (route.getBigDecimal("actualServiceCost") != null){
			actualServiceCost = actualServiceCost.add(route.getBigDecimal("actualServiceCost"));
		}
		if (route.getBigDecimal("actualOtherCost")){
			actualOtherCost = actualOtherCost.add(route.getBigDecimal("actualOtherCost"));
		}
	}
}
context.actualTransportCost = actualTransportCost;
context.actualServiceCost = actualServiceCost;
context.actualOtherCost = actualOtherCost;
context.actualCost = actualCost;
