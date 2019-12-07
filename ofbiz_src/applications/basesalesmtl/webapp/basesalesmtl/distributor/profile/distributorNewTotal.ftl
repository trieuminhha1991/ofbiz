<#-- upgrade from ../addDistibutor.ftl -->
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/crmresources/js/generalUtils.js"></script>
<style type="text/css">
	.step-content .step-pane {
		border: 1px solid #eee;
		margin-bottom: 20px;
		padding-top: 20px;
		min-height: 360px;
	}
	#tab-confirmInfo {
	    min-width: 16.5% !important;
	}
</style>

<#assign listCurrencyUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />

<script type="text/javascript">
	Loading.show();
	
	<#if parameters.partyId?exists>
	document.title = "${StringUtil.wrapString(uiLabelMap.BSEditDistributor)}"
	$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSEditDistributor)}");
	$($(".widget-header").children("h4")).html("${StringUtil.wrapString(uiLabelMap.BSEditDistributor)}");
	</#if>
	var partyIdPram = "${parameters.partyId?if_exists}";
	var listCurrencyUom = [<#if listCurrencyUom?exists><#list listCurrencyUom as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
	},</#list></#if>];
	
	multiLang = _.extend(multiLang, {
		BEPasswordEqualsUserName: "${StringUtil.wrapString(uiLabelMap.BEPasswordEqualsUserName)}",
		BEPasswordShort: "${StringUtil.wrapString(uiLabelMap.BEPasswordShort)}",
		BSSupervisorId: "${StringUtil.wrapString(uiLabelMap.BSSupervisorId)}",
		BSSupervisor: "${StringUtil.wrapString(uiLabelMap.BSSupervisor)}",
		
        BSCheckSpecialCharacter: "${StringUtil.wrapString(uiLabelMap.BSCheckSpecialCharacter)}",
        BSCheckAddress: "${StringUtil.wrapString(uiLabelMap.BSCheckAddress)}",
        BSCheckPhone: "${StringUtil.wrapString(uiLabelMap.BSCheckPhone)}",
        BSCheckEmail: "${StringUtil.wrapString(uiLabelMap.BSCheckEmail)}",
        BSCheckWebsite: "${StringUtil.wrapString(uiLabelMap.BSCheckWebsite)}",
        BSCheckTaxCode: "${StringUtil.wrapString(uiLabelMap.BSCheckTaxCode)}",
        BSWebsiteIsNotValid: "${StringUtil.wrapString(uiLabelMap.BSWebsiteIsNotValid)}",
        BSEmailIsNotValid: "${StringUtil.wrapString(uiLabelMap.BSEmailIsNotValid)}",
        BSPhoneIsNotValid: "${StringUtil.wrapString(uiLabelMap.BSPhoneIsNotValid)}",
        BSAddressIsNotValid: "${StringUtil.wrapString(uiLabelMap.BSAddressIsNotValid)}",
        BSAddressIsNull: "${StringUtil.wrapString(uiLabelMap.BSAddressIsNull)}",
        BSTaxCodeIsNotValid: "${StringUtil.wrapString(uiLabelMap.BSTaxCodeIsNotValid)}",
        BSCharacterIsNotValid: "${StringUtil.wrapString(uiLabelMap.BSCharacterIsNotValid)}",
        BSCheckId: "${StringUtil.wrapString(uiLabelMap.BSCheckId)}",
        BSCheckFullName: "${StringUtil.wrapString(uiLabelMap.BSCheckFullName)}",
        BSBirthDateBeforeToDay: "${StringUtil.wrapString(uiLabelMap.BSBirthDateBeforeToDay)}",
	});
	
	var UpdateMode = false;
	$(function(){
		if (partyIdPram) UpdateMode = true;
	});
    var checkRex = function(value,uilabel){
        if(OlbCore.isNotEmpty(uilabel) && OlbCore.isNotEmpty(value)){
            var regexCheck = new RegExp(uilabel);
            if(regexCheck.test(value)){
                return true;
            }
        }
        return false;
    };
</script>

<div id="containerNestedSlide" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotificationNestedSlide" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>

<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
	    <ul class="wizard-steps wizard-steps-square">
	        <li data-target="#generalInfo" class="active">
	            <span class="step">1. ${uiLabelMap.GeneralInformation}</span>
	        </li>
	        <li data-target="#contactInfo">
	            <span class="step">2. ${uiLabelMap.ContactInformation}</span>
	        </li>
	        <#if parameters.partyId?exists>
		        <#assign textBtnLast = uiLabelMap.CommonUpdate/>
		        <li data-target="#representativeInfo">
		        	<span class="step">3. ${uiLabelMap.BERepresentative}</span>
		        </li>
	        <#else>
		        <#assign textBtnLast = uiLabelMap.CommonCreate/>
		        <li data-target="#facilityInfo">
		            <span class="step">3. ${uiLabelMap.FacilityInformation}</span>
		        </li>
		        <li data-target="#representativeInfo">
		        	<span class="step">4. ${uiLabelMap.BERepresentative}</span>
		        </li>
		        <li data-target="#accountInfo">
		            <span class="step">5. ${uiLabelMap.BSAccountInfo}</span>
		        </li>
		        <li data-target="#confirmInfo" id="tab-confirmInfo">
		            <span class="step">6. ${uiLabelMap.BSConfirm}</span>
		        </li>
	        </#if>
		</ul>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container">
		<#include "distributorNewInfo.ftl"/>
		
		<#include "distributorNewContactInfo.ftl"/>
			
		<#if parameters.partyId?exists>
			<#include "distributorNewOwnerInfo.ftl"/>
		<#else>
			<#include "distributorNewFacilityInfo.ftl"/>

			<#include "distributorNewOwnerInfo.ftl"/>

			<#include "distributorNewAccountInfo.ftl"/>

			<#include "distributorNewConfirm.ftl"/>
		</#if>
	</div>
	<#--
	<div class="wizard-actions">
		<button class="btn btn-next btn-success form-action-button pull-right" data-last="${StringUtil.wrapString(textBtnLast)}" id="btnNext">
			${uiLabelMap.CommonNext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
		<button class="btn btn-prev form-action-button pull-right" id="btnPrev">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.CommonPrevious}
		</button>
	</div>
	-->
	<div class="row-fluid wizard-actions">
		<button class="btn btn-small btn-prev" id="btnPrevWizard">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.BSPrev}
		</button>
		<button class="btn btn-small btn-success btn-next" id="btnNextWizard" data-last="${uiLabelMap.BSFinish}">
			${uiLabelMap.BSNext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
	</div>
</div>

<div class="container_loader">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript">
	var extendId = new Object();
	<#if parameters.partyId?exists>
		var primaryLocation = {};
	</#if>
	
	$(function(){
		OlbDropNewTotal.init();
		if (partyIdPram) {
			OlbDropNewTotal.letUpdate(partyIdPram);
			$("#account-info").addClass('hide');
		}
		Loading.hide();
	});
	
	var OlbDropNewTotal = (function(){
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			jOlbUtil.notification.create($("#containerNestedSlide"), $("#jqxNotificationNestedSlide"));
		};
		var initEvent = function(){
			$('#fuelux-wizard').ace_wizard().on('change' , function(e, info) {
		        if (info.step == 1 && (info.direction == "next")) {
		        	return Validator.validate.generalInfo();
		        } else if (info.step == 2 && (info.direction == "next")) {
					<#if parameters.partyId?exists>		        
		        		var validateStep = listAddressObj.getValidate();
		        	<#else>
		        		var validateStep = Validator.validate.contactInfo();
		        	</#if>
		        	
		        	if (validateStep) {
		        		<#if parameters.partyId?exists>		        
		        			var locate = $("#shippingContactMechGrid").jqxGrid("getrows");
		        			for(var idx =0; idx< locate.length; idx++ ){
 					        	if(locate[idx].contactMechPurposeTypeId == "PRIMARY_LOCATION"){
 					        		primaryLocation = {
	 					        		countryGeoId: locate[idx].countryGeoId,
										stateProvinceGeoId: locate[idx].stateProvinceGeoId,
										districtGeoId: locate[idx].districtGeoId,
										wardGeoId: locate[idx].wardGeoId,
										address1: locate[idx].address1,
										toName: locate[idx].toName,
										attnName: locate[idx].attnName,
										postalCode: locate[idx].postalCode,
									}
 					        	}
 					        } 
		        		</#if>
		        		
		        		var distributorCode = $("#partyCode").val();
			        	$("#wn_fa_facilityName").val("Kho " + distributorCode);
			        	
			        	var isCopyAddress = $("#wn_fa_isUsePrimaryAddress").val();
			        	if (isCopyAddress) {
			        		//disableField();
							OlbDistributorNewFacility.setValue(OlbDistributorNewContact.getValue());
			        	}
		        	}
		        	
		        	return validateStep;
		        } else if(info.step == 3 && (info.direction == "next")) {
		        	var validateStep = Validator.validate.facilityInfo();
		        	
		        	if (validateStep) {
			        	var isCopyAddress = $("#wn_own_isUsePrimaryAddress").val();
			        	if (isCopyAddress) {
			        		//disableField();
							OlbDistributorNewOwner.setValue(OlbDistributorNewContact.getValue());
			        	}
		        	}
		        	
		        	return validateStep;
				} else if(info.step == 4 && (info.direction == "next")) {
					return Validator.validate.representativeInfo();
				} else if(info.step == 5 && (info.direction == "next")) {
					var validateStep = Validator.validate.accountInfo();
					
					if (validateStep) {
						var valueTotal = OlbDistributorNewInfo.getValueDesc();
						valueTotal.facilityInfos = OlbDistributorNewFacility.getValueDesc();
						valueTotal.representativeInfos = OlbDistributorNewOwner.getValueDesc();
						OlbDistributorNewConfirm.setValue(valueTotal);
					}
					
					return validateStep;
				}
		        if (info.direction == "previous") {
		        	Validator.hide();
		        }
		    }).on('finished', function(e) {
		    	if (UpdateMode) {
					mesConfirm = multiLang.UpdateConfirm;
				} else {
					mesConfirm = multiLang.CreateNewConfirm;
				}
				
				jOlbUtil.confirm.dialog(mesConfirm, function(){
					createDistributor();
				});
		    }).on('stepclick', function(e){
				//return false;//prevent clicking on steps
			});
		};
		
		var createDistributor = function(){
			$("#btnPrevWizard").addClass("disabled");
			$("#btnNextWizard").addClass("disabled");
			
			var logoImageUrl;
			if ($('#logoImageUrl').prop('files')[0]) {
				logoImageUrl = DataAccess.uploadFile($('#logoImageUrl').prop('files')[0]);
			}
			var data = OlbDistributorNewInfo.getValue();
			data.logoImageUrl = logoImageUrl;
			
			if (!_.isEmpty(extendId)) {
				data = _.extend(data, extendId);
			}
			if (UpdateMode) {
				data = _.extend(data, OlbDistributorNewOwner.getValue());
			} else {
				data = _.extend(data, OlbDistributorNewOwner.getValue(), OlbDistributorNewFacility.getValue());
			}
			
			var url = UpdateMode ? "updateDistributor" : "createDistributor";
			$.ajax({
				type: 'POST',
				url: url,
				data: data,
				beforeSend: function(){
					$("#loader_page_common").show();
				},
				success: function(data){
					jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
								$("#btnPrevWizard").removeClass("disabled");
								$("#btnNextWizard").removeClass("disabled");
								
					        	$('#containerNestedSlide').empty();
					        	$('#jqxNotificationNestedSlide').jqxNotification({ template: 'info'});
					        	$("#jqxNotificationNestedSlide").html(errorMessage);
					        	$("#jqxNotificationNestedSlide").jqxNotification("open");
					        	return false;
							}, function(){
								var txtMsg = uiLabelMap.wgupdatesuccess;
								if (UpdateMode) {
									txtMsg = multiLang.updateSuccess;
								} else {
									txtMsg = multiLang.addSuccess;
								}
								
								$('#containerNestedSlide').empty();
					        	$('#jqxNotificationNestedSlide').jqxNotification({ template: 'info'});
					        	$("#jqxNotificationNestedSlide").html(txtMsg);
					        	$("#jqxNotificationNestedSlide").jqxNotification("open");
					        	if (data.partyId != undefined && data.partyId != null) {
					        		window.location.href = "DistributorDetail?partyId=" + data.partyId;
					        	}
							}
					);
				},
				error: function(data){
					alert("Send request is error");
					$("#btnPrevWizard").removeClass("disabled");
					$("#btnNextWizard").removeClass("disabled");
				},
				complete: function(data){
					$("#loader_page_common").hide();
				},
			});
		};
		var letUpdate = function(partyId) {
			var data = DataAccess.getData({
				url: "loadDistributorInfo",
				data: {partyId: partyId},
				source: "distributorInfo"});
			extendId.addressId = data.addressId;
			OlbDistributorNewInfo.setValue(data);
			setTimeout(function(){
				OlbDistributorNewOwner.setValue(data.representative);
			}, 400);
		};
		
		return {
			init: init,
			letUpdate: letUpdate,
		};
	}());

	if (typeof (Validator) == "undefined") {
		var Validator = (function() {
			var hide = function() {
				if (UpdateMode) {
					$("#generalInfo").jqxValidator("hide");
					$("#representativeInfo").jqxValidator("hide");
				} else {
					$("#generalInfo").jqxValidator("hide");
					$("#contactInfo").jqxValidator("hide");
					$("#facilityInfo").jqxValidator("hide");
					$("#representativeInfo").jqxValidator("hide");
					$("#accountInfo").jqxValidator("hide");
				}
			};
			var validate = (function() {
				var generalInfo = function() {
					return $("#generalInfo").jqxValidator("validate");
				};
				var representativeInfo = function() {
					return $("#representativeInfo").jqxValidator("validate");
				};
				var contactInfo = function() {
					if (UpdateMode) {
						return true;
					} else {
						return $("#contactInfo").jqxValidator("validate");
					}
				};
				var facilityInfo = function() {
					if (UpdateMode) {
						return true;
					} else {
						return $("#facilityInfo").jqxValidator("validate");
					}
				};
				var accountInfo = function() {
					if (UpdateMode) {
						return true;
					} else {
						return $("#accountInfo").jqxValidator("validate");
					}
				};
				return {
					generalInfo: generalInfo,
					contactInfo: contactInfo,
					facilityInfo: facilityInfo,
					representativeInfo: representativeInfo,
					accountInfo: accountInfo
				}
			})();
			return {
				hide: hide,
				validate: validate
			}
		})();
	}
</script>