$(function () {
    TripDetailObj.init();
});
var TripDetailObj = (function () {
    var init = function () {
        if (noteValidate === undefined) var noteValidate;
        initInputs();

        initElementComplex();
        initEvents();
        initValidateForm();
    };
    var map;
    var markers = [];
    var infowindows = [];
    var key = "AIzaSyCHu4vQUKFsMnqpjk_HHjIIAU_yejvT5cs";
    var listPackItems = [];

    function initMap() {
        var mapOptions = {
            label: "",
            zoom: 15,
            center: new google.maps.LatLng(21.0056183, 105.8433475),
            zoomControl: true,
            scaleControl: false,
            rotateControl: false,
            fullscreenControl: false,
            mapTypeId: google.maps.MapTypeId.ROADMAP
        };

        map = new google.maps.Map(document.getElementById('map'), mapOptions);

        var bounds = new google.maps.LatLngBounds();
        for (var x in listPackItems) {
            var contentString = '<div id="content">' +
                '<div id="siteNotice">' +
                '</div>' +
                '<h1 id="firstHeading" class="firstHeading">' + listPackItems[x].packId + '</h1>' +
                '<div id="bodyContent">' +
                '<p><b>' + uiLabelMap.CustomerId + '      :' + listPackItems[x].partyIdTo +
                '<p><b>' + uiLabelMap.Address + '      :' + listPackItems[x].fullName +
                '</p>' +
                '</div>' +
                '</div>';
            var latLong = {lat: listPackItems[x].latitude, lng: listPackItems[x].longitude};
            markers.push(makeMarker(latLong, listPackItems[x].packId
                ,contentString, makeInfoWindow));
            bounds.extend(latLong);
        }
        map.fitBounds(bounds);
    }

    function makeMarker(latLong, packId,contentString, callback) {
        var marker = new google.maps.Marker({
            position: latLong,
            map: map,
            title: packId
        });
        markers.push(marker);

        callback(contentString,marker)
        return marker;
    }

    function makeInfoWindow(contentString, marker) {
        var infowindow = new google.maps.InfoWindow({
            content: contentString
        });
        // infowindow.open(map, marker);
        marker.addListener('click', function () {
            infowindow.open(map, marker);
        });
        infowindows.push(infowindow);
    }

    var viewMap = function () {
        // $("#viewMap").jqxWindow('open');
        initMap();
    }
    var initInputs = function () {
        $("#viewMap").jqxWindow({
            maxWidth: 2000,
            minWidth: 500,
            width: 1500,
            modalZIndex: 10000,
            zIndex: 10000,
            minHeight: 500,
            height: 650,
            maxHeight: 800,
            resizable: false,
            cancelButton: $("#addProductCancel"),
            keyboardNavigation: true,
            keyboardCloseKey: 15,
            isModal: true,
            autoOpen: false,
            modalOpacity: 0.7,
            theme: theme
        });
    };
    var initGrid = function () {
        var rendertoolbar = function (toolbar) {
            toolbar.html("");
            var container = $("<div id='toolbarcontainerGridMap' class='widget-header' style='height:33px !important;'><div id='jqxProductSearch' class='pull-right' style='margin-left: -10px !important; margin-top: 4px; padding: 0px !important'></div></div>");
            toolbar.append(container);
            container.append('<div class="margin-top10">');
            container.append('<a href="javascript:TripDetailObj.viewMap()" data-rel="tooltip" data-placement="bottom" class="button-action"><i class="fa fa-map-marker"></i></a>');
            container.append('</div>');

        }
        var grid = $("#jqxgridPackSelected");
        var dataField = [
            {name: 'orderId', type: 'string'},
            {name: 'deliveryId', type: 'string'},
            {name: 'postalAddressName', type: 'string'},
            {name: 'shipBeforeDate', type: 'date', other: 'Timestamp'},
            {name: 'shipAfterDate', type: 'date', other: 'Timestamp'},
            {name: 'estimatedDeliveryDate', type: 'date', other: 'Timestamp'},
            {name: 'partyName', type: 'string'},
            {name: 'partyCode', type: 'string'},
            {name: 'fullName', type: 'string'},
            {name: 'statusId', type: 'string'},
        ];
        var columnlistPack = [
            {
                text: uiLabelMap.SequenceId, sortable: false, filterable: false, editable: false, pinned: true,
                groupable: false, draggable: false, resizable: false,
                datafield: '', columntype: 'number', minwidth: 50,
                cellsrenderer: function (row, column, value) {
                    return '<div style=margin:4px;>' + (value + 1) + '</div>';
                }
            },

            {text: uiLabelMap.OrderId, datafield: 'orderId', align: 'left', pinned: true,minwidth: 150,
                cellsrenderer: function(row, colum, value){
                    /*var menuItem = '${selectedMenuItem}';
                    var subMenuItem = '${selectedSubMenuItem}';*/
                    var link = 'viewOrder?orderId=' + value /*+ '&selectedMenuItem='+ menuItem +'&selectedSubMenuItem='+subMenuItem*/;
                    return '<span><a href=\"' + link + '\">' + value + '</a></span>';
                }},

            {text: uiLabelMap.DeliveryId, datafield: 'deliveryId', align: 'left', pinned: true,minwidth: 100,
                cellsrenderer: function(row, colum, value){
                    /*var menuItem = '${selectedMenuItem}';
                    var subMenuItem = '${selectedSubMenuItem}';*/
                    var link = 'deliverySalesDeliveryDetail?deliveryId=' + value /*+ '&selectedMenuItem='+ menuItem +'&selectedSubMenuItem='+subMenuItem*/;
                    return '<span><a href=\"' + link + '\">' + value + '</a></span>';
                }},
            {text: uiLabelMap.CustomerId, datafield: 'partyCode', align: 'left',minwidth: 250,},
            {text: uiLabelMap.BSCustomerName, datafield: 'partyName', align: 'left',minwidth: 250,},
            {text: uiLabelMap.Address, datafield: 'postalAddressName', align: 'left', minWidth: 300},

            {
                text: uiLabelMap.RequireDeliveryDate, datafield: 'estimatedDeliveryDate', align: 'left', minwidth: 100,
                cellsrenderer: function (row, column, value) {
                    if (!value) {
                        return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
                    } else {
                        return '<span style=\"text-align: right\">' + DatetimeUtilObj.formatFullDate(value) + '</span>';
                    }
                }
            },
            // {
            //     text: uiLabelMap.ShipBeforeDate, datafield: 'shipAfterDate', align: 'left', width: 150,
            //     cellsrenderer: function (row, column, value) {
            //         if (!value) {
            //             return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
            //         } else {
            //             return '<span style=\"text-align: right\">' + DatetimeUtilObj.formatFullDate(value) + '</span>';
            //         }
            //     }
            // },
            {
                text: uiLabelMap.Status, datafield: 'statusId', align: 'left', minwidth : 180,
                cellsrenderer: function (row, column, value) {
                    return '<span style=\"text-align: right\">' + TripDetailObj.viewCurrentPackStatus(value) + '</span>';
                }
            }
        ];
        initListPackItems();
        var config = {
            columns: columnlistPack,
            datafields: dataField,
            width: '100%',
            height: 'auto',
            sortable: true,
            editable: false,
            filterable: true,
            pageable: true,
            showfilterrow: true,
            useUtilFunc: false,
            useUrl: false,
            url: '',
            groupable: false,
            showgroupsheader: false,
            showaggregates: false,
            showstatusbar: false,
            virtualmode: false,
            showdefaultloadelement: true,
            autoshowloadelement: true,
            showtoolbar: false,
            columnsresize: true,
            isSaveFormData: true,
            formData: "filterObjData",
            bindresize: true,
            pagesize: 10,
            key: 'orderId',
            // rendertoolbar: rendertoolbar,
        }
        listPackGrid = new OlbGrid(grid, listPackItems, config, []);
        //OlbGridUtil.updateSource(grid, 'jqxGeneralServicer?sname=JQGetListPackByTripId&shippingTripId=' + shippingTripId, listPackItems, false);
    };
    var initListPackItems = function () {
        $.ajax({
            type: 'POST',
            url: 'jqxGeneralServicer?sname=JQGetListPackByTripId',
            async: false,
            data: {
                shippingTripId: shippingTripId,
                pagesize:0
            },
            success: function (data) {
                list = data.results;
            }
        });
        for (var i = 0; i < list.length; i++) {
            listPackItems[i] = list[i];
        }
    }
    var initElementComplex = function () {
        initGrid();
    };
    var initEvents = function () {
        // alert(dataShipper.shipper);
        $("#shipperPartyId").text(fullName);
        for (var i = 0; i < statusAllStatusDE.length; i++) {
            if (statusAllStatusDE[i].statusId == tripStatus) {
                $("#tripStatus").text(statusAllStatusDE[i].description);
                if (tripStatus == "TRIP_CREATED") {
                    $("#statusTitle").text(uiLabelMap.Hasnotbeenapproved);
                } else {
                    if (tripStatus == "TRIP_CONFIRMED") {
                        $("#statusTitle").text(uiLabelMap.Hasbeenapproved);
                    }
                }

            }
        }

    };
    var initValidateForm = function () {
    };
    var viewCurrentPackStatus = function (value) {
        for (var i = 0; i < statusAllStatusPack.length; i++) {
            if (statusAllStatusPack[i].statusId == value) {
                return statusAllStatusPack[i].description;
            }
        }
    };

    return {
        init: init,
        viewMap: viewMap,
        viewCurrentPackStatus: viewCurrentPackStatus,
    }
}());
