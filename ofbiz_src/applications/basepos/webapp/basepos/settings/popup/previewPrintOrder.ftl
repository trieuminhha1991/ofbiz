<style>
	.headerFontSize{
		font-size: 12px;
	}
	.infoFontSize{
		font-size: 10px;
	}
	.contentFontSize{
		font-size: 8px;
	}
	.order-logo-container {
		width: 50px;
		height: 50px;
		margin: 5px 0px;
	}
</style>
<div id="PrintOrder" style="width:98%;font-family: Arial; font-size:8px; margin:0 auto; padding:0; margin-left: 3px; margin-right: 3px">
		<table border="0" cellpadding="1" cellspacing="1" style="width:100%">
			<tbody>
				<tr>
					<td colspan="2" rowspan="3">
						<div class="order-logo-container">
							<img id="logo"/>
						</div>
					</td>
					<td colspan="10" rowspan="1">
						<b style="font-weight:bold;" class="infoFontSize" id="storeName">
						</b>
					</td>
				</tr>
				<tr>
					<td colspan="10" rowspan="1">
						<span style="padding:0;" class="infoFontSize" id="addressStore">
						</span>
					</td>
				</tr>
				<tr>
					<td colspan="10" rowspan="1">
						<span style="padding:0;" class="infoFontSize" id="phoneStore">
						</span></td>
				</tr>
				<tr>
					<td colspan="12"><center><span class="headerFontSize" style="font-weight:bold;">${uiLabelMap.BPOSRetailBill}</span></center></td>
				</tr>
				<tr>
					<td colspan="12">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="6" rowspan="1"><span class="infoFontSize">${uiLabelMap.BPOSEmployee}:</span></td>
					<td colspan="6" rowspan="1"><span id="employee" style="font-weight:bold;" class="infoFontSize">${userLogin.userLoginId}</span></td>
				</tr>
				<tr>
					<td colspan="6" rowspan="1"><span class="infoFontSize">${uiLabelMap.BPOSTime}:</span></td>
					<#assign timeSale = Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().toString()>
					<td colspan="6" rowspan="1"><span id="timeSale" style="font-weight:bold;" class="infoFontSize">${timeSale}</span></td>
				</tr>
				<tr id="customerName">
					<td colspan="6" rowspan="1"><span class="infoFontSize">${uiLabelMap.BPOSCustomer}:</span></td>
					<td colspan="6" rowspan="1"><span id="name" style="font-weight:bold;" class="infoFontSize">Nguyen Van A</span></td>
				</tr>
				<tr id="customerAddress">
					<td colspan="6" rowspan="1"><span class="infoFontSize">${uiLabelMap.BPOSAddress}:</span></td>
					<td colspan="6" rowspan="1"><span id="address" style="font-weight:bold;" class="infoFontSize">Nguyen Chi Thanh</span></td>
				</tr>
				<tr id="customerPhone">
					<td colspan="6" rowspan="1"><span class="infoFontSize">${uiLabelMap.BPOSMobile}:</span></td>
					<td colspan="6" rowspan="1"><span id="mobile" style="font-weight:bold;" class="infoFontSize">000-000-0000</span></td>
				</tr>
				<tr>
					<td colspan="12">&nbsp;</td>
				</tr>
				<tr>
					<td colspan="12"><span id="currencyUom" style="float:right; margin: 0 10px 10px 0;" class="infoFontSize">ĐVT: VND</span></td>
				</tr>
			</tbody>
		</table>
		<table cellpadding="0" cellspacing="0" style="width:100%;">
			<thead>
				<tr>
					<th style="border:1px solid #CCC;"><center><span style="font-weight:bold;padding:0 2px;" class="contentFontSize">#</span></center></th>
					<th style="border:1px solid #CCC;"><center><span style="font-weight:bold;padding:0 2px;" class="contentFontSize">${uiLabelMap.BPOSShortProductName}</span></center></th>
					<th style="border:1px solid #CCC;"><center><span style="font-weight:bold;padding:0 2px;" class="contentFontSize">${uiLabelMap.BPOSPrice}</span></center></th>
					<th style="border:1px solid #CCC;"><center><span style="font-weight:bold;padding:0 2px;" class="contentFontSize">${uiLabelMap.BPOSDiscount}</span></center></th>
					<th style="border:1px solid #CCC;"><center><span style="font-weight:bold;padding:0 2px;" class="contentFontSize">${uiLabelMap.BPOSShortQuantity}</span></center></th>
					<th style="border:1px solid #CCC;"><center><span style="font-weight:bold;padding:0 2px;" class="contentFontSize">${uiLabelMap.BPOSPrintAmount}</span></center></th>
				</tr>
			</thead>
			<tbody id="bodyPrint">
				<tr>
					<td style="border:1px solid #CCC;"><span style="text-align:center;display:block;padding:0 2px;" class="contentFontSize">1</span></td>
					<td style="border:1px solid #CCC;"><span style="float:left;padding:0 2px;text-align:justify;" class="contentFontSize">SP 0001</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;padding:0 2px;" class="contentFontSize">10.000</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;padding:0 2px;" class="contentFontSize">0.000</span></td>
					<td style="border:1px solid #CCC;"><span style="text-align:center;display:block;padding:0 2px;" class="contentFontSize">1</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;padding:0 2px;" class="contentFontSize">10.000</span></td>
				</tr>
				<tr>
					<td style="border:1px solid #CCC;"><span style="text-align:center;display:block;padding:0 2px;" class="contentFontSize">2</span></td>
					<td style="border:1px solid #CCC;"><span style="float:left;padding:0 2px;text-align:justify;" class="contentFontSize">SP 0002</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;padding:0 2px;" class="contentFontSize">20.000</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;padding:0 2px;" class="contentFontSize">1.000</span></td>
					<td style="border:1px solid #CCC;"><span style="text-align:center;display:block;padding:0 2px;" class="contentFontSize">1</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;padding:0 2px;" class="contentFontSize">19.000</span></td>
				</tr>
				<tr>
					<td style="border:1px solid #CCC;"><span style="text-align:center;display:block;padding:0 2px;" class="contentFontSize">3</span></td>
					<td style="border:1px solid #CCC;"><span style="float:left;padding:0 2px;text-align:justify;" class="contentFontSize">SP 0003</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;padding:0 2px;" class="contentFontSize">30.000</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;padding:0 2px;" class="contentFontSize">3.000</span></td>
					<td style="border:1px solid #CCC;"><span style="text-align:center;display:block;padding:0 2px;" class="contentFontSize">1</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;padding:0 2px;" class="contentFontSize">27.000</span></td>
				</tr>
				<tr>
					<td style="border:1px solid #CCC;" colspan="5"><span style="float:right;font-weight:bold;padding:0 2px;" class="contentFontSize">${uiLabelMap.BPOSGrandTotal}</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;font-weight:bold;padding:0 2px;" class="contentFontSize">56.000</span></td>
				</tr>
				<tr>
					<td style="border:1px solid #CCC;" colspan="5"><span style="float:right;font-weight:bold;padding:0 2px;" class="contentFontSize">${uiLabelMap.BPOSDiscount}</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;font-weight:bold;padding:0 2px;" class="contentFontSize">5.000</span></td>
				</tr>
				<tr>
					<td style="border:1px solid #CCC;" colspan="5"><span style="float:right;font-weight:bold;padding:0 2px;" class="contentFontSize">${uiLabelMap.BPOSTotalPay}</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;font-weight:bold;padding:0 2px;" class="contentFontSize">51.000</span></td>
				</tr>
				<tr>
					<td style="border:1px solid #CCC;" colspan="5"><span style="float:right;font-weight:bold;padding:0 2px;" class="contentFontSize">${uiLabelMap.BPOSCustomerPay}</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;font-weight:bold;padding:0 2px;" class="contentFontSize">60.000</span></td>
				</tr>
				<tr>
					<td style="border:1px solid #CCC;" colspan="5"><span style="float:right;font-weight:bold;padding:0 2px;" class="contentFontSize">${uiLabelMap.BPOSReturnCash}</span></td>
					<td style="border:1px solid #CCC;"><span style="float:right;font-weight:bold;padding:0 2px;" class="contentFontSize">9.000</span></td>
				</tr>
			</tbody>
		</table>
		<p style="text-align:center; margin-top: 10px"><span class="infoFontSize">${uiLabelMap.BPOSGoodBye}</span></p>
	</div>
<style>
@media print {
	#PrintOrder{
		display: block !important;
	}
	#PrintOrder table tr td,#PrintOrder table tr th{
		
	}
}
</style>