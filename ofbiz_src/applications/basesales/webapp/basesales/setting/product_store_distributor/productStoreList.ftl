<script type="text/javascript">
	<#assign listProdStoreStatus = delegator.findByAnd("StatusItem", {"statusTypeId": "PRODSTORE_STATUS"}, null, false)!/>
	var productStoreStatusData = [
	<#if listProdStoreStatus?exists>
		<#list listProdStoreStatus as item>
		{statusId: "${item.statusId?if_exists}", description: "${item.get("description", locale)?if_exists}"},
		</#list>
	</#if>
	];
	
	var cellClassNameStore = function (row, columnfield, value, data) {
		var statusId = data.statusId;
		if (statusId == 'PRODSTORE_DISABLED') {
			return 'background-cancel';
		}
	};
</script>
<#assign dataField = "[
			{name: 'productStoreId', type: 'string'}, 
			{name: 'storeName', type: 'string'},
			{name: 'fullName', type: 'string'},
			{name: 'groupNameLocal', type: 'string'},
			{name: 'title', type: 'string'},
			{name: 'subtitle', type: 'string'},
			{name: 'inventoryFacilityId', type: 'string'},
			{name: 'payToPartyId', type: 'string'},
			{name: 'partyCode', type: 'string'},
			{name: 'defaultCurrencyUomId', type: 'string'},
			{name: 'salesMethodChannelEnumId', type: 'string'},
			{name: 'storeCreditAccountEnumId', type: 'string'},
			{name: 'statusId', type: 'string'},
		]"/>
<#assign columnlist = "
			{text: '${StringUtil.wrapString(uiLabelMap.BSSaleChanelId)}', dataField: 'productStoreId', width: 140, cellClassName: cellClassNameStore, 
				cellsrenderer: function(row, colum, value) {
			    	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			    	return \"<span><a href='showProductStore?productStoreId=\" + data.productStoreId + \"'>\" + data.productStoreId + \"</a></span>\";
			    }
			}, 
			{text: '${StringUtil.wrapString(uiLabelMap.BSSaleChanelName)}', dataField: 'storeName', minwidth: 140, cellClassName: cellClassNameStore},
			{text: '${StringUtil.wrapString(uiLabelMap.BSPayToParty)}', dataField: 'partyCode', width: 140, cellClassName: cellClassNameStore},
			{text: '${StringUtil.wrapString(uiLabelMap.BSFullName)}', dataField: 'fullName', width: 260, cellClassName: cellClassNameStore},
			{text: '${StringUtil.wrapString(uiLabelMap.BSInternalName)}', dataField: 'groupNameLocal', width: 160, cellClassName: cellClassNameStore},
			{text: '${StringUtil.wrapString(uiLabelMap.BSDefaultCurrencyUomId)}', dataField: 'defaultCurrencyUomId', width: 80, cellClassName: cellClassNameStore},
			{ text: '${StringUtil.wrapString(uiLabelMap.BSStatusId)}', dataField: 'statusId', width: 120, cellClassName: cellClassNameStore, filtertype: 'checkedlist',
				cellsrenderer: function(row, column, value){
					if (productStoreStatusData.length > 0) {
						for(var i = 0 ; i < productStoreStatusData.length; i++){
							if (value == productStoreStatusData[i].statusId){
								return '<span title =\"' + productStoreStatusData[i].description +'\">' + productStoreStatusData[i].description + '</span>';
							}
						}
					}
					return '<span title=' + value +'>' + value + '</span>';
			 	}, 
			 	createfilterwidget: function (column, columnElement, widget) {
			 		if (productStoreStatusData.length > 0) {
						var filterDataAdapter = new $.jqx.dataAdapter(productStoreStatusData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
							renderer: function(index, label, value){
								if (productStoreStatusData.length > 0) {
									for(var i = 0; i < productStoreStatusData.length; i++){
										if(productStoreStatusData[i].statusId == value){
											return '<span>' + productStoreStatusData[i].description + '</span>';
										}
									}
								}
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
					}
	   			}
			}
		"/>
<@jqGrid id="jqxgrid" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow1" columnlist=columnlist dataField=dataField
		viewSize="15" showtoolbar="true" filtersimplemode="true" showstatusbar="false" isShowTitleProperty="true" addType="popup"
		url="jqxGeneralServicer?sname=JQGetListProductStoreAndDetailDis" mouseRightMenu="true" contextMenuId="contextMenu" 
	/>
<style>
	.line-height{
		line-height: 25px;
	} 
</style>
<div id="contextMenu" style="display:none">
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>

<script type="text/javascript">
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.BSRefresh)}") {
        	$("#jqxgrid").jqxGrid('updatebounddata');
        }
	});
</script>

