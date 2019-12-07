<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="11x17-landscape" page-width="17in" page-height="11in"
                margin-top="0.1in" margin-bottom="0.5in" margin-left="0.5in" margin-right="0.5in">
                <fo:region-body margin-top="1in" margin-bottom="0.5in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        <fo:page-sequence master-reference="11x17-landscape">
            <fo:flow flow-name="xsl-region-body" font-family="Arial">
            <fo:block text-align="center" font-size="24pt" font-weight="bold" margin-top="12px">${uiLabelMap.ListAttachments}</fo:block>
		            <fo:table table-layout="fixed"  width="100%" margin-top="20px">
		        	<fo:table-column column-width="4%"/>
		        	<fo:table-column column-width="20%"/>
		        	<fo:table-column column-width="7%"/>
		        	<fo:table-column column-width="5%"/>
		        	<fo:table-column column-width="10%"/>
		        	<fo:table-column column-width="5%"/>
		        	<fo:table-column column-width="14%"/>
		        	<fo:table-column column-width="14%"/>
		        	<fo:table-column column-width="5%"/>
		        	<fo:table-column column-width="11%"/>
		        	<fo:table-column column-width="5%"/>
		        	<fo:table-body>
				        	<fo:table-row>
								<fo:table-cell number-rows-spanned="2" border= "1px solid black">
									<fo:block text-align="center" font-size="14pt" margin-top="12px">${uiLabelMap.accSTT}</fo:block>
								</fo:table-cell>
								<fo:table-cell number-rows-spanned="2" border= "1px solid black">
									<fo:block text-align="center" font-size="14pt" margin-top="12px">${uiLabelMap.CommodityName}</fo:block>
								</fo:table-cell>
								<fo:table-cell number-rows-spanned="2" border= "1px solid black">
									<fo:block text-align="center" font-size="14pt" margin-top="12px">${uiLabelMap.CommodityCode}</fo:block>
								</fo:table-cell>
								<fo:table-cell number-rows-spanned="2" border= "1px solid black">
									<fo:block text-align="center" font-size="14pt" margin-top="12px">${uiLabelMap.KARNumber}</fo:block>
								</fo:table-cell>
								<fo:table-cell number-rows-spanned="2" border= "1px solid black">
									<fo:block text-align="center" font-size="14pt" margin-top="12px">${uiLabelMap.CommodityQuantity}</fo:block>
								</fo:table-cell>
								<fo:table-cell number-rows-spanned="2" border= "1px solid black">
									<fo:block text-align="center" font-size="14pt" margin-top="12px">${uiLabelMap.currency}</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="2" border= "1px solid black">
									<fo:block text-align="center" font-size="14pt" margin-top="5px">${uiLabelMap.weight}(${weightUnit})</fo:block>
								</fo:table-cell>
								<fo:table-cell number-rows-spanned="2" border= "1px solid black">
									<fo:block text-align="center" font-size="14pt" margin-top="12px">${uiLabelMap.ContractNumber}</fo:block>
								</fo:table-cell>
								<fo:table-cell number-rows-spanned="2" border= "1px solid black">
									<fo:block text-align="center" font-size="14pt" margin-top="12px">${uiLabelMap.Value}(${currencyUnit})</fo:block>
								</fo:table-cell>
								<fo:table-cell number-rows-spanned="2" border= "1px solid black">
									<fo:block text-align="center" font-size="14pt" margin-top="12px">${uiLabelMap.Origin}</fo:block>
								</fo:table-cell>
							</fo:table-row>
							
							<fo:table-row>
								<fo:table-cell border= "1px solid black">
									<fo:block text-align="center" font-size="14pt" margin-top="5px">${uiLabelMap.NetWeight}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block text-align="center" font-size="14pt" margin-top="5px">${uiLabelMap.ShipmentTotalWeight}</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<#assign OneTurn = true>
							<#list listConvertProductInfo as lpi>
								
							<#if OneTurn>
							<fo:table-row>
								<fo:table-cell border= "1px solid black">
									<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.index}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtinternalName}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtCommodityCode}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtKAR}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtquantity}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtquantityUomId}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtNetWeight}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtShipmentTotalWeight}</fo:block>
								</fo:table-cell>
								<fo:table-cell number-rows-spanned="${listConvertProductInfo?size}" border= "1px solid black">
									<fo:block text-align="center" font-size="13pt" margin-top="12px">${txtContractNumber}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtValue}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtbrandName}</fo:block>
								</fo:table-cell>
							</fo:table-row>
	                        <#else>
							<fo:table-row>
										<fo:table-cell border= "1px solid black">
											<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.index}</fo:block>
										</fo:table-cell>
										<fo:table-cell border= "1px solid black">
											<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtinternalName}</fo:block>
										</fo:table-cell>
										<fo:table-cell border= "1px solid black">
											<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtCommodityCode}</fo:block>
										</fo:table-cell>
										<fo:table-cell border= "1px solid black">
											<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtKAR}</fo:block>
										</fo:table-cell>
										<fo:table-cell border= "1px solid black">
											<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtquantity}</fo:block>
										</fo:table-cell>
										<fo:table-cell border= "1px solid black">
											<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtquantityUomId}</fo:block>
										</fo:table-cell>
										<fo:table-cell border= "1px solid black">
											<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtNetWeight}</fo:block>
										</fo:table-cell>
										<fo:table-cell border= "1px solid black">
											<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtShipmentTotalWeight}</fo:block>
										</fo:table-cell>
										<fo:table-cell border= "1px solid black">
											<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtValue}</fo:block>
										</fo:table-cell>
										<fo:table-cell border= "1px solid black">
											<fo:block text-align="center" font-size="13pt" margin-top="12px">${lpi.txtbrandName}</fo:block>
										</fo:table-cell>
							</fo:table-row>
	                        </#if>
	                        <#assign OneTurn = false>
							</#list>
							
							<fo:table-row>
								<fo:table-cell number-columns-spanned="2" border= "1px solid black">
									<fo:block font-weight="bold" text-align="center" font-size="16pt" margin-top="12px">${uiLabelMap.GrandTotal?upper_case}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block></fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block font-weight="bold" text-align="center" font-size="16pt" margin-top="12px">${txtKARTotal}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block font-weight="bold" text-align="center" font-size="16pt" margin-top="12px">${txtquantityTotal}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block></fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block font-weight="bold" text-align="center" font-size="16pt" margin-top="12px">${txttxtNetWeightTotal}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block font-weight="bold" text-align="center" font-size="16pt" margin-top="12px">${txtShipmentTotalWeightTotal}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block></fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block font-weight="bold" text-align="center" font-size="16pt" margin-top="12px">${txttxtValueTotal}</fo:block>
								</fo:table-cell>
								<fo:table-cell border= "1px solid black">
									<fo:block></fo:block>
								</fo:table-cell>
							</fo:table-row>
							
					</fo:table-body>
					</fo:table>
            </fo:flow>
        </fo:page-sequence>
    </fo:root>
</#escape>
