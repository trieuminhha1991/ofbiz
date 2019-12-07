<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxpasswordinput.js"></script>
<div class="widget-body">	 
	<div class="widget-main">
		<div class="row-fluid">
			<div id="fuelux-wizard" class="row-fluid">
				<ul class="wizard-steps">
					<li data-target="#step1" style="min-width: 25%; max-width: 25%;" class="active"><span class="step">1</span> <span class="title">${uiLabelMap.GeneralInformation}</span></li>
					<li data-target="#step2" style="min-width: 25%; max-width: 25%;" ><span class="step">2</span> <span class="title">${uiLabelMap.ContactInformation}</span></li>
					<li data-target="#step3" style="min-width: 25%; max-width: 25%;" ><span class="step">3</span> <span class="title">${uiLabelMap.JobInformation}</span></li>
					<li data-target="#step4" style="min-width: 25%; max-width: 25%;" ><span class="step">4</span> <span class="title">${uiLabelMap.LoginInformation}</span></li>
				</ul>
			</div>
			
			<hr>
			
			<div class="step-content row-fluid position-relative">
				<form name="createEmployee" id="createEmployee" method = "post" action = "<@ofbizUrl>createEmployee</@ofbizUrl>">
					<input type="hidden" value="${internalOrgId?if_exists}" name="internalOrgId">
					<div class="step-pane active" id="step1">
						<#--<!-- <#include "recruitment/applicantGeneralInfo.ftl"> -->
						<#include "employee/createEmployeeForm/employeeGeneralInfo.ftl">
						<#include "employee/createEmployeeForm/employeeEducation.ftl">
						<#include "employee/createEmployeeForm/employeeWorkingProcess.ftl">
						<#include "employee/createEmployeeForm/employeeFamily.ftl">
						<#include "employee/createEmployeeForm/employeeSkill.ftl">
				    </div>			    		    
					<div class="step-pane" id="step2">
						<#include "employee/createEmployeeForm/employeeContactInfo.ftl"/>
						<#--<!-- <div class="row-fluid">
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
											
											<td>
												<label class="padding-bottom5 padding-right15 margin-left30" for="primaryEmailAddress">
													${uiLabelMap.EmailAddressPrimary}
												</label>
											</td>
			      							<td>
			       								<input type="text" size="60" maxlength="255" name="primaryEmailAddress" id="primaryEmailAddress" />
			      							</td>
										</tr>
										<tr>	
											<td><label class="padding-bottom5 padding-right15" for="phone_work">${uiLabelMap.PhoneWork}</label></td>
											<td>
												<input type="text" name="phone_work" id="phone_work"/>
											</td>
											
											<td><label class="padding-bottom5 padding-right15 margin-left30" for="personalEmailAddress">${uiLabelMap.PersonalEmailAddress}</label></td>
			      							<td>
			       								<input type="text" size="60" maxlength="255" name="personalEmailAddress" id="personalEmailAddress" />
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
						</div> -->
						<#--<!-- <div class="row-fluid">
							<div class="span12 margin-top30">
								<div class="span6" style="border: 1px solid #EEE; padding: 15px; border-radius: 5px; margin-left: 0px;">
									<div class="title-border">
										<span>${uiLabelMap.PermanentResidence}</span>
									</div>
									<table cellspacing="0">
									<tr>
										<td>
											<label class="padding-bottom5 padding-right15" for="address1_PermanentResidence">
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
						</div> -->
					</div>
					<div class="step-pane" id="step3">
						<#include "employee/createEmployeeForm/employeeWorkInfo.ftl">
					</div>
					<div class="step-pane" id="step4">
						<#include "employee/createEmployeeForm/employeeUserLoginInfo.ftl">
					</div>
				</form>
			</div>
			
			<hr/>
					
			<div class="row-fluid wizard-actions">
				<button class="btn btn-prev btn-small"><i class="icon-arrow-left"></i> Prev</button>
				<button class="btn btn-success btn-next btn-small" id="next" data-last="Finish">Next <i class="icon-arrow-right icon-on-right"></i></button>
			</div>
		</div>

	</div><!--/widget-main-->
</div> <!-- /widget-body-->

<script type="text/javascript">
$(function() {

	var $validation = false;
	$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(!$('#createEmployee').valid()) return false;
	}).on('finished', function(e) {
		document.getElementById("createEmployee").submit();
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
	$.validator.addMethod('validateToDay',function(value,element){
			if(value!=null||value!=undefined){
				var now = new Date();
				now.setHours(0,0,0,0);
				return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss")<=now;
			}else{
				return true;
			}
		},'less than today');
	$.validator.addMethod("greaterThan", 
		function(value, element, params) {
			if(value){
			 	return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss");	
			 			
			}else{
				return true;
			}
		},'Must be greater than');
		
	$('#createEmployee').validate({
		errorElement: 'span',
		errorClass: 'help-inline red-color',
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
		focusInvalid: false,
		rules: {
			firstName: {
				required: true,
			},
			lastName: {
				required: true,
			},
			emailAddress: {
				required: true,
				email: true
			},
			userLoginId:{
				required: true,
			},
			currentPassword: {
				required: true,
				minlength: 6
			},
			currentPasswordVerify: {
				required: true,
				minlength: 6,
				equalTo: "#currentPassword"
			},
			birthDate_i18n:{
				validateToDay:true,
			},
			passportIssueDate_i18n:{
				validateToDay:true
			},
			idIssueDate_i18n:{
				validateToDay:true
			},
			passportExpiryDate_i18n:{
				greaterThan:'#passportIssueDate_i18n'
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
				required: "<span style='color:red;'>Bắt buộc</span>",
			},
			lastName: {
				required: "<span style='color:red;'>Bắt buộc</span>",
			},
			emailAddress: {
				required: "<span style='color:red;'>Bắt buộc</span>",
				email: "<span style='color:red;'>Hãy nhập email đúng định dạng</span>"
			},
			userLoginId:{
				required: "<span style='color:red;'>Bắt buộc</span>",
			},
			currentPassword: {
				required: "<span style='color:red;'>Bắt buộc</span>",
				minlength: "<span style='color:red;'>Mật khẩu phải 6 kí tự trở lên</span>"
			},
			currentPasswordVerify: {
				required: "<span style='color:red;'>Bắt buộc</span>",
				minlength: "<span style='color:red;'>Mật khẩu phải 6 kí tự trở lên</span>",
				equalTo: "<span style='color:red;'>Mật khẩu nhập lại không khớp</span>"
			},
			birthDate_i18n:{
				validateToDay:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredBirthDay)}'
			},
			passportIssueDate_i18n:{
				validateToDay:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredPassPortDate)}'
			},
			passportExpiryDate_i18n:{
				greaterThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredPassPortExpiryDate)}'
			},
			idIssueDate_i18n:{
				validateToDay:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredPassPortDate)}'
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