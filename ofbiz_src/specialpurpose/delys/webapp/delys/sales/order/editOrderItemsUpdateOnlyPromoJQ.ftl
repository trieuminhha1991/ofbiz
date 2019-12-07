<style type="text/css">
	.background-promo {
		color: #009900 !important;
  		background: #f1ffff !important;
	}
</style>
<script language="JavaScript" type="text/javascript">
  	function quicklookup(element) {
    	window.location='<@ofbizUrl>LookupBulkAddSupplierProductsInApprovedOrder</@ofbizUrl>?orderId='+element.value;
  	}
</script>
<script type="text/javascript">
	var productQuantities = new Array();
	var rowChangeArr = new Array();
	<#if orderItemSGList?exists && orderItemSGList?has_content>
		<#assign defaultItemDeliveryDate = ""/>
		<#list orderItemSGPromoList as orderItem>
			<#if (orderItem.productId?exists)>
				var objNew = {};
	   			objNew["productId"] = "${orderItem.productId}";
	   			<#if orderItem.quantityUomId?exists>
	   				objNew["quantityUomId"] = "${orderItem.quantityUomId}";
	   			</#if>
	   			<#if orderItem.alternativeQuantity?exists>
	   				objNew["quantity"] = "${orderItem.alternativeQuantity}";
	   			<#elseif orderItem.quantity?exists>
	   				objNew["quantity"] = "${orderItem.quantity}";
	   			</#if>
	   			<#if orderItem.expireDate?exists>
	   				objNew["expireDate"] = "${orderItem.expireDate}";
	   			</#if>
	   			<#if orderItem.orderItemSeqId?exists>
	   				objNew["orderItemSeqId"] = "${orderItem.orderItemSeqId}";
	   			</#if>
	   			<#if orderItem.shipGroupSeqId?exists>
	   				objNew["shipGroupSeqId"] = "${orderItem.shipGroupSeqId}";
	   			</#if>
	   			productQuantities.push(objNew);
				rowChangeArr.push("${orderItem.orderItemSeqId}");
			</#if>
		</#list>
		<#assign prodCatalogId = orderItemSGList[0].prodCatalogId?default("")/>
		<#assign defaultItemDeliveryDate = orderItemSGList[0].estimatedDeliveryDate?default("")/>
	</#if>
  	var cellclass = function (row, columnfield, value) {
 		var data = $('#jqxgridSO').jqxGrid('getrowdata', row);
        if (data.isPromo != undefined && data.isPromo != null && "Y" == data.isPromo) {
            return 'background-promo';
        }
    }
</script>
<#if orderHeader?has_content>
	<#-- price change rules -->
	<#assign allowPriceChange = false/>
	<#if (orderHeader.orderTypeId == 'PURCHASE_ORDER' || security.hasEntityPermission("ORDERMGR", "_SALES_PRICEMOD", session))>
	    <#assign allowPriceChange = true/>
	</#if>
<div class="row-fluid">
	<div class="span12">
		<#if !orderItemList?has_content>
            <span class="alert">${uiLabelMap.checkhelper_sales_order_lines_lookup_failed}</span>
        <#else>
	        <form name="updateItemInfo" id="updateItemInfo" class="form-horizontal basic-custom-form" method="post" action="<@ofbizUrl>updateOrderItemsSalesUop</@ofbizUrl>">
	            <input type="hidden" name="orderId" value="${orderId}"/>
	            <input type="hidden" name="orderItemSeqId" value=""/>
	            <input type="hidden" name="shipGroupSeqId" value=""/>
	        	<#if (orderHeader.orderTypeId == 'PURCHASE_ORDER')>
	              	<input type="hidden" name="supplierPartyId" value="${partyId}"/>
	              	<input type="hidden" name="orderTypeId" value="PURCHASE_ORDER"/>
	        	</#if>
	        </form>
	        <div style="margin-bottom:10px">
	            <div class="row-fluid">
					<div class="span12">
		            	<#assign dataField="[{ name: 'orderItemSeqId', type: 'string' },
		            						{ name: 'shipGroupSeqId', type: 'string' },
		            						{ name: 'productId', type: 'string' },
						               		{ name: 'productName', type: 'string' },
						               		{ name: 'quantityUomId', type: 'string'},
						               		{ name: 'productPackingUomId', type: 'string'},
						               		{ name: 'quantity', type: 'number', formatter: 'integer'},
						               		{ name: 'packingUomId', type: 'string'}, 
						               		{ name: 'isPromo', type: 'string'}, ">
						<#if isEditExpireDate?exists && isEditExpireDate>
						<#assign dataField= dataField + "{ name: 'expireDate', type: 'string'}, 
											{ name: 'expireDateList', type: 'string'},
						               		{ name: 'atpTotal', type: 'string'},
						               		{ name: 'qohTotal', type: 'string'}">
						</#if>
					    <#assign dataField= dataField + "]"/>
						    
	<#--{ text: '${uiLabelMap.DAOrderItemSeqId}', dataField: 'orderItemSeqId', width: '80px', editable:false},-->
						<#assign columnlist="{ text: '${uiLabelMap.DAShipGroupSeqId}', dataField: 'shipGroupSeqId', width: '80px', editable:false, cellclassname: cellclass},
											 { text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '180px', editable:false, cellclassname: cellclass},
											 { text: '${uiLabelMap.DAProductName}', dataField: 'productName', editable:false, cellclassname: cellclass},
											 { text: '${uiLabelMap.DAUom}', dataField: 'quantityUomId', width: '120px', editable:false, cellclassname: cellclass, 
											 	cellsrenderer: function(row, column, value){
											 		var data = $('#jqxgridSO').jqxGrid('getrowdata', row);
											 		var packingUomIdArray = data['packingUomId'];
											 		if (packingUomIdArray != undefined && packingUomIdArray != null && packingUomIdArray.length > 0) {
											 			for (var i = 0 ; i < packingUomIdArray.length; i++){
							    							if (value == packingUomIdArray[i].uomId){
							    								return '<span title = ' + packingUomIdArray[i].description +'>' + packingUomIdArray[i].description + '</span>';
							    							}
							    						}
											 		}
						    						return '<span title=' + value +'>' + value + '</span>';
												}
						                     },">
						<#if isEditExpireDate?exists && isEditExpireDate>
						<#assign columnlist= columnlist + "{ text: '${uiLabelMap.DAExpireDate}', dataField: 'expireDate', width: '180px', columntype: 'dropdownlist', filterable:false, sortable:false, cellclassname: cellclass,  
										 		initeditor: function (row, cellvalue, editor) {
											 		var expireDateData = new Array();
													var data = $('#jqxgridSO').jqxGrid('getrowdata', row);
													var rowindex = row;
													var itemSelected = data['expireDate'];
													var expireDateArray = data['expireDateList'];
													var rowNull = {};
													rowNull['expireDate'] = '';
													rowNull['qohTotal'] = '';
													rowNull['atpTotal'] = '';
													expireDateData[0] = rowNull;
													for (var i = 0; i < expireDateArray.length; i++) {
														var expireDateItem = expireDateArray[i];
														var row = {};
														row['expireDate'] = '' + expireDateItem.expireDate;
														row['qohTotal'] = '' + expireDateItem.qohTotal;
														row['atpTotal'] = '' + expireDateItem.atpTotal;
														expireDateData[i+1] = row;
													}
											 		var sourceDataPacking = {
										                localdata: expireDateData,
										                datatype: \"array\"
										            };
										            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
										            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'expireDate', valueMember: 'expireDate'});
										            editor.jqxDropDownList('selectItem', itemSelected);
						                      	},
						                      	//createeditor: function (row, cellvalue, editor) {
						                      	//	editor.on('select', function (event){
												//	    var args = event.args;
												//	    if (args) {
														    // index represents the item's index.                
												//		    var index = args.index;
												//		    var item = args.item;
														    // get item's label and value.
														    //var label = item.label;
														    //var value = item.value;
												//		}
												//	});
						                      	//}
						                 	},
						                 	{ text: '${uiLabelMap.DAQOHTotal}', dataField: 'qohTotal', width: '100px', editable:false, filterable:false, sortable:false, cellclassname: cellclass},
						                 	{ text: '${uiLabelMap.DAATPTotal}', dataField: 'atpTotal', width: '100px', editable:false, filterable:false, sortable:false, cellclassname: cellclass},">
						</#if>
						<#assign columnlist= columnlist + "{ text: '${uiLabelMap.DAQuantity}', dataField: 'quantity', cellsalign: 'right', filterable:false, editable:false, sortable:false, cellclassname: cellclass, 
											 	cellsrenderer: function(row, column, value){
											 		var data = $('#jqxgridSO').jqxGrid('getrowdata', row);
						    						var indexFinded = rowChangeArr.indexOf(data.orderItemSeqId);
						    						var productId = data.productId;
						    						var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
						    						if (indexFinded > -1) {
											   			var objSelected = productQuantities[indexFinded];
											   			if (productId == objSelected.productId) {
											   				data.quantity = productQuantities[indexFinded].quantity;
											   				returnVal += productQuantities[indexFinded].quantity + '</div>';
											   				return returnVal;
											   			} else {
											   				for(i = 0 ; i < productQuantities.length; i++){
								    							if (productId == productQuantities[i].productId){
								    								data.quantity = productQuantities[i].quantity;
								    								returnVal += productQuantities[i].quantity + '</div>';
											   						return returnVal;
								    							}
								    						}
											   			}
										   			}
										   			returnVal += value + '</div>';
									   				return returnVal;
											 	}
											 }
						              		"/>
						<#-- defaultSortColumn="productId" statusbarjqxgridSO -->
						<#if isEditExpireDate?exists && isEditExpireDate>
						<@jqGrid id="jqxgridSO" clearfilteringbutton="false" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
								viewSize="15" showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" 
								url="jqxGeneralServicer?sname=JQGetListOrderItem&catalogId=${currentCatalogId?if_exists}&orderId=${orderHeader.orderId?if_exists}"/>
						<#else>
						<@jqGrid id="jqxgridSO" clearfilteringbutton="false" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
								viewSize="15" showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" 
								url="jqxGeneralServicer?sname=JQGetListOrderItem&catalogId=${currentCatalogId?if_exists}&orderId=${orderHeader.orderId?if_exists}"/>
						</#if>
								<#--JQGetListProductByCategoryCatalogByOrder-->
					</div>
				</div>
	        </div>
	        <div class="row-fluid wizard-actions">
				<a href="javascript: updateCartItems();" class="btn btn-small btn-primary">
	        		<i class="icon-ok open-sans">${uiLabelMap.DAUpdate}</i>
	        	</a>
			</div>
	        <div id="checkoutInfoLoader" style="overflow: hidden; position: absolute; width: 1120px; height: 640px; display: none;" class="jqx-rc-all jqx-rc-all-olbius">
				<div style="z-index: 99999; margin-left: -66px; left: 50%; top: 5%; margin-top: -24px; position: relative; width: 100px; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
					<div style="float: left;">
						<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
						<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
					</div>
				</div>
			</div>
	 		<table class="basic-table" cellspacing="0" style="width:100%">
	 			<#list orderHeaderAdjustments as orderHeaderAdjustment>
	 				<#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
		            <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
		            <#assign orderAdjustmentId = orderHeaderAdjustment.get("orderAdjustmentId")>
		            <#assign productPromoCodeId = ''>
		            <#if adjustmentType.get("orderAdjustmentTypeId") == "PROMOTION_ADJUSTMENT" && orderHeaderAdjustment.get("productPromoId")?has_content>
		                <#assign productPromo = orderHeaderAdjustment.getRelatedOne("ProductPromo", false)>
		                <#assign productPromoCodes = delegator.findByAnd("ProductPromoCode", {"productPromoId":productPromo.productPromoId}, null, false)>
		                <#assign orderProductPromoCode = ''>
		                <#list productPromoCodes as productPromoCode>
		                    <#if !(orderProductPromoCode?has_content)>
		                        <#assign orderProductPromoCode = delegator.findOne("OrderProductPromoCode", {"productPromoCodeId":productPromoCode.productPromoCodeId, "orderId":orderHeaderAdjustment.orderId}, false)?if_exists>
		                    </#if>
		                </#list>
		                <#if orderProductPromoCode?has_content>
		                    <#assign productPromoCodeId = orderProductPromoCode.get("productPromoCodeId")>
		                </#if>
		            </#if>
		            <#if adjustmentAmount != 0>
			            <tr>
			              	<td width="80%">
			              		<span class="label" style="float:right; margin:3px 8px">
			              			${orderHeaderAdjustment.comments?if_exists}&nbsp;
			              			${orderHeaderAdjustment.get("description")?if_exists}
				            	</span>
				           	</td>
			              	<td width="10%" nowrap="nowrap"><@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId/></td>
			              	<td width="10%" colspan="2">&nbsp;</td>
			            </tr>
	            	</#if>
	        	</#list>
	        </table>
	        <#-- subtotal -->
	        <table class="basic-table" cellspacing="0" style="width:100%">
	            <tr class="align-text">
	              <td width="80%"><span class="label" style="float:right; margin:3px 8px">${uiLabelMap.OrderItemsSubTotal}</span></td>
	              <td width="10%" nowrap="nowrap"><@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId/></td>
	              <td width="10%" colspan="2">&nbsp;</td>
	            </tr>
	
	            <#-- other adjustments -->
	            <tr class="align-text">
	              <td><span class="label" style="float:right; margin:3px 8px">${uiLabelMap.DATotalOrderAdjustments}</span></td>
	              <td nowrap="nowrap"><@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId/></td>
	              <td colspan="2">&nbsp;</td>
	            </tr>
	
	            <#-- shipping adjustments -->
	            <tr class="align-text">
	              <td><span class="label" style="float:right; margin:3px 8px">${uiLabelMap.OrderTotalShippingAndHandling}</span></td>
	              <td nowrap="nowrap"><@ofbizCurrency amount=shippingAmount isoCode=currencyUomId/></td>
	              <td colspan="2">&nbsp;</td>
	            </tr>
	
	            <#-- tax adjustments -->
	            <tr class="align-text">
	              <td><span class="label" style="float:right; margin:3px 8px">${uiLabelMap.OrderTotalSalesTax}</span></td>
	              <td nowrap="nowrap"><@ofbizCurrency amount=taxAmount isoCode=currencyUomId/></td>
	              <td colspan="2">&nbsp;</td>
	            </tr>
	
	            <#-- grand total -->
	            <tr class="align-text">
	              <td><span class="label" style="float:right; margin:3px 8px">${uiLabelMap.OrderTotalDue}</span></td>
	              <td nowrap="nowrap"><@ofbizCurrency amount=grandTotal isoCode=currencyUomId/></td>
	              <td colspan="2">&nbsp;</td>
	            </tr>
	        </table>
        </#if>
	</div><!--.span12-->
</div>
<script type="text/javascript">
	function updateCartItems() {
		if (productQuantities.length > 0) {
			var strParam = "N";
			var countQuantity = 0;
			for (i = 0; i < productQuantities.length; i++) {
				var rowData = productQuantities[i];
	   			var productId = rowData.productId;
	   			var quantity = rowData.quantity;
	   			var quantityUomId = rowData.quantityUomId;
	   			var expireDate = rowData.expireDate;
	   			var orderItemSeqId = rowData.orderItemSeqId;
	   			var shipGroupSeqId = rowData.shipGroupSeqId;
	   			if (quantityUomId == undefined) quantityUomId = "";
	   			if (expireDate == undefined) expireDate = "";
	   			if (orderItemSeqId == undefined) orderItemSeqId = "";
	   			if (shipGroupSeqId == undefined) shipGroupSeqId = "";
	   			
	   			countQuantity += quantity;
				strParam += "|OLBIUS|" + productId + "|SUIBLO|" + quantity + "|SUIBLO|" + quantityUomId + "|SUIBLO|" + expireDate + "|SUIBLO|" + orderItemSeqId + "|SUIBLO|" + shipGroupSeqId;
			}
			if (countQuantity <= 0) {
				bootbox.dialog("${uiLabelMap.DANotYetChooseProduct}!", [{
					"label" : "OK",
					"class" : "btn-small btn-primary",
					}]
				);
				return false;
			} else {
				//  style='color:#b94a48'
				var formSend = document.getElementById('updateItemInfo');
			    var hiddenField = document.createElement("input");
	            hiddenField.setAttribute("type", "hidden");
	            hiddenField.setAttribute("name", "strParam");
	            hiddenField.setAttribute("value", strParam);
	            formSend.appendChild(hiddenField);
			    formSend.submit();
			}
		} else {
			//bootbox.alert("${uiLabelMap.DANotYetChooseProduct}!");
			bootbox.dialog("${uiLabelMap.DANotYetChooseProduct}!", [{
				"label" : "OK",
				"class" : "btn-small btn-primary",
				}]
			);
			return false;
		}
	}
</script>
<script type="text/javascript">
	$("#jqxgridSO").on("cellBeginEdit", function(event){
		var args = event.args;
    	if (args.datafield == "expireDate") {
    		var rowBoundIndex = args.rowindex;
	    	var data = $("#jqxgridSO").jqxGrid("getrowdata", rowBoundIndex);
	    	var valueSelected = data.expireDate;
	    	var expireDateList = data.expireDateList;
	    	if (valueSelected != null && expireDateList != null) {
	    		for (var i = 0; i < expireDateList.length; i++) {
	    			var row = expireDateList[i];
	    			if (valueSelected != null && valueSelected == row.expireDate) {
						$('#jqxgridSO').jqxGrid('setcellvalue', rowBoundIndex, 'qohTotal', row.qohTotal, 'atpTotal', row.atpTotal);
	    			}
	    		}
	    	}
    	}
	});
	$("#jqxgridSO").on("cellEndEdit", function (event) {
    	var args = event.args;
    	if (args.datafield == "quantity") {
    		var rowBoundIndex = args.rowindex;
	    	var data = $("#jqxgridSO").jqxGrid("getrowdata", rowBoundIndex);
	    	if (data && data.productId) {
	    		var productId = data.productId;
	    		var quantityUomId = data.quantityUomId;
	    		var orderItemSeqId = data.orderItemSeqId;
	    		var shipGroupSeqId = data.shipGroupSeqId;
		   		var oldValue = args.oldvalue;
		   		var newValue = args.value;
		   		var indexFinded = rowChangeArr.indexOf(orderItemSeqId);
		   		if (indexFinded > -1) {
		   			var objSelected = productQuantities[indexFinded];
		   			if (productId == objSelected["productId"] && oldValue == objSelected["quantity"]) {
		   				objSelected["quantity"] = newValue;
		   				objSelected["quantityUomId"] = quantityUomId;
		   			} else {
		   				for (var i = 0; i < productQuantities.length; i++) {
		   					var objItem = productQuantities[i];
		   					if (productId == objItem["productId"]) {
		   						objItem["quantity"] = newValue;
		   						objItem["quantityUomId"] = quantityUomId;
		   						break;
		   					}
		   				}
		   			}
		   		} else {
		   			if (newValue && !(/^\s*$/.test(newValue))) {
		   				var objNew = {};
			   			objNew["productId"] = productId;
			   			objNew["quantityUomId"] = quantityUomId;
			   			objNew["quantity"] = newValue;
			   			objNew["orderItemSeqId"] = orderItemSeqId;
			   			objNew["shipGroupSeqId"] = shipGroupSeqId;
			   			var expireDate = data.expireDate;
			   			if (expireDate != undefined) {
			   				objNew["expireDate"] = expireDate;
			   			}
			   			productQuantities.push(objNew);
			   			rowChangeArr.push(orderItemSeqId);
		   			}
		   		}
	    	}
    	} else if (args.datafield == "quantityUomId") {
    		var rowBoundIndex = args.rowindex;
	    	var data = $("#jqxgridSO").jqxGrid("getrowdata", rowBoundIndex);
	    	if (data && data.productId) {
	    		var productId = data.productId;
	    		var quantityUomId = data.quantityUomId;
	    		var orderItemSeqId = data.orderItemSeqId;
		   		var oldValue = args.oldvalue;
		   		var newValue = args.value;
		   		var indexFinded = rowChangeArr.indexOf(orderItemSeqId);
		   		if (indexFinded > -1) {
		   			var objSelected = productQuantities[indexFinded];
		   			if (productId == objSelected["productId"] && oldValue == objSelected["quantityUomId"]) {
		   				objSelected["quantityUomId"] = newValue;
		   			} else {
		   				for (var i = 0; i < productQuantities.length; i++) {
		   					var objItem = productQuantities[i];
		   					if (productId == objItem["productId"]) {
		   						objItem["quantityUomId"] = newValue;
		   						break;
		   					}
		   				}
		   			}
	   			}
	    	}
    	} else if (args.datafield == "expireDate") {
    		var rowBoundIndex = args.rowindex;
	    	var data = $("#jqxgridSO").jqxGrid("getrowdata", rowBoundIndex);
	    	if (data && data.productId) {
	    		var oldValue = args.oldvalue;
		   		var newValue = args.value;
	    		var valueSelected = args.value; //newValue
	    		var expireDateList = data.expireDateList;
	    		var orderItemSeqId = data.orderItemSeqId;
	    		if (valueSelected != undefined && expireDateList != undefined) {
		    		for (var i = 0; i < expireDateList.length; i++) {
		    			var row = expireDateList[i];
		    			if (valueSelected != null && valueSelected == row.expireDate) {
							$('#jqxgridSO').jqxGrid('setcellvalue', rowBoundIndex, 'qohTotal', row.qohTotal);
							$('#jqxgridSO').jqxGrid('setcellvalue', rowBoundIndex, 'atpTotal', row.atpTotal);
		    			}
		    		}
		    	}
		    	var productId = data.productId;
		    	var indexFinded = rowChangeArr.indexOf(orderItemSeqId);
		   		if (indexFinded > -1) {
		   			var objSelected = productQuantities[indexFinded];
		   			if (productId == objSelected["productId"] && oldValue == objSelected["expireDate"]) {
		   				objSelected["expireDate"] = newValue;
		   				objSelected["quantityUomId"] = data.quantityUomId;
		   			} else {
		   				for (var i = 0; i < productQuantities.length; i++) {
		   					var objItem = productQuantities[i];
		   					if (productId == objItem["productId"]) {
		   						objItem["expireDate"] = newValue;
		   						objItem["quantityUomId"] = data.quantityUomId;
		   						break;
		   					}
		   				}
		   			}
	   			}
	    	}
    	}
	});
</script>
</#if>