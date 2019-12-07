<#escape x as x?xml>
	<fo:block font-size="11" font-family="Times">
		<fo:table table-layout="fixed">
			<fo:table-column column-width="70%"/>
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell text-align="left">
						<#assign partyNameTo = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, payment.partyIdTo?if_exists, true, true)?if_exists>
	    				<#assign fullAddressTo = ""/>
	    				<#if partyAddressTo.address1?has_content> 
			            	<#assign fullAddressTo = fullAddressTo + " " + partyAddressTo.address1 + ","/> 
		            	</#if>
	    				<#if partyAddressTo.address2?has_content> 
			             	<#assign fullAddressTo = fullAddressTo + " " +  partyAddressTo.address2 + ","/> 
			            </#if>
			            <#if partyAddressTo.wardGeoId?has_content>
			            	<#if "_NA_" == partyAddressTo.wardGeoId>
			            		<#assign fullAddressTo = fullAddressTo + "___, "/> 
				           	<#else>
				           		<#assign wardGeo = delegator.findOne("Geo", {"geoId" : partyAddressTo.wardGeoId}, true)!/>
				           		<#assign fullAddressTo = fullAddressTo + " " + wardGeo?default(partyAddressTo.wardGeoId).geoName?default(partyAddressTo.wardGeoId) + ", "/> 
							</#if>
						</#if>
			            <#if partyAddressTo.districtGeoId?has_content>
			            	<#if "_NA_" == partyAddressTo.districtGeoId>
			            		<#assign fullAddressTo = fullAddressTo + "___, "/> 
				           	<#else>
				            	<#assign districtGeo = delegator.findOne("Geo", {"geoId" : partyAddressTo.districtGeoId}, true)!/>
				            	<#assign fullAddressTo = fullAddressTo + " " + districtGeo?default(partyAddressTo.districtGeoId).geoName?default(partyAddressTo.districtGeoId) + ", "/> 
				           	</#if>
						</#if>
			            <#if partyAddressTo.stateProvinceGeoId?has_content>
			            	<#if "_NA_" == partyAddressTo.stateProvinceGeoId>
			            		<#assign fullAddressTo = fullAddressTo + "___, "/> 
				           	<#else>
				            	<#assign provinceGeo = delegator.findOne("Geo", {"geoId" : partyAddressTo.stateProvinceGeoId}, true)!/>
				            	<#assign fullAddressTo = fullAddressTo + " " + provinceGeo?default(partyAddressTo.districtGeoId).geoName?default(partyAddressTo.stateProvinceGeoId) /> 
				           	</#if>
						</#if>
						<fo:block font-weight="bold" font-size="10">${partyNameTo?if_exists}</fo:block>
						<fo:block font-style="italic" font-size="10">${fullAddressTo?if_exists}</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left">
						<fo:block margin-left="25px">${StringUtil.wrapString(uiLabelMap.BACCVoucherNumber)}: <#if payment.paymentCode?exists>${payment.paymentCode}<#else>${parameters.paymentId?if_exists}</#if></fo:block>
						<fo:block margin-left="25px">${StringUtil.wrapString(uiLabelMap.BACCDEBIT)}:.........../${StringUtil.wrapString(uiLabelMap.BACCCREDIT)}:...........</fo:block>	
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table> 
		<fo:table table-layout="fixed" margin-top="10px">
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell text-align="center">
						<fo:block font-weight="bold" font-size="160%" text-transform="uppercase">${StringUtil.wrapString(uiLabelMap.BACCReceiptVoucher)}</fo:block>						
						<fo:block font-style="italic"> ${StringUtil.wrapString(uiLabelMap.BACCDay)} ${payment.effectiveDate?string["dd"]} ${StringUtil.wrapString(uiLabelMap.BACCMonth)} ${payment.effectiveDate?string["MM"]} ${StringUtil.wrapString(uiLabelMap.BACCYear)} ${payment.effectiveDate?string["yyyy"]}</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="10px" width="100%">
			<fo:table-column column-width="20%"/>
			<fo:table-column column-width="20%"/>
			<fo:table-column column-width="10%"/>
			<fo:table-column column-width="15%"/>
			<fo:table-column column-width="8%"/>
			<fo:table-column column-width="10%"/>
			<fo:table-body>
				<fo:table-row height="18px">
					<fo:table-cell text-align="left">
						<fo:block>${StringUtil.wrapString(uiLabelMap.BACCFullNamePayer)}:</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left" number-columns-spanned="5">
						<fo:block text-transform="uppercase">${payment.organizationName?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="18px">
					<fo:table-cell text-align="left">
						<fo:block>${StringUtil.wrapString(uiLabelMap.BACCOrganization)}:</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left" number-columns-spanned="5">
						<#assign partyNameFrom = Static["com.olbius.basehr.util.PartyHelper"].getPartyName(delegator, payment.partyIdFrom?if_exists, true, true)?if_exists>
						<fo:block text-transform="uppercase">${partyNameFrom?if_exists}</fo:block>											
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="18px">
					<fo:table-cell text-align="left">
						<fo:block>${StringUtil.wrapString(uiLabelMap.CommonAddress1)}:</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left" number-columns-spanned="5">
	    				<#assign fullAddress = ""/>
	    				<#if partyAddressFrom.address1?has_content> 
			            	<#assign fullAddress = fullAddress + " " + partyAddressFrom.address1 + ","/> 
		            	</#if>
	    				<#if partyAddressFrom.address2?has_content> 
			             	<#assign fullAddress = fullAddress + " " +  partyAddressFrom.address2 + ","/> 
			            </#if>
			            <#if partyAddressFrom.wardGeoId?has_content>
			            	<#if "_NA_" == partyAddressFrom.wardGeoId>
			            		<#assign fullAddress = fullAddress + "___, "/> 
				           	<#else>
				           		<#assign wardGeo = delegator.findOne("Geo", {"geoId" : partyAddressFrom.wardGeoId}, true)!/>
				           		<#assign fullAddress = fullAddress + " " + wardGeo?default(partyAddressFrom.wardGeoId).geoName?default(partyAddressFrom.wardGeoId) + ", "/> 
							</#if>
						</#if>
			            <#if partyAddressFrom.districtGeoId?has_content>
			            	<#if "_NA_" == partyAddressFrom.districtGeoId>
			            		<#assign fullAddress = fullAddress + "___, "/> 
				           	<#else>
				            	<#assign districtGeo = delegator.findOne("Geo", {"geoId" : partyAddressFrom.districtGeoId}, true)!/>
				            	<#assign fullAddress = fullAddress + " " + districtGeo?default(partyAddressFrom.districtGeoId).geoName?default(partyAddressFrom.districtGeoId) + ", "/> 
				           	</#if>
						</#if>
			            <#if partyAddressFrom.stateProvinceGeoId?has_content>
			            	<#if "_NA_" == partyAddressFrom.stateProvinceGeoId>
			            		<#assign fullAddress = fullAddress + "___, "/> 
				           	<#else>
				            	<#assign provinceGeo = delegator.findOne("Geo", {"geoId" : partyAddressFrom.stateProvinceGeoId}, true)!/>
				            	<#assign fullAddress = fullAddress + " " + provinceGeo?default(partyAddressFrom.districtGeoId).geoName?default(partyAddressFrom.stateProvinceGeoId)/> 
				           	</#if>
						</#if>
						<fo:block>${fullAddress?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="18px">
					<fo:table-cell text-align="left">
						<fo:block>${StringUtil.wrapString(uiLabelMap.IdentifyCard)}:</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left">
						<fo:block>${payment.identifyCard?if_exists}</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left">
						<fo:block>${StringUtil.wrapString(uiLabelMap.IssuedDatePDF)}:</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left">
						<fo:block>
							<#if payment.issuedDate?exists>
								${payment.issuedDate?string["dd/MM/yyyy"]}
							</#if>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left">
						<fo:block>${StringUtil.wrapString(uiLabelMap.IssuePlacePDF)}:</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left">
						<fo:block>
							${payment.issuedPlace?if_exists}
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="18px">
					<fo:table-cell text-align="left">
						<fo:block>${StringUtil.wrapString(uiLabelMap.BACCReasonFiling)}:</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left" number-columns-spanned="5">
						<fo:block>${payment.comments?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="18px">
					<fo:table-cell text-align="left">
						<fo:block>${StringUtil.wrapString(uiLabelMap.BACCAmount)}:</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left" number-columns-spanned="5">
					<#if payment?exists>
					<#assign amount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(payment.amount, payment.currencyUomId?if_exists, locale)>
					</#if>
						<fo:block >${amount?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="18px">
					<fo:table-cell text-align="left">
					<#if payment?exists>
					<#assign amountInWords = Static["com.olbius.acc.utils.UtilServices"].countNumberMoney("${payment.amount?string?if_exists}")/>
					</#if>
						<fo:block>${StringUtil.wrapString(uiLabelMap.BACCWriteInWords)}:</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left" number-columns-spanned="5">
						<fo:block font-weight="bold">${amountInWords?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="18px">
					<fo:table-cell text-align="left">
						<fo:block>
							<#assign listInvoices = delegator.findByAnd("PaymentApplication", {"paymentId" : payment.paymentId}, null, false)>
							<fo:inline>${StringUtil.wrapString(uiLabelMap.BACCAttach)}:</fo:inline>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="left" number-columns-spanned="5">
						<fo:block>
							<#list listInvoices as item>
								<fo:inline font-weight="bold">${item.invoiceId}</fo:inline>
							</#list>
							${StringUtil.wrapString(uiLabelMap.BACCOriginalVouchers)}
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<fo:table table-layout="fixed" margin-top="10px">
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row >
				<fo:table-cell width="100px">
				<fo:block font-size="90%" font-weight="bold" text-transform="uppercase" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCEstablished)}</fo:block> 
				</fo:table-cell>
				<fo:table-cell width="100px">
					<fo:block font-size="90%" font-weight="bold" text-transform="uppercase" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCPayers)}</fo:block> 
				</fo:table-cell>
				<fo:table-cell width="90px">
					<fo:block font-size="90%" font-weight="bold" text-transform="uppercase" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCCAshier)}</fo:block> 
				</fo:table-cell>
				<fo:table-cell width="110px">
					<fo:block font-size="90%" font-weight="bold" text-transform="uppercase" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCChiefAccount)}</fo:block> 
				</fo:table-cell>
				<fo:table-cell width="110px">
					<fo:block font-size="90%" font-weight="bold" text-transform="uppercase" text-align="center">${StringUtil.wrapString(uiLabelMap.BACCDirector)}</fo:block>
				</fo:table-cell>
				</fo:table-row>
				<fo:table-row >
					<fo:table-cell >
						<fo:block text-align="center" >(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center">
						<fo:block>(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center">
						<fo:block>(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center">
						<fo:block>(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
					</fo:table-cell>
					<fo:table-cell text-align="center">
						<fo:block>(${StringUtil.wrapString(uiLabelMap.BACCSignatureFullName)})</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
		<fo:table table-layout="fixed" space-after.optimum="10pt" margin-top="50px">
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell text-align="left">
						<fo:block>
							<fo:inline>--------------------------------------------------------------------------------------------------------------------------------------------</fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell text-align="left">
						<fo:block>
							<fo:inline font-size="100%" text-transform="uppercase" >SGC-QF-04/KT-10/02</fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</fo:block>
</#escape>