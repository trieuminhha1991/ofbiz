<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<form class="form-horizontal basic-custom-form form-decrease-padding" action="<@ofbizUrl>findFacilityLocationDis</@ofbizUrl>" method="GET" name="findFacilityLocation">
				 <#if (facilityId?exists)><input type="hidden" name="facilityId" value="${facilityId}" /></#if>
				 <div class="row-fluid">
					<div class="span6">
						<#if !(facilityId?exists)>
							<div class="control-group">
								<label class="control-label">${uiLabelMap.ProductFacility}</label>
								<div class="controls">
									<input type="text" value="" size="19" maxlength="20" />
								</div>
							</div>
						</#if>
						<div class="control-group">
							<label class="control-label">${uiLabelMap.ProductLocationSeqId}</label>
							<div class="controls">
								<#if parameters.facilityId?exists>
				                    <#assign LookupFacilityLocationView="LookupFacilityLocation?facilityId=${facilityId}">
				                <#else>
				                    <#assign LookupFacilityLocationView="LookupFacilityLocation">
				                </#if>
				                <@htmlTemplate.lookupField formName="findFacilityLocation" name="locationSeqId" id="locationSeqId" fieldFormName="${LookupFacilityLocationView}"/>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">${uiLabelMap.CommonArea}</label>
							<div class="controls">
								<input type="text" name="areaId" value="" size="19" maxlength="20" />
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">${uiLabelMap.ProductAisle}</label>
							<div class="controls">
								<input type="text" name="aisleId" value="" size="19" maxlength="20" />
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">${uiLabelMap.ProductSection}</label>
							<div class="controls">
								<input type="text" name="sectionId" value="" size="19" maxlength="20" />
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">${uiLabelMap.ProductLevel}</label>
							<div class="controls">
								<input type="text" name="levelId" value="" size="19" maxlength="20" />
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">${uiLabelMap.ProductPosition}</label>
							<div class="controls">
								<input type="text" name="positionId" value="" size="19" maxlength="20" />
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">&nbsp;</label>
							<div class="controls">
								<button class="btn btn-small btn-primary" type="submit" name="look_up">
					            	<i class="icon-search"></i>
					            	${uiLabelMap.CommonFind}
					            </button>
							</div>
						</div>
					</div><!--.span6-->
				</div><!--.row-fluid-->
			</form>
			
			<#if foundLocations?exists>
				<#-- TODO: Put this in a screenlet - make it look more like the party find screen -->
				<div style="clear:both"></div>
				<hr/>
				<div style="text-align:right">
					<h5 class="lighter block green" style="float:left">
						${uiLabelMap.CommonFound}:&nbsp;${foundLocations.size()}&nbsp;${uiLabelMap.ProductLocationsFor}&nbsp;
						<#if facility?exists>${(facility.facilityName)?if_exists}</#if> [ID:${facilityId?if_exists}]
					</h5>
				</div>
				<div style="clear:both"></div>
				<div>
					<#if productStoreFacilities?exists && productStoreFacilities?has_content>
						<table class="table table-striped table-bordered table-hover">
							<thead>
								<tr>
									<th style="width:10px">${uiLabelMap.DANo}</th>
									<th class="center">${uiLabelMap.ProductFacility}</th>
									<th class="center">${uiLabelMap.ProductLocationSeqId}</th>
									<th class="center">${uiLabelMap.ProductType}</th>
									<th class="center">${uiLabelMap.CommonArea}</th>
									<th class="center">${uiLabelMap.ProductAisle}</th>
									<th class="center">${uiLabelMap.ProductSection}</th>
									<th class="center">${uiLabelMap.ProductLevel}</th>
									<th class="center">${uiLabelMap.ProductPosition}</th>
									<th>&nbsp;</th>
								</tr>
							</thead>
							<tbody>
							<#-- toggle the row color -->
							<#assign rowClass = "2">
        					<#list foundLocations as location>
        						<#assign locationTypeEnum = location.getRelatedOne("TypeEnumeration", true)?if_exists>
								<tr<#if rowClass == "1"> class="alternate-row"</#if>>
									<td>${facilityItem_index + 1}</td>
									<td><a class="btn btn-info btn-mini" href="<@ofbizUrl>EditFacility?facilityId=${(location.facilityId)?if_exists}</@ofbizUrl>">${(location.facilityId)?if_exists}</a></td>
									<td><a class="btn btn-info btn-mini" href="<@ofbizUrl>EditFacilityLocation?facilityId=${facilityId}&locationSeqId=${(location.locationSeqId)?if_exists}</@ofbizUrl>">${(location.locationSeqId)?if_exists}</a></td>
									<td>${(locationTypeEnum.get("description",locale))?default(location.locationTypeEnumId?if_exists)}</td>
									<td>${(location.areaId)?if_exists}</td>
									<td>${(location.aisleId)?if_exists}</td>
									<td>${(location.sectionId)?if_exists}</td>
									<td>${(location.levelId)?if_exists}</td>
									<td>${(location.positionId)?if_exists}</td>
									<td class="button-col">
						              	<a class="btn btn-info btn-mini" href="<@ofbizUrl>EditInventoryItem?facilityId=${(location.facilityId)?if_exists}&locationSeqId=${(location.locationSeqId)?if_exists}</@ofbizUrl>">${uiLabelMap.ProductNewInventoryItem}</a>
						              	<#if itemId?exists>
						                	<a class="btn btn-info btn-mini" href="<@ofbizUrl>UpdateInventoryItem?inventoryItemId=${itemId}&facilityId=${facilityId}&locationSeqId=${(location.locationSeqId)?if_exists}</@ofbizUrl>">${uiLabelMap.ProductSetItem} ${itemId}</a>
						              	</#if>
						              	<a class="btn btn-info btn-mini" href="<@ofbizUrl>EditFacilityLocation?facilityId=${(location.facilityId)?if_exists}&locationSeqId=${(location.locationSeqId)?if_exists}</@ofbizUrl>">${uiLabelMap.CommonEdit}</a>
						            </td>
								</tr>
								<#if rowClass == "2">
						            <#assign rowClass = "1">
						        <#else>
						            <#assign rowClass = "2">
						        </#if>
							</#list>
							</tbody>
						</table>
					<#else>
						<div class="alert alert-info">${uiLabelMap.DANoItemToDisplay}</div>
					</#if>
				</div>
			</#if>
		</div>
	</div>
</div>
