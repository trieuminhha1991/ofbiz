<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.BACCCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
    			<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_glAccountId}
    				</div>
    				<div class='span7'>
						<input type="text" id="glAccountId2"></input>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.FormFieldTitle_glAccountTypeId}
    				</div>
    				<div class='span7'>
						<div id="glAccountTypeId2"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.FormFieldTitle_glAccountClassId}
    				</div>
    				<div class='span7'>
						<div id="glAccountClassId2"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.FormFieldTitle_glResourceTypeId}
    				</div>
    				<div class='span7'>
    					<div id="glResourceTypeId2"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.BACCGlTaxFormAccountId}
    				</div>
    				<div class='span7'>
    					<div id="glTaxFormId2"></div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.FormFieldTitle_parentGlAccountId}
    				</div>
    				<div class='span7'>
    					<div id="parentGlAccountId2">
    						<div id="jqxgridGlAccount"></div>
    					</div>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk'>
    					${uiLabelMap.BACCAccountCode}
    				</div>
    				<div class='span7'>
    					<input type="text" id="accountCode2"></input>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right asterisk '>
    					${uiLabelMap.BACCAccountName}
    				</div>
    				<div class='span7'>
						<input type="text" id="accountName2"></input>
    				</div>
				</div>
				<div class='row-fluid margin-bottom10'>
    				<div class='span5 align-right'>
    					${uiLabelMap.Description}
    				</div>
    				<div class='span7'>
						<input type="text" id="description2"></input>
    				</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<#assign getAlGlAccount="getAll" />
<@jqOlbCoreLib hasCore=false hasValidator=true/>
<script type="text/javascript" src="/accresources/js/setting/addGlAccount.js?v=0.0.1"></script>