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
<style type="text/css">
	.nav-pagesize select {
		padding:2px !important;
		margin:0 !important;
	}
</style>

<#assign viewSize= state.getViewSize()/>
<#assign filterDate= filterDate?if_exists />

<#-- order list -->
<#if hasPermission>
  	<div class="widget-body">	
  	<#--
  	<div class="widget-header">
  		<#if productStoreId == "1">
	        <h4>${uiLabelMap.OrderOrderList} ${uiLabelMap.for} ${uiLabelMap.allStore}</h4>
        <#else>
        	<h6>${uiLabelMap.OrderOrderList} ${uiLabelMap.for} ${storeName?if_exists} - ${productStoreId?if_exists}</h6>
        </#if>-->
       <#-- <div class="widget-toolbar">
       	<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
		</div>
    </div> -->
    <#-- Only allow the search fields to be hidden when we have some results -->
	<#if orderHeaderList?has_content>
	  	<#assign hideFields = parameters.hideFields?default("N")>
	<#else>
	  	<#assign hideFields = "N">
	</#if>
    <#-- Pagination -->
    <#assign viewIndex = state.getViewIndex()?default(0) />
    	<#--
    	<#if state.hasPrevious()>
          	<#assign viewIndex= state.getViewIndex() - 1/>
          	<#assign viewSize= state.getViewSize()/>
         	<#assign filterDate= filterDate?if_exists />
        </#if>
        <#if state.hasNext()>
            <#assign viewIndex= state.getViewIndex() + 1/>
          	<#assign viewSize= state.getViewSize()/>
          	<#assign filterDate= filterDate?if_exists />                 
      	</#if>
    	-->
    	<#if requestNameScreen?exists && "purcharseOrderListDis" == requestNameScreen>
    		<#assign requestUri = "findPurcharseOrderListDis"/>
    	<#elseif requestNameScreen?exists && "salesOrderListDis" == requestNameScreen>
    		<#assign requestUri = "findSalesOrderListDis"/>
    	<#else>
    		<#assign requestUri = "orderListSm"/>
    	</#if>
      	<#if viewIndex?exists>
	      	<#assign lowIndex = viewIndex * viewSize + 1>
	      	<#assign highIndex = viewIndex * viewSize + listSizeDisplay>
      	</#if>

		<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
		<#assign commonUrl = "${requestUri}?hideFields=" + hideFields?if_exists + paramList?if_exists + "&sortField=" + sortField?if_exists + "&"/>
		<#assign viewIndexFirst = 0/>
	    <#assign viewIndexPrevious = viewIndex - 1/>
	    <#assign viewIndexNext = viewIndex + 1/>
	    <#assign viewIndexLast = Static["java.lang.Math"].floor(listSize/viewSize)/>
	    <#assign messageMap = Static["org.ofbiz.base.util.UtilMisc"].toMap("lowCount", lowIndex, "highCount", highIndex, "total", listSize)/>
	    <#assign commonDisplaying = Static["org.ofbiz.base.util.UtilProperties"].getMessage("CommonUiLabels", "CommonDisplaying", messageMap, locale)/>
	    <@nextPrev commonUrl=commonUrl ajaxEnabled=false javaScriptEnabled=false paginateStyle="dataTables_paginate paging_bootstrap pagination" 
	    		paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=listSize viewSize=viewSize ajaxFirstUrl="" firstUrl="" 
	    		paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" 
	    		ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" 
	    		ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
    
	 	<div class="widget-main">
			<div class="row-fluid">
				<div class="form-horizontal basic-custom-form form-decrease-padding" style="display: block;"> 
        			<table class="table table-striped table-bordered table-hover dataTable" cellspacing='0' style="width: 100%;">
			          	<thead>
			          	<tr class="header-row">
			          		<th width="3%">${uiLabelMap.DANo}</th>
				            <th width="8%">${uiLabelMap.DACreateDate}</th>
				            <th>${uiLabelMap.DAOrderId}</th>
				            <th>${uiLabelMap.DAOrderName}</th>
				            <th width="10%">${uiLabelMap.OrderOrderType}</th>
				            <#--
				            <th width="10%">${uiLabelMap.OrderOrderBillFromParty}</th>
				            <th width="10%">${uiLabelMap.OrderOrderBillToParty}</th>
				            -->
				            <th>${uiLabelMap.DACustomer}</th>
				            <th width="10%">${uiLabelMap.OrderProductStore}</th>
				            <th width="10%">${uiLabelMap.CommonAmount}</th>
				            <#--<th width="10%">${uiLabelMap.OrderTrackingCode}</th>-->
				            <#if state.hasFilter('filterInventoryProblems') || state.hasFilter('filterAuthProblems') || state.hasFilter('filterPOsOpenPastTheirETA') || state.hasFilter('filterPOsWithRejectedItems') || state.hasFilter('filterPartiallyReceivedPOs')>
				                <th width="10%">${uiLabelMap.CommonStatus}</th>
				                <#if state.hasFilter('filterInventoryProblems') || state.hasFilter('filterAuthProblems') || state.hasFilter('filterPOsOpenPastTheirETA') || state.hasFilter('filterPOsWithRejectedItems') || state.hasFilter('filterPartiallyReceivedPOs')>
				                <th width="5%">${uiLabelMap.CommonFilter}</th>
				                </#if>
				            <#else>
				                <th colspan="2" width="15%">${uiLabelMap.CommonStatus}</th>
				            </#if>
			          	</tr>
			          	</thead>
          				<#list orderHeaderList as orderHeader>
          					<#assign status = delegator.findOne("StatusItem", {"statusId" : orderHeader.statusId}, false)>
            				<#--<#assign status = orderHeader.getRelatedOne("StatusItem", true)>-->
            				<#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
        					<#--
        					<#assign billToParty = orh.getBillToParty()?if_exists>
            				<#assign billFromParty = orh.getBillFromParty()?if_exists>
				            <#if billToParty?has_content>
				                <#assign billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billToParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
				                <#assign billTo = billToPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")/>
				                <#-- <#assign billTo = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(billToParty, true)?if_exists>
				            <#else>
				              	<#assign billTo = ''/>
				            </#if>
				            <#if billFromParty?has_content>
				              	<#assign billFrom = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(billFromParty, true)?if_exists>
				            <#else>
				              	<#assign billFrom = ''/>
				            </#if>
        					-->
            				<#--<#assign productStore = orderHeader.getRelatedOne("ProductStore", true)?if_exists />-->
            				<#assign productStore = delegator.findOne("ProductStore", {"productStoreId" : orderHeader.productStoreId}, false)?if_exists>
            				<#assign displayParty = orh.getPlacingParty()>
            				<tr>
            					<td><#if viewIndex?exists>${orderHeader_index + lowIndex}<#else>${orderHeader_index + 1}</#if></td>
				              	<td nowrap><#if orderHeader.orderDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy - HH:mm:ss", locale, timeZone)!}</#if></td>
				              	<td>
					                <a href="<@ofbizUrl>orderView?orderId=${orderHeader.orderId}</@ofbizUrl>">${orderHeader.orderId}</a>
				              	</td>
				              	<td>
				              		<#if orderHeader.orderName?exists && orderHeader.orderName?has_content && (orderHeader.orderName?length &gt; 40)>
				              			<span title="${orderHeader.orderName}">${orderHeader.orderName?substring(0,40)} ...</span>
				              		<#else>
				              			${orderHeader.orderName?if_exists}
				              		</#if>
				              	</td>
				              	<td>
				              		<#--${orderHeader.getRelatedOne("OrderType", true).get("description",locale)}-->
				              		<#assign orderType = delegator.findOne("OrderType", {"orderTypeId" : orderHeader.orderTypeId}, false)>
				              		${orderType.get("description",locale)}
				              	</td>
				              	<td>
				              		<#if displayParty.groupName?exists && displayParty.groupName?has_content>
				              			<span id="partyDetailModel" title="${displayParty.groupName}" style="cursor:help;color:#005580" 
				              				onclick="javascript:showCustomerDetail('${displayParty.partyId?default('')}', 
				              					'${displayParty.groupName?default('')}', '${displayParty.officeSiteName?default('')}', 
				              					'${displayParty.groupNameLocal?default('')}', '${displayParty.comments?default('')}', '${displayParty.logoImageUrl?default('')}')">${displayParty.partyId}</span>
				              		<#else>
				              			<span title="N/A">${displayParty.partyId}</span>
			              			</#if>
			              		</td>
				              	<#--
				              	<td>${billFrom?if_exists}</td>
				              	<td>${billTo?if_exists}</td>
				              	-->
				              	<td><#if productStore?has_content>${productStore.storeName?default(productStore.productStoreId)}</#if></td>
				              	<td class="align-right"><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom/></td>
				              	<#--
				              	<td>
					                <#assign trackingCodes = orderHeader.getRelated("TrackingCodeOrder", null, null, false)>
					                <#if trackingCodes?has_content>
					                <#list trackingCodes as trackingCode>
					                    <#if trackingCode?has_content>
					                        <a class="btn btn-primary btn-mini" href="/marketing/control/FindTrackingCodeOrders?trackingCodeId=${trackingCode.trackingCodeId}&amp;externalLoginKey=${requestAttributes.externalLoginKey?if_exists}">${trackingCode.trackingCodeId}</a><br />
					                    </#if>
					                </#list>
					                </#if>
				              	</td>
				              	-->
              					<td>
              						<#--${orderHeader.getRelatedOne("StatusItem", true).get("description",locale)}-->
              						${status.get("description",locale)}
              					</td>
              					<#if state.hasFilter('filterInventoryProblems') || state.hasFilter('filterAuthProblems') || state.hasFilter('filterPOsOpenPastTheirETA') || state.hasFilter('filterPOsWithRejectedItems') || state.hasFilter('filterPartiallyReceivedPOs')>
              						<td>
                  						<#if filterInventoryProblems.contains(orderHeader.orderId)>
                    						Inv&nbsp;
                  						</#if>
					                  	<#if filterAuthProblems.contains(orderHeader.orderId)>
						                   Aut&nbsp;
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
        					</tr>
          				</#list>
          				<#if !orderHeaderList?has_content>
            				<tr><td colspan="10"><p class="alert alert-info">${uiLabelMap.DAOrderNoOrderFound}</p></td></tr>
          				</#if>
        			</table>
        			
    			</div>
    		</div>
    	</div>
    	<#-- Pagination -->
		<#include "component://common/webcommon/includes/htmlTemplate.ftl"/>
	    <@nextPrev commonUrl=commonUrl ajaxEnabled=false javaScriptEnabled=false paginateStyle="dataTables_paginate paging_bootstrap pagination" paginateFirstStyle="nav-first" viewIndex=viewIndex highIndex=highIndex listSize=listSize viewSize=viewSize ajaxFirstUrl="" firstUrl="" paginateFirstLabel="" paginatePreviousStyle="nav-previous" ajaxPreviousUrl="" previousUrl="" paginatePreviousLabel="" pageLabel="" ajaxSelectUrl="" selectUrl="" ajaxSelectSizeUrl="" selectSizeUrl="" commonDisplaying=commonDisplaying paginateNextStyle="nav-next" ajaxNextUrl="" nextUrl="" paginateNextLabel="" paginateLastStyle="nav-last" ajaxLastUrl="" lastUrl="" paginateLastLabel="" paginateViewSizeLabel="" />
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
						<tr>
							<td>${uiLabelMap.DAOfficeSite}</td>
							<td><div id="modal-officeSite"></div></td>
						</tr>
						<tr>
							<td>${uiLabelMap.DALocal}</td>
							<td><div id="modal-local"></div></td>
						</tr>
						<tr>
							<td>${uiLabelMap.DAComments}</td>
							<td><div id="modal-comments"></div></td>
						</tr>
						<tr>
							<td>${uiLabelMap.DALogoImage}</td>
							<td><div id="modal-logoImage"></div></td>
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
  		function showCustomerDetail(partyId, groupName, officeSiteName, groupNameLocal, comments, logoImageUrl) {
  			var imgLogo = "<img src='" + logoImageUrl + "' width='100px' height='20px' alt='" + logoImageUrl + "' />";
  			$('#modal-customerId').html(partyId);
  			$('#modal-customerName').html(groupName);
  			$('#modal-officeSite').html(officeSiteName);
  			$('#modal-local').html(groupNameLocal);
  			$('#modal-comments').html(comments);
  			$('#modal-logoImage').html(imgLogo);
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
  	<div id="modal-table-background" class="modal-backdrop hide fade in" 
		onclick="javascript:closeModelTable();" style="z-index:990"></div>
<#else>
  	<div><p class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</p></div>
</#if>
