<#include "script/CreateTrainingCourseSummaryScript.ftl"/>
<div id="containerjqxgrid" class="container-noti"><#-- style="background-color: transparent; overflow: auto;"-->
</div>
<div id="jqxNotificationjqxgrid">
    <div id="notificationContentjqxgrid">
    </div>
</div>
<#assign addWindowId = "AddNewPartyAttWindow"/>
<#include "AddPartyToTrainingCourse.ftl"/>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.TrainigCourseDetailSummaryTitle}</h4>
		<div class="widget-toolbar no-border">
			<div class="row-fluid">
				<span>
					<a href="javascript:void(0)" id="saveSummaryTraining" style="font-size: 15px"><i class="fa fa-floppy-o"></i>${uiLabelMap.CommonSave}</a>
				</span>
			</div>
		</div>
	</div>
	<div class="widget-body hide" id="mainContainer">
		<div class="row-fluid" >
			<form class="form-horizontal">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid'>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 text-algin-right'>
									<label>${uiLabelMap.TrainingCourseIdShort}</label>
								</div>  
								<div class="span8">
									<input type="text" id="trainingCodeView" value="${StringUtil.wrapString(trainingCourse.trainingCourseCode)}">
						   		</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 text-algin-right'>
									<label>${uiLabelMap.CommonActualStartDate}</label>
								</div>  
								<div class="span8">
									<div class="row-fluid">
										<div class="span12">
											<div class="span5">
												<div id="actualStartDate"></div>
											</div>
											<div class="span7">
												<div class='row-fluid'>
													<div class="span4 text-algin-right">
														<label>${uiLabelMap.HREstimated}</label>
													</div>
													<div class="span8">
														<div id="estimatedStartDate"></div>
													</div>
												</div>
											</div>
										</div>
									</div>
						   		</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 text-algin-right'>
									<label>${uiLabelMap.AmountPerEmployeePaid}</label>
								</div>
								<div class="span8">
									<div class="row-fluid">
										<div class="span12">
											<div class="span5">
												<div id="actualEmplPaid"></div>
											</div>
											<div class="span7">
												<div class='row-fluid'>
													<div class="span4 text-algin-right">
														<label>${uiLabelMap.HREstimated}</label>
													</div>
													<div class="span8">
														<div id="estimatedEmplPaid"></div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
							<div class='row-fluid margin-bottom10'>
								<div class='span4 text-algin-right'>
									<label>${uiLabelMap.CommonActualNumber}</label>
								</div>
								<div class="span8">
									<div class="row-fluid">
										<div class="span12">
											<div class="span5">
												<div id="actualNumber"></div>
											</div>
											<div class="span7">
												<div class='row-fluid'>
													<div class="span4 text-algin-right">
														<label>${uiLabelMap.HREstimated}</label>
													</div>
													<div class="span8">
														<div id="estimatedNumber"></div>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>	
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label>${uiLabelMap.TrainingCourseNameShort}</label>
							</div>  
							<div class="span8">
								<input type="text" id="trainingCourseNameView" value="${StringUtil.wrapString(trainingCourse.trainingCourseName)}">
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label>${uiLabelMap.HRCommonActualEndDate}</label>
							</div>  
							<div class="span8">
								<div class="row-fluid">
									<div class="span12">
										<div class="span5">
											<div id="actualEndDate"></div>
										</div>
										<div class="span7">
											<div class='row-fluid'>
												<div class="span4 text-algin-right">
													<label>${uiLabelMap.HREstimated}</label>
												</div>
												<div class="span8">
													<div id="estimatedEndDate"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
					   		</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label>${uiLabelMap.AmountCompanySupportActual}</label>
							</div>
							<div class="span8">
								<div class="row-fluid">
									<div class="span12">
										<div class="span5">
											<div id="actualAmountCompanySup"></div>
										</div>
										<div class="span7">
											<div class='row-fluid'>
												<div class="span4 text-algin-right">
													<label>${uiLabelMap.HREstimated}</label>
												</div>
												<div class="span8">
													<div id="amountCompanySupport"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 text-algin-right'>
								<label>${uiLabelMap.TotalAmountActual}</label>
							</div>
							<div class="span8">
								<div class="row-fluid">
									<div class="span12">
										<div class="span5">
											<div id="totalAmountActual"></div>
										</div>
										<div class="span7">
											<div class='row-fluid'>
												<div class="span4 text-algin-right">
													<label>${uiLabelMap.HREstimated}</label>
												</div>
												<div class="span8">
													<div id="totalAmountEstimated"></div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
		<div class="row-fluid" style="border-bottom:1px dotted #d0d8e0; margin-top: 15px"></div>
		<#include "PartyAttendanceTraining.ftl"/>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/training/CreateTrainingCourseSummary.js"></script>