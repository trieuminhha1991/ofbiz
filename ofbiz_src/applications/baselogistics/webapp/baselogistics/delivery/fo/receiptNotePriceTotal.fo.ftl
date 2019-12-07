<fo:table-row border="solid 0.2mm black">
    <fo:table-cell text-align="left" number-columns-spanned="8">
    	<fo:block line-height="20px" margin-left="5px">${uiLabelMap.OrderItemsSubTotal}</fo:block>
    </fo:table-cell>
    <fo:table-cell text-align="right">
		<fo:block line-height="20px" margin-right="2px"> 	
			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemPriceTotal?if_exists, "#,##0.00", locale)}
		</fo:block>
    </fo:table-cell>
</fo:table-row>
<#assign grandTotalByDelivery = itemPriceTotal + taxTotalByDelivery>

<#if allOrderAdjustmentsPromoDelivery?has_content>
	<#list allOrderAdjustmentsPromoDelivery as objAdj>
		<#assign grandTotalByDelivery = grandTotalByDelivery + objAdj.amount?if_exists>
		<fo:table-row border="solid 0.2mm black">
			<fo:table-cell text-align="left" number-columns-spanned="8">
				<fo:block line-height="20px" margin-left="5px">${StringUtil.wrapString(objAdj.promoName)}</fo:block>
			</fo:table-cell>
			<fo:table-cell text-align="right">
				<fo:block line-height="20px" margin-right="2px"> ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(objAdj.amount, "#,##0.00", locale)}</fo:block>
			</fo:table-cell>
		</fo:table-row>
	</#list>	
</#if>

<#list listTaxTotals as taxTotalItem>
	<fo:table-row border="solid 0.2mm black">
		<fo:table-cell text-align="left" number-columns-spanned="8">
			<fo:block line-height="20px" margin-left="5px"><#if taxTotalItem.description?exists>${taxTotalItem.description?if_exists}</#if></fo:block>
		</fo:table-cell>
		<fo:table-cell text-align="right">
			<fo:block line-height="20px" margin-right="2px"> 
				<#if taxTotalItem.amount?exists && taxTotalItem.amount &lt; 0>
					${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(-taxTotalItem.amount, "#,##0.00", locale)}
				<#elseif taxTotalItem.amount?exists>
					${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxTotalItem.amount, "#,##0.00", locale)}
				</#if>
			</fo:block>
		</fo:table-cell>
	</fo:table-row>
</#list>
<#if taxDiscountTotal?has_content && taxDiscountTotal != 0>
	<#assign grandTotalByDelivery = grandTotalByDelivery + taxDiscountTotal>
	<fo:table-row border="solid 0.2mm black">
		<fo:table-cell text-align="left" number-columns-spanned="8">
			<fo:block line-height="20px" margin-left="5px"> ${StringUtil.wrapString(uiLabelMap.BPTaxDiscountTotal)} </fo:block>
		</fo:table-cell>
		<fo:table-cell text-align="right">
			<fo:block line-height="20px" margin-right="2px">  ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxDiscountTotal, "#,##0.00", locale)} </fo:block>
		</fo:table-cell>
	</fo:table-row>
</#if>
<fo:table-row border="solid 0.2mm black">
    <fo:table-cell text-align="left" number-columns-spanned="8">
    	<fo:block line-height="20px" font-weight="bold" margin-left="5px">${uiLabelMap.TotalAmountPay}: </fo:block>
    </fo:table-cell>
    <fo:table-cell text-align="right">
        <fo:block line-height="20px" margin-right="2px"> 	
        	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotalByDelivery, "#,##0.00", locale)} (${currencyUomId?if_exists})
    	</fo:block> 
    </fo:table-cell>
</fo:table-row>
<fo:table-row border="solid 0.2mm black">
    <fo:table-cell text-align="left" number-columns-spanned="9">
    	<#assign abc = Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotalByDelivery?if_exists, "#,##0.00", locale)>
        <#if locale == "vi">
    		<#assign stringTotal = abc.replaceAll("\\.", "")>
    		<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].ConvertDecimalToString(grandTotalByDelivery?if_exists, currencyUomId?if_exists, delegator)>
    		<fo:block line-height="20px" font-weight="bold" margin-left="5px">${uiLabelMap.ByString}: ${totalDlvString?if_exists}</fo:block>
    	<#elseif locale == "en">
    		<#assign stringTotal = abc.replaceAll("\\,", "")>
    		<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].changeToWords(stringTotal, true)>
    		<fo:block line-height="20px" font-weight="bold" margin-left="5px">${uiLabelMap.ByString}: ${totalDlvString?if_exists}</fo:block>
    	</#if>
    </fo:table-cell>
</fo:table-row>