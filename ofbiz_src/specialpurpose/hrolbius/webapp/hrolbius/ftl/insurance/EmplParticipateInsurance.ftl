<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxcombobox.js" type="text/javascript"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtooltip.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />
<#assign datafield = "[{name: 'partyId', type: 'string'},
					   {name: 'insuranceDelarationId', type: 'string'},
					   {name: 'partyName', type: 'string'},
					   {name: 'insuranceSocialNbr', type:'string'},
					   {name: 'birthDate', type: 'date'},
					   {name: 'isWomen', type: 'string'},
					   {name: 'jobDescription', type: 'string'},
					   {name: 'salary', type: 'number'},
					   {name: 'ratioSalary', type: 'number'},
					   {name: 'allowancePosition', type: 'number'},
					   {name: 'allowanceSeniorityExces', type: 'number'},
					   {name: 'allowanceSeniority', type: 'number'},
					   {name: 'allowanceOther', type: 'number'},
					   {name: 'agreementFromDate', type: 'date'},
					   {name: 'agreementThruDate', type: 'date'},
					   {name: 'insuranceParticipateTypeId', type: 'string'},
					   {name: 'agreementNbr', type: 'string'},
					   {name: 'agreemenType', type: 'string'},
					   {name: 'agreementSignDate', type: 'date'},
					   {name: 'rateContribution', type: 'number'},
					   {name: 'insuranceTypeParticipate', type: 'string'},
					  ]"/>
					  
<script type="text/javascript">
var reParticipateTypeArr = [
	<#if reParticipateTypeList?has_content>
		<#list reParticipateTypeList as participate>
			{
				insuranceParticipateTypeId: '${participate.insuranceParticipateTypeId}',
				description: '${StringUtil.wrapString(participate.sign)} - (${StringUtil.wrapString(participate.description)})'
			},
		</#list>
	</#if>
];
<#assign columnlist = "{datafield: 'partyId', hidden: true},
						{datafield: 'insuranceDelarationId', hidden: true},
						{text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName', width: 130, editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.SocialInsuranceNbrIdentify)}', datafield: 'insuranceSocialNbr', width: '130', editable: true},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceBirthDate)}', datafield: 'birthDate', cellsformat: 'dd/MM/yyyy ', columntype: 'template', width: '160', editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFemale)}', datafield: 'isWomen', width: 70, editable: false, cellsalign: 'center'},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceEmplPositionType)}', datafield: 'jobDescription', width: 200},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceSalaryUom)}', datafield: 'salary', width: 150, 
							cellsalign: 'right', columngroup:'insuranceSalary', columntype : 'numberinput', 
							cellsrenderer: function (row, column, value) {
								if(value){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
								}
								},
								createeditor: function (row, column, editor) {
									editor.jqxNumberInput({ width: 150, height: 30, spinButtons: true, decimalDigits: 0});
								},			 		 	
							initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
								if(!cellvalue || cellvalue == ''){
									editor.val(null);
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceRatio)}', datafield: 'ratioSalary', 
							columntype : 'numberinput', width: 70, columngroup:'insuranceSalary', cellsalign: 'right',
							createeditor: function (row, column, editor) {
									editor.jqxNumberInput({ width: 70, height: 30, spinButtons: true, decimalDigits: 1});
								},
							initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
								if(!cellvalue || cellvalue == ''){
									editor.val(null);
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceAllowancePosition)}', datafield: 'allowancePosition', 
							width: 130, cellsalign: 'right', columngroup:'insuranceAllowance', columntype : 'numberinput',
							cellsrenderer: function (row, column, value) {
								if(value){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
								}
							},
							createeditor: function (row, column, editor) {
								editor.jqxNumberInput({width: 130, height: 30, spinButtons: true, decimalDigits: 0});
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
								if(!cellvalue || cellvalue == ''){
									editor.val(null);
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceAllowanceSeniorityExces)}(%)', datafield: 'allowanceSeniorityExces',
							width: 100, cellsalign: 'right', columngroup:'insuranceAllowance', columntype : 'numberinput',
							createeditor: function (row, column, editor) {
								editor.jqxNumberInput({width: 100, height: 30, spinButtons: true, decimalDigits: 2, symbolPosition: 'right', symbol: '%'});
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
								if(!cellvalue || cellvalue == ''){
									editor.val(null);
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceAllowanceSeniority)}(%)', datafield: 'allowanceSeniority', width: 100, cellsalign: 'right', 
							columngroup:'insuranceAllowance', columntype : 'numberinput',
							createeditor: function (row, column, editor) {
								editor.jqxNumberInput({width: 100, height: 30, spinButtons: true, decimalDigits: 2, symbolPosition: 'right', symbol: '%'});
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
								if(!cellvalue || cellvalue == ''){
									editor.val(null);
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceOtherAllowance)}', datafield: 'allowanceOther', width: 100, cellsalign: 'right', 
							columngroup:'insuranceAllowance', columntype : 'numberinput',
							createeditor: function (row, column, editor) {
								editor.jqxNumberInput({width: 100, height: 30, spinButtons: true, decimalDigits: 0});																
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
								if(!cellvalue || cellvalue == ''){
									editor.val(null);
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'agreementFromDate', width: 230, cellsformat: 'dd/MM/yyyy', 
							columntype: 'datetimeinput',
							createeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({width: 229, height: 29, formatString: 'dd/MM/yyyy'});
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
								if(!cellvalue || cellvalue == ''){
									editor.val(null);
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'agreementThruDate', width: 230, cellsformat: 'dd/MM/yyyy', 
							columntype: 'datetimeinput', columngroup: 'insuranceNotes',
							createeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({width: 229, height: 29, formatString: 'dd/MM/yyyy'});
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
								editor.val(cellvalue);
							}	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceParticipateType)}', datafield: 'insuranceParticipateTypeId', width: 130, 
							columngroup: 'insuranceNotes', columntype: 'dropdownlist',
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < reParticipateTypeArr.length; i++){
									if(reParticipateTypeArr[i].insuranceParticipateTypeId == value){
										return '<span>' + reParticipateTypeArr[i].description + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createeditor: function (row, column, editor) {
								var sourceParticipateType = {
										localdata: newParticipateTypeArr,
						                datatype: 'array'
								}
								var dataAdapterParticipateType = new $.jqx.dataAdapter(sourceParticipateType);
								editor.jqxDropDownList({source: dataAdapterParticipateType, displayMember: 'description', valueMember: 'insuranceParticipateTypeId', 
									height: 29, width: 130,
								});
								if(newParticipateTypeArr.length < 8){
									editor.jqxDropDownList({autoDropDownHeight: true});
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceAgreementNbr)}', datafield: 'agreementNbr', width: 115, columngroup: 'insuranceNotes'},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceAgreementType)}', datafield: 'agreemenType', width: 185, columngroup: 'insuranceNotes'},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceAgreementSignDate)}', datafield: 'agreementSignDate', width: 130, cellsformat: 'dd/MM/yyyy', 
							columntype: 'datetimeinput', columngroup: 'insuranceNotes',
							createeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({width: 129, height: 29, formatString: 'dd/MM/yyyy'});
							},
							initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
								if(!cellvalue || cellvalue == ''){
									editor.val(null);
								}
							}	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceRateContribution)}(%)', datafield: 'rateContribution', columngroup: 'insuranceNotes', width: 120, editable: false,
							cellsrenderer: function (row, column, value) {
								return '<span>' + (value * 100) + '</span>';
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceTypeParticipate)}', datafield: 'insuranceTypeParticipate', 
							columngroup: 'insuranceNotes', width: 150, columntype: 'dropdownlist',
						}
						" />
						
<#assign columngroups = "{text: '${StringUtil.wrapString(uiLabelMap.InsuranceSalaryGroup)}', align: 'center', name: 'insuranceSalary'},
						 {text: '${StringUtil.wrapString(uiLabelMap.InsuranceAllowance)}', name: 'insuranceAllowance', align: 'center'},
						 {text: '${StringUtil.wrapString(uiLabelMap.HRNotes)}', name: 'insuranceNotes', align: 'center'}"
						 />
						 
						 
</script>
<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>		
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.EmplParticipateInsuranceProcess}</h4>
		<div class="widget-toolbar none-content">
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class='row-fluid margin-bottom10'>
					<div class='span2' style="text-align: left;">
						<b>${uiLabelMap.PeriodDeclaration}</b>
					</div>
					<div class="span9" style="margin: 0">
						<div class="row-fluid">
							<div class="span3">
								<div id="monthCustomTime"></div>
							</div>
							<div class="span3" style="margin: 0">
								<div id="yearCustomTime"></div>
							</div>
							<div class="span6" style="margin: 0; display: none" id="warning">
								<i style="color: #438eb9">(${StringUtil.wrapString(uiLabelMap.InsuranceDeclarationNotSetting)})</i>
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span2' style="text-align: left;">
						<b>${uiLabelMap.TimesSetting}</b>
					</div>
					<div class="span9" style="margin: 0">
						<div class="span3">
							<div id="timesSetting"></div>
						</div>
						<div class="span3" style="margin: 0">
							<button id="addNew" class="grid-action-button icon-plus-sign">${uiLabelMap.CommonCreate}</button>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid" id="jqxGridInsuranceContainer" style="display: none;">
			<div class="row-fluid">
				<div class="span12" style="text-align: right;">
					<div class='row-fluid margin-bottom10'>
						<button id="excelBtn" class="grid-action-button icon-file-excel-o">${uiLabelMap.ExportExcel}</button>
						<button id="addNewParty" class="grid-action-button icon-plus-sign">${uiLabelMap.AddNewPartyRegisterInsurance}</button>
						<button id="deleteNewParty" class="grid-action-button icon-trash">${uiLabelMap.accDeleteSelectedRow}</button>
					</div>
				</div>	
			</div>
			<div class="row-fluid">
				<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist  showtoolbar="false" columngrouplist=columngroups
					 filterable="false" alternativeAddPopup="" deleterow="false" editable="true" addrow="false"
					 url="" id="jqxgrid" jqGridMinimumLibEnable="false"
					 removeUrl="" deleteColumn="" 
					 updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePartyInsuranceDeclaration" 
					 editColumns="" 
					 selectionmode="singlerow" 
				/>
			</div>
		</div>
	</div>	
</div>
<div class="row-fluid">
	<div id="popupWindow" class='hide'>
		<div id="popupWindowHeader">
			${StringUtil.wrapString(uiLabelMap.AddNewPartyRegisterInsurance)}
		</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.CommonEmployee}</label>
					</div>
					<div class="span7">
						<@htmlTemplate.renderSearchPartyInOrg inputId="partyIdNew" searchBtnId="searchEmpl" windowSearchId="popupWindowEmplList"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.InsuranceParticipateType}</label>
					</div>
					<div class="span7">
						<div id="insuranceParticipateTypeNew"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.SocialInsuranceNbrIdentify}</label>
					</div>
					<div class="span7">
						<input type="text" id="insuranceNbrNew">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.InsuranceTypeParticipate}</label>
					</div>
					<div class="span7">
						<div id="insuranceTypeParticipate"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.DecisionParticipateNbr}</label>
					</div>
					<div class="span7">
						<input type="text" id="decisionParticipateNbr">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.CommonFromDate}</label>
					</div>
					<div class="span7">
						<div id="insuranceAgreementFromDate"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.CommonThruDate}</label>
					</div>
					<div class="span7">
						<div id="insuranceAgreementThruDateContainer">
							<div id="insuranceAgreementThruDate"></div>
						</div>
						<!-- <div><i style="color: #438eb9">(${StringUtil.wrapString(uiLabelMap.insuranceAgreementThruDateNotes)})</i></div> -->
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.InsuranceAgreementSignDate}</label>
					</div>
					<div class="span7">
						<div id="insuranceAgreementSignDate"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.InsuranceAgreementType}</label>
					</div>
					<div class="span7">
						<input type="text" id="insuranceAgreementType">
						<!-- <div><i style="color: #438eb9">(${StringUtil.wrapString(uiLabelMap.InsuranceAgreementTypeNotes)})</i></div> -->
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
</div>
<div class="row-fluid">
	<div id="popupWindowEmplList" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div>
			<div id="splitterEmplList" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplList"></div>
				</div>
				<div id="ContentPanel" style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	                   <div id="EmplListInOrg">
	                   </div>
	               </div>
	        	</div>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function () {
	createJqxGridSearchEmpl();
	initJqxSearchEmplWindow();
	
});

function getPartyInsuranceSocialNbr(partyId){
	$.ajax({
		url: 'getPartyInsuranceSocialNbr',
		data: {partyId: partyId},
		type: 'POST',
		success: function(data){
			if(data.insuranceSocialNbr){
				$("#insuranceNbrNew").jqxInput('val', data.insuranceSocialNbr);
			}else{
				$("#insuranceNbrNew").jqxInput('val', '');
			}				
		}
	});
}

function createJqxGridSearchEmpl(){
	var datafield =  [
		{name: 'partyId', type: 'string'},
		{name: 'partyName', type: 'string'},
		{name: 'emplPositionType', type: 'string'},
		{name: 'department', type: 'string'},
		{name: 'dateJoinCompnay', type: 'date'},
	];
	var columnlist = [
      {text: '${uiLabelMap.EmployeeId}', datafield: 'partyId' , editable: false, cellsalign: 'left', width: 90, filterable: false},
	  {text: '${uiLabelMap.EmployeeName}', datafield: 'partyName', editable: false, cellsalign: 'left', width: 150, filterable: false},
	  {text: '${uiLabelMap.Position}', datafield: 'emplPositionType', editable: false, cellsalign: 'left', width: 130, filterable: false},
	  {text: '${uiLabelMap.CommonDepartment}', datafield: 'department', editable: false, cellsalign: 'left', filterable: false},
	  {text: '${uiLabelMap.DateJoinCompany}', hidden: true, datafield: 'dateJoinCompnay', cellsformat: 'dd/MM/yyyy', editable: false, cellsalign: 'left', filterable: true,
		  filtertype: 'range'  
	  },
	];
	
	var config = {
		width: '100%', 
		height: 467,
		autoheight: false,
		virtualmode: true,
		showfilterrow: true,
		showtoolbar: false,
		selectionmode: 'singlerow',
		pageable: true,
		sortable: false,
        filterable: true,
        editable: false,
        selectionmode: 'singlerow',
        url: 'JQGetEmplListInOrg&hasrequest=Y',
        source: {pagesize: 15}
	};
	GridUtils.initGrid(config, datafield, columnlist, null, $("#EmplListInOrg"));
	
	$("#EmplListInOrg").on('rowdoubleclick', function(event){
		var args = event.args;
	    var boundIndex = args.rowindex;
	    var data = $("#EmplListInOrg").jqxGrid('getrowdata', boundIndex);
	    $('#popupWindowEmplList').jqxWindow('close');
	    $("#partyIdNew").jqxInput('val', {value: data.partyId, label: data.partyName + ' [' + data.partyId + ']'});
	    getPartyInsuranceSocialNbr(data.partyId);
	});
}

function initJqxSearchEmplWindow(){
	$('#popupWindowEmplList').jqxWindow({
	    showCollapseButton: true, autoOpen: false, maxWidth: "80%", minWidth: "50%", maxHeight: 520, height: 520, width: "80%", isModal: true, 
	    theme:'olbius', collapsed:false,
	    initContent: function () {  
	    	initJqxSplitter();
	    }
	});
	$('#popupWindowEmplList').on('open', function(event){
		<#if expandTreeId?has_content>
			$("#jqxTreeEmplList").jqxTree('expandItem', $("#${expandTreeId}_jqxTreeEmplList")[0]);
			$('#jqxTreeEmplList').jqxTree('selectItem', $("#${expandTreeId}_jqxTreeEmplList")[0]);
		</#if>
	});
	$('#popupWindowEmplList').on('close', function(event){
		$("#EmplListInOrg").jqxGrid('clearselection');
	});
}

function initJqxSplitter(){
	$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
}
</script>
<#if expandedList?has_content>
	<#assign expandTreeId=expandedList[0]>
<#else>
	<#assign expandTreeId="">
</#if>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId=expandTreeId isDropDown="false" width="100%" height="100%" expandAll="false"/>
	
<script type="text/javascript">
function jqxTreeEmplListSelect(event){
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	var tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}

</script>	


<script type="text/javascript">
var yearCustomTimePeriod = [
	<#if customTimePeriodYear?has_content>
		<#list customTimePeriodYear as customTimePeriod>
			{
				customTimePeriodId: "${customTimePeriod.customTimePeriodId}",
				periodTypeId: "${customTimePeriod.periodTypeId}",
				periodName: "${StringUtil.wrapString(customTimePeriod.periodName)}",
				fromDate: ${customTimePeriod.fromDate.getTime()},
				thruDate: ${customTimePeriod.thruDate.getTime()}
			},
		</#list>
	</#if>
];
$(document).ready(function(){
	initCustomTimePeriodDropDownlist();	
	initJqxDropdownlist();
	initJqxNotification();
	initBtnEvent();
	initJqxInputText();
	initJqxInputEvent();
	initJqxCombobox();
	initJqxValidator();
	initJqxDateTimeInput();
	initJqxWindow();
});

function initJqxInputEvent(){
	$("#partyIdNew").on('select', function(){
		var valueInput = $("#partyIdNew").jqxInput('val');
		var partyId = valueInput; 
		if(valueInput.value){
			partyId = valueInput.value; 
		}
		getPartyInsuranceSocialNbr(partyId);
	});
}

function initBtnEvent(){
	$("#addNew").click(function(event){
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.CreateNewInsuranceDeclaration)}",
			[{
				"label" : "${uiLabelMap.CommonSubmit}",
    		    "class" : "btn-primary btn-mini icon-ok",
    		    "callback": function() {
    		 		createNewInsuranceDeclaration();   	
    		    }	
			},
			{
    		    "label" : "${uiLabelMap.CommonClose}",
    		    "class" : "btn-danger btn-mini icon-remove",
    		    "callback": function() {
    		    	
    		    }
    		}]		
		);
	});
	$("#addNewParty").click(function(event){
		openJqxWindow($("#popupWindow"));
	});
	$("#btnCancel").click(function(event){
		$("#popupWindow").jqxWindow('close');
	});
	
	$("#btnSave").click(function(event){
		var valid = $("#popupWindow").jqxValidator('validate');
		if(!valid){
			return;
		}
		$(this).attr("disabled", "disabled");
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.InsuranceConfirmAddEmpl)}",
				[
				 {
					 "label" : "${uiLabelMap.CommonSubmit}",
		    		    "class" : "btn-primary btn-mini icon-ok",
		    		    "callback": function() {
		    		    	addEmplToInsuranceDeclaration();
		    		    	$("#btnSave").removeAttr("disabled");
		    		    	$("#popupWindow").jqxWindow('close');
		    		    }
				 },
				 {
	    		    "label" : "${uiLabelMap.CommonCancel}",
	    		    "class" : "btn-danger btn-mini icon-remove",
	    		    "callback": function() {
	    		    	$("#btnSave").removeAttr("disabled");
	    		    }
	    		}]		
			);
	});
	
	$("#deleteNewParty").click(function(event){
		var selectedIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		if(selectedIndex < 0){
			return;
		}
		var data = $("#jqxgrid").jqxGrid('getrowdata', selectedIndex);
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.DAAreYouSureDelete)}",
			[
			{
				"label" : "${uiLabelMap.CommonSubmit}",
    		    "class" : "btn-primary btn-mini icon-ok",
    		    "callback": function() {
    		 		deletePartyInsuranceDecl(data);
    		    }
			},
			{
				"label" : "${uiLabelMap.CommonClose}",
    		    "class" : "btn-danger btn-mini icon-remove",
    		    "callback": function() {
    		    	
    		    }
			}
			]	
		)
	});
	
	$("#excelBtn").click(function(event){
		var insuranceDelarationId = $("#timesSetting").jqxDropDownList('val');
		window.location.href = "exportInsuranceDeclarationParticipate?insuranceDelarationId=" + insuranceDelarationId;
	});
}

function deletePartyInsuranceDecl(data){
	var dataSubmit = {};
	dataSubmit.partyId = data.partyId;
	dataSubmit.insuranceDelarationId = data.insuranceDelarationId;
	$("#jqxgrid").jqxGrid({ disabled: true});
	$("#jqxgrid").jqxGrid('showloadelement');
	$.ajax({
		url: 'deletePartyInsuranceDecl',
		data: dataSubmit,
		type: 'POST',
		success: function(response){
			$("#jqxNtf").jqxNotification('closeLast');
			if(response._EVENT_MESSAGE_){
				$("#jqxNtfContent").text(response._EVENT_MESSAGE_);
				$("#jqxNtf").jqxNotification({template: 'info'});
				$("#jqxNtf").jqxNotification("open");
				$("#jqxgrid").jqxGrid('updatebounddata');
			}else{
				$("#jqxNtfContent").text(response._ERROR_MESSAGE_);
				$("#jqxNtf").jqxNotification({template: 'error'});
				$("#jqxNtf").jqxNotification("open");
			}
		},
		error: function(jqXHR, textStatus, errorThrown){
			
		},
		complete: function(jqXHR, textStatus){
			$("#jqxgrid").jqxGrid({ disabled: false});
			$("#jqxgrid").jqxGrid('hideloadelement');
		}
	});
}

function initJqxDateTimeInput(){
	$("#insuranceAgreementFromDate").jqxDateTimeInput({width: '98%', height: 25, formatString: 'dd/MM/yyyy', theme: 'olbius'});
	$("#insuranceAgreementThruDate").jqxDateTimeInput({width: '98%', height: 25, formatString: 'dd/MM/yyyy', theme: 'olbius'});
	$("#insuranceAgreementSignDate").jqxDateTimeInput({width: '98%', height: 25, formatString: 'dd/MM/yyyy', theme: 'olbius'});
	$("#insuranceAgreementFromDate").val(null);
	$("#insuranceAgreementThruDate").val(null);
	$("#insuranceAgreementSignDate").val(null);
	createJqxTooltip($("#insuranceAgreementThruDateContainer"), '${StringUtil.wrapString(uiLabelMap.insuranceAgreementThruDateNotes)}');
}

function addEmplToInsuranceDeclaration(){
	var data = {};
	data.partyId = $("#partyIdNew").jqxInput('val').value; 
	data.insuranceParticipateTypeId = $("#insuranceParticipateTypeNew").val();
	data.insuranceDelarationId = $("#timesSetting").jqxDropDownList('val');
	data.insuranceSocialNbr = $("#insuranceNbrNew").val();
	var insuranceTypeSelect = $("#insuranceTypeParticipate").jqxComboBox('getSelectedItems');
	var insuranceTypeJsonArr = new Array();
	for(var i = 0; i < insuranceTypeSelect.length; i++){
		insuranceTypeJsonArr.push(insuranceTypeSelect[i].value);
	}
	data.insuranceTypeId = JSON.stringify(insuranceTypeJsonArr);
	data.agreementNbr = $("#decisionParticipateNbr").val();
	data.agreementFromDate = $("#insuranceAgreementFromDate").jqxDateTimeInput('val', 'date').getTime();
	var thruDate = $("#insuranceAgreementThruDate").jqxDateTimeInput('val', 'date');
	var signDate = $("#insuranceAgreementSignDate").jqxDateTimeInput('val', 'date');
	if(thruDate){
		data.agreementThruDate = thruDate.getTime();
	}
	if(signDate){
		data.agreementSignDate = signDate.getTime();
	}
	$.ajax({
		url: 'createPartyInsuranceDeclaration',
		data: data,
		type: 'POST',
		success: function(data){
			$("#jqxNtf").jqxNotification('closeLast');
			if(data.responseMessage == "success"){
				//$('#jqxgrid').jqxGrid('updatebounddata');
				$("#jqxNtfContent").text(data.successMessage);
				$("#jqxNtf").jqxNotification({template: 'info'});
				$("#jqxNtf").jqxNotification("open");
				$("#jqxgrid").jqxGrid('updatebounddata');	
			}else{
				$("#jqxNtfContent").text(data.errorMessage);
				$("#jqxNtf").jqxNotification({template: 'error'});
				$("#jqxNtf").jqxNotification("open");
			}
		}
	});
}

function createNewInsuranceDeclaration(){
	var customTimePeriodId = $("#monthCustomTime").val();
	$("#jqxNtf").jqxNotification('closeLast');
	$.ajax({
		url: 'createEmplInsuranceParticipate',
		data: {customTimePeriodId: customTimePeriodId},
		type: 'POST',
		success: function(data){
			if(data.responseMessage == "success"){
				//$('#jqxgrid').jqxGrid('updatebounddata');
				$("#jqxNtfContent").text(data.successMessage);
				$("#jqxNtf").jqxNotification({template: 'info'});
				$("#jqxNtf").jqxNotification("open");
				refreshTimesSettingJqxDropDownList(data.insuranceDelarationId);	
			}else{
				$("#jqxNtfContent").text(data.errorMessage);
				$("#jqxNtf").jqxNotification({template: 'error'});
				$("#jqxNtf").jqxNotification("open");
			}
		}, 
		error: function( jqXHR, textStatus, errorThrown){
			
		},
		complete: function( jqXHR, textStatus){
			
		}
	});
}

function refreshTimesSettingJqxDropDownList(selectItem){
	var item = $("#monthCustomTime").jqxDropDownList('getSelectedItem');
	getInsuranceDeclarationData(item.value, "ITEM", selectItem);
}

function initJqxDropdownlist(){
	var sourceParticipateType = {
			localdata: reParticipateTypeArr,
            datatype: "array"
	}
	var dataAdapterParticipateType = new $.jqx.dataAdapter(sourceParticipateType);
	$("#insuranceParticipateTypeNew").jqxDropDownList({source: dataAdapterParticipateType, displayMember: "description", valueMember: "insuranceParticipateTypeId", 
		height: 25, width: '98%', theme: 'olbius'
	});
	<#if (reParticipateTypeList?size < 8)>
		$("#insuranceParticipateTypeNew").jqxDropDownList({autoDropDownHeight: true});
	</#if>
}

function initJqxCombobox(){
	var dataInsuranceType = [
   		<#if insuranceTypeList?has_content>
   			<#list insuranceTypeList as insuranceType>
   				{
   					insuranceTypeId: '${insuranceType.insuranceTypeId}',
   					description: '${StringUtil.wrapString(insuranceType.description)}'
   				},
   			</#list>
   		</#if>
   	];	
	var source = {
			localdata: dataInsuranceType,
            datatype: "array"
	}
	var dataAdapter = new $.jqx.dataAdapter(source);
   	$("#insuranceTypeParticipate").jqxComboBox({source: dataAdapter, displayMember: "description", 
   		valueMember: "insuranceTypeId", height: 25, width: '98%', theme: 'olbius', multiSelect: true});	
   	if(dataInsuranceType.length < 8){
   		$("#insuranceTypeParticipate").jqxComboBox({autoDropDownHeight: true});
   	}
   	$("#insuranceTypeParticipate").jqxComboBox('clearSelection');
}

function initCustomTimePeriodDropDownlist(){
	var sourceCustomTimeYear = {
			localdata: yearCustomTimePeriod,
            datatype: "array"
	};
	var dataAdapter = new $.jqx.dataAdapter(sourceCustomTimeYear);
	
	$('#yearCustomTime').jqxDropDownList({source: dataAdapter, displayMember: "periodName", valueMember: "customTimePeriodId", 
		height: 25, width: 150, theme: 'olbius'
    });	
	
	$("#monthCustomTime").jqxDropDownList({ displayMember: "periodName", valueMember: "customTimePeriodId", height: 25, width: 150, theme: 'olbius'});
	
	$("#timesSetting").jqxDropDownList({height: 25, width: 150, theme: 'olbius', autoDropDownHeight: true, placeHolder:'',
		displayMember: "sequenceNum", valueMember: "insuranceDelarationId"	
	});
	<#if (customTimePeriodYear?size < 8)>
		$("#yearCustomTime").jqxDropDownList({autoDropDownHeight: true});
	</#if>
	
	initCustomTimeDropdownlistEvent();
	
	<#if selectYearCustomTimePeriodId?exists>
		$("#yearCustomTime").jqxDropDownList('selectItem', "${selectYearCustomTimePeriodId}");
	<#else>
		$("#yearCustomTime").jqxDropDownList('selectIndex', 0 ); 
	</#if>
}

function initCustomTimeDropdownlistEvent(){
	$("#timesSetting").on('select', function(event){
		var args = event.args;
		if(args){
			var item = args.item;
			var insuranceDelarationId = item.value;
			var tmpS = $("#jqxgrid").jqxGrid('source');
			tmpS._source.url = "jqxGeneralServicer?sname=getPartyInsuranceDeclaration&hasrequest=Y&insuranceDelarationId=" + insuranceDelarationId;
			$("#jqxgrid").jqxGrid('source', tmpS);
		}
	});
	
	$("#monthCustomTime").on('select', function(event){
		var args = event.args;
		if(args){
			var item = args.item;
			var value = item.value;
			getInsuranceDeclarationData(value, "INDEX", 0);				
		}
	});
	$("#yearCustomTime").on('select', function(event){
		var args = event.args;
		if(args){
			var item = args.item;
			var value = item.value;
			$.ajax({
				url: "getCustomTimePeriodByParent",
				data: {parentPeriodId: value},
				type: 'POST',
				success: function(data){
					if(data.listCustomTimePeriod){
						var listCustomTimePeriod = data.listCustomTimePeriod;
						var selectItem = listCustomTimePeriod.filter(function(item, index, array){
							var nowTimestamp = ${startDate.getTime()};
							if(item.fromDate <= nowTimestamp && item.thruDate >= nowTimestamp){
								return item;
							}
						});
						var tempSource = {
								localdata: listCustomTimePeriod,
				                datatype: "array"
						}
						var tmpDataAdapter = new $.jqx.dataAdapter(tempSource);
						$("#monthCustomTime").jqxDropDownList('clearSelection');
						$("#monthCustomTime").jqxDropDownList({source: tmpDataAdapter});
						if(selectItem.length > 0){
							$("#monthCustomTime").jqxDropDownList('selectItem', selectItem[0].customTimePeriodId);
						}else{
							$("#monthCustomTime").jqxDropDownList({selectedIndex: 0 });
						}
					}
				},
				complete: function(jqXHR, textStatus){
					
				}
			});
		}
	});
}

function initJqxValidator(){
	$("#popupWindow").jqxValidator({
		rules:[
		  {
			input: "#partyIdNew",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: 'required'
		  },
		  {
			input: "#decisionParticipateNbr",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: function (input, commit){
				if(!input.val()){
					return false;
				}
				return true;
			}
		  },
		  {
			input: "#insuranceAgreementFromDate",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: function (input, commit){
				if(!input.val()){
					return false;
				}
				return true;
			}
		  },
		  {
			input: "#insuranceParticipateTypeNew",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: function (input, commit){
				var value = input.val();
				if(!value){
					return false
				}
				return true;
			}
		  },
		  {
			input: "#insuranceTypeParticipate",
			message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
			rule: function (input, commit){
				var value = input.val();
				if(!value){
					return false
				}
				return true;
			}
		  }
		]
	});
}

function getInsuranceDeclarationData(customTimePeriodId, selectType, select){
	$("#timesSetting").jqxDropDownList('clearSelection');	
	$.ajax({
		url: 'getInsDeclParticipatetByCustomPeriod',
		data: {customTimePeriodId: customTimePeriodId},
		type: 'POST',
		success: function(data){
			if(data.responseMessage == "success"){
				if(data.listReturn && data.listReturn.length > 0){
					var tmpS = {
						localdata: data.listReturn,
		                datatype: "array"	
					};
					var tmpDataAdapter = new $.jqx.dataAdapter(tmpS);
					$("#timesSetting").jqxDropDownList({source: tmpDataAdapter});	
					if(selectType == "INDEX"){
						$("#timesSetting").jqxDropDownList('selectIndex', select);
					}else if(selectType == "ITEM"){
						$("#timesSetting").jqxDropDownList('selectItem', select);
					}
					$("#warning").hide();
					$("#jqxGridInsuranceContainer").show();
				}else{
					$("#timesSetting").jqxDropDownList({source: []});
					$("#warning").show();
					$("#jqxGridInsuranceContainer").hide();
				}
			}else{
				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ErrorWhenRetrieveData)}",
					[{
		    		    "label" : "${uiLabelMap.CommonClose}",
		    		    "class" : "btn-danger btn-mini icon-remove",
		    		    "callback": function() {
		    		    }
		    		}]		
				);
			}
		}
	});
}

function initJqxWindow(){
	$("#popupWindow").jqxWindow({showCollapseButton: false,autoOpen: false,
		maxWidth: 520, minWidth: 520, height: 465, width: 520, isModal: true, theme:'olbius',
		initContent: function(){
			
		}	
	});
	$("#popupWindow").on('close', function(event){
		GridUtils.clearForm($(this));
		$("#popupWindow").jqxValidator('hide');
	});
	$("#popupWindow").on('open', function(event){
		var monthCustomTimeSource = $("#monthCustomTime").jqxDropDownList('source');
		if(monthCustomTimeSource){
			var data = monthCustomTimeSource._source.localdata;
			var selectItem = $("#monthCustomTime").jqxDropDownList('getSelectedItem');
			var index = selectItem.index;
			$("#insuranceAgreementFromDate").val(new Date(data[index].fromDate));
		}
	});
}

function initJqxNotification(){
	$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#notifyContainer"});
}

function initJqxInputText(){
	$("#insuranceNbrNew").jqxInput({height: 20, width: '96%', theme: 'olbius'});
	$("#decisionParticipateNbr").jqxInput({height: 20, width: '96%', theme: 'olbius'});
	$("#insuranceAgreementType").jqxInput({height: 20, width: '96%', theme: 'olbius'});
	createJqxTooltip($("#insuranceAgreementType"), '${StringUtil.wrapString(uiLabelMap.InsuranceAgreementTypeNotes)}');
}

function createJqxTooltip(element, message){
	element.jqxTooltip({ content: message, name: 'movieTooltip', position: 'mouseenter', theme: 'olbius'});
}

function openJqxWindow(jqxWindowDiv){
	var wtmp = window;
	var tmpwidth = jqxWindowDiv.jqxWindow('width');
	jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
	jqxWindowDiv.jqxWindow('open');
}
</script>