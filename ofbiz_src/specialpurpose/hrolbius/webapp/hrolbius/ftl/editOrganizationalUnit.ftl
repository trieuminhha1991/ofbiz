<script>
<#assign countries = Static["org.ofbiz.common.CommonWorkers"].getCountryList(delegator) !>
var countryCodeData = new Array();
<#if countries?exists>
	<#list countries as country >
		var row = {};
		row['geoId'] = '${country.geoId?if_exists}';
		row['codeNumber'] = '${country.codeNumber?if_exists}';
		countryCodeData[${country_index}] = row;
	</#list>
</#if>

<#assign provinces = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "PROVINCE"), null, null, null, false) !>
var areaCodeData = new Array();
<#if provinces?exists>
	<#list provinces as province >
		var row = {};
		row['geoId'] = '${province.geoId?if_exists}';
		row['codeNumber'] = '${province.codeNumber?if_exists}';
		areaCodeData[${province_index}] = row;
	</#list>
</#if>
</script>

<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<div id="fuelux-wizard" class="row-fluid">
				<ul class="wizard-steps">
					<li data-target="#step1" style="min-width: 40%; max-width: 25%;" class="active"><span class="step">1</span> <span class="title">${uiLabelMap.GeneralInformation}</span></li>
					<li data-target="#step2" style="min-width: 40%; max-width: 25%;" ><span class="step">2</span> <span class="title">${uiLabelMap.ContactInformation}</span></li>
					<!-- <li data-target="#step3" style="min-width: 25%; max-width: 25%;" ><span class="step">3</span> <span class="title">${uiLabelMap.ManagerInformation}</span></li> -->
				</ul>
			</div>
			
			<hr>
			
			<div class="step-content row-fluid position-relative">
				<form name="editOrganizationalUnit" id="editOrganizationalUnit" method="post" action="<@ofbizUrl>createOrganizationalUnit</@ofbizUrl>">
					<div class="step-pane active" id="step1">
					   	<table cellspacing="0" style="width: 100%">
					   		<tbody>
					   			<tr>
									<td><label class="padding-bottom5 padding-right15 margin-left30" for="organizationalUnitName">${uiLabelMap.OrganizationalUnitName}</label> <span style="color:red">(*)</span></td>
									<td>
										<input type="text" name="organizationalUnitName" id="organizationalUnitName"/>
									</td>
								</tr>
								<tr>
									<td><label class="padding-bottom5 padding-right15 margin-left30" for="orgUnitLevel">${uiLabelMap.OrganizationUnitLevel}</label></td>
									<td>
										<select id="orgUnitLevel" name="orgUnitLevel">
											<#list orgUnitLevels as orgUnitLevel>
												<option value="${orgUnitLevel.roleTypeId}">${orgUnitLevel.description}</option>
											</#list>
										</select>
									</td>
								</tr>
								<tr>
									<td><label class="padding-bottom5 padding-right15 margin-left30" for="functions">${uiLabelMap.Functions}</label></td>
									<td>
										<input type="text" name="functions" id="functions"/>
									</td>
								</tr>
								<tr>
									<td><label class="padding-bottom5 padding-right15 margin-left30" for="officeSiteName">${uiLabelMap.OfficeSiteName}</label></td>
									<td>
										<input type="text" name="officeSiteName" id="officeSiteName"/>
									</td>
								</tr>
								
								<tr>
									<td><label class="padding-bottom5 padding-right15 margin-left30" for="parentOrgId">${uiLabelMap.BelongsTo}</label></td>
									<td>
										<@htmlTemplate.lookupField formName="editOrganizationalUnit" name="parentOrgId" id="parentOrgId" fieldFormName="LookupPartyGroup" value="${parameters.parentOrgId?if_exists}"/>
									</td>
								</tr>
								
								
								<tr>
									<td><label class="padding-bottom5 padding-right15 margin-left30" for="parentRoleTypeId">${uiLabelMap.OrganizationUnitLevel}</label></td>
									<td>
										<select id="parentRoleTypeId" name="parentRoleTypeId">
											<#list partyRoles as roleType>
												<#assign roleTypeGv = delegator.findOne("RoleType", Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", roleType), false)>
												<option value="${roleTypeGv.roleTypeId}">${roleTypeGv.description}</option>
											</#list>
										</select>
									</td>
								</tr>
								</tbody>
					   		</table>
				   		</div>
						<div class="step-pane" id="step2">
							<table>
								<tr>
									<td ><label class="padding-bottom5 padding-right15" for="address1">${uiLabelMap.PartyAddressLine}</label></td>
									<td>
										<input type="text" name="address1" id="address1"/>
									</td>
								</tr>
								<tr>   
      								<td><label class="padding-bottom5 padding-right15" for="editOrganizationalUnit_countryGeoId">${uiLabelMap.CommonCountry}</label></td>
      								<td>     
        								<select name="countryGeoId" id="editOrganizationalUnit_countryGeoId" onchange="searchCountry()">
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
      								<td><label class="padding-bottom5 padding-right15" for="editOrganizationalUnit_stateProvinceGeoId">${uiLabelMap.PartyState}</label></td>
      								<td>
       	 								<select name="stateProvinceGeoId" id="editOrganizationalUnit_stateProvinceGeoId">
        								</select>
      								</td>
    							</tr>
								<tr>
      								<td width="20%"><label class="padding-bottom5 padding-right15">${uiLabelMap.PartyPhoneNumber}</label></td>
      								<td  width="40%">
      									<input type="tel" size="4" maxlength="10" name="countryCode"  style="width: 30px" id="countryCode" />
        								<b>-</b>&nbsp;<input type="text" size="4" maxlength="10"  name="areaCode" id="areaCode" style="width: 30px"/>
        								<b>-</b>&nbsp;<input type="text" size="15" maxlength="15" name="contactNumber" style="width: 96px"/>
										<#--<div class="span3 input-prepend">
											<span class="add-on"><i class="icon-phone"></i></span>
											<input style="width: 180px" type="tel" id="phoneNumber" name="phoneNumber" />
										</div>-->
      								</td>
      								<#--<td style="vertical-align: initial; padding-left: 10px;" width="45%">[${uiLabelMap.CommonCountryCode}]-[${uiLabelMap.PartyAreaCode}]-[${uiLabelMap.PartyContactNumber}]</td>-->
    							</tr>
								<tr>
									<td><label class="padding-bottom5 padding-right15" for="emailAddress">${uiLabelMap.EmailAddress}</label></td>
      								<td>
       									<input type="text" size="60" maxlength="255" name="emailAddress" id="emailAddress" />
      								</td>
								</tr>
							</table>
						</div>
						<#--
						<!-- <div class="step-pane" id="step3">
							<table>
								<tr>
									<td><label class="padding-bottom5 padding-right15" for="managerId">${uiLabelMap.Manager}</label></td>
      								<td>
      									<@htmlTemplate.lookupField formName="editOrganizationalUnit" name="managerId" id="managerId" fieldFormName="LookupPerson"/>
										<span class="tooltipob">${uiLabelMap.required}</span>
      								</td>
								</tr>
								<tr>
									<td><label class="padding-bottom5 padding-right15" for="title">${uiLabelMap.Title}</label></td>
      								<td>
       									<select name="title" id="title">
       										<#list titles as title>
       											<option value="${title.roleTypeId}">${title.description}</option>
       										</#list>
       									</select>
      								</td>
								</tr>
							</table>
						</div> -->
					</form>
				</div>
			
				<hr/>
					
				<div class="row-fluid wizard-actions">
					<button class="btn btn-prev btn-small"><i class="icon-arrow-left"></i> Prev</button>
					<button class="btn btn-success btn-next btn-small" data-last="Finish ">Next <i class="icon-arrow-right icon-on-right"></i></button>
				</div>
			</div>

		</div><!--/widget-main-->
	</div> <!-- /widget-body-->

<script type="text/javascript">
function searchCountry(){
	var countryGeoId = $("#editOrganizationalUnit_countryGeoId").val();
	for (var i = 0; i < countryCodeData.length; i++){
		if (countryGeoId==countryCodeData[i].geoId){
			$("#countryCode").val(countryCodeData[i].codeNumber);
		}
	}
}

$("#editOrganizationalUnit_stateProvinceGeoId").click(function(){
	var provinceGeoId = $("#editOrganizationalUnit_stateProvinceGeoId").val();
	for (var j = 0; j < areaCodeData.length; j++){
		if (provinceGeoId==areaCodeData[j].geoId){
			$("#areaCode").val(areaCodeData[j].codeNumber);
		}
		if (provinceGeoId=='_NA'){
			$("#areaCode").val("");
		}
	}
});

$(function() {

	var $validation = false;
	$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
		var countryGeoId = $("#editOrganizationalUnit_countryGeoId").val();
		var provinceGeoId = $("#editOrganizationalUnit_stateProvinceGeoId").val();
		for (var i = 0; i < countryCodeData.length; i++){
			if (countryGeoId==countryCodeData[i].geoId){
				$("#countryCode").val(countryCodeData[i].codeNumber);
			}
		}
		
		for (var j = 0; j < areaCodeData.length; j++){
			if (provinceGeoId==areaCodeData[j].geoId){
				$("#areaCode").val(areaCodeData[j].codeNumber);
			}
			if (provinceGeoId=='_NA'){
				$("#areaCode").val("");
			}
		}
		
		if(!$('#editOrganizationalUnit').valid()) return false;
	}).on('finished', function(e) {
		document.getElementById("editOrganizationalUnit").submit();
	});
	
// 	$.mask.definitions['~']='[+-]';
// 	$('#phoneNumber').mask('9999-9999-99999999999');	
	
	jQuery.validator.addMethod("phone", function (value, element) {
		return this.optional(element) || /^[0-9-+]+$/.test(value);
	}, "<span style='color:red;'>Hãy nhập SĐT đúng định dạng</span>");
	
	$.validator.addMethod("nospecialcharacter", function(value, element) {
		if(value){
			return this.optional(element) || /^[A-Za-z\u00C0-\u1EF9\s\d,\.\-]+$/i.test(value);
		} else 
			return true;
	}, "Letters, numbers, and underscores only please");
	
	$('#editOrganizationalUnit').validate({
		focusInvalid: false,
		errorElement: 'div',
    	errorClass: "invalid",
    	errorPlacement: function(error, element) {
			element.addClass("border-error");
    		if (element.parent() != null ){   
				element.parent().find("button").addClass("button-border");     			
    			error.appendTo(element.parent());
			}
    	  },
		rules: {
			organizationalUnitName: {
				required: true,
				nospecialcharacter: true
			},
			functions:{
				nospecialcharacter: true
			},
			officeSiteName:{
				nospecialcharacter: true
			},
			parentOrgId:{
				required: true,
			},
			emailAddress: {
				email: true,
			},
			contactNumber: {
				phone: 'required',
			},
			address1:{
				nospecialcharacter: true
			}
		},

		messages: {
			organizationalUnitName: {
				required: "<span style='color:red;'>Bắt buộc</span>",
				nospecialcharacter: "<span style='color:red;'>${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}</span>"
			},
			functions:{
				nospecialcharacter: "<span style='color:red;'>${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}</span>"
			},
			officeSiteName:{
				nospecialcharacter: "<span style='color:red;'>${uiLabelMap.HrolbiusRequiredNotSpecialCharacter}</span>"
			},
			parentOrgId:{
				required: "<span style='color:red;'>Bắt buộc</span>",
			},
			emailAddress: {
				email: "<span style='color:red;'>Hãy nhập email đúng định dạng</span>"
			},
			address1:{
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
		},
		invalidHandler: function (form) {
		}
	});
	})
</script>