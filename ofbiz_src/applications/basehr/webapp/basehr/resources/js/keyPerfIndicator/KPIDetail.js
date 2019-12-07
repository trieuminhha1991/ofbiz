var editKPIObj_Detail = (function(){
	var _editMode = false;
	var _rowData = {};
	var init = function(){
		initJqxDropDownList();
		initJqxNumberInput();
		initJqxInput();
		initJqxValidator();
		initBtnEvent();
		initJqxWindow();
		initJqxPanel();
	};
	var initJqxInput = function(){
		$("#CriteriaName_Detail").jqxInput({width: '95%', height: 20});
		$("#CriteriaId_Detail").jqxInput({width: '95%', height: 20, disabled: true});
	};
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxEditor();
		}
		createJqxWindow($("#DetailWindow"), 550, 520, initContent);
		$("#DetailWindow").on('close', function(event){
			Grid.clearForm($(this));
			//_editMode = false;
			_rowData = {};
			editCriteriaTypeObj.setPerfCriteriaTypeIdSelected(null);
		});
		$("#DetailWindow").on('open', function(event){
			$(this).jqxWindow('setTitle', uiLabelMap.EditKPI);
			_editMode = true;
			setData(_rowData);
		});
		
	};
	var initJqxPanel = function(){
		$("#contentPanel_Detail").jqxPanel({width: '99.5%', height: 400, scrollBarSize: 15});
	};
	
	var setData = function(data){
		$("#CriteriaType_Detail").val(data.perfCriteriaTypeId);
		$("#CriteriaId_Detail").val(data.criteriaId);
		$("#CriteriaName_Detail").val(data.criteriaName);
		$("#descriptionKPI_Detail").val(data.description);
		$("#periodTypeNew_Detail").val(data.periodTypeId);
		$("#targetNumberNew_Detail").val(data.target);
		$("#uomIdNew_Detail").val(data.uomId);
		$("#perfCriDevelopmetTypeNew_Detail").val(data.perfCriDevelopmetTypeId);
	};
	
	var initBtnEvent = function(){
		$("#viewListCriteriaTypeBtn_Detail").click(function(event){
			editCriteriaTypeObj.openWindow();
		});
		$("#alterSave").click(function(event){
			var valid = $("#DetailWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			if(_editMode){
				updateKPI();
			}else{
				bootbox.dialog(uiLabelMap.CreateKPIConfirm,
						[{
							"label" : uiLabelMap.CommonSubmit,
							"class" : "btn-primary btn-small icon-ok open-sans",
							"callback": function() {
								createKPI();
							}	
						},
						{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
				);
			}
		});
		$("#alterCancel").click(function(event){
			$("#DetailWindow").jqxWindow('close');
		});
	};
	
	var getData = function(){
		var data = {};
		data.criteriaName = $("#CriteriaName_Detail").val();
		data.criteriaId = $("#CriteriaId_Detail").val();
		data.perfCriteriaTypeId = $("#CriteriaType_Detail").val();
		data.description = $("#descriptionKPI_Detail").val();
		data.periodTypeId = $("#periodTypeNew_Detail").val();
		data.target = $("#targetNumberNew_Detail").val();
		data.uomId = $("#uomIdNew_Detail").val();
		data.perfCriDevelopmetTypeId = $("#perfCriDevelopmetTypeNew_Detail").val();
		return data;
	}
	
	var updateKPI = function(){
		var dataUpdate = getData();
		dataUpdate.criteriaId = _rowData.criteriaId;
		$("#jqxgrid").jqxGrid('updaterow', _rowData.uid, dataUpdate);
		$("#DetailWindow").jqxWindow('close');
	};
	
	var createKPI = function(){
		$("#alterSave").attr("disabled", "disabled");
		$("#alterCancel").attr("disabled", "disabled");
		var row = getData();
		$("#jqxgrid").jqxGrid('addrow', null, row, 'first');
		$("#DetailWindow").jqxWindow('close');
		$("#alterSave").removeAttr("disabled");
		$("#alterCancel").removeAttr("disabled")
	};
	
	var initJqxEditor = function(){
		$("#descriptionKPI_Detail").jqxEditor({ 
    		width: '97%',
            theme: 'olbiuseditor',
            tools: '',
            height: 100,
        });
	};
	
	var initJqxNumberInput = function(){
		$("#targetNumberNew_Detail").jqxNumberInput({ width: '97%', height: '25px',  spinButtons: true, max: 999999999999, digits: 11});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownListBinding($("#uomIdNew_Detail"), [{name: "uomId"}, {name: "description"}, {name: "abbreviation"}], 'getKPIUomList', 
				"listReturn", "uomId", "abbreviation", '100%', 25);
		createJqxDropDownList(globalVar.periodTypeArr, $("#periodTypeNew_Detail"), "periodTypeId", "description", 25, '97%');
		createJqxDropDownList(globalVar.perfCriDevelopmentTypeArr, $("#perfCriDevelopmetTypeNew_Detail"), "perfCriDevelopmetTypeId", "perfCriDevelopmetName", 25, '97%');
		createJqxDropDownListBinding($("#CriteriaType_Detail"), [{name: 'perfCriteriaTypeId'}, {name: 'description'}], 
				"getPerfCriteriaType", "listReturn", "perfCriteriaTypeId", "description", "100%", 25);
		$("#CriteriaType_Detail").on('bindingComplete', function(event){
			if(editCriteriaTypeObj.getPerfCriteriaTypeIdSelected()){
				$("#CriteriaType_Detail").val(editCriteriaTypeObj.getPerfCriteriaTypeIdSelected())
			}
		});
	};
	var initJqxValidator = function(){
		$("#DetailWindow").jqxValidator({
			rules : [
					{input : '#CriteriaName_Detail', message : uiLabelMap.FieldRequired, action : 'blur',
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
                    {input : '#CriteriaId_Detail', message : uiLabelMap.FieldRequired, action : 'blur',
                        rule : function(input, commit){
                            if(!input.val()){
                                return false;
                            }
                            return true;
                        }
                    },
					{input : '#CriteriaType_Detail', message : uiLabelMap.FieldRequired, action: 'blur', 
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{input : '#periodTypeNew_Detail', message : uiLabelMap.FieldRequired, action: 'blur', 
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{input : '#periodTypeNew_Detail', message : uiLabelMap.FieldRequired, action: 'blur', 
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{input : '#addNewUomId_Detail', message : uiLabelMap.FieldRequired, action: 'blur', 
						rule : function(input, commit){
							if(!$("#uomIdNew_Detail").val()){
								return false;
							}
							return true;
						}
					},
					{input : '#perfCriDevelopmetTypeNew_Detail', message : uiLabelMap.FieldRequired, action: 'blur', 
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
	         ]
		});
	};
	
	var updateCriteriaTypeDropDownList = function(){
		updateJqxDropDownListBinding($("#CriteriaType_Detail"), 'getPerfCriteriaType');
	};
	
	var set_rowData = function(_rowdata_tmp){
		_rowData = _rowdata_tmp;
	};
	
	return {
		init : init,
		updateCriteriaTypeDropDownList: updateCriteriaTypeDropDownList,
		set_rowData : set_rowData
	}
}());

$(document).ready(function(){
	editKPIObj_Detail.init();
});