import javolution.util.FastSet;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;

Set<String> fieldToSelects = FastSet.newInstance();
fieldToSelects.add("quotaId");
fieldToSelects.add("quotaName");
fieldToSelects.add("quotaTypeId");
fieldToSelects.add("description");
fieldToSelects.add("fromDate");
fieldToSelects.add("thruDate");
List<GenericValue> listQuota = delegator.findList("QuotaHeader", EntityCondition.makeCondition("quotaTypeId", "IMPORT_QUOTA"), fieldToSelects, null, null, false);
context.listQuota = listQuota;