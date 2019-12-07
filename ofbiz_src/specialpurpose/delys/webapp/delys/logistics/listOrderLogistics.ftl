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

<script type="text/javascript">
    var checkBoxNameStart = "view";
    var formName = "findorder";


    function setCheckboxes() {
        // This would be clearer with camelCase variable names
        var allCheckbox = document.forms[formName].elements[checkBoxNameStart + "all"];
        for(i = 0;i < document.forms[formName].elements.length;i++) {
            var elem = document.forms[formName].elements[i];
            if (elem.name.indexOf(checkBoxNameStart) == 0 && elem.name.indexOf("_") < 0 && elem.type == "checkbox") {
                elem.checked = allCheckbox.checked;
            }
        }
    }

</script>

<#macro pagination>
    <table class="basic-table" cellspacing='0'>
         <tr>
	        <td>
	          <#if state.hasPrevious()>
	            <a href="<@ofbizUrl>findOrderLogistics?viewIndex=${state.getViewIndex() - 1}&amp;viewSize=${state.getViewSize()}&amp;filterDate=${filterDate?if_exists}</@ofbizUrl>" class="btn btn-primary btn-mini margin-top20 open-sans"><i class="icon-arrow-left  icon-on-left"></i>&nbsp;${uiLabelMap.CommonPrevious}</a>
	          </#if>
	        </td>
	        <td align="right">
	          <#if state.hasNext()>
	            <a href="<@ofbizUrl>findOrderLogistics?viewIndex=${state.getViewIndex() + 1}&amp;viewSize=${state.getViewSize()}&amp;filterDate=${filterDate?if_exists}</@ofbizUrl>" class="btn btn-primary btn-mini margin-top20 open-sans">${uiLabelMap.CommonNext}&nbsp;<i class="icon-arrow-right  icon-on-right"></i></a>
	          </#if>
	        </td>
      </tr>
    </table>
</#macro>
<#-- order list -->
<div id="orderLookup" class="widget-box olbius-extra">
<div class="row-fluid">
<span class="span12">
	<div class="widget-box collapsed">
		 <h3>${uiLabelMap.OrderLookupOrder}</h3>
    		<div class="widget-toolbar none-content floatright margin-top">
        		<div class="widget-toolbar">
       				<a href="#" data-action="collapse" class="open-sans"><i class="icon-chevron-up"></i><b class="icon-search open-sans">Search Options</b></a>
				</div>
    		</div>

    <div class="widget-body">
    <div class="widget-body-inner">
    <div class="widget-main">
      <form method="post" name="findorder" style="margin: -12px !important" action="<@ofbizUrl>findOrderLogistics</@ofbizUrl>">
        <input type="hidden" name="changeStatusAndTypeState" value="Y" />
        <table class="table table-striped table-bordered table-hover dataTable" cellspacing='0'>
          <tr>
             <td align="right" class="width-table-column10">${uiLabelMap.CommonStatus}</td>
            <td nowrap="nowrap">
                <div>
                    <label>
						<input type="checkbox" name="viewall" value="Y" onclick="javascript:setCheckboxes()" <#if state.hasAllStatus()>checked="checked"</#if>><span class="lbl">${uiLabelMap.CommonAll}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewcreated" value="Y" <#if state.hasStatus('viewcreated')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonCreated}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewprocessing" value="Y" <#if state.hasStatus('viewprocessing')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonProcessing}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewapproved" value="Y" <#if state.hasStatus('viewapproved')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonApproved}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewhold" value="Y" <#if state.hasStatus('viewhold')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonHeld}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewcompleted" value="Y" <#if state.hasStatus('viewcompleted')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonCompleted}</span>
					</label>
                    <#--input type="checkbox" name="viewsent" value="Y" <#if state.hasStatus('viewsent')>checked="checked"</#if> />${uiLabelMap.CommonSent}-->
                    <label>
						<input type="checkbox" name="viewrejected" value="Y" <#if state.hasStatus('viewrejected')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonRejected}</span>
					</label>
                    <label>
						<input type="checkbox" name="viewcancelled" value="Y" <#if state.hasStatus('viewcancelled')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.CommonCancelled}</span>
					</label>
					<label>
						<input type="checkbox" name="viewnppapproved" value="Y" <#if state.hasStatus('viewnppapproved')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.DADistributorApproved}</span>
					</label>
					<label>
						<input type="checkbox" name="viewsubapproved" value="Y" <#if state.hasStatus('viewsubapproved')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.DASubApproved}</span>
					</label>
					<label>
						<input type="checkbox" name="viewsadapproved" value="Y" <#if state.hasStatus('viewsadapproved')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.DASadApproved}</span>
					</label>
                </div>
            </td>
          </tr>
          <tr>
            <td align="right" class="width-table-column10">${uiLabelMap.CommonType}</td>
            <td nowrap="nowrap">
                <div>
                    <label>
						<input type="checkbox" name="view_SALES_ORDER" value="Y" <#if state.hasType('view_SALES_ORDER')>checked="checked"</#if>/><span class="lbl">${descr_SALES_ORDER}</span>
					</label>
                    <label>
						<input type="checkbox" name="view_PURCHASE_ORDER" value="Y" <#if state.hasType('view_PURCHASE_ORDER')>checked="checked"</#if>/><span class="lbl">${descr_PURCHASE_ORDER}</span>
					</label>
                </div>
            </td>
          </tr>
          <tr>
            <td align="right" class="width-table-column10">${uiLabelMap.CommonFilter}</td>
            <td nowrap="nowrap">
                <div>
                    <label>
						<input type="checkbox" name="filterInventoryProblems" value="Y"
                        <#if state.hasFilter('filterInventoryProblems')>checked="checked"</#if>/>
                        <span class="lbl">${uiLabelMap.OrderFilterInventoryProblems}</span>
					</label>
                    <label>
						<input type="checkbox" name="filterAuthProblems" value="Y"
                        <#if state.hasFilter('filterAuthProblems')>checked="checked"</#if>/>
                        <span class="lbl">${uiLabelMap.OrderFilterAuthProblems}</span>
					</label>
                </div>
            </td>
          </tr>
          <tr>
            <td align="right" class="width-table-column10">${uiLabelMap.CommonFilter} (${uiLabelMap.OrderFilterPOs})</td>
            <td nowrap="nowrap">
                <div>
                    <label>
						<input type="checkbox" name="filterPartiallyReceivedPOs" value="Y"
                        <#if state.hasFilter('filterPartiallyReceivedPOs')>checked="checked"</#if>/>
                        <span class="lbl">${uiLabelMap.OrderFilterPartiallyReceivedPOs}</span>
					</label>
                    <label>
						<input type="checkbox" name="filterPOsOpenPastTheirETA" value="Y"
                        <#if state.hasFilter('filterPOsOpenPastTheirETA')>checked="checked"</#if>/>
                        <span class="lbl">${uiLabelMap.OrderFilterPOsOpenPastTheirETA}</span>
					</label>
                    <label>
						<input type="checkbox" name="filterPOsWithRejectedItems" value="Y"
                        <#if state.hasFilter('filterPOsWithRejectedItems')>checked="checked"</#if>/>
                        <span class="lbl">${uiLabelMap.OrderFilterPOsWithRejectedItems}</span>
					</label>
                </div>
            </td>
          </tr>
          <tr>
            <td colspan="2" align="center">
              <a href="javascript:document.findorder.submit()" class="btn btn-primary btn-small icon-search"> ${uiLabelMap.CommonFind}</a>
            </td>
          </tr>
        </table>
      </form>
    </div>
    </div>
    </div>
     
 </div>
<#if hasPermission>
  <div id="findOrdersList" style="border:0px;" class="widget-box olbius-extra">   
  <!--<div class="widget-header widget-header-small header-color-blue2">
        <h6>${uiLabelMap.OrderOrderList}</h6>
        <div class="widget-toolbar">
       	<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
		</div>
    </div>-->
    <div class="widget-body-inner">
    <h3 calss="header-color-blue2">${uiLabelMap.OrderOrderList}</h3>
    <div class="widget-main">
        <table class="table table-striped table-bordered table-hover dataTable" cellspacing='0' >
          <tr class="header-row">
            <td width="15%">${uiLabelMap.CommonDate}</td>
            <td width="10%">${uiLabelMap.OrderOrder} ${uiLabelMap.CommonNbr}</td>
            <td width="10%">${uiLabelMap.OrderOrderName}</td>
            <td width="10%">${uiLabelMap.OrderOrderType}</td>
            <td width="10%">${uiLabelMap.OrderOrderBillFromParty}</td>
            <td width="10%">${uiLabelMap.OrderOrderBillToParty}</td>
            <td width="10%">${uiLabelMap.OrderProductStore}</td>
            <td width="10%">${uiLabelMap.CommonAmount}</td>
            <td width="10%">${uiLabelMap.OrderTrackingCode}</td>
            <#if state.hasFilter('filterInventoryProblems') || state.hasFilter('filterAuthProblems') || state.hasFilter('filterPOsOpenPastTheirETA') || state.hasFilter('filterPOsWithRejectedItems') || state.hasFilter('filterPartiallyReceivedPOs')>
                <td width="10%">${uiLabelMap.CommonStatus}</td>
                <td width="5%">${uiLabelMap.CommonFilter}</td>
            <#else>
                <td colspan="2" width="15%">${uiLabelMap.CommonStatus}</td>
            </#if>
          </tr>
          <#list orderHeaderList as orderHeader>
            <#assign status = orderHeader.getRelatedOne("StatusItem", true)>
            <#assign orh = Static["org.ofbiz.order.order.OrderReadHelper"].getHelper(orderHeader)>
            <#assign billToParty = orh.getBillToParty()?if_exists>
            <#assign billFromParty = orh.getBillFromParty()?if_exists>
            <#if billToParty?has_content>
                <#assign billToPartyNameResult = dispatcher.runSync("getPartyNameForDate", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", billToParty.partyId, "compareDate", orderHeader.orderDate, "userLogin", userLogin))/>
                <#assign billTo = billToPartyNameResult.fullName?default("[${uiLabelMap.OrderPartyNameNotFound}]")/>
                <#-- <#assign billTo = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(billToParty, true)?if_exists> -->
            <#else>
              <#assign billTo = ''/>
            </#if>
            <#if billFromParty?has_content>
              <#assign billFrom = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(billFromParty, true)?if_exists>
            <#else>
              <#assign billFrom = ''/>
            </#if>
            <#assign productStore = orderHeader.getRelatedOne("ProductStore", true)?if_exists />
            <tr>
              <td><#if orderHeader.orderDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "", locale, timeZone)!}</#if></td>
              <td>
                ${orderHeader.orderId}
              </td>
              <td>${orderHeader.orderName?if_exists}</td>
              <td>${orderHeader.getRelatedOne("OrderType", true).get("description",locale)}</td>
              <td>${billFrom?if_exists}</td>
              <td>${billTo?if_exists}</td>
              <td><#if productStore?has_content>${productStore.storeName?default(productStore.productStoreId)}</#if></td>
              <td><@ofbizCurrency amount=orderHeader.grandTotal isoCode=orderHeader.currencyUom/></td>
              <td>
                <#assign trackingCodes = orderHeader.getRelated("TrackingCodeOrder", null, null, false)>
                <#list trackingCodes as trackingCode>
                    <#if trackingCode?has_content>
                        <a class="btn btn-primary btn-mini open-sans" href="/marketing/control/FindTrackingCodeOrders?trackingCodeId=${trackingCode.trackingCodeId}&amp;externalLoginKey=${requestAttributes.externalLoginKey?if_exists}">${trackingCode.trackingCodeId}</a><br />
                    </#if>
                </#list>
              </td>
              <td>${orderHeader.getRelatedOne("StatusItem", true).get("description",locale)}</td>
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
              <#else>
              <td>&nbsp;</td>
              </#if>
            </tr>
          </#list>
          <#if !orderHeaderList?has_content>
            <tr><td colspan="9"><h3>${uiLabelMap.OrderNoOrderFound}</h3></td></tr>
          </#if>
        </table>
        <@pagination/>
   	 		</div>
   		 </div>
    	</span>
<#else>
  <div><p class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</p></div>
</#if>

