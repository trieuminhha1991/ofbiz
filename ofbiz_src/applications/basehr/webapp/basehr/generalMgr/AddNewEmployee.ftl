<#if !defaultSuffix?exists>
	<#assign defaultSuffix = ""/>
</#if>
<#if !includeJs?exists || includeJs == "true">
	<#include "component://basehr/webapp/basehr/generalMgr/script/AddNewEmployeeScript.ftl"/>
	<script type="text/javascript" src="/hrresources/js/generalMgr/AddNewEmployeeProfileInfo.js"></script>
	<script type="text/javascript" src="/hrresources/js/generalMgr/AddNewEmployeeContactInfo.js"></script>
	<script type="text/javascript" src="/hrresources/js/generalMgr/AddNewEmployee.js?v=0.0.1"></script>
	<#include "component://basehr/webapp/basehr/insurance/script/HosipitalListScript.ftl"/>
	<script type="text/javascript" src="/hrresources/js/insurance/HosipitalListScript.js"></script>	
</#if>

<div class='form-window-content' >
	<div class="row-fluid" >
	    <div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
	        <ul class="wizard-steps wizard-steps-square">
	                <li data-target="#generalInfo" class="active">
	                    <span class="step">1. ${uiLabelMap.GeneralInformation}</span>
	                </li>
	                <li data-target="#contactInfo">
	                    <span class="step">2. ${uiLabelMap.ContactInformation}</span>
	                </li>
	                <li data-target="#employmentInfo">
	                    <span class="step">3. ${uiLabelMap.EmployeeWorkInformation}</span>
	                </li>
	                <li data-target="#userLoginInfo">
	                    <span class="step">4. ${uiLabelMap.UserLoginInformation}</span>
	                </li>
	    	</ul>
	    </div><!--#fuelux-wizard-->
	   	<div class="step-content row-fluid position-relative" id="step-container">
			<div class="step-pane active" id="generalInfo">
				<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 15px">
					<#include "AddNewEmployeeProfileInfo.ftl"/> 
				</div>
			</div>
			<div class="step-pane" id="contactInfo">
				<div class="span12">
					<#include "AddNewEmployeeContactInfo.ftl"/>
				</div>
			</div>
			<div class="step-pane" id="employmentInfo">
				<div class="row-fluid" style="margin-top: 10px">
					<div class="form-legend" style="margin-bottom: 25px">
						<div class="contain-legend">
							<span class="content-legend" >
								${StringUtil.wrapString(uiLabelMap.EmployeeInfo)}
							</span>
						</div>
						<div class="row-fluid" style="">
							<div class="span12">
								<div class="span6">
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${uiLabelMap.PartyIdWork}</label>
										</div>
										<div class="span7">
											<div id="dropDownButton${defaultSuffix}">
												<div style="border: none;" id="jqxTree${defaultSuffix}">
												</div>
											</div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${uiLabelMap.HrCommonPosition}</label>
										</div>
										<div class="span7">
											<div id="emplPositionTypeId${defaultSuffix}" style="display: inline-block; float: left; margin-right: 3px"></div>
											<button id="addEmplPositionTypeBtn${defaultSuffix}" class="btn btn-mini btn-primary" title="${uiLabelMap.CommonAddNew}" style="float: left;">
												<i class="icon-only icon-plus open-sans"></i>
											</button>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${uiLabelMap.DateJoinCompany}</label>
										</div>
										<div class="span7">
											<div id="dateJoinCompany${defaultSuffix}"></div>
										</div>
									</div>
									<div class='row-fluid'>
										<div class="span5 text-algin-right">
											<label class="">${uiLabelMap.HRCommonCurrStatus}</label>
										</div>
										<div class="span7">
											<div id="statusId${defaultSuffix}"></div>
										</div>
									</div>
									
								</div>
								<div class="span6">
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${uiLabelMap.SalaryBaseFlat}</label>
										</div>
										<div class="span7">
											<div class="row-fluid">
												<div id="salaryBaseFlat${defaultSuffix}" style="display: inline-block; float: left; margin-right: 3px"></div>
												<!-- <button id="updateSalBasePosition" title="${StringUtil.wrapString(uiLabelMap.UpdateByEmplPositionTypeSalary)}"
													class="btn btn-primary btn-mini" style="float: left;"><i class="icon-only icon-align-justify"></i></button>	 -->
											</div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${uiLabelMap.PeriodTypePayroll}</label>
										</div>
										<div class="span7">
											<div id="periodType${defaultSuffix}"></div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="">${uiLabelMap.HREmplReasonResign}</label>
										</div>
										<div class="span7">
											<div id="reasonResign${defaultSuffix}"></div>
										</div>
									</div>
									<div class='row-fluid'>
										<div class="span5 text-algin-right">
											<label class="">${uiLabelMap.HREmplResignDate}</label>
										</div>
										<div class="span7">
											<div id="resignDate${defaultSuffix}"></div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row-fluid">
					<div class="form-legend" style="margin-bottom: 0px">
						<div class="contain-legend">
							<div class="content-legend" style="display: inline-block;">
								<div id="isParticipateIns" style="margin-left: 0px !important; display: inline-block; font-size: 14px">${uiLabelMap.ParticipateInsuranceAtCompany}</div>
							</div>
						</div>
						<div class="row-fluid disabledArea" id="insuranceInfo${defaultSuffix}" style="">
							<div class="span12">
								<div class="span6">
									<div class='row-fluid margin-bottom10'>
										<div class="span4 text-algin-right">
											<label class="">${uiLabelMap.SocialInsuranceNbr}</label>
										</div>
										<div class="span8">
											<input type="text" id="insuranceSocialNbr${defaultSuffix}">
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span4 text-algin-right">
											<label class="">${uiLabelMap.ParticipateInsuranceFrom}</label>
										</div>
										<div class="span8">
											<div id="insParticipateFrom${defaultSuffix}"></div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span4 text-algin-right">
											<label class="">${uiLabelMap.InsuranceNewlyParticipateFrom}</label>
										</div>
										<div class="span8">
											<div style="display: inline-block; margin-right: 5px" id="monthFromParticipate${defaultSuffix}" ></div>						
											<div style="display: inline-block;" id="yearFromParticipate${defaultSuffix}"></div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span4 text-algin-right">
											<label class="">${uiLabelMap.InsuranceSalaryShort}</label>
										</div>
										<div class="span8">
											<div id="insuranceSalary${defaultSuffix}"></div>
										</div>
									</div>
									<div class='row-fluid'>
										<div class="span4 text-algin-right">
											<label class="">${uiLabelMap.InsuranceType}</label>
										</div>
										<div class="span8">
											<#if insuranceTypeList?has_content>
												<#assign span = (12 / insuranceTypeList?size)?floor>
											</#if>
											<div class="row-fluid">
												<#if insuranceTypeList?has_content>
													<#list insuranceTypeList as insuranceType>
														<div class="span${span}">
															<div id="insuranceType${insuranceType.insuranceTypeId}${defaultSuffix}" style="margin-left: 0px !important; padding-top: 5px">
																<span>${StringUtil.wrapString(insuranceType.description)}</span>
															</div>
														</div>
													</#list>
												</#if>
											</div>
										</div>
									</div>
								</div>
								<div class="span6">
									<div class='row-fluid margin-bottom10'>
										<div class="span4 text-algin-right">
											<label class="">${uiLabelMap.HealthInsuranceNbr}</label>
										</div>
										<div class="span8">
											<input type="text" id="insuranceHealth${defaultSuffix}">
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span4 text-algin-right">
											<label class="">${uiLabelMap.EffectiveFromDate}</label>
										</div>
										<div class="span8">
											<div style="display: inline-block; margin-right: 5px" id="monthFromHealthIns${defaultSuffix}" ></div>						
											<div style="display: inline-block;" id="yearFromHealthIns${defaultSuffix}"></div>
											<!-- <div id="effectiveFromDate"></div> -->
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span4 text-algin-right">
											<label class="">${uiLabelMap.DateExpire}</label>
										</div>
										<div class="span8">
											<div style="display: inline-block; margin-right: 5px" id="monthThruHealthIns${defaultSuffix}" ></div>						
											<div style="display: inline-block;" id="yearThruHealthIns${defaultSuffix}"></div>
											<!-- <div id="effectiveThruDate"></div> -->
										</div>
									</div>
									<div class='row-fluid'>
										<div class="span4 text-algin-right">
											<label class="">${uiLabelMap.HealthCareProviderShort}</label>
										</div>
										<div class="span8">
											<div class="row-fluid">
												<input id="healthCareProvider${defaultSuffix}" type="text">
												<button id="searchHealthCareProvider${defaultSuffix}" title="${StringUtil.wrapString(uiLabelMap.ChooseHealthCareProvider)}"
													class="btn btn-primary btn-mini"><i class="icon-only icon-align-justify"></i></button>									
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="step-pane" id="userLoginInfo">
				<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 15px; width: 800px; height:200px;">
					<div class="row-fluid">
						<div class="span12">
							<div class='row-fluid margin-bottom10'>
								<div class="span3 text-algin-right">
									<label class="asterisk">${uiLabelMap.userLoginId}</label>
								</div>
								<div class="span6">
									<input type="text" id="userLoginId${defaultSuffix}">
								</div>
							</div>
							<div class='row-fluid margin-bottom10' style="margin-left: 4.8%">
								<div class="span3 text-algin-right">									
								</div>
								<div class="span6" id="useDefaultPwd${defaultSuffix}">
									<label>${uiLabelMap.HRUseDefaultPassword}</label>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class="span3 text-algin-right">
									<label class="asterisk">${uiLabelMap.CommonPassword}</label>
								</div>
								<div class="span6">
									<input type="password" id="password${defaultSuffix}">
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class="span3 text-algin-right">
									<label class="asterisk">${uiLabelMap.HRConfirmPassword}</label>
								</div>
								<div class="span6">
									<input type="password" id="confirmPassword${defaultSuffix}">
								</div>
							</div>
						</div>
					</div>		
				</div>
			</div>
	    </div>
	    <div class="form-action wizard-actions">
			<button class="btn btn-next btn-success form-action-button pull-right" data-last="${StringUtil.wrapString(uiLabelMap.HRCommonCreateNew)}" id="btnNext">
				${uiLabelMap.CommonNext}
				<i class="icon-arrow-right icon-on-right"></i>
			</button>
			<button class="btn btn-prev form-action-button pull-right" id="btnPrev">
				<i class="icon-arrow-left"></i>
				${uiLabelMap.CommonPrevious}
			</button>
		</div>
	</div>
</div>

<#include "component://basehr/webapp/basehr/insurance/ViewListHosipitalList.ftl"/>

<div id="listHospitalWindow" class="hide">
	<div>${StringUtil.wrapString(uiLabelMap.ScreenletTitle_HealthCareProvider)}</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<div id="hospitalList"></div>
		</div>
	</div>
</div>