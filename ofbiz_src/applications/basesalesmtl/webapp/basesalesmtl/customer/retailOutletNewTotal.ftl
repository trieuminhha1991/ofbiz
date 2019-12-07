<#-- upgrade from ../addAgent.ftl -->
<style type="text/css">
	.step-content .step-pane {
		border: 1px solid #eee;
		margin-bottom: 20px;
		padding-top: 20px;
		min-height: 360px;
	}
</style>

<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>

<#assign listCurrencyUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
<#assign visitFrequencyTypes = delegator.findList("VisitFrequencyType", null, null, null, null, false) />
<script>
	Loading.show();
	<#if parameters.partyId?exists>
	document.title = "${StringUtil.wrapString(uiLabelMap.BSEditAgent)}";
	$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSEditAgent)}");
	$($(".widget-header").children("h4")).html("${StringUtil.wrapString(uiLabelMap.BSEditAgent)}");
	</#if>
	var partyIdPram = "${parameters.partyId?if_exists}";
	var listCurrencyUom = [<#if listCurrencyUom?exists><#list listCurrencyUom as item>{
		uomId: "${item.uomId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
    var visitFrequencyTypes = [<#if visitFrequencyTypes?exists><#list visitFrequencyTypes as item>{
		visitFrequencyTypeId: "${item.visitFrequencyTypeId?if_exists}",
		description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	multiLang = _.extend(multiLang, {
		salesmanId: "${StringUtil.wrapString(uiLabelMap.salesmanId)}",
		DADistributorId: "${StringUtil.wrapString(uiLabelMap.DADistributorId)}",
		DADistributorName: "${StringUtil.wrapString(uiLabelMap.DADistributorName)}",
		BsRouteId: "${StringUtil.wrapString(uiLabelMap.BsRouteId)}",
		BSRouteName: "${StringUtil.wrapString(uiLabelMap.BSRouteName)}",

        BSCheckSpecialCharacter: "${StringUtil.wrapString(uiLabelMap.BSCheckSpecialCharacter)}",
        BSCheckAddress: "${StringUtil.wrapString(uiLabelMap.BSCheckAddress)}",
        BSCheckPhone: "${StringUtil.wrapString(uiLabelMap.BSCheckPhone)}",
        BSCheckEmail: "${StringUtil.wrapString(uiLabelMap.BSCheckEmail)}",
        BSCheckWebsite: "${StringUtil.wrapString(uiLabelMap.BSCheckWebsite)}",
        BSCheckTaxCode: "${StringUtil.wrapString(uiLabelMap.BSCheckTaxCode)}",
        BSWebsiteIsNotValid: "${StringUtil.wrapString(uiLabelMap.BSWebsiteIsNotValid)}",
        BSEmailIsNotValid: "${StringUtil.wrapString(uiLabelMap.BSEmailIsNotValid)}",
        BSPhoneIsNotValid: "${StringUtil.wrapString(uiLabelMap.BSPhoneIsNotValid)}",
        BSTaxCodeIsNotValid: "${StringUtil.wrapString(uiLabelMap.BSTaxCodeIsNotValid)}",
        BSCharacterIsNotValid: "${StringUtil.wrapString(uiLabelMap.BSCharacterIsNotValid)}",
        BSCheckPostalCode: "${StringUtil.wrapString(uiLabelMap.BSCheckPostalCode)}",
        BSPostalCodeIsNotValid: "${StringUtil.wrapString(uiLabelMap.BSPostalCodeIsNotValid)}",
        BSCheckFullName: "${StringUtil.wrapString(uiLabelMap.BSCheckFullName)}",
		});
	
	var UpdateMode = false;
	$(function(){
		if(partyIdPram) UpdateMode = true;
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
<#--<script src="/salesmtlresources/js/supervisor/addAgent.js"></script>-->
<script src="/salesmtlresources/js/supervisor/tmp.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/crmresources/js/generalUtils.js"></script>

<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>

<div id="containerNestedSlide" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotificationNestedSlide" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>

<#if parameters.partyId?exists>
	<#assign textBtnLast = uiLabelMap.CommonUpdate/>
<#else>
	<#assign textBtnLast = uiLabelMap.CommonCreate/>
</#if>

<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
	    <ul class="wizard-steps wizard-steps-square">
			<#if parameters.partyId?exists>
				<li data-target="#generalInfo" class="active">
					<span class="step">1. ${uiLabelMap.GeneralInformation}</span>
				</li>

				<li data-target="#representativeInfo">
					<span class="step">2. ${uiLabelMap.BERepresentative}</span>
				</li>
				<li data-target="#confirmInfo">
					<span class="step">3. ${uiLabelMap.BSConfirm}</span>
				</li>
			<#else>
				<li data-target="#generalInfo" class="active">
					<span class="step">1. ${uiLabelMap.GeneralInformation}</span>
				</li>

				<li data-target="#contactInfo">
					<span class="step">2. ${uiLabelMap.ContactInformation}</span>
				</li>

				<li data-target="#representativeInfo">
					<span class="step">3. ${uiLabelMap.BERepresentative}</span>
				</li>
				<li data-target="#confirmInfo">
					<span class="step">4. ${uiLabelMap.BSConfirm}</span>
				</li>
			</#if>
		</ul>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container">
		<#if parameters.partyId?exists>
			<#include "retailOutletNewInfo.ftl"/>

			<#include "retailOutletNewOwnerInfo.ftl"/>

			<#include "retailOutletNewConfirm.ftl"/>
		<#else>
			<#include "retailOutletNewInfo.ftl"/>

			<#include "retailOutletNewContactInfo.ftl"/>

			<#include "retailOutletNewOwnerInfo.ftl"/>

			<#include "retailOutletNewConfirm.ftl"/>
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
	
	$(function(){
		OlbOutletNewTotal.init();
		
		if (partyIdPram) {
			OlbOutletNewTotal.letUpdate(partyIdPram);
			$("#account-info").addClass('hide');
		}
		
		Loading.hide();
	});
	var OlbOutletNewTotal = (function(){
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
		        	var validateStep = Validator.validate.contactInfo();
		        	<#if parameters.partyId?exists>
		        	if (validateStep) {
			        	var isCopyAddress = $("#wn_own_isUsePrimaryAddress").val();
			        	var valueTotal = OlbOutletNewInfo.getValueDesc();
							valueTotal.representativeInfos = OlbOutletNewOwner.getValueDesc();
							OlbOutletNewConfirm.setValue(valueTotal);
			        	if (isCopyAddress) {
			        		//disableField();
			        	}
		        	}
		        	</#if>
		        	
		        	return validateStep;
		        } else if (info.step == 3 && (info.direction == "next")) {
		        	var validateStep = Validator.validate.representativeInfo();
					if (validateStep) {
						var valueTotal = OlbOutletNewInfo.getValueDesc();
						valueTotal.representativeInfos = OlbOutletNewOwner.getValueDesc();
						OlbOutletNewConfirm.setValue(valueTotal);
					}
					
					return validateStep;
		        }
		        if(info.direction == "previous"){
		        	Validator.hide();
		        }
		    }).on('finished', function(e) {
		    	if (UpdateMode) {
					mesConfirm = multiLang.UpdateConfirm;
				} else {
					mesConfirm = multiLang.CreateNewConfirm;
				}
				
				jOlbUtil.confirm.dialog(mesConfirm, function(){
					createRetailOutlet();
				});
		    });
		};
		var createRetailOutlet = function(){
			$("#btnPrevWizard").addClass("disabled");
			$("#btnNextWizard").addClass("disabled");
			
			var logoImageUrl;
			if ($('#logoImageUrl').prop('files')[0]) {
				logoImageUrl = DataAccess.uploadFile($('#logoImageUrl').prop('files')[0]);
			}
			var data = OlbOutletNewInfo.getValue();
			data.logoImageUrl = logoImageUrl;
			
			if (!_.isEmpty(extendId)) {
				data = _.extend(data, extendId);
			}
			if (UpdateMode) {
				data = _.extend(data, OlbOutletNewOwner.getValue());
			} else {
				data = _.extend(data, OlbOutletNewContact.getValue(), OlbOutletNewOwner.getValue());
			}
			
			var url = UpdateMode ? "updateAgent" : "createAgent";
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
					        		window.location.href = "AgentDetail?partyId=" + data.partyId;
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
				url: "loadAgentInfo",
				data: {partyId: partyId},
				source: "agentInfo"});
			extendId.addressId = data.addressId;
			OlbOutletNewInfo.setValue(data);
			setTimeout(function(){
				OlbOutletNewOwner.setValue(data.representative);
			}, 400);
			
			<#--if (data.contacts) {
				AdditionalContact.initGrid(data.contacts);
			}-->
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
					$("#representativeInfo").jqxValidator("hide");
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
				return {
					generalInfo: generalInfo,
					contactInfo: contactInfo,
					representativeInfo: representativeInfo
				}
			})();
			return {
				hide: hide,
				validate: validate
			}
		})();
	}
</script>
