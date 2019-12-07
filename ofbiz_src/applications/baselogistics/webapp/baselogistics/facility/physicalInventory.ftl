<div id="divPhysicalInventory" class="hide">
	<div class='form-window-container'>
		<div class='form-window-content' style="overflow-x: hidden; margin-bottom: 10px;">
			<div class="row-fluid">
				<div class="span12 no-left-margin">
					<div id="gridPhysicalInventory"></div>
				</div>
			</div>
		    <div class='row-fluid'>
		        <div class="span12" >
			        <button class="btn btn-small btn-danger form-action-button pull-right" id='cancelExport'><i class='icon-remove'></i>${uiLabelMap.CommonCancel}</button>
					<button class="btn btn-small btn-primary form-action-button pull-right" id='exportExcel'><i class=' icon-upload-alt'></i>${uiLabelMap.ExportExcel}</button>
					<button class="btn btn-small btn-primary form-action-button pull-right" id='updatePhysicalInventory'><i class='icon-edit'></i>${uiLabelMap.UpdatePhysicalInventory}</button>
				</div>
		    </div>
		</div>
	</div>
</div>

<div id="divCreatePhysicalInventoryAndVariance" style="display: none;">
	<div id="gridCreatePhysicalInventoryAndVariance"></div>
	<div class="row-fluid">
		<div class="span12 margin-top20" style="margin-bottom:10px;">
			<#if hasOlbPermission("MODULE", "LOG_FACILITY", "VIEW")>
				<button id="cancelExportCreatePhysicalInventoryAndVariance" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
				<button id="updatePhysicalInventoryAndVariance" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
			</#if>
			<button style="display: none;float:right;" id='exportCreatePhysicalInventoryAndVariance'><i class=' icon-download-alt'></i>${uiLabelMap.ExportExcel}</button>
	    </div>
	</div>
</div>
<script>
	//INVENTORY WAREHOUSE (KIEM KHO)
	function physicalInventory() {
		getAllProductForPhysicalInventory(null, facilityIdGlobal);
		$("#gridPhysicalInventoryClearFilters").css("display", "block");
		$("#gridPhysicalInventoryClearFilters").css("float", "right");
		$("#gridPhysicalInventoryClearFilters").css("margin-top", "5px");
		$("#searchGrid").css({ "display": "none"});
		$("#divCreatePhysicalInventoryAndVariance").css({ "display": "none"});
		$("#searchWraper").css({ "display": "none"});
		$("#Gridcontent").animate({ "margin-top": "0px"});
		$('#jqxInputSearch').val('');
		$("#searchGrid").css({ "display": "none"});
	    listProductAvalible = [];
	}
	
	function getAllProductForPhysicalInventory(productId, facilityId) {
		var allProduct = [];
		$.ajax({
	  		  url: "getListProductAvalibleAjax",
	  		  type: "POST",
	  		  data: {productId: productId, facilityId: facilityId},
	  		  dataType: "json",
	  		  async: false,
	  		  success: function(res) {
	  			allProduct = listProductAvalible.concat(res["listProductAvalible"]);
	  		  }
		  	}).done(function() {
		  		
		  	});
		bindGridForPhysicalInventory(allProduct);
		renderGridCreatePhysicalInventoryAndVariance(allProduct);
	}
	
	function bindGridForPhysicalInventory(allProduct) {
		for ( var x in allProduct) {
			if (typeof allProduct[x].expireDate == 'object') {
				if (allProduct[x].expireDate){
					allProduct[x].expireDate = allProduct[x].expireDate['time'];
				} else {
					allProduct[x].expireDate = null;
				}
				if (allProduct[x].datetimeReceived){
					allProduct[x].datetimeReceived = allProduct[x].datetimeReceived['time'];
				} else {
					allProduct[x].datetimeReceived = null;
				}
			}
 		}
		var sourceForPhysicalInventory =
        {
            localdata: allProduct,
            datatype: "local",
            datafields:
            [
                { name: 'inventoryItemId', type: 'string' },
                { name: 'productId', type: 'string' },
                { name: 'productName', type: 'string' },
                { name: 'locationId', type: 'string' },
                { name: 'quantity', type: 'number' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'uomId', type: 'string' },
                { name: 'quantityUomId', type: 'string' },
            ],
            addrow: function (rowid, rowdata, position, commit) {
            	
                commit(true);
            },
            deleterow: function (rowid, commit) {
                
                commit(true);
            },
            updaterow: function (rowid, newdata, commit) {
            	
                commit(true);
            }
        };
        var dataAdapterForPhysicalInventory = new $.jqx.dataAdapter(sourceForPhysicalInventory);
        $("#gridPhysicalInventory").jqxGrid({
            source: dataAdapterForPhysicalInventory,
            localization: {groupsheaderstring: "${StringUtil.wrapString(uiLabelMap.Groupsheaderstring)}", filterselectstring: "${StringUtil.wrapString(uiLabelMap.wgfilterselectstring)}"},
            showfilterrow: true,
            filterable: true,
            editable:false,
            handlekeyboardnavigation: function (event) {
                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                if (key == 70 && event.ctrlKey) {
                	ClearFiltersGridPhysicalInventory();
                	return true;
                }
		 	},
		 	groupable: true,
            autoheight: true,
            theme: 'olbius',
            sortable: true,
            pageable: true,
            showaggregates: true,
            showstatusbar: true,
            width: '100%',
            statusbarheight: 40,
            selectionmode: 'singlerow',
            columns: [
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					  groupable: false, draggable: false, resizable: false,
					  datafield: '', columntype: 'number', width: 50,
					  cellsrenderer: function (row, column, value) {
						  return '<div style=margin:4px;>' + (value + 1) + '</div>';
					  }
				},      
				{ text: '${StringUtil.wrapString(uiLabelMap.FacilityLocationPosition)}', dataField: 'locationId', editable:false, filtertype: 'checkedlist', align: 'left', cellsalign: 'left', minwidth: 200,
				    cellsrenderer: function(row, colum, value){
				  	  var locationCode = getLocationCode(value);
				  	  return '<span>' + locationCode + '</span>';
				    },
				    createfilterwidget: function (column, htmlElement, editor) {
                		  var listProductAvalibleInFacility = getAllProductInFacility();
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listlocationFacility), displayMember: 'locationId', valueMember: 'locationId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return getLocationCode(value);
				                }
	    		        	});
	    		        	editor.jqxDropDownList('checkAll');
                    }
				},
                  { text: '${StringUtil.wrapString(uiLabelMap.InventoryItemId)}', dataField: 'inventoryItemId', filtertype: 'input', align: 'left', cellsalign: 'left', width: 150, editable:false },
	              { text: '${StringUtil.wrapString(uiLabelMap.ProductId)}', dataField: 'productId', align: 'left', cellsalign: 'left', filtertype: 'checkedlist', editable:false, width: 180, cellclassname: cellclassname,
                	  createfilterwidget: function (column, htmlElement, editor) {
                		  var listProductAvalibleInFacility = getAllProductInFacility();
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(listProductAvalibleInFacility), displayMember: 'productId', valueMember: 'productId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return mapProductWithName[value];
				                }
	    		        	});
	    		        	editor.jqxDropDownList('checkAll');
	                    }
	              },
	              { text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', dataField: 'productName', filtertype: 'input', align: 'left', cellsalign: 'left', width: 200, editable:false },
	              { text: '${StringUtil.wrapString(uiLabelMap.ReceivedDate)}', dataField: 'datetimeReceived', align: 'left', editable:false, width: 200, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
	              { text: '${StringUtil.wrapString(uiLabelMap.ExpireDate)}', dataField: 'expireDate', align: 'left', editable:false, width: 200, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
	              { text: '${StringUtil.wrapString(uiLabelMap.Unit)}', dataField: 'uomId', editable:false, align: 'left', filtertype: 'checkedlist', cellsalign: 'right', width: 120,
	            	  cellsrenderer: function(row, colum, value){
	            		  var data = $("#gridPhysicalInventory").jqxGrid('getrowdata', row);
	            		  	if (value){
	            		  		return '<span style=\"text-align: right\">' + mapQuantityUom[value] + '</span>';
	            		  	} else {
	            		  		if (data.quantityUomId){
	    			        		return '<span style=\"text-align: right\">' + mapQuantityUom[data.quantityUomId] + '</span>';
	    			        	} else {
	    			        		if (data.quantityUomId){
	        			        		return '<span style=\"text-align: right\">' + mapQuantityUom[data.quantityUomId] + '</span>';
	        			        	} else {
	        			        		return '<span style=\"text-align: right\">_NA_</span>';
	        			        	}
	    			        	}
	            		  	}
	    		      },
		              createfilterwidget: function (column, htmlElement, editor) {
	    		        	editor.jqxDropDownList({ autoDropDownHeight: true, source: fixSelectAll(quantityUomData), displayMember: 'quantityUomId', valueMember: 'quantityUomId' ,
	                            renderer: function (index, label, value) {
	                            	if (index == 0) {
	                            		return value;
									}
								    return mapQuantityUom[value];
				                }
	    		        	});
	    		        	editor.jqxDropDownList('checkAll');
	                   }
	              },
	              { text: '${StringUtil.wrapString(uiLabelMap.Quantity)}', dataField: 'quantity', align: 'left', width: 150, columntype:'numberinput', cellsalign: 'right', filtertype: 'number',
	            	  cellsrenderer: function(row, colum, value){
							return '<span style=\"float: right;padding-right: 5px;text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
	            	  },
						aggregates: ['sum'],
						aggregatesrenderer: function (aggregates) {
							var renderstring = "";
							$.each(aggregates, function (key, value) {
								renderstring += '<div style="position: relative; margin: 4px; overflow: hidden;">' + '${uiLabelMap.Total}' + ': ' + value.toLocaleString('${localeStr}') +'</div>';
							});
							return renderstring;
						},
						createeditor: function(row, column, editor){
							editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
						}
	              }
	            ]
        });
        $("#divPhysicalInventory").css({ "display": "block"});
        $("#Gridcontent").css({ "display": "none"});
	}
	$("#updatePhysicalInventory").click(function () {
		$("#divPhysicalInventory").css({ "display": "none"});
        $("#divCreatePhysicalInventoryAndVariance").css({ "display": "block"});
	});
</script>
<#include 'updatePhysicalInventory.ftl'/>