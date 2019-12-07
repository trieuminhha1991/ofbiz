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

import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.base.util.UtilValidate;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityConditionBuilder;
import org.ofbiz.entity.condition.EntityConditionList;
import org.ofbiz.entity.condition.EntityExpr;
import org.ofbiz.entity.condition.EntityOperator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.service.ServiceUtil;
import java.util.ArrayList;

import com.olbius.acc.report.AccountingReportUtil;

import javolution.util.FastList;

exprBldr =  new EntityConditionBuilder();
invoice = context.invoice;
invoiceTypeId = parameters.invoiceTypeId;
result = ServiceUtil.returnSuccess();
if (!invoice && !invoiceTypeId) return;

if(!invoiceTypeId){
	invoiceTypeId = invoice.invoiceTypeId
}

if(invoiceTypeId == null || !invoiceTypeId) return;
glAccountOrganizationAndClassList = null;
List<Map<String, Object>> invoiceItemTypes = new ArrayList<Map<String, Object>>();
if ("SALES_INVOICE".equals(invoiceTypeId)) {
    itemTypesCond = exprBldr.OR() {
        EQUALS(invoiceItemTypeId: "SINVOICE_HEAD_ADJ")
        EQUALS(invoiceItemTypeId: "SINVOICE_ITM_ADJ")
        EQUALS(invoiceItemTypeId: "INV_PROD_ITEM")
        /*EQUALS(invoiceItemTypeId: "SRT_PROD_ITEM")*/
    }
    ittTmp = delegator.findList("InvoiceItemType", itemTypesCond, null, ["parentTypeId", "invoiceItemTypeId"], null, false);
	String par = "";
	for(GenericValue e : ittTmp){
		par = e.getString("invoiceItemTypeId");
		if(!UtilValidate.isEmpty(par)){
			invoiceItemTypes.addAll(AccountingReportUtil.getAllInvoiceItemType(par, delegator));
		}
	}
   if(invoice) glAccountOrganizationAndClassList = delegator.findByAnd("GlAccountOrganizationAndClass", [organizationPartyId : invoice.partyIdFrom], null, false);
} else if ("PURCHASE_INVOICE".equals(invoiceTypeId)) {
    itemTypesCond = exprBldr.OR() {
        EQUALS(invoiceItemTypeId: "PINVOICE_ADJ")
        EQUALS(invoiceItemTypeId: "PINVOICE_ITM_ADJ")
        EQUALS(invoiceItemTypeId: "PINV_PROD_ITEM")
        /*EQUALS(invoiceItemTypeId: "CRETURN_ADJ")*/
        /*EQUALS(invoiceItemTypeId: "CRT_PROD_ITEM")*/
    }
    ittTmp = delegator.findList("InvoiceItemType", itemTypesCond, null, ["parentTypeId", "invoiceItemTypeId"], null, false);
	for(GenericValue e : ittTmp){
		par = e.getString("invoiceItemTypeId");
		if(!UtilValidate.isEmpty(par)){
			invoiceItemTypes.addAll(AccountingReportUtil.getAllInvoiceItemType(par, delegator));
		}
	}
   if(invoice) glAccountOrganizationAndClassList = delegator.findByAnd("GlAccountOrganizationAndClass", [organizationPartyId : invoice.partyId], null, false);
} else if ("PAYROL_INVOICE".equals(invoiceTypeId)) {
    itemTypesCond = exprBldr.OR() {
        EQUALS(parentTypeId: "PAYROL")
    }
    ittTmp = delegator.findList("InvoiceItemType", itemTypesCond, null, ["parentTypeId", "invoiceItemTypeId"], null, false);
    parentList = EntityUtil.getFieldListFromEntityList(ittTmp,"parentTypeId",true);
	for(String e : parentList){
		if(!UtilValidate.isEmpty(e)){
			invoiceItemTypeSet = AccountingReportUtil.getAllInvoiceItemTypeWithHashMap(e, delegator);
			Iterator invoiceItemIterator = invoiceItemTypeSet.iterator();
			while(invoiceItemIterator.hasNext()){
				Map<String,Object> mapTemp = (Map<String,Object>) invoiceItemIterator.next();
				invoiceItemTypes.add(mapTemp);
			}
		}
	}
   if(invoice) glAccountOrganizationAndClassList = delegator.findByAnd("GlAccountOrganizationAndClass", [organizationPartyId : invoice.partyId], null, false);
} else if ("COMMISSION_INVOICE".equals(invoiceTypeId)) {
    itemTypesCond = exprBldr.OR() {
        EQUALS(invoiceItemTypeId: "COMM_INV_ITEM")
        EQUALS(invoiceItemTypeId: "COMM_INV_ADJ")
    }
    ittTmp = delegator.findList("InvoiceItemType", itemTypesCond, null, ["parentTypeId", "invoiceItemTypeId"], null, false);
	for(GenericValue e : ittTmp){
		par = e.getString("invoiceItemTypeId");
		if(!UtilValidate.isEmpty(par)){
			invoiceItemTypes.addAll(AccountingReportUtil.getAllInvoiceItemType(par, delegator));
		}
	}
   if(invoice) glAccountOrganizationAndClassList = delegator.findByAnd("GlAccountOrganizationAndClass", [organizationPartyId : invoice.partyId], null, false);
} else {
    map = delegator.findByAnd("InvoiceItemTypeMap", [invoiceTypeId : invoiceTypeId], null, true);
    ittTmp = EntityUtil.getRelated("InvoiceItemType", map);
    parentList = EntityUtil.getFieldListFromEntityList(ittTmp,"parentTypeId",true);
	for(String e : parentList){
		if(!UtilValidate.isEmpty(e)){
			invoiceItemTypes.addAll(AccountingReportUtil.getAllInvoiceItemType(e, delegator));
		}
	}
}
context.invoiceItemTypes = invoiceItemTypes;
result.invoiceItemTypes = invoiceItemTypes;
context.glAccountOrganizationAndClassList = glAccountOrganizationAndClassList;
return result;