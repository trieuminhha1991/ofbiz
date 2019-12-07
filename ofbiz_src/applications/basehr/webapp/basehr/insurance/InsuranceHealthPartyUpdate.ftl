<div id="insuranceHealthUpdateWindow" class="hide">
	<div>${uiLabelMap.UpdateInsuranceHealthInfo}</div>
	<div class='form-window-container'>
		<div class="form-window-content" style="position: relative;">
			<div class="row-fluid">
				<div class="span12">
					<div class="span5">
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.EmployeeId}</label>
							</div>
							<div class="span8">
								<input type="text" id="insHealthUpdatePartyCode">
							</div>
						</div>
						<div class='row-fluid'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.SocialInsuranceNbr}</label>
							</div>
							<div class="span8">
								<input type="text" id="insHealthUpdateSocialInsNbr">
							</div>
						</div>
					</div>
					<div class="span7">
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.EmployeeName}</label>
							</div>
							<div class="span8">
								<input type="text" id="insHealthUpdateFullName">
							</div>
						</div>
						<div class='row-fluid'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.Sexual}</label>
							</div>
							<div class="span8">
								<div class="row-fluid">
									<div class="span12">
										<div class="span3">
											<div id="insHealthUpdateGender"></div>
										</div>
										<div class="span9">
											<div class="row-fluid">
												<div class="span5 text-algin-right">
													<label class="">${uiLabelMap.BirthDate}</label>
												</div>
												<div class="span7">
													<div id="insHealthUpdateBirthDate"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<hr style="margin: 10px 0 20px"/>
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class="form-legend" style="margin-bottom: 10px">
							<div class="contain-legend">
								<span class="content-legend" >
									<a href="javascript:void(0)">${StringUtil.wrapString(uiLabelMap.OldInformation)}</a>
								</span>
							</div>
							<div class="row-fluid" style="margin-top: 5px">
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.HealthInsuranceNbr}</label>
									</div>
									<div class="span8">
										<input type="text" id="insHealthUpdateHealthInsNbrOld">
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.InsurancePrimaryHCEstablishment}</label>
									</div>
									<div class="span8">
										<input type="text" id="insHealthUpdateHospitalNameOld">
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.EffectiveFromDate}</label>
									</div>
									<div class="span8">
										<div class="row-fluid">
											<div style="display: inline-block; margin-right: 5px" id="insHealthUpdateMonthFromOld" ></div>						
											<div style="display: inline-block;" id="insHealthUpdateYearFromOld" ></div> 	
										</div>
									</div>
								</div>
								<div class='row-fluid'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.DateExpire}</label>
									</div>
									<div class="span8">
										<div class="row-fluid">
											<div style="display: inline-block; margin-right: 5px" id="insHealthUpdateMonthThruOld" ></div>						
											<div style="display: inline-block;" id="insHealthUpdateYearThruOld" ></div> 	
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="form-legend" style="margin-bottom: 10px">
							<div class="contain-legend">
								<span class="content-legend" >
									<a href="javascript:void(0)">${StringUtil.wrapString(uiLabelMap.NewInformation)}</a>
								</span>
							</div>
							<div class="row-fluid" style="margin-top: 5px">
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.HealthInsuranceNbr}</label>
									</div>
									<div class="span8">
										<input type="text" id="insHealthUpdateHealthInsNbrNew">
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.InsurancePrimaryHCEstablishment}</label>
									</div>
									<div class="span8">
										<input type="text" id="insHealthUpdateHospitalNameNew">
										<button class="btn btn-mini btn-primary" id="insHealthUpdateChooseHospitalBtn" style="" title="">
											<i class="icon-list icon-only"></i></button>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.EffectiveFromDate}</label>
									</div>
									<div class="span8">
										<div class="row-fluid">
											<div style="display: inline-block; margin-right: 5px" id="insHealthUpdateMonthFromNew" ></div>						
											<div style="display: inline-block;" id="insHealthUpdateYearFromNew" ></div> 	
										</div>
									</div>
								</div>
								<div class='row-fluid'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.DateExpire}</label>
									</div>
									<div class="span8">
										<div class="row-fluid">
											<div style="display: inline-block; margin-right: 5px" id="insHealthUpdateMonthThruNew" ></div>						
											<div style="display: inline-block;" id="insHealthUpdateYearThruNew" ></div> 	
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelInsHealthUpdate" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveInsHealthUpdate">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/insurance/InsuranceHealthPartyUpdate.js"></script>