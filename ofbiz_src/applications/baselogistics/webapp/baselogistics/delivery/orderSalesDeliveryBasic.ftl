<#include "script/salesDeliveryBasicScript.ftl"/>
<div id="deliverydis-tab" class="tab-pane<#if activeTab?exists && activeTab == "deliverydis-tab"> active</#if>">
<#assign initrowdetails = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	var sourceGridDetail =
    {
        localdata: datarecord.rowDetail,
        datatype: 'local',
        datafields:
	        [{ name: 'deliveryId', type: 'string' },
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
			{ name: 'actualExpireDate', type: 'date', other: 'Timestamp'},
			{ name: 'actualManufacturedDate', type: 'date', other: 'Timestamp'},
			{ name: 'expireDate', type: 'date', other: 'Timestamp'},
         	{ name: 'deliveryStatusId', type: 'string'},
			]
    };
    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
    grid.jqxGrid({
        width: '98%',
        height: 210,
        theme: 'olbius',
        localization: getLocalization(),
        source: dataAdapterGridDetail,
        sortable: true,
        pagesize: 5,
 		pageable: true,
 		columnsresize: true,
        selectionmode: 'singlerow',
        columns: [{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.ProductCode}', dataField: 'productCode', width: 120, editable: true, columntype: 'dropdownlist', pinned: true,
						cellsrenderer: function(row, column, value){
						 }
					},
					{ text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 200, editable: false,},
//					{ text: '${uiLabelMap.Quantity}', dataField: 'quantity', cellsalign: 'right', width: 120, editable: false,},
//					{ text: '${uiLabelMap.ExpireDate}', dataField: 'actualExpireDate', width: 120, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
//					},
					{ text: '${uiLabelMap.ActualDeliveryQuantitySum}', dataField: 'actualExportedQuantity', columntype: 'numberinput', width: 130, cellsalign: 'right', editable: true, sortable: false,},
					{ text: '${uiLabelMap.ActualDeliveredQuantitySum}', columntype: 'numberinput',  cellsalign: 'right', dataField: 'actualDeliveredQuantity', width: 130, editable: true,},
					{ text: '${uiLabelMap.IsPromo}', dataField: 'isPromo', width: 120, editable: true, columntype: 'dropdownlist',
						 cellsrenderer: function(row, column, value){
							 if ('Y' == value){
								 return '<span title=\"' + value + '\">${uiLabelMap.Yes}</span>';
							 } else {
								 return '<span title=\"' + value + '\">${uiLabelMap.No}</span>';
							 }
						 },
					},
				]
        });
 }"/>

<#assign columnlist="
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},		
">
<#if isOwnerDistributor == true>
			<#assign columnlist= columnlist + " { text: '${uiLabelMap.DeliveryDocId}', pinned: true, dataField: 'deliveryId', width: 145, editable:false, 
				cellsrenderer: function(row, column, value){
					return '<span><a href=\"javascript:SalesDlvObj.showDetailPopup(&#39;'+value+'&#39;);\"> ' + value  + '</a> - <a href=\"deliveryAndExport.pdf?deliveryId='+value+'\"> [${uiLabelMap.PDF}]</a></span>';
				 }
				},">
<#else>
		<#assign columnlist= columnlist + " { text: '${uiLabelMap.DeliveryDocId}', pinned: true, dataField: 'deliveryId', width: 145, editable:false, 
			cellsrenderer: function(row, column, value){
				return '<span>' + value  + ' - <a href=\"deliveryAndExport.pdf?deliveryId='+value+'\"> [${uiLabelMap.PDF}]</a></span>';
			 }
			},">
</#if>

	<#assign columnlist= columnlist + " { text: '${uiLabelMap.ExportFromFacility}', dataField: 'originFacilityName', minwidth: 150, editable:false,
					 cellsrenderer: function(row, column, value){
						 return '<span title=\"' + value + '\">' + value + '</span>';
					 },
				 },
				 { text: '${uiLabelMap.Status}', dataField: 'statusId', width: 145, editable:false, filtertype: 'checkedlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < statusData.length; i++){
								if(statusData[i].statusId == value){
									return '<span title=' + value + '>' + statusData[i].description + '</span>'
								}
							}
						},
						createfilterwidget: function (column, columnElement, widget) {
							var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
								autoBind: true
							});
							var records = filterDataAdapter.records;
							widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value){
						        	if (statusData.length > 0) {
										for(var i = 0; i < statusData.length; i++){
											if(statusData[i].statusId == value){
												return '<span>' + statusData[i].description + '</span>';
											}
										}
									}
									return value;
								}
							});
							widget.jqxDropDownList('checkAll');
			   			},
					 },
				 { text: '${uiLabelMap.DestAddress}', dataField: 'destAddress', minwidth: 150, editable:false,
					 cellsrenderer: function(row, column, value){
						return '<span title=\"' + value + '\">' + value + '</span>'
					 }, 
				 },
				 { text: '${uiLabelMap.RequireDeliveryDate}', dataField: 'deliveryDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				 },
				{ text: '${uiLabelMap.EstimatedExportDate}', dataField: 'estimatedStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				},
				{ text: '${uiLabelMap.EstimatedDeliveryDate}', dataField: 'estimatedArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				},
				{ text: '${uiLabelMap.ActualExportedDate}', dataField: 'actualStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				},
				{ text: '${uiLabelMap.ActualDeliveredDate}', dataField: 'actualArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 }, 
				},
				{ text: '${uiLabelMap.DeliveryBatchNumber}', dataField: 'shipmentId', width: 120, editable:false,
					cellsrenderer: function(row, column, value){
						return '<span title=\"' + value + '\">' + value + '</span>'
					 },
				 },
				 { text: '${uiLabelMap.CreatedDate}', dataField: 'createDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\" title=\"_NA_\">_NA_</span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 }, 	 
				 },
				 "/>
<#assign dataField="[{ name: 'deliveryId', type: 'string' },
				{ name: 'deliveryTypeId', type: 'string' },
				{ name: 'statusId', type: 'string' },
             	{ name: 'partyIdTo', type: 'string' },
             	{ name: 'destContactMechId', type: 'string' },
             	{ name: 'partyIdFrom', type: 'string' },
				{ name: 'originContactMechId', type: 'string' },
				{ name: 'orderId', type: 'string' },
             	{ name: 'originProductStoreId', type: 'string' },
             	{ name: 'originFacilityId', type: 'string' },
             	{ name: 'destFacilityId', type: 'string' },
             	{ name: 'destFacilityName', type: 'string' },
             	{ name: 'originFacilityName', type: 'string' },
				{ name: 'createDate', type: 'date', other: 'Timestamp' },
				{ name: 'deliveryDate', type: 'date', other: 'Timestamp' },
				{ name: 'estimatedStartDate', type: 'date', other: 'Timestamp' },
				{ name: 'estimatedArrivalDate', type: 'date', other: 'Timestamp' },
				{ name: 'actualStartDate', type: 'date', other: 'Timestamp' },
				{ name: 'actualArrivalDate', type: 'date', other: 'Timestamp' },
				{ name: 'totalAmount', type: 'number' },
				{ name: 'no', type: 'string' },
				{ name: 'shipmentId', type: 'string' },
				{ name: 'destAddress', type: 'string' },
				{ name: 'originAddress', type: 'string' },
				{ name: 'defaultWeightUomId', type: 'string' },
				{ name: 'rowDetail', type: 'string'},
	 		 	]"/>
<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
	 url="jqxGeneralServicer?sname=getListDelivery&fromOrderId=${parameters.orderId?if_exists}&deliveryTypeId=DELIVERY_SALES" rowdetailsheight="230"
	 customTitleProperties="ListDeliveryDoc" jqGridMinimumLibEnable="true" initrowdetails="true" initrowdetailsDetail=initrowdetails 
 />
</div>
<div id="popupDeliveryDetailWindow" class="hide popup-bound">
<div id="titleDetailId">${uiLabelMap.DeliveryDoc}</div>
<div class='form-window-container'>
	<div class='form-window-content'>
    	<div id="popupContainerNotify" style="width: 100%; overflow: auto;"></div>
		<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
			${uiLabelMap.DetailInfo}
			<a style="float:right;font-size:14px; cursor: pointer;" id="printPDF" target="_blank" data-rel="tooltip" title="${uiLabelMap.ExportPdf}" data-placement="bottom" data-original-title="${uiLabelMap.DeliveryNote}"><i class="fa-file-pdf-o"></i>&nbsp;${uiLabelMap.PDF}&nbsp;</a>
			<div style="float:right;font-size:14px;margin-right:15px" id="orderNote"></div>
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
					<div class="span5" style="text-align: right;">${uiLabelMap.Receiver}:</div>
	    		    <div class="span7"><div id="partyIdToDT" class="green-label"></div></div>
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
					<div class="span5" style="text-align: right;">${uiLabelMap.ExportFromFacility}:</div>
				    <div class="span7"><div id="originFacilityIdDT" class="green-label"></div></div>
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
		<div class="row-fluid margin-top10">
			<div style="margin-left: 20px"><div id="jqxgridDlvItem"></div></div>
		</div>
		<div class="form-action popup-footer">
            <button id="alterCancel2" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
            <button id="alterSave2" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
        </div>
	</div>
</div>
</div>

<div id="noteWindow" class="hide popup-bound">
<div>${uiLabelMap.DeliveryNoting}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
				${uiLabelMap.ReceiveReturn} 
			</h4>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.ReturnToFacility}</div></div>
						<div class="span7"><div id="facilityReturnId" class="green-label"></div></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;"><div class="asterisk">${uiLabelMap.ReceivedDate}</div></div>
					    <div class="span7"><div id="datetimeReceived" class="green-label"></div></div>
					</div>
				</div>
			</div>
			<div class="row-fluid margin-top20">
				<h4 class="row header smaller lighter blue" style="margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
					${uiLabelMap.ListProduct}
				</h4>
				<div style="margin-left: 20px"><div id="noteGrid"></div></div>
			</div>
		</div>
	<div class="form-action popup-footer">
	    <button id="noteCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
		<button id="noteSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
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
        height: 330,
        enabletooltips: true,
        autoheight: false,
        pageable: true,
        pagesize: 10,
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
					{ text: '${uiLabelMap.InventoryItemId}', hidden: true, datafield: 'inventoryItemId',
					},
					{ text: '${uiLabelMap.ProductCode}', dataField: 'productCode', width: 150, editable: true, columntype: 'dropdownlist', pinned: true,
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
					     		    	SalesDlvObj.updateRowData(item.value);
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
					{ text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 200, editable: false,
						cellsrenderer: function(row, column, value){
							var data = $('#jqxgridDlvItem').jqxGrid('getrowdata', row);
							if (!data.productCode){
								return '<span style=\"text-align: right\">...</span>';
							}
						}
					},
					{ text: '${uiLabelMap.Quantity}', dataField: 'quantity', cellsalign: 'right', width: 150, editable: false,
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
					{ text: '${uiLabelMap.ActualDeliveryQuantitySum}', dataField: 'actualExportedQuantity', columntype: 'numberinput', width: 150, cellsalign: 'right', editable: true, sortable: false,
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
					  { text: '${uiLabelMap.ActualDeliveredQuantitySum}', columntype: 'numberinput',  cellsalign: 'right', dataField: 'actualDeliveredQuantity', width: 150, editable: true,
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
					{ text: '${uiLabelMap.IsPromo}', dataField: 'isPromo', width: 150, editable: false, columntype: 'dropdownlist', 
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
				 	},																																																																																																																																																																																																																																																											
                 ]
	    });
	}

function loadNoteGrid(valueDataSoure){
	var sourceOrderItem =
	    {
        datafields:[{ name: 'productId', type: 'string' },
                    { name: 'productCode', type: 'string' },
                    { name: 'productName', type: 'string' },
                    { name: 'quantityUomId', type: 'string' },
                    { name: 'inventoryItemId', type: 'string' },
                    { name: 'deliveryId', type: 'string' },
                    { name: 'deliveryItemSeqId', type: 'string' },
                    { name: 'quantity', type: 'number' },
                 	{ name: 'note', type: 'string' },
                 	{ name: 'inventoryItemStatusId', type: 'string' },
                 	{ name: 'returnReasonId', type: 'string' },
                 	{ name: 'expiredDate', type: 'date', other: 'Timestamp' },
                 	{ name: 'manufacturedDate', type: 'date', other: 'Timestamp' },
                 	{ name: 'actualExpireDate', type: 'date', other: 'Timestamp' },
                 	{ name: 'actualManufacturedDate', type: 'date', other: 'Timestamp' },
                 	{ name: 'actualExportedQuantity', type: 'number' },
                 	{ name: 'actualDeliveredQuantity', type: 'number' },
                 	{ name: 'batch', type: 'string'},
		 		 	],
        localdata: valueDataSoure,
        datatype: "array",
    };
    var dataAdapterOrderItem = new $.jqx.dataAdapter(sourceOrderItem);
    $("#noteGrid").jqxGrid({
    source: dataAdapterOrderItem,
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
    localization: getLocalization(),
    columns: [	
			{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
			    groupable: false, draggable: false, resizable: false,
			    datafield: '', columntype: 'number', width: 50,
			    cellsrenderer: function (row, column, value) {
			        return '<span style=margin:4px;>' + (value + 1) + '</span>';
			    }
			},
			{ text: '${uiLabelMap.ProductId}', dataField: 'productCode', width: 120, editable: false, pinned: true},
			{ text: '${uiLabelMap.Quantity}', dataField: 'quantity', width: 120, editable: false, 
				cellsrenderer: function(row, column, value){
			 		return '<span style=\"text-align: right\">' + value.toLocaleString('${localeStr}') + '</span>';
				}
			},
			{ text: '${uiLabelMap.Unit}', dataField: 'quantityUomId', width: 120, editable: false,
				cellsrenderer: function(row, column, value){
					for(var i = 0; i < quantityUomData.length; i++){
						if(value == quantityUomData[i].uomId){
							return '<span style=\"text-align: right\">'+ quantityUomData[i].description + '</span>';
					 	}
					}
				 }
			},
			{ text: '${uiLabelMap.ManufactureDate}', dataField: 'manufacturedDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 150, editable: true, sortable: false,
				cellsrenderer: function (row, column, value){
					if (value){
						return '<span style=\"text-align: right;\" class=\"focus-color\">' + getFormattedDate(new Date(value)) + '</span>';
					} else {
						var data = $('#noteGrid').jqxGrid('getrowdata', row);
						if (data.actualManufacturedDate){
							var id = data.uid;
					 		$('#noteGrid').jqxGrid('setcellvaluebyid', id, 'manufacturedDate', data.actualManufacturedDate);
							return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\">' + getFormattedDate(new Date(data.actualManufacturedDate)) + '</span>';
						}
						return '<span style=\"text-align: right;\" class=\"focus-color\">_NA_</span>';
					}
				},
				createeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = $('#noteGrid').jqxGrid('getrowdata', row);
					if (data.actualManufacturedDate){
						editor.jqxDateTimeInput('setDate', new Date(data.actualManufacturedDate));
					}	
			 	},
			 	initeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = $('#noteGrid').jqxGrid('getrowdata', row);
					if (data.actualManufacturedDate){
						editor.jqxDateTimeInput('setDate', new Date(data.actualManufacturedDate));
					}
			 	},
			 	validation: function (cell, value) {
			 		var now = new Date();
			        if (value > now) {
			            return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeNow}'};
			        }
			        var data = $('#noteGrid').jqxGrid('getrowdata', cell.row);
			        if (data.expiredDate){
			        	var exp = new Date(data.expiredDate);
			        	if (exp < new Date(value)){
				        	return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeExpireDate}'};
				        }
			        }
			        return true;
				 },
			},
			{ text: '${uiLabelMap.ExpireDate}', dataField: 'expiredDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 150, editable: true, sortable: false,
				cellsrenderer: function (row, column, value){
					if (value){
						return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\">' + getFormattedDate(new Date(value)) + '</span>';
					} else {
						var data = $('#noteGrid').jqxGrid('getrowdata', row);
						if (data.actualExpireDate){
							var id = data.uid;
					 		$('#noteGrid').jqxGrid('setcellvaluebyid', id, 'expiredDate', data.actualExpireDate);
							return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\">' + getFormattedDate(new Date(data.actualExpireDate)) + '</span>';
						}
						return '<span style=\"text-align: right; background-color: #e6ffb8; height: 100%;\">_NA_</span>';
					}
				},
				createeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = $('#noteGrid').jqxGrid('getrowdata', row);
					if (data.actualExpireDate){
						editor.jqxDateTimeInput('setDate', new Date(data.actualExpireDate));
					}
			 	},
			 	initeditor: function (row, column, editor) {
					editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
					var data = $('#noteGrid').jqxGrid('getrowdata', row);
					if (data.actualExpireDate){
						editor.jqxDateTimeInput('setDate', new Date(data.actualExpireDate));
					}
			 	},
			 	validation: function (cell, value) {
			        var data = $('#noteGrid').jqxGrid('getrowdata', cell.row);
			        if (data.manufacturedDate){
			        	var mft = new Date(data.manufacturedDate);
				        if (mft > new Date(value)){
				        	return { result: false, message: '${uiLabelMap.ExpireDateMustBeBeforeManufactureDate}'};
				        }
			        }
			        return true;
				 },
			},
			{ text: '${uiLabelMap.Reason}', dataField: 'returnReasonId', minwidth: 300, editable: true, columntype: 'dropdownlist',
				cellsrenderer: function(row, column, value){
					var data = $('#noteGrid').jqxGrid('getrowdata', row);
					var id = data.uid;
					if (!value && returnReasonData.length > 0){
						$('#noteGrid').jqxGrid('setcellvaluebyid', id, 'returnReasonId', returnReasonData[0].returnReasonId);
						value = returnReasonData[0].returnReasonId;
					}
					if (!value){
						return '<span style=\"text-align: left\" class=\"focus-color\">${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}</span>';
					} else {
						var data = $('#noteGrid').jqxGrid('getrowdata', row);
						for (var i = 0; i < returnReasonData.length; i ++){
							if (returnReasonData[i].returnReasonId == value){
								return '<span style=\"text-align: left\" class=\"focus-color\">' + returnReasonData[i].description + '</span>';
							}
						}
					}
				},
				initeditor: function(row, value, editor){
					editor.jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', autoDropDownHeight: false, dropDownHeight: '200px', source: returnReasonData, selectedIndex: 0, dropDownWidth: '300px', popupZIndex: 755, displayMember: 'description', valueMember: 'returnReasonId',});
				}
			},
			{ text: '${uiLabelMap.ProductStatus}', dataField: 'inventoryItemStatusId', width: 150, editable: true, columntype: 'dropdownlist',
				cellsrenderer: function(row, column, value){
					var data = $('#noteGrid').jqxGrid('getrowdata', row);
					var id = data.uid;
					if (!value){
						$('#noteGrid').jqxGrid('setcellvaluebyid', id, 'inventoryItemStatusId', 'Good');
						value = 'Good';
					}
					for (var i = 0; i < invStatusData.length; i ++){
						if (invStatusData[i].statusId == value){
							return '<span style=\"text-align: left\" class=\"focus-color\">' + invStatusData[i].description + '</span>';
						}
					}
				},
				initeditor: function(row, value, editor){
					editor.jqxDropDownList({placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}', autoDropDownHeight: false, dropDownHeight: '200px', source: invStatusData, selectedIndex: 0, dropDownWidth: '150px', popupZIndex: 755, displayMember: 'description', valueMember: 'statusId',});
				}
			},
		]
    });
}
</script>