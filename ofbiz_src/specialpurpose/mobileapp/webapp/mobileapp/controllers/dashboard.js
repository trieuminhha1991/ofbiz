/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */
olbius.controller('DashboardController', function($rootScope, $scope, $location, $timeout, $controller, DashboardFactory, EmployeeService) {
	var self = $scope;
	var root = $rootScope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.salesReportId = "salesReportLoading";
	self.customerReportId = "customerReportLoading";
	self.reports = {
		salesReport : {
			time : "WEEK",
			fromDate : null,
			thruDate : new Date()
		},
		customerReport : {
			time : "WEEK",
			fromDate : null,
			thruDate : new Date(),
			quantityRange: [5, 10, 15],
			limit: 5
		}
	};
	self.currentKpi = null;
	self.$on('$viewContentLoaded', function() {
		self.setHeader('Dashboard', "/main", false);
		$timeout(function(){
			self.calculateSalesReport();
			self.getCustomerOrderReport();
			self.getDataType('results', self.getCurrentKpi, EmployeeService.getCurrentKpi, null, 'currentKpi');
		}, 500);
	});

	self.calculateSalesReport = function() {
		var show = self.salesReportLoading.showLoading;
		var hide = self.salesReportLoading.hideLoading;
		var report = _.clone(self.reports.salesReport);
		if (report.type == "RANGE") {
			if (!report.fromDate) {
				report.fromDate = new Date(1970, 0, 1, 0, 0, 0, 0);
			}
			if (!report.thruDate) {
				report.thruDate = new Date();
			}
		}

		var obj = {
			time : report.time,
			fromDate : self.getStartDate(report.fromDate),
			thruDate : self.getEndDate(report.thruDate)
		};
		DashboardFactory.evaluateTotal(obj, show, hide).then(function(res) {
			var data = res.data;
			if(data && data.length){
				self.reports.salesReport.value = data[0].orderValue;
			}
		}, hide);
	};
	self.getCustomerOrderReport = function() {
		var show = self.customerReportLoading.showLoading;
		var hide = self.customerReportLoading.hideLoading;
		var report = _.clone(self.reports.customerReport);
		if (report.type == "RANGE") {
			if (!report.fromDate) {
				report.fromDate = new Date(1970, 0, 1, 0, 0, 0, 0);
			}
			if (!report.thruDate) {
				report.thruDate = new Date();
			}
		}
		var obj = {
			time : report.time,
			limit: report.limit,
			fromDate : self.getStartDate(report.fromDate),
			thruDate : self.getEndDate(report.thruDate)
		};
		DashboardFactory.evaluateCustomer(obj, show, hide).then(function(res) {
			var data = res.data;
			var dtf = res.datafields;
			if(data && data.length){
				self.reports.customerReport.value = data;
			}
			self.reports.customerReport.dataFields = dtf;
		}, hide);
	};
	self.getCurrentKpi = function(data){
		self.currentKpi = data;
	};
});
