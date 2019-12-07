<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<div id="fuelux-wizard" class="row-fluid">
				<ul class="wizard-steps">
					<li data-target="#step1" style="min-width: 25%; max-width: 25%;" class="active"><span class="step">1</span> <span class="title">${uiLabelMap.GeneralInformation}</span></li>
					<li data-target="#step2" style="min-width: 25%; max-width: 25%;" ><span class="step">2</span> <span class="title">${uiLabelMap.ContactInformation}</span></li>
					<li data-target="#step3" style="min-width: 25%; max-width: 25%;" ><span class="step">3</span> <span class="title">${uiLabelMap.RecruitmentInformation}</span></li>
				</ul>
			</div>
			<hr>
			<div class="step-content row-fluid position-relative">
				<form name="editApplicant" id="editApplicant" method = "post" action = "<@ofbizUrl>CreateApplicant</@ofbizUrl>">
					<div class="step-pane active" id="step1">
						<#include "recruitment/applicantGeneralInfo.ftl">
				    </div>
					
					<div class="step-pane" id="step2">
						<div class="row-fluid">
							<div class="span12 margin-top30" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px">
								<div class="title-border">
									<span>${uiLabelMap.ContactInformation}</span>
								</div>
								<table cellspacing="0">
							   		<tbody>
							   			<tr>
											<td><label class="padding-bottom5 padding-right15" for="">${uiLabelMap.PhoneMobile}</label></td>
											<td>
												<input type="text" name="phone_mobile" id="phone_mobile"/>
											</td>
											
											<td><label class="padding-bottom5 padding-right15 margin-left30" for="primaryEmailAddress">${uiLabelMap.PrimaryEmailAddress}</label></td>
			      							<td>
			       								<input type="text" size="60" maxlength="255" name="primaryEmailAddress" id="primaryEmailAddress" />
			      							</td>
										</tr>
										<tr>	
											<td><label class="padding-bottom5 padding-right15" for="phone_home">${uiLabelMap.PhoneHome}</label></td>
											<td>
												<input type="text" name="phone_home" id="phone_home"/>
											</td>
											<td><label class="padding-bottom5 padding-right15 margin-left30" for="otherEmailAddress">${uiLabelMap.OtherEmailAddress}</label></td>
			      							<td>
			       								<input type="text" size="60" maxlength="255" name="otherEmailAddress" id="otherEmailAddress" />
			      							</td>
										</tr>
										
									</tbody>
						   		</table>
							</div>
						</div>
						<div class="row-fluid">
							<div class="span12 margin-top30">
								<div class="span6" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px; margin-left: 0px;">
									<div class="title-border">
										<span>${uiLabelMap.PermanentResidence}</span>
									</div>
									<table cellspacing="0">
									<tr>
										<td>
											<label class="padding-bottom5 padding-right15 asterisk" for="address1_PermanentResidence">
												${uiLabelMap.PartyAddressLine}
											</label>
										</td>
										<td>
											<input type="text" name="address1_PermanentResidence" id="address1_PermanentResidence"/>
										</td>
									</tr>
									<tr>   
		      							<td><label class="padding-bottom5 padding-right15" for="permanentResidence_countryGeoId">
		      								${uiLabelMap.CommonCountry}</label></td>
		      							<td>     
		        							<select name="permanentResidence_countryGeoId" id="permanentResidence_countryGeoId">
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
		      							<td><label class="padding-bottom5 padding-right15" for="createEmployee_stateProvinceGeoId">${uiLabelMap.PartyState}</label></td>
		      							<td>
		       	 							<select name="permanentResidence_stateProvinceGeoId" id="permanentResidence_stateProvinceGeoId">
		        							</select>
		      							</td>
		    						</tr>
		    						<tr>
		      							<td>
		      								<label class="padding-bottom5 padding-right15" for="permanentResidence_districtGeoId">
		      									${uiLabelMap.PartyDistrictGeoId}
		      								</label>
	      								</td>
		      							<td>
		       	 							<select name="permanentResidence_districtGeoId" id="permanentResidence_districtGeoId">
		        							</select>
		      							</td>
		    						</tr>
		    						<tr>
		      							<td>
		      								<label class="padding-bottom5 padding-right15" for="permanentResidence_wardGeoId">
		      									${uiLabelMap.PartyWardGeoId}
		      								</label>
	      								</td>
		      							<td>
		       	 							<select name="permanentResidence_wardGeoId" id="permanentResidence_wardGeoId">
		        							</select>
		      							</td>
		    						</tr>
		    						</table>
								</div>
								<div class="span1" style="display: block; margin-top: 100px">
									<button class="btn btn-small btn-primary" id="copyContactInfo">
										<i class="icon-arrow-right"></i>
									</button>
								</div>
								<div class="span5" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px; margin-right: 0px;">
									<div class="title-border">
										<span>${uiLabelMap.CurrentResidence}</span>
									</div>
									<table cellspacing="0">
									<tr>
										<td>
											<label class="padding-bottom5 padding-right15" for="address1_CurrResidence">
												${uiLabelMap.PartyAddressLine}
											</label>
										</td>
										<td>
											<input type="text" name="address1_CurrResidence" id="address1_CurrResidence"/>
										</td>
									</tr>
									<tr>   
		      							<td><label class="padding-bottom5 padding-right15" for="currResidence_countryGeoId">${uiLabelMap.CommonCountry}</label></td>
		      							<td>     
		        							<select name="currResidence_countryGeoId" id="currResidence_countryGeoId">
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
		      							<td><label class="padding-bottom5 padding-right15" for="currResidence_stateProvinceGeoId">${uiLabelMap.PartyState}</label></td>
		      							<td>
		       	 							<select name="currResidence_stateProvinceGeoId" id="currResidence_stateProvinceGeoId">
		        							</select>
		      							</td>
		    						</tr>
		    						<tr>
		      							<td>
		      								<label class="padding-bottom5 padding-right15" for="currResidence_districtGeoId">
		      									${uiLabelMap.PartyDistrictGeoId}
		      								</label>
	      								</td>
		      							<td>
		       	 							<select name="currResidence_districtGeoId" id="currResidence_districtGeoId">
		        							</select>
		      							</td>
		    						</tr>
		    						<tr>
		      							<td>
		      								<label class="padding-bottom5 padding-right15" for="currResidence_wardGeoId">
		      									${uiLabelMap.PartyWardGeoId}
		      								</label>
	      								</td>
		      							<td>
		       	 							<select name="currResidence_wardGeoId" id="currResidence_wardGeoId">
		        							</select>
		      							</td>
		    						</tr>
		    						</table>
								</div>
							</div>
						</div>
						
					</div>
					<#--
					<!-- <div class="step-pane" id="step2">
						<table>
							<tr>
								<td ><label class="padding-bottom5 padding-right15" for="address1">${uiLabelMap.PartyAddressLine}</label></td>
								<td>
									<input type="text" name="address1" id="address1"/>
								</td>
							</tr>
							<tr>   
      							<td><label class="padding-bottom5 padding-right15" for="editApplicant_countryGeoId">${uiLabelMap.CommonCountry}</label></td>
      							<td>     
        							<select name="countryGeoId" id="editApplicant_countryGeoId">
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
      							<td><label class="padding-bottom5 padding-right15" for="editApplicant_stateProvinceGeoId">${uiLabelMap.PartyState}</label></td>
      							<td>
       	 							<select name="stateProvinceGeoId" id="editApplicant_stateProvinceGeoId">
        							</select>
      							</td>
    						</tr>
							<tr>
      							<td><label class="padding-bottom5 padding-right15" for="countryCode">${uiLabelMap.PartyPhoneNumber}</label></td>
      							<td>
       								<input type="text" size="4" maxlength="10" name="countryCode"  style="width: 50px" id="countryCode"/>
        							-&nbsp;<input type="text" size="4" maxlength="10" name="areaCode" style="width: 50px"/>
        							-&nbsp;<input type="text" size="15" maxlength="15" name="contactNumber" style="width: 150px"/>
        							&nbsp;${uiLabelMap.PartyContactExt}&nbsp;<input type="text" size="6" maxlength="10" style="width: 150px"/>
      							</td>
      							<td style="vertical-align: initial; padding-left: 14px;">[${uiLabelMap.CommonCountryCode}] [${uiLabelMap.PartyAreaCode}] [${uiLabelMap.PartyContactNumber}] [${uiLabelMap.PartyContactExt}]</td>
    						</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="emailAddress">${uiLabelMap.EmailAddress}</label></td>
      							<td>
       								<input type="text" size="60" maxlength="255" name="emailAddress" id="emailAddress" />
      							</td>
							</tr>
						</table>
					</div> -->
					<div class="step-pane" id="step3">
						<table>
							<tr>
								<td><label class="padding-bottom5 padding-right15 asterisk" for="jobRequestId">${uiLabelMap.JobRequestId}</label></td>
      							<td>
      								<@htmlTemplate.lookupField formName="editApplicant" name="jobRequestId" id="jobRequestId" fieldFormName="LookupJobRequest"/>
      							</td>
							</tr>
							<tr>
								<td><label class="padding-bottom5 padding-right15" for="description">${uiLabelMap.Description}</label></td>
      							<td>
       								<input type="text" name="description" id="description">
      							</td>
							</tr>
						</table>
					</div>
				</form>
			</div>
			
			<hr/>
			<#--
			<!-- <div class="row-fluid wizard-actions">
				<button class="btn btn-prev btn-small" id="btnPrev"><i class="icon-arrow-left"></i> Prev</button>
				<button class="btn btn-success btn-next btn-small" id="btnNext" data-last="Finish ">Next <i class="icon-arrow-right icon-on-right"></i></button>
			</div> -->
			<div class="row-fluid wizard-actions">
				<button class="btn btn-prev btn-small" id="btnPrev"><i class="icon-arrow-left"></i> ${uiLabelMap.CommonPrevious}</button>
				<button class="btn btn-success btn-next btn-small" id="btnNext" data-last="Finish ">${uiLabelMap.CommonNext} <i class="icon-arrow-right icon-on-right"></i></button>
			</div>
		</div>

	</div><!--/widget-main-->
</div> <!-- /widget-body-->

<script type="text/javascript">
$(function() {

	var $validation = false;
	$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
		if(info.direction == "next"){
			if(!$('#editApplicant').valid()) 
				return false;	
		}
	}).on('finished', function(e) {
		if(!$('#editApplicant').valid()) {
			return false;
		} else{
			document.getElementById("editApplicant").submit();
		}
	});
	
	$("#copyContactInfo").click(function(){
		address1 = jQuery("#address1_PermanentResidence").val();
		countryGeoId = jQuery("#permanentResidence_countryGeoId").val();
		stateGeoId = jQuery("#permanentResidence_stateProvinceGeoId").val();
		districtGeoId = jQuery("#permanentResidence_districtGeoId").val();
		wardGeoId = jQuery("#permanentResidence_wardGeoId").val();
		
		jQuery("#address1_CurrResidence").val(address1);
		jQuery("#currResidence_countryGeoId").val(countryGeoId);
		jQuery("#currResidence_countryGeoId").trigger("change");
		
		jQuery("#currResidence_stateProvinceGeoId").val(stateGeoId);
		jQuery("#currResidence_stateProvinceGeoId").trigger("change");
		
		jQuery("#currResidence_districtGeoId").val(districtGeoId);
		jQuery("#currResidence_districtGeoId").trigger("change");
		
		jQuery("#currResidence_wardGeoId").val(wardGeoId);
	});
	
	/* $("#btnNext").click(function(){
		$('#editApplicant').valid();
	}); */
	$.validator.addMethod('validateToday',function(value,element){
		if(value){
			today= new Date();
			today.setHours(0,0,0,0);
			return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss")<= today;
		}else{
			return true;
		}
	},'less than today');
	
	$.validator.addMethod('greatThan',function(value,element,params){
		if(!value){
			return true;
		}else{
//			console.log(Date.parse(value));
		 	return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss")>= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss");
		}
	},'great than from date');
	$('#editApplicant').validate({
		errorElement: 'span',
		errorClass: 'help-inline red-color',
		focusInvalid: false,
		errorPlacement: function(error, element) {
			element.addClass("border-error");
    		if (element.parent() != null ){   
				element.parent().find("button").addClass("button-border");     			
    			error.appendTo(element.parent());
			}
    	  },
    	unhighlight: function(element, errorClass) {
    		$(element).removeClass("border-error");
    		$(element).parent().find("button").removeClass("button-border");
    	},
		rules: {
			firstName: {
				required: true,
			},
			lastName: {
				required: true,
			},
			idNumber:{
				required: true,
			},
			jobRequestId:{
				required: true,
			},
			emailAddress: {
				required: true,
				email: true
			},
			currentPassword: {
				required: true,
				minlength: 6
			},
			address1_PermanentResidence: {
				required: true,
			},
			currentPasswordVerify: {
				required: true,
				minlength: 6,
				equalTo: "#currentPassword"
			},
			birthDate_i18n:{
				validateToday:true
			},
			passportIssueDate_i18n:{
				validateToday:true
			},
			idIssueDate_i18n:{
				validateToday:true
			},
			passportExpiryDate_i18n:{
				greatThan:'#passportIssueDate_i18n'
			},
			phone_mobile:{
				number: true
			},
			phone_work: {
				number: true,
			},
			phone_home:{
				number: true,
			}
		},

		messages: {
			firstName: {
				required: "${uiLabelMap.CommonRequired}",
			},
			lastName: {
				required: "${uiLabelMap.CommonRequired}",
			},
			idNumber: {
				required: "${uiLabelMap.CommonRequired}",
			},
			emailAddress: {
				required: "Please provide a valid email.",
				email: "Please provide a valid email."
			},
			jobRequestId:{
				required: "${uiLabelMap.CommonRequired}",
			},
			currentPassword: {
				required: "Please specify a password.",
				minlength: "Please specify a secure password."
			},
			address1_PermanentResidence: {
				required: "${uiLabelMap.CommonRequired}",
			},
			birthDate_i18n:{
				validateToday:"${uiLabelMap.HrolbiusRequiredBirthDay}"
			},
			passportIssueDate_i18n:{
				validateToday:"${uiLabelMap.HrolbiusRequiredPassPortDate}"
			},
			idIssueDate_i18n:{
				validateToday:"${uiLabelMap.HrolbiusRequiredPassPortDate}"
			},
			passportExpiryDate_i18n:{
				greatThan:'${uiLabelMap.HrolbiusRequiredPassPortExpiryDate}'
			},
			phone_mobile:{
				number: "${uiLabelMap.RequiredValueIsNumber}"
			},
			phone_work: {
				number: "${uiLabelMap.RequiredValueIsNumber}",
			},
			phone_home:{
				number: "${uiLabelMap.RequiredValueIsNumber}",
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