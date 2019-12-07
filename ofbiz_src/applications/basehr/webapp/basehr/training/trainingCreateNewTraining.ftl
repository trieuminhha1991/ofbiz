<#if !popWindowId?has_content>
	<#assign popWindowId = "newTrainingWindow"/>  
</#if>
<#include "script/trainingCreateNewTrainingScript.ftl"/>
<div id="${popWindowId}" class="hide">
	<div>${uiLabelMap.CreateNewTrainingCourse}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div class="row-fluid" >
				<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
			        <ul class="wizard-steps wizard-steps-square">
		                <li data-target="#trainingCourseInfo" class="active">
		                    <span class="step">1. ${uiLabelMap.GeneralInformation}</span>
		                </li>
		                <li data-target="#trainingCourseSkill">
		                    <span class="step">2. ${uiLabelMap.TrainingCourseSkillType}</span>
		                </li>
		                <li data-target="#trainingCourseTrainee">
		                    <span class="step">3. ${uiLabelMap.TrainingCourseTrainees}</span>
		                </li>
		                <li data-target="#trainingCourseProviderAndCost">
		                    <span class="step">4. ${uiLabelMap.TrainingCourseProvider} ${uiLabelMap.CommonAnd} ${uiLabelMap.HRCommonCost}</span>
		                </li>
			    	</ul>
			    </div><!--#fuelux-wizard-->
			    <div class="step-content row-fluid position-relative" id="step-container">
			    	<div class="step-pane active" id="trainingCourseInfo">
			    		<div class="row-fluid" style="margin-top: 15px">
				    		<div class="span12">
					    		<div class="span6">
					    			<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											<label class="asterisk">${uiLabelMap.TrainingCourseIdShort}</label>
										</div>  
										<div class="span7">
											<input type="text" id="trainingCourseId${popWindowId}">
								   		</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											<label class="asterisk">${uiLabelMap.HRRegisterStartDateShort}</label>
										</div>  
										<div class="span7">
											<div id="registerFromDate${popWindowId}"></div>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											<label class="asterisk">${uiLabelMap.HRCommonFromDate}</label>
										</div>  
										<div class="span7">
											<div id="fromDate${popWindowId}"></div>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											<label class="">${uiLabelMap.CommonLocation}</label>
										</div>  
										<div class="span7">
											<input type="text" id="location${popWindowId}">
											<!-- <div id="geoId${popWindowId}"></div> -->
								   		</div>
									</div>
					    		</div>
					    		
					    		<div class="span6">
					    			<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											<label class="asterisk">${uiLabelMap.TrainingCourseNameShort}</label>
										</div>  
										<div class="span7">
											<input type="text" id="trainingCourseName${popWindowId}">
								   		</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											<label class="asterisk">${uiLabelMap.HRRegisterEndDateShort}</label>
										</div>  
										<div class="span7">
											<div id="registerThruDate${popWindowId}"></div>
								   		</div>
									</div>
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											<label class="asterisk">${uiLabelMap.HRCommonThruDate}</label>
										</div>  
										<div class="span7">
											<div id="thruDate${popWindowId}"></div>
								   		</div>
									</div>
									
									<div class='row-fluid margin-bottom10'>
										<div class='span5 text-algin-right'>
											<label class="">${uiLabelMap.TrainingFormTypeId}</label>
										</div>  
										<div class="span7">
											<div id="trainingFormTypeId${popWindowId}"></div>
								   		</div>
									</div>
					    		</div>
				    		</div>
			    		</div>
			    		<div class="row-fluid">
			    			<div class="span12">
			    				<div class='row-fluid margin-bottom10'>
									<div class='span2 text-algin-right' style="margin-left: 29px">
										<label class="">${uiLabelMap.CommonPurpose}</label>
									</div>  
									<div class="span9">
										<div id="trainingPurposeTypeId${popWindowId}"></div>
							   		</div>
								</div>
			    			</div>
			    		</div>
			    		<div class="row-fluid">
			    			<div class="span12">
			    				<div class='row-fluid margin-bottom10'>
			    					<div class='span2 text-algin-right' style="margin-left: 29px">
					    				<label class="">${uiLabelMap.CommonCertificate}</label>
					    			</div>
					    			<div class="span9">
										<input type="text" id="certificate${popWindowId}">
							   		</div>
			    				</div>
			    			</div>
			    		</div>
			    		<div class="row-fluid">
			    			<div class="span12">
				    			<div class='row-fluid margin-bottom10'>
					    			<div class='span2 text-algin-right' style="margin-left: 29px">
					    				<label class="">${uiLabelMap.CommonDescription}</label>
					    			</div>
					    			<div class="span9">
					    				<textarea id="description${popWindowId}" data-maxlength="250" rows="2" style="resize: vertical;margin-top:0px" class="span12"></textarea>
					    			</div>
				    			</div>
			    			</div>
			    		</div>
			    	</div>
			  		<div class="step-pane" id="trainingCourseSkill">
			  			<div id="skillTypeGrid${popWindowId}"></div>
			  		</div>
			  		<div class="step-pane" id="trainingCourseTrainee">
			  			<div id="partyExpectedGrid${popWindowId}"></div>
			  		</div>
			  		<div class="step-pane" id="trainingCourseProviderAndCost">
			  			<div class="row-fluid" style="margin-top: 15px">
			  				<div class="span12">
			  					<div class="span6 boder-all-profile" style="padding: 20px 10px 0 15px">
			  						<span class="text-header">${uiLabelMap.CostEstimatedEmployee}</span>
			  						<div class='row-fluid margin-bottom10'>
				  						<div class='span4 text-algin-right'>
				  							<label class="">${uiLabelMap.AmountEstimatedEmplPaid}</label>
				  						</div>
				  						<div class="span8">
				  							<div id="amountEmplPaid${popWindowId}"></div>
				  						</div>
				  					</div>	
				  					<div class='row-fluid margin-bottom10'>
				  						<div class='span4 text-algin-right'>
				  							<label class="">${uiLabelMap.AmountCompanySupport}</label>
				  						</div>
				  						<div class="span8">
				  							<div id="amountCompanyPaid${popWindowId}"></div>
				  						</div>
				  					</div>
			  					</div>
			  					<div class="span6 boder-all-profile" style="padding: 20px 10px 0 15px">
			  						<span class="text-header">${uiLabelMap.TotalAmountEstimated}</span>
			  						<div class='row-fluid margin-bottom10'>
				  						<div class='span4 text-algin-right'>
				  							<label class="">${uiLabelMap.CommonEstimatedNumber}</label>
				  						</div>
				  						<div class="span8">
				  							<div id="nbrEmplEstimated${popWindowId}"></div>
				  						</div>
				  					</div>
			  						<div class='row-fluid margin-bottom10'>
				  						<div class='span4 text-algin-right'>
				  							<label class="">${uiLabelMap.TotalAmountEstimated}</label>
				  						</div>
				  						<div class="span8">
				  							<div id="totalCostEstimated${popWindowId}"></div>
				  						</div>
				  					</div>
			  					</div>
			  				</div>
			  			</div>
			  			<div class="row-fluid">
				  			<div class="span12 boder-all-profile"  style="margin-top: 15px; padding: 20px 10px 0 15px">
				  				<div class="span6">
				  					<div class='row-fluid margin-bottom10'>
					  					<div class='span4 text-algin-right'>
											<label class="">${uiLabelMap.TrainingProvider}</label>
										</div>  
										<div class="span8">
											<div id="trainingProvider${popWindowId}"></div>
								   		</div>
					  				</div>
				  				</div>
				  				<div class="span6">
				  					<div class='row-fluid margin-bottom10'>
					  					<div class='span4 text-algin-right'>
											<label class="">${uiLabelMap.HRAddrContactShort}</label>
										</div>
										 <div class="span8">
										 	<div id="providerContact${popWindowId}"></div>
										 </div>
				  					</div>
				  				</div>
				  			</div>
			  			</div>
			  			<div class="row-fluid" style="margin-top: 15px">
			  				<div class='row-fluid '>
		  						<div class='span1 text-algin-right'>
		  						</div>
		  						<div class="span11">
		  							<div id="isNotPublic${popWindowId}"><label>${uiLabelMap.OnlyEmplInRegisterList}</label></div>
		  						</div>
		  					</div>
			  				<div class='row-fluid margin-bottom10'>
			  					<div class='span1 text-algin-right'>
								</div>  
								<div class="span11">
									<div id="isPublic${popWindowId}"><label>${uiLabelMap.AllowAllEmployeeRegister}</label></div>
						   		</div>
			  				</div>
			  				<div class='row-fluid margin-bottom10'>
			  					<div class='span1 text-algin-right'>
								</div>  
								<div class="span9" >
									<div id="allowCancelRegister${popWindowId}" style="float: left; margin-left: 0 !important"><label>${uiLabelMap.AllowCancelRegisterBefore}</label></div>
									<div id="nbrDayBeforeStart${popWindowId}" style="float: left;"></div>
									<div style="float: left; margin-left: 10px"><label>${uiLabelMap.CommonDay}</label></div>
									
								</div>
								<div class="span3" style="margin: 0">
								</div>
			  				</div>
			  			</div>
			  		</div>
			    </div>
			    <div class="form-action wizard-actions">
					<button class="btn btn-next btn-success form-action-button pull-right" 
						data-last="${StringUtil.wrapString(uiLabelMap.HRCommonCreateNew)}" id="btnNext">
						${uiLabelMap.CommonNext}
						<i class="icon-arrow-right icon-on-right"></i>
					</button>
					<button class="btn btn-prev form-action-button pull-right" id="btnPrev">
						<i class="icon-arrow-left"></i>
						${uiLabelMap.CommonPrevious}
					</button>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoading" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinner-ajax"></div>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="row-fluid">
	<div id="addPartyExpectedWindow" class='hide'>
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
	                   <div id="EmplListInOrg">
	                   </div>
	               </div>
	               <div class="row-fluid" style="margin-top: 10px">
						<button id="btnCancelAddParty" class='btn btn-danger form-action-button pull-right'>
							<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
						<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSaveAddParty">
							<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
					</div>
	        	</div>
			</div>
		</div>
	</div>
</div>

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

<script type="text/javascript" src="/hrresources/js/training/trainingCreateNewTrainingInfo.js"></script>
<script type="text/javascript" src="/hrresources/js/training/trainingCreateTrainingSkill.js"></script>
<script type="text/javascript" src="/hrresources/js/training/trainingCreatePartyExpectedJoin.js"></script>
<script type="text/javascript" src="/hrresources/js/training/trainingCreateTrainingProvider.js"></script>
<script type="text/javascript" src="/hrresources/js/training/trainingCreateNewTraining.js"></script>

<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId="" isDropDown="false" width="100%" height="100%" expandAll="false"/>
	
<script type="text/javascript">
function jqxTreeEmplListSelect(event){
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	$("#EmplListInOrg").jqxGrid('clearselection');
	var partyId = item.value;
	refreshBeforeReloadGrid($("#EmplListInOrg"));
	
	tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}
</script>	