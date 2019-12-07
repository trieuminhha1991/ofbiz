<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/demos/sampledata/generatedata.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript" src="/delys/images/js/import/miscUtil.js"></script>


<div id="AddProductInLocationFacility" class="hide">
	<div>${uiLabelMap.AddProductInLocationFacility}</div>
	<div style="overflow: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div id="contentNotificationCreateInventoryItemLocaitonError">
				</div>
				<div id="contentNotificationCheckNegative">
				</div>
				<div id="contentNotificationCheckQOH">
				</div>
				<div class="control-group no-left-margin">
					<div style="overflow:hidden;overflow-y:visible; max-height:300px !important;">
						<div id="jqxTreeGridAddProduct">
						</div>
					</div>
			    </div>
			    <div class="control-group no-left-margin">
					<div class="controls">
						<input style="margin-right: 5px;" type="button" id="alterAddProduct" value="${uiLabelMap.CommonSave}" />
				       	<input id="alterExitProduct" type="button" value="${uiLabelMap.CommonCancel}" />  
					</div>      	
			    </div>
			</div>
		</div>	
	</div>
</div>

<div id="jqxNotificationInventoryItemLocationError">
	<div id="notificationContentInventoryItemLocationError">
	</div>
</div>

<div id="jqxNotificationCheckNegative" >
	<div id="notificationCheckNegative">
	</div>
</div>

<div id="jqxNotificationCheckQOH" >
	<div id="notificationCheckQOH">
	</div>
</div>

<script>
	//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	$("#AddProductInLocationFacility").jqxWindow({
	    width: '80%', maxWidth: '80%', height:400 ,resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterExitProduct"), modalOpacity: 0.7           
	});
	
	$("#jqxNotificationInventoryItemLocationError").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCreateInventoryItemLocaitonError", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationCheckNegative").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckNegative", opacity: 0.9, autoClose: true, template: "error" });
	$("#jqxNotificationCheckQOH").jqxNotification({ width: "100%", appendContainer: "#contentNotificationCheckQOH", opacity: 0.9, autoClose: true, template: "error" });
	
	$("#alterAddProduct").jqxButton({ height: 30, width: 80 });
	$("#alterExitProduct").jqxButton({ height: 30, width: 80 });
	
	
	$("#alterAddProduct").click(function (){
		$("#alterAddProduct").jqxButton({ disabled: true});
		var data = $('#jqxTreeGridAddProduct').jqxTreeGrid('getRows');
		var locationId = new Array();
		var productId = new Array();
		var inventoryItemId = new Array();
		var quantity = new Array();
		var uomId = new Array();
		var value = [];
		
		var b = 0;
		for(var rows in data){
			value = data[rows];
			var locationIdCheck = value.locationId;
			var productIdCheck = value.productId;
			var inventoryItemIdCheck = value.inventoryItemId;
			var quantityCheck = value.quantity;
			var uomIdCheck = value.uomId;
			
			if(locationIdCheck != undefined && locationIdCheck != "" && productIdCheck != undefined && productIdCheck != "" && inventoryItemIdCheck != undefined && inventoryItemIdCheck != "" && quantityCheck != undefined && quantityCheck != ""&& uomIdCheck != undefined && uomIdCheck != ""){
				locationId.push(value.locationId);
				productId.push(value.productId);
				inventoryItemId.push(value.inventoryItemId);
				quantity.push(value.quantity);
				uomId.push(value.uomId);
				b++;
			}
		}
		if(b <= 0){
			$("#notificationContentInventoryItemLocationError").text('${StringUtil.wrapString(uiLabelMap.DSCheckIsEmptyCreateLocationFacility)}');
			$("#jqxNotificationInventoryItemLocationError").jqxNotification('open');
		}
		else{
			var dataPram = {locationId : locationId, productId: productId, inventoryItemId: inventoryItemId, quantity: quantity, uomId: uomId};
			$.ajax({
				url: "addProductInLocationFacility",
				type: "POST",
				data: dataPram,
				dataType: "json",
				success: function(data) {
					
				}    
			}).done(function(data) {
				var value = data["value"];
				
				if(value == "negative"){
					$("#notificationCheckNegative").text('${StringUtil.wrapString(uiLabelMap.DSCheckQuantityNegative)}');
					$("#jqxNotificationCheckNegative").jqxNotification('open');
				}
				if(value == "number"){
					$("#notificationCheckNegative").text('${StringUtil.wrapString(uiLabelMap.CheckQuantityIsNumber)}');
					$("#jqxNotificationCheckNegative").jqxNotification('open');
				}
				if(value == "checkQoh"){
					$("#notificationCheckQOH").text('${StringUtil.wrapString(uiLabelMap.checkQuantityOnHandTotal)}');
					$("#jqxNotificationCheckQOH").jqxNotification('open');
				}
				if(value == "success"){
					loadData();
					$('#AddProductInLocationFacility').jqxWindow('close');
					$("#notificationContentInventoryItemLocationSuccess").text('${StringUtil.wrapString(uiLabelMap.NotifiCreateSucess)}');
					$("#jqxNotificationInventoryItemLocationSuccess").jqxNotification('open');
				}
			});
		}
		loadData();
	});
	
	function checkParentLocationIdInDataRow(data){
		var parentLocationIdInDataRow = [];
		lbl:for(var i = 0; i < data.length; i++){
				for(var j = 0; j < data.length; j++){
					if(data[i].locationId == data[j].parentLocationId){
						continue lbl;
					}
				}
				parentLocationIdInDataRow.push(data[i].locationId);
		}
		uniquesLocationId = [];
		for(var i=0;i<parentLocationIdInDataRow.length;i++){
			var str=parentLocationIdInDataRow[i];
	    	if(uniquesLocationId.indexOf(str)==-1){
	    		uniquesLocationId.push(str);
	        }
	    }
		
		loadDataRowToJqxGirdTree(uniquesLocationId);
	}

	function loadDataRowToJqxGirdTree(dataLocationId){
		$.ajax({
			  url: "loadDataRowToJqxGirdTreeAddProduct",
			  type: "POST",
			  data: {locationId: dataLocationId},
			  dataType: "json",
			  success: function(data) {
			  }    
		}).done(function(data) {
			var listLocationFacility = data["listLocationFacility"];
			addProductInLocationByFacility(listLocationFacility);
		});
	}

	function addProductInLocationByFacility(sourceData){
		var source =
	    {
	        localdata: sourceData,
	        dataType: "json",
	        datafields:
	        [
	            { name: 'locationId', type: 'string' },
	            { name: 'locationCode', type: 'string' },
	            { name: 'locationFacilityTypeId', type: 'string' },
	            { name: 'parentLocationId', type: 'string' },
	            { name: 'description', type: 'string' },
	            { name: 'productId', type: 'string' },
	            { name: 'facilityId', type: 'string' },
	            { name: 'inventoryItemId', type: 'string'},
	            { name: 'quantity', type: 'number' },
	            { name: 'uomId', type: 'string' }
	        ],
	        hierarchy:
	        {
	        	keyDataField: { name: 'locationId' },
	            parentDataField: { name: 'parentLocationId' }
	        },
	        id: 'locationId',
	    };
		
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
			bindDataJqxTreeGirdAddProductToLocationInFacility(source, rowDetail);
		});
		
	}

	function bindDataJqxTreeGirdAddProductToLocationInFacility(source, rowDetail){
		var dataAdapter = new $.jqx.dataAdapter(source,{
	    	beforeLoadComplete: function (records) {
		    	for (var x = 0; x < records.length; x++) {
		    		for(var key in rowDetail){
		    			if(records[x].locationId == key){
		    				records[x].rowDetailDataAdapter = rowDetail[key];
		    			}
		    		}
		    	}
		    	return records;
	    	}
	    });
		var listExpireDate;
	    var listConfigPacking;
	    
	    $("#jqxTreeGridAddProduct").jqxTreeGrid(
	    {
	    	width: '100%',
	        source: dataAdapter,
	        pageable: true,
	        columnsResize: true,
	        altRows: true,
	        selectionMode: 'multipleRows',
	        sortable: true,
	        rowDetails: true,
	        editable: true,
	        editSettings: {
	            saveOnPageChange: true,
	            saveOnBlur: true,
	            saveOnSelectionChange: true,
	            cancelOnEsc: true,
	            saveOnEnter: true,
	            editSingleCell: true,
	            editOnDoubleClick: true,
	            editOnF2: true
	        },
	        rowDetailsRenderer: function (rowKey, row) {
	        	var indent = (1+row.level) * 20;
	        	var rowDetailDataInLocation = row.rowDetailDataAdapter;
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
	       	 $("#jqxTreeGridAddProduct").jqxTreeGrid('selectRow', '2');
	        },
	        
	        columns: [
		        /*{ text: '${uiLabelMap.FacilityLocationPosition}', datafield: 'locationCode', width: 100 },*/
		        { text: '${uiLabelMap.FacilityLocationPosition}', datafield: 'description', minwidth: 200, editable: false },
		        { text: '${uiLabelMap.ProductProductId}', datafield: 'productId', minwidth: 150, columnType: 'template',
		        	createEditor: function (row, cellvalue, editor, cellText, width, height) {
		        		editor.jqxDropDownList({autoDropDownHeight: true, placeHolder: "Please select....", source: productData, width: '100%', height: '100%' ,displayMember: 'productId', valueMember: 'productId'});
		        	},
		        },
		        { text: '${uiLabelMap.ProductExpireDate}', datafield: 'inventoryItemId', minwidth: 150 , columntype: 'template' ,editable:true,
		        	cellsRenderer: function (row, column, value) {
		                 return "<input type='button' value='Click Me' class='jqx-buttons'/>";
		             },
		        	initEditor: function (row, cellValue, editor, cellText, width, height) {
		        		editor.jqxDropDownList({ autoDropDownHeight: true, source: arrayExpireDate, displayMember: 'inventoryItemId', valueMember: 'inventoryItemId' ,
                            renderer: function (index, label, value) {
			                    var datarecord = arrayExpireDate[index];
			                    return datarecord.expireDate;
			                },selectionRenderer: function () {
				                var item = editor.jqxDropDownList('getSelectedItem');
				                if (item) {
		  							return '<span title=' + item.value +'>' + getExpireDate(item.value) + '</span>';
				                }
				                return '<span>Please Choose:</span>';
				            }});
		        	}
		        },
		        { text: '${uiLabelMap.DSQuantity}', datafield: 'quantity', minwidth: 100, editable:true	,
	            	  cellsRenderer: function (row, column, value) {
			                 return "<input type='button' value='Click Me' class='jqx-buttons'/>";
			             }
		        },
		        { text: '${uiLabelMap.QuantityUomId}', datafield: 'uomId', minwidth: 150, columnType: 'template',
		        	initEditor: function (row, cellValue, editor, cellText, width, height) {
			        	editor.jqxDropDownList({autoDropDownHeight: true, source: uniquesUomId, placeHolder: "Please select...." ,width: '100%', height: '100%'});
			        }
		        }
	        ]
	    });
	    
	    $("#AddProductInLocationFacility").jqxWindow('open');
	}
	
	function getExpireDate(inventoryItemId) {
		if (inventoryItemId != null) {
			for ( var x in arrayExpireDate) {
				if (inventoryItemId == arrayExpireDate[x].inventoryItemId) {
					return arrayExpireDate[x].expireDate;
				}
			}
		} else {
			return "";
		}
	}
	
	
	$('#jqxTreeGridAddProduct').on('cellEndEdit', function (event){
	    var args = event.args;
	    // row key
	    var rowKey = args.key;
	    // row's data.
	    var row = args.row;
	    var rowBoundIndex = args.index;
	    // column's data field.
	    var columnDataField = args.dataField;
	    // column's display field.
	    var columnDisplayField = args.displayField;
	    // cell's value.
	    var value = args.value;
	    var facilityId22 = '${facilityId}';
	    if(columnDataField == "productId"){
	    	$.ajax({
	    		url: "loadExpireDateByProductIdInInventoryItem",
	    		type: "POST",
	    		data: {facilityId: facilityId22, productId: value},
	    		dataType: "json",
	    		success: function(data) {
	    		}	  
	    	}).done(function(data) {
	    		var listInventoryItem = data["listExpireDate"];
				var listConfigPacking = data["listConfigPacking"];
	    		loadExpireDateAndUomIdByProductId(listInventoryItem, listConfigPacking, rowKey);
	    	});
	    }
	});
	
	var arrayExpireDate = [];
	function loadExpireDateAndUomIdByProductId(listInventoryItem, listConfigPacking, rowKey){
		arrayExpireDate = [];
		for(var i in listInventoryItem){
			if(listInventoryItem[i].expireDate == null){
			}else{
				var arrayExpireDateMap = {};
				arrayExpireDateMap['inventoryItemId'] = (listInventoryItem[i].inventoryItemId);
				arrayExpireDateMap['expireDate'] = (new Date(listInventoryItem[i].expireDate.time)).toTimeOlbius();
				arrayExpireDate.push(arrayExpireDateMap);
			}
		}
		$("#jqxTreeGridAddProduct").jqxTreeGrid('setCellValue', rowKey, 'inventoryItemId', null);
		$("#jqxTreeGridAddProduct").jqxTreeGrid('setCellValue', rowKey, 'uomId', null);
		
		var arrayUomId = [];
		if(listConfigPacking != undefined){
			for(var j = 0; j < listConfigPacking.length; j++){
				arrayUomId.push(listConfigPacking[j]);
			}
		}
		loadUomIdByProductIdFromConfigPacking(arrayUomId);
	}

	function loadUomIdByProductIdFromConfigPacking(arrayUomId){
		var uomFromId = [];
		var uomToId = [];
		for(var i in arrayUomId){
			uomFromId.push(arrayUomId[i].uomFromId);
			uomToId.push(arrayUomId[i].uomToId);
		}
		
		var uniquesUomFromId = [];
		var uniquesUomToId = [];
		
		for(var i=0;i<uomFromId.length;i++){
			var str=uomFromId[i];
	    	if(uniquesUomFromId.indexOf(str)==-1){
	    		uniquesUomFromId.push(str);
	        }
	    }
		
		for(var j=0; j<uomToId.length;j++){
			var str=uomToId[j];
	    	if(uniquesUomToId.indexOf(str) == -1){
	    		uniquesUomToId.push(str);
	        }
	    }
		
		bindingDataToUomId(uniquesUomFromId, uniquesUomToId);
	}

	function bindingDataToUomId(uomFromId, uomToId){
		var uomIdDataBinding = [];
		for(var i in uomFromId){
			uomIdDataBinding.push(uomFromId[i]);
		}
		for(var i in uomToId){
			uomIdDataBinding.push(uomToId[i]);
		}
		bindingDataToUomId2(uomIdDataBinding);
	}

	var uniquesUomId = [];
	function bindingDataToUomId2(uomIdDataBinding){
		uniquesUomId = [];
		for(var i=0;i<uomIdDataBinding.length;i++){
			var str=uomIdDataBinding[i];
	    	if(uniquesUomId.indexOf(str)==-1){
	    		uniquesUomId.push(str);
	        }
	    }
	}
	
	/*function bindingExpiredateByProductIdInLocation(dataSoure){
		$(bindingExpireDate).jqxDropDownList({source: dataSoure, placeHolder: "Please select....", width: '100%', height: '100%', displayMember: 'expireDate', valueMember: 'inventoryItemId'});
	}*/
</script>