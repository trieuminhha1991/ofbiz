<#escape x as x?xml>
<fo:block font-size="9" font-family="Arial">
	<fo:block>
		<fo:table table-layout="fixed" space-after.optimum="10pt">
		    <fo:table-column/>
			<fo:table-body>
		    	<fo:table-row>
			        <fo:table-cell text-align="center">
			        	<fo:block font-weight="bold" font-size="150%">${StringUtil.wrapString(uiLabelMap.StockIssueNoteUpperCase)}</fo:block>
			        </fo:table-cell>
		      	</fo:table-row>
		      	<fo:table-row>
		    		<fo:table-cell text-align="center">
		    			<fo:block>${labelInstance?if_exists}</fo:block>
		    		</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
		    		<fo:table-cell text-align="center">
		    		</fo:table-cell>
				</fo:table-row>
		    </fo:table-body>
		 </fo:table>
	</fo:block>
	<fo:block margin-bottom="10px">
		<fo:table table-layout="fixed" space-after.optimum="10pt">
		<fo:table-column column-width="380pt"/>
		<fo:table-column column-width="250pt"/>
		 <fo:table-body>
			<fo:table-row >
				<fo:table-cell>
					<fo:block margin-top="0px">${uiLabelMap.CustomerName}: ${partyTo?if_exists.lastName?if_exists} ${partyTo?if_exists.middleName?if_exists} ${partyTo?if_exists.firstName?if_exists} ${partyTo?if_exists.groupName?if_exists}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block >${uiLabelMap.OrderId}: ${orderIdByDelivery?if_exists}</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell >
					<fo:block margin-top="3px">${uiLabelMap.CustomerId}: ${partyTo.partyCode?if_exists} </fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block margin-top="3px">${StringUtil.wrapString(uiLabelMap.ExportFromFacility)}:
						<#if originFacility.facilityCode?has_content>
							${originFacility.facilityCode?if_exists} - ${originFacility.facilityName?if_exists}
						<#else>
							${originFacility.facilityId?if_exists} - ${originFacility.facilityName?if_exists}
						</#if>
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell >
					<fo:block margin-top="3px">${uiLabelMap.Address}: ${shippingAddress}.</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block margin-top="3px">${uiLabelMap.Saller}: ${partySallerFullName?if_exists}</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell >
					<fo:block margin-top="3px">${uiLabelMap.PhoneNumber}: ${phoneCustomer?if_exists} </fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block margin-top="3px">${uiLabelMap.PhoneNumber}: ${phoneSaller?if_exists} </fo:block>
				</fo:table-cell>
			</fo:table-row>
		 </fo:table-body>
	</fo:table>
	</fo:block>
	<fo:table table-layout="fixed" space-after.optimum="10pt"> 
        <#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW") && allowViewPrice?has_content && allowViewPrice = "1">
            <#assign textLength = 40 />
            <fo:table-column border="solid 0.3mm black" column-width="20pt"/>
            <fo:table-column border="solid 0.2mm black" column-width="52pt"/>
            <fo:table-column border="solid 0.2mm black" column-width="200pt"/>
            <fo:table-column border="solid 0.2mm black" column-width="45pt"/>
            <fo:table-column border="solid 0.2mm black" column-width="50pt"/>
            <fo:table-column border="solid 0.2mm black" column-width="50pt"/>
            <fo:table-column border="solid 0.2mm black" column-width="65pt"/>
            <fo:table-column border="solid 0.2mm black" column-width="70pt"/>
        <#else>
            <#assign textLength = 120 />
            <fo:table-column border="solid 0.3mm black" column-width="20pt"/>
            <fo:table-column border="solid 0.2mm black" column-width="52pt"/>
            <fo:table-column border="solid 0.2mm black" column-width="320pt"/>
            <fo:table-column border="solid 0.2mm black" column-width="45pt"/>
            <fo:table-column border="solid 0.2mm black" column-width="50pt"/>
            <fo:table-column border="solid 0.2mm black" column-width="50pt"/>
        </#if>

		<fo:table-header border="solid 0.2mm black">
          <fo:table-row>
          	<fo:table-cell text-align="center" number-rows-spanned="2">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.SequenceId)}</fo:block>
	        </fo:table-cell>
          	<fo:table-cell text-align="center" number-rows-spanned="2">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.LogMaHang)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center" number-rows-spanned="2">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.LogTenHangHoa)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center" number-rows-spanned="2">
	      	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.UnitSum)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center" number-columns-spanned="2">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.Quantity)}</fo:block>
	        </fo:table-cell>
            <#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW") && allowViewPrice?has_content && allowViewPrice = "1">
                <fo:table-cell text-align="center" number-rows-spanned="2">
                    <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.UnitPrice)}</fo:block>
                  </fo:table-cell>
                <fo:table-cell text-align="center" number-rows-spanned="2">
                    <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.Monetized)}</fo:block>
                </fo:table-cell>
            </#if>
          </fo:table-row>
          <fo:table-row border="solid 0.3mm black">
	          <fo:table-cell text-align="center">
		      		<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.Required)}</fo:block>
		      </fo:table-cell>
		      <fo:table-cell text-align="center">
	      			<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ActualExported)}</fo:block>
      		  </fo:table-cell>
          </fo:table-row>
		</fo:table-header>
		
	   	<fo:table-body>
	   		<#assign valueItemNoPromo = 0>
	   		<#list listItemTmp as item >
	   			<#assign i = i + 1>
		   		<fo:table-row border="solid 0.2mm black">
		   			<fo:table-cell  text-align="left">
				       <fo:block line-height="20px" margin-left="2px"> ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(i?if_exists, "#,##0", locale)}</fo:block>
				   	</fo:table-cell>
				  	<fo:table-cell  text-align="left">
				       <fo:block line-height="20px" margin-left="2px">${item.productCode?if_exists}</fo:block>
				   	</fo:table-cell>
					<fo:table-cell  text-align="left">
						<fo:block line-height="20px" margin-left="2px">
							<#if item.productName?has_content>
				        		<#if item.productName?length &lt; textLength >
					                ${StringUtil.wrapString(item.productName?if_exists)}
					            <#else>
					                ${StringUtil.wrapString(item.productName?substring(0, textLength))} ...
					            </#if>
				        	</#if>
				        	<#if item.selectedAmount?has_content && item.selectedAmount &gt; 0>
				        		[A: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.selectedAmount?if_exists, "#,##0.#", locale)}]
				        	</#if>
						</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="center">
				   		<fo:block line-height="20px">${item.unit?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="right">
		   				<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.quantity?if_exists, "#,##0.#", locale)}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="right">
					   <#if item.actualExportedQuantity?has_content>
					   		<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists, "#,##0.#", locale)}</fo:block>
			        	<#else>
			        		<fo:block></fo:block>
			        	</#if>
				   </fo:table-cell>
				   <#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW") && allowViewPrice?has_content && allowViewPrice = "1">
				   <fo:table-cell  text-align="right">
				       <fo:block line-height="20px" margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitPrice?if_exists, "#,##0.00", locale)}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="right">
					   <#if item.itemTotal?has_content>
				   		<#assign valueItemNoPromo = valueItemNoPromo + item.itemTotal>
					   <fo:block line-height="20px" margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.itemTotal?if_exists, "#,##0.00", locale)}</fo:block>
			        	<#else>
			        		<fo:block></fo:block>
			        	</#if>
				   </fo:table-cell>
				   </#if>
			   </fo:table-row>
		    </#list>
		    <#if hasOlbPermission("MODULE", "PO_PRICE", "VIEW") && allowViewPrice?has_content && allowViewPrice = "1">
		    <#if displayTotal == true>
		    	<#assign totalDiscountPromo = 0>
			    <fo:table-row border="solid 0.2mm black">
			        <fo:table-cell text-align="left" number-columns-spanned="7">
			        	<fo:block line-height="20px" font-weight="bold" margin-left="5px">${uiLabelMap.OrderItemsSubTotal?upper_case}</fo:block>
			        </fo:table-cell>
			        <fo:table-cell text-align="right" number-columns-spanned="1">
				        <#if valueItemNoPromo?has_content>
				        	<#assign totalDiscountPromo = total - valueItemNoPromo>
				        	<fo:block line-height="20px" margin-right="2px" text-align="right" font-weight="bold" margin-left="5px">
				        		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(valueItemNoPromo?if_exists, "#,##0.00", locale)}
				        	</fo:block>
			        	<#else>
			        		<fo:block></fo:block>
			        	</#if>
			        </fo:table-cell>
			    </fo:table-row>
			    <#assign totalAfterPromo = 0>
			    <#if orderAdjustmentsPromo.size() &gt; 0>
			    	<#list orderAdjustmentsPromo as adj>
			    		<#assign totalAfterPromo = totalAfterPromo + adj.amount?if_exists>
		    		</#list>
		    		<#assign totalAfterPromo = totalAfterPromo + totalDiscountPromo?if_exists>
			    </#if>
		     	<fo:table-row border="solid 0.2mm black">
			        <fo:table-cell text-align="left" number-columns-spanned="7">
			        	<fo:block line-height="20px" font-weight="bold" margin-left="5px">${uiLabelMap.BLPromotionTotal?upper_case}</fo:block>
			        </fo:table-cell>
			        <fo:table-cell text-align="right" number-columns-spanned="1">
				        <#if totalAfterPromo?has_content>
				        	<fo:block line-height="20px" margin-right="2px" text-align="right" font-weight="bold" margin-left="5px">
				        		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(totalAfterPromo?if_exists, "#,##0.00", locale)}
				        	</fo:block>
			        	<#else>
			        		<fo:block>0</fo:block>
			        	</#if>
			        </fo:table-cell>
			    </fo:table-row>
			    <fo:table-row border="solid 0.2mm black">
			        <fo:table-cell text-align="left" number-columns-spanned="7">
			        	<fo:block line-height="20px" font-weight="bold" margin-left="5px">${uiLabelMap.BSTotalSalesTax?upper_case}</fo:block>
			        </fo:table-cell>
			        <fo:table-cell text-align="right" number-columns-spanned="1">
				        <#if totalAfterPromo?has_content>
				        	<fo:block line-height="20px" margin-right="2px" text-align="right" font-weight="bold" margin-left="5px">
				        		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxTotalByDelivery?if_exists, "#,##0.00", locale)}
				        	</fo:block>
			        	<#else>
			        		<fo:block>0</fo:block>
			        	</#if>
			        </fo:table-cell>
			    </fo:table-row>
			    <fo:table-row border="solid 0.2mm black">
			        <fo:table-cell text-align="left" number-columns-spanned="7">
			        	<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].ConvertDecimalToString(grandTotal?if_exists)>
			        	<fo:block line-height="20px" font-weight="bold" margin-left="5px">${uiLabelMap.Total?upper_case}: ${totalDlvString?if_exists}</fo:block>
			        </fo:table-cell>
			        <fo:table-cell text-align="right" number-columns-spanned="1">
				        <#if totalAfterPromo?has_content>
				        	<fo:block line-height="20px" margin-right="2px" text-align="right" font-weight="bold" margin-left="5px">
				        	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotal?if_exists, "#,##0.00", locale)}
				        	</fo:block>
			        	<#else>
			        		<fo:block>0</fo:block>
			        	</#if>
			        </fo:table-cell>
			    </fo:table-row>
		    </#if>
		    </#if>
		</fo:table-body>
	</fo:table>
 	<#if displayTotal == true>
		<fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px">
			<fo:table-column column-width="60pt"/>
			<fo:table-column column-width="400pt"/>
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-body>
		 		<fo:table-cell>
		 			<fo:block>${uiLabelMap.Notes}:</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:table table-layout="fixed" space-after.optimum="10pt">
						<fo:table-column column-width="400pt"/>
						<fo:table-body>
							
						</fo:table-body>
					</fo:table>
				</fo:table-cell>
			 </fo:table-body>
		</fo:table>
	</#if>
</fo:block>
</#escape>