<#include "script/ViewInvoiceItemTypeListScript.ftl"/>

<div id="containerjqxgrid" class="container-noti"><#-- style="background-color: transparent; overflow: auto;"-->
</div>
<div id="jqxNotificationjqxgrid">
    <div id="notificationContentjqxgrid">
    </div>
</div>

<div id="invoiceItemTypeTree" class="jqx-grid-context-menu"></div>

<div id="editInvoiceItemTypeWindow" class="hide">
	<div>${uiLabelMap.BACCCreateNew}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<form class="form-horizontal form-window-content-custom">
				<div class='row-fluid'>
					<div class="span4">
						<label class='asterisk'>${uiLabelMap.CommonId}</label>
					</div>
					<div class="span8">
						<input type="text" id="editTypeId"> 
					</div>
				</div>
				<div class='row-fluid'>
					<div class="span4">
						<label class='asterisk'>${uiLabelMap.CommonDescription}</label>
					</div>
					<div class="span8">
						<input type="text" id="editTypeDescription"> 
					</div>
				</div>
				<div class='row-fluid' style="position: relative;">
					<div class="span4">
						<label class=''>${uiLabelMap.BACCChildOfType}</label>
					</div>
					<div class="span8">
						<div id="itemTypeDropDown">
							<div id="itemTypeGrid"></div>
						</div>
						<a id="clearItemType" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" title="clear" style="right: 0px;">
							<i class="fa fa-eraser"></i>
						</a>
					</div>
				</div>
				<div class='row-fluid' style="position: relative;">
					<div class="span4">
						<label class=''>${uiLabelMap.BACCInvoiceTypeId}</label>
					</div>
					<div class="span8">
						<div id="invoiceTypeDropDown"></div>
						<a id="clearInvoiceType" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" title="clear" style="right: 0px;">
							<i class="fa fa-eraser"></i>
						</a>
					</div>
				</div>
				<div class='row-fluid' style="position: relative;">
					<div class="span4">
						<label class=''>${uiLabelMap.BACCAccountName}</label>
					</div>
					<div class="span8">
						<div id="glAccountDropDown">
							<div id="glAccountGrid"></div>
						</div>
						<a id="clearGlAccount" tabindex="-1" href="javascript:void(0);" class="clear-value-jqx" title="clear" style="right: 0px;">
							<i class="fa fa-eraser"></i>
						</a>
					</div>
				</div>
			</form>
		</div>
		<div class="form-action">
			<button type="button" class='btn btn-danger form-action-button pull-right'id="cancelEditInvoiceItemType">
				<i class='icon-remove'></i>&nbsp;${uiLabelMap.CommonCancel}</button>	
			<button type="button" class='btn btn-primary form-action-button pull-right' id="saveEditInvoiceItemType">
				<i class='fa fa-check'></i>&nbsp;${uiLabelMap.CommonSave}</button>
		</div>
	</div>		
</div>

<div id="contextMenu" class="hide">
	<ul>
		<li class="icon-edit" action="edit">${StringUtil.wrapString(uiLabelMap.CommonEdit)}</li>
	</ul>
</div>

<script type="text/javascript" src="/accresources/js/setting/invoiceItemType/viewInvoiceItemTypeList.js"></script>
<script type="text/javascript" src="/accresources/js/setting/invoiceItemType/editInvoiceItemType.js"></script>