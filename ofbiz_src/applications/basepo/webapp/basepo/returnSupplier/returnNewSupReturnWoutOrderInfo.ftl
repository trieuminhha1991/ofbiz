<#include "script/returnNewSupReturnWoutOrderInfoScript.ftl"/>
<form class="form-horizontal form-window-content-custom" id="newReturnSupplierWoutOrder" name="newReturnSupplierWoutOrder" method="post" action="<@ofbizUrl>showTransferConfirmPage</@ofbizUrl>">
	<div class="row-fluid">
		<div class="span11">
			<div class="row-fluid">
				<div class="span6">
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.BSSupplierId}</label>
						</div>
						<div class="span7">
							<div id="supplierId" name ="toPartyId">
								<div id="supplierGrid"></div>
								<input id="toPartyId" type="hidden"></input>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label class="required">${uiLabelMap.ExportFromFacility}</label>
						</div>
						<div class="span7">
							<div id="destinationFacilityId">
								<div id="destinationFacilityGrid"></div>
							</div>
				   		</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid'>
						<div class='span3'>
							<label>${uiLabelMap.BSCurrencyUomId}</label>
						</div>
						<div class="span9">
							<div id="currencyUomId" name = "currencyUomIdDes">
								<div id="currencyUomGrid"></div>
								<input id="currencyUomIdDes" type="hidden"></input>
							</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span3'>
							<label>${uiLabelMap.BSDescription}</label>
						</div>
						<div class="span9">
							<textarea id="description" name="description" class="autosize-transition span12" style="resize: vertical; margin-top:0;margin-bottom:0"></textarea>
				   		</div>
					</div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
</form>