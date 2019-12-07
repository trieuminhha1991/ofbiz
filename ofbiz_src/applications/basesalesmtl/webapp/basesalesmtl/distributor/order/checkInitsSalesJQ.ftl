<style type="text/css">
	.form-size-mini .controls .field-lookup input[type="text"] {
		width: 171px;
  		height: 25px;
	}
	.form-size-mini .controls .field-lookup a:before {
		height: 22px;
	  	margin-top: 1px;
	  	font-size: 12px;
	  	width: 18px;
	}
	.btn.btn-prev#btnPrevWizard {
		background-color: #87b87f!important;
  		border-color: #87b87f;
	}
	.btn.btn-prev#btnPrevWizard:hover {
		background-color: #629b58!important;
  		border-color: #87b87f;
	}
	.btn.btn-prev#btnPrevWizard:disabled {
		background-color: #abbac3!important;
	  	border-color: #abbac3;
	}
	#jqxgridSOContainer {
		height : auto !important;
	}
</style>
<script type="text/javascript">
	var productQuantities = new Array();
	var rowChangeArr = new Array();
	<#if session?exists>
		<#assign cart = session.getAttribute("shoppingCart")!/>
	</#if>
	<#if cart?exists && cart?has_content>
		<#assign orderItems = cart.makeOrderItems()>
		<#list orderItems as orderItem>
			<#if (orderItem.productId?exists) && (!(orderItem.isPromo?exists) || orderItem.isPromo?string == "N")>
				var objNew = {};
	   			objNew["productId"] = "${orderItem.productId}";
	   			<#if orderItem.quantityUomId?exists>
	   				objNew["quantityUomId"] = "${orderItem.quantityUomId}";
	   			</#if>
	   			<#if orderItem.alternativeQuantity?exists>
	   				objNew["quantity"] = "${orderItem.alternativeQuantity}";
	   			</#if>
	   			<#if orderItem.expireDate?exists>
	   				objNew["expireDate"] = "${orderItem.expireDate}";
	   			</#if>
	   			productQuantities.push(objNew);
				rowChangeArr.push("${orderItem.productId}");
			</#if>
		</#list>
	</#if>
	
	<#if currencies?exists>
		var localDataCurrency = [
			<#list currencies as dataItem>
				<#assign description = StringUtil.wrapString(dataItem.get("description", locale)) />
				{uomId: "${dataItem.uomId}",
				description: "${description}"},
			</#list>
		];
	<#else>
		var localDataCurrency = [];
	</#if>
</script>

<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>
<script type="text/javascript">
	var uomData = new Array();
	<#list uomList as uomItem>
		<#assign description = StringUtil.wrapString(uomItem.get("description", locale)) />
		var row = {};
		row['uomId'] = '${uomItem.uomId}';
		row['description'] = "${description?default('')}";
		uomData[${uomItem_index}] = row;
	</#list>
</script>
<div class="row-fluid">
	<div class="span6">
		<form class="form-horizontal basic-custom-form form-size-mini" id="initOrderEntry" name="initOrderEntry" method="post" action="<@ofbizUrl>initOrderEntrySales</@ofbizUrl>" style="display: block;">
			<input type="hidden" name="salesChannelEnumId" value="WEB_SALES_CHANNEL"/>
			<input type="hidden" name="originOrderId" value="${parameters.originOrderId?if_exists}"/>
		  	<input type="hidden" name="finalizeMode" value="type"/>
		  	<input type="hidden" name="orderMode" value="SALES_ORDER"/>
		  	<input type="hidden" name="CURRENT_CATALOG_ID" value="${currentCatalogId?if_exists}"/>
		  	<input type="hidden" name="catalogId" value="${currentCatalogId?if_exists}"/>
			<div class="control-group">
				<label class="control-label" for="orderId">${uiLabelMap.DAOrderId}</label>
				<div class="controls">
					<div class="span12">
						<input type="text" name="orderId" id="orderId" class="span12 input-small" style="width:210px" maxlength="20" value="<#if cart?exists && cart?has_content && cart.getOrderId()?exists>${(cart.getOrderId())?if_exists}<#else>${parameters.orderId?if_exists}</#if>">
					</div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label required" for="partyId">${uiLabelMap.DACustomer}</label>
				<div class="controls">
					<div class="span12">
						<input type="text" name="partyId" id="partyId" class="span12 input-small disabled" style="width:208px" readonly="true" maxlength="20" value="${userLogin.partyId?if_exists}"/>
					</div>
				</div>
			</div>
	  		<div class="control-group">
				<label class="control-label required" for="desiredDeliveryDate2">${uiLabelMap.DADesiredDeliveryDate}</label>
				<div class="controls">
					<div class="span12">
						<div id="desiredDeliveryDate2"></div>
					</div>
				</div>
			</div>
			<div class="control-group">
				<label class="control-label" for="orderName">${uiLabelMap.DAOrderName}</label>
				<div class="controls">
					<div class="span12">
						<input type="text" name="orderName" id="orderName" class="span12" maxlength="100" value="<#if cart?exists && cart?has_content && cart.getOrderName()?exists>${cart.getOrderName()?if_exists}<#else>${parameters.orderName?if_exists}</#if>">
					</div>
				</div>
			</div>
			<input type="hidden" name="salesMethodChannelEnumId" id="salesMethodChannelEnumId" value="SALES_GT_CHANNEL"/>
			<div class="control-group">
				<label class="control-label" for="productStoreId">${uiLabelMap.DAProductStore}</label>
				<div class="controls">
					<div class="span12">
						<input type="hidden" name="productStoreId" id="productStoreId" class="span12 input-small" maxlength="20" value="TELESALES_01"/>
						<input type="text" class="span12 input-small disabled" readonly="true" maxlength="20" value="${(productStores[0].storeName)?if_exists}" style="display:inline-block; width: calc(100% - 167px); margin-bottom: 11px;"/>
						<div style="display:inline-block; width: 163px;">
							<label class="control-label required" for="currencyUomId" style="width: 60px !important; color:#393939; display:inline-block; margin-top: -10px !important">
								${uiLabelMap.DAAbbCurrency}
							</label>
							<div class="controls" style="display:inline-block;margin-left: 17px !important;margin-top: 0 !important;vertical-align: bottom;">
								<div class="span12" style="text-align:right">
									<div id="currencyUomId" name="currencyUomId"></div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</form>
	</div><!-- .span6 -->
	<div class="span6">
			<div style="position:relative">
				<div id="checkoutInfoLoader" style="overflow: hidden; position: absolute; display: none; left: 45%; top: 25%; " class="jqx-rc-all jqx-rc-all-olbius">
					<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
						<div style="float: left;">
							<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
							<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
						</div>
					</div>
				</div>
				<div id="checkoutInfo">
					${setContextField("currentPartyId", userLogin.partyId?default(""))}
					${screens.render("component://basesalesmtl/widget/DistributorScreens.xml#QuickCheckoutAjaxSales")}
				</div>
			</div>
	</div><!-- .span6 -->
</div><!-- .row-fluid -->

<br />
<#assign dataField="[{ name: 'productId', type: 'string' },
               		{ name: 'productName', type: 'string' },
               		{ name: 'quantityUomId', type: 'string'},
               		{ name: 'productPackingUomId', type: 'string'},
               		{ name: 'quantity', type: 'number', formatter: 'integer'},
               		{ name: 'packingUomId', type: 'string'},
               		{ name: 'expireDate', type: 'string'}, 
               		{ name: 'expireDateList', type: 'string'},
               		{ name: 'atpTotal', type: 'string'},
               		{ name: 'qohTotal', type: 'string'}]"/>

<#assign columnlist="{ text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '14%', editable:false},
					 { text: '${uiLabelMap.DAProductName}', dataField: 'productName', editable:false},
					 { text: '${uiLabelMap.DAUom}', dataField: 'quantityUomId', width: '12%', columntype: 'dropdownlist', 
					 	cellsrenderer: function(row, column, value){
    						for (var i = 0 ; i < uomData.length; i++){
    							if (value == uomData[i].uomId){
    								return '<span title = ' + uomData[i].description +'>' + uomData[i].description + '</span>';
    							}
    						}
    						return '<span title=' + value +'>' + value + '</span>';
						},
					 	initeditor: function (row, cellvalue, editor) {
					 		var packingUomData = new Array();
							var data = $('#jqxgridSO').jqxGrid('getrowdata', row);
							
							var itemSelected = data['quantityUomId'];
							var packingUomIdArray = data['packingUomId'];
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
					 		var sourceDataPacking =
				            {
				                localdata: packingUomData,
				                datatype: \"array\"
				            };
				            var dataAdapterPacking = new $.jqx.dataAdapter(sourceDataPacking);
				            editor.jqxDropDownList({ source: dataAdapterPacking, displayMember: 'description', valueMember: 'uomId'});
				            editor.jqxDropDownList('selectItem', itemSelected);
                      	}
                    },
				 	{ text: '${uiLabelMap.DAQuantity}', dataField: 'quantity', width: '14%', cellsalign: 'right', filterable:false, sortable:false,columntype : 'numberinput',
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgridSO').jqxGrid('getrowdata', row);
					 		var returnVal = '<div class=\"align-right\" style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: left; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
					 		if (data != undefined && data != null) {
	    						var indexFinded = rowChangeArr.indexOf(data.productId);
	    						var productId = data.productId;
	    						if (indexFinded > -1) {
						   			var objSelected = productQuantities[indexFinded];
						   			if (productId == objSelected.productId) {
						   				data.quantity = productQuantities[indexFinded].quantity;
						   				returnVal += formatnumber(productQuantities[indexFinded].quantity,'USD') + '</div>';
						   				return returnVal;
						   			} else {
						   				for(i = 0 ; i < productQuantities.length; i++){
			    							if (productId == productQuantities[i].productId){
			    								data.quantity = productQuantities[i].quantity;
			    								returnVal += formatnumber(productQuantities[i].quantity,'USD') + '</div>';
						   						return returnVal;
			    							}
			    						}
						   			}
					   			}
				   			}
				   			returnVal += formatnumber(value,'USD') + '</div>';
			   				return returnVal;
					 	},validation : function(cell, value){
					 		if((value<0) || (value % 1 != 0)){
					 			 return { result: false, message: '${uiLabelMap.DAValueCantbeNegativeAndMustbeInteger}' };
					 		}
					 		return true;
					 	},cellBeginEdit : function(row, datafield, columntype){
					    	if (datafield == 'expireDate') {
						    	var data = $('#jqxgridSO').jqxGrid('getrowdata', row);
						    	var valueSelected = data.expireDate;
						    	var expireDateList = data.expireDateList;
						    	if (valueSelected != null && expireDateList != null) {
						    		for (var i = 0; i < expireDateList.length; i++) {
						    			var row = expireDateList[i];
						    			if (valueSelected != null && valueSelected == row.expireDate) {
											$('#jqxgridSO').jqxGrid('setcellvalue', row, 'qohTotal', row.qohTotal, 'atpTotal', row.atpTotal);
						    			}
						    		}
						    	}
					    	}
					 	},cellEndEdit : function(row, datafield, columntype, oldvalue, newvalue){
					 		if(datafield == 'quantity'){
						    	var data = $('#jqxgridSO').jqxGrid('getrowdata', row);
						    	if (data && data.productId) {
						    		var productId = data.productId;
						    		var quantityUomId = data.quantityUomId;
							   		var oldValue = oldvalue;
							   		var newValue = newvalue;
							   		if (isNaN(newValue)) {
							   			$('#jqxgridSO').jqxGrid('setcellvalue', row, 'quantity', oldValue);
							   			return false;
							   		}
							   		var indexFinded = rowChangeArr.indexOf(productId);
							   		if (indexFinded > -1) {
							   			var objSelected = productQuantities[indexFinded];
							   			if (productId == objSelected['productId'] && oldValue == objSelected['quantity']) {
							   				objSelected['quantity'] = newValue;
							   			} else {
							   				for (var i = 0; i < productQuantities.length; i++) {
							   					var objItem = productQuantities[i];
							   					if (productId == objItem['productId']) {
							   						objItem['quantity'] = newValue;
							   						break;
							   					}
							   				}
							   			}
							   		} else {
							   			if (newValue && newValue != null) {
							   				var objNew = {};
								   			objNew['productId'] = productId;
								   			objNew['quantityUomId'] = quantityUomId;
								   			objNew['quantity'] = newValue;
								   			var expireDate = data.expireDate;
								   			if (expireDate != undefined) {
								   				objNew['expireDate'] = expireDate;
								   			}
								   			productQuantities.push(objNew);
								   			rowChangeArr.push(productId);
							   			}
							   		}
						    	}
					 		}
					 		else if(datafield == 'quantityUomId'){
						    	var data = $('#jqxgridSO').jqxGrid('getrowdata', row);
						    	if (data && data.productId) {
						    		var productId = data.productId;
						    		var quantityUomId = data.quantityUomId;
							   		var oldValue = oldvalue;
							   		var newValue = newvalue;
							   		var indexFinded = rowChangeArr.indexOf(productId);
							   		if (indexFinded > -1) {
							   			var objSelected = productQuantities[indexFinded];
							   			if (productId == objSelected['productId'] && oldValue == objSelected['quantityUomId']) {
							   				objSelected['quantityUomId'] = newValue;
							   			} else {
							   				for (var i = 0; i < productQuantities.length; i++) {
							   					var objItem = productQuantities[i];
							   					if (productId == objItem['productId']) {
							   						objItem['quantityUomId'] = newValue;
							   						break;
							   					}
							   				}
							   			}
						   			}
						    	}
					 		}
					 		else if(datafield == 'expireDate'){
						    	var data = $('#jqxgridSO').jqxGrid('getrowdata', row);
						    	if (data && data.productId) {
						    		var oldValue = oldvalue;
							   		var newValue = newvalue;
						    		var valueSelected = args.value; //newValue
						    		var expireDateList = data.expireDateList;
						    		if (valueSelected != undefined && expireDateList != undefined) {
							    		for (var i = 0; i < expireDateList.length; i++) {
							    			var row = expireDateList[i];
							    			if (valueSelected != null && valueSelected == row.expireDate) {
												$('#jqxgridSO').jqxGrid('setcellvalue', row, 'qohTotal', row.qohTotal);
												$('#jqxgridSO').jqxGrid('setcellvalue', row, 'atpTotal', row.atpTotal);
							    			}
							    		}
							    	}
							    	var productId = data.productId;
							    	var indexFinded = rowChangeArr.indexOf(productId);
							   		if (indexFinded > -1) {
							   			var objSelected = productQuantities[indexFinded];
							   			if (productId == objSelected['productId'] && oldValue == objSelected['expireDate']) {
							   				objSelected['expireDate'] = newValue;
							   			} else {
							   				for (var i = 0; i < productQuantities.length; i++) {
							   					var objItem = productQuantities[i];
							   					if (productId == objItem['productId']) {
							   						objItem['expireDate'] = newValue;
							   						break;
							   					}
							   				}
							   			}
						   			}
						    	}
					 		}
					 	},createeditor : function(row, cellvalue, editor){
					 		editor.jqxNumberInput({decimalDigits : 0,digits : 9});
					 	}
					}"/>
<@jqGrid id="jqxgridSO" clearfilteringbutton="false" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="15" showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" 
		url="jqxGeneralServicer?sname=JQGetListProductByCategoryCatalogLM&catalogId=&productStoreId=TELESALES_01&hasrequest=Y" 
		mouseRightMenu="true" contextMenuId="contextMenu" />
<div style="position:relative">
	<div id="info_loader" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 900;" class="jqx-rc-all jqx-rc-all-olbius">
		<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
			<div style="float: left;">
				<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
				<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
			</div>
		</div>
	</div>
</div>

<div style="clear:both"></div>

<div id='contextMenu' style="display:none">
	<ul>
	    <li><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.DARefresh)}</li>
	</ul>
</div>
<script type="text/javascript">
	//Create Theme
	$.jqx.theme = 'olbius';  
	theme = $.jqx.theme;
	
	$("#contextMenu").jqxMenu({ width: 200, autoOpenPopup: false, mode: 'popup', theme: theme});
	$("#contextMenu").on('itemclick', function (event) {
		var args = event.args;
        var rowindex = $("#jqxgridSO").jqxGrid('getselectedrowindex');
        var tmpKey = $.trim($(args).text());
        if (tmpKey == "${StringUtil.wrapString(uiLabelMap.DARefresh)}") {
        	$("#jqxgridSO").jqxGrid('updatebounddata');
        }
    });
</script>
<script src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript">
	$(".chzn-select").chosen({allow_single_deselect:true , no_results_text: "${uiLabelMap.DANoSuchState}"});
</script>
<script type="text/javascript">
	$("#orderId").jqxInput({height: 25, maxLength:20});
	$("#desiredDeliveryDate2").jqxDateTimeInput({width: '208px', height: '25px', allowNullDate: true, value: null, formatString: 'dd/MM/yyyy HH:mm:ss'});
	<#if cart?exists && cart?has_content && cart.getDefaultItemDeliveryDate()?exists>
		$('#desiredDeliveryDate2').jqxDateTimeInput('setDate', "${StringUtil.wrapString(cart.getDefaultItemDeliveryDate())}");
	</#if>
	
	var sourceCurrency = {
		localdata: localDataCurrency,
        datatype: "array",
        datafields: [
            { name: 'uomId' },
            { name: 'description' }
        ]
    };
    var dataAdapterCurrency = new $.jqx.dataAdapter(sourceCurrency, {
            formatData: function (data) {
                if ($("#currencyUomId").jqxComboBox('searchString') != undefined) {
                    data.searchKey = $("#currencyUomId").jqxComboBox('searchString');
                    return data;
                }
            }
        }
    );
    $("#currencyUomId").jqxComboBox({
        width: 80,
        placeHolder: " ${StringUtil.wrapString(uiLabelMap.DAChooseACurrency)}",
        dropDownWidth: 280,
        height: 25,
        source: dataAdapterCurrency,
        remoteAutoComplete: false,
        autoDropDownHeight: false,          
        displayMember: "uomId",
        valueMember: "uomId",
        renderer: function (index, label, value) {
            var item = dataAdapterCurrency.records[index];
            if (item != null) {
                var label = item.uomId;
                return label;
            }
            return "";
        },
        renderSelectedItem: function(index, item)
        {
            var item = dataAdapterCurrency.records[index];
            if (item != null) {
                var label = item.uomId;
                return label;
            }
            return "";
        },
        search: function (searchString) {
            dataAdapterCurrency.dataBind();
        }
    });
    $("#currencyUomId").jqxComboBox('selectItem', 'VND');
</script>