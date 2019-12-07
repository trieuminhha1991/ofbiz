<div id="editRecruitmentPlanWindow" class="hide">
	<div>${uiLabelMap.CreateNewRecruitmentPlan}</div>
	<div class='form-window-content' style="position: relative;">
		<div class='form-window-content' >
			<div class="row-fluid">
				<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
			        <ul class="wizard-steps wizard-steps-square">
			                <li data-target="#recruitmentInfo" class="active">
			                    <span class="step">1. ${uiLabelMap.RecruitmentInfoShort}</span>
			                </li>
			                <li data-target="#recruitmentCondSelect">
			                    <span class="step">2. ${uiLabelMap.ConditionPreliminarySelection}</span>
			                </li>
			                <li data-target="#recruitmentBoard">
			                    <span class="step">3. ${uiLabelMap.RecruitmentBoardShort}</span>
			                </li>
			                <li data-target="#recruitmentRound">
			                    <span class="step">4. ${uiLabelMap.RecruitmentRound}</span>
			                </li>
			                <li data-target="#recruitmentCost">
			                    <span class="step">5. ${uiLabelMap.RecruitmentCost}</span>
			                </li>
			    	</ul>
			    </div><!--#fuelux-wizard-->
			    <div class="step-content row-fluid position-relative" id="step-container">
			    	<div class="step-pane active" id="recruitmentInfo">
			    		<div class="row-fluid">
				    		<div class="span12" style="margin-top: 20px">
				    			<div class="span6">
				    				<div class='row-fluid margin-bottom10'>
					    				<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}</label>
										</div>
										<div class="span7">
											<input type="text" id="recruitmentPlanNameNew">
										</div>
					    			</div>
					    			<div class='row-fluid margin-bottom10'>
					    				<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}</label>
										</div>
										<div class="span7">
											<div id="dropDownButtonAddNew" class="">
												<div style="border: none;" id="jqxTreeAddNew">
												</div>
											</div>
										</div>
					    			</div>
					    			<div class='row-fluid margin-bottom10'>
										<div class='span5 align-right'>
											<label class="asterisk">${uiLabelMap.TimeRecruitmentPlan}</label>
										</div>
										<div class="span7">
											<div class="row-fluid">
												<div class="span12">
													<div style="display: inline-block; margin-right: 5px" id="monthNew" ></div>						
													<div style="display: inline-block;" id="yearNew" ></div> 	
												</div>
											</div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 align-right'>
											<label class="">${uiLabelMap.RecruitmentTime}</label>
										</div>
										<div class="span7">
											<div id="recruitmentDateTime"></div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 align-right'>
											<label class="">${uiLabelMap.HREstimatedCost}</label>
										</div>
										<div class="span7">
											<div id="estimatedCostNew"></div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 align-right'>
											<label class="">${uiLabelMap.HrolbiusAmountSalary}</label>
										</div>
										<div class="span7">
											<div id="salaryAmountNew"></div>
										</div>
									</div>
									<div class='row-fluid'>
										<div class='span5 align-right'>
											<label class="">${uiLabelMap.HRNotes}</label>
										</div>
										<div class="span7">
											<textarea id="requirementDesc"></textarea>
										</div>
									</div>
				    			</div>
				    			<div class="span6">
					    			<div class='row-fluid margin-bottom10'>
					    				<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.RecruitmentPosition)}</label>
										</div>
										<div class="span7">
											<div id="emplPositionTypeIdNew"></div>
										</div>
					    			</div>
					    			<div class='row-fluid margin-bottom10'>
										<div class='span5 align-right'>
											<label class="asterisk">${uiLabelMap.CommonQuantity}</label>
										</div>
										<div class="span7">
											<div id="quantityNew"></div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 align-right'>
											<label class="">${uiLabelMap.RecruitmentFormType}</label>
										</div>
										<div class="span7">
											<div id="recruitmentFormTypeNew"></div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 align-right'>
											<label class="">${uiLabelMap.DateLineForApply}</label>
										</div>
										<div class="span7">
											<div id="recruitmentApplyDateTime"></div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 align-right'>
											<label class="">${uiLabelMap.HRActualCost}</label>
										</div>
										<div class="span7">
											<div id="actualCost"></div>
										</div>
									</div>
				    			</div>
				    		</div>
			    		</div>
			    	</div>
			    	<div class="step-pane" id="recruitmentCondSelect">
			    		<div class="span12">
			    			<div id="recruitReqCondGrid"></div>
			    		</div>
			    	</div>
			    	<div class="step-pane" id="recruitmentBoard">
			    		<div id="recruitmentBoardGrid"></div>
			    	</div>
			    	<div class="step-pane" id="recruitmentRound">
			    		<div id="recruitmentRoundGrid"></div>
			    	</div>
			    	<div class="step-pane" id="recruitmentCost">
			    		<div id="recruitmentCostGrid"></div>
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
					<div id="ajaxLoading" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerAjax"></div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<#include "RecruitmentConditionHtml.ftl"/>

<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmentCondition.js"></script>
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitCreateRecruitPlanNotBaseRequirement.js"></script>