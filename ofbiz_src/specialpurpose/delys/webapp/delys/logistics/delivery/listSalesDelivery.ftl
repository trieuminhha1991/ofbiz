<script type="text/javascript" src="/delys/images/js/util/DateUtil.js" ></script>
<script>
	
    <#assign uomConversions = delegator.findList("UomConversion", null, null, null, null, false) />
	var uomConvertData = new Array();
	<#list uomConversions as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['uomIdTo'] = "${item.uomIdTo}";
		row['conversionFactor'] = "${item.conversionFactor}";
		uomConvertData[${item_index}] = row;
	</#list>
    
	
    // FIXME Remove all cached data, replace by using ajax request and get Json data.
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false)/>
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get('description', locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${description}";
		statusData[${item_index}] = row;
	</#list>
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var uomData = [];
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)/>
		row['quantityUomId'] = "${item.uomId?if_exists}";
		row['description'] = "${description?if_exists}";
		uomData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign abbreviation = StringUtil.wrapString(item.get("abbreviation", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${abbreviation?if_exists}";
		weightUomData[${item_index}] = row;
	</#list>
	
	<#assign postalAddresses = delegator.findList("PostalAddress", null, null, null, null, false)>
	var pstAddrData = [];
	<#list postalAddresses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.address1?default(''))/>
		row['contactMechId'] = "${item.contactMechId}";
		row['description'] = "${description}";
		pstAddrData[${item_index}] = row;
	</#list>
	
	<#assign prodStores = delegator.findList("ProductStore", null, null, null, null, false)>
	var prodStoreData = [];
	<#list prodStores as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.storeName?if_exists)/>
		row['productStoreId'] = "${item.productStoreId?if_exists}";
		row['description'] = "${description?if_exists}";
		prodStoreData[${item_index}] = row;
	</#list>
	
	<#assign facis = delegator.findList("Facility", null, null, null, null, false)>
	var faciData = [];
	<#list facis as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = "${item.facilityId?if_exists}";
		row['ownerPartyId']= "${item.ownerPartyId?if_exists}";
		row['description'] = "${description?if_exists}";
		row['productStoreId'] = "${item.productStoreId?if_exists}";
		faciData[${item_index}] = row;
	</#list>
	
	<#assign deliveryTypes = delegator.findList("DeliveryType", null, null, null, null, false)>
	var deliveryTypeData = [];
	<#list deliveryTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get('description' locale)?if_exists)/>
		row['deliveryTypeId'] = "${item.deliveryTypeId?if_exists}";
		row['description'] = "${description?if_exists}";
		deliveryTypeData[${item_index}] = row;
	</#list>
</script>
<#assign localeStr = "VI" />
<#if locale = "en">
	<#assign localeStr = "EN" />
</#if>
<div id="deliveries-tab" class="tab-pane">
	<#assign columnlist="
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},		
					{ text: '${uiLabelMap.DeliveryId}', pinned: true, dataField: 'deliveryId', width: 120, filtertype:'input', editable:false, 
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						var orderIdTemp = data.orderId;
						return '<span><a href=\"javascript:void(0);\" onclick=\"showDetailPopup(&#39;' + value + '&#39;,&#39;'+ orderIdTemp +'&#39;)\"> ' + value  + '</a></span>';
					 }
					},
					{ text: '${uiLabelMap.deliveryDate}', dataField: 'deliveryDate', width: 120, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false},
					{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 120, editable:false, columntype: 'dropdownlist',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < statusData.length; i++){
								if(statusData[i].statusId == value){
									return '<span title=' + value + '>' + statusData[i].description + '</span>'
								}
							}
						},
						cellbeginedit: function (row, datafield, columntype) {
							var data = $('#jqxgrid2').jqxGrid('getrowdata', row);
							if(data.deliveryStatusId == 'DLV_CREATED'){
								tmpEditable = false;
								return true;
							}else{
								tmpEditable = true;
								return false;
							}
						},
						createeditor: function(row, value, editor){
							var statusData = [];
							var row = {};
							row['statusId'] = 'DLV_APPROVED';
							row['description'] = 'Delivery approved';
							statusData[0] = row;
							
							row = {};
							row['statusId'] = 'DLV_CANCELED';
							row['description'] = 'Delivery canceled';
							statusData[1] = row;
							
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
					 { text: '${uiLabelMap.FacilityFrom}', dataField: 'originFacilityId', minwidth: 150, editable:false,
						 cellsrenderer: function(row, column, value){
							 for(var i = 0; i < faciData.length; i++){
								 if(faciData[i].facilityId == value){
									 return '<span title=' + value + '>' + faciData[i].description + '</span>'
								 }
							 }
						 },
						 filtertype: 'input'
					 },
					 { text: '${uiLabelMap.FacilityTo}', dataField: 'destFacilityId', minwidth: 150, editable:false,
						 cellsrenderer: function(row, column, value){
							 for(var i = 0; i < faciData.length; i++){
								 if(faciData[i].facilityId == value){
									 return '<span title=' + value + '>' + faciData[i].description + '</span>'
								 }
							 }
						 },
						 filtertype: 'input'
					 },
					 { text: '${uiLabelMap.createDate}', dataField: 'createDate', width: 150, cellsformat: 'd', filtertype: 'range', editable:false},
					 { text: '${uiLabelMap.DeliveryType}', dataField: 'deliveryTypeId', width: 120, editable:false,
							cellsrenderer: function(row, column, value){
								for(var i = 0; i < deliveryTypeData.length; i++){
									if(deliveryTypeData[i].deliveryTypeId == value){
										return '<span title=' + value + '>' + deliveryTypeData[i].description + '</span>'
									}
								}
							},
							filtertype: 'input'
						 }, 
					 { text: '${uiLabelMap.noNumber}', dataField: 'no', width: 120, filtertype: 'input', editable:false}
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
					{ name: 'createDate', type: 'date', other: 'Timestamp' },
					{ name: 'deliveryDate', type: 'date', other: 'Timestamp' },
					{ name: 'estimatedStartDate', type: 'date', other: 'Timestamp' },
					{ name: 'estimatedArrivalDate', type: 'date', other: 'Timestamp' },
					{ name: 'totalAmount', type: 'number' },
					{ name: 'no', type: 'string' },
		 		 	]"/>

	<@jqGrid filtersimplemode="true" id="jqxgrid" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="" editable="true" 
		 url="jqxGeneralServicer?sname=getListDelivery&deliveryTypeId=${parameters.deliveryTypeId?if_exists}&deliveryId=${parameters.deliveryId?if_exists}" createUrl="" editmode="dblclick"
		 addColumns="" updateUrl="" editColumns=""
		 jqGridMinimumLibEnable="true"/>

</div>
<div id="popupDeliveryDetailWindow">
    <div>${uiLabelMap.GeneralInfo}</div>
	<div style="overflow: hidden;">
	    <div>
			<h4 class="row header smaller lighter blue" style="margin-right:25px !important;margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
				${uiLabelMap.Delivery}
				<a style="float:right;font-size:14px;" id="printPDF" target="_blank" data-rel="tooltip" title="${uiLabelMap.ExportPdf}" data-placement="bottom" data-original-title="${uiLabelMap.ExportPdf}"><i class="fa-file-pdf-o"></i>&nbsp;PDF</a>
			</h4>
			<div class="row-fluid">
			    <div class="span2" style="text-align: right;">${uiLabelMap.deliveryIdDT}:</div>
			    <div class="span4"><div id="deliveryIdDT" class="green-label"></div></div>
			    <div class="span2" style="text-align: right;">${uiLabelMap.statusIdDT}:</div>
			    <div class="span4"><div id="statusIdDT" class="green-label"></div></div>
    		</div>
    		<div class="row-fluid">
    		    <div class="span2" style="text-align: right;">${uiLabelMap.OrderId}:</div>
    		    <div class="span4"><div id="orderIdDT"class="green-label"></div></div>
    		    <div class="span2" style="text-align: right;">${uiLabelMap.noNumber}:</div>
    		    <div class="span4" style="text-align: left;"><div id="noDT" class="green-label"></div></div>
    		</div>
    		<div class="row-fluid">
	    		<div class="span2" style="text-align: right;">${uiLabelMap.Sender}:</div>
			    <div class="span4"><div id="partyIdFromDT" class="green-label"></div></div>
			    <div class="span2" style="text-align: right;">${uiLabelMap.Receiver}:</div>
    		    <div class="span4"><div id="partyIdToDT" class="green-label"></div></div>
    		</div>
    		<div class="row-fluid">
	    		<div class="span2" style="text-align: right;">${uiLabelMap.facility}:</div>
			    <div class="span4"><div id="originFacilityIdDT" class="green-label"></div></div>
			    <div class="span2" style="text-align: right;">${uiLabelMap.destFacilityId}:</div>
			    <div class="span4"><div id="destFacilityIdDT" class="green-label"></div></div>
    		</div>
    		<div class="row-fluid">
    		    <div class="span2" style="text-align: right;">${uiLabelMap.OriginAddress}:</div>
    		    <div class="span4"><div id="originContactMechIdDT" class="green-label"></div></div>
    		    <div class="span2" style="text-align: right;">${uiLabelMap.customerAddress}:</div>
    		    <div class="span4"><div id="destContactMechIdDT" class="green-label"></div></div>
    		</div>
    		<div class="row-fluid">
	    		<div class="span2" style="text-align: right;">${uiLabelMap.createDate}:</div>
			    <div class="span4"><div id="createDateDT" class="green-label"></div></div>
    		    <div class="span2" style="text-align: right;">${uiLabelMap.RequireDeliveryDate}:</div>
    		    <div class="span4"><div id="deliveryDateDT" class="green-label"></div></div>
    		</div>
    		<div class="row-fluid">
			    <div class="span2" style="text-align: right;">${uiLabelMap.TotalWeight}:</div>
			    <div class="span4"><div id="totalWeight" class="green-label"></div></div>
			    <div class="span2" id="actualStartLabel" style="text-align: right;" class="asterisk"></div>
			    <div class="span4"><div id="actualStartDate" class="green-label"></div></div>
			</div>
			<div class="row-fluid">
				<div class="span2" id="scanLabel" style="text-align: right;" class="asterisk"></div>
			    <div class="span4"><div id="scanfile" class="green-label"></div></div>
			    <div class="span2" id="actualArrivalLabel" style="text-align: right;" class="asterisk"></div>
			    <div class="span4"><div id="actualArrivalDate" class="green-label"></div></div>
			</div>
    		<div class="row-fluid">
				<div style="margin-left: 20px"><#include "component://delys/webapp/delys/accounting/appr/listDeliveryItem.ftl"/></div>
			</div>
			<div class="form-action">
	            <div class='row-fluid'>
	                <div class="span12 margin-top20" style="margin-bottom:10px;">
	                    <button id="alterCancel2" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                    <button id="alterSave2" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	                </div>
	            </div>
	        </div>
		</div>
	</div>
</div>


<#assign columnlistFA="{ text: '${uiLabelMap.facilityId}', pinned: true, dataField: 'facilityId', width: 150, editable:false,
                            cellsrenderer: function(row, column, value){
                                var data = $('#jqxgridFAINV').jqxGrid('getrowdata', row);
                                return '<a href=\"javascript:void(0);\" onclick=\"selectFacility(' + \"'\" + data.facilityId + \"'\" + ');\">' + data.facilityId + '</a>';
                            }},
                        { text: '${uiLabelMap.FormFieldTitle_facilityName}', pinned: true, dataField: 'facilityName', editable:false}"/>
<#assign dataFieldFA="[{ name: 'facilityId', type: 'string' },{ name: 'facilityName', type: 'string' }]"/>
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
<script type="text/javascript">
	var listImage = [];
	var pathScanFile = null;
	$('#document').ready(function(){
		$('#jqxFileScanUpload').jqxWindow({ width: 400, modalZIndex: 10000, height: 220, isModal: true, autoOpen: false });
		initAttachFile();
	});
	
	// upload scan file
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
	
	function formatFullDate(value) {
		if (value != undefined && value != null && !(/^\s*$/.test(value))) {
			var dateStr = "";
			dateStr += addZero(value.getFullYear()) + '-';
			dateStr += addZero(value.getMonth()+1) + '-';
			dateStr += addZero(value.getDate()) + ' ';
			dateStr += addZero(value.getHours()) + ':';
			dateStr += addZero(value.getMinutes()) + ':';
			dateStr += addZero(value.getSeconds());
			return dateStr;
		} else {
			return "";
		}
	}
	function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
	}
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
        if("DLV_CONFIRMED" == glDeliveryStatusId && !pathScanFile){
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
                    map.fromOrderId = data.fromOrderId;
                    map.fromOrderItemSeqId = data.fromOrderItemSeqId;
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
                if ("DLV_CONFIRMED" == glDeliveryStatusId){
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
                        $('#jqxgrid2').jqxGrid('updatebounddata');
                        $('#jqxgrid').jqxGrid('updatebounddata');
                    },
                    error: function(response){
                        $('#jqxgrid2').jqxGrid('hideloadelement');
                    }
                });
                displayEditSuccessMessage('jqxgrid');
            }
        });
	});
	$("#alterCancel2").click(function () {
	    $("#popupDeliveryDetailWindow").jqxWindow('close');
	});
</script>
<script type="text/javascript">
	$("#popupDeliveryDetailWindow").jqxWindow({
	    maxWidth: 1500, minWidth: 945, modalZIndex: 10000, zIndex:10000, minHeight: 680, maxHeight: 1200, resizable: true, cancelButton: $("#alterCancel2"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
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
		
	$('#popupDeliveryDetailWindow').on('close', function (event) { 
		if($("#jqxgrid").is('*[class^="jqx"]')){
			$("#jqxgrid").jqxGrid('updatebounddata');
		}
		if($("#jqxgrid").is('*[class^="jqx"]')){
			$("#jqxgrid").jqxGrid('updatebounddata');
		}
		$('#jqxgrid2').jqxGrid('clearselection');
		$('#actualStartLabel').html("");
		$('#actualStartDate').html("");
		$('#actualArrivalLabel').html("");
		$('#actualArrivalDate').html("");
	});
	
	function functionAfterUpdate2(){
	    var tmpS = $("#jqxgrid2").jqxGrid('source');
	    if(typeof tmpS === 'undefined'){
	    	return;
	    }
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
	                "label" : "${uiLabelMap.CommonOk}",
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
	                "label" : "${uiLabelMap.CommonOk}",
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
	            "label" : "${uiLabelMap.CommonOk}",
	            "class" : "btn btn-primary standard-bootbox-bt",
	            "icon" : "fa fa-check",
	            }]
	        );
	        return true;
	    }
	    if(data.statusId == 'DELI_ITEM_CREATED' && (data.inventoryItemId == null || data.actualExportedQuantity == 0)){
	        if(data.inventoryItemId == null){
	            bootbox.dialog("${uiLabelMap.DItemMissingFieldsExp}", [{
	                "label" : "${uiLabelMap.CommonOk}",
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
	                "label" : "${uiLabelMap.CommonOk}",
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
</script>
<script type="text/javascript">
		$("#jqxgrid2").on("bindingComplete", function (event) {
			var rows = $("#jqxgrid2").jqxGrid('getrows');
			var total = 0;
			var defaultWeightUomId = null;
			if (rows.length > 0){
				defaultWeightUomId = rows[0].defaultWeightUomId;
			}
			var desc = "";
			if (rows.length > 0 && defaultWeightUomId != null){
				for (var i=0; i<rows.length; i++){
					if (rows[0].defaultWeightUomId == rows[0].weightUomId){
						total = total + rows[i].weight;
					} else {
						for (var j=0; j<uomConvertData.length; j++){
							if ((uomConvertData[j].uomId == rows[i].baseWeightUomId && uomConvertData[j].uomIdTo == rows[i].defaultWeightUomId) || (uomConvertData[j].uomId == rows[i].defaultWeightUomId && uomConvertData[j].uomIdTo == rows[i].baseWeightUomId)){
								total = total + (uomConvertData[j].conversionFactor)*rows[i].weight;
								break;
							}
						}
					}
				}
				for(var i = 0; i < weightUomData.length; i++){
					if(weightUomData[i].uomId == rows[0].defaultWeightUomId){
						desc = weightUomData[i].description;
					}
				}
				var value = parseInt(total); 
				$('#totalWeight').text(value.toLocaleString('${localeStr}') + " " +(desc));
			} else {
				$('#totalWeight').text(total + " " +(desc));
			}
		});
		function afterAdd(){
			$("#jqxgrid1").jqxGrid('updatebounddata');
		}
		
		// check is specialist or storekeeper of facility export
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
		var listInv = [];
	    var tmpValue;
	    var glDeliveryId;
	    var glOriginFacilityId;
	    var glDeliveryStatusId;
	    
		function showDetailPopup(deliveryId, orderId){
			checkRoleByDelivery(deliveryId);
			var deliveryDT;
			glDeliveryId = deliveryId;
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
	                       url: "getINVByOrderAndDlv",
	                       data: {'orderId': orderId, 'facilityId':deliveryDT.originFacilityId, 'deliveryId': deliveryDT.deliveryId},
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
	        // get datail of party 
	        var listOrderParties = new Array();
	        $.ajax({
	               type: "POST",
	               url: "getOrderPartyNameView",
	               data: {'orderId': orderId},
	               dataType: "json",
	               async: false,
	               success: function(response){
	            	   listOrderParties = response.listOrderParties;
	               },
	               error: function(response){
	                 alert("Error:" + response);
	               }
	        });
	        var partyData = [];
	        for (var m = 0; m < listOrderParties.length; m++){
        		var row = {};
        		var description = listOrderParties[m].groupName;
        		row['partyId'] = listOrderParties[m].partyId;
        		row['description'] = description;
        		partyData[m] = row;
	        }
	        glOriginFacilityId = deliveryDT.originFacilityId;
	        glDeliveryStatusId = deliveryDT.statusId;
			//Set deliveryId for target print pdf
			var href = "/delys/control/delivery.pdf?deliveryId=";
			href += deliveryId
			$("#printPDF").attr("href", href);
			
			//Create deliveryIdDT
			$("#deliveryIdDT").text(deliveryDT.deliveryId);
			
			//Create statusIdDT
			var stName = "";
	        for(i=0; i < statusData.length; i++){
	            if(statusData[i].statusId==deliveryDT.statusId){
	                stName = statusData[i].description;
	            }
	        }
			$("#statusIdDT").text(stName);
			
			//Create orderIdDT 
			$("#orderIdDT").text(deliveryDT.orderId);
			
			
			//Create originFacilityIdDT
			var faName = "";
			for(i=0; i < faciData.length; i++){
			    if(faciData[i].facilityId==deliveryDT.originFacilityId){
			        faName = faciData[i].description;
			    }
			}
			$("#originFacilityIdDT").text(faName);
			
			//Create originFacilityIdDT
			var faName2 = "";
			for(i=0; i < faciData.length; i++){
			    if(faciData[i].facilityId==deliveryDT.destFacilityId){
			        faName2 = faciData[i].description;
			    }
			}
			$("#destFacilityIdDT").text(faName2);
			
			//Create originProductStoreIdDT
			var originProductStoreId = deliveryDT.originProductStoreId;
			var productStoreName;
			for(var i = 0; i < prodStoreData.length; i++){
				if(originProductStoreId == prodStoreData[i].productStoreId){
					productStoreName = prodStoreData[i].description;
					break;
				}
			}
			$("#originProductStoreIdDT").text(productStoreName);
			
			//Create createDateDT
//			var createDate = formatDate(deliveryDT.createDate);
			var createDate = new Date(deliveryDT.createDate);
			if (createDate.getMonth()+1 < 10){
				if (createDate.getDate() < 10){
					$("#createDateDT").text('0'+createDate.getDate() + '/0' + (createDate.getMonth()+1) + '/' + createDate.getFullYear());
				} else {
					$("#createDateDT").text(createDate.getDate() + '/0' + (createDate.getMonth()+1) + '/' + createDate.getFullYear());
				}
			} else {
				if (createDate.getDate() < 10){
					$("#createDateDT").text('0'+createDate.getDate() + '/' + (createDate.getMonth()+1) + '/' + createDate.getFullYear());
				} else {
					$("#createDateDT").text(createDate.getDate() + '/' + (createDate.getMonth()+1) + '/' + createDate.getFullYear());
				}
			}
			
			//Create partyIdToDT
			var partyIdTo = deliveryDT.partyIdTo;
			var partyNameTo;
			for(var i = 0; i < partyData.length; i++){
				if(partyIdTo == partyData[i].partyId){
					partyNameTo = partyData[i].description;
					break;
				}
			}
			$("#partyIdToDT").text(partyNameTo);
			
			//Create destContactMechIdDT
			var destContactMechId = deliveryDT.destContactMechId;
			var destContactMech;
			for(var i = 0; i < pstAddrData.length; i++){
				if(destContactMechId == pstAddrData[i].contactMechId){
					destContactMechId = pstAddrData[i].description;
					break;
				}
			}
			$("#destContactMechIdDT").text(destContactMechId);
			//Create partyIdFromDT
			var partyIdFrom = deliveryDT.partyIdFrom;
			var partyNameFrom;
			for(var i = 0; i < partyData.length; i++){
				if(partyIdFrom == partyData[i].partyId){
					partyNameFrom = partyData[i].description;
					break;
				}
			}
			$("#partyIdFromDT").text(partyNameFrom);
			
			//Create originContactMechIdDT
			var originAddr;
			for(var i = 0; i < pstAddrData.length; i++){
				if(deliveryDT.originContactMechId == pstAddrData[i].contactMechId){
					originAddr = pstAddrData[i].description;
					break;
				}
			}
			$("#originContactMechIdDT").text(originAddr);
			
			//Create deliveryDateDT
			var deliveryDate = new Date(deliveryDT.deliveryDate);
			if (deliveryDate.getMonth()+1 < 10){
				if (deliveryDate.getDate() < 10){
					$("#deliveryDateDT").text('0'+deliveryDate.getDate() + '/0' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear());
				} else {
					$("#deliveryDateDT").text(deliveryDate.getDate() + '/0' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear());
				}
			} else {
				if (deliveryDate.getDate() < 10){
					$("#deliveryDateDT").text('0'+deliveryDate.getDate() + '/' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear());
				} else {
					$("#deliveryDateDT").text(deliveryDate.getDate() + '/' + (deliveryDate.getMonth()+1) + '/' + deliveryDate.getFullYear());
				}
			}
			
			//Create noDT
			$("#noDT").text(deliveryDT.no);
			
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
				if ("DLV_CONFIRMED" == deliveryDT.statusId){
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
				$('#actualArrivalDate').html("");
				$('#actualStartDate').css("border-width", "0px");
				$('#actualStartLabel').html("");
				$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
				$('#actualStartDate').html("");
				$('#actualStartDate').jqxDateTimeInput({width: 200});
				$('#actualStartDate').css("border-width", "1px");
			}
			if ("DLV_CONFIRMED" == deliveryDT.statusId){
				$('#actualArrivalLabel').html("");
				$('#actualArrivalLabel').append('${uiLabelMap.ActualDeliveredDate}:');
				$('#actualArrivalDate').html("");
				$('#actualArrivalDate').jqxDateTimeInput({width: 200});
				
				$('#actualStartLabel').html("");
				$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
				$('#actualStartDate').html("");
				var date = deliveryDT.actualStartDate;
				var temp = date.split(" ");
				var d = temp[0].split("-");
				$('#actualStartDate').append(d[2]+'/'+d[1]+'/'+d[0]);
				$('#actualStartDate').css("border-width", "1px");
				$('#actualArrivalDate').css("border-width", "1px");
			}
			if ("DLV_EXPORTED" == deliveryDT.statusId){
				$('#actualStartDate').css("border-width", "0px");
				$('#actualArrivalDate').css("border-width", "0px");
				$('#actualStartLabel').html("");
				$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
				$('#actualStartDate').html("");
				var date = deliveryDT.actualStartDate;
				var temp = date.split(" ");
				var d = temp[0].split("-");
				$('#actualStartDate').append(d[2]+'/'+d[1]+'/'+d[0]);
			}
			if ("DLV_DELIVERED" == deliveryDT.statusId){
				$('#actualStartDate').css("border-width", "0px");
				$('#actualArrivalDate').css("border-width", "0px");
				$('#actualStartLabel').html("");
				$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
				$('#actualStartDate').html("");
				var date = deliveryDT.actualStartDate;
				var temp = date.split(" ");
				var d = temp[0].split("-");
				$('#actualStartDate').append(d[2]+'/'+d[1]+'/'+d[0]);
				
				$('#actualArrivalLabel').html("");
				$('#actualArrivalLabel').append('${uiLabelMap.ActualDeliveredDate}:');
				$('#actualArrivalDate').html("");
				var arrDate = deliveryDT.actualArrivalDate;
				var temp2 = arrDate.split(" ");
				var d2 = temp[0].split("-");
				$('#actualArrivalDate').append(d2[2]+'/'+d2[1]+'/'+d2[0]);
				
			}
			//Create Grid
			
	        var tmpS = $("#jqxgrid2").jqxGrid('source');
	        tmpS._source.url = "jqxGeneralServicer?sname=getListDeliveryItem&deliveryId=" + deliveryId;
	        $("#jqxgrid2").jqxGrid('source', tmpS);
	        
			//Open Window
			$("#popupDeliveryDetailWindow").jqxWindow('open');
		}
</script>
<script type="text/javascript">
	<#assign localeStr = "VI" />
	<#if locale = "en">
	    <#assign localeStr = "EN" />
	</#if>
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