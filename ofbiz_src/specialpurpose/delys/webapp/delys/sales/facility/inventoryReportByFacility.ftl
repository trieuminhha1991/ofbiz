<#--
<#list listFacilities as facilityItem>
	${facilityItem}<br />
</#list>
-->

<style type="text/css">
	.nav-pagesize select {
		padding:2px !important;
		margin:0 !important;
	}
</style>

<#--<#if hasPermission>-->
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
	
	 	<div class="widget-main">
			<div class="row-fluid">
				<div class="form-horizontal basic-custom-form form-decrease-padding" style="display: block;"> 
        			<table class="table table-striped table-bordered table-hover dataTable" cellspacing='0' style="width: 100%; margin-bottom: 2%;">
			          	<thead>
			          		<tr>
			          			<td>${uiLabelMap.DAFacilityId}</td>
			          			<td>${uiLabelMap.DAProductId}</td>
			          			<td>${uiLabelMap.DAExpireDate}</td>
			          			<td>${uiLabelMap.DAQOHTotal}</td>
			          			<td>${uiLabelMap.DAATPTotal}</td>
			          			<td>${uiLabelMap.DAATQTotal}</td>
			          			<td>${uiLabelMap.DAQuantityOnOrder}</td>
			          			<#--QOH" : quantityOnHandTotal, "ATP" : availableToPromiseTotal, "AQT" : accountingQuantityTotal, "QOO" : quantityOnOrder]);-->
			          		</tr>
			          	</thead>
			          	<tbody>
			          		<#assign firstListTwo = true>
	          				<#list resultMap as facilityItem>
	          					<#assign firstListOne = true>
	          					<#assign count = 0 />
	          					<#list facilityItem.productIdDateMapValue as productItem>
	          						<#assign count = count + 1 />
	          						<#if count == 1>
	          							<#assign firstListTwo = true>
	          						</#if>
		            				<tr>
		            					<#if firstListOne>
		            						<#assign firstListOne = false>
			            					<td rowspan="${facilityItem.productIdDateMapSize?if_exists}">
			            						${facilityItem.facilityId?if_exists}
			            					</td>
		            					</#if>
		            					<#if firstListTwo>
			            					<td rowspan="${productItem.listInventoryItemByProductSize?if_exists}">
			            						 ${productItem.productIdDateMap.productId}
		            						</td>
		            						<#assign firstListTwo = false>
	            						</#if>
		            					<td>
		            						<#if productItem.productIdDateMap.expireDate?has_content>
		            							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(productItem.productIdDateMap.expireDate, "dd/MM/yyyy", locale, timeZone)!}
	            							</#if>
	            						</td>
		            					<td>${productItem.listInventoryItemByProduct.QOH?if_exists}</td>
					          			<td>${productItem.listInventoryItemByProduct.ATP?if_exists}</td>
					          			<td>${productItem.listInventoryItemByProduct.AQT?if_exists}</td>
					          			<td>${productItem.listInventoryItemByProduct.QOO?if_exists}</td>
		            				</tr>
		            				<#if count == productItem.listInventoryItemByProductSize>
	          							<#assign firstListTwo = true>
	          							<#assign count = 0 />
	          						</#if>
	            				</#list>
	            			</#list>
            			</tbody>
        			</table>
        			
    			</div>
    		</div>
    	</div>
    	<#-- Pagination -->
	</div>
  	
<#--
<#else>
  	<div><p class="alert alert-danger">${uiLabelMap.OrderViewPermissionError}</p></div>
</#if>
-->
