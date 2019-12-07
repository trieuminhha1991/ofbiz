<#if !addWindowId?has_content>
	<#assign addWindowId = "AddNewPartyAttWindow"/>
</#if>
<script type="text/javascript">
 globalVar.addWindowId = "${addWindowId}";
</script>
<div id="${addWindowId}" class="hide">
	<div>${uiLabelMap.AddPartyToTrainingCourse}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="asterisk">${uiLabelMap.CommonEmployee}</label>
				</div>
				<div class="span8">
					<div class="span12">
						<div id="dropDownButtonGroupEmpl" style="display: inline-block; ">
							<div id="jqxGridGroupEmpl"></div>
						</div>
						<div class="btn-group" style="display: inline-block; float: right;">
							<button class="btn btn-primary btn-mini dropdown-toggle" data-toggle="dropdown" style="height: 27px;" id="chooseEmplBtn" 
								title="${StringUtil.wrapString(uiLabelMap.ClickToChooseEmpl)}" >
								<i class="icon-plus icon-only" style="font-size: 14px"></i></button>
							<ul class="dropdown-menu pull-right">
								<li>
									<a href="javascript:addPartyToTrainingObj.selectTypeListEmpl('ALL')">${uiLabelMap.SelectFromEmplList}</a>
								</li>
	
								<li>
									<a href="javascript:addPartyToTrainingObj.selectTypeListEmpl('REGISTED')">${uiLabelMap.SelectFromEmplRegisted}</a>
								</li>
	
								<li>
									<a href="javascript:addPartyToTrainingObj.selectTypeListEmpl('EXPECTED')">${uiLabelMap.SelectFromEmplExpectedAttend}</a>
								</li>
							</ul>	
						</div>
					</div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.HRCommonResults}</label>
				</div>
				<div class="span8">
					<div id="partyTrainingResult"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.TrainingAmountEmployeeMustPaid}</label>
				</div>
				<div class="span8">
					<div id="amountMustPaid"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.AmountCompanySupport}</label>
				</div>
				<div class="span8">
					<div id="companySupport"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.TrainingAmountEmployeePaid}</label>
				</div>
				<div class="span8">
					<div id="amountEmplPaid"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class="span4 text-algin-right">
					<label class="">${uiLabelMap.HRCommonComment}</label>
				</div>
				<div class="span8">
					<textarea id="commentTraining"></textarea>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="loadingAddEmplTraining" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAddEmplTraining"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelAddPartyToTraining">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveAddPartyToTraining">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="ListRegistedWindow" class="hide">
	<div>${uiLabelMap.ListEmplRegistedTraining}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div id="listRegistedGrid"></div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right' onclick="addPartyToTrainingObj.closeWindow('ListRegistedWindow')">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' onclick="addPartyToTrainingObj.saveChoosEmpl('ListRegistedWindow')">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="ListExpectedWindow" class="hide">
	<div>${uiLabelMap.EmplExpectedAttendance}</div>
	<div class='form-window-container' style="position: relative;">
		<div class="form-window-content">
			<div id="listExpectedGrid"></div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right' onclick="addPartyToTrainingObj.closeWindow('ListExpectedWindow')">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' onclick="addPartyToTrainingObj.saveChoosEmpl('ListExpectedWindow')">
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
	    		<button type="button" class='btn btn-danger form-action-button pull-right' onclick="addPartyToTrainingObj.closeWindow('popupWindowEmplList')">
					<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonClose}</button>
	    		<button type="button" class='btn btn-primary form-action-button pull-right' onclick="addPartyToTrainingObj.saveChoosEmpl('popupWindowEmplList')">
					<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
	    	</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/hrresources/js/training/AddPartyToTrainingCourse.js"></script>
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