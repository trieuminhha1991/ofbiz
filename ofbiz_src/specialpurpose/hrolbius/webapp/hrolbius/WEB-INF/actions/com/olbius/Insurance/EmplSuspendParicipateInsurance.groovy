import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.entity.util.EntityUtil;

import com.olbius.util.Organization;
import com.olbius.util.PartyUtil;

reportId = parameters.parm0;
partyIdFrom = parameters.partyIdFrom;
List<GenericValue> partyParticipateInsurance = null;
List<EntityCondition> conditions = FastList.newInstance();
conditions.add(EntityCondition.makeCondition("statusId", "PARTICIPATING"));
EntityFindOptions options = new EntityFindOptions();
options.setDistinct(true);
if(partyIdFrom){
	Organization org = PartyUtil.buildOrg(delegator, partyIdFrom);
	List<GenericValue> emplList = org.getEmployeeInOrg(delegator);
	List<String> emplListStr = EntityUtil.getFieldListFromEntityList(emplList, "partyId", true);
	conditions.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, emplListStr));	
}
partyParticipateInsurance = delegator.findList("PartyParticipateInsuranceAndStatus", EntityCondition.makeCondition(conditions, EntityOperator.AND), UtilMisc.toSet("partyId", "statusDatetimeLast"), UtilMisc.toList("statusDatetimeLast"), options, false);
//println ("reportId:" + reportId);
//List<GenericValue> emplList =  PartyUtil.getEmployeeInOrg(delegator);
//get list employee participating insurance, to suspend or stop participating

/*List<String> tempPartyId = EntityUtil.getFieldListFromEntityList(partyParticipateInsurance, "partyId", true);
emplList = EntityUtil.filterByCondition(emplList, EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, tempPartyId));*/
if(reportId){
	List<GenericValue> emplInReport = delegator.findByAnd("PartyInsuranceReport", UtilMisc.toMap("reportId", reportId), null, false);
	List<String> partyIds = EntityUtil.getFieldListFromEntityList(emplInReport, "partyId", true);
	//filter employee have appearance in report
	partyParticipateInsurance = EntityUtil.filterByCondition(partyParticipateInsurance, EntityCondition.makeCondition("partyId", EntityOperator.NOT_IN, partyIds));
}

context.listIt = partyParticipateInsurance;