<script type="text/javascript" src="/delys/images/js/util/DateUtil.js" ></script>
<script>
	//Create theme
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	originFacilitySelected = null;
    <#if orderHeaderTemp.statusId == "ORDER_COMPLETED">
        var editableDI = false;
    <#else>
        var editableDI = true;
    </#if>
    var glOrderId = '${parameters.orderId}';
    
    <#assign originProductStore = orderHeaderTemp.productStoreId?if_exists>
    
    <#assign company = Static["com.olbius.util.MultiOrganizationUtil"].getCurrentOrganization(delegator)/>
    
    <#assign uomConversions = delegator.findList("UomConversion", null, null, null, null, false) />
	var uomConvertData = new Array();
	<#list uomConversions as item>
		var row = {};
		row['uomId'] = "${item.uomId}";
		row['uomIdTo'] = "${item.uomIdTo}";
		row['conversionFactor'] = "${item.conversionFactor}";
		uomConvertData[${item_index}] = row;
	</#list>
	
    <#assign orderItems = delegator.findList("OrderItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", parameters.orderId, "isPromo", "N")), null, null, null, false) />
	var orderItemData = new Array();
	<#list orderItems as item>
		var row = {};
		row['orderId'] = "${item.orderId}";
		row['estimatedDeliveryDate'] = "${item.estimatedDeliveryDate.getTime()}";
		orderItemData[${item_index}] = row;
	</#list>
	
	var estimatedDeliveryDate = new Date(parseInt(orderItemData[0].estimatedDeliveryDate));
	
    <#assign countryList = delegator.findList("Geo", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("geoTypeId", "COUNTRY"), null, null, null, false) />
	var countryData = new Array();
	<#list countryList as geo>
		<#assign geoId = StringUtil.wrapString(geo.geoId) />
		<#assign geoName = StringUtil.wrapString(geo.geoName) />
		var row = {};
		row['geoId'] = "${geo.geoId}";
		row['geoName'] = "${geo.geoName}";
		countryData[${geo_index}] = row;
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
	
	var partyData = [];
	<#list listOParty as item>
		var row = {};
		<#assign description = StringUtil.wrapString(StringUtil.wrapString(item.firstName?if_exists) + StringUtil.wrapString(item.middleName?if_exists) + StringUtil.wrapString(item.lastName?if_exists) + StringUtil.wrapString(item.groupName?if_exists))>
		row['partyId'] = "${item.partyId}";
		row['description'] = "${description}";
		partyData[${item_index}] = row;
	</#list>
	
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "productStoreId", originProductStore, "ownerPartyId", company)), null, null, null, false)>
	var facilityData = [];
	<#list facilities as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = "${item.facilityId?if_exists}";
		row['ownerPartyId']= "${item.ownerPartyId?if_exists}";
		row['description'] = "${description?if_exists}";
		row['productStoreId'] = "${item.productStoreId?if_exists}";
		facilityData[${item_index}] = row;
	</#list>
	
	//Create partyIdTo Input
	var partyToData = [];
	<#list listPartyTo as item>
		<#assign description = StringUtil.wrapString(StringUtil.wrapString(item.firstName?if_exists) + StringUtil.wrapString(item.middleName?if_exists) + StringUtil.wrapString(item.lastName?if_exists) + StringUtil.wrapString(item.groupName?if_exists))>
		var row = {};
		row['partyId'] = '${item.partyId}';
		row['description'] = '${description}';
		partyToData[${item_index}] = row;
	</#list>
	//Create partyIdFrom Input
	var partyFromData = [];
	<#list listPartyFrom as item>
		<#assign description = StringUtil.wrapString(StringUtil.wrapString(item.firstName?if_exists) + StringUtil.wrapString(item.middleName?if_exists) + StringUtil.wrapString(item.lastName?if_exists) + StringUtil.wrapString(item.groupName?if_exists))>
		var row = {};
		row['partyId'] = '${item.partyId}';
		row['description'] = '${description}';
		partyFromData[${item_index}] = row;
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
						 return '<span><a href=\"javascript:void(0);\" onclick=\"showDetailPopup(&#39;' + value + '&#39;)\"' + '> ' + value  + '</a></span>'
					 }
					},
//					{ text: '${uiLabelMap.Receiver}', dataField: 'partyIdTo', width: 150, editable:false,
//						cellsrenderer: function(row, column, value){
//							for(var i = 0; i < partyData.length; i++){
//								if(partyData[i].partyId == value){
//									return '<span title=' + value + '>' + partyData[i].description + '</span>'
//								}
//							}
//						},
//						filtertype: 'input'
//					 },
//					 { text: '${uiLabelMap.address}', dataField: 'destContactMechId', width: 250, editable:false,
//						 cellsrenderer: function(row, column, value){
//							 for(var i = 0; i < pstAddrData.length; i++){
//								 if(pstAddrData[i].contactMechId == value){
//									 return '<span title=' + value + '>' + pstAddrData[i].description + '</span>'
//								 }
//							 }
//						 },
//						 filtertype: 'input'
//					 },
//					 { text: '${uiLabelMap.OrderId}', dataField: 'orderId', width: 150, filtertype: 'input', editable:false},
					{ text: '${uiLabelMap.EstimatedExportDate}', dataField: 'estimatedStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right'},
					{ text: '${uiLabelMap.EstimatedDeliveryDate}', dataField: 'estimatedArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right'},
					{ text: '${uiLabelMap.ActualExportedDate}', dataField: 'actualStartDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right'},
					{ text: '${uiLabelMap.ActualDeliveredDate}', dataField: 'actualArrivalDate', width: 150, cellsformat: 'dd/MM/yyyy', filtertype: 'range', editable:false, cellsalign: 'right'},
					
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
							 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							 return '<span>' + data.originFacilityName + '</span>';
						 },
						 filtertype: 'input'
					 },
					 { text: '${uiLabelMap.FacilityTo}', dataField: 'destFacilityId', minwidth: 150, editable:false,
						 cellsrenderer: function(row, column, value){
							 var data = $('#jqxgrid').jqxGrid('getrowdata', row);
							 return '<span>' + data.destFacilityName + '</span>';
						 },
						 filtertype: 'input'
					 },
//					 { text: '${uiLabelMap.partyIdFrom}', dataField: 'partyIdFrom', width: 150, editable:false,
//							cellsrenderer: function(row, column, value){
//								for(var i = 0; i < partyData.length; i++){
//									if(partyData[i].partyId == value){
//										return '<span title=' + value + '>' + partyData[i].description + '</span>'
//									}
//								}
//							},
//							filtertype: 'input'
//						 },
//						 { text: '${uiLabelMap.originContactMechId}', dataField: 'originContactMechId', width: 250, editable:false,
//							 cellsrenderer: function(row, column, value){
//								 for(var i = 0; i < pstAddrData.length; i++){
//									 if(pstAddrData[i].contactMechId == value){
//										 return '<span title=' + value + '>' + pstAddrData[i].description + '</span>'
//									 }
//								 }
//							 },
//							 filtertype: 'input'
//						 },
//					 { text: '${uiLabelMap.productStore}',dataField: 'originProductStoreId', width: 150, editable:false,
//						 cellsrenderer: function(row, column, value){
//							 for(var i = 0; i < prodStoreData.length; i++){
//								 if(prodStoreData[i].productStoreId == value){
//									 return '<span title=' + value + '>' + prodStoreData[i].description + '</span>'
//								 }
//							 }
//						 },
//						 filtertype: 'input'
//					 },
					 { text: '${uiLabelMap.noNumber}', dataField: 'no', width: 120, filtertype: 'input', editable:false},
					 { text: '${uiLabelMap.createDate}', dataField: 'createDate', width: 150, cellsformat: 'd', filtertype: 'range', editable:false, cellsalign: 'right'},
//					 { text: '${uiLabelMap.totalAmount}', dataField: 'totalAmount', width: 150, editable:false,
//						 cellsrenderer: function(row, column, value){
//							 return '<span>' + formatcurrency(value) + '</span>';
//						 },
//						 filtertype: 'input'
//					 },
//					 { text: '${uiLabelMap.DeliveryType}', dataField: 'deliveryTypeId', width: 120, editable:false,
//							cellsrenderer: function(row, column, value){
//								for(var i = 0; i < deliveryTypeData.length; i++){
//									if(deliveryTypeData[i].deliveryTypeId == value){
//										return '<span title=' + value + '>' + deliveryTypeData[i].description + '</span>'
//									}
//								}
//							},
//							filtertype: 'input'
//						 }, 
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
					{ name: 'totalAmount', type: 'number' },
					{ name: 'no', type: 'string' },
					{ name: 'defaultWeightUomId', type: 'string' },
		 		 	]"/>
<#if security.hasPermission("DELIVERY_CREATE", userLogin) && orderHeaderTemp.statusId != "ORDER_COMPLETED">
	<@jqGrid filtersimplemode="true" id="jqxgrid" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="true" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
		 url="jqxGeneralServicer?sname=getListDelivery&orderId=${parameters.orderId}&deliveryTypeId=DELIVERY_SALES" createUrl="jqxGeneralServicer?sname=createDelivery&jqaction=C" editmode="dblclick"
		 addColumns="listOrderItems(java.util.List);orderId;currencyUomId;statusId;destFacilityId;originProductStoreId;partyIdTo;partyIdFrom;createDate(java.sql.Timestamp);destContactMechId;originContactMechId;originFacilityId;deliveryDate(java.sql.Timestamp);deliveryTypeId[DELIVERY_SALES];no;estimatedStartDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);defaultWeightUomId" 	 
		 updateUrl="jqxGeneralServicer?sname=updateDelivery&jqaction=U" editColumns="deliveryId;statusId" functionAfterAddRow="afterAdd()"
		 jqGridMinimumLibEnable="true"/>
<#else>
	<@jqGrid filtersimplemode="true" id="jqxgrid" addrefresh="true" usecurrencyfunction="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" addrow="false" filterable="true" alternativeAddPopup="alterpopupWindow" editable="true" 
		 url="jqxGeneralServicer?sname=getListDelivery&orderId=${parameters.orderId}" createUrl="jqxGeneralServicer?sname=createDelivery&jqaction=C" editmode="dblclick"
		 addColumns="listOrderItems(java.util.List);orderId;currencyUomId;statusId;destFacilityId;originProductStoreId;partyIdTo;partyIdFrom;createDate(java.sql.Timestamp);destContactMechId;originContactMechId;originFacilityId;deliveryDate(java.sql.Timestamp);deliveryTypeId[DELIVERY_SALES];no;estimatedStartDate(java.sql.Timestamp);estimatedArrivalDate(java.sql.Timestamp);defaultWeightUomId" 	 
		 updateUrl="jqxGeneralServicer?sname=updateDelivery&jqaction=U" editColumns="deliveryId;statusId"
		 jqGridMinimumLibEnable="true"/>
</#if>
</div>
<div id="alterpopupWindow" class="hide">
	<div>${uiLabelMap.AddNewDeliverySales}</div>
	 <input type="hidden" name="orderId"/>
	<input type="hidden" name="currencyUomId"/>
	<input type="hidden" name="statusId"/>
	<input type="hidden" name="orderDate"/>
	<input type="hidden" name="originProductStoreId" value=""/>
	<div class='form-window-container'>
		<div class='form-window-content'>
	    	<h4 class="row header smaller lighter blue" style="margin: 5px 25px 20px 20px !important;font-weight:500;line-height:20px;font-size:18px;">
	            ${uiLabelMap.GeneralInfo}
	        </h4>
	        <div class="row-fluid">
	    		<div class="span6">
	    			<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.OrderId}: </div>
						</div>
						<div class="span7">	
							<div id="orderId" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.Sender}: </div>
						</div>
						<div class="span7">	
							<div id="partyIdFrom" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.originFacilityId}: </div>
						</div>
						<div class="span7">	
							<div class="span8">
						        <div id="originFacilityId" class="green-label"></div>
					        </div>
					        <div class="span2">
					            <a href="javascript:void(0);" onclick="getFacilityList();" style="margin-left:40px;"><i class="icon-search"></i></a>
					        </div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.OriginAddress}: </div>
						</div>
						<div class="span7">	
							<div id="originContactMechId" style="width: 100%;" class="green-label pull-left"></div><div class='pull-right' style="margin-right: 27px !important;"><a onclick="addOriginFacilityAddress()"><i style="padding-left: 10px;" class="icon-plus-sign"></i></a></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.RequireDeliveryDate}: </div>
						</div>
						<div class="span7">	
							<div id="deliveryDate" style="width: 100%;"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.TotalWeight}: </div>
						</div>
						<div class="span7">	
							<div class="row-fluid">
								<div class="span5">
									<div id="totalProductWeight" class="green-label"></div>
								</div>
								<div class="span7">
									<div id="listWeightUomId" class="green-label"></div>
								</div>
							</div>
						</div>
					</div>
	    		</div>
	    		<div class="span6">
		    		<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
							<div> ${uiLabelMap.noNumber}: </div>
						</div>
						<div class="span7">	
							<input id="no" style="width: 100%;" type="text"/>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.Receiver}: </div>
						</div>
						<div class="span7">	
							<div id="partyIdTo" style="width: 100%;" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
	    				<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.destFacilityId}: </div>
						</div>
						<div class="span7">	
							<div type='text' id="destFacilityId" class="green-label"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.customerAddress}: </div>
						</div>
						<div class="span7">	
							<div id="destContactMechId" style="width: 100%;" class="green-label pull-left"></div><div class='pull-right' style="margin-right: 27px !important;"><a onclick="addDestFacilityAddress()"><i style="padding-left: 10px;" class="icon-plus-sign"></i></a></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.EstimatedStartDate}: </div>
						</div>
						<div class="span7">	
							<div id="estimatedStartDate" style="width: 100%;"></div>
						</div>
					</div>
					<div class="row-fluid margin-bottom10">	
						<div class="span5" style="text-align: right">
	    					<div> ${uiLabelMap.EstimatedArrivalDate}: </div>
						</div>
						<div class="span7">	
							<div id="estimatedArrivalDate" style="width: 100%;"></div>
						</div>
					</div>
	    		</div>
	    		<div class="row-fluid">
					<div style="margin-left: 20px;"><#include "listOrderItemDelivery.ftl"/></div>
				</div>
			</div>
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
<div id="popupDeliveryDetailWindow">
    <div>${uiLabelMap.Delivery}</div>
	<div style="overflow: hidden;">
	    <div>
			<h4 class="row header smaller lighter blue" style="margin-right:25px !important;margin-left: 20px !important;font-weight:500;line-height:20px;font-size:18px;">
				${uiLabelMap.GeneralInfo}
				<a style="float:right;font-size:14px;" id="printPDF" target="_blank" data-rel="tooltip" title="${uiLabelMap.ExportPdf}" data-placement="bottom" data-original-title="${uiLabelMap.ExportPdf}"><i class="fa-file-pdf-o"></i>&nbsp;PDF</a>
			</h4>
			<div class="row-fluid">
				<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.deliveryIdDT}:</div>
						<div class="span7"><div id="deliveryIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.statusIdDT}:</div>
					    <div class="span7"><div id="statusIdDT" class="green-label"></div></div>
					</div>
				</div>
    		</div>
    		<div class="row-fluid">
	    		<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.OrderId}:</div>
		    		    <div class="span7"><div id="orderIdDT"class="green-label"></div></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.noNumber}:</div>
		    		    <div class="span7" style="text-align: left;"><div id="noDT" class="green-label"></div></div>
					</div>
				</div>
    		</div>
    		<div class="row-fluid">
	    		<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.Sender}:</div>
					    <div class="span7"><div id="partyIdFromDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.Receiver}:</div>
		    		    <div class="span7"><div id="partyIdToDT" class="green-label"></div></div>
					</div>
				</div>
    		</div>
    		<div class="row-fluid">
	    		<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.facility}:</div>
					    <div class="span7"><div id="originFacilityIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.destFacilityId}:</div>
					    <div class="span7"><div id="destFacilityIdDT" class="green-label"></div></div>
					</div>
				</div>
    		</div>
    		<div class="row-fluid">
	    		<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.OriginAddress}:</div>
		    		    <div class="span7"><div id="originContactMechIdDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid">
					   	<div class="span5" style="text-align: right;">${uiLabelMap.customerAddress}:</div>
		    		    <div class="span7"><div id="destContactMechIdDT" class="green-label"></div></div>
					</div>
				</div>
    		</div>
    		<div class="row-fluid">
	    		<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.createDate}:</div>
					    <div class="span7"><div id="createDateDT" class="green-label"></div></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.RequireDeliveryDate}:</div>
		    		    <div class="span7"><div id="deliveryDateDT" class="green-label"></div></div>
					</div>
				</div>
    		</div>
    		<div class="row-fluid">
	    		<div class="span6">
					<div class="row-fluid">
						<div class="span5" style="text-align: right;">${uiLabelMap.TotalWeight}:</div>
					    <div class="span7"><div id="totalWeight" class="green-label"></div></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid">
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
					<div class="row-fluid">
						<div class="span5" id="scanLabel" style="text-align: right;" class="asterisk"></div>
					    <div class="span7"><div id="scanfile" class="green-label"></div></div>
					</div>
				</div>
				<div class="span6">
					<div class="row-fluid">
						<div class="span5" id="actualArrivalLabel" style="text-align: right;" class="asterisk"></div>
					    <div class="span7">
					    	<div id="actualArrivalDateDis" class="green-label"></div>
					    	<div id="actualArrivalDate" class="green-label"></div>
					    </div>
					</div>
				</div>
			</div>
    		<div class="row-fluid">
				<div style="margin-left: 20px"><#include "listDeliveryItem.ftl"/></div>
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
<div id="facilityWindow" class="hide">
    <div>${uiLabelMap.PickAFacility}</div>
    <div style="overflow: hidden;">
        <div style="margin-left:15px;">
            <script type="text/javascript">
                $("#facilityWindow").jqxWindow({
                    maxWidth: 1500, minWidth: 550, minHeight: 327, maxHeight: 1200, resizable: false,  isModal: true, modalZIndex: 1000, autoOpen: false, modalOpacity: 0.7, theme:theme           
                });
            </script>
            <@jqGrid id="jqxgridFAINV" columnlist=columnlistFA dataField=dataFieldFA url="jqxGeneralServicer" bindresize="false" 
                autoheight="false" height="260" customTitleProperties="AvailableFacilityList"/>
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
<div id="addPostalAddressWindow" style="display: none">
	<div class="row-fluid">
		${uiLabelMap.NewFacilityAddress}
	</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<div class='row-fluid margin-top10'>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px">${StringUtil.wrapString(uiLabelMap.Facility)}</div>
					</div>  
					<div class="span7">
						<div id="seletedFacilityId" class="green-label"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.LogCommonNational)}</div>
					</div>  
					<div class="span7">
						<div id="countryGeoId"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.PartyCity)}</div>
					</div>  
					<div class="span7">
						<div id="stateProvinceGeoId"></div>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.CityPostalCode)}</div>
					</div>  
					<div class="span7">
						<input id="postalCode" style=""></input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div class="asterisk">${StringUtil.wrapString(uiLabelMap.PartyAddressLine1)}</div>
					</div>  
					<div class="span7">
						<input id="address1">
						</input>
					</div>
				</div>
				<div class='row-fluid margin-bottom10'>
					<div class='span5 text-algin-right'>
						<div style="margin-right: 10px">${StringUtil.wrapString(uiLabelMap.PartyAddressLine2)}</div>
					</div>  
					<div class="span7">
						<input id="address2">
						</input>
					</div>
				</div>
				<div class="form-action">
					<div class='row-fluid'>
						<div class="span12 margin-top10">
							<button id="newAddrCancelButton" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
							<button id="newAddrOkButton" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
						</div>
					</div>
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
	var destContactData = new Array();
	var originContactData = new Array();
	contactMechPurposeTypeId = null;
	$("#addPostalAddressWindow").jqxWindow({
		maxWidth: 640, minWidth: 630, minHeight: 345, maxHeight: 600, cancelButton: $("#newAddrCancelButton"), resizable: true,  isModal: true, autoOpen: false,
	});
	function updateStateProvince(){
		var request = $.ajax({
			  url: "loadGeoAssocListByGeoId",
			  type: "POST",
			  data: {geoId : $("#countryGeoId").val(),
				  },
			  dataType: "json",
			  success: function(data) {
				  var listcontactMechPurposeTypeMap = data["listGeoAssocMap"];
				  var contactMechPurposeTypeId = new Array();
				  var description = new Array();
				  var array_keys = new Array();
				  var array_values = new Array();
				  for(var i = 0; i < listcontactMechPurposeTypeMap.length; i++){
					  
					  for (var key in listcontactMechPurposeTypeMap[i]) {
					      array_keys.push(key);
					      array_values.push(listcontactMechPurposeTypeMap[i][key]);
					  }
					  
				  }
				  
				  var dataTest = new Array();
				  for (var j =0; j < array_keys.length; j++){
							var row = {};
							row['id'] = array_keys[j];
							row['value'] = array_values[j];
							dataTest[j] = row;
				  }
				  if (dataTest.length == 0){
					  var dataEmpty = new Array();
					  $("#stateProvinceGeoId").jqxDropDownList({source: dataEmpty, autoDropDownHeight: true});
					  $("#stateProvinceGeoId").jqxDropDownList('setContent', '${uiLabelMap.CommonNoStatesProvincesExists}'); 
				  } else {
					  $("#stateProvinceGeoId").jqxDropDownList({source: dataEmpty, autoDropDownHeight: false});
					  $("#stateProvinceGeoId").jqxDropDownList({selectedIndex: 0,  source: dataTest, displayMember: 'value', valueMember: 'id'});
					  if ("VNM" == $("#countryGeoId").val()){
						  $("#stateProvinceGeoId").jqxDropDownList('val', "VN-HN");
					  }
				  }
			  }
		}); 
	}
	$("#countryGeoId").on('change', function (event) {
		updateStateProvince();
	});
	$('#addPostalAddressWindow').jqxValidator({
		rules: [
               { input: '#address1', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
               { input: '#postalCode', message: '${uiLabelMap.FieldRequired}', action: 'keyup, blur', rule: 'required' },
               ]
    });
	
	$('#addPostalAddressWindow').on('close', function (event) {
		$('#address1').val("");
		$('#address2').val("");
		$('#postalCode').val("");
	});
	
	$('#destContactMechId').jqxDropDownList({source: destContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});	
	$('#originContactMechId').jqxDropDownList({source: originContactData, selectedIndex: 0, width: 200, theme: theme, displayMember: 'description', valueMember: 'contactMechId'});
	$('#listWeightUomId').jqxDropDownList({source: weightUomData, selectedIndex: 0, width: 90, theme: theme, displayMember: 'description', valueMember: 'uomId'});
	$('#listWeightUomId').jqxDropDownList('val','WT_kg');
			
	// create list originfacility
	$('#originFacilityId').jqxDropDownList({selectedIndex: 0, width: 200, source: facilityData, theme: theme, displayMember: 'description', valueMember: 'facilityId',});
	
	//Create Destination Facility
	var destFacilitySource = new Array();
	$('#destFacilityId').jqxDropDownList({selectedIndex: 0, width: 200, source: destFacilitySource, theme: theme, displayMember: 'description', valueMember: 'facilityId'});
	
	// Create list partyIdto 
	$("#partyIdTo").jqxDropDownList({selectedIndex: 0, width: 200, dropDownWidth: 200, source: partyToData, theme: theme, displayMember: 'description', valueMember: 'partyId'});
	
	// Create list partyIdFrom
	$("#partyIdFrom").jqxDropDownList({selectedIndex: 0, width: 200, dropDownWidth: 200, source: partyFromData, theme: theme, displayMember: 'description', valueMember: 'partyId'});
	
	var listImage = [];
	var pathScanFile = null;
	$('#document').ready(function(){
		$('#totalProductWeight').text('0');
		var stateProvinceGeoData = new Array();
		$("#countryGeoId").jqxDropDownList({source: countryData, width: 200, displayMember: "geoName", valueMember: "geoId"});
		$("#countryGeoId").jqxDropDownList('val', 'VNM');
		$("#stateProvinceGeoId").jqxDropDownList({source: stateProvinceGeoData, width: 200, displayMember: "value", valueMember: "id"});
		
		$("#postalCode").jqxInput({width: 195});
		$("#address1").jqxInput({width: 195});
		$("#address2").jqxInput({width: 195});
		updateStateProvince();
	
		$('#originProductStoreId').val('${originProductStore}');
			
		$('#jqxFileScanUpload').jqxWindow({ width: 400, modalZIndex: 10000, height: 220, isModal: true, autoOpen: false });
		initAttachFile();
		update({
			partyId: $("#partyIdTo").val(),
			}, 'getFacilityByPartyId' , 'listFacilities', 'facilityId', 'facilityName', 'destFacilityId');
		
		update({
			facilityId: $("#originFacilityId").val(),
			contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
		
		update({
			facilityId: $("#destFacilityId").val(),
			contactMechPurposeTypeId: "SHIPPING_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
	});
	$("#originFacilityId").on('change', function(event){
		update({
			facilityId: $("#originFacilityId").val(),
			contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
	});
	$("#destFacilityId").on('change', function(event){
		update({
			facilityId: $("#destFacilityId").val(),
			contactMechPurposeTypeId: "SHIPPING_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
	});
	
	function addOriginFacilityAddress(){
		var originFacilityId = $('#originFacilityId').val();
		if (originFacilityId){
			$('#seletedFacilityId').text($('#originFacilityId').text());
			contactMechPurposeTypeId = "SHIP_ORIG_LOCATION";
			$("#addPostalAddressWindow").jqxWindow("open");
		} else {
			bootbox.dialog("${uiLabelMap.PleaseChooseFacilityBefore}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		}
	}
	
	function addDestFacilityAddress(){
		var destFacilityId = $('#destFacilityId').val();
		if (destFacilityId){
			$('#seletedFacilityId').text($('#destFacilityId').text());
			contactMechPurposeTypeId = "SHIPPING_LOCATION";
			$("#addPostalAddressWindow").jqxWindow("open");
		} else {
			bootbox.dialog("${uiLabelMap.PleaseChooseFacilityBefore}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		}
	}
	$("#newAddrOkButton").click(function (event) {
		if (!$("#countryGeoId").val()){
			bootbox.dialog("${uiLabelMap.PleaseChooseCountryBefore}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		} else if (!$("#countryGeoId").val()){
			bootbox.dialog("${uiLabelMap.PleaseChooseProvinceBefore}", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		} else {
			var validate = $('#addPostalAddressWindow').jqxValidator('validate');
			if (validate){
				var facilityIdTemp = null;
				if ("SHIP_ORIG_LOCATION" == contactMechPurposeTypeId){
					facilityIdTemp = $("#originFacilityId").val();
				} else {
					facilityIdTemp = $("#destFacilityId").val();
				}
				if (facilityIdTemp){
					bootbox.confirm("${uiLabelMap.DAAreYouSureSave}", "${uiLabelMap.CommonCancel}", "${uiLabelMap.CommonSave}",function(result){ 
						if(result){
							jQuery.ajax({
								url: "createFacilityContactMechPostalAddress",
								type: "POST",
								async: false,
								data: {
									facilityId: facilityIdTemp,
									contactMechTypeId: "POSTAL_ADDRESS", 
									contactMechPurposeTypeId : contactMechPurposeTypeId, 
									address1: $('#address1').val(), 
									address2: $('#address2').val(),
									countryGeoId: $('#countryGeoId').val(),
									stateProvinceGeoId: $('#stateProvinceGeoId').val(),
									postalCode: $('#postalCode').val(),
									},
								success: function(res) {
									$('#addPostalAddressWindow').jqxWindow('close');
									if ("SHIP_ORIG_LOCATION" == contactMechPurposeTypeId){
										update({
											facilityId: $("#originFacilityId").val(),
											contactMechPurposeTypeId: "SHIP_ORIG_LOCATION",
											}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'originContactMechId');
									} else {
										update({
											facilityId: $("#destFacilityId").val(),
											contactMechPurposeTypeId: "SHIPPING_LOCATION",
											}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'destContactMechId');
									}
					       	  	}
							});
						}
					});
				} else {
					bootbox.dialog("${uiLabelMap.PleaseChooseFacilityBefore}", [{
		                "label" : "${uiLabelMap.CommonOk}",
		                "class" : "btn btn-primary standard-bootbox-bt",
		                "icon" : "fa fa-check",
		                }]
		            );
				}
			}
		}
	});
	function update(jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        success: function(res) {
	        	var json = res[data];
	            renderHtml(json, key, value, id);
	        }
	    });
	}
	function renderHtml(data, key, value, id){
		var y = "";
		var source = new Array();
		var index = 0;
		for (var x in data){
			index = source.length;
			var row = {};
			row[key] = data[x][key];
			row['description'] = data[x][value];
			source[index] = row;
		}
		if($("#"+id).length){
			$("#"+id).jqxDropDownList('clear');
			$("#"+id).jqxDropDownList({source: source, selectedIndex: 0});
		}
	}
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
    function selectFacility(facilityId){
        $("#originFacilityId").jqxDropDownList('selectItem',facilityId);
        $("#facilityWindow").jqxWindow('close');
    }
    function getFacilityList(){
        var tmpS = $("#jqxgridFAINV").jqxGrid('source');
        tmpS._source.url = "jqxGeneralServicer?sname=getAvailableINV&orderId=${parameters.orderId}";
        $("#jqxgridFAINV").jqxGrid('source', tmpS);
        $("#facilityWindow").jqxWindow('open');
    }
    function getInventoryItemTotalByFacility(){
    	var facId = $("#originFacilityId").val();
    	if (facId){
    		var rows = $('#jqxgrid1').jqxGrid('getrows');
    		if (rows && rows.length > 0){
        		for(var i = 0; i < rows.length; i++){
        			(function(i){
        				var data = $('#jqxgrid1').jqxGrid('getrowdata', i);
            			var exp = data.expireDate;
        				var expireDate = formatFullDate(exp);
        				jQuery.ajax({
            				url: "getDetailQuantityInventory",
            				type: "POST",
            				data: {
            					productId: data.productId,
            					expireDate: expireDate,
            					originFacilityId: facId,
            				},
            				success: function(res){
            					setTimeout(function(){
            						if (parseInt(res.availableToPromiseTotal) <= 0){
                						var id = $('#jqxgrid1').jqxGrid('getrowid', i);
                    					$("#jqxgrid1").jqxGrid('setcellvaluebyid', id, "quantityOnHandTotal", res.quantityOnHandTotal);
                    					$("#jqxgrid1").jqxGrid('setcellvaluebyid', id, "availableToPromiseTotal", res.availableToPromiseTotal);
                					} else if(res != undefined){
                						var id = $('#jqxgrid1').jqxGrid('getrowid', i);
                    					$("#jqxgrid1").jqxGrid('setcellvaluebyid', id, "quantityOnHandTotal", res.quantityOnHandTotal);
                    					$("#jqxgrid1").jqxGrid('setcellvaluebyid', id, "availableToPromiseTotal", res.availableToPromiseTotal);
                					}
            					}, 200);
                            },
                            error: function(response){
                            }
            			});
        			}(i));
        		}
    		}
    	}
    }
    
	//Create orderId 
	$("#orderId").text('${parameters.orderId}');
	$('#alterpopupWindow input[name=orderId]').val('${parameters.orderId?if_exists}');
	
	//Set Value for statusId
	$('#alterpopupWindow input[name=statusId]').val('DLV_CREATED');
	
	//Create CurrencyUom
	$('#alterpopupWindow input[name=currencyUomId]').val('${orderHeader.currencyUom?if_exists}');
	
	//Create orderDate
	$('#alterpopupWindow input[name=orderDate]').val('${orderHeader.orderDate?if_exists}');
	
//	//Create ProductStore
//	var prodStoreData = [];
//	<#list listProStore as item>
//		var row = {};
//		<#assign description = StringUtil.wrapString(item.storeName) >
//		row['productStoreId'] = '${item.productStoreId}';
//		row['description'] = '${description}';
//		prodStoreData[${item_index}] = row;
//	</#list>
//	$("#originProductStoreId").jqxDropDownList({source: prodStoreData, selectedIndex: 0, displayMember: 'description', valueMember: 'productStoreId'});
//	
	//Create Order date
	<#assign orderDateDisplay = StringUtil.wrapString(Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate?if_exists, "dd/MM/yyyy", locale, timeZone))>
	$('#orderDateDisplay').text('${orderDateDisplay?if_exists}');
	
	//Load data grid for jqxGrid1
    /*var tmpS = $("#jqxgrid1").jqxGrid('source');
    tmpS._source.url = "jqxGeneralServicer?sname=getListDeliveryItem&facilityId=" + facilityData[0].facilityId + "&orderId=" + "${parameters.orderId?if_exists}";
    $("#jqxgrid1").jqxGrid('source', tmpS);*/
	$('#originFacilityId').on('change', function(event){
		var tmpS = $("#jqxgrid1").jqxGrid('source');
        tmpS._source.url = "jqxGeneralServicer?sname=getListOrderItemDelivery&orderId=${parameters.orderId?if_exists}&facilityId=" + $('#originFacilityId').val();
        $("#jqxgrid1").jqxGrid('source', tmpS);
        $("#jqxgrid1").on("bindingComplete", function (event) {
    		getInventoryItemTotalByFacility();
        });
	});
	
	$('#estimatedStartDate').jqxDateTimeInput({width: 200});
	$('#estimatedArrivalDate').jqxDateTimeInput({width: 200});
	
	//Create deliveryDate
	$("#deliveryDate").jqxDateTimeInput({width: 200, formatString: 'dd/MM/yyyy', disabled: true});
	$("#deliveryDate").jqxDateTimeInput('val', estimatedDeliveryDate);
	
	//Create noNumber
	$('#no').jqxInput({width: 195});
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
                    map.fromOrderId = '${parameters.orderId}';
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
	// update the edited row when the user clicks the 'Save' button: Create new Delivery.
	$("#alterCancel").click(function () {
	     $("#alterpopupWindow").jqxWindow('close');
	});
	$("#alterCancel2").click(function () {
	    $("#popupDeliveryDetailWindow").jqxWindow('close');
	});
    $("#alterSave").click(function () {
		var row;
		//Get List Order Item
		var selectedIndexs = $('#jqxgrid1').jqxGrid('getselectedrowindexes');
		if(selectedIndexs.length == 0){
		    bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		    return false;
		} 
		bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}",function(result){ 
		    if(result){   
		        $("#alterpopupWindow").jqxWindow('close');
        		var listOrderItems = [];
        		var orderIdTmp;
        		for(var i = 0; i < selectedIndexs.length; i++){
        			var data = $('#jqxgrid1').jqxGrid('getrowdata', selectedIndexs[i]);
        			var map = {};
        			map['orderItemSeqId'] = data.orderItemSeqId;
        			map['orderId'] = data.orderId;
        			orderIdTmp = data.orderId;
        			map['quantity'] = data.requiredQuantityTmp;
        			var exp = data.expireDate;
        			if (exp){
        				map['expireDate'] = exp.getTime();
        			}
        			listOrderItems[i] = map;
        		}
        		var listOrderItems = JSON.stringify(listOrderItems);
        		row = { 
        				orderId: orderIdTmp,
        				currencyUomId:$('#alterpopupWindow input[name=currencyUomId]').val(),
        				statusId:$('#alterpopupWindow input[name=statusId]').val(),
        				originContactMechId:$('#originContactMechId').val(),
        				destFacilityId:$('#destFacilityId').val(),
        				originProductStoreId:$('#originProductStoreId').val(),
        				partyIdTo:$('#partyIdTo').val(),
        				partyIdFrom:$('#partyIdFrom').val(), 
//        				createDate:new Date(nowTimestamp.getTime()),	
        				destContactMechId:$('#destContactMechId').val(),
        				originFacilityId:$('#originFacilityId').val(),
        				deliveryDate:$('#deliveryDate').jqxDateTimeInput('getDate'),
        				estimatedStartDate:$('#estimatedStartDate').jqxDateTimeInput('getDate'),
        				estimatedArrivalDate:$('#estimatedArrivalDate').jqxDateTimeInput('getDate'),
        				no:$('#no').val(),
        				defaultWeightUomId : $('#listWeightUomId').val(),
        				listOrderItems:listOrderItems
            	};
        		$("#jqxgrid").jqxGrid('addRow', null, row, "first");
        		/* Disable for refreshing purpose: 
        		// select the first row and clear the selection.
        		$("#jqxgrid").jqxGrid('clearSelection');                        
        		$("#jqxgrid").jqxGrid('selectRow', 0);  
            	$("#alterpopupWindow").jqxWindow('close');*/
        		var tmpUrl = window.location.href;
        		if(tmpUrl.indexOf("orderId") < 0){
        		    tmpUrl += "?orderId=${orderId}";
        		}
        		if(tmpUrl.indexOf("deliveries-tab") < 0){
        		    tmpUrl += "&activeTab=deliveries-tab";
        		}
        		//window.location.href = tmpUrl; disable for demo.
        		$("#jqxgrid").jqxGrid('updatebounddata'); 
		    }
		});
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
		if($("#jqxgridDlv").is('*[class^="jqx"]')){
			$("#jqxgridDlv").jqxGrid('updatebounddata');
		}
		$('#jqxgrid2').jqxGrid('clearselection');
	});
	
	function functionAfterUpdate2(){
//	    var tmpS = $("#jqxgrid2").jqxGrid('source');
//	    tmpS._source.url = "jqxGeneralServicer?sname=getListDeliveryItem&deliveryId=" + glDeliveryId;
//	    $("#jqxgrid2").jqxGrid('source', tmpS);
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
		$('#actualArrivalDate').jqxDateTimeInput({width: 200});
		$('#actualArrivalDate').hide();
		$('#actualStartDate').jqxDateTimeInput({width: 200});
		$('#actualStartDate').hide();
		var listInv = [];
	    var tmpValue;
	    var glDeliveryId;
	    var glOriginFacilityId;
	    var glDeliveryStatusId;
		function showDetailPopup(deliveryId){
			checkRoleByDelivery(deliveryId);
			var deliveryDT;
			glDeliveryId = deliveryId;
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
	                       data: {'orderId': '${parameters.orderId}', 'facilityId':deliveryDT.originFacilityId, 'deliveryId': deliveryDT.deliveryId},
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
			$("#destContactMechIdDT").text(deliveryDT.originAddress);
			
			//Create originContactMechIdDT
			$("#originContactMechIdDT").text(deliveryDT.destAddress);
			
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
				$('#actualStartLabel').show;
				$('#actualStartLabel').html("");
				$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
				
				$('#actualArrivalLabel').hide();
				$('#actualStartDateDis').hide();
				
				$('#actualStartDate').show();
				$('#actualArrivalDate').hide();
			}
			if ("DLV_CONFIRMED" == deliveryDT.statusId){
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
			if ("DLV_EXPORTED" == deliveryDT.statusId){
				$('#actualStartDate').hide();
				$('#actualArrivalDate').hide();
				
				$('#actualStartLabel').show();
				$('#actualStartLabel').html("");
				$('#actualStartLabel').append('${uiLabelMap.ActualExportedDate}:');
				
				$('#actualArrivalLabel').hide();
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
				
				/*$('#actualArrivalDateDis').show();
				$('#actualArrivalDateDis').html("");
				var arrDate = deliveryDT.actualArrivalDate;
				var temp2 = arrDate.split(" ");
				var d2 = temp[0].split("-");
				$('#actualArrivalDateDis').append(d2[2]+'/'+d2[1]+'/'+d2[0]);*/
				
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
	//Create Window
    var checkStorekeeper = false;
	$("#alterpopupWindow").jqxWindow({
		maxWidth: 1500, minWidth: 950, minHeight: 656, maxHeight: 1200, resizable: true,  isModal: true, modalZIndex: 10000, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme:theme           
	});
	initGridjqxgrid1();
	$("#alterpopupWindow").on('open', function (event) {
		var tmpS = $("#jqxgrid1").jqxGrid('source');
	    tmpS._source.url = "jqxGeneralServicer?sname=getListOrderItemDelivery&orderId=${parameters.orderId?if_exists}&facilityId=" + $('#originFacilityId').val();
	    $("#jqxgrid1").jqxGrid('source', tmpS);
	    $("#jqxgrid1").on("bindingComplete", function (event) {
			getInventoryItemTotalByFacility();
	    });
	});
	
	$('#listWeightUomId').on('change', function(event){
		updateTotalWeight(event.args.rowindex);
	});
	function rowsunelectfunction2(event){
		updateTotalWeight();
	}
	function updateTotalWeight(){
		var totalProductWeight = 0;
		var selectedIndexs = $('#jqxgrid1').jqxGrid('getselectedrowindexes');
		for(var i = 0; i < selectedIndexs.length; i++){
			var data = $('#jqxgrid1').jqxGrid('getrowdata', selectedIndexs[i]);
			var baseWeightUomId = data.baseWeightUomId;
			var defaultWeightUomId = $('#listWeightUomId').val();
			var itemWeight = 0;
			if (data.availableToPromiseTotal < 1){
				itemWeight = 0;
			} else {
				itemWeight = (data.requiredQuantityTmp)*(data.weight);
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
	function rowselectfunction2(event){
	    if (typeof event.args.rowindex != 'number'){
	        var tmpArray = event.args.rowindex;
	        for(i = 0; i < tmpArray.length; i++){
	            if(checkRequiredData(tmpArray[i])){
	                $('#jqxgrid1').jqxGrid('clearselection');
	                break; // Stop for first item
	            }
	        }
	    } else{
	        checkRequiredData(event.args.rowindex);
	    }
	    updateTotalWeight();
	}
	function checkRequiredData(rowindex){
	    var data = $('#jqxgrid1').jqxGrid('getrowdata', rowindex);
	    if(data == undefined){
	        return true; // to break the loop
	    } 
	    if (data.availableToPromiseTotal < 1){
	    	displayNotEnough(rowindex, "${uiLabelMap.FacilityNotEnoughProduct}");
	    	return true;
	    }
	    if(data.requiredQuantityTmp < 1){
	        displayAlert(rowindex, "${uiLabelMap.NumberGTZ}");
	        return true;
	    }else if(data.requiredQuantityTmp > (data.requiredQuantity - data.createdQuantity)){
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
	        	 $("#jqxgrid1").jqxGrid('unselectrow', rowindex);
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
	            //$('#jqxgrid1').jqxGrid('unselectrow', rowindex);
	            $("#jqxgrid1").jqxGrid('begincelledit', rowindex, "requiredQuantityTmp");
	        }
	        }]
	    );
	}
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