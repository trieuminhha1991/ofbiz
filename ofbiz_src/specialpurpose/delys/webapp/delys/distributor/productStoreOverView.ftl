<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
		<#if productStoreSelected?exists>
			<div class="form-horizontal basic-custom-form form-decrease-padding" style="display: block;">
				<div class="row margin_left_10 row-desc">
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAProductStoreId}:</label>
							<div class="controls-desc">
								<b>${productStoreSelected.productStoreId?if_exists}</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAStoreName}:</label>
							<div class="controls-desc">
								${productStoreSelected.storeName?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DACurrencyUomId}:</label>
							<div class="controls-desc">
								${productStoreSelected.defaultCurrencyUomId?if_exists}
							</div>
						</div>
					</div><!--.span6-->
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAMainFacilityId}:</label>
							<div class="controls-desc">
								${productStoreSelected.inventoryFacilityId?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAPayToPartyId}:</label>
							<div class="controls-desc">
								${productStoreSelected.payToPartyId?if_exists}
							</div>
						</div>
					</div><!--.span6-->
				</div><!--.row-->
			</div><!--.form-horizontal-->
			<!--BEGIN-->
			<div style="clear:both"></div>
			<hr/>
			<div style="text-align:right">
				<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.DAListProductStoreFacility}</b></h5>
			</div>
			<div style="clear:both"></div>
			<div>
				<#if productStoreFacilities?exists && productStoreFacilities?has_content>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width:10px">${uiLabelMap.DANo}</th>
								<th class="center">${uiLabelMap.DAFacilityId}</th>
								<th class="center">${uiLabelMap.DAFromDate}</th>
								<th class="center">${uiLabelMap.DAThruDate}</th>
								<th class="center">${uiLabelMap.DASequenceNum}</th>
							</tr>
						</thead>
						<tbody>
						<#list productStoreFacilities as facilityItem>
							<tr>
								<td>${facilityItem_index + 1}</td>
								<td>${facilityItem.facilityId?if_exists}</td>
								<td>${facilityItem.fromDate?if_exists}</td>
								<td>${facilityItem.thruDate?if_exists}</td>
								<td>${facilityItem.sequenceNum?if_exists}</td>
							</tr>
						</#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
				</#if>
			</div>
			<!--END LIST 3-->
			<#--
			<div style="clear:both"></div>
			<div style="text-align:right">
				<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.DAListProductStoreRole}</b></h5>
			</div>
			<div style="clear:both"></div>
			<div>
				<#if productStoreRoles?exists && productStoreRoles?has_content>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width:10px">${uiLabelMap.DANo}</th>
								<th class="center">${uiLabelMap.DAPartyId}</th>
								<th class="center">${uiLabelMap.DARoleTypeId}</th>
								<th class="center">${uiLabelMap.DAFromDate}</th>
								<th class="center">${uiLabelMap.DAThruDate}</th>
								<th class="center">${uiLabelMap.DASequenceNum}</th>
							</tr>
						</thead>
						<tbody>
						<#list productStoreRoles as productStoreRoleItem>
							<tr>
								<td>${productStoreRoleItem_index + 1}</td>
								<td>${productStoreRoleItem.partyId?if_exists}</td>
								<td>${productStoreRoleItem.roleTypeId?if_exists}</td>
								<td>${productStoreRoleItem.fromDate?if_exists}</td>
								<td>${productStoreRoleItem.thruDate?if_exists}</td>
								<td>${productStoreRoleItem.sequenceNum?if_exists}</td>
							</tr>
						</#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
				</#if>
			</div>
			-->
			<!--END LIST 1-->
			<div style="clear:both"></div>
			<div style="text-align:right">
				<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.DAListProductStorePromo}</b></h5>
			</div>
			<div style="clear:both"></div>
			<div>
				<#if productStorePromoAndAppls?exists && productStorePromoAndAppls?has_content>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th style="width:10px">${uiLabelMap.DANo}</th>
								<th class="center">${uiLabelMap.DAProductPromoId}</th>
								<th class="center">${uiLabelMap.DAName}</th>
								<th class="center">${uiLabelMap.DAType}</th>
								<th class="center">${uiLabelMap.DAFromDate}</th>
								<th class="center">${uiLabelMap.DAThruDate}</th>
								<th class="center">${uiLabelMap.DASequenceNum}</th>
							</tr>
						</thead>
						<tbody>
						<#list productStorePromoAndAppls as promoAndApplItem>
							<tr>
								<td>${promoAndApplItem_index + 1}</td>
								<td>${promoAndApplItem.productPromoId?if_exists}</td>
								<td>${promoAndApplItem.promoName?if_exists}</td>
								<td>${promoAndApplItem.productPromoTypeId?if_exists}</td>
								<td>${promoAndApplItem.fromDate?if_exists}</td>
								<td>${promoAndApplItem.thruDate?if_exists}</td>
								<td>${promoAndApplItem.sequenceNum?if_exists}</td>
							</tr>
						</#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
				</#if>
			</div>
			<!--END LIST 2-->
			
			
			
		</#if>
		</div>
	</div>
</div>