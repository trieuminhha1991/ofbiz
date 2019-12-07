<#include 'script/shipmentDetailScript.ftl'/>
<div id="general-tab" class="tab-pane<#if activeTab?exists && activeTab == "general-tab"> active</#if>">				
	<div style="position:relative"><!-- class="widget-body"-->
		<div><!--class="widget-main"-->
			<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px, margin-top: 20px">
				${uiLabelMap.ShipmentTransfer}
			</h3>
			<div class="row-fluid" id="detailShipment">
				<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
					<div class="row-fluid">
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.ShipmentId}</span>
								</div>
								<div class="span7">
									<div class="green-label" id="shipmentId" name="shipmentId"></div>
						   		</div>
							</div>
						</div>
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.Status}</span>
								</div>
								<div class="span7">
									<div class="green-label" id="statusId" name="statusId"></div>
						   		</div>
							</div>
						</div>
					</div>
					<div class='row-fluid' style="margin-bottom: -10px !important">
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.OriginFacility}</span>
								</div>
								<div class="span7">
									<div class="green-label" id="originFacilityId" name="originFacilityId"></div>
						   		</div>
							</div>
						</div>
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.DestFacility}</span>
								</div>
								<div class="span7">
									<div class="green-label" id="destinationFacilityId" name="destinationFacilityId"></div>
						   		</div>
							</div>
						</div>
					</div>
					<div class='row-fluid' style="margin-bottom: -10px !important">
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.EstimatedShipDate}</span>
								</div>
								<div class="span7">
									<div id="estimatedShipDate" class="green-label"></div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.OriginAddress}</span>
								</div>
								<div class="span7">
									<div class="green-label" id="originContactMechId" name="originContactMechId"></div>
						   		</div>
							</div>
						</div>
						<div class="span6">
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.EstimatedArrivalDate}</span>
								</div>
								<div class="span7">
									<div id="estimatedArrivalDate" class="green-label">
									</div>
						   		</div>
							</div>
							<div class='row-fluid'>
								<div class='span5'>
									<span style="float: right;">${uiLabelMap.DestAddress}</span>
								</div>
								<div class="span7">
									<div class="green-label" id="destinationContactMechId" name="destinationContactMechId"></div>
						   		</div>
							</div>
						</div>
					</div>
					<div class='row-fluid' style="margin-bottom: -10px !important"></div>
				</div>
			</div><!-- .form-horizontal -->
		</div><!--.row-fluid-->
		<div class="row-fluid margin-top10">
			<div class="span12">
				<div id="jqxgridShipmentItem" style="width: 100%"></div>
			</div>
		</div>
	</div>

	<#assign dataFieldShipmentItem ="[
	{ name: 'shipmentId', type: 'string'},
	{ name: 'shipmentItemSeqId', type: 'string'},
	{ name: 'productName', type: 'string' },
	{ name: 'productId', type: 'string'},
	{ name: 'productCode', type: 'string'},
	{ name: 'quantity', type: 'number'},
	{ name: 'quantityUomId', type: 'string'},
	{ name: 'baseQuantityUomId', type: 'string'},
	{ name: 'datetimeManufactured', type: 'date', other: 'Timestamp'},
    { name: 'expireDate', type: 'date', other: 'Timestamp'},
    { name: 'datetimeReceived', type: 'date', other: 'Timestamp'},
	]"/>
	<#assign columnlistShipmentItem ="
	{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
		groupable: false, draggable: false, resizable: false,
		datafield: '', columntype: 'number', width: 50,
		cellsrenderer: function (row, column, value) {
			return '<div style=margin:4px;>' + (value + 1) + '</div>';
		}
	},
	{ text: '${uiLabelMap.ProductId}', datafield: 'productCode', width: 200, pinned: true, editable: false,},
	{ text: '${uiLabelMap.ProductName}', datafield: 'productName', minwidth: 200, editable: false,},
	{ text: '${uiLabelMap.ReceiveDate}', datafield: 'datetimeReceived', width: 150, columntype: 'datetimeinput', editable: false, filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
	{ text: '${StringUtil.wrapString(uiLabelMap.ProductManufactureDate)}', dataField: 'datetimeManufactured', editable: false, width: 150, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
	{ text: '${StringUtil.wrapString(uiLabelMap.ProductExpireDate)}', dataField: 'expireDate', width: 150, editable: false, columntype: 'datetimeinput', filtertype: 'range', cellsformat: 'dd/MM/yyyy', cellsalign: 'right' },
	{ text: '${uiLabelMap.Quantity}', datafield: 'quantity', width: 100, editable: false,
		cellsrenderer: function(row, column, value){
			if (value){
				return '<span style=\"text-align: right;\">' +  value.toLocaleString('${localeStr}') + '</span>';
			} else {
				return '<span style=\"text-align: right;\">'+0+'</span>';
			}
		},
	},
	{ text: '${uiLabelMap.Unit}', datafield: 'quantityUomId', width: 100, editable: false,
		cellsrenderer: function(row, colum, value){
			var data = $('#jqxgridShipmentItem').jqxGrid('getrowdata', row);
			if(value){
				for (var i=0; i < quantityUomData.length; i++){
					if (quantityUomData[i].uomId == value){
						return '<span>' +  quantityUomData[i].description + '</span>';
					}
				}
				return '<span></span>';
			} else {
				for (var i=0; i < quantityUomData.length; i++){
					if (quantityUomData[i].uomId == data.baseQuantityUomId){
						return '<span>' +  quantityUomData[i].description + '</span>';
					}
				}
				return '<span></span>';
			}
		}
	}"/>
	
	<@jqGrid filtersimplemode="true" id="jqxgridShipmentItem" filterable="false" dataField=dataFieldShipmentItem columnlist=columnlistShipmentItem editable="false" showtoolbar="false"
		url="jqxGeneralServicer?sname=getListShipmentItem&shipmentId=${parameters.shipmentId?if_exists}"
	/>
</div>
<script type="text/javascript" src="/logresources/js/shipment/shipmentDetail.js"></script>