<#if !listEmplKPIWindow?has_content>
	<#assign listEmplKPIWindow = "listEmplKPIWindow"/>
</#if>
<#include "script/ViewListEmplKPIScript.ftl"/>
<div id="${listEmplKPIWindow}" class="hide">
	<div>${uiLabelMap.EmplKPIInformation}</div>
	<div class='form-window-container'>
		<div class="row-fluid">
			<div class="span12">
				<button id="" class="grid-action-button" style="margin-right: 0;"><i class="icon-only fa-user open-sans" style="font-size: 20px; margin-bottom: 3px"></i></button>
				<input type="text" id="searchPartyId">
				<button id="searchPartyIdBtn" title="${uiLabelMap.CommonSearch}" class="grid-action-button">
					<i class="icon-only icon-search open-sans" style="font-size: 18px; position: relative; top: -2px; margin-left: 0; margin-bottom: 3px"></i></button>
			</div>
		</div>
		<div class="row-fluid">
			<div id="containerjqxgrid${listEmplKPIWindow}" style="background-color: transparent; overflow: auto; width: 100%;">
	    	</div>
	    	<div id="jqxNotificationjqxgrid${listEmplKPIWindow}">
		        <div id="notificationContentjqxgrid${listEmplKPIWindow}">
		        </div>
		    </div>
			<div id="jqxgrid${listEmplKPIWindow}"></div>
		</div>
	</div>
</div>
<div class="row-fluid">
	<div id="popupWindowEmplListSearch" class='hide'>
		<div>
			${uiLabelMap.HREmplList}
		</div>
		<div class='form-window-container'>
			<div id="splitterEmplListSearch" style="border: none;">
				<div style="overflow: hidden !important" class="jqx-hideborder jqx-hidescrollbars">
					<div id="jqxTreeEmplListSearch"></div>
				</div>
				<div style="overflow: hidden !important;">
					<div class="jqx-hideborder jqx-hidescrollbars" >
						<div class='form-window-content'>
							<div id="EmplListInOrgSearch">
							</div>
						</div>
					</div>
				</div>
			</div>
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
<#if security.hasEntityPermission("HR_KPIPERF", "_UPDATE", session)>
<div id="addNewEmplKPIListWindow" class="hide">
	<div>${uiLabelMap.AssignKPIForEmpl}</div>
	<div class='form-window-container'>
		<div class="form-window-content" style="position: relative;">
			<div class="row-fluid">
				<div class="span12">
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
							<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HRCommonFields)}</label>
						</div>
						<div class="span8">
							<div id="perfCriteriaTypeNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HRCommonKPIName)}</label>
						</div>
						<div class="span8">
							<div id="criteriaIdNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class="span4 text-algin-right">
							<label class="asterisk">${StringUtil.wrapString(uiLabelMap.HRFrequency)}</label>
						</div>
						<div class="span8">
							<div id="periodTypeNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="">${uiLabelMap.HRTarget}</label>
						</div>
						<div class='span8'>
							<div id="targetNumberNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.HRCommonUnit}</label>
						</div>
						<div class='span8'>
							<div id="uomIdNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="">${uiLabelMap.KPIWeigth}</label>
						</div>
						<div class='span8'>
							<div id="weightNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.HREffectiveDate}</label>
						</div>
						<div class='span8'>
							<div id="fromDateNew"></div>
						</div>
					</div>
					<div class='row-fluid margin-bottom10'>
						<div class='span4 align-right'>
							<label class="asterisk">${uiLabelMap.HRExpireDate}</label>
						</div>
						<div class='span8'>
							<div id="thruDateNew"></div>
						</div>
					</div>
				</div>
			</div>
			<div class="row-fluid no-left-margin">
				<div id="ajaxLoading${listEmplKPIWindow}" class="hide" style="z-index: 9999999999; position: absolute; top: 50%; left: 50%">
					<div class="loader-page-common-custom" id="spinnerAjax${listEmplKPIWindow}"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
					<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
					<button type="button" class="btn btn-success form-action-button pull-right" id="saveAndContinue"><i class='fa-plus'></i>&nbsp;${uiLabelMap.SaveAndContinue}</button>
				</div>
			</div>
		</div>
	</div>
</div>
</#if>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewListEmplKPI.js?v=0.0.1"></script>
<#if security.hasEntityPermission("HR_KPIPERF", "_UPDATE", session)>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/CreateKPIForEmpl.js?v=0.0.2"></script>

</#if>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplList"
	jqxTreeSelectFunc="jqxTreeEmplListSelect" expandTreeId=expandTreeId isDropDown="false" width="100%" height="100%" expandAll="false"/>
<@htmlTemplate.renderJqxTreeDropDownBtn treeDropDownSource=treePartyGroup id="jqxTreeEmplListSearch"
jqxTreeSelectFunc="jqxTreeEmplListSelectSearch" expandTreeId=expandTreeId isDropDown="false" width="100%" height="100%" expandAll="false"/>
<script type="text/javascript">
function jqxTreeEmplListSelect(event){
	var item = $('#jqxTreeEmplList').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	refreshBeforeReloadGrid($("#EmplListInOrg"));
	tmpS = $("#EmplListInOrg").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrg").jqxGrid('source', tmpS);
}
function jqxTreeEmplListSelectSearch(event){
	var item = $('#jqxTreeEmplListSearch').jqxTree('getItem', event.args.element);
	var partyId = item.value;
	refreshBeforeReloadGrid($("#EmplListInOrgSearch"));
	tmpS = $("#EmplListInOrgSearch").jqxGrid('source');
	tmpS._source.url = 'jqxGeneralServicer?sname=JQGetEmplListInOrg&hasrequest=Y&partyGroupId=' + partyId;
	$("#EmplListInOrgSearch").jqxGrid('source', tmpS);
}
</script>	