<@jqGridMinimumLib/>
<script>
	var defaultCity = "${StringUtil.wrapString(city?if_exists)}";
	var routes = [<#list routes?if_exists as route>
	{
	partyId : "${route.partyId}",
	groupName : "${route.groupName}",
	},
	</#list>];
	var sups = [<#list sups?if_exists as sup>"${sup}",</#list>];
	var distributor = [<#list distributor?if_exists as dis>"${dis}",</#list>];
	var isDistributor = "${isDistributor?if_exists}"=="Y"?true:false;
	var agents = new Array();
	<#if agents?exists>
	agents = ${StringUtil.wrapString(agents?if_exists)};
	</#if>
	var org = [<#list org?if_exists as o>"${o}",</#list>];
	var addresses = [<#list addressesValue?if_exists as address>{
	stateProvinceGeoId : "${address.stateProvinceGeoId?if_exists}",
	districtGeoId : "${address.districtGeoId?if_exists}"
	},</#list>];
	var uniqAddresses = _.uniq(_.pluck(addresses, 'stateProvinceGeoId'));
	var currentSup = "${currentSup?if_exists}"
	var userLoginId = "${userLogin.userLoginId}";
	<#if routes?has_content>
	var notSup = false;
	<#else>
	var notSup = true;
	</#if>
	var label = {
		"BSSpecialPromotion" : "${uiLabelMap.BSSpecialPromotion}",
		BSProgramName : "${uiLabelMap.BSProgramName}",
		BSRuleRegistration : "${uiLabelMap.BSRuleRegistration}",
		BSStatus : "${uiLabelMap.BSStatus}",
		BSFinalResult : "${uiLabelMap.BSFinalResult}",
		BSResult : "${uiLabelMap.BSResult}",
		BSResult : "${uiLabelMap.BSResult}",
		BSRule : "${uiLabelMap.BSRule}",
		ProductProductId : "${uiLabelMap.ProductProductId}",
		ProductProductName : "${uiLabelMap.ProductProductName}",
	};
</script>
<script src="/aceadmin/assets/js/markerwithlabel.js"></script>
<div class='map-container'>
	<div class='map-container' id="googlemap"></div>
	
		<!-- <button id='remove'></button> -->
		
	</div>
</div>