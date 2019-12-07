<div style="position:relative">
	<div class="row-fluid">
		<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
			<div class="span12">
				<div class='row-fluid' style="margin-bottom: -10px !important">
					<div class="span6">
						<div class='row-fluid'>
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.FacilityFrom}</div></div>
						    <div class="span7"><div id="facilityIdDT" style="color: #037C07;" class="green-label"></div></div>
						</div>
						<div class='row-fluid'>
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.Address}</div></div>
						    <div class="span7"><div id="contactMechIdDT" class="green-label"></div></div>
						</div>
						<div class='row-fluid hide'>
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.Status}</div></div>
					 	    <div class="span7"><div id="statusDEIdDT" style="color: #037C07;" class="green-label"></div></div>
						</div>
						<div class="row-fluid">
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.ShipmentType}</div></div>
					 	    <div class="span7"><div id="shipmentTypeIdDT" class="green-label"></div></div>
						</div>
						<div class="row-fluid">
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.TransportCost}</div></div>
					 	    <div class="span7"><div id="shipCostDT" class="green-label"></div></div>
						</div>
				   		<div class='row-fluid'>
					   		<div class="span5" style="text-align: right;"><div>${uiLabelMap.StartShipDate}</div></div>
					 	    <div class="span7"><div class="green-label" id="fromDateDT"></div></div>
						</div>
						<div class='row-fluid'>
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.EndShipDate}</div></div>
					 	    <div class="span7"><div class="green-label" id="thruDateDT"></div></div>
						</div>
					</div>
					<div class="span6">
						<div class='row-fluid'>
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.CarrierParty}</div></div>
					 	    <div class="span7"><div id="carrierPartyIdDT" class="green-label"></div></div>
				   		</div>
						<div class='row-fluid'>
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.Vehicle}</div></div>
					 	    <div class="span7"><div id="fixedAssetIdDT" class="green-label"></div></div>
						</div>
						<div class='row-fluid'>
							<div class="span5" style="text-align: right;"><div>${uiLabelMap.Driver}</div></div>
					 	    <div class="span7"><div id="driverPartyIdDT" class="green-label"></div></div>
						</div>
						<div class="row-fluid">
				    		<div class="span5" style="text-align: right;"><div>${uiLabelMap.Deliverer}</div></div>
					 	    <div class="span7"><div id="delivererPartyIdDT" class="green-label"></div></div>
						</div>
						<div class="row-fluid">
				    		<div class="span5" style="text-align: right;"><div>${uiLabelMap.Description}</div></div>
					 	    <div class="span7"><div id="descriptionDT" class="green-label"></div></div>
						</div>
					</div>
				</div>
			</div>
		</div><!-- .form-horizontal -->
	</div><!--.row-fluid-->
	<div class="row-fluid margin-top10">
		<div class="span12">
			<div id="jqxgridShipmentSelected" style="width: 100%"></div>
		</div>
	</div>
</div>
<script>
	<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false)>
	var qtyUomData = new Array();
	<#list uoms as item>
		var row = {};
		<#assign descUom = StringUtil.wrapString(item.get('description', locale)?if_exists)/>
		row['quantityUomId'] = '${item.uomId?if_exists}';
		row['description'] = '${descUom?if_exists}';
		qtyUomData.push(row);
	</#list>
</script>
<#assign dataFieldShipment="[
	{ name: 'productId', type: 'string' },
	{ name: 'shipmentId', type: 'string' },
	{ name: 'orderId', type: 'string' },
	{ name: 'deliveryId', type: 'string' },
	{ name: 'shipmentItemSeqId', type: 'string' },
	{ name: 'productCode', type: 'string'},
	{ name: 'productName', type: 'string' },
	{ name: 'quantity', type: 'number' },
	{ name: 'quantityCreate', type: 'number' },
	{ name: 'quantityUomId', type: 'string' },	   
	]"/>
		   
<#assign columnlistShipment="
	{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
	    groupable: false, draggable: false, resizable: false,
	    datafield: '', columntype: 'number', width: 50,
	    cellsrenderer: function (row, column, value) {
	        return '<div style=margin:4px;>' + (value + 1) + '</div>';
	    }
	},
	{ text: '${uiLabelMap.ProductCode}', datafield: 'productCode', align: 'left', width: 150, pinned: true},
	{ text: '${uiLabelMap.ProductName}', datafield: 'productName', align: 'left', minwidth: 200,},
	{ text: '${uiLabelMap.CommonDeliveryId}', datafield: 'deliveryId', align: 'left', width: 150,},
	{ text: '${uiLabelMap.OrderId}', datafield: 'orderId', align: 'left', width: 150,},
	{ text: '${uiLabelMap.Quantity}', datafield: 'quantity', align: 'left', width: 200, cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
			if(value != null && value != undefined){
				return '<span style=\"text-align: right;\">' + value.toLocaleString('${localeStr}') + '</span>';
			}
			return '<span style=\"text-align: right;\">' + 0 + '</span>';
	    },
	},
	{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', align: 'left', width: 200, cellsalign: 'right',
		cellsrenderer: function(row, colum, value){
			for (var i = 0; i < qtyUomData.length; i ++) {
				if(qtyUomData[i].quantityUomId == value){
					return '<span style=\"text-align: right;\">' + qtyUomData[i].description + '</span>';
				}
			}
			return '<span style=\"text-align: right;\">_NA_</span>';
	    },
	},
	"/>
<@jqGrid filtersimplemode="true" id="jqxgridShipmentSelected" filterable="false" dataField=dataFieldShipment columnlist=columnlistShipment editable="false" showtoolbar="false" clearfilteringbutton="false" initrowdetailsDetail=initrowdetails
	url="" editmode='click' initrowdetails = "false" selectionmode=""
/>
<script type="text/javascript" src="/logresources/js/deliveryentry/newDeliveryEntryConfirm.js"></script>