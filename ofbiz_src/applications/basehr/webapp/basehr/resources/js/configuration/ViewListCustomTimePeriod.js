var customTimePeriodObject = (function(){
	var init = function(){
		initJqxNotification();
		initJqxTreeGrid();
	};
	
	var initJqxNotification = function(){
		$("#container").width('100%');
        $("#jqxNotification").jqxNotification({ 
        	icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'}, 
        	width: '100%', 
        	appendContainer: "#container", 
        	opacity: 1, autoClose: true, template: "info" 
        });
	};
	
	var initJqxTreeGrid = function(){
		var configCustomTimePeriod = {
				width: '100%',
				height: 'auto',
				filterable: false,
				pageable: true,
				showfilterrow: false,
				key: 'customTimePeriodId',
				parentKeyId: 'parentPeriodId',
				localization: getLocalization(),
				editable: true,
				datafields: [
		             {name: 'customTimePeriodId', type: 'string'},
		             {name: 'parentPeriodId', type: 'string'},
		             {name: 'periodTypeId', type: 'string'},
		             {name: 'periodName', type: 'string'},
		             {name: 'fromDate', type: 'date'},
		             {name: 'thruDate', type: 'date'},
		             ],
	             columns: [
	                   {text: uiLabelMap.HRCustomTimePeriodId, datafield: 'customTimePeriodId', width: '20%', editable: false},
	                   {text: uiLabelMap.HRPeriodName, datafield: 'periodName',
	                	   validation: function (cell, value) {
	                	        if (value && validationNameWithoutHtml(value)) {
	                	            return { result: false, message: uiLabelMap.HRContainSpecialSymbol};
	                	        }
	                	        return true;
	                	    }
	                   },
	                   {text: uiLabelMap.CommonPeriodType, datafield: 'periodTypeId', width: '17%', editable: false, columnType: "template",
	                	   cellsrenderer: function(row, colum, value) {
	                		   if (globalVar.periodTypeArr.length > 0) {
									for(var i = 0 ; i < globalVar.periodTypeArr.length; i++){
										if (value == globalVar.periodTypeArr[i].periodTypeId){
											return '<span title =\"' + globalVar.periodTypeArr[i].description +'\">' + globalVar.periodTypeArr[i].description + '</span>';
										}
									}
								}
								return '<span title=' + value +'>' + value + '</span>';
	                	   },
	                	   	createEditor: function (row, cellvalue, editor, cellText, width, height) {
	                		   createJqxDropDownList(globalVar.periodTypeArr, editor, "periodTypeId", "description", height, width);
	                		   editor.jqxDropDownList({disabled: true});
							},
							initEditor: function (row, cellvalue, editor, celltext, width, height) {
								editor.val(cellvalue);
							},
							
	                   },
	                   {text: uiLabelMap.CommonFromDate, datafield: 'fromDate', width: '18%', cellsformat: 'dd/MM/yyyy', columnType: "template",
	                	   cellsrenderer: function(row, colum, value) {
								if (value) {
									var dateLong = new Date(value);
									if (dateLong) return dateLong.toString("dd/MM/yyyy");
								}
								return "";
							},
							createEditor: function (row, cellvalue, editor, cellText, width, height) {
								editor.jqxDateTimeInput({width: width, height: height});
							},
							initEditor: function (row, cellvalue, editor, celltext, width, height) {
								var date = new Date(cellvalue);
								editor.val(date);
							},
							getEditorValue: function (row, cellvalue, editor) {
		                       // return the editor's value.
		                       return editor.jqxDateTimeInput('val', 'date');
		                    }
	                   },
	                   {text: uiLabelMap.CommonThruDate, datafield: 'thruDate', width: '18%', cellsformat: 'dd/MM/yyyy', columnType: "template",
	                	   cellsrenderer: function(row, colum, value) {
								if (value) {
									var dateLong = new Date(value);
									if (dateLong) return dateLong.toString("dd/MM/yyyy");
								}
								return "";
							},
							createEditor: function (row, cellvalue, editor, cellText, width, height) {
								editor.jqxDateTimeInput({width: width, height: height});
							},
							initEditor: function (row, cellvalue, editor, celltext, width, height) {
								var date = new Date(cellvalue);
								editor.val(date);
							},
							getEditorValue: function (row, cellvalue, editor) {
		                       // return the editor's value.
		                       return editor.jqxDateTimeInput('val', 'date');
		                    }
	                   },
	                ],
               useUrl: true,
               root: 'results',
               url: 'jqxGeneralServicer?sname=JQListCustomTimePeriodPayroll&pagesize=0',
               useUtilFunc: true,
               showToolbar: true,
               rendertoolbar: function (toolbar){
            	   globalObject.renderJqxTreeGridToolbar(toolbar);   
               },
               source: {
            	   updateRow: function(rowID, rowData, commit){
            		   $("#jqxCustomTimePeriod").jqxTreeGrid({ disabled:true});
            		   var dataSubmit = {};
            		   dataSubmit.periodName = rowData.periodName;
            		   dataSubmit.customTimePeriodId = rowData.customTimePeriodId;
            		   dataSubmit.fromDate = rowData.fromDate.getTime();
            		   dataSubmit.thruDate = rowData.thruDate.getTime();
            		   $.ajax({
            			  url: 'updatePayrollCustomTimePeriod',
            			  data: dataSubmit,
            			  type: 'POST',
            			  success: function(response){
            				  if(response.responseMessage == 'success'){
            					  $('#container').empty();
            					  $('#jqxNotification').jqxNotification({ template: 'success'});
            					  $("#notificationContent").html(response.successMessage);
            					  $("#jqxNotification").jqxNotification("open");
            					  commit(true);
            				  }else{
            					  $('#container').empty();
            					  $('#jqxNotification').jqxNotification({ template: 'error'});
            					  $("#notificationContent").html(response.errorMessage);
            					  $("#jqxNotification").jqxNotification("open");
            					  commit(false)
            				  }
            			  },
            			  error: function(err){
            				  commit(false);
            			  },
            			  complete: function(jqXHR, textStatus){
            				  $("#jqxCustomTimePeriod").jqxTreeGrid({ disabled:false });
          	    		}
            		   });
            		   
            	   }
               }
		};
		initTreeGrid($("#jqxCustomTimePeriod"), null, configCustomTimePeriod, []);
	};
	
	var initTreeGrid = function(treeGridObject, localData, config, selectArr){
		config =  processConfig(config);
		var source;
		if (config.useUrl && (config.url != null)) {
			source = {
                datatype: "json",
                datafields: config.dataFields, 
                data: {
					noConditionFind: 'Y',
					conditionsFind: 'N',
				},
                url: config.url,
                async: false,
                root: config.root,
                id: config.key,
                hierarchy: {
                    keyDataField: { name: config.key },
                    parentDataField: { name: config.parentKeyId }
                },
            };
		}else {
			source = {
	        	localdata: localData,
		        datatype: "array",
		        datafields: config.dataFields, 
		    };
		}
		if(typeof(config.source.updateRow) != 'undefined'){
			source.updateRow = config.source.updateRow; 
		}
	    var dataAdapter = new $.jqx.dataAdapter(source);
	    $.jqx.theme = config.theme;
	    var theme = $.jqx.theme;
	    $(treeGridObject).jqxTreeGrid({
	    	source: dataAdapter,
          	theme: theme,
          	width: config.width,
          	height: config.height,
          	altRows: true,
          	autoRowHeight: false,
          	localization: config.localization,
          	editable: config.editable,
          	selectionMode: config.selectionMode,
          	ready: function () {
          		if (config.expandAll) {
          			//$("#${id}").jqxTreeGrid('expandAll');
          			//expandAllTreeGrid("${id}");
          		} else {
          			$(treeGridObject).jqxTreeGrid('expandRow', 1);
          		}
            },
          	columns: config.columns,
          	columnGroups: config.columnGroups,
          	columnsResize: config.columnsresize,
          	showToolbar: config.showToolbar,
          	rendertoolbar: config.rendertoolbar,
	    });
	};
	
	var processConfig = function(config){
		
		var configCf = {};
		var source = config.source;
		if (typeof(config) == 'undefined' || config == null) {
			return configCf;
		}
		configCf.source = typeof(config.source) != 'undefined' ? config.source: {};
		configCf.theme = typeof(config.theme) != 'undefined' ? config.theme : "olbius";
		configCf.width = typeof(config.width) != 'undefined' ? config.width : "100%";
		configCf.height = typeof(config.height) != 'undefined' ? config.height : 25;
		configCf.selectedIndex = config.selectedIndex != null ? config.selectedIndex : null;
		configCf.useUrl = config.useUrl ? config.useUrl : false;
		configCf.url = config.url ? config.url : "";
		configCf.root = config.root ? config.root : "results";
		
		configCf.key = config.key ? config.key : "id";
		configCf.value = config.value ? config.value : "description";
		configCf.description = config.description ? config.description : "description";
		configCf.source = typeof(config.source) != 'undefined' ? config.source : {};
		configCf.selectionmode = typeof(config.selectionmode) != 'undefined' ? config.selectionmode : 'singleRow';
		configCf.selectionMode = typeof(config.selectionMode) != 'undefined' ? config.selectionMode : null;
		
		configCf.expandAll = typeof(config.expandAll) != 'undefined' ? config.expandAll : false;
		configCf.columnGroups = typeof(config.columnGroups) != 'undefined' ? config.columnGroups : null;
		configCf.parentKeyId = typeof(config.parentKeyId) != 'undefined' ? config.parentKeyId : null;
		
		configCf.datatype = config.datatype ? config.datatype : 'json';
		configCf.sortcolumn = config.sortcolumn ? config.sortcolumn : '';
		configCf.sortdirection = config.sortdirection ? config.sortdirection : 'asc';
		configCf.columns = config.columns ? config.columns : [];
		configCf.columngroups = config.columngroups ? config.columngroups : null;
		
		configCf.filterable = typeof(config.filterable) != 'undefined' ? config.filterable : true;
		configCf.virtualmode = typeof(config.virtualmode) != 'undefined' ? config.virtualmode : true; 
		var rendergridrows = null;
		if (configCf.virtualmode) {
			rendergridrows = function(obj) {	
				return obj.data;
			}; 
		}
		configCf.rendergridrows = typeof(config.rendergridrows) != 'undefined' ? config.rendergridrows : rendergridrows;
		configCf.showfilterrow = typeof(config.showfilterrow) != 'undefined' ? config.showfilterrow : true;
		configCf.sortable = typeof(config.sortable) != 'undefined' ? config.sortable : true;
		configCf.editable = typeof(config.editable) != 'undefined' ? config.editable : false;
		configCf.autoheight = typeof(config.autoheight) != 'undefined' ? config.autoheight : true;
		configCf.columnsresize = typeof(config.columnsresize) != 'undefined' ? config.columnsresize : true;
		configCf.pageable = typeof(config.pageable) != 'undefined' ? config.pageable : true;
		
		configCf.rendertoolbar = typeof(config.rendertoolbar) != 'undefined' ? config.rendertoolbar : null;
		configCf.showToolbar = typeof(config.showToolbar) != 'undefined' ? config.showToolbar : true;
		
		var coupleKeyValue = [];
		if (config.key != null) coupleKeyValue.push({name: config.key});
		if (config.value != null) coupleKeyValue.push({name: config.value});
		configCf.datafields = config.datafields ? config.datafields : coupleKeyValue;
		
		configCf.showdefaultloadelement = config.showdefaultloadelement ? config.showdefaultloadelement : false;
		configCf.autoshowloadelement = config.autoshowloadelement ? config.autoshowloadelement : false;
		configCf.useUtilFunc = config.useUtilFunc ? config.useUtilFunc : false;
		
		configCf.dropDownHorizontalAlignment = config.dropDownHorizontalAlignment ? config.dropDownHorizontalAlignment : 'left';
		configCf.searchId = config.searchId ? config.searchId : null;
		configCf.displayId = config.displayId ? config.displayId : null;
		configCf.displayValue = config.displayValue ? config.displayValue : null;
		configCf.displayLabel = config.displayLabel ? config.displayLabel : null;
		
		configCf.source.dataUrl = typeof(config.dataUrl) != 'undefined' ? config.dataUrl : null;
		configCf.source.pagesize = typeof(config.pagesize) != 'undefined' ? config.pagesize : null;
		configCf.localization = typeof(config.localization) != 'undefined' ? config.localization : getLocalization();
		
		return configCf;
	};
	
	return{
		init: init
	}
}());

$(function(){
	customTimePeriodObject.init();
});
