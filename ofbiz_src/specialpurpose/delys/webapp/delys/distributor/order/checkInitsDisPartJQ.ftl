<#--
Copy from checkInitsSalesJQ.ftl file
-->
<script type="text/javascript">
	var productQuantities = new Array();
	var rowChangeArr = new Array();
	<#if session?exists>
		<#assign cart = session.getAttribute("shoppingCart")!/>
	</#if>
	<#if cart?exists && cart?has_content>
		<#assign orderItems = cart.makeOrderItems()>
		<#list orderItems as orderItem>
			<#if (orderItem.productId?exists) && (!(orderItem.isPromo?exists) || orderItem.isPromo?string == "N")>>
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
</script>
<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>
<script type="text/javascript">
	var uomData = new Array();
	<#list uomList as uomItem>
		<#assign description = StringUtil.wrapString(uomItem.get("description", locale)) />
		var row = {};
		row['uomId'] = '${uomItem.uomId}';
		row['description'] = "${description}";
		uomData[${uomItem_index}] = row;
	</#list>
</script>
<#assign dataField="[{ name: 'productId', type: 'string' },
               		{ name: 'productName', type: 'string' },
               		{ name: 'quantityUomId', type: 'string'},
               		{ name: 'productPackingUomId', type: 'string'},
               		{ name: 'quantity', type: 'number', formatter: 'integer'},
               		{ name: 'packingUomId', type: 'string'},
               		{ name: 'expireDate', type: 'string'}, 
               		{ name: 'expireDateList', type: 'string'},
               		{ name: 'atpTotal', type: 'string'},
               		{ name: 'qohTotal', type: 'string'}
                	]"/>
<#assign columnlist="{ text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '180px', editable:false},
					 { text: '${uiLabelMap.DAProductName}', dataField: 'productName', editable:false},
					 { text: '${uiLabelMap.DAUom}', dataField: 'quantityUomId', width: '120px', columntype: 'dropdownlist',
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
                     { text: '${uiLabelMap.DAExpireDate}', dataField: 'expireDate', width: '180px', columntype: 'dropdownlist', filterable:false, sortable:false, 
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
                 	},
                 	{ text: '${uiLabelMap.DAQOHTotal}', dataField: 'qohTotal', width: '100px', editable:false, filterable:false, sortable:false},
                 	{ text: '${uiLabelMap.DAATPTotal}', dataField: 'atpTotal', width: '100px', editable:false, filterable:false, sortable:false},
				 	{ text: '${uiLabelMap.DAQuantity}', dataField: 'quantity', cellsalign: 'right', filterable:false, sortable:false, 
					 	cellsrenderer: function(row, column, value){
					 		var data = $('#jqxgridSO').jqxGrid('getrowdata', row);
    						var indexFinded = rowChangeArr.indexOf(data.productId);
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
<#--
<@jqGrid id="jqxgridSO" defaultSortColumn="productId" clearfilteringbutton="true" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="10" showtoolbar="true" editmode="click" selectionmode="multiplecellsadvanced" 
		url="jqxGeneralServicer?sname=JQGetListProductByCategoryCatalog&catalogId=${currentCatalogId?if_exists}"/>
-->
<@jqGrid id="jqxgridSO" clearfilteringbutton="true" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
		viewSize="15" showtoolbar="false" editmode="click" selectionmode="multiplecellsadvanced" 
		url="jqxGeneralServicer?sname=JQGetListProductByCategoryCatalogLM&catalogId=${currentCatalogId?if_exists}&productStoreId=${currentStoreId?if_exists}&hasrequest=Y"/>	

<script src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript">
	$(".chzn-select").chosen({allow_single_deselect:true , no_results_text: "${uiLabelMap.DANoSuchState}"});
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
		   		var oldValue = args.oldvalue;
		   		var newValue = args.value;
		   		var indexFinded = rowChangeArr.indexOf(productId);
		   		if (indexFinded > -1) {
		   			var objSelected = productQuantities[indexFinded];
		   			if (productId == objSelected["productId"] && oldValue == objSelected["quantity"]) {
		   				objSelected["quantity"] = newValue;
		   			} else {
		   				for (var i = 0; i < productQuantities.length; i++) {
		   					var objItem = productQuantities[i];
		   					if (productId == objItem["productId"]) {
		   						objItem["quantity"] = newValue;
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
			   			var expireDate = data.expireDate;
			   			if (expireDate != undefined) {
			   				objNew["expireDate"] = expireDate;
			   			}
			   			productQuantities.push(objNew);
			   			rowChangeArr.push(productId);
		   			}
		   		}
	    	}
    	} else if (args.datafield == "quantityUomId") {
    		var rowBoundIndex = args.rowindex;
	    	var data = $("#jqxgridSO").jqxGrid("getrowdata", rowBoundIndex);
	    	if (data && data.productId) {
	    		var productId = data.productId;
	    		var quantityUomId = data.quantityUomId;
		   		var oldValue = args.oldvalue;
		   		var newValue = args.value;
		   		var indexFinded = rowChangeArr.indexOf(productId);
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
		    	var indexFinded = rowChangeArr.indexOf(productId);
		   		if (indexFinded > -1) {
		   			var objSelected = productQuantities[indexFinded];
		   			if (productId == objSelected["productId"] && oldValue == objSelected["expireDate"]) {
		   				objSelected["expireDate"] = newValue;
		   			} else {
		   				for (var i = 0; i < productQuantities.length; i++) {
		   					var objItem = productQuantities[i];
		   					if (productId == objItem["productId"]) {
		   						objItem["expireDate"] = newValue;
		   						break;
		   					}
		   				}
		   			}
	   			}
	    	}
    	}
	});
</script>
