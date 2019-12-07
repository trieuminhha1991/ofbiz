<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#assign dataField="[
	{name : 'leaveTypeId', type : 'string'},
	{name : 'description',type : 'string'}
]"/>

<#assign columnlist="
			{text : '${uiLabelMap.CommonId}',dataField : 'leaveTypeId', width : '250px', editable : false},
			{text : '${uiLabelMap.CommonDescription}',dataField : 'description'}
"/>

<@jqGrid filtersimplemode="true"  deleterow="true" filterable="true" addrow="true" addType="popup" addrefresh="true" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		editable="true" 
		 url="jqxGeneralServicer?sname=JQgetListEmplLeaveType"
		 createUrl="jqxGeneralServicer?sname=createEmplLeaveType&jqaction=C" addColumns="leaveTypeId;description"
		 removeUrl="jqxGeneralServicer?sname=deleteEmplLeaveType&jqaction=D" deleteColumn="leaveTypeId"
		 updateUrl="jqxGeneralServicer?sname=updateEmplLeaveType&jqaction=U" editColumns="leaveTypeId;description"
		/>	


<div id="alterpopupWindow" style="display : none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div style="overflow: hidden;">
		<form id="formAdd" class="form-horizontal jqx-window-header">
			<div class="row-fluid no-left-margin">
				<div class="span3 asterisk" style="text-align:right;">${uiLabelMap.CommonId}</div>
				<div class="span4">
					<input type="text" id="EmplLeaveTypeIdAdd"/>
				</div>
			</div>
			
			<div class="row-fluid no-left-margin">
				<div class="span3 asterisk" style="text-align:right;">${uiLabelMap.CommonDescription} </div>
				<div class="span4">
					<input type="text" id="descriptionAdd"/>
				</div>
			</div>
			
			<div class="control-group no-left-margin">
				<div class="" style="width:166px;margin:0 auto;">
					<button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" style="margin-right: 5px; margin-top: 10px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
					<button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" style="margin-right: 5px; margin-top: 10px;" id="alterCancel"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
				</div>
			</div>
		</form>
	</div>
</div>


<script type="text/javascript">
$.jqx.theme = 'olbius';
var theme = theme;

$('#alterpopupWindow').jqxWindow({ width: 400, height : 150, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7 });

	$('#EmplLeaveTypeIdAdd').jqxInput({width : '200px',height : '20px'});
	$('#descriptionAdd').jqxInput({width : '200px',height : '20px'});
	$('#formAdd').jqxValidator({
			rules : [
				{input : '#EmplLeaveTypeIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#descriptionAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'}
			]
	})
	$('#alterSave').click(function(){
		$('#formAdd').jqxValidator('validate');
	});
	$('#formAdd').on('validationSuccess',function(){
	var row = {};
	row = {
		leaveTypeId : $('#EmplLeaveTypeIdAdd').val(),
		description : $('#descriptionAdd').val()
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
			