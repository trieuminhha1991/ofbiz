<#escape x as x?xml>
		<fo:block text-align="center" font-size="20pt" padding="15pt">
			${StringUtil.wrapString(uiLabelMap.LogInventoryReport)}
		</fo:block>
		<fo:block font-size="8pt">
			<fo:table border-spacing="1pt">
	            <fo:table-column column-width="30pt"  column-height="200pt"/>
	            <fo:table-column column-width="60pt" column-height="200pt"/>
	            <fo:table-column column-width="70pt" column-height="200pt"/>
	            <fo:table-column column-width="80pt" column-height="200pt"/>
				<fo:table-column column-width="110pt" column-height="200pt"/>
				<fo:table-column column-width="55pt" column-height="200pt"/>
				<fo:table-column column-width="65pt" column-height="200pt"/>
				<fo:table-column column-width="60pt" column-height="200pt"/>
	            <fo:table-header>
	                <fo:table-row font-weight="bold" border="1px solid" font-size="10pt" text-align="center">
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                    	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.SequenceId)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.LogDate)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.LogFacilityName)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ProductCode)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.BPOProductName)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.QuantityOnHandTotal)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.AvailableToPromiseTotal)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.QuantityUomId)}</fo:block>
	                    </fo:table-cell>
	                </fo:table-row>
	            </fo:table-header>
	            <fo:table-body border="1px solid" font-size="10pt">
						<#list listInventotyReport as item>
	                    <fo:table-row border="1px solid" text-align="right">
	                        <fo:table-cell padding="5pt" border="1px solid">
	                            <fo:block>
	                                ${item_index + 1}
	                            </fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>${item.date?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>${item.facilityId?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>${item.productCode?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>${item.productName?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>${item.inventoryTotal?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>${item.availableToPromiseTotal?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>
	                            	<#list listUom as lu>
		                        		<#if item.currencyId == lu.uomId>
		                        			${lu?if_exists.get('description', locale)?if_exists}
		                        		</#if>	
		                        	</#list>
	                           	</fo:block>
	                        </fo:table-cell>
	                    </fo:table-row>
	                    </#list>
	            </fo:table-body>
	        </fo:table>
		</fo:block>
</#escape>