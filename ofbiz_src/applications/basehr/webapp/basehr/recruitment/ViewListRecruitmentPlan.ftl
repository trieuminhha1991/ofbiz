<style>
 #popoverAddNew .jqx-popover-content{
 	padding: 0 5px 3px
 }
</style>


<#include "script/ViewListRecruitmentPlanScript.ftl"/>
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<#assign datafield = "[{name: 'recruitmentPlanId', type: 'string'},
						{name: 'recruitmentRequireId', type: 'string'},
						{name: 'recruitmentPlanName', type: 'string'},
						{name: 'partyId', type: 'string'},
						{name: 'groupName', type: 'string'},
						{name: 'quantity', type: 'number'},
						{name: 'recruitmentFormTypeId', type: 'string'},
						{name: 'statusId', type: 'string'},
						{name: 'month', type: 'number'},
						{name: 'year', type: 'number'},
						{name: 'emplPositionTypeId', type: 'string'},
						{name: 'salaryAmount', type: 'number'},
						{name: 'estimatedCost', type: 'number'},
						{name: 'requirementDesc', type: 'number'},
						{name: 'recruitmentFromDate', type: 'date'},
						{name: 'recruitmentThruDate', type: 'date'},
						{name: 'applyFromDate', type: 'date'},
						{name: 'applyThruDate', type: 'date'},
						{name: 'isCreatedRecruitRequire', type: 'bool'},
						]"/>
						
<script type="text/javascript">
<#assign columnlist = "
						{text: '${StringUtil.wrapString(uiLabelMap.RecruitmentPlanName)}', datafield: 'recruitmentPlanName', 
							width: '20%', editable: false, filtertype: 'input'},
						{text: '${StringUtil.wrapString(uiLabelMap.RecruitingPosition)}', datafield: 'emplPositionTypeId', editable: false, width: '20%',
							columntype: 'dropdownlist', filtertype: 'checkedlist',
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.emplPositionTypeArr.length; i++){
									if(value == globalVar.emplPositionTypeArr[i].emplPositionTypeId){
										return '<span>' + globalVar.emplPositionTypeArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.emplPositionTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'emplPositionTypeId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', editable: false, width: '15%', filterable: true},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonQty)}', datafield: 'quantity', editable: false, width: '10%', 
							columntype: 'numberinput', filtertype: 'number', cellsalign: 'right'},
						{text: '${StringUtil.wrapString(uiLabelMap.TimeRecruiting)}', datafield: 'month', editable: false, width: '14%', filterable: false,
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								if(data){
									return '<span>${StringUtil.wrapString(uiLabelMap.CommonMonth)} ' + (value + 1) + '/' + data.year  +'</span>';
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CreatedByRecruitmentRequirement)}', datafield: 'isCreatedRecruitRequire', columntype: 'checkbox', 
							editable: false, width: '15%', filterable: false},
						
						{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', editable: false, columntype: 'dropdownlist', width: '12%',
							filtertype: 'checkedlist',
							cellsrenderer: function (row, column, value) {
								for(i = 0; i < globalVar.statusArr.length; i++){
									if(value == globalVar.statusArr[i].statusId){
										return '<span>' + globalVar.statusArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.statusArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'statusId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
						}
						"/>
</script>		
<div id="containerNtf" style="background-color: transparent; overflow: auto; width: 100%;">
</div>
<div id="jqxNotificationNtf">
    <div id="notificationContentNtf">
    </div>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.RecruitmentPlanList}</h4>
		<div class="widget-toolbar none-content" >
			<#if security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", session)>
				<button id="addNewSelection" class="grid-action-button" style="font-size: 14px"><i class="icon-plus open-sans"></i><span>${uiLabelMap.CommonAddNew}</span></button>
			</#if>
			<button id="clearFilter" class="grid-action-button" style="font-size: 14px"><i class="icon-filter open-sans"></i><span>${uiLabelMap.accRemoveFilter}</span></button>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span12">
							<div id="year"></div>
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
			<#assign mouseRightMenu = "true"/>
			<#assign contextMenuId = "contextMenu"/>
			<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="false" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="false" addType="popup" editable="false" deleterow="false" selectionmode="singlerow"
				alternativeAddPopup="alterpopupWindow" addrow="true" showlist="false" sortable="true"  mouseRightMenu=mouseRightMenu
				contextMenuId=contextMenuId url="" jqGridMinimumLibEnable="false"/>
		</div>
	</div>
</div>		
<div id="contextMenu" class="hide">
	<ul>
		<li action="viewRecruitmentPlan">
			<i class="fa-search"></i>${uiLabelMap.RecruitmentPlanDetail}
		</li>
		<li action="viewListCandidates">
			<i class="fa-users"></i>${uiLabelMap.RecruitmentCandidatesList}
        </li>
		<li action="viewListRecruitRound">
			<i class="fa-info-circle"></i>${uiLabelMap.RecruitmentRoundList}
        </li>
		<li action="viewRecruitmentListCost">
			<i class="fa-money"></i>${uiLabelMap.RecruitmentCost}
        </li>
	</ul>
</div>
<#if security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", session)>
	<div id="popoverAddNew" class="hide">
		<div id="popoverContent">
			<button id="addHRRecPlanBaseRecruitReq" class="grid-action-button" style="font-size: 14px"><i class="fa fa-sign-in open-sans"></i><span>${uiLabelMap.AddNewBaseOnRecruitmentRequire}</span></button>
			<button id="addNewHRRecPlan" class="grid-action-button" style="font-size: 14px; color: red !important"><i class="fa fa-sign-out open-sans"></i><span>${uiLabelMap.AddNewNotBaseOnRecruitmentRequire}</span></button>
		</div>
	</div>
	<#include "RecruitCreateRecruitmentPlan.ftl"/>		
</#if>
<#include "RecruitListCandidates.ftl"/>
<#include "RecruitmentPlanDetail.ftl"/>
<#include "RecruitListRound.ftl"/>		

<#if security.hasEntityPermission("HR_RECRUITMENT", "_ADMIN", session)>
	<#include "RecruitmentCostUtils.ftl"/>
</#if>
<script type="text/javascript" src="/hrresources/js/recruitment/ViewListRecruitmentPlan.js"></script>

<#include "RecruitmentViewCostItemList.ftl"/>	
