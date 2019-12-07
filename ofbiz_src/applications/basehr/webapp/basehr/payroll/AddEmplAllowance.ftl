<div id="AddEmplAllowancesWindow" class="hide">
	<div>${uiLabelMap.AddNewAllowance}</div>
	<div class='form-window-container' style="position: relative;">
		<div class='form-window-content'>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.CommonEmployee)}</label>
				</div>
				<div class="span8">
					<div class="span11">
						<div id="dropDownButtonGroupEmpl">
							<div id="jqxGridGroupEmpl"></div>
						</div>
					</div>
					<div class="span1" style="margin: 0;">
						<button class="grid-action-button" style="margin: 0; padding: 2px" id="chooseEmplBtn" 
							title="${StringUtil.wrapString(uiLabelMap.ClickToChooseEmpl)}">
							<i class="icon-plus" style="font-size: 16.5px; position: relative; top: 2px; margin-left: 0; margin-bottom: 3px"></i></button>
					</div>
				</div>	
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HREmplAllowances)}</label>
				</div>
				<div class="span8">
					<div id="allowanceAdd"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.CommonPeriodType)}</label>
				</div>
				<div class="span8">
					<div id="periodTypeAllowanceAdd"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.HRCommonAmount)}</label>
				</div>
				<div class="span8">
					<div id="amountAllowanceAdd"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.EffectiveFromDate)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid hide periodTypeDaily">
						<div id="fromDateDaily"></div>
					</div>
					<div class="row-fluid hide periodTypeMonthly">
						<div style="display: inline-block; float: left; margin-right: 5px" id="monthFromMonthly"></div>
						<div style="display: inline-block; float: left;" id="yearFromMonthly"></div> 
					</div>
					<div class="row-fluid hide periodTypeYearly">
						<div id="yearFromYearly"></div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${StringUtil.wrapString(uiLabelMap.PayrollThruDate)}</label>
				</div>
				<div class="span8">
					<div class="row-fluid hide periodTypeDaily">
						<div id="thruDateDaily"></div>
					</div>
					<div class="row-fluid hide periodTypeMonthly">
						<div style="display: inline-block; float: left; margin-right: 5px" id="monthThruMonthly"></div>
						<div style="display: inline-block; float: left;" id="yearThruMonthly"></div> 
					</div>
					<div class="row-fluid hide periodTypeYearly">
						<div id="yearThruYearly"></div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingAdd" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAdd"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAdd">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinueAdd">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAdd">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div class="row-fluid">
	<div id="popupWindowEmplList" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div class='form-window-container'>
			<div id="splitterEmplList" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplList"></div>
				</div>
				<div style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	               		<div class='form-window-content'>
		                   <div id="EmplListInOrg">
		                   </div>
	               		</div>
	               </div>
	        	</div>
			</div>
			<div class="form-action">
	    		<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelChooseEmpl">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
	    		<button type="button" class='btn btn-primary form-action-button pull-right' id="saveChooseEmpl">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
	    	</div>
		</div>
	</div>
</div>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId="" isDropDown="false" width="100%" height="100%" expandAll="false"/>
<script type="text/javascript">
<#if expandedList?has_content>
<#assign expandTreeId=expandedList[0]>
if(typeof(globalVar.expandTreeId) == 'undefined'){
	globalVar.expandTreeId = "${expandTreeId}";		
}
</#if>
function jqxTreeEmplListSelect(event){
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	refreshBeforeReloadGrid($("#EmplListInOrg"));
	tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}
</script>
<script type="text/javascript" src="/hrresources/js/payroll/AddEmplAllowance.js"></script>