<div class="tab-pane<#if activeTab?exists && activeTab == "items-tab"> active</#if>" id="items-tab">
    <h4 class="smaller green">${uiLabelMap.ListProduct}</h4>
    <div class="row-fluid">
    	<div class="span12">

			<#assign dataField="[{ name: 'shippingTripId', type: 'string'},
            					 { name: 'packId', type: 'string'},
            					 { name: 'packItemSeqId', type: 'string'},
            					 { name: 'deliveryId', type: 'string'},
            					 { name: 'productCode', type: 'string'},
            					 { name: 'productName', type: 'string'},
            					 { name: 'quantityUomId', type: 'string'},
            					 { name: 'description', type: 'string'},
            					 { name: 'quantity', type: 'number'},
                                 { name: 'cancelQuantity', type: 'number'}
            					 ]"/>
            <#assign columnlist="
            				 	{ text: '${uiLabelMap.BSProductId}', datafield: 'productCode', width: '15%', editable: false, },
            				 	{ text: '${uiLabelMap.BSProductName}', datafield: 'productName', width: '40%', editable: false, },
            				 	{ text: '${uiLabelMap.BSUom}', datafield: 'description', width: '15%', editable: false, },
            				 	{ text: '${uiLabelMap.BSQuantity}', datafield: 'quantity', width: '15%', editable: false, },
                                { text: '${uiLabelMap.BSCancelQuantity}', datafield: 'cancelQuantity', width: '15%', editable: false, },
             				"/>
			<@jqGrid id="jqxGridPackOrderItems" dataField=dataField columnlist=columnlist
            	url="jqxGeneralServicer?sname=JQGetListOrderItemDetails&shippingTripId=${parameters.shippingTripId?if_exists}"
            	filterable="false" sortable="false" showtoolbar="false"
            	/>
	    </div>
    </div>
</div>