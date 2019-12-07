 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
 
<#assign dataField="[
						{ name: 'orderId', type: 'string'},
						{ name: 'productId', type: 'string'},
						{ name: 'partyId', type: 'string'},
						{ name: 'fromDate', type: 'date'},
						{ name: 'productName', type: 'string'},
						{ name: 'createdBy', type: 'string'},
						{ name: 'qtyInInventory', type: 'number'},
						{ name: 'orderDate', type: 'date'}
					]"/>

<#assign columnlist= " {
						text : '${uiLabelMap.DAOrderId}',	dataField : 'orderId', width : '70px' 
						},
						{
						text : '${uiLabelMap.DAProductId}' , dataField : 'productId'
						},
						{ 
						text : '${uiLabelMap.DAPartyId}' ,dataField : 'partyId'
						},{
							text : '${uiLabelMap.DAFromDate}' , dataField : 'fromDate'
						},{
							text : '${uiLabelMap.DAProductName}', dataField : 'productName',cellsrenderer : 
							function(row,columnfield,value){
								var data = processData(value);
								return '<span id=\"' + row  + '\" onmouseenter = \"showDetail('+ \"'\"+ row + \"'\" +',' +\"'\" + data + \"'\" +')\">' + value +'</span>';
							}							
						},{
							text : '${uiLabelMap.DACreatedBy}' ,dataField : 'createdBy',width : '110px'
						},{
							text : '${uiLabelMap.DAQuantityInventory}',dataField : 'qtyInInventory', width : '110px'
						},{
							text : '${uiLabelMap.DAOrderDate}' , dataField : 'orderDate'
						}" />
<@jqGrid filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		 url="jqxGeneralServicer?sname=JQGetListInventoryHistory" />
<script type="text/javascript">
	var showDetail  = function(id,value){
			$('#' + id).jqxTooltip({content : value,position : 'right'});	
		}
	var processData = function(data){
		var newData = '';
		if(data.length > 40){
			newData = data.substr(0,40) + '...';
		}else {
			newData = data;
		}
		return newData;
	}
</script>					
					
					