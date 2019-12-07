<#escape x as x?xml>
<fo:block font-size="9" margin-top="${margin_top}" font-family="Arial">
	<fo:block>
		<fo:table table-layout="fixed" space-after.optimum="10pt">
		    <fo:table-column/>
			<fo:table-body>
		    	<fo:table-row>
			        <fo:table-cell text-align="center">
			        	<fo:block font-weight="bold" font-size="150%">${StringUtil.wrapString(uiLabelMap.BLReturnDocumentUppercase)}</fo:block>
			        </fo:table-cell>
		      	</fo:table-row>
		      	<fo:table-row>
		    		<fo:table-cell text-align="center">
		    			<fo:block>${labelInstance}</fo:block>
		    		</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
		    		<fo:table-cell text-align="center">
		    		</fo:table-cell>
				</fo:table-row>
		    </fo:table-body>
		 </fo:table>
	</fo:block>
	<fo:block margin-bottom="10px">
		<fo:table table-layout="fixed" space-after.optimum="10pt">
			<fo:table-column column-width="200pt"/>
			 <fo:table-body>
				<fo:table-row height="15pt">
					<fo:table-cell>
						<fo:block margin-top="0px">${uiLabelMap.Supplier}: ${fullName?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="15pt">
					<fo:table-cell>
						<fo:block margin-top="0px">${uiLabelMap.Facility}: ${facilityName?if_exists}</fo:block>
					</fo:table-cell>
				</fo:table-row>
			 </fo:table-body>
		</fo:table>
	</fo:block>
	<fo:table table-layout="fixed" space-after.optimum="10pt"> 
		<fo:table-column border="solid 0.2mm black" column-width="30pt"/>
		<fo:table-column border="solid 0.2mm black" column-width="65pt"/>
		<fo:table-column border="solid 0.2mm black" column-width="230pt"/>
		<fo:table-column border="solid 0.2mm black" column-width="30pt"/>
		<fo:table-column border="solid 0.2mm black" column-width="60pt"/>
		<fo:table-column border="solid 0.2mm black" column-width="60pt"/>
		<fo:table-column border="solid 0.2mm black" column-width="80pt"/>
		<fo:table-header border="solid 0.2mm black">
          <fo:table-row>
	          <fo:table-cell text-align="center">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.SequenceId)}</fo:block>
	        </fo:table-cell>
	          <fo:table-cell text-align="center">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.BLSKUCode)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.ProductName)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center">
	      	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.UnitSum)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.Quantity)}</fo:block>
	        </fo:table-cell>
	        <fo:table-cell text-align="center">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.UnitPrice)}</fo:block>
	          </fo:table-cell>
	        <fo:table-cell text-align="center">
	        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.BLMoneyTotalNoVAT)}</fo:block>
	        </fo:table-cell>
          </fo:table-row>
		</fo:table-header>
		
	   	<fo:table-body>
	   		<#list listItemTmp as item >
		   		<fo:table-row border="solid 0.2mm black">
				  	<fo:table-cell  text-align="left">
				       <fo:block line-height="20px" margin-left="2px">${index?if_exists}</fo:block>
				   	</fo:table-cell>
				  	<fo:table-cell  text-align="left">
				       <fo:block line-height="20px" margin-left="2px">${item.productCode?if_exists}</fo:block>
				   	</fo:table-cell>
					<fo:table-cell  text-align="left">
						<fo:block line-height="20px" margin-left="2px">
							<#if item.productName?has_content>
				        		<#if item.productName?length &lt; 50>
					                ${StringUtil.wrapString(item.productName?html)}
					            <#else>
					                ${StringUtil.wrapString(item.productName?html?substring(0, 50))} ...
					            </#if>
				        	</#if>
						</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="center">
				   		<fo:block line-height="20px">
				   			<#if item.requireAmount?has_content && item.requireAmount == 'Y'>
				   				${item.weightUomId?if_exists}
				   			<#else>
			   					${item.quantityUomId?if_exists}
				   			</#if>
				   		</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="right">
		   				<fo:block line-height="20px"  margin-right="2px">
		   				<#if item.requireAmount?has_content && item.requireAmount == 'Y'>
			   				<#if item.receivedAmount?has_content>
			   					${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.receivedAmount?if_exists, "#,##0", locale)}
			   				<#else>
			   					${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.returnAmount?if_exists, "#,##0", locale)}
			   				</#if>
		   				<#else>
		   					<#if item.receivedQuantity?has_content>
			   					${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.receivedQuantity?if_exists, "#,##0", locale)}
			   				<#else>
			   					${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.returnQuantity?if_exists, "#,##0", locale)}
			   				</#if>
		   				</#if>
		   				</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="right">
		   				<fo:block line-height="20px"  margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.returnPrice?if_exists, "#,##0", locale)}</fo:block>
				   </fo:table-cell>
				   <fo:table-cell  text-align="right">
					   <#if item.total?has_content>
					   <fo:block line-height="20px" margin-right="2px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.total?if_exists, "#,##0.00", locale)}</fo:block>
			        	<#else>
			        		<fo:block></fo:block>
			        	</#if>
				   </fo:table-cell>
				   <#assign index = index + 1>
			   </fo:table-row>
		    </#list>
		    <#if displayTotal == true>
			    <fo:table-row border="solid 0.2mm black">
			        <fo:table-cell text-align="left" number-columns-spanned="6">
			        	<#assign totalDlvString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].ConvertDecimalToString(total)>
			        	<fo:block line-height="20px" font-weight="bold" margin-left="5px">${uiLabelMap.Summary}: ${totalDlvString?if_exists}</fo:block>
			        </fo:table-cell>
			        <fo:table-cell text-align="right" number-columns-spanned="1">
				        <#if total?has_content && total != 0>
				        	<fo:block line-height="20px" margin-right="2px" text-align="right" font-weight="bold" margin-left="5px">${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(total?if_exists, "#,##0.00", locale)}</fo:block>
			        	<#else>
			        		<fo:block></fo:block>
			        	</#if>
			        </fo:table-cell>
			    </fo:table-row>
		    </#if>
		</fo:table-body>
	</fo:table>
</fo:block>
</#escape>