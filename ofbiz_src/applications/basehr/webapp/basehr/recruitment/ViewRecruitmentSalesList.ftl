<#include "script/ViewRecruitmentSalesListScript.ftl"/>
<#assign datafield = "[{name: 'recruitmentPlanSalesId', type: 'string'},
						{name: 'recruitmentPlanSalesName', type: 'string'},
						{name: 'customTimePeriodId', type: 'string'},
						{name: 'statusId', type: 'string'},
						{name: 'partyId', type: 'string'},
						{name: 'totalEmployee', type: 'number'},
						{name: 'groupName', type: 'string'},
						{name: 'periodName', type: 'string'}
					   ]"/>
					   
<script type="text/javascript">
<#assign columnlist = "{datafield: 'recruitmentPlanSalesId', hidden: true},
						{datafield: 'customTimePeriodId', hidden: true},	
						{datafield: 'partyId', hidden: true},
						{text: '${StringUtil.wrapString(uiLabelMap.RecruitmentPlan)}', datafield: 'recruitmentPlanSalesName', width: '22%', filtertype: 'input'},
						{text: '${StringUtil.wrapString(uiLabelMap.TimeRecruitmentPlan)}', datafield: 'periodName', width: '22%', filterable: false, editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: '22%', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', width: '22%', columntype: 'dropdownlist',
							filtertype: 'checkedlist', editable: false,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.statusArr.length; i++){
									if(globalVar.statusArr[i].statusId == value){
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
						{text: '${StringUtil.wrapString(uiLabelMap.NumberOfRecruited)}', datafield: 'totalEmployee', width: '12%', cellsalign: 'right',
							columntype: 'numberinput', filtertype: 'number', editable: false}
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
		<h4>${uiLabelMap.RecruitmentSalesList}</h4>
		<div class="widget-toolbar none-content" >
			<#if security.hasEntityPermission("RECRUITSALESPLAN", "_CREATE", session)>
				<button id="addNewRecEmpl" class="grid-action-button" style="font-size: 14px"><i class="icon-plus open-sans"></i><span>${uiLabelMap.CommonAddNew}</span></button>
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
							<div id="yearCustomTimePeriod"></div>
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
		<li action="addNewEmployee">
			<i class="fa fa-user-plus"></i>${uiLabelMap.AddNewEmployee}
        </li>
		<li action="viewListEmpl">
			<i class="fa fa-users"></i>${uiLabelMap.RecruitedList}
        </li>
		<li action="recruitmentOffer">
			<i class="fa fa-arrow-circle-up"></i>${uiLabelMap.RecruitmentOffer}
        </li>
	</ul>
</div>
<#assign defaultSuffix = "addNewEmpl"/>
<#include "RecruitmentSalesAddNewEmpl.ftl"/>
<#include "RecruitmentSalesListEmpl.ftl"/>
<#include "RecruitmentSalesOffer.ftl"/>
<script type="text/javascript" src="/hrresources/js/recruitment/ViewRecruitmentSalesList.js"></script>