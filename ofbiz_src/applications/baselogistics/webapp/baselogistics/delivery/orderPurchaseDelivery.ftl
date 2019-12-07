<#include "component://baselogistics/webapp/baselogistics/UPCA/receiveProductByUPCACode.ftl"/>
<#assign acceptFile="image/*"/>
<#assign entityName="Delivery"/>
<#include "component://basesalesmtl/webapp/basesalesmtl/common/fileAttachment.ftl"/>
<#include "script/purchaseDeliveryScript.ftl"/>

<div id="notifyUpdateDeliverySuccessful" style="display: none;">
<div>
	${uiLabelMap.UpdateSuccessfully}.
</div>
</div>
<div id="deliveries-tab" class="tab-pane<#if activeTab?exists && activeTab == "deliveries-tab"> active</#if>">
<#assign columnlist="
				{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
				    groupable: false, draggable: false, resizable: false,
				    datafield: '', columntype: 'number', width: 50,
				    cellsrenderer: function (row, column, value) {
				        return '<div style=margin:4px;>' + (value + 1) + '</div>';
				    }
				},
				{ text: '${uiLabelMap.ReceiveNoteId}', pinned: true, dataField: 'deliveryId', width: 145, editable:false,
				cellsrenderer: function(row, column, value){
					 return '<span><a href=\"javascript:PODlvObj.showDetailDelivery(&#39;' + value + '&#39;);\"' + '> ' + value  + '</a></span>'
				 }
				},
				{ text: '${uiLabelMap.ReceiveToFacility}', dataField: 'destFacilityName', minwidth: 150, editable:false,
					 cellsrenderer: function(row, column, value){
						 return '<span title=\"' + value + '\">' + value + '</span>';
					 },
				 },
				 { text: '${uiLabelMap.Status}', dataField: 'statusId', width: 145, editable:false, filtertype: 'checkedlist',
					cellsrenderer: function(row, column, value){
						var desc = value;
						if ('DLV_EXPORTED' == value){
							desc = '${StringUtil.wrapString(uiLabelMap.Shipping)}';
				        } else if ('DLV_DELIVERED' == value){
				        	desc = '${StringUtil.wrapString(uiLabelMap.Completed)}';
				        } else {
							for(var i = 0; i < statusData.length; i++){
								if(statusData[i].statusId == value){
									desc = statusData[i].description;
								}
							}
				        }
						return '<span title=' + desc + '>' + desc + '</span>';
					},
					createfilterwidget: function (column, columnElement, widget) {
						var filterDataAdapter = new $.jqx.dataAdapter(statusData, {
							autoBind: true
						});
						var records = filterDataAdapter.records;
						widget.jqxDropDownList({source: records, displayMember: 'statusId', valueMember: 'statusId',
							renderer: function(index, label, value){
								if ('DLV_EXPORTED' == value){
									return '<span>${StringUtil.wrapString(uiLabelMap.Shipping)}</span>';
						        } else if ('DLV_DELIVERED' == value){
						        	return '<span>${StringUtil.wrapString(uiLabelMap.Completed)}</span>';
						        } else {
						        	if (statusData.length > 0) {
										for(var i = 0; i < statusData.length; i++){
											if(statusData[i].statusId == value){
												return '<span>' + statusData[i].description + '</span>';
											}
										}
									}
						        }
								return value;
							}
						});
						widget.jqxDropDownList('checkAll');
		   			},
				 },
				 { text: '${uiLabelMap.FormFieldTitle_invoiceId}', dataField: 'invoiceId', minwidth: 150, editable:false,
					 cellsrenderer: function(row, column, value){
						return '<span title=\"' + value + '\">' + value + '</span>'
					 },
				 },
				 { text: '${uiLabelMap.FacilityAddress}', dataField: 'destAddress', minwidth: 150, editable:false,
					 cellsrenderer: function(row, column, value){
						return '<span title=\"' + value + '\">' + value + '</span>'
					 },
				 },
				 { text: '${uiLabelMap.RequireDeliveryDate}', dataField: 'deliveryDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 },
				 },
				{ text: '${uiLabelMap.EstimatedStartDelivery}', dataField: 'estimatedStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 },
				},
				{ text: '${uiLabelMap.EstimatedEndDelivery}', dataField: 'estimatedArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 },
				},
				{ text: '${uiLabelMap.ActualStartDelivery}', dataField: 'actualStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 },
				},
				{ text: '${uiLabelMap.ActualEndDelivery}', dataField: 'actualArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
						 } else {
							 return '<span style=\"text-align: right\">'+ DatetimeUtilObj.formatFullDate(value)+'</span>';
						 }
					 },
				},
				{ text: '${uiLabelMap.ReceiveBatchNumber}', hidden:true, dataField: 'no', width: 120, editable:false,
					cellsrenderer: function(row, column, value){
						return '<span title=\"' + value + '\">' + value + '</span>'
					 },
				 },
				 { text: '${uiLabelMap.CreatedDate}', dataField: 'createDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right',
					 cellsrenderer: function(row, column, value){
						 if (!value){
							 return '<span style=\"text-align: right\"></span>';
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
				{ name: 'destAddress', type: 'string' },
				{ name: 'originAddress', type: 'string' },
				{ name: 'invoiceId', type: 'string' },
				{ name: 'defaultWeightUomId', type: 'string' },
				{name: 'conversionFactor', type: 'string' },
	 		 	]"/>
<#if hasOlbPermission("MODULE", "LOG_DELIVERY", "CREATE") && orderHeader.statusId == "ORDER_APPROVED" && createdDone == false>
	<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=getListDelivery&fromOrderId=${parameters.orderId}&deliveryTypeId=DELIVERY_PURCHASE" createUrl="jqxGeneralServicer?sname=createDelivery&jqaction=C" editmode="dblclick"
		 addColumns="deliveryId;listOrderItems(java.util.List);orderId;currencyUomId;statusId;destFacilityId;originProductStoreId;partyIdTo;partyIdFrom;createDate(java.sql.Timestamp);destContactMechId;conversionFactor;originContactMechId;originFacilityId;deliveryDate(java.sql.Timestamp);deliveryTypeId[DELIVERY_PURCHASE];no;estimatedStartDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);defaultWeightUomId"
		 updateUrl="" editColumns="" functionAfterAddRow="PODlvObj.checkCreatedDone" customTitleProperties="ListReceiveNote"
		 jqGridMinimumLibEnable="true" bindresize="false" mouseRightMenu="true" contextMenuId="DeliveryMenu"
		 customcontrol1="icon-plus@${uiLabelMap.QuickCreate}@javascript:PODlvObj.showPopupSelectFacility('${parameters.orderId}')"
	 />
<#else>
	<@jqGrid filtersimplemode="true" id="jqxgridDelivery" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true"
		 url="jqxGeneralServicer?sname=getListDelivery&fromOrderId=${parameters.orderId}&deliveryTypeId=DELIVERY_PURCHASE" createUrl="jqxGeneralServicer?sname=createDelivery&jqaction=C" editmode="dblclick"
		 updateUrl="" editColumns="" functionAfterAddRow="" customTitleProperties="ListReceiveNote" mouseRightMenu="true" contextMenuId="DeliveryMenu"
		 jqGridMinimumLibEnable="true" bindresize="false"
	 />
</#if>
</div>

<div id="notifyIdCheckDelivery" style="display: none;">
<div>
	${uiLabelMap.ReceiveNoteIdExisted}.
</div>
</div>
<div id="notifyIdNotHaveStorekeeper" style="display: none;">
<div>
	${uiLabelMap.NoStorekeeperOfSeletedFacility}.
</div>
</div>
<div id="containerNotify" style="width: 100%; overflow: auto;">
</div>
<div id="alterpopupWindow" class="hide popup-bound">
	<div>${uiLabelMap.AddNewPurchaseDelivery} - ${uiLabelMap.OrderId}: ${parameters.orderId?if_exists}</div>
	<input type="hidden" id="deliveryTypeId" value="DELIVERY_PURCHASE"/>
	<div class='form-window-container'>
		<div id="containerPopupNotify"></div>
		<div class='form-window-content'>
	        <div class="row-fluid">
	    		<div class="span4">
		    		<!-- <div class="row-fluid margin-top10 margin-bottom10">
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px"> ${uiLabelMap.DAOrderId} </div>
						</div>
						<div class="span7">
							<div id="orderId" style="width: 100%;" class="green-label">${parameters.orderId}</div>
						</div>
					</div> -->
					<div class="row-fluid margin-top10 margin-bottom5 hide">
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px"> ${uiLabelMap.ReceiveNoteId} </div>
						</div>
						<div class="span7">
							<input id="deliveryId" style="width: 100%;" class="green-label"></input>
						</div>
					</div>
					<div class="row-fluid margin-top10 margin-bottom5">
						<div class="span5" style="text-align: right">
	    					<div class="asterisk"> ${uiLabelMap.ReceiveToFacility} </div>
						</div>
						<div class="span7">
							<div id="facilityPopup" class="green-label">
								<div id="jqxgridFacilityPopup"></div>
							</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom5">
						<div class="span5" style="text-align: right">
	    					<div class="asterisk"> ${uiLabelMap.FacilityAddress} </div>
						</div>
						<div class="span7">
							<div id="destContactMechId" style="width: 100%; color: #037C07;" class="green-label"></div>
						</div>
					</div>
                    <div class="row-fluid margin-bottom5 hide" id="divConversionCreate">
                        <div class="span5" style="text-align: right">
                            <div class="asterisk"> ${uiLabelMap.BACCExchangedRate} </div>
                        </div>
                        <div class="span7">
                            <div id="conversionFactorCreate"></div>
                        </div>
                    </div>
	    		</div>
	    		<div class="span4">
		    	<!--	<div class="row-fluid margin-bottom10">
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px"> ${uiLabelMap.ReceiveBatchNumber} </div>
						</div>
						<div class="span7">
							<input id="no" style="width: 100%;" type="text" class="green-label"></input>
						</div>
					</div>
				-->
		    		<div class="row-fluid margin-top10 margin-bottom5">
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px"> ${uiLabelMap.Supplier} </div>
						</div>
						<div class="span7">
							<div id="partyIdFrom" style="width: 100%;" class="green-label">${partyFrom.groupName?if_exists}</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom5">
						<div class="span5" style="text-align: right">
							<div style="margin-right: 10px"> ${uiLabelMap.RequireDeliveryDate} </div>
						</div>
						<div class="span7">
							<div id="deliveryDate" style="width: 100%;"></div>
						</div>
					</div>
	    		</div>
	    		<div class="span4">
					<div class="row-fluid margin-top10 margin-bottom5">
						<div class="span5" style="text-align: right">
	    					<div style="margin-right: 10px"> ${uiLabelMap.EstimatedStartDelivery} </div>
						</div>
						<div class="span7">
							<div id="estimatedStartDate" style="width: 100%;"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom5">
						<div class="span5" style="text-align: right">
	    					<div style="margin-right: 10px"> ${uiLabelMap.EstimatedEndDelivery} </div>
						</div>
						<div class="span7">
							<div id="estimatedArrivalDate" style="width: 100%;"></div>
						</div>
					</div>
	    		</div>
			</div>
			<div class="row-fluid margin-top10">
				<div style="margin-left: 20px"><div id="jqxgrid1"></div></div>
			</div>
		</div>
		<div class="form-action popup-footer">

	        <#if hasOlbPermission("MODULE", "LOG_DELIVERY", "CREATE")>
	        	<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
	        	<button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	    	<#else>
		        <#if hasOlbPermission("MODULE", "LOG_DELIVERY", "VIEW")>
		        	<button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonClose}</button>
	        	</#if>
        	</#if>
		</div>
	</div>
</div>

<div id="selectFacilityWindow" class="hide popup-bound">
	<div>${uiLabelMap.SelectFacilityToReceive}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input type="hidden" id="defaultOrderId" value=""/>
			<div class="row-fluid margin-top20">
	    		<div class="row-fluid">
					<div class="span3" style="text-align: right">
						<div> ${uiLabelMap.Facility} </div>
					</div>
					<div class="span5">
						<div id="facility" class="green-label">
							<div id="jqxgridFacility"></div>
						</div>
					</div>
				</div>
				<div class="row-fluid margin-top10">
					<div class="span3" style="text-align: right">
						<div> ${uiLabelMap.Address} </div>
					</div>
					<div class="span5">
						<div id="defaultContactMechId" style="width: 100%; color: #037C07;" class="green-label"></div>
					</div>
				</div>
                <div class="row-fluid margin-top10 hide" id="divConversion">
                    <div class="span3" style="text-align: right">
                        <div> ${uiLabelMap.ConversionFactor} </div>
                    </div>
                    <div class="span5">
                        <div id="conversionFactor" style="width: 100%; color: #037C07;" class="green-label"></div>
                    </div>
                </div>
			</div>
		</div>
		<div class="form-action popup-footer">
	        <button id="quickCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	        <#if hasOlbPermission("MODULE", "LOG_DELIVERY", "UPDATE")>
	        	<button id="quickSaveApprove" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	        <#else>
	            <#if hasOlbPermission("MODULE", "LOG_DELIVERY", "CREATE")>
	            	<button id="quickSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	            </#if>
	        </#if>
		</div>
	</div>
</div>

<div id="jqxFileScanUpload" style="display: none" class="popup-bound">
	<div>
	    <span>
	        ${uiLabelMap.UploadFileScan}
	    </span>
	</div>
	<div style="overflow: hidden; text-align: center">
		<input multiple type="file" id="attachFile">
		</input>
		<div class="form-action popup-footer">
			<button id="uploadCancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
			<button id="uploadOkButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		</div>
	</div>
</div>
<#include 'purchaseDeliveryCommon.ftl'>
<script type="text/javascript">
	//Create Window
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 950, width: 1300, minHeight: 200, maxHeight: 900, resizable: false, height: 600, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme
	});

	$("#selectFacilityWindow").jqxWindow({
		maxWidth: 800, minWidth: 300, width:550, height: 200, minHeight: 100, maxHeight: 656, resizable: false, isModal: true, modalZIndex: 100000, zIndex: 100000, autoOpen: false, cancelButton: $("#quickCancel"), modalOpacity: 0.7, theme:theme
	});

	$("#notifyUpdateDeliverySuccessful").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#popupContainerNotify",
        autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success"
    });
	var pathScanFile = null;
	$(document).ready(function (){
		Loading.setIndex('999999');
		var destContactData = new Array();
		$("#deliveryId").jqxInput({width: 195, height: 20});
		// $("#no").jqxInput({width: 195, height: 20});

		$('#destContactMechId').jqxDropDownList({placeHolder: '${uiLabelMap.PleaseSelectTitle}', dropDownHeight: 150, source: destContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});

		$('#defaultContactMechId').jqxDropDownList({placeHolder: '${uiLabelMap.PleaseSelectTitle}', dropDownHeight: 150, source: destContactData, selectedIndex: 0, width: 350, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});

        $("#conversionFactor").jqxNumberInput({width: 350, spinButtons: true, decimalDigits:2, groupSeparator: '',
            digits: 11, spinMode: 'simple',  inputMode: 'simple', min: 1});
        $("#conversionFactorCreate").jqxNumberInput({width: 200, spinButtons: true, decimalDigits:2, groupSeparator: '',
            digits: 11, spinMode: 'simple',  inputMode: 'simple', min: 1});
        if('VND' !== currencyUom) {
            $("#divConversionCreate").removeClass('hide');
        }
		$("#deliveryDate").jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss', disabled: true});
		$("#deliveryDate").jqxDateTimeInput('val', estimatedDeliveryDate);

		var tmp = $('#deliveryDate').jqxDateTimeInput('getDate');
		$('#estimatedStartDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss'});
		$('#estimatedArrivalDate').jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy HH:mm:ss'});
		$('#estimatedStartDate').val(tmp);
		$('#estimatedArrivalDate').val(tmp);

		$("#notifyIdCheckDelivery").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerPopupNotify",
	        autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "error"
	    });
		$("#notifyIdNotHaveStorekeeper").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
	        autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "error"
	    });
		$("#detailApprove").hide();
		$("#detailConfirm").hide();

		$('#jqxFileScanUpload').jqxWindow({ width: 400, modalZIndex: 10000, height: 220, isModal: true, autoOpen: false });
	});

	function loadOrderItem(valueDataSoure){
		var sourceOrderItem =
		    {
	        datafields:[{ name: 'orderId', type: 'string' },
	                 	{ name: 'orderItemSeqId', type: 'string' },
	                 	{ name: 'productId', type: 'string' },
	                 	{ name: 'productCode', type: 'string' },
	                 	{ name: 'productName', type: 'string' },
	                 	{ name: 'expireDate', type: 'date', other: 'Timestamp' },
						{ name: 'prodCatalogId', type: 'string' },
						{ name: 'productCategoryId', type: 'string' },
	                 	{ name: 'orderItemTypeId', type: 'string' },
	                 	{ name: 'resQuantity', type: 'number' },
	                 	{ name: 'requiredQuantity', type: 'number' },
	                 	{ name: 'createdQuantity', type: 'number' },
						{ name: 'quantityUomId', type: 'string'},
						{ name: 'weightUomId', type: 'string'},
	                 	{ name: 'facilityId', type: 'string' },
	                 	{ name: 'requiredQuantityTmp', type: 'string' },
	                 	{ name: 'quantityQC', type: 'number' },
	                 	{ name: 'quantityEA', type: 'number' },
	                 	{ name: 'quantityCreate', type: 'string' },
						{ name: 'unitPrice', type: 'number'},
						{ name: 'quantityOnHandTotal', type: 'number'},
						{ name: 'amountOnHandTotal', type: 'number'},
						{ name: 'availableToPromiseTotal', type: 'number'},
						{ name: 'weight', type: 'number'},
						{ name: 'baseWeightUomId', type: 'string' },
						{ name: 'baseQuantityUomId', type: 'string' },
						{ name: 'requireAmount', type: 'string' },
						{ name: 'convertNumber', type: 'string' },
			 		 	],
	        localdata: valueDataSoure,
	        datatype: "array",
	    };
	    var dataAdapterOrderItem = new $.jqx.dataAdapter(sourceOrderItem);
	    $("#jqxgrid1").jqxGrid({
        source: dataAdapterOrderItem,
        filterable: true,
        showfilterrow: true,
        theme: 'olbius',
        rowsheight: 26,
        width: '100%',
        height: 350,
        autoheight: false,
        enabletooltips: true,
        autoheight: false,
        pageable: true,
        pagesize: 10,
        editable: true,
        columnsresize: true,
        selectionmode: 'checkbox',
        localization: getLocalization(),
        columns: [{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
			datafield: '', columntype: 'number', width: 50,
			cellsrenderer: function (row, column, value) {
		    return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    }
		},
          { text: '${uiLabelMap.ProductId}', dataField: 'productCode', width: 150, editable: false, pinned: true},
          { text: '${uiLabelMap.ProductName}', dataField: 'productName', minwidth: 200, editable: false,
          },
		 { text: '${uiLabelMap.BSPurchaseUomId}', datafield: 'quantityUomId', align: 'left', width: 120, filtertype: 'checkedlist', editable: false,
			cellsrenderer: function (row, column, value){
				var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
				var requireAmount = data.requireAmount;
				if (requireAmount && 'Y' == requireAmount) {
					return '<span style=\"text-align: right\">' + getUomDescription(data.weightUomId) +'</span>';
				} else {
					return '<span style=\"text-align: right\">' + getUomDescription(value) +'</span>';
				}
			},
		},
		 { text: '${uiLabelMap.BLQuantityByQCUom}', filterable: false, dataField: 'quantityQC', columntype: 'numberinput', width: 120, editable: true,
 				cellsrenderer: function(row, column, value){
                   return '<span class=\"focus-color align-right\">' + formatnumber(value)+ '</span>';
				},
				createeditor: function(row, value, editor){
			        var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
			        var requireAmount = data.requireAmount;
			        if (requireAmount && 'Y' == requireAmount) {
			        	editor.jqxNumberInput({ decimalDigits: 2});
			        } else {
			        	editor.jqxNumberInput({ decimalDigits: 0});
			        }
			        if (value === null || value === undefined || value === ''){
			        	if (data.requiredQuantity && data.createdQuantity){
				        	editor.jqxNumberInput('val', data.requiredQuantity - data.createdQuantity);
				        }
			        }
			    },
			    validation: function (cell, value) {
    	 			if (value < 0) {
        		 	return { result: false, message: '${uiLabelMap.NumberGTZ}'};}

            	 	var dataTmp = $('#jqxgrid1').jqxGrid('getrowdata', cell.row);
            	 	var convert = dataTmp.convertNumber;
				 	var x = value*convert + Number(dataTmp.quantityCreate) - Number(dataTmp.quantityQC)*convert;
				 	var y = dataTmp.requiredQuantityTmp;
				 	if (x > y){
						 return { result: false, message: '${uiLabelMap.BLQuantityGreateThanQuantityNotReceiveYet}: ' + x + ' > ' + y};
				 	}
				 	return true;
			 	},
			    cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
				 	var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
				 	var convert = data.convertNumber;
		        	if (data.quantityCreate) {
		        		var totalEA = data.quantityCreate - oldvalue*convert + newvalue*convert;
			        	$('#jqxgrid1').jqxGrid('setcellvaluebyid', data.uid, 'quantityCreate', totalEA);
		        	} else {
		        		var totalEA = newvalue*convert;
			        	$('#jqxgrid1').jqxGrid('setcellvaluebyid', data.uid, 'quantityCreate', totalEA);
		        	}
	    		}
		 },
		 { text: '${uiLabelMap.BLQuantityByEAUom}', filterable: false, dataField: 'quantityEA', columntype: 'numberinput', width: 120, editable: true,
 				cellsrenderer: function(row, column, value){
                   return '<span class=\"focus-color align-right\">' + formatnumber(value)+ '</span>';
				},
				createeditor: function(row, value, editor){
			        var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
			        var requireAmount = data.requireAmount;
			        if (requireAmount && 'Y' == requireAmount) {
			        	editor.jqxNumberInput({ decimalDigits: 2});
			        } else {
			        	editor.jqxNumberInput({ decimalDigits: 0});
			        }
			        if (value === null || value === undefined || value === ''){
			        	if (data.requiredQuantity && data.createdQuantity){
				        	editor.jqxNumberInput('val', data.requiredQuantity - data.createdQuantity);
				        }
			        }
			    },
		     	validation: function (cell, value) {
    	 			if (value < 0) {
        		 	return { result: false, message: '${uiLabelMap.NumberGTZ}'};}

            	 	var dataTmp = $('#jqxgrid1').jqxGrid('getrowdata', cell.row);
				 	var x = value + Number(dataTmp.quantityCreate) - Number(dataTmp.quantityEA);
				 	var y = dataTmp.requiredQuantityTmp;
				 	if (x > y){
						 return { result: false, message: '${uiLabelMap.BLQuantityGreateThanQuantityNotReceiveYet}: ' + x + ' > ' + y};
				 	}
				 	return true;
			 	},
			 	cellendedit: function (row, datafield, columntype, oldvalue, newvalue) {
				 	var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
		        	if (data.quantityCreate) {
		        		var totalEA = data.quantityCreate - oldvalue + newvalue;
			        	$('#jqxgrid1').jqxGrid('setcellvaluebyid', data.uid, 'quantityCreate', totalEA);
		        	} else {
		        		$('#jqxgrid1').jqxGrid('setcellvaluebyid', data.uid, 'quantityCreate', newvalue);
		        	}
	    		}
		 },
		 { text: '${uiLabelMap.BLQuantityEATotal}', filterable: false, columntype: 'numberinput',  cellsalign: 'right', dataField: 'quantityCreate', width: 120, editable: false,
			 cellsrenderer: function (row, column, value){
				return '<span class=\"align-right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>';
		 	}
		 },
		 { text: '${uiLabelMap.OrderNumber}', filterable: false, dataField: 'requiredQuantity', width: 150, editable: false,
             cellsrenderer: function(row, column, value){
                 var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
                 var description = '';
                 if (data.quantityUomId){
                	 for(var i = 0; i < uomData.length; i++){
                         if(uomData[i].quantityUomId == data.quantityUomId){
                             description = uomData[i].description;
                         }
                	 }
                	 return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value)+'</span>';
                 } else if (data.baseQuantityUomId){
                	 for(var i = 0; i < uomData.length; i++){
                         if(uomData[i].quantityUomId == data.baseQuantityUomId){
                             description = uomData[i].description;
                         }
                	 }
                	 return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) +'</span>';
                 }
                 return '<span style=\"text-align: right\" title=' + formatnumber(value) + '>' + formatnumber(value) + '</span>';
		     }
		 },
		 { text: '${uiLabelMap.QuantityCreateOrReceived}', filterable: false, width: 150, editable: false,
		     cellsrenderer: function(row, column, value){
		         var data = $('#jqxgrid1').jqxGrid('getrowdata', row);
		         var tmp = data.createdQuantity;
	             return '<span style=\"text-align: right\">' + formatnumber(tmp) + '</span>';
		     }
		 },
		 { text: '${uiLabelMap.RequiredExpireDate}', hidden: true, dataField: 'expireDate', width: 150, cellsformat: 'dd/MM/yyyy', cellsalign: 'right', filtertype: 'range', editable: false,
       	  cellsrenderer: function(row, column, value){
				 if (value){
					 return '<span style=\"text-align: right\" title=\"' + value + '\">' + value + '</span>'
				 } else {
					 return '<span style=\"text-align: right\"></span>'
				 }
			 },
		 },
	    ]
	    });
	}

</script>