<style type="text/css">
	#horizontalScrollBarjqxgrid {
		visibility: inherit !important;
	}
</style>
<#include "script/SocialInsuranceBenefitsScript.ftl"/>
<#assign datafield = "[{name: 'partyId', type: 'string'},
					   {name: 'benefitTypeId', type: 'string'},
					   {name: 'emplLeaveId', type: 'string'},
					   {name: 'insAllowancePaymentDeclId', type: 'string'},
					   {name: 'partyName', type: 'string'},	
					   {name: 'genderMale', type: 'date'},
					   {name: 'genderFemale', type: 'date'},
					   {name: 'insuranceSocialNbr', type: 'string'},
					   {name: 'insuranceParticipatePeriod', type: 'string'},
					   {name: 'insuranceSalary', type: 'string'},
					   {name: 'fromDateLeave', type: 'date'},
					   {name: 'thruDateLeave', type: 'date'},
					   {name: 'totalDayLeave', type: 'number'},
					   {name: 'accumulatedLeave', type: 'number'},
					   {name: 'allowanceAmount', type: 'number'}
					  ]"/>
					  
<script type="text/javascript">
<#assign columnlist = "{datafield: 'emplLeaveId', hidden: true},
						{datafield: 'insAllowancePaymentDeclId', hidden: true},
						{text: '${StringUtil.wrapString(uiLabelMap.HRSequenceNbr)}', datafield: '', editable: false,	
							groupable: false,  columntype: 'number', width: 50, sortable: false, filterable: false,
							cellsrenderer: function (row, column, value) {
						    	return '<span>' + (value + 1) + '</span>';
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceBenefitType)}', datafield: 'benefitTypeId', filterType : 'checkedlist',
							width: '15%', sortable: false,columntype: 'dropdownlist', editable: false,
							cellsrenderer: function (row, column, value) {
								for(var i = 0; i < insuranceAllowanceBenefitTypeArr.length; i++){
									if(insuranceAllowanceBenefitTypeArr[i].benefitTypeId == value){
										return '<span>' + insuranceAllowanceBenefitTypeArr[i].name + '</span>';
									}
								}
								return '<span>' + value + '</span>';
							},
							createeditor: function (row, column, editor, celltext, cellwidth, cellheight) {
								var source = {
										localdata: insuranceAllowanceBenefitTypeArr,
						                datatype: 'array'
								}
								var dataAdapter = new $.jqx.dataAdapter(source);
								editor.jqxDropDownList({source: dataAdapter, displayMember: 'description', valueMember: 'benefitTypeId', 
									height: cellheight, width: cellwidth, dropDownWidth: 300,
								});
								if(insuranceAllowanceBenefitTypeArr.length < 8){
									editor.jqxDropDownList({autoDropDownHeight: true});
								}
							},
							createfilterwidget : function(column, columnElement, widget){
								var source = {
										localdata : insuranceAllowanceBenefitTypeArr,
										datatype : 'array'
								};
								var filterBoxAdapter = new $.jqx.dataAdapter(source, {autoBind : true});
								var dataFilter = filterBoxAdapter.records;
								//dataFilter.splice(0,0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source : dataFilter, valueMember : 'benefitTypeId', displayMember : 'description', dropDownWidth: 280});
								if(dataFilter.length <= 8){
									widget.jqxDropDownList({autoDropDownHeight : true});
								}else{
									widget.jqxDropDownList({autoDropDownHeight : false});
								}
							},
						},
						{text: '${StringUtil.wrapString(uiLabelMap.HRFullName)}', datafield: 'partyName', width: 130, editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonMale)}', datafield: 'genderMale', width: 90, editable: false,
							columngroup: 'partyBirthDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',
							createeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({width: 90, height: 25, formatString: 'dd/MM/yyyy'});
							},
							cellbeginedit: function (row, datafield, columntype) {
								var data = $('#jqxgrid').jqxGrid('getrowdata', row);
								if (!data.genderMale){
						            return false;
						        }
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFemale)}', datafield: 'genderFemale', width: 90, editable: false, 
							columngroup: 'partyBirthDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',
							createeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({width: 90, height: 25, formatString: 'dd/MM/yyyy'});
							},
							
						},
						{text: '${StringUtil.wrapString(uiLabelMap.SocialInsuranceNbrIdentify)}', datafield: 'insuranceSocialNbr', width: 130, editable: false},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceParticipatePeriod)}', datafield: 'insuranceParticipatePeriod', width: 150, editable : false},
						{text: '${StringUtil.wrapString(uiLabelMap.InsuranceSocialSalaryBenefit)}', datafield: 'insuranceSalary', width: 200, editable: false,
							cellsalign: 'right', columntype : 'numberinput',
							cellsrenderer: function (row, column, value) {
								if(value){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
								}
			 		 		}	
						},
						{text: '${StringUtil.wrapString(uiLabelMap.HRCommonInPeriod)}', datafield: 'totalDayLeave', editable: true, width: '8%', columntype: 'numberinput',
							filterType :'number', columngroup: 'leaveGroup',  cellsalign: 'right'
						},
						{text: '${StringUtil.wrapString(uiLabelMap.DateAccumulatedLeaveYTD)}', datafield: 'accumulatedLeave', width: '15%', columngroup: 'dateLeaveActual',
							columntype: 'numberinput', filterType :'number', columngroup: 'leaveGroup', cellsalign: 'right'
						},
						{text: '${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}', datafield: 'allowanceAmount', width: '15%', cellsalign: 'right', editable: false, 
							filterType :'number', columngroup: 'nbrOfProposal', columntype: 'numberinput',
							cellsrenderer: function (row, column, value) {
								if(value){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";
								}
			 		 		},
			 		 		createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
			 		 			editor.jqxNumberInput({width: cellwidth, heigth: cellheight, inputMode: 'advanced', decimalDigits: 0});
			 		 		}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonFromDate)}', datafield: 'fromDateLeave', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: '11%', 
							editable: false, columngroup: 'dayGenerate'
						},
						{text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDateLeave', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: '11%', 
								editable: false, columngroup: 'dayGenerate'
						}
						
"/>

<#assign columngroups = "{text: '${StringUtil.wrapString(uiLabelMap.NumberOfProposal)}', name: 'nbrOfProposal', align: 'center'},
 						{text: '${StringUtil.wrapString(uiLabelMap.HRNumberDayLeave)}', name: 'leaveGroup', align: 'center', parentgroup: 'nbrOfProposal'},
  						{text: '${StringUtil.wrapString(uiLabelMap.DayGenerate)}', name: 'dayGenerate', align: 'center'}
 "/>						
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
		<h4>${uiLabelMap.InsBenefitSicknessPregnancyTitle}</h4>
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
						<div id="timeSetting"></div>
					</div>
					<div class="span3" style="margin: 0">
						<button id="addNew" class="grid-action-button icon-plus-sign open-sans">${uiLabelMap.CommonCreate}</button>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid" id="jqxGridInsuranceContainer" style="visibility: hidden;">
			<div class="row-fluid">
				<div class="span12" style="text-align: right;">
					<div class='row-fluid margin-bottom10' style="float: right;">
						<!-- <button id="addNewParty" class="grid-action-button icon-plus-sign open-sans" style="float: right;">${uiLabelMap.accAddNewRow}</button> -->
						
						<button id="deleteBtn" class="grid-action-button icon-trash open-sans" style="float: right;">${uiLabelMap.Delete}</button>
						<button id="removeFilter" class="grid-action-button icon-filter open-sans" style="float: right;">${uiLabelMap.HRCommonRemoveFilter}</button>
						<button id="getInfoEmplLeave" class="grid-action-button fa-database open-sans" style="float: right;" title="${uiLabelMap.GetInformationEmplLeaveFromTimeMgr}">
							${uiLabelMap.ChooseEmployeeFromEmplLeave}
						</button>
						<button id="excelBtn" class="grid-action-button icon-file-excel-o open-sans">${uiLabelMap.ExportExcel}</button>
					</div>
				</div>	
			</div>
			<div class="row-fluid">
				<@jqGrid filtersimplemode="true" dataField=datafield columnlist=columnlist  showtoolbar="false" columngrouplist=columngroups
					 filterable="true" deleterow="true" editable="true" addrow="false"
					 url="" id="jqxgrid" jqGridMinimumLibEnable="false" 
					 removeUrl="jqxGeneralServicer?jqaction=D&sname=deletePartyInsuranceAllowancePayment" 
					 deleteColumn="insAllowancePaymentDeclId;emplLeaveId" sortable="false"
					 updateUrl="jqxGeneralServicer?jqaction=U&sname=updatePartyInsuranceAllowancePayment" 
					 editColumns="insAllowancePaymentDeclId;emplLeaveId;benefitTypeId;insuranceSocialNbr;insuranceSalary(java.math.BigDecimal);statusConditionBenefit;fromDateLeave(java.sql.Timestamp);thruDateLeave(java.sql.Timestamp);accumulatedLeave(java.lang.Double);totalDayLeave(java.math.BigDecimal);allowanceAmount(java.math.BigDecimal)" 
					 selectionmode="singlerow" 
				/>
			</div>
		</div>
	</div>
</div>
<div class="row-fluid">
	<div id="emplLeaveBenefitListWindow" class="hide">
		<div>${uiLabelMap.HREmployeeLeaveList}</div>
		<div class="form-window-container">
			<div class='row-fluid margin-bottom10'>
				<div class="span12" style="margin-right: 15px">
					<div id="dropDownButtonEmplLeave" style="margin-top: 5px;" class="pull-right">
						<div style="border: none;" id="jqxTreeEmplLeave">
								
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div id="emplLeaveListGrid"></div>
			</div>
			<div class="form-action">
				<button id="btnCancelEmplLeave" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSaveEmplLeave">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
				<button type="button" class='btn btn-success form-action-button pull-right' id="btnCalcAllowanceAmount">
					<i class='fa fa-calculator'></i>${uiLabelMap.HrCalculate}</button>
			</div>
		</div>
	</div>
</div>

<#include "ExportSocialInsuranceBenefitsExcel.ftl"/>
<script type="text/javascript" src="/hrresources/js/insurance/SocialInsuranceBenefitsAction.js"></script>
<script type="text/javascript" src="/hrresources/js/insurance/SocialInsuranceBenefits.js"></script>
<script type="text/javascript" src="/hrresources/js/insurance/SocialInsuranceBenefitsEvent.js"></script>
