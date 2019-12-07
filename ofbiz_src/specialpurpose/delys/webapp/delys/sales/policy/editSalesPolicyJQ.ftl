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
		${uiLabelMap.DAAbbPolicyInfomation} 
	</h4>
	<div id="fuelux-wizard" class="row-fluid hide span8" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-mini">
			<li data-target="#step1" class="active">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.DAAbbPolicyInfomation}" data-placement="bottom">1</span>
			</li>

			<li data-target="#step2">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.DAAbbSalesPolicyRules}" data-placement="bottom">2</span>
			</li>
		</ul>
	</div>
	<div class="span1 align-right" style="padding-top:5px">
		<#--
		<#if salesPolicyId?has_content && salesPolicy?exists>
			<a href="<@ofbizUrl>viewSalesPromo?salesPolicyId=${salesPolicy.salesPolicyId?if_exists}</@ofbizUrl>" data-rel="tooltip" title="${uiLabelMap.DAViewDetail} ${salesPolicy.salesPolicyId?if_exists}" data-placement="bottom" class="no-decoration">
				<i class="fa-search open-sans open-sans-index" style="font-size:16pt"></i>
			</a>
		</#if>
		-->
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
			<#include "editSalesPolicyInfoGeneral.ftl"/>
		</div>

		<div class="step-pane" id="step2">
			<#include "editSalesPolicyRulesJQ.ftl"/>
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
<div>
	<div style="text-align:left">
		<#if salesPolicy?exists && ("SALES_PL_CANCELLED" != salesPolicy.statusId && "SALES_PL_ACCEPTED" != salesPolicy.statusId)>
		<span class="widget-toolbar none-content">
			<a class="btn btn-primary btn-mini" href="javascript:acceptSalesPolicy();" style="font-size:13px; padding:0 8px">
				<i class="icon-ok open-sans">${uiLabelMap.DAApproveAccept}</i></a>
          	<a class="btn btn-danger btn-mini" href="javascript:cancelSalesPolicy();" style="font-size:13px; padding:0 8px">
				<i class="icon-remove open-sans">${uiLabelMap.DAApproveCancel}</i></a>
			<form name="PolicyAccept" method="post" action="<@ofbizUrl>changePolicyStatus</@ofbizUrl>">
            	<input type="hidden" name="statusId" value="SALES_PL_ACCEPTED">
                <input type="hidden" name="salesPolicyId" value="${salesPolicy.salesPolicyId?if_exists}">
          	</form>
			<form name="PolicyCancel" method="post" action="<@ofbizUrl>changePolicyStatus</@ofbizUrl>">
            	<input type="hidden" name="statusId" value="SALES_PL_CANCELLED">
                <input type="hidden" name="salesPolicyId" value="${salesPolicy.salesPolicyId?if_exists}">
                <input type="hidden" name="changeReason" id="changeReason" value="" />
          	</form>
		</span>
		</#if>
	<#--security.hasPermission("SALES_POLICY_APPROVE", session) && -->
		<#--<#if ("SALES_PL_CANCELLED" != salesPolicy.statusId)>
			<#if (salesPolicy.thruDate?exists && salesPolicy.thruDate &gt; nowTimestamp) || !(salesPolicy.thruDate?exists)>
				<#if salesPolicy.statusId?exists && salesPolicy.statusId == "SALES_PL_CREATED">
					<span class="widget-toolbar none-content">
						<a class="btn btn-primary btn-mini" href="javascript:acceptSalesPolicy();" style="font-size:13px; padding:0 8px">
							<i class="icon-ok open-sans">${uiLabelMap.DAApproveAccept}</i></a>
		              	<a class="btn btn-primary btn-mini" href="javascript:cancelSalesPolicy();" style="font-size:13px; padding:0 8px">
							<i class="icon-remove open-sans">${uiLabelMap.DAApproveCancel}</i></a>
						<form name="PolicyAccept" method="post" action="<@ofbizUrl>changePolicyStatus</@ofbizUrl>">
		                	<input type="hidden" name="salesPolicyId" value="SALES_PL_ACCEPTED">
			                <input type="hidden" name="productPromoId" value="${salesPolicy.productPromoId?if_exists}">
		              	</form>
						<form name="PolicyCancel" method="post" action="<@ofbizUrl>changePolicyStatus</@ofbizUrl>">
		                	<input type="hidden" name="statusId" value="SALES_PL_CANCELLED">
			                <input type="hidden" name="salesPolicyId" value="${salesPolicy.productPromoId?if_exists}">
			                <input type="hidden" name="changeReason" id="changeReason" value="" />
		              	</form>
					</span>
				</#if>
				<span class="widget-toolbar none-content">
					<form name="updatePromoThruDate" id="updatePromoThruDate" method="POST" action="<@ofbizUrl>updatePromoThruDate</@ofbizUrl>">
						<input type="hidden" name="productPromoId" value="${salesPolicy.productPromoId?if_exists}" />
						<label for="thruDate">${uiLabelMap.DAThroughDate}:&nbsp;&nbsp;&nbsp;</label>
						<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDate" value="${salesPolicy.thruDate?if_exists}" event="" action="" className="" alert="" 
							title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
							timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
							classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
							pmSelected="" compositeType="" formName=""/>
						<button class="btn btn-primary btn-mini" type="button" onclick="javascript:updatePromoThruDate2();">${uiLabelMap.DAUpdate}</button>
					</form>
				</span>
			<#else>
				<span style="color:#D7432E">${uiLabelMap.DAThisRecordHasExpired}</span>
			</#if>
		</#if>-->
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
				    $("#step-title").html("${uiLabelMap.DAAbbPolicyInfomation}");
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
				if(!$('#editSalesPolicy').jqxValidator('validate')) return false;
				$("#step-title").html("${uiLabelMap.DAAbbSalesPolicyRules}");
			} else if ((info.step == 2) && (info.direction == "previous")) {
        		$("#step-title").html("${uiLabelMap.DAAbbPolicyInfomation}");
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
			if(!$('#editSalesPolicyRuleCondAction').jqxValidator('validate')) return false;
			
			<#if salesPolicyId?has_content && salesPolicy?exists>
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
					var data = $("#editSalesPolicy").serialize();
					if (fromDateL != null) data += "&fromDate=" + fromDateL;
					if (thruDateL != null) data += "&thruDate=" + thruDateL;
					data += "&" + $("#editSalesPolicyRuleCondAction").serialize();
					
					var roleTypeIds = $("#roleTypeIds").jqxComboBox('getSelectedItems');
					if (roleTypeIds != undefined && roleTypeIds != null && !(/^\s*$/.test(roleTypeIds))) {
						for (var i = 0; i < roleTypeIds.length; i++) {
							var item = roleTypeIds[i];
						 	if (item != null) data += "&roleTypeIds=" + item.value;
						}
					}
					
					<#--
					var productStoreIds = $("#productStoreIds").jqxComboBox('getSelectedItems');
					if (productStoreIds != undefined && productStoreIds != null && !(/^\s*$/.test(productStoreIds))) {
						for (var i = 0; i < productStoreIds.length; i++) {
							var item = productStoreIds[i];
						 	if (item != null) data += "&productStoreIds=" + item.value;
						}
					}
					-->
					
					$('[id^="paymentParty"]').each(function(i, obj) {
					    data += getDataStrUrlComboBoxOne(i, obj);
					});
					$('[id^="salesStatementTypeId"]').each(function(i, obj) {
					    data += getDataStrUrlComboBoxOne(i, obj);
					});
					$('[id^="productIdListCond"]').each(function(i, obj) {
					    data += getDataStrUrlComboBox(i, obj);
					});
					$('[id^="productCatIdListCond"]').each(function(i, obj) {
					    data += getDataStrUrlComboBox(i, obj);
					});
					$('[id^="productIdListAction"]').each(function(i, obj) {
					    data += getDataStrUrlComboBox(i, obj);
					});
					$('[id^="productCatIdListAction"]').each(function(i, obj) {
					    data += getDataStrUrlComboBox(i, obj);
					});
					
					data += getDataStrUrlComboBoxById("geoIdsInclude");
					data += getDataStrUrlComboBoxById("geoIdsExclude");
					
					<#if salesPolicyId?has_content && salesPolicy?exists>
						var url = "updateSalesPolicyAdvance";
					<#else>
						var url = "createSalesPolicyAdvance";
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
						        	if (data.salesPolicyId != undefined && data.salesPolicyId != null && !(/^\s*$/.test(data.salesPolicyId))) {
						        		window.location.href = "editSalesPolicy?salesPolicyId=" + data.salesPolicyId;
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
		$('#editSalesPolicyRuleCondAction').jqxValidator({
			position: 'bottom',
			rules: [
				{input: '[id^="salesStatementTypeId"]', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
        			function (input, commit) {
						if(!isNotEmptyComboBoxOne(input)){
							return false;
						}
						return true;
					}
				},
				{input: '[id^="paymentParty"]', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
        			function (input, commit) {
						if(!isNotEmptyComboBoxOne(input)){
							return false;
						}
						return true;
					}
				},
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
		$('#editSalesPolicy').jqxValidator({
			position: 'bottom',
        	rules: [
        		{input: '#salesPolicyId', message: '${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
        			function (input, commit) {
        				var value = $(input).val();
						if(!(/^\s*$/.test(value)) && !(/^[a-zA-Z0-9_]+$/.test(value))){
							return false;
						}
						return true;
					}
				},
				{input: '#policyName', message: '${uiLabelMap.DAThisFieldSizeNotGreaterThan10000}', action: 'blur', rule: 
        			function (input, commit) {
						var sizeData = $(input).val().length;
						if (sizeData > 10000) {
							return false;
						}
						return true;
					}
				},
        		{input: '#policyName', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
        			function (input, commit) {
						if($(input).val() == null || /^\s*$/.test($(input).val())){
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
				{input: '#fromDate', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($('#fromDate').jqxDateTimeInput('getDate') == null || $('#fromDate').jqxDateTimeInput('getDate') == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#geoIdsInclude', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if(!isNotEmptyComboBox(input)){
							return false;
						}
						return true;
					}
				},
        	]
        });
	});
	function acceptSalesPolicy() {
		bootbox.confirm("${uiLabelMap.DAAreYouSureAccept}", function(result) {
			if(result) {
				document.PolicyAccept.submit();
			}
		});
	}
	function cancelSalesPolicy() {
		bootbox.confirm("${uiLabelMap.DAAreYouSureCancelNotAccept}", function(result) {
			if(result) {
				document.PolicyCancel.submit();
			}
		});
	}
</script>
<#--
{input: '#productStoreIds', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($('#productStoreIds').val() == null || $('#productStoreIds').val() == ''){
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
-->
