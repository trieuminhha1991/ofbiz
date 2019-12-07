import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilMisc;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import com.olbius.acc.report.financialstm.CashFlowBuilder;
import com.olbius.acc.report.financialstm.PeriodUtils; 

builder = new CashFlowBuilder();
if(parameters.customTimePeriodId == null){
	parameters.customTimePeriodId = customTimePeriodDefault.customTimePeriodId;
	jsonArray = builder.convertToJsonArray(parameters, delegator);
	context.periodName = PeriodUtils.getPeriodName(parameters.customTimePeriodId, delegator);
	context.previousPeriodName = PeriodUtils.getPreviousPeriodName(parameters.customTimePeriodId, delegator);
	context.jsonData = jsonArray.toString();
}else{
	jsonArray = builder.convertToJsonArray(parameters, delegator);
	context.periodName = PeriodUtils.getPeriodName(parameters.customTimePeriodId, delegator);
	context.previousPeriodName = PeriodUtils.getPreviousPeriodName(parameters.customTimePeriodId, delegator);
	context.jsonData = jsonArray.toString();
}