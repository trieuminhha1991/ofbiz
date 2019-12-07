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
	                    <#list listSalesForecastAndItems as salesForecastPartyItem>
							<li <#if salesForecastPartyItem_index == 0> class="active"</#if> style="margin-top:0; margin-bottom:0; padding-top:0 !important; padding-bottom:0 !important">
								<a data-toggle="tab" href="#${salesForecastPartyItem_index}-tab">${salesForecastPartyItem.internalPartyIds}</a>
							</li>
						</#list>
	                </ul>
	            </div>
	        </div>
	        <div class="widget-body">
	            <div class="widget-main padding-12 no-padding-left no-padding-right">
	                <div class="tab-content padding-4">

<#list listSalesForecastAndItems as salesForecastPartyItem>
<#assign kt=0/>
<#assign mm = parameters.monthMapSeq/>

	<div id="${salesForecastPartyItem_index}-tab" class="tab-pane<#if salesForecastPartyItem_index == 0> active</#if>">
	<form method="post" action="<@ofbizUrl>CreateImportPlanItem</@ofbizUrl>" name="create">
	<#list mm.entrySet() as item>
		<input name="monthSeq" type="hidden" value="${item.value}"></input>
	</#list>
	<input type="hidden" value="${customTimeParent.periodName?if_exists}" name="year"></input>
	<input type="hidden" name="yearSeq" value="${parameters.yearSeq}"></input>
	<input type="hidden" name="fromDate" value="${parameters.fromDate}" />
	<input type="hidden" name="thruDate" value="${parameters.thruDate}"/>
	<input type="hidden" name="organizationPartyId" value="${parameters.organizationPartyId}"/>
	<input type="hidden" name="internalPartyId" value="${salesForecastPartyItem.internalPartyIds}"/>
<table>
<tr>
<td>
        <table id="sale-forecast-view" class="table table-striped table-bordered table-hover">
            <thead>
                <tr class="sf-product">
                	<td class="sf-months" colspan="1" rowspan="2" style="text-align:center">THANG</td>
                	<#list listProduct as productItem>
                		<#assign categoryId = productItem.categoryId!>
                		<#assign productList = productItem.productList!>
	                    <td <#if categoryId?exists && productList?exists>colspan="${productList?size}" </#if>rowspan="1" style="text-align:center">
	                    	${categoryId?if_exists}
	                    	<#--
	                    	<#if categoryId?exists>
	                    		<#assign categoryHeader = delegator.findOne("ProductCategory", {"productCategoryId" : categoryId}, false)!/>
	                    		${categoryHeader.categoryName?default(categoryHeader.categoryId)}
	                    	</#if>
	                    	-->
                    	</td>
                    	<#assign prs= productList?size />
                    	<#assign kt=kt+prs/> 
                	</#list>
                	<#assign kt=kt+prs/>
                	<td class="sf-value" rowspan="2" style="text-align:center">VALUE (1,000d)</td>
                </tr>
                <tr class="sf-product-child">
                	<#list listProduct as productItem>
                		<#assign categoryId = productItem.categoryId!>
                		<#assign productList = productItem.productList!>
                		<#if productList?exists>
                		<#list productList as item>
                			<td style="text-align:center">${item.internalName}</td>
                		</#list>
                		</#if>
                	</#list>
                </tr>
            </thead>
            <tbody>
            <#list salesForecastPartyItem.forecastAndItems as forecastAndItem>
          <!--  <tr>
            <td colspan="${kt}">
                    <form method="post" action="<@ofbizUrl>CreateImportPlanItem</@ofbizUrl>" name="create">
                    <table> -->
            	<#assign forecastItems = forecastAndItem.forecastItems>
            	<#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : forecastAndItem.forecast.customTimePeriodId}, false)!>
                <tr class="sf-current-year <#if "SALES_QUARTER" == customTimePeriod.periodTypeId> quarter-row<#elseif "SALES_YEAR" == customTimePeriod.periodTypeId> sf-total-row</#if>">
                   	
                    <td colspan="1" class="sf-month <#if "SALES_QUARTER" == customTimePeriod.periodTypeId> quarter<#elseif "SALES_YEAR" == customTimePeriod.periodTypeId> sf-total</#if>" style="text-align:center">
                    	${customTimePeriod.periodName?if_exists}
                    </td>
                    <#list listProduct as productItem>
		        		<#assign categoryId = productItem.categoryId!>
		        		<#assign productList = productItem.productList!>
		        		<#if productList?exists>
		        		<#list productList as item>
		        			<td>
		        				<#assign printed = false>
		        				<#list forecastItems as forecastItem>
		        				<#if (forecastItem.productId?exists) && (item.productId == forecastItem.productId)>
		        					<#if forecastItem.quantity?exists>
		        						<#if "SALES_QUARTER" == customTimePeriod.periodTypeId || "SALES_YEAR" == customTimePeriod.periodTypeId>
		        							${forecastItem.quantity?string(",##0")}
		        							<#else>
		        							<input type="text" name="${forecastItem.productId}" value="${forecastItem.quantity?string(",##0")}"></input>
		        							
		        						</#if>
		        					<!--	${forecastItem.quantity?string(",##0")} -->
		        					</#if>
		        					<#assign printed = true>
		        					<#break/>
		        				<#else>
		        				</#if>
		        				</#list>
		        				<#if !printed>
		        					<input type="text" value="0" name="${item.productId}"></input>
		        				</#if>
	        				</td>
		        		</#list>
		        		</#if>
		        	</#list>
		        	<td>#</td>
                </tr>
		        <!--	</table>
		        	</form>
		        	</td>
		        	</tr> -->
         	</#list>
            </tbody>
        </table>
        </td>
</tr>
</table>
<input type="submit" class="btn btn-small btn-primary" value="${uiLabelMap.createImportPlan}"></input>
</form>
</div>
</#list>

</div></div></div>
</div>        
    </div>
</div>
</div>
