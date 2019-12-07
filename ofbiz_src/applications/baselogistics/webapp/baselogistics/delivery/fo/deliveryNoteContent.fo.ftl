<#escape x as x?xml>
<fo:block font-size="9" font-family="Arial">
	<fo:block>
		<fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="20px">
		    <fo:table-column/>
			<fo:table-body>
		    	<fo:table-row>
			        <fo:table-cell text-align="center">
			        	<fo:block font-weight="bold" font-size="150%">${StringUtil.wrapString(uiLabelMap.DeliveryAndExportNote)}</fo:block>
			        </fo:table-cell>
		      	</fo:table-row>
		      	<fo:table-row>
		    		<fo:table-cell text-align="center">
		    			<fo:block>${labelInstance}</fo:block>
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
			<fo:table-column column-width="320pt"/>
			<fo:table-column column-width="10pt"/>
			<fo:table-column column-width="220pt"/>
			 <fo:table-body>
				<fo:table-row >
					<fo:table-cell>
						<fo:block margin-top="0px">${uiLabelMap.CustomerName}: ${partyTo?if_exists.lastName?if_exists} ${partyTo?if_exists.middleName?if_exists} ${partyTo?if_exists.firstName?if_exists} ${partyTo?if_exists.groupName?if_exists}</fo:block>
					</fo:table-cell>
					<fo:table-cell>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block >${uiLabelMap.OrderId}: ${orderIdByDelivery?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row >
					<fo:table-cell>
						<fo:block margin-top="3px">${uiLabelMap.CustomerId}: 
							<#if partyIdTo?exists && "_NA_" == partyIdTo>
							<#else>
								${partyTo?if_exists.partyCode?if_exists} 
							</#if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
					</fo:table-cell>
					<fo:table-cell>
						<fo:block margin-top="3px">${uiLabelMap.Facility}: ${originFacility.facilityName?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row >
					<fo:table-cell >
						<fo:block margin-top="3px">
							${uiLabelMap.Address}: ${customerAddress?if_exists}
						</fo:block>
					</fo:table-cell>
					<fo:table-cell>
					</fo:table-cell>
					<fo:table-cell >
						<fo:block margin-top="3px">${uiLabelMap.Address}: ${originAddress?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row >
					<fo:table-cell >
						<fo:block margin-top="3px">
							${uiLabelMap.PhoneNumber}: 
							<#if phoneCustomer?has_content>
								${phoneCustomer?if_exists}
							</#if>
				    	</fo:block>
					</fo:table-cell>
					<fo:table-cell>
					</fo:table-cell>
					<fo:table-cell >
						<#if personSale?exists>
							<#if contactNumberSales?has_content>
								<fo:block margin-top="3px">${uiLabelMap.Saller}: ${personSale.lastName?if_exists} ${personSale.middleName?if_exists} ${personSale.firstName?if_exists} - ${contactNumberSales}</fo:block>
							<#else>
								<fo:block margin-top="3px">${uiLabelMap.Saller}: ${personSale.lastName?if_exists} ${personSale.middleName?if_exists} ${personSale.firstName?if_exists}</fo:block>
							</#if>
						<#else>
							<fo:block margin-top="3px">${uiLabelMap.Saller}: </fo:block>
						</#if>
					</fo:table-cell>
				</fo:table-row>
			 </fo:table-body>
		</fo:table>
	</fo:block>

	<fo:table table-layout="fixed" space-after.optimum="10pt" border="solid 0.3mm black"> 

		    <fo:table-column border="solid 0.3mm black" column-width="20pt"/>
            <fo:table-column border="solid 0.3mm black" column-width="72pt"/>
            <fo:table-column border="solid 0.3mm black" column-width="200pt"/>
            <fo:table-column border="solid 0.3mm black" column-width="35pt"/>
            <fo:table-column border="solid 0.3mm black" column-width="45pt"/>
            <fo:table-column border="solid 0.3mm black" column-width="40pt"/>
            <fo:table-column border="solid 0.3mm black" column-width="65pt"/>
            <fo:table-column border="solid 0.3mm black" column-width="75pt"/>

		<fo:table-header border="solid 0.3mm black">
          <fo:table-row border="solid 0.3mm black">
		      	<fo:table-cell text-align="center">
		        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.SequenceId)}</fo:block>
		        </fo:table-cell>
		      	<fo:table-cell text-align="center">
		        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.LogMaHang)}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.LogTenHangHoa)}</fo:block>
		        </fo:table-cell>
	         	<fo:table-cell text-align="center">
	         		<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.BLKM)}</fo:block>
         		</fo:table-cell>
		        <fo:table-cell text-align="center">
		      		<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.UnitSum)}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.QuantitySum)}</fo:block>
		        </fo:table-cell>
		        <fo:table-cell text-align="center">
		      		<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.UnitPrice)}</fo:block>
		        </fo:table-cell>
	         	<fo:table-cell text-align="center">
	         		<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ApTotal)}</fo:block>
	         	</fo:table-cell>
          </fo:table-row>
		</fo:table-header>
	   	<fo:table-body>
		    <#list listItemTmp as item >
		    	<#assign i = i + 1>
		   		<fo:table-row border="solid 0.3mm black">
				  	<fo:table-cell  text-align="left">
				       <fo:block line-height="20px" margin-left="2px"> ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(i?if_exists, "#,##0", locale)}</fo:block>
				   	</fo:table-cell>
				  	<fo:table-cell  text-align="left">
				       <fo:block line-height="20px" margin-left="2px">${item.productCode?if_exists}</fo:block>
				   	</fo:table-cell>
					<fo:table-cell  text-align="left">
						<fo:block line-height="20px" margin-left="2px">
							<#if item.productName?has_content>
				        		<#if item.productName?length &lt; 45>
					                ${StringUtil.wrapString(item.productName)}
					            <#else>
					                ${StringUtil.wrapString(item.productName?substring(0, 45))} ...
					            </#if>
				        	</#if>
				        	<#if item.selectedAmount?has_content && item.selectedAmount &gt; 0>
				        		[A: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.selectedAmount?if_exists, "#,##0.#", locale)}]
				        	</#if>
						</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="center">
				       <fo:block line-height="20px" margin-left="2px">
				       		<#if item.isPromo?exists && item.isPromo == 'Y'>
				       			${uiLabelMap.LogYes}
				       		<#else>
				       			${uiLabelMap.LogNO}
				       		</#if>
				       </fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="center">
				   		<fo:block line-height="20px">${item.unit?if_exists}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="right">
		   				<#if item.actualExportedQuantity?has_content>
		   					<#if item.actualExportedQuantity &gt; 0>
			   					<#if item.requireAmount?has_content && item.requireAmount == 'Y'>
		   							<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists, "#,##0.00", locale)}</fo:block>
			   						<#assign itemTotal = item.unitPrice>
			   					<#else>
			   						<#if item.convertNumber?exists>
			   							<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists/item.convertNumber?if_exists, "#,##0", locale)}</fo:block>
			   							<#assign itemTotal = (item.unitPrice * item.actualExportedQuantity/item.convertNumber)>
			   						<#else>
			   							<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists, "#,##0", locale)}</fo:block>
			   							<#assign itemTotal = (item.unitPrice * item.actualExportedQuantity)>
			   						</#if>
			   					</#if>
		   					<#else>
		   						<#if "DLV_EXPORTED" == statusId || "DLV_DELIVERED" == statusId>
		   							<fo:block>0</fo:block>
		   						<#else>
		   							<fo:block></fo:block>
		   						</#if>
		   					</#if>
			        	<#else>
			        		<fo:block></fo:block>
			        	</#if>
				   </fo:table-cell>
                       <fo:table-cell  text-align="right">
                           <fo:block line-height="20px" margin-right="2px">
                            <#if item.unitPrice?exists>
                                ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitPrice?if_exists, "#,##0.00", locale)}
                            </#if>
                           </fo:block>
                       </fo:table-cell>
                       <fo:table-cell  text-align="right">
                           <#if itemTotal?has_content>
                                <fo:block line-height="20px" margin-right="2px">
                                    ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemTotal?if_exists, "#,##0.00", locale)}
                                </fo:block>
                            <#else>
                                <fo:block></fo:block>
                            </#if>
                       </fo:table-cell>
			   </fo:table-row>
		    </#list>
			<#if displayTotal == true>
				    <#include "deliveryCostTotal.fo.ftl">
			</#if>
		</fo:table-body>
	</fo:table>
	<#if displayTotal == true>
		<#include "deliveryInfoTotal.fo.ftl">
	</#if>
</fo:block>
</#escape>