<#if hasPermission>
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
<#-- order list -->
<div id="orderLookup" >
	<div class="row-fluid">
    	<div class="widget-main">
      		<form method="post" name="findorder" style="margin: -12px !important" action="<#if targetOrderSearch?exists><#if targetOrderSearch == "orderListSm"><@ofbizUrl>orderListSmOrigin</@ofbizUrl><#elseif targetOrderSearch == "orderListPurchaseDis"><@ofbizUrl>findPurcharseOrderListDis</@ofbizUrl><#elseif targetOrderSearch == "orderListSalesDis"><@ofbizUrl>findSalesOrderListDis</@ofbizUrl></#if><#else><@ofbizUrl>orderListOrigin</@ofbizUrl></#if>">
        		<input type="hidden" name="changeStatusAndTypeState" value="Y" />
        		<input type="hidden" name="view_SALES_ORDER" value="Y" />
        		<input type="hidden" name="isFilterProductStoreId" value="Y" />
        		<input type="hidden" name="viewSize" value="${viewSize?default(1)}" />
        		<input type="hidden" name="viewIndex" value="${viewIndex?default(0)}" />
    			<div class="span12 no-left-margin">
    				<div class="span6 no-left-margin">
    					<label for="productStoreId"><b>${uiLabelMap.listStore}:&nbsp;</b>
		    				<#if parameters.listAllStore?has_content>
						        <select name="productStoreId" id="productStoreId">
								 	<option value="">${uiLabelMap.All}</option>
								 	<#list parameters.listAllStore as store>
								  		<option value="${store.productStoreId}"<#if parameters.productStoreId?exists && parameters.productStoreId == store.productStoreId> selected="selected"</#if>>${store.storeName?if_exists}</option>
								 	</#list>
								</select>
							<#else>
								<p class="alert alert-info">${uiLabelMap.NotHadStore}</p>
							</#if>
		    			</label>
    				</div><!--.span6 001-->
    				<div class="span6" style="padding-top: 5px;">
    					<label>
    						<b>${uiLabelMap.CommonStatus}:&nbsp;&nbsp;&nbsp;</b>
    						<input type="checkbox" name="viewall" value="Y" onclick="javascript:setCheckboxes()" <#if state.hasAllStatus()>checked="checked"</#if>>
    						<span class="lbl" style="vertical-align:bottom">&nbsp${uiLabelMap.DASelectAll}</span>
    					</label>
    				</div><!--.span6 002-->
    			</div>
				
				<#--
				<label for="productStoreId"><b>${uiLabelMap.CommonFilter} (${uiLabelMap.OrderFilterPOs})</b></label>
				<label>
					<input type="checkbox" name="filterPartiallyReceivedPOs" value="Y"
                    <#if state.hasFilter('filterPartiallyReceivedPOs')>checked="checked"</#if>/>
                    <span class="lbl">&nbsp${uiLabelMap.OrderFilterPartiallyReceivedPOs}</span>
				</label>
				<label>
					<input type="checkbox" name="filterPOsOpenPastTheirETA" value="Y"
                    <#if state.hasFilter('filterPOsOpenPastTheirETA')>checked="checked"</#if>/>
                    <span class="lbl">&nbsp${uiLabelMap.OrderFilterPOsOpenPastTheirETA}</span>
				</label>
				<label>
					<input type="checkbox" name="filterPOsWithRejectedItems" value="Y"
                    <#if state.hasFilter('filterPOsWithRejectedItems')>checked="checked"</#if>/>
                    <span class="lbl">&nbsp${uiLabelMap.OrderFilterPOsWithRejectedItems}</span>
				</label> -->
					
    			<label><b>${uiLabelMap.CommonStatus}:&nbsp;</b></label>
    			<div class="span12" style="margin-left:10px">
    				<div class="span3">
    					<label></label>
						<label><input type="checkbox" name="viewcreated" value="Y" <#if state.hasStatus('viewcreated')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonCreated}</span></label>
						<label><input type="checkbox" name="viewprocessing" value="Y" <#if state.hasStatus('viewprocessing')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonProcessing}</span></label>
						<label><input type="checkbox" name="viewapproved" value="Y" <#if state.hasStatus('viewapproved')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonApproved}</span></label>
					</div><!--.span3 001-->
    				<div class="span3">
    					<label><input type="checkbox" name="viewsupapproved" value="Y" <#if state.hasStatus('viewsupapproved')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.DASupApproved}</span></label>
						<label><input type="checkbox" name="viewsadapproved" value="Y" <#if state.hasStatus('viewsadapproved')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.DASadApproved}</span></label>
						<label><input type="checkbox" name="viewnppapproved" value="Y" <#if state.hasStatus('viewnppapproved')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.DADistributorApproved}</span></label>
    					<#--<input type="checkbox" name="viewsent" value="Y" <#if state.hasStatus('viewsent')>checked="checked"</#if> />${uiLabelMap.CommonSent}-->
    				</div><!--.span3 002-->
    				<div class="span3">
    					<label><input type="checkbox" name="viewhold" value="Y" <#if state.hasStatus('viewhold')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.DAHeld}</span></label>
						<label><input type="checkbox" name="viewcompleted" value="Y" <#if state.hasStatus('viewcompleted')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonCompleted}</span></label>
						<label><input type="checkbox" name="viewrejected" value="Y" <#if state.hasStatus('viewrejected')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonRejected}</span></label>
    					<label><input type="checkbox" name="viewcancelled" value="Y" <#if state.hasStatus('viewcancelled')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonCancelled}</span></label>
    				</div><!--.span3 003-->
    				<div class="span3">
    					<label for="productStoreId"><b>${uiLabelMap.CommonType}</b></label>
						<div>
		                    <label><input type="checkbox" name="view_SALES_ORDER" value="Y" <#if state.hasType('view_SALES_ORDER')>checked="checked"</#if>/><span class="lbl">&nbsp${descr_SALES_ORDER}</span></label>
		                    <label><input type="checkbox" name="view_PURCHASE_ORDER" value="Y" <#if state.hasType('view_PURCHASE_ORDER')>checked="checked"</#if>/><span class="lbl">&nbsp${descr_PURCHASE_ORDER}</span></label>
		                </div>
    				</div><!--.span3 003-->
    			</div><!--.span12-->
    			<div class="span12" style="margin-left:10px">
	    			<div class="span3" style="margin-top:-10px">
	    				<label style="text-decoration: underline; margin-bottom:5px"><b>${uiLabelMap.CommonFilter}:</b></label>
		                <label>
							<input type="checkbox" name="filterInventoryProblems" value="Y"
		                    <#if state.hasFilter('filterInventoryProblems')>checked="checked"</#if>/>
		                    <span class="lbl">&nbsp${uiLabelMap.OrderFilterInventoryProblems}</span>
						</label>
						<labe>
							<input type="checkbox" name="filterAuthProblems" value="Y"
		                    <#if state.hasFilter('filterAuthProblems')>checked="checked"</#if>/>
		                    <span class="lbl">&nbsp${uiLabelMap.OrderFilterAuthProblems}</span>
						</label>
					</div><!--.span3-->
					<div class="span3">
					</div><!--.span3-->
					<div class="span3">
					</div><!--.span3-->
	    			<div class="span3">
	    				<a href="javascript:document.findorder.submit()" class="btn btn-primary btn-small icon-search open-sans"> ${uiLabelMap.CommonFind}</a>
	    			</div><!--.span3-->
    			</div><!--.span12-->
      		</form>
		</div>
	</div>
</div>
<hr />
<#else>
  	<div><p class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</p></div>
</#if>