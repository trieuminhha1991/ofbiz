<div class="widget-box" id="facilityReport" style="border-bottom: none">
	  	<div class="widget-header widget-header-small header-color-blue2">
		  	<h5>${uiLabelMap.PageTitleFacilitiesReport}</h5>
	  	</div>
		 <div class="widget-body" style="border-bottom: none;">
		 <#if inventoryItemTotals?has_content>
				<table width="100%" border="0" cellspacing="0" cellpadding="0" class="table table-striped table-bordered dataTable table-hover">
					<tr>
						<th>${uiLabelMap.ProductProductId}</th>
						<th>${uiLabelMap.ProductQoh}</th>
						<th>${uiLabelMap.ProductAtp}</th>
						<th>${uiLabelMap.ProductCostPrice}</th>
						<th>${uiLabelMap.ProductRetailPrice}</th>
						<th>${uiLabelMap.CommonTotal} ${uiLabelMap.ProductCostPrice}</th>
						<th>${uiLabelMap.CommonTotal} ${uiLabelMap.ProductRetailPrice}</th>
					</tr>
					<#list inventoryItemTotals as inventoryItem>
					<tr>
						<td>${inventoryItem.productId?if_exists}</td>
						<td>${inventoryItem.quantityOnHand?if_exists}</td>
						<td>${inventoryItem.availableToPromise?if_exists}</td>
						<td>${inventoryItem.costPrice?if_exists}</td>
						<td>${inventoryItem.retailPrice?if_exists}</td>
						<td>${inventoryItem.totalCostPrice?if_exists}</td>
						<td>${inventoryItem.totalRetailPrice?if_exists}</td>
					</tr>
					</#list>
					<#else>
						<p class="alert alert-info">No record found</p>
					</#if>
				</table>
		</div>
</div>	 
