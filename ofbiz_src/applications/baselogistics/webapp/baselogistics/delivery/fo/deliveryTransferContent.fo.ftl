<#escape x as x?xml>
<fo:block font-size="9" font-family="Arial">
	<fo:block>
		<fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="20px">
		    <fo:table-column/>
			<fo:table-body>
		    	<fo:table-row>
			        <fo:table-cell text-align="center">
			        	<fo:block font-weight="bold" font-size="150%">${StringUtil.wrapString(uiLabelMap.DeliveryExportAndTransferNote)}</fo:block>
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
		<fo:table-column column-width="45pt"/>
		<fo:table-column column-width="400pt"/>
		 <fo:table-body>
			<fo:table-row >
				<fo:table-cell>
					<fo:block >${uiLabelMap.TransferId}: ${delivery.transferId?if_exists}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block >${uiLabelMap.TransferDate}: 
					</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell>
					<fo:block margin-top="0px">${uiLabelMap.OriginFacility}: ${orginFacility.facilityName?if_exists}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
				<fo:table-cell>
				<fo:block margin-top="0px" color="red"> <#if transfer.shipAfterDate?has_content> ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(transfer.shipAfterDate?if_exists, "dd/MM/yyyy HH:mm", locale, timeZone)!} - ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(transfer.shipBeforeDate?if_exists, "dd/MM/yyyy HH:mm", locale, timeZone)!} </#if> </fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell>
					<fo:block margin-top="0px">${uiLabelMap.DestFacility}: ${destFacility.facilityName?if_exists} </fo:block>
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell>
					<fo:block margin-top="0px">${uiLabelMap.OriginAddress}: ${originAddress.fullName?if_exists}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
				<fo:table-cell >
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell>
					<fo:block margin-top="0px">${uiLabelMap.DestAddress}: ${destAddress.fullName?if_exists}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
				<fo:table-cell >
				</fo:table-cell>
			</fo:table-row>
		 </fo:table-body>
	</fo:table>
	</fo:block>
	<fo:table table-layout="fixed" space-after.optimum="10pt" border="solid 0.3mm black"> 
		<fo:table-column border="solid 0.3mm black" column-width="20pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="80pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="230pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="50pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="65pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="65pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="55pt"/>
		<fo:table-header border="solid 0.3mm black">
     	<fo:table-row border="solid 0.3mm black">
     		<fo:table-cell text-align="center" border="1px solid">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.SequenceId)}</fo:block>
	        </fo:table-cell>
          	<fo:table-cell text-align="center" border="1px solid">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ProductCodeSum)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center" border="1px solid">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ProductName)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center" border="1px solid">
	      	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.Unit)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center" border="1px solid">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ActualDeliveryQuantitySum)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center" border="1px solid">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ActualDeliveredQuantitySum)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center" border="1px solid">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ExpiredDateSum)}</fo:block>
	        </fo:table-cell>
	      </fo:table-row>
		</fo:table-header>
	   	<fo:table-body>
	   		<#assign i = 0>
	   		<#list listItemTmp as item>
	   			<#assign i = i + 1>
		   		<fo:table-row border="solid 0.3mm black">
		   			<fo:table-cell text-align="left">
				       <fo:block line-height="20px" margin-left="2px"> ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(i?if_exists, "#,##0", locale)}</fo:block>
				   	</fo:table-cell>
				  	<fo:table-cell text-align="left">
				  	 	<fo:block line-height="20px" margin-left="2px"> ${item.productCode?if_exists}</fo:block>
				   	</fo:table-cell>
				   	<fo:table-cell text-align="left">
				   	 	<fo:block line-height="20px" margin-left="2px">
				   	 		<#if item.productName?has_content>
				        		<#if item.productName?length &lt; 45>
					                ${StringUtil.wrapString(item.productName?html)}
					            <#else>
					                ${StringUtil.wrapString(item.productName?html?substring(0, 45))} ...
					            </#if>
				        	</#if>
				   	 	</fo:block>
				   	</fo:table-cell>
				   	<fo:table-cell text-align="center">
				   		<fo:block line-height="20px" margin-left="2px" margin-right="2px">
					   		<#assign uomQty = delegator.findOne("Uom", {"uomId" : item.quantityUomId?if_exists}, false)/>
					   		<#if item.requireAmount?has_content && item.requireAmount == 'Y' && item.amountUomTypeId?has_content && item.amountUomTypeId == 'WEIGHT_MEASURE'>
					   			<#assign uomQty = delegator.findOne("Uom", {"uomId" : item.weightUomId?if_exists}, false)/>
					   			<#assign descPackingUom = StringUtil.wrapString(uomQty.get("abbreviation", locale)) />
				   			<#else>
				   				<#assign descPackingUom = StringUtil.wrapString(uomQty.get("description", locale)) />
					   		</#if>
				   			
				   			${descPackingUom?if_exists}
				   		</fo:block>
				   	</fo:table-cell>
				   	<fo:table-cell text-align="right">
				   		<#if item.requireAmount?has_content && item.requireAmount == 'Y' && item.amountUomTypeId?has_content && item.amountUomTypeId == 'WEIGHT_MEASURE'>
						   	<#if item.actualExportedAmount?has_content>
						   		<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedAmount?if_exists, "#,##0.00", locale)}</fo:block>
				        	<#else>
				        		<fo:block margin-right="2px">0</fo:block>
				        	</#if>
			        	<#else>
			        		<#if item.actualExportedQuantity?has_content>
						   		<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists, "#,##0", locale)}</fo:block>
				        	<#else>
				        		<fo:block margin-right="2px">0</fo:block>
				        	</#if>
			        	</#if>
				   	</fo:table-cell>
				   	<fo:table-cell text-align="right">
				   		<#if item.requireAmount?has_content && item.requireAmount == 'Y' && item.amountUomTypeId?has_content && item.amountUomTypeId == 'WEIGHT_MEASURE'>
						   	<#if item.actualDeliveredAmount?has_content>
						   		<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredAmount?if_exists, "#,##0.00", locale)}</fo:block>
				        	<#else>
				        		<fo:block margin-right="2px">0</fo:block>
				        	</#if>
			        	<#else>
			        		<#if item.actualExportedQuantity?has_content>
						   		<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists, "#,##0", locale)}</fo:block>
				        	<#else>
				        		<fo:block margin-right="2px">0</fo:block>
				        	</#if>
			        	</#if>
				   	</fo:table-cell>
				   	<fo:table-cell text-align="right">
						<#if item.actualExpireDate?has_content>
							<fo:block line-height="20px" margin-left="2px" margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(item.actualExpireDate?if_exists, "dd/MM/yyyy", locale, timeZone)!}</fo:block>
			        	<#else>
			        		<fo:block margin-right="2px"></fo:block>
			        	</#if>
				   	</fo:table-cell>
			   </fo:table-row>
		   </#list>
	    </fo:table-body>
	</fo:table>
</fo:block>
</#escape>	