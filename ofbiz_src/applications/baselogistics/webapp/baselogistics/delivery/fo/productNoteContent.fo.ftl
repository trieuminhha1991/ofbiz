<#escape x as x?xml>
<#assign fullAddress = ""/>
<#assign orderContactMechValueMaps = Static['org.ofbiz.party.contact.ContactMechWorker'].getOrderContactMechValueMaps(delegator, orderId) />
<#list orderContactMechValueMaps as orderContactMechValueMap>
  	<#assign contactMech = orderContactMechValueMap.contactMech>
  	<#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
  	<#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
        <#assign postalAddress = orderContactMechValueMap.postalAddress>
        <#if postalAddress?has_content>
            <#if postalAddress.address1?has_content> 
            	<#assign fullAddress = fullAddress + " " + postalAddress.address1 + ","/> 
        	</#if>
            <#if postalAddress.address2?has_content> 
             	<#assign fullAddress = fullAddress + " " +  postalAddress.address2 + ","/> 
            </#if>
            <#if postalAddress.wardGeoId?has_content>
            	<#if "_NA_" == postalAddress.wardGeoId>
            		<#assign fullAddress = fullAddress + "___, "/> 
	           	<#else>
	           		<#assign wardGeo = delegator.findOne("Geo", {"geoId" : postalAddress.wardGeoId}, true)!/>
	           		<#assign fullAddress = fullAddress + " " + wardGeo?default(postalAddress.wardGeoId).geoName?default(postalAddress.wardGeoId) + ", "/> 
				</#if>
			</#if>
            <#if postalAddress.districtGeoId?has_content>
            	<#if "_NA_" == postalAddress.districtGeoId>
            		<#assign fullAddress = fullAddress + "___, "/> 
	           	<#else>
	            	<#assign districtGeo = delegator.findOne("Geo", {"geoId" : postalAddress.districtGeoId}, true)!/>
	            	<#assign fullAddress = fullAddress + " " + districtGeo?default(postalAddress.districtGeoId).geoName?default(postalAddress.districtGeoId) + ", "/> 
	           	</#if>
			</#if>
            <#if postalAddress.countryGeoId?has_content>
		      	<#assign country = postalAddress.getRelatedOne("CountryGeo", true)>
		      	<#assign fullAddress = fullAddress + " " + country.get("geoName", locale)?default(country.geoId)/>
	    	</#if>
	    	<#if postalAddress.attnName?exists>
	    		<#assign receiverNumber = postalAddress.attnName/>
    		<#else>
	    		<#assign receiverNumber = ""/>
	    	</#if>
        </#if>
        <#break>
    </#if>
</#list>
<#assign partyToContactMechPurposePhones = delegator.findList("PartyContactMechPurpose", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", partyIdTo?if_exists, "contactMechPurposeTypeId", "PHONE_SHIPPING")), null, null, null, false) />
<#if partyToContactMechPurposePhones?has_content && partyToContactMechPurposePhones?length &gt; 0>
	<#assign contactMechShipId = partyToContactMechPurposePhones[0].contactMechId?if_exists>
	<#assign telecomNumberShip = delegator.findList("TelecomNumber", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("contactMechId", contactMechShipId?if_exists)), null, null, null, false) />
</#if>

<fo:block font-size="9" font-family="Arial">
	<fo:block>
		<fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="20px">
		    <fo:table-column/>
			<fo:table-body>
		    	<fo:table-row>
			        <fo:table-cell text-align="center">
			        	<fo:block font-weight="bold" font-size="150%">${StringUtil.wrapString(uiLabelMap.BSListProduct)}</fo:block>
			        </fo:table-cell>
		      	</fo:table-row>
				<fo:table-row>
		    		<fo:table-cell text-align="center">
		    		</fo:table-cell>
				</fo:table-row>
		    </fo:table-body>
		 </fo:table>
	</fo:block>

	<fo:table table-layout="fixed" space-after.optimum="10pt" border="solid 0.3mm black"> 
		<fo:table-column border="solid 0.3mm black" column-width="20pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="100pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="300pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="35pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="35pt"/>
		<fo:table-column border="solid 0.3mm black" column-width="40pt"/>
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
			   							<#--<#assign itemTotal = (item.unitPrice * item.actualExportedQuantity/item.convertNumber)>-->
			   						<#else>
			   							<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExportedQuantity?if_exists, "#,##0", locale)}</fo:block>
			   							<#--<#assign itemTotal = (item.unitPrice * item.actualExportedQuantity)>-->
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

			   </fo:table-row>
		    </#list>

		</fo:table-body>
	</fo:table>
</fo:block>
</#escape>