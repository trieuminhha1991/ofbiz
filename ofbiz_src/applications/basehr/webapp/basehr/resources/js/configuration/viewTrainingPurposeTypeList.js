$(function(){
	TrainingPurposeTypeObj.init();
});
var TrainingPurposeTypeObj = (function(){
	var init = function(){
		var validatorTmp;
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function(){
		$("#trainingPurposeTypeId").jqxInput({width: 295, height: 20});
		
		$('#parentTypeId').jqxDropDownList({placeHolder: uiLabelMap.PleaseSelectTitle, width: 300, source:trainingPurposeTypeData , theme: theme, displayMember: 'description', valueMember: 'trainingPurposeTypeId',});

		$("#description").jqxInput({ width: 300, height: 60});
		
		$("#alterpopupWindow").jqxWindow({
			width: 500, height: 250, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme: theme
		});
		$("#trainingPurposeTypeMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
		jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
	};
	var initElementComplex = function(){
		
	};

	var initEvents = function(){
		$("#trainingPurposeTypeMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgridTrainingPurposeType").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgridTrainingPurposeType").jqxGrid('getrowdata', rowindex);
			var trainingPurposeTypeId = dataRecord.trainingPurposeTypeId;
			var description = dataRecord.description;
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
							deleteTrainingPurposeType(trainingPurposeTypeId);
					}
				}]);
			} else if (tmpStr == uiLabelMap.Edit){
				checkUpdate = true;
				$("#trainingPurposeTypeId").val(trainingPurposeTypeId);
				$("#description").val(description);
				openPopupTrainingPurposeType(uiLabelMap.Edit);
			} else if(tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridTrainingPurposeType').jqxGrid('updatebounddata');
			}
		});
		
		$("#addButtonSave").click(function () {
			var resultValidate = validatorTmp.validate();
			if(!resultValidate) return false;
			var trainingPurposeTypeId = $('#trainingPurposeTypeId').val();
			var parentTypeId = $('#parentTypeId').jqxDropDownList('val');
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
						if(checkUpdate == true){
							updateTrainingPurposeType(trainingPurposeTypeId, parentTypeId, description);
							checkUpdate = false;
						} else {
							createTrainingPurposeType(trainingPurposeTypeId, parentTypeId, description);
							checkUpdate = false;
						}
				}
			}]);
		});
		
		$("#alterpopupWindow").on('close', function (){
			validatorTmp.hide();
		});
	};
	
	var initValidateForm = function(){
		var extendRules1 = [
            { input: '#trainingPurposeTypeId', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'keyup, blur', rule: 'required',
            	rule: function (input) {	
            		var value = $("#trainingPurposeTypeId").val();
            		if(value && !(/^[a-zA-Z0-9_-]+$/.test(value))){
            			return false;
            		}
            		return true;
            	}
            },
        ];
		var mapRules1 = [
	     	{input: '#trainingPurposeTypeId', type: 'validInputNotNull'},
	     	{input: '#description', type: 'validInputNotNull'},
        ];
		validatorTmp = new OlbValidator($('#alterpopupWindow'), mapRules1, extendRules1, {position: 'right'});
	};
	
	function deleteTrainingPurposeType(trainingPurposeTypeId, description){
		Loading.show('loadingMacro');
		setTimeout(function(){
			$.ajax({
				url: "deleteTrainingPurposeType",
				type: "POST",
				data: {trainingPurposeTypeId: trainingPurposeTypeId},
				dataType: "json",
				success: function(data) {
					$('#jqxgridTrainingPurposeType').jqxGrid('updatebounddata');
				}
			}).done(function(res) {
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
	
	function createTrainingPurposeType(trainingPurposeTypeId, parentTypeId, description){
		Loading.show('loadingMacro');
		setTimeout(function(){
			$.ajax({
				url: "createTrainingPurposeType",
				type: "POST",
				data: {trainingPurposeTypeId: trainingPurposeTypeId, parentTypeId: parentTypeId, description: description},
				dataType: "json",
				success: function(data) {
					trainingPurposeTypeData.push({'trainingPurposeTypeId': trainingPurposeTypeId, 'parentTypeId': parentTypeId, 'description': description});
				}
			}).done(function(data) {
				var value = data["value"];
				$('#jqxgridTrainingPurposeType').jqxGrid('updatebounddata');
				$('#alterpopupWindow').jqxWindow('close');
				checkUpdate = true;
				$('#container').empty();
                $('#jqxNotification').jqxNotification({ template: 'info'});
                $("#jqxNotification").html(uiLabelMap.NotifiUpdateSucess);
                $("#jqxNotification").jqxNotification("open");
                
			});
			Loading.hide('loadingMacro');	 
    	}, 200);
	}
	
	function updateTrainingPurposeType(trainingPurposeTypeId, parentTypeId, description){
		Loading.show('loadingMacro');
		setTimeout(function(){
			$.ajax({
				url: "updateTrainingPurposeType",
				type: "POST",
				data: {trainingPurposeTypeId: trainingPurposeTypeId, parentTypeId: parentTypeId, description: description},
				dataType: "json",
				success: function(data) {
				}
			}).done(function(data) {
				var value = data["value"];
				$('#jqxgridTrainingPurposeType').jqxGrid('updatebounddata');
				$('#alterpopupWindow').jqxWindow('close');
				checkUpdate = true;
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
	
	var openPopupTrainingPurposeType = function openPopupTrainingPurposeType(title){
		$('#alterpopupWindow').jqxWindow('setTitle', title);
		if(!title){
			$('#alterpopupWindow').jqxWindow('setTitle', uiLabelMap.AddTrainingPurposeType);
		}
		$("#alterpopupWindow").jqxWindow('open');
		$("#trainingPurposeTypeId").jqxInput('val', '');
		$("#description").jqxInput('val', '');
		$('#parentTypeId').jqxDropDownList({source:trainingPurposeTypeData});

	}
	
	return {
		openPopupTrainingPurposeType: openPopupTrainingPurposeType,
		init: init,
		
	}
}());