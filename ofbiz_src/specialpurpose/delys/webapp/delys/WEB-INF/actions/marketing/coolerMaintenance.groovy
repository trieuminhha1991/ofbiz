import org.ofbiz.base.util.*;
import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;
import org.ofbiz.entity.*;
import java.util.List;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

// Put the result of CategoryWorker.getRelatedCategories into the separateRootType function as attribute.
// The separateRootType function will return the list of category of given catalog.
// PLEASE NOTE : The structure of the list of separateRootType function is according to the JSON_DATA plugin of the jsTree.

EntityCondition type = EntityCondition.makeCondition("fixedAssetTypeId",EntityOperator.EQUALS,"COOLER");

listCooler = delegator.findList("FixedAsset", type, UtilMisc.toSet("fixedAssetId", "fixedAssetName", "serialNumber"), UtilMisc.toList("fixedAssetId"), null, true);

context.listCooler = listCooler;
