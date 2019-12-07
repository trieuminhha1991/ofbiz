<#include "script/supReturnNewSupReturnInfoScript.ftl"/>
<form class="form-horizontal form-window-content-custom" id="newReturnSupplier" name="newReturnSupplier" method="post" action="<@ofbizUrl>showTransferConfirmPage</@ofbizUrl>">
	<div class="row-fluid">
		<div class="span11">
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid">
						<div class="span5">
							<span class="asterisk">${uiLabelMap.POSupplier}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="supplier" name="toPartyId">
								<div id="jqxgridToParty"></div>
								<input id="toPartyId" type="hidden"></input>
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<span class="asterisk">${uiLabelMap.BPOCurrencyUomId}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="currencyUomId"></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<span class="asterisk">${uiLabelMap.ExportFromFacility}</span>
						</div>
						<div class="span7">
							<div id="facilityBtn" style="width: 100%;">
								<div class="green-label" id="destinationFacilityId"></div>
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<span class="asterisk">${uiLabelMap.PurchasrOrderPO}</span>
						</div>
						<div class="span7">
							<div id="orderHeaderBtn" style="width: 100%;">
								<div class="green-label" id="orderHeaderGrid"></div>
							</div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid hide">
						<div class="span5">
							<span class="asterisk">${uiLabelMap.POEntryDate}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="entryDate"></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<span>${uiLabelMap.Description}</span>
						</div>
						<div class="span7 align-left">
							<textarea id="description" data-maxlength="250" rows="2" style="resize: vertical;margin-top:0px;" class="span12"></textarea>
						</div>
					</div>
				</div>
			</div>
		</div><!--.span11-->
	</div><!--.row-fluid-->
</form>