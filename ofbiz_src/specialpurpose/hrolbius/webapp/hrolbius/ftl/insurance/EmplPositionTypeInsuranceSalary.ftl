<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/assets/jsdelys/formatCurrency.js"></script>
<script src="/delys/images/js/generalUtils.js"></script>
<script src="/aceadmin/assets/js/bootbox.min.js" type="text/javascript"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<#assign datafield = "[{name: 'emplPositionTypeId', type: 'string'},
					   {name: 'description', type: 'string'},
					   {name: 'insuranceSalary', type: 'number'},
					   {name: 'allowancePosition', type: 'number'},
					   {name: 'allowanceSeniority', type: 'number'},
					   {name: 'allowanceSeniorityExces', type: 'number'},
					   {name: 'allowanceOther', type: 'number'},
					   {name: 'totalSalary', type: 'number'},
					   {name: 'periodTypeId', type: 'string'},
					   {name: 'fromDate', type: 'date'},
					   {name: 'thruDate', type: 'date'},
					  ]" />

<script type="text/javascript">
var periodTypeArr = [
	<#if periodTypeList?has_content>
		<#list periodTypeList as periodType>
			{
				periodTypeId: '${periodType.periodTypeId}',
				description: '${StringUtil.wrapString(periodType.description?if_exists)}'
			},
		</#list>
	</#if>
];
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.HRSequenceNbr)}', datafield: '', editable: false,	
							groupable: false,  columntype: 'number', width: 50,
							cellsrenderer: function (row, column, value) {
		                    	return '<span>' + (value + 1) + '</span>';
		                    }
					    },
					   {text: '${StringUtil.wrapString(uiLabelMap.EmployeePositionId)}', datafield: 'emplPositionTypeId', width: 120, editable: false},
					   {text: '${StringUtil.wrapString(uiLabelMap.EmplPositionTypeName)}', datafield: 'description', width: 150, editable: false},
					   {text: '${StringUtil.wrapString(uiLabelMap.InsuranceSocialSalary)}', datafield: 'insuranceSalary', width: 150, editable: false,
						   columngroup: 'insuranceSalaryGroup',	
						   cellsrenderer: function (row, column, value) {
								if(value){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
								}
							}
					   },
					   {text: '${StringUtil.wrapString(uiLabelMap.InsuranceAllowancePosition)}', datafield: 'allowancePosition', 
							width: 130, cellsalign: 'right', columngroup:'insuranceSalaryGroup', columntype : 'numberinput',
							cellsrenderer: function (row, column, value) {
								if(value){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceAllowanceSeniority)}(%)', datafield: 'allowanceSeniority', width: 100, cellsalign: 'right', 
							columngroup:'insuranceSalaryGroup', columntype : 'numberinput',
							cellsrenderer: function (row, column, value) {
								if(value){
									return '<span style=\"text-align: right\">' + value * 100 + '%</span>';
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceAllowanceSeniorityExces)}(%)', datafield: 'allowanceSeniorityExces',
							width: 100, cellsalign: 'right', columngroup:'insuranceSalaryGroup', columntype : 'numberinput',
							cellsrenderer: function (row, column, value) {
								if(value){
									return '<span style=\"text-align: right\">' + value * 100 + '%</span>';
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceOtherAllowance)}', datafield: 'allowanceOther', width: 120, cellsalign: 'right', 
							columngroup:'insuranceSalaryGroup', columntype : 'numberinput',
							cellsrenderer: function (row, column, value) {
								if(value){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceSocialSalaryTotal)}', datafield: 'totalSalary', editable: false,	
							 columntype: 'number', width: 150, columngroup:'insuranceSalaryGroup',
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								var salary = data.insuranceSalary;
								if(salary){
									var valueRet = salary;
									if(data.allowancePosition){
										valueRet += data.allowancePosition;
									}
									if(data.allowanceSeniority){
										valueRet += salary * data.allowanceSeniority; 
									}
									if(data.allowanceSeniorityExces){
										valueRet += salary * data.allowanceSeniorityExces;
									}
									if(data.allowanceOther){
										valueRet += data.allowanceOther;
									}
									return '<span>' + formatcurrency(valueRet) + '</span>';
								}
								return '<span>' + value + '</span>';
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonPeriodType)}', datafield: 'periodTypeId', width: 140,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < periodTypeArr.length; i++){
									if(periodTypeArr[i].periodTypeId == value){
										return '<span>' + periodTypeArr[i].description + '<span>';
									}
								}
								return '<span>' + value + '<span>';
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDate', width: 130, cellsformat: 'dd/MM/yyyy', 
							columntype: 'datetimeinput'
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', width: 130, cellsformat: 'dd/MM/yyyy', 
							columntype: 'datetimeinput'
						}
					   "/>	
<#assign columngroups = "{text: '${StringUtil.wrapString(uiLabelMap.HrolbiusAmountSalary)}', align: 'center', name: 'insuranceSalaryGroup'}"/>	

</script>	
<div class="row-fluid">
	<div id="notifyContainer">
		<div id="jqxNtf">
			<div id="jqxNtfContent"></div>
		</div>
	</div>
</div>				  
<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist showtoolbar="true" columngrouplist=columngroups
	 filterable="false" alternativeAddPopup="popupAddRow" deleterow="false" editable="true" addrow="true" addType="popup"
	 url="" id="jqxgrid" jqGridMinimumLibEnable="false"
	 removeUrl="" deleteColumn="" 
	 updateUrl="" 
	 editColumns="" customControlAdvance="<div id='jqxDatimeInput'></div>"
	 selectionmode="singlerow" 
/>

<script type="text/javascript">
<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
<#assign startMonth = Static["org.ofbiz.base.util.UtilDateTime"].getMonthStart(nowTimestamp)/>
<#assign endMonth = Static["org.ofbiz.base.util.UtilDateTime"].getMonthEnd(nowTimestamp, timeZone, locale)/>
var emplPositionTypeArr = [
	<#if emplPositionTypeList?has_content>
		<#list emplPositionTypeList as emplPositionType>
			{
				emplPositionTypeId: '${emplPositionType.emplPositionTypeId}',
				description: '${StringUtil.wrapString(emplPositionType.description?if_exists)} [${emplPositionType.emplPositionTypeId}]'
			},
		</#list>
	</#if>
];

$(document).ready(function () {
	$("#jqxgrid").on('loadCustomControlAdvance', function(){
		$("#jqxDatimeInput").jqxDateTimeInput({ width: 220, height: 25,  selectionMode: 'range', theme: 'olbius'});
		$("#jqxDatimeInput").on('change', function (event){
			var selection = $("#jqxDatimeInput").jqxDateTimeInput('getRange');
			refreshGridData(selection.from, selection.to);
		});
		$("#jqxDatimeInput").jqxDateTimeInput('setRange', new Date(${startMonth.getTime()}), new Date(${endMonth.getTime()}));
	});
	initJqxNumberInput();
	initJqxDateTimeInput();
	initJqxDropdownlist();
	initJqxValidator();
	initJqxWindow();
	initBtnEvent();
	initJqxNotification();
});

function initJqxWindow(){
	$("#popupAddRow").jqxWindow({showCollapseButton: false,autoOpen: false,
		maxWidth: 500, height: 460, width: 500, isModal: true, theme:'olbius',
		initContent: function(){
		}	
	});
	$("#popupAddRow").on('open', function(event){
		$("#fromDate").val(new Date(${startMonth.getTime()}));
	});
	
	$("#popupAddRow").on('close', function(event){
		GridUtils.clearForm($(this));
		$("#popupAddRow").jqxValidator('hide');
	});
}

function initJqxValidator(){
	$("#popupAddRow").jqxValidator({
		rules:[
			{
				input: "#emplPositionTypeId",
				message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
				rule: function(input, commit){
					if(!input.val()){
						return false;
					}
					return true;
				}
		  	},
		  	{
		  		input: "#insuranceSocialSalary",
		  		message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))} ${StringUtil.wrapString(uiLabelMap.CommonAnd)} ${StringUtil.wrapString(uiLabelMap.ValueNotLessThanZero)}",
		  		rule: function(input, commit){
					if(!input.val() || input.val() < 0){
						return false;
					}
					return true;
				}
		  	},
		  	{
		  		input: "#insuranceAllowancePosition",
		  		message: "${StringUtil.wrapString(uiLabelMap.ValueNotLessThanZero)}",
		  		rule: function(input, commit){
					if(input.val() && input.val() < 0){
						return false;
					}
					return true;
				}
		  	},
		  	{
		  		input: "#insuranceAllowanceSeniority",
		  		message: "${StringUtil.wrapString(uiLabelMap.ValueNotLessThanZero)}",
		  		rule: function(input, commit){
					if(input.val() && input.val() < 0){
						return false;
					}
					return true;
				}
		  	},
		  	{
		  		input: "#insuranceAllowanceSeniorityExces",
		  		message: "${StringUtil.wrapString(uiLabelMap.ValueNotLessThanZero)}",
		  		rule: function(input, commit){
					if(input.val() && input.val() < 0){
						return false;
					}
					return true;
				}
		  	},
		  	{
		  		input: "#insuranceOtherAllowance",
		  		message: "${StringUtil.wrapString(uiLabelMap.ValueNotLessThanZero)}",
		  		rule: function(input, commit){
					if(input.val() && input.val() < 0){
						return false;
					}
					return true;
				}
		  	},
		  	{
		  		input: "#fromDate",
		  		message: "${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}",
		  		rule: function(input, commit){
					if(!input.val()){
						return false;
					}
					return true;
				}
		  	},
		  	{
		  		input: "#thruDate",
		  		message: "${StringUtil.wrapString(uiLabelMap.ThruDateMustGreaterThanFromDate?default(''))}",
		  		rule: function(input, commit){
					var thruDate = input.jqxDateTimeInput('val', 'date');		  			
					if(thruDate){
						var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
						if(thruDate.getTime() < fromDate.getTime()){
							return false;
						}
						return true;
					}
					return true;
				}
		  	}
		]
	});
}

function initJqxNumberInput(){
	$("#insuranceSocialSalary").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: false, theme: 'olbius', decimalDigits: 0});
	$("#insuranceAllowancePosition").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: false, theme: 'olbius', decimalDigits: 0});
	$("#insuranceOtherAllowance").jqxNumberInput({ width: '98%', height: '25px', spinButtons: false, theme: 'olbius', decimalDigits: 0});
	$("#insuranceAllowanceSeniority").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: false, theme: 'olbius', digits: 3, symbolPosition: 'right', symbol: '%'});
	$("#insuranceAllowanceSeniorityExces").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: false, theme: 'olbius', digits: 3, symbolPosition: 'right', symbol: '%'});
}

function initJqxDropdownlist(){
	var source = {
			localdata: emplPositionTypeArr,
			datatype: "array"
	};
	var dataAdapter = new $.jqx.dataAdapter(source);
	$("#emplPositionTypeId").jqxDropDownList({source: dataAdapter, displayMember: "description", valueMember: "emplPositionTypeId", 
		height: 25, width: '98%', theme: 'olbius'
    });	
	
	var sourcePeriodType = {
			localdata: periodTypeArr,
			datatype: "array"
	};
	var dataAdapterPeiodType = new $.jqx.dataAdapter(sourcePeriodType);
	$("#periodTypeId").jqxDropDownList({source: dataAdapterPeiodType, displayMember: "description", valueMember: "periodTypeId", 
		height: 25, width: '98%', theme: 'olbius'
    });
	if(periodTypeArr.length < 8){
		$("#periodTypeId").jqxDropDownList({autoDropDownHeight: true});
	}
}

function initJqxDateTimeInput(){
	$("#fromDate").jqxDateTimeInput({width: '98%', height: 25, formatString: 'dd/MM/yyyy', theme: 'olbius'});
	$("#thruDate").jqxDateTimeInput({width: '98%', height: 25, formatString: 'dd/MM/yyyy', theme: 'olbius'});
	$("#thruDate").val(null);
}

function initJqxNotification(){
	$("#jqxNtf").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: false, template: "info", appendContainer: "#notifyContainer"});
}

function initBtnEvent(){
	$("#btnCancel").click(function(event){
		$("#popupAddRow").jqxWindow('close');
	});
	
	$("#btnSave").click(function(event){
		var valid = $("#popupAddRow").jqxValidator('validate');
		if(!valid){
			return;
		}
		var emplPositionTypeSelect = $("#emplPositionTypeId").jqxDropDownList('getSelectedItem');
		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ConfirmAddSettingInsuranceSalary)} " + emplPositionTypeSelect.label + "?",
			[
			{
				"label" : "${uiLabelMap.CommonSubmit}",
    		    "class" : "btn-primary btn-mini icon-ok",
    		    "callback": function() {
    		    	createInsuranceSalary();
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
}

function createInsuranceSalary(){
	var data = {};
	data.emplPositionTypeId = $("#emplPositionTypeId").jqxDropDownList('getSelectedItem').value;
	data.insuranceSalary = $("#insuranceSocialSalary").val();
	data.allowancePosition = $("#insuranceAllowancePosition").val();
	data.allowanceSeniority = $("#insuranceAllowanceSeniority").val()/100;
	data.allowanceSeniorityExces = $("#insuranceAllowanceSeniorityExces").val()/100;
	data.allowanceOther = $("#insuranceOtherAllowance").val();
	data.fromDate = $("#fromDate").jqxDateTimeInput('val', 'date').getTime();
	data.periodTypeId = $("#periodTypeId").val();
	var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
	if(thruDate){
		data.thruDate = $("#thruDate").jqxDateTimeInput('val', 'date').getTime();
	}
	
	$.ajax({
		url: 'createEmplPositionTypeInsuranceSalary',
		data: data,
		type: 'POST',
		success: function(response){
			$("#popupAddRow").jqxWindow('close');
			$("#jqxNtf").jqxNotification('closeLast');
			if(response.responseMessage == 'success'){
				$("#jqxNtfContent").text(response.successMessage);
				$("#jqxNtf").jqxNotification({template: 'info'});
				$("#jqxNtf").jqxNotification("open");
				$("#jqxgrid").jqxGrid('updatebounddata');
			}else{
				$("#jqxNtfContent").text(response.errorMessage);
				$("#jqxNtf").jqxNotification({template: 'error'});
				$("#jqxNtf").jqxNotification("open");
			}
		}
	});
}

function refreshGridData(fromDate, thruDate){
	var source = $("#jqxgrid").jqxGrid('source');
	source._source.url = 'jqxGeneralServicer?sname=JQGetEmplPosTypeInsuranceSalary&hasrequest=Y&fromDate=' + fromDate.getTime() + '&thruDate=' + thruDate.getTime();
	$("#jqxgrid").jqxGrid('source', source);
}
</script>
<div class="row-fluid">
	<div id="popupAddRow" class="hide">
		<div id="windowHeader">
			${StringUtil.wrapString(uiLabelMap.CommonAddSetting)}
		</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.EmployeePositionId}</label>
					</div>
					<div class="span7">
						<div id="emplPositionTypeId"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.InsuranceSocialSalary}</label>
					</div>
					<div class="span7">
						<div id="insuranceSocialSalary"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.InsuranceAllowancePositionFull}</label>
					</div>
					<div class="span7">
						<div id="insuranceAllowancePosition"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.InsuranceAllowanceSeniorityFull}</label>
					</div>
					<div class="span7">
						<div id="insuranceAllowanceSeniority"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.InsuranceAllowanceSeniorityExcesFull}</label>
					</div>
					<div class="span7">
						<div id="insuranceAllowanceSeniorityExces"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.InsuranceOtherAllowanceFull}</label>
					</div>
					<div class="span7">
						<div id="insuranceOtherAllowance"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.CommonPeriodType}</label>
					</div>
					<div class="span7">
						<div id="periodTypeId"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.CommonFromDate}</label>
					</div>
					<div class="span7">
						<div id="fromDate"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.CommonThruDate}</label>
					</div>
					<div class="span7">
						<div id="thruDate"></div>
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