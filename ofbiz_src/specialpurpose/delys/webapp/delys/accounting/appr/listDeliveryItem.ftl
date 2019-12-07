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
						{ text: '${uiLabelMap.ProductProductId}', dataField: 'productId', width: 150, editable: false, pinned: true},
						{ text: '${uiLabelMap.quantity}', dataField: 'quantity', cellsalign: 'right', width: 80, editable: false,
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
						{ text: '${uiLabelMap.RequiredExpireDate}', dataField: 'expireDate', width: 150, cellsformat: 'dd/MM/yyyy', editable: false, cellsalign: 'right'},
						{ text: '${uiLabelMap.ExpireDate}', dataField: 'inventoryItemId', columntype: 'dropdownlist', width: 130, editable: true, sortable: false,
						    cellsrenderer: function(row, column, value){
						        var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
						        if(!data || !data.actualExpireDate){
						            if(value != null && value != ''){
						                for(i = 0; i < listInv.length;i++){
						                    if(listInv[i].inventoryItemId == value){
						                        var tmpDate = new Date(listInv[i].expireDate.time);
						                        return '<span style=\"text-align: right\">' + $.datepicker.formatDate('dd/mm/yy', tmpDate) + '</span>';
						                    }
						                }
						                var tmpDate = new Date(value);
						                return '<span style=\"text-align: right\">' + value + '</span>';
	                                }
						            return '<span></span>';
						        }
						        var tmpDate = new Date(data.actualExpireDate);
						        return '<span style=\"text-align: right\">' + $.datepicker.formatDate('dd/mm/yy', tmpDate) + '</span>';
						    }, 
						    initeditor: function(row, value, editor){
    						    var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
    						    var invData = [];
    						    var iIndex = 0;
                                for(i = 0; i < listInv.length;i++){
                                    if(listInv[i].productId == data.productId){
                                        var tmpDate ;//= new Date(listInv[i].expireDate.time);
                                        var tmpValue = new Object();
                                        
                                        //tmpValue.expireDate =  $.datepicker.formatDate('dd/mm/yy', tmpDate);
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
                                        invData[iIndex++] = tmpValue;
                                    }
                                }
                                editor.jqxDropDownList({ source: invData, dropDownWidth: '220px', popupZIndex: 755, displayMember: 'expireDate', valueMember: 'inventoryItemId',
                                    renderer: function(index, label, value) {
                                        var item = editor.jqxDropDownList('getItem', index);
                                        return '<span>[<span style=\"color:green;\">RC:</span>&nbsp;' + item.originalItem.receivedDate + ']&nbsp;[<span style=\"color:blue;\">EXP:</span>&nbsp' + item.originalItem.expireDate + ']</span>';
                                    }
                                });
                                for(i = 0; i < invData.length;i++){
                                    var tmpDate = new Date(data.actualExpireDate);
                                    var tmpStr = $.datepicker.formatDate('dd/mm/yy', tmpDate);
                                    if((invData[i].productId = data.productId) && (tmpStr = data.actualExpireDate)){
                                        editor.jqxDropDownList('selectItem', invData[i].inventoryItemId);
                                        break;
                                    }
                                }
                                if(data.statusId == 'DELI_ITEM_CONFIRMED'){
                                    editor.jqxDropDownList({disabled: true});
                                }else{
                                    editor.jqxDropDownList({disabled: false});
                                }
                        },validation: function (cell, value) {
                            if (value == null || value == undefined || value == '') {
                                return { result: false, message: '${uiLabelMap.RequiredINV}'};
                            }
                            return true;
                        },
                        cellbeginedit: function (row, datafield, columntype) {
							var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
							var check = $.inArray(glOriginFacilityId, listFacilityManage);
							if((data.statusId == 'DELI_ITEM_EXPORTED')  || (data.statusId == 'DELI_ITEM_CONFIRMED') || (data.statusId == 'DELI_ITEM_DELIVERED') || check == -1 || (isStorekeeperFrom == false && isSpecialist == false) || ((isStorekeeperFrom == true || isSpecialist == true) && data.statusId != 'DELI_ITEM_CREATED')){
								return false;
							}else{
                                return true;
							}
					    },
                        },
					 	"/>
<#if security.hasPermission("DELIVERY_ITEM_UPDATE", userLogin)>
	<#assign columnlist2 = columnlist2 + "{ text: '${uiLabelMap.actualExportedQuantity}', dataField: 'actualExportedQuantity', columntype: 'numberinput', width: 150, cellsalign: 'right', editable: true, sortable: false,
											cellbeginedit: function (row, datafield, columntype) {
												var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
												var check = $.inArray(glOriginFacilityId, listFacilityManage);
												if((data.statusId == 'DELI_ITEM_EXPORTED')  || (data.statusId == 'DELI_ITEM_CONFIRMED') || (data.statusId == 'DELI_ITEM_DELIVERED') || check == -1 || (isStorekeeperFrom == false && isSpecialist == false) || ((isStorekeeperFrom == true || isSpecialist == true) && data.statusId != 'DELI_ITEM_CREATED')){
													tmpEditable = true;
													return false;
												}else{
												    tmpEditable = false;
                                                    return true;
												}
										    },
										    initeditor: function(row, value, editor){
										        var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
										        if(data.statusId == 'DELI_ITEM_EXPORTED' || data.statusId == 'DELI_ITEM_CONFIRMED' ){
				                                    editor.jqxNumberInput({disabled: true});
				                                }else{
				                                    editor.jqxNumberInput({disabled: false});
                                                    if (data.quantity){
                                                    	editor.jqxNumberInput('val', data.quantity);
                                                    }
				                                }
										    },
										    validation: function (cell, value) {
										        if (value <= 0) {
										            return { result: false, message: '${uiLabelMap.NumberGTZ}'};
										        }
										        return true;
										    },
										    cellsrenderer: function (row, column, value){
												 return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
											 }
										  },
										 { text: '${uiLabelMap.actualDeliveredQuantity}', columntype: 'numberinput',  cellsalign: 'right', dataField: 'actualDeliveredQuantity', width: 150, editable: true,
											cellbeginedit: function (row, datafield, columntype) {
												var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
												var check = $.inArray(glOriginFacilityId, listFacilityManage);
												
												if(data.statusId == 'DELI_ITEM_DELIVERED' || check == -1 || (isStorekeeperTo == false && isSpecialist == false)){
													tmpEditable = true;
													return false;
												}else{
													if (isSpecialist == true && (data.statusId != 'DELI_ITEM_EXPORTED' || data.statusId != 'DELI_ITEM_CONFIRMED')){
														tmpEditable = false;
	                                                    return true;
													} else {
														tmpEditable = true;
														return false;
													}
													if (isStorekeeperTo == true && (data.statusId != 'DELI_ITEM_EXPORTED' || data.statusId != 'DELI_ITEM_CONFIRMED')){
														tmpEditable = false;
	                                                    return true;
													} else {
														tmpEditable = true;
														return false;
													}
                                                }
											 }, 
											 initeditor: function(row, value, editor){
	                                                var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
	                                                if (data.fromTransferItemSeqId){
	                                                	if(data.statusId != 'DELI_ITEM_EXPORTED'){
	                                                		editor.jqxNumberInput({disabled: true});
		                                                }else{
		                                                    editor.jqxNumberInput({disabled: false});
		                                                    if (data.actualExportedQuantity){
		                                                    	editor.jqxNumberInput('val', data.actualExportedQuantity);
		                                                    }
		                                                }
	                                                } else if (data.fromOrderItemSeqId) {
	                                                	if(data.statusId != 'DELI_ITEM_EXPORTED'){
	                                                		editor.jqxNumberInput({disabled: true});
		                                                }else{
		                                                    editor.jqxNumberInput({disabled: false});
		                                                    if (data.actualExportedQuantity){
		                                                    	editor.jqxNumberInput('val', data.actualExportedQuantity);
		                                                    }
		                                                }
	                                                }
	                                         },
	                                         validation: function (cell, value) {
	                                        	 if (tmpEditable){
											        if (value <= 0) {
											            return { result: false, message: '${uiLabelMap.NumberGTZ}'};
											        }
	                                        	 } else {
	                                        		 return true;
	                                        	 }
										        return true;
											 },
											 cellsrenderer: function (row, column, value){
												 return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + '</span>'
											 }
										 },
										 { text: '${uiLabelMap.weight}', dataField: 'weight', cellsalign: 'right', width: 150, editable: false, sortable: false,
											 cellsrenderer: function (row, column, value){
												 var desc;
												 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
												 for(var i = 0; i < weightUomData.length; i++){
													 if(weightUomData[i].uomId == data.weightUomId){
														 desc = weightUomData[i].description;
													 }
												 }
												 return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + ' ('+desc+')</span>'
											 }
										 },
										 "/>
<#else>
	<#assign columnlist2 =columnlist2 + "{ text: '${uiLabelMap.actualExportedQuantity}', cellsalign: 'right', dataField: 'actualExportedQuantity', width: 150, editable: false, sortable: false,},
										{ text: '${uiLabelMap.actualDeliveredQuantity}', cellsalign: 'right', dataField: 'actualDeliveredQuantity', width: 150, editable: false, sortable: false,},
										{ text: '${uiLabelMap.weight}', dataField: 'weight', cellsalign: 'right', width: 150, editable: false, sortable: false,
											cellsrenderer: function (row, column, value){
												 var desc;
												 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
												 for(var i = 0; i < weightUomData.length; i++){
													 if(weightUomData[i].uomId == data.weightUomId){
														 desc = weightUomData[i].description;
													 }
												 }
												 return '<span style=\"text-align: right\" title=' + value.toLocaleString('${localeStr}') + '>' + value.toLocaleString('${localeStr}') + ' ('+desc+')</span>';
											 }
										 },
										"/>
</#if>					 
					 
<#assign columnlist2 = columnlist2 + "
					 { text: '${uiLabelMap.statusId}', dataField: 'statusId', width: 150, editable: false,
						 cellsrenderer: function(row, column, value){
							 for(var i = 0; i < dlvItemStatusData.length; i++){
								 if(value == dlvItemStatusData[i].statusId){
									 return '<span title=' + value + '>' + dlvItemStatusData[i].description + '</span>';
								 }
							 }
						 }
					 	},
					 	{ text: '${uiLabelMap.comments}', dataField: 'comments', width: 150, editable: false},
					 "/>
</div>
<@jqGrid width="890" id="jqxgrid2" autoheight="false" height="262" usecurrencyfunction="true" dataField=dataField2 columnlist=columnlist2 clearfilteringbutton="false" showtoolbar="true" filterable="false" editable="true" sortable="false"
		url="jqxGeneralServicer?sname=getListDeliveryItem" bindresize="false" jqGridMinimumLibEnable="false" offlinerefreshbutton="false"
		otherParams="productId,quantityUomId,expireDate,defaultWeightUomId:S-getDeliveryItemDetail(deliveryId,deliveryItemSeqId)<productId,quantityUomId,expireDate,defaultWeightUomId>;weight,weightUomId:S-getProductDeliveryWeight(deliveryId,deliveryItemSeqId)<weight,weightUomId>"
		customTitleProperties="DeliveryItemList" viewSize="5" functionAfterUpdate="functionAfterUpdate2()"
		selectionmode="checkbox" editmode="dblclick" updateoffline="true" rowselectfunction="rowselectfunction(event);"
		defaultSortColumn="deliveryItemSeqId ASC" customLoadFunction="true" beforeprocessing="false"/>
