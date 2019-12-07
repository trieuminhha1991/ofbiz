<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script>
	var emplPositionTypes = [
	<#list emplPositionTypes as emplPosition>
		{
			emplPositionTypeId : "${emplPosition.emplPositionTypeId}",
			description : "${StringUtil.wrapString(emplPosition.description?default(''))}"
		},
	</#list>	
	];
	
	

</script>


<#assign dataField="[
	{name : 'perfReviewItemTypeId', type : 'string'},
	{name : 'description',type : 'string'},
	{name : 'emplPositionTypeId', type : 'string'},
	{name : 'weight',type : 'number', other: 'Double'},
	{name : 'fromDate', type : 'date', other: 'Timestamp'},
	{name : 'thruDate',type : 'date', other: 'Timestamp'}
]"/>

<#assign columnlist="
			{text : '${uiLabelMap.FormFieldTitle_perfReviewItemTypeId}',dataField : 'perfReviewItemTypeId', width : '100px', editable : false},
			{text : '${uiLabelMap.CommonDescription}',dataField : 'description', minwidth : '250px'},
			{text : '${uiLabelMap.HumanResEmplPositionId}',dataField : 'emplPositionTypeId', width : '150px', filtertype: 'list', columntype: 'dropdownlist', editable: true,
				cellsrenderer: function(row, column, value){
					for(var i = 0; i < emplPositionTypes.length; i++){
						if(value == emplPositionTypes[i].emplPositionTypeId){
							return '<span title=' + value + '>' + emplPositionTypes[i].description + '</span>';
						}
					}
					return '<span>' + value + '</span>';
				},
				createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
			        editor.jqxDropDownList({source: emplPositionTypes, valueMember: 'emplPositionTypeId', displayMember:'description' });
			    }
			
			},
			{text : '${uiLabelMap.jobWeight}', dataField: 'weight', width: '150px',cellsalign: 'center', columntype: 'numberinput', cellsformat: 'f2', filtertype: 'number',
				createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
					editor.jqxNumberInput({inputMode: 'simple', spinButtons: true});
				}
			},
			{text : '${uiLabelMap.CommonFromDate}',dataField : 'fromDate',columntype : 'datetimeinput', cellsformat : 'd', width : '200',cellsalign: 'center', filtertype: 'range'},
			{text : '${uiLabelMap.CommonThruDate}',dataField : 'thruDate',columntype : 'datetimeinput', cellsformat : 'd', width : '200',cellsalign: 'center', filtertype: 'range'}
"/>

<@jqGrid filtersimplemode="true"  deleterow="true" filterable="true" addrow="true" addType="popup" addrefresh="true" alternativeAddPopup="alterpopupWindow" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
	editable="true" 
	url="jqxGeneralServicer?sname=JQgetFindPerfReviewItemType"
	createUrl="jqxGeneralServicer?sname=createPerfReviewItemType&jqaction=C" addColumns="perfReviewItemTypeId;description;emplPositionTypeId;weight(java.lang.Double);fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
	removeUrl="jqxGeneralServicer?sname=deletePerfReviewItemType&jqaction=D" deleteColumn="perfReviewItemTypeId"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=uppPerfReviewItemType" editColumns="perfReviewItemTypeId;description;emplPositionTypeId;weight(java.lang.Double);fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
/>	

<div class="row-fluid">
<div class="span12">	
<div id="alterpopupWindow" style="display : none;">
	<div>${uiLabelMap.CommonAdd}</div>
	<div style="overflow: hidden;">
		<form id="formAdd" class="form-horizontal">
		<div class="row-fluid" >
		<div class="span12">
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.FormFieldTitle_perfReviewItemTypeId}</label>
				<div class="controls">
					<input type="text" id="PerfReviewItemTypeIdAdd"/>
				</div>
			</div>
			
			<div class="control-group no-left-margin">
				<label class="control-label asterisk">${uiLabelMap.CommonDescription} </label>
				<div class="controls">
					<input type="text" id="descriptionAdd"/>
				</div>
			</div>
			
			<div class="control-group no-left-margin">
				<label class="control-label">${uiLabelMap.HumanResEmplPositionId} </label>
				<div class="controls">
					<div id="emplPositionTypeIdAdd">
       				</div>
				</div>
			</div>
			
			<div class="control-group no-left-margin">
				<label class="control-label">${uiLabelMap.jobWeight} </label>
				<div class="controls">
					<div id='weightAdd'></div>
				</div>
			</div>
			
			<div class="control-group no-left-margin">
				<label class="control-label">${uiLabelMap.CommonFromDate} </label>
				<div class="controls">
        			<div id="fromDateAdd">
        			</div>
        		</div>
			</div>
			
			<div class="control-group no-left-margin">
				<label class="control-label">${uiLabelMap.CommonThruDate} </label>
				<div class="controls">
        			<div id="thruDateAdd">
        			</div>
        		</div>
			</div>
			
			<div class="control-group no-left-margin">
				<label class="control-label">&nbsp;</label>
				<div class="controls">
					<button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" style="margin-right: 5px; margin-top: 10px;" id="alterSave"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
					<button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" style="margin-right: 5px; margin-top: 10px;" id="alterCancel"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
				</div>
			</div>
			</div>
			</div>
		</form>
	</div>
</div>
</div>
</div>

<script type="text/javascript">
$.jqx.theme = 'olbius';
var theme = theme;

var emplPositionTypeAdd = $('#emplPositionTypeIdAdd');
emplPositionTypeAdd.jqxDropDownList({
	theme: 'olbius',
	source: emplPositionTypes,
	width: 205,
	height: 30,
	displayMember: "description",
	valueMember : "emplPositionTypeId"
});
<#if (emplPositionTypes?size < 8)>
	emplPositionTypeAdd.jqxDropDownList({autoDropDownHeight: true});		
</#if>

$('#alterpopupWindow').jqxWindow({ width: 500, height :400,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7 });
	$('#PerfReviewItemTypeIdAdd').jqxInput({width : '200px',height : '25px'});
	$('#descriptionAdd').jqxInput({width : '200px',height : '25px'});
	$("#weightAdd").jqxNumberInput({ width: '205px', height: '25px', inputMode: 'simple', spinButtons: true, digits: 3 });
	$("#fromDateAdd").jqxDateTimeInput({width: '205px', height: '25px'});
	$("#thruDateAdd").jqxDateTimeInput({width: '205px', height: '25px'});
	$('#formAdd').jqxValidator({
			rules : [
				{input : '#PerfReviewItemTypeIdAdd', message: '${uiLabelMap.ValidatePerfReviewItemTypeId}', action: 'keyup, blur', rule: 'required' },
				{input : '#descriptionAdd', message: '${uiLabelMap.ValidateDescriptionAdd}', action : 'blur',rule : 'required' }
				
			]
	});
	$('#alterSave').click(function(){
		$('#formAdd').jqxValidator('validate');
	});
	$('#formAdd').on('validationSuccess',function(){
	var row = {};
	row = {
		perfReviewItemTypeId : $('#PerfReviewItemTypeIdAdd').val(),
		description : $('#descriptionAdd').val(),
		emplPositionTypeId : $('#emplPositionTypeIdAdd').val(),
		weight : $('#weightAdd').val(),
		fromDate : convertDate($('#fromDateAdd').val()),
		thruDate : convertDate($('#thruDateAdd').val())
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
	function convertDate(date) {
	 	if (!date) {
			return null;
		}
		var dateArray = date.split("/");
		var newDate = new Date(dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0]);
		return newDate.getTime();
	}
</script>		
			