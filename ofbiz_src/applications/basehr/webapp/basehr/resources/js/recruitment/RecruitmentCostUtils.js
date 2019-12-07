var recruitCostItemTypeListObj = (function(){
	var _isUpdateRecruitCostItemType = false;
	var _recruitCostItemTypeSelected = "";
	var init = function(){
		initJqxGrid();
		initJqxInput();
		initJqxDropDownList();
		initJqxValidator();
		initEvent();
		initJqxWindow();
		$("#jqxNotificationrecruitCostItemListGrid").jqxNotification({ width: "100%", appendContainer: "#containerrecruitCostItemListGrid", 
			opacity: 0.9, template: "info" });
	};
	
	var initJqxInput = function(){
		$("#recruitCostItemNameNew").jqxInput({width: '96%', height: 20});
		$("#recruitCostItemTypeCommentNew").jqxInput({width: '96%', height: 20});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.recruitmentCostCatTypeArr, $("#recruitCostCatTypeNew"), "recruitCostCatTypeId", "recruitCostCatName", 25, '100%');
	};
	
	var initJqxGrid = function(){
		var datafield = [
		                 {name: 'recruitCostItemTypeId', type: 'string'},
		                 {name: 'recruitCostItemName', type:'string'},
		                 {name: 'recruitCostCatTypeId', type:'string'},
		                 {name: 'comment', type: 'string'}];
		var columns = [
		               {datafield: 'recruitCostItemTypeId', hidden: true},
		               {text: uiLabelMap.RecruitmentCostItemName, datafield: 'recruitCostItemName', width: '30%'},
				       {text: uiLabelMap.RecruitmentCostCategory, datafield: 'recruitCostCatTypeId', width: '30%', columntype: 'dropdownlist',
		            	   createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
		            		   createJqxDropDownList(globalVar.recruitmentCostCatTypeArr, editor, "recruitCostCatTypeId", "recruitCostCatName", cellheight, cellwidth);
		            	   },
		            	   cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties){
		            		   for(var i = 0; i < globalVar.recruitmentCostCatTypeArr.length; i++){
		            			   if(globalVar.recruitmentCostCatTypeArr[i].recruitCostCatTypeId == value){
		            				   return '<span>' + globalVar.recruitmentCostCatTypeArr[i].recruitCostCatName + '</span>';
		            			   }
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   }
				       },
				       {text: uiLabelMap.HRNotes, datafield: 'comment', width: '40%'},
				       ];	
		var grid = $("#recruitCostItemListGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "recruitCostItemListGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentCostReasonList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(
	        		grid, container, uiLabelMap.CommonAddNew, {
		        		type: "popup",
		        		container: $("#addRecruitCostItemTypeWindow"),
		        	}
		        );
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		var config = {
				url: 'JQGetListRecruitmentCostItemType',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: true,
				filterable: true,
				editmode: 'dblclick',
				localization: getLocalization(),
				source: {
					addColumns: 'recruitCostItemName;recruitCostCatTypeId;recruitCostCatName;comment',
					createUrl: 'jqxGeneralServicer?jqaction=C&sname=createRecruitmentCostItemType',
					updateUrl: "jqxGeneralServicer?jqaction=U&sname=updateRecruitmentCostItemType",
					editColumns: "recruitCostItemTypeId;recruitCostItemName;recruitCostCatTypeId;comment",
					functionAfterAddRow: function(){
						_isUpdateRecruitCostItemType = true
					},
					functionAfterUpdate: function(){
						_isUpdateRecruitCostItemType = true
					},
					pagesize: 10
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#RecruitCostItemTypeListWindow"), 600, 470);
		createJqxWindow($("#addRecruitCostItemTypeWindow"), 475, 220);
		$("#addRecruitCostItemTypeWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		
		$("#RecruitCostItemTypeListWindow").on('close', function(event){
			$("#recruitCostItemListGrid").jqxGrid('clearselection');
			editRecruitmentCostItemObj.updateRecruitmentCostItemType(_isUpdateRecruitCostItemType, _recruitCostItemTypeSelected);
			_isUpdateRecruitCostItemType = false;
			_recruitCostItemTypeSelected = "";
		});
	};
	
	var initEvent = function(){
		$("#searchRecCostCatBtn").click(function(event){
			recruitCostCatTypeListObj.openWindow();
			$("#addRecruitCostItemTypeWindow").jqxValidator('hide');
		});
		$("#cancelCreateCostItemType").click(function(event){
			$("#addRecruitCostItemTypeWindow").jqxWindow('close');
		});
		$("#saveCreateCostItemType").click(function(event){
			var valid = $("#addRecruitCostItemTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			createRecruitCostItemType();
			$("#addRecruitCostItemTypeWindow").jqxWindow('close');
		});
		$("#saveAndContinueCostItemType").click(function(event){
			var valid = $("#addRecruitCostItemTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			createRecruitCostItemType();
		});
		
		$("#cancelSelectCostItemType").click(function(event){
			$("#RecruitCostItemTypeListWindow").jqxWindow('close');
		});
		$("#saveSelectCostItemType").click(function(event){
			var index = $("#recruitCostItemListGrid").jqxGrid('getselectedrowindex');
			if(index > -1){
				var data = $("#recruitCostItemListGrid").jqxGrid('getrowdata', index);
				_recruitCostItemTypeSelected = data.recruitCostItemTypeId;
				$("#RecruitCostItemTypeListWindow").jqxWindow('close');
			} else {
				jOlbUtil.alert.error(uiLabelMap.NotChooseReasonYet);
				return false;
			}
		});
	};
	
	var createRecruitCostItemType = function(){
		var row = {
				recruitCostItemName: $("#recruitCostItemNameNew").val(),
				recruitCostCatTypeId: $("#recruitCostCatTypeNew").val(),
				comment: $("#recruitCostItemTypeCommentNew").val()
		};
		$("#recruitCostItemListGrid").jqxGrid('addrow', null, row, 'first');
	};
	
	var openWindow = function(){
		openJqxWindow($("#RecruitCostItemTypeListWindow"));
	};
	
	var initJqxValidator = function(){
		$("#addRecruitCostItemTypeWindow").jqxValidator({
			rules: [
			        {input : '#recruitCostItemNameNew', message : uiLabelMap.FieldRequired, action : 'blur',
			        	rule : function(input, commit){
			        		if(!input.val()){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#recruitCostItemNameNew', message : uiLabelMap.InvalidChar, action : 'blur',
			        	rule : function(input, commit){
			        		var val = input.val();
			        		if(val){
			        			if(validationNameWithoutHtml(val)){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        },
			        {input : '#searchRecCostCatBtn', message: uiLabelMap.FieldRequired, action : 'valuechanged',
			        	rule : function(input, commit){
			        		var value = $("#recruitCostCatTypeNew").val();
			        		if(!value){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {
			        	input : '#recruitCostItemTypeCommentNew', message : uiLabelMap.InvalidChar, action : 'blur',
			        	rule : function(input, commit){
			        		var val = input.val();
			        		if(val){
			        			if(validationNameWithoutHtml(val)){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			        }
	        ]
		});
	};
	
	var updateRecruitCostCatType = function(refresh, recruitCostCatTypeId){
		if(refresh){
			$.ajax({
				url: 'getRecruitmentCostCategoryType',
				type: 'POST',
				success: function(response){
					if(response.listReturn){
						globalVar.recruitmentCostCatTypeArr = response.listReturn;
						updateSourceDropdownlist($("#recruitCostCatTypeNew"), globalVar.recruitmentCostCatTypeArr);
					}
				},
				complete: function(jqXHR, textStatus){
					if(typeof(recruitCostCatTypeId) != 'undefined' && recruitCostCatTypeId.length > 0){
						$("#recruitCostCatTypeNew").val(recruitCostCatTypeId);	
					}
				}
			});
		}else{
			if(typeof(recruitCostCatTypeId) != 'undefined' && recruitCostCatTypeId.length > 0){
				$("#recruitCostCatTypeNew").val(recruitCostCatTypeId);	
			}
		}
	};
	
	return{
		init: init,
		updateRecruitCostCatType: updateRecruitCostCatType,
		openWindow: openWindow
	}
}());

var recruitCostCatTypeListObj = (function(){
	var _isUpdateRecruitCostCatType = false;
	var _recruitCostCatTypeSelected = "";
	var init = function(){
		initJqxGrid();
		initJqxInput();
		initJqxValidator();
		initEvent();
		initJqxWindow();
		$("#jqxNotificationrecruitCostCatTypeListGrid").jqxNotification({ width: "100%", appendContainer: "#containerrecruitCostCatTypeListGrid", 
			opacity: 0.9, template: "info" });
	};
	
	var initJqxGrid = function(){
		var datafield = [{name: 'recruitCostCatTypeId', type: 'string'},
		                 {name: 'recruitCostCatName', type:'string'},
		                 {name: 'comment', type: 'string'}];
		var columns = [{datafield: 'recruitCostCatTypeId', hidden: true},
		               {text: uiLabelMap.RecruitmentCostCategoryType, datafield: 'recruitCostCatName', width: '50%'},
				       {text: uiLabelMap.HRNotes, datafield: 'comment', width: '50%'},
				       ];
		var grid = $("#recruitCostCatTypeListGrid");
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "recruitCostCatTypeListGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentCostCategoryTypeList + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(
	        		grid, container, uiLabelMap.CommonAddNew, {
		        		type: "popup",
		        		container: $("#addRecruitCostCatTypeWindow"),
		        	}
		        );
	        Grid.createFilterButton(grid, container, uiLabelMap.accRemoveFilter);
		};
		var config = {
				url: 'JQGetListRecruitmentCatType',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: true,
				filterable: true,
				editmode: 'dblclick',
				localization: getLocalization(),
				source: {
					addColumns: 'recruitCostCatName;comment',
					createUrl: 'jqxGeneralServicer?jqaction=C&sname=createRecruitmentCostCatType',
					updateUrl: "jqxGeneralServicer?jqaction=U&sname=updateRecruitmentCostCatType",
					editColumns: "recruitCostCatTypeId;recruitCostCatName;comment",
					pagesize: 5,
					functionAfterAddRow: function(){
						_isUpdateRecruitCostCatType = true
					},
					functionAfterUpdate: function(){
						_isUpdateRecruitCostCatType = true
					}
				}
		};
		Grid.initGrid(config, datafield, columns, null, grid);
	};
	
	var initJqxInput = function(){
		$("#recruitCostCatNameNew").jqxInput({width: '96%', height: 20});
		$("#recruitCostCatTypeCommentNew").jqxInput({width: '96%', height: 20});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#addRecruitCostCatTypeWindow"), 450, 185);
		createJqxWindow($("#recruitCostCatTypeWindow"), 600, 370);
		$("#addRecruitCostCatTypeWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
		
		$("#recruitCostCatTypeWindow").on('close', function(event){
			$("#recruitCostCatTypeListGrid").jqxGrid('clearselection');
			//recruitCostItemTypeListObj is defined in RecruitmentCostUtils.js
			recruitCostItemTypeListObj.updateRecruitCostCatType(_isUpdateRecruitCostCatType, _recruitCostCatTypeSelected);
			_isUpdateRecruitCostCatType = false;
			_recruitCostCatTypeSelected = "";
		});
	};
	
	var initEvent = function(){
		$("#saveSelectCostCatType").click(function(event){
			var index = $("#recruitCostCatTypeListGrid").jqxGrid('getselectedrowindex');
			if(index > -1){
				var data = $("#recruitCostCatTypeListGrid").jqxGrid('getrowdata', index);
				_recruitCostCatTypeSelected = data.recruitCostCatTypeId;
				$("#recruitCostCatTypeWindow").jqxWindow('close');
			}
		});
		
		$("#cancelCreateCostCatType").click(function(event){
			$("#addRecruitCostCatTypeWindow").jqxWindow('close');
		});
		$("#saveCreateCostCatType").click(function(event){
			var valid = $("#addRecruitCostCatTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			createRecruitCostCatType();
			$("#addRecruitCostCatTypeWindow").jqxWindow('close');
		});
		$("#saveAndContinueCostCatType").click(function(event){
			var valid = $("#addRecruitCostCatTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			createRecruitCostCatType();
		});
		
		$("#cancelSelectCostCatType").click(function(event){
			$("#recruitCostCatTypeWindow").jqxWindow('close');
		});
	};
	
	var initJqxValidator = function(){
		$("#addRecruitCostCatTypeWindow").jqxValidator({
			rules: [
			        {input : '#recruitCostCatNameNew', message : uiLabelMap.FieldRequired, action : 'blur',
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
	
	var createRecruitCostCatType = function(){
		var row = {
				recruitCostCatName: $("#recruitCostCatNameNew").val(),
				comment: $("#recruitCostCatTypeCommentNew").val()
		};
		$("#recruitCostCatTypeListGrid").jqxGrid('addrow', null, row, 'first');
	};
	
	var openWindow = function(){
		openJqxWindow($("#recruitCostCatTypeWindow"));
	};
	
	return{
		init: init,
		openWindow: openWindow
	}
}());

var editRecruitmentCostItemObj = (function(){
	var _isEditMode = false;
	var _createRecruitmentCostItem = null;
	var _updateRecruitmentCostItem = null;
	var init = function(){
		initJqxDropDownList();
		initJqxNumberInput();
		initJqxInput();
		initJqxWindow();
		initJqxValidator();
		initEvent();
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.recruitmentCostItemArr, $("#recruitCostItemNew"), "recruitCostItemTypeId", "recruitCostItemName", 25, '101%');
		$("#recruitCostItemNew").on('select', function(event){
			var args = event.args;
			if (args) {
				var index = args.index;
				var dataRecord = globalVar.recruitmentCostItemArr[index];
				if(dataRecord){
					$("#recruitCostCatId").val(dataRecord.recruitCostCatName);
				}
			}
		});
	};
	
	var initJqxNumberInput = function(){
		$("#recruitCostItemAmount").jqxNumberInput({ width: '97%', height: '25px', min:0, max: 9999999999, decimalDigits: 0, spinButtons: true });
	};
	
	var initJqxInput = function(){
		$("#recruitCostComment").jqxInput({width: '95%', height: 20});
		$("#recruitCostCatId").jqxInput({width: '95%', height: 20, disabled: true, displayMember: 'recruitCostCatName', valueMember: 'recruitCostCatId'});
	};
	
	var initJqxValidator = function(){
		$("#addRecruitmentCostWindow").jqxValidator({
			rules:  [
				        {input : '#searchRecCostBtn', message : uiLabelMap.FieldRequired, action : 'valuechanged',
				        	rule : function(input, commit){
				        		var value = $("#recruitCostItemNew").val();
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
		$("#searchRecCostBtn").click(function(event){
			//recruitCostItemTypeListObj is defined in RecruitmentCostUtils.js
			recruitCostItemTypeListObj.openWindow();
			$("#addRecruitmentCostWindow").jqxValidator('hide');
		});
		$("#cancelCreateCost").click(function(event){
			$("#addRecruitmentCostWindow").jqxWindow('close');
		});
		$("#saveCreateCost").click(function(event){
			var valid = $("#addRecruitmentCostWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			if(!_isEditMode && typeof(_createRecruitmentCostItem) == "function"){
				_createRecruitmentCostItem(true);
			}else if(typeof(_updateRecruitmentCostItem) == "function"){
				_updateRecruitmentCostItem();
			}
		});
		$("#saveAndContinueCost").click(function(event){
			var valid = $("#addRecruitmentCostWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			if(!_isEditMode && typeof(_createRecruitmentCostItem) == "function"){
				_createRecruitmentCostItem(false);;
			}else if(typeof(_updateRecruitmentCostItem) == "function"){
				_updateRecruitmentCostItem();
			}
		});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#addRecruitmentCostWindow"), 475, 270);
		$("#addRecruitmentCostWindow").on('close', function(event){
			Grid.clearForm($(this));
			_isEditMode = false;
		});
		$("#addRecruitmentCostWindow").on('open', function(event){
			if(!_isEditMode){
				$(this).jqxWindow('setTitle', uiLabelMap.AddRecruitmentCost);
			}else{
				$(this).jqxWindow('setTitle', uiLabelMap.EditRecruitmentCost);
			}
		});
	};
	
	var updateRecruitmentCostItemType = function(refresh, recruitCostItemTypeId){
		if(refresh){
			$.ajax({
				url: 'getRecruitmentCostItemType',
				type: 'POST',
				success: function(response){
					if(response.listReturn){
						globalVar.recruitmentCostItemArr = response.listReturn;
						updateSourceDropdownlist($("#recruitCostItemNew"), globalVar.recruitmentCostItemArr);
						$("#recruitCostCatId").val("");
					}
				},
				complete: function(jqXHR, textStatus){
					if(typeof(recruitCostItemTypeId) != 'undefined' && recruitCostItemTypeId.length > 0){
						$("#recruitCostItemNew").val(recruitCostItemTypeId);	
					}
				}
				
			});
		}else{
			if(typeof(recruitCostItemTypeId) != 'undefined' && recruitCostItemTypeId.length > 0){
				$("#recruitCostItemNew").val(recruitCostItemTypeId);
			}
		}
	};
	
	var setEditMode = function(value){
		_isEditMode = value;
	};
	var setCreateRecruitmentCostItem = function(createRecruitmentCostItem){
		_createRecruitmentCostItem = createRecruitmentCostItem;
	};
	var setUpdateRecruitmentCostItem = function(updateRecruitmentCostItem){
		_updateRecruitmentCostItem = updateRecruitmentCostItem;
	};
	return{
		init: init,
		setEditMode: setEditMode,
		updateRecruitmentCostItemType: updateRecruitmentCostItemType,
		setCreateRecruitmentCostItem: setCreateRecruitmentCostItem,
		setUpdateRecruitmentCostItem: setUpdateRecruitmentCostItem,
	}
}());

$(document).ready(function(){
	recruitCostItemTypeListObj.init();
	recruitCostCatTypeListObj.init();
	editRecruitmentCostItemObj.init();
});