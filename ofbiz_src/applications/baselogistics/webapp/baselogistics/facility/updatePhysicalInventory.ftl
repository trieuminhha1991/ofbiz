<div id="divInputVariance" class="hiden popup-bound">
	<div>${uiLabelMap.UpdateQuantityProduct}</div>
	<div style="overflow-x: hidden;">
		<div class="row-fluid">
			<div class="span12">
				<div class="span3" style="text-align: right;">${StringUtil.wrapString(uiLabelMap.Quantity)}<span style="color:red;"> *</span></div>
				<div class="span9"><div id="txtQuantityVariance"></div><label id="lblPacking" style="color:#037c07;position: absolute;margin-left: 230px;margin-top: -25px;"></label></div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span12">
				<div class="span3" style="text-align: right;">${StringUtil.wrapString(uiLabelMap.Comments)}<span style="color:red;"> *</span></div>
				<div class="span9"><textarea  class="note-area no-resize" id="tarCommentsVariance" autocomplete="off" style="resize: none; width: 189px"></textarea></div>
			</div>
		</div>
		<div class="form-action popup-footer">
		    <button id="alterCancelInputVariance" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		    <button id="alterSaveInputVariance" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	    </div>
	</div>
</div>

<script>
	function renderGridCreatePhysicalInventoryAndVariance(allProduct) {
		for ( var x in allProduct) {
			if (typeof allProduct[x].expireDate == 'object') {
				allProduct[x].expireDate = allProduct[x].expireDate['time'];
			}
			allProduct[x].locationCode = getLocationCode(allProduct[x].locationId);
 		}
		var sourceGridCreatePhysicalInventoryAndVariance =
        {
            localdata: allProduct,
            datatype: "local",
            datafields:
            [
                { name: 'inventoryItemId', type: 'string' },
                { name: 'productId', type: 'string' },
                { name: 'productName', type: 'string' },
                { name: 'expireDate', type: 'date', other: 'Timestamp'},
                { name: 'locationId', type: 'string' },
                { name: 'statusId', type: 'string' },
                { name: 'uomId', type: 'string' },
                <#list listVarianceReason as reasonItem>
           		{ name: '${reasonItem.varianceReasonId}', type: 'number', formatter: 'integer'},
           		{ name: '${reasonItem.varianceReasonId}desr', type: 'string',},
   		</#list>
            ],
            addrow: function (rowid, rowdata, position, commit) {
            	
                commit(true);
            },
            deleterow: function (rowid, commit) {
                
                commit(true);
            },
            updaterow: function (rowid, newdata, commit) {
            	
                commit(true);
            },
        };
        var dataAdapterGridCreatePhysicalInventoryAndVariance = new $.jqx.dataAdapter(sourceGridCreatePhysicalInventoryAndVariance);
		$("#gridCreatePhysicalInventoryAndVariance").jqxGrid({
				source : dataAdapterGridCreatePhysicalInventoryAndVariance,
				localization: getLocalization(),
				editable: false,
				autoheight: true,
				editmode: 'selectedrow',
	            theme: 'olbius',
	            pageable: true,
	            width: '100%',
	            sortable: true,
	            selectionmode: 'singlerow',
	            showpinnedcolumnbackground: true,
	            columns: [
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					  groupable: false, draggable: false, resizable: false,
					  datafield: '', columntype: 'number', width: 50,
					  cellsrenderer: function (row, column, value) {
						  return '<div style=margin:4px;>' + (value + 1) + '</div>';
					  }
				},          
				{ text: '${StringUtil.wrapString(uiLabelMap.FacilityLocationPosition)}', dataField: 'locationId', pinned: true, editable: false, filtertype: 'input', align: 'left', cellsalign: 'left', minwidth: 200,
					    cellsrenderer: function(row, colum, value){
					  	  var locationCode = getLocationCode(value);
					  	  return '<span>' + locationCode + '</span>';
					    }
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.InventoryItemId)}', dataField: 'inventoryItemId', align: 'left', width: 150, editable:false },
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductId)}', dataField: 'productId', align: 'left', width: 180, editable:false},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', dataField: 'productName', align: 'left', width: 180, editable:false},
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
				{ text: '${StringUtil.wrapString(uiLabelMap.Unit)}', dataField: 'uomId', align: 'left', width: 180, editable:false,
					cellsrenderer: function(row, colum, value){
						var data = $("#gridCreatePhysicalInventoryAndVariance").jqxGrid('getrowdata', row);
						if(value){
							return '<span>' + mapQuantityUom[value] + '</span>';
						} else {
							if (data.quantityUomId){
    			        		return '<span>' + mapQuantityUom[data.quantityUomId] + '</span>';
    			        	} else {
    			        		if (data.quantityUomId){
        			        		return '<span>' + mapQuantityUom[data.quantityUomId] + '</span>';
        			        	} else {
        			        		return '<span>_NA_</span>';
        			        	}
    			        	}
						}
					},	
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ExpireDate)}', dataField: 'expireDate', align: 'left', width: 200, columntype: "datetimeinput", filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'left', editable:false },
				  
				<#list listVarianceReason as reasonItem>
					{ text: getDescriptionVarianceReason('${reasonItem.varianceReasonId}'), dataField: '${reasonItem.varianceReasonId}', columngroup: 'VarianceReasonDetails', align: 'left', cellsalign: 'right', columntype:'numberinput', width: 180,
						  cellsrenderer: function(row, colum, value){
							return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
						  },
						  createeditor: function(row, column, editor){
							editor.jqxNumberInput({inputMode: 'advanced', spinMode: 'simple', groupSeparator: '.', min:0 });
						  }
					},
					{ text: '', dataField: '${reasonItem.varianceReasonId}desr', columngroup: 'VarianceReasonDetails', hidden: true},
				</#list>
	            ],
	            columngroups: [
	                           { text: '${StringUtil.wrapString(uiLabelMap.VarianceReasonDetails)}', align: 'left', name: 'VarianceReasonDetails' }
	                       ]
	    });
	}
	
	$("#exportCreatePhysicalInventoryAndVariance").jqxToggleButton();
	$("#exportCreatePhysicalInventoryAndVariance").on('click', function () {
        var toggled = $("#exportCreatePhysicalInventoryAndVariance").jqxToggleButton('toggled');
        if (toggled) {
   		 	$("#exportCreatePhysicalInventoryAndVariance").html("<i class='icon-ok'></i>${StringUtil.wrapString(uiLabelMap.CommonSave)}");
   		 	$("#gridCreatePhysicalInventoryAndVariance").jqxGrid('exportdata', 'xls', '${StringUtil.wrapString(uiLabelMap.InventoryCountTheVotes)}');
        }else {
        	progessDataToCreatePhysicalInventoryAndVariance();
        }
    });
	
	function progessDataToCreatePhysicalInventoryAndVariance() {
		var gridCreatePhysicalInventoryAndVarianceData = $("#gridCreatePhysicalInventoryAndVariance").jqxGrid('getdisplayrows');
		
		for ( var z in gridCreatePhysicalInventoryAndVarianceData) {
			if (typeof gridCreatePhysicalInventoryAndVarianceData[z].expireDate == 'object') {
				gridCreatePhysicalInventoryAndVarianceData[z].expireDate = gridCreatePhysicalInventoryAndVarianceData[z].expireDate.getTime();
			}
		}
		var dataRecord = new Array();
		var partyId = "${userLogin.partyId}";
		
		for ( var s in gridCreatePhysicalInventoryAndVarianceData) {
			var thisObject = gridCreatePhysicalInventoryAndVarianceData[s];
			var inventoryItemId = thisObject.inventoryItemId;
			for ( var x in arrayVarianceReason) {
				if (_.has(thisObject, arrayVarianceReason[x])) {
					var varianceReasonId = arrayVarianceReason[x];
					var comments = varianceReasonId + "desr";
					var quantityOnHandVar = thisObject[varianceReasonId];
					var rowRecord = {};
					rowRecord.partyId = partyId;
					rowRecord.varianceReasonId = varianceReasonId;
					rowRecord.quantityOnHandVar = quantityOnHandVar;
					rowRecord.inventoryItemId = inventoryItemId;
					rowRecord.comments = thisObject[comments];
					dataRecord.push(rowRecord);
					createPhysicalInventoryAndVarianceAjax(rowRecord);
					// update inventoryItem location
					var dataInvLoc = {};
					dataInvLoc.inventoryItemId = thisObject.inventoryItemId;
					dataInvLoc.productId = thisObject.productId;
					dataInvLoc.locationId = thisObject.locationId;
					dataInvLoc.quantity = quantityOnHandVar;
					dataInvLoc.uomId = thisObject.uomId;
					updateInventoryItemInLocation(dataInvLoc);
					
					if (quantityOnHandVar < 0){
						var dataNewInv = {};
						dataNewInv.inventoryItemId = thisObject.inventoryItemId;
						dataNewInv.locationId = thisObject.locationId;
						dataNewInv.quantity = quantityOnHandVar;
						dataNewInv.quantityUomId = thisObject.uomId;
						createNewInventoryItemInLocation(dataNewInv);
					}
				}
			}
		}
		if (dataRecord.length > 0) {
			$('#jqxNotificationNested').jqxNotification('closeLast');
			$("#jqxNotificationNested").jqxNotification({ template: 'info'});
          	$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
          	$("#jqxNotificationNested").jqxNotification("open");
		}
		$("#gridCreatePhysicalInventoryAndVariance").jqxGrid('updatebounddata');
	}
	
	$("#gridCreatePhysicalInventoryAndVariance").on("celldoubleclick", function (event){
	    var args = event.args;
	    var rowBoundIndex = args.rowindex;
	    var rowVisibleIndex = args.visibleindex;
	    var rightClick = args.rightclick; 
	    var ev = args.originalEvent;
	    var columnIndex = args.columnindex;
	    var dataField = args.datafield;
	    var value = args.value;
	    if (_.indexOf(arrayVarianceReason, dataField) != -1) {
	    	varianceReasonIdActive = dataField;
	    	rowBoundIndexReasonIdActive = rowBoundIndex;
	    	var data = $("#gridCreatePhysicalInventoryAndVariance").jqxGrid('getrowdata', rowBoundIndex);
	    	var uomId = mapQuantityUom[data.uomId];
	    	$("#lblPacking").text("(" + uomId + ")");
	    	var wtmp = window;
	    	var tmpwidth = $('#divInputVariance').jqxWindow('width');
            $("#divInputVariance").jqxWindow({ position: { x: (wtmp.outerWidth - tmpwidth)/2, y: pageYOffset + 30 } });
	    	$("#divInputVariance").jqxWindow('open');
		}
	});
	
	$("#divInputVariance").jqxWindow({theme: 'olbius',
	    width: 450, maxWidth: 1845, minHeight: 200, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancelInputVariance"), modalOpacity: 0.7
	});
	$("#txtQuantityVariance").jqxNumberInput({theme: 'olbius', width: '200px', decimalDigits: 0, decimalSeparator: "." });
	$("#alterSaveInputVariance").click(function () {
		if ($('#divInputVariance').jqxValidator('validate')) {
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
			    		var thisQuantityVariance = $('#txtQuantityVariance').jqxNumberInput('val');
						var thisCommentsVariance = $("#tarCommentsVariance").val();
						thisCommentsVariance = thisCommentsVariance.trim();
						var commentForReason = varianceReasonIdActive + "desr";
						$("#gridCreatePhysicalInventoryAndVariance").jqxGrid('setcellvalue', rowBoundIndexReasonIdActive, varianceReasonIdActive, thisQuantityVariance);
						$("#gridCreatePhysicalInventoryAndVariance").jqxGrid('setcellvalue', rowBoundIndexReasonIdActive, commentForReason, thisCommentsVariance);
						$("#divInputVariance").jqxWindow('close');
			    	Loading.hide('loadingMacro');
			    	}, 500);
			    }
			}]);	
		}
	});
	$('#divInputVariance').jqxValidator({
    });
	$('#divInputVariance').on('close', function (event) {
		varianceReasonIdActive = "";
    	rowBoundIndexReasonIdActive = "";
    	enableScrolling();
    	$('#divInputVariance').jqxValidator('hide');
	});
	var openGuide = true;
	$("#divInputVariance").on('open', function (event) {
		$('#txtQuantityVariance').jqxNumberInput('val', 0);
		$("#tarCommentsVariance").val("");
		disableScrolling();
		/*if (openGuide) {
			bootbox.alert("${StringUtil.wrapString(uiLabelMap.GuideCreatePhysicalInventoryAndVariance)}");
		}
		openGuide = false;*/
	});
</script>
