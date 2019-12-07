<style type="text/css">
.aquaCell{
	background-color: aqua !important;
}
.greenyellowCell{
	background-color: greenyellow !important;
}
.redCell{
	background-color: red !important;
}
.grayCell{
	background-color: gray !important;
	color: seashell !important; 
}
</style>
<#include "script/ViewListHRPlanningScript.ftl"/>
<#assign datafield = "[{name: 'emplPositionTypeId', type: 'string'},"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', width: '16%', datafield: 'emplPositionTypeId', editable: false,
						cellsrenderer: function (row, column, value) {
							for(var i = 0; i < globalVar.emplPositionTypeArr.length; i++){
								if(value == globalVar.emplPositionTypeArr[i].emplPositionTypeId){
									return '<span>' + globalVar.emplPositionTypeArr[i].description + '</span>';
								}
							}
							return '<span>' + value + '</span>';
						}
						},"/>
<#list 0..11 as x>
	<#assign datafield = datafield + "{name: 'quantity_" + x + "', type: 'number'},"/>
	<#assign datafield = datafield + "{name: 'statusId_" + x + "', type: 'string'},"/>
	<#assign datafield = datafield + "{name: 'customTimePeriodId_" + x + "', type: 'string'},"/>
	<#assign datafield = datafield + "{name: 'comment_" + x + "', type: 'string'},"/>
	
	<#assign columnlist = columnlist + "{text: globalVar.monthNames[" + x +"], datafield: 'quantity_" + x +"', width: '7%', columntype: 'numberinput',
											cellsrenderer: function (row, column, value) {
												var className = '';
												var data = $('#jqxgrid').jqxGrid('getrowdata', row);
												if(data){
													var statusId = data['statusId_" + x + "'];
													if(statusId == 'HR_PLANNING_WAIT'){
														className = 'greenyellowCell';
													}else if(statusId == 'HR_PLANNING_ACC'){
														className = 'aquaCell';
													}else if(statusId == 'HR_PLANNING_REJ'){
														className = 'redCell';
													}else if(statusId == 'HR_PLANNING_CAN'){
														className = 'grayCell';
													}
												}
												
												return '<span id=' + id + ' class=' + className + '>' + value + '</span>';
											},
											
										}," />
	<#assign columnlist = columnlist + "{datafield: 'statusId_" + x +"', hidden: true}," />
	<#assign columnlist = columnlist + "{datafield: 'customTimePeriodId_" + x +"', hidden: true}," />
	<#assign columnlist = columnlist + "{datafield: 'comment_" + x +"', hidden: true}," />
</#list>
<#assign datafield = datafield + "]"/> 	
<div id="containerNtf" style="background-color: transparent; overflow: auto; width: 100%;">
</div>
<div id="jqxNotificationNtf">
    <div id="notificationContentNtf">
    </div>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header" style="min-height: 0">
		<h4>${uiLabelMap.HRPlanning}</h4>
		<div class="widget-toolbar none-content" >
			<button id="addNewHRPlanBtn" class="grid-action-button" style="font-size: 14px"><i class="icon-plus open-sans"></i><span>${uiLabelMap.CommonAddNew}</span></button>
			<div style="float:right;margin-left:20px;margin-top: 4px; font-size: 14px; font-weight: normal;">
				<span style="width: 16px">${uiLabelMap.Year}</span>
				<div style="float:right;margin-left: 4px" id="yearCustomTimePeriod"></div>
			</div>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span12">
							<div class="span6">
								<div id="emplPositionTypeDropDown"></div>
							</div>
							<div class="span6">
								<button class="grid-action-button open-sans" id="clearFilter"><i class="icon-filter open-sans"></i>${uiLabelMap.accRemoveFilter}</button>
							</div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10 '>
						<div class="span12" style="margin-right: 10px">
							<div id="dropDownButton" class="pull-right">
								<div style="border: none;" id="jqxTree">
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<#if security.hasEntityPermission("HR_RECRUITMENT", "_UPDATE", session)>
				<#assign mouseRightMenu = "true"/>
				<#assign contextMenuId = "contextMenu"/>
			<#else>
				<#assign mouseRightMenu = "false"/>
				<#assign contextMenuId = ""/>	
			</#if>
		
			<@jqGrid filtersimplemode="false" filterable="false" addrefresh="true" showtoolbar="false" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="false" addType="popup" editable="false" deleterow="false" selectionmode="singlecell"
				alternativeAddPopup="alterpopupWindow" addrow="true" showlist="false" sortable="false" mouseRightMenu=mouseRightMenu 
				contextMenuId=contextMenuId url="" jqGridMinimumLibEnable="false"/>
		</div>
	</div>
</div>				
<div id="addHRPlaningWindow" class="hide">
	<div>${uiLabelMap.AddHRPlanning}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="position: relative;">
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.OrganizationalUnit}</label>
							</div>
							<div class='span8'>
								<div id="dropDownButtonAddNew" class="pull-right">
									<div style="border: none;" id="jqxTreeAddNew">
									</div>
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
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="asterisk">${uiLabelMap.Year}</label>
							</div>
							<div class="span8">
								<div id="yearCustomTimeNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="asterisk">${uiLabelMap.HRCommonApprover}</label>
							</div>
							<div class="span8">
								<div id="approverListDropDownBtn">
									 <div style="border-color: transparent;" id="jqxGridApprover">
           							 </div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12 boder-all-profile" style="padding: 15px 10px 0 0">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonJanuary}</label>
							</div>
							<div class="span8">
								<input type="number" id="month1" min="0">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonMarch}</label>
							</div>
							<div class="span8">
								<input type="number" id="month3" min="0">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonMay}</label>
							</div>
							<div class="span8">
								<input type="number" id="month5" min="0">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonJuly}</label>
							</div>
							<div class="span8">
								<input type="number" id="month7" min="0">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonSeptember}</label>
							</div>
							<div class="span8">
								<input type="number" id="month9" min="0">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonNovember}</label>
							</div>
							<div class="span8">
								<input type="number" id="month11" min="0">
							</div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonFebruary}</label>
							</div>
							<div class="span8">
								<input type="number" id="month2" min="0">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonApril}</label>
							</div>
							<div class="span8">
								<input type="number" id="month4" min="0">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonJune}</label>
							</div>
							<div class="span8">
								<input type="number" id="month6" min="0">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonAugust}</label>
							</div>
							<div class="span8">
								<input type="number" id="month8" min="0">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonOctober}</label>
							</div>
							<div class="span8">
								<input type="number" id="month10" min="0">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span4 align-right'>
								<label class="">${uiLabelMap.CommonDecember}</label>
							</div>
							<div class="span8">
								<input type="number" id="month12" min="0">
							</div>
						</div>
						
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoadingAddNew" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAjaxAddNew"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
					<button type="button" class="btn btn-success form-action-button pull-right" id="saveAndContinue"><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
				</div>
			</div>
		</div>
	</div>	
</div>
   
<#if security.hasEntityPermission("HR_RECRUITMENT", "_UPDATE", session)>
	<div id='contextMenu' class="hide">
		<ul>
			<li action="edit">
				<i class="fa fa-edit"></i>${uiLabelMap.CommonEdit}
	        </li>
			<#if security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", session)>
			<li action="approver">
				<i class="fa fa-pencil"></i>${uiLabelMap.HRApprove}
	        </li>
	        </#if>
		</ul>
	</div>
	<#if security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", session)>
		<#include "RecruitmentApproveHRPlanning.ftl"/>
	</#if>
	<#include "RecruitmentEditHRPlanning.ftl"/>
</#if>
<script type="text/javascript" src="/hrresources/js/recruitment/ViewListHRPlanning.js"></script>
