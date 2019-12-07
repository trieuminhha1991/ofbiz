<#-- upgrade from ../supervisor/agentDetail.ftl -->
<#--
<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
-->
<style>
	.row-fluid {
	    min-height: 30px;
	}
	.text-header {
		color: black !important;
	}
	.boder-all-profile .label {
	    font-size: 14px;
	    text-shadow: none;
	    background-color: #3a87ad !important;
		margin: 0px;
		color: white !important;
    	line-height: 14px !important;
		margin-top: -20px;
	}
</style>

<#if !activeTab?exists><#assign activeTab = ""/></#if>
<#if !agentInfo?exists><#assign agentInfo = {}/></#if>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<li<#if activeTab == "" || activeTab == "partyoverview-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#partyoverview-tab">${uiLabelMap.BSOverview}</a>
									</li>
									<li<#if activeTab == "partyowner-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#partyowner-tab">${uiLabelMap.BERepresentative}</a>
									</li>
									<#--
									<li<#if activeTab == "salesman-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#salesman-tab">${uiLabelMap.BSSalesman}</a>
									</li>
									<li<#if activeTab == "retailoutlet-tab"> class="active"</#if>>
										<a data-toggle="tab" href="#retailoutlet-tab">${uiLabelMap.BSRetailOutlet}</a>
									</li>
									-->
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
                            <#if agentInfo.partyId?exists>
                                <a href="javascript:void(0)" data-rel="tooltip"
                                   onclick="OlbAgentDetailOnMap.openMapDetail('${agentInfo.partyId?default("")}','${agentInfo.geoPointId?default("")}','${agentInfo.address1?default("")}', '${agentInfo.latitude?default('')}', '${agentInfo.longitude?default('')}')"
                                   title="${uiLabelMap.BSTitleLocaltionAgent}" data-placement="left" class="button-action">
                                    <i class="fa fa-globe open-sans"></i>
                                </a>
                            </#if>
							<#if agentInfo.partyId?exists && hasOlbPermission("MODULE", "${permission}", "UPDATE")>
								<a href="<@ofbizUrl>${linkEdit}?partyId=${agentInfo.partyId}&sub=${parameters.sub?if_exists}</@ofbizUrl>" data-rel="tooltip" 
					    			title="${uiLabelMap.BSEdit}" data-placement="left" class="button-action">
					    			<i class="icon-edit open-sans"></i>
					    		</a>
							</#if>
						</div>
					</div>
					<script type="text/javascript">
						$('[data-rel=tooltip]').tooltip();
					</script>
				</div>
			</div>
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">
					
						<#include "retailOutletViewInfo.ftl"/>
						
						<#include "retailOutletViewOwner.ftl">
						
						<#--
						<div id="salesman-tab" class="tab-pane<#if activeTab == "salesman-tab"> active</#if>">
							<div class="row-fluid">
								<div class="span12">
									${setContextField("partyIdFrom", agentInfo.partyId)}
									${screens.render("component://basesalesmtl/widget/DistributorScreens.xml#ListSalesmanAssignedInner")}
								</div>
							</div>
						</div>
						
						<div id="retailoutlet-tab" class="tab-pane<#if activeTab == "retailoutlet-tab"> active</#if>">
							<div class="row-fluid">
								<div class="span12">
									${setContextField("partyIdFrom", agentInfo.partyId)}
									${setContextField("hiddenDistributor", "true")}
									${screens.render("component://basesalesmtl/widget/SupervisorScreens.xml#RetailOutletListInner")}
								</div>
							</div>
						</div>
						-->
						
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
					</div>
				</div><!--/widget-main-->
			</div><!--/widget-body-->
		</div><!--/widget-box-->
	</div><!-- /span12 -->
</div><!--/row-->

<#include "popup/viewAgentDetailOnMap.ftl">
<div id="containerNestedSlide" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotificationNestedSlide" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>

<#assign listCurrencyUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "CURRENCY_MEASURE"), null, null, null, false) />
<#assign listUserLogin = delegator.findList("UserLogin", null, Static["org.ofbiz.base.util.UtilMisc"].toSet("userLoginId"), null, null, false) />
<#assign listSubsidiaries = Static["com.olbius.basehr.util.SecurityUtil"].getPartiesByRoles("SUBSIDIARY", delegator)>
<#assign listStatusItem = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "PARTY_STATUS"), null, null, null, false) />

<@jqGridMinimumLib/>
<@jqOlbCoreLib />
<script type="text/javascript">
	<#if parameters.partyId?exists>
	$($(".breadcrumb").children()[2]).html("<span class='divider'><i class='icon-angle-right'></i></span>${StringUtil.wrapString(uiLabelMap.BSViewAgent)}");
	$('[data-rel=tooltip]').tooltip();
	</#if>
	var partyIdPram = "${parameters.partyId?if_exists}";
	var mapSubsidiaries = {<#if listSubsidiaries?exists><#list listSubsidiaries as subsidiary>
		<#assign groupName= Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, subsidiary, false)>
			"${subsidiary?if_exists}": "${StringUtil.wrapString(groupName?if_exists)}",
		</#list></#if>};
	var mapCurrencyUom = {<#if listCurrencyUom?exists><#list listCurrencyUom as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.description?if_exists)}",
	</#list></#if>};
	var mapStatusItem = {<#if listStatusItem?exists><#list listStatusItem as item>
	"${item.statusId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
	</#list></#if>};
	var BSNewAgentNotify = "${StringUtil.wrapString(uiLabelMap.BSNewAgentNotify)}";
	
	$(function(){
		OlbRetailOutletViewTotal.init();
	});
	if (typeof(OlbRetailOutletViewTotal) == "undefined") {
		var OlbRetailOutletViewTotal = (function(){
			var init = function () {
				initElement();
				initEvent();
			};
			var initElement = function(){
				jOlbUtil.notification.create($("#containerNestedSlide"), $("#jqxNotificationNestedSlide"));
			};
			var initEvent = function() {
				$("#btnAccept").click(function() {
					$("#btnAccept").attr("disabled", true);
					$.ajax({
						type: 'POST',
						url: "setAgentStatus",
						data: {
							partyId: partyIdPram,
							statusId: "PARTY_ENABLED"
						},
						beforeSend: function(){
							$("#loader_page_common").show();
						},
						success: function(data){
							jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
							        	$('#containerNestedSlide').empty();
							        	$('#jqxNotificationNestedSlide').jqxNotification({ template: 'info'});
							        	$("#jqxNotificationNestedSlide").html(errorMessage);
							        	$("#jqxNotificationNestedSlide").jqxNotification("open");
							        	return false;
									}, function(){
										$('#containerNestedSlide').empty();
							        	$('#jqxNotificationNestedSlide').jqxNotification({ template: 'info'});
							        	$("#jqxNotificationNestedSlide").html("${uiLabelMap.wgupdatesuccess}");
							        	$("#jqxNotificationNestedSlide").jqxNotification("open");
							        	
							        	$("#accept-wrapper").addClass("hide");
										$("#statusTitle").text(mapStatusItem["PARTY_ENABLED"]);
									}
							);
						},
						error: function(data){
							alert("Send request is error");
						},
						complete: function(data){
							$("#loader_page_common").hide();
							location.reload();
						},
					});
				});
				
				$("#btnReject").click(function() {
					$("#btnReject").attr("disabled", true);
					$.ajax({
						type: 'POST',
						url: "setAgentStatus",
						data: {
							partyId: partyIdPram,
							statusId: "PARTY_DISABLED"
						},
						beforeSend: function(){
							$("#loader_page_common").show();
						},
						success: function(data){
							jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
							        	$('#containerNestedSlide').empty();
							        	$('#jqxNotificationNestedSlide').jqxNotification({ template: 'info'});
							        	$("#jqxNotificationNestedSlide").html(errorMessage);
							        	$("#jqxNotificationNestedSlide").jqxNotification("open");
							        	return false;
									}, function(){
										$('#containerNestedSlide').empty();
							        	$('#jqxNotificationNestedSlide').jqxNotification({ template: 'info'});
							        	$("#jqxNotificationNestedSlide").html("${uiLabelMap.wgupdatesuccess}");
							        	$("#jqxNotificationNestedSlide").jqxNotification("open");
							        	
							        	$("#reject-wrapper").addClass("hide");
										$("#statusTitle").text(mapStatusItem["PARTY_DISABLED"]);
									}
							);
						},
						error: function(data){
							alert("Send request is error");
						},
						complete: function(data){
							$("#loader_page_common").hide();
							location.reload();
						},
					});
				});
			};
			return {
				init: init,
			};
		}());
	}
</script>