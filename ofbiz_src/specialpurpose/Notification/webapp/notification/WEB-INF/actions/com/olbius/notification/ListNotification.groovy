import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityJoinOperator
import org.ofbiz.entity.condition.EntityOperator
import org.ofbiz.entity.util.EntityFindOptions;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilProperties;

import com.olbius.common.util.EntityMiscUtil;

ntfLimitNumber = 0;

enableNotification = UtilProperties.getPropertyValue("general.properties", "notification.enable");
if(enableNotification == "N"){
	context.listNofification = [];
	ntfLimitNumber = 20;
	return;
}else{
	if(UtilProperties.getPropertyValue("general.properties", "notification.limit")){
		ntfLimitNumber = UtilProperties.getPropertyValue("general.properties", "notification.limit").toInteger();
	}
}
context.ntfLimitNumber = ntfLimitNumber;

// get list by notification group
partyId = userLogin.get("partyId");
EntityExpr entityExpr1 = EntityCondition.makeCondition("partyId",EntityJoinOperator.EQUALS, partyId);
EntityExpr entityExpr2 = EntityCondition.makeCondition("state",EntityJoinOperator.EQUALS, "open");
EntityExpr entityEmptyNtfGroup1 = EntityCondition.makeCondition("ntfGroupId",EntityJoinOperator.EQUALS, null);
EntityExpr entityEmptyNtfGroup2 = EntityCondition.makeCondition("ntfGroupId",EntityJoinOperator.EQUALS, "");
EntityExpr entityExpr3 = EntityCondition.makeCondition("openTime",EntityJoinOperator.EQUALS, null);
EntityExpr entityExpr4 = EntityCondition.makeCondition("openTime",EntityJoinOperator.LESS_THAN_EQUAL_TO, UtilDateTime.nowTimestamp());
EntityCondition groupCond = EntityCondition.makeCondition(UtilMisc.toList(entityEmptyNtfGroup1,entityEmptyNtfGroup2),EntityJoinOperator.OR);
EntityCondition groupCond2 = EntityCondition.makeCondition(UtilMisc.toList(entityExpr1,entityExpr2),EntityJoinOperator.AND);
EntityCondition groupCond3 = EntityCondition.makeCondition(UtilMisc.toList(entityExpr3,entityExpr4),EntityJoinOperator.OR);

EntityFindOptions opts = new EntityFindOptions(true, EntityFindOptions.TYPE_SCROLL_INSENSITIVE, EntityFindOptions.CONCUR_READ_ONLY, false);
opts.setLimit(ntfLimitNumber + 1);
opts.setOffset(0);
context.listNofification = delegator.findList("Notification", 
												EntityCondition.makeCondition(UtilMisc.toList(groupCond,groupCond2,groupCond3),EntityJoinOperator.AND), 
												UtilMisc.toSet("ntfId","targetLink","dateTime","header","action","ntfType"), 
												UtilMisc.toList("-dateTime"), opts, false);
/*context.listNofification = delegator.findList("Notification", 
												EntityCondition.makeCondition(UtilMisc.toList(groupCond,groupCond2,groupCond3),EntityJoinOperator.AND), 
												UtilMisc.toSet("ntfId","targetLink","dateTime","header","action","ntfType"), 
												UtilMisc.toList("-dateTime"), null, false);
*/

context.parameters = parameters;

// get list by empty notification group
Set<String> params = new HashSet<String>();
params.add("ntfId");
params.add("targetLink");
params.add("dateTime");
params.add("header");
params.add("action");
params.add("actionGroup");
params.add("partyId");
params.add("ntfGroupId");
params.add("description");
params.add("imagePath");
listNofification2 = delegator.findList("NtfNtfGroup", 
												EntityCondition.makeCondition(UtilMisc.toList(groupCond2,groupCond3),EntityJoinOperator.AND), 
												params, 
												UtilMisc.toList("ntfGroupId"), opts, false);
if(listNofification2.size() > 0){
	tmpNG = listNofification2.get(0).get("ntfGroupId");
	tmpList = new ArrayList();
	tmpList.add(listNofification2.get(0));
	tmpMap = new HashMap<String, List>();
	listNG = new ArrayList();
	listKey = new ArrayList();
	if(listNofification2.size() == 1){
		tmpMap.put(tmpNG, tmpList);
		listNG.add(tmpMap);
		listKey.add(tmpNG);
	}else{	
		for(int i = 1; i < listNofification2.size();i++){
			if(listNofification2.get(i).get("ntfGroupId") == tmpNG){
				tmpList.add(listNofification2.get(i));
			}else{
				tmpMap.put(tmpNG, tmpList);
				listNG.add(tmpMap);
				listKey.add(tmpNG);
				tmpNG = listNofification2.get(i).get("ntfGroupId");
				tmpList = new ArrayList();
			}
		}		
		tmpMap.put(tmpNG, tmpList);
		listNG.add(tmpMap);
		listKey.add(tmpNG);
	}
	context.listNG = listNG;							
	context.listKey = listKey;							
}