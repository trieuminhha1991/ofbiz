import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import org.apache.commons.lang.StringUtils;

List<GenericValue> insuranceTypeList = delegator.findByAnd("PartyInsuranceReport", UtilMisc.toMap("partyId", partyId, "reportId", reportId, "insuranceParticipateTypeId", insuranceParticipateTypeId), UtilMisc.toList("insuranceTypeId"), false);
context.insuranceTypes = "";
if(UtilValidate.isNotEmpty(insuranceTypeList)){		
	List<String> insuranceTypes = EntityUtil.getFieldListFromEntityList(insuranceTypeList, "insuranceTypeId", false);
	context.insuranceTypes = StringUtils.join(insuranceTypes, ", ");
}