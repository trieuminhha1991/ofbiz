<div id="editRecruitPlanRequireWindow" class="hide">
	<div>${uiLabelMap.AddNewRecruitmentBaseOnRecruitment}</div>
	<div class='form-window-content' style="position: relative;">
		<div class='form-window-content' >
			<div class="row-fluid">
				<div id="wizardRequirement" class="row-fluid hide" data-target="#stepContainerRequirement">
			        <ul class="wizard-steps wizard-steps-square">
			                <li data-target="#recruitmentRequireInfo" class="active">
			                    <span class="step">1. ${uiLabelMap.RecruitmentRequirement}</span>
			                </li>
			                <li data-target="#recruitmentBoardReq">
			                    <span class="step">2. ${uiLabelMap.RecruitmentBoardShort}</span>
			                </li>
			                <li data-target="#recruitmentRoundReq">
			                    <span class="step">3. ${uiLabelMap.RecruitmentRound}</span>
			                </li>
			                <li data-target="#recruitmentCostReq">
			                    <span class="step">4. ${uiLabelMap.RecruitmentCost}</span>
			                </li>
			    	</ul>
			    </div><!--#fuelux-wizard-->
			    <div class="step-content row-fluid position-relative" id="stepContainerRequirement">
			    	<div class="step-pane active" id="recruitmentRequireInfo">
			    		<div class="row-fluid">
			    			<div id="recruitmentReqGrid"></div>
			    		</div>
			    		<div class="row-fluid" style="margin-top: 15px">
			    			<div class="span12">
			    				<div class="span6">
			    					<div class='row-fluid margin-bottom10'>
										<div class='span4 align-right'>
											<label class="">${uiLabelMap.RecruitmentTime}</label>
										</div>
										<div class="span8">
											<div id="recruitmentDateTimeReq"></div>
										</div>
									</div>
			    				</div>
			    				<div class="span6">
			    					<div class='row-fluid margin-bottom10'>
										<div class='span4 align-right'>
											<label class="">${uiLabelMap.DateLineForApply}</label>
										</div>
										<div class="span8">
											<div id="recruitmentApplyDateTimeReq"></div>
										</div>
									</div>
			    				</div>
			    			</div>
			    		</div>
			    	</div>
			    	<div class="step-pane" id="recruitmentBoardReq">
			    		<div id="recruitmentBoardReqGrid"></div>
			    	</div>
			    	<div class="step-pane" id="recruitmentRoundReq">
			    		<div id="recruitmentRoundReqGrid"></div>
			    	</div>
			    	<div class="step-pane" id="recruitmentCostReq">
			    		<div id="recruitmentCostReqGrid"></div>
			    	</div>
			    </div>
			    <div class="form-action wizard-actions">
					<button class="btn btn-next btn-success form-action-button pull-right" data-last="${uiLabelMap.CommonSave}" id="btnNextReq">
						${uiLabelMap.CommonNext}
						<i class="icon-arrow-right icon-on-right"></i>
					</button>
					<button class="btn btn-prev form-action-button pull-right" id="btnPrevReq">
						<i class="icon-arrow-left"></i>
						${uiLabelMap.CommonPrevious}
					</button>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="loadingReq" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerReq"></div>
					</div>
				</div>
			</div>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitCreateRecruitPlanBaseRequirement.js"></script>