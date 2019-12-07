<#include "script/ViewEmplPositionTypeInsSalaryListScript.ftl"/>
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
	 removeUrl="" deleteColumn="" showlist="false"
	 updateUrl="" 
	 editColumns="" customControlAdvance="<div id='jqxDatimeInput'></div>"
	 selectionmode="singlerow" 
/>

<script type="text/javascript">

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
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSave">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/insurance/ViewEmplPositionTypeInsSalaryList.js"></script>