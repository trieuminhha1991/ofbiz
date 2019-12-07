if(app !== undefined)
	app.controller('StoreListController',function($scope,$rootScope,$controller,$state,$stateParams,AgentsEvent,Util){
		var root = $rootScope;
		
		var self = $scope;	
		
		self.self = self;
			
		self.currentPage = 0;
		
		self.isLoadMore = true;
		
		self.list = [];
		
		var _config = {
				$inject : {
					$controller : $controller
				},
				'event' : [
				           	{
				           		_name : 'enter',
				           		_func : function(){
				           			
				           			self.partyId = $stateParams.partyId;
				           			
				           			var name = $stateParams.name;
				           			
				           			Util.isValid(self.partyId) ? root.showToggle = false : root.showToggle = true;
				           			
				           			self.title = name ? self.getLabel('storeList') + ' (' + name + ') ' : self.getLabel('storeList');
				           			if(_.isEmpty(self.list))
				           				self.getStoreList(Util.isValid(self.partyId) ? self.partyId : void 0);
				           		}
				           	},
				           	{
				           		_name  : '$stateChangeStart',
				           		_func : function(event, toState, toParams, fromState, fromParams){
				           				
				           		}
				           	}
				          ],
				'behavior' : [
			               	{
			               		_fname : 'getStoreList',
			               		_fbody : function(args,_data){
			               			var data = {pagesize:config.pagesize,pagenum:self.currentPage};
			               			
			               			_.isUndefined(args) ? void 0 : _.extend(data,{partyId : args});
			               			
			               			if(Util.isValid(_data))
			               				_.extend(data,_data);
			               			
			               			AgentsEvent.getStoresList(data,function(res){
			               				if(!Util.isValid(res.data))
			               				{
			               					self.isLoadMore = false;
			               					return;
			               				}	
			               				if(!Util.isValid(res.data.listIterator) || _.isEmpty(res.data.listIterator))
			               					self.isLoadMore = false;
			               				else 	
			               				{
			               					self.processStore(res.data.listIterator,data);
			               					
			               					if(_.has(res.data,'TotalRows'))
			               					{
			               						root.totalStores = res.data.TotalRows;
				             					if(root.totalStores >= 100)
				             						root.totalStores = "100+";
			               					}	
			               				}
			               			},self.show,self.hide);
			               		}
			               	},
			               	{
			                	 _fname : 'processStore',
			                	 _fbody: function(obj,data){
			                		 
			                					if(obj && _.isArray(obj) && obj.length != 0)
				                				{
			                						if(Util.isValid(data) && _.has(data,'filter'))
			                						{
			                							var temp = obj[0];
				                						temp.id = temp.partyId ? temp.partyId : temp.partyCode;
				                						
				                						 if(_.has(temp,'statusId'))
				                							 temp.status = temp.statusId == 'PARTY_ENABLED' ? self.getDayLabel('statusStore')['enabled']['vi'] : self.getDayLabel('statusStore')['disabled']['vi'];
				                						
			                							self.list = [temp];
			                						}else{
			                							angular.forEach(obj,function(value,index){
					                						var temp = value;
					                						temp.id = value.partyId ? value.partyId : value.partyCode;
					                						
					                						 if(_.has(temp,'statusId'))
					                							 temp.status = temp.statusId == 'PARTY_ENABLED' ? self.getDayLabel('statusStore')['enabled']['vi'] : self.getDayLabel('statusStore')['disabled']['vi'];
					                							 
					                						self.list.push(temp);
					                					})
			                						}	
				                					
				                					self.currentPage++;
				                					self.isLoadMore = true;
				                				
				                				}else self.isLoadMore = false;
			                	 }
			                },
			               	{
			               		_fname : 'viewStoresDetail',
			               		_fbody : function(index){
			               			/*if(act == 'getPerson')
			               			{
			               				self.select(index);
			               				return;
			               			}	*/
			               			
			               			if(isNaN(index))
			               				return;
			               			
			               			$state.go('menu.storeDetail',{store : JSON.stringify(self.getStoreInList(index))});	
			               			
			               		}
			               	},
			               	{
			               		_fname : 'getStoreInList',
			               		_fbody : function(i){
			               			if(_.isEmpty(self.list) || i > _.size(self.list))
			               				return {};
			               				
			               			return self.list[i];	
			               		}
			               	},
			               	{
			                	 _fname : 'runSearch',
			                	 _fbody: function(filter){
			                		 
			                		 var data = {
			                				 filter : filter
			                		 }
			                		 
			                		 self.getStoreList((Util.isValid(self.partyId) ? self.partyId : void 0),data);
			                	 }
			                },
			                {
			                	_fname  :'refresh',
			                	_fbody : function(){
			                		self.currentPage = 0;
			                		self.list = [];
			                		 self.getStoreList((Util.isValid(self.partyId) ? self.partyId : void 0));
			                	}
			                },
			               	{
			                	 _fname : 'loadMore',
			                	 _fbody: function(){
			                		 self.getStoreList(Util.isValid(self.partyId) ? self.partyId : void 0);
			                	 }
			                },
			                {
			                	_fname : 'viewInventoryStore',
			                	_fbody: function(){
			                		$state.go('menu.inventoryStoreList')
			                	}
			                }
		               ]	
		}
			
		if(_olbius)
			_olbius.init(self,_config);
			
	})