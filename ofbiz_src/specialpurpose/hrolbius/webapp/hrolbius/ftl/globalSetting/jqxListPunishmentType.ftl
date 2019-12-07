<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField="[
	{name : 'punishmentTypeId', type : 'string'},
	{name : 'description',type : 'string'},
	{name : 'numberRemindToWarning', type : 'number' }
]"/>

<#assign columnlist="
			{text : '${uiLabelMap.CommonId}',dataField : 'punishmentTypeId', width : '400px', editable : false },
			{text : '${uiLabelMap.HROlbiusTypeProposeDescription}',dataField : 'description', width : '400px' },
			{text : '${uiLabelMap.NumberRemindToWarning}',dataField : 'numberRemindToWarning',cellsalign: 'center', columntype:'numberinput', filtertype: 'number'}
"/>

<@jqGrid filtersimplemode="true"  deleterow="true" filterable="true" addrow="true" addType="popup" addrefresh="true" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		editable="true" 
		 url="jqxGeneralServicer?sname=JQgetListPunishmentType"
		 createUrl="jqxGeneralServicer?sname=createPunishmentType&jqaction=C" addColumns="punishmentTypeId;description;numberRemindToWarning"
		 removeUrl="jqxGeneralServicer?sname=deletePunishmentType&jqaction=D" deleteColumn="punishmentTypeId"
		 updateUrl="jqxGeneralServicer?sname=updatePunishmentType&jqaction=U" editColumns="punishmentTypeId;description;numberRemindToWarning"
		/>	


<div id="alterpopupWindow" style="display : none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div style="overflow: hidden;">
		<form id="formAdd" class="form-horizontal">
			<div class="row-fluid no-left-margin">
				<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.CommonId}</div>
				<div class="span4">
					<input type="text" id="PunishmentIdAdd"/>
				</div>
			</div>
			
			<div class="row-fluid no-left-margin">
				<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.HROlbiusTypeProposeDescription}</div>
				<div class="span4">
					<input type="text" id="descriptionAdd"/>
				</div>
			</div>
			
			<div class="row-fluid no-left-margin">
				<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.NumberRemindToWarning}</div>
				<div class="span4">
					<input type="text" id="numberRemindToWarningAdd"/>
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

$('#alterpopupWindow').jqxWindow({ width: 400, height : 200,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7 });

	$('#PunishmentIdAdd').jqxInput({width : '200px',height : '20px'});
	$('#descriptionAdd').jqxInput({width : '200px',height : '20px'});
	$('#numberRemindToWarningAdd').jqxInput({width : '200px',height : '20px'});
	$('#formAdd').jqxValidator({
			rules : [
				{input : '#PunishmentIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#descriptionAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#numberRemindToWarningAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{
                input: "#numberRemindToWarningAdd", 
                message: "${StringUtil.wrapString(uiLabelMap.NumberRequired?default(''))}", 
                action: 'change', 
                rule: function (input, commit) {
                	var val = $("#numberRemindToWarningAdd").val();
                    return isNaN(val) ? false : true;
                }
            	},
			]
	})
	$('#alterSave').click(function(){
		$('#formAdd').jqxValidator('validate');
	});
	$('#formAdd').on('validationSuccess',function(){
	var row = {};
	row = {
		punishmentTypeId : $('#PunishmentIdAdd').val(),
		description : $('#descriptionAdd').val(),
		numberRemindToWarning : $('#numberRemindToWarningAdd').val()
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
			