<style type="text/css">
	table.table-padding48 td {
		padding: 4px 8px;
	}
</style>
<#if listProduct?exists>
	<input type="hidden" id="productSizeNumber" value="${listProduct?size}"/>
	<table class="table table-bordered table-padding48">
		<tr>
			<td style="width:10px"><b>${uiLabelMap.DANo}</b></td>
			<td class="center"><b>${uiLabelMap.DAProductId}</b></td>
			<td class="center"><b>${uiLabelMap.DAInternalName}</b></td>
		</tr>
		<#list listProduct as productItem>
			<tr>
				<td>${productItem_index + 1}</td>
				<td>${productItem.productId?if_exists}</td>
				<td>${productItem.internalName?if_exists}</td>
			</tr>
		</#list>
	</table>
<#else>
	<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
</#if>