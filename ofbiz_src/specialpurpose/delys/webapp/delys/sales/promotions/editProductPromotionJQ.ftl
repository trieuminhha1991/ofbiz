<style type="text/css">
	.btn.btn-prev#btnPrevWizard {
		background-color: #87b87f!important;
  		border-color: #87b87f;
	}
	.btn.btn-prev#btnPrevWizard:hover {
		background-color: #629b58!important;
  		border-color: #87b87f;
	}
	.btn.btn-prev#btnPrevWizard:disabled {
		background-color: #abbac3!important;
	  	border-color: #abbac3;
	}
	.nav-tabs {
		border-bottom:none;
	}
</style>
<div class="row-fluid">
	<h4 id="step-title" class="header smaller blue span3" style="margin-bottom:0 !important; border-bottom: none; margin-top:3px !important; padding-bottom:0">
		${uiLabelMap.DAAbbPromotionInfomation} 
	</h4>
	<div id="fuelux-wizard" class="row-fluid hide span8" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-mini">
			<li data-target="#step1" class="active">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.DAAbbPromotionInfomation}" data-placement="bottom">1</span>
			</li>

			<li data-target="#step2">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.DAAbbPromotionRules}" data-placement="bottom">2</span>
			</li>
		</ul>
	</div>
	<div class="span1 align-right" style="padding-top:5px">
		<#if productPromoId?has_content && productPromo?exists>
			<a href="<@ofbizUrl>viewProductPromo?productPromoId=${productPromo.productPromoId?if_exists}</@ofbizUrl>" data-rel="tooltip" title="${uiLabelMap.DAViewDetail} ${productPromo.productPromoId?if_exists}" data-placement="bottom" class="no-decoration">
				<i class="fa-search open-sans open-sans-index" style="font-size:16pt"></i>
			</a>
		</#if>
	</div>
	<div style="clear:both"></div>
	<hr style="margin: 8px 0" />
	
	<div id="jqxNotification">
    	<div id="notificationContent"></div>
    </div>
    <div id="container" style="background-color: transparent; overflow: auto;">
    </div>
    
	<div class="step-content row-fluid position-relative" id="step-container">
		<div class="step-pane active" id="step1">
			<#include "editProductPromotionInfoGeneral.ftl"/>
		</div>

		<div class="step-pane" id="step2">
			<#include "editProductPromotionRules.ftl"/>
		</div>
	</div>
	
	<div class="row-fluid wizard-actions">
		<button class="btn btn-prev btn-small" id="btnPrevWizard">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.DAPrev}
		</button>
		<button class="btn btn-success btn-next btn-small" data-last="${uiLabelMap.DAFinish}" id="btnNextWizard">
			${uiLabelMap.DANext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
	</div>
</div>
<div style="position:relative">
	<div id="info_loader" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 900;" class="jqx-rc-all jqx-rc-all-olbius">
		<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div style="float: left;">
				<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
				<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
			</div>
		</div>
	</div>
</div>
<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/fuelux/fuelux.wizard.min.js"></script>
<script src="/delys/images/js/additional-methods.min.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>
<script src="/delys/images/js/jquery.maskedinput.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/delys/images/js/sales/salesCommon.js"></script>
<script type="text/javascript">
	$(function() {
	    $("#jqxNotification").jqxNotification({ width: '100%', appendContainer: "#container", opacity: 0.9, autoClose: false, template: "info" });
		
		$('[data-rel=tooltip]').tooltip();
	
		$(".wizard-steps li").click(function(e){
			var target = $(this).attr('data-target');
			var wiz = $('#fuelux-wizard').data('wizard');
			currentStep = wiz.currentStep;
			if (currentStep == 1) {
				return;
			} else if (currentStep == 2) {
				if (target == "#step1") {
				    $("#step-title").html("${uiLabelMap.DAAbbPromotionInfomation}");
				}
			}
		});
		
		var $validation = false;
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			//if(info.step == 1 && $validation) {
			//	if(!$('#validation-form').valid()) return false;
			//}
			if ((info.step == 1) && (info.direction == "next")) {
				$('#container').empty();
				if(!$('#editProductPromo').jqxValidator('validate')) return false;
				$("#step-title").html("${uiLabelMap.DAAbbPromotionRules}");
			} else if ((info.step == 2) && (info.direction == "previous")) {
        		$("#step-title").html("${uiLabelMap.DAAbbPromotionInfomation}");
			}
		}).on('finished', function(e) {
			if ($('[id^="ruleName_o_"]') == undefined || $('[id^="ruleName_o_"]').length <= 0) {
				bootbox.dialog("${uiLabelMap.DAYouNotYetCreateRule}!", [{
					"label" : "OK",
					"class" : "btn-small btn-primary",
					}]
				);
				return false;
			}
			if(!$('#editProductPromoRuleCondAction').jqxValidator('validate')) return false;
			
			<#if productPromoId?has_content && productPromo?exists>
				var message = "${uiLabelMap.DAAreYouSureUpdate}";
			<#else>
				var message = "${uiLabelMap.DAAreYouSureCreate}";
			</#if>
			bootbox.confirm(message, function(result) {
				if(result) {
					$("#btnPrevWizard").addClass("disabled");
					$("#btnNextWizard").addClass("disabled");
					
					//window.location.href = "processOrderSales";
					var fromDateL;
					var thruDateL;
					if ($("#fromDate").jqxDateTimeInput('getDate') != null) fromDateL = $("#fromDate").jqxDateTimeInput('getDate').getTime();
					if ($("#thruDate").jqxDateTimeInput('getDate') != null) thruDateL = $("#thruDate").jqxDateTimeInput('getDate').getTime();
					var data = $("#editProductPromo").serialize();
					if (fromDateL != null) data += "&fromDate=" + fromDateL;
					if (thruDateL != null) data += "&thruDate=" + thruDateL;
					if ($("#useLimitPerOrder").jqxNumberInput('getDecimal') != null) data += "&useLimitPerOrder=" + $("#useLimitPerOrder").jqxNumberInput('getDecimal');
					if ($("#useLimitPerCustomer").jqxNumberInput('getDecimal') != null) data += "&useLimitPerCustomer=" + $("#useLimitPerCustomer").jqxNumberInput('getDecimal');
					if ($("#useLimitPerPromotion").jqxNumberInput('getDecimal') != null) data += "&useLimitPerPromotion=" + $("#useLimitPerPromotion").jqxNumberInput('getDecimal');
					
					data += "&" + $("#editProductPromoRuleCondAction").serialize();
					
					var roleTypeIds = $("#roleTypeIds").jqxComboBox('getSelectedItems');
					if (roleTypeIds != undefined && roleTypeIds != null && !(/^\s*$/.test(roleTypeIds))) {
						for (var i = 0; i < roleTypeIds.length; i++) {
							var item = roleTypeIds[i];
						 	if (item != null) data += "&roleTypeIds=" + item.value;
						}
					}
					
					var productStoreIds = $("#productStoreIds").jqxComboBox('getSelectedItems');
					if (productStoreIds != undefined && productStoreIds != null && !(/^\s*$/.test(productStoreIds))) {
						for (var i = 0; i < productStoreIds.length; i++) {
							var item = productStoreIds[i];
						 	if (item != null) data += "&productStoreIds=" + item.value;
						}
					}
					
					$('[id^="productIdListCond"]').each(function(i, obj) {
					    var productIdListCond = $(obj).jqxComboBox('getSelectedItems');
					    var id = $(obj).attr("id");
						if (productIdListCond != undefined && productIdListCond != null && !(/^\s*$/.test(productIdListCond))) {
							for (var i = 0; i < productIdListCond.length; i++) {
								var item = productIdListCond[i];
							 	if (item != null) data += "&" + id + "=" + item.value;
							}
						}
					});
					$('[id^="productCatIdListCond"]').each(function(i, obj) {
					    var productCatIdListCond = $(obj).jqxComboBox('getSelectedItems');
					    var id = $(obj).attr("id");
						if (productCatIdListCond != undefined && productCatIdListCond != null && !(/^\s*$/.test(productCatIdListCond))) {
							for (var i = 0; i < productCatIdListCond.length; i++) {
								var item = productCatIdListCond[i];
							 	if (item != null) data += "&" + id + "=" + item.value;
							}
						}
					});
					$('[id^="productIdListAction"]').each(function(i, obj) {
					    var productIdListAction = $(obj).jqxComboBox('getSelectedItems');
					    var id = $(obj).attr("id");
						if (productIdListAction != undefined && productIdListAction != null && !(/^\s*$/.test(productIdListAction))) {
							for (var i = 0; i < productIdListAction.length; i++) {
								var item = productIdListAction[i];
							 	if (item != null) data += "&" + id + "=" + item.value;
							}
						}
					});
					<#--
					$('[id^="productCatIdListAction"]').each(function(i, obj) {
					    var productCatIdListAction = $(obj).jqxComboBox('getSelectedItems');
					    var id = $(obj).attr("id");
						if (productCatIdListAction != undefined && productCatIdListAction != null && !(/^\s*$/.test(productCatIdListAction))) {
							for (var i = 0; i < productCatIdListAction.length; i++) {
								var item = productCatIdListAction[i];
							 	if (item != null) data += "&" + id + "=" + item.value;
							}
						}
					});
					-->
					
					<#if productPromoId?has_content && productPromo?exists>
						var url = "updateProductPromoAdvance";
					<#else>
						var url = "createProductPromoAdvance";
					</#if>
					$.ajax({
			            type: "POST", 
			            url: url,
			            data: data,
			            beforeSend: function () {
							$("#info_loader").show();
						}, 
			            success: function (data) {
			            	if (data.thisRequestUri == "json") {
			            		var errorMessage = "";
						        if (data._ERROR_MESSAGE_LIST_ != null) {
						        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
						        		//errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
						        		errorMessage += "<p>" + data._ERROR_MESSAGE_LIST_[i] + "</p>";
						        	}
						        }
						        if (data._ERROR_MESSAGE_ != null) {
						        	//errorMessage += "<p><b>${uiLabelMap.DAErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
						        	errorMessage = data._ERROR_MESSAGE_;
						        }
						        if (errorMessage != "") {
						        	$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'error'});
						        	$("#jqxNotification").html(errorMessage);
						        	$("#jqxNotification").jqxNotification("open");
						        	$("#btnPrevWizard").removeClass("disabled");
									$("#btnNextWizard").removeClass("disabled");
						        } else {
						        	if (data.productPromoId != undefined && data.productPromoId != null && !(/^\s*$/.test(data.productPromoId))) {
						        		window.location.href = "viewProductPromo?productPromoId=" + data.productPromoId;
						        	}
					        		/*
					        		$('#container').empty();
						        	$('#jqxNotification').jqxNotification({ template: 'info'});
						        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
						        	$("#jqxNotification").jqxNotification("open");
					        		*/
						        }
			            	}
			            },
			            error: function () {
			                $("#btnPrevWizard").removeClass("disabled");
							$("#btnNextWizard").removeClass("disabled");
			            },
			            complete: function() {
					        $("#info_loader").hide();
					    }
			        });
				}
			});
			
		}).on('stepclick', function(e){
			//return false;//prevent clicking on steps
		});
		/*
		rule: function (input) {
   			var value = $(input).val();
   			if (isNaN(value)) return false;
   			else return true;
   		}
		*/
		$('#editProductPromoRuleCondAction').jqxValidator({
			position: 'bottom',
			rules: [
        		{input: '[id^="condValue"]', message: '${uiLabelMap.DANotValidateDataOnlyPositiveInteger}', action: 'keyup, blur', 
	           		rule: function (input) {
	           			var value = $(input).val();
	           			if (isNaN(value)) {
	           				return false;
	           			} else {
	           				var valueN = ~~Number(value);
	           				if (valueN < 0) return false;
	           			}
	           			return true;
	           		}
				},
				{input: '[id^="quantity"]', message: '${uiLabelMap.DANotValidateDataOnlyPositiveInteger}', action: 'keyup, blur', 
	           		rule: function (input) {
	           			var value = $(input).val();
	           			if (isNaN(value)) {
	           				return false;
	           			} else {
	           				var valueN = ~~Number(value);
	           				if (valueN < 0) return false;
	           			}
	           			return true;
	           		}
				},
				{input: '[id^="amount"]', message: '${uiLabelMap.DANotValidateDataOnlyPositiveInteger}', action: 'keyup, blur', 
	           		rule: function (input) {
	           			var value = $(input).val();
	           			if (isNaN(value)) {
	           				return false;
	           			} else {
	           				var valueN = ~~Number(value);
	           				if (valueN < 0) return false;
	           			}
	           			return true;
	           		}
				},
			]
		});
		$('#editProductPromo').jqxValidator({
			position: 'bottom',
        	rules: [
        		{input: '#productPromoId', message: '${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
        			function (input, commit) {
        				var value = $(input).val();
						if(!(/^\s*$/.test(value)) && !(/^[a-zA-Z0-9_]+$/.test(value))){
							return false;
						}
						return true;
					}
				},
				{input: '#promoText', message: '${uiLabelMap.DAThisFieldSizeNotGreaterThan10000}', action: 'blur', rule: 
        			function (input, commit) {
						var sizeData = $(input).val().length;
						if (sizeData > 10000) {
							return false;
						}
						return true;
					}
				},
				{input: '#paymentMethod', message: '${uiLabelMap.DAThisFieldSizeNotGreaterThan255}', action: 'blur', rule: 
        			function (input, commit) {
						var sizeData = $(input).val().length;
						if (sizeData > 255) {
							return false;
						}
						return true;
					}
				},
        		{input: '#promoName', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
        			function (input, commit) {
						if($('#promoName').val() == null || $('#promoName').val() == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#productPromoTypeId', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($('#productPromoTypeId').val() == null || $('#productPromoTypeId').val() == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#roleTypeIds', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if(!isNotEmptyComboBox(input)){
							return false;
						}
						return true;
					}
				},
				{input: '#productStoreIds', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if(!isNotEmptyComboBox(input)){
							return false;
						}
						return true;
					}
				},
				{input: '#fromDate', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($('#fromDate').jqxDateTimeInput('getDate') == null || $('#fromDate').jqxDateTimeInput('getDate') == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#fromDate', message: '${StringUtil.wrapString(uiLabelMap.DARequiredValueGreaterThanOrEqualToToday)}', action: 'blur', rule: 
					function (input, commit) {
						var now = new Date();
						now.setHours(0,0,0,0);
		        		if(input.jqxDateTimeInput('getDate') < now){
		        			return false;
		        		}
		        		return true;
		    		}
				},
				{input: '#promoSalesTargets', message: '${uiLabelMap.DANotValidateDataOnlyNumber}', action: 'blur', 
	           		rule: function (input) {
	           			var value = $(input).val();
	           			if (isNaN(value)) return false;
	           			else return true;
	           		}
				}
        	]
        });
	})
</script>
