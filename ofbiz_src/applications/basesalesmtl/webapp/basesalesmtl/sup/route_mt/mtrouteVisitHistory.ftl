<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/salesmtlresources/js/common/map.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true hasValidator=true hasComboBoxSearchRemote=true hasCore=true/>

<div class='row-fluid' id="routeVisitHistoryContainer" style="margin: 0px">
    <div id="mySidenav" class="sidenav">
        <a href="javascript:void(0)" class="closebtn" onclick="closeNav()"><i class="fas fa-angle-up"></i></a>
        <div class='row-fluid sidenav-content'>
            <div class='row-fluid margin-bottom10'>
                <label>${uiLabelMap.fromDate}: </label>
                <div id="visitHistoryFromDate"></div>
            </div>
            <div class='row-fluid margin-bottom10'>
                <label>${uiLabelMap.thruDate}: </label>
                <div id="visitHistoryThruDate"></div>
            </div>
            <div class='row-fluid margin-bottom10'>
                <label>${uiLabelMap.BSChooseSalesman}: </label>
                <div id="dropdownSalesmanVisitHistoryMap">
                    <div id="salesmanGridVisitHistoryMap"></div>
                </div>
            </div>
            <div class='row-fluid'>
                <a href="javascript:void(0)" class="loadbtn" onclick="OlbRouteVisitHistory.loadData()"><i class="fas fa-search"></i>${uiLabelMap.CommonSearch}</a>
            </div>
        </div>
    </div>
    <a href="javascript:void(0)" title="Menu" class="openbtn" onclick="openNav()"><i class="fas fa-angle-down"></i></a>
    <div id="main">
        <div class='row-fluid'>
            <div class='span12'>
                <div id="mapVisitHistory" style="height: 100px;"></div>
            </div>
        </div>
    </div>
</div>

<div style="position:relative">
    <div id="loader_page_common_visit_history" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 99998;" class="jqx-rc-all jqx-rc-all-olbius">
        <div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
            <div style="float: left;">
                <div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
                <span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.BSLoading}...</span>
            </div>
        </div>
    </div>
</div>
<style>
    .sidenav {
        height: 0px;
        width: 250px;
        position: absolute;
        z-index: 2;
        top: 0;
        right: 0;
        background-color: #438eb9;
        overflow-x: hidden;
        overflow-y: hidden;
        padding-top: 0px;
        transition: 0.5s;
    }
    .sidenav-content {
        margin-top: 50px!important;
        margin-left: 15px!important;
        color: white;
    }
    .sidenav a {
        padding: 8px 8px 8px 32px;
        text-decoration: none;
        font-size: 25px;
        color: #fff;
        display: block;
        transition: 0.3s;
    }
    .sidenav a:hover {
        color: #f1f1f1;
    }
    .sidenav .closebtn {
        position: absolute;
        top: 0px;
        right: 39px;
        border: 1px;
        font-size: 32px;
        margin-left: 50px;
    }
    .sidenav .loadbtn {
        background-color: ghostwhite;
        float: right;
        margin: 10px 23px;
        font-size: 15px;
        color: rgba(67, 131, 180, 0.9);
        padding-left: 10px;
    }
    .sidenav .loadbtn:hover {
        color: #4383b4;
        background-color: white;
    }
    #routeVisitHistoryContainer .openbtn {
        top: 0;
        font-size: 32px;
        margin-left: 50px;
        position: absolute;
        z-index: 1;
        right: 25px;
        border: 1px;
        height: 30px;
        width: 68px;
        padding-top: 10px;
        background-color: rgba(255, 255, 255, 0.5);
        text-align: center;
    }
    #main {
        transition: margin-left .5s;
    }
    @media screen and (max-height: 450px) {
        .sidenav {
            padding-top: 15px;
        }
        .sidenav a {
            font-size: 18px;
        }
    }
</style>
<script>
    function openNav() {
        document.getElementById("mySidenav").style.height = "320px";
    }

    function closeNav() {
        document.getElementById("mySidenav").style.height = "0";
    }

    if (typeof (OlbRouteVisitHistory) == "undefined") {
        var OlbRouteVisitHistory = (function () {
            var self = {};
            self.customers = [];
            self.polyline = [];
            self.markers = [];
            self.flightPath = null;
            self.currentSalesmanId = null;
            var salesmanDDB;
            var validatorVAL;
            self.map;
            self.init = function () {
                self.initElement();
                self.initElementComplex();
                self.bindEvent();
                self.initMap();
                self.initValidateForm();
            };
            self.initElement = function() {
                $("#mapVisitHistory").height($(window).height()*0.8);
                var thruDate = new Date();
                var fromDate = new Date();
                fromDate.setDate(thruDate.getDate() - 1);

                jOlbUtil.dateTimeInput.create("#visitHistoryFromDate", {
                    width: '90%',
                    value: fromDate,
                    formatString: 'dd/MM/yyyy'
                });
                jOlbUtil.dateTimeInput.create("#visitHistoryThruDate", {
                    width: '90%',
                    value: thruDate,
                    formatString: 'dd/MM/yyyy'
                });
            };
            self.initElementComplex = function () {
                var configPartySalesman = {
                    useUrl: true,
                    root: 'results',
                    widthButton: '90%',
                    datafields: [
                        {name: "partyId", type: "string"},
                        {name: "partyCode", type: "string"},
                        {name: "fullName", type: "string"},
                    ],
                    columns: [
                        {text: "${uiLabelMap.BSSalesmanCode}", datafield: "partyCode", width: "30%"},
                        {text: "${uiLabelMap.BSSalesman}", datafield: "fullName"},
                    ],
                    url: 'JQGetListSalesmanManagementMT',
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
                salesmanDDB = new OlbDropDownButton($("#dropdownSalesmanVisitHistoryMap"), $("#salesmanGridVisitHistoryMap"), null, configPartySalesman, []);
            };
            self.initMap = function () {
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

                map = new google.maps.Map(document.getElementById('mapVisitHistory'), mapOptions);
                self.map = map;
                openNav();
            };

            self.bindEvent = function () {
                $("#dropdownSalesmanVisitHistoryMap").on('close', function (event) {
                    self.currentSalesmanId = salesmanDDB.getValue();
                });
            };

            function createIcon(color) {
                return 'https://mt.google.com/vt/icon/name=icons/onion/SHARED-mymaps-pin-container_4x.png,icons/onion/1899-blank-shape_pin_4x.png&highlight=' + color + ',fff&scale=0.8';
            };

            self.loadData = function(){
                if (!validatorVAL.validate()) return false;

                var fromDate = $("#visitHistoryFromDate").jqxDateTimeInput('getDate').getTime();
                var thruDate = $("#visitHistoryThruDate").jqxDateTimeInput('getDate').getTime();
                var salesmanId = self.currentSalesmanId;
                var dataInput = {};
                dataInput["fromDate"] = fromDate;
                dataInput["thruDate"] = thruDate;
                dataInput["salesmanId"] = salesmanId;
                var alertFlag = false;
                if (OlbCore.isEmpty(fromDate) ||OlbCore.isEmpty(thruDate) ||OlbCore.isEmpty(salesmanId)) {
                    alertFlag = true;
                }
                if (alertFlag) {
                    bootbox.dialog("${uiLabelMap.CommonPleaseSelectAllRequiredOptions}", [{
                                "label" : "${uiLabelMap.OK}",
                                "class" : "btn btn-primary standard-bootbox-bt",
                                "icon" : "fa fa-check",
                            }]
                    );
                    return;
                }
                //setTimeout để chắc chắn rằng Map đã được load.
                setTimeout(function(){
                    $.ajax({
                        url: 'getRouteVisitHistory',
                        type: 'POST',
                        data: dataInput,
                        beforeSend: function(){
                            $("#loader_page_common_visit_history").show();
                        },
                        success: function(data){
                            self.processData(data);
                        },
                        error: function(data){
                            alert("Send request is error");
                        },
                        complete: function(data){
                            $("#loader_page_common_visit_history").hide();
                        },
                    });
                }, 300);
            };

            self.processData = function(data){
                self.customers = [];
                self.customers = data.customers;
                self.drawMap(self.customers);
                if (OlbCore.isEmpty(data.customers)){
                    bootbox.dialog("${uiLabelMap.BSNoData}", [{
                                "label" : "${uiLabelMap.OK}",
                                "class" : "btn btn-primary standard-bootbox-bt",
                                "icon" : "fa fa-check",
                            }]
                    );
                    return;
                }
            };

            self.drawMap = function (data) {
                self.clearMap();
                self.setMarker(data);
                self.setPolyline(data);
            };
            self.clearMap = function(){
                $.each(self.markers, function (i, value) {
                    value.setMap(null);
                });
                if (OlbCore.isNotEmpty(self.flightPath)) {
                    self.flightPath.setMap(null);
                }
            };
            self.setMarker = function (data) {
                self.markers = [];
                var bounds = new google.maps.LatLngBounds();
                if (OlbCore.isNotEmpty(data)){
                    $.each(data, function (index, value) {
                        var marker = new google.maps.Marker({
                            position: new google.maps.LatLng(value.customerLatitude, value.customerLongitude),
                            draggable: false,
                            icon: 'http://maps.google.com/mapfiles/kml/pal3/icon56.png',
                            label: (index + 1).toString(),
                            map: self.map,
                            animation: google.maps.Animation.DROP,
                            title:value.customerCode
                        });
                        bounds.extend(marker.position);
                        self.markers.push(marker);
                        var checkIn = "";
                        var checkOut = "";

                        if (OlbCore.isNotEmpty(value.checkInDate)) {
                            checkIn = jOlbUtil.dateTime.formatFullDate(value.checkInDate)
                        };

                        if (OlbCore.isNotEmpty(value.checkOutDate)) {
                            checkOut = jOlbUtil.dateTime.formatFullDate(value.checkOutDate)
                        };

                        var html = "<i class='fa fa-tag' style='width: 18px; margin-bottom: 5px'></i>&nbsp" + "${uiLabelMap.BSCustomerId}: " + value.customerCode + "</br>"
                                + "<i class='fa fa-home' style='width: 18px; margin-bottom: 5px'></i>&nbsp" + "${uiLabelMap.BSCustomerName}: " + value.customerName + "</br>"
                                + "<i class='fa fa-check' style='width: 18px; margin-bottom: 5px'></i>&nbsp" + "${uiLabelMap.BSCheckInTime}: " + checkIn + "</br>"
                                + "<i class='fa fa-check' style='width: 18px; margin-bottom: 5px'></i>&nbsp" + "${uiLabelMap.BSCheckOutTime}: " + checkOut + "</br>";
                        var title = value.customerCode;

                        var infoWindow = new google.maps.InfoWindow({content: '<div id="iw" title="'+title+'" style="width:280px!important;color:#000">' + html + '</div>'});

                        marker.addListener('click', function(evt) {
                            infoWindow.open(self.map, marker);
                        });
                    });
                    self.map.fitBounds(bounds);
                }
            };

            self.setPolyline = function (data) {
                self.polyline = [];
                if (OlbCore.isNotEmpty(data)){
                    var tmpListLatLng = [];
                    $.each(data, function(_,v){
                        tmpListLatLng.push({lat: v.customerLatitude, lng: v.customerLongitude});
                    });
                    var lineSymbol = {
                        path: 'M29.395,0H17.636c-3.117,0-5.643,3.467-5.643,6.584v34.804c0,3.116,2.526,5.644,5.643,5.644h11.759   c3.116,0,5.644-2.527,5.644-5.644V6.584C35.037,3.467,32.511,0,29.395,0z M34.05,14.188v11.665l-2.729,0.351v-4.806L34.05,14.188z    M32.618,10.773c-1.016,3.9-2.219,8.51-2.219,8.51H16.631l-2.222-8.51C14.41,10.773,23.293,7.755,32.618,10.773z M15.741,21.713   v4.492l-2.73-0.349V14.502L15.741,21.713z M13.011,37.938V27.579l2.73,0.343v8.196L13.011,37.938z M14.568,40.882l2.218-3.336   h13.771l2.219,3.336H14.568z M31.321,35.805v-7.872l2.729-0.355v10.048L31.321,35.805',
                        scale: 0.5,
                        fillColor: "#f46362",
                        fillOpacity: 1,
                        strokeWeight: 0.5,
                        offset: '5%',
                        anchor: new google.maps.Point(25, 0),
                    };
                    self.flightPath = new google.maps.Polyline({
                        path: tmpListLatLng,
                        geodesic: true,
                        strokeColor: "#2872b9",
                        strokeOpacity: 1,
                        strokeWeight: 2,
                        icons: [{
                            icon: lineSymbol,
                            offset: '100%'
                        }],
                        map: self.map,
                    });
                    self.animateCar(self.flightPath);
                } else {
                    self.clearMap();
                }
            };

            self.animateCar = function(line) {
                var count = 0;
                window.setInterval(function() {
                    count = (count + 1) % 200;
                    var icons = line.get('icons');
                    icons[0].offset = (count / 2) + '%';
                    line.set('icons', icons);
                }, 60);
            };

            self.initValidateForm = function(){
                var extendRules = [{input: '#visitHistoryFromDate, #visitHistoryThruDate', message: '${uiLabelMap.BSTimeDurationLessThanAMonth}', action: 'valueChanged',
                    rule: function(input, commit){
                        var fromDate = $("#visitHistoryFromDate").jqxDateTimeInput('getDate').getTime();
                        var thruDate = $("#visitHistoryThruDate").jqxDateTimeInput('getDate').getTime();
                        var duration = thruDate - fromDate;
                        if (duration < 2592000000){ // 2592000000 = 30*24*60*60*1000 = a month;
                            return true;
                        }
                        return false;
                    }
                }];
                var mapRules = [
                    {input: '#visitHistoryFromDate', type: 'validInputNotNull'},
                    {input: '#visitHistoryThruDate', type: 'validInputNotNull'},
                    {input: '#visitHistoryFromDate, #visitHistoryThruDate', type: 'validCompareTwoDate', paramId1 : "visitHistoryFromDate", paramId2 : "visitHistoryThruDate"},
                    {input: '#dropdownSalesmanVisitHistoryMap', type: 'validObjectNotNull', objType: 'dropDownButton'},
                ];
                validatorVAL = new OlbValidator($('#routeVisitHistoryContainer'), mapRules, extendRules, {position: 'left'});
            };
            return self;
        })();
    }
    $(function () {
        setTimeout(function () {
            OlbRouteVisitHistory.init();
        }, 300);
    });
</script>
