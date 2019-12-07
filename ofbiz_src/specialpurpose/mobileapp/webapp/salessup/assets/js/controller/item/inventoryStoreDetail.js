if(app !== undefined)
	app.controller('InventoryStoreDetailController',function($scope,$controller,$stateParams,$log,Util){
		var self = $scope;
		
		self.store = {};
		
		var _config = {
				$inject : {
					$controller : $controller
				},
				'event' : [
				           {
				        	   _name : 'enter',
				        	   _func : function(){
				        		   var store = $stateParams.store;
				        		   self.store = angular.fromJson(store);
				        		   if(_.has(self.store,'fromDate'))
				        			   self.store.time = utils.formatDDMMYYYY(self.store.fromDate.time)
				        	   }
				           }
	           ],
	           'behavior' : [{
	        	  _fname :  'fixData',
	        	  _fbody : function(){
		        		 if(_.has(self.store,'statusId'))
		        			 self.store.status = self.store.statusId == 'PARTY_ENABLED' ? self.getDayLabel('statusStore')['enabled']['vi'] : self.getDayLabel('statusStore')['disabled']['vi'];
	        			 if(_.has(self.store,'telecomNumber'))
        				 self.store.telecomNumber = self.store.telecomNumber === null  ? self.getLabel('NotValid') : self.store.telecomNumber
	        	  }
	           }]
		}
		
		if(_olbius)
			_olbius.init(self,_config);
		
	})