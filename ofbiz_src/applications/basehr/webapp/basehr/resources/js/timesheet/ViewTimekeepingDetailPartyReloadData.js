var timesheetDetailWizard = (function(){
	var _timekeepingDetailId = globalVar.timekeepingDetailId;
	var init = function(){
		$('#wizardReloadData').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && info.direction == "next") {
				if(!addEmplTimesheetDetailObj.validate()){
					return false;
				}
				setTimeout(function(){ 
					addEmplTimesheetDetailObj.prepareSourceFileData(); 
				}, 5);
	        }else if(info.step == 2 && (info.direction == "next")){
	        	setTimeout(function(){ 
	        		addEmplTimesheetDetailObj.setFromDate(globalVar.fromDate);
	        		addEmplTimesheetDetailObj.setThruDate(globalVar.thruDate);
	        		addEmplTimesheetDetailObj.prepareJoinColumnData(); 
	        	}, 5);
	        }
	    }).on('finished', function(e) {
	    	bootbox.dialog(uiLabelMap.ConfirmReloadData,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							updateTimesheetDetailParty();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
	    }).on('stepclick', function(e){
	    	
	    });
		initJqxWindow();
		create_spinner($("#spinnerReloadData"));
	};
	var updateTimesheetDetailParty = function(){
		var dataSubmit = addEmplTimesheetDetailObj.getData();
		dataSubmit.timekeepingDetailId = _timekeepingDetailId;
		var formData = new FormData();
		$("#upLoadFileForm").find('input[type=file]').each(function(){
			 var field_name = $(this).attr('name');
			 //for fields with "multiple" file support
			 //field name should be something like `myfile[]`
			 var files = $(this).data('ace_input_files');
			 if(files && files.length > 0) {
			     for(var f = 0; f < files.length; f++) {
			    	formData.append(field_name, files[f]);
			    }
			 }
		});
		for(var key in dataSubmit){
			formData.append(key, dataSubmit[key]);
		}
		$("#btnPrev").attr("disabled", "disabled");
		$("#btnNext").attr("disabled", "disabled");
		$("#loadingReloadData").show();
		$.ajax({
			url: 'updateTimekeepingDetailPartyList',
			data: formData,
			type: 'POST',
			cache : false,
			contentType : false,
			processData : false,
			success: function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
    				$("#timekeepingDetailReloadDataWindow").jqxWindow('close');
				}else{
					bootbox.dialog(response.errorMessage,
							[
							{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#loadingReloadData").hide();
				$("#btnPrev").removeAttr("disabled");
				$("#btnNext").removeAttr("disabled");
			}
		});
	};
	var resetStep = function(){
		$('#wizardReloadData').wizard('previous');
		$('#wizardReloadData').wizard('previous');
	};
	var initJqxWindow = function(){
		createJqxWindow($("#timekeepingDetailReloadDataWindow"), 700, 475);
		$("#timekeepingDetailReloadDataWindow").on('open', function(event){
			addEmplTimesheetDetailObj.onWindowOpen();
		});
		$("#timekeepingDetailReloadDataWindow").on('close', function(event){
			resetStep();
			addEmplTimesheetDetailObj.resetData();
			addEmplTimesheetDetailObj.hideValidate();
		});
	};
	var openWindow = function(){
		openJqxWindow($("#timekeepingDetailReloadDataWindow"));
	};
	return{
		init: init,
		openWindow: openWindow,
	}
}());

$(document).ready(function(){
	addEmplTimesheetDetailObj.init();//addEmplTimesheetDetailObj is defined in AddNewEmplTimesheetDetailExcelFile.js
	timesheetDetailWizard.init();
});