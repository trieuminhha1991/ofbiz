<#assign fullAddress = ""/>
<#assign orderContactMechValueMaps = Static['org.ofbiz.party.contact.ContactMechWorker'].getOrderContactMechValueMaps(delegator, orderId)! />
<#list orderContactMechValueMaps as orderContactMechValueMap>
  	<#assign contactMech = orderContactMechValueMap.contactMech>
  	<#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
  	<#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
        <#assign postalAddress = orderContactMechValueMap.postalAddress>
        <#if postalAddress?has_content>
	    	<#if postalAddress.toName?has_content>
	    		<#assign fullAddress = fullAddress + postalAddress.toName/>
	    		<#if postalAddress.attnName?has_content> 
	    		<#assign fullAddress = fullAddress + "(" +postalAddress.attnName + ")"/>
				</#if>
				<#assign fullAddress = fullAddress + "."/>
    		</#if>
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
		      	<#assign country = postalAddress.getRelatedOne("CountryGeo", true)!>
		      	<#assign fullAddress = fullAddress + " " + country.get("geoName", locale)?default(country.geoId)/>
	    	</#if>
	    	<#if postalAddress.attnName?exists>
	    		<#assign receiverNumber = postalAddress.attnName/>
    		<#else>
	    		<#assign receiverNumber = ''/>
	    	</#if>
        </#if>
    </#if>
</#list>

<fo:block font-size="9" font-family="Arial" margin-top="30px">
	<fo:block margin-bottom="10px">
		<fo:table table-layout="fixed" space-after.optimum="10pt">
			<fo:table-column column-width="150pt"/>
			<fo:table-column column-width="220pt"/>
			 <fo:table-body>
				<fo:table-row >
					<fo:table-cell>
						<fo:block margin-top="0px">${uiLabelMap.CustomerNameSum}: ${partyTo?if_exists.lastName?if_exists} ${partyTo?if_exists.middleName?if_exists} ${partyTo?if_exists.firstName?if_exists} ${partyTo?if_exists.groupName?if_exists}</fo:block>
					</fo:table-cell>
					<fo:table-cell>
						<#if personSale?exists>
							<fo:block>${uiLabelMap.SallerSum}: ${personSale.lastName?if_exists} ${personSale.middleName?if_exists} ${personSale.firstName?if_exists}</fo:block>
						<#else>
							<fo:block>${uiLabelMap.SallerSum}: </fo:block>
						</#if>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row >
					<fo:table-cell >
						<fo:block margin-top="3px">${uiLabelMap.PhoneNumberSum}: 
							<#if receiverNumber?has_content>
								${receiverNumber}
							<#elseif contactNumber?has_content>
								${contactNumber}
							</#if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell >
						<fo:block margin-top="3px">${uiLabelMap.PhoneNumberSum}: 
							<#if contactNumberSales?exists && contactNumberSales != ''>
								${contactNumberSales}
							</#if>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			 </fo:table-body>
		</fo:table>
	</fo:block>
	<fo:block padding="3pt">${uiLabelMap.ShippingAddress}: 
		<fo:inline font-style="italic">
			<#assign orderContactMechValueMaps = Static['org.ofbiz.party.contact.ContactMechWorker'].getOrderContactMechValueMaps(delegator, orderId) />
			<#list orderContactMechValueMaps as orderContactMechValueMap>
		      	<#assign contactMech = orderContactMechValueMap.contactMech>
		      	<#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
		      	<#if contactMech.contactMechTypeId == "POSTAL_ADDRESS">
		            <#assign postalAddress = orderContactMechValueMap.postalAddress>
		            <#if postalAddress?has_content>
				    	<#if postalAddress.toName?has_content>${postalAddress.toName}<#if postalAddress.attnName?has_content> (${postalAddress.attnName})</#if>.</#if>
			            <#if postalAddress.address1?has_content> ${postalAddress.address1}, </#if>
			            <#if postalAddress.address2?has_content> ${postalAddress.address2}, </#if>
			            <#if postalAddress.wardGeoId?has_content>
			            	<#if "_NA_" == postalAddress.wardGeoId>
				            	 ___, 
				           	<#else>
				           		<#assign wardGeo = delegator.findOne("Geo", {"geoId" : postalAddress.wardGeoId}, true)!/>
				            	 ${wardGeo?default(postalAddress.wardGeoId).geoName?default(postalAddress.wardGeoId)}, 
							</#if>
						</#if>
			            <#if postalAddress.districtGeoId?has_content>
			            	<#if "_NA_" == postalAddress.districtGeoId>
				            	 ___, 
				           	<#else>
				            	<#assign districtGeo = delegator.findOne("Geo", {"geoId" : postalAddress.districtGeoId}, true)!/>
				            	 ${districtGeo?default(postalAddress.districtGeoId).geoName?default(postalAddress.districtGeoId)}, 
				           	</#if>
						</#if>
			            <#if postalAddress.city?has_content> ${postalAddress.city}, </#if>
			            <#if postalAddress.countryGeoId?has_content>
					      	<#assign country = postalAddress.getRelatedOne("CountryGeo", true)>
					      	${country.get("geoName", locale)?default(country.geoId)}
				    	</#if>
		            </#if>
		        </#if>
		        <#break>
		    </#list>
		</fo:inline>
	</fo:block>
	<#if shipAfterDate?exists && shipBeforeDate?exists>
		<fo:block padding="3pt">${uiLabelMap.DatetimeDelivery}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipAfterDate?if_exists, "dd/MM/yyyy HH:mm", locale, timeZone)!} - ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipBeforeDate?if_exists, "dd/MM/yyyy HH:mm", locale, timeZone)!}</fo:block>
	<#else>
		<fo:block padding="3pt">${uiLabelMap.DatetimeDelivery}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate?if_exists, "dd/MM/yyyy HH:mm", locale, timeZone)!} - ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate?if_exists, "dd/MM/yyyy HH:mm", locale, timeZone)!}</fo:block>
	</#if>
	<fo:block padding="3pt"><fo:inline> ${StringUtil.wrapString(uiLabelMap.Notes)}: </fo:inline><fo:inline> ${shippingInstructions?if_exists}</fo:inline></fo:block>
</fo:block>