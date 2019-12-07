import java.util.*;
import org.ofbiz.base.util.*;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import org.ofbiz.entity.condition.EntityJoinOperator;

List<EntityCondition> listAllConditions = FastList.newInstance();
listAllConditions.add(EntityCondition.makeCondition(UtilMisc.toMap("productTypeId", "FINISHED_GOOD", "isVariant", "N")));
List<GenericValue> listProductVirtual = delegator.findList("Product", EntityCondition.makeCondition(listAllConditions, EntityJoinOperator.AND), null, null, null, false);

context.listProductVirtual = listProductVirtual;

String ballotBox = "&bsemi;";
context.ballotBox = ballotBox;