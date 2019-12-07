<#escape x as x?xml>
	<fo:block text-align="center" font-weight="bold" font-size="14pt">
		${uiLabelMap.cashflowReport}
	</fo:block>
	<fo:block font-weight="bold" text-align="center">(${uiLabelMap.byDirect})</fo:block>
	<fo:block font-weight="bold" text-align="center">${uiLabelMap.at}....${uiLabelMap.date}...${uiLabelMap.month}...${uiLabelMap.year}</fo:block>
	<fo:block font-weight="bold" text-align="right">${uiLabelMap.uom}:...</fo:block>
	
	<fo:table table-layout="fixed" width="100%" margin-top="10px">
        <fo:table-column column-width="40%"/>
        <fo:table-column column-width="10%"/>
        <fo:table-column column-width="14%"/>
        <fo:table-column column-width="18%"/>
        <fo:table-column column-width="18%"/>
        <fo:table-header>
          <fo:table-row>
              <fo:table-cell border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black" text-align="left">
              	<fo:block font-weight="bold" margin-bottom="10px">${uiLabelMap.target}</fo:block>
              </fo:table-cell>
              <fo:table-cell border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black" text-align="center">
              	<fo:block font-weight="bold">${uiLabelMap.code}</fo:block>
              </fo:table-cell>
              <fo:table-cell border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black" text-align="center">
              	<fo:block font-weight="bold">${uiLabelMap.demonstration}</fo:block>
              </fo:table-cell>
              <fo:table-cell border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black" text-align="right">
              	<fo:block font-weight="bold" text-align="right">${context.strCurrentYear}</fo:block>
              </fo:table-cell>
              <fo:table-cell border-bottom-style="solid" border-bottom-width="thin" border-bottom-color="black" text-align="right">
              	<fo:block font-weight="bold" text-align="right">${context.strPreviousYear}</fo:block>
              </fo:table-cell>
          </fo:table-row>
        </fo:table-header>
        <fo:table-body>
        	<fo:table-row>
        		<fo:table-cell><fo:block margin-top="10px"></fo:block></fo:table-cell>
        		<fo:table-cell></fo:table-cell>
        		<fo:table-cell></fo:table-cell>
        		<fo:table-cell></fo:table-cell>
        		<fo:table-cell></fo:table-cell>
        	</fo:table-row>
			<#if listReportData?has_content>
				<#list listReportData as lrd>
					<fo:table-row>
			            <fo:table-cell>
			            	<fo:block margin-top="5px" text-align="left">${lrd.get("name")?if_exists}</fo:block>
			            </fo:table-cell>
			            <fo:table-cell>
			            	<fo:block margin-top="5px" text-align="center">${lrd.get("code")?if_exists}</fo:block>
			            </fo:table-cell>
			            <fo:table-cell>
							<fo:block margin-top="5px" text-align="center">${lrd.get("demonstration")?if_exists}</fo:block>
			            </fo:table-cell>
			            <fo:table-cell>
							<fo:block margin-top="5px" text-align="right">
								<#if lrd.get("value1")?exists><@ofbizCurrency amount=lrd.get("value1")?if_exists isoCode=currencyUomId?if_exists/></#if>
							</fo:block>
			            </fo:table-cell>
			            <fo:table-cell>
							<fo:block margin-top="5px" text-align="right"><#if lrd.get("value2")?exists><@ofbizCurrency amount=lrd.get("value2")?if_exists isoCode=currencyUomId?if_exists/></#if></fo:block>
			            </fo:table-cell>
		            </fo:table-row>
	            </#list>
			</#if>
        </fo:table-body>
    </fo:table>
</#escape>