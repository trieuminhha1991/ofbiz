if(app !== undefined)
	app.controller('UpdateAddressController',function($scope,$controller,$stateParams,$window,$timeout,RouteEvent,Util,popupApp,StorageFactory){
		
		var self = $scope;
		
		self.self = self;
		
		self.address = {content : ''};
		
		self.options = {
				useSearch : true,
				useDirection : true,
				filter : {
					id : 'searchAddress'
				}
		}
		
		var config = {
				$inject : {
					$controller : $controller
				},
				'event' : [
				           	{
				           		_name : 'enter',
				           		_func : function(){
				           			var winHeight = $window.innerHeight;
				           			var googleMapContainer = $('.angular-google-map-container')
				           			if(googleMapContainer)
				           			{
				           				googleMapContainer.css('height',winHeight > 100 ? winHeight - 100 : 400);
				           				$timeout(function(){self._child.resizeMap();},500);
				           			}	
				           				
				           			self.route = angular.fromJson($stateParams.route);
				           			if(self.getAddressRoute && self.renderAddress && Util.isValid(self.route))
				           				self.getAddressRoute({'routeId[]' : JSON.stringify([{'route_0' : self.route.partyId}])},self.renderAddress);
				           		}
				           	}
				           ],
				 'behavior' : [{
					 _fname : 'updateAddress',
					 _fbody : function(){
						var selecteds = self._child.placeSelecteds;
					 	
					 	var isSuccess = true;
					 	for(var i = 0 ;i  < selecteds.length;i++){
					 		var data = angular.extend(selecteds[i],{customerId : self.route.partyId,roleTypeId:'ROUTE'});
					 		
					 		RouteEvent.updateLocation(data).then(function(res){
					 			if(!Util.isValid(res.data) || res.data.status != 'success')
					 			{
					 				popupApp.alert(self.getLabel('UpdateAddressFailed'),'',null);
					 				isSuccess = false;
					 			}
					 		})	
					 		
					 		if(!isSuccess)
					 			break;
					 	}
					 	
					 	if(isSuccess)
				 		{
					 			popupApp.alert(self.getLabel('UpdateAddressSuccess'),'',null);
					 			self.mapDirections.prototype.getDirection(_.union(self._autoComplete.prototype.getPlaces(),angular.fromJson(StorageFactory.getLocalItem('points'))));
				 		}
						 	
					 }
				 },
				 {
					 _fname : 'confirmUpdate',
					 _fbody : function(){
						 var selecteds = self._child.placeSelecteds;
						 if(!Util.isValid(self.route) || !Util.isValid(selecteds) || _.isEmpty(selecteds))
						 	{
						 		popupApp.alert(self.getLabel('NotValidAddress'),'',null);
						 		return;
						 	}	
						 
						 popupApp.showConfirm(self.getLabel('ConfirmUpdateRoute'),'',function(res){
							 if(res === true)
								 self.updateAddress();
						 });
					 }
				 }
				 ,{
					 _fname : 'getAddressRoute',
					 _fbody : function(data,callback){
						 if(!Util.isValid(data))
							 return;
						 
						 RouteEvent.getAddressOfRoute(data,null).then(function(res){
								if(_.has(res,'data'))
									if(typeof callback == 'function')
										callback(res);
							},function(err){
								console.log(err)
							});
					 }
				 },{
					 _fname : 'renderAddress',
					 _fbody : function(res){
						 if(!Util.isValid(res.data))
							 return;
						 if(!Util.isValid(res.data.listIterator))
							 return;
						 var route = _.first(res.data.listIterator);
						 
						 var points = [];
						 
						 _.isEmpty(route['route_0']) ? void 0 : (function(p,r){
							 if(_.isArray(r) && !_.isEmpty(r))
							{
								 for(var j in r){
									 p.push({
										 latitude : r[j].latitude,
										 longitude : r[j].longitude
									 })
								 }
							}	 
						 }(points,route['route_0']))
						 
						self.mapDirections.prototype.getDirection(points);
						StorageFactory.setLocalItem('points',JSON.stringify(points));
						 
					 }
				 }] 
		}
		
		if(_olbius)
			_olbius.init(self,config);
		
	})