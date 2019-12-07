import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;


if(partyId){
	List<GenericValue> workingLateReason = delegator.findByAnd("EmplWorkingLate", UtilMisc.toMap("partyId", partyId, "reasonFlag", "Y"), null, false);
	List<GenericValue> workingLateNoReason = delegator.findByAnd("EmplWorkingLate", UtilMisc.toMap("partyId", partyId, "reasonFlag", "N"), null, false);
	if(UtilValidate.isNotEmpty(workingLateReason)){
		/*context.workingLateTimesReason = EntityUtil.getFirst(workingLateReason).getString("emplWorkingLateTimes");*/
		context.workingLateTimesReason = workingLateReason.size();
	}
	if(UtilValidate.isNotEmpty(workingLateNoReason)){
		/*context.workingLateTimesNoReason = EntityUtil.getFirst(workingLateNoReason).getString("emplWorkingLateTimes");*/
		context.workingLateTimesNoReason = workingLateNoReason.size();
	}
}