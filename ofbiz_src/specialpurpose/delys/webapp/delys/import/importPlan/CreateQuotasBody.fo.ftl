<#escape x as x?xml>
	<fo:block font-weight="bold" font-size= "140%" text-align="center">CONTRACT</fo:block>
	<fo:block font-weight="bold" font-size= "120%" text-align="center">${txtNamePlan}</fo:block>
	<fo:block font-weight="bold" font-size= "100%" text-align="center">This purchase order is made on ${madeOn}</fo:block>
	
<fo:table table-layout="fixed" width="100%" margin-top="20px">
	<fo:table-column column-width="20%"/>
    <fo:table-column column-width="80%"/>
 	<fo:table-body>
 		<fo:table-row>
        		<fo:table-cell>
        			<fo:block font-size= "110%">Between </fo:block>
        		</fo:table-cell>
        		<fo:table-cell>
					<fo:table table-layout="fixed" width="100%" margin-top="20px">
						<fo:table-column column-width="50%"/>
					    <fo:table-column column-width="50%"/>
					 	<fo:table-body>
					 		<fo:table-row>
				        		<fo:table-cell number-columns-	="2">
				        			<fo:block font-weight="bold" font-size= "110%" text-align="center">${txtCompanyName1}</fo:block>
				        		</fo:table-cell>
				        	 </fo:table-row>
				        	 <fo:table-row>
				        		<fo:table-cell number-columns-	="2">
				        			<fo:block text-align="center">${txtAddress1}</fo:block>
				        		</fo:table-cell>
				        	 </fo:table-row>
				        	 <fo:table-row>
				        		<fo:table-cell>
				        			<fo:block text-align="center">Tel: ${txtTel1}</fo:block>
				        		</fo:table-cell>
								<fo:table-cell>
				        			<fo:block text-align="center">Fax: ${txtFax1}</fo:block>
				        		</fo:table-cell>
				        	 </fo:table-row>
				        	 <fo:table-row>
				        		<fo:table-cell number-columns-	="2">
				        			<fo:block text-align="center">${txtOther1}</fo:block>
				        		</fo:table-cell>
				        	 </fo:table-row>
					 	</fo:table-body>
					</fo:table>
				</fo:table-cell>
        </fo:table-row>
 		<fo:table-row>
        		<fo:table-cell>
        			<fo:block font-size= "110%">And </fo:block>
        		</fo:table-cell>
        		<fo:table-cell>
					<fo:table table-layout="fixed" width="100%" margin-top="20px">
						<fo:table-column column-width="50%"/>
					    <fo:table-column column-width="50%"/>
					 	<fo:table-body>
					 		<fo:table-row>
				        		<fo:table-cell number-columns-	="2">
				        			<fo:block font-weight="bold" font-size= "110%" text-align="center">${txtCompanyName2}</fo:block>
				        		</fo:table-cell>
				        	 </fo:table-row>
				        	 <fo:table-row>
				        		<fo:table-cell number-columns-	="2">
				        			<fo:block text-align="center">${txtAddress2}</fo:block>
				        		</fo:table-cell>
				        	 </fo:table-row>
				        	 <fo:table-row>
				        		<fo:table-cell number-columns-	="2">
				        			<fo:block text-align="center">${txtOther2}</fo:block>
				        		</fo:table-cell>
				        	 </fo:table-row>
					 	</fo:table-body>
					</fo:table>
				</fo:table-cell>
        </fo:table-row>
	</fo:table-body>
</fo:table>
<fo:block font-weight="bold" font-size= "120%" text-align="left" margin-top="20px">1. CONTRACT VALUE</fo:block>
<fo:block text-align="left" margin-top="10px">1.1	The total purchase order price is ${totalPriceAll}  ${currencyUomId}</fo:block>
<fo:block text-align="left">1.2	The breakdown of this amount is as follows: </fo:block>
	<#assign count=1 />
	<fo:table table-layout="fixed" width="100%" margin-top="10px">
        <fo:table-column column-width="5%"/>
        <fo:table-column column-width="25%"/>
        <fo:table-column column-width="8%"/>
        <fo:table-column column-width="15%"/>
        <fo:table-column column-width="8%"/>
        <fo:table-column column-width="15%"/>
        <fo:table-column column-width="8%"/>
        <fo:table-column column-width="17%"/>
        <fo:table-header>
          <fo:table-row>
              <fo:table-cell border= "1px solid black" text-align="center">
              	<fo:block font-weight="bold" margin-top="16px">No.</fo:block>
              </fo:table-cell>
              <fo:table-cell border= "1px solid black" text-align="center">
              	<fo:block font-weight="bold" margin-top="16px">Description</fo:block>
              </fo:table-cell>
              <fo:table-cell border= "1px solid black" text-align="center">
              	<fo:block font-weight="bold" margin-top="16px">Unit</fo:block>
              </fo:table-cell>
              <fo:table-cell border= "1px solid black" text-align="center">
              	<fo:block font-weight="bold" margin-top="16px">Quantity</fo:block>
              </fo:table-cell>
              <fo:table-cell border= "1px solid black" text-align="center">
              	<fo:block font-weight="bold" margin-top="16px">Unit Price</fo:block>
              </fo:table-cell>
              <fo:table-cell border= "1px solid black" text-align="center">
              	<fo:block font-weight="bold" margin-top="16px">Quantity</fo:block>
              </fo:table-cell>
              <fo:table-cell border= "1px solid black" text-align="center">
              	<fo:block font-weight="bold" margin-top="16px">Unit</fo:block>
              </fo:table-cell>
              <fo:table-cell border= "1px solid black" text-align="center">
              	<fo:block font-weight="bold" margin-top="16px">Total</fo:block>
              </fo:table-cell>
          </fo:table-row>
        </fo:table-header>
		 <fo:table-body>
			<#list listProductInfor as lst>
				<fo:table-row>
				<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px">${count}</fo:block>
             	</fo:table-cell>
				<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px">${lst.internalName}</fo:block>
             	</fo:table-cell>
             	<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px">${lst.productPackingUomId}</fo:block>
             	</fo:table-cell>
             	<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px">${lst.quantityImport}</fo:block>
             	</fo:table-cell>
             	<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px">${lst.lastPrice}</fo:block>
             	</fo:table-cell>
             	<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px">${lst.totalWeight}</fo:block>
             	</fo:table-cell>
             	<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px">${lst.weightUomId}</fo:block>
             	</fo:table-cell>
             	<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px">${lst.totalPrice}</fo:block>
             	</fo:table-cell>
				</fo:table-row>
				<#assign count = count + 1 />
				<#assign weightUomId = lst.weightUomId />
		 	</#list>
		<fo:table-row>
				<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px"></fo:block>
             	</fo:table-cell>
				<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px">Total</fo:block>
             	</fo:table-cell>
             	<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px"></fo:block>
             	</fo:table-cell>
             	<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px"></fo:block>
             	</fo:table-cell>
             	<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px"></fo:block>
             	</fo:table-cell>
             	<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px">${totalWeightAll}</fo:block>
             	</fo:table-cell>
             	<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px">${weightUomId}</fo:block>
             	</fo:table-cell>
             	<fo:table-cell border= "1px solid black" text-align="center">
              		<fo:block font-weight="bold" margin-top="16px"> ${totalPriceAll} </fo:block>
             	</fo:table-cell>
				</fo:table-row>
		</fo:table-body>
</fo:table>
<fo:block text-align="left" margin-top="20px">1.3 All the payment could be combine ratio between Euro and USD. CFR My Dinh</fo:block>
<fo:block text-align="left">1.4 The Purchaser will transfer of money before shipment.</fo:block>
<fo:block text-align="left">In favor of the Supplier, issued by a Vietnamese bank through the advising bank and the Purchaser shall inform by fax to the Supplier immediately.</fo:block>

					<fo:table table-layout="fixed" width="100%" margin-top="20px">
						<fo:table-column column-width="30%"/>
					    <fo:table-column column-width="70%"/>
					 	<fo:table-body>
					 		<fo:table-row>
				        		<fo:table-cell>
				        			<fo:block text-align="center">Supplier bank: </fo:block>
				        		</fo:table-cell>
								<fo:table-cell>
				        			<fo:block text-align="center">${txtSupplierBank}</fo:block>
				        		</fo:table-cell>
				        	 </fo:table-row>
							<fo:table-row>
				        		<fo:table-cell>
				        			<fo:block text-align="center">Beneficiary: </fo:block>
				        		</fo:table-cell>
								<fo:table-cell>
				        			<fo:block text-align="center">${txtBeneficiary}</fo:block>
				        		</fo:table-cell>
				        	 </fo:table-row>
					 	</fo:table-body>
					</fo:table>
<fo:block font-weight="bold" font-size= "120%" text-align="left" margin-top="20px">2. TERMS OF DELIVERY</fo:block>
<fo:block text-align="left">2.1 The date of shipment:  ${txtTheDateOfShipment}</fo:block>
<fo:block text-align="left">2.2 Port of discharging:  ${txtPortOfDischarging}</fo:block>
<fo:block text-align="left">2.3 Transshipment is allowed. Partial shipment is allowed.</fo:block>
<fo:block font-weight="bold" font-size= "120%" text-align="left" margin-top="20px">3. PACKING</fo:block>
<fo:block text-align="left">${txtPacking}</fo:block>
<fo:block font-weight="bold" font-size= "120%" text-align="left" margin-top="20px">4. DOCUMENTATION</fo:block>
<fo:block text-align="left">${txtDocumentation}</fo:block>
<fo:block font-weight="bold" font-size= "120%" text-align="left" margin-top="20px">5. TRANSPORTATION</fo:block>
<fo:block text-align="left">${txtTransportation}</fo:block>
				<fo:table table-layout="fixed" width="100%" margin-top="20px">
						<fo:table-column column-width="50%"/>
					    <fo:table-column column-width="50%"/>
					 	<fo:table-body>
					 		<fo:table-row>
				        		<fo:table-cell>
				        			<fo:block text-align="center" font-weight="bold" font-size= "120%">FOR THE SUPPLIER</fo:block>
				        		</fo:table-cell>
								<fo:table-cell>
				        			<fo:block text-align="center" font-weight="bold" font-size= "120%">FOR THE PURCHASER</fo:block>
				        		</fo:table-cell>
				        	 </fo:table-row>
					 	</fo:table-body>
					</fo:table>
</#escape>
