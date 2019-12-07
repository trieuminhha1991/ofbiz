<#escape x as x?xml>
		<fo:block text-align="center" font-size="20pt" padding="15pt">
			${StringUtil.wrapString(uiLabelMap.ReportTransfer)}
		</fo:block>
		<fo:block font-size="8pt">
			<fo:table border-spacing="1pt">
				<fo:table-column column-width="35pt"  column-height="200pt"/>
	            <fo:table-column column-width="60pt" column-height="200pt"/>
	            <fo:table-column column-width="50pt" column-height="200pt"/>
	            <fo:table-column column-width="120pt" column-height="200pt"/>
	            <fo:table-column column-width="80pt" column-height="200pt"/>
				<fo:table-column column-width="80pt" column-height="200pt"/>
				<fo:table-column column-width="60pt" column-height="200pt"/>
				<fo:table-column column-width="50pt" column-height="200pt"/>
	            <fo:table-header>
	                <fo:table-row font-weight="bold" border="1px solid" font-size="10pt" text-align="center">
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                    	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.SequenceId)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.RequiredByDate)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.TransferId)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.BPOProductName)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.FacilityFrom)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.FacilityTo)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.RequiredNumber)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.QuantityUomId)}</fo:block>
	                    </fo:table-cell>
	                </fo:table-row>
	            </fo:table-header>
	            <fo:table-body border="1px solid" font-size="10pt">
	            		<#if listTransferItemReport?exists>
	            			<#list listTransferItemReport as item>
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
			                            <fo:block>${item.transferId?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
			                            <fo:block>${item.productName?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell text-align="left" padding="5pt" border="1px solid">
			                            <fo:block>${item.originFacilityName?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
			                            <fo:block>${item.destFacilityName?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
			                        	<fo:block>${item.quantity?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
			                            <fo:block>${item.uomId?if_exists}</fo:block>
			                        </fo:table-cell>
			                    </fo:table-row>
		                    </#list>
	            		</#if>
	            </fo:table-body>
	        </fo:table>
		</fo:block>
</#escape>