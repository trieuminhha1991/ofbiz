<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<#assign datafield = "[{name: 'dateHoliday', type: 'date'},					   
					   {name: 'holidayName', type: 'string'},
					   {name: 'emplTimekeepingSignId', type: 'string'},					   
					   {name: 'dateInWeek', type: 'string'},					   
					   {name: 'note', type: 'string'}]"/>
					   
<script type="text/javascript">
var dayInWeekArr = ["${StringUtil.wrapString(uiLabelMap.wgsunday)}",
                    "${StringUtil.wrapString(uiLabelMap.wgmonday)}",
                    "${StringUtil.wrapString(uiLabelMap.wgtuesday)}",
                    "${StringUtil.wrapString(uiLabelMap.wgwednesday)}",
                    "${StringUtil.wrapString(uiLabelMap.wgthursday)}",
                    "${StringUtil.wrapString(uiLabelMap.wgfriday)}",
                    "${StringUtil.wrapString(uiLabelMap.wgsaturday)}"];
                    
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.HolidayName)}', datafield: 'holidayName', width: 230, editable: false},
					   {text: '${StringUtil.wrapString(uiLabelMap.CommonDate)}', datafield: 'dateHoliday', width: 140, editable: false, cellsformat: 'dd/MM/yyyy ', columntype: 'template'},
					   {text: '${StringUtil.wrapString(uiLabelMap.DayInWeek)}', datafield: 'dateInWeek', width: 150, editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								var dayInWeek = data.dateHoliday.getDay();
								return '<span>' + dayInWeekArr[dayInWeek] +'</span>';
							}					   	
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.HRCommonEmplTimekeepingSign)}', datafield: 'emplTimekeepingSignId', width: 150, editable: false},
					  
					   {text: '${StringUtil.wrapString(uiLabelMap.HRNotes)}', datafield: 'note', editable: false}" />
</script>

<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>
<@jqGrid url="jqxGeneralServicer?sname=JQgetListHolidayInYear&hasrequest=Y" dataField=datafield columnlist=columnlist
	clearfilteringbutton="true"
	editable="true" customControlAdvance="<div id='yearNumberInput' style='margin-right: 5px'></div>"
	filterable="false" 
	customcontrol1="fa-database@${uiLabelMap.GetTheHolidays}@#javascript:void(0)@grabHolidayInYear()" 
	customcontrol2="icon-list-alt@${uiLabelMap.HolidayConfigList}@#javascript:void(0)@displayListHolidayConfig()" 
	jqGridMinimumLibEnable="false" 
	autorowheight="true" jqGridMinimumLibEnable="false" sortable="false"
	showtoolbar = "true" deleterow="true" filterable="false" clearfilteringbutton="false" showlist="false"
	updateUrl="" editColumns=""
	removeUrl="jqxGeneralServicer?sname=deleteHolidayInYear&jqaction=D" deleteColumn="dateHoliday(java.sql.Date)"
	createUrl="jqxGeneralServicer?sname=createHolidayYear&jqaction=C" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
	addColumns="dateHoliday(java.sql.Date);holidayName;note;emplTimekeepingSignId" editmode="selectedcell"/>
<#assign cal = Static["java.util.Calendar"].getInstance()/>	
<div id="popupAddRow" class='hide'>
	<div id="popupWindowHeader">
	</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="control-label asterisk">${uiLabelMap.HolidayName}</label>
				</div>
				<div class="span6">
					<input type="text" id="holidayName">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="control-label asterisk">${uiLabelMap.DateHoliday}</label>
				</div>
				<div class="span6">
					<div id="dateHoliday"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="control-label">${uiLabelMap.HRCommonEmplTimekeepingSign}</label>
				</div>
				<div class="span6">
					<div id="emplTimekeepingSignId"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span6 text-algin-right">
					<label class="control-label">${uiLabelMap.HRNotes}</label>
				</div>
				<div class="span6">
					<input type="text" id="note">
				</div>
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
<div id="newHolidayConfigWindow" class="hide">
	<div id="popupWindowHeaderConfig">
		${StringUtil.wrapString(uiLabelMap.AddNewHolidayConfig)}
	</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="control-label asterisk">${uiLabelMap.HolidayName}</label>
				</div>
				<div class="span7">
					<input type="text" id="holidayNameConfig">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="control-label asterisk">${uiLabelMap.CommonDate}/${uiLabelMap.CommonMonth}</label>
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
					<label class="control-label">${uiLabelMap.HRCommonEmplTimekeepingSign}</label>
				</div>
				<div class="span7">
					<div id="emplTimekeepingSignIdConfig"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span5 text-algin-right">
					<label class="control-label">${uiLabelMap.LunarCalendar}</label>
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
				<i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
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
			<div id="containerjqxgridHolidayConfig">
			</div>
			<div id="jqxNotificationjqxgridHolidayConfig">
				<div id="notificationContentjqxgridHolidayConfig"></div>
			</div>
			
		</div>
		<div id="jqxgridHolidayConfig"></div>	
	</div>
</div>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<script type="text/javascript">
var emplTimekeepingSignArr = [
	<#if emplTimekeepingSignList?has_content>
		<#list emplTimekeepingSignList as emplTimekeepingSign>
			{
				emplTimekeepingSignId: '${emplTimekeepingSign.emplTimekeepingSignId}',
				description: '${StringUtil.wrapString(emplTimekeepingSign.description?if_exists)}',
				sign: '${emplTimekeepingSign.sign}'
			},
		</#list>
	</#if>
];
$(document).ready(function () {
	$("#jqxgrid").on('loadCustomControlAdvance', function(event){
		$("#yearNumberInput").jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
			digits: 4, spinMode: 'simple',  inputMode: 'simple'});
		$("#yearNumberInput").val(${cal.get(Static["java.util.Calendar"].YEAR)})
		$("#yearNumberInput").on('valueChanged', function(event){
			var value = event.args.value;
			var source = $("#jqxgrid").jqxGrid('source');
			source._source.url = 'jqxGeneralServicer?sname=JQgetListHolidayInYear&hasrequest=Y&year=' + value;
			$("#jqxgrid").jqxGrid('source', source);
		});
	});
	initJqxNotification();	
	initJqxInput();
	initjqxDateTimeInput();
	initJqxDropdownlist();
	initJqxValidator();
	initBtnEvent();
	initJqxNumberInput();
	initJqxCheckBox();
	initJqxGrid();
	initJqxWindow();
});

function initJqxInput(){
	$("#holidayName").jqxInput({width: '94%', height: 20, theme: 'olbius'});
	$("#note").jqxInput({width: '94%', height: 20, theme: 'olbius'});
	$("#holidayNameConfig").jqxInput({width: '94%', height: 20, theme: 'olbius'});
}

function initjqxDateTimeInput(){
	$("#dateHoliday").jqxDateTimeInput({ width: '96%', height: '25px', theme: 'olbius'});	
}

function initJqxDropdownlist(){
	var source = {
	        localdata: emplTimekeepingSignArr,
	        datatype: "array"
    };
	var dataAdapter = new $.jqx.dataAdapter(source);
	$("#emplTimekeepingSignId, #emplTimekeepingSignIdConfig").jqxDropDownList({source: dataAdapter, displayMember: "sign", valueMember: "emplTimekeepingSignId", theme: 'olbius', width: '96%', height: 25});
}

function initJqxValidator(){
	$("#popupAddRow").jqxValidator({
		rules:[
		  {
			input: "#holidayName",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: 'required'
		  },
		  {
			input: "#dateHoliday",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: function (input, commit){
				if(!input.val()){
					return false;
				}
				return true;
			}
		  }
		]
	});
	
	$("#newHolidayConfigWindow").jqxValidator({
		rules: [
			{
				input: '#holidayNameConfig',
				message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
				rule: 'required'
			}		        
		]
	});
}

function initBtnEvent(){
	$("#btnCancel").click(function(event){
		$("#popupAddRow").jqxWindow('close');
	});
	
	$("#btnSave").click(function(event){
		var valid = $("#popupAddRow").jqxValidator('validate');
		if(!valid){
			return;
		}
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.CreateHolidayConfirm)}?",
			 [
			 {
    		    "label" : "${uiLabelMap.CommonSubmit}",
    		    "class" : "btn-primary btn-mini icon-ok",
    		    "callback": function() {
    		    	createHoliday();	    		
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
	
	$("#saveHolidayConfig").click(function(event){
		var valid = $("#newHolidayConfigWindow").jqxValidator('validate');
		if(!valid){
			return;
		}
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.HrCreateNewConfirm)}?",
			 [
			 {
	   		    "label" : "${uiLabelMap.CommonSubmit}",
	   		    "class" : "btn-primary btn-mini icon-ok",
	   		    "callback": function() {
	   		    	createNewHolidayConfig();	    			
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
	
	$("#cancelHolidayConfig").click(function(event){
		$("#newHolidayConfigWindow").jqxWindow('close');
	});
}

function createNewHolidayConfig(){
	var lunarCalendarCheck = $("#lunarCalendar").val();	
	var row = {
			description: $("#holidayNameConfig").val(),
			emplTimekeepingSignId: $("#emplTimekeepingSignIdConfig").val(),
			dateOfMonth: $("#dateHolidayConfig").val(),
			month:$("#monthHolidayConfig").val(),
			lunarCalendar: $("#lunarCalendar").val()	
	}
	$("#jqxgridHolidayConfig").jqxGrid({disabled: true});
	$("#jqxgridHolidayConfig").jqxGrid('showloadelement');
	$("#newHolidayConfigWindow").jqxWindow('close');
	$.ajax({
		url: 'createHolidayConfig',
		data: row,
		type: 'POST',
		success: function(response){
			$("#jqxNotificationjqxgridHolidayConfig").jqxNotification('closeLast');
			if(response.responseMessage == 'success'){
				$("#notificationContentjqxgridHolidayConfig").text(response.successMessage);
				$("#jqxNotificationjqxgridHolidayConfig").jqxNotification({template: 'info'});
				$("#jqxNotificationjqxgridHolidayConfig").jqxNotification("open");
				$("#jqxgridHolidayConfig").jqxGrid('updatebounddata');
			}else{
				$("#notificationContentjqxgridHolidayConfig").text(response.errorMessage);
				$("#jqxNotificationjqxgridHolidayConfig").jqxNotification({template: 'error'});
				$("#jqxNotificationjqxgridHolidayConfig").jqxNotification("open");
			}
		},
		complete:  function(jqXHR, textStatus){
			$("#jqxgridHolidayConfig").jqxGrid({disabled: false});
			$("#jqxgridHolidayConfig").jqxGrid('hideloadelement');
		}
	});
}

function createHoliday(){
	var row = {
			holidayName: $("#holidayName").val(),
			dateHoliday: $("#dateHoliday").jqxDateTimeInput('val', 'date'),
			emplTimekeepingSignId: $("#emplTimekeepingSignId").val(),
			note: $("#note").val()
	}
	$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	$("#popupAddRow").jqxWindow('close');
}

function initJqxNumberInput(){
	$("#dateHolidayConfig").jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
		digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 31});
	$("#monthHolidayConfig").jqxNumberInput({width: 65, height: 25, spinButtons: true, decimalDigits:0, groupSeparator: '', 
		digits: 2, spinMode: 'simple',  inputMode: 'simple', min: 1, max: 12});
}

function initJqxCheckBox(){
	 $('#lunarCalendar').jqxCheckBox({height: 25, theme: 'olbius'});
}

function initJqxGrid(){
	var datafield =  [
	          		{name: 'holidayConfigId', type: 'string'},
	          		{name: 'emplTimekeepingSignId', type: 'string'},
	          		{name: 'description', type: 'string'},
	          		{name: 'calendarType', type: 'bool'},
	          		{name: 'dateOfMonth', type: 'number'},
	          		{name: 'month', type: 'number'},
   	];
   	var columnlist = [
		{datafield: 'holidayConfigId' , editable: false, hidden: true},
   	  	{text: '${StringUtil.wrapString(uiLabelMap.HolidayName)}', datafield: 'description', editable: false, width: 220, filterable: false},
   	  	{datafield: 'month', editable: false, hidden: true,filterable: false},
   	  	{text: '${StringUtil.wrapString(uiLabelMap.DateHoliday)}', datafield: 'dateOfMonth', editable: false, width: 100, cellsalign: 'left', filterable: false,
	   	  	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
	   	  		var data = $('#jqxgridHolidayConfig').jqxGrid('getrowdata', row);
	   	  		var month = data.month;
	   	  		if(value < 10){
	   	  			value = "0" + value;
	   	  		}
	   	  		if(month < 10){
	   	  			month = "0" + month;
	   	  		}
	   	  		return '<span>' + value + '/' + month +'</span>';
	   	  	}	
   	  	},
   	  	{text: '${uiLabelMap.HRCommonEmplTimekeepingSignShort}', datafield: 'emplTimekeepingSignId', editable: false, width: 100, filterable: false},
   	 	{text: '${StringUtil.wrapString(uiLabelMap.LunarCalendar)}', datafield: 'calendarType', editable: false,  columntype: 'checkbox'}   	  
   	];
   	
   	var rendertoolbar = function (toolbar){
		toolbar.html('');
        var me = this;
        var jqxheader = $("<div id='toolbarcontainer' class='widget-header'><h4>" + "</h4><div id='toolbarButtonContainer' class='pull-right'></div></div>");
     	toolbar.append(jqxheader);
        var container = $('#toolbarButtonContainer');
     	var button = $('<button id="addrowbutton" style="margin-left:20px; cursor: pointer"><i class="icon-plus-sign"></i>${uiLabelMap.accAddNewRow}</button>')
       	var delBtn = $('<button style="margin-left: 20px; cursor: pointer" id="deleterowbutton"><i class="icon-trash"></i>${uiLabelMap.accDeleteSelectedRow}</button>');
     	container.append(button);        
     	container.append(delBtn);
        button.jqxButton();
        delBtn.jqxButton();
        button.on('click', function () { 
        	openJqxWindow($("#newHolidayConfigWindow"));
        });
        delBtn.on('click', function(event){
        	confirmHolidayConfig();
        });
   	}
   	
   	var config = {
   		width: '100%', 
   		height: 355,
   		autoheight: false,
   		virtualmode: true,
   		showfilterrow: true,
   		showtoolbar: true,
   		rendertoolbar: rendertoolbar,
   		selectionmode: 'singlerow',
   		pageable: true,
   		sortable: false,
        filterable: false,
        editable: false,
        rowsheight: 26,
        selectionmode: 'singlerow',
        url: 'JQGetListHolidayConfig&hasrequest=Y',                
        source: {pagesize: 10, removeUrl: 'deleteHolidayConfig', deleteColumns: 'holidayConfigId'}
   	};
   	GridUtils.initGrid(config, datafield, columnlist, null, $("#jqxgridHolidayConfig"));
}

function confirmHolidayConfig(){
	var selectIndex = $("#jqxgridHolidayConfig").jqxGrid('getselectedrowindex');
	if(selectIndex > -1){
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.NotifyDelete)}",
				 [
				 {
		   		    "label" : "${uiLabelMap.CommonSubmit}",
		   		    "class" : "btn-primary btn-mini icon-ok",
		   		    "callback": function() {
		   		    	deleteHolidayConfig(selectIndex, $("#jqxgridHolidayConfig"));	    			
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
	}
}

function deleteHolidayConfig(selectIndex, gridEle){
	var id = gridEle.jqxGrid('getrowid', selectIndex);
	gridEle.jqxGrid('deleterow', id);
}

function initJqxWindow(){
	$("#popupAddRow").jqxWindow({showCollapseButton: false,autoOpen: false,
		width: 450, height: 270, isModal: true, theme:'olbius',
		initContent: function(){
			
		}	
	});
	$("#popupAddRow").on('open', function(event){
		var year = $("#yearNumberInput").val();
		var startYear = new Date(year, 0, 1, 0, 0, 0, 0);
		var endYear =  new Date(year, 11, 31, 0, 0, 0, 0);
		var nowDate = new Date(${nowTimestamp.getTime()});
		var currMonth = nowDate.getMonth();
		var currDate = nowDate.getDate();
		var selectDate = new Date(year, currMonth, currDate, 0, 0, 0, 0);
		$("#dateHoliday").jqxDateTimeInput({min: startYear, max: endYear});
		$("#dateHoliday").val(selectDate);
		$("#popupAddRow").jqxWindow('setTitle', '${StringUtil.wrapString(uiLabelMap.AddHolidayInYear)} ' + year);
	});
	
	$("#popupAddRow").on('close', function(event){
		$("#popupAddRow").jqxValidator('hide');
		GridUtils.clearForm($(this));
	});
	
	$("#holidayConfigListWindow").jqxWindow({showCollapseButton: false,autoOpen: false,
		width: 600, height: 410, isModal: true, theme:'olbius',
		initContent: function(){
			
		}	
	});
	$("#newHolidayConfigWindow").jqxWindow({showCollapseButton: false,autoOpen: false,
		width: 500, height: 260, isModal: true, theme:'olbius',
		initContent: function(){
			
		}	
	});
	$("#newHolidayConfigWindow").on('open', function(event){
		var date = new Date(${nowTimestamp.getTime()});
		$("#dateHolidayConfig").val(date.getDate());
		$("#monthHolidayConfig").val(date.getMonth() + 1);
	});
	
	$("#newHolidayConfigWindow").on('close', function(event){
		$("#newHolidayConfigWindow").jqxValidator('hide');
		GridUtils.clearForm($(this));
	});
}

function initJqxNotification(){
	$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#notifyContainer"});
	$("#jqxNotificationjqxgridHolidayConfig").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#containerjqxgridHolidayConfig"});
}

function grabHolidayInYear(){
	var year = $("#yearNumberInput").val();
	$("#jqxgrid").jqxGrid({disabled: true});
	$("#jqxgrid").jqxGrid('showloadelement');
	$.ajax({
		url: 'createOrUpdateHolidayInYear',
		data: {year: year},
		type: 'POST',
		success: function(response){
			if(response._EVENT_MESSAGE_){
				var source = $("#jqxgrid").jqxGrid('source');
				source._source.url = 'jqxGeneralServicer?sname=JQgetListHolidayInYear&hasrequest=Y&year=' + year;
				$("#jqxgrid").jqxGrid('source', source);
			}else{
				$("#jqxNtfContent").text(response._ERROR_MESSAGE_);
				$("#jqxNtf").jqxNotification({template: 'error'});
				$("#jqxNtf").jqxNotification("open");
			}
		},
		complete:  function(jqXHR, textStatus){
			$("#jqxgrid").jqxGrid({disabled: false});
			$("#jqxgrid").jqxGrid('hideloadelement');
		}
	});
}

function displayListHolidayConfig(){
	openJqxWindow($("#holidayConfigListWindow"));
}

function openJqxWindow(jqxWindowDiv){
	var wtmp = window;
	var tmpwidth = jqxWindowDiv.jqxWindow('width');
	jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
	jqxWindowDiv.jqxWindow('open');
}
</script>