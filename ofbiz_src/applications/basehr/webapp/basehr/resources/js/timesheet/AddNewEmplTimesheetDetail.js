var initWizard = (function(){
	var init = function(){
		$('#wizardAddNew').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				if(!generalInfoObj.validate()){
					return false;
				}
			}else if(info.step == 2) {
				if(info.direction == "next"){
					if(!addEmplTimesheetDetailObj.validate()){
						return false;
					}
					setTimeout(function(){ 
						addEmplTimesheetDetailObj.prepareSourceFileData(); 
					}, 5);
				}else if(info.direction == "previous"){
					addEmplTimesheetDetailObj.hideValidate();
				}
	        }else if(info.step == 3 && (info.direction == "next")){
	        	setTimeout(function(){ 
	        		addEmplTimesheetDetailObj.setFromDate($("#fromDate").jqxDateTimeInput('val', 'date'));
	        		addEmplTimesheetDetailObj.setThruDate($("#thruDate").jqxDateTimeInput('val', 'date'));
	        		addEmplTimesheetDetailObj.prepareJoinColumnData(); 
	        	}, 5);
	        }
	    }).on('finished', function(e) {
	    	bootbox.dialog(uiLabelMap.ConfirmCreateEmplTimesheetDetail,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createEmplTimesheetDetail();
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
		create_spinner($("#spinnerCreateNew"));
	};
	var createEmplTimesheetDetail = function(){
		var generalData = generalInfoObj.getData();
		var fileData = addEmplTimesheetDetailObj.getData();
		var dataSubmit = $.extend({}, generalData, fileData);
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
		$("#loadingCreateNew").show();
		$.ajax({
			url: 'createTimekeepingDetail',
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
    				$("#AddEmplTimesheetDetailWindow").jqxWindow('close');
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
				$("#loadingCreateNew").hide();
				$("#btnPrev").removeAttr("disabled");
				$("#btnNext").removeAttr("disabled");
			}
		});
	};
	var resetStep = function(){
		$('#wizardAddNew').wizard('previous');
		$('#wizardAddNew').wizard('previous');
		$('#wizardAddNew').wizard('previous');
	};
	var initJqxWindow = function(){
		createJqxWindow($("#AddEmplTimesheetDetailWindow"), 700, 485);
		$("#AddEmplTimesheetDetailWindow").on('open', function(event){
			addEmplTimesheetDetailObj.onWindowOpen();
			generalInfoObj.onWindowOpen();
		});
		$("#AddEmplTimesheetDetailWindow").on('close', function(event){
			generalInfoObj.resetData();
			addEmplTimesheetDetailObj.resetData();
			resetStep();
			generalInfoObj.hideValidate();
			addEmplTimesheetDetailObj.hideValidate();
		});
	};
	return{
		init: init
	}
}());

var downloadFileTemplateObj = (function(){
	var init = function(){
		initEvent();
	};
	var initEvent = function(){
		$("#downloadFileTemplate").click(function(event){
			var fromDate = $("#fromDate").jqxDateTimeInput('val', 'date');
			var thruDate = $("#thruDate").jqxDateTimeInput('val', 'date');
			var partySelectedItem = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
			var partyId = partySelectedItem.value;
			var month = $("#monthTS").val();
			var year = $("#yearTS").val();
			window.open('downloadTimesheetTemplate?partyId=' + partyId + "&fromDate=" + fromDate.getTime() + "&thruDate=" + thruDate.getTime() + "&month=" + month + "&year=" + year, '_blank');
		});
	};
	return{
		init: init
	}
}());

$(document).ready(function(){
	addEmplTimesheetDetailObj.init();//addEmplTimesheetDetailObj is defined in AddNewEmplTimesheetDetailExcelFile.js
	generalInfoObj.init();//generalInfoObj is defined in AddNewEmplTimesheetDetailGeneralInfo.js
	downloadFileTemplateObj.init();
	initWizard.init();
});