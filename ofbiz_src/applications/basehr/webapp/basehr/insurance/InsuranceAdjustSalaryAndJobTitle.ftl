<div id="adjustSalJobWindow" class="hide">
	<div>${uiLabelMap.AdjustSalaryAndJobTitle}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="asterisk">${uiLabelMap.EmployeeId}</label>
							</div>
							<div class="span8">
								<input type="text" id="emplAdjustSalCode">
								<button class="btn btn-mini btn-primary" id="chooseEmplAdjustSal" style="" 
									title="${StringUtil.wrapString(uiLabelMap.ClickToChooseEmpl)}">
									<i class="icon-plus icon-only"></i>
								</button>
							</div>
						</div>
						<div class='row-fluid'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.CommonDepartment}</label>
							</div>
							<div class="span8">
								<input type="text" id="emplAdjustSalDept">
							</div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.EmployeeName}</label>
							</div>
							<div class="span8">
								<input type="text" id="emplAdjustSalName">
							</div>
						</div>
						<div class='row-fluid'>
							<div class="span4 text-algin-right">
								<label class="">${uiLabelMap.SocialInsuranceNbr}</label>
							</div>
							<div class="span8">
								<input type="text" id="emplAdjustSalSocialInsNbr">
							</div>
						</div>
					</div>
				</div>
			</div>
			<hr style="margin: 10px 0 20px"/>
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class="form-legend">
							<div class="contain-legend">
								<span class="content-legend" >
									<a href="javascript:void(0)">${StringUtil.wrapString(uiLabelMap.OldInformation)}</a>
								</span>
							</div>
							<div class="row-fluid" style="margin-top: 5px">
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.HRCommonJobTitle}</label>
									</div>
									<div class="span8">
										<div id="oldEmplPosTypeInfoList"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.InsuranceSalaryShort}</label>
									</div>
									<div class="span8">
										<div id="oldInsuranceSalary"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.FromMonthYear}</label>
									</div>
									<div class="span8">
										<div class="row-fluid">
											<div style="display: inline-block; margin-right: 5px" id="monthAdjSalOldInfoFrom" ></div>						
											<div style="display: inline-block;" id="yearAdjSalOldInfoFrom" ></div> 	
										</div>
									</div>
								</div>
								<div class='row-fluid'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.ToMonthYear}</label>
									</div>
									<div class="span8">
										<div class="row-fluid">
											<div style="display: inline-block; margin-right: 5px" id="monthAdjSalOldInfoTo" ></div>						
											<div style="display: inline-block;" id="yearAdjSalOldInfoTo" ></div> 	
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="form-legend">
							<div class="contain-legend">
								<span class="content-legend" >
									<a href="javascript:void(0)">${StringUtil.wrapString(uiLabelMap.NewInformation)}</a>
								</span>
							</div>
							<div class="row-fluid" style="margin-top: 5px">
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.HRCommonJobTitle}</label>
									</div>
									<div class="span8">
										<div id="newEmplPosTypeInfoList"></div>
									</div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class="span4 text-algin-right">
									<label class="">${uiLabelMap.InsuranceSalaryShort}</label>
								</div>
								<div class="span8">
									<div id="newInsuranceSalary"></div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class="span4 text-algin-right">
									<label class="">${uiLabelMap.FromMonthYear}</label>
								</div>
								<div class="span8">
									<div class="row-fluid">
										<div style="display: inline-block; margin-right: 5px" id="monthAdjSalNewInfoFrom" ></div>						
										<div style="display: inline-block;" id="yearAdjSalNewInfoFrom" ></div> 	
									</div>
								</div>
							</div>
							<div class='row-fluid'>
								<div class="span4 text-algin-right">
									<label class="">${uiLabelMap.ToMonthYear}</label>
								</div>
								<div class="span8">
									<div class="row-fluid">
										<div style="display: inline-block; margin-right: 5px" id="monthAdjSalNewInfoTo" ></div>						
										<div style="display: inline-block;" id="yearAdjSalNewInfoTo" ></div> 	
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingAdjustSalJob" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAdjustSalJob"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelAdjSalJob" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveContinueAdjSalJob">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAdjSalJob">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="listEmplParticipateInsWindow" class="hide">
	<div>${uiLabelMap.ChooseEmplToAdjustSalaryAndJob}</div>
	<div class="form-window-container">
		<div id="listEmplCurrentParticipating"></div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/insurance/InsuranceAdjustSalaryAndJobTitle.js"></script>