var generalInfoObj = (function(){
	var init = function(){
		initInput();
		initDropDown();
		initJqxTreeDropDownBtn();
		initValidator();
		initData();
		initEvent();
	};
	var initInput = function(){
		$("#partyTargetNameAdd").jqxInput({width: '97%', height: 20});
		$("#fromDateAdd").jqxDateTimeInput({width: '100%', height: 25});
		$("#thruDateAdd").jqxDateTimeInput({width: '95%', height: 25});
	};
	var initJqxTreeDropDownBtn = function(){
		var config = {dropDownBtnWidth: 335, treeWidth: 335};
		globalObject.createJqxTreeDropDownBtn($("#jqxTree"), $("#partyIdAdd"), globalVar.rootPartyArr, "tree", "treeChild", config);
	};
	
	var initDropDown = function(){
		createJqxDropDownListExt($("#periodTypeAdd"), globalVar.periodTypeArr, {valueMember: 'periodTypeId', displayMember: 'description', width: '98%', height: 25});
	};
	var initEvent = function(){
		$('#jqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#jqxTree').jqxTree('getItem', event.args.element);
		  	setDropdownContent(event.args.element, $("#jqxTree"), $("#partyIdAdd"));
		});
		
		$("#periodTypeAdd").on('select', function(event){
			var args = event.args;
			if(args){
				var index = args.index;
				var periodType = globalVar.periodTypeArr[index];
				var fromDate = $("#fromDateAdd").jqxDateTimeInput('val', 'date');
				setThruDateValue(fromDate, periodType.periodLength, periodType.uomId);
			}
		});
		$("#fromDateAdd").on('valueChanged', function (event){
			var periodTypeSelected = $("#periodTypeAdd").jqxDropDownList('getSelectedItem');
			if(periodTypeSelected){
				var originalItem = periodTypeSelected.originalItem;
				var fromDate = event.args.date;
				setThruDateValue(fromDate, originalItem.periodLength, originalItem.uomId);
			}
		});
	};
	var setThruDateValue = function(fromDate, periodLength, uomId){
		if(fromDate && periodLength && uomId){
			var year = fromDate.getFullYear();
			var month = fromDate.getMonth();
			var day = fromDate.getDate();
			if(uomId == "TF_yr"){
				year += periodLength;
			}else if(uomId == "TF_mon"){
				month += periodLength;
			}else if(uomId == "TF_wk"){
				day += periodLength * 7;
			}else if(uomId == "TF_day"){
				day += periodLength;
			}
			var date = new Date(year, month, day - 1);
			$("#thruDateAdd").val(date);
		}
	};
	var initData = function(){
		var date = new Date();
		date.setDate(1);
		
		if(globalVar.rootPartyArr.length > 0){
			$("#jqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_tree")[0]);
		}
		var item = $('#jqxTree').jqxTree('getSelectedItem');
		setDropdownContent(item.element, $("#jqxTree"), $("#partyIdAdd"));
		
		$("#fromDateAdd").val(date);
		$("#periodTypeAdd").jqxDropDownList({selectedIndex: 0});
		$("#descriptionAdd").val("");
	};
	var initJqxEditor = function(){
		$("#descriptionAdd").jqxEditor({ 
    		width: '98%',
            theme: 'olbiuseditor',
            tools: '',
            height: 100,
        });
	};
	var initContent = function(){
		initJqxEditor();
	};
	var initValidator = function(){
		$("#generalInfo").jqxValidator({
			rules: [
				{input : '#partyTargetNameAdd', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#partyIdAdd', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#periodTypeAdd', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#thruDateAdd', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#fromDateAdd', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#thruDateAdd', message : uiLabelMap.ThruDateMustGreaterThanFromDate, action : 'blur',
					rule : function(input, commit){
						var fromDate = $('#fromDateAdd').jqxDateTimeInput('val', 'date');
						var thruDate = $(input).jqxDateTimeInput('val', 'date');
						if(thruDate && thruDate < fromDate){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	var validate = function(){
		return $("#generalInfo").jqxValidator('validate');
	};
	var hideValidate = function(){
		$("#generalInfo").jqxValidator('hide');
	};
	var resetData = function(){
		Grid.clearForm($("#generalInfo"));
		hideValidate();
	};
	var getData = function(){
		var data = {};
		data.partyTargetName = $("#partyTargetNameAdd").val();
		data.partyId = $("#jqxTree").jqxTree('getSelectedItem').value;
		data.periodTypeId = $("#periodTypeAdd").val();
		data.fromDate = $("#fromDateAdd").jqxDateTimeInput('val', 'date').getTime();
		data.thruDate = $("#thruDateAdd").jqxDateTimeInput('val', 'date').getTime();
		var description = $("#descriptionAdd").val();
		if(description && description.length > 0){
			data.description = description;
		}
		return data;
	};
	return{
		init: init,
		initData: initData,
		resetData: resetData,
		getData: getData,
		initContent: initContent,
		validate: validate,
		hideValidate: hideValidate
	}
}());
var settingTargetObj = (function(){
	var init = function(){
		initGrid();
		initDropDown();
		initInput();
		initWindow();
		initEvent();
		initValidator();
	};
	var initGrid = function(){
		var grid = $("#keyPerfIndItemGrid");
		var datafield = [{name: 'keyPerfIndicatorId', type: 'string'},
		                 {name: 'keyPerfIndicatorName', type: 'string'},
		                 {name: 'weight', type: 'number'},
		                 {name: 'uomId', type: 'string'},
		                 {name: 'target', type: 'number'},
		                 ];
		var columns = [{text: uiLabelMap.KeyPerfIndicator, datafield: 'keyPerfIndicatorName', width: '30%'},
		               {text: uiLabelMap.KPIWeigth, datafield: 'weight', columntype: 'numberinput', width: '15%',
							cellsrenderer: function (row, column, value){
				     		   if(typeof(value) == 'number'){
				     			   return '<span>' + value + '%</span>'
				     		   }
				     	   }
		               },
		               {text: uiLabelMap.HRTarget, datafield: 'target', columntype: 'numberinput', width: '40%',
		            	   cellsrenderer: function (row, column, value){
		            		   if(typeof(value) == 'number'){
		            			   return '<span>' + formatNumber(value) + '</span>'
		            		   }
		            	   }
		               },
		               {text: uiLabelMap.HRCommonMeasure, datafield: 'uomId', width: '15%', columntype: 'dropdownlist',
							cellsrenderer: function (row, column, value){
					 		   for(var i = 0; i< globalVar.uomArr.length; i++){
					 			   if(value == globalVar.uomArr[i].uomId){
					 				   return '<span>' + globalVar.uomArr[i].abbreviation + '</span>';
					 			   }
					 		   }
					 		   return '<span>' + value + '</span>';
					 	    },
		               }
		               ];
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "keyPerfIndItemGrid";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.SettingTarget + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#addNewKeyPerfIndicator")});
    		Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		};
		
		var config = {
				url: '',
				showtoolbar : true,
				rendertoolbar: rendertoolbar,
				width : '100%',
				virtualmode: false,
				editable: true,
				localization: getLocalization(),
				source: {
					localdata: [],
					pagesize: 5
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initDropDown = function(){
		createJqxDropDownListBinding($("#keyPerfIndicatorList"), [{name: 'keyPerfIndicatorId'}, {name: 'keyPerfIndicatorName'}],
				'', "listReturn", "keyPerfIndicatorId", "keyPerfIndicatorName", "98%", 25);
		createJqxDropDownListExt($("#itemUom"), globalVar.uomArr, {displayMember: 'abbreviation', valueMember: 'uomId', width: '98%', height: 25});
	};
	var initInput = function(){
		$("#itemWeight").jqxNumberInput({width: '98%', height: 25, spinButtons: true, digits: 3, decimalDigits: 1, symbolPosition: 'right', symbol: '%'});
		$("#itemTarget").jqxNumberInput({width: '98%', height: 25, spinButtons: true, digits: 12, decimalDigits: 1, max: 999999999999});
	};
	var initWindow = function(){
		createJqxWindow($("#addNewKeyPerfIndicator"), 400, 280);
	};
	var initValidator = function(){
		$("#addNewKeyPerfIndicator").jqxValidator({
			rules:[
				{input : '#keyPerfIndicatorList', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#itemUom', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
			]
		});
	}
	var initEvent = function(){
		$("#addNewKeyPerfIndicator").on('close', function(event){
			Grid.clearForm($("#addNewKeyPerfIndicator"));
		});
		$("#cancelAddNewTarget").click(function(event){
			$("#addNewKeyPerfIndicator").jqxWindow('close');
		});
		$("#saveAddNewTarget").click(function(event){
			var valid = $("#addNewKeyPerfIndicator").jqxValidator('validate');
			if(!valid){
				return;
			}
			var data = {};
			var kpi = $("#keyPerfIndicatorList").jqxDropDownList('getSelectedItem');
			data.keyPerfIndicatorName = kpi.label;
			data.keyPerfIndicatorId = kpi.value;
			data.weight = $("#itemWeight").val();
			data.target = $("#itemTarget").val();
			data.uomId = $("#itemUom").val();
			$("#keyPerfIndItemGrid").jqxGrid('addrow', null, data, 'first');
			$("#addNewKeyPerfIndicator").jqxWindow('close');
		});
	};
	var prepareData = function(){
		var data = generalInfoObj.getData();
		updateJqxDropDownListBinding($("#keyPerfIndicatorList"), "getKeyPerfIndicatorByPartyInPeriod?partyId=" + data.partyId + "&fromDate=" + data.fromDate + "&thruDate=" + data.thruDate);
	};
	var validate = function(){
		var rows = $("#keyPerfIndItemGrid").jqxGrid('getrows');
		if(rows.length == 0){
			bootbox.dialog(uiLabelMap.KeyPerfIndicatorIsNotSetting,
					[
					{
		    		    "label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
		    		}]		
				);
			return false;
		}
		return true;
	};
	var resetData = function(){
		var source = $("#keyPerfIndItemGrid").jqxGrid('source');
		source._source.localdata = [];
		$("#keyPerfIndItemGrid").jqxGrid('source', source);
	};
	var getData = function(){
		return JSON.stringify($("#keyPerfIndItemGrid").jqxGrid('getrows'));
	};
	return {
		init: init,
		prepareData: prepareData,
		validate: validate,
		resetData: resetData,
		getData: getData
	}
}());


var addKeyPerfIndPartyTargerObj = (function(){
	var init = function(){
		generalInfoObj.init();
		settingTargetObj.init();
		initWizard();
		initWindow();
		initEvent();
		create_spinner($("#spinnerAddNew"));
	};
	var initWizard = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
	        if(info.step == 1 && (info.direction == "next")) {
	        	var valid = generalInfoObj.validate();
	        	if(!valid){
	        		return valid;
	        	}
	        	settingTargetObj.prepareData();
	        }
	        if(info.direction == "previous"){
	        	
	        }
	    }).on('stepclick', function(e){
	    });
	};
	var createKeyPerfIndPartyTarget = function(){
		var data = generalInfoObj.getData();
		data.targetItem = settingTargetObj.getData();
		disableAll();
		$.ajax({
			type : 'POST',
			url : 'createKeyPerfIndPartyTarget',
			data : data,
			success : function(response){
				if(response.responseMessage == "success"){
					Grid.renderMessage('jqxgrid', response.successMessage, {autoClose: true,
						template : 'info', appendContainer : "#containerjqxgrid", opacity : 0.9});
					$("#AddNewKeyPerfIndicatorWindow").jqxWindow('close');
					$('#jqxgrid').jqxGrid('updatebounddata');
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
			}
		});
	};
	var initData = function(){
		generalInfoObj.initData();
	};
	var initWindow = function(){
		var initContent = function(){
			generalInfoObj.initContent();
		};
		createJqxWindow($("#AddNewKeyPerfIndicatorWindow"), 550, 430, initContent);
	};
	var initEvent = function(){
		$("#AddNewKeyPerfIndicatorWindow").on('open', function(event){
			generalInfoObj.initData();
		});
		$("#AddNewKeyPerfIndicatorWindow").on('close', function(event){
			generalInfoObj.resetData();
			settingTargetObj.resetData();
			resetStep();
		});
		$('#fuelux-wizard').on('finished', function(event){
			var rowCount = $("#keyPerfIndItemGrid").jqxGrid('getrows').length;
			if(rowCount != 0){
				bootbox.dialog(uiLabelMap.CreateKeyPerfIndPartyTargetConfirm,
						[{
							"label" : uiLabelMap.CommonSubmit,
			    		    "class" : "btn-primary btn-small icon-ok open-sans",
			    		    "callback": function() {
			    		    	createKeyPerfIndPartyTarget();
			    		    }	
						},
						{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
			    		}]		
					);
			}else{
				bootbox.dialog(uiLabelMap.TargetsHaveNotSetting,
						[{
			    		    "label" : uiLabelMap.CommonClose,
			    		    "class" : "btn-danger btn-small icon-remove open-sans",
			    		}]		
					);
			}
		});
	};
	var resetStep = function(){
		$('#fuelux-wizard').wizard('previous');
	};
	var disableAll = function(){
		$("#loadingAddNew").show();
		$("#btnNext").attr("disabled", "disabled");
		$("#btnPrev").attr("disabled", "disabled");
	};
	var enableAll = function(){
		$("#loadingAddNew").hide();
		$("#btnNext").removeAttr("disabled");
		$("#btnPrev").removeAttr("disabled");
	};
	return{
		init: init,
		disableAll: disableAll,
		enableAll: enableAll
	}
}());

$(document).ready(function(){
	addKeyPerfIndPartyTargerObj.init();
});