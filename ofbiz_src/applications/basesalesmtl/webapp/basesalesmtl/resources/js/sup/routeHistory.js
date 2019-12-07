
function history(){};
history.optionsMap = {zoom : 12};
history.mapElement = function(){return $('#googlemap').length > 0 ? $('#googlemap') : null};
history.prototype = {
		setMyMap : function(){this.mymap = Map();},
		getMyMap : function(){return this.mymap},
		processRequest : {
			parent : this,
			'getListRouteHistory' : function(parent,data){
				var _re = $.ajax({
					url:'getListRouteHistory',
					type  :'POST',
					data : (data !== undefined ? data : {}),
					datatype : 'json',
					success : function(res){parent.renderRoute(res.listroute)},
					error : function(){}
				})
				
			}
		},
		createInstanceMultiDirection : function(){
			var instance = this.getMyMap().makeInstanceDrawMultiDirections();
			this.multiDirections  = new instance();
		},
		renderStore : function(data){
			this.drawStoreMarker(data);
			
			for(var x in data){
				var value = data[x].address;
				if(value){
					var str = "";
					if(value.address1){
						str = value.address1;
					}
					if(value.wardGeoName){
						str += ' ' + value.wardGeoName;
					}
					if(value.districtGeoName){
						str += ' ' + value.districtGeoName;
					}
					if(value.stateProvinceGeoName){
						str +=  ' ' + value.stateProvinceGeoName;
					}
					(function(x,m){
						var partyId = data[x].partyIdTo;
						var contactMechId = value.contactMechId;
						m.getMyMap().getPlacePredictions(str, function(predictions, status){
							if(status == google.maps.places.PlacesServiceStatus.OK)
								m.processAutoCustomerLocation(predictions, partyId, contactMechId);
						});
					})(x,this);
				}
			};
		},
		processAutoCustomerLocation : function(predictions, partyId, contactMechId){
			var parent = this;
			if(predictions && predictions.length){
				var place = predictions[0];
				var place_id = place.place_id;
				if(typeof(Map) != 'undefined' && this.getMyMap().checkMapExist()){
					parent.getMyMap().getPlaceInfo(place_id, function(info, status){
						if(status == google.maps.places.PlacesServiceStatus.OK){
							parent.renderExactCustomerLocation(info, partyId, contactMechId);
						}
					});

				}
			}
		},
		renderExactCustomerLocation : function(info, partyId, contactMechId){
			var position = info.geometry;
			var obj = this.getStoreMarkerInfo(position.location, info.formatted_address);
			this.getMyMap().makeMaker(obj);
		},
		drawStoreMarker : function(data) {
			for (var x in data) {
				var position = {
					lat : data[x].latitude,
					lng : data[x].longitude
				};
				var title = "<i class='fa fa-home'></i>&nbsp;" + data[x].groupName + "</br>"
						+ "<i class='fa fa-map-marker'></i>&nbsp;" + data[x].address1;
				var tt = data[x].groupName + " - " + data[x].address1;
				var obj = this.getStoreMarkerInfo(position, tt, title);
				this.getMyMap().makeMaker(obj);
			}
		},
		renderRoute : function(r){
			if(_.isUndefined(r))
				return;
			
			for(var k in r){
				var addrs = [];
				var data = _.values(r[k]);
				data = data[0];
				if(data === undefined || data == null)
					continue;
				
				for(var x in data){
					
					if(_.has(data[x],'customerInRoute'))
						this.renderStore(data[x]['customerInRoute']);
					
					if(data[x].address1 && data[x].latitude && data[x].longitude){
						var addr = data[x].address1;
						if(data[x].wardGeo){
							addr += " " + data[x].wardGeo;
						}
						if(data[x].districtGeo){
							addr += " " + data[x].districtGeo;
						}
						if(data[x].stateProvinceGeo){
							addr += " " + data[x].stateProvinceGeo;
						}
						addrs.push({
							address : addr,
							latitude: data[x].latitude,
							longitude: data[x].longitude
						});
					}
					
				}
				
				this.multiDirections.getDirection(addrs);
				this.getMyMap().map.setZoom(12);
			}
			
		},
		bindEvent : function(){
			var parent = this;
			var allInput = $('input[name=customertype]');
			var input = allInput[0];
			if(input !== undefined)
				$(input).click(function(){
					var state = $(input).prop('checked');
					if(state !== true)
						parent.multiDirections.clearAllDirection(history.optionsMap.zoom);
					else
						parent.processRequest.getListRouteHistory(parent);
					
					allInput.prop('checked',state)
				});
			
			$('#refreshMap').click(function(){
				var arr = [];
				for(var i = 0;i < allInput.length;i++){
					var e = $(allInput[i]);
					var s = e.prop('checked');
					if(s === true)
						arr.push({i : e.prop('value')});
				}
				
				parent.multiDirections.clearAllDirection(history.optionsMap.zoom);
				parent.processRequest.getListRouteHistory(parent,{partyId : JSON.stringify(arr)});
			})
		},
		getStoreMarkerInfo : function(position, title, infoTitle){
			var arr = ['blue_marker.png', 'dark_marker.png', 'red_marker.png'];
			var img = _.sample(arr);
			return {
				map : this.getMyMap().map,
				icon : {
					url : '/aceadmin/assets/images/' + img,
					size : new google.maps.Size(71, 71),
					origin : new google.maps.Point(0, 0),
					anchor : new google.maps.Point(17, 34),
					scaledSize : new google.maps.Size(25, 25)
				},
				position : position,
				info : {
					title : infoTitle
				},
				title : title
			}
		},
		init : function(){
			this.setMyMap();
			this.createInstanceMultiDirection();
			
			if(this.getMyMap() === undefined ) 
				return;
			
			this.bindEvent();
			
			return (function(h,m){
				var e;
				try {	
					e = h.mapElement.call(h);
					if( e == null)
						return;
				} catch (e) {
					throw new TypeError('element init maps not exists!')
				}
				
				if(typeof m === 'object')
					m.initMap(e,h.optionsMap);
				
			}(history,this.getMyMap()))
		}
}

$(document).ready(function(){new history().init();})

