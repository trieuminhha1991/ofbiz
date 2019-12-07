<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
 <fo:layout-master-set>
      <fo:simple-page-master master-name="main" page-height="11in" page-width="8.5in"
        margin-top="-0.8in" margin-bottom="1in" margin-left="0.5in" margin-right="1in">
          <fo:region-body margin-top="1in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="-2in"/>
      </fo:simple-page-master>
</fo:layout-master-set>
<fo:page-sequence master-reference="main">
	<fo:static-content flow-name="xsl-region-after">
            <fo:block font-size="10pt" text-align="right" space-before="10pt">
                 <fo:page-number ref-id="theEnd"/>
            </fo:block>
        </fo:static-content>
    <fo:flow flow-name="xsl-region-body" font-family="Arial">
		<fo:block>
			<fo:block font-weight="bold" font-size= "150%" text-align="center">${uiLabelMap.CHXHCNVN}</fo:block>
			<fo:block font-weight="bold" font-size= "101%" text-align="center">${uiLabelMap.DLTDHP}</fo:block>
			<fo:block font-weight="bold" text-align="center">------------------</fo:block>
			<fo:block font-weight="bold" font-size= "121%" margin-top="20px" text-align="center">${uiLabelMap.BCBHQ}</fo:block>
			<fo:block font-weight="bold" text-align="center">${NumberQP}</fo:block>
			<fo:table table-layout="fixed" width="100%" margin-top="20px">
				<fo:table-column column-width="25%"/>
  			    <fo:table-column column-width="25%"/>
				<fo:table-column column-width="25%"/>
   				<fo:table-column column-width="25%"/>
 				<fo:table-body>
        	<fo:table-row>
        		<fo:table-cell>
        			<fo:block >${uiLabelMap.DVNK}: </fo:block>
        		</fo:table-cell>
        		<fo:table-cell number-columns-spanned="3">
					<fo:block font-size= "121%">${DVNK}</fo:block>
				</fo:table-cell>
        	</fo:table-row>
			<fo:table-row>
        		<fo:table-cell>
        			<fo:block >${uiLabelMap.Address}: </fo:block>
        		</fo:table-cell>
        		<fo:table-cell number-columns-spanned="3">
					<fo:block >${AddressDVNK}</fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell>
        			<fo:block >${uiLabelMap.Phone}: </fo:block>
        		</fo:table-cell>
        		<fo:table-cell>
					<fo:block >${txtPhone}</fo:block>
				</fo:table-cell>
				<fo:table-cell>
        			<fo:block >${uiLabelMap.Fax}: </fo:block>
        		</fo:table-cell>
        		<fo:table-cell>
					<fo:block >${txtFax}</fo:block>
				</fo:table-cell>
        	</fo:table-row>
     </fo:table-body>
			</fo:table>
			<fo:block font-weight="bold" font-size= "121%" margin-top="20px" text-align="center">${uiLabelMap.Pulish}:</fo:block>
			<fo:table table-layout="fixed" width="100%" margin-top="20px">
				<fo:table-column column-width="15%"/>
			    <fo:table-column column-width="85%"/>
			 	<fo:table-body>
			        	<fo:table-row>
			        		<fo:table-cell>
			        			<fo:block >${uiLabelMap.Product}: </fo:block>
			        		</fo:table-cell>
			        		<fo:table-cell>
								<fo:block font-weight="bold" text-align="left" font-size= "111%">${productName}</fo:block>
							</fo:table-cell>
			        	</fo:table-row>
			     </fo:table-body>
			</fo:table>
			<fo:block >${uiLabelMap.origin}:  ${txtXuatXu}</fo:block>
			<fo:block >${uiLabelMap.Manufacturer}:  ${txtNhaSanXuat}</fo:block>
			<fo:block >${uiLabelMap.Address}: ${ManufacturerAddress}</fo:block>
			<fo:block >${uiLabelMap.Exporter}: ${txtNhaXuatKhau}</fo:block>
			<fo:block >${uiLabelMap.QCVN}: ${QCVN}</fo:block>
			<fo:block >${uiLabelMap.PTDGSPH}: ${PTDGSPH}</fo:block>
			<fo:block>${CamKet}</fo:block>
			<fo:table table-layout="fixed" width="100%" margin-top="20px">
				<fo:table-column column-width="55%"/>
			    <fo:table-column column-width="45%"/>
			 	<fo:table-body>
			        	<fo:table-row>
			        		<fo:table-cell>
			        		</fo:table-cell>
			        		<fo:table-cell>
								<fo:block>${NoiViet}</fo:block>
							</fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
			        		<fo:table-cell>
			        		</fo:table-cell>
			        		<fo:table-cell>
								<fo:block font-weight="bold" text-align="left" font-size= "111%">${ctDelys}</fo:block>
							</fo:table-cell>
			        	</fo:table-row>
			     </fo:table-body>
			</fo:table>
		</fo:block>
		<fo:block id="theEnd2"/>
	</fo:flow>
</fo:page-sequence>

<fo:page-sequence master-reference="main">
	<fo:static-content flow-name="xsl-region-after">
            <fo:block font-size="10pt" text-align="right" space-before="10pt">
                 <fo:page-number ref-id="theEnd"/>
            </fo:block>
        </fo:static-content>
    <fo:flow flow-name="xsl-region-body" font-family="Arial">
		<fo:block>
			<fo:block font-weight="bold" font-size= "121%" margin-top="60px" text-align="center">${uiLabelMap.BTTCTSP}</fo:block>
<fo:block text-align="center">----------------</fo:block>
<fo:table table-layout="fixed" border= "1px solid black" width="100%" margin-top="10px" margin-bottom="10px">
	<fo:table-column column-width="40%"/>
    <fo:table-column column-width="35%"/>
    <fo:table-column column-width="25%"/>
 	<fo:table-body>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.SKHDTTPHN}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${catalog}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${NumberQP}</fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">${ctDelys}</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${productName}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px" font-weight="bold" text-align="left" font-size= "111%"></fo:block>
				</fo:table-cell>
        	</fo:table-row>
     </fo:table-body>
</fo:table>
<fo:block font-weight="bold" text-align="left" font-size= "130%">I. ${uiLabelMap.TechnicalRequirements} </fo:block>
<fo:block font-weight="bold" text-align="left" font-size= "118%" >1. ${uiLabelMap.SensoryNorm} </fo:block>
<fo:table table-layout="fixed" border= "1px solid black" width="100%" margin-bottom="20px">
	<fo:table-column column-width="8%"/>
    <fo:table-column column-width="35%"/>
    <fo:table-column column-width="57%"/>
 	<fo:table-body>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.No} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.NormName} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${uiLabelMap.publishLevel} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">1</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.StatusOutside} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtStatusOutside} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">2</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Color} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtColor} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">3</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Flavor} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtFlavor} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">4</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Impurities} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtImpurities} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
     </fo:table-body>
</fo:table>

<fo:block font-weight="bold" text-align="left" font-size= "118%">2. ${uiLabelMap.PhysicsNorm} </fo:block>
<fo:table table-layout="fixed" border= "1px solid black" width="100%" margin-bottom="20px">
	<fo:table-column column-width="8%"/>
    <fo:table-column column-width="35%"/>
    <fo:table-column column-width="15%"/>
    <fo:table-column column-width="41%"/>
 	<fo:table-body>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.No} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.NormName} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Unit}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${uiLabelMap.publishLevel} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">1</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Calories}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtCaloriesUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtCalories} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">2</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Protein} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtProteinUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtProtein} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">3</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.CarbonHydrat} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtCarbonHydratUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtCarbonHydrat} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">4</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Fat} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtFatUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtFat} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
			<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">5</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Canxi}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtCanxiUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtCanxi} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block  margin="6px" font-weight="bold">6</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.pH} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtpHUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtpH} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
     </fo:table-body>
</fo:table>

<fo:block font-weight="bold" text-align="left" font-size= "118%">3. ${uiLabelMap.MicroorganismNorm}</fo:block>
<fo:table table-layout="fixed" border= "1px solid black" width="100%" margin-bottom="20px">
	<fo:table-column column-width="8%"/>
    <fo:table-column column-width="35%"/>
    <fo:table-column column-width="15%"/>
    <fo:table-column column-width="41%"/>
 	<fo:table-body>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.No} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.NormName} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Unit}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${uiLabelMap.maxLevel} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">1</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Lmonocytogenes}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtLmonocytogenesUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtLmonocytogenes} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">2</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Salmonella}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtSalmonellaUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtSalmonella} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">3</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Staphylococci} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtStaphylococciUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtStaphylococci} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">4</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Staphylococcal} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtStaphylococcalUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtStaphylococcal} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
     </fo:table-body>
</fo:table>

<fo:block font-weight="bold" text-align="left" font-size= "118%">3. ${uiLabelMap.HeavyMetals}</fo:block>
<fo:table table-layout="fixed" border= "1px solid black" width="100%" margin-bottom="20px">
    <fo:table-column column-width="35%"/>
    <fo:table-column column-width="30%"/>
    <fo:table-column column-width="35%"/>
 	<fo:table-body>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.MetalName} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Unit}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${uiLabelMap.maxLevel} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Lead}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtLeadUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtLead} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Antimony}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtAntimoxyUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtAntimoxy} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Arsen} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtArsenUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtArsen} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Cadimi} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtCadimiUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtCadimi} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
			<fo:table-row>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Mercury} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtMercuryUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtMercury} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
     </fo:table-body>
</fo:table>

<fo:block font-weight="bold" text-align="left" font-size= "118%">2. ${uiLabelMap.UnwantedNorms} </fo:block>
<fo:table table-layout="fixed" border= "1px solid black" width="100%" margin-bottom="20px">
	<fo:table-column column-width="8%"/>
    <fo:table-column column-width="35%"/>
    <fo:table-column column-width="15%"/>
    <fo:table-column column-width="41%"/>
 	<fo:table-body>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.No} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.NormName} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Unit}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${uiLabelMap.maxLevel} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">1</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Aflatoxin}</fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtAflatoxinUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtAflatoxin} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">2</fo:block>
        		</fo:table-cell>
				<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px">${uiLabelMap.Melamine} </fo:block>
        		</fo:table-cell>
        		<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtMelamineUnit} </fo:block>
				</fo:table-cell>
				<fo:table-cell border= "1px solid black">
					<fo:block margin="6px">${txtMelamine} </fo:block>
				</fo:table-cell>
        	</fo:table-row>
        	<fo:table-row>
        		<fo:table-cell border= "1px solid black">
        			<fo:block margin="6px" font-weight="bold">3</fo:block>
        		</fo:table-cell>
        		<fo:table-cell number-columns-spanned="3" border= "1px solid black">
        			<fo:block margin="6px">${txtUnwantedNorms} </fo:block>
        		</fo:table-cell>
        	</fo:table-row>
	 </fo:table-body>
</fo:table>

<fo:block font-weight="bold" margin="6px" text-align="left" font-size= "130%">II. ${uiLabelMap.Components}</fo:block>
<fo:block margin="6px">${Components}</fo:block>

<fo:block font-weight="bold" margin="6px" text-align="left" font-size= "130%">III. ${uiLabelMap.shelfLifeInstructionAndMaintain}</fo:block>
<fo:block font-weight="bold" text-align="left" font-size= "118%" >a) ${uiLabelMap.shelfLife}: </fo:block>
<fo:block margin="6px">- ${uiLabelMap.expireDay}  ${txtExpireDay}  ${uiLabelMap.fromProductionDate}.</fo:block>
<fo:block margin="6px">- ${uiLabelMap.dateOfManufacture}  ${dateOfManufacture}.</fo:block>
<fo:block margin="6px">- ${uiLabelMap.ExpireDate}  ${ExpireDate}.</fo:block>
<fo:block font-weight="bold" text-align="left" font-size= "118%" >b) ${uiLabelMap.Instruction}:  </fo:block>
<fo:block margin="6px">${Instruction}</fo:block>
<fo:block font-weight="bold" text-align="left" font-size= "118%" >c) ${uiLabelMap.Maintain}: </fo:block>
<fo:block margin="6px">${Maintain}</fo:block>
<fo:block font-weight="bold" margin="6px" text-align="left" font-size= "130%">IV. ${uiLabelMap.PackagingMaterialAndPacking} </fo:block>
<fo:block margin="6px">${PackagingMaterialAndPacking} </fo:block>
<fo:block font-weight="bold" margin="6px" text-align="left" font-size= "130%">V. ${uiLabelMap.OriginAndTraderImport} </fo:block>
<fo:block margin="6px">- ${uiLabelMap.Manufacturer}: ${Manufacturer}</fo:block>
<fo:block margin="6px">- ${uiLabelMap.Address}: ${AddressManufacturer}</fo:block>
<fo:block margin="6px">- ${uiLabelMap.Importer}: ${Importer}</fo:block>
<fo:block margin="6px">- ${uiLabelMap.Address}: ${AddressImporter} </fo:block>
<fo:block margin="6px">- ${uiLabelMap.Phone}: ${txtPhone}</fo:block>
<fo:block margin="6px">- ${uiLabelMap.Fax}: ${txtFax} </fo:block>
		</fo:block>
		<fo:block id="theEnd"/>
	</fo:flow>
</fo:page-sequence>

<fo:page-sequence master-reference="main">
	<fo:static-content flow-name="xsl-region-after">
            <fo:block font-size="10pt" text-align="right" space-before="10pt">
                 <fo:page-number/>
            </fo:block>
        </fo:static-content>
    <fo:flow flow-name="xsl-region-body" font-family="Arial">
		<fo:block>
			<fo:block font-weight="bold" font-size= "121%" margin-top="60px" text-align="center">${uiLabelMap.draftLabelOfProduct}</fo:block>
			<fo:block font-weight="bold"  margin-top="10px" font-size= "111%">${uiLabelMap.ProductName}:  ${productName}</fo:block>
			<fo:block font-weight="bold"  margin-top="10px" font-size= "111%">${uiLabelMap.Components2}:  ${Components2}</fo:block>
			<fo:block font-weight="bold"  margin-top="10px" font-size= "111%">${uiLabelMap.NutritiousInfoIn100g}:  ${NutritiousInfoIn100g}</fo:block>
			<fo:block font-weight="bold"  margin-top="10px" font-size= "111%">${uiLabelMap.dateOfManufacture}:  ${dateOfManufacture}</fo:block>
			<fo:block font-weight="bold"  margin-top="10px" font-size= "111%">${uiLabelMap.ExpireDate}:  ${shelfLife2}</fo:block>
			<fo:block font-weight="bold"  margin-top="10px" font-size= "111%">${uiLabelMap.Maintain}:  ${txtMaintain}</fo:block>
			<fo:block font-weight="bold"  margin-top="10px" font-size= "111%">${uiLabelMap.Instruction}:  ${txtInstruction}</fo:block>
			<fo:block font-weight="bold"  margin-top="10px" font-size= "111%">${uiLabelMap.NetWeight}:  ${txtNetWeight}</fo:block>
			<fo:block font-weight="bold"  margin-top="10px" font-size= "111%">${uiLabelMap.origin}:  ${txtorigin}</fo:block>
			<fo:block font-weight="bold"  margin-top="10px" font-size= "111%">${uiLabelMap.Exporter}:  ${Exporter}</fo:block>
			<fo:block font-weight="bold"  margin-top="10px" font-size= "111%">${uiLabelMap.Importer}:  ${Importer2}</fo:block>
			<fo:block>${uiLabelMap.Address}:  ${AddressImporter}</fo:block>
			<fo:block>${uiLabelMap.Phone}:  ${txtPhone}</fo:block>
			<fo:block>${uiLabelMap.Fax}:  ${txtFax}</fo:block>
		</fo:block>
		<fo:block id="theEnd2"/>
	</fo:flow>
</fo:page-sequence>

<fo:page-sequence master-reference="main">
	<fo:static-content flow-name="xsl-region-after">
            <fo:block font-size="10pt" text-align="right" space-before="10pt">
                 <fo:page-number/>
            </fo:block>
        </fo:static-content>
    <fo:flow flow-name="xsl-region-body" font-family="Arial">
		<fo:block>
			<fo:block font-weight="bold" font-size= "121%" margin-top="60px" text-align="center">${uiLabelMap.PeriodicalSurveyPlan}</fo:block>
			<fo:block margin-top="20px">${uiLabelMap.organizationName}:  ${organizationName}</fo:block>
			<fo:block>${uiLabelMap.Address}:  ${organizationAddress}</fo:block>
			<fo:block>${uiLabelMap.Phone}:  ${txtPhone}</fo:block>
			<fo:block>${uiLabelMap.Fax}:  ${txtFax}</fo:block>
			<fo:block font-weight="bold" font-size= "121%" margin-top="60px" text-align="center">${uiLabelMap.PeriodicalSurveyPlan}</fo:block>
			<fo:block text-align="center">${uiLabelMap.Product}:  ${productName}</fo:block>
			<fo:table table-layout="fixed" border= "1px solid black" width="100%"  margin-top="20px"  margin-bottom="20px">
				<fo:table-column column-width="8%"/>
			    <fo:table-column column-width="35%"/>
			    <fo:table-column column-width="20%"/>
			    <fo:table-column column-width="37%"/>
			 	<fo:table-body>
			        	<fo:table-row>
			        		<fo:table-cell border= "1px solid black">
			        			<fo:block margin="6px">${uiLabelMap.No} </fo:block>
			        		</fo:table-cell>
			        		<fo:table-cell border= "1px solid black">
			        			<fo:block margin="6px">${uiLabelMap.TechnologyApplying} </fo:block>
			        		</fo:table-cell>
			        		<fo:table-cell border= "1px solid black">
			        			<fo:block margin="6px">${uiLabelMap.SurveyTest}</fo:block>
			        		</fo:table-cell>
			        		<fo:table-cell border= "1px solid black">
								<fo:block margin="6px">${uiLabelMap.RePulish} </fo:block>
							</fo:table-cell>
			        	</fo:table-row>
						<fo:table-row>
			        		<fo:table-cell border= "1px solid black">
			        			<fo:block margin="6px">01</fo:block>
			        		</fo:table-cell>
			        		<fo:table-cell border= "1px solid black">
			        			<fo:block margin="6px">${TechnologyApplying} </fo:block>
			        		</fo:table-cell>
			        		<fo:table-cell border= "1px solid black">
			        			<fo:block margin="6px">${SurveyTest}</fo:block>
			        		</fo:table-cell>
			        		<fo:table-cell border= "1px solid black">
								<fo:block margin="6px">${RePulish} </fo:block>
							</fo:table-cell>
			        	</fo:table-row>
				 </fo:table-body>
			</fo:table>
			<fo:table table-layout="fixed" width="100%" margin-top="60px">
				<fo:table-column column-width="55%"/>
			    <fo:table-column column-width="45%"/>
			 	<fo:table-body>
			        	<fo:table-row>
			        		<fo:table-cell>
			        			
			        		</fo:table-cell>
			        		<fo:table-cell>
								<fo:block>${NoiViet}</fo:block>
							</fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
			        		<fo:table-cell>
			        			
			        		</fo:table-cell>
			        		<fo:table-cell>
								<fo:block font-weight="bold" text-align="left" font-size= "111%">${ctDelys}</fo:block>
							</fo:table-cell>
			        	</fo:table-row>
			     </fo:table-body>
			</fo:table>
		</fo:block>
		<fo:block id="theEnd2"/>
	</fo:flow>
</fo:page-sequence>

<fo:page-sequence master-reference="main">
	<fo:static-content flow-name="xsl-region-after">
            <fo:block font-size="10pt" text-align="right" space-before="10pt">
                 <fo:page-number/>
            </fo:block>
        </fo:static-content>
    <fo:flow flow-name="xsl-region-body" font-family="Arial">
		<fo:block>
			<fo:block font-weight="bold" font-size= "121%" margin-top="60px" text-align="center">${uiLabelMap.BBCCHQ}</fo:block>
			<fo:block text-align="center">${uiLabelMap.Product}:  ${productName}</fo:block>
			<fo:block margin-top="10px">${uiLabelMap.thtpt}:  ${thtpt}</fo:block>
			<fo:block margin-top="20px">1. ${uiLabelMap.xdspcdkn}:  ${xdspcdkn}</fo:block>
			<fo:block>2. ${uiLabelMap.dgsphcsp}:  ${dgsphcsp}</fo:block>
			<fo:block>3. ${uiLabelMap.xlkqdgsph}:  ${xlkqdgsph}</fo:block>
			<fo:block>4. ${uiLabelMap.klvsph}:  ${klvsph}</fo:block>
			<fo:table table-layout="fixed" width="100%" margin-top="60px">
				<fo:table-column column-width="55%"/>
			    <fo:table-column column-width="45%"/>
			 	<fo:table-body>
			        	<fo:table-row>
			        		<fo:table-cell>
			        			
			        		</fo:table-cell>
			        		<fo:table-cell>
								<fo:block>${NoiViet}</fo:block>
							</fo:table-cell>
			        	</fo:table-row>
			        	<fo:table-row>
			        		<fo:table-cell>
			        			
			        		</fo:table-cell>
			        		<fo:table-cell>
								<fo:block font-weight="bold" text-align="left" font-size= "111%">${ctDelys}</fo:block>
							</fo:table-cell>
			        	</fo:table-row>
			     </fo:table-body>
			</fo:table>
		</fo:block>
		<fo:block id="theEnd2"/>
	</fo:flow>
</fo:page-sequence>
</fo:root>
</#escape>