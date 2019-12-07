if (typeof (OlbDeliveryClusterCustomerObj) == "undefined") {
	var OlbDeliveryClusterCustomerObj = (function() {
		var gridListCustomerAssigned;
		var currDeliveryClusterId ;
		var init = function () {
            initElement();
            initElementComplex();
            initEvent();
            OlbAddCustCluster.init();
        };

		var initElement = function() {
			$("#jqxwindowListCustToCluster").jqxWindow({ theme: "olbius", cancelButton: $("#cancelViewListCust"),
				width: 750, maxWidth: 1845, minHeight: 310, height: 460, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7
			});
			$("#jqxNotificationCust").jqxNotification({ width: 250, position: "top-right",
				opacity: 0.9, autoClose: true, template: "info" });
		};

		var initElementComplex = function() {
			var datafields = [{ name: "customerId", type: "string" },
				{ name: "partyCode", type: "string" },
				{ name: "partyName", type: "string" },
				{ name: "executorId", type: "string" },
				{ name: "managerId", type: "string" },
                { name: "executorCode", type: "string" },
				{ name: "managerCode", type: "string" },
                { name: "executorName", type: "string" },
				{ name: "managerName", type: "string" },
				{ name: "geoPointId", type: "string" },
				{ name: "postalAddressName", type: "string" },
				{ name: "latitude", type: "number" },
				{ name: "longitude", type: "number" },
				{ name: "supervisorId", type: "string" }];
			var columns = [
				{text: uiLabelMap.BSCustomerId, datafield: "partyCode", editable: false, width: 110},
				{text: uiLabelMap.BSCustomerName, datafield: "partyName", editable: false,width: 150},
				{text: uiLabelMap.BSAddress, datafield: "postalAddressName", editable: false},
                {text: uiLabelMap.BSLocation, datafield: "geoPointId", width: 125, sortable: false, editable: false,
                    cellsrenderer: function(row, column, value, a, b, data){
                        var local = "", localNoFixed = "";
                        if(!!value) {
                            local = [data.latitude.toFixed(3), data.longitude.toFixed(3)].join(", ");
                            localNoFixed = [data.latitude, data.longitude].join(", ");
                            return '<div class=\"jqx-grid-cell-left-align\" style=\"margin-top: 4px;\" title=\"'+localNoFixed+'\">'+local+'</div>';
                        } else {
                            return '<div style="width: 100%;height: 100%">' +
                                '<a href="javascript:void(0)" onclick="OlbDeliveryClusterCustomerObj.openUpdateLocation(\''+ row +'\')" class="blue"><i class="fa-plus-circle open-sans"></i>'+uiLabelMap.BSUpdateLocationCustomer+'</a>' +
                                '</div>'
                        }
                    }
                }];
            var config = {
                width: "100%",
                pagesize: 10,
                filterable: true,
                sortable: true,
                pageable: true,
                editable: true,
                editmode: 'dblclick',
                showfilterrow: true,
                datafields: datafields,
                columns: columns,
                useUrl: true,
                url: '',
                useUtilFunc: true,
                showtoolbar: false,
                showdefaultloadelement: true,
                autoshowloadelement: true,
                enabletooltips: true,
                selectionmode: "checkbox",
            };
            gridListCustomerAssigned = new OlbGrid($("#jqxgridViewListCust"), null, config, []);
		};

        var initEvent = function() {
            $("#jqxwindowListCustToCluster").on("close", function (event) {
                $('#jqxgridViewListCust').jqxGrid('clear');
                $('#jqxgridAddCust').jqxGrid('clear');
                $('#jqxgridViewListCust').jqxGrid('clearselection');
                $('#jqxgridAddCust').jqxGrid('clearselection');

                var source = $('#jqxgridViewListCust').jqxGrid('source');
                source._source.url = "jqxGeneralServicer?sname=JQGetEmpty";
                $('#jqxgridViewListCust').jqxGrid('source', source);
                $("#jqxgridViewListCust").jqxGrid('gotopage', 0);
            });
            $("#saveListCust").click(function() {
                var data = OlbAddCustCluster.getValue();
                if (_.isEmpty(data)) {
                    bootbox.alert(uiLabelMap.BSNotCustomerSelected);
                } else {
                    var customers = [];
                    for ( var x in data) {
                        customers.push(data[x].partyId);
                    }
                    var dataMap = {
                        deliveryClusterId: currDeliveryClusterId,
                        parties: JSON.stringify(customers)
                    };
                    $.ajax({
                        type: 'POST',
                        url: 'createDeliveryClusterCustomers',
                        data: dataMap,
                        beforeSend: function(){
                            $("#loader_page_common").show();
                        },
                        success: function(data){
                            jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
                                    $('#container').empty();
                                    $('#jqxNotification').jqxNotification({ template: 'error'});
                                    $("#jqxNotification").html(errorMessage);
                                    $("#jqxNotification").jqxNotification("open");
                                    return false;
                                }, function(){
                                    OlbDeliveryClusterCustomerObj.notify({});
                                    gridListCustomerAssigned.updateBoundData();
                                }
                            );
                        },
                        error: function(data){
                            alert("Send request is error");
                        },
                        complete: function(data){
                            $("#loader_page_common").hide();
                        },
                    });
                    OlbAddCustCluster.close();
                }
            });
        };

		var updateSource = function (deliveryClusterId) {
            gridListCustomerAssigned.updateSource("jqxGeneralServicer?sname=JQGetListCustomerShipperByCluster&deliveryClusterId=" + deliveryClusterId, null, null);
        };
		var open = function(rowId) {
            var grid = $('#jqxgridDeliveryCluster');
            var rowData = grid.jqxGrid('getrowdata', rowId);
            currDeliveryClusterId = rowData.deliveryClusterId;
            var shipperId = rowData.executorId;
            $(".customerInfo").text(rowData.deliveryClusterName + " [" + rowData.deliveryClusterCode + "]");
            $("#jqxwindowListCustToCluster").data("deliveryClusterId", currDeliveryClusterId);
            $("#jqxwindowListCustToCluster").data("shipperId", shipperId);
            OlbDeliveryClusterCustomerObj.updateSource(currDeliveryClusterId);
            var wtmp = window;
            var tmpwidth = $("#jqxwindowListCustToCluster").jqxWindow("width");
            $("#jqxwindowListCustToCluster").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
            $("#jqxwindowListCustToCluster").jqxWindow("open");
		};
		var notify = function(res) {
			$("#jqxNotificationCust").jqxNotification("closeLast");
			if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
				$("#jqxNotificationCust").jqxNotification({ template: "error"});
				$("#notificationContentCust").text(uiLabelMap.updateError);
				$("#jqxNotificationCust").jqxNotification("open");
			}else {
				$("#jqxNotificationCust").jqxNotification({ template: "info"});
				$("#notificationContentCust").text(uiLabelMap.updateSuccess);
				$("#jqxNotificationCust").jqxNotification("open");
				$('#jqxgridViewListCust').jqxGrid('clearselection');
				$('#jqxgridAddCust').jqxGrid('clearselection');
			}
		};
		var _delete = function() {
            var data = OlbDeliveryClusterCustomerObj.getValue();
            if (!_.isEmpty(data)) {
                bootbox.dialog(uiLabelMap.ConfirmDelete,
                    [
                        {"label": uiLabelMap.wgcancel,
                            "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
                            "callback": function() {bootbox.hideAll();}
                        },
                        {"label": uiLabelMap.wgok,
                            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
                            "callback": function() {
                                var customers = [];
                                for ( var x in data) {
                                    customers.push(data[x].customerId);
                                }
                                var dataMap = {
                                    deliveryClusterId: currDeliveryClusterId,
                                    parties: JSON.stringify(customers)
                                };

                                $.ajax({
                                    type: 'POST',
                                    url: 'removeDeliveryClusterCustomers',
                                    data: dataMap,
                                    beforeSend: function(){
                                        $("#loader_page_common").show();
                                    },
                                    success: function(data){
                                        jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
                                                $('#container').empty();
                                                $('#jqxNotification').jqxNotification({ template: 'error'});
                                                $("#jqxNotification").html(errorMessage);
                                                $("#jqxNotification").jqxNotification("open");
                                                return false;
                                            }, function(){
                                                $('#container').empty();
                                                $('#jqxNotification').jqxNotification({ template: 'info'});
                                                $("#jqxNotification").html(uiLabelMap.wgdeletesuccess);
                                                $("#jqxNotification").jqxNotification("open");
                                                gridListCustomerAssigned.updateBoundData();
                                            }
                                        );
                                    },
                                    error: function(data){
                                        alert("Send request is error");
                                    },
                                    complete: function(data){
                                        $("#loader_page_common").hide();
                                    },
                                });
                            }
                        }
                    ]
                );
			} else {
				bootbox.alert(uiLabelMap.BSNotCustomerSelected);
			}
		};
        var getValue = function() {
            var value = [];
            var result = [];
            var rowindexes = $('#jqxgridViewListCust').jqxGrid('getselectedrowindexes');
            for ( var x in rowindexes) {
                value.push($('#jqxgridViewListCust').jqxGrid('getrowdata', rowindexes[x]));
            }
            $.each(value, function(i, v) {
                if (OlbCore.isNotEmpty(v)) {
                    result.push(v);
                }
            });
            return result;
        };

        var openUpdateLocation = function(row) {
            var data = $("#jqxgridViewListCust").jqxGrid("getrowdata",row);
            OlbDCCustomerOnMap.open(data);
        };
		return {
			init: init,
			open: open,
            openUpdateLocation: openUpdateLocation,
            updateSource: updateSource,
			notify: notify,
            getValue: getValue,
			_delete: _delete
		};
	})();
}
if (typeof (OlbAddCustCluster) == "undefined") {
	var OlbAddCustCluster = (function() {
	    var gridListCustomerAvailable;
	    var init = function () {
            initElement();
            initElementComplex();
        };
		var initElement = function() {
			$("#jqxwindowAddCustToCluster").jqxWindow({ theme: "olbius", cancelButton: $("#cancelAddCust"),
				width: 750, maxWidth: 1845, minHeight: 310, height: 460, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7
			});
		};
		var initElementComplex = function() {
            var datafields = [{ name: "partyId", type: "string" },
                { name: "partyCode", type: "string" },
                { name: "postalAddressName", type: "string" },
                { name: "partyName", type: "string" }];
            var columns = [
                {text: uiLabelMap.BSCustomerId, datafield: "partyCode", width: 150},
                {text: uiLabelMap.BSCustomerName, datafield: "partyName"},
                {text: uiLabelMap.BSAddress, datafield: "postalAddressName", width: 300}];
            var config = {
                width: "100%",
                pagesize: 10,
                filterable: true,
                pageable: true,
                sortable: true,
                showfilterrow: true,
                datafields: datafields,
                columns: columns,
                useUrl: true,
                url: '',
                useUtilFunc: true,
                showtoolbar: false,
                contextMenu: "contextMenu",
                showdefaultloadelement: true,
                autoshowloadelement: true,
                enabletooltips: true,
                selectionmode: "checkbox",
            };
            gridListCustomerAvailable = new OlbGrid($("#jqxgridAddCust"), null, config, []);
		};

        var updateSource = function (deliveryClusterId,shipperId) {
            gridListCustomerAvailable.updateSource("jqxGeneralServicer?sname=JQGetListCustomerCluster&type=SMCHANNEL_MT", null, null);
        };

		var open = function() {
            var deliveryClusterId = $("#jqxwindowListCustToCluster").data("deliveryClusterId");
		    var shipperId = $("#jqxwindowListCustToCluster").data("shipperId");
			OlbAddCustCluster.updateSource(deliveryClusterId,shipperId);
			var wtmp = window;
			var tmpwidth = $("#jqxwindowAddCustToCluster").jqxWindow("width");
	        $("#jqxwindowAddCustToCluster").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#jqxwindowAddCustToCluster").jqxWindow("open");
		};
		var close = function() {
			$('#jqxgridViewListCust').jqxGrid('updatebounddata');
			$('#jqxgridAddCust').jqxGrid('updatebounddata');
			$("#jqxwindowAddCustToCluster").jqxWindow("close");
            //goto page 0 without call server.
            var source = $('#jqxgridAddCust').jqxGrid('source');
            source._source.url = "jqxGeneralServicer?sname=JQGetEmpty";
            $('#jqxgridAddCust').jqxGrid('source', source);
            $("#jqxgridAddCust").jqxGrid('gotopage', 0);
		};
		var getValue = function() {
			var value = [];
			var result = [];
			var rowindexes = $('#jqxgridAddCust').jqxGrid('getselectedrowindexes');
			for ( var x in rowindexes) {
				value.push($('#jqxgridAddCust').jqxGrid('getrowdata', rowindexes[x]));
			}
            $.each(value, function(i, v) {
                if (OlbCore.isNotEmpty(v)) {
                    result.push(v);
                }
            });
			return result;
		};
		return {
			init: init,
			open: open,
			close: close,
            updateSource: updateSource,
			getValue: getValue
		};
	})();
}

$(function() {
    OlbDeliveryClusterCustomerObj.init();
});