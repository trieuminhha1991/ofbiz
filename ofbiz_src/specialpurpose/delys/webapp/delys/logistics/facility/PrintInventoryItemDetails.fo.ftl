<#escape x as x?xml>
		<fo:block text-align="center" font-size="20pt">
			${StringUtil.wrapString(uiLabelMap.DSPhysicalInventoryItem)}
		</fo:block>
    	<fo:block padding="20pt" text-align="center" font-size="22pt" color="#00CCFF" font-weight="bold" >
			${StringUtil.wrapString(uiLabelMap.DSInventoryItemDetail)}  ${facilityId}  ${StringUtil.wrapString(uiLabelMap.DSInlocation)} ${locationSeqId}
		</fo:block>
		<fo:block font-size="8pt">
			<fo:table border-spacing="3pt">
	            <fo:table-column column-width="30pt"  column-height="200pt"/>
	            <fo:table-column column-width="120pt" column-height="200pt"/>
	            <fo:table-column column-width="120pt" column-height="200pt"/>
	            <fo:table-column column-width="100pt" column-height="200pt"/>
	            <fo:table-column column-width="100pt" column-height="200pt"/>
				<fo:table-column column-width="100pt" column-height="200pt"/>
				
	            <fo:table-header>
	                <fo:table-row font-weight="bold" border="1px solid" font-size="10pt" text-align="center">
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                    	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.DANo)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ProductId)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.DAQuantityOnHandTotal)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.DAAvailableToPromiseTotal)}</fo:block>
	                    </fo:table-cell>
						<fo:table-cell background-color="#D4D0C8" border="1px solid">
							<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.DSReason)}</fo:block>
						</fo:table-cell>
						<fo:table-cell background-color="#D4D0C8" border="1px solid">
							<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.DSQuantity)}</fo:block>
						</fo:table-cell>	
	                </fo:table-row>
	            </fo:table-header>
	            <fo:table-body border="1px solid" font-size="10pt">
						<#list listItems as item>
	                    <fo:table-row border="1px solid" text-align="right">
	                        <fo:table-cell padding="5pt" border="1px solid">
	                            <fo:block>
	                                ${item_index + 1}
	                            </fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>${item.ProductID?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>${item.QuantityOnHandTotal?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>
	                                <fo:block>${item.AvailableToPromiseTotal?if_exists}</fo:block>
	                            </fo:block>
	                        </fo:table-cell>
							<fo:table-cell text-align="right" padding="5pt" border="1px solid">
								<fo:block margin-top="15pt">
									<fo:block></fo:block>
	                            </fo:block>
	                            <fo:block border="0.5px solid">
	                            </fo:block>
	                            <fo:block margin-top="15pt">
	                            	<fo:block></fo:block>
	                            </fo:block>
	                            <fo:block border="0.5px solid">
	                            </fo:block>
	                            <fo:block margin-top="15pt">
	                            	<fo:block></fo:block>
	                            </fo:block>
	                            <fo:block border="0.5px solid">
	                            </fo:block>
	                            <fo:block margin-top="15pt">
	                            	<fo:block></fo:block>
	                            </fo:block>
	                            <fo:block border="0.5px solid">
	                            </fo:block>
	                            <fo:block margin-top="15pt">
	                            	<fo:block></fo:block>
	                            </fo:block>
	                        </fo:table-cell>
							<fo:table-cell text-align="right" padding="5pt" border="1px solid">
								<fo:block margin-top="15pt">
									<fo:block></fo:block>
	                            </fo:block>
	                            <fo:block border="0.5px solid">
	                            </fo:block>
	                            <fo:block margin-top="15pt">
	                            	<fo:block></fo:block>
	                            </fo:block>
	                            <fo:block border="0.5px solid">
	                            </fo:block>
	                            <fo:block margin-top="15pt">
	                            	<fo:block></fo:block>
	                            </fo:block>
	                            <fo:block border="0.5px solid">
	                            </fo:block>
	                            <fo:block margin-top="15pt">
	                            	<fo:block></fo:block>
	                            </fo:block>
	                            <fo:block border="0.5px solid">
	                            </fo:block>
	                            <fo:block margin-top="15pt">
	                            	<fo:block></fo:block>
	                            </fo:block>
	                        </fo:table-cell>
	                    </fo:table-row>
	                    </#list>
	            </fo:table-body>
	        </fo:table>
		</fo:block>
</#escape>