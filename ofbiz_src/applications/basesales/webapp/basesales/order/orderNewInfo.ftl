<#if !enableProductStore?exists><#assign enableProductStore = true/></#if>
<#if !isPurchaseSelfie?exists><#assign isPurchaseSelfie = false/></#if>
<style>
	#horizontalScrollBarcustomerGrid {
		visibility: inherit !important;
	}
</style>
<form class="form-horizontal form-window-content-custom" id="initOrderEntry" name="initOrderEntry" method="post" action="<@ofbizUrl>initOrderEntrySales</@ofbizUrl>">
	<div class="row-fluid">
		<div class="span12">
			<#if !isPurchaseSelfie>
			<div class='row-fluid'>
				<div class='span5'>
					<label>${uiLabelMap.BSOrderId}</label>
				</div>
				<div class="span7">
					<input class="span12" type="text" id="orderId" name="orderId" value=""/>
		   		</div>
			</div>
			</#if>
			<div class='row-fluid'>
				<div class='span5'>
					<label>${uiLabelMap.BSCustomerPOId}</label>
				</div>
				<div class="span7">
					<input class="span12" type="text" id="externalId" name="externalId" value=""/>
		   		</div>
			</div>
			<#--<div class='row-fluid'>
				<div class='span5'>
					<label>${uiLabelMap.BSOrderName}</label>
				</div>
				<div class="span7">
					<input class="span12" type="text" id="orderName" name="orderName" value=""/>
		   		</div>
			</div>-->
			<div class='row-fluid'<#if !enableProductStore>style="display:none"</#if>>
				<div class='span5'>
					<label class="required">${uiLabelMap.BSSalesChannel}</label>
				</div>
				<div class="span7">
					<div id="productStoreId"></div>
		   		</div>
			</div>
			<div class='row-fluid' <#if isPurchaseSelfie>style="display:none"</#if>>
				<div class='span5'>
					<label class="required">${uiLabelMap.BSCustomerId}</label>
				</div>
				<div class="span7">
					<#if isPurchaseSelfie?exists && isPurchaseSelfie>
					<input type="hidden" id="customerId" value=""/>
					<input type="text" id="customerCode" value=""/>
					<#else>
					<div id="customerId">
						<div id="customerGrid"></div>
					</div>
					</#if>
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label>${uiLabelMap.BSAgreementId}</label>
				</div>
				<div class="span7">
					<div id="agreementId">
						<div id="agreementGrid"></div>
					</div>
		   		</div>
			</div>
			<div class='row-fluid'>
				<div class='span5'>
					<label class="required">${uiLabelMap.BSDesiredDeliveryDate}</label>
				</div>
				<div class="span7">
					<div id="desiredDeliveryDate"></div>
		   		</div>
			</div>
			<div class='row-fluid no-margin'>
				<div class='span5'>
					<label>${uiLabelMap.BSShipDateAmong}</label>
				</div>  
				<div class="span7">
					<div class="row-fluid">
						<div class="span6">
							<div id="shipAfterDate"></div>
						</div>
						<div class="span6">
							<div id="shipBeforeDate"></div>
						</div>
					</div>
		   		</div>
			</div>
			<#if !isPurchaseSelfie?exists || !isPurchaseSelfie>
			<div class='row-fluid'>
				<div class='span5'>
					<label>${uiLabelMap.BSPriority}</label>
				</div>
				<div class="span7">
					<div id="orderPriorityId"></div>
		   		</div>
			</div>
			</#if>
		</div><!--.span12-->
	</div><!--.row-fluid-->
</form>
<#include 'script/orderNewInfoScript.ftl'/>