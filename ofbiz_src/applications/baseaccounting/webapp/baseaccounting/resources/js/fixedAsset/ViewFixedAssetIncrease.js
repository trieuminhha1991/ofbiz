var viewFixedAssetIncreaseObj = (function(){
	var init = function(){
		initContextMenu();
		initEventContextment();
	};
	
	var initEventContextment = function(){
		$('#jqxgrid').on('rowselect', function (event){
			var args = event.args;
			var rowdata = args.row;
			if(rowdata['isPosted']==true){
				$("#contextMenu").jqxMenu('disable', "postAcctgTrans", true);
			}else{
				$("#contextMenu").jqxMenu('disable', "postAcctgTrans", false);
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
				fixedAssetIncreaseNewObj.openWindow(data);//fixedAssetIncreaseNewObj is defined in fixedAssetIncreaseNew.js
			}else if(action=="postAcctgTrans"){
				bootbox.dialog(uiLabelMap.BACCPostedFixAssetConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 updatePostedFixAsset(data['fixedAssetIncreaseId'], true);
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
	var updatePostedFixAsset = function(fixedAssetIncreaseId,isPosted){
		Loading.show('loadingMacro');
		var data = {fixedAssetIncreaseId: fixedAssetIncreaseId};
		if(isPosted){
			data.isPosted = "Y";
		}else{
			data.isPosted = "N";
		}
		$.ajax({
			url: 'updatePostedFixedAssetAcctgTransIncrement',
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
	viewFixedAssetIncreaseObj.init();
});