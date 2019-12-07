<#include "script/fastSalesDeliveryScript.ftl"/>
<div id="popupDeliveryDetailWindow" class="hide popup-bound">
	<div id="titleDetailId">${uiLabelMap.DeliveryNote}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<div id="popupContainerNotify" style="width: 100%; overflow: auto;"></div>
			<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
				${uiLabelMap.GeneralInfo}
			</h4>
			<div class="row-fluid">
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.DeliveryId}:</div>
						<div class="span7"><div id="deliveryIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.Status}:</div>
					    <div class="span7"><div id="statusIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
		    		<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.RequireDeliveryDate}:</div>
		    		    <div class="span7"><div id="deliveryDateDT" class="green-label"></div></div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
	    		<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.DAOrderId}:</div>
		    		    <div class="span7"><div id="orderIdDT"class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.DeliveryBatchNumber}:</div>
		    		    <div class="span7" style="text-align: left;"><div id="noDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
	    			<div class="row-fluid">
			    		<div class="span5" style="text-align: right;">${uiLabelMap.EstimatedStartDate}:</div>
		    		    <div class="span7"><div id="estimatedStartDateDT" class="green-label"></div></div>
	    		    </div>
				</div>
			</div>
			<div class="row-fluid">
	    		<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.Sender}:</div>
					    <div class="span7"><div id="partyIdFromDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.Receiver}:</div>
		    		    <div class="span7"><div id="partyIdToDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
			    		<div class="span5" style="text-align: right;">${uiLabelMap.EstimatedArrivalDate}:</div>
		    		    <div class="span7"><div id="estimatedArrivalDateDT" class="green-label"></div></div>
				    </div>
				</div>
			</div>
			<div class="row-fluid">
	    		<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.ExportFromFacility}:</div>
					    <div class="span7"><div id="originFacilityIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.DeliveryReason}:</div>
					    <div class="span7"><div id="deliveryTypeDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" id="actualStartLabel" style="text-align: right;" class="asterisk">${uiLabelMap.ActualExportedDate}:</div>
					    <div class="span7">
					    	<div id="actualStartDateDis" class="green-label"></div>
					    	<div id="actualStartDate" class="green-label"></div>
				    	</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
	    		<div class="span4">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.OriginAddress}:</div>
		    		    <div class="span7"><div id="originContactMechIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
					   	<div class="span5" style="text-align: right;">${uiLabelMap.CustomerAddress}:</div>
		    		    <div class="span7"><div id="destContactMechIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
					<div class="row-fluid">
						<div class="span5" id="actualArrivalLabel" style="text-align: right;" class="asterisk">${uiLabelMap.ActualDeliveredDate}:</div>
					    <div class="span7">
					    	<div id="actualArrivalDateDis" class="green-label"></div>
					    	<div id="actualArrivalDate" class="green-label"></div>
					    </div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
	    		<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
					${uiLabelMap.ListProduct}
					<a style="float:right;font-size:14px; margin-right: 5px" id="addRow" href="javascript:FastSalesDlvObj.addNewRow()" data-rel="tooltip" title="${uiLabelMap.AddRow}" data-placement="bottom"><i class="icon-plus-sign open-sans"></i></a>
				</h4>
				<div style="margin-left: 20px"><div id="jqxgridDlvItem"></div></div>
			</div>
			<div class="form-action popup-footer">
	            <button id="fastCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
	            <button id="fastSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	        </div>
		</div>
	</div>
</div>

<script>

function loadDeliveryItem(valueDataSoure){
	var sourceProduct =
	    {
	        datafields:[{ name: 'deliveryId', type: 'string' },
	                 	{ name: 'deliveryItemSeqId', type: 'string' },
	                 	{ name: 'fromOrderItemSeqId', type: 'string' },
	                 	{ name: 'fromTransferItemSeqId', type: 'string' },
	                 	{ name: 'fromOrderId', type: 'string' },
	                 	{ name: 'fromTransferId', type: 'string' },
	                 	{ name: 'productId', type: 'string' },
	                 	{ name: 'productCode', type: 'string' },
	                 	{ name: 'productName', type: 'string' },
	                 	{ name: 'quantityUomId', type: 'string' },
	                 	{ name: 'actualExportedQuantity', type: 'number' },
	                 	{ name: 'actualDeliveredQuantity', type: 'number' },
	                 	{ name: 'statusId', type: 'string' },
	                 	{ name: 'isPromo', type: 'string' },
	                 	{ name: 'batch', type: 'string' },
	                 	{ name: 'quantity', type: 'number' },
	                 	{ name: 'inventoryItemId', type: 'string' },
						{ name: 'actualExpireDate', type: 'string', other: 'Timestamp'},
						{ name: 'actualManufacturedDate', type: 'string', other: 'Timestamp'},
						{ name: 'expireDate', type: 'date', other: 'Timestamp'},
	                 	{ name: 'deliveryStatusId', type: 'string'},
						{ name: 'weight', type: 'number'},
						{ name: 'productWeight', type: 'number'},
						{ name: 'weightUomId', type: 'String'},
						{ name: 'defaultWeightUomId', type: 'String'},
			 		 	],
	        localdata: valueDataSoure,
	        datatype: "array",
	    };
	    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
	    $("#jqxgridDlvItem").jqxGrid({
        source: dataAdapterProduct,
        filterable: false,
        showfilterrow: false,
        theme: 'olbius',
        rowsheight: 26,
        width: '100%',
        height: 210,
        enabletooltips: true,
        autoheight: false,
        pageable: true,
        pagesize: 5,
        editable: true,
        columnsresize: true,
        localization: getLocalization(),
	        columns: [	
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<span style=margin:4px;>' + (value + 1) + '</span>';
					    }
					},
					{ text: '${uiLabelMap.FromOrderItemSeqId}', hidden: true, datafield: 'fromOrderItemSeqId',
					},
					{ text: '${uiLabelMap.ProductCode}', dataField: 'productCode', width: 120, editable: true, columntype: 'dropdownlist', pinned: true,
						createeditor: function (row, cellvalue, editor) {
							var codeSourceData = [];
							for (var n = 0; n < valueDataSoure.length; n ++){
								var prCode = valueDataSoure[n].productCode;
								var kt = false;
								for (var m = 0; m < codeSourceData.length; m ++){
									if (codeSourceData[m].productCode == prCode){
										kt = true;
										break;
									}
								}
								if (kt == false){
									var map = {};
									map['productCode'] = prCode;
									codeSourceData.push(map);
								}
							}
							var sourcePrCode =
							{
				               localdata: codeSourceData,
				               datatype: 'array'
							};
							var dataAdapterPrCode = new $.jqx.dataAdapter(sourcePrCode);
							editor.off('change');
							editor.jqxDropDownList({source: dataAdapterPrCode, autoDropDownHeight: true, displayMember: 'productCode', valueMember: 'productCode', placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}'
							});
							editor.on('change', function (event){
								var args = event.args;
					     	    if (args) {
				     	    		var item = args.item;
					     		    if (item){
					     		    	FastSalesDlvObj.updateRowData(item.value);
					     		    } 
					     	    }
					        });
						 },
						 cellbeginedit: function (row, datafield, columntype) {
							 var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							 if (data.productCode){
								 return false;
							 }
							 return true;
						 },
						 cellsrenderer: function(row, column, value){
							 var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							 if (!data.productCode){
								 return '<span> ${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)} </span>';
							 }
						 }
					},
					{ text: '${uiLabelMap.ProductName}', dataField: 'productName', width: 200, editable: false,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							if (!data.productCode){
								return '<span style=\"text-align: right\">...</span>';
							}
						}
					},
					{ text: '${uiLabelMap.Quantity}', dataField: 'quantity', cellsalign: 'right', width: 120, editable: false,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							if (value === null || value === undefined || value === ""){
								if (data.productCode){
									return '<span style=\"text-align: right;\">_NA_</span>';
								} else {
									return '<span style=\"text-align: right;\">...</span>';
								}
							}
							var descriptionUom = data.quantityUomId;
							for(var i = 0; i < quantityUomData.length; i++){
								if(data.quantityUomId == quantityUomData[i].uomId){
									descriptionUom = quantityUomData[i].description;
							 	}
							}
							return '<span style=\"text-align: right\">' + value +' (' + descriptionUom +  ')</span>';
						 }
					},
					{ text: '${uiLabelMap.ActualExpireDate}', dataField: 'inventoryItemId', columntype: 'dropdownlist', width: 120, editable: true, sortable: false,
					    cellsrenderer: function(row, column, value){
					        var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					        if(data != null && data != undefined){
					        	if(value != null && value != '' && value != undefined){
					        		if (data.statusId == 'DELI_ITEM_APPROVED'){
					            		for(k = 0; k < listInv.length; k++){
						                    if(listInv[k].inventoryItemId == value){
						                        var tmpDate = new Date(listInv[k].expireDate.time);
						                        return '<span style=\"text-align: right\" class=\"focus-color\">' + $.datepicker.formatDate('dd/mm/yy', tmpDate) + '</span>';
						                    }
						                }
						                var tmpDate = new Date(value);
						                return '<span style=\"text-align: right\" class=\"focus-color\">' + value + '</span>';
					            	} else {
					            		var check = false;
					            		for(k = 0; k < listInv.length; k++){
						                    if(listInv[k].inventoryItemId == value){
						                    	check = true;
						                        var tmpDate = new Date(listInv[k].expireDate.time);
						                        return '<span style=\"text-align: right\">' + $.datepicker.formatDate('dd/mm/yy', tmpDate) + '</span>';
						                    }
						                }
					            		if (check == false){
					            			if (data.actualExpireDate != null && data.actualExpireDate != undefined && data.actualExpireDate != ''){
					            				return '<span style=\"text-align: right\">' + getFormattedDate(new Date(data.actualExpireDate)) + '</span>';
					            			}
					            		}
						                var tmpDate = new Date(value);
						                return '<span style=\"text-align: right\">' + value + '</span>';
					            	}
					            } else {
					            	if (data.productCode){
					            		if (data.statusId == 'DELI_ITEM_APPROVED'){
					            			var id = data.uid;
						            		var check = false;
									 		for(i = 0; i < listInv.length; i++){
									 			if (data.productId == listInv[i].productId && listInv[i].quantityOnHandTotal >= data.actualExportedQuantity){
									 				$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'inventoryItemId', listInv[i].inventoryItemId);
									 				var tmpDate = new Date(listInv[i].expireDate.time);
									 				check = true;
								                    return '<span style=\"text-align: right\" class=\"focus-color\">' + $.datepicker.formatDate('dd/mm/yy', tmpDate) + '</span>';
									 			} else {
									 				check = false;
									 			}
									 		}
									 		if (check == false){
									 			return '<span title=\"${uiLabelMap.NotEnoughDetail}: ' +data.actualExportedQuantity+ '\" style=\"text-align: left\" class=\"warning-color\">${uiLabelMap.NotEnough}</span>';
									 		}
						            	} else {
						            		return '<span style=\"text-align: right;\">_NA_</span>';
						            	}
					            	} else {
										return '<span style=\"text-align: right;\">...</span>';
									}
					            }
					            return '<span></span>';
					        }
					        var tmpDate = new Date(data.actualExpireDate);
					        return '<span style=\"text-align: right\">' + $.datepicker.formatDate('dd/mm/yy', tmpDate) + '</span>';
					    }, 
					    initeditor: function(row, value, editor){
						    var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						    var uid = data.uid;
						    var invData = [];
						    var iIndex = 0;
						    var curInvId = null;
						    if (data.inventoryItemId != null && data.inventoryItemId != undefined){
						    	curInvId = data.inventoryItemId;
						    }
						    var invEnoughTmp = null;
					        for(i = 0; i < listInv.length; i++){
					            if(listInv[i].productId == data.productId){
					            	if (listInv[i].quantityOnHandTotal >= data.actualExportedQuantity){
					            		invEnoughTmp = listInv[i].inventoryItemId;
					            	}
					                var tmpDate ;
					                var tmpValue = new Object();
					                
					                if(listInv[i].expireDate != null){
					                    tmpDate = new Date(listInv[i].expireDate.time);
					                    tmpValue.expireDate =  $.datepicker.formatDate('dd/mm/yy', tmpDate);
					                }else{
					                    tmpValue.expireDate = '';
					                }
					               
					                if(listInv[i].datetimeReceived != null){
					                    tmpDate = new Date(listInv[i].datetimeReceived.time);
					                    tmpValue.receivedDate =  $.datepicker.formatDate('dd/mm/yy', tmpDate);
					                }else{
					                    tmpValue.receivedDate = '';
					                }
					                tmpValue.inventoryItemId = listInv[i].inventoryItemId;
					                tmpValue.productId = listInv[i].productId;
					                tmpValue.quantityOnHandTotal = listInv[i].quantityOnHandTotal;
					                tmpValue.availableToPromiseTotal = listInv[i].availableToPromiseTotal;
					                
					                tmpValue.quantityCurrent = listInv[i].quantityOnHandTotal;
					                
					                var qtyUom = '';
					                for(var j = 0; j < quantityUomData.length; j++){
										if(listInv[i].quantityUomId == quantityUomData[j].uomId){
											qtyUom = quantityUomData[j].description;
									 	}
									}
					                tmpValue.qtyUom = qtyUom;
					                invData[iIndex++] = tmpValue;
					            }
					        }
					        if (invData.length <= 0){
					        	editor.jqxDropDownList({ placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: invData, dropDownWidth: '680px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
						            renderer: function(index, label, value) {
						                var item = editor.jqxDropDownList('getItem', index);
						                return '<span>[<span style=\"color:blue;\">RC:</span>&nbsp;' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">EXP:</span>&nbsp' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + item.originalItem.quantityOnHandTotal.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">ATP:</span>&nbsp' + item.originalItem.availableToPromiseTotal.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">CUR:</span>&nbsp' + item.originalItem.quantityCurrent.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']</span>';
						            },
						        });
					        } else {
					        	if (curInvId != null){
					        		 var curInv = null;
					        		 var curQuantity = 0;
					        		 var allrowTmp = $('#jqxgridDlvItem').jqxGrid('getrows');
				        			 for(var j = 0; j < allrowTmp.length; j++){
				        				 if (allrowTmp[j].inventoryItemId == curInvId){
				        					 curQuantity = curQuantity + allrowTmp[j].actualExportedQuantity;
				        				 }
				        			 }
					        		 for(var i = 0; i < invData.length;i++){
					        			 if(invData[i].inventoryItemId == curInvId){
					        				 invData[i].quantityCurrent = invData[i].quantityCurrent - curQuantity;
					        			 } else {
					        				 var qtyOfOtherInv = 0;
					        				 for(var j = 0; j < allrowTmp.length; j++){
						        				 if (allrowTmp[j].inventoryItemId == invData[i].inventoryItemId){
						        					 qtyOfOtherInv = qtyOfOtherInv + allrowTmp[j].actualExportedQuantity;
						        				 }
						        			 }
					        				 invData[i].quantityCurrent = invData[i].quantityCurrent - qtyOfOtherInv;
					        			 }
					        		 }
					        		 editor.jqxDropDownList({ placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: invData, selectedIndex: 0, dropDownWidth: '680px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
				        			 	renderer: function(index, label, value) {
							                var item = editor.jqxDropDownList('getItem', index);
							                return '<span>[<span style=\"color:blue;\">RC:</span>&nbsp;' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">EXP:</span>&nbsp' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + item.originalItem.quantityOnHandTotal.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">ATP:</span>&nbsp' + item.originalItem.availableToPromiseTotal.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">CUR:</span>&nbsp' + item.originalItem.quantityCurrent.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']</span>';
						            	},
					        		 });
					        		 editor.jqxDropDownList('selectItem', curInvId);
					        	} else {
					        		editor.jqxDropDownList({ placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: invData, selectedIndex: 0, dropDownWidth: '680px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
				        			 	renderer: function(index, label, value) {
							                var item = editor.jqxDropDownList('getItem', index);
							                return '<span>[<span style=\"color:blue;\">RC:</span>&nbsp;' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">EXP:</span>&nbsp' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + item.originalItem.quantityOnHandTotal.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">ATP:</span>&nbsp' + item.originalItem.availableToPromiseTotal.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">CUR:</span>&nbsp' + item.originalItem.quantityCurrent.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']</span>';
						            	},
					        		});
					        		if (invEnoughTmp != null){
					        			editor.jqxDropDownList('selectItem', invEnoughTmp);
					        		} else {
					        			for(var i = 0; i < invData.length;i++){
								            var tmpDate = new Date(data.actualExpireDate);
								            var tmpStr = $.datepicker.formatDate('dd/mm/yy', tmpDate);
								            if((invData[i].productId = data.productId) && (tmpStr = data.actualExpireDate)){
								                editor.jqxDropDownList('selectItem', invData[i].inventoryItemId);
								                break;
								            }
								        }
					        		}
					        	}
					        	
					        }
					    },
					    validation: function (cell, value) {
					    	if (listInv.length < 1){
					    		return { result: false, message: '${uiLabelMap.FacilityNotEnoughProduct}'};
						    }
					    	
					        if (value == null || value == undefined || value == '') {
					            return { result: false, message: '${uiLabelMap.DmsFieldRequired}'};
					        }
					        return true;
					    },
					    cellbeginedit: function (row, datafield, columntype) {
							var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							if(data.statusId != 'DELI_ITEM_APPROVED'){
								return false;
							}else{
					            return true;
							}
						},
					}, 	
					{ text: '${uiLabelMap.ActualDeliveryQuantitySum}', dataField: 'actualExportedQuantity', columntype: 'numberinput', width: 130, cellsalign: 'right', editable: true, sortable: false,
						cellbeginedit: function (row, datafield, columntype) {
							var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							if(data.statusId != 'DELI_ITEM_APPROVED'){
								return false;
							}else{
                                return true;
							}
					    },
					    initeditor: function(row, value, editor){
					        var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					        if(data.statusId == 'DELI_ITEM_EXPORTED'){
                                editor.jqxNumberInput({disabled: true});
                            }else{
                                editor.jqxNumberInput({disabled: false});
                                if (value != null && value != undefined){
                                	editor.jqxNumberInput('val', value);
                                } else {
                                	if (data.productCode != null && data.productCode != undefined){
                                		if (data.quantity){
                                        	editor.jqxNumberInput('val', data.quantity);
                                        }
                                	} else {
                                		editor.jqxNumberInput('val', 0);
                                	}
                                }
                            }
					    },
					    validation: function (cell, value) {
					        if (value < 0) {
					            return { result: false, message: '${uiLabelMap.NumberGTZ}'};
					        }
					        var dataTmp = $('#jqxgridDlvItem').jqxGrid('getrowdata', cell.row);
					        var prCode = dataTmp.productCode;
					        var isPromo = dataTmp.isPromo;
					        var rows = $('#jqxgridDlvItem').jqxGrid('getrows');
							
					        var listByPr = [];
					        for (var i = 0; i < rows.length; i ++){
					        	if (prCode == rows[i].productCode && isPromo == rows[i].isPromo){
									 listByPr.push(rows[i]);
					        	} 
					        }
					        
					        var allDlvQty = 0;
					        for (var i = 0; i < listDeliveryItemData.length; i ++){
					        	if (listDeliveryItemData[i].productCode == prCode && listDeliveryItemData[i].isPromo == isPromo){
					        		allDlvQty = allDlvQty + listDeliveryItemData[i].quantity;
					        	}
							}
					        var curQty = 0;
					        for (var i = 0; i < listByPr.length; i ++){
					        	if (listByPr[i].productCode == prCode && listByPr[i].isPromo == isPromo){
					        		curQty = curQty + listByPr[i].actualExportedQuantity;
					        	}	
					        }
					        var totalCreated = curQty + value - parseInt(dataTmp.actualExportedQuantity);
					        if (totalCreated - allDlvQty > 0){
					        	return { result: false, message: '${uiLabelMap.QuantityGreateThanQuantityCreatedInSalesDelivery}: ' + totalCreated + ' > ' + allDlvQty};
					        }
					        return true;
					    },
					    cellsrenderer: function (row, column, value){
					    	var tmp = null;
						 	var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						 	if (value === null || value === undefined || value === ''){
						 		if (data.productCode){
						 			if (data.statusId == 'DELI_ITEM_APPROVED'){
						 				var id = data.uid;
                                		var orderQty = data.quantity;
								 		$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'actualExportedQuantity', orderQty);
								 		return '<span style=\"text-align: right;\" class=\"focus-color\" title=' + orderQty.toLocaleString('${localeStr}') + '>' + orderQty.toLocaleString('${localeStr}') + '</span>';
						 				
						 			} else {
						 				return '<span style=\"text-align: right;\" class=\"focus-color\" title=' + 0 + '>' + 0 + '</span>';
						 			}
						 		} else {
									return '<span style=\"text-align: right;\">...</span>';
								}
						 	}
						 	if (data.statusId == 'DELI_ITEM_APPROVED'){
						 		return '<span style=\"text-align: right;\" class=\"focus-color\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
						 	} else {
						 		return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
						 	}
				    	}
					  },
					  { text: '${uiLabelMap.ActualDeliveredQuantitySum}', columntype: 'numberinput',  cellsalign: 'right', dataField: 'actualDeliveredQuantity', width: 130, editable: true,
						cellbeginedit: function (row, datafield, columntype) {
							var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							if(data.statusId != 'DELI_ITEM_EXPORTED'){
								return false;
							} else{
                                return true;
                            }
						 }, 
						 initeditor: function(row, value, editor){
                            var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
                        	if(data.statusId != 'DELI_ITEM_EXPORTED'){
                        		editor.jqxNumberInput({disabled: true});
                            } else{
                                editor.jqxNumberInput({disabled: false});
                                if (null === value || value === undefined){
                                	if (data.actualExportedQuantity){
                                		if (data.actualExportedQuantity > 0){
                                        	editor.jqxNumberInput('val', data.actualExportedQuantity);
                                        } else {
                                        	editor.jqxNumberInput({disabled: true});
                                        }
                                	}
                                } else {
                                	if (data.actualExportedQuantity > 0){
                                    	editor.jqxNumberInput('val', data.actualExportedQuantity);
                                    } else {
                                    	editor.jqxNumberInput({disabled: true});
                                    }
                                }
                            }
                         },
                         validation: function (cell, value) {
					        if (value < 0) {
					            return { result: false, message: '${uiLabelMap.NumberGTZ}'};
					        }
					        return true;
						 },
						 cellsrenderer: function (row, column, value){
						 	var tmp = null;
						 	var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						 	if (null === value || value === undefined || value === ''){
						 		if (data.productCode){
                            		if (data.statusId == 'DELI_ITEM_EXPORTED'){
                            			var id = data.uid;
                                		var actualExprt = data.actualExportedQuantity;
                                		if (actualExprt == 0){
                                			$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'actualDeliveredQuantity', actualExprt);
									 		return '<span style=\"text-align: right\" title=' + actualExprt.toLocaleString('${localeStr}') + '>' + actualExprt.toLocaleString('${localeStr}') + '</span>';
                                		} else {
                                			$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'actualDeliveredQuantity', actualExprt);
									 		return '<span style=\"text-align: right\" class=\"focus-color\" title=' + actualExprt.toLocaleString('${localeStr}') + '>' + actualExprt.toLocaleString('${localeStr}') + '</span>';
                                		}
                            		} else {
                            			return '<span style=\"text-align: right\" title=' + 0 + '>' + 0 + '</span>';
                            		}
						 		} else {
									return '<span style=\"text-align: right;\">...</span>';
						 		}
						 	}
						 	if (data.statusId == 'DELI_ITEM_EXPORTED'){
						 		var actualExprt = data.actualExportedQuantity;
                        		if (actualExprt == 0){
                        			return '<span style=\"text-align: right;\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
                        		} else {
                        			return '<span style=\"text-align: right;\" class=\"focus-color\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
                        		}
						 	} else {
						 		return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
						 	}
						 	return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
						 }
					 },
					 { text: '${uiLabelMap.WeightBaseSum}', dataField: 'weight', cellsalign: 'right', width: 120, editable: false, sortable: false,
						 cellsrenderer: function (row, column, value){
							 var desc;
							 var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							 for(var i = 0; i < weightUomData.length; i++){
								 if(weightUomData[i].uomId == data.weightUomId){
									 desc = weightUomData[i].description;
								 }
							 }
							 if (value != null && value != undefined && '' != value){
								 return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + ' ('+desc+')</span>';
							 } else {
								 if (data.productCode){
									 if (data.productWeight){
										 value = data.productWeight;
										 return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + ' ('+desc+')</span>';
									 } else {
										 return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>0 ('+desc+')</span>';
									 }
								 } else {
									return '<span style=\"text-align: right;\">...</span>';
								 }
							 }
						 }
					 },
					 { text: '${uiLabelMap.RequiredExpireDate}', dataField: 'expireDate', width: 120, cellsformat: 'dd/MM/yyyy', editable: false, cellsalign: 'right',
						 cellsrenderer: function(row, column, value){
							 var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							 if (value === null || value === undefined || value === ''){
								 if (data.productCode){
									 return '<span style=\"text-align: right\">_NA_</span>';
								 } else {
									 return '<span style=\"text-align: right;\">...</span>';
								 }
							 } else {
								 return '<span style=\"text-align: right\">'+ SalesDlvObj.getFormattedDate(value)+'</span>';
							 }
						 }
					},
					{ text: '${uiLabelMap.IsPromo}', dataField: 'isPromo', width: 120, editable: true, columntype: 'dropdownlist', 
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							if (value === null || value === undefined || value === ''){
								if (data.productCode){
									 return '<span style=\"text-align: right\">_NA_</span>';
								 } else {
									 return '<span style=\"text-align: right;\">...</span>';
								 }
							} else {
								if (value == 'Y'){
									return '<span style=\"text-align: left\">${uiLabelMap.LogYes}</span>';
								}
								if (value == 'N'){
									return '<span style=\"text-align: left\">${uiLabelMap.LogNO}</span>';
								}
							}
						},
						cellbeginedit: function (row, datafield, columntype) {
							var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							var code = data.productCode; 
							var count = 0;
							for(var i = 0; i < listDeliveryItemData.length; i++){
								var item = listDeliveryItemData[i];
								if (item.productCode == code){
									count = count + 1;
								}
							}
							if((data.deliveryItemSeqId === null || data.deliveryItemSeqId === undefined) && count > 1){
								return true;
							} else{
                                return false;
                            }
						}, 
						initeditor: function(row, value, editor){
					        var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
					        var listPromoStatus = [];
					        var map = {};
					        map['isPromoId'] = 'Y';
					        map['isPromoValue'] = 'Y';
					        listPromoStatus.push(map);
					        map = {};
					        map['isPromoId'] = 'N';
					        map['isPromoValue'] = 'N';
					        listPromoStatus.push(map);
					        editor.off('change');
					        editor.jqxDropDownList({ placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: listPromoStatus, selectedIndex: 0, dropDownWidth: '120px', popupZIndex: 755, displayMember: 'isPromoId', valueMember: 'isPromoValue',
		        			 	renderer: function(index, label, value) {
					                var item = editor.jqxDropDownList('getItem', index);
					                if (item.originalItem.isPromoId == 'Y'){
										return '<span style=\"text-align: left\">${uiLabelMap.LogYes}</span>';
									}
									if (item.originalItem.isPromoId == 'N'){
										return '<span style=\"text-align: left\">${uiLabelMap.LogNO}</span>';
									}
				            	}
					        });
					        if (data.isPromo == 'Y'){
					        	editor.jqxDropDownList('selectItem', 'Y');
					        } else {
					        	editor.jqxDropDownList('selectItem', 'N');
					        }
					        var uid = data.uid;
					        var prCode = data.productCode;
					        editor.on('change', function (event){
								var args = event.args;
					     	    if (args) {
				     	    		var item = args.item;
					     		    if (item){
					     		    	var isPromo = item.value;
					     		    	var objTmp = 0;
					     		    	for (var e = 0; e < listDeliveryItemData.length; e ++){
					     		    		if (listDeliveryItemData[e].productCode == prCode && listDeliveryItemData[e].isPromo == isPromo){
					     		    			objTmp = listDeliveryItemData[e];
					     		    			break;
					     		    		}
					     		    	}
					     		    	$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', uid, 'quantity', objTmp.quantity);
					     		    	$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', uid, 'fromOrderItemSeqId', objTmp.fromOrderItemSeqId);
					     		    } 
					     	    }
					        });
						},
					
				 	},																																																																																																																																																																																																																																																											
					{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 120, editable: false,
						 cellsrenderer: function(row, column, value){
							 var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							 if (value === null || value === undefined || value === ''){
								 if (data.productCode){
									 return '<span style=\"text-align: right\">_NA_</span>';
								 } else {
									 return '<span style=\"text-align: right;\">...</span>';
								 }
							 } else {
								 for(var i = 0; i < dlvItemStatusData.length; i++){
									 if(value == dlvItemStatusData[i].statusId){
										 return '<span title=' + value + '>' + dlvItemStatusData[i].description + '</span>';
									 }
								 }
							 }
						 }
				 	},
                 ]
	    });
	}
</script>