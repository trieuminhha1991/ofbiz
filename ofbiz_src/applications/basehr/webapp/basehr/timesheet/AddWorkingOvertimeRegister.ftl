<#if !addNewWindow?has_content>
	<#assign addNewWindow = "AddWorkOTRegisterWindow"/>
</#if>
<script type="text/javascript">
globalVar.addNewWindow = "${addNewWindow}";
</script>
<div id="${addNewWindow}" class="hide">
	<div>${uiLabelMap.AddWorkingOvertimeRegister}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="asterisk">${uiLabelMap.CommonEmployee}</label>
				</div>
				<div class="span9">
					<input type="text" id="partyId${addNewWindow}">
					<button id="searchPartyNewBtn" title="${uiLabelMap.CommonSearch}" class="btn btn-mini btn-primary">
						<i class="icon-only icon-search open-sans" style="font-size: 15px; position: relative; top: -2px;"></i></button>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.HrCommonPosition}</label>
				</div>
				<div class="span9">
					<input type="text" id="emplPosition${addNewWindow}">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.CommonDepartment}</label>
				</div>
				<div class="span9">
					<input type="text" id="groupName${addNewWindow}">
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.HREmplOvertimeDateRegis}</label>
				</div>
				<div class="span9">
					<div id="dateRegistered${addNewWindow}"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.CommonBeginingFromDate}</label>
				</div>
				<div class="span9">
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div id="fromDate${addNewWindow}"></div>
							</div>
							<div class="span8">
								<div class='row-fluid'>
									<div class='span5 text-algin-right'>
										<label class="">${uiLabelMap.CommonThruDate}</label>
									</div>
									<div class="span7">
										<div id="thruDate${addNewWindow}"></div>
									</div>
								</div>							
							</div>				
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.WorkingOvertimeStartTime}</label>
				</div>
				<div class="span9">
					<div class="row-fluid">
						<div class="span12">
							<div class="span4">
								<div id="startTime${addNewWindow}"></div>
							</div>
							<div class="span8">
								<div class='row-fluid'>
									<div class='span5 text-algin-right'>
										<label class="">${uiLabelMap.WorkingOvertimeEndTime}</label>
									</div>
									<div class="span7">
										<div id="endTime${addNewWindow}"></div>
									</div>
								</div>							
							</div>				
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.HRCommonApplyFor}</label>
				</div>
				<div class="span9">
					<div id="dayOfWeekAppl${addNewWindow}"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span3 text-algin-right'>
					<label class="">${uiLabelMap.WorkingOvertimeReason}</label>
				</div>
				<div class="span9">
					<textarea id="reasonRegister${addNewWindow}"></textarea>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loading${addNewWindow}" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinner${addNewWindow}"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancel${addNewWindow}">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-success form-action-button pull-right'id="saveAndContinue${addNewWindow}">
				<i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="save${addNewWindow}">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
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
				<div style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	                   <div id="EmplListInOrg">
	                   </div>
	               </div>
	        	</div>
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
<script type="text/javascript" src="/hrresources/js/timesheet/AddWorkingOvertimeRegister.js"></script>