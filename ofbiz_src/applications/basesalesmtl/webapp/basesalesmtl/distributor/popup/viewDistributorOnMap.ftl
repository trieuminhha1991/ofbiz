<div id="jqxwindowDistributorOnMap" style="display:none;">
    <div>${uiLabelMap.BSTitleLocaltionDist}</div>
    <div>
        <div class="pull-left" style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.DADistributor}:</div> <div class="distInfo jqxwindowTitle" style="display: inline-block;"></div></div>
        <div class="row inline-box">
            <input type="text" class="form-control" id="location-dist" placeholder="latitude, longitude" title="${uiLabelMap.BSLocation}">
            <input id="pac-input" class="form-control" type="text" placeholder="Type city, zip or address here..">
        </div>
        <div id="map" style="height: 470px;overflow-y: hidden;">
        </div>

        <div class="form-action row-fluid">
            <div class="span12">
                <button id="button-close" class="btn btn-danger form-action-button pull-right" ><i class="icon-remove"></i>${uiLabelMap.BSClose}</button>
                <button id="button-update" class="btn btn btn-success form-action-button pull-right" onClick="updateAgentLocation()"><i class="fa-floppy-o"></i>${uiLabelMap.BSUpdateLocation}</button>
            </div>
        </div>
    </div>
</div>

<style>
    #pac-input {
        width: 250px;
        height: 30px;
        xmargin-top: 10px;
        border: 1px solid;
        xpadding-left: 10px;
    }

    #location-dist {
        width: 250px;
        height: 25px;
        xmargin-top: 10px;
        border: 1px solid;
        xpadding-left: 10px;
        color: black;
    }
</style>

<script>
    $(document).ready(function() {
        DistributorOnMap.init();
    });

    var infoWindow;
    var map;
    var markers = [];
    var key = "AIzaSyCHu4vQUKFsMnqpjk_HHjIIAU_yejvT5cs";  //need modify it
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
        var input = document.getElementById('location-dist');
        map.controls[google.maps.ControlPosition.TOP_RIGHT].push(input);
        var search = document.getElementById('pac-input');
        map.controls[google.maps.ControlPosition.BOTTOM_CENTER].push(search);

        var autocomplete = new google.maps.places.Autocomplete(search);
        autocomplete.bindTo('bounds', map);
        autocomplete.setTypes(["geocode"]);

        autocomplete.addListener('place_changed', function() {
            infoWindow.close();
            var place = autocomplete.getPlace();
            var lat = place.geometry.location.lat();
            var lng = place.geometry.location.lng();
            moveMarker(lat, lng, map._data);
        });
    }

    function clearMarkers() {
        for (var i = 0; i < markers.length; i++) {
            markers[i].setMap(null);
        }
        markers = [];
    }

    function geoCode(address) {
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
                    resolve(location)
                },
                error: function(err){console.log(err)}
            })
        })
    }

    function updateAgentLocation() {
        dist = map._data;
        $.ajax({
            url:"updateLocationDist",
            type: "post",
            data: {
                "partyId": dist.partyId,
                "geoPointId": dist.geoPointId,
                "lat": dist.lat,
                "lng": dist.lng,
                "dataSourceId": "GEOPT_GOOGLE"
            },
            success: function(data){
                map._data.latitude = dist.lat;
                map._data.longitude = dist.lng;
                DistributorSatellite.notify("success")
                setTimeout(function(){$("#button-close").click() }, 500);
            },
            error: function(err){DistributorSatellite.notify(err); console.log(err)}
        })
    }

    function moveMarker(lat, lng, dist) {
        clearMarkers()
        map.setCenter(new google.maps.LatLng(lat, lng));
        marker = new google.maps.Marker({
            position: new google.maps.LatLng(lat, lng),
            draggable: true,
            map: map
        });

        markers.push(marker);

        map._data.lat = lat;
        map._data.lng = lng;
        $("#location-dist").val([lat, lng].join(", "));

        var html = '';
        var pos = marker.getPosition();

        html += '<b>Address:</b> ' + dist.address1;
        html += '<br><small>' + '<i class="ti ti-location-pin"></i> Latitude: ' + pos.lat().toString().substr(0, 10) + ' &nbsp; Longitude: ' + pos.lng().toString().substr(0, 10) + '</small><br>';

        infoWindow = new google.maps.InfoWindow({content: "<div id='iw' style='width:250px!important;color:#000'>" + html + "</div>"});

        google.maps.event.addListener(marker, 'drag', function (evt) {
            $("#location-dist").val([evt.latLng.lat(), evt.latLng.lng()].join(", "));
            map._data.lat = evt.latLng.lat();
            map._data.lng = evt.latLng.lng();
            html = '';
            pos = marker.getPosition();

            html += '<b>Address:</b> ' + dist.address1;
            html += '<br><small>' + '<i class="ti ti-location-pin"></i> Latitude: ' + pos.lat().toString().substr(0, 10) + ' &nbsp; Longitude: ' + pos.lng().toString().substr(0, 10) + '</small><br>';

            //map.panTo(pos);
            infoWindow = new google.maps.InfoWindow({content: "<div id='iw' style='width: 250px!important;color:#000'>" + html + "</div>"});
        });

        google.maps.event.clearListeners(map, 'click');
        map.addListener('click', function(evt) {
            infoWindow.open(map, marker);
            marker.setPosition(evt.latLng);
            $("#location-dist").val([evt.latLng.lat(), evt.latLng.lng()].join(", "));
            map._data.lat = evt.latLng.lat();
            map._data.lng = evt.latLng.lng();
        });

        infoWindow.open(map, marker);
        google.maps.event.addListener(marker, 'dragend', function(e) {
            infoWindow.open(map, marker);
        });
        google.maps.event.addListener(marker, 'dragstart', function(e) {
            infoWindow.close();
        });
    }

    $('#location-dist').keyup(function(e){
        if(e.keyCode == 13)
        {
            var local = $('#location-dist').val();
            try {
                var [lat, lng] = local.split(",").map(function(e) {return parseFloat(e)});
                if(!!(lat&&lng)) {
                    moveMarker(lat, lng, map._data);
                }
            } catch (e) {
                console.log(e);
            }

        }
    });

    var DistributorOnMap = (function() {
        var initJqxElements = function() {
            $("#jqxwindowDistributorOnMap").jqxWindow({
                theme: "olbius", maxWidth: 1000, width: 1000, height: 600, resizable: false,  isModal: true, autoOpen: false,
                cancelButton: $("#button-close"), modalOpacity: 0.7
            });
        };
        var open = async function(dist) {

            var wtmp = window;
            var tmpwidth = $("#jqxwindowDistributorOnMap").jqxWindow("width");
            $("#jqxwindowDistributorOnMap").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
            $("#jqxwindowDistributorOnMap").jqxWindow("open");
            if(!map) {
                initMap();
            }

            clearMarkers();

            map._data = dist;

            var lat;
            var lng;

            if(!!(dist.longitude||dist.latitude)) {
                lat = dist.latitude;
                lng = dist.longitude;
            } else {
                var location = await geoCode(dist.address1);
                lat = location.lat;
                lng = location.lng;
            }

            $("#location-dist").val([lat, lng].join(", "));
            $("#pac-input").val("");

            moveMarker(lat, lng, dist);

        }
        return {
            init: function() {
                initJqxElements();
            },
            open: open
        };
    })();

</script>