$(function(){
	TransferDetailObj.init();
});
var TransferDetailObj = (function() {
	var init = function() {
		if (noteValidate === undefined) var noteValidate;
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function() {
		if (noteData.length <= 0) {
			$("#noteContent").hide();
		} else {
			$("#noteContent").show();
			$('#listNote').html("");
			for (var i = 0; i < noteData.length; i ++){
				$('#listNote').append("["+DatetimeUtilObj.formatFullDate(noteData[i].noteDateTime) +"] - " + noteData[i].noteInfo + "</br>");
			}
		}
		$("#note").jqxInput({ width: 300, height: 100});
		$("#noteTransfer").jqxWindow({
			maxWidth: 800, minWidth: 300, width: 500, height: 230, minHeight: 100, maxHeight: 800, resizable: false, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#noteCancel"), modalOpacity: 0.7, theme:theme           
		});
		
		if (transferDate != null && (shipBeforeDate == null || shipAfterDate == null)){
			$("#transferDateDT").text(DatetimeUtilObj.formatFullDate(new Date(transferDate)));
		} else if (transferDate != null && shipBeforeDate != null && shipAfterDate != null){
			$("#transferDateDT").text(DatetimeUtilObj.formatFullDate(new Date(transferDate)) + " (" + DatetimeUtilObj.formatFullDate(new Date(shipAfterDate)) - DatetimeUtilObj.formatFullDate(new Date(shipBeforeDate)) + ")");
		} else if (transferDate == null && (shipBeforeDate != null && shipAfterDate != null)){
			$("#transferDateDT").text(DatetimeUtilObj.formatFullDate(new Date(shipAfterDate)) + ' - ' + DatetimeUtilObj.formatFullDate(new Date(shipBeforeDate)));
		}
		if (statusDatetime != null && statusDatetime != undefined && statusDatetime != ''){
			$("#completedDateDT").text(DatetimeUtilObj.formatFullDate(new Date(statusDatetime)));
		} else {
			$("#completedDateDT").text('');
		}
		
		if (shipBeforeDateDt){
			$("#shipBeforeDateDT").text(DatetimeUtilObj.formatToMinutes(new Date(shipBeforeDateDt)));
		}
		if (shipAfterDateDt){
			$("#shipAfterDateDT").text(DatetimeUtilObj.formatToMinutes(new Date(shipAfterDateDt)));
		}
		
	};
	var initElementComplex = function() {
	};
	var initEvents = function() {
		$('#noteSave').click(function(){
			var resultValidate = !noteValidate.validate();
			if(resultValidate) return false;
			var note = $("#note").val().split('\n').join(' ');
			rejectTransfer(transferId, note);
		});
	};
	
	var rejectTransfer = function (transferId, note){
		bootbox.dialog(uiLabelMap.AreYouSureReject, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": uiLabelMap.OK,
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	Loading.show('loadingMacro');
            	setTimeout(function(){
					$.ajax({
						url: "changeTransferStatus",
			    		type: "POST",
			    		async: false,
			    		data: {
			    			transferId: transferId,
			    			statusId: "TRANSFER_REJECTED",
			    			setItemStatus: "Y",
			    			noteInfo: note,
			    			newItemStatus: "TRANSFER_REJECTED",
			    		},
			    		success: function (res){
			    			location.reload();
			    		}
					});
					Loading.hide('loadingMacro');
            	}, 500);
            }
		}]);
	};
	
	var initValidateForm = function(){
		var mapNoteRules = [
            {input: '#note', type: 'validInputNotNull'},
		];
		noteValidate = new OlbValidator($('#noteTransfer'), mapNoteRules, null, {position: 'right'});
	};
	return {
		init: init,
	}
}());