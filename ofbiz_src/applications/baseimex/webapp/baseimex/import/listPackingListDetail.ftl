<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
 
 <#assign dataField="[
 	{ name: 'productId', type: 'string'},
 	{ name: 'productCode', type: 'string'},
 	{ name: 'productName', type: 'string'},
	{ name: 'packingListId', type: 'string'},
	{ name: 'packingListSeqId', type: 'string'},
	{ name: 'orderItemSeqId', type: 'string'},
	{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
	{ name: 'expireDate', type: 'date', other: 'Timestamp'},
	{ name: 'batchNumber', type: 'string'},
	{ name: 'globalTradeItemNumber', type: 'string'},
	{ name: 'packingUnit', type: 'number'},
	{ name: 'packingUomId', type: 'string'},
	{ name: 'orderUnit', type: 'number'},
	{ name: 'originOrderUnit', type: 'number'},
	{ name: 'orderUomId', type: 'string'},
	{ name: 'quantity', type: 'number'},
	{ name: 'itemDescription', type: 'string'}
	]"/>
<#assign columnlist="
	 	{ text: '${StringUtil.wrapString(uiLabelMap.SequenceId)}', sortable: false, filterable: false, editable: false, pinned: true,
        	  groupable: false, draggable: false, resizable: false,
        	  datafield: '', columntype: 'number', width: 50,
        	  cellsrenderer: function (row, column, value) {
        		  return '<div style=\"cursor: pointer;\">' + (value + 1) + '</div>';
        	  }
      	},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductId)}', datafield: 'productCode', editable: false, width: 120,
			cellsrenderer: function(row, colum, value){
			},
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', datafield: 'productName', editable: false, minwidth: 150,
			cellsrenderer: function(row, colum, value){
			},
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.globalTradeItemNumber)}', datafield: 'globalTradeItemNumber', width: 120, editable: true},
		{ text: '${StringUtil.wrapString(uiLabelMap.batchNumber)}', datafield: 'batchNumber', width: 120, editable: true },
		{ text: '${StringUtil.wrapString(uiLabelMap.packingUnits)}', datafield: 'packingUnit', width: 120, editable: true, columntype:'numberinput', cellsalign: 'right',
			cellsrenderer: function(row, colum, value){
     		   return '<div class=\"align-right\">' +formatnumber(value)+ '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.packingUomId)}', hidden: true, dataField: 'packingUomId', width: 80, editable: true},
		{ text: '${StringUtil.wrapString(uiLabelMap.orderUnits)}', datafield: 'orderUnit', width: 120, editable: true, columntype:'numberinput', cellsalign: 'right',
			cellsrenderer: function(row, colum, value){
	     		   return '<div class=\"align-right\">' +formatnumber(value)+ '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.orderUomId)}', hidden: true, dataField: 'orderUomId', width: 80, editable: true},
		{ text: '${StringUtil.wrapString(uiLabelMap.originOrderUnit)}', datafield: 'originOrderUnit', width: 120, editable: false, cellsalign: 'right', 
			cellsrenderer: function(row, colum, value){
	     		   return '<div class=\"align-right\">' +formatnumber(value)+ '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.dateOfManufacture)}', datafield: 'datetimeManufactured', width: 120, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',
			initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
        		var uid = $('#jqxgridPackingListDetail').jqxGrid('getrowid', row);
        		editor.on('change', function(event){
        			var jsDate = event.args.date;
        		});
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', datafield: 'expireDate', width: 120, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',
			initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
        		var uid = $('#jqxgridPackingListDetail').jqxGrid('getrowid', row);
        		var datemanu = $('#jqxgridPackingListDetail').jqxGrid('getcellvaluebyid', uid, 'datetimeManufactured');
        		if(datemanu && datemanu != null && datemanu != ''){
        			editor.jqxDateTimeInput('setMinDate', datemanu);
        		}else{
        			
        		}
			},
			validation: function (cell, value) {
				var uid = $('#jqxgridPackingListDetail').jqxGrid('getrowid', cell.row);
        		var datemanu = $('#jqxgridPackingListDetail').jqxGrid('getcellvaluebyid', uid, 'datetimeManufactured');
		        if ((!value || value == null || value == '') && (datemanu && datemanu != null && datemanu != '')) {
		        	return { result: false};
		        }
		        return true;
		    }
		}
		
		"/>
<@jqGrid id="jqxgridPackingListDetail" filtersimplemode="false" addType="" dataField=dataField columnlist=columnlist viewSize="10"
		showtoolbar="true" addrow="false" deleterow="false" editable="true" isShowTitleProperty="true"
		clearfilteringbutton="false" customcontrol3="icon-plus open-sans@${uiLabelMap.AddDetailPL}@javascript:ContainerManager.btnAddNewRowDetail()"
		customcontrol1="icon-trash open-sans@${uiLabelMap.CommonDelete}@javascript:ContainerManager.btnRemoveRowDetail()"
		customcontrol2="icon-refresh open-sans@${uiLabelMap.AddPL}@javascript: void(0);@btnAddNewPL()" showlist="false"
		url="" customTitleProperties="Detail" autoheight="true" filterable="true" updateoffline="true" editmode="selectedcell"
		customLoadFunction = "true"/>