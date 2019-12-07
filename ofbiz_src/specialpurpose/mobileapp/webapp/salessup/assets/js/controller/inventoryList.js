if(app !== undefined)
	app.controller('InventoryListController',function($scope,$rootScope,$controller,$state,$stateParams,AgentsEvent,Util){
		var root = $rootScope;
		
		var self = $scope;	
	
		self.list = [];
		
		self.currentPage = 0;
		
		self.isLoadMore = true;
		
		var _config = {
				$inject : {
					$controller : $controller
				},
				'event' : [
				           	{
				           		_name : 'enter',
				           		_func : function(){
				           			if($stateParams.storeid)
				           				self.storeid = $stateParams.storeid
				           			
				           			if(_.isEmpty(self.list))
				           				self.getInventoryList();
				           		}
				           	}
				           ],
	           'behavior' : [
				               	{ 
				               		_fname : 'getInventoryList',
				               		_fbody : function(args){
				               			
				               			var data = {pagesize:config.pagesize,pagenum:self.currentPage,storeid : self.storeid};
				               			
				               			AgentsEvent.getInventoryList(data,function(res){
				               				
				               				if(!Util.isValid(res.data))
				               				{
				               					self.isLoadMore = false;
				               					return;
				               				}	
				               				if(!Util.isValid(res.data.listIterator) || _.isEmpty(res.data.listIterator))
				               					self.isLoadMore = false;
				               				else 	
				               				{
				               					self.processStore(res.data.listIterator);
				               					
				               				}
				               				
				               			},self.show,self.hide);
				               		}
				               	},
				               	{
				                	 _fname : 'processStore',
				                	 _fbody: function(obj){
				                		 self.list = obj;
				                					/*if(obj && _.isArray(obj) && obj.length != 0)
					                				{
					                					angular.forEach(obj,function(value,index){
					                						var temp = value;
					                						
					                						temp.id = value.partyId ? value.partyId : value.partyCode;
					                						
					                						 if(_.has(temp,'statusId'))
					                							 temp.status = temp.statusId == 'PARTY_ENABLED' ? self.getDayLabel('statusStore')['enabled']['vi'] : self.getDayLabel('statusStore')['disabled']['vi'];
					                	        			
					                						self.list.push(temp);
					                					})
					                					self.currentPage++;
					                					self.isLoadMore = true;
					                				
					                				}else self.isLoadMore = false;*/
				                	 }
				                },
				                {
				               		_fname : 'viewStoresDetail',
				               		_fbody : function(index){
				               			
				               			if(isNaN(index))
				               				return;
				               			
				               			
				               			var inventory = self.getStoreInList(index);
				               			
				               			if(!Util.isValid(inventory))
				               				return;
				               			
			               				$state.go('menu.inventoryStoreDetail',{store : JSON.stringify(inventory)});	
				               		}
				               	},
				            	{
				               		_fname : 'getStoreInList',
				               		_fbody : function(i){
				               			if(_.isEmpty(self.list) || i > _.size(self.list) || isNaN(i))
				               				return {};
				               				
				               			return self.list[i];	
				               		}
				               	},
				               	{
				                	 _fname : 'loadMore',
				                	 _fbody: function(){
				                		 self.getInventoryList();
				                	 }
				                }
				            ]   	
		}
		
		if(_olbius)
			_olbius.init(self,_config);
	
	});