	
app.directive('googlemap', ['$rootScope','$timeout','GPS','uiGmapGoogleMapApi','popupApp','Util',function($rootScope,$timeout,GPS,GoogleMapApi,popupApp,Util) {
	/**
	 * the function allow connect and isolate to controller use this directive 
	 * @param scope : Object scope of Directive isolate to Object Scope of Controller used
	 * @param element: allow manipulation to DOM tree element
	 *  
	 * */
    var init = function(scope, element, attrs) {
    	/**
    	 * options for instance Google Maps
    	 * @notice this required! 
    	 * */
    	scope.map = {
    			center : { latitude: 45, longitude: -73 },
    			control : {},
    			zoom : 14,
    			dragging : false,
    			refresh : false,
    			bounds : {},
    			markers : [],
    			scrollwheel : true,
    			events : {
    				'dragend'  : function(){
    					
    				},
    				'zoom_changed' : function(){
    					
    				}
    			}
		};
    	
    	scope.placeSelecteds = [];
    	
    	scope.root = $rootScope;
    	
    	/*scope.currentController = scope.currentController ? scope.currentController : 'CommonController';*/
    	
    	/**
    	 * This method init base config for polylines
    	 * @param config
    	 * @return object config for this 
    	 * */
    	scope.initOptionsPolyline = function(config){
    		return {
    			id : utils.makeid(1),
    			path : config.path ? config.path : [],
    			stroke : config.stroke ? config.stroke : { color: utils ? utils.getRandomColor() : '#FF0000', weight: 10,opacity : 1.0},
    			editable : config.editable ? config.editable  : false,
				draggable: config.draggable ? config.draggable : false,
	            geodesic: config.geodesic ? config.geodesic :false,
	            visible: config.visible ? config.visible : true,
        		events : {
					click : function(evt){}
				}
    		}
    	}
    	
    	element.ready(function() {
			scope.init();
		});
    	/**
    	 * This method init GoogleMap API when use angular google maps js
    	 * 
    	 * */
    	scope.init = function(){
    		
    		if(!scope.current)
        		scope.current = scope;
    		
        	scope.current._child = scope;
    		
    		GoogleMapApi.then(function(maps) {
    			
    			if(!scope.options)
            		scope.options = {};
    			
    			if(!scope.map.google)
    				scope.map.google = maps;
    			if(scope.options.useDirection)
    				scope._ImplementsGoogleDirections();
    			
    			
    			if(scope.options.useSearch === true)
    				scope._ImplementGoogleFilter();
    			
    			if(!scope.currentLocation)
				{
    				try {
    					GPS.getCurrentLocation(scope);
    					if(!_.isUndefined(scope.currentLocation))
    						scope.focus();
					} catch (e) {
						console.log(e);
						/*popupApp.alert('','Required GPS,please check',null);*/
					}
				}
    		},function(err){
    			console.log(err)
    		});
    	}
    	/**
    	 * This method create a new marker on maps
    	 * @param all attribute for a marker of maps 
    	 * @return object marker for maps
    	 * */
    	scope.createMarker = function(id, place_id, name, lat, long, icon, options, templateurl, events, place) {
			var marker = {
				id : id,
				coords : {
					latitude : lat, transclude: true,
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
    	/**
    	 * init marker default is point to current location of device use apps
    	 * 
    	 * */
		scope.initMarkerDefault = function(){
			return {
				key : utils.makeid(10),
				coords : scope.currentLocation,
			}
		}
		
		/**
		 * set zoom for maps instance
		 * */
		scope.setZoom = function(zoom){
			if(scope.map.control.getGMap)
				scope.map.control.getGMap().zoom = isNaN(zoom) ? 10 : zoom;
			scope.map.control.refresh();
		}
		/**
		 * This run when use googlemaps Directive
		 * init and focus current location or focus to marker get directions
		 * 
		 * */
    	scope.focus = function(){
    		
    		if(scope.map.control.refresh)
			{
    			scope.marker = scope.initMarkerDefault();
    			var _realWatch = scope.currentLocation;
    			if(scope.polylines && scope.polylines.length > 0)
    				_realWatch = scope.polylines[0]['path'][0];
    			if(Util.isValid(scope.polylines))
    				{
    					var i = 0;
    					while(i < scope.polylines.length)
						{
    						scope.current.mapDirections.prototype.getDirection(scope.polylines[i]['path']);
    						i++;
						}
    				}
    			scope.map.control.refresh(_realWatch);
    			
    			if(scope.options.useSearch)
				{	
					scope.current._autoComplete.prototype.run();
				}
			}
			scope.resizeMap();
    	}
    	
    	scope.resizeMap = function(){
    		$timeout(function(){
    			try {
    				google.maps.event.trigger(scope.map.control.getGMap(), 'resize');
				} catch (e) {
					
				}
    			
    		},200)
    	}
    	
    	scope.renderAddress = function(){
    		var obj = scope.current._autoComplete.prototype;
    		
    		if(_.isEmpty(obj.getPlaces()))
    			return;
    		
    		if(!_.has(scope.current,'_autoComplete'))
    			return;
    		
    		obj.renderDirections(obj.getPlaces());
    	}

    	scope._ImplementGoogleFilter = function(){
    		
    		var _this = scope.current ? scope.current : scope;
    		
    		_this._autoComplete = function(){};
    		
    		var complete ;
    		
    		_this._autoComplete.prototype = {
    				listPlaces : [],
    				getPlaces : function(){
    					return this.listPlaces;
    				},
    				renderDirections : function(places){
    					
    					if(!Util.isValid(places) || places.length == 0)
    						return;
    					if(_.has(_this,'mapDirections'))
    						_this.mapDirections.prototype.getDirection(places);
    					
    				},
    				getInfomations : function(arr,type){
    					for(var k in arr){
    						if(_.isArray(arr[k].types))
    							for(var j in arr[k].types){
    								if(arr[k].types[j] == type)
    								{
    									return 	arr[k]['short_name'] ? arr[k]['short_name'] : arr[k]['long_name'];
    									break;
    								}
    							}
    					}
    				},
    				fillInAddress : function(){
    					var parent  = _this._autoComplete.prototype;
    					var place = complete.getPlace();
    					
    					if(!Util.isValid(place))
    						return;
    					 /*console.log(place.geometry.location.lat())*/
    					
    					scope.setZoom(15);
    					
    					if(!Util.isValid(place.geometry))
    					{
    						/*popupApp.alert(scope.root.getLabel('LocationNotInScope'),'',null)*/
    						return
    					}	
    					
    					scope.placeSelecteds.push({
    							latitude:place.geometry.location.lat(),
    							longitude:place.geometry.location.lng(),
    							address:place.formatted_address,
    							stateProvinceGeoId:parent.getInfomations(place.address_components,'administrative_area_level_1'),
    							districtGeoId:place.name,
    							countryGeoId:parent.getInfomations(place.address_components,'country'),
    							isClearOld:false
    					})
    						
    					parent.listPlaces.push({
    						 latitude : place.geometry.location.lat(),
    						 longitude : place.geometry.location.lng()
    					 });
    					
    					parent.listPlaces = utils ? utils.removeDuplicate(parent.listPlaces) : parent.listPlaces;
    					 
    					/*parent.renderDirections(parent.getPlaces());*/
    				},
    				initAutoComplete : function(){
    					complete = new google.maps.places.Autocomplete((document.getElementById(scope.options.filter.id)),  {types: ['geocode']});
    					complete.addListener('place_changed', this.fillInAddress);
    					$(document).on({
                            'DOMNodeInserted': function() {
                                $('.pac-item, .pac-item span', this).addClass('needsclick');
                            }
                        }, '.pac-container');
    				},	
    				run : function(){
    					this.initAutoComplete();
    				}
    		}
    		
    		_this._autoComplete.prototype.run();
    		
    	}
    	
    	
    	/**
    	 * Default Angular Google Maps not include directions of GoogleMap v3 API
    	 * this method implement google Directions Function for GoogleMap Directive in This Project
    	 * @author : Namdn
    	 * */
    	scope._ImplementsGoogleDirections = function(){
    		
    		var _this = scope.current ? scope.current : scope;
    		_this.mapDirections = function(){};
			
			_this.mapDirections.prototype = {
					getDisplay : function(){return  new google.maps.DirectionsRenderer({
						polylineOptions: {
						      strokeColor: utils ? utils.getRandomColor() : '#FF0000',
				    		  weight : 5
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
							
							if(!Util.isValid(_arr))
								return [];
							var waypoints = [];
							for(var k in _arr){
								var loc = _arr[k];
								
								if(!Util.isValid(loc.latitude) || !Util.isValid(loc.longitude))
									continue;
								
								waypoints.push({
									location  : new google.maps.LatLng(loc.latitude,loc.longitude),
								})
							}
							return waypoints;
						}(arr))
					},
					getRequest : function(points){
						
						var first = points  ? _.first(points)  : []
						var last = points  ? _.last(points)  : []
						var request = {
								origin : new google.maps.LatLng(first.latitude,first.longitude),
								destination : new google.maps.LatLng(last.latitude,last.longitude),
								travelMode: google.maps.DirectionsTravelMode.DRIVING
						}
						
						if(points.length > 2) 
						{
							request.waypoints = this.getWayPoint(points);
							
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
						if(!Util.isValid(points))
							return;
						
						var parent = this;
						this.directionsService.route(this.getRequest(points), function (response, status) {
							if (status === google.maps.DirectionsStatus.OK) {
								var _displayObj = parent.getDisplay();
								_displayObj.setDirections(response);
								_displayObj.setMap(scope.map.control.getGMap());
								_displayObj.setPanel(document.getElementById('displayInfoDirections'));
							} else {
						    	  /*popupApp.alert(scope.root.getLabel('LocationNotInScope'),'',null);*/
						    	  if(points.length >= 1)
						    		  points.splice(points.length - 1,1);
							}
					    });
					}
			}
			
    	}
    	
    }
    	
    return {
        restrict: 'AEC',
        templateUrl : 'templates/common/googlemap.htm',
        scope : {
        	current : "=",
//        	currentController : "=",
        	options : "=?"
        },
        transclude: true,
        link: init
    };
}]);

