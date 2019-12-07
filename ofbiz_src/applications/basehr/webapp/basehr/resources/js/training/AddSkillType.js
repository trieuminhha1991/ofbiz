var addNewSkillTypeObj = (function(){
	var init = function(){
		initJqxInput();
		initJqxDropDownList();
		initJqxValidator();
		initEvent();
		initJqxWindow();
		create_spinner($("#spinnerCreateSkillType"));
	};
	
	var initJqxInput = function(){
		$("#newSkillTypeId").jqxInput({width: '96%', height: 20});
		$("#descriptionSkillTypeNew").jqxInput({width: '96%', height: 20});
	};
	var initJqxValidator = function(){
		$("#addNewSkillTypeWindow").jqxValidator({
			rules:[
			       { input: '#descriptionSkillTypeNew', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
			       { input: '#newSkillTypeId', message: uiLabelMap.OnlyContainInvalidChar, action: 'keyup, blur', position: 'topcenter',
			    	   rule: function (input, commit){
			    		   var value = input.val();
			    		   if(value){
			    			   if(/^[a-zA-Z0-9-_]*$/.test(value) == false) {
			    				    return false;
			    				}
			    		   }
			    		   return true;
			    	   }
			       },
			       { input: '#parentNewSkillType', message: uiLabelMap.FieldRequired, action: 'keyup, blur',
			    	   rule: function (input, commit){
			    		   var value = input.val();
			    		   if(!value){
		    				  return false;
			    		   }
			    		   return true;
			    	   }
			       },
            ]
		});
	};
	var initEvent = function(){
		$("#cancelNewSkillType").click(function(event){
			$("#addNewSkillTypeWindow").jqxWindow('close');
		});
		$("#saveNewSkillType").click(function(event){
			var valid = $("#addNewSkillTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var dataSubmit = {};
			if($("#newSkillTypeId").val() && $("#newSkillTypeId").val().trim()){
				dataSubmit.skillTypeId = $("#newSkillTypeId").val(); 
			}
			dataSubmit.description = $("#descriptionSkillTypeNew").val();
			dataSubmit.parentTypeId = $("#parentNewSkillType").val();
			$("#loadingCreateSkillType").show();
			$("#cancelNewSkillType").attr("disabled", "disabled");
			$("#saveNewSkillType").attr("disabled", "disabled");
			$.ajax({
				url: 'createNewSkillType',
				data: dataSubmit,
				type: 'POST',
				success: function(response){
					if(response._EVENT_MESSAGE_){
						$("#addNewSkillTypeWindow").trigger('addSkillTypeSuccess');
						$("#addNewSkillTypeWindow").jqxWindow('close');
					}else{
						bootbox.dialog(response._ERROR_MESSAGE_,
								[{
									"label" : uiLabelMap.CommonClose,
					    		    "class" : "btn-danger btn-small icon-remove open-sans",    			    		    
								}]		
							);
					}
				},
				complete: function(jqXHR, textStatus){
					$("#loadingCreateSkillType").hide();
					$("#cancelNewSkillType").removeAttr("disabled");
					$("#saveNewSkillType").removeAttr("disabled");
				}
			});
		});
		$("#searchParentSkillTypeBtn").click(function(event){
			parentSkillTypeObj.openWindow();
		});
	};
	var initJqxDropDownList = function(){
		var source =
        {
            datatype: "json",
            datafields: [
                { name: 'skillTypeId' },
                { name: 'description' }
            ],
            url: 'getListParentSkillType',
            root: "listReturn",
            type: 'POST',
            async: true,
        };
        var dataAdapter = new $.jqx.dataAdapter(source,{
        	loadComplete: function(records){
        		if(records.listReturn && records.listReturn.length >= 8){
        			$("#parentNewSkillType").jqxDropDownList({autoDropDownHeight: false});
        		}else{
        			$("#parentNewSkillType").jqxDropDownList({autoDropDownHeight: true});
        		}
        	}
        });
        $("#parentNewSkillType").jqxDropDownList({
            source: dataAdapter, displayMember: "description", valueMember: "skillTypeId", width: '101%', height: 25
        });
	};
	var initJqxWindow = function(){
		createJqxWindow($("#addNewSkillTypeWindow"), 400, 230);
		$("#addNewSkillTypeWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
	};
	var updateParentSkillType = function(){
		var source = $("#parentNewSkillType").jqxDropDownList('source');
		$("#parentNewSkillType").jqxDropDownList('clearSelection');
		source._source.url = "getListParentSkillType";
		$("#parentNewSkillType").jqxDropDownList('source', source);
	};
	return{
		init: init,
		updateParentSkillType: updateParentSkillType
	}
}());

var parentSkillTypeObj = (function(){
	var _updateParentSkillTypeList = false;
	var init = function(){
		initJqxGrid();
		initJqxInput();
		initJqxNotification();
		initJqxWindow();
		initEvent();
		initJqxValidator();
	};
	var initJqxNotification = function(){
		$("#jqxNotificationparentSkillTypeGrid").jqxNotification({ width: "100%", 
			appendContainer: "#containerparentSkillTypeGrid", opacity: 0.9, template: "info" });
	};
	var initJqxValidator = function(){
		$("#AddNewParentSkillTypeWindow").jqxValidator({
			rules:[
			       { input: '#descriptionParentSkillTypeNew', message: uiLabelMap.FieldRequired, action: 'keyup, blur', rule: 'required' },
			       { input: '#newParentSkillTypeId', message: uiLabelMap.OnlyContainInvalidChar, action: 'keyup, blur',
			    	   rule: function (input, commit){
			    		   var value = input.val();
			    		   if(value){
			    			   if(/^[a-zA-Z0-9-_]*$/.test(value) == false) {
			    				    return false;
			    				}
			    		   }
			    		   return true;
			    	   }
			       },
            ]
		});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#listParentSkillTypeWindow"), 600, 340);
		createJqxWindow($("#AddNewParentSkillTypeWindow"), 400, 200);
		$("#AddNewParentSkillTypeWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		$("#listParentSkillTypeWindow").on('close', function(event){
			if(_updateParentSkillTypeList){
				addNewSkillTypeObj.updateParentSkillType();
			};
			_updateParentSkillTypeList = false;
		});
	};
	var initJqxInput = function(){
		$("#newParentSkillTypeId").jqxInput({width: '96%', height: 20});
		$("#descriptionParentSkillTypeNew").jqxInput({width: '96%', height: 20});
	};
	var initJqxGrid = function(){
		var datafield = [{name: 'skillTypeId', type: 'string'},
		                 {name: 'description', type: 'string'}];
		var columns = [{text: uiLabelMap.ParentSkillTypeId, datafield: 'skillTypeId', width: '40%', editable: false},
		               {text: uiLabelMap.CommonDescription, datafield: 'description', width: '60%',
							cellvaluechanging: function (row, column, columntype, oldvalue, newvalue) {
				                  // return the old value, if the new value is empty.
				                 if (!newvalue || newvalue.trim() == "") return oldvalue;
				            },
						    validation: function (cell, value) {
						    	if(!value || value.trim().length == 0){
						            return {result: false, message: uiLabelMap.FieldRequired};
						        }
						        return true;
						    }
		               }];
		
		var grid = $("#parentSkillTypeGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "parentSkillTypeGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.ParentSkillTypeList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(
	        		grid, container, uiLabelMap.CommonAddNew, {
		        		type: "popup",
		        		container: $("#AddNewParentSkillTypeWindow"),
		        	}
		        );
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		var config = {
				url: 'JQListParentSkillType',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: true,
				filterable: true,
				localization: getLocalization(),
				source: {
					pagesize: 5,
					createUrl: 'jqxGeneralServicer?jqaction=C&sname=createNewSkillType',
					addColumns: "skillTypeId;description",
					updateUrl: "jqxGeneralServicer?jqaction=U&sname=updateSkillType",
					editColumns: "skillTypeId;description",
					functionAfterUpdate: function(){
						_updateParentSkillTypeList = true;
					},
					functionAfterAddRow: function(){
						_updateParentSkillTypeList = true;
					}
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	var openWindow = function(){
		openJqxWindow($("#listParentSkillTypeWindow"));
	};
	var initEvent = function(){
		$("#cancelNewParentSkillType").click(function(event){
			$("#AddNewParentSkillTypeWindow").jqxWindow('close');
		});
		$("#saveNewParentSkillType").click(function(event){
			var valid = $("#AddNewParentSkillTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			var row = {
				skillTypeId: $("#newParentSkillTypeId").val(),
				description: $("#descriptionParentSkillTypeNew").val()
			};
			$("#parentSkillTypeGrid").jqxGrid('addrow', null, row, 'first');
			$("#AddNewParentSkillTypeWindow").jqxWindow('close');
		});
	};
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	addNewSkillTypeObj.init();
	parentSkillTypeObj.init();
});