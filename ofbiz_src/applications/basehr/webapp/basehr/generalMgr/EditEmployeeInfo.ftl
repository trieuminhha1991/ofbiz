<#if !editEmplWindow?has_content>
	<#assign editEmplWindow = "editEmplWindow"/>  
</#if>
<#if security.hasEntityPermission("HR_DIRECTORY", "_ADMIN", session)>
    <#assign hasEditPermisstion = "true" />
<#else>
    <#assign hasEditPermisstion = "false" />
</#if>
<script type="text/javascript">
if(typeof(globalVar) == 'undefined'){
	globalVar = {};
}
globalVar.editEmplWindow = "${editEmplWindow}";
globalVar.hasEditPermisstion = "${hasEditPermisstion?string}";

if(typeof(uiLabelMap) == 'undefined'){
	uiLabelMap = {};
}
uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
if(typeof(globalVar.geoCountryList) == 'undefined'){
	globalVar.geoCountryList = [
		<#if geoCountryList?has_content>
			<#list geoCountryList as geo>
				{
					geoId: '${geo.geoId}',
					geoName: "${StringUtil.wrapString(geo.geoName?if_exists)}"
				},
			</#list>
		</#if>                  	
	];
}

if(typeof(globalVar.geoArr) == 'undefined'){
	globalVar.geoArr = [
	           <#if geoList?has_content>
	      		<#list geoList as geo>
	      			{
	      				geoId: '${geo.geoId}',
	      				geoName: "${StringUtil.wrapString(geo.geoName?if_exists)}"
	      			},
	      		</#list>
	      	 </#if>
	      ];
}

if(typeof(globalVar.religionTypes) == 'undefined'){
	globalVar.religionTypes = [
       	<#if listReligionTypes?has_content>
       		<#list listReligionTypes as religionT>
       		{
       	    	religionId : "${religionT.religionId}",
       	        description : "${StringUtil.wrapString(religionT.description)}"
       		},
       		</#list>	
       	</#if>
       ];
}

       
if(typeof(globalVar.ethnicOriginList) == 'undefined'){
	globalVar.ethnicOriginList = [
		<#if ethnicOriginList?has_content>               
		<#list ethnicOriginList as ethnicOrigin1>
		{
			ethnicOriginId : "${ethnicOrigin1.ethnicOriginId}",
			description : "${StringUtil.wrapString(ethnicOrigin1.description)}"
		},
		</#list>	
		</#if>
	];
}       

if(typeof(globalVar.maritalStatusList) == 'undefined'){
	globalVar.maritalStatusList = [
		<#if maritalStatusList?has_content>               
	    	<#list maritalStatusList as maritalStatus1>
	    	{
	    		maritalStatusId : "${maritalStatus1.statusId}",
	    		description : "${StringUtil.wrapString(maritalStatus1.description)}"
	    	},
	    	</#list>	
    	</#if>
    ];	
}

if(typeof(globalVar.nationalityTypes) == 'undefined'){
	globalVar.nationalityTypes = [
     	<#if listNationalityTypes?has_content>
     		<#list listNationalityTypes as nationalityT >
     	    {
     	    	nationalityId : "${nationalityT.nationalityId}",
     	    	description : "${StringUtil.wrapString(nationalityT.description)}"
     	    },
     	    </#list>	
     	</#if>
     ];	
}

if(typeof(globalVar.genderList) == 'undefined'){
	globalVar.genderList = [
	  	<#if genderList?has_content>                   
	  		<#list genderList as gender1>
	  		{
	  			genderId : "${gender1.genderId}",
	  			description : "${StringUtil.wrapString(gender1.description)}"
	  		},
	  		</#list>	
	  	</#if>
	];
}
</script>
<div id="${editEmplWindow}" class="hide">
	<div>${uiLabelMap.CommonEdit}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div class="row-fluid" >
				<div id="fuelux-wizard${editEmplWindow}" class="row-fluid hide" data-target="#step-container${editEmplWindow}">
			        <ul class="wizard-steps wizard-steps-square">
			                <li data-target="#generalInfo${editEmplWindow}" class="active">
			                    <span class="step">1. ${uiLabelMap.GeneralInformation}</span>
			                </li>
			                <li data-target="#contactInfo${editEmplWindow}">
			                    <span class="step">2. ${uiLabelMap.ContactInformation}</span>
			                </li>
			    	</ul>
			    </div><!--#fuelux-wizard-->
			    <div class="step-content row-fluid position-relative" id="step-container${editEmplWindow}">
					<div class="step-pane active" id="generalInfo${editEmplWindow}">
						<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 10px">
							<div class="span2">
								<span class="profile-picture">
									<img class="personal-image" id="avatar${editEmplWindow}" src="/aceadmin/assets/avatars/no-avatar.png" alt="Avatar" style="cursor: pointer;" 
										title="${uiLabelMap.ClickToChangeAvatar}">
								</span>
							</div>	
							<div class="span5">
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="asterisk">${uiLabelMap.EmployeeIdShort}</label>
									</div>  
									<div class="span8">
										<input type="text" id="employeeId${editEmplWindow}">
							   		</div>
								</div>						
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="asterisk">${uiLabelMap.LastName}</label>
									</div>
									<div class="span8">
										<input type="text" id="lastName${editEmplWindow}" name="lastName"/>
									</div>
								</div>					
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.MiddleName}</label>
									</div>
									<div class="span8">
										<input type="text" id="middleName${editEmplWindow}" name="middleName"/>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="asterisk">${uiLabelMap.FirstName}</label>
									</div>  
									<div class="span8">
										<input type="text" id="firstName${editEmplWindow}" name="firstName"/>
							   		</div>
								</div>	
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.certProvisionId}</label>
									</div>
									<div class="span8">
										<input id="editIdNumber${editEmplWindow}" type="text"/>
									</div>
								</div>						
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HrolbiusidIssueDate}</label>
									</div>
									<div class="span8">
										<div id="idIssueDateTime${editEmplWindow}"></div>
									</div>
								</div>				
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HrolbiusidIssuePlace}</label>
									</div>
									<div class="span8">
										<div id="idIssuePlaceDropDown${editEmplWindow}"></div>
									</div>
								</div>						
							</div> 
							<div class="span5">
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.PartyGender}</label>
									</div>
									<div class="span8">
										<div id="gender${editEmplWindow}"></div>
									</div>
								</div>		
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.PartyBirthDate}</label>
									</div>
									<div class="span8">
										<div id="birthDate${editEmplWindow}"></div>
									</div>
								</div>	
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.BirthPlace}</label>
									</div>
									<div class="span8">
										<input type="text" id="birthPlace${editEmplWindow}"/>
									</div>
								</div>	
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.NativeLand}</label>
									</div>
									<div class="span8">
										<input type="text" id="nativeLandInput${editEmplWindow}">
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.EthnicOrigin}</label>
									</div>
									<div class="span8">
										<div id="ethnicOriginDropdown${editEmplWindow}"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HrolbiusReligion}</label>
									</div>
									<div class="span8">
										<div id="religionDropdown${editEmplWindow}"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.HrolbiusNationality}</label>
									</div>
									<div class="span8">
										<div id="nationalityDropdown${editEmplWindow}"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class='span4 text-algin-right'>
										<label class="">${uiLabelMap.MaritalStatus}</label>
									</div>
									<div class="span8">
										<div id="maritalStatusDropdown${editEmplWindow}"></div>
									</div>
								</div>
							</div> 
						</div>
					</div>
					<div class="step-pane" id="contactInfo${editEmplWindow}">
						<div class="row-fluid">
							<div class="span12">
								<div class="span6">
									<div class="row-fluid" id="permanentResidence${editEmplWindow}">
										<div class="span12 boder-all-profile">
											<span class="text-header">${uiLabelMap.PermanentResidence}</span>
											<div class='row-fluid margin-bottom10'>
												<div class='span4 text-algin-right'>
													<label class="">${uiLabelMap.CommonAddress1}</label>
												</div>  
												<div class="span8">
													<input id="paddress1${editEmplWindow}" type="text">
										   		</div>
											</div>
											<div class='row-fluid margin-bottom10'>
												<div class="span4 text-algin-right">
													<label class="">${uiLabelMap.CommonCountry}</label>
												</div>
												<div class="span8">
													<div id="countryGeoIdPermRes${editEmplWindow}"></div>
												</div>
											</div>
											<div class='row-fluid margin-bottom10'>
												<div class="span4 text-algin-right">
													<label class="">${uiLabelMap.CommonCity}</label>
												</div>
												<div class="span8">
													<div id="stateGeoIdPermRes${editEmplWindow}"></div>
												</div>
											</div>
											<div class='row-fluid margin-bottom10'>
												<div class="span4 text-algin-right">
													<label class="">${uiLabelMap.PartyDistrictGeoId}</label>
												</div>
												<div class="span8">
													<div id="countyGeoIdPermRes${editEmplWindow}"></div>
												</div>
											</div>
											<div class='row-fluid margin-bottom10'>
												<div class="span4 text-algin-right">
													<label class="">${uiLabelMap.DmsWard}</label>
												</div>
												<div class="span8">
													<div id="wardGeoIdPermRes${editEmplWindow}"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="span6">
									<div class="row-fluid">
										<div class="span12 boder-all-profile" id="currentResidence${editEmplWindow}">
											<span class="text-header">
												${uiLabelMap.CurrentResidence}
												<button title="${StringUtil.wrapString(uiLabelMap.CopyPermanentResidence)}" id="copyPermRes${editEmplWindow}" class="grid-action-button fa-files-o" style="margin: 0; padding: 0 !important"></button>
											</span>
											<div class='row-fluid margin-bottom10'>
												<div class="span4 text-algin-right">
													<label class="">${uiLabelMap.CommonAddress1}</label>
												</div>
												<div class="span8">
													<input type="text" id="address1CurrRes${editEmplWindow}">
												</div>
											</div>
											<div class='row-fluid margin-bottom10'>
												<div class="span4 text-algin-right">
													<label class="">${uiLabelMap.CommonCountry}</label>
												</div>
												<div class="span8">
													<div id="countryGeoIdCurrRes${editEmplWindow}"></div>
												</div>
											</div>
											<div class='row-fluid margin-bottom10'>
												<div class="span4 text-algin-right">
													<label class="">${uiLabelMap.CommonCity}</label>
												</div>
												<div class="span8">
													<div id="stateGeoIdCurrRes${editEmplWindow}"></div>
												</div>
											</div>
											<div class='row-fluid margin-bottom10'>
												<div class="span4 text-algin-right">
													<label class="">${uiLabelMap.PartyDistrictGeoId}</label>
												</div>
												<div class="span8">
													<div id="countyGeoIdCurrRes${editEmplWindow}"></div>
												</div>
											</div>
											<div class='row-fluid margin-bottom10'>
												<div class="span4 text-algin-right">
													<label class="">${uiLabelMap.DmsWard}</label>
												</div>
												<div class="span8">
													<div id="wardGeoIdCurrRes${editEmplWindow}"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="row-fluid">
							<div class="span12 boder-all-profile">
								<div class="span6">
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.PhoneNumber}</label>
									</div>
									<div class="span8">
										<input type="text" id="phoneMobile${editEmplWindow}">
									</div>
								</div>
								<div class="span6">
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.HRCommonEmail}</label>
									</div>
									<div class="span8">
										<input type="text" id="emailAddress${editEmplWindow}">
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="form-action wizard-actions">
					<button class="btn btn-next btn-success form-action-button pull-right" data-last="${StringUtil.wrapString(uiLabelMap.CommonUpdate)}" 
						id="btnNext${editEmplWindow}">
						${uiLabelMap.CommonNext}
						<i class="icon-arrow-right icon-on-right"></i>
					</button>
					<button class="btn btn-prev form-action-button pull-right" id="btnPrev${editEmplWindow}">
						<i class="icon-arrow-left"></i>
						${uiLabelMap.CommonPrevious}
					</button>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoading${editEmplWindow}" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinner-ajax${editEmplWindow}"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/generalMgr/EditEmployeeInfo.js"></script>