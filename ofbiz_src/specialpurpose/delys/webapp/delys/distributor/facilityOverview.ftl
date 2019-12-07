<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
		<#if facilitySelected?exists>
			<div class="form-horizontal basic-custom-form form-decrease-padding" id="updateQuotation" name="updateQuotation" style="display: block;">
				<div class="row margin_left_10 row-desc">
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAFacilityId}:</label>
							<div class="controls-desc">
								<b>${facilitySelected.facilityId?if_exists}</b>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAFacilityName}:</label>
							<div class="controls-desc">
								${facilitySelected.facilityName?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAType}:</label>
							<div class="controls-desc">
								<#assign facilityType = facilitySelected.getRelatedOne("FacilityType", false)/>
								${facilityType.description?if_exists}
							</div>
						</div>
					</div><!--.span6-->
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAOwnerPartyId}:</label>
							<div class="controls-desc">
								${facilitySelected.ownerPartyId?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAGeoPointId}:</label>
							<div class="controls-desc">
								${facilitySelected.geoPointId?if_exists}
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DADescription}:</label>
							<div class="controls-desc">
								${facilitySelected.description?if_exists}
							</div>
						</div>
					</div><!--.span6-->
				</div><!--.row-->
			</div><!--.form-horizontal-->
		</#if>
		</div>
	</div>
</div>