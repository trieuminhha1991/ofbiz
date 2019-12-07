
if(typeof app !== undefined)
	app.controller('RouteController',function($scope,$rootScope,$controller,$log,$timeout,$state,$stateParams,$ionicHistory,Util,RouteEvent,popupApp,StorageFactory){
		var root = $rootScope;
		var self = $scope;
		
		self.filterRoads = [];
		
		self.self = self;
		
		self.visible = false;
		
		self.btnDel = false;
		
		self.changeBtn = true;
		
		self.isShow = true;
		
		self.isBack = false;
		
		self.isLoadMore = true;
		
		self.isNoResultFound = false;
		
		self.currentPage = 0;
		
		self.title = self.getLabel('add_route');
		
		self.options = {
			autoload : true,
			useDirection : true	
		};
		
		self.msg = {
			content : '',	
			show : false	
		}
		
		self.scale = {
				val : 1,
				offset : 0
		}
		
		self._act = 'other';
		
		self.loadList = self.show 
		
		if(!Util.isValid($stateParams.act) || $stateParams.act == 'add')
			self.newRoute = {days : uiLabelMap.day_of_week};
		else
			self._act = 'update';
		
		var _config = {
				$inject : {
					$controller : $controller
				},
				'event' : [
				           	{
				           		_name : 'enter',
				           		_func : function(){
				           			if(!Util.isValid($stateParams.act))
				        			{
				           				if(_.isEmpty(self.roads))
				           				{
				           					self.getRouteList('init');
				           					self.prepareDays();
				           				}	
				        			}else if($stateParams.act == 'update')
				        				self.title = self.getLabel('UpdateAddress')
				        				
				        			var call = $stateParams.callback;	
				        			var isCallback = $stateParams.isCallback;
				        			if(typeof call == 'function' && isCallback !== true)	
				        			{
				        					self.initDays();
				        					call(self);
				        			}	
				        			var _person = $stateParams.person;
				        			
				        			if($stateParams.route)
				        				self.newRoute = $stateParams.route;
				        			
				        			if(_person != null)
				        				self.newRoute.employeeId = _person.partyId;
				           			
				           		}
				           	},{
				           		_name : 'self.isLoadMore',
				           		type: 'watch',
				           		_func : function(){
				           			self.loadList = self.isLoadMore === true ? function(){} : self.show;
				           		}
				           	}
				          ]	,
				   'behavior' : [
				                 {
				                	 _fname : 'initDays',
				                	 _fbody: function(){
				                		 self.newRoute = {days : uiLabelMap.day_of_week};
				                	 }
				                 },
				                 {
				                	 _fname : 'getRouteList',
				                	 _fbody: function(state,_data){
				                		 
				                		 if(self.searchOptions == 'today')
				                		 {
				                			 	self.roads = [];
				                			 	self.isLoadMore = false;
				                		 }
				                		 
				                		 if(state === 'init')
				             				self.currentPage = 0;
				                		 
				                		 var data = {pagesize:  config.pagesize,pagenum : self.currentPage,supId : config.userDefault.USERNAME};
				                		 
				                		 if(_data)
				                		 		_.extend(data,_data);
				                		 
				                		if(state == 'back')
			                		 	{
				                			if(self.isLoadMore === false)
				                				self.isLoadMore = true;
				                			
				                			if(self.current < 3*config.pagesize)
				                			{
				                				self.$broadcast('scroll.refreshComplete');
				                				self.currentPage =  _.size(self.roads) / config.pagesize;
			                		 			return;
				                			}else
				                			{
				                				self.currentPage = self.current - 3*config.pagesize;
			                					self.$broadcast('scroll.refreshComplete');
				                			}	
				                			
			                		 		_.extend(data,{pagenum : self.currentPage});
			                		 	}
				                		 		
					             		RouteEvent.getRoute(data,function(response){
					             				var isEmpty = false;
					             				if(!_.isEmpty(response) && !_.isEmpty(response.data.listIterator))
					             				{
					             					self.processRoad(response.data.listIterator,state,_data);
					             					self.isShow = false;
					             					if(!angular.isObject(_data))
					             					{
					             						root.totalRoad = response.data.TotalRows;
						             					if(root.totalRoad >= 100)
						             						root.totalRoad = root.totalRoad + "+";
					             					}
					             				}else {
					             					isEmpty = true;
					             					if(!_.isUndefined(_data))
					             						self.processRoad([],'init');
					             					self.isLoadMore = false;
					             				}
					             				
					             				if(state !== 'back')
					             				{
					             					self.$broadcast('scroll.infiniteScrollComplete');
					             				}else	
					             				{
					             					self.$broadcast('scroll.refreshComplete');
					             				}	
					             				
			             						self.currentPage = _.size(self.roads) / config.pagesize;
					             				
					             				self.previous = self.current ? self.current : 0;
					             				
					             				if(!isEmpty)
					             					self.current = self.currentPage > 0 ? (self.currentPage)*config.pagesize : 0;
				             					else{}
			             						
					             				if(_.size(self.roads) >= 3*config.pagesize && state !== 'back')
					             					self.decrementEle();
					             			},self.loadList,self.hide);
				                	 }
				                 },
				                 {
				                	 _fname : 'decrementEle',
				                	 _fbody : function(){
				                		 if(_.isEmpty(self.roads) || _.size(self.roads) < config.pagesize)
				                			 return;
				                		 
				                		 for(var i = 0 ;i < config.pagesize;i++)
				                			 self.roads.splice(0,1);
				                		 
				                		 
				                		 self.isBack = true;
				                	 }
				                 },
				                 {
				                	_fname : 'doRefresh',
				                	_fbody : function(){
				                		if(self.searchOptions !== 'today')
				                			self.getRouteList('back');
				                	}
				                 },
				                 {
				                	 _fname : 'refresh',
				                	 _fbody : function(){
				                		 self.getRouteList('init');
				                	 }
				                 },
				                 {
				                	 _fname : 'processRoad',
				                	 _fbody: function(obj,state,_data){
				                		 
				                			if(state == 'init' || !_.isUndefined(_data))
				                				self.roads = obj;
				                			else
				                			{
				                				if(state == 'back')
				                				{
				                					self.shiftRoads(obj);
				                				}else{
				                					if(obj && _.isArray(obj) && obj.length != 0)
					                				{
					                					angular.forEach(obj,function(value,index){
					                						self.roads.push(value);
					                					})
					                					self.isLoadMore = true;
					                				}else self.isLoadMore = false;
				                				}	
				                			}
				                	 }
				                 },
				                 {
				                	 _fname : 'shiftRoads',
				                	 _fbody : function(obj){
				                		 if(_.isEmpty(self.roads))
				                			 self.roads = obj;
				                		 var temp = [];
				                		 var _size =_.size(self.roads) - 1;
				                		if(_size >= config.pagesize)
			                			{
				                			 for(var i = 0;i < config.pagesize;i++)
				                				 self.roads.splice(_size--,1);
			                			}
				                		
				                		 
				                		 for(var i in self.roads)
				                			 temp.push(self.roads[i]);
				                		 
				                		 self.roads = [];
				                		 
				                		 for(var i in obj)
				                			 self.roads.push(obj[i]);
				                		 
				                		 for(var i in temp)
				                			 self.roads.push(temp[i]);
				                		 
				                	 }
				                 },
				                 {
				                	 _fname : 'getAddressForRoute',
				                	 _fbody: function(){
				                		 /*
				             			 * send request
				             			 * */
				             			function send(s){
				             				RouteEvent.getAddressOfRoute(s,null).then(function(res){
				             					if(_.has(res,'data'))
				             						self.drawPolylines(res.data);
				             					
				             				},function(err){
				             					console.log(err)
				             				});
				             			}
				             			/*
				             			 * prepare data
				             			 * */
				             			if(angular.isUndefined(self.roads) || self.roads.length == 0)
				             			{
				             				/*self.getRouteList();
				             				this();*/
				             				return;
				             			}
				             			
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
				                	 _fname : 'drawPolylines',
				                	 _fbody: function(data){
				                		 self.polylines = [];
				             			var iterator = _.has(data,'listIterator') ? data.listIterator : null;
				             			
				             			if(iterator == null) return;
				             			
				             			for(var k in iterator)
				             			{
				             				if(_.isNull(iterator[k]) || _.isUndefined(iterator[k])) 
				             					continue;
				             				
				             				var _routes = iterator[k]['route_' + k];
				             				
				             				if(_.isEmpty(_routes)) 
				             					continue;
				             				
				             				self.polylines.push(self._child.initOptionsPolyline({
				             					path : (function(_routes){
				             						var _p = [];
				             						for(var i in _routes){
				             							_p.push({
				             								latitude : _routes[i].latitude,
				             								longitude : _routes[i].longitude
				             							})
				             						}
				             						return _p;
				             					}(_routes))
				             				}));
				             			}
				             			
				             			if(!_.isEmpty(self.polylines))
				             				self.render();
				                	 }
				                 },
				                 {
				                	 _fname : 'render',
				                	 _fbody: function(){
				                		self.show();
				             			$timeout(function(){
				             				self._child.polylines = self.polylines;
				             				self._child.focus();
				             				self.isShow = true;	
				             				self.hide();
				             			},500)
				                	 }
				                 },
				                 {
				                	 _fname : 'viewMap',
				                	 _fbody: function(data){
				                		 $state.go('menu.commonmap',{title  : self.getLabel('RouteList')});
				                	 }
				                 },
				                 {
				                	 _fname : 'updateAddress',
				                	 _fbody: function($index){
				                		 
				                			var obj = self._getRoute($index);
				                			
				                			if(!Util.isValid(obj)) 
				                				return;
				                			
				                			$state.go('menu.updateAddress',{route : JSON.stringify(obj)});
				                	 }
				                 },
				                 {
				                	 _fname : 'backList',
				                	 _fbody: function(data){
				                		self.changeBtn = true;
				             			self.isShow = false;
				                	 }
				                 },
				                 {
				                	 _fname : 'loadMore',
				                	 _fbody: function(data){
				                		 self.getRouteList();
				                	 }
				                 },
				                 {
				                	 _fname : 'removeRoute',
				                	 _fbody: function(data){
				                		 self.btnDel = self.btnDel ? false : true;
				                	 }
				                 },
				                 {
				                	 _fname : 'confirmDelRoute',
				                	 _fbody: function(text,index){
				                		 if(_tmpMsg)
				             				_tmpMsg = {
				             						"vi" : text,
				             						"en" : text
				             					}
				             			popupApp.showConfirm(utils.getLabelCustom('delConfirm',_tmpMsg,28),self.getLabel('Notice'),function(res){
				             				if(res === true)
				             					RouteEvent.removeRoute({partyId : text},function(res){
				             						if(Util.isValid(res['data'][config.msg.erm]) || Util.isValid(res['data'][config.msg.erml]))
				             							popupApp.alert('','del failed',null);
				             						else {
				             							self.roads.splice(index,1);
				             							self.showMsg('del_success');
				             						}
				             					},self.show,self.hide)
				             			});
				                	 }
				                 },
				                 {
				                	 _fname : 'delRoute',
				                	 _fbody: function(index){
				                		 if(_.isEmpty(self.roads)) return;
				             			
				             			self.confirmDelRoute(self._getRoute(index).partyId,index);
				                	 }
				                 },
				                 {
				                	 _fname : '_getRoute',
				                	 _fbody: function(index){
				                		 if(!Util.isValid(self.roads)) return ;
				             			
				             			for(var k in self.roads){
				             				if(k == index)
				             				{
				             					var _obj = self.roads[k].partyId ? self.roads[k].partyId : self.roads[k].partyCode;
				             					if(_.isUndefined(_obj) || _.isNull(_obj))
				             						return;
				             					
				             					return self.roads[k];
				             				}	
				             					
				             			}
				                	 }
				                 },
				                 {
				                	 _fname : 'viewRoadDetail',
				                	 _fbody: function(act,index){
				                		if(self.btnDel === true) return;
				             			
				             			var _road = self._getRoute(index);
				             			_road._index = index;
				             			if(Util.isValid(_road))
				             				$state.go('menu.routedetail',{route : JSON.stringify(_road),act : (act ? act : null)})
				                	 }
				                 },
				                 {
				                	 _fname : 'addRoute',
				                	 _fbody: function($type,num,object){
				                		 if($type == 'add' || $type == 'update')
				             			{
				             				if(!isNaN(num))
				             				{
				             					var r = self._getRoute(num);
				             					if(r === undefined && Util.isValid(object)) 
				             						r = object;
				             					
				             					(function(c,t){
				             						for(var k in t['scheduleRoute']){
				             							for(var i in c.days){
				             								if(c.days[i].val == t['scheduleRoute'][k]['scheduleRoute'])
				             								{
				             									c.days[i].isChecked = true;
				             								}	
				             									
				             								else continue;
				             							}
				             							
				             						}
				             						c.employeeId = Util.isValid(t.employeeId) ? t.employeeId[0]['partyCode'] : null;
				             						c.partyCode = t.partyCode ? t.partyCode : null;
				             						c.description = t.description ? t.description : null;
				             						c.groupName = t.groupName ? t.groupName : null
				             						c.partyId = t.partyId ? t.partyId : null;
				             					}(self.newRoute,r));
				             				}else{
				             					if($type == 'update')
				             						self._updateroute();
				             				}
				             				
				             				var isCallback = false;
				             				if($stateParams.callback)
				             				{
				             					isCallback = true;
				             					$stateParams.callback = null;
				             				}	
				             				if(!isCallback)
				             					$state.go('menu.addroute',{isCallback : isCallback,act : $type,route : (!isNaN(num) ? JSON.stringify(self.newRoute) : null)});
				             			}	
				             			else	
				             				self._addroute();
				                	 }
				                 },
				                 {
				                	 _fname : 'prepareDays',
				                	 _fbody: function(act,index){
				                		for(var k in self.newRoute.days)
				             			{
				             				self.newRoute.days[k]['isChecked'] = false;
				             				self.newRoute.days[k]['val'] = self.newRoute.days[k]['en'].toUpperCase();
				             			}
				                	 }
				                 },
				                 {
				                	 _fname : 'prepareScheduleRoute',
				                	 _fbody: function(act,index){
				                		 var arr = [];
				             			var obj;
				             			for(var k in self.newRoute.days){
				             				obj = self.newRoute.days[k];
				             				if(obj.isChecked === true)
				             					arr.push(obj.val);
				             			}
				             			
				             			return JSON.stringify(arr);
				                	 }
				                 },
				                 {
				                	 _fname : '_addroute',
				                	 _fbody: function(){
				                		 self.newRoute.scheduleRoute = self.prepareScheduleRoute();
				             			RouteEvent.createRoute(self.newRoute,function(res){
				             				if(!Util.isValid((res['data'][config.msg.erm])) && !Util.isValid((res['data'][config.msg.erml])))
				             				{
				             					$state.go('menu.routelist');
				             				}else {
				             					popupApp.alert(self.getLabel('addRouteNotSuccess'),'',null);
				             				}
				             			},self.show,self.hide);
				                	 }
				                 },
				                 {
				                	 _fname : '_updateroute',
				                	 _fbody: function(){
				                		self.newRoute.scheduleRoute = self.prepareScheduleRoute();
				             			RouteEvent.updateRoute(self.newRoute,function(res){
				             				if(!Util.isValid((res['data'][config.msg.erm])) && !Util.isValid((res['data'][config.msg.erml])))
				             				{
				             						$state.go('menu.routelist');
				             						$timeout(function(){self.showMsg('update_success');},1000)
				             				}else
				             					popupApp.alert(self.getLabel('updateRouteNotSuccess'),'',null);
				             			},self.show,self.hide);
				                	 }
				                 },
				                 {
				                	 _fname : 'getPerson',
				                	 _fbody: function(){
				                		 StorageFactory.setLocalItem(config.storage.newRoute,self.newRoute);
				             			$state.go('menu.salesmanlist',{act : 'getPerson'});
				                	 }
				                 },
				                 {
				                	 _fname : 'setChecked',
				                	 _fbody: function(index){
				                		 if(!Util.isValid(index))
				             				return;
				             			var checked = self.newRoute.days[index.val].isChecked;
				             			self.newRoute.days[index.val].isChecked = (checked === true ? false : true);
				                	 }
				                 },
				                 {
				                	 _fname : 'refreshScale',
				                	 _fbody: function(){
				                		 self.scale = {
				             					val : 1,
				             					offset : 0
				             			}
				                	 }
				                 },
				                 {
				                	 _fname : 'onScroll',
				                	 _fbody: function(){
				                		 if(self.scale.val < 0.4)
				             			{
				             				return;
				             			}	
				             			
				             			if(self.scale.isScroll)
				             				return;
				             			
				             			self.scale.val -= 0.02;
				             			self.scale.offset += 0.02;
				             			$('.view-road-on-map').css('transform','scale(' + self.scale.val + ')');
				                	 }
				                 },
				                 {
				                	 _fname : 'onRelease',
				                	 _fbody: function(){
				                		var i = 0;
				             			while(i < self.scale.offset)
				             			{
				             				i += 0.02;
				             				self.scale.val += 0.02;
				             				$('.view-road-on-map').css('transform','scale(' + self.scale.val +')');
				             			}	
				             			self.refreshScale();
				                	 }
				                 },
				                 {
				                	 _fname : 'runSearch',
				                	 _fbody: function(filter){
				                		 
				                		 var data = {
				                				 filter : filter,
				                				 pagesize : -1,
				                				 pagenum : 0
				                		 }
				                		 
				                		 self.getRouteList(null,data);
				                	 }
				                 }
			                 ]       
				
				
		}
		
		if(_olbius)
			_olbius.init(self,_config);
		
	})
	