if(app !== undefined)
	app.controller('CommonMapController',function($scope,$controller,$stateParams,$window,$timeout,RouteEvent,popupApp){
		
		var self = $scope;
		
		self.self = self;
		
		self.currentPage = 0;
		
		self.roads = [];
		
		self.numberRoads = [{val : 5},{val : 10},{val : 15},{val : 'all'}];
		
		self.options = {
				useDirection : true,
				isLoaded : false
		}
		
		var winHeight = $window.innerHeight;
		
		var _config = {
				$inject : {
					$controller : $controller
				},
				event : [
				         	{
				        	 _name : 'enter',
					         _func : function(){
				        		 $('#displayInfoDirections').css('height',winHeight / 2)
					        	 self.getRouteList();
					         }
			         		}
				         ],
				behavior : [
				            {
			            	_fname : 'getAllAddress',
				            _fbody : function(){
				            	/*
				    			 * send request
				    			 * */
				    			function send(s){
				    				RouteEvent.getAddressOfRoute(s,null).then(function(res){
				    					if(_.has(res,'data'))
				    						self.drawRoute(res.data);
				    					
				    				},function(err){
				    					console.log(err)
				    				});
				    			}
				    			/*
				    			 * prepare data
				    			 * */
				    			if(angular.isUndefined(self.roads) || self.roads.length == 0)
				    				return;
				    			
				    			var jsonArr = [];
				    			var jsonStr = '';
				    			
				    			for(var k in self.roads)
				    			{
				    				var _ele = self.roads[k];
				    				var _key = 'route_' + k;
				    				if(_.isNull(_ele) || _.isUndefined(_ele))
				    					continue;
				    				if( _.has(_ele,'partyId'))
				    				{
				    					var objTmp = {};
				    					objTmp[_key] = (_ele.partyId ? _ele.partyId : _ele.partyCode);
				    					jsonArr.push(objTmp);
				    				}	
				    			}
				    			
				    			if(!_.isEmpty(jsonArr))
				    				jsonStr = JSON.stringify(jsonArr);
				    			
				    			//call send method
				    			send({'routeId[]' : jsonStr});
				            	
				            	}
				            },
				            {
				            	_fname : 'getRouteList',
				            	_fbody : function(num,_data){
				            		var data = {pagesize: (num ? num :  config.pagesize),pagenum : self.currentPage,supId : config.userDefault.USERNAME};
				            		
				            		if(_data)
				            			_.extend(data,_data);
				            		
				        			RouteEvent.getRoute(data,function(response){
				        				if(!_.isEmpty(response) && !_.isEmpty(response.data.listIterator))
				        				{
				        					self.processRoad(response.data.listIterator);
				        					self.getAllAddress();
				        				}else {
				        					
				        				}
				        			},self.show,self.hide);
				            		
				            	}
				            },
				            {
				            	_fname : 'processRoad',
				            	_fbody : function(obj){
				            		if(_.isEmpty(obj))
				            			return;
				            		
				            		self.roads = obj;
				            		
				            	}
				            },
				            {
				            	_fname : 'drawRoute',
				            	_fbody : function(data){
				         			var iterator = _.has(data,'listIterator') ? data.listIterator : null;
				         			
				         			if(iterator == null) return;
				         			
				         			for(var k in iterator)
				         			{
				         				if(_.isNull(iterator[k]) || _.isUndefined(iterator[k])) 
				         					continue;
				         				
				         				var _routes = iterator[k]['route_' + k];
				         				
				         				if(_.isEmpty(_routes)) 
				         					continue;
				         				
				         				var $route_data = (function(_routes){
			         						var _p = [];
			         						for(var i in _routes){
			         							_p.push({
			         								latitude : _routes[i].latitude,
			         								longitude : _routes[i].longitude
			         							})
			         						}
			         						return _p;
			         					}(_routes))
			         					
			         				if(!_.isEmpty($route_data))
					         				self.render($route_data);
				         			
				         			}
				         		
				         			self.options.isLoaded = true;
				            	}
				            },
				            {
				            	_fname : 'render',
				            	_fbody : function(routelist){
				            		$timeout(function(){
				            			self.mapDirections.prototype.getDirection(routelist);
				            		},300)
				            	}
				            },
				            {
						    	_fname : 'openOptions',
						    	_fbody : function(){
						    		
						    		var template = "<div class='setting'>";
							    		template += "<div class='row'><div class='col-50'>Số tuyến</div><div class='col'><select ng-model='options.numberRoad' ng-options='y.val as y.val for y in numberRoads track by y.val'></select></div></div>";
							    		template += "</div>";
							    		
							    		popupApp.showConfirm(template,self.getLabel('filter'),function(res){
							    			if(res === true)
							    			{
							    				self.getRouteList(self.options.numberRoad);
							    			}	
							    		},self);
						    		
						    	}
						    }
			            ]         
		}
		
		if(_olbius)
			_olbius.init(self,_config);
		
	})