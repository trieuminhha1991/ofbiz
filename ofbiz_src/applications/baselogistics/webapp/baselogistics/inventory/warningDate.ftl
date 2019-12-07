<script>

	<#assign activeTab = "tab_1"/>
	
	$('.nav.nav-tabs li').on('click', function(){
    	// clear parameter
    	var thisHref = location.href;
    	var queryParam = thisHref.split("?");
    	var newHref = "";
    	if (queryParam != null && queryParam != undefined) {
    		newHref = queryParam[0] + "?";
    	}
    	var isAdded = false;
    	if (queryParam.length > 1) {
    		var varsParam = queryParam[1].split("&");
		    for (var i = 0; i < varsParam.length; i++) {
		        var pairParam = varsParam[i].split("=");
		        if(pairParam[0] != 'activeTab'){
		        	if (isAdded) newHref += "&";
		        	newHref += varsParam[i];
		        	isAdded = true;
		        }
		    }
    	}
    	var tabObj = $(this).find("a[data-toggle=tab]");
    	if (tabObj != null && tabObj != undefined) {
    		var tabHref = tabObj.attr("href");
    		if (tabHref.indexOf("#") == 0) {
    			var tabId = tabHref.substring(1);
    			window.history.pushState({}, "", newHref + '&activeTab=' + tabId);
    		}
    	}
    });
	
</script>

<div class="row-fluid margin-top5">
	<div class="span12">
		<div class="tabbable">
			<ul class="nav nav-tabs" id="recent-tab">
				<li class="<#if activeTab?exists && activeTab == "tab_1">active</#if>" id="li_1">
					<a data-toggle="tab" href="#tab_1">
						<span>${uiLabelMap.LogListInventoryDateReach?if_exists}</span>
					</a>
				</li>
				<li class="<#if activeTab?exists && activeTab == "tab_2">active</#if>" id="li_2">
					<a data-toggle="tab" href="#tab_2">
						<span>${uiLabelMap.BLListInventoryExpired?if_exists}</span>
					</a>
				</li>
				<li class="<#if activeTab?exists && activeTab == "tab_3">active</#if>" id="li_3">
					<a data-toggle="tab" href="#tab_3">
						<span>${uiLabelMap.BLInventoryDateNeedToSales?if_exists}</span>
					</a>
				</li>
			</ul>
		</div>
	</div>
</div>

<div class="tab-content overflow-visible" style="padding:8px 0; border: none !important;">
	<div class="tab-pane<#if activeTab?exists && activeTab == "tab_1"> active</#if>" id="tab_1">
		<#include "listInventoryReachDate.ftl">
	</div>
	<div class="tab-pane<#if activeTab?exists && activeTab == "tab_2"> active</#if>" id="tab_2">
		<#include "listInventoryExpired.ftl">
	</div>
	<div class="tab-pane<#if activeTab?exists && activeTab == "tab_3"> active</#if>" id="tab_3">
		<#include "listInventoryReachDateSales.ftl">
	</div>
</div>