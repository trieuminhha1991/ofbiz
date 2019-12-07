<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<form id='CreateInvoiceTotalPDF' action="CreateInvoiceTotalPDF" method="post">
<div class="row-fluid" style="margin-top:50px">
<div class="span12 no-left-margin">
		<div class="span6">
<textarea rows="10" cols="450" style="margin: 10px 0px; width: 97%; height: 100%;resize:none" id="tarInvoicePartyInfo" spellcheck='false'>Zott Dairy Asia Pacific Pte.Ltd, 1 Scotts Road # 21-10 Shaw Centre,
Singapore 228208
OLBIUS Viet Nam
91 Nguyen Chi Thanh
Dong Da District
00000 HANOI
VIETNAM
</textarea>
			
		</div>
		<div class="span6">
			<table border= "1px solid black"  width="100%">
				<tr>
					<td height="25"><h4 style="margin-left: 30px"><b>INVOICE</b></h4></td>
				</tr>
				<tr>
					<td>
<textarea rows="10" cols="450" style="margin: 10px 0px; width: 97%; height: 100%;resize:none" id="tarInvoiceInfo" spellcheck='false'>Recipient of the invoice:  
Client number:  
Invoice-No:  
Invoice-Date:  
Contact address:                Phone:  
                                              Fax:  
</textarea>
					</td>
				</tr>
			</table>
		</div>
</div>
</div><br/>
<hr class="hr-no-margin" width="100%" style="border: 1px solid #0099CC" size="1">
<table width="100%">
	<tr>
		<td width="5%">Pos</td>
		<td width="30%">Article-No</td>
		<td width="20%">Delevery-Quantity</td>
		<td width="15%" style="text-align: center;">Price</td>
		<td width="2%" style="text-align: right;">per</td>
		<td width="12%" style="text-align: right;">Unit</td>
		<td width="9%" style="text-align: right;">VAT</td>
		<td width="7%"><div class='span2 no-left-margin' style="text-align: right;">Goods-Value</div></td>
	</tr>
	<tr>
		<td width="5%"></td>
		<td width="30%">Denotation</td>
		<td width="20%" style="text-align: center;">EAN-No</td>
		<td width="15%" style="text-align: center;">Net-Weight</td>
		<td width="2%" style="text-align: left;">Unit</td>
		<td width="12%" style="text-align: right;">Gross-Weight</td>
		<td width="9%" style="text-align: right;">Unit</td>
		<td width="7%" style="text-align: right;"></td>
	</tr>
</table>
<hr class="hr-no-margin" width="100%" style="border: 1px solid #0099CC" size="1">
<br/>

<div class="row-fluid">
	<div class="span12 no-left-margin">
			<div class="span1 no-left-margin"></div>
			<div class="span11 no-left-margin"><span>We invoice the products which were dilivered through Zott SE & Co.KG, Germany</span></div>
	</div>
	<div class="span12 no-left-margin">
			<div class="span1 no-left-margin"></div>
			<div class="span11 no-left-margin">
					<div class="row-fluid">
						<div class="span12 no-left-margin">
								<div class="span2"><u>Delivery</u></div>
								<div class="span2"><span>OLBIUS Viet Nam 91 Nguyen Chi Thanh, Dong Da District, 00000 HANOI VIETNAM</span></div>
								<div class="span8" style="padding-left: 105px;">
									<table width="100%" id="tableAddble">
										<tr>
											<td><input type="textbox" name="txtClientNo"  placeholder="Client-No" /></td>
										</tr>
										<tr>
											<td><input type="textbox" name="txtOrderNo"  placeholder="Order-No"/></td>
											<td><input type="textbox" name="txtExternalOrderNo"  placeholder="External-Order-No" /></td>
										</tr>
										<tr>
											<td><input type="textbox" name="txtDeliveryNo1"  placeholder="Delivery-No 1" /></td>
											<td></td>
										</tr>
										<tr>
											<td><input type="textbox" name="txtDeliveryDate1"  placeholder="Delivery-Date 1" /></td>
											<td></td>
										</tr>
									</table>
								</div>
						</div>
					</div>
			</div>
	</div>
</div>
<input type="button" name='AddField' value="Add" style="margin-left: 64%;" >
<#if listOrder?exists>
<#assign listSize= listOrder?size />
<#assign hasColor= true />
<table width="100%" style="margin-top: 32px;" border= "1px solid #8470FF;">
	<#list listOrder as order>
				<#if hasColor>
					<tr class="color">
				<#else>
					<tr class="color1">
				</#if>
				<td width="5%"><input type="text" class="input10" id="txtPos_${order_index}" placeholder="Pos" /></td>
				<td width="5%"><input type="text" class="input40" id="txtArticleNO_${order_index}" placeholder="Article-No" /></td>
				<td width="5%"><input type="text" class="input40 numbers" id="txtKAR_${order_index}" />KAR</td>
				<td width="5%"><input type="text" class="input40 numbers" onkeypress='return event.charCode >= 48 && event.charCode <= 57' id="txtQuantity_${order_index}" value="${order.quantity?string("##,000")}"/>${order.quantityUomId}</td>
				<td width="10%"><input type="text" class="input40" id="txtPrice_${order_index}" value="${order.unitPrice}"/><label id="lblCurrencyUom_${order_index}">${order.currencyUom}</label></td>
				<td width="5%"><input type="text" class="input10 numbers" id="txtPer_${order_index}" placeholder="per" /></td>
				<td width="10%"><label id="lblQuantityUomId_${order_index}">${order.quantityUomId}</label><input type="text" class="input10 numbers" id="txtVAT_${order_index}" placeholder="VAT" /></td>
				<td width="5%"><input type="text" class="input50Red numbers" id="txtGoodsValue_${order_index}" onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' onchange="txtGoodsValueChange()" value="${order.grandTotal?string("##,000")}" /></td>
			</tr>

			<#assign currencyUom= order.currencyUom />
			<#assign weightUomId= order.weightUomId />
			<#if hasColor>
				<tr class="color">
			<#else>
				<tr class="color1">
			</#if>
				<td></td>
				<td colspan="2"><input id="lblInternalName_${order_index}" value="${order.internalName}" spellcheck='false' /></td>
				<td><input type="text" class="input40 numbers" id="txtEANNO_${order_index}" placeholder="EAN-No" /></td>
				<td colspan="2"><input type="text" class="input50 numbers" onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' id="txtNetWeight_${order_index}" onchange="txtNetWeightChange()" value="${order.productWeight?string("##,000.00##")}" /><label id="lblWeightUomId_${order_index}">${order.weightUomId}</label></td>
				<td colspan="2"><input type="text" class="input50 numbers" onkeypress='return ((event.charCode >= 40 && event.charCode <= 57) || (event.charCode == 0))' id="txtGrossWeight_${order_index}" onchange="txtGrossWeightChange()" value="${order.weight?string("##,000.00##")}" />${order.weightUomId}</td>
			</tr>
			<#if hasColor>
				<tr class="color">
			<#else>
				<tr class="color1">
			</#if>
				<td></td>
				<td>Commodity Code</td>
				<td colspan="6"><input type="text" class="input50 numbers" id="txtCommodity_${order_index}" placeholder="Commodity Code" /></td>
			</tr>
			<#assign hasColor = !hasColor />
	</#list>
</table>
</#if>
<input type="hidden" id="myList" name="myList" />
<input type="hidden" id="weightUomId" name="weightUomId" />
<input type="hidden" id="currencyUom" name="currencyUom" />
<input type="hidden" id="txtInvoicePartyInfo" name="txtInvoicePartyInfo" />
<input type="hidden" id="txtInvoiceInfo" name="txtInvoiceInfo" />
<input type="hidden" id="txtDynamicField" name="txtDynamicField" />

<hr class="hr-no-margin" width="100%" style="border: 1px solid black" size="1">
<div class="row-fluid">
	<div class="span12 no-left-margin">
			<div class="span2"></div>
			<div class="span2">Total</div>
			<div class="span2">VAT</div>
			<div class="span2">VAT-Value</div>
			<div class="span2">VAT-Amount</div>
			<div class="span2">Final-Amount</div>
	</div>
</div>
<hr class="hr-no-margin" width="100%" style="border: 1px solid black" size="1">
<div class="row-fluid">
	<div class="span12 no-left-margin">
		<div class="span2"></div>
		<div class="span2"><input class="input50Red numbers" type="text" id="lblTotal" name="lblTotal" value="${grandTotalAll?string("##,000")}" /></div>
		<div class="span2"><input class="input30 numbers" type="text" id="lblVAT" name="lblVAT" /></div>
		<div class="span2"><input class="input50 numbers" type="text" id="lblVATValue" name="lblVATValue" /></div>
		<div class="span2"><input class="input50 numbers" type="text" id="lblVATAmount" name="lblVATAmount" /></div>
		<div class="span2"><input class="input50Red numbers" type="text" id="lblFinalAmount" name="lblFinalAmount" value="${grandTotalAll?string("##,000")}" /></div>
	</div>
</div>
<hr class="hr-no-margin" width="100%" style="border: 1px solid black" size="1">
<div class="row-fluid">
	<div class="span12 no-left-margin">
		<div class="span10"></div>
		<div class="span2" style="text-align:right"><span>${currencyUom}<span></div>
	</div>
</div>
<hr class="hr-no-margin" width="100%" style="border: 1px solid black" size="1">
<div class="row-fluid">
	<div class="span12 no-left-margin">
		<div class="span2">No-of-Pallet</div>
		<div class="span2"><input class="input50 numbers" type="text" onchange="txtNoOfPalletChange()" id="txtNoOfPallet" name="txtNoOfPallet" /></div>
		<div class="span2">Total-Net-Weight</div>
		<div class="span2"><input class="input50 numbers" type="text" id="txtTotalNetWeight" name="txtTotalNetWeight" value="${productWeightTotal?string("##,000.00##")}" /></div>
		<div class="span2">${weightUomId}</div>
		<div class="span2">Term-of-delivery</div>
	</div>
</div>
<div class="row-fluid">
	<div class="span12 no-left-margin">
		<div class="span2">No-of-Sale-Unit</div>
		<div class="span2"><input class="input50 numbers" type="text" id="txtNoOfSaleUnit" name="txtNoOfSaleUnit" /></div>
		<div class="span2">Total-Gross-Weight</div>
		<div class="span2"><input class="input50 numbers" type="text" id="txtTotalGrossWeightt" name="txtTotalGrossWeightt" value="${weightTotal?string("##,000.00##")}" /></div>
		<div class="span2">${weightUomId}</div>
		<div class="span2">CFR/ICD My Dinh</div>
	</div>
</div>
<hr class="hr-no-margin" width="100%" style="border: 1px solid black" size="1">
<div class="row-fluid">
	<div class="span12 no-left-margin">
		<div class="span2">DB-Tragerpallette</div>
		<div class="span2"><input class="input40 numbers" type="text" id="txtDBTragerpallette" name="txtDBTragerpallette" /></div>
		<div class="span2">x <input class="input40 numbers" type="text" onchange="txtXChange()" id="txtX" name="txtX" /> ${weightUomId}</div>
		<div class="span2"><input class="input50 numbers" type="text" id="txtTotalNoPallet" name="txtTotalNoPallet" /></div>
		<div class="span2">${weightUomId}</div>
		<div class="span2">(additional gross weight)</div>
	</div>
</div>
<div style="margin-top: 50px;">
	<span>We here with certify that the mentioned goods in this invoice have been manufactured</span><br/>
	<span>In the Federal Republic of Germany and that the origin is at there as well as the</span><br/>
	<span>Indicated prices are the current export market prices.</span><br/>
	<span>For tax purposes the date of performing an abligation is the day of loading. Sales Condition are included in the Valid Listing.</span><br/>
</div>
<hr width="100%" style="border: 1px solid #0099CC" size="1" margin-top="35px">
<div class="row-fluid">
	<div class="span12 no-left-margin">
			<div class="span3">
				Zott Dairy Asia Pacific Pte.Ltd.<br/>
				Address of record: Singapore<br/>
				Register: 201408669<br/>
			</div>
			<div class="span3">
				Service-Centre:<br/>
				Phone: +49(0)9078-801-0<br/>
				Fax: +49(0)9078/801-110<br/>
				Email: info@zott.sg<br/>
				Internet: www.zott.de<br/>
			</div>
			<div class="span3">
				Directors:<br/>
				Anton Hammer<br/>
				Herve Massot<br/>
				Martin Mannhardt<br/>
			</div>
			<div class="span3">
				HSBC Ltd.<br/>
				IBAN Code: DE44300308801948546007<br/>
				BIC Code: TUBDDEDD<br/>
			</div>
	</div>
</div>
<button type="button" name="CreatePDF" class="btn btn-primary form-action-button pull-right"><i class="icon-print"></i>${uiLabelMap.AccountingExportAsPdf}</button>
</form>
<style>
.hr-no-margin {
	  margin: 0px 0;
	opacity: 0.1;
}
.input10 {
	width: 40px !important;
	font-size: 12px !important;
	height: 15px !important;
}
.input30 {
	width: 50px !important;
	font-size: 10px;
	height: 15px !important;
}
.input50 {
	width: 120px !important;
	height: 15px !important;
}
.input50Red {
	width: 120px !important;
	font-size: 10px;
	height: 15px !important;
	color: red !important;
}
.input40 {
	width: 80px !important;
	font-size: 10px;
	height: 15px !important;
}
.color {
	background: #dae5f4;
}
.color1 {
	background: #FFDEAD;
}
</style>
<script>
	var locale = "vi";
	$(document).ready(function () {
//		locale = '${locale}';
	});

	var listInputNumber = $('.numbers');
	for (var int = 0; int < listInputNumber.length; int++) {
		listInputNumber[int].addEventListener('blur', function () {
			this.setAttribute('type', 'text');
		    this.value = String(this.value).toDecimalFormat();
		});
		listInputNumber[int].addEventListener('focus', function () {
			this.setAttribute('type', 'text');
			this.value = String(this.value).toDecimal();
		});
	}
	var quantityForm = 1;
	$("input[name='AddField']").click(function(){
		quantityForm += 1;
		$("#tableAddble").find('tbody')
			    .append($('<tr>')
			        .append($('<td>')
			            .append($('<input>')
			                .attr('type', 'text')
			                .attr('name', 'txtDeliveryNo' + quantityForm)
			                .attr('placeholder', 'Delivery-No ' + quantityForm)
			            )
		        )
	    );
		$("#tableAddble").find('tbody')
			    .append($('<tr>')
			        .append($('<td>')
			            .append($('<input>')
			                .attr('type', 'text')
			                .attr('name', 'txtDeliveryDate' + quantityForm)
			                .attr('placeholder', 'Delivery-Date ' + quantityForm)
			            )
		        )
	    );
	});
	
	var listSize = '${listSize}';
	listSize = listSize.toInt();
	
	$("button[name='CreatePDF']").click(function(){
		var listField = new Array();
		for (var int = 1; int <= quantityForm; int++) {
			var thisField = {};
			var thisDeliveryNo = $("input[name='txtDeliveryNo"  + int + "']").val();
			var thisDeliveryDate = $("input[name='txtDeliveryDate"  + int + "']").val();
			thisField.thisDeliveryNo = thisDeliveryNo;
			thisField.thisIndex = int;
			thisField.thisDeliveryDate = thisDeliveryDate;
			listField.push(thisField);
		}
		$("#txtDynamicField").val(JSON.stringify(listField));
		
		var tarInvoicePartyInfo = $("#tarInvoicePartyInfo").val();
		var cout = tarInvoicePartyInfo.getQuantityChars("\n");
		var listtarInvoicePartyInfo = new Array();
		for (var x = 0; x <= cout; x++) {
			var thisLine = tarInvoicePartyInfo.split("\n")[x];
			if (thisLine == undefined) {
				break;
			}
			listtarInvoicePartyInfo.push(thisLine);
		}
		$("#txtInvoicePartyInfo").val(JSON.stringify(listtarInvoicePartyInfo));
		
		var tarInvoiceInfo = $("#tarInvoiceInfo").val();
		var cout = tarInvoiceInfo.getQuantityChars("\n");
		var listtarInvoiceInfo = new Array();
		for (var x = 0; x <= cout; x++) {
			var line = {};
			var thisLine = tarInvoiceInfo.split("\n")[x];
			if (thisLine == undefined || thisLine == null || thisLine == "") {
				break;
			}
			var label = thisLine.splitTwoPart(":")[0];
			var text = thisLine.splitTwoPart(":")[1];
			if (label.charAt(0) == " ") {
				text = thisLine;
				label = "";
			}
			
			line.label = label.trim();
			line.text = text.trim();
			listtarInvoiceInfo.push(line);
		}
		$("#txtInvoiceInfo").val(JSON.stringify(listtarInvoiceInfo));
		
		var listProduct = new Array();
		var currencyUom;
		var weightUomId;
		for (var int = 0; int < listSize; int++) {
			var thisProduct = {};
			thisProduct.txtPos = $("#txtPos_" + int).val();
			thisProduct.txtArticleNO = $("#txtArticleNO_" + int).val();
			thisProduct.txtKAR = $("#txtKAR_" + int).val();
			thisProduct.txtQuantity = $("#txtQuantity_" + int).val();
			thisProduct.txtPrice = $("#txtPrice_" + int).val();
			thisProduct.txtPer = $("#txtPer_" + int).val();
			thisProduct.txtVAT = $("#txtVAT_" + int).val();
			thisProduct.txtGoodsValue = $("#txtGoodsValue_" + int).val();
			thisProduct.txtEANNO = $("#txtEANNO_" + int).val();
			thisProduct.txtNetWeight = $("#txtNetWeight_" + int).val();
			thisProduct.txtGrossWeight = $("#txtGrossWeight_" + int).val();
			thisProduct.txtCommodity = $("#txtCommodity_" + int).val();
			thisProduct.lblInternalName = $("#lblInternalName_" + int).val();
			thisProduct.lblQuantityUomId = $("#lblQuantityUomId_" + int).text();
			thisProduct.lblWeightUomId = $("#lblWeightUomId_" + int).text();
			thisProduct.lblCurrencyUom = $("#lblCurrencyUom_" + int).text();
			if (thisProduct.lblWeightUomId == "Kilogram") {
				thisProduct.lblWeightUomId = "KG";
			}
			weightUomId = thisProduct.lblWeightUomId;
			currencyUom = thisProduct.lblCurrencyUom;
			listProduct.push(thisProduct);
		}
		var param =  JSON.stringify(listProduct);
		$("#myList").val(param);
		$("#weightUomId").val(weightUomId);
		$("#currencyUom").val(currencyUom);
		$("button[name='CreatePDF']").hide( "slow", function() {
			$("#CreateInvoiceTotalPDF").submit();
		});
	});
	function txtGoodsValueChange() {
		var total = 0;
		for (var int = 0; int < listSize; int++) {
			var value = $("#txtGoodsValue_" + int).val();
			if (value == "") {
				value = "0";
			}else {
				
			}
			if (value.checkContainValue(",")) {
				value = value.replaceAll(",", ".");
				total += parseFloat(value);
			}else {
				total += parseFloat(value.toDecimal());
			}
		}
		total = total.toString().replaceAll(".", ",");
		$("#lblTotal").val(total.toDecimalFormat());
		$("#lblFinalAmount").val(total.toDecimalFormat());
	}
	function txtNetWeightChange() {
		var total = 0;
		for (var int = 0; int < listSize; int++) {
			var value = $("#txtNetWeight_" + int).val();
			if (value == "") {
				value = "0";
			}
			if (value.checkContainValue(",")) {
				value = value.replaceAll(".", "");
				value = value.replaceAll(",", ".");
				total += parseFloat(value);
			}else {
				total += parseFloat(value.toDecimal());
			}
		}
		total = total.toString().replaceAll(".", ",");
		$("#txtTotalNetWeight").val(total.toDecimalFormat());
	}
	function txtGrossWeightChange() {
		var total = 0;
		for (var int = 0; int < listSize; int++) {
			var value = $("#txtGrossWeight_" + int).val();
			if (value == "") {
				value = "0";
			}else {
			}
			if (value.checkContainValue(",")) {
				value = value.replaceAll(".", "");
				value = value.replaceAll(",", ".");
				total += parseFloat(value);
			}else {
				total += parseFloat(value.toDecimal());
			}
		}
		total = total.toString().replaceAll(".", ",");
		$("#txtTotalGrossWeightt").val(total.toDecimalFormat());
	}
	function txtNoOfPalletChange() {
		var value = $("#txtNoOfPallet").val().toDecimal();
		$("#txtDBTragerpallette").val(value.toString().toDecimalFormat());
	}
	function txtXChange() {
		var txtNoOfPallet = $("#txtNoOfPallet").val().toDecimal();
		if (txtNoOfPallet == "") {
			txtNoOfPallet ="0";
		}
		var txtX = $("#txtX").val().toDecimal();
		var value = txtX.toFloat()*txtNoOfPallet.toFloat();
		$("#txtTotalNoPallet").val(value.toString().toDecimalFormat());
	}
</script>