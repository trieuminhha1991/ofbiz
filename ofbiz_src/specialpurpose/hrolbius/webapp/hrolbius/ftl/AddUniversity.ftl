<div id="${dataToggleModalId}" class="modal hide fade" tabindex="-1" >
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			${uiLabelMap.CommonAdd} ${uiLabelMap.University}
		</div>
	</div>	
	<div class="modal-body no-padding">
		<div class="widget-body">	 
			<div class="widget-main">
				<div class="row-fluid">
				<form name="AddUniversity" id="AddUniversity" method="post" action="<@ofbizUrl>createUniversity</@ofbizUrl>">
						<table>
							<tr>
								<td ><label class="padding-bottom5 padding-right150 asterisk" for="schoolId">${uiLabelMap.CommonId}</label></td>
								<td>
									<input type="text" name="schoolId" id="schoolId"/>
								</td>
							</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right150 asterisk" for="schoolName">${uiLabelMap.UniversityName}</label></td>
     								<td>
      									<input type="text" size="60" maxlength="255" name="schoolName" id="schoolName" />
     								</td>
							</tr>
							<tr>   
     								<td><label class="padding-bottom5 padding-right150 asterisk" for="AddUniversity_countryGeoId">${uiLabelMap.CommonCountry}</label></td>
     								<td>     
       								<select name="countryGeoId" id="AddUniversity_countryGeoId">
         									${screens.render("component://common/widget/CommonScreens.xml#countries")}        
          									<#assign defaultCountryGeoId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "country.geo.id.default")>
         									<option selected="selected" value="${defaultCountryGeoId}">
           									<#assign countryGeo = delegator.findOne("Geo",Static["org.ofbiz.base.util.UtilMisc"].toMap("geoId",defaultCountryGeoId), false)>
           									${countryGeo.get("geoName",locale)}
         									</option>
       								</select>
     								</td>
   							</tr>
							<tr>
     								<td><label class="padding-bottom5 padding-right150 asterisk" for="AddUniversity_stateProvinceGeoId">${uiLabelMap.PartyState}</label></td>
     								<td>
      	 								<select name="stateProvinceGeoId" id="AddUniversity_stateProvinceGeoId">
       								</select>
     								</td>
   							</tr>
   							<tr>
								<td><label class="padding-bottom5 padding-right150" for="description">${uiLabelMap.HROlbiusTypeProposeDescription}</label></td>
     								<td>
      									<input type="text" size="60" maxlength="255" name="description" id="description" />
     								</td>
							</tr>
							<tr>
								<td></td>
								<td>
									<button type="submit" class="btn btn-small btn-primary" name="submitButton" id="submitButton" style="margin-top: 15px;"><i class="icon-ok"></i>${uiLabelMap.CommonCreate}</button>
								</td>
							</tr>
						</table>
					</form>
				</div>
			</div><!--/widget-main-->
		</div> <!-- /widget-body-->
	</div>
	
</div>
<script type="text/javascript">
$(document).ready(function(){
	jQuery("#${createNewLinkId}").attr("data-toggle", "modal");
	jQuery("#${createNewLinkId}").attr("role", "button");
	jQuery("#${createNewLinkId}").attr("href", "#${dataToggleModalId}");
	$.validator.addMethod("nospecialcharacter", function(value, element) {
		if(value){
			return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d,\.]+$/i.test(value);
		} else 
			return true;
	}, "Letters, numbers, and underscores only please");
	
	$.validator.addMethod("validateId", function (value, element) {
		return this.optional(element) || /^\S+$/i.test(value);
	}, "${uiLabelMap.NoWhiteSpaceNotify}");
	
	$('#AddUniversity').validate({
		errorElement: 'span',
		errorClass: 'help-inline',
		focusInvalid: false,
		rules: {
			schoolId: {
				required: true,
				validateId: true,
				nospecialcharacter:true
			},
			schoolName:{
				required: true,
			},
			countryGeoId:{
				required: true,
			},
			stateProvinceGeoId:{
				required: true,
			},
			description:{
				nospecialcharacter: true
			}
		},

		messages: {
			schoolId: {
				required: "<span style='color:red;'>Bắt buộc</span>",
				validateId: "<span style='color:red;'>${uiLabelMap.NoWhiteSpaceNotify}</span>",
				nospecialcharacter: "<span style='color:red;'>${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}</span>"
			},
			schoolName: {
				required: "<span style='color:red;'>Bắt buộc</span>",
			},
			countryGeoId: {
				required: "<span style='color:red;'>Bắt buộc</span>",
			},
			stateProvinceGeoId: {
				required: "<span style='color:red;'>Bắt buộc</span>",
			},
			description:{
				nospecialcharacter: "<span style='color:red;'>${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}</span>"
			}
		},

		invalidHandler: function (event, validator) { //display error alert on form submit   
			$('.alert-error', $('.login-form')).show();
		},

		highlight: function (e) {
			$(e).closest('.control-group').removeClass('info').addClass('error');
		},

		success: function (e) {
			$(e).closest('.control-group').removeClass('error').addClass('info');
			$(e).remove();
		},

		submitHandler: function (form) {
			form.submit();
		},
		invalidHandler: function (form) {
		}
	});
})
</script>