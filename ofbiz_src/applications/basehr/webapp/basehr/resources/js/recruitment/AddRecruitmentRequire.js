var generalInfoRecReqObj = (function(){
	var _ancestorTree = [];
	var _isInitedWindow = false;
	var defaultData = {};
	var init = function(){
		initJqxDropDownList();
		initJqxNumberInput();
		initJqxTreeButton();
		initEvent();
		initJqxValidator();
		initSimpleInput();
		
	};
	var initDropDownGrid = function(){
		$("#approverListDropDownBtn").jqxDropDownButton({width: '98%', height: 25});
		var datafield = [{name: 'partyId', type: 'string'},
		                 {name: 'partyName', type: 'string'},
		                 {name: 'emplPositionType', type: 'string'}];
		var columns = [{text: uiLabelMap.EmployeeId, datafield : 'partyId', width : '23%', editable: false},
		               {text: uiLabelMap.EmployeeName, datafield: 'partyName', width: '30%', editable: false},
		               {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', width: '47%', editable: false}];
		var config = {
		   		width: 500, 
		   		rowsheight: 25,
		   		autoheight: true,
		   		virtualmode: true,
		   		showfilterrow: false,
		   		selectionmode: 'singlerow',
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: false,
		        url: 'JQGetListHRMAdminAuthorization',    
	   			showtoolbar: false,
	        	source: {pagesize: 5, id: 'partyId'}
		 };
		Grid.initGrid(config, datafield, columns, null, $("#jqxGridApprover"));
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList([], $("#emplPositionTypeAddNew"), "emplPositionTypeId", "description", 25, '98%');
		createJqxDropDownList(globalVar.recruitmentFormTypeArr, $("#recruitmentFormTypeNew"), "recruitmentFormTypeId", "description", 25, '98%');
		var monthData = [];
		for(var i = 0; i < 12; i++){
			monthData.push({month: i, description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		createJqxDropDownList(monthData, $("#monthNew"), "month", "description", 25, 90);
	};
	
	var initEvent = function(){
		/*$("#jqxGridApprover").on('rowselect', function (event) {
            var args = event.args;
            var row = $("#jqxGridApprover").jqxGrid('getrowdata', args.rowindex);
            var dropDownContent = '<div style="position: relative; margin-left: 3px; margin-top: 5px;">' + row['partyName'] + '</div>';
            $("#approverListDropDownBtn").jqxDropDownButton('setContent', dropDownContent);
            $("#approverListDropDownBtn").jqxDropDownButton('close');
        });*/
		$("#unplannedRecruitmentCheck").on('change', function(event){
			var checked = event.args.checked;
			$("#quantityUnplannedNew").jqxNumberInput({disabled: !checked});
		});
		$("#yearNew").on("valueChanged", function(event){
			var year = event.args.value;
			var month = $("#monthNew").val();
			var item = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
			if(item){
				var partyId = item.value;
				refreshEmplPositionTypeDropDown(partyId, month, year);
			}
		});
		$("#monthNew").on("select", function(event){
			var args = event.args;
			if(args){
				var month = args.item.value;
				var year = $("#yearNew").val();
				var item = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
				if(item){
					var partyId = item.value;
					refreshEmplPositionTypeDropDown(partyId, month, year);
				}
			}
		});
		$("#emplPositionTypeAddNew").on('select', function(event){
			var args = event.args;
			if(args){
				var emplPositionTypeId = args.item.value;
				var month = $("#monthNew").val();
				var year = $("#yearNew").val();
				var partyIdTreeSelected = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
				var partyId = partyIdTreeSelected.value;
				if(partyId && month && year && emplPositionTypeId){
					getRecruitAnticipateByMonthYear(partyId, emplPositionTypeId, month, year);
				}
			}
		});
	};
	
	var getRecruitAnticipateByMonthYear = function(partyId, emplPositionTypeId, month, year){
		disableAll();
		$("#ajaxLoading").show();
		$.ajax({
			url: 'getRecruitAnticipateByMonthYear',
			data: {partyId: partyId, emplPositionTypeId: emplPositionTypeId, month: month, year: year},
			type: 'POST',
			success: function(response){
				if(response._ERROR_MESSAGE_){
					bootbox.dialog(response._ERROR_MESSAGE_,
							[
							{
				    		    "label" : uiLabelMap.CommonClose,
				    		    "class" : "btn-danger btn-small icon-remove open-sans",
				    		}]		
						);
					$("#quantityNew").val(0);
					$("#quantityApproved").val(0);
				}else if(response.isPlanned){
					$("#quantityNew").val(response.quantity);
					$("#quantityApproved").val(response.quantity);
				}else{
					bootbox.dialog(response.message,
							[
							{
				    		    "label" : uiLabelMap.CommonClose,
				    		    "class" : "btn-danger btn-small icon-remove open-sans",
				    		}]		
						);
					$("#quantityNew").val(0);
					$("#quantityApproved").val(0);
				}
			},
			complete: function(jqXHR, textStatus){
				enableAll();
				$("#ajaxLoading").hide();
			}
		});
	};
	var initJqxNumberInput = function(){
		$("#quantityApproved").jqxNumberInput({width: '98%', height: '25px', spinButtons: true,  inputMode: 'simple', decimalDigits: 0, min: 0, disabled: true});
		$("#quantityNew").jqxNumberInput({width: '92%', height: '25px', spinButtons: true,  inputMode: 'simple', decimalDigits: 0, min: 0, disabled: false});
		$("#quantityUnplannedNew").jqxNumberInput({width: '94%', height: '25px', spinButtons: true,  inputMode: 'simple', decimalDigits: 0, min: 0, disabled: true});
	};
	
	var expandTreeCompleteFunc = function(){
		if(_ancestorTree.length > 0){
			var ancestorId = _ancestorTree.splice(0, 1)[0];
			$("#jqxTreeAddNew").jqxTree('expandItem', $("#" + ancestorId + "_treeNew")[0]);
		}else{
			if(defaultData.hasOwnProperty("partyId")){
				$("#jqxTreeAddNew").jqxTree('selectItem', $("#" + defaultData.partyId + "_treeNew")[0]);
			};
		}
	};
	
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 448, treeWidth: 448, async: false, expandCompleteFunc: expandTreeCompleteFunc};
		globalObject.createJqxTreeDropDownBtn($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"), globalVar.rootPartyArr, "treeNew", "treeChildNew", config);
		$('#jqxTreeAddNew').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTreeAddNew').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTreeAddNew"), $("#dropDownButtonAddNew"));
			var partyId = item.value;
			var month = $("#monthNew").val();
			var year = $("#yearNew").val();
			if(month && year){
				refreshEmplPositionTypeDropDown(partyId, month, year);
			}
		});
	};
	
	var refreshEmplPositionTypeDropDown = function(partyId, month, year){
		if(_isInitedWindow){
			if(typeof(partyId) != 'undefined' && partyId.length > 0 && typeof(month) != 'undefined'){
				$("#emplPositionTypeAddNew").jqxDropDownList({disabled: true});
				$.ajax({
					url: 'getListAllEmplPositionTypeOfParty',
					data: {partyId: partyId, month: month, year: year},
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							updateSourceDropdownlist($("#emplPositionTypeAddNew"), response.listReturn);
							if(defaultData.hasOwnProperty("emplPositionTypeId")){
								$("#emplPositionTypeAddNew").val(defaultData.emplPositionTypeId);
							}
						}
					},
					complete: function(jqXHR, textStatus){
						$("#emplPositionTypeAddNew").jqxDropDownList({disabled: false});
					}
				});
			}
		}
	};
	
	var initJqxEditor = function(){
		$("#commentNew").jqxEditor({ 
    		width: '98%',
            theme: 'olbiuseditor',
            tools: '',
            height: 130,
        });
		if(defaultData.hasOwnProperty("comment")){
			$("#commentNew").val(defaultData.comment);
		}
	};
	
	var resetData = function(){
		_isInitedWindow = false;
		clearDropDownContent($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"));
		Grid.clearForm($("#generalInfo"));
		$('#monthNew').jqxDropDownList('clearSelection');
		$('#emplPositionTypeAddNew').jqxDropDownList('clearSelection');
		$("#jqxTreeAddNew").jqxTree('collapseAll');
		delete defaultData.emplPositionTypeId;
		delete defaultData.monthCustomTimePeriodId;
		delete defaultData.comment;
		delete defaultData.partyId;
	};
	
	var getData = function(){
		var retData = {};
		var itemSelect = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
		retData.partyId = itemSelect.value;
		retData.month = $('#monthNew').val();
		retData.year = $('#yearNew').val();
		retData.emplPositionTypeId = $("#emplPositionTypeAddNew").val();
		retData.quantity = $("#quantityNew").val();
		var checked = $("#unplannedRecruitmentCheck").jqxCheckBox('checked');
		if(checked){
			retData.quantityUnplanned = $("#quantityUnplannedNew").val();
			retData.isUnplanned = "Y";
		}
		retData.recruitmentFormTypeId = $("#recruitmentFormTypeNew").val();
		retData.comment = $("#commentNew").jqxEditor('val');
		/*var approverRowIndex = $("#jqxGridApprover").jqxGrid('getselectedrowindex');
		var approvedPartyData = $("#jqxGridApprover").jqxGrid('getrowdata', approverRowIndex);
		retData.approvedPartyId = approvedPartyData.partyId;*/
		return retData;
	};
	
	var setData = function(data){
		defaultData.monthCustomTimePeriodId = data.monthCustomTimePeriodId;
		defaultData.emplPositionTypeId = data.emplPositionTypeId;
		$("#recruitmentFormTypeNew").val(data.recruitmentFormTypeId);
		$("#yearCustomTimeNew").val(data.yearCustomTimePeriodId);
		$("#quantityNew").val(data.quantity);
		if(data.hasOwnProperty("comment")){
			$("#commentNew").val(data.comment);
			defaultData.comment = data.comment;
		}
		if(data.hasOwnProperty("ancestorTree")){
			defaultData.partyId = data.partyId;
			_ancestorTree = data.ancestorTree;
			if(_ancestorTree.length > 0){
				var ancestorId = _ancestorTree.splice(0, 1)[0];
				$("#jqxTreeAddNew").jqxTree('expandItem', $("#" + ancestorId + "_treeNew")[0]);
			}
		}
		$("#jqxTreeAddNew").jqxTree('selectItem', $("#" + data.partyId + "_treeNew")[0]);
		/*if(data.approvedPartyId){
			var index = $('#jqxGridApprover').jqxGrid('getrowboundindexbyid', data.approvedPartyId);
			$('#jqxGridApprover').jqxGrid('selectrow', index);
		}*/
	};
	
	var validate = function(){
		var valid = $("#generalInfo").jqxValidator('validate');
		var valid2 = $("#unplannedRecruitmentCheck").jqxValidator('validate'); 
		return  valid && valid2;
	};
	
	var hideValidate = function(){
		$("#generalInfo").jqxValidator('hide');
		$("#unplannedRecruitmentCheck").jqxValidator('hide');
	};
	
	var initJqxValidator = function(){
		$("#generalInfo").jqxValidator({
			rules: [
				{input : '#monthNew', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#yearNew', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#emplPositionTypeAddNew', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				
				{input : '#quantityUnplannedNew', message : uiLabelMap.ValueMustBeGreateThanZero, action: 'blur', 
					rule : function(input, commit){
						var checked = $("#unplannedRecruitmentCheck").jqxCheckBox('checked');
						if(checked){
							if(input.val() <= 0){
								return false;
							}
						}
						return true;
					}
				},
				{input : '#quantityNew', message : uiLabelMap.ValueMustLessThanValueAppr, action: 'blur', 
					rule : function(input, commit){
						var quantityAppr = $("#quantityApproved").val();
						if(input.val() > quantityAppr){
							return false;
						}
						return true;
					}
				},
				{input : '#dropDownButtonAddNew', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						var itemSelect = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
						if(!itemSelect){
							return false;
						}
						return true;
					}
				},
				/*{input : '#approverListDropDownBtn', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						var selectedIndex = $("#jqxGridApprover").jqxGrid('getselectedrowindex');
						if(selectedIndex < 0){
							return false;
						}
						return true;
					}
				},*/
            ]
		});
		$("#unplannedRecruitmentCheck").jqxValidator({
			rules: [
				{input : '#unplannedRecruitmentCheck', message : uiLabelMap.RecruitmentQuantityPlanAndUnplanedMustGreaterThanZero, action: 'blur', 
					rule : function(input, commit){
						var quantityUnplanned = $("#quantityUnplannedNew").val();
						var quantityPlanned = $("#quantityNew").val();
						var checked = $("#unplannedRecruitmentCheck").jqxCheckBox('checked');
						if(!checked){
							if(quantityPlanned + quantityUnplanned <= 0){
								return false;
							}
						}
						return true;
					}
				},    
			],
			arrow: false,
			position: 'bottomcenter',
		});
	};
	var initSimpleInput = function(){
		$("#unplannedRecruitmentCheck").jqxCheckBox({ width: '100%', height: 25});
		$("#quantityApprTooltip").jqxTooltip({content: '<i>' + uiLabelMap.RecruitmentAnticipateQuantityNotes + '</i>', position: 'mouse', name: 'movieTooltip'});
		$("#yearNew").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
	};
	var disableAll = function(){
		$("#btnNext").attr("disabled", "disabled");
		$("#btnPrev").attr("disabled", "disabled");
		$("#dropDownButtonAddNew").jqxDropDownButton({disabled: true});
		$("#monthNew").jqxDropDownList({disabled: true});
		$("#yearNew").jqxNumberInput({disabled: true});
		$("#recruitmentFormTypeNew").jqxDropDownList({disabled: true});
		$("#emplPositionTypeAddNew").jqxDropDownList({disabled: true});
		var checked = $("#unplannedRecruitmentCheck").jqxCheckBox('checked');
		if(checked){
			$("#quantityUnplannedNew").jqxNumberInput({disabled: true});
		}
		$("#unplannedRecruitmentCheck").jqxCheckBox({disabled: true});
		$("#commentNew").jqxEditor({disabled: true});
	};
	
	var enableAll = function(){
		$("#btnNext").removeAttr("disabled");
		$("#btnPrev").removeAttr("disabled");
		$("#dropDownButtonAddNew").jqxDropDownButton({disabled: false});
		$("#monthNew").jqxDropDownList({disabled: false});
		$("#yearNew").jqxNumberInput({disabled: false});
		$("#recruitmentFormTypeNew").jqxDropDownList({disabled: false});
		$("#emplPositionTypeAddNew").jqxDropDownList({disabled: false});
		var checked = $("#unplannedRecruitmentCheck").jqxCheckBox('checked');
		if(checked){
			$("#quantityUnplannedNew").jqxNumberInput({disabled: false});
		}
		$("#unplannedRecruitmentCheck").jqxCheckBox({disabled: false});
		$("#commentNew").jqxEditor({disabled: false});
	};
	var initWindowContent = function(){
		initJqxEditor();
	};
	var initData = function(){
		var date = new Date();
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTreeAddNew").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_treeNew")[0]);
		}
		$("#monthNew").val(date.getMonth());
		$("#yearNew").val(date.getFullYear());
		_isInitedWindow = true;
		refreshEmplPositionTypeDropDown(globalVar.rootPartyArr[0].partyId, date.getMonth(), date.getFullYear());
	};
	return{
		init: init,
		validate: validate,
		getData: getData,
		setData: setData,
		resetData: resetData,
		initData: initData,
		hideValidate: hideValidate,
		initWindowContent: initWindowContent,
	}
}());

var initWizard = (function(){
	var _isEditMode = false;
	var _recruitmentRequireId = "";
	var _data = {};
	var init = function(){
		create_spinner($("#spinnerAjax"));
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
	        if(info.step == 1 && (info.direction == "next")) {
	        	var valid = generalInfoRecReqObj.validate();
	        	if(!valid){
	        		return false;
	        	}
	        }
	    }).on('finished', function(e) {
	    	if(_isEditMode){
	    		updateRecruitRequirement();
	    	}else{
	    		bootbox.dialog(uiLabelMap.ConfirmCreateRecruitRequirement,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								createRecruitRequirement();
							}	
						},
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
	    	}
	    }).on('stepclick', function(e){
	    	
	    });
		
		initJqxWindow();
		initEvent();
	};
	
	var initEvent = function(){
		$("#jqxgrid").on('rowdoubleclick', function(event){
			/*var args = event.args;
			var boundIndex = args.rowindex;
			var data = $("#jqxgrid").jqxGrid('getrowdata', boundIndex);
			openEditRecruitmentRequire(data);
			$("#quantityNew").jqxNumberInput('val', data.quantity);
			$("#quantityUnplannedNew").jqxNumberInput('val', data.quantityUnplanned);
			$("#quantityApproved").jqxNumberInput('val', data.quantityAppr);*/
		});
	};
	
	var openEditRecruitmentRequire = function(data){
		_isEditMode = true;
		var monthCustomTimePeriodId = data.customTimePeriodId;
		data.yearCustomTimePeriodId = $("#yearCustomTimePeriod").val();
		data.monthCustomTimePeriodId = monthCustomTimePeriodId;
		_recruitmentRequireId = data.recruitmentRequireId;
		_data = data;
		openWindow();
		disableBtn();
		$("#ajaxLoading").show();
		$.when(
			$.ajax({
				url: 'getAncestorTreeOfPartyGroup',
				data: {partyId: _data.partyId},
				type: 'POST',
				success: function(response){
					if(response.ancestorTree && response.ancestorTree.length > 0){
						_data.ancestorTree = response.ancestorTree;
					}
				},
				complete: function(jqXHR, textStatus){
					generalInfoRecReqObj.setData(_data);
				}
			}),
			$.ajax({
				url: 'getRecruitmentRequireConds',
				data: {recruitmentRequireId: _data.recruitmentRequireId},
				type: 'POST',
				success: function(response){
					if(response.results){
						recuritmentReqCondObj.setData(response.results);
					}
				},
				complete: function(jqXHR, textStatus){}
			})
		).done(function(ajaxData1, ajaxData2){
			$("#ajaxLoading").hide();
			enableBtn();
		});
	};
	
	var updateRecruitRequirement = function(){
		var generalData = generalInfoRecReqObj.getData();
		var condData = recuritmentReqCondObj.getData();
    	var dataSubmit = $.extend({}, generalData, condData);
    	dataSubmit.recruitmentRequireId = _recruitmentRequireId;
    	disableBtn();
    	$("#ajaxLoading").show();
    	$.ajax({
    		url: 'updateRecruitmentRequireAndCond',
    		data: dataSubmit,
    		type: 'POST',
    		success: function(response){
    			if(response.responseMessage == "success"){
    				$("#addRecruitmentRequireWindow").jqxWindow('close');
    				$('#containerNtf').empty();
					$("#jqxNotificationNtf").jqxNotification('closeLast');
					$("#notificationContentNtf").text(response.successMessage);
					$("#jqxNotificationNtf").jqxNotification('open');
					$("#jqxgrid").jqxGrid('updatebounddata');
    			}else{
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger icon-remove",
							}]
					);
				}
    		},
    		complete: function(jqXHR, textStatus){
    			enableBtn();
    			$("#ajaxLoading").hide();
			}
    	});
	};
	
	var createRecruitRequirement = function(){
		var generalData = generalInfoRecReqObj.getData();
    	//var condData = recruitReqCriteriaObj.getData();
		var condData = recuritmentReqCondObj.getData();
    	var dataSubmit = $.extend({}, generalData, condData);
    	disableBtn();
    	$("#ajaxLoading").show();
    	$.ajax({
    		url: 'createRecruitmentRequireCond',
    		data: dataSubmit,
    		type: 'POST',
    		success: function(response){
    			if(response.responseMessage == "success"){
    				$("#addRecruitmentRequireWindow").jqxWindow('close');
    				$('#containerNtf').empty();
					$("#jqxNotificationNtf").jqxNotification('closeLast');
					$("#notificationContentNtf").text(response.successMessage);
					$("#jqxNotificationNtf").jqxNotification('open');
					$("#jqxgrid").jqxGrid('updatebounddata');
    			}else{
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]
					);
				}
    		},
    		complete: function(jqXHR, textStatus){
    			enableBtn();
    			$("#ajaxLoading").hide();
			}
    	});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#addRecruitmentRequireWindow"), 730, 540, generalInfoRecReqObj.initWindowContent);
		$("#addRecruitmentRequireWindow").on('open', function(event){
			recuritmentReqCondObj.setGridEle($("#recruitReqCondGrid"));
			if(!_isEditMode){
				generalInfoRecReqObj.initData();
				$("#addRecruitmentRequireWindow").jqxWindow('setTitle', uiLabelMap.AddNewRecruitmentRequire);
			}else{
				$("#addRecruitmentRequireWindow").jqxWindow('setTitle', uiLabelMap.EditRecruitmentRequirement);
			}
		});
		$("#addRecruitmentRequireWindow").on('close', function(event){
			generalInfoRecReqObj.resetData();
			generalInfoRecReqObj.hideValidate();
			recuritmentReqCondObj.resetData();
			initWizard.resetStep();
			_isEditMode = false;
			_recruitmentRequireId = "";
			_data = {};
			//$('#jqxGridApprover').jqxGrid('clearselection');
		});
	};
	
	var disableBtn = function(){
		$("#btnNext").attr("disabled", "disabled");
		$("#btnPrev").attr("disabled", "disabled");
	};
	
	var enableBtn = function(){
		$("#btnNext").removeAttr("disabled");
		$("#btnPrev").removeAttr("disabled");
	};
	
	var resetStep = function(){
		$('#fuelux-wizard').wizard('previous');
	};
	var openWindow = function(){
		openJqxWindow($("#addRecruitmentRequireWindow"));
	};
	
	return {
		init: init,
		resetStep: resetStep,
		openEditRecruitmentRequire: openEditRecruitmentRequire,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	generalInfoRecReqObj.init();
	initWizard.init();
});