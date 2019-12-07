<#-- upgrade from ../common/representative.ftl -->
<#--
<script src="/salesmtlresources/js/common/representative.js"></script>s
-->
<div class="step-pane" id="representativeInfo">
	<input type="hidden" id="wn_own_partyId" value=""/>
	<input type="hidden" id="wn_own_addressId" value=""/>
	<input type="hidden" id="wn_own_contactNumberId" value=""/>
	<input type="hidden" id="wn_own_infoStringId" value=""/>
	<div class="row-fluid form-horizontal form-window-content-custom">
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid">
					<div class="span5"><label class="text-right">${uiLabelMap.FullName}</label></div>
					<div class="span7"><input type="text" id="wn_own_fullName" tabindex="15"/></div>
				</div>
				<div class="row-fluid">
					<div class="span5"><label class="text-right">${uiLabelMap.DmsPartyBirthDate}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><div id="wn_own_birthDate" tabindex="16"></div></div>
				</div>
				<div class="row-fluid">
					<div class="span5"><label class="text-right">${uiLabelMap.DmsPartyGender}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><div id="wn_own_gender"></div></div>
				</div>
			</div>
			<div class="span6">
				<div class="row-fluid">
					<div class="span5"><label class="text-right">${uiLabelMap.PhoneNumber}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><input type="tel" id="wn_own_phoneNumber" tabindex="17"/></div>
				</div>
				<div class="row-fluid">
					<div class="span5"><label class="text-right">${uiLabelMap.EmailAddress}&nbsp;&nbsp;&nbsp;</label></div>
					<div class="span7"><input type="email" id="wn_own_emailAddress" tabindex="22"/></div>
				</div>
			</div>
		</div>
		<div class="row-fluid margin-bottom0">
			<div class="span12">
				<div class="span6">
					<div class="row-fluid margin-top10">
						<div class="span5">
							<div style="float:right" id="wn_own_isUsePrimaryAddress"><span></span></div>
						</div>
						<div class="span7" style="text-align:left">
							<b><i>${uiLabelMap.BSUsePrimaryAddress}</i></b>
						</div>
					</div>
				</div>
				<div class="span6"></div>
				
				${setContextField("themePortrait", "landscape")}
				${setContextField("prefixName", "wn_own")}
				${screens.render("component://basesales/widget/PartyScreens.xml#NewContactMechShippingAddress")}
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
    isUsePrimaryAddressOld=false;
	var listGender = [{
		value : 'M',
		label : '${StringUtil.wrapString(uiLabelMap.DmsMale)}'
	}, {
		value : 'F',
		label : '${StringUtil.wrapString(uiLabelMap.DmsFemale)}'
	}];
	
	$(function(){
		OlbOutletNewOwner.init();
	});
	
	var OlbOutletNewOwner = (function(){
		var init = function(){
			initElement();
			initEvent();
			initValidateForm();
		};
		var initElement = function(){
			jOlbUtil.input.create("#wn_own_fullName", {width: '98%'});
			jOlbUtil.input.create("#wn_own_phoneNumber", {width: '98%'});
			jOlbUtil.input.create("#wn_own_emailAddress", {width: '98%'});
			
			jOlbUtil.dateTimeInput.create("#wn_own_birthDate", {formatString: "dd/MM/yyyy", width: '100%', allowNullDate: true, value: null, showFooter: true});
			$("#wn_own_birthDate").jqxDateTimeInput('setDate', null);
			
			$("#wn_own_isUsePrimaryAddress").jqxCheckBox({width: 20, height: 20});
			
			var configGender = {
				width: '100%',
				placeHolder: uiLabelMap.BSClickToChoose,
				useUrl: false,
				url: '',
				key: 'value',
				value: 'label',
				autoDropDownHeight: true
			}
			new OlbDropDownList($("#wn_own_gender"), listGender, configGender, []);
		};
		var initEvent = function(){
			$("#wn_own_isUsePrimaryAddress").on("checked", function(event){
				disableField();
				setValue(OlbOutletNewContact.getValue());
			});
			$("#wn_own_isUsePrimaryAddress").on("unchecked", function(event){
				enableField();
			});
		};
		var initValidateForm = function(){
			var extendRules = [
                {input: "#wn_own_fullName", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                    rule: function (input, commit) {
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRex(input.val(), multiLang.BSCheckFullName);
                    }
                },
                {input: "#wn_own_phoneNumber", message: multiLang.BSPhoneIsNotValid, action: "keyup, blur",
                    rule: function (input, commit) {
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRex(input.val(), multiLang.BSCheckPhone);
                    }
                },
                {input: "#wn_own_emailAddress", message: multiLang.BSEmailIsNotValid, action: "keyup, blur",
                    rule: function (input, commit) {
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRex(input.val(), multiLang.BSCheckEmail);
                    }
                },
                {input: "#wn_own_toName", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                    rule: function (input, commit) {
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRex(input.val(), multiLang.BSCheckFullName);
                    }
                },
                {input: "#wn_own_attnName", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                    rule: function (input, commit) {
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRex(input.val(), multiLang.BSCheckSpecialCharacter);
                    }
                },
                {input: "#wn_own_address1", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                    rule: function (input, commit) {
                        return checkRex(input.val(), multiLang.BSCheckAddress);
                    }
                },
                {input: "#wn_own_postalCode", message: multiLang.BSPostalCodeIsNotValid, action: "keyup, blur",
                    rule: function (input, commit) {
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRex(input.val(), multiLang.BSCheckPostalCode);
                    }
                },
            ];
			var mapRules = [
				{input: '#wn_own_countryGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_own_stateProvinceGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_own_countyGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_own_wardGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_own_address1', type: 'validInputNotNull'},
			];
			validatorVAL = new OlbValidator($("#representativeInfo"), mapRules, extendRules, {position: "bottom", scroll: true});
		};
		var getValue = function() {
			var value = {
				addressId: $("#wn_own_addressId").val(),
				infoStringId: $("#wn_own_infoStringId").val(),
				contactNumberId: $("#wn_own_contactNumberId").val(),
				partyId: $("#wn_own_partyId").val(),
				partyFullName: $("#wn_own_fullName").val(),
				gender: $("#wn_own_gender").jqxDropDownList("val"),
				birthDate: $("#wn_own_birthDate").jqxDateTimeInput('getDate') ? $("#wn_own_birthDate").jqxDateTimeInput('getDate').getTime() : null,
				contactNumber: $("#wn_own_phoneNumber").val(),
				infoString: $("#wn_own_emailAddress").val(),
                isUsePrimaryAddressOld: isUsePrimaryAddressOld,
				isUsePrimaryAddress: $("#wn_own_isUsePrimaryAddress").val(),
				countryGeoId: $("#wn_own_countryGeoId").jqxComboBox("val"),
				stateProvinceGeoId: $("#wn_own_stateProvinceGeoId").jqxComboBox("val"),
				districtGeoId: $("#wn_own_countyGeoId").jqxComboBox("val"),
				wardGeoId: $("#wn_own_wardGeoId").jqxComboBox("val"),
				address1: $("#wn_own_address1").val(),
				toName: $("#wn_own_toName").val(),
				attnName: $("#wn_own_attnName").val(),
				postalCode: $("#wn_own_postalCode").val(),
			};
			<#--
			if (!_.isEmpty(extendId)) {
				value = _.extend(value, extendId);
			}
			-->
			return {representative: JSON.stringify(value)};
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				if (!_.isEmpty(extendId) && !_.isEmpty(extendId.addressId)) {
					if (extendId.addressId == data.addressId) {
						$("#wn_own_isUsePrimaryAddress").jqxCheckBox('check');
                        isUsePrimaryAddressOld=true
					}
				}
				$("#wn_own_contactNumberId").val(data.contactNumberId);
				$("#wn_own_infoStringId").val(data.infoStringId);
				$("#wn_own_addressId").val(data.addressId);
				$("#wn_own_partyId").val(data.partyId);
				$("#wn_own_countryGeoId").jqxComboBox("val", data.countryGeoId);
				$("#wn_own_stateProvinceGeoId").jqxComboBox("val", data.stateProvinceGeoId);
				$("#wn_own_countyGeoId").jqxComboBox("val", data.districtGeoId);
				$("#wn_own_wardGeoId").jqxComboBox("val", data.wardGeoId);
				$("#wn_own_address1").val(data.address1);
				$("#wn_own_toName").val(data.toName);
				$("#wn_own_attnName").val(data.attnName);
				$("#wn_own_postalCode").val(data.postalCode);
				
				$("#wn_own_fullName").val(data.partyFullName);
				$("#wn_own_gender").jqxDropDownList("val", data.gender);
				$("#wn_own_birthDate").jqxDateTimeInput('setDate', data.birthDate?new Date(data.birthDate):null);
				$("#wn_own_phoneNumber").val(data.contactNumber);
				$("#wn_own_emailAddress").val(data.infoString);
				
				<#--
				extendId.contactNumberId = data.contactNumberId;
				extendId.infoStringId = data.infoStringId;
				extendId.addressId = data.addressId;
				extendId.partyId = data.partyId;-->
			}
		};
		var getValueDesc = function(){
			var birthDate = $("#wn_own_birthDate").jqxDateTimeInput('getDate') ? $("#wn_own_birthDate").jqxDateTimeInput('getDate').getTime() : "";
			var birthDateDesc = birthDate ? (new Date(birthDate)).toTimeOlbius() : "";
			
			var value = {
				partyFullName: $("#wn_own_fullName").val(),
				gender: $("#wn_own_gender").jqxDropDownList("val"),
				birthDate: birthDate,
				contactNumber: $("#wn_own_phoneNumber").val(),
				infoString: $("#wn_own_emailAddress").val(),
				
				isUsePrimaryAddress: $("#wn_own_isUsePrimaryAddress").val(),
				birthDateDesc: birthDateDesc,
			};
			
			// get address full name
			var toName = $("#wn_own_toName").val();
			var attnName = $("#wn_own_attnName").val();
			var postalCode = $("#wn_own_postalCode").val();
			
			var addressFullName = toName ? toName + " " : "";
			if (!_.isEmpty(attnName)) addressFullName += "(" + attnName + ") ";
			else if (addressFullName != "") addressFullName += ", ";
			addressFullName += $("#wn_own_address1").val();
			
			var wardGeoItem = $("#wn_own_wardGeoId").jqxComboBox("getSelectedItem");
			var wardGeoStr = typeof("wardGeoItem") != "undefined" ? wardGeoItem.label : "";
			if (OlbCore.isNotEmpty(wardGeoStr)) addressFullName += ", " + wardGeoStr;
			var countyGeoItem = $("#wn_own_countyGeoId").jqxComboBox("getSelectedItem");
			var countyGeoStr = typeof("countyGeoItem") != "undefined" ? countyGeoItem.label : "";
			if (OlbCore.isNotEmpty(countyGeoStr)) addressFullName += ", " + countyGeoStr;
			var stateProvinceGeoItem = $("#wn_own_stateProvinceGeoId").jqxComboBox("getSelectedItem");
			var stateProvinceGeoStr = typeof("stateProvinceGeoItem") != "undefined" ? stateProvinceGeoItem.label : "";
			if (OlbCore.isNotEmpty(stateProvinceGeoStr)) addressFullName += ", " + stateProvinceGeoStr;
			var countryGeoItem = $("#wn_own_countryGeoId").jqxComboBox("getSelectedItem");
			var countryGeoStr = typeof("countryGeoItem") != "undefined" ? countryGeoItem.label : "";
			if (OlbCore.isNotEmpty(countryGeoStr)) addressFullName += ", " + countryGeoStr;
			
			value.addressFullName = addressFullName;
			
			// gender
			var gender = $("#wn_own_gender").jqxDropDownList("val");
			var genderDesc = "";
			if (!_.isEmpty(gender)) {
				for (var i = 0; i < listGender.length; i++) {
					if (gender == listGender[i].value) {
						genderDesc = listGender[i].label;
						break;
					}
				}
			}
			value.genderDesc = genderDesc;
			
			return value;
		};
		
		var disableField = function() {
			$("#wn_own_countryGeoId").jqxComboBox("disabled", true);
			$("#wn_own_stateProvinceGeoId").jqxComboBox("disabled", true);
			$("#wn_own_countyGeoId").jqxComboBox("disabled", true);
			$("#wn_own_wardGeoId").jqxComboBox("disabled", true);
			$("#wn_own_address1").jqxInput("disabled", true);
			$("#wn_own_toName").jqxInput("disabled", true);
			$("#wn_own_attnName").jqxInput("disabled", true);
			$("#wn_own_postalCode").jqxInput("disabled", true);
		};
		var enableField = function() {
			$("#wn_own_countryGeoId").jqxComboBox("disabled", false);
			$("#wn_own_stateProvinceGeoId").jqxComboBox("disabled", false);
			$("#wn_own_countyGeoId").jqxComboBox("disabled", false);
			$("#wn_own_wardGeoId").jqxComboBox("disabled", false);
			$("#wn_own_address1").jqxInput("disabled", false);
			$("#wn_own_toName").jqxInput("disabled", false);
			$("#wn_own_attnName").jqxInput("disabled", false);
			$("#wn_own_postalCode").jqxInput("disabled", false);
		};
		
		return {
			init: init,
			setValue: setValue,
			getValue: getValue,
			getValueDesc: getValueDesc,
		};
	}());
	
	
</script>