<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<#assign datafield = "[{name: 'workingShiftId', type: 'string'},
						{name: 'workingShiftName', type: 'string'},
						{name: 'shiftStartTime', type: 'date'},
						{name: 'shiftBreakStart', type: 'date'},
						{name: 'shiftBreakEnd', type: 'date'},
						{name: 'shiftEndTime', type: 'date'},
						{name: 'startOverTimeAfterShift', type: 'date'},
						{name: 'endOverTimeAfterShift', type: 'date'},
						{name: 'allowLateMinute', type: 'number'},
						{name: 'isAllowOTAfterShift', type: 'string'},
						{name: 'minMinuteOvertime', type: 'number'}]"/>
						
<script type="text/javascript">
var workingShiftWorkTypeArr = [
	<#if workingShiftWorkTypeList?has_content>        
		<#list workingShiftWorkTypeList as workingShiftWorkType>
		{
			workTypeId: "${workingShiftWorkType.workTypeId}",
			description: "${StringUtil.wrapString(workingShiftWorkType.description?if_exists)}",
			sign: '${StringUtil.wrapString(workingShiftWorkType.sign?if_exists)}'
		},	
		</#list>
	</#if>
];

var dayOfWeekArr = [
		<#if dayOfWeekList?has_content>
			<#list dayOfWeekList as dayOfWeek>
			{
				dayOfWeek: "${dayOfWeek.dayOfWeek}",
				description: "${StringUtil.wrapString(dayOfWeek.description?if_exists)}"
			},		
			</#list>
		</#if>
];
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.WorkingShiftId)}', datafield: 'workingShiftId', width: 120},
					   {text: '${StringUtil.wrapString(uiLabelMap.WorkingShiftName)}', datafield: 'workingShiftName', width: 180},
					   {text: '${StringUtil.wrapString(uiLabelMap.WorkingShiftStart)}', datafield: 'shiftStartTime', width: 130, 
						   cellsformat: 'HH:mm', columntype: 'datetimeinput'
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.WorkingShiftBreakStart)}', datafield: 'shiftBreakStart', width: 190, 
						   cellsformat: 'HH:mm', columntype: 'datetimeinput'
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.WorkingShiftBreakEnd)}', datafield: 'shiftBreakEnd', width: 190, 
						   cellsformat: 'HH:mm', columntype: 'datetimeinput'
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.WorkingShiftEnd)}', datafield: 'shiftEndTime', 
						   cellsformat: 'HH:mm', columntype: 'datetimeinput'
					   },
					   {datafield: 'minMinuteOvertime', hidden: true},
					   {datafield: 'allowLateMinute', hidden: true},
					   {datafield: 'isAllowOTAfterShift', hidden: true}
					   "/>

</script>
<div id="containerNtf">
</div>
<div id="jqxNotification">
	<div id="notificationContent"></div>
</div>				
<@jqGrid url="jqxGeneralServicer?sname=JQgetListWorkingShift&hasrequest=Y" dataField=datafield columnlist=columnlist
	clearfilteringbutton="true"
	editable="false"
	filterable="false" 
	jqGridMinimumLibEnable="false" 
	autorowheight="true" jqGridMinimumLibEnable="false" sortable="false"
	showtoolbar = "true" deleterow="true" filterable="false" clearfilteringbutton="false" showlist="false"
	updateUrl="" editColumns=""
	customcontrol1="icon-cog@${uiLabelMap.ConfigPartyWorkingShift}@#javascript:void(0)@configPartyWorkingShift()"
	removeUrl="jqxGeneralServicer?sname=&jqaction=D" deleteColumn=""
	createUrl="jqxGeneralServicer?sname=&jqaction=C" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
	addColumns=""/>	

<div id="popupAddRow" class="hide">
	<div>${uiLabelMap.CreateNewWorkingShift}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.WorkingShiftIdFull}</label>
							</div>
							<div class="span7">
								<input type="text" id="workingShiftIdNew">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.WorkingShiftNameFull}</label>
							</div>
							<div class="span7">
								<input type="text" id="workingShiftNameNew">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.MinuteAllowWorkLate}</label>
							</div>
							<div class="span7">
								<div id="allowLateMinuteNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.MinimumMinuteCalcOT}</label>
							</div>
							<div class="span7">
								<div id="minMinuteOvertimeNew"></div>
							</div>
						</div>
						
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.AllowWorkingOvertimeAfterShift}</label>
							</div>
							<div class="span7">
								<div id="allowOTAfterShift" style="margin-left: 0 !important"></div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="asterisk">${uiLabelMap.WorkingShiftStart}</label>
							</div>
							<div class="span5">
								<div id="shiftStartTimeNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.WorkingShiftBreakStart}</label>
							</div>
							<div class="span5">
								<div id="shiftBreakStartTimeNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label>${uiLabelMap.WorkingShiftBreakEnd}</label>
							</div>
							<div class="span5">
								<div id="shiftBreakEndTimeNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="asterisk">${uiLabelMap.WorkingShiftEnd}</label>
							</div>
							<div class="span5">
								<div id="shiftEndTimeNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.StartOverTimeAfterShift}</label>
							</div>
							<div class="span5">
								<div id="startOverTimeAfterShiftNew"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.EndOverTimeAfterShift}</label>
							</div>
							<div class="span5">
								<div id="endOverTimeAfterShiftNew"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<hr/>
			</div>
			<div class="row-fluid">
				<div id="jqxgridShiftWorkTypeNew"></div>
			</div>
		</div>
		<div class="form-action">
			<button id="btnCancelNew" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSaveNew">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>	
	
<div id="workingShiftEditWindow" class="hide">
	<div></div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class="row-fluid">
				<div class="span12">
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.WorkingShiftIdFull}</label>
							</div>
							<div class="span7">
								<input type="text" id="workingShiftId">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="asterisk">${uiLabelMap.WorkingShiftNameFull}</label>
							</div>
							<div class="span7">
								<input type="text" id="workingShiftName">
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.MinuteAllowWorkLate}</label>
							</div>
							<div class="span7">
								<div id="allowLateMinute"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.MinimumMinuteCalcOT}</label>
							</div>
							<div class="span7">
								<div id="minMinuteOvertime"></div>
							</div>
						</div>
						
						<div class='row-fluid margin-bottom10'>
							<div class='span5 text-algin-right'>
								<label class="">${uiLabelMap.AllowWorkingOvertimeAfterShift}</label>
							</div>
							<div class="span7">
								<div id="allowOTAfterShiftEdit" style="margin-left: 0 !important"></div>
							</div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.WorkingShiftStart}</label>
							</div>
							<div class="span5">
								<div id="shiftStartTime"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.WorkingShiftBreakStart}</label>
							</div>
							<div class="span5">
								<div id="shiftBreakStartTime"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label>${uiLabelMap.WorkingShiftBreakEnd}</label>
							</div>
							<div class="span5">
								<div id="shiftBreakEndTime"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.WorkingShiftEnd}</label>
							</div>
							<div class="span5">
								<div id="shiftEndTime"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.StartOverTimeAfterShift}</label>
							</div>
							<div class="span5">
								<div id="startOverTimeAfterShift"></div>
							</div>
						</div>
						<div class='row-fluid margin-bottom10'>
							<div class='span7 text-algin-right'>
								<label class="">${uiLabelMap.EndOverTimeAfterShift}</label>
							</div>
							<div class="span5">
								<div id="endOverTimeAfterShift"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<hr/>
			</div>
			<div class="row-fluid">
				<div id="containerjqxgridShiftWorkType">
				</div>
				<div id="jqxNotificationjqxgridShiftWorkType">
					<div id="notificationContentjqxgridShiftWorkType"></div>
				</div>
				<div id="jqxgridShiftWorkType"></div>
			</div>
		</div>
		<div class="form-action">
			<button id="btnCancel" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSave">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>	
<div id="configPartyWorkingShiftWindow" class="hide">
	<div>
		${uiLabelMap.ConfigPartyWorkingShift}
	</div>
	<div>
		<div id="containerNtfEditConfigPartyWS">
		</div>
		<div id="jqxNotificationEditConfigPartyWS">
			<div id="notificationContentEditConfigPartyWS"></div>
		</div>
		<div id="configPartyShiftTreeGrid"></div>		
	</div>
</div>
<div id="editConfigPartyWSWindow" class='hide'>
	<div>${uiLabelMap.EditConfigPartyConfigWorkingShift}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.OrgUnitName}</label>
				</div>
				<div class="span7">
					<input type="text" id="configWSPartyName">					
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.OrgUnitId}</label>
				</div>
				<div class="span7">
					<input type="text" id="configWSPartyId">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span5 text-algin-right'>
					<label class="">${uiLabelMap.HrCommonWorkingShift}</label>
				</div>
				<div class="span7">
					<div id="workingShiftDropdownlist"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelConfigPartyWS" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveConfigPartyWS">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<script type="text/javascript">
var globalVar = {
		getAllWorkingShift: true,
		allWorkingShiftArr: []
}
</script>

<#include "component://hrolbius/webapp/hrolbius/ftl/timeManager/js/configPartyWorkingShiftJs.ftl"/>

<script type="text/javascript">
var workingShiftWorkTypeArr = [
	<#if workingShiftWorkTypeList?has_content>
		<#list workingShiftWorkTypeList as workingShiftWorkType>
		{
			workTypeId: "${workingShiftWorkType.workTypeId}",
			description: '${StringUtil.wrapString(workingShiftWorkType.description)}'
		},
		</#list>
	</#if>
];
$(document).ready(function(){
	initJqxgridEvent();
	initJqxInput();
	initJqxDatetimeInput();
	initJqxGridWorkWeek();
	initJqxGridCreateNewWorkShift();
	initJqxNotification();
	initJqxNumberInput();
	initBtnEvent();
	initJqxDropdownlist();
	initJqxCheckBox();
	initJqxWindow();
	initJqxValidator();
});

function initJqxgridEvent(){
	$("#jqxgrid").on("rowdoubleclick", function(event){
		var index = event.args.rowindex;
		var data = $("#jqxgrid").jqxGrid('getrowdata', index); 
		openJqxWindow($("#workingShiftEditWindow"));
	});
}

function initJqxInput(){
	$("#workingShiftId, #workingShiftName, #workingShiftIdNew, #workingShiftNameNew").jqxInput({width: '95%', height: 20, theme: 'olbius'});
	$("#workingShiftId").jqxInput({disabled: true});
	$("#configWSPartyId, #configWSPartyName").jqxInput({width: '95%', height: 20, theme: 'olbius', disabled: true})
}

function initJqxDatetimeInput(){
	$("#shiftStartTime, #shiftBreakStartTime, #shiftBreakEndTime, #shiftEndTime, #startOverTimeAfterShift, #endOverTimeAfterShift").jqxDateTimeInput({
		formatString: 'HH:mm:ss', 
		showCalendarButton: false,
		width: '89%', 
		height: '25px',
		theme: 'olbius'
	});
	$("#shiftStartTimeNew, #shiftBreakStartTimeNew, #shiftBreakEndTimeNew, #shiftEndTimeNew, #startOverTimeAfterShiftNew, #endOverTimeAfterShiftNew").jqxDateTimeInput({
		formatString: 'HH:mm:ss', 
		showCalendarButton: false,
		width: '89%', 
		height: '25px',
		theme: 'olbius',
		value: null
	});
	$("#startOverTimeAfterShift, #endOverTimeAfterShift").jqxDateTimeInput({
		disabled: true
	});
	$("#startOverTimeAfterShiftNew, #endOverTimeAfterShiftNew").jqxDateTimeInput({
		disabled: true
	});
}

function initJqxGridWorkWeek(){
	var datafield =  [
	    <#if dayOfWeekList?has_content>
        	<#list dayOfWeekList as dayOfWeek>
     		{name: '${dayOfWeek.dayOfWeek}', type: 'string'},
        	</#list>
	    </#if>     		
	    {name: 'workingShiftId', type: 'string'},
   	];
	
	var columnlist = [
		<#if dayOfWeekList?has_content>
			<#list dayOfWeekList as dayOfWeek>
				{text: '${StringUtil.wrapString(dayOfWeek.description)}', datafield: '${dayOfWeek.dayOfWeek}', 
					<#if dayOfWeek_has_next>width: 110,</#if> columntype: 'dropdownlist',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
						for(var i = 0; i < workingShiftWorkTypeArr.length; i++){
							if(value == workingShiftWorkTypeArr[i].workTypeId){
								return '<span>' + workingShiftWorkTypeArr[i].description +'</span>';
							}
						}
						return '<span>' + value +'</span>';
					},
					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
				        var dataSource = {
				        		localdata: workingShiftWorkTypeArr,
				                datatype: "array"
				        };
				        var dataAdapter = new $.jqx.dataAdapter(dataSource);
				        editor.jqxDropDownList({source: dataAdapter,  displayMember: "description", valueMember: "workTypeId", autoDropDownHeight: true});
				    }
				},
			</#list>
		</#if>
		{hidden: true, datafield: 'workingShiftId'}
	];
	
	var rendertoolbar = function (toolbar){
		var jqxheader = $("<div id='toolbarcontainer' class='widget-header'><h4>${StringUtil.wrapString(uiLabelMap.WorkingShifWorkTypeWorkWeek)}</h4><div id='toolbarButtonContainer' class='pull-right'></div></div>");
		toolbar.append(jqxheader);
	};
	var editColumns = "<#if dayOfWeekList?has_content><#list dayOfWeekList as dayOfWeek>${dayOfWeek.dayOfWeek};</#list></#if>workingShiftId";	
	var config = {
	   		width: '100%', 
	   		autoheight: true,
	   		virtualmode: true,
	   		showfilterrow: true,
	   		showtoolbar: true,
	   		rendertoolbar: rendertoolbar,
	   		selectionmode: 'singlecell',
	   		pageable: false,
	   		sortable: false,
	        filterable: false,
	        editable: true,
	        editmode: 'dblclick',
	        rowsheight: 26,
	        url: 'JQGetWorkingShiftDayWeek&hasrequest=Y',                
	        source: {pagesize: 10}
   	};
   	GridUtils.initGrid(config, datafield, columnlist, null, $("#jqxgridShiftWorkType"));
   	//$("#jqxgridShiftWorkType").jqxGrid('selectionmode', 'singlecell');
}

function initJqxGridCreateNewWorkShift(){
	var dataDefault = generatedataWorkShiftTypeInWeek();
	var source =
    {
        localdata: dataDefault,
        datatype: "array",
        datafields:
        [
		<#if dayOfWeekList?has_content>
			<#list dayOfWeekList as dayOfWeek>
				{name: '${dayOfWeek.dayOfWeek}', type: 'string'},
			</#list>
		</#if>  
        ]
    };
	
	var columnlist = [
   		<#if dayOfWeekList?has_content>
   			<#list dayOfWeekList as dayOfWeek>
   				{text: '${StringUtil.wrapString(dayOfWeek.description)}', datafield: '${dayOfWeek.dayOfWeek}', 
   					<#if dayOfWeek_has_next>width: 110,</#if> columntype: 'dropdownlist',
   					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
   						for(var i = 0; i < workingShiftWorkTypeArr.length; i++){
   							if(value == workingShiftWorkTypeArr[i].workTypeId){
   								return '<span>' + workingShiftWorkTypeArr[i].description +'</span>';
   							}
   						}
   						return '<span>' + value +'</span>';
   					},
   					createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
   				        var dataSource = {
   				        		localdata: workingShiftWorkTypeArr,
   				                datatype: "array"
   				        };
   				        var dataAdapter = new $.jqx.dataAdapter(dataSource);
   				        editor.jqxDropDownList({source: dataAdapter,  displayMember: "description", valueMember: "workTypeId", autoDropDownHeight: true});
   				    }
   				},
   			</#list>
   		</#if>
   		{hidden: true, datafield: 'workingShiftId'}
   	];
	var rendertoolbar = function (toolbar){
		var jqxheader = $("<div id='toolbarcontainer' class='widget-header'><h4>${StringUtil.wrapString(uiLabelMap.WorkingShifWorkTypeWorkWeek)}</h4><div id='toolbarButtonContainer' class='pull-right'></div></div>");
		toolbar.append(jqxheader);
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	$("#jqxgridShiftWorkTypeNew").jqxGrid({
		width: '100%',
        source: dataAdapter,
        pageable: true,
        autoheight: true,
        columns: columnlist,
        showtoolbar: true,
   		rendertoolbar: rendertoolbar,
   		sortable: false,
        filterable: false,
        editable: true,
        editmode: 'dblclick',
        rowsheight: 26,
        selectionmode: 'singlecell',
        theme: 'olbius'
	});
}

function generatedataWorkShiftTypeInWeek(){
	var data = [{
		MONDAY: 'ALL_SHIFT',
		TUESDAY: 'ALL_SHIFT',
		WEDNESDAY: 'ALL_SHIFT',
		THURSDAY: 'ALL_SHIFT',
		FRIDAY:'ALL_SHIFT',
		SATURDAY: 'FIRST_HALF_SHIFT',
		SUNDAY: 'DAY_OFF'
	}];
	return data;
}

function initJqxNotification(){
	$("#jqxNotificationjqxgridShiftWorkType").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#containerjqxgridShiftWorkType"});
	$("#jqxNotification").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#containerNtf"});
	$("#jqxNotificationEditConfigPartyWS").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#containerNtfEditConfigPartyWS"});
}

function initJqxNumberInput(){
	$("#minMinuteOvertime, #allowLateMinute, #minMinuteOvertimeNew, #allowLateMinuteNew").jqxNumberInput({ 
		width: '97%', height: '25px',  spinButtons: true, theme: 'olbius', inputMode: 'simple',
		decimalDigits: 0, min: 0
	});
	$("#minMinuteOvertimeNew, #minMinuteOvertime").jqxNumberInput({
		disabled: true
	});
}

function initBtnEvent(){
	$("#btnCancel").click(function(event){
		$("#workingShiftEditWindow").jqxWindow('close');
	});
	
	$("#btnCancelNew").click(function(event){
		$("#popupAddRow").jqxWindow('close');
	});
	
	$("#btnSave").click(function(event){
		var valid = $("#workingShiftEditWindow").jqxValidator('validate');
		if(!valid){
			return;
		}
		var dataSubmit = {};
		dataSubmit.workingShiftId = $("#workingShiftId").val();
		dataSubmit.workingShiftName = $("#workingShiftName").val();
		dataSubmit.minMinuteOvertime = $("#minMinuteOvertime").val();
		dataSubmit.allowLateMinute = $("#allowLateMinute").val();
		dataSubmit.shiftStartTime = $("#shiftStartTime").jqxDateTimeInput('val', 'date').getTime();
		dataSubmit.shiftEndTime = $("#shiftEndTime").jqxDateTimeInput('val', 'date').getTime();
		dataSubmit.isAllowOTAfterShift = $("#allowOTAfterShiftEdit").val();
		var shiftBreakStartTime = $("#shiftBreakStartTime").jqxDateTimeInput('val', 'date');
		if(shiftBreakStartTime){
			dataSubmit.shiftBreakStartTime = shiftBreakStartTime.getTime();
		}
		var shiftBreakEndTime = $("#shiftBreakEndTime").jqxDateTimeInput('val', 'date');
		if(shiftBreakEndTime){
			dataSubmit.shiftBreakEndTime = shiftBreakEndTime.getTime();
		}
		var startOverTimeAfterShift = $("#startOverTimeAfterShift").jqxDateTimeInput('val', 'date');
		if(startOverTimeAfterShift){
			dataSubmit.startOverTimeAfterShift = startOverTimeAfterShift.getTime();
		}
		var endOverTimeAfterShift = $("#endOverTimeAfterShift").jqxDateTimeInput('val', 'date');
		if(endOverTimeAfterShift){
			dataSubmit.endOverTimeAfterShift = endOverTimeAfterShift.getTime();
		}
		var rows = $("#jqxgridShiftWorkType").jqxGrid('getrows');
		if(rows[0]){
			<#if dayOfWeekList?has_content>
				<#list dayOfWeekList as dayOfWeek>
					dataSubmit.${dayOfWeek.dayOfWeek} = rows[0].${dayOfWeek.dayOfWeek};
				</#list> 
			</#if>
		}
		$("#workingShiftEditWindow").jqxWindow('close');
		$("#jqxgrid").jqxGrid({disabled: true});
		$("#jqxgrid").jqxGrid('showloadelement');
		$.ajax({
			url: 'updateWorkingShift',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				$("#jqxNotification").jqxNotification('closeLast');
				if(response.responseMessage == 'success'){
					$('#containerNtf').empty();
					$('#jqxNotification').jqxNotification({template: 'info'});
					$("#notificationContent").text(response.successMessage);
					$("#jqxNotification").jqxNotification("open");
					$("#jqxgrid").jqxGrid('updatebounddata');
					globalVar.getAllWorkingShift = true;
				}else{
					$('#containerNtf').empty();
					$('#jqxNotification').jqxNotification({template: 'error'});
					$("#notificationContent").text(response.errorMessage);
					$("#jqxNotification").jqxNotification("open");
				}
			},
			complete:  function(jqXHR, textStatus){
				$("#jqxgrid").jqxGrid({disabled: false});
				$("#jqxgrid").jqxGrid('hideloadelement');
			}
		});
	});
	
	$("#btnSaveNew").click(function(event){
		var valid = $("#popupAddRow").jqxValidator('validate');
		if(!valid){
			return;
		}
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.CreateNewWorkingShiftConfirm)}",
			 [
			 {
	   		    "label" : "${uiLabelMap.CommonSubmit}",
	   		    "class" : "btn-primary btn-mini icon-ok",
	   		    "callback": function() {
	   		    	createNewWorkingShift();	    			
	   		    }
	   		 },
	   		 {
	   		    "label" : "${uiLabelMap.CommonCancel}",
	   		    "class" : "btn-danger icon-remove btn-mini",
	   		    "callback": function() {
	   		    	
	   		    }
	   		 }
	   		 ]
		 );
	});
	
	$("#cancelConfigPartyWS").click(function(event){
		$("#editConfigPartyWSWindow").jqxWindow('close');
	});
	
	$("#saveConfigPartyWS").click(function(event){
		var workingShiftSelectItem = $("#workingShiftDropdownlist").jqxDropDownList('getSelectedItem');
		if(workingShiftSelectItem){
			var dataSubmit = {};	
			dataSubmit.partyId = $("#configWSPartyId").val();
			dataSubmit.workingShiftId = workingShiftSelectItem.value;
			$("#configPartyShiftTreeGrid").jqxTreeGrid({disabled: true});
			$("#saveConfigPartyWS").attr("disabled", "disabled");
			$("#cancelConfigPartyWS").attr("disabled", "disabled");
			$.ajax({
				url: 'EditWorkingShiftPartyConfig',
				data: dataSubmit,
				type: 'POST',
				success: function(response){
					$("#jqxNotificationEditConfigPartyWS").jqxNotification("closeLast");
					if(response._EVENT_MESSAGE_){
						$('#containerNtfEditConfigPartyWS').empty();
						$('#jqxNotificationEditConfigPartyWS').jqxNotification({template: 'info'});
						$("#notificationContentEditConfigPartyWS").text(response._EVENT_MESSAGE_);
						$("#jqxNotificationEditConfigPartyWS").jqxNotification("open");
						//var selection = $("#configPartyShiftTreeGrid").jqxTreeGrid('getSelection');
						$("#configPartyShiftTreeGrid").jqxTreeGrid('setCellValue', dataSubmit.partyId, 'workingShiftName', workingShiftSelectItem.label);
						$("#configPartyShiftTreeGrid").jqxTreeGrid('setCellValue', dataSubmit.partyId, 'workingShiftId', workingShiftSelectItem.value);
					}else{
						$('#containerNtfEditConfigPartyWS').empty();
						$('#jqxNotificationEditConfigPartyWS').jqxNotification({template: 'error'});
						$("#notificationContentEditConfigPartyWS").text(response._ERROR_MESSAGE_);
						$("#jqxNotificationEditConfigPartyWS").jqxNotification("open");
					}
					$("#editConfigPartyWSWindow").jqxWindow('close');
				},
				complete:  function(jqXHR, textStatus){
					$("#configPartyShiftTreeGrid").jqxTreeGrid({disabled: false});
					$("#saveConfigPartyWS").removeAttr("disabled");
					$("#cancelConfigPartyWS").removeAttr("disabled");
				}
			});	
		}else{
			$("#editConfigPartyWSWindow").jqxWindow('close');
		}
	});
}

function createNewWorkingShift(){
	var dataSubmit = {};
	if($("#workingShiftIdNew").val()){
		dataSubmit.workingShiftId = $("#workingShiftIdNew").val();
	}
	dataSubmit.workingShiftName = $("#workingShiftNameNew").val();
	dataSubmit.minMinuteOvertime = $("#minMinuteOvertimeNew").val();
	dataSubmit.allowLateMinute = $("#allowLateMinuteNew").val();
	dataSubmit.shiftStartTime = $("#shiftStartTimeNew").jqxDateTimeInput('val', 'date').getTime();
	dataSubmit.shiftEndTime = $("#shiftEndTimeNew").jqxDateTimeInput('val', 'date').getTime();
	dataSubmit.isAllowOTAfterShift = $("#allowOTAfterShift").val();
	var shiftBreakStartTime = $("#shiftBreakStartTimeNew").jqxDateTimeInput('val', 'date');
	if(shiftBreakStartTime){
		dataSubmit.shiftBreakStartTime = shiftBreakStartTime.getTime();
	}
	var shiftBreakEndTime = $("#shiftBreakEndTimeNew").jqxDateTimeInput('val', 'date');
	if(shiftBreakEndTime){
		dataSubmit.shiftBreakEndTime = shiftBreakEndTime.getTime();
	}
	var startOverTimeAfterShift = $("#startOverTimeAfterShiftNew").jqxDateTimeInput('val', 'date');
	if(startOverTimeAfterShift){
		dataSubmit.startOverTimeAfterShift = startOverTimeAfterShift.getTime();
	}
	var endOverTimeAfterShift = $("#endOverTimeAfterShiftNew").jqxDateTimeInput('val', 'date');
	if(endOverTimeAfterShift){
		dataSubmit.endOverTimeAfterShift = endOverTimeAfterShift.getTime();
	}
	var rows = $("#jqxgridShiftWorkTypeNew").jqxGrid('getrows');
	if(rows[0]){
		<#if dayOfWeekList?has_content>
			<#list dayOfWeekList as dayOfWeek>
				dataSubmit.${dayOfWeek.dayOfWeek} = rows[0].${dayOfWeek.dayOfWeek};
			</#list> 
		</#if>
	}
	$("#popupAddRow").jqxWindow('close');
	$("#jqxgrid").jqxGrid({disabled: true});
	$("#jqxgrid").jqxGrid('showloadelement');
	$.ajax({
		url: 'createWorkingShift',
		data: dataSubmit,
		type: 'POST',
		success: function(response){
			$("#jqxNotification").jqxNotification('closeLast');
			if(response.responseMessage == 'success'){
				$('#containerNtf').empty();
				$('#jqxNotification').jqxNotification({template: 'info'});
				$("#notificationContent").text(response.successMessage);
				$("#jqxNotification").jqxNotification("open");
				$("#jqxgrid").jqxGrid('updatebounddata');
				globalVar.getAllWorkingShift = true;
			}else{
				$('#containerNtf').empty();
				$('#jqxNotification').jqxNotification({template: 'error'});
				$("#notificationContent").text(response.errorMessage);
				$("#jqxNotification").jqxNotification("open");
			}
		},
		complete:  function(jqXHR, textStatus){
			$("#jqxgrid").jqxGrid({disabled: false});
			$("#jqxgrid").jqxGrid('hideloadelement');
		}
	});
}

function initJqxValidator(){
	$("#popupAddRow").jqxValidator({
		rules:[
		  {
			input: "#workingShiftNameNew",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: 'required'
		  },
		  {
			input: "#shiftStartTimeNew",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: function (input, commit){
				if(!input.val()){
					return false;
				}
				return true;
			}
		  },
		  {
			input: "#shiftEndTimeNew",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: function (input, commit){
				if(!input.val()){
					return false;
				}
				return true;
			}
		  },
		  {
			  input: "#startOverTimeAfterShiftNew",
			  message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			  rule: function (input, commit){
				  if(!$("#allowOTAfterShift").val()){
					  return true;
				  }
				  if(!input.val()){
					  return false;
				  }
				  return true;
			  }
		  },
		  {
			  input: "#endOverTimeAfterShiftNew",
			  message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			  rule: function (input, commit){
				  if(!$("#allowOTAfterShift").val()){
					  return true;
				  }
				  if(!input.val()){
					  return false;
				  }
				  return true;
			  }
		  },
		  {
			  input: "#minMinuteOvertimeNew",
			  message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			  rule: function (input, commit){
				  if(!$("#allowOTAfterShift").val()){
					  return true;
				  }
				  if(input.val() == undefined){
					  return false;
				  }
				  return true;
			  }
		  }
		]
	});
	$("#workingShiftEditWindow").jqxValidator({
		rules:[
		  {
			input: "#workingShiftName",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: 'required'
		  },
		  {
			input: "#shiftStartTime",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: function (input, commit){
				if(!input.val()){
					return false;
				}
				return true;
			}
		  },
		  {
			input: "#shiftEndTime",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: function (input, commit){
				if(!input.val()){
					return false;
				}
				return true;
			}
		  },
		  {
			  input: "#startOverTimeAfterShift",
			  message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			  rule: function (input, commit){
				  if(!$("#allowOTAfterShiftEdit").val()){
					  return true;
				  }
				  if(!input.val()){
					  return false;
				  }
				  return true;
			  }
		  },
		  {
			  input: "#endOverTimeAfterShift",
			  message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			  rule: function (input, commit){
				  if(!$("#allowOTAfterShiftEdit").val()){
					  return true;
				  }
				  if(!input.val()){
					  return false;
				  }
				  return true;
			  }
		  },
		  {
			  input: "#minMinuteOvertime",
			  message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			  rule: function (input, commit){
				  if(!$("#allowOTAfterShiftEdit").val()){
					  return true;
				  }
				  if(input.val() == undefined){
					  return false;
				  }
				  return true;
			  }
		  }
		]
	});
}

function initJqxDropdownlist(){
	$("#workingShiftDropdownlist").jqxDropDownList({source: [], autoDropDownHeight: true, width: '97%', height: 25,  
		displayMember: "workingShiftName", valueMember: "workingShiftId",
	});
}

function initJqxCheckBox(){
	$("#allowOTAfterShift, #allowOTAfterShiftEdit").jqxCheckBox({theme: 'olbius', height: 25});
	$('#allowOTAfterShift').on('change', function (event) {
		$("#minMinuteOvertimeNew").jqxNumberInput({ disabled: !event.args.checked });
        $("#startOverTimeAfterShiftNew, #endOverTimeAfterShiftNew").jqxDateTimeInput({
     		disabled: !event.args.checked
     	});
     });
	 $("#allowOTAfterShiftEdit").on('change', function (event) {
		$("#minMinuteOvertime").jqxNumberInput({ disabled: !event.args.checked });
		$("#startOverTimeAfterShift, #endOverTimeAfterShift").jqxDateTimeInput({
			 disabled: !event.args.checked 
		});
	 });
}

function initJqxWindow(){
	$("#workingShiftEditWindow").jqxWindow({showCollapseButton: false,autoOpen: false,
		width: 850, height: 520, isModal: true, theme:'olbius',
		initContent: function(){
			
		}	
	});
	
	$("#workingShiftEditWindow").on('close', function(event){
		GridUtils.clearForm($(this));
	});
	
	$("#workingShiftEditWindow").on('open', function(event){
		var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		var data = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
		var tmpS = $("#jqxgridShiftWorkType").jqxGrid('source');
		tmpS._source.url = 'jqxGeneralServicer?sname=JQGetWorkingShiftDayWeek&hasrequest=Y&workingShiftId=' + data.workingShiftId;
		$("#jqxgridShiftWorkType").jqxGrid('source', tmpS);
		$("#workingShiftEditWindow").jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.WorkingShiftInfo)}: ' + data.workingShiftName);
		fillData(data);
	});
	
	$("#configPartyWorkingShiftWindow").jqxWindow({showCollapseButton: false,autoOpen: false,
		width: 850, height: 510, isModal: true, theme:'olbius',
		initContent: function(){
			
		}	
	});		
	
	$("#editConfigPartyWSWindow").jqxWindow({showCollapseButton: false,autoOpen: false,
		width: 350, height: 230, isModal: true, theme:'olbius',
		initContent: function(){
			
		}	
	});
	
	$("#editConfigPartyWSWindow").on('close', function(event){
		$("#configWSPartyId").val("");
		$("#configWSPartyName").val("");
		$("#workingShiftDropdownlist").jqxDropDownList('clearSelection');
	});
	
	$("#popupAddRow").jqxWindow({showCollapseButton: false,autoOpen: false,
		width: 850, height: 520, isModal: true, theme:'olbius',
		initContent: function(){
			
		}	
	});
	
	$("#popupAddRow").on('close', function(event){
		GridUtils.clearForm($(this));
		$("#popupAddRow").jqxValidator('hide');
	});
	
	$("#popupAddRow").on('open', function(event){
		var s = $("#jqxgridShiftWorkTypeNew").jqxGrid('source');
		s._source.localdata = generatedataWorkShiftTypeInWeek();
		$("#jqxgridShiftWorkTypeNew").jqxGrid('source', s);
	});
	
}

function fillData(data){	
	$("#workingShiftId").val(data.workingShiftId);
	$("#workingShiftName").val(data.workingShiftName);
	$("#minMinuteOvertime").val(data.minMinuteOvertime);
	$("#allowLateMinute").val(data.allowLateMinute);
	$("#shiftStartTime").val(data.shiftStartTime);
	var isAllowOTAfterShift = data.isAllowOTAfterShift;
	if("Y" == isAllowOTAfterShift){
		$("#allowOTAfterShiftEdit").jqxCheckBox({checked: true});
	}
	if(data.shiftBreakStart){
		$("#shiftBreakStartTime").val(data.shiftBreakStart);	
	}else{
		$("#shiftBreakStartTime").val(null);
	}
	if(data.shiftBreakEnd){
		$("#shiftBreakEndTime").val(data.shiftBreakEnd);
	}else{
		$("#shiftBreakEndTime").val(null);
	}
	$("#shiftEndTime").val(data.shiftEndTime);
	if(data.startOverTimeAfterShift){
		$("#startOverTimeAfterShift").val(data.startOverTimeAfterShift);
	}else{
		$("#startOverTimeAfterShift").val(null);
	}
	if(data.endOverTimeAfterShift){
		$("#endOverTimeAfterShift").val(data.endOverTimeAfterShift);
	}else{
		$("#endOverTimeAfterShift").val(null);
	}
}

function configPartyWorkingShift(){
	openJqxWindow($("#configPartyWorkingShiftWindow"));
}

function openJqxWindow(jqxWindowDiv){
	var wtmp = window;
	var tmpwidth = jqxWindowDiv.jqxWindow('width');
	jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
	jqxWindowDiv.jqxWindow('open');
}
</script>		