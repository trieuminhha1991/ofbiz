import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import com.olbius.basehr.util.MultiOrganizationUtil;

String userLoginId = userLogin.userLoginId;
String orgId = MultiOrganizationUtil.getCurrentOrganization(delegator, userLoginId);

List<GenericValue> listPeriod = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(UtilMisc.toMap("periodTypeId", "COMMERCIAL_YEAR", "organizationPartyId", orgId)), null, null, null, false);

context.listPeriod = listPeriod;