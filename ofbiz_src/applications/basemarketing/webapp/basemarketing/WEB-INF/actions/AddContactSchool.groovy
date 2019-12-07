import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.entity.util.EntityUtil;
import javolution.util.FastList;
import com.olbius.dms.util.PartyHelper;

context.listGeoCOUNTRY = delegator.findList("Geo", EntityCondition.makeCondition(UtilMisc.toMap("geoTypeId", "COUNTRY")), UtilMisc.toSet("geoId", "geoName"), null, null, false);
context.listGeoPROVINCE = delegator.findList("Geo", EntityCondition.makeCondition(UtilMisc.toMap("geoTypeId", "PROVINCE")), UtilMisc.toSet("geoId", "geoName"), null, null, false);
context.listGeoDISTRICT = delegator.findList("Geo", EntityCondition.makeCondition(UtilMisc.toMap("geoTypeId", "DISTRICT")), UtilMisc.toSet("geoId", "geoName"), null, null, false);
context.listGeoWARD = delegator.findList("Geo", EntityCondition.makeCondition(UtilMisc.toMap("geoTypeId", "WARD")), UtilMisc.toSet("geoId", "geoName"), null, null, false);