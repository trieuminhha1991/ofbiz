<div id='DeliveryMenu' style="display:none;">
	<ul>
	    <li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BSViewDetail)}</li>
	    <li><i class="fa fa-eye"></i>${StringUtil.wrapString(uiLabelMap.BLQuickView)}</li>
	    <li><i class="fa fa-file-pdf-o"></i>${StringUtil.wrapString(uiLabelMap.ReceiptNote)}</li>
	    <li><i class="fa fa-file-pdf-o"></i>${StringUtil.wrapString(uiLabelMap.BLReceiptNoteWithPrice)}</li>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
	</ul>
</div>
<div id="container" class="container-noti"></div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notification">
    </div>
</div>	
<div id="detailpopupWindow" class="hide popup-bound">
	<div id="titleDetailId">${uiLabelMap.ReceiveNote}</div>
	<div class='form-window-container'>
	<input type="hidden" id="currentDlvStatusId"></input>
		<div class='form-window-content'>
			<div id="popupContainerNotify" style="width: 100%; overflow: auto;"></div>
	    	<h4 class="row header smaller lighter blue" style="margin: 5px 10px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
	            ${uiLabelMap.GeneralInfo}
				<#if hasRoles == true>
		            <a style="float:right;font-size:14px;margin-left: 5px; cursor: pointer; cursor: hand;" id="cancelDlv" target="_blank" data-rel="tooltip" title="${uiLabelMap.CommonCancel}" data-placement="bottom" data-original-title="${uiLabelMap.CommonCancel}"><i class="fa icon-trash red"></i>${uiLabelMap.CommonCancel}</a>
		            <a href="#collapse" data-toggle="collapse" style="float: left; font-size: 12px; padding-right: 2px;" title="${uiLabelMap.Expand}"><i class="fa fa-expand" aria-hidden="true"></i></a>
		            <a style="float:right;font-size:14px; margin-top: 1px; margin-left: 5px; cursor: pointer; cursor: hand;" id="editDlv" target="_blank" data-rel="tooltip" title="${uiLabelMap.CommonEdit}" data-placement="bottom" data-original-title="${uiLabelMap.CommonEdit}"><i class="icon-edit"></i>${uiLabelMap.CommonEdit}</a>
		            <#if !hasOlbPermission("MODULE", "LOG_DELIVERY", "ADMIN")>
						<a style="float:right;font-size:14px;cursor: pointer;" id="sendRequestApprove" target="_blank" data-rel="tooltip" title="${uiLabelMap.SendRequestApprove}" data-placement="bottom" data-original-title="${uiLabelMap.DeliveryNote}"><i style="font-style: normal !important;" class="fa fa fa-paper-plane-o"></i>&nbsp;${uiLabelMap.CommonSend}&nbsp;</a>
					</#if>
		            <a style="float:right;font-size:14px;" id="printPDF" target="_blank" data-rel="tooltip" title="${uiLabelMap.ExportPdf}" data-placement="bottom" data-original-title="${uiLabelMap.ExportPdf}"><i class="fa fa-file-pdf-o"></i>${uiLabelMap.ReceiptNote}</a>
		            <a style="float:right;font-size:14px;margin-right:10px" id="printPDFWithPrice" target="_blank" data-rel="tooltip" title="${uiLabelMap.ExportPdf}" data-placement="bottom" data-original-title="${uiLabelMap.ExportPdf}"><i class="fa fa-file-pdf-o"></i>${uiLabelMap.BLReceiptNoteWithPrice}</a>
					<div style="float:right;font-size:14px;margin-right:10px" id="scanfile"></div>
				</#if>
	        </h4>
	        <div id="collapse" class="collapse">
	        	<div class="row-fluid">
					<div class="span4">
						<div class="row-fluid">	
							<div class="span4" style="text-align: right"> <div> ${uiLabelMap.ReceiveToFacility} </div> </div>
							<div class="span8">	<div id="destFacilityDT" style="width: 100%;" class="green-label"></div> </div>
						</div>
					</div>
		    		<div class="span4">
						<div class="row-fluid">	
							<div class="span5" style="text-align: right"> <div> ${uiLabelMap.Supplier} </div> </div>
							<div class="span7">	<div id="partyIdFromDT" style="width: 100%;" class="green-label"></div> </div>
						</div>
					</div>
		    		<div class="span4">
						<div class="row-fluid">	
				    		<div class="span5" style="text-align: right;">${uiLabelMap.EstimatedStartDelivery}</div>
			    		    <div class="span7"><div id="estimatedStartDateDT" class="green-label"></div></div>
		    		    </div>
	    		    </div>
	    		</div>
	    		<div class="row-fluid">
		    		<div class="span4">
						<div class="row-fluid">	
							<div class="span4" style="text-align: right"> <div> ${uiLabelMap.FacilityAddress} </div> </div>
							<div class="span8"> <div id="destContactMechIdDT" style="width: 100%;" class="green-label"></div> </div>
						</div>
		    		</div>
		    		<div class="span4">
						<div class="row-fluid">	
							<div class="span5" style="text-align: right"> <div> ${uiLabelMap.RequireDeliveryDate} </div> </div>
							<div class="span7"> <div id="deliveryDateDT" style="width: 100%;" class="green-label"></div> </div>
						</div>
		    		</div>
					<div class="span4 hide">
						<div class="row-fluid">	
							<div class="span5" style="text-align: right"> <div> ${uiLabelMap.ReceiveBatchNumber} </div> </div>
							<div class="span7"> <div id="noDT" style="width: 100%;" type="text" class="green-label"></div> </div>
						</div>
					</div>
					 <div class="span4">
		    		    <div class="row-fluid">	
				    		<div class="span5" style="text-align: right;">${uiLabelMap.EstimatedEndDelivery}</div>
			    		    <div class="span7"><div id="estimatedArrivalDateDT" class="green-label"></div></div>
						</div>
					</div>
	    		</div>
    		</div>
    		<div class="row-fluid">
    		    <div class="span4">
		    		<div class="row-fluid">	
						<div class="span4" style="text-align: right"><div> ${uiLabelMap.ReceiveNoteId} </div></div>
						<div class="span8"><div id="deliveryIdDT" style="width: 100%;" class="green-label"></div></div>
					</div>
				</div>
				<div class="span4">
				 	<div class="row-fluid">	
						<div class="span4" style="text-align: right"> <div> ${uiLabelMap.Status} </div> </div>
						<div class="span8"> <div id="statusIdDT" style="width: 100%;" class="green-label"></div> </div>
					</div>
					<div class="row-fluid hide">	
						<div class="span4" style="text-align: right"> <div> ${uiLabelMap.FormFieldTitle_invoiceId} </div> </div>
						<div class="span8"> 
							<input id="invoiceId" style="width: 100%;" type="text" class="green-label"></input>
							<div id="invoiceIdDT" style="width: 100%;" class="green-label"></div> 
						</div>
					</div>
				</div>
				<div class="span4">
	    		    <div class="row-fluid">	
						<div class="span5" id="actualStartLabel" style="text-align: right;" class="asterisk">${uiLabelMap.ActualStartDelivery}</div>
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
						<div class="span4" style="text-align: right"> <div> ${uiLabelMap.DAOrderId} </div> </div>
						<div class="span8"> <div id="orderIdDT" style="width: 100%;" class="green-label">${parameters.orderId?if_exists}</div></div>
					</div>
				</div>
				<div class="span4">
		    		<#-- <div class="row-fluid">	
						<div class="span4" style="text-align: right"> <div> ${uiLabelMap.BLReceiveToLocation} </div> </div>
						<div class="span8">
							<div id="locationProduct"  style="z-index: 99999999 !important">
			    				<div id="jqxgridLocationProduct"></div>
			    			</div> 
						</div>
					</div>
					 -->
				</div>
				<div class="span4">
					<div class="row-fluid margin-bottom10">	
						<div class="span5" id="actualArrivalLabel" style="text-align: right;" class="asterisk">${uiLabelMap.ActualEndDelivery}</div>
					    <div class="span7">
					    	<div id="actualArrivalDateDis" class="green-label"></div>
					    	<div id="actualArrivalDate" class="green-label"></div>
					    </div>
					</div>
	    		</div>
    		</div>
			<div class="row-fluid">
				<h4 class="row header smaller lighter blue" style="margin: 0px 10px 0px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
					${uiLabelMap.ListProduct}
					<a style="float:right;font-size:14px; margin-right: 5px" id="addRow" href="javascript:PODlvObj.addNewRow()" data-rel="tooltip" title="${uiLabelMap.AddRow}" data-placement="bottom"><i class="icon-plus-sign open-sans"></i></a>
					<a class="pointer pull-right" style="font-size:14px; margin-right: 7px" id="btnScanUPCA" data-rel="tooltip" title="${uiLabelMap.ReceiveProductByUPCACode}" data-placement="bottom"><i class="fa-barcode open-sans"></i></a>
					<input class="span4 pull-right hide" type="text" id="txtUPCACode" placeholder="${uiLabelMap.UPCACode}" autocomplete="off"/>
				</h4>
				<div style="margin-left: 20px; margin-right: 10px; margin-top: 5px"><div id="jqxgrid2"></div></div>
				<div style="margin-left: 20px; margin-right: 10px; margin-top: 5px"><div id="jqxgridConfirm"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="detailCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
    		<#if hasRoles == true>
	        	<button class="btn btn-small btn-success btn-next form-action-button pull-right" id="detailSave">
					${uiLabelMap.LogFinish}
					<i class="icon-arrow-right icon-on-right"></i>
				</button>
	        	<button class="btn btn-small btn-success btn-next form-action-button pull-right" id="btnNextWizard">
					${uiLabelMap.Confirm}
					<i class="icon-arrow-right icon-on-right"></i>
				</button>
	        	<button class="btn btn-small btn-prev form-action-button pull-right" id="btnPrevWizard">
					<i class="icon-arrow-left"></i>
					${uiLabelMap.LogPrev}
				</button>
		        <#if hasOlbPermission("MODULE", "LOG_DELIVERY", "ADMIN")>
		        	<button id="alterApproveAndContinue" class='btn btn-success form-action-button pull-right'>${uiLabelMap.ApproveAndContinue} <i class='icon-arrow-right'></i> </button>
		        	<button id="detailApprove" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Approve}</button>
		        </#if>
		        <button id="confirmAndContinue" class='btn btn-success form-action-button pull-right'>${uiLabelMap.ConfirmAndContinue} <i class='icon-arrow-right'></i> </button>
		        <button id="detailConfirm" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.Confirm}</button>
	        </#if>
		</div>
	</div>
</div>

<div id="editWindow" class="hide popup-bound">
	<div>${uiLabelMap.BLDeliveryEdit}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<a style="float:right;font-size:14px; margin-right: 5px" id="editAddRow" href="javascript:PODlvObj.editAddNewProduct()" data-rel="tooltip" title="${uiLabelMap.AddRow}" data-placement="bottom"><i class="icon-plus-sign open-sans"></i></a>
				<div><div id="editGrid"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="editCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
			<button id="editSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<div id="editAddProductWindow" class="hide popup-bound">
	<div>${uiLabelMap.BLAddProducts}</div>
	<div class="form-window-container">
		<div class="form-window-content">
			<div class="row-fluid">
				<div><div id="editAddProductGrid"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">
			<button id="editAddProductCancel" class="btn btn-danger form-action-button pull-right"><i class="fa-remove"></i> ${uiLabelMap.CommonClose}</button>
			<button id="editAddProductSave" class="btn btn-primary form-action-button pull-right"><i class="fa-check"></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>

<script>

function loadDeliveryItem(valueDataSoure){
		var sourceTmp = [];
		PODlvObj.renderHtmlContainGrids;
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
		                 	{ name: 'batch', type: 'string' },
		                 	{ name: 'isPromo', type: 'string' },
		                 	{ name: 'comment', type: 'string' },
		                 	{ name: 'actualExportedQuantity', type: 'number' },
		                 	{ name: 'actualDeliveredQuantity', type: 'number' },
		                 	{ name: 'actualDeliveredQuantityQC', type: 'number' },
		                 	{ name: 'actualDeliveredQuantityEA', type: 'number' },
		                 	{ name: 'actualExportedAmount', type: 'number' },
		                 	{ name: 'actualDeliveredAmount', type: 'number' },
		                 	{ name: 'statusId', type: 'string' },
		                 	{ name: 'amount', type: 'number' },
		                 	{ name: 'quantity', type: 'number' },
		                 	{ name: 'inventoryItemId', type: 'string' },
							{ name: 'actualExpireDate', type: 'date', other: 'Timestamp'},
							{ name: 'expireDate', type: 'date', other: 'Timestamp'},
							{ name: 'actualManufacturedDate', type: 'date', other: 'Timestamp'},
		                 	{ name: 'deliveryStatusId', type: 'string'},
							{ name: 'weight', type: 'number'},
							{ name: 'productWeight', type: 'number'},
							{ name: 'weightUomId', type: 'string'},
							{ name: 'expRequired', type: 'string'},
							{ name: 'mnfRequired', type: 'string'},
							{ name: 'lotRequired', type: 'string'},
							{ name: 'defaultWeightUomId', type: 'string'},
							{ name: 'UPCACode', type: 'string'},
							{ name: 'requireAmount', type: 'string'},
							{ name: 'quantityUomIds', type: 'string'},
							{ name: 'weightUomIds', type: 'string'},
							{ name: 'orderWeightUomId', type: 'string'},
							{ name: 'orderQuantityUomId', type: 'string'},
							<#--  TODO { name: 'locationCode', type: 'string'},  -->
							{ name: 'convertNumber', type: 'number'},
							<#-- TODO { name: 'location', value: 'locationCode', values: { source: locationAdapter.records, value: 'value', name: 'label' } },  -->
	     				],
		        localdata: valueDataSoure,
		        datatype: "array",
		    };
		    var dataAdapterProduct = new $.jqx.dataAdapter(sourceProduct);
		    $("#jqxgrid2").jqxGrid({
	        source: dataAdapterProduct,
	        filterable: true,
	        showfilterrow: true,
	        theme: 'olbius',
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
						{ text: '${uiLabelMap.UPCACode}', hidden: true, datafield: 'UPCACode',
						},
						{ text: '${uiLabelMap.ProductId}', dataField: 'productCode', width: 110, editable: true, pinned: true, columntype: 'dropdownlist',
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
								
								var sourceDataProduct =
								{
					               localdata: codeSourceData,
					               datatype: 'array'
								};
								var dataAdapterProduct = new $.jqx.dataAdapter(sourceDataProduct);
								editor.off('change');
								editor.jqxDropDownList({source: dataAdapterProduct, autoDropDownHeight: true, displayMember: 'productCode', valueMember: 'productCode', placeHolder: '${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)}',
									renderer: function(index, label, value) {
						                var item = editor.jqxDropDownList('getItem', index);
						                return '<span>'+item.originalItem.productCode+'</span>';
						            }
								});
								editor.on('change', function (event){
									var args = event.args;
						     	    if (args) {
					     	    		var item = args.item;
						     		    if (item){
						     		    	PODlvObj.updateRowData(item.value);
						     		    } 
						     	    }
						        });
							 },
							 cellbeginedit: function (row, datafield, columntype) {
								 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								 if (data.productId){
									 return false;
								 }
								 return true;
							 },
							 cellsrenderer: function(row, column, value){
								 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								 if (!data.productId){
									 return '<span> ${StringUtil.wrapString(uiLabelMap.PleaseSelectTitle)} </span>';
								 }
								 return '<span>'+value+'</span>';
							 }
						},
						{ text: '${uiLabelMap.ProductName}', dataField: 'productName', width: 160, editable: false,
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								if (!data.productId){
									return '<span style=\"text-align: right\"></span>';
								}
							}
						},
						{ text: '${uiLabelMap.BLPackingForm}', filterable: false, dataField: 'convertNumber', width: 90, editable: false,
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								if (!data.productId){
									return '<span style=\"text-align: right\"></span>';
								} else {
									return '<span style=\"text-align: right; height: 100%;\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
								}
							}
						},
						{ text: '${uiLabelMap.BLQuantityByQCUom}', filterable: false, columntype: 'numberinput',  cellsalign: 'right', dataField: 'actualDeliveredQuantityQC', width: 120, editable: true,
							cellbeginedit: function (row, datafield, columntype) {
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								var check = $.inArray(glOriginFacilityId, listFacilityManage);
								if(data.statusId != 'DELI_ITEM_EXPORTED'){
									tmpEditable = false;
									return false;
								} else{
									tmpEditable = true;
									return true;
		                        }
							 }, 
							 createeditor: function(row, value, editor){
	                            var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
	                        	if(data.statusId != 'DELI_ITEM_EXPORTED'){
	                        		editor.jqxNumberInput({disabled: true});
	                            }else{
	                                var requireAmount = data.requireAmount;
							        if (requireAmount && 'Y' == requireAmount) {
							        	editor.jqxNumberInput({ decimalDigits: 2});
							        } else {
							        	editor.jqxNumberInput({ decimalDigits: 0});
							        }
	                                if (value === null || value === undefined || (value == 0 && is == 0)){
	                                	if (data.actualExportedQuantityQC){
	                                		editor.jqxNumberInput('val', data.actualExportedQuantityQC);
	                                	}
	                                }
	                                if (is == 0){
	                                	is = is + 1;
	                                }
	                            }
		                     },
		                     validation: function (cell, value) {
		                    	 if (value < 0) {
		                    		 return { result: false, message: '${uiLabelMap.NumberGTZ}'};
		                    	 }
							        
		                    	 var dataTmp = $('#jqxgrid2').jqxGrid('getrowdata', cell.row);
		                    	 var requireAmount = dataTmp.requireAmount;
								 var prodIdTmp = dataTmp.productId;
								 var isPromo = dataTmp.isPromo;
								 var rows = $('#jqxgrid2').jqxGrid('getrows');
								 var listByPr = [];
								 for (var i = 0; i < rows.length; i ++){
									 if (prodIdTmp == rows[i].productId && rows[i].isPromo == isPromo){
										 listByPr.push(rows[i]);
									 } 
								 }
								 var allDlvQty = 0;
								 for (var i = 0; i < deliveryItemData.length; i ++){
									 if (deliveryItemData[i].productId == prodIdTmp && deliveryItemData[i].isPromo == isPromo){
									 	if (requireAmount == 'Y') {
									 		allDlvQty = allDlvQty + deliveryItemData[i].actualExportedAmount;
									 	} else {
									 		allDlvQty = allDlvQty + deliveryItemData[i].actualExportedQuantity;
									 	}
									 }
								 }
								 var curQty = 0;
								 for (var i = 0; i < listByPr.length; i ++){
									 if (listByPr[i].productId == prodIdTmp){
										 curQty = curQty + listByPr[i].actualDeliveredQuantity;
									 }
								 }
								 var convert = dataTmp.convertNumber;
								 var x = curQty + (value - Number(dataTmp.actualDeliveredQuantityQC))*convert;
								 var y = allDlvQty;
								 if (x > y){
									 return { result: false, message: '${uiLabelMap.QuantityGreateThanQuantityCreatedInDelivery} ' + x + ' > ' + y};
								 }
								 return true;
							 },
							 cellsrenderer: function (row, column, value){
							 	var tmp = null;
							 	var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
							 	var actualExported = data.actualExportedQuantity;
							 	if(data.statusId == 'DELI_ITEM_EXPORTED'){
							 		if (data.orderQuantityUomId == data.quantityUomId) {
							 			return '<span style=\"text-align: right; height: 100%;\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
								 	} else {
										return '<span class=\"focus-color\" style=\"text-align: right; height: 100%;\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'								 	
								 	}
						 		} else {
						 			if (value === null || value === undefined){
						 				if (data.productId){
						 					return '<span style=\"text-align: right;\"></span>';
						 				} else {
						 					return '<span style=\"text-align: right;\"></span>';
						 				}
						 			} else {
						 				if(data.statusId == 'DELI_ITEM_DELIVERED'){
						 					if (value > 0) {
						 						return '<span style=\"text-align: right;\">' + formatnumber(value) + '</span>';
						 					} else {
						 						return '<span style=\"text-align: right;\">' + value + '</span>';
						 					}
						 				} else {
							 				if(data.statusId == 'DELI_ITEM_APPROVED'){
							 					return '<span style=\"text-align: right;\">' + formatnumber(value) + '</span>';
							 				} else {
						 						return '<span style=\"text-align: right;\"></span>';
					 						}
						 				}
						 			}
							 	}
							 },
							 cellbeginedit: function (row, datafield, columntype) {
								 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								 if (data.orderQuantityUomId == data.quantityUomId) {
								 	return false;
								 }
								 if (data) {
									 if (data.statusId){
										 if ("DELI_ITEM_EXPORTED" == data.statusId){
											 return true;
										 }
									 }
								 }
								 return false;
							 },
							 cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
							 	var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
							 	var convert = data.convertNumber;
					        	if (data.actualDeliveredQuantity) {
					        		var totalEA = data.actualDeliveredQuantity - oldvalue*convert + newvalue*convert; 
						        	$('#jqxgrid2').jqxGrid('setcellvaluebyid', data.uid, 'actualDeliveredQuantity', totalEA);
					        	} else {
					        		var totalEA = newvalue*convert; 
						        	$('#jqxgrid2').jqxGrid('setcellvaluebyid', data.uid, 'actualDeliveredQuantity', totalEA);
					        	}
				    		}
						 },
						{ text: '${uiLabelMap.BLQuantityByEAUom}', filterable: false, columntype: 'numberinput',  cellsalign: 'right', dataField: 'actualDeliveredQuantityEA', width: 120, editable: true,
							cellbeginedit: function (row, datafield, columntype) {
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								var check = $.inArray(glOriginFacilityId, listFacilityManage);
								if(data.statusId != 'DELI_ITEM_EXPORTED'){
									tmpEditable = false;
									return false;
								} else{
									tmpEditable = true;
									return true;
		                        }
							 }, 
							 createeditor: function(row, value, editor){
	                            var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
	                        	if(data.statusId != 'DELI_ITEM_EXPORTED'){
	                        		editor.jqxNumberInput({disabled: true});
	                            }else{
	                                var requireAmount = data.requireAmount;
							        if (requireAmount && 'Y' == requireAmount) {
							        	editor.jqxNumberInput({ decimalDigits: 2});
							        } else {
							        	editor.jqxNumberInput({ decimalDigits: 0});
							        }
	                                if (value === null || value === undefined || (value == 0 && is == 0)){
	                                	if (data.actualExportedQuantity){
	                                		editor.jqxNumberInput('val', data.actualExportedQuantityEA);
	                                	}
	                                }
	                                if (is == 0){
	                                	is = is + 1;
	                                }
	                            }
		                     },
		                     validation: function (cell, value) {
		                    	 if (value < 0) {
		                    		 return { result: false, message: '${uiLabelMap.NumberGTZ}'};
		                    	 }
							        
		                    	 var dataTmp = $('#jqxgrid2').jqxGrid('getrowdata', cell.row);
		                    	 var requireAmount = dataTmp.requireAmount;
								 var prodIdTmp = dataTmp.productId;
								 var isPromo = dataTmp.isPromo;
								 var rows = $('#jqxgrid2').jqxGrid('getrows');
								 var listByPr = [];
								 for (var i = 0; i < rows.length; i ++){
									 if (prodIdTmp == rows[i].productId && rows[i].isPromo == isPromo){
										 listByPr.push(rows[i]);
									 } 
								 }
								 var allDlvQty = 0;
								 for (var i = 0; i < deliveryItemData.length; i ++){
									 if (deliveryItemData[i].productId == prodIdTmp && deliveryItemData[i].isPromo == isPromo){
									 	if (requireAmount == 'Y') {
									 		allDlvQty = allDlvQty + deliveryItemData[i].actualExportedAmount;
									 	} else {
									 		allDlvQty = allDlvQty + deliveryItemData[i].actualExportedQuantity;
									 	}
									 }
								 }
								 var curQty = 0;
								 for (var i = 0; i < listByPr.length; i ++){
									 if (listByPr[i].productId == prodIdTmp){
										 curQty = curQty + listByPr[i].actualDeliveredQuantity;
									 }
								 }
								 var x = curQty + value - Number(dataTmp.actualDeliveredQuantityEA);
								 var y = allDlvQty;
								 if (x > y){
									 return { result: false, message: '${uiLabelMap.QuantityGreateThanQuantityCreatedInDelivery} ' + x + ' > ' + y};
								 }
								 return true;
							 },
							 cellsrenderer: function (row, column, value){
							 	var tmp = null;
							 	var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
							 	var actualExported = data.actualExportedQuantity;
							 	if(data.statusId == 'DELI_ITEM_EXPORTED'){
							 		if (data.orderQuantityUomId != data.quantityUomId) {
							 			return '<span style=\"text-align: right; height: 100%;\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'
								 	} else {
										return '<span class=\"focus-color\" style=\"text-align: right; height: 100%;\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>'								 	
								 	}
						 		} else {
						 			if (value === null || value === undefined){
						 				if (data.productId){
						 					return '<span style=\"text-align: right;\"></span>';
						 				} else {
						 					return '<span style=\"text-align: right;\"></span>';
						 				}
						 			} else {
						 				if(data.statusId == 'DELI_ITEM_DELIVERED'){
						 					if (value > 0) {
						 						return '<span style=\"text-align: right;\">' + formatnumber(value) + '</span>';
						 					} else {
						 						return '<span style=\"text-align: right;\">' + value + '</span>';
						 					}
						 				} else {
						 					if(data.statusId == 'DELI_ITEM_APPROVED'){
							 					return '<span style=\"text-align: right;\">' + formatnumber(value) + '</span>';
							 				} else {
						 						return '<span style=\"text-align: right;\"></span>';
					 						}
						 				}
						 			}
							 	}
							 },
							 cellbeginedit: function (row, datafield, columntype) {
								 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								 if (data) {
						 			if (data.orderQuantityUomId != data.quantityUomId) {
									 	return false;
									 }
									 if (data.statusId){
										 if ("DELI_ITEM_EXPORTED" == data.statusId){
											 return true;
										 }
									 }
								 } 
								 return false;
							 },
							 cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
							 	var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
					        	if (data.actualDeliveredQuantity) {
					        		var totalEA = data.actualDeliveredQuantity - oldvalue + newvalue; 
						        	$('#jqxgrid2').jqxGrid('setcellvaluebyid', data.uid, 'actualDeliveredQuantity', totalEA);
					        	} else {
					        		$('#jqxgrid2').jqxGrid('setcellvaluebyid', data.uid, 'actualDeliveredQuantity', newvalue);
					        	}
				    		}
						 },
						 { text: '${uiLabelMap.BLQuantityEATotal}', filterable: false, columntype: 'numberinput',  cellsalign: 'right', dataField: 'actualDeliveredQuantity', width: 120, editable: false,
							 cellsrenderer: function (row, column, value){
							 	var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
							 	if (!data.productId){
				 					return '<span style=\"text-align: right;\"></span>';
				 				} else {
				 					return '<span class=\"align-right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>';
				 				}
						 		return '<span class=\"align-right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>';
						 	}
						 },
						 { text: '${uiLabelMap.ManufacturedDateSum}', filterable: false, dataField: 'actualManufacturedDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 120, editable: true, sortable: false,
							cellsrenderer: function (row, column, value){
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								var id = data.uid;
								if(data.statusId == 'DELI_ITEM_EXPORTED'){
									if (value){
										return '<span class=\"focus-color\" style=\"text-align: right; height: 100%;\">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
									} else {
										return '<span class=\"focus-color\" style=\"text-align: right; height: 100%;\"></span>';
									}
								} else {
									if (value){
										if (typeof data.actualManufacturedDate != 'number') data.actualExpireDate = Number(data.actualManufacturedDate);
										return '<span style=\"text-align: right\">' + DatetimeUtilObj.getFormattedDate(new Date(data.actualManufacturedDate)) + '</span>';
									} else {
										if (data.productId){
											return '<span style=\"text-align: right;\"></span>';
										} else {
											return '<span style=\"text-align: right;\"></span>';
										}
									}
								}
							},
							createeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
						    	if(data.statusId != 'DELI_ITEM_EXPORTED'){
						    		editor.jqxDateTimeInput({disabled: true});
						        }else{
						            editor.jqxDateTimeInput({disabled: false});
						        }
						 	},
						 	initeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy',showFooter: true});
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
						    	if(data.statusId != 'DELI_ITEM_EXPORTED'){
						    		editor.jqxDateTimeInput({disabled: true});
						        }else{
						            editor.jqxDateTimeInput({disabled: false});
						        }
						 	},
						 	validation: function (cell, value) {
						 		var now = new Date();
						 		if (value) {
							        if (value > now) {
							            return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeNow}'};
							        }
							        var data = $('#jqxgrid2').jqxGrid('getrowdata', cell.row);
							        if (data.actualExpireDate){
							        	var exp = new Date(data.actualExpireDate);
							        	if (exp < new Date(value)){
								        	return { result: false, message: '${uiLabelMap.ManufactureDateMustBeBeforeExpireDate}'};
								        }
							        }
						        } else {
						        	var data = $('#jqxgrid2').jqxGrid('getrowdata', cell.row);
						        	if (data.mnfRequired == "Y") {
						        		return { result: false, message: '${uiLabelMap.DmsFieldRequired}'};
						        	}
						        }
						        return true;
							 },
							 cellbeginedit: function (row, datafield, columntype) {
								 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								 if (data) {
									 if (data.statusId){
										 if ("DELI_ITEM_EXPORTED" == data.statusId){
											 return true;
										 }
									 }
								 }
								 return false;
							 },
						},
						{ text: '${uiLabelMap.ExpiredDateSum}', filterable: false, dataField: 'actualExpireDate', columntype: 'datetimeinput', cellsformat: 'dd/MM/yyyy', width: 120, editable: true, sortable: false,
							cellsrenderer: function (row, column, value){
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								var id = data.uid;
								if(data.statusId == 'DELI_ITEM_EXPORTED'){
									if (value){
										return '<span class=\"focus-color\" style=\"text-align: right; height: 100%;\">' + DatetimeUtilObj.getFormattedDate(new Date(value)) + '</span>';
									} else {
										if (data.expireDate){
											$('#jqxgrid2').jqxGrid('setcellvaluebyid', id, 'actualExpireDate', data.expireDate);
											return '<span class=\"focus-color\" style=\"text-align: right; height: 100%;\">' + DatetimeUtilObj.getFormattedDate(new Date(data.expireDate)) + '</span>';
										} else {
											return '<span class=\"focus-color\" style=\"text-align: right; height: 100%;\"></span>';
										}
									}
								} else {
									if (value){
										if (typeof data.actualExpireDate != 'number') data.actualExpireDate = Number(data.actualExpireDate);
										return '<span style=\"text-align: right\">' + DatetimeUtilObj.getFormattedDate(new Date(data.actualExpireDate)) + '</span>';
									} else {
										if (data.productId){
											return '<span style=\"text-align: right;\"></span>';
										} else {
											return '<span style=\"text-align: right;\"></span>';
										}
									}
								}
							},
							createeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy', showFooter: true});
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
						    	if(data.statusId != 'DELI_ITEM_EXPORTED'){
						    		editor.jqxDateTimeInput({disabled: true});
						        }else{
						            editor.jqxDateTimeInput({disabled: false});
						            if (data.expireDate){
						            	editor.jqxDateTimeInput('val', data.expireDate);
						            }
						        }
						 	},
						 	initeditor: function (row, column, editor) {
								editor.jqxDateTimeInput({ height: '25px',  formatString: 'dd/MM/yyyy' });
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
						    	if(data.statusId != 'DELI_ITEM_EXPORTED'){
						    		editor.jqxDateTimeInput({disabled: true});
						        }else{
						            editor.jqxDateTimeInput({disabled: false});
						            if (data.expireDate){
						            	editor.jqxDateTimeInput('val', data.expireDate);
						            }
						        }
						 	},
						 	validation: function (cell, value) {
						 		var now = new Date();
						 		if (value) {
							        if (value && value < now) {
							            return { result: false, message: '${uiLabelMap.ExpireDateMustBeAfterNow}'};
							        }
							        var data = $('#jqxgrid2').jqxGrid('getrowdata', cell.row);
							        if (data.actualManufacturedDate){
							        	var mft = new Date(data.actualManufacturedDate);
								        if (mft > new Date(value)){
								        	return { result: false, message: '${uiLabelMap.ExpireDateMustBeBeforeManufactureDate}'};
								        }
							        }
						        } else {
						        	var data = $('#jqxgrid2').jqxGrid('getrowdata', cell.row);
						        	if (data.expRequired == "Y") {
						        		return { result: false, message: '${uiLabelMap.DmsFieldRequired}'};
						        	}
						        }
						        return true;
							 },
							 cellbeginedit: function (row, datafield, columntype) {
								 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								 if (data) {
									 if (data.statusId){
										 if ("DELI_ITEM_EXPORTED" == data.statusId){
											 return true;
										 }
									 }
								 }
								 return false;
							 },
						},
						 { text: '${uiLabelMap.Batch}', filterable: false, dataField: 'batch', width: 110, editable: true,
							 cellsrenderer: function(row, column, value){
								 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								 if(data.statusId == 'DELI_ITEM_EXPORTED'){
								 	if (value === null || value === undefined || value === ''){
					 					return '<span class=\"focus-color\" style=\"text-align: right; height: 100%;\"></span>';
								 	} else {
								 		return '<span class=\"focus-color\" style=\"text-align: right; height: 100%;\" title=' + value + '>' + value + '</span>'
								 	}
						 		} else {	
					 				if (data.productId){
										 if (value){
											 return '<span title=' + value + ' style=\"text-align: right\">' + value + '</span>';
										 } else {
											 return '<span style=\"text-align: right\"></span>';
										 }
									 } else {
										 return '<span style=\"text-align: right\"></span>';
									 }
							 	}
							 },
							 validation: function (cell, value) {
		                    	 if (!value) {
	                    	 		var data = $('#jqxgrid2').jqxGrid('getrowdata', cell.row);
						        	if (data.lotRequired == "Y") {
						        		return { result: false, message: '${uiLabelMap.DmsFieldRequired}'};
						        	}
		                    	 }
		                    	 if(value && !(/^[a-zA-Z0-9_-]+$/.test(value))){
		                    		 return { result: false, message: '${uiLabelMap.ThisFieldMustNotByContainSpecialCharacter}'};
		                    	 }
		                    	 return true;
							 },
							 cellbeginedit: function (row, datafield, columntype) {
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								if(data.statusId != 'DELI_ITEM_EXPORTED'){
									return false;
								} else{
									return true;
		                        }
							 }, 
						},
					 	<#-- { text: '${uiLabelMap.BLLocationCode}', hidden:true,  filterable: false, dataField: 'locationCode', displayfield: 'location', width: 110, editable: true, columntype: 'combobox',
						 	 cellsrenderer: function(row, column, value){
								 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								 if(data.statusId == 'DELI_ITEM_EXPORTED'){
								 	if (value === null || value === undefined || value === ''){
					 					return '<span class=\"focus-color\" style=\"text-align: right; height: 100%;\"></span>';
								 	} else {
								 		return '<span class=\"focus-color\" style=\"text-align: right; height: 100%;\" title=' + value + '>' + value + '</span>'
								 	}
						 		} else {	
					 				if (data.productId){
										 if (value){
											 return '<span title=' + value + ' style=\"text-align: right\">' + value + '</span>';
										 } else {
											 return '<span style=\"text-align: right\"></span>';
										 }
									 } else {
										 return '<span style=\"text-align: right\"></span>';
									 }
							 	}
							 },
						 	createeditor: function (row, value, editor) {
	                            editor.jqxComboBox({ source: locationAdapter, displayMember: 'label', valueMember: 'value', autoDropDownHeight : false, height : '25px'});
	                            var id = $(editor[0]).attr("aria-owns");
	                           	document.getElementById(id).style.zIndex = 9999999;
                        	},
                        	validation: function (cell, value) {
		                    	 if (value) {
                    	 			for (var x in listLocationData) {
                    	 				if (listLocationData[x].locationCode == value.value) return true;
                    	 			}
                    	 			return { result: false, message: '${uiLabelMap.BLLocationCodeNotExisted}'};
		                    	 }
		                    	 return true;
							 },
						},
						 -->
						{ text: '${uiLabelMap.CreatedNumberSum}', filterable: false, dataField: 'quantity', cellsalign: 'right', width: 130, editable: false,
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								if (data.productId){
									if (data.requireAmount && data.requireAmount == 'Y') {
										return '<span style=\"text-align: right\">' + formatnumber(data.amount) +'</span>';
									}
									return '<span style=\"text-align: right\">' + formatnumber(value) +'</span>';
								} else {
									return '<span style=\"text-align: right\"></span>';
								}
							 }
						},
						{ text: '${uiLabelMap.BLUnitEA}', filterable: false, datafield: 'quantityUomId', align: 'left', width: 100, filtertype: 'checkedlist', editable: false,
							cellsrenderer: function (row, column, value){
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								if (data.productId){
									if (data.requireAmount && data.requireAmount == 'Y') {
										return '<span style=\"text-align: right\">' + getUomDescription(data.weightUomId) +'</span>';
									} else {
										return '<span style=\"text-align: right\">' + getUomDescription(data.quantityUomId) +'</span>';
									}
								} else {
									return '<span style=\"text-align: right\"></span>';
								}
							},
						},
						{ text: '${uiLabelMap.IsPromo}', filterable: false, dataField: 'isPromo', width: 120, editable: true, columntype: 'dropdownlist', 
							cellsrenderer: function(row, column, value){
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								if (value === null || value === undefined || value === ''){
									if (data.productCode){
										 return '<span style=\"text-align: right\"></span>';
									 } else {
										 return '<span style=\"text-align: right;\"></span>';
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
								var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								var code = data.productCode; 
								var count = 0;
								for(var i = 0; i < deliveryItemData.length; i++){
									var item = deliveryItemData[i];
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
						        var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
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
						     		    	for (var e = 0; e < deliveryItemData.length; e ++){
						     		    		if (deliveryItemData[e].productCode == prCode && deliveryItemData[e].isPromo == isPromo){
						     		    			objTmp = deliveryItemData[e];
						     		    			break;
						     		    		}
						     		    	}
						     		    	$('#jqxgrid2').jqxGrid('setcellvaluebyid', uid, 'quantity', objTmp.quantity);
						     		    	$('#jqxgrid2').jqxGrid('setcellvaluebyid', uid, 'fromOrderItemSeqId', objTmp.fromOrderItemSeqId);
						     		    } 
						     	    }
						        });
							},
						
					 	},
						 { text: '${uiLabelMap.OrderExpireDate}', filterable: false, hidden: true, dataField: 'expireDate', width: 120, cellsformat: 'dd/MM/yyyy', editable: false, cellsalign: 'right',
							 cellsrenderer: function(row, column, value){
								 if (!value){
									 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
									 if (data.productId){
										 return '<span style=\"text-align: right\"></span>';
									 } else {
										 return '<span style=\"text-align: right\"></span>';
									 }
								 } else {
									 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.getFormattedDate(value)+'</span>';
								 }
							 }
						},
						{ text: '${uiLabelMap.Status}', filterable: false, dataField: 'statusId', width: 150, editable: false,
							 cellsrenderer: function(row, column, value){
								 var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
								 if (data.productId){
									 if (value){
										 var desc = value;
										 if ('DELI_ITEM_EXPORTED' == value){
											desc = '${StringUtil.wrapString(uiLabelMap.Shipping)}';
										 } else if ('DELI_ITEM_DELIVERED' == value){
									    	desc = '${StringUtil.wrapString(uiLabelMap.Received)}';
										 } else {
									    	for(var i = 0; i < dlvItemStatusData.length; i++){
												 if(value == dlvItemStatusData[i].statusId){
													 desc = dlvItemStatusData[i].description;
													 break;
												 }
									    	}
										 }
										 return '<span title=' + desc + '>' + desc + '</span>';
									 } else {
										 return '<span style=\"text-align: right\"></span>';
									 }
								 } else {
									 return '<span style=\"text-align: right\"></span>';
								 }
							 }
						},
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
				{ text: '${uiLabelMap.BLQuantityAvailable}', dataField: 'quantity', width: 150, editable: false, 
					cellsrenderer: function (row, column, value) {
						return '<span style=\"text-align: right\">' + formatnumber(value) + '</span>';
					}
				},
				{ text: '${uiLabelMap.BSPurchaseUomId}', filterable: false, dataField: 'quantityUomId', width: 150, editable: false,
					cellsrenderer: function (row, column, value) {
						var data = grid.jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') value = data.weightUomId;
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
				{ text: '${uiLabelMap.BSPurchaseUomId}', dataField: 'quantityUomId', width: 150, editable: false,
					cellsrenderer: function (row, column, value) {
						var data = grid.jqxGrid('getrowdata', row);
						if (data.requireAmount && data.requireAmount == 'Y') value = data.weightUomId;
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