<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxsplitter.js"></script>
<@jqOlbCoreLib hasDropDownList=true/>
<script type="text/javascript" src="/posresources/js/setting/configPrintOrderPOS.js"></script>
<script type="text/javascript" src="/posresources/assets/js/jquery.PrintArea.js"></script>
<style>
.margin-top-2px {
	margin-top: -2px;
}
.ace-file-input {
	width: 252px;
}
</style>
<script>
	var fontTypeData =
	[
		{ fontTypeId: "Arial", description: "Arial"},
		{ fontTypeId: "Times New Roman", description: "Times New Roman"},
		{ fontTypeId: "Open Sans", description: "Open Sans"}
	];
	<#assign productStoreList = delegator.findByAnd("ProductStore", {"salesMethodChannelEnumId":"SMCHANNEL_POS"}, null, false) />
	var productStoreListData = [<#list productStoreList as ps>{
		productStoreId: "${ps.productStoreId}",
		storeName: "${StringUtil.wrapString(ps.storeName)}"
	},</#list>];
	var selectPrintData = [ { value: "Y", text: "${uiLabelMap.BSYes}" }, { value: "N", text: "${uiLabelMap.BSNo}" } ];
	
	if (multiLang) {
		multiLang = _.extend(multiLang, {
			BPOSAreYouSureDeleteThisConfigPrintOrder: "${StringUtil.wrapString(uiLabelMap.BPOSAreYouSureDeleteThisConfigPrintOrder)}",
			BPOSNoFile: "${StringUtil.wrapString(uiLabelMap.BPOSNoFile)}",
			BPOSChoose: "${StringUtil.wrapString(uiLabelMap.BPOSChoose)}",
			BPOSChange: "${StringUtil.wrapString(uiLabelMap.BPOSChange)}",
			BPOSValidateGreaterThanZero: "${StringUtil.wrapString(uiLabelMap.BPOSValidateGreaterThanZero)}",
		});
	}
</script>

<#assign dataField="[{ name: 'productStoreId', type: 'string' },
					{ name: 'storeName', type: 'string'},
					{ name: 'fontFamily', type: 'string'},
					{ name: 'headerFontSize', type: 'number'},
					{ name: 'infoFontSize', type: 'number'},
					{ name: 'contentFontSize', type: 'number'},
					{ name: 'isPrintBeforePayment', type: 'string'},
					{ name: 'logo', type: 'string'}]"/>
					
<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.BPOSId)}', datafield: 'productStoreId', width: 100 }, 
					{ text: '${StringUtil.wrapString(uiLabelMap.BPOSProductStore)}', datafield: 'storeName', minwidth: 200 },
					{ text: '${StringUtil.wrapString(uiLabelMap.BPOSFontFamily)}', datafield: 'fontFamily', filtertype: 'checkedlist', width: 170,
						createfilterwidget: function (column, columnElement, widget) {
							widget.jqxDropDownList({ source: fontTypeData, displayMember: 'description', valueMember: 'fontTypeId', autoDropDownHeight:true });
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.BPOSHeaderFontSize)}', datafield: 'headerFontSize', cellsalign:'right', width: 150, filtertype: 'number' },
					{ text: '${StringUtil.wrapString(uiLabelMap.BPOSInfoFontSize)}', datafield: 'infoFontSize', cellsalign:'right', width: 150, filtertype: 'number' },
					{ text: '${StringUtil.wrapString(uiLabelMap.BPOSContentFontSize)}', datafield: 'contentFontSize', cellsalign:'right', width: 150, filtertype: 'number' },
					{ text: '${StringUtil.wrapString(uiLabelMap.BPOSSelectTypePrint)}', datafield: 'isPrintBeforePayment', filtertype: 'checkedlist', width: 170,
						cellsrenderer: function (row, columns, value, defaulthtml, columnproperties, rowdata) {
							for ( var x in selectPrintData) {
								if (value == selectPrintData[x].value) {
									value = selectPrintData[x].text;
									break;
								}
							}
							return '<div style=\"margin:4px;\">' + value + '</div>';
						}, createfilterwidget: function (column, columnElement, widget) {
							widget.jqxDropDownList({ source: selectPrintData, displayMember: 'text', valueMember: 'value', autoDropDownHeight:true });
						}
					}"/>

<#if hasOlbPermission("MODULE", "PRINT_ORDERPOS_NEW", "")>
	<#assign addrow = "true" />
<#else>
	<#assign addrow = "false" />
</#if>
<#if hasOlbPermission("MODULE", "TERMINALPOS_DELETE", "")>
	<#assign deleterow = "true" />
<#else>
	<#assign deleterow = "false" />
</#if>

<@jqGrid id="jqxgridConfigPrintOrder" addrow=addrow deleterow=deleterow filtersimplemode="true" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showlist="false" 
	showtoolbar="true" editable="false" addrefresh="true" editrefresh="true" alternativeAddPopup="alterpopupWindow"
	contextMenuId="contextMenu" mouseRightMenu="true" filterable="true" url="jqxGeneralServicer?sname=JQGetListConfigPrintOrder" addType="popup"
	createUrl="jqxGeneralServicer?sname=createConfigPrintOrderPOS&jqaction=C"
	addColumns="productStoreId;fontFamily;headerFontSize(java.math.BigDecimal);infoFontSize(java.math.BigDecimal);contentFontSize(java.math.BigDecimal);isPrintBeforePayment;logo"
	updateUrl="jqxGeneralServicer?jqaction=U&sname=updateConfigPrintOrderPOS"
	editColumns="productStoreId;fontFamily;headerFontSize(java.math.BigDecimal);infoFontSize(java.math.BigDecimal);contentFontSize(java.math.BigDecimal);isPrintBeforePayment;logo"
	removeUrl="jqxGeneralServicer?jqaction=D&sname=deleteConfigPrintOrderPOS"
	deleteColumn="productStoreId"/>

<div id="contextMenu" style="display: none;">
	<ul>
		<li id="mnuRefresh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
		<li id="mnuPrint"><i class="fa fa-print"></i>${StringUtil.wrapString(uiLabelMap.POSPrint)}</li>
		<#if hasOlbPermission("MODULE", "TERMINALPOS_EDIT", "")>
			<li id="mnuEdit"><i class="fa-pencil open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSEdit)}</li>
		</#if>
		<#if hasOlbPermission("MODULE", "TERMINALPOS_DELETE", "")>
			<li id="mnuDelete"><i class="fa-trash open-sans"></i>${StringUtil.wrapString(uiLabelMap.BSDelete)}</li>
		</#if>
	</ul>
</div>

<#include "popup/addConfigPrintOrder.ftl">