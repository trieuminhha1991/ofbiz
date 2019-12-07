var transGridCtxMenuObj = (function(){
	var init = function(){
		initContextMenu();
		initEvent();
	};
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 150);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#jqxgridTrans").jqxGrid('getselectedrowindex');
			var data = $("#jqxgridTrans").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "edit"){
				editAcctgTransEntryObj.openWindow(data);
			} else if (action == "remove") {
				bootbox.dialog(uiLabelMap.BACCAreYouSureCancelThisAcctgTrans,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							cancelAcctgTrans(data.acctgTransId);	
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
				);
			} else if (action == "posted") {
				bootbox.dialog(uiLabelMap.BACCAreYouSurePostedThisAcctgTrans,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								postedAcctgTrans(data.acctgTransId);	
							}	
						},
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
					);
				}
		});
	};
	var initEvent = function() {
		$("#contextMenu").on('shown', function (event) {
			var rowindex = $("#jqxgridTrans").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgridTrans").jqxGrid('getrowdata', rowindex);
			var isCanceled = dataRecord.isCanceled;
			if(isCanceled && isCanceled == 'Y') {
				$(this).jqxMenu('disable', "remove", true);
			} else {
				$(this).jqxMenu('disable', "remove", false);
			}
		});
	};
	var cancelAcctgTrans = function(data) {
		Loading.show('loadingMacro');
		$.ajax({
			url: "cancelAcctgTransOlbius",
			data: {
				acctgTransId: data
			},
			type: 'POST',
			success: function(response) {
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgridTrans', response.successMessage, {template : 'success', appendContainer : '#containerjqxgridTrans'});
					$("#jqxgridTrans").jqxGrid('updatebounddata');
				} else {
					bootbox.dialog(response.errorMessage,
						[{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
					);			
				}
			},
			complete: function() {
				Loading.hide('loadingMacro');
			}
		});
	};
	
	var postedAcctgTrans = function(data) {
		Loading.show('loadingMacro');
		$.ajax({
			url: "postAcctgTransOlb",
			data: {
				acctgTransId: data
			},
			type: 'POST',
			success: function(response) {
    			if(response._ERROR_MESSAGE_ || response._ERROR_MESSAGE_LIST_){
    				var message = typeof(response._ERROR_MESSAGE_) != "undefined"? response._ERROR_MESSAGE_ : response._ERROR_MESSAGE_LIST_[0] 
    				bootbox.dialog(message,
    						[{
    							"label" : uiLabelMap.CommonClose,
    			    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
    						}]		
    					);
    			}else{
    				Grid.renderMessage('jqxgridTrans', response._EVENT_MESSAGE_, {template : 'success', appendContainer : '#containerjqxgridTrans'});
    				$("#jqxgridTrans").jqxGrid('updatebounddata');
    			}												
			},
			complete: function() {
				Loading.hide('loadingMacro');
			}
		});
	};
	
	return{
		init: init
	}
}());

$(document).on('ready', function(){
	transGridCtxMenuObj.init();
});