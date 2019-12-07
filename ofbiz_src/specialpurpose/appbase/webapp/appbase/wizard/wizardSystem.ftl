<#if layoutSettings.VT_HDR_IMAGE_URL?has_content>
<#assign logoGroup = layoutSettings.VT_HDR_IMAGE_URL.get(0)/>
</#if>
<style type="text/css">
	.setup-block-item {
		<#--
		-moz-border-radius-topleft:15px;
		-webkit-border-top-left-radius:15px;
		border-top-left-radius: 15px;
		-moz-border-radius-topright:15px;
		-webkit-border-top-right-radius:15px;
		border-top-right-radius: 15px;
		-->
		-moz-border-radius:15px;
		-webkit-border-radius:15px;
		border-radius: 15px;
		
		border:5px solid #ccc; 
		background-color: #fff; 
		display:inline-block;
		padding: 10px;
		cursor: pointer;
	}
	.setup-block-item.alert-success {
	    background-color: #dff0d8;
    	border-color: #d6e9c6;
	}
	.setup-block-item.alert-error {
        background-color: #f2dede;
    	border-color: #eed3d7;
	}
	.footer {
		position: relative;
	}
	.footer button {
		color: #FFF;
	    padding: 8px 10px;
	    margin-left: 5px;
	    line-height: 15px;
	    border: none;
	}
	.setup-block-line {
		display:inline-block;
		width: 120px;
		position: relative;
	}
	.setup-block-line .line:before {
	    display: block;
	    content: "";
	    width: 100%;
	    height: 1px;
	    font-size: 0;
	    overflow: hidden;
	    border-top: 4px solid #ced1d6;
	    position: relative;
	    top: 0px;
	    z-index: 1;
	}
	.setup-block-line .arrow:after {
		display: block;
	    content: "";
	    width: 0.6em;
	    height: 0.6em;
	    border-right: 0.3em solid #ced1d6;
	    border-top: 0.3em solid #ced1d6;
	    transform: rotate(45deg);
	    position: absolute;
	    right: 0;
	    top: -0.3em;
	    z-index: 10;
	}
	.steps-container {
		width: 910px;
	    margin: 0 auto;
	}
	.steps-container .setup-block-item {
		width: 100px;
		text-align: center
	}
</style>
<div id="container" class="container-noti"></div>
<div id="jqxNotification">
    <div id="notificationContent">
    </div>
</div>

<div id="main-content">
	<div class="row-fluid">
		<div class="span12">
			<div style="width: 80%; margin: 20px auto;">
				<div class="row-fluid">
					<div style="text-align: center;">
						<h1>
							<span class="logo-group-name">
								<img width="57" height="33" style="vertical-align:top" src="<@ofbizContentUrl>${StringUtil.wrapString(logoGroup?if_exists)}</@ofbizContentUrl>"/>
							</span>
							<#if locale?exists && locale == "vi">
								<span class="white">${uiLabelMap.AppBaseWelcomeTitleApplicationLabel}</span>
								<span class="red">${uiLabelMap.AppBaseWelcomeTitleApplicationName}</span>
							<#else>
								<span class="red">${uiLabelMap.AppBaseWelcomeTitleApplicationName}</span>
								<span class="white">${uiLabelMap.AppBaseWelcomeTitleApplicationLabel}</span>
							</#if>
						</h1>
						<h4 class="blue">${uiLabelMap.AppBaseWelcomeTitleCompanyName}</h4>
					</div>
				</div>
				<div style="padding: 16px; background: #f7f7f7;">
					<#--<h4 class="smaller blue" style="text-transform: uppercase;">${uiLabelMap.AppBasePageTitleWizardSystem}</h4>-->
					<div class="alert alert-block alert-success">
						<button type="button" class="close" data-dismiss="alert">
							<i class="icon-remove"></i>
						</button>
						<i class="icon-ok green"></i>
						${uiLabelMap.AppBaseWelcomeTitleWizardSystem}
					</div>
					<div class="row-fluid">
						<div class="steps-container">
							<div id="step1" class="setup-block-item" title="${uiLabelMap.BSOrganization}">
								${uiLabelMap.BSOrganization}
							</div>
							<div class="setup-block-line"><div class="line"></div><div class="arrow"></div></div>
							
							<div id="step2" class="setup-block-item" title="${uiLabelMap.BSSubsidiary}">
								${uiLabelMap.BSSubsidiary}
							</div>
							<div class="setup-block-line"><div class="line"></div><div class="arrow"></div></div>
							
							<div id="step3" class="setup-block-item" title="${uiLabelMap.BSHRManager}">
								${uiLabelMap.BSAbbHRManager}
							</div>
							<div class="setup-block-line"><div class="line"></div><div class="arrow"></div></div>
							
							<div id="step4" class="setup-block-item" title="${uiLabelMap.BSAdministrator}">
								${uiLabelMap.BSAdministrator}
							</div>
						</div>
					</div>
		   			<div class="footer">
		   				<div class="row-fluid">
		   					<div class="span12">
			   					<div class="pull-right margin-top10">
						   			<button id="alterGoToSystem" style="display:none" class='btn btn-primary'><i class='fa-check'></i>${uiLabelMap.BSSetupHasCompletedGoToSystem}</button>
						   		</div>
			   				</div>
		   				</div>
		   			</div>
				</div>
				<#--
				<div class="toolbar clearfix" style="background: #5090c1; border-top: 2px solid #597597;">
					<button class="width-35 pull-right btn btn-small btn-primary"><i class="icon-key"></i>Clear</button>
				</div>
				-->
			</div>
		</div><!--/span-->
	</div><!--/row-->
</div>

<div style="position:relative">
	<div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
		<div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div>
				<div class="jqx-grid-load"></div>
				<span>${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>

<#assign defaultCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)!/>
<#assign countryGeoId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCountryGeo(delegator)!/>

<#include "wizardSetupOrganization.ftl">
<#include "wizardSetupDepartment.ftl">
<#include "wizardSetupHrManager.ftl">
<#include "wizardSetupOlbiusAdmin.ftl">

<#include "component://widget/templates/jqwLocalization.ftl"/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcalendar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatetimeinput.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.filter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.filter.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.sort.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.sort.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.selection.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.selection.extend.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.export.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.aggregates.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.columnsreorder.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.columnsresize.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.grouping.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.pager.js"></script> 
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.edit.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxgrid.edit.extend.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>

<@jqOlbCoreLib hasGrid=true hasDropDownList=true hasComboBox=true hasValidator=true/>
<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
	setUiLabelMap("validContainSpecialCharacter", "${StringUtil.wrapString(uiLabelMap.validContainSpecialCharacter)}");
	setUiLabelMap("validFieldRequire", "${StringUtil.wrapString(uiLabelMap.validFieldRequire)}");
	setUiLabelMap("validRequiredValueGreatherOrEqualDateTimeToDay", "${StringUtil.wrapString(uiLabelMap.validRequiredValueGreatherOrEqualDateTimeToDay)}");
	setUiLabelMap("validStartDateMustLessThanOrEqualFinishDate", "${StringUtil.wrapString(uiLabelMap.validStartDateMustLessThanOrEqualFinishDate)}");
	setUiLabelMap("validRequiredValueGreatherOrEqualToDay", "${StringUtil.wrapString(uiLabelMap.validRequiredValueGreatherOrEqualToDay)}");
	setUiLabelMap("filterchoosestring", "${StringUtil.wrapString(uiLabelMap.filterchoosestring)}");
	setUiLabelMap("wgupdatesuccess", "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
	
	<#assign listCurrencyUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
	var listCurrencyUom = [
	<#if listCurrencyUom?exists>
		<#list listCurrencyUom as item>
		{	uomId: "${item.uomId?if_exists}",
			description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
		},
		</#list>
	</#if>
	];
	
	$(function(){
		OlbWizardSystem.init();
		
		<#if organization?has_content>
			OlbWizardSystem.markSuccess("step1");
    		OlbWizardStep1.setValue({
    				"partyId": "${organization.partyId}", 
    				"partyName" : "${StringUtil.wrapString(organization.groupName?if_exists)}",
    				"phoneNumber" : "${StringUtil.wrapString(organization.phoneNumber?if_exists)}",
    			});
		</#if>
		<#if isHaveSubsidiary?has_content>
			OlbWizardSystem.markSuccess("step2");
			<#if isHaveSubsidiary.systemValue == "false">OlbWizardStep2.setValue({noHaveSubsidiary: true});</#if>
		</#if>
		<#if isExistHrManager?exists && isExistHrManager>
			OlbWizardSystem.markSuccess("step3");
		</#if>
		<#if isExistOlbiusAdmin?exists && isExistOlbiusAdmin>
			OlbWizardSystem.markSuccess("step4");
		</#if>
	});
	var OlbWizardSystem = (function(){
		var init = function(){
			initElement();
			initEvent();
			
			OlbWizardStep1.init();
			OlbWizardStep2.init();
			OlbWizardStep3.init();
			OlbWizardStep4.init();
		};
		var initElement = function(){
			jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
		};
		var initEvent = function(){
			$("#alterGoToSystem").on("click", function(){
				window.location.href = 'main';
			});
		};
		var markSuccess = function(stepId){
			var tt_step = $("#" + stepId);
    		tt_step.addClass("alert-success");
    		tt_step.prepend($('<i class="icon-ok green"></i>'));
		}
		var checkSetupComplete = function(){
			$("#alterGoToSystem").hide();
			$.ajax({
                type: "POST",
                url: "checkSetupComplete",
                beforeSend: function(){
                    $("#loader_page_common").show();
                }, 
                success: function(data){
                    jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
			        	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'error'});
			        	$("#jqxNotification").html(errorMessage);
			        	$("#jqxNotification").jqxNotification("open");
					}, function(data){
                    	$('#container').empty();
			        	$('#jqxNotification').jqxNotification({ template: 'info'});
			        	$("#jqxNotification").html(uiLabelMap.wgupdatesuccess);
			        	$("#jqxNotification").jqxNotification("open");
			        	if (data.isComplete) {
			        		$("#alterGoToSystem").show();
			        		return true;
			        	}
                    });
                },
                error: function(){
                    alert("Send to server is false!");
                },
                complete: function(){
                	$("#loader_page_common").hide();
                }
            });
		}
		return {
			init: init,
			markSuccess: markSuccess,
			checkSetupComplete: checkSetupComplete
		};
	}());
</script>