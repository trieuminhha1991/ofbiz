<div id="changingJobPositionWindow" class="hide">
	<div>${uiLabelMap.ChangingJobPosition}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div class="row-fluid">
				<div class="row-fluid form-horizontal form-window-content-custom label-text-left content-description">
					<div class="span12">
						<div class="span6">
							<div class='row-fluid'>
								<div class="span6 text-algin-right">
									<span style="float: right;">${uiLabelMap.EmployeeId}</span>
								</div>								
								<div class="span6">
									<div id="changeJobPositionEmployeeId" class="green-label" style="text-align: left;"></div>
								</div>								
							</div>	
							<div class='row-fluid'>
								<div class="span6 text-algin-right">
									<span style="float: right;">${uiLabelMap.PartyGender}</span>
								</div>								
								<div class="span6">
									<div id="changeJobPositionGender" class="green-label" style="text-align: left;"></div>
								</div>								
							</div>	
						</div>
						<div class="span6">
							<div class='row-fluid'>
								<div class="span6 text-algin-right">
									<span style="float: right;">${uiLabelMap.EmployeeName}</span>
								</div>								
								<div class="span6">
									<div id="changeJobPositionEmployeeName" class="green-label" style="text-align: left;"></div>
								</div>								
							</div>	
							<div class='row-fluid'>
								<div class="span6 text-algin-right">
									<span style="float: right;">${uiLabelMap.PartyBirthDate}</span>
								</div>								
								<div class="span6">
									<div id="changeJobPositionBirthDate" class="green-label" style="text-align: left;"></div>
								</div>								
							</div>	
						</div>
					</div>
				</div>
			</div>	
			<hr style="margin: 10px 0 20px">	
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class="form-legend">
							<div class="contain-legend">
								<span class="content-legend" >
									<a href="javascript:void(0)">${StringUtil.wrapString(uiLabelMap.OldJobPosition)}</a>
								</span>
							</div>
							<div class="row-fluid" style="margin-top: 10px">
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.PartyIdWork}</label>
									</div>
									<div class="span8">
										<div id="oldDeptChangingPosition"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.HrCommonPosition}</label>
									</div>
									<div class="span8">
										<div id="oldPosTypeChangePos"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.CommonFromDate}</label>
									</div>
									<div class="span8">
										<div id="oldFromDateChangingPosition"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.CommonThruDate}</label>
									</div>
									<div class="span8">
										<div id="oldThruDateChangingPosition"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="form-legend">
							<div class="contain-legend">
								<span class="content-legend" >
									<a href="javascript:void(0)">${StringUtil.wrapString(uiLabelMap.NewJobPosition)}</a>
								</span>
							</div>
							<div class="row-fluid" style="margin-top: 10px">
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.PartyIdWork}</label>
									</div>
									<div class="span8">
										<div id="newDeptChangePosDropDownBtn">
											<div id="newDeptChangePosTree"></div>
										</div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.HrCommonPosition}</label>
									</div>
									<div class="span8">
										<div id="newPosTypeChangingPosition"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.CommonFromDate}</label>
									</div>
									<div class="span8">
										<div id="newFromDateChangingPosition"></div>
									</div>
								</div>
								<div class='row-fluid margin-bottom10'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.CommonThruDate}</label>
									</div>
									<div class="span8">
										<div id="newThruDateChangingPosition"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelChangingPosition" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<#if security.hasEntityPermission("HR_DIRECTORY", "_UPDATE", session)>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveChangingPosition">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</#if>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/generalMgr/EditJobPosition.js"></script>