<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script>
	var listUomTranfer = [];
	var attrValue = "";
	var attrName = "";
	var tooltiprenderer = function (element) {
        $(element).jqxTooltip({position: 'mouse', content: $(element).text() });
    }
</script>
<#assign columnlistProduct="
				 { text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				 },
				 { text: '${uiLabelMap.ProductId}', dataField: 'productId', width: '150', align: 'center', editable: false, pinned: true,
				 },
				 { text: '${uiLabelMap.ProductName}', dataField: 'productName', align: 'center', minwidth: 250, editable: false, enabletooltips: true },
				 { text: '${uiLabelMap.Unit}', dataField: 'quantityUomId', align: 'center', width: '100', cellsalign: 'right', filterable: false, columntype: 'dropdownlist', editable: true,
					 cellsrenderer: function (row, column, value) {
						 if (value){
                    		 return '<span style=\"text-align: left\">' + getDescriptionByUomId(value) +'</span>';
                    	 }
					 },
					 initeditor: function (row, cellvalue, editor) {
					 		var packingUomData = new Array();
					 		var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
							var itemSelected = data['quantityUomId'];
							var packingUomIdArray = data['packingUomIds'];
							for (var i = 0; i < packingUomIdArray.length; i++) {
								var packingUomIdItem = packingUomIdArray[i];
								var row = {};
								if (packingUomIdItem.description == undefined || packingUomIdItem.description == '') {
									row['description'] = '' + packingUomIdItem.uomId;
								} else {
									row['description'] = '' + packingUomIdItem.description;
								}
								row['uomId'] = '' + packingUomIdItem.uomId;
								packingUomData[i] = row;
							}
					 		var sourceDataPacking = {
				                localdata: packingUomData,
				                datatype: 'array'
				            };
				            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
				            editor.jqxDropDownList({ source: dataAdapterPacking, placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', displayMember: 'description', valueMember: 'uomId'});
				            editor.jqxDropDownList('selectItem', itemSelected);
				      	}
				 },
				 { text: '${uiLabelMap.Quantity}', dataField: 'quantityAccepted', width: '100', align: 'center', editable: true, filterable: false, cellsalign: 'right', columntype: 'numberinput', sortable: false,
					 cellsrenderer: function(row, column, value){
//						 var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
//                    	 var returnVal = '<div class=\"innerGridCellContent align-right\">';
// 				 		 if (data != undefined && data != null) {
// 				 			 var productId = data.productId;
// 				 			 if(mapDataSoureQuantityAccepted[productId] != null && mapDataSoureQuantityAccepted[productId] != undefined){
// 				 				returnVal += mapDataSoureQuantityAccepted[productId];
// 				 				data.quantityAccepted = mapDataSoureQuantityAccepted[productId];
// 				 				returnVal += '</div>';
// 				 				return returnVal;
// 				 			 }
// 			   			 }
// 				 		 returnVal += value + '</div>';
//		   				 return returnVal;
       				 },
                     createeditor: function (row, cellvalue, editor) {
                         editor.jqxNumberInput({spinButtons: true , spinMode: 'simple',  min:0, decimalDigits: 0 });
                     },
                     validation: function (cell, value) {
 				        if (value <= 0) {
 				            return { result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
 				        }
 				        return true;
 					 },
				 },
				 { text: '${uiLabelMap.UnitPrice}', dataField: 'unitCost', width: '100', align: 'center', editable: true, filterable: false, cellsalign: 'right', columntype: 'numberinput', sortable: false,
					 cellsrenderer: function(row, column, value){
//						 var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
//                    	 var returnVal = '<div class=\"innerGridCellContent align-right\">';
// 				 		 if (data != undefined && data != null) {
// 				 			 var productId = data.productId;
// 				 			 if(mapDataSoureUnitCost[productId] != null && mapDataSoureUnitCost[productId] != undefined){
// 				 				returnVal += mapDataSoureUnitCost[productId];
// 				 				data.unitCost = mapDataSoureUnitCost[productId];
// 				 				returnVal += '</div>';
// 				 				return returnVal;
// 				 			 }
// 			   			 }
// 				 		 returnVal += value + '</div>';
//		   				 return returnVal;
       				 },
					 createeditor: function (row, cellvalue, editor) {
						 editor.jqxNumberInput({spinButtons: true , spinMode: 'simple',  min:0, decimalDigits: 0 });
                     },
				 },
				 { text: '${uiLabelMap.ManufactureDate}', dataField: 'datetimeManufactured', width: '150', columntype: 'datetimeinput', filterable: false, cellsformat: 'dd/MM/yyyy', cellsalign: 'right', filtertype: 'range', editable: true,
					 validation: function (cell, value) {
				 		var now = new Date();
				        if (value > now) {
				            return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeNow}'};
				        }
				        var data = $('#jqxgridProduct').jqxGrid('getrowdata', cell.row);
				        if (data.expireDate){
				        	var exp = new Date(data.expireDate);
				        	if (exp < new Date(value)){
					        	return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeExpireDate}'};
					        }
				        }
				        return true;
					 },
					 createeditor: function (row, column, editor) {
						editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					 },
					 initeditor: function (row, column, editor) {
						editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					 },
				 },
				 { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', width: '150' ,columntype: 'datetimeinput', filterable: false, cellsformat: 'dd/MM/yyyy', cellsalign: 'right', filtertype: 'range', editable: true,
					 initeditor: function (row, column, editor) {
					 	editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					 },
					 validation: function (cell, value) {
				        var data = $('#jqxgridProduct').jqxGrid('getrowdata', cell.row);
				        if (data.datetimeManufactured){
				        	var mft = new Date(data.datetimeManufactured);
					        if (mft > new Date(value)){
					        	return { result: false, message: '${uiLabelMap.ExpireDateMustBeBeforeManufactureDate}'};
					        }
				        }
				        return true;
					 },
					 createeditor: function (row, column, editor) {
						editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					 },
				 },
				 { text: '${uiLabelMap.InventoryItemType}', dataField: 'inventoryItemTypeId', align: 'center', width: '150', cellsalign: 'right', filterable: false, columntype: 'dropdownlist', editable: true,
					 cellsrenderer: function (row, column, value) {
						 if (value){
							 for (var i = 0; i < listInventoryItemTypesData.length; i ++){
								 if (listInventoryItemTypesData[i].inventoryItemTypeId == value){
									 return '<span style=\"text-align: left\">' + listInventoryItemTypesData[i].description +'</span>';
								 }
							 }
                    	 }
					 },
					 initeditor: function (row, cellvalue, editor) {
				 		var sourceTmp = {
			                localdata: listInventoryItemTypesData,
			                datatype: 'array'
			            };
			            var dataAdapterTmp = new $.jqx.dataAdapter(sourceTmp);
			            editor.jqxDropDownList({ source: dataAdapterTmp, placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', displayMember: 'description', valueMember: 'inventoryItemTypeId'});
			            editor.jqxDropDownList('selectItem', 'NON_SERIAL_INV_ITEM');
					 }
				 },
				 
				 "/>
<#assign dataFieldProduct="[{ name: 'productId', type: 'string' },
             	{ name: 'productName', type: 'string' },
             	{ name: 'quantityAccepted', type: 'number' },
             	{ name: 'unitCost', type: 'number' },
             	{ name: 'quantityUomId', type: 'string' },
             	{ name: 'packingUomIds', type: 'string' },
             	{ name: 'inventoryItemTypeId', type: 'string' },
             	{ name: 'expireDate', type: 'date', other: 'Timestamp' },
             	{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp' },
	 		 	]"/>

<@jqGrid selectionmode="none" idExisted="true" filtersimplemode="true"  viewSize="15" pagesizeoptions="['5', '10', '15', '20', '25', '30', '50', '100']" id="jqxgridProduct" dataField=dataFieldProduct columnlist=columnlistProduct 
	clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" editable="true" customTitleProperties="ListProduct"  pageable="true" selectionmode="checkbox"
	url="jqxGeneralServicer?sname=JQGetListProductTotal" editmode="click" bindresize="false" jqGridMinimumLibEnable="false" autoheight="false" offmode="true"/>

<script>

var mapDataSoureQuantityAccepted = {};
var mapDataSoureUnitCost = {};
var mapDataSoureQuantityRejected = {};
$("#jqxgridProduct").on('cellendedit', function (event) 
{
	var args = event.args;
    var dataField = event.args.datafield;
    var rowBoundIndex = event.args.rowindex;
    var value = args.value;
    var oldvalue = args.oldvalue;
    var rowData = args.row;
    if(rowData != undefined){
    	if(dataField == "quantityAccepted"){
    		mapDataSoureQuantityAccepted[rowData.productId] = value;
    	}
    	if(dataField == "unitCost"){
    		mapDataSoureUnitCost[rowData.productId] = value;
    	}
    }
});

$('#jqxgridProduct').on('rowselect', function (event) 
{
    var args = event.args;
    var rowBoundIndex = args.rowindex;
    var rowData = args.row;
    var gridId = $(event.currentTarget).attr("id");
    var tmpArray = event.args.rowindex;
	if(typeof event.args.rowindex != 'number'){
        for(i = 0; i < tmpArray.length; i++){
            if(checkRequiredPurchaseLabelItem(tmpArray[i]), gridId){
                $('#jqxgridProduct').jqxGrid('clearselection');
                break; 
            }
        }
    }else{
    	if(checkRequiredPurchaseLabelItem(event.args.rowindex, gridId)){
            $('#jqxgridProduct').jqxGrid('unselectrow', event.args.rowindex);
        }
    }
});

function checkRequiredPurchaseLabelItem(rowindex, gridId){
	var data = $('#jqxgridProduct').jqxGrid('getrowdata', rowindex);
	if(data){
		var quantityAccepted = data.quantityAccepted;
		var unitCost = data.unitCost; 
		var productId = data.productId;
        if(quantityAccepted <= 0 || quantityAccepted == undefined){
        	$('#jqxgridProduct').jqxGrid('unselectrow', rowindex);
            bootbox.dialog("${uiLabelMap.QuantityNotEntered}", [{
                "label" : "${uiLabelMap.OK}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                		$('#jqxgridProduct').jqxGrid('begincelledit', rowindex, "quantityAccepted");
                    }
                }]
            );
            return true;
        } else if(data.unitCost == undefined ){
			$('#jqxgridProduct').jqxGrid('unselectrow', rowindex);
            bootbox.dialog("${uiLabelMap.UnitCostNotEntered}", [{
                "label" : "${uiLabelMap.OK}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                		$('#jqxgridProduct').jqxGrid('begincelledit', rowindex, "unitCost");
                    }
                }]
            );
        } else if (!data.datetimeManufactured){
        	$('#jqxgridProduct').jqxGrid('unselectrow', rowindex);
            bootbox.dialog("${uiLabelMap.ManufacturedDateNotEnter}", [{
                "label" : "${uiLabelMap.OK}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                		$('#jqxgridProduct').jqxGrid('begincelledit', rowindex, "datetimeManufactured");
                    }
                }]
            );
            return true;
        } else if (!data.expireDate){
        	$('#jqxgridProduct').jqxGrid('unselectrow', rowindex);
            bootbox.dialog("${uiLabelMap.ExpireDateNotEnter}", [{
                "label" : "${uiLabelMap.OK}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                		$('#jqxgridProduct').jqxGrid('begincelledit', rowindex, "expireDate");
                    }
                }]
            );
            return true;
        } else if (!data.inventoryItemTypeId){
        	$('#jqxgridProduct').jqxGrid('unselectrow', rowindex);
            bootbox.dialog("${uiLabelMap.InventoryItemTypeNotEnter}", [{
                "label" : "${uiLabelMap.OK}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                		$('#jqxgridProduct').jqxGrid('begincelledit', rowindex, "inventoryItemTypeId");
                    }
                }]
            );
            return true;
        }
	}
}

$('#jqxgridProduct').on('rowunselect', function (event) {
});

function receiveInventoryProductByLog(listProducts){
	var facilityId = $("#facilityId").val();
	var inventoryItemTypeId = $("#inventoryItemTypeId").val();
	var datetimeReceived= $('#datetimeReceived').jqxDateTimeInput('getDate').getTime(); 
	$.ajax({
		url: "receiveInventoryProductFromOther",
		type: "POST",
		data: {
			listProducts: listProducts,
			facilityId: facilityId, 
			inventoryItemTypeId: inventoryItemTypeId,
			datetimeReceived: datetimeReceived,
		},
		dataType: "json",
		success: function(data) {
		}
	}).done(function(data) {
		$('#jqxgridProduct').jqxGrid('clearselection');
		$('#jqxgridProduct').jqxGrid('updatebounddata');
		mapDataSoureQuantityAccepted = {};
		$("#notificationReceiveSuccess").text('${StringUtil.wrapString(uiLabelMap.LogReceiveProuductSuccess)}');
		$("#jqxNotificationReceiveSuccess").jqxNotification('open');
	}); 
}
</script>