var Map = (function() {
	var self = {};
	self.element, self.map, self.searchBox, self.searchBoxId, self.defaultBounds, self.currentPlace, self.polyLine;
	self.currentMarkerAddress
	self.autocompleteService, self.placeService, self.searchMarker, self.geocode;
	self.directionsService, self.directionsDisplay, self.cluster;
	self.markers = [];
	self.location = {};
	//self.constFactors = [{"factor":1,"lat":180,"lng":360},{"factor":0.98,"lat":180,"lng":180},{"factor":0.97,"lat":90,"lng":180},{"factor":0.95,"lat":90,"lng":90},{"factor":0.93,"lat":45,"lng":90},{"factor":0.92,"lat":45,"lng":45},{"factor":0.9,"lat":22.5,"lng":45},{"factor":0.88,"lat":22.5,"lng":22.5},{"factor":0.87,"lat":11.25,"lng":22.5},{"factor":0.85,"lat":11.25,"lng":11.25},{"factor":0.83,"lat":5.625,"lng":11.25},{"factor":0.82,"lat":5.625,"lng":5.625},{"factor":0.8,"lat":2.8125,"lng":5.625},{"factor":0.78,"lat":2.8125,"lng":2.8125},{"factor":0.77,"lat":1.40625,"lng":2.8125},{"factor":0.75,"lat":1.40625,"lng":1.40625},{"factor":0.73,"lat":0.703125,"lng":1.40625},{"factor":0.72,"lat":0.703125,"lng":0.703125},{"factor":0.7,"lat":0.3515625,"lng":0.703125},{"factor":0.68,"lat":0.3515625,"lng":0.3515625},{"factor":0.67,"lat":0.17578125,"lng":0.3515625},{"factor":0.65,"lat":0.17578125,"lng":0.17578125},{"factor":0.63,"lat":0.087890625,"lng":0.17578125},{"factor":0.62,"lat":0.087890625,"lng":0.087890625},{"factor":0.6,"lat":0.0439453125,"lng":0.087890625},{"factor":0.58,"lat":0.0439453125,"lng":0.0439453125},{"factor":0.57,"lat":0.02197265625,"lng":0.0439453125},{"factor":0.55,"lat":0.02197265625,"lng":0.02197265625},{"factor":0.53,"lat":0.01098632813,"lng":0.02197265625},{"factor":0.52,"lat":0.01098632813,"lng":0.01098632813},{"factor":0.5,"lat":0.005493164063,"lng":0.01098632813},{"factor":0.48,"lat":0.005493164063,"lng":0.005493164063},{"factor":0.47,"lat":0.002746582031,"lng":0.005493164063},{"factor":0.45,"lat":0.002746582031,"lng":0.002746582031},{"factor":0.43,"lat":0.001373291016,"lng":0.002746582031},{"factor":0.42,"lat":0.001373291016,"lng":0.001373291016},{"factor":0.4,"lat":0.0006866455078,"lng":0.001373291016},{"factor":0.38,"lat":0.0006866455078,"lng":0.0006866455078},{"factor":0.37,"lat":0.0003433227539,"lng":0.0006866455078},{"factor":0.35,"lat":0.0003433227539,"lng":0.0003433227539},{"factor":0.33,"lat":0.000171661377,"lng":0.0003433227539},{"factor":0.32,"lat":0.000171661377,"lng":0.000171661377},{"factor":0.3,"lat":0.00008583068848,"lng":0.000171661377},{"factor":0.28,"lat":0.00008583068848,"lng":0.00008583068848},{"factor":0.27,"lat":0.00004291534424,"lng":0.00008583068848},{"factor":0.25,"lat":0.00004291534424,"lng":0.00004291534424},{"factor":0.23,"lat":0.00002145767212,"lng":0.00004291534424},{"factor":0.22,"lat":0.00002145767212,"lng":0.00002145767212},{"factor":0.2,"lat":0.00001072883606,"lng":0.00002145767212},{"factor":0.18,"lat":0.00001072883606,"lng":0.00001072883606},{"factor":0.17,"lat":0.00000536441803,"lng":0.00001072883606},{"factor":0.15,"lat":0.00000536441803,"lng":0.00000536441803},{"factor":0.13,"lat":0.000002682209015,"lng":0.00000536441803},{"factor":0.12,"lat":0.000002682209015,"lng":0.000002682209015},{"factor":0.1,"lat":0.000001341104507,"lng":0.000002682209015},{"factor":0.08,"lat":0.000001341104507,"lng":0.000001341104507},{"factor":0.07,"lat":6.705522537e-7,"lng":0.000001341104507},{"factor":0.05,"lat":6.705522537e-7,"lng":6.705522537e-7},{"factor":0.03,"lat":3.352761269e-7,"lng":6.705522537e-7},{"factor":0.02,"lat":3.352761269e-7,"lng":3.352761269e-7},{"factor":0,"lat":1.676380634e-7,"lng":3.352761269e-7}];
	self.constFactors = [{"factor":0,"lat":1.676380634e-7,"lng":3.352761269e-7},{"factor":0.02,"lat":3.352761269e-7,"lng":3.352761269e-7},{"factor":0.03,"lat":3.352761269e-7,"lng":6.705522537e-7},{"factor":0.05,"lat":6.705522537e-7,"lng":6.705522537e-7},{"factor":0.07,"lat":6.705522537e-7,"lng":0.000001341104507},{"factor":0.08,"lat":0.000001341104507,"lng":0.000001341104507},{"factor":0.1,"lat":0.000001341104507,"lng":0.000002682209015},{"factor":0.12,"lat":0.000002682209015,"lng":0.000002682209015},{"factor":0.13,"lat":0.000002682209015,"lng":0.00000536441803},{"factor":0.15,"lat":0.00000536441803,"lng":0.00000536441803},{"factor":0.17,"lat":0.00000536441803,"lng":0.00001072883606},{"factor":0.18,"lat":0.00001072883606,"lng":0.00001072883606},{"factor":0.2,"lat":0.00001072883606,"lng":0.00002145767212},{"factor":0.22,"lat":0.00002145767212,"lng":0.00002145767212},{"factor":0.23,"lat":0.00002145767212,"lng":0.00004291534424},{"factor":0.25,"lat":0.00004291534424,"lng":0.00004291534424},{"factor":0.27,"lat":0.00004291534424,"lng":0.00008583068848},{"factor":0.28,"lat":0.00008583068848,"lng":0.00008583068848},{"factor":0.3,"lat":0.00008583068848,"lng":0.000171661377},{"factor":0.32,"lat":0.000171661377,"lng":0.000171661377},{"factor":0.33,"lat":0.000171661377,"lng":0.0003433227539},{"factor":0.35,"lat":0.0003433227539,"lng":0.0003433227539},{"factor":0.37,"lat":0.0003433227539,"lng":0.0006866455078},{"factor":0.38,"lat":0.0006866455078,"lng":0.0006866455078},{"factor":0.4,"lat":0.0006866455078,"lng":0.001373291016},{"factor":0.42,"lat":0.001373291016,"lng":0.001373291016},{"factor":0.43,"lat":0.001373291016,"lng":0.002746582031},{"factor":0.45,"lat":0.002746582031,"lng":0.002746582031},{"factor":0.47,"lat":0.002746582031,"lng":0.005493164063},{"factor":0.48,"lat":0.005493164063,"lng":0.005493164063},{"factor":0.5,"lat":0.005493164063,"lng":0.01098632813},{"factor":0.52,"lat":0.01098632813,"lng":0.01098632813},{"factor":0.53,"lat":0.01098632813,"lng":0.02197265625},{"factor":0.55,"lat":0.02197265625,"lng":0.02197265625},{"factor":0.57,"lat":0.02197265625,"lng":0.0439453125},{"factor":0.58,"lat":0.0439453125,"lng":0.0439453125},{"factor":0.6,"lat":0.0439453125,"lng":0.087890625},{"factor":0.62,"lat":0.087890625,"lng":0.087890625},{"factor":0.63,"lat":0.087890625,"lng":0.17578125},{"factor":0.65,"lat":0.17578125,"lng":0.17578125},{"factor":0.67,"lat":0.17578125,"lng":0.3515625},{"factor":0.68,"lat":0.3515625,"lng":0.3515625},{"factor":0.7,"lat":0.3515625,"lng":0.703125},{"factor":0.72,"lat":0.703125,"lng":0.703125},{"factor":0.73,"lat":0.703125,"lng":1.40625},{"factor":0.75,"lat":1.40625,"lng":1.40625},{"factor":0.77,"lat":1.40625,"lng":2.8125},{"factor":0.78,"lat":2.8125,"lng":2.8125},{"factor":0.8,"lat":2.8125,"lng":5.625},{"factor":0.82,"lat":5.625,"lng":5.625},{"factor":0.83,"lat":5.625,"lng":11.25},{"factor":0.85,"lat":11.25,"lng":11.25},{"factor":0.87,"lat":11.25,"lng":22.5},{"factor":0.88,"lat":22.5,"lng":22.5},{"factor":0.9,"lat":22.5,"lng":45},{"factor":0.92,"lat":45,"lng":45},{"factor":0.93,"lat":45,"lng":90},{"factor":0.95,"lat":90,"lng":90},{"factor":0.97,"lat":90,"lng":180},{"factor":0.98,"lat":180,"lng":180},{"factor":1,"lat":180,"lng":360}];
	self.triggerInit = function() {
		$('body').trigger('mapinit');
	};
	self.checkMapExist = function() {
		if ( typeof (google) === 'object' && typeof (google.maps) === 'object')
			return true;
	};
	self.processAddress = function(address) {
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
	self.initMap = function(obj, option) {
		if (!option)
			option = {};
		if (self.checkMapExist()) {
			var id = obj.attr('id');
			var ran = makeid(10);
			if (id)
				id += ran;
			else
				id = ran;
			self.element = $('<div id="' + id + '" class="googlemap"></div>');
			obj.append(self.element);
			var config = {
				center : option.center ? option.center : {
					lat : 21.0255296,
					lng : 105.8218232
				},
				scrollwheel : option.scrollwheel ? option.scroolwheel : true,
				mapTypeId : option.mapTypeId ? option.mapTypeId : google.maps.MapTypeId.ROADMAP,
				// mapTypeId : google.maps.MapTypeId.TERRAIN,
				zoom : option.zoom ? option.zoom : 12,
				minZoom: 2
			};
			self.map = new google.maps.Map(document.getElementById(id), config);
			self.map.addListener('bounds_changed', function() {
				if (self.searchBox)
					self.searchBox.setBounds(self.map.getBounds());
			});
		}
		self.completeService = new google.maps.places.AutocompleteService();
		self.placeService = new google.maps.places.PlacesService(self.map);
		self.directionsService = new google.maps.DirectionsService;
		self.directionsDisplay = new google.maps.DirectionsRenderer;
		self.geocode = new google.maps.Geocoder();
		self.directionsDisplay.setMap(self.map);
		self.defaultBounds = new google.maps.LatLngBounds();
		// self.cluster = new MarkerClusterer(self.map, []);
	};
	self.resize = function() {
		google.maps.event.trigger(self.map, "resize");
	};
	self.placeFocus = function(place) {
		self.geocode.geocode({
			'address' : place
		}, function(results, status) {
			if (status == google.maps.GeocoderStatus.OK) {
				self.map.setCenter(results[0].geometry.location);
			}
		});
	};
	self.initsearchBox = function(id) {
		var input = document.getElementById(id);
		self.searchBoxId = id;
		self.searchBox = new google.maps.places.Autocomplete(input, {
			types : ['geocode']
		});
		google.maps.event.addListener(self.searchBox, 'place_changed', self.fillInAddress);
	};
	self.getPlacePredictions = function(input, callback) {
		if (self.completeService) {
			self.completeService.getPlacePredictions({
				input : input
			}, callback);
		}
	};
	self.getQueryPredictions = function(input, callback) {
		if (self.completeService) {
			self.completeService.getQueryPredictions({
				input : input
			}, callback);
		}
	};
	self.getPlaceInfo = function(placeId, callback) {
		if (self.placeService) {
			self.placeService.getDetails({
				placeId : placeId
			}, callback);
		}
	};

	self.fillInAddress = function() {
		try {
			// self.clearAllMarker();
			self.currentPlace = self.searchBox.getPlace();
			self.currentMarkerAddress = self.currentPlace;
			if (self.searchMarker) {
				self.removeObject(self.searchMarker);
			}
			self.searchMarker = self.createMarker(self.currentPlace, {
				draggable : true
			});
			self.searchMarker.addListener('dragend', function() {
				$('#' + self.searchBoxId).val('');
				var position = self.searchMarker.position;
				self.getAddress({
					latitude : position.lat(),
					longitude : position.lng()
				}, function(res) {
					if (res && res.length) {
						self.currentMarkerAddress = res[0];
						var addr = "";
						var obj = self.currentMarkerAddress.address_components;
						for (var x = 0; x < 3; x++) {
							addr += " " + obj[x]['long_name'];
						}
						addr = addr.trim();
						self.searchMarker.setTitle(addr);
						if (self.searchMarker.infowindow) {
							self.searchMarker.infowindow.setContent(addr);
						}
					}
				});
			});
			self.searchMarker.addListener('click', function() {
				self.toggleBounce(self.searchMarker);
			});
			if (self.currentPlace.geometry) {
				self.map.setCenter(self.currentPlace.geometry.location);
				self.map.setZoom(15);
			}
		} catch(e) {
			console.log(e);
		}
	};

	self.getAddress = function(location, callback) {
		try {
			var ll = new google.maps.LatLng(location.latitude, location.longitude);
			self.geocode.geocode({
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
	self.getZoom = function() {
		return self.map.getZoom()
	}
	self.toggleBounce = function(marker) {
		if (marker.getAnimation() !== null) {
			marker.setAnimation(null);
		} else {
			marker.setAnimation(google.maps.Animation.BOUNCE);
			setTimeout(function() {
				marker.setAnimation(null);
			}, 3000);
		}
	};
	self.createMarkerCluster = function(markers, options) {
		return new MarkerClusterer(self.map, markers, options);
	};
	self.showMarkerCluster = function(cluster, markers) {
		cluster.addMarkers(markers)
	};
	self.hideMarkerCluster = function(cluster) {
		cluster.clearMarkers();
	};
	self.createMarker = function(place, option) {
		try {
			var icon;
			if (!option || !option.icon) {
				var icon = {
					url : place.icon,
					size : new google.maps.Size(71, 71),
					origin : new google.maps.Point(0, 0),
					anchor : new google.maps.Point(17, 34),
					scaledSize : new google.maps.Size(25, 25)
				};
			} else {
				icon = option.icon;
			}
			var position = option && option.position ? self.createLatLng(option.position) : (place && place.geometry ? place.geometry.location : {});
			var name = option && option.title ? option.title : (place && place.name ? place.name : "");
			var obj = {
				map : self.map,
				icon : icon,
				title : name,
				draggable : option.draggable ? option.draggable : false,
				position : position,
				info : option.info
			};
			return self.makeMaker(obj);
		} catch(e) {
			console.log(e);
			return null;
		}
	};
	self.createIcon = function(url, width, height, sw, sh) {
		var icon = {
			url : url,
			size : new google.maps.Size(width, height),
			origin : new google.maps.Point(0, 0),
			scaledSize : new google.maps.Size( sw ? sw : width, sh ? sh : height)
		}
		return icon;
	};
	self.createMarkerImage = function(url, width, height) {
		var markerImage = new google.maps.MarkerImage(url, new google.maps.Size(width, height));
		return markerImage;
	};
	self.createLatLng = function(position) {
		if (position && position.latitude && position.longitude) {
			return new google.maps.LatLng(position.latitude, position.longitude);
		}
		return {};
	};
	self.makeMaker = function(option) {
		var info = _.clone(option.info);
		delete (option.info);
		var marker = new google.maps.Marker(option);
		self.markers.push(marker);
		if (info && info.title) {
			var infowindow = new google.maps.InfoWindow({
				content : info.title
			});
			marker.infowindow = infowindow;
			marker.addListener('click', function() {
				infowindow.open(self.map, marker);
			});
		}
		return marker;
	};
	self.makeMakerLabel = function(option) {
		if (option && !option.map)
			option.map = self.map;
		return new MarkerWithLabel(option);
	};
	self.createInfoWindow = function(options) {
		return new google.maps.InfoWindow(options);
	};
	self.createMarkerCluster = function(markers, options) {
		if (!self.map)
			return;
		return new MarkerClusterer(self.map, markers, options);
	};
	self.clearMarkerCluster = function() {
		if (self.cluster && _.isFunction(self.cluster.clearMarkers))
			self.cluster.clearMarkers();
	};
	self.removeObject = function(obj) {
		obj.setMap(null);
	};
	self.clearAllMarker = function() {
		for (var i = 0; i < self.markers.length; i++) {
			self.markers[i].setMap(null);
		}
	};
	self.removeMarkers = function(markers) {
		for (var i = 0; i < markers.length; i++) {
			markers[i].setMap(null);
		}
	};
	self.clearMarkerPlace = function() {
		self.currentPlace = null;
		self.currentMarkerAddress = null;
	};
	self.drawPolyline = function(data) {
		if (self.polyLine)
			self.polyLine.setMap(null);
		var flightPlanCoordinates = [];
		if (!data.length) {
			return;
		}
		for (var x in data) {
			flightPlanCoordinates.push({
				lat : data[x].latitude,
				lng : data[x].longitude
			});
		}
		if ( typeof (google.maps) === 'undefined' || typeof (google.maps) === 'undefined') {
			return;
		}
		self.polyLine = new google.maps.Polyline({
			path : flightPlanCoordinates,
			geodesic : true,
			strokeColor : '#FF0000',
			strokeOpacity : 1.0,
			strokeWeight : 2
		});
		self.polyLine.setMap(self.map);
	};
	self.drawPolygon = function(options) {
		var defaultOptions = {
			strokeColor : '#FF0000',
			strokeOpacity : 0.8,
			strokeWeight : 2,
			fillColor : '#FF0000',
			fillOpacity : 0.35
		};
		defaultOptions = _.extend(defaultOptions, options)
		var polygon = new google.maps.Polygon(defaultOptions);
		polygon.setMap(self.map);
		return polygon;
	};
	self.setDirections = function(routes, options, callback) {
		if (routes.length < 2) {
			return;
		}
		var first = _.first(routes);
		var last = _.last(routes);
		var tmp = _.initial(_.rest(routes));
		var waypts = [];
		for (var x in tmp) {
			if (tmp[x].latitude && tmp[x].longitude) {
				var location = new google.maps.LatLng(tmp[x].latitude, tmp[x].longitude);
				waypts.push({
					location : location,
					stopover : true
				});
			}
		}
		var checkboxArray = document.getElementById('waypoints');
		try {
			self.directionsService.route({
				origin : new google.maps.LatLng(first.latitude, first.longitude),
				destination : new google.maps.LatLng(last.latitude, last.longitude),
				waypoints : waypts,
				optimizeWaypoints : true,
				travelMode : google.maps.TravelMode.DRIVING
			}, function(response, status) {
				if (status === google.maps.DirectionsStatus.OK) {
					self.directionsDisplay.setDirections(response);
				}
			});
		} catch(e) {
			console.log(e);
		}
	};
	
	 self.getRandomColor = function(){
		   var letters = '0123456789ABCDEF'.split('');
		   var color = '#';
		   for (var i = 0; i < 6; i++ ) {
		       color += letters[Math.floor(Math.random() * 16)];
		   }
		   return color;
	}
	
	self.makeInstanceDrawMultiDirections = function(){
		
		var mapDirections = function(){};
		
		mapDirections.prototype = {
				collectionDisplay : [],
				getDisplay : function(){return  new google.maps.DirectionsRenderer({
					polylineOptions: {
				      strokeColor:(typeof self.getRandomColor ===  'function' )? self.getRandomColor() : '#FF0000',
		    		  weight : 100,
		    		  suppressMarkers:true
				    }
				})},
				directionsService : new google.maps.DirectionsService(),
				geocoder : new google.maps.Geocoder(),
				directions : {
					origin : 'Halifax, NS',
					destination : 'Boston, MA'
				},
				getWayPoint : function(arr){
					return (function(_arr){
						
						if(_.isEmpty(_arr))
							return [];
						
						var waypoints = [];
						for(var k in _arr){
							var loc = _arr[k];
							
							if(_.isUndefined(loc.latitude) || _.isUndefined(loc.longitude))
								continue;
							
							waypoints.push({
								location  : new google.maps.LatLng(loc.latitude,loc.longitude),
								stopover : true
							})
						}
						return waypoints;
					}(arr))
				},
				getRequest : function(points){
					
					var first = points  ? _.first(points)  : []
					var last = points  ? _.last(points)  : []
					var middle = _.initial(_.rest(points));
					
					var request = {
							origin : new google.maps.LatLng(first.latitude,first.longitude),
							destination : new google.maps.LatLng(last.latitude,last.longitude),
							travelMode: google.maps.DirectionsTravelMode.DRIVING
					}
					
					if(points.length > 2) 
					{
						request.waypoints = this.getWayPoint(middle);
						if(request.waypoints.length > 8)
							request.waypoints = this.setPointLimit(request.waypoints);
						request.optimizeWaypoints = true;
					}
					
					return request
				},
				setPointLimit : function(points){
					var _points = [];	
					for(var i = 0;i < 8;i++){
						_points.push(points[i]);
					}
					return _points;
				},
				getDirection : function(points){
					if(_.isEmpty(points))
						return;
					
					var parent = this;
					this.directionsService.route(this.getRequest(points), function (response, status) {
						if (status === google.maps.DirectionsStatus.OK) {
							var _displayObj = parent.getDisplay();
							_displayObj.setDirections(response);
							_displayObj.setMap(self.map);
							parent.collectionDisplay.push(_displayObj);
							/*_displayObj.setPanel(document.getElementById('displayInfoDirections'));*/
						} else {
					    	  if(points.length >= 1)
					    		  points.splice(points.length - 1,1);
						}
				    });
				},
				clearAllDirection : function(zoom){
					if(!_.isEmpty(this.collectionDisplay))
						for(var d in this.collectionDisplay){
							if(this.collectionDisplay[d] !== undefined)
							{
								this.collectionDisplay[d].set('directions', null);
								delete this.collectionDisplay[d];
								self.map.setZoom(zoom);
							}	
						}
					
					self.clearAllMarker();
				}
		}
		return mapDirections;
	}

	self.clearDirection = function() {
		if (self.directionsDisplay) {
			self.directionsDisplay.set('directions', null);
		}
	};
	self.geolocate = function() {
		if (navigator.geolocation) {
			navigator.geolocation.getCurrentPosition(function(position) {
				var geolocation = {
					lat : position.coords.latitude,
					lng : position.coords.longitude
				};
				var circle = new google.maps.Circle({
					center : geolocation,
					radius : position.coords.accuracy
				});
				self.searchBox.setBounds(circle.getBounds());
			});
		}
	};
	self.getLocation = function(callback) {
		if (navigator.geolocation) {
			self.location = {
				latitude : position.coords.latitude,
				longitude : position.coords.longitude
			};
			navigator.geolocation.getCurrentPosition(callback);
		}
	};
	self.getFactory = function(zoom, maxZoom = 17) {

		if(zoom >= maxZoom || zoom == 22) {
			return 0
		}

		var {ne, sw} = self.getBoundPoint();

		var west = sw.longitude;
		var east = ne.longitude;
		var angleLng = east - west;
		if(angleLng < 0) {
			angleLng += 360;
		}

		

		var northern = sw.latitude;
		var south = ne.latitude;
		var angleLat = northern - south;
		if(angleLat < 0) {
			angleLat += 180;
		}
		angleLat = angleLat/5
		angleLng = angleLng/5
		//find factor
		var factor;
		var index;
		var factors = self.constFactors;
		for(var i = 0; i < factors.length - 1; ++i) {
			var curr = factors[i];
			var next = factors[i + 1];

			if(curr.lng <= angleLng && next.lng >= angleLng  ) {
				if(curr.lng == angleLng && next.lng == angleLng) {
					var distanceA =  Math.abs(curr.lat - angleLat);
					var distanceB =  Math.abs(next.lat - angleLat);
					if(distanceA < distanceB) {
						factor = curr;
						index= i;
					} else {
						factor = next;
						index = i + 1
					}
				} else {
					factor = curr;
					index = i;
				}
				break;
			}
		}

		if(!factor) {
			factor = factors[factors.length - 1]
			index = factors.length - 1;
		}
		//console.log(factor)
		// return factors[index - 2].factor;

		// var arr = 1;
		// var bound = 2;
		// var range = 0.041;
		// arr = arr - range * (zoom - bound);
		// arr = arr < 0 ? 0 : arr;
		// arr = arr > 1 ? 1 : arr;
		// console.log(arr)

		// var a = zoom*3 - 11;
		// var factor = self.constFactors[a];
		// console.log(zoom, factor)
		return factor.factor;
	};
	self.getBoundPoint = function() {
		var bound = self.map.getBounds();
		var ne = bound.getNorthEast();
		var sw = bound.getSouthWest();
		return {
			ne : {
				latitude : ne.lat(),
				longitude : ne.lng()
			},
			sw : {
				latitude : sw.lat(),
				longitude : sw.lng()
			}
		};
	};
	return self;
});
