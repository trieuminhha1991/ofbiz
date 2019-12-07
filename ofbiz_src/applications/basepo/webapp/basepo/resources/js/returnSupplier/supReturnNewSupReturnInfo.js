$(function() {
	supReturnInfo.init();
});

var supReturnInfo = (function() {
	var validatorVAL;
	var init = function() {
		initSupplierGrid();
		initFacilityGrid($("#destinationFacilityId"));
		initElement();
		initEvent();
		initElementComplex();
		initValidateForm();
	};
	
	var initFacilityGrid = function(grid){
		var url = "jqGetFacilities&isOpening=Y&facilityGroupId=FACILITY_INTERNAL";
		var datafield =  [
			{name: 'facilityId', type: 'string'},
			{name: 'facilityName', type: 'string'},
			{name: 'facilityCode', type: 'string'}
      	];
      	var columnlist = [
				{text: uiLabelMap.FacilityId, datafield: 'facilityCode', width: '20%', pinned: true, classes: 'pointer',
					cellsrenderer: function (row, column, value) {
						if (!value) {
							var data = grid.jqxGrid('getrowdata', row);
							value = data.partyId;
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
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{datafield: 'partyId', hidden: true},
				{text: uiLabelMap.POSupplierId, datafield: 'partyCode', width: '25%', pinned: true,
					cellsrenderer: function (row, column, value) {
						if (!value) {
							var data = $("#jqxgridToParty").jqxGrid('getrowdata', row);
							value = data.partyId;
						}
						return '<div style="cursor: pointer;">' + (value) + '</div>';
				    }
				},
				{text: uiLabelMap.POSupplierName, datafield: 'groupName', width: '75%',
					cellsrenderer: function (row, column, value) {
				        return '<div style="cursor: pointer;">' + (value) + '</div>';
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
      	Grid.initGrid(config, datafield, columnlist, null, $("#jqxgridToParty"));
	};
	
	var initElement = function() {

		$("#supplier").jqxDropDownButton({width: 300}); 
		$('#supplier').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#facilityBtn").jqxDropDownButton({width: 300}); 
		$('#facilityBtn').jqxDropDownButton('setContent', '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\" class=\"green-label\">'+uiLabelMap.PleaseSelectTitle+'</div>');
		
		$("#entryDate").jqxDateTimeInput({
			formatString : "dd/MM/yyyy HH:mm:ss",
			width : 300,
			showFooter : true,
			allowNullDate : false,
			value : null,
			disabled: true,
		});
		$("#entryDate").jqxDateTimeInput('val', new Date());
		$("#currencyUomId").jqxDropDownList({
			source : [],
			disabled : true,
			theme : theme,
			width : 300,
			placeHolder : uiLabelMap.BSClickToChoose,
			autoDropDownHeight : true
		});

		$("#orderHeaderBtn").jqxDropDownButton({
			theme : theme,
			width : 300,
			height : 25
		});
		$("#orderHeaderBtn")
				.jqxDropDownButton(
						"setContent",
						"<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\" class=\"green-label\">"
								+ uiLabelMap.PleaseSelectTitle + "</div>");

		$("#description").jqxInput({
			width : 300,
			height : 75
		});
		initOrderGrid();
	};

	var initOrderGrid = function() {
		var datafieldOrders = [ {
			name : "orderId",
			type : "string"
		}, {
			name : "orderDate",
			type : "date",
			other : "Timestamp"
		} ];
		var columnOrders = [ {
			text : uiLabelMap.SequenceId,
			sortable : false,
			filterable : false,
			editable : false,
			groupable : false,
			draggable : false,
			resizable : false,
			datafield : "",
			columntype : "number",
			width : 50,
			cellsrenderer : function(row, column, value) {
				return "<div style=\"margin:4px;\">" + (value + 1) + "</div>";
			}
		}, {
			text : uiLabelMap.OrderId,
			datafield : "orderId",
			editable : false,
			width : 200
		}, {
			text : uiLabelMap.OrderDate,
			dataField : "orderDate",
			align : "left",
			cellsalign : "left",
			filterable : true,
			minwidth : 200,
			editable : false,
			cellsformat : "dd/MM/yyyy HH:mm:ss",
			filtertype : "range"
		} ];
		var configGridOrder = {
			width : 500,
			rowsheight : 25,
			autoheight : true,
			virtualmode : true,
			showfilterrow : false,
			selectionmode : "checkbox",
			pageable : true,
			sortable : false,
			filterable : true,
			editable : false,
			url : "",
			showtoolbar : false,
			source : {
				pagesize : 5,
				id : "orderId"
			}
		};
		Grid.initGrid(configGridOrder, datafieldOrders, columnOrders, null,
				$("#orderHeaderGrid"));
	};

	var initElementComplex = function() {

	};

	var initEvent = function() {
		$("#destinationFacilityId").on('rowselect', function (event) {
	        var args = event.args;
	        var rowData = args.row;
	        facilitySelected = rowData;
	        var description = uiLabelMap.PleaseSelectTitle; 
	        if (rowData.facilityCode) {
	        	description = rowData.facilityCode + ' - ' + rowData.facilityName;
	        } else {
	        	description = rowData.facilityId + ' - ' + rowData.facilityName;
	        }
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\" class=\"green-label\">'+ description +' </div>';
	        $('#facilityBtn').jqxDropDownButton('setContent', dropDownContent);
	        $('#facilityBtn').jqxDropDownButton('close');
	    });
		
		$("#jqxgridToParty").on('rowclick', function (event) {
	        var args = event.args;
	        var boundIndex = args.rowindex;
			var data = $("#jqxgridToParty").jqxGrid('getrowdata', boundIndex);
			$('#supplier').jqxDropDownButton('close');
	        var desc = null;
	        if (data){
	        	var partyId = data.partyId;
	        	$("#toPartyId").val(partyId);
	        	updateCurrencyUomId(partyId);
                desc = data.groupName;
	        } else {
				desc = uiLabelMap.PleaseSelectTitle;
			}
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ desc+'</div>';
	        $('#supplier').jqxDropDownButton('setContent', dropDownContent);
	    });

        $('#currencyUomId').on('select', function (event)
        {
            var args = event.args;
            var supplier = $("#toPartyId").val();
            if (args) {
                var item = args.item;
                var value = item.value;
                var orderSrc = $("#orderHeaderGrid").jqxGrid("source");
                if (orderSrc._source) {
                    orderSrc._source.url = "jqxGeneralServicer?sname=JQGetListPOBySupplier&supplierId=" + supplier + "&currencyUomId=" + value;
                    $("#orderHeaderGrid").jqxGrid("source", orderSrc);
                }
            }
        });
		
		$("#orderHeaderGrid").on("rowunselect", function(event) {
			var args = event.args;
			if (args.rowindex instanceof Array || args.rowindex < 0) {
				listOrderIds = [];
				updateContentButton(listOrderIds);
			} else {
				var rowBoundIndex = args.rowindex;
				var rowData = args.row;
				if (rowData) {
					var orderId = rowData.orderId;
					$.each(listOrderIds, function(i) {
						var olb = listOrderIds[i];
						if (olb == orderId) {
							listOrderIds.splice(i, 1);
						}
					});
					updateContentButton(listOrderIds);
				}
			}
			updateGridProduct(listOrderIds);
            SupReturnProductPromoObj.updateGridProductPromo(listOrderIds);
		});

		$("#orderHeaderGrid").on("rowselect", function(event) {
			var args = event.args;
			if (args.rowindex instanceof Array) {
				listOrderIds = [];
				for (var i = 0; i < args.rowindex.length; i++) {
					var allItems = $("#orderHeaderGrid").jqxGrid("getrows")
					for (var j = 0; j < allItems.length; j++) {
						var rowData = allItems[j];
						if (rowData && rowData != window) {
							var orderId = rowData.orderId;
							listOrderIds.push(orderId);
							updateContentButton(listOrderIds);
						}
					}
				}
			} else {
				var rowBoundIndex = args.rowindex;
				var rowData = args.row;
				if (rowData) {
					var orderId = rowData.orderId;
					listOrderIds.push(orderId);
					updateContentButton(listOrderIds);
				}
			}
			updateGridProduct(listOrderIds);
            SupReturnProductPromoObj.updateGridProductPromo(listOrderIds);
        });
	};

	var updateContentButton = function(orderIds) {
		if (orderIds.length > 0) {
			if (orderIds.length <= 5) {
				var strId = orderIds[0];
				for (var i = 1; i < orderIds.length; i++) {
					strId = strId + ", " + orderIds[i];
				}
				$("#orderHeaderBtn")
						.jqxDropDownButton(
								"setContent",
								"<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\" class=\"green-label\">"
										+ strId + "</div>");
			} else {
				var strId = orderIds[0];
				for (var i = 1; i < 5; i++) {
					strId = strId + ", " + orderIds[i];
				}
				$("#orderHeaderBtn")
						.jqxDropDownButton(
								"setContent",
								"<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\" class=\"green-label\">"
										+ strId + ", ...</div>");
			}
		} else {
			$("#orderHeaderBtn").jqxDropDownButton(
					"setContent",
					"<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">"
							+ uiLabelMap.PleaseSelectTitle + "</div>");
		}
	};

	var updateUomBySupplier = function(partyId) {
		var data = [];
		for (var i = 0; i < supplierAndCurrencyData.length; i++) {
			var value = {currencyUomId: supplierAndCurrencyData[i].currencyUomId, abbreviation: supplierAndCurrencyData[i].abbreviation};
			if (supplierAndCurrencyData[i].partyId == partyId && _.findIndex(data, value) == -1) {
				data.push(value);
			}
		}
		$("#currencyUomId").jqxDropDownList({
			source : data,
			theme : theme,
			selectedIndex : 0,
			displayMember : "abbreviation",
			valueMember : "currencyUomId",
			disabled : false,
			autoDropDownHeight : true
		});
	};

	var updateGridProduct = function(listOrderIds) {
		var listObj = [];
		for (var i = 0; i < listOrderIds.length; i++) {
			var row = {};
			row["orderId"] = listOrderIds[i];
			listObj.push(row);
		}
		listObj = JSON.stringify(listObj);
		var listOrderItems = [];
		$.ajax({
			type : "POST",
			url : "getOrderItemsByOrdersToReturn",
			data : {
				"listOrderIds" : listObj
			},
			dataType : "json",
			async : false,
			success : function(response) {
				listOrderItems = response.listOrderItems;
			},
			error : function(response) {
				alert("Error:" + response);
			}
		}).done(function() {
			SupReturnProductObj.loadProduct(listOrderItems);
		});
	};

    var getListOrderIds = function() {
        return listOrderIds;
    }
    var initValidateForm = function() {
		var mapRules = [ {
			input : "#currencyUomId",
			type : "validInputNotNull"
		}, {
			input : "#orderHeaderBtn",
			type : "validInputNotNull"
		}, {
			input : "#facilityBtn",
			type : "validInputNotNull"
		} ];
		var extendRules = [
				{
					input : "#orderHeaderBtn",
					message : uiLabelMap.FieldRequired,
					rule : function(input, commit) {
						var allSelectedItems = $("#orderHeaderGrid").jqxGrid(
								"getselectedrowindexes");
						if (allSelectedItems.length <= 0)
							return false;
						return true;
					}
				}, 
				{
					input : "#facilityBtn",
					message : uiLabelMap.FieldRequired,
					rule : function(input, commit) {
						if (!facilitySelected)
							return false;
						return true;
					}
				}, 
				{
					input : "#supplier",
					message : uiLabelMap.FieldRequired,
					action : "valueChanged",
					rule : function(input, commit) {
						var list = $("#jqxgridToParty").jqxGrid("getselectedrowindexes");
						if (!list || list.length <= 0) return false;
						return true;
					}
				}
				];
		validatorVAL = new OlbValidator($("#newReturnSupplier"), mapRules,
				extendRules, {
					position : "right"
				});
	};
	var getValidator = function() {
		return validatorVAL;
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
			$("#currencyUomId").jqxDropDownList({
				source : currencyCombo,
				theme : theme,
				selectedIndex : 0,
				displayMember : "description",
				valueMember : "currencyUomId",
				disabled : false,
				autoDropDownHeight : true
			});
            var currencyUomId = $("#currencyUomId").jqxDropDownList("val");
            var orderSrc = $("#orderHeaderGrid").jqxGrid("source");
            if (orderSrc._source) {
                orderSrc._source.url = "jqxGeneralServicer?sname=JQGetListPOBySupplier&supplierId=" + partyId + "&currencyUomId=" + currencyUomId;
                $("#orderHeaderGrid").jqxGrid("source", orderSrc);
            }
        });
	};
	return {
		init : init,
		getValidator : getValidator,
        getListOrderIds: getListOrderIds
	};
}());