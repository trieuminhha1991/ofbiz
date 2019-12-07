<script type="text/javascript">
	function addZero(i) {
	    if (i < 10) {
	        i = "0" + i;
	    }
	    return i;
	}
</script>

<#assign columnlist="{ text: '${uiLabelMap.DACreateDate}', dataField: 'orderDate', width: '160px', cellsformat: 'dd/MM/yyyy', filtertype:'range', align: 'center',
					 	cellsrenderer: function(row, colum, value) {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	var newDate = new Date(value);
                        	return \"<span>\" + addZero(value.getDate()) + '/' + addZero(value.getMonth()+1) + '/' + addZero(value.getFullYear()) + ' ' + addZero(value.getHours())+':'+addZero(value.getMinutes())+':'+addZero(value.getSeconds()) + \"</span>\";
                        }
					 },
					 { text: '${uiLabelMap.DAOrderId}', dataField: 'orderId', align: 'center', 
					 	cellsrenderer: function(row, colum, value) {
                        	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
                        	return \"<span><a href='javascript:void(0);' onclick='javascript:set_value(&#39;\" + data.orderId + \"&#39;)'>\" + data.orderId + \"</a></span>\";
                        }
                     },
					 { text: '${uiLabelMap.DAOrderName}', dataField: 'orderName', align: 'center',
					 	cellsrenderer: function(row, column, value){
    						return '<span title=\"' + value +'\">' + value + '</span>';
					 	}
					 },
					 { text: '${uiLabelMap.DAOrderType}', dataField: 'orderTypeId', sortable: false, filterable: false, align: 'center'},
					 { text: '${uiLabelMap.DACustomer}', dataField: 'customer', align: 'center'},
					 { text: '${uiLabelMap.OrderProductStore}', dataField: 'productStoreId', width: '100px', align: 'center'},
					 { text: '${uiLabelMap.CommonAmount}', dataField: 'grandTotal', width: '200px', align: 'center', 
					 	cellsrenderer: function(row, column, value) {
					 		var data = $(\"#jqxgrid\").jqxGrid(\"getrowdata\", row);
					 		var str = \"<div style='text-align:right; width:95%; margin-left:0!important'>\";
							str += formatcurrency(value,data.currencyUom);
							str += \"</div>\";
							return str;
					 	}
					 },
					 { text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', width: '160px', align: 'center'}
					 "/>
<#assign dataField="[{ name: 'orderDate', type: 'date', other: 'Timestamp'},
					{ name: 'orderId', type: 'string'},
					{ name: 'orderName', type: 'string'},
					{ name: 'orderTypeId', type: 'string'},
					{ name: 'customer', type: 'string'},
					{ name: 'productStoreId', type: 'string'},
					{ name: 'grandTotal', type: 'string'},
					{ name: 'statusId', type: 'string'}, 
					{ name: 'currencyUom', type: 'string'}
					]"/>

<@jqGrid filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist usecurrencyfunction="true" clearfilteringbutton="true" 
		 url="jqxGeneralServicer?sname=JQListOrderListCompanyForProposal" />

