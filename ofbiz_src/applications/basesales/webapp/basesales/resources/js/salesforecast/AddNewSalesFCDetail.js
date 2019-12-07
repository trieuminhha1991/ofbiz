if (typeof (OlbWizardUploadSalesFC) == "undefined") {
	var OlbWizardUploadSalesFC = (function(){
		var init = function(){
			$('#wizardAddNew').ace_wizard().on('change' , function(e, info){
				if(info.step == 1 && (info.direction == "next")) {
					if(!addSalesFCSheetDetailObj.validate()){
						return false;
					}
					setTimeout(function(){
						addSalesFCSheetDetailObj.prepareSourceFileData();
					}, 5);
		        }else if(info.step == 2 && (info.direction == "next")){
		        	setTimeout(function(){
		        		addSalesFCSheetDetailObj.prepareJoinColumnData();
		        	}, 5);
		        }
		    }).on('finished', function(e) {
		    	bootbox.dialog(uiLabelMap.ConfirmCreateFileSalesFCSheetDetail,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								createSalesForecastDetail();
							}	
						},
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
		    }).on('stepclick', function(e, info){

		    });
			initJqxWindow();
			create_spinner($("#spinnerCreateNew"));
		};
		var createSalesForecastDetail = function(){
            var tabActiveCurrent = $("#tabActiveDefault").val();
            var jqxGridId = "jqxSalesForecast" + tabActiveCurrent;
            var dataRow = $("#" + jqxGridId).jqxGrid("getboundrows");
            var dataList = [];
            if (typeof(dataRow) != 'undefined') {
                dataRow.forEach(function (dataItem) {
                    if (dataItem != window) {
                        var itemMap = dataItem;
                        itemMap.internalName = "";
                        itemMap.features = "";
                        dataList.push(itemMap);
                    }
                });
            }

			var fileData = addSalesFCSheetDetailObj.getData();
			//var dataSubmit = $.extend({}, generalData, fileData);
			var dataSubmit = $.extend({}, fileData);
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
            var salesForecastId = $("#salesForecastId_" + tabActiveCurrent).val();
            var parentSalesForecastId = $("#parentSalesForecastId_" + tabActiveCurrent).val();
            var customTimePeriodId = $("#customTimePeriodId_" + tabActiveCurrent).val();
            var internalPartyId = $("#internalPartyId_" + tabActiveCurrent).val();
            var organizationPartyId = $("#organizationPartyId_" + tabActiveCurrent).val();
            var currencyUomId = $("#currencyUomId_" + tabActiveCurrent).val();
			
			for(var key in dataSubmit){
				formData.append(key, dataSubmit[key]);
			}
            formData.append("salesForecastId", salesForecastId);
            formData.append("parentSalesForecastId", parentSalesForecastId);
            formData.append("customTimePeriodId", customTimePeriodId);
            formData.append("internalPartyId", internalPartyId);
            formData.append("organizationPartyId", organizationPartyId);
            formData.append("currencyUomId", currencyUomId);
            formData.append("customTimePeriodList", JSON.stringify(listPeriod));
			$("#btnPrev").attr("disabled", "disabled");
			$("#btnNext").attr("disabled", "disabled");
			$("#loadingCreateNew").show();
			$.ajax({
				url: 'createSaleForecastDetail',
				data: formData,
				type: 'POST',
				cache : false,
				contentType : false,
				processData : false,
				success: function(response){
				    //test
					if(response.responseMessage == "success"){
                        $('#jqxNotificationSf').jqxNotification({ template: 'info'});
                        $("#jqxNotificationSf").html(uiLabelMap.wgupdatesuccess);
                        $("#jqxNotificationSf").jqxNotification("open");
                        $("#" + jqxGridId).jqxGrid("updatebounddata");
	    				$("#UploadFileSalesFCEdited").jqxWindow('close');
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
		};
		var initJqxWindow = function(){
			$("#UploadFileSalesFCEdited").on('close', function(event){
                resetStep();
				addSalesFCSheetDetailObj.resetData();
				addSalesFCSheetDetailObj.hideValidate();
			});
		};
		return{
			init: init
		}
	}());
}

$(document).ready(function(){
    OlbWizardUploadSalesFC.init();
    addSalesFCSheetDetailObj.init();//addSalesFCSheetDetailObj is defined in AddNewSalesFCDetailExcelFile.js
});