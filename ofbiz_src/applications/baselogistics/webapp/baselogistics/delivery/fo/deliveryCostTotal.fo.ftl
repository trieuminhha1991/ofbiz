<#list listTaxTotals as taxTotalItem>
	<fo:table-row border="solid 0.2mm black">
		<fo:table-cell text-align="left" number-columns-spanned="7">
			<fo:block line-height="20px" margin-left="5px"><#if taxTotalItem.description?exists>${taxTotalItem.description?if_exists}</#if></fo:block>
		</fo:table-cell>
		<fo:table-cell text-align="right">
			<#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
				<fo:block line-height="20px" margin-right="2px"> 
					<#if taxTotalItem.amount?exists && taxTotalItem.amount &lt; 0>
						${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(-taxTotalItem.amount, "#,##0.00", locale)}
					<#elseif taxTotalItem.amount?exists>
						${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxTotalItem.amount, "#,##0.00", locale)}
					</#if>
				</fo:block>
    		<#else>
        		<fo:block line-height="20px" margin-right="2px"> 	
    			</fo:block>
    		</#if>
		</fo:table-cell>
	</fo:table-row>
</#list>
<#list orderAdjustmentsPromo as objAdj>
    <fo:table-row border="solid 0.2mm black">
		<fo:table-cell text-align="left" number-columns-spanned="7">
			<fo:block line-height="20px" margin-left="5px"> 
    			<#if objAdj.promoName?has_content>${StringUtil.wrapString(objAdj.promoName)}</#if>
            </fo:block>
        </fo:table-cell>
    <fo:table-cell text-align="right">
        <fo:block line-height="20px" margin-right="2px"> 
        	<#if objAdj.amount &lt; 0>
			<#assign stringTotalNagative = - objAdj.amount>
				${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(stringTotalNagative, "#,##0.00", locale)}
			<#else>
				${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(objAdj.amount, "#,##0.00", locale)}
			</#if>
        </fo:block>
    </fo:table-cell>
    </fo:table-row>
</#list>
<fo:table-row border="solid 0.2mm black">
    <fo:table-cell text-align="left" number-columns-spanned="7">
    	<fo:block line-height="20px" margin-left="5px" font-weight="bold">${StringUtil.wrapString(uiLabelMap.OrderItemsSubTotal)?upper_case}</fo:block>
    </fo:table-cell>
    <fo:table-cell text-align="right" number-columns-spanned="1">
    		<#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
    			<fo:block line-height="20px" margin-right="2px" font-weight="bold"> 	
    				${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(total, "#,##0.00", locale)}
    			</fo:block>
    		<#else>
        		<fo:block line-height="20px" margin-right="2px"> 	
    			</fo:block>
    		</#if>
    </fo:table-cell>
</fo:table-row>
<#if (taxTotalByDelivery &lt; 0)>
    <fo:table-row border="solid 0.2mm black">
		<fo:table-cell text-align="left" number-columns-spanned="7" font-weight="bold">
			<fo:block line-height="20px" margin-left="5px">${StringUtil.wrapString(uiLabelMap.BSTotalSalesTax)?upper_case} </fo:block>
        </fo:block>
        </fo:table-cell>
        <fo:table-cell text-align="right">
            <#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
                <fo:block line-height="20px" margin-right="2px" font-weight="bold">
                    <#assign taxAmountNegative = -taxTotalByDelivery>
                    (${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxAmountNegative, "#,##0.00", locale)})
                </fo:block>
    		<#else>
        		<fo:block line-height="20px" margin-right="2px"> 	
    			</fo:block>
    		</#if>
        </fo:table-cell>
    </fo:table-row>
<#else>
	<fo:table-row border="solid 0.2mm black">
		<fo:table-cell text-align="left" number-columns-spanned="7" font-weight="bold">
			<fo:block line-height="20px" margin-left="5px">${StringUtil.wrapString(uiLabelMap.BSTotalSalesTax)?upper_case} </fo:block>
        </fo:table-cell>
        <fo:table-cell text-align="right">
            <#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
                <fo:block line-height="20px" margin-right="2px" font-weight="bold"> 
                	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxTotalByDelivery, "#,##0.00", locale)}
                </fo:block>
    		<#else>
        		<fo:block line-height="20px" margin-right="2px"> 	
    			</fo:block>
    		</#if>
        </fo:table-cell>
    </fo:table-row>
</#if>
<#if discountAmountTotal?has_content>
	<fo:table-row border="solid 0.2mm black">
		<fo:table-cell text-align="left" number-columns-spanned="7" font-weight="bold">
			<fo:block line-height="20px" margin-left="5px">${StringUtil.wrapString(uiLabelMap.BSTotalOrderAdjustments)?upper_case} </fo:block>
        </fo:table-cell>
        <fo:table-cell text-align="right">
            <#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
                <fo:block line-height="20px" margin-right="2px" font-weight="bold"> 
                	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(discountAmountTotal, "#,##0.00", locale)}
                </fo:block>
    		<#else>
        		<fo:block line-height="20px" margin-right="2px"> 	
    			</fo:block>
    		</#if>
        </fo:table-cell>
    </fo:table-row>
</#if>
<fo:table-row border="solid 0.2mm black">
    <fo:table-cell text-align="left" number-columns-spanned="7">
    	<fo:block line-height="20px" font-weight="bold" margin-left="5px">${StringUtil.wrapString(uiLabelMap.TotalAmountPay)?upper_case} </fo:block>
    </fo:table-cell>
    <fo:table-cell text-align="right" number-columns-spanned="1">
        <#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
	        <fo:block line-height="20px" margin-right="2px" font-weight="bold"> 	
	        	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotal, "#,##0.00", locale)}
        	</fo:block> 
		<#else>
    		<fo:block line-height="20px" margin-right="2px"> 	
			</fo:block>
		</#if>
    </fo:table-cell>
</fo:table-row>
<fo:table-row border="solid 0.2mm black">
    <fo:table-cell text-align="left" number-columns-spanned="8">
        <#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
        	<#assign abc = Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotal, "#,##0.00", locale)>
	        <#if locale == "vi">
        		<#assign stringTotal = abc.replaceAll("\\.", "")>
        		<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].ConvertDecimalToString(grandTotal)>
        		<fo:block line-height="20px" font-weight="bold" margin-left="5px">${StringUtil.wrapString(uiLabelMap.ByString)?upper_case}: ${totalDlvString?if_exists}</fo:block>
        	<#elseif locale == "en">
        		<#assign stringTotal = abc.replaceAll("\\,", "")>
        		<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].changeToWords(stringTotal, true)>
        		<fo:block line-height="20px" font-weight="bold" margin-left="5px">${StringUtil.wrapString(uiLabelMap.ByString)?upper_case}: ${totalDlvString?if_exists}</fo:block>
        	</#if>
		<#else>
    		<fo:block line-height="20px" margin-right="2px"> 	
			</fo:block>
		</#if>
    </fo:table-cell>
</fo:table-row>