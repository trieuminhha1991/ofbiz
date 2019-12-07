<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:layout-master-set>
      <fo:simple-page-master master-name="main" page-height="11in" page-width="8.5in"
        margin-top="0.5in" margin-bottom="1in" margin-left=".5in" margin-right="1in">
          <fo:region-body margin-top="1in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    <fo:page-sequence master-reference="main">
    <fo:flow flow-name="xsl-region-body">
    		<fo:block font-size="18pt" padding="20pt" text-align="center" font-weight="bold">${uiLabelMap.ReceiptNote}</fo:block>
    		<fo:block font-size="10pt" padding="20pt" text-align="center">${uiLabelMap.deliveryDate}: ${delivery.deliveryDate}</fo:block>
    		<#assign address = delegator.findOne("PostalAddress", {"contactMechId" : delivery.contactMechId}, true) />
    		<#assign facility = delegator.findOne("Facility", {"facilityId" : delivery.originFacilityId}, true) />
    		<fo:table font-size="10pt" width="50%" margin-left="3%">
    			<fo:table-body>
    			<fo:table-row>
                    <fo:table-cell>
                    	<fo:block>${uiLabelMap.Receiver}:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                    </fo:table-cell>
                </fo:table-row>
    			<fo:table-row>
                    <fo:table-cell>
                    	<fo:block>${uiLabelMap.Unit}:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                    	<fo:block>${delivery.partyId}</fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell>
                    	<fo:block>${uiLabelMap.Address}:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                    	<fo:block>${address.address1}</fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                    <fo:table-cell>
                    	<fo:block>${uiLabelMap.DeliveryReason}:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                    	<fo:block></fo:block>
                    </fo:table-cell>
                </fo:table-row>
                <fo:table-row>
                	<fo:table-cell>
                    	<fo:block>${uiLabelMap.DeliveryFromFacility}:</fo:block>
                    </fo:table-cell>
                    <fo:table-cell>
                    	<fo:block>${facility.facilityName}</fo:block>
                    </fo:table-cell>
                </fo:table-row>
    		</fo:table-body>
    		</fo:table>
    		
			<fo:block font-size="10pt">
		    <fo:table margin-top="20pt">
		        <fo:table-column column-width="10%"/>
		        <fo:table-column column-width="30%"/>
		        <fo:table-column column-width="20%"/>
		        <fo:table-column column-width="10%"/>
		        <fo:table-column column-width="10%"/>
		        <fo:table-column column-width="10%"/>
		        <fo:table-column column-width="10%"/>
		        <fo:table-header>
		            <fo:table-row border-style="solid" border-width="1px" border-color="black" border-collapse="collapse">
		                <fo:table-cell padding="2pt" border="1pt solid" border-width=".1mm"><fo:block>${uiLabelMap.SequenceId}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" border="1pt solid" border-width=".1mm"><fo:block>${uiLabelMap.ProductProductName}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" border="1pt solid" border-width=".1mm"><fo:block>${uiLabelMap.ProductProductId}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" border="1pt solid" border-width=".1mm"><fo:block>${uiLabelMap.Unit}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"  border="1pt solid" border-width=".1mm"><fo:block>${uiLabelMap.Quantity}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" border="1pt solid" border-width=".1mm"><fo:block>${uiLabelMap.ActualReceiveQuantity}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" border="1pt solid" border-width=".1mm"><fo:block>${uiLabelMap.comment}</fo:block></fo:table-cell>
		            </fo:table-row>
		        </fo:table-header>
		        <fo:table-body>
		            <#list listItems as item>
						<#assign product = delegator.findOne("Product", {"productId" : item.productId}, true) />
		                <fo:table-row border-style="solid" border-width="1px" border-color="black" border-collapse="collapse">
		                      <fo:table-cell border="1pt solid" border-width=".1mm" padding="2pt">
		                    	<fo:block>${item.orderItemSeqId?if_exists}</fo:block>
		                    </fo:table-cell>
		                      <fo:table-cell border="1pt solid" border-width=".1mm">
		                        <fo:block>${product.productName?if_exists}</fo:block>
		                    </fo:table-cell>
		                      <fo:table-cell border="1pt solid" border-width=".1mm" padding="2pt">
		                        <fo:block>${item.productId?if_exists}</fo:block>
		                    </fo:table-cell>
		                      <fo:table-cell border="1pt solid" border-width=".1mm" padding="2pt">
		                        <fo:block>${item.quantityUomId?if_exists}</fo:block>
		                    </fo:table-cell>
		                      <fo:table-cell border="1pt solid" border-width=".1mm" padding="2pt">
		                        <fo:block>${item.quantity?if_exists}</fo:block>
		                    </fo:table-cell>
		                      <fo:table-cell border="1pt solid" border-width=".1mm" padding="2pt">
		                        <fo:block>${item.actualQuantity?if_exists}</fo:block>
		                    </fo:table-cell>
		                      <fo:table-cell border="1pt solid" border-width=".1mm" padding="2pt">
		                        <fo:block></fo:block>
		                    </fo:table-cell>
		                </fo:table-row>
		            </#list>
		        </fo:table-body>
		    </fo:table>
		</fo:block>
		<fo:block font-size="10pt" padding="20pt" text-align="right">${uiLabelMap.createDate}: ${delivery.createDate}</fo:block>
		
		<fo:block font-size="10pt">
			<fo:table margin-top="10pt">
				<fo:table-column column-width="20%"/>
		        <fo:table-column column-width="20%"/>
		        <fo:table-column column-width="20%"/>
		        <fo:table-column column-width="20%"/>
		        <fo:table-column column-width="20%"/>
		        <fo:table-header>
		            <fo:table-row>
		                <fo:table-cell padding="2pt"><fo:block>${uiLabelMap.Director}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"><fo:block>${uiLabelMap.accAccountings}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"><fo:block>${uiLabelMap.StoreKeeper}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"><fo:block>${uiLabelMap.Deliverer}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"><fo:block>${uiLabelMap.Receiver}</fo:block></fo:table-cell>
		            </fo:table-row>
		            <fo:table-row>
		                <fo:table-cell padding="2pt"><fo:block>(${uiLabelMap.Sign})</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"><fo:block>(${uiLabelMap.Sign})</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"><fo:block>(${uiLabelMap.Sign})</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"><fo:block>(${uiLabelMap.Sign})</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"><fo:block>(${uiLabelMap.Sign})</fo:block></fo:table-cell>
		            </fo:table-row>
		        </fo:table-header>
		        <fo:table-body>
		        	 <fo:table-row>
		                <fo:table-cell padding="2pt"><fo:block></fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"><fo:block></fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"><fo:block></fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"><fo:block></fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt"><fo:block></fo:block></fo:table-cell>
		            </fo:table-row>
		        </fo:table-body>
			</fo:table> 
		</fo:block>
	</fo:flow>
	</fo:page-sequence>
</fo:root>
</#escape>