app.controller('LeaveController', function($rootScope, $scope, $controller, EmployeeService, CalendarFactory) {
	var self = $scope;
	$.extend(this, $controller('BaseController', {
		$scope : self
	}));
	self.fromDateShift = [];
	self.thruDateShift = [];
	self.reasons = [];
	self.leaveTypes = [];
	self.employee = {
		fromDateLeaveTypeId : "",
		thruDateLeaveTypeId : "",
		emplLeaveReasonTypeId : "",
		workingShiftId: "",
		fromDate : new Date(),
		thruDate : new Date(),
		description : ""
	};
	self.fromDate = {
		options : {
			date: self.employee.fromDate,
	        mode: "date",
	        minDate: new Date(),
	        maxDate: config.date.max
		}
	};
	self.thruDate = {
		options: {
			date: self.employee.thruDate,
			mode: "date",
			minDate : new Date(),
			maxDate : config.date.max
		}
	};

	self.$on('$ionicView.enter', function() {
		self.getDataType("leaveReasonType", self.initReasonData, EmployeeService.getReason, {}, config.storage.leaveReasonType);
		self.getDataType("workingShift", self.initWorkingShift, EmployeeService.getWorkingShift, {}, config.storage.workingShift);
	});
	self.initReasonData = function(data) {
		self.reasons = data;
		if (self.reasons && self.reasons.length !== 0) {
			self.employee.emplLeaveReasonTypeId = self.reasons[0].emplLeaveReasonTypeId;
		}
	};
	self.initWorkingShift = function(data){
		if(data && data != "undefined"){
			if(typeof(data) != 'object')
				self.workingShifts = JSON.parse(data);
			else self.workingShifts = data
			if (self.workingShifts && self.workingShifts.length !== 0) {
				self.employee.workingShiftId = self.workingShifts[0].workingShiftId;
			}
		}
		
	};
	self.$watch('employee.workingShiftId', function(){
		if(self.employee.workingShiftId){
			var data = self.getWorkingShiftWithId(self.employee.workingShiftId);
			if(!data.shiftBreakStart && data.shiftStartTime && data.shiftEndTime){
				var total = data.shiftStartTime + data.shiftEndTime;
				if(data.shiftStartTime >= data.shiftEndTime){
					total += 86400000;
				}
				data.shiftBreakStart = total/2;
				data.shiftBreakEnd = data.shiftBreakStart;
			}else if(!data.shiftStartTime || !data.shiftEndTime){
				data = null;
			}
			self.initFromDateWorkingShift(data);
			self.initThruDateWorkingShift(data);
		}
	});
	self.initFromDateWorkingShift = function(data){
		var arr = [];
		if(data){
			var time1 = CalendarFactory.getTimeValue(data.shiftStartTime);
			arr.push({
				time : "FIRST_HALF_DAY",
				description: time1
			});
			var time2 = CalendarFactory.getTimeValue(data.shiftBreakEnd);
			arr.push({
				time : "SECOND_HALF_DAY",
				description: time2
			});
			self.employee.fromDateLeaveTypeId = arr[0].time;
		}
		self.fromDateShift = arr;
	};
	self.initThruDateWorkingShift = function(data){
		var arr = [];
		if(data){
			var time1 = CalendarFactory.getTimeValue(data.shiftBreakStart);
			arr.push({
				time : "FIRST_HALF_DAY",
				description: time1
			});
			var time2 = CalendarFactory.getTimeValue(data.shiftEndTime);
			arr.push({
				time : "SECOND_HALF_DAY",
				description: time2
			});
			self.employee.thruDateLeaveTypeId = arr[0].time;
		}
		self.thruDateShift = arr;
	};
	self.getWorkingShiftWithId = function(workingShiftId){
		for(var x in self.workingShifts){
			if(self.workingShifts[x].workingShiftId == workingShiftId){
				return self.workingShifts[x];
			}
		}
	};
	self.validate = function(){
		if(self.employee.fromDate && self.employee.thruDate
			&& self.employee.emplLeaveReasonTypeId && self.employee.workingShiftId
			&& self.employee.thruDateLeaveTypeId && self.employee.fromDateLeaveTypeId
			&& self.employee.fromDate <= self.employee.thruDate)
			return true;
		return false;
	};
	self.checkBeforeSend = function(){
		if (self.validate()) {
			self.buildConfirm(null, self.getLabel('ConfirmLeave'), self.submit)
		}else
			self.showError(self.getLabel('PleasePressFullInformation'));
	};
	self.submit = function() {
		var data = 	_.clone(self.employee);
		data.fromDate = self.formatDateDMY(data.fromDate);
		data.thruDate = self.formatDateDMY(data.thruDate);
		EmployeeService.submit(data, self.showLoading, self.hideLoading).then(function(data) {
			if (!data._ERROR_MESSAGE_) {
				self.buildAlert("", self.getLabel('SendSuccess'));
			} else if (data._ERROR_MESSAGE_ && data['_ERROR_MESSAGE_'].indexOf('ALREADY_EXIST') != -1) {
				self.buildAlert("", self.getLabel('Dupplicate'));
			} else {
				self.buildAlert("", self.getLabel('SendError'));
			}
		}, function() {
			self.hideLoading();
		});
	};
});
