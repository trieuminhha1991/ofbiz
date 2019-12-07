<div id="RecruitmentSaleAddEmplWindow" class="hide">
	<div>${uiLabelMap.RecruitmentMoreEmpl}</div>
	<div class='form-window-container'>
		<div class='form-window-content' >
			<div class="row-fluid" >
				<div id="wizardAddEmpl" class="row-fluid hide" data-target="#stepAddEmpl">
			        <ul class="wizard-steps wizard-steps-square">
			                <li data-target="#recruitmentInfo" class="active">
			                    <span class="step">1. ${uiLabelMap.RecruitmentInfo}</span>
			                </li>
			                <li data-target="#profileInfo">
			                    <span class="step">2. ${uiLabelMap.EmployeeInfo}</span>
			                </li>
			                <li data-target="#contactInfo">
			                    <span class="step">3. ${uiLabelMap.ContactInformation}</span>
			                </li>
			    	</ul>
			    </div><!--#fuelux-wizard-->
			    <div class="step-content row-fluid position-relative" id="stepAddEmpl">
			    	<div class="step-pane active" id="recruitmentInfo">
						<div class="span12" style="margin-top: 20px">
							<div class="row-fluid">
								<div class="span12">
									<div class="span6">
										<div class='row-fluid margin-bottom10'>
						    				<div class="span4 text-algin-right">
												<label class="asterisk">${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}</label>
											</div>
											<div class="span8">
												<div id="dropDownButtonAddNew" class="">
													<div style="border: none;" id="jqxTreeAddNew">
													</div>
												</div>
											</div>
						    			</div>
						    			<div class='row-fluid margin-bottom10'>
											<div class='span4 align-right'>
												<label class="asterisk">${uiLabelMap.RecruitmentPlan}</label>
											</div>
											<div class="span8">
												<div class="row-fluid">
													<div class="span12">
														<div class="span6">
															<div id="monthCustomTimeNew"></div>
														</div>
														<div class="span6">
															<div id="yearCustomTimeNew"></div>
														</div>
													</div>
												</div>
											</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class="span4 text-algin-right">
												<label class="">${uiLabelMap.SalaryBaseFlat}</label>
											</div>
											<div class="span8">
												<div id="salaryBaseFlat"></div>
											</div>
										</div>
									</div>
									<div class="span6">
										<div class='row-fluid margin-bottom10'>
											<div class='span5 align-right'>
												<label class="asterisk">${uiLabelMap.RecruitingPosition}</label>
											</div>
											<div class="span7">
												<div id="emplPositionTypeAddNew"></div>
											</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class="span5 text-algin-right">
												<label class="asterisk">${uiLabelMap.RecruitmentEnumType}</label>
											</div>
											<div class="span7">
												<div id="enumRecruitmentTypeNew"></div>
											</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class='span5 align-right'>
												<label class="asterisk">${uiLabelMap.StarWorkingFrom}</label>
											</div>
											<div class="span7">
												<div id="startWorkingFromDate"></div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span12">
									<div class='row-fluid margin-bottom10'>
										<div class='span2 align-right'>
											<label class="">${uiLabelMap.HRNotes}</label>
										</div>
										<div class="span10" style="margin-left: 16px">
											<textarea id="commentAddNew"></textarea>
										</div>
									</div>
								</div>							
							</div>
						</div>									    		
			    	</div>
			    	<div class="step-pane" id="profileInfo">
			    		<div class="span12 boder-all-profile" style="padding-bottom: 10px; margin-bottom: 15px">
				    		<#include "/basehr/webapp/basehr/generalMgr/AddNewEmployeeProfileInfo.ftl"/>
			    		</div>
			    	</div>
			    	<div class="step-pane" id="contactInfo">
			    		<#include "/basehr/webapp/basehr/generalMgr/AddNewEmployeeContactInfo.ftl"/>
			    	</div>
			    </div>
			    <div class="form-action wizard-actions">
					<button class="btn btn-next btn-success form-action-button pull-right" data-last="${uiLabelMap.CommonSave}" id="btnNext">
						${uiLabelMap.CommonNext}
						<i class="icon-arrow-right icon-on-right"></i>
					</button>
					<button class="btn btn-prev form-action-button pull-right" id="btnPrev">
						<i class="icon-arrow-left"></i>
						${uiLabelMap.CommonPrevious}
					</button>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="loadingAddNewEmpl" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerAddNewEmpl"></div>
					</div>
				</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript">
globalVar.defaultSuffix = "${defaultSuffix?if_exists}";
globalVar.profileInfoDiv = "profileInfo";
</script>
<script type="text/javascript" src="/hrresources/js/generalMgr/AddNewEmployeeProfileInfo.js"></script>
<script type="text/javascript" src="/hrresources/js/generalMgr/AddNewEmployeeContactInfo.js"></script>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmentSalesAddNewEmpl.js"></script>