<div class="row-fluid">
	<div class="span6">
		<h4 class="smaller green" style="display:inline-block">
			${uiLabelMap.GeneralInfo}
		</h4>
		<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable">
			<tr>
				<td align="right" valign="top" width="25%">
					<span><b>${uiLabelMap.TransferType}</b> </span>
				</td>
				<td valign="top" width="70%">
					<span id="transferTypeIdDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="25%">
					<span><b>${uiLabelMap.FacilityFrom}</b> </span>
				</td>
				<td valign="top" width="70%">
					<span id="originFacilityIdDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="25%">
					<span><b>${uiLabelMap.OriginAddress}</b> </span>
				</td>
				<td valign="top" width="70%">
					<span id="originContactMechDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="25%">
					<span><b>${uiLabelMap.FacilityTo}</b> </span>
				</td>
				<td valign="top" width="70%">
					<span id="destFacilityIdDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="25%">
					<span><b>${uiLabelMap.DestAddress}</b> </span>
				</td>
				<td valign="top" width="70%">
					<span id="destContactMechDT"></span>
				</td>
			</tr>
		</table>
	</div><!--.span6-->
	<div class="span6">
		<h4 class="smaller green" style="display:inline-block">
			<!-- title -->
		</h4>
		<table width="100%" border="0" cellpadding="1" class="table table-striped table-hover table-bordered dataTable margin-top15">
			<tr>
				<td align="right" valign="top" width="30%">
					<div><b>${uiLabelMap.BSDesiredDeliveryDate}</b></div>
				</td>
				<td valign="top" width="70%">
					<span id="shipAfterDateDT"></span> - <span id="shipBeforeDateDT"></span>		
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="30%">
					<div><b>${uiLabelMap.ShipmentMethod}</b></div>
				</td>
				<td valign="top" width="70%">
					<span id="shipmentMethodTypeIdDT"></span>	
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="30%">
					<div><b>${uiLabelMap.CarrierParty}</b></div>
				</td>
				<td valign="top" width="70%">
					<span id="carrierPartyIdDT"></span>	
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="25%">
					<div><b>${uiLabelMap.Description}</b></div>
				</td>
				<td valign="top" width="70%">
					<span id="descriptionDT"></span>	
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
					<th width="10%" class="align-center"><span><b>${uiLabelMap.Note}</b></span></th>
					<th width="8%" class="align-center"><span><b>${uiLabelMap.QOH}</b></span></th>
					<th width="8%" class="align-center"><span><b>${uiLabelMap.Unit}</b></span></th>
					<th width="8%" class="align-center"><span><b>${uiLabelMap.Quantity}</b></span></th>
					<th width="8%" class="align-center"><span><b>${uiLabelMap.UnitPrice}</b></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.BPOTotal} </br></span></th>
				</tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
	</div>
</div>