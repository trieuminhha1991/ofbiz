if(typeof app !== undefined)
	app.controller('LeaveController',function($scope,$rootScope,$controller,$state,EmployeeEvent,Util){
		var root = $rootScope;
		
		var self = $scope;	
		
		self.self = self;
	
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
				           			if(angular.isUndefined(self.listOrg))
				           				self.getListOrgManagedByParty();
				           			else
				           				self.getListEmplLeave('init');
				           		}
				           	}
				           ],
				'behavior' : [
				              {
								_fname : 'getListEmplLeave',
								_fbody : function(state,_data){
										if(state == 'init')
										{
											self.currentPage = 0;
											self.list = [];
										}	
											
										var data = {pagenum : self.currentPage,pagesize : config.pagesize};
										
										if(!_.has(data,'year'))
											data.year = new Date().getYear() + 1900;
										
										if(!_.has(data,'partyId'))
											data.partyId = self.listOrg[0];
											
										if(Util.isValid(_data))
											_.extend(data,_data);
										
										EmployeeEvent.getEmplLeaveList(data,function(res){
											 if(!_.has(res,config.msg.erm) && !_.has(res,config.msg.erml) && _.has(res.data,'listIterator'))
											 self.processLeave(res.data.listIterator);
											 else self.isLoadMore = false;
										},self.show,self.hide);
									}
				              },
				              {
				                	 _fname : 'loadMore',
				                	 _fbody: function(){
				                		 self.getListEmplLeave();
				                	 }
			                 },
				             {
				               		_fname : 'viewLeaveDetail',
				               		_fbody : function(index){
				               			
				               			if(isNaN(index))
				               				return;
				               			
				               			$state.go('menu.leaveDetail',{leave : JSON.stringify(self.getLeaveInList(index))});	
				               		}
				            },
				          	{
				               		_fname : 'getLeaveInList',
				               		_fbody : function(i){
				               			if(_.isEmpty(self.list) || i > _.size(self.list))
				               				return {};
				               				
				               			return self.list[i];	
				               		}
			               	},
			                {
			                	_fname : 'processLeave', 
			                	_fbody : function(obj){
			                		 if(obj && _.isArray(obj) && obj.length != 0)
		                				{
		                					angular.forEach(obj,function(value,index){
		                						self.list.push(value);
		                					})
		                					self.isLoadMore = true;
		                					self.currentPage++;
		                				}else self.isLoadMore = false;
			                	 }
			                 },
				             {
				            	  _fname : 'getListOrgManagedByParty',
				            	  _fbody : function(){
				            		  EmployeeEvent.getListOrgManagedByParty().then(function(res){
				            			  if(!_.has(res,config.msg.erm) && !_.has(res,config.msg.erml) && _.has(res.data,'resultList'))
				            			  {
				            				  self.listOrg = res.data.resultList;
				            				  if(!_.isEmpty(self.listOrg))
							           				self.getListEmplLeave();
				            			  }
				            		  })
				            	  }
				             },
				             {
			                	 _fname : 'runSearch',
			                	 _fbody: function(filter){
			                		 
			                		 var data = {
			                				 filter : filter,
			                				 pagesize : -1,
			                				 pagenum : 0
			                		 }
			                		 
			                		 self.getListEmplLeave(null,data);
			                	 }
			                 }
			              ]           
				}
		
		if(_olbius)
			_olbius.init(self,_config);
		
	})
	