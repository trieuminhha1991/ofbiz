<div id="PopupRouteSalesmanOnMap" class='hide'>
    <div>
    ${uiLabelMap.BSRouteOnMap}
    </div>
    <div class="form-window-container">
        <div class='row-fluid'>
            <div class='span10'>
                <div id="mapRouteSalesman" style="height: 500px;"></div>
            </div>
            <div class='span2' style="margin-left: 10px">
                <div class='row-fluid'>
                    <h3>${uiLabelMap.BSNote}</h3>
                    <div id="commentsContainer" style="height: 120px; overflow-y: scroll; overflow-x: hidden">
                    <#--add cmt here-->
                    </div>
                </div>
                <hr>
                <div class='row-fluid'>
                    <label>${uiLabelMap.BSRoute}: </label>
                    <div id="routeIds" class="close-box-custom"></div>
                </div>
            </div>
        </div>
        <div class="form-action">
            <button id="cancelViewRouteOnMap" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
        </div>
    </div>
</div>
<style>
    .custom-control-toolbar {
        margin-top: 6px;
    }
</style>
<script>
    if (typeof (OlbRouteSalesmanOnMap) == "undefined") {
        var OlbRouteSalesmanOnMap = (function() {
            var self = {};
            self.popup;
            self.colors = ["f44336","795548","9c27b0","9e9e9e","673ab7","3f51b5","2196f3","03a9f4","cddc39","ffeb3b","ff9800","ff5722","e91e63"];
            self.colorsPerRoute = {};
            self.currentSalesmanId = null;
            self.currentRouteIds = [];
            self.routeSimples = [];
            self.listRouteWithLatLon = {};
            self.listRouteAll = {};
            self.allCustomer = [];
            self.polyline = [];
            self.markers = [];

            self.flightPlanCoordinates = {};  //map các tọa độ thuộc đường polyline theo từng route
            self.flightPath = {}; // map các đường polyline
            var routeCBB;
            self.map;
            self.init = function () {
                self.initForm();
                self.initElementComplex();
                self.bindEvent();
            };
            self.initForm = function() {
                self.popup = $('#PopupRouteSalesmanOnMap');
                self.popup.jqxWindow({
                    width : 1200,
                    maxWidth : 1200,
                    height : 545,
                    resizable: false,
                    isModal : true,
                    autoOpen : false,
                    modalOpacity : 0.7,
                    cancelButton : '#cancelViewRouteOnMap',
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
                    key: 'routeId',
                    value: 'routeCode',
                    dropDownHeight: 200,
                    multiSelect: true,
                };
                routeCBB = new OlbComboBox($("#routeIds"), null, config, []);
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

                map = new google.maps.Map(document.getElementById('mapRouteSalesman'), mapOptions);
                self.map = map;
            };

            self.bindEvent = function(){
                $('#routeIds').on('change', function (event) {
                    self.clearComment();
                    self.clearMap();
                    self.currentRouteIds = routeCBB.getValue();
                    self.getData();
                });
            };

            self.getData = function(){
                self.allCustomer = [];
                self.listRouteAll = {};
                self.listRouteWithLatLon = {};
                if (OlbCore.isEmpty(self.currentRouteIds)) {
                    return;
                }

                //setTimeout để chắc chắn rằng Map đã được load.
                setTimeout(function(){
                    $.ajax({
                        url: 'getListCustomerPerRoute',
                        type: 'POST',
                        data: {routeIds: JSON.stringify(self.currentRouteIds)},
                        success: function(data){
                            self.processData(data);
                        }
                    });
                }, 300);
            };

            self.processData = function(data){
                self.allCustomer = data.routes;
                //init data with routeId
                $.each(self.currentRouteIds, function (index, value) {
                    self.listRouteAll[value] = [];
                    self.listRouteWithLatLon[value] = [];
                });

                //set data to listRouteWithLatLon, listRouteAll
                $.each(self.allCustomer, function (index, value) {
                    $.each(self.currentRouteIds, function (indexRouteId, valueRouteId) {
                        if (value.routeId == valueRouteId) {
                            self.listRouteAll[valueRouteId].push(value);
                            if (OlbCore.isNotEmpty(value.latitude) && OlbCore.isNotEmpty(value.longitude)) {
                                self.listRouteWithLatLon[valueRouteId].push(value);
                            }
                        }
                    });
                });
                self.drawMap(self.listRouteWithLatLon);
            };

            self.drawMap = function (data) {
                self.clearMap();
                self.makeColor(data);
                self.setMarker(data);
                self.setPolyline(data);
                self.setComment(data);
            };

            self.setMarker = function (routeMap) {
                self.markers = [];
                var bounds = new google.maps.LatLngBounds();
                if (OlbCore.isNotEmpty(routeMap)){
                    $.each(routeMap, function (index, value) {
                        //value : 1 mang cac dai ly.
                        $.each(value, function(_,v){
                            var marker = new google.maps.Marker({
                                position: new google.maps.LatLng(v.latitude, v.longitude),
                                draggable: false,
                                icon: createIcon(self.colorsPerRoute[v.routeId]),
                                map: self.map,
                                title:v.customerCode
                            });
                            bounds.extend(marker.position);
                            self.markers.push(marker);

                            var html = "<i class='fa fa-tag' style='width: 18px; margin-bottom: 5px'></i>&nbsp" + "${uiLabelMap.BSCustomerId}: " +v.customerCode + "</br>"
                                    + "<i class='fa fa-home'style='width: 18px; margin-bottom: 5px'></i>&nbsp" + "${uiLabelMap.BSCustomerName}: " +v.customerName + "</br>"
                                    + "<i class='fa fa-road'style='width: 18px; margin-bottom: 5px'></i>&nbsp" + "${uiLabelMap.BSRoute}: " + v.routeId + "</br>"
                                    + "<i class='fa fa-car'style='width: 18px; margin-bottom: 5px'></i>&nbsp" + "${uiLabelMap.BSSequenceIdCustomer}: " + v.sequenceNum + "</br>"
                                    + "<i class='fa fa-map-marker'style='width: 18px; margin-bottom: 5px'></i>&nbsp" + v.postalAddressName;
                            var title = v.customerCode;

                            var infoWindow = new google.maps.InfoWindow({content: '<div id="iw" title="'+title+'" style="width:280px!important;color:#000">' + html + '</div>'});

                            marker.addListener('click', function(evt) {
                                infoWindow.open(self.map, marker);
                            });

                        });
                    });
                }
                self.map.fitBounds(bounds);
            };

            self.setPolyline = function (routeMap) {
                self.polyline = [];
                self.flightPlanCoordinates = {};
                self.flightPath = {};
                if (OlbCore.isNotEmpty(routeMap)){
                    $.each(routeMap, function (index, value) {
                        //draw polyline
                        var tmpListLatLng = [];
                        var currRouteId;
                        $.each(value, function(_,v){
                            tmpListLatLng.push({lat: v.latitude, lng: v.longitude});
                            currRouteId = v.routeId;
                        });
                        self.flightPlanCoordinates[currRouteId] = tmpListLatLng;

                        var colorOfPath = '#'+ self.colorsPerRoute[currRouteId];
                        self.flightPath[currRouteId] = new google.maps.Polyline({
                            path: self.flightPlanCoordinates[currRouteId],
                            geodesic: true,
                            strokeColor: colorOfPath,
                            strokeOpacity: 0.8,
                            strokeWeight: 2,
                            icons: [{
                                icon: {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW},
                                offset: '100%',
                                repeat: '50px'
                            }]
                        });
                        self.flightPath[currRouteId].setMap(self.map);
                    });
                } else {
                    self.clearMap();
                }
            };

            self.setComment = function (routeMap) {
                $("#commentsContainer").empty();
                if (OlbCore.isNotEmpty(routeMap)) {
                    $.each(self.currentRouteIds, function (index, value) {
                        $.each(self.routeSimples, function (i, v) {
                            if (v.routeId == value) {
                                $("#commentsContainer").append("<div><img src=" + "'" + createIcon(self.colorsPerRoute[value]) + "'>  " + v.routeCode + "</div>");
                            }
                        });
                    });
                }
            };

            self.clearComment = function (){
                $("#commentsContainer").empty();
            }

            self.clearMap = function(){
                $.each(self.markers, function (i, value) {
                    value.setMap(null);
                });

                if (OlbCore.isNotEmpty(self.currentRouteIds)) {
                    $.each(self.currentRouteIds, function (i, value) {
                        if (OlbCore.isNotEmpty(self.flightPath[value])) {
                            self.flightPath[value].setMap(null);
                        }
                    });
                }
            };

            self.makeColor = function (data) {
                self.colorsPerRoute = {};
                $.each(self.currentRouteIds, function (indexRouteId, valueRouteId) {
                    self.colorsPerRoute[valueRouteId] = self.colors[indexRouteId];
                });
            };

            function createIcon(color) {
                return 'https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-pin-container_4x.png,icons/onion/1899-blank-shape_pin_4x.png&highlight=' + color + ',fff&scale=0.8';
            };

            self.open = function(rowData) {
                if (self.popup) {
                    self.popup.jqxWindow('open');
                }
                routeCBB.clearAll();
                if (OlbCore.isNotEmpty(rowData)) {
                    self.currentSalesmanId = rowData.partyId;
                    var url = 'jqxGeneralServicer?sname=JQGetListRouteSimple&pagesize=0' + '&salesmanId=' + self.currentSalesmanId;
                    routeCBB.updateSource(url,null,function () {
                        //select all routes for the first time.
                        var items = $("#routeIds").jqxComboBox('getItems');
                        self.clearMap();
                        self.currentRouteIds = [];
                        self.routeSimples = [];
                        $.each(items, function (_, v) {
                            self.routeSimples.push(v.originalItem);
                        });
                        $.each(items, function (i, v) {
                            self.currentRouteIds.push(v.value);
                        });
                        self.getData();
                    });
                }
            };

            self.close = function() {
                self.popup.jqxWindow('close');
            };
            return self;
        })();
    }
    $(function () {
        OlbRouteSalesmanOnMap.init();
    });
</script>
