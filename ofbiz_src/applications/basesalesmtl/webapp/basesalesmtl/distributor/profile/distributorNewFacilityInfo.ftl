<#-- upgrade from ../create/facilityInfo.ftl -->
<div class="step-pane" id="facilityInfo">
	<div class="row-fluid form-horizontal form-window-content-custom">
		<div class="span6">
			<div class="row-fluid margin-top10">
				<div class="span5">
					<div style="float:right" id="wn_fa_isUsePrimaryAddress"><span></span></div>
				</div>
				<div class="span7">
					<b><i>${uiLabelMap.BSUsePrimaryAddress}</i></b>
				</div>
			</div>
			<div class="row-fluid margin-top10" style="display:none">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.LogFacilityName}</label></div>
				<div class="span7"><input type="text" id="wn_fa_facilityName" tabindex="1"/></div>
			</div>
			<#--
			<div class="row-fluid margin-top10">
				<div class="span5"><label class="text-right">${uiLabelMap.PhoneNumber}&nbsp;&nbsp;&nbsp;</label></div>
				<div class="span7"><input type="number" id="txtFPhoneNumber" tabindex="2"/></div>
			</div>
			-->
		</div><!--.span6-->
	</div>
	
	${setContextField("themePortrait", "landscape")}
	${setContextField("prefixName", "wn_fa")}
	${screens.render("component://basesales/widget/PartyScreens.xml#NewContactMechShippingAddress")}
</div>

<script type="text/javascript">
	$(function(){
		OlbDistributorNewFacility.init();
	});
	var OlbDistributorNewFacility = (function(){
		var validatorVAL;
		
		var init = function(){
			initEvent();
			initElement();
			initValidateForm();
		};
		var initElement = function(){
			$("#wn_fa_isUsePrimaryAddress").jqxCheckBox({width: 20, height: 20});
		};
		var initEvent = function(){
			$("#wn_fa_isUsePrimaryAddress").on("checked", function(event){
				disableField();
				setValue(OlbDistributorNewContact.getValue());
			});
			$("#wn_fa_isUsePrimaryAddress").on("unchecked", function(event){
				enableField();
			});
			<#--$("#isUsePrimaryAddress").click(function() {
				AddFacility.setValue(AddDistributor.getValue());
			});-->
		};
		var initValidateForm = function(){
			var extendRules = [
                {input: "#wn_fa_toName", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                    rule: function (input, commit) {
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRex(input.val(), multiLang.BSCheckFullName);
                    }
                },
                {input: "#wn_fa_attnName", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                    rule: function (input, commit) {
                        if(OlbCore.isEmpty(input.val())){
                            return true;
                        }
                        return checkRex(input.val(), multiLang.BSCheckSpecialCharacter);
                    }
                },
                {input: "#wn_fa_address1", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                    rule: function (input, commit) {
                        return checkRex(input.val(), multiLang.BSCheckAddress);
                    }
                },
            ];
			var mapRules = [
				{input: '#wn_fa_countryGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_fa_stateProvinceGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_fa_countyGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_fa_wardGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
		        {input: '#wn_fa_address1', type: 'validInputNotNull'},
			];
			validatorVAL = new OlbValidator($("#facilityInfo"), mapRules, extendRules, {position: "bottom", scroll: true});
		};
		var getValidator = function(){
			return validatorVAL;
		};
		var setValue = function(data) {
			if (!_.isEmpty(data)) {
				$("#wn_fa_countryGeoId").jqxComboBox("val", data.countryGeoId);
				$("#wn_fa_stateProvinceGeoId").jqxComboBox("val", data.stateProvinceGeoId);
				$("#wn_fa_countyGeoId").jqxComboBox("val", data.districtGeoId);
				$("#wn_fa_wardGeoId").jqxComboBox("val", data.wardGeoId);
				$("#wn_fa_address1").val(data.address1);
				$("#wn_fa_toName").val(data.toName);
				$("#wn_fa_attnName").val(data.attnName);
				$("#wn_fa_postalCode").val(data.postalCode);
			}
		};
		var getValue = function() {
			var value = {
				isUsePrimaryAddress: $("#wn_fa_isUsePrimaryAddress").val(),
				countryGeoId: $("#wn_fa_countryGeoId").jqxComboBox("val"),
				stateProvinceGeoId: $("#wn_fa_stateProvinceGeoId").jqxComboBox("val"),
				districtGeoId: $("#wn_fa_countyGeoId").jqxComboBox("val"),
				wardGeoId: $("#wn_fa_wardGeoId").jqxComboBox("val"),
				address1: $("#wn_fa_address1").val(),
				toName: $("#wn_fa_toName").val(),
				attnName: $("#wn_fa_attnName").val(),
				postalCode: $("#wn_fa_postalCode").val(),
				
				facilityName: $("#wn_fa_facilityName").val(),
				<#--contactNumber: $("#txtFPhoneNumber").val(),-->
			};
			return {facility: JSON.stringify(value)};
		};
		
		var getValueDesc = function() {
			// get address full name
			var toName = $("#wn_fa_toName").val();
			var attnName = $("#wn_fa_attnName").val();
			var postalCode = $("#wn_fa_postalCode").val();
			
			var addressFullName = toName ? toName + " " : "";
			if (!_.isEmpty(attnName)) addressFullName += "(" + attnName + ") ";
			else if (addressFullName != "") addressFullName += ", ";
			addressFullName += $("#wn_fa_address1").val();
			
			var wardGeoItem = $("#wn_fa_wardGeoId").jqxComboBox("getSelectedItem");
			var wardGeoStr = typeof("wardGeoItem") != "undefined" ? wardGeoItem.label : "";
			if (OlbCore.isNotEmpty(wardGeoStr)) addressFullName += ", " + wardGeoStr;
			var countyGeoItem = $("#wn_fa_countyGeoId").jqxComboBox("getSelectedItem");
			var countyGeoStr = typeof("countyGeoItem") != "undefined" ? countyGeoItem.label : "";
			if (OlbCore.isNotEmpty(countyGeoStr)) addressFullName += ", " + countyGeoStr;
			var stateProvinceGeoItem = $("#wn_fa_stateProvinceGeoId").jqxComboBox("getSelectedItem");
			var stateProvinceGeoStr = typeof("stateProvinceGeoItem") != "undefined" ? stateProvinceGeoItem.label : "";
			if (OlbCore.isNotEmpty(stateProvinceGeoStr)) addressFullName += ", " + stateProvinceGeoStr;
			var countryGeoItem = $("#wn_fa_countryGeoId").jqxComboBox("getSelectedItem");
			var countryGeoStr = typeof("countryGeoItem") != "undefined" ? countryGeoItem.label : "";
			if (OlbCore.isNotEmpty(countryGeoStr)) addressFullName += ", " + countryGeoStr;
			
			var value = {
				isUsePrimaryAddress: $("#wn_fa_isUsePrimaryAddress").val(),
				addressFullName: addressFullName
			};
			return value;
		};
		
		var disableField = function() {
			$("#wn_fa_countryGeoId").jqxComboBox("disabled", true);
			$("#wn_fa_stateProvinceGeoId").jqxComboBox("disabled", true);
			$("#wn_fa_countyGeoId").jqxComboBox("disabled", true);
			$("#wn_fa_wardGeoId").jqxComboBox("disabled", true);
			$("#wn_fa_address1").jqxInput("disabled", true);
			$("#wn_fa_toName").jqxInput("disabled", true);
			$("#wn_fa_attnName").jqxInput("disabled", true);
			$("#wn_fa_postalCode").jqxInput("disabled", true);
		};
		var enableField = function() {
			$("#wn_fa_countryGeoId").jqxComboBox("disabled", false);
			$("#wn_fa_stateProvinceGeoId").jqxComboBox("disabled", false);
			$("#wn_fa_countyGeoId").jqxComboBox("disabled", false);
			$("#wn_fa_wardGeoId").jqxComboBox("disabled", false);
			$("#wn_fa_address1").jqxInput("disabled", false);
			$("#wn_fa_toName").jqxInput("disabled", false);
			$("#wn_fa_attnName").jqxInput("disabled", false);
			$("#wn_fa_postalCode").jqxInput("disabled", false);
		};
		
		return {
			init: init,
			getValue: getValue,
			setValue: setValue,
			getValueDesc: getValueDesc,
		};
	}());
</script>
