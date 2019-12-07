<div class="row-fluid">
	<div class="span4">
		<h4 class="smaller green" style="display:inline-block">
			${uiLabelMap.BIEBillOfLading}
		</h4>
		<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
			<tr>
				<td align="right" valign="top" width="45%">
					<span><b>${uiLabelMap.BillNumber}</b> </span>
				</td>
				<td valign="top" width="55%">
					<span id="txtBillNumberDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<span><b>${uiLabelMap.FromShippingLine}</b> </span>
				</td>
				<td valign="top" width="55%">
					<span id="shippingPartyDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<span><b>${uiLabelMap.departureDate}</b> </span>
				</td>
				<td valign="top" width="55%">
					<span id="txtdepartureDateDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<span><b>${uiLabelMap.arrivalDate}</b> </span>
				</td>
				<td valign="top" width="55%">
					<span id="txtarrivalDateDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<span><b>${uiLabelMap.Description}</b> </span>
				</td>
				<td valign="top" width="55%">
					<span id="billDescriptionDT"></span>
				</td>
			</tr>
		</table>
	</div>
	<div class="span4">
		<h4 class="smaller green" style="display:inline-block">
			${uiLabelMap.BIEContainer}
		</h4>
		<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
			<tr>
				<td align="right" valign="top" width="45%">
					<span><b>${uiLabelMap.BIEContainerType}</b> </span>
				</td>
				<td valign="top" width="55%">
					<span id="containerTypeIdDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<span><b>${uiLabelMap.containerNumber}</b> </span>
				</td>
				<td valign="top" width="55%">
					<span id="containerNumberDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<span><b>${uiLabelMap.sealNumber}</b> </span>
				</td>
				<td valign="top" width="55%">
					<span id="sealNumberDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<span><b>${uiLabelMap.ReceiveToFacility}</b> </span>
				</td>
				<td valign="top" width="55%">
					<span id="facilityContainerDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<span><b>${uiLabelMap.Description}</b> </span>
				</td>
				<td valign="top" width="55%">
					<span id="contDescriptionDT"></span>
				</td>
			</tr>
		</table>
	</div>
	<div class="span4">
		<h4 class="smaller green" style="display:inline-block">
			${uiLabelMap.BIEPackingList}
		</h4>
		<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
			<tr>
				<td align="right" valign="top" width="30%">
					<div><b>${uiLabelMap.packingListNumber}</b></div>
				</td>
				<td valign="top" width="55%">
					<span id="packingListNumberDT"></span>	
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<div><b>${uiLabelMap.BIEVendorInvoiceNum}</b></div>
				</td>
				<td valign="top" width="55%">
					<span id="invoiceNumberDT"></span>	
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<div><b>${uiLabelMap.BIEVendorOrderNum}</b></div>
				</td>
				<td valign="top" width="55%">
					<span id="orderNumberSuppDT"></span>	
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<div><b>${uiLabelMap.BIEAgreement}</b></div>
				</td>
				<td valign="top" width="55%">
					<span id="agreementDT"></span>	
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<div><b>${uiLabelMap.packingListDate}</b></div>
				</td>
				<td valign="top" width="55%">
					<span id="packingListDateDT"></span>	
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<div><b>${uiLabelMap.invoiceDate}</b></div>
				</td>
				<td valign="top" width="55%">
					<span id="invoiceDateDT"></span>	
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<div><b>${uiLabelMap.totalNetWeight}</b></div>
				</td>
				<td valign="top" width="55%">
					<span id="totalNetWeightDT"></span>	
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="45%">
					<div><b>${uiLabelMap.totalGrossWeight}</b></div>
				</td>
				<td valign="top" width="55%">
					<span id="totalGrossWeightDT"></span>	
				</td>
			</tr>
		</table>
	</div>
</div>

<div class="row-fluid">
	<div class="span12">
		<h4 class="smaller green" style="display:inline-block">${uiLabelMap.ListProduct}</h4>
		<table id="tableProduct" width="100%" border="0" cellpadding="0" class="table table-striped table-bordered table-hover dataTable">
			<thead>
				<tr valign="bottom" style="height: 40px">
					<th width="3%"><span><b>${uiLabelMap.SequenceId}</b></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.ProductId}</b></span></th>
					<th width="25%" class="align-center"><span><b>${uiLabelMap.ProductName}</b></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.originOrderUnit}</b></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.packingUnits}</b></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.orderUnits}</b></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.dateOfManufacture}</b></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.ProductExpireDate}</b></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.batchNumber}</b></span></th>
				</tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
	</div>
</div>