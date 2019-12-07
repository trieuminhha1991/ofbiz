<#include "script/supReturnEditSupReturnInfoScript.ftl"/>
<form class="form-horizontal form-window-content-custom" id="editReturnSupplier" name="newReturnSupplier" method="post" action="">
	<div class="row-fluid">
		<div class="span11">
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid">
						<div class="span5">
							<span class="asterisk">${uiLabelMap.POSupplier}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="partyId" name="partyId">
							</div>
						</div>
					</div>
					<div class="row-fluid hide">
						<div class="span5">
							<span class="asterisk">${uiLabelMap.BPOCurrencyUomId}</span>
						</div>
						<div class="span7">
							<div class="green-label" id="currencyUomId"></div>
						</div>
					</div>
					<div class="row-fluid">
						<div class="span5">
							<span class="asterisk">${uiLabelMap.Facility}</span>
						</div>
						<div class="span7">
							<div id="facility">
								<div id="jqxGridFacility"></div>
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