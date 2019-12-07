<#include "script/ViewEmplTimekeepingReportScript.ftl"/>

<#assign datafield ="[{name: 'partyId', type:'string'},
					  {name: 'partyCode', type:'string'},
					  {name: 'partyName', type:'string'},"/>
<#assign columnlist = "{datafield: 'partyId', hidden: true},
					   {text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', width: 160, cellsalign: 'left', editable: false, pinned: true},
					   {text: '${uiLabelMap.EmployeeId}', datafield: 'partyCode', width: 120,cellsalign: 'left', pinned: true, editable: false,
								
    					},"/>
<#assign cal = Static["java.util.Calendar"].getInstance()/>
<#assign CalendarDate =  Static["java.util.Calendar"].DATE/>   					
<#assign CalendarMonth =  Static["java.util.Calendar"].MONTH/>   					
<#assign CalendarYear =  Static["java.util.Calendar"].YEAR/>  
<#assign columngrouplist = ""/> 
<#assign dayOfWeekNameList = [uiLabelMap.CommonSundayShort, uiLabelMap.CommonMondayShort, uiLabelMap.CommonTuesdayShort, uiLabelMap.CommonWednesdayShort, uiLabelMap.CommonThursdayShort, uiLabelMap.CommonFridayShort, uiLabelMap.CommonSaturdayShort]/>					
<#list dateOfMonth as date>
	${cal.setTime(date)}
	<#assign dataFieldGroup = cal.get(CalendarDate) + "/" +  (cal.get(CalendarMonth) + 1) + "/" + cal.get(CalendarYear)/>
	<#assign datafield = datafield + "{name: 'date_"+ dataFieldGroup +"', type: 'date'},"/>
	<#assign datafield = datafield + "{name: 'startTime_"+ dataFieldGroup +"', type: 'date'},"/>
	<#assign datafield = datafield + "{name: 'endTime_"+ dataFieldGroup +"', type: 'date'},"/> 
	
	<#assign columnlist = columnlist + "{datafield: 'date_"+ dataFieldGroup +"', hidden: 'true'},"/>
	<#assign columnlist = columnlist + "{text: '${uiLabelMap.HRCommonInTime}', width: 100, columntype: 'datetimeinput', cellsalign: 'center', filterable: false, datafield: 'startTime_"+ dataFieldGroup + "', columngroup: '" + dataFieldGroup+ "', cellsformat: 'HH:mm:ss',
		createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
			editor.jqxDateTimeInput({width: cellwidth, height: cellheight, formatString: 'HH:mm:ss', showCalendarButton: false});
			if(cellvalue){
				editor.val(cellvalue);
			}
		},
		initeditor: function (row, column, editor) {
            editor.jqxDateTimeInput({formatString: 'HH:mm:ss', showCalendarButton: false});
			editor.jqxDateTimeInput('val', new Date(${startTime.getTime()}));
        },
        cellendedit: function(rowid, datafield, columntype, oldvalue, newvalue){
        	var editFlag = viewEmplTimekeepingObject.updateEmplTimeInDate(rowid, datafield, columntype, oldvalue, newvalue);
        	return editFlag;
        }
	},"/>
	<#assign columnlist = columnlist + "{text: '${uiLabelMap.HRCommonOutTime}', columntype: 'datetimeinput', width: 100, cellsalign: 'center',filterable: false,  datafield: 'endTime_"+ dataFieldGroup + "', columngroup: '" + dataFieldGroup + "', cellsformat: 'HH:mm:ss',
		createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
			editor.jqxDateTimeInput({width: cellwidth, height: cellheight, formatString: 'HH:mm:ss', showCalendarButton: false});
			if(cellvalue){
				editor.val(cellvalue);
			}else{
				editor.val(new Date(${endTime.getTime()}));
			}
		},
		initeditor: function (row, column, editor) {
            editor.jqxDateTimeInput({formatString: 'HH:mm:ss', showCalendarButton: false});
			editor.jqxDateTimeInput('val', new Date(${endTime.getTime()}));
        },
        cellendedit: function(rowid, datafield, columntype, oldvalue, newvalue){        	
        	var editFlag = viewEmplTimekeepingObject.updateEmplTimeInDate(rowid, datafield, columntype, oldvalue, newvalue);
        	return editFlag;
        }
		
	},"/>
	<#assign columngrouplist = columngrouplist + "{text: '" + cal.get(CalendarDate) + "/" + (cal.get(CalendarMonth) + 1) + " - " + dayOfWeekNameList[cal.get(Static["java.util.Calendar"].DAY_OF_WEEK) - 1] + "', name: '" + dataFieldGroup + "', align: 'center'},"/>
</#list>    										  
<#assign datafield = datafield + "]"/>
<div class="row-fluid">
	<div id="appendNotification">
		<div id="updateNotification">
			<span id="notificationText"></span>
		</div>
	</div>	
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.DetailEmplTimekeepingReportTiltle}</h4>
		<div class="widget-toolbar none-content">
			<#if security.hasEntityPermission("HR_TIMESHEET", "_CREATE", session)>
			<button id="importFromExcel" class="grid-action-button fa-file-excel-o open-sans">${uiLabelMap.ImportFromExcel}</button>
			</#if>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class="row-fluid">
						<div class="span3" style="margin: 0; padding: 0">
							<label style="display: inline;">${uiLabelMap.CommonMonth} / ${uiLabelMap.CommonYear}</label>
						</div>
						<div class="span9" style="margin: 0">
							<div class="row-fluid">
								<div id="month" class="pull-left" style="margin-right: 6px"></div>
								<div id="year" class="pull-left" style="margin-right: 6px"></div>
								<button class="btn-primary btn-mini btn pull-left" id="btnSearch" >
									<i class="icon-search"></i>
								</button>
							</div>
						</div>
					</div>	
				</div>	
				<div class="span6">
					<div class="row-fluid">
						<div id="dropDownButton" class="pull-right">
							<div style="border: none;" id="jqxTree">
							</div>
						</div>
						<button class="pull-right grid-action-button fa-filter open-sans" id="removeFilter" >
							${uiLabelMap.accRemoveFilter}
						</button>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">	
			<#if security.hasEntityPermission("HR_TIMESHEET", "_ADMIN", session)>
				<#assign edit = "true">
			<#else>
				<#assign edit = "false">
			</#if>
			<#if security.hasEntityPermission("HR_TIMESHEET", "_ADMIN", session)>
				<#assign mouseRightMenu = "true">
			<#else>
				<#assign mouseRightMenu = "false">
			</#if>
			<@jqGrid url="" 
				columnlist=columnlist dataField=datafield 
				filtersimplemode="true" editmode="selectedcell" sortable="false" showtoolbar="false" editable=edit id="jqxgrid" 
				clearfilteringbutton="true" columngrouplist=columngrouplist mouseRightMenu=mouseRightMenu contextMenuId="contextMenu"
				updateRowFunction="updateRowCustom" selectionmode="singlecell"
				jqGridMinimumLibEnable="false"
				/>
		</div>
	</div>
</div>
	
<div id="importExcelWindow" class="hide">
	<div>${uiLabelMap.ImportFromExcel}</div>
	<div class="form-window-container">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.FileExcelTimekeeping}</label>
				</div>
				<div class="span7">
					<div style="width: 111%">
						<form class="no-margin" action="" class="row-fluid" id="upLoadFileForm"  method="post" enctype="multipart/form-data">
							<input type="hidden" name="_uploadedFile_fileName" id="_uploadedFile_fileName" value="" />
							<input type="hidden" name="_uploadedFile_contentType" id="_uploadedFile_contentType" value="" />
							<div class="rowf-fluid">
								<div class="span12" style="margin-bottom: 0px !important; height: 0px !important">
						 			<input type="file" id="uploadedFile" name="uploadedFile"/>
						 		</div>
							</div>
					 	</form>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.TimekeepingFromDate}</label>
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
										<label class="">${uiLabelMap.CommonThruDate}</label>
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
					<label class="">${uiLabelMap.CommonTemplate}</label>
				</div>
				<div class="span8">
					<img src="/hrresources/icon/template_cham_cong.jpg" id="imagePreview" height="250" width="99%" style="max-width: none;"/>
					<a href="javascript:void(0)" class="icon-download open-sans">${uiLabelMap.DownloadFileTemplate}</a>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.NumberLineTitle}</label>
				</div>
				<div class="span8">
					<div class="row-fluid">
						<div class="span12">
							<div class="span2">
								<div id="numberLineTitle"></div>
							</div>
							<div class="span10">
								<div class='row-fluid'>
									<div class="span4 text-algin-right">
										<label class="">${uiLabelMap.DateTimePattern}</label>
									</div>
									<div class="span8">
										<div id="dateTimePattern"></div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.ColumnExcelImportMap}</label>
				</div>
				<div class="span8">
					<div class="display-none-pager-header">
						<div id="gridMapField"></div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label>${uiLabelMap.OverrideDataWay}</label>
				</div>
				<div class="span8">
					<div id="overrideDataWay"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button class='btn btn-danger form-action-button pull-right' id="alterCancel"><i class='fa-remove'></i>${uiLabelMap.CommonCancel}</button>
					<button class='btn btn-primary form-action-button pull-right' id="alterSave"><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
				
</div>
<#if security.hasEntityPermission("HR_TIMESHEET", "_DELETE", session)>
<div id="contextMenu" class="hide">
	<ul>
		<li action="delete">${StringUtil.wrapString(uiLabelMap.Delete)}</li>
	</ul>
</div>
</#if>
<script type="text/javascript" src="/hrresources/js/timesheet/ViewEmplTimekeepingReport.js"></script>
<style>     
#EmplTimeKeeping .green {
    color: black\9;
    background-color: #b6ff00\9;
}
#EmplTimeKeeping .yellow {
    color: black\9;
    background-color: yellow\9;
}
#EmplTimeKeeping .red {
    color: black\9;
    background-color: #FF6600;
}
#EmplTimeKeeping .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .green:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: #b6ff00;
}
#EmplTimeKeeping .yellow:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .yellow:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: yellow;
}
#EmplTimeKeeping .red:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected), .jqx-widget .red:not(.jqx-grid-cell-hover):not(.jqx-grid-cell-selected) {
    color: black;
    background-color: #e83636;
}
#EmplTimeKeeping .white{
	color: black;
    background-color: #ffffff;
}
</style>
