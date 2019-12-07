<#if orderId?exists>
<div id="alterpopupWindow" style="display:none">
	<div>${uiLabelMap.BSAddProductToOrder}</div>
	<div class='form-window-container'>
		<div class='form-window-content'>
			<input type="hidden" name="orderId" id="orderId_w" value="${orderId}"/>
	        <#if !catalogCol?has_content>
                <input type="hidden" name="prodCatalogId" id="wn_prodCatalogId" value=""/>
            </#if>
            <#if catalogCol?has_content && catalogCol?size == 1>
                <input type="hidden" name="prodCatalogId" id="wn_prodCatalogId" value="${catalogCol.first}"/>
            </#if>
            <#if shipGroups?size == 1>
                <input type="hidden" name="shipGroupSeqId" id="wn_shipGroupSeqId" value="${shipGroups.first.shipGroupSeqId}"/>
            </#if>
			<div id="containerAppendItem" style="background-color: transparent; overflow: auto;"></div>
		    <div id="jqxNotificationAppendItem" style="margin-bottom:5px">
		        <div id="notificationContentContactMech">
		        </div>
		    </div>
			<div class="row-fluid">
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_itemDesiredDeliveryDate" class="required">${uiLabelMap.BSDesiredDeliveryDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_itemDesiredDeliveryDate"></div>
				   		</div>
					</div>
				</div>
				<div class="span6 form-window-content-custom">
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_shipBeforeDate">${uiLabelMap.BSShipBeforeDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_shipBeforeDate"></div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_shipAfterDate">${uiLabelMap.BSShipAfterDate}</label>
						</div>
						<div class='span7'>
							<div id="wn_shipAfterDate"></div>
				   		</div>
					</div>
				</div>
			</div>
			<div class="row-fluid">
				<div class="span12 form-window-content-custom">
					<div id="wnew_jqxGridProduct"></div>
				</div>
			</div>
		</div>
	   	<div class="form-action">
	   		<div class="pull-right form-window-content-custom">
	   			<button id="alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
				<button id="alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
	   		</div>
		</div>
	</div>
</div>

<#assign configwnew_jqxGridProduct = ""/>
<#assign gridProductItemsId = "wnew_jqxGridProduct"/>
<#assign idExisted = "true"/>
<#assign viewSize = "10"/>
<#assign otherParamUrl = "productStoreId=${productStore?if_exists.productStoreId?if_exists}&hasrequest=Y"/>
<#assign displayQuantityReturnPromo = true/>
<#include "component://basesales/webapp/basesales/product/productItemsPopup.ftl"/>

<div style="position:relative">
	<div id="loader_page_common_popup" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 99998;" class="jqx-rc-all jqx-rc-all-olbius">
		<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div style="float: left;">
				<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
				<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.BSLoading}...</span>
			</div>
		</div>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript">
	$(function(){
		pageCommonAddItemsPopup.init();
	});
	var pageCommonAddItemsPopup = (function(){
		$.jqx.theme = 'olbius';
		theme = $.jqx.theme;
		var formatString = 'dd/MM/yyyy HH:mm:ss';
		var dataSelected = [];
		
		var init = function(){
			initElement();
			initEvent();
		};
		var initElement = function(){
			$("#alterpopupWindow").jqxWindow({
				maxWidth: 1000, width: 1000, height: 560, resizable: true, isModal: true, autoOpen: false, cancelButton: $("#alterCancel"), modalOpacity: 0.7, theme: theme
			});
			
			$("#wn_itemDesiredDeliveryDate").jqxDateTimeInput({width: '100%', height: 25, allowNullDate: true, value: null, formatString: formatString, disabled: true});
			$("#wn_shipBeforeDate").jqxDateTimeInput({width: '98%', height: 25, allowNullDate: true, value: null, formatString: formatString, disabled: true});
			$("#wn_shipAfterDate").jqxDateTimeInput({width: '98%', height: 25, allowNullDate: true, value: null, formatString: formatString, disabled: true});
			<#if defaultItemDeliveryDate?exists>$("#wn_itemDesiredDeliveryDate").jqxDateTimeInput('setDate', '${defaultItemDeliveryDate}');</#if>
			<#if defaultShipBeforeDate?exists>$("#wn_shipBeforeDate").jqxDateTimeInput('setDate', '${defaultShipBeforeDate}');</#if>
			<#if defaultShipAfterDate?exists>$("#wn_shipAfterDate").jqxDateTimeInput('setDate', '${defaultShipAfterDate}');</#if>
			
			$("#containerAppendItem").width('100%');
            $("#jqxNotificationAppendItem").jqxNotification({ 
            	icon: { width: 25, height: 25, url: '/aceadmin/assets/images/info.jpg'}, 
            	width: '100%', 
            	appendContainer: "#containerAppendItem", 
            	opacity: 1, autoClose: true, template: "success" 
            });
		};
		var initEvent = function(){
			//createUrl="jqxGeneralServicer?jqaction=C&sname=appendOrderItemSales" addrefresh="true" addrow="true" 
			//addColumns="orderId;shipGroupSeqId;quantity(java.math.BigDecimal);productId;prodCatalogId;shipGroupSeqId;
			//itemDesiredDeliveryDate(java.sql.Timestamp);basePrice(java.math.BigDecimal);overridePrice;reasonEnumId;orderItemTypeId;
			//changeComments;quantityUomId"
			$("#alterSave").on('click', function(){
				var dataMap = {
					orderId: '${orderHeader.orderId}',
				};
				
				checkValidManual();
				if (!checkValidManual) return checkValidManual;
				
				if (dataSelected.length > 0) {
					dataMap.productList = JSON.stringify(dataSelected);
					
					$.ajax({
						type: 'POST',
						url: 'appendOrderItemsLoadToCart',
						data: dataMap,
						beforeSend: function(){
							$("#loader_page_common_popup").show();
						},
						success: function(data){
							<#--if (data.thisRequestUri == "json") {
					    		var successMessage = "";
						        if (data._SUCCESS_MESSAGE_LIST_ != null) {
						        	for (var i = 0; i < data._SUCCESS_MESSAGE_LIST_.length; i++) {
						        		successMessage += "<p>" + data._SUCCESS_MESSAGE_LIST_[i] + "</p>";
						        	}
						        }
						        if (data._SUCCESS_MESSAGE_ != null) {
						        	successMessage += "<p>" + data._SUCCESS_MESSAGE_ + "</p>";
						        }
						        if (successMessage != "") {
						        	$("#container").empty();
				    	        	$("#jqxNotification").jqxNotification({ template: 'info'});
				    	        	$("#jqxNotification").html(successMessage);
				    	        	$("#jqxNotification").jqxNotification("open");
				    	        	$("#alterpopupWindow").jqxWindow("close");
				    	        	window.location.href = 'viewOrder?orderId=' + orderId;
						        	return true;
						        }
					        }-->
							jOlbUtil.processResultDataAjax(data, function(){
								$("#container").empty();
			    	        	$("#jqxNotification").jqxNotification({ template: 'error'});
			    	        	$("#jqxNotification").html(errorMessage);
			    	        	$("#jqxNotification").jqxNotification("open");
							}, function(){
								$('#container').empty();
			    	        	$('#jqxNotification').jqxNotification({ template: 'info'});
			    	        	$("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
			    	        	$("#jqxNotification").jqxNotification("open");
			    	        	window.location.href = 'viewOrder?orderId=' + orderId;
			    	        	return true;
							}, function(data){
								$("#alterpopupWindow").jqxWindow("close");
								$("#windowEditContactMech").jqxWindow("open");
								$("#windowEditContactMechContainer").html(data);
							});
						},
						error: function(data){
							alert("Send request is error");
						},
						complete: function(data){
							$("#loader_page_common_popup").hide();
						},
					});
				} else {
					var messageError = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>${uiLabelMap.BSYouNotYetChooseProduct}!</span>";
					bootbox.dialog(messageError, [{
						"label" : "OK",
						"class" : "btn-mini btn-primary width60px",
					}]);
					return false;
				}
			});
		};
		var checkValidManual = function(){
			var prodCatalogId = "" + $("#wn_prodCatalogId").val();
			var shipGroupSeqId = "" + $("#wn_shipGroupSeqId").val();
			var itemDesiredDeliveryDate = $("#wn_itemDesiredDeliveryDate").jqxDateTimeInput('getDate') != null ? $("#wn_itemDesiredDeliveryDate").jqxDateTimeInput('getDate').getTime() : "";
			var shipBeforeDate = $("#wn_shipBeforeDate").jqxDateTimeInput('getDate') != null ? $("#wn_shipBeforeDate").jqxDateTimeInput('getDate').getTime() : "";
			var shipAfterDate = $("#wn_shipAfterDate").jqxDateTimeInput('getDate') != null ? $("#wn_shipAfterDate").jqxDateTimeInput('getDate').getTime() : "";
			
			dataSelected = [];
			var dataTmp = productOrderMap;
			if (typeof(dataTmp) == 'undefined') {
				alert("Error check data");
			}
			$.each(dataTmp, function (key, value){
				//if (typeof(value) != 'undefined' && (parseInt(value.quantity) > 0 || parseInt(value.quantityReturnPromo) > 0)) {
				if (typeof(value) != 'undefined') {
					var prodItem = {
						prodCatalogId: prodCatalogId,
						shipGroupSeqId: shipGroupSeqId,
						productId: value.productId,
						quantityUomId: typeof(value.quantityUomId) != 'undefined' ? value.quantityUomId : '',
						quantity: typeof(value.quantity) != 'undefined' ? value.quantity : 0,
						quantityReturnPromo: typeof(value.quantityReturnPromo) != 'undefined' ? value.quantityReturnPromo : 0,
						itemDesiredDeliveryDate: itemDesiredDeliveryDate,
						shipBeforeDate: shipBeforeDate,
						shipAfterDate: shipAfterDate,
						amount: '',
						overridePrice: '',
						reasonEnumId: '',
						orderItemTypeId: '',
						changeComments: '',
						itemAttributesMap: '',
						calcTax: '',
						expireDate: '',
					};
					dataSelected.push(prodItem);
					
					/*  row["amount"] = (typeof(data.amount) != "undefined" && data.amount != null) ? data.amount : "";
						row["overridePrice"] = typeof(data.overridePrice) != "undefined" ? data.overridePrice : "";
						row["reasonEnumId"] = typeof(data.reasonEnumId) != "undefined" ? data.reasonEnumId : "";
						row["orderItemTypeId"] = typeof(data.orderItemTypeId) != "undefined" ? data.orderItemTypeId : "";
						row["changeComments"] = typeof(data.changeComments) != "undefined" ? data.changeComments : "";
						row["itemAttributesMap"] = typeof(data.itemAttributesMap) != "undefined" ? data.itemAttributesMap : null;
						row["calcTax"] = typeof(data.calcTax) != "undefined" ? data.calcTax : null;
						row["quantityUomId"] = typeof(data.quantityUomId) != "undefined" ? data.quantityUomId : "";
						row["expireDate"] = typeof(data.expireDate) != "undefined" ? data.expireDate : "";
					*/
				}
			});
			
			if (dataSelected.length > 0) {
				return true;
			} else {
				var messageError = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>${uiLabelMap.BSYouNotYetChooseProduct}!</span>";
				bootbox.dialog(messageError, [{
					"label" : "OK",
					"class" : "btn-mini btn-primary width60px",
				}]);
				return false;
			}
			return true;
		};
		var processResultAppendItems = function(data){
			if (data.thisRequestUri == "json") {
        		var errorMessage = "";
		        if (data._ERROR_MESSAGE_LIST_ != null) {
		        	for (var i = 0; i < data._ERROR_MESSAGE_LIST_.length; i++) {
		        		errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_LIST_[i] + "</p>";
		        	}
		        }
		        if (data._ERROR_MESSAGE_ != null) {
		        	errorMessage += "<p><b>${uiLabelMap.BSErrorUper}</b>: " + data._ERROR_MESSAGE_ + "</p>";
		        }
		        if (errorMessage != "") {
		        	$('#containerAppendItem').empty();
		        	$('#jqxNotificationAppendItem').jqxNotification({ template: 'error'});
		        	$("#jqxNotificationAppendItem").html(errorMessage);
		        	$("#jqxNotificationAppendItem").jqxNotification("open");
		        	return false;
		        } else {
		        	/*
		        	$('#containerAppendItem').empty();
		        	$('#jqxNotificationAppendItem').jqxNotification({ template: 'info'});
		        	$("#jqxNotificationAppendItem").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
		        	$("#jqxNotificationAppendItem").jqxNotification("open");
		        	*/
		        	var orderId = data.orderId;
		        	if (OlbCore.isNotEmpty(orderId)) {
		        		window.location.href = "editSalesOrder?orderId=" + orderId;
		        	}
		        	return true;
		        }
        	} else {
        		return true;
        	}
		};
		return {
			init: init,
		};
	}());
</script>
</#if>
<#--<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_allowSolicitation" class="required">${uiLabelMap.BSProduct}</label>
						</div>
						<div class='span7'>
							<input type="hidden" name="productId" id="productId_w" value=""/>
							<div id="jqxdropdownbuttonProduct">
					       	 	<div id="jqxgridProduct"></div>
					       	</div>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_allowSolicitation" class="required">${uiLabelMap.OrderQuantity}</label>
						</div>
						<div class='span7'>
							<input type="text" size="6" name="quantity" id="quantity_w" value="${requestParameters.quantity?default("1")}" style="min-height: 18px;"/>
				   		</div>
					</div>
					<div class='row-fluid'>
						<div class='span5'>
							<label for="wn_allowSolicitation" class="required">${uiLabelMap.BSUom}</label>
						</div>
						<div class='span7'>
							<div id="jqxgridQuantityUom"></div>
				   		</div>
					</div>-->
<#--
<script type="text/javascript">
	var parentProductIds = {};
	var cellClass = function (row, columnfield, value) {
 		var data = $('#wnew_jqxGridProduct').jqxGrid('getrowdata', row);
 		var returnValue = "";
 		if (typeof(data) != 'undefined') {
 			if (typeof(data.parentProductId) != 'undefined' && typeof(data.colorCode) != 'undefined') {
 				var parentProductId = data.parentProductId;
 				if (parentProductId != null && !(/^\s*$/.test(parentProductId))) {
 					if (typeof(parentProductIds[parentProductId]) == 'undefined') {
 						var newColor = '' + data.colorCode;//''+(0x1000000+(Math.random())*0xffffff).toString(16).substr(1,6);
 						var className = newColor.replace("#", "");
 						parentProductIds[parentProductId] = "background-" + className;
 						$("<style type='text/css'> .background-" + className + "{background-color:" + newColor + " !important} </style>").appendTo("head");
 						returnValue += "background-" + className;
 					} else {
	 					returnValue += parentProductIds[parentProductId];
	 				}
 				}
 			}
 			if (typeof(data.productAvailable) != 'undefined') {
 				if (data.productAvailable == 'true') {
 					returnValue += " row-cell-success";
 				} else if (data.productAvailable == 'false') {
 					returnValue += " row-cell-error";
 				}
 			}
 			//if (typeof(data.isVirtual) != 'undefined' && "Y" == data.isVirtual) {
	        //}
	        return returnValue;
 		}
    }
</script>
-->
<#--
<#assign dataField = "[
				{name: 'productId', type: 'string'},
				{name: 'parentProductId', type: 'string'},
		   		{name: 'productName', type: 'string'},
		   		{name: 'internalName', type: 'string'},
		   		{name: 'quantityUomId', type: 'string'},
		   		{name: 'packingUomIds', type: 'string'},
		   		{name: 'isVirtual', type: 'string'},
		   		{name: 'isVariant', type: 'string'},
		   		{name: 'parentProductId', type: 'string'},
		   		{name: 'features', type: 'string'},
		   		{name: 'colorCode', type: 'string'},
		   		{name: 'quantity', type: 'number', formatter: 'integer'},
		   		{name: 'productAvailable', type: 'string'}
			]"/>
<#assign columnlist = "{text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', dataField: 'productId', width: '13%', editable:false, cellClassName: cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSParentProduct)}', dataField: 'parentProductId', editable: false, width: '13%', cellClassName: cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.DmsProductTaste)}', dataField: 'features', editable: false, width: '10%', cellClassName: cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSInternalName)}', dataField: 'internalName', editable: false, width: '15%', cellClassName: cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', dataField: 'productName', editable: false, cellClassName: cellClass},
				{text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', dataField: 'quantityUomId', width: '12%', columntype: 'dropdownlist', cellClassName: cellClass,
					cellsrenderer: function(row, column, value){
				 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
				 		var resultVal = value;
			   			for (var i = 0 ; i < uomData.length; i++){
							if (resultVal == uomData[i].uomId){
								returnVal += uomData[i].description + '</div>';
		   						return returnVal;
							}
						}
			   			returnVal += value + '</div>';
		   				return returnVal;
					},
				 	initeditor: function (row, cellvalue, editor) {
				 		var packingUomData = new Array();
						var data = $('#wnew_jqxGridProduct').jqxGrid('getrowdata', row);
						
						var itemSelected = data['quantityUomId'];
						var packingUomIdArray = data['packingUomIds'];
						for (var i = 0; i < packingUomIdArray.length; i++) {
							var packingUomIdItem = packingUomIdArray[i];
							var row = {};
							if (packingUomIdItem.description == undefined || packingUomIdItem.description == '') {
								row['description'] = '' + packingUomIdItem.uomId;
							} else {
								row['description'] = '' + packingUomIdItem.description;
							}
							row['uomId'] = '' + packingUomIdItem.uomId;
							packingUomData[i] = row;
						}
				 		var sourceDataPacking = {
			                localdata: packingUomData,
			                datatype: 'array'
			            };
			            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
			            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId'});
			            editor.jqxDropDownList('selectItem', itemSelected);
			      	}
				},
			 	{text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', dataField: 'quantity', cellsalign: 'right', filterable:false, sortable: false, 
			 		cellClassName: cellClass, columntype: 'numberinput', cellsformat: 'd',
			 		cellsrenderer: function(row, column, value){
				 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
			   			returnVal += formatnumber(value) + '</div>';
		   				return returnVal;
				 	},
					validation: function (cell, value) {
						if (value < 0) {
							return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
						}
						return true;
					},
					createeditor: function (row, cellvalue, editor) {
						editor.jqxNumberInput({decimalDigits: 0, digits: 9});
					}
			 	}
			"/>
<@jqGrid id="wnew_jqxGridProduct" idExisted="true" clearfilteringbutton="false" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField 
		viewSize="10" showtoolbar="false" editmode="click" selectionmode="checkbox" width="100%" bindresize="false" groupable="true" 
		url="jqxGeneralServicer?sname=JQGetListProductByStoreOrCatalog&productStoreId=${productStore?if_exists.productStoreId?if_exists}&hasrequest=Y" 
	/>
-->
<#--
			var selectedRowIndexes = $('#wnew_jqxGridProduct').jqxGrid('selectedrowindexes');
			if (selectedRowIndexes.length <= 0) {
				var messageError = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>${uiLabelMap.BSYouNotYetChooseProduct}!</span>";
				bootbox.dialog(messageError, [{
					"label" : "OK",
					"class" : "btn-mini btn-primary width60px",
					}]
				);
				return false;
			}
			var message = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> ";
			message += "<span class='message-content-alert-danger'>${uiLabelMap.BSExistProductDoNotHaveQuantityIs}: ";
			var hasMessage = false;
			var isFirst = true;
			dataSelected = [];
			for(var i = 0; i < selectedRowIndexes.length; i++) {
				var index = selectedRowIndexes[i];
				var data = $('#wnew_jqxGridProduct').jqxGrid('getrowdata', index);
				if (data) {
					var row = {};
					row["productId"] = data.productId;
					var quantity = data.quantity;
					if (!OlbCore.isNotEmpty(quantity)) {
						hasMessage = true;
						if (!isFirst) message += ", ";
						message += data.productId;
						isFirst = false;
					} else {
						row["prodCatalogId"] = prodCatalogId;
						row["shipGroupSeqId"] = shipGroupSeqId;
						row["quantity"] = quantity;
						row["amount"] = (typeof(data.amount) != "undefined" && data.amount != null) ? data.amount : "";
						row["overridePrice"] = typeof(data.overridePrice) != "undefined" ? data.overridePrice : "";
						row["reasonEnumId"] = typeof(data.reasonEnumId) != "undefined" ? data.reasonEnumId : "";
						row["orderItemTypeId"] = typeof(data.orderItemTypeId) != "undefined" ? data.orderItemTypeId : "";
						row["changeComments"] = typeof(data.changeComments) != "undefined" ? data.changeComments : "";
						row["itemDesiredDeliveryDate"] = itemDesiredDeliveryDate;
						row["shipBeforeDate"] = shipBeforeDate;
						row["shipAfterDate"] = shipAfterDate;
						row["itemAttributesMap"] = typeof(data.itemAttributesMap) != "undefined" ? data.itemAttributesMap : null;
						row["calcTax"] = typeof(data.calcTax) != "undefined" ? data.calcTax : null;
						row["quantityUomId"] = typeof(data.quantityUomId) != "undefined" ? data.quantityUomId : "";
						row["expireDate"] = typeof(data.expireDate) != "undefined" ? data.expireDate : "";
						dataSelected.push(row);
					}
				}
			}
			message += "</span>";
			if (hasMessage) {
				bootbox.dialog(message, [{
					"label" : "OK",
					"class" : "btn-mini btn-primary width60px",
					}]
				);
				//.addClass("alert alert-danger");
				return false;
			}
			-->