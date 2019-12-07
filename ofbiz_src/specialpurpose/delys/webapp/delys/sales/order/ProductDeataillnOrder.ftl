<script  type="text/javascript">
	var orderId = '${parameters.orderId}';
	
	<#assign productList = delegator.findList("Product", null, null, null, null, false) />
	var productData = new Array();
	<#list productList as product>
		var row = {};
		row['productId'] = "${product.productId}";
		row['description'] = "${StringUtil.wrapString(product.get('description', locale)?if_exists)}";
		productData[${product_index}] = row;
	</#list>
	function getDescriptionByProductId(productId) {
		for ( var x in productData) {
			if (productId == productData[x].productId) {
				return productData[x].description;
			}
		}
	}
	
	<#assign uomList = delegator.findList("Uom", null, null, null, null, false) />
	var uomData = new Array();
	<#list uomList as uom>
		var row = {};
		row['uomId'] = "${uom.uomId}";
		row['description'] = "${StringUtil.wrapString(uom.get('description', locale)?if_exists)}";
		uomData[${uom_index}] = row;
	</#list>
	
	function getDescriptionByUomId(uomId) {
		for ( var x in uomData) {
			if (uomId == uomData[x].uomId) {
				return uomData[x].description;
			}
		}
	}
	
</script>	

<style>
	div.buttonHidden{
	    visibility: hidden;
	}
</style>
<#assign dataField="[
	{ name: 'productId', type: 'string'},
	{ name: 'expireDate', type: 'date', other: 'Timestamp'},
	{ name: 'alternativeQuantity', type: 'string'},
	{ name: 'quantityUomId', type: 'string'},
]"/>
<#assign columnlist="
	{ text: '${StringUtil.wrapString(uiLabelMap.accProductName)}', datafield: 'productId', editable:false,
		cellsrenderer: function(row, colum, value){
			var data = $('#jqxgrid').jqxGrid('getrowdata', row);
			var value = data.productId;
			if(value){
				var productId = getDescriptionByProductId(value);
			    return '<span>' + productId + '</span>';
			}
	    },
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.ExpirationDate)}', datafield: 'expireDate', filtertype: 'range', cellsformat: 'dd/MM/yyyy', editable:false, cellsalign: 'right',},
	{ text: '${StringUtil.wrapString(uiLabelMap.quantity)}', datafield: 'alternativeQuantity', editable:false,
		cellsrenderer: function(row, colum, value){
			if(value){
				return '<span style=\"text-align:right\">' + value.toLocaleString('${locale}') + '</span>';
			}
	    }, 
	},
	{ text: '${StringUtil.wrapString(uiLabelMap.QuantityUomId)}', datafield: 'quantityUomId', editable:false,
		cellsrenderer: function(row, colum, value){
			if(value){
				var quantityUomId = getDescriptionByUomId(value);
			    return '<span style=\"text-align:right\">' + quantityUomId + '</span>';
			}
	    },  
	},
"/>

<@jqGrid filtersimplemode="true" id="jqxgrid" filterable="true"  dataField=dataField columnlist=columnlist clearfilteringbutton="true" showtoolbar="true" editmode="click" 
	url="jqxGeneralServicer?sname=JQXGetProductListInOrder&orderId=${parameters.orderId}"
/>
<div class='row-fluid'>
	<div class="span12 margin-top10">
		<button id="buttonCancel" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		<button id="buttonSave" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
	</div>
</div>
<script>
	$('#document').ready(function(){
	});
	
	$('#buttonCancel').click(function(){
		window.location.href = "orderList";
	});

	$('#buttonSave').click(function(event){
		var rows = $('#jqxgridDeliveryItem').jqxGrid('getrows');
		if (rows.length > 0){
			var selectedRows = $('#jqxgridDeliveryItem').jqxGrid('getselectedrowindexes');
			if (selectedRows.length > 0){
				bootbox.confirm("${uiLabelMap.DAAreYouSureSave}",function(result){ 
					if(result){
						for (var i = 0; i < selectedRows.length; i ++){
							var data = $('#jqxgridDeliveryItem').jqxGrid('getrowdata', selectedRows[i]);
							var exp = data.actualExpireDate;
							var fac = data.facilityId;
							if (!fac){
								fac = facilityData[0].facilityId;
							}
							addNewProductByInventoryItem(data.fromOrderId, data.deliveryId, data.productId, exp.getTime(), data.actualExportedQuantity, data.quantityUomId, fac);
						}
						window.location.href = "orderList";
					}
				});
			} else {
				bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                }]
	            );
			}
		} else {
			bootbox.dialog("${uiLabelMap.MustUploadScanFile}!", [{
                "label" : "${uiLabelMap.CommonOk}",
                "class" : "btn btn-primary standard-bootbox-bt",
                "icon" : "fa fa-check",
                }]
            );
		}
	});
	
	function addNewProductByInventoryItem(orderId, deliveryId, productId, expireDate, quantity, uomId, facilityId){
		jQuery.ajax({
			url: "addNewProductByInventoryItem",
			type: "POST",
			data: {
				orderId: orderId, 
				deliveryId: deliveryId, 
				productId: productId, 
				expireDate: expireDate, 
				quantity: quantity, 
				uomId: uomId, 
				facilityId: facilityId,
			},
			dataType: "json",
			async: false,
			success: function(data) {
			}
		});
	}
	function rowselectfunction(event){
	    if(typeof event.args.rowindex != 'number'){
	        var tmpArray = event.args.rowindex;
	        for(i = 0; i < tmpArray.length; i++){
	            if(checkRequiredData2(tmpArray[i])){
	                $('#jqxgridDeliveryItem').jqxGrid('clearselection');
	                break; // Stop for first item
	            }
	        }
	    }else{
	        if(checkRequiredData2(event.args.rowindex)){
	            $('#jqxgridDeliveryItem').jqxGrid('unselectrow', event.args.rowindex);
	        }
	    }
	}
	
	function checkRequiredData2(rowindex){
	    var data = $('#jqxgridDeliveryItem').jqxGrid('getrowdata', rowindex);
	    if(data.statusId == 'DELI_ITEM_EXPORTED'){
	        if(data.actualDeliveredQuantity == 0){
	            $('#jqxgridDeliveryItem').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.DLYItemMissingFieldsDlv}", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#jqxgridDeliveryItem").jqxGrid('begincelledit', rowindex, "actualDeliveredQuantity");
	                    }
	                }]
	            );
	            return true;
	        }
	        if(data.actualDeliveredQuantity > data.actualExportedQuantity){
	            $('#jqxgridDeliveryItem').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.LogCheckActuallyExportedGreaterRealCommunication}", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#jqxgridDeliveryItem").jqxGrid('begincelledit', rowindex, "actualDeliveredQuantity");
	                    }
	                }]
	            );
	            return true;
	        }
	        if(!data.actualDeliveredExpireDate){
	            $('#jqxgridDeliveryItem').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.ExpireDateMissing}", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#jqxgridDeliveryItem").jqxGrid('begincelledit', rowindex, "actualDeliveredExpireDate");
	                    }
	                }]
	            );
	            return true;
	        }
	        if(!data.actualDeliveredExpireDate){
	            $('#jqxgridDeliveryItem').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.ExpireDateMissing}", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#jqxgridDeliveryItem").jqxGrid('begincelledit', rowindex, "actualDeliveredExpireDate");
	                    }
	                }]
	            );
	            return true;
	        }
	        if(!data.facilityId){
	            $('#jqxgridDeliveryItem').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.FacilityMissing}", [{
	                "label" : "${uiLabelMap.CommonOk}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                        $("#jqxgridDeliveryItem").jqxGrid('begincelledit', rowindex, "facilityId	");
	                    }
	                }]
	            );
	            return true;
	        }
	    }
	    return false;
	}
</script>	