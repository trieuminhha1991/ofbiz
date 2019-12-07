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
					<span id="transferTypeDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="25%">
					<span><b>${uiLabelMap.OriginFacility}</b> </span>
				</td>
				<td valign="top" width="70%">
					<span id="originFacilityDT"></span>
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="25%">
					<span><b>${uiLabelMap.DestFacility}</b> </span>
				</td>
				<td valign="top" width="70%">
					<span id="destFacilityDT"></span>
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
					<div><b>${uiLabelMap.ShipAfterDate}</b></div>
				</td>
				<td valign="top" width="70%">
					<span id="shipAfterDateDT"></span>	
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="25%">
					<div><b>${uiLabelMap.ShipBeforeDate}</b></div>
				</td>
				<td valign="top" width="70%">
					<span id="shipBeforeDateDT"></span>	
				</td>
			</tr>
			<tr>
				<td align="right" valign="top" width="30%">
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
					<th width="5%"><span><b>${uiLabelMap.SequenceId}</b></span></th>
					<th width="15%" class="align-center"><span><b>${uiLabelMap.ProductId}</b></span></th>
					<th minwidth="25%" class="align-center"><span><b>${uiLabelMap.ProductName}</b></span></th>
					<th width="15%" class="align-center"><span><b>${uiLabelMap.Unit}</b></span></th>
					<th width="15%" class="align-center"><span><b>${uiLabelMap.RequiredNumberSum}</b></span></th>
				</tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
	</div>
</div>
