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

import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.accounting.payment.*;
import org.ofbiz.accounting.util.UtilAccounting;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.service.ServiceUtil;
import com.olbius.util.MultiOrganizationUtil;
import org.ofbiz.base.util.UtilDateTime;
import java.math.*;


orderId = parameters.orderId;
organizationPartyId =  MultiOrganizationUtil.getCurrentOrganization(delegator);
result = ServiceUtil.returnSuccess();

orderIdCond = EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, orderId);
roleTypeIdCond = EntityCondition.makeCondition("roleTypeId", EntityOperator.EQUALS, "BILL_TO_CUSTOMER");
topCond = EntityCondition.makeCondition([orderIdCond, roleTypeIdCond], EntityOperator.AND);

orderHeaderDetailList = delegator.findList("OrderHeaderDetail", topCond, null, null, null,  false);

orderHeaderDetail = EntityUtil.getFirst(orderHeaderDetailList);

partyIdFromCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, orderHeaderDetail.partyId);
            
partyIdCond = EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId);

exprList = [EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
             EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())];            
thruDateCond = EntityCondition.makeCondition(exprList, EntityOperator.OR);
topConds = EntityCondition.makeCondition([partyIdFromCond, partyIdCond, thruDateCond], EntityOperator.AND);
println "Viettb Topcon" + topConds;
partyPaymentMethodList = delegator.findList("PartyPaymentMethod", topConds, null, null, null, false);
println "Viettb partyPaymentMethodList" + partyPaymentMethodList;
List paymentMethodCond = [];
if (partyPaymentMethodList) {
            paymentMethodIds = EntityUtil.getFieldListFromEntityList(partyPaymentMethodList, "paymentMethodId", true);
            paymentMethodCond.add(EntityCondition.makeCondition("paymentMethodId", EntityOperator.IN, paymentMethodIds));
            paymentMethodCond.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, orderHeaderDetail.partyId));
            paymentMethodCond.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));                 
        }
        else {
				exprListSup = [EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
				             EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())];            
				thruDateSupCond = EntityCondition.makeCondition(exprList, EntityOperator.OR);
				partyIdFromSupCond = EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, orderHeaderDetail.partyId);
				partyRelationshipTypeIdSupCond = EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP");
				topCondsSup = EntityCondition.makeCondition([thruDateSupCond, partyIdFromSupCond, partyRelationshipTypeIdSupCond], EntityOperator.AND);				
				partyRelationshipSup = EntityUtil.getFirst(delegator.findList("PartyRelationship", topCondsSup, null, null, null,  false));        	
				println "Viettbsup" + partyRelationshipSup;

				exprListAsm = [EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
				             EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())];            
				thruDateAsmCond = EntityCondition.makeCondition(exprList, EntityOperator.OR);
				partyIdFromAsmCond = EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyRelationshipSup.partyIdFrom);
				partyRelationshipTypeIdAsmCond = EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP");
				topCondsAsm = EntityCondition.makeCondition([thruDateAsmCond, partyIdFromAsmCond, partyRelationshipTypeIdAsmCond], EntityOperator.AND);				
				partyRelationshipAsm = EntityUtil.getFirst(delegator.findList("PartyRelationship", topCondsAsm, null, null, null,  false));        	
				println "Viettb asm" + partyRelationshipAsm;

				exprListRsm = [EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
				             EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())];            
				thruDateRsmCond = EntityCondition.makeCondition(exprList, EntityOperator.OR);
				partyIdFromRsmCond = EntityCondition.makeCondition("partyIdTo", EntityOperator.EQUALS, partyRelationshipAsm.partyIdFrom);
				partyRelationshipTypeIdRsmCond = EntityCondition.makeCondition("partyRelationshipTypeId", EntityOperator.EQUALS, "GROUP_ROLLUP");
				topCondsRsm = EntityCondition.makeCondition([thruDateRsmCond, partyIdFromRsmCond, partyRelationshipTypeIdRsmCond], EntityOperator.AND);				
				partyRelationshipRsm = EntityUtil.getFirst(delegator.findList("PartyRelationship", topCondsRsm, null, null, null,  false));        	
				println "Viettb Rsm" + partyRelationshipRsm;
        	
        		exprListPar = [EntityCondition.makeCondition("thruDate", EntityOperator.EQUALS, null),
             	EntityCondition.makeCondition("thruDate", EntityOperator.GREATER_THAN, UtilDateTime.nowTimestamp())];            
				thruDateParCond = EntityCondition.makeCondition(exprList, EntityOperator.OR);
				partyIdFromParCond = EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, partyRelationshipRsm.partyIdFrom);
				topCondsPar = EntityCondition.makeCondition([partyIdFromParCond, partyIdCond, thruDateParCond], EntityOperator.AND);
				partyPaymentMethodListPar = delegator.findList("PartyPaymentMethod", topCondsPar, null, null, null, false);
				println "Viettb Par" + partyPaymentMethodListPar;
				
				if (partyPaymentMethodListPar) {
					            paymentMethodIds = EntityUtil.getFieldListFromEntityList(partyPaymentMethodListPar, "paymentMethodId", true);
            					paymentMethodCond.add(EntityCondition.makeCondition("paymentMethodId", EntityOperator.IN, paymentMethodIds));
					            paymentMethodCond.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS,  partyRelationshipRsm.partyIdFrom));
					            paymentMethodCond.add(EntityCondition.makeCondition("organizationPartyId", EntityOperator.EQUALS, organizationPartyId));              					                  					
				}
				else {
					paymentMethodCond.add(EntityCondition.makeCondition("paymentMethodId", EntityOperator.EQUALS, null));
				}
        }          
            
paymentMethodIdCond =  EntityCondition.makeCondition(paymentMethodCond, EntityOperator.AND); 
println "Viettb paymentMethodIdCond" +  paymentMethodIdCond;
paymentMethods = delegator.findList("PaymentMethodAndPartyPaymentMethod", paymentMethodIdCond, null, null, null, false); 
                 
context.paymentMethods = paymentMethods;
result.paymentMethods = paymentMethods;
return result;