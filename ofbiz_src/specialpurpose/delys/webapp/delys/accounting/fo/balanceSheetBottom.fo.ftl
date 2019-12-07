<#escape x as x?xml>
	<fo:block text-align="right" font-weight="bold" font-size="14pt" margin-top="10px">
		${uiLabelMap.date}...${uiLabelMap.month}...${uiLabelMap.year}...
	</fo:block>
	<fo:table table-layout="fixed" width="100%" margin-top="10px">
        <fo:table-column column-width="33%"/>
        <fo:table-column column-width="33%"/>
        <fo:table-column column-width="33%"/>
        <fo:table-body>
          <fo:table-row>
            <fo:table-cell>
              <fo:block font-weight="bold" text-align="center">${uiLabelMap.reporter}</fo:block>
			  <fo:block font-weight="bold" text-align="center">${uiLabelMap.signAndName}</fo:block>
            </fo:table-cell>
            <fo:table-cell>
              <fo:block font-weight="bold" text-align="center">${uiLabelMap.chiefAccount}</fo:block>
			  <fo:block font-weight="bold" text-align="center">${uiLabelMap.signAndName}</fo:block>
            </fo:table-cell>
            <fo:table-cell>
				<fo:block font-weight="bold" text-align="center">${uiLabelMap.director}</fo:block>
				<fo:block font-weight="bold" text-align="center">${uiLabelMap.signAndNameAndStamp}</fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-body>
    </fo:table>
</#escape>