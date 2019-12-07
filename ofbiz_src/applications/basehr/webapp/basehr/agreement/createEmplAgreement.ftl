<style>
.marginOnlyLeft10{
	margin-left: 10px
}
.marginBottom10{
	margin-bottom: 10px
}
</style>
<div class="row-fluid" style="position: relative;">
	<#if !windowPopupId?has_content>
	 <#assign windowPopupId = "windowPopupId"/>
	</#if>
	<div id="${windowPopupId}" class="hide">
		<div>${StringUtil.wrapString(uiLabelMap.HRNewAgreement)}</div>
		<div class='form-window-container' >
			<div class='form-window-content' style="border: none;">
				<div class="row-fluid">
					<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
				        <ul class="wizard-steps wizard-steps-square">
				                <li data-target="#createAgreementStep1" class="active">
				                    <span class="step">1. ${uiLabelMap.AgreementInfo}</span>
				                </li>
				                <li data-target="#createAgreementStep2">
				                    <span class="step">2. ${uiLabelMap.OtherInformation}</span>
				                </li>
				    	</ul>
				    </div><!--#fuelux-wizard-->
				    <div class="step-content row-fluid position-relative" id="step-container">
				    	<div class="step-pane active" id="createAgreementStep1">
							<div class="span12" style="margin-top: 20px">
								<div class="span6">
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.PartyIdFrom)}</label>
										</div>
										<div class="span7">
											<input type="text" id="partyIdFrom${windowPopupId?if_exists}">
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="">${StringUtil.wrapString(uiLabelMap.HRAgreementName)}</label>
										</div>
										<div class="span7">
											<input type="text" id="agreementDesc${windowPopupId?if_exists}">
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.agreementTypeId)}</label>
										</div>
										<div class="span7">
											<div id="agreementTypeId${windowPopupId?if_exists}"></div>
										</div>
									</div>
									
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.EmployeeFullName)}</label>
										</div>
										<div class="span7">
											<input type="text" id="employeeName${windowPopupId?if_exists}">
												<img alt="search" id="searchEmpl" width="16" height="16" src="/aceadmin/assets/images/search_lg.png" 
												style="
												   border: #d5d5d5 1px solid; padding: 4px;
												   border-bottom-right-radius: 3px; border-top-right-radius: 3px; margin-left: -3.5px;
												   background-color: #f0f0f0; border-left: 0px; cursor: pointer;
												"/>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HREffectiveDate)}</label>
										</div>
										<div class="span7">
											<div class="row-fluid">
												<div class="span12">
													<div class="span10">
														<div id="fromDate${windowPopupId?if_exists}"></div>
													</div>		
													<div class="span2" style="">
														<button id="getDateJoinCompany" title="${StringUtil.wrapString(uiLabelMap.GetDateJoinCompanyOfEmpl)}" 
															class="btn btn-mini btn-primary" style="width: 95%">
															<i class="icon-only icon-list open-sans" style="font-size: 15px; position: relative; top: -2px;"></i>
														</button>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="">${StringUtil.wrapString(uiLabelMap.JobPosition)}</label>
										</div>
										<div class="span7">
											<div id="emplPosition${windowPopupId?if_exists}"></div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="">${StringUtil.wrapString(uiLabelMap.SalaryBaseFlat)}</label>
										</div>
										<div class="span7">
											<div id="basicSalary${windowPopupId?if_exists}"></div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="">${StringUtil.wrapString(uiLabelMap.InsuranceSalaryShort)}</label>
										</div>
										<div class="span7">
											<div id="insuranceSalary${windowPopupId?if_exists}"></div>
										</div>
									</div>
								</div>
								<div class="span6">
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.EmplAgreementNumber)}</label>
										</div>
										<div class="span7">
											<input type="text" id="agreementCode${windowPopupId?if_exists}">
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.AgreementDate)}</label>
										</div>
										<div class="span7">
											<div id="agreementDate${windowPopupId?if_exists}"></div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="">${StringUtil.wrapString(uiLabelMap.ContactDuration)}</label>
										</div>
										<div class="span7">
											<div class="row-fluid">
												<div class="span12">
													<div class="span10">
														<div id="agreementDuration${windowPopupId?if_exists}"></div>
													</div>		
													<div class="span2" style="">
														<button id="newAgrDur" title="${StringUtil.wrapString(uiLabelMap.CreateNewAgrDur)}" 
															class="btn btn-mini btn-primary" style="width: 95%">
															<i class="icon-only icon-plus open-sans" style="font-size: 15px; position: relative; top: -2px;"></i>
														</button>
													</div>
												</div>
											</div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="asterisk">${StringUtil.wrapString(uiLabelMap.EmployeeId)}</label>
										</div>
										<div class="span7">
											<input id="partyIdTo${windowPopupId?if_exists}" type="text">
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="">${StringUtil.wrapString(uiLabelMap.HRExpireDate)}</label>
										</div>
										<div class="span7">
											<div id="thruDate${windowPopupId?if_exists}"></div>
										</div>
									</div>
									
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="">${StringUtil.wrapString(uiLabelMap.PartyIdWork)}</label>
										</div>
										<div class="span7">
											<input type="text" id="organization${windowPopupId?if_exists}">
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="">${StringUtil.wrapString(uiLabelMap.HRPayRate)}</label>
										</div>
										<div class="span7">
											<div id="payRate${windowPopupId?if_exists}"></div>
										</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class="span5 text-algin-right">
											<label class="">${StringUtil.wrapString(uiLabelMap.WorkingShifWorkTypeWorkWeek)}</label>
										</div>
										<div class="span7">
											<div id="workTypeWorkWeek${windowPopupId?if_exists}"></div>
										</div>
									</div>
								</div>
							</div>
				    	</div>
				    	<div class="step-pane" id="createAgreementStep2">
				    		<div id="jqxPanelStep2" style="border: none;">
					    		<div class="span12" style="margin-top: 10px">
					    			<div class="span6">
						    			<div class='row-fluid margin-bottom10'>
											<div class="span4 text-algin-right">
												<label class="">${StringUtil.wrapString(uiLabelMap.HRPersonRepresentOfOrg)}</label>
											</div>
											<div class="span8">
												<#assign partyIdRepresent = "partyIdRep" + windowPopupId?if_exists/>
												<input type="text" id="partyIdRep${windowPopupId?if_exists}">
													<img alt="search" id="searchRepresent" width="16" height="16" src="/aceadmin/assets/images/search_lg.png" 
													style="
													   border: #d5d5d5 1px solid; padding: 4px;
													   border-bottom-right-radius: 3px; border-top-right-radius: 3px; margin-left: -3.5px;
													   background-color: #f0f0f0; border-left: 0px; cursor: pointer;
													"/>	
											</div>
										</div>
					    			</div>
					    			<div class="span6">
					    				<div class='row-fluid margin-bottom10'>
											<div class="span4 text-algin-right">
												<label class="">${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}</label>
											</div>
											<div class="span8">
												<div id="representEmplPosition${windowPopupId?if_exists}"></div>
											</div>
										</div>
					    			</div>
					    		</div>
					    		<div class="row-fluid">
						    		<div class="span12">
					    				<div class='row-fluid margin-bottom10'>
											<div class="span2 text-algin-right">
												<label class="">${StringUtil.wrapString(uiLabelMap.HRCommonAttactFile)}</label>
											</div>
											<div class="span10">
												<div class="row-fluid" id="agreementFileList">
												</div>
												<div class="row-fluid">	
													<#if security.hasEntityPermission("HR_AGREEMENT", "_ADMIN", session)>
														<div class="span12">
															<div class="span11" style="margin: 0px 0 0 -6px; height: 0px !important; width: 93%">
																<form class="no-margin" action="" class="row-fluid" id="upLoadFileForm"  method="post" enctype="multipart/form-data">
																	<input type="hidden" name="_uploadedFile_fileName" id="_uploadedFile_fileName" value="" />
																	<input type="hidden" name="_uploadedFile_contentType" id="_uploadedFile_contentType" value="" />
														 			<input type="file" id="uploadedFile" name="uploadedFile"/>
															 	</form>
														 	</div>
													 		<div class="span1 pull-right" style="margin: 2px 5px 0 5px; text-align: right;">
													 			<button class="btn btn-primary btn-mini" type="button" style="width: 100%" id="uploadFileAgrBtn">
													 				<i class="icon-only fa-upload"></i></button>
													 		</div>
													 	</div>
													 </#if>
											 	</div>
											</div>
										</div>
					    			</div>
					    		</div>
					    		<div class="row-fluid" style="margin-top: 10px">
					    			<div class="span12">
					    				<div id="allowanceGrid"></div>
					    			</div>
					    		</div>
				    		</div>
				    	</div>
				    </div>
				    <#if security.hasEntityPermission("HR_AGREEMENT", "_ADMIN", session)>
				    	<#assign btnFinish = uiLabelMap.CommonSave>
				    <#else>
				    	<#assign btnFinish = uiLabelMap.CommonClose>
				    </#if>
					<div class="form-action wizard-actions">
						<button class="btn btn-next btn-success form-action-button pull-right" data-last="${btnFinish}" id="btnNext">
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
							<div class="loader-page-common-custom" id="spinner-ajax"></div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
<div class="row-fluid">
	<div id="popupWindowEmplList" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div>
			<div id="splitterEmplList" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplList"></div>
				</div>
				<div id="ContentPanel" style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	                   <div id="EmplListInOrg">
	                   </div>
	               </div>
	        	</div>
			</div>
		</div>
	</div>
</div>
<div class="row-fluid">
	<div id="popupWindowEmpReplList" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div>
			<div id="splitterEmplRepList" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplRepList"></div>
				</div>
				<div id="ContentPanelRep" style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	                   <div id="EmplRepresentList">
	                   </div>
	               </div>
	        	</div>
			</div>
		</div>
	</div>
</div>
<div id="addNewAllowanceWindow" class="hide">
	<div>${uiLabelMap.AddNewAllowance}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.AllowancesType)}</label>
				</div>
				<div class="span7">
					<div id="allowance${windowPopupId?if_exists}"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}</label>
				</div>
				<div class="span7">
					<div id="allowanceAmount${windowPopupId?if_exists}"></div>
				</div>
			</div>
			<div class="form-action">
	    		<button type="button" class='btn btn-danger form-action-button pull-right' id="alterCancel">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
				<button id="saveAllowanceAndContinue" class='btn btn-success form-action-button pull-right'>
					<i class='fa-plus'></i> ${uiLabelMap.SaveAndContinue}</button>
	    		<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
	    	</div>
		</div>	
	</div>
</div>
<div id="newAgrDurationWindow" class="hide">
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class="form-row-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.CommonName}</label>
				</div>
				<div class="span8">
					<input type="text" id="agreementeriodName">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 align-right'>
					<label class="asterisk">${uiLabelMap.LengthOfTime}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span6">
								<div id="periodLength"></div>
							</div>
							<div class="span6">
								<div id="uomIdAgrPeriod"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingAgreementPeriod" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAgreementPeriod"></div>
				</div>
			</div>		
		</div>
		<div class="form-action">
			<button type="button" class="btn btn-danger form-action-button pull-right" id="cancelCreateAgrDur">
				<i class="icon-remove"></i>&nbsp;${uiLabelMap.CommonClose}</button>
			<button type="button" class="btn btn-primary form-action-button pull-right" id="saveCreateAgrDur">
				<i class="fa fa-check"></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/agreement/createEmplAgreement.js"></script>
<script type="text/javascript" src="/hrresources/js/agreement/createEmplAgreementStep1.js"></script>
<script type="text/javascript" src="/hrresources/js/agreement/createEmplAgreementStep2.js"></script>
<#include "script/createEmplAgreementScript.ftl" />
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId=expandTreeId isDropDown="false" width="100%" height="100%" expandAll="false"/>
