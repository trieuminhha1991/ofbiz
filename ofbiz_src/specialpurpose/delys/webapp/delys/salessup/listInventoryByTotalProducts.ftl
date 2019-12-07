 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
 
<#assign dataField="[
						{ name: 'productId', type: 'string'},
						{ name: 'productName', type: 'string'},
						{ name: 'qtyInInventory', type: 'number'}
					]"/>

<#assign columnlist= " 
						{
						text : '${uiLabelMap.DAProductId}' , dataField : 'productId'
						},
						{
							text : '${uiLabelMap.DAProductName}', dataField : 'productName',cellsrenderer : 
							function(row,columnfield,value){
								var data = processData(value);
								return '<span id=\"' + row  + '\" onmouseenter = \"showDetail('+ \"'\"+ row + \"'\" +',' +\"'\" + data + \"'\" +')\">' + value +'</span>';
							}							
						},{
							text : '${uiLabelMap.salessupTotalInventoryByProducts}',dataField : 'qtyInInventory', width : '110px'
						}" />
<@jqGrid filtersimplemode="true" filterable="true" addType="popup" dataField=dataField columnlist=columnlist  clearfilteringbutton="true" 
		 url="jqxGeneralServicer?sname=JQGetListInventoryByProducts" />
<script type="text/javascript">
	var showDetail  = function(id,value){
			$('#' + id).jqxTooltip({content : value,position : 'right'});	
		}
	var processData = function(data){
		var newData = '';
		if(data.length > 35){
			newData = data.substr(0,35) + '...';
		}else {
			newData = data;
		}
		return data;
	}
</script>					
					
					