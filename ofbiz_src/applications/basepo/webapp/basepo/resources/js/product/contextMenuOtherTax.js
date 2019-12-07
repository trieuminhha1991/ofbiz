$(function(){
	ctxMenuOtherTax.init();
});

var ctxMenuOtherTax = (function(){
	var init = (function(){
		initElement();
		initEvent();
	});
	var initElement = function(){
		$("#contextMenu").jqxMenu({ width: 200, height: 60, autoOpenPopup: false, mode: 'popup'});
	};
	var initEvent = function(){
		$("#contextMenu").unbind('itemclick').on('itemclick', function (event) {
			var rowindex = $("#jqxGridOtherTax").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxGridOtherTax").jqxGrid('getrowdata', rowindex);
	        checkAction(dataRecord, event);
			
		});
		
		
		function checkAction(dataRecord, event){
			var args = event.args;
			var rowindex = $("#jqxGridOtherTax").jqxGrid('getselectedrowindex');
	        var dataRecord = $("#jqxGridOtherTax").jqxGrid('getrowdata', rowindex);	  
			if($(args).attr("action") == 'removeOtherTax'){
				removeOtherTax(dataRecord.productCategoryId, productId, dataRecord.fromDate);
			}else if($(args).attr("action") == 'refresh'){
				$('#jqxGridOtherTax').jqxGrid('updatebounddata');
			}
		};
		
		function openJqxWindow(jqxWindowDiv){
			var wtmp = window;
			var tmpwidth = jqxWindowDiv.jqxWindow('width');
			jqxWindowDiv.jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 80 } });
			jqxWindowDiv.jqxWindow('open');
		};
		
		
		var removeOtherTax = function( productCategoryId, productId , fromDate ) {
			bootbox.dialog(uiLabelMap.AreYouSureRemove, 
					[{"label": uiLabelMap.CommonCancel, 
						"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
			            "callback": function() {bootbox.hideAll();}
			        }, 
			        {"label": uiLabelMap.OK,
		            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		            "callback": function() {
						
				    	setTimeout(function(){
				    		Loading.show('loadingMacro');
				    		$.ajax({
				    			url : 'removeOtherTax',
				    			type : "POST",
				    			data : {
				    				categoryId: productCategoryId,
				    				productId: productId,
				    				fromDate: fromDate.getTime()
				    			},
				    			beforeSend: function(){
				    				
				    			},
				    			success : function(data) {
				    				$('#container').empty();
				                    $('#jqxNotification').jqxNotification({ template: 'success'});
				                    $("#notificationContent").text(uiLabelMap.RemoveSuccess);
				                    $("#jqxNotification").jqxNotification("open");
				                    $('#jqxGridOtherTax').jqxGrid('updatebounddata');
				    			},
				    			error: function(data){
				    				alert("Send request is error");
				    			},
				    			complete : function(jqXHR, textStatus) {
				    			}
				    		});
				            Loading.hide('loadingMacro');
				    	}, 500);
		            }
		        }]);
		};
	};
	
	return {
		init: init,
	}
}());