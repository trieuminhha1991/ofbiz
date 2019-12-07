<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>
<script type="text/javascript" src="/delys/images/js/import/progressing.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/delys/images/js/import/Underscore1.8.3.js"></script>
 
 
<#assign dataField="[{ name: 'containerNumber', type: 'string'},
					{ name: 'billNumber', type: 'string'},
					{ name: 'orderId', type: 'string'},
					{ name: 'agreementName', type: 'string'}
					]"/>
<#assign columnlist="
					{ text: '${StringUtil.wrapString(uiLabelMap.No)}',sortable: false, filterable: false, editable: false,
        groupable: false, draggable: false, resizable: false,
        datafield: '', columntype: 'number', width: 50,
        cellsrenderer: function (row, column, value) {
            return \"<div style='margin-top: 3px; text-align: left;  '>\" + (value + 1)+  \"</div>\";  
        } },
					{ text: '${StringUtil.wrapString(uiLabelMap.containerNumber)}', datafield: 'containerNumber', align: 'center', width: 250, editable: false, filterable : true  },
					{ text: '${StringUtil.wrapString(uiLabelMap.billNumber)}', datafield: 'billNumber',  align: 'center' , width: 250, editable: false, filterable : true  },
					{ text: '${StringUtil.wrapString(uiLabelMap.OrderId)}', datafield: 'orderId', align: 'center',editable: false, filterable : true, width: 250 },
					{ text: '${StringUtil.wrapString(uiLabelMap.AgreementName)}', datafield: 'agreementName', align: 'center',editable: false, filterable : true },
					"/>
<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" viewSize="15"
		showtoolbar="true" editable="false" filterable="true" 
		url="jqxGeneralServicer?sname=JQgetListContainerLookup"
/>

