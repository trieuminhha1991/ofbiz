if(app !== undefined)
	app.controller('LeaveDetailController',function($scope,$controller,$stateParams,$log,$ionicPopover,EmployeeEvent,Util,popupApp){
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
				        		   var leave = $stateParams.leave;
				        		   if(Util.isValid(leave))
				        			   self.leave = angular.fromJson(leave);
				        		   self.fixData();
				        		   self.initPopover();
				        	   }
				           }
	           ],
	           'behavior' : [{
	        	  _fname :  'fixData',
	        	  _fbody : function(){
		        		 if(_.has(self.leave,'fromDate'))
		        			 self.leave._fromDate = utils.formatDDMMYYYY(self.leave.fromDate);
		        		 if(_.has(self.leave,'thruDate'))
		        			 self.leave._thruDate = utils.formatDDMMYYYY(self.leave.thruDate);
	        	  }
	           },
	           {
	            	_fname : 'initPopover',  
	            	_fbody : function(){
	            		  $ionicPopover.fromTemplateUrl('popoverApproveLeave.html', {
	            		      scope: self
	            		   }).then(function(popover) {
	            		      self.popover = popover;
	            		   });
	            	  }
	              },
	              {
	            	  _fname : 'openOptions',
	            	  _fbody : function($event){
	            		  self.popover.show($event);
	            	  }
	              },
	              {
	            	  _fname : 'confirm',
	            	  _fbody : function($x){
	            		  popupApp.showConfirm(self.getLabel('ApproveLeaveConfirm') + " (" + self.renderStatus($x) + ")",null,function(res){
	            			  if(res === true)
            				  {
	            				  if($x == 'a')
	    	            			  self.leaveApproveResult = 'LEAVE_APPROVED';
	    	            		  else if($x == 'r')
	    	            			  self.leaveApproveResult = 'LEAVE_REJECTED';
	    	            		  else
	    	            			  self.leaveApproveResult = 'LEAVE_CANCEL';
	    	            		  
	    	            		  self.sendApprove();
            				  }else
	            				  return;
	            			 
	            		  },self);
	            	  }
	              },
	              {
	            	  _fname : 'view',
	            	  _fbody : function($x){
	            		  self.confirm($x);
	            	  }
	              },
	              {
	            	  _fname : 'sendApprove',
	            	  _fbody : function(){
	            		  var data = {statusId : self.leaveApproveResult,emplLeaveId : self.leave.emplLeaveId,commentApproval : self.leave.note};
	            		  
	            		  EmployeeEvent.approvalEmplLeaveBySup(data,function(res){
	            			  if(_.has(res,config.msg.erm) || _.has(res,config.msg.erml))
	            				  popupApp.alert(self.getLabel('ApprovedFailed'),null,null);
	            			  else
            				  {
	            				  popupApp.alert(self.getLabel('ApprovedSuccess'),null,null);
	            				  self.leave.statusDes = self.renderStatus();
            				  }
	            			  self.popover.hide();
	            		  },self.show,self.hide);
	            	  }
	              },
	              {
	            	  _fname : 'renderStatus',
	            	  _fbody : function($x){
	            		 if(_.isEmpty(self.leaveApproveResult))
            			 {
	            			  if($x == 'a')
		            			  self.leaveApproveResult = 'LEAVE_APPROVED';
		            		  else if($x == 'r')
		            			  self.leaveApproveResult = 'LEAVE_REJECTED';
		            		  else
		            			  self.leaveApproveResult = 'LEAVE_CANCEL';
            			 } 
	            		  
    					  switch(self.leaveApproveResult){
        					  case 'LEAVE_APPROVED' : 
        						return self.getLabel('accept');
        					  case 'LEAVE_REJECTED' : 
        					  	return self.getLabel('reject');
        					  case 'LEAVE_CANCEL' :
        						return self.getLabel('cancelLeave');
        					  default : 
        						  break;
    					  }
	            	  }
	              }
	           ]
		}
		
		if(_olbius)
			_olbius.init(self,_config);
		
	})