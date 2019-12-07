<div id="StockProductFromLocationToLocationInFacility" class="hide">
	<div>${uiLabelMap.StockLocationInFacility}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div id="contentNotificationSuccessMoveProduct2"  style="width:100%">
				</div>
				<div class="control-group no-left-margin">
					<div class="span6">
						<div style="overflow:hidden;overflow-y:visible; max-height:300px !important;">
							<div id="jqxgridStock">
							</div>
						</div>
					</div>
					<div class="span6">
						<div style="overflow:hidden;overflow-y:visible; max-height:300px !important;">
							<div id="jqxgridStockTranfer">
							</div>
						</div>
					</div>
			    </div>
			    <div class="control-group no-left-margin">
					<div class="span12">
				       	<input id="alterExitStockProduct" type="button" value="${uiLabelMap.CommonCancel}" />  
					</div>      	
			    </div>
			</div>
		</div>	
	</div>
</div>

<div id="dialogTranferProductToLocation" class="hide">
	<div>${uiLabelMap.ProductNewLocationType}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div class="form-horizontal">
					<div id="contentNotificationCheckSelectDropDownToTranfer"  style="width:100%">
					</div>
					<div id="contentNotificationCheckInventoryItemIdExists"  style="width:100%">
					</div>
					<div id="contentNotificationContentNested"  style="width:100%">
					</div>
					<div id="contentNotificationCheckQuantityTranfer"  style="width:100%">
					</div>
					<div id="contentNotificationCheckSelectProductTranfer"  style="width:100%">
					</div>
					<div id="contentNotificationCheckQuantityTranferNegative"  style="width:100%">
					</div>
					<div class="control-group no-left-margin">
						<div class="controls">
							<input id="locationCodeCurrent" type="hidden"></input>
						</div>
					</div>
					
					<div class="control-group no-left-margin">
						<div class="control-label">${uiLabelMap.StockProductIdForLocationInFacilityForLocation}:</div>
						<div class="controls">
							<div id="locationIdTranfer"></div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div style="overflow:hidden;overflow-y:visible; max-height:200px !important;">
							<div id="jqxgridProductByLocationInFacility">
							</div>
						</div>
					</div>
					<div class="control-group no-left-margin">
						<div class="controls">
							<input id="locationIdCurrent" type="hidden"></input>
						</div>
					</div>
					<div class="control-group no-left-margin">
					</div>
					<div class="control-group no-left-margin">
						<div class="controls">
							<input style="margin-right: 5px;" type="button" id="save" value="${uiLabelMap.CommonSave}" />
					       	<input id="cancel" type="button" value="${uiLabelMap.CommonCancel}" />  
						</div>      	
				    </div>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="jqxMessageNotificationSuccessMoveProduct2">
	<div id="notificationContentSuccessMoveProduct2">
	</div>
</div>

<div id="jqxNotificationCheckInventoryItemIdExists" >
	<div id="notificationCheckInventoryItemIdExists">
	</div>
</div>

<div id="jqxNotificationSelectDropDownToTranfer" >
	<div id="notificationCheckSelectDropDownToTranfer">
	</div>
</div>

<div id="jqxNotificationNested">
	<div id="notificationContentNested">
	</div>
</div>

<div id="jqxNotificationQuantityTranfer" >
	<div id="notificationQuantityTranfer">
	</div>
</div>

<div id="jqxNotificationCheckSelectProductTranfer" >
	<div id="notificationCheckSelectProductTranfer">
	</div>
</div>

<div id="jqxNotificationQuantityTranferNegative" >
	<div id="notificationQuantityTranferNegative">
	</div>
</div>

<script>


$("#jqxMessageNotificationSuccessMoveProduct2").jqxNotification({ width: "100%", appendContainer: "#contentNotificationSuccessMoveProduct2", opacity: 0.9, autoClose: true, template: "success" });
$("#jqxNotificationSelectDropDownToTranfer").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckSelectDropDownToTranfer", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationCheckInventoryItemIdExists").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckInventoryItemIdExists", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationNested").jqxNotification({ width: "100%", appendContainer: "#contentNotificationContentNested", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationQuantityTranfer").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckQuantityTranfer", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationCheckSelectProductTranfer").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckSelectProductTranfer", opacity: 0.9, autoClose: true, template: "error" });
$("#jqxNotificationQuantityTranferNegative").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckQuantityTranferNegative", opacity: 0.9, autoClose: true, template: "error" });

$("#StockProductFromLocationToLocationInFacility").jqxWindow({
    width: '80%', maxWidth: '80%', height:400 ,resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterExitStockProduct"), modalOpacity: 0.7           
});

$("#alterExitStockProduct").jqxButton({ height: 30, width: 80 });

$("#save").jqxButton({ height: 30, width: 80 });
$("#cancel").jqxButton({ height: 30, width: 80 });
$("#cancel").mousedown(function () {
    $("#dialogTranferProductToLocation").jqxWindow('close');
});
	
var dataSoureValues = [];
var dataSoureLable = [];
$('#locationIdTranfer').on('close', function (event) {
	dataSoureValues = [];
	dataSoureLable = [];
	var items = $("#locationIdTranfer").jqxDropDownList('getCheckedItems');
	if(items.length == 0){
		/*$("#notificationCheckSelectDropDownToTranfer").text('${StringUtil.wrapString(uiLabelMap.DSCheckSelectDropDownToTranfer)}');
     	$("#jqxNotificationSelectDropDownToTranfer").jqxNotification('open');*/
	}
	else{
		for(var values in items){
			dataSoureValues.push(items[values].value);
			dataSoureLable.push(items[values].label);
		}
		loadProductByLocationIdInFacility(dataSoureValues, dataSoureLable);
	}
});	

	
function loadDataAndDetailJqxgridStock(source, rowDetail){
	var dataAdapter = new $.jqx.dataAdapter(source,{
    	beforeLoadComplete: function (records) {
	    	for (var x = 0; x < records.length; x++) {
	    		for(var key in rowDetail){
	    			if(records[x].locationId == key){
	    				records[x].rowDetailData = rowDetail[key];
	    			}
	    		}
	    	}
	    	return records;
    	}
    });
	
	$("#jqxgridStock").jqxTreeGrid(
	{
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
            	if(rowDetailDataInLocation.length == 0){
            		
            	}else{
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
            	}
            },
            ready: function()
            {
           	 $("#jqxgridStock").jqxTreeGrid('selectRow', '2');
            },
	        columns: [
	        	{ text: '${uiLabelMap.StockProductIdForLocationInFacilityForLocation}', dataField: 'locationCode', minwidth: 200},
	            { text: '${uiLabelMap.Description}', dataField: 'description', minwidth: 120 },
            ]
	 });
}

function loadDataOfRowDetailByMyTreeGird(source, rowDetail){
	loadDataAndDetailJqxgridStock(source, rowDetail);
}

function updateBoundDataTreeGird(source){
	var facilityId = '${facilityId}';
	var rowDetail;
	$.ajax({
		  url: "loadListLocationFacility",
		  type: "POST",
		  data: {facilityId: facilityId},
		  dataType: "json",
		  success: function(data) {
			  rowDetail = data["listInventoryItemLocationDetailMap"]
		  }    
	}).done(function(data) {
		loadDataOfRowDetailByMyTreeGird(source, rowDetail);
	});
}

function loadProductByLocationIdInFacility(locationIdTranferValue, locationIdTranferLable) {
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

function test(){
	 var id = $("#jqxgridProductByLocationInFacility").jqxGrid('getrowid', 0);
	$("#jqxgridProductByLocationInFacility").jqxGrid('deleterow', id);
	return 555;
} 

function loadFunctionAddProductInLocation(sourceData){
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
	    localData: sourceData
    };
	updateBoundDataTreeGird(source);
}

function loadDataRowToJqxGirdTreeMoveProduct(dataLocationId, dataLocationIdRemain){
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
			loadFunctionAddProductInLocation(listLocationFacility);
			loadFunctionStockProductInLocation(listLocationFacilityRemain);
			loadDataJqxDropDownListByLocationIdTranfer(listLocationFacility);
			$("#locationCodeCurrent").jqxInput({ disabled: true});
	        $("#locationIdCurrent").jqxInput();
	    	$("#myTree").jqxTreeGrid('clearSelection');
	        $("#StockProductFromLocationToLocationInFacility").jqxWindow('open');
		});
	}
	else{
		$("#notificationContentSelectMyTreeFullSelect").text('${StringUtil.wrapString(uiLabelMap.DSCheckSelectTreeFullSelect)}');
     	$("#jqxMessageNotificationSelectMyTreeFullSelect").jqxNotification('open');
	}
}

function loadDataRowDeatailByjqxTreeGirdClickJqxButtonStockLocation(){
    if(dataRow.length == 0){
    	$("#notificationContentSelectMyTree").text('${StringUtil.wrapString(uiLabelMap.SelectStockProductJqxTreeGird)}');
     	$("#jqxMessageNotificationSelectMyTree").jqxNotification('open');
    }
    else{
    	checkParentLocationIdInDataRowMoveProduct(dataRow);
    }
}

function checkParentLocationIdInDataRowMoveProduct(data){
	
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
	
	var uniquesLocationIdTmpList = [];
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
	
	uniquesLocationIdTmpList = [];
	for(var i=0;i<parentLocationIdIntmpList.length;i++){
		var str=parentLocationIdIntmpList[i];
    	if(uniquesLocationIdTmpList.indexOf(str)==-1){
    		uniquesLocationIdTmpList.push(str);
        }
    }
	
	uniquesLocationId = [];
	for(var i=0;i<parentLocationIdInDataRow.length;i++){
		var str=parentLocationIdInDataRow[i];
    	if(uniquesLocationId.indexOf(str)==-1){
    		uniquesLocationId.push(str);
        }
    }
	
	loadDataRowToJqxGirdTreeMoveProduct(uniquesLocationId, uniquesLocationIdTmpList);
}

function loadFunctionStockProductInLocation(dataSoureInput){
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
	updateBoundDataTreeGirdRemain(source);
}

function updateBoundDataTreeGirdRemain(source){
	var facilityId = '${facilityId}';
	var rowDetail;
	$.ajax({
		  url: "loadListLocationFacility",
		  type: "POST",
		  data: {facilityId: facilityId},
		  dataType: "json",
		  success: function(data) {
			  rowDetail = data["listInventoryItemLocationDetailMap"]
		  }    
	}).done(function(data) {
		loadDataOfRowDetailByMyTreeGirdRemain(source, rowDetail);
	});
}

function loadDataOfRowDetailByMyTreeGirdRemain(source, rowDetail){
	loadDataAndDetailJqxgridStockTranfer(source, rowDetail);
}

$("#save").click(function (){
	$('#save').jqxButton({disabled: true });
	tranfersProductFromLocationToLocationInFacility();
});

function loadDataAndDetailJqxgridStockTranfer(source, rowDetail){
	var dataAdapter = new $.jqx.dataAdapter(source,{
    	beforeLoadComplete: function (records) {
	    	for (var x = 0; x < records.length; x++) {
	    		for(var key in rowDetail){
	    			if(records[x].locationId == key){
	    				records[x].rowDetailData = rowDetail[key];
	    			}
	    		}
	    	}
	    	return records;
    	}
    });
	$("#jqxgridStockTranfer").jqxTreeGrid
	({
	    	width: '100%',
	        source: dataAdapter,
	        columnsResize: true,
	        altRows: true,
	        pageable: true,
            selectionMode: 'multipleRows',
            sortable: true,
            editable:true,
            rowDetails: true,
            rowDetailsRenderer: function (rowKey, row) {
            	var indent = (1+row.level) * 20;
            	
            	var rowDetailDataInLocation = row.rowDetailData;
            	if(rowDetailDataInLocation.length == 0){
            	}
            	else{
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
            	}
            },
            ready: function()
            {
            	$("#jqxgridStockTranfer").jqxTreeGrid('selectRow', '2');
                $("#dialogTranferProductToLocation").on('close', function () {
                    // enable jqxTreeGrid.
                    $("#jqxgridStockTranfer").jqxTreeGrid({ disabled: false });
                });
                
            },
	        columns: [
	        	{ text: '${uiLabelMap.FacilitylocationSeqIdCurrent}', dataField: 'locationCode', minwidth: 100, editable:false},
            ]
	 });
	
	$("#jqxgridStockTranfer").on('rowDoubleClick', function (event) {
		$("#dialogTranferProductToLocation").jqxWindow('open');
		
        var args = event.args;
        var key = args.key;
        var row = args.row;
        // update the widgets inside jqxWindow.
        $("#dialogTranferProductToLocation").jqxWindow('setTitle', "${uiLabelMap.FacilitylocationSeqIdCurrent}: " + row.locationCode);
        $("#locationIdCurrent").val(row.locationId);
        $("#locationCodeCurrent").val(row.locationCode);
        // disable jqxTreeGrid.
        loadDetailsJqxGird([], []);
        $("#locationIdTranfer").jqxDropDownList('uncheckAll');
        $("#jqxgridStockTranfer").jqxTreeGrid({ disabled: true });
    });
}


function loadDataJqxDropDownListByLocationIdTranfer(sourceData){
	$("#locationIdTranfer").jqxDropDownList({source: sourceData, placeHolder: "Please select" , checkboxes: true, displayMember: 'locationCode', valueMember: 'locationId'});
}

var productDataInLocation = [];
function loadDetailsJqxGird(arrayRowDetails, dataAdapter){
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
                productDataInLocation.push(data1);
            });
            grid.on('rowunselect', function (event) {
            	var row = args.row;
            	var ii = productDataInLocation.indexOf(row);
            	productDataInLocation.splice(ii, 1);
            });
        }
        
    }    
    loadJqxGirdByJqxgridProductByLocationInFacility(dataAdapter, initrowdetails);
}

function loadJqxGirdByJqxgridProductByLocationInFacility(dataAdapter, initrowdetails){
	$("#jqxgridProductByLocationInFacility").jqxGrid(
	{
	    width: '100%',
	    source: dataAdapter,
	    columnsresize: true,
	    pageable: true,
        rowdetails: true,
        initrowdetails: initrowdetails,
        rowdetailstemplate: { rowdetails: "<div id='grid' style='margin: 10px;'></div>", rowdetailsheight: 220, rowdetailshidden: true },
        ready: function () {
            $("#jqxgridProductByLocationInFacility").jqxGrid('showrowdetails', 1);
        },
	    columns: [
	      { text: '${uiLabelMap.StockProductIdForLocationInFacilityForLocation}', datafield: 'locationCode', minwidth: 120 },
	    ]
	});
}

function tranfersProductFromLocationToLocationInFacility(){
	$("#locationIdTranfer").jqxDropDownList('setContent', 'Please choose...'); 
	$("#locationIdTranfer").jqxDropDownList('uncheckAll');
	var locationIdCurrent = $('#locationIdCurrent').val();
	var inventoryItemIdTranfers = [];
	var locationIdTranfers = [];
	var productIdTranfers = [];
	var quantityCurrentTranfers = [];
	var quantityTranferTranfer = [];
	var uomIdTranfer = [];
	
	if(productDataInLocation.length == 0 || productDataInLocation[0] == undefined){
		$("#notificationCheckSelectProductTranfer").text('${StringUtil.wrapString(uiLabelMap.CheckSelectProductTranfer)}');
     	$("#jqxNotificationCheckSelectProductTranfer").jqxNotification('open');
	}else{
		var b = 0;
		for(var i in productDataInLocation){
			var a = productDataInLocation[i].quantityTranfer;
			if(a != undefined && a != null && a != ""){
				inventoryItemIdTranfers.push(productDataInLocation[i].inventoryItemId);
				locationIdTranfers.push(productDataInLocation[i].locationId);
				productIdTranfers.push(productDataInLocation[i].productId);
				quantityCurrentTranfers.push(productDataInLocation[i].quantity);
				quantityTranferTranfer.push(productDataInLocation[i].quantityTranfer);
				uomIdTranfer.push(productDataInLocation[i].uomId);
				b++;
			}
		}
		if(b <= 0){
			$("#notificationContentNested").text('${StringUtil.wrapString(uiLabelMap.CheckQuantityTransferSum)}');
			$("#jqxNotificationNested").jqxNotification('open');
			unRowSelect();
		}
		else{
			$.ajax({
				  url: "tranfersProductFromLocationToLocationInFacility",
				  type: "POST",
				  data: {inventoryItemIdTranfers: inventoryItemIdTranfers, locationIdCurrent: locationIdCurrent, locationIdTranfers: locationIdTranfers, productIdTranfers: productIdTranfers, quantityCurrentTranfers: quantityCurrentTranfers, quantityTranferTranfer: quantityTranferTranfer, uomIdTranfer: uomIdTranfer},
				  dataType: "json",
				  success: function(data) {
				  }    
			}).done(function(data) {
				unRowSelect();
				var value = data["value"];
				if(value == "errorQuantityTranfer"){
					$("#notificationQuantityTranfer").text('${StringUtil.wrapString(uiLabelMap.CheckQuantityCurrent)}');
			     	$("#jqxNotificationQuantityTranfer").jqxNotification('open');
			     	productDataInLocation = [];
			     	productIdTranfers = [];
				}
				if(value == "negative"){
					$("#notificationQuantityTranferNegative").text('${StringUtil.wrapString(uiLabelMap.DSCheckQuantityNegative)}');
			     	$("#jqxNotificationQuantityTranferNegative").jqxNotification('open');
			     	productDataInLocation = [];
				}
				if(value == "number"){
					$("#notificationQuantityTranferNegative").text('${StringUtil.wrapString(uiLabelMap.CheckQuantityIsNumber)}');
			     	$("#jqxNotificationQuantityTranferNegative").jqxNotification('open');
			     	productDataInLocation = [];
				}
				if(value == "success"){
					dataSoureValues = [];
					dataSoureLable = [];
					$("#locationIdTranfer").jqxDropDownList('uncheckAll');
					productDataInLocation = [];
					loadDataRowDeatailByjqxTreeGirdClickJqxButtonStockLocation();
					loadData();
					$('#dialogTranferProductToLocation').jqxWindow('close');
					$("#notificationContentSuccessMoveProduct2").text('${StringUtil.wrapString(uiLabelMap.StockLocationInventoryItemSuccess)}');
					$("#jqxMessageNotificationSuccessMoveProduct2").jqxNotification('open');
				}
			});
		}
	}
	$('#save').jqxButton({disabled: false });
}

$("#alterExitStockProduct").click(function (){
	loadData();
	dataRow = [];
});

function unRowSelect(){
	$('#grid0').jqxGrid('clearselection');
}

$('#dialogTranferProductToLocation').on('open', function (event) {
//	loadDetailsJqxGird([], []);
}); 
</script>