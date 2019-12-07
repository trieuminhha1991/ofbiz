var viewListPartyFormulaIITypeObject = (function(){
	var init = function(){
		createJqxDataTable();
		initJqxTreeButton();
	};
	
	var initJqxTreeButton = function(){
		var config = {dropDownBtnWidth: 240, treeWidth: 240};
		globalObject.createJqxTreeDropDownBtn($("#searchJqxTree"), $("#searchDropDownButton"), globalVar.rootPartyArr, "treeSearch", "treeChildSearch", config);
		 
		$('#searchJqxTree').on('select', function(event){
			var id = event.args.element.id;
			var item = $('#searchJqxTree').jqxTree('getItem', event.args.element);
		  	setDropdownContent(event.args.element, $("#searchJqxTree"), $("#searchDropDownButton"));
		  	var source = $('#jqxDataTable').jqxDataTable('source');
		  	source._source.url = "getPartyFormulaInvoiceItemType?partyId=" + item.value;
		  	$('#jqxDataTable').jqxDataTable('source', source);
		  	$('#jqxDataTable').jqxDataTable('updateBoundData');
		  	
		});
		if(globalVar.rootPartyArr.length > 0){
			$("#searchJqxTree").jqxTree('selectItem', $("#" + globalVar.rootPartyArr[0].partyId + "_treeSearch")[0]);
		}
	};
	
	var initSourceJqxDataTable = function(){
		var source = {
				dataType: "json",
				dataFields: [
					{name: 'code', type: 'string' },
					{name: 'groupName', type: 'string' },
					{name: 'description', type: 'string' },
					{name: 'partyId', type: 'string' },
					{name: 'invoiceItemTypeId', type: 'string'},
					{name: 'fromDate', type: 'date'},
					{name: 'thruDate', type: 'date'},			             
					{name: 'orgId', type: 'string'},			             
					{name: 'orgName', type: 'string'},			             
				],
				root: 'listReturn',
	            type: 'POST',	 
	            url: '',  
	            addRow: function (rowID, rowData, position, commit){
	            	$('#jqxDataTable').jqxDataTable({disabled: true});
	            	$.ajax({
	            		url: 'createPartyFormulaInvoiceItemTypeJQ',
	            		data: rowData,
	            		type: 'POST',
	            		async: true,
	            		success: function(response){
	            			$("#jqxNtf").jqxNotification('closeLast');            			
	            			if(response.responseMessage == 'success'){            				
	            				$("#jqxNtfContent").text(response.successMessage);
	            				$("#jqxNtf").jqxNotification({template: 'info'});
	            				$("#jqxNtf").jqxNotification("open");
	                			commit(true);
	            			}else{
	            				$("#jqxNtfContent").text(response.errorMessage);
	            				$("#jqxNtf").jqxNotification({template: 'error'});
	            				$("#jqxNtf").jqxNotification("open");
	            				commit(false);
	            			}
	            		},
	            		error: function(jqXHR, textStatus, errorThrown){
	            			commit(false);
	            		},
	            		complete: function(jqXHR, textStatus){
	            			$('#jqxDataTable').jqxDataTable({disabled:false});            			
	            			$('#jqxDataTable').jqxDataTable('updateBoundData');
	            		}
	            	});
	            	//commit(true);
	            },
	            updateRow: function (rowId, rowData, commit) {
	            	$('#jqxDataTable').jqxDataTable({disabled: true});
	            	var dataSubmit = {};
	        		var fromDate = rowData.fromDate.getTime();
	        		var thruDate = rowData.thruDate;
	        		if(thruDate){
	        			dataSubmit.thruDate = thruDate.getTime();            			
	        		}
	        		dataSubmit.fromDate = fromDate;
	        		dataSubmit.partyId = rowData.partyId;
	        		dataSubmit.code = rowData.code;
	        		dataSubmit.invoiceItemTypeId = rowData.invoiceItemTypeId;
	        		
	            	$.ajax({
	            		url: 'updatePartyFormulaInvoiceItemType',
	            		data: dataSubmit,
	            		type: 'POST',
	            		async: false,
	            		success: function(response){
	            			$("#jqxNtf").jqxNotification('closeLast');            			
	            			if(response.responseMessage == 'success'){            				
	            				$("#jqxNtfContent").text(response.successMessage);
	            				$("#jqxNtf").jqxNotification({template: 'info'});
	            				$("#jqxNtf").jqxNotification("open");
	                			commit(true);
	            			}else{
	            				$("#jqxNtfContent").text(response.errorMessage);
	            				$("#jqxNtf").jqxNotification({template: 'error'});
	            				$("#jqxNtf").jqxNotification("open");
	            				commit(false);
	            			}
	            		},
	            		error: function(jqXHR, textStatus, errorThrown){
	            			commit(false);
	            		},
	            		complete: function(jqXHR, textStatus){
	            			$('#jqxDataTable').jqxDataTable({disabled:false});            			
	            			$('#jqxDataTable').jqxDataTable('updateBoundData');
	            		}
	            	});
	            },
	            deleteRow: function (rowID, commit) {
	            	commit(true);
	            }
		}
		return source;
	};

	var initColumnlistJqxDataTable = function(){
		var columnlist = [
				{text: uiLabelMap.FormulaCode,  filtertype: 'checkedlist', datafield: 'code', width: 290, editable: false, cellClassName: 'backgroundWhiteColor',
					createfilterwidget: function(column, columnElement, widget){
						//var filterBoxAdapter = new $.jqx.dataAdapter(invoiceItems, {autoBind: true});
						var filterBoxAdapter = new $.jqx.dataAdapter(codes, {autoBind: true});
						var dataSoureList = filterBoxAdapter.records;
					    //dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
						widget.jqxDropDownList({
							theme: 'olbius',
							source: dataSoureList,
							width: '98%',
							dropDownHeight: 200,
							autoDropDownHeight: false,
							displayMember: 'name',
							valueMember: 'code'
						});
					},
					cellsrenderer: function(row, column, value){
						for(var i = 0; i < codes.length; i++){
							if(value == codes[i].code){
								return '<span>' + codes[i].name + ' [' + value +']'+ '</span>';
							}
						}
						return '<span>' + value + '</span>';
					}
				},
				{text: uiLabelMap.Department, filterable: false, datafield: 'partyId', hidden: true, editable: false},
				{datafield: 'groupName', hidden: true, editable: false},
				{text: uiLabelMap.AccountingInvoiceItemType, filtertype: 'checkedlist', datafield: 'invoiceItemTypeId', 
					cellClassName: 'backgroundWhiteColor',
					 width: 480, editable: false,
					 createfilterwidget: function(column, columnElement, widget){
					    var filterBoxAdapter = new $.jqx.dataAdapter(invoiceItems, {autoBind: true});
						var dataSoureList = filterBoxAdapter.records;
					    //dataSoureList.splice(0, 0, uiLabelMap.filterselectallstring);
					    widget.jqxDropDownList({ source: dataSoureList, displayMember: 'description', dropDownHeight: 300, autoDropDownHeight: false, valueMember : 'invoiceItemTypeId',  filterable:true, searchMode:'containsignorecase'});
					},
					cellsrenderer : function(row, column, value, rowData){
						//var val = $('#jqxDataTable').jqxDataTable('getrowdata', row);
						return '<div style="margin-top: 6px; margin-left: 4px;">' + rowData.description +' [' + rowData.invoiceItemTypeId + ']' +'</div>';
					}
				},
				{text: uiLabelMap.fromDate, datafield: 'fromDate',cellsformat: 'dd/MM/yyyy', 
					 editable: false, width: 130, cellClassName: 'backgroundWhiteColor'},
				{text: uiLabelMap.CommonThruDate, datafield: 'thruDate', cellsformat: 'dd/MM/yyyy', editable: true,
					cellClassName: 'backgroundWhiteColor', columntype: 'custom',
					createEditor: function (row, cellValue, editor, width, height) {
						var fromDate = $("#jqxDataTable").jqxDataTable('getCellValue', row, 'fromDate');					
						editor.jqxDateTimeInput({width: '95%', height: '100%', theme: 'olbius', min: fromDate, dropDownHorizontalAlignment: 'right'});
					},
					initEditor: function (row, cellValue, editor) {
	                    // set jqxInput editor's initial value.
	                    if(cellValue){
	                    	editor.val(cellValue);
	                    }else{
	                    	editor.val(null);
	                    }
	                },
	                getEditorValue: function (index, value, editor) {
	                    // get jqxInput editor's value.
	                    return editor.jqxDateTimeInput('val', 'date');
	                },
	                
				}                 
		];
		return columnlist;
	};

	var createJqxDataTable = function(){
		var source = initSourceJqxDataTable();
		var columns = initColumnlistJqxDataTable();
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
		$("#jqxDataTable").jqxDataTable({
				width: '100%',		
				pagerMode: "advanced",
			    pageable: true,
			    theme: 'olbius',
			    editable: globalVar.editable,
			    serverProcessing: true,
			    source: dataAdapter,
			    altRows: true,
			    pageSize: 15,
			    groups: ['partyId'],		    
			    columnsResize: true,
			    localization: getLocalization(),
			    editSettings: { saveOnPageChange: true, saveOnBlur: true, saveOnSelectionChange: true,
			    		cancelOnEsc: true, saveOnEnter: true, editSingleCell: true, editOnDoubleClick: true, editOnF2: true},
			    groupsRenderer: function(value, rowData, level){
			    	var data = rowData.data;
			    	var retVal =  '<b>' + uiLabelMap.Department + ': ' + data.groupName;
			    	if(data.orgName && data.orgName.length > 0){
			    		retVal += ' - <i>' + data.orgName + '</i>'; 
			    	}
			    	retVal += '</b>';
			        return retVal;
			    },
			    columns:columns
		 });
	};
	
	return{
		init: init
	}
}());



$(document).ready(function(){
	viewListPartyFormulaIITypeObject.init();
});