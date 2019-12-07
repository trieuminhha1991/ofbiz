var emplFamilyObj = (function(){
	var _partyId = "";
	var init = function(){
		initGrid();
		initJqxWindow();
		initEvent();
		$("#jqxNotificationviewDetailFamilyGrid").jqxNotification({ width: "100%", appendContainer: "#containerviewDetailFamilyGrid",
				opacity: 0.9, autoClose: true, template: "info" });
	};
	var initJqxWindow = function(){
		createJqxWindow($("#emplFamilyWindow"), 860, 520);
	};
	var openWindow = function(){
		openJqxWindow($("#emplFamilyWindow"));
	};
	var setData = function(data){
		_partyId = data.partyId;
	};
	var initGrid = function(){
		var dataField=[{ name: 'relPartyId', type: 'string' },
						 { name: 'personFamilyBackgroundId', type: 'string' },
						 { name: 'partyId', type: 'string'},
						 { name: 'partyFamilyId', type: 'string' },
						 { name: 'familyFirstName', type: 'string' },
						 { name: 'familyFullName', type: 'string' },
						 { name: 'partyRelationshipTypeId', type: 'string' },
						 { name: 'occupation', type: 'string' },
						 { name: 'birthDate', type: 'date'},
						 { name: 'placeWork', type: 'string'},
						 { name: 'phoneNumber', type: 'number'},
						 { name: 'isDependent', type: 'bool'},
						 { name: 'statusId', type: 'string'},
						 { name: 'dependentStartDate', type: 'date'},
						 { name: 'dependentEndDate', type: 'date'},
		 ];
		var columnlist = [
							{ text: uiLabelMap.HRFullName, datafield: 'familyFirstName', width: '14%', filtertype: 'input',
								cellsrenderer: function(row, column, value){
									var rowData = $('#viewDetailFamilyGrid').jqxGrid('getrowdata', row);
									if(rowData){
										return '<span>' + rowData.familyFullName +'</span>';
									}
								},
								cellclassname: function (row, column, value, data) {
								    if (data.isDependent) {
								    	var statusId = data.statusId
								    	if(statusId == 'DEP_ACCEPT'){
								    		return 'highlightCell';
								    	}else if(statusId == 'DEP_REJECT'){
								    		return 'redCell';
								    	}else if(statusId == 'DEP_WAITING_APPR'){
								    		return 'aquamarineCell';
								    	}
								    }
								}
							},	
							{ text: uiLabelMap.HRRelationship, datafield: 'partyRelationshipTypeId', width: '9%', 
								filtertype: 'list', columntype: 'dropdownlist', editable: true, sortable: false,
								cellsrenderer: function(row, column, value){
									for(var i = 0; i < partyRelationshipType.length; i++){
										if(value == partyRelationshipType[i].partyRelationshipTypeId){
											return '<span title=' + value + '>' + partyRelationshipType[i].partyRelationshipName + '</span>';
										}
									}
									return '<span>' + value + '</span>';
								},
								cellclassname: function (row, column, value, data) {
								    if (data.isDependent) {
								    	var statusId = data.statusId
								    	if(statusId == 'DEP_ACCEPT'){
								    		return 'highlightCell';
								    	}else if(statusId == 'DEP_REJECT'){
								    		return 'redCell';
								    	}else if(statusId == 'DEP_WAITING_APPR'){
								    		return 'aquamarineCell';
								    	}
								    }
								},
								createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
							        editor.jqxDropDownList({source: partyRelationshipType, valueMember: 'partyRelationshipTypeId', displayMember:'partyRelationshipName' });
							    },
							    createfilterwidget: function (column, htmlElement, editor) {
							        editor.jqxDropDownList({ source: fixSelectAll(partyRelationshipType), displayMember: 'partyRelationshipName', valueMember: 'partyRelationshipTypeId' ,
							        	renderer: function (index, label, value) {
							        		if (index == 0) {
							        			return value;
							        		}
							                for(var i = 0; i < partyRelationshipType.length; i++){
							                	if(value == partyRelationshipType[i].partyRelationshipTypeId){
							                		return partyRelationshipType[i].partyRelationshipName; 
							                	}
							                }
							            }});
							        editor.jqxDropDownList('checkAll');
							    }
							},
							{ text: uiLabelMap.BirthDate, datafield: 'birthDate', width: '12%', cellsformat: 'd', filtertype:'range', columntype: 'datetimeinput',
								createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
									editor.jqxDateTimeInput({width: '110px', height: '31px'});
								},
								cellclassname: function (row, column, value, data) {
								   if(data.isDependent) {
								   		var statusId = data.statusId
								    	if(statusId == 'DEP_ACCEPT'){
								    		return 'highlightCell';
								    	}else if(statusId == 'DEP_REJECT'){
								    		return 'redCell';
								    	}else if(statusId == 'DEP_WAITING_APPR'){
								    		return 'aquamarineCell';
								    	}
								    }
								}
							},
							{ text: uiLabelMap.HRCommonRegister, datafield: 'isDependent',filtertype: 'list', columntype: 'checkbox', editable: false,
								filterable: false, sortable: false, columngroup:'dependentGroup', width: '8%',
								cellclassname: function (row, column, value, data) {
								    if (data.isDependent) {
								    	var statusId = data.statusId
								    	if(statusId == 'DEP_ACCEPT'){
								    		return 'highlightCell';
								    	}else if(statusId == 'DEP_REJECT'){
								    		return 'redCell';
								    	}else if(statusId == 'DEP_WAITING_APPR'){
								    		return 'aquamarineCell';
								    	}
								    }
								}
							},
							
							{text: uiLabelMap.DependentDeductionStart, datafield: 'dependentStartDate', width: '15%', 
								editable: false, cellsformat: 'dd/MM/yyyy', columngroup:'dependentGroup',
								cellclassname: function (row, column, value, data) {
								    if (data.isDependent) {
								    	var statusId = data.statusId;
								    	if(statusId == 'DEP_ACCEPT'){
								    		return 'highlightCell';
								    	}else if(statusId == 'DEP_REJECT'){
								    		return 'redCell';
								    	}else if(statusId == 'DEP_WAITING_APPR'){
								    		return 'aquamarineCell';
								    	}
								    }
								}	
							},
							{text: uiLabelMap.DependentDeductionEnd, datafield: 'dependentEndDate', width: '19%', 
								editable: false, cellsformat: 'dd/MM/yyyy', columngroup:'dependentGroup',
								cellclassname: function (row, column, value, data) {
								    if (data.isDependent) {
								    	var statusId = data.statusId;
								    	if(statusId == 'DEP_ACCEPT'){
								    		return 'highlightCell';
								    	}else if(statusId == 'DEP_REJECT'){
								    		return 'redCell';
								    	}else if(statusId == 'DEP_WAITING_APPR'){
								    		return 'aquamarineCell';
								    	}
								    }
								}	
							},
							{ text: uiLabelMap.HRApprove, datafield: 'statusId', editable: false, width: '13%',
								sortable: false, columngroup:'dependentGroup',
								cellsrenderer: function(row, column, value){
									for(var i = 0; i < globalVar.dependentStatusList.length; i++){
										if(globalVar.dependentStatusList[i].statusId == value){
											return '<span>' + globalVar.dependentStatusList[i].description + '</span>';
										}
									}
									return '<span>' + value + '</span>';
								},
								cellclassname: function (row, column, value, data) {
								    if (data.isDependent) {
								    	var statusId = data.statusId;
								    	if(statusId == 'DEP_ACCEPT'){
								    		return 'highlightCell';
								    	}else if(statusId == 'DEP_REJECT'){
								    		return 'redCell';
								    	}else if(statusId == 'DEP_WAITING_APPR'){
								    		return 'aquamarineCell';
								    	}
								    }
								}
							},
							{ text: uiLabelMap.placeWork, datafield: 'placeWork', width: '16%', filtertype: 'string',
								cellclassname: function (row, column, value, data) {
								    if (data.isDependent) {
								    	var statusId = data.statusId
								    	if(statusId == 'DEP_ACCEPT'){
								    		return 'highlightCell';
								    	}else if(statusId == 'DEP_REJECT'){
								    		return 'redCell';
								    	}else if(statusId == 'DEP_WAITING_APPR'){
								    		return 'aquamarineCell';
								    	}
								    }
								}
							},
							{ text: uiLabelMap.HROccupation, datafield: 'occupation', width: '16%',
								cellclassname: function (row, column, value, data) {
								    if (data.isDependent) {
								    	var statusId = data.statusId
								    	if(statusId == 'DEP_ACCEPT'){
								    		return 'highlightCell';
								    	}else if(statusId == 'DEP_REJECT'){
								    		return 'redCell';
								    	}else if(statusId == 'DEP_WAITING_APPR'){
								    		return 'aquamarineCell';
								    	}
								    }
								}
							},
							{ text: uiLabelMap.PartyPhoneNumber, datafield: 'phoneNumber', width: '12%', columntype: 'numberinput', filtertype: 'number',
								sortable: false,
								createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
									editor.jqxNumberInput({inputMode: 'simple', spinButtons: false});
								},
								cellclassname: function (row, column, value, data) {
								    if (data.isDependent) {
								    	var statusId = data.statusId
								    	if(statusId == 'DEP_ACCEPT'){
								    		return 'highlightCell';
								    	}else if(statusId == 'DEP_REJECT'){
								    		return 'redCell';
								    	}else if(statusId == 'DEP_WAITING_APPR'){
								    		return 'aquamarineCell';
								    	}
								    }
								}
							},
		];
		var grid = $("#viewDetailFamilyGrid");
   		var rendertoolbar = function (toolbar){
   			toolbar.html("");
   			var id = "viewDetailFamilyGrid";
   			var me = this;
   			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.FamilyMembers + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
   			toolbar.append(jqxheader);
   	     	var container = $('#toolbarButtonContainer' + id);
   	        var maincontainer = $("#toolbarcontainer" + id);
   	        //Grid.createAddRowButton(grid, container, uiLabelMap.accAddNewRow, {type: "popup", container: $("#addEmplFamilyWindow")});
			//Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
				//	"", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
   		}; 
   		var columngroups = [{ text: uiLabelMap.IsPersonDependent, align: 'center', name: 'dependentGroup'}];
		var config = {
				width: '100%', 
	      		autoheight: true,
	      		showfilterrow: false,
	      		showtoolbar: true,
	      		rendertoolbar: rendertoolbar,
	      		pageable: true,
	      		sortable: true,
	      		filterable: false,
	      		editable: false,
	      		selectionmode: 'singlerow',
	      		url: '',
	      		virtualmode: true,
	      		columngroups: columngroups,
	      		source: {
	      			pagesize: 10,
	      			deleteColumns: "personFamilyBackgroundId",
	      			removeUrl: "deletePersonFamilyBackground"
	      		}
		};
		Grid.initGrid(config, dataField, columnlist, null, grid);
		Grid.createContextMenu(grid, $("#contextMenuFamily"), false);
	};
	var initEvent = function(){
		$("#emplFamilyWindow").on('close', function(event){
			$("#jqxNotificationviewDetailFamilyGrid").jqxNotification('closeAll');
			$("#viewDetailFamilyGrid").jqxGrid('clearselection');
			refreshBeforeReloadGrid($("#viewDetailFamilyGrid"));
		});
		$("#emplFamilyWindow").on('open', function(event){
			var tmpS = $("#viewDetailFamilyGrid").jqxGrid('source');
			tmpS._source.url = "jqxGeneralServicer?sname=JQGetListEmplFamily&partyId=" + _partyId;
			$("#viewDetailFamilyGrid").jqxGrid('source', tmpS);
		});
		$("#addEmplFamilyWindow").on('createPersonFamilySuccess', function(){
			$("#viewDetailFamilyGrid").jqxGrid('updatebounddata');
		});
		$("#emplFamilyWindow").on('open', function(event){
			familyObject.setData({partyId: _partyId});//familyObject is defined in family.js
		});
		$("#emplFamilyWindow").on('close', function(event){
			familyObject.setData({partyId: null});//familyObject is defined in family.js
		});
	};
	return{
		init: init,
		openWindow: openWindow,
		setData: setData
	}
}());

var familyContextMenuObj = (function(){
	var init = function(){
		createJqxMenu("contextMenuFamily", 30, 180, {popupZIndex: 22000});
		initEvent();
	};
	var initEvent = function(){
		$("#contextMenuFamily").on('itemclick', function (event) {
			var args = event.args;
			var rowindex = $("#viewDetailFamilyGrid").jqxGrid('getselectedrowindex');
            var dataRecord = $("#viewDetailFamilyGrid").jqxGrid('getrowdata', rowindex);
            var action = $(args).attr("action");
            if(action == "approval"){
            	apprFamilyDependentObj.setData(dataRecord);
            	openJqxWindow($("#apprFamilyDependentWindow"));
            }
		});
		$("#contextMenuFamily").on('shown', function (event) {
			var rowindex = $("#viewDetailFamilyGrid").jqxGrid('getselectedrowindex');
			var dataRecord = $("#viewDetailFamilyGrid").jqxGrid('getrowdata', rowindex);
			var isDependent = dataRecord.isDependent;
			if(isDependent){
				$(this).jqxMenu('disable', "approvalFamilyDependent", false);
			}else{
				$(this).jqxMenu('disable', "approvalFamilyDependent", true);
			}
		});
	};
	return{
		init: init
	}
}());

var apprFamilyDependentObj = (function(){
	var _personFamilyBackgroundId;
	var init = function(){
		initSimpleInput();
		initDropDown();
		initRadioButton();
		initWindow();
		create_spinner($("#spinnerFamilyAppr"));
		initEvent();
		initValidator();
	};
	var initSimpleInput = function(){
		$("#familyFullName").jqxInput({width: '96%', height: 20, disabled: true});
		$("#familyBirthDate").jqxDateTimeInput({width: '98%', height: 25});
		$("#familyDeductionStart").jqxDateTimeInput({width: '98%', height: 25});
		$("#familyDeductionEnd").jqxDateTimeInput({width: '98%', height: 25, showFooter: true});
	};
	var initDropDown = function(){
		createJqxDropDownList(partyRelationshipType, $("#familyRelation"), "partyRelationshipTypeId", "partyRelationshipName", 25, '98%');
		createJqxDropDownList(globalVar.dependentStatusList, $("#familyCurrStatus"), "statusId", "description", 25, '98%');
		$("#familyCurrStatus").jqxDropDownList({disabled: true});
	};
	var initRadioButton = function(){
		$("#acceptAppr").jqxRadioButton({ width: '95%', height: 25, checked: false});
		$("#rejectAppr").jqxRadioButton({ width: '95%', height: 25, checked: false});
	};
	var initWindow = function(){
		createJqxWindow($("#apprFamilyDependentWindow"), 485, 390);
	};
	var getData = function(){
		var data = {};
		data.partyRelationshipTypeId = $("#familyRelation").val();
		var birthDate = $("#familyBirthDate").jqxDateTimeInput('val', 'date');
		if(birthDate){
			data.birthDate = birthDate.getTime();
		}
		var dependentStartDate = $("#familyDeductionStart").jqxDateTimeInput('val', 'date');
		if(dependentStartDate){
			data.dependentStartDate = dependentStartDate.getTime();
		}
		var dependentEndDate = $("#familyDeductionEnd").jqxDateTimeInput('val', 'date');
		if(dependentEndDate){
			data.dependentEndDate = dependentEndDate.getTime();
		}
		var accept = $("#acceptAppr").jqxRadioButton('checked');
		var reject = $("#rejectAppr").jqxRadioButton('checked');
		if(accept){
			data.statusId = "DEP_ACCEPT";
		}else if(reject){
			data.statusId = "DEP_REJECT";
		}
		return data;
	};
	var initEvent = function(){
		$("#cancelFamilyAppr").click(function(event){
			$("#apprFamilyDependentWindow").jqxWindow('close');
		});
		$("#saveFamilyAppr").click(function(event){
			var valid = $("#apprFamilyDependentWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var data = getData();
			data.personFamilyBackgroundId = _personFamilyBackgroundId;
			$("#cancelFamilyAppr").attr("disabled", "disabled");
			$("#saveFamilyAppr").attr("disabled", "disabled");
			$("#loadingFamilyAppr").show();
			$.ajax({
				url: 'approvalDependentFamilyEmpl',
				data: data,
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						Grid.renderMessage('viewDetailFamilyGrid', response.successMessage, {autoClose: true,
							template : 'info', appendContainer: "#containerviewDetailFamilyGrid", opacity : 0.9});
						$("#apprFamilyDependentWindow").jqxWindow('close');
						$("#viewDetailFamilyGrid").jqxGrid('updatebounddata');
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
					$("#cancelFamilyAppr").removeAttr("disabled");
					$("#saveFamilyAppr").removeAttr("disabled");
					$("#loadingFamilyAppr").hide();
				}
			});
		});
		$("#apprFamilyDependentWindow").on('close', function(event){
			Grid.clearForm($(this));
			_personFamilyBackgroundId = "";
		});
	};
	var setData = function(data){
		$("#familyFullName").val(data.familyFullName);
		_personFamilyBackgroundId = data.personFamilyBackgroundId;
		if(data.birthDate){
			$("#familyBirthDate").val(data.birthDate);
		}else{
			$("#familyBirthDate").val(null);
		}
		$("#familyCurrStatus").val(data.statusId);
		$("#familyRelation").val(data.partyRelationshipTypeId);
		if(data.dependentStartDate){
			$("#familyDeductionStart").val(data.dependentStartDate);
		}else{
			$("#familyDeductionStart").val(null);
		}
		if(data.dependentEndDate){
			$("#familyDeductionEnd").val(data.dependentEndDate);
		}else{
			$("#familyDeductionEnd").val(null);
		}
	};
	var initValidator = function(){
		$("#apprFamilyDependentWindow").jqxValidator({
			rules: [
				{input : '#rejectAppr', message : uiLabelMap.PleaseSelectOption, action: 'blur', 
					rule : function(input, commit){
						var accept = $("#acceptAppr").jqxRadioButton('checked');
						var reject = $("#rejectAppr").jqxRadioButton('checked');
						if(!accept && !reject){
							return false;
						}
						return true;
					}
				},   
			]
		});
	};
	return{
		init: init,
		setData: setData
	};
}());

$(document).ready(function () {
	familyContextMenuObj.init();
	emplFamilyObj.init();
	apprFamilyDependentObj.init();
});