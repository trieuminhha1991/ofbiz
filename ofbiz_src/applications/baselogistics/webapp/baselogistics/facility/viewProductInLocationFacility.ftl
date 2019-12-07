<div id="jqxwindowPopupViewerProductsInLocations" class="hide popup-bound">
	<div>${uiLabelMap.ListProductByLocation}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="overflow-x: hidden">
	        <div class="row-fluid">
				<div class="span12 no-left-margin">
					<div class="disable-scroll" id="jqxTabsViewerProductsInLocations">
					</div>
				</div>
			</div>
			<div class="form-action popup-footer">
                <button id="alterCancelViewerProductsInLocations" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	        </div>
		</div>
	</div>
</div>

<script>
	//VIEW PRODUCT IN LOCATION FACILITY
	$("#jqxwindowPopupViewerProductsInLocations").jqxWindow({theme: 'olbius',
	    width: 1200, minWidth: 500, maxWidth: 1500, minHeight: 300, height: 470, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelViewerProductsInLocations"), modalOpacity: 0.7
	});

	$('#jqxwindowPopupViewerProductsInLocations').on('close', function (event) {
		setTimeout(function(){
			$("#viewProduct").attr('disabled', true);
			$("#addProduct").attr('disabled', true);
			$("#moveProductTo").attr('disabled', true);
			$("#btnCancelReset").attr('disabled', true);
		}, 500);
		enableScrolling();
		$('#jqxTabsViewer').jqxTabs('destroy');
		reset();
	});
	$("#jqxwindowPopupViewerProductsInLocations").on('open', function (event) {
		disableScrolling();
	});
	
	$("#viewProduct").click(function () {
		if (arrayLocationId.length == 0) {
			bootbox.dialog('${StringUtil.wrapString(uiLabelMap.ChooseLocation)}', [{
	            "label" : '${uiLabelMap.OK}',
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            }]
	        );
			return;
		}
		renderHtmlGridViewer();
		updateMode = false;
		moveMode = false;
		$('#jqxTabsViewer').jqxTabs({width: '96%', height: 366, theme: 'olbius'});
		dataInLocation = getListInventoryItemInLocation();
		for ( var s in arrayLocationId) {
			for ( var d in dataInLocation[arrayLocationId[s]]) {
				dataInLocation[arrayLocationId[s]][d].expireDate == undefined?dataInLocation[arrayLocationId[s]][d].expireDate = null : dataInLocation[arrayLocationId[s]][d].expireDate = dataInLocation[arrayLocationId[s]][d].expireDate['time'];
				dataInLocation[arrayLocationId[s]][d].datetimeReceived == undefined?dataInLocation[arrayLocationId[s]][d].datetimeReceived = null : dataInLocation[arrayLocationId[s]][d].datetimeReceived = dataInLocation[arrayLocationId[s]][d].datetimeReceived['time'];
			}
			bindDataGridViewer(arrayLocationId[s]);
		}
		$("#jqxwindowPopupViewerProductsInLocations").jqxWindow('open');
	});
	
	function renderHtmlGridViewer() {
		var htmlRenderTabs = "<div id='jqxTabsViewer' style='margin-left: 20px !important; height: 356px !important; margin-right: 20px !important; border: 1px solid #CCC !important;'><ul style='margin-left: 8px'>";
		var htmlRenderGrids = "";
		for ( var x in arrayLocationId) {
			var gridId = "jqxGridViewer" + arrayLocationId[x];
	        htmlRenderTabs += "<li style=\"margin-top: 6px; height: 15px !important; border-bottom-width: 0px; border-color: white;\">" + getLocationCode(arrayLocationId[x]) + "</li>";
	        htmlRenderGrids += "<div style='overflow: hidden; height: 313px !important; '><div style='border:none;' id=" + gridId + " ></div></div>";
		}
		htmlRenderTabs += "</ul>" + htmlRenderGrids + "</div>";
		$("#jqxTabsViewerProductsInLocations").html(htmlRenderTabs);
	}
	function bindDataGridViewer(gridId) {
		var activeGrid = "jqxGridViewer" + gridId;
		var dataViewer = dataInLocation[gridId];
		var sourceViewer =
        {
            localdata: dataViewer,
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
                { name: 'statusId', type: 'string' },
                { name: 'quantityUomId', type: 'string' }
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
        var dataAdapterViewer = new $.jqx.dataAdapter(sourceViewer);
        $("#" + activeGrid).jqxGrid({
                source: dataAdapterViewer,
                localization: getLocalization(),
                width: '100%',
                theme: 'olbius',
                selectionmode: 'singlerow',
                height: '100%',
                editable:false,
                sortable: true,
                pageable: true,
                columns: [
                   { text: '${uiLabelMap.InventoryItemId}', dataField: 'inventoryItemId', align: 'left', width: 100, editable:false },
                   { text: '${uiLabelMap.ProductId}', dataField: 'productId', align: 'left', width: 130, editable:false },
                   { text: '${uiLabelMap.ProductName}', dataField: 'productName', align: 'left', minwidth: 200, editable:false },
                   { text: '${StringUtil.wrapString(uiLabelMap.Status)}', dataField: 'statusId', align: 'left', width: 150, editable:false,
	   					cellsrenderer: function(row, colum, value){
	   						for(i=0; i < statusData.length; i++){
	   				            if(statusData[i].statusId == value){
	   				            	return '<span style=\"text-align: left;\" title='+value+'>' + statusData[i].description + '</span>';
	   				            }
	   				        }
	   						if (!value){
	   			            	return '<span style=\"text-align: left;\" title='+value+'>${uiLabelMap.InventoryGood}</span>';
	   						}
	   					}
   					},
	               { text: '${uiLabelMap.Quantity}', dataField: 'quantity', align: 'left', width: 130, cellsalign: 'right', columntype:"numberinput", filtertype: 'number', 
                	   validation: function (cell, value) {
                		   var result = validateQuantity(value);
                		   return result;
                	    },
                	    cellsrenderer: function(row, colum, value){
							return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
						}
	               },
	               { text: '${uiLabelMap.ReceivedDate}', dataField: 'datetimeReceived', align: 'left', width: 120, editable:false, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
	               { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', align: 'left', width: 120, editable:false, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
	               { text: '${uiLabelMap.Unit}', dataField: 'uomId', align: 'left', filtertype: 'checkedlist', editable:false, width: 120,
						cellsrenderer: function(row, colum, value){
					        var data = $("#" + activeGrid).jqxGrid('getrowdata', row);
							var productId = data.productId;
							if (value){
								return '<span>' + mapQuantityUom[mapProductWithUom[productId]] + '</span>';
							} else {
								if (data.quantityUomId){
	    			        		return '<span>' + mapQuantityUom[data.quantityUomId] + '</span>';
	    			        	} else {
	    			        		return '<span>_NA_</span>';
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
	              }
                ]
          });
	}
</script>