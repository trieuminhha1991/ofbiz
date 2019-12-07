if(app !== undefined)
	app.controller('RouteDetailController',function($scope,$stateParams,$controller,$ionicHistory,$log,$state,Util){
		var self = $scope;	
		
		self.newRoute = {days : uiLabelMap.day_of_week};
		
		var atr = {
				$routeDetail : {}
		}
		
		var config = {
				atr : atr,
				$inject : {
					$controller : $controller
				},
				'event' : [
				           	{
				           		_name : 'enter',
				           		_func : function(){
				           			
				           			self.$routeDetail = angular.fromJson($stateParams.route);
				           			
				           			self.procesSchedule();
				           			
				           			var t = $stateParams.type;
				           			
				           			if(!Util.isValid(t) || t == 'view')
				           				self.isUpdateNormal = true;
				           			else
				           				self.isUpdateNormal = false;
				           		}
				           	}
				           ],
				 'behavior' : [
				               	{
				               		_fname : 'prepare',
				               		_fbody : function(_this){
				               			if(_.has(_this,'addRoute'))
				               				_this.addRoute('update',self.$routeDetail._index,self.$routeDetail);
				               		}
				               	},
				               	{
				               		_fname : 'updateAddress',
				               		_fbody : function(road){
				               			if(!Util.isValid(road)) 
				            				return;
				               			$state.go('menu.updateAddress',{route : JSON.stringify(road)});
				               		}
				               	},
				               	{
				               		_fname : 'updateAddressNormal',
				               		_fbody : function(){
				               			if(_.isEmpty(self.$routeDetail))
				               				return;
				               			
				               			$state.go('menu.addroute',{act : 'update',callback : self.prepare});
				               		}
				               		
				               	},
				               	{
				               		_fname : 'procesSchedule',
				               		_fbody : function(){
				               			if(_.isEmpty(self.$routeDetail) || !_.has(self.$routeDetail,'scheduleRoute'))
				               				return;
				               			
				               			var schedule = self.$routeDetail.scheduleRoute;
				               			var _name = '';
				               			if(_.isArray(schedule))
					               			angular.forEach(schedule,function(val,i){
					               				_name +=' ' + uiLabelMap.day_of_week[val.scheduleRoute]['vi'];
					               			})
					               		else
					               			_name = _.has(uiLabelMap.day_of_week,schedule) ? uiLabelMap.day_of_week[schedule]['vi'] : '';
				               			self.$routeDetail._name = _name;
				               		}
				               	}
		               ]          
		}
		
		if(_olbius)
			_olbius.init(self,config);
		
		
	});