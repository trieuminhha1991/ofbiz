var recruitmentInfoObj = (function(){
	var defaultData = {};
	var _isInitedWindow = false;
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxTreeButton();
		initJqxNumberInput();
		initJqxDateTimeInput();
		initJqxValidator();
		initEvent();
	};
	var initJqxInput = function(){
		$("#recruitmentPlanNameNew").jqxInput({width: '96%', height: 20});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList([], $("#emplPositionTypeIdNew"), "emplPositionTypeId", "description", 25, '98%');
		createJqxDropDownList(globalVar.recruitmentFormTypeArr, $("#recruitmentFormTypeNew"), "recruitmentFormTypeId", "description", 25, '98%');
		var monthData = [];
		for(var i = 0; i < 12; i++){
			monthData.push({month: (i+1), description: uiLabelMap.CommonMonth + " " + (i + 1)});
		}
		createJqxDropDownList(monthData, $("#monthNew"), "month", "description", 25, 90);
	};
	
	var expandTreeCompleteFunc = function(){
		if(defaultData.hasOwnProperty("partyId")){
			$("#jqxTreeAddNew").jqxTree('selectItem', $("#" + defaultData.partyId + "_treeNew")[0]);
		};
	};
	
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 228, treeWidth: 228, async: true, expandCompleteFunc: expandTreeCompleteFunc};
		globalObject.createJqxTreeDropDownBtn($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"), globalVar.rootPartyArr, "treeNew", "treeChildNew", config);
	};
	
	var initJqxNumberInput = function(){
		$("#quantityNew").jqxNumberInput({width: '98%', height: '25px', spinButtons: true,  inputMode: 'simple', decimalDigits: 0, min: 0});
		$("#salaryAmountNew").jqxNumberInput({width: '98%', height: '25px', spinButtons: true,  decimalDigits: 0, min: 0, max: 9999999999});
		$("#estimatedCostNew").jqxNumberInput({width: '98%', height: '25px', spinButtons: true,  decimalDigits: 0, min: 0, max: 9999999999});
		$("#actualCost").jqxNumberInput({width: '98%', height: '25px', spinButtons: true,  decimalDigits: 0, min: 0, max: 9999999999, disabled: true});
		$("#yearNew").jqxNumberInput({ width: 70, height: 25, spinButtons: true, inputMode: 'simple', decimalDigits: 0});
	};
	
	var initJqxEditor = function(){
		$("#requirementDesc").jqxEditor({ 
    		width: '280%',
            theme: 'olbiuseditor',
            tools: '',
            height: 120,
        });
		if(defaultData.hasOwnProperty("comment")){
			$("#requirementDesc").val(defaultData.comment);
		}
	};
	
	var initJqxDateTimeInput = function(){
		$("#recruitmentDateTime").jqxDateTimeInput({ width: '98%', height: 25,  selectionMode: 'range' , showFooter: true});
		$("#recruitmentApplyDateTime").jqxDateTimeInput({ width: '98%', height: 25,  selectionMode: 'range', showFooter: true});
		$("#recruitmentDateTime").val(null);
		$("#recruitmentApplyDateTime").val(null);
	};
	
	var initEvent = function(){
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
	};
	
	var refreshEmplPositionTypeDropDown = function(partyId, month, year){
		if(_isInitedWindow){
			if(typeof(partyId) != 'undefined' && partyId.length > 0 && typeof(month) != 'undefined'){
				$("#emplPositionTypeIdNew").jqxDropDownList({disabled: true});
				$.ajax({
					url: 'getListAllEmplPositionTypeOfParty',
					data: {partyId: partyId, month: month, year: year},
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							updateSourceDropdownlist($("#emplPositionTypeIdNew"), response.listReturn);
							if(defaultData.hasOwnProperty("emplPositionTypeId")){
								$("#emplPositionTypeIdNew").val(defaultData.emplPositionTypeId);
							}
						}
					},
					complete: function(jqXHR, textStatus){
						$("#emplPositionTypeIdNew").jqxDropDownList({disabled: false});
					}
				});
			}
		}
	};
	
	var initWindowContent = function(){
		initJqxEditor();
	};
	
	var resetData = function(){
		_isInitedWindow = false;
		clearDropDownContent($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"));
		Grid.clearForm($("#recruitmentInfo"));
	};
	
	var getData = function(){
		var retData = {};
		retData.recruitmentPlanName = $("#recruitmentPlanNameNew").val();
		var itemSelect = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
		retData.partyId = itemSelect.value;
		retData.quantity = $("#quantityNew").val();
		retData.recruitmentFormTypeId = $("#recruitmentFormTypeNew").val();
		retData.month = $('#monthNew').val()-1;
		retData.year = $('#yearNew').val();
		retData.emplPositionTypeId = $("#emplPositionTypeIdNew").val();
		retData.salaryAmount = $("#salaryAmountNew").val();
		retData.estimatedCost = $("#estimatedCostNew").val();
		retData.requirementDesc = $("#requirementDesc").val();
		var recruitmentDate = $("#recruitmentDateTime").jqxDateTimeInput('getRange');
		if(recruitmentDate.from && recruitmentDate.to){
			retData.recruitmentFromDate = recruitmentDate.from.getTime();
			retData.recruitmentThruDate = recruitmentDate.to.getTime();
		}
		var recruitmentApplyDate = $("#recruitmentApplyDateTime").jqxDateTimeInput('getRange');
		if(recruitmentApplyDate.from && recruitmentApplyDate.to){
			retData.applyFromDate = recruitmentDate.from.getTime();
			retData.applyThruDate = recruitmentDate.to.getTime();
		}
		return retData;
	};
	
	var initJqxValidator = function(){
		$("#recruitmentInfo").jqxValidator({
			rules: [
			        {input : '#recruitmentPlanNameNew', message : uiLabelMap.FieldRequired, action : 'blur',
			        	rule : function(input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#recruitmentPlanNameNew', message : uiLabelMap.InvalidChar, action : 'blur',
			        	rule : function(input, commit){
			        		var val = input.val();
			        		if(val){
			        			if(validationNameWithoutHtml(val)){
				        			return false;
				        		}
			        		}
			        		return true;
			        	}
			        },
			        
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
			        {input : '#emplPositionTypeIdNew', message : uiLabelMap.FieldRequired, action : 'blur',
			        	rule : function(input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {input : '#quantityNew', message : uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
			        	rule : function(input, commit){
			        		if(input.val() <= 0){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {input : '#dropDownButtonAddNew', message : uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
			        	rule : function(input, commit){
			        		var itemSelect = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
							if(!itemSelect){
								return false;
							}
							return true;
			        	}
			        },
	        ]
		});
	};
	var initData = function(){
		var date = new Date();
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTreeAddNew").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_treeNew")[0]);
		}
		$("#monthNew").val(date.getMonth()+1);
		$("#yearNew").val(date.getFullYear());
		_isInitedWindow = true;
		refreshEmplPositionTypeDropDown(globalVar.rootPartyArr[0].partyId, date.getMonth(), date.getFullYear());
	};
	var validate = function(){
		return $("#recruitmentInfo").jqxValidator('validate');
	};
	
	return{
		init: init,
		validate: validate,
		resetData: resetData,
		getData: getData,
		initData: initData,
		initWindowContent: initWindowContent
	}
}());

var wizardObj = (function(){
	var _isEditMode = false;
	var init = function(){
		initWizard();
		initJqxWindow();
		create_spinner($("#spinnerAjax"));
	};
	
	var initJqxWindow = function(){
		var initContent = function(){
			recruitmentInfoObj.initWindowContent();
		};
		createJqxWindow($("#editRecruitmentPlanWindow"), 870, 550, initContent);
		$("#editRecruitmentPlanWindow").on('open', function(event){
			recruitmentInfoObj.initData();
			/**
			 * editRecruitmentCostItemObj is defined in RecruitmentCostUtils.js
			 * recruitmentCostGridObj is defined in RecruitCreateRecruitCost.js
			 */
			editRecruitmentCostItemObj.setCreateRecruitmentCostItem(recruitmentCostGridObj.createRecruitmentCostItem);
			editRecruitmentCostItemObj.setUpdateRecruitmentCostItem(recruitmentCostGridObj.updateRecruitmentCostItem);
			
			recruitmentBoardObj.setGridEle($("#recruitmentBoardGrid"));//recruitmentBoardObj is defined in RecruitCreateRecruitmentPlanBoard.js
			recruitmentRoundObject.setGridEle($("#recruitmentRoundGrid"));//recruitmentRoundObject is defined in RecruitCreateRecruitRound.js
			recruitmentCostGridObj.setGridEle($("#recruitmentCostGrid"));//recruitmentSubjectListObj is defined in RecruitCreateRecruitCost.js
			
		});
		$("#editRecruitmentPlanWindow").on('close', function(event){
			recruitmentInfoObj.resetData();
			recruitmentBoardObj.resetData();
			recruitmentRoundObject.resetData();
			recuritmentReqCondObj.resetData();
			recruitmentCostGridObj.resetData();
			editRecruitmentCostItemObj.setCreateRecruitmentCostItem(null);//editRecruitmentCostItemObj is defined in RecruitmentCostUtils.js
			editRecruitmentCostItemObj.setUpdateRecruitmentCostItem(null);
			resetStep();
		});
	};
	
	var initWizard = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
	        if(info.step == 1 && (info.direction == "next")) {
	        	if(!recruitmentInfoObj.validate()){
	        		return false;
	        	}
	        }else if(info.step == 3 && (info.direction == "next")){
	        	if(!recruitmentBoardObj.validate()){
	        		return false;
	        	}
	        	recruitmentRoundObject.setInterviewerMakerData(recruitmentBoardObj.getGridRowData());
	        }else if(info.step == 4 && (info.direction == "next")){
	        	
	        }
	    }).on('finished', function(e) {
	    	if(_isEditMode){

	    	}else{
	    		bootbox.dialog(uiLabelMap.ConfirmCreateRecruitmentPlan,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								createRecruitmentPlan();
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
	};
	
	var createRecruitmentPlan = function(){
		var board = recruitmentBoardObj.getData();
		var round = recruitmentRoundObject.getData();
		var info = recruitmentInfoObj.getData();
		//var cond = recruitReqCriteriaObj.getData();
		var cond = recuritmentReqCondObj.getData();
		var cost = recruitmentCostGridObj.getData();
		var dataSubmit = $.extend({}, board, round, info, cond, cost);
		$("#ajaxLoading").show();
		disableBtn();
		recruitmentCostGridObj.disable();
		$.ajax({
			url: 'createRecruitmentPlan',
			type: 'POST',
			data: dataSubmit,
			success: function(response){
				if(response.responseMessage == "success"){
					$('#containerNtf').empty();
					$("#jqxNotificationNtf").jqxNotification('closeLast');
					$("#notificationContentNtf").text(response.successMessage);
					$("#jqxNotificationNtf").jqxNotification('open');
					$("#jqxgrid").jqxGrid('updatebounddata');
					$("#editRecruitmentPlanWindow").jqxWindow('close');
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
				$("#ajaxLoading").hide();
				enableBtn();
				recruitmentCostGridObj.enable();
			}
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
		$('#fuelux-wizard').wizard('previous');
		$('#fuelux-wizard').wizard('previous');
		$('#fuelux-wizard').wizard('previous');
	};
	
	var openWindow = function(){
		openJqxWindow($("#editRecruitmentPlanWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function(){
	recruitmentInfoObj.init();
	recuritmentReqCondObj.init($("#recruitReqCondGrid"));//recuritmentReqCondObj is defined in RecruitmentCondition.js
	recruitmentBoardObj.initJqxGrid("recruitmentBoardGrid");//recruitmentBoardObj is defined in RecruitCreateRecruitmentPlanBoard.js
	recruitmentRoundObject.initJqxGrid("recruitmentRoundGrid");//recruitmentRoundObject is defined in RecruitCreateRecruitRound.js
	recruitmentCostGridObj.initJqxGrid("recruitmentCostGrid");//recruitmentCostGridObj is defined in RecruitCreateRecruitCost.js
	wizardObj.init();
});