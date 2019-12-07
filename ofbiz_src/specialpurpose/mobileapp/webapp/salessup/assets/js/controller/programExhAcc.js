if(app !== undefined)
	app.controller('ProgramExhAccController',function($rootScope,$scope,$controller,$state,PromotionEvent,Util){
		var root = $rootScope;
		
		var self = $scope;
		
		self.self = self;
		
		self.currentPage = 0;
		
		self.list = [];
		
		var _config = {
				$inject : {
					$controller : $controller
				},
				'event' : [{
	           		_name : 'enter',
	           		_func : function(){
	           			self.getListProgram();
	           		}
	           	}],
	           	'behavior': [
       	            {
		               		_fname : 'getListProgram',
		               		_fbody : function(){
		               			PromotionEvent.getListExhAcc({pagesize : config.pagesize,pagenum : self.currentPage },function(res){
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
		               					self.processList(self.list,res.data.listIterator);
		               					if(_.has(res.data,'TotalRows'))
		               					{
		               						root.totalProgram = res.data.TotalRows;
			    							if(root.totalProgram >= 100)
			    								root.totalProgram = root.totalProgram + "+";
		               					}	
		               				}	
		               				self.currentPage ++;
		               			},self.show,self.hide);
		           			}
               		},
       	            {
	               		_fname : 'loadMore',
	               		_fbody : function(){
	               			self.getListProgram();
	               		}
	               	},
	               	{
			        	   '_fname' : 'getStatusDes',
			        	   '_fbody' : function(des){
			        		   var _d = '';
			        		   switch(des)
			        		   {
			        		   	case 'PROMO_ACCEPTED' : 
			        		   		_d = 'Đã duyệt'
			        		   		break;	
			        		   	case 'PROMO_CREATED' : 
			        		   		_d =  'Mới tạo'
		        		   			break;
			        		   default :
			        			   _d = 'Mới tạo';
			        		   		break;
			        		   }
			        		   return _d;
			        	   }
	               	},
	               	{
	               		_fname : 'getProgramInList',
	               		_fbody : function(i){
	               			if(_.isEmpty(self.list) || i > _.size(self.list))
	               				return {};
	               			return self.list[i];	
	               		}
	               		
	               	},
	            	{
	               		_fname : 'prepare',
	               		_fbody : function(src){
	               			var _src = [];
	               			angular.forEach(src,function(e){
	               				e._status_ = self.getStatusDes(e.statusId);
	               				_src.push(e);
	               			})	
	               			return _src;
	               		}
	               		
	               	},
	               	{
	               		_fname : 'processList',
	               		_fbody : function(src,des){
	               			if(_.isEmpty(des))
	               			{
	               				self.list = !Util.isValid(src) || !_.isArray(src) ? [] : self.prepare(src);
	               				return;
	               			}	
	               			
	               			if(_.isEmpty(src))
	               			{
	               				self.list =  !Util.isValid(des) || !_.isArray(des) ? [] : self.prepare(des);
	               				return;
	               			}	
	               			
	               			self.prepare(des);
	               			
	               			self.list = utils ? utils.removeDuplicate(src) : src;
	               			self.currentPage++;
	               		}
	               	},
	                {
	               		_fname : 'viewProgramDetail',
	               		_fbody : function(i){
	               			$state.go('menu.programExhAccDetail',{programId : self.getProgramInList(i).productPromoId})
	               		}
	               	}
	           	]
               			
		}
		
		if(_olbius)
			_olbius.init(self,_config);
		
	})
	
	
	
	
	
	
	
	