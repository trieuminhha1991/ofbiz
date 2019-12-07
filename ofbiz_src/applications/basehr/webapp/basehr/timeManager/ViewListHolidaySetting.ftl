<#include "script/ViewListHolidaySettingScript.ftl"/>
<#assign datafield = "[{name: 'dateHoliday', type: 'date'},					   
					   {name: 'holidayName', type: 'string'},
					   {name: 'emplTimekeepingSignId', type: 'string'},					   
					   {name: 'dateInWeek', type: 'string'},					   
					   {name: 'sign', type: 'string'},					   
					   {name: 'description', type: 'string'},					   
					   {name: 'note', type: 'string'}]"/>
					   
<script type="text/javascript">

<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.HolidayName)}', datafield: 'holidayName', width: 230, editable: false},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonDate)}', datafield: 'dateHoliday', width: 140, editable: false, cellsformat: 'dd/MM/yyyy ', columntype: 'template', filterType : 'range'},
					   {text: '${StringUtil.wrapString(uiLabelMap.DayInWeek)}', datafield: 'dateInWeek', width: 150, editable: false, filterable : false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								if(data.dateHoliday != undefined){
									var dayInWeek = data.dateHoliday.getDay();
								}
								return '<span>' + dayInWeekArr[dayInWeek] +'</span>';
							},
					   },
					   {datafield: 'emplTimekeepingSignId', hidden: true},
					   {text: '${StringUtil.wrapString(uiLabelMap.HRCommonEmplTimekeepingSign)}', datafield: 'sign', width: 150, editable: false,filterType : 'checkedlist',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
							  	if(value){
							  		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
									var text = value + ' (' + data.description + ')';
									return '<span>' + text + '<span>';							  
							 	}
						   },
						   createfilterwidget : function(column, columnElement, widget){
							   var source = {
									   localdata : emplTimekeepingSignArr,
									   datatype : 'array'
							   };
							   var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
							   var dataFilter= filterBoxAdapter.records;
							   //dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
							   widget.jqxDropDownList({source : dataFilter, valueMember : 'emplTimekeepingSignId', displayMember : 'description'});
							   if(dataFilter.length <= 8){
								   widget.jqxDropDownList({autoDropDownHeight : true});
							   }else{
								   widget.jqxDropDownList({autoDropDownHeight : false});
							   }
						   }
					   },
					  
					   {text: '${StringUtil.wrapString(uiLabelMap.HRNotes)}', datafield: 'note', editable: false}" />
</script>

<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>
<#if security.hasEntityPermission("HR_TIMEMGR", "_ADMIN", session)>
	<#assign addrow="true">
	<#assign deleterow="true">
	<#assign customcontrol1="fa-database@${uiLabelMap.GetTheHolidays}@#javascript:void(0)@grabHolidayInYear()"/>
<#else>	
	<#assign addrow="false">
	<#assign deleterow="false">
	<#assign customcontrol1=""/>
</#if>
<#assign customcontrol = "icon-filter open-sans@${uiLabelMap.HRCommonRemoveFilter}@javascript: void(0);@RemoveFilter()">
<@jqGrid url="jqxGeneralServicer?sname=JQgetListHolidayInYear&hasrequest=Y" dataField=datafield columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" customControlAdvance="<div id='yearNumberInput' style='margin-right: 5px'></div>"
	filterable="true" 
	customcontrol1=customcontrol1 
	customcontrol2="icon-list-alt@${uiLabelMap.HolidayConfigList}@#javascript:void(0)@displayListHolidayConfig()" 
	customcontrol3=customcontrol
	jqGridMinimumLibEnable="false" 
	autorowheight="true" jqGridMinimumLibEnable="false" sortable="false"
	showtoolbar = "true" deleterow=deleterow filterable="true" clearfilteringbutton="false" showlist="false"
	updateUrl="" editColumns=""
	removeUrl="jqxGeneralServicer?sname=deleteHolidayInYear&jqaction=D" deleteColumn="dateHoliday(java.sql.Date)"
	createUrl="jqxGeneralServicer?sname=createHolidayYear&jqaction=C" alternativeAddPopup="popupAddRow" addrow=addrow addType="popup" 
	addColumns="dateHoliday(java.sql.Date);holidayName;note;emplTimekeepingSignId" editmode="selectedcell"/>
	
<div id="popupAddRow" class='hide'>
	<div id="popupWindowHeader">
	</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="asterisk">${uiLabelMap.HolidayName}</label>
				</div>
				<div class="span6">
					<input type="text" id="holidayName">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="asterisk">${uiLabelMap.DateHoliday}</label>
				</div>
				<div class="span6">
					<div id="dateHoliday"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="asterisk">${uiLabelMap.HRCommonEmplTimekeepingSign}</label>
				</div>
				<div class="span6">
					<div id="emplTimekeepingSignId"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="">${uiLabelMap.HRNotes}</label>
				</div>
				<div class="span6">
					<input type="text" id="note">
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="btnCancel" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSave">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>	
</div>
<div id="newHolidayConfigWindow" class="hide">
	<div id="popupWindowHeaderConfig">
		${StringUtil.wrapString(uiLabelMap.AddNewHolidayConfig)}
	</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class=" asterisk">${uiLabelMap.HolidayName}</label>
				</div>
				<div class="span7">
					<input type="text" id="holidayNameConfig">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class=" asterisk">${uiLabelMap.CommonDate}/${uiLabelMap.CommonMonth}</label>
				</div>
				<div class="span7">
					<div class="row-fluid">
						<div class="span4">
							<div id="dateHolidayConfig"></div>
						</div>						
						<div class="span4">
							<div id="monthHolidayConfig"></div>
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="asterisk">${uiLabelMap.HRCommonEmplTimekeepingSign}</label>
				</div>
				<div class="span7">
					<div id="emplTimekeepingSignIdConfig"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="">${uiLabelMap.LunarCalendar}</label>
				</div>
				<div class="span7">
					<div style="margin-left: 16px; margin-top: 3px">
						<div id="lunarCalendar"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button id="cancelHolidayConfig" class='btn btn-danger form-action-button pull-right'>
				<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveHolidayConfig">
				<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="holidayConfigListWindow" class="hide">
	<div id="popupWindowHeader">
		${uiLabelMap.HolidayConfigListInYear}
	</div>
	<div>
		<div class="row-fluid">
			<div id="containerjqxgridHolidayConfig" class="container-noti">
			</div>
			<div id="jqxNotificationjqxgridHolidayConfig">
				<div id="notificationContentjqxgridHolidayConfig"></div>
			</div>
		</div>
		<div id="jqxgridHolidayConfig"></div>	
	</div>
</div>

<script type="text/javascript" src="/hrresources/js/timeManager/ViewListHolidaySetting.js"></script>