$(function(){
	SkillTypeObj.init();
	ParentSkillTypeObj.init();
});

var ParentSkillTypeObj = (function(){
	var init = function(){
		var validator;
		initJqxWindow();
		initJqxInput();	
		initJqxValidator();
		initEvents();
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#addParentTypeIdWindow"), 450, 250);
	}
	
	var initJqxInput = function(){
		$('#parentSkillTypeId').jqxInput({width: "96%", height: 20});
		$('#parentDescription').jqxInput({width: "96%", height: 60});
	};
	
	var initJqxValidator = function(){
		var extendRules = [
		                    { input: '#parentSkillTypeId', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'keyup, blur', rule: 'required',
		                    	rule: function (input) {	
		                    		var value = input.val();
		                    		if(value && !(/^[a-zA-Z0-9_-]+$/.test(value))){
		                    			return false;
		                    		}
		                    		return true;
		                    	}
		                    },
		                ];
		
		var mapRules = [
		     	     	{input: '#parentSkillTypeId', type: 'validInputNotNull'},
		     	    	{input: '#parentDescription', type: 'validInputNotNull'},
		             ];
		
		validator = new OlbValidator($('#addParentTypeIdWindow'), mapRules, extendRules, {position: 'right'});
	};
	
	var initEvents = function(){
		$('#buttonSave').click(function(){
			var resultValidate = validator.validate();
			if(!resultValidate) return false;
			var parentTypeId = $('#parentSkillTypeId').val();
			var parentDes = $ ('#parentDescription').val();
			var title = uiLabelMap.AreYouSureCreate;
			bootbox.dialog(title,
				[{"label": uiLabelMap.CommonCancel,
					"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
					"callback": function() {bootbox.hideAll();}
				},
				{"label": uiLabelMap.OK,
					"icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
					"callback": function() {
						createParentSkillType(parentTypeId, parentDes);
				}
			}]);
		});
		
		$('#buttonCancel').click(function(){
			$('#parentSkillTypeId').val('');
			$ ('#parentDescription').val('');
			validator.hide();
			$("#addParentTypeIdWindow").jqxWindow('close');
		});
		
		$('#addParentTypeIdWindow').on('close', function(){
			$('#parentSkillTypeId').val('');
			$ ('#parentDescription').val('');
			validator.hide();
		});
	} 
	
	var createParentSkillType = function(parentTypeId, parentDescription){
		Loading.show('loadingMacro');
		setTimeout(function(){
			$.ajax({
				url: "createNewSkillType",
				type: "POST",
				data: {skillTypeId: parentTypeId, description: parentDescription},
				dataType: "json",
				success: function(data) {
				}
			}).done(function(data) {
				if($.inArray({skillTypeId: parentTypeId, description: parentDescription}, parentSkillTypeData) !== -1){
					parentSkillTypeData.push({skillTypeId: parentTypeId, description: parentDescription});
				}
				$('#parentTypeId').jqxDropDownList({source: parentSkillTypeData});
				$('#parentSkillTypeId').val('');
				$ ('#parentDescription').val('');
				$('#addParentTypeIdWindow').jqxWindow('close');
				$('#container').empty();
                $('#jqxNotification').jqxNotification({ template: 'info'});
                $("#jqxNotification").html(uiLabelMap.NotifiUpdateSucess);
                $("#jqxNotification").jqxNotification("open");
                
			});
			Loading.hide('loadingMacro');	 
    	}, 200);
	}
	
	var openPopupParentSkillType =  function(){
		$('#addParentTypeIdWindow').jqxWindow('setTitle', uiLabelMap.AddNew);
		$('#addParentTypeIdWindow').jqxWindow('open');
	};
	
	return {
		init: init,
		openPopupParentSkillType : openPopupParentSkillType,
	}
	
}());

/*-----------------------------------------------------------Add Skill Type Has Parent--------------------------------------------------------------*/

var SkillTypeObj = (function(){
	var init = function(){
		var validatorTmp;
		initInputs();
		initElementComplex();
		initEvents();
		initValidateForm();
	};
	var initInputs = function(){
		$("#skillTypeId").jqxInput({width: "96%", height: 20});
		createJqxDropDownListExt($('#parentTypeId'), parentSkillTypeData, {placeHolder: uiLabelMap.PleaseSelectTitle, width: '97%', height: 25, displayMember: 'description', valueMember: 'skillTypeId'});
		$("#description").jqxInput({width: "96%", height: 60});
		createJqxWindow($("#alterpopupWindow"), 450, 250);
		$("#skillTypeMenu").jqxMenu({ width: 220, autoOpenPopup: false, mode: 'popup', theme: 'olbius'});
		jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
	};
	var initElementComplex = function(){
		
	};

	var initEvents = function(){
		$("#skillTypeMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgridSkillType").jqxGrid('getselectedrowindex');
			var dataRecord = $("#jqxgridSkillType").jqxGrid('getrowdata', rowindex);
			var skillTypeId = dataRecord.skillTypeId;
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
							deleteSkillType(skillTypeId);
					}
				}]);
			} else if (tmpStr == uiLabelMap.Edit){
				checkUpdate = true;
				$('#alterpopupWindow').jqxWindow('setTitle', uiLabelMap.LogEditReturnsReason + ': ' + skillTypeId);
				$("#alterpopupWindow").jqxWindow('open');
				$("#skillTypeId").val(skillTypeId);
				$("#description").val(description);
				openPopupSkillType(skillTypeId, description);
			} else if(tmpStr == uiLabelMap.BSRefresh){
				$('#jqxgridSkillType').jqxGrid('updatebounddata');
			}
		});
		
		$("#addButtonSave").click(function () {
			var resultValidate = validatorTmp.validate();
			if(!resultValidate) return false;
			var skillTypeId = $('#skillTypeId').val();
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
							updateSkillType(skillTypeId, parentTypeId, description);
							checkUpdate = false;
						} else {
							createSkillType(skillTypeId, parentTypeId, description);
							checkUpdate = false;
						}
				}
			}]);
		});
		
		$("#addButtonCancel").click(function () {
			validatorTmp.hide();
			$("#skillTypeId").val('');
			$('#description').val('');
			$("#alterpopupWindow").jqxWindow('close');
		});
		
		$("#addButtonParentTypeId").click(function () {
			ParentSkillTypeObj.openPopupParentSkillType();
		});
		
		$("#alterpopupWindow").on('close', function (){
			$("#skillTypeId").val('');
			$('#description').val('');
			validatorTmp.hide();
		});
	};
	
	var initValidateForm = function(){
		var extendRules1 = [
            { input: '#skillTypeId', message: uiLabelMap.ThisFieldMustNotByContainSpecialCharacter, action: 'keyup, blur', rule: 'required',
            	rule: function (input) {	
            		var value = $("#skillTypeId").val();
            		if(value && !(/^[a-zA-Z0-9_-]+$/.test(value))){
            			return false;
            		}
            		return true;
            	}
            },
        ];
		var mapRules1 = [
	     	{input: '#skillTypeId', type: 'validInputNotNull'},
	    	{input: '#parentTypeId', type: 'validObjectNotNull', objType: 'dropDownList'},
	     	{input: '#description', type: 'validInputNotNull'},
        ];
		validatorTmp = new OlbValidator($('#alterpopupWindow'), mapRules1, extendRules1, {position: 'right'});
	};
	
	function deleteSkillType(skillTypeId, description){
		Loading.show('loadingMacro');
		setTimeout(function(){
			$.ajax({
				url: "deleteSkillType",
				type: "POST",
				data: {skillTypeId: skillTypeId},
				dataType: "json",
				success: function(data) {
					$('#jqxgridSkillType').jqxGrid('updatebounddata');
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
	
	function createSkillType(skillTypeId, parentTypeId, description){
		Loading.show('loadingMacro');
		setTimeout(function(){
			$.ajax({
				url: "createNewSkillType",
				type: "POST",
				data: {skillTypeId: skillTypeId, parentTypeId: parentTypeId, description: description},
				dataType: "json",
				success: function(data) {
				}
			}).done(function(data) {
				var value = data["value"];
				$('#jqxgridSkillType').jqxGrid('updatebounddata');
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
	
	function updateSkillType(skillTypeId, parentTypeId, description){
		Loading.show('loadingMacro');
		setTimeout(function(){
			$.ajax({
				url: "updateSkillType",
				type: "POST",
				data: {skillTypeId: skillTypeId, parentTypeId: parentTypeId, description: description},
				dataType: "json",
				success: function(data) {
				}
			}).done(function(data) {
				var value = data["value"];
				$('#jqxgridSkillType').jqxGrid('updatebounddata');
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
	
	var openPopupSkillType = function openPopupSkillType(){
		$('#alterpopupWindow').jqxWindow('setTitle', uiLabelMap.AddNew);
		$("#alterpopupWindow").jqxWindow('open');
	}
	
	return {
		init: init,
		openPopupSkillType: openPopupSkillType,
	}
}());