<div id="StockProductFromLocationToLocationInFacilityContrary" class="hide">
	<div>${uiLabelMap.StockLocationInFacility}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div id="contentNotificationSuccessMoveProduct"  style="width:100%">
				</div>
				<div class="control-group no-left-margin">
					<div class="span6">
						<div style="overflow:hidden;overflow-y:visible; max-height:300px !important;">
							<div id="jqxgridStockTranferContrary">
							</div>
						</div>
					</div>
					<div class="span6">
						<div style="overflow:hidden;overflow-y:visible; max-height:300px !important;">
							<div id="jqxgridStockContrary">
							</div>
						</div>
					</div>
			    </div>
			    <div class="control-group no-left-margin">
			    </div>
			    <div class="control-group no-left-margin">
					<div class="span12">
				       	<input id="cancelStockProductContrary" type="button" value="${uiLabelMap.CommonCancel}" />  
					</div>      	 
			    </div>
			</div>
		</div>	
	</div>
</div>

<div id="dialogTranferProductToLocationContrary" class="hide">
	<div>${uiLabelMap.ProductNewLocationType}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div class="form-horizontal">
					<div id="contentNotificationCheckInventoryItemIdExistsContrary">
					</div>
					<div id="contentNotificationContentNestedContrary" style="width:100%">
					</div>
					<div id="contentNotificationCheckQuantityTranferContrary"  style="width:100%">
					</div>
					<div id="contentNotificationCheckSelectDropDownToTranferContrary"  style="width:100%">
					</div>
					<div id="contentNotificationCheckSelectProductTranferContrary"  style="width:100%">
					</div>
					<div id="contentNotificationCheckQuantityTranferNegativeContrary"  style="width:100%">
					</div>
					<div class="control-group no-left-margin">
						<div class="controls">
							<input id="locationCodeCurrentContrary" type="hidden"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div class="control-label">${uiLabelMap.StockProductIdForLocationInFacilityForLocation}:</div>
						<div class="controls">
							<div id="locationIdTranferContrary"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div style="overflow:hidden;overflow-y:visible; max-height:200px !important;">
							<div id="jqxgridProductByLocationInFacilityContrary">
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div class="controls">
							<input id="locationIdCurrentContrary" type="hidden"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
					</div>
					<div class="control-group no-left-margin">
						<div class="controls">
							<input style="margin-right: 5px;" type="button" id="saveContrary" value="${uiLabelMap.CommonSave}" />
					       	<input id="cancelContrary" type="button" value="${uiLabelMap.CommonCancel}" />  
						</div>      	
				    </div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="jqxMessageNotificationSuccessMoveProduct">
	<div id="notificationContentSuccessMoveProduct">
	</div>
</div>

<div id="jqxNotificationCheckInventoryItemIdExistsContrary" >
	<div id="notificationCheckInventoryItemIdExistsContrary">
	</div>
</div>

<div id="jqxNotificationQuantityTranferContrary" >
	<div id="notificationQuantityTranferContrary">
	</div>
</div>

<div id="jqxNotificationSelectDropDownToTranferContrary" >
	<div id="notificationCheckSelectDropDownToTranferContrary">
	</div>
</div>

<div id="jqxNotificationCheckSelectProductTranferContrary" >
	<div id="notificationCheckSelectProductTranferContrary">
	</div>
</div>

<div id="jqxNotificationQuantityTranferNegativeContrary" >
	<div id="notificationQuantityTranferNegativeContrary">
	</div>
</div>
<script>

$("#jqxMessageNotificationSuccessMoveProduct").jqxNotification({ width: "100%", appendContainer: "#contentNotificationSuccessMoveProduct", opacity: 0.9, autoClose: true, template: "success" });
$("#jqxNotificationCheckInventoryItemIdExistsContrary").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckInventoryItemIdExistsContrary", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationQuantityTranferContrary").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckQuantityTranferContrary", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationSelectDropDownToTranferContrary").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckSelectDropDownToTranferContrary", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationCheckSelectProductTranferContrary").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckSelectProductTranferContrary", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationQuantityTranferNegativeContrary").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckQuantityTranferNegativeContrary", opacity: 0.9, autoClose: true, template: "error" });
$("#StockProductFromLocationToLocationInFacilityContrary").jqxWindow({
    width:'80%', maxWidth: '80%' , height:400 ,resizable: false, isModal: true, autoOpen: false, cancelButton: $("#cancelStockProductContrary"), modalOpacity: 0.7, theme: 'olbius'         
});

$("#saveContrary").jqxButton({ height: 30, width: 80 });
$("#cancelContrary").jqxButton({ height: 30, width: 80 });
$("#cancelContrary").mousedown(function () {
    $("#dialogTranferProductToLocationContrary").jqxWindow('close');
});

$("#cancelStockProductContrary").jqxButton({ height: 30, width: 80 });

$("#dialogTranferProductToLocationContrary").jqxWindow({
    width: '80%',
    height: 350,
    autoOpen: false,
    resizable: false, 
    isModal: true, 
    autoOpen: false, 
    cancelButton: $("#cancelContrary"), 
    modalOpacity: 0.7
});

function loadDataRowToJqxGirdTreeMoveProductContrary(dataLocationId, dataLocationIdRemain){
	if(dataLocationIdRemain.length != 0){
		$.ajax({
			  url: "loadDataRowToJqxGirdTree",
			  type: "POST",
			  data: {locationId: dataLocationId, locationIdRemain: dataLocationIdRemain},
			  dataType: "json",
			  success: function(data) {
			  }    
		}).done(function(data) {
			var listLocationFacility = data["listLocationFacility"];
			var listLocationFacilityRemain = data["listLocationFacilityRemain"];
			loadFunctionAddProductInLocationContrary(listLocationFacility);
			loadFunctionStockProductInLocationContrary(listLocationFacility, listLocationFacilityRemain);
			loadDataJqxDropDownListByLocationIdTranferContrary(listLocationFacilityRemain);
			$("#locationCodeCurrentContrary").jqxInput({ disabled: true});
	        $("#locationIdCurrentContrary").jqxInput();
	    	$("#StockProductFromLocationToLocationInFacilityContrary").jqxWindow('open');
	    	$("#myTree").jqxTreeGrid('clearSelection');
		});
	}else{
		$("#notificationContentSelectMyTreeFullSelect").text('${StringUtil.wrapString(uiLabelMap.DSCheckSelectTreeFullSelect)}');
     	$("#jqxMessageNotificationSelectMyTreeFullSelect").jqxNotification('open');
	}
}

function loadFunctionAddProductInLocationContrary(dataRow){
	var source =
	{
		dataType: "json",
	    dataFields: [
	        { name: 'locationId', type: 'string' },         
	    	{ name: 'facilityId', type: 'string' }, 
	    	{ name: 'locationCode', type: 'string' },
	    	{ name: 'parentLocationId', type: 'string' },
	    	{ name: 'locationFacilityTypeId', type: 'string' },
	    	{ name: 'description', type: 'string' },
	    ],
	    hierarchy:
	    {
	    	keyDataField: { name: 'locationId' },
	        parentDataField: { name: 'parentLocationId' }
	    },
	    id: 'locationId',
	    localData: dataRow
    };
	updateBounDataJqxTreeGirdContrary(source);
}

function updateBounDataJqxTreeGirdContrary(source){
	var facilityId = '${facilityId}';
	
	var listlocationFacilityMapOfJqxgridStockContrary;
	var rowDetailJqxgridStockContrary;
	$.ajax({
		  url: "loadListLocationFacility",
		  type: "POST",
		  data: {facilityId: facilityId},
		  dataType: "json",
		  success: function(data) {
			  listlocationFacilityMapOfJqxgridStockContrary = data["listlocationFacilityMap"];
			  rowDetailJqxgridStockContrary = data["listInventoryItemLocationDetailMap"]
		  }    
	}).done(function(data) {
		loadDataAndDetailJqxgridStockContrary(source, rowDetailJqxgridStockContrary);
	});
}

function loadDataAndDetailJqxgridStockContrary(source, rowDetailJqxgridStockContrary){
	var dataAdapter = new $.jqx.dataAdapter(source,{
    	beforeLoadComplete: function (records) {
	    	for (var x = 0; x < records.length; x++) {
	    		for(var key in rowDetailJqxgridStockContrary){
	    			if(records[x].locationId == key){
	    				records[x].rowDetailData = rowDetailJqxgridStockContrary[key];
	    			}
	    		}
	    	}
	    	return records;
    	}
    });
	
	$("#jqxgridStockContrary").jqxTreeGrid({
    	width: '100%',
        source: dataAdapter,
        pageable: true,
        columnsResize: true,
        altRows: true,
        selectionMode: 'multipleRows',
        sortable: true,
        rowDetails: true,
        rowDetailsRenderer: function (rowKey, row) {
        	var indent = (1+row.level) * 20;
        	
        	var rowDetailDataInLocation = row.rowDetailData;
        	/*if(rowDetailDataInLocation == 'undefined' || quantityData == 'undefined' || uomIdData == 'undefined'){
        	}*/
        	var detailsData = [];
        	var details = "<table class='table table-striped table-bordered table-hover dataTable' style='margin: 10px; min-height: 95px; height: 95px; width:100%" + indent + "px;'>" +
							"<thead>" +
	        					"<tr>" +
				    				"<th>" + '${uiLabelMap.ProductProductId}' + "</th>" +
				    			  	"<th>" + '${uiLabelMap.Quantity}' + "</th>" +
				    			  	"<th>" + '${uiLabelMap.QuantityUomId}' + "</th>" +
				    		    "</tr>" +
			    		    "</thead>";
        	for(var i in rowDetailDataInLocation){
        		var productIdData = rowDetailDataInLocation[i].productId;
        		var quantityData = rowDetailDataInLocation[i].quantity;
        		var uomIdData = rowDetailDataInLocation[i].uomId;
        		details += "<tbody>" +
	        					"<tr>" +
			        				"<td>" + productIdData + "</td>" +
			        			  	"<td>" + quantityData + "</td>" +
			        			  	"<td>" + getUom(uomIdData) + "</td>" +
			        			"</tr>" +
			        		"</tbody>";
        	}
        	details += "</table>";
        	detailsData.push(details);
            return detailsData;
        },
        ready: function()
        {
        	$("#jqxgridStockContrary").jqxTreeGrid('selectRow', '2');
        	$("#dialogTranferProductToLocationContrary").on('close', function () {
        		$("#jqxgridStockContrary").jqxTreeGrid({ disabled: false });
            });
        },
        columns: [
        	{ text: '${uiLabelMap.FacilitylocationSeqIdCurrent}', dataField: 'locationCode', minwidth: 200},
            { text: '${uiLabelMap.Description}', dataField: 'description', minwidth: 120 },
        ]
	});
		
	$("#jqxgridStockContrary").on('rowDoubleClick', function (event) {
		$("#dialogTranferProductToLocationContrary").jqxWindow('open');
		
	    var args = event.args;
	    var key = args.key;
	    var row = args.row;
	    // update the widgets inside jqxWindow.
	    $("#dialogTranferProductToLocationContrary").jqxWindow('setTitle', "${uiLabelMap.FacilitylocationSeqIdCurrent}: " + row.locationCode);
	    $("#locationIdCurrentContrary").val(row.locationId);
	    $("#locationCodeCurrentContrary").val(row.locationCode);
	    // disable jqxTreeGrid.
	    loadDetailsJqxGirdContrary([], []);
        $("#locationIdTranferContrary").jqxDropDownList('uncheckAll');
	    $("#jqxgridStockContrary").jqxTreeGrid({ disabled: true });
	});
}

var productDataInLocationContrary = [];
function loadDetailsJqxGirdContrary(arrayRowDetails, dataAdapter){
	var sourceRowDetails =
    {
        localdata: arrayRowDetails,
        datatype: "local",
        datafields:
        [
         	{ name: 'inventoryItemId', type: 'string' },
            { name: 'locationId', type: 'string' },
            { name: 'productId', type: 'string' },
            { name: 'quantity', type: 'number' },
            { name: 'uomId', type: 'string' }
        ],
        addrow: function (rowid, rowdata, position, commit) {
            commit(true);
        },
    };
    var ordersDataAdapter = new $.jqx.dataAdapter(sourceRowDetails, { autoBind: true });
    orders = ordersDataAdapter.records;
    var nestedGrids = new Array();
    
    // create nested grid.
    var initrowdetails = function (index, parentElement, gridElement, record) {
        var id = record.uid.toString();
        var grid = $($(parentElement).children()[0]);
        nestedGrids[index] = grid;
        var filtergroup = new $.jqx.filter();
        var filter_or_operator = 1;
        var filtervalue = id;
        var filtercondition = 'equal';
        var filter = filtergroup.createfilter('stringfilter', filtervalue, filtercondition);
        // fill the orders depending on the id.
        var ordersbyid = [];
        for (var m = 0; m < orders.length; m++) {
        	if (record.locationId == orders[m]["locationId"]){
        		ordersbyid.push(orders[m]);
        	}
        }
        var orderssource = { datafields: [
            { name: 'inventoryItemId', type: 'string' },                              
            { name: 'locationId', type: 'string' },
            { name: 'productId', type: 'string' },
            { name: 'quantity', type: 'string' },
            { name: 'quantityTranfer', type: 'string' },
            { name: 'uomId', type: 'string' },
        ],
            id: 'locationId',
            localdata: ordersbyid
        }
        
        var nestedGridAdapter = new $.jqx.dataAdapter(orderssource);
        if (grid != null) {
            grid.jqxGrid({
                source: nestedGridAdapter, width: '100%', height: '100%',
                selectionmode: 'checkbox',
                editable: true,
                columns: [
                  { text: '${uiLabelMap.ProductProductId}', datafield: 'productId', minwidth: 200, editable: false},
                  { text: '${uiLabelMap.QuantityCurrent}', datafield: 'quantity', width: 150, editable: false },
                  { text: '${uiLabelMap.QuantityTransferSum}', datafield: 'quantityTranfer', width: 150, editable: true },
                  { text: '${uiLabelMap.QuantityUomId}', datafield: 'uomId', width: 150 , editable: false,
                	  cellsrenderer: function(row, colum, value){
   				       var data = grid.jqxGrid('getrowdata', row);
   				       var uomId = data.uomId;
   				       var uomId = getUom(uomId);
   				       return '<span>' + uomId + '</span>';
   				   	  }
                  },
               ]
            });
            grid.on('rowselect', function (event) {
            	var rowindex1 = grid.jqxGrid('getselectedrowindex');
                var data1 = grid.jqxGrid('getrowdata', rowindex1);
                productDataInLocationContrary.push(data1);
            });
            grid.on('rowunselect', function (event) {
            	var row = args.row;
            	var ii = productDataInLocation.indexOf(row);
            	productDataInLocationContrary.splice(ii, 1);
            });
            grid.jqxTooltip({ content: '${uiLabelMap.EnterQuantityTransferSum}', position: 'mouse', name: 'movieTooltip'});
        }
        
    }    
    
    $("#jqxgridProductByLocationInFacilityContrary").jqxGrid(
	{
	    width: '100%',
	    source: dataAdapter,
	    columnsresize: true,
	    pageable: true,
        rowdetails: true,
        initrowdetails: initrowdetails,
        rowdetailstemplate: { rowdetails: "<div id='grid' style='margin: 10px;'></div>", rowdetailsheight: 220, rowdetailshidden: true },
        ready: function () {
            $("#jqxgridProductByLocationInFacilityContrary").jqxGrid('showrowdetails', 1);
        },
	    columns: [
	      { text: '${uiLabelMap.StockProductIdForLocationInFacilityForLocation}', datafield: 'locationCode', minwidth: 120 },
	    ]
	});
}

function loadFunctionStockProductInLocationContrary(dataRow, dataSoureInput){
	var source =
	{
		dataType: "json",
	    dataFields: [
	        { name: 'locationId', type: 'string' },         
	    	{ name: 'locationCode', type: 'string' },
	    	{ name: 'parentLocationId', type: 'string' },
	    	{ name: 'description', type: 'string' },
	    	{ name: 'locationIdTranfer', type: 'string' },
	    	{ name: 'productTranfer', type: 'string' },
	    	{ name: 'quantityTranfer', type: 'string' },
	    ],
	    hierarchy:
	    {
	    	keyDataField: { name: 'locationId' },
	        parentDataField: { name: 'parentLocationId' }
	    },
	    id: 'locationId',
	    localData: dataSoureInput
    };
	updateBounDataJqxTreeGirdContraryRemain(source);
}

function updateBounDataJqxTreeGirdContraryRemain(source){
	var facilityId = '${facilityId}';
	var listlocationFacilityMapOfJqxgridStockContrary;
	var rowDetailJqxgridStockContrary;
	$.ajax({
		  url: "loadListLocationFacility",
		  type: "POST",
		  data: {facilityId: facilityId},
		  dataType: "json",
		  success: function(data) {
			  rowDetailJqxgridStockContrary = data["listInventoryItemLocationDetailMap"]
		  }    
	}).done(function(data) {
		loadDataAndDetailJqxgridStockTranferContrary(source, rowDetailJqxgridStockContrary);
	});
}

$('#locationIdTranferContrary').on('close', function (event) {
	var dataSoureValues = [];
	var dataSoureLable = [];
	var items = $("#locationIdTranferContrary").jqxDropDownList('getCheckedItems');
	if(items.length == 0){
//		$("#notificationCheckSelectDropDownToTranferContrary").text('${StringUtil.wrapString(uiLabelMap.DSCheckSelectDropDownToTranfer)}');
//     	$("#jqxNotificationSelectDropDownToTranferContrary").jqxNotification('open');
	}else{
		for(var values in items){
			dataSoureValues.push(items[values].value);
			dataSoureLable.push(items[values].label);
		}
		$("#locationIdTranferContrary").jqxDropDownList('uncheckAll');
		loadProductByLocationIdInFacilityContrary(dataSoureValues, dataSoureLable);
	}
});

function loadDataAndDetailJqxgridStockTranferContrary(source, rowDetailJqxgridStockContrary){
	var dataAdapter = new $.jqx.dataAdapter(source,{
    	beforeLoadComplete: function (records) {
	    	for (var x = 0; x < records.length; x++) {
	    		for(var key in rowDetailJqxgridStockContrary){
	    			if(records[x].locationId == key){
	    				records[x].rowDetailData = rowDetailJqxgridStockContrary[key];
	    			}
	    		}
	    	}
	    	return records;
    	}
    });
	$("#jqxgridStockTranferContrary").jqxTreeGrid
	({
	    	width: '100%',
	        source: dataAdapter,
	        pageable: true,
	        columnsResize: true,
	        altRows: true,
            selectionMode: 'multipleRows',
            sortable: true,
            editable:true,
            rowDetails: true,
            rowDetailsRenderer: function (rowKey, row) {
            	var indent = (1+row.level) * 20;
            	
            	var rowDetailDataInLocation = row.rowDetailData;
            	/*if(rowDetailDataInLocation == 'undefined' || quantityData == 'undefined' || uomIdData == 'undefined'){
            	}*/
            	var detailsData = [];
            	var details = "<table class='table table-striped table-bordered table-hover dataTable' style='margin: 10px; min-height: 95px; height: 95px; width:100%" + indent + "px;'>" +
    							"<thead>" +
    	        					"<tr>" +
    				    				"<th>" + '${uiLabelMap.ProductProductId}' + "</th>" +
    				    			  	"<th>" + '${uiLabelMap.Quantity}' + "</th>" +
    				    			  	"<th>" + '${uiLabelMap.QuantityUomId}' + "</th>" +
    				    		    "</tr>" +
    			    		    "</thead>";
            	for(var i in rowDetailDataInLocation){
            		var productIdData = rowDetailDataInLocation[i].productId;
            		var quantityData = rowDetailDataInLocation[i].quantity;
            		var uomIdData = rowDetailDataInLocation[i].uomId;
            		details += "<tbody>" +
    	        					"<tr>" +
    			        				"<td>" + productIdData + "</td>" +
    			        			  	"<td>" + quantityData + "</td>" +
    			        			  	"<td>" + getUom(uomIdData) + "</td>" +
    			        			"</tr>" +
    			        		"</tbody>";
            	}
            	details += "</table>";
            	detailsData.push(details);
                return detailsData;
            },
            ready: function()
            {
            	$("#jqxgridStockTranferContrary").jqxTreeGrid('selectRow', '1');
                
            },
	        columns: [
	        	{ text: '${uiLabelMap.StockProductIdForLocationInFacilityForLocation}', dataField: 'locationCode', minwidth: 200, editable:false},
	        	{ text: '${uiLabelMap.Description}', dataField: 'description', minwidth: 120 },
            ]
	 });
}

function tranfersProductFromLocationToLocationInFacilityContrary(){
	var locationIdCurrent = $('#locationIdCurrentContrary').val();
	var inventoryItemIdTranfers = [];
	var locationIdTranfers = [];
	var productIdTranfers = [];
	var quantityCurrentTranfers = [];
	var quantityTranferTranfer = [];
	var uomIdTranfer = [];
	if(productDataInLocationContrary.length == 0 || productDataInLocationContrary[0] == undefined){
		$("#notificationCheckSelectProductTranferContrary").text('${StringUtil.wrapString(uiLabelMap.CheckSelectProductTranfer)}');
     	$("#jqxNotificationCheckSelectProductTranferContrary").jqxNotification('open');
	}else{
		var b = 0;
		for(var i in productDataInLocationContrary){
			var a = productDataInLocationContrary[i].quantityTranfer;
			if(a != undefined && a != null && a != ""){
				inventoryItemIdTranfers.push(productDataInLocationContrary[i].inventoryItemId);
				locationIdTranfers.push(productDataInLocationContrary[i].locationId);
				productIdTranfers.push(productDataInLocationContrary[i].productId);
				quantityCurrentTranfers.push(productDataInLocationContrary[i].quantity);
				quantityTranferTranfer.push(productDataInLocationContrary[i].quantityTranfer);
				uomIdTranfer.push(productDataInLocationContrary[i].uomId);
				b++;
			}
		}
		if(b <= 0){
			$("#notificationContentNestedContrary").text('${StringUtil.wrapString(uiLabelMap.CheckQuantityTransferSum)}');
			$("#jqxNotificationNestedContrary").jqxNotification('open');
			unRowSelectContrary();
		}else{
			$.ajax({
			  url: "tranfersProductFromLocationToLocationInFacility",
			  type: "POST",
			  data: {inventoryItemIdTranfers: inventoryItemIdTranfers, locationIdCurrent: locationIdCurrent, locationIdTranfers: locationIdTranfers, productIdTranfers: productIdTranfers, quantityCurrentTranfers: quantityCurrentTranfers, quantityTranferTranfer: quantityTranferTranfer, uomIdTranfer: uomIdTranfer},
			  dataType: "json",
			  success: function(data) {
			  }    
			}).done(function(data) {
				unRowSelectContrary();
				var value = data["value"];
				if(value == "errorQuantityTranfer"){
					productDataInLocationContrary = [];
					$("#notificationQuantityTranferContrary").text('${StringUtil.wrapString(uiLabelMap.CheckQuantityCurrent)}');
			     	$("#jqxNotificationQuantityTranferContrary").jqxNotification('open');
				}
				if(value == "negative"){
					productDataInLocationContrary = [];
					$("#notificationQuantityTranferNegativeContrary").text('${StringUtil.wrapString(uiLabelMap.DSCheckQuantityNegative)}');
			     	$("#jqxNotificationQuantityTranferNegativeContrary").jqxNotification('open');
				}
				if(value == "number"){
					productDataInLocationContrary = [];
					$("#notificationQuantityTranferNegativeContrary").text('${StringUtil.wrapString(uiLabelMap.DSCheckQuantityNegative)}');
			     	$("#jqxNotificationQuantityTranferNegativeContrary").jqxNotification('open');
				}
				if(value == "success"){
					inventoryItemIdTranfers = [];
					locationIdTranfers = [];
					productIdTranfers = [];
					quantityCurrentTranfers = [];
					quantityTranferTranfer = [];
					uomIdTranfer = [];
					productDataInLocationContrary = [];
					loadDataDetailByTreeGirdContraryWhenClickSaveContrary();
					$('#dialogTranferProductToLocationContrary').jqxWindow('close');
					$("#jqxgridStockContrary").jqxTreeGrid('updateBoundData');
					$("#jqxgridStockTranferContrary").jqxTreeGrid('updateBoundData');
					$("#notificationContentSuccessMoveProduct").text('${StringUtil.wrapString(uiLabelMap.StockLocationInventoryItemSuccess)}');
					$("#jqxMessageNotificationSuccessMoveProduct").jqxNotification('open');
					loadData();
				}
			});
		}
	}
	$('#saveContrary').jqxButton({disabled: false });
}

$("#cancelStockProductContrary").click(function (){
	loadData();
	dataRow = [];
});

function loadDataJqxDropDownListByLocationIdTranferContrary(dataSource){
	$("#locationIdTranferContrary").jqxDropDownList({source: dataSource, placeHolder: "Please select" , checkboxes: true,displayMember: 'locationCode', valueMember: 'locationId'});
}

function loadProductByLocationIdInFacilityContrary(locationIdTranferValue, locationIdTranferLable) {
	var listMapProductLocationInFacility;
	$.ajax({
		  url: "loadProductByLocationIdInFacility",
		  type: "POST",
		  data: {locationIdTranfer: locationIdTranferValue},
		  dataType: "json",
		  success: function(data) {
			  listMapProductLocationInFacility = data["listMapProductLocationInFacility"];
		  }    
	}).done(function(data) {
		resultLoadProductByLocationIdInFacility(listMapProductLocationInFacility, locationIdTranferValue, locationIdTranferLable);
	});
}

$("#saveContrary").click(function (){
	$('#saveContrary').jqxButton({disabled: true });
	tranfersProductFromLocationToLocationInFacilityContrary();
});

function loadDataDetailByTreeGirdContraryWhenClickSaveContrary(){
    if(dataRow.length == 0){
    	$("#notificationContentSelectMyTree").text('${StringUtil.wrapString(uiLabelMap.SelectStockProductJqxTreeGird)}');
     	$("#jqxMessageNotificationSelectMyTree").jqxNotification('open');
    }else{
    	checkParentLocationIdInDataRowMoveProductContrary(dataRow);
    }
    loadData();
}

function checkParentLocationIdInDataRowMoveProductContrary(data){
	var rowAll = $("#myTree").jqxTreeGrid('getRows');
    var rowsData = new Array();
    var traverseTree = function(rowAll)
    {
        for(var i = 0; i < rowAll.length; i++)
        {
        	var objectRowData = {
    			locationId: rowAll[i].locationId,
    			parentLocationId: rowAll[i].parentLocationId,
    			locationCode: rowAll[i].locationCode,
    			description: rowAll[i].description,
        	};
            rowsData.push(objectRowData);
            if (rowAll[i].records)
            {
                traverseTree(rowAll[i].records);
            }
        }
    };
    traverseTree(rowAll);
    var iIndex = 0;
    var tmpList = [];
    lbl1: for(i = 0; i < rowsData.length;i++){
    	var tmp1 = rowsData[i];
    	for(j = 0; j < data.length; j++){
    		if(data[j].locationId == tmp1.locationId){
    			continue lbl1;
    		}
    	}
    	tmpList[iIndex++] = tmp1;
    }
	
	
	var parentLocationIdInDataRow = [];
	lbl:for(var i = 0; i < data.length; i++){
			for(var j = 0; j < data.length; j++){
				if(data[i].locationId == data[j].parentLocationId){
					continue lbl;
				}
			}
			parentLocationIdInDataRow.push(data[i].locationId);
	}
	
	var parentLocationIdIntmpList = [];
	lbl2:for(var i = 0; i < tmpList.length; i++){
			for(var j = 0; j < tmpList.length; j++){
				if(tmpList[i].locationId == tmpList[j].parentLocationId){
					continue lbl2;
				}
			}
			parentLocationIdIntmpList.push(tmpList[i].locationId);
	}
	
	uniquesLocationId = [];
	for(var i=0;i<parentLocationIdInDataRow.length;i++){
		var str=parentLocationIdInDataRow[i];
    	if(uniquesLocationId.indexOf(str)==-1){
    		uniquesLocationId.push(str);
        }
    }
	
	uniquesLocationIdTmpList = [];
	for(var i=0;i<parentLocationIdIntmpList.length;i++){
		var str=parentLocationIdIntmpList[i];
    	if(uniquesLocationIdTmpList.indexOf(str)==-1){
    		uniquesLocationIdTmpList.push(str);
        }
    }
	
	loadDataRowToJqxGirdTreeMoveProductContrary(uniquesLocationId, uniquesLocationIdTmpList);
}

function unRowSelectContrary(){
	$('#grid0').jqxGrid('clearselection');
}
</script>