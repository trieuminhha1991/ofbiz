$(function(){
	VarReasonObj.init();
});
var VarReasonObj = (function(){
	var init = function(){
		var validatorTmp;
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function(){
		$("#varianceReasonId").jqxInput({width: 295, height: 20, disabled: false});
		$("#description").jqxInput({ width: 300, height: 60});
		$("#alterpopupWindow").jqxWindow({
			width: 500, height: 250, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme: theme
		});
		$('#negativeNumber').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, source: yesNoData,selectedIndex: 0, theme: theme, displayMember: 'description', valueMember: 'typeId',});
		$("#varReasonMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
		jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
	};
	var initElementComplex = function(){
		
	};

	var initEvents = function(){
		$("#varReasonMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgridVarianceReason").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgridVarianceReason").jqxGrid('getrowdata', rowindex);
			var varianceReasonId = dataRecord.varianceReasonId;
			var description = dataRecord.description;
			var negativeNumber = dataRecord.negativeNumber;
			var tmpStr = $.trim($(args).text());
			if (tmpStr == uiLabelMap.CommonDelete) {
				bootbox.dialog(uiLabelMap.AreYouSureDelete,
					[{"label": uiLabelMap.CommonCancel,
						"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
						"callback": function() {bootbox.hideAll();}
					}, 
					{"label": uiLabelMap.OK,
						"icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
						"callback": function() {
							deleteVarianceReason(varianceReasonId);
					}
				}]);
			} else if (tmpStr == uiLabelMap.Edit){
				checkUpdate = true;
				$('#alterpopupWindow').jqxWindow('setTitle', uiLabelMap.LogEditReturnsReason + ': ' + varianceReasonId);
				$("#alterpopupWindow").jqxWindow('open');
				$("#varianceReasonId").val(varianceReasonId);
				$("#varianceReasonId").jqxInput({disabled: true});
				$("#description").val(description);
				$("#negativeNumber").jqxDropDownList('val', negativeNumber);
				openPopupVarianceReason(varianceReasonId, description);
			} else if(tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridVarianceReason').jqxGrid('updatebounddata');
			}
		});
		
		$("#addButtonSave").click(function () {
			var resultValidate = validatorTmp.validate();
			if(!resultValidate) return false;
			var varianceReasonId = $('#varianceReasonId').val();
			var description = $("#description").jqxInput('val').split('\n').join(' ');
			var title = uiLabelMap.AreYouSureCreate;
			if(checkUpdate == true){
				title = uiLabelMap.AreYouSureUpdate;
			}
			bootbox.dialog(title,
				[{"label": uiLabelMap.CommonCancel,
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					"callback": function() {bootbox.hideAll();}
				},
				{"label": uiLabelMap.OK,
					"icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					"callback": function() {
						updateVarianceReason(varianceReasonId, description);
				}
			}]);
			checkUpdate = false;
		});
		
		$("#alterpopupWindow").on('close', function (){
			validatorTmp.hide();
			$("#varianceReasonId").jqxInput({disabled: false});
			$("#varianceReasonId").jqxInput('clear');
			$("#description").jqxInput('clear');
			checkUpdate = false;
		});
	};
	
	var initValidateForm = function(){
		var extendRules1 = [
            { input: '#varianceReasonId', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'keyup, blur', rule: 'required',
            	rule: function (input) {	
            		var value = $("#varianceReasonId").val();
            		if(value && !(/^[a-zA-Z0-9_-]+$/.test(value))){
            			return false;
            		}
            		return true;
            	}
            },
        ];
		var mapRules1 = [
	     	{input: '#varianceReasonId', type: 'validInputNotNull'},
	     	{input: '#description', type: 'validInputNotNull'},
	     	{input: '#negativeNumber', type: 'validObjectNotNull', objType: 'dropDownList' },
        ];
		validatorTmp = new OlbValidator($('#alterpopupWindow'), mapRules1, extendRules1, {position: 'right'});
	};
	
	function deleteVarianceReason(varianceReasonId, description){
		Loading.show('loadingMacro');
		setTimeout(function(){
			$.ajax({
				url: "deleteVarianceReason",
				type: "POST",
				data: {varianceReasonId: varianceReasonId},
				dataType: "json",
				success: function(data) {
					$('#jqxgridVarianceReason').jqxGrid('updatebounddata');
				}
			}).done(function(res) {
				var errorMess = "OLBIUS_VARIANCE_CONSTRAIN";
				if(res["_ERROR_MESSAGE_"] || res["_ERROR_MESSAGE_LIST_"]){
					jOlbUtil.alert.error(uiLabelMap.CheckLinkedData);
					return false;
				} else {
					$('#container').empty();
					$('#jqxNotification').jqxNotification({ template: 'info'});
					$("#jqxNotification").html(uiLabelMap.NotifiDeleteSucess);
					$("#jqxNotification").jqxNotification("open");
				}
				checkUpdate = true;
			});
			Loading.hide('loadingMacro');	 
    	}, 200);
	}
	
	function updateVarianceReason(varianceReasonId, description){
		Loading.show('loadingMacro');
		setTimeout(function(){
			$.ajax({
				url: "updateVarianceReason",
				type: "POST",
				data: {varianceReasonId: varianceReasonId, description: description, isAutoTransaction: 'N', isSum: 'Y', negativeNumber: $('#negativeNumber').jqxDropDownList('val')},
				dataType: "json",
				success: function(data) {
				}
			}).done(function(data) {
				var value = data["value"];
				$('#jqxgridVarianceReason').jqxGrid('updatebounddata');
				$('#alterpopupWindow').jqxWindow('close');
				checkUpdate = false;
				$('#container').empty();
                $('#jqxNotification').jqxNotification({ template: 'info'});
                $("#jqxNotification").html(uiLabelMap.NotifiUpdateSucess);
                $("#jqxNotification").jqxNotification("open");
                
			});
			Loading.hide('loadingMacro');	 
    	}, 200);
	}
	
	var getLocalization = function () {
	    var localizationobj = {};
	    localizationobj.pagergotopagestring = uiLabelMap.wgpagergotopagestring + ":";
	    localizationobj.pagershowrowsstring = uiLabelMap.wgpagershowrowsstring + ":";
	    localizationobj.pagerrangestring = uiLabelMap.wgpagerrangestring;
	    localizationobj.pagernextbuttonstring = uiLabelMap.wgpagernextbuttonstring;
	    localizationobj.pagerpreviousbuttonstring = uiLabelMap.wgpagerpreviousbuttonstring;
	    localizationobj.sortascendingstring = uiLabelMap.wgsortascendingstring;
	    localizationobj.sortdescendingstring = uiLabelMap.wgsortdescendingstring;
	    localizationobj.sortremovestring = uiLabelMap.wgsortremovestring;
	    localizationobj.emptydatastring = uiLabelMap.wgemptydatastring;
	    localizationobj.filterselectstring = uiLabelMap.wgfilterselectstring;
	    localizationobj.filterselectallstring = uiLabelMap.wgfilterselectallstring;
	    localizationobj.filterchoosestring = uiLabelMap.filterchoosestring;
	    localizationobj.groupsheaderstring = uiLabelMap.wgdragDropToGroupColumn;
	    localizationobj.todaystring = uiLabelMap.wgtodaystring;
	    localizationobj.clearstring = uiLabelMap.wgclearstring;
	    return localizationobj;
	};
	
	var openPopupVarianceReason = function openPopupVarianceReason(){
		$('#alterpopupWindow').jqxWindow('setTitle', uiLabelMap.AddNew);
		$("#alterpopupWindow").jqxWindow('open');
	}
	
	return {
		init: init,
		openPopupVarianceReason: openPopupVarianceReason,
	}
}());