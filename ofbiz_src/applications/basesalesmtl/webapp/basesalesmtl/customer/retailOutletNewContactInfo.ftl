<#-- upgrade from ..basesalesmtl/supervisor/createAgent/contactInfo.ftl -->

<div class="step-pane" id="contactInfo">
<#if parameters.partyId?exists>
	<#include "component://basesalesmtl/webapp/basesalesmtl/common/listAddress.ftl"/>
<#else>
	${setContextField("themePortrait", "landscape")}
	${screens.render("component://basesales/widget/PartyScreens.xml#NewContactMechShippingAddress")}
	
	<script type="text/javascript">
		$(function(){
			OlbOutletNewContact.init();
		});
		var OlbOutletNewContact = new function () {
			var validatorVAL;
			
			var init = function(){
				initValidateForm();
			};
			var initValidateForm = function(){
				var extendRules = [
                    {input: "#wn_toName", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                        rule: function (input, commit) {
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRex(input.val(), multiLang.BSCheckFullName);
                        }
                    },
                    {input: "#wn_attnName", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                        rule: function (input, commit) {
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRex(input.val(), multiLang.BSCheckSpecialCharacter);
                        }
                    },
                    {input: "#wn_address1", message: multiLang.BSCharacterIsNotValid, action: "keyup, blur",
                        rule: function (input, commit) {
                            return checkRex(input.val(), multiLang.BSCheckAddress);
                        }
                    },
                    {input: "#wn_postalCode", message: multiLang.BSPostalCodeIsNotValid, action: "keyup, blur",
                        rule: function (input, commit) {
                            if(OlbCore.isEmpty(input.val())){
                                return true;
                            }
                            return checkRex(input.val(), multiLang.BSCheckPostalCode);
                        }
                    },
                ];
				var mapRules = [
					{input: '#wn_countryGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
			        {input: '#wn_stateProvinceGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
			        {input: '#wn_countyGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
			        {input: '#wn_wardGeoId', type: 'validObjectNotNull', objType: 'comboBox'},
			        {input: '#wn_address1', type: 'validInputNotNull'},
				];
				validatorVAL = new OlbValidator($("#contactInfo"), mapRules, extendRules, {position: "bottom", scroll: true});
			};
			var getValidator = function(){
				return validatorVAL;
			};
			var getValue = function(){
				return {
					countryGeoId: $("#wn_countryGeoId").jqxComboBox("val"),
					stateProvinceGeoId: $("#wn_stateProvinceGeoId").jqxComboBox("val"),
					districtGeoId: $("#wn_countyGeoId").jqxComboBox("val"),
					wardGeoId: $("#wn_wardGeoId").jqxComboBox("val"),
					address1: $("#wn_address1").val(),
					toName: $("#wn_toName").val(),
					attnName: $("#wn_attnName").val(),
					postalCode: $("#wn_postalCode").val(),
				}
			};
			return {
				init: init,
				getValidator: getValidator,
				getValue: getValue,
			};
		}
	</script>
</#if>
</div>
