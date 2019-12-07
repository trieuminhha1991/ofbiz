<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtabs.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.spinner.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script>
	theme = 'olbius';
	sequenceNum = 0;
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
	
	//Prepare data for Work Effort Type 
	<#assign listWorkEffortType = delegator.findList("WorkEffortType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentTypeId", "RECRUITMENT_ROUND"), null, null, null, false) >
	var workEffortTypeData = new Array();
	<#list listWorkEffortType as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['workEffortTypeId'] = '${item.workEffortTypeId}';
		row['description'] = '${description}';
		workEffortTypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for Empl Position Type 
	<#assign listEmplPositionTypes = delegator.findList("EmplPositionType", null, null, null, null, false) >
	var emplPositionTypeData = new Array();
	<#list listEmplPositionTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists) />
		row['emplPositionTypeId'] = '${item.emplPositionTypeId}';
		row['description'] = '${description}';
		emplPositionTypeData[${item_index}] = row;
	</#list>
	
	//Prepare data for Party Name
	<#assign listPartyGroups = delegator.findList("PartyGroup", null, null, null, null, false) >
	var partyData = new Array();
	<#list listPartyGroups as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.groupName?if_exists) />
		row['partyId'] = '${item.partyId}';
		row['description'] = "${description}";
		partyData[${item_index}] = row;
	</#list>
	
</script>
<#assign dataField="[{ name: 'workEffortId', type: 'string' },
					 { name: 'workEffortName', type: 'string' }
					 ]"/>

<#assign columnlist="{ text: '${uiLabelMap.CommonId}', datafield: 'workEffortId', width: 200,
						cellsrenderer: function(column, row, value){
		             		return '<span><a href=FindApplRecruitmentProcess?workEffortId=' + value + '>' + value + '</a></span>';
		             	}
					 },
                     { text: '${uiLabelMap.workEffortName}', datafield: 'workEffortName'}
					 "/>

<@jqGrid addrow="true" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true" initrowdetailsDetail="initrowdetails" initrowdetails="true"
		 url="jqxGeneralServicer?sname=JQGetListOfficeRecruitmentProcess" alternativeAddPopup="alterpopupNewRecrProcess" dataField=dataField columnlist=columnlist
		 />

<#include "jqxEditRecruitmentRound.ftl" />
<div class="row-fluid">
	<div class="span12">
		<div id="alterpopupNewRecrProcess" style="display: none">
			<div id="windowHeaderNewRecrProcess">
	            <span>
	               ${uiLabelMap.NewRecrProcess}
	            </span>
	        </div>
	        <div style="overflow: hidden; padding: 5px; margin-left: 10px" id="windowContentNewRecrProcess">
			    <div id='jqxTabs' style="position: relative;">
		            <ul>
		                <li>${uiLabelMap.RecruitmentProcess}</li>
		                <li>${uiLabelMap.RecruitmentRound}</li>
		                <li>${uiLabelMap.ContactInfo}</li>
		            </ul>
		            <div id="newRecrProcess">
		            	<div class="basic-form form-horizontal" style="margin-top: 10px">
		        			<form name="createNewRecrProcess" id="createNewRecrProcess">	
					            <div class="row-fluid" >
									<div class="span12">
										<div class="span6">
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.JobRequest}:</label>  
												<div class="controls">
													<div id="jrJobRequestId">
														<div id="jqxJRGrid"></div>
													</div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.workEffortName}:</label>  
												<div class="controls">
													<input id="workEffortName"></input>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.estimatedStartDate}:</label>  
												<div class="controls">
													<div id="estimatedStartDate"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.estimatedCompletionDate}:</label>  
												<div class="controls">
													<div id="estimatedCompletionDate"></div>
												</div>
											</div>
											<#--<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.actualStartDate}:</label>  
												<div class="controls">
													<div id="actualStartDate"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label asterisk">${uiLabelMap.actualCompletionDate}:</label>  
												<div class="controls">
													<div id="actualCompletionDate"></div>
												</div>
											</div>
											-->
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.CommonDescription}:</label>  
												<div class="controls">
													<input id="description"></input>
												</div>
											</div>
										</div>
										<div class="span6">
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.Position}:</label>  
												<div class="controls">
													<div id="emplPositionTypeId">
														<div id="jqxPosGrid"></div>
													</div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.Department}:</label>  
												<div class="controls">
													<div id="partyId">
														<div id="jqxPartyGrid"></div>
													</div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.workLocation}:</label>
												<div class="controls">
													<input id="workLocation"></input>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.ResourceNumber}:</label>  
												<div class="controls">
													<div id="resourceNumber"></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.RecruitmentType}:</label>
												<div class="controls">
													<div id="recruitmentTypeId" ></div>
												</div>
											</div>
											<div class="control-group no-left-margin">
												<label class="control-label">${uiLabelMap.RecruitmentForm}:</label>
												<div class="controls">
													<div id="recruitmentFormId" ></div>
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
		            <div id="newRecrRound" style="margin:10px">
		            	<div id="jqxgridNewRecrRound"></div>
		                <div class="row-fluid jqx-tabs-button-olbius">
			            	<div class="span12" style="text-align: right">
			            		<button type="button" class="btn btn-primary btn-success back btn-small"><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		               			<button type="button" class="btn btn-primary btn-small next">${uiLabelMap.CommonNext}<i class="icon-arrow-right"></i></button>
		                	</div>
	                	</div>
		            </div>
		            <div id="newContactInfo">
			            <div class="basic-form form-horizontal" style="margin-top: 10px">
		        			<form name="createNewContactInfo" id="createNewContactInfo">	
					            <div class="row-fluid" >
									<div class="span12">
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.FullName}:</label>  
											<div class="controls">
												<div id="ctPartyId">
													<div id="jqxCtPartyGrid"></div>
												</div>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.Position}:</label>  
											<div class="controls">
												<div id="ctEmplPositionTypeId">
													<div id="jqxCtPosGrid"></div>
												</div>
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.Email}:</label>  
											<div class="controls">
												<input id="ctEmail">
											</div>
										</div>
										<div class="control-group no-left-margin">
											<label class="control-label">${uiLabelMap.Mobile}:</label>  
											<div class="controls">
												<input id="ctMobile">
											</div>
										</div>
									</div>
								</div>
							</form>
						</div>
		                <div class="row-fluid jqx-tabs-button-olbius">
			            	<div class="span12" style="text-align: right">
			            		<button type="button" class="btn btn-primary btn-success back btn-small"><i class="icon-arrow-left"></i>${uiLabelMap.CommonPrevious}</button>
		               			<button type="button" id='submit' class="btn btn-primary btn-small"><i class="icon-ok"></i>${uiLabelMap.CommonCreate}</button>
		                	</div>
	                	</div>
		            </div>
		        </div>
	        </div>
		</div>
	</div>
</div>
<script>
	$(document).ready(function (){
		$("#alterpopupNewRecrProcess").jqxWindow({
		    showCollapseButton: false, maxHeight: 1000, autoOpen: false, maxWidth: "90%", height: 530, minWidth: '40%', width: "90%", isModal: true,
		    theme:theme, collapsed:false,
		    initContent: function () {
		    	// Create jqxTabs.
		        $('#jqxTabs').jqxTabs({ width: '98%', height: 430, position: 'top', disabled:true,
		        	initTabContent:function (tab) {
		        		if(tab == 0){
		        			//Job Request Grid
		                	var sourceJR =
		                	{
		                			datafields:
		                				[
		                				 { name: 'jobRequestId', type: 'string' },
		                				 { name: 'emplPositionTypeId', type: 'string' },
		                				 { name: 'resourceNumber', type: 'string' },
		                				 { name: 'workLocation', type: 'string' },
		                				 { name: 'recruitmentFormId', type: 'string' },
		                				 { name: 'recruitmentTypeId', type: 'string' },
		                				 { name: 'partyId', type: 'string' },
		                				 { name: 'fromDate', type: 'date' },
		                				 { name: 'thruDate', type: 'date' }
		                				],
		                			cache: false,
		                			root: 'results',
		                			datatype: "json",
		                			updaterow: function (rowid, rowdata) {
		                				// synchronize with the server - send update command   
		                			},
		                			beforeprocessing: function (data) {
		                				sourceJR.totalrecords = data.TotalRows;
		                			},
		                			filter: function () {
		                				// update the grid and send a request to the server.
		                				$("#jqxJRGrid").jqxGrid('updatebounddata');
		                			},
		                			pager: function (pagenum, pagesize, oldpagenum) {
		                				// callback called when a page or page size is changed.
		                			},
		                			sort: function () {
		                				$("#jqxJRGrid").jqxGrid('updatebounddata');
		                			},
		                			sortcolumn: 'jobRequestId',
		                			sortdirection: 'asc',
		                			type: 'POST',
		                			data: {
		                				noConditionFind: 'Y',
		                				conditionsFind: 'N',
		                			},
		                			pagesize:15,
		                			contentType: 'application/x-www-form-urlencoded',
		                			url: 'jqxGeneralServicer?sname=getListJobRequest',
		                	};
		                	var dataAdapterJR = new $.jqx.dataAdapter(sourceJR);
		                	$("#jrJobRequestId").jqxDropDownButton({ width: 200, height: 25});
		                	$("#jqxJRGrid").jqxGrid({
		                		source: dataAdapterJR,
		                		filterable: true,
		                		showfilterrow: true,
		                		virtualmode: true, 
		                		sortable:true,
		                		theme: theme,
		                		editable: false,
		                		autoheight:true,
		                		pageable: true,
		                		width: 800,
		                		rendergridrows: function(obj)
		                		{
		                			return obj.data;
		                		},
		                	columns: [
		                	  { text: '${uiLabelMap.CommonId}', datafield: 'jobRequestId', filtertype: 'input'},
		                	  { text: '${uiLabelMap.Position}', datafield: 'emplPositionTypeId', filtertype: 'input', width: 250,
		                		  cellsrenderer: function(column, row, value){
		  							for(var i = 0; i < emplPositionTypeData.length; i++){
		  								if(value == emplPositionTypeData[i].emplPositionTypeId){
		  									return '<span title=' + value + '>' + emplPositionTypeData[i].description + '</span>';
		  								}
		  							}
		  							return '<span>' + value + '</span>';
		  						}
		                	  },
		                	  { text: '${uiLabelMap.Department}', datafield: 'partyId', filtertype: 'input', width: 150,
		                		  cellsrenderer: function(column, row, value){
			  							for(var i = 0; i < partyData.length; i++){
			  								if(value == partyData[i].partyId){
			  									return '<span title=' + value + '>' + partyData[i].description + '</span>';
			  								}
			  							}
			  							return '<span>' + value + '</span>';
			  						}
		                	  },
		                	  { text: '${uiLabelMap.fromDate}', datafield: 'fromDate', filtertype: 'input', cellsformat: 'dd/MM/yyyy', width: 150},
		                	  { text: '${uiLabelMap.thruDate}', datafield: 'thruDate', filtertype: 'input', cellsformat: 'dd/MM/yyyy', width: 150}
		                	]
		                	});
		                	$("#jqxJRGrid").on('rowselect', function (event) {
		                		var args = event.args;
		                		var row = $("#jqxJRGrid").jqxGrid('getrowdata', args.rowindex);
		                		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['jobRequestId'] +'</div>';
		                		$('#jrJobRequestId').jqxDropDownButton('setContent', dropDownContent);
		                		
		                		//Set Party
		                		selectedPartyId = row['partyId'];
		                		var groupName = selectedPartyId;
		                		for(var i = 0; i < partyData.length; i++){
		                			if(partyData[i].partyId == selectedPartyId){
		                				groupName = partyData[i].description;
		                				break;
		                			}
		                		}
		                		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ groupName +'</div>';
		                		$('#partyId').jqxDropDownButton('setContent', dropDownContent);
		                		
		                		//Set EmplPositionType
		                		selectedEmplPositionTypeId = row['emplPositionTypeId'];
		                		var description = selectedEmplPositionTypeId;
		                		for(var i = 0; i < emplPositionTypeData.length; i++){
		                			if(emplPositionTypeData[i].emplPositionTypeId == selectedEmplPositionTypeId){
		                				description = emplPositionTypeData[i].description;
		                				break;
		                			}
		                		}
		                		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +'</div>';
		                		$('#emplPositionTypeId').jqxDropDownButton('setContent', dropDownContent);
		                		
		                		//Set workLocation
		                		$('#workLocation').val(row['workLocation']);
		                		
		                		//Set resourceNumber
		                		$('#resourceNumber').val(row['resourceNumber']);
		                		
		                		//Set recruitmentFormId
		                		var selectedRFIndex = 0;
		                		for(var i = 0; i < recruitmentFormData.length; i++){
		                			if(recruitmentFormData[i].recruitmentFormId == row['recruitmentFormId']){
		                				selectedRFIndex = i;
		                				break;
		                			}
		                		}
		                		$("#recruitmentFormId").jqxDropDownList({selectedIndex:selectedRFIndex});
		                		
		                		//Set recruitmentTypeId
		                		var selectedRTIndex = 0;
		                		for(var i = 0; i < recruitmentTypeData.length; i++){
		                			if(recruitmentTypeData[i].recruitmentTypeId == row['recruitmentTypeId']){
		                				selectedRTIndex = i;
		                				break;
		                			}
		                		}
		                		$("#recruitmentTypeId").jqxDropDownList({selectedIndex:selectedRTIndex});
		                		
		                		//Set workEffortName
		                		$("#workEffortName").val("Đợt tuyển dụng " + row['jobRequestId']);
		                		
		                		$('#jrJobRequestId').jqxDropDownButton('close');
		                	});
		                	
		                	//Party Grid
		                	var sourceParty =
		                	{
		                			datafields:
		                				[
		                				 { name: 'partyId', type: 'string' },
		                				 { name: 'groupName', type: 'string' }
		                				],
		                			cache: false,
		                			root: 'results',
		                			datatype: "json",
		                			updaterow: function (rowid, rowdata) {
		                				// synchronize with the server - send update command   
		                			},
		                			beforeprocessing: function (data) {
		                				sourceParty.totalrecords = data.TotalRows;
		                			},
		                			filter: function () {
		                				// update the grid and send a request to the server.
		                				$("#jqxPartyGrid").jqxGrid('updatebounddata');
		                			},
		                			pager: function (pagenum, pagesize, oldpagenum) {
		                				// callback called when a page or page size is changed.
		                			},
		                			sort: function () {
		                				$("#jqxPartyGrid").jqxGrid('updatebounddata');
		                			},
		                			sortcolumn: 'partyId',
		                			sortdirection: 'asc',
		                			type: 'POST',
		                			data: {
		                				noConditionFind: 'Y',
		                				conditionsFind: 'N',
		                			},
		                			pagesize:15,
		                			contentType: 'application/x-www-form-urlencoded',
		                			url: 'jqxGeneralServicer?sname=getListPartyGroups',
		                	};
		                	var dataAdapterParty = new $.jqx.dataAdapter(sourceParty);
		                	$("#partyId").jqxDropDownButton({ width: 200, height: 25, disabled: true});
		                	$("#jqxPartyGrid").jqxGrid({
		                		source: dataAdapterParty,
		                		filterable: true,
		                		showfilterrow: true,
		                		virtualmode: true, 
		                		sortable:true,
		                		theme: theme,
		                		editable: false,
		                		autoheight:true,
		                		pageable: true,
		                		width: 800,
		                		rendergridrows: function(obj)
		                		{
		                			return obj.data;
		                		},
		                	columns: [
		                	  { text: '${uiLabelMap.CommonId}', datafield: 'partyId', filtertype: 'input', width: 150},
		                	  { text: '${uiLabelMap.Department}', datafield: 'groupName', filtertype: 'input'},
		                	]
		                	});
		                	$("#jqxPartyGrid").on('rowselect', function (event) {
		                		var args = event.args;
		                		var row = $("#jqxPartyGrid").jqxGrid('getrowdata', args.rowindex);
		                		selectedPartyId = row['partyId'];
		                		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['groupName'] +'</div>';
		                		$('#partyId').jqxDropDownButton('setContent', dropDownContent);
		                	});
		                	
		                	//Position Grid
		                	var sourcePos =
		                	{
		                			datafields:
		                				[
		                				 { name: 'emplPositionTypeId', type: 'string' },
		                				 { name: 'partyId', type: 'string' },
		                				 { name: 'description', type: 'string' },
		                				 { name: 'groupName', type: 'string' },
		                				],
		                			cache: false,
		                			root: 'results',
		                			datatype: "json",
		                			updaterow: function (rowid, rowdata) {
		                				// synchronize with the server - send update command   
		                			},
		                			beforeprocessing: function (data) {
		                				sourcePos.totalrecords = data.TotalRows;
		                			},
		                			filter: function () {
		                				// update the grid and send a request to the server.
		                				$("#jqxPosGrid").jqxGrid('updatebounddata');
		                			},
		                			pager: function (pagenum, pagesize, oldpagenum) {
		                				// callback called when a page or page size is changed.
		                			},
		                			sort: function () {
		                				$("#jqxPosGrid").jqxGrid('updatebounddata');
		                			},
		                			sortcolumn: 'emplPositionTypeId',
		                			sortdirection: 'asc',
		                			type: 'POST',
		                			data: {
		                				noConditionFind: 'Y',
		                				conditionsFind: 'N',
		                			},
		                			pagesize:15,
		                			contentType: 'application/x-www-form-urlencoded',
		                			url: 'jqxGeneralServicer?sname=getListEmplPositionTypes',
		                	};
		                	var dataAdapterParty = new $.jqx.dataAdapter(sourcePos);
		                	$("#emplPositionTypeId").jqxDropDownButton({ width: 200, height: 25, disabled: true});
		                	$("#jqxPosGrid").jqxGrid({
		                		source: dataAdapterParty,
		                		filterable: true,
		                		showfilterrow: true,
		                		virtualmode: true, 
		                		sortable:true,
		                		theme: theme,
		                		editable: false,
		                		autoheight:true,
		                		pageable: true,
		                		width: 800,
		                		rendergridrows: function(obj)
		                		{
		                			return obj.data;
		                		},
		                	columns: [
		                	  { text: '${uiLabelMap.Position}', datafield: 'description', filtertype: 'input', width: 150},
		                	  { text: '${uiLabelMap.Department}', datafield: 'groupName', filtertype: 'input'},
		                	]
		                	});
		                	$("#jqxPosGrid").on('rowselect', function (event) {
		                		var args = event.args;
		                		var row = $("#jqxPosGrid").jqxGrid('getrowdata', args.rowindex);
		                		selectedEmplPositionTypeId = row['emplPositionTypeId'];
		                		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['description'] +'</div>';
		                		$('#emplPositionTypeId').jqxDropDownButton('setContent', dropDownContent);
		                	});
		                	
		                	//Create workEffortName
		                	$("#workEffortName").jqxInput({width: 195});
		                	
		                	//Create estimatedStartDate
		                	$("#estimatedStartDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy'});
		                	
		                	//Create estimatedCompletionDate
		                	$("#estimatedCompletionDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});
		                	/*$("#estimatedCompletionDate").on('change', function (event) {  
                			    var selectedDate = event.args.date;
                			    $("#actualCompletionDate").jqxDateTimeInput({value: selectedDate});
                			}); */
		                	/*	//Create actualStartDate
		                	$("#actualStartDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy'});
		                	
		                	//Create actualCompletionDate
		                	$("#actualCompletionDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy', value: null});*/
		                	
		                	//Create description
		                	$("#description").jqxInput({width: 195});
		                	
		                	//Create resourceNumber
		                	$("#resourceNumber").jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0, disabled: true});
		                	
		                	//Create workLocation
		                	$("#workLocation").jqxInput({width: 195, disabled: true});
		                	
		                	//Create recruitmentTypeId
		                	$("#recruitmentTypeId").jqxDropDownList({source: recruitmentTypeData, valueMember: 'recruitmentTypeId', displayMember: 'description', disabled: true});
		                	
		                	//Create recruitmentFormId
		                	$("#recruitmentFormId").jqxDropDownList({source: recruitmentFormData, valueMember: 'recruitmentFormId', displayMember: 'description', disabled: true});
		        		}else if(tab == 1){
		        			rrData = new Array();
		        			row = {
		    		        		workEffortName:"Vòng trúng tuyển",
		    		        		sequenceNum:"10000",
		    		        		workEffortTypeId:"ROUND_SELECTED",
		    		        		description:"Vòng mặc định",
		    				  };
		        			rrData[0] = row;
		        			rrIndex = 1;
		        			var source =
		                    {
		                        localdata: rrData,
		                        datatype: "array",
		                        datafields:
		                        [
	                            	{ name: 'workEffortName', type: 'string' },
	                            	{ name: 'workEffortTypeId', type: 'string' },
									{ name: 'estimatedStartDate', type: 'date' },
									{ name: 'estimatedCompletionDate', type: 'date' },
									{ name: 'description', type: 'string' },
									{ name: 'location', type: 'string'}
		                        ],
			        			updaterow: function (rowid, rowdata, commit) {
			        				rrData[rowid] = rowdata;
			        		        commit(true);
			        		    }
		                    };
		                	var dataAdapter = new $.jqx.dataAdapter(source);
		                    
		                	$("#jqxgridNewRecrRound").jqxGrid(
		                    {
		                        width: '98%',
		                        source: dataAdapter,
		                        columnsresize: true,
		                        pageable: true,
		                        editable: true,
		                        autoheight: true,
		                        showtoolbar: true,
		                        rendertoolbar: function (toolbar) {
		                            var container = $("<div id='toolbarcontainer' class='widget-header'>");
		                            toolbar.append(container);
		                            container.append('<h4></h4>');
		                            container.append('<button id="rrAddrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>');
		                            container.append('<button id="rrDelrowbutton" style="margin-left:20px; margin-top:5px; margin-right:5px;"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
				                    $("#rrAddrowbutton").jqxButton();
				                    $("#rrDelrowbutton").jqxButton();
		                            // create new row.
		                            $("#rrAddrowbutton").on('click', function () {
		                            	$("#createNewRecrRoundWindow").jqxWindow('open');
		                            });
		                            
		                            // create new row.
		                            $("#rrDelrowbutton").on('click', function () {
		                            	var selectedrowindex = $('#jqxgridNewRecrRound').jqxGrid('selectedrowindex'); 
		                            	rrData.splice(selectedrowindex, 1);
		                            	$('#jqxgridNewRecrRound').jqxGrid('updatebounddata'); 
		                            	
		                            });
		        	            },
		                        columns: [
									{ text: '${uiLabelMap.roundType}', datafield: 'workEffortTypeId', width: 150,columntype: 'dropdownlist',
										cellsrenderer: function(column, row, value){
											for(var i = 0; i < workEffortTypeData.length; i++){
												if(value == workEffortTypeData[i].workEffortTypeId){
													return '<span title=' + value + '>' + workEffortTypeData[i].description + '</span>';
												}
											}
											return '<span>' + value + '</span>';
										},
										createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
									        editor.jqxDropDownList({source: workEffortTypeData, selectedIndex: 0, valueMember: 'workEffortTypeId', displayMember: 'description'});
									    }
								  },
	                          	  { text: '${uiLabelMap.roundName}', datafield: 'workEffortName', width: 150},
	                          	  { text: '${uiLabelMap.estimatedStartDate}', datafield: 'estimatedStartDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm', columntype:'datetimeinput',
			  						createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
			  					        editor.jqxDateTimeInput({formatString: 'dd/MM/yyyy HH:mm'});
			  					    }  
	                          	  },
		                          { text: '${uiLabelMap.estimatedCompletionDate}', datafield: 'estimatedCompletionDate', width: 150, cellsformat: 'dd/MM/yyyy HH:mm', columntype:'datetimeinput',
	                          		createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
			  					        editor.jqxDateTimeInput({formatString: 'dd/MM/yyyy HH:mm'});
			  					    }   
		                          },
		                          { text: '${uiLabelMap.description}', datafield: 'description', width: 150},
		                          { text: '${uiLabelMap.location}', datafield: 'location'},
		                        ]
		                    });
		        		}else if(tab == 2){
		        			//Party Grid
		                	var sourceParty =
		                	{
		                			datafields:
		                				[
		                				 { name: 'partyId', type: 'string' },
		                				 { name: 'firstName', type: 'string' },
		                				 { name: 'lastName', type: 'string' },
		                				 { name: 'middleName', type: 'string' }
		                				],
		                			cache: false,
		                			root: 'results',
		                			datatype: "json",
		                			updaterow: function (rowid, rowdata) {
		                				// synchronize with the server - send update command   
		                			},
		                			beforeprocessing: function (data) {
		                				sourceParty.totalrecords = data.TotalRows;
		                			},
		                			filter: function () {
		                				// update the grid and send a request to the server.
		                				$("#jqxCtPartyGrid").jqxGrid('updatebounddata');
		                			},
		                			pager: function (pagenum, pagesize, oldpagenum) {
		                				// callback called when a page or page size is changed.
		                			},
		                			sort: function () {
		                				$("#jqxCtPartyGrid").jqxGrid('updatebounddata');
		                			},
		                			sortcolumn: 'partyId',
		                			sortdirection: 'asc',
		                			type: 'POST',
		                			data: {
		                				noConditionFind: 'Y',
		                				conditionsFind: 'N',
		                			},
		                			pagesize:15,
		                			contentType: 'application/x-www-form-urlencoded',
		                			url: 'jqxGeneralServicer?sname=getListPeople',
		                	};
		                	var dataAdapterParty = new $.jqx.dataAdapter(sourceParty,{
		        		    	autoBind: true,
		        		    	formatData: function (data) {
		        		    		if (data.filterscount) {
		        		                var filterListFields = "";
		        		                for (var i = 0; i < data.filterscount; i++) {
		        		                    var filterValue = data["filtervalue" + i];
		        		                    var filterCondition = data["filtercondition" + i];
		        		                    var filterDataField = data["filterdatafield" + i];
		        		                    var filterOperator = data["filteroperator" + i];
		        		                    filterListFields += "|OLBIUS|" + filterDataField;
		        		                    filterListFields += "|SUIBLO|" + filterValue;
		        		                    filterListFields += "|SUIBLO|" + filterCondition;
		        		                    filterListFields += "|SUIBLO|" + filterOperator;
		        		                }
		        		                data.filterListFields = filterListFields;
		        		            }else{
		        		            	data.filterListFields = null;
		        		            }
		        		            return data;
		        		        },
		        		        loadError: function (xhr, status, error) {
		        		            alert(error);
		        		        },
		        		        downloadComplete: function (data, status, xhr) {
		        	                if (!sourceParty.totalRecords) {
		        	                	sourceParty.totalRecords = parseInt(data['odata.count']);
		        	                }
		        		        }
		        		    });
		                	$("#ctPartyId").jqxDropDownButton({ width: 225, height: 25});
		                	$("#jqxCtPartyGrid").jqxGrid({
		                		source: dataAdapterParty,
		                		filterable: true,
		                		showfilterrow: true,
		                		virtualmode: true, 
		                		sortable:true,
		                		theme: theme,
		                		editable: false,
		                		autoheight:true,
		                		pageable: true,
		                		width: 800,
		                		rendergridrows: function(obj)
		                		{
		                			return obj.data;
		                		},
		                	columns: [
		                	  { text: '${uiLabelMap.CommonId}', datafield: 'partyId', filtertype: 'input', width: 150},
		                	  { text: '${uiLabelMap.firstName}', datafield: 'firstName', filtertype: 'input'},
		                	  { text: '${uiLabelMap.middleName}', datafield: 'middleName', filtertype: 'input'},
		                	  { text: '${uiLabelMap.lastName}', datafield: 'lastName', filtertype: 'input'},
		                	]
		                	});
		                	$("#jqxCtPartyGrid").on('rowselect', function (event) {
		                		var args = event.args;
		                		var row = $("#jqxCtPartyGrid").jqxGrid('getrowdata', args.rowindex);
		                		selectedCtPartyId = row['partyId'];
		                		var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['lastName'] + " " + row['middleName'] + " " + row['firstName'] +'</div>';
		                		$('#ctPartyId').jqxDropDownButton('setContent', dropDownContent);
		                		var posData = new Array();
		                		$.ajax({
		            				url: 'getEmplPosition',
		            				type: "POST",
		            				data: {partyId: selectedCtPartyId},
		            				dataType: 'json',
		            				async: false,
		            				success : function(data) {
	            						var pos = {emplPositionTypeId: data.emplPositionTypeId, description: data.emplPositionTypeDesc}
	            						posData[0] = pos;
		            				}
		            			});
		                		$("#ctEmplPositionTypeId").jqxDropDownList({source: posData});
		                		
		                		$.ajax({
		            				url: 'getPersonalEmail',
		            				type: "POST",
		            				data: {partyId: selectedCtPartyId},
		            				dataType: 'json',
		            				async: false,
		            				success : function(data) {
		            					$("#ctEmail").val(data.emailAddress);
		            				}
		            			});
		                		
		                		$.ajax({
		            				url: 'getPartyTelephone',
		            				type: "POST",
		            				data: {partyId: selectedCtPartyId, contactMechPurposeTypeId: 'PHONE_MOBILE'},
		            				dataType: 'json',
		            				async: false,
		            				success : function(data) {
		            					$("#ctMobile").val('+' + data.countryCode + data.contactNumber);
		            				}
		            			});
		                		$("#ctPartyId").jqxDropDownButton('close');
		                	});
		                	
		                	//Create ctEmplPositionTypeId
		                	$("#ctEmplPositionTypeId").jqxDropDownList({width: 225, selectedIndex:0, valueMember: 'emplPositionTypeId', displayMember:'description'});
		                	
		                	//Create ctEmail 
		                	$("#ctEmail").jqxInput({width: 220});
		                	
		                	//Create ctMobile
		                	$("#ctMobile").jqxInput({width: 220});
		        		}
		        	}
		        });
		        $("#jqxTabs").jqxTabs('enableAt', 0);
		    }
		});
		
		/*========================================Recruitment Round=========================================*/
		//Create createNewRecrRoundWindow
		$("#createNewRecrRoundWindow").jqxWindow({
	        showCollapseButton: false, maxHeight: 1000,modalZIndex: 1000, autoOpen: false, maxWidth: "80%", height: 350, minWidth: '40%', width: "80%", isModal: true,
	        theme:'olbius', collapsed:false,cancelButton: $('#alterCancelRecrRound')
	    });
		
		//Create rrWorkEffortName
		$("#rrWorkEffortName").jqxInput({width: 195});
		
		//Create rrEstimatedStartDate
		$("#rrEstimatedStartDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy HH:mm'});
		
		//Create rrEstimatedCompletionDate
		$("#rrEstimatedCompletionDate").jqxDateTimeInput({formatString: 'dd/MM/yyyy HH:mm', value: null});
		
		//Create rrDescription
		$("#rrDescription").jqxInput({width: 195});
		
		//Create rrLocation
		$("#rrLocation").jqxInput({width: 195});
		
		//Create rrSequenceNum
		$('#rrSequenceNum').jqxNumberInput({spinButtons: true, decimalDigits: 0, min: 0});
		
		//Create workEffortTypeId
		$("#rrWorkEffortTypeId").jqxDropDownList({source: workEffortTypeData, selectedIndex: 0, valueMember: 'workEffortTypeId', displayMember: 'description'});
		
		//Handle click event on alterSaveRecrRound
		$("#alterSaveRecrRound").click(function () {
			$("#createNewRecrRoundWindow").jqxValidator('validate');
	    });
		
		$("#createNewRecrRoundWindow").on('validationSuccess', function (event) {
			sequenceNum = $("#rrSequenceNum").val() + 1;
			var row;
	        row = {
	        		workEffortName:$('#rrWorkEffortName').val(),
	        		estimatedStartDate:$("#rrEstimatedStartDate").jqxDateTimeInput('getDate').getTime(),
	        		estimatedCompletionDate:$("#rrEstimatedCompletionDate").jqxDateTimeInput('getDate').getTime(),
	        		description:$("#rrDescription").val(),
	        		location:$("#rrLocation").val(),
	        		sequenceNum:$("#rrSequenceNum").val(),
	        		workEffortTypeId:$("#rrWorkEffortTypeId").val(),
			  };
	        rrIndex = rrData.length - 1 ;
	        rrData[++rrIndex] = row;
	        $("#jqxgridNewRecrRound").jqxGrid('updatebounddata');
	        // select the first row and clear the selection.
	        $("#jqxgridNewRecrRound").jqxGrid('clearSelection');
	        $("#jqxgridNewRecrRound").jqxGrid('selectRow', 0);
	        $("#createNewRecrRoundWindow").jqxWindow('close');
		});
		
		/*========================================End Recruitment Round=========================================*/
		$(".next").click(function(){
			var selectedItem = $("#jqxTabs").jqxTabs('selectedItem');
			if(selectedItem == 0){
				$("#createNewRecrProcess").jqxValidator('validate');
			}else{
				$("#jqxTabs").jqxTabs('disableAt', selectedItem);
				$("#jqxTabs").jqxTabs('enableAt', selectedItem + 1);
			}
			$("#jqxTabs").jqxTabs('next');
			
		});
		$("#createNewRecrProcess").on('validationSuccess', function (event) {
			$("#jqxTabs").jqxTabs('disableAt', 0);
			$("#jqxTabs").jqxTabs('enableAt', 1);
		});
		$(".back").click(function(){
			var selectedItem = $("#jqxTabs").jqxTabs('selectedItem');
			$("#jqxTabs").jqxTabs('disableAt', selectedItem);
			$("#jqxTabs").jqxTabs('enableAt', selectedItem - 1);
			$("#jqxTabs").jqxTabs('previous');
		});
		
		//Validate createNewRecrRoundWindow
		$("#createNewRecrRoundWindow").jqxValidator({
			rules: [
				{input: '#rrWorkEffortName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
				{input: '#rrEstimatedStartDate', message: '${uiLabelMap.LTDateFieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if(input.jqxDateTimeInput('getDate') > $("#rrEstimatedCompletionDate").jqxDateTimeInput('getDate') && $("#rrEstimatedCompletionDate").jqxDateTimeInput('getDate')){
	                    	return false;
	                    }else{
	                    	return true;
	                    }
	                    	
                	}
				},
				{input: '#rrEstimatedStartDate', message: '${uiLabelMap.BetweenStartEndDateFieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if(input.jqxDateTimeInput('getDate') <= $("#estimatedCompletionDate").jqxDateTimeInput('getDate') && $("#estimatedStartDate").jqxDateTimeInput('getDate') <= input.jqxDateTimeInput('getDate')){
	                    	return true;
	                    }else{
	                    	return false;
	                    }
	                    	
                	}
				},
				{input: '#rrEstimatedCompletionDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if(input.val()){
	                    	return true;
	                    }else{
	                    	return false;
	                    }
	                    	
                	}
				},
				{input: '#rrEstimatedCompletionDate', message: '${uiLabelMap.GTDateFieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if(input.jqxDateTimeInput('getDate') < $("#rrEstimatedStartDate").jqxDateTimeInput('getDate')){
	                    	return false;
	                    }else{
	                    	return true;
	                    }
	                    	
                	}
				},
				{input: '#rrEstimatedCompletionDate', message: '${uiLabelMap.BetweenStartEndDateFieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if((input.jqxDateTimeInput('getDate') <= $("#estimatedCompletionDate").jqxDateTimeInput('getDate')) && ($("#estimatedStartDate").jqxDateTimeInput('getDate') <= input.jqxDateTimeInput('getDate'))){
	                    	return true;
	                    }else{
	                    	return false;
	                    }
	                    	
                	}
				},
				{input: '#rrLocation', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule:'required' }
			]
		});
		
		//Validate createNewRecrProcess
		$("#createNewRecrProcess").jqxValidator({
			rules: [
				{input: '#workEffortName', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required'},
				{input: '#jrJobRequestId', message: '${uiLabelMap.FieldRequired}', action: 'keyup, close', 
					rule: function (input, commit) {
	                    if(input.val()){
	                    	return true;
	                    }else{
	                    	return false;
	                    }
	                    	
                	}
				},
				{input: '#estimatedStartDate', message: '${uiLabelMap.LTDateFieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if(input.jqxDateTimeInput('getDate') > $("#estimatedCompletionDate").jqxDateTimeInput('getDate') && $("#estimatedCompletionDate").jqxDateTimeInput('getDate')){
	                    	return false;
	                    }else{
	                    	return true;
	                    }
	                    	
                	}
				},
				{input: '#estimatedCompletionDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup, close', 
					rule: function (input, commit) {
	                    if(input.val()){
	                    	return true;
	                    }else{
	                    	return false;
	                    }
	                    	
                	}
				},
				{input: '#estimatedCompletionDate', message: '${uiLabelMap.GTDateFieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if(input.jqxDateTimeInput('getDate') < $("#estimatedStartDate").jqxDateTimeInput('getDate')){
	                    	return false;
	                    }else{
	                    	return true;
	                    }
	                    	
                	}
				},
				/*{input: '#actualStartDate', message: '${uiLabelMap.LTDateFieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if(input.jqxDateTimeInput('getDate') > $("#actualCompletionDate").jqxDateTimeInput('getDate') && $("#actualCompletionDate").jqxDateTimeInput('getDate')){
	                    	return false;
	                    }else{
	                    	return true;
	                    }
	                    	
                	}
				},
				{input: '#actualCompletionDate', message: '${uiLabelMap.FieldRequired}', action: 'keyup,change, close', 
					rule: function (input, commit) {
	                    if(input.val()){
	                    	return true;
	                    }else{
	                    	return false;
	                    }
	                    	
                	}
				},
				{input: '#actualCompletionDate', message: '${uiLabelMap.GTDateFieldRequired}', action: 'keyup, change, close', 
					rule: function (input, commit) {
	                    if(input.jqxDateTimeInput('getDate') < $("#actualStartDate").jqxDateTimeInput('getDate')){
	                    	return false;
	                    }else{
	                    	return true;
	                    }
	                    	
                	}
				},*/
				{input: '#resourceNumber', message: '${uiLabelMap.NumberFieldRequired}', action: 'keyup, blur',
					rule: function (input, commit) {
	                    if (isFinite(input.val())) {
	                        return true;
	                    }
	                    return false;
	                }
				}
			]
		});
		
		//Handle click submit
		$("#submit").click(function(){
			var newRRData = {};
			newRRData['workEffortName'] = $("#workEffortName").val();
			newRRData['estimatedStartDate'] = $("#estimatedStartDate").jqxDateTimeInput('getDate').getTime();
			newRRData['estimatedCompletionDate'] = $("#estimatedCompletionDate").jqxDateTimeInput('getDate').getTime();
			/*newRRData['actualStartDate'] = $("#actualStartDate").jqxDateTimeInput('getDate').getTime();*/
			/*newRRData['actualCompletionDate'] = $("#actualCompletionDate").jqxDateTimeInput('getDate').getTime();*/
			newRRData['description'] = $("#description").val();
			newRRData['resourceNumber'] = $("#resourceNumber").val();
			newRRData['workLocation'] = $("#workLocation").val();
			newRRData['recruitmentTypeId'] = $("#recruitmentTypeId").val();
			newRRData['recruitmentFormId'] = $("#recruitmentFormId").val();
			newRRData['emplPositionTypeId'] = selectedEmplPositionTypeId;
			newRRData['jobRequestId'] = $("#jrJobRequestId").val();
			newRRData['partyId'] = selectedPartyId;
			newRRData['ctPartyId'] = selectedCtPartyId;
			newRRData['ctEmplPositionTypeId'] = $("#ctEmplPositionTypeId").val();
			newRRData['ctMobile'] = $("#ctMobile").val();
			newRRData['ctEmail'] = $("#ctEmail").val();
			newRRData['listRounds'] = JSON.stringify(rrData);
			//Sent request create applicant
			$.ajax({
				url: 'createRecruitmentProcess',
				type: "POST",
				data: newRRData,
				dataType: 'json',
				async: false,
				success : function(data) {
					if(data.responseMessage == 'error'){
						bootbox.confirm("Tạo mới đợt tuyển dụng không thành công", function(result) {
							return;
						});
					}else{
						$("#alterpopupNewRecrProcess").jqxWindow('close');
						$("#jqxgrid").jqxGrid('updatebounddata');
					}
				}
			});
		});
	});
	var initrowdetails = function (index, parentElement, gridElement, datarecord) {
		var detail = $($(parentElement).children()[0]);
		var jqxGridRoundId = "jqxGridRoundId" + datarecord.uid;
		var workEffortId = datarecord.workEffortId;
		if(detail){
			detail.attr("id", jqxGridRoundId);
			var dataSource = {
    				datafields: [
    								{name: 'workEffortId', type: 'string'},
    						        {name: 'workEffortName', type: 'string'},
    							],
    					cache: false,
    					datatype: 'json',
    					type: 'POST',
    					data: {workEffortId: workEffortId},
    					url: "getListRecruitmentRound"
    				};
    	 
			detail.jqxGrid({
                width: 850,
                source: dataSource,                
                pageable: true,
                autoheight: true,
                editable: false,
                columns: [
                  { text: '${uiLabelMap.CommonId}', datafield: 'workEffortId', width: 250,
                	  cellsrenderer: function(column, row, value){
		             		return '<span><a href=FindApplRecruitmentProcess?workEffortId=' + value + '>' + value + '</a></span>';
	             	}
                  },
                  { text: '${uiLabelMap.roundName}', datafield: 'workEffortName'}
                ]
            });
		}
    }
</script>