<#--
${listSalesForecastAndItems}
-->
<div class="widget-box transparent">
<div class="row-fluid">
    <div class="span12 widget-container-span">
	    <div class="widget-box transparent">
	        <div class="widget-header">
	            <h4>${uiLabelMap.CreateImportPlanItem}: 
	            	<#if customTimePeriodId?exists>
	            		<#assign customTimeParent = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : customTimePeriodId}, false)!/>
                		${customTimeParent.periodName?if_exists}
                	</#if>
	            </h4>
	            <div class="widget-toolbar no-border">
	                <ul class="nav nav-tabs" id="myTab2">
	                    <#list listProductPlans as listProductPlan>
							<li <#if listProductPlan_index == 0> class="active"</#if> style="margin-top:0; margin-bottom:0; padding-top:0 !important; padding-bottom:0 !important">
								<a data-toggle="tab" href="#${listProductPlan_index}-tab">${listProductPlan.internalPartyId}</a>
							</li>
						</#list>
	                </ul>
	            </div>
	        </div>
	        <div class="widget-body">
	            <div class="widget-main padding-12 no-padding-left no-padding-right">
	                <div class="tab-content padding-4">

<#list listProductPlans as listProductPlan>
	<div id="${listProductPlan_index}-tab" class="tab-pane<#if listProductPlan_index == 0> active</#if>">
	<form method="post" action="<@ofbizUrl>EditImportPlanItem</@ofbizUrl>" name="EditPlan">
	<input type="hidden" value="${parameters.customTimePeriodId}" name="customTime"/>
<table>
<tr>
<td>
        <table id="sale-forecast-view" class="table table-striped table-bordered table-hover">
            <thead>
                <tr class="sf-product">
                	<td class="sf-months" colspan="1" rowspan="2" style="text-align:center">${uiLabelMap.Time}</td>
                	<#list listProduct as productItem>
	                    <td style="text-align:center">${productItem.internalName}</td>
                	</#list>
                	<td class="sf-value" rowspan="2" style="text-align:center">Chon</td>
                </tr>
            </thead>
            <tbody>
            <#assign month = 1!>
            <#assign week = 1!>
            <#list listProductPlan.item as planItem>
            <#assign productPlanId = planItem.prPlanIds!>
            <#if month <=21>
                <tr class="sf-current-year">
                    <td colspan="1" class="sf-month" style="">
                    <#if month <=12>
                    	<a>${uiLabelMap.Month}_${month}</a>
                    	<input type="hidden" value="${productPlanId}" name="month"/>
                    <#else>
                    	<a href="<@ofbizUrl>getProductPlanItemToDivide?productPlanId=${productPlanId}</@ofbizUrl>">${uiLabelMap.Week} ${week}</a>
                    	<input type="hidden" value="${productPlanId}" name="month"/>
            			<#assign week = week +1!>
                    </#if>
                    </td>
                    <#list planItem.aMcc as productItem>
                    <#if productItem.planQuantity?exists>
		        			<td>
		        				<input type="text" value="${productItem.planQuantity?string(",##0")}" name="quantity"></input>
		        				<input type="hidden" value="${productItem.productPlanItemSeqId}" name="Seq" />
	        				</td>
	        				<#else>
	        				<td>
		        				<input type="text" value="-" name="quantity"></input>
		        				<input type="hidden" value="${productItem.productPlanItemSeqId}" name="Seq" />
	        				</td>
	        		</#if>		
		        	</#list>
		        	<td>
		        		<label>
		        			<input type="checkbox" name="check" value="Y_${month}"/>
		        			<span class="lbl"></span>
		        		</label>
		        	</td>
                </tr>
                </#if>
		        <#assign month = month+1!>
         	</#list>
            </tbody>
        </table>
        </td>
</tr>
</table>
<input type="submit" class="btn btn-small btn-primary" value="${uiLabelMap.Edit}"></input>
</form>
</div>
</#list>

</div></div></div>
</div>        
    </div>
</div>
</div>
