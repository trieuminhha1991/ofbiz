<script type="text/javascript">
	var contactMechDataColumn = new Array();
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("ownerPartyId", Static["com.olbius.util.MultiOrganizationUtil"].getCurrentOrganization(delegator)), null, null, null, false)>
	var facilityData = new Array();
	<#list facilities as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.facilityName?if_exists)/>
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['description'] = '${description?if_exists}';
		facilityData[${item_index}] = row;
	</#list>
	
	<#assign statusItems = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "DELIVERY_STATUS"), null, null, null, false)>
	var statusData = new Array();
	<#list statusItems as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)/>
		row['statusId'] = '${item.statusId?if_exists}';
		row['description'] = '${description?if_exists}';
		statusData[${item_index}] = row;
	</#list>
	<#if parameters.orderId?has_content>
		<#assign requirementByOrder = delegator.findList("OrderRequirement", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("orderId", parameters.orderId), null, null, null, false)>
		<#if requirementByOrder?has_content>
			<#assign reqOrderStatus = requirementByOrder[0].statusId>
		</#if>
	</#if>
	<#assign postalAddress = delegator.findList("FacilityContactMechDetail", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("contactMechPurposeTypeId", "SHIPPING_LOCATION"), null, null, null, false) />
	var postalAddressData = new Array();
	<#list postalAddress as item>
		var row = {};
		row['contactMechId'] = '${item.contactMechId}';
		row['description'] = '${item.address1?if_exists}';
		postalAddressData[${item_index}] = row;
	</#list>
</script>

<#assign dataField="[{ name: 'deliveryId', type: 'string' },
				{ name: 'deliveryTypeId', type: 'string' },
				{ name: 'statusId', type: 'string' },
				{ name: 'partyIdTo', type: 'string' },
				{ name: 'destContactMechId', type: 'string' },
				{ name: 'partyIdFrom', type: 'string' },
				{ name: 'originContactMechId', type: 'string' },
				{ name: 'orderId', type: 'string' },
				{ name: 'returnId', type: 'string' },
				{ name: 'originProductStoreId', type: 'string' },
				{ name: 'originFacilityId', type: 'string' },
				{ name: 'destProductStoreId', type: 'string' },
				{ name: 'destFacilityId', type: 'string' },
				{ name: 'createDate', type: 'date', other: 'Timestamp' },
				{ name: 'deliveryDate', type: 'date', other: 'Timestamp' },
				{ name: 'totalAmount', type: 'number' },
				{ name: 'no', type: 'string' },
					]"/>

<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.ReceiptId)}', datafield: 'deliveryId', width: '10%', align: 'center', cellsrenderer:
			       		function(row, colum, value){
							return '<span><a href=\"getDetailPurchaseDelivery?deliveryId=' + value + '\"> ' + value  + '</a></span>'
			        	} 
					},">
<#if parameters.orderId?has_content>
	<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.OrderId)}', datafield: 'orderId', width: '12%', align: 'center',
					},">
<#elseif parameters.returnId?has_content>
	<#assign columnlist = columnlist + "{ text: '${StringUtil.wrapString(uiLabelMap.OrderId)}', datafield: 'orderId', width: '12%', align: 'center',
					},">			
</#if>
	<#assign columnlist = columnlist + "
					{ text: '${StringUtil.wrapString(uiLabelMap.ReceiveDate)}', datafield: 'deliveryDate',filtertype : 'range', width: '12%', align: 'center', cellsformat: 'd', editable: false, cellsformat: 'dd/MM/yyyy',},
					{ text: '${StringUtil.wrapString(uiLabelMap.ReceiptToFacility)}',filtertype  : 'checkedlist', datafield: 'destFacilityId', minwidth: '15%', align: 'center',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < facilityData.length; i++){
								if(facilityData[i].facilityId == value){
									return '<span title=' + value + '>' + facilityData[i].description + '</span>'
								}
							}
						},createfilterwidget : function(row,column,widget){
							var filterBox = new $.jqx.dataAdapter(facilityData,{autoBind : true});
							var records = filterBox.records;
							records.splice(0,0,'${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}');
							widget.jqxDropDownList({displayMember : 'description',valueMember : 'facilityId',dropDownHeight : 200,source : records});
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.FacilityAddress)}', datafield: 'destContactMechId', minwidth: '15%', align: 'center',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < postalAddressData.length; i++){
								if(postalAddressData[i].contactMechId == value){
									return '<span title=' + value + '>' + postalAddressData[i].description + '</span>'
								}
							}
						}
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.createDate)}', datafield: 'createDate', filtertype : 'range',width: '12%', align: 'center', cellsformat: 'd', editable: false, cellsformat: 'dd/MM/yyyy',},
					{ text: '${StringUtil.wrapString(uiLabelMap.Status)}',filtertype : 'checkedlist', datafield: 'statusId', width: '12%', align: 'center',
						cellsrenderer: function(row, column, value){
							for(var i = 0; i < statusData.length; i++){
								if(statusData[i].statusId == value){
									return '<span title=' + value + '>' + statusData[i].description + '</span>'
								}
							}
						},createfilterwidget : function(row,column,widget){
							var filterBox = new $.jqx.dataAdapter(statusData,{autoBind : true});
							var records = filterBox.records;
							records.splice(0,0,'${StringUtil.wrapString(uiLabelMap.wgfilterselectallstring)}');
							widget.jqxDropDownList({displayMember : 'description',valueMember : 'statusId',dropDownHeight : 200,source : records});
						}
					},
					"/>
<#if parameters.orderId?has_content>
	<#if reqOrderStatus?has_content && reqOrderStatus == "REQ_CONFIRMED">
		<#assign deliveryByOrders = delegator.findList("Delivery", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("orderId", parameters.orderId?if_exists), null, null, null, false)>
		<#if deliveryByOrders?has_content>
			<@jqGrid filtersimplemode="true" customTitleProperties="ListDelivery" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
				showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
				url="jqxGeneralServicer?sname=getListDelivery&orderId=${parameters.orderId?if_exists}&deliveryTypeId=DELIVERY_PURCHASE" 
				customcontrol2="fa-cubes@${uiLabelMap.OrderDetail}@purchaseOrderView?orderId=${parameters.orderId?if_exists}&countryGeoId=${parameters.countryGeoId?if_exists}"
			/>
		<#else>
			<@jqGrid filtersimplemode="true" customTitleProperties="ListDelivery" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
				showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
				url="jqxGeneralServicer?sname=getListDelivery&orderId=${parameters.orderId?if_exists}&deliveryTypeId=DELIVERY_PURCHASE" 
				customcontrol2="fa-cubes@${uiLabelMap.OrderDetail}@purchaseOrderView?orderId=${parameters.orderId?if_exists}&countryGeoId=${parameters.countryGeoId?if_exists}"
				customcontrol1="icon-plus@${uiLabelMap.CommonCreateNew}@createNewDelivery?orderId=${parameters.orderId?if_exists}&countryGeoId=${parameters.countryGeoId?if_exists}"
			/>
		</#if>
	<#else>
		<@jqGrid filtersimplemode="true" customTitleProperties="ListDelivery" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
			showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
			url="jqxGeneralServicer?sname=getListDelivery&orderId=${parameters.orderId?if_exists}&deliveryTypeId=DELIVERY_PURCHASE" 
			customcontrol1="fa-cubes@${uiLabelMap.OrderDetail}@purchaseOrderView?orderId=${parameters.orderId?if_exists}&countryGeoId=${parameters.countryGeoId?if_exists}"
		/>
	</#if>
<#elseif parameters.returnId?has_content>
	<@jqGrid customTitleProperties="ListDelivery" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		url="jqxGeneralServicer?sname=getListDelivery&returnId=${parameters.returnId?if_exists}&deliveryTypeId=DELIVERY_RETURN" 
		customcontrol2="fa-cubes@${uiLabelMap.OrderDetail}@viewDetailReturnOrder?returnId=${parameters.returnId?if_exists}"
		customcontrol1="icon-plus@${uiLabelMap.CommonCreateNew}@createNewDelivery?returnId=${parameters.returnId?if_exists}"
	/>			
<#else>
	<@jqGrid filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false"
		url="jqxGeneralServicer?sname=getListDelivery&deliveryTypeId=DELIVERY_PURCHASE"
	/>	
</#if>
					
