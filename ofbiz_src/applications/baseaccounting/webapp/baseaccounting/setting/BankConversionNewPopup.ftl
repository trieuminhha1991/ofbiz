<div id="alterpopupWindow" style="display:none;">
    <div>${uiLabelMap.accCreateNew}</div>
    <div style="overflow: hidden;">
		<div class='row-fluid form-window-content'>
			<form id="formAdd">
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
					<label class="required">${uiLabelMap.BACCBankId}</label>
					</div>
					<div class='span7'>
						<input id="bankId"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 align-right'>
						<label class="required">${uiLabelMap.BACCBankName}</label>
					</div>
					<div class='span7'>
						<input id="bankName" />
					</div>
				</div>
				<div class="row-fluid  margin-bottom10">
					<div class='span5 align-right'>
						<label class="required">${uiLabelMap.BACCBankAddress}</label>
					</div>
					<div class="span7">
						<#--<div id="shippingContactMechId">
							<div id="shippingContactMechGrid"></div>
						</div>-->
						<input id="bankAddress" />
						<a class="container-add-plus" href="javascript:action.createAddress();" id="addNewShippingAddress" class="add-quickly"><i class="icon-plus open-sans"></i></a>
			   		</div>
		   		</div>
			</form>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="cancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="saveAndContinue" class='btn btn-success form-action-button pull-right'><i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
					<button id="save" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Save}</button>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true/>
${screens.render("component://basesales/widget/PartyScreens.xml#PopupNewContactMechShippingAddress")}
<script>
	<#assign userLoginId = userLogin.userLoginId?if_exists/>
	<#if userLoginId?exists>
		<#assign defaultUserLogin = delegator.findOne("UserLogin", {"userLoginId" : userLoginId}, false)!/>
		<#if defaultUserLogin?exists><#assign defaultPartyId = defaultUserLogin.partyId/></#if>
	</#if>
	var defaultPartyId = <#if defaultPartyId?exists>'${defaultPartyId}'<#else>null</#if>;
	var uiLabelMap = {};
	uiLabelMap.PleaseChooseAcc = "${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}";
	uiLabelMap.FieldRequiredAccounting = "${StringUtil.wrapString(uiLabelMap.BACCFieldRequiredAccounting?default(''))}";
	uiLabelMap.BACCFieldContainsSpecialChar = "${StringUtil.wrapString(uiLabelMap.BACCFieldContainsSpecialChar?default(''))}";
	uiLabelMap.BSContactMechId =  "${StringUtil.wrapString(uiLabelMap.BSContactMechId?default(''))}";
	uiLabelMap.BSReceiverName =  "${StringUtil.wrapString(uiLabelMap.BSReceiverName?default(''))}";
	uiLabelMap.BSOtherInfo =  "${StringUtil.wrapString(uiLabelMap.BSOtherInfo?default(''))}";
	uiLabelMap.BSAddress =  "${StringUtil.wrapString(uiLabelMap.BSAddress?default(''))}";
	uiLabelMap.BSWard =  "${StringUtil.wrapString(uiLabelMap.BSWard?default(''))}";
	uiLabelMap.BSCounty =  "${StringUtil.wrapString(uiLabelMap.BSCounty?default(''))}";
	uiLabelMap.BSStateProvince =  "${StringUtil.wrapString(uiLabelMap.BSStateProvince?default(''))}";
	uiLabelMap.BSCountry =  "${StringUtil.wrapString(uiLabelMap.BSCountry?default(''))}";
	uiLabelMap.BACCNewAddress = "${StringUtil.wrapString(uiLabelMap.BACCNewAddress?default(''))}";
	uiLabelMap.wgaddsuccess =  "${StringUtil.wrapString(uiLabelMap.wgaddsuccess?default(''))}";
	uiLabelMap.BACCBankExisted =  "${StringUtil.wrapString(uiLabelMap.BACCBankExisted?default(''))}";
</script>
<script src="/accresources/js/setting/addBankConversion.js"></script>