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
import java.math.*;

parentTypeId = parameters.parentTypeId;
partyIdFrom = parameters.partyIdFrom;
roleTypeId = parameters.roleTypeId;

roleTypeIdList = delegator.findByAnd("PartyRole", [roleTypeId : roleTypeId], null,  true);

decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");

partyIdFromCond = EntityCondition.makeCondition("partyIdFrom", EntityOperator.EQUALS, partyIdFrom);
parentTypeCond = EntityCondition.makeCondition("parentTypeId", EntityOperator.EQUALS, parentTypeId);

exprList = [EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INVOICE_IN_PROCESS"),
             EntityCondition.makeCondition("statusId", EntityOperator.EQUALS, "INVOICE_READY")];
statusCond = EntityCondition.makeCondition(exprList, EntityOperator.OR);

List partyCond = [];

if (roleTypeIdList) {
            partyIds = EntityUtil.getFieldListFromEntityList(roleTypeIdList, "partyId", true);
            partyCond.add(EntityCondition.makeCondition("partyId", EntityOperator.IN, partyIds));                  
        }
		else {
		 	partyCond.add(EntityCondition.makeCondition("partyId", EntityOperator.EQUALS, null));
		 }        
partyIdCond =  EntityCondition.makeCondition(partyCond, EntityOperator.AND);              

topCond = EntityCondition.makeCondition([parentTypeCond, partyIdFromCond, partyIdCond, statusCond], EntityOperator.AND);
invoices = delegator.findList("InvoiceAndType", topCond, null, null, null, false);
totalToApply = 0.0;
totalValue = 0.0;
if (invoices) {       
    invoices.each { invoice ->
        invoiceId = invoice.getString("invoiceId");
        BigDecimal tmpBD = InvoiceWorker.getInvoiceCurrencyConversionRate(delegator,invoiceId);
        applied = InvoiceWorker.getInvoiceApplied(delegator,invoiceId)*tmpBD;
        value = InvoiceWorker.getInvoiceTotal(delegator,invoiceId)*tmpBD;
        toApply = (value.subtract(applied))*tmpBD;
        totalToApply += toApply.setScale(decimals,rounding);
        totalValue += value.setScale(decimals,rounding);
    }
}
context.totalToApply = totalToApply.setScale(decimals,rounding);
context.totalValue = totalValue.setScale(decimals,rounding);