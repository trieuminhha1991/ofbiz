<#assign datafield = "[{name: 'invoiceItemTypeId', type: 'string'},
					   {name: 'invoiceItemSeqId', type: 'string'},
					   {name: 'invoiceItemTypeDesc', type: 'string'},
		               {name: 'productId', type: 'string'},
		               {name: 'productCode', type: 'string'},
		               {name: 'productName', type: 'string'},
		               {name: 'uomId', type: 'string'},
		               {name: 'quantity', type: 'number'},
     	 		 	   {name: 'amount', type: 'number'},
     	 		 	   {name: 'description', type: 'string'},
     	 		 	   {name: 'currencyUomId', type: 'string'},
     	 		 	   {name: 'total', type: 'number'},
     	 		 	   {name: 'taxAuthPartyId', type: 'string'},
     	 		 	   {name: 'taxAuthGeoId', type: 'string'},
     	 		 	   {name: 'taxAuthorityRateSeqId', type: 'string'},
					   ]"/>

<script type="text/javascript">
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BACCSeqId)}', sortable: false, filterable: false, editable: false, groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return '<div style=\"margin:4px;\">' + (value + 1) + '</div>';
						    }
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCInvoiceItemType)}', datafield: 'invoiceItemTypeDesc', width: '25%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCProductId)}', datafield: 'productCode', width: '13%'},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCProductName)}', datafield: 'productName', width: '22%',},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCQuantity)}', datafield: 'quantity', width: '9%', columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function (row, column, value) {
								if(typeof(value)== 'number'){
									return '<span class=\"align-right\">' + value + '</span>';
								}
								return '<span>' + value + '</span>';
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCUnitPrice)}', datafield: 'amount', width: '14%', columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxgridInvItem').jqxGrid('getrowdata', row);
								if(data){
									return '<span class=\"align-right\">' + formatcurrency(value, data.currencyUomId) + '</span>';	
								}
							}
						},
						{text: '${StringUtil.wrapString(uiLabelMap.BACCTotal)}', datafield: 'total', width: '14%', columntype: 'numberinput', filtertype: 'number',
							cellsrenderer: function (row, column, value) {
								var data = $('#jqxgridInvItem').jqxGrid('getrowdata', row);
								if(data){
									return '<span class=\"align-right\">' + formatcurrency(value, data.currencyUomId) + '</span>';	
								}
							}
						},
						"/>
</script>
<#assign customTitleProperties = StringUtil.wrapString(uiLabelMap.BACCInvoiceItemList)/>
<#if invoice.statusId == "INVOICE_IN_PROCESS">
	<#assign mouseRightMenu = "true">
	<#assign contextMenuId = "contextMenu">
	<#assign addrow = "true"/>
<#else>
	<#assign mouseRightMenu = "false">
	<#assign contextMenuId = "">
	<#assign addrow = "false"/>	
</#if>
<@jqGrid id="jqxgridInvItem" filtersimplemode="true" editable="false" showtoolbar="true" clearfilteringbutton="true"
		 url="jqxGeneralServicer?sname=JqxGetListInvoiceItems&invoiceId=${parameters.invoiceId}" dataField=datafield columnlist=columnlist
		 jqGridMinimumLibEnable="false" addrow=addrow alternativeAddPopup="invoiceItemNewWindow" addType="popup"
		 customTitleProperties=customTitleProperties
		 mouseRightMenu=mouseRightMenu contextMenuId=contextMenuId/>

<#if invoice.statusId == "INVOICE_IN_PROCESS">
	<div id="contextMenu" class="hide">
		<ul>
			<li action="edit" id="editInvoiceItem">
				<i class="icon-edit"></i>${uiLabelMap.CommonEdit}
	        </li>
		</ul>
	</div>
	<#include "invoiceItemEdit.ftl"/>
	<#include "invoiceItemNew.ftl"/>
</#if>		 
