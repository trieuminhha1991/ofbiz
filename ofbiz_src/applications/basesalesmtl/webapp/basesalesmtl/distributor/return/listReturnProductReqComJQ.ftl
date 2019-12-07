<script type="text/javascript">
	<#assign statusList = delegator.findByAnd("StatusItem", {"statusTypeId" : "ORDER_RETURN_STTS"}, null, false)!/>
	<#assign reasonList = delegator.findByAnd("ReturnReason", null, null, false)!/>
	<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)!/>
	<#assign returnHeaderTypeList = delegator.findByAnd("ReturnHeaderType", null, null, false)!/>
	var statusData = [<#if statusList?exists><#list statusList as statusItem>{
		statusId: '${statusItem.statusId}',
		description: '${StringUtil.wrapString(statusItem.get("description", locale))}'
		},</#list></#if>];
	
	var reasonData = [<#if reasonList?exists><#list reasonList as reasonItem>{
		reasonId: '${reasonItem.returnReasonId}',
		description: '${StringUtil.wrapString(reasonItem.get("description", locale))}'
		},</#list></#if>];
	
	var uomData = [<#if uomList?exists><#list uomList as uomItem>{
		uomId: '${uomItem.uomId}',
		description: '${StringUtil.wrapString(uomItem.get("description", locale))}'
		},</#list></#if>];
	
	var returnHeaderTypeData = [<#if returnHeaderTypeList?exists><#list returnHeaderTypeList as item>{
		typeId: '${item.returnHeaderTypeId}',
		description: '${StringUtil.wrapString(item.get("description", locale))}'
		},</#list></#if>];
</script>

<#assign dataField="[{ name: 'returnId', type: 'string'},
					{name: 'returnHeaderTypeId', type: 'string'},
	             	{name: 'statusId', type: 'string'},
	             	{name: 'createdBy', type: 'string'},
	             	{name: 'fromPartyId', type: 'string'},
					{name: 'toPartyId', type: 'string'},
					{name: 'fromPartyName', type: 'string'},
					{name: 'toPartyName', type: 'string'},
	             	{name: 'paymentMethodId', type: 'string'},
	             	{name: 'finAccountId', type: 'string'},
	             	{name: 'billingAccountId', type: 'string'},
	             	{name: 'entryDate', type: 'date', other: 'Timestamp'},
					{name: 'originContactMechId', type: 'string'},
					{name: 'destinationFacilityId', type: 'string'},
					{name: 'needsInventoryReceive', type: 'string'},
					{name: 'currencyUomId', type: 'string'},
					{name: 'supplierRmaId', type: 'string'}]"/>
<#if security.hasPermission("LOGISTICS_VIEW", session)>
	<#assign columnlist="
			{text: '${uiLabelMap.DAReturnOrderId}', dataField: 'returnId', width: '8%', align: 'center', 
				cellsrenderer: function(row, colum, value) {
	            	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	            	return \"<span><a href='viewDetailReturnOrder?returnId=\" + data.returnId + \"'>\" + data.returnId + \"</a></span>\";
	            }
			},">
<#else>
	<#assign columnlist="
			{text: '${uiLabelMap.DAReturnOrderId}', dataField: 'returnId', width: '8%', align: 'center',
				cellsrenderer: function(row, colum, value) {
	            	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
	            	return \"<span><a href='viewReturnOrderGeneral?returnId=\" + data.returnId + \"'>\" + data.returnId + \"</a></span>\";
	            }
			},
			{text: '${uiLabelMap.DAReturnHeaderTypeId}', dataField: 'returnHeaderTypeId', width: '13%', filtertype: 'checkedlist', align: 'center', 
				cellsrenderer: function(row, column, value){
					for(var i = 0; i < returnHeaderTypeData.length; i++){
						if(returnHeaderTypeData[i].typeId == value){
							return '<span title=' + value + '>' + returnHeaderTypeData[i].description + '</span>'
						}
					}
				},
			 	createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(returnHeaderTypeData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'typeId', valueMember: 'typeId',
						renderer: function(index, label, value){
							for(var i = 0; i < returnHeaderTypeData.length; i++){
								if(returnHeaderTypeData[i].typeId == value){
									return '<span>' + returnHeaderTypeData[i].description + '</span>';
								}
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			}
			},
			{text: '${uiLabelMap.DAStatus}', dataField: 'statusId', width: '13%', filtertype: 'checkedlist', align: 'center', 
				cellsrenderer: function(row, column, value){
					for(var i = 0; i < statusData.length; i++){
						if(statusData[i].statusId == value){
							return '<span title=' + value + '>' + statusData[i].description + '</span>'
						}
					}
				},
			 	createfilterwidget: function (column, columnElement, widget) {
					var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
						autoBind: true
					});
					var records = filterDataAdapter.records;
					widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
						renderer: function(index, label, value){
							for(var i = 0; i < statusData.length; i++){
								if(statusData[i].statusId == value){
									return '<span>' + statusData[i].description + '</span>';
								}
							}
							return value;
						}
					});
					widget.jqxDropDownList('checkAll');
	   			}
		 	},">
</#if>
<#assign columnlist= columnlist + "
					{text: '${uiLabelMap.DAEntryDate}', dataField: 'entryDate', width: '12%', cellsformat: 'd', align: 'center', filtertype: 'range'},
					{text: '${uiLabelMap.FacilityToReceive}', dataField: 'destinationFacilityId', minwidth: '15%', align: 'center'},
					{text: '${uiLabelMap.ReturnFrom}', dataField: 'fromPartyId', minwidth: '20%', align: 'center', 
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							return '<span title='+data.fromPartyName+'>'+data.fromPartyName+'</span>';
						}	
				 	},
				 	{text: '${uiLabelMap.ReturnAddress}', dataField: 'originContactMechId', width: '20%', align: 'center'},
				 	{text: '${uiLabelMap.CurrencyUom}', dataField: 'currencyUomId', width: '8%', align: 'center'},
				 	{text: '${uiLabelMap.DACreatedBy}', dataField: 'createdBy', width: '10%', align: 'center'},
				 "/>
<@jqGrid url="jqxGeneralServicer?sname=JQGetListReturnOrderToCompany" dataField=dataField columnlist=columnlist filterable="true" clearfilteringbutton="true"
		 showtoolbar="true" alternativeAddPopup="alterpopupWindow" filtersimplemode="true" addType="popup" deleterow="false" defaultSortColumn="entryDate" sortdirection="desc" 
		 mouseRightMenu="true" contextMenuId="contextMenu" otherParams="fromPartyName:S-getPartyName(partyId{fromPartyId})<partyName>;toPartyName:S-getPartyName(partyId{toPartyId})<partyName>;"
		 />

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	    <li><i class="fa fa-search"></i>${StringUtil.wrapString(uiLabelMap.DAViewDetail)}</li>
	</ul>
</div>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
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
				var returnId = data.returnId;
				var url = 'viewReturnOrderGeneral?returnId=' + returnId;
				var win = window.open(url, '_blank');
				win.focus();
			}
        }
	});
</script>
