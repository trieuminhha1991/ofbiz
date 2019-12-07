if(app !== undefined)
app.controller('CommonController',function($rootScope,$scope,$ionicHistory,LoadingApps,popupApp){
	var self = $scope;
	var root = $rootScope;
	
	root.uiLabelMap = uiLabelMap ? uiLabelMap : {};
	
	root.getLabel = utils.getLabel;
	
	root.getDayLabel = utils.getDayLabel;
	
	self.show = LoadingApps.show;
	
	self.hide  = LoadingApps.hide;
	
	self.alert = popupApp.alert;
	
	self.popup = popupApp.popup;
	
	self.showConfirm = popupApp.showConfirm;
	
	self.backViewTitle = $ionicHistory.backView() ? $ionicHistory.backView().title : '';
	
	self.back = function(){
		$ionicHistory.goBack();
	}
})

