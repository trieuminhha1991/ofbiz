import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator;
import org.ofbiz.base.util.UtilMisc;


listFunction = new ArrayList<String>();
listFunctionName = new ArrayList<String>();
for( String str:parameters.formulaList){
	GenericValue generic = delegator.findOne("PayrollFormula",[code:str],false);
	listFunction.add(generic.get("function"));
	listFunctionName.add(generic.get("name"));
}
context.listFunction = listFunction;
context.listFunctionName = listFunctionName;

// Find party's payroll
pdfPartyId = parameters.pdfPartyId;
context.userPayroll = delegator.findOne("Person",[partyId:pdfPartyId],false); 

// Find parameters list for specific user
paymentCond = [];
conditionsAnd = [];
orConditionList = [];
mainCondition = [];
mainCondition2 = [];
mainCondition3 = [];
mainConditionOR = [];
fromDate = null;
thruDate = null;
try{
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Date parsedDate = dateFormat.parse(parameters.fromDate.toString());
    fromDate = new Timestamp(parsedDate.getTime());
    parsedDate = dateFormat.parse(parameters.thruDate.toString());
    thruDate = new Timestamp(parsedDate.getTime());
}catch(Exception e){
	org.ofbiz.base.util.Debug.logError(e,"Payroll pdf groovy");
}
paymentCond.add(EntityCondition.makeCondition("date", EntityOperator.GREATER_THAN_EQUAL_TO, fromDate));
paymentCond.add(EntityCondition.makeCondition("date", EntityOperator.LESS_THAN_EQUAL_TO, thruDate));
paymentCond.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, pdfPartyId));
conditionsAnd = EntityCondition.makeCondition(paymentCond, EntityOperator.AND);

mainCondition.add(EntityCondition.makeCondition("date", EntityOperator.EQUALS, null));
mainCondition.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, pdfPartyId));
mainCondition2 = EntityCondition.makeCondition(mainCondition, EntityOperator.AND);

mainCondition3.add(mainCondition2);
mainCondition3.add(conditionsAnd); 

mainConditionOR = EntityCondition.makeCondition(mainCondition3, EntityOperator.OR);

parametersList = delegator.findList("PayrollEmplParameters", mainConditionOR, null, null, null, false);
parametersCompanyList = new ArrayList<String>();
parametersTotalList = new ArrayList<String>();

for(int i = 0; i < parametersList.size();i++){
	org.ofbiz.base.util.Debug.logInfo("OLBIUS1" + parametersList.get(i).get("value"),"");
	String strActualValue = com.olbius.payroll.PayrollEngine.getActualParameterValue(delegator, parametersList.get(i).get("value"), pdfPartyId, fromDate, thruDate);
	org.ofbiz.base.util.Debug.logInfo("OLBIUS: ","");
	String strActualPercent = parametersList.get(i).get("actualPercent");
	
	parametersTotalList.add(strActualValue);
	if(strActualPercent != null && !strActualPercent.isEmpty()){
		parametersList.get(i).set("value",com.olbius.payroll.PayrollUtil.evaluateStringExpression(strActualPercent));
		//if(parametersList.get(i).get("type").equals("CONSTPERCENT")){
		//	parametersCompanyList.add("0");
		//}else{
			parametersCompanyList.add(com.olbius.payroll.PayrollUtil.evaluateStringExpression(strActualValue + "-" + strActualPercent));
		//}
	}else{
		parametersList.get(i).set("value",strActualValue);
		parametersCompanyList.add("0");
	}
} 
context.parametersList = parametersList;
context.parametersTotalList = parametersTotalList;
context.parametersCompanyList = parametersCompanyList;
listParameterName = [];
for(int i = 0; i < context.parametersList.size();i++){
	tmpCode = context.parametersList.get(i).code;
	GenericValue generic = delegator.findOne("PayrollParameters",[code:tmpCode],false);
	listParameterName.add(generic.get("name"));
}
context.listParameterName = listParameterName;

// get global parameters
EntityExpr entityExpr1 = EntityCondition.makeCondition("defaultValue",EntityJoinOperator.NOT_EQUAL, "0"); // defaultValue != 0
listGlobalParameter = delegator.findList("PayrollParameters", entityExpr1, UtilMisc.toSet("code","name","type","defaultValue","actualValue"), null, null, false);
context.listGlobalParameter = listGlobalParameter;
listCompnayValue = new ArrayList<String>();
for(int i = 0; i < listGlobalParameter.size();i++){
	listCompnayValue.add(com.olbius.payroll.PayrollUtil.evaluateStringExpression(listGlobalParameter.get(i).get("defaultValue") + "-" + listGlobalParameter.get(i).get("actualValue")));
}
context.listCompnayValue = listCompnayValue;