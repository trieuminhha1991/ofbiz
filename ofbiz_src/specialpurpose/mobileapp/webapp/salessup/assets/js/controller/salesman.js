if(typeof app !== undefined)
	app.controller('SalesmanController',function($rootScope,$scope,$controller,$state,$stateParams,StorageFactory,EmployeeEvent,Util){
		var root = $rootScope;
		var self = $scope;
		
		self.self = self;
		
		self.currentPage = 0;
		
		self.isLoadMore = true;
		
		self.list = []
		
		self.showBack = true;
		
		self.showSelectPerson = false;
		
		self.action = $stateParams.act == 'getPerson' ? $stateParams.act : 'view';
		
		var _config = {
				$inject : {
					$controller : $controller
				},
				'event' : [
				           	{
				           		_name : 'enter',
				           		_func : function(){
				           			if(_.isEmpty(self.list))
				           				self.getListEmployee();
				           			//if get person,allow actions : 
				           			var act = $stateParams.act;
				           			if(Util.isValid(act))
				           			{
				           				if(_.isEqual(act,'getPerson'))
				           					root.showToggle = false;
				           			}else
				           				root.showToggle = true;
				           		}
				           	},
				           	{
				           		_name  : '$stateChangeStart',
				           		_func : function(event, toState, toParams, fromState, fromParams){
				           			if(!_.isEmpty(toState.name))
				           				root.showToggle = true;
				           		}
				           	}
				           ],
				 'behavior' : [
				               	{
				               		_fname : 'getListEmployee',
				               		_fbody : function(s,d){
				               			if(s === 'init')
				               				self.currentPage = 0;
				               			
				               			var data = {pagesize : config.pagesize,pagenum : self.currentPage };
				               			
				               			if(angular.isObject(d))
				               				angular.extend(data,d);
				               			
				               			EmployeeEvent.getListSalerBySup(data,function(res){
				               				if(!Util.isValid(res.data))
				               				{
				               					self.isLoadMore = false;
				               					return;
				               				}	
				               				if(!Util.isValid(res.data.listIterator) || _.isEmpty(res.data.listIterator))
				               					self.isLoadMore = false;
				               				else 	
				               				{
				               					self.isLoadMore = true;
				               					self.processList(res.data);
				               					if(!angular.isObject(d))
				               					{
				               						root.totalSaler = res.data.listIterator.length;
					               					if(root.totalSaler >= 100)
					            						root.totalSaler = root.totalSaler + "+";
				               					}	
				               					
				               				}	
				               				self.currentPage ++;
				               			},self.show,self.hide);
				               		}
				               	},
				               	{
				               		_fname : 'loadMore',
				               		_fbody : function(){
				               			self.getListEmployee();
				               		}
				               	},
				               	{
				               		_fname : 'processList',
				               		_fbody : function(des){
				               			if(_.has(des,'listIterator'))
				               				self.list = des.listIterator;
				               			if(_.has(des,'routeList'))
				               				self.addRouteInList(des.routeList);
				               		}
				               	},
				               	{
				               		_fname : 'addRouteInList',
				               		_fbody : function(list){
				               			console.log(list);
				               			if(_.isEmpty(list))
				               				return;
				               			var t = self.list
				               			for(var l in t)
				               			{
				               				t[l].routeName = '';
				               				for(var n in list)
			               					{
				               					if(!_.has(list[n],'partyId') || !_.has(t[l],'partyId'))
				               						continue;
				               					
				               					if(t[l].partyId == list[n].partyId)
				               					{
				               						var g = list[n].groupName ? list[n].groupName  : (list[n].description ? list[n].description : "");
				               						
				               						var b = g.indexOf('(');
				               						var isContinue = true;
				               						if(n < _.size(list) - 1 )
				               						{
				               							if(t[l].partyId != list[Number(n) + 1].partyId)
				               								isContinue = false;
				               						}else	
				               							isContinue = false;
				               						
				               						t[l].routeName += (g.length > 0 ?  g.substring(0,(b != -1 ? b : g.length)).trim() : '') + (isContinue ? ' -- ':'') ;
				               						
				               						if(!isContinue)
				               							break;
				               					}	
				               						
			               					}
				               			}	
				               				
				               		}
				               	},
				               	{
				               		_fname : 'getEmployeeInList',
				               		_fbody : function(i){
				               			if(_.isEmpty(self.list) || i > _.size(self.list))
				               				return {};
				               			return self.list[i];	
				               		}
				               		
				               	},
				               	{
				               		_fname  :'viewEmployeeDetail',
				               		_fbody : function(act,index){
				               			if(act == 'getPerson')
				               			{
				               				self.select(index);
				               				return;
				               			}	
				               			
				               			if(isNaN(index))
				               				return;
				               			
				               			$state.go('menu.employeeDetail',{empl : JSON.stringify(self.getEmployeeInList(index))});	
				               		}
				               	},
				               	{
				               		_fname : 'select',
				               		_fbody : function(val){
				               			
				               			if(val === true)
				               				self.showSelectPerson = self.showSelectPerson === true ? false : true
			               				else 
		               					{
			               					if(isNaN(val))
			               						return;
			               					
			               					$state.go('menu.addroute',{route : StorageFactory.getLocalItem(config.storage.newRoute),person : self.getEmployeeInList(val)})
			               					self.showSelectPerson = false;
		               					}
				               		}
				               	},
				               	{
				               		_fname : 'runSearch',
				               		_fbody: function(filter){
				               			self.getListEmployee(null,{
				               				filter : filter,
				               				pagesize : -1,
				               				pagenum  :0
				               			});
				               		}
				               	},
				               	{
				               		_fname : 'refresh',
				               		_fbody : function(){
				               			self.getListEmployee('init');
				               		}
				               	}
			               ]          
		}
		
		if(_olbius)
			_olbius.init(self,_config);
		
	})
	