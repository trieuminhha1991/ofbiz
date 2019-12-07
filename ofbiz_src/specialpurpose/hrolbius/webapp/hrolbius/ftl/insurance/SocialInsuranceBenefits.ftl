<@jqGridMinimumLib/>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxmaskedinput.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqxtooltip.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>

<#assign datafield = "[{name: 'partyId', type: 'string'},
					   {name: 'benefitTypeId', type: 'string'},
					   {name: 'partyName', type: 'string'},	
					   {name: 'genderMale', type: 'date'},
					   {name: 'genderFemale', type: 'date'},
					   {name: 'insuranceSocialNbr', type: 'string'},
					   {name: 'insuranceParticipatePeriod', type: 'string'},
					   {name: 'insuranceSalary', type: 'string'},
					   {name: 'statusConditionBenefit', type: 'string'},
					   {name: 'timeConditionBenefit', type: 'date'},
					   {name: 'fromDateLeave', type: 'date'},
					   {name: 'thruDateLeave', type: 'date'},
					   {name: 'totalDateLeave', type: 'number'},
					   {name: 'accumulatedLeave', type: 'number'},
					   {name: 'allowanceAmount', type: 'number'}
					  ]"/>
					  
<script type="text/javascript">
var ONE_DAY = 1000 * 60 * 60 * 24;


var insuranceAllowanceBenefitTypeArr = [
	<#if insuranceAllowanceBenefitTypeList?has_content>
		<#list insuranceAllowanceBenefitTypeList as insuranceAllowanceBenefitType>
			{
				benefitTypeId: '${insuranceAllowanceBenefitType.benefitTypeId}',
				description: '${StringUtil.wrapString(insuranceAllowanceBenefitType.description?if_exists)} - ${insuranceAllowanceBenefitType.benefitTypeCode}',
				code: '${insuranceAllowanceBenefitType.benefitTypeCode}',
				name: '${StringUtil.wrapString(insuranceAllowanceBenefitType.description?if_exists)}'
			},
		</#list>
	</#if>
];

var tooltiprenderer = function (element) {
	var contentTooltip = '<div style="text-align: left">';
	for(var i = 0; i < insuranceAllowanceBenefitTypeArr.length; i++){
		contentTooltip += insuranceAllowanceBenefitTypeArr[i].code + ' - ' + insuranceAllowanceBenefitTypeArr[i].name + '<br/>'; 
	}
	contentTooltip += '</div>';
	createJqxTooltip($(element), contentTooltip, false);
}
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.HRSequenceNbr)}', datafield: '', editable: false,	
							groupable: false,  columntype: 'number', width: 50, sortable: false,
							cellsrenderer: function (row, column, value) {
						    	return '<span>' + (value + 1) + '</span>';
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceBenefitType)}*', datafield: 'benefitTypeId', width: 70, rendered: tooltiprenderer, sortable: false,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < insuranceAllowanceBenefitTypeArr.length; i++){
									if(insuranceAllowanceBenefitTypeArr[i].benefitTypeId == value){
										return '<span>' + insuranceAllowanceBenefitTypeArr[i].code + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.HRFullName)}', datafield: 'partyName', width: 130},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonMale)}', datafield: 'genderMale', width: 90, 
							columngroup: 'partyBirthDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy'
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFemale)}', datafield: 'genderFemale', width: 90, 
							columngroup: 'partyBirthDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy'
						},
						{text: '${StringUtil.wrapString(uiLabelMap.SocialInsuranceNbrIdentify)}', datafield: 'insuranceSocialNbr', width: 130, editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceParticipatePeriod)}', datafield: 'insuranceParticipatePeriod', width: 150},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceSocialSalaryBenefit)}', datafield: 'insuranceSalary', width: 200, editable: false,
							cellsalign: 'right', columntype : 'numberinput',
							cellsrenderer: function (row, column, value) {
								if(value){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
								}
			 		 		}	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceStatusCondBenefitShort)}', datafield: 'statusConditionBenefit', width: 150,  columngroup: 'benefitCondition'},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceTimeCondBenefitShort)}', datafield: 'timeConditionBenefit', width: 150,  
							columngroup: 'benefitCondition', columntype: 'datetimeinput', cellsformat: 'MM/yyyy' 
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDateLeave', width: 130, 
							columngroup: 'leaveGroup', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy'
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDateLeave', width: 130,
							columngroup: 'leaveGroup', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy'	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.HRCommonTotal)}', datafield: 'totalDateLeave', editable: false, width: 120, columngroup: 'leaveGroup',
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								var fromDate = data.fromDateLeave;
								var thruDate = data.thruDateLeave;
								if(fromDate && thruDate){
									var difference_ms = Math.abs(fromDate.getTime() - thruDate.getTime());
									return '<span>' + Math.round(difference_ms/ONE_DAY) + '</span>';
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.DateAccumulatedLeaveYTD)}', datafield: 'accumulatedLeave', width: 140, columngroup: 'dateLeaveActual'},
						{text: '${StringUtil.wrapString(uiLabelMap.AllowanceAmountInPeriod)}', datafield: 'allowanceAmount', width: 170,
							cellsrenderer: function (row, column, value) {
								if(value){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
								}
			 		 		}	
						}
"/>

<#assign columngroups = "{text: '${StringUtil.wrapString(uiLabelMap.YearOfBirth)}', align: 'center', name: 'partyBirthDate'},
		 {text: '${StringUtil.wrapString(uiLabelMap.InsuranceBenefitCondition)}', name: 'benefitCondition', align: 'center'},
		 {text: '${StringUtil.wrapString(uiLabelMap.DateLeaveActual)}', name: 'dateLeaveActual', align: 'center'},
		 {text: '${StringUtil.wrapString(uiLabelMap.HRCommonInPeriod)}', name: 'leaveGroup', align: 'center', parentgroup: 'dateLeaveActual'},
 "/>						
</script>		
<script type="text/javascript">
$(document).ready(function () {
	createJqxGridSearchEmpl();
	initJqxWindowSearchEmpl();
});
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
	    //getPartyInsuranceSocialNbr(data.partyId);
	});
}

function initJqxWindowSearchEmpl(){
	$('#popupWindowEmplList').jqxWindow({
	    showCollapseButton: true, autoOpen: false, maxWidth: "80%", minWidth: "50%", maxHeight: 520, height: 520, width: "80%", isModal: true, 
	    theme:'olbius', collapsed:false,
	    initContent: function () {  
	    	initJqxSplitter();
	    }
	});
}

function initJqxSplitter(){
	$("#splitterEmplList").jqxSplitter({  width: '100%', height: '100%', panels: [{ size: '25%'}, {size: '75%'}] });
}

function jqxTreeEmplListSelect(event){
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	var tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}
</script>
<#if expandedList?has_content>
	<#assign expandTreeId=expandedList[0]>
<#else>
	<#assign expandTreeId="">
</#if>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId=expandTreeId isDropDown="false" width="100%" height="100%" expandAll="false"/>
	
<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>
<div class="widget-box transparent no-bottom-border">
	<div class="widget-header">
		<h4>${uiLabelMap.SocialInsuranceBenefits}</h4>
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
		<div class="row-fluid" id="jqxGridInsuranceContainer" style="display: none;">
			<div class="row-fluid">
				<div class="span12" style="text-align: right;">
					<div class='row-fluid margin-bottom10'>
						<button id="addNewParty" class="grid-action-button icon-plus-sign">${uiLabelMap.accAddNewRow}</button>
					</div>
				</div>	
			</div>
			<div class="row-fluid">
				<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist  showtoolbar="false" columngrouplist=columngroups
					 filterable="false" deleterow="false" editable="true" addrow="false"
					 url="" id="jqxgrid" jqGridMinimumLibEnable="false" 
					 removeUrl="" deleteColumn="" sortable="false"
					 updateUrl="" 
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
			${StringUtil.wrapString(uiLabelMap.AddEmplReceiveBenefit)}
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
						<label class="control-label asterisk">${StringUtil.wrapString(uiLabelMap.InsuranceBenefitType)}</label>
					</div>
					<div class="span7">
						<div id="benefitType"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.InsuranceParticipatePeriod}</label>
					</div>
					<div class="span7">
						<div id="insuranceParticipatePeriodTooltip">
							<input id="insuranceParticipatePeriod"/>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.InsuranceSocialSalaryBenefit}</label>
					</div>
					<div class="span7">
						<div id="insuranceSocialSalaryBenefit"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.InsuranceStatusCondBenefit}</label>
					</div>
					<div class="span7">
						<input type="text" id="insuranceStatusCondBenefit">
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.InsuranceTimeCondBenefit}</label>
					</div>
					<div class="span7">
						<div id="insuranceTimeCondBenefit"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.LeaveFromDate}</label>
					</div>
					<div class="span7">
						<div id="leaveFromDate"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.LeaveThruDate}</label>
					</div>
					<div class="span7">
						<div id="leaveThruDate"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.DateAccumulatedLeaveYTDLeave}</label>
					</div>
					<div class="span7">
						<div id="accumulatedLeaveYTD"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.AllowanceAmountInPeriod}</label>
					</div>
					<div class="span7">
						<div id="allowanceAmountInPeriod"></div>
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
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startDate = Static["org.ofbiz.base.util.UtilDateTime"].getDayStart(nowTimestamp) />
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
	initBtnEvent();
	initJqxDateTimeInput();
	initJqxNumberInput();
	initJqxInputText();
	initJqxMaskInput();
	initJqxDropdownlist();
	initJqxWindow();
	initJqxNotification();
	initJqxValidator();
});

function initCustomTimePeriodDropDownlist(){
	var sourceCustomTimeYear = {
			localdata: yearCustomTimePeriod,
            datatype: "array"
	};
	var dataAdapter = new $.jqx.dataAdapter(sourceCustomTimeYear);
	
	$('#yearCustomTime').jqxDropDownList({source: dataAdapter, displayMember: "periodName", valueMember: "customTimePeriodId", 
		height: 25, width: 150, theme: 'olbius'
    });	
	
	$("#monthCustomTime").jqxDropDownList({displayMember: "periodName", valueMember: "customTimePeriodId", height: 25, width: 150, theme: 'olbius'});
	
	$("#timesSetting").jqxDropDownList({height: 25, width: 150, theme: 'olbius', autoDropDownHeight: true, placeHolder:'',
		displayMember: "sequenceNum", valueMember: "insAllowancePaymentDeclId"	
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

function initJqxDateTimeInput(){
	$("#insuranceTimeCondBenefit").jqxDateTimeInput({ width: '97%', height: '25px', theme: 'olbius', showCalendarButton: false, formatString: 'MM/yyyy'});
	$("#leaveFromDate").jqxDateTimeInput({ width: '97%', height: '25px', theme: 'olbius'});
	$("#leaveThruDate").jqxDateTimeInput({ width: '97%', height: '25px', theme: 'olbius'});
}

function initJqxNumberInput(){
	$("#accumulatedLeaveYTD").jqxNumberInput({width: '97%', height: '25px', spinButtons: false, theme: 'olbius', decimalDigits: 1, digits: 2});
	$("#allowanceAmountInPeriod").jqxNumberInput({width: '97%', height: '25px', spinButtons: false, theme: 'olbius', decimalDigits: 0, digits: 9});
	$("#insuranceSocialSalaryBenefit").jqxNumberInput({width: '97%', height: '25px', spinButtons: false, theme: 'olbius', decimalDigits: 0, digits: 9});
}

function initJqxInputText(){
	$("#insuranceStatusCondBenefit").jqxInput({height: 20, width: '95%', theme: 'olbius'});
}

function initJqxMaskInput(){
	$("#insuranceParticipatePeriod").jqxMaskedInput({ width: '95%', height: 20, mask: '##-##', theme: 'olbius'});
	createJqxTooltip($("#insuranceParticipatePeriodTooltip"), '<div style="text-align: left"><b>-${uiLabelMap.HRCommonEnter}</b><br/><b>-${uiLabelMap.HRCommonYearMonth}</b><br/><b>-${uiLabelMap.InsuranceParticipatePeriodNotes}</b></div>');
}

function initJqxDropdownlist(){
	var source = {
			localdata: insuranceAllowanceBenefitTypeArr,
            datatype: "array"
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	$("#benefitType").jqxDropDownList({source: dataAdapter, displayMember: "description", valueMember: "benefitTypeId", 
		height: 25, width: '97%', theme: 'olbius'
    });	
}

function initJqxWindow(){
	$("#popupWindow").jqxWindow({
		showCollapseButton: false, isModal: true, height: 500, width: 520,
        autoOpen: false, theme: 'olbius',
        initContent: function () {
           
        }
	});
	$("#popupWindow").on('close', function(event){
		GridUtils.clearForm($(this));
		$('#popupWindow').jqxValidator('hide');
	});
	$("#popupWindow").on('open', function(event){
		var monthCustomTimeSource = $("#monthCustomTime").jqxDropDownList('source');
		if(monthCustomTimeSource){
			var data = monthCustomTimeSource._source.localdata;
			var selectItem = $("#monthCustomTime").jqxDropDownList('getSelectedItem');
			var index = selectItem.index;
			var fromDate = new Date(data[index].fromDate);
			var thruDate = new Date(data[index].thruDate);
			$("#leaveFromDate").val(fromDate);
			$("#leaveThruDate").val(thruDate);
			$("#insuranceTimeCondBenefit").val(fromDate);
		}
	});
}

function initCustomTimeDropdownlistEvent(){
	$("#timesSetting").on('select', function(event){
		var args = event.args;
		if(args){
			var item = args.item;
			var insAllowancePaymentDeclId = item.value;
			var tmpS = $("#jqxgrid").jqxGrid('source');
			tmpS._source.url = "jqxGeneralServicer?sname=getPartyInsuranceAllowancePayment&hasrequest=Y&insAllowancePaymentDeclId=" + insAllowancePaymentDeclId;
			$("#jqxgrid").jqxGrid('source', tmpS);
		}
	});
	
	$("#monthCustomTime").on('select', function(event){
		var args = event.args;
		if(args){
			var item = args.item;
			var value = item.value;
			getInsuranceAllowancePaymentDecl(value, "INDEX", 0);				
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

function createNewInsuranceAllowancePaymentDecl(){
	var customTimePeriodId = $("#monthCustomTime").val();
	$("#jqxNtf").jqxNotification('closeLast');
	$.ajax({
		url: 'createNewInsuranceAllowancePaymentDecl',
		data: {customTimePeriodId: customTimePeriodId},
		type: 'POST',
		success: function(data){
			if(data.responseMessage == "success"){
				//$('#jqxgrid').jqxGrid('updatebounddata');
				$("#jqxNtfContent").text(data.successMessage);
				$("#jqxNtf").jqxNotification({template: 'info'});
				$("#jqxNtf").jqxNotification("open");
				refreshTimesSettingJqxDropDownList(data.insAllowancePaymentDeclId);	
			}else{
				$("#jqxNtfContent").text(data.errorMessage);
				$("#jqxNtf").jqxNotification({template: 'error'});
				$("#jqxNtf").jqxNotification("open");
			}
		}, 
		error: function(jqXHR, textStatus, errorThrown){
			
		},
		complete: function( jqXHR, textStatus){
			
		}
	});
}

function refreshTimesSettingJqxDropDownList(selectItem){
	var item = $("#monthCustomTime").jqxDropDownList('getSelectedItem');
	getInsuranceAllowancePaymentDecl(item.value, "ITEM", selectItem);
}

function getInsuranceAllowancePaymentDecl(customTimePeriodId, selectType, select){
	$("#timesSetting").jqxDropDownList('clearSelection');	
	$.ajax({
		url: 'getInsAllowancePaymentByCustomPeriod',
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

function initBtnEvent(){
	$("#addNew").click(function(event){
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.CreateNewInsuranceDeclaration)}",
				[
				{
					"label" : "${uiLabelMap.CommonSubmit}",
	    		    "class" : "btn-primary btn-mini icon-ok",
	    		    "callback": function() {
	    		    	createNewInsuranceAllowancePaymentDecl();   	
	    		    }		
				},
				{
					"label" : "${uiLabelMap.CommonClose}",
	    		    "class" : "btn-danger btn-mini icon-remove",
	    		    "callback": function() {
	    		    	
	    		    }
				}
				]
		);
	});
	
	$("#addNewParty").click(function(event){
		openJqxWindow($("#popupWindow"));
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
		    		    	createInsuranceAllowancePaymentDecl();
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
	    		 }
				 ]		
		);
	});
	
	$("#btnCancel").click(function(event){
		$("#popupWindow").jqxWindow('close');
	});
}

function createInsuranceAllowancePaymentDecl(){
	var data = {};
	data.partyId = $("#partyIdNew").jqxInput('val').value;
	data.insAllowancePaymentDeclId = $("#timesSetting").jqxDropDownList('val');
	
	data.benefitTypeId = $("#benefitType").val();
	data.insuranceParticipatePeriod = $("#insuranceParticipatePeriod").val();
	data.insuranceSalary = $("#insuranceSocialSalaryBenefit").val();
	data.statusConditionBenefit = $("#insuranceStatusCondBenefit").val();
	var timeConditionBenefit = $("#insuranceTimeCondBenefit").jqxDateTimeInput('val', 'date');
	if(timeConditionBenefit){
		data.timeConditionBenefit = timeConditionBenefit.getTime();
	}
	data.fromDateLeave = $("#leaveFromDate").jqxDateTimeInput('val', 'date').getTime();
	data.leaveThruDate = $("#leaveThruDate").jqxDateTimeInput('val', 'date').getTime();
	data.accumulatedLeave = $("#accumulatedLeaveYTD").val();
	data.allowanceAmount = $("#allowanceAmountInPeriod").val();
	$.ajax({
		url: 'createPartyInsuranceAllowancePaymentDecl',
		data: data,
		type: 'POST',
		success: function(data){
			$("#jqxNtf").jqxNotification('closeLast');
			if(data.responseMessage == "success"){
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

function initJqxValidator(){
	$("#popupWindow").jqxValidator({
		rules:[
			{
				input: "#partyIdNew",
				message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
				rule: 'required'
		  	},
			{
				input: "#benefitType",
				message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
				rule: function(input, commit){
					if(!input.val()){
						return false;
					}
					return true;
				}
		  	},
			{
				input: "#insuranceParticipatePeriod",
				message: "${StringUtil.wrapString(uiLabelMap.ValueIsInvalid?default(''))}",
				rule: function(input, commit){
					var re = /[0-9][0-9]\-[0-9][0-9]/
					if(!input.val().match(re)){
						return false;
					}
					return true;
				}
		  	},
		  	{
				input: "#insuranceSocialSalaryBenefit",
				message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))} ${StringUtil.wrapString(uiLabelMap.CommonAnd)} ${StringUtil.wrapString(uiLabelMap.ValueNotLessThanZero)}",
				rule: function(input, commit){
					if(input.val() < 0){
						return false;
					}
					return true;
				}
		  	},
		  	{
				input: "#leaveFromDate",
				message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
				rule: function(input, commit){
					if(!input.val()){
						return false;
					}
					return true;
				}
		  	},
		  	{
				input: "#leaveThruDate",
				message: "${StringUtil.wrapString(uiLabelMap.ThruDateMustGreaterThanFromDate?default(''))}",
				rule: function(input, commit){
					var thruDate = input.jqxDateTimeInput('val', 'date');		  			
					if(thruDate){
						var fromDate = $("#leaveFromDate").jqxDateTimeInput('val', 'date');
						if(thruDate.getTime() < fromDate.getTime()){
							return false;
						}
						return true;
					}
					return true;
				}
		  	},
		  	{
				input: "#allowanceAmountInPeriod",
				message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))} ${StringUtil.wrapString(uiLabelMap.CommonAnd)} ${StringUtil.wrapString(uiLabelMap.ValueNotLessThanZero)}",
				rule: function(input, commit){
					if(input.val() < 0){
						return false;
					}
					return true;
				}
		  	},
		  	
		]		       
	});
}

function initJqxNotification(){
	$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#notifyContainer"});
}

function createJqxTooltip(element, message, autoHide){
	if(typeof autoHide === "undefined"){
		autoHide = true;
	}
	element.jqxTooltip({ content: message, name: 'movieTooltip', position: 'bottom', theme: 'olbius', autoHide: autoHide});
}

function openJqxWindow(jqxWindowDiv){
	var wtmp = window;
	var tmpwidth = jqxWindowDiv.jqxWindow('width');
	jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
	jqxWindowDiv.jqxWindow('open');
}
</script>