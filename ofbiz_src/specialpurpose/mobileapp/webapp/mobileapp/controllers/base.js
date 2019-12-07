/*global todomvc, angular */

'use strict';
/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */
olbius.controller('BaseController', function($rootScope, $scope, $compile, $location, $route, CustomerService, DialogFactory, StorageFactory, CalendarFactory, NumberFactory, LanguageFactory) {
	var self = $scope;
	self.self = self;
	var root = $rootScope;
	self.search = "";
	self.customers = {};
	self.locale = localStorage.getItem('locale');
	self.configPage = configPage;
	self.body = angular.element($('.main-container'));
	self.html = angular.element($('html'));
	self.wrapper = angular.element($('#wrapper'));
	self.issidebar = false;
	root.isHeader = true;
	root.isMain = false;
	root.showLoading = function() {
		self.disableScroll();
		if(root.MainLoading){
			root.MainLoading.show();
		}
	};
	root.hideLoading = function() {
		self.enableScroll();
		if(root.MainLoading){
			root.MainLoading.hide();
		}
	};
	self.LanguageFactory = LanguageFactory;
	self.checkLastTimeCheckLocation = function(){
		var key = 'lastTimeCheckLocation';
		var lastTime = self.getLocalItem(key);
		var date = new Date().getTime();
		if(!lastTime){
			self.setLocalItem(key, date);
			return true;
		}else {
			var re = date - lastTime;
			if(re > configPage.gps.delayTime){
				self.setLocalItem(key, date);
				return true;
			}
		}
		return false;
	};
	/*get all customer of salesman & save to localStorage*/
	self.getStore = function(data, before, after) {
		var customers = self.getLocalItem("customers");
		if (customers && customers.content && customers.content.length) {
			var start = data.size * data.page;
			var end = start + data.size + 1;
			self.customers.content = customers.content.slice(start, end);
			self.customers.total = customers.total;
			if(after && typeof(after) == 'function'){
				after();
			}
		} else {
			return CustomerService.getStoreByRoad(data, before, after).then(function(res) {
				self.customers.content = res.customers;
				self.customers.total = res.total;
				self.customers.routes = res.routes;
				if (self.customers.content && self.customers.content.length) {
					self.setLocalItem("customers", self.customers);
				}
			});
		}
	};

	self.initScreen = function() {
		$('.main-container').removeClass('main-bg');
		$('.fullscreen').css('height', $(document).height());
		$('.fullscreen').css('max-width', $(document).width());
		self.enableScroll();
		self.clearAllDialog();
	};
	self.disableScrollX = function() {
		self.body.css('overflow-x', 'hidden');
	};
	self.enableScrollX = function() {
		self.body.css('overflow-x', '');
	};
	self.disableScrollY = function() {
		self.body.css('overflow-y', 'hidden');
	};
	self.enableScrollY = function() {
		self.body.css('overflow-y', '');
	};
	self.disableScroll = function() {
		var obj = {
			'overflow' : 'hidden',
			'height' : '100%'
		};
		$("#wrapper").css(obj);
		self.body.css(obj);
		self.html.css(obj);
	};
	self.enableScroll = function() {
		var obj = {
			'overflow' : '',
			'height' : ''
		};
		self.body.css(obj);
		self.html.css(obj);
	};
	self.setHeader = function(header, back, isLogo) {
		root.headerTitle = LanguageFactory.getLabel(header);
		root.previous = back;
		root.showLogo = isLogo;
	};

	self.getDataType = function(key, initData, remoteData, param, localkey) {
		var data = self.getLocalItem(key);
		if (data && data.length) {
			initData(data);
		} else {
			if(param){
				remoteData(param).then(function(res) {
					// self.setLocalItem(key, res[key]);
					initData(res[key]);
				});
			}else{
				remoteData().then(function(res) {
					// self.setLocalItem(key, res[key]);
					initData(res[key]);
				});
			}

		}
	};
	self.cloneObject = function(obj){
		return JSON.parse(JSON.stringify(obj));
	};
	self.processNumberLocale = function(num){
		return NumberFactory.processNumberLocale(num, locale);
	};
	self.FormatNumberBy3 = function(num, decpoint, sep) {
		return NumberFactory.FormatNumberBy3(num, decpoint, sep);
	};

	self.range = function(min, max, step) {
		return NumberFactory.range(min, max, step);
	};
	self.formatDateDMY = function(date) {
		return CalendarFactory.formatDateDMY(date);
	};
	self.formatDateYMD = function(date, h, m, s, mili) {
		return CalendarFactory.formatDateYMD(date);
	};
	self.getStartDate = function(date){
		return CalendarFactory.getStartDate(date);
	};
	self.getEndDate = function(date){
		return CalendarFactory.getEndDate(date);
	};
	self.getCurrentDate = function() {
		return CalendarFactory.getCurrentDate();
	};
	self.getCurrentDateYMD = function() {
		return CalendarFactory.getCurrentDateYMD();
	};
	self.getTimeValue = function(time, isShowSecond){
		return CalendarFactory.getTimeValue(time, isShowSecond);
	};
	self.buildConfirm = function(header, body, submit, close, compile, force) {
		return DialogFactory.buildConfirm(self, header, body, submit, close, compile, force);
	};
	self.buildAlert = function(title, body, close, force) {
		return DialogFactory.buildAlert(self, title, body, close, force);
	};
	self.buildDialog = function(header, body, options, force) {
		return DialogFactory.buildDialog(self, header, body, options, force);
	};
	self.getButton = function(callback, icon, label, css, action) {
		return DialogFactory.getButton(self, callback, icon, label, css, action);
	};
	self.closeDialog = function(dialog){
		return DialogFactory.closeDialog(self, dialog);
	};
	self.clearAllDialog = function(){
		return DialogFactory.clearAllDialog(self);
	};
	self.updateListCustomer = function(customer){
		var customers = self.getLocalItem('customers');
		if(customer){
			for(var x in customers.content){
				var obj = customers.content[x];
				if(obj.partyIdTo == customer.partyIdTo){
					customers.content.splice(x, 0, customer);
					customers.content.splice(parseInt(x) + 1, 1);
					self.setLocalItem('customers', customers);
					return;
				}
			}
		}
	};
	self.changeState = function(state) {
		$location.path(state);
	};
	self.reloadState = function() {
		$route.reload();
	};
	self.setLocalItem = function(key, item) {
		return StorageFactory.setLocalItem(key, item);
	};
	self.getLocalItem = function(key) {
		return StorageFactory.getLocalItem(key);
	};
	self.removeLocalItem = function(key){
		return StorageFactory.removeLocalItem(key);
	};
	self.resetForm = function(id){
		var form = $('#' + id);
		form.find(':input') .val('');
	};
	self.initScreen();
});
