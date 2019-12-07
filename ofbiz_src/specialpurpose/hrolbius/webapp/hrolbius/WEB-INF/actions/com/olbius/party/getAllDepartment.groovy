import java.util.List;
import java.util.Properties;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.entity.GenericValue;

import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

Properties generalProp = UtilProperties.getProperties("general");
String defaultOrganizationPartyId = (String)generalProp.get("ORGANIZATION_PARTY");
Organization org = PartyUtil.buildOrg(delegator, defaultOrganizationPartyId, false, false);

List<GenericValue> listDepartment = org.getAllDepartmentList(delegator);
/*delegator.findByAnd("PartyGroupRoleType", UtilMisc.toMap("roleTypeId", "DEPARTMENT"), null, false);*/
context.directChildDepartment = org.getDirectChildList(delegator);
context.listDepartment = listDepartment;
context.org = org;