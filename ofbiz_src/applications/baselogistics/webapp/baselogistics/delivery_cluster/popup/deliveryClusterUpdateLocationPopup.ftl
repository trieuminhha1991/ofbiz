<div id="WindowPopupCustomerOnMap" class='hide'>
    <div>
    ${uiLabelMap.BSTitleLocaltionAgent}
    </div>
    <div class="form-window-container">
        <div class='row-fluid'>
            <div class='span10'>
                <div id="mapCustomer" style="height: 500px;"></div>
            </div>
            <div class='span2' style="margin-left: 10px">
                <div class='row-fluid'>
                    <h3>${uiLabelMap.BSNote}</h3>
                    <div id="conmentsContainer">
                    <#--add cmt here-->
                    </div>
                </div>
                <hr>
                <div class='row-fluid'>
                    <label>${uiLabelMap.BSLongitude}: </label>
                    <div id="input_longitude"/>
                </div>
                <div class='row-fluid'>
                    <label>${uiLabelMap.BSLatitude}: </label>
                    <div id="input_latitude"/>
                </div>
            </div>
        </div>
        <div class="form-action">
            <button id="cancelViewCustomerOnMap" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
            <button id="updateLocation" class="btn btn btn-success form-action-button pull-right" onClick="OlbDCCustomerOnMap.updateCustomerLocation()"><i class="fa-floppy-o"></i>${uiLabelMap.BSUpdateLocation}</button>
        </div>
    </div>
</div>

<script>
    if (typeof (OlbDCCustomerOnMap) == "undefined") {
        var OlbDCCustomerOnMap = (function() {
            var self = {};
            var key = "AIzaSyCHu4vQUKFsMnqpjk_HHjIIAU_yejvT5cs"; // can cho vao config
            self.popup;
            self.markers = [];
            self.map;
            self.address;
            self.currCustomer;
            self.location = {};
            self.init = function () {
                self.initForm();
                self.initElementComplex();
                self.bindEvent();
            };
            self.initForm = function() {
                self.popup = $('#WindowPopupCustomerOnMap');
                self.popup.jqxWindow({
                    width : 1200,
                    maxWidth : 1200,
                    height : 545,
                    resizable: false,
                    isModal : true,
                    autoOpen : false,
                    modalOpacity : 0.7,
                    cancelButton : '#cancelViewCustomerOnMap',
                    theme : theme,
                    initContent : function() {
                        self.initMap();
                    }
                });

                $("#input_latitude").jqxNumberInput({ width:  180, height: 24,  max : 180, digits: 3, decimalDigits:10, spinButtons: true, min: -180});
                $("#input_longitude").jqxNumberInput({ width:  180, height: 24,  max : 180, digits: 3, decimalDigits:10, spinButtons: true, min: -180});

            };

            self.initElementComplex = function (){
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

                map = new google.maps.Map(document.getElementById('mapCustomer'), mapOptions);
                self.map = map;
            };

            self.bindEvent = function(){
                $("#input_latitude").on("keyup", function(event) {
                    event.preventDefault();
                    if (event.keyCode === 13) {
                        self.location.lat = $("#input_latitude").jqxNumberInput('val');
                        self.drawMap();
                    }
                });
                $("#input_longitude").on("keyup", function(event) {
                    event.preventDefault();
                    if (event.keyCode === 13) {
                        self.location.lng = $("#input_longitude").jqxNumberInput('val');
                        self.drawMap();
                    }
                });
            };

            self.drawMap = function () {
                self.clearMap();
                if (OlbCore.isNotEmpty(self.location) && OlbCore.isNotEmpty(self.map)) {
                    self.map.setZoom(15);
                    $("#input_latitude").jqxNumberInput('val', self.location.lat);
                    $("#input_longitude").jqxNumberInput('val', self.location.lng);

                    var marker = new google.maps.Marker({
                        position: self.location,
                        map: self.map,
                        draggable: true,
                        animation: google.maps.Animation.DROP,
                        title: self.address
                    })
                    self.markers.push(marker);
                    self.map.setCenter(self.location);

                    google.maps.event.addListener(marker, 'dragend', function(marker){
                        var latLng = marker.latLng;
                        $("#input_latitude").jqxNumberInput('val', latLng.lat());
                        $("#input_longitude").jqxNumberInput('val', latLng.lng());
                        self.location.lat = $("#input_latitude").jqxNumberInput('val');
                        self.location.lng = $("#input_longitude").jqxNumberInput('val');
                    });
                }
            };

            self.clearMap = function(){
                $.each(self.markers, function (i, value) {
                    value.setMap(null);
                });
                $("#input_latitude").jqxNumberInput('val',0);
                $("#input_longitude").jqxNumberInput('val',0);
            };

            self.geoCode = function (address){
                self.clearMap();
                setTimeout(function(){
                    self.location = {};
                    return new Promise(function(resolve, reject) {
                        $.ajax({
                            url: 'https://maps.googleapis.com/maps/api/geocode/json?address='+address+'&key='+key,
                            type: "get",
                            success: function(data){
                                var location;
                                if(data.status==="OK") {
                                    location =  data.results[0].geometry.location;
                                } else {
                                    location =  {lat: 21.0024661, lng: 105.8399883}
                                }
                                self.location = location;
                                resolve(location);
                                self.drawMap();
                            },
                            error: function(err){console.log(err)}
                        })
                    });
                }, 300);
            };

            self.generateComment = function(){
                $("#conmentsContainer").html("<p><label>"+ uiLabelMap.BSAddress +": </label>" + self.address + "</p>");
            };

            self.updateCustomerLocation = function(){
                $.ajax({
                    url:"updateLocationAgent",
                    type: "post",
                    data: {
                        "partyId": self.currCustomer.customerId,
                        "geoPointId": self.currCustomer.geoPointId,
                        "lat": self.location.lat,
                        "lng": self.location.lng,
                        "dataSourceId": "GEOPT_GOOGLE"
                    },
                    beforeSend: function(){
                        $("#loader_page_common").show();
                    },
                    success: function(data){
                        jOlbUtil.processResultDataAjax(data, function(data, errorMessage){
                                    $('#jqxNotification').jqxNotification({ template: 'error'});
                                    $("#jqxNotification").html(errorMessage);
                                    $("#jqxNotification").jqxNotification("open");
                                    return false;
                                }, function(){
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
                        self.close();
                        $("#jqxgridViewListCust").jqxGrid("updatebounddata");
                    }
                });
            };

            self.open = function(data) {
                self.address = "";
                self.addressToSearch = "";
                self.currCustomer = {};
                if (OlbCore.isNotEmpty(data)) {
                    if (self.popup) {
                        self.popup.jqxWindow('open');
                    }
                    self.currCustomer = data;
                    var addressMatch = "";
                    if (OlbCore.isNotEmpty(data.postalAddressName)){
                        addressMatch = data.postalAddressName.match(/[^(null)\s,].+/); //exclude null,
                    }
                    if (OlbCore.isNotEmpty(addressMatch)) {
                        self.address = addressMatch[0];
                    };
                    if (locale === "vi") {
                        self.addressToSearch = self.address + ", " + uiLabelMap.BSVietNam;
                    } else {
                        self.addressToSearch = self.address;
                    }

                } else {
                    self.addressToSearch = "Ha Noi, Viet Nam";
                }
                self.generateComment();
                self.geoCode(self.addressToSearch);
            };

            self.close = function() {
                self.popup.jqxWindow('close');
            };
            return self;
        })();
    }

    $(function () {
        OlbDCCustomerOnMap.init();
    });
</script>
