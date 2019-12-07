<#-- upgrade from ../addMTCustomer.ftl -->

<script type="text/javascript">
	var partyIdPram = "${parameters.partyId?if_exists}";
	var extendData = {};
	
	var UpdateMode = false;
	if (partyIdPram) {
		UpdateMode = true;
		document.title = "${StringUtil.wrapString(uiLabelMap.BSUpdateCustomerMT)}";
		$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSUpdateCustomerMT)}");
		$($(".widget-header").children("h4")).html("${StringUtil.wrapString(uiLabelMap.BSUpdateCustomerMT)}");
	}
</script>

<div id="jqxNotificationNestedSlide">
	<div id="notificationContentNestedSlide"></div>
</div>

<div class="row-fluid">
	<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
	    <ul class="wizard-steps wizard-steps-square">
			<li data-target="#generalInfo" class="active">
                <span class="step">1. ${uiLabelMap.GeneralInformation}</span>
            </li>
            <li data-target="#contactInfo">
                <span class="step">2. ${uiLabelMap.ContactInformation}</span>
            </li>
            <li data-target="#representativeInfo">
	        	<span class="step">3. ${uiLabelMap.BERepresentative}</span>
	        </li>
		</ul>
	</div><!--#fuelux-wizard-->
	<div class="step-content row-fluid position-relative" id="step-container">
		<#include "customerMTNewInfo.ftl"/>
		
		<#include "customerMTNewContactInfo.ftl"/>
		
		<#include "customerMTNewOwnerInfo.ftl"/>
	</div>
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

<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript" src="/crmresources/js/DataAccess.js"></script>

<script type="text/javascript">
	if (typeof uiLabelMap == "undefined") var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	uiLabelMap.BsRouteId = "${StringUtil.wrapString(uiLabelMap.BsRouteId)}";
	uiLabelMap.BSRouteName = "${StringUtil.wrapString(uiLabelMap.BSRouteName)}";
	uiLabelMap.BSCompanyId = "${StringUtil.wrapString(uiLabelMap.BSCompanyId)}";
	uiLabelMap.BSCompanyName = "${StringUtil.wrapString(uiLabelMap.BSCompanyName)}";
	uiLabelMap.salesmanId = "${StringUtil.wrapString(uiLabelMap.salesmanId)}";
	uiLabelMap.DmsPartyLastName = "${StringUtil.wrapString(uiLabelMap.DmsPartyLastName)}";
	uiLabelMap.DmsPartyMiddleName = "${StringUtil.wrapString(uiLabelMap.DmsPartyMiddleName)}";
	uiLabelMap.DmsPartyFirstName = "${StringUtil.wrapString(uiLabelMap.DmsPartyFirstName)}";
	uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
	uiLabelMap.BSPhoneIsNotValid = "${StringUtil.wrapString(uiLabelMap.BSPhoneIsNotValid)}";
	uiLabelMap.BSEmailIsNotValid = "${StringUtil.wrapString(uiLabelMap.BSEmailIsNotValid)}";
	uiLabelMap.BSCheckPhone = "${StringUtil.wrapString(uiLabelMap.BSCheckPhone)}";
	uiLabelMap.BSCheckEmail = "${StringUtil.wrapString(uiLabelMap.BSCheckEmail)}";
	uiLabelMap.BSCharacterIsNotValid = "${StringUtil.wrapString(uiLabelMap.BSCharacterIsNotValid)}";
	uiLabelMap.BSCheckFullName = "${StringUtil.wrapString(uiLabelMap.BSCheckFullName)}";
	uiLabelMap.BSCheckSpecialCharacter = "${StringUtil.wrapString(uiLabelMap.BSCheckSpecialCharacter)}";
	uiLabelMap.BSCheckAddress = "${StringUtil.wrapString(uiLabelMap.BSCheckAddress)}";
	uiLabelMap.BSPostalCodeIsNotValid = "${StringUtil.wrapString(uiLabelMap.BSPostalCodeIsNotValid)}";
	uiLabelMap.BSCheckPostalCode = "${StringUtil.wrapString(uiLabelMap.BSCheckPostalCode)}";
	uiLabelMap.UpdateConfirm = "${StringUtil.wrapString(uiLabelMap.UpdateConfirm)}";
	uiLabelMap.CreateNewConfirm = "${StringUtil.wrapString(uiLabelMap.CreateNewConfirm)}";
	uiLabelMap.updateSuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	uiLabelMap.addSuccess = "${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}";
	uiLabelMap.BSProductStoreId = "${StringUtil.wrapString(uiLabelMap.BSProductStoreId)}";
	uiLabelMap.BSStoreName = "${StringUtil.wrapString(uiLabelMap.BSStoreName)}";
	uiLabelMap.BSOwner = "${StringUtil.wrapString(uiLabelMap.FormFieldTitle_ownerContentId)}";
	uiLabelMap.BSSalesChannelEnumId = "${StringUtil.wrapString(uiLabelMap.BSSalesChannelEnumId)}";
	uiLabelMap.BSId = "${StringUtil.wrapString(uiLabelMap.BSId)}";
	uiLabelMap.BSFullName = "${StringUtil.wrapString(uiLabelMap.BSFullName)}";
	
	$(function(){
		Loading.show();
		OlbCustomerMTNew.init();
	});
	if (typeof OlbCustomerMTNew == "undefined") {
	var OlbCustomerMTNew = (function(){
		var init = function(){
			initElement();
			OlbCustomerMTNewInfo.init();
			if (!UpdateMode) OlbCustomerMTNewContact.init();
			OlbCustomerMTNewOwner.init();
			
			if (UpdateMode) { // update selected data
				letUpdate(partyIdPram);
			}
			
			setTimeout(function() {
				Loading.hide();
			}, 2000);
		};
		var letUpdate = function(partyId) {
			var data = DataAccess.getData({
				url: "loadMTCustomerInfo",
				data: {partyId: partyId},
				source: "MTCustomerInfo"});
			setValue(data);
		};
		var setValue = function(data){
			extendData.contactNumberId = data.contactNumberId;
			extendData.infoStringId = data.infoStringId;
			extendData.addressId = data.addressId;
			extendData.taxAuthInfosfromDate = data.taxAuthInfosfromDate;
			extendData.partyId = data.partyId;
			
			OlbCustomerMTNewInfo.setValue(data);
			OlbCustomerMTNewOwner.setValue(data.representative);
		};
		var initElement = function(){
			jOlbUtil.notification.create($("#jqxNotificationNestedSlide"), $("#notificationContentNestedSlide"));
			
			$('#fuelux-wizard').ace_wizard().on('change' , function(e, info) {
		        if (info.step == 1 && (info.direction == "next")) {
		        	return OlbCustomerMTNewInfo.getValidator().validate();
		        } else if (info.step == 2 && (info.direction == "next")) {
		        	if (!UpdateMode) return OlbCustomerMTNewContact.getValidator().validate();
		        	else return true;
		        }
		        if(info.direction == "previous"){
		        	OlbCustomerMTNewInfo.getValidator().hide();
		        }
		    }).on('finished', function(e) {
		    	if (!OlbCustomerMTNewOwner.getValidator().validate()){
		    		return false;
		    	}
		    	if (UpdateMode) {
					mesConfirm = uiLabelMap.UpdateConfirm;
				} else {
					mesConfirm = uiLabelMap.CreateNewConfirm;
				}
				
				jOlbUtil.confirm.dialog(mesConfirm, function(){
					createCustomerMT();
				});
		    });
		};
		var createCustomerMT = function(){
			$("#btnPrevWizard").addClass("disabled");
			$("#btnNextWizard").addClass("disabled");
			
			var logoImageUrl;
			if ($('#logoImageUrl').prop('files')[0]) {
				logoImageUrl = DataAccess.uploadFile($('#logoImageUrl').prop('files')[0]);
			}
			var data = OlbCustomerMTNewInfo.getValue();
			data.logoImageUrl = logoImageUrl;
			
			if (!_.isEmpty(extendData)) {
				data = _.extend(data, extendData);
			}
			if (UpdateMode) {
				data = _.extend(data, OlbCustomerMTNewOwner.getValue());
			} else {
				data = _.extend(data, OlbCustomerMTNewContact.getValue(), OlbCustomerMTNewOwner.getValue());
			}
			
			var url = UpdateMode ? "updateMTCustomer" : "createMTCustomer";
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
									txtMsg = uiLabelMap.updateSuccess;
								} else {
									txtMsg = uiLabelMap.addSuccess;
								}
								
								$('#containerNestedSlide').empty();
					        	$('#jqxNotificationNestedSlide').jqxNotification({ template: 'info'});
					        	$("#jqxNotificationNestedSlide").html(txtMsg);
					        	$("#jqxNotificationNestedSlide").jqxNotification("open");
					        	if (data.partyId != undefined && data.partyId != null) {
					        		window.location.href = "MTCustomerDetail?partyId=" + data.partyId;
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
		
		return {
			init: init
		}
	}());
	};
</script>
