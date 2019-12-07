<script>
<#assign localeStr = "VI" />
<#if locale = "en">
<#assign localeStr = "EN" />
</#if>
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["SHIPMENT_STATUS", "PURCH_SHIP_STATUS", "DELI_ENTRY_STATUS"]), null, null, null, false)/>
	var statusData = new Array();
	<#list statuses as item>
		var row = {};
		<#assign descStatus = item.get("description" ,locale)/>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${StringUtil.wrapString(descStatus)}';
		statusData[${item_index}] = row;
	</#list>
	<#assign shipmentTypes = delegator.findList("ShipmentType", null, null, null, null, false)/>
	var shipmentTypeData = new Array();
	
	<#list shipmentTypes as item>
		<#assign descType = StringUtil.wrapString(item.get("description", locale))/>
		var row = {};
		row['shipmentTypeId'] = '${item.shipmentTypeId}';
		row['description'] = '${StringUtil.wrapString(descType?if_exists)}';
		shipmentTypeData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		row['uomId'] = '${item.uomId}';
		row['description'] = '${StringUtil.wrapString(item.abbreviation?if_exists)}';
		weightUomData[${item_index}] = row;
	</#list>
	var windowheight = '400px';
	<#assign deliveryEntry = delegator.findOne("DeliveryEntryDetail", Static["org.ofbiz.base.util.UtilMisc"].toMap("deliveryEntryId", parameters.deliveryEntryId?if_exists), false)/>
	<#assign facility = delegator.findOne("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", deliveryEntry.facilityId), false)/>
	
	<#assign deliverers = delegator.findList("DeliveryEntryRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "LOG_DELIVERER", "deliveryEntryId", parameters.deliveryEntryId?if_exists)), null, null, null, false)>
	<#if deliverers?has_content>
		<#assign deliverer = delegator.findOne("PartyNameView", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", deliverers.get(0).get("partyId")), false)>
	</#if>
	
	<#assign drivers = delegator.findList("DeliveryEntryRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "LOG_DRIVER", "deliveryEntryId", parameters.deliveryEntryId?if_exists)), null, null, null, false)>
	<#if drivers?has_content>
		<#assign driver = delegator.findOne("PartyNameView", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", drivers.get(0).get("partyId")), false)>
	</#if>
	
	<#assign carriers = delegator.findList("DeliveryEntryRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("roleTypeId", "CARRIER", "deliveryEntryId", parameters.deliveryEntryId?if_exists)), null, null, null, false)>
	<#if carriers?has_content>
		<#assign carrier = delegator.findOne("PartyNameView", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", carriers.get(0).get("partyId")), false)>
	</#if>
	
	<#assign vehicles = delegator.findList("DeliveryEntryFixedAsset", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("deliveryEntryId", parameters.deliveryEntryId?if_exists), null, null, null, false)>
	<#if vehicles?has_content>
		<#assign vehicle = delegator.findOne("FixedAsset", Static["org.ofbiz.base.util.UtilMisc"].toMap("fixedAssetId", vehicles.get(0).get("fixedAssetId")), false)>
	</#if>
	
	var datetimeDelivery = new Date(${parameters.fromDate?if_exists});
	var datetimeDeliveryString = datetimeDelivery.toString("dd/MM/yyyy");
	
	<#assign uomConversions = delegator.findList("UomConversion", null, null, null, null, false) />
	var uomConvertData = new Array();
	<#list uomConversions as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['uomIdTo'] = "${item.uomIdTo}";
		row['conversionFactor'] = "${item.conversionFactor}";
		uomConvertData[${item_index}] = row;
	</#list>
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var quantityUomData = [];
	<#list uoms as item>
		var row = {};
		<#assign descPackingUom = StringUtil.wrapString(item.description?if_exists)/>
		row['quantityUomId'] = "${item.uomId?if_exists}";
		row['description'] = "${descPackingUom?if_exists}";
		quantityUomData.push(row);
	</#list>
	
	function reponsiveRowDetails(grid, parentElement) {
	    $(window).bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	    $('#sidebar').bind('resize', function() {
	    	$(grid).jqxGrid({ width: "96%"});
	    });
	}
</script>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">	
<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
	<div>
		<div class="row-fluid">
		    <div class="span3" style="text-align: right;">${uiLabelMap.DeliveryEntryId} </div>
		    <div class="span3"><div id="facilityId" class="green-label">${deliveryEntry.deliveryEntryId?if_exists}</div></div>
		    <div class="span2" style="text-align: right;">${uiLabelMap.Status}</div>
		    <div class="span3"><div id="statusId" class="green-label"></div></div>
		</div>
		<div class="row-fluid">
			<div class="span3" style="text-align: right;">${uiLabelMap.CarrierParty} </div>
		    <div class="span3"><div id="facilityId" class="green-label">${carrier.groupName?if_exists}</div></div>
		    <div class="span2" style="text-align: right;">${uiLabelMap.TransportCost} </div>
	    	<div class="span3">
	    		<div id="shipCost" class="green-label">
	    		</div>
		    	<div id="weight" class="green-label hide"></div>
	    	</div>
		</div>
		<#if driver?has_content>
			<div class="row-fluid">
				<div class="span3" style="text-align: right;">${uiLabelMap.ExportFromFacility} </div>
			    <div class="span3"><div id="facilityId" class="green-label">${deliveryEntry.facilityName?if_exists}</div></div>
			    <div class="span2" style="text-align: right;">${uiLabelMap.Driver} </div>
			    <div class="span3"><div id="driverId" class="green-label">${driver.lastName?if_exists} ${driver.middleName?if_exists} ${driver.firstName?if_exists}</div></div>
		    </div>
		    <div class="row-fluid">
			    <div class="span3" style="text-align: right;">${uiLabelMap.Address} </div>
			    <div class="span3"><div id="facilityId" class="green-label">${deliveryEntry.fullName?if_exists}</div></div>
			    <div class="span2" style="text-align: right;">${uiLabelMap.Deliverer} </div>
			    <div class="span3"><div id="delivererId" class="green-label">${deliverer.lastName?if_exists} ${deliverer.middleName?if_exists} ${deliverer.firstName?if_exists}</div></div>
		    </div>
	    <#else>
			<div class="row-fluid">
				<div class="span3" style="text-align: right;">${uiLabelMap.ExportFromFacility} </div>
			    <div class="span3"><div id="facilityId" class="green-label">${deliveryEntry.facilityName?if_exists}</div></div>
			    <div class="span2" style="text-align: right;">${uiLabelMap.Address} </div>
			    <div class="span3"><div id="facilityId" class="green-label">${deliveryEntry.fullName?if_exists}</div></div>
		    </div>
	    </#if>
		<div class="row-fluid">
		    <div class="span3" style="text-align: right;">${uiLabelMap.StartShipDate} </div>
		    <div class="span3"><div id="fromDate" class="green-label"></div></div>
		    
		    <div class="span2" style="text-align: right;">${uiLabelMap.Vehicles}</div>
		    <div class="span3"><div id="vehicle" class="green-label">
		    
		     <#if vehicle?has_content>
			    ${vehicle.fixedAssetId?if_exists} - ${vehicle.fixedAssetName?if_exists}
	    	</#if>
	    	</div>
	    	</div>
		    
	 	</div>
		<div class="row-fluid">
		    <div class="span3" style="text-align: right;">${uiLabelMap.EndShipDate} </div>
		    <div class="span3"><div id="thruDate" class="green-label"></div></div>
		    <div class="span2" style="text-align: right;">${uiLabelMap.Description} </div>
		    <div class="span3"><div id="description" class="green-label">${deliveryEntry.description?if_exists}</div></div>
	 	</div>
 	</div>
</div>
<div>
<#assign initrowdetails = "function (index, parentElement, gridElement, datarecord) {
	var grid = $($(parentElement).children()[0]);
	$(grid).attr('id','jqxgridDetail'+index);
	var gridDetailId = 'jqxgridDetail'+index;
	reponsiveRowDetails(grid);
	if(datarecord.rowDetail){
		var sourceGridDetail =
	    {
	        localdata: datarecord.rowDetail,
	        datatype: 'local',
	        datafields:
	        [
	            { name: 'productId', type: 'string' },
	            { name: 'shipmentId', type: 'string' },
	            { name: 'shipmentItemSeqId', type: 'string' },
	            { name: 'productCode', type: 'string'},
	            { name: 'productName', type: 'string' },
				{ name: 'quantity', type: 'number' },
				{ name: 'quantityCreate', type: 'number' },
				{ name: 'quantityUomId', type: 'string' },
	        ]
	    };
	    var dataAdapterGridDetail = new $.jqx.dataAdapter(sourceGridDetail);
	    grid.jqxGrid({
	        width: '98%',
	        height: '94%',
	        theme: 'olbius',
	        localization: getLocalization(),
	        source: dataAdapterGridDetail,
	        sortable: true,
	        pagesize: 5,
	 		pageable: true,
	 		editable: false,
	        columns: [
					{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
					    groupable: false, draggable: false, resizable: false,
					    datafield: '', columntype: 'number', width: 50,
					    cellsrenderer: function (row, column, value) {
					        return '<div style=margin:4px;>' + (value + 1) + '</div>';
					    }
					},
					{ text: '${uiLabelMap.ProductCode}', datafield: 'productCode', align: 'left', width: 150, pinned: true},
					{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', minwidth: 250,},
					{ text: '${uiLabelMap.Quantity}', datafield: 'quantity', align: 'left', width: 150, cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							if(value != null && value != undefined){
								return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
							}
							return '<span style=\"text-align: right;\">' + 0 + '</span>';
					    },
					},
					{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 150, cellsalign: 'right',
						cellsrenderer: function(row, colum, value){
							var data = $('#'+gridDetailId).jqxGrid('getrowdata', row);
							for (var i = 0; i < quantityUomData.length; i ++) {
								if(quantityUomData[i].quantityUomId == value){
									return '<span style=\"text-align: right;\">' + quantityUomData[i].description + '</span>';
								}
							}
							return '<span style=\"text-align: right;\"></span>';
					    },
					},
                 ]
		    });
		}else {
		}
	}"/>
<#assign dataField="[{ name: 'shipmentId', type: 'string'},
		   { name: 'shipmentTypeId', type: 'string'},
		   { name: 'statusId', type: 'string'},
		   { name: 'primaryOrderId', type: 'string'},
		   { name: 'primaryTransferId', type: 'string'},
		   { name: 'estimatedReadyDate', type: 'date', other: 'Timestamp'},
		   { name: 'estimatedShipDate', type: 'date', other: 'Timestamp'},
		   { name: 'estimatedArrivalDate', type: 'date', other: 'Timestamp'},
		   { name: 'estimatedShipCost', type: 'number'},
		   { name: 'currencyUomId', type: 'string'},
		   { name: 'originFacilityId', type: 'string'},
		   { name: 'destinationFacilityId', type: 'string'},
		   { name: 'originContactMechId', type: 'string'},
		   { name: 'destinationContactMechId', type: 'string'},
		   { name: 'destFacilityName', type: 'string'},
		   { name: 'originAddress', type: 'string'},
		   { name: 'destAddress', type: 'string'},
		   { name: 'totalWeight', type: 'number'},
		   { name: 'defaultWeightUomId', type: 'string'},
		   { name: 'rowDetail', type: 'string'},
		   ]"/>
		   
<#assign columnlist="
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
		    groupable: false, draggable: false, resizable: false,
		    datafield: '', columntype: 'number', width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<span style=margin:4px;>' + (value + 1) + '</span>';
		    }
		},
		{ text: '${uiLabelMap.DeliveryCode}', datafield: 'deliveryId', width: 120, editable: false, pinned: true, cellsrenderer:
		   function(row, colum, value){
		    var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		    if (!value) value = data.shipmentId;
		    return '<span>' + value + '</span>';
		}},
		{ text: '${uiLabelMap.ShipmentType}', datafield: 'shipmentTypeId', minwidth: 150, editable: false, cellsrenderer:
		function(row, colum, value){
		    for(var i = 0; i < shipmentTypeData.length; i++){
				if(shipmentTypeData[i].shipmentTypeId == value){
					return '<span title=' + value + '>' + shipmentTypeData[i].description + '</span>'
				}
			}
		}
		},
		{ text: '${uiLabelMap.DAOrderId}', datafield: 'primaryOrderId', minwidth: 150, editable: false, cellsrenderer:
			function(row, colum, value){
			var link = 'viewOrder?orderId=' + value;
		    return '<span><a href=\"' + link + '\">' + value + '</a></span>';
			}
		},
		{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 150, editable: false, columntype: 'dropdownlist', filtertype: 'input', filtertype: 'checkedlist',
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
		{ text: '${uiLabelMap.DestAddress}',  datafield: 'destAddress', width: 180, editable: false,
		},
		{ text: '${uiLabelMap.EstimatedShipDate}', filterable: false, datafield: 'estimatedShipDate', width: 180, editable: false, cellsformat: 'dd/MM/yyyy',
		},
		{ text: '${uiLabelMap.EstimatedArrivalDate}', filterable: false,  datafield: 'estimatedArrivalDate', width: 180, editable: false, cellsformat: 'dd/MM/yyyy',
		},
		{ text: '${uiLabelMap.EstimatedShipCost}', filterable: false, hidden: true, datafield: 'estimatedShipCost', minwidth: 180, editable: false, cellsrenderer:
			function(row, colum, value){
				var data = $('#jqxgrid').jqxGrid('getrowdata', row);
				return \"<span>\" + formatcurrency(data.estimatedShipCost, data.currencyUomId) + \"</span>\";
			}
		},
		{ text: '${uiLabelMap.TotalWeight}', filterable: false, hidden:true,  datafield: 'totalWeight', width: 150, editable: false, cellsrenderer:
			function(row, colum, value){
		   	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		   	var defaultWeightUomId = data.defaultWeightUomId;
		   	var defaultWeightUom = null;
		   	for (var i = 0; i < weightUomData.length; i++){
		   		if (defaultWeightUomId == weightUomData[i].uomId){
		   			defaultWeightUom = weightUomData[i].description;
		   		}
		   	}
		    var totalWeight = data.totalWeight;
		    return '<span>' + totalWeight.toLocaleString('${localeStr}') +' (' + defaultWeightUom +  ')</span>';
		}
		},
	"/>
<#assign deliveryEntry = delegator.findOne("DeliveryEntryDetail", {"deliveryEntryId": parameters.deliveryEntryId?if_exists}, false)/>
<#if "DELI_ENTRY_CREATED" == deliveryEntry.statusId >
	<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false" addrow="false"
		showtoolbar="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" editmode='click'
		url="jqxGeneralServicer?sname=getShipmentInDE&deliveryEntryId=${parameters.deliveryEntryId?if_exists}"
		customTitleProperties="ListShipmentInDeliveryEntry" viewSize="5"
		mouseRightMenu="true" contextMenuId="menuDelete" initrowdetailsDetail=initrowdetails initrowdetails = "true"
		addColumns="shipmentId;shipmentTypeId;statusId;primaryOrderId;estimatedReadyDate(java.sql.Timestamp);estimatedShipDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);estimatedShipCost(java.math.BigDecimal);originFacilityId;destinationFacilityId;originContactMechId;destinationContactMechId;vehicleId;totalWeight(java.math.BigDecimal);defaultWeightUomId"
	/>
<#else>
	<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false" addrow="false"
		showtoolbar="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" editmode='click'
		url="jqxGeneralServicer?sname=getShipmentInDE&deliveryEntryId=${parameters.deliveryEntryId?if_exists}"
		customTitleProperties="ListShipmentInDeliveryEntry" viewSize="5" initrowdetailsDetail=initrowdetails initrowdetails = "true"
		addColumns="shipmentId;shipmentTypeId;statusId;primaryOrderId;estimatedReadyDate(java.sql.Timestamp);estimatedShipDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);estimatedShipCost(java.math.BigDecimal);originFacilityId;destinationFacilityId;originContactMechId;destinationContactMechId;vehicleId;totalWeight(java.math.BigDecimal);defaultWeightUomId"
	/>
</#if>
</div>	

<div id="alterpopupWindow" class="hide popup-bound">
	<div class="row-fluid">
		${uiLabelMap.AddNewShipmentToDE}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="overflow-x: hidden;">
			<input type="hidden" id="facilityId" value="${parameters.facilityId?if_exists}"></input>
			<input type="hidden" id="fromDateTmp" value="${parameters.fromDate?if_exists}"></input>
			<#assign facility = delegator.findOne("Facility",{'facilityId', deliveryEntry.facilityId?if_exists}, false)/>
			<h4 class="row header smaller lighter blue" style="margin: 5px 0px 10px 10px !important; padding-left: 10px !important;font-weight:500;line-height:20px;font-size:18px;">
            ${uiLabelMap.BindingConditions}
	        </h4>
	    	<div class="row-fluid">
	    		<div class="span6">
	    			<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.ExportFromFacility} </div>
						</div>
						<div class="span7">
							<div id="facility" style="width: 100%;" class="green-label">${deliveryEntry.facilityName?if_exists}</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.Address} </div>
						</div>
						<div class="span7">
							<div id="address" style="width: 100%;" class="green-label">${deliveryEntry.fullName?if_exists}</div>
						</div>
					</div>
				</div>
				<div class="span6">
	    			<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.StartShipDate} </div>
						</div>
						<div class="span7">
							<div id="datetimeDelivery" style="width: 100%;" class="green-label">${deliveryEntry.fromDate?if_exists}</div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.EndShipDate} </div>
						</div>
						<div class="span7">
							<div id="datetimeDelivered" style="width: 100%;" class="green-label">${deliveryEntry.thruDate?if_exists}</div>
						</div>
					</div>
				</div>
			</div>
	        <div class="row-fluid" style="margin-left: 10px">
	            <div class='span12'><#include "listShipmentFilter.ftl"/></div>
		    </div>
		    <div class="form-action popup-footer">
                <button id="alterCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
                <button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	        </div>
	    </div>
    </div>
</div>
<div id='menuDelete' style="display:none;">
	<ul>
	    <li><i class="fa fa-trash red"></i>${uiLabelMap.CommonDelete}</li>
	</ul>
</div>
<style type="text/css">
.bootbox{
    z-index: 990009 !important;
}
.modal-backdrop{
    z-index: 890009 !important;
}
</style>
<script>
	$(document).ready(function(){
	});
	//Create theme

	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	$("#menuDelete").jqxMenu({ width: 100, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#menuDelete").on('itemclick', function (event) {
        var args = event.args;
        var rowindex = $("#jqxgrid").jqxGrid('getselectedrowindex');
        var dataRecord = $("#jqxgrid").jqxGrid('getrowdata', rowindex);
        var shipmentId = dataRecord.shipmentId;
		bootbox.dialog("${uiLabelMap.PONotificationBeforeDelete}", 
		[{"label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {
            	bootbox.hideAll();
            	$('#jqxgrid').jqxGrid('clearSelection');
        	}
        }, 
        {"label": "${StringUtil.wrapString(uiLabelMap.OK)}",
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	deleteDeliveryEntryIdInShipment(shipmentId);
            }
		}]);
    });
	
	function deleteDeliveryEntryIdInShipment(shipmentId){
		$.ajax({
			url: "deleteDeliveryEntryIdInShipment",
			type: "POST",
			data: {shipmentId: shipmentId},
			dataType: "json",
			success: function(data) {
			}
		}).done(function(data) {
			$("#jqxgrid").jqxGrid('updatebounddata');
			$('#jqxgrid').jqxGrid('clearselection');
		});
	}
	
	var weightUom;
	for(var i = 0; i < weightUomData.length; i++){
		if(weightUomData[i].uomId == '${deliveryEntry.weightUomId?if_exists}'){
			weightUom = weightUomData[i].description;
		}
	}
	$("#jqxgrid").on("bindingcomplete", function(event){
		updateTotalWeight();
	});
	
	var totalWeight = 0;
	function updateTotalWeight(){
		totalWeight = 0;
		var rows = $('#jqxgrid').jqxGrid('getrows');
		var uomIdTo = '${deliveryEntry.weightUomId?if_exists}';
		for(var i = 0; i < rows.length; i++){
			var uomId = rows[i].defaultWeightUomId;
			if (uomId == uomIdTo){
				totalWeight = totalWeight + rows[i].totalWeight;
			} else {
				for (var j=0; j<uomConvertData.length; j++){
					if ((uomConvertData[j].uomId == uomId && uomConvertData[j].uomIdTo == uomIdTo)){
						totalWeight = totalWeight + (uomConvertData[j].conversionFactor)*(rows[i].totalWeight);
						break;
					}
					if ((uomConvertData[j].uomId == uomIdTo && uomConvertData[j].uomIdTo == uomId)){
						totalWeight = totalWeight + (rows[i].totalWeight)/(uomConvertData[j].conversionFactor);
						break;
					}
				}
			}
		}
		$("#weight").text(totalWeight.toLocaleString('${localeStr}') + " (" + weightUom + ")");
	}
	
	<#if deliveryEntry.shipCost?has_content>
		var cost = "${deliveryEntry.shipCost?if_exists}";
	 	<#if locale == 'vi'>
			if (typeof cost == 'string') {
				cost = cost.replace(',', '.');
			}
		</#if>
		$("#shipCost").text(formatnumber(parseFloat(cost)));
	</#if>
	
	<#if deliveryEntry.description?has_content>
		$("#description").text('${StringUtil.wrapString(deliveryEntry.description?if_exists)}');
	<#else>
		$("#description").text('');
	</#if>
	<#if deliveryEntry.fromDate?has_content>
		$("#fromDate").text('${StringUtil.wrapString(Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(deliveryEntry.fromDate, "dd/MM/yyyy HH:mm", locale, timeZone)!)}');	
	<#else>
		$("#fromDate").text('');
	</#if>
	
	<#if deliveryEntry.thruDate?has_content>
		$("#thruDate").text('${StringUtil.wrapString(Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(deliveryEntry.thruDate, "dd/MM/yyyy HH:mm", locale, timeZone)!)}');	
	<#else>
		$("#thruDate").text('');
	</#if>
	
	for(var i = 0; i < statusData.length; i++){
		if(statusData[i].statusId == '${deliveryEntry.statusId}'){
			$("#statusId").text(statusData[i].description);
		}
	}
	
	//Create Button
	$('#alterpopupWindow').on('open', function (event) {
		initGridjqxgridfilterGrid();
		var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
		var curFacilityId = "${deliveryEntry.facilityId?if_exists}";
		var curFromDate = "${deliveryEntry.fromDate?if_exists}";
		if (curFacilityId == "" || curFacilityId == null){
	 		curFacilityId = $("#facilityId").val();
	 	}
		var temp;
	 	if ((curFromDate == "" || curFromDate == null) && $("#fromDate").jqxDateTimeInput('getDate') != null){
	 		temp = $("#fromDate").jqxDateTimeInput('getDate');
	 		temp.setHours(0,0,0,0);
	 	} else {
	 		temp = new Date(curFromDate);
	 		temp.setHours(0,0,0,0);
	 	}
	 	tmpS._source.url = "jqxGeneralServicer?sname=getFilterShipment&deliveryEntryId=${parameters.deliveryEntryId?if_exists}&facilityId="+curFacilityId+"&fromDate="+temp.getTime();
	 	$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
	});
	//Handle for alterSave3
	$("#alterCancel").on('click', function(event){
		$("#alterpopupWindow").jqxWindow('close');
	});
	$("#alterSave").on('click', function(event){
		var selectedIndexs = $('#jqxgridfilterGrid').jqxGrid('getselectedrowindexes');
        if(selectedIndexs.length == 0){
            bootbox.dialog("${uiLabelMap.YouNotYetChooseShipment}!", [{
                "label" : "${uiLabelMap.OK}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
            return false;
        }
		bootbox.dialog("${uiLabelMap.AreYouSureSave}", 
		[{"label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", 
			"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
            "callback": function() {bootbox.hideAll();}
        }, 
        {"label": "${StringUtil.wrapString(uiLabelMap.OK)}",
            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
            "callback": function() {
            	var selectedRow = $('#jqxgridfilterGrid').jqxGrid('getselectedrowindexes');
				var parameters = {};
				for(var i = 0; i < selectedRow.length; i++){
					var dataGrid = $('#jqxgridfilterGrid').jqxGrid('getrowdata', selectedRow[i]);
					parameters['shipmentId_o_' + i] = dataGrid.shipmentId;
					parameters['deliveryEntryId_o_' + i] = '${parameters.deliveryEntryId?if_exists}';
				}
				
				$.ajax({
			        url: "assignShipmentToDE",
			        type: "post",
			        data: parameters,
			        success: function(){
			        	$('#jqxgrid').jqxGrid("updatebounddata");
			        	$("#alterpopupWindow").jqxWindow('close');
			        },
			        error:function(){
			        }
			    });
            }
		}]);
	});
</script>

<script>
	//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	//Create popup window
	var wheight = '';
	if(typeof windowheight !== 'undefined'){
	    wheight = windowheight;
	}else{
	    windowheight = '525px';
	}
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 995, minHeight: 400, height: 485, maxHeight: 1200, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
</script>