import org.ofbiz.base.util.*;

import groovy.xml.Entity;
import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

import java.util.List;

// Put the result of CategoryWorker.getRelatedCategories into the separateRootType function as attribute.
// The separateRootType function will return the list of category of given catalog.
// PLEASE NOTE : The structure of the list of separateRootType function is according to the JSON_DATA plugin of the jsTree.
List<EntityCondition> type = FastList.newInstance();
type.add(EntityCondition.makeCondition("geoTypeId", "PROVINCE"));
type.add(EntityCondition.makeCondition("geoTypeId", "STATE"));
List<EntityCondition> cond = FastList.newInstance();
cond.add(EntityCondition.makeCondition(type, EntityOperator.OR));
cond.add(EntityCondition.makeCondition("geoAssocTypeId", "REGIONS"));
cond.add(EntityCondition.makeCondition("geoIdFrom", "VNM"));

provinces = delegator.findList("GeoAssocAndGeoToDetail", EntityCondition.makeCondition(cond, EntityOperator.AND), null, null, null, true);
context.provinces = provinces;
