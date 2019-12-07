<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
<@jqOlbCoreLib />
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script>
<#assign localeStr = "VI" />
<#if locale = "en">
	<#assign localeStr = "EN" />
</#if>

<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false)>
var mapUomData = {
		<#if uoms?exists>
			<#list uoms as item>
				<#assign s1 = StringUtil.wrapString(item.get("description", locale)?if_exists)/>
				"${item.uomId?if_exists}": "${s1}",
			</#list>
		</#if>	
};

var addZero = function(i) {
    if (i < 10) {i = "0" + i;}
    return i;
};

function receiveFacilityDistributor(){
	var tmpS = $("#jqxgridProductReturn").jqxGrid('source');
    tmpS._source.url = "jqxGeneralServicer?sname=getListProductReturnDistributors&returnId=${returnHeader.returnId?if_exists}";
    $("#jqxgridProductReturn").jqxGrid('source', tmpS);
	$("#alterpopupWindowReceiveFacility").jqxWindow('open');
}
</script>
<div id="alterpopupWindowReceiveFacility" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.StockIn}
	</div>
	<div class='form-window-container'>
		<div class="row-fluid">
				<div id="jqxgridProductReturn"></div>
	    </div>
		<div class="form-action popup-footer">
            <button id="addButtonCancel" class='btn btn-danger form-action-button pull-right' ><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
            <button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	    </div>
	</div>
</div>

<#assign columnlist="
	{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	    groupable: false, draggable: false, resizable: false,
	    datafield: '', columntype: 'number', width: 50,
	    cellsrenderer: function (row, column, value) {
	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    }
	},		
	{ text: '${uiLabelMap.ProductId}', pinned: true, dataField: 'productCode', width: 120, editable:false, 
	},
	{ text: '${uiLabelMap.ProductName}', dataField: 'productName', width: 250, editable:false, pinned: true,
	},
	{ text: '${uiLabelMap.ProductManufactureDate}', dataField: 'datetimeManufactured', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:true, cellsalign: 'right',
		columntype: 'datetimeinput',
		createeditor: function (row, column, editor) {
			editor.jqxDateTimeInput({width: 150, height: 25, formatString: 'dd/MM/yyyy'});
		},
		cellbeginedit: function (row, datafield, columntype) {
			var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
			var quantityRecieve = data.quantityRecieve;
			if(quantityRecieve == 0){
				return false;
			}
		},
		cellsrenderer: function(row, colum, value) {
			if (!value){
				 return '<span style=\"text-align: right\" title=\"_NA_\" class=\"focus-color\">_NA_</span>';
			 } else {
				 return '<span style=\"text-align: right\" class=\"focus-color\">'+ DatetimeUtilObj.getFormattedDate(value)+'</span>';
			 }
		},
		validation: function (cell, value) {
	 		var now = new Date();
	        if (value > now) {
	            return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeNow}'};
	        }
	        var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', cell.row);
	        if (data.expireDate){
	        	var exp = new Date(data.expireDate);
	        	if (exp < new Date(value)){
		        	return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeExpireDate}'};
		        }
	        }
	        return true;
		 },
	}, 
	{ text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:true, cellsalign: 'right',
		columntype: 'datetimeinput',
		createeditor: function (row, column, editor) {
			editor.jqxDateTimeInput({width: 150, height: 25, formatString: 'dd/MM/yyyy'});
		},
		cellbeginedit: function (row, datafield, columntype) {
			var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
			var quantityRecieve = data.quantityRecieve;
			if(quantityRecieve == 0){
				return false;
			}
		},
		cellsrenderer: function(row, colum, value) {
			if (!value){
				 return '<span style=\"text-align: right\" title=\"_NA_\" class=\"focus-color\">_NA_</span>';
			 } else {
				 return '<span style=\"text-align: right\" class=\"focus-color\">'+ DatetimeUtilObj.getFormattedDate(value)+'</span>';
			 }
		},
		validation: function (cell, value) {
	        var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', cell.row);
	        if (data.datetimeManufactured){
	        	var mnf = new Date(data.datetimeManufactured);
	        	if (mnf > new Date(value)){
		        	return { result: false, message: '${uiLabelMap.ExpireDateMustBeBeforeManufactureDate}'};
		        }
	        }
	        return true;
		 },
	},
	{ text: '${uiLabelMap.NoNumber}', datafield: 'lotId', align: 'left', width: 120, editable: true, cellsalign: 'left', filterable: false, 
		cellsrenderer: function(row, column, value){
			 if (!value){
				 return '<span style=\"text-align: right\" title=\"_NA_\" class=\"focus-color\">_NA_</span>';
			 } else {
				 return '<span style=\"text-align: right\" class=\"focus-color\">'+ value +'</span>';
			 }
		}, 
		createeditor: function (row, column, editor) {
			editor.jqxInput({width: 150, height: 25});
		},
		cellbeginedit: function (row, datafield, columntype) {
			var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
			var quantityRecieve = data.quantityRecieve;
			if(quantityRecieve == 0){
				return false;
			}
		}
	},
	{ text: '${uiLabelMap.ListQuantityImported}', datafield: 'quantityRecieve', align: 'left', width: 130, editable: true, cellsalign: 'right', columntype: 'numberinput', cellsformat: 'd', filterable: false,
		cellsrenderer: function(row, column, value){
			if(value == null){
				return '<span style=\"text-align: right;\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
			}
	 	},
		validation: function (cell, value) {
			var row = cell.row;
			var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
			var quantityOnHandDiff = data.quantityOnHandDiff;
			var returnQuantity = data.returnQuantity;
			if(value <= 0){
				return {result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
			}else{
				if(quantityOnHandDiff == null){
					if(value > returnQuantity){
						return {result: false, message: '${uiLabelMap.NumberEnteredWasIncorrect}'};
					}
				}else{
					var valueInput = value + quantityOnHandDiff; 
					if(valueInput > returnQuantity){
						return {result: false, message: '${uiLabelMap.NumberEnteredWasIncorrect}'};
					}
				}
			}
			return true;
		},
		createeditor: function (row, cellvalue, editor) {
			editor.jqxNumberInput({decimalDigits: 0, digits: 9});
		},
		cellbeginedit: function (row, datafield, columntype) {
			var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
			var quantityRecieve = data.quantityRecieve;
			if(quantityRecieve == 0){
				return false;
			} 
		}
	},
	{ text: '${uiLabelMap.RequiredNumber}', datafield: 'returnQuantity', align: 'left', width: 130, editable: false, cellsalign: 'right', filterable: false,
	},
	{ text: '${uiLabelMap.QuantityNumberWasEntered}', datafield: 'quantityOnHandDiff', align: 'left', width: 130, editable: false, cellsalign: 'right', filterable: false,
		cellsrenderer: function(row, column, value){
 			if (value != undefined && value != null && value != ''){
 				return '<span style=\"text-align: right;\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
 			}else{
 				return '<span style=\"text-align: right;\" title=\"0\">0</span>';
 			}
	 	},
	},
	{ text: '${uiLabelMap.QuantityUomId}', datafield: 'quantityUomId', align: 'left', width: 120, editable: false, cellsalign: 'left',  filterable: false,
		cellsrenderer: function (row, column, value){
			if(value){
				return '<span>' + mapUomData[value] + '<span>';
			}
		}
	},
	"/>
	<#assign dataField="[{ name: 'productId', type: 'string' },
	{ name: 'productCode', type: 'string' },
	{ name: 'productName', type: 'string' },
	{ name: 'returnQuantity', type: 'number' },
	{ name: 'quantityOnHandDiff', type: 'number'},
	{ name: 'quantityRecieve', type: 'number' }, 
	{ name: 'currencyUomId', type: 'string' },
	{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp' },
	{ name: 'expireDate', type: 'date', other: 'Timestamp' },
	{ name: 'lotId', type: 'string' },
	{ name: 'returnId', type: 'string' }, 
	{ name: 'returnItemSeqId', type: 'string' },  
	{ name: 'quantityUomId', type: 'string' },  
]"/>

<@jqGrid filtersimplemode="true" id="jqxgridProductReturn" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" editable="true" 
url="jqxGeneralServicer?sname=getListProductReturnDistributors&returnId=${returnHeader.returnId?if_exists}" editmode="selectedcell" idExisted="true"
customTitleProperties="ListProduct"
jqGridMinimumLibEnable="true"/>
<script>
	$("#alterpopupWindowReceiveFacility").jqxWindow({
		maxWidth: 1500, minWidth: 550, height:350, width:1200, minHeight: 500, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:'olbius'           
	});
	
	$("#jqxgridProductReturn").on('cellbeginedit', function (event) 
	{
		document.getElementById("addButtonSave").disabled = true;
	});


	$("#jqxgridProductReturn").on('cellendedit', function (event) 
	{
		document.getElementById("addButtonSave").disabled = false;
	});
	
	$("#addButtonSave").click(function () {
		var facilityId = '${returnHeader.destinationFacilityId}';
		if(facilityId == null || facilityId == "" || facilityId == undefined){
			bootbox.dialog("${uiLabelMap.SelectFacilityToReceive}", 
				[{"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }
	        ]);
		}else{
			var dataRows = $('#jqxgridProductReturn').jqxGrid('getrows');
			if(dataRows.length == 0){
				document.getElementById("addButtonSave").disabled = true;
				bootbox.dialog("${uiLabelMap.LogNotifyReceice}", 
					[{"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
			            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
			            "callback": function() {bootbox.hideAll();}
			        }
		        ]);
			}else{
				var checkReceive = true;
				for(var i in dataRows){
					var datetimeManufactured = dataRows[i].datetimeManufactured; 
					var expireDate = dataRows[i].expireDate; 
					if(datetimeManufactured == null){
						bootbox.dialog("${uiLabelMap.LogEnterDateManufacturer}", 
							[{"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
					            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					            "callback": function() {bootbox.hideAll();}
					        }
				        ]);
						checkReceive = false;
						break;
					}
					
					if(expireDate == null){
						bootbox.dialog("${uiLabelMap.LogEnteExpireDate}", 
							[{"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
					            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					            "callback": function() {bootbox.hideAll();}
					        }
				        ]);
						checkReceive = false;
						break;
					}
				}
				 
				if(checkReceive == true){
					var listProducts = [];
					if (dataRows != undefined && dataRows.length > 0){
						for (var i = 0; i < dataRows.length; i ++){
							var data = dataRows[i];
							var map = {};
					   		map['productId'] = data.productId;
					   		map['datetimeManufactured'] = data.datetimeManufactured.getTime();
				   			map['expireDate'] = data.expireDate.getTime();
					   		map['quantity'] = data.quantityRecieve;
					   		if (OlbCore.isNotEmpty(data.lotId)) {
                                map['lotId'] = data.lotId.toUpperCase();
                            } else {
                                map['lotId'] = "";
                            }
					   		map['currencyUom'] = data.currencyUomId;
					   		map['returnId'] = data.returnId; 
					   		map['returnItemSeqId'] = data.returnItemSeqId;
					   		if(data.quantityRecieve != 0){
					   			listProducts.push(map);
					   		}
						}
					}
					if(listProducts.length == 0){
						document.getElementById("addButtonSave").disabled = true;
						bootbox.dialog("${uiLabelMap.LogNotifyReceice}", 
							[{"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
					            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					            "callback": function() {bootbox.hideAll();}
					        }
				        ]);
					}else{
						var listProductData = JSON.stringify(listProducts);
						bootbox.dialog("${uiLabelMap.LogYouSureYouWantToImport}", 
							[{"label": "${StringUtil.wrapString(uiLabelMap.wgcancel)}", 
								"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					            "callback": function() {bootbox.hideAll();}
					        }, 
					        {"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
					            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					            "callback": function() {
					            	receiveFacilityDistributer(listProductData, facilityId);
					        }
				        }]);
					}
				}
			}
		}
	});
	$("#jqxNotificationAddSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddSuccess", opacity: 0.9, autoClose: true, template: "success" });
	function receiveFacilityDistributer(listProduct, facilityId){
		document.getElementById("addButtonSave").disabled = true;
		$.ajax({
			url: "receiveFacilityDistributer",
			type: "POST",
			data: {
				listProducts: listProduct,
				facilityId: facilityId,
			},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			var value = data["value"];
			$("#alterpopupWindowReceiveFacility").jqxWindow('close');
			$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.LogSuccess)}');
			$("#jqxNotificationAddSuccess").jqxNotification('open');
			window.location.replace("viewReturnOrder?returnId=${returnHeader.returnId?if_exists}");
		});
	}
</script>
