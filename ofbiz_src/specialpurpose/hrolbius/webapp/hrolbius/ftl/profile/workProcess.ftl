<div class="tab-pane" id="workProcessInfoTab">
	<#assign dataField="[{ name: 'partyId', type: 'string' },
						{ name: 'personWorkingProcessId', type: 'string' },
						{ name: 'companyName', type: 'string' },
						{ name: 'emplPositionTypeId', type: 'string' },
						{ name: 'jobDescription', type: 'string'},
						{ name: 'payroll', type: 'string'},
						{ name: 'terminationReasonId', type: 'string'},
						{ name: 'rewardDiscrip', type: 'string'},
						{ name: 'fromDate', type: 'date', other: 'Timestamp' },
						{ name: 'thruDate', type: 'date', other: 'Timestamp' }
						]"/>

	<#assign columnlist="{ text: '${uiLabelMap.CompanyName}', datafield: 'companyName', width: 200,
							cellsrenderer: function(column, row, value){
								for(var i = 0; i < partyData.length; i++){
									if(value == partyData[i].partyId){
										return '<span title=' + value + '>' + partyData[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							}
						 },
						 { text: '${uiLabelMap.personWorkingProcessId}', datafield: 'personWorkingProcessId', width: 200, hidden: true},
						 { text: '${uiLabelMap.EmplPositionTypeId}', datafield: 'emplPositionTypeId', width: 200, filtertype: 'list', columntype: 'dropdownlist', editable: true,
							 cellsrenderer: function(column, row, value){
									for(var i = 0;  i < emplPositionType2.length; i++){
										if(emplPositionType2[i].emplPositionTypeId == value){
											return '<span title=' + value + '>' + emplPositionType2[i].description + '</span>'
										}
									}
									return '<span>' + value + '</span>'
								},
								createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							        editor.jqxDropDownList({source: emplPositionType2, valueMember: 'emplPositionTypeId', displayMember:'description' });
							    },
							    createfilterwidget: function (column, htmlElement, editor) {
					                editor.jqxDropDownList({ source: fixSelectAll(emplPositionType2), displayMember: 'description', valueMember: 'emplPositionTypeId' ,
					                	renderer: function (index, label, value) {
					                		if (index == 0) {
					                			return value;
					                		}
					                        for(var i = 0; i < emplPositionType2.length; i++){
					                        	if(value == emplPositionType2[i].emplPositionTypeId){
					                        		return emplPositionType2[i].description; 
					                        	}
					                        }
					                    }});
					                editor.jqxDropDownList('checkAll');
					            }
						 },
						 { text: '${uiLabelMap.JobDescription}', datafield: 'jobDescription', width: 200,},
						 { text: '${uiLabelMap.HRSalary}', datafield: 'payroll', width: 200},
						 { text: '${uiLabelMap.TerminationReason}', datafield: 'terminationReasonId', width: 200},
						 { text: '${uiLabelMap.HRRewardAndDisciplining}', datafield: 'rewardDiscrip', width: 200},
						 { text: '${uiLabelMap.CommonFromDate}', datafield: 'fromDate', width: 200, cellsformat: 'd', filtertype:'range', columntype: 'datetimeinput',
							 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
									editor.jqxDateTimeInput({ });
							}
						 },
						 { text: '${uiLabelMap.CommonThruDate}', datafield: 'thruDate', width: 200, cellsformat: 'd', filtertype:'range', columntype: 'datetimeinput',
							 createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
									editor.jqxDateTimeInput({ });
							},
							validation: function (cell, value) {
								var data = $('#jqxgridWorkProcessInfo').jqxGrid('getrowdata', cell.row);
								if( value < data.fromDate ){
							    	return { result: false, message: '${uiLabelMap.TimeBeginAfterTimeEnd}'};
							    }
							    else 
							        return true;
							    }
						 },
						"/>
						
	<@jqGrid addrow="true" editable="true" deleterow="true" addType="popup" alternativeAddPopup="alterpopupWindow2" isShowTitleProperty="false" id="jqxgridWorkProcessInfo" filtersimplemode="true" showtoolbar="true" clearfilteringbutton="true"
	url="jqxGeneralServicer?sname=JQGetListEmplWorkProcess&partyId=${parameters.partyId}" dataField=dataField columnlist=columnlist
	updateUrl="jqxGeneralServicer?jqaction=U&sname=editPersonWorkingProcess" editColumns="personWorkingProcessId;companyName;emplPositionTypeId;jobDescription;payroll;terminationReasonId;rewardDiscrip;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
	createUrl="jqxGeneralServicer?sname=createPersonWorkingProcess&jqaction=C" addColumns="partyId;personWorkingProcessId;companyName;emplPositionTypeId;jobDescription;payroll;terminationReasonId;rewardDiscrip;fromDate(java.sql.Timestamp);thruDate(java.sql.Timestamp)"
	removeUrl="jqxGeneralServicer?sname=deleteWorkingProcess&jqaction=D" deleteColumn="personWorkingProcessId"	
			
	/>		
</div>
<div id="alterpopupWindow2" style="display : none;">
<div>${uiLabelMap.CommonAdd}</div>
<div style="overflow: hidden;">
	<form id="WorkingProcessForm" class="form-horizontal">
		<input type="hidden" value="${parameters.partyId}" id="partyId" name="partyId" />
		
		<div class="row-fluid no-left-margin">
			<label class="span4">${uiLabelMap.CommonFromDate}</label>
			<div class="span8" style="margin-bottom: 10px;">
				<div id="workProcess_fromDate_title"></div>
			</div>
		</div>
    
		<div class="row-fluid no-left-margin">
			<label class="span4">${uiLabelMap.CommonThruDate}</label>
			<div class="span8" style="margin-bottom: 10px;">
				<div id="workProcess_thruDate_title"></div>
			</div>
		</div>
		
		<div class="row-fluid no-left-margin">
			<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.CompanyName}</div>
			<div class="span8" style="margin-bottom: 10px;">
				<input type="text" id="workProcess_companyName_title" name="companyName"/>
			</div>
		</div>
				
		<div class="row-fluid no-left-margin">
			<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.EmplPositionTypeId}</div>
			<div class="span8" style="margin-bottom: 10px;">
				<div id="emplPositionTypeIdWorkProcessAdd"></div>
			</div>
		</div>
		
		<div class="row-fluid no-left-margin">
			<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.JobDescription}</div>
			<div class="span8" style="margin-bottom: 10px;">
				<input type="text" id="workProcess_JobDescription" name="workProcessJobDescription"/>
			</div>
		</div>
        		
        <div class="row-fluid no-left-margin">
			<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.HRSalary}</div>
			<div class="span8" style="margin-bottom: 10px;">
				<input type="text" id="workProcess_Payroll_title" name="workProcess_Payroll_title"/>
			</div>
		</div>
			
		<div class="row-fluid no-left-margin">
			<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.TerminationReason}</div>
			<div class="span8" style="margin-bottom: 10px;">
				<input type="text" id="workProcess_TerminationReason" name="workProcess_TerminationReason"/>
			</div>
		</div>
		
		<div class="row-fluid no-left-margin">
			<div class="span4 asterisk" style="text-align:right;">${uiLabelMap.HRRewardAndDisciplining}</div>
			<div class="span8" style="margin-bottom: 10px;">
				<input type="text" id="workProcess_rewardDiscrip" name="workProcess_rewardDiscrip"/>
			</div>
		</div>
		
		<div class="control-group no-left-margin" style="float:right">
			<div class="" style="width:166px;margin:0 auto;">
				<button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius" style="margin-right: 5px; margin-top: 10px;" id="alterSave2"><i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
				<button type="button" class="jqx-rc-all jqx-rc-all-olbius jqx-button jqx-button-olbius jqx-widget jqx-widget-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius btn-danger" style="margin-right: 5px; margin-top: 10px;" id="alterCancel2"><i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
			</div>
		</div>
				
	</form>
</div>
</div>		 
 
<#assign emplPositionType2 = delegator.findList("EmplPositionType", null , null, orderBy,null, false)>
<script type="text/javascript">
$.jqx.theme = 'olbius';
var theme = theme;

var emplPositionType2 = [
    <#list emplPositionType2 as emplPositionT2>
    {
    	emplPositionTypeId : "${emplPositionT2.emplPositionTypeId}",
    	description : "${StringUtil.wrapString(emplPositionT2.description?if_exists)}"
    },
    </#list>	
];


$("#emplPositionTypeIdWorkProcessAdd").jqxDropDownList({ autoDropDownHeight: true, dropDownHeight: 5 });
$('#alterpopupWindow2').jqxWindow({ width: 480, height : 470,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7 });
$('#workProcess_companyName_title').jqxInput({width : '243px',height : '25px'});
$('#workProcess_JobDescription').jqxInput({width : '243px',height : '25px'});
$('#workProcess_Payroll_title').jqxInput({width : '243px',height : '25px'});
$('#workProcess_TerminationReason').jqxInput({width : '243px',height : '25px'});
$('#workProcess_rewardDiscrip').jqxInput({width : '243px',height : '25px'});
$("#emplPositionTypeIdWorkProcessAdd").jqxDropDownList({ source: emplPositionType2, width: '248px', height: '25px', selectedIndex: 1, displayMember: "description", valueMember : "emplPositionTypeId"});
$("#workProcess_fromDate_title").jqxDateTimeInput({width: '248px', height: '25px'});
$("#workProcess_thruDate_title").jqxDateTimeInput({width: '248px', height: '25px'});

$('#WorkingProcessForm').jqxValidator({
	rules : [
		{input : '#workProcess_companyName_title',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
		{input : '#workProcess_JobDescription',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
		{input : '#workProcess_TerminationReason',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
		{input : '#workProcess_rewardDiscrip',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
		{input : '#workProcess_Payroll_title',message : '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}' , action : 'blur',rule : 'required'},
	]
});

$('#alterSave2').click(function(){
	$('#WorkingProcessForm').jqxValidator('validate');
});

$('#WorkingProcessForm').on('validationSuccess',function(){
	var row = {};
	row = {
		partyId: $('#partyId').val(),
		companyName : $('#workProcess_companyName_title').val(),
		jobDescription : $('#workProcess_JobDescription').val(),
		payroll : $('#workProcess_Payroll_title').val(),
		terminationReasonId : $('#workProcess_TerminationReason').val(),
		rewardDiscrip : $('#workProcess_rewardDiscrip').val(),
		fromDate : $('#workProcess_fromDate_title').val(),
		thruDate : $('#workProcess_thruDate_title').val(),
		emplPositionTypeId : $("#emplPositionTypeIdWorkProcessAdd").val(),
	};
	
	$("#jqxgridWorkProcessInfo").jqxGrid('addRow', null, row, "first");
// select the first row and clear the selection.
	$("#jqxgridWorkProcessInfo").jqxGrid('clearSelection');                        
	$("#jqxgridWorkProcessInfo").jqxGrid('selectRow', 0);  
	$("#alterpopupWindow2").jqxWindow('close');
});

$('#alterpopupWindow2').on('close',function(){
	$('#WorkingProcessForm').trigger('reset');
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
