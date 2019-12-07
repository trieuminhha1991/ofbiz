<script type="text/javascript" src="/delys/images/js/util/DateUtil.js" ></script>
<#assign transferType = "true"/>
<script>

	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = [];
	<#list weightUoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale)) />
		row['uomId'] = "${item.uomId}";
		row['description'] = "${description?if_exists}";
		weightUomData[${item_index}] = row;
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
	
	<#assign parties = delegator.findList("PartyNameView", null, null, null, null, false) />
	var partyData = [];
	<#list parties as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.firstName?if_exists) + StringUtil.wrapString(item.middleName?if_exists) + StringUtil.wrapString(item.lastName?if_exists) + StringUtil.wrapString(item.groupName?if_exists)>
		row['partyId'] = "${item.partyId}";
		row['description'] = "${description?if_exists}";
		partyData[${item_index}] = row;
	</#list>
	
	<#assign postalAddresses = delegator.findList("PostalAddress", null, null, null, null, false)>
	var pstAddrData = [];
	<#list postalAddresses as item>
		var row = {};
		<#if item.address1?has_content>
			<#assign description = StringUtil.wrapString(item.address1?if_exists)/>
		</#if>
		row['contactMechId'] = "${item.contactMechId}";
		row['description'] = "${description?if_exists}";
		pstAddrData[${item_index}] = row;
	</#list>
	
	<#assign prodStores = delegator.findList("ProductStore", null, null, null, null, false)>
	var prodStoreData = [];
	<#list prodStores as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.storeName?if_exists)/>
		row['productStoreId'] = "${item.productStoreId?if_exists}";
		row['description'] = "${item.storeName?if_exists}";
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
		<#assign description = StringUtil.wrapString(item.description?if_exists)/>
		row['deliveryTypeId'] = "${item.deliveryTypeId?if_exists}";
		row['description'] = "${description?if_exists}";
		deliveryTypeData[${item_index}] = row;
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
	<#assign columnlist="{ text: '${uiLabelMap.TransferNoteId}', dataField: 'deliveryId', width: 150, filtertype:'input', editable:false, 
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgrid').jqxGrid('getrowdata', row);
						var orderIdTemp = data.orderId;
						return '<span><a href=\"javascript:void(0);\" onclick=\"showDetailPopup(&#39;' + value + '&#39;,&#39;'+ orderIdTemp +'&#39;)\"> ' + value  + '</a></span>';
					 }
					},
					{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 170, editable:true, columntype: 'dropdownlist',
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
					 { text: '${uiLabelMap.FacilityFrom}', dataField: 'originFacilityId', width: 200, editable:false,
						 cellsrenderer: function(row, column, value){
							 for(var i = 0; i < faciData.length; i++){
								 if(faciData[i].facilityId == value){
									 return '<span title=' + value + '>' + faciData[i].description + '</span>'
								 }
							 }
						 },
						 filtertype: 'input'
					 },
					 { text: '${uiLabelMap.FacilityTo}', dataField: 'destFacilityId', editable:false,
						 cellsrenderer: function(row, column, value){
							 for(var i = 0; i < faciData.length; i++){
								 if(faciData[i].facilityId == value){
									 return '<span title=' + value + '>' + faciData[i].description + '</span>'
								 }
							 }
						 },
						 filtertype: 'input'
					 },
					 { text: '${uiLabelMap.createDate}', dataField: 'createDate', width: 200, cellsformat: 'dd/MM/yyyy', filtertype: 'date', editable:false},
					 { text: '${uiLabelMap.TransferDate}', dataField: 'deliveryDate', width: 200, cellsformat: 'dd/MM/yyyy', filtertype: 'date', editable:false},
					 "/>
	<#assign dataField="[{ name: 'deliveryId', type: 'string' },
					{ name: 'statusId', type: 'string' },
                 	{ name: 'originFacilityId', type: 'string' },
                 	{ name: 'destFacilityId', type: 'string' },
					{ name: 'createDate', type: 'date', other: 'Timestamp' },
					{ name: 'deliveryDate', type: 'date', other: 'Timestamp' },
		 		 	]"/>
	<@jqGrid filtersimplemode="true" id="jqxgrid" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="" editable="false" 
		 url="jqxGeneralServicer?sname=getListTransferDelivery&transferId=${parameters.transferId?if_exists}" createUrl="jqxGeneralServicer?sname=createTransferDelivery&jqaction=C" editmode="dblclick"
		 addColumns="listTransferItems(java.util.List);transferId;statusId;destFacilityId;originFacilityId;deliveryDate(java.sql.Timestamp);deliveryTypeId[DELIVERY_TRANSFER];" 	 
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
		<div class='row-fluid' style="margin-left: 30px">
			<div class='span3' style="text-align: right;">
				${uiLabelMap.deliveryIdDT}:
			</div>
			<div class='span3 green-label'>
				<div id="deliveryIdDT">
				</div>
			</div>
			<div class='span2' style="text-align: right;">
				${uiLabelMap.statusIdDT}:
			</div>
			<div class='span4 green-label'>
				<div id="statusIdDT">
				</div>
			</div>
		</div>
		<div class='row-fluid' style="margin-left: 30px">
			<div class='span3' style="text-align: right;">
				${uiLabelMap.FacilityFrom}:
			</div>
			<div class='span3 green-label'>
				<div id="originFacilityIdDT">
				</div>
			</div>
			<div class='span2' style="text-align: right;">
				${uiLabelMap.FacilityTo}:
			</div>
			<div class='span4 green-label'>
				<div id="destFacilityIdDT">
				</div>
			</div>
		</div>
		<div class='row-fluid' style="margin-left: 30px">
			<div class='span3' style="text-align: right; ">
				${uiLabelMap.OriginContactMech}:
			</div>
			<div class='span3 green-label'>
				<div id="originContactMechIdDT">
				</div>
			</div>
			<div class='span2' style="text-align: right;">
				${uiLabelMap.DestinationContactMech}:
			</div>
			<div class='span4 green-label'>
				<div id="destContactMechIdDT">
				</div>
			</div>
		</div>
		<div class='row-fluid' style="margin-left: 30px">
			<div class='span3' style="text-align: right;">
				${uiLabelMap.createDate}:
			</div>
			<div class='span3 green-label'>
				<div id="createDateDT">
				</div>
			</div>
			<div class='span2' style="text-align: right;">
				${uiLabelMap.deliveryDate}:
			</div>
			<div class='span4 green-label'>
				<div id="deliveryDateDT">
				</div>
			</div>
		</div>
		<div class="row-fluid" style="margin-left: 30px">
		    <div class="span3" style="text-align: right;">${uiLabelMap.TotalWeight}:</div>
		    <div class="span3"><div id="totalWeight" class="green-label"></div></div>
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
	var listInv = [];
    var tmpValue;
    var glDeliveryId;
    var glOriginFacilityId;
    var glDeliveryStatusId;
    
	function showDetailPopup(deliveryId, transferId){
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
                       data: {'transferId': transferId, 'facilityId':deliveryDT.originFacilityId, 'deliveryId': deliveryDT.deliveryId},
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
//		var createDate = formatDate(deliveryDT.createDate);
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