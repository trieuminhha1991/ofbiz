<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxradiobutton.js"></script>
<@jqGridMinimumLib />
<script>
	//Prepare data for Employee Position Type 
	<#assign currentDept = Static["com.olbius.util.PartyUtil"].getOrgByManager(userLogin, delegator)>
	<#assign listEmplPositionTypes = delegator.findList("DepPositionTypeView", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("deptId", currentDept), null, null, null, false) >
	var positionTypeData = new Array();
	<#list listEmplPositionTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['emplPositionTypeId'] = '${item.emplPositionTypeId}';
		row['description'] = '${description}';
		positionTypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for Employee Position Type 
	<#assign listAllEmplPositionTypes = delegator.findList("EmplPositionType", null, null, null, null, false) >
	var allPositionTypeData = new Array();
	<#list listAllEmplPositionTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['emplPositionTypeId'] = '${item.emplPositionTypeId}';
		row['description'] = '${description}';
		allPositionTypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for Recruitment Type 
	<#assign listRecruitmentTypes = delegator.findList("RecruitmentType", null, null, null, null, false) >
	var recruitmentTypeData = new Array();
	<#list listRecruitmentTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['recruitmentTypeId'] = '${item.recruitmentTypeId}';
		row['description'] = '${description}';
		recruitmentTypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for Recruitment Form 
	<#assign listRecruitmentForms = delegator.findList("RecruitmentForm", null, null, null, null, false) >
	var recruitmentFormData = new Array();
	<#list listRecruitmentForms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['recruitmentFormId'] = '${item.recruitmentFormId}';
		row['description'] = '${description}';
		recruitmentFormData[${item_index}] = row;
	</#list>
	
	//Prepare for party data
	<#assign listStatus = delegator.findList("StatusItem", null, null, null, null, false) />
	var statusData = new Array();
	<#list listStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		statusData[${item_index}] = row;
	</#list>
	
	//Prepare for Gender data
	<#assign listGenders = delegator.findList("Gender", null, null, null, null, false) />
	var genderData = new Array();
	<#list listGenders as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)>
		row['genderId'] = '${item.genderId}';
		row['description'] = '${description}';
		genderData[${item_index}] = row;
	</#list>
	
	//Prepare for role type data
	<#assign listRoleTypes = delegator.findList("RoleType", null, null, null, null, false)>
	var roleTypeData = new Array();
	<#list listRoleTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description)>
		row['roleTypeId'] = '${item.roleTypeId}';
		row['description'] = "${description}";
		roleTypeData[${item_index}] = row;
	</#list>
	
	 var idColumnFilter = function () {
         var filtergroup = new $.jqx.filter();
         var filter_or_operator = 1;
         var filtervalue = '${StringUtil.wrapString(parameters.jobRequestId?if_exists)}';
         var filtercondition = 'contains';
         var filter = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);
         filtergroup.addfilter(filter_or_operator, filter);
         return filtergroup;
     }();
</script>

<#assign rowdetailstemplateAdvance = "<ul style='margin-left: 30px;'><li class='title'>${uiLabelMap.Criteria}</li><li>${uiLabelMap.JobDescription}</li></ul><div class='criteria'></div><div class='description'></div>" />

<#assign dataField="[{ name: 'jobRequestId', type: 'string'},
					 { name: 'partyId', type: 'string' },
					 { name: 'fromDate', type: 'date', other: 'Timestamp' },
					 { name: 'emplPositionTypeId', type: 'string' },
					 { name: 'resourceNumber', type: 'number' },
					 { name: 'availableNumber', type: 'number' },
					 { name: 'recruitmentTypeId', type: 'string' },
					 { name: 'recruitmentFormId', type: 'string' },
					 { name: 'jobDescription', type: 'string' },
					 { name: 'isInPlan', type: 'string' },
					 { name: 'statusId', type: 'string' },
					 { name: 'age', type: 'string' },
					 { name: 'educationSystemTypeId', type: 'string' },
					 { name: 'genderId', type: 'string' },
					 { name: 'englishSkillId', type: 'string' },
					 { name: 'workSkillId', type: 'string' },
					 { name: 'itSkillId', type: 'string' },
					 { name: 'proposalSal', type: 'number' },
					 { name: 'experience', type: 'string' },
					 { name: 'reason', type: 'string' },
					 { name: 'actorRoleTypeId', type: 'string' },
					 { name: 'actorPartyId', type: 'string' }
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.CommonId}', datafield: 'jobRequestId', width: 100, filter: idColumnFilter, editable: false, pinned: true},
                     { text: '${uiLabelMap.Department}', datafield: 'partyId', width: 250, editable: false,
						cellsrenderer: function(row, column, value){
							  var partyName = value;
							  $.ajax({
									url: 'getPartyName',
									type: 'POST',
									data: {partyId: value},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											partyName = data.partyName;
										}
							        }
								});
							  return '<span title' + value + '>' + partyName + '</span>';
							}
                     },
                     { text: '${uiLabelMap.FromDate}', datafield: 'fromDate', width: 150, cellsformat: 'd', filtertype: 'range', editable: false},
                     { text: '${uiLabelMap.Position}', datafield: 'emplPositionTypeId', width: 250, editable: false,
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < allPositionTypeData.length; i++){
								if(value == allPositionTypeData[i].emplPositionTypeId){
									return '<span title=' + value + '>' + allPositionTypeData[i].description + '</span>'
								}
							}
							return '<span>' + value + '</span>';
						}
                     },
                     { text: '${uiLabelMap.ResourceNumber}', datafield: 'resourceNumber', width: 150, editable: false},
                     { text: '${uiLabelMap.availabelNumber}', datafield: 'availableNumber', width: 150, editable: false},
                     { text: '${uiLabelMap.RecruitmentType}', datafield: 'recruitmentTypeId', width: 250, editable: false,
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < recruitmentTypeData.length; i++){
								if(value == recruitmentTypeData[i].recruitmentTypeId){
									return '<span title=' + value + '>' + recruitmentTypeData[i].description + '</span>'
								}
							}
							return '<span>' + value + '</span>';
						}
                     },
                     { text: '${uiLabelMap.RecruitmentForm}', datafield: 'recruitmentFormId', width: 250, editable: false,
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < recruitmentFormData.length; i++){
								if(value == recruitmentFormData[i].recruitmentFormId){
									return '<span title=' + value + '>' + recruitmentFormData[i].description + '</span>'
								}
							}
							return '<span>' + value + '</span>';
						}
                     },
                     { text: '${uiLabelMap.InPlan}', datafield: 'isInPlan', width: 150, editable: false, 
                    	 cellsrenderer: function(row, column, value){
 							for(var i = 0; i < statusData.length; i++){
 								if(value == statusData[i].statusId){
 									return '<span title=' + value + '>' + statusData[i].description + '</span>'
 								}
 							}
 							return '<span>' + value + '</span>';
 						}
                     },
                     { text: '${uiLabelMap.Status}', datafield: 'statusId', width: 150, editable: true, columntype: 'dropdownlist',
                    	 cellsrenderer: function(row, column, value){
  							for(var i = 0; i < statusData.length; i++){
  								if(value == statusData[i].statusId){
  									return '<span title=' + value + '>' + statusData[i].description + '</span>'
  								}
  							}
  							return '<span>' + value + '</span>';
  						}
                     },
                     { text: '${uiLabelMap.actorPartyId}', datafield: 'actorPartyId', editable: true, width: 150,
                    	 cellsrenderer: function(row, column, value){
							  var partyName = value;
							  $.ajax({
									url: 'getPartyName',
									type: 'POST',
									data: {partyId: value},
									dataType: 'json',
									async: false,
									success : function(data) {
										if(!data._ERROR_MESSAGE_){
											partyName = data.partyName;
										}
							        }
								});
							  return '<span title' + value + '>' + partyName + '</span>';
							}
                     },
                     { text: '${uiLabelMap.actorRoleTypeId}', datafield: 'actorRoleTypeId', editable: true, width: 150,
                    	 cellsrenderer: function(row, column, value){
  							for(var i = 0; i < roleTypeData.length; i++){
  								if(value == roleTypeData[i].roleTypeId){
  									return '<span title=' + value + '>' + roleTypeData[i].description + '</span>';
  								}
  							}
  							return '<span title=' + value + '>' + value + '</span>';
  						}
                     },
                     { text: '${uiLabelMap.CommonComment}', datafield: 'reason', width: 150, editable: true}
					 "/>

<@jqGrid id="jqxgrid" filtersimplemode="true" addrefresh="true" jqGridMinimumLibEnable="false" rowdetailstemplateAdvance=rowdetailstemplateAdvance addType="popup" alternativeAddPopup="alterpopupNewJobRequest" addrow="true" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JQGetListJobRequest" dataField=dataField columnlist=columnlist initrowdetailsDetail="initrowdetails" initrowdetails="true" editable="false"
		 createUrl="jqxGeneralServicer?sname=createJobRequest&jqaction=C" addColumns="workLocation;partyId;recruitmentTypeId;recruitmentFormId;emplPositionTypeId;fromDate(java.sql.Timestamp);resourceNumber(java.lang.Long);availableNumber(java.lang.Long);genderId;age;experience;educationSystemTypeId;englishSkillId;workSkillId;itSkillId;reason;proposalSal(java.lang.Long);jobDescription"
		 updateUrl="jqxGeneralServicer?sname=updateJobRequest&jqaction=U" editColumns="jobRequestId;partyId;statusId;reason;isInPlan"
		 customcontrol1="fa fa-hand-o-right@${uiLabelMap.HRPropose}@javascript: void(0);@propose()"
		 customcontrol2="fa fa-thumbs-o-up@${uiLabelMap.HRApprove}@javascript: void(0);@approve()"
		/>
<#--====================================================Create new popup window==========================================================-->
<#--====================================================CSS=================================================================-->
<style>
.not-active {
	   pointer-events: none;
	   cursor: default;
	   opacity: 0.5;
	}
</style>
<#--====================================================CSS=================================================================-->
<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupNewJobRequest" style="display:none;">
			<div id="windowHeaderNewJobRequest">
	            <span>
	               ${uiLabelMap.NewJobRequest}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentNewRecrProcess">
			    <div id='jqxTabs' style="position: relative;">
		            <ul>
		                <li>${uiLabelMap.RecruitmentInfo}</li>
		                <li>${uiLabelMap.JobRequirementAndDescription}</li>
		            </ul>
		            <div id="tab1">
			            <div class="basic-form form-horizontal" style="margin-top: 10px">
							<form name="createNewJobRequest" id="createNewJobRequest">
								<div class="row-fluid" >
									<div class="span12">
										<div class="span6">
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.Department}:</label>
												<div class="controls">
													<div id="partyIdLabel" style="height: 25px;"></div>
													<input id="partyIdAdd" type="hidden">
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.Position}:</label>
												<div class="controls">
													<div id="emplPositionTypeId" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.workLocation}:</label>
												<div class="controls">
													<input id="workLocation"></input>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.needResourceDate}:</label>
												<div class="controls">
													<div id="fromDate"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.proposalSal}(VND):</label>
												<div class="controls">
													<div id="proposalSal" ></div>
												</div>
											</div>
										</div>
										<div class="span6">
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.ResourceNumber}:</label>
												<div class="controls">
													<div id="resourceNumber"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.RecruitmentType}:</label>
												<div class="controls">
													<div id="recruitmentTypeId" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.RecruitmentForm}:</label>
												<div class="controls">
													<div id="recruitmentFormId" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.resourceRemainInPlan}:</label>
												<div class="controls">
													<div id="resourceRemainInPlanLabel" style="height: 25px;">${uiLabelMap.CommonUndefinded}</div>
													<input id="resourceRemainInPlan" type="hidden"></input>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.overPlanReason}:</label>
												<div class="controls">
													<input id="overPlanReason"></input>
												</div>
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
						<div class="row-fluid jqx-tabs-button-olbius">
		                	<div class="span12" style="text-align: right">
		                		<button type="button" class="btn btn-primary next btn-small" >${uiLabelMap.CommonNext} <i class="icon-arrow-right"></i></button>
		                	</div>
		            	</div>
					</div>
					<div class="basic-form form-horizontal" style="margin-top: 10px">
						<form name="createNewJobReqAndDes" id="createNewJobReqAndDes">
							<div class="row-fluid" >
								<div class="span12">
									<div class="span6">
										<div class="control-group no-left-margin">
											<label class="control-label asterisk">${uiLabelMap.experienceRequest}:</label>
											<div class="controls">
												<input id="experience"></input>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label asterisk">${uiLabelMap.genderRequest}:</label>
											<div class="controls">
												<div id="genderId"></div>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label asterisk">${uiLabelMap.ageRequest}:</label>
											<div class="controls">
												<input id="age"></input>
											</div>
										</div>
									</div>
									<div class="span6">
										<div class="control-group no-left-margin">
											<label class="control-label asterisk">${uiLabelMap.educationSystemTypeIdRequest}:</label>
											<div class="controls">
												<input id="educationSystemTypeId" ></input>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label asterisk">${uiLabelMap.englishSkillIdRequest}:</label>
											<div class="controls">
												<input id="englishSkillId" ></input>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label asterisk">${uiLabelMap.workSkillIdRequest}:</label>
											<div class="controls">
												<input id="workSkillId" ></input>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label asterisk">${uiLabelMap.itSkillIdRequest}:</label>
											<div class="controls">
												<input id="itSkillId" ></input>
											</div>
										</div>
									</div>
									<div class="control-group no-left-margin">
										<label class="control-label asterisk">${uiLabelMap.JobDescription}:</label>
										<div class="controls" id="jobDescriptionContainer">
											<div id="jobDescription" ></div>
										</div>
									</div>
								</div>
							</div>
							 <div class="row-fluid jqx-tabs-button-olbius">
							 	<div class="span12" style="text-align: right">
							 		<button type="button" class="btn btn-success back btn-small" ><i class="icon-arrow-left"></i>${uiLabelMap.CommonBack}</button>
			                		<button type="button" id="btnCreate" class="btn btn-primary btn-small" ><i class="icon-ok"></i>${uiLabelMap.CommonCreate}</button>
			                	</div>
		                	</div>
						</form>
					</div>
	            </div>
	        </div>
		</div>
	</div>
</div>
<#--====================================================/Create new popup window==========================================================-->
<#include "jqxApprove.ftl" />
<script>
	$('#jqxgrid').on('rowSelect', function (event) {
		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
		 <#if Static["com.olbius.util.RoleHelper"].getCurrentRole(userLogin, delegator) == Static["com.olbius.recruitment.helper.RoleTyle"].MANAGER_ROLE>
		 	$("#customcontrol2jqxgrid").addClass("not-active");
	     </#if>
	     <#if Static["com.olbius.util.RoleHelper"].getCurrentRole(userLogin, delegator) == Static["com.olbius.recruitment.helper.RoleTyle"].CEO_ROLE>
		 	$("#addrowbuttonjqxgrid").addClass("not-active");
		 	$("#customcontrol1jqxgrid").addClass("not-active");
	     </#if>
	});
	
	$("#alterSaveAppr").on('click', function(){
		var submitData = {};
		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
		var wgapprovesuccess = "${StringUtil.wrapString(uiLabelMap.wgapprovesuccess)}";
		//Send request propose
		var submitData = {};
		submitData['jobRequestId'] = rowData['jobRequestId'];
		submitData['partyId'] = rowData['partyId'];
		submitData['reason'] = $("#apprComment").val();
		submitData['statusId'] = $("#jqxAccepted").jqxRadioButton('val') ? "JR_ACCEPTED" : "JR_REJECTED";
		submitData['isInPlan'] = rowData['isInPlan'];
    	$.ajax({
			url: 'updateJobRequest',
			type: "POST",
			data: submitData,
			dataType: 'json',
			async: false,
			success : function(data) {
				var message = "";
				var template = "";
				if(data._ERROR_MESSAGE_ || data._ERROR_MESSAGE_LIST_){
					if(data._ERROR_MESSAGE_LIST_){
						message += data._ERROR_MESSAGE_LIST_;
					}
					if(data._ERROR_MESSAGE_){
						message += data._ERROR_MESSAGE_;
					}
					template = "error";
				}else{
					message = wgapprovesuccess;
					template = "success";
					$("#wdwApprove").jqxWindow('close');
					$('#jqxgrid').jqxGrid('updatebounddata');
				}
				updateGridMessage('jqxgrid', template ,message);
			}
		});
	});
	function approve(){
		$("#wdwApprove").jqxWindow('open');
	}
	function propose(){
		var submitData = {};
		var rowindex = $('#jqxgrid').jqxGrid('getselectedrowindex');
		var rowData = $('#jqxgrid').jqxGrid('getrowdata', rowindex);
		var wgproposesuccess = "${StringUtil.wrapString(uiLabelMap.wgproposesuccess)}";
		//Send request propose
		var submitData = {};
		submitData['jobRequestId'] = rowData['jobRequestId'];
		submitData['partyId'] = rowData['partyId'];
		submitData['statusId'] = 'JR_PROPOSED';
		submitData['reason'] = rowData['reason'];
		submitData['isInPlan'] = rowData['isInPlan'];
    	$.ajax({
			url: 'updateJobRequest',
			type: "POST",
			data: submitData,
			dataType: 'json',
			async: false,
			success : function(data) {
				var message = "";
				var template = "";
				if(data._ERROR_MESSAGE_){
					message = data._ERROR_MESSAGE_;
					template = "error";
				}else{
					message = wgproposesuccess;
					template = "success";
				}
				updateGridMessage('jqxgrid', template ,message);
			}
		});
	}
	/*
	 * Prepare data for popup window
	 * */
	$(document).ready(function(){
		<#include "jsApprove.ftl" />
		//Create window
		$("#alterpopupNewJobRequest").jqxWindow({
	        showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "80%", height: 550, minWidth: '40%', width: "80%", isModal: true,
	        theme:theme, collapsed:false, initContent: function () {
	        	// Create jqxTabs.
		        $('#jqxTabs').jqxTabs({ width: '98%', height: 450, position: 'top', disabled:true,
		        	initTabContent:function (tab) {
		        		if(tab == 0){
		        			//Create partyId
		        			<#assign partyId = Static["com.olbius.util.PartyUtil"].getOrgByManager(userLogin, delegator) >
		        			<#assign partyName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)>
		        			jQuery("#partyIdLabel").text('${StringUtil.wrapString(partyName)}');
		        			$("#partyIdAdd").val('${partyId}');
		        			
		        			//Create workLocation
		        			$("#workLocation").jqxInput({width: 195, height: 21});
		        			
		        			//Create emplPositionTypeId
		        			$("#emplPositionTypeId").jqxDropDownList({source: positionTypeData, valueMember: 'emplPositionTypeId', displayMember: 'description'});
		        			$("#emplPositionTypeId").on('change', function (event){     
		        			    var args = event.args;
		        			    if (args && $("#fromDate").jqxDateTimeInput('getDate')) {
		        				    var item = args.item;
		        				    var value = item.value;
		        				    //Create recruitmentTypeId
		        		        	$.ajax({
		        		    			url: 'getResourceInPlan',
		        		    			type: "POST",
		        		    			data: {'partyId': $("#partyIdAdd").val(), 'emplPositionTypeId': value, 'fromDate': $("#fromDate").jqxDateTimeInput('getDate').getTime()},
		        		    			dataType: 'json',
		        		    			async: false,
		        		    			success : function(data) {
		        		    				if(data.responseMessage == 'success'){
		        		    					$("#resourceRemainInPlanLabel").text(data.resourceInPlan);
		        		    					$('#resourceRemainInPlan').val(data.resourceInPlan);
		        		    				}
		        		    			}
		        		    		});
		        			    }
		        			});
		        			
		        			//Create fromDate
		        			$("#fromDate").jqxDateTimeInput({value: null});
		        			$("#fromDate").on('change', function (event){     
		        			    var args = event.args;
		        			    if (args && $('#emplPositionTypeId').val()) {
		        			    	 var fromDate = event.args.date;
		        				    //Create recruitmentTypeId
		        		        	$.ajax({
		        		    			url: 'getResourceInPlan',
		        		    			type: "POST",
		        		    			data: {'partyId': $("#partyIdAdd").val(), 'emplPositionTypeId': $('#emplPositionTypeId').val(), 'fromDate': fromDate.getTime()},
		        		    			dataType: 'json',
		        		    			async: false,
		        		    			success : function(data) {
		        		    				if(data.responseMessage == 'success'){
		        		    					$("#resourceRemainInPlanLabel").text(data.resourceInPlan);
		        		    					$('#resourceRemainInPlan').val(data.resourceInPlan);
		        		    				}
		        		    			}
		        		    		});
		        			    }
		        			});
		        			//Create proposalSal
		        			$("#proposalSal").jqxNumberInput({decimalDigits: 0,  spinButtons: true });
		        			
		        			//Create resourceNumber
		        			$("#resourceNumber").jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0, inputMode: 'simple'});
		        			$('#resourceNumber').on('valueChanged', function () {
		        				var resourceRemainInPlan = parseInt($('#resourceRemainInPlan').val());
		        				var resourceNumber = $('#resourceNumber').jqxNumberInput('getDecimal');
		        				if(resourceNumber == 0 || isNaN(resourceRemainInPlan) || resourceRemainInPlan >= resourceNumber){
		        					$('#overPlanReason').jqxInput({disabled: true });
		        				}else{
		        					$('#overPlanReason').jqxInput({disabled: false });
		        				}
		        			});
		        			//Create recruitmentTypeId
		        			$("#recruitmentTypeId").jqxDropDownList({autoDropDownHeight: true, source: recruitmentTypeData, valueMember: 'recruitmentTypeId', displayMember: 'description'});
		        			
		        			$("#overPlanReason").jqxInput({width: 195, height: 21, disabled: true});
		        			//Create recruitmentFormId
		        			$("#recruitmentFormId").jqxDropDownList({autoDropDownHeight: true, source: recruitmentFormData, valueMember: 'recruitmentFormId', displayMember: 'description'});
		        		}else{
		        			//Create genderId
		        			$("#genderId").jqxDropDownList({source: genderData, valueMember: 'genderId', displayMember: 'description'});
		        			
		        			//Create age
		        			$("#age").jqxInput({width: 195, height: 21});
		        			
		        			//Create experience
		        			$("#experience").jqxInput({width: 195, height: 21});
		        			
		        			//Create educationSystemTypeId
		        			$("#educationSystemTypeId").jqxInput({width: 195, height: 21});
		        			
		        			//Create englishSkillId
		        			$("#englishSkillId").jqxInput({width: 195, height: 21});
		        			
		        			//Create workSkillId
		        			$("#workSkillId").jqxInput({width: 195, height: 21});
		        			
		        			//Create itSkillId
		        			$("#itSkillId").jqxInput({width: 195, height: 21});
		        			
		        			//Create jobDescription
		        			$("#jobDescription").jqxEditor({
		                        height: "200px",
		                        width: '96%',
		                    });
		        		}
		        	}
		        });
		        
		        $('#jqxTabs').jqxTabs('enableAt', 0);
            }
	    });

	    //update the edited row when the user clicks the 'Save' button.
	    //Handle if validation is success
	    $('#btnCreate').on('click', function (event) {
	    	$("#createNewJobReqAndDes").jqxValidator('validate');
	    });
	    
	    // initialize validator.
        $('#createNewJobRequest').jqxValidator({
            rules: [{ input: '#resourceNumber', message: '${uiLabelMap.FieldRequired}', action: 'blur, keyup',
            			rule: function (input, commit) {
            				var val = input.jqxNumberInput('getDecimal');
                            if (val) {
                                return true;
                            }
                            return false;
                        }
           			},
           			{ input: '#resourceNumber', message: '${uiLabelMap.FieldIsNotPlan}', action: 'blur, keyup',
            			rule: function (input, commit) {
            				var val = input.jqxNumberInput('getDecimal');
                            if (val > $("#resourceRemainInPlan").val() && !$("#overPlanReason").val()) {
                                return false;
                            }
                            return true;
                        }
           			},
           			{ input: '#overPlanReason', message: '${uiLabelMap.FieldRequired}', action: 'blur, keyup',
            			rule: function (input, commit) {
            				var val = $("#resourceNumber").jqxNumberInput('getDecimal');
                            if (val > $("#resourceRemainInPlan").val() && !input.val()) {
                                return false;
                            }else{
                            	$('#createNewJobRequest').jqxValidator('hideHint', '#resourceNumber');
                            	return true;
                            }
                        }
           			},
           			{ input: '#workLocation', message: '${uiLabelMap.FieldRequired}', action: 'blur, keyup', rule: 'required'},
           			{ input: '#recruitmentTypeId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
           				}
           			},
           			{ input: '#recruitmentFormId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
        				}
        			},
           			{ input: '#fromDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
           				}
           			},
           			{ input: '#emplPositionTypeId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
        				}
        			}
                   ]
        });
        $('#createNewJobReqAndDes').jqxValidator({
            rules: [{ input: '#age', message: '${uiLabelMap.FieldRequired}', action: 'blur, keyup', rule: 'required'},
        			{ input: '#experience', message: '${uiLabelMap.FieldRequired}', action: 'blur, keyup', rule: 'required'},
        			{ input: '#educationSystemTypeId', message: '${uiLabelMap.FieldRequired}', action: 'blur, keyup', rule: 'required'},
        			{ input: '#englishSkillId', message: '${uiLabelMap.FieldRequired}', action: 'blur, keyup', rule: 'required'},
        			{ input: '#workSkillId', message: '${uiLabelMap.FieldRequired}', action: 'blur, keyup', rule: 'required'},
        			{ input: '#itSkillId', message: '${uiLabelMap.FieldRequired}', action: 'blur, keyup', rule: 'required'},
        			{ input: '#workLocation', message: '${uiLabelMap.FieldRequired}', action: 'blur, keyup', rule: 'required'},
        			{ input: '#genderId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', rule: function (input, commit) {
	                       if(input.val()){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
     					}
        			},
        			{ input: '#jobDescriptionContainer', message: '${uiLabelMap.FieldRequired}', action: 'blur, keyup', rule: function (input, commit) {
        				var obj = $("#jobDescription").jqxEditor('val');
        				if(obj != "<div>​</div>"){
	                    	   return true;
	                       }else{
	                    	   return false;
	                       }
  						}
        			}
                   ]
        });
        
        $(".next").on('click', function(){
    		var selectedItem = $("#jqxTabs").jqxTabs('selectedItem');
    		if(selectedItem == 0){
    			$("#createNewJobRequest").jqxValidator('validate');
    		}
    		$("#jqxTabs").jqxTabs('disableAt', selectedItem);
    		$("#jqxTabs").jqxTabs('next');
    	});
        
        $(".back").on('click', function(){
    		var selectedItem = $("#jqxTabs").jqxTabs('selectedItem');
    		$("#jqxTabs").jqxTabs('disableAt', selectedItem);
    		$("#jqxTabs").jqxTabs('enableAt', selectedItem - 1);
    		$("#jqxTabs").jqxTabs('previous');
    	});
        
        $("#createNewJobRequest").on('validationSuccess', function (event) {
			$("#jqxTabs").jqxTabs('enableAt', 1);
		});
        
        $("#createNewJobReqAndDes").on('validationSuccess', function (event) {
        	var row;
	        row = {
	        		partyId: $('#partyIdAdd').val(),
	        		workLocation: $('#workLocation').val(),
	        		recruitmentTypeId: $('#recruitmentTypeId').val(),
	        		recruitmentFormId: $('#recruitmentFormId').val(),
	        		emplPositionTypeId:$('#emplPositionTypeId').val(),
	        		fromDate:$("#fromDate").jqxDateTimeInput('getDate').getTime(),
	        		resourceNumber:$('#resourceNumber').val(),
	        		availableNumber:$("#resourceRemainInPlan").val(),
	        		genderId:$('#genderId').val(),
	        		age:$('#age').val(),
	        		experience:$("#experience").val(),
	        		educationSystemTypeId:$("#educationSystemTypeId").val(),
	        		englishSkillId:$("#englishSkillId").val(),
	        		workSkillId:$("#workSkillId").val(),
	        		itSkillId:$("#itSkillId").val(),
	        		reason:$("#overPlanReason").val(),
	        		proposalSal:$("#proposalSal").val(),
	        		jobDescription:$("#jobDescription").val()
			  };
		   $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	        // select the first row and clear the selection.
	        $("#jqxgrid").jqxGrid('clearSelection');
	        $("#jqxgrid").jqxGrid('selectRow', 0);
	        $("#alterpopupNewJobRequest").jqxWindow('close');
		});
	});
	
	var initrowdetails = function (index, parentElement, gridElement, datarecord) {
        var tabsdiv = $($(parentElement).children()[0]);
        var jobRequestId = datarecord.jobRequestId;
        var jobDescription = datarecord.jobDescription;
        var age = datarecord.age;
        var experience = datarecord.experience;
        var genderId = datarecord.genderId == 'M' ? 'Nam' : 'Nữ';
        var educationSystemTypeId = datarecord.educationSystemTypeId;
        var englishSkillId = datarecord.englishSkillId;
        var workSkillId = datarecord.workSkillId;
        var itSkillId = datarecord.itSkillId;
        if(tabsdiv.length){
        	 var criteriaContainerFluid = $('<div class="container-fluid" style="padding-top: 20px"></div>');
        	 var criteriaContainer = $('<div class="row-fluid"></div>');
        	 var criteriaContainerFirstSpan6 = $('<div class="span6"></div>');
        	 var criteriaContainerSecondSpan6 = $('<div class="span6"></div>');
        	 var descriptionContainer = $('<div style="margin: 15px;" ></div>');
        	 var criteria = tabsdiv.find('.criteria');
        	 var description = tabsdiv.find('.description');
        	 criteriaContainerFluid.append(criteriaContainer);
        	 criteriaContainer.append(criteriaContainerFirstSpan6);
        	 criteriaContainer.append(criteriaContainerSecondSpan6);
        	 descriptionContainer.append(jobDescription);
        	 criteriaContainerFirstSpan6.append('<p>' + '${uiLabelMap.ageRequest}' + ': ' + age + '</p>');
        	 criteriaContainerFirstSpan6.append('<p>' + '${uiLabelMap.experienceRequest}' + ': ' + experience + '</p>');
        	 criteriaContainerFirstSpan6.append('<p>' + '${uiLabelMap.genderRequest}' + ': ' + genderId + '</p>');
        	 criteriaContainerSecondSpan6.append('<p>' + '${uiLabelMap.educationSystemTypeIdRequest}' + ': ' + educationSystemTypeId + '</p>');
        	 criteriaContainerSecondSpan6.append('<p>' + '${uiLabelMap.englishSkillIdRequest}' + ': ' + englishSkillId + '</p>');
        	 criteriaContainerSecondSpan6.append('<p>' + '${uiLabelMap.workSkillIdRequest}' + ': ' + workSkillId + '</p>');
        	 criteriaContainerSecondSpan6.append('<p>' + '${uiLabelMap.itSkillIdRequest}' + ': ' + itSkillId + '</p>');
        	 description.append(descriptionContainer);
        	 criteria.append(criteriaContainerFluid);
        	 tabsdiv.jqxTabs({ width: '90%', height: 200, position: 'top'});
        }
    }
</script>