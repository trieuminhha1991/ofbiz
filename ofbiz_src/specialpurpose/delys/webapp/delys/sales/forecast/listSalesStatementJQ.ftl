<#assign statementTypeList = delegator.findByAnd("SalesStatementType", null, null, false) />
<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "SALES_SM_STATUS"}, null, false) />
<script type="text/javascript">
	<#if statusList?exists>
		var statusData = [
		<#list statusList as statusItem>
			<#assign description = StringUtil.wrapString(statusItem.get("description", locale)) />
			{
				statusId : '${statusItem.statusId}',
				description : "${description}"
			},
		</#list>
		];
	<#else>
		var statusData = [];
	</#if>
	<#if statementTypeList?exists>
		var statementTypeData = [
		<#list statementTypeList as item>
			<#assign description = StringUtil.wrapString(item.get("description", locale)) />
			{
				typeId : '${item.salesTypeId}',
				description : "${description}"
			},
		</#list>
		];
	<#else>
		var statementTypeData = [];
	</#if>
</script>

<#assign dataField = "[{name: 'salesId', type: 'string'}, 
						{name: 'salesTypeId', type: 'string'}, 
						{name: 'parentSalesId', type: 'string'}, 
						{name: 'salesName', type: 'string'},
						{name: 'statusId', type: 'string'},
						{name: 'currencyUomId', type: 'string'},
						{name: 'salesForecastId', type: 'string'},
						{name: 'organizationPartyId', type: 'string'},
						{name: 'internalPartyId', type: 'string'},
						{name: 'customTimePeriodId', type: 'string'},
						]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.DAStatementId)}', dataField: 'salesId', width: '12%',
							cellsrenderer: function(row, colum, value) {
	                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	                        	return \"<span><a href='/delys/control/viewSalesStatement?salesId=\" + data.salesId + \"'>\" + data.salesId + \"</a></span>\";
	                        }
						}, 
						{text: '${StringUtil.wrapString(uiLabelMap.DACustomTimePeriod)}', dataField: 'customTimePeriodId', width: '12%'},
						{text: '${StringUtil.wrapString(uiLabelMap.DAStatementName)}', dataField: 'salesName'},
						{text: '${StringUtil.wrapString(uiLabelMap.DAStatus)}', dataField: 'statusId', width: '10%', filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < statusData.length; i++){
	    							if (value == statusData[i].statusId){
	    								return '<span title = ' + statusData[i].description +'>' + statusData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
						 	}, 
						 	createfilterwidget: function (column, columnElement, widget) {
								var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'statusId'});
								widget.jqxDropDownList('checkAll');
				   			}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.DAFromParty)}', dataField: 'organizationPartyId', width: '10%'},
						{text: '${StringUtil.wrapString(uiLabelMap.DAToParty)}', dataField: 'internalPartyId', width: '10%'},
						{text: '${StringUtil.wrapString(uiLabelMap.DAStatementType)}', dataField: 'salesTypeId', width: '10%', filtertype: 'checkedlist', 
							cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	    						for(var i = 0 ; i < statementTypeData.length; i++){
	    							if (value == statementTypeData[i].typeId){
	    								return '<span title = ' + statementTypeData[i].description +'>' + statementTypeData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
						 	}, 
						 	createfilterwidget: function (column, columnElement, widget) {
								var filterDataAdapter = new $.jqx.dataAdapter(statementTypeData, {
									autoBind: true
								});
								var records = filterDataAdapter.records;
								records.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								widget.jqxDropDownList({source: records, displayMember: 'description', valueMember: 'typeId'});
								widget.jqxDropDownList('checkAll');
				   			}
				   		},
						{text: '${StringUtil.wrapString(uiLabelMap.DAParentStatement)}', dataField: 'parentSalesId', width: '10%'},
						{text: '${StringUtil.wrapString(uiLabelMap.DASalesForecast)}', dataField: 'salesForecastId', width: '10%'},
						"/>
<#assign tmpCreateUrl = ""/>
<#assign tmpCreateUrl2 = ""/>
<#if security.hasPermission("SALESMGR_INOUT_CREATE", session)>
	<#assign tmpCreateUrl = "icon-plus open-sans@${uiLabelMap.DACreateNew}@newSalesStatement"/>
	<#assign tmpCreateUrl2 = "icon-plus open-sans@${uiLabelMap.DACreateNew} 2@editSalesStatement"/>
</#if>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="25" showtoolbar="true" filtersimplemode="true" showstatusbar="false" customcontrol1=tmpCreateUrl customcontrol2=tmpCreateUrl2 
		url="jqxGeneralServicer?sname=JQGetListSalesStatement&salesTypeId=${salesTypeId?if_exists}" mouseRightMenu="true" contextMenuId="contextMenu"/>

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
			if (data != undefined && data != null) {
				var salesId = data.salesId;
				var url = 'viewSalesStatement?salesId=' + salesId;
				var win = window.open(url, '_self');
				win.focus();
			}
        } else if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DAViewDetailInNewTab)}") {
        	var data = $("#jqxgrid").jqxGrid("getrowdata", rowindex);
			if (data != undefined && data != null) {
				var salesId = data.salesId;
				var url = 'viewSalesStatement?salesId=' + salesId;
				var win = window.open(url, '_blank');
				win.focus();
			}
        }
	});
</script>
