	$(function(){
		RouteCustomerOlb.init();
	})
	
	var RouteCustomerOlb = (function(){
		
		var init = function(){
			initJqxWindow();
			initNotification();
			bindEvent();
		}
		
		var submitRouteCus = function(){
			var tmp = listCustomerDistribution.jqxGrid('getboundrows');
				   	  	var arrData = [];
				   	  	var arrVal = [];
				   	  	var grid = $('#listCustomerOfRoute' + indexGlobal);
				   	  	if(tmp && tmp.length > 0){
				   	  		for(var i = 0 ;i < tmp.length ; i++){
				   	  			if(tmp[i].partyId){
				   	  				arrData.push({
				   	  					partyId : tmp[i].partyId,
				   	  					routeId : rtIdCus
				   	  				});
				   	  				arrVal.push({
				   	  					partyId : tmp[i].partyId,
				   	  					groupName : tmp[i].groupName
				   	  				});
				   	  				}
				   	  		}
				   	  	}
				   	 
				   	  	if(arrData && arrData.length > 0){
				   	  		$.ajax({
					   	  		url : 'distributionRouteStores',
					   	  		data : {
					   	  			listDistributionStores : JSON.stringify(arrData)
					   	  		},
					   	  		async : false,
					   	  		type : 'POST',
					   	  		dataType : 'json',
					   	  		success : function(response){
					   	  			$.each(indexSelectedTmp,function(index){
										var tmp = {
					   	  					partyId : indexSelectedTmp[index].partyId,
					   	  					groupName : indexSelectedTmp[index].groupName
					   	  					};		
					   	  			})
					   	  			indexSelectedTmp = new Array();	
					   	  			gridCustomer.jqxGrid('updatebounddata');
					   	  			alertMessage(success,true);
					   	  			var tmp = grid.jqxGrid('addrow',null,arrVal);
					   	  		},
					   	  		error : function(request,status,xhr){
					   	  			alertMessage(error,true);	
					   	  		}
					   	  	});
				   	  	}
				};
		
				var cancelRouteCus = function(){
						routePopupCus.jqxWindow('close');
					}
			
				var bindEventCustom = function(id){
					id.bind('dropTargetEnter',function(event){
							event.preventDefault();
							$(event.args.target).css('border','2px solid #000');
							id.jqxDragDrop('dropAction','none');	
							id.jqxDragDrop({revert : false});
						});	
					id.bind('dropTargetLeave',function(event){
							event.preventDefault();
							$(event.args.target).css('border','2px solid #000');
							$(this).jqxDragDrop('dropAction','none');	
						});	
					id.bind('dragEnd',function(event){
						event.preventDefault();
						var cell = gridCustomer.jqxGrid('getselectedcell');
						if(cell === 'undefined' || cell == null){
							return;
						}
						var data = gridCustomer.jqxGrid('getrowdata',cell.rowindex);
					     listCustomerDistribution.css('border', '2px dashed #aaa');
					        var position = $.jqx.position(event.args);
					        var cellDis = listCustomerDistribution.jqxGrid('getcellatposition', position.left, position.top);
					        var tmp = listCustomerDistribution.jqxGrid('getrows');
					        for(var i=0;i<tmp.length;i++){
					        	if(data.partyId==tmp[i].partyId){
					        		return false;
								}
					        }
					        if (cellDis != null) {
					       	 	listCustomerDistribution.jqxGrid('addrow',null,data);
					       	 	indexSelectedTmp.push(data);
					        }
						});	
					};	
			
					var initGridCustomersNotAssign = function(){
						var dataFieldCus = [
						  	              {name : "partyId", type:"String"},
						  	              {name: "groupName", type: "String"}
						                ];
					  	var columncus = [
					  						{
					  							text : uiLabelMap.BSCustomerId,
					  							datafield : 'partyId',
					  							width : '200px'
					  						},
					  						{
					  							text : uiLabelMap.BSCustomerName,
					  							datafield : 'groupName'
					  						}
					  					];
					  	
					  	new OlbGrid('#gridCustomer',null, {
					  		url : 'JQGetListCustomerOfUserLogin&partyId=${parameters.userLogin.partyId?if_exists}',
					  		width : '100%',
					  		selectionmode: 'singlecell',
					  		showfilterrow : true,localization: getLocalization(),
					  		autoHeight : true,
					  		datafields  :dataFieldCus,
					  		columns : columncus,
					  		useUtilFunc : true
					  		}
					  		,null);
					  	
					  	initGridCustomerAssigned(dataFieldCus,columncus);
					}
			
					var initGridCustomerAssigned = function(datafields,columns){
						new OlbGrid('#listCustomerDistribution',null, {
					  		url : 'JQGetListCustomerOfUserLogin&partyId=${parameters.userLogin.partyId?if_exists}',
					  		width : '100%',
					  		selectionmode: 'singlecell',
					  		showfilterrow : true,localization: getLocalization(),
					  		autoHeight : true,
					  		pagesize : 5,
							pagesizeoptions: ['5', '10', '15'],
							pageable: true,
					  		datafields  :datafields,
					  		columns : columns,
					  		useUtilFunc : true
					  		}
					  		,null);
						
					}
			
					var bindDragDropCus = function(){
						var tmp  = gridCustomer.find("div[role='row']");
						for(var i = 0 ;i < tmp.length ; i++){	
							(function(i){
								$('#' + tmp[i].id).jqxDragDrop({dropTarget : listCustomerDistribution,revert : true, appendTo: 'body',  dragZIndex: 99999,
				                    dropAction: 'none',
				                    initFeedback: function (feedback) {
				                        feedback.height(25);
				                    }});
								bindEventCustom($('#' + tmp[i].id));
							})(i);
							
						}
					}
			
					var bindEvent = function(){
						gridCustomer.on('bindingComplete',function(){
							bindDragDropCus();
						});	

						routePopupCus.on('open', function(){
							bindDragDropCus();
						})
						
						routePopupCus.on('close',function(){
							routePopupCus.css('display','none');
							indexSelectedTmp = new Array();
						});
						
						
					}
				
				var initJqxWindow = function(){
					routePopupCus.jqxWindow({ width: 800, height : 500,resizable: true,  isModal: true, autoOpen: false, modalOpacity: 0.7,draggable : false});
				}
	
				var displayPopupCus = function(routeId,index){
						rtIdCus = routeId;
						indexGlobal = index;
						routePopupCus.css('display','block');
						routePopupCus.jqxWindow('open');
				}		
					
				var initNotification = function(){
					jOlbUtil.notification.create('#notification','#messageNotificationCus','success');
					jOlbUtil.notification.create('#notification','#messageNotificationErrorCus','error');
				}
					
				var removeCustomer = function(index){
					initDialog(deleteDialogCus,
						 function(){
		    					$(this).dialog('close');
		    					$('#listCustomerOfRoute'+index).jqxGrid('clearfilters');
		    					var cell = $('#listCustomerOfRoute'+index).jqxGrid('getselectedcell');
			 			   		if(!cell) return;
			 			   		var data =   $('#listCustomerOfRoute'+index).jqxGrid('getrowdata',cell.rowindex);
			 			   		if(data){
				 			   		sendRequestRemove(data,'removeCustomerOutRoute',
				 			   				function(response,status,xhr){
					 			   				if(response._ERROR_MESSAGE_ || response._ERROR_MESSAGE_LIST_){
								   					var mess = response._ERROR_MESSAGE_ ? response._ERROR_MESSAGE_ : response._ERROR_MESSAGE_LIST
						 			   				alertMessage(routePopupCus,messageNotificationCus,mess,false);
					 			   				}else{
					 			   					$('#listCustomerOfRoute'+index).jqxGrid('deleterow',cell.rowindex);
					 			   					var obj = {'partyId': data.partyId,'groupName' : data.groupName};
					 			   					dataCs.push(obj);
					 			   					var mess = response._EVENT_MESSAGE_ ? response._EVENT_MESSAGE_ : 'success'
						 			   				alertMessage(routePopupCus,messageNotificationCus,mess,false);
					 			   				}
					 			   			}
							   		);
			 			   		}
			 	   			}
					  	);
					}	
				
		return {
			submitRouteCus : submitRouteCus,
			cancelRouteCus : cancelRouteCus,
			bindEventCustom : bindEventCustom,
			removeCustomer : removeCustomer,
			init: init
		}
	}())
	
	
	
	
	
