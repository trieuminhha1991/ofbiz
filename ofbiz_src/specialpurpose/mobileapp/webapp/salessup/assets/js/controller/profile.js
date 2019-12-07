if(typeof app !== undefined)
	app.controller('ProfileController',function($scope,$rootScope,$controller,$timeout,$ionicPopover,$ionicScrollDelegate,ProfileEvent,StorageFactory,Util,popupApp){
		
		var self = $scope;
		
		self.profile = {};
		
		self.profileType = 'contact';
		
		self.buttons = [
		                {
					        text: '<b><i class="ion-ios-camera"></i></b>',
					        type: 'button-positive',
					        onTap: function(e) {
					        	console.log(e);
					        }
                       }, {
					        text: '<b><i class="fa fa-file-image-o" aria-hidden="true"></i></b>',
					        type: 'none',
					        onTap: function(e) {
					        	console.log(e);
					        }
                       }		                
	                ]
		
		var _config = {
				$inject : {
					$controller : $controller
				},
				'event' : [
				           	{
				           		_name : 'enter',
				           		_func : function(){
				           			self.renderProfile();
				           			self.initPopover();
				           		}
				           	}
				           ],
				'behavior' : [
				              {
				            		_fname : 'renderProfile',
				            		_fbody : function(){
				            			
				            			try {
				            				
				            				var profile = StorageFactory.getLocalItem(config.profile);
				            				console.log(profile)
				            				if(!Util.isValid(profile))
				            					ProfileEvent.getEmployeeInfo({},function(res){
				            						if(_.has(res,config.msg.erm) || _.has(res,config.msg.erml))
				            							return;
				            						
				            						profile = _.has(res.data,'mapInfo') ? res.data.mapInfo : {};
				            						
				            					},self.hide,self.hide);
				            				self.profile = profile;
				            				
										} catch (e) {
											console.log(e);
											throw new TypeError(config.msg.ERROR_PROCESS)
										}
				            		}
				              },
				              {
				            	_fname : 'initPopover',  
				            	_fbody : function(){
				            		  $ionicPopover.fromTemplateUrl('popover.html', {
				            		      scope: self
				            		   }).then(function(popover) {
				            		      self.popover = popover;
				            		   });
				            	  }
				              },
				              {
				            	_fname : 'scrollTop',
				            	_fbody : function(){$ionicScrollDelegate.$getByHandle('mainScroll').scrollTop();}
				              },
				              {
				            	  _fname : 'openOptions',
				            	  _fbody : function($event){
				            		  self.popover.show($event);
				            	  }
				              },
				              {
				            	  _fname : 'view',
				            	  _fbody : function($x){
				            		  self.show();
				            		  $timeout(function(){self.hide();self.scrollTop();},400)
				            		  if($x == 'base')
				            			  self.profileType = 'base';
				            		  else if($x == 'contact')
				            			  self.profileType = 'contact';
				            		  else
				            			  self.profileType = 'position';
			            			  self.popover.hide();
				            	  }
				              },
				              {
				            	  _fname : 'getPicture',
				            	  _fbody : function(){
				            		  popupApp.popup(null,{},self,self.buttons,function(res){console.log('that righttt...')})
				            	  }
				              }
				             ]           
		}
		

		if(_olbius)
			_olbius.init(self,_config);
	})
	