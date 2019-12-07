<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField="[
	{name : 'description', type : 'string'},
	{name : 'startTime',type : 'date',other : 'Date'},
	{name : 'endTime',type : 'date',other : 'Date'}
]"/>
<#assign columnlist="
			{text : '${uiLabelMap.WorkingShiftName}',dataField : 'description',filterable :false},
			{text : '${uiLabelMap.StartTime}',dataField : 'startTime',filterable :false,columntype : 'datetimeinput',cellsformat : 'HH:mm:ss'},
			{text : '${uiLabelMap.EndTime}',dataField : 'endTime',filterable :false,columntype : 'datetimeinput',cellsformat : 'HH:mm:ss'}
"/>

<@jqGrid filtersimplemode="true"  deleterow="true" filterable="true" addrow="true"   addType="popup" addrefresh="true" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		editable="true" 
		 url="jqxGeneralServicer?sname=JQgetListWorkingShift"
		 createUrl="jqxGeneralServicer?sname=createWorkingShiftHR&jqaction=C" addColumns="description;startTime;endTime"
		 removeUrl="jqxGeneralServicer?sname=deleteWorkingShift&jqaction=D" deleteColumn="workingShiftId"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateWorkingShift"  editColumns="workingShiftId;description;startTime;endTime"
		/>	
<div id="alterpopupWindow" style="display : none;">
	<div>${uiLabelMap.AddWorkingShift}</div>
	<div style="overflow: hidden;">
		<form id="formAdd" class="form-horizontal">
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.WorkingShiftName}</label>
				<div class="controls">
					<input type="text" id="descriptionAdd"/>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.StartTime} </label>
				<div class="controls">
					<div id="startTimeAdd"></div>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.EndTime}</label>
				<div class="controls">
					<div id="endTimeAdd"></div>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label">&nbsp;</label>
				<div class="controls">
					<button type="button" class='btn btn-primary btn-mini' style="margin-right: 5px; margin-top: 10px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
					<button type="button" class='btn btn-danger btn-mini' style="margin-right: 5px; margin-top: 10px;" id="alterCancel"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
				</div>
			</div>
		</form>
	</div>
</div>


<script type="text/javascript">
$.jqx.theme = 'olbius';
var theme = theme;

$('#alterpopupWindow').jqxWindow({ width: 500, height : 230,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7 });

	$('#descriptionAdd').jqxInput({width : '200px',height : '25px'});
	$('#startTimeAdd').jqxDateTimeInput({width  :'200px',height : '25px',showCalendarButton : false,formatString : 'HH:mm:ss'});
	$('#endTimeAdd').jqxDateTimeInput({width  :'200px',height : '25px',showCalendarButton : false,formatString : 'HH:mm:ss'});
	$('#formAdd').jqxValidator({
			rules : [
				{input : '#descriptionAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#startTimeAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#endTimeAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'}
			]
	});
	$('#alterSave').click(function(){
		$('#formAdd').jqxValidator('validate');
	});
	$('#formAdd').on('validationSuccess',function(){
	var row = {};
	row = {
		description : $('#descriptionAdd').val(),
		startTime : $('#startTimeAdd').val(),
		endTime : $('#endTimeAdd').val()
	};
	 $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	// select the first row and clear the selection.
    $("#jqxgrid").jqxGrid('clearSelection');                        
    $("#jqxgrid").jqxGrid('selectRow', 0);  
    $("#alterpopupWindow").jqxWindow('close');
	});
	$('#alterpopupWindow').on('close',function(){
		$('#formAdd').trigger('reset');
	});
</script>		
			