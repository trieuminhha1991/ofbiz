$(function(){
	OlbSettingProductStoreNewShipmentCost.init();
});

var OlbSettingProductStoreNewShipmentCost = (function(){
	var initWindow = (function(){
		$('#alterpopupWindow').jqxWindow({ width: 600, height : 300, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel1"), modalOpacity: 0.7, title: addNew7});
	});

	var initInput = (function(){
		jOlbUtil.input.create("#shipmentCostEstimateAdd", {width:'95%',height:24});
		$("#orderFlatPriceAdd").jqxNumberInput({width: '96.6%', height: 28, spinButtons: false, inputMode: 'simple', decimalDigits: 0 });
		$("#orderPricePercentAdd").jqxNumberInput({width: '96.6%', height: 28, spinButtons: false, inputMode: 'simple', decimalDigits: 0 });
		$("#orderItemFlatPriceAdd").jqxNumberInput({width: '96.6%', height: 28, spinButtons: false, inputMode: 'simple', decimalDigits: 0 });
	});
	
	var shipmentMethodTypeId = null;
	var carrierPartyId = null;
	var carrierRoleTypeId = null;
	
	var initDropDownButton = (function(){
		var sourcePartyFrom = {
			datafields:[{name: 'shipmentMethodTypeId', type: 'string'},
				   		{name: 'partyId', type: 'string'},
				   		{name: 'roleTypeId', type: 'string'},
		    ],
			cache: false,
			root: 'results',
			datatype: "json",
			updaterow: function (rowid, rowdata) {
				// synchronize with the server - send update command   
			},
			beforeprocessing: function (data) {
			    sourcePartyFrom.totalrecords = data.TotalRows;
			},
			filter: function () {
			   	// update the grid and send a request to the server.
			   	$("#carrierShipmentMethodGrid").jqxGrid('updatebounddata');
			},
			pager: function (pagenum, pagesize, oldpagenum) {
			  	// callback called when a page or page size is changed.
			},
			sort: function () {
			  	$("#carrierShipmentMethodGrid").jqxGrid('updatebounddata');
			},
			sortcolumn: 'shipmentMethodTypeId',
           	sortdirection: 'asc',
			type: 'POST',
			data: {
				noConditionFind: 'Y',
				conditionsFind: 'N',
			},
			pagesize:5,
			contentType: 'application/x-www-form-urlencoded',
			url: 'jqxGeneralServicer?sname=JQGetListCarrierShipment',
		};
		
		var dataAdapterPF = new $.jqx.dataAdapter(sourcePartyFrom,
	    {
	    	autoBind: true,
	    	formatData: function (data) {
	    		if (data.filterscount) {
	                var filterListFields = "";
	                for (var i = 0; i < data.filterscount; i++) {
	                    var filterValue = data["filtervalue" + i];
	                    var filterCondition = data["filtercondition" + i];
	                    var filterDataField = data["filterdatafield" + i];
	                    var filterOperator = data["filteroperator" + i];
	                    filterListFields += "|OLBIUS|" + filterDataField;
	                    filterListFields += "|SUIBLO|" + filterValue;
	                    filterListFields += "|SUIBLO|" + filterCondition;
	                    filterListFields += "|SUIBLO|" + filterOperator;
	                }
	                data.filterListFields = filterListFields;
	            }
	            return data;
	        },
	        loadError: function (xhr, status, error) {
	            alert(error);
	        },
	        downloadComplete: function (data, status, xhr) {
	                if (!sourcePartyFrom.totalRecords) {
	                    sourcePartyFrom.totalRecords = parseInt(data['odata.count']);
	                }
	        }
	    });	
		$('#carrierShipmentAdd').jqxDropDownButton({ width: '96.6%', height: 30, dropDownHorizontalAlignment: 'left'});
		$("#carrierShipmentMethodGrid").jqxGrid({
			width:600,
			source: dataAdapterPF,
			filterable: true,
			virtualmode: true, 
			sortable:true,
			editable: false,
			autoheight:true,
			pageable: true,
			showfilterrow: true,
			rendergridrows: function(obj) {	
				return obj.data;
			},
			columns:[{text: shipmentMethodTypeLabel, datafield: 'shipmentMethodTypeId', width: 200},
						{text: carrierPartyLabel, datafield: 'partyId', width: 200, },
						{text: carrierRoleTypeLabel, datafield: 'roleTypeId', width: 200},
					]
		});
		  
		$("#carrierShipmentMethodGrid").on('rowselect', function (event) {
	        var args = event.args;
	        var row = $("#carrierShipmentMethodGrid").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['shipmentMethodTypeId'] + "/" + row['partyId'] + "/" + row['roleTypeId'] + '</div>';
	        $("#carrierShipmentAdd").jqxDropDownButton('setContent', dropDownContent);
	        
	        shipmentMethodTypeId = row['shipmentMethodTypeId'];
	        carrierPartyId = row['partyId'];
	        carrierRoleTypeId = row['roleTypeId'];
	    });
	});
	
	var eventValidate = (function(){
		$('#ProStoShipmentCostForm').jqxValidator({
		   	rules : [
				{input: '#shipmentCostEstimateAdd', message: validateEmpty, action: 'blur', 
					rule: function (input, commit) {
						var value = $(input).val();
						value = value.replace(/[^\w]/gi, '');
						var res = '';
						for(var x in value){
							res += value[x].toUpperCase();
						}
						var result = $('#shipmentCostEstimateAdd').val(res);
						if(/^\s*$/.test(result)){
							return false;
						}
						return true;
					}
				},
				{input: '#carrierShipmentAdd', message: validateEmpty, action: 'blur', 
					rule: function (input, commit) {
		 				var value = $(input).val();
						if(/^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
				{input: '#orderItemFlatPriceAdd', message: validateNumber, action: 'blur', rule: 
					function (input, commit) {
						if($('#orderItemFlatPriceAdd').jqxNumberInput('getDecimal') < 0) {
							return false;
						}
						return true;
					}
				},
				{input: '#orderPricePercentAdd', message: validateNumber, action: 'blur', rule: 
					function (input, commit) {
						if($('#orderPricePercentAdd').jqxNumberInput('getDecimal') < 0) {
							return false;
						}
						return true;
					}
				},
				{input: '#orderFlatPriceAdd', message: validateNumber, action: 'blur', rule: 
					function (input, commit) {
						if($('#orderFlatPriceAdd').jqxNumberInput('getDecimal') < 0) {
							return false;
						}
						return true;
					}
				},
			]
		});
	});
	
	var eventAdd = (function(){
		$('#alterSave1').click(function(){
			$('#ProStoShipmentCostForm').jqxValidator('validate');
		});
		
		$('#ProStoShipmentCostForm').on('validationSuccess',function(){
			var row = {};
			row = {
					shipmentCostEstimateId : $('#shipmentCostEstimateAdd').val(),
					shipmentMethodTypeId : shipmentMethodTypeId,
					carrierPartyId : carrierPartyId,
					carrierRoleTypeId : carrierRoleTypeId,
					productStoreId : productStoreId,
					orderFlatPrice : $('#orderFlatPriceAdd').val(),
					orderPricePercent: $('#orderPricePercentAdd').val(),
					orderItemFlatPrice: $('#orderItemFlatPriceAdd').val(),
			};
			
			$("#jqxgrid").jqxGrid('addRow', null, row, "first");
			$("#jqxgrid").jqxGrid('clearSelection');                        
			$("#jqxgrid").jqxGrid('selectRow', 0);  
			$("#alterpopupWindow").jqxWindow('close');
			$("#jqxgrid").jqxGrid('updatebounddata');
		});
	});	
	
	var eventClose = (function(){
		$('#alterpopupWindow').on('close',function(){
			$('#ProStoShipmentCostForm').jqxValidator('hide');
			$('#carrierShipmentAdd').jqxDropDownList('clearSelection');
			$('#shipmentCostEstimateAdd').val(null);
			$('#orderFlatPriceAdd').val(0);
			$('#orderPricePercentAdd').val(0);
			$('#orderItemFlatPriceAdd').val(0);
		});
	});
	
	return {
		init: function(){
			initWindow();
			initInput();
			initDropDownButton();
			eventValidate();
			eventAdd();
			eventClose();
		}
	}
}());