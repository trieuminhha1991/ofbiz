<div id="${dataToggleModalId}" class="modal hide fade" tabindex="-1" >
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.CommonAdd} ${uiLabelMap.HealthCareProvider}
		</div>
	</div>	
	<div class="modal-body no-padding">
		
		
		
		<form <#if !(ajaxSubmit?exists && ajaxSubmit == "true")> action="<@ofbizUrl>${linkUrl}</@ofbizUrl>" </#if> id="${formId}" class="form-horizontal" method="post" name="${formId}">
			<div class="row-fluid">
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.HealthCareProviderName}</label>
					<div class="controls">
						<input type="text" name="groupName" id="${formId}_groupName" class="required">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.HRCommonNational}</label>
					<div class="controls">
						<!-- <input type="text" name="countryGeoId" id="${formId}_countryGeoId" class="required"> -->
						<select id="${formId}_countryGeoId" name="countryGeoId">
							<#list geoList as geo>
								<option value="${geo.geoId}" <#if defaultCountryGeoId==geo.geoId>selected="selected"</#if>>${geo.geoId}: ${geo.geoName}</option>
							</#list>
						</select>	
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label">${uiLabelMap.CommonState}/${uiLabelMap.CommonCity}</label>
					<div class="controls">
						<select id="${formId}_stateProvinceGeoId" name="stateProvinceGeoId">
						</select>
					</div>
				</div>					
				<div class="control-group">
					<label class="control-label">
						&nbsp;  
					</label>
					<div class="controls">
						<button class="btn btn-small btn-primary"  type="submit" id="btnSubmit" <#if (ajaxSubmit?exists && ajaxSubmit == "true")>data-dismiss="modal"</#if>>
							<i class="icon-ok"></i>
							${uiLabelMap.CommonSubmit}
						</button>
					</div>
				</div>
			</div>					
		</form>
	</div>

</div>

<script type="text/javascript">
jQuery(document).ready(function() {
	jQuery("#${createNewLinkId}").attr("data-toggle", "modal");
	jQuery("#${createNewLinkId}").attr("role", "button");
	jQuery("#${createNewLinkId}").attr("href", "#${dataToggleModalId}");
	<#if (ajaxSubmit?exists && ajaxSubmit == "true")>
		jQuery("#btnSubmit").click(function(event){
			event.preventDefault();
			var formData = jQuery("#${formId}").serializeArray();
			jQuery.ajax({
				url: "<@ofbizUrl>${linkUrl}</@ofbizUrl>",
				data: formData,
				type: "POST",
				success: function(data){					
					if(data._EVENT_MESSAGE_){
						bootbox.dialog({
							message: data._EVENT_MESSAGE_,
							title: "${uiLabelMap.Result}",
							buttons:{
								main: {
									label: "OK!",
									className: "btn-small btn-primary icon-ok open-sans"
								}
							}
						});		
						<#if fieldFillValue?exists>
							jQuery(".${fieldFillValue}").val(data.partyId);
						</#if>
						
					}else{
						bootbox.dialog({
							message: "${uiLabelMap.ErrorWhenCreateNew}",
							title: "${uiLabelMap.Result}",
							buttons:{
								main: {
									label: "OK!",
									className: "btn-small btn-danger open-sans"
								}
							}
						});
					}
				},
				
			});
		});
	</#if>
});
</script>