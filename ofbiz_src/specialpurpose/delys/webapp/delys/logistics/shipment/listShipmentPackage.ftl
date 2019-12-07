<script>
	<#assign boxTypes = delegator.findList("ShipmentBoxType", null, null, null, null, false) />
	var boxTypeData = new Array();
	<#list boxTypes as item>
		var row = {};
		row['boxTypeId'] = '${item.shipmentBoxTypeId?if_exists}';
		row['description'] = '${item.description?if_exists}';
		boxTypeData[${item_index}] = row;
	</#list>
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		row['weightUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${item.abbreviation?if_exists}';
		weightUomData[${item_index}] = row;
	</#list>
	
	<#assign packingUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
	var packingUomData = new Array();
	<#list packingUoms as item>
		var row = {};
		row['quantityUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${item.abbreviation?if_exists}';
		packingUomData[${item_index}] = row;
	</#list>
	
	$("#popupShipmentPackage").jqxWindow({
		maxWidth: 1500, minWidth: 800, minHeight: 500, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	$('#popupShipmentPackage').on('open', function (event) {
		initGridjqxgridShipmentPackage();
	});
</script>
<h4 class="row header smaller lighter blue">
	${uiLabelMap.ListShipmentPackage}
</h4>
<#assign initrowdetailsDetail = "function (index, parentElement, gridElement, datarecord) {
	 	var packageDataAdapter = new $.jqx.dataAdapter(datarecord.rowDetail, { autoBind: true });
        packages = packageDataAdapter.records;
		
		 var nestedGrids = new Array();
         var id = datarecord.uid.toString();
        
         var grid = $($(parentElement).children()[0]);
         $(grid).attr('id', 'jqxgridDetail_'+index);
         nestedGrids[index] = grid;
       
         var packagebyid = [];
        
         for (var m = 0; m < packages.length; m++) {
        	 packagebyid.push(packages[m]);
         }
         var packagessource = { datafields: [
                 { name: 'shipmentId', type:'string' },
				 { name: 'shipmentPackageSeqId', type:'string' },
	         	 { name: 'shipmentItemSeqId', type:'string' },
	         	 { name: 'productId', type:'string' },
	         	 { name: 'productName', type:'string' },
	         	 { name: 'weight', type:'string' },
	         	 { name: 'weightUomId', type:'string' },
	             { name: 'quantity', type: 'number' },
	             { name: 'quantityUomId', type:'string' },
	             { name: 'totalWeight', type:'string' },
             ],
             localdata: packagebyid,
             updaterow: function (rowid, newdata, commit) {
	        	 commit(true);
	        	 var shipmentId = newdata.shipmentId;
	        	 var shipmentPackageSeqId = newdata.shipmentPackageSeqId;
	        	 var shipmentItemSeqId = newdata.shipmentItemSeqId;
	        	 var quantity = newdata.quantity;
	        	 
	        	 $.ajax({
                     type: 'POST',                        
                     url: 'updateShipmentPackageContent',
                     data: { shipmentId: shipmentId, shipmentPackageSeqId: shipmentPackageSeqId, shipmentItemSeqId: shipmentItemSeqId, quantity: quantity},
                     success: function (data, status, xhr) {
                         if (data.responseMessage == 'error'){
                         	commit(false);
                         } else{
                         	commit(true);
                         	grid.jqxGrid('updatebounddata');
                         }
                     },
                     error: function () {
                         commit(false);
                     }
                 });
             }
         }
         var nestedGridAdapter = new $.jqx.dataAdapter(packagessource);
        
         if (grid != null) {
        	 var a ;
             grid.jqxGrid({
                 source: nestedGridAdapter, width: '96%', autoheight:'true',
                 showtoolbar:false,
                 showstatusbar: false,
		 		 editable: true,
		 		 editmode:'selectedrow',
		 		 showheader: true,
		 		 selectionmode:'singlerow',
		 		 columnsresize: true,
		 		 rowsheight: 28,
		 		 pagesize: 5,
		 		 pageable: true,
		 		 pagesizeoptions: ['5', '10', '15'],
		 		 theme: 'olbius',
                 columns: [
	                   { text: '${uiLabelMap.Product}', datafield: 'productId', width: 150 , editable: false, cellsalign: 'center'},
	                   { text: '${uiLabelMap.Quantity}', dataField: 'quantity', width: 150, editable: false, cellsalign: 'center', 
							cellsrenderer: function(row, column, value){
								var data = grid.jqxGrid('getrowdata', row);
								for(var i = 0; i < packingUomData.length; i++){
									if(packingUomData[i].quantityUomId == data.quantityUomId){
										return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + value +'('+ packingUomData[i].description + ')</span></div>'
									}
								}
							}	
	                   },
	                   { text: '${uiLabelMap.weight}', dataField: 'weight', width: 150, editable: false, cellsalign: 'center',
	                	   cellsrenderer: function(row, column, value){
								var data = grid.jqxGrid('getrowdata', row);
								for(var i = 0; i < weightUomData.length; i++){
									if(weightUomData[i].weightUomId == data.weightUomId){
										return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + value +'('+ weightUomData[i].description + ')</span>'
									}
								}
							}	
	                   },
	                   { text: '${uiLabelMap.TotalWeight}', dataField: 'totalWeight', editable: false, cellsalign: 'center',
	                	   cellsrenderer: function(row, column, value){
								var data = grid.jqxGrid('getrowdata', row);
								var totalWeight = (parseFloat(data.weight) * parseFloat(data.quantity)).toFixed(2);
								for(var i = 0; i < weightUomData.length; i++){
									if(weightUomData[i].weightUomId == data.weightUomId){
										return '<div style=\"margin-left:5px;margin-top:5px\"><span>' + totalWeight +'('+ weightUomData[i].description + ')</span>'
									}
								}
							}
	                   },
                   ]
             });
         }
 }"/>

<#assign dataField="[{ name: 'shipmentId', type: 'string'},
					{ name: 'shipmentPackageSeqId', type: 'string' }, 
					{ name: 'shipmentBoxTypeId', type: 'string'},
					{ name: 'weight', type: 'string'},
					{ name: 'weightUomId', type: 'string'},
					{ name: 'dateCreated', type: 'date', other: 'Timestamp'},
					{ name: 'rowDetail', type: 'string'},
					]"/>
<#assign columnlist="{ text: '${uiLabelMap.SequenceId}', datafield: 'shipmentPackageSeqId', width: '150px', editable: false, filterable: false},
					{ text: '${uiLabelMap.ShipmentBoxType}', datafield: 'shipmentBoxTypeId', width: '150px', editable: false, filterable: false,
						cellsrenderer: function(row, colum, value){
							    for(var i = 0; i < boxTypeData.length; i++){
									 if(value == boxTypeData[i].boxTypeId){
										 return '<span title=' + value + '>' + boxTypeData[i].description + '</span>';
									 }
							    }
							}
             		},
					{ text: '${uiLabelMap.weight}', datafield: 'weight', editable: false, width: '150px', filterable: false,
						cellsrenderer: function(row, colum, value){
							    var data = $('#jqxgridShipmentPackage').jqxGrid('getrowdata', row);
							    for(var i = 0; i < weightUomData.length; i++){
							    	if(data.weightUomId == weightUomData[i].weightUomId){
							    		return '<span>' + value +' (' + weightUomData[i].description +  ')</span>';
							    	}
							    }
							}
					},
					{ text: '${uiLabelMap.createDate}', datafield: 'dateCreated', editable: false, cellsformat: 'd', filterable: false},
				"/>
<@jqGrid id="jqxgridShipmentPackage" filtersimplemode="true" filterable="false" bindresize="false" width="700" addType="" initrowdetails = "true" rowsheight="28" dataField=dataField initrowdetailsDetail=initrowdetailsDetail editmode="selectedrow" editable="true" columnlist=columnlist clearfilteringbutton="false" showtoolbar="true" addrow="false"
		 	url="jqxGeneralServicer?sname=getShipmentPackage" customLoadFunction="true" rowdetailsheight="200" isShowTitleProperty="false"
		 />