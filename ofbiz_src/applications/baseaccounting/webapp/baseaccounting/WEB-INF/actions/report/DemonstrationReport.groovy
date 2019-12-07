import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.base.util.UtilMisc;
import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import com.olbius.acc.report.AccountingReportUtil; 

// 0. Global data 
List listRoot = new ArrayList();
List listCond = new ArrayList();
String strPeriodType = parameters.periodtype;
if(!strPeriodType){
	strPeriodType = "FISCAL_YEAR";
}
// get 2 nearest closed CustomTimePeriod
listCond.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, parameters.organizationPartyId));
listCond.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, strPeriodType));
listCond.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "Y")); 
lastTimePeriodHistory = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, UtilMisc.toList("thruDate DESC"), null, false);
if(!lastTimePeriodHistory || lastTimePeriodHistory.size() < 2){
	return;
}
String strPeriod1 = lastTimePeriodHistory.get(0).get("customTimePeriodId");
String strPeriod2 = lastTimePeriodHistory.get(1).get("customTimePeriodId");
String strCurrentYear = lastTimePeriodHistory.get(0).get("periodName");
String strPreviousYear = lastTimePeriodHistory.get(1).get("periodName");
context.strCurrentYear = strCurrentYear;
context.strPreviousYear = strPreviousYear;
// FIXME check for missing timeperiod
// get data

// 1. get reportId
listCond = new ArrayList();
listCond.add(EntityCondition.makeCondition("reportTypeId", EntityOperator.EQUALS, "9003"));
listCond.add(EntityCondition.makeCondition("isDefault", EntityOperator.EQUALS, "Y"));
listBSReport = delegator.findList("AccReport", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
if(!listBSReport){
	return;
}
String strReportId = listBSReport.get(0).get("reportId");
// 2. get report target tree
	// 2.1 get root of tree
listCond = new ArrayList();
listCond.add(EntityCondition.makeCondition("reportId", EntityOperator.EQUALS, strReportId));
listCond.add(EntityCondition.makeCondition("parentTargetId", EntityOperator.EQUALS, null));
listBSReport = delegator.findList("AccReportTarget", EntityCondition.makeCondition(listCond, EntityOperator.AND), null, null, null, false);
	// 2.2 create tree model and fill data
listBSReport.each{ bsReport->
	Map tmpMap = new HashMap();
	tmpMap.put("targetId", bsReport.get("targetId"));
	tmpMap.put("name", bsReport.get("name"));
	tmpMap.put("code", bsReport.get("code"));
	tmpMap.put("demonstration", bsReport.get("demonstration"));
	tmpMap.put("displayStyle", bsReport.get("displayStyle"));
	tmpMap.put("orderIndex", bsReport.get("orderIndex"));
	tmpMap.put("formula", bsReport.get("formula"));
	tmpMap.put("unionSign", bsReport.get("unionSign"));
	tmpMap.put("displaySign", bsReport.get("displaySign"));
	tmpMap.put("children", AccountingReportUtil.getChildrenTree(bsReport.get("targetId"), strPeriod1, strPeriod2, delegator, parameters.organizationPartyId));
	listRoot.add(tmpMap);
}

// 3. summary data
listRoot.each{ root ->
	List child = root.get("children");
	child = AccountingReportUtil.sortByOrderIndex(child);
	BigDecimal tmpValue1 = new BigDecimal(0);
	BigDecimal tmpValue2 = new BigDecimal(0);
	if(child){
		child.each{ chd ->
			tmpValue1 = tmpValue1.add(AccountingReportUtil.calculateAndUpdate(chd, "value1"));
			tmpValue2 = tmpValue2.add(AccountingReportUtil.calculateAndUpdate(chd, "value2"));
		}
	}
	root.put("value1", tmpValue1);
	root.put("value2", tmpValue2);
}
// 4. create list data
List listReportData = new ArrayList();
listRoot.each{ root ->
	listReportData.add(root);
	if(root.get("children")){
		listReportData.addAll(AccountingReportUtil.addAllChild(root.get("children")));
	}
}
int iSize = listReportData.size();
for(int i = 0; i < iSize; i++){
	for(int j = i + 1; j < iSize;j++){
		String tmpStr1 = listReportData.get(i).get("orderIndex");
		String tmpStr2 = listReportData.get(j).get("orderIndex");
		if(!tmpStr1){
			tmpStr1 = "0";
		}
		if(!tmpStr2){
			tmpStr2 = "0";
		}
		int tmpInt1 = Integer.parseInt(tmpStr1);
		int tmpInt2 = Integer.parseInt(tmpStr2);
		
		if(tmpInt1 > tmpInt2){
			Collections.swap(listReportData, i, j);
		}
	}
}
context.listReportData = listReportData;

// convert to json format(display on jqx)
JSONArray jsonArray = new JSONArray();
listRoot.each{ root->
	JSONObject tmpJsonObject = JSONObject.fromObject(root);
	jsonArray.add(tmpJsonObject);
}
context.listRoot = listRoot;
context.testJson = jsonArray.toString();