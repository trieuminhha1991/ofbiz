<script src="/salesmtlresources/js/facility/receiveProduct.js"></script>

<div id="jqxwindowReceiveProducts" class="hide">
<div>${uiLabelMap.DmsListProducts}</div>
<div style="overflow: hidden;">
	
	<div class="margin-top10 margin-bottom10 pull-right">
		<div class="pull-right"><div id="txtLookupFacility"></div></div>
		<div class="pull-right"><label class="text-right asterisk" style="margin: 5px 10px;">${uiLabelMap.PageTitleLookupFacility}</label></div>
	</div>

	<div>
		<div id="jqxgridReceiveProducts"></div>
	</div>
	<div class="form-action">
		<button id="alterCancelReceive" class='btn btn-danger form-action-button pull-right'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
		<button id="alterSaveReceive" class='btn btn-primary form-action-button pull-right'><i class='fa-check'></i> ${uiLabelMap.ReceiveProduct}</button>
	</div>
</div>
</div>

<script>
	var ownerPartyId = "${(userLogin.partyId)?if_exists}";
	function checkRequiredPurchaseLabelItem(rowindex, gridId){
		var data = $('#jqxgridReceiveProducts').jqxGrid('getrowdata', rowindex);
		if(data){
	        if(data.unitListPrice == undefined ){
				$('#jqxgridReceiveProducts').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.UnitCostNotEntered}", [{
	                "label" : "${uiLabelMap.OK}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                		$('#jqxgridReceiveProducts').jqxGrid('begincelledit', rowindex, "unitCost");
	                    }
	                }]
	            );
	        } else if (!data.datetimeManufactured){
	        	$('#jqxgridReceiveProducts').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.ManufacturedDateNotEnter}", [{
	                "label" : "${uiLabelMap.OK}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                		$('#jqxgridReceiveProducts').jqxGrid('begincelledit', rowindex, "datetimeManufactured");
	                    }
	                }]
	            );
	            return true;
	        } else if (!data.expireDate){
	        	$('#jqxgridReceiveProducts').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.ExpireDateNotEnter}", [{
	                "label" : "${uiLabelMap.OK}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                		$('#jqxgridReceiveProducts').jqxGrid('begincelledit', rowindex, "expireDate");
	                    }
	                }]
	            );
	            return true;
	        } else if (!data.inventoryItemTypeId){
	        	$('#jqxgridReceiveProducts').jqxGrid('unselectrow', rowindex);
	            bootbox.dialog("${uiLabelMap.InventoryItemTypeNotEnter}", [{
	                "label" : "${uiLabelMap.OK}",
	                "class" : "btn btn-primary standard-bootbox-bt",
	                "icon" : "fa fa-check",
	                "callback": function() {
	                		$('#jqxgridReceiveProducts').jqxGrid('begincelledit', rowindex, "inventoryItemTypeId");
	                    }
	                }]
	            );
	            return true;
	        }
		}
	}
	$("#alterSaveReceive").click(function() {
		if ($("#jqxwindowReceiveProducts").jqxValidator("validate")) {
			var dataSoureInput = ReceiveProducts.getData();
			if(dataSoureInput.length != 0){
				var listProducts = [];
				for(var i in dataSoureInput){
					var row = {};
					row['productId'] = dataSoureInput[i].productId;
					row['quantity'] = dataSoureInput[i].receiveQuantity;
					if (dataSoureInput[i].unitListPrice){
						row['unitCost'] = dataSoureInput[i].unitListPrice;
					} else {
						row['unitCost'] = 0;
					}
					row['quantityUomId'] = dataSoureInput[i].quantityUomId;
					row['inventoryItemTypeId'] = dataSoureInput[i].inventoryItemTypeId;
					row['datetimeManufactured'] = dataSoureInput[i].datetimeManufactured.getTime();
					row['expireDate'] = dataSoureInput[i].expireDate.getTime();
					listProducts.push(row);
				}
				listProducts = JSON.stringify(listProducts);
				bootbox.dialog("${StringUtil.wrapString(uiLabelMap.AreYouSureSave)}", 
						[{"label": "${StringUtil.wrapString(uiLabelMap.CommonCancel)}", 
							"icon": 'fa fa-remove', "class": 'btn  btn-danger form-action-button pull-right',
				            "callback": function() {bootbox.hideAll();}
				        },
				        {"label": "${StringUtil.wrapString(uiLabelMap.OK)}",
				            "icon": 'fa-check', "class": 'btn btn-primary form-action-button pull-right',
				            "callback": function() {
				            	ReceiveProducts.submit(listProducts);
				        }
			    }]);
				$("#jqxwindowReceiveProducts").jqxWindow('close');
			}else{
				bootbox.dialog("${uiLabelMap.YouNotYetChooseItem}", [{
		            "label" : "${uiLabelMap.OK}",
		            "class" : "btn btn-primary standard-bootbox-bt",
		            "icon" : "fa fa-check",
		            }]
		        );
			}
		}
	});
</script>