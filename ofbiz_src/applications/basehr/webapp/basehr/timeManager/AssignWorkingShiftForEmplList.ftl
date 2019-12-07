<div id="assignWSEmplListWindow" class="hide">
	<div>${uiLabelMap.AssignWorkingShiftForGroupEmpl}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="groupEmplPanel">
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="asterisk">
							${uiLabelMap.CommonGroupEmployee}
						</label>
					</div>
					<div class="span8">
						<div class="row-fluid">
							<div class="span12">
								<div class="span11">
									<div id="dropDownButtonGroupEmpl">
										<div id="jqxGridGroupEmpl">
           							 	</div>
									</div>
								</div>
								<div class="span1" style="margin: 0;">
									<button class="grid-action-button" style="margin: 0; padding: 2px" id="chooseEmplBtn" 
										title="${StringUtil.wrapString(uiLabelMap.ClickToChooseEmpl)}">
										<i class="icon-plus icon-only" style="font-size: 16.5px; position: relative; top: 2px; margin-left: 0; margin-bottom: 3px">
									</i></button>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="asterisk">
							${uiLabelMap.CommonFromDate}
						</label>
					</div>
					<div class="span8">
						<div id="assignGroupEmplFromDate"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="asterisk">
							${uiLabelMap.CommonThruDate}
						</label>
					</div>
					<div class="span8">
						<div id="assignGroupEmplThruDate"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class="asterisk">
							${uiLabelMap.HrCommonWorkingShift}
						</label>
					</div>
					<div class="span8">
						<div id="workingShiftGroupEmpl"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span4 text-algin-right'>
						<label class=""></label>
					</div>
					<div class="span8">
						<div id="overrideData" style="margin-left: -5px !important; margin-top: 5px">${uiLabelMap.OverrideData}</div>
					</div>
				</div>
				<div class="row-fluid no-left-margin">
					<div id="loadingGroupEmpl" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
						<div class="loader-page-common-custom" id="spinnerGroupEmpl"></div>
					</div>
				</div>
			</div>
		</div>
		<div class="form-action">
    		<button type="button" class='btn btn-danger form-action-button pull-right' id="cancelGroupEmpl">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
			<button id="saveAndContinueGroupEmpl" type="button" class="btn btn-success form-action-button pull-right">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
    		<button type="button" class='btn btn-primary form-action-button pull-right' id="saveGroupEmpl">
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

<script type="text/javascript" src="/hrresources/js/timeManager/AssignWorkingShiftForEmplList.js"></script>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList" 
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId=expandTreeId isDropDown="false" width="100%" height="100%" expandAll="false"/>
<script type="text/javascript">
if(typeof(workingShiftArr) == 'undefined'){
	var workingShiftArr = [
     		<#if workingShiftList?has_content>
     			<#list workingShiftList as workingShift>
     			{
     				workingShiftId: "${workingShift.workingShiftId}",
     				workingShiftName: "${StringUtil.wrapString(workingShift.workingShiftName?if_exists)}"
     			},	
     			</#list>
     		</#if>
     ];
}

function jqxTreeEmplListSelect(event){
	refreshBeforeReloadGrid($('#EmplListInOrg'));
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}
<#if expandedList?has_content>
	<#assign expandTreeId=expandedList[0]>
	if(typeof(globalVar.expandTreeId) == 'undefined'){
		globalVar.expandTreeId = "${expandTreeId}";		
	}
<#else>
	<#assign expandTreeId="">
</#if>
</script>
