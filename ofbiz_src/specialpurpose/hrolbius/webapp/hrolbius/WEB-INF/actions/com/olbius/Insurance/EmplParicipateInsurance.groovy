import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

reportId = parameters.parm0;
//println ("reportId:" + reportId);
partyIdFrom = parameters.partyIdFrom;
List<GenericValue> emplList = null;
EntityFindOptions options = new EntityFindOptions();
options.setDistinct(true);
if(partyIdFrom){
	Organization org = PartyUtil.buildOrg(delegator, partyIdFrom);
	emplList = org.getEmployeeInOrg(delegator);
}else{
	 emplList =  PartyUtil.getEmployeeInOrg(delegator);
}
if(reportId){
	List<GenericValue> emplInReport = delegator.findByAnd("PartyInsuranceReport", UtilMisc.toMap("reportId", reportId), null, false);
	List<String> partyIds = EntityUtil.getFieldListFromEntityList(emplInReport, "partyId", true);
	emplList = EntityUtil.filterByCondition(emplList, EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, partyIds));
}

List<GenericValue> partyParticipateInsurance = delegator.findList("PartyParticipateInsuranceAndStatus", EntityCondition.makeCondition("statusId", "PARTICIPATING"), UtilMisc.toSet("partyId", "statusDatetimeLast"), UtilMisc.toList("statusDatetimeLast"), options, false);
List<String> tempPartyId = EntityUtil.getFieldListFromEntityList(partyParticipateInsurance, "partyId", true);
emplList = EntityUtil.filterByCondition(emplList, EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, tempPartyId));
context.listIt = emplList;