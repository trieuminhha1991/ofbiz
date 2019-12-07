var emplPosKPIObj = (function(){
	var _rowTooltipData = {};
	var init = function(){
		initJqxTreeDataTable();
		initJqxDropDownList();
		initBtnEvent();
		initJqxTreeDataTableEvent();
		initJqxNotification();
	};
	
	var initJqxNotification = function(){
		$('#jqxNotificationNtf').jqxNotification({width: '100%', autoClose: true, template : 'info', opacity : 0.9, appendContainer : '#containerNtf'});
	};
	
	var initBtnEvent = function(){
		$("#addNewBtn").click(function(event){
			editEmplPosKPIObj.openWindow();
		});
		$("#clearFilterBtn").click(function(){
			$("#statusIdFilter").jqxDropDownList('clearSelection');
			$("#emplPositionTypeFilter").jqxDropDownList('clearSelection');
			refreshJqxDataTable();
		});
		$('#deleteBtn').click(function(event){
			var selection = $('#jqxEmplPosTypePerfCri').jqxDataTable('getSelection');
			if(selection.length == 0){
				bootbox.dialog(uiLabelMap.NoRowSelected, [{
					"label" : uiLabelMap.CommonClose,
					"icon": 'fa fa-remove',
					"class": 'btn  btn-danger form-action-button pull-right',
					"callback" : function(){
						bootbox.hideAll();
					}
				}]);
				return;
			}
			bootbox.dialog(uiLabelMap.wgdeleteconfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							var data = [];
							for(var i=0 ; i < selection.length ; i++){
								var row = {
										criteriaId : selection[i].criteriaId,
										emplPositionTypeId : selection[i].emplPositionTypeId
								};
								data.push(row);
							}
							var dataSubmit = JSON.stringify(data);
							$.ajax({
								type : 'POST',
								url : 'expireEmplTypeCriteria',
								data : {
									dataSubmit : dataSubmit
								},
								datatype : 'json',
								success : function(response){
									$('#jqxNotificationNtf').jqxNotification('closeLast');
									if(response.responseMessage == "success"){
										$('#containerNtf').empty();
										$('#jqxNotificationNtf').jqxNotification({width: '100%', autoClose: true, template : 'info', opacity : 0.9, appendContainer : '#containerNtf'});
										$('#content_noti').text(uiLabelMap.deleteSuccessfully);
										$('#jqxNotificationNtf').jqxNotification('open');
										$('#jqxEmplPosTypePerfCri').jqxDataTable('updateBoundData');
									}else{
										bootbox.dialog(response._ERROR_MESSAGE_, [{
											"label" : uiLabelMap.CommonClose,
											"icon": 'fa fa-remove',
											"class": 'btn  btn-danger form-action-button pull-right',
											"callback" : function(){
												bootbox.hideAll();
											}
										}]);
									}
								}
							});
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
		
	};
	
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.emplPositionTypeArr, $("#emplPositionTypeFilter"), "emplPositionTypeId", "description", 25, 250);
		createJqxDropDownList(globalVar.statusArr, $("#statusIdFilter"), "statusId", "description", 25, 250);
		$("#emplPositionTypeFilter").jqxDropDownList({placeHolder: uiLabelMap.ChooseEmplPositionType});
		$("#statusIdFilter").jqxDropDownList({placeHolder: uiLabelMap.ChooseStatus});
		$("#statusIdFilter").on('select', function(event){
			var args = event.args;
		    if (args) {
		    	var index = args.index;
		    	var item = args.item; 
		    	var statusId = item.value;
		    	var emplPositionTypeId = $("#emplPositionTypeFilter").val();
		    	refreshJqxDataTable(statusId, emplPositionTypeId);
		    }
		});
		$("#emplPositionTypeFilter").on('select', function(event){
			var args = event.args;
			if (args) {
				var index = args.index;
				var item = args.item; 
				var emplPositionTypeId = item.value;
				var statusId = $("#statusIdFilter").val();
				refreshJqxDataTable(statusId, emplPositionTypeId);
			}
		});
		$("#statusIdFilter").jqxDropDownList('selectIndex', 0 );
	};
	
	var refreshJqxDataTable = function(statusId, emplPositionTypeId){
		var param = "";
		if(typeof(statusId) != 'undefined' && statusId.length > 0){
			param += "&statusId=" + statusId;
		}
		if(typeof(emplPositionTypeId) != 'undefined' && emplPositionTypeId.length > 0){
			param += "&emplPositionTypeId=" + emplPositionTypeId;
		}
		var url = 'jqxGeneralServicer?sname=GetListEmplPosCriType' + param;
		var source = $("#jqxEmplPosTypePerfCri").jqxDataTable('source');
		source._source.url = url;
		$("#jqxEmplPosTypePerfCri").jqxDataTable('source', source);
		$("#jqxEmplPosTypePerfCri").jqxDataTable('updateBoundData');
	}
	
	
	var initJqxTreeDataTable = function(){
		var datafields = getDatafields();
		var columns = getColumns();
		var source = {
				dataType: "json",
				dataFields: datafields,
				root: 'results',
	            type: 'POST',	
	            url: '',
		};
		var dataAdapter = new $.jqx.dataAdapter(source, {
	        loadComplete: function () {	        	
	            // data is loaded.
	        },
	        downloadComplete: function (data, status, xhr) {
	            if (data){	            	
	                source.totalRecords = data.totalRows;
	            }
	        },
	        loadError: function (xhr, status, error) {
	            throw new Error(error.toString());
	        } 
		});
		$("#jqxEmplPosTypePerfCri").jqxDataTable({
				width: '99%',		
				pagerMode: "advanced",
			    pageable: true,
			    theme: 'olbius',
			    editable: true,
			    serverProcessing: true,
			    source: dataAdapter,
			    altRows: true,
			    pageSize: 15,
			    localization: getLocalization(),
			    groups: ['emplPositionTypeId'],		    
			    columnsResize: true,
			    editSettings: { saveOnPageChange: true, saveOnBlur: true, saveOnSelectionChange: true,
			    		cancelOnEsc: true, saveOnEnter: true, editSingleCell: true, editOnDoubleClick: true, editOnF2: true},
			    groupsRenderer: function(value, rowData, level){
			    	var data = rowData.data;
			    	var retVal =  '<b>' + uiLabelMap.HrCommonPosition + ': ' + data.description;
			    	retVal += '</b>';
			        return retVal;
			    },
			    columns:columns,
			    rendered: function(){
			    	/*var keys = Object.keys(_rowTooltipData);
			    	for(var i = 0; i < keys.length; i++){
			    		var tooltip = _rowTooltipData[keys[i]];
			    		displayTooltipOnRowDataTale("INFO", "jqxEmplPosTypePerfCri", tooltip.row + 1, 0, tooltip.data);
			    		delete _rowTooltipData[keys[i]];
			    	}*/
			    }
		 });
	};
	
	var initJqxTreeDataTableEvent = function(){
		$("#jqxEmplPosTypePerfCri").on('cellEndEdit', function(event){
			var value = event.args.value;
			var row = event.args.row;
			var data = {criteriaId : row.criteriaId, emplPositionTypeId : row.emplPositionTypeId, criteriaName : value};
			$.ajax({
				type : 'POST',
				url : 'updateKPIEmplPositionType',
				data : data,
				success : function(){
					
				}
			})
		})
	};
	
	var getDatafields = function(){
		var retData = [
		       		{name : 'emplPositionTypeId', type : 'string'},
		       		{name : 'description', type : 'string'},
		       		{name : 'criteriaId', type : 'string'},
		       		{name : 'criteriaName', type : 'string'},
		       		{name : 'perfCriteriaTypeId', type : 'string'},
		       		{name : 'periodTypeId', type : 'string'},
		    		{name : 'target', type : 'number'},
		    		{name : 'uomId', type : 'string'},
		    		{name : 'descriptionKPI', type : 'string'},
		    		{name : 'statusId', type : 'string'},
		    		{name : 'weight', type : 'number', other: 'BigDecimal'},
		    	];
		return retData;
	};
	
	var getColumns = function(){
		var retData = [
		               {text : uiLabelMap.HRCommonKPIName, datafield : 'criteriaName', width : '20%', cellClassName: 'backgroundWhiteColor',
		            	   editable: false,
		            	   cellsRenderer: function (row, column, value, rowData) {
		            		   if(column == "criteriaName" && rowData.descriptionKPI){
		            			   _rowTooltipData["row_" + row] = {row: row, data: rowData.descriptionKPI};
		            		   }
		            		   return '<span>' + value + '</span>';
		            	   },
		            	   cellendedit : function(){
		            		   
		            	   }
		            	   
		               },
		               {text : uiLabelMap.HRCommonFields, width : '20%', dataField : 'perfCriteriaTypeId', columntype: 'template',
		            	   cellClassName: 'backgroundWhiteColor', editable: false,
			       			cellsRenderer: function (row, column, value, rowData) {
			       				for(var i = 0; i < globalVar.perfCriteriaTypeArr.length; i++){
			       					if(value == globalVar.perfCriteriaTypeArr[i].perfCriteriaTypeId){
			       						return '<span>' + globalVar.perfCriteriaTypeArr[i].description + '</span>'; 
			       					}
			       				}
			       				return '<span>' + value + '</span>';
			       			}
			       		},
			       		{text: uiLabelMap.KPIWeigth, width: '12%', datafield: 'weight', columntype: 'template',
			       			cellClassName: 'backgroundWhiteColor', editable: false,
			       			cellsRenderer: function(row, column, value, rowData) {
			       				if(typeof(value) == "number"){
			       					return "<span>" + value * 100 + "%</value>";
			       				}
			       				return "<span>" + value + "</value>";
			       			}
			       		},
			       		{text : uiLabelMap.HRFrequency, width : '12%', datafield : 'periodTypeId', columntype: 'template',
			       			cellClassName: 'backgroundWhiteColor', editable: false,
			    			cellsRenderer: function (row, column, value, rowData) {
			    				for(var i = 0; i < globalVar.periodTypeArr.length; i++){
			    					if(value == globalVar.periodTypeArr[i].periodTypeId){
			    						return '<span>' + globalVar.periodTypeArr[i].description + '</span>'; 
			    					}
			    				}
			    				return '<span>' + value + '</span>';
			    			},
			    		},
			    		{text : uiLabelMap.HRTarget, width : '15%', datafield : 'target',  columntype: 'template',
			    			cellClassName: 'backgroundWhiteColor', editable: false,
			    			cellsRenderer: function (row, column, value, rowData) {
			    				var uomId = rowData.uomId;
			    				var retVal = value;
			    				var uomDes = uomId;
			    				if(value){
			    					retVal = formatNumber(value);
			    					if(uomId){
			    						for(var i = 0; i < globalVar.uomArr.length; i++){
			    							if(globalVar.uomArr[i].uomId == uomId){
			    								uomDes = globalVar.uomArr[i].abbreviation;
			    								break;
			    							}
			    						}
			    					}
			    					retVal += ' ' + uomDes;
			    				}
			    				return '<span>' + retVal + '<span>';
			    			}
			    		},
			    		{text : uiLabelMap.CommonStatus, datafield : 'statusId', columntype: 'template', 
			    			cellClassName: 'backgroundWhiteColor', editable: false, width : '21%',
			    			cellsRenderer: function (row, column, value, rowData	) {
			    				for(var i = 0; i < globalVar.statusArr.length; i++){
			    					if(value == globalVar.statusArr[i].statusId){
			    						return '<span>' + globalVar.statusArr[i].description + '</span>'; 
			    					}
			    				}
			    				return '<span>' + value + '</span>';
			    			}
			    		},
			    		{datafield: 'uomId', hidden: true},
			    		{datafield: 'emplPositionTypeId', hidden: true, editable: false},	
			            {datafield: 'descriptionKPI', hidden: true, editable: false},	
			            {datafield : 'description', hidden: true},
		];
		return retData;
		
	};
	return{
		init: init
	}
}());

var editEmplPosKPIObj = (function(){
	var _refreshGridAfterCLoseWindow = false;
	var _listKPIData = [];
	var init = function(){
		initJqxDropDownList();
		initJqxNumberInput();
		initJqxWindow();
		initBtnEvent();
		initJqxValidator();
		create_spinner($("#spinnerPositionTypeKPI"));
	};
	var initJqxDropDownList = function(){
		createJqxDropDownList(globalVar.emplPositionTypeArr, $("#emplPositionTypeEdit"), "emplPositionTypeId", "description", 25, "98%");
		createJqxDropDownList(globalVar.perfCriteriaTypeArr, $("#perfCriteriaTypeEdit"), "perfCriteriaTypeId", "description", 25, "98%");
		createJqxDropDownList(globalVar.uomArr, $("#uomIdEdit"), "uomId", "abbreviation", 25, '98%');
		createJqxDropDownList(globalVar.periodTypeArr, $("#periodTypeEdit"), "periodTypeId", "description", 25, '98%');
		createJqxDropDownList([], $("#perfCriteriaEdit"), "criteriaId", "criteriaName", 25, '98%');
		$("#emplPositionTypeEdit").jqxDropDownList({placeHolder: uiLabelMap.ChooseEmplPositionType});
		$("#perfCriteriaTypeEdit").jqxDropDownList({placeHolder: uiLabelMap.ChoosePerfCriteriaType});
		
		$("#perfCriteriaTypeEdit").on('select', function(event){
			var args = event.args;
			if(args){
				var item = args.item;
				var value = item.value;
				$("#perfCriteriaEdit").jqxDropDownList({disabled: true});
				listKPIData = [];
				$.ajax({
					url: 'getPerfCriteriaByType',
					data: {perfCriteriaTypeId: value},
					type: 'POST',
					success: function(response){
						if(response.responseMessage == "success"){
							updateSourceDropdownlist($("#perfCriteriaEdit"), response.listReturn);
							listKPIData = response.listReturn;
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
						$("#perfCriteriaEdit").jqxDropDownList({disabled: false});
					}
				});
		    }
		});
		
		$("#perfCriteriaEdit").on('select', function(event){
			var args = event.args;
			if(args){
				var index = args.index;
				var dataRecord = listKPIData[index];
				if(dataRecord){
					$("#targetEdit").val(dataRecord.target);
					$("#periodTypeEdit").val(dataRecord.periodTypeId);
					$("#uomIdEdit").val(dataRecord.uomId);
				}
			}
		});
		$("#perfCriteriaEdit").on('unselect', function(event){
			$("#uomIdEdit").jqxDropDownList('clearSelection');
			$("#periodTypeEdit").jqxDropDownList('clearSelection');
			$("#targetEdit").val(0);
		});
	};
	
	var initJqxNumberInput = function(){
		$("#targetEdit").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: true, max: 999999999999, digits: 11, min: 0, theme: 'olbius'});
		$("#weightEdit").jqxNumberInput({ width: '98%', height: '25px',  spinButtons: true, max: 100, digits: 3, min: 0,
			symbolPosition: 'right', symbol: '%', inputMode: 'simple', theme: 'olbius'});
	};
	var initJqxValidator = function(){
		$("#editEmplPosKPIWindow").jqxValidator({
			rules: [
				{input : '#emplPositionTypeEdit', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#perfCriteriaTypeEdit', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#perfCriteriaEdit', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#periodTypeEdit', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#uomIdEdit', message : uiLabelMap.FieldRequired, action : 'blur',
					rule : function(input, commit){
						if(!input.val()){
							return false;
						}
						return true;
					}
				},
				{input : '#targetEdit', message : uiLabelMap.ValueMustBeGreateThanZero, action : 'blur',
					rule : function(input, commit){
						if(input.val() != 'undefined' && input.val() <= 0){
							return false;
						}
						return true;
					}
				},
			]
		});
	};
	var disableAll = function(){
		$("#targetEdit").jqxNumberInput({disabled: true});
		$("#weightEdit").jqxNumberInput({disabled: true});
		$("#uomIdEdit").jqxDropDownList({disabled: true});
		$("#periodTypeEdit").jqxDropDownList({disabled: true});
		$("#emplPositionTypeEdit").jqxDropDownList({disabled: true});
		$("#perfCriteriaTypeEdit").jqxDropDownList({disabled: true});
		$("#perfCriteriaEdit").jqxDropDownList({disabled: true});
		$("#alterCancel").attr("disabled", "disabled");
		$("#alterSave").attr("disabled", "disabled");
		$("#saveAndContinue").attr("disabled", "disabled");
	};
	
	var enableAll = function(){
		$("#targetEdit").jqxNumberInput({disabled: false});
		$("#weightEdit").jqxNumberInput({disabled: false});
		$("#uomIdEdit").jqxDropDownList({disabled: false});
		$("#periodTypeEdit").jqxDropDownList({disabled: false});
		$("#emplPositionTypeEdit").jqxDropDownList({disabled: false});
		$("#perfCriteriaTypeEdit").jqxDropDownList({disabled: false});
		$("#perfCriteriaEdit").jqxDropDownList({disabled: false});
		$("#alterCancel").removeAttr("disabled");
		$("#alterSave").removeAttr("disabled");
		$("#saveAndContinue").removeAttr("disabled");
	};
	
	var initBtnEvent = function(){
		$("#alterCancel").click(function(event){
			$("#editEmplPosKPIWindow").jqxWindow('close');
		});
		$("#alterSave").click(function(event){
			var valid = $("#editEmplPosKPIWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.AssignKPIEmplPositionTypeConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							addKPIForEmplPositionType(true);
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
		$("#saveAndContinue").click(function(event){
			var valid = $("#editEmplPosKPIWindow").jqxValidator('validate');
			if(!valid){
				return;
			}
			bootbox.dialog(uiLabelMap.AssignKPIEmplPositionTypeConfirm,
					[{
						"label" : uiLabelMap.CommonSubmit,
						"class" : "btn-primary btn-small icon-ok open-sans",
						"callback": function() {
							addKPIForEmplPositionType(false);
						}	
					},
					{
						"label" : uiLabelMap.CommonClose,
						"class" : "btn-danger btn-small icon-remove open-sans",
					}]		
			);
		});
		
	};
	var getData = function(){
		var data = {};
		data.emplPositionTypeId = $("#emplPositionTypeEdit").val();
		data.criteriaId = $("#perfCriteriaEdit").val();
		data.periodTypeId = $("#periodTypeEdit").val();
		data.target = $("#targetEdit").val();
		data.uomId = $("#uomIdEdit").val();
		data.weight = $("#weightEdit").val()/100;
		return data;
	};
	var addKPIForEmplPositionType = function(isCloseWindow){
		$("#loadingPositionTypeKPI").show();
		disableAll();
		var data = getData();
		$.ajax({
			url: 'addKPIForEmplPositionType',
			data: data,
			type: 'POST',
			success: function(response){
				if(response.responseMessage == "success"){
					_refreshGridAfterCLoseWindow = true;
					$('#jqxNotificationNtf').jqxNotification('closeLast');
					if(isCloseWindow){
						$("#editEmplPosKPIWindow").jqxWindow('close');
						$('#jqxNotificationNtf').jqxNotification({width: '100%', autoClose: true, template : 'info', opacity : 0.9, appendContainer : '#containerNtf'});
						$('#containerNtf').empty();
						$('#content_noti').text(response.successMessage);
						$('#jqxNotificationNtf').jqxNotification('open');
					}
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
				$("#loadingPositionTypeKPI").hide();
			}
		});
	};
	var initJqxWindow = function(){
		createJqxWindow($("#editEmplPosKPIWindow"), 500, 380);
		$("#editEmplPosKPIWindow").on('close', function(event){
			Grid.clearForm($(this));
			if(_refreshGridAfterCLoseWindow){
				$("#jqxEmplPosTypePerfCri").jqxDataTable('updateBoundData');
				_refreshGridAfterCLoseWindow = false;
			}
		});
	};
	
	var openWindow = function(){
		openJqxWindow($("#editEmplPosKPIWindow"));
	};
	
	return{
		init: init,
		openWindow: openWindow
	}
}());

$(document).ready(function(){
	emplPosKPIObj.init();
	editEmplPosKPIObj.init();
});