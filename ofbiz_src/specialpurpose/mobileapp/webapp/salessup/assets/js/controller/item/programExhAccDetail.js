if(app !== undefined)
		app.controller('ProgramExhAccDetailController',function($scope,$controller,$stateParams,$window,$log,$timeout,Util,PromotionEvent,popupApp){
			var self = $scope;
			
			self.promoDetail = {};
			
			var _config = {
					$inject : {
						$controller : $controller
					},
					'event' : [
					           {
					        	   _name : 'enter',
					        	   _func : function(){
//					        		   self.resizeSlide();
					        		   
					        		   var programId = $stateParams.programId;
					        		   
					        		   if(Util.isValid(programId))
					        			   self.getDetail(programId);
					        	   }
					           }
		           ],
		           'behavior' : [{
		        	   _fname : 'resizeSlide',
		        	   _fbody : function(index){
		        		   index = typeof index !== undefined ? index : 0;
		        		   
		        		   var winHeight = $window.innerHeight;
		        		   var contentHeight = $('.form-slide.content').height()
		        		   var contentRules = $('.form-slide.rules').height()
		        		  var slideHeight =  winHeight > contentHeight ? winHeight : (contentHeight + 40);
		        		  slideHeight = slideHeight >  contentRules ? slideHeight  :contentRules;
		        		  
		        		  index = index == 0 ? slideHeight = contentHeight : (index == 1 ? slideHeight = contentRules : winHeight);
		        		   $('.slide-box').css('height',index == 0 ? slideHeight + 50 : slideHeight);
		        	   }
		           },{
		        	   _fname : 'getDetail',
		        	   _fbody : function(id){
		        		   PromotionEvent.getDetailProgramExhAcc({'promoId' : id},function(res){
	               				if(!Util.isValid(res.data))
	               				{
	               					return;
	               				}	
	               				if(!Util.isValid(res.data.content) || _.isEmpty(res.data.content))
	               					return;
	               				else 	
	               				{
	               					self.promoDetail = res.data.content;
	               					if(_.has(self.promoDetail,'productPromo'))
	               					{
	               						self.promoDetail._statusId_ = self.getStatusDes(self.promoDetail.productPromo.statusId);
	               						self.promoDetail._requireCode_ = self.promoDetail.productPromo.requireCode == 'Y' ? 'Y' : 'N';
	               						var time =  self.promoDetail.productPromo.fromDate;
	               						self.promoDetail.fromDate = Util.isValid(time) ? utils.formatDDMMYYYY(time.time) : time;
	               						time =  self.promoDetail.productPromo.thruDate;
	               						self.promoDetail.thruDate = Util.isValid(time) ? utils.formatDDMMYYYY(time.time) : time;
	               						
	               						self.prepatePromoContent(self.promoDetail);
	               						
	               					}	
	               				}	
	               			},self.show,self.hide);
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
		        	   '_fname' : 'prepatePromoContent',
		        	   '_fbody' : function(data){
		        		   self.promoContent = {};
		        		   if(!_.has(data,'productPromoRules') || !_.has(data,'mapRuleDetail'))
		        			   return;
		        		   
		        		   for(var k in data['productPromoRules']){
		        			   var obj = data['productPromoRules'][k];
		        			   var map = data['mapRuleDetail'];
		        			   if(!Util.isValid(obj))
		        				   continue
		        			   
	        				   var ruleid = obj['productPromoRuleId'];
		        			   var rName = _.has(obj,'ruleName') ?  obj['ruleName'] + ' (' + ruleid  + ')': ruleid;
		        			   	self.promoContent[ruleid] = {'ruleName' : rName};
        				   		if(_.has(map,'productsCondsList_' + ruleid))	
	        				   		self.promoContent[ruleid]['contentConds'] = map['productsCondsList_' + ruleid]
	        				 	if(_.has(map,'condsName_' + ruleid))	
	        				   		self.promoContent[ruleid]['conds'] = map['condsName_' + ruleid]
	        				   	if(_.has(map,'actionsName_' + ruleid))	
	        				   		self.promoContent[ruleid]['acts'] = map['actionsName_' + ruleid]
	        					if(_.has(map,'productActsList_' + ruleid))	
	        				   		self.promoContent[ruleid]['contentActs'] = map['productActsList_' + ruleid]
		        		   }
		        		   
		        		   $timeout(function(){
		        			   	self.resizeSlide();
		        		   },200);
		        	   }
		           },
		           {
		        	   '_fname' : 'getActionStatus',
		        	   '_fbody' : function(r){
		        		   if(r === true)
		        			   return 'PROMO_ACCEPTED'
		        		return 'PROMO_CANCELLED'	   
		        	   }
		           },
		           {
		        	   '_fname' : 'approvePromotion',
		        	   '_fbody' : function(result){
		        		   var msg = _.isEmpty(self.promoDetail) ?self.getLabel('NotPromoValid') : self.getLabel('ApprovedConfirm');
		        		   popupApp.showConfirm(msg,null,function(res){
		        			   if(_.isEmpty(self.promoDetail)) 
		        				   return;
		        			   
		        			   if(self.promoDetail.productPromo.statusId == 'PROMO_ACCEPTED')
		        			   {
		        				   popupApp.alert(self.getLabel('ApproveNotify'),'',null)
		        				   return;
		        			   }   	
	        				   if(res === true)
	        				   {
		        				   if(result === true)
		        					   PromotionEvent.acceptPromotion({statusId : self.getActionStatus(result),productPromoId : self.promoDetail.productPromoId},function(res){
		        						   if(Util.isValid(res[config.emg.erm]) || Util.isValid(res[config.emg.erml]))
			   	               				{
		        							   popupApp.alert(getLabel('ApprovedFailed'),'',null);
			   	               					return;
			   	               				}else{
			   	               					popupApp.alert(getLabel('ApprovedSuccess'),'',null);
			   	               					$timeout(function(){
			   	               						$state.go('menu.programExhAcc');
			   	               					},1000);
			   	               				}	
		        					   },self.show,self.hide);
		        				   else 
		        					   PromotionEvent.cancelPromotion({statusId : self.getActionStatus(result),productPromoId : self.promoDetail.productPromoId,changeReason : ''},function(res){
		        						   if(Util.isValid(res[config.emg.erm]) || Util.isValid(res[config.emg.erml]))
			   	               				{
		        							   popupApp.alert(getLabel('ApprovedFailed'),'',null);
			   	               					return;
			   	               				}else{
			   	               					popupApp.alert(getLabel('ApprovedSuccess'),'',null);
			   	               					$timeout(function(){
			   	               						$state.go('menu.programExhAcc');
			   	               					},1000);
			   	               				}	
		        					   },self.show,self.hide);
	        				   }
		        		   });
		        	   }
		           }
	           ]
			}
			
			if(_olbius)
				_olbius.init(self,_config);
			
		})
	
	
	