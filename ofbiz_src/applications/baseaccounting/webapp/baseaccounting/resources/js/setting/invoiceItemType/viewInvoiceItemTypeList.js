var viewInvoiceItemTypeObj = (function(){
	var _rowKeySelect = "";
	var init = function(){
		initTreeGrid();
		initContextMenu();
		$("#jqxNotificationjqxgrid").jqxNotification({ width: "100%", appendContainer: "#containerjqxgrid", opacity: 1, autoClose: true, template: "success" });
	};
	var initTreeGrid = function(){
		var grid = $("#invoiceItemTypeTree");
		var datafield = [{name: 'invoiceItemTypeId', type: 'string'},
		                 {name: 'parentTypeId', type: 'string'},
		                 {name: 'parentTypeDesc', type: 'string'},
		                 {name: 'description', type: 'string'},
		                 {name: 'invoiceTypeId', type: 'string'},
		                 {name: 'invoiceTypeDesc', type: 'string'},
		                 {name: 'defaultGlAccountId', type: 'string'},
		                 {name: 'accountName', type: 'string'},];
		
		var columns = [{text: uiLabelMap.CommonId, dataField: 'invoiceItemTypeId', width: '15%'},
		               {text: uiLabelMap.CommonDescription, dataField: 'description', width: '28%'},
		               {text: uiLabelMap.BACCInvoiceTypeId, dataField: 'invoiceTypeDesc', width: '24%'},
		               {text: uiLabelMap.BACCGlAccountId, dataField: 'defaultGlAccountId', width: '9%'},
		               {text: uiLabelMap.BACCAccountName, dataField: 'accountName', width: '24%'},
		               ];
		
		var source = {
				dataType: "json",
				dataFields: datafield,
				hierarchy:
                {
                    keyDataField: { name: 'invoiceItemTypeId' },
                    parentDataField: { name: 'parentTypeId' }
                },
                id: 'invoiceItemTypeId',
                type: 'POST',
                root: 'listReturn',
                url: 'getListInvoiceItemType'
		};
		var rendertoolbar = function(toolbar){
			toolbar.html("");
			var id = "invoiceItemTypeTree";
			var jqxheaderStr = "<div id='toolbarcontainer" + id + "' class='widget-header'><h4>" 
								+ uiLabelMap.BACCInvoiceItemList + "</h4><div id='toolbarButtonContainer" + id + "' class='pull-right'></div></div>";
			var jqxheader = $(jqxheaderStr);
			toolbar.append(jqxheader);
			var container = $("#toolbarButtonContainer" + id);
			
			var customcontrolAdvance = "<div style='float: left; width: 16px; height: 16px; position: relative; margin: 3px; display: block;' title='" + uiLabelMap.CommonSearch + "' class='jqx-icon-search'></div>" +
										"<div id='columnChooser' style='display: inline-block; float: left; margin-right: 5px'></div>" +
										"<input type='text' id='filterInput' style='float: left'/>" +
										"<div style='float: left; width: 16px; height: 16px; position: relative; margin: 3px; display: block;' id='clearFilter' title='" + uiLabelMap.accRemoveFilter + "' class='jqx-icon-close jqx-icon-close-light'></div>";
			container.append("<div style='float:left;margin-left:20px;margin-top: 7px; font-size: 14px; font-weight: normal;'>"+ customcontrolAdvance +"</div>");
			createCustomControlAdvance();
			
			var customcontrol1 = "icon-plus open-sans@" + uiLabelMap.accAddNewRow + "@javascript:void(0)@editInvoiceItemTypeObj.openNewWindow()";
			Grid.createCustomControlButton(grid, container, customcontrol1);
			var buttonContainer = $('<div class="custom-control-toolbar"></div>');
			var buttonExpend = $('<a id="btnExpend' + id + '" style="color:#438eb9;" href="javascript:void(0);"><i class="fa fa-expand"></i></a>');
			var buttonCollapse = $('<a id="btnCollapse' + id + '" style="color:#438eb9;" href="javascript:void(0);"><i class="fa fa-compress"></i></a>');
			buttonContainer.append(buttonCollapse);
			buttonContainer.append(buttonExpend);
			$(container).append(buttonContainer);
			
			$("#btnExpend" + id).click(function(){
				grid.jqxTreeGrid('expandAll', true);
			});
			$("#btnCollapse" + id).click(function(){
				grid.jqxTreeGrid('collapseAll', true);
			});
		};
		var dataAdapter = new $.jqx.dataAdapter(source);
		grid.jqxTreeGrid(
        {
            width: '100%',
            height: 510,
            source: dataAdapter,
            sortable: false, 
            columnsResize: true,
            //filterable: true,
            ready: function(){
            	//grid.jqxTreeGrid('expandAll');
            },
            theme: 'olbius',
            columns: columns,
            localization: getLocalization(),
            showToolbar: true,
        	rendertoolbar: rendertoolbar
        });
	};
	var createCustomControlAdvance = function(){
		var columnChooserList = [{text: uiLabelMap.CommonId, dataField: 'invoiceItemTypeId'},
		  		               	 {text: uiLabelMap.CommonDescription, dataField: 'description'},
				                 {text: uiLabelMap.BACCInvoiceTypeId, dataField: 'invoiceTypeDesc'},
				                 {text: uiLabelMap.BACCGlAccountId, dataField: 'defaultGlAccountId'},
				                 {text: uiLabelMap.BACCAccountName, dataField: 'accountName'},
				               ];
		accutils.createJqxDropDownList($("#columnChooser"), columnChooserList, {valueMember: 'dataField', displayMember: 'text', width: 110, height: 21, theme: 'olbius', selectedIndex: 0});
		$("#filterInput").jqxInput({width: 140, height: 21, theme: 'olbius'});
		initEvent();
	};
	var initEvent = function(){
		$("#clearFilter").click(function(e){
			$("#invoiceItemTypeTree").jqxTreeGrid('clearFilters');
			$("#filterInput").val("");
		});
		$("#filterInput").keypress(function(e){
			if(e.which == 13) {
				var filtervalue = $("#filterInput").val();
				var dataField = $("#columnChooser").val();
				var filtertype = 'stringfilter';
				var filtergroup = new $.jqx.filter();
				var filter_or_operator = 1;
				var filtercondition = 'CONTAINS';
                var filter = filtergroup.createfilter(filtertype, filtervalue, filtercondition);
                filtergroup.addfilter(filter_or_operator, filter);
                $("#invoiceItemTypeTree").jqxTreeGrid('addFilter', dataField, filtergroup);
                $("#invoiceItemTypeTree").jqxTreeGrid('applyFilters');
			}
		});
		$("#invoiceItemTypeTree").on('bindingComplete', function(event){
			if(_rowKeySelect.length > 0){
				var rowSelect = $("#invoiceItemTypeTree").jqxTreeGrid('getRow', _rowKeySelect);
				if(rowSelect){
					var parentTypeId = rowSelect.parentTypeId;
					var expandTreeArr = [];
					while(parentTypeId && parentTypeId.length > 0){
						expandTreeArr.unshift(parentTypeId);
						rowSelect = $("#invoiceItemTypeTree").jqxTreeGrid('getRow', parentTypeId);
						parentTypeId = rowSelect.parentTypeId;
					}
				}
				expandTreeArr.forEach(function(rowKeyExpand){
					$("#invoiceItemTypeTree").jqxTreeGrid('expandRow', rowKeyExpand);
				});
				$("#invoiceItemTypeTree").jqxTreeGrid('selectRow', _rowKeySelect);
				_rowKeySelect = "";
			}
		});
	};
	var initContextMenu = function(){
		accutils.createJqxMenu("contextMenu", 30, 150);
		$("#invoiceItemTypeTree").on('contextmenu', function () {
            return false;
        });
        $("#invoiceItemTypeTree").on('rowClick', function (event) {
            var args = event.args;
            if (args.originalEvent.button == 2) {
                var scrollTop = $(window).scrollTop();
                var scrollLeft = $(window).scrollLeft();
                $("#contextMenu").jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                return false;
            }else{
            	$("#contextMenu").jqxMenu('close');
            }
        });
        $("#contextMenu").on('itemclick', function (event) {
			var args = event.args;
			var selection = $("#invoiceItemTypeTree").jqxTreeGrid('getSelection');
			var rowData = selection[0];
			if($.trim($(args).attr("action")) == "edit") {
				editInvoiceItemTypeObj.openWindow(rowData);
			}
        });
	};
	var setRowKeySelect = function(rowKey){
		_rowKeySelect = rowKey;
	};
	return{
		init: init,
		setRowKeySelect: setRowKeySelect
	}
}());

$(document).ready(function () {
	$.jqx.theme = 'olbius';
	viewInvoiceItemTypeObj.init();
});