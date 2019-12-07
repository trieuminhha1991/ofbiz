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
    	<fo:block padding="20pt">${uiLabelMap.ListInventoryItemsTotal}</fo:block>
			<fo:block font-size="8pt">
		    <fo:table>
		        <fo:table-column column-width="40pt"/>
		        <fo:table-column column-width="80pt"/>
		        <fo:table-column column-width="50pt"/>
		        <fo:table-column column-width="50pt"/>
		        <fo:table-column column-width="50pt"/>
		        <fo:table-column column-width="20pt"/>
		        <fo:table-column column-width="30pt"/>
		        <fo:table-column column-width="30pt"/>
		        <fo:table-column column-width="50pt"/>
				<fo:table-column column-width="30pt"/>
		        <fo:table-column column-width="20pt"/>
		        <fo:table-column column-width="50pt"/>
		        <fo:table-column column-width="50pt"/>
		        <fo:table-header>
		            <fo:table-row font-weight="bold">
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.InventoryItemId}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.ProductProductId}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.ProductProductName}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.DatetimeReceived}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.ProductExpireDate}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.ProductLotId}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.ProductBinNum}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.ContainerId}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.UnitCost}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.currencyUomId}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.FormFieldTitle_quantityOnHandTotal}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.FormFieldTitle_availableToPromiseTotal}</fo:block></fo:table-cell>
		                <fo:table-cell padding="2pt" background-color="#D4D0C8"><fo:block>${uiLabelMap.Status}</fo:block></fo:table-cell>
		            </fo:table-row>
		        </fo:table-header>
		        <fo:table-body>
		            <#list listItems as item>
		            	<#assign inventoryItem = item.getRelatedOne("InventoryItem", false)/>
		                <fo:table-row>
		                    <fo:table-cell padding="2pt">
		                    	<fo:block>${item.inventoryItemId?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell padding="2pt">
		                        <fo:block>${item.productId?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell padding="2pt">
		                        <fo:block>${item.internalName?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell padding="2pt">
		                        <fo:block>${item.datetimeReceived?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell padding="2pt">
		                        <fo:block>${item.expireDate?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell padding="2pt">
		                        <fo:block>${item.lotId?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell padding="2pt">
		                        <fo:block>${item.binNumber?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell padding="2pt">
		                        <fo:block>${item.containerId?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell padding="2pt">
		                        <fo:block>${item.unitCost?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell padding="2pt">
		                        <fo:block>${item.currencyUomId?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell padding="2pt">
		                        <fo:block>${item.quantityOnHandTotal?if_exists}</fo:block>
		                    </fo:table-cell>
		                    <fo:table-cell padding="2pt">
		                        <fo:block>${item.availableToPromiseTotal?if_exists}</fo:block>
		                    </fo:table-cell>
		                       <fo:table-cell padding="2pt">
		                        <fo:block>
		                        	<#assign status = inventoryItem.getRelatedOne("StatusItem", false)?if_exists/>
		                        	<#if status?has_content>
		                        		${status.get("description",locale)}
		                        	<#else>
			                        	${uiLabelMap.NoStatusDefinition}
			                        </#if>
		                        </fo:block>
		                    </fo:table-cell>
		                </fo:table-row>
		            </#list>
		        </fo:table-body>
		    </fo:table>
		</fo:block>
	</fo:flow>
	</fo:page-sequence>
</fo:root>
</#escape>