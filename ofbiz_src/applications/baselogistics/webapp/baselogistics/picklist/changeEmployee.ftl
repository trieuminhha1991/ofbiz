<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>
<script type="text/javascript" src="/logresources/js/picklist/changeEmployee.js?v=0.0.1"></script>

<div id="jqwWindowChangeEmployee" class="hide popup-bound">
	<div>${uiLabelMap.Employee}</div>
	<div class="form-window-content-custom">
		<div class="row-fluid margin-top10 margin-bottom10">
			<div class="span3"><label class="text-right asterisk">${uiLabelMap.DmsNhanVienSoan}</label></div>
			<div class="span9">
				<div id="checkEmployee">
					<div id="checkEmployeeGrid"></div>
				</div>
			</div>
		</div>
		<div class="row-fluid margin-top10 margin-bottom10">
			<div class="span3"><label class="text-right asterisk">${uiLabelMap.DmsNhanVienKiem}</label></div>
			<div class="span9">
				<div id="recheckEmployee">
					<div id="recheckEmployeeGrid"></div>
				</div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="btnCancel" class="btn btn-danger form-action-button pull-right" tabindex="8"><i class="icon-remove"></i>${uiLabelMap.CommonCancel}</button>
			<button id="btnSave" class="btn btn-primary form-action-button pull-right" tabindex="7"><i class="icon-ok"></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>