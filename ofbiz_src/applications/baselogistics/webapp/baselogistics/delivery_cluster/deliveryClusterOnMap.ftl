<div id="PopupClusterOnMap" class='hide'>
    <div>
    ${uiLabelMap.BLDeliveryClusterOnMap}
    </div>
    <div class="form-window-container">
        <div class='row-fluid'>
            <div class='span10'>
                <div id="mapCluster" style="height: 500px;"></div>
            </div>
            <div class='span2' style="margin-left: 10px">
                <div class='row-fluid'>
                    <h3>${uiLabelMap.BSNote}</h3>
                    <div id="commentsContainer" style="height: 120px; overflow-y: scroll; overflow-x: hidden">
                        <#--add cmt here-->
                    </div>
                </div>
                <hr>
                <div class='row-fluid margin-bottom10'>
                    <label >${uiLabelMap.BLChooseShipper}: </label>
                    <div id="dropdownShipperClusterMap">
                        <div id="shipperGridClusterMap"></div>
                    </div>
                </div>
                <div class='row-fluid'>
                    <label>${uiLabelMap.BLDeliveryClusterCode}: </label>
                    <div id="deliveryClusterIds" class="close-box-custom"></div>
                </div>
            </div>
        </div>
        <div class="form-action">
            <button id="cancelViewClusterOnMap" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
        </div>
    </div>
</div>
<style>
    .custom-control-toolbar {
        margin-top: 6px;
    }
</style>
<script>
    if (typeof (OlbDeliveryClusterOnMap) == "undefined") {
        var OlbDeliveryClusterOnMap = (function() {
            var self = {};
            self.popup;
            self.colors = ["f44336","795548","9c27b0","9e9e9e","673ab7","3f51b5","2196f3","03a9f4","cddc39","ffeb3b","ff9800","ff5722","e91e63"];
            self.colorsPerCluster = {};
            self.currentClusterIds = [];
            self.clusterSimples = [];
            self.listClusterWithLatLon = {};
            self.listClusterAll = {};
            self.allCustomer = [];
            self.polyline = [];
            self.markers = [];
            self.currentShipperId = null;

            self.flightPlanCoordinates = {};  //map các tọa độ thuộc đường polyline theo từng route
            self.flightPath = {}; // map các đường polyline
            var shipperDDB;
            var clusterCBB;
            self.map;
            self.init = function () {
                self.initForm();
                self.initElementComplex();
                self.bindEvent();
            };
            self.initForm = function() {
                self.popup = $('#PopupClusterOnMap');
                self.popup.jqxWindow({
                    width : 1200,
                    maxWidth : 1200,
                    height : 545,
                    resizable: false,
                    isModal : true,
                    autoOpen : false,
                    modalOpacity : 0.7,
                    cancelButton : '#cancelViewClusterOnMap',
                    theme : theme,
                    initContent : function() {
                        self.initMap();
                    }
                });
            };

            self.initElementComplex = function (){
                var config = {
                    width: '100%',
                    placeHolder: uiLabelMap.BSClickToChoose,
                    useUrl: true,
                    url: '',
                    key: 'deliveryClusterId',
                    value: 'deliveryClusterCode',
                    dropDownHeight: 200,
                    multiSelect: true,
                };
                clusterCBB = new OlbComboBox($("#deliveryClusterIds"), null, config, []);

                var configPartySalesman = {
                    useUrl: true,
                    root: 'results',
                    widthButton: '100%',
                    datafields: [
                        {name: "partyId", type: "string"},
                        {name: "partyCode", type: "string"},
                        {name: "fullName", type: "string"},
                    ],
                    columns: [
                        {text: "${uiLabelMap.BLShipperCode}", datafield: "partyCode", width: "30%"},
                        {text: "${uiLabelMap.BLShipperName}", datafield: "fullName"},
                    ],
                    url: 'JQGetListShipperCluster',
                    useUtilFunc: true,
                    key: 'partyId',
                    keyCode: 'partyCode',
                    description: ['fullName'],
                    autoCloseDropDown: true,
                    dropDownHorizontalAlignment: 'right',
                    filterable: false,
                    sortable: false,
                    autorowheight: false,
                    showfilterrow: true,
                    selectionmode: 'singlerow',
                };
                shipperDDB = new OlbDropDownButton($("#dropdownShipperClusterMap"), $("#shipperGridClusterMap"), null, configPartySalesman, []);
            };

            self.initMap = function(){
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

                map = new google.maps.Map(document.getElementById('mapCluster'), mapOptions);
                self.map = map;
            };

            self.bindEvent = function(){
                $('#deliveryClusterIds').on('change', function (event) {
                    self.clearComment();
                    self.clearMap();
                    self.currentClusterIds = clusterCBB.getValue();
                    self.allCustomer = [];
                    self.listClusterAll = {};
                    self.listClusterWithLatLon = {};
                    if (OlbCore.isEmpty(self.currentClusterIds)) {
                        return;
                    }
                    //setTimeout để chắc chắn rằng Map đã được load.
                    setTimeout(function(){
                        $.ajax({
                            url: 'getListCustomerPerDeliveryCluster',
                            type: 'POST',
                            data: {deliveryClusterIds: JSON.stringify(self.currentClusterIds)},
                            success: function(data){
                                self.processData(data);
                            }
                        });
                    }, 300);
                });

                $("#dropdownShipperClusterMap").on('close', function (event) {
                    self.currentShipperId = shipperDDB.getValue();
                    if (OlbCore.isNotEmpty(self.currentShipperId)) {
                        var url = 'jqxGeneralServicer?sname=JQGetListDeliveryClusterSimple&pagesize=0' + '&shipperId=' + self.currentShipperId;
                        self.updateSource(url);
                        self.clearComment();
                        self.clearMap();
                    }
                });

                self.popup.on('close', function(){
                    self.clearMap();
                    self.clearComment();
                    shipperDDB.clearAll();
                });
            };

            self.processData = function(data){

                self.allCustomer = data.customers;
                $.each(self.currentClusterIds, function (index, value) {
                    self.listClusterAll[value] = [];
                    self.listClusterWithLatLon[value] = [];
                });

                $.each(self.allCustomer, function (index, value) {
                    $.each(self.currentClusterIds, function (indexClusterId, valueClusterId) {
                        if (value.deliveryClusterId == valueClusterId) {
                            self.listClusterAll[valueClusterId].push(value);
                            if (OlbCore.isNotEmpty(value.latitude) && OlbCore.isNotEmpty(value.longitude)) {
                                self.listClusterWithLatLon[valueClusterId].push(value);
                            }
                        }
                    });
                });
                self.drawMap(self.listClusterWithLatLon);
            };

            self.drawMap = function (data) {
                self.clearMap();
                self.makeColor(data);
                self.setMarker(data);
                /*self.setPolyline(data);*/
                self.setComment(data);
            };

            self.setMarker = function (clusterMap) {
                self.markers = [];
                var bounds = new google.maps.LatLngBounds();
                if (OlbCore.isNotEmpty(clusterMap)){
                    $.each(clusterMap, function (index, value) {
                        //value : 1 mang cac dai ly.
                        $.each(value, function(_,v){
                            var marker = new google.maps.Marker({
                                position: new google.maps.LatLng(v.latitude, v.longitude),
                                draggable: false,
                                icon: OlbDeliveryClusterUtil.createIcon(self.colorsPerCluster[v.deliveryClusterId]),
                                map: self.map,
                                title:v.customerCode
                            });
                            bounds.extend(marker.position);
                            self.markers.push(marker);

                            var html = "<i class='fa fa-tag' style='width: 18px; margin-bottom: 5px'></i>&nbsp" + "${uiLabelMap.BSCustomerId}: " +v.customerCode + "</br>"
                                    + "<i class='fa fa-home'style='width: 18px; margin-bottom: 5px'></i>&nbsp" + "${uiLabelMap.BSCustomerName}: " +v.customerName + "</br>"
                                    + "<i class='fa fa-road'style='width: 18px; margin-bottom: 5px'></i>&nbsp" + "${uiLabelMap.BLDeliveryClusterId}: " + v.deliveryClusterId + "</br>"
                                    + "<i class='fa fa-map-marker'style='width: 18px; margin-bottom: 5px'></i>&nbsp" + v.postalAddressName;
                            var title = v.customerCode;
                            var infoWindow = new google.maps.InfoWindow({content: '<div id="iw" title="'+title+'" style="width:280px!important;color:#000">' + html + '</div>'});
                            marker.addListener('click', function(evt) {
                                infoWindow.open(self.map, marker);
                            });

                        });
                    });
                }
                if (bounds.b.b === 180) {
                    bounds.extend(new google.maps.LatLng(21.0056183, 105.8433475));
                }
                self.map.fitBounds(bounds);
            };

            self.setPolyline = function (clusterMap) {
                self.polyline = [];
                self.flightPlanCoordinates = {};
                self.flightPath = {};
                if (OlbCore.isNotEmpty(clusterMap)){
                    $.each(clusterMap, function (index, value) {
                        //draw polyline
                        var tmpListLatLng = [];
                        var currDeliveryClusterId;
                        $.each(value, function(_,v){
                            tmpListLatLng.push({lat: v.latitude, lng: v.longitude});
                            currDeliveryClusterId = v.deliveryClusterId;
                        });
                        self.flightPlanCoordinates[currDeliveryClusterId] = tmpListLatLng;

                        var colorOfPath = '#'+ self.colorsPerCluster[currDeliveryClusterId];
                        self.flightPath[currDeliveryClusterId] = new google.maps.Polyline({
                            path: self.flightPlanCoordinates[currDeliveryClusterId],
                            geodesic: true,
                            strokeColor: colorOfPath,
                            strokeOpacity: 0.8,
                            strokeWeight: 2,
                            /*icons: [{
                                icon: {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW},
                                offset: '100%',
                                repeat: '50px'
                            }]*/
                        });
                        self.flightPath[currDeliveryClusterId].setMap(self.map);
                    });
                } else {
                    self.clearMap();
                }
            };

            self.setComment = function (clusterMap) {
                $("#commentsContainer").empty();
                if (OlbCore.isNotEmpty(clusterMap)){
                    $.each(self.currentClusterIds, function (index, value) {
                        $.each(self.clusterSimples, function (i, v) {
                            if (v.deliveryClusterId == value) {
                                $("#commentsContainer").append( "<div>" +
                                    "<div style='height: 20px; width: 20px; display: inline-block'>" +
                                        "<svg viewBox='0 0 360 360' ><path fill='#"+self.colorsPerCluster[value]+"' d='" + OlbDeliveryClusterUtil.getSvgIcon() + "'/></svg>" +
                                    "</div>" +
                                    "<span style='display: inline-block; padding-left: 5px'>" +
                                        v.deliveryClusterCode + "" +
                                    "</span>" +
                                "</div>" );
                            }
                        });
                    });
                }
            };

            self.clearComment = function () {
                $("#commentsContainer").empty();
            };

            self.clearMap = function(){
                $.each(self.markers, function (i, value) {
                    value.setMap(null);
                });

                if (OlbCore.isNotEmpty(self.currentClusterIds)) {
                    $.each(self.currentClusterIds, function (i, value) {
                        if (OlbCore.isNotEmpty(self.flightPath[value])) {
                            self.flightPath[value].setMap(null);
                        }
                    });
                }
            };

            self.makeColor = function (data) {
                self.colorsPerCluster = {};
                $.each(self.currentClusterIds, function (i, v) {
                    self.colorsPerCluster[v] = self.colors[i];
                });
            };

            self.open = function() {
                if (self.popup) {
                    self.popup.jqxWindow('open');
                }
                clusterCBB.clearAll();
                var grid = $('#${id}');
                var rowIndexSelected = grid.jqxGrid('getSelectedRowindex');
                var rowData = grid.jqxGrid('getrowdata', rowIndexSelected);
                var url = 'jqxGeneralServicer?sname=JQGetListDeliveryClusterSimple&pagesize=0';
                if (OlbCore.isNotEmpty(rowData)) {
                    shipperDDB.selectItem([rowData.executorId]);
                    url = 'jqxGeneralServicer?sname=JQGetListDeliveryClusterSimple&pagesize=0' + '&shipperId=' + rowData.executorId;
                }
                self.updateSource(url);
                if (OlbCore.isNotEmpty(rowData)) {
                    clusterCBB.selectItem([rowData.deliveryClusterId]);
                }
            };

            self.updateSource = function (url) {
                clusterCBB.updateSource(url,null,function () {
                    //select all cluster for the first time.
                    var items = $("#deliveryClusterIds").jqxComboBox('getItems');
                    self.clusterSimples = [];
                    $.each(items, function (_, v) {
                        self.clusterSimples.push(v.originalItem);
                    });
                });
            };

            self.close = function() {
                self.popup.jqxWindow('close');
            };
            return self;
        })();
    }
    $(function () {
        OlbDeliveryClusterOnMap.init();
    });
</script>
