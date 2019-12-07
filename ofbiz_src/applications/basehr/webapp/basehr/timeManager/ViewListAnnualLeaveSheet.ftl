<#include "script/ViewListAnnualLeaveSheetScript.ftl"/>
<#assign datafield = "[{name: 'partyId', type: 'string'},
						{name: 'partyCode', type: 'string'},
						{name: 'firstName', type: 'string'},
						{name: 'fullName', type: 'string'},
						{name: 'fromDate', type: 'date', other: 'Timestamp'},
						{name: 'emplPositionTypeDesc', type: 'string'},
						{name: 'groupName', type: 'string'},
						{name: 'lastYearLeft', type: 'number'},
						{name: 'lastYearTransferred', type: 'number'},
						{name: 'leaveDayYear', type: 'number'},
						{name: 'grantedLeave', type: 'number'},
						{name: 'totalDayLeave', type: 'number'},
						{name: 'annualLeftDay', type: 'number'},
						{name: 'remainDay', type: 'number'},
						]"/>
						
<script type="text/javascript">
<#assign columnlist="{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyCode', width: '11%', editable: false},
					{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'firstName', width: '15%', editable: false, 
						cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							var dataRecord = $('#jqxgrid').jqxGrid('getrowdata', row);
							if(dataRecord){
								return '<span>' + dataRecord.fullName + '</span>';
							}
						}
					},
					{text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionTypeDesc', width: '16%', editable: false, },
					{text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'groupName', width: '16%', editable: false},
					{text: '${StringUtil.wrapString(uiLabelMap.AnnualLastYearLeft)}', datafield: 'lastYearLeft', width: '15%', columntype: 'numberinput', 
						editable: false,filterType : 'number', cellsalign: 'right'},
					{text: '${StringUtil.wrapString(uiLabelMap.AnnualLastYearTransferred)}', datafield: 'lastYearTransferred', width: '15%', 
							columntype: 'numberinput',filterType : 'number', cellsalign: 'right'},
					{text: '${StringUtil.wrapString(uiLabelMap.AnnualLeaveDayYear)}', datafield: 'leaveDayYear', width: '15%', 
								columntype: 'numberinput',filterType : 'number', cellsalign: 'right'},
					{text: '${StringUtil.wrapString(uiLabelMap.AnnualGrantedLeave)}', datafield: 'grantedLeave', width: '15%', 
									columntype: 'numberinput',filterType : 'number', cellsalign: 'right'},
					{text: '${StringUtil.wrapString(uiLabelMap.TotalDayLeaveAnnual)}', datafield: 'totalDayLeave', width: '15%', 
										columntype: 'numberinput', editable: false,filterType : 'number', cellsalign: 'right'},
					{text: '${StringUtil.wrapString(uiLabelMap.AnnualLeftDay)}', datafield: 'annualLeftDay',
							width: '15%', columntype: 'numberinput', editable: false, sortable: false,filterable: false, cellsalign: 'right'},
					{text: '${StringUtil.wrapString(uiLabelMap.AnnualDayRemain)}', datafield: 'remainDay', width: '15%', 
						columntype: 'numberinput', editable: false, sortable: false, filterable: false, cellsalign: 'right'
					}"/>
</script>
<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.AnnualLeaveSheet}</h4>
		<div class="widget-toolbar none-content" style="width: 500px; padding: 0; margin: 0; line-height: 30px">
			<div class="row-fluid">
				<div class="span2" style="float: right; margin-right: 10px; margin-top: 5px; padding: 0">
					<div id="yearNumberInput"></div>
				</div>
				<div class="span6" style="float: right; margin: 0; padding: 0; text-align: right;">
					<button id="addNew" class="grid-action-button icon-plus open-sans">${uiLabelMap.CommonAddNew}</button>
				</div>
			</div>
			
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div class="span12" style="margin-right: 15px">
							<div id="dropDownButton" style="margin-top: 5px;" class="">
								<div style="border: none;" id="jqxTree">
										
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid margin-bottom10">
						<button id="removeFilter" class="grid-action-button icon-filter open-sans pull-right">${uiLabelMap.HRCommonRemoveFilter}</button>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid addrow="true" addType="popup" id="jqxgrid" filtersimplemode="true" showtoolbar="false" clearfilteringbutton="false"
				 deleterow="true" addrefresh="true" alternativeAddPopup="alterpopupWindow" editable="true" dataField=datafield columnlist=columnlist
				 url="" filterable="true" showlist="false"
				 createUrl="" width="100%" bindresize="false"
				 addColumns="" sortable="false"
				 removeUrl="" deleteColumn=""
				 updateUrl="jqxGeneralServicer?sname=updateEmplLeaveRegulation&jqaction=U" 
				 editColumns="partyId;fromDate(java.sql.Timestamp);lastYearLeft(java.math.BigDecimal);lastYearTransferred(java.math.BigDecimal);leaveDayYear(java.math.BigDecimal);grantedLeave(java.math.BigDecimal)" 
				 jqGridMinimumLibEnable="false"/>
		</div>
	</div>
</div>
<div id="popupAddNewWindow" class="hide">
	<div>${uiLabelMap.HRCreateAnnualLeaveSheet}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span9 text-algin-right'>
					<label class="asterisk">${uiLabelMap.AddNewAnnualLeaveSheetInYear}</label>
				</div>
				<div class="span3">
					<div id="annualLeaveYear"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span9 text-algin-right'>
					<label class="">${uiLabelMap.MoveAnnualLastYearLeft}</label>
				</div>
				<div class="span3">
					<div id="moveAnnualLastYearLeft" style="margin-left: 0 !important; margin-top: 3px"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
    		<button type="button" class='btn btn-danger form-action-button pull-right' id="alterCancel">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
    		<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSave">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
    	</div>
	</div>	
</div>

<script type="text/javascript" src="/hrresources/js/timeManager/ViewListAnnualLeaveSheet.js"></script>