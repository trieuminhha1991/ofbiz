var recruitmentSalesAddEmplRecInfo = (function(){
	var _defaultData = {};
	var init = function(){
		initJqxTreeButton();
		initJqxDropDownList();
		initJqxCommonInput();
		initEvent();
		initJqxValidator();
	};
	var expandTreeCompleteFunc = function(){
		/*if(_ancestorTree.length > 0){
			var ancestorId = _ancestorTree.splice(0, 1)[0];
			$("#jqxTreeAddNew").jqxTree('expandItem', $("#" + ancestorId + "_treeNew")[0]);
		}else{
			if(defaultData.hasOwnProperty("partyId")){
				$("#jqxTreeAddNew").jqxTree('selectItem', $("#" + defaultData.partyId + "_treeNew")[0]);
			};
		}*/
	};
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 240, treeWidth: 240, async: false, expandCompleteFunc: expandTreeCompleteFunc};
		globalObject.createJqxTreeDropDownBtn($("#jqxTreeAddNew"), $("#dropDownButtonAddNew"), globalVar.rootPartyArr, "treeNew", "treeChildNew", config);
		$('#jqxTreeAddNew').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTreeAddNew').jqxTree('getItem', event.args.element);
			setDropdownContent(event.args.element, $("#jqxTreeAddNew"), $("#dropDownButtonAddNew"));
			var partyId = item.value;
			var customTimePeriod = $("#monthCustomTimeNew").val();
			if(customTimePeriod){
				//refreshEmplPositionTypeDropDown(partyId, customTimePeriod);
			}
		});
	};
	var initJqxCommonInput = function(){
		$("#startWorkingFromDate").jqxDateTimeInput({width: '98%', height: 25});
		$("#salaryBaseFlat").jqxNumberInput({width: '98%', height: 25, decimalDigits: 0, digits: 10, max: 9999999999});
		$("#commentAddNew").jqxTextArea({height: 150, width: '100%', minLength: 1});
	};
	var initJqxDropDownList = function(){
		createJqxDropDownList([], $("#emplPositionTypeAddNew"), "emplPositionTypeId", "description", 25, '98%');
		createJqxDropDownList(globalVar.customTimePeriodArr, $("#yearCustomTimeNew"), "customTimePeriodId", "periodName", 25, '96%');
		createJqxDropDownList([], $("#monthCustomTimeNew"), "customTimePeriodId", "periodName", 25, '95%');
		createJqxDropDownList(globalVar.recruitmentTypeEnumArr, $("#enumRecruitmentTypeNew"), "enumId", "description", 25, '98%');
	};
	var initEvent = function(){
		$("#yearCustomTimeNew").on("select", function(event){
			var args = event.args;
			if(args){
				 var value = args.item.value;
				 $.ajax({
					url: "getCustomTimePeriodByParent",
					data: {parentPeriodId: value},
					type: 'POST',
					success: function(data){
						if(data.listCustomTimePeriod){
							var listCustomTimePeriod = data.listCustomTimePeriod;
							updateSourceDropdownlist($("#monthCustomTimeNew"), listCustomTimePeriod);
							if(_defaultData.hasOwnProperty("monthCustomTimePeriodId")){
								$("#monthCustomTimeNew").val(_defaultData.monthCustomTimePeriodId);
							}else{
								var nowDate = new Date();
								var month = nowDate.getMonth();
								$("#monthCustomTimeNew").jqxDropDownList({selectedIndex: month});
							}
						}
					},
					complete: function(jqXHR, textStatus){
						
					}
				});
			}
		});
		$("#monthCustomTimeNew").on("select", function(event){
			var args = event.args;
			if(args){
				var value = args.item.value;
				var item = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
				if(item){
					var partyId = item.value;
					refreshEmplPositionTypeDropDown(partyId, value);
				}
			}
		});
	};
	var refreshEmplPositionTypeDropDown = function(partyId, customTimePeriod){
		if(typeof(partyId) != 'undefined' && partyId.length > 0 && typeof(customTimePeriod) != 'undefined' && customTimePeriod.length > 0){
			//$("#emplPositionTypeAddNew").jqxDropDownList({disabled: true});
			$.ajax({
				url: 'getListAllEmplPositionTypeOfParty',
				data: {partyId: partyId, customTimePeriod: customTimePeriod},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						updateSourceDropdownlist($("#emplPositionTypeAddNew"), response.listReturn);
						if(_defaultData.hasOwnProperty("emplPositionTypeId")){
							$("#emplPositionTypeAddNew").val(_defaultData.emplPositionTypeId);
						}
					}
				},
				complete: function(jqXHR, textStatus){
					//$("#emplPositionTypeAddNew").jqxDropDownList({disabled: false});
				}
			});
		}
	};
	var windowOpenInit = function(){
		Grid.clearForm($("#recruitmentInfo"));
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTreeAddNew").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_treeNew")[0]);
		}
		$("#yearCustomTimeNew").val($("#yearCustomTimePeriod").val());
	};
	var resetData = function(){
		Grid.clearForm($("#recruitmentInfo"));
		_defaultData = {};
		$("#jqxTreeAddNew").jqxTree('selectItem', null);
	};
	var initJqxValidator = function(){
		$("#recruitmentInfo").jqxValidator({
			rules: [
			        {
			        	input: '#monthCustomTimeNew',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
		        			if(!input.val()){
		        				return false;
		        			}
		        			return true;
			        	}
			        },
			        {
			        	input: '#emplPositionTypeAddNew',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#enumRecruitmentTypeNew',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#startWorkingFromDate',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input: '#dropDownButtonAddNew',
			        	message: uiLabelMap.FieldRequired,
			        	action: 'blur',
			        	rule: function (input, commit){
			        		var selectedItem = $("#jqxTreeAddNew").jqxTree('getSelectedItem');
			        		if(!selectedItem){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			]
		});
	};
	var getData = function(){
		var data = {};
		var selectedItem = $("#jqxTreeAddNew").jqxTree("getSelectedItem");
		data.partyIdFrom = selectedItem.value;
		data.customTimePeriodId = $("#monthCustomTimeNew").val();
		data.salaryBaseFlat = $("#salaryBaseFlat").val();
		data.emplPositionTypeId = $("#emplPositionTypeAddNew").val();
		data.enumRecruitmentTypeId = $("#enumRecruitmentTypeNew").val();
		data.fromDate = $("#startWorkingFromDate").jqxDateTimeInput('val', 'date').getTime();
		data.comment = $("#commentAddNew").val();
		return data;
	};
	var validate = function(){
		return $("#recruitmentInfo").jqxValidator('validate');
	};
	var hideValidate = function(){
		$("#recruitmentInfo").jqxValidator('hideValidate');
	};
	var setData = function(data){
		_defaultData.monthCustomTimePeriodId = data.customTimePeriodId;
	};
	return{
		init: init,
		windowOpenInit: windowOpenInit,
		resetData: resetData,
		validate: validate,
		getData: getData,
		setData: setData
	}
}());

var recruitmentSalesAddEmplObj = (function(){
	var init = function(){
		initWizard();
		initJqxWindow();
		create_spinner($("#spinnerAddNewEmpl"));
		$("#jqxNotificationNtf").jqxNotification({width: "100%", appendContainer: "#containerNtf", 
			opacity: 0.9, autoClose: true, template: "info"})
	};
	
	var initWizard = function(){
		$('#wizardAddEmpl').ace_wizard().on('change' , function(e, info){
	        if(info.step == 1 && (info.direction == "next")) {
	        	if(!recruitmentSalesAddEmplRecInfo.validate()){
	        		return false;
	        	}
	        }else if(info.step == 2){
        		if(info.direction == "next" && !addNewEmplGeneralInfo.validate()){
        			return false;
        		}
        		if(info.direction == "previous"){
        			addNewEmplGeneralInfo.hideValidate();
        		}
        	}else if(info.step == 3 && info.direction == "previous"){
        		permanentResInfo.hideValidate();
        		currResInfo.hideValidate();
        	}
	    }).on('finished', function(e) {
	    	if(!permanentResInfo.validate() || !currResInfo.validate()){
	    		return false;
	    	}
	    	bootbox.dialog(uiLabelMap.AddNewEmplToSalesRecruitmentWarning,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							addEmplToRecruitment();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
	    }).on('stepclick', function(e){
	    	
	    });
	};
	
	var addEmplToRecruitment = function(){
		var recruitmentInfoData = recruitmentSalesAddEmplRecInfo.getData();
		var emplInfoData = addNewEmplGeneralInfo.getData();
		var permanentResInfoData = permanentResInfo.getData();
    	var currResInfoData = currResInfo.getData();
    	var phoneInfoData = phoneNumberContactObj.getData();
    	var emailInfoData = emailContactObj.getData();
    	var dataSubmit = $.extend({}, recruitmentInfoData, emplInfoData, phoneInfoData, emailInfoData);
    	dataSubmit.permanentRes = JSON.stringify(permanentResInfoData);
    	dataSubmit.currRes = JSON.stringify(currResInfoData);
    	$("#loadingAddNewEmpl").show();
    	$("#btnNext").attr("disabled", "disabled");
    	$("#btnPrev").attr("disabled", "disabled");
    	$.ajax({
    		url: 'createRecruitmentSalesEmpl',
    		type: 'POST',
    		data: dataSubmit,
    		success: function(response){
    			if(response.responseMessage == 'success'){
    				$("#containerNtf").empty();
    				$("#jqxNotificationNtf").jqxNotification('closeLast');
					$("#jqxNotificationNtf").jqxNotification({ autoClose: true, template : 'info', appendContainer : "#containerNtf", opacity : 0.9});
					$("#notificationContentNtf").text(response.successMessage);
					$("#jqxNotificationNtf").jqxNotification('open');
					$("#jqxgrid").jqxGrid('updatebounddata');
    				$("#RecruitmentSaleAddEmplWindow").jqxWindow('close');
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
    			$("#loadingAddNewEmpl").hide();
    			$("#btnNext").removeAttr("disabled");
    	    	$("#btnPrev").removeAttr("disabled");
    		}
    	});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#RecruitmentSaleAddEmplWindow"), 800, 490);
		$("#RecruitmentSaleAddEmplWindow").on('open', function(event){
			recruitmentSalesAddEmplRecInfo.windowOpenInit();
		});
		$("#RecruitmentSaleAddEmplWindow").on('close', function(event){
			recruitmentSalesAddEmplRecInfo.resetData();
			addNewEmplGeneralInfo.resetData();
			currResInfo.resetData();
			permanentResInfo.resetData();
			emailContactObj.resetData();
			phoneNumberContactObj.resetData();
			resetStep();
		});
	};
	var resetStep = function(){
		$('#wizardAddEmpl').wizard('previous');
		$('#wizardAddEmpl').wizard('previous');
	};
	var openWindow = function(){
		openJqxWindow($("#RecruitmentSaleAddEmplWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	addNewEmplGeneralInfo.init();//addNewEmplGeneralInfo is defined in AddNewEmployeeProfileInfo.js
	permanentResInfo.init();//permanentResInfo is defined in AddNewEmployeeContactInfo.js
	currResInfo.init();//currResInfo is defined in AddNewEmployeeContactInfo.js
	phoneNumberContactObj.init();
	emailContactObj.init();
	recruitmentSalesAddEmplRecInfo.init();
	recruitmentSalesAddEmplObj.init();
});