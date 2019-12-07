<div id="addRecruitmentRequireWindow" class="hide">
	<div>${uiLabelMap.AddNewRecruitmentRequire}</div>
	<div class='form-window-content' style="position: relative;">
		<div class='form-window-content' >
			<div class="row-fluid">
				<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
			        <ul class="wizard-steps wizard-steps-square">
			                <li data-target="#generalInfo" class="active">
			                    <span class="step">1. ${uiLabelMap.RecruitmentRequireInfo}</span>
			                </li>
			                <li data-target="#recruitmentRequireConds">
			                    <span class="step">2. ${uiLabelMap.ConditionPreliminarySelection}</span>
			                </li>
			    	</ul>
			    </div><!--#fuelux-wizard-->
			    <div class="step-content row-fluid position-relative" id="step-container">
			    	<div class="step-pane active" id="generalInfo">
			    		<div class="span12" style="margin-top: 20px">
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
									<label class="asterisk">${uiLabelMap.TimeNeedEmployee}</label>
								</div>
								<div class="span8">
									<div class="row-fluid">
										<div style="display: inline-block; margin-right: 5px" id="monthNew" ></div>						
										<div style="display: inline-block;" id="yearNew" ></div> 	
									</div>
								</div>
							</div>
			    			<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="asterisk">${uiLabelMap.HrCommonPosition}</label>
								</div>
								<div class="span8">
									<div id="emplPositionTypeAddNew"></div>
								</div>
							</div>
			    			<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.RecruitmentQuantityApproved}</label>
								</div>
								<div class="span8" id="quantityApprTooltip">
									<div id="quantityApproved"></div>
								</div>
							</div>
			    			<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.PlannedRecruitment}</label>
								</div>
								<div class="span8">
									<div class="row-fluid">
										<div class="span12">
											<div class="span4" >
												<div id="quantityNew"></div>
											</div>
											<div class="span8">
												<div class='row-fluid'>
													<div class='span6 align-right'>
														 <div id='unplannedRecruitmentCheck' style="float: right; margin-top: 5px">
														 	<span style="font-size: 14px">${uiLabelMap.UnplannedRecruitment}</span>
														 </div>
													</div>
													<div class="span6">
														<div id="quantityUnplannedNew"></div>
													</div>
													
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.RecruitmentFormType}</label>
								</div>
								<div class="span8">
									<div id="recruitmentFormTypeNew"></div>
								</div>
							</div>
							<#--<!-- <div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="asterisk">${uiLabelMap.HRCommonApprover}</label>
								</div>
								<div class="span8">
									<div id="approverListDropDownBtn">
										 <div style="border-color: transparent;" id="jqxGridApprover">
	           							 </div>
									</div>
								</div>
							</div> -->
							<div class='row-fluid margin-bottom10'>
								<div class='span4 align-right'>
									<label class="">${uiLabelMap.RecruitmentReason}</label>
								</div>
								<div class="span8">
									<textarea id="commentNew"></textarea>
								</div>
							</div>
			    		</div>
			    	</div>
			    	<div class="step-pane" id="recruitmentRequireConds">
			    		<div class="span12">
			    			<div id="recruitReqCondGrid"></div>
			    		</div>
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

<script type="text/javascript" src="/hrresources/js/recruitment/AddRecruitmentRequire.js"></script>