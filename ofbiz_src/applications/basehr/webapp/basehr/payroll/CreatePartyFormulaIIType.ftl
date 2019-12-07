<div id="popupAddRow" class="hide">
    <div>${uiLabelMap.CommonAddSetting}</div> 
    <div class='form-window-container'>
    	<div class='form-window-content'>
	    	<form id="popupAddRowForm" class="">
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.formulaName}</label>
					</div>
					<div class="span7">
						<div id="codeadd">
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.AccountingInvoicePurchaseItemType}</label>
					</div>
					<div class="span7">
						<div id='invoiceItemTypeIdJQ'>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.Department}</label>
					</div>
					<div class="span7">
						<div id="jqxDropDownButton" class="">
							<div style="border: none;" id="jqxTree">
							</div>
						</div>
											
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class=" asterisk">${uiLabelMap.AvailableFromDate}</label>
					</div>
					<div class="span7">
						<div id="fromDateJQ"></div>
					</div>
				</div>
	    	</form>
    	</div>
    	<div class="form-action">
			<button id="cancelBtn" type="button" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave"><i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
    </div>
</div> 
<script type="text/javascript">
	var invoiceItems = [
		<#list invoiceItemTypeList as invoiceItemType>	
			{
				invoiceItemTypeId: "${invoiceItemType.invoiceItemTypeId}",
				description : "${StringUtil.wrapString(invoiceItemType.description?default(''))}"
			},
		</#list>
	];
	if(typeof(uiLabelMap) == "undefined"){
		uiLabelMap = {};
	}
	uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}";
	uiLabelMap.AddNewRowConfirm = "${StringUtil.wrapString(uiLabelMap.AddNewRowConfirm?default(''))}";
	uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit?default(''))}";
	uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose?default(''))}";
	uiLabelMap.NotifyDelete = "${StringUtil.wrapString(uiLabelMap.NotifyDelete?default(''))}";
	
	<#if !rootOrgId?exists>
		<#assign rootOrgId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
		<#assign orgRootParty = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", rootOrgId), false)/>
	</#if>
	if(typeof(globalVar) == "undefined"){
		globalVar = {};
	}	
	globalVar.rootOrgId = "${rootOrgId}";
	globalVar.groupName = '${orgRootParty.groupName}';
	
</script>
<script type="text/javascript" src="/hrresources/js/payroll/CreatePartyFormulaIIType.js"></script>