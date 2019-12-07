 <script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
 
 <#assign dataField="[{ name: 'productId', type: 'string'},
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
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', datafield: 'productId', editable: true, columntype: 'combobox',
			cellsrenderer: function(row, colum, value){
				for(var i = 0; i < product.length; i++){
					if(value == product[i].productId){
						return '<div style=\"margin: inherit;\">' + product[i].description + '</div>';
					}
				}
			},
	        createeditor: function (row, cellvalue, editor, celltext, cellwidth, cellheight) {
				editor.jqxComboBox({source: product, displayMember:'description', valueMember: 'productId'});
	        }
//	        validation: function (cell, value) {
//		        if (!value || value == null || value == '') {
//		            return { result: false, message: \"${StringUtil.wrapString(uiLabelMap.NotAllowEmpty)}\" };
//		        }
//		        return true;
//		    }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.globalTradeItemNumber)}', datafield: 'globalTradeItemNumber', width: 120, editable: true},
		{ text: '${StringUtil.wrapString(uiLabelMap.batchNumber)}', datafield: 'batchNumber', width: 150, editable: true },
		{ text: '${StringUtil.wrapString(uiLabelMap.packingUnits)}', datafield: 'packingUnit', width: 120, editable: true, columntype:'numberinput', cellsalign: 'right',
			cellsrenderer: function(row, colum, value){
     		   return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +value.toLocaleString('${localeStr}')+ '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.packingUomId)}', hidden: true, dataField: 'packingUomId', width: 80, editable: true},
		{ text: '${StringUtil.wrapString(uiLabelMap.orderUnits)}', datafield: 'orderUnit', width: 120, editable: true, columntype:'numberinput', cellsalign: 'right',
			cellsrenderer: function(row, colum, value){
	     		   return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +value.toLocaleString('${localeStr}')+ '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.orderUomId)}', hidden: true, dataField: 'orderUomId', width: 80, editable: true},
		{ text: '${StringUtil.wrapString(uiLabelMap.originOrderUnit)}', datafield: 'originOrderUnit', width: 180, editable: false, cellsalign: 'right', 
			cellsrenderer: function(row, colum, value){
	     		   return '<div style=\"text-align: right; margin-right:10px;margin-top: 5px;\">' +value.toLocaleString('${localeStr}')+ '</div>';
			}
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.dateOfManufacture)}', datafield: 'datetimeManufactured', width: 120, editable: true, columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy',
			initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
        		var uid = $('#jqxgridPackingListDetail').jqxGrid('getrowid', row);
//        		var datemanu = $('#jqxgridPackingListDetail').jqxGrid('getcellvaluebyid', row, 'datetimeManufactured');
//        		editor.jqxDateTimeInput('setMinDate', datemanu);
        		editor.on('change', function(event){
        			var jsDate = event.args.date;
//        			$('#jqxgridPackingListDetail').jqxGrid('setcellvaluebyid', uid, 'expireDate', null);
        		});
			}
//			cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
//				$('#jqxgridPackingListDetail').jqxGrid('begincelledit', row, 'expireDate');
//		    }
//			geteditorvalue: function (row, cellvalue, editor) {
//			    return editor.find('input').val();
//			}
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
//			cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
//				var uid = $('#jqxgridPackingListDetail').jqxGrid('getrowid', row);
//				var data = $('#jqxgridPackingListDetail').jqxGrid('getrowdatabyid', uid);
//				var datemanu = data.datetimeManufactured;
//				var productId = data.productId;
//				if(newvalue && newvalue != null && newvalue != ''){
//					if (datemanu && datemanu != null && datemanu != '') {
//						
//						if(productId && productId != null && productId != ''){
//							//thong bao QA shelf life
//						}else{
//							//thong bao san pham chua dc chon
//							retun
//						}
//					}
//				}else{
//					
//				}
//     	       	return executeQualityPublication(data, value, productIdComboBoxCell);
//		    }
		}
		
		"/>
<@jqGrid id="jqxgridPackingListDetail" filtersimplemode="false" addType="" dataField=dataField columnlist=columnlist viewSize="5"
		showtoolbar="true" addrow="false" deleterow="false" editable="true" isShowTitleProperty="true" width="1240" height="250"
		clearfilteringbutton="false" customcontrol3="icon-plus open-sans@${uiLabelMap.AddDetailPL}@javascript: void(0);@btnAddNewRowDetail()" sortable="false"
		customcontrol1="icon-trash open-sans@${uiLabelMap.CommonDelete}@javascript: void(0);@btnRemoveRowDetail()"
		customcontrol2="icon-refresh open-sans@${uiLabelMap.AddPL}@javascript: void(0);@btnAddNewPL()" showlist="false"
		url="" customTitleProperties="Detail" bindresize="false" autoheight="false" filterable="false" updateoffline="true" editmode="selectedcell"
		customLoadFunction = "true"/>

<script type="text/javascript">
//	//BEGIN cell end edit
//	$("#jqxgridPackingListDetail").on('cellEndEdit', function (event) {
//		    var args = event.args;
//		    var dataField = event.args.datafield;
//		    var rowBoundIndex = event.args.rowindex;
//		    var value = args.value;
////		    var oldvalue = args.oldvalue;
////		    var rowData = args.row;
//		    if(dataField == "datetimeManufactured"){
//		    	
//		    }else if(dataField == "expireDate"){
//		    	var uid = $('#jqxgridPackingListDetail').jqxGrid('getrowid', rowBoundIndex);
//				var data = $('#jqxgridPackingListDetail').jqxGrid('getrowdatabyid', uid);
//				var datemanu = data.datetimeManufactured;
//				var productId = data.productId;
//				if (datemanu && datemanu != null && datemanu != '') {
//					if(productId && productId != null && productId != ''){
//						//thong bao QA shelf life
////						executeQualityPublication(data, value, productId);
//					}else{
////						//thong bao san pham chua dc chon
////						bootbox.dialog("Chua chon san pham!", [{
////							"label" : "error",
////							"class" : "btn-small btn-danger",
////							"callback": function(){
//////								$('#jqxgridPackingListDetail').jqxGrid('begincelledit', rowBoundIndex, 'productId');
////							}
////							}]
////						);
//					}
//				}else{
//					
//				}
//		    }
//		});
//	//END cell end edit
//
//	function btnAddNewRowDetail(){
//		var datarow = {originOrderUnit: 0};
//        $("#jqxgridPackingListDetail").jqxGrid('addrow', null, datarow,'first');
//	}
//	
//	function btnRemoveRowDetail(){
//		var selectedrowindex = $("#jqxgridPackingListDetail").jqxGrid('getselectedrowindex');
//		var rowscount = $("#jqxgridPackingListDetail").jqxGrid('getdatainformation').rowscount;
//		if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
//	         var id = $("#jqxgridPackingListDetail").jqxGrid('getrowid', selectedrowindex);
//	         var commit = $("#jqxgridPackingListDetail").jqxGrid('deleterow', id);
//	     }
//	}
</script>