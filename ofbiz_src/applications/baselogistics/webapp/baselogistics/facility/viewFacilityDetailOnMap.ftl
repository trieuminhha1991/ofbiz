<div id="jqxwindowAgentDetailOnMap" style="display:none;">
	<div>${uiLabelMap.BLTitleLocaltionFacility}</div>
	<div>
		<div class="pull-left" style="font-size: 16px;"><div class="jqxwindowTitle">${uiLabelMap.BLFacilityName}:</div> <div class="facilityInfo jqxwindowTitle" style="display: inline-block;">${facility.facilityName?if_exists}</div></div>
		<div class="row inline-box">
			<input type="text" class="form-control" id="location-facility" placeholder="latitude, longitude" title="${uiLabelMap.BSLocation}">
			<input id="pac-input" class="form-control" type="text" placeholder="Type city, zip or address here..">
		</div>
		<div id="map" style="height: 470px;overflow-y: hidden;">
		</div>
		
		<div class="form-action row-fluid">
			<div class="span12">
				<button id="button-close" class="btn btn-danger form-action-button pull-right" ><i class="icon-remove"></i>${uiLabelMap.BSClose}</button>
				<button id="button-update" class="btn btn btn-success form-action-button pull-right" onClick="updateFacilityLocation()"><i class="fa-floppy-o"></i>${uiLabelMap.BSUpdateLocation}</button>
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
	
	#location-facility {
		width: 250px;
		height: 25px;
		xmargin-top: 10px;
		border: 1px solid;
		xpadding-left: 10px;
		color: black;
	}
</style>

<script>
    if (!uiLabelMap) var uiLabelMap = {};
    uiLabelMap.updateSuccess = "${StringUtil.wrapString(uiLabelMap.UpdateSuccessfully)}";
    uiLabelMap.updateError = "${StringUtil.wrapString(uiLabelMap.wgupdateerror)}";

    var editable = true;
    <#--<#if facilityInfo.partyId?exists && hasOlbPermission("MODULE", "${permission}", "UPDATE")>-->
        <#--editable = true;-->
    <#--</#if>-->
</script>

<script>
	$(document).ready(function() {
		OlbFacilityDetailOnMap.init();
	});
	
	var infoWindow;
	var map;
	var markers = [];
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
			
		map = new google.maps.Map(document.getElementById('map'), mapOptions);
        var input = document.getElementById('location-facility');
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
	
	function updateFacilityLocation() {
		facility = map._data;
		$.ajax({
			url:"updateLocationFacility",
			type: "post",
			data: {
				"facilityId": facility.facilityId,
				"geoPointId": facility.geoPointId,
                "postalAddressId": facility.postalAddressId,
				"lat": facility.lat,
				"lng": facility.lng,
				"dataSourceId": "GEOPT_GOOGLE"
			},
			success: function(data){
				map._data.latitude = facility.lat;
				map._data.longitude = facility.lng;
				OlbFacilityDetailOnMap.notify("success")
				setTimeout(function(){$("#button-close").click() }, 500);
			},
			error: function(err){OlbFacilityDetailOnMap.notify(err); console.log(err)}
		})
	}
	
	function moveMarker(lat, lng, agent) {
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
		$("#location-facility").val([lat, lng].join(", "));
	
	   	var html = '';
		var pos = marker.getPosition();
		
		html += '<b>Address:</b> ' + agent.address1;
		html += '<br><small>' + '<i class="ti ti-location-pin"></i> Latitude: ' + pos.lat().toString().substr(0, 10) + ' &nbsp; Longitude: ' + pos.lng().toString().substr(0, 10) + '</small><br>';
		
		infoWindow = new google.maps.InfoWindow({content: "<div id='iw' style='width:250px!important;color:#000'>" + html + "</div>"});
			  
		google.maps.event.addListener(marker, 'drag', function (evt) {
			$("#location-facility").val([evt.latLng.lat(), evt.latLng.lng()].join(", "));
			map._data.lat = evt.latLng.lat();
			map._data.lng = evt.latLng.lng();
			html = '';
			pos = marker.getPosition();
			
			html += '<b>Address:</b> ' + agent.address1;
			html += '<br><small>' + '<i class="ti ti-location-pin"></i> Latitude: ' + pos.lat().toString().substr(0, 10) + ' &nbsp; Longitude: ' + pos.lng().toString().substr(0, 10) + '</small><br>';
			
			//map.panTo(pos);
			infoWindow = new google.maps.InfoWindow({content: "<div id='iw' style='width: 250px!important;color:#000'>" + html + "</div>"});
		});
		
		  google.maps.event.clearListeners(map, 'click');
		  map.addListener('click', function(evt) {
		  		infoWindow.open(map, marker);
		  		marker.setPosition(evt.latLng);
				$("#location-facility").val([evt.latLng.lat(), evt.latLng.lng()].join(", "));
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
	
	$('#location-facility').keyup(function(e){
	    if(e.keyCode == 13)
	    {
	    	var local = $('#location-facility').val();
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
	
	var OlbFacilityDetailOnMap = (function() {
	    var init = function () {
            initJqxElements()
            if (!editable) {
                $("#button-update").hide();
            }
        };

		var initJqxElements = function() {
			$("#jqxwindowAgentDetailOnMap").jqxWindow({
				theme: "olbius", maxWidth: 1000, width: 1000, height: 600, resizable: false,  isModal: true, autoOpen: false,
				cancelButton: $("#button-close"), modalOpacity: 0.7
			});
		};
		var open = async function(agent) {
			
			var wtmp = window;
	    	var tmpwidth = $("#jqxwindowAgentDetailOnMap").jqxWindow("width");
	        $("#jqxwindowAgentDetailOnMap").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 40 }});
	    	$("#jqxwindowAgentDetailOnMap").jqxWindow("open");
	    	if(!map) {
		    	initMap();
	    	}
	    	
	    	clearMarkers();
	    	
	    	map._data = agent;
	    	
	    	var lat;
			var lng;
			
			if(!!(agent.longitude||agent.latitude)) {
				lat = agent.latitude;
				lng = agent.longitude;
			} else {
				var location = await geoCode(agent.address1);
				lat = location.lat;
				lng = location.lng;
			}
			
			$("#location-facility").val([lat, lng].join(", "));
			$("#pac-input").val("");
			
			moveMarker(lat, lng, agent);
	    	
		}

        var openMapDetail = function(facilityId, geoPointId, address, latitude, longitude,postalAddressId) {
            var facility = {};
            var lat = null;
            var lon = null;
            facility["address1"] = address;
            facility["facilityId"] = facilityId;
            facility["geoPointId"] = geoPointId;
            facility["postalAddressId"] = postalAddressId;

            if (OlbCore.isNotEmpty(latitude)) {
                lat = Number(latitude.replace(',', '.'));
            }
            if (OlbCore.isNotEmpty(longitude)) {
                lon = Number(longitude.replace(',', '.'));
            }
            facility["latitude"] = lat;
            facility["longitude"] = lon;
            open(facility);
        };

        var notify = function(res) {

            $("#jqxNotificationNestedSlide").jqxNotification("closeLast");
            if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
                var errormes = "";
                res["_ERROR_MESSAGE_"]?errormes=res["_ERROR_MESSAGE_"]:errormes=res["_ERROR_MESSAGE_LIST_"];
                $("#jqxNotificationNestedSlide").jqxNotification({ template: "error"});
                $("#notificationContent").text(errormes);
                $("#jqxNotificationNestedSlide").jqxNotification("open");
            }else {
                console.log(uiLabelMap.updateSuccess);
                $("#jqxNotificationNestedSlide").jqxNotification({ template: "info"});
                $("#notificationContent").html(uiLabelMap.updateSuccess);
                $("#jqxNotificationNestedSlide").jqxNotification("open");
                location.reload();
            }
        };
		return {
			init: init,
            notify: notify,
            openMapDetail: openMapDetail
		};
	})();
	
</script>