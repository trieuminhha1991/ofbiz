<#escape x as x?xml>
<fo:block font-size="9" margin-top="${margin_top}" font-family="Arial">
	<fo:block>
		<fo:table table-layout="fixed" space-after.optimum="10pt">
		    <fo:table-column/>
			<fo:table-body>
		    	<fo:table-row>
			        <fo:table-cell text-align="center">
			        	<fo:block font-weight="bold" font-size="150%">
			        		<#if requirement.reasonEnumId?has_content>
			        			<#assign enumeration = delegator.findOne("Enumeration", {"enumId" : requirement.reasonEnumId?if_exists}, false)/>
			        			${uiLabelMap.BLDocumentCommon?upper_case} ${StringUtil.wrapString(enumeration.get('description', locale))?upper_case}
			        		</#if>
			        	</fo:block>
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
			<fo:table-column column-width="300pt"/>
			 <fo:table-body>
				<fo:table-row height="15pt">
					<fo:table-cell>
						<fo:block margin-top="0px">
						${uiLabelMap.Facility}:
						<#if requirement.facilityId?has_content>
		        			<#assign fa = delegator.findOne("Facility", {"facilityId" : requirement.facilityId?if_exists}, false)/>
		        			${StringUtil.wrapString(fa.get('facilityName', locale))}
		        		</#if>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row height="15pt">
					<fo:table-cell>
						<fo:block margin-top="0px">
						${uiLabelMap.Address}: 
						<#if requirement.contactMechId?has_content>
		        			<#assign ctm = delegator.findOne("PostalAddressFullNameDetail", {"contactMechId" : requirement.contactMechId?if_exists}, false)/>
		        			${StringUtil.wrapString(ctm.fullName?if_exists)}
		        		</#if>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			 </fo:table-body>
		</fo:table>
	</fo:block>
	<fo:table table-layout="fixed" space-after.optimum="10pt" border="solid 0.2mm black"> 
		<fo:table-column border="solid 0.2mm black" column-width="30pt"/>
		<fo:table-column border="solid 0.2mm black" column-width="70pt"/>
		<#if hasOlbPermission("MODULE", "REQUIREMENT_PRICE", "VIEW")>
			<fo:table-column border="solid 0.2mm black" column-width="200pt"/>
		<#else>
			<fo:table-column border="solid 0.2mm black" column-width="340pt"/>
		</#if>
		<fo:table-column border="solid 0.2mm black" column-width="40pt"/>
		<fo:table-column border="solid 0.2mm black" column-width="60pt"/>
		<#if hasOlbPermission("MODULE", "REQUIREMENT_PRICE", "VIEW")>
			<fo:table-column border="solid 0.2mm black" column-width="60pt"/>
			<fo:table-column border="solid 0.2mm black" column-width="80pt"/>
		</#if>
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
	        <#if hasOlbPermission("MODULE", "REQUIREMENT_PRICE", "VIEW")>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.UnitPrice)}</fo:block>
		          </fo:table-cell>
		        <fo:table-cell text-align="center">
		        	<fo:block font-weight="bold" padding="5pt">${StringUtil.wrapString(uiLabelMap.Monetized)}</fo:block>
		        </fo:table-cell>
	        </#if>
          </fo:table-row>
		</fo:table-header>
		
	   	<fo:table-body>
   			<#assign total = 0>
	   		<#list listItemTmp as item >
	   			<#assign itemTotal = 0>
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
				        		<#if item.productName?length &lt; 60>
					                ${StringUtil.wrapString(item.productName)}
					            <#else>
					                ${StringUtil.wrapString(item.productName?substring(0, 60))} ...
					            </#if>
				        	</#if>
						</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="center">
				   		<fo:block line-height="20px">
				   			<#if item.requireAmount?has_content && item.requireAmount == 'Y'>
				   				<#assign uom = delegator.findOne("Uom", {"uomId" : item.weightUomId?if_exists}, false)/>
		        				${StringUtil.wrapString(uom.get('abbreviation', locale))}
				   			<#else>
		   						<#assign uom = delegator.findOne("Uom", {"uomId" : item.quantityUomId?if_exists}, false)/>
		        				${StringUtil.wrapString(uom.get('abbreviation', locale))}
				   			</#if>
				   		</fo:block>
				   </fo:table-cell>
				   <fo:table-cell text-align="right">
		   				<fo:block line-height="20px"  margin-right="2px">
		   					<#if item.requireAmount?has_content && item.requireAmount == 'Y'>
		   						<#if item.actualExecutedQuantity?has_content>
			   						${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExecutedWeight?if_exists, "#,##0.00", locale)}
			   						<#assign itemTotal = item.actualExecutedWeight>
		   						<#else>
		   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.weight?if_exists, "#,##0.00", locale)}
		   							<#assign itemTotal = item.weight>
		   						</#if>
				   			<#else>
			   					<#if item.actualExecutedQuantity?has_content>
			   						${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.actualExecutedQuantity?if_exists, "#,##0", locale)}
			   						<#assign itemTotal = item.actualExecutedQuantity>
		   						<#else>
		   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.quantity?if_exists, "#,##00", locale)}
		   							<#assign itemTotal = item.quantity>
		   						</#if>
				   			</#if>
		   				</fo:block>
				   </fo:table-cell>
				   <#if hasOlbPermission("MODULE", "REQUIREMENT_PRICE", "VIEW")>
					   <fo:table-cell text-align="right">
			   				<fo:block line-height="20px"  margin-right="2px">
		   						<#if item.unitCost?has_content>
			   						${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(item.unitCost?if_exists, "#,##0.00", locale)}
			   						<#assign itemTotal = itemTotal*item.unitCost/>
		   						<#else>
		   							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(0, "#,##00", locale)}
		   							<#assign itemTotal = 0/>
		   						</#if>
			   				</fo:block>
					   </fo:table-cell>
					   <fo:table-cell  text-align="right">
			        		<fo:block line-height="20px"  margin-right="2px">
		        				${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemTotal?if_exists, "#,##0.00", locale)}
			        		</fo:block>
					   </fo:table-cell>
				   </#if>
				   <#assign index = index + 1>
				   <#assign total = total + itemTotal?if_exists/>
			   </fo:table-row>
		    </#list>
		    <#if hasOlbPermission("MODULE", "REQUIREMENT_PRICE", "VIEW")>
			    <#if displayTotal == true>
				    <fo:table-row border="solid 0.2mm black">
			            <fo:table-cell text-align="left" number-columns-spanned="6">
			        		<fo:block line-height="20px" margin-left="2pt" margin-top="2pt" font-weight="bold">
		            			${uiLabelMap.OrderItemsSubTotal}
	            			</fo:block>
				        </fo:table-cell>
			            <fo:table-cell text-align="right" number-columns-spanned="1">
			        		<fo:block line-height="20px" margin-right="2pt" margin-top="2pt"> 
			        			${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(total?if_exists, "#,##0.00", locale)}
			            	</fo:block>
				        </fo:table-cell>
				    </fo:table-row>
				    <fo:table-row border="solid 0.2mm black">
					    <fo:table-cell text-align="left" number-columns-spanned="7">
			        		<fo:block line-height="20px" font-weight="bold" margin-left="5px">
			        			${uiLabelMap.ByString}: 
			        			<#if total?has_content>
			        			<#assign totalString = Static["com.olbius.baselogistics.util.LogisticsStringUtil"].ConvertDecimalToString(total)>
			        				${totalString?if_exists}
			        			</#if>
			        		</fo:block>
					    </fo:table-cell>
					</fo:table-row>
			    </#if>
		    </#if>
		</fo:table-body>
	</fo:table>
</fo:block>
</#escape>