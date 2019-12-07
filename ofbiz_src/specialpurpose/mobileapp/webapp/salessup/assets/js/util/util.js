if(app !== undefined)
	app.factory('Util',function($rootScope,$state,$ionicHistory,$ionicSideMenuDelegate){
		var self = this;
		
		$rootScope.getLabel = self.getLabel = function(key,locale){
			if(uiLabelMap === undefined) 
				return key;
			
			return uiLabelMap[key] !== undefined ? uiLabelMap[key][locale ? locale : 'vi'] : key;
		}
		
		self.changeState = function(target){
			$state.go(target  ? target : 'login');
		}
		
		self.isValid = function(obj){
			return !_.isUndefined(obj) && !_.isNull(obj) && !_.isEmpty(obj);
		}
		
		
		self.refreshAllViews = function(){
			$ionicHistory.clearCache();
			$ionicHistory.clearHistory();
		}
		
		self.toggleMenu = function(){
			$ionicSideMenuDelegate.toggleLeft();
		}
		return self;
	});
	
	/**
	 * initilize LoadingApps util
	 * */
	app.factory('LoadingApps',function($ionicLoading,Util){
		this.show = function(text,spinner,callback){
			spinner = spinner ? spinner : 'android';
			$ionicLoading.show({
				      template: !text ? '<ion-spinner icon="'+ spinner +'">' +  '</ion-spinner>'  : Util.getLabel(text)
			    }).then(function(){
			    	if(angular.isDefined(callback) && typeof callback == 'function')
			    		callback();
			    });
		}
		
		this.hide = function(callback){
			 $ionicLoading.hide().then(function(){
				 if(typeof callback == "function")
					 callback();
			 });
		}
		
		
		return this;
	})
	
	app.factory('popupApp',function($ionicPopup,$timeout){
		
		this.popupCustom = function(templateUrl,title,scope,callback){
			   var alertPopup = $ionicPopup.alert({
				     title:title,
				     scope : scope,
				     templateUrl: templateUrl
				   });
			   return alertPopup;
		}
		
		this.showConfirm = function(template,title,callback,scope){
			   var confirmPopup = $ionicPopup.confirm({
			   		scope : scope,
				     title: title ? title : '<button class="button button-assertive"><i class="ion-alert"></i>' + uiLabelMap['titleConfirm']['vi'] +'</button>',
				     template: template
				   });

				   confirmPopup.then(function(res) {
					   if(typeof callback === 'function')
						   callback(res);
				   });
		}
		
		this.alert = function(template,title,callback){
				   var alertPopup = $ionicPopup.alert({
				     title:title,
				     template: template
				   });

				   alertPopup.then(function(res) {
					   if(typeof callback === 'function')
						   callback(res);
				   });
		}
		
		
		this.popup = function(template,title,scope,buttons,callback){
			var myPopup = $ionicPopup.show({
			    template: template,
			    title: title.title ? title.title : '',
			    subTitle: title.subTitle ? title.subTitle : '',
			    scope: scope,
			    buttons: buttons ? buttons : [{ text: 'Cancel' },
			                                   {
										        text: '<b>Save</b>',
										        type: 'button-positive',
										        onTap: function(e) {
										        	console.log(e);
										        }
			                                   }
		    								]
				});

			  myPopup.then(function(res) {
				  if(typeof callback === 'function')
					   callback(res);
			  });
/*
			  $timeout(function() {
			     myPopup.close(); 
			  }, 3000);*/
		 };
		 
		 return this;
	})
	
app.factory('StorageFactory', function($rootScope) {
	var root = $rootScope;
	var self = this;
	self.setLocalItem = function(key, item) {
		if(_.isUndefined(item) || _.isNull(item)){return;}
		if ( typeof (item) == 'object') {
			localStorage.setItem(key, JSON.stringify(item));
		} else {
			localStorage.setItem(key, item);
		}
	};
	self.getLocalItem = function(key) {
		var x = localStorage.getItem(key);
		if(x && (x.indexOf("{")  != -1 || x.indexOf("[") != -1)){
			return angular.fromJson(localStorage.getItem(key));
		}
		return x;
	};
	self.removeLocalItem = function(key){
		localStorage.removeItem(key);
	};
	
	self.clear = function(){
		localStorage.clear();
	}
	return self;
});

	
