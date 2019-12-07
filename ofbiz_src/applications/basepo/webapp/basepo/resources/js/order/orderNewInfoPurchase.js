$(function() {
	OlbOrderInfo.init();
});

var OlbOrderInfo = (function() {
	var validatorVAL;
	var contactMechDDB;
	var curDate = new Date();
	var DD = curDate.getDate();
	var MM = curDate.getMonth();
	var YY = curDate.getFullYear();
	
	var init = function() {
		initElementComplex();
		initSupplierGrid();
		initFacilityGrid($("#jqxgridFacility"));
		initElements();
		initEvents();
		initValidateForm();
		initOrderItemReceivedGrid();
	};
	
	var initSupplierGrid = function(){
		var url = "jqGetListPartySupplier";
		var datafield =  [
			{name: 'partyId', type: 'string'},
			{name: 'partyCode', type: 'string'},
			{name: 'groupName', type: 'string'},
      	];
      	var columnlist = [
              { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;cursor:pointer;>' + (value + 1) + '</div>';
				    }
				},
				{datafield: 'partyId', hidden: true}, 
				{text: uiLabelMap.POSupplierId, datafield: 'partyCode', width: '25%', pinned: true, classes: 'pointer',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							var data = $("#jqxgridSupplier").jqxGrid('getrowdata', row);
							value = data.partyId;
						}
				        return '<div style=margin:4px;cursor:pointer;>' + (value) + '</div>';
				    }
				},
				{text: uiLabelMap.POSupplierName, datafield: 'groupName', width: '75%',
					cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;cursor:pointer;>' + (value) + '</div>';
				    }
				},
      	];
      	
      	var config = {
  			width: 500, 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        useUrl: true,
	        url: url,                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, $("#jqxgridSupplier"));
	};
	
	var initOrderItemReceivedGrid = function(){
		var url = "jqGetOrderItemReceived&orderId=" + orderId;
		var grid = $("#jqxgridOrderItemReceived");
		var datafield =  [
		            { name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'quantity', type: 'number' },
					{ name: 'alternativeQuantity', type: 'number' },
					{ name: 'quantityReceived', type: 'number' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'quantityUomIds', type: 'string' },
					{ name: 'unitPrice', type: 'number' },
					{ name: 'alternativeUnitPrice', type: 'number' },
					{ name: 'itemSubTotal', type: 'number' },
					{ name: 'productWeight', type: 'number' },
					{ name: 'convertPacking', type: 'number' },
					{ name: 'weightUomId', type: 'string' },
					{ name: 'purchaseUomId', type: 'string' },
					{ name: 'weightUomIds', type: 'string' },
					{ name: 'requireAmount', type: 'string' },
					{ name: 'currencyUomId', type: 'string' },
					{ name: 'itemComment', type: 'string' },
					{ name: 'productName', type: 'string' },
					];
		var columnlist = [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
				groupable: false, draggable: false, resizable: false,
				datafield: '', columntype: 'number', width: 50,
				cellsrenderer: function (row, column, value) {
					return '<div style=margin:4px;>' + (value + 1) + '</div>';
				}
			},
			{ text: uiLabelMap.BPOProductId, datafield: 'productCode', width: 100, editable: false, pinned: true},
			{ text: uiLabelMap.BPOProductName, datafield: 'productName', minwidth: 150, editable: false, pinned: true},
			{text: uiLabelMap.BLPackingForm, sortable: false, dataField: 'convertPacking', editable: false, width: 90, filterable:false,
				cellsrenderer: function(row, column, value) {
					return '<span class="align-right">' + formatnumber(value) +'</span>';
				}, 
			},
			{text: uiLabelMap.BSPurchaseUomId, sortable: false, dataField: 'quantityUomId', width: 100, filterable:false,
				cellsrenderer: function(row, column, value) {
					if (value) {
						var desc = getUomDesc(value);
						return '<span class="align-right">' + desc +'</span>';
					} 
					return value;
				}, 
			},
			{ text: uiLabelMap.BLPurchaseQtySum, datafield: 'alternativeQuantity', sortable: false,  width: '100', editable: true, filterable: false, sortable: false, 
				rendered: function (element) {
			      	$(element).jqxTooltip({ position: 'mouse', content: uiLabelMap.ByPurchaseQuantityUom});
			  	},
				cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value) {
					return '<span class="align-right">' + formatnumber(value) +'</span>';
				}, 
			},
			{text: uiLabelMap.BLReceivedNumberSum, datafield: 'quantityReceived', width: 100, editable: false, sortable: false, filterable: false,
				cellsalign: 'right', columntype: 'numberinput', sortable: false, hidden: false,
				cellsrenderer: function(row, column, value){
					var data = grid.jqxGrid('getrowdata', row);
					value = data.quantityReceived/data.convertPacking;
					return '<span class="align-right">' + formatnumber(value) +'</span>';
				}
			},
			{text: uiLabelMap.BLQuantityEATotal, datafield: 'quantity', width: 100, editable: false, sortable: false, filterable: false,
				cellsalign: 'right', columntype: 'numberinput', sortable: false, hidden: false,
				cellsrenderer: function(row, column, value){
					var data = grid.jqxGrid('getrowdata', row);
					value = data.quantityReceived;
					return '<span class="align-right">' + formatnumber(value) +'</span>';
				}
			},
			{ text: uiLabelMap.UnitPrice, datafield: 'alternativeUnitPrice', sortable: false, width: 100, editable: true, filterable: false, cellsalign: 'right', columntype: 'numberinput',
				cellsrenderer: function(row, column, value){
					if (value) {
						return '<span class="align-right" title=\"'+formatnumber(value, null, 3)+'\">' + formatnumber(value, null, 3) +'</span>';
					}
				},
			},
			{ text: uiLabelMap.BPOTotal, datafield: 'itemSubTotal', sortable: false, width: 120, editable: false, filterable: false, cellsalign: 'right', sortable: false,
				cellsrenderer: function(row, column, value){
					var data = grid.jqxGrid('getrowdata', row);
					value = data.quantityReceived/data.convertPacking*data.alternativeUnitPrice;
					return '<span class="align-right">' + formatnumber(value) +'</span>';
				},
			},
			{ text: uiLabelMap.Note, datafield: 'itemComment', sortable: false, width: 100, editable: true, filterable: false, cellsalign: 'left', sortable: false,
				cellsrenderer: function(row, column, value) {
					if (value) {
						return '<span>' + value +'</span>';
					} 
					return value;
				}, 
			},
      ];
		
		var config = {
				width: '100%', 
				virtualmode: true,
				showtoolbar: false,
				selectionmode: 'singlerow',
				pageable: true,
				sortable: true,
				filterable: true,	        
				editable: false,
				rowsheight: 26,
				useUrl: true,
				url: url,                
				source: {pagesize: 10}
		};
		Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	
	var initElements = function() {
		$("#facility").jqxDropDownButton({width: 350, theme: theme});
		$('#facility').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
//		if (typeof orderId != 'undefined' && orderId != null && orderId != undefined) {
//			$('#facility').jqxDropDownButton({disabled: true});
//		} else {
//			$('#facility').jqxDropDownButton({disabled: false});
//		}
//		
		$("#supplier").jqxDropDownButton({width: 350, theme: theme});
		var descTmp = uiLabelMap.PleaseSelectTitle;
		if (defaultSupplierName != null) {
			descTmp = defaultSupplierName;
		} 
		$('#supplier').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+descTmp+'</div>');
		if (typeof orderId != 'undefined' && orderId != null && orderId != undefined) {
			$('#supplier').jqxDropDownButton({disabled: true});
		} else {
			$('#supplier').jqxDropDownButton({disabled: false});
		}
		
		$("#shipBeforeDate").jqxDateTimeInput({
			formatString : "dd/MM/yyyy HH:mm",
			width : 250,
			showFooter : true,
			allowNullDate : true,
			theme : theme,
			value : null,
			dropDownHorizontalAlignment : "left"
		});
		$("#shipAfterDate").jqxDateTimeInput({
			formatString : "dd/MM/yyyy HH:mm",
			width : 250,
			showFooter : true,
			allowNullDate : true,
			theme : theme,
			value : null,
			dropDownHorizontalAlignment : "left"
		});
		$("#currencyUomId").jqxDropDownList({
			source : [],
			disabled : true,
			theme : theme,
			width : 250,
			placeHolder : uiLabelMap.BSClickToChoose,
			autoDropDownHeight : true,
			displayMember : "description",
			valueMember : "currencyUomId"
		});
		
		$("#originFacilityId").jqxDropDownList({
			source : facilityData,
			theme : theme,
			width : 300,
			displayMember : "description",
			valueMember : "facilityId",
			disabled : false,
			placeHolder : uiLabelMap.BSClickToChoose,
		});
		if (defaultShipAfterDate != null){
			$("#shipAfterDate").jqxDateTimeInput("setDate", defaultShipAfterDate);
		}
			
		if (defaultShipBeforeDate != null)
			$("#shipBeforeDate").jqxDateTimeInput("setDate", defaultShipBeforeDate);
		if (defaultFacilityId != null) {
			$("#originFacilityId").val(defaultFacilityId);
			if (facilitySelected != null){
				if (facilitySelected) {
		        	if (facilitySelected.facilityCode != null){
		        		description = '['+ facilitySelected.facilityCode +'] ' + facilitySelected.facilityName;
		        	} else {
		        		description = '['+ facilitySelected.facilityId +'] ' + facilitySelected.facilityName;
		        	}
		        }
		        
				$("#shippingContactMechGrid").jqxGrid("source")._source.url = "jqxGeneralServicer?sname=JQGetListContactMechByFacility&facilityId=" + facilitySelected.facilityId;
				$("#shippingContactMechGrid").jqxGrid("updatebounddata");
				
		        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
		        $('#facility').jqxDropDownButton('setContent', dropDownContent);
			}
		}
		
		if (supplierSelected != null){
			var description = "";
        	if (supplierSelected.partyCode != null){
        		description = '['+ supplierSelected.partyCode +'] ' + supplierSelected.groupName;
        	} else {
        		description = '['+ supplierSelected.partyId +'] ' + supplierSelected.groupName;
        	}
	        
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#supplier').jqxDropDownButton('setContent', dropDownContent);
		}
		
		if (defaultSupplierId != null){
			$("#supplierId").val(defaultSupplierId);
			updateCurrencyUomId(defaultSupplierId);
			if (defaultCurrencyUomId != null && $("#currencyUomId").length > 0){
				$("#currencyUomId").jqxDropDownList('val', defaultCurrencyUomId);
			}
		}
	};

	var initElementComplex = function() {
		var configContactMech = {
			useUrl : true,
			root : "results",
			widthButton : 300,
			showdefaultloadelement : false,
			autoshowloadelement : false,
			dropDownHorizontalAlignment : "right",
			datafields : [ {
				name : "contactMechId",
				type : "string"
			}, {
				name : "toName",
				type : "string"
			}, {
				name : "attnName",
				type : "string"
			}, {
				name : "address1",
				type : "string"
			}, {
				name : "city",
				type : "string"
			}, {
				name : "stateProvinceGeoId",
				type : "string"
			}, {
				name : "stateProvinceGeoName",
				type : "string"
			}, {
				name : "postalCode",
				type : "string"
			}, {
				name : "countryGeoId",
				type : "string"
			}, {
				name : "countryGeoName",
				type : "string"
			}, {
				name : "districtGeoId",
				type : "string"
			}, {
				name : "districtGeoName",
				type : "string"
			}, {
				name : "wardGeoId",
				type : "string"
			}, {
				name : "wardGeoName",
				type : "string"
			} ],
			columns : [ {
				text : uiLabelMap.BSContactMechId,
				datafield : "contactMechId",
				width : "100px"
			}, {
				text : uiLabelMap.BSReceiverName,
				datafield : "toName",
				width : "140px",
				hidden: true,
			}, {
				text : uiLabelMap.BSOtherInfo,
				datafield : "attnName",
				width : "140px",
				hidden: true,
			}, {
				text : uiLabelMap.BSAddress,
				datafield : "address1",
				width : "25%"
			}, {
				text : uiLabelMap.BSWard,
				datafield : "wardGeoName",
				width : "20%"
			}, {
				text : uiLabelMap.BSCounty,
				datafield : "districtGeoName",
				width : "120px"
			}, {
				text : uiLabelMap.BSStateProvince,
				datafield : "stateProvinceGeoName",
				width : "100px"
			}, {
				text : uiLabelMap.BSCountry,
				datafield : "countryGeoName",
				width : "100px"
			} ],
			url : "",
			useUtilFunc : true,
			key : "contactMechId",
			description : [ "address1", "city" ],
			autoCloseDropDown : true,
			filterable : false,
			sortable : false,
		};
		contactMechDDB = new OlbDropDownButton($("#shippingContactMechId"),
				$("#shippingContactMechGrid"), null, configContactMech, null);
	};
	
	function getProductOrderMap(supplierId){
		$.ajax({
			url: "getProductOrderMap",
			type: "POST",
			data: {supplierId : supplierId
				},
			dataType: "json",
			async: false,
			success : function(data) {
			}
		});
		return productOrderMap;
	}
	
	var initEvents = function() {
		$("#currencyUomId").on('change', function (event) {
			if (supplierSelected && facilitySelected) {
				var facilityId = $("#originFacilityId").jqxDropDownList('val');
				var currencyUomId = $("#currencyUomId").jqxDropDownList('val');
				if (orderId != null && orderId != undefined) {
					listOrderItemData = getOrderItemToUpdate(orderId, currencyUomId, partyId, facilityId);
					
					OlbGridUtil.updateSource($("#jqxgridProduct"), null, listOrderItemData, null);
					$("#jqxgridProduct").jqxGrid({virtualmode: false});
					
				} else {
					if (facilitySelected != null){
						facilityId = facilitySelected.facilityId;
						var url = null;
						if (productPlanId != null && customTimePeriodId != null){
							url = "jqxGeneralServicer?sname=JQListProductBySupplier&hasrequest=Y&supplierId="
								+ supplierSelected.partyId + "&currencyUomId=" + currencyUomId + "&facilityId=" + facilityId
								+ "&productPlanId=" + productPlanId + "&customTimePeriodId=" + customTimePeriodId;
						} else {
							url = "jqxGeneralServicer?sname=JQListProductBySupplier&hasrequest=Y&supplierId="
								+ supplierSelected.partyId + "&currencyUomId=" + currencyUomId + "&facilityId=" + facilityId;
						}
						$("#jqxgridProduct").jqxGrid("source")._source.url = url;
						$("#jqxgridProduct").jqxGrid("updatebounddata");
					}
				}
			}
		});
		
		$("#jqxgridFacility").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        facilitySelected = $.extend({}, rowData);
	        var description = uiLabelMap.PleaseSelectTitle; 
	        if (facilitySelected) {
	        	if (facilitySelected.facilityCode != null){
	        		description = '['+ facilitySelected.facilityCode +'] ' + facilitySelected.facilityName;
	        	} else {
	        		description = '['+ facilitySelected.facilityId +'] ' + facilitySelected.facilityName;
	        	}
	        }
	        
			$("#shippingContactMechGrid").jqxGrid("source")._source.url = "jqxGeneralServicer?sname=JQGetListContactMechByFacility&facilityId=" + facilitySelected.facilityId;
			$("#shippingContactMechGrid").jqxGrid("updatebounddata");
			
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ description +' </div>';
	        $('#facility').jqxDropDownButton('setContent', dropDownContent);
	    });
		
		$('#jqxgridFacility').on('rowdoubleclick', function (event) { 
			$('#facility').jqxDropDownButton('close');
		});
		
		$("#jqxgridFacility").on('bindingcomplete', function (event) {
			if (facilitySelected != null){
				var rows = $('#jqxgridFacility').jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						var index = $('#jqxgridFacility').jqxGrid('getrowboundindexbyid', data1.uid);
						if (data1.facilityId == facilitySelected.facilityId){
							$('#jqxgridFacility').jqxGrid('selectrow', index);
						} else {
							$('#jqxgridFacility').jqxGrid('unselectrow', index);
						}
					}
				}
			}
		});
		
		$("#jqxgridSupplier").on('bindingcomplete', function (event) {
			if (supplierSelected != null){
				var rows = $('#jqxgridSupplier').jqxGrid('getrows');
				if (rows.length > 0){
					for (var i in rows){
						var data1 = rows[i];
						var index = $('#jqxgridSupplier').jqxGrid('getrowboundindexbyid', data1.uid);
						if (data1.partyId == supplierSelected.partyId){
							$('#jqxgridSupplier').jqxGrid('selectrow', index);
						} else {
							$('#jqxgridSupplier').jqxGrid('unselectrow', index);
						}
					}
				}
			}
		});
		
		$("#jqxgridSupplier").on('rowselect', function (event) {
			
	        var args = event.args;
	        var boundIndex = args.rowindex;
			var data = $("#jqxgridSupplier").jqxGrid('getrowdata', boundIndex);
			$("#jqxgridProduct").jqxGrid('clear');
	        var desc = null;
	        if (data){
	        	supplierSelected = $.extend({}, data);
	        	var partyId = supplierSelected.partyId;
	        	$("#supplierId").val(partyId);
        		updateCurrencyUomId(partyId);
        		if (supplierSelected.partyCode != null){
        			desc = "[" + supplierSelected.partyCode + "] " + supplierSelected.groupName;
        		} else {
        			desc = "[" + supplierSelected.partyId + "] " + supplierSelected.groupName;
        		}
	        } else {
				desc = uiLabelMap.PleaseSelectTitle;
			}
	        $('#supplier').jqxDropDownButton('close');
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ desc+'</div>';
	        $('#supplier').jqxDropDownButton('setContent', dropDownContent);
	    });

	};

	var updateCurrencyUomId = function(partyId) {
		$.ajax({
			url : "getSupplierCurrencyUom",
			type : "POST",
			data : {
				partyId : partyId,
			},
			dataType : "json",
			success : function(data) {
				
			}
		}).done(function(data) {
			var listCurrencyUoms = data.listCurrencyUoms;
			var currencyCombo = [];
			if (listCurrencyUoms != undefined && listCurrencyUoms.length > 0) {
				for (var i = 0; i < listCurrencyUoms.length; i ++) {
					var x = {};
					x.currencyUomId = listCurrencyUoms[i].uomId;
					x.description = listCurrencyUoms[i].abbreviation;
					currencyCombo.push(x);
				}
			}
			$("#currencyUomId").jqxDropDownList({ source : currencyCombo, disabled : false });
			$("#currencyUomId").jqxDropDownList('selectIndex', 0);
			var facilityId = $("#originFacilityId").jqxDropDownList('val');
			if (typeof orderId != 'undefined' && orderId != null && orderId != undefined) {
				$('#currencyUomId').jqxDropDownList({disabled: true});
			} else {
				$('#currencyUomId').jqxDropDownList({disabled: false});
			}
			
			if (currencyCombo.length > 0){
				if (orderId != null && orderId != undefined) {
					listOrderItemData = getOrderItemToUpdate(orderId, currencyCombo[0].currencyUomId, partyId, facilityId);
					
					OlbGridUtil.updateSource($("#jqxgridProduct"), null, listOrderItemData, null);
					$("#jqxgridProduct").jqxGrid({virtualmode: false});
					
				} else {
					if (facilitySelected != null){
						facilityId = facilitySelected.facilityId;
						var url = null;
						if (productPlanId != null && customTimePeriodId != null){
							url = "jqxGeneralServicer?sname=JQListProductBySupplier&hasrequest=Y&supplierId=" 
								+ partyId + "&currencyUomId=" + currencyCombo[0].currencyUomId + "&facilityId=" + facilityId
								+ "&productPlanId=" + productPlanId + "&customTimePeriodId=" + customTimePeriodId;
						} else {
							url = "jqxGeneralServicer?sname=JQListProductBySupplier&hasrequest=Y&supplierId=" 
								+ partyId + "&currencyUomId=" + currencyCombo[0].currencyUomId + "&facilityId=" + facilityId;
						}
						$("#jqxgridProduct").jqxGrid("source")._source.url = url;
						$("#jqxgridProduct").jqxGrid("updatebounddata");
					}
				}
			}
		});
	};

	var initValidateForm = function() {
		var extendRules = [ {
				input : "#shipAfterDate",
				message : uiLabelMap.BSStartDateMustLessThanOrEqualFinishDate,
				action : "valueChanged",
				rule : function(input, commit) {
					return OlbValidatorUtil.validElement(input, commit,
							"validCompareStartDateAndFinishDate");
				}
			},
			{
				input : "#supplier",
				message : uiLabelMap.FieldRequired,
				action : "valueChanged",
				rule : function(input, commit) {
					if (!orderId){
						if (supplierSelected == null || supplierSelected == undefined) return false;
					}
					return true;
				}
			},
			{
				input : "#facility",
				message : uiLabelMap.FieldRequired,
				action : "valueChanged",
				rule : function(input, commit) {
					if (facilitySelected == null || facilitySelected == undefined) return false;
					return true;
				}
			},
		];
		var mapRules = [ 
		{
			input : "#currencyUomId",
			type : "validInputNotNull"
		}, {
			input : "#shipAfterDate",
			type : "validDateTimeInputNotNull"
		}, {
			input : "#shipBeforeDate",
			type : "validDateTimeInputNotNull"
		}, {
			input : "#shipBeforeDate",
			type : "validDateTimeCompareToday"
		}, ];
		if (!orderId) {
			mapRules.push({
				input : "#shipAfterDate",
				type : "validDateTimeCompareToday"
			})
		}
		validatorVAL = new OlbValidator($("#initPurchaseOrderEntry"), mapRules,
				extendRules, {
					position : "right"
				});
	};

	var getValidator = function() {
		return validatorVAL;
	};

	var getContactMechDDB = function() {
		return contactMechDDB;
	};
	
	var getOrderItemToUpdate = function (orderId, currencyUomId, supplierId, facilityId){
 		var listProducts = [];
 		$.ajax({
 			url: "getOrderItemToUpdate",
 			type: "POST",
 			data: {
 				orderId : orderId,
 				supplierId: supplierId,
 				currencyUomId: currencyUomId,
 				facilityId: facilityId,
 			},
 			dataType: "json",
 			async: false,
 			success : function(data) {
 				listProducts = data.listProducts;
 			}
 		});
 		return listProducts;
 	};
	var initFacilityGrid = function(grid){
		var url = "jqGetFacilities&primaryFacilityGroupId=FACILITY_INTERNAL";
		var datafield =  [
			{name: 'facilityId', type: 'string'},
			{name: 'facilityCode', type: 'string'},
			{name: 'facilityName', type: 'string'},
      	];
      	var columnlist = [
				{text: uiLabelMap.BLFacilityId, datafield: 'facilityCode', width: '20%', pinned: true, classes: 'pointer',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							var data = grid.jqxGrid('getrowdata', row);
							value = data.facilityId;
						}
				        return '<div style="cursor:pointer;">' + (value) + '</div>';
				    }
				},
				{text: uiLabelMap.FacilityName, datafield: 'facilityName', width: '80%',
					cellsrenderer: function (row, column, value) {
				        return '<div style="cursor:pointer;">' + (value) + '</div>';
				    }
				},
      	];
      	
      	var config = {
  			width: 500, 
	   		virtualmode: true,
	   		showtoolbar: false,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: true,
	        filterable: true,	        
	        editable: false,
	        rowsheight: 26,
	        useUrl: true,
	        url: url,                
	        source: {pagesize: 10}
      	};
      	Grid.initGrid(config, datafield, columnlist, null, grid);
	};
	return {
		init : init,
		getValidator : getValidator,
		getContactMechDDB : getContactMechDDB,
	}
}());