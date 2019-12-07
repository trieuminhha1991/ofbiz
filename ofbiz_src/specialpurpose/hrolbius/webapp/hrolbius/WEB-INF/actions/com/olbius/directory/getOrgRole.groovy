import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;

partyId = parameters.orgId;
List<GenericValue> roleList = delegator.findByAnd("RoleTypeAndParty", UtilMisc.toMap("partyId", partyId, "parentTypeId", "ORGANIZATION_UNIT"), null, false);

context.roleList = roleList;