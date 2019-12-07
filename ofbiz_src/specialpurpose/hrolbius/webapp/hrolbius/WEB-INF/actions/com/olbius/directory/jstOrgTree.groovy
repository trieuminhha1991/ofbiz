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

/*
 * This script is also referenced by the ecommerce's screens and
 * should not contain order component's specific code.
 */
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.*;
import org.ofbiz.product.catalog.*;
import org.ofbiz.product.category.*;

import javolution.util.FastMap;
import javolution.util.FastList;
import javolution.util.FastList.*;

import org.ofbiz.entity.*;

import com.olbius.util.PartyUtil;

import java.util.List;

// Put the result of CategoryWorker.getRelatedCategories into the separateRootType function as attribute.
// The separateRootType function will return the list of category of given catalog.
// PLEASE NOTE : The structure of the list of separateRootType function is according to the JSON_DATA plugin of the jsTree.

completedTree =  FastList.newInstance();
completedTreeContext =  FastList.newInstance();
existParties =  FastList.newInstance();
subtopLists =  FastList.newInstance();
partyIdFrom=userLogin.getString("partyId");
Properties generalProp = UtilProperties.getProperties("general");
String defaultOrganizationPartyId = (String)generalProp.get("ORGANIZATION_PARTY");
if(PartyUtil.isAdmin(partyIdFrom, delegator)){
	partyId=defaultOrganizationPartyId;	
}else{
	partyId=PartyUtil.getOrgByManager(partyIdFrom, delegator);
}

//FIXME use PartyUtil
//Employee in Org
List<EntityCondition> conditions = FastList.newInstance();
conditions.add(EntityUtil.getFilterByDateExpr());
conditions.add(EntityCondition.makeCondition("partyIdFrom", partyId));
conditions.add(EntityCondition.makeCondition("roleTypeIdFrom", "INTERNAL_ORGANIZATIO"));
conditions.add(EntityCondition.makeCondition("roleTypeIdTo", "EMPLOYEE"));
List<GenericValue> listEmpl = delegator.findList("Employment", EntityCondition.makeCondition(conditions, EntityOperator.AND), null, null, null, false);
partyRootMap = FastMap.newInstance();

for(GenericValue temp: listEmpl){
	person = delegator.findOne("Person", [partyId : temp.getString("partyIdTo")], false);
	if(person){
		partyPersonMap = FastMap.newInstance();
		partyPersonMap.put("partyId", person.getString("partyId"));
		//partyPersonMap.put("groupName", partyGroup.getString("groupName"));
		completedTreeContext.add(partyPersonMap);
	}
	
	//subtopLists.addAll(temp.getString("partyIdTo"));
}

//FIXME use PartyUtil
//internalOrg list
partyRelationships = EntityUtil.filterByDate(delegator.findByAnd("PartyRelationship", [partyIdFrom : partyId, partyRelationshipTypeId : "GROUP_ROLLUP"], null, false));

if (partyRelationships) {
    //root
    partyRoot = delegator.findOne("PartyGroup", [partyId : partyId], false);
    //partyRootMap = FastMap.newInstance();
    partyRootMap.put("partyId", partyId);
    partyRootMap.put("groupName", partyRoot.getString("groupName"));

	//child
	
    for(partyRelationship in partyRelationships) {		
        partyGroup = delegator.findOne("PartyGroup", [partyId : partyRelationship.getString("partyIdTo")], false);        
		if(partyGroup){
			partyGroupMap = FastMap.newInstance();
			partyGroupMap.put("partyId", partyGroup.getString("partyId"));
			partyGroupMap.put("groupName", partyGroup.getString("groupName"));
			completedTreeContext.add(partyGroupMap);
		}
        //subtopLists.addAll(partyRelationship.getString("partyIdTo"));
    }
    /*partyRootMap.put("child", completedTreeContext);
    completedTree.add(partyRootMap);*/
}else{	
	partyRoot = delegator.findOne("PartyGroup", [partyId : partyId], false);
	//partyRootMap = FastMap.newInstance();
	if(UtilValidate.isNotEmpty(partyRoot)){
		partyRootMap.put("partyId", partyId);
		partyRootMap.put("groupName", partyRoot.getString("groupName"));
	}
	//completedTree.add(partyRootMap);
}
if(UtilValidate.isNotEmpty(completedTreeContext)){
	partyRootMap.put("child", completedTreeContext);
}
completedTree.add(partyRootMap);
// The complete tree list for the category tree
context.homePartyId = partyId;
context.completedTree = completedTree;
//context.subtopLists = subtopLists;