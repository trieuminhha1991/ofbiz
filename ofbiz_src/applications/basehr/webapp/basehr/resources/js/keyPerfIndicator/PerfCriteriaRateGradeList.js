var perfCriteriaRateGradeObj = (function(){
	var _isUpdate = false;
	var init = function(){
		initGrid();
		initJqxWindow();
		$("#jqxNotificationperfCriteriaRateGradeGrid").jqxNotification({ width: "100%", appendContainer: "#containerperfCriteriaRateGradeGrid", 
			opacity: 0.9, template: "info" });
	};
	var initGrid = function(){
		var datafield = [{name: 'perfCriteriaRateGradeId', type: 'string'},
		                 {name: 'perfCriteriaRateGradeName', type: 'string'},
		                 {name: 'fromRating', type: 'number'},
		                 {name: 'toRating', type: 'number'}];
		var columns = [{datafield: 'perfCriteriaRateGradeId', hidden: true, editable: false},
		                  {text: uiLabelMap.HRCommonClassification, datafield: 'perfCriteriaRateGradeName', width: '30%'},
		                  {text: uiLabelMap.CommonFrom, datafield: 'fromRating', width: '35%', columntype: 'numberinput', filtertype: 'number', cellsalign: 'right',
		                	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		                		  return '<span class="align-right">' + value + '%<span>'; 
							   },
		                  },
		                  {text: uiLabelMap.HRCommonToUppercase, datafield: 'toRating', width: '35%', columntype: 'numberinput', filtertype: 'number', 
		                	  cellsalign: 'right',
		                	  cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
								  if(value){
									  return '<span class="align-right">' + value + '%<span>'; 
								  }
								  return '<span class="align-right">' + value + '<span>';
							   },
		                  }
		];
		var grid = $("#perfCriteriaRateGradeGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "perfCriteriaRateGradeGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.PerfCriteriaRateLevelList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
        	Grid.createAddRowButton(
        			grid, container, uiLabelMap.CommonAddNew, {
        				type: "popup",
        				container: $("#addPerfCriteriaRateGradeWindow"),
        			}
        	);
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
	        Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		};
		var config = {
				url: 'JQGetPerfCriteriaRateGrade',
				rendertoolbar: rendertoolbar,
				showtoolbar: true,
				width: '100%',
				virtualmode: true,
				editable: true,
				filterable: true,
				source: {
					addColumns: 'perfCriteriaRateGradeName;fromRating(java.math.BigDecimal);toRating(java.math.BigDecimal)',
					createUrl: 'jqxGeneralServicer?jqaction=C&sname=createPerfCriteriaRateGrade',
					updateUrl: "jqxGeneralServicer?jqaction=U&sname=updatePerfCriteriaRateGrade",
					editColumns: "perfCriteriaRateGradeId;perfCriteriaRateGradeName;fromRating(java.math.BigDecimal);toRating(java.math.BigDecimal)",
					removeUrl: "jqxGeneralServicer?jqaction=D&sname=deletePerfCriteriaRateGrade",
					deleteColumns: 'perfCriteriaRateGradeId',
					functionAfterAddRow: function(){
						_isUpdate = true;
					},
					functionAfterUpdate: function(){
						_isUpdate = true;
					},
					pagesize: 10
				},
				localization: getLocalization(),
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var initJqxWindow = function(){
		createJqxWindow($("#perfCriteriaRateGradeWindow"), 520, 420);
		$("#perfCriteriaRateGradeWindow").on('close', function(event){
			if(_isUpdate){
				createKpiRewardPunishmentObj.updatePerfCriteriaRateGradeDropDown();//createKpiRewardPunishmentObj is defined in CreateKPIRewardPunishmentPolicy.js
			}
			_isUpdate = false;
		});
	};
	var openWindow = function(){
		openJqxWindow($("#perfCriteriaRateGradeWindow"));
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

var addPerfCriteriaRateGradeObj = (function(){
	var init = function(){
		initInput();
		initJqxValidator();
		initJqxWindow();
		create_spinner($("#spinnerKPIRating"));
		initEvent();
	};
	var initInput = function(){
		$("#perfCriteriaRateGradeName").jqxInput({width: '95%', height: 20});
		$("#fromRating").jqxNumberInput({ width: '97%', height: '25px', digits: 3, symbolPosition: 'right', symbol: '%', decimalDigits: 0,  spinButtons: true });
		$("#toRating").jqxNumberInput({ width: '100%', height: '25px', digits: 3, symbolPosition: 'right', symbol: '%', decimalDigits: 0,  spinButtons: true });
		$("#toRating").val(null);
	};
	var initJqxWindow = function(){
		createJqxWindow($("#addPerfCriteriaRateGradeWindow"), 400, 220);
		$("#addPerfCriteriaRateGradeWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
	};
	var initJqxValidator = function(){
		$("#addPerfCriteriaRateGradeWindow").jqxValidator({
			rules: [
				{input : '#perfCriteriaRateGradeName', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#fromRating', message : uiLabelMap.ValueMustGreaterOrEqualThanZero, action : 'blur',
					rule : function(input, commit){
						if(input.val() < 0){
							return false;
						}
						return true;
					}
				},
				{input : '#toRating', message : uiLabelMap.ValueMustGreaterThanFrom, action : 'blur',
					rule : function(input, commit){
						if(input.val() == 0){
							return false;
						}
						if(!input.val()){
							return true;
						}
						var fromRating = $("#fromRating").val();
						if(fromRating >= input.val()){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	var initEvent = function(){
		$("#clearToRatingValue").click(function(event){
			$("#toRating").val(null);
		});
		$("#cancelKPIRate").click(function(event){
			$("#addPerfCriteriaRateGradeWindow").jqxWindow('close');
		});
		$("#saveKPIRate").click(function(event){
			var valid = $("#addPerfCriteriaRateGradeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.ConfirmCreateNewKPIRating,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							createKPIRating();
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
	};
	
	var createKPIRating = function(){
		$("#loadingAddKPIRating").show();
		$("#cancelKPIRate").attr("disabled", "disabled");
		$("#saveKPIRate").attr("disabled", "disabled");
		var data = getData();
		$("#perfCriteriaRateGradeGrid").jqxGrid('addrow', null, data, 'first');
		$("#addPerfCriteriaRateGradeWindow").jqxWindow('close');
		enable();
	};
	var getData = function(){
		var data = {};
		data.perfCriteriaRateGradeName = $("#perfCriteriaRateGradeName").val();
		data.fromRating = $("#fromRating").val();
		data.toRating = $("#toRating").val();
		return data;
	};
	var enable = function(){
		$("#loadingAddKPIRating").hide();
		$("#cancelKPIRate").removeAttr("disabled");
		$("#saveKPIRate").removeAttr("disabled");
	};
	return{
		init: init,
	}
}());
$(document).ready(function(){
	perfCriteriaRateGradeObj.init();
	addPerfCriteriaRateGradeObj.init();
});