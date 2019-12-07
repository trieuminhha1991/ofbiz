<#escape x as x?xml>
<fo:block font-size="9" font-family="Arial">
	<fo:block>
		<fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="20px">
		    <fo:table-column/>
			<fo:table-body>
		    	<fo:table-row>
			        <fo:table-cell text-align="center">
			        	<fo:block font-weight="bold" font-size="150%">${StringUtil.wrapString(uiLabelMap.DeliveryNoteByRequirement)}</fo:block>
			        </fo:table-cell>
		      	</fo:table-row>
		      	<fo:table-row>
		    		<fo:table-cell text-align="center">
		    			<fo:block>${uiLabelMap.SecondInstance}: ${uiLabelMap.FacilitySave}</fo:block>
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
		<fo:table-column column-width="35pt"/>
		<fo:table-column column-width="400pt"/>
		 <fo:table-body>
			<fo:table-row >
				<fo:table-cell>
					<fo:block >${uiLabelMap.RequirementId}: ${delivery.requirementId?if_exists}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block >${uiLabelMap.LogRequirementStartDate}: 
						 ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(requirement.requirementStartDate?if_exists, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!} 
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
				<fo:block margin-top="0px"></fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell>
					<fo:block margin-top="0px"><#if destFacility?has_content> ${uiLabelMap.DestFacility}: ${destFacility.facilityName?if_exists} </#if> </fo:block>
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell>
					<fo:block margin-top="0px"><#if originAddress?has_content> ${uiLabelMap.OriginAddress}: ${originAddress.fullName?if_exists} </#if></fo:block>
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
				<fo:table-cell >
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row >
				<fo:table-cell>
					<fo:block margin-top="0px"><#if destAddress?has_content> ${uiLabelMap.DestAddress}: ${destAddress.fullName?if_exists} </#if></fo:block>
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
		<fo:table-column border="solid 0.3mm black" column-width="80pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="245pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="60pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="80pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="80pt"/>
		<fo:table-header border="solid 0.3mm black">
	      <fo:table-row border="solid 0.3mm black">
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
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.Quantity)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center" border="1px solid">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ExpiredDateSum)}</fo:block>
	        </fo:table-cell>
	      </fo:table-row>
		</fo:table-header>
	   	<fo:table-body>
	   		<#list listItems as item>
		   		<fo:table-row border="solid 0.3mm black">
				  	<fo:table-cell text-align="left">
				  	 	<fo:block line-height="20px" margin-left="2px"> ${item.productCode?if_exists}</fo:block>
				   	</fo:table-cell>
				   	<fo:table-cell text-align="left">
				   	 	<fo:block line-height="20px" margin-left="2px"> ${item.productName?if_exists}</fo:block>
				   	</fo:table-cell>
				   	<fo:table-cell text-align="center">
				   		<fo:block line-height="20px" margin-left="2px" margin-right="2px">
					   		<#assign uomQty = delegator.findOne("Uom", {"uomId" : item.quantityUomId?if_exists}, false)/>
				   			<#assign descPackingUom = StringUtil.wrapString(uomQty.get("description", locale)) />
				   			${descPackingUom?if_exists}
				   		</fo:block>
				   	</fo:table-cell>
				   	<fo:table-cell text-align="right">
					   	<#if item.actualDeliveredQuantity?has_content>
					   		<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualDeliveredQuantity?if_exists, "#,##0", locale)}</fo:block>
			        	<#else>
						   	<#if item.actualExportedQuantity?has_content>
						   		<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists, "#,##0", locale)}</fo:block>
				        	<#else>
				        		<fo:block></fo:block>
				        	</#if>
			        	</#if>
				   	</fo:table-cell>
				   	<fo:table-cell text-align="right">
						<#if item.actualExpireDate?has_content>
							<fo:block line-height="20px" margin-left="2px" margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(item.actualExpireDate?if_exists, "dd/MM/yyyy", locale, timeZone)!}</fo:block>
			        	<#else>
			        		<fo:block></fo:block>
			        	</#if>
				   	</fo:table-cell>
			   </fo:table-row>
		   </#list>
	    </fo:table-body>
	</fo:table>
	<fo:table margin-top="10px" width="545px" table-layout="fixed" space-after.optimum="10pt" font-size="10pt" border="solid 0.3mm black">
		 <fo:table-column border="solid 0.1mm black"/>
		 <fo:table-column border="solid 0.1mm black"/>
		 <fo:table-column border="solid 0.1mm black"/>
		 <fo:table-body>
			<fo:table-row margin-top="10px" border="solid 0.1mm black">
				<fo:table-cell text-align="center">
				    <fo:block font-weight="bold" margin-top="2px">${uiLabelMap.DeliveryCreatedBy}</fo:block>
				    <fo:block font-size="80%">(${uiLabelMap.SignAndKeyFullName})</fo:block>
				</fo:table-cell>	
				<fo:table-cell text-align="center">
				    <fo:block font-weight="bold" margin-top="2px">${uiLabelMap.Storekeeper}</fo:block>
				    <fo:block font-size="80%">(${uiLabelMap.SignAndKeyFullName})</fo:block>
				</fo:table-cell>
				<fo:table-cell text-align="center" >
				    <fo:block font-weight="bold" margin-top="2px">${uiLabelMap.DeliveryMan}</fo:block>
				    <fo:block font-size="80%">(${uiLabelMap.SignAndKeyFullName})</fo:block>
				</fo:table-cell> 
			</fo:table-row>
			<fo:table-row height="60px">
				<fo:table-cell >
				</fo:table-cell>
				<fo:table-cell >
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
			</fo:table-row>
		 </fo:table-body>
	</fo:table>
</fo:block>
</#escape>	