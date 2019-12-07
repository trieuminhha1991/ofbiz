var editKPIObj = (function(){
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
		$("#CriteriaName").jqxInput({width: '95%', height: 20});
	};
	var initJqxWindow = function(){
		var initContent = function(){
			initJqxEditor();
		}
		createJqxWindow($("#" + globalVar.newKPIWindow), 660, 550, initContent);
		$("#" + globalVar.newKPIWindow).on('open', function(event){
			if(_editMode){
				$(this).jqxWindow('setTitle', uiLabelMap.EditKPI);
				setData(_rowData);
			}else{
				$(this).jqxWindow('setTitle', uiLabelMap.CreateNewKPI);
			}
		});
		$('#' + globalVar.newKPIWindow).bind('close', function(){
			_editMode = false;
			wizardObj.resetStep();
			$("#createNewKpi" + globalVar.newKPIWindow).jqxValidator('hide');
			$('#CriteriaType').jqxDropDownList('clearSelection');
			$('#CriteriaName').jqxInput('val', null);
			$('#descriptionKPI').jqxEditor('val','');
			$('#periodTypeNew').jqxDropDownList('clearSelection');
			$('#targetNumberNew').jqxNumberInput('val', null);
			$('#uomIdNew').jqxDropDownList('clearSelection');
			$('#perfCriDevelopmetTypeNew').jqxDropDownList('clearSelection');
			$('#fromDate').jqxDateTimeInput({value : new Date()});
			$('#thruDate').jqxDateTimeInput({value : new Date()});
			$('#setupGrid').jqxGrid('clear');
		})
	};
	var initJqxPanel = function(){
		$("#contentPanel").jqxPanel({width: '99.5%', height: 380, scrollBarSize: 15});
	};
	
	var setData = function(data){
		$("#CriteriaType").val(data.perfCriteriaTypeId);
		$("#CriteriaName").val(data.criteriaName);
		$("#descriptionKPI").val(data.description);
		$("#periodTypeNew").val(data.periodTypeId);
		$("#targetNumberNew").val(data.target);
		$("#uomIdNew").val(data.uomId);
		$("#perfCriDevelopmetTypeNew").val(data.perfCriDevelopmetTypeId);
		policyKPI.refreshGridData(data);
	};
	
	var initBtnEvent = function(){
		$("#viewListCriteriaTypeBtn").click(function(event){
			editCriteriaTypeObj.openWindow();
		});
	};
	
	var getData = function(){
		var data = {};
		data.criteriaName = $("#CriteriaName").val();
		data.perfCriteriaTypeId = $("#CriteriaType").val();
		data.description = $("#descriptionKPI").val();
		data.periodTypeId = $("#periodTypeNew").val();
		data.target = $("#targetNumberNew").val();
		data.uomId = $("#uomIdNew").val();
		data.perfCriDevelopmetTypeId = $("#perfCriDevelopmetTypeNew").val();
		return data;
	};
	
	var updateKPI = function(){
		var dataUpdate = getData();
		dataUpdate.criteriaId = _rowData.criteriaId;
		$("#jqxgrid").jqxGrid('updaterow', _rowData.uid, dataUpdate);
		$("#" + globalVar.newKPIWindow).jqxWindow('close');
	};
	
	var createKPI = function(){
		var row = getData();
		$("#jqxgrid").jqxGrid('addrow', null, row, 'first');
		$("#" + globalVar.newKPIWindow).jqxWindow('close');
	};
	
	var initJqxEditor = function(){
		$("#descriptionKPI").jqxEditor({ 
    		width: '97%',
            theme: 'olbiuseditor',
            tools: '',
            height: 100,
        });
	};
	
	var initJqxNumberInput = function(){
		$("#targetNumberNew").jqxNumberInput({ width: '97%', height: '25px',  spinButtons: true, max: 999999999999, digits: 11});
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownListBinding($("#uomIdNew"), [{name: "uomId"}, {name: "description"}, {name: "abbreviation"}], 'getKPIUomList', 
				"listReturn", "uomId", "abbreviation", '100%', 25);
		createJqxDropDownList(globalVar.periodTypeArr, $("#periodTypeNew"), "periodTypeId", "description", 25, '97%');
		createJqxDropDownList(globalVar.perfCriDevelopmentTypeArr, $("#perfCriDevelopmetTypeNew"), "perfCriDevelopmetTypeId", "perfCriDevelopmetName", 25, '97%');
		createJqxDropDownListBinding($("#CriteriaType"), [{name: 'perfCriteriaTypeId'}, {name: 'description'}], 
				"getPerfCriteriaType", "listReturn", "perfCriteriaTypeId", "description", "100%", 25);
		$("#CriteriaType").on('bindingComplete', function(event){
			if(editCriteriaTypeObj.getPerfCriteriaTypeIdSelected()){
				$("#CriteriaType").val(editCriteriaTypeObj.getPerfCriteriaTypeIdSelected())
			}
		});
	};
	var initJqxValidator = function(){
		$("#createNewKpi" + globalVar.newKPIWindow).jqxValidator({
			rules : [
					{input : '#CriteriaName', message : uiLabelMap.FieldRequired, action : 'blur',
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{input : '#CriteriaType', message : uiLabelMap.FieldRequired, action: 'blur', 
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{input : '#periodTypeNew', message : uiLabelMap.FieldRequired, action: 'blur', 
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{input : '#periodTypeNew', message : uiLabelMap.FieldRequired, action: 'blur', 
						rule : function(input, commit){
							if(!input.val()){
								return false;
							}
							return true;
						}
					},
					{input : '#addNewUomId', message : uiLabelMap.FieldRequired, action: 'blur', 
						rule : function(input, commit){
							if(!$("#uomIdNew").val()){
								return false;
							}
							return true;
						}
					},
					{input : '#perfCriDevelopmetTypeNew', message : uiLabelMap.FieldRequired, action: 'blur', 
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
		updateJqxDropDownListBinding($("#CriteriaType"), 'getPerfCriteriaType');
		updateJqxDropDownListBinding($('#CriteriaType_Detail'), 'getPerfCriteriaType');
	};
	
	var validate = function(){
		return $("#createNewKpi" + globalVar.newKPIWindow).jqxValidator('validate');
	};
	
	var closeWindow = function(){
		$("#" + globalVar.newKPIWindow).jqxWindow('close');
	};
	
	return {
		init : init,
		updateCriteriaTypeDropDownList: updateCriteriaTypeDropDownList,
		validate : validate,
		getData : getData,
		closeWindow : closeWindow
	}
}());

var editCriteriaTypeObj = (function(){
	var _perfCriteriaTypeId = null;
	var init = function(){
		initJqxInput();
		initJqxGrid();
		initBtnEvent();
		initJqxValidator();
		initJqxNotification();
		initJqxWindow();
	};
	
	var initJqxNotification = function(){
		$("#jqxNotificationgridCriteriaType").jqxNotification({ width: "100%", appendContainer: "#containergridCriteriaType", opacity: 0.9, template: "info" });
	};
	
	var initBtnEvent = function(){
		$("#cancelCreateKpiType").click(function(event){
			$("#createCriteriaTypeWindow").jqxWindow('close');
		});
		$("#saveCreateKpiType").click(function(event){
			var valid = $("#createCriteriaTypeWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.CannotDeleteAfterCreate,
				[{
					"label" : uiLabelMap.CommonSubmit,
	    		    "class" : "btn-primary btn-small icon-ok open-sans",
	    		    "callback": function() {
	    		    	$("#saveCreateKpiType").attr("disabled", "disabled");
	    				var row = {
								perfCriteriaTypeId: $('#CriteriaTypeId').val(),
	    						description: $('#CriteriaTypeName').val()
	    				};
	    				$('#gridCriteriaType').jqxGrid('addrow', null, row, 'first');
	    				$("#createCriteriaTypeWindow").jqxWindow('close');
	    				$("#saveCreateKpiType").removeAttr("disabled");
	    		    }	
				},
				{
	    		    "label" : uiLabelMap.CommonClose,
	    		    "class" : "btn-danger btn-small icon-remove open-sans",
	    		}]		
			);
			
		});
		$("#cancelSelectListCriteriaType").click(function(event){
			$("#listCriteriaTypeWindow").jqxWindow('close');
		});
		$("#chooseCriteriaTypeSelected").click(function(event){
			var index = $("#gridCriteriaType").jqxGrid('getselectedrowindex');
			if(index > -1){
				var data = $("#gridCriteriaType").jqxGrid('getrowdata', index);
				_perfCriteriaTypeId = data.perfCriteriaTypeId;
			}
			$("#listCriteriaTypeWindow").jqxWindow('close');
		});
	};
	
	var initJqxValidator = function(){
		$('#createCriteriaTypeWindow').jqxValidator({
			rules : [
			      	{input: '#CriteriaTypeName', message : uiLabelMap.FieldRequired, action : 'blur', rule : 'required'},
					{input: '#CriteriaTypeId', message : uiLabelMap.FieldRequired, action : 'blur', rule : 'required'}
	         ]
		});
	};
	
	var initJqxWindow = function(){
		createJqxWindow($("#listCriteriaTypeWindow"), 680, 340);
		$("#listCriteriaTypeWindow").on('close', function(event){
			editKPIObj.updateCriteriaTypeDropDownList();
			$("#gridCriteriaType").jqxGrid('clearselection');
		});
		createJqxWindow($("#createCriteriaTypeWindow"), 500, 150);
		$("#createCriteriaTypeWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
	};
	
	var initJqxInput = function(){
		$('#CriteriaTypeName').jqxInput({width: '95%', height: 20});
	};
	
	var initJqxGrid = function(){
		var grid = $('#gridCriteriaType');
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "gridCriteriaType";
			var jqxheader = $("<div id='toolbarcontainer" + id +"' class='widget-header'><h4>" + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>");
			toolbar.append(jqxheader);
	     	var container = $('#toolbarButtonContainer' + id);
	        var maincontainer = $("#toolbarcontainer" + id);
            //comment hide button new NewCriteriaType
	        Grid.createAddRowButton(
	        	grid, container, uiLabelMap.CommonAddNew, {
	        		type: "popup",
	        		container: $("#createCriteriaTypeWindow"),
	        	}
	        );
		};
		
		var dataFieldCriteriaType = [
		                             {name : 'perfCriteriaTypeId', type : 'String'},
		                             {name : 'description', type : 'String'}
		                     ];
		var columnCriteriaType = [
	          {text : uiLabelMap.CriteriaTypeId, dataField : 'perfCriteriaTypeId', width : '30%', editable: false},
	          {text : uiLabelMap.CommonDescription, dataField : 'description'}
	    ];
		
		var config = {
				url: 'GetListCriteriaType',
				rendertoolbar : rendertoolbar,
				showtoolbar : true,
				width : '100%',
				virtualmode: true,
				editable: true,
				editmode: 'dblclick',
				localization: getLocalization(),
				source: {
					addColumns: 'perfCriteriaTypeId;description',
					createUrl: 'jqxGeneralServicer?jqaction=C&sname=createNewCriteriaType',
					updateUrl: "jqxGeneralServicer?jqaction=U&sname=updateCriteriaType",
					editColumns: "perfCriteriaTypeId;description",
					pagesize: 5
				}
		};
		Grid.initGrid(config, dataFieldCriteriaType, columnCriteriaType,null, $('#gridCriteriaType'));
	};
	var openWindow = function(){
		openJqxWindow($("#listCriteriaTypeWindow"));
	};
	var getPerfCriteriaTypeIdSelected = function(){
		return _perfCriteriaTypeId;
	};
	var setPerfCriteriaTypeIdSelected = function(value){
		_perfCriteriaTypeId = value;
	};
	return{
		init: init,
		getPerfCriteriaTypeIdSelected: getPerfCriteriaTypeIdSelected,
		setPerfCriteriaTypeIdSelected: setPerfCriteriaTypeIdSelected,
		openWindow: openWindow
	}
}());
var uomIdKPIObj = (function(){
	var init = function(){
		initJqxInput();
		initJqxWindow();
		initEvent();
		initJqxValidator();
		create_spinner($("#spinnerUom"));
	};
	var initJqxWindow = function(){
		createJqxWindow($("#uomIdKPIWindow"), 400, 140);
	};
	var initJqxInput = function(){
		$("#uomAbbreviation").jqxInput({width: '96%', height: 20});
	};
	var initJqxValidator = function(){
		$('#uomIdKPIWindow').jqxValidator({
			rules : [
			      {input: '#uomAbbreviation', message : uiLabelMap.FieldRequired, action : 'blur', rule : 'required'}
	         ]
		});
	};
	var initEvent = function(){
		$("#addNewUomId").click(function(event){
			openJqxWindow($("#uomIdKPIWindow"));
		});
		$('#addNewUomId_Detail').click(function(){
			openJqxWindow($("#uomIdKPIWindow"));
		});
		
		$("#cancelAddUom").click(function(event){
			$("#uomIdKPIWindow").jqxWindow('close');
		});
		$("#saveAddUom").click(function(event){
			var valid = $('#uomIdKPIWindow').jqxValidator('validate');
			if(!valid){
				return;
			}
			$("#loadingUom").show();
			$("#cancelAddUom").attr("disabled", "disabled");
			$("#saveAddUom").attr("disabled", "disabled");
			$.ajax({
				url: 'createKPIUom',
				data: {abbreviation: $("#uomAbbreviation").val()},
				type: 'POST',
				success: function(response){
					if(response.responseMessage == "success"){
						var source = $("#uomIdNew").jqxDropDownList('source');
						source._source.url = "getKPIUomList";
						$("#uomIdNew").jqxDropDownList('source', source);
						
						var source_detail = $("#uomIdNew_Detail").jqxDropDownList('source');
						source_detail._source.url = "getKPIUomList";
						$("#uomIdNew_Detail").jqxDropDownList('source', source_detail);
						$("#uomIdKPIWindow").jqxWindow('close');
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
					$("#loadingUom").hide();
					$("#cancelAddUom").removeAttr("disabled");
					$("#saveAddUom").removeAttr("disabled");
				}
			});
		});
		$("#uomIdKPIWindow").on('close', function(event){
			Grid.clearForm($(this));
		});
	};
	return{
		init: init
	}
}());

var policyKPI = (function(){

	var init = function(){
		initJqxDateTime();
		initGrid();
		initValidate();
	};
	
	var initJqxDateTime = function(){
		$('#fromDate').jqxDateTimeInput({width : '200px', height : '25px'});
		$('#thruDate').jqxDateTimeInput({width : '200px', height : '25px', clearString:'Clear', showFooter:true,});
	};
	
	var initGrid = function(){
		
		var rendertoolbar = function(toolbar){
			var jqxheader = $("<div id='toolbarcontainerPolicy' class='widget-header'><h5><b>" + uiLabelMap.SetupKPIPolicy + "</b></h5><div id='toolbarButtonContainer' class='pull-right'></div></div>");
    		toolbar.append(jqxheader);
    		var container = $('#toolbarButtonContainer');
    		var grid = $("#setupGrid");
    		Grid.createAddRowButton(grid, container, uiLabelMap.CommonAddNew, {type: "popup", container: $("#setupKpiPolicyWindow")});
		};
		
		var config = {
				width: '100%', 
	      		height: 220,
	      		autoheight: false,
	      		virtualmode : true,
	      		showfilterrow: false,
	      		showtoolbar: true,
	      		rendertoolbar: rendertoolbar,
	      		pageable: true,
	      		sortable: false,
	      		filterable: false,
	      		editable: false,
	      		url: '',
		};
		var column = [
	              {datafield : 'kpiPolicyEnumId', hidden : 'true'},
	              {text : uiLabelMap.KPIFromRating, datafield : 'fromRating', width : '25%',
	            	  	cellsrenderer : function(row, column, value){
	            	  		var val = value;
	            	  		if(value){
	            	  			val = formatnumber(value);
	            	  		}
	            	  		return '<span>' + val + '</span>';
	            	  	},
	              },
	              {text : uiLabelMap.KPIToRating, datafield : 'toRating', width : '25%',
	            	  	cellsrenderer : function(row, column, value){
	            	  		var val = value;
	            	  		if(value){
	            	  			val = formatnumber(value);
	            	  		}
	            	  		return '<span>' + val + '</span>';
	            	  	},
	              },
	              {text : uiLabelMap.HRCommonAmount, datafield : 'amount', width : '25%',
	            	  	cellsrenderer : function(row, column, value){
	            	  		var val = value;
	            	  		if(value){
	            	  			val = formatnumber(value);
	            	  		}
	            	  		return '<span>' + val + '</span>';
	            	  	},
	              },
	              {text : uiLabelMap.RewardPunishment, datafield : 'description', width : '25%'}
        ];
		var datafield = [
                 {name : 'fromRating', type : 'number'},
                 {name : 'toRating', type : 'number'},
                 {name : 'amount', type : 'number'},
                 {name : 'kpiPolicyEnumId', type : 'string'},
                 {name : 'description', type : 'string'}
         ];
		
		 Grid.initGrid(config, datafield, column, null, $('#setupGrid'));
	};
	
	var refreshGridData = function(data){
		var tmpSrc = $('#setupGrid').jqxGrid('source');
		tmpSrc._source.url = "jqxGeneralServicer?sname=getKpiPolicyItem&criteriaId=" + data.criteriaId;
		$('#setupGrid').jqxGrid('source', tmpSrc);
	};
	
	var initValidate = function(){
		$('#kpiPolicySetup' + globalVar.newKPIWindow).jqxValidator({
			rules : [
			         {
			        	 input : '#fromDate', action : 'blur', message : uiLabelMap.FieldRequired,
			        	 rule : function(input, commit){
			        		 if(!input.jqxDateTimeInput('getDate')){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         },
			         {
			        	input : '#fromDate', action : 'blur', message : uiLabelMap.FromDateLessThanEqualThruDate,
			        	rule : function(input, commit){
			        		if($('#thruDate').jqxDateTimeInput('getDate')){
			        			if(input.jqxDateTimeInput('getDate') > $('#thruDate').jqxDateTimeInput('getDate')){
			        				return false;
			        			}
			        		}
			        		return true;
			        	}
			         },
			         {
			        	 input : '#pagersetupGrid', message : uiLabelMap.FieldRequired,
			        	 rule : function(input, commit){
			        		 var data = $('#setupGrid').jqxGrid('getrows');
			        		 if(!data.length){
			        			 return false;
			        		 }
			        		 return true;
			        	 }
			         }
			         
         ]
		});
	};
	var validate = function(){
		return $('#kpiPolicySetup' + globalVar.newKPIWindow).jqxValidator('validate');
	};
	
	var getData = function(){
		var data = {};
		data.fromDate = $('#fromDate').jqxDateTimeInput('getDate').getTime();
		if($('#thruDate').jqxDateTimeInput('getDate')){
			data.thruDate = $('#thruDate').jqxDateTimeInput('getDate').getTime();
		}else{
			data.thruDate = null;
		}
		data.setup = $('#setupGrid').jqxGrid('getrows');
		
		return data;
	};
	
	return {
		init : init,
		validate : validate,
		getData : getData,
		refreshGridData : refreshGridData
	}
}());

var setupPolicy = (function(){
	var editmode = false;
	var init = function(){
		initWindow();
		initNumberInput();
		initDropDown();
		initValidate();
		initBtnEvent();
		initWindowEvent();
	};
	
	var initWindow = function(){
		createJqxWindow($('#setupKpiPolicyWindow'), 375, 260);
	};
	
	var initNumberInput = function(){
		$('#fromRating').jqxNumberInput({width : '98%', height : '22px', symbolPosition: 'right', symbol: '%', spinButtons: true, allowNull: true });
		$('#toRating').jqxNumberInput({width : '98%', height : '22px', symbolPosition: 'right', symbol: '%', spinButtons: true, allowNull: true });
		$('#amount').jqxNumberInput({width : '98%', height : '22px', spinButtons: true });
	};
	
	
	initDropDown = function(){
		createJqxDropDownList(globalVar.enumKpi, $('#status'), 'enumId' , 'description', '25px', '98%');
	};
	
	var initValidate = function(){
		$('#setupKpiPolicyWindow').jqxValidator({
			rules : [
		         {input : '#fromRating', action : 'blur', message : uiLabelMap.FieldRequired, rule :
		        	 	function(input, commit){
		        	 		if(input.val() === null){
		        	 			return false;
		        	 		}
		        	 		return true;
		         		}
		         },
		         {input : '#amount', action : 'blur', message : uiLabelMap.FieldRequired, rule : 
		        	 	function(input, commit){
		        	 		if(!input.val()){
		        	 			return false;
		        	 		}
		        	 		return true;
         				}
		         },
		         {input : '#status', action : 'blur', message : uiLabelMap.FieldRequired,
		        	 	rule : function(input, commit){
		        	 		var val = input.val();
		        	 		if(!val){
		        	 			return false;
		        	 		}
		        	 		return true;
		        	 	}
		         },
		         {
		        	 input : '#toRating', action : 'blur', message : uiLabelMap.KpiPolicyItemPointValidate,
		        	 rule : function(input, commit){
		        		 if(input.val()){
		        			 if($('#fromRating').val() > input.val()){
		        				 return false;
		        			 }
		        		 }
		        		 return true;
		        	 }
		         },
		         {input : '#fromRating', action : 'blur', message : uiLabelMap.PointCannotBeNagative,
		        	 rule : function(input, commit){
		        		 if(input.val() < 0){
		        			 return false;
		        		 }
		        		 return true;
		        	 }
		         }
         ]
		})
	};
	
	var getData = function(){
		var data = {};
		data.fromRating = $('#fromRating').val();
		if($('#toRating').val()){
			data.toRating = $('#toRating').val();
		}else{
			data.toRating = null;
		}
		data.amount = $('#amount').val();
		data.kpiPolicyEnumId = $('#status').val();
		data.description = $('#status')[0].textContent;
		return data;
	};
	
	var initBtnEvent = function(){
		var checkSetup = function(data){
			var rows = [];
			if(!editmode){
				return true;
			}else{
				rows = $('#setupGrid').jqxGrid('getrows');
			}
			if(rows.length != 0){
				for(var i = 0; i < rows.length ; i++){
					var fromRating = data.fromRating;
					var toRating = data.toRating;
					var x = rows[i].fromRating;
					var y = rows[i].toRating;
					if(y != null){
						if(toRating != null){
							if((fromRating >= x && fromRating <= y) || (toRating >= x && toRating <= y)){
								return false;
							}else{
								if(fromRating < x && toRating >= x){
									return false;
								}
							}
						}
						return true;
					}else{
						if(toRating != null){
							if(toRating > x){
								return false;
							}
						}else{
							return false;
						}
					}
				}
			}
			return true;
		}
		
		$('#saveSetupKpi').click(function(){
			var validate = $('#setupKpiPolicyWindow').jqxValidator('validate');
			if(validate){
				var data = getData();
				data.flag = 'save';
				if(checkSetup(data)){
					if(editmode){
						$('#setupGrid').jqxGrid('addRow', null, data, "first");
						$('#setupKpiPolicyWindow').jqxWindow('close');
					}else{
						editPolicyObject.updateKpiPolicy(data);
					}
				}else{
					bootbox.dialog(uiLabelMap.WrongSetup, [{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-ok open-sans"
					}])
				}
			}else{
				return false;
			}
		});
		
		$('#saveAndContinueSetup').click(function(){
			var validate = $('#setupKpiPolicyWindow').jqxValidator('validate');
			if(validate){
				var data = getData();
				data.flag = 'saveAndContinue';
				if(checkSetup(data)){
					if(editmode){
						$('#setupGrid').jqxGrid('addRow', null, data, "first");
					}else{
						editPolicyObject.updateKpiPolicy(data);
					}
				}else{
					bootbox.dialog(uiLabelMap.WrongSetup, [{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-primary btn-small icon-ok open-sans"
					}])
				}
			}else{
				return false;
			}
		});
		
		$('#cancelSetupKpi').click(function(){
			$('#setupKpiPolicyWindow').jqxWindow('close');
		});
	};
	
	var initWindowEvent = function(){
		$('#setupKpiPolicyWindow').bind('close', function(){
			$('#fromRating').jqxNumberInput('val', null);
			$('#toRating').jqxNumberInput('val', null);
			$('#amount').jqxNumberInput('val', null);
			$('#status').jqxDropDownList('clearSelection');
		});
		$("#" + globalVar.newKPIWindow).on('open', function(){
			editmode = true;
		});
		$("#" + globalVar.newKPIWindow).on('close', function(){
			editmode = false;
		});
	};
	
	
	return {
		init : init
	}
}());

var wizardObj = (function(){
	var init = function(){
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if(info.step == 1 && (info.direction == "next")) {
				if(!editKPIObj.validate()){
					return false;
				}
			}
		}).on('finished', function(e) {
			var valid = policyKPI.validate();
			if(!valid){
				return false;
			}
			var data_step1 = editKPIObj.getData();
			var data_step2 = policyKPI.getData();
			var dataSubmit = $.extend({}, data_step1, data_step2);
			bootbox.dialog(uiLabelMap.CreateKPIConfirm, [{
				"label" : uiLabelMap.CommonSubmit,
				"class" : "btn-primary btn-small icon-ok open-sans",
				"callback": function() {
					createKpi(dataSubmit);
				}
			},{
				"label" : uiLabelMap.CommonClose,
				"class" : "btn-danger btn-small icon-ok open-sans"
			}])
		}).on('stepclick', function(e){
		});
	};
	
	var createKpi = function(data){
		$.ajax({
			type : 'POST',
			data : {
				'criteriaName': data.criteriaName,
				'description' : data.description,
				'fromDate' : data.fromDate,
				'perfCriDevelopmetTypeId' : data.perfCriDevelopmetTypeId,
				'perfCriteriaTypeId' : data.perfCriteriaTypeId,
				'periodTypeId' : data.periodTypeId,
				'setup': JSON.stringify(data.setup),
				'target' : data.target,
				'thruDate' : data.thruDate,
				'uomId' : data.uomId
			},
			url : 'createNewKpiAndSetup',
			success : function(response){
				if(response.responseMessage == "success"){
					var success = uiLabelMap.createSuccessfully;
					Grid.renderMessage('jqxgrid', success, {autoClose: true,
						template : 'info', appendContainer : "#containerjqxgrid", opacity : 0.9});
					editKPIObj.closeWindow();
					$('#jqxgrid').jqxGrid('updatebounddata');
				}else{
					bootbox.dialog(response.errorMessage,
						[{
							"label" : uiLabelMap.CommonClose,
							"class" : "btn-danger btn-small icon-remove open-sans",
						}]		
					);
				}
			}
		})
	};
	
	var resetStep = function(){
		$('#fuelux-wizard').wizard('previous');
	};
	
	return {
		init : init,
		resetStep : resetStep,
	}
}());

$(document).ready(function(){
	setupPolicy.init();
	policyKPI.init();
	wizardObj.init();
	editKPIObj.init();
	editCriteriaTypeObj.init();
	uomIdKPIObj.init();
});
