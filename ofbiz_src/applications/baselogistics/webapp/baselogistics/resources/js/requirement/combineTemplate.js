$(function(){
	CombineObj.init();
});
var CombineObj = (function() {
	var init = function() {
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		if (statusId != "REQ_APPROVED"){
			viewRequirementDetail(requirementId);
		}
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				var selectedIndexs = $('#jqxgridItemLabelFrom').jqxGrid('getselectedrowindexes');
				if(selectedIndexs.length <= 0){
				    bootbox.dialog(uiLabelMap.YouNotYetChooseProduct, [{
		                "label" : uiLabelMap.OK,
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
				    return false;
				} else {
					for (var i = 0; i < selectedIndexs.length; i ++){
						var data = $('#jqxgridItemLabelFrom').jqxGrid('getrowdata', selectedIndexs[i]);
						listInventoryItemFrom.push(data);
					}
				} 
			} else if(info.step == 2 && (info.direction == "next")) {
				var selectedIndexs = $('#jqxgridItemLabelTo').jqxGrid('getselectedrowindexes');
				if(selectedIndexs.length <= 0){
				    bootbox.dialog(uiLabelMap.YouNotYetChooseProduct, [{
		                "label" : uiLabelMap.OK,
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
				    return false;
				} else {
					for (var i = 0; i < selectedIndexs.length; i ++){
						var data = $('#jqxgridItemLabelTo').jqxGrid('getrowdata', selectedIndexs[i]);
						listInventoryItemTo.push(data);
					}
				} 
				showConfirmPage();
			}
		}).on('finished', function(e) {
			jOlbUtil.confirm.dialog(uiLabelMap.AreYouSureExecuted, function() {
				Loading.show('loadingMacro');
            	setTimeout(function(){
            		finishCombineProduct();
	            	Loading.hide('loadingMacro');
            	}, 500);
            });
		}).on('stepclick', function(e){
			//prevent clicking on steps
		});
	};
	
	function showConfirmPage(){
		var listAllInventoryItem = [];
		var selectedIndexs1 = $('#jqxgridItemLabelFrom').jqxGrid('getselectedrowindexes');
		var selectedIndexs2 = $('#jqxgridItemLabelTo').jqxGrid('getselectedrowindexes');
		for (var i = 0; i < selectedIndexs1.length; i ++){
			var data = $('#jqxgridItemLabelFrom').jqxGrid('getrowdata', selectedIndexs1[i]);
			listAllInventoryItem.push(data);
		}
		for (var i = 0; i < selectedIndexs2.length; i ++){
			var data = $('#jqxgridItemLabelTo').jqxGrid('getrowdata', selectedIndexs2[i]);
			listAllInventoryItem.push(data);
		}
		var tmpSource = $("#jqxgridInventoryItemSelected").jqxGrid('source');
		if(typeof(tmpSource) != 'undefined'){
			tmpSource._source.localdata = listAllInventoryItem;
			$("#jqxgridInventoryItemSelected").jqxGrid('source', tmpSource);
		}
	}
	
	function finishCombineProduct(){
		var listInventoryItemFrom = [];
		var listInventoryItemTo = [];
		var selectedIndexs1 = $('#jqxgridItemLabelFrom').jqxGrid('getselectedrowindexes');
		var selectedIndexs2 = $('#jqxgridItemLabelTo').jqxGrid('getselectedrowindexes');
		for (var i = 0; i < selectedIndexs1.length; i ++){
			var data = $('#jqxgridItemLabelFrom').jqxGrid('getrowdata', selectedIndexs1[i]);
			listInventoryItemFrom.push(data);
		}
		for (var i = 0; i < selectedIndexs2.length; i ++){
			var data = $('#jqxgridItemLabelTo').jqxGrid('getrowdata', selectedIndexs2[i]);
			listInventoryItemTo.push(data);
		}
		var listInventoryItemFromJson = JSON.stringify(listInventoryItemFrom);
		var listInventoryItemToJson = JSON.stringify(listInventoryItemTo);
		
		$.ajax({
            type: "POST",
            url: "createInventoryItemLabelApplFromRequirements",
            data: {
            	requirementId: requirementId,
            	listInventoryItemIdFroms: listInventoryItemFromJson,
            	listInventoryItemIdTos: listInventoryItemToJson,
            },
            dataType: "json",
            async: false,
            success: function(response){
            	viewRequirementDetail(requirementId);
            },
            error: function(response){
              alert("Error:" + response);
            }
		});
	}
	
	function viewRequirementDetail(requirementId){
		window.location.href = 'viewRequirementDetail?requirementId=' + requirementId;
	}
	var initValidateForm = function(){
		
	};
	return {
		init: init,
	}
}());