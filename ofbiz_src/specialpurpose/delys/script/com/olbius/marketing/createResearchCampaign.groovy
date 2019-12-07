import org.ofbiz.base.util.*;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;

import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.EntityCondition;

import java.util.List;

// Put the result of CategoryWorker.getRelatedCategories into the separateRootType function as attribute.
// The separateRootType function will return the list of category of given catalog.
// PLEASE NOTE : The structure of the list of separateRootType function is according to the JSON_DATA plugin of the jsTree.

costTypeList = delegator.findList("MarketingCostType", null, null, null, null, true);
System.out.println(costTypeList);
products = delegator.findList("Product", null, null, null, null, true);
province = delegator.findList("Geo", EntityCondition.makeCondition("geoTypeId", "PROVINCE"), null, null, null, true);
context.costTypeList = costTypeList;
context.products = products;
context.province = province;
