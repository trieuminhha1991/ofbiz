<script type="text/javascript" src="/delys/images/js/util/DateUtil.js" ></script>
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script>
	var alterData = new Object();
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false) />
	var statusData = [];
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale))>
		row['statusId'] = "${item.statusId}";
		row['description'] = "${description?if_exists}";
		statusData[${item_index}] = row;
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
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("abbreviation", locale	)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${description?if_exists}";
		weightUomData[${item_index}] = row;
	</#list>
	
	<#assign storeKeeper = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "LOG_STOREKEEPER", "partyId", userLogin.partyId)), null, null, null, false)/>
	var listFacilityManage = [];
	<#list storeKeeper as item>
		listFacilityManage.push('${item.facilityId}');
	</#list>
	<#assign specialist = delegator.findList("FacilityParty", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "LOG_SPECIALIST", "partyId", userLogin.partyId)), null, null, null, false)/>
	<#list specialist as item>
		listFacilityManage.push('${item.facilityId}');
	</#list>
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_ITEM_STATUS"), null, null, null, false)>
	var dlvItemStatusData = [];
	<#list statuses as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		row['statusId'] = '${item.statusId}';
		row['description'] = '${description}';
		dlvItemStatusData[${item_index}] = row;
	</#list>
</script>
<div id="deliveries-tab" class="tab-pane">
	<#assign columnlist="
					{	text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					    	return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},			
					{ text: '${uiLabelMap.TransferNoteId}', dataField: 'deliveryId', width: 150, filtertype:'input', editable:false, pinned: true,
					cellsrenderer: function(row, column, value){
						 return '<span><a onclick=\"showDetailPopup(&#39;' + value + '&#39;)\"' + '> ' + value  + '</a></span>'
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
	<@jqGrid filtersimplemode="true" id="jqxgrid" addrefresh="true" usecurrencyfunction="true" addType="popup" 
		dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="" editable="false" 
		url="jqxGeneralServicer?sname=getListTransferDelivery" createUrl="" editmode="dblclick"
	/>
</div>
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
						${uiLabelMap.TransferId}:
					</div>
					<div class='span7 green-label'>
						<div id="transferIdDT">
						</div>
					</div>
				</div>
			</div>
			<div class="span6">
			</div>
		</div>
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
	                <button id="alterCancel2" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	                <button id="alterSave2" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
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

<script type="text/javascript">
	$("#popupDeliveryDetailWindow").jqxWindow({
	    maxWidth: 1500, minWidth: 945, modalZIndex: 10000, zIndex:10000, minHeight: 600, maxHeight: 1200, resizable: true, cancelButton: $("#alterCancel2"), keyboardNavigation: true, keyboardCloseKey: 15,  isModal: true, autoOpen: false, modalOpacity: 0.7, theme:theme           
	});
	initGridjqxgrid2();
	
	$('#document').ready(function(){
		$('#jqxFileScanUpload').jqxWindow({ width: 400, modalZIndex: 10000, height: 220, isModal: true, autoOpen: false });
		initAttachFile();
	});
	
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var listImage = [];
	var pathScanFile = null;
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
	})
	
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
	
	$('#actualStartDate').jqxDateTimeInput({width: 200});
	$('#actualStartDate').hide();
	$('#actualArrivalDate').jqxDateTimeInput({width: 200});
	$('#actualArrivalDate').hide();
	
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
		
		//Create transferIdDT 
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
			
			$('#actualStartLabel').show();
			$('#actualStartLabel').html("");
			$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
			
			$('#actualArrivalDateDis').hide();
			
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
	function showAttachFilePopup(){
		$('#jqxFileScanUpload').jqxWindow('open');
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
	$('#popupDeliveryDetailWindow').on('close', function (event) { 
		$('#jqxgrid2').jqxGrid('clearselection');
	});
</script>
