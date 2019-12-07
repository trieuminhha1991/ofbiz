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

<script language="JavaScript" type="text/javascript">
<!-- //
function lookupOrders(click) {
    orderIdValue = document.lookuporder.orderId.value;
    if (orderIdValue.length > 1) {
        document.lookuporder.action = "<@ofbizUrl>orderView</@ofbizUrl>";
        document.lookuporder.method = "get";
    } else {
        document.lookuporder.action = "<@ofbizUrl>searchOrders</@ofbizUrl>";
    }

    if (click) {
        document.lookuporder.submit();
    }
    return true;
}
function toggleOrderId(master) {
    var form = document.massOrderChangeForm;
    var orders = form.elements.length;
    for (var i = 0; i < orders; i++) {
        var element = form.elements[i];
        if (element.name == "orderIdList") {
            element.checked = master.checked;
        }
    }
}
function setServiceName(selection) {
    document.massOrderChangeForm.action = selection.value;
}
function runAction() {
    var form = document.massOrderChangeForm;
    form.submit();
}

function toggleOrderIdList() {
    var form = document.massOrderChangeForm;
    var orders = form.elements.length;
    var isAllSelected = true;
    for (var i = 0; i < orders; i++) {
        var element = form.elements[i];
        if (element.name == "orderIdList" && !element.checked)
            isAllSelected = false;
    }
    jQuery('#checkAllOrders').attr("checked", isAllSelected);
}

// -->
    function paginateOrderList(viewSize, viewIndex, hideFields) {
        document.paginationForm.viewSize.value = viewSize;
        document.paginationForm.viewIndex.value = viewIndex;
        document.paginationForm.hideFields.value = hideFields;
        document.paginationForm.submit();
    }
</script>

<div id="findOrdersList" style="border:0px" class="widget-box no-border-bottom transparent">
	<!-- <#if requestParameters.hideFields?default("N") == "Y">
  		<a class="icon-search open-sans" style="padding-top:10px;padding-right:30px float:right" href="javascript:document.lookupandhidefields${requestParameters.hideFields}.submit()">${uiLabelMap.CommonShowLookupFields}</a>
	<#else>
    	<#if orderList?exists><a class="icon-search open-sans" style="padding-top:10px;padding-right:30px float:right" href="javascript:document.lookupandhidefields${requestParameters.hideFields?default("Y")}.submit()">${uiLabelMap.CommonHideFields}</a></#if>
   	</#if>-->
	
	<form name="paginationForm" method="post" action="<@ofbizUrl>searchOrders</@ofbizUrl>">
      	<input type="hidden" name="viewSize"/>
      	<input type="hidden" name="viewIndex"/>
      	<input type="hidden" name="hideFields"/>
		<#if paramIdList?exists && paramIdList?has_content>
			<#list paramIdList as paramIds>
				<#assign paramId = paramIds.split("=")/>
				<input type="hidden" name="${paramId[0]}" value="${paramId[1]}"/>
			</#list>
		</#if>
	</form>
	<form name="massOrderChangeForm" method="post" action="javascript:void(0);">
		<div align="right">
			<div style="margin-top:10px">
				<div style="float:left;">
			 		<#--<a class="btn btn-mini btn-primary icon-search open-sans"   href="/partymgr/control/findparty?externalLoginKey=${requestAttributes.externalLoginKey?if_exists}">${uiLabelMap.PartyLookupParty}</a>-->
			        <#if (orderList?has_content && (0 &lt; orderList?size))>
						<#--
				    	<#assign listSize = orderListSize>
						<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
						<#assign commonUrl = "searchOrders?hideFields=" + hideFields?if_exists + paramList?if_exists + "&sortField=" + sortField?if_exists + "&"/>
						<#assign viewIndexFirst = 0/>
					    <#assign viewIndexPrevious = viewIndex - 1/>
					    <#assign viewIndexNext = viewIndex + 1/>
					    <#assign viewIndexLast = Static["java.lang.Math"].floor(listSize/viewSize)/>
					    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount", lowIndex, "highCount", highIndex, "total", listSize)/>
					    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
					    <@nextPrev commonUrl=commonUrl ajaxEnabled=false javaScriptEnabled=false paginateStyle="dataTables_paginate paging_bootstrap pagination" paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=listSize viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
				    	-->
				    	<#if (orderListSize > highIndex)>
							<a class="btn btn-mini btn-primary open-sans" href="javascript:paginateOrderList('${viewSize}', '${viewIndex+1}', '${requestParameters.hideFields?default("N")}')">${uiLabelMap.CommonNext}</a>
						</#if>
						<#if (orderListSize > 0)>
							<span>${uiLabelMap.CommonShow} ${lowIndex} - ${highIndex} ${uiLabelMap.CommonOf} ${orderListSize}</span>
						</#if>
						<#if (viewIndex > 1)>
							<a class="btn btn-mini btn-primary open-sans" href="javascript:paginateOrderList('${viewSize}', '${viewIndex-1}', '${requestParameters.hideFields?default("N")}')">${uiLabelMap.CommonPrevious}</a>
						</#if>
				    	
					</#if>
				</div>
				<!-- <a class="btn btn-mini btn-primary icon-ok open-sans"  href="javascript:lookupOrders(true);">${uiLabelMap.DALookupOrder}</a> -->
				<#--
				<input type="hidden" name="screenLocation" value="component://order/widget/ordermgr/OrderPrintScreens.xml#OrderPDF"/>
		        <select name="serviceName" onchange="javascript:setServiceName(this);">
		           <option value="javascript:void(0);">&nbsp;</option>
		           <option value="<@ofbizUrl>massApproveOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderApproveOrder}</option>
		           <option value="<@ofbizUrl>massHoldOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderHold}</option>
		           <option value="<@ofbizUrl>massProcessOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderProcessOrder}</option>
		           <option value="<@ofbizUrl>massCancelOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderCancelOrder}</option>
		           <option value="<@ofbizUrl>massCancelRemainingPurchaseOrderItems?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderCancelRemainingPOItems}</option>
		           <option value="<@ofbizUrl>massRejectOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderRejectOrder}</option>
		           <option value="<@ofbizUrl>massPickOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderPickOrders}</option>
		           <option value="<@ofbizUrl>massQuickShipOrders?hideFields=${requestParameters.hideFields?default("N")}${paramList}</@ofbizUrl>">${uiLabelMap.OrderQuickShipEntireOrder}</option>
		           <option value="<@ofbizUrl>massPrintOrders?hideFields=${requestParameters.hideFields?default('N')}${paramList}</@ofbizUrl>">${uiLabelMap.CommonPrint}</option>
		           <option value="<@ofbizUrl>massCreateFileForOrders?hideFields=${requestParameters.hideFields?default('N')}${paramList}</@ofbizUrl>">${uiLabelMap.ContentCreateFile}</option>
		        </select>
		        <select name="printerName">
		           	<option value="javascript:void(0);">&nbsp;</option>
		           	<#list printers as printer>
		           		<option value="${printer}">${printer}</option>
		           	</#list>
		        </select>
				<a href="javascript:runAction();" class="btn btn-mini btn-primary open-sans icon-check">${uiLabelMap.OrderRunAction}</a>
				-->
			</div>
		</div>
		
		<table class="table table-striped table-bordered table-hover dataTable" cellspacing='0'>
			<tr class="header-row">
				<#--
				<td>
		            <label>
						<input type="checkbox" id="checkAllOrders" name="checkAllOrders" value="1" onchange="javascript:toggleOrderId(this);"/><span class="lbl"></span>
					</label>
				</td>
				-->
				<td>${uiLabelMap.DANo}</td>
	          	<td>${uiLabelMap.DAOrderType}</td>
	          	<td>${uiLabelMap.DAOrderId}</td>
	          	<td>${uiLabelMap.DACustomerId}</td>
	          	<#--<td>${uiLabelMap.DACustomerName}</td>
	          	<td align="right">${uiLabelMap.OrderSurvey}</td>-->
	          	<td align="right">${uiLabelMap.DAItemsOrdered}</td>
	          	<td align="right">${uiLabelMap.DAItemsBackOrdered}</td>
	          	<td align="right">${uiLabelMap.OrderItemsReturned}</td>
	          	<#--<td>&nbsp;</td>-->
	            <td align="right">${uiLabelMap.DARemainingSubTotal}</td>
	          	<td walign="right">${uiLabelMap.OrderOrderTotal}</td>
				<td>${uiLabelMap.OrderDate}</td>
				<#if (requestParameters.filterInventoryProblems?default("N") == "Y") || (requestParameters.filterPOsOpenPastTheirETA?default("N") == "Y") || (requestParameters.filterPOsWithRejectedItems?default("N") == "Y") || (requestParameters.filterPartiallyReceivedPOs?default("N") == "Y")>
	              	<td>${uiLabelMap.CommonStatus}</td>
	              	<td>${uiLabelMap.CommonFilter}</td>
	            <#else>
	              	<td>${uiLabelMap.CommonStatus}</td>
	            </#if>
				<#--<td>&nbsp;</td>-->
			</tr>
        	<#if orderList?has_content>
	          	<#assign alt_row = false>
	          	<#list orderList as orderHeader>
	            	<#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
		            <#assign statusItem = orderHeader.getRelatedOne("StatusItem", true)!>
		            <#assign orderType = orderHeader.getRelatedOne("OrderType", true)>
		            <#if orderType.orderTypeId == "PURCHASE_ORDER">
		              <#assign displayParty = orh.getSupplierAgent()?if_exists>
		            <#else>
		              <#assign displayParty = orh.getPlacingParty()?if_exists>
		            </#if>
		            <#assign partyId = displayParty.partyId?default("_NA_")>
		            <tr valign="middle"<#if alt_row> class="alternate-row"</#if>>
		              	<#--
		              	<td>
		              		</br>
		                 	<label >
								<input type="checkbox" name="orderIdList" onchange="javascript:toggleOrderIdList();"/><span class="lbl"></span>
							</label>
				      	</td>
		              	-->
		              	<td>${orderHeader_index + viewIndex}</td>
		              	<td>${orderType.get("description",locale)?default(orderType.orderTypeId?default(""))}</td>
		              	<td><a href="<@ofbizUrl>orderView?orderId=${orderHeader.orderId}</@ofbizUrl>" >${orderHeader.orderId}</a></td>
		              	<td>
	      					<#if displayParty?has_content>
		                      	<#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
		                      	<#assign displayPartyNameResultTwo = displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]") />
		                  	<#else>
		                    	<#assign displayPartyNameResultTwo = uiLabelMap.CommonNA />
		                  	</#if>
		                	<#if partyId != "_NA_">
		                  		<#--<a href="${customerDetailLink}${partyId}" R>${partyId}</a>-->
		                  		<span id="partyDetailModel" title="${displayPartyNameResultTwo}" style="cursor:help;color:#005580" 
		              				onclick="javascript:showCustomerDetail('${partyId?default('')}', '${displayPartyNameResultTwo?default('')}')">
	              					${partyId}
          						</span>
		                	<#else>
		                  		<span title="N/A">${uiLabelMap.CommonNA}</span>
		                	</#if>
		                	
		              	</td>
		                <#--
		                <td>
			                <div>
			                  	<#if displayParty?has_content>
			                      	<#assign displayPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", displayParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
			                      	${displayPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")}
			                  	<#else>
			                    	${uiLabelMap.CommonNA}
			                  	</#if>
		                	</div>
			                <div>
			                	<#if placingParty?has_content>
			                  		<#assign partyId = placingParty.partyId>
			                  		<#if placingParty.getEntityName() == "Person">
			                    		<#if placingParty.lastName?exists>
			                      			${placingParty.lastName}<#if placingParty.firstName?exists>, ${placingParty.firstName}</#if>
			                    		<#else>
			                      			${uiLabelMap.CommonNA}
			                    		</#if>
			                  		<#else>
			                    		<#if placingParty.groupName?exists>
			                      			${placingParty.groupName}
			                    		<#else>
			                      			${uiLabelMap.CommonNA}
			                    		</#if>
			                  		</#if>
		                		<#else>
			                  		${uiLabelMap.CommonNA}
		                		</#if>
			                </div>
		                </td>
	            		-->
		              	<#--<td align="right">${orh.hasSurvey()?string.number}</td>-->
		              	<td align="right" class="align-right">${orh.getTotalOrderItemsQuantity()?string.number}</td>
		              	<td align="right" class="align-right">${orh.getOrderBackorderQuantity()?string.number}</td>
		              	<td align="right" class="align-right">${orh.getOrderReturnedQuantity()?string.number}</td>
		              	<#--<td>&nbsp;</td>-->
		              
		              	<td align="right" class="align-right"><@ofbizCurrency amount=orderHeader.remainingSubTotal isoCode=orh.getCurrency()/></td>
		              	<td align="right" class="align-right"><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orh.getCurrency()/></td>
		              	<#if (requestParameters.filterInventoryProblems?default("N") == "Y") || (requestParameters.filterPOsOpenPastTheirETA?default("N") == "Y") || (requestParameters.filterPOsWithRejectedItems?default("N") == "Y") || (requestParameters.filterPartiallyReceivedPOs?default("N") == "Y")>
		                  	<td>
		                      	<#if filterInventoryProblems.contains(orderHeader.orderId)>
		                        	Inv&nbsp;
		                      	</#if>
		                      	<#if filterPOsOpenPastTheirETA.contains(orderHeader.orderId)>
		                        	ETA&nbsp;
		                      	</#if>
		                      	<#if filterPOsWithRejectedItems.contains(orderHeader.orderId)>
		                        	Rej&nbsp;
		                      	</#if>
		                      	<#if filterPartiallyReceivedPOs.contains(orderHeader.orderId)>
		                        	Part&nbsp;
		                      	</#if>
		                  	</td>
		              	</#if>
		              	<td nowrap>
		              		<#if orderHeader.get("orderDate")?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.get("orderDate"), "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}</#if>
		              	</td>
		              	<td><#if statusItem?exists && statusItem?has_content>${statusItem.get("description",locale)?default(statusItem.statusId?default("N/A"))}<#else>N/A</#if></td>
		              	<#--
		              	<td align='right'>
		                	<a href="<@ofbizUrl>orderView?orderId=${orderHeader.orderId}</@ofbizUrl>" class='btn btn-primary btn-mini icon-eye-open open-sans'>${uiLabelMap.CommonView}</a>
		              	</td>
		              	-->
	            	</tr>
		            <#-- toggle the row color -->
		            <#assign alt_row = !alt_row>
	          	</#list>
	        <#else>
	          	<tr>
	            	<td colspan='15'><div class="alert alert-info open-sans">${uiLabelMap.OrderNoOrderFound}</div></td>
	          	</tr>
	        </#if>
	        <#if lookupErrorMessage?exists>
	          	<tr>
	            	<td colspan='15'><div class="alert alert-danger open-sans">${lookupErrorMessage}</div></td>
	          	</tr>
	        </#if>
      	</table>
	</form>
</div>
<div id="modal-table" class="modal hide fade" tabindex="-1">
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal" onclick="javascript:closeModelTable()">&times;</button>
			${uiLabelMap.DAViewCustomerDetail}
		</div>
	</div>
	<div class="modal-body no-padding">
		<div class="row-fluid">
			<table class="table table-striped table-bordered table-hover no-margin-bottom no-border-top">
				<tbody>
					<tr>
						<td>${uiLabelMap.DACustomerId}</td>
						<td><div id="modal-customerId"></div></td>
					</tr>
					<tr>
						<td>${uiLabelMap.DACustomerName}</td>
						<td><div id="modal-customerName"></div></td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>

	<div class="modal-footer">
		<button class="btn btn-small btn-danger pull-left" data-dismiss="modal" onclick="javascript:closeModelTable();">
			<i class="icon-remove"></i>
			Close
		</button>
	</div>
</div>
<script type="text/javascript">
	function showCustomerDetail(partyId, partyName) {
		$('#modal-customerId').html(partyId);
		$('#modal-customerName').html(partyName);
		
		$('#modal-table').slideDown(500);
		$('#modal-table-background').show();
		$('#modal-table').css("opacity", "1", "important");
		$('#modal-table').css("top", "10%");
	}
	function closeModelTable() {
		$('#modal-table').slideUp(500);
		setTimeout(function(){
		    $('#modal-table-background').hide();
	  	}, 600);
	}
</script>
<style type="text/css">
	#partyDetailModel:hover {
		text-decoration: underline;
	}
</style>
<div id="modal-table-background" class="modal-backdrop hide fade in" onclick="javascript:closeModelTable();" style="z-index:990"></div>
