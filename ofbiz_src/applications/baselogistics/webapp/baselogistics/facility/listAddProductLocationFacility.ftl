<div id="jqxwindowPopupAdderProductToLocation" style="display:none;">
	<div>${uiLabelMap.AddProductToLocation}</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="overflow-x: hidden">
	    	<h4 class="row header smaller lighter blue" style="margin: 5px 25px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
	            ${uiLabelMap.ListProductNotHasLocation} (${uiLabelMap.DragAndDropProductToLocation})
	        </h4>
			<div class="row-fluid">
				<div class="span12 no-left-margin">
					<div id="gridFrom" style="margin-left: 20px !important; margin-right: 20px !important; border: 1px solid #CCC !important;"></div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12 no-left-margin">
					<div class="disable-scroll" id="jqxTabsContain">
					</div>
				</div>
			</div>
			<div class="form-action">
	            <div class='row-fluid'>
	                <div class="span12 margin-top20">
	                    <button id="alterCancelAdderProductToLocation" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                    <button id="alterSaveAdderProductToLocation" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	                </div>
	            </div>
	        </div>
		</div>
	</div>
</div>

<script>
	//ADD PRODUCT TO LOCATION FACILITY
	$("#jqxwindowPopupAdderProductToLocation").jqxWindow({theme: 'olbius', minWidth: 700, maxHeight: 1400, maxWidth: 1200, width: 1200, minHeight: 400, height: 600, resizable: false,  isModal: true, autoOpen: false, cancelButton: $("#alterCancelAdderProductToLocation"), modalOpacity: 0.7
	});

	$("#addProduct").click(function () {
		if (arrayLocationId.length == 0) {
			bootbox.dialog('${StringUtil.wrapString(uiLabelMap.ChooseLocation)}', [{
	            "label" : '${uiLabelMap.OK}',
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            }]
	        );
			return;
		}
		getAllProductNotLocationAjax();
	});
	
	function getAllProductNotLocationAjax() {
		var listProductNotLocation = [];
		$.ajax({
			url: "getAllProductNotLocationAjax",
			type: "POST",
			data: {facilityId: facilityIdGlobal},
			dataType: "json",
			async: false,
			success: function(res) {
				listProductNotLocation = res["listProductNotLocation"];
			}
		}).done(function() {
			renderAddProduct(listProductNotLocation);
		});
	}
	
	var idGridActive;
	var listProductNotLocationTemp = [];
	var dataInLocation;
	function renderAddProduct(listProductNotLocation) {
		for ( var d in listProductNotLocation) {
			listProductNotLocation[d].expireDate == undefined?listProductNotLocation[d].expireDate = null : listProductNotLocation[d].expireDate = listProductNotLocation[d].expireDate['time'];
			listProductNotLocation[d].datetimeReceived == undefined?listProductNotLocation[d].datetimeReceived = null : listProductNotLocation[d].datetimeReceived = listProductNotLocation[d].datetimeReceived['time'];
		}
		updateMode = false;
		moveMode = false;
		dataInLocation = getListInventoryItemInLocation();
		renderHtmlContainGrids();
		$('#jqxTabsLocation').jqxTabs({height: 232, theme: 'olbius', width: '96%'});
		idGridActive = arrayLocationId[0];
		$('#jqxTabsLocation').on('selected', function (event) { 
		    var selectedTab = event.args.item;
		    idGridActive = arrayLocationId[selectedTab];
		    changeTargetWhenGridActive();
		});
		for ( var s in arrayLocationId) {
			for ( var d in dataInLocation[arrayLocationId[s]]) {
				dataInLocation[arrayLocationId[s]][d].expireDate == undefined?dataInLocation[arrayLocationId[s]][d].expireDate = null : dataInLocation[arrayLocationId[s]][d].expireDate = dataInLocation[arrayLocationId[s]][d].expireDate['time'];
				dataInLocation[arrayLocationId[s]][d].datetimeReceived == undefined?dataInLocation[arrayLocationId[s]][d].datetimeReceived = null : dataInLocation[arrayLocationId[s]][d].datetimeReceived = dataInLocation[arrayLocationId[s]][d].datetimeReceived['time'];
			}
			bindDataGridTo(arrayLocationId[s]);
			handleOldvalue(arrayLocationId[s]);
		}
		changeTargetWhenGridActive();
		var source =
        {
            localdata: listProductNotLocation,
            datafields:
            [
                { name: 'productId', type: 'string' },
                { name: 'productCode', type: 'string' },
                { name: 'productName', type: 'string' },
                { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
                { name: 'quantityOnHandTotal', type: 'number' },
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'inventoryItemId', type: 'string' },
                { name: 'uomId', type: 'string' },
                { name: 'statusId', type: 'string' },
                { name: 'quantityUomId', type: 'string' },
            ],
        };
		listProductNotLocationTemp = listProductNotLocation;
		var dataAdapter = new $.jqx.dataAdapter(source);
        $("#gridFrom").jqxGrid({
            source: dataAdapter,
            localization: getLocalization(),
            showfilterrow: true,
            filterable: true,
            height: 212,
            width: '96%',
            theme: 'olbius',
            selectionmode: 'singlecell',
            pageable: true,
            sortable: true,
            columns: [
              { text: '${uiLabelMap.InventoryItemId}', dataField: 'inventoryItemId', align: 'left', width: 100, editable:false },
              { text: '${uiLabelMap.ProductId}', dataField: 'productId', align: 'left', width: 120 , cellclassname: cellclassname },
              { text: '${uiLabelMap.ProductName}', dataField: 'productName', align: 'left', minwidth: 200 },
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
              { text: '${uiLabelMap.Quantity}', dataField: 'quantityOnHandTotal', align: 'left', filtertype: 'number', width: 120, cellsalign: 'right',
            	  cellsrenderer: function(row, colum, value){
						return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
            	  }
              },
              { text: '${uiLabelMap.ReceivedDate}', dataField: 'datetimeReceived', align: 'left', width: 130, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
              { text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', align: 'left', width: 130, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
              { text: '${uiLabelMap.Unit}', dataField: 'uomId', align: 'left', filtertype: 'checkedlist', width: 120,
            	  cellsrenderer: function(row, colum, value){
    			        var data = $('#gridFrom').jqxGrid('getrowdata', row);
    			        var productId = data.productId;
    			        if (value){
    			        	return '<span>' + mapQuantityUom[value] + '</span>';
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
            ],
            handlekeyboardnavigation: function (event) {
                var key = event.charCode ? event.charCode : event.keyCode ? event.keyCode : 0;
                if (key == 70 && event.ctrlKey) {
                	$('#gridFrom').jqxGrid('clearfilters');
                	return true;
                }
            },
        });
        var wtmp = window;
    	var tmpwidth = $('#jqxwindowPopupAdderProductToLocation').jqxWindow('width');
        $("#jqxwindowPopupAdderProductToLocation").jqxWindow('open');
	}
	
	$("#alterSaveAdderProductToLocation").click(function () {
		bootbox.dialog(uiLabelMap.AreYouSureSave, 
		[{"label": uiLabelMap.CommonCancel, 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
		    "callback": function() {bootbox.hideAll();}
		}, 
		{"label": uiLabelMap.OK,
		    "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
		    "callback": function() {
		    	Loading.show('loadingMacro');
		    	setTimeout(function(){		
		    		var totalRecord = new Array();
					for ( var x in arrayLocationId) {
						totalRecord.push.apply(totalRecord, dataInLocation[arrayLocationId[x]]);
					}
					for ( var z in totalRecord) {
						if (typeof totalRecord[z].expireDate == 'object') {
							if (totalRecord[z].expireDate){
								totalRecord[z].expireDate = totalRecord[z].expireDate.getTime();
							}
						}
					}
					saveDataToLocation(totalRecord);
					$("#jqxwindowPopupAdderProductToLocation").jqxWindow('close');
		    	Loading.hide('loadingMacro');
		    	}, 500);
		    }
		}]);	
	});
	
	function saveDataToLocation(totalRecord) {
		var result;
		$.ajax({
  		  url: "addToLocationAjax",
  		  type: "POST",
  		  data: {totalRecord: JSON.stringify(totalRecord)},
  		  dataType: "json",
  		  async: true,
  		  success: function(res) {
  			  result = res["RESULT_MESSAGE"];
  		  }
	  	}).done(function() {
	  		if (updateMode) {
				location.reload();
			}
	  		getListlocationFacility(facilityIdGlobal);
	  		getGeneralQuantity();
	  		checkHasInventoryInLocation();
	  		if (checkProductNotLocationAjax()) {
				$("#divHasProductsNotLocation").text("${StringUtil.wrapString(uiLabelMap.HasProductsNotLocation)}");
			}else {
				$("#divHasProductsNotLocation").text("");
			}
	  		$('#jqxNotificationNested').jqxNotification('closeLast');
	  		if (result == "SUSSESS") {
	  			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
              	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
              	$("#jqxNotificationNested").jqxNotification("open");
              	isNeedReposition = false;
			} else {
				$("#jqxNotificationNested").jqxNotification({ template: 'error'});
    			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.UpdateError)}");
              	$("#jqxNotificationNested").jqxNotification("open");
			}
	  	});
	}
</script>