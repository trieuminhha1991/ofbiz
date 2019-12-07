if (typeof (OlbCustomerRoute) == "undefined") {
	var OlbCustomerRoute = (function($) {
		var gridListCustomerAssigned;
		var _routeId ;
		var cellClass;
		var init = function () {
            initElement();
            initElementComplex();
            initEvent();
            OlbAddCustRoute.init();
        };

		var initElement = function() {
			$("#jqxwindowListCustToRoute").jqxWindow({ theme: "olbius", cancelButton: $("#cancelViewListCust"),
				width: 950, maxWidth: 1845, minHeight: 310, height: 460, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7
			});
			$("#jqxNotificationCust").jqxNotification({ width: 250, position: "top-right",
				opacity: 0.9, autoClose: true, template: "info" });
		};

		var initElementComplex = function() {
			var datafields = [{ name: "customerId", type: "string" },
				{ name: "partyCode", type: "string" },
				{ name: "fullName", type: "string" },
				{ name: "distributorId", type: "string" },
				{ name: "salesmanId", type: "string" },
				{ name: "geoPointId", type: "string" },
				{ name: "postalAddressName", type: "string" },
				{ name: "latitude", type: "number" },
				{ name: "longitude", type: "number" },
				{ name: "supervisorId", type: "string" },
				{ name: "sequenceNum", type: "number" },
				{ name: "routeId", type: "string" },
				{ name: "statusId", type: "string" }];
			var columns = [
				{text: uiLabelMap.BSAgentId, datafield: "partyCode", editable: false, width: 110, cellClassName: cellClass,},
				{text: uiLabelMap.BSAgentName, datafield: "fullName", editable: false, cellClassName: cellClass,},
				{text: uiLabelMap.BSDistributorId, datafield: "distributorId", editable: false, width: 130, cellClassName: cellClass,},
				{text: uiLabelMap.BSSalesmanCode, datafield: "salesmanId", editable: false, width: 120, cellClassName: cellClass,},
                {text: uiLabelMap.BSAddress, datafield: "postalAddressName", editable: false, width: 200, cellClassName: cellClass,},
                {text: uiLabelMap.BSLocation, datafield: "geoPointId", width: 125, sortable: false, editable: false, cellClassName: cellClass,
                    cellsrenderer: function(row, column, value, a, b, data){
                        var local = "", localNoFixed = "";
                        if(!!value) {
                            local = [data.latitude.toFixed(3), data.longitude.toFixed(3)].join(", ");
                            localNoFixed = [data.latitude, data.longitude].join(", ");
                            return '<div class=\"jqx-grid-cell-left-align\" style=\"margin-top: 4px;\" title=\"'+localNoFixed+'\">'+local+'</div>';
                        } else {
                            return '<div style="width: 100%;height: 100%">' +
                                '<a href="javascript:void(0)" onclick="OlbCustomerRoute.openUpdateLocation(\''+ row +'\')" class="blue"><i class="fa-plus-circle open-sans"></i>'+uiLabelMap.BSUpdateLocationCustomer+'</a>' +
                                '</div>'
                        }
                    }
                },
                {text: uiLabelMap.BSSequenceIdCustomer, datafield: "sequenceNum",cellsalign: "right", columntype: "numberinput", editable: true, width: 80, cellClassName: cellClass,
                    validation: function (cell, value) {
                        if(value < 0){
                            return {result: false, message: uiLabelMap.ValueMustBeGreateThanZero};
                        }
                        return true;
                    },
                    createeditor: function (row, cellvalue, editor, celltext, cellwidth) {
                        editor.jqxNumberInput({width: cellwidth, inputMode: 'simple', decimalDigits: 0});
                    },
                    cellsrenderer: function(row, column, value){
                        if(typeof(value) == 'number'){
                            return "<span style='text-align: right'>" + value + "</span>";
                        }
                    },
                    initeditor: function (row, cellvalue, editor, celltext, pressedkey) {
                        if(typeof(cellvalue) == 'number'){
                            editor.val(cellvalue);
                        }
                    },
                    cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
                        updateSequenceNum(row, oldvalue, newvalue);
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

        var updateSequenceNum = function(row,oldValue, newValue){
            var data = $("#jqxgridViewListCust").jqxGrid("getrowdata",row);
            var dataMap = {
                routeId: _routeId,
                customerId: data.customerId,
                sequenceNum: newValue
            };

            var datas = $("#jqxgridViewListCust").jqxGrid("getboundrows");
            $.each(datas, function (index, value) {
                if (OlbCore.isNotEmpty(value) && OlbCore.isNotEmpty(value.sequenceNum) && value.sequenceNum == newValue) {
                    $("#jqxgridViewListCust").jqxGrid('setcellvalue', index, 'sequenceNum', null);
                }
            });

            $.ajax({
                type: 'POST',
                url: 'updateRouteCustomerSequenceNum',
                data: dataMap,
                beforeSend: function(){
                    $("#loader_page_common").show();
                },
                success: function(data){
                    jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
                            $("#jqxgridViewListCust").jqxGrid('setcellvalue', row, 'sequenceNum', oldValue);
                            $('#container').empty();
                            $('#jqxNotification').jqxNotification({ template: 'error'});
                            $("#jqxNotification").html(errorMessage);
                            $("#jqxNotification").jqxNotification("open");
                            return false;
                        }, function(){
                            $('#container').empty();
                            $("#jqxgridViewListCust").jqxGrid("updatebounddata");
                            $('#jqxNotification').jqxNotification({ template: 'info'});
                            $("#jqxNotification").html(uiLabelMap.updateSuccess);
                            $("#jqxNotification").jqxNotification("open");
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
        };

        var initEvent = function() {
            $("#jqxwindowListCustToRoute").on("close", function (event) {
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
                var data = OlbAddCustRoute.getValue();
                if (_.isEmpty(data)) {
                    bootbox.alert(uiLabelMap.BSNotCustomerSelected);
                } else {
                    var customers = [];
                    for ( var x in data) {
                        customers.push(data[x].partyId);
                    }
                    var dataMap = {
                        routeId: _routeId,
                        parties: JSON.stringify(customers)
                    };
                    $.ajax({
                        type: 'POST',
                        url: 'createRouteStores',
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
                                    OlbCustomerRoute.notify({});
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
                    OlbAddCustRoute.close();
                }
            });

        };

		var updateSource = function (routeId) {
            gridListCustomerAssigned.updateSource("jqxGeneralServicer?sname=JQGetCustAssignedToRoute&routeId=" + routeId, null, null);
        };
		var open = function(row) {
            var grid = $('#ListRoute');
            var rowData = grid.jqxGrid('getrowdata', row);
            _routeId = rowData.routeId;
            //var salesmanId = rowData.employeeId;
            var salesmanId = rowData.salesmanId;
            $(".customerInfo").text(rowData.routeName + " [" + rowData.routeCode + "]");
            $("#jqxwindowListCustToRoute").data("routeId", _routeId);
            $("#jqxwindowListCustToRoute").data("salesmanId", salesmanId);
            OlbCustomerRoute.updateSource(_routeId);
            var wtmp = window;
            var tmpwidth = $("#jqxwindowListCustToRoute").jqxWindow("width");
            $("#jqxwindowListCustToRoute").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
            $("#jqxwindowListCustToRoute").jqxWindow("open");
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
            var data = OlbCustomerRoute.getValue();
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
                                    routeId: _routeId,
                                    parties: JSON.stringify(customers)
                                };

                                $.ajax({
                                    type: 'POST',
                                    url: 'removeRouteStores',
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
            var dataInput = data;
            //dataInput["address1"] = data.postalAddressName;
            OlbCustomerOnMap.open(dataInput);
        };

        var cellClass = function (row, columnfield, value) {
            var data = $('#jqxgridViewListCust').jqxGrid('getrowdata', row);
            if (typeof(data) != 'undefined') {
                if ("PARTY_DISABLED" == data.statusId) {
                    return "background-cancel";
                }
            }
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
	})(jQuery);
}
if (typeof (OlbAddCustRoute) == "undefined") {
	var OlbAddCustRoute = (function($) {
	    var gridListCustomerAvailable;
	    var init = function () {
            initElement();
            initElementComplex();
            initEvent();
        };
		var initElement = function() {
			$("#jqxwindowAddCust").jqxWindow({ theme: "olbius", cancelButton: $("#cancelAddCust"),
				width: 950, maxWidth: 1845, minHeight: 310, height: 460, resizable: false,  isModal: true, autoOpen: false, modalOpacity: 0.7
			});
		};

		var initEvent = function () {

            $("#jqxgridAddCust").on("rowselect", function (event) {
                var rowindex = event.args.rowindex;
                var rowdata = $("#jqxgridAddCust").jqxGrid('getrowdata', rowindex);
                var geoPointId = rowdata['geoPointId'];
                if(geoPointId == ""){
                    bootbox.dialog(uiLabelMap.BSCustomerNoGeoPointAreYouSureAdd,
                        [
                            {"label": uiLabelMap.wgcancel,
                                "icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
                                "callback": function() {
                                    $('#jqxgridAddCust').jqxGrid('unselectrow', rowindex);
                                }
                            },
                            {"label": uiLabelMap.wgok,
                                "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
                                "callback": function() {
                                    // do not thing
                                }

                            }
                        ]
                    );
                }
            });

        };

		var initElementComplex = function() {
            var datafields = [{ name: "partyId", type: "string" },
                { name: "partyCode", type: "string" },
                { name: "fullName", type: "string" },
                { name: "distributorId", type: "string" },
                { name: "postalAddressName", type: "string" },
                { name: "salesmanId", type: "string" },
                { name: "geoPointId", type: "string" },
                { name: "supervisorId", type: "string" }];
            var columns = [
                {text: uiLabelMap.BSAgentId, datafield: "partyCode", width: 150},
                {text: uiLabelMap.BSAgentName, datafield: "fullName"},
                {text: uiLabelMap.BSAddress, datafield: "postalAddressName", editable: false, width: 200},
                {text: uiLabelMap.BSDistributorId, datafield: "distributorId", width: 150},
                {text: uiLabelMap.BSSalesmanCode, datafield: "salesmanId", width: 150}];
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

        var updateSource = function (salesmanId,routeId) {
            gridListCustomerAvailable.updateSource("jqxGeneralServicer?sname=JQGetCustAvailable&salesmanId=" + salesmanId + "&routeId=" + routeId, null, null);
        };

		var open = function() {
            var routeId = $("#jqxwindowListCustToRoute").data("routeId");
		    var salesmanId = $("#jqxwindowListCustToRoute").data("salesmanId");
			OlbAddCustRoute.updateSource(salesmanId,routeId);
			var wtmp = window;
			var tmpwidth = $("#jqxwindowAddCust").jqxWindow("width");
	        $("#jqxwindowAddCust").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 70 }});
	        $("#jqxwindowAddCust").jqxWindow("open");
		};
		var close = function() {
			$('#jqxgridViewListCust').jqxGrid('updatebounddata');
			$('#jqxgridAddCust').jqxGrid('updatebounddata');
			$("#jqxwindowAddCust").jqxWindow("close");
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
	})(jQuery);
}

$(function() {
    // ensure that ContextMenuRoute init one time.
    if (typeof (OlbCustomerRoute) != "undefined") {
        if (flagPopupLoadUpdateCustRoute) {
            OlbCustomerRoute.init();
            flagPopupLoadUpdateCustRoute = false;
            setTimeout(function(){ flagPopupLoadUpdateCustRoute = true }, 300);
        }
    }
});