$(function(){
	routeEmplOlb.init();
})	

var routeEmplOlb = (function(){
		var init = function(){
			initjqxWindow();	
			initNotification();
		}
		
		var initjqxWindow = function(){
			routePopup.jqxWindow({ width: 800, height : 425,resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7,draggable : false});
		}
		
		var displayPopup = function(routeId,index){
			rtId = routeId;
			indexGlobal = index;
			routePopup.css('display','block');
			routePopup.jqxWindow('open');
		};
		
		var bindEvent = function(){
			routePopup.on('open',function(){
				bindDragDrop();
			})
			routePopup.on('close',function(){
				routePopup.css('display','none');
			});
		}
		
		var initNotification = function(){
			jOlbUtil.notification.create('#notification','#messageNotification','success');
			jOlbUtil.notification.create('#notification','#messageNotificationError','error');
		}
		
	  var removeSM = function(index){
				  initDialog(deleteDialog,
					 function(){
							var cell = $('#listSMOfRoute'+index).jqxGrid('getselectedcell');
					   		var data =   $('#listSMOfRoute'+index).jqxGrid('getrowdata',cell.rowindex);
					   		if(data) sendRequestRemove(data,'removeSMOutRoute',
					   				function(response,status,xhr){
						   				$('#listSMOfRoute'+index).jqxGrid('deleterow',cell.rowindex);
						   				if(response._ERROR_MESSAGE_ || response._ERROR_MESSAGE_LIST_){
						   					var mess = response._ERROR_MESSAGE_ ? response._ERROR_MESSAGE_ : response._ERROR_MESSAGE_LIST_ ;
						   					alertMessage(routePopup,messageNotification,mess,false);
						   				}else{
						   					var mess = response._EVENT_MESSAGE_ ? response._EVENT_MESSAGE_ : 'success';
						   					alertMessage(routePopup,messageNotification,mess,false);
						   				}
						   			}
					   		);
					   		$(this).dialog('close');
						}
				  	);
	    	   };

		return {
			init : init ,
			removeSM  : removeSM 
		}
	}())	
	
   
