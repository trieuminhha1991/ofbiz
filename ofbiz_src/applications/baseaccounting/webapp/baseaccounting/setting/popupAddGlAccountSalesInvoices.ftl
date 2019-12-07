<div id="alterpopupWindow" class ='hide'>
    <div>${uiLabelMap.BACCCreateNew}</div>
    <div style="overflow: hidden;">
    	<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_invoiceItemTypeId}
    				</div>
    				<div class='span7'>
						<div id="invoiceItemTypeId">
 						</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_overrideGlAccountId}
    				</div>
    				<div class='span7'>
						<div id="glAccountId">
							<div id="jqxgridGlAccount"></div>
 						</div>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
    </div>
</div>
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script src="/accresources/js/setting/addGlAccountSalesInvoices.js"></script>