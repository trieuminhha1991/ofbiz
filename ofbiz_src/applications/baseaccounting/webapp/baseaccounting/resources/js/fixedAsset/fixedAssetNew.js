/**
 * fixedAssetNewStep1 variable is defined in fixedAssetNewStep1.js file
 * fixedAssetNewStep2 variable is defined in fixedAssetNewStep2.js file
 * fixedAssetNewStep3 variable is defined in fixedAssetNewStep3.js file
 */
var fixedAssetObj = (function(){
	var init = function(){
		initWindow();
		initWizard();
		initEvent();
	};
	var initWizard = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.direction == "next") {
				if(info.step == 1){
 					var isValid = fixedAssetNewStep1.validate();
 					if(!isValid){
 						return false;
 					}
 				}else if(info.step == 2){
 					var isValid = fixedAssetNewStep2.validate();
 					if(!isValid){
 						return false;
 					}
 				}
			}else if(info.direction == "previous"){
				fixedAssetNewStep2.hideValidate();
 			}
		}).on('finished', function(e) {
			bootbox.dialog(uiLabelMap.CreateFixedAssetConfirm,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createFixedAsset();
						}
					},
					 {
						 "label" : uiLabelMap.CommonCancel,
						 "class" : "btn-danger btn-small icon-remove open-sans",
					 }
					 ]		
			);
		}).on('stepclick', function(e){
 			//return false;//prevent clicking on steps
 		});
	};
	var createFixedAsset = function(){
		Loading.show('loadingMacro');
		var commonData = fixedAssetNewStep1.getData();
		var depData = fixedAssetNewStep2.getData();
		var month = depData.usefulLives;
		var submitedData = $.extend({}, commonData, depData);
		var dateAcquired = $('#dateAcquired').jqxDateTimeInput('getDate');
		var expectedEndOfLife = new Date(new Date(dateAcquired).setMonth(dateAcquired.getMonth() + parseInt(month)));
		submitedData.expectedEndOfLife = accutils.getTimestamp(expectedEndOfLife);
		submitedData.listAccompanyComponents = JSON.stringify(fixedAssetNewStep3.getData());
		$.ajax({
			url: 'createFixedAssetAndDep',
			type: "POST",
			data: submitedData,
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
					  $("#addNewFixedAssetWindow").jqxWindow('close');
					  $("#jqxgridAsset").jqxGrid('updatebounddata');
					  Grid.renderMessage('jqxgridAsset', response.successMessage, {template : 'success', appendContainer : '#containerjqxgridAsset'});
				  }
			  },
			complete:  function(jqXHR, textStatus){
				Loading.hide('loadingMacro');	
			}
		});
	};
	var initWindow = function(){
		accutils.createJqxWindow($("#addNewFixedAssetWindow"), 900, 540);	
	};
	var initEvent = function(){
		$("#addNewFixedAssetWindow").on('open', function(){
			fixedAssetNewStep1.windownOpenInit();
			fixedAssetNewStep2.windownOpenInit();
		});
		$("#addNewFixedAssetWindow").on('close', function(){
			fixedAssetNewStep1.resetData();
			fixedAssetNewStep2.resetData();
			fixedAssetNewStep3.resetData();
			$('#fuelux-wizard').wizard('previous');
			$('#fuelux-wizard').wizard('previous');
		});
	};
	return{
		init: init
	}
}());
$(document).ready(function(){
	fixedAssetObj.init();
});