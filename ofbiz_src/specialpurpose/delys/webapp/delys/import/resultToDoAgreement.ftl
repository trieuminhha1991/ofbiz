<#if listLotToDoAgreement?exists && productId?exists >
    <#assign orderMode = "PURCHASE_ORDER" />
	<#assign productPlanId= parameters.productPlanHeader />
	<table class="table table-striped table-bordered table-hover">
		<thead>
			<tr class="sf-product">
				<td>Chia HD</td>
				<#list productId as pro>
					<td>
						${pro.productId}
					</td>
				</#list>
			</tr>
		</thead>
		<tbody>
			<#list listLotToDoAgreement as lot>
				<tr>
				<td><a href="<@ofbizUrl>editPurchaseAgreement?orderMode=${orderMode}&lotId=${lot.lot}&productPlanId=${productPlanId}</@ofbizUrl>">${lot.lot}</a></td>
				<#list productId as pro>
					<#assign value = 0 />
					<#list lot.listProductLot as lotItem>
						<#if lotItem.productId == pro.productId>
							<#assign value = lotItem.lotQuantity/>	
						</#if>
					</#list>
					<td>${value}</td>
				</#list>
				</tr>
			</#list>
		</tbody>
	
	</table>


</#if>