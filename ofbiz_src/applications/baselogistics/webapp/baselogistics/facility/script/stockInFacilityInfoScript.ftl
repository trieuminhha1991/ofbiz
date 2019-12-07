<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnumberinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/logresources/js/logisticsCommon.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/globalization/globalize.culture.vi-VN.js"></script>
<script type="text/javascript">
	var orderId = '${parameters.orderId}'; 
	<#assign company = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.get("userLoginId"))/>
	<#assign facilitys = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", userLogin.get("partyId"))), null, null, null, false) />
	
	var facilityData = [
	   	<#if facilitys?exists>
	   		<#list facilitys as item>
	   			{
	   				facilityId: "${item.facilityId?if_exists}",
	   				facilityName: "${StringUtil.wrapString(item.get("facilityName", locale)?if_exists)}"
	   			},
	   		</#list>
	   	</#if>
	];
	
	$("#facilityId").jqxDropDownList({selectedIndex: 0, source: facilityData, displayMember: 'facilityName', valueMember: 'facilityId', width: '200px', theme: "olbius", placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}', dropDownHeight: 250, autoDropDownHeight: true});
</script>
<script>
	$("#jqxNotificationAddSuccess").jqxNotification({ width: "100%", appendContainer: "#contentNotificationAddSuccess", opacity: 0.9, autoClose: true, template: "success" });
	var productData = [];
	var hiddenGl = false;
	var allDone = true;
	$.ajax({
		url: "jqxGeneralServicer?sname=getListDeliveryAndInventoryItemInfo",
		type: "POST",
		data: {
			orderId: orderId
		},
		dataType: "json",
		success: function(data) {
			productData = data.results;
		}
	}).done(function() {
		if (productData.length <= 0){
			allDone = false;
		}
		if (allDone == true){
			for (var i = 0; i < productData.length; i ++){
				if (productData[i].quantityOnHandDiff === null || productData[i].quantityOnHandDiff === undefined){
					allDone = false;
					break;
				} else {
					if (productData[i].quantityOnHandDiff < productData[i].quantityOrderItem){
						allDone = false;
						break;
					}
				}
			}
		}
		if (allDone == true){
			hiddenGl = true;
			$("#addRow").hide();
			$("#addButtonSave").hide();
		} else {
			hiddenGl = false;
			$("#addRow").show();
			$("#addButtonSave").show();
		}
		loadProduct(productData);
	 });
	
	$("#jqxgridOrderInven").on('cellbeginedit', function (event) 
	{
		document.getElementById("addButtonSave").disabled = true;
	});


	$("#jqxgridOrderInven").on('cellendedit', function (event) 
	{
		document.getElementById("addButtonSave").disabled = false;
	});
	
	$("#addButtonSave").click(function () {
		var facilityId = $("#facilityId").val();
		if(facilityId == null || facilityId == "" || facilityId == undefined){
			bootbox.dialog("${uiLabelMap.SelectFacilityToReceive}", 
				[{"label": "${StringUtil.wrapString(uiLabelMap.wgok)}",
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {bootbox.hideAll();}
		        }
	        ]);
		}else{
			var dataRows = $('#jqxgridOrderInven').jqxGrid('getrows');
			var listProducts = [];
			if (dataRows != undefined && dataRows.length > 0){
				for (var i = 0; i < dataRows.length; i ++){
					var data = dataRows[i];
					var map = {};
			   		map['productId'] = data.productId;
			   		if(OlbCore.isNotEmpty(data.datetimeManufactured)){
                        map['datetimeManufactured'] = data.datetimeManufactured.getTime();
                    }else{
                        map['datetimeManufactured'] = null;
                    }
                    if(OlbCore.isNotEmpty(data.datetimeManufactured)){
                        map['expireDate'] = data.expireDate.getTime();
                    }else{
                        map['expireDate'] = null;
                    }
			   		map['quantity'] = data.quantityRecieve;
			   		map['lotId'] = data.lotId;
			   		map['unitPrice'] = data.unitPrice;
			   		map['currencyUom'] = data.currencyUom;
			   		map['fromOrderId'] = data.fromOrderId;
			   		map['fromOrderItemSeqId'] = data.fromOrderItemSeqId;
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
			            	window.location.replace("viewOrder?orderId="+orderId+"&activeTab=stockIn-tab");
			        }
		        }]);
			}
		}
	});
	
	function receiveFacilityDistributer(listProduct, facilityId){
		document.getElementById("addButtonSave").disabled = true;
		$.ajax({
			url: "receiveFacilityDistributer",
			type: "POST",
			data: {
				listProducts: listProduct,
				facilityId: facilityId,
				orderId: orderId
			},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) { 
			var value = data["value"];
			if(value == "success"){
				$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.LogSuccess)}');
				$("#jqxNotificationAddSuccess").jqxNotification('open');
				$('#jqxgridOrderInven').jqxGrid('updatebounddata');
			}
			if(value == "error"){
				$("#notificationAddSuccess").text('${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}');
				$("#jqxNotificationAddSuccess").jqxNotification('open');
			}
			document.getElementById("addButtonSave").disabled = false;
		});
	}
	
	var dataField = [
         { name: 'productId', type: 'string' },
         { name: 'productCode', type: 'string' },
         { name: 'productName', type: 'string' },
         { name: 'datetimeManufactured', type: 'date', other: 'Timestamp' },
         { name: 'expireDate', type: 'date', other: 'Timestamp' },
         { name: 'lotId', type: 'string' },
         { name: 'quantityOrderItem', type: 'number' },
         { name: 'actualExportedQuantity', type: 'number' }, 
         { name: 'quantityOnHandDiff', type: 'number'},
         { name: 'quantityRecieve', type: 'number' }, 
         { name: 'unitPrice', type: 'number' }, 
         { name: 'currencyUom', type: 'string' },
         { name: 'fromOrderId', type: 'string' }, 
         { name: 'fromOrderItemSeqId', type: 'string' }];
	var columnList = [
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
		    groupable: false, draggable: false, resizable: false,
		    datafield: '', columntype: 'number', width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    }
		},		
		{ text: '${uiLabelMap.ProductCode}', dataField: 'productCode', width: 120, editable: true, columntype: 'dropdownlist', pinned: true,
			createeditor: function (row, cellvalue, editor) {
				var codeSourceData = [];
				for (var n = 0; n < productData.length; n ++){
					var prCode = productData[n].productCode;
					var kt = false;
					for (var m = 0; m < codeSourceData.length; m ++){
						if (codeSourceData[m].productCode == prCode){
							kt = true;
							break;
						}
					}
					if (kt == false){
						var map = {};
						map['productCode'] = prCode;
						codeSourceData.push(map);
					}
				}
				var sourcePrCode =
				{
	               localdata: codeSourceData,
	               datatype: 'array'
				};
				var dataAdapterPrCode = new $.jqx.dataAdapter(sourcePrCode);
				editor.off('change');
				editor.jqxDropDownList({source: dataAdapterPrCode, autoDropDownHeight: true, displayMember: 'productCode', valueMember: 'productCode', placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}'
				});
				editor.on('change', function (event){
					var args = event.args;
		     	    if (args) {
	     	    		var item = args.item;
		     		    if (item){
		     		    	StockInObj.updateRowData(item.value);
		     		    } 
		     	    }
		        });
			 },
			 cellbeginedit: function (row, datafield, columntype) {
				 var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
				 if (data.productCode){
					 return false;
				 }
				 return true;
			 },
			 cellsrenderer: function(row, column, value){
				 var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
				 if (!data.productCode){
					 return '<span> ${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)} </span>';
				 }
			 }
		},
		{ text: '${uiLabelMap.ProductName}', dataField: 'productName', width: 250, editable:false, pinned: true,
			cellsrenderer: function(row, column, value){
				var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
				if (!data.productCode){
					return '<span style=\"text-align: right\">...</span>';
				}
			}
		},
		{ text: '${uiLabelMap.ProductManufactureDate}', dataField: 'datetimeManufactured', width: 120, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:true, cellsalign: 'right',
			columntype: 'datetimeinput',
			createeditor: function (row, column, editor) {
				editor.jqxDateTimeInput({width: 150, height: 25, formatString: 'dd/MM/yyyy'});
			},
			cellsrenderer: function(row, column, value){
				var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
				if (value === null || value === undefined || value === ""){
					if (data.productCode){
						return '<span style=\"text-align: right;\">_NA_</span>';
					} else {
						return '<span style=\"text-align: right;\">...</span>';
					}
				}
			 },
			 cellbeginedit: function (row, datafield, columntype) {
				 var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
				 if (data.quantityOnHandDiff >= data.quantityOrderItem){
					 return false;
				 }
				 return true;
			 },
		},
		{ text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', width: 120, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:true, cellsalign: 'right',
			columntype: 'datetimeinput',
			createeditor: function (row, column, editor) {
				editor.jqxDateTimeInput({width: 150, height: 25, formatString: 'dd/MM/yyyy'});
			},
			cellsrenderer: function(row, column, value){
				var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
				if (value === null || value === undefined || value === ""){
					if (data.productCode){
						return '<span style=\"text-align: right;\">_NA_</span>';
					} else {
						return '<span style=\"text-align: right;\">...</span>';
					}
				}
			 },
			 cellbeginedit: function (row, datafield, columntype) {
				 var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
				 if (data.quantityOnHandDiff >= data.quantityOrderItem){
					 return false;
				 }
				 return true;
			 },
		},
		{ text: '${uiLabelMap.QuantityNumberWasEntered}', datafield: 'quantityOnHandDiff', align: 'left', width: 140, editable: false, cellsalign: 'right', filterable: false,
			cellsrenderer: function(row, column, value){
					if (value != undefined && value != null && value != ''){
						return '<span style=\"text-align: right;\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
					}else{
						var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
						if (data.productCode){
							return '<span style=\"text-align: right;\">0</span>';
						} else {
							return '<span style=\"text-align: right;\">...</span>';
						}
					}
		 	},
		},
		{ text: '${uiLabelMap.ListQuantityImported}', datafield: 'quantityRecieve', align: 'left', width: 140, editable: true, cellsalign: 'right', columntype: 'numberinput',  filterable: false, hidden: hiddenGl,
			cellsrenderer: function(row, column, value){
				if(value != null && value != undefined && value != ''){
					return '<span style=\"text-align: right; background-color: #deedf5\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
				} else {
					var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
					if (data.productCode){
						return '<span style=\"text-align: right; background-color: #deedf5\">0</span>';
					} else {
						return '<span style=\"text-align: right;\">...</span>';
					}
				}
		 	},
			validation: function (cell, value) {
				var row = cell.row;
				var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
				var quantityOnHandDiff = data.quantityOnHandDiff;
				var actualExportedQuantity = data.actualExportedQuantity;
				if(value <= 0){
					return {result: false, message: '${uiLabelMap.QuantityMustBeGreateThanZero}'};
				}else{
					if(quantityOnHandDiff == null){
						if(value > actualExportedQuantity){
							return {result: false, message: '${uiLabelMap.NumberEnteredWasIncorrect}'};
						}
					}else{
						var valueInput = value + quantityOnHandDiff; 
						if(valueInput > actualExportedQuantity){
							return {result: false, message: '${uiLabelMap.NumberEnteredWasIncorrect}'};
						}
					}
				}
				return true;
			},
			initeditor: function (row, cellvalue, editor) {
				editor.jqxNumberInput({decimalDigits: 0, digits: 9});
			},
			cellbeginedit: function (row, datafield, columntype) {
				 var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
				 if (data.quantityOnHandDiff >= data.quantityOrderItem){
					 return false;
				 }
				 return true;
			},
		},
		{ text: '${uiLabelMap.RequiredNumber}', datafield: 'quantityOrderItem', align: 'left', width: 150, editable: false, cellsalign: 'right', filterable: false,
			cellsrenderer: function(row, column, value){
				if (!value){
					var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
					if (data.productCode){
						return '<span style=\"text-align: right;\">0</span>';
					} else {
						return '<span style=\"text-align: right;\">...</span>';
					}
				}
			},
		},
		{ text: '${uiLabelMap.ActualExportedQuantity}', datafield: 'actualExportedQuantity', align: 'left', width: 150, editable: false, cellsalign: 'right', filterable: false,
			cellsrenderer: function(row, column, value){
				if (!value){
					var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
					if (data.productCode){
						return '<span style=\"text-align: right;\">0</span>';
					} else {
						return '<span style=\"text-align: right;\">...</span>';
					}
				}
			},
		},
		{ text: '${uiLabelMap.NoNumber}', datafield: 'lotId', align: 'left', width: 100, editable: false, cellsalign: 'left', filterable: true,
			cellsrenderer: function(row, column, value){
				if (!value){
					var data = $('#jqxgridOrderInven').jqxGrid('getrowdata', row);
					if (data.productCode){
						return '<span style=\"text-align: right;\">_NA_</span>';
					} else {
						return '<span style=\"text-align: right;\">...</span>';
					}
				}
			},
		},
      ];

	function loadProduct(valueDataSoure){
		var sourceProduct =
		    {
		        datafields: dataField,
		        localdata: valueDataSoure,
		        datatype: "array",
		    };
		    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
		    $("#jqxgridOrderInven").jqxGrid({
	        source: dataAdapterProduct,
	        filterable: false,
	        showfilterrow: false,
	        theme: 'olbius',
	        rowsheight: 26,
	        width: '97%',
	        height: 320,
	        enabletooltips: true,
	        autoheight: false,
	        pageable: true,
	        pagesize: 10,
	        editable: true,
	        columnsresize: true,
	        localization: getLocalization(),
	        columns: columnList,	
		    });
	}
</script>