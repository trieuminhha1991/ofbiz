var recruitReqApprObj = (function(){
	var _recruitmentRequireId = "";
	var _data = {};
	var _hasPermission = false;
	var init = function(){
		initJqxInput();
		initJqxPanel();
		initJqxCheckBox();
		initJqxValidator();
		initJqxGrid();
		initJqxWindow();
		initEvent();
		create_spinner($("#spinnerAjaxApprove"));
	};
	
	var initJqxInput = function(){
		$("#partyIdAppr").jqxInput({width: '96%', height: 20, displayMember: 'partyName', valueMember: 'partyId', disabled: true});
		$("#monthYearAppr").jqxInput({width: '95%', height: 20, disabled: true});
		$("#emplPositionTypeIdAppr").jqxInput({width: '96%', height: 20, displayMember: 'description', valueMember: 'emplPositionTypeId', disabled: true});
		$("#recruitmentFormTypeAppr").jqxInput({width: '95%', height: 20, displayMember: 'description', valueMember: 'recruitmentFormTypeId', disabled: true});
		$("#partyIdCreated").jqxInput({width: '95%', height: 20, displayMember: 'partyName', valueMember: 'partyId', disabled: true});
		$("#currStatusId").jqxInput({width: '98%', height: 20, disabled: true, displayMember: 'description', valueMember: 'statusId'});
	};
	var initJqxNumberInput = function(){
		$("#quantityAppr").jqxNumberInput({width: '98%', height: 25, disabled: true, inputMode: 'simple', decimalDigits: 0});
		$("#quantityUnplannedAppr").jqxNumberInput({width: '97%', height: 25, disabled: true, inputMode:'simple', decimalDigits: 0});
	};
	var initJqxCheckBox = function(){
		$("#plannedRadioBtn").jqxCheckBox({ width: "50px", height: 22, disabled: true});
		$("#unplannedRadioBtn").jqxCheckBox({ width: '70px', height: 22, disabled: true});
	};
	
	var initEvent = function(){
		$("#alterCancelAppr").click(function(event){
			$("#recruitRequireApprWindow").jqxWindow('close');
		});
		
		$("#acceptApproval").click(function(event){
			if(!validate()){
				return;
			}
			approveRecritmentReq("accept", uiLabelMap.AcceptRecruitmentRequireConfirm);
		});
		$("#rejectApproval").click(function(event){
			if(!validate()){
				return;
			}
			approveRecritmentReq("reject", uiLabelMap.RejectRecruitmentRequireConfirm);
		});
		$("#cancelApproval").click(function(event){
			if(!validate()){
				return;
			}
			approveRecritmentReq("cancel", uiLabelMap.CancelRecruitmentRequireConfirm);
		});
	};
	
	var approveRecritmentReq = function(statusCode, message){
		bootbox.dialog(message,
				[{
					"label" : uiLabelMap.CommonSubmit,
					"class" : "btn-primary btn-small icon-ok open-sans",
					"callback": function() {
						executeApproveRecritmentReq(statusCode);
					}	
				},
				{
					"label" : uiLabelMap.CommonClose,
					"class" : "btn-danger btn-small icon-remove open-sans",
				}]
		);
	};
	
	var executeApproveRecritmentReq = function(statusCode){
		$("#ajaxLoadingApprove").show();
		disableBtn();
		var dataSubmit = {};
		dataSubmit.recruitmentRequireId = _recruitmentRequireId;
		
		if($("#commentApproval").jqxEditor('val')){
			dataSubmit.changeReason = $("#commentApproval").jqxEditor('val'); 
		}
		dataSubmit.statusCode = statusCode;
		$.ajax({
			url: 'ApproveRecruitmentRequire',
			data: dataSubmit,
			type: 'POST',
			success: function(response){
				if(response._EVENT_MESSAGE_){
					$("#recruitRequireApprWindow").jqxWindow('close');
					Grid.renderMessage('jqxgrid', response._EVENT_MESSAGE_, {autoClose : true,
						template : 'info', appendContainer: "#containerjqxgrid", opacity : 0.9});
					$("#jqxgrid").jqxGrid('updatebounddata');
				}else{
					var mess = uiLabelMap.HasErrorWhenProcess;
					if (response._ERROR_MESSAGE_ != undefined && response._ERROR_MESSAGE_ != null && response._ERROR_MESSAGE_ != ''){
						mess = response._ERROR_MESSAGE_;
					}
					bootbox.dialog(mess,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]
					);
				}
			},
			complete: function(jqXHR, textStatus){
				$("#ajaxLoadingApprove").hide();
				enableBtn();
			}
		});
	}
	
	var disableBtn = function(){
		$("#acceptApproval").attr("disabled", "disabled");
		$("#alterCancelAppr").attr("disabled", "disabled");
		$("#cancelApproval").attr("disabled", "disabled");
		$("#rejectApproval").attr("disabled", "disabled");
		$("#commentApproval").jqxEditor({disabled: true});
	};
	
	var enableBtn = function(){
		$("#cancelApproval").removeAttr("disabled");
		$("#alterCancelAppr").removeAttr("disabled");
		$("#rejectApproval").removeAttr("disabled");
		$("#acceptApproval").removeAttr("disabled");
		$("#commentApproval").jqxEditor({disabled: false});
	};
	
	var initJqxGrid = function(){
		var datafield = [
		                 {name: 'recruitmentReqCondTypeId', type: 'string'},
		                 {name: 'recruitmentReqCondTypeName', type: 'string'},
		                 {name: 'conditionDesc', type: 'string'},
		                 ];
		var columns = [
		               {datafield : 'recruitmentReqCondTypeId', hidden: true},
		               {text: uiLabelMap.RecruitmentCriteria, datafield: 'recruitmentReqCondTypeName', width: '40%', editable: false},
		               {text: uiLabelMap.HRCondition, datafield: 'conditionDesc', width: '60%', editable: false,
		            	   cellsrenderer: function(row, columnfield, value, defaulthtml, columnproperties){
		            		   return defaulthtml;
		            	   }
		               }
		               ];
		var grid = $("#recruitmentReqCondGridAppr");
		var config = {
				width: '99%',
		   		rowsheight: 25,
		   		autoheight: true,
		   		virtualmode: true,
		   		showfilterrow: false,
		   		selectionmode: 'singlerow',
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: false,
		        url: '',    
	   			showtoolbar: false,
	   			pagesizeoptions: [5],
	        	source: {pagesize: 5, id: 'recruitmentReqCondTypeId'}
		};
		Grid.initGrid(config, datafield, columns, null, $("#recruitmentReqCondGridAppr"));
	};
	
	var validate = function(){
		return $("#recruitRequireApprWindow").jqxValidator('validate');
	};
	
	var initJqxValidator = function(){
		$("#recruitRequireApprWindow").jqxValidator({
			rules: [
            ]
		});
	};
	var prepareData = function(){
		_recruitmentRequireId = _data.recruitmentRequireId;
		$("#partyIdAppr").jqxInput('val', {value: _data.partyId, label: _data.groupName});
		$("#monthYearAppr").val(uiLabelMap.CommonMonth + ' ' + (_data.month + 1) + '/' + _data.year);
		var emplPositionTypeId = _data.emplPositionTypeId;
		var emplPositionTypeDesc = "";
		for(var i = 0; i < globalVar.emplPositionTypeArr.length; i++){
			if(emplPositionTypeId == globalVar.emplPositionTypeArr[i].emplPositionTypeId){
				emplPositionTypeDesc = globalVar.emplPositionTypeArr[i].description;
				break;
			}
		}
		$("#quantityAppr").val(_data.quantity);
		$("#quantityUnplannedAppr").val(_data.quantityUnplanned);
		var recruitmentFormTypeId = _data.recruitmentFormTypeId;
		if(recruitmentFormTypeId){
			var recruitmentFormTypeDesc = "";
			for(var i = 0; i < globalVar.recruitmentFormTypeArr.length; i++){
				if(recruitmentFormTypeId == globalVar.recruitmentFormTypeArr[i].recruitmentFormTypeId){
					recruitmentFormTypeDesc = globalVar.recruitmentFormTypeArr[i].description;
					break;
				}
			}
			$("#recruitmentFormTypeAppr").jqxInput('val', {label: recruitmentFormTypeDesc, value: recruitmentFormTypeId});
		}
		$("#partyIdCreated").jqxInput('val', {label: _data.fullName, value: _data.partyIdCreated});
		if(_data.comment){
			$("#commentRecruitment").html(_data.comment);
		}else{
			$("#commentRecruitment").html(uiLabelMap.HRCommonNotSetting);
		}
		var statusId = _data.statusId;
		if(statusId){
			var statusDesc = "";
			for(var i = 0; i < globalVar.statusArr.length; i++){
				if(statusId == globalVar.statusArr[i].statusId){
					statusDesc = globalVar.statusArr[i].description;
					break;
				}
			}
			$("#currStatusId").jqxInput('val', {label: statusDesc, value: statusId});
			
		}
		$("#emplPositionTypeIdAppr").jqxInput('val', {label: emplPositionTypeDesc, value: emplPositionTypeId});
		if(_data.enumRecruitReqTypeId == "RECRUIT_REQUIRE_UNPLANNED"){
			$("#plannedRadioBtn").jqxCheckBox({checked: false});
			$("#unplannedRadioBtn").jqxCheckBox({checked: true});
		}else if(_data.enumRecruitReqTypeId == "RECRUIT_REQUIRE_PLANNED"){
			$("#plannedRadioBtn").jqxCheckBox({checked: true});
			$("#unplannedRadioBtn").jqxCheckBox({checked: false});
		}else{
			$("#plannedRadioBtn").jqxCheckBox({checked: false});
			$("#unplannedRadioBtn").jqxCheckBox({checked: false});
		}
		refreshGrid(_data.recruitmentRequireId);
		$("#ajaxLoadingApprove").show();
		disableBtn();
		$.ajax({
			url: 'checkPermissionApprRecruitmentRequire',
			data: {recruitmentRequireId: _data.recruitmentRequireId},
			type: 'POST',
			success: function(response){
				if(response.hasPermission){
					_hasPermission = response.hasPermission;
				}
			},
			complete: function(jqXHR, textStatus){
				$("#ajaxLoadingApprove").hide();
				enableBtn();
				if(_hasPermission){
					enableAppr();
				}else{
					disableAppr();
				}
			}
		});
	};
	var enableAppr = function(){
		$("#cancelApproval").show();
		$("#rejectApproval").show();
		$("#acceptApproval").show();
		$("#commentApproval").jqxEditor({disabled: false});
	};
	var disableAppr = function(){
		$("#cancelApproval").hide();
		$("#rejectApproval").hide();
		$("#acceptApproval").hide();
		$("#commentApproval").jqxEditor({disabled: true});
	};
	var setData = function(data){
		_data = data;
	};
	var refreshGrid = function(recruitmentRequireId){
		refreshBeforeReloadGrid($("#recruitmentReqCondGridAppr"));
		var source = $("#recruitmentReqCondGridAppr").jqxGrid('source');
		source._source.url = "jqxGeneralServicer?sname=JQListRecruitmentRequireCond&recruitmentRequireId=" + recruitmentRequireId;
		$("#recruitmentReqCondGridAppr").jqxGrid('source', source);
	};
	var initJqxEditor = function(){
		$("#commentApproval").jqxEditor({ 
    		width: '99%',
            theme: 'olbiuseditor',
            tools: '',
            height: 110,
            disabled: true
        });
	};
	
	var initJqxPanel = function(){
		$("#panelRecruitmentRequireConds").jqxPanel({ width: '99,5%', height: '99%', autoUpdate: true});
	};
	
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxEditor();
			initJqxNumberInput();
		};
		createJqxWindow($("#recruitRequireApprWindow"), 900, 580, initContent);
		$("#recruitRequireApprWindow").on('close', function(event){
			_recruitmentRequireId = "";
			Grid.clearForm($(this));
			_data = {};
			_hasPermission = false;
			disableAppr();
		});
		$("#recruitRequireApprWindow").on('open', function(event){
			prepareData();
		});
	};
	
	var openWindow = function(){
		openJqxWindow($("#recruitRequireApprWindow"));
	};
	return{
		init: init,
		setData: setData,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	recruitReqApprObj.init()
});