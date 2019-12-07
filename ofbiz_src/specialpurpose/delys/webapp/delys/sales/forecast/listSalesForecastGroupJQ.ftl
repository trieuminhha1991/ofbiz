<#assign dataField = "[{name: 'customTimePeriodId', type: 'string'}, 
						{name: 'customTimePeriodId', type: 'string'},
						{name: 'periodName', type: 'string'},
						{name: 'fromDate', type: 'date', other: 'Timestamp'},
						{name: 'thruDate', type: 'date', other: 'Timestamp'},
					]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DACustomTimePeriod)}', dataField: 'customTimePeriodId', width: '16%',
							cellsrenderer: function(row, colum, value) {
	                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                        	if (data) {
	                        		return \"<span><a href='/delys/control/viewSalesForecastByYear?customTimePeriodId=\" + data.customTimePeriodId + \"'>\" + data.customTimePeriodId + ' - ${uiLabelMap.DAYear} ' + data.periodName + \"</a></span>\";
	                        	}
	                        	return '';
	                        }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.DAPeriodName)}', dataField: 'periodName'},
						{text: '${StringUtil.wrapString(uiLabelMap.DAFromDate)}', dataField: 'fromDate', width: '16%', cellsformat: 'dd/MM/yyyy'},
						{text: '${StringUtil.wrapString(uiLabelMap.DAThruDate)}', dataField: 'thruDate', width: '16%', cellsformat: 'dd/MM/yyyy'},
						"/>

<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" 
		url="jqxGeneralServicer?sname=JQGetListSalesForecastGroup" mouseRightMenu="true" contextMenuId="contextMenu"/>

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="icon-refresh open-sans"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetail)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}</li>
	</ul>
</div>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetail)}") {
        	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			/*if (data != undefined && data != null) {
				var salesId = data.salesId;
				var url = 'viewSalesStatement?salesId=' + salesId;
				var win = window.open(url, '_self');
				win.focus();
			}*/
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}") {
        	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			/*if (data != undefined && data != null) {
				var salesId = data.salesId;
				var url = 'viewSalesStatement?salesId=' + salesId;
				var win = window.open(url, '_blank');
				win.focus();
			}*/
        }
	});
</script>
