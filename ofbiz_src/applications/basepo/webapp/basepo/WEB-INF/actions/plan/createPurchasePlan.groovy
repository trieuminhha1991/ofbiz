import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.base.util.UtilMisc;
import com.olbius.basehr.util.MultiOrganizationUtil;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilValidate;

GenericValue userLogin = (GenericValue)context.get("userLogin");
String companyStr = MultiOrganizationUtil.getCurrentOrganization(delegator, userLogin.getString("userLoginId"));

//get supplier party
context.listFacility = delegator.findList("Facility", EntityCondition.makeCondition(UtilMisc.toMap("facilityTypeId", "WAREHOUSE", 
	"ownerPartyId", companyStr)), null, null, null, true);