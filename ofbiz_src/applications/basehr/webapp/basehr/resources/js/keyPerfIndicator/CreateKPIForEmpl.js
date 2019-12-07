var editEmplKPIObj = (function(){
	var _listKPIData = [];
	var init = function(){
        initJqxGridSearchEmpl();
		//initJqxInput();
		initJqxDropDownList();
        initDropDownGrid();
		initJqxNumberInput();
		initBtnEvent();
		initJqxDateTimeInput();
		initJqxValidator();
		initJqxNotification();
		initjqxWindow();
		create_spinner($("#spinnerAjax" + globalVar.listEmplKPIWindow));
	};
	
	var initJqxNotification = function(){
		$("#jqxNotificationjqxgrid" + globalVar.listEmplKPIWindow).jqxNotification({ 
			width: "100%", appendContainer: "#containerjqxgrid" + globalVar.listEmplKPIWindow, 
			opacity: 0.9, autoClose: true, template: "info" 
		});
	};
	
	/*var initJqxInput = function(){
		$("#partyIdNew").jqxInput({width: '85%', height: 20, 
			disabled: true, valueMember: 'partyId', displayMember: 'partyName'});
	};*/
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.perfCriteriaTypeArr, $("#perfCriteriaTypeNew"), "perfCriteriaTypeId", "description", 25, '98%');
		createJqxDropDownList(globalVar.uomArr, $("#uomIdNew"), "uomId", "abbreviation", 25, '98%');
		createJqxDropDownList(globalVar.periodTypeArr, $("#periodTypeNew"), "periodTypeId", "description", 25, '98%');
		createJqxDropDownList([], $("#criteriaIdNew"), "criteriaId", "criteriaName", 25, '98%');

		$("#perfCriteriaTypeNew").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var value = item.value;
				$("#criteriaIdNew").jqxDropDownList({disabled: true});
				_listKPIData = [];
				$.ajax({
					url: 'getPerfCriteriaByType',
					data: {perfCriteriaTypeId: value},
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							updateSourceDropdownlist($("#criteriaIdNew"), response.listReturn);
							_listKPIData = response.listReturn;
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
						$("#criteriaIdNew").jqxDropDownList({disabled: false});
					}
				});
		    }
		});
		
		$("#criteriaIdNew").on('select', function(event){
			var args = event.args;
			if(args){
				var index = args.index;
				var dataRecord = _listKPIData[index];
				if(dataRecord){
					$("#targetNumberNew").val(dataRecord.target);
					$("#periodTypeNew").val(dataRecord.periodTypeId);
					$("#uomIdNew").val(dataRecord.uomId);
				}
			}
		});
		$("#criteriaIdNew").on('unselect', function(event){
			$("#uomIdNew").jqxDropDownList('clearSelection');
			$("#periodTypeNew").jqxDropDownList('clearSelection');
			$("#targetNumberNew").val(0);
		});
	};
    var initDropDownGrid = function(){
        var datafield = [{name: 'partyId', type: 'string'},
            {name: 'partyCode', type: 'string'},
            {name: 'fullName', type: 'string'},
            {name: 'emplPositionType', type: 'string'}];
        var columns = [{text: uiLabelMap.EmployeeId, datafield : 'partyCode', width : '23%', editable: false},
            {text: uiLabelMap.EmployeeName, datafield: 'fullName', width: '30%', editable: false},
            {text: uiLabelMap.HrCommonPosition, datafield: 'emplPositionType', width: '47%', editable: false}];
        var grid = $("#jqxGridGroupEmpl");
        var rendertoolbar = function (toolbar){
            toolbar.html("");
            var id = "jqxGridGroupEmpl";
            var me = this;
            var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.EmployeeListSelected + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
            toolbar.append(jqxheader);
            var container = $('#toolbarButtonContainer' + id);
            var maincontainer = $("#toolbarcontainer" + id);
            var str = '<button style="margin-left: 20px;" id="deleterowbutton'+id+'"><i class="icon-trash open-sans"></i><span>'+ uiLabelMap.wgdelete + '</span></button>';
            container.append(str);
            var obj = $("#deleterowbutton" + id);
            obj.jqxButton();
            obj.click(function(){
                var selectedrowindexes = grid.jqxGrid('getselectedrowindexes');
                var rowIDs = [];
                for(var i = 0; i < selectedrowindexes.length; i++){
                    var rowid = grid.jqxGrid('getrowid', selectedrowindexes[i]);
                    rowIDs.push(rowid);
                }
                grid.jqxGrid('deleterow', rowIDs);
                var source = $("#jqxGridGroupEmpl").jqxGrid('source');
                var records = source.records;
                source._source.localdata = records;
                emplListKPICommonObj.setContentDropDownBtn(records.length + " " + uiLabelMap.EmployeeSelected);
                grid.jqxGrid('clearselection');
            });
        };
        var config = {
            width: 500,
            rowsheight: 25,
            autoheight: true,
            virtualmode: false,
            showfilterrow: false,
            selectionmode: 'multiplerows',
            pageable: true,
            sortable: false,
            filterable: false,
            editable: false,
            showtoolbar: true,
            rendertoolbar : rendertoolbar,
            source: {pagesize: 5, id: 'partyId', localdata: []}
        };
        Grid.initGrid(config, datafield, columns, null, grid);
        $("#dropDownButtonGroupEmpl").jqxDropDownButton({width: '100%', height: 25});
    };
	
	var initJqxDateTimeInput = function(){
		$("#fromDateNew").jqxDateTimeInput({width: '98%', height: 25});
		$("#thruDateNew").jqxDateTimeInput({width: '98%', height: 25, showFooter: true});
	};
	
	var initJqxValidator = function(){
		$("#addNewEmplKPIListWindow").jqxValidator({
			rules: [
                { input: '#chooseEmplBtn', message: uiLabelMap.NoPartyChoose, action: 'none',
                    rule : function(input, commit){
                        var records = $("#jqxGridGroupEmpl").jqxGrid('source').records;
                        if(records.length <= 0){
                            return false;
                        }
                        return true;
                    }
                },
				{input : '#perfCriteriaTypeNew', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#criteriaIdNew', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#periodTypeNew', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#uomIdNew', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#weightNew', message : uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
					rule : function(input, commit){
						if(input.val() != 'undefined' && input.val() <= 0){
							return false;
						}
						return true;
					}
				},
				{input : '#targetNumberNew', message : uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
					rule : function(input, commit){
						if(input.val() != 'undefined' && input.val() <= 0){
							return false;
						}
						return true;
					}
				},
				{input : '#fromDateNew', message : uiLabelMap.FieldRequired , action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
                {input : '#thruDateNew', message : uiLabelMap.FieldRequired , action : 'blur',
                    rule : function(input, commit){
                        if(!input.val()){
                            return false;
                        }
                        return true;
                    }
                },
				{input : '#thruDateNew', message : uiLabelMap.ExpireDateMustGreaterOrEqualThanEffectiveDate , action : 'blur',
					rule : function(input, commit){
						var thruDate = input.jqxDateTimeInput('val', 'date');
						if(!thruDate){
							return true;
						}
						var fromDate = $("#fromDateNew").jqxDateTimeInput('val', 'date');
						if(thruDate < fromDate){
							return false;
						}
						return true;
					}
				}
			]
		});
	};
	
	var initJqxNumberInput = function(){
		$("#targetNumberNew").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: true, max: 999999999999, digits: 11, min: 0});
		$("#weightNew").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: true, max: 100, digits: 3, min: 0,
			symbolPosition: 'right', symbol: '%', inputMode: 'simple'});
	};
	
	var initBtnEvent = function(){
		/*$("#searchPartyNewBtn").click(function(event){
			emplListKPICommonObj.openWindow();
		});*/
        $("#chooseEmplBtn").click(function(event){
            openJqxWindow($('#popupWindowEmplList'));
        });
		$("#alterSave").click(function(event){
			if(!validate()){
				return false;
			}
			bootbox.dialog(uiLabelMap.AssignKPIConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							addKPIForEmpl(true);
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
		$("#saveAndContinue").click(function(event){
			if(!validate()){
				return false;
			}
			bootbox.dialog(uiLabelMap.AssignKPIConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							addKPIForEmpl(false);
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
		$("#alterCancel").click(function(event){
			$("#addNewEmplKPIListWindow").jqxWindow('close');
		});
	};
	
	var addKPIForEmpl = function(isCloseWindow){
		$("#ajaxLoading" + globalVar.listEmplKPIWindow).show();
		disableAll();
		var data = getData();
		$.ajax({
			url: 'addKPIForEmployee',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					if(isCloseWindow){
						$("#addNewEmplKPIListWindow").jqxWindow('close');
						Grid.renderMessage('jqxgrid' + globalVar.listEmplKPIWindow, response.successMessage, {autoClose: true,
							template : 'info',
							appendContainer : "#containerjqxgrid" + globalVar.listEmplKPIWindow,
							opacity : 0.9});
					}
					$("#jqxgrid" + globalVar.listEmplKPIWindow).jqxGrid('updatebounddata');
					viewEmplListKPIObj.setRefreshGrid(true);
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
				enableAll();
				$("#ajaxLoading" + globalVar.listEmplKPIWindow).hide();
			}
		});
	}
	
	var getData = function(){
		var data = {};
		//data.partyId = $("#partyIdNew").val().value;
        var partyIdArr = [];
        var rowsParty = $("#jqxGridGroupEmpl").jqxGrid('getrows');
        for(var i = 0; i < rowsParty.length; i++){
            partyIdArr.push(rowsParty[i].partyId);
        }
        data.partyIds = JSON.stringify(partyIdArr);

		data.criteriaId = $("#criteriaIdNew").val();
		data.periodTypeId = $("#periodTypeNew").val();
		data.target = $("#targetNumberNew").val();
		data.uomId = $("#uomIdNew").val();
		data.weight = $("#weightNew").val()/100;
		data.fromDate = $("#fromDateNew").jqxDateTimeInput('val', 'date').getTime();
		var thruDate = $("#thruDateNew").jqxDateTimeInput('val', 'date');
		if(thruDate){
			data.thruDate = thruDate.getTime();
		}
		return data;
	};
    var initJqxGridSearchEmpl = function(){
        createJqxGridSearchEmpl($("#EmplListInOrg"), {uiLabelMap: uiLabelMap, selectionmode: 'checkbox', sourceId: "partyId"});
    };

	var validate = function(){
		return $("#addNewEmplKPIListWindow").jqxValidator('validate');
	};
	
	var initjqxWindow = function(){
		createJqxWindow($("#addNewEmplKPIListWindow"), 500, 460);
		$("#addNewEmplKPIListWindow").on('open', function(event){
			emplListKPICommonObj.setViewEmplKPIList(false);
			var data = $("#searchPartyId").val();
			if(data){
				setPartyIdForEditKPI({partyId: data.value, partyName: data.label});
			}
			$("#thruDateNew").val(null);
			$("#fromDateNew").val(new Date(globalVar.monthStart));
		});
		$("#addNewEmplKPIListWindow").on('close', function(event){
			emplListKPICommonObj.setViewEmplKPIList(true);
			Grid.clearForm($(this));
		});
	};
	
	var setPartyIdForEditKPI = function(data){
		$("#partyIdNew").jqxInput('val', {label: data.partyName, value: data.partyId});
	};
	
	var disableAll = function(){
		$("#alterCancel").attr("disabled", "disabled");
		$("#alterSave").attr("disabled", "disabled");
		$("#saveAndContinue").attr("disabled", "disabled");
		//$("#searchPartyNewBtn").attr("disabled", "disabled");
		$("#targetNumberNew").jqxNumberInput({disabled: true});
		$("#periodTypeNew").jqxDropDownList({disabled: true});
		$("#uomIdNew").jqxDropDownList({disabled: true});
		$("#criteriaIdNew").jqxDropDownList({disabled: true});
		$("#perfCriteriaTypeNew").jqxDropDownList({disabled: true});
		$("#fromDateNew").jqxDateTimeInput({disabled: true});
		$("#thruDateNew").jqxDateTimeInput({disabled: true});
	};
	
	var enableAll = function(){
		$("#alterCancel").removeAttr("disabled");
		$("#alterSave").removeAttr("disabled");
		$("#saveAndContinue").removeAttr("disabled");
		//$("#searchPartyNewBtn").removeAttr("disabled");
		$("#targetNumberNew").jqxNumberInput({disabled: false});
		$("#periodTypeNew").jqxDropDownList({disabled: false});
		$("#uomIdNew").jqxDropDownList({disabled: false});
		$("#criteriaIdNew").jqxDropDownList({disabled: false});
		$("#perfCriteriaTypeNew").jqxDropDownList({disabled: false});
		$("#fromDateNew").jqxDateTimeInput({disabled: false});
		$("#thruDateNew").jqxDateTimeInput({disabled: false});
	};
	
	var openWindow = function(){
		openJqxWindow($("#addNewEmplKPIListWindow"));
	};
	return{
		init: init,
		openWindow: openWindow,
		setPartyIdForEditKPI: setPartyIdForEditKPI
	}
}());

$(document).ready(function(){
	editEmplKPIObj.init();
});