if(app !== undefined)
	app.controller('EmployeeDetailController',function($scope,$rootScope,$controller,$stateParams,$state,$log,Util){
		var root = $rootScope;
		
		var self = $scope;
		
		var _config = {
				$inject : {
					$controller : $controller
				},
				'event' : [
				           {
				        	   _name : 'enter',
				        	   _func : function(){
				        		   var empl = $stateParams.empl;
				        		   self.employee = Util.isValid(empl) ? angular.fromJson(empl) : {};
				        		   self.employee._id_ = self.employee.partyCode ? self.employee.partyCode: self.employee.partyId;
				        		   self.employee._gender_ = (self.employee.gender == 'M' ? 'Nam' : 'Nữ')
				        		   self.employee._birthday_ = (self.employee.birthDate ? utils.formatDDMMYYYY(self.employee.birthDate) : "")
				        	   }
				           },
				           {
				           		_name : 'leave',
				           		_func :function(){
				           			
				           		}
				           }
			           ],
	           'behavior' : [{
	        	   _fname  :'viewStoresManager',
	        	   _fbody : function(){
	        		   $state.go('menu.storeList',{partyId : self.employee.partyId ? self.employee.partyId : self.employee.partyCode,name : self.employee.fullName ? self.employee.fullName : self.employee.description});
	        	   }
	           }]
		}
		
		if(_olbius)
			_olbius.init(self,_config);
		
	})