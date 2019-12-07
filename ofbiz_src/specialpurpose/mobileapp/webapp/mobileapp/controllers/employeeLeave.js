/*global todomvc, angular */
'use strict';

/**
 * The main controller for the app. The controller: - retrieves and persists the
 * model via the todoStorage service - exposes the model to the template and
 * provides event handlers
 */
olbius.controller('EmployeeLeaveController', function($rootScope, $scope, $controller, $location, EmployeeService, LanguageFactory) {
	var self = $scope;
	var root = $rootScope;
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
		thruDate : "",
		description : ""
	};

	self.$on('$viewContentLoaded', function() {
		self.setHeader('ForLeave', "/main", false);
		self.getDataType("leaveReasonType", self.initReasonData, EmployeeService.getReason);
		// self.getDataType("leaveType", self.initLeaveTypeData, EmployeeService.getType);
		self.getDataType("workingShift", self.initWorkingShift, EmployeeService.getWorkingShift);
	});

	self.initReasonData = function(data) {
		self.reasons = data;
		if (self.reasons && self.reasons.length !== 0) {
			self.employee.emplLeaveReasonTypeId = self.reasons[0].emplLeaveReasonTypeId;
		}
	};
	// self.initLeaveTypeData = function(data) {
		// self.leaveTypes = data;
		// if (self.leaveTypes && self.leaveTypes.length !== 0) {
			// self.employee.leaveTypeId = self.leaveTypes[0].leaveTypeId;
		// }
	// };
	self.initWorkingShift = function(data){
		self.workingShifts = $.parseJSON(data);
		if (self.workingShifts && self.workingShifts.length !== 0) {
			self.employee.workingShiftId = self.workingShifts[0].workingShiftId;
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
			var time1 = self.getTimeValue(data.shiftStartTime);
			arr.push({
				time : "FIRST_HALF_DAY",
				description: time1
			});
			var time2 = self.getTimeValue(data.shiftBreakEnd);
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
			var time1 = self.getTimeValue(data.shiftBreakStart);
			arr.push({
				time : "FIRST_HALF_DAY",
				description: time1
			});
			var time2 = self.getTimeValue(data.shiftEndTime);
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
	self.viewReport = function() {
		self.changeState('/employee-leave/report');
	};
	self.validate = function(){
		if(self.employee.fromDate && self.employee.thruDate
			&& self.employee.emplLeaveReasonTypeId && self.employee.workingShiftId
			&& self.employee.thruDateLeaveTypeId && self.employee.fromDateLeaveTypeId)
			return true;
		return false;
	};
	self.submit = function() {
		if (self.validate()) {
			var data = 	self.cloneObject(self.employee);
			data.fromDate = self.formatDateDMY(data.fromDate);
			data.thruDate = self.formatDateDMY(data.thruDate);
			EmployeeService.submit(data, root.showLoading, root.hideLoading).then(function(data) {
				if (!data._ERROR_MESSAGE_) {
					self.buildAlert("", LanguageFactory.getLabel('SendSuccess'));
				} else if (data._ERROR_MESSAGE_ && data['_ERROR_MESSAGE_'].indexOf('ALREADY_EXIST') != -1) {
					self.buildAlert("", LanguageFactory.getLabel('Dupplicate'));
				} else {
					self.buildAlert("", LanguageFactory.getLabel('SendError'));
				}
			}, function() {
				root.hideLoading();
			});
		} else{
			self.buildAlert("", LanguageFactory.getLabel('PleasePressFullInformation'));
		}
	};
});
