import java.sql.Timestamp;

import javolution.util.FastList;
import javolution.util.FastMap;

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;

import com.olbius.payroll.entity.EntityEmplParameters;
import com.olbius.payroll.entity.EntityParameter;

GenericValue payrollFormula = delegator.findOne("PayrollFormula", UtilMisc.toMap("code", "LUONG"), false);
String function = payrollFormula.getString("function");
String desc = payrollFormula.getString("name");
String[] functionArr = function.split("[\\+\\-\\*\\/\\%]");
List<String> codeList = FastList.newInstance(); 
for(String tempFunc: functionArr){
	tempFunc = tempFunc.trim();
	if(tempFunc.contains("()")){
		tempFunc = tempFunc.replace("(","");
		tempFunc = tempFunc.replace(")","");	
		if(!codeList.contains(tempFunc)){
			codeList.add(tempFunc);
		}	
	}
}
List<GenericValue> listPayroll = delegator.findByAnd("PayrollTable", UtilMisc.toMap("code", "LUONG"), null, false);
List<EntityEmplParameters> entityEmplParametersList = FastList.newInstance();
for(GenericValue payroll: listPayroll){
	EntityEmplParameters entityEmplParameter = new EntityEmplParameters();
	entityEmplParametersList.add(entityEmplParameter);
	List<EntityParameter> entityParamters = FastList.newInstance();
	entityEmplParameter.setEmplParameters(entityParamters);
	Timestamp fromDate = payroll.getTimestamp("fromDate");
	Timestamp thruDate = payroll.getTimestamp("thruDate");
	String statusId = payroll.getString("statusId");
	String partyId = payroll.getString("partyId");
	entityEmplParameter.setPartyId(partyId);
	
	EntityParameter entityParameter = new EntityParameter();
	entityParameter.setCode(payroll.getString("code"));
	entityParameter.setFromDate(fromDate);
	entityParameter.setThruDate(thruDate);
	entityParameter.setValue(payroll.getString("value"));
	entityParamters.add(entityParameter);
	
	
	for(String tempCode: codeList){
		GenericValue tempPayroll = delegator.findOne("PayrollTable", UtilMisc.toMap("partyId", partyId, "code", tempCode, "fromDate", fromDate, "thruDate", thruDate), false);
		if(UtilValidate.isNotEmpty(tempPayroll)){
			EntityParameter entityParam = new EntityParameter();
			entityParam.setCode(tempPayroll.getString("code"));
			entityParam.setFromDate(tempPayroll.getTimestamp("fromDate"));
			entityParam.setThruDate(tempPayroll.getTimestamp("thruDate"));
			entityParam.setValue(tempPayroll.getString("value"));
			entityParamters.add(entityParam);
		}
	}
}
context.entityEmplParametersList = entityEmplParametersList;
List<String> allCodeList = codeList; 
allCodeList.add(0, "LUONG");
context.allCodeList = allCodeList;
