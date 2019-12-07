/**
 * fixedAssetIncreaseNewStep1 variable is defined in fixedAssetIncreaseNewStep1.js file
 * fixedAssetIncreaseNewStep2 variable is defined in fixedAssetIncreaseNewStep2.js file
 */

var fixedAssetIncreaseNewObj = (function(){
	var _isEdit = false;
	var _data = {};
	var init = function(){
		initWindow();
		initWizard();
		initEvent();
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#addNewFAIncreaseWindow"), 850, 500);	
	};
	var initWizard = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.direction == "next") {
				if(info.step == 1){
					var isValid = fixedAssetIncreaseNewStep1.validate();
					if(!isValid){
						return false;
					}
				}
			}else if(info.direction == "previous"){
				
			}
		}).on('finished', function(e) {
			var valid = fixedAssetIncreaseNewStep2.validate();
			if(!valid){
				return false;
			}
			if(_isEdit){
				editFixedAssetIncrease();
			}else{
				bootbox.dialog(uiLabelMap.BACCCreateNewConfirm,
						[
						 {
							 "label" : uiLabelMap.CommonSubmit,
							 "class" : "btn-primary btn-small icon-ok open-sans",
							 "callback": function() {
								 editFixedAssetIncrease();
							 }
						 },
						 {
							 "label" : uiLabelMap.CommonCancel,
							 "class" : "btn-danger btn-small icon-remove open-sans",
						 }
						 ]		
				);
			}
		}).on('stepclick', function(e){
 			//return false;//prevent clicking on steps
 		});
	};
	
	var editFixedAssetIncrease = function(){
		Loading.show('loadingMacro');
		var data = fixedAssetIncreaseNewStep1.getData();
		data.fixedAssetIncreaseItem = JSON.stringify(fixedAssetIncreaseNewStep2.getData());
		var url = '';
		if(_isEdit){
			data.fixedAssetIncreaseId = _data.fixedAssetIncreaseId;
			url = 'editFixedAssetIncrease';
		}else{
			url = 'createFixedAssetIncrease';
		}
		$.ajax({
			url: url,
			type: "POST",
			data: data,
			success: function(response) {
				  if(response.responseMessage == "error"){
					  bootbox.dialog(response.errorMessage,
								[
								{
									"label" : uiLabelMap.CommonClose,
									"class" : "btn-danger btn-small icon-remove open-sans",
								}]		
						);		
				  }else{
					  $("#addNewFAIncreaseWindow").jqxWindow('close');
					  $("#jqxgrid").jqxGrid('updatebounddata');
					  Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	
	var initEvent = function(){
		$("#addNewFAIncreaseWindow").on('open', function(event){
			fixedAssetIncreaseNewStep1.windownOpenInit(_isEdit, _data);
			fixedAssetIncreaseNewStep2.windownOpenInit(_isEdit, _data);
		});
		$("#addNewFAIncreaseWindow").on('close', function(event){
			fixedAssetIncreaseNewStep1.resetData();
			fixedAssetIncreaseNewStep2.resetData();
			$('#fuelux-wizard').wizard('previous');
			_isEdit = false;
			_data = {};
		});
	};
	var openWindow = function(data){
		_data = data;
		_isEdit = true;
		accutils.openJqxWindow($("#addNewFAIncreaseWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	fixedAssetIncreaseNewObj.init();
});