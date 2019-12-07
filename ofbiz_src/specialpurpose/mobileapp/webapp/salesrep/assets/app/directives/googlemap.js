app.directive('googlemap', ["$rootScope", "$compile", "$timeout", 'DialogFactory', 'LanguageFactory', 'uiGmapGoogleMapApi', "GPS", 'NumberFactory',
function(root, $compile, $timeout, DialogFactory, LanguageFactory, GoogleMapApi, GPS, NumberFactory) {
	function init(scope, element, attrs) {
		var blue = 'assets/images/blue_marker.png';
		/* init center of map */
		if (!scope.current) {
			scope.current = scope;
		}
		if (!scope.options) {
			scope.options = {
				fixedLocationButton : true
			};
		}
		if (!scope.parent) {
			scope.parent = scope.$parent;
		}
		scope.direction = false;
		scope.googlemap = {};
		scope.selected = {};
		scope.clicked = {};
		if (!scope.parent) {
			scope.parent = scope.$parent;
		}
		var center = {
			latitude : 0,
			longitude : 0
		};
		if (localStorage.lastLocation) {
			center = JSON.parse(localStorage.lastLocation);
		}
		/* init map information */
		scope.map = {
			center : center,
			control : {},
			zoom : 12,
			dragging : false,
			refresh : false,
			bounds : {},
			markers : []
		};
		scope.parent.map = scope.map;
		scope.searchbox = null;
		scope.gmap = scope;
		if (scope.options.autoload) {
			scope.parent.showLoading();
			element.ready(function() {
				scope.init();
			});
		}
		scope.init = function() {
			var mapel = $(".angular-google-map-container");
			if (scope.height && !isNaN(scope.height)) {
				mapel.height(scope.height);
			} else {
				var h = $('.scroll-content').height();
				console.log(h);
				mapel.css('height', '100%');
			}
			GoogleMapApi.then(function(maps) {
				scope.googleVersion = maps.version;
				scope.googlemap = maps;
				maps.visualRefresh = true;
				scope.directionsDisplay = new maps.DirectionsRenderer();
				scope.directionsService = new maps.DirectionsService();
				scope.geocoder = new maps.Geocoder();
				scope.defaultBounds = new google.maps.LatLngBounds(new google.maps.LatLng(40.82148, -73.66450), new google.maps.LatLng(40.66541, -74.31715));
				scope.map.bounds = {
					northeast : {
						latitude : scope.defaultBounds.getNorthEast().lat(),
						longitude : scope.defaultBounds.getNorthEast().lng()
					},
					southwest : {
						latitude : scope.defaultBounds.getSouthWest().lat(),
						longitude : -scope.defaultBounds.getSouthWest().lng()
					}
				};
				scope.initSearchBox({
					bounds : new google.maps.LatLngBounds(scope.defaultBounds.getNorthEast(), scope.defaultBounds.getSouthWest())
				});
				if(config.gps.required){
					GPS.getCurrentLocation(scope.parent);
				}
				scope.parent.hideLoading();
				scope.initSearchClick()
			});
		};
		scope.initSearchClick = function() {
			$timeout(function(){
				var container = document.getElementsByClassName('pac-container');
				console.log(container)
		    	angular.element(container).attr('data-tap-disabled', 'true');
			    angular.element(container).on("click", function(){
			        document.getElementById('pac-input').blur();
			    });	
			}, 1500)
		};
		scope.initSearchBox = function(options) {
			scope.searchbox = {
				template : 'searchbox.tpl.html',
				position : 'top-left',
				options : options,
				events : {
					places_changed : function(searchBox) {
						console.log(searchBox);
						var places = searchBox.getPlaces();
						if (places.length == 0) {
							return;
						}
						// For each place, get the icon, place name, and location.
						var newMarkers = [];
						var bounds = new google.maps.LatLngBounds();
						for (var i = 0, place; place = places[i]; i++) {
							// Create a marker for each place.
							var options = {
								draggable : true
							};
							var marker = scope.createMarker(place.place_id, place.place_id, place.name, place.geometry.location.lat(), place.geometry.location.lng(), blue, options, 'window.tpl.html', place);
							scope.markerClickEvent(marker);
							newMarkers.push(marker);
							bounds.extend(place.geometry.location);
						}

						scope.map.bounds = {
							northeast : {
								latitude : bounds.getNorthEast().lat(),
								longitude : bounds.getNorthEast().lng()
							},
							southwest : {
								latitude : bounds.getSouthWest().lat(),
								longitude : bounds.getSouthWest().lng()
							}
						};
						scope.map.markers = newMarkers;
					}
				}
			};
		};
		scope.clearSearch = function() {
			$('#pac-input').val('');
			// scope.clearAllMarker();
		};
		scope.chooseSelected = function() {
			scope.removeAllInfoWindow();
			scope.selected = {
				marker : scope.marker,
				latitude : scope.clicked.latitude,
				longitude : scope.clicked.longitude
			};
		};

		scope.parent.focus = function(lat, long) {
			var location = {
				latitude : lat,
				longitude : long
			};
			localStorage.setItem(config.storage.lastLocation, JSON.stringify(location));
			scope.marker = scope.createMarker(0, null, '', lat, long, blue);
			if (scope.map.control.refresh) {
				scope.map.control.refresh(location);
			}
		};
		scope.parent.getSelectedPoint = function(isNotFormat) {
			if (scope.map.markers.length == 1) {
				var marker = scope.map.markers[0];
				if (!isNotFormat) {
					scope.selected = {
						marker : marker,
						latitude : NumberFactory.processNumberLocale(marker.latitude),
						longitude : NumberFactory.processNumberLocale(marker.longitude)
					};
				} else {
					scope.selected = {
						marker : marker,
						latitude : marker.latitude,
						longitude : marker.longitude
					};
				}

			}
			return scope.selected;
		};
		scope.parent.getGoogleMap = function() {
			return scope.googlemap;
		};
		scope.parent.processAddress = function(address) {
			var obj = {};
			for (var x in address) {
				var types = address[x].types;
				if (types.length) {
					var type = types[0];
					switch(type) {
					case "country" :
						obj.countryGeoId = address[x].short_name;
						break;
					case "administrative_area_level_1" :
						obj.stateProvinceGeoId = address[x].short_name;
						break;
					case "administrative_area_level_2" :
						obj.districtGeoId = address[x].short_name;
						break;
					case "route" :
						obj.name = address[x].short_name;
						break;
					}
				}
			}
			return obj;
		};
		scope.parent.processAddressComponent = function(address) {
			var obj = {};
			if (!address) {
				return obj;
			}
			var length = address.length;
			if (length > 4) {
				var addr = "";
				var rem = length - 4;
				for (var x = 0; x < rem; x++) {
					addr += address[x].long_name + " ";
				}
				obj.address = addr.trim();
				var end = length - 1;
				var to = end - 3;
				for (var x = end; x > to; x--) {
					if (x == end) {
						obj.countryGeoId = address[x].short_name;
					} else if (x == (end - 1)) {
						obj.stateProvinceGeoId = address[x].short_name;
					} else if (x == (end - 2)) {
						obj.districtGeoId = address[x].short_name;
					} else
						break;
				}
			} else if (length == 4) {
				obj.address = address[0].long_name;
				address.splice(0, 2);
				obj.stateProvinceGeoId = address[0].short_name;
				obj.countryGeoId = address[1].short_name;
			}

			return obj;
		};

		scope.getDirections = function(origin, destination) {
			var request = {
				origin : new scope.googlemap.LatLng(origin.latitude, origin.longitude),
				destination : new scope.googlemap.LatLng(destination.latitude, destination.longitude),
				travelMode : scope.googlemap.DirectionsTravelMode.DRIVING
			};
			scope.directionsService.route(request, function(response, status) {
				if (status === scope.googlemap.DirectionsStatus.OK) {
					scope.directionsDisplay.setDirections(response);
					scope.directionsDisplay.setMap(scope.map.control.getGMap());
					scope.directionsDisplay.setPanel(document.getElementById('directionsList'));
					$("#distance").html(response.routes[0].legs[0].distance.text);
					$("#duration").html(response.routes[0].legs[0].duration.text);
					scope.direction = true;
				} else {
					DialogFactory.buildAlert(scope, '', 'Google route unsuccesfull!');
				}
			});
		};
		scope.parent.setMarkers = function(data) {
			scope.markers = [];
			$timeout(function() {
				scope.map.markers = data;
			}, 500);
		};
		scope.parent.getMarkerPoint = function() {
			return {
				latitude : NumberFactory.processNumberLocale(scope.marker.coords.latitude),
				longitude : NumberFactory.processNumberLocale(scope.marker.coords.longitude),
			};
		};
		scope.clearAllMarker = function() {
			scope.map.markers = [];
		};
		scope.createMarker = function(id, place_id, name, lat, long, icon, options, templateurl, events, place) {
			var marker = {
				id : id,
				coords : {
					latitude : lat,
					longitude : long
				},
				icon : icon,
				latitude : lat,
				longitude : long
			};
			if (place_id) {
				marker.place_id = place_id;
			}
			if (name) {
				marker.name = name;
			} else {
				marker.name = '';
			}
			if (options) {
				marker.options = options;
			}
			if (templateurl) {
				marker.templateurl = templateurl;
			}
			if (events) {
				marker.events = events;
			};
			if (place) {
				marker.place = place;
			}
			return marker;
		};
		scope.removeAllInfoWindow = function() {
			var markers = scope.map.markers;
			var newMarker = new Array();
			for (var x in markers) {
				markers[x].options.visible = false;
				markers[x].show = false;
				var m = scope.createMarker(makeid(10), markers[x].place_id, markers[x].name, markers[x].latitude, markers[x].longitude, blue, {
					draggable : true
				}, markers[x].templateurl, markers[x].events, markers[x].place);
				scope.markerClickEvent(m);
				newMarker.push(m);
			}
			scope.$evalAsync();
			scope.map.markers = newMarker;
		};
		scope.getCurrentLocation = function() {
			return scope.parent.currentLocation;
		};
		scope.updateCurrentLocation = function(location) {
			if (!location) {
				location = scope.getCurrentLocation();
			}
			if (location) {
				var marker = scope.createMarker(makeid(10), null, '', location.latitude, location.longitude, blue, {
					draggable : true
				});
				scope.map.markers = [];
				scope.markerClickEvent(marker);
				scope.map.markers = [marker];
				if (scope.map.control.refresh) {
					$timeout(function() {
						scope.setZoom();
						scope.map.control.refresh({
							latitude : location.latitude,
							longitude : location.longitude
						});
					}, 500)
				}
			}
		};
		scope.setZoom = function(zoom) {
			if (!zoom)
				zoom = config.map.zoom;
			var element = scope.map.control.getGMap();
			element.setZoom(zoom);
		};
		scope.getAddress = function(location, callback) {
			try {
				var ll = new google.maps.LatLng(location.latitude, location.longitude);
				scope.geocoder.geocode({
					'location' : ll
				}, function(results, status) {
					if (status == "OK" && typeof (callback) == 'function') {
						callback(results);
					}
				});
			} catch(e) {
				console.log(e);
			}
		};
		scope.markerClickEvent = function(marker) {
			marker.onClicked = function() {
				marker.options.visible = true;
				marker.show = true;
				var id = marker.place_id;
				var long = marker.longitude;
				var lat = marker.latitude;
				scope.clicked = {
					marker : marker,
					latitude : lat,
					longitude : long
				};
				var str = '<div id="marker-' + id + '">' + marker.name + "<br/>" + "<b>Lat:</b> " + lat;
				str += "<br/> <b>Long:</b> " + long + "<hr class='margin-0 margin-bottom-10'/>";
				str += "<center><button ng-click='chooseSelected(marker)' class='button button-royal margin-right-5'>";
				str += "<i class='icon fa fa-map-pin'></i></button>" + "<button ng-click='onClicked(clicked)' class='button button-positive margin-left-5'>";
				str += "<i class='icon fa fa-motorcycle'></i></button></center></div>";
				marker.title = str;
				setTimeout(function() {
					$compile($('#marker-' + id).contents())(scope);
				}, 500);
			};
		};
		scope.onClicked = function(data) {
			try {
				scope.removeAllInfoWindow();
				DialogFactory.buildConfirm(scope, "", LanguageFactory.getLabel('DirectionHere'), function(dialog) {
					var first = {
						latitude : scope.marker.coords.latitude,
						longitude : scope.marker.coords.longitude,
					};
					var second = {
						latitude : data.latitude,
						longitude : data.longitude
					};
					scope.parent.PointSelected = second;
					scope.getDirections(first, second);
					dialog.close();
				});
			} catch(e) {
				console.log(e);
			}
		};
		scope.onMarkerClicked = function(data) {
			var point = data.coords;
			scope.onClicked(point);
		};
	}

	return {
		templateUrl : 'templates/item/googlemap.htm',
		restrict : 'ACE',
		scope : {
			current : "=",
			map : "=",
			gmap : "=",
			parent : "=",
			marker : '=',
			store : '=',
			height : '=',
			options : '='
		},
		currentLocation : "=",
		link : init
	};
}]);
