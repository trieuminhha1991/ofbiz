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
<#include "script/ViewRecruitmentAnticipateListScript.ftl"/>

<#assign datafield = "[{name: 'emplPositionTypeId', type: 'string'},
					   {name: 'emplPositionTypeDesc', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'recruitAnticipateId', type: 'string'},
					   {name: 'groupName', type: 'string'},
					   {name: 'createdByPartyId', type: 'string'},
					   {name: 'statusId', type: 'string'},"
/>
<#assign columnlist = "{datafield: 'emplPositionTypeId', hidden: true},
						{datafield: 'partyId', hidden: true},
						{datafield: 'createdByPartyId', hidden: true},
						{datafield: 'recruitAnticipateId', hidden: true},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', editable: false, width: '18%'},
						{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', width: '16%', datafield: 'emplPositionTypeDesc', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', width: '16%', datafield: 'statusId', editable: false,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < globalVar.statusArr.length; i++){
									if(value == globalVar.statusArr[i].statusId){
										return '<span>' + globalVar.statusArr[i].description + '</span>'; 
									}
								}
								return '<span>' + value + '</span>';
							},
						},"
						/>
						
<#list 0..11 as x>
	<#assign datafield = datafield + "{name: 'quantity_" + x + "', type: 'number'},"/>
	<#assign datafield = datafield + "{name: 'statusId_" + x + "', type: 'string'},"/>
	<#assign datafield = datafield + "{name: 'recruitAnticipateSeqId_" + x + "', type: 'number'},"/>
	
	<#assign columnlist = columnlist + "{text: globalVar.monthNames[" + x +"], datafield: 'quantity_" + x +"', width: '7%', columntype: 'numberinput',
											cellsrenderer: function (row, column, value) {
												var className = '';
												var data = $('#jqxgrid').jqxGrid('getrowdata', row);
												if(data){
													var statusId = data['statusId_" + x + "'];
													if(statusId == 'REC_ANT_WAIT'){
														className = 'greenyellowCell';
													}else if(statusId == 'REC_ANT_ACC'){
														className = 'aquaCell';
													}else if(statusId == 'REC_ANT_REJ'){
														className = 'redCell';
													}else if(statusId == 'REC_ANT_CAN'){
														className = 'grayCell';
													}
												}
												return '<span id=' + id + ' class=' + className + '>' + value + '</span>';
											},
											
										}," />
	<#assign columnlist = columnlist + "{datafield: 'statusId_" + x +"', hidden: true}," />
	<#assign columnlist = columnlist + "{datafield: 'recruitAnticipateSeqId_" + x +"', hidden: true}," />
	
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
		<h4>${uiLabelMap.RecruitmentAnticipate}</h4>
		<div class="widget-toolbar none-content" >
			<button id="addRecruitmentAnticipateBtn" class="grid-action-button" style="font-size: 14px"><i class="icon-plus open-sans"></i><span>${uiLabelMap.CommonAddNew}</span></button>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div id="year"></div>
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
		
			<@jqGrid filtersimplemode="false" filterable="false" addrefresh="true" showtoolbar="false" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="false" addType="popup" editable="false" deleterow="false" 
				alternativeAddPopup="alterpopupWindow" addrow="true" showlist="false" sortable="false" mouseRightMenu="true" 
				contextMenuId="contextMenu" url="" jqGridMinimumLibEnable="false"/>
		</div>
	</div>
</div>		
<div id='contextMenu' class="hide">
	<ul>
		<li action="approver">
			<i class="fa-pencil"></i>${uiLabelMap.HRApprove}
        </li>
		<li action="edit" id="editRecruitAnticipate">
			<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
	</ul>
</div>
<#include "CreateRecruitmentAnticipate.ftl"/>					
<#include "ApprovalRecruitmentAnticipate.ftl"/>					
<#include "EditRecruitmentAnticipate.ftl"/>					
<script type="text/javascript" src="/hrresources/js/recruitment/ViewRecruitmentAnticipateList.js"></script>