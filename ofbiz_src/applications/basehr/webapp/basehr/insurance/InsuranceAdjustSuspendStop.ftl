<div id="adjustSuspendStopWindow" class="hide">
	<div>${uiLabelMap.EditEmplSuspendParticipate}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid" style="margin-top: 15px">
				<div class="form-legend" style="margin-bottom: 10px">
					<div class="contain-legend">
						<span class="content-legend" >
							<a href="javascript:void(0)">${StringUtil.wrapString(uiLabelMap.InsuranceSuspendStopParticipate)}</a>
						</span>
					</div>
					<div class="row-fluid" style="margin-top: 5px" id="insuranceSuspendInfo">
						<div class='row-fluid margin-bottom10'>
							<div class="span2 text-algin-right">
								<label class="asterisk">${uiLabelMap.InsuranceSuspendStopFrom}</label>
							</div>
							<div class="span10">
								<div class="row-fluid">
									<div class="span4">
										<div style="display: inline-block; margin-right: 5px" id="monthStartSuspendStop" ></div>						
										<div style="display: inline-block;" id="yearStartSuspendStop"></div>
									</div>
									<div class="span8">
										<div class='row-fluid'>
											<div class="span2 text-algin-right">
												<label class="">${uiLabelMap.HRCommonToUppercase}</label>
											</div>
											<div class="span10">
												<div style="display: inline-block; margin-right: 5px" id="monthEndSuspendStop" ></div>						
												<div style="display: inline-block;" id="yearEndSuspendStop"></div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class='row-fluid'>
							<div class="span2 text-algin-right">
								<label class="asterisk">${uiLabelMap.HRCommonReason}</label>
							</div>
							<div class="span10">
								<div id="suspendReasonList"></div>
							</div>
						</div>
						<div class="row-fluid margin-top10 hide" id="supplementInfo">
							<div class="row-fluid">
								<div class="span2 text-algin-right">
									<label class=""></label>
								</div>
								<div class="span10">
									<div class="row-fluid">
										<div class="span4">
											<div style='margin-top: 10px; display: inline-block;' id='isReturnSHICard'>
												<span style="font-size: 14px">${uiLabelMap.InsuranceReturnSHICard}</span>
											</div>
										</div>
										<div class="span8">
											<div class='row-fluid'>
												<div class="span2 text-algin-right">
													<label class=""></label>
												</div>
												<div class="span10">
													<div style='margin-top: 10px; display: inline-block;' id='isSupplement'>
														<span style="font-size: 14px">${uiLabelMap.InsuranceSupplementSHI}</span>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row-fluid margin-top10">
								<div class="span2 text-algin-right">
									<label class="">${uiLabelMap.InsuranceSupplementFrom}</label>
								</div>
								<div class="span10">
									<div class="row-fluid">
										<div class="span4">
											<div style="display: inline-block; margin-right: 5px" id="monthStartSupplement" ></div>						
											<div style="display: inline-block;" id="yearStartSupplement"></div>
										</div>
										<div class="span8">
											<div class='row-fluid'>
												<div class="span2 text-algin-right">
													<label class="">${uiLabelMap.HRCommonToUppercase}</label>
												</div>
												<div class="span10">
													<div style="display: inline-block; margin-right: 5px" id="monthEndSupplement" ></div>						
													<div style="display: inline-block;" id="yearEndSupplement"></div>
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
			<div class="row-fluid">
				<div id="listEmplParticipateGrid"></div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingSuspendStopParticipate" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerSuspendStopParticipate"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelAdjSuspendStop" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveContinueAdjSuspendStop">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>		
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAdjSuspendStop">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/insurance/InsuranceAdjustSuspendStop.js"></script>