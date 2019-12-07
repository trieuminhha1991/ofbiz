<#if !activeTab?has_content>
	<#assign activeTab = "productFormula-tab">
</#if>
<div class="row-fluid margin-top20">
	<div class="span12">
		<div class="tabbable">
			<ul class="nav nav-tabs" id="recent-tab">
				<li class="<#if activeTab?exists && activeTab == "formula"> active</#if>" id="">
					<a data-toggle="tab" href="#formula" title="${uiLabelMap.BLFormula}">
						<span>${uiLabelMap.BLFormula}</span>
					</a>
				</li>
				<li class="<#if activeTab?exists && activeTab == "formulaParameter"> active</#if>" id="">
					<a data-toggle="tab" href="#formulaParameter" title="${uiLabelMap.BLParamater}">
						<span>${uiLabelMap.BLParamater}</span>
					</a>
				</li>
				<li class="<#if activeTab?exists && activeTab == "productFormula"> active</#if>" id="">
					<a data-toggle="tab" href="#productFormula" title="${uiLabelMap.BLProductAndFormula}>">
						<span>${uiLabelMap.BLProductAndFormula}</span>
					</a>
				</li>
				<li class="<#if activeTab?exists && activeTab == "parameterStore"> active</#if>" id="">
					<a data-toggle="tab" href="#parameterStore" title="${uiLabelMap.BLParameterAndProductStore}>">
						<span>${uiLabelMap.BLParameterAndProductStore}</span>
					</a>
				</li>
			</ul>
		</div><!--.tabbable-->
	</div>
</div>
<div class="tab-content overflow-visible" style="padding:0; border: none !important;">
	<div class="tab-pane<#if activeTab?exists && activeTab == "formula"> active</#if>" id="formula">
		<div class="margin-top10 margin-bottom10">
			<div><#include "listFormulas.ftl"/></div>
		</div>
	</div>
	<div class="tab-pane<#if activeTab?exists && activeTab == "formulaParameter"> active</#if>" id="formulaParameter">
		<div class="margin-top10 margin-bottom10">
			<div><#include "listFormulaParameters.ftl"/></div>
		</div>
	</div>
	<div class="tab-pane<#if activeTab?exists && activeTab == "productFormula"> active</#if>" id="productFormula">
		<div class="margin-top10 margin-bottom10">
			<div><#include "productFormula.ftl"/></div>
		</div>
	</div>
	<div class="tab-pane<#if activeTab?exists && activeTab == "parameterStore"> active</#if>" id="parameterStore">
		<div class="margin-top10 margin-bottom10">
			<div><#include "parameterProductStore.ftl"/></div>
		</div>
	</div>
</div>
<script>
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