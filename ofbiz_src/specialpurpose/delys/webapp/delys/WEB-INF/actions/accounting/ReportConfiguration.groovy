import javolution.util.FastList;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;

reportTypeId = request.getParameter("reportTypeId");


List<EntityCondition> reportTypeCons = FastList.newInstance();
GenericValue reportTypeSelected = null;
if(reportTypeId){
	reportTypeCons.add(EntityCondition.makeCondition("reportTypeId", EntityOperator.NOT_EQUAL, reportTypeId));
	reportTypeSelected = delegator.findOne("AccReportType", UtilMisc.toMap("reportTypeId",reportTypeId), false);
}
EntityCondition retporTypeCon = EntityCondition.makeCondition(reportTypeCons, EntityOperator.AND);
List<GenericValue> accReportTypes = delegator.findList("AccReportType", retporTypeCon, null, null, null,false);
if(UtilValidate.isNotEmpty(reportTypeSelected)){
	accReportTypes.add(0, reportTypeSelected);
}
if(UtilValidate.isNotEmpty(accReportTypes)){
	context.accReportTypes = accReportTypes;
	reportTypeIdSelected  = accReportTypes.get(0);
	context.reportTypeIdSelected = reportTypeIdSelected;
}
