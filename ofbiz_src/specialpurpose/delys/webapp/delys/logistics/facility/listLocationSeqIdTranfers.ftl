<script>
	var facilityId = '${parameters.facilityId}';
	var locationSeqId = '${parameters.locationSeqId}';
	//Style for jqxGrid1
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;

	$("#copyPopupWindow").jqxWindow({
    	width: 1000, height:800 ,resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterExit"), modalOpacity: 0.7          
    });
    
    
     
		
    
</script>

<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	if(datarecord.rowDetail == null || datarecord.rowDetail.length < 1){
		return 'Not Data';
	}
	var ordersDataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
    var orders = ordersDataAdapter.records;
    
		var nestedGrids = new Array();
        var id = datarecord.uid.toString();
        
         var grid = $($(parentElement).children()[0]);
         $(grid).attr(\"id\",\"jqxgridDetail\");
         nestedGrids[index] = grid;
       
         var ordersbyid = [];
        
         for (var ii = 0; ii < orders.length; ii++) {
                 ordersbyid.push(orders[ii]);
         }
         var orderssource = { datafields: [	
         	 { name: \'inventoryItemId\', type:\'string\' },
         	 { name: \'productId\', type:\'string\' },
         	 { name: \'quantity\', type:\'string\' },
         	 { name: \'uomId\', type:\'string\' }
         	],
           
             localdata: ordersbyid
         }
         var nestedGridAdapter = new $.jqx.dataAdapter(orderssource);
        
         if (grid != null) {
             grid.jqxGrid({
                 source: nestedGridAdapter, width: 700, height: 150,
                 showtoolbar:false,
		 		 editable:false,
		 		 editmode:\"click\",
		 		 showheader: true,
		 		 selectionmode:\"singlecell\",
		 		 theme: 'energyblue',
                 columns: [
                   { text: \'${uiLabelMap.InventoryItemId}\', datafield: \'inventoryItemId\',editable: false},
                   { text: \'${uiLabelMap.ProductId}\', datafield: \'productId\',editable: false},
                   { text: \'${uiLabelMap.Quantity}\', datafield: \'quantity\',editable: false},
                   { text: \'${uiLabelMap.QuantityUomId}\', datafield: \'uomId\',editable: false}
                 ]
             });
         }
 }"/>

<h4 class="row header smaller lighter blue">
	${uiLabelMap.TitleStockProductByLocation}
</h4>
<div class="row-fluid">
	<div class="span12">
		<#assign dataField1="[
					 	{ name: 'locationSeqId', type: 'string' },
					 	{ name: 'quantityTranfers', type: 'string' },
					 	{ name: 'rowDetail', type: 'string' }
		 		 	 ]"/>
		<#assign columnlist1="{ text: '${uiLabelMap.ToLocationSumLocation}', dataField: 'locationSeqId', width: 250, editable: false},
							  { text: '${uiLabelMap.QuantityTransferSum}', dataField: 'quantityTranfers', editable: true}
							 "/>
		<@jqGrid id="jqgrid2" filtersimplemode="true" width="750" usecurrencyfunction="true" addType="popup" dataField=dataField1 
				columnlist=columnlist1 clearfilteringbutton="true" showtoolbar="false" addrow="true" filterable="true" editmode="dblclick" editable="true" 
				url="jqxGeneralServicer?sname=JQXgetLocationAndQuantityByFacilityId&facilityId=${parameters.facilityId}&locationSeqId=${parameters.locationSeqId}" 
				bindresize="false" initrowdetailsDetail=initrowdetailsDetail initrowdetails="true"
		/>
	</div>
</div>