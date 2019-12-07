<#include "script/ViewTrainingDetailScript.ftl"/>
<div id="containerEditTraining" class="container-noti">
</div>
<div id="jqxNotificationEditTraining">
    <div id="notificationContentEditTraining">
    </div>
</div>
<#assign statusId = trainingCourse.statusId/>
<#assign hasUpdatePerm = security.hasEntityPermission("HR_TRAINING", "_UPDATE", session)/>
<#assign hasApprPerm = security.hasEntityPermission("HR_TRAINING", "_APPROVE", session)/>
<#assign isEditable = Static["com.olbius.basehr.training.TrainingHelper"].isEditableTraining(delegator, security, userLogin, trainingCourseId)/> 
<style>
.widget-toolbar-border-right {
	border: 1px solid #d9d9d9 !important; 
	border-width: 0 1px 0 0 !important
}
</style>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.TrainigCourseDetailTitle}</h4>
		<div class="widget-toolbar no-border invoice-info <#if (statusDateTimeList?size > 0)>widget-toolbar-border-right</#if>">
			<div class="row-fluid">
			<#if (statusId == "TRAINING_PLANNED_REJ" || statusId == "TRAINING_PLANNED") && hasUpdatePerm>
				<div class="span12">
					<a style="font-size: 14px; cursor: pointer;" id="sendApprTraining" href="javascript:void(0)" data-rel="tooltip" title="${uiLabelMap.SendRequestApproval}" data-placement="bottom" class="button-action"><i class="fa fa-paper-plane-o"></i>${uiLabelMap.SendRequestApproval}</a>
				</div>
			<#elseif statusId == "TRAINING_PLANNED_PPS" && hasApprPerm>
				<div class="span12">
					<a style="font-size: 14px; cursor: pointer;" id="apprTrainingCourse" href="javascript:void(0)" data-rel="tooltip" title="${uiLabelMap.HRApprove}" data-placement="bottom" class="button-action"><i class="fa fa-check-circle-o"></i>${uiLabelMap.HRApprove}</a>
				</div>
			<#elseif (statusId == "TRAINING_PLANNED_ACC" && hasApprPerm) ||  statusId == "TRAINING_SUMMARY" || statusId == "TRAINING_COMPLETED">
				<div class="span12">
					<a style="font-size: 14px; cursor: pointer;" href="ViewTrainingSummary?trainingCourseId=${trainingCourseId}" id="summaryTrainingCourse"><i class="fa fa-check-square-o"></i>${uiLabelMap.HRCommonSummarizing}</a>
				</div>
			</#if>
			</div>
		</div>
		<#if (statusDateTimeList?size > 0)>
			<div class="widget-toolbar">
				<a style="font-size: 14px; cursor: pointer;" href="javascript:void(0)" id="viewApprHistory"><i class="fa fa-history"></i>${uiLabelMap.ApprovalHistory}</a>
			</div>
		</#if>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="row-fluid" style="margin-top: 15px;"><!-- training course info -->
				<div class="span12" style="position: relative;">
					<#if (statusDateTimeList?size > 0)>
						<#assign statusDateTime = statusDateTimeList.get(0)/>
						<#if statusId == statusDateTime.statusId && statusDateTime.changeReason?has_content>
							<#assign popOverContent = statusDateTime.changeReason/>
						</#if>  
					</#if>
					<div class="title-status" style="top: 5px; right: 5px;" 
								<#if popOverContent?has_content>data-placement="left" title="<b>${StringUtil.wrapString(uiLabelMap.HRCommonReason)}</b>" data-rel="popover" data-placement="bottom" data-content="${popOverContent}"</#if>>
						<span style="font-size: 15px"><b>${StringUtil.wrapString(trainingStatus.description)}</b></span>
					</div>
					<div class="form-legend" style="margin-bottom: 15px">
						<div class="contain-legend">
							<span class="content-legend" style="font-size: 15px">
								<a href="javascript:void(0)" title="<#if isEditable>${StringUtil.wrapString(uiLabelMap.ClickToEdit)}</#if>" id="editTrainingCourseInfoBtn">
									${StringUtil.wrapString(uiLabelMap.GeneralInformation)}&nbsp;&nbsp;<#if isEditable><i class="icon-edit"></i></#if></a>
							</span>
						</div>
						<div class="row-fluid">
							<div class="span12" style="margin-top: 10px">
								<div class="span4">
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
											<label class=""><b>${uiLabelMap.HRCommonEstimateStartDateShort}:</b></label>
										</div>  
										<div class="span7">
											<span style="font-size: 14px" id="fromDateView">${trainingCourse.fromDate?string["dd/MM/yyyy"]}</span>
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
									<div class='row-fluid margin-bottom10'>
										<div class='span5 algin-left'>
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
								<div class="span4">
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
											<label class=""><b>${uiLabelMap.HRCommonEstimateEndDateShort}:</b></label>
										</div>  
										<div class="span7">
											<span style="font-size: 14px" id="thruDateView">${trainingCourse.thruDate?string["dd/MM/yyyy"]}</span>
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
								<div class="span4">
									<h4 class="bold smaller">
										<i class="fa-history bold"></i>
										${uiLabelMap.ApprovalHistory}
									</h4>
									<div class="widget-body" style="padding-top: 0">
										<div class="widget-main padding-2">
											<div id="listApprHistoryDetail" class="profile-feed">
												<#if trainingCourseStatusList?has_content>
													<#list trainingCourseStatusList as trainingCourseStatus>
														<div class="profile-activity clearfix" style="padding: 5px 4px">
															<div class="row-fluid">
																<span style="font-size: 14px"><i class="icon-ok blue"></i>${uiLabelMap.HRApprover}: <a class="user" href="javascript:void(0)">${trainingCourseStatus.fullName} - </a>
																${uiLabelMap.HRCommonResults}: <a class="user" href="javascript:void(0)">${trainingCourseStatus.description}</a></span>
																<#if trainingCourseStatus.changeReason?has_content>
																	<div class="row-fluid">
																		<div class="span2">
																			<i class="icon-plus blue"></i> ${uiLabelMap.HRCommonReason}:
																		</div>
																		<div class="span10">
																			 ${StringUtil.wrapString(trainingCourseStatus.changeReason)}
																		</div>
																	</div>
																</#if>
																<div class="time">
																	<i class="icon-time bigger-110"></i>
																	${uiLabelMap.ApprovalDate}: ${trainingCourseStatus.statusDatetime?string["dd/MM/yyyy HH:mm:ss"]}
																</div>
															</div>
														</div>
													</#list>
												<#else>
													<div class="span6">
														<i class="fa-lightbulb-o red"></i> ${uiLabelMap.NotApprovedYet}
													</div>		
												</#if>
											</div>
										</div>
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
											<span style="font-size: 14px"><i class="icon-plus green"></i><b>${uiLabelMap.CostEstimatedEmployee}</b></span>
										</div>
									</div>
									<div class="row-fluid">
										<div class="span12">
											<div class='row-fluid margin-left30'>
												<div class='span5 text-algin-left'>
													<i class="icon-caret-right blue"></i><b>${uiLabelMap.AmountEstimatedEmplPaid}:</b>
												</div>  
												<div class="span7">
													<div id="estimatedEmplPaidView"><@ofbizCurrency amount=trainingCourseDetail.estimatedEmplPaid?if_exists isoCode="VND"/></div>
										   		</div>
											</div>
											<div class='row-fluid margin-bottom10 margin-left30'>
												<div class='span5 text-algin-left'>
													<i class="icon-caret-right blue"></i><b>${uiLabelMap.AmountCompanySupport}:</b>
												</div>  
												<div class="span7">
													<div id="amountCompanySupportView"><@ofbizCurrency amount=trainingCourseDetail.amountCompanySupport?if_exists isoCode="VND"/></div>
										   		</div>
											</div>
										</div>
										
									</div>									
								</div>
								<div class="span6">
									<div class="row-fluid margin-bottom10">
										<div class="margin-left15">
											<span style="font-size: 14px"><i class="icon-plus green"></i><b>${uiLabelMap.HREstimatedCost}</b></span>
										</div>
									</div>
									<div class="row-fluid">
										<div class='row-fluid'>
											<div class="margin-left30">
												<div class='span5 text-algin-left'>
													<i class="icon-caret-right blue"></i><b>${uiLabelMap.CommonEstimatedNumber}:</b>
												</div>  
												<div class="span7">
													<div id="estimatedNumberView">${trainingCourseDetail.estimatedNumber}</div>
										   		</div>
											</div>
										</div>
										<div class='row-fluid margin-bottom10'>
											<div class="margin-left30">
												<div class='span5 text-algin-left'>
													<i class="icon-caret-right blue"></i><b>${uiLabelMap.TotalAmountEstimated}:</b>
												</div>  
												<div class="span7">
													<div id="totalEstimatedCostView"><@ofbizCurrency amount=trainingCourseDetail.totalEstimatedCost?if_exists isoCode="VND"/></div>
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
			<#if isEditable>
				<#assign addrow="true"/>
				<#assign deleterow="true"/>
			<#else>
				<#assign addrow="false"/>
				<#assign deleterow="false"/>
			</#if>	
			<!-- ======================================  training course party expected attendance ================================-->
			<div class="row-fluid">
				<div class="span12">
					<#assign attendanceDatafield = "[{name: 'trainingCourseId', type: 'string'},
													{name: 'partyId', type: 'string'},
													{name: 'partyCode', type: 'string'},
													{name: 'firstName', type: 'string'},
													{name: 'fullName', type: 'string'},
													{name: 'groupName', type: 'string'},
													{name: 'emplPositionType', type: 'string'},
													]"/>
					<#assign attendanceColumns = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '15%'},
												  {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'firstName', width: '25%',
													   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
														   var rowData = $('#gridPtyExpectedAtt').jqxGrid('getrowdata', row);
														   if(rowData){
															   return '<span>' + rowData.fullName + '</span>';
														   }
													   }
												   },
												   {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: '30%'},
												   {text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionType', width: '30%'},
												   "/>			
					<#assign customTitleProperties = StringUtil.wrapString(uiLabelMap.EmplExpectedAttendance)/>	
					<@jqGrid filtersimplemode="false" filterable="false" showtoolbar="true" dataField=attendanceDatafield columnlist=attendanceColumns  
							clearfilteringbutton="false"  editable="false" deleterow=deleterow selectionmode="singlerow"
							addrow=addrow alternativeAddPopup="popupWindowEmplList" addType="popup" 
							showlist="false" sortable="true" id="gridPtyExpectedAtt" 
							customTitleProperties=customTitleProperties
							updateUrl="" editColumns=""
							removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteTrainingPartyAttendance" deleteColumn="trainingCourseId;partyId"
							url="jqxGeneralServicer?sname=JQListTrainingPartyExpectedAtt&trainingCourseId=${trainingCourseId}" jqGridMinimumLibEnable="false"/>											   									
				</div>
			</div>
			<!-- ./end -->
			
			<!-- ======================================  training course party registed ================================-->
			<div class="row-fluid">
				<div class="span12">
					<#assign attendanceDatafield = "[{name: 'trainingCourseId', type: 'string'},
													{name: 'partyId', type: 'string'},
													{name: 'partyCode', type: 'string'},
													{name: 'firstName', type: 'string'},
													{name: 'fullName', type: 'string'},
													{name: 'groupName', type: 'string'},
													{name: 'emplPositionType', type: 'string'},
													]"/>
					<#assign attendanceColumns = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '15%'},
												  {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'firstName', width: '25%',
													   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
														   var rowData = $('#gridPtyRegisted').jqxGrid('getrowdata', row);
														   if(rowData){
															   return '<span>' + rowData.fullName + '</span>';
														   }
													   }
												   },
												   {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: '30%'},
												   {text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionType', width: '30%'},
												   "/>			
					<#assign customTitleProperties = StringUtil.wrapString(uiLabelMap.EmplRegisted)/>	
					<@jqGrid filtersimplemode="false" filterable="false" showtoolbar="true" dataField=attendanceDatafield columnlist=attendanceColumns  
							clearfilteringbutton="false"  editable="false" deleterow=deleterow selectionmode="singlerow"
							addrow=addrow alternativeAddPopup="popupWindowEmplList" addType="popup" 
							showlist="false" sortable="true" id="gridPtyRegisted" 
							customTitleProperties=customTitleProperties
							updateUrl="" editColumns=""
							removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteTrainingPartyAttendance" deleteColumn="trainingCourseId;partyId"
							url="jqxGeneralServicer?sname=JQListTrainingPartyRegisted&trainingCourseId=${trainingCourseId}" jqGridMinimumLibEnable="false"/>											   									
				</div>
			</div>
			<!-- ./end -->
			
			<!-- ================================================= training course skill ============================================-->
			<div class="row-fluid" >
				<div class="span12">
				<#assign skillDatafield = "[{name: 'trainingCourseId', type: 'string'},
							                 {name: 'skillTypeId', type: 'string'},
							                 {name: 'description', type: 'string'},
							                 {name: 'parentTypeDescription', type: 'string'},
							                 {name: 'resultTypeId', type: 'string'}]"/>
							                 
				<#assign skillColumnlist = "{text: '${StringUtil.wrapString(uiLabelMap.HRSkillType)}', datafield: 'description', width: '30%', editable: false},
							                {text: '${StringUtil.wrapString(uiLabelMap.HRSkillTypeParent)}', datafield: 'parentTypeDescription', width: '30%', editable: false},
							                {text: '${StringUtil.wrapString(uiLabelMap.RequrimentLevelSkillTrainingCourse)}', datafield:'resultTypeId', width: '40%', columntype: 'dropdownlist',
							            	   cellsrenderer: function (row, column, value){
							            		   for(var i = 0; i< globalVar.trainingResultTypeArr.length; i++){
							            			   if(value == globalVar.trainingResultTypeArr[i].resultTypeId){
							            				   return '<span>' + globalVar.trainingResultTypeArr[i].description + '</span>';
							            			   }
							            		   }
							            		   return '<span>' + value + '</span>';
							            	   },
							            	   createEditor: function (row, cellvalue, editor, cellText, width, height) {
							            		   createJqxDropDownList(globalVar.trainingResultTypeArr, editor, 'resultTypeId', 'description', height, width);
							            	   },
							            	   initEditor: function (row, cellvalue, editor, celltext, width, height) {
													editor.val(cellvalue);
							            	   }
							                }"/>	
				<#assign customTitleProperties = StringUtil.wrapString(uiLabelMap.TrainingCourseSkillTypeList)/>	
				<@jqGrid filtersimplemode="false" filterable="false" showtoolbar="true" dataField=skillDatafield columnlist=skillColumnlist  
					clearfilteringbutton="false"  editable="true" deleterow=deleterow selectionmode="singlerow"
					addrow=addrow alternativeAddPopup="addSkillTrainingWindow" addType="popup" 
					showlist="false" sortable="true" id="gridSkillTrain" 
					customTitleProperties=customTitleProperties
					updateUrl="jqxGeneralServicer?jqaction=U&sname=updateTrainingCourseSkillType" editColumns="trainingCourseId;skillTypeId;resultTypeId"
					removeUrl="jqxGeneralServicer?sname=deleteTrainingCourseSkillType&jqaction=D" deleteColumn="trainingCourseId;skillTypeId"
					url="jqxGeneralServicer?sname=JQListTrainingCourseSkillType&trainingCourseId=${trainingCourseId}" jqGridMinimumLibEnable="false"/>																                									                  
				</div>		
			</div>
			<!-- ./end training course skill -->
		</div>
	</div>
</div>
<div id="apprHisWindow" class="hide">
	<div>${uiLabelMap.ApprovalHistory}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="widget-box transparent">
				<div class="widget-header widget-header-small">
					<h4 class="blue smaller">
						<i class="fa-history blue"></i>
						${uiLabelMap.ApprovalHistory}
					</h4>
				</div>
				<div class="widget-body" style="padding-top: 0">
					<div class="widget-main padding-2">
						<div id="listApprHistory" class="profile-feed">
							<#if trainingCourseStatusList?has_content>
								<#list trainingCourseStatusList as trainingCourseStatus>
									<div class="profile-activity clearfix" style="padding: 5px 4px">
										<div class="row-fluid">
											<span style="font-size: 14px"><i class="icon-ok blue"></i>${uiLabelMap.HRApprover}: <a class="user" href="javascript:void(0)">${trainingCourseStatus.fullName} - </a>
											${uiLabelMap.HRCommonResults}: <a class="user" href="javascript:void(0)">${trainingCourseStatus.description}</a></span>
											<#if trainingCourseStatus.changeReason?has_content>
												<div class="row-fluid">
													<div class="span2">
														<i class="icon-plus blue"></i> ${uiLabelMap.HRCommonReason}:
													</div>
													<div class="span10">
														 ${StringUtil.wrapString(trainingCourseStatus.changeReason)}
													</div>
												</div>
											</#if>
											<div class="time">
												<i class="icon-time bigger-110"></i>
												${uiLabelMap.ApprovalDate}: ${trainingCourseStatus.statusDatetime?string["dd/MM/yyyy HH:mm:ss"]}
											</div>
										</div>
									</div>
								</#list>
							</#if>
						</div>
					</div>
				</div>
			</div>
			<div class="hr hr2 hr-double"></div>
		</div>
		<div class="form-action">
			<button id="closeApprHistory" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
		</div>
	</div>	
</div>
<script type="text/javascript" src="/hrresources/js/training/ViewTrainingDetail.js"></script>

<#if isEditable>
	<div id="addSkillTrainingWindow" class="hide">
		<div>${uiLabelMap.AddTrainingCourseSkillType}</div>
		<div class='form-window-container' style="position: relative;">
			<div class="form-window-content">
				<div id="skillListGrid"></div>
			</div>
			<div class="form-action">
				<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditTrainingCourseSkill">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
				<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditTrainingCourseSkill">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
			</div>
		</div>	
	</div>
	<#include "editTrainingCourseInfo.ftl"/>	
	<script type="text/javascript" src="/hrresources/js/training/editTrainingCourseSkill.js"></script>
	<#include "AddSkillType.ftl"/>
	<#include "editTrainingCourseCost.ftl"/>
	
	<div class="row-fluid">
		<div id="popupWindowEmplList" class='hide'>
			<div>
				${uiLabelMap.HREmplList}
			</div>
			<div class='form-window-container'>
				<div id="splitterEmplList" style="border: none;">
					<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
						<div id="jqxTreeEmplList"></div>
					</div>
					<div style="overflow: hidden !important;">
		               <div class="jqx-hideborder jqx-hidescrollbars" >
		               		<div class='form-window-content'>
			                   <div id="EmplListInOrg">
			                   </div>
		               		</div>
		               </div>
		        	</div>
				</div>
				<div class="form-action">
		    		<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelAddPartyExpected">
						<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
		    		<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddPartyExpected">
						<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		    	</div>
			</div>
		</div>
	</div>	
	<script type="text/javascript" src="/hrresources/js/training/AddPartyExpectedAttendance.js"></script>
	<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
		jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId="" isDropDown="false" width="100%" height="100%" expandAll="false"/>
	<script type="text/javascript">
		<#if expandedList?has_content>
			<#assign expandTreeId=expandedList[0]>
			if(typeof(globalVar.expandTreeId) == 'undefined'){
				globalVar.expandTreeId = "${expandTreeId}";		
			}
		</#if>
		function jqxTreeEmplListSelect(event){
			var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
			var partyId = item.value;
			refreshBeforeReloadGrid($("#EmplListInOrg"));
			tmpS = $("#EmplListInOrg").jqxGrid('source');
			tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
			$("#EmplListInOrg").jqxGrid('source', tmpS);
		}
	</script>
</#if>

<#if statusId == "TRAINING_PLANNED_PPS" && hasApprPerm>
	<#include "ApprovalTrainingCourse.ftl"/>
</#if>