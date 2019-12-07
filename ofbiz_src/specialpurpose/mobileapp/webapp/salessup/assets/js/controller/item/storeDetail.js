if(app !== undefined)
	app.controller('StoreDetailController',function($scope,$controller,$stateParams,$log,Util){
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
				        		   if(Util.isValid(store))
				        			   self.store = angular.fromJson(store);
				        		   self.fixData();
				        		   $log.log(self.store)
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