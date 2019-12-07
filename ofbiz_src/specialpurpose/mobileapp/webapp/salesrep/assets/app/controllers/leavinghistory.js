app.controller('LeavingHistoryController', function($rootScope, $scope, $controller, EmployeeService) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.year = {
		start: 1970,
		value: ''
	};
	self.history = {};
	self.$watch('year.value', function() {
		if(self.year.value){
			self.getData();
		}
	});

	self.init = function() {
		self.initYear();
		self.year.value = new Date().getYear() + 1900;
		self.getData();
	};
	self.initYear = function(){
		var arr = [];
		var cur = new Date().getYear() + 1900;
		for(var i = self.year.start; i <= cur; i++){
			arr.push(i);	
		}
		self.yearRange = arr;
	};
	self.getData = function() {
		if(!self.year.value || self.processing || self.processingLeave) return;
		self.processing = true;
		EmployeeService.getEmplLeaveInfo({year : self.year.value}).then(function(res){
			self.history = {
				annualLeft : res.annualLeft,
				annualLeaveRemain : res.annualLeaveRemain
			};
			self.processing = false;
			self.closeRefresh();
		}, function(){
			self.processing = false;
			self.closeRefresh();
		});
		self.self.processingLeave = true;
		EmployeeService.getHistory({year : self.year.value}).then(function(res){
			self.historyDetail = res.emlLeaveStatus;
			self.processingLeave = false;
			self.closeRefresh();
		}, function(){
			self.processingLeave = false;
			self.closeRefresh();
		});
	};
	self.closeRefresh = function(){
		if(!self.processingLeave && !self.processing)
				self.$broadcast('scroll.refreshComplete');
	};
	self.$on('$ionicView.enter', self.init);
});