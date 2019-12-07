import org.apache.commons.lang.StringUtils;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.util.EntityUtil;

List<GenericValue> partyInsuranceTypeParticipateStt = delegator.findByAnd("PartyParticipateInsuranceAndStatus", UtilMisc.toMap("partyId", partyId), UtilMisc.toList("insuranceTypeId"), false);
if(UtilValidate.isNotEmpty(partyInsuranceTypeParticipateStt)){
	List<GenericValue> insuranceTypeParticipate = EntityUtil.filterByCondition(partyInsuranceTypeParticipateStt, EntityCondition.makeCondition("statusId", "PARTICIPATING"));
	if(UtilValidate.isNotEmpty(insuranceTypeParticipate)){
		/*List<String> insuranceParticipateList = EntityUtil.getFieldListFromEntityList(insuranceTypeParticipate, "insuranceTypeId", true);
		context.insuranceTypePartictipate = StringUtils.join(insuranceParticipateList, ", ");*/
		context.statusId = "PARTICIPATING";
	}else{
		List<GenericValue> insuranceTypeSuspend = EntityUtil.filterByCondition(partyInsuranceTypeParticipateStt, EntityCondition.makeCondition("statusId", "SUSPEND_PARTICIPATE"));
		if(UtilValidate.isNotEmpty(insuranceTypeSuspend)){
			/*List<String> insuranceSuspendParticipateList = EntityUtil.getFieldListFromEntityList(insuranceTypeSuspend, "insuranceTypeId", true);
			context.insuranceTypeSuspendParticipate = StringUtils.join(insuranceSuspendParticipateList, ", ");*/
			context.statusId = "SUSPEND_PARTICIPATE";
		}else{
			List<GenericValue> insuranceTypeStop = EntityUtil.filterByCondition(partyInsuranceTypeParticipateStt, EntityCondition.makeCondition("statusId", "STOP_PARTICIPATE"));
			if(UtilValidate.isNotEmpty(insuranceTypeStop)){
				/*List<String> insuranceStopParticipateList = EntityUtil.getFieldListFromEntityList(insuranceTypeStop, "insuranceTypeId", true);
				context.insuranceTypeStopParticipate = StringUtils.join(insuranceStopParticipateList, ", ");*/
			context.statusId = "STOP_PARTICIPATE";
			}
		}
	}
}