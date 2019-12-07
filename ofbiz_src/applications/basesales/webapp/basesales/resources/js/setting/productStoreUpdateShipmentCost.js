$(function(){
	OlbSettingUpdateProductStoreShipmentCost.init();
});

var OlbSettingUpdateProductStoreShipmentCost = (function(){
	var shipmentMethodTypeId2 = null;
	var carrierPartyId2 = null;
	var carrierRoleTypeId2 = null;
	
	var eventMenu = (function(){
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
	        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action"); 
            if (action == 'update') {
            	var wtmp = window;
        	   	var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        	   	var data = $("#jqxgrid").jqxGrid('getrowdata',rowindex);
        	   	var tmpwidth = $('#alterpopupWindowEdit').jqxWindow('width');
        	   	$('#alterpopupWindowEdit').jqxWindow('open');
    		   	if (rowindex >= 0) {
    		   		openPSShipmentCostEdit();
    		   	}
            }
		});
		
		function openPSShipmentCostEdit(){
			var indexSeleted = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var data = $("#jqxgrid").jqxGrid("getrowdata", indexSeleted);
			if (data != null) {
				if (data.shipmentCostEstimateId != null) $("#shipmentCostEstimateEdit").val(data.shipmentCostEstimateId);
				if(data.shipmentMethodTypeId != null && data.carrierPartyId !=null && data.carrierRoleTypeId !=null){
					var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + data.shipmentMethodTypeId + "/" + data.carrierPartyId + "/" + data.carrierRoleTypeId + '</div>';
			        $("#carrierShipmentEdit").jqxDropDownButton('setContent', dropDownContent);
				}
				if (data.orderFlatPrice != null) $("#orderFlatPriceEdit").jqxNumberInput('setDecimal', data.orderFlatPrice);
				if (data.orderPricePercent != null) $("#orderPricePercentEdit").jqxNumberInput('setDecimal', data.orderPricePercent);
				if (data.orderItemFlatPrice != null) $("#orderItemFlatPriceEdit").jqxNumberInput('setDecimal', data.orderItemFlatPrice);
				$("#alterpopupWindowEdit").jqxWindow("open");
				
				shipmentMethodTypeId2 = data.shipmentMethodTypeId;
				carrierPartyId2 = data.carrierPartyId;
				carrierRoleTypeId2 = data.carrierRoleTypeId;
			}
		}
	});
	
	var initWindow = (function(){
		$('#alterpopupWindowEdit').jqxWindow({width: 600, height : 300, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancel2"), modalOpacity: 0.7, title: updatePopup3});
	});
	
	var initInput = (function(){
		jOlbUtil.input.create("#shipmentCostEstimateEdit", {width:'95%',height:24, disabled: true});
		$("#orderFlatPriceEdit").jqxNumberInput({width: '96.6%', height: 28, spinButtons: false, inputMode: 'simple', decimalDigits: 0 });
		$("#orderPricePercentEdit").jqxNumberInput({width: '96.6%', height: 28, spinButtons: false, inputMode: 'simple', decimalDigits: 0 });
		$("#orderItemFlatPriceEdit").jqxNumberInput({width: '96.6%', height: 28, spinButtons: false, inputMode: 'simple', decimalDigits: 0 });
	});
	
	var initDropDownList = (function(){
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
				   	$("#carrierShipmentMethodGridEdit").jqxGrid('updatebounddata');
				},
				pager: function (pagenum, pagesize, oldpagenum) {
				  	// callback called when a page or page size is changed.
				},
				sort: function () {
				  	$("#carrierShipmentMethodGridEdit").jqxGrid('updatebounddata');
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
		$('#carrierShipmentEdit').jqxDropDownButton({ width: '96.6%', height: 30, dropDownHorizontalAlignment: 'left'});
		$("#carrierShipmentMethodGridEdit").jqxGrid({
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
		  
		$("#carrierShipmentMethodGridEdit").on('rowselect', function (event) {
	        var args = event.args;
	        var row2 = $("#carrierShipmentMethodGridEdit").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row2['shipmentMethodTypeId'] + "/" + row2['partyId'] + "/" + row2['roleTypeId'] + '</div>';
	        $("#carrierShipmentEdit").jqxDropDownButton('setContent', dropDownContent);
	        
	        shipmentMethodTypeId2 = row2['shipmentMethodTypeId'];
	        carrierPartyId2 = row2['partyId'];
	        carrierRoleTypeId2 = row2['roleTypeId'];
	    });
		
		$('#alterSave2').click(function () {
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
		   	if (rowindex >= 0) {
			   	editPSShipmentCost();
	           	$('#alterpopupWindowEdit').jqxWindow('hide');
	           	$('#alterpopupWindowEdit').jqxWindow('close');
		   	}
	    });
		
		function editPSShipmentCost(){
			var row = $("#jqxgrid").jqxGrid('getselectedrowindexes');
			var success = editSuccess;
			var cMemberr = new Array();
				var data3 = $("#jqxgrid").jqxGrid('getrowdata', row);
				var map = {};
				map['productStoreId'] = data3.productStoreId;
				map['shipmentCostEstimateId'] = data3.shipmentCostEstimateId;
				map['shipmentMethodTypeId'] = shipmentMethodTypeId2;
			    map['carrierPartyId'] = carrierPartyId2;
		        map['carrierRoleTypeId'] = carrierRoleTypeId2;
				map['orderFlatPrice'] = $("#orderFlatPriceEdit").val();
				map['orderPricePercent'] = $("#orderPricePercentEdit").val();
				map['orderItemFlatPrice'] = $("#orderItemFlatPriceEdit").val();
				cMemberr = map;
			if (cMemberr.length <= 0){
				return false;
			} else {
				cMemberr = JSON.stringify(cMemberr);
				jQuery.ajax({
			        url: 'updateShipmentCost',
			        type: 'POST',
			        async: true,
			        data: {
			        		'cMemberr': cMemberr,
		        		},
			        success: function(res) {
			        	var message = '';
						var template = '';
						if(res._ERROR_MESSAGE_ || res._ERROR_MESSAGE_LIST_){
							if(res._ERROR_MESSAGE_LIST_){
								message += res._ERROR_MESSAGE_LIST_;
							}
							if(res._ERROR_MESSAGE_){
								message += res._ERROR_MESSAGE_;
							}
							template = 'error';
						}else{
							message = success;
							template = 'success';
							$("#jqxgrid").jqxGrid('updatebounddata');
			        		$("#jqxgrid").jqxGrid('clearselection');
						}
						updateGridMessage('jqxgrid', template ,message);
			        },
			        error: function(e){
			        	console.log(e);
			        }
			    });
			}
		}
	});
	
	return {
		init: function(){
			initWindow();
			initInput();
			initDropDownList();
			eventMenu();
		}
	}
}());
