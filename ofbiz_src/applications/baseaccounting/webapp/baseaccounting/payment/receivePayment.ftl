<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session)>
	<div class="widget-header widget-header-blue widget-header-flat">
		<h4>${uiLabelMap.BACCOrderReceivePayments} : ${uiLabelMap.OrderOrderTotal} <@ofbizCurrency amount='${parameters.avo}' isoCode='${parameters.avt}' rounding=0/> </h4>
		<span class="widget-toolbar none-content">
			<a href="<@ofbizUrl>${donePage}</@ofbizUrl>">
				<i class="icon-arrow-left icon-on-left open-sans">${uiLabelMap.CommonBack}</i>
			</a>
			<a href="javascript:document.paysetupform.submit()">
				<i class="icon-save open-sans">${uiLabelMap.CommonSave}</i>
			</a>
		</span>
	</div>
	<div class="widget-body">
		<div class="widget-main no-padding">
			<div class="row-fluid">
				<#--action="<@ofbizUrl>receiveOfflinePayments/${donePage}</@ofbizUrl>"-->
	      		<form method="post" action="<@ofbizUrl>receiveOfflinePayments</@ofbizUrl>" name="paysetupform" style="padding-top:15px">
			        <#if requestParameters.workEffortId?exists>
			            <input type="hidden" name="workEffortId" value="${requestParameters.workEffortId}" />
			        </#if>
			        <input type="hidden" name="orderId" value="${parameters.orderId}"/>
	        		<input type="hidden" name="partyId" value="${orderRoles[0].partyId}" />
			        <#if paymentMethods?has_content>
				        <table class="table table-striped table-bordered table-hover dataTable">
				          	<tr class="header-row">
								<td width="15%" align="right">${uiLabelMap.AccountingBankName}</td>
								<td width="20%" align="right">${uiLabelMap.AccountingBankOwner}</td>
					            <td width="20%" align="right">${uiLabelMap.AccountingAccountNumber}</td>
					            <td width="15%" align="right">${uiLabelMap.AccountingBankType}</td>
					            <td width="15%" class="align-center">${uiLabelMap.AccountingOrderAmount}</td>					            
				          	</tr>
		          			<#list paymentMethods as payMethod>							
				          		<#if payMethod.paymentMethodTypeId?if_exists == "COMPANY_CHECK">
									<#assign eftAccount = delegator.findOne("EftAccount",Static["org.ofbiz.base.util.UtilMisc"].toMap("paymentMethodId", "${payMethod.paymentMethodId}"), false) />
					          		<tr>
										<td width="15%" align="right">${eftAccount.get("bankName",locale)?default(eftAccount.paymentMethodId)}</td>
										<td width="20%" align="right">${eftAccount.get("companyNameOnAccount",locale)}</td>
										<td width="20%" align="right">${eftAccount.get("accountNumber",locale)}</td>
										<td width="20%" align="right">${uiLabelMap.bankDeposite}</td>
							            <td width="15%"><input type="text" size="100%" name="${payMethod.paymentMethodId}_amount" /></td>
						          	</tr>
								</#if>
				          		<#if payMethod.paymentMethodTypeId?if_exists == "CREDIT_CARD">
									<#assign creditCard = delegator.findOne("CreditCard",Static["org.ofbiz.base.util.UtilMisc"].toMap("paymentMethodId", "${payMethod.paymentMethodId}"), false) />
					          		<tr>
										<td width="15%" align="right"> <#if creditCard.titleOnCard?exists>${creditCard.titleOnCard} </#if> </td>
										<td width="20%" align="right"> <#if creditCard.companyNameOnCard?exists>${creditCard.companyNameOnCard} </#if> 
				                      	(${creditCard.firstNameOnCard?default("N/A")}&nbsp;
				                      	<#if creditCard.middleNameOnCard?has_content>${creditCard.middleNameOnCard}&nbsp;</#if>
				                      	${creditCard.lastNameOnCard?default("N/A")})</td>
										<td width="20%" align="right">${creditCard.get("cardNumber",locale)}</td>
										<td width="20%" align="right">${uiLabelMap.bankDeposite}</td>
							            <td width="15%"><input type="text" size="100%" name="${payMethod.paymentMethodId}_amount" /></td>
									</tr>
								</#if>
								<#if payMethod.paymentMethodTypeId?if_exists == "CASH">
									<#assign partyName = delegator.findOne("PartyNameView",Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "${payMethod.partyId}"), false) />	
					          		<tr>
										<td width="15%" align="right"></td>
										<td width="20%" align="right"> <#if partyName.groupName?exists>${partyName.groupName} </#if> 
				                      	(${partyName.firstName?default("N/A")}&nbsp;
				                      	<#if partyName.middleName?has_content>${partyName.middleName}&nbsp;</#if>
				                      	${partyName.lastName?default("N/A")})</td>
										<td width="20%" align="right"></td>
										<td width="20%" align="right">${payMethod.description}</td>
							            <td width="15%"><input type="text" size="100%" name="${payMethod.paymentMethodId}_amount"  /></td>
									</tr>
								</#if>
		          			</#list>
		        		</table>
					<#else>
						<tr >
							<td width="100%" align="right"> ${uiLabelMap.PaymentMethodAndPartyPaymentMethod} </td>						</tr>
	        		</#if>
										
	      		</form>
	      	</div>
	      	<div class="form-actions align-right no-bottom-margin margin-top10 margin-bottom10">
	      		<span class="widget-toolbar none-content">
					<a href="<@ofbizUrl>${donePage}</@ofbizUrl>"><i class="icon-arrow-left icon-on-left open-sans">${uiLabelMap.CommonBack}</i></a>
					<a href="javascript:document.paysetupform.submit()"><i class="icon-save open-sans">${uiLabelMap.CommonSave}</i></a>
				</span>
			</div>
		</div>
	</div>
<#else>
	<div class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</div>
</#if>
