<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField="[
	{name : 'recruitmentFormId', type : 'string'},
	{name : 'description',type : 'string'}
]"/>
<#assign columnlist="
			{text : '${uiLabelMap.CommonId}',dataField : 'recruitmentFormId',editable : false},
			{text : '${uiLabelMap.HRolbiusRecruitmentFormDescription}',dataField : 'description'}
"/>

<@jqGrid filtersimplemode="true"  deleterow="true" filterable="true" addrow="true"   addType="popup" addrefresh="true" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		editable="true" 
		 url="jqxGeneralServicer?sname=JQgetListRecruitmentForm"
		 createUrl="jqxGeneralServicer?sname=createRecruitmentForm&jqaction=C" addColumns="recruitmentFormId;description"
		 removeUrl="jqxGeneralServicer?sname=deleteRecruitmentForm&jqaction=D" deleteColumn="recruitmentFormId"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateRecruitmentForm"  editColumns="recruitmentFormId;description"
		/>	
<div id="alterpopupWindow" style="display : none;">
	<div>${uiLabelMap.CommonAdd} ${uiLabelMap.HRolbiusRecruitmentForm}</div>
	<div style="overflow: hidden;">
	<div>
		<form id="formAdd" class="form-horizontal">
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.CommonId}</label>
				<div class="controls">
					<input type="text" id="recruitmentFormAdd"/>
				</div>
			</div>
			
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.HRolbiusRecruitmentFormDescription} </label>
				<div class="controls">
					<input type="text" id="recruitmentFormDescriptionAdd"/>
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
</div>


<script type="text/javascript">
$.jqx.theme = 'olbius';
var theme = theme;

$('#alterpopupWindow').jqxWindow({ width: 500, height : 230,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7 });

	$('#recruitmentFormDescriptionAdd').jqxInput({width : '200px',height : '25px'});
	$('#recruitmentFormAdd').jqxInput({width : '200px',height : '25px'});
	$('#formAdd').jqxValidator({
			rules : [
				{input : '#recruitmentFormAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#recruitmentFormDescriptionAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'}
			]
	})
	$('#alterSave').click(function(){
		$('#formAdd').jqxValidator('validate');
	});
	$('#alterCancel').click(function(){
		$('#formAdd').trigger('reset');
	});
	$('#formAdd').on('validationSuccess',function(){
	var row = {};
	row = {
		recruitmentFormId : $('#recruitmentFormAdd').val(),
		description : $('#recruitmentFormDescriptionAdd').val()
	};
	 $("#jqxgrid").jqxGrid('addRow', null, row, "first");
	// select the first row and clear the selection.
    $("#jqxgrid").jqxGrid('clearSelection');                        
    $("#jqxgrid").jqxGrid('selectRow', 0);  
    $("#alterpopupWindow").jqxWindow('close');
    $('#formAdd').trigger('reset');
	});
</script>		
			