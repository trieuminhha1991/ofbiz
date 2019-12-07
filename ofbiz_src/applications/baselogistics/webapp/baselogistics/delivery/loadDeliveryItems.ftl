<script>
	function loadDeliveryItem(valueDataSoure) {
		var sourceProduct =
		{
			datafields:
			[
				{ name: 'deliveryId', type: 'string' },
				{ name: 'deliveryItemSeqId', type: 'string' },
				{ name: 'fromOrderItemSeqId', type: 'string' },
				{ name: 'fromTransferItemSeqId', type: 'string' },
				{ name: 'fromOrderId', type: 'string' },
				{ name: 'fromTransferId', type: 'string' },
				{ name: 'productId', type: 'string' },
				{ name: 'productCode', type: 'string' },
				{ name: 'productName', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'orderQuantityUomId', type: 'string' },
				{ name: 'alternativeQuantity', type: 'number' },
				{ name: 'actualExportedQuantity', type: 'number' },
				{ name: 'actualDeliveredQuantity', type: 'number' },
				
				{ name: 'amount', type: 'number' },
				{ name: 'actualExportedAmount', type: 'number' },
				{ name: 'actualDeliveredAmount', type: 'number' },
				
				{ name: 'statusId', type: 'string' },
				{ name: 'isPromo', type: 'string' },
				{ name: 'batch', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'inventoryItemId', type: 'string' },
				{ name: 'actualExpireDate', type: 'string', other: 'Timestamp' },
				{ name: 'actualManufacturedDate', type: 'string', other: 'Timestamp' },
				{ name: 'expireDate', type: 'date', other: 'Timestamp' },
				{ name: 'deliveryStatusId', type: 'string' },
				{ name: 'weight', type: 'number' },
				{ name: 'productWeight', type: 'number' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'defaultWeightUomId', type: 'string' },
				{ name: 'expRequired', type: 'string' },
				{ name: 'mnfRequired', type: 'string' },
				{ name: 'topRequired', type: 'string' },
				{ name: 'quantityUomIds', type: 'string' },
				{ name: 'convertNumber', type: 'number' },
				{ name: 'selectedAmount', type: 'number' },
				{ name: 'requireAmount', type: 'string' },
				{ name: 'locationId', type: 'string' },
				{ name: 'locationCode', type: 'string' },
			],
			localdata: valueDataSoure,
			datatype: "array",
		};
		var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
		$("#jqxgridDlvItem").jqxGrid({
			source: dataAdapterProduct,
			filterable: true,
			showfilterrow: true,
			theme: theme,
			rowsheight: 26,
			width: '100%',
			height: 370,
			enabletooltips: true,
			autoheight: false,
			pageable: true,
			pagesize: 10,
			editable: true,
			columnsresize: true,
			localization: getLocalization(),
			columns:
			[
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<span style=margin:4px;>' + (value + 1) + '</span>';
					}
				},
				{ hidden: true, datafield: 'fromOrderItemSeqId' },
				{ text: '${uiLabelMap.ProductCode}', dataField: 'productCode', width: 120, editable: true, columntype: 'dropdownlist', pinned: true,
					createeditor: function (row, cellvalue, editor) {
						var codeSourceData = [];
						for (var n in valueDataSoure) {
							var prCode = valueDataSoure[n].productCode;
							var selectedAmount = valueDataSoure[n].selectedAmount;
							var requireAmount = valueDataSoure[n].requireAmount;
							var weightUomId = valueDataSoure[n].weightUomId;
							var kt = false;
							for (var m in codeSourceData) {
								if (codeSourceData[m].productCode == prCode && codeSourceData[m].selectedAmount == selectedAmount) {
									kt = true;
									break;
								}
							}
							if (kt == false) {
								var map = {};
								map['productCode'] = prCode;
								map['selectedAmount'] = selectedAmount;
								map['requireAmount'] = requireAmount;
								map['weightUomId'] = weightUomId;
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
						editor.jqxDropDownList({source: dataAdapterPrCode, autoDropDownHeight: true, displayMember: 'productCode', valueMember: 'productCode', placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}',
							renderer: function (index, label, value) {
								var item = editor.jqxDropDownList('getItem', index);
								var prCode = item.originalItem.productCode;
								var selectedAmount = item.originalItem.selectedAmount;
								var requireAmount = item.originalItem.requireAmount;
								var weightUomId = item.originalItem.weightUomId;
								var desc = getUomDescription(weightUomId);
								if (requireAmount && weightUomId) {
									return '<span>' + prCode + ' - (' + selectedAmount + ' ' + desc + ')</span>';
								} else {
									return '<span>' + prCode + '</span>';
								}
							}
						});
						editor.on('change', function (event) {
							var args = event.args;
							if (args) {
								var item = args.item;
								if (item) {
									var selectedAmount = item.originalItem.selectedAmount; 
									SalesDlvObj.updateRowData(item.value, selectedAmount);
								} 
							}
						});
					},
					cellbeginedit: function (row, datafield, columntype) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (data.productCode) {
							return false;
						}
						return true;
					},
					cellsrenderer: function (row, column, value) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (!data.productCode) {
							return '<span> ${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)} </span>';
						}
					}
				},
				{ text: '${uiLabelMap.ProductName}', dataField: 'productName', width: 190, editable: false,
					cellsrenderer: function (row, column, value) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (!data.productCode) {
							return '<span style=\"text-align: right\"></span>';
						}
					}
				},
				{ text: '${uiLabelMap.BSSalesUomId}', filterable: false, datafield: 'orderQuantityUomId', align: 'left', width: 110, filtertype: 'checkedlist', columntype: 'dropdownlist', editable: false,
					cellsrenderer: function (row, column, value) {
						for (var i in quantityUomData) {
							if (quantityUomData[i].uomId == value) {
								return '<span style=\"text-align: right\">' + quantityUomData[i].description +'</span>';
							}
						}
					}
				},
				{ text: '${uiLabelMap.BLPackingForm}', filterable: false, datafield: 'convertNumber', align: 'left', width: 110, filtertype: 'checkedlist', columntype: 'dropdownlist', editable: false,
					cellsrenderer: function (row, column, value) {
						return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
					}
				},
				{ text: '${uiLabelMap.LogInventoryItem}', filterable: false, dataField: 'inventoryItemId', columntype: 'dropdownlist', width: 200, editable: true, sortable: false,
					cellsrenderer: function (row, column, value) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (data != null && data != undefined) {
							if (value != null && value != '' && value != undefined) {
								if (data.statusId == 'DELI_ITEM_APPROVED') {
									for (var k in listInv) {
										if (listInv[k].inventoryItemId == value && listInv[k].locationId == data.locationId) {
											if (listInv[k].expireDate != null && listInv[k].expireDate != undefined && listInv[k].expireDate != '') {
												var tmpDate = new Date(listInv[k].expireDate);
												var qoh = listInv[k].quantityOnHandTotal;
												var qty = data.actualExportedQuantity;
												if (data.requireAmount == 'Y') {
													qoh = listInv[k].amountOnHandTotal;
												}
												if (qoh >= qty) {
													return '<span style=\"text-align: right\" class=\"focus-color\">' + DatetimeUtilObj.getFormattedDate(tmpDate) + '</span>';
												} else {
													return '<span style=\"text-align: right\" class=\"warning-color\" title=\"${uiLabelMap.NotEnough}\">' + DatetimeUtilObj.getFormattedDate(tmpDate) + '</span>';												
												}
											} else {
												var qoh = listInv[k].quantityOnHandTotal;
												var qty = data.actualExportedQuantity;
												if (data.requireAmount == 'Y') {
													qoh = listInv[k].amountOnHandTotal;
												}
												if (qoh >= qty) {
													return '<span style=\"text-align: right\" class=\"focus-color\">${uiLabelMap.AnInvItemMissExpiredDate}</span>';
												} else {
													return '<span style=\"text-align: right\" class=\"warning-color\" title=\"${uiLabelMap.NotEnough}\">${uiLabelMap.AnInvItemMissExpiredDate}</span>';												
												}
											}
										}
									}
									if (listInv.length > 0) {
										$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', row, 'inventoryItemId', listInv[0].inventoryItemId);
										var tmpDate = new Date(listInv[0].expireDate);
										return '<span style=\"text-align: right\" class=\"focus-color\">' + DatetimeUtilObj.getFormattedDate(tmpDate) + '</span>';
									} else {
										return '<span title=\"${uiLabelMap.NotEnough}\" style=\"text-align: left\" class=\"warning-color\">${uiLabelMap.NotEnough}</span>';
									}
								} else {
									if (data.actualExpireDate != null && data.actualExpireDate != undefined && data.actualExpireDate != '') {
										if (typeof data.actualExpireDate != 'number') data.actualExpireDate = Number(data.actualExpireDate);
										return '<span style=\"text-align: right\">' + DatetimeUtilObj.getFormattedDate(new Date(data.actualExpireDate)) + '</span>';
									} else {
										return '<span style=\"text-align: right\">${uiLabelMap.AnInvItemMissExpiredDate}</span>';
									}
								}
							} else {
								if (data.productCode) {
									if (data.statusId == 'DELI_ITEM_APPROVED') {
										var id = data.uid;
										var check = false;
										var requireAmount = data.requireAmount;
										var convert = data.convertNumber;
										if (requireAmount && requireAmount == 'Y') {
											var reqAmount = data.actualExportedQuantity;
											for (var i in listInv) {
												if (data.productId == listInv[i].productId && listInv[i].amountOnHandTotal >= reqAmount) {
													$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'inventoryItemId', listInv[i].inventoryItemId);
													if (listInv[i].expireDate) {
														var tmpDate = new Date(listInv[i].expireDate);
														return '<span style=\"text-align: right\" class=\"focus-color\">' + DatetimeUtilObj.getFormattedDate(tmpDate) + '</span>';
													} else {
														return '<span style=\"text-align: right\" class=\"focus-color\">${uiLabelMap.AnInvItemMissExpiredDate}</span>';
													}
													check = true;
												} else {
													check = false;
												}
											}
										} else {
											for (var i in listInv) {
												if (data.productId == listInv[i].productId && listInv[i].quantityOnHandTotal >= (data.actualExportedQuantity * convert)) {
													$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'inventoryItemId', listInv[i].inventoryItemId);
													if (listInv[i].expireDate) {
														var tmpDate = new Date(listInv[i].expireDate);
														return '<span style=\"text-align: right\" class=\"focus-color\">' + DatetimeUtilObj.getFormattedDate(tmpDate) + '</span>';
													} else {
														return '<span style=\"text-align: right\" class=\"focus-color\">${uiLabelMap.AnInvItemMissExpiredDate}</span>';
													}
													check = true;
													return '<span style=\"text-align: right\" class=\"focus-color\">' + DatetimeUtilObj.getFormattedDate(tmpDate) + '</span>';
												} else {
													check = false;
												}
											}
										}
										if (check == false) {
											return '<span title=\"${uiLabelMap.NotEnoughDetail}: ' +data.actualExportedQuantity+ '\" style=\"text-align: left\" class=\"warning-color\">${uiLabelMap.NotEnough}</span>';
										}
									} else {
										return '<span style=\"text-align: right;\"></span>';
									}
								} else {
									return '<span style=\"text-align: right;\"></span>';
								}
							}
							return '<span></span>';
						}
						var tmpDate = new Date(data.actualExpireDate);
						return '<span style=\"text-align: right\">' + DatetimeUtilObj.getFormattedDate(tmpDate) + '</span>';
					}, 
					initeditor: function (row, value, editor) {
						editor.off('change');
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						var convert = data.convertNumber;
						var orderQuantityUomId = data.orderQuantityUomId;
						var uid = data.uid;
						var invData = [];
						var iIndex = 0;
						var curInvId = null;
						var curLocCode = null;
						if (data.inventoryItemId != null && data.inventoryItemId != undefined) {
							curInvId = data.inventoryItemId;
							curLocCode = data.locationCode;
						}
						var invEnoughTmp = null;
						var requireAmount = data.requireAmount;
						var selectedAmount = data.selectedAmount;
						for (var i in listInv) {
							if (listInv[i].productId == data.productId) {
								var tmpDate ;
								var tmpValue = new Object();
								
								if (listInv[i].expireDate != null) {
									tmpDate = new Date(listInv[i].expireDate);
									tmpValue.expireDate =  DatetimeUtilObj.getFormattedDate(tmpDate);
								} else {
									tmpValue.expireDate = '${uiLabelMap.ProductMissExpiredDate}';
								}
								if (listInv[i].datetimeManufactured != null) {
									tmpDate = new Date(listInv[i].datetimeManufactured);
									tmpValue.datetimeManufactured =  DatetimeUtilObj.getFormattedDate(tmpDate);
								} else {
									tmpValue.datetimeManufactured = '${uiLabelMap.ProductMissDatetimeManufactured}';
								}
								
								if (listInv[i].datetimeReceived != null) {
									tmpDate = new Date(listInv[i].datetimeReceived);
									tmpValue.receivedDate =  DatetimeUtilObj.getFormattedDate(tmpDate);
								} else {
									tmpValue.receivedDate = '${uiLabelMap.ProductMissDatetimeReceived}';
								}
								
								tmpValue.inventoryItemId = listInv[i].inventoryItemId;
								tmpValue.productId = listInv[i].productId;
								var uomDesc = '';
								if (requireAmount == 'Y') {
									if (listInv[i].amountOnHandTotal >= data.actualExportedQuantity) {
										invEnoughTmp = listInv[i].inventoryItemId;
									}
									tmpValue.quantityOnHandTotal = listInv[i].amountOnHandTotal;
									tmpValue.availableToPromiseTotal = listInv[i].availableToPromiseTotal;
									tmpValue.quantityCurrent = listInv[i].amountOnHandTotal;
									uomDesc = getUomDescription(data.weightUomId);
								} else {
									if (listInv[i].quantityOnHandTotal >= data.actualExportedQuantity*convert) {
										invEnoughTmp = listInv[i].inventoryItemId;
									}
									tmpValue.quantityOnHandTotal = listInv[i].quantityOnHandTotal;
									tmpValue.availableToPromiseTotal = listInv[i].availableToPromiseTotal;
									tmpValue.quantityCurrent = listInv[i].quantityOnHandTotal;
									uomDesc = getUomDescription(listInv[i].baseQuantityUomId);
								}
								var desOrderUom = '';
								for (var j in quantityUomData) {
									if (orderQuantityUomId == quantityUomData[j].uomId) {
										desOrderUom = quantityUomData[j].description;
									}
								}
								tmpValue.orderQuantityUomId = desOrderUom;
								tmpValue.uomDesc = uomDesc;
								tmpValue.convertNumber = convert;
								tmpValue.locationCode = listInv[i].locationCode;
								tmpValue.locationId = listInv[i].locationId;
								invData[iIndex++] = tmpValue;
							}
						}
						if (invData.length <= 0) {
							editor.jqxDropDownList({ placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: invData, dropDownWidth: '660px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
								renderer: function (index, label, value) {
									var item = editor.jqxDropDownList('getItem', index);
									if (curInvId == item.originalItem.inventoryItemId && curLocCode == item.originalItem.locationCode) {
										editor.jqxDropDownList('selectIndex', index);
									}
									var curDesc = '';
									if (item.originalItem.quantityCurrent != 0) {
										curDesc = formatnumber(item.originalItem.quantityCurrent);
									} else {
										curDesc = 0;
									}
									var atpDesc = '';
									if (item.originalItem.availableToPromiseTotal != 0) {
										atpDesc = formatnumber(item.originalItem.availableToPromiseTotal)
									} else {
										atpDesc = 0;
									}
									var qohDesc = '';
									if (item.originalItem.quantityOnHandTotal != 0) {
										qohDesc = formatnumber(item.originalItem.quantityOnHandTotal)
									} else {
										qohDesc = 0;
									}
									var locCode = ' _ ';
									if (item.originalItem.locationCode) {
										locCode = item.originalItem.locationCode;
									}
									return '<span>[<span style=\"color:blue;\">'+uiLabelMap.ExpiredDateSum+':</span>&nbsp;' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.ManufacturedDateSum + ':</span>&nbsp' + item.originalItem.datetimeManufactured + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.ReceivedDateSum + ':</span>&nbsp' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + qohDesc + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">ATP:</span>&nbsp' + atpDesc + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">CUR:</span>&nbsp' + curDesc + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.BLLocationCodeSum+ ':</span>&nbsp' + locCode + ']</span>';
								}
							});
						} else {
							if (curInvId != null) {
								var curInv = null;
								var curQuantity = 0;
								var allrowTmp = $('#jqxgridDlvItem').jqxGrid('getrows');
								for (var j in allrowTmp) {
									if (allrowTmp[j].inventoryItemId == curInvId && allrowTmp[j].locationCode == curLocCode) {
										if (requireAmount && requireAmount == 'Y') {
											curQuantity = curQuantity + allrowTmp[j].actualExportedQuantity;
										} else {
											curQuantity = curQuantity + allrowTmp[j].actualExportedQuantity*convert;
										}
									}
								}
								for (var i in invData) {
									if (invData[i].inventoryItemId == curInvId && invData[i].locationCode == curLocCode) {
										invData[i].quantityCurrent = invData[i].quantityCurrent - curQuantity;
									} else {
										var qtyOfOtherInv = 0;
										for (var j in allrowTmp) {
											if (allrowTmp[j].inventoryItemId == invData[i].inventoryItemId && allrowTmp[j].locationCode == invData[i].locationCode) {
												if (requireAmount && requireAmount == 'Y') {
													qtyOfOtherInv = qtyOfOtherInv + allrowTmp[j].actualExportedQuantity;
												} else {
													qtyOfOtherInv = qtyOfOtherInv + allrowTmp[j].actualExportedQuantity*convert;
												}
											}
										}
										invData[i].quantityCurrent = invData[i].quantityCurrent - qtyOfOtherInv;
									}
								}
								editor.jqxDropDownList({ placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: invData, selectedIndex: 0, dropDownWidth: '660px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
									renderer: function (index, label, value) {
										var item = editor.jqxDropDownList('getItem', index);
										if (curInvId == item.originalItem.inventoryItemId && curLocCode == item.originalItem.locationCode) {
											editor.jqxDropDownList('selectIndex', index);
										}
										var qoh = item.originalItem.quantityOnHandTotal;
										if (requireAmount && requireAmount == 'Y') {
											var desBaseUom = item.originalItem.uomDesc;
											var curDetail = '';
											if (item.originalItem.quantityCurrent != 0) {
												curDetail = formatnumber(item.originalItem.quantityCurrent)
											} else {
												curDetail = 0;
											}
											curDetail = curDetail + ' (' + desBaseUom + ')';
											
											var qohDetail = '';
											if (item.originalItem.quantityOnHandTotal != 0) {
												qohDetail = formatnumber(item.originalItem.quantityOnHandTotal)
											} else {
												qohDetail = 0;
											}
											qohDetail = qohDetail + ' (' + desBaseUom + ')';
											var locCode = ' _ ';
											if (item.originalItem.locationCode) {
												locCode = item.originalItem.locationCode;
											}
											return '<span>[<span style=\"color:blue;\">'+uiLabelMap.ExpiredDateSum+':</span>&nbsp;' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.ManufacturedDateSum + ':</span>&nbsp' + item.originalItem.datetimeManufactured + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.ReceivedDateSum + ':</span>&nbsp' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + qohDetail  + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">CUR:</span>&nbsp' + curDetail  + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.BLLocationCodeSum+ ':</span>&nbsp' + locCode + ']</span>';
										} else {
											var convert = item.originalItem.convertNumber;
											var x = Math.floor(qoh/convert);
											var y = qoh - x*convert;
											var desBaseUom = item.originalItem.uomDesc;
											var qohDetail = '';
											if (x != 0) {
												qohDetail = formatnumber(x) + ' (' + item.originalItem.orderQuantityUomId + ')';
											} else {
												qohDetail = 0 + ' (' + item.originalItem.orderQuantityUomId + ')';
											}
											if (y > 0) {
												qohDetail = qohDetail + ' &#59; ' + formatnumber(y) + ' (' + desBaseUom + ')';
											}
											
											var cur = item.originalItem.quantityCurrent;
											var j = Math.floor(cur/convert);
											var k = curDetail - j*convert;
											var curDetail = '';
											if (j != 0) {
												curDetail = formatnumber(j) + ' (' + item.originalItem.orderQuantityUomId + ')';
											} else {
												curDetail = 0 + ' (' + item.originalItem.orderQuantityUomId + ')';
											}
											if (k > 0) {
												curDetail = curDetail + ' &#59; ' + formatnumber(k) + ' (' + desBaseUom + ')';
											}
											var locCode = ' _ ';
											if (item.originalItem.locationCode) {
												locCode = item.originalItem.locationCode;
											}
											return '<span>[<span style=\"color:blue;\">'+uiLabelMap.ExpiredDateSum+':</span>&nbsp;' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.ManufacturedDateSum + ':</span>&nbsp' + item.originalItem.datetimeManufactured + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.ReceivedDateSum + ':</span>&nbsp' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + qohDetail  + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">CUR:</span>&nbsp' + curDetail  + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.BLLocationCodeSum+ ':</span>&nbsp' + locCode + ']</span>';
										}
									}
								});
							} else {
								editor.jqxDropDownList({ placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', source: invData, selectedIndex: 0, dropDownWidth: '660px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
									renderer: function (index, label, value) {
										var item = editor.jqxDropDownList('getItem', index);
										if (curInvId == item.originalItem.inventoryItemId && curLocCode == item.originalItem.locationCode) {
											editor.jqxDropDownList('selectIndex', index);
										}
										var qoh = item.originalItem.quantityOnHandTotal;
										if (requireAmount && requireAmount == 'Y') {
											var desBaseUom = item.originalItem.uomDesc;
											var qohDetail = '';
											if (item.originalItem.quantityOnHandTotal != 0) {
												qohDetail = formatnumber(item.originalItem.quantityOnHandTotal) + ' (' + desBaseUom + ')';
											} else {
												qohDetail = 0 + ' (' + desBaseUom + ')';
											}
											var curDetail = '';
											if (item.originalItem.quantityCurrent != 0) {
												curDetail = formatnumber(item.originalItem.quantityCurrent) + ' (' + desBaseUom + ')';
											} else {
												curDetail = 0 + ' (' + desBaseUom + ')';
											}
											var locCode = ' _ ';
											if (item.originalItem.locationCode) {
												locCode = item.originalItem.locationCode;
											}
											return '<span>[<span style=\"color:blue;\">'+uiLabelMap.ExpiredDateSum+':</span>&nbsp;' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.ManufacturedDateSum + ':</span>&nbsp' + item.originalItem.datetimeManufactured + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.ReceivedDateSum + ':</span>&nbsp' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + qohDetail  + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">CUR:</span>&nbsp' + curDetail  + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.BLLocationCodeSum+ ':</span>&nbsp' + locCode + ']</span>';
										} else {
											var convert = item.originalItem.convertNumber;
											var x = Math.floor(qoh/convert);
											var y = qoh - x*convert;
											var desBaseUom = item.originalItem.uomDesc;
											var qohDetail = '';
											if (x != 0) {
												qohDetail = formatnumber(x) + ' (' + desBaseUom + ')';
											} else {
												qohDetail = 0 + ' (' + desBaseUom + ')';
											}
											if (y > 0) {
												qohDetail = qohDetail + ' &#59; ' + formatnumber(y) + ' (' + item.originalItem.orderQuantityUomId + ')';
											}
											
											var cur = item.originalItem.quantityCurrent;
											var j = Math.floor(cur/convert);
											var k = curDetail - j*convert;
											var curDetail = '';
											if (j != 0) {
												curDetail = formatnumber(j) + ' (' + desBaseUom + ')';
											} else {
												curDetail = 0 + ' (' + desBaseUom + ')';
											}
											if (k > 0) {
												curDetail = curDetail + ' &#59; ' + formatnumber(k) + ' (' + item.originalItem.orderQuantityUomId + ')';
											}
											var locCode = ' _ ';
											if (item.originalItem.locationCode) {
												locCode = item.originalItem.locationCode;
											}
											return '<span>[<span style=\"color:blue;\">'+uiLabelMap.ExpiredDateSum+':</span>&nbsp;' + item.originalItem.expireDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.ManufacturedDateSum + ':</span>&nbsp' + item.originalItem.datetimeManufactured + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.ReceivedDateSum + ':</span>&nbsp' + item.originalItem.receivedDate + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">QOH:</span>&nbsp' + qohDetail  + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">CUR:</span>&nbsp' + curDetail  + ']&nbsp; - &nbsp;[<span style=\"color:blue;\">' + uiLabelMap.BLLocationCodeSum+ ':</span>&nbsp' + locCode + ']</span>';
										}
									}
								});
							}
						}
						editor.on('change', function (event) {
							var args = event.args;
							if (args) {
								var item = args.item;
								if (item) {
									$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', data.uid, 'locationCode', item.originalItem.locationCode);
									$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', data.uid, 'locationId', item.originalItem.locationId);
								}
							}
			        	});
					},
					validation: function (cell, value) {
						if (listInv.length < 1) {
							return { result: false, message: '${uiLabelMap.FacilityNotEnoughProduct}' };
						}
						
						if (value == null || value == undefined || value == '') {
							return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
						}
						return true;
					},
					cellbeginedit: function (row, datafield, columntype) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (data.statusId != 'DELI_ITEM_APPROVED' || listInv.length == 0) {
							return false;
						} else {
							return true;
						}
					},
				}, 
				{ hidden: true, dataField: 'locationId' },
				{ text: '${uiLabelMap.Location}', filterable: false, dataField: 'locationCode', width: 130, cellsalign: 'right', editable: false, sortable: false },
				{ text: '${uiLabelMap.ActualDeliveryQuantitySum}', filterable: false, dataField: 'actualExportedQuantity', columntype: 'numberinput', width: 120, cellsalign: 'right', editable: true, sortable: false,
					cellbeginedit: function (row, datafield, columntype) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (data.statusId != 'DELI_ITEM_APPROVED') {
							return false;
						} else {
							return true;
						}
					},
					initeditor: function (row, value, editor) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (data.statusId == 'DELI_ITEM_EXPORTED') {
							editor.jqxNumberInput({disabled: true});
						} else {
							editor.jqxNumberInput({decimalDigits: 0, disabled: false});
	                    	if (data.requireAmount && data.requireAmount == 'Y') {
	                    		editor.jqxNumberInput({decimalDigits: 3, disabled: false});
	                    	}
							if (value != null && value != undefined) {
								editor.jqxNumberInput('val', value);
							} else {
								if (data.productCode != null && data.productCode != undefined) {
									if (data.requireAmount && data.requireAmount == 'Y'){
										if (data.amount) {
											editor.jqxNumberInput('val', data.amount);
										}
									}else{
										if (data.quantity) {
											editor.jqxNumberInput('val', data.quantity);
										}
									}
								} else {
									editor.jqxNumberInput('val', 0);
								}
							}
						}
					},
					validation: function (cell, value) {
						if (value < 0) {
							return { result: false, message: '${uiLabelMap.NumberGTZ}' };
						}
						var dataTmp = $('#jqxgridDlvItem').jqxGrid('getrowdata', cell.row);
						var convert = dataTmp.convertNumber;
						var prCode = dataTmp.productCode;
						var isPromo = dataTmp.isPromo;
						var rows = $('#jqxgridDlvItem').jqxGrid('getrows');
						
						var requireAmount = dataTmp.requireAmount;
						var selectedAmount = dataTmp.selectedAmount;
						
						var listByPr = [];
						for (var i in rows) {
							if (prCode == rows[i].productCode && isPromo == rows[i].isPromo && requireAmount == rows[i].requireAmount) {
								listByPr.push(rows[i]);
							} 
						}
						
						var allDlvQty = 0;
						for (var k in listDeliveryItemData) {
							if (listDeliveryItemData[k].productCode == prCode && listDeliveryItemData[k].isPromo == isPromo && requireAmount == listDeliveryItemData[k].requireAmount) {
								if (requireAmount && requireAmount == 'Y'){
									allDlvQty = allDlvQty + listDeliveryItemData[k].amount;								
								} else {
									allDlvQty = allDlvQty + listDeliveryItemData[k].quantity;
								}
							}
						}
						var curQty = 0;
						for (var h in listByPr) {
							if (listByPr[h].productCode == prCode && listByPr[h].isPromo == isPromo && listByPr[h].requireAmount == requireAmount) {
								curQty = curQty + listByPr[h].actualExportedQuantity;
							}	
						}
						if (requireAmount && requireAmount == 'Y') {
							var totalCreated = curQty + value - dataTmp.actualExportedQuantity;
							if (totalCreated - allDlvQty > 0) {
								return { result: false, message: '${uiLabelMap.QuantityGreateThanQuantityCreatedInSalesDelivery}: ' + formatnumber(totalCreated, null, 3) + ' > ' + formatnumber(allDlvQty, null, 3)};
							}
						} else {
							var totalCreated = curQty + value - parseInt(dataTmp.actualExportedQuantity);
							if (totalCreated*convert - allDlvQty > 0) {
								return { result: false, message: '${uiLabelMap.QuantityGreateThanQuantityCreatedInSalesDelivery}: ' + totalCreated + ' > ' + allDlvQty/convert};
							}						
						}
						return true;
					},
					cellsrenderer: function (row, column, value) {
						var tmp = null;
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						var convert = data.convertNumber;
						if (value === null || value === undefined || value === '') {
							if (data.productCode) {
								if (data.statusId == 'DELI_ITEM_APPROVED') {
									var id = data.uid;
									var orderQty = data.quantity;
									$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'actualExportedQuantity', orderQty/convert);
									return '<span style=\"text-align: right;\" class=\"focus-color\" title=' + formatnumber(value, null, 3) + '>' + formatnumber(value, null, 3) + '</span>';
									
								} else {
									return '<span style=\"text-align: right;\" class=\"focus-color\" title=' + 0 + '>' + 0 + '</span>';
								}
							} else {
								return '<span style=\"text-align: right;\"></span>';
							}
						}
						if (data.statusId == 'DELI_ITEM_APPROVED') {
							return '<span style=\"text-align: right;\" class=\"focus-color\" title=' + formatnumber(value, null, 3) + '>' + formatnumber(value, null, 3) + '</span>'
						} else {
							return '<span style=\"text-align: right\" title=' + formatnumber(value, null, 3) + '>' + formatnumber(value, null, 3) + '</span>'
						}
					}
				},
				{ text: '${uiLabelMap.ActualDeliveredQuantitySum}', filterable: false, columntype: 'numberinput',  cellsalign: 'right', dataField: 'actualDeliveredQuantity', width: 120, editable: true,
					cellbeginedit: function (row, datafield, columntype) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (data.statusId != 'DELI_ITEM_EXPORTED') {
							return false;
						} else {
							return true;
						}
					}, 
					initeditor: function (row, value, editor) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						var convert = data.convertNumber;
						if (data.statusId != 'DELI_ITEM_EXPORTED') {
							editor.jqxNumberInput({disabled: true});
						} else {
							editor.jqxNumberInput({decimalDigits: 0, disabled: false});
	                    	if (data.requireAmount && data.requireAmount == 'Y') {
	                    		editor.jqxNumberInput({decimalDigits: 3, disabled: false});
	                    	}
							editor.jqxNumberInput({disabled: false});
							if (null === value || value === undefined) {
								if (data.actualExportedQuantity) {
									if (data.actualExportedQuantity > 0) {
										editor.jqxNumberInput('val', data.actualExportedQuantity);
									} else {
										editor.jqxNumberInput({disabled: true});
									}
								}
							} else {
								editor.jqxNumberInput('val', value);
							}
						}
					},
					validation: function (cell, value) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', cell.row);
						if (value < 0) {
							return { result: false, message: '${uiLabelMap.NumberGTZ}' };
						}
						if (value > data.actualExportedQuantity) {
							return { result: false, message: '${uiLabelMap.CannotGreaterThanActualExportedQuantity}' };
						}
						return true;
					},
					cellsrenderer: function (row, column, value) {
						var tmp = null;
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (null === value || value === undefined || value === '') {
							if (data.productCode) {
								if (data.statusId == 'DELI_ITEM_EXPORTED') {
									var id = data.uid;
									var actualExprt = data.actualExportedQuantity;
									if (actualExprt == 0) {
										$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'actualDeliveredQuantity', actualExprt);
										return '<span style=\"text-align: right\" title=' + formatnumber(actualExprt, null, 3) + '>' + formatnumber(actualExprt, null, 3) + '</span>';
									} else {
										$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', id, 'actualDeliveredQuantity', actualExprt);
										return '<span style=\"text-align: right\" class=\"focus-color\" title=' + formatnumber(actualExprt, null, 3) + '>' + formatnumber(actualExprt, null, 3) + '</span>';
									}
								} else {
									return '<span style=\"text-align: right\" title=' + 0 + '>' + 0 + '</span>';
								}
							} else {
								return '<span style=\"text-align: right;\"></span>';
							}
						}
						if (data.statusId == 'DELI_ITEM_EXPORTED') {
							var actualExprt = data.actualExportedQuantity;
							if (actualExprt == 0) {
								return '<span style=\"text-align: right;\" title=' + formatnumber(value, null, 3) + '>' + formatnumber(value, null, 3) + '</span>';
							} else {
								return '<span style=\"text-align: right;\" class=\"focus-color\" title=' + formatnumber(value, null, 3) + '>' + formatnumber(value, null, 3) + '</span>';
							}
						} else {
							return '<span style=\"text-align: right\" title=' + formatnumber(value, null, 3) + '>' + formatnumber(value, null, 3) + '</span>';
						}
						return '<span style=\"text-align: right\" title=' + formatnumber(value, null, 3) + '>' + formatnumber(value, null, 3) + '</span>';
					}
				},
				
				{ text: '${uiLabelMap.IsPromo}', filterable: false, dataField: 'isPromo', width: 120, editable: true, columntype: 'dropdownlist', 
					cellsrenderer: function (row, column, value) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (value === null || value === undefined || value === '') {
							if (data.productCode) {
								return '<span style=\"text-align: right\"></span>';
							} else {
								return '<span style=\"text-align: right;\"></span>';
							}
						} else {
							if (value == 'Y') {
								return '<span style=\"text-align: left\">${uiLabelMap.LogYes}</span>';
							}
							if (value == 'N') {
								return '<span style=\"text-align: left\">${uiLabelMap.LogNO}</span>';
							}
						}
					},
					cellbeginedit: function (row, datafield, columntype) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						var code = data.productCode; 
						var count = 0;
						for (var i in listDeliveryItemData) {
							var item = listDeliveryItemData[i];
							if (item.productCode == code) {
								count = count + 1;
							}
						}
						if ((data.deliveryItemSeqId === null || data.deliveryItemSeqId === undefined) && count > 1) {
							return true;
						} else {
							return false;
						}
					}, 
					initeditor: function (row, value, editor) {
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
							renderer: function (index, label, value) {
								var item = editor.jqxDropDownList('getItem', index);
								if (item.originalItem.isPromoId == 'Y') {
									return '<span style=\"text-align: left\">${uiLabelMap.LogYes}</span>';
								}
								if (item.originalItem.isPromoId == 'N') {
									return '<span style=\"text-align: left\">${uiLabelMap.LogNO}</span>';
								}
							}
						});
						if (data.isPromo == 'Y') {
							editor.jqxDropDownList('selectItem', 'Y');
						} else {
							editor.jqxDropDownList('selectItem', 'N');
						}
						var uid = data.uid;
						var prCode = data.productCode;
						var selectedAmount = data.selectedAmount;
						editor.on('change', function (event) {
							var args = event.args;
							if (args) {
								var item = args.item;
								if (item) {
									var isPromo = item.value;
									var objTmp = 0;
									for (var e in listDeliveryItemData) {
										if (listDeliveryItemData[e].productCode == prCode && listDeliveryItemData[e].isPromo == isPromo && listDeliveryItemData[e].requireAmount == requireAmount) {
											objTmp = listDeliveryItemData[e];
											break;
										}
									}
									$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', uid, 'quantity', objTmp.quantity);
									$('#jqxgridDlvItem').jqxGrid('setcellvaluebyid', uid, 'fromOrderItemSeqId', objTmp.fromOrderItemSeqId);
								} 
							}
						});
					}
				},
				{ text: '${uiLabelMap.RequiredExpireDate}', filterable: false, hidden: true, dataField: 'expireDate', width: 120, cellsformat: 'dd/MM/yyyy', editable: false, cellsalign: 'right',
					cellsrenderer: function (row, column, value) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (value === null || value === undefined || value === '') {
							if (data.productCode) {
								return '<span style=\"text-align: right\"></span>';
							} else {
								return '<span style=\"text-align: right;\"></span>';
							}
						} else {
							return '<span style=\"text-align: right\">'+ DatetimeUtilObj.getFormattedDate(value)+'</span>';
						}
					}
				},
				{ text: '${uiLabelMap.WeightBaseSum}', filterable: false, hidden: true, dataField: 'weight', cellsalign: 'right', width: 120, editable: false, sortable: false,
					cellsrenderer: function (row, column, value) {
						var desc;
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						for (var i in weightUomData) {
							if (weightUomData[i].uomId == data.weightUomId) {
								desc = weightUomData[i].description;
							}
						}
						if (value != null && value != undefined && '' != value) {
							return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>';
						} else {
							if (data.productCode) {
								if (data.productWeight) {
									value = data.productWeight;
									return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) +'</span>';
								} else {
									return '<span style=\"text-align: right\" title=\"0\">0</span>';
								}
							} else {
								return '<span style=\"text-align: right;\"></span>';
							}
						}
					}
				},
				{ text: '${uiLabelMap.Weight}', filterable: false, hidden:true, datafield: 'selectedAmount', align: 'left', width: 110, filtertype: 'checkedlist', columntype: 'dropdownlist', editable: false,
					cellsrenderer: function (row, column, value) {
						if (value) {	
							var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							var weightUomId = data.weightUomId;
							return '<span style=\"text-align: right\">' + formatnumber(value) + ' ' + getUomDescription(weightUomId) + '</span>';
						} else {
							return '<span style=\"text-align: right\"></span>';
						}
					}
				},
				{ text: '${uiLabelMap.OrderNumberSum}', filterable: false, dataField: 'quantity', cellsalign: 'right', width: 110, editable: false,
					cellsrenderer: function (row, column, value) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (value === null || value === undefined || value === "") {
							if (data.productCode) {
								return '<span style=\"text-align: right;\"></span>';
							} else {
								return '<span style=\"text-align: right;\"></span>';
							}
						}
						var convert = data.convertNumber;
						value = value/convert;
						if (data.requireAmount && data.requireAmount == 'Y'){
							value = data.amount;
						}
						return '<span style=\"text-align: right\">' + formatnumber(value, null, 3) +'</span>';
					}
				},
				{ text: '${uiLabelMap.Status}', filterable: false, dataField: 'statusId', width: 120, editable: false,
					cellsrenderer: function (row, column, value) {
						var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
						if (value === null || value === undefined || value === '') {
							if (data.productCode) {
								return '<span style=\"text-align: right\"></span>';
							} else {
								return '<span style=\"text-align: right;\"></span>';
							}
						} else {
							for (var i in dlvItemStatusData) {
								if (value == dlvItemStatusData[i].statusId) {
									return '<span title=' + value + '>' + dlvItemStatusData[i].description + '</span>';
								}
							}
						}
					}
				}
			]
		});
	}
	
	function loadNoteGrid(valueDataSoure) {
		var sourceOrderItem =
		{
			datafields:
			[
				{ name: 'productId', type: 'string' },
				{ name: 'productCode', type: 'string' },
				{ name: 'productName', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'orderQuantityUomId', type: 'string' },
				{ name: 'inventoryItemId', type: 'string' },
				{ name: 'deliveryId', type: 'string' },
				{ name: 'deliveryItemSeqId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'convertNumber', type: 'number' },
				{ name: 'note', type: 'string' },
				{ name: 'inventoryItemStatusId', type: 'string' },
				{ name: 'returnReasonId', type: 'string' },
				{ name: 'expiredDate', type: 'date', other: 'Timestamp' },
				{ name: 'manufacturedDate', type: 'date', other: 'Timestamp' },
				{ name: 'actualExpireDate', type: 'date', other: 'Timestamp' },
				{ name: 'actualManufacturedDate', type: 'date', other: 'Timestamp' },
				{ name: 'actualExportedQuantity', type: 'number' },
				{ name: 'actualDeliveredQuantity', type: 'number' },
				{ name: 'batch', type: 'string' },
				{ name: 'expRequired', type: 'string' },
				{ name: 'mnfRequired', type: 'string' },
				{ name: 'topRequired', type: 'string' },
			],
			localdata: valueDataSoure,
			datatype: "array",
		};
		var dataAdapterOrderItem = new $.jqx.dataAdapter(sourceOrderItem);
		$("#noteGrid").jqxGrid({
			source: dataAdapterOrderItem,
			filterable: false,
			showfilterrow: false,
			theme: theme,
			rowsheight: 26,
			width: '100%',
			height: 210,
			enabletooltips: true,
			autoheight: false,
			pageable: true,
			pagesize: 5,
			editable: true,
			localization: getLocalization(),
			columns:
			[
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<span style=margin:4px;>' + (value + 1) + '</span>';
					}
				},
				{ text: '${uiLabelMap.ProductId}', dataField: 'productCode', width: 120, editable: false, pinned: true},
				{ text: '${uiLabelMap.Quantity}', dataField: 'quantity', width: 120, editable: false, 
					cellsrenderer: function (row, column, value) {
						return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
					}
				},
				{ text: '${uiLabelMap.BSSalesUomId}', dataField: 'orderQuantityUomId', width: 120, editable: false,
					cellsrenderer: function (row, column, value) {
						for (var i in quantityUomData) {
							if (value == quantityUomData[i].uomId) {
								return '<span style=\"text-align: right\">'+ quantityUomData[i].description + '</span>';
							}
						}
					}
				},
				{ text: '${uiLabelMap.ManufactureDate}', dataField: 'manufacturedDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 150, editable: true, sortable: false,
					cellsrenderer: function (row, column, value) {
						if (value) {
							return '<span class=\"align-right focus-color\">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
						} else {
							var data = $('#noteGrid').jqxGrid('getrowdata', row);
							if (data.actualManufacturedDate) {
								var id = data.uid;
								$('#noteGrid').jqxGrid('setcellvaluebyid', id, 'manufacturedDate', data.actualManufacturedDate);
								return '<span class=\"align-right focus-color\">' + DatetimeUtilObj.getFormattedDate(new Date(data.actualManufacturedDate)) + '</span>';
							}
							return '<span class=\"align-right focus-color\"></span>';
						}
					},
					createeditor: function (row, column, editor) {
						editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy', showFooter: true});
						var data = $('#noteGrid').jqxGrid('getrowdata', row);
						if (data.actualManufacturedDate) {
							editor.jqxDateTimeInput('setDate', new Date(data.actualManufacturedDate));
						}
					},
					validation: function (cell, value) {
						var now = new Date();
						if (value) {
							if (value > now) {
								return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeNow}' };
							}
							var data = $('#noteGrid').jqxGrid('getrowdata', cell.row);
							if (data.expiredDate) {
								var exp = new Date(data.expiredDate);
								if (exp < new Date(value)) {
									return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeExpireDate}' };
								}
							}
						} else {
							var data = $('#noteGrid').jqxGrid('getrowdata', cell.row);
							if (data.mnfRequired == 'Y') {
								return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
							}
						}
						return true;
					}
				},
				{ text: '${uiLabelMap.ExpireDate}', dataField: 'expiredDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 150, editable: true, sortable: false,
					cellsrenderer: function (row, column, value) {
						if (value) {
							return '<span class=\"align-right focus-color\">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
						} else {
							var data = $('#noteGrid').jqxGrid('getrowdata', row);
							if (data.actualExpireDate) {
								var id = data.uid;
								$('#noteGrid').jqxGrid('setcellvaluebyid', id, 'expiredDate', data.actualExpireDate);
								return '<span class=\"align-right focus-color\">' + DatetimeUtilObj.getFormattedDate(new Date(data.actualExpireDate)) + '</span>';
							}
							return '<span class=\"align-right focus-color\"></span>';
						}
					},
					createeditor: function (row, column, editor) {
						editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy', showFooter: true});
						var data = $('#noteGrid').jqxGrid('getrowdata', row);
						if (data.actualExpireDate) {
							editor.jqxDateTimeInput('setDate', new Date(data.actualExpireDate));
						}
					},
					validation: function (cell, value) {
						if (value) {
							var data = $('#noteGrid').jqxGrid('getrowdata', cell.row);
							if (data.manufacturedDate) {
								var mft = new Date(data.manufacturedDate);
								if (mft > new Date(value)) {
									return { result: false, message: '${uiLabelMap.ExpireDateMustBeBeforeManufactureDate}' };
								}
							}
						} else {
							var data = $('#noteGrid').jqxGrid('getrowdata', cell.row);
							if (data.expRequired == 'Y') {
								return { result: false, message: '${uiLabelMap.DmsFieldRequired}' };
							}
						}
						return true;
					}
				},
				{ text: '${uiLabelMap.Reason}', dataField: 'returnReasonId', minwidth: 300, editable: true, columntype: 'dropdownlist',
					cellsrenderer: function (row, column, value) {
						var data = $('#noteGrid').jqxGrid('getrowdata', row);
						var id = data.uid;
						if (!value && returnReasonData.length > 0) {
							$('#noteGrid').jqxGrid('setcellvaluebyid', id, 'returnReasonId', returnReasonData[0].returnReasonId);
							value = returnReasonData[0].returnReasonId;
						}
						if (!value) {
							return '<span style=\"text-align: left\" class=\"focus-color\">${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}</span>';
						} else {
							var data = $('#noteGrid').jqxGrid('getrowdata', row);
							for (var i in returnReasonData) {
								if (returnReasonData[i].returnReasonId == value) {
									return '<span style=\"text-align: left\" class=\"focus-color\">' + returnReasonData[i].description + '</span>';
								}
							}
						}
					},
					initeditor: function (row, value, editor) {
						editor.jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', autoDropDownHeight: false, dropDownHeight: '200px', source: returnReasonData, selectedIndex: 0, dropDownWidth: '300px', popupZIndex: 755, displayMember: 'description', valueMember: 'returnReasonId',});
					}
				},
				{ text: '${uiLabelMap.ProductStatus}', dataField: 'inventoryItemStatusId', width: 150, editable: true, columntype: 'dropdownlist',
					cellsrenderer: function (row, column, value) {
						var data = $('#noteGrid').jqxGrid('getrowdata', row);
						var id = data.uid;
						if (!value) {
							$('#noteGrid').jqxGrid('setcellvaluebyid', id, 'inventoryItemStatusId', 'Good');
							value = 'Good';
						}
						for (var i in invStatusData) {
							if (invStatusData[i].statusId == value) {
								return '<span style=\"text-align: left\" class=\"focus-color\">' + invStatusData[i].description + '</span>';
							}
						}
					},
					initeditor: function (row, value, editor) {
						editor.jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', autoDropDownHeight: false, dropDownHeight: '200px', source: invStatusData, selectedIndex: 0, dropDownWidth: '150px', popupZIndex: 755, displayMember: 'description', valueMember: 'statusId',});
					}
				}
			]
	    });
	}
	
	function loadDebtGrid(valueDataSoure) {
		var sourceOrderItem =
		{
			datafields:
			[
				{ name: 'productId', type: 'string' },
				{ name: 'productCode', type: 'string' },
				{ name: 'productName', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'requireAmount', type: 'string' },
				{ name: 'fromOrderId', type: 'string' },
				{ name: 'fromOrderItemSeqId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'orderQuantityUomId', type: 'string' },
				{ name: 'orderWeightUomId', type: 'string' },
				{ name: 'debtQuantity', type: 'number' },
				{ name: 'actualExportedQuantity', type: 'number' }
			],
			localdata: valueDataSoure,
			datatype: "array"
		};
		var dataAdapterOrderItem = new $.jqx.dataAdapter(sourceOrderItem);
		var grid = $("#debtGrid");
		grid.jqxGrid({
			source: dataAdapterOrderItem,
			filterable: false,
			showfilterrow: false,
			theme: theme,
			rowsheight: 26,
			width: '100%',
			height: 345,
			enabletooltips: true,
			autoheight: false,
			pageable: true,
			pagesize: 10,
			editable: true,
			localization: getLocalization(),
			columns:
			[
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<span style=margin:4px;>' + (value + 1) + '</span>';
					}
				},
				{ text: '${uiLabelMap.ProductId}', dataField: 'productCode', width: 120, editable: false, pinned: true},
				{ text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 120, editable: false, pinned: true},
				{ text: '${uiLabelMap.Quantity}', dataField: 'actualExportedQuantity', width: 120, editable: false, 
					cellsrenderer: function (row, column, value) {
						return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
					}
				},
				{ text: '${uiLabelMap.BSSalesUomId}', dataField: 'orderQuantityUomId', width: 120, editable: false,
					cellsrenderer: function (row, column, value) {
						var data = grid.jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') value = data.weightUomId;
						return '<span style=\"text-align: right\">' + getUomDescription(value) +'</span>';
					}
				},
				{ text: '${uiLabelMap.BLDebtQuantity}',columntype: 'numberinput',  cellsalign: 'right', dataField: 'debtQuantity', width: 120, editable: true, 
					cellsrenderer: function (row, column, value) {
						return '<span class=\"focus-color align-right\">'+ formatnumber(value)+ '</span>';
					},
					initeditor: function (row, value, editor) {
						var data = grid.jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						if (requireAmount && 'Y' == requireAmount) {
							editor.jqxNumberInput({ decimalDigits: 2});
						} else {
							editor.jqxNumberInput({ decimalDigits: 0});
						}
					},
					validation: function (cell, value) {
						if (value < 0) {
							return { result: false, message: '${uiLabelMap.NumberGTZ}' };
						}
						var data = grid.jqxGrid('getrowdata', cell.row);
						if (data.actualExportedQuantity < value) {
							return { result: false, message: '${uiLabelMap.QuantityGreateThanQuantityCreatedInDelivery} ' + value + ' > ' + data.actualExportedQuantity};
						}
						return true;
					}
				}
			]
		});
	}
	
	function loadEditGrid(valueDataSoure) {
		var sourceOrderItem =
		{
			datafields:
			[
				{ name: 'productId', type: 'string' },
				{ name: 'productCode', type: 'string' },
				{ name: 'productName', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'requireAmount', type: 'string' },
				{ name: 'fromOrderId', type: 'string' },
				{ name: 'fromOrderItemSeqId', type: 'string' },
				{ name: 'deliveryId', type: 'string' },
				{ name: 'deliveryItemSeqId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'baseQuantityUomId', type: 'string' },
				{ name: 'baseWeightUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'createdQuantity', type: 'number' },
				{ name: 'newQuantity', type: 'number' },
				{ name: 'convertNumber', type: 'number' }
			],
			localdata: valueDataSoure,
			datatype: "array"
		};
		var dataAdapterOrderItem = new $.jqx.dataAdapter(sourceOrderItem);
		var grid = $("#editGrid");
		grid.jqxGrid({
			source: dataAdapterOrderItem,
			filterable: true,
			showfilterrow: true,
			theme: theme,
			rowsheight: 26,
			width: '100%',
			height: 360,
			enabletooltips: true,
			autoheight: false,
			pageable: true,
			pagesize: 10,
			editable: true,
			localization: getLocalization(),
			columns:
			[
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<span style=margin:4px;>' + (value + 1) + '</span>';
					}
				},
				{ text: '${uiLabelMap.ProductId}', dataField: 'productCode', width: 120, editable: false, pinned: true},
				{ text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 120, editable: false, pinned: true},
				{ text: '${uiLabelMap.BLQuantityAvailable}', filterable: false, dataField: 'quantity', width: 150, editable: false, 
					cellsrenderer: function (row, column, value) {
						return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
					}
				},
				{ text: '${uiLabelMap.BSSalesUomId}', filterable: false, dataField: 'quantityUomId', width: 150, editable: false,
					cellsrenderer: function (row, column, value) {
						var data = grid.jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') value = data.orderWeightUomId;
						return '<span style=\"text-align: right\">' + getUomDescription(value) +'</span>';
					}
				},
				{ text: '${uiLabelMap.CreatedNumberSum}', filterable: false, columntype: 'numberinput', hidden: true,  cellsalign: 'right', dataField: 'createdQuantity', width: 150, editable: false, 
					cellsrenderer: function (row, column, value) {
						return '<span class=\"align-right\">'+ formatnumber(value)+ '</span>';
					},
				},
				{ text: '${uiLabelMap.Quantity}', filterable: false, columntype: 'numberinput',  cellsalign: 'right', dataField: 'newQuantity', width: 150, editable: true, 
					cellsrenderer: function (row, column, value) {
						return '<span class=\"focus-color align-right\">'+ formatnumber(value)+ '</span>';
					},
					initeditor: function (row, value, editor) {
						var data = grid.jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						if (requireAmount && 'Y' == requireAmount) {
							editor.jqxNumberInput({ decimalDigits: 2});
						} else {
							editor.jqxNumberInput({ decimalDigits: 0});
						} 
					},
					validation: function (cell, value) {
						if (value < 0) {
							return { result: false, message: '${uiLabelMap.NumberGTOEZ}' };
						}
						var data = grid.jqxGrid('getrowdata', cell.row);
						if (data.quantity < value) {
							return { result: false, message: '${uiLabelMap.BLQuantityGreateThanQuantityAvailable} ' + value + ' > ' + data.quantity};
						}
						return true;
					}
				}
			]
		});
	}
	
	function loadEditAddProductGrid(valueDataSoure) {
		var sourceOrderItem =
		{
			datafields:
			[
				{ name: 'productId', type: 'string' },
				{ name: 'productCode', type: 'string' },
				{ name: 'productName', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'requireAmount', type: 'string' },
				{ name: 'fromOrderId', type: 'string' },
				{ name: 'fromOrderItemSeqId', type: 'string' },
				{ name: 'deliveryId', type: 'string' },
				{ name: 'deliveryItemSeqId', type: 'string' },
				{ name: 'weightUomId', type: 'string' },
				{ name: 'quantityUomId', type: 'string' },
				{ name: 'baseQuantityUomId', type: 'string' },
				{ name: 'baseWeightUomId', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'createdQuantity', type: 'number' },
				{ name: 'newQuantity', type: 'number' },
				{ name: 'convertNumber', type: 'number' }
			],
			localdata: valueDataSoure,
			datatype: "array"
		};
		var dataAdapterOrderItem = new $.jqx.dataAdapter(sourceOrderItem);
		var grid = $("#editAddProductGrid");
		grid.jqxGrid({
			source: dataAdapterOrderItem,
			filterable: false,
			showfilterrow: false,
			theme: theme,
			rowsheight: 26,
			width: '100%',
			height: 345,
			enabletooltips: true,
			autoheight: false,
			pageable: true,
			pagesize: 10,
			editable: true,
			selectionmode: 'checkbox',
			localization: getLocalization(),
			columns:
			[
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
					groupable: false, draggable: false, resizable: false,
					datafield: '', columntype: 'number', width: 50,
					cellsrenderer: function (row, column, value) {
						return '<span style=margin:4px;>' + (value + 1) + '</span>';
					}
				},
				{ text: '${uiLabelMap.ProductId}', dataField: 'productCode', width: 120, editable: false, pinned: true},
				{ text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 120, editable: false, pinned: true},
				{ text: '${uiLabelMap.BLQuantityAvailable}', dataField: 'quantity', width: 150, editable: false, 
					cellsrenderer: function (row, column, value) {
						return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
					}
				},
				{ text: '${uiLabelMap.BSSalesUomId}', dataField: 'quantityUomId', width: 150, editable: false,
					cellsrenderer: function (row, column, value) {
						var data = grid.jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') value = data.orderWeightUomId;
						return '<span style=\"text-align: right\">' + getUomDescription(value) +'</span>';
					}
				},
				{ text: '${uiLabelMap.Quantity}',columntype: 'numberinput',  cellsalign: 'right', dataField: 'newQuantity', width: 150, editable: true, 
					cellsrenderer: function (row, column, value) {
						return '<span class=\"focus-color align-right\">'+ formatnumber(value)+ '</span>';
					},
					initeditor: function (row, value, editor) {
						var data = grid.jqxGrid('getrowdata', row);
						var requireAmount = data.requireAmount;
						if (requireAmount && 'Y' == requireAmount) {
							editor.jqxNumberInput({ decimalDigits: 2});
						} else {
							editor.jqxNumberInput({ decimalDigits: 0});
						} 
					},
					validation: function (cell, value) {
						if (value < 0) {
							return { result: false, message: '${uiLabelMap.NumberGTOEZ}' };
						}
						var data = grid.jqxGrid('getrowdata', cell.row);
						if (data.quantity < value) {
							return { result: false, message: '${uiLabelMap.BLQuantityGreateThanQuantityAvailable} ' + value + ' > ' + data.quantity};
						}
						return true;
					}
				}
			]
		});
	}
	
</script>