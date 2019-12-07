<#include "script/listCustomerTimePaymentScript.ftl"/>
<#assign datafield = "[{name: 'customerTimePaymentId', type: 'string'},
					   {name: 'partyId', type: 'string'},
					   {name: 'partyName', type: 'string'},
					   {name: 'dueDay', type: 'string'},
					   {name: 'fromDate', type: 'date', other: 'timestamp'},
					   {name: 'thruDate', type: 'date', other: 'timestamp'}
					   ]"/>
<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BACCSeqId)}', sortable: false, filterable: false, editable: false,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: '5%',
						    cellsrenderer: function (row, column, value) {
						        return \"<div style='margin:4px;'>\" + (value + 1) + \"</div>\";
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCCustomerTimePaymentId)}', datafield: 'customerTimePaymentId', width: '18%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCPartyId)}', datafield: 'partyId', width: '20%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCGroupName)}', datafield: 'partyName', width: '20%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCDueDay)}' + ' (${StringUtil.wrapString(uiLabelMap.BSDayLowercase)})', datafield: 'dueDay', width: '20%'},
                        { text: '${StringUtil.wrapString(uiLabelMap.DmsFromDate)}', datafield: 'fromDate', width: '15%', filtertype: 'range', cellsformat: 'dd/MM/yyyy' },
					    { text: '${StringUtil.wrapString(uiLabelMap.DmsThruDate)}', datafield: 'thruDate', width: '15%', filtertype: 'range', cellsformat: 'dd/MM/yyyy' }
						"/>
</script>

<div id="contentNotificationAddSuccess">
</div>

<@jqGrid dataField=datafield columnlist=columnlist id="jqxgridListCustomer"
clearfilteringbutton="true" filterable="true"
showtoolbar="true" jqGridMinimumLibEnable="false"
alternativeAddPopup="addNewCustomerTimePaymentWindow" addrow="true" addType="popup"
url="jqxGeneralServicer?sname=jqGetListCustomerTimePayment"
mouseRightMenu="true" contextMenuId="contextMenu"
/>

<div id='contextMenu' class="hide">
    <ul>
        <li action="edit" id="editCustomerTimePaymentMenu">
            <i class="icon-edit"></i>${uiLabelMap.CommonEdit}
        </li>
        <li action="expire" id="expireCustomerTimePaymentMenu">
            <i class="fa fa-ban"></i>${uiLabelMap.ExpireRelationship}
        </li>
    </ul>
</div>

<div id="addNewCustomerTimePaymentWindow" class="hide">
    <div id="newCustomerLabel">${uiLabelMap.BACCCreateNew}</div>
    <div class='form-window-container' style="position: relative;">
        <div class='form-window-content'>
            <div class="row-fluid">
                <div class='row-fluid margin-bottom10'>
                    <div class='span4 text-algin-right'>
                        <label class='required'>${uiLabelMap.BACCOrganization}</label>
                    </div>
                    <div class="span8">
                        <div id="enumPartyTypeId"></div>
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class='span4'>
                        <label class=''></label>
                    </div>
                    <div class="span8">
                        <div id="partyId" style="display: inline-block; float: left;">
                            <div id="partyGrid"></div>
                        </div>
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class="span4 text-algin-right">
                        <label class="">${StringUtil.wrapString(uiLabelMap.BACCDueDay)} (${uiLabelMap.BSDate})</label>
                    </div>
                    <div class="span8">
                        <div id="dueDay">
                        </div>
                    </div>
                </div>
                <div class='row-fluid margin-bottom10'>
                    <div class="span4 text-algin-right">
                        <label class="asterisk">${StringUtil.wrapString(uiLabelMap.BACCFromDate)}</label>
                    </div>
                    <div class="span8">
                        <div id="fromDate"></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="form-action">
            <button type="button" class='btn btn-danger form-action-button pull-right'
                    id="cancelAddCustomerTimePayment">
                <i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>
            <button type="button" class='btn btn-primary form-action-button pull-right'
                    id="saveAddCustomerTimePayment">
                <i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
        </div>
    </div>
</div>

<div id="jqxNotificationAddSuccess" >
    <div id="notificationAddSuccess">
    </div>
</div>
<script type="text/javascript"
        src="/accresources/js/setting/customertimepayment/listCustomerTimePayment.js"></script>
<script type="text/javascript"
        src="/accresources/js/setting/customertimepayment/editCustomerTimePayment.js"></script>
						   