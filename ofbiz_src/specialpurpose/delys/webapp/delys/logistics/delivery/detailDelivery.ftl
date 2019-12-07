<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification.js"></script>
<script type="text/javascript">
	<#assign deliveryId = parameters.deliveryId?if_exists/>
	<#assign orderId = parameters.orderId?if_exists/>
	<#assign returnId = parameters.returnId?if_exists/>
	<#assign company = Static["com.olbius.util.MultiOrganizationUtil"].getCurrentOrganization(delegator)/>
	company = '${company?if_exists}'; 
	deliveryId = "${parameters.deliveryId?if_exists}"; 
	orderId = "${parameters.orderId?if_exists}";
	returnId = "${parameters.returnId?if_exists}";
	checkInit = false;
	<#assign checkDelivery = delegator.findList("Delivery", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", "${parameters.orderId?if_exists}", "deliveryTypeId", "DELIVERY_PURCHASE")), null, null, null, false)>
	<#if checkDelivery?has_content>
		window.location.href = "getDetailPurchaseDelivery?deliveryId=${checkDelivery[0].deliveryId}";
	</#if>
	
	<#if deliveryId?has_content>
		<#assign delivery = delegator.findOne("Delivery", {"deliveryId" : deliveryId}, false)/>
		<#assign deliveryDateDis = delivery.deliveryDate?if_exists/>
	</#if>
	<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("ownerPartyId", company), null, null, null, false)>
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
		<#assign description = StringUtil.wrapString(item.get("description", locale))/>
		row['statusId'] = '${item.statusId?if_exists}';
		row['description'] = '${description?if_exists}';
		statusData[${item_index}] = row;
	</#list>
	
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var uomData = new Array();
	<#list uoms as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.description?if_exists)/>
		row['uomId'] = '${item.uomId?if_exists}';
		row['description'] = '${description?if_exists}';
		uomData[${item_index}] = row;
	</#list>
	
	<#assign returnReasons = delegator.findList("ReturnReason", null, null, null, null, false)>
	var returnReasonData = new Array();
	<#list returnReasons as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale))/>
		row['returnReasonId'] = '${item.returnReasonId?if_exists}';
		row['description'] = '${description?if_exists}';
		returnReasonData[${item_index}] = row;
	</#list>
	reqFacilityId = null;
	reqContactMechId = null;
	reqDate = null;
	<#if orderId?has_content>
		
		<#assign orderRequirement = delegator.findList("OrderRequirement", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("orderId", "${parameters.orderId?if_exists}"), null, null, null, false)>
		<#if orderRequirement?has_content>
			reqFacilityId = '${orderRequirement[0].facilityId}';
			reqContactMechId = '${orderRequirement[0].contactMechId}';
			reqDate = '${orderRequirement[0].requirementDate}';
		</#if>
		
		<#assign orderItems = delegator.findList("OrderItemAndProduct", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("orderId", "${parameters.orderId?if_exists}"), null, null, null, false)>
		
		<#assign products = delegator.findList("OrderItemGroupByProduct", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("orderId", "${parameters.orderId?if_exists}"), null, null, null, false)>
		var productData = new Array();
		<#list products as item>
			var row = {};
			<#assign productId = '${item.productId?if_exists}'/>
			row['productId'] = '${productId}';
			row['orderId'] = '${item.orderId?if_exists}';
			row['baseQuantityUomId'] = '${item.baseQuantityUomId?if_exists}';
			productData[${item_index}] = row;
		</#list>
		var orderItemData = new Array();
		<#list orderItems as item >
		 	var row = {};
		 	row['orderId'] = '${item.orderId?if_exists}';
		 	row['orderItemSeqId'] = '${item.orderItemSeqId?if_exists}';
		 	row['productId'] = '${item.productId?if_exists}';
		 	row['productName'] = '${item.productName?if_exists}';
		 	row['expireDate'] = '${item.expireDate?if_exists}';
		 	<#if item.datetimeManufactured?has_content>
		 		row['datetimeManufactured'] = '${item.datetimeManufactured?string["dd/MM/yyyy"]}';
		 	<#else>
		 		row['datetimeManufactured'] = '';
		 	</#if>
		 	row['quantity'] = '${item.quantity?if_exists}';
		 	<#assign quantityUomBySupplier = Static["com.olbius.util.ProductUtil"].getQuantityUomBySupplier(delegator, productId, orderId)/>
		 	row['quantityUomId'] = '${quantityUomBySupplier}';
		 	<#assign convertNumber = 1 />
		 	<#list products as prod >
		 		<#if prod.productId == item.productId>
		 			<#assign productId = item.productId	>
		 			<#assign uomToId = prod.baseQuantityUomId>
		 			<#assign uomFromId = quantityUomBySupplier>
		 			<#if uomFromId?has_content && uomToId?has_content && productId?has_content>
				 		<#assign convertNumber = Static["com.olbius.util.ProductUtil"].getConvertPackingNumber(delegator, productId, uomFromId, uomToId)/>
				 	</#if>
		 		</#if>
		 	</#list>
		 	row['productPacking'] = '1x${convertNumber}';
		 	orderItemData[${item_index}] = row;
		</#list>
	<#elseif returnId?has_content>
		<#assign returnItems = delegator.findList("ReturnItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("returnId", "${parameters.returnId?if_exists}"), null, null, null, false)>
		
		<#assign products = delegator.findList("ReturnItemGroupByProduct", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("returnId", "${parameters.returnId?if_exists}"), null, null, null, false)>
		var productData = new Array();
		<#list products as item>
			var row = {};
			<#assign productId = '${item.productId?if_exists}'/>
			row['productId'] = '${productId}';
			row['returnId'] = '${item.returnId?if_exists}';
			row['productName'] = '${item.productName?if_exists}';
			row['baseQuantityUomId'] = '${item.baseQuantityUomId?if_exists}';
			productData[${item_index}] = row;
		</#list>
		var returnItemData = new Array();
		<#list returnItems as item >
		 	var row = {};
		 	row['returnId'] = '${item.returnId?if_exists}';
		 	row['returnItemSeqId'] = '${item.returnItemSeqId?if_exists}';
		 	row['productId'] = '${item.productId?if_exists}';
		 	row['expireDate'] = '${item.expireDate?if_exists}';
		 	row['quantity'] = '${item.alterQuantity?if_exists}';
		 	row['quantityUomId'] = '${item.alterQuantityUomId?if_exists}';
		 	<#assign convertNumber = 1 />
		 	<#list products as prod >
		 		<#if prod.productId == item.productId>
		 			<#assign productId = item.productId	>
		 			<#assign uomToId = prod.baseQuantityUomId>
		 			<#assign uomFromId = item.quantityUomId>
		 			<#if uomFromId?has_content && uomToId?has_content && productId?has_content>
				 		<#assign convertNumber = Static["com.olbius.util.ProductUtil"].getConvertPackingNumber(delegator, productId, uomFromId, uomToId)/>
				 	</#if>
		 		</#if>
		 	</#list>
		 	row['productPacking'] = '1x${convertNumber}';
		 	returnItemData[${item_index}] = row;
		</#list>
	</#if>
	
	<#assign dlvTypes = delegator.findList("DeliveryType", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("parentDeliveryTypeId", "DELIVERY_OUT"), null, null, null, false)>
	var dlvTypeData = new Array();
	<#list dlvTypes as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale))/>
		row['deliveryTypeId'] = '${item.deliveryTypeId?if_exists}';
		row['description'] = '${description?if_exists}';
		dlvTypeData[${item_index}] = row;
	</#list>
	
	<#assign invStatus = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "INV_NON_SER_STTS"), null, null, null, false)>
	var InvStatusData = new Array();
	<#list invStatus as item>
		var row = {};
		<#assign description = StringUtil.wrapString(item.get("description", locale))/>
		row['inventoryStatusId'] = '${item.statusId?if_exists}';
		row['description'] = '${description?if_exists}';
		InvStatusData[${item_index}] = row;
	</#list>
	
	
</script>
<div class="row-fluid">
<#if deliveryId?has_content && security.hasPermission("DELIVERY_UPDATE", session)>
	<div class="span12 no-left-margin" style="text-align: right">
		<div class="widget-toolbar no-border">
			<a href="/delys/control/purchaseOrderView?orderId=${delivery.orderId?if_exists}"><i class="fa-cubes"></i>${uiLabelMap.OrderDetail}</a>
			<a href="/delys/control/getPurchaseDeliverys?deliveryType=DELIVERY_PURCHASE&orderId=${delivery.orderId?if_exists}&countryGeoId=${parameters.countryGeoId?if_exists}"><i class="fa-file-text-o"></i>${uiLabelMap.Receipt}</a>
		</div>	
	</div>
<#elseif orderId?has_content>
	<div class="span12 no-left-margin" style="text-align: right">
		<div class="widget-toolbar no-border">
			<a href="/delys/control/purchaseOrderView?orderId=${parameters.orderId?if_exists}"><i class="fa-cubes"></i>${uiLabelMap.OrderDetail}</a>
			<a href="/delys/control/getPurchaseDeliverys?deliveryType=DELIVERY_PURCHASE&orderId=${parameters.orderId?if_exists}&countryGeoId=${parameters.countryGeoId?if_exists}"><i class="fa-file-text-o"></i>${uiLabelMap.Receipt}</a>
		</div>	
	</div>
<#elseif returnId?has_content>
	<div class="span12 no-left-margin" style="text-align: right">
		<div class="widget-toolbar no-border">
			<a href="/delys/control/viewDetailReturnOrder?returnId=${parameters.returnId?if_exists}"><i class="fa-cubes"></i>${uiLabelMap.OrderDetail}</a>
			<a href="/delys/control/getDeliveryByReturn?deliveryType=DELIVERY_RETURN&returnId=${parameters.returnId?if_exists}"><i class="fa-file-text-o"></i>${uiLabelMap.Receipt}</a>
			<a onclick="showConfirmPopup()"><i class="icon-save"></i>${uiLabelMap.Save}</a>
			<a href="/delys/control/createNewDelivery?returnId=${parameters.returnId?if_exists}"><i class="icon-refresh"></i>${uiLabelMap.Cancel}</a>
		</div>	
	</div>
</#if>
<div id="notifyId" style="display: none;">
	<div>
		${uiLabelMap.createSuccessfully}
	</div>
</div>
<div id="containerNotify" style="width: 100%; height: 20%; margin-top: 15px; overflow: auto;">
</div>
<div id="notifyMissInfoId" style="display: none;">
	<div>
		${uiLabelMap.EnterMissInformation}
	</div>
</div>
<div class="span12 no-left-margin">
	<h3 style="text-align:center;font-weight:bold; padding:0;line-height:28px;text-transform:uppercase;">
		${uiLabelMap.CargoUnloadingReport}
	</h3>
<div>
<div class="span12 no-left-margin mgt10">
	<div class="row-fluid">
		<div class="span4 no-left-margin">
			<div style="text-align: left;display:inline; margin-left: 10%">
				<#if deliveryId?has_content>
					<#if security.hasPermission("DELIVERY_UPDATE", session)>
						<#if delivery.pathScanFile?has_content>
							<a href="${delivery.pathScanFile}" target='_blank'><i class='fa-file-text-o'></i>${uiLabelMap.FileScan}</a>
						<#else>
							<a id="linkId" class="asterisk" onclick="showAttachFilePopup()"><i class="icon-upload"></i>${uiLabelMap.AttachFileScan}</a>
						</#if>
					<#elseif security.hasPermission("DELIVERY_VIEW", session)>
						<#if delivery.pathScanFile?has_content>
							<a href="${delivery.pathScanFile}" target='_blank'><i class='fa-file-text-o'></i>${uiLabelMap.FileScan}</a>
						</#if>
					</#if>
				<#else>
					<#if pathScanFile?has_content>
						<a href="${delivery.pathScanFile}" target='_blank'><i class='fa-file-text-o'></i>${uiLabelMap.FileScan}</a>
					<#else>
						<a id="linkId" class="asterisk" onclick="showAttachFilePopup()"><i class="icon-upload"></i>${uiLabelMap.AttachFileScan}</a>
					</#if>
				</#if>
			</div>
			<div style="text-align: right;display:inline; padding-left: 35%">
				${uiLabelMap.Template}: . . . 
			</div>
		</div>
	<div class="span4" style="text-align: center;">
		${uiLabelMap.PageNumber}: . . . . . /. . . .
	</div>

	<div class="span4 no-left-margin">
		<div style="display:inline;float:left;">${uiLabelMap.ReceiptType}:   ${receiveType?if_exists}</div>
		<#if security.hasPermission("DELIVERY_UPDATE", session)>
			<div id="print" style="display:inline;float:left; padding-left: 20%">
				<a target="_blank" href="/delys/control/printReceiveDelivery?deliveryId=${deliveryId?if_exists}&orderId=${orderId?if_exists}&returnId=${returnId?if_exists}"><i class="icon-print"></i>${uiLabelMap.CommonPrint}</a>
			</div>
		</#if>
	</div>
	</div>
</div>
<#if deliveryId?has_content>
	<#assign facilityDis = delegator.findOne("Facility", {"facilityId" : delivery.destFacilityId}, false)/>
	<#assign facilityAddressDis = delegator.findOne("PostalAddress", {"contactMechId" : delivery.destContactMechId}, false)/>
	<#assign deliveryDateDis = Static["com.olbius.util.CommonUtil"].convertTimestampToDate(delivery.deliveryDate)/>
	<div class="span12 mgt20" style="margin-bottom:2%">
		<div class="row-fluid">
		<div class="span3 row-desc row" style="text-align: left">
			<b style="display:inline;float:left;">${uiLabelMap.ReceiptDate}:</b> <b class="controls-desc">${deliveryDateDis}</b>
		</div>
		<div class="span2 no-left-margin row-desc row" style="text-align: left">
			<b>${uiLabelMap.PurchaseOrder}:</b><b style="margin-bottom: 2.5px !important;" class="controls-desc">${delivery.orderId}</b> 
		</div>
		<div class="span3 no-left-margin" style="text-align: left">
			<div style="float:left;">
			<b>${uiLabelMap.Vehicle}: </b>
			</div> </br>
			<div style="float:left;" >
			<b class="mgt20">${uiLabelMap.NumberPlate}: </b>
			</div>
		</div>
		<div class="span4 row-desc row" style="text-align: left">
			<div style="float:left;">
			<b>${uiLabelMap.FacilityToReceive}:</b> <b class="controls-desc"> ${facilityDis.facilityName}</b>
			</div> </br>
			<div style="float:left;" >
			<b class="mgt20">${uiLabelMap.FacilityAddress}:</b> <b class="controls-desc"> ${facilityAddressDis.address1}</b>
			</div>
		</div>
		</div>
	</div>
<#else>
<div class="span12 no-left-margin mgt20" style="margin-bottom:2%">
<div class="row-fluid">
	<div class="span3" style="text-align: left">
		<b style="display:inline;float:left;" class="asterisk">${uiLabelMap.ReceiptDate}:</b>
		<div id="deliveryDate" style="display:inline;float:left; margin-left:2%">
		</div>
	</div>
	<div class="span2 no-left-margin row-desc row" style="text-align: left">
		<b>${uiLabelMap.PurchaseOrder}:</b><b class="controls-desc" style="margin-bottom: 2.5px !important;">${orderId?if_exists}${returnId?if_exists}</b> 
	</div>
	<div class="span3 no-left-margin" style="text-align: left">
		<b style="display:inline; float:left;margin-left:10%">${uiLabelMap.Vehicle}:</b> 
		<input style="display:inline; float:right;" type="text" id="inputVehicle"/>
		<b style="display:inline; float:left;margin-left:10%" class="mgt10">${uiLabelMap.NumberPlate}:</b>
		<input style="display:inline; float:right; margin-top:2px" type="text" id="inputNumberPlate"/>
	</div>
	<div class="span4" style="text-align: left">
		<b style="display:inline;float:left;margin-left:10%" class="asterisk">${uiLabelMap.FacilityToReceive}:</b>
		<div id="destFacilityId" style="display:inline;float:right; margin-left:2%">
		</div>
		<b style="display:inline;float:left;margin-left:10%" class="mgt10 asterisk">${uiLabelMap.FacilityAddress}:</b>
		<div id="facilityCTMId" style="display:inline;float:right; margin-top:2px">
		</div>
	</div>
	</div>
</div>
</div>
</#if>
<#assign localeStr = "VI" />
	<#if locale = "en">
<#assign localeStr = "EN" />
	</#if>
<#assign dataField="[{ name: 'productId', type: 'string'},
			   { name: 'productName', type: 'string'},
			   { name: 'quantityUomId', type: 'string'},
			   { name: 'productPacking', type: 'string'},
			   { name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
			   { name: 'expireDate', type: 'date', other: 'Timestamp'},
			   { name: 'proDateIns', type: 'date', other: 'Timestamp'},
			   { name: 'expDateIns', type: 'date', other: 'Timestamp'},
			   { name: 'quantity', type: 'string'},
			   { name: 'inspectionQty', type: 'string'},
			   { name: 'actualExpireDate', type: 'date', other: 'Timestamp'},
			   { name: 'actualManufacturedDate', type: 'date', other: 'Timestamp'},
			   { name: 'actualDeliveredQuantity', type: 'string'},
			   { name: 'baseQuantityUomId', type: 'string'},
			   { name: 'convertNumber', type: 'string'},
			   { name: 'orderId', type: 'string'},
			   ]"/>
<#assign columngrouplist="  
						{ text: '${uiLabelMap.ImportPackingList}', align: 'center', name: 'PackingList' },
						{ text: '${uiLabelMap.Inspection}', align: 'center', name: 'Inspection' },
						{ text: '${uiLabelMap.Quantity}', align: 'center', name: 'Quantity' },
							" />
<#if deliveryId?has_content>
	<#assign columnlist="{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productId', filterable: true, minwidth: '15%', align: 'center', columntype: 'dropdownlist',
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', datafield: 'productName', filterable: true, minwidth: '18%', align: 'center', editable: false, },
		{ text: '${StringUtil.wrapString(uiLabelMap.Unit)}', datafield: 'quantityUomId', filterable: false, width: '5%', align: 'center', editable: false, 
			cellsrenderer: function(row, column, value){
				for(var i = 0; i < uomData.length; i++){
					if(uomData[i].uomId == value){
						return '<span title=' + uomData[i].description + '>' + uomData[i].description + '</span>'
					}
				}
			},
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.Packing)}', filterable: false, sortable: false, datafield: 'productPacking', width: '7%', align: 'center',  editable: false,
			cellsrenderer: function(row, column, value){
				var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
				return '<span title=' +'1x'+ data.convertNumber + '>' + '1x'+ data.convertNumber + '</span>'
			},
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProduceDate)}', filterable: true, datafield: 'datetimeManufactured', columngroup: 'PackingList', width: '10%', align: 'center', editable: false, cellsformat: 'dd/MM/yyyy', filtertype: 'date'},
		"/>
<#elseif orderId?has_content>
	<#assign columnlist = "
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productId', filterable: false, minwidth: '15%', align: 'center', columntype: 'dropdownlist',
			createeditor: function (row, cellvalue, editor) {
					var sourceDataProduct =
					{
		               localdata: productData,
		               datatype: 'array'
					};
					var dataAdapterProduct = new $.jqx.dataAdapter(sourceDataProduct);
					editor.off('change');
					editor.jqxDropDownList({source: dataAdapterProduct, autoDropDownHeight: true, displayMember: 'productId', valueMember: 'productId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}'
					});
					editor.on('change', function (event){
						var args = event.args;
			     	    if (args) {
		     	    		var item = args.item;
			     		    if (item){
			     		       updateProductById(item.value);
			     		    } 
			     	    }
			        });
				 },
				 cellbeginedit: function (row, datafield, columntype) {
					 var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
			    }
			},
			{ text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', datafield: 'productName', filterable: false, minwidth: '18%', align: 'center', editable: false, },
			{ text: '${StringUtil.wrapString(uiLabelMap.Unit)}', datafield: 'quantityUomId', filterable: false, width: '5%', align: 'center', editable: false, 
				cellsrenderer: function(row, column, value){
					for(var i = 0; i < uomData.length; i++){
						if(uomData[i].uomId == value){
							return '<span title=' + uomData[i].description + '>' + uomData[i].description + '</span>'
						}
					}
				},
			},	
			{ text: '${StringUtil.wrapString(uiLabelMap.Packing)}', filterable: false, datafield: 'productPacking', width: '7%', align: 'center',  editable: false,
				},
			{ text: '${StringUtil.wrapString(uiLabelMap.ProduceDate)}', filterable: false, datafield: 'datetimeManufactured', columngroup: 'PackingList', width: '10%', align: 'center', editable: false, cellsformat: 'dd/MM/yyyy',},
		"/>
<#elseif returnId?has_content>
	<#assign columnlist = "
	{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productId', filterable: false, minwidth: '15%', align: 'center', columntype: 'dropdownlist',
		createditor: function (row, cellvalue, editor) {
				var sourceDataProduct =
				{
	               localdata: productData,
	               datatype: 'array'
				};
				var dataAdapterProduct = new $.jqx.dataAdapter(sourceDataProduct);
				editor.off('change');
				editor.jqxDropDownList({source: dataAdapterProduct, autoDropDownHeight: true, displayMember: 'productId', valueMember: 'productId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}'
				});
				editor.on('change', function (event){
					var args = event.args;
		     	    if (args) {
	     	    		var item = args.item;
		     		    if (item){
		     		       updateProductById(item.value);
		     		    } 
		     	    }
		        });
			 },
			 cellbeginedit: function (row, datafield, columntype) {
				 var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
		    }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', datafield: 'productName', filterable: false, minwidth: '18%', align: 'center', editable: false, },
		{ text: '${StringUtil.wrapString(uiLabelMap.Unit)}', datafield: 'quantityUomId', filterable: false, width: '5%', align: 'center', editable: false, 
			cellsrenderer: function(row, column, value){
				for(var i = 0; i < uomData.length; i++){
					if(uomData[i].uomId == value){
						return '<span title=' + uomData[i].description + '>' + uomData[i].description + '</span>'
					}
				}
			},
		},
	{ text: '${StringUtil.wrapString(uiLabelMap.Packing)}', filterable: false, datafield: 'productPacking', width: '7%', align: 'center',  editable: false,
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ProduceDate)}', filterable: false, datafield: 'datetimeManufactured', columngroup: 'PackingList', width: '10%', align: 'center', editable: true, editale: true, cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput',},
"/>
</#if>					
<#if deliveryId?has_content>
	<#assign columnlist = columnlist + "
					{ text: '${StringUtil.wrapString(uiLabelMap.ExpireDate)}', filterable: true, datafield: 'expireDate', columngroup: 'PackingList', width: '10%', align: 'center',  editable: true, cellsformat: 'dd/MM/yyyy', filtertype: 'date',
					},	
					{ text: '${StringUtil.wrapString(uiLabelMap.ProduceDate)}', filterable: true, datafield: 'actualManufacturedDate', columngroup: 'Inspection', width: '10%', align: 'center', filtertype: 'date', editable: false, cellsformat: 'dd/MM/yyyy',
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ExpireDate)}', filterable: true, datafield: 'actualExpireDate', columngroup: 'Inspection', width: '10%', align: 'center', filtertype: 'date', editable: false, cellsformat: 'dd/MM/yyyy',
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ImportPackingList)}', filterable: true, datafield: 'quantity', columngroup: 'Quantity', width: '10%', align: 'center', editable: false,
						cellsrenderer: function(row, column, value){
							if (value){
								var localeQuantity = parseInt(value);
								return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>'
							} else {
								return '<span></span>';
							}
						},
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.Inspection)}', datafield: 'actualDeliveredQuantity',filterable: true,  columngroup: 'Quantity', width: '10%', align: 'center',
						cellsrenderer: function(row, column, value){
							if (value){
								var localeQuantity = parseInt(value);
								return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>'
							} else {
								return '<span></span>';
							}
						},
					},
						"/>
<#elseif orderId?has_content || returnId?has_content >
	<#assign columnlist = columnlist + "
					{ text: '${StringUtil.wrapString(uiLabelMap.ExpireDate)}', filterable: false, datafield: 'expireDate', columngroup: 'PackingList', width: '10%', align: 'center',  editable: true, columntype: 'dropdownlist',
						cellsrenderer: function(row, column, value){
							if (!value){
								return '';
							} else {
								return '<span>'+getFormattedDate(new Date(value))+'<span>';
							}
						},
						createeditor: function (row, cellvalue, editor) {
							var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
							var expireDateData = getListExpByProduct(data.productId);
							var sourceExp =
							{
				               localdata: expireDateData,
				               datatype: 'array'
							};
							var dataAdapterExp = new $.jqx.dataAdapter(sourceExp);
							editor.jqxDropDownList({source: dataAdapterExp, autoDropDownHeight: true, displayMember: 'description', valueMember: 'expireDate'});
							editor.off('change');
							editor.on('change', function (event){
								var args = event.args;
					     	    if (args) {
					     	    	var item = args.item;
					     	    	if (item){
					     	    		var date = item.value;
					     	    		var expireDate = new Date(date);
						     		    if (expireDate){ 
						     		    	updateRowData(data.productId, expireDate);
						     		    } 
					     	    	}
					     	    }
							});
						},
						validation: function (cell, value) {
							var id = cell.row;
							var data = $('#jqxgridProduct').jqxGrid('getrowdata', id);
					        if (data.productId){
					        	if (value == null){
					        		return { result: false, message: '${uiLabelMap.FieldRequired}' };
					        	}
					        }
					        return true;
					    },
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ProduceDate)}', filterable: false, datafield: 'proDateIns', columngroup: 'Inspection', width: '10%', align: 'center', cellsformat: 'd', editable: true, cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput',
						validation: function (cell, value) {
							var id = cell.row;
							var data = $('#jqxgridProduct').jqxGrid('getrowdata', id);
					        if (data.productId != '' && data.productId != null && data.expireDate != null && data.expireDate != ''){
					        	if (value == null){
					        		return { result: false, message: '${uiLabelMap.FieldRequired}' };
					        	} else {
					        		var today = new Date();
					        		var day1 = value.getDate();
					        		var month1 = value.getMonth() + 1;
					        		var year1 = value.getFullYear();
					        		var day2 = today.getDate();
					        		var month2 = today.getMonth() + 1;
					        		var year2 = today.getFullYear();
									if (year2 >= year1){
										if (month2 > month1){
										} else {
											if (month2 = month1){
												if (day2 < day1){
													return { result: false, message: '${uiLabelMap.ProduceDateNotGreateThanNow}' };
												}
											} else {
												return { result: false, message: '${uiLabelMap.ProduceDateNotGreateThanNow}' };
											}
										}
									} else {
										return { result: false, message: '${uiLabelMap.ProduceDateNotGreateThanNow}' };
									}
					        	}
					        }
					        return true;
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ExpireDate)}', datafield: 'expDateIns', filterable: false, columngroup: 'Inspection', width: '10%', align: 'center', cellsformat: 'd', editable: true, cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput',
						validation: function (cell, value) {
							var id = cell.row;
							var data = $('#jqxgridProduct').jqxGrid('getrowdata', id);
							if (data.productId != '' && data.productId != null && data.expireDate != null && data.expireDate != ''){
					        	if (value == null){
					        		return { result: false, message: '${uiLabelMap.FieldRequired}' };
					        	}
					        }
					        return true;
					    }
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.ImportPackingList)}', datafield: 'quantity', filterable: false, columngroup: 'Quantity', width: '10%', align: 'center', editable: false,
						cellsrenderer: function(row, column, value){
							if (value){
								var localeQuantity = parseInt(value);
								return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>'
							} else {
								return '<span></span>';
							}
						},
					},
					{ text: '${StringUtil.wrapString(uiLabelMap.Inspection)}', datafield: 'inspectionQty', columntype: 'numberinput', filterable: false, columngroup: 'Quantity', width: '10%', align: 'center',
						initeditor: function (row, cellvalue, editor) {
	                         editor.jqxNumberInput({ decimalDigits: 0, digits: 10 });
	                     },
						validation: function (cell, value) {
							var id = cell.row;
							var data = $('#jqxgridProduct').jqxGrid('getrowdata', id);
							if (data.productId != '' && data.productId != null && data.expireDate != null && data.expireDate != ''){
						        if (value < 0) {
						            return { result: false, message: '${uiLabelMap.QuantityGreateThanZero}' };
						        }
						        if (!value){
						        	return { result: false, message: '${uiLabelMap.FieldRequired}' };
						        }
							}
					        return true;
					    },
					},"/>
</#if>
<#if deliveryId?has_content>
	<@jqGrid id="jqxgridProduct" filterable="true" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" columngrouplist=columngrouplist
		showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="" editable="false" 
		url="jqxGeneralServicer?sname=getDetailPurchaseDelivery&deliveryId=${parameters.deliveryId?if_exists}" otherParams="quantityUomId:S-getQuantityUomBySupplier(orderId,productId)<quantityUomId>;convertNumber:S-getProductConvertNumber(productId,quantityUomId,baseQuantityUomId)<convertNumber>" 
	/>
<#elseif orderId?has_content>
	<@jqGrid id="jqxgridProduct" sortable="false" filterable="false" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" columngrouplist=columngrouplist
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" editmode="click" updateoffline="true" 
	url=""
	customcontrol1="icon-plus@${uiLabelMap.AddRow}@javascript:void(0);@addNewRow()"
	customcontrol2="icon-remove@${uiLabelMap.Delete}@javascript:void(0);@deleteRow()"
	/>	
<#elseif returnId?has_content>
	<@jqGrid id="jqxgridProduct" sortable="false" filterable="false" customTitleProperties="DetailReceiveDelivery" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" columngrouplist=columngrouplist
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" editmode="click" updateoffline="true" 
	url=""
	customcontrol1="icon-plus@${uiLabelMap.AddRow}@javascript:void(0);@addNewRow()"
	customcontrol2="icon-remove@${uiLabelMap.Delete}@javascript:void(0);@deleteRow()"
	/>
</#if>
<#assign dataField="[{ name: 'productId', type: 'string'},
				{ name: 'productName', type: 'string'},
				{ name: 'quantityUomId', type: 'string'},
				{ name: 'productPacking', type: 'string'},
				{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
				{ name: 'expireDate', type: 'date', other: 'Timestamp'},
				{ name: 'quantity', type: 'number'},
				{ name: 'actualExportedQuantity', type: 'number'},
				{ name: 'deliveryTypeId', type: 'string'},
				{ name: 'inventoryStatusId', type: 'string'},
				{ name: 'reasonId', type: 'string'},
				
				]"/>
<#assign columnlist="">
<#if deliveryId?has_content>
		<#assign columnlist = " 
			{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productId', filterable: true, width: '13%', align: 'center', columntype: 'dropdownlist',
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', datafield: 'productName', filterable: true, width: '15%', align: 'center', editable: false, },
				{ text: '${StringUtil.wrapString(uiLabelMap.Unit)}', datafield: 'quantityUomId', filterable: false, width: '5%', align: 'center', editable: false, 
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridNote').jqxGrid('getrowdata', row);
						for(var i = 0; i < uomData.length; i++){
							if(uomData[i].uomId == value){
								return '<span title=' + value + '>' + uomData[i].description + '</span>'
							}
						}
					},
				},
			{ text: '${StringUtil.wrapString(uiLabelMap.Quantity)}', filterable: true, datafield: 'actualExportedQuantity', width: '7%', align: 'center',  editable: true,
			cellsrenderer: function(row, column, value){
							if (value){
								var localeQuantity = parseInt(value);
								return '<span title=' + localeQuantity.toLocaleString('${localeStr}') + '>' + localeQuantity.toLocaleString('${localeStr}') + '</span>'
							} else {
								return '<span></span>';
							}
						},
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProduceDate)}', datafield: 'datetimeManufactured',  filterable: true, columngroup: 'PackingList', width: '10%', align: 'center', filtertype: 'date', editable: false, cellsformat: 'dd/MM/yyyy',},
		{ text: '${StringUtil.wrapString(uiLabelMap.ExpireDate)}', datafield: 'expireDate', columngroup: 'PackingList', filterable: true, width: '10%', align: 'center', filtertype: 'date', editable: true, cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput',
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.Note)}', datafield: 'deliveryTypeId', minwidth: '20%', align: 'center', columntype: 'dropdownlist',
			cellsrenderer: function(row, column, value){
				for(var i = 0; i < dlvTypeData.length; i++){
					if(dlvTypeData[i].deliveryTypeId == value){
						return '<span title=' + dlvTypeData[i].description + '>' + dlvTypeData[i].description + '</span>'
					}
				}
			}
		},
		"/>
<#elseif orderId?has_content>
	<#assign columnlist = "
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productId', filterable: false, width: '13%', align: 'center', columntype: 'dropdownlist',
				createeditor: function (row, cellvalue, editor) {
						var sourceDataProduct =
						{
			               localdata: productData,
			               datatype: 'array'
						};
						var dataAdapterProduct = new $.jqx.dataAdapter(sourceDataProduct);
						editor.off('change');
						editor.jqxDropDownList({source: dataAdapterProduct, autoDropDownHeight: true, displayMember: 'productId', valueMember: 'productId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}'
						});
						editor.on('change', function (event){
							var args = event.args;
				     	    if (args) {
			     	    		var item = args.item;
				     		    if (item){
			     		    		updateNoteRowData(item.value);
				     		    } 
				     	    }
				        });
					 },
					 cellbeginedit: function (row, datafield, columntype) {
				    }
				},
				{ text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', datafield: 'productName', filterable: false, width: '15%', align: 'center', editable: false, },
				{ text: '${StringUtil.wrapString(uiLabelMap.Unit)}', datafield: 'quantityUomId', filterable: false, width: '5%', align: 'center', editable: false, 
					cellsrenderer: function(row, column, value){
						var data = $('#jqxgridNote').jqxGrid('getrowdata', row);
						for(var i = 0; i < uomData.length; i++){
							if(uomData[i].uomId == value){
								return '<span title=' + value + '>' + uomData[i].description + '</span>'
							}
						}
					},
				},
			{ text: '${StringUtil.wrapString(uiLabelMap.Quantity)}', filterable: false, columntype: 'numberinput', datafield: 'quantity', width: '7%', align: 'center',  editable: true,
			initeditor: function (row, cellvalue, editor) {
		        editor.jqxNumberInput({ decimalDigits: 0, digits: 10 });
		    },
			validation: function (cell, value) {
				var id = cell.row;
				var data = $('#jqxgridNote').jqxGrid('getrowdata', id);
				if (data.productId != '' && data.productId != null && data.expireDate != null && data.expireDate != ''){
			        if (value < 0) {
			            return { result: false, message: '${uiLabelMap.QuantityGreateThanZero}' };
			        }
			        if (value == '' || value == null){
			        	return { result: false, message: '${uiLabelMap.FieldRequired}' };
			        }
				}
		        return true;
		    }
		},
		{ text: '${StringUtil.wrapString(uiLabelMap.ProduceDate)}', datafield: 'datetimeManufactured', filterable: false, columngroup: 'PackingList', width: '10%', align: 'center', cellsformat: 'd', editable: false, cellsformat: 'dd/MM/yyyy',},
		{ text: '${StringUtil.wrapString(uiLabelMap.ExpireDate)}', datafield: 'expireDate', columngroup: 'PackingList', filterable: false, width: '10%', align: 'center',editable: true, columntype: 'dropdownlist',
			validation: function (cell, value) {
				var id = cell.row;
				var data = $('#jqxgridNote').jqxGrid('getrowdata', id);
				if (data.productId){
			        if (!value){
			        	return { result: false, message: '${uiLabelMap.FieldRequired}' };
			        }
				}
		        return true;
		    },
			cellsrenderer: function(row, column, value){
				if (!value || value == ''){
					return '';
				} else {
					from = value.split('/');
					return '<span>'+getFormattedDate(new Date(from[2], from[1] - 1, from[0]))+'<span>';
				}
			},
			initeditor: function (row, cellvalue, editor) {
				var data = $('#jqxgridNote').jqxGrid('getrowdata', row);
				var expireDateData = getListExpByProduct(data.productId);
				var sourceExp =
				{
	               localdata: expireDateData,
	               datatype: 'array'
				};
				var dataAdapterExp = new $.jqx.dataAdapter(sourceExp);
				editor.jqxDropDownList({source: dataAdapterExp, autoDropDownHeight: true, displayMember: 'description', valueMember: 'expireDate'});
				editor.off('change');
				editor.on('change', function (event){
					var args = event.args;
		     	    if (args) {
		     	    	var item = args.item;
		     	    	if (item){
		     	    		var date = item.value;
		     	    		var expireDate = new Date(date);
			     		    if (expireDate){ 
			     		    	updateProductNote(data.productId, expireDate);
			     		    } 
		     	    	}
		     	    }
				});
			},
		},
//		{ text: '${StringUtil.wrapString(uiLabelMap.InventoryType)}', datafield: 'inventoryStatusId', width: '20%', align: 'center', columntype: 'dropdownlist',
//			cellsrenderer: function(row, column, value){
//				for(var i = 0; i < InvStatusData.length; i++){
//					if(InvStatusData[i].inventoryStatusId == value){
//						return '<span title=' + InvStatusData[i].description + '>' + InvStatusData[i].description + '</span>'
//					}
//				}
//			},
//			initeditor: function (row, cellvalue, editor) {
//				var sourceInvStatus =
//				{
//	               localdata: InvStatusData,
//	               datatype: 'array'
//				};
//				var dataAdapterInvStatus = new $.jqx.dataAdapter(sourceInvStatus);
//				editor.jqxDropDownList({source: dataAdapterInvStatus, displayMember: 'description', valueMember: 'inventoryStatusId'
//				});
//			 },
//			 cellbeginedit: function (row, datafield, columntype) {
//		    }
//		},
		{ text: '${StringUtil.wrapString(uiLabelMap.Note)}', datafield: 'deliveryTypeId', filterable: false, minwidth: '20%', align: 'center', columntype: 'dropdownlist',
			cellsrenderer: function(row, column, value){
				for(var i = 0; i < dlvTypeData.length; i++){
					if(dlvTypeData[i].deliveryTypeId == value){
						return '<span title=' + dlvTypeData[i].description + '>' + dlvTypeData[i].description + '</span>'
					}
				}
			},
			createeditor: function (row, cellvalue, editor) {
				var sourceDlvType =
				{
	               localdata: dlvTypeData,
	               datatype: 'array'
				};
				var dataAdapterDlvType = new $.jqx.dataAdapter(sourceDlvType);
				editor.jqxDropDownList({source: dataAdapterDlvType, autoDropDownHeight: true, displayMember: 'description', valueMember: 'deliveryTypeId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}'
				});
			 },
			 cellbeginedit: function (row, datafield, columntype) {
		    }
		},
		"/>
<#elseif returnId?has_content>
	<#assign columnlist = "
		{ text: '${StringUtil.wrapString(uiLabelMap.ProductProductId)}', datafield: 'productId', filterable: false, width: '13%', align: 'center', columntype: 'dropdownlist',
			createeditor: function (row, cellvalue, editor) {
					var sourceDataProduct =
					{
		               localdata: productData,
		               datatype: 'array'
					};
					var dataAdapterProduct = new $.jqx.dataAdapter(sourceDataProduct);
					editor.off('change');
					editor.jqxDropDownList({source: dataAdapterProduct, autoDropDownHeight: true, displayMember: 'productId', valueMember: 'productId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}'
					});
					editor.on('change', function (event){
						var args = event.args;
			     	    if (args) {
		     	    		var item = args.item;
			     		    if (item){
			     		    	updateNoteRowData(item.value);
			     		    } 
			     	    }
			        });
				 },
				 cellbeginedit: function (row, datafield, columntype) {
			    }
			},
			{ text: '${StringUtil.wrapString(uiLabelMap.ProductName)}', datafield: 'productName', filterable: false, width: '15%', align: 'center', editable: false, },
			{ text: '${StringUtil.wrapString(uiLabelMap.Unit)}', datafield: 'quantityUomId', filterable: false, width: '5%', align: 'center', editable: false, 
				cellsrenderer: function(row, column, value){
					var data = $('#jqxgridNote').jqxGrid('getrowdata', row);
					for(var i = 0; i < uomData.length; i++){
						if(uomData[i].uomId == value){
							return '<span title=' + value + '>' + uomData[i].description + '</span>'
						}
					}
				},
			},
	{ text: '${StringUtil.wrapString(uiLabelMap.Quantity)}', filterable: false, datafield: 'quantity', columntype: 'numberinput', width: '7%', align: 'center',  editable: true,
		initeditor: function (row, cellvalue, editor) {
	        editor.jqxNumberInput({ decimalDigits: 0, digits: 10 });
	    },
		validation: function (cell, value) {
			var id = cell.row;
			var data = $('#jqxgridNote').jqxGrid('getrowdata', id);
			if (data.productId != '' && data.productId != null && data.expireDate != null && data.expireDate != ''){
		        if (value < 0) {
		            return { result: false, message: '${uiLabelMap.QuantityGreateThanZero}' };
		        }
		        if (value == '' || value == null){
		        	return { result: false, message: '${uiLabelMap.FieldRequired}' };
		        }
			}
	        return true;
	    }
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ProduceDate)}', datafield: 'datetimeManufactured', filterable: false, columngroup: 'PackingList', width: '10%', align: 'center', cellsformat: 'd', editable: true, cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput',},
	{ text: '${StringUtil.wrapString(uiLabelMap.ExpireDate)}', datafield: 'expireDate', columngroup: 'PackingList', filterable: false, width: '10%', align: 'center', cellsformat: 'd', editable: true, cellsformat: 'dd/MM/yyyy', columntype: 'datetimeinput',
		validation: function (cell, value) {
			var id = cell.row;
			var data = $('#jqxgridNote').jqxGrid('getrowdata', id);
			if (data.productId){
		        if (!value){
		        	return { result: false, message: '${uiLabelMap.FieldRequired}' };
		        }
			}
	        return true;
	    },
	    cellsrenderer: function(row, column, value){
			if (!value){
				return '';
			} else {
				from = value.split('/');
				return '<span>'+getFormattedDate(new Date(from[2], from[1] - 1, from[0]))+'<span>';
			}
		},
		initeditor: function (row, cellvalue, editor) {
			var data = $('#jqxgridProduct').jqxGrid('getrowdata', row);
			var expireDateData = getListExpByProduct(data.productId);
			var sourceExp =
			{
               localdata: expireDateData,
               datatype: 'array'
			};
			var dataAdapterExp = new $.jqx.dataAdapter(sourceExp);
			editor.jqxDropDownList({source: dataAdapterExp, autoDropDownHeight: true, displayMember: 'description', valueMember: 'expireDate'});
			editor.off('change');
			editor.on('change', function (event){
				var args = event.args;
	     	    if (args) {
	     	    	var item = args.item;
	     	    	if (item){
	     	    		var date = item.value;
	     	    		var expireDate = new Date(date);
		     		    if (expireDate){ 
		     		    	updateProductNote(data.productId, expireDate);
		     		    } 
	     	    	}
	     	    }
			});
		},
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.InventoryType)}', datafield: 'inventoryStatusId', width: '20%', align: 'center', columntype: 'dropdownlist',
		cellsrenderer: function(row, column, value){
			for(var i = 0; i < InvStatusData.length; i++){
				if(InvStatusData[i].inventoryStatusId == value){
					return '<span title=' + InvStatusData[i].description + '>' + InvStatusData[i].description + '</span>'
				}
			}
		},
		createeditor: function (row, cellvalue, editor) {
			var sourceInvStatus =
			{
               localdata: InvStatusData,
               datatype: 'array'
			};
			var dataAdapterInvStatus = new $.jqx.dataAdapter(sourceInvStatus);
			editor.jqxDropDownList({source: dataAdapterInvStatus, autoDropDownHeight: true, displayMember: 'description', valueMember: 'inventoryStatusId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}'
			});
		 },
		 cellbeginedit: function (row, datafield, columntype) {
	    }
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.Note)}', filterable: false, datafield: 'reasonId', width: '20%', align: 'center', columntype: 'dropdownlist',
		cellsrenderer: function(row, column, value){
			for(var i = 0; i < returnReasonData.length; i++){
				if(returnReasonData[i].returnReasonId == value){
					return '<span title=' + returnReasonData[i].description + '>' + returnReasonData[i].description + '</span>'
				}
			}
		},
		createeditor: function (row, cellvalue, editor) {
			var sourceReason =
			{
               localdata: returnReasonData,
               datatype: 'array'
			};
			var dataAdapterReason = new $.jqx.dataAdapter(sourceReason);
			editor.jqxDropDownList({source: dataAdapterReason ,autoDropDownHeight: true, displayMember: 'description', valueMember: 'returnReasonId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}'
			});
		 },
		 cellbeginedit: function (row, datafield, columntype) {
	    }
	},
	"/>
</#if>
<#if deliveryId?has_content>
	<@jqGrid id="jqxgridNote" filterable="true" customTitleProperties="NoteOfInspection" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true"
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="false" 
	url="jqxGeneralServicer?sname=getNoteOfPurchaseDelivery&deliveryId=${parameters.deliveryId?if_exists}" otherParams="quantityUomId:S-getQuantityUomBySupplier(orderId,productId)<quantityUomId>"
	/>
<#elseif orderId?has_content>
	<@jqGrid id="jqxgridNote" sortable="false" filterable="false" customTitleProperties="NoteOfInspection" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" 
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" editmode="click" updateoffline="true" 
	url=""
	customcontrol1="icon-plus@${uiLabelMap.AddRow}@javascript:void(0);@addNewNoteRow()"
	customcontrol2="icon-remove@${uiLabelMap.Delete}@javascript:void(0);@deleteNoteRow()"
	/>	
<#elseif returnId?has_content>
	<@jqGrid id="jqxgridNote" sortable="false" filterable="false" customTitleProperties="NoteOfInspection" filtersimplemode="true" addType="popup" dataField=dataField columnlist=columnlist clearfilteringbutton="true" 
	showtoolbar="true" addrow="false" deleterow="false" alternativeAddPopup="alterpopupWindow" editable="true" editmode="click" updateoffline="true" 
	url=""
	customcontrol1="icon-plus@${uiLabelMap.AddRow}@javascript:void(0);@addNewNoteRow()"
	customcontrol2="icon-remove@${uiLabelMap.Delete}@javascript:void(0);@deleteNoteRow()"
	/>	
</#if>

<#if orderId?has_content || deliveryId?has_content>
	<div class="row-fluid">
		<div class="span12 no-left-margin">
			<div class="span3" style="text-align:center">
				<b>${uiLabelMap.LogisticsManager}</b>
			</div>
			<div class="span2" style="text-align:center">
			<b>${uiLabelMap.QAStaff}</b>
			</div>
			<div class="span2" style="text-align:center">
				<b>${uiLabelMap.Transporter}</b>	
			</div>
			<div class="span2" style="text-align:center">
				<b>${uiLabelMap.StoreKeeper}</b>
			</div>
			<div class="span3" style="text-align:center">
				<b>${uiLabelMap.DeliveryRepresentative}</b>
			</div>
		</div>
		<br/>
		<br/>
		<br/>
		<br/>
		<br/>
		<div class="span12 no-left-margin">
			<div class="span3" style="text-align:center">
				${uiLabelMap.DateTimeDetail}
			</div>
			<div class="span2" style="text-align:center">
			${uiLabelMap.DateTimeDetail}
			</div>
			<div class="span2" style="text-align:center">
				${uiLabelMap.DateTimeDetail}	
			</div>
			<div class="span2" style="text-align:center">
				${uiLabelMap.DateTimeDetail}
			</div>
			<div class="span3" style="text-align:center">
				${uiLabelMap.DateTimeDetail}
			</div>
		</div>
	</div>
	<br/>
	<br/>
	<#if !deliveryId?has_content>
		<div class="form-action">
			<div class='row-fluid'>
				<div class="span12 margin-top10">
					<button id="buttonCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
					<button id="buttonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				</div>
			</div>
		</div>
	</#if>
</#if>
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
    
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtooltip.js"></script>
<script type="text/javascript">
	$.jqx.theme = 'olbius';
	theme = $.jqx.theme;
	var pathScanFile;
	$('#document').ready(function(){
		var facilityCTMData = new Array();
		if (!deliveryId){
			$("#destFacilityId").jqxDropDownList({ source: facilityData, width: '60%', height: '22', displayMember: 'description', valueMember: 'facilityId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}' });
			$("#facilityCTMId").jqxDropDownList({ source: facilityCTMData, width: '60%', height: '22', displayMember: 'description', valueMember: 'contactMechId', placeHolder: '${StringUtil.wrapString(uiLabelMap.filterchoosestring)}' });
			$("#deliveryDate").jqxDateTimeInput({ width: '40%', height: '22px', formatString: 'dd/MM/yyyy'});
			$("#inputVehicle").jqxInput({placeHolder: ". . . ${StringUtil.wrapString(uiLabelMap.EnterVehicleName)} . . .", height: 22, width: '50%', minLength: 1});
			$("#inputNumberPlate").jqxInput({placeHolder: ". . . ${StringUtil.wrapString(uiLabelMap.EnterNumberPlate)} . . .", height: 22, width: '50%', minLength: 1});
			initValidateDropdown($('#destFacilityId'), 'destFacilityId');
			initValidateDatetime($('#deliveryDate'), 'deliveryDate');
		}
		if (reqFacilityId){
			$("#destFacilityId").jqxDropDownList('val', reqFacilityId);
		}
		if (reqDate){
			$("#deliveryDate").jqxDateTimeInput('val', reqDate);
		}
		$("#notifyId").jqxNotification({ width: "100%", opacity: 0.9, appendContainer: "#containerNotify",
            autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "success"
        });
		$("#notifyMissInfoId").jqxNotification({ width: "100%", opacity: 0.9,  appendContainer: "#containerNotify",
            autoOpen: false, animationOpenDelay: 800, autoClose: true, template: "warning"
        });
		$('#jqxFileScanUpload').jqxWindow({ width: 400, height: 250, isModal: false, autoOpen: false });
		
		$('#attachFile').ace_file_input({
			style:'well',
			btn_choose:'${StringUtil.wrapString(uiLabelMap.DropFileOrClickToChoose)}',
			btn_change:null,
			no_icon:'icon-cloud-upload',
			droppable:true,
			onchange:null,
			thumbnail:'small',
			before_change:function(files, dropped) {
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
	});
	
	$("#buttonSave").click(function () {
		showConfirmPopup();
	});
	
	$("#buttonCancel").click(function () {
		location.reload();
	});
	
//	$("#buttonSentToAcc").click(function () {
//		jQuery.ajax({
//			url: "sendNotifyNewDeliveryToAccountant",
//			type: "POST",
//			data: {
//				deliveryId: deliveryId,
//			},
//			success: function(res) {
//	        }
//		});
//	});
	
	$("#destFacilityId").on('change', function(event){
		var tmp = $("#destFacilityId").jqxDropDownList('getSelectedItem');
		updateCTM({
			facilityId: tmp.value,
			contactMechPurposeTypeId: "SHIPPING_LOCATION",
			}, 'getFacilityContactMechs' , 'listFacilityContactMechs', 'contactMechId', 'address1', 'facilityCTMId');
		if (reqFacilityId == tmp.value && reqContactMechId){
			$("#facilityCTMId").jqxDropDownList('val', reqContactMechId);
		}
	});
	var listImage = [];
	$('#uploadOkButton').click(function(){
		saveFileUpload();
	});
	$('#uploadCancelButton').click(function(){
		$('#jqxFileScanUpload').jqxWindow('close');
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
						if (deliveryId){
							updateDeliveryPathScanFile(path, deliveryId);
						}
						$('#linkId').html("");
						$('#linkId').append("<a href='"+path+"' target='_blank'><i class='fa-file-text-o'></i>'"+dataResourceName+"'</a> <a onclick='removeScanFile()'><i class='fa-remove'></i></a>");
			        }
				}).done(function() {
				});
			}
			$('#jqxFileScanUpload').jqxWindow('close');
	}
	function removeScanFile (){
		pathScanFile = null;
		$('#linkId').html("");
		$('#linkId').append("<a id='linkId' onclick='showAttachFilePopup()'><i class='icon-upload'></i>${uiLabelMap.AttachFileScan}</a>");
	}
     function updateDeliveryPathScanFile(path, deliveryId){
    	 jQuery.ajax({
				url: "updateDeliveryPathScanFile",
				type: "POST",
				data: {
					pathScanFile: path,
					deliveryId: deliveryId
				},
				success: function(res) {
		        }
			});
     }
	function showAttachFilePopup(){
		$('#jqxFileScanUpload').jqxWindow('open');
	}
	function showConfirmPopup(){
		bootbox.confirm("${uiLabelMap.DAAreYouSureSave}",function(result){ 
				createDeliverys();
			}
		);
	}
	function createDeliverys(){
		$('#deliveryDate').jqxValidator('validate');
		$('#destFacilityId').jqxValidator('validate');
		if (!pathScanFile){
			bootbox.dialog("${uiLabelMap.MustUploadScanFile}!", [{
                "label" : "OK",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		} else {
			if (orderId){
				var test = checkInfoCompleted();
				if (test){
					createOtherDeliverys();
				}
			} else if (returnId){
				createReturnDelivery();
			}
		}
	}
	function closeConfirmWindow (){
		$("#confirmWindow").jqxWindow('close');
	}
	function initValidateDropdown(element, id){
		element.jqxValidator({
			rtl: true,
			rules:[{
				input: '#'+id, 
	            message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', 
	            action: 'blur', 
	            rule: function (input) {	
	         	   	var tmp = $('#'+id).jqxDropDownList('getSelectedItem');
	                return tmp ? true : false;
	            }
			}],
		});
	}
	
	function initValidateDatetime(element, id){
		element.jqxValidator({
			rules:[{
				input: '#'+id, 
	            message: '${StringUtil.wrapString(uiLabelMap.FieldRequired?default(''))}', 
	            action: 'blur', 
	            rule: function (input) {	
	            	var tmp = $("#"+id).jqxDateTimeInput('getDate');
	                return tmp ? true : false;
	            }
			}],
		});
	}
	
	function updateCTM (jsonObject, url, data, key, value, id) {
	    jQuery.ajax({
	        url: url,
	        type: "POST",
	        data: jsonObject,
	        async: false,
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
	function updateProductNote(productId, expireDate){
		if (orderId){
			var orderItem = {};
			for (var i = 0; i < orderItemData.length; i++){
				if (orderItemData[i].productId == productId){
					var stringExpireDateTmp = JSON.stringify(orderItemData[i].expireDate);
					var stringExpireDate = JSON.parse(stringExpireDateTmp);
					var orderExpireDate;
					if(typeof(stringExpireDate) == "object"){
						orderExpireDate = stringExpireDate;
					}else{
						orderExpireDate = new Date(stringExpireDate);
					}
					if (orderExpireDate.getDate() == expireDate.getDate() && orderExpireDate.getMonth() == expireDate.getMonth() && orderExpireDate.getFullYear() == expireDate.getFullYear()){
						var orderItemTmp = JSON.stringify(orderItemData[i]);
						orderItem = JSON.parse(orderItemTmp);
						break;
					}
				}
			}
			if (orderItem["orderId"]){
				orderItem["quantity"] = "";
		        var selectedrowindex = $("#jqxgridNote").jqxGrid('getselectedrowindex');
		        var rowscount = $("#jqxgridNote").jqxGrid('getdatainformation').rowscount;
		        if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
		            var id = $("#jqxgridNote").jqxGrid('getrowid', selectedrowindex);
		            $("#jqxgridNote").jqxGrid('updaterow', id, orderItem);
		            $("#jqxgridNote").jqxGrid('ensurerowvisible', selectedrowindex);
		        }
			} else {
				updateNoteRowData(productId);
			}
		} else if (returnId){
			var returnItem = {};
			for (var i = 0; i < returnItemData.length; i++){
				if (returnItemData[i].productId == productId){
					var stringExpireDateTmp = JSON.stringify(returnItemData[i].expireDate);
					var stringExpireDate = JSON.parse(stringExpireDateTmp);
					var returnExpireDate;
					if(typeof(stringExpireDate) == "object"){
						returnExpireDate = stringExpireDate;
					}else{
						returnExpireDate = new Date(stringExpireDate);
					}
					if (returnExpireDate.getDate() == expireDate.getDate() && returnExpireDate.getMonth() == expireDate.getMonth() && returnExpireDate.getFullYear() == expireDate.getFullYear()){
						var returnItemTmp = JSON.stringify(returnItemData[i]);
						returnItem = JSON.parse(returnItemTmp);
						break;
					}
				}
			}
			if (returnItem["returnId"]){
				returnItem["quantity"] = "";
				for(var i = 0; i < productData.length; i++){
					if (productData[i].productId == productId){
						var nameTmp = JSON.stringify(productData[i].productName);
						returnItem["productName"] = JSON.parse(nameTmp);
					}
				}
		        var selectedrowindex = $("#jqxgridNote").jqxGrid('getselectedrowindex');
		        var rowscount = $("#jqxgridNote").jqxGrid('getdatainformation').rowscount;
		        if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
		            var id = $("#jqxgridNote").jqxGrid('getrowid', selectedrowindex);
		            $("#jqxgridNote").jqxGrid('updaterow', id, returnItem);
		            $("#jqxgridNote").jqxGrid('ensurerowvisible', selectedrowindex);
		        }
			} else {
				updateNoteRowData(productId);
			}
		}
	}
	function getListExpByProduct(productId){
		var listExps = new Array();
		if (productId){
			for (var i = 0; i < orderItemData.length; i++){
				if (orderItemData[i].productId == productId){
					var row = {};
					row['expireDate'] = orderItemData[i].expireDate;
					from = orderItemData[i].expireDate.split('/');
					row['description'] = getFormattedDate(new Date(from[2], from[1] - 1, from[0]));
					listExps.push(row);
				}
			}
		} 
		return listExps;
	}
	function updateRowData(productId, expireDate){
		if (orderId){
			var orderItem = {};
			var test = false;
			for (var i = 0; i < orderItemData.length; i++){
				if (orderItemData[i].productId == productId){
					var stringExpireDateTmp = JSON.stringify(orderItemData[i].expireDate);
					var stringExpireDate = JSON.parse(stringExpireDateTmp);
					var orderExpireDate;
					if(typeof(stringExpireDate) == "object"){
						orderExpireDate = stringExpireDate;
					}else{
						orderExpireDate = new Date(stringExpireDate);
					}
					if (orderExpireDate.getDate() == expireDate.getDate() && orderExpireDate.getMonth() == expireDate.getMonth() && orderExpireDate.getFullYear() == expireDate.getFullYear()){
						var orderItemTmp = JSON.stringify(orderItemData[i]);
						orderItem = JSON.parse(orderItemTmp);
						test = true;
					}
				}
				if (test){
					break;
				}
			}
			if (orderItem["orderId"]){
				orderItem["proDateIns"] = '';
				orderItem["expDateIns"] = '';
				orderItem["inspectionQty"] = '';
		        var selectedrowindex = $("#jqxgridProduct").jqxGrid('getselectedrowindex');
		        var rowscount = $("#jqxgridProduct").jqxGrid('getdatainformation').rowscount;
		        if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
		            var id = $("#jqxgridProduct").jqxGrid('getrowid', selectedrowindex);
		            $("#jqxgridProduct").jqxGrid('updaterow', id, orderItem);
		        }
			} else {
				updateProductById(productId);
			}
		} else if (returnId){
			var newRow = {};
			for (var i = 0; i < returnItemData.length; i++){
				if (returnItemData[i].productId == productId){
					var stringExpireDateTmp = JSON.stringify(returnItemData[i].expireDate);
					var stringExpireDate = JSON.parse(stringExpireDateTmp);
					var returnExpireDate;
					if (typeof(stringExpireDate) == "object"){
						returnExpireDate = stringExpireDate;
					}else{
						returnExpireDate = new Date(stringExpireDate);
					}
					if (returnExpireDate.getDate() == expireDate.getDate() && returnExpireDate.getMonth() == expireDate.getMonth() && returnExpireDate.getFullYear() == expireDate.getFullYear()){
						var returnItemTmp = JSON.stringify(returnItemData[i]);
						newRow = JSON.parse(returnItemTmp);
						break;
					}
				}
			}
			if (newRow["returnId"]){
				for(var i = 0; i < productData.length; i++){
					if (productData[i].productId == productId){
						var nameTmp = JSON.stringify(productData[i].productName);
						newRow["productName"] = JSON.parse(nameTmp);
						newRow["proDateIns"] = '';
						newRow["expDateIns"] = expireDate;
						newRow["inspectionQty"] = '';
					}
				}
		        var selectedrowindex = $("#jqxgridProduct").jqxGrid('getselectedrowindex');
		        var rowscount = $("#jqxgridProduct").jqxGrid('getdatainformation').rowscount;
		        if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
		            var id = $("#jqxgridProduct").jqxGrid('getrowid', selectedrowindex);
		            $("#jqxgridProduct").jqxGrid('updaterow', id, newRow);
		            $("#jqxgridProduct").jqxGrid('ensurerowvisible', selectedrowindex);
		        }
			} else {
				updateProductById(productId);
			}
		}
	}
	function createReturnDelivery() {
		var rows = $('#jqxgridProduct').jqxGrid('getrows');
		for(var i = 0; i < rows.length; i++){
			(function(i){
				if (rows[i].returnId){
					if (!rows[i].expDateIns){
						var obj = $("#row"+i+"jqxgridProduct div[role='gridcell']");
						$(obj[7]).jqxTooltip({theme:'tooltip-validation', content: '${uiLabelMap.FieldRequired}', position: 'bottom', autoHide: false});
						setTimeout(function(){
							$(obj[7]).jqxTooltip("open");
						}, 100);
						return false;
					}
					if (!rows[i].inspectionQty){
						var obj = $("#row"+i+"jqxgridProduct div[role='gridcell']");
						$(obj[8]).jqxTooltip({theme:'tooltip-validation', content: '${uiLabelMap.FieldRequired}', position: 'bottom', autoHide: false});
						setTimeout(function(){
							$(obj[8]).jqxTooltip("open");
						}, 100);
						return false;
					}
				}
			})(i);
		}
		var listReturnItems = new Array();
		for(var i = 0; i < rows.length; i++){
			if (rows[i]["inspectionQty"]){
				var map = {};
				map['returnId'] = rows[i].returnId;
				map['returnItemSeqId'] = rows[i].returnItemSeqId;
				if (rows[i].proDateIns){
					map['actualManufacturedDate'] = formatFullDate(rows[i].proDateIns);
				}
				map['actualExpireDate'] = formatFullDate(rows[i].expDateIns);
				map['actualDeliveredQuantity'] = rows[i].inspectionQty;
				map['quantity'] = rows[i].quantity;
				map['statusId'] = 'DELI_ITEM_DELIVERED';
				map['inventoryStatusId'] = "INV_NS_GOOD";
				listReturnItems[i] = map;
			}
		}
		var noteRows = $('#jqxgridNote').jqxGrid('getrows');
		
		for(var i = 0; i < noteRows.length; i++){
			(function(i){
				if (noteRows[i].returnId){
					if (!noteRows[i].quantity){
						var obj = $("#row"+i+"jqxgridNote div[role='gridcell']");
						$(obj[3]).jqxTooltip({theme:'tooltip-validation', content: '${uiLabelMap.FieldRequired}', position: 'bottom', autoHide: false});
						setTimeout(function(){
							$(obj[3]).jqxTooltip("open");
						}, 100);
						return false;
					}
					if (!noteRows[i].reasonId){
						var obj = $("#row"+i+"jqxgridNote div[role='gridcell']");
						$(obj[6]).jqxTooltip({theme:'tooltip-validation', content: '${uiLabelMap.FieldRequired}', position: 'bottom', autoHide: false});
						setTimeout(function(){
							$(obj[6]).jqxTooltip("open");
						}, 100);
						return false;
					}
				}
			})(i);
		}
		
		var listNoteItems = new Array();
		for(var i = 0; i < noteRows.length; i++){
			if (noteRows[i]["returnId"]){
				listNoteItems.push(noteRows[i]);
			}
		}
		for (var i = 0; i < listNoteItems.length; i ++){
			if (listNoteItems[i]["quantity"]){
				var map = {};
				map['returnId'] = listNoteItems[i].returnId;
				map['returnItemSeqId'] = listNoteItems[i].returnItemSeqId;
				map['actualExpireDate'] = formatFullDate(listNoteItems[i].expireDate);
				if (listNoteItems[i].actualManufacturedDate){
					map['actualManufacturedDate'] = formatFullDate(listNoteItems[j].actualManufacturedDate);
				}
				map['actualDeliveredQuantity'] = listNoteItems[i].quantity;
				map['quantity'] = listNoteItems[i].quantity;
				map['statusId'] = 'DELI_ITEM_DELIVERED';
				map['inventoryStatusId'] = listNoteItems[i].inventoryStatusId;
				listReturnItems.push(map);
			}
		}
		if (listReturnItems.length <= 0){
			$("#notifyMissInfoId").jqxNotification("open");
			return false;
		} else {
			listReturnItems = JSON.stringify(listReturnItems);
			var facTmp = $("#destFacilityId").jqxDropDownList('getSelectedItem');
			var destFacilityIdTmp = facTmp.value;
			var ctmTmp = $("#facilityCTMId").jqxDropDownList('getSelectedItem');
			var destContactMechIdTmp = ctmTmp.value;
			jQuery.ajax({
		        url: "createReturnDelivery",
		        type: "POST",
		        async: true,
		        data: {'listReturnItems': listReturnItems,
		        		"partyIdTo": company,
		        		"returnId": returnId,
		        		"statusId": "DLV_DELIVERED",
		        		"destFacilityId": destFacilityIdTmp,
						"destContactMechId": destContactMechIdTmp,
						"deliveryDate": formatFullDate($('#deliveryDate').jqxDateTimeInput('value')),
		        		'deliveryTypeId': "DELIVERY_RETURN",
		        		},
		        success: function(res) {
		        	var newDeliveryId = res.deliveryId;
		        	if (pathScanFile){
		        		updateDeliveryPathScanFile(path, newDeliveryId);
		        	}
		        	$("#notifyId").jqxNotification("open");
		        	window.onbeforeunload = null;
		        	window.location.href = "getDetailPurchaseDelivery?deliveryId="+newDeliveryId;
		        }
		    });
		}
	}
	function createPurchaseDelivery() {
		var rows = $('#jqxgridProduct').jqxGrid('getrows');
		
		var listOrderItems = new Array();
		for(var i = 0; i < rows.length; i++){
			if (rows[i]["inspectionQty"]){
				var map = {};
				map['orderId'] = rows[i].orderId;
				map['orderItemSeqId'] = rows[i].orderItemSeqId;
				map['actualExpireDate'] = formatFullDate(rows[i].expDateIns);
				map['actualManufacturedDate'] = formatFullDate(rows[i].proDateIns);
				map['actualDeliveredQuantity'] = rows[i].inspectionQty;
				map['quantity'] = rows[i].quantity;
				map['statusId'] = 'DELI_ITEM_DELIVERED';
				listOrderItems[i] = map;
			}
		}
		if (listOrderItems.length <= 0){
			$("#notifyMissInfoId").jqxNotification("open");
			return false;
		} else {
			listOrderItems = JSON.stringify(listOrderItems);
			var facTmp = $("#destFacilityId").jqxDropDownList('getSelectedItem');
			var destFacilityIdTmp = facTmp.value;
			var ctmTmp = $("#facilityCTMId").jqxDropDownList('getSelectedItem');
			var destContactMechIdTmp = ctmTmp.value;
			jQuery.ajax({
		        url: "createDelivery",
		        type: "POST",
		        async: true,
		        data: {'listOrderItems': listOrderItems,
		        		"partyIdTo": company,
		        		"orderId": orderId,
		        		"statusId": "DLV_DELIVERED",
						"destFacilityId": destFacilityIdTmp,
						"destContactMechId": destContactMechIdTmp,
						"deliveryDate": formatFullDate($('#deliveryDate').jqxDateTimeInput('value')),
		        		'deliveryTypeId': "DELIVERY_PURCHASE",
		        		},
		        success: function(res) {
		        	var newDeliveryId = res.deliveryId;
		        	if (pathScanFile){
		        		updateDeliveryPathScanFile(pathScanFile, newDeliveryId);
		        	}
		        	$("#notifyId").jqxNotification("open");
		        	window.onbeforeunload = null;
		        	window.location.href = "getDetailPurchaseDelivery?deliveryId="+newDeliveryId;
		        }
		    });
		}
	}
	function checkInfoCompleted(){
		var testCompleted = true;
		var rows = $('#jqxgridProduct').jqxGrid('getrows');
		for(var i = 0; i < rows.length; i++){
			if (rows[i].orderId){
				if (!rows[i].expDateIns){
					var obj = $("#row"+i+"jqxgridProduct div[role='gridcell']");
					$(obj[7]).jqxTooltip({theme:'tooltip-validation', content: '${uiLabelMap.FieldRequired}', autoHideDelay: 3000, position: 'bottom', autoHide: true});
					setTimeout(function(){
						$(obj[7]).jqxTooltip("open");
					}, 100);
					testCompleted = false;
				}
				if (!rows[i].proDateIns){
					var obj = $("#row"+i+"jqxgridProduct div[role='gridcell']");
					$(obj[6]).jqxTooltip({theme:'tooltip-validation', content: '${uiLabelMap.FieldRequired}', autoHideDelay: 3000, position: 'bottom', autoHide: true});
					setTimeout(function(){
						$(obj[6]).jqxTooltip("open");
					}, 100);
					testCompleted = false;
				}
				if (!rows[i].inspectionQty){
					var obj = $("#row"+i+"jqxgridProduct div[role='gridcell']");
					$(obj[9]).jqxTooltip({theme:'tooltip-validation', content: '${uiLabelMap.FieldRequired}', autoHideDelay: 3000, position: 'bottom', autoHide: true});
					setTimeout(function(){
						$(obj[9]).jqxTooltip("open");
					}, 100);
					testCompleted = false;
				}
			}
		}
		var noteRows = $('#jqxgridNote').jqxGrid('getrows');
		for(var i = 0; i < noteRows.length; i++){
			if (noteRows[i].orderId){
				if (!noteRows[i].quantity){
					var obj = $("#row"+i+"jqxgridNote div[role='gridcell']");
					$(obj[3]).jqxTooltip({theme:'tooltip-validation', content: '${uiLabelMap.FieldRequired}', position: 'bottom', autoHideDelay: 3000, autoHide: true});
					setTimeout(function(){
						$(obj[3]).jqxTooltip("open");
					}, 100);
					return false;
					testCompleted = false;
				}
				if (!noteRows[i].deliveryTypeId){
					var obj = $("#row"+i+"jqxgridNote div[role='gridcell']");
					$(obj[6]).jqxTooltip({theme:'tooltip-validation', content: '${uiLabelMap.FieldRequired}', position: 'bottom', autoHideDelay: 3000, autoHide: true});
					setTimeout(function(){
						$(obj[6]).jqxTooltip("open");
					}, 100);
					return false;
					testCompleted = false;
				}
			}
		}
		return testCompleted;
	}
	function createOtherDeliverys(){
		var noteRows = $('#jqxgridNote').jqxGrid('getrows');
		var listNoteItems = new Array();
		for(var i = 0; i < noteRows.length; i++){
			if (noteRows[i]["orderId"]){
				listNoteItems.push(noteRows[i]);
			}
		}
		var listOtherDlvTypes = new Array();
		for(var i = 0; i < listNoteItems.length; i++){
			var deliveryTypeTmp = listNoteItems[i].deliveryTypeId;
			var test = false;
			for (var j = 0; j < listOtherDlvTypes.length; j ++){
				if (listOtherDlvTypes[j] == deliveryTypeTmp){
					test = true;
				}
			}
			if (!test){
				listOtherDlvTypes.push(deliveryTypeTmp);
			}
		}
		var listAllExportItems = Array();
		for(var i = 0; i < listOtherDlvTypes.length; i++){
			var listExportItems = new Array();
			for (var j = 0; j < listNoteItems.length; j ++){
				if (listOtherDlvTypes[i] == listNoteItems[j].deliveryTypeId){
					var mapNote = {};
					mapNote['orderId'] = listNoteItems[j].orderId;
					mapNote['orderItemSeqId'] = listNoteItems[j].orderItemSeqId;
					mapNote['quantity'] = listNoteItems[j].quantity;
					mapNote['actualExpireDate'] = formatFullDate(listNoteItems[j].expireDate);
					var tempDate = listNoteItems[j].datetimeManufactured.split("/");
					mapNote['actualManufacturedDate'] = formatFullDate(new Date(tempDate[2], tempDate[1] - 1, tempDate[0]));
					mapNote['statusId'] = 'DELI_ITEM_CREATED';
					listExportItems.push(mapNote);
				}
			}
			var mapTmp = {};
			mapTmp['listExportItems'] = listExportItems;
			mapTmp['deliveryTypeId'] = listOtherDlvTypes[i];
			mapTmp['partyIdFrom'] = company;
			mapTmp['orderId'] = orderId;
			mapTmp['deliveryDate'] = formatFullDate($('#deliveryDate').jqxDateTimeInput('value'));
			mapTmp['statusId'] = "DLV_CREATED";
			var facTmp = $("#destFacilityId").jqxDropDownList('getSelectedItem');
			mapTmp['originFacilityId'] = facTmp.value;
			var ctmTmp = $("#facilityCTMId").jqxDropDownList('getSelectedItem');
			mapTmp['originContactMechId'] = ctmTmp.value;
			mapTmp['noNumber'] = '';
			
			listAllExportItems.push(mapTmp);
		}
		if (listAllExportItems.length > 0){
			listAllExportItems = JSON.stringify(listAllExportItems);
			jQuery.ajax({
		        url: "createMultiDelivery",
		        type: "POST",
		        async: true,
		        data: {'listDeliverys': listAllExportItems,
		        		},
		        success: function(res) {
		        	createPurchaseDelivery();
		        }
		    });
		} else {
			createPurchaseDelivery();
		}
	}
	function addZero(i) {
	    if (i < 10) {i = "0" + i;}
	    return i;
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
	function addNewNoteRow(){
		var datarow = generateNoteRow();
        $("#jqxgridNote").jqxGrid('addrow', null, datarow);
	}
	function addNewRow(){
		var datarow = generaterow();
        $("#jqxgridProduct").jqxGrid('addrow', null, datarow);
	}
	
	function deleteRow(){
		var selectedrowindex = $("#jqxgridProduct").jqxGrid('getselectedrowindex');
        var rowscount = $("#jqxgridProduct").jqxGrid('getdatainformation').rowscount;
        if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
            var id = $("#jqxgridProduct").jqxGrid('getrowid', selectedrowindex);
            $("#jqxgridProduct").jqxGrid('deleterow', id);
        }
	}
	
	function deleteNoteRow(){
		var selectedrowindex = $("#jqxgridNote").jqxGrid('getselectedrowindex');
        var rowscount = $("#jqxgridNote").jqxGrid('getdatainformation').rowscount;
        if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
            var id = $("#jqxgridNote").jqxGrid('getrowid', selectedrowindex);
            $("#jqxgridNote").jqxGrid('deleterow', id);
        }
	}
	function updateNoteRowData(productId){
		var datarow = generateNoteRow(productId);
        var selectedrowindex = $("#jqxgridNote").jqxGrid('getselectedrowindex');
        var rowscount = $("#jqxgridNote").jqxGrid('getdatainformation').rowscount;
        if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
            var id = $("#jqxgridNote").jqxGrid('getrowid', selectedrowindex);
            $("#jqxgridNote").jqxGrid('updaterow', id, datarow);
            $("#jqxgridNote").jqxGrid('ensurerowvisible', selectedrowindex);
        }
	}
	function updateProductById(productId){
		var datarow = generaterow(productId);
        var selectedrowindex = $("#jqxgridProduct").jqxGrid('getselectedrowindex');
        var rowscount = $("#jqxgridProduct").jqxGrid('getdatainformation').rowscount;
        if (selectedrowindex >= 0 && selectedrowindex < rowscount) {
            var id = $("#jqxgridProduct").jqxGrid('getrowid', selectedrowindex);
            $("#jqxgridProduct").jqxGrid('updaterow', id, datarow);
            $("#jqxgridProduct").jqxGrid('ensurerowvisible', selectedrowindex);
        }
	}
	function getFormattedDate(date) {
		  var year = date.getFullYear();
		  var month = (1 + date.getMonth()).toString();
		  month = month.length > 1 ? month : '0' + month;
		  var day = date.getDate().toString();
		  day = day.length > 1 ? day : '0' + day;
		  return day + '/' + month + '/' + year;
	}
	function generaterow(productId) {
		var row = {};
		if (productId){
			for(var i = 0; i < orderItemData.length; i++){
				if (orderItemData[i].productId == productId){
					var orderIdTmp = JSON.stringify(orderItemData[i].orderId);
					row["orderId"] = JSON.parse(orderIdTmp);
					var itemSeqIdTmp = JSON.stringify(orderItemData[i].orderItemSeqId);
					row["orderItemSeqId"] = JSON.parse(itemSeqIdTmp);
					var idTmp = JSON.stringify(orderItemData[i].productId);
					row["productId"] = JSON.parse(idTmp);
					var nameTmp = JSON.stringify(orderItemData[i].productName);
					row["productName"] = JSON.parse(nameTmp);
					var qtyUomTmp = JSON.stringify(orderItemData[i].quantityUomId);
					row["quantityUomId"] = JSON.parse(qtyUomTmp);
					var packingTmp = JSON.stringify(orderItemData[i].productPacking);
					row["productPacking"] = JSON.parse(packingTmp);
					var manufDate = JSON.stringify(orderItemData[i].datetimeManufactured);
					row["datetimeManufactured"] = JSON.parse(manufDate);
					row["expireDate"] = orderItemData[i].expireDate;
					var qtyTmp = JSON.stringify(orderItemData[i].quantity);
					row["quantity"] = JSON.parse(qtyTmp);
					row["proDateIns"] = '';
					row["expDateIns"] = '';
					row["inspectionQty"] = '';
					break;
				}
			}
		} else {
			row["productId"] = "";
			row["productName"] = "";
			row["quantityUomId"] = "";
			row["productPacking"] = "";
			row["datetimeManufactured"] = "";
			row["expireDate"] = "";
			row["quantity"] = "";
		}
        return row;
    }
	function generateNoteRow(productId) {
		var row = {};
		if (productId){
			for(var i = 0; i < orderItemData.length; i++){
				if (orderItemData[i].productId == productId){
					var idTmp = JSON.stringify(orderItemData[i].productId);
					row["productId"] = JSON.parse(idTmp);
					var nameTmp = JSON.stringify(orderItemData[i].productName);
					row["productName"] = JSON.parse(nameTmp);
					var qtyUomTmp = JSON.stringify(orderItemData[i].quantityUomId);
					row["quantityUomId"] = JSON.parse(qtyUomTmp);
					var manufDate = JSON.stringify(orderItemData[i].datetimeManufactured);
					row["datetimeManufactured"] = JSON.parse(manufDate);
					var expDateTmp = JSON.stringify(orderItemData[i].expireDate);
					var tem = new Date(orderItemData[i].expireDate);
					row["expireDate"] = getFormattedDate(tem);
					row["quantity"] = '';
				}
				break;
			}
		} else {
			row["productId"] = "";
			row["productName"] = "";
			row["quantityUomId"] = "";
			row["datetimeManufactured"] = "";
			row["expireDate"] = "";
			row["quantity"] = "";
		}
        return row;
    }
	
</script>