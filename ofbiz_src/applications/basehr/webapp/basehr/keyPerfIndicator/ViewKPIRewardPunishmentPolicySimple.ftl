<#include "script/ViewKPIRewardPunishmentPolicyScript.ftl"/>
<#assign datafield = "[	{name: 'perfCriteriaPolicyId', type: 'string'},
						{name: 'criteriaId', type: 'string'},
						{name: 'fromDate', type: 'date'},
						{name: 'thruDate', type: 'date'},
					  ]"/>
<script type="text/javascript">
	<#assign columnlist = "{text : '${StringUtil.wrapString(uiLabelMap.KpiPolicyId)}', width : '30%', datafield : 'perfCriteriaPolicyId', editable : false},
                        {text : '${StringUtil.wrapString(uiLabelMap.HRKeyPerfIndApplyId)}', datafield : 'criteriaId', editable: false},
                        {text: '${StringUtil.wrapString(uiLabelMap.HREffectiveDate)}', datafield: 'fromDate', cellsformat: 'dd/MM/yyyy',
                           editable: false, columntype: 'datetimeinput', width: '20%', filtertype: 'range'
                        },
                        {text: '${StringUtil.wrapString(uiLabelMap.CommonThruDate)}', datafield: 'thruDate', cellsformat: 'dd/MM/yyyy',
                           editable: false, columntype: 'datetimeinput', width: '20%', filtertype: 'range'
                        },
					"/>
</script>
<@jqGrid filtersimplemode="true" filterable="true" addrefresh="true" showtoolbar="true" dataField=datafield columnlist=columnlist
clearfilteringbutton="true" addType="popup" editable="true" deleterow="false"  showlist="false"
url="jqxGeneralServicer?sname=JQGetPerfCriteriaPolicySimple"  mouseRightMenu="true" contextMenuId="contextMenu"
createUrl="" jqGridMinimumLibEnable="false"
addColumns="" customControlAdvance="<div id='dateTimeInput'></div>"
removeUrl=""
/>
<div id="contextMenu" style="display:none;">
    <ul>
        <li action="viewPunishmentPolicyDetail">
            <i class="fa-search"></i>${uiLabelMap.ViewDetails}
        </li>
        <li action="editPunishmentPolicy">
            <i class="fa-pencil"></i>${uiLabelMap.CommonEdit}
        </li>
    </ul>
</div>
<div id="editPolicyWindowDetail" class="hide">
    <div>${uiLabelMap.SetupKPIPolicy}</div>
    <div class='form-window-container' >
        <div class="form-window-content">
            <div class="row-fluid">
                <div class="span12">
                    <div id="containersetupGrid_edit" style="background-color: transparent; overflow: auto; width: 100%;">
                        <div id="jqxNotificationsetupGrid_edit">
                            <div id="notificationContentsetupGrid_edit"></div>
                        </div>
                    </div>
                    <div id="setupGrid_edit"></div>
                </div>
            </div>
        </div>
        <div class="form-action">
            <div class='row-fluid'>
                <div class="span12 margin-top10">
                    <button id="alterCancel_edit" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
                    <button id="alterSave_edit" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="setupKpiPolicyWindow" class="hide">
    <div>${uiLabelMap.SetupKPIPolicy}</div>
    <div class='form-window-container' style="position: relative;">
        <div class='form-window-content'>
            <div class="row-fluid margin-bottom10">
                <div class="span4 align-right">
                    <label class="asterisk">${uiLabelMap.KPIFromRating}</label>
                </div>
                <div class="span8">
                    <div id="fromRating"></div>
                </div>
            </div>
            <div class="row-fluid margin-bottom10">
                <div class="span4 align-right">
                    <label>${uiLabelMap.KPIToRating}</label>
                </div>
                <div class="span8">
                    <div id="toRating"></div>
                </div>
            </div>
            <div class="row-fluid margin-bottom10">
                <div class="span4 align-right">
                    <label class="asterisk">${uiLabelMap.HRCommonAmount}</label>
                </div>
                <div class="span8">
                    <div id="amount"></div>
                </div>
            </div>
            <div class="row-fluid margin-bottom10">
                <div class="span4 align-right">
                    <label class="asterisk">${uiLabelMap.RewardPunishment}</label>
                </div>
                <div class="span8">
                    <div id="status"></div>
                </div>
            </div>
        </div>
        <div class="form-action">
            <div class='row-fluid'>
                <div class="span12 margin-top10">
                    <button id="cancelSetupKpi" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
                    <button id="saveAndContinueSetup" class="btn btn-success form-action-button pull-right">${uiLabelMap.SaveAndContinue}</button>
                    <button id="saveSetupKpi" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.CommonSave}</button>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="editKpiPolicyWindow" class="hide">
    <div>${uiLabelMap.EditPolicy}</div>
    <div class='form-window-container' >
        <div class="form-window-content">
            <div class="row-fluid margin-bottom10">
                <div class="span4 align-right">
                    <label>${uiLabelMap.KpiPolicyId}</label>
                </div>
                <div class="span8">
                    <input type="text" id="policyId"/>
                </div>
            </div>
            <div class="row-fluid margin-bottom10">
                <div class="span4 align-right">
                    <label>${uiLabelMap.HRKeyPerfIndApplyId}</label>
                </div>
                <div class="span8">
                    <input type="text" id="criteriaId"/>
                </div>
            </div>
            <div class="row-fluid margin-bottom10">
                <div class="span4 align-right">
                    <label class="asterisk">${uiLabelMap.CommonFromDate}</label>
                </div>
                <div class="span8">
                    <div id="fromDateNew"></div>
                </div>
            </div>
            <div class="row-fluid margin-bottom10">
                <div class="span4 align-right">
                    <label>${uiLabelMap.CommonThruDate}</label>
                </div>
                <div class="span8">
                    <div id="thruDateNew"></div>
                </div>
            </div>
        </div>
        <div class="form-action">
            <div class='row-fluid'>
                <div class="span12 margin-top10">
                    <button id="alterCancel_new" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.Cancel}</button>
                    <button id="alterSave_new" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i>${uiLabelMap.Save}</button>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" src="/hrresources/js/keyPerfIndicator/ViewKPIRewardPunishmentPolicySimple.js?v=0.0.2"></script>
