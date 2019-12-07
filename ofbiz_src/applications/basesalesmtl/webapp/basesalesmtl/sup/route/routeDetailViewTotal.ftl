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
<script type="text/javascript">
    var routeId = "${parameters.routeId?if_exists}";
    var salesmanId = "${routeInfo.salesmanId?if_exists}";
	<#assign createdBy = delegator.findOne("PartyNameView", {"partyId" : routeInfo.createdByUserLoginId}, false)!/>
	<#assign manager = delegator.findOne("PartyNameView", {"partyId" : routeInfo.managerId}, false)!/>
    var sortByDay = function(arr) {
        var standard = ["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY"];
        return arr.sort(function(acc, curr) {
            return standard.indexOf(acc.value) - standard.indexOf(curr.value);
        });
    }

    var days = sortByDay([<#if Days?exists><#list Days as day>{
		value: 	'${StringUtil.wrapString(day.dayOfWeek)}',
		description: '${StringUtil.wrapString(day.description)}'
	},</#list></#if>]);
    var dayMap = {};
    <#if Days?exists>
        <#list Days as day>
		dayMap['${StringUtil.wrapString(day.dayOfWeek)}'] = '${StringUtil.wrapString(day.description)}';
        </#list>
    </#if>
    var scheduleRoute = "${routeInfo.scheduleRoute?if_exists?default('')}";
    var scheduleRouteVal = "";
    if (OlbCore.isNotEmpty(scheduleRoute)) {
        var regexp = /\w+/gi;
        var matches = scheduleRoute.match(regexp);
        var dayWeekConverted = matches.map(function (v,i,a){
            return dayMap[v];
        });
        dayWeekConverted.sort();
        scheduleRouteVal = dayWeekConverted.join(", ");
    };
    var routeStatus = '${StringUtil.wrapString(uiLabelMap.BSRouteEnabled)}';
    <#if routeInfo.statusId?exists>
        <#if routeInfo.statusId = "ROUTE_DISABLED">
            routeStatus = '${StringUtil.wrapString(uiLabelMap.BSRouteDisabled)}';
        </#if>
    </#if>

    $(document).ready(function() {
        document.getElementById("info_scheduleRoute").append(scheduleRouteVal);
        document.getElementById("statusTitle").append(routeStatus);
    });
</script>

<#if !activeTab?exists><#assign activeTab = ""/></#if>
<#if !routeInfo?exists><#assign routeInfo = {}/></#if>
<#if manager?exists>
<div class="row-fluid">
    <div class="span12">
        <div class="widget-box transparent" id="recent-box">
            <div class="widget-header" style="border-bottom:none">
                <div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
                    <div class="row-fluid">
                        <div class="span10">
                            <div class="tabbable">
                                <ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
                                    <li<#if activeTab == "" || activeTab == "routeoverview-tab"> class="active"</#if>>
                                        <a data-toggle="tab" href="#routeoverview-tab">${uiLabelMap.BSOverview}</a>
                                    </li>
                                    <li<#if activeTab == "customer-tab"> class="active"</#if>>
                                        <a data-toggle="tab" href="#customer-tab">${uiLabelMap.BSRetailOutletList}</a>
                                    </li>
                                    <li<#if activeTab == "routemap-tab"> class="active"</#if>>
                                        <a data-toggle="tab" id="routeMapTab" href="#routemap-tab">${uiLabelMap.BSRouteOnMap}</a>
                                    </li>
                                </ul>
                            </div><!--.tabbable-->
                        </div>
                        <div class="span2" style="height:34px; text-align:right">
							<#if routeInfo.partyId?exists && hasOlbPermission("MODULE", "${permission}", "UPDATE")>
                                <a href="<@ofbizUrl>${linkEdit}?partyId=${routeInfo.partyId}&sub=${parameters.sub?if_exists}</@ofbizUrl>" data-rel="tooltip"
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
						<#include "routeDetailViewInfo.ftl"/>
                        <div id="customer-tab" class="tab-pane<#if activeTab == "customer-tab"> active</#if>">
                            <div class="row-fluid">
                                <div class="span12">
                                ${setContextField("routeId", routeInfo.routeId)}
                                ${screens.render("component://basesalesmtl/widget/SupervisorScreens.xml#RetailOutletListInner")}
                                </div>
                            </div>
                        </div>
                        <#include "routeDetailViewMap.ftl"/>
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

<div id="containerNestedSlide" style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotificationNestedSlide" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>
<@jqGridMinimumLib/>
<@jqOlbCoreLib />

<#else>
    <div class="row-fluid">
        <div class="span12">
            <span>${uiLabelMap.BSRouteNotExisted}...</span>
        </div>
    </div>
</#if>