<#assign dataField = "[
					{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'categoryId', type: 'string' },
					{ name: 'facilityId', type: 'string' },
					{ name: 'facilityCode', type: 'string' },
					{ name: 'facilityName', type: 'string' },
					{ name: 'idSKU', type: 'string' },
					{ name: 'primaryUPC', type: 'string' },
					{ name: 'listUPCs', type: 'string' },
					{ name: 'qohEA', type: 'number'},
					{ name: 'quantityConvert', type: 'number'},
					{ name: 'qohQC', type: 'number'},
					{ name: 'uomId', type: 'string'},
					{ name: 'numberDayInv', type: 'number'},"/>
			
<#if flag == "InventoryTurnoverRatio">
	<#assign dataField = dataField + "{ name: 'ratioSKU', type: 'number'}" />
</#if>
<#assign dataField = dataField + "]" />

<#assign columnlist = "
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.FacilityId}', dataField: 'facilityCode', pinned: true,
						cellsrenderer: function(row, colum, value){
							if(!value){
								var data = $('#inventoryByDateProduct').jqxGrid('getrowdata', row);
								value = data.facilityId;
							}
							return '<span>' + value + '</span>';
					    }
					},
					{ text: '${uiLabelMap.BLCategoryProduct}', dataField: 'categoryId', pinned: true,},
					{ text: '${uiLabelMap.BLSKUCode}', dataField: 'productCode', pinned: true, width: 150},
					{ text: '${uiLabelMap.ProductName}', dataField: 'productName'},
					{ text: '${uiLabelMap.BSUPC}', datafield: 'idSKU', align: 'left', width: 160,
						cellsrenderer: function(row, colum, value){
							var data = $('#inventoryByDateProduct').jqxGrid('getrowdata', row);
							if(data.primaryUPC){
								return '<span title=\"' + '${uiLabelMap.BLDoubleClickToViewDetail}' +'\">' + data.primaryUPC + '</span>';
							}
							return '<span>' + value + '</span>';
					    }
					},
					{ text: '${uiLabelMap.BLQuantityEATotal}', dataField: 'qohEA', filtertype: 'number', cellsalign: 'right', width: 150,
						cellsrenderer: function (row, column, value) {
							if (value){
								return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
							} else {
								return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
							}
						}
					},
					{ text: '${uiLabelMap.BLPackingForm}', dataField: 'quantityConvert', filtertype: 'number', cellsalign: 'right', width: 100,
						filtertype: 'number',
						cellsrenderer: function (row, column, value) {
							if (value){
								return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
							} else {
								return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
							}
						}
					},
					{ text: '${uiLabelMap.BLQuantityByQCUom}', dataField: 'qohQC', filterable: false, sortable: false, cellsalign: 'right', width: 150,
						cellsrenderer: function (row, column, value) {
							if (value){
								return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
							} else {
								return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
							}
						}
					},
					{ text: '${uiLabelMap.NumberDayInInventory}', dataField: 'numberDayInv', filterable: false, sortable: true, cellsalign: 'right', width: 150,
						cellsrenderer: function (row, column, value) {
							if (value){
								return '<div class=\"text-right\">' + formatnumber(value) + '</div>';
							} else {
								return '<div class=\"text-right\">' + formatnumber(0) + '</div>';
							}
						}
					},
				"/>
<@jqGrid id="inventoryByDateProduct" url="jqxGeneralServicer?sname=JQListInventoryByProductReport&flag=${flag}" filtersimplemode="true" filterable="true" isSaveFormData="true"
	formData="filterObjData" dataField=dataField columnlist=columnlist usecurrencyfunction="true" clearfilteringbutton="true" customTitleProperties="BLReportInventoryByDate" 
	customcontrol1 = "fa-file-excel-o open-sans@${StringUtil.wrapString(uiLabelMap.BSExportExcel)}@javascript: void(0);@exportExcel2()" selectionmode="singlecell"/>

<div id="upcPopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.BLUPCCode}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div id="listUPC" style="overflow:auto; height: 100%"></div>
		</div>
	</div>
</div>

<script type="text/javascript">
	var filterObjData = new Object();
	var exportExcel2 = function(){
		var dataGrid = $("#inventoryByDateProduct").jqxGrid('getrows');
		if (dataGrid.length == 0) {
			jOlbUtil.alert.error("${uiLabelMap.BSNoDataToExport}");
			return false;
		}
	
		var winURL = "exportInventoryByDateProductExcel";
		var form = document.createElement("form");
		form.setAttribute("method", "POST");
		form.setAttribute("action", winURL);
		form.setAttribute("target", "_blank");
		
		if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
			$.each(filterObjData.data, function(key, value) {
				var hiddenField1 = document.createElement("input");
				hiddenField1.setAttribute("type", "hidden");
				hiddenField1.setAttribute("name", key);
				hiddenField1.setAttribute("value", value);
				form.appendChild(hiddenField1);
			});
		}
		
		document.body.appendChild(form);
		form.submit();
	};
	
	$("#upcPopupWindow").jqxWindow({
		maxWidth: 1000, minWidth: 200, width: 300, modalZIndex: 10000, zIndex:10000, minHeight: 100, 
		height: 300, maxHeight: 800, resizable: true, cancelButton: $("#locationCancel"), keyboardNavigation: true, keyboardCloseKey: 15,  
		isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
	});
	
	$("#inventoryByDateProduct").on("celldoubleclick", function (event) {
	    var args = event.args;
	    var rowBoundIndex = args.rowindex;
	    var rowVisibleIndex = args.visibleindex;
	    var rightClick = args.rightclick; 
	    var ev = args.originalEvent;
	    var columnIndex = args.columnindex;
	    var dataField = args.datafield;
	    var value = args.value;
	    if (dataField == "idSKU"){
	    	var data = $('#inventoryByDateProduct').jqxGrid('getrowdata', rowBoundIndex);
	    	if (data.listUPCs){
	    		var listUPCs = data.listUPCs;
	    		var primaryUPC = data.primaryUPC;
	    		var text = "<div style='font-weight: bold'> SKU: " + data.productCode + "</div>";
	    		for (var x in listUPCs){
	    			if (listUPCs[x].idValue != '' && listUPCs[x].idValue != null && listUPCs[x].idValue != 'null' && listUPCs[x].idValue != undefined){
	    				if (listUPCs[x].idValue == primaryUPC){
	    					text = text + "<div style='color: red'>" + listUPCs[x].idValue + " - " + "${uiLabelMap.BLPrimaryUPC}" + "</div>"; 
		    			} else {
		    				text = text + "<div>" + listUPCs[x].idValue + "</div>"; 
		    			}
	    			}
	    		}
	    		$("#listUPC").html(text);
	    		$("#upcPopupWindow").jqxWindow('open');
	    	}
	    }
	});
</script>