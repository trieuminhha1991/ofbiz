<#escape x as x?xml>
<fo:block font-size="9" font-family="Arial">
	<fo:table margin-top="65px" table-layout="fixed" space-after.optimum="10pt" font-size="9pt" border="solid 0.3mm black">
		 <fo:table-column border="solid 0.2mm black" column-width="182pt"/>
		 <fo:table-column border="solid 0.2mm black" column-width="182pt"/>
		 <fo:table-column border="solid 0.2mm black" column-width="182pt"/>
		 <fo:table-body>
			<fo:table-row margin-top="10px" border="solid 0.2mm black">
				<fo:table-cell text-align="center" >
				    <fo:block font-weight="bold">${uiLabelMap.DeliveryCreatedBy}</fo:block>
				    <fo:block >(${uiLabelMap.SignAndKeyFullName})</fo:block>
				</fo:table-cell>
				<fo:table-cell text-align="center" >
				    <fo:block font-weight="bold">${uiLabelMap.Storekeeper}</fo:block>
				    <fo:block >(${uiLabelMap.SignAndKeyFullName})</fo:block>
				</fo:table-cell> 
				<fo:table-cell text-align="center" >
				    <fo:block font-weight="bold">${uiLabelMap.Receiver}</fo:block>
				    <fo:block >(${uiLabelMap.SignAndKeyFullName})</fo:block>
				</fo:table-cell>
			</fo:table-row>
			<fo:table-row height="50px">
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