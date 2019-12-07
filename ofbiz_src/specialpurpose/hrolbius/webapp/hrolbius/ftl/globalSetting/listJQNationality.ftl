<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField="[
	{name : 'nationalityId', type : 'string'},
	{name : 'description',type : 'string'}
]"/>
<#assign columnlist="
			{text : '${uiLabelMap.CommonId}',dataField : 'nationalityId',width : '250px',editable : false},
			{text : '${uiLabelMap.CommonDescription}',dataField : 'description',filterable :false}
"/>

<@jqGrid filtersimplemode="true"  deleterow="true" filterable="true" addrow="true"   addType="popup" addrefresh="true" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		editable="true" 
		 url="jqxGeneralServicer?sname=JQgetListNationality"
		 createUrl="jqxGeneralServicer?sname=createNationality&jqaction=C" addColumns="nationalityId;description"
		 removeUrl="jqxGeneralServicer?sname=deleteNationality&jqaction=D" deleteColumn="nationalityId"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateNationality"  editColumns="nationalityId;description"
		/>	
<div id="alterpopupWindow" style="display : none;">
	<div>${uiLabelMap.CommonAdd} ${uiLabelMap.Nationality}</div>
	<div style="overflow: hidden;">
		<form id="formAdd" class="form-horizontal">
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.CommonId}</label>
				<div class="controls">
					<input type="text" id="nationalityIdAdd"/>
				</div>
			</div>
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.CommonDescription} </label>
				<div class="controls">
					<input type="text" id="descriptionAdd"/>
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

	$('#nationalityIdAdd').jqxInput({width : '200px',height : '25px'});
	$('#descriptionAdd').jqxInput({width : '200px',height : '25px'});
	$('#formAdd').jqxValidator({
			rules : [
				{input : '#nationalityIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#descriptionAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'}
			]
	})
	$('#alterSave').click(function(){
		$('#formAdd').jqxValidator('validate');
	});
	$('#formAdd').on('validationSuccess',function(){
	var row = {};
	row = {
		nationalityId : $('#nationalityIdAdd').val(),
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
			