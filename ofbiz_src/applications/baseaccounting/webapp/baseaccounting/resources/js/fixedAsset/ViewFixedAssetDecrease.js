var viewFixedAssetDecreaseObj = (function(){
	var init = function(){
		initContextMenu();
		initEventContextment()
	};
	
	var initEventContextment = function(){
		$('#jqxgrid').on('rowselect', function (event){
			var args = event.args;
			var rowdata = args.row;
			if(rowdata['isPosted']==true){
				$("#contextMenu").jqxMenu('disable', "postAcctgTransDescrease", true);
			}else{
				$("#contextMenu").jqxMenu('disable', "postAcctgTransDescrease", false);
			}
		});
	};
	
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 190);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var boundIndex = $("#jqxgrid").jqxGrid('getselectedrowindex');
			var data = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
			var action = $(args).attr("action");
			if(action == "edit"){
				createFixedAssetDecreaseObj.openWindow(data);//createFixedAssetDecreaseObj is defined in fixedAssetDecreaseNew.js
			}else if(action == 'postAcctgTransDescrease'){
				bootbox.dialog(uiLabelMap.BACCPostedFixAssetConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 updatePostedFixAssetAccTransDecrease(data['fixedAssetDecreaseId'], true);
							 }
						 },
						 {
							 "label" : uiLabelMap.CommonCancel,
							 "class" : "btn-danger btn-small icon-remove open-sans",
						 }
						 ]		
				);
			}
		});
	};
	
	var updatePostedFixAssetAccTransDecrease = function(fixedAssetDecreaseId,isPosted){
		Loading.show('loadingMacro');
		var data = {fixedAssetDecreaseId: fixedAssetDecreaseId};
		if(isPosted){
			data.isPosted = "Y";
		}else{
			data.isPosted = "N";
		}
		$.ajax({
			url: 'updatePostedFixAssetAccTransDecrease',
			type: "POST",
			data: data,
			success: function(response) {
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
						);
					return
				}
				$("#jqxgrid").jqxGrid('updatebounddata');
				Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {template : 'success', appendContainer : '#containerjqxgrid'});
			},
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	return{
		init: init
	}
}());
$(document).ready(function(){
	viewFixedAssetDecreaseObj.init();
});