<#escape x as x?xml>
<fo:block text-align="justify">
<#if agreement?exists>
	<#--header top-->
	<fo:block>
		<fo:block text-align="center" text-transform="uppercase" font-weight="700" font-size="16pt" margin-top="0.2cm" margin-bottom="0.2cm">
			${uiLabelMap.DPExhAgreTitlePage} ${uiLabelMap.DPMonth} <#if productPromo?exists && productPromo.fromDate?exists>${productPromo.get("fromDate").getMonth() + 1}/${productPromo.get("fromDate").getYear() + 1900}</#if>
		</fo:block>
		<fo:block font-weight="bold" text-align="center" font-style="italic">
			(${uiLabelMap.DPAbbRegard}: ${regrandTitle?if_exists})
		</fo:block>
		<fo:block margin-top="0.4cm" text-align="center" font-style="italic">
			${uiLabelMap.DPToday}, ${uiLabelMap.DPDayLowercase} ${productPromoDate?default("...")} ${uiLabelMap.DPMonthLowercase} ${productPromoMonth?default("...")} ${uiLabelMap.DPYearLowercase} ${productPromoYear?default("...")} , ${uiLabelMap.DPRepresentTwoSidesInclude}:
		</fo:block>
		<fo:block margin-top="0.2cm" font-weight="bold" text-transform="uppercase">
			${uiLabelMap.DPPartyA}: <#if partyA?exists><#if partyA.groupName?exists>${partyA.groupName}<#elseif partyA.firstName?exists>${partyA.lastName?if_exists} ${partyA.middleName?if_exists} ${partyA.firstName?if_exists}</#if></#if>
		</fo:block>
		<fo:block margin-top="0.2cm">
			<fo:list-block>
				<fo:list-item>
				  	<fo:list-item-label end-indent="label-end()">
				     	<fo:block>-</fo:block>
				  	</fo:list-item-label>
				  	<fo:list-item-body start-indent="body-start()">
				       	<fo:block>${uiLabelMap.DPAddress}: <#if contactAddress?exists>${contactAddress.address1?if_exists}.</#if></fo:block>
				  	</fo:list-item-body>
				</fo:list-item>
				<fo:list-item><#-- space-after="5mm"-->
				  	<fo:list-item-label end-indent="label-end()">
				     	<fo:block>-</fo:block>
				  	</fo:list-item-label>
				  	<fo:list-item-body start-indent="body-start()">
				       	<fo:block>${uiLabelMap.DPByMan}: ${supervisorNameStr?if_exists}. ${uiLabelMap.DPPosition}: ${uiLabelMap.DPSupervisor} ${uiLabelMap.DPDoRepresentation} - ${uiLabelMap.DPAbbPhone}: ........................</fo:block>
				  	</fo:list-item-body>
				</fo:list-item>
			</fo:list-block>
		</fo:block>
		<fo:block margin-top="0.2cm" font-weight="bold">
			<fo:inline text-transform="uppercase">${uiLabelMap.DPPartyB}</fo:inline>: ${uiLabelMap.DPStore}: <#if partyB?exists><#if partyB.groupName?exists>${partyB.groupName}<#elseif partyB.firstName?exists>${partyB.lastName?if_exists} ${partyB.middleName?if_exists} ${partyB.firstName?if_exists}</#if></#if> - ${uiLabelMap.DPAbbPhone}: ........................ 
		</fo:block>
		<fo:block margin-top="0.2cm">
			<fo:list-block>
				<fo:list-item>
				  	<fo:list-item-label end-indent="label-end()">
				     	<fo:block>-</fo:block>
				  	</fo:list-item-label>
				  	<fo:list-item-body start-indent="body-start()">
				       	<fo:block>${uiLabelMap.DPAddress}: <#if contactAddressB?exists>${contactAddressB.address1?if_exists}.</#if></fo:block>
				  	</fo:list-item-body>
				</fo:list-item>
				<fo:list-item><#-- space-after="5mm"-->
				  	<fo:list-item-label end-indent="label-end()">
				     	<fo:block>-</fo:block>
				  	</fo:list-item-label>
				  	<fo:list-item-body start-indent="body-start()">
				       	<fo:block>${uiLabelMap.DPByManFeman}: .................................................. ${uiLabelMap.DPPosition}: .......................................... ${uiLabelMap.DPDoRepresentation}</fo:block>
				  	</fo:list-item-body>
				</fo:list-item>
			</fo:list-block>
		</fo:block>
		<fo:block margin-top="0.2cm" font-style="italic">
			${uiLabelMap.DPWithConvensionContent} ${regrandTitle?if_exists} ${uiLabelMap.DPAsFollows}:
		</fo:block>
		
		<#--<fo:block margin-top="0.2cm">
			${uiLabelMap.DPHeaderTitleContentL2} <fo:inline font-weight="bold">${uiLabelMap.DPHeaderTitleContentL3} </fo:inline>
			<#if productQuotation.fromDate?has_content>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(productQuotation.fromDate, "dd/MM/yyyy", locale, timeZone)!}</#if></fo:inline> ${uiLabelMap.DPHeaderTitleContentL4}:
		</fo:block>-->
		
		<fo:block>
			<fo:block font-weight="bold" text-transform="uppercase" margin-top="0.4cm">${uiLabelMap.DPCondition} I :</fo:block>
			<#if generalExhTerms?exists>
			<fo:list-block><#-- space-after="5mm"-->
				<#list generalExhTerms as generalExhTerm>
				<fo:list-item>
				  	<fo:list-item-label end-indent="label-end()">
				     	<fo:block>-</fo:block>
				  	</fo:list-item-label>
				  	<fo:list-item-body start-indent="body-start()">
				       	<fo:block>${generalExhTerm?if_exists}</fo:block>
				  	</fo:list-item-body>
				</fo:list-item>
				</#list>
			</fo:list-block>
			</#if>
		</fo:block>
	</fo:block>
	
	<#--
	context.generalExhTerms = generalExhTerms;
	context.rightsRreTerms = rightsRreTerms;
	context.partyAResppreTerms = partyAResppreTerms;
	context.partyBResppreTerms = partyBResppreTerms;
	context.partiesResppreTerms = partiesResppreTerms;
	-->
	
	<fo:block>
		<fo:block font-weight="bold" text-transform="uppercase" margin-top="0.4cm">${uiLabelMap.DPCondition} II: ${uiLabelMap.DPBenefit} ${uiLabelMap.DPAnd} ${uiLabelMap.DPResponsibilities}</fo:block>
		<fo:block font-weight="bold" text-transform="uppercase" margin-top="0.2cm" margin-bottom="0.2cm">1/ ${uiLabelMap.DPBenefit}:</fo:block>
		<fo:block>
			<#if rightsRreTerms?exists>
			<fo:list-block>
				<#list rightsRreTerms as rightsRreTerm>
				<fo:list-item>
				  	<fo:list-item-label end-indent="label-end()"><fo:block>-</fo:block></fo:list-item-label>
				  	<fo:list-item-body start-indent="body-start()">
				       	<fo:block>${rightsRreTerm?if_exists}.</fo:block>
				  	</fo:list-item-body>
				</fo:list-item>
				</#list>
			</fo:list-block>
			</#if>
		</fo:block>
		
		<fo:table border-color="black" border-style="solid" border-width="1pt" margin-top="0.2cm">
			<fo:table-column column-width="20%"/>
		    <fo:table-column column-width="40%"/>
		    <fo:table-column column-width="40%"/>
		    <fo:table-header>
		    	<fo:table-row border-color="black">
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-columns-spanned="2">
		            	<fo:block font-weight="bold" text-transform="uppercase">${uiLabelMap.DPExhibitedRequest}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold" text-transform="uppercase">${uiLabelMap.DPExhibitedReward}</fo:block>
		        	</fo:table-cell>
		        </fo:table-row>
		    </fo:table-header>
		    <fo:table-body>
		    	<#if listProductPromoData?exists>
		    	<#list listProductPromoData as productPromoData>
		        <fo:table-row>
		        	<#if productPromoData_index == 0>
		        	<fo:table-cell padding="2mm 2mm" text-align="center" display-align="center" border="1pt solid black" text-transform="uppercase" <#if rowCount?exists>number-rows-spanned="${rowCount}"</#if>>
		                <fo:block font-weight="bold">${uiLabelMap.DPExhibited}</fo:block>
		            </fo:table-cell>
		        	</#if>
		        	<#if productPromoData.condition?exists>
		            <fo:table-cell padding="2mm 2mm" border="1pt solid black"<#if disparityAction?exists && (disparityAction != 0) && (productPromoData_index == (rowCountAction-1))> number-rows-spanned="${disparityAction + 1}"</#if>>
		                <fo:block font-weight="bold">${productPromoData.condition?if_exists}</fo:block>
		            </fo:table-cell>
		            </#if>
		            <#if productPromoData.action?exists>
		            <fo:table-cell padding="2mm 2mm" display-align="center" border="1pt solid black"<#if disparityCond?exists && (disparityCond != 0) && (productPromoData_index == (rowCountCond-1))> number-rows-spanned="${disparityCond + 1}"</#if>>
		                <fo:block font-weight="bold">${productPromoData.action?if_exists}</fo:block>
		            </fo:table-cell>
		            </#if>
		       	</fo:table-row>
		       	</#list>
		       	</#if>
		  	</fo:table-body>
		</fo:table>
		
		<fo:block font-weight="bold" text-transform="uppercase" margin-top="0.4cm">2/ ${uiLabelMap.DPResponsibilities}</fo:block>
		<fo:block font-weight="bold" margin-top="0.2cm">
			a/ ${uiLabelMap.DPPartyA}
		</fo:block>
		<fo:block>
			<#if partyAResppreTerms?exists && partyAResppreTerms?size &gt; 0>
			<fo:list-block><#-- space-after="5mm"-->
				<#list partyAResppreTerms as partyAResppreTerm>
				<fo:list-item>
				  	<fo:list-item-label end-indent="label-end()"><fo:block>-</fo:block></fo:list-item-label>
				  	<fo:list-item-body start-indent="body-start()">
				       	<fo:block>${partyAResppreTerm?if_exists}.</fo:block>
				  	</fo:list-item-body>
				</fo:list-item>
				</#list>
			</fo:list-block>
			</#if>
		</fo:block>
		
		<fo:block font-weight="bold" margin-top="0.2cm">
			b/ ${uiLabelMap.DPPartyB}
		</fo:block>
		<fo:block>
			<#if partyBResppreTerms?exists && partyBResppreTerms?size &gt; 0>
			<fo:list-block><#-- space-after="5mm"-->
				<#list partyBResppreTerms as partyBResppreTerm>
				<fo:list-item>
				  	<fo:list-item-label end-indent="label-end()"><fo:block>-</fo:block></fo:list-item-label>
				  	<fo:list-item-body start-indent="body-start()">
				       	<fo:block>${partyBResppreTerm?if_exists}</fo:block>
				  	</fo:list-item-body>
				</fo:list-item>
				</#list>
			</fo:list-block>
			</#if>
		</fo:block>
		<fo:block font-weight="bold" margin-top="0.2cm">
			<#if partiesResppreTerms?exists && partiesResppreTerms?size &gt; 0>c/ <#list partiesResppreTerms as partiesResppreTerm>${partiesResppreTerm?if_exists}.</#list></#if>
		</fo:block>
	</fo:block>
	
	<fo:block margin-top="0.4cm" font-style="italic">
		${uiLabelMap.DPThisMemorandumShallBeMadeInDuplicateAndShallBeValidFromTheDateOfSigning}.
	</fo:block>
	
	<#--footer-->
	<fo:block margin-top="0.5cm">
		<fo:table>
			<fo:table-column column-width="50%"/>
		    <fo:table-column column-width="50%"/>
		    <fo:table-body>
		        <fo:table-row>
		        	<fo:table-cell padding="1mm" text-align="center">
		                <fo:block></fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="center">
		                <fo:block>${uiLabelMap.DPHaNoi}, ${uiLabelMap.DPDayLowercase} ... ${uiLabelMap.DPMonthLowercase} ... ${uiLabelMap.DPYearLowercase} ...</fo:block>
		            </fo:table-cell>
		       	</fo:table-row>
		       	<fo:table-row>
		            <fo:table-cell padding="1mm" text-align="center">
		                <fo:block text-transform="uppercase" font-weight="bold">${uiLabelMap.DPRepresentation} ${uiLabelMap.DPPartyA}</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="center">
		                <fo:block text-transform="uppercase" font-weight="bold">${uiLabelMap.DPRepresentation} ${uiLabelMap.DPPartyB}</fo:block>
		            </fo:table-cell>
		       	</fo:table-row>
		  	</fo:table-body>
		</fo:table>
		<#--
		<fo:block text-align="right" font-weight="bold" font-style="italic" margin-top="0.2cm">${uiLabelMap.DPHaNoi}, ${uiLabelMap.DPDayLowercase} 
		<#if fromDateDateTime?exists><#if fromDateDateTime.get(5) &lt; 9>0${fromDateDateTime.get(5)}<#else>${fromDateDateTime.get(5)}</#if> ${uiLabelMap.DPMonthLowercase} <#if fromDateDateTime.get(2) &lt; 9>0${fromDateDateTime.get(2) + 1}<#else>${fromDateDateTime.get(2) + 1}</#if> ${uiLabelMap.DPYearLowercase} ${fromDateDateTime.get(1)}
		<#else>... ${uiLabelMap.DPMonthLowercase} ... ${productQuotation.fromDate.getYear()}</#if></fo:block>
		-->
	</fo:block>
<#else>
	<fo:block>
		<fo:block text-align="center" text-transform="uppercase" font-weight="700" font-size="14pt" margin-top="0.2cm" margin-bottom="0.2cm">
			${uiLabelMap.DPExhAgreTitlePage}
		</fo:block>
	</fo:block>
	
	<#--body-->
	<fo:block margin-top="2mm" text-transform="uppercase">
		<fo:block></fo:block>
	</fo:block>
</#if>
</fo:block>
</#escape>