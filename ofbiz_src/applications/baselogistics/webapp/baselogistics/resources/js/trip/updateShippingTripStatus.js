$(function () {
    TripDetailObj.init();
});
var TripDetailObj = (function() {
    var gridConfirm = $('#confirmWindow');
    var gridPackStatus;
    var gridPackOrder;
    var listPackSelected = $('#jqxgridPackSelected');
    var listPackItems = [];
	var init = function() {
		if (noteValidate === undefined) var noteValidate;
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
		initConfirmGrid();
        initListPackItems();
        initPackOrder();
        
	};
	var initInputs = function() {
		$("#shippingTripId").text(shippingTrip.shippingTripId);
		$("#shipperPartyId").text(shippingTrip.shipper);
		$("#estimatedTimeStart").text(shippingTrip.startDateTime);
		$("#estimatedTimeEnd").text(shippingTrip.finishedDateTime);
		$("#tripCost").text(formatnumber(parseFloat(shippingTrip.tripCost)));
		$("#costCustomerPaid").text(formatnumber(parseFloat(shippingTrip.costCustomerPaid)));
		$("#description").text(shippingTrip.description);
		for(var i = 0; i < statusDataDE.length; i++){
			if(statusDataDE[i].statusId == tripStatus ){
					$("#tripStatus").text(statusDataDE[i].description);
			}
		}
	};
	var initConfirmGrid = function() {
		$("#confirmWindow").jqxWindow({
			 minWidth: '80%',maxHeight:'100%',minHeight: 450,height: 450,resizable: true, cancelButton: $("#confirmCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
		});
		var columns =  [
			{ text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true, cellClassName: cellClass,
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: '4%',
			    cellsrenderer: function (row, column, value) {
			        return '<div style=margin:4px;>' + (value + 1) + '</div>';
			    }
			},
			{ text: uiLabelMap.OrderId, datafield: 'orderId', align: 'left', pinned: true,width: '11%', cellClassName: cellClass,},
			{ text: uiLabelMap.CustomerName, datafield: 'partyName', align: 'left', width:'14%', cellClassName: cellClass,},
			{ text: uiLabelMap.BLDmsCustomerAddress, datafield: 'postalAddressName', align: 'left',minwidth:600, cellClassName: cellClass,},
		 	{ text: uiLabelMap.Status, datafield: 'packStatus', align: 'center', width: '14%',
		 cellsrenderer: function(row, column, value){
			 if(value != "Xong"){
				 return '<span style=\"text-align: center; background-color: #daddd5; \">'+ value +'</span>';
			 }
			return '<span style=\"text-align: center\">'+ value +'</span>';
		 }
		}
		];
		var dataField = [
			{ name: 'orderId', type: 'string',},
			{ name: 'partyName', type: 'string'},
			{ name: 'postalAddressName', type: 'string'},
			{ name: 'packStatus', type: 'string'},
            { name: 'packId', type: 'string'},
		];
		var configGrid = {
				datafields: dataField,
				columns: columns,
				width: '100%',
				height: 'auto',
				sortable: true,
				filterable: false,
				pageable: true,
				showfilterrow: false,
				useUtilFunc: false,
				groupable: false,
				showgroupsheader: false,
				showaggregates: false,
				showstatusbar: false,
				showdefaultloadelement:true,
				autoshowloadelement:true,
				showtoolbar:false,
				columnsresize: true,
				isSaveFormData: true,
				selectionmode: "none",
				bindresize: true,
				pagesize: 10,
		};
	 gridPackStatus = new OlbGrid($('#jqxgridConfirmPackStatus'), null, configGrid, []);
	}
	var initElementComplex = function() {
	};
    var initPackOrder = function () {
         var columns = [
             { text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
         	    groupable: false, draggable: false, resizable: false,
         	    datafield: '', columntype: 'number', width: '3%',
         	    cellsrenderer: function (row, column, value) {
         	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
         	    }
         	},
         	{ text: uiLabelMap.OrderId, datafield: 'orderId', align: 'left', pinned: true, editable: false, width: '14%'},
         	{ text: uiLabelMap.CustomerName, datafield: 'partyName', align: 'left', editable: false, width:'14%'},
         	{ text: uiLabelMap.BLDmsCustomerAddress, datafield: 'postalAddressName', align: 'left', editable: false, width: '59%'},
          	{ text: uiLabelMap.BLCompleted, datafield: 'packStatus', align: 'center', columntype:'checkbox', editable: 'true',width:'11%',}
        ];
        var dataField = [
            { name: 'orderId', type: 'string',},
			{ name: 'partyName', type: 'string'},
			{ name: 'postalAddressName', type: 'string'},
			{ name: 'packStatus', type: 'bool'},
            { name: 'packId', type: 'string'},
        ];
        var configGrid = {
            datafields: dataField,
            columns: columns,
            editable: "true",
            editmode: 'click',
            width: '100%',
            height: 'auto',
            sortable: true,
            filterable: false,
            pageable: true,
            useUtilFunc: false,
            groupable: false,
            showgroupsheader: false,
            showaggregates: false,
            showstatusbar: false,
            showdefaultloadelement: true,
            autoshowloadelement: true,
            showtoolbar: false,
            columnsresize: true,
            isSaveFormData: true,
            selectionmode: "none",
            bindresize: true,
            virtualmode: false,
            pagesize: 15,
        };
        gridPackOrder = new OlbGrid($('#jqxgridPackSelected'), listPackItems, configGrid, []);
    }
    var initListPackItems = function () {
        $.ajax({
            type: 'POST',
            url: 'jqxGeneralServicer?sname=JQGetListPackByTripId&pagesize=0',
            async: false,
            data: {
                shippingTripId: shippingTrip.shippingTripId,
            },
            success: function (data) {
                list = data.results;
            }
        });
        for (var i = 0; i < list.length; i++) {
            listPackItems[i] = list[i];
        }
    }
                
	var initEvents = function() {
		$('#btnConfirm').on('click', function() {
			var dataConfirm = new Array();
			var rows = $("#jqxgridPackSelected").jqxGrid('getrows');
			for (var i = 0; i < listPackItems.length; i++) {
				var row = rows[i];
				var mapData = {};
				mapData['packId'] = row.packId;
                mapData['orderId'] = row.orderId;
				mapData['partyName'] = row.partyName;
				mapData['postalAddressName'] = row.postalAddressName;
				var status = row.packStatus ;
				if (status) {
					mapData['packStatus'] = uiLabelMap.Done;
				}else {
					mapData['packStatus'] = uiLabelMap.Canceled;
				}
				dataConfirm.push(mapData);
			}
			gridPackStatus.updateSource(null, dataConfirm, function () {
			});
			$("#confirmWindow").jqxWindow('open');
			gridPackStatus.updateBoundData();
			$('#confirmSave').on('click', function() {
				jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureUpdate, function() {
					$("#confirmWindow").jqxWindow('close');
					Loading.show('loadingMacro');
	            	setTimeout(function(){
	            		finishUpdateTripStatus();
		            	Loading.hide('loadingMacro');
	            	}, 500);
	            });
			});
			$('#confirmCancel').on('click', function() {
					$("#confirmWindow").jqxWindow('close');
			});
		});
	};
	var finishUpdateTripStatus = function() {
		var rows = $("#jqxgridPackSelected").jqxGrid('getrows');
		var listPackStatus = [];
		for (var i = 0; i < listPackItems.length; i++) {
			var packStatusTmp = {};
			packStatusTmp['packId'] = rows[i].packId;
			if (rows[i].packStatus) {
				packStatusTmp['packStatusId'] ="PACK_DELIVERED";
			} else {
				packStatusTmp['packStatusId'] = "PACK_CANCELLED";
			}
			listPackStatus.push(packStatusTmp);
		}
		var listPackStatus = JSON.stringify(listPackStatus);
		var dataMap = {};
		dataMap = {
				shippingTripId: shippingTrip.shippingTripId,
				listPackStatus: listPackStatus
		};
		$.ajax({
			type: 'POST',
			url: 'updateShippingTripStatus',
			async: false,
			data: dataMap,
			beforeSend: function(){
				$("#loader_page_common").show();

			},
			success: function(data){
				if (data._ERROR_MESSAGE_) {
					jOlbUtil.alert.error(uiLabelMap.HasErrorWhenProcess);
					return;
				}
				jOlbUtil.alert.info(uiLabelMap.SuccessfulWhenUpdate);
				viewTripDetail(data.shippingTripId);
			},
			error: function(data){
				alert("Send request is error");
			},
			complete: function(data){
				$("#loader_page_common").hide();
			},
		});
	};
	var cellClass = function (row, columnfield, value) {
 		var data = $('#jqxgridConfirmPackStatus').jqxGrid('getrowdata', row);
 		if (typeof(data) != 'undefined') {
 			if ("SHIP_PACK_CANCELLED" == data.statusId) {
 				return "background-cancel";
 			}
 		}
    }
	var initValidateForm = function(){
		};
	var viewTripDetail = function (tripId) {
		window.location.href = "shippingTripDetail?shippingTripId="+tripId;
	};
	return {
		init: init,
	}
})();
