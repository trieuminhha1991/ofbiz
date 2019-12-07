<script type="text/javascript" src="/delys/images/js/util/DateUtil.js" ></script>
<#assign transferType = "true"/>
<script>

	<#assign localeStr = "VI" />
	<#if locale = "en">
	<#assign localeStr = "EN" />
	</#if>
	
	<#assign itemTypes = delegator.findList("TransferItemType", null, null, null, null, false) >
	var transferItemTypeData = [];
	<#list itemTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		row['transferItemTypeId'] = "${item.transferItemTypeId?if_exists}";
		row['description'] = "${description}";
		transferItemTypeData[${item_index}] = row;
	</#list>
	
	<#assign listUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var packingUomData = [];
	<#list listUoms as item>
		var row = {};
		<#assign qtyDesc = StringUtil.wrapString(item.get("description", locale))/>
		row['uomId'] = "${item.uomId?if_exists}";
		row['description'] = "${qtyDesc?if_exists}";
		packingUomData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${description?if_exists}";
		weightUomData[${item_index}] = row;
	</#list>
	
	<#assign uomConversions = delegator.findList("UomConversion", null, null, null, null, false) />
	var uomConvertData = new Array();
	<#list uomConversions as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['uomIdTo'] = "${item.uomIdTo}";
		row['conversionFactor'] = "${item.conversionFactor}";
		uomConvertData[${item_index}] = row;
	</#list>
	
    var listInv = [];
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false) />
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${description?if_exists}";
		statusData[${item_index}] = row;
	</#list>
	
	<#assign originFacility = delegator.findOne("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", transfer.originFacilityId), false)>
	<#assign destFacility = delegator.findOne("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", transfer.destFacilityId), false)>
	<#assign transferShipGroup = delegator.findList("TransferItemShipGroup", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("transferId", transfer.transferId)), null, null, null, false)>
	<#assign originCTM = transferShipGroup.get(0).originContactMechId>
	<#assign destCTM = transferShipGroup.get(0).destContactMechId>
	<#assign originFacilityAddress = delegator.findOne("PostalAddress", {"contactMechId" : originCTM}, true) />
	<#assign destFacilityAddress = delegator.findOne("PostalAddress", {"contactMechId" : destCTM}, true) />
	var deliveryDT;
	var listInv = [];
	$.ajax({
        type: "POST",
        url: "getInvByTransferAndDlv",
        data: {'transferId': '${parameters.transferId}'},
        dataType: "json",
        async: false,
        success: function(response){
            listInv = response.listData;
        },
        error: function(response){
          alert("Error:" + response);
        }
	});
</script>
<div id="deliveries-tab" class="tab-pane">
	<#assign columnlist= "
					{	text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					    	return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.TransferNoteId}', dataField: 'deliveryId', width: 120, filtertype:'input', editable:false, pinned: true,
					cellsrenderer: function(row, column, value){
						 return '<span><a href=\"javascript:void(0);\" onclick=\"showDetailPopup(&#39;' + value + '&#39;)\"' + '> ' + value  + '</a></span>'
					 }
					},
					{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, editable:true, columntype: 'dropdownlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < statusData.length; i++){
								if(statusData[i].statusId == value){
									return '<span title=' + value + '>' + statusData[i].description + '</span>'
								}
							}
						},
						createeditor: function(row, value, editor){
							editor.jqxDropDownList({ source: statusData, displayMember: 'statusId', valueMember: 'statusId',
								renderer: function(index, label, value) {
									for(var i = 0; i < statusData.length; i++){
										if(value == statusData[i].statusId){
											return '<span>' + statusData[i].description + '</span>'
										}
									}
								}
							});
						},
						filtertype: 'input'
					 },
					 { text: '${uiLabelMap.FacilityFrom}', dataField: 'originFacilityId', width: 150, editable:false,
						 cellsrenderer: function(row, column, value){
							 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							 return '<span>' + data.originFacilityName + '</span>';
						 },
						 filtertype: 'input'
					 },
					 { text: '${uiLabelMap.FacilityTo}', dataField: 'destFacilityId', editable:false, width: 150,
						 cellsrenderer: function(row, column, value){
							 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							 return '<span>' + data.destFacilityName + '</span>';
						 },
						 filtertype: 'input'
					 },
					 { text: '${uiLabelMap.ActualExportedDate}', dataField: 'actualStartDate', width: 130, cellsformat: 'dd/MM/yyyy', filtertype: 'date', editable:false},
					 { text: '${uiLabelMap.ActualDeliveredDate}', dataField: 'actualArrivalDate', width: 130, cellsformat: 'dd/MM/yyyy', filtertype: 'date', editable:false},
					 { text: '${uiLabelMap.EstimatedExportDate}', dataField: 'estimatedStartDate', width: 130, cellsformat: 'dd/MM/yyyy', filtertype: 'date', editable:false},
					 { text: '${uiLabelMap.EstimatedDeliveryDate}', dataField: 'estimatedArrivalDate', width: 130, cellsformat: 'dd/MM/yyyy', filtertype: 'date', editable:false},
					 { text: '${uiLabelMap.createDate}', dataField: 'createDate', width: 130, cellsformat: 'dd/MM/yyyy', filtertype: 'date', editable:false},
					 "/>
	<#assign dataField="[{ name: 'deliveryId', type: 'string' },
					{ name: 'statusId', type: 'string' },
                 	{ name: 'originFacilityId', type: 'string' },
                 	{ name: 'destFacilityId', type: 'string' },
                 	{ name: 'destFacilityName', type: 'string' },
                 	{ name: 'originFacilityName', type: 'string' },
                 	{ name: 'createDate', type: 'date', other: 'Timestamp' },
                 	{ name: 'estimatedStartDate', type: 'date', other: 'Timestamp' },
                 	{ name: 'actualStartDate', type: 'date', other: 'Timestamp' },
                 	{ name: 'estimatedArrivalDate', type: 'date', other: 'Timestamp' },
					{ name: 'actualArrivalDate', type: 'date', other: 'Timestamp' },
					{ name: 'deliveryDate', type: 'date', other: 'Timestamp' },
					{ name: 'defaultWeightUomId', type: 'string' },
		 		 	]"/>
	<@jqGrid filtersimplemode="true" id="jqxgrid" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
		 url="jqxGeneralServicer?sname=getListTransferDelivery&transferId=${parameters.transferId?if_exists}&deliveryId=${parameters.deliveryId?if_exists}" createUrl="jqxGeneralServicer?sname=createTransferDelivery&jqaction=C" editmode="dblclick"
		 addColumns="listTransferItems(java.util.List);transferId;statusId;destFacilityId;originFacilityId;deliveryDate(java.sql.Timestamp);deliveryTypeId[DELIVERY_TRANSFER];estimatedStartDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);defaultWeightUomId" 	 
		 updateUrl="jqxGeneralServicer?sname=updateTransferDelivery&jqaction=U" editColumns="statusId" functionAfterAddRow="updateJqxgridProduct()" customCss="mgrTop10"
		 customTitleProperties="ListTransferNote"/>
</div>
<style type="text/css">
    .mgrTop10{
        margin-top:10px !important;
    }
</style>
<div id="popupDeliveryDetailWindow" class="hide">
	<div>${uiLabelMap.DeliveryDetail}</div>
	<div style="overflow: hidden;">
	    <h4 class="row header smaller lighter blue" style="margin-right:25px !important;margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
			${uiLabelMap.GeneralInfo}
			<a style="float:right;font-size:14px;" id="printPDF" target="_blank" data-rel="tooltip" title="${uiLabelMap.PrintToPDF}" data-placement="bottom" data-original-title="${uiLabelMap.PrintToPDF}"><i class="fa-file-pdf-o"></i>&nbsp;PDF</a>
		</h4>
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5' style="text-align: right;">
						${uiLabelMap.deliveryIdDT}:
					</div>
					<div class='span7 green-label'>
						<div id="deliveryIdDT">
						</div>
					</div>
				</div>
			</div>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5' style="text-align: right;">
						${uiLabelMap.statusIdDT}:
					</div>
					<div class='span7 green-label'>
						<div id="statusIdDT">
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5' style="text-align: right;">
						${uiLabelMap.FacilityFrom}:
					</div>
					<div class='span7 green-label'>
						<div id="originFacilityIdDT">
						</div>
					</div>
				</div>
			</div>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5' style="text-align: right;">
						${uiLabelMap.FacilityTo}:
					</div>
					<div class='span7 green-label'>
						<div id="destFacilityIdDT">
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5' style="text-align: right; ">
						${uiLabelMap.OriginContactMech}:
					</div>
					<div class='span7 green-label'>
						<div id="originContactMechIdDT">
						</div>
					</div>
				</div>
			</div>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5' style="text-align: right;">
						${uiLabelMap.DestinationContactMech}:
					</div>
					<div class='span7 green-label'>
						<div id="destContactMechIdDT">
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5' style="text-align: right;">
						${uiLabelMap.createDate}:
					</div>
					<div class='span7 green-label'>
						<div id="createDateDT">
						</div>
					</div>
				</div>
			</div>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span5' style="text-align: right;">
						${uiLabelMap.RequireDeliveryDate}:
					</div>
					<div class='span7 green-label'>
						<div id="deliveryDateDT">
						</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span6">
				<div class='row-fluid'>
				    <div class="span5" style="text-align: right;">${uiLabelMap.TotalWeight}:</div>
				    <div class="span7"><div id="totalWeight" class="green-label"></div></div>
			    </div>
		    </div>
		    <div class="span6">
				<div class='row-fluid'>
				    <div class="span5" id="actualStartLabel" style="text-align: right;" class="asterisk"></div>
				    <div class="span7">
				    	<div id="actualStartDateDis" class="green-label"></div>
				    	<div id="actualStartDate" class="green-label"></div>
			    	</div>
			    </div>
		    </div>
		</div>
		<div class="row-fluid">
			<div class="span6">
				<div class='row-fluid'>
					<div class="span5" id="scanLabel" style="text-align: right;" class="asterisk"></div>
				    <div class="span7"><div id="scanfile" class="green-label"></div></div>
			    </div>
		    </div>
		    <div class="span6">
				<div class='row-fluid'>
				    <div class="span5" id="actualArrivalLabel" style="text-align: right;" class="asterisk"></div>
				    <div class="span7">
				    	<div id="actualArrivalDateDis" class="green-label"></div>
				    	<div id="actualArrivalDate" class="green-label"></div>
				    </div>
			    </div>
		    </div>
		</div>
		<div class='row-fluid'>
			<div class='span12'>
				<div style="margin-left: 20px"><#include "component://delys/webapp/delys/accounting/appr/listDeliveryItem.ftl" /></div>
			</div>
		</div>
        <div class="form-action">
	        <div class='row-fluid'>
	            <div class="span12 margin-top20" style="margin-bottom:10px;">
	                <button id="alterCancel2" class='btn btn-danger form-action-button pull-right' style="margin-right:15px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                <button id="alterSave2" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	            </div>
	        </div>
	    </div>
	</div>
</div>
<#assign trasferType = delegator.findOne("TransferType", {"transferTypeId", transfer.transferTypeId}, false)>
<#assign typeDescriptionTmp = StringUtil.wrapString(trasferType.get("description", locale))>
<div id="alterpopupWindow" class="hide">
	<div>${uiLabelMap.AddNewDeliverySales}</div>
	<div>
		<input type="hidden" id="transferId" value="${transfer.transferId}"></input>
		<div class='row-fluid'>
			<div class='span12'>
			    <h4 class="row header smaller lighter blue" style="margin: 5px 25px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
				    ${uiLabelMap.GeneralInfo}
				</h4>
			</div>
		</div>
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.TransferId}: </div>
					</div>
					<div class="span7">
						<div class="green-label">${transfer.transferId}</div>
					</div>
				</div>
				<div class="row-fluid">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.FacilityFrom}: </div>
					</div>
					<div class="span7">
						<#if transfer.transferTypeId == "TRANS_INTERNAL">
							<div class="green-label">${originFacility.facilityName}</div>
		    			<#elseif transfer.transferTypeId = "TRANS_SALES_CHANNEL">
		    				<div class="green-label">${originFacility.facilityName} GT</div> 
		    			</#if>
					</div>
				</div>
				<div class="row-fluid">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.FacilityAddress}: </div>
					</div>
					<div class="span7">
						<div class="green-label">${originFacilityAddress.address1}</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div style="margin-top: 5px"> ${uiLabelMap.RequireDeliveryDate}: </div>
					</div>
					<div class="span7">
						<div id="deliveryDate" style="width: 200px" class="green-label"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div style="margin-top: 5px"> ${uiLabelMap.TotalWeight}: </div>
					</div>
					<div class="span7">	
						<div class="row-fluid">
							<div class="span5">
								<div style="margin-top: 5px" id="totalProductWeight" class="green-label"></div>
							</div>
							<div class="span7">
								<div id="listWeightUomId" class="green-label"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="span6">
				<div class="row-fluid">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.TransferType}: </div>
					</div>
					<div class="span7">
						<div class="green-label">${typeDescriptionTmp}</div>
					</div>
				</div>
				<div class="row-fluid">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.FacilityTo}: </div>
					</div>
					<div class="span7">
						<#if transfer.transferTypeId == "TRANS_INTERNAL">
							<div class="green-label">${destFacility.facilityName}</div>
		    			<#elseif transfer.transferTypeId = "TRANS_SALES_CHANNEL">
		    				<div class="green-label">${destFacility.facilityName} MT</div>
		    			</#if>
					</div>
				</div>
				<div class="row-fluid">	
					<div class="span5" style="text-align: right">
						<div> ${uiLabelMap.FacilityAddress}: </div>
					</div>
					<div class="span7">
						<div class="green-label">${destFacilityAddress.address1}</div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<div style="margin-top: 5px"> ${uiLabelMap.EstimatedStartDate}: </div>
					</div>
					<div class="span7">	
						<div id="estimatedStartDate" style="width: 100%;"></div>
					</div>
				</div>
				<div class="row-fluid margin-bottom10">	
					<div class="span5" style="text-align: right">
						<#if transfer.transferTypeId == "TRANS_INTERNAL">
							<div style="margin-top: 5px"> ${uiLabelMap.EstimatedArrivalDate}: </div>
		    			<#elseif transfer.transferTypeId = "TRANS_SALES_CHANNEL">
		    				<div style="margin-top: 5px"> ${uiLabelMap.EstimatedCompletedDate}: </div>
		    			</#if>
					</div>
					<div class="span7">	
						<div id="estimatedArrivalDate" style="width: 100%;"></div>
					</div>
				</div>
			</div>
		</div>
		<div>
			<div class="margin-left20 margin-top20"><#include "ListTransferItem.ftl" /></div>
		</div> 
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="addButtonCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:21px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="addButtonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<div id="jqxFileScanUpload" style="display: none">
	<div>
	    <span>
	        ${uiLabelMap.UploadFileScan}
	    </span>
	</div>
	<div style="overflow: hidden; text-align: center">
		<input multiple type="file" id="attachFile">
		</input>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="uploadCancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="uploadOkButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</div>
</div>

<style type="text/css">
    .bootbox{
        z-index: 99000 !important;
    }
    .modal-backdrop{
        z-index: 89000 !important;
    }
</style>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript">
	var listImage = [];
	var pathScanFile = null;
	$('#document').ready(function(){
		$.jqx.theme = 'olbius';
		theme = $.jqx.theme;
		$('#jqxgrid').jqxGrid('selectrow', 0);
		$('#totalProductWeight').text('0');
		$("#alterpopupWindow").jqxWindow({
			maxWidth: 1500, modalZIndex: 10000, minWidth: 950, minHeight: 630, maxHeight: 1200, resizable: true,  isModal: true, autoOpen: false, cancelButton: $("#addButtonCancel"), modalOpacity: 0.7, theme:theme           
		});
		$('#jqxFileScanUpload').jqxWindow({ width: 400, modalZIndex: 10000, height: 220, isModal: true, autoOpen: false });
		initAttachFile();
		initGridjqxgridProduct();
	});
	$("#alterpopupWindow").on('open', function(){
		
	});
	$("#deliveryDate").jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy', disabled: true});
	var temp = '${transfer.estimatedStartDate}';
	var arr = temp.split(" ");
	var arr2 = arr[0].split("-");
	$("#deliveryDate").jqxDateTimeInput('val', new Date(arr2[0], arr2[1]-1, arr2[2]));
	$('#estimatedStartDate').jqxDateTimeInput({width: 200});
	$('#estimatedStartDate').jqxDateTimeInput('val', new Date(arr2[0], arr2[1]-1, arr2[2]));
	$('#estimatedArrivalDate').jqxDateTimeInput({width: 200});
	temp = '${transfer.estimatedCompletedDate}';
	arr = temp.split(" ");
	arr2 = arr[0].split("-");
	$('#estimatedArrivalDate').jqxDateTimeInput('val', new Date(arr2[0], arr2[1]-1, arr2[2]));
	
	$('#listWeightUomId').jqxDropDownList({source: weightUomData, selectedIndex: 0, width: 90, theme: theme, displayMember: 'description', valueMember: 'uomId'});
	$('#listWeightUomId').jqxDropDownList('val','WT_kg');
	
	function initAttachFile(){
		$('#attachFile').html('');
		listImage = [];
		$('#attachFile').ace_file_input({
			style:'well',
			btn_choose:'${StringUtil.wrapString(uiLabelMap.DropFileOrClickToChoose)}',
			btn_change:null,
			no_icon:'icon-cloud-upload',
			droppable:true,
			onchange:null,
			thumbnail:'small',
			before_change:function(files, dropped) {
				listImage = [];
				var count = files.length;
				for (var int = 0; int < files.length; int++) {
					var imageName = files[int].name;
					var hashName = imageName.split(".");
					var extended = hashName.pop();
					if (extended == "JPG" || extended == "jpg" || extended == "jpeg" || extended == "gif" || extended == "png") {
						listImage.push(files[int]);
					}
				}
				return true;
			},
			before_remove : function() {
				listImage = [];
				return true;
			}
		});
	}
	$('#uploadOkButton').click(function(){
		saveFileUpload();
	});
	$('#uploadCancelButton').click(function(){
		$('#jqxFileScanUpload').jqxWindow('close');
	});
	$('#jqxFileScanUpload').on('close', function(event){
		initAttachFile();
	});
	function saveFileUpload (){
		var folder = "/delys/logDelivery";
		for ( var d in listImage) {
			var file = listImage[d];
			var dataResourceName = file.name;
			var path = "";
			var form_data= new FormData();
			form_data.append("uploadedFile", file);
			form_data.append("folder", folder);
			jQuery.ajax({
				url: "uploadDemo",
				type: "POST",
				data: form_data,
				cache : false,
				contentType : false,
				processData : false,
				success: function(res) {
					path = res.path;
					pathScanFile = path;
					$('#linkId').html("");
					$('#linkId').attr('onclick', null);
					$('#linkId').append("<a href='"+path+"' onclick='' target='_blank'><i class='fa-file-text-o'></i>'"+dataResourceName+"'</a> <a onclick='removeScanFile()'><i class='fa-remove'></i></a>");
		        }
			}).done(function() {
			});
		}
		$('#jqxFileScanUpload').jqxWindow('close');
	}
	function removeScanFile (){
		pathScanFile = null;
		$('#linkId').html("");
		$('#linkId').attr('onclick', null);
		$('#linkId').append("<a id='linkId' onclick='showAttachFilePopup()'><i class='icon-upload'></i>${uiLabelMap.AttachFileScan}</a>");
	}
	function showAttachFilePopup(){
		$('#jqxFileScanUpload').jqxWindow('open');
	}
	
	// update the edited row when the user clicks the 'Save' button.
	$("#addButtonSave").click(function () {
		var row;
		//Get List Order Item
		var selectedIndexs = $('#jqxgridProduct').jqxGrid('getselectedrowindexes');
		if(selectedIndexs.length == 0){
		    bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		    return false;
		} else {
			bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}",function(result){ 
    			if(result){	
    				var listTransferItems = [];
    				for(var i = 0; i < selectedIndexs.length; i++){
    					var data = $('#jqxgridProduct').jqxGrid('getrowdata', selectedIndexs[i]);
    					var map = {};
    					map['transferItemSeqId'] = data.transferItemSeqId;
    					map['transferId'] = data.transferId;
    					map['inventoryItemId'] = data.inventoryItemId;
    					map['shipGroupSeqId'] = data.shipGroupSeqId;
    					map['quantity'] = data.quantityToDelivery;
    					listTransferItems.push(map);
    				}
    				var listTransferItems = JSON.stringify(listTransferItems);
    				row = { 
    					transferId:$('#transferId').val(),
    					deliveryDate:$('#deliveryDate').jqxDateTimeInput('getDate'),
    					listTransferItems:listTransferItems,
    					estimatedStartDate: $('#estimatedStartDate').jqxDateTimeInput('getDate'),
    					estimatedArrivalDate: $('#estimatedArrivalDate').jqxDateTimeInput('getDate'),
    					defaultWeightUomId: $('#listWeightUomId').val(),
    				};
    				$("#alterpopupWindow").jqxWindow('close');
    				$("#jqxgrid").jqxGrid('addRow', null, row, "first");
    				$("#jqxgrid").jqxGrid('updatebounddata');                        
    			}
    		});
		}
	});
	$('#alterpopupWindow').on('close', function (event) { 
		$('#jqxgridProduct').jqxGrid('clearSelection');
	}); 
	$("#addButtonCancel").click(function () {
		$("#alterpopupWindow").jqxWindow('close');
	});
	
	function updateTotalWeight(){
		var totalProductWeight = 0;
		var selectedIndexs = $('#jqxgridProduct').jqxGrid('getselectedrowindexes');
		for(var i = 0; i < selectedIndexs.length; i++){
			var data = $('#jqxgridProduct').jqxGrid('getrowdata', selectedIndexs[i]);
			var baseWeightUomId = data.baseWeightUomId;
			var defaultWeightUomId = $('#listWeightUomId').val();
			var itemWeight = 0;
			if (data.availableToPromiseTotal < 1){
				itemWeight = 0;
			} else {
				itemWeight = (data.quantityToDelivery)*(data.weight)*(data.convertNumber);
			}
			if (baseWeightUomId == defaultWeightUomId){
				totalProductWeight = totalProductWeight + itemWeight;
			} else {
				for (var j=0; j<uomConvertData.length; j++){
					if ((uomConvertData[j].uomId == baseWeightUomId && uomConvertData[j].uomIdTo == defaultWeightUomId) || (uomConvertData[j].uomId == defaultWeightUomId && uomConvertData[j].uomIdTo == baseWeightUomId)){
						totalProductWeight = totalProductWeight + (uomConvertData[j].conversionFactor)*itemWeight;
						break;
					}
				}
			}
		}
		var n = parseFloat(totalProductWeight)
		totalProductWeight = Math.round(n * 1000)/1000;
		$('#totalProductWeight').text(totalProductWeight);
	}
	function rowselectfunctionProduct(event){
	    if (typeof event.args.rowindex != 'number'){
	        var tmpArray = event.args.rowindex;
	        for(i = 0; i < tmpArray.length; i++){
	            if(checkRequiredData(tmpArray[i])){
	                $('#jqxgridProduct').jqxGrid('clearselection');
	                break; // Stop for first item
	            }
	        }
	    } else{
	        var test = checkRequiredData(event.args.rowindex);
	        if (!test){
	        	updateTotalWeight();
	        }
	    }
	}
	function checkRequiredData(rowindex){
	    var data = $('#jqxgridProduct').jqxGrid('getrowdata', rowindex);
	    if(data == undefined){
	        return true; // to break the loop
	    } 
	    if (data.availableToPromiseTotal < 1){
	    	displayNotEnough(rowindex, "${uiLabelMap.FacilityNotEnoughProduct}");
	    	return true;
	    }
	    if(data.quantityToDelivery == undefined){
	        displayAlert(rowindex, "${uiLabelMap.DLYItemMissingFieldsDlv}");
	        return true;
	    } else if (data.quantityToDelivery < 1){
	    	displayAlert(rowindex, "${uiLabelMap.NumberGTZ}");
	        return true;
	    }else if(data.quantityToDelivery > data.quantity){
	        displayAlert(rowindex, "${uiLabelMap.ExportValueLTZRequireValue}");
	        return true;
	    }
	    return false;
	}
	
	function displayNotEnough(rowindex, message){
	    bootbox.dialog(message, [{
	        "label" : "${uiLabelMap.CommonOk}",
	        "class" : "btn btn-primary standard-bootbox-bt",
	        "icon" : "fa fa-check",
	        "callback": function() {
	        	 $("#jqxgridProduct").jqxGrid('unselectrow', rowindex);
	        }
	        }]
	    );
	}
	function displayAlert(rowindex, message){
	    bootbox.dialog(message, [{
	        "label" : "${uiLabelMap.CommonOk}",
	        "class" : "btn btn-primary standard-bootbox-bt",
	        "icon" : "fa fa-check",
	        "callback": function() {
	            $("#jqxgridProduct").jqxGrid('begincelledit', rowindex, "quantityToDelivery");
	        }
	        }]
	    );
	}
	
	function checkRequiredTranferProductByFacilityToFacility(rowindex){
		var data = $('#jqxgridProduct').jqxGrid('getrowdata', rowindex);
		if(data == undefined){
            bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                        $("#jqxgridProduct").jqxGrid('begincelledit', rowindex, "quantity");
                    }
                }]
            );
            return true;
		}else{
			var quantity = data.quantity;
	    	var quantityToDelivery = data.quantityToDelivery;
	        if(quantityToDelivery == 0 || quantityToDelivery == undefined){
	            $('#jqxgridProduct').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
	                "label" : "OK",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#jqxgridProduct").jqxGrid('begincelledit', rowindex, "quantityToDelivery");
	                    }
	                }]
	            );
	            return true;
	        }else{
	        	if(quantityToDelivery > quantity){
	                $('#jqxgridProduct').jqxGrid('unselectrow', rowindex);
	                bootbox.dialog("${uiLabelMap.QuantityCantNotGreateThanQuantityNeedTransfer}", [{
	                    "label" : "OK",
	                    "class" : "btn btn-primary standard-bootbox-bt",
	                    "icon" : "fa fa-check",
	                    "callback": function() {
	                            $("#jqxgridProduct").jqxGrid('begincelledit', rowindex, "quantityToDelivery");
	                        }
	                    }]
	                );
	                return true;
	            }
	        }
		}
	}
	$("#jqxgrid2").on("bindingComplete", function (event) {
		var rows = $("#jqxgrid2").jqxGrid('getrows');
		var total = 0;
		for (var i=0; i<rows.length; i++){
			total = total + rows[i].weight;
		}
		if (rows.length > 0){
			var desc = "";
			for(var i = 0; i < weightUomData.length; i++){
				if(weightUomData[i].uomId == rows[0].weightUomId){
					desc = weightUomData[i].description;
				}
			}
			var value = parseInt(total); 
			$('#totalWeight').text(value.toLocaleString('${localeStr}') + " " +(desc));
		} else {
			$('#totalWeight').text(total);
		}
	});
	function functionAfterUpdate(){
		$.ajax({
	           type: "POST",
	           url: "getDeliveryById",
	           data: {'deliveryId': selectedDlvId},
	           dataType: "json",
	           async: false,
	           success: function(response){
	        	   deliveryDT = response;
	           },
	           error: function(response){
	             alert("Error:" + response);
	           }
	    });
		//Create statusIdDT
		var statusDT;
		for(var i = 0; i < statusData.length; i++){
			if(deliveryDT.statusId == statusData[i].statusId){
				statusDT = 	statusData[i].description;
				break;
			}
		}
		$("#statusIdDT").text(statusDT);
		
		$("#jqxgrid2").jqxGrid('updatebounddata');
	}
	function updateJqxgridProduct(){
		$("#jqxgridProduct").jqxGrid("updatebounddata");
	}
	
	var isStorekeeperFrom = false;
	var isStorekeeperTo = false;
	var isSpecialist = false;
	function checkRoleByDelivery(deliveryId){
		$.ajax({
               type: "POST",
               url: "checkRoleByDelivery",
               data: {'deliveryId': deliveryId},
               dataType: "json",
               async: false,
               success: function(response){
            	   isStorekeeperFrom = response.isStorekeeperFrom;
            	   isStorekeeperTo = response.isStorekeeperTo;
            	   isSpecialist = response.isSpecialist;
               },
               error: function(response){
                 alert("Error:" + response);
               }
        });
	}
	$('#actualStartDate').jqxDateTimeInput({width: 200});
	$('#actualStartDate').hide();
	$('#actualArrivalDate').jqxDateTimeInput({width: 200});
	$('#actualArrivalDate').hide();
	var listInv = [];
    var tmpValue;
    var glDeliveryId;
    var glOriginFacilityId;
    var glDeliveryStatusId;
	function showDetailPopup(deliveryId){
		checkRoleByDelivery(deliveryId);
		selectedDlvId = deliveryId;
		//Create theme
		$.jqx.theme = 'olbius';
		theme = $.jqx.theme;
		
		//Cache delivery
		$.ajax({
	           type: "POST",
	           url: "getDeliveryById",
	           data: {'deliveryId': deliveryId},
	           dataType: "json",
	           async: false,
	           success: function(response){
	        	   deliveryDT = response;
	        	   $.ajax({
                       type: "POST",
                       url: "getInvByTransferAndDlv",
                       data: {'facilityId':deliveryDT.originFacilityId, 'deliveryId': deliveryDT.deliveryId},
                       dataType: "json",
                       async: false,
                       success: function(response){
                           listInv = response.listData
                       },
                       error: function(response){
                         alert("Error:" + response);
                       }
                   });
	           },
	           error: function(response){
	             alert("Error:" + response);
	           }
	    });
		//Set deliveryId for target print pdf
		var href = "/delys/control/delivery.pdf?deliveryId=";
		href += deliveryId
		$("#printPDF").attr("href", href);
		
		//Create deliveryIdDT
		$("#deliveryIdDT").text(deliveryDT.deliveryId);
		glOriginFacilityId = deliveryDT.originFacilityId;
        glDeliveryStatusId = deliveryDT.statusId;
		//Create statusIdDT
		var statusDT;
		for(var i = 0; i < statusData.length; i++){
			if(deliveryDT.statusId == statusData[i].statusId){
				statusDT = 	statusData[i].description;
				break;
			}
		}
		$("#statusIdDT").text(statusDT);
		
		//Create orderIdDT 
		$("#transferIdDT").text(deliveryDT.transferId);
		
		//Create originFacilityIdDT
		$("#originFacilityIdDT").text(deliveryDT.originFacilityName);
		
		//Create destFacilityIdDT
		$("#destFacilityIdDT").text(deliveryDT.destFacilityName);
		
		//Create createDateDT
		var createDate = new Date(deliveryDT.createDate);
		if (createDate.getMonth()+1 < 10){
			if (createDate.getDate() < 10){
				$("#createDateDT").text('0' + createDate.getDate() + '/0' + (createDate.getMonth()+1) + '/' + createDate.getFullYear());
			} else {
				$("#createDateDT").text(createDate.getDate() + '/0' + (createDate.getMonth()+1) + '/' + createDate.getFullYear());
			}
		} else {
			if (createDate.getDate() < 10){
				$("#createDateDT").text('0' + createDate.getDate() + '/' + (createDate.getMonth()+1) + '/' + createDate.getFullYear());
			} else {
				$("#createDateDT").text(createDate.getDate() + '/' + (createDate.getMonth()+1) + '/' + createDate.getFullYear());
			}
		}
		
		//Create destContactMechIdDT
		$("#destContactMechIdDT").text(deliveryDT.originAddress);
		
		//Create originContactMechIdDT
		$("#originContactMechIdDT").text(deliveryDT.destAddress);
		
		//Create deliveryDateDT
		var deliveryDate = new Date(deliveryDT.deliveryDate);
		if (deliveryDate.getMonth()+1 < 10){
			if (deliveryDate.getDate() < 10){
				$("#deliveryDateDT").text('0'+ deliveryDate.getDate() + '/0' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear());
			} else {
				$("#deliveryDateDT").text(deliveryDate.getDate() + '/0' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear());
			}
		} else {
			if (deliveryDate.getDate() < 10){
				$("#deliveryDateDT").text('0' + deliveryDate.getDate() + '/' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear());
			} else {
				$("#deliveryDateDT").text(deliveryDate.getDate() + '/' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear());
			}
		}
		
		//Create Grid
        var tmpS = $("#jqxgrid2").jqxGrid('source');
        tmpS._source.url = "jqxGeneralServicer?sname=getListDeliveryItem&deliveryId=" + deliveryId;
        $("#jqxgrid2").jqxGrid('source', tmpS);
        if ((!isSpecialist && !isStorekeeperFrom && !isStorekeeperTo) || deliveryDT.statusId == "DLV_DELIVERED" || (isStorekeeperTo && !isStorekeeperFrom && deliveryDT.statusId != "DLV_EXPORTED") || (!isStorekeeperTo && isStorekeeperFrom && deliveryDT.statusId != "DLV_CREATED")){
        	$('#alterSave2').hide();
    	}
      //Create pathScanfile
		var path = "";
		if (deliveryDT.pathScanFile){
			$('#scanLabel').html("");
			$('#scanLabel').append('${uiLabelMap.FileScan}:');
			path = deliveryDT.pathScanFile;
			var fileName = path.split('/')[7]; 
			$('#scanfile').html("");
			$('#scanfile').append("<a href="+path+" target='_blank'><i class='fa-file-text-o'></i>'"+fileName+"'</a>");
		} else {
			if ("DLV_EXPORTED" == deliveryDT.statusId){
				$('#scanLabel').html("");
				$('#scanLabel').append('${uiLabelMap.FileScan}:');
				$('#scanfile').html("");
				$('#scanfile').append("<a id='linkId' onclick='showAttachFilePopup()'><i class='icon-upload'></i>${uiLabelMap.AttachFileScan}</a>");
			} else {
				$('#scanLabel').html("");
				$('#scanfile').html("");
			}
		}
        if ("DLV_CREATED" == deliveryDT.statusId){
			$('#actualStartLabel').html("");
			$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
			$('#actualStartDate').show();
			$('#actualStartDateDis').hide();
			$('#actualArrivalLabel').hide();
			$('#actualArrivalDate').hide();
			$('#actualArrivalDateDis').hide();
		}
		if ("DLV_EXPORTED" == deliveryDT.statusId){
			$('#actualArrivalLabel').show();
			$('#actualArrivalLabel').html("");
			$('#actualArrivalLabel').append('${uiLabelMap.ActualDeliveredDate}:');
			
			$('#actualArrivalDate').show();
			$('#actualStartDate').hide();
			
			$('#actualArrivalDateDis').hide();
			
			$('#actualStartLabel').show();
			$('#actualStartLabel').html("");
			$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
			
			$('#actualStartDateDis').show();
			$('#actualStartDateDis').html("");
			var date = deliveryDT.actualStartDate;
			var temp = date.split(" ");
			var d = temp[0].split("-");
			$('#actualStartDateDis').append(d[2]+'/'+d[1]+'/'+d[0]);
		}
		if ("DLV_DELIVERED" == deliveryDT.statusId){
			$('#actualStartDate').hide();
			$('#actualArrivalDate').hide();
			
			$('#actualStartLabel').show();
			$('#actualStartLabel').html("");
			$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
			
			$('#actualStartDateDis').show();
			$('#actualStartDateDis').html("");
			var date = deliveryDT.actualStartDate;
			var temp = date.split(" ");
			var d = temp[0].split("-");
			$('#actualStartDateDis').append(d[2]+'/'+d[1]+'/'+d[0]);
			
			$('#actualArrivalLabel').show();
			$('#actualArrivalLabel').html("");
			$('#actualArrivalLabel').append('${uiLabelMap.ActualDeliveredDate}:');
			
			$('#actualArrivalDateDis').show();
			$('#actualArrivalDateDis').html("");
			var arrDate = deliveryDT.actualArrivalDate;
			var temp2 = arrDate.split(" ");
			var d2 = temp[0].split("-");
			$('#actualArrivalDateDis').append(d2[2]+'/'+d2[1]+'/'+d2[0]);
		}
        
		//Open Window
		$("#popupDeliveryDetailWindow").jqxWindow('open');
	}
    $("#alterCancel2").click(function () {
       $("#popupDeliveryDetailWindow").jqxWindow('close'); 
    });
    $("#alterSave2").click(function () {
    	var row;
        //Get List Delivery Item
        var selectedIndexs = $('#jqxgrid2').jqxGrid('getselectedrowindexes');
        if(selectedIndexs.length == 0){
            bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
            return false;
        }
        if("DLV_EXPORTED" == glDeliveryStatusId && !pathScanFile){
            bootbox.dialog("${uiLabelMap.MustUploadScanFile}!", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
            return false;
        }
        bootbox.confirm("${uiLabelMap.DAAreYouSureSave}",function(result){ 
            if(result){  
                $("#popupDeliveryDetailWindow").jqxWindow('close');
        	    var listDeliveryItems = [];
        	    var curDeliveryId = null;
                for(var i = 0; i < selectedIndexs.length; i++){
                    var data = $('#jqxgrid2').jqxGrid('getrowdata', selectedIndexs[i]);
                    var map = {};
                    // Make sure data is completed
                    // FIXME create detail message for following cases
                    /*if(data.statusId == 'DELI_ITEM_EXPORTED'){
                        if(data.actualDeliveredQuantity == 0){
                            bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
                                "label" : "${uiLabelMap.CommonOk}",
                                "class" : "btn btn-primary standard-bootbox-bt",
                                "icon" : "fa fa-check",
                                }]
                            );
                            return false;
                        }
                    }else */
                    if(data.statusId == 'DELI_ITEM_CREATED'){
                        if(data.inventoryItemId == null && data.actualExportedQuantity == 0 && data.actualDeliveredQuantity==0){
                            bootbox.dialog("${uiLabelMap.DItemMissingFieldsExp}", [{
                                "label" : "${uiLabelMap.CommonOk}",
                                "class" : "btn btn-primary standard-bootbox-bt",
                                "icon" : "fa fa-check",
                                }]
                            );
                            return false;
                        }
                    }
                    map.fromTransferId = data.fromTransferId;
                    map.fromTransferItemSeqId = data.fromTransferItemSeqId;
                    map.inventoryItemId = data.inventoryItemId;
                    map.deliveryId = data.deliveryId;
                    map.deliveryItemSeqId = data.deliveryItemSeqId;
                    map.actualExportedQuantity = data.actualExportedQuantity;
                    map.actualDeliveredQuantity = data.actualDeliveredQuantity;
                    curDeliveryId = data.deliveryId;
                    listDeliveryItems[i] = map;
                }
                $('#jqxgrid2').jqxGrid('showloadelement');
                var listDeliveryItems = JSON.stringify(listDeliveryItems);
                var actualStartDateTmp;
                var actualArrivalDateTmp;
                if ("DLV_CREATED" == glDeliveryStatusId){
                	var tmp = $('#actualStartDate').jqxDateTimeInput('getDate');
                	if (tmp){
                		actualStartDateTmp = tmp.getTime();
                	}
                }
                if ("DLV_EXPORTED" == glDeliveryStatusId){
                	var tmp = actualArrivalDateTmp = $('#actualArrivalDate').jqxDateTimeInput('getDate');
                	if (tmp){
                		actualArrivalDateTmp = tmp.getTime();
                	}
                }
                row = { 
                        listDeliveryItems:listDeliveryItems,
                        pathScanFile: pathScanFile,
                        deliveryId: curDeliveryId,
                        actualStartDate: actualStartDateTmp,
                    	actualArrivalDate: actualArrivalDateTmp,
                      };
                // call Ajax request to Update Exported or Delivered value
                $.ajax({
                    type: "POST",
                    url: "updateDeliveryItemList",
                    data: row,
                    dataType: "json",
                    async: false,
                    success: function(data){
                        $('#jqxgrid').jqxGrid('updatebounddata');
                    },
                    error: function(response){
                        $('#jqxgrid').jqxGrid('hideloadelement');
                    }
                });
                displayEditSuccessMessage('jqxgrid');
            }
        });
    });
</script>
<script type="text/javascript">
$("#popupDeliveryDetailWindow").jqxWindow({
    maxWidth: 1500, minWidth: 945, modalZIndex: 10000, zIndex:10000, minHeight: 585, maxHeight: 1200, resizable: true, cancelButton: $("#alterCancel2"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
});
initGridjqxgrid2();
<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_ITEM_STATUS"), null, null, null, false)>
var dlvItemStatusData = [];
<#list statuses as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.get("description", locale)) />
	row['statusId'] = '${item.statusId}';
	row['description'] = '${description}';
	dlvItemStatusData[${item_index}] = row;
</#list>
<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
var quantityUomData = [];
<#list uoms as item>
	var row = {};
	<#assign description = StringUtil.wrapString(item.get("description", locale)) />
	row['uomId'] = '${item.uomId}';
	row['description'] = '${description}';
	quantityUomData[${item_index}] = row;
</#list>

$('#popupDeliveryDetailWindow').on('open', function (event) {
});

$('#popupDeliveryDetailWindow').on('close', function (event) { 
	if($("#jqxgrid").is('*[class^="jqx"]')){
		$("#jqxgrid").jqxGrid('updatebounddata');
	}
	if($("#jqxgridDlv").is('*[class^="jqx"]')){
		$("#jqxgridDlv").jqxGrid('updatebounddata');
	}
	$('#jqxgrid2').jqxGrid('clearselection');
});
function functionAfterUpdate2(){
    var tmpS = $("#jqxgrid2").jqxGrid('source');
    tmpS._source.url = "jqxGeneralServicer?sname=getListDeliveryItem&deliveryId=" + glDeliveryId;
    $("#jqxgrid2").jqxGrid('source', tmpS);
}
function rowselectfunction(event){
    if(typeof event.args.rowindex != 'number'){
        var tmpArray = event.args.rowindex;
        for(i = 0; i < tmpArray.length; i++){
            if(checkRequiredData2(tmpArray[i])){
                $('#jqxgrid2').jqxGrid('clearselection');
                break; // Stop for first item
            }
        }
    }else{
        if(checkRequiredData2(event.args.rowindex)){
            $('#jqxgrid2').jqxGrid('unselectrow', event.args.rowindex);
        }
    }
}
function checkRequiredData2(rowindex){
    var data = $('#jqxgrid2').jqxGrid('getrowdata', rowindex);
    if(data.statusId == 'DELI_ITEM_EXPORTED'){
        if(data.actualDeliveredQuantity == 0){
            $('#jqxgrid2').jqxGrid('unselectrow', rowindex);
            bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                        $("#jqxgrid2").jqxGrid('begincelledit', rowindex, "actualDeliveredQuantity");
                    }
                }]
            );
            return true;
        }
        if(data.actualDeliveredQuantity > data.actualExportedQuantity){
            $('#jqxgrid2').jqxGrid('unselectrow', rowindex);
            bootbox.dialog("${uiLabelMap.LogCheckActuallyExportedGreaterRealCommunication}", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                        $("#jqxgrid2").jqxGrid('begincelledit', rowindex, "actualDeliveredQuantity");
                    }
                }]
            );
            return true;
        }
    }
    if(data.statusId == 'DELI_ITEM_DELIVERED'){
        bootbox.dialog("${uiLabelMap.DLYItemComplete}", [{
            "label" : "OK",
            "class" : "btn btn-primary standard-bootbox-bt",
            "icon" : "fa fa-check",
            }]
        );
        return true;
    }
    if(data.statusId == 'DELI_ITEM_CREATED' && (data.inventoryItemId == null || data.actualExportedQuantity == 0)){
        if(data.inventoryItemId == null){
            bootbox.dialog("${uiLabelMap.DItemMissingFieldsExp}", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                    $("#jqxgrid2").jqxGrid('begincelledit', rowindex, "inventoryItemId");
                }
                }]
            );
            return true;
        }else{
            bootbox.dialog("${uiLabelMap.DItemMissingFieldsExp}", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                "callback": function() {
                    $("#jqxgrid2").jqxGrid('begincelledit', rowindex, "actualExportedQuantity");
                }
            }]
            );
            return true;
        }
    }
    return false;
}
function confirmExportNumber(rowid, rowdata){
    var tmpRowData = new Object();
    tmpRowData.productId = rowdata.productId;
    tmpRowData.quantityUomId = rowdata.quantityUomId;
    tmpRowData.fromOrderId = rowdata.fromOrderId;
    tmpRowData.fromOrderItemSeqId = rowdata.fromOrderItemSeqId;
    tmpRowData.inventoryItemId = rowdata.inventoryItemId;
    tmpRowData.deliveryId = rowdata.deliveryId;
    tmpRowData.deliveryItemSeqId = rowdata.deliveryItemSeqId;
    tmpRowData.actualExportedQuantity = rowdata.actualExportedQuantity;
    tmpRowData.actualDeliveredQuantity = rowdata.actualDeliveredQuantity;
    tmpRowData.actualExpireDate = rowdata.actualExpireDate;
    tmpRowData.expireDate = rowdata.expireDate;
    for(i = 0; i < listInv.length;i++){
        if(listInv[i].productId == tmpRowData.productId){
            var tmpDate = new Date(listInv[i].expireDate.time);
            var tmpValue = new Object();
            tmpRowData.expireDate =  $.datepicker.formatDate('dd/mm/yy', tmpDate);
            break;
        }
    }
    var strMsg;
    if(tmpRowData.actualDeliveredQuantity != null && tmpRowData.actualDeliveredQuantity > 0){
        strMsg = "${uiLabelMap.ConfirmToDelivery} #" +  tmpRowData.productId + ' ${uiLabelMap.WithExpireDate} ' + tmpRowData.expireDate + ' ${uiLabelMap.LogIs} ' +
        tmpRowData.actualDeliveredQuantity + ' [' + tmpRowData.quantityUomId + '] ?';
    }else{
        strMsg = "${uiLabelMap.ConfirmToExport} #" +  tmpRowData.productId + ' ${uiLabelMap.WithExpireDate} ' + tmpRowData.expireDate + ' ${uiLabelMap.LogIs} ' +
        tmpRowData.actualExportedQuantity + ' [' + tmpRowData.quantityUomId + '] ?';
    }
    bootbox.confirm(strMsg, function(result) {
        if(result){
            editPending = true;
            $("#jqxgrid2").jqxGrid('updaterow', rowid, tmpRowData);
        }else{
            editPending = false;
        }
    });
    
}
<#assign storeKeeper = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "LOG_STOREKEEPER", "partyId", userLogin.partyId)), null, null, null, false)/>
var listFacilityManage = [];
<#list storeKeeper as item>
	listFacilityManage.push('${item.facilityId}');
</#list>
<#assign specialist = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "LOG_SPECIALIST", "partyId", userLogin.partyId)), null, null, null, false)/>
<#list specialist as item>
listFacilityManage.push('${item.facilityId}');
</#list>
</script>