<#escape x as x?xml>
<fo:block font-size="9" margin-top="${margin_top?if_exists}" font-family="Arial">
	 <fo:table table-layout="fixed" space-after.optimum="10pt">
	    <fo:table-column/>
	    <fo:table-column/>
	    <fo:table-column/>
    	<fo:table-body>
	    	<fo:table-row>
	    		<fo:table-cell text-align="center">
	    		</fo:table-cell>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold" font-size="150%">${uiLabelMap.ReceiptNoteUpperCase}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="right">
		        </fo:table-cell>
	      	</fo:table-row>
	      	<fo:table-row>
	    		<fo:table-cell text-align="center">
	    		</fo:table-cell>
	    		<fo:table-cell text-align="center">
	    			<fo:block>${labelInstance}</fo:block>
	    		</fo:table-cell>
	    		<fo:table-cell text-align="left" margin-left="50px">
        		</fo:table-cell>
    		</fo:table-row>
	    </fo:table-body>
	 </fo:table>
	 <fo:table table-layout="fixed" space-after.optimum="5pt">
	 	<fo:table-column column-width="360pt"/>
	 	<fo:table-column column-width="10pt"/>
		<fo:table-column column-width="300pt"/>
	 	<fo:table-body>
	    	<fo:table-row>
	    		<fo:table-cell text-align="left">
	    			<fo:block>${uiLabelMap.Supplier}: ${supplierCode?default(supplierId)} - ${supplierName} </fo:block>
	    		</fo:table-cell>
	    		<fo:table-cell text-align="left">
	    			<fo:block></fo:block>
	    		</fo:table-cell>
	    		<fo:table-cell text-align="left">
	    			<fo:block>${uiLabelMap.OrderId}: ${orderId}</fo:block>
	    		</fo:table-cell>
    		</fo:table-row>
    		<fo:table-row>
	    		<fo:table-cell text-align="left">
	    			<fo:block margin-top="5px">${uiLabelMap.BPOCurrencyUomId}: ${currencyUomId?if_exists}</fo:block>
	    		</fo:table-cell>
	    		<fo:table-cell text-align="left">
	    			<fo:block></fo:block>
	    		</fo:table-cell>
	    		<fo:table-cell text-align="left">
	    			<fo:block margin-top="5px">${uiLabelMap.Orderer}: ${createdBy}</fo:block>
	    		</fo:table-cell>
			</fo:table-row>
			<fo:table-row>
	    		<fo:table-cell text-align="left">
	    			<fo:block margin-top="5px">${uiLabelMap.Address}: 
				    	<#if postalAddress?has_content> ${postalAddress.fullName?if_exists}</#if>
	    			</fo:block>
	    		</fo:table-cell>
	    		<fo:table-cell text-align="left">
	    			<fo:block></fo:block>
	    		</fo:table-cell>
	    		<fo:table-cell text-align="left">
	    			<fo:block margin-top="5px">${uiLabelMap.FacilityToReceive}: ${facilityName}</fo:block>
				</fo:table-cell>
			</fo:table-row>
    	</fo:table-body>	
	</fo:table>
	<fo:table table-layout="fixed" space-after.optimum="10pt"> 
		<fo:table-column border="solid 0.2mm black" column-width="20px"/>
		<fo:table-column border="solid 0.2mm black" column-width="70px"/>
		<fo:table-column border="solid 0.2mm black" column-width="155px"/>
		<fo:table-column border="solid 0.2mm black" column-width="30px"/>
		<fo:table-column border="solid 0.2mm black" column-width="43px"/>
		<fo:table-column border="solid 0.2mm black" column-width="40px"/>
		<fo:table-column border="solid 0.2mm black" column-width="50px"/>
		<fo:table-column border="solid 0.2mm black" column-width="60px"/>
		<fo:table-column border="solid 0.2mm black" column-width="79px"/>
		<fo:table-header border="solid 0.2mm black">
          <fo:table-row border="solid 0.2mm black">
              <fo:table-cell text-align="center">
              	<fo:block line-height="20px" font-weight="bold">${uiLabelMap.SequenceId}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
              	<fo:block line-height="20px" font-weight="bold">${uiLabelMap.LogMaHang}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
              	<fo:block line-height="20px" font-weight="bold">${uiLabelMap.LogTenHangHoa}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
            	<fo:block line-height="20px" font-weight="bold">${uiLabelMap.BLPackingFormSum}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
              	<fo:block line-height="20px" font-weight="bold">${uiLabelMap.BLQuantityByQCUom}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
              	<fo:block line-height="20px" font-weight="bold">${uiLabelMap.BLQuantityByEAUom}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
            	<fo:block line-height="20px" font-weight="bold">${uiLabelMap.BLQuantityEATotal}</fo:block>
        	  </fo:table-cell>
              <fo:table-cell text-align="center">
              	<fo:block line-height="20px" font-weight="bold">${uiLabelMap.UnitPrice}</fo:block>
              </fo:table-cell>
              <fo:table-cell text-align="center">
            	<fo:block line-height="20px" font-weight="bold">${uiLabelMap.Monetized}</fo:block>
        	  </fo:table-cell>
          </fo:table-row>
		</fo:table-header>
	   	<fo:table-body>
	   		<#assign i = 0>
	   		<#list listItemTmp as item >
	   			<#assign i = i + 1>
			   	<fo:table-row border="solid 0.2mm black">
			        <fo:table-cell text-align="left">
			        	<fo:block line-height="20px" margin-left="2px"> ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(i?if_exists, "#,##0", locale)}</fo:block>
			        </fo:table-cell>
			        <fo:table-cell text-align="left">
			        	<fo:block margin-left="2px" line-height="20px" >${item.productId?if_exists}</fo:block>
			        </fo:table-cell>
			        <fo:table-cell text-align="left">
			        	<fo:block margin-left="2px" line-height="20px" >
			        	<#if item.productName?has_content>
			        		<#if item.productName?length &lt; 30>
				                ${StringUtil.wrapString(item.productName?html)}
				            <#else>
				                ${StringUtil.wrapString(item.productName?substring(0, 30))} ...
				            </#if>
			        	</#if>
			        	</fo:block>
			        </fo:table-cell>
			        <fo:table-cell text-align="center">
			        	<fo:block line-height="20px" >${item.convertNumber?if_exists}</fo:block>
			        </fo:table-cell>
			        <fo:table-cell text-align="right">
			        	<#if item.actualDeliveredQuantity == 0>
			        		<fo:block line-height="20px" margin-right="2px">0</fo:block>
			        	<#else>
			        		<#if item.isKg?has_content && 'Y' == item.isKg>
			        			<fo:block line-height="20px" margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.qcQuantity?if_exists, "#,##0.00", locale)}</fo:block>
			        		<#else>
			        			<fo:block line-height="20px" margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.qcQuantity?if_exists, "#,##0", locale)}</fo:block>
			        		</#if>
			        	</#if>
			        </fo:table-cell>
			        <fo:table-cell text-align="right">
			        	<#if item.actualDeliveredQuantity == 0>
			        		<fo:block line-height="20px" margin-right="2px">0</fo:block>
			        	<#else>
			        		<#if item.isKg?has_content && 'Y' == item.isKg>
								<fo:block line-height="20px" margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.eaQuantity?if_exists, "#,##0.00", locale)}</fo:block>			        		
			        		<#else>
			        			<fo:block line-height="20px" margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.eaQuantity?if_exists, "#,##0", locale)}</fo:block>
			        		</#if>
			        	</#if>
			        </fo:table-cell>
			        <fo:table-cell text-align="right">
			        	<#if item.actualDeliveredQuantity == 0>
			        		<fo:block line-height="20px" margin-right="2px">0</fo:block>
			        	<#else>
			        		<#if item.isKg?has_content && 'Y' == item.isKg>
			        			<fo:block line-height="20px" margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredQuantity?if_exists, "#,##0.00", locale)}</fo:block>
			        		<#else>
			        			<fo:block line-height="20px" margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredQuantity?if_exists, "#,##0", locale)}</fo:block>
			        		</#if>
			        	</#if>
			        </fo:table-cell>
			        <fo:table-cell text-align="right">
			        	<#if item.alternativeUnitPrice?has_content>
				        	<fo:block line-height="20px" margin-right="2px">
			        			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.alternativeUnitPrice?if_exists, "#,##0.00", locale)}
			        		</fo:block>
			        	<#elseif item.unitPrice?has_content>
			        		<fo:block line-height="20px" margin-right="2px">
			        			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitPrice?if_exists, "#,##0.00", locale)}
			        		</fo:block>
			        	<#else>
			        		<fo:block line-height="20px" ></fo:block>
			        	</#if>
			        </fo:table-cell>
			        <fo:table-cell text-align="right">
			        	<#if item.total?has_content>
			        		<fo:block line-height="20px" margin-right="2px">
			        			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.total?if_exists, "#,##0.00", locale)}
			        		</fo:block>
			        	<#else>
			        		<fo:block line-height="20px" ></fo:block>
			        	</#if>
			        </fo:table-cell>
			    </fo:table-row>
		    </#list>
		    <#if displayTotal == true>
				<#include "receiptNotePriceTotal.fo.ftl">
			</#if>
		</fo:table-body>
	</fo:table>
</fo:block>
</#escape>