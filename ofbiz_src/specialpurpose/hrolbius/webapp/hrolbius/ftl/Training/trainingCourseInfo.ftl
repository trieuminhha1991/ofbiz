<div class="row-fluid">
	<div class="span12">
		<div class="basic-form form-horizontal">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.TrainingCourseId}</label>
							<div class="controls">
								${trainingCourse.trainingCourseId}
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.TrainingCourseName}</label>
							<div class="controls">
								${trainingCourse.trainingCourseName}
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.CommonDescription}</label>
							<div class="controls">
								${trainingCourse.description?if_exists}&nbsp;
							</div>
						</div>
						
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.TimeEstimated}</label>
							<div class="controls">
								<#if trainingCourse.estimatedFromDate?exists>
									${trainingCourse.estimatedFromDate?string["dd/MM/yyyy"]} <#if trainingCourse.estimatedThruDate?exists>- ${trainingCourse.estimatedThruDate?string["dd/MM/yyyy"]}</#if>
								<#else>
									${uiLabelMap.HRCommonNotSetting}
								</#if> 
							</div>
						</div>
						
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.CommonEstimatedNumber}</label>
							<div class="controls">
								${trainingCourse.estimatedNumber?if_exists}
							</div>
						</div>
					</div>
					<div class="span6">
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.TrainingTypeId}</label>
							<div class="controls">
								<#if trainingCourse.trainingTypeId?exists>
									<#assign trainingType = delegator.findOne("TrainingType", Static["org.ofbiz.base.util.UtilMisc"].toMap("trainingTypeId", trainingCourse.trainingTypeId), false)>
									${trainingType.description?if_exists}
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.TrainingFormTypeId}</label>
							<div class="controls">
								<#if trainingCourse.trainingFormTypeId?exists>
									<#assign trainingFormType = delegator.findOne("TrainingFormType", Static["org.ofbiz.base.util.UtilMisc"].toMap("trainingFormTypeId", trainingCourse.trainingFormTypeId), false)>
									${trainingFormType.description?if_exists}
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.TrainingLocation}</label>
							<div class="controls">
								<#if trainingCourse.trainingLocation?exists>
									${trainingCourse.trainingLocation?if_exists}
								<#else>
									${uiLabelMap.HRCommonNotSetting}		
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.TimeActual}</label>
							<div class="controls">
								<#if trainingCourse.actualFromDate?exists>
									${trainingCourse.actualFromDate?string["dd/MM/yyyy"]} <#if trainingCourse.actualThruDate?exists>- ${trainingCourse.actualThruDate?string["dd/MM/yyyy"]}</#if> 
								<#else>
									${uiLabelMap.HRCommonNotSetting}	 
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.CommonActualNumber}</label>
							<div class="controls">
								<#if trainingCourse.actualNumber?exists>
									${trainingCourse.actualNumber}
								<#else>
									${uiLabelMap.HRCommonNotSetting}
								</#if>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12 mgt20">
					<div class="span5 boder-all-profile" style="margin-left: 60px">
						<span class="text-header">${uiLabelMap.CostEstimatedEmployee}</span>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.AmountEstimatedEmplPaid}</label>
							<div class="controls">
								<#if trainingCourse.estimatedEmplPaid?exists>
									<@ofbizCurrency amount=trainingCourse.estimatedEmplPaid isoCode=trainingCourse.uomId/>
								<#else>
									${uiLabelMap.HRCommonNotSetting}
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.AmountCompanySupport}</label>
							<div class="controls">
								<#if trainingCourse.amountCompanySupport?exists>
									<@ofbizCurrency amount=trainingCourse.amountCompanySupport isoCode=trainingCourse.uomId/>
								<#else>
									${uiLabelMap.HRCommonNotSetting}
								</#if>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.AmountTotal}</label>
							<div class="controls">
								<#if trainingCourse.estimatedEmplPaid?exists>
									<#assign estimatedEmpl = trainingCourse.estimatedEmplPaid>
								<#else>	 
									<#assign estimatedEmpl = 0>
								</#if> 
								
								<#if trainingCourse.amountCompanySupport?exists>
									<#assign amountCompanySupport = trainingCourse.amountCompanySupport>
								<#else>	 
									<#assign amountCompanySupport = 0>
								</#if>
								<#assign totalAmount = estimatedEmpl + amountCompanySupport>
								<@ofbizCurrency amount=totalAmount isoCode=trainingCourse.uomId/> 
							</div>
						</div>
					</div>
					<div class="span5 boder-all-profile" style="">
						<span class="text-header">${uiLabelMap.TotalAmountEstimated}</span>
						<div class="control-group no-left-margin">
							<#if trainingCourse.estimatedNumber?exists>
								<#assign estimatedNumber = trainingCourse.estimatedNumber>
							<#else>
								<#assign estimatedNumber = 0>	
							</#if>
							<label class="control-label">${uiLabelMap.AmountEstimatedEmplPaid}</label>
							<div class="controls">
								<@ofbizCurrency amount=(estimatedEmpl * estimatedNumber) isoCode=trainingCourse.uomId/>			
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.AmountCompanySupport}</label>
							<div class="controls">
								<@ofbizCurrency amount=(amountCompanySupport * estimatedNumber) isoCode = trainingCourse.uomId/>
							</div>
						</div>
						<div class="control-group no-left-margin">
							<label class="control-label">${uiLabelMap.AmountTotal}</label>
							<div class="controls">
								<@ofbizCurrency amount=(estimatedEmpl * estimatedNumber + amountCompanySupport * estimatedNumber) isoCode = trainingCourse.uomId/>
							</div>
						</div>
					</div>
				</div>
			</div>
			<#if trainingCourse.statusId == "TRAINING_PLANNED">
				<div class="row-fluid">
					<div class="span12">
						<div class="control-group no-left-margin">
							<form action="<@ofbizUrl>createTrainingCourseProposal</@ofbizUrl>" method="post">
								<input type="hidden" name="trainingCourseId" value="${trainingCourse.trainingCourseId}">
								<div class="control-group no-left-margin">
									<label class="control-label">&nbsp;</label>
									<div class="controls">
										<button type="submit" class="btn btn-primary btn-mini icon-ok">${uiLabelMap.SendTrainingCoursePlan}</button>
									</div>
								</div>
							</form>
						</div>
					</div>
				</div>
			</#if>	
		</div>
	</div>
</div>