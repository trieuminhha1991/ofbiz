app.controller('DashboardController', function($rootScope, $scope, $location, $timeout, $controller, DashboardService, EmployeeService, CalendarFactory, Chart) {
    var self = $scope;
    var root = $rootScope;
    $.extend(this, $controller('BaseController', {
        $scope: self
    }));
    self.salesReportId = "salesReportLoading";
    self.customerReportId = "customerReportLoading";
    self.reports = {
        salesReport: {
            time: "WEEK",
            fromDate: null,
            thruDate: new Date()
        },
        customerReport: {
            time: "WEEK",
            fromDate: null,
            thruDate: new Date(),
            quantityRange: [5, 10, 15],
            limit: 5
        },
        orderTotal: {
            time: "MONTH",
            fromDate: null,
            thruDate: new Date(),
            show: "PRODUCT_STORE",
            type: ["PRODUCT_STORE"],
            config: {}
        },
        salesmanTop: {
            time: "MONTH",
            fromDate: null,
            thruDate: new Date(),
            config: {}
        }
    };
    self.kpi = null;
    self.initData = function(force){
	var before;
	if(!force){
		before = self.showLoading;
	}
	self.getDataType('results', self.getCurrentKpi, EmployeeService.getCurrentKpi, null, config.storage.kpi, before, self.hide, force);
        self.getOrderTotal(force);
        self.getSalesmanTop(force);
    };
    // self.$watch("reports.salesReport.time", function(){
    // if(self.reports.salesReport.time){
    // self.calculateSalesReport();
    // }
    // });

    self.$watch("reports.orderTotal.time", function() {
        if (self.reports.orderTotal.time) {
            self.getOrderTotal();
        }
    });

    self.$watch("reports.salesmanTop.time", function() {
        if (self.reports.salesmanTop.time) {
            self.getSalesmanTop();
        }
    });

    self.$watch("reports.customerReport.time", function() {
        if (self.reports.salesReport.time) {
            self.getCustomerOrderReport();
        }
    });
    self.$watch("reports.customerReport.limit", function() {
        if (self.reports.customerReport.limit) {
            self.getCustomerOrderReport();
        }
    });
    self.hide = function(){
	self.hideLoading();
	self.$broadcast('scroll.refreshComplete');
    };
    self.calculateSalesReport = function(force) {
	var before;
	if(!force){
		before = self.showLoading;
	}
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
            time: report.time,
            fromDate: CalendarFactory.getStartDate(report.fromDate),
            thruDate: CalendarFactory.getEndDate(report.thruDate)
        };
        DashboardService.evaluateTotal(obj, before, self.hide).then(function(res) {
            var data = res.data;
            if (data && data.length) {
                self.reports.salesReport.value = data[0].orderValue;
            }
        }, self.hideLoading);
    };
    self.getCustomerOrderReport = function(force) {
	var before;
	if(!force){
		before = self.showLoading;
	}
        if (self.reports.customerReport.processing) return;
        self.reports.customerReport.processing = true;
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
            time: report.time,
            limit: report.limit,
            fromDate: CalendarFactory.getStartDate(report.fromDate),
            thruDate: CalendarFactory.getEndDate(report.thruDate)
        };
        DashboardService.evaluateCustomer(obj, before, self.hide).then(function(res) {
            self.reports.customerReport.processing = false;
            var data = res.data;
            var dtf = res.datafields;
            if (data && data.length) {
                self.reports.customerReport.value = data;
            } else {
                self.reports.customerReport.value = [];
            }
            self.reports.customerReport.dataFields = dtf;
        }, function() {
            self.reports.customerReport.processing = false;
            self.hideLoading();
        });
    };
    self.getOrderTotal = function(force) {
	var before;
	if(!force){
		before = self.showLoading;
	}
        if (self.reports.orderTotal.processing) return;
        self.reports.orderTotal.processing = true;
        var report = _.clone(self.reports.orderTotal);
        report.dateType = report.time;
        report.time = "PREVYEAR";
        var obj = _.clone(report);
        obj.fromDate = CalendarFactory.getStartDate(report.fromDate),
            obj.thruDate = CalendarFactory.getEndDate(report.thruDate)
        DashboardService.getOrderTotal(obj, before, self.hide).then(function(res) {
            self.reports.orderTotal.processing = false;
            var yAxis = res.yAxis;
            var series = [];
            var total = 0;
            var employee = self.getLocalItem(config.storage.employee);
            for (var x in yAxis) {
                series.push({
                    name: x,
                    data: yAxis[x]
                });
                for(var y in yAxis[x]){
			total += yAxis[x][y];
                }
            }
            self.reports.orderTotal.config = Chart.initConfig({
		subtitle: {
		            text: self.getLabel('Total') + ': ' + self.formatNumberDecimal(total) + '<sup>đ</sup>'
		        }
            }, series, { "text": self.getTime(report.time) }, false, {
                categories: res.xAxis
            }, {
                title: {
                    text: self.getLabel('Turnover')
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            }, false, null, null);
        }, function() {
            self.reports.orderTotal.processing = false;
            self.hideLoading();
        });
    };

    self.getSalesmanTop = function(force) {
	var before;
	if(!force){
		before = self.showLoading;
	}
        if (self.reports.salesmanTop.processing) return;
        self.reports.salesmanTop.processing = true;
        var report = _.clone(self.reports.salesmanTop);
        report.dateType = report.time;
        report.time = "PREVYEAR";
        var obj = _.clone(report);
        obj.fromDate = CalendarFactory.getStartDate(report.fromDate),
            obj.thruDate = CalendarFactory.getEndDate(report.thruDate)
        DashboardService.getSalesmanTop(obj, before, self.hide).then(function(res) {
            self.reports.salesmanTop.processing = false;
            var yAxis = res.yAxis;
            var series = [];
            var i = 0;
            for (var x in yAxis) {
                i++;
                series.push({
                    name: x,
                    data: yAxis[x]
                });
            }
            var plotOptions = {};
            var xAxis = [];
            for (var i = 0; i < res.xAxis.length; i++) {
                xAxis.push(self.getLabel('T' + (i+1)));
            }
            self.reports.salesmanTop.config = Chart.initConfig({
		"chart": {
			      "type": "column"
			    },
            }, series, { "text": self.getTime(report.time) }, false, {
                categories: xAxis
            }, {
                title: {
                    text: self.getLabel('Turnover')
                },
                plotLines: [{
                    value: 0,
                    width: 1,
                    color: '#808080'
                }]
            }, false, null, null);
        }, function() {
            self.reports.salesmanTop.processing = false;
            self.hideLoading();
        });
    };
    self.getTime = function(type) {
        var date = new Date();
        switch (type) {
            case "WEEK":
                return self.getLabel('Thisweek');
            case "MONTH":
                var m = date.getMonth() + 1;
                return self.getLabel('month' + m);
            case "YEAR":
                var m = date.getYear() + 1900;
                return self.getLabel('Year') + " " + m;
            default:
                return "";
        };
    };
    self.getCurrentKpi = function(data) {
        if(data && data.length){
		self.kpi = data[0];
        }
    };
    self.$on('$ionicView.loaded', self.initData);
});
