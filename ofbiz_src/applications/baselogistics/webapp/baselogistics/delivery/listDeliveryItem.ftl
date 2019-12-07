<#assign localeStr = "VI" />
<#if locale = "en">
    <#assign localeStr = "EN" />
</#if>
<div>
<#assign dataField2="[{ name: 'deliveryId', type: 'string' },
                 	{ name: 'deliveryItemSeqId', type: 'string' },
                 	{ name: 'fromOrderItemSeqId', type: 'string' },
                 	{ name: 'fromTransferItemSeqId', type: 'string' },
                 	{ name: 'fromOrderId', type: 'string' },
                 	{ name: 'fromTransferId', type: 'string' },
                 	{ name: 'productId', type: 'string' },
                 	{ name: 'productCode', type: 'string' },
                	{ name: 'productName', type: 'string' },
                 	{ name: 'quantityUomId', type: 'string' },
                 	{ name: 'comment', type: 'string' },
                 	{ name: 'actualExportedQuantity', type: 'number' },
                 	{ name: 'actualDeliveredQuantity', type: 'number' },
                 	{ name: 'statusId', type: 'string' },
                 	{ name: 'quantity', type: 'number' },
                 	{ name: 'inventoryItemId', type: 'string' },
					{ name: 'actualExpireDate', type: 'string', other: 'Timestamp'},
					{ name: 'expireDate', type: 'date', other: 'Timestamp'},
                 	{ name: 'deliveryStatusId', type: 'string'},
					{ name: 'weight', type: 'number'},
					{ name: 'weightUomId', type: 'String'},
					{ name: 'defaultWeightUomId', type: 'String'},
					
		 		 	]"/>
<#assign columnlist2="
						{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
					        groupable: false, draggable: false, resizable: false,
					        datafield: '', columntype: 'number', width: 50,
					        cellsrenderer: function (row, column, value) {
					            return '<span style=margin:4px;>' + (value + 1) + '</span>';
					        }
					    },
						{ text: '${uiLabelMap.ProductId}', dataField: 'productCode', width: 150, editable: false, pinned: true},
						{ text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 150, editable: false, pinned: true},
						{ text: '${uiLabelMap.quantity}', dataField: 'quantity', cellsalign: 'right', width: 100, editable: false,
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								var descriptionUom = data.quantityUomId;
								for(var i = 0; i < quantityUomData.length; i++){
									if(data.quantityUomId == quantityUomData[i].uomId){
										descriptionUom = quantityUomData[i].description;
								 	}
								}
								return '<span style=\"text-align: right\">' + value +' (' + descriptionUom +  ')</span>';
							 }
						},
						{ text: '${uiLabelMap.ExpireDate}', dataField: 'inventoryItemId', columntype: 'dropdownlist', width: 130, editable: true, sortable: false,
						    cellsrenderer: function(row, column, value){
						        var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
						        var uid = data.uid;
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
										 				$('#jqxgrid2').jqxGrid('setcellvaluebyid', id, 'inventoryItemId', listInv[i].inventoryItemId);
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
						    	var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
							    var uid = data.uid;
							    var invData = [];
							    var iIndex = 0;
							    var curInvId = null;
							    if (data.inventoryItemId != null && data.inventoryItemId != undefined){
							    	curInvId = data.inventoryItemId;
							    }
						        for(i = 0; i < listInv.length; i++){
						            if(listInv[i].productId == data.productId && listInv[i].quantityOnHandTotal >= data.actualExportedQuantity){
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
							            }
							        });
						        } else {
						        	if (curInvId != null){
						        		 var curInv = null;
						        		 var curQuantity = 0;
						        		 var allrowTmp = $('#jqxgrid2').jqxGrid('getrows');
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
							            	}
						        		 });
						        		 editor.jqxDropDownList('selectItem', curInvId);
						        	} else {
						        		editor.jqxDropDownList({ placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: invData, selectedIndex: 0, dropDownWidth: '680px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
					        			 	renderer: function(index, label, value) {
								                var item = editor.jqxDropDownList('getItem', index);
								                return '<span>[<span style=\"color:blue;\">RC:</span>&nbsp;' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">EXP:</span>&nbsp' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + item.originalItem.quantityOnHandTotal.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">ATP:</span>&nbsp' + item.originalItem.availableToPromiseTotal.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">CUR:</span>&nbsp' + item.originalItem.quantityCurrent.toLocaleString('${localeStr}') + ' (' + qtyUom + ')' + ']</span>';
							            	}
						        		 });
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
                        },validation: function (cell, value) {
                        	if (listInv.length < 1){
					    		return { result: false, message: '${uiLabelMap.FacilityNotEnoughProduct}'};
						    }
                        	if (value == null || value == undefined || value == '') {
                                return { result: false, message: '${uiLabelMap.FieldRequired}'};
                            }
                            return true;
                        },
                        cellbeginedit: function (row, datafield, columntype) {
							var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
							if (data.statusId == 'DELI_ITEM_APPROVED'){
								return true;
							} else {
								return false;
							}
					    },
                        },
					 	"/>
<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "UPDATE")>
	<#assign columnlist2 = columnlist2 + "{ text: '${uiLabelMap.ActualExportedQuantity}', dataField: 'actualExportedQuantity', columntype: 'numberinput', width: 150, cellsalign: 'right', editable: true, sortable: false,
										cellbeginedit: function (row, datafield, columntype) {
											var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
											if(data.statusId != 'DELI_ITEM_APPROVED'){
												return false;
											}else{
									            return true;
											}
									    },
									    initeditor: function(row, value, editor){
									        var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
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
									        var dataTmp = $('#jqxgrid2').jqxGrid('getrowdata', cell.row);
									        var prCode = dataTmp.productCode;
									        var rows = $('#jqxgrid2').jqxGrid('getrows');
											
									        var listByPr = [];
									        for (var i = 0; i < rows.length; i ++){
									        	if (prCode == rows[i].productCode){
													 listByPr.push(rows[i]);
									        	} 
									        }
									        var allDlvQty = 0;
									        for (var i = 0; i < listDeliveryItemData.length; i ++){
									        	if (listDeliveryItemData[i].productCode == prCode){
									        		allDlvQty = allDlvQty + listDeliveryItemData[i].quantity;
									        	}
											}
									        var curQty = 0;
									        for (var i = 0; i < listByPr.length; i ++){
									        	if (listByPr[i].productCode == prCode){
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
										 	var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
										 	if (value === null || value === undefined || value === ''){
										 		if (data.productId){
										 			if (data.statusId == 'DELI_ITEM_APPROVED'){
										 				var id = data.uid;
									            		var transferQty = data.quantity;
												 		$('#jqxgrid2').jqxGrid('setcellvaluebyid', id, 'actualExportedQuantity', transferQty);
												 		return '<span style=\"text-align: right;\" class=\"focus-color\" title=' + transferQty.toLocaleString('${localeStr}') + '>' + transferQty.toLocaleString('${localeStr}') + '</span>';
										 				
										 			} else {
										 				return '<span style=\"text-align: right;\" title=' + 0 + '>' + 0 + '</span>';
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
									  	{ text: '${uiLabelMap.ActualDeliveredQuantity}', columntype: 'numberinput',  cellsalign: 'right', dataField: 'actualDeliveredQuantity', width: 150, editable: true,
									  		cellbeginedit: function (row, datafield, columntype) {
												var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
												if(data.statusId != 'DELI_ITEM_EXPORTED'){
													return false;
												} else{
				                                    return true;
				                                }
											 }, 
											 initeditor: function(row, value, editor){
				                                var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
				                            	if(data.statusId != 'DELI_ITEM_EXPORTED'){
				                            		editor.jqxNumberInput({disabled: true});
				                                }else{
				                                    editor.jqxNumberInput({disabled: false});
				                                    if (null === value || value === undefined){
				                                    	if (data.actualExportedQuantity){
				                                        	editor.jqxNumberInput('val', data.actualExportedQuantity);
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
											 	var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
											 	if (null === value || value === undefined || value === ''){
											 		if (data.productCode){
					                            		if (data.statusId == 'DELI_ITEM_EXPORTED'){
					                            			var id = data.uid;
					                                		var actualExprt = data.actualExportedQuantity;
													 		$('#jqxgrid2').jqxGrid('setcellvaluebyid', id, 'actualDeliveredQuantity', actualExprt);
													 		return '<span style=\"text-align: right\" class=\"focus-color\" title=' + actualExprt.toLocaleString('${localeStr}') + '>' + actualExprt.toLocaleString('${localeStr}') + '</span>';
					                            		} else {
					                            			return '<span style=\"text-align: right\" title=' + 0 + '>' + 0 + '</span>';
					                            		}
											 		} else {
														return '<span style=\"text-align: right;\">...</span>';
											 		}
											 	}
											 	if (data.statusId == 'DELI_ITEM_EXPORTED'){
											 		return '<span style=\"text-align: right;\" class=\"focus-color\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
											 	} else {
											 		return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
											 	}
											 	return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>';
											 }
										 },
										 { text: '${uiLabelMap.RequiredExpireDate}', dataField: 'expiredDate', width: 150, cellsformat: 'dd/MM/yyyy', editable: false, cellsalign: 'right',
												cellsrenderer: function(row, column, value){
													if (value){
														return '<span style=\"text-align: right\">' + DatetimeUtilObj.formatFullDate(value) + '</span>';
													} else {
														return '<span style=\"text-align: right\">_NA_</span>';
													}
												}
											},
//										 { text: '${uiLabelMap.weight}', dataField: 'weight', cellsalign: 'right', width: 150, editable: false, sortable: false,
//											 cellsrenderer: function (row, column, value){
//												 var desc;
//												 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
//												 for(var i = 0; i < weightUomData.length; i++){
//													 if(weightUomData[i].uomId == data.weightUomId){
//														 desc = weightUomData[i].description;
//													 }
//												 }
//												 return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + ' ('+desc+')</span>'
//											 }
//										 },
										 "/>
<#else>
	<#assign columnlist2 =columnlist2 + "{ text: '${uiLabelMap.ActualExportedQuantity}', cellsalign: 'right', dataField: 'actualExportedQuantity', width: 150, editable: false, sortable: false,},
										{ text: '${uiLabelMap.ActualDeliveredQuantity}', cellsalign: 'right', dataField: 'actualDeliveredQuantity', width: 150, editable: false, sortable: false,},
										{ text: '${uiLabelMap.RequiredExpireDate}', dataField: 'expireDate', width: 150, cellsformat: 'dd/MM/yyyy', editable: false, cellsalign: 'right',
											cellsrenderer: function(row, column, value){
												if (value){
													return '<span style=\"text-align: right\">' + DatetimeUtilObj.formatFullDate(value) + '</span>';
												} else {
													return '<span style=\"text-align: right\">_NA_</span>';
												}
											}
										},
//										{ text: '${uiLabelMap.weight}', dataField: 'weight', cellsalign: 'right', width: 150, editable: false, sortable: false,
//											cellsrenderer: function (row, column, value){
//												 var desc;
//												 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
//												 for(var i = 0; i < weightUomData.length; i++){
//													 if(weightUomData[i].uomId == data.weightUomId){
//														 desc = weightUomData[i].description;
//													 }
//												 }
//												 return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + ' ('+desc+')</span>';
//											 }
//										 },
										"/>
</#if>					 
					 
<#assign columnlist2 = columnlist2 + "
					 { text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, editable: false,
						 cellsrenderer: function(row, column, value){
							 for(var i = 0; i < dlvItemStatusData.length; i++){
								 if(value == dlvItemStatusData[i].statusId){
									 return '<span title=' + value + '>' + dlvItemStatusData[i].description + '</span>';
								 }
							 }
						 }
					 	},
//					 	{ text: '${uiLabelMap.comments}', dataField: 'comments', width: 150, editable: false},
					 "/>
</div>
<@jqGrid width="100%" id="jqxgrid2" autoheight="false" height="245" usecurrencyfunction="true" dataField=dataField2 columnlist=columnlist2 clearfilteringbutton="false" showtoolbar="true" filterable="false" editable="true" sortable="false"
		url="" bindresize="false" jqGridMinimumLibEnable="false" offlinerefreshbutton="false"
		customTitleProperties="DeliveryItemList" viewSize="5"
		selectionmode="checkbox" editmode="click"
		beforeprocessing="false"/>
