<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript">
	function processOrder() {
		var cform = document.cartForm;
	    var len = cform.elements;
		if(!len){
		}else{
			bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}", function(result){
				if(result){
					window.location.href = "<@ofbizUrl>processDeliveryReq</@ofbizUrl>";
				}
			});
		}
	}
</script>
<div class="widget-box transparent no-bottom-border" id="screenlet_1">
	<div class="widget-header">
		<h4>${uiLabelMap.DAStepTwo}: ${uiLabelMap.DAAddOrderIntoProposal}</h4>
		<span class="widget-toolbar none-content">
			<#if deliveryCart.size() = 0>
				<a href="javascript:void(0)" onclick="javascript:processOrder();" class="dislink" style="color:#aaa"><i class="icon-ok open-sans">${uiLabelMap.DACreateProposal}</i></a>
			<#else>
	    		<a href="javascript:void(0)" onclick="javascript:processOrder();"><i class="icon-ok open-sans">${uiLabelMap.DACreateProposal}</i></a>
	      	</#if>
	      	<#if (deliveryCart.size() > 0)>
	      		<a href="javascript:void(0)" onclick="javascript:removeSelected();"><i class="icon-remove open-sans">${uiLabelMap.DARemoveSelected}</i></a>
	      	<#else>
	      		<a href="javascript:void(0)" class="dislink" style="color:#aaa"><i class="icon-remove open-sans">${uiLabelMap.DARemoveSelected}</i></a>
	      	</#if>
	      	<a href="<@ofbizUrl>emptyDeliveryCart</@ofbizUrl>"><i class="icon-trash open-sans">${uiLabelMap.DAClearDeliveryProposal}</i></a>
		</span>
	</div>
	<div class="widget-body">
		<div id="screenlet_1_col" class="widget-body-inner">
			<div class="row-fluid">
				<form class="form-horizontal basic-custom-form" id="quickAddForm" name="quickAddForm" method="post" action="<@ofbizUrl>addDeliveryReqItem</@ofbizUrl>" style="display: block;">
					<div class="row-fluid">
						<div class="span5">
							<div class="control-group">
								<label class="control-label required" for="productQuotationId">${uiLabelMap.DASalesOrderId}</label>
								<div class="controls">
									<div class="span12">
										<@htmlTemplate.lookupField value="${requestParameters.salesOrderId?if_exists}" formName="quickAddForm" 
											name="order_id" id="order_id" fieldFormName="LookupSalesOrderHeaderAndShipInfoMini" title="${uiLabelMap.PageTitleLookupSalesOrderHeaderAndShipInfo}"/>
			       	 					<span class="tooltip">${uiLabelMap.ProductLeaveSingleProductReceiving}</span>
									</div>
								</div>
							</div>
						</div><!--.span6-->
						<div class="span4">
							<div class="control-group">
								<label class="control-label" for="itemDescription">${uiLabelMap.DADescription}</label>
								<div class="controls">
									<div class="span12">
										<input style="display:inline-block" type="text" name="itemDescription" id="itemDescription" value="${parameters.itemDescription?if_exists}">
									</div>
								</div>
							</div>
						</div>
						<div class="span3">
							<div class="control-group">
								<span class="help-inline tooltipob pull-right" style="margin:0; padding:0; margin-top:7px">
									<button class="btn btn-primary btn-mini open-sans" type="submit" style="margin-bottom:0">
										<i class="icon-ok"></i>${uiLabelMap.DAAddToList}
									</button>
								</span>
							</div>
						</div><!--.span6-->
					</div><!--.row-->
				</form>
			</div><!--.row-fluid-->
			
			<hr />
			<script language="JavaScript" type="text/javascript">
				function toggleAll() {
				    var cform = document.cartForm;
				    var len = cform.elements.length;
				    for (var i = 0; i < len; i++) {
				        var e = cform.elements[i];
				        if (e.name == "selectedItem") {
				            toggle(e);
				        }
				    }
				}
				function removeSelected() {
				    var cform = document.cartForm;
				    cform.removeSelected.value = true;
				    cform.submit();
				}
			</script>
			<div>
				<#include "showDeliveryProposalItems.ftl"/>
			</div>
			<#--
			<#include "showDeliveryProposalItemsByFacility.ftl"/>
			-->
		</div>
	</div>
</div>

<script language="JavaScript" type="text/javascript">
  document.quickAddForm.order_id.focus();
  $()
</script>		
		
		
		

		