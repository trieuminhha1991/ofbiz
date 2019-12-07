if(app !== undefined)
	app.controller('InventoryStoreController',function($scope,$rootScope,$controller,$state,$stateParams,AgentsEvent,Util){
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
				           			if(_.isEmpty(self.list))
				           				self.getListInventoryAgent();
				           		}
				           	}
				           ],
	           'behavior' : [
				               	{ 
				               		_fname : 'getListInventoryAgent',
				               		_fbody : function(args){
				               			
				               			var data = {pagesize:config.pagesize,pagenum:self.currentPage};
				               			
				               			AgentsEvent.getListCheckInventoryAgent(data,function(res){
				               				
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
				               					
				               					/*if(_.has(res.data,'TotalRows'))
				               					{
				               						root.totalStores = res.data.TotalRows;
					             					if(root.totalStores >= 100)
					             						root.totalStores = "100+";
				               					}	*/
				               				}
				               				
				               			},self.show,self.hide);
				               		}
				               	},
				               	{
				                	 _fname : 'processStore',
				                	 _fbody: function(obj){
				                		 
				                					if(obj && _.isArray(obj) && obj.length != 0)
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
					                				
					                				}else self.isLoadMore = false;
				                	 }
				                },
				                {
				               		_fname : 'viewStoresDetail',
				               		_fbody : function(index){
				               			
				               			if(isNaN(index))
				               				return;
				               			
				               			
				               			var id = self.getStoreInList(index)['partyId'];
				               			
				               			if(!Util.isValid(id))
				               				return;
				               			
			               				$state.go('menu.inventoryList',{storeid : id});	
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
				                		 self.getListInventoryAgent();
				                	 }
				                }
				            ]   	
		}
		
		if(_olbius)
			_olbius.init(self,_config);
	
	});