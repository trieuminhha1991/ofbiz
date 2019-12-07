<#include "script/ViewListEmplPayrollParametersScript.ftl" />
<#assign dataFields = "[{name: 'partyId', type: 'string'},
						{name: 'partyName', type: 'string'},
						{name: 'department', type: 'string'},
						{name: 'emplPositionTypeId', type: 'string'},
						{name: 'emplBonusAmount', type: 'number'},
						{name: 'emplAllowances', type: 'number'}
						]" />
<script type="text/javascript">
	<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.EmployeeId)}', datafield: 'partyId', editable: false, cellsalign: 'left', width: 130},
						   {text: '${StringUtil.wrapString(uiLabelMap.EmployeeName)}', datafield: 'partyName', width: 160},
						   {text: '${StringUtil.wrapString(uiLabelMap.CommonDepartment)}', datafield: 'department', cellsalign: 'left', width: 200},
						   {text: '${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}', datafield: 'emplPositionTypeId', width: 180},
						   {text: '${StringUtil.wrapString(uiLabelMap.HRCommonBonus)}', datafield: 'emplBonusAmount', width: 180,filterType : 'number',
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";				
								}   
						   },
						   {text: '${StringUtil.wrapString(uiLabelMap.HREmplAllowances)}', datafield: 'emplAllowances',filterType : 'number',
							   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
									return \"<div style='margin-top: 3px; text-align: right; margin-right: 3px;'>\" + formatcurrency(value) + \"</div>\";				
								}   
						   }
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
		<h4>${uiLabelMap.BonusAndAllowancesStaff}</h4>
		<div class="widget-toolbar none-content">
			<button id="removeFilter" class="grid-action-button icon-filter open-sans">${StringUtil.wrapString(uiLabelMap.HRCommonRemoveFilter)}</button>
			<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>
				<button id="addNew" class="grid-action-button icon-plus-sign open-sans" style="font-size: 14px">${uiLabelMap.accAddNewRow}</button>
				<button class="grid-action-button fa-database open-sans" style="float: right; font-size: 14px" id="settingPyrllPosTypeConfig" 
					title="${StringUtil.wrapString(uiLabelMap.SettingPayrollParamByPosTypeConfigFull)}">
					${uiLabelMap.SettingPayrollParamByPosTypeConfig}
				</button>
			</#if>
		</div>
	</div>
	<div class="widget-body">
		<div class="row-fluid">
			<div class="span12">
				<div class="span6">
					<div class='row-fluid margin-bottom10'>
						<div id="dateTimeInput"></div>						
					</div>
				</div>
				<div class="span6">
					<div class='row-fluid margin-bottom10'>						
						<div class="span12" style="margin-right: 15px">
							<div id="jqxDropDownButton" class="pull-right">
								<div style="border: none;" id="jqxTree">
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<@jqGrid filtersimplemode="true" addType="popup" dataField=dataFields columnlist=columnlist clearfilteringbutton="true" showtoolbar="false" 
				 filterable="true" alternativeAddPopup="popupWindowAddPartyAttend" deleterow="false" editable="false" addrow="false"
				 url="" id="jqxgrid" jqGridMinimumLibEnable="false"
				 initrowdetails="true" initrowdetailsDetail=rowDetails rowdetailsheight="250" rowdetailstemplateAdvance=rowdetailstemplateAdvance
				 removeUrl="" deleteColumn="" updateUrl="" editColumns="" selectionmode="singlerow" 
				 />					
		</div>
	</div>
</div>

<script type="text/javascript" src="/hrresources/js/payroll/ViewListEmplPayrollParameters.js"></script>
<#if security.hasEntityPermission("HR_MGRPAYROLL", "_CREATE", session)>
	<#include "CreateEmplPayrollParameters.ftl"/>
</#if>
