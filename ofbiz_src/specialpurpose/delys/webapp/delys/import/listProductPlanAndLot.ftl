<div class="widget-box transparent">
<div class="row-fluid">
    <div class="span12 widget-container-span">
	    <div class="widget-box transparent">
	        <div class="widget-header">
	            <h4>${uiLabelMap.SelectProductLotToCreatePA}</h4>
	        </div>
	        <div class="widget-body">
	            <div class="widget-main padding-12 no-padding-left no-padding-right">
	                <div class="tab-content padding-4">
    					<table id="sale-forecast-view" class="table table-striped table-bordered table-hover">
				            <thead>
				                <tr class="sf-product">
				                	<td class="sf-months" colspan="1" rowspan="2" style="text-align:center">${uiLabelMap.ProductLotId}</td>
				                	<#list listPlanItems as planItem>
					                    <td style="text-align:center">${planItem.productId}</td>
				                	</#list>
				                	<td>${uiLabelMap.Status}</td>
				                </tr>
				            </thead>
				            <tbody>
				            <#list listLots as mapLot>
				            	<#assign listProductByLot = mapLot.listProductByLot!>
				            	<#assign lotId = mapLot.lotId!>
				            	<#assign productPlanId = mapLot.productPlanId!>
				            	<#assign orderMode = "PURCHASE_ORDER">
				            	<#assign statusId = mapLot.statusId>
				            	<#assign status = delegator.findOne("StatusItem", {"statusId" : statusId}, true)!>
				                <tr class="sf-current-year">
				                    <td colspan="1" class="sf-month" style="text-align:center">
				                    	<#if mapLot.statusId == "LOT_CREATED" || mapLot.statusId == "LOT_APPROVED">
				                    		<a href="<@ofbizUrl>editPurchaseAgreement?orderMode=${orderMode}&lotId=${lotId}&productPlanId=${productPlanId}</@ofbizUrl>">${lotId}</a>
				                    	<#else>
				                    		${lotId}
				                    	</#if>
				                    </td>
				                    <#list listProductByLot as mapProduct>
				                    	<#assign productPackingUomId = mapProduct.productPackingUomId!>
				                    	<#assign shipmentUomId = mapProduct.shipmentUomId!>
					        			<td>
					        				<input type="text" value="${mapProduct.quantity}" name="quantity"></input>
					        				<input type="hidden" value="${mapProduct.productId}" name="productId"/>
				        				</td>
						        	</#list>
						        	
						        	<td>${status.description}</td>
				                </tr>
				         	</#list>
				            </tbody>
				        </table>
					</div>
				</div>
			</div>
		</div>        
	</div>
</div>
</div>