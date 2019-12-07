<#escape x as x?xml>
		<fo:block text-align="center" font-size="20pt" padding="15pt">
			${StringUtil.wrapString(uiLabelMap.LogReportWareHourse)}
		</fo:block>
		<fo:block font-size="8pt">
			<fo:table border-spacing="1pt">
				<fo:table-column column-width="35pt"  column-height="200pt"/>
	            <fo:table-column column-width="80pt" column-height="200pt"/>
	            <fo:table-column column-width="140pt" column-height="200pt"/>
	            <fo:table-column column-width="70pt" column-height="200pt"/>
				<fo:table-column column-width="70pt" column-height="200pt"/>
				<fo:table-column column-width="70pt" column-height="200pt"/>
				<fo:table-column column-width="70pt" column-height="200pt"/>
	            <fo:table-header>
	                <fo:table-row font-weight="bold" border="1px solid" font-size="10pt" text-align="center">
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                    	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.SequenceId)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ReceivedDate)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.BPOProductName)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ExpireDate)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ReceivedQuantity)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.QuantityUomId)}</fo:block>
	                    </fo:table-cell>
	                    <fo:table-cell background-color="#D4D0C8" border="1px solid">
	                        <fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.LogFacilityName)}</fo:block>
	                    </fo:table-cell>
	                </fo:table-row>
	            </fo:table-header>
	            <fo:table-body border="1px solid" font-size="10pt">
	            		<#if listReceiveWarehouseReport?exists>
	            			<#list listReceiveWarehouseReport as item>
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
			                            <fo:block>
			                            	<fo:block>${item.productName?if_exists}</fo:block>
			                            </fo:block>
			                        </fo:table-cell>
			                       	<fo:table-cell text-align="right" padding="5pt" border="1px solid">
			                            <fo:block>${item.expireDate?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
			                            <fo:block>${item.quantityOnHandTotal?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
			                            <fo:block>
			                            	<#list listUom as lu>
				                        		<#if item.uomId == lu.uomId>
				                        			${lu?if_exists.get('description', locale)?if_exists}
				                        		</#if>	
				                        	</#list>
			                           	</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
			                            <fo:block>
			                            	<fo:block>${item.facilityName?if_exists}</fo:block>
			                            </fo:block>
			                        </fo:table-cell>
			                        
			                        <#--
			                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
				                        <fo:block>
			                            	<#if item.enumId?exists>
				                            	<#list listEnumeration as ps>
					                        		<#if item.enumId == ps.enumId>
					                        			 ${ps?if_exists.get('description', locale)?if_exists}
					                        		</#if>
					                        	</#list>
			                            	</#if>
			                            </fo:block>
		                            </fo:table-cell>
			                        <fo:table-cell text-align="right" padding="5pt" border="1px solid">
			                            <fo:block>
			                            	<#if item.categoryId?exists>
				                            	<#list listProductCategory as pc>
					                        		<#if item.categoryId == pc.productCategoryId>
					                        			 ${pc?if_exists.get('categoryName', locale)?if_exists}
					                        		</#if>	
					                        	</#list>
			                            	</#if>
			                            </fo:block>
				                    </fo:table-cell>
				                    -->
			                    </fo:table-row>
		                    </#list>
	            		</#if>
	            </fo:table-body>
	        </fo:table>
		</fo:block>
</#escape>