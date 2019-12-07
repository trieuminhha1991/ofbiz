var recruitReqCriteriaObj = (function(){
	var _seqCriteriaId = 0;
	var _criteriaCondObj = {};
	var _defaultdata = {};
	var init = function(){
		_criteriaCondObj[_seqCriteriaId] = 1;
		initCondition(0, 0);
		initEvent();
	};
	
	var initEvent = function(){
		$("#addNewCriteria").click(function(event){
			_seqCriteriaId++;
			_criteriaCondObj[_seqCriteriaId] = 0;
			createNewRecruitCriteria(_seqCriteriaId);
		});
	};
	
	var createNewRecruitCriteria = function(seqCriteriaId){
		var condSeq = _criteriaCondObj[seqCriteriaId];
		var newRecruitmentCriteria = $("<div class='form-legend marginBottom20' id='recruitmentCriteria_" + seqCriteriaId + "'>");
		var newRecruitmentCriteriaHeader = createRecruitmentCriteriaHeader(seqCriteriaId);
		var newRecruitmentCriteriaCond = createRecruitmentCriteriaCond(seqCriteriaId, condSeq);
		var containDiv = $("<div class='contain'></div>");
		containDiv.append(newRecruitmentCriteriaCond);
		newRecruitmentCriteria.append(newRecruitmentCriteriaHeader);
		newRecruitmentCriteria.append(containDiv);
		newRecruitmentCriteria.appendTo($("#criteriaContainer"));
		initCondition(seqCriteriaId, condSeq, false);
		_criteriaCondObj[seqCriteriaId] = condSeq + 1;
	};
	
	var createRecruitmentCriteriaHeader = function(seqCriteriaId){
		var containLegend = $("<div class='contain-legend'></div>");
		var contentLegend = $("<span class='content-legend text-normal'>" + uiLabelMap.RecruitmentCriteria + "  </span>");
		var createAnchorLink = $("<a href='javascript:recruitReqCriteriaObj.deleteCriteria(" + seqCriteriaId + ")'><i class='fa-times-circle open-sans open-sans-index' title='" + uiLabelMap.removeRecruitmentCriteria + "'></i></a>");
		contentLegend.append(createAnchorLink);
		containLegend.append(contentLegend);
		return containLegend;
	};
	
	var createRecruitmentCriteriaCond = function(seqCriteriaId, condSeq){
		var rowFluidDiv = $("<div class='row-fluid' style='margin-top: 5px'></div>");
		var conditionContainerDiv = $("<div class='span12' id='conditionContainer_" + seqCriteriaId + "_" + condSeq + "'></div>");
		var rowFluid2 = $('<div class="row-fluid"></div>');
		var rowFluidSpan12  = $('<div class="span12"></div>');
		var conditionContentDiv1 = $('<div class="span1"></div>');
		if(getNbrCondition(seqCriteriaId) > 0){
			conditionContentDiv1.append('<div id="recLogicInputCond_' + seqCriteriaId + '_' + condSeq + '"></div>');
		}
		var conditionContentDiv2 = $('<div class="span4"><div id="recInputParam_' + seqCriteriaId + '_' + condSeq + '"></div></div>');
		var conditionContentDiv3 = $('<div class="span2"><div id="recInputCond_' + seqCriteriaId + '_' + condSeq + '"></div></div>');
		var conditionContentDiv4 = $('<div class="span4"><input type="text" id="recInputCondValue_' + seqCriteriaId + '_' + condSeq + '"></div>');
		var rowFluidSpan12Span1 = $('<div class="span1" style="margin: 0"></div>');
		var rowFluidS12S1RF = $('<div class="row-fluid"></div>');
		rowFluidSpan12Span1.append(rowFluidS12S1RF);
		var conditionContentDiv5 = $('<div class="span12"></div>');
		rowFluidS12S1RF.append(conditionContentDiv5);
		var addNewCondLink = $('<div class="span6"><a href="javascript:recruitReqCriteriaObj.addNewCondition(' + seqCriteriaId + ', ' + condSeq + ')" ' 
				+ 'class="grid-action-button marginOnlyLeft10"  title=" ' + uiLabelMap.HRAddCondition + '"><i class="icon-only fa-plus-circle"></i></a></div>');
		var deleteCondLink = $('<div class="span6"><a href="javascript:recruitReqCriteriaObj.deleteCondition(' + seqCriteriaId + ', ' + condSeq + ')" ' 
				+ ' class="grid-action-button marginOnlyLeft10" title="' + uiLabelMap.HRRemoveCondition + '"><i class="icon-only icon-trash"></i></a></div>');
		conditionContentDiv5.append(addNewCondLink);
		conditionContentDiv5.append(deleteCondLink);
		rowFluidSpan12.append(conditionContentDiv1);
		rowFluidSpan12.append(conditionContentDiv2);
		rowFluidSpan12.append(conditionContentDiv3);
		rowFluidSpan12.append(conditionContentDiv4);
		rowFluidSpan12.append(rowFluidSpan12Span1);
		rowFluid2.append(rowFluidSpan12);
		conditionContainerDiv.append(rowFluid2);
		rowFluidDiv.append(conditionContainerDiv);
		return rowFluidDiv;
	};
	
	var deleteCriteria = function(seqCriteriaId){
		delete _criteriaCondObj[seqCriteriaId];
		$("#recruitmentCriteria_" + seqCriteriaId).remove();
	};
	
	var initCondition = function(seqCriteriaId, condSequenceId, isCreateLoginOp){
		createJqxDropDownList(globalVar.recInputParamArr, $("#recInputParam_" + seqCriteriaId + "_" + condSequenceId), "enumId", "description", 25, "100%");
		createJqxDropDownList(globalVar.recOpCondArr, $("#recInputCond_" + seqCriteriaId + "_" + condSequenceId), "enumId", "description", 25, "100%");
		$("#recInputCondValue_" + seqCriteriaId + "_" + condSequenceId).jqxInput({ width: '97%', height: '20px'});
		initConditionEvent(seqCriteriaId, condSequenceId);
		if(isCreateLoginOp){
			createJqxDropDownList(globalVar.recLogicCondArr, $("#recLogicInputCond_" + seqCriteriaId + "_" + condSequenceId), 
					"enumId", "description", 25, "125%");
			$("#recLogicInputCond_" + seqCriteriaId + "_" + condSequenceId).jqxDropDownList({selectedIndex: 0});
		}
	};
	
	var initConditionEvent = function(seqCriteriaId, condSequenceId){
		$("#recInputParam_" + seqCriteriaId + "_" + condSequenceId).on('select', function(event){
			 var args = event.args;
			 if(args){
				 var item = args.item;
				 var value = item.value;
				 var elementType = getTypeOfElement(value);
				 setTypeInputForCondValue(elementType, seqCriteriaId, condSequenceId);
				 if(elementType == 'dropdownlist'){
					 updateCondValueInputSource(value, seqCriteriaId, condSequenceId);
				 }
			 }
		});
	};
	
	var setTypeInputForCondValue = function(elementType, seqCriteriaId, condSequenceId){
		var inputCondValue = $("#recInputCondValue_" + seqCriteriaId + "_" + condSequenceId);
		if(elementType == 'dropdownlist'){
			var parentInputCondValue = inputCondValue.parent();
			inputCondValue.remove();
			inputCondValue = $("<div id='recInputCondValue_" + seqCriteriaId + "_" + condSequenceId + "'></div>");
			parentInputCondValue.append(inputCondValue);
			createJqxDropDownList([], inputCondValue, "condValue", "description", 25, "100%");
		}else if(elementType == 'numberinput'){
			var parentInputCondValue = inputCondValue.parent();
			inputCondValue.remove();
			inputCondValue = $("<div id='recInputCondValue_" + seqCriteriaId + "_" + condSequenceId + "'></div>");
			parentInputCondValue.append(inputCondValue);
			inputCondValue.jqxNumberInput({ width: '100%', height: '25px',  spinButtons: true, inputMode: 'simple', decimalDigits: 0});
			if(_defaultdata.hasOwnProperty(seqCriteriaId + "_" + condSequenceId)){
				inputCondValue.val(_defaultdata[seqCriteriaId + "_" + condSequenceId]);
			}
		}else if(elementType == 'text'){
			if(!inputCondValue.is("input")){
				var parentInputCondValue = inputCondValue.parent();
				inputCondValue.remove();
				inputCondValue = $("<input type='text' id='recInputCondValue_" + seqCriteriaId +"_" + condSequenceId + "'>");
				parentInputCondValue.append(inputCondValue);
			}
			inputCondValue.jqxInput({width: '97%', height: 20});
			if(_defaultdata.hasOwnProperty(seqCriteriaId + "_" + condSequenceId)){
				inputCondValue.val(_defaultdata[seqCriteriaId + "_" + condSequenceId]);
			}
		}
	};
	
	var updateCondValueInputSource = function(enumId, seqCriteriaId, condSequenceId){
		var inputEle = $("#recInputCondValue_" + seqCriteriaId + "_" + condSequenceId);
		if(enumId == 'RECIP_MAJOR'){
			inputEle.jqxDropDownList({valueMember: 'majorId', displayMember: 'description'});
			if(!globalVar.hasOwnProperty("majorArr")){
				inputEle.jqxDropDownList({disabled: true});
				$.ajax({
					url: 'getListMajor',
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							globalVar.majorArr = response.listReturn;
							updateSourceDropdownlist(inputEle, globalVar.majorArr);
						}
					},
					complete: function(jqXHR, textStatus){
						inputEle.jqxDropDownList({disabled: false});
						if(_defaultdata.hasOwnProperty(seqCriteriaId + "_" + condSequenceId)){
							inputEle.val(_defaultdata[seqCriteriaId + "_" + condSequenceId]);
						}
					}
				});
			}else{
				updateSourceDropdownlist(inputEle, globalVar.majorArr);
			}
		}else if(enumId == 'RECIP_GENDER'){
			inputEle.jqxDropDownList({valueMember: 'genderId', displayMember: 'description'});
			updateSourceDropdownlist(inputEle, globalVar.genderArr);
		}else if(enumId == 'RECIP_SCHOOL'){
			inputEle.jqxDropDownList({valueMember: 'schoolId', displayMember: 'schoolName'});
			if(!globalVar.hasOwnProperty("educationSchoolArr")){
				inputEle.jqxDropDownList({disabled: true});
				$.ajax({
					url: 'getListEducationSchool',
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							globalVar.educationSchoolArr = response.listReturn;
							updateSourceDropdownlist(inputEle, globalVar.educationSchoolArr);
						}
					},
					complete: function(jqXHR, textStatus){
						inputEle.jqxDropDownList({disabled: false});
						if(_defaultdata.hasOwnProperty(seqCriteriaId + "_" + condSequenceId)){
							inputEle.val(_defaultdata[seqCriteriaId + "_" + condSequenceId]);
						}
					}
				});
			}else{
				updateSourceDropdownlist(inputEle, globalVar.educationSchoolArr);
			}
		}else if(enumId == 'RECIP_CLASS'){
			inputEle.jqxDropDownList({valueMember: 'classificationTypeId', displayMember: 'description'});
			updateSourceDropdownlist(inputEle, globalVar.degreeClassTypeArr);
		}else if(enumId == 'RECIP_DEGREE'){
			inputEle.jqxDropDownList({valueMember: 'educationSystemTypeId', displayMember: 'description'});
			updateSourceDropdownlist(inputEle, globalVar.educationSystemTypeArr);
		}
		if(_defaultdata.hasOwnProperty(seqCriteriaId + "_" + condSequenceId)){
			inputEle.val(_defaultdata[seqCriteriaId + "_" + condSequenceId]);
		}
	};
	
	var getTypeOfElement = function(enumId){
		var elementType = '';
		if('RECIP_MAJOR' == enumId){
			 elementType = 'dropdownlist';
		 }else if('RECIP_GENDER' == enumId){
			 elementType = 'dropdownlist';
		 }else if('RECIP_AGE' == enumId){
			 elementType = 'numberinput';
		 }else if('RECIP_SCHOOL' == enumId){
			 elementType = 'dropdownlist';
		 }else if('RECIP_EXPERIENCE' == enumId){
			 elementType = 'numberinput';
		 }else if('RECIP_DEGREE' == enumId){
			 elementType = 'dropdownlist';
		 }else if('RECIP_CLASS' == enumId){
			 elementType = 'dropdownlist';
		 }else if('RECIP_SAL_FROM' == enumId){
			 elementType = 'numberinput';
		 }else if('RECIP_SAL_TO' == enumId){
			 elementType = 'numberinput';
		 }else if('RECIP_OTHER' == enumId){
			 elementType = 'text';
		 }else{
			 elementType = 'text';
		 }
		return elementType;
	};
	
	var addNewCondition = function(seqCriteriaId, condSeq){
		var newCondSeq = _criteriaCondObj[seqCriteriaId];
		var newCondEle = createRecruitmentCriteriaCond(seqCriteriaId, newCondSeq);
		var containDiv = $("#conditionContainer_" + seqCriteriaId + "_" + condSeq).parents(".contain");
		var isCreateLogicCond = false;
		if(getNbrCondition(seqCriteriaId) > 0){
			isCreateLogicCond = true;
		}
		containDiv.append(newCondEle);
		initCondition(seqCriteriaId, newCondSeq, isCreateLogicCond);
		_criteriaCondObj[seqCriteriaId] = newCondSeq + 1;
	};
	
	var deleteCondition = function(seqCriteriaId, condSeq){
		$("#conditionContainer_" + seqCriteriaId + "_" + condSeq).parent().remove();
		var nbrCond = getNbrCondition(seqCriteriaId); 
		if(nbrCond == 0){
			deleteCriteria(seqCriteriaId);
		}else if(nbrCond <= 1){
			$("[id^='recLogicInputCond_" + seqCriteriaId + "']").remove();	
		}
	};
	
	var getNbrCondition = function(seqCriteriaId){
		return $("[id^='conditionContainer_" + seqCriteriaId + "']").length;
	};
	
	var setData = function(data){
		var keys = Object.keys(data);
		for(var i = 0; i < keys.length; i++){
			if($("#recruitmentCriteria_" + i).length == 0){
				if(!_criteriaCondObj.hasOwnProperty(i)){
					_criteriaCondObj[i] = 0;
				}
				createNewRecruitCriteria(i);
			}
			var listConds = data[keys[i]];
			for(var j = 0; j < listConds.length; j++){
				var condData = listConds[j];
				if($("#conditionContainer_" + i + "_" + j).length == 0){
					var newCondDiv = createRecruitmentCriteriaCond(i, j);
					var containDiv = $("#recruitmentCriteria_" + i + " > .contain");
					containDiv.append(newCondDiv);
					var isCreateLogicCond = false;
					if(getNbrCondition(i) > 0){
						isCreateLogicCond = true;
					}
					initCondition(i, j, isCreateLogicCond);
					_criteriaCondObj[i] = j+1;
				}
				var inputParamEnumId = condData.inputParamEnumId;
				if(condData.hasOwnProperty("condValue") && condData.condValue != null){
					_defaultdata[i + "_" + j] = condData.condValue;
				}else if(condData.hasOwnProperty("otherValue") && condData.otherValue != null){
					_defaultdata[i + "_" + j] = condData.otherValue;
				}
				
				$("#recInputParam_" + i + "_" + j).val(inputParamEnumId);
				
				if(condData.hasOwnProperty("operatorEnumId")){
					$("#recInputCond_" + i + "_" + j).val(condData.operatorEnumId);
				}
				if(condData.logicalEnumId){
					$("#recLogicInputCond_" + i + "_" + j).val(condData.logicalEnumId);
				}
			}
		}
	};
	
	var getData = function(){
		var retData = [];
		for(var key in _criteriaCondObj){
			var nbrCond = _criteriaCondObj[key];
			var recruitReqCond = [];
			for(var i = 0; i < nbrCond; i++){
				if($("#conditionContainer_" + key + "_" + i).length > 0){
					var tempData = {};
					var operatorEnumId = $("#recInputCond_" + key + "_" + i).val();
					var inputParamEnumId = $("#recInputParam_" + key + "_" + i).val();
					var condValue = $("#recInputCondValue_" + key + "_" + i).val();
					if($("#recLogicInputCond_" + key + "_" + i).length > 0){
						var logicalEnumId = $("#recLogicInputCond_" + key + "_" + i).val();
						tempData.logicalEnumId = logicalEnumId;
					}
					if(typeof(operatorEnumId) != 'undefined' && operatorEnumId.length > 0){
						tempData.operatorEnumId = operatorEnumId;
					}
					if(typeof(inputParamEnumId) != 'undefined' && inputParamEnumId.length > 0){
						tempData.inputParamEnumId = inputParamEnumId;
					}
					if(condValue){
						tempData.condValue = condValue;
					}
					recruitReqCond.push(tempData);
				}
			}
			retData.push(recruitReqCond);
		}
		return {recruitReqCond: JSON.stringify(retData)};
	};
	
	var resetData = function(){
		$("#criteriaContainer").empty();
		_criteriaCondObj = {};
		_seqCriteriaId = 0;
		_criteriaCondObj[_seqCriteriaId] = 0;
		_defaultdata = {};
		_data = {};
		createNewRecruitCriteria(_seqCriteriaId);
	};
	
	return{
		init: init,
		deleteCriteria: deleteCriteria,
		addNewCondition: addNewCondition,
		deleteCondition: deleteCondition,
		getData: getData,
		setData: setData,
		resetData: resetData
	}
}());

$(document).ready(function(){
	recruitReqCriteriaObj.init();
});