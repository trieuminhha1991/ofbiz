

$(function () {
    console.log("Nam");
    google.maps.event.addDomListener(window, "load", TripDetailRouteObj.init);

});

var TripDetailRouteObj = (function () {
    var init = function () {
        initInputs();
        initElementComplex();
        initEvents();
        $("#omnibox").hide();
        if( isHasOptimalRoute !== ""){
            $("#displayRouteCtn").show();
        }else{
            $("#displayRouteCtn").hide();
        }
    };
    var map;
    var markers = [];
    var optimalRoute = [];
    var flightPlanCoordinates = [];
    var infowindows = [];
    // var placeIdArray = [];
    // var polylines = [];
    // var snappedCoordinates = [];
    var originPoint ;
    var desPoint;
    var waypoints = [];
    var lisCustomer = [];
    var drawingManager;
    var oms;

    var key = "AIzaSyCHu4vQUKFsMnqpjk_HHjIIAU_yejvT5cs";

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
        // add OverlappingMarker
        drawingManager = new google.maps.drawing.DrawingManager({
            drawingControl: true,
            drawingControlOptions: {
                position: google.maps.ControlPosition.BOTTOM_CENTER,
                drawingModes: [google.maps.drawing.OverlayType.RECTANGLE]
            },

            rectangleOptions: {
                strokeColor: '#FF0000',
                strokeOpacity: 0.8,
                strokeWeight: 2,
                fillColor: '#FF0000',
                fillOpacity: 0.1,
                clickable: true,
                editable: false
            }
        });

        map = new google.maps.Map($('#mapRoute')[0], mapOptions);
        // add OverlappingMarker
        drawingManager.setMap(map);
        oms = new OverlappingMarkerSpiderfier(map, {
            markersWontMove: true,   // we promise not to move any markers, allowing optimizations
            markersWontHide: true,   // we promise not to change visibility of any markers, allowing optimizations
            basicFormatEvents: true  // allow the library to skip calculating advanced formatting information
        });
        google.maps.event.addListener(drawingManager, 'rectanglecomplete', function (event) {

            // Get circle center and radius
            var ne = event.getBounds().getNorthEast();
            var sw = event.getBounds().getSouthWest();
            // Remove overlay from map
            event.setMap(null);
            //drawingManager.setDrawingMode(null);

            //remove old rectangle
            if(!checkAddChosseMarkers) {
                rectangles.forEach(function(rectangle) {
                    rectangle.setMap(null)
                })
                rectangles = [];
            }

            // Create circle
            self.createRectangle(ne, sw);
        });
        var bounds = new google.maps.LatLngBounds();
        for ( var i = 0; i< optimalRoute.length; i++) {
            var x = optimalRoute[i];
            var flightPlanCoordinateTemp = [];
            if( i < optimalRoute.length -1){
                var y = optimalRoute[i+1];
                flightPlanCoordinateTemp.push(new google.maps.LatLng( x.latitude,  x.longitude));
                flightPlanCoordinateTemp.push(new google.maps.LatLng( y.latitude,  y.longitude));
                flightPlanCoordinates.push(flightPlanCoordinateTemp);
            }
            var marker;
            if ( typeof (x.facilityId) == "undefined"){
                var phoneNumber = x.phoneNumber;
                // Create marker for Customer
                if ( phoneNumber === null ) phoneNumber = "NaN";
                var html = "<i class='fa fa-tag text-blue' style='width: 18px; margin-bottom: 5px'></i>&nbsp" + uiLabelMap.BSCustomerId + ": " + x.customerId + "</br>"
                    + "<i class='fa fa-home'style='width: 18px; margin-bottom: 5px'></i>&nbsp" + uiLabelMap.BSCustomerName + ": " + x.customerName +"</br>"
                    + "<i class='fa fa-phone'style='width: 18px; margin-bottom: 5px'></i>&nbsp" + uiLabelMap.PhoneNumber + ": " + phoneNumber +"</br>"
                    // + "<i class='fa fa-car hide'style='width: 18px; margin-bottom: 5px'></i>&nbsp" + uiLabelMap.BSSequenceIdCustomer + ": " + x.sequenceNum + "</br>"
                    + "<i class='fa fa-map-marker text-danger'style='width: 18px; margin-bottom: 5px'></i>&nbsp" + x.address;
                var pointTmp = new google.maps.LatLng(x.latitude, x.longitude);
                // Fix: overlapping marker
                // if(markers.length != 0) {
                //     for ( var j=0; j < markers.length; j++) {
                //         var existingMarker = markers[j];
                //         var pos = existingMarker.getPosition();
                //         if (pointTmp.equals(pos)) {
                //             var a = 360.0 / markers.length;
                //             var newLat = pos.lat() + -.0004 * Math.cos((+a*i) / 180 * Math.PI);  //x
                //             var newLng = pos.lng() + -.0008 * Math.sin((+a*i) / 180 * Math.PI);  //Y
                //             pointTmp = new google.maps.LatLng(newLat,newLng);
                //         }
                //     }
                // }
                // add a waypoint to list of waypoint

                if ( i < optimalRoute.length - 1){
                    // duplicate customer will be ignored
                    if( !(($.inArray(x.customerId, lisCustomer)) == -1)){
                        console.log("Lap lai khach hang :" + x.customerName );
                    }else {
                        lisCustomer.push(x.customerId);
                        var waypointTmp = {} ;
                        waypointTmp['location'] = pointTmp ;
                        waypointTmp['stopover'] = true;
                        waypoints.push(waypointTmp);
                        marker = makeMarker(pointTmp);
                        markers.push(marker);
                        oms.addMarker(marker);
                        makeInfoWindow(html, marker);
                    }
                }else {
                    // add the end point
                    desPoint = pointTmp;
                    marker = makeMarker(pointTmp);
                    makeInfoWindow(html, marker);
                }
            }else {
                var htmlFac = "<i class='fa fa-home' style='width: 18px; margin-bottom: 5px'></i>&nbsp" + uiLabelMap.FacilityName + ": " + x.facilityName + "</br>"
                    + "<i class='fa fa-map-marker'style='width: 18px; margin-bottom: 5px'></i>&nbsp" + x.address;
                originPoint = new google.maps.LatLng(x.latitude, x.longitude);
                marker = new google.maps.Marker({
                    position: originPoint,
                    draggable: false,
                    icon: {
                        url: "/logresources/images/warehouse.svg",
                        scaledSize: new google.maps.Size(32, 32),
                    },
                    map: map,
                });
                makeInfoWindow(htmlFac, marker);
            }
            bounds.extend(marker.position);
        }
        map.fitBounds(bounds);
        makeSearchBox();
    }

    var makeMarker = function ( latlong) {
        var marker = new google.maps.Marker({
            position: latlong,
            draggable: true,
            icon: {
                url: createIcon("DC143C" ),
                scaledSize: new google.maps.Size(32, 32)
            },
            map: map,
        });
        return marker;
    };
    var makeInfoWindow= function(html, marker){
        var infowindow = new google.maps.InfoWindow({
            content: html
        });
        // infowindow.open(map, marker);
        marker.addListener('click', function () {
            infowindow.open(map, marker);
        });
        // infowindows.push(infowindow);
    };

    var makeSearchBox = function () {
        var search = document.getElementById('pac-input');
        var autocomplete = new google.maps.places.Autocomplete(search);
        autocomplete.bindTo('bounds', map);
        autocomplete.setTypes(["geocode"]);

        autocomplete.addListener('place_changed', function() {
            var place = autocomplete.getPlace();
            if(!!place.geometry) {
                var lat = place.geometry.location.lat();
                var lng = place.geometry.location.lng();
                map.setCenter(new google.maps.LatLng(lat, lng));
            }
        });

        $('#pac-input').keyup(function(e){
            if(e.keyCode == 13)
            {
                // self.searchAdress();
            }
        });
        var omnibox = document.getElementById('omnibox');
        map.controls[google.maps.ControlPosition.TOP_LEFT].push(omnibox);
    }

    var searchAdress = function () {
        var address = $("#pac-input").val();
        $.ajax({
            url: 'https://maps.googleapis.com/maps/api/geocode/json?address='+address+'&key='+key,
            type: "get",
            success: function(data){
                var location;
                if(data.status==="OK") {
                    location =  data.results[0].geometry.location;
                     map.setCenter(new google.maps.LatLng(location.lat, location.lng));
                } else {
                    alert("Not found location")
                }
            },
            error: function(err){console.log(err)}
        })
    }

    var setPolyline = function () {
        var directionsService = new google.maps.DirectionsService();
        // var polylineOptionsActual = new google.maps.Polyline({
        //     strokeColor: '#FF0000',
        //     strokeOpacity: 1.0,
        //     strokeWeight: 10
        // });
        var directionsDisplay = new google.maps.DirectionsRenderer({suppressMarkers: true});
        directionsDisplay.setMap(map);
        var request = {
            origin: originPoint,
            destination: desPoint,
            travelMode: 'DRIVING',
            waypoints: waypoints,
            optimizeWaypoints: true
        };
        directionsService.route(request, function(response, status) {
            if (status == 'OK') {
                directionsDisplay.setDirections(response);
            }else {
                alert('Canot dispaly directions!');
            }
        });
        // console.log(lisCustomer);
        // console.log(flightPlanCoordinates.length);
        // var lineSymbol = {
        //     path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW
        // };
        // for (var i = 0; i < flightPlanCoordinates.length; i++) {
        //
        //     var flightPath = new google.maps.Polyline({
        //         path: flightPlanCoordinates[i],
        //         geodesic: true,
        //         icons: [{
        //             icon: lineSymbol,
        //             offset: '100%',
        //             repeat: '50px'
        //         }],
        //         strokeColor: '#FF0000',
        //         strokeOpacity: 1.0,
        //         strokeWeight: 2
        //     });
        //     flightPath.setMap(map);
        // }

        // console.log(originPoint.toString());
        // console.log(desPoint.toString());
        console.log();

    }

    var initInputs = function () {
    };

    var initElementComplex = function () {

    };

    var initEvents = function () {
        $("#optimizeRouteShippingTrip").on('click', function () {
            jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureCreate, function(){
                createOptimalRoute();
                if(optimalRoute.length > 0){
                    $("#displayRouteCtn").show();
                    $("#omnibox").show();
                    $("#displayRoute").hide();
                    $("#optimizeRouteShippingTrip").hide();
                    initMap();
                    setPolyline();
                    // $("#displayRouteCtn").display();
                }

            } );
        });
        $("#displayRoute").on('click', function () {
            $("#displayRoute").hide();
            getOptimalRoute(routeTripId);
            $("#omnibox").show();
            initMap();
            setPolyline();
        });
    };

    var createOptimalRoute = function () {
        $.ajax({
            type: 'POST',
            url: 'createOptimalRouteShippingTrip',
            async: false,
            data: {
                shippingTripId: shippingTripId,
            },
            success: function (data) {
                console.log("OK");
                var x = data;
                if ( data["_ERROR_MESSAGE_"]){
                    jOlbUtil.alert.error(uiLabelMap.BLMissedLatLong + ": " + data["_ERROR_MESSAGE_"]);
                }else {
                    getOptimalRoute(data['routeTripId']);
                }

            }
        });
    };


    var getOptimalRoute = function (routeTripId) {
            $.ajax({
                type: 'POST',
                url: 'getOptimalRouteShippingTrip',
                async: false,
                data: {
                    routeTripId: routeTripId,
                },
                success: function (data) {
                    optimalRoute = data['optimalRouteShippingTrip'];
                }
            });

    };

    var createIcon = function(color){
        return "/logresources/images/store.svg";
    }

    return {
        init: init,
        searchAdress: searchAdress,
    }
})();


