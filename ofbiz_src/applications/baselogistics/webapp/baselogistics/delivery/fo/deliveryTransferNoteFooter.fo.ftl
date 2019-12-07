<#escape x as x?xml>
<fo:block font-size="9" font-family="Arial">
	<fo:table margin-top="10px" width="545px" table-layout="fixed" space-after.optimum="10pt" font-size="10pt" border="solid 0.3mm black">
	 	<fo:table-column border="solid 0.1mm black"/>
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
				    <fo:block font-weight="bold" margin-top="2px">${uiLabelMap.StorekeeperOriginFacility}</fo:block>
				    <fo:block font-size="80%">(${uiLabelMap.SignAndKeyFullName})</fo:block>
				</fo:table-cell>
				<fo:table-cell text-align="center" >
				    <fo:block font-weight="bold" margin-top="2px">${uiLabelMap.DeliveryMan}</fo:block>
				    <fo:block font-size="80%">(${uiLabelMap.SignAndKeyFullName})</fo:block>
				</fo:table-cell> 
				<fo:table-cell text-align="center" >
				    <fo:block font-weight="bold" margin-top="2px">${uiLabelMap.StorekeeperDestFacility}</fo:block>
				    <fo:block font-size="80%">(${uiLabelMap.SignAndKeyFullName})</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row height="100px">
				<fo:table-cell >
				</fo:table-cell>
				<fo:table-cell >
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
				<fo:table-cell>
				</fo:table-cell>
			</fo:table-row>
	 	</fo:table-body>
	</fo:table>
</fo:block>
</#escape>