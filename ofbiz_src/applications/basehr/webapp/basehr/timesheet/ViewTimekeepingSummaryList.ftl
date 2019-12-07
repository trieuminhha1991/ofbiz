<#include "script/ViewTimekeepingSummaryListScript.ftl"/>
<#assign datafield = "[{name: 'timekeepingSummaryId', type: 'string'},
						{name: 'partyId', type: 'string'},
						{name: 'timekeepingSummaryName', type: 'string'},
						{name: 'groupName', type: 'string'},
						{name: 'fromDate', type: 'date'},
						{name: 'thruDate', type: 'date'},
					    ]"/>
					    
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.TimekeepingSummary)}', datafield: 'timekeepingSummaryName', width: '30%',
						   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							   var rowData = $('#jqxgrid').jqxGrid('getrowdata', row);
							   var timekeepingSummaryId = rowData.timekeepingSummaryId;
							   return '<a href=\"ViewTimekeepingSummaryParty?timekeepingSummaryId='+ timekeepingSummaryId + '\" title=\"${StringUtil.wrapString(uiLabelMap.ViewDetails)}\">' + value + '</a>';
						   }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}', datafield: 'groupName', width: '25%'},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', editable: false, 
							   columntype: 'datetimeinput', width: '20%', cellsformat: 'dd/MM/yyyy',filtertype: 'range'},
					    {text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', editable: false, 
							   columntype: 'datetimeinput', width: '25%', cellsformat: 'dd/MM/yyyy', filtertype: 'range'},
						"/>
</script>					
<@jqGrid filtersimplemode="false" filterable="true" showtoolbar="true" dataField=datafield columnlist=columnlist  
				clearfilteringbutton="true"  editable="false" deleterow="false" selectionmode="singlerow"
				addrow="true" alternativeAddPopup="AddTimekeepingSummaryWindow" addType="popup" 
				showlist="false" sortable="true"  mouseRightMenu="true" 
				contextMenuId="contextMenu" url="jqxGeneralServicer?sname=JQGetListTimekeepingSummary" jqGridMinimumLibEnable="false"/>
				
<div id="contextMenu" class="hide">
	<ul>
		<li action="refreshData">
			<i class="fa fa-refresh"></i>${uiLabelMap.DmsRefreshData}
        </li>
	</ul>
</div>				
				
<div id="AddTimekeepingSummaryWindow" class="hide">
	<div>${uiLabelMap.AddTimesheetSummary}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.OrganizationalUnit)}</label>
				</div>
				<div class="span8">
					<div id="dropDownButtonAddNew" class="">
						<div style="border: none;" id="jqxTreeAddNew">
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HRCommonMonthYear)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span3">
								<div id="monthTS"></div>
							</div>
							<div class="span3">
								<div id="yearTS"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.CommonFromDate)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div id="fromDate"></div>
							</div>
							<div class="span8">
								<div class='row-fluid'>
									<div class="span4 text-algin-right">
										<label class="asterisk">${StringUtil.wrapString(uiLabelMap.CommonThruDate)}</label>
									</div>
									<div class="span8">
										<div id="thruDate"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.EmplTimesheetName)}</label>
				</div>
				<div class="span8">
					<input type="text" id="timekeepingSummaryName">
				</div>
			</div>
			<div class='row-fluid'>
				<div id="timekeepingDetailGrid"></div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingCreateNew" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerCreateNew"></div>
				</div>
			</div>
		</div>
		<div class='form-action'>
			<button type="button" class="btn btn-danger form-action-button pull-right icon-remove open-sans" id="cancelAdd">${uiLabelMap.CommonCancel}</button>
			<button type="button" class="btn btn-primary form-action-button pull-right icon-ok open-sans" id="saveAdd">${uiLabelMap.CommonSubmit}</button>
		</div>
	</div>
</div>				    
<script type="text/javascript" src="/hrresources/js/timesheet/ViewTimekeepingSummaryList.js"></script>
<script type="text/javascript" src="/hrresources/js/timesheet/AddTimekeepingSummaryList.js"></script>