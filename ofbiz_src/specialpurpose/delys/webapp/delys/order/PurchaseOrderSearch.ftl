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
	<form method="post" id="paging" style="margin: -12px !important" action="<@ofbizUrl>findPurchase</@ofbizUrl>">
    <table class="basic-table" cellspacing='0'>
         <tr>
        <td>
          <#if state.hasPrevious()>
          <#assign viewIndex= state.getViewIndex() - 1/>
          <#assign viewSize= state.getViewSize()/>
          <#assign filterDate= filterDate?if_exists />
            <a href="javascript:void(0)" onclick="paging.submit()" class="btn btn-primary btn-mini margin-left10"><i class="icon-arrow-left  icon-on-left"></i>&nbsp;${uiLabelMap.CommonPrevious}</a>
          </#if>
        </td>
        <td align="right">
          <#if state.hasNext()>
            <#assign viewIndex= state.getViewIndex() + 1/>
          	<#assign viewSize= state.getViewSize()/>
          	<#assign filterDate= filterDate?if_exists />                 
            <a href="javascript:void(0)" onclick="paging.submit()" class="btn btn-primary btn-mini margin-left10"><i class="icon-arrow-right  icon-on-right"></i>&nbsp;${uiLabelMap.CommonNext}</a>
          </#if>
        </td>
      </tr>
    </table>
    <input type="hidden" name="viewIndex" value="${viewIndex}" />
    <input type="hidden" name="viewSize" value="${viewSize}" />
    <input type="hidden" name="filterDate" value="${filterDate}" />
    </form>
</#macro>
<#if hasPermission>
<div id="orderLookup" class="widget-box transparent no-bottom-border" style="margin-top:30px;">
    <#--<div class="widget-header">
        <h4>${uiLabelMap.OrderLookupOrder}</h4>
        <div class="widget-toolbar">
       	<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
		</div> -->
    </div>
    <div class="widget-body">
    <div class="widget-body-inner">
    <div class="widget-main">
      <form method="post" name="findorder" style="margin: -12px !important" action="<@ofbizUrl>findPurchase</@ofbizUrl>">
        <input type="hidden" name="changeStatusAndTypeState" value="Y" />
        <table cellspacing='0'>
        <tr>
        	<td width="30%" style="font-weight:bold;">${uiLabelMap.listStore}</td><td width="30%" style="font-weight:bold;">${uiLabelMap.CommonType}</td>
        </tr>
        <tr>  
	        <td width="30%">
	        <#if parameters.listAllDistributor?has_content>
	        <select name="productStoreId">
				 	<option value="">${uiLabelMap.All}</option>
				 	<#list parameters.listAllDistributor as store>
				  		<option value="${store.productStoreId}">${store.storeName?if_exists}</option>
				 	</#list>
			</select>
			<#else>
				<p class="alert alert-info">${uiLabelMap.NotHadStore}</p>
			</#if>
			</td>
			<td width="30%" nowrap="nowrap">
				<div>
                    <label>
						<input type="checkbox" name="view_SALES_ORDER" value="Y" <#if state.hasType('view_SALES_ORDER')>checked="checked"</#if>/><span class="lbl">&nbsp${descr_SALES_ORDER}</span>
					</label>
                    <label>
						<input type="checkbox" name="view_PURCHASE_ORDER" value="Y" <#if state.hasType('view_PURCHASE_ORDER')>checked="checked"</#if>/><span class="lbl">&nbsp${descr_PURCHASE_ORDER}</span>
					</label>
                </div>
			</td>
		</tr>
	      <tr>
	         <td width="30%" align="left" class="width-table-column10" style="font-weight:bold;">${uiLabelMap.CommonStatus}</td><td width="30%" style="font-weight:bold;"> ${uiLabelMap.CommonFilter}</td>
	 	  </tr>
     	  <tr>
     	  	<td width="30%">
     	  		<label>
					<input type="checkbox" name="viewall" value="Y" onclick="javascript:setCheckboxes()" <#if state.hasAllStatus()>checked="checked"</#if>><span class="lbl">&nbsp${uiLabelMap.CommonAll}</span>
				</label>
     	  	</td>
     	  	<td width="30%">
     	  		<label>
					<input type="checkbox" name="filterInventoryProblems" value="Y"
                    <#if state.hasFilter('filterInventoryProblems')>checked="checked"</#if>/>
                    <span class="lbl">&nbsp${uiLabelMap.OrderFilterInventoryProblems}</span>
				</label>
     	  	</td>
 	  	  </tr>
 	  	   <tr>
     	  	<td width="30%">
     	  		<label>
					<input type="checkbox" name="viewcreated" value="Y" <#if state.hasStatus('viewcreated')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonCreated}</span>
				</label>
     	  	</td>
     	  	<td width="30%">
     	  		<label>
					<input type="checkbox" name="filterAuthProblems" value="Y"
                    <#if state.hasFilter('filterAuthProblems')>checked="checked"</#if>/>
                    <span class="lbl">&nbsp${uiLabelMap.OrderFilterAuthProblems}</span>
				</label>
     	  	</td>
 	  	  </tr>
 	  	   <tr>
     	  	<td width="30%">
     	  		<label>
					<input type="checkbox" name="viewprocessing" value="Y" <#if state.hasStatus('viewprocessing')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonProcessing}</span>
				</label>
     	  	</td>
     	  	<td width="30%" style="font-weight:bold;">&nbsp${uiLabelMap.CommonFilter} (${uiLabelMap.OrderFilterPOs})</td>
 	  	  </tr>
 	  	   <tr>
     	  	<td width="30%">
     	  		 <label>
					<input type="checkbox" name="viewapproved" value="Y" <#if state.hasStatus('viewapproved')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonApproved}</span>
				</label>
     	  	</td>
     	  	<td width="30%">
     	  		<label>
					<input type="checkbox" name="filterPartiallyReceivedPOs" value="Y"
                    <#if state.hasFilter('filterPartiallyReceivedPOs')>checked="checked"</#if>/>
                    <span class="lbl">&nbsp${uiLabelMap.OrderFilterPartiallyReceivedPOs}</span>
				</label>
     	  	</td>
 	  	  </tr>
 	  	   <tr>
     	  	<td width="30%">
     	  		
                <label>
					<input type="checkbox" name="viewhold" value="Y" <#if state.hasStatus('viewhold')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonHeld}</span>
				</label>
     	  	</td>
     	  	<td width="30%">
     	  		 <label>
					<input type="checkbox" name="filterPOsOpenPastTheirETA" value="Y"
                    <#if state.hasFilter('filterPOsOpenPastTheirETA')>checked="checked"</#if>/>
                    <span class="lbl">&nbsp${uiLabelMap.OrderFilterPOsOpenPastTheirETA}</span>
				</label>
     	  	</td>
 	  	  </tr>
 	  	   <tr>
     	  	<td width="30%">
     	  		<label>
					<input type="checkbox" name="viewcompleted" value="Y" <#if state.hasStatus('viewcompleted')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonCompleted}</span>
				</label>
     	  	</td>
     	  	<td width="30%">
     	  		 <label>
					<input type="checkbox" name="filterPOsWithRejectedItems" value="Y"
                    <#if state.hasFilter('filterPOsWithRejectedItems')>checked="checked"</#if>/>
                    <span class="lbl">&nbsp${uiLabelMap.OrderFilterPOsWithRejectedItems}</span>
				</label>
     	  	</td>
 	  	  </tr>
 	  	   <tr>
     	  	<td width="30%">
     	  		<label>
					<input type="checkbox" name="viewrejected" value="Y" <#if state.hasStatus('viewrejected')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonRejected}</span>
				</label>
     	  	</td>
     	  	<td width="30%"></td>
 	  	  </tr>
 	  	   <tr>
     	  	<td width="30%">
     	  		 <label>
					<input type="checkbox" name="viewcancelled" value="Y" <#if state.hasStatus('viewcancelled')>checked="checked"</#if> /><span class="lbl">&nbsp${uiLabelMap.CommonCancelled}</span>
				</label>
     	  	</td>
     	  	<td width="30%"></td>
 	  	  </tr>
 	  	  <tr>
     	  	<td width="30%">
 	  	  <label>
				<input type="checkbox" name="viewnppapproved" value="Y" <#if state.hasStatus('viewnppapproved')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.DADistributorApproved}</span>
			</label>
			</td>
			</tr>
			<tr>
			<td>
			<label>
				<input type="checkbox" name="viewsupapproved" value="Y" <#if state.hasStatus('viewsupapproved')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.DASupApproved}</span>
			</label></td>
			</tr>
			<tr>
			<td>
			<label>
				<input type="checkbox" name="viewsadapproved" value="Y" <#if state.hasStatus('viewsadapproved')>checked="checked"</#if> /><span class="lbl">${uiLabelMap.DASadApproved}</span>
			</label></td>
			</tr>
          <tr>
            <td colspan="2" align="center">
              <a href="javascript:document.findorder.submit()" class="btn btn-primary btn-small icon-search open-sans"> ${uiLabelMap.CommonFind}</a>
            </td>
          </tr>
        </table>
      </form>
    </div>
    </div>
    </div>
 </div>
 <#else>
  <div><p class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</p></div>
</#if>
 
