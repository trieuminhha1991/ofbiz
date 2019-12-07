<div id="adjustParticipateWindow" class="hide">
	<div>${uiLabelMap.AdjustEmplParticipateInsurance}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="asterisk">${uiLabelMap.CommonEmployee}</label>
							</div>
							<div class="span8">
								<input type="text" id="employeeName">
								<button class="btn btn-mini btn-primary" id="chooseEmplBtn" style="" 
									title="${StringUtil.wrapString(uiLabelMap.ClickToChooseEmpl)}">
									<i class="icon-plus icon-only"></i>
								</button>
							</div>
						</div>	
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.CommonDepartment}</label>
							</div>
							<div class="span8">
								<div id="dropDownAdjParticipate">
									<div style="border: none;" id="jqxTreeAdjParticipate">
									</div>
								</div>
							</div>
						</div>	
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="asterisk">${uiLabelMap.InsuranceNewlyParticipateFrom}</label>
							</div>
							<div class="span8">
								<div class="row-fluid">
									<div style="display: inline-block; margin-right: 5px" id="monthNewlyParticipateFrom" ></div>						
									<div style="display: inline-block;" id="yearNewlyParticipateFrom" ></div> 	
								</div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.SocialInsuranceNbr}</label>
							</div>
							<div class="span8">
								<input type="text" id="socialInsNbrAdjParticipate">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.ParticipateFrom}</label>
							</div>
							<div class="span8">
								<div class="row-fluid">
									<div style="display: inline-block; margin-right: 5px" id="monthStartParticipate" ></div>						
									<div style="display: inline-block;" id="yearStartParticipate" ></div> 	
								</div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.HealthInsuranceNbr}</label>
							</div>
							<div class="span8">
								<input type="text" id="healthInsuranceNbr">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.EffectiveFromDate}</label>
							</div>
							<div class="span8">
								<div class="row-fluid">
									<div style="display: inline-block; margin-right: 5px" id="monthFromHealthIns" ></div>						
									<div style="display: inline-block;" id="yearFromHealthIns" ></div> 	
								</div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="asterisk">${uiLabelMap.InsuranceSalaryShort}</label>
							</div>
							<div class="span8">
								<div id="insuranceSalAdjPariticipate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.InsuranceAllowancePositionFull}</label>
							</div>
							<div class="span8">
								<div id="insuranceAllowancePos"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.InsuranceAllowance} ${uiLabelMap.InsuranceAllowanceSeniorityExces}</label>
							</div>
							<div class="span8">
								<div id="allowanceSeniorExces"></div>
							</div>
						</div>
					</div>
					
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.Sexual}</label>
							</div>
							<div class="span8">
								<div class="row-fluid">
									<div class="span12">
										<div class="span3">
											<div id="genderAdjParticipate"></div>
										</div>
										<div class="span9">
											<div class="row-fluid">
												<div class="span5 text-algin-right">
													<label class="">${uiLabelMap.BirthDate}</label>
												</div>
												<div class="span7">
													<div id="birthDateParticipate"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.HrCommonPosition}</label>
							</div>
							<div class="span8">
								<div id="emplPositionTypeAdjParticipate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.CommonThruDate}</label>
							</div>
							<div class="span8">
								<div class="row-fluid">
									<div style="display: inline-block; margin-right: 5px" id="monthNewlyParticipateThru" ></div>						
									<div style="display: inline-block;" id="yearNewlyParticipateThru" ></div> 	
								</div>
							</div>
						</div>
						<#if insuranceTypeList?has_content>
							<#assign span = (12 / insuranceTypeList?size)?floor>
						</#if>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.InsuranceType}</label>
							</div>
							<div class="span8">
								<div class="row-fluid">
									<#if insuranceTypeList?has_content>
										<#list insuranceTypeList as insuranceType>
											<div class="span${span}">
												<div id="insuranceType${insuranceType.insuranceTypeId}" style="margin-left: 0px !important; padding-top: 5px">
													<span>${StringUtil.wrapString(insuranceType.description)}</span>
												</div>
											</div>
										</#list>
									</#if>
								</div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.InsuranceRateContribution}</label>
							</div>
							<div class="span8">
								<div class="row-fluid">
									<#if insuranceTypeList?has_content>
										<#list insuranceTypeList as insuranceType>
											<div class="span${span}">
												<div id="rate${insuranceType.insuranceTypeId}"></div>
											</div>
										</#list>
									</#if>
								</div>
							</div>
						</div>	
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.InsurancePrimaryHCEstablishment}</label>
							</div>
							<div class="span8">
								<input type="text" id="hospitalNameAdjParicipate">
								<button class="btn btn-mini btn-primary" id="chooseHospitalBtn" style="" title=""><i class="icon-list icon-only"></i></button>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.DateExpire}</label>
							</div>
							<div class="span8">
								<div class="row-fluid">
									<div style="display: inline-block; margin-right: 5px" id="monthThruHealthIns" ></div>						
									<div style="display: inline-block;" id="yearThruHealthIns" ></div> 	
								</div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class=""></label>
							</div>
							<div class="span8">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.InsuranceAllowance} ${uiLabelMap.InsuranceAllowanceSeniority}</label>
							</div>
							<div class="span8">
								<div id="allowanceSenior"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.InsuranceAllowance} ${uiLabelMap.InsuranceOtherAllowance}</label>
							</div>
							<div class="span8">
								<div id="allowanceOther"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div class="row-fluid no-left-margin">
				<div id="loadingAdjustParticipate" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAdjustParticipate"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelAdjParticipate" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveContinueAdjParticipate">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAdjParticipate">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>

<div id="reparticipateWindow" class="hide">
	<div>${uiLabelMap.AdjustEmplParticipateInsurance}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid" style="margin-top: 10px" id="reparticipateInfo">
				<div class="form-legend" style="margin-bottom: 10px">
					<div class="contain-legend">
						<span class="content-legend" >
							<a href="javascript:void(0)">${StringUtil.wrapString(uiLabelMap.InsuranceReparticipateInfo)}</a>
						</span>
					</div>
					<div class="row-fluid" style="margin-top: 5px; border-bottom: 1px dotted #e2e2e2">
						<div class="span12">
							<div class="span6">
								<div class='row-fluid margin-bottom10'>
									<div class="span3 text-algin-right">
										<label class="asterisk">${uiLabelMap.InsuranceReparticipateFrom}</label>
									</div>
									<div class="span9">
										<div style="display: inline-block; margin-right: 5px" id="monthStartReparticipate" ></div>						
										<div style="display: inline-block;" id="yearStartReparticipate"></div>
									</div>
								</div>
								<div class='row-fluid '>
									<div class="span3 text-algin-right">
										<label class="">${uiLabelMap.HRCommonToUppercase}</label>
									</div>
									<div class="span9">
										<div style="display: inline-block; margin-right: 5px" id="monthEndReparticipate" ></div>						
										<div style="display: inline-block;" id="yearEndReparticipate"></div>
									</div>
								</div>
							</div>
							<div class="span6">
								<div class='row-fluid margin-bottom10'>
									<div class="span3 text-algin-right">
										<label class="">${uiLabelMap.InsuranceType}</label>
									</div>
									<div class="span9">
										<div class="row-fluid">
											<#if insuranceTypeList?has_content>
												<#list insuranceTypeList as insuranceType>
													<div class="span${span}">
														<div id="insuranceTypeReParticipate${insuranceType.insuranceTypeId}" style="margin-left: 0px !important; padding-top: 5px">
															<span>${StringUtil.wrapString(insuranceType.description)}</span>
														</div>
													</div>
												</#list>
											</#if>
										</div>
									</div>
								</div>
								<div class='row-fluid'>
									<div class="span3 text-algin-right">
										<label class="">${uiLabelMap.InsuranceRateContribution}</label>
									</div>
									<div class="span9">
										<div class="row-fluid">
											<#if insuranceTypeList?has_content>
												<#list insuranceTypeList as insuranceType>
													<div class="span${span}">
														<div id="rateReparticipate${insuranceType.insuranceTypeId}"></div>
													</div>
												</#list>
											</#if>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row-fluid">
						<div class='row-fluid margin-top10'>
							<div id="isInsuranceSalaryUnchange" style="margin-left: -2px !important">
								<span style="font-size: 14px">${uiLabelMap.InsuranceSalaryUnchange}</span>
							</div>
						</div>
						<div class='row-fluid margin-top5'>
							<div class="span12">
								<div class="span3"><label>${uiLabelMap.InsuranceSalaryShort}</label></div>
								<div class="span3"><label>${uiLabelMap.InsuranceAllowancePositionFull}</label></div>
								<div class="span2"><label>${uiLabelMap.InsuranceAllowanceShort} ${uiLabelMap.InsuranceAllowanceSeniority}</label></div>
								<div class="span2"><label>${uiLabelMap.InsuranceAllowance} ${uiLabelMap.InsuranceAllowanceSeniorityExces}</label></div>
								<div class="span2"><label>${uiLabelMap.InsuranceAllowance} ${uiLabelMap.InsuranceOtherAllowance}</label></div>
							</div>
						</div>
						<div class='row-fluid'>
							<div class="span12">
								<div class="span3"><div id="reparticipateInsSal"></div></div>
								<div class="span3"><div id="reparticipateAllowancePos"></div></div>
								<div class="span2"><div id="reparticipateAllowanceSeniority"></div></div>
								<div class="span2"><div id="reparticipateAllowanceSeniorityExces"></div></div>
								<div class="span2"><div id="reparticipateAllowanceOther"></div></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div id="listEmplSuspendGrid"></div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingRearticipate" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerRearticipate"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelReparticipate" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveContinueReparticipate">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveReparticipate">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/insurance/InsuranceNewlyParticipate.js"></script>
<script type="text/javascript" src="/hrresources/js/insurance/InsuranceReparticipate.js"></script>