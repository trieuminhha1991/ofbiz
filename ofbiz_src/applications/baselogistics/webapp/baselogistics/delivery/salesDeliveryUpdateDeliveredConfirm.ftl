<div id="info" class="font-bold margin-top10">
	<div class="span12">
		<div class='row-fluid'>
			<div class="span6">
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.OrderId}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;" id="orderId2" name="orderId2">${delivery.orderId?if_exists}</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.ShippingAddress}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;" id="shippingAddress2" name="shippingAddress2">
							${customerAddress?if_exists}
						</div>
			   		</div>
				</div>
				<div class='row-fluid'>
					<div class='span4 align-right'>
						<span>${uiLabelMap.ActualExportedDate}</span>
					</div>
					<div class="span8">
						<div class="green-label" style="text-align: left;" id="actualDate2" name="actualDate2">
							<#if delivery?has_content>
								<#if delivery.actualStartDate?exists>
									<span>${delivery.actualStartDate?datetime?string('dd/MM/yyyy HH:mm')}</span>
								</#if>
							</#if>
						</div>
			   		</div>
				</div>
			</div>
			<div class="span6">
				<div class='row-fluid'>
					<div class='row-fluid'>
						<div class='span3 align-right'>
							<span>${uiLabelMap.CreatedDate}</span>
						</div>
						<div class="span9">
							<div class="green-label" style="text-align: left;" id="entryDate2" name="entryDate">${delivery.createDate?datetime?string('dd/MM/yyyy HH:mm')}</div>
				   		</div>
					</div>
				</div>
				<div class='row-fluid'>
					<div class='span3 align-right'>
						<span>${uiLabelMap.ExportFromFacility}</span>
					</div>
					<div class="span9">
						<div class="green-label" id="facilityId2" name="facilityId2">
						<#if facility?has_content>
							<#if facility.facilityCode?has_content>
								[${facility.facilityCode?if_exists}] ${facility.facilityName?if_exists}
							<#else>
								[${facility.facilityId?if_exists}] ${facility.facilityName?if_exists}
							</#if>
						</#if>
						</div>
			   		</div>
				</div>
				<div class="row-fluid">
					<div class="span3 align-right">
						<span>${StringUtil.wrapString(uiLabelMap.Address)}</span>
					</div>
					<div class="span9">
						<span class="green-label">${originAddress?if_exists}</span>
					</div>
				</div>
			</div>
		</div>
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
					<th width="30%" class="align-center"><span><b>${uiLabelMap.ProductName}</b></span></th>
					<th width="6%" class="align-center"><span><b>${uiLabelMap.Unit}</b></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.IsPromo}</b></span></th>
					<th width="8%" class="align-center"><span><b>${uiLabelMap.RequiredNumberSum}</b></span></th>
					<th width="8%" class="align-center"><span><b>${uiLabelMap.ActualDeliveryQuantitySum}</b></span></th>
					<th width="8%" class="align-center"><span><b>${uiLabelMap.ActualDeliveredQuantitySum}</b></span></th>
					<th width="8%" class="align-center"><span><b>${uiLabelMap.UnitPrice}</b></span></th>
					<th width="10%" class="align-center"><span><b>${uiLabelMap.BPOTotal} </br></span></th>
				</tr>
			</thead>
			<tbody>
				
			</tbody>
		</table>
	</div>
</div>
