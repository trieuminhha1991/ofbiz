var viewListBenefitTypeObject = (function(){
	var seq_id = 0;
	var benefitTypeId;
	var init = function(){
		initJqxGrid();
		initContextMenu();
		initJqxDropDownList();
		initJqxDropDownListEvent();
		initJqxNumberInput();
		initBtnEvent();
		initJqxWindow();
		initJqxNotification();
	};
	
	var initJqxNotification = function(){
		$("#jqxNotificationjqxInsuranceBenefitTypeGrid").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerjqxInsuranceBenefitTypeGrid"});
		$("#jqxNotificationjqxgrid").jqxNotification({width: '100%', opacity: 0.9, autoOpen: false, autoClose: true, template: "info", appendContainer: "#containerjqxgrid"});
	};
	var initJqxPanel = function(){
		$("#benefitTypeRulesPanel").jqxPanel({width: '99,5%', height: 450, scrollBarSize: 15});
	};
	var initBtnEvent = function(){
		$("#btnCancelRules").click(function(event){
			$("#createBenefitRuleWindow").jqxWindow('close');
		});
		$("#addNewCondition").click(function(event){
			seq_id++;
			createNewCondition();
			
		});
		$("#deleteCond_0").click(function(event){
			$(this).closest(".row-fluid").remove();
			seq_id--;
		});
		$("#btnSaveRules").click(function(event){
			bootbox.dialog(uiLabelMap.ConfirmAddRuleBenefit,
					[
					 {
						"label" : uiLabelMap.CommonSubmit,
		    		    "class" : "btn-primary btn-small icon-ok open-sans",
		    		    "callback": function() {
		    		 		createInsBenefitTypeRule();
		    		    }	
					},
					{
						"label" : uiLabelMap.CommonClose,
		    		    "class" : "btn-danger btn-small icon-remove open-sans",
		    		    "callback": function() {
		    		    	
		    		    }
					}
					]	
			);
		});
	};
	
	var createInsBenefitTypeRule = function(){
		var data = {};
		data.benefitTypeId = benefitTypeId;
		var benefitTypeRuleConds = new Array();
		var benefitTypeRuleAction = new Array();
		$("div[id^='insBenInParam']").each(function(){
			var id = $(this).attr("id");
			var suffix = id.substring(id.indexOf("_"));
			var inputParamEnumId = $(this).val();
			var operatorEnumId = $("#insBenefitCond" + suffix).val();
			var condValue = $("#insBenefitCondNumberInput" + suffix).val();
			if(inputParamEnumId && operatorEnumId){
				benefitTypeRuleConds.push({inputParamEnumId: inputParamEnumId, operatorEnumId: operatorEnumId, condValue: condValue});
			}
		});	
		var insBeMaxLeave = $("#insBeMaxLeave").val();
		var maxDayLeaveNumberInput = $("#maxDayLeaveNumberInput").val();
		var uomId = $("#uomId").val();
		if(insBeMaxLeave && uomId){
			benefitTypeRuleAction.push({benefitTypeActionEnumId: insBeMaxLeave, quantity: maxDayLeaveNumberInput, uomId: uomId});
		}
		var insBenActSal = $("#insBenActSal").val();
		var rateBenefit = $("#rateBenefit").val();
		var monthCalcBenefit = $("#monthCalcBenefit").val();
		if(insBenActSal){
			benefitTypeRuleAction.push({benefitTypeActionEnumId: insBenActSal, quantity: rateBenefit, amount: monthCalcBenefit});
		}
		data.benefitTypeCond = JSON.stringify(benefitTypeRuleConds);		
		data.benefitTypeAct = JSON.stringify(benefitTypeRuleAction);
		disabledJqxGrid($("#jqxInsuranceBenefitTypeGrid"));
		$("#createBenefitRuleWindow").jqxWindow('close');
		$.ajax({
			url: "createInsBenefitTypeRule",
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					$("#notificationContentjqxInsuranceBenefitTypeGrid").text(response.successMessage);
					$("#jqxNotificationjqxInsuranceBenefitTypeGrid").jqxNotification({template: 'info'});
					$("#jqxNotificationjqxInsuranceBenefitTypeGrid").jqxNotification("open");
					$("#jqxInsuranceBenefitTypeGrid").jqxGrid('updatebounddata');
				}else{
					$("#notificationContentjqxInsuranceBenefitTypeGrid").text(response.errorMessage);
					$("#jqxNotificationjqxInsuranceBenefitTypeGrid").jqxNotification({template: 'error'});
					$("#jqxNotificationjqxInsuranceBenefitTypeGrid").jqxNotification("open");
				}
			},
			complete:  function(jqXHR, textStatus){
				enableJqxGrid($("#jqxInsuranceBenefitTypeGrid"));
			}
		});
	};
	
	var createNewCondition = function(){
		var newCondDiv = $("<div class='row-fluid'></div>");
		newCondDiv.append('<div class="span12"><div class="span7">' +
								'<div id="insBenInParam_'+ seq_id + '"></div>' +
								'</div>' +
								'<div class="span2">'+
									'<div id="insBenefitCond_'+ seq_id + '"></div>'+
								'</div>'+
								'<div class="span2">' +
									'<div id="insBenefitCondNumberInput_'+ seq_id + '"></div>'+
								'</div>'+
								'<div class="span1">' +
									'<button class="btn-mini grid-action-button" id="deleteCond_' + seq_id + '"><i class="icon-only icon-trash"></i></button>' +
								'</div>' +
							'</div>');
		newCondDiv.appendTo($("#conditionContainer"));
		createJqxDropDownList(globalVar.insBenParamArr, $("#insBenInParam_" + seq_id), "enumId", "description", 25, "100%");
		createJqxDropDownList(globalVar.insBenCondOperArr, $("#insBenefitCond_" + seq_id), "enumId", "enumCode", 25, "100%");
		$("#insBenefitCondNumberInput_" + seq_id).jqxNumberInput({ width: '100%', height: '25px', inputMode: 'simple', spinButtons: false, decimalDigits: 0});
		$("#deleteCond_" + seq_id).click(function(event){
			$(this).closest(".row-fluid").remove();
			seq_id--;
		});
		resizeWindow(true, false, 30);
	};
	
	var initJqxDropDownListEvent = function(){
		$("#insBenActSal").on('select', function(event){
			 var args = event.args;
			 if (args) {
				 var value = args.item.value;
				 if(value == "INS_BE_RATE_SAL_MON"){
					 $("#amountValueBenefitSal").show();
					 resizeWindow(false, true, 30);
				 }else{
					 $("#amountValueBenefitSal").hide();
				 }
			 }
		});
	};
	
	var resizeWindow = function(condIncrement, actionIncrement, incrementNbr){
		var condsHeight = $("#benefitCond").outerHeight();
		var actsHeight = $("#benefitAction").outerHeight();
		var jqxWindowHeight = $("#createBenefitRuleWindow").jqxWindow('height');
		if(condIncrement){
			if((condsHeight + incrementNbr) > actsHeight && (condsHeight + incrementNbr + 70) > jqxWindowHeight){
				resizeHeightJqxWindow($("#createBenefitRuleWindow"), incrementNbr);
			}
		}else if(actionIncrement){
			if((actsHeight + incrementNbr) > condsHeight && (actsHeight + incrementNbr + 70) > jqxWindowHeight){
				resizeHeightJqxWindow($("#createBenefitRuleWindow"), incrementNbr);
			}
		}
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.insBenParamArr, $("#insBenInParam_" + seq_id), "enumId", "description", 25, "100%");
		createJqxDropDownList(globalVar.insBenCondOperArr, $("#insBenefitCond_" + seq_id), "enumId", "enumCode", 25, "100%");
		createJqxDropDownList(globalVar.insBenActTimeArr, $("#insBeMaxLeave"), "enumId", "description", 25, "100%");
		createJqxDropDownList(globalVar.uomArr, $("#uomId"), "uomId", "abbreviation", 25, "100%");
		createJqxDropDownList(globalVar.insBenActSalArr, $("#insBenActSal"), "enumId", "description", 25, "100%");
	};
	
	var initJqxNumberInput = function(){
		$("#maxDayLeaveNumberInput").jqxNumberInput({ width: '100%', height: '25px', inputMode: 'simple', spinButtons: false, decimalDigits: 0 });
		$("#rateBenefit").jqxNumberInput({ width: '100%', height: '25px', inputMode: 'simple', spinButtons: false });
		$("#monthCalcBenefit").jqxNumberInput({ width: '100%', height: '25px', inputMode: 'simple', spinButtons: false, decimalDigits: 0});
		$("#insBenefitCondNumberInput_" + seq_id).jqxNumberInput({ width: '100%', height: '25px', inputMode: 'simple', spinButtons: false, decimalDigits: 0});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#benefitTypeRulesWindow"), 800, 520, initJqxPanel);
		createJqxWindow($("#createBenefitRuleWindow"), 800, 250);
		$("#createBenefitRuleWindow").jqxWindow({minHeight: 250, maxHeight: 500});
		
		$("#createBenefitRuleWindow").on('close', function(){
			Grid.clearForm($(this));
			$("#amountValueBenefitSal").hide();
			$(this).jqxWindow({height: 250});
			if(seq_id > 0){
				$("#conditionContainer").children().each(function(){
					$(this).remove();
					seq_id--;
				});
				seq_id++;
				createNewCondition();
			}
		});
	};
	
	var initContextMenu = function(){
		createJqxMenu("contextMenu", 30, 170);
		$("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == 'viewBenefitRules'){
            	var tempBenefitTypeId = dataRecord.benefitTypeId;
            	benefitTypeId = tempBenefitTypeId;
            	var tempS = $("#jqxInsuranceBenefitTypeGrid").jqxGrid('source');
            	tempS._source.url = "jqxGeneralServicer?sname=JQgetListRulesInsBenefitType&hasrequest=Y&benefitTypeId=" + tempBenefitTypeId;
            	$("#jqxInsuranceBenefitTypeGrid").jqxGrid('source', tempS);
            	openJqxWindow($("#benefitTypeRulesWindow"));
            }
		});
	};
	
	var initJqxGrid = function(){
		var datafield = [{name: "benefitTypeId", type: "string"},
		                 {name: "benefitTypeRuleId", type: "string"},
		                 {name: "benefitTypeRuleConds", type: "string"},
		                 {name: "benefitTypeRuleActs", type: "string"},
	    ];
		
		var columnlist = [{datafield: 'benefitTypeId', hidden: true},
		                {datafield: 'benefitTypeRuleId', hidden: true},  
						{text: uiLabelMap.HRSequenceNbr, sortable: false, filterable: false, editable: false,
						    groupable: false, draggable: false, resizable: false,
						    datafield: '', columntype: 'number', width: 50,
						    cellsrenderer: function (row, column, value) {
						        return "<div style='margin:4px;'>" + (value + 1) + "</div>";
						    }
						},
						{text: uiLabelMap.IBCondition, datafield: 'benefitTypeRuleConds', filterable: false, editable: false, width: '30%',
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(value){
									var retVal = "<ul style='margin: 0 0 0 10px'>";
									for(var i = 0; i < value.length; i++){
										retVal += "<li>" + value[i] + "</li>";
									}
									retVal += "</ul>";
									return '<span>' + retVal + '</span>'; 
								}
								return '<span>' + value + '<span>';
							}
						},
						{text: uiLabelMap.IBAction, datafield: 'benefitTypeRuleActs', filterable: false, editable: false,
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
								if(value){
									var retVal = "<ul style='margin: 0 0 0 10px'>";
									for(var i = 0; i < value.length; i++){
										retVal += "<li>" + value[i] + "</li>";
									}
									retVal += "</ul>";
									return '<span>' + retVal + '</span>';
								}
								return '<span>' + value + '<span>';
							}
						},
		];
		
		var rendertoolbar = function (toolbar){
			toolbar.html('');
	        var me = this;
	        var jqxheader = $("<div id='toolbarcontainer' class='widget-header'><h4>" + uiLabelMap.HRCommonContent + "</h4><div id='toolbarButtonContainer' class='pull-right'></div></div>");
	     	toolbar.append(jqxheader);
	        var container = $('#toolbarButtonContainer');
	     	var button = $('<button id="addrowbutton" style="cursor: pointer"><i class="icon-plus-sign"></i>' + uiLabelMap.accAddNewRow + '</button>')
	       	var delBtn = $('<button style=" cursor: pointer" id="deleterowbutton"><i class="icon-trash"></i>' + uiLabelMap.accDeleteSelectedRow + '</button>');
	     	container.append(button);        
	     	container.append(delBtn);
	        button.jqxButton();
	        delBtn.jqxButton();
	        button.on('click', function () { 
	        	openJqxWindow($("#createBenefitRuleWindow"));
	        });
	        delBtn.on('click', function(event){
	        	var selectIndex = $("#jqxInsuranceBenefitTypeGrid").jqxGrid('getselectedrowindex');
	        	if(selectIndex > -1){
	        		bootbox.dialog(uiLabelMap.NotifyDelete,
	        				[
	        				 {
	        					 "label" : uiLabelMap.CommonSubmit,
	        					 "class" : "btn-primary btn-small icon-ok open-sans",
	        					 "callback": function() {
	        						 deleteInsBenefitTypeRule();
	        					 }	
	        				 },
	        				 {
	        					 "label" : uiLabelMap.CommonClose,
	        					 "class" : "btn-danger btn-small icon-remove open-sans",
	        				 }
	        				 ]	
	        		);
	        	}
	        });
	   	};
	   	var config = {
	   		width: '100%', 
	   		autoheight: true,
	   		//height: 450,
	   		autorowheight: true,
	   		virtualmode: true,
	   		showfilterrow: false,
	   		rendertoolbar: rendertoolbar,
	   		selectionmode: 'singlerow',
	   		pageable: true,
	   		sortable: false,
	        filterable: false,
	        editable: false,
	        rowsheight: 26,
	        selectionmode: 'singlerow',
	        url: '',    
   			showtoolbar: true,
        	source: {pagesize: 10, removeUrl: "deleteInsBenefitTypeRule", deleteColumns: "benefitTypeId;benefitTypeRuleId"}
	   	};
	   	Grid.initGrid(config, datafield, columnlist, null, $("#jqxInsuranceBenefitTypeGrid"));
	};
	
	var deleteInsBenefitTypeRule = function(){
		var gridEle = $("#jqxInsuranceBenefitTypeGrid");
		var selectIndex = $("#jqxInsuranceBenefitTypeGrid").jqxGrid('getselectedrowindex');
		var id = gridEle.jqxGrid('getrowid', selectIndex);
		gridEle.jqxGrid('deleterow', id);
	};
	
	return{
		init: init
	}
}());

$(document).ready(function () {
	viewListBenefitTypeObject.init();
});
