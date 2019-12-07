<@jqGridMinimumLib/>
<script>
	var defaultCity = "${StringUtil.wrapString(city?if_exists)}";
	var routes = [<#list routes?if_exists as route>
	{
	partyId : "${route.routeId}",
	groupName : "${route.routeName}",
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
	<#if routes?has_content>
		<div class='action'>
	<#else>
		<div class='action action-small'>
	</#if>
		<!-- <button id='remove'></button> -->
		<div class="widget-box collapsed">
			<div class="widget-header">
				<h4>${uiLabelMap.BSFilterCondition}</h4>
				<div class="widget-toolbar">
					<div class='all-input'>
						<input name="customertype" value="all" type="checkbox" checked>
						<div class="lbl">
							&nbsp;
						</div>
					</div>
					<a href="javascript:void(0)" id="refreshMap"> <i class="icon-refresh"></i> </a>
					<a href="#" data-action="collapse"> <i class="icon-chevron-down"></i> </a>
				</div>
			</div>

			<div class="widget-body">
				<div class="filter-condition">
					<#if routes?has_content>
					<div class='row-fluid'>
						<div class='span6'>
							<h4 class='header-filter'>${uiLabelMap.BSListRoute}</h4>
							<div class='select-container'>
								<input name="customertype" value="customer" type="checkbox" checked>
								<div class="lbl">
									${uiLabelMap.BSMAllObject}
								</div>
							</div>
							<#if routes?has_content>
							<#list routes?if_exists as route>
							<div class='select-container'>
								<input name="customertype" data-value='ROUTE' value="${route.routeId}" type="checkbox" checked>
								<div class="lbl">
									${route.routeName}
								</div>
							</div>
							</#list>
							</#if>
						</div>
						<div class='span6'>
							<h4 class='header-filter'>${uiLabelMap.BSPotentialCustomer}</h4>
							<div class='select-container'>
								<input name="customertype" value="contact" type="checkbox" checked>
								<div class="lbl">
									${uiLabelMap.BSMAllObject}
								</div>
							</div>
							
							<hr/>
							<div class='row-fluid'>
								<input name="boundarydrawing" type="checkbox">
								<div class="lbl">
									${uiLabelMap.BSEnableBoundary}
								</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<label>${uiLabelMap.BSNoOrderTime}</label>
								</div>
								<div class='span7'>
									<select id="ordertimeconditions" class='full-width-input'>
										<option value="">${uiLabelMap.BSRemoveChosenDays}</option>
										<option value="7">7 ${uiLabelMap.BSDays}</option>
										<option value="10">10 ${uiLabelMap.BSDays}</option>
										<option value="15">15 ${uiLabelMap.BSDays}</option>
										<option value="20">20 ${uiLabelMap.BSDays}</option>
										<option value="30">30 ${uiLabelMap.BSDays}</option>
										<option value="60">60 ${uiLabelMap.BSDays}</option>
									</select>
								</div>
							</div>
							<p style='display: none'>${uiLabelMap.BSResult}: <b id="orderResult">0</b> ${uiLabelMap.BSCustomerNoOrderTransation}.</p>
							<hr/>
							<div class='select-container'>
								<input name="product" value="product" type="checkbox">
								<div class="lbl">
									${uiLabelMap.DAProduct}
								</div>
							</div>
							<div class='select-container'>
								<div id="txtProduct" class="hide">
									<div style="border-color: transparent;" id="jqxgridProduct"></div>
								</div>
							</div>
						</div>
					</div>
					<#else>
					<div class='select-container'>
						<input name="customertype" value="customer" type="checkbox" checked>
						<div class="lbl">
							${uiLabelMap.BSCustomerManaging}
						</div>
					</div>
					<div class='select-container'>
						<input name="customertype" value="contact" type="checkbox" checked>
						<div class="lbl">
							${uiLabelMap.BSPotentialCustomer}
						</div>
					</div>
					<hr/>
					<div class='select-container'>
						<input name="product" value="product" type="checkbox">
						<div class="lbl">
							${uiLabelMap.DAProduct}
						</div>
					</div>
					<div class='select-container'>
						<div id="txtProduct" class="hide">
							<div style="border-color: transparent;" id="jqxgridProduct"></div>
						</div>
					</div>
					<div class='row-fluid hide'>
						<input name="boundarydrawing" type="checkbox">
						<div class="lbl">
							${uiLabelMap.BSEnableBoundary}
						</div>
					</div>
					</#if>
				</div>
			</div>
		</div>
	</div>
</div>