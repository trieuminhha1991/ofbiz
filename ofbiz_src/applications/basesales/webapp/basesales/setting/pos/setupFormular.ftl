<style>
.jqx-grid-pager.jqx-grid-pager-olbius {
	z-index: 0 !important;
}
div[role="rowgroup"] {
	overflow-y: auto !important;
}
</style>

<#include "script/setupFormularScript.ftl"/>
<#include "component://basepos/webapp/basepos/common/showNotification.ftl"/>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	setupFormularObject.initRowDetail(index, parentElement, gridElement, datarecord);
}"/>
<#assign dataField = "[{ name: 'productId', type: 'string' },
						{ name: 'productCode', type: 'string' },
						{ name: 'productName', type: 'string' },
						{ name: 'qoh', type: 'number' },
						{ name: 'qoo', type: 'number' },
						{ name: 'qtyL', type: 'number' },
						{ name: 'qtyS', type: 'number' },
						{ name: 'qpdL', type: 'number' },
						{ name: 'qpdS', type: 'number' },
						{ name: 'lidL', type: 'number' },
						{ name: 'lidS', type: 'number' },
						{ name: 'lastSold', type: 'date', other: 'Timestamp' },
						{ name: 'lastReceived',type: 'date', other: 'Timestamp' },
						{ name: 'status', type: 'string' }]"/>
<#assign columnlist = "
					{ text: '${uiLabelMap.SettingProductID}',  datafield: 'productCode', width: 150 },
					{ text: '${uiLabelMap.SettingProductName}', datafield: 'productName', width: 200 },
					{ text: '${uiLabelMap.SettingQOH_PO}', datafield: 'qoh', width: 100, columntype: 'numberinput', filtertype: 'number',
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${uiLabelMap.SettingQOO}', datafield: 'qoo', width: 100, columntype: 'numberinput', filtertype: 'number',
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${uiLabelMap.SettingQTYL}', datafield: 'qtyL', width: 100, columntype: 'numberinput', filtertype: 'number',
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${uiLabelMap.SettingQPDL}', datafield: 'qpdL', width: 100, columntype: 'numberinput', filtertype: 'number',
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${uiLabelMap.SettingLIDL}', datafield: 'lidL', width: 100, columntype: 'numberinput', filtertype: 'number',
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${uiLabelMap.SettingQTYS}', datafield: 'qtyS', width: 100, columntype: 'numberinput', filtertype: 'number',
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${uiLabelMap.SettingQPDS}', datafield: 'qpdS', width: 100, columntype: 'numberinput', filtertype: 'number',
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${uiLabelMap.SettingLIDS}', datafield: 'lidS', width: 100, columntype: 'numberinput', filtertype: 'number',
						cellsrenderer: function(row, column, value, a, b, data) {
							return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						}
					},
					{ text: '${uiLabelMap.SettingLastsold}', datafield: 'lastSold', width: 150, filtertype: 'range', cellsformat: 'dd/MM/yyyy' },
					{ text: '${uiLabelMap.SettingLastReceived}', datafield: 'lastReceived', width: 150,filtertype: 'range', cellsformat: 'dd/MM/yyyy' },
					{ text: '${uiLabelMap.SettingStatus}', datafield: 'status' }"/>	
<#assign customcontrol1="icon-plus-sign open-sans@${uiLabelMap.SettingPeriodSetup}@javascript:setupFormularObject.openTimePeriodWindow()" />						
<#assign customcontrol2="icon-plus-sign open-sans@${uiLabelMap.SettingSetupFormular}@javascript:setupFormularObject.openFormularWindow()" />						
<@jqGrid filtersimplemode="true" 
		 dataField=dataField
		 filterable="true"  
		 id="jqxGridFormular"
		 columnlist=columnlist
		 clearfilteringbutton="true"
		 showtoolbar="true"
		 editable="false" 
		 editmode="click"
		 viewSize="15"
		 initrowdetails = "true"
		 initrowdetailsDetail= initrowdetailsDetail
		 bindresize="true"
		 customcontrol1 = customcontrol1 
		 customcontrol2 = customcontrol2 
		 url="jqxGeneralServicer?sname=JQGetListProductSummary"/>

<div id="setupPeriodTimeWindow" style="display:none;">
	<div>${uiLabelMap.SettingPeriodSetup}</div>
	<div class="form-window-container ">
		<div class="form-window-content row-fluid">
			<div class="span12">
				<div class="span3"><label class="asterisk align-right margin-top5">${uiLabelMap.SettingLongPeriod}</label></div>
				<div class="span9">
					<div id="longPeriod"> </div>
				</div>
			</div>
			<div class="span12 margin-left0 margin-top10">
	 			<div class="span3"><label class="asterisk align-right margin-top5">${uiLabelMap.SettingShortPeriod}</label></div>
				<div class="span9">
					<div id="shortPeriod"></div>
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" id='cancelTimePeriodButton' class="btn btn-danger form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.SettingCancel}</button>
			<button type="button" id='createTimePeriodButton' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.SettingUpdate}</button>
		</div>
	</div>
</div>
<div id="setupFormularWindow" style="display:none;">
	<div>${uiLabelMap.SettingSetupFormular}</div>
	<div class="form-window-container ">
		<div class="form-window-content row-fluid">
			<div class="span12">
				<input id="jqxInputFormular"></input>
			</div>
			<div class="span12 margin-left0 margin-top10">
 				<div id="jqxGridOperand">
				</div>
			</div>
		</div>
		<div class="form-action">
			<button type="button" id='cancelFormularButton' class="btn btn-danger form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.SettingCancel}</button>
			<button type="button" id='createFormularButton' class="btn btn-primary form-action-button pull-right"><i class='icon-ok'></i>${uiLabelMap.SettingCreate}</button>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/posresources/js/common/Common.js"></script>
<script type="text/javascript" src="/posresources/js/common/jqx.notification.js"></script>
<script type="text/javascript" src="/posresources/js/common/jqx.window.js"></script>
<script type="text/javascript" src="/posresources/js/common/jqx.input.js"></script>
<script type="text/javascript" src="/posresources/js/common/jqx.numberinput.js"></script>
<script type="text/javascript" src="/posresources/js/common/jqx.splitter.js"></script>
<script type="text/javascript" src="/salesresources/js/pos/SetupFormular.js"></script>

