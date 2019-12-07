<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<#assign dataField="[
	{name : 'emplPositionTypeId', type : 'string'},
	{name : 'description',type : 'string'}
]"/>
<#assign columnlist="
			{text : '${uiLabelMap.CommonId}',dataField : 'emplPositionTypeId',width : '250px',editable : false,cellsrenderer : 
				function(row,columnfield,value){
					var data = $(\"#jqxgrid\").jqxGrid('getrowdata',row);
					return '<a href=\"EditEmplPositionTypes?emplPositionTypeId='+ data.emplPositionTypeId +'\">'+ data.emplPositionTypeId +'</a>';
				}
			},
			{text : '${uiLabelMap.CommonDescription}',dataField : 'description',filterable :false}
"/>

<@jqGrid filtersimplemode="true"  deleterow="true" filterable="true" addrow="true"   addType="popup" addrefresh="true" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		editable="true" 
		 url="jqxGeneralServicer?sname=JQgetListEmplPositionTypes"
		 createUrl="jqxGeneralServicer?sname=createEmplPositionType&jqaction=C" addColumns="emplPositionTypeId;parentTypeId;description;dayLeaveRegulation"
		 removeUrl="jqxGeneralServicer?sname=deleteEmplPositionType&jqaction=D" deleteColumn="emplPositionTypeId"
		 updateUrl="jqxGeneralServicer?jqaction=U&sname=updateEmplPositionType"  editColumns="emplPositionTypeId;parentTypeId;description;dayLeaveRegulation"
		/>	
<div id="alterpopupWindow" style="display : none;">
	<div>${uiLabelMap.HROlbiusNewEmplPositionType}</div>
	<div style="overflow: hidden;">
		<form id="formAdd" class="form-horizontal">
			<input type="hidden" name="hasTable" value="N">				
			<div class="row-fluid">
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.EmplPositionTypeId}</label>
					<div class="controls">
						<input type="text" name="emplPositionTypeIdAdd" id="emplPositionTypeIdAdd" class="required">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.FormFieldTitle_parentTypeId}</label>
					<div class="controls">
						<input type="text" name="parentTypeIdAdd" id="parentTypeIdAdd" >
					</div>
				</div>					
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.CommonDescription}</label>
					<div class="controls">
						<input type="text" name="description" id="descriptionAdd" class="required">
					</div>
				</div>
				<div class="control-group no-left-margin">
					<label class="control-label asterisk">${uiLabelMap.DayLeaveRegulation}</label>
					<div class="controls">
						<input type="text" name="dayLeaveRegulation" id="dayLeaveRegulationAdd" class="required">
					</div>
				</div>
				<div class="control-group">
					<div class="control-group no-left-margin">
						<label class="control-label">&nbsp;</label>
						<div class="controls">
							<button type="button" class='btn btn-primary btn-mini' style="margin-right: 5px; margin-top: 10px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
							<button type="button" class='btn btn-danger btn-mini' style="margin-right: 5px; margin-top: 10px;" id="alterCancel"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
						</div>
					</div>
				</div>
			</div>	
		</form>
	</div>
</div>


<script type="text/javascript">
$.jqx.theme = 'olbius';
var theme = theme;
	
$('#alterpopupWindow').jqxWindow({ width: 500, height : 300,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7 });

	$('#emplPositionTypeIdAdd').jqxInput({width : '200px',height : '25px'});
	$('#descriptionAdd').jqxInput({width : '200px',height : '25px'});
	$('#dayLeaveRegulationAdd').jqxInput({width : '200px',height : '25px'});
	$('#parentTypeIdAdd').jqxInput({width : '200px',height : '25px'});
	$('#formAdd').jqxValidator({
			rules : [
				{input : '#emplPositionTypeIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#descriptionAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#dayLeaveRegulationAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
				{input : '#dayLeaveRegulationAdd',message : '${StringUtil.wrapString(uiLabelMap.RequirementNumber?default(''))}' , action : 'blur',rule : function(){
					var dayLeave = $('#dayLeaveRegulationAdd').val();
					return !isNaN(dayLeave) && dayLeave;
				}},
				{input : '#parentTypeIdAdd',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'}
			]
	})
	$('#alterSave').click(function(){
		$('#formAdd').jqxValidator('validate');
	});
	$('#formAdd').on('validationSuccess',function(){
	var row = {};
	row = {
		emplPositionTypeId : $('#emplPositionTypeIdAdd').val(),
		description : $('#descriptionAdd').val(),
		dayLeaveRegulation : $('#dayLeaveRegulationAdd').val(),
		parentTypeId : $('#parentTypeIddAdd').val()
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
			