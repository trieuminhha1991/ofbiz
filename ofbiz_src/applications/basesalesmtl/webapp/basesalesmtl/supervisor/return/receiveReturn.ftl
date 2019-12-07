<#include "component://baselogistics/webapp/baselogistics/return/script/receiveReturnScript.ftl"/>
<div id="notifyUpdateSuccess" style="display: none;">
<div>
	${uiLabelMap.NotifiUpdateSucess}. 
</div>
</div>
<div id="containerNotify" style="width: 100%; overflow: auto;">
</div>
<div>
	<h4 class="row header smaller lighter blue" style="margin: 5px 0px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
		${uiLabelMap.GeneralInfo}
	</h4>
</div>
<div class="rowfluid">
	<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
	<div class="span12">
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.OrderReturnId}: </div>
				</div>  
				<div class="span7">
					<div class="green-label">${returnId}</div>
		   		</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.CreateBy}: </div>
				</div>  
				<div class="span7">
					<div id="createdBy" class="green-label">${fullNameCreateBy}</div>
				</div>
			</div>
		</div>
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.ReturnFrom}: </div>
				</div>  
				<div class="span7">
					<div id="fromPartyId" class="green-label">${fromPartyId} [${returnHeader.fromPartyId}]</div>
		   		</div>
			</div>
			<#if returnHeaderTypeId == 'CUSTOMER_RETURN'>
				<div class='span6'>
					<div class='span5 text-algin-right'>
					<div>${uiLabelMap.CreatedDate}: </div>
				</div>  
				<div class="span7">
					<div id="entryDate" class="green-label"></div> 
					</div>
				</div>
			<#else>
				<div class='span6'>
					<div class='span5 text-algin-right'>
						<div>${uiLabelMap.ReturnTo}: </div>
					</div>  
					<div class="span7">
						<div id="toName" class="green-label">
							${toPartyName?if_exists}
						</div>
			   		</div>
				</div>
			</#if>
		</div>
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
					<div>${uiLabelMap.ReturnType}: </div>
				</div>  
				<div class="span7">
					<div id="returnHeaderTypeId" class="green-label">${returnHeaderTypeDesc?if_exists}</div>
		   		</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div class="asterisk">${uiLabelMap.ReceiveToFacility}: </div>
				</div>  
				<div class="span7">
					<div style="font-weight: bold;" id="destinationFacilityId" class="green-label">${facility.facilityName?if_exists}</div>
		   		</div>
			</div>
		</div>
		<div class='row-fluid margin-bottom8'>
			<div class='span5'>
				<div class='span5 text-algin-right'>
				<div>${uiLabelMap.OrderCurrentStatus}: </div>
			</div>  
			<div class="span7">
				<div id="statusId" class="green-label">${statusId}</div>
	   		</div>
			</div>
			<div class='span6'>
				<div class='span5 text-algin-right'>
					<div class="asterisk">${uiLabelMap.ReceivedDate}: </div>
				</div>  
				<div class="span7">
					<div id="datetimeReceived"></div>
		   		</div>
			</div>
		</div>
	</div>
	</div>
</div>
<#assign columnlist="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.ProductId}', pinned: true, dataField: 'productCode', width: 150, editable:false, 
						cellsrenderer: function (row, column, value){
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
						},
					},
					{ text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 200, editable:false,
					},
					{ text: '${uiLabelMap.ManufactureDate}', dataField: 'datetimeManufactured', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 150, editable: true, sortable: false, filterable: false,
						cellsrenderer: function (row, column, value){
							if (value){
								return '<span style=\"text-align: right;\" class=\"focus-color\">' + ReceiveReturnObj.getFormattedDate(new Date(value)) + '</span>';
							} else {
								if (listReturnItemSelected.length > 0){
									var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
									for (var i = 0; i < listReturnItemSelected.length; i ++){
										if (listReturnItemSelected[i].returnItemSeqId == data.returnItemSeqId){
											value = listReturnItemSelected[i].datetimeManufactured;
											break;
										}
									}
									if (value != null && value != undefined && value != ''){
										return '<span style=\"text-align: right; background-color: #deedf5; height: 100%;\">' + ReceiveReturnObj.getFormattedDate(new Date(value)) + '</span>';
									} else {
										return '<span style=\"text-align: right; background-color: #deedf5; height: 100%;\">_NA_</span>';
									}
								} else {
									return '<span style=\"text-align: right; background-color: #deedf5; height: 100%;\">_NA_</span>';
								}
							}
						},
						initeditor: function (row, column, editor) {
							editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							if (data.datetimeManufactured){
								editor.jqxDateTimeInput('setDate', new Date(data.datetimeManufactured));
							} else {
								if (listReturnItemSelected.length > 0){
									for (var i = 0; i < listReturnItemSelected.length; i ++){
										if (listReturnItemSelected[i].returnItemSeqId == data.returnItemSeqId){
											if (listReturnItemSelected[i].datetimeManufactured != null && listReturnItemSelected[i].datetimeManufactured != undefined){
												editor.jqxDateTimeInput('setDate', new Date(listReturnItemSelected[i].datetimeManufactured));
												break;
											}
										}
									}
								}
							}
					 	},
					 	validation: function (cell, value) {
					 		var now = new Date();
					        if (value > now) {
					            return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeNow}'};
					        }
					        var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', cell.row);
					        if (data.expireDate){
					        	var exp = new Date(data.expireDate);
					        	if (exp < new Date(value)){
						        	return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeExpireDate}'};
						        }
					        }
					        return true;
						 },
					},
					{ text: '${uiLabelMap.ExpireDate}', dataField: 'expireDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 150, editable: true, sortable: false, filterable: false,
						cellsrenderer: function (row, column, value){
							if (value){
								return '<span style=\"text-align: right; background-color: #deedf5; height: 100%;\">' + ReceiveReturnObj.getFormattedDate(new Date(value)) + '</span>';
							} else {
								if (listReturnItemSelected.length > 0){
									var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
									for (var i = 0; i < listReturnItemSelected.length; i ++){
										if (listReturnItemSelected[i].returnItemSeqId == data.returnItemSeqId){
											value = listReturnItemSelected[i].expireDate;
											break;
										}
									}
									if (value != null && value != undefined && value != ''){
										return '<span style=\"text-align: right; background-color: #deedf5; height: 100%;\">' + ReceiveReturnObj.getFormattedDate(new Date(value)) + '</span>';
									} else {
										return '<span style=\"text-align: right; background-color: #deedf5; height: 100%;\">_NA_</span>';
									}
								} else {
									return '<span style=\"text-align: right; background-color: #deedf5; height: 100%;\">_NA_</span>';
								}
							}
						},
						initeditor: function (row, column, editor) {
							editor.jqxDateTimeInput({ height: '25px', formatString: 'dd/MM/yyyy' });
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							if (data.expireDate){
								editor.jqxDateTimeInput('setDate', new Date(data.expireDate));
							} else {
								if (listReturnItemSelected.length > 0){
									for (var i = 0; i < listReturnItemSelected.length; i ++){
										if (listReturnItemSelected[i].returnItemSeqId == data.returnItemSeqId){
											if (listReturnItemSelected[i].expireDate != null && listReturnItemSelected[i].expireDate != undefined){
												editor.jqxDateTimeInput('setDate', new Date(listReturnItemSelected[i].expireDate));
												break;
											}
										}
									}
								}
							}
					 	},
					 	validation: function (cell, value) {
					        var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', cell.row);
					        if (data.datetimeManufactured){
					        	var mft = new Date(data.datetimeManufactured);
						        if (mft > new Date(value)){
						        	return { result: false, message: '${uiLabelMap.ExpireDateMustBeBeforeManufactureDate}'};
						        }
					        }
					        return true;
						 }
					},
					{ text: '${uiLabelMap.QuantityReturned}', dataField: 'returnQuantity', columntype: 'numberinput', width: 150, editable: true, filterable: false,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							var descriptionUom = data.quantityUomId;
							for(var i = 0; i < quantityUomData.length; i++){
								if(data.quantityUomId == quantityUomData[i].uomId){
									descriptionUom = quantityUomData[i].description;
							 	}
							}
							return '<span style=\"text-align: right; background-color: #deedf5;\">' + value.toLocaleString(locale) +' (' + descriptionUom +  ')</span>';
						},
						initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
					        editor.jqxNumberInput({ decimalDigits: 0});
					        var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
					        if (data.returnQuantity){
					        	editor.jqxNumberInput('val', data.returnQuantity);
					        }
					    },
					    validation: function (cell, value) {
					        if (value < 0){
					        	return { result: false, message: '${uiLabelMap.ExportValueMustBeGreaterThanZero}'};
					        }
					        return true;
						 },
					},
					{ text: '${uiLabelMap.Batch}', dataField: 'lotId', width: 120, editable: true, filterable: false,
						 cellsrenderer: function(row, column, value){
							 if (value === null || value === undefined || '' === value){
								 return '<span style=\"text-align: right; background-color: #deedf5; height: 100%;\">_NA_</span>';
							 } else {
								 return '<span style=\"text-align: right; background-color: #deedf5; height: 100%;\">'+value+'</span>';
							 }
						 },
						 validation: function (cell, value) {
	                    	 if (!value) {
	                    		 return { result: false, message: '${uiLabelMap.DmsFieldRequired}'};
	                    	 }
	                    	 if(value && !(/^[a-zA-Z0-9_-]+$/.test(value))){
	                    		 return { result: false, message: '${uiLabelMap.ThisFieldMustNotByContainSpecialCharacter}'};
	                    	 }
	                    	 return true;
						 },
						 cellbeginedit: function (row, datafield, columntype) {
						 }, 
					},
					{ text: '${uiLabelMap.UnitPrice}', dataField: 'returnPrice', columntype: 'numberinput', width: 150, editable: true, filterable: false, 
						cellsrenderer: function (row, column, value){
							if(value){
								return '<span style=\"text-align: right; background-color: #deedf5;\">' + formatcurrency(value, '${returnHeader.currencyUomId?if_exists}') + '<span>';
							} else {
								 return '<span style=\"text-align: right; background-color: #deedf5; height: 100%;\">_NA_</span>';
							}
						},
						initeditor: function (row, cellvalue, editor, celltext, pressedChar) {
					        editor.jqxNumberInput({ decimalDigits: 0});
					        var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
					        if (data.returnPrice){
					        	editor.jqxNumberInput('val', data.returnPrice);
					        }
					    },
					},
					{ text: '${uiLabelMap.Reason}', dataField: 'returnReasonId', width: 200, editable: true, columntype: 'dropdownlist', filterable: false,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							if (value === undefined || value === null || '' === value){
								var id = data.uid;
								$('#jqxgridProductReturn').jqxGrid('setcellvaluebyid', id, 'returnReasonId', listReturnReason[0].returnReasonId);
							} else {
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								for (var i = 0; i < listReturnReason.length; i ++){
									if (listReturnReason[i].returnReasonId == value){
										return '<span style=\"text-align: left\" class=\"focus-color\">' + listReturnReason[i].description + '</span>';
									}
								}
							}
						},
						initeditor: function(row, value, editor){
							editor.jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', autoDropDownHeight: false, dropDownHeight: '300px', source: listReturnReason, selectedIndex: 0, dropDownWidth: '200px', popupZIndex: 755, displayMember: 'description', valueMember: 'returnReasonId',});
						},
						createeditor: function (row, column, editor) {
							editor.jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', autoDropDownHeight: false, dropDownHeight: '300px', source: listReturnReason, selectedIndex: 0, dropDownWidth: '200px', popupZIndex: 755, displayMember: 'description', valueMember: 'returnReasonId',});
						},
					},
					{ text: '${uiLabelMap.ProductStatus}', dataField: 'inventoryStatusId', width: 150, editable: true, columntype: 'dropdownlist', filterable: false,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							if (value === undefined || value === null || '' === value){
								if (listReturnItemSelected.length > 0){
									var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
									for (var i = 0; i < listReturnItemSelected.length; i ++){
										if (listReturnItemSelected[i].returnItemSeqId == data.returnItemSeqId){
											value = listReturnItemSelected[i].inventoryStatusId;
											break;
										}
									}
									if (value != null && value != undefined && value != ''){
										for (var i = 0; i < invStatusData.length; i ++){
											if (invStatusData[i].inventoryStatusId == value){
												return '<span style=\"text-align: left\" class=\"focus-color\">' + invStatusData[i].description + '</span>';
											}
										}
									} else {
										return '<span style=\"text-align: left\" class=\"focus-color\">${uiLabelMap.InventoryGood}</span>';
									}
								} else {
									return '<span style=\"text-align: left\" class=\"focus-color\">${uiLabelMap.InventoryGood}</span>';
								}
							} else {
								var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
								for (var i = 0; i < invStatusData.length; i ++){
									if (invStatusData[i].inventoryStatusId == value){
										return '<span style=\"text-align: left\" class=\"focus-color\">' + invStatusData[i].description + '</span>';
									}
								}
							}
						},
						initeditor: function (row, column, editor) {
							editor.jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', autoDropDownHeight: false, dropDownHeight: '100px', source: invStatusData, selectedIndex: 0, dropDownWidth: '150px', popupZIndex: 755, displayMember: 'description', valueMember: 'inventoryStatusId',});
							editor.jqxDropDownList('val', 'Good');
						},
					},
					{ text: '${uiLabelMap.CommonStatus}', dataField: 'statusId', editable:true, width: 150, hidden: true,
						cellsrenderer: function (row, column, value){
							if(value){
								if ('CUSTOMER_RETURN' == returnHeaderTypeId){
									for (var i = 0; i < statusSOData.length; i ++){
										if (value == statusSOData[i].statusId){
											return '<span>' + statusSOData[i].description + '<span>';
										}
									}
								} else {
									for (var i = 0; i < statusPOData.length; i ++){
										if (value == statusPOData[i].statusId){
											return '<span>' + statusPOData[i].description + '<span>';
										}
									}
								}
							} else {
								return '<span><span>';
							}
						}
					},
					{ text: '${uiLabelMap.ActualExportedQuantity}', dataField: 'alternativeQuantity', width: 150, editable:false,
						cellsrenderer: function (row, column, value){
							var data = $('#jqxgridProductReturn').jqxGrid('getrowdata', row);
							var descriptionUom = data.quantityUomId;
							for(var i = 0; i < quantityUomData.length; i++){
								if(data.quantityUomId == quantityUomData[i].uomId){
									descriptionUom = quantityUomData[i].description;
							 	}
							}
							if (value){
								return '<span style=\"text-align: right\">' + value.toLocaleString(locale) +' (' + descriptionUom +  ')</span>';
							} else {
								return '<span style=\"text-align: right\">' + data.returnQuantity.toLocaleString(locale) +' (' + descriptionUom +  ')</span>';
							}
						},
					},
					{ text: '${uiLabelMap.ReturnType}', dataField: 'returnTypeId', editable:false, width: 200, 
						cellsrenderer: function (row, column, value){
							if(value){
								return '<span>' + mapReturnType[value] + '<span>';
							}
						}
					}"/>
<#assign dataField="[{ name: 'orderId', type: 'string' },
					{ name: 'productId', type: 'string' },
					{ name: 'productCode', type: 'string' },
					{ name: 'productName', type: 'string' },
					{ name: 'description', type: 'string' },
					{ name: 'quantityUomId', type: 'string' },
					{ name: 'returnQuantity', type: 'number' },
					{ name: 'inventoryStatusId', type: 'string' },
					{ name: 'returnId', type: 'string' },
					{ name: 'returnItemSeqId', type: 'string' },
					{ name: 'returnPrice', type: 'number' },
					{ name: 'alternativeQuantity', type: 'number' },
					{ name: 'returnReasonId', type: 'string' },
					{ name: 'statusId', type: 'string' },
					{ name: 'lotId', type: 'string' },
					{ name: 'orderItemSeqId', type: 'string' },
					{ name: 'returnTypeId', type: 'string' },
					{ name: 'expireDate', type: 'date', other: 'Timestamp' },
					{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp' },
					{ name: 'datetimeReceived', type: 'date', other: 'Timestamp' }]"/>
<@jqGrid filtersimplemode="true" id="jqxgridProductReturn" addrefresh="true" usecurrencyfunction="true" dataField=dataField columnlist=columnlist clearfilteringbutton="true" filterable="true"  editable="true" 
	url="jqxGeneralServicer?sname=JQGetListReturnDetail&returnId=${parameters.returnId?if_exists}" selectionmode='multiplecellsadvanced' editmode='click' viewSize="10" 
	customTitleProperties="ListProductReturnItem" jqGridMinimumLibEnable="true" />
<div class="row-fluid wizard-actions margin-top10">
	<button class="btn btn-small btn-primary btn-next" id="receiveProduct" data-last="${uiLabelMap.LogFinish}">
		<i class="fa-download"></i>
		${uiLabelMap.ProductReceiveProduct}
	</button>
</div>