$(function(){
	ReqDlvObj.init();
});
var ReqDlvObj = (function(){
	var init = function(){
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function(){
		glDeliveryId = null;
		$('#jqxFileScanUpload').jqxWindow({ width: 400, modalZIndex: 10000, height: 220, isModal: true, autoOpen: false });
		$("#popupDeliveryDetailWindow").jqxWindow({
		    maxWidth: 1500, minWidth: 950, width: 1300, modalZIndex: 10000, zIndex:10000, minHeight: 500, height: 500, maxHeight: 670, resizable: false, cancelButton: $("#alterCancel2"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
		});
		initAttachFile();
	};
	var initElementComplex = function(){
		
	};
	var initEvents = function(){
		$('#uploadCancelButton').click(function(){
			$('#jqxFileScanUpload').jqxWindow('close');
		});
		$('#jqxFileScanUpload').on('close', function(event){
			$('.remove').trigger('click');
			initAttachFile();
		});
		$('#uploadOkButton').click(function(){
			saveFileUpload();
		});
	};
	
	var initValidateForm = function (){
		
	};
	
	function saveFileUpload (){
		Loading.show('loadingMacro');
    	setTimeout(function(){
			var folder = "/baseLogistics/delivery";
			for ( var d in listImage) {
				var file = listImage[d];
				var dataResourceName = file.name;
				var path = "";
				var form_data= new FormData();
				form_data.append("uploadedFile", file);
				form_data.append("folder", folder);
				jQuery.ajax({
					url: "uploadImages",
					type: "POST",
					data: form_data,
					cache : false,
					contentType : false,
					processData : false,
					success: function(res) {
						path = res.path;
						pathScanFile = path;
						$('#linkId').html("");
						$('#linkId').attr('onclick', null); 
						$('#linkId').append("<a href='"+path+"' onclick='' target='_blank'><i class='fa-file-image-o'></i>"+uiLabelMap.Scan+"</a>");
						if (path != null && path != undefined){
							$.ajax({
				   				 type: "POST",
				   				 url: "updateDeliveryScanfile",
				   				 data: {
				   					 pathScanFile: pathScanFile,
				   					 deliveryId: glDeliveryId,
				   				 },
				   				 dataType: "json",
				   				 async: false,
				   				 success: function(data){
				   				 },
				   				 error: function(response){
				   				 }
				   		 	});
						}
			        }
				}).done(function() {
				});
			}
			$('#jqxFileScanUpload').jqxWindow('close');
			Loading.hide('loadingMacro');
    	}, 500);
	}
	
	var showDetailPopup = function showDetailPopup(deliveryId){
		var href = "printDeliveryRequirement.pdf?deliveryId=" + deliveryId;
		$('#printPDF').attr('href', href);
		var deliveryDT;
		$.ajax({
			type: "POST",
			url: "getDeliveryById",
			data: {'deliveryId': deliveryId},
			dataType: "json",
			async: false,
			success: function(response){
				deliveryDT = response;
			},
		});
		glDeliveryId = deliveryDT.deliveryId;
		$("#deliveryIdDT").text(deliveryDT.deliveryId);
		
		var stName = null;
        for(i=0; i < statusData.length; i++){
            if(statusData[i].statusId == deliveryDT.statusId){
                stName = statusData[i].description;
            }
        }
        if (stName){
        	$("#statusIdDT").text(stName);
        } else {
        	$("#statusIdDT").text("_NA_");
        }
        if (deliveryDT.requirementId){
        	$("#requirementIdDT").text(deliveryDT.requirementId);
        } else {
        	$("#requirementIdDT").text("_NA_");
        }
		
		// Create originFacilityIdDT
		$("#originFacilityIdDT").text(deliveryDT.originFacilityName);
		
		// Create destFacilityIdDT
		$("#deliveryTypeDT").text(deliveryDT.deliveryTypeDesc);
		
		// Create createDateDT
		$('#createDateDT').text(formatFullDate(new Date(deliveryDT.createDate)));
		
		// Create partyIdToDT
		var partyIdTo = deliveryDT.partyIdTo;
		$("#partyIdToDT").text("");
		
		// Create destContactMechIdDT
		$("#destContactMechIdDT").text(deliveryDT.destAddress);
		
		// Create originContactMechIdDT
		$("#originContactMechIdDT").text(deliveryDT.originAddress);
		
		// Create partyIdFromDT
		$("#partyIdFromDT").text("");
		
		// Create deliveryDateDT
		var deliveryDate = new Date(deliveryDT.deliveryDate);
		$('#deliveryDateDT').text(formatFullDate(new Date(deliveryDT.deliveryDate)));
		
		// Create noDT
		if (deliveryDT.no){
        	$("#noDT").text(deliveryDT.no);
        } else {
        	$("#noDT").text("_NA_");
        }
		
		var path = "";
		if (deliveryDT.pathScanFile){
			path = deliveryDT.pathScanFile;
			$('#scanfile').html("");
			$('#scanfile').append("<a href="+path+" target='_blank'><i class='fa-file-image-o'></i>"+uiLabelMap.Scan+"</a>");
		} else {
			if ("DLV_DELIVERED" == deliveryDT.statusId || "DLV_EXPORTED" == deliveryDT.statusId){
				$('#scanfile').html("");
				$('#scanfile').append("<a id='linkId' href='javascript:ReqDlvObj.showAttachFilePopup()' onclick=''><i class='fa-upload'></i> "+uiLabelMap.Scan+"</a>");
			} else {
				$('#scanfile').html("");
			}
		}
		
		$('#estimatedStartDateDT').html("");
		$('#estimatedStartDateDT').text(formatFullDate(new Date(deliveryDT.estimatedStartDate)));
		
		$('#estimatedArrivalDateDT').html("");
		$('#estimatedArrivalDateDT').text(formatFullDate(new Date(deliveryDT.estimatedArrivalDate)));
		
		if ("DLV_PROPOSED" == deliveryDT.statusId){
			if (perAdmin){
				$('#titleDetailId > div:first-child').html("");
				$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryNote + " - " + uiLabelMap.ApproveDelivery);
			} else {
				$('#titleDetailId > div:first-child').html("");
				$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryNote + " - " + uiLabelMap.WaitForApprove);
			}
		} else {
			$('#alterApproveAndContinue').hide();
		}
		if (deliveryDT.actualStartDate){
			$('#actualStartDateDis').text(formatFullDate(new Date(deliveryDT.actualStartDate)));
		} else {
			$('#actualStartDateDis').text('_NA_');
		}
		
		if (deliveryDT.actualArrivalDate){
			$('#actualArrivalDateDis').text(formatFullDate(new Date(deliveryDT.actualArrivalDate)));
		} else {
			$('#actualArrivalDateDis').text('_NA_');
		}
		
		if ("DLV_APPROVED" == deliveryDT.statusId){
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryNote + " - " + uiLabelMap.UpdateActualExportedQuantity);
		} 
		if ("DLV_EXPORTED" == deliveryDT.statusId){
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryNote + " - " + uiLabelMap.UpdateActualDeliveredQuantity);
			
		}
		if ("DLV_DELIVERED" == deliveryDT.statusId){
			$('#titleDetailId > div:first-child').html("");
			$('#titleDetailId > div:first-child').text(uiLabelMap.DeliveryNote+ " - " + uiLabelMap.DeliveryDoc);
		}
		
		var listDeliveryItems = [];
		$.ajax({
            type: "POST",
            url: "getDeliveryItemByDeliveryId",
            data: {'deliveryId': deliveryId},
            dataType: "json",
            async: false,
            success: function(response){
                listDeliveryItems = response['listDeliveryItems'];
            },
            error: function(response){
              alert("Error:" + response);
            }
		});
		listDeliveryItemData = listDeliveryItems;
		if ("DLV_EXPORTED" == deliveryDT.statusId){
			for (var m = 0; m < listDeliveryItems.length; m ++){
				listDeliveryItems[m]["actualDeliveredQuantity"] = listDeliveryItems[m].actualExportedQuantity;
			}
		}
		loadDeliveryItem(listDeliveryItems);
		// Open Window
		$("#popupDeliveryDetailWindow").jqxWindow('open');
	}
	
	var formatFullDate = function formatFullDate(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			var dateStr = "";
			dateStr += addZero(value.getFullYear()) + '-';
			dateStr += addZero(value.getMonth()+1) + '-';
			dateStr += addZero(value.getDate()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	}
	function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	}
	var showAttachFilePopup = function showAttachFilePopup(){
		$('#jqxFileScanUpload').jqxWindow('open');
	}
	function initAttachFile(){
		$('#attachFile').html('');
		listImage = [];
		$('#attachFile').ace_file_input({
			style:'well',
			btn_choose: uiLabelMap.DropFileOrClickToChoose,
			btn_change:null,
			no_icon:'icon-cloud-upload',
			droppable:true,
			onchange:null,
			thumbnail:'small',
			before_change:function(files, dropped) {
				listImage = [];
				var count = files.length;
				for (var int = 0; int < files.length; int++) {
					var imageName = files[int].name;
					var hashName = imageName.split(".");
					var extended = hashName.pop();
					if (imageName.length > 50){
						bootbox.dialog(uiLabelMap.NameOfImagesMustBeLessThan50Character, [{
			                "label" : uiLabelMap.OK,
			                "class" : "btn btn-primary standard-bootbox-bt",
			                "icon" : "fa fa-check",
			                }]
			            );
			            return false;
					} else {
						if (extended == "JPG" || extended == "jpg" || extended == "jpeg" || extended == "gif" || extended == "png") {
							listImage.push(files[int]);
						}
					} 
				}
				return true;
			},
			before_remove : function() {
				listImage = [];
				return true;
			}
		});
	}
	
	var getFormattedDate = function getFormattedDate(date) {
		  var year = date.getFullYear();
		  var month = (1 + date.getMonth()).toString();
		  month = month.length > 1 ? month : '0' + month;
		  var day = date.getDate().toString();
		  day = day.length > 1 ? day : '0' + day;
		  return day + '/' + month + '/' + year;
	}
	
	return {
		init: init,
		formatFullDate: formatFullDate,
		getFormattedDate: getFormattedDate,
		showDetailPopup: showDetailPopup,
		showAttachFilePopup: showAttachFilePopup,
	};
}());