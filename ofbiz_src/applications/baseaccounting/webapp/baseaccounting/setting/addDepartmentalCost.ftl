<script src="/accresources/js/setting/addDepartmentalCost.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<style>
.form-window-content .row-fluid {
    min-height: 30px;
}
</style>

<div id="alterpopupWindow" class ="hide">
<div>${uiLabelMap.BACCCreateNew}</div>
<div style="overflow: hidden;">
	<div class="row-fluid form-window-content">
		<div class="span6">
			
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.InvoiceItemType}</label></div>
				<div class="span7"><div id="txtInvoiceItemType"></div></div>
			</div>
			
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.BACCOrganizationParty}</label></div>
				<div class="span7"><div id="txtOrganization"></div></div>
			</div>
			
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsFromDate}</label></div>
				<div class="span7"><div id="txtFromDate"></div></div>
			</div>
			
		</div>
		<div class="span6">
			<div class="row-fluid margin-top10"></div>
			
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.CommonDepartment}</label></div>
				<div class="span7"><div id="txtDepartment"></div></div>
			</div>
			
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right">${uiLabelMap.DmsThruDate}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span7"><div id="txtThruDate"></div></div>
			</div>
			
		</div>
	</div>
	<div class="form-action">
		<div class="row-fluid">
			<div class="span12 margin-top10">
				<button id="cancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonCancel}</button>
				<button id="save" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
</div>