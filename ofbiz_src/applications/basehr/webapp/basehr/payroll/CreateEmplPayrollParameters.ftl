<script type="text/javascript">
if(typeof(globalObject) == "undefined" || typeof(globalObject.createJqxTreeDropDownBtn == "undefined")){
	var globalObject = (function(){
		<#assign defaultSuffix = ""/>
		${setContextField("defaultSuffix", defaultSuffix)}
		<#include "component://basehr/webapp/basehr/common/createJqxTree.ftl"/>
		return{
			createJqxTreeDropDownBtn: createJqxTreeDropDownBtn${defaultSuffix?if_exists}
		}
	}());
}

if(typeof(globalVar) == "undefined"){
	globalVar = {};
}
<#if expandedList?has_content>
	globalVar.expandTreeId = "${expandedList[0]}";
</#if>

<#assign nowTimestamp = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp()/>
globalVar.nowTimestamp = ${nowTimestamp.getTime()};
if(typeof(uiLabelMap) == "undefined"){
	uiLabelMap = {};
}
uiLabelMap.CommonRequired = '${uiLabelMap.CommonRequired}';
uiLabelMap.HRCommonNotSetting = '${uiLabelMap.HRCommonNotSetting}';
uiLabelMap.EnterEmployeeId = "${StringUtil.wrapString(uiLabelMap.EnterEmployeeId)}";
uiLabelMap.EmployeeId = "${StringUtil.wrapString(uiLabelMap.EmployeeId)}";
uiLabelMap.EmployeeName = "${StringUtil.wrapString(uiLabelMap.EmployeeName)}";
uiLabelMap.HrCommonPosition = "${StringUtil.wrapString(uiLabelMap.HrCommonPosition)}";
uiLabelMap.CommonDepartment = "${StringUtil.wrapString(uiLabelMap.CommonDepartment)}";
uiLabelMap.CreateEmplPayrollParametersWarning = "${StringUtil.wrapString(uiLabelMap.CreateEmplPayrollParametersWarning)}";
uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
uiLabelMap.PayrollParamPositionHighest = "${StringUtil.wrapString(uiLabelMap.PayrollParamPositionHighest)}";
uiLabelMap.PayrollParamPositionLowest = "${StringUtil.wrapString(uiLabelMap.PayrollParamPositionLowest)}";
uiLabelMap.ValueMustBeGreateThanZero = "${StringUtil.wrapString(uiLabelMap.ValueMustBeGreateThanZero)}";
uiLabelMap.DateJoinCompany = "${StringUtil.wrapString(uiLabelMap.DateJoinCompany)}";

</script>

<script type="text/javascript" src="/hrresources/js/payroll/CreateEmplPayrollParameters.js"></script>
<div class="row-fluid">
	<div id="popupWindowPayrollEmplParams" class='hide'>
		<div id="PayrollEmplParamsWindowHeader">
			${StringUtil.wrapString(uiLabelMap.SetEmplAllowancesAndBonus)}
		</div>
		<div class="form-window-container">
			<div class="form-window-content">
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.CommonEmployee}</label>
					</div>
					<div class="span7">
						<input type="text" id="partyIdPayrollParam">
						<img alt="search" id="searchEmpl" width="16" height="16" src="/aceadmin/assets/images/search_lg.png" 
						style="
						   border: #d5d5d5 1px solid;
						   padding: 5.5px;
						   border-bottom-right-radius: 3px;
						   border-top-right-radius: 3px;
						   margin-left: -4px;
						   background-color: #f0f0f0;
						   border-left: 0px;
						   cursor: pointer;
						"/>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label asterisk">${uiLabelMap.AllowancesType}</label>
					</div>
					<div class="span7">
						<div id="parameterCodeNew"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
						<div class="span5 text-algin-right">
							<label class="control-label">${uiLabelMap.CommonPeriodType}</label>
						</div>
						<div class="span7">
							<div id="periodTypeParamNew">${uiLabelMap.HRCommonNotSetting}</div>
						</div>
					</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.parameterValue}</label>
					</div>
					<div class="span7">
						<div id="parameterValueNew"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.AvailableFromDate}</label>
					</div>
					<div class="span7">
						<div id="fromDateParamNew"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class="span5 text-algin-right">
						<label class="control-label">${uiLabelMap.CommonThruDate}</label>
					</div>
					<div class="span7">
						<div id="thruDateParamNew"></div>
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
<div class="row-fluid">
	<div id="popupWindowEmplList" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div class="">
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
<div class="row-fluid">
	<div id="windowConfigParamPosType" class='hide'>
		<div>
			${uiLabelMap.SettingParamByPosType}
		</div>
		<div class='form-window-container'>
			<div class='form-window-content'>
				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
						<label class="asterisk">${uiLabelMap.CommonFromDate}</label>     					
   					</div>
					<div class="span7">
						<div id="configPayrollParamFromDate"></div>						
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
   					<div class='span5 text-algin-right'>
						<label class="">${uiLabelMap.CommonThruDate}</label>     					
   					</div>
					<div class="span7">
						<div id="configPayrollParamThruDate"></div>						
					</div>
				</div>
				
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<label class="">${StringUtil.wrapString(uiLabelMap.PayrollParamForMorePositionFulfillment)}</label>
   					</div>
					<div class="span7">
						<div id="configPyrllParamSettingDropdown"></div>
					</div>
				</div>
			</div>
			<div class="form-action">
				<button id="btnCancelConfigParam" class='btn btn-danger form-action-button pull-right'>
					<i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button type="button" class='btn btn-primary form-action-button pull-right' id="btnSaveConfigParam">
					<i class='fa fa-check'></i>${uiLabelMap.CommonSave}</button>
			</div>
		</div>
	</div>
</div>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId=expandTreeId isDropDown="false" width="100%" height="100%" expandAll="false"/>
