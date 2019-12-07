<#include "script/invoiceShippingTripAccmInfoScript.ftl">
<form class="form-horizontal form-window-content-custom" id="initInvoiceEntry" name="initInvoiceEntry">
	<div class="row-fluid">
		<div class="span12">
			<div class="span5">
			    <div class='row-fluid'>
                	<div class='span5'>
                		<label class='required'>${uiLabelMap.BACCInvoiceType}</label>
                	</div>
                	<div class="span7">
                		<div id="invoiceTypeId"></div>
                	</div>
                </div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCInvoiceDate}</label>
					</div>
					<div class="span7">
						<div id="invoiceDate"></div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>${uiLabelMap.BACCInvoiceDueDate}</label>
					</div>
					<div class="span7">
						<div id="dueDate"></div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span5'>
						<label>${uiLabelMap.BACCDescription}</label>
					</div>
					<div class="span7">
						<div class="row-fluid">
							<textarea id="description" style="width: 91%; float: left;"></textarea>
						</div>
					</div>
				</div>
			</div>
			<div class="span7">
				<div class='row-fluid'>
					<div class='span5'>
						<label class='required'>Shipper</label>
					</div>
					<div class="span7">
						<div id="shipperIdInfo"></div>
					</div>
				</div>

				<div class='row-fluid'>
                	<div class='span5'>
                		<label class='required'>${uiLabelMap.BACCInvoiceToParty}</label>
                	</div>
                	<div class="span7">
                		<div id="partyIdInfo">
                		</div>
                	</div>
                </div>
			</div>
		</div>
		<div class="span12" style="margin-left: 0px;">
		    <div id="jqxGridInvoiceAccmInfo"></div>
		</div>
	</div>
</form>