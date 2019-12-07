var editJobPositionObj = (function(){
	var _data = {};
	var init = function(){
		initDropDown();
		initInput();
		initWindow();
		initEvent();
		initValidator();
	};
	var initDropDown = function(){
		var config = {dropDownBtnWidth: '97%', treeWidth: 220};
		globalObject.createJqxTreeDropDownBtn($("#newDeptChangePosTree"), $("#newDeptChangePosDropDownBtn"), globalVar.rootPartyArr, "treeChangePos", "treeChildChangePos", config);
		createJqxDropDownListExt($("#newPosTypeChangingPosition"), [], {valueMember: 'emplPositionTypeId', displayMember: 'description', width: '97%', height: 25});
		createJqxDropDownListExt($("#oldDeptChangingPosition"), [], {valueMember: 'partyId', displayMember: 'groupName', width: '97%', height: 25, disabled: true});
		createJqxDropDownListExt($("#oldPosTypeChangePos"), [], {valueMember: 'emplPositionTypeId', displayMember: 'description', width: '97%', height: 25, disabled: true});
	};
	var initInput = function(){
		$("#oldFromDateChangingPosition").jqxDateTimeInput({width: '97%', height: 25, disabled: true, value: null});
		$("#oldThruDateChangingPosition").jqxDateTimeInput({width: '97%', height: 25, disabled: true, value: null});
		$("#newFromDateChangingPosition").jqxDateTimeInput({width: '97%', height: 25});
		$("#newThruDateChangingPosition").jqxDateTimeInput({width: '97%', height: 25, showFooter: true});
	};
	var initWindow = function(){
		createJqxWindow($("#changingJobPositionWindow"), 750, 410);
	};
	var initEvent = function(){
		$("#newDeptChangePosTree").on('select', function(event){
			var id = event.args.element.id;
		  	var item = $("#newDeptChangePosTree").jqxTree('getItem', args.element);
			setDropdownContent(item, $(this), $("#newDeptChangePosDropDownBtn"));
			var value = $("#newDeptChangePosTree").jqxTree('getItem', $("#" + id)[0]).value;
			getEmplPositionTypeInOrg(value);
		});
		$("#changingJobPositionWindow").on('open', function(){
			initOpen();
		});
		$("#changingJobPositionWindow").on('close', function(){
			Grid.clearForm($(this));
			$("#newDeptChangePosTree").jqxTree('selectItem', null);
			$("#changeJobPositionEmployeeId").html("");
			$("#changeJobPositionGender").html("");
			$("#changeJobPositionEmployeeName").html("");
			$("#changeJobPositionBirthDate").html("");
			_data = {};
			updateSourceDropdownlist($("#oldPosTypeChangePos"), []);
			updateSourceDropdownlist($("#newPosTypeChangingPosition"), []);
		});
		$("#cancelChangingPosition").click(function(event){
			$("#changingJobPositionWindow").jqxWindow('close');
		});
		$("#saveChangingPosition").click(function(event){
			var valid = $("#changingJobPositionWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.ChangePositionAndDeptConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
		    		    "class" : "btn-primary btn-small icon-ok open-sans",
		    		    "callback": function() {
		    		 		changePositionAndDept();   	
		    		    }	
					},
					{
		    		    "label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
		    		}]		
				);
		});
	};
	
	var getEmplPositionTypeInOrg = function(partyGroupId){
		$("#emplPositionTypeId" + globalVar.defaultSuffix).jqxDropDownList({disabled: true});
		$.ajax({
			url: 'getListEmplPositionTypeByParty',
			data: {partyId: partyGroupId},
			type: 'POST',
			success: function(response){
				if(response.responseMessage == 'success'){
					updateSourceDropdownlist($("#newPosTypeChangingPosition"), response.listReturn);
				}
			},
			complete: function(jqXHR, status){
				$("#newPosTypeChangingPosition").jqxDropDownList({disabled: false});
			}
		});
	};
	var openWindow = function(data){
		_data = data;
		openJqxWindow($("#changingJobPositionWindow"));
	};
	var initOpen = function(){
		Loading.show('loadingMacro');
		$("#changeJobPositionEmployeeId").html(_data.partyCode);
		$("#changeJobPositionEmployeeName").html(_data.fullName);
		$("#newThruDateChangingPosition").val(null);
		var genderDes = "_________";
		for(var i = 0; i < genderArr.length; i++){
			if(genderArr[i].genderId == _data.gender){
				genderDes = genderArr[i].description;
				break;
			}
		}
		var birthDate = "_________";
		if(_data.birthDate){
			birthDate = getDate(_data.birthDate) + "/" + getMonth(_data.birthDate) + "/" + _data.birthDate.getFullYear();
		}
		
		$("#changeJobPositionGender").html(genderDes);
		$("#changeJobPositionBirthDate").html(birthDate);
		$.ajax({
			url: 'getPartyPositionAndDeptLastest',
			data: {partyId: _data.partyId},
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
					return;
				}
				var results = response.results;
				setData(results);
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var initValidator = function(){
		$("#changingJobPositionWindow").jqxValidator({
			scroll: false,
			position: 'bottom',
			rules: [
			     {input: '#newDeptChangePosDropDownBtn', message: uiLabelMap.FieldRequired, action: 'blur',
		        	rule: function (input, commit){
		        		var item = $("#newDeptChangePosTree").jqxTree('getSelectedItem');
		        		if(!item){
		        			return false;
		        		}
		        		return true;
		        	}
		        },
		        {input: '#newPosTypeChangingPosition', message: uiLabelMap.FieldRequired, action: 'blur',
		        	rule: function (input, commit){
		        		if(!input.val()){
		        			return false;
		        		}
		        		return true;
		        	}
		        },
		        {input: '#newFromDateChangingPosition', message: uiLabelMap.FieldRequired, action: 'blur',
		        	rule: function (input, commit){
		        		if(!input.val()){
		        			return false;
		        		}
		        		return true;
		        	}
		        },
		        {input: '#newFromDateChangingPosition', message: uiLabelMap.NewPositionFromDateGreaterThanOldPositionFromDate, action: 'blur',
		        	rule: function (input, commit){
		        		var oldThruDate = $("#oldThruDateChangingPosition").jqxDateTimeInput('val', 'date');
		        		if(!oldThruDate){
		        			var oldFromDate = $("#oldFromDateChangingPosition").jqxDateTimeInput('val', 'date');
		        			var newFromDate = $(input).jqxDateTimeInput('val', 'date');
		        			var oldPosType = $("#oldPosTypeChangePos").val();
		        			if(oldFromDate > newFromDate){
		        				return false;
		        			}
		        			if(oldPosType != "_NA_" && (oldFromDate.getTime() - newFromDate.getTime()) == 0){
		        				return false;
		        			}
		        		}
		        		return true;
		        	}
		        },
		        {input: '#newFromDateChangingPosition', message: uiLabelMap.OnlyAllowValueAfterOldPositionThruDateOneDay, action: 'blur',
		        	rule: function (input, commit){
		        		var oldThruDate = $("#oldThruDateChangingPosition").jqxDateTimeInput('val', 'date');
		        		if(oldThruDate){
		        			var newFromDate = $(input).jqxDateTimeInput('val', 'date');
		        			oldThruDate.setDate(oldThruDate.getDate() + 1);
		        			oldThruDate.setHours(0,0,0,0);
		        			newFromDate.setHours(0,0,0,0);
		        			if(oldThruDate.getTime() != newFromDate.getTime()){
		        				return false;
		        			}
		        		}
		        		return true;
		        	}
		        },
		        {input: '#newThruDateChangingPosition', message: uiLabelMap.GTDateFieldRequired, action: 'blur',
		        	rule: function (input, commit){
		        		var newThruDate = $(input).jqxDateTimeInput('val', 'date');
		        		if(newThruDate){
		        			var newFromDate = $("#newFromDateChangingPosition").jqxDateTimeInput('val', 'date');
		        			if(newThruDate < newFromDate){
		        				return false;
		        			}
		        		}
		        		return true;
		        	}
		        },
		        {input: '#newPosTypeChangingPosition', message: uiLabelMap.NewPositionTypeMustBeDiffOldPositionType, action: 'blur',
		        	rule: function (input, commit){
		                var oldDept = $("#oldDeptChangingPosition").val();
		                var newDept = $("#newDeptChangePosTree").jqxTree('getSelectedItem').value;
		                if(oldDept !== newDept){
		                    return true;
                        }
		        		var oldPositionType = $("#oldPosTypeChangePos").val();
		        		var newPositionType = $(input).val();
		        		var thruDateOld = $("#oldThruDateChangingPosition").jqxDateTimeInput('val', 'date');
		        		if(oldPositionType === newPositionType && !thruDateOld){
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
		var selectItemPartyIdFrom = $("#newDeptChangePosTree").jqxTree('getSelectedItem');
		data.partyId = _data.partyId;
		data.partyIdFrom = selectItemPartyIdFrom.value;
		data.emplPositionTypeId = $("#newPosTypeChangingPosition").val();
		data.fromDate = $("#newFromDateChangingPosition").jqxDateTimeInput('val', 'date').getTime();
		var thruDate = $("#newThruDateChangingPosition").jqxDateTimeInput('val', 'date');
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		return data;
	};
	
	var changePositionAndDept = function(){
		Loading.show('loadingMacro');
		var data = getData();
		$.ajax({
			url: 'changingEmplPositionAndPartyRel',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "error"){
					bootbox.dialog(response.errorMessage,
							[{
								"label" : uiLabelMap.CommonClose,
								"class" : "btn-danger btn-small icon-remove open-sans",
							}]		
					);
					return;
				}
				Grid.renderMessage('jqxgrid', response.successMessage, {template : 'success', appendContainer : '#containerjqxgrid'});
				$("#changingJobPositionWindow").jqxWindow('close');
				$("#jqxgrid").jqxGrid('updatebounddata');
			},
			complete: function(){
				Loading.hide('loadingMacro');
			}
		});
	};
	var setData = function(data){
		var date = new Date();
		var startNextMonth = new Date(date.getFullYear(), date.getMonth() + 1, 1);
		updateSourceDropdownlist($("#oldDeptChangingPosition"), [{partyId: data.partyIdFrom, groupName: data.groupName}]);
		$("#oldDeptChangingPosition").jqxDropDownList({selectedIndex: 0});
		var emplPositionTypeArr = [];
		if(data.emplPositionTypeId){
			emplPositionTypeArr = [{emplPositionTypeId: data.emplPositionTypeId, description: data.description}];
		}else{
			emplPositionTypeArr = [{emplPositionTypeId: "_NA_", description: uiLabelMap.NotSetting}];
		}
		updateSourceDropdownlist($("#oldPosTypeChangePos"), emplPositionTypeArr);
		$("#oldPosTypeChangePos").jqxDropDownList({selectedIndex: 0});
		var fromDate = new Date(data.fromDate);
		$("#oldFromDateChangingPosition").val(fromDate);
		if(data.thruDate){
			var thruDate = new Date(data.thruDate);
			$("#oldThruDateChangingPosition").val(thruDate);
			$("#newFromDateChangingPosition").val(new Date(thruDate.getFullYear(), thruDate.getMonth(), thruDate.getDate() + 1));
		}else{
			$("#oldThruDateChangingPosition").val(null);
			var tempDate = new Date(fromDate.getFullYear(), fromDate.getMonth() + 1, 1);
			if(startNextMonth > tempDate){
				$("#newFromDateChangingPosition").val(startNextMonth);
			}else{
				$("#newFromDateChangingPosition").val(tempDate);
			}
		}
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());
$(document).ready(function () {
	editJobPositionObj.init();
});
