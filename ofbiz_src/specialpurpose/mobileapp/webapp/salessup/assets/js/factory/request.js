if(app !== undefined)
	
	/**
	 * initilize object process request use dependency $https angular
	 * 
	 * */
	app.factory('RequestApp',['$q','$rootScope','$http','$state','$timeout','popupApp',function RequesHandler($q,$rootScope,$http,$state,$timeout,popupApp){
		this._config = {};
		
		var root = $rootScope;
		
		this.triggerRequest = function(_config,wait){
			var self = new Object();
			
			self.defaultHeader = function(){
				return {'Content-Type' : 'application/x-www-form-urlencoded'};
			}
			
			self.tranfromRequestDefault = function(data){
				if(_.isEmpty(data)) 
					return data;
				
				var arr = [];
				
				for(var k in data)
					arr.push(encodeURIComponent(k) + '=' + encodeURIComponent(data[k]));	
				
				return arr.join('&');
			}
			
			self.setConfigHandler = function(_config){
				self._config = {
						method : 'POST',
						url : config.baseUrl + (_config.url ? _config.url : ''),
						data : _config.data ? _config.data : {},
						dataType : _config.dataType ? _config.dataType : "json",
						headers : _config.headers ? _config.headers : self.defaultHeader(),	
						eventHandlers : _config.eventHandlers ? _config.eventHandlers : {},
						transformRequest : _config.transformRequest ? _config.transformRequest : self.tranfromRequestDefault,
						cache  : _config.cache ? _config.cache : false,
						timeout : _config.timeout ? _config.timeout : 60000,
				}
				
				if(typeof _config.callback === 'function')
					self._config.callback = _config.callback;
				
				if(_.has(self._config.data,'pagenum'))
					self._config.data.pagenum = self._config.data.pagenum  === undefined ? 0 : self._config.data.pagenum ;
				
				if(_.has(self._config.data,'pagesize'))
					self._config.data.pagesize = self._config.data.pagesize  === undefined ? config.pagesize : self._config.data.pagesize ;
				
				_config.data ? self._config.data = _config.data : void 0;
			}
			
			self.required = function(data){
				if(!_.has(data,'data'))
					return;
				if(_.isEmpty($('.popup')) && (!_.has(data.data,config.msg.erm) && !_.has(data.data,config.msg.erml)))
					popupApp.alert('',$rootScope.getLabel('LogInTimeout'),null);
				else
					popupApp.alert((_.has(data.data,config.msg.erm) ? data.data[config.msg.erm]:data.data[config.msg.erml]),'<i class="ion-alert-circled"></i>&nbsp;Login Failed',null);
				if($('.popup-head').length > 0)
					$('.popup-head').css('background','#ef473a');
				if($('.popup-title').length > 0)
					$('.popup-title').css('color','white');
				
				$timeout(function(){
					$state.go('login');
				},config.timeoutRequest);
			}
			
			self.checkLogin = function(obj){
				if(obj.data && _.has(obj.data,'login'))
				{
					if(obj.data.login == 'FALSE')
						self.required(obj);
				}else
				{
					popupApp.alert(root.getLabel('CheckNetWorkDevice'),'<i class="ion-alert-circled"></i>&nbsp;Login Failed',null);
					if($('.popup-head').length > 0)
						$('.popup-head').css('background','#ef473a');
					if($('.popup-title').length > 0)
						$('.popup-title').css('color','white');
					return false;
				}	
			}
			
			self.getConfigHandler = function(){return self._config;}
			
			self.success = function(response){
				var connect = self.checkLogin(response);
				if(connect === false)
					return;
				
				if(self._config && typeof self._config.callback === 'function')
					self._config.callback(response);
			}
			
			self.failed = function(error){
				var connect = self.checkLogin(error);
				if(connect === false)
					return;
				if(self._config && typeof self._config.callback === 'function')
					self._config.callback(error);
			}
			
			self.run = function(_config,wait)
						{
							var httpRequest = null;
							var success = true;
							try {
								 httpRequest = $http(self.getConfigHandler(self.setConfigHandler(_config)));
								 
								 if(_config.after && typeof _config.after == 'function')
									 _config.after();
								 if(!wait)
									 return httpRequest;
								 $timeout(function(){
									 if(_config.before && typeof _config.before == 'function')
										 _config.before();
									 httpRequest.then(self.success,self.failed);
								 },config.timeoutRequest);
							} catch (e) {
								success  = false;
								throw e;
							}
							return (function(s){if(success) delete s;}(self));
						}
			
			return self.run(_config,wait);
		}
		
		return this;
	}]);


/**
 * The interface request allow provide commonest with other request
 * @param url : url of request implements 
 * @param inject : the dependency of request implement use
 * 
 * */
var requestInterface = function(){}
	
requestInterface.prototype  = {
		$inject : null,
		setInject  : function($inject){
			this.$inject = $inject;
		},
		getInject : function(){
			return this.$inject;
		},
		commonRequestFunc : function(url){
			var parent = this;
			return  function(data,callback,after,before){
				if(!after && !before)
					return parent.$inject.triggerRequest({data : data,url : url},false);
				return parent.$inject.triggerRequest({data : data,url : url,callback : callback,after : after,before : before},true);
			}
		},
		init : function(url){
			return this.commonRequestFunc.call(this,url)
		},
		implement : function(url){
			return this.init.call(this,url);
		}
}
/*
 * init instance implements request Interface use in all factory request apps
 * 
 * */
var _requestImpl = new requestInterface();

/**
 * initilize request for event login
 * 
 */
app.factory('LoginEvent',['RequestApp',function(RequestApp){
	
		_requestImpl.setInject(RequestApp)
		
		this.doLogin = _requestImpl.implement('checkLogin');
		
		this.doLogout = _requestImpl.implement('logout');
		
		return this;
	}])
	
	/**
	 * initilize route factory request 
	 * 
	 * */
	app.factory('RouteEvent',['RequestApp',function(RequestApp){
		
		this.getRoute = _requestImpl.implement('getListRouteBySup');
		
		this.getAddressOfRoute = _requestImpl.implement('getAddressRoute');
		
		this.removeRoute = _requestImpl.implement('deleteRoute');
		
		this.createRoute = _requestImpl.implement('createRoute');
		
		this.updateRoute = _requestImpl.implement('updateRoute');
		
		this.updateLocation = _requestImpl.implement('updateLocationCustomer');
		
		return this;
	}])
	
	app.factory('EmployeeEvent',function(RequestApp){
		this.getListSalerBySup  = _requestImpl.implement('getListSalerBySup');
		
		this.getListOrgManagedByParty = _requestImpl.implement('getListOrgManagedByParty');
		
		this.getEmplLeaveList = _requestImpl.implement('getEmplLeaveList');
		
		this.approvalEmplLeaveBySup = _requestImpl.implement('approvalEmplLeaveBySup');
		
		return this;
	})
	
	app.factory('PromotionEvent',function(RequestApp,popupApp){
		
		this.getListExhAcc =  _requestImpl.implement('getListExhAcc');
		
		this.getDetailProgramExhAcc = _requestImpl.implement('getDetailProgramExhAcc');
		
		/**
		 * this request allow acept promotions
		 * @param productPromoId
		 * @param statusId ('PROMO_ACCEPTED')
		 * 
		 * */
		this.acceptPromotion = _requestImpl.implement('changePromoExtStatus');
		
		/**
		 * this request allow cancel promotions
		 * @param productPromoId
		 * @param statusId ('PROMO_CANCELLED')
		 * @param changeReason : reason cancel promotions
		 * 
		 * */
		
		this.cancelPromotion =  _requestImpl.implement('changePromoExtStatus');
		
		return this;
	})
	
	app.factory('DashboardEvent',function(RequestApp){
		/**
		 * get total order complete in today 
		 * 
		 * */
		this.getOrderComplete = _requestImpl.implement('getOrderComplete');
		
		this.getOrderCancel = _requestImpl.implement('getOrderCancel');
		
		this.getOrderIntransit = _requestImpl.implement('getOrderIntransit');
		
		this.getTurnOver = _requestImpl.implement('getTurnOver');
		
		this.getTurnOverChart = _requestImpl.implement('getTurnOverChart');
		
		this.getCustomerRatingChart = _requestImpl.implement('getCustomerRatingChart');
		
		return this;
		
	})
	
	
	app.factory('AgentsEvent',function(RequestApp){
		
		this.getStoresList = _requestImpl.implement('getStoresList');
		
		this.getListCheckInventoryAgent = _requestImpl.implement('getListCheckInventoryAgent');
		
		this.getInventoryList =  _requestImpl.implement('getInventoryList');
		
		return this;
		
	})
	
	app.factory('ProfileEvent',function(RequestApp){
		this.getEmployeeInfo = _requestImpl.implement('getInfoProfile');
		
		return this;
	})
	
	