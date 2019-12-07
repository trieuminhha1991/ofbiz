
app.controller('LoginController', function($rootScope, $scope,$timeout,Util,LoadingApps,LoginEvent,ProfileEvent,popupApp,StorageFactory) {
	var self = $scope;
	
	self.$on('$ionicView.enter',function(){
		self.constructor();
	})
	
	self.constructor = function(){
		if(config)
			self.user = config.userDefault ? config.userDefault : {USERNAME : '',PASSWORD : ''};
			localStorage[config.storage.userLogin] = JSON.stringify(self.user);
			
		self.login = function(){
			 
			 LoginEvent.doLogin(self.user,function(response){
				 if(response && response.data.login  == 'TRUE')
				{
					 Util.refreshAllViews();
					 self.getProfile();
					 $timeout(function(){Util.toggleMenu(); Util.changeState('menu.dashboard'); },500);
				} 
			 },LoadingApps.show(''),LoadingApps.hide);
		}	
		
		self.getProfile = function(){
			ProfileEvent.getEmployeeInfo().then(function(res){
				if(_.has(res,config.msg.erm) || _.has(res,config.msg.erml) || !_.has(res,'data'))
					return;
				else{
					if(!_.has(res.data,'mapInfo'))
						return;
					
					StorageFactory.setLocalItem(config.profile,JSON.stringify(res.data.mapInfo))
				}
			})
			
		}
		
		self.viewPass = function(){
			var _passwordElement = angular.element(document).find('input')[1]; 
			
			if(angular.isDefined(_passwordElement))
			{
				angular.element(_passwordElement).attr('type','text');
				$timeout(function(){
					angular.element(_passwordElement).attr('type','password');
				},1000);
			}
		};
		
		self._bind = function(){
			this.$watch('user.PASSWORD', function(){
				if(self.user && self.user.PASSWORD){
					self.showViewPassword = true;
				}else self.showViewPassword = false;
			});
		}
		
		self._bind();
		
	}
	
});