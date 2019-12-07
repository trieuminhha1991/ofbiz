<!-- maybe delete -->
<#if !popupPartyAttTraining?has_content>
	<#assign popupPartyAttTraining = "editPartyAttTrainingWindow"/>  
</#if>
<script type="text/javascript">
if(typeof(globalVar) == 'undefined'){
	globalVar = {};
}
globalVar.editPartyAttTraining = "${popupPartyAttTraining}";
if(typeof(uiLabelMap) == 'undefined'){
	uiLabelMap = {};
}
globalVar.statusRegisterArr = [
	<#if statusRegisterList?has_content>
		<#list statusRegisterList as status>
		{
			statusId: '${status.statusId}',
			description: '${StringUtil.wrapString(status.description?if_exists)}'
		},
		</#list>
	</#if>
];

if(typeof(globalVar.trainingResultTypeArr) == 'undefined'){
	globalVar.trainingResultTypeArr = [
		<#if trainingResultTypeList?has_content>
			<#list trainingResultTypeList as trainingResultType>
			{
				resultTypeId: '${trainingResultType.resultTypeId}',
				description: '${StringUtil.wrapString(trainingResultType.description?if_exists)}'
			},
			</#list>
		</#if>                             
	];
}

<#if expandedList?has_content>
	<#assign expandTreeId=expandedList[0]>
	if(typeof(globalVar.expandTreeId) == 'undefined'){
		globalVar.expandTreeId = "${expandTreeId}";		
	}
<#else>
	<#assign expandTreeId="">
</#if>
uiLabelMap.wgdelete = "${StringUtil.wrapString(uiLabelMap.wgdelete)}";
uiLabelMap.CannotDeleteRow = "${StringUtil.wrapString(uiLabelMap.CannotDeleteRow)}";
uiLabelMap.wgdeleteconfirm = "${StringUtil.wrapString(uiLabelMap.wgdeleteconfirm)}";
uiLabelMap.wgok = "${StringUtil.wrapString(uiLabelMap.wgok)}";
uiLabelMap.wgcancel = "${StringUtil.wrapString(uiLabelMap.wgcancel)}";
uiLabelMap.TrainingResult = "${StringUtil.wrapString(uiLabelMap.TrainingResult)}";
uiLabelMap.CommonStatus = "${StringUtil.wrapString(uiLabelMap.CommonStatus)}";
uiLabelMap.TrainingAmountEmployeeMustPaid = "${StringUtil.wrapString(uiLabelMap.TrainingAmountEmployeeMustPaid)}";
uiLabelMap.AmountCompanySupport = "${StringUtil.wrapString(uiLabelMap.AmountCompanySupport)}";
uiLabelMap.TrainingAmountEmployeePaid = "${StringUtil.wrapString(uiLabelMap.TrainingAmountEmployeePaid)}";
</script>
<div id="${popupPartyAttTraining}" class="hide">
	<div>${uiLabelMap.EmplAttandanceTraining}</div>
	<div class='form-window-container'>
		<div id="containerpartyAttendance${popupPartyAttTraining}" style="background-color: transparent; overflow: auto; width: 100%;">
    	</div>
    	<div id="jqxNotificationpartyAttendance${popupPartyAttTraining}">
	        <div id="notificationContentpartyAttendance${popupPartyAttTraining}">
	        </div>
	    </div>
		<div id="partyAttendance${popupPartyAttTraining}"></div>
	</div>
</div>
<div id="addPartyAttendanceWindow${popupPartyAttTraining}" class="hide">
	<div>${uiLabelMap.CommonAddNew}</div>
	<div class='form-window-container'>
		<div class="form-window-content">
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="asterisk">${uiLabelMap.EmployeeName}</label>
				</div>
				<div class="span8">
					<input type="text" id="party${popupPartyAttTraining}">
					<img alt="search" id="searchBtn${popupPartyAttTraining}" width="16" height="16" src="/aceadmin/assets/images/search_lg.png" 
						style="
						   border: #d5d5d5 1px solid;
						   padding: 4px;
						   border-bottom-right-radius: 3px;
						   border-top-right-radius: 3px;
						   margin-left: -3px;
						   background-color: #f0f0f0;
						   border-left: 0px;
						   cursor: pointer;
						"/>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.CommonStatus}</label>
				</div>
				<div class="span8">
					<div id="statusId${popupPartyAttTraining}"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.TrainingResult}</label>
				</div>
				<div class="span8">
					<div id="resultTypeId${popupPartyAttTraining}"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.TrainingAmountEmployeeMustPaid}</label>
				</div>
				<div class="span8">
					<div id="employeeAmount${popupPartyAttTraining}"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.AmountCompanySupport}</label>
				</div>
				<div class="span8">
					<div id="employerAmount${popupPartyAttTraining}"></div>
				</div>
			</div>
			<div class='row-fluid margin-bottom10'>
				<div class='span4 text-algin-right'>
					<label class="">${uiLabelMap.TrainingAmountEmployeePaid}</label>
				</div>
				<div class="span8">
					<div id="employeePaid${popupPartyAttTraining}"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="alterCancelPartyAtt">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="alterSavePartyAtt">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<div class="row-fluid">
	<div id="windowEmplList${popupPartyAttTraining}" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div>
			<div id="splitterEmplList${popupPartyAttTraining}" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplList${popupPartyAttTraining}"></div>
				</div>
				<div style="overflow: hidden !important;">
	               <div class="jqx-hideborder jqx-hidescrollbars" >
	                   <div id="EmplListInOrg${popupPartyAttTraining}">
	                   </div>
	               </div>
	        	</div>
			</div>
		</div>
	</div>
</div>

<script type="text/javascript" src="/hrresources/js/training/trainingPartyAttendanceTraining.js"></script>
<#assign jqxTreeEmplAtt = "jqxTreeEmplList" + popupPartyAttTraining/>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id=jqxTreeEmplAtt 
	jqxTreeSelectFunc="jqxTreeEmplAttandanceSelect" expandTreeId="" isDropDown="false" width="100%" height="100%" expandAll="false"/>

<script type="text/javascript">
function jqxTreeEmplAttandanceSelect(event){
	var item = $('#jqxTreeEmplList${popupPartyAttTraining}').jqxTree('getItem', event.args.element);
	$("#EmplListInOrg${popupPartyAttTraining}").jqxGrid('clearselection');
	var partyId = item.value;
	var tmpS = $("#EmplListInOrg${popupPartyAttTraining}").jqxGrid('source');
	tmpS.pagenum = 0;
	tmpS._source.url = '';
	$('#EmplListInOrg${popupPartyAttTraining}').jqxGrid('gotopage', 0);
	$("#EmplListInOrg${popupPartyAttTraining}").jqxGrid('source', tmpS);
	
	tmpS = $("#EmplListInOrg${popupPartyAttTraining}").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg${popupPartyAttTraining}").jqxGrid('source', tmpS);
}
</script>	
	