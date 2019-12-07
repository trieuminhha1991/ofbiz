<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<#assign partyIdFrom = partyOrg.partyId />
<#assign partyNameFrom = partyOrg.groupName?if_exists />
<#assign agreementId = parameters.agreementId?if_exists />
<#if parameters.agreementId?exists>
	<#assign agreement = delegator.findOne("Agreement", Static["org.ofbiz.base.util.UtilMisc"].toMap("agreementId", agreementId), false) />
	<#assign partyGroup = delegator.findOne("PartyGroup", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", agreement.partyIdTo), false) />
	<script>
		var agreementDate="";
		var fromDate="";
		var thruDate="";
		<#if agreement.agreementDate?exists>
			<#assign agreementDate = agreement.agreementDate.getTime() />
			agreementDate = ${agreementDate};
		</#if>
		<#if agreement.fromDate?exists>
			<#assign fromDate = agreement.fromDate.getTime() />
			var fromDate = ${fromDate};
		</#if>
		<#if agreement.thruDate?exists>
			<#assign thruDate = agreement.thruDate.getTime() />
			var thruDate = ${thruDate};
		</#if>
	</script>
</#if>
<div id="alterpopupWindow" <#if !parameters.agreementId?exists> style="display: none;"</#if>>
<#if !parameters.agreementId?exists> <div>${uiLabelMap.CreateNewPurchaseAgreement}</div> </#if>
	<div <#if !parameters.agreementId?exists> style="overflow: hidden;" </#if>>
		<input type="hidden" name="statusIdAdd" id="statusIdAdd" value="AGREEMENT_CREATED"/>
		<div style="">
			<form id="formAdd">
				<div class="row-fluid" style="margin-top:10px;">
					<div class="span6">
						<div class="span4 align-right asterisk">
							${uiLabelMap.DAPartyFrom}
						</div>
						<div class="span8" >
							<div id="" data-role="">
								<a>${StringUtil.wrapString(partyNameFrom)}</a>
							</div>
							<input type="hidden" id="partyIdFrom" value="${partyIdFrom}"/>
						</div>
					</div>
					<div class="span6">
						<div class="span4 align-right asterisk">
							${uiLabelMap.DAPartyTo}
						</div>
						<div class="span8" >
						<#if !parameters.agreementId?exists>
							<div id="partyIdToAdd" data-role="roleTypeIdToAdd">
									<div id="jqxPartyToGrid"></div>
							</div>
							<#else>
							<div>
								<a>${StringUtil.wrapString(partyGroup.groupName?if_exists)}</a>
							</div>
						</#if>
							<input type="hidden" id="partyIdTo"/>
							<input type="hidden" id="agreementTypeIdAdd" value="PURCHASE_AGREEMENT" />
						</div>
					</div>
				</div>
				
				<div class="row-fluid" style="margin-top:10px;">
					<div class="span6">
						<div class="span4 align-right asterisk">
							${uiLabelMap.DARoleTypeIdFrom}
						</div>
						<div class="span8">
							<div id="">
								<a>${StringUtil.wrapString(uiLabelMap.RolePurchase)}</a>
								<input type="hidden" value="OWNER" id="roleTypeIdFrom"/>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="span4 align-right asterisk">
							${uiLabelMap.DARoleTypeIdTo}
						</div>
						<div class="span8">
							<div id="roleTypeIdToAdd">
								<a>${StringUtil.wrapString(uiLabelMap.RoleSeller)}</a>
								<input type="hidden" value="SUPPLIER" id="roleTypeIdTo"/>
							</div>
						</div>
					</div>
				</div>
				
				<div class="row-fluid" style="margin-top:10px;">
					<div class="span6">
						<div class="span4 align-right asterisk">
							${uiLabelMap.DAFromDate}
						</div>
						<div class="span8">
							<div id="fromDateAdd"></div>
						</div>
					</div>
					<div class="span6">
						<div class="span4 align-right ">
							${uiLabelMap.DAThruDate}
						</div>
						<div class="span8">
							<div id="thruDateAdd"></div>
						</div>
					</div>
				</div>
				
				<div class="row-fluid" style="margin-top:10px;">
					<div class="span6">
						<div class="span4 align-right asterisk">
							${uiLabelMap.DAAgreementDate}
						</div>
						<div class="span8">
							<div id="agreementDateAdd"></div>
						</div>
					</div>
					<div class="span6">
						<div class="span4 align-right ">
							${uiLabelMap.DADescription}
						</div>
						<div class="span8">
							<input id="descriptionAdd" value="${(agreement.description)?if_exists}"/>
						</div>
					</div>
				</div>
			</form>
		</div>
		<hr style="margin: 5px !important;"/>
		<#if !parameters.agreementId?exists>
			<div class="form-action">
				<div class="row-fluid">
					<div class="span12 margin-top10">
						<button id="cancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.Cancel}</button>
						<button id="saveAndContinue" class="btn btn-success form-action-button pull-right"><i class="fa-plus"></i> ${uiLabelMap.SaveAndContinue}</button>
						<button id="save" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.Save}</button>
					</div>
				</div>
			</div>
			<#else>
			<div class="row-fluid">
				<div class="span12 margin-top10">
					<button id="save" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</#if>
		
	</div>
</div>

<!--window edit text area-->
<div class="hide" id="textWindow">
	<div>${uiLabelMap.EditTextAdd}</div>
	<div class="row-fluid">
		<div class="span12 form-window-content">
			<div id="editText"></div>		
		</div>
		<div class="form-action">
			<button id="cancelText"class="btn btn-danger form-action-button pull-right"><i class="fa-cancel"></i> ${uiLabelMap.CommonCancel}</button>
			<button id="addText" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonAdd}</button>
		</div>
	</div>
</div>
<#if !parameters.agreementId?exists>
	<script src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
	<script type="text/javascript" src="/poresources/js/agreement/popupAddAgreement.js"></script>
<#else>
	<@jqGridMinimumLib/>
	<script type="text/javascript" src="/poresources/js/agreement/editAgreement.js"></script>
</#if>

<script>
	if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
	uiLabelMap.PartyPartyId = "${StringUtil.wrapString(uiLabelMap.PartyPartyId)}";
	uiLabelMap.PartyGroupName = "${StringUtil.wrapString(uiLabelMap.PartyGroupName)}";
	uiLabelMap.CommonRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
</script>