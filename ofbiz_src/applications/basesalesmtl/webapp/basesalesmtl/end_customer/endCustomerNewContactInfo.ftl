<#-- upgrade from .../basesalesmtl/supervisor/createCustomer/contactInfo.ftl -->

<div class="step-pane" id="contactInfo">
<#if parameters.partyId?exists>
	<#include "component://basesalesmtl/webapp/basesalesmtl/common/listAddress.ftl"/>
<#else>
	<div class="row-fluid form-horizontal form-window-content-custom">
		<div class="span6">
			<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsCountry}</label></div>
				<div class="span7"><div id="txtCountry" tabindex="1"></div></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsProvince}</label></div>
				<div class="span7"><div id="txtProvince" tabindex="2"></div></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.DmsCounty}</label></div>
				<div class="span7"><div id="txtCounty" tabindex="3"></div></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.DmsWard}</label></div>
				<div class="span7"><div id="txtWard" tabindex="4"></div></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.DmsAddress1}</label></div>
				<div class="span7"><input type="text" id="tarAddress" tabindex="5"/></div>
			</div>
		</div>
		<div class="span6">
			<div class="row-fluid">
				<div class="span5"><label class="text-right asterisk">${uiLabelMap.PhoneNumber}</label></div>
				<div class="span7"><input type="tel" id="txtPhoneNumber" tabindex="6"/></div>
			</div>
			<div class="row-fluid">
				<div class="span5"><label class="text-right">${uiLabelMap.EmailAddress}</label></div>
				<div class="span7"><input type="email" id="txtEmailAddress" tabindex="7"/></div>
			</div>
		</div>
	</div><!--./form-window-content-custom-->
</#if>
	<script type="text/javascript">
		if (typeof (OlbCustomerMTNewContact) == "undefined") {
			var OlbCustomerMTNewContact = (function(){
				var validatorVAL;
				var countryCBB, stateProvinceGeoCBB, districtGeoCBB, wardGeoCBB;
				
				var init = function(){
					initElement();
					initElementComplex();
					if (!UpdateMode) initValidateForm();
					initEvent();
				};
				var initElement = function(){
					jOlbUtil.input.create('#tarAddress', {width: '96%'});
					if (!UpdateMode) jOlbUtil.input.create('#txtEmailAddress', {width: '96%'});
					if (!UpdateMode) jOlbUtil.input.create('#txtPhoneNumber', {width: '96%'});
				};
				var initElementComplex = function(){
					if (!UpdateMode) {
						wardGeoCBB = InternalUtilNew.initComboboxGeo("", "WARD", "txtWard");
					    districtGeoCBB = InternalUtilNew.initComboboxGeo("", "DISTRICT", "txtCounty");
					    stateProvinceGeoCBB = InternalUtilNew.initComboboxGeo("", "PROVINCE", "txtProvince");
					    countryCBB = InternalUtilNew.initComboboxGeo("", "COUNTRY", "txtCountry");
					}
				};
				var initEvent = function(){
					if (!UpdateMode) {
						countryCBB.selectItem(["VNM"]);
						InternalUtilNew.updateComboBoxGeo('VNM', 'PROVINCE', stateProvinceGeoCBB, "");
						
		                countryCBB.selectListener(function(itemData, index){
		                    if (itemData) {
			        			stateProvinceGeoCBB.clearAll();
			        			districtGeoCBB.clearAll();
			        			wardGeoCBB.clearAll();
			                    InternalUtilNew.updateComboBoxGeo(itemData.value, 'PROVINCE', stateProvinceGeoCBB, "");
		                    }
		                });
		                stateProvinceGeoCBB.selectListener(function(itemData, index){
		                    if (itemData) {
			        			districtGeoCBB.clearAll();
			        			wardGeoCBB.clearAll();
			                    InternalUtilNew.updateComboBoxGeo(itemData.value, 'DISTRICT', districtGeoCBB, "");
		                    }
		                });
		                districtGeoCBB.selectListener(function(itemData, index){
		        			wardGeoCBB.clearAll();
		                    if(itemData){
		                        InternalUtilNew.updateComboBoxGeo(itemData.value, 'WARD', wardGeoCBB, "");
		                    }
		                });
					}
				};
				var getValue = function(){
					var returnData;
					if (!UpdateMode) {
						returnData = {
							countryGeoId: OlbCore.isNotEmpty(countryCBB.getValue())?countryCBB.getValue():"",
							stateProvinceGeoId:OlbCore.isNotEmpty(stateProvinceGeoCBB.getValue())?stateProvinceGeoCBB.getValue():"",
							districtGeoId: OlbCore.isNotEmpty(districtGeoCBB.getValue())?districtGeoCBB.getValue():"",
							wardGeoId: OlbCore.isNotEmpty(wardGeoCBB.getValue())?wardGeoCBB.getValue():"",
							address1: $("#tarAddress").val(),
							contactNumber: $("#txtPhoneNumber").val(),
							infoString: $("#txtEmailAddress").val(),
						}
					}
					return returnData;
				};
				var setValue = function(data){
					$("#txtCountry").jqxComboBox("val", data.countryGeoId);
					$("#txtProvince").jqxComboBox("val", data.stateProvinceGeoId);
					$("#txtCounty").jqxComboBox("val", data.districtGeoId);
					$("#txtWard").jqxComboBox("val", data.wardGeoId);
					stateProvinceGeoId = data.stateProvinceGeoId;
					districtGeoId = data.districtGeoId;
					wardGeoId = data.wardGeoId;
					$("#tarAddress").val(data.address1);
				};
				var initValidateForm = function(){
					var extendRules = [
							{input: "#txtPhoneNumber", message: uiLabelMap.BSPhoneIsNotValid, action: "keyup, blur",
	                            rule: function (input, commit) {
	                                return OlbValidatorUtil.validElement(input, commit, "validPhoneNumber");
	                            }
	                        },
	                        {input: "#txtEmailAddress", message: uiLabelMap.BSEmailIsNotValid, action: "keyup, blur",
	                            rule: function (input, commit) {
	                                return OlbValidatorUtil.validElement(input, commit, "validEmailAddress");
	                            }
	                        },
			           ];
					var mapRules = [
							{input: '#txtPhoneNumber', type: 'validInputNotNull'},
							{input: '#tarAddress', type: 'validInputNotNull'},
							{input: '#txtCountry', type: 'validObjectNotNull', objType: 'comboBox'},
							{input: '#txtProvince', type: 'validObjectNotNull', objType: 'comboBox'},
			            ];
					validatorVAL = new OlbValidator($('#contactInfo'), mapRules, extendRules, {position: 'bottom', scroll: true});
				};
				var getValidator = function(){
					return validatorVAL;
				};
				return {
					init: init,
					getValidator: getValidator,
					getValue: getValue,
					setValue: setValue,
				}
			}());
		}
		
		if (typeof (InternalUtilNew) == "undefined") {
			var InternalUtilNew = (function() {
				var initComboboxGeo = function(geoId, geoTypeId, elementObj){
					var url = "";
					if(geoTypeId != "COUNTRY" && geoId){
						url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId + "&geoId=" + geoId;
					}else if(geoTypeId == "COUNTRY"){
						url =  "autoCompleteGeoAjax?geoTypeId=" + geoTypeId;
					}
		
					var configGeo = {
					    width: '98%', dropDownHeight: 150,
						placeHolder: uiLabelMap.BSClickToChoose,
						useUrl: true,
						url: url,
						key: 'geoId',
						value: 'geoName',
						root: 'listGeo',
						autoDropDownHeight: false,
						datafields: [{name: "geoId"}, {name: "geoName"}],
					}
					return new OlbComboBox($("#" + elementObj), null, configGeo, []);
				};
				var updateComboBoxGeo = function(geoId, geoTypeId, comboBoxOLB, defaultValue){
					comboBoxOLB.updateSource('autoCompleteGeoAjax?geoTypeId=' + geoTypeId + "&geoId=" + geoId, null, function(){
						if (defaultValue) {
							comboBoxOLB.selectItem([defaultValue]);
						}
					});
				};
				return {
					initComboboxGeo: initComboboxGeo,
					updateComboBoxGeo: updateComboBoxGeo
				};
			})();
		}
	</script>

</div>
