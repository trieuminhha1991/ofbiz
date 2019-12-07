<div class='map-container'>
	<div class='map-container' id="googlemap"></div>
	<div class='action action-small'>
		<div class="widget-box collapsed">
			<div class="widget-header">
				<h4>${uiLabelMap.BSFilterCondition}</h4>
				<div class="widget-toolbar">
					<a href="javascript:void(0)" id="refreshMap"> <i class="icon-refresh"></i> </a>
					<a href="#" data-action="collapse"> <i class="icon-chevron-down"></i> </a>
				</div>
			</div>
		
			<div class="widget-body">
				<div class="filter-condition">
					<div class="row-fluid"> 
						<div class='span4'>
							<h4 class='header-filter'>${uiLabelMap.BSListRoute}</h4>
							<div class='select-container'>
								<input name="customertype" value="customer" type="checkbox" unchecked>
								<div class="lbl">
									${uiLabelMap.BSMAllObject}
								</div>
							</div>
							<#if routes?has_content>
							<#list routes?if_exists as route>
							<div class='select-container'>
								<input name="customertype" data-value='ROUTE' value="${route.routeId}" type="checkbox">
								<div class="lbl">
									${route.routeName}
								</div>
							</div>
							</#list>
							</#if>
						</div>
						<div class='span4'>
							<div class='select-container'>
								<input name="isAllRoute" value="all_route" type="checkbox" checked>
								<div class="lbl">
									${uiLabelMap.BSCustomerOnRoute}
								</div>
							</div>
							<div class='select-container'>
								<input name="" value="contact" type="checkbox" checked>
								<div class="lbl">
									${uiLabelMap.BSCustomerVisited}
								</div>
							</div>
						</div>
						<div class="span4">
							<h4 class='header-filter'>${uiLabelMap.BSListRoute}</h4>
							<div class='select-container'>
								<input name="salesman" value="customer" type="checkbox" unchecked>
								<div class="lbl">
									${uiLabelMap.BSMAllObject}
								</div>
							</div>
							<#if listSalesman?has_content>
								<#list listSalesman?if_exists as salesman>
									<div class='select-container'>
										<input name="salesman" value="${salesman.partyId}" type="checkbox" checked>
										<div class="lbl">
										${salesman.fullName}
										</div>
									</div>
								</#list>
							</#if>	
						</div>
					</div>
				</div>
		</div>
	</div>
</div>	
