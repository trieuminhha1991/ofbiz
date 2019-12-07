<#assign resultService = dispatcher.runSync("getInvoiceSpecification", Static["org.ofbiz.base.util.UtilMisc"].toMap("invoiceId", parameters.invoiceId, "userLogin", userLogin))/>
<#assign listProduct = resultService.listProduct/> 
<#assign listProductTax0 = resultService.listProductTax0/>
<#assign listSalesTax = resultService.listSalesTax/>
<#assign listDiscount = resultService.listDiscount/>
<div class="row-fluid">
	<div class="span12" style="text-align: right;">
		<a href="javascript:void(0)" id="exportInvoiceSpecificatioPDF" data-placement="bottom" class="grid-action-button" data-original-title="PDF">
			<i class="fa fa-file-pdf-o"></i>${StringUtil.wrapString(uiLabelMap.BSExportPDF)}
		</a>
	</div>
</div>
<div class="row-fluid">
	<div class="form-horizontal basic-custom-form">
		<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered">
			<thead>
				<tr style="font-weight: bold;">
					<td style="width:20px">${uiLabelMap.BSSTT}</td>
					<td class="align-left" style="width:90px">${uiLabelMap.BACCGCSKU}</td>
					<td align="left" class="align-left">${StringUtil.wrapString(uiLabelMap.BACCProductName2)?upper_case}</td>
					<td align="left" class="align-left" style="width:65px">${StringUtil.wrapString(uiLabelMap.BSCalculateUomId)?upper_case}</td>
					<td align="left" class="align-left" style="width:53px">${StringUtil.wrapString(uiLabelMap.BSQuantity)?upper_case}</td>
				  	<td align="left" class="align-left" style="width:87px">${StringUtil.wrapString(uiLabelMap.BACCUnitPrice)?upper_case}</td>
				  	<td align="left" class="align-left" style="width:106px">${StringUtil.wrapString(uiLabelMap.AmountNotIncludeVAT)?upper_case}</td>
					<td align="left" class="align-left" style="width:45px">${StringUtil.wrapString(uiLabelMap.BACCOnlyVAT)?upper_case}</td>
					<td align="left" class="align-left" style="width:120px">${StringUtil.wrapString(uiLabelMap.AmountIncludeVAT)?upper_case}</td>
				</tr>
			</thead>
			<tbody>
				<#assign totalAmountTax0 = 0/>
                <#assign totalDiscount = 0/>
                <#assign totalAmountNotTax = 0/>
                <#assign totalTaxAmount = 0/>
                <#assign totalAmount = 0/>
                <#assign actualTotalAmountTax0 = 0 />
				<#if listProduct?has_content || listProductTax0?has_content>
					<#list listProduct as tempProduct>
                        <#assign totalAmountNotTax = totalAmountNotTax + tempProduct.amountNotTax/>
                        <#assign totalAmount = totalAmount + tempProduct.amountNotTax/>
						<tr>
							<td>${tempProduct_index + 1}</td>
							<td valign="top">${tempProduct.productCode?if_exists}</td>
							<td valign="top">
								${StringUtil.wrapString(tempProduct.productName?if_exists)}
							</td>
							<td valign="top">
								<#if tempProduct.uomAbbreviation?exists>
									${StringUtil.wrapString(tempProduct.uomAbbreviation)}
								<#else>
									${StringUtil.wrapString(tempProduct.quantityUomId?if_exists)}
								</#if>
							</td>
							<td align="right" class="align-right" valign="top">
								${tempProduct.quantity}
							</td>
							<td align="right" class="align-right" valign="top">
								<@ofbizCurrency amount=tempProduct.amount isoCode=tempProduct.currencyUomId/>
							</td>
							<td align="right" class="align-right" valign="top">
								<@ofbizCurrency amount=tempProduct.amountNotTax isoCode=tempProduct.currencyUomId/>
							</td>
							<td align="right" class="align-right" valign="top">
								${tempProduct.taxPercentage?if_exists}%
							</td>
							<td align="right" class="align-right" valign="top">
								<@ofbizCurrency amount=tempProduct.amountTaxInc isoCode=tempProduct.currencyUomId/>
							</td>
						</tr>
					</#list>

					<#list listProductTax0 as tempProductTax0>
						<#assign totalAmountTax0 = totalAmountTax0 + tempProductTax0.amount*tempProductTax0.quantity/>
						<#assign actualTotalAmountTax0 = actualTotalAmountTax0 + tempProductTax0.amountNotTax>
						<tr>
							<td>${listProduct?size + tempProductTax0_index + 1}</td>
							<td valign="top">${tempProductTax0.productCode}</td>
							<td valign="top">
								${StringUtil.wrapString(tempProductTax0.productName)}
							</td>
							<td valign="top">
								<#if tempProductTax0.uomAbbreviation?exists>
									${StringUtil.wrapString(tempProductTax0.uomAbbreviation)}
								<#else>
									${StringUtil.wrapString(tempProductTax0.quantityUomId ?if_exists)}	
								</#if>
							</td>
							<td align="right" class="align-right" valign="top">
								${tempProductTax0.quantity}
							</td>
							<td align="right" class="align-right" valign="top">
								<#if tempProductTax0.isPromotionalProduct?has_content >
									<@ofbizCurrency amount=0 isoCode=tempProductTax0.currencyUomId/>
								<#else>
									<@ofbizCurrency amount=tempProductTax0.amount isoCode=tempProductTax0.currencyUomId/>
								</#if>								
							</td>
							<td align="right" class="align-right" valign="top">
								<#if tempProductTax0.isPromotionalProduct?has_content>
									<@ofbizCurrency amount=0 isoCode=tempProductTax0.currencyUomId/>
								<#else>
									<@ofbizCurrency amount=tempProductTax0.amount*tempProductTax0.quantity isoCode=tempProductTax0.currencyUomId/>
								</#if>								
							</td>
							<td align="right" class="align-right" valign="top">
								<#if tempProductTax0.taxPercentage?has_content>
									${tempProductTax0.taxPercentage}%
								<#else>
									0%
								</#if>								
							</td>
							<td align="right" class="align-right" valign="top">
								<#if tempProductTax0.isPromotionalProduct?has_content>
									<@ofbizCurrency amount=0 isoCode=tempProductTax0.currencyUomId/>
								<#else>
									<@ofbizCurrency amount=tempProductTax0.amount*tempProductTax0.quantity isoCode=tempProductTax0.currencyUomId/>
								</#if>
							</td>
						</tr>
					</#list>
					
					<#list listDiscount as tempProduct>
                        <#assign totalDiscount = totalDiscount + tempProduct.amountNotTax/>
	                    <tr>
	                        <td>${tempProduct_index + 1}</td>
	                        <td valign="top">${tempProduct.productCode?if_exists}</td>
	                        <td valign="top">
	                        ${StringUtil.wrapString(tempProduct.productName?if_exists)}
	                        </td>
	                        <td valign="top">
	                            <#if tempProduct.uomAbbreviation?exists>
										${StringUtil.wrapString(tempProduct.uomAbbreviation)}
									<#else>
	                            ${StringUtil.wrapString(tempProduct.quantityUomId?if_exists)}
	                            </#if>
	                        </td>
	                        <td align="right" class="align-right" valign="top">
	                        ${tempProduct.quantity}
	                        </td>
	                         <td align="right" class="align-right" valign="top">
                            <#if tempProduct.amount <= 0>
                                (<@ofbizCurrency amount=tempProduct.amount*(-1) isoCode=tempProduct.currencyUomId/>)
                            <#else>
                                <@ofbizCurrency amount=tempProduct.amount isoCode=tempProduct.currencyUomId/>
                            </#if>
                        	</td>	                       
	                        <td align="right" class="align-right" valign="top" colspan="4">
	                            <#if tempProduct.amountTaxInc <= 0>
	                                (<@ofbizCurrency amount=tempProduct.amountTaxInc*(-1) isoCode=tempProduct.currencyUomId/>)
	                            <#else>
	                                <@ofbizCurrency amount=tempProduct.amountTaxInc isoCode=tempProduct.currencyUomId/>
	                            </#if>
	                        </td>
	                    </tr>
                    </#list> 
				<#else>
					<tr>
						<td align="center" class="align-center" valign="top" colspan="9" valign="top">
							<b>${StringUtil.wrapString(uiLabelMap.wgemptydatastring)}</b>
						</td>
					</tr>		
				</#if>
                <tr>
                    <td colspan="2" valign="top"></td>
                    <td valign="top" align="center" class="align-center"><b>${StringUtil.wrapString(uiLabelMap.AmountNotIncludeVAT)}</b></td>
                    <td colspan="4" valign="top" align="center" class="align-center"><b>${StringUtil.wrapString(uiLabelMap.BSAbbValueAddedTax)}</b></td>
                    <td colspan="2" valign="top" align="center" class="align-center"><b>${StringUtil.wrapString(uiLabelMap.AmountIncludeVAT)}</b></td>
                </tr>
                <#if listProductTax0?has_content>
					<#assign totalAmountNotTax = totalAmountNotTax + totalAmountTax0/>
					<#assign totalAmount = totalAmount + totalAmountTax0/>
					<tr>
						<td colspan="2" valign="top" style="border-top: 0px;">
							${StringUtil.wrapString(uiLabelMap.BACCNoVATType)}
						</td>
						<td align="right" class="align-right" valign="top">
							<b><@ofbizCurrency amount=actualTotalAmountTax0 isoCode=listProductTax0.get(0).currencyUomId/></b>
						</td>
						<td align="right" class="align-right" valign="top" colspan="4">
							<b><@ofbizCurrency amount=0 isoCode=listProductTax0.get(0).currencyUomId/></b>
						</td>
						<td align="right" class="align-right" valign="top" colspan="2">
							<b><@ofbizCurrency amount=actualTotalAmountTax0 isoCode=listProductTax0.get(0).currencyUomId/></b>
						</td>
					</tr>
				</#if>
				<#if listSalesTax?has_content>					
					<#list listSalesTax as saleTax>
						<#assign totalTaxAmount = totalTaxAmount + saleTax.totalTaxAmount/>
						<tr>
							<td colspan="2" valign="top" style="border-top: 0px;">
								<#if (saleTax.taxPercentage > 0)>
									${StringUtil.wrapString(uiLabelMap.BACCVATType)} ${saleTax.taxPercentage}%:
								<#else>
                                    <#if saleTax.taxDescription?exists && saleTax.taxDescription.equals("VAT 0%")>
                                        ${StringUtil.wrapString(uiLabelMap.BACCVATType)} ${saleTax.taxPercentage}%:
                                    <#else>
                                        ${StringUtil.wrapString(uiLabelMap.BACCNoVATType)}
                                    </#if>
								</#if>
							</td>
							<td align="right" class="align-right" valign="top">
								<b><@ofbizCurrency amount=saleTax.amountNotTax rounding=2 isoCode=saleTax.currencyUomId/></b>
							</td>
							<td align="right" class="align-right" valign="top" colspan="4">
								<b><@ofbizCurrency amount=saleTax.totalTaxAmount rounding=2 isoCode=saleTax.currencyUomId/></b>
							</td>
							<td align="right" class="align-right" valign="top" colspan="2">
								<b><@ofbizCurrency amount=saleTax.amountTaxInc rounding=2 isoCode=saleTax.currencyUomId/></b>
							</td>
						</tr>
					</#list> 
				</#if>
				<#assign currencyUomId = "VND" />
				<#if listSalesTax?has_content>
					<#assign currencyUomId = listSalesTax.get(0).currencyUomId />
				</#if>
                <#assign totalAmountNotTax = totalAmountNotTax + totalDiscount/>
                <#assign totalAmount = totalAmount + totalDiscount + totalTaxAmount/>
				<tr>
					<td colspan="2" valign="top">
						<b>${StringUtil.wrapString(uiLabelMap.BACCAmountTotal)}:</b>
					</td>
					<td align="right" class="align-right" valign="top">
						<b><@ofbizCurrency amount=totalAmountNotTax isoCode=currencyUomId/></b>
					</td>
					<td align="right" class="align-right" valign="top" colspan="4">
						<b><@ofbizCurrency amount=totalTaxAmount isoCode=currencyUomId/></b>
					</td>
					<td align="right" class="align-right" valign="top" colspan="2">
						<b><@ofbizCurrency amount=totalAmount isoCode=currencyUomId/></b>
					</td>
				</tr>
			</tbody>
		</table>
	</div>
</div>
<script type="text/javascript">
$(document).ready(function () {
	$("#exportInvoiceSpecificatioPDF").click(function(event){
		exportFunction({invoiceId: "${parameters.invoiceId}"}, 'exportInvoiceSpecificationPDF');
	});
});
var exportFunction = function(parameters, url){
	var winURL = url;
	var form = document.createElement("form");
	form.setAttribute("method", "post");
	form.setAttribute("action", winURL);
	form.setAttribute("target", "_blank");
	for(var key in parameters){
		if (parameters.hasOwnProperty(key)) {
			var input = document.createElement('input');
			input.type = 'hidden';
			input.name = key;
			input.value = parameters[key];
			form.appendChild(input);
		}
	}
	document.body.appendChild(form);
	form.submit();  
	document.body.removeChild(form);
};
</script>