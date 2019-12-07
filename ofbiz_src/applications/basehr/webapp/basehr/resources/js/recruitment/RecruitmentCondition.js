var recuritmentReqCondObj = (function(){
	var _isEditable = false;
	var _gridEle = null;
	var init = function(gridEle){
		_gridEle = gridEle;
		initJqxGrid();
		initJqxDropDownList();
		initJqxInput();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerRecruitReqCondType"));
	};
	var initJqxGrid = function(){
		var datafield = [
		                 {name: 'recruitmentReqCondTypeId', type: 'string'},
		                 {name: 'recruitmentReqCondTypeName', type: 'string'},
		                 {name: 'conditionDesc', type: 'string'},
		                 ];
		var columns = [
		               {datafield : 'recruitmentReqCondTypeId', hidden: true},
		               {text: uiLabelMap.RecruitmentCriteria, datafield: 'recruitmentReqCondTypeName', width: '40%', editable: false},
		               {text: uiLabelMap.HRCondition, datafield: 'conditionDesc', width: '60%', editable: false,
		            	   cellsrenderer: function(row, columnfield, value, defaulthtml, columnproperties){
		            		   return defaulthtml;
		            	   }
		               }
		               ];
		
		var grid = _gridEle;
		var rendertoolbar = function (toolbar){
			toolbar.html("");
			var id = "recruitReqCondGrid";
			var me = this;
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h5>" + uiLabelMap.RecruitmentRequirementPosition + "</h5><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
	        Grid.createAddRowButton(
	        		grid, container, uiLabelMap.CommonAddNew, {
		        		type: "popup",
		        		container: $("#addRecruitmentRequireCondWindow"),
		        	}
		        );
	        Grid.createDeleteRowButton(grid, container, uiLabelMap.wgdelete, "",
                    "", uiLabelMap.CannotDeleteRow, uiLabelMap.wgdeleteconfirm, uiLabelMap.wgok, uiLabelMap.wgcancel);
		};
		var config = {
				width: '100%',
		   		rowsheight: 25,
		   		autoheight: true,
		   		virtualmode: false,
		   		showfilterrow: false,
		   		selectionmode: 'singlerow',
		   		pageable: true,
		   		sortable: false,
		        filterable: false,
		        editable: false,
		        url: '',    
	   			showtoolbar: true,
	   			rendertoolbar: rendertoolbar,
	        	source: {pagesize: 10, id: 'recruitmentReqCondTypeId'}
		};
		Grid.initGrid(config, datafield, columns, null, _gridEle);
	};
	var initJqxDropDownList = function(){
		createJqxDropDownListBinding($("#recruitReqCondTypeList"), [{name: 'recruitmentReqCondTypeId'}, {name: 'recruitmentReqCondTypeName'}],
				'getRecruitmentReqCondTypeList', "listReturn", "recruitmentReqCondTypeId", "recruitmentReqCondTypeName", "100%", 25);
	};
	var initJqxValidator = function(){
		$("#RecruitReqCondTypeWindow").jqxValidator({
			rules: [	
				{input : '#recruitReqCondTypeNameNew', message : uiLabelMap.FieldRequired, action: 'blur', 
					rule : function(input, commit){
						if(!input.val() || input.val().trim().length <= 0){
							return false;
						}
						return true;
					}
				},
			]
		});
		$("#addRecruitmentRequireCondWindow").jqxValidator({
			rules: [	
			        {input : '#addNewRecruitReqCondType', message : uiLabelMap.FieldRequired, action: 'blur', 
			        	rule : function(input, commit){
			        		var value = $("#recruitReqCondTypeList").val();
			        		if(!value){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        {input : '#conditionDescNew', message : uiLabelMap.FieldRequired, action: 'blur', 
			        	rule : function(input, commit){
			        		if(!input.val() || input.val().trim().length <= 0){
			        			return false;
			        		}
			        		return true;
			        	}
			        },
			        ]
		});
	};
	var initJqxTextArea = function(){
		$("#conditionDescNew").jqxTextArea({ 
    		width: '98%',
            height: 110,
            
        });
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#addRecruitmentRequireCondWindow"), 500, 270, initJqxTextArea);
		createJqxWindow($("#RecruitReqCondTypeWindow"), 400, 150);
		$("#addRecruitmentRequireCondWindow").on('open', function(event){
			if(!_isEditable){
				$("#addRecruitmentRequireCondWindow").jqxWindow('setTitle', uiLabelMap.HRAddCondition);
			}else{
				$("#addRecruitmentRequireCondWindow").jqxWindow('setTitle', uiLabelMap.HREditCondition);
			}
		});
		$("#addRecruitmentRequireCondWindow").on('close', function(event){
			Grid.clearForm($(this));
			_isEditable = false;
		});
		$("#RecruitReqCondTypeWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
	};
	var initJqxInput = function(){
		$("#recruitReqCondTypeNameNew").jqxInput({width: '96%', height: 20});
	};
	var initEvent = function(){
		$("#cancelRecruitReqCondType").click(function(event){
			$("#RecruitReqCondTypeWindow").jqxWindow('close');
		});
		$("#addNewRecruitReqCondType").click(function(event){
			openJqxWindow($("#RecruitReqCondTypeWindow"));
		});
		$("#alterCancel").click(function(event){
			$("#addRecruitmentRequireCondWindow").jqxWindow('close');
		});
		$("#alterSave").click(function(event){
			var valid = $("#addRecruitmentRequireCondWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addRecruitmentRequireCond(true);
		});
		$("#saveAndContinue").click(function(event){
			var valid = $("#addRecruitmentRequireCondWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			addRecruitmentRequireCond(false);
		});
		$("#saveRecruitReqCondType").click(function(event){
			var valid = $("#RecruitReqCondTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			$("#loadingRecruitReqCondType").show();
			$("#saveRecruitReqCondType").attr("disabled", "disabled");
			$("#cancelRecruitReqCondType").attr("disabled", "disabled");
			$("#recruitReqCondTypeNameNew").jqxInput({disabled: true});
			$.ajax({
				url: 'createRecruitmentReqCondType',
				data: {recruitmentReqCondTypeName: $("#recruitReqCondTypeNameNew").val()},
				type: 'POST',
				success: function(response){
					if(response._EVENT_MESSAGE_){
						updateJqxDropDownListBinding($("#recruitReqCondTypeList"), "getRecruitmentReqCondTypeList");
						$("#RecruitReqCondTypeWindow").jqxWindow('close');
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
					$("#loadingRecruitReqCondType").hide();
					$("#saveRecruitReqCondType").removeAttr("disabled");
					$("#cancelRecruitReqCondType").removeAttr("disabled");
					$("#recruitReqCondTypeNameNew").jqxInput({disabled: false});
				}
			});
		});
	};
	var addRecruitmentRequireCond = function(isCloseWindow){
		var selectedItem = $("#recruitReqCondTypeList").jqxDropDownList('getSelectedItem');
		var row = {
				recruitmentReqCondTypeId: selectedItem.value,
				recruitmentReqCondTypeName: selectedItem.label,
				conditionDesc: $("#conditionDescNew").jqxTextArea('val')
		};
		var data = _gridEle.jqxGrid('getrowdatabyid', selectedItem.value);
		if(data){
			_gridEle.jqxGrid('updaterow', selectedItem.value, row);
		}else{
			_gridEle.jqxGrid('addrow', null, row, "last");
		}
		if(isCloseWindow){
			$("#addRecruitmentRequireCondWindow").jqxWindow('close');
		}
	};
	var resetData = function(){
		var source = _gridEle.jqxGrid('source');
		source._source.localdata = [];
		_gridEle.jqxGrid('source', source);
	};
	var setData = function(data){
		var source = _gridEle.jqxGrid('source');
		source._source.localdata = data;
		_gridEle.jqxGrid('source', source);
	};
	var getData = function(){
		var rowData = _gridEle.jqxGrid('getrows');
		return {recruitReqCond: JSON.stringify(rowData)};
	};
	var setGridEle = function(grid){
		_gridEle = grid;
	};
	return{
		init: init,
		resetData: resetData,
		setData: setData,
		getData: getData,
		setGridEle: setGridEle
	}
}());