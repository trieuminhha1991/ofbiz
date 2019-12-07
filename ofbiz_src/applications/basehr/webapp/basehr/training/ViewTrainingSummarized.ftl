<#assign statusId = trainingCourse.statusId/>
<#assign hasUpdatePerm = security.hasEntityPermission("HR_TRAINING", "_UPDATE", session)/>
<#if hasUpdatePerm && statusId == "TRAINING_SUMMARY">
	<#assign isEditable = true/>
<#else>
	<#assign isEditable = false/>
</#if>
<script type="text/javascript">
<#if isEditable>
	globalVar.isEditable = true; 
<#else>
	globalVar.isEditable = false;
</#if>

</script>
<style>
.widget-toolbar-border-right {
	border: 1px solid #d9d9d9 !important; 
	border-width: 0 1px 0 0 !important
}
</style>
<div id="containerjqxgrid" class="container-noti"><#-- style="background-color: transparent; overflow: auto;"-->
</div>
<div id="jqxNotificationjqxgrid">
    <div id="notificationContentjqxgrid">
    </div>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.TrainigCourseDetailSummaryTitle}</h4>
		<div class="widget-toolbar no-border <#if isEditable>invoice-info widget-toolbar-border-right</#if>">
			<div class="row-fluid">
				<#if isEditable>
				<div class="span12">
				</#if>
				<a href="ViewTrainingDetail?trainingCourseId=${trainingCourseId}"><i class="fa-file-text-o"></i>${uiLabelMap.HRCommonPlanning}</a>
				<#if isEditable>
				</div>
				</#if>
			</div>
		</div>
		<#if isEditable>
			<div class="widget-toolbar">
				<a href="javascript:void(0)" id="completeTrainingCourse"><i class="fa-dot-circle-o"></i>${uiLabelMap.CompleteTrainingCourse}</a>					
			</div>
		</#if>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="row-fluid">
				<div class="row-fluid" style="margin-top: 15px;"><!-- training course info -->
					<div class="span12" style="position: relative;">
						<div class="form-legend" style="margin-bottom: 15px">
							<div class="contain-legend">
								<span class="content-legend" style="font-size: 15px">
									<a href="javascript:void(0)" title="<#if isEditable>${StringUtil.wrapString(uiLabelMap.ClickToEdit)}</#if>" id="editTrainingCourseInfoBtn">
										${StringUtil.wrapString(uiLabelMap.GeneralInformation)}&nbsp;&nbsp;<#if isEditable><i class="icon-edit"></i></#if></a>
								</span>
							</div>
							<div class="row-fluid">
								<div class="span12" style="margin-top: 10px">
									<div class="span5">
										<div class='row-fluid margin-bottom10'>
											<div class='span5 algin-left'>
												<label><b>${uiLabelMap.TrainingCourseIdShort}:</b></label>
											</div>  
											<div class="span7">
												<span style="font-size: 14px" id="trainingCodeView">${StringUtil.wrapString(trainingCourse.trainingCourseCode)}</span>
									   		</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class='span5 algin-left'>
												<label class=""><b>${uiLabelMap.HRRegisterStartDateShort}:</b></label>
											</div>  
											<div class="span7">
												<span style="font-size: 14px" id="registerFromDateView">${trainingCourse.registerFromDate?string["dd/MM/yyyy"]}</span>
									   		</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class='span5 algin-left'>
												<label class=""><b>${uiLabelMap.HRCommonFromDate}:</b></label>
											</div>  
											<div class="span7">
												<span style="font-size: 14px" id="actualFromDateView">${trainingCourse.actualFromDate?string["dd/MM/yyyy"]}</span>
												&nbsp;<span style="font-size: 12px">(${uiLabelMap.HREstimated}: ${trainingCourse.fromDate?string["dd/MM/yyyy"]})</span>
									   		</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class='span5 algin-left'>
												<label class=""><b>${uiLabelMap.CommonPurpose}:</b></label>
											</div>  
											<div class="span7">
												<#if trainingCourseDetail.purposeDesc?has_content>
													<#assign trainingPurpose = trainingCourseDetail.purposeDesc/> 
												<#else>
													<#assign trainingPurpose = uiLabelMap.HRCommonNotSetting/>
												</#if>
												<span style="font-size: 14px" id="trainingPurposeTypeView">${StringUtil.wrapString(trainingPurpose)}</span>
									   		</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class='span5 algin-left'>
												<label class=""><b>${uiLabelMap.CommonLocation}:</b></label>
											</div>  
											<div class="span7">	
												<#if trainingCourseDetail.location?has_content>
													<#assign trainingLocation = trainingCourseDetail.location/> 
												<#else>
													<#assign trainingLocation = uiLabelMap.HRCommonNotSetting/>
												</#if>
												<span style="font-size: 14px" id="locationView">${StringUtil.wrapString(trainingLocation)}</span>
									   		</div>
										</div>
									</div>
									<div class="span6">
										<div class='row-fluid margin-bottom10'>
											<div class='span5 algin-left'>
												<label><b>${uiLabelMap.TrainingCourseNameShort}:</b></label>
											</div>  
											<div class="span7">
												<span style="font-size: 14px" id="trainingCourseNameView">${StringUtil.wrapString(trainingCourse.trainingCourseName)}</span>
									   		</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class='span5 algin-left'>
												<label class=""><b>${uiLabelMap.HRRegisterEndDateShort}:</b></label>
											</div>  
											<div class="span7">
												<span style="font-size: 14px" id="registerThruDateView">${trainingCourse.registerThruDate?string["dd/MM/yyyy"]}</span>
									   		</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class='span5 algin-left'>
												<label class=""><b>${uiLabelMap.HRCommonThruDate}:</b></label>
											</div>  
											<div class="span7">
												<span style="font-size: 14px" id="actualThruDateView">${trainingCourse.actualThruDate?string["dd/MM/yyyy"]}</span>
												&nbsp;<span style="font-size: 12px">(${uiLabelMap.HREstimated}: ${trainingCourse.thruDate?string["dd/MM/yyyy"]})</span>
									   		</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class='span5 algin-left'>
												<label class=""><b>${uiLabelMap.CommonCertificate}:</b></label>
											</div>  
											<div class="span7">
												<#if trainingCourseDetail.certificate?has_content>
													<#assign certificate = trainingCourseDetail.certificate/> 
												<#else>
													<#assign certificate = uiLabelMap.HRCommonNotSetting/>
												</#if>
												<span style="font-size: 14px" id="certificateView">${StringUtil.wrapString(certificate)}</span>
									   		</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class='span5 algin-left'>
												<label class=""><b>${uiLabelMap.TrainingFormTypeId}:</b></label>
											</div>  
											<div class="span7">
												<#if trainingCourseDetail.formTypeDesc?has_content>
													<#assign formTypeDesc = trainingCourseDetail.formTypeDesc/> 
												<#else>
													<#assign formTypeDesc = uiLabelMap.HRCommonNotSetting/>
												</#if>
												<span style="font-size: 14px" id="trainingFormTypeView">${StringUtil.wrapString(formTypeDesc)}</span>
									   		</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span12">
									<div class='row-fluid'>
										<div class='span2 algin-left'>
											<label class=""><b>${uiLabelMap.CommonDescription}:</b></label>
										</div>  
										<div class="span7">
											<#if trainingCourseDetail.description?has_content>
												<#assign description = trainingCourseDetail.description/> 
											<#else>
												<#assign description = uiLabelMap.HRCommonNotSetting/>
											</#if>
											<div style="font-size: 14px" id="descriptionView">${StringUtil.wrapString(description)}</div>
								   		</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div><!-- ./end training course info -->
				<!-- ======================================  training course cost and provider ================================-->
				<div class="row-fluid" style="margin-top: 15px">
					<div class="span12">
						<div class="form-legend" style="margin-bottom: 15px">
							<div class="contain-legend">
								<span class="content-legend" style="font-size: 15px">
									<a href="javascript:void(0)" title="<#if isEditable>${StringUtil.wrapString(uiLabelMap.ClickToEdit)}</#if>" id="editTrainingCourseCostBtn">
										${StringUtil.wrapString(uiLabelMap.TrainingCostAndProvider)}&nbsp;&nbsp;<#if isEditable><i class="icon-edit"></i></#if></a>
								</span>
							</div>
							<div class="row-fluid">
								<div class="span12" style="margin-top: 10px">
									<div class="span6">
										<div class="row-fluid margin-bottom10">
											<div class="margin-left10">
												<span style="font-size: 14px"><i class="icon-plus green"></i><b>${uiLabelMap.CostActualEmployee}</b></span>
											</div>
										</div>
										<div class="row-fluid">
											<div class="span12">
												<div class='row-fluid margin-left30'>
													<div class='span5 text-algin-left'>
														<i class="icon-caret-right blue"></i><b>${uiLabelMap.AmountEstimatedEmplPaid}:</b>
													</div>  
													<div class="span7">
														<div>
															<span style="font-size: 14px" id="actualEmplPaidView"><@ofbizCurrency amount=trainingCourseDetail.actualEmplPaid?if_exists isoCode="VND"/></span>
															<span style="font-size: 12px">(${uiLabelMap.HREstimated}: <@ofbizCurrency amount=trainingCourseDetail.estimatedEmplPaid?if_exists isoCode="VND"/>)</span>
														</div>
											   		</div>
												</div>
												<div class='row-fluid margin-bottom10 margin-left30'>
													<div class='span5 text-algin-left'>
														<i class="icon-caret-right blue"></i><b>${uiLabelMap.AmountCompanySupport}:</b>
													</div>  
													<div class="span7">
														<div id="amountCompanySupportView">
															<span style="font-size: 14px" id="actualAmountCompanySupView"><@ofbizCurrency amount=trainingCourseDetail.actualAmountCompanySup?if_exists isoCode="VND"/></span>
															<span style="font-size: 12px">(${uiLabelMap.HREstimated}: <@ofbizCurrency amount=trainingCourseDetail.amountCompanySupport?if_exists isoCode="VND"/>)</span>
														</div>
											   		</div>
												</div>
											</div>
											
										</div>									
									</div>
									<div class="span6">
										<div class="row-fluid margin-bottom10">
											<div class="margin-left15">
												<span style="font-size: 14px"><i class="icon-plus green"></i><b>${uiLabelMap.HRActualCost}</b></span>
											</div>
										</div>
										<div class="row-fluid">
											<div class='row-fluid'>
												<div class="margin-left30">
													<div class='span5 text-algin-left'>
														<i class="icon-caret-right blue"></i><b>${uiLabelMap.CommonActualNumber}:</b>
													</div>  
													<div class="span7">
													<#if trainingCourseDetail.totalPartyAtt?has_content>
														<#assign totalPartyAtt = trainingCourseDetail.totalPartyAtt/> 
													<#else>
														<#assign totalPartyAtt = 0/>
													</#if>
														<div>
															<span style="font-size: 14px" id="actualNumberView">${totalPartyAtt}</span>
															<span style="font-size: 12px">(${uiLabelMap.HREstimated}: ${trainingCourseDetail.estimatedNumber})</span>
														</div>
											   		</div>
												</div>
											</div>
											<div class='row-fluid margin-bottom10'>
												<div class="margin-left30">
													<div class='span5 text-algin-left'>
														<i class="icon-caret-right blue"></i><b>${uiLabelMap.TotalAmountActual}:</b>
													</div>  
													<div class="span7">
														<div>
															<span style="font-size: 14px" id="totalActualCostView"><@ofbizCurrency amount=trainingCourseDetail.totalActualCost?if_exists isoCode="VND"/></span>
															<span style="font-size: 12px">(${uiLabelMap.HREstimated}: <@ofbizCurrency amount=trainingCourseDetail.totalEstimatedCost?if_exists isoCode="VND"/>)</span>
														</div>
											   		</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row-fluid">
								<div class="span12">
									<div class="span6">
										<div class="row-fluid margin-bottom10">
											<div class='row-fluid'>
												<div class="margin-left15">
													<div class='span5 text-algin-left'>
														<span style="font-size: 14px"><i class="icon-plus green"></i><b>${uiLabelMap.TrainingProvider}:</b></span>
													</div>  
													<div class="span7">
														<#if trainingCourseDetail.providerId?has_content>
															<#assign providerName = StringUtil.wrapString(trainingCourseDetail.providerName)/>
														<#else>
															<#assign providerName = StringUtil.wrapString(uiLabelMap.HRCommonNotSetting)/>
														</#if>
														<div style="font-size: 14px" id="providerNameView">${providerName}</div>
											   		</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class="row-fluid">
								<#if trainingCourseDetail.isPublic == "Y">
									<#assign trainingModeDesc = StringUtil.wrapString(uiLabelMap.AllowAllEmployeeRegister)/>
								<#else>
									<#assign trainingModeDesc = StringUtil.wrapString(uiLabelMap.OnlyEmplInRegisterList)/>
								</#if>
								<#if trainingCourseDetail.isCancelRegister == "Y">
									<#assign trainingRegisterDesc = StringUtil.wrapString(uiLabelMap.AllowCancelRegisterBefore) + " " + trainingCourseDetail.cancelBeforeDay + " " + uiLabelMap.CommonDay/>
								<#else>
									<#assign trainingRegisterDesc = StringUtil.wrapString(uiLabelMap.NotAllowCancelRegister)/>
								</#if>
								<div class="row-fluid margin-bottom10">
									<div class="margin-left15">
										<span style="font-size: 14px"><i class="icon-plus green"></i><b>${uiLabelMap.HRCommonRequest}</b></span>
									</div>
								</div>
								<div class="row-fluid">
									<div class="margin-left30">
										<div class="span12">
											<div class='row-fluid margin-bottom10'>
												<i class="icon-ok blue"></i><b><span id="trainingModeView">${trainingModeDesc}</span></b>
											</div>
											<div class='row-fluid'>
												<i class="icon-ok blue"></i><b><span id="trainingRegisterView">${trainingRegisterDesc}</span></b>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>		
				</div><!-- ./end training course cost and provider -->
				<div class="hr hr-dotted"></div>
				<!-- ======================================  training course party expected attendance ================================-->
				<#include "PartyAttendanceTraining.ftl"/>
				<!-- ./end -->
			</div>	
		</div>
	</div>	
</div>
<#if isEditable>
	<div id="editTrainingInfoWindow" class="hide">
		<div>${uiLabelMap.CommonEdit}</div>
		<div class='form-window-container' style="position: relative;">
			<div class="form-window-content">
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="">${uiLabelMap.HRCommonFromDate}</label>
					</div>  
					<div class="span8">
						<div id="actualFromDate"></div>
			   		</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="">${uiLabelMap.HRCommonThruDate}</label>
					</div>  
					<div class="span8">
						<div id="actualThruDate"></div>
			   		</div>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="loadingTrainingCourseInfo" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerTrainingCourseInfo"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditTrainingCourseInfo">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
				<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditTrainingCourseInfo">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
		</div>	
	</div>
	<div id="editTrainingCostWindow" class="hide">
		<div>${uiLabelMap.CommonEdit}</div>
		<div class='form-window-container' style="position: relative;">
			<div class="form-window-content">
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="">${uiLabelMap.AmountEstimatedEmplPaid}</label>
					</div>  
					<div class="span8">
						<div id="actualEmplPaid"></div>
			   		</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="">${uiLabelMap.AmountCompanySupportActual}</label>
					</div>  
					<div class="span8">
						<div id="actualAmountCompanySup"></div>
			   		</div>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="loadingTrainingCost" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerTrainingCost"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditTrainingCost">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
				<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditTrainingCost">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
		</div>	
	</div>
	<script type="text/javascript" src="/hrresources/js/training/ViewTrainingSummarized.js"></script>
	<#assign addWindowId = "AddNewPartyAttWindow"/>
	<#include "AddPartyToTrainingCourse.ftl"/>
</#if>