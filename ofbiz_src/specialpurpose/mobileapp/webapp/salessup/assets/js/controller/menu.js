if(typeof app !== undefined)
{
	app.controller('MenuController',function($rootScope,$scope,$timeout,$ionicHistory,$state,Util,StorageFactory,LoginEvent,popupApp,RouteEvent,PromotionEvent,EmployeeEvent,AgentsEvent){
		var root = $rootScope;
		var self = $scope;
		root.showToggle = true;
		self.isDrag = true;
		self.img_url = 'assets/img/logo_olbius_white.png';
		self.infor = {
				saler : {total : 0},
				road : {total : 0}
		}
		self.$on('$ionicView.enter',function(){
			var currentView = $ionicHistory.currentView();
			if(Util.isValid(currentView))
				if(currentView.stateName != 'menu.routelist' && currentView.stateName != 'menu.salesmanlist' && currentView.stateName != 'menu.programExhAcc')
				{
					/*get total road and saler*/
					self.getInfor();
				}
			self.isDrag = currentView.stateName == 'menu.programExhAccDetail' ? false : true;
			
			self.getProfile();
		});
		
		self.getProfile = function(){
			var profile = StorageFactory.getLocalItem(config.profile)
			if(Util.isValid(profile))
			{
				self.profileName = (profile.name ? profile.name : "") + (profile.emailAddress ? "\n " + profile.emailAddress : "");				
			}	
		}
		
		self.getInfor = function(target){
			var configCommon = {pagesize : 5,pagenum : 0};
			if(target == 'route' || !target)
				RouteEvent.getRoute(_.extend(configCommon,{supId : config.userDefault.USERNAME})).then(function(response){
					if(!_.isEmpty(response) && !_.isEmpty(response.data.TotalRows))
					{
						root.totalRoad = response.data.TotalRows;
						if(root.totalRoad >= 100)
							root.totalRoad = "100+";
					}else root.totalRoad = 0;
				});
			if(target == 'saler' || !target)
				EmployeeEvent.getListSalerBySup(configCommon).then(function(response){
					if(!_.isEmpty(response) && !_.isEmpty(response.data.listIterator))
					{
						root.totalSaler = _.size(response.data.listIterator);
						if(root.totalSaler >= 100)
							root.totalSaler = "100+";
					}else root.totalSaler = 0;
				});
			
			if(target == 'program' || !target)
				PromotionEvent.getListExhAcc(configCommon).then(function(response){
					if(!_.isEmpty(response) && !_.isEmpty(response.data.listIterator))
					{
						root.totalProgram = _.size(response.data.listIterator);
						if(root.totalProgram >= 100)
							root.totalProgram = "100+";
					}else root.totalProgram = 0;
				});
			
			if(target == 'agents' || !target)
				AgentsEvent.getStoresList(configCommon).then(function(response){
					if(!_.isEmpty(response) && !_.isEmpty(response.data.TotalRows))
					{
						root.totalStores = response.data.TotalRows;
						if(root.totalStores >= 100)
							root.totalStores = "100+";
					}else root.totalStores = 0;
				});
			
		}
		
		self.goState = function(id){
			$state.go(id);
		}
		
		self.active = function(tab){
			if(!tab) return;
			
			var current = $ionicHistory.currentView();
			
			if(!current) return;
			
			if(current.stateId == tab)
				return 'tab-current'
			return '';
		}
		
		self.clearAll = function(){
			$ionicHistory.clearHistory();
			$ionicHistory.clearCache();
			StorageFactory.clear();
		}
		
		self.logout = function(){
			popupApp.showConfirm(null,self.getLabel('LogoutNtf'),function(res){
				if(res === true)
					LoginEvent.doLogout().then(function(res){
							if(Util.isValid(res))
								if(res.statusText == "OK")
								{
									$state.go('login');
									self.clearAll();
								}
								else popupApp.alert('','Logout failed',null);
					})
			});
		}
		
	})
}