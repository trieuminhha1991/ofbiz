<@jqGridMinimumLib/>
<script src="/aceadmin/jqw/jqwidgets/jqxeditor.js" type="text/javascript"></script>
<#assign datafield = "[{name: 'workOvertimeRegisId', type: 'string'},
					   {name: 'dateRegistration', type: 'date'},
					   {name: 'overTimeFromDate', type: 'date'},
					   {name: 'overTimeThruDate', type: 'date'},
					   {name: 'reasonRegister', type: 'string'},
					   {name: 'statusId', type: 'string'}]"/>
					   
<script type="text/javascript">
var statusWorkOvertimeArr = [
	<#if statusListWorkOvertime?exists>
		<#list statusListWorkOvertime as status>
			{
				statusId: "${status.statusId}",
				description: "${StringUtil.wrapString(status.description)}"
			}
			<#if status_has_next>
			,
			</#if>
		</#list>
	</#if>                             
];
<#assign columnlist = "{datafield: 'workOvertimeRegisId', hidden: true},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonDate)}', datafield: 'dateRegistration', 'width': 150, 'cellsalign': 'left', 'editable': false, columntype: 'template', cellsformat: 'dd/MM/yyyy'},
						{text: '${StringUtil.wrapString(uiLabelMap.overTimeFromDate)}', datafield: 'overTimeFromDate', width: '150px',cellsformat: 'HH:mm:ss', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.overTimeThruDate)}', datafield: 'overTimeThruDate', width: '150px', cellsformat: 'HH:mm:ss', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.ReasonRegisterWorkOvertime)}', datafield: 'reasonRegister', width: '200px', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonStatus)}', datafield: 'statusId', 
							cellsrenderer: function(row, column, value){
								for(var i = 0; i < statusWorkOvertimeArr.length; i++){
									if(statusWorkOvertimeArr[i].statusId == value){
										return '<div style=\"margin-top: 4px; margin-left: 2px\">' + statusWorkOvertimeArr[i].description + '</div>';		
									}
								}
							}	
						}"/>
$(document).ready(function () {
	$("#popupAddRow").jqxWindow({
		width: 700, minWidth: 700, height: 380,  maxHeight:380, resizable: true, isModal: true, autoOpen: false, theme: 'olbius',
		initContent: function(){
			
		}
	});	
	$("#jqxNotification").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#jqxNotifyContainer"});
	$("#btnSave").click(function(){
		var row = {
			dateRegistration: $("#dateRegister").jqxDateTimeInput('getDate'),
			overTimeFromDate: $("#dateRegisterStartTime").jqxDateTimeInput('getDate'),
			overTimeThruDate: $("#dateRegisterEndTime").jqxDateTimeInput('getDate'),
			reasonRegister: $("#reasonRegister").val()
		}
		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
	});
	$("#dateRegister").jqxDateTimeInput({width: '200px', height: '25px', theme: 'olbius'});
	$("#dateRegisterStartTime").jqxDateTimeInput({width: '200px', height: '25px', theme: 'olbius', formatString: "HH:mm:ss", showCalendarButton: false});
	$("#dateRegisterEndTime").jqxDateTimeInput({width: '200px', height: '25px', theme: 'olbius', formatString: "HH:mm:ss", showCalendarButton: false});
	$("#reasonRegister").jqxEditor({
        width: '400px',
        tools: 'datetime | clear | backcolor | font | bold italic underline',
        toolbarPosition: 'bottom',
        theme: 'olbius'
    });
});
						
</script>					   
<@jqGrid url="jqxGeneralServicer?sname=JQGetEmplWorkOvertimeRegis" dataField=datafield columnlist=columnlist
	editable="false" 
	editrefresh ="true"
	editmode="click" id="jqxgrid"
	showtoolbar = "true" deleterow="true" jqGridMinimumLibEnable="false" 
	removeUrl="jqxGeneralServicer?sname=deleteEmplWorkOvertimeRegis&jqaction=D" deleteColumn="workOvertimeRegisId"
	createUrl="jqxGeneralServicer?jqaction=C&sname=createWorkOvertimeRegis" alternativeAddPopup="popupAddRow" addrow="true" addType="popup" 
	addColumns="dateRegistration(java.sql.Date);overTimeFromDate(java.sql.Time);overTimeThruDate(java.sql.Time);reasonRegister" 
	updateUrl=""
/>
<div class="row-fluid">
	<div class="row-fluid">
		<div id="jqxNotifyContainer">
			<div id="jqxNotification"></div>
		</div>
	</div>
</div>
<div class="row-fluid">
	<div id="popupAddRow" style="display: none;">
		<div>${uiLabelMap.HREmplWorkOvertimeRegis}</div>
		<div class="row-fluid">
			<form action="" class="form-horizontal">
				<div class="control-group">
					<label class="control-label">${uiLabelMap.HREmplOvertimeDateRegis}</label>
					<div class="controls">
						<div id="dateRegister"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.HREmplOverTimeFromDate}</label>
					<div class="controls">
						<div id="dateRegisterStartTime"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.HREmplOverTimeThruDate}</label>
					<div class="controls">
						<div id="dateRegisterEndTime"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">${uiLabelMap.ReasonRegisterWorkOvertime}</label>
					<div class="controls">
						<div id="reasonRegister"></div>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label">&nbsp;</label>
					<div class="controls">
						<button class="btn btn-small btn-primary icon-ok" id="btnSave" type="button">${uiLabelMap.CommonSave}</button>
						<button class="btn btn-small btn-danger icon-remove" id="btnCancel" type="button">${uiLabelMap.CommonCancel}</button>
					</div>
				</div>
			</form>
		</div>
	</div>
</div>
