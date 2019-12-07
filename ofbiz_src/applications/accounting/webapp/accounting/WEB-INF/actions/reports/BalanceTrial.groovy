/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import net.sf.json.JSONObject;

import org.ofbiz.base.util.UtilDateTime;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilProperties;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.party.party.PartyWorker;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import net.sf.json.JSON;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import javolution.util.FastList;
import javolution.util.FastMap;


List mainAndExprs = FastList.newInstance();
String strFType = parameters.periodtype;
if(strFType == null){
	strFType = "FISCAL_QUARTER";
}
mainAndExprs.add(EntityCondition.makeCondition("periodTypeId", EntityOperator.EQUALS, strFType));
mainAndExprs.add(EntityCondition.makeCondition("isClosed", EntityOperator.EQUALS, "Y"));
listCTPtmp = delegator.findList("CustomTimePeriod", EntityCondition.makeCondition(mainAndExprs, EntityOperator.AND), null, ["thruDate DESC"], null, false);
listCTP = FastList.newInstance();
if(UtilValidate.isNotEmpty(listCTPtmp)){

	for (int i =0 ; i < 2 ; i ++){
		if(i < listCTPtmp.size()){
			GenericValue tmp = listCTPtmp.get(i);
			if(UtilValidate.isNotEmpty(tmp)){
				listCTP.add(tmp);
			}else{
				break;
			}
		}else{
			break;
		}
		
	}
}
context.listCTP = listCTP;
listHeader = FastList.newInstance();
listKey= FastList.newInstance();
listCTP.each{ tmpelem ->
	listHeader.add(tmpelem.get("periodName"));
	listKey.add("F" + tmpelem.get("customTimePeriodId"));
}
context.listHeader = listHeader;
context.listKey = listKey;


EntityCondition parentCond = EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, null);
	
List listTMP = delegator.findList("GlAccount", parentCond, null, UtilMisc.toList("glAccountId"), null, false);
List listTree = FastList.newInstance();

if(UtilValidate.isNotEmpty(listTMP)){
	for (tmp in listTMP) {
		Map tmpMap = new HashMap();
		String glAccountId = (String) tmp.getString("glAccountId");
		tmpMap.put("parentGlAccountId", "0");
		tmpMap.put("glAccountId", tmp.get("glAccountId"));
		tmpMap.put("accountName", tmp.get("accountName"));
		
		Map<String, String> condGL = FastMap.newInstance();
		if(UtilValidate.isNotEmpty(listCTP)){
			List balanceTmp = FastList.newInstance();
			for (tmpTCP in listCTP) {
				
				String balance = (String) "F"+ tmpTCP.getString("customTimePeriodId");
				balanceTmp.add(balance);
				tmpMap.put("balance",balanceTmp);			
				
			}
			
		}
		List tmpList = getChildrenTree(tmp.get("glAccountId"), organizationPartyId);
		if(tmpList!=null && tmpList.size() > 0){
			
			tmpMap.put("children", tmpList);
		}
		listTree.add(tmpMap);
		
	}
	
}

listTree.each{ listGlClass->
	List balanceTmps = listGlClass.get("balance");
	List tmpList = listGlClass.get("children");
	for (balance in balanceTmps) {

		String balanceTmp = balance.replace("F","");
		Map tmpSum = sumBalance(listGlClass, balanceTmp);
		/*if(UtilValidate.isEmpty(tmpSum)){
			tmpSum = new HashMap();
			tmpSum.put("postedDebits",0 );
			tmpSum.put("postedCredits",0 );
			tmpSum.put("endingBalance",0 );
		}*/
		listGlClass.put(balance,tmpSum);
	}
}
// convert to json format(display on jqx)
JSONArray jsonArray = new JSONArray();
listTree.each{ listGlClass->
	JSONObject tmpJsonObject = JSONObject.fromObject(listGlClass);
	jsonArray.add(tmpJsonObject);
}
context.listTree = listTree;
context.testJson = jsonArray.toString();

// get all glAccount child of a glAccount
List getChildrenTree(String parentGlAccountId, String organizationPartyId){
	List listTMP = delegator.findList("GlAccount",EntityCondition.makeCondition("parentGlAccountId", EntityOperator.EQUALS, parentGlAccountId), UtilMisc.toSet("glAccountId", "accountName", "accountCode"), UtilMisc.toList("glAccountId"), null, false);
	if(listTMP.empty){
		return null;
	}else{
		List listGlAccountChild = FastList.newInstance();
		for (tmp in listTMP) {
			Map tmpMap = new HashMap();
			tmpMap.put("parentGlAccountId", "0");
			tmpMap.put("glAccountId", tmp.get("glAccountId"));
			tmpMap.put("accountName", tmp.get("accountName"));
			String glAccountId = (String) tmp.getString("glAccountId");
			List tmpList = getChildrenTree(glAccountId, organizationPartyId);
			if(tmpList!=null && tmpList.size() > 0){
				tmpMap.put("children", tmpList);
			}
			
			if(UtilValidate.isNotEmpty(listCTP)){
				for (tmpTCP in listCTP) {	
					String balance = (String) "F"+ tmpTCP.getString("customTimePeriodId");
					tmpMap.put(balance,sumBalance(tmpMap,tmpTCP.getString("customTimePeriodId")));
					/*Map<String, String> condGL = FastMap.newInstance();
					condGL.put("glAccountId", glAccountId);
					condGL.put("organizationPartyId", organizationPartyId);
					condGL.put("customTimePeriodId", tmpTCP.getString("customTimePeriodId"));
					GenericValue glAccountHistory = delegator.findOne("GlAccountHistory", condGL, false);
					if(UtilValidate.isNotEmpty(glAccountHistory)){
				
						tmpMap.put(balance,glAccountHistory);
					}else{
						Map tmpEmpty = new HashMap();
						tmpEmpty.put("postedDebits",0);
						tmpEmpty.put("postedCredits", 0);
						tmpEmpty.put("endingBalance", 0);
						tmpMap.put(balance,tmpEmpty);
							
					}*/
				
					
				}
			
			}
		
			listGlAccountChild.add(tmpMap);
		
		}
		return listGlAccountChild;
	}
	
}
Map sumBalance(Map data, String customTimePeriodId){
	Map tmpMap = new HashMap();
	BigDecimal debited = BigDecimal.ZERO;
	BigDecimal credited = BigDecimal.ZERO;
	BigDecimal balanced = BigDecimal.ZERO;
	String glAccountId = (String) data.get("glAccountId");
	Map<String, String> condGL = FastMap.newInstance();
	condGL.put("glAccountId", glAccountId);
	condGL.put("organizationPartyId", organizationPartyId);
	condGL.put("customTimePeriodId", customTimePeriodId);
	GenericValue glAccountHistory = delegator.findOne("GlAccountHistory", condGL, false);
	
	listData = data.get("children");
	
	if(UtilValidate.isNotEmpty(listData)){
		listData.each{ tmpList ->

				Map bd = sumBalance(tmpList,customTimePeriodId );
				BigDecimal tmpDebited = bd.get("postedDebits");
				BigDecimal tmpCredited = bd.get("postedCredits");
				BigDecimal tmpBalanaced = bd.get("endingBalance");
				debited = debited.add(tmpDebited);
				credited = credited.add(tmpCredited);
				balanced = balanced.add(tmpBalanaced);
	
		}
	}
	if(UtilValidate.isNotEmpty(glAccountHistory)){
		
		BigDecimal tmpDebited = glAccountHistory.get("postedDebits");

		BigDecimal tmpCredited = glAccountHistory.get("postedCredits");
		BigDecimal tmpBalanaced = glAccountHistory.get("endingBalance");
		if(UtilValidate.isNotEmpty(tmpDebited)){
			debited = debited.add(tmpDebited);
		}
		if(UtilValidate.isNotEmpty(tmpCredited)){
				credited = credited.add(tmpCredited);
		}
		if(UtilValidate.isNotEmpty(tmpBalanaced)){
			balanced = balanced.add(tmpBalanaced);
		}

	
		
	}
	tmpMap.put("postedDebits", debited);
	tmpMap.put("postedCredits", credited);
	tmpMap.put("endingBalance", balanced);
	return tmpMap;

}