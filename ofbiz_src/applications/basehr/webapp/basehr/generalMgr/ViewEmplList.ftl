<#include "script/ViewEmplListScript.ftl"/>
<style>
	.highlightCell{
		background-color: aqua !important;
		
	}
	.redCell{
		background-color: red !important;
	}
	.aquamarineCell{
		background-color: aquamarineCell !important;
	}
</style>
<#assign datafield = "[{name: 'partyId', type: 'string'},
						{name: 'partyCode', type: 'string'},
					   {name: 'firstName', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'gender', type: 'string'},					   
					   {name: 'birthDate', type: 'date'},
					   {name: 'emplPositionType', type: 'string'},
					   {name: 'partyGroupId', type: 'string'},
					   {name: 'department', type: 'string'},
					   {name: 'dateJoinCompany', type: 'date'},
					   {name: 'workingStatusId', type: 'string'},
					   {name: 'terminationReasonId', type: 'string'},
					   {name: 'dateResign', type: 'date'},
					   {name: 'agreementDate', type: 'date'},
					   {name: 'agreementTypeId', type: 'string'},
					   {name: 'probationaryDeadline', type: 'date'},
					   {name: 'agreementThruDate', type: 'date'},
					   {name: 'birthPlace', type: 'string'},
					   {name: 'seniorityMonth', type: 'number'},
					   ]"/>
<script type="text/javascript">
	
	<#assign columnlist = "
						   {text: '${uiLabelMap.EmployeeId}', datafield: 'partyCode', width: '12%', pinned: true},
						   {text: '${uiLabelMap.EmployeeName}', datafield: 'fullName', width: '18%', pinned: true,
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								   var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
								   if(rowData){
									   return '<span>' + rowData.fullName + '</span>';
								   }
							   }
						   },
						   {text: '${uiLabelMap.PartyGender}', datafield: 'gender', width: '10%', filtertype: 'checkedlist',
							   columntype: 'dropdownlist',
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								   for(var i = 0; i < genderArr.length; i++){
									   if(genderArr[i].genderId == value){
										   return '<div style=\"margin-top: 4px; margin-left: 2px\">' + genderArr[i].description + '</div>'; 
									   }
								   }
								   return '<div style=\"margin-top: 4px; margin-left: 2px\">' +  value + '</div>';
							   },
							   createfilterwidget: function(column, columnElement, widget){
									var source = {
									        localdata: genderArr,
									        datatype: 'array'
									};		
									var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
								    var dataSoureList = filterBoxAdapter.records;
								    dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
								    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'genderId'});
								    if(dataSoureList.length < 8){
								    	widget.jqxDropDownList({autoDropDownHeight: true});
								    }else{
								    	widget.jqxDropDownList({autoDropDownHeight: false});
								    }
								}
						   },
						   {text: '${uiLabelMap.HrCommonPosition}', datafield: 'emplPositionType', width: '15%'},
						   {text: '${uiLabelMap.PartyIdWork}', datafield: 'department', width: '20%'},
						   {text: '${uiLabelMap.PartyBirthDate}', datafield: 'birthDate', width: '10%', cellsformat: 'dd/MM/yyyy', 
							   columntype: 'datetimeinput', filtertype: 'range'},
						   {text: '${uiLabelMap.DateJoinCompany}', datafield: 'dateJoinCompany', width: '11%', 
								   cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput', filtertype: 'range'},
						   {text: '${uiLabelMap.HRCommonCurrStatus}', datafield: 'workingStatusId', width: '15%',
							   filtertype: 'checkedlist', columntype: 'dropdownlist', 
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								   for(var i = 0; i < globalVar.statusWorkingArr.length; i++){
									   if(globalVar.statusWorkingArr[i].statusId == value){
										   return '<div style=\"margin-top: 4px; margin-left: 2px\">' + globalVar.statusWorkingArr[i].description + '</div>'; 
									   }
								   }
								   return '<div style=\"margin-top: 4px; margin-left: 2px\">' +  value + '</div>';
							   },
							   createfilterwidget: function(column, columnElement, widget){
									var source = {
									        localdata: globalVar.statusWorkingArr,
									        datatype: 'array'
									};		
									var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
								    var dataSoureList = filterBoxAdapter.records;
								    dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
								    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'statusId'});
								    if(dataSoureList.length < 8){
								    	widget.jqxDropDownList({autoDropDownHeight: true});
								    }else{
								    	widget.jqxDropDownList({autoDropDownHeight: false});
								    }
								}   
						   },
						   {text: '${uiLabelMap.AgreementDate}', datafield: 'agreementDate', width: '11%', filtertype: 'range', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput'},
						   {text: '${uiLabelMap.HREmplResignDate}', datafield: 'dateResign', width: '11%', filtertype: 'range', cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput'},
						   {text: '${uiLabelMap.HREmplReasonResign}', datafield: 'terminationReasonId', width: '13%', filtertype: 'checkedlist', columntype: 'dropdownlist',
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								   for(var i = 0; i < globalVar.terminationReasonArr.length; i++){
									   if(value == globalVar.terminationReasonArr[i].terminationReasonId){
										   return '<span>' + globalVar.terminationReasonArr[i].description + '</span>';
									   }
								   }
								   return '<span>' + value + '</span>';
							   },
							   createfilterwidget: function(column, columnElement, widget){
									var source = {
									        localdata: globalVar.terminationReasonArr,
									        datatype: 'array'
									};		
									var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
								    var dataSoureList = filterBoxAdapter.records;
								    dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
								    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'terminationReasonId'});
								    if(dataSoureList.length < 8){
								    	widget.jqxDropDownList({autoDropDownHeight: true});
								    }else{
								    	widget.jqxDropDownList({autoDropDownHeight: false});
								    }
								} 
						   },
						   {text: '${uiLabelMap.HRCommonSeniority} (${StringUtil.wrapString(uiLabelMap.HRCommonMonthLowercase)})', datafield: 'seniorityMonth', width: '10%', columntype: 'numbertimeinput', filtertype: 'number', cellsalign: 'right'},
						   {text: '${uiLabelMap.agreementTypeId}', datafield: 'agreementTypeId', width: '16%', filtertype: 'checkedlist', columntype: 'dropdownlist',
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								   for(var i = 0; i < globalVar.agreementTypeArr.length; i++){
									   if(value == globalVar.agreementTypeArr[i].agreementTypeId){
										   return '<span>' + globalVar.agreementTypeArr[i].description + '</span>';
									   }
								   }
								   return '<span>' + value + '</span>';
							   },
							   createfilterwidget: function(column, columnElement, widget){
									var source = {
									        localdata: globalVar.agreementTypeArr,
									        datatype: 'array'
									};		
									var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
								    var dataSoureList = filterBoxAdapter.records;
								    dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
								    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'agreementTypeId'});
								    if(dataSoureList.length < 8){
								    	widget.jqxDropDownList({autoDropDownHeight: true});
								    }else{
								    	widget.jqxDropDownList({autoDropDownHeight: false});
								    }
								} 
						   },
						   {text: '${uiLabelMap.ProbationDuration}', datafield: 'probationaryDeadline', width: '11%', cellsformat: 'dd/MM/yyyy', 
							   columntype: 'datetimeinput', filtertype: 'range'},
						   {text: '${uiLabelMap.AgreemntThruDate}', datafield: 'agreementThruDate', width: '14%', cellsformat: 'dd/MM/yyyy', 
								   columntype: 'datetimeinput', filtertype: 'range'}
						   "/>
    
</script>		

<div id="appendNotification" class="container-noti">
</div>
<div id="updateNotification" >
	<span id="notificationText"></span>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.HREmplList}</h4>
		<div class="widget-toolbar none-content pull-right">
			<button id="clearFilterBtn" class="pull-right grid-action-button fa-filter open-sans">${uiLabelMap.accRemoveFilter}</button>
			<#if security.hasEntityPermission("HR_DIRECTORY", "_ADMIN", session)>
			<button id="addNewEmployee" class="pull-right grid-action-button icon-plus open-sans">${uiLabelMap.accAddNewRow}</button>
			</#if>
            <button id="exportExcelEmplList" class="pull-right grid-action-button icon-file-excel-o open-sans">${uiLabelMap.BSExportExcel}</button>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div id="dateTimeInput"></div>						
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>						
						<div class="span12" style="margin-right: 15px">
							<div id="jqxDropDownButton" class="pull-right">
								<div style="border: none;" id="jqxTree">
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid url="" dataField=datafield columnlist=columnlist
				clearfilteringbutton="true" id="jqxgrid" 
				editable="false" width="100%" filterable="true"
				initrowdetails="true" initrowdetailsDetail=rowDetails rowdetailsheight="250" rowdetailstemplateAdvance=rowdetailstemplateAdvance
				showtoolbar="false" deleterow="false" jqGridMinimumLibEnable="false" mouseRightMenu="true" contextMenuId="contextMenu" isSaveFormData="true" formData="filterObjData"
				/>			
		</div>
	</div>
</div>

<div id='contextMenu' class="hide">
	<ul>
		<#--<!-- <li action="viewDetail">
			<i class="icon-eye-open"></i>${uiLabelMap.ViewDetails}
        </li>  -->       
		<li action="editEmplInfo">
			<i class="icon-wrench"></i>${uiLabelMap.EmployeeWorkInformation}
        </li>        
		<li action="positionFulfillmentList">
			<i class="fa-list-ol"></i>${uiLabelMap.EmplPositionFulfillmentList}
        </li>        
		<li action="editPersonInfoContact">
			<i class="icon-user"></i>${uiLabelMap.EmployeeInfo}
        </li>        
		<li action="editFamilyInfo">
			<i class="fa-home"></i>${uiLabelMap.HREmployeeFamilyInfo}
        </li>        
		<li action="changeJobPosition">
			<i class="fa-exchange"></i>${uiLabelMap.ChangingJobPosition}
        </li>        
		<li action="resetPassword">
			<i class="fa-refresh"></i>${uiLabelMap.SettingResetPassword}
        </li>
        <li action="infoAccount">
			<i class="fa fa-info-circle"></i>${uiLabelMap.HRInfoAccountEmployee}
        </li>        
	</ul>
</div>
<div id="editEmplInfoWindow" class="hide">
	<div>${uiLabelMap.UpdateEmployeeInfo}</div>
	<div class='form-window-container'>
		<div class='form-window-content' >
			<div class="row-fluid" style="position: relative;">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.EmployeeId}</label>
							</div>
							<div class="span7">
								<input type="text" id="partyIdToUpdate">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.PartyIdWork}</label>
							</div>
							<div class="span7">
								<input type="text" id="partyIdFromUpdate">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HrCommonPosition}</label>
							</div>
							<div class="span7">
								<input type="text" id="emplPositionTypeIdUpdate">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.DateJoinCompany}</label>
							</div>
							<div class="span7">
								<div id="dateJoinCompanyUpdate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HREmplReasonResign}</label>
							</div>
							<div class="span7">
								<div id="reasonResignUpdate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.SocialInsuranceNbr}</label>
							</div>
							<div class="span7">
								<input type="text" id="insuranceSocialNbrUpdate">
							</div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.EmployeeName}</label>
							</div>
							<div class="span7">
								<input type="text" id="employeeNameUpdate">
							</div>
						</div>
						
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.SalaryBaseFlat}</label>
							</div>
							<div class="span7">
								<div class="row-fluid">
									<div id="salaryBaseFlatUpdate" style="display: inline-block;"></div>
									<div id="periodTypeSalUpdate" style="display: inline-block;"></div>
								</div>
							</div>
						</div>
						
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HRCommonCurrStatus}</label>
							</div>
							<div class="span7">
								<div id="statusIdUpdate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.ProbationaryDeadline}</label>
							</div>
							<div class="span7">
								<div id="probationaryDeadline"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.HREmplResignDate}</label>
							</div>
							<div class="span7">
								<div id="resignDateUpdate"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class="span5 text-algin-right">
								<label class="">${uiLabelMap.ParticipateFrom}</label>
							</div>
							<div class="span7">
								<div id="insParticipateFromUpdate"></div>
							</div>
						</div>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="ajaxLoading_editEmplInfo" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinner-ajax_editEmplInfo"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelUpdateEmpl" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<#if security.hasEntityPermission("HR_DIRECTORY", "_UPDATE", session)>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="saveUpdateEmpl">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</#if>
		</div>
	</div>
</div>
<#assign defaultSuffix = "addNew"/>
<#assign includeJs = "false"/>
${setContextField("defaultSuffix", defaultSuffix)}
${setContextField("includeJs", "false")}
<#include "component://basehr/webapp/basehr/insurance/script/HosipitalListScript.ftl"/>
<div class="row-fluid" style="position: relative;">
	<div id="addNewEmployeeWindow" class="hide">
		<div>${StringUtil.wrapString(uiLabelMap.AddNewEmployee)}</div>
		<div class='form-window-container'>
			${screens.render("component://basehr/widget/GeneralManagerScreens.xml#AddNewEmployee")}
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoading" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinner-ajax"></div>
				</div>
			</div>
		</div>
	</div>
</div>
<#--<!-- <#include "ViewEmplDetail.ftl"/> -->

<#assign editEmplWindow = "editEmplInfoContactWindow"/>
<#include "EditEmployeeInfo.ftl"/>
<#include "component://basehr/webapp/basehr/generalMgr/script/AddNewEmployeeScript.ftl"/>
<#include "EditEmployeeFamilyInfo.ftl"/>
<#include "EditJobPosition.ftl"/>
<#include "AddNewEmployeeSetPosTypeForOrg.ftl"/>
<#include "ResetPassword.ftl"/>
<#include "EmplPositionFulfillmentList.ftl"/>
<#include "InfoAccountEmpl.ftl"/>
<script type="text/javascript" src="/hrresources/js/generalMgr/AddNewEmployeeProfileInfo.js"></script>
<script type="text/javascript" src="/hrresources/js/generalMgr/AddNewEmployeeContactInfo.js"></script>
<script type="text/javascript" src="/hrresources/js/generalMgr/AddNewEmployee.js?v=0.0.1"></script>
<script type="text/javascript" src="/hrresources/js/generalMgr/ViewEmplList.js?v=0.0.4"></script>
<script type="text/javascript" src="/hrresources/js/insurance/HosipitalListScript.js"></script>
