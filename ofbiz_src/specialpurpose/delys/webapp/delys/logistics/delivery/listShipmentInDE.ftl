<script>
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["SHIPMENT_STATUS", "PURCH_SHIP_STATUS", "DELI_ENTRY_STATUS"]), null, null, null, false)/>
	var statusDataArr = new Array();
	<#list statuses as item>
		var row = {};
		<#assign description = item.get("description" ,locale)/>
		row['statusId'] = '${item.statusId}';
		row['description'] = '${StringUtil.wrapString(description)}';
		statusDataArr[${item_index}] = row;
	</#list>
	<#assign shipmentTypes = delegator.findList("ShipmentType", null, null, null, null, false)/>
	var shipmentTypeData = new Array();
	<#list shipmentTypes as item>
		var row = {};
		row['shipmentTypeId'] = '${item.shipmentTypeId}';
		row['description'] = '${item.description?if_exists}';
		shipmentTypeData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.abbreviation)/>
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description?if_exists}';
		weightUomData[${item_index}] = row;
	</#list>
	var windowheight = '400px';
	<#assign deliveryEntry = delegator.findOne("DeliveryEntry", Static["org.ofbiz.base.util.UtilMisc"].toMap("deliveryEntryId", parameters.deliveryEntryId?if_exists), false)/>
	<#assign facility = delegator.findOne("Facility", Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityId", deliveryEntry.facilityId), false)/>
	
	var datetimeDelivery = new Date(${parameters.fromDate?if_exists});
	var datetimeDeliveryString = datetimeDelivery.toString("dd/MM/yyyy");
</script>
<div>
<div style="overflow: hidden;margin-top:10px;margin-bottom:10px;">
	<div>
	    <h4 class="row header smaller lighter blue" style="margin-right:25px !important;font-weight:500;line-height:20px;font-size:18px;">
		    ${uiLabelMap.DeliveryEntry}
		</h4>
		<div class="row-fluid">
		    <div class="span2" style="text-align: right;">${uiLabelMap.FacilityFrom}: </div>
		    <div class="span2"><div id="facilityId" class="green-label"></div></div>
		    <div class="span2" style="text-align: right;">${uiLabelMap.weight}: </div>
		    <div class="span2"><div id="weight" class="green-label"></div></div>
		</div>
		<div class="row-fluid">
		    <div class="span2" style="text-align: right;">${uiLabelMap.EstimatedShipDate}: </div>
		    <div class="span2"><div id="fromDate" class="green-label"></div></div>
		    <div class="span2" style="text-align: right;">${uiLabelMap.Status}:</div>
		    <div class="span2"><div id="statusId" class="green-label"></div></div>
	 	</div>
	 	<div class="row-fluid">
	 	    <div class="span2" style="text-align: right;">${uiLabelMap.Description}: </div>
	 	    <div class="span2"><div id="description" class="green-label"></div></div>
	 	</div>
 	</div>
</div>
<div>
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
		   ]"/>
		   
<#assign columnlist="
		{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true, 
		    groupable: false, draggable: false, resizable: false,
		    datafield: '', columntype: 'number', width: 50,
		    cellsrenderer: function (row, column, value) {
		        return '<span style=margin:4px;>' + (value + 1) + '</span>';
		    }
		},
		{ text: '${uiLabelMap.ShipmentId}', datafield: 'shipmentId', width: 110, editable: false, filtertype:'input', pinned: true, cellsrenderer:
		   function(row, colum, value){
		    var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		    var shipmentId = data.shipmentId;
		    var shipmentTypeId = data.shipmentTypeId;
		    var link = 'editShipmentInfo?shipmentId=' + shipmentId +'&shipmentTypeId=' + shipmentTypeId;
		    return '<span><a href=\"' + link + '\">' + shipmentId + '</a></span>';
		}},
		{ text: '${uiLabelMap.ShipmentType}', datafield: 'shipmentTypeId', minwidth: 150, filtertype:'input', editable: false, cellsrenderer:
		function(row, colum, value){
		    for(var i = 0; i < shipmentTypeData.length; i++){
				if(shipmentTypeData[i].shipmentTypeId == value){
					return '<span title=' + value + '>' + shipmentTypeData[i].description + '</span>'
				}
			}
		}
		},
		{ text: '${uiLabelMap.Status}', dataField: 'statusId', width: 100, align: 'center', filtertype:'input', cellsalign: 'right', editable: false, columntype: 'dropdownlist', filtertype: 'input', 
		cellsrenderer: function(row, column, value){
			for(var i = 0; i < statusDataArr.length; i++){
				if(statusDataArr[i].statusId == value){
					return '<span title=' + value + '>' + statusDataArr[i].description + '</span>'
				}
			}
		},
		},
		{ text: '${uiLabelMap.FacilityTo}',  datafield: 'destFacilityName', width: 160, editable: false, filtertype:'input',
		},
		{ text: '${uiLabelMap.OriginContactMech}',  datafield: 'originAddress', width: 160, editable: true, 
		},
		{ text: '${uiLabelMap.DestinationContactMech}', sortable:false, datafield: 'destAddress', width: 182, editable: false, 
	    },
	   { text: '${uiLabelMap.ShipmentTotalWeight}', filterable: false,  datafield: 'totalWeight', width: 180, editable: false, cellsrenderer:
			function(row, colum, value){
		   	var data = $('#jqxgrid').jqxGrid('getrowdata', row);
		   	var defaultWeightUomId = data.defaultWeightUomId;
		    var defaultWeightUom = getWeightUnit(defaultWeightUomId);
		    var totalWeight = data.totalWeight;
		    return '<span>' + totalWeight +' (' + defaultWeightUom +  ')</span>';
		}
		},
		{ text: '${uiLabelMap.FormFieldTitle_estimatedReadyDate}', filterable: false, datafield: 'estimatedReadyDate', width: 160, editable: false, cellsformat: 'dd/MM/yyyy',
		},
		{ text: '${uiLabelMap.FormFieldTitle_estimatedShipDate}', filterable: false, datafield: 'estimatedShipDate', width: 160, editable: false, cellsformat: 'dd/MM/yyyy',
		},
		{ text: '${uiLabelMap.EstimatedArrivalDate}', filterable: false,  datafield: 'estimatedArrivalDate', width: 160, editable: false, cellsformat: 'dd/MM/yyyy',
		},
		{ text: '${uiLabelMap.EstimatedShipCost}', filterable: false, datafield: 'estimatedShipCost', minwidth: 180, editable: false, cellsrenderer:
			function(row, colum, value){
				var data = $('#jqxgrid').jqxGrid('getrowdata', row);
				return \"<span>\" + formatcurrency(data.estimatedShipCost, data.currencyUomId) + \"</span>\";
			}
		},
		
	"/>
<#assign deliveryEntry = delegator.findOne("DeliveryEntry", {"deliveryEntryId": parameters.deliveryEntryId?if_exists}, false)/>
<#if "DELI_ENTRY_CREATED" == deliveryEntry.statusId >
	<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false" addrow="true"
		showtoolbar="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" editmode='click'
		url="jqxGeneralServicer?sname=JQGetListShipmentInDE&deliveryEntryId=${parameters.deliveryEntryId?if_exists}" updateUrl="jqxGeneralServicer?sname=updateProductPrice&jqaction=U"
		createUrl="jqxGeneralServicer?sname=createProductPrice&jqaction=C" otherParams="totalWeight:S-getTotalShipmentItem(shipmentId)<totalWeight>"
		addColumns="shipmentId;shipmentTypeId;statusId;primaryOrderId;estimatedReadyDate(java.sql.Timestamp);estimatedShipDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);estimatedShipCost(java.math.BigDecimal);originFacilityId;destinationFacilityId;originContactMechId;destinationContactMechId;vehicleId;totalWeight(java.math.BigDecimal);defaultWeightUomId"
	/>
<#else>
	<@jqGrid filtersimplemode="false" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="false" addrow="false"
		showtoolbar="true" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" editmode='click'
		url="jqxGeneralServicer?sname=JQGetListShipmentInDE&deliveryEntryId=${parameters.deliveryEntryId?if_exists}" updateUrl="jqxGeneralServicer?sname=updateProductPrice&jqaction=U"
		createUrl="jqxGeneralServicer?sname=createProductPrice&jqaction=C" otherParams="totalWeight:S-getTotalShipmentItem(shipmentId)<totalWeight>"
		addColumns="shipmentId;shipmentTypeId;statusId;primaryOrderId;estimatedReadyDate(java.sql.Timestamp);estimatedShipDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);estimatedShipCost(java.math.BigDecimal);originFacilityId;destinationFacilityId;originContactMechId;destinationContactMechId;vehicleId;totalWeight(java.math.BigDecimal);defaultWeightUomId"
	/>
</#if>
</div>					     

<div id="alterpopupWindow" class="hide">
	<div class="row-fluid">
		${uiLabelMap.accCreateNew}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content' style="overflow-x: hidden;">
			<input type="hidden" id="facilityId" value="${parameters.facilityId?if_exists}"></input>
			<input type="hidden" id="fromDate" value="${parameters.fromDate?if_exists}"></input>
			<#assign facility = delegator.findOne("Facility",{'facilityId', parameters.facilityId?if_exists}, false)/>
			<h4 class="row header smaller lighter blue" style="margin: 5px 25px 10px 10px !important;font-weight:500;line-height:20px;font-size:18px;">
            ${uiLabelMap.GeneralInfo}
	        </h4>
	    	<div class="row-fluid">
	    		<div class="span6">
	    			<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.Facility}: </div>
						</div>
						<div class="span7">
							<div id="facility" style="width: 100%;" class="green-label">${facility.facilityName?if_exists}</div>
						</div>
					</div>
				</div>
				<div class="span6">
	    			<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.FormFieldTitle_estimatedShipDate}: </div>
						</div>
						<div class="span7">
							<div id="datetimeDelivery" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
				</div>
			</div>
	        <div class="row-fluid" style="margin-left: 10px">
	            <div class='span12'><#include "listShipmentFilter.ftl"/></div>
		    </div>
		    <div class="form-action">
		        <div class='row-fluid'>
		            <div class="span12 margin-top20" style="margin-bottom:10px;">
		                <button id="alterCancel" class='btn btn-danger form-action-button pull-right' style="margin-right:20px !important;"><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		                <button id="alterSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
		            </div>
		        </div>
	        </div>
	    </div>
    </div>
</div>
<script>
	//Create Window popup
	//Create theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	$("#datetimeDelivery").text(datetimeDeliveryString);
	$("#facilityId").text('${StringUtil.wrapString(facility.facilityName)}');
	var weightUom;
	for(var i = 0; i < weightUomData.length; i++){
		if(weightUomData[i].uomId == '${deliveryEntry.weightUomId}'){
			weightUom = weightUomData[i].description;
		}
	}
	$("#weight").text('${deliveryEntry.weight?string(",##0")} ('+weightUom+')');
	$("#description").text('${StringUtil.wrapString(deliveryEntry.description?if_exists)}');
	$("#fromDate").text('${StringUtil.wrapString(Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(deliveryEntry.fromDate, "dd/MM/yyyy", locale, timeZone)!)}');
	for(var i = 0; i < statusDataArr.length; i++){
		if(statusDataArr[i].statusId == '${deliveryEntry.statusId}'){
			$("#statusId").text(statusDataArr[i].description);
		}
	}
	
	//Create Button
	$('#alterpopupWindow').on('open', function (event) {
		initGridjqxgridfilterGrid();
		var tmpS = $("#jqxgridfilterGrid").jqxGrid('source');
		var curFacilityId = "${parameters.facilityId?if_exists}";
		var curFromDate = "${parameters.fromDate?if_exists}";
		if (curFacilityId == "" || curFacilityId == null){
	 		curFacilityId = $("#facilityId").val();
	 	}
	 	if ((curFromDate == "" || curFromDate == null) && $("#fromDate").jqxDateTimeInput('getDate') != null){
	 		curFromDate = $("#fromDate").jqxDateTimeInput('getDate').getTime();
	 	}
	 	tmpS._source.url = "jqxGeneralServicer?sname=JQGetListFilterShipment&deliveryEntryId=${parameters.deliveryEntryId?if_exists}&facilityId="+curFacilityId+"&fromDate="+curFromDate;
	 	$("#jqxgridfilterGrid").jqxGrid('source', tmpS);
	});
	//Handle for alterSave3
	$("#alterCancel").on('click', function(event){
		$("#alterpopupWindow").jqxWindow('close');
	});
	$("#alterSave").on('click', function(event){
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
	});
</script>

<script type="text/javascript">
	<#assign listSMStatus = delegator.findList("StatusItem", null, null, null, null, false)/>
	<#assign listWeightUnit = delegator.findList("Uom", null, null, null, null, false)/>
	
	<#assign shipmentType = delegator.findList("ShipmentType", null, null, null, null, false)/>
	var pptData = new Array();
	<#if shipmentType?exists>
		<#list shipmentType as item>
			var row = {};
			<#assign description = StringUtil.wrapString(item.description)/>
			row['shipmentTypeId'] = '${item.shipmentTypeId?if_exists}';
			row['description'] = "${description}";
			pptData[${item_index}] = row;
		</#list>
	</#if>
	function getShipmentType(shipmentTypeId) {
		for ( var x in pptData) {
			if (shipmentTypeId == pptData[x].shipmentTypeId) {
				return pptData[x].description;
			}
		}
	}
	var listSMStatus = new Array();
	<#if listSMStatus?exists>
		<#list listSMStatus as item>
			var row = {};
			<#assign description = StringUtil.wrapString(item.description)/>
			row['statusId'] = '${item.statusId?if_exists}';
			row['description'] = "${description}";
			listSMStatus[${item_index}] = row;
		</#list>
	</#if>
	function getStatusItem(statusId) {
		for ( var x in listSMStatus) {
			if (statusId == listSMStatus[x].statusId) {
				return listSMStatus[x].description;
			}
		}
	}
	
	var listWeightUnit = new Array();
	<#if listWeightUnit?exists>
		<#list listWeightUnit as item>
			var row = {};
			<#assign description = StringUtil.wrapString(item.description)/>
			row['uomId'] = '${item.uomId?if_exists}';
			row['description'] = "${description}";
			listWeightUnit[${item_index}] = row;
		</#list>
	</#if>
	function getWeightUnit(uomId) {
		for ( var x in listWeightUnit) {
			if (uomId == listWeightUnit[x].uomId) {
				return listWeightUnit[x].description;
			}
		}
	}
	
	function getWeight(id) {
		var thisWeight = 0;
		jQuery.ajax({
	        url: "getTotalShipmentItem",
	        type: "POST",
	        data: {shipmentId: id},
	        success: function(res) {
	        	thisWeight = res["totalWeight"];
	        }
	    }).done(function() {
	    	return thisWeight;
		});
		return thisWeight;
	}
</script>
<script>
	<#assign shipmentTypes = delegator.findList("ShipmentType", null, null, null, null, false) />
	var shipmentTypeData = new Array();
	<#list shipmentTypes as item>
	 	<#assign description = StringUtil.wrapString(item.get("description",locale))>
		var row = {};
		row['shipmentTypeId'] = '${item.shipmentTypeId}';
		row['description'] = '${description?if_exists}';
		shipmentTypeData[${item_index}] = row;
	</#list>
	
	<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", Static["org.ofbiz.entity.condition.EntityOperator"].IN , ["SHIPMENT_STATUS","PURCH_SHIP_STATUS"]), null, null, null, false) />
	var statusData = new Array();
	<#list statuses as item>
	    <#assign description = StringUtil.wrapString(item.get("description",locale))>
		var row = {};
		row['statusId'] = '${item.statusId}';
		row['description'] = "${description?if_exists}";
		statusData[${item_index}] = row;
	</#list>
	
	<#assign weightUoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false)>
	var weightUomData = new Array();
	<#list weightUoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.abbreviation) />
		row['uomId'] = '${item.uomId}';
		row['description'] = '${description?if_exists}';
		weightUomData[${item_index}] = row;
	</#list>
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
		maxWidth: 1500, minWidth: 995, minHeight: 470, maxHeight: 1200, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
</script>