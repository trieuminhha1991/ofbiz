var viewFixedAssetDepreciationCalcObj = (function(){
	var init = function(){
		initContextMenu();
		initEventContextment();
	};
	

	var initEventContextment = function(){
		$('#jqxgrid').on('rowselect', function (event){
			var args = event.args;
			var rowdata = args.row;
			if(rowdata['isPosted']==true){
				$("#contextMenu").jqxMenu('disable', "postAcctgTransDepreciation", true);
			}else{
				$("#contextMenu").jqxMenu('disable', "postAcctgTransDepreciation", false);
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
				fixedAssetDepreciationCalcNewObj.openWindow(data);//fixedAssetDepreciationCalcNewObj is defined in fixedAssetDepreciationCalcNew.js
			}else if(action == "postAcctgTransDepreciation"){
				bootbox.dialog(uiLabelMap.BACCPostedFixAssetConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 updatePostedFixAssetDeprecation(data['depreciationCalcId'], true);
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
	
	var updatePostedFixAssetDeprecation = function(depreciationCalcId,isPosted){
		Loading.show('loadingMacro');
		var data = {depreciationCalcId: depreciationCalcId};
		if(isPosted){
			data.isPosted = "Y";
		}else{
			data.isPosted = "N";
		}
		$.ajax({
			url: 'updatePostedFixedAssetAcctgTransDeprecation',
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
	viewFixedAssetDepreciationCalcObj.init();
});