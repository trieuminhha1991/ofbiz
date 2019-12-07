<#escape x as x?xml>
		<fo:block text-align="center" font-size="20pt">
			${StringUtil.wrapString(uiLabelMap.DSPhysical)}
		</fo:block>
    	<fo:block padding="20pt" text-align="center" font-size="22pt" color="#00CCFF" font-weight="bold" >
			${StringUtil.wrapString(uiLabelMap.DSPhysicalInventoryItemDetail)}  ${facilityId} 
		</fo:block>
		<fo:block font-size="8pt">
			<fo:table border-spacing="3pt">
	            <fo:table-column column-width="30pt"  column-height="200pt"/>
	            <fo:table-column column-width="60pt" column-height="200pt"/>
	            <fo:table-column column-width="100pt" column-height="200pt"/>
	            <fo:table-column column-width="100pt" column-height="200pt"/>
				<fo:table-column column-width="50pt" column-height="200pt"/>
				<fo:table-column column-width="50pt" column-height="200pt"/>
				<fo:table-column column-width="80pt" column-height="200pt"/>
				<fo:table-column column-width="80pt" column-height="200pt"/>
	            <fo:table-header>
	                <fo:table-row font-weight="bold" border="1px solid" font-size="10pt" text-align="center">
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                    	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.DANo)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ProductLocationSeqId)}</fo:block>
	                    </fo:table-cell>	                    
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ProductProductName)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ExpireDate2)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.QOH)}</fo:block>
	                    </fo:table-cell>	
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ATP)}</fo:block>
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
	                            <fo:block>${item.locationSeqId?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>
	                                <fo:block>${item.internalName?if_exists}</fo:block>
	                            </fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>${item.expireDate?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>
	                                <fo:block>${item.quantityOnHand?if_exists}</fo:block>
	                            </fo:block>
	                        </fo:table-cell>
							<fo:table-cell text-align="right" padding="5pt" border="1px solid">
	                            <fo:block>
	                                <fo:block>${item.availableToPromise?if_exists}</fo:block>
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