<style>
.marginBottom20{
	margin-bottom: 20px;
}

.marginOnlyLeft10{
	margin: 0 0 0 10px;
}

.marginTop20{
	margin-top: 20px
}
</style>
<#include "script/ViewListRecruitmentRequireScript.ftl"/>
<#assign datafield = "[{name: 'recruitmentRequireId', type: 'string'},
					   {name: 'month', type: 'number'},
					   {name: 'year', type: 'number'},
					   {name: 'emplPositionTypeId', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'groupName', type: 'string'},
					   {name: 'quantity', type: 'number'},
					   {name: 'quantityAppr', type: 'number'},
					   {name: 'quantityUnplanned', type: 'number'},
					   {name: 'recruitmentFormTypeId', type: 'string'},
					   {name: 'statusId', type: 'string'},
					   {name: 'comment', type: 'string'},
					   {name: 'createdByPartyId', type: 'string'},
					   {name: 'approvedPartyId', type: 'string'},
					   {name: 'fullName', type: 'string'},
					   {name: 'recruitAnticipatePlanCreated', type: 'bool'},
					   {name: 'enumRecruitReqTypeId', type: 'string'},
					   ]"/>
					   
<script type="text/javascript">
<#assign columnlist = "
						{text: '${StringUtil.wrapString(uiLabelMap.RecruitingPosition)}', datafield: 'emplPositionTypeId', editable: false, width: '18%',
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
						{text: '${StringUtil.wrapString(uiLabelMap.TimeRecruiting)}', datafield: 'month', editable: false, width: '14%', filterable: false,
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								if(data){
									return '<span>${StringUtil.wrapString(uiLabelMap.CommonMonth)} ' + (value + 1) + '/' + data.year  +'</span>';
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', editable: false, width: '18%', filterable: true},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', width: '13%', editable: false, 
							columntype: 'dropdownlist', filtertype: 'checkedlist',
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
						},
						{text: '${StringUtil.wrapString(uiLabelMap.RecruitmentEnumType)}', datafield: 'enumRecruitReqTypeId', editable: false, width: '15%', 
							columntype: 'dropdownlist', filtertype: 'checkedlist',
							cellsrenderer: function (row, column, value) {
								for(i = 0; i < globalVar.recruitReqEnumArr.length; i++){
									if(value == globalVar.recruitReqEnumArr[i].enumId){
										return '<span>' + globalVar.recruitReqEnumArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},	
							createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.recruitReqEnumArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'enumId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.RecruitmentQuantityApproved)}', datafield: 'quantityAppr', editable: false, width: '12%', 
							cellsalign: 'right', columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function (row, column, value) {
								var rowdata = $('#jqxgrid').jqxGrid('getrowdata', row);
								if(rowdata!=null)
								{
									if(rowdata.recruitAnticipatePlanCreated){
										return '<div style=\"text-align: right\">' + value + '</div>';
									}else{
										return '<div style=\"text-align: right\">_______</div>';
									}
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.PlannedRecruitmentShort)}', datafield: 'quantity', editable: false, width: '11%', 
							columntype: 'numberinput', filtertype: 'number', cellsalign: 'right',},
						{text: '${StringUtil.wrapString(uiLabelMap.QuantityUnplannedShort)}', datafield: 'quantityUnplanned', editable: false, width: '11%', 
								columntype: 'numberinput', filtertype: 'number', cellsalign: 'right',},
						{text: '${StringUtil.wrapString(uiLabelMap.RecruitmentFormType)}', datafield: 'recruitmentFormTypeId', width: '14%', editable: false, 
							columntype: 'dropdownlist', filtertype: 'checkedlist',
							cellsrenderer: function (row, column, value) {
								for(i = 0; i < globalVar.recruitmentFormTypeArr.length; i++){
									if(value == globalVar.recruitmentFormTypeArr[i].recruitmentFormTypeId){
										return '<span>' + globalVar.recruitmentFormTypeArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createfilterwidget: function(column, columnElement, widget){
								var source = {
								        localdata: globalVar.recruitmentFormTypeArr,
								        datatype: 'array'
								};		
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind: true});
							    var dataSoureList = filterBoxAdapter.records;
							    //dataSoureList.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', valueMember : 'recruitmentFormTypeId'});
							    if(dataSoureList.length > 8){
							    	widget.jqxDropDownList({autoDropDownHeight: false});
							    }else{
							    	widget.jqxDropDownList({autoDropDownHeight: true});
							    }
							}
						},
						
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
		<h4>${uiLabelMap.RecruitmentRequirement}</h4>
		<div class="widget-toolbar none-content" >
			<button id="addNewHRRecReq" class="grid-action-button" style="font-size: 14px"><i class="icon-plus open-sans"></i><span>${uiLabelMap.CommonAddNew}</span></button>
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
			<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="false" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="false" addType="popup" editable="false" deleterow="false" selectionmode="singlerow"
				alternativeAddPopup="alterpopupWindow" addrow="true" showlist="false" sortable="true" mouseRightMenu="true" 
			   contextMenuId="contextMenu" url="" jqGridMinimumLibEnable="false"/>
		</div>
	</div>
</div>

<div id='contextMenu' class="hide">
	<ul>
		<li action="approver">
			<i class="fa-pencil"></i>${uiLabelMap.HRApprove}
        </li>
		<li action="edit" id="editRecruitmentRequireMenu">
			<i class="icon-edit"></i>${uiLabelMap.HrCommonEdit}
        </li>
	</ul>
</div>
<#include "RecruitmentRequireApproval.ftl"/>
<#include "EditRecruitmentRequire.ftl"/>
<#include "AddRecruitmentRequire.ftl"/>
<#include "RecruitmentConditionHtml.ftl"/>

<#--<!-- <script type="text/javascript" src="/hrresources/js/recruitment/RecruitPreliminarySelConds.js"></script> -->
<script type="text/javascript" src="/hrresources/js/recruitment/RecruitmentCondition.js"></script>
<script type="text/javascript" src="/hrresources/js/recruitment/ViewListRecruitmentRequire.js"></script>