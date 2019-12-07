<#escape x as x?xml>
<fo:block>
<#if true>
	<#--header top-->
	<fo:block>
		<fo:block text-align="center" text-transform="uppercase" font-weight="700" font-size="18pt" margin-top="0.2cm" margin-bottom="0.2cm">
			${uiLabelMap.DASalesOrderFormTitle}
		</fo:block>
		<fo:block text-align="center" font-weight="600" font-style="italic" font-size="12pt" margin-top="0.2cm" margin-bottom="0.2cm">
			${uiLabelMap.DPMonth} <#if orderDateTime.get(2) &lt; 9>0${orderDateTime.get(2) + 1}<#else>${orderDateTime.get(2) + 1}</#if> ${uiLabelMap.DPYearLowercase} ${orderDateTime.get(1)}
		</fo:block>
		<fo:block margin-top="0.2cm" font-size="11pt">
			<fo:table>
				<fo:table-column column-width="37%"/>
				<fo:table-column column-width="30%"/>
				<fo:table-column column-width="32%"/>
				<fo:table-body>
					<fo:table-row>
						<fo:table-cell padding-top="10px">
							<fo:block>
								${uiLabelMap.DADistributor}: <fo:inline font-weight="bold">${displayPartyNameResult?if_exists}</fo:inline>
							</fo:block>
							<fo:block>
								${uiLabelMap.DAArea}: <fo:inline font-weight="bold">${areaGeoName?if_exists}</fo:inline>
							</fo:block>
							<fo:block>
								${uiLabelMap.DAAddress}: 
								<fo:inline font-weight="bold">
									<#list listAddressCust as addressCust>
										${addressCust}
									</#list>
								</fo:inline>
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="10px" padding-left="0.5cm">
							<fo:block>
								${uiLabelMap.DASUP}: ${displaySUPsNameResult}
							</fo:block>
						</fo:table-cell>
						<fo:table-cell padding-top="10px">
							<fo:block>
								${uiLabelMap.DAOrderNumber}: ${orderHeader.orderId}
							</fo:block>
							<fo:block>
								${uiLabelMap.DACreateOrderDate}: 
								<fo:inline>
									<#if orderHeader.orderDate?exists>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
									</#if>
								</fo:inline>
							</fo:block>
							<fo:block>
								${uiLabelMap.DADesiredDeliveryDate2}: 
								<fo:inline>
									<#if desiredDeliveryDate?exists>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
									</#if>
								</fo:inline>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</fo:table-body>
			</fo:table>
		</fo:block>
	</fo:block>
	
	<#--body-->
	<fo:block margin-top="4mm">
		<fo:table border-color="black" border-style="solid" border-width="1pt">
			<fo:table-column column-width="1cm"/>
			<fo:table-column/>
		    <#--<fo:table-column/>-->
		    <fo:table-column column-width="1cm"/>
		    <fo:table-column column-width="1cm"/>
		    <fo:table-column column-width="1cm"/>
		    <fo:table-column column-width="1cm"/>
		    <fo:table-column column-width="1cm"/>
		    <fo:table-column column-width="1.5cm"/>
		    <fo:table-column/>
		    <fo:table-column/>
		    <fo:table-column/>
		    <fo:table-header>
		        <fo:table-row border-color="black" background-color="#FFFF99" font-size="8pt">
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold">${uiLabelMap.DANo}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold" text-transform="uppercase">${uiLabelMap.DAProductName}</fo:block>
		        	</fo:table-cell>
		            <#--<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold" text-transform="uppercase">${uiLabelMap.DABarcode}</fo:block>
		        	</fo:table-cell>-->
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold" text-transform="uppercase">${uiLabelMap.DAPackingPerTray}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold" text-transform="uppercase">${uiLabelMap.DAQuantityUomId}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-columns-spanned="3">
		            	<fo:block font-weight="bold" text-transform="uppercase">${uiLabelMap.DAQuantity}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold" text-transform="uppercase">${uiLabelMap.DASumTray}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold" text-transform="uppercase">${uiLabelMap.DAPrice} ${uiLabelMap.DAParenthesisBeforeVAT}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold" text-transform="uppercase">${uiLabelMap.DASubTotalPrice} ${uiLabelMap.DAParenthesisBeforeVAT}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-rows-spanned="2">
		            	<fo:block font-weight="bold" text-transform="uppercase">${uiLabelMap.DAPrice} ${uiLabelMap.DAParenthesisAfterVAT}</fo:block>
		        	</fo:table-cell>
		        </fo:table-row>
		        <fo:table-row border-color="black" background-color="#FFFF99" text-transform="uppercase" font-size="8pt">
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DAOrdered}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DAPromos}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.DASum}</fo:block>
		        	</fo:table-cell>
		        </fo:table-row>
		    </fo:table-header>
		    <fo:table-body>
				<#list listItemLine as itemLine>
					<#assign productId = itemLine.productId?if_exists/>
    				<#assign product = itemLine.product?if_exists/>
		        <fo:table-row>
		        	<fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>${itemLine_index + 1}</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" border="1pt solid black">
		                <fo:block>
		                	${itemLine.productName?if_exists}
		                </fo:block>
		            </fo:table-cell>
		            <#--<fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>
		                	${itemLine.barcode?if_exists}
						</fo:block>
		            </fo:table-cell>-->
		            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>
							${itemLine.packingPerTray?if_exists}
						</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
		                <fo:block>
		                	${itemLine.quantityUomDescription?if_exists}
	                	</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block>
		                	${itemLine.quantity?if_exists}
	                	</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block>
            				
						</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block>
		                	${itemLine.quantity?if_exists}
                		</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block>
		                	<#if itemLine.sumTray?exists>
			                	${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.sumTray, "#0.00", locale)}
        					</#if>
						</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block>
		                	<@ofbizCurrency amount=itemLine.unitPriceBeVAT isoCode=currencyUomId/>
                		</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block>
		                	<@ofbizCurrency amount=itemLine.subTotalBeVAT isoCode=currencyUomId rounding=0/>
                		</fo:block>
		            </fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block>
		                	<@ofbizCurrency amount=itemLine.unitPriceAfVAT isoCode=currencyUomId rounding=0/>
                		</fo:block>
		            </fo:table-cell>
				</fo:table-row>
				</#list>
				<#-- display tax prices sum -->
				<#list listTaxTotal as taxTotalItem>
					<fo:table-row>
						<fo:table-cell padding="1mm" text-align="right" number-columns-spanned="10" border="1pt solid black">
							<fo:block><#if taxTotalItem.description?exists>${StringUtil.wrapString(taxTotalItem.description)}</#if></fo:block>
						</fo:table-cell>
						<fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
							<fo:block>
								<#if taxTotalItem.amount?exists && taxTotalItem.amount &lt; 0>
									(<@ofbizCurrency amount=-taxTotalItem.amount isoCode=currencyUomId/>)
								<#elseif taxTotalItem.amount?exists>
									<@ofbizCurrency amount=taxTotalItem.amount isoCode=currencyUomId/>
								</#if>
							</fo:block>
						</fo:table-cell>
					</fo:table-row>
				</#list>
				
				<#list orderHeaderAdjustments as orderHeaderAdjustment>
	                <#assign adjustmentType = orderHeaderAdjustment.getRelatedOne("OrderAdjustmentType", false)>
	                <#assign adjustmentAmount = Static["org.ofbiz.order.order.OrderReadHelper"].calcOrderAdjustment(orderHeaderAdjustment, orderSubTotal)>
	                <#if adjustmentAmount != 0>
	                    <fo:table-row>
	                        <fo:table-cell padding="1mm" text-align="right" number-columns-spanned="10" border="1pt solid black">
	                            <fo:block>
		                            <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description} </#if>
		                            <#--${adjustmentType.get("description", locale)}-->
		                            <#if orderHeaderAdjustment.comments?has_content> ${orderHeaderAdjustment.comments}</#if>
	                            </fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
	                        	<fo:block>
	                        		<#if (adjustmentAmount &lt; 0)>
	                            		<#assign adjustmentAmountNegative = -adjustmentAmount>
		                            	(<@ofbizCurrency amount=adjustmentAmountNegative isoCode=currencyUomId rounding=0/>)
		                            <#else>
		                            	<@ofbizCurrency amount=adjustmentAmount isoCode=currencyUomId rounding=0/>
		                            </#if>
	                        	</fo:block>
	                        </fo:table-cell>
	                    </fo:table-row>
	                </#if>
	            </#list>
				
				<#-- subtotal -->
      			<fo:table-row>
        			<fo:table-cell padding="1mm" text-align="right" number-columns-spanned="10" font-weight="bold" border="1pt solid black">
        				<fo:block>
        					${uiLabelMap.DAOrderItemsSubTotal}
        				</fo:block>
        			</fo:table-cell>
        			<fo:table-cell padding="1mm" text-align="right" border="1pt solid black" font-size="11pt">
        				<fo:block>
        					<#if (orderSubTotal &lt; 0)>
	                    		<#assign orderSubTotalNegative = -orderSubTotal>
	                    		(<@ofbizCurrency amount=orderSubTotalNegative isoCode=currencyUomId rounding=0/>)
	                    	<#else>
	                    		<@ofbizCurrency amount=orderSubTotal isoCode=currencyUomId rounding=0/>
	                		</#if>
        				</fo:block>
    				</fo:table-cell>
      			</fo:table-row>
      			
      			<#-- other adjustments -->
	            <fo:table-row>
	              	<fo:table-cell padding="1mm" text-align="right" number-columns-spanned="10" font-weight="bold" border="1pt solid black">
	              		<fo:block>
	              			${uiLabelMap.DATotalOrderAdjustments}
	              		</fo:block>
	              	</fo:table-cell>
	              	<fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
	              		<fo:block>
	              			<#if (otherAdjAmount &lt; 0)>
	                    		<#assign otherAdjAmountNegative = -otherAdjAmount>
								(<@ofbizCurrency amount=otherAdjAmountNegative isoCode=currencyUomId rounding=0/>)
							<#else>
								<@ofbizCurrency amount=otherAdjAmount isoCode=currencyUomId rounding=0/>
							</#if>
	              		</fo:block>
					</fo:table-cell>
	            </fo:table-row>
      			
      			<#-- tax adjustments -->
	          	<fo:table-row>
		            <fo:table-cell padding="1mm" text-align="right" number-columns-spanned="10" font-weight="bold" border="1pt solid black">
						<fo:block>${uiLabelMap.OrderTotalSalesTax}</fo:block>
					</fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		            	<fo:block>
			            	<#if (taxAmount &lt; 0)>
	                    		<#assign taxAmountNegative = -taxAmount>
			            		(<@ofbizCurrency amount=taxAmountNegative isoCode=currencyUomId rounding=0/>)
			            	<#else>
			            		<@ofbizCurrency amount=taxAmount isoCode=currencyUomId rounding=0/>
			            	</#if>
		            	</fo:block>
		            </fo:table-cell>
	          	</fo:table-row>
      			
      			<#-- shipping adjustments -->
      			<#if shippingAmount?exists && shippingAmount?has_content && (shippingAmount != 0)>
      				<fo:table-row>
		              	<fo:table-cell padding="1mm" text-align="right" number-columns-spanned="10" font-weight="bold" border="1pt solid black">
		              		<fo:block>
		              			${uiLabelMap.OrderTotalShippingAndHandling}
		              		</fo:block>
		              	</fo:table-cell>
		              	<fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		              		<fo:block>${shippingAmount}
		              			<#if (shippingAmount &lt; 0)>
		                    		<#assign shippingAmountNegative = -shippingAmount>
									(<@ofbizCurrency amount=shippingAmountNegative isoCode=currencyUomId rounding=0/>)
								<#else>
									<@ofbizCurrency amount=shippingAmount isoCode=currencyUomId rounding=0/>
								</#if>
		              		</fo:block>
						</fo:table-cell>
		            </fo:table-row>
	          	</#if>
      			
	          	<#-- grand total -->
	          	<fo:table-row>
		            <fo:table-cell padding="1mm" text-align="right" number-columns-spanned="10" font-weight="bold" text-transform="uppercase" border="1pt solid black">
		             	<fo:block>
		             		${uiLabelMap.DATotalAmountPayment}
		             	</fo:block>
	             	</fo:table-cell>
		            <fo:table-cell padding="1mm" text-align="right" font-weight="bold" border="1pt solid black">
		            	<fo:block>
		            		<#if (grandTotal &lt; 0)>
	                    		<#assign grandTotalNegative = -grandTotal>
			            		(<@ofbizCurrency amount=grandTotalNegative isoCode=currencyUomId rounding=0/>)
			            	<#else>
			            		<@ofbizCurrency amount=grandTotal isoCode=currencyUomId rounding=0/>
			            	</#if>
		            	</fo:block>
		            </fo:table-cell>
	          	</fo:table-row>
			</fo:table-body>
		</fo:table>
	</fo:block>
	
	<#--footer-->
	<#--
	<fo:block margin-top="0.5cm">
		<fo:block>${uiLabelMap.DPFooterThank1}</fo:block>
		<fo:block font-weight="bold" margin-top="0.2cm">${uiLabelMap.DPFooterThank2}</fo:block>
		<fo:block text-align="right" font-weight="bold" font-style="italic" margin-top="0.2cm">${uiLabelMap.DPHaNoi}, ${uiLabelMap.DPDayLowercase} 
		<#if fromDateDateTime?exists><#if fromDateDateTime.get(5) &lt; 9>0${fromDateDateTime.get(5)}<#else>${fromDateDateTime.get(5)}</#if> ${uiLabelMap.DPMonthLowercase} <#if fromDateDateTime.get(2) &lt; 9>0${fromDateDateTime.get(2) + 1}<#else>${fromDateDateTime.get(2) + 1}</#if> ${uiLabelMap.DPYearLowercase} ${fromDateDateTime.get(1)}
		<#else>... ${uiLabelMap.DPMonthLowercase} ... ${productQuotation.fromDate.getYear()}</#if></fo:block>
	</fo:block>
	-->
<#else>
	<fo:block>
		<fo:block text-align="center" text-transform="uppercase" font-weight="700" font-size="14pt" margin-top="0.2cm" margin-bottom="0.2cm">
			${uiLabelMap.DAOrderFormTitle}
		</fo:block>
	</fo:block>
	
	<#--body-->
	<fo:block margin-top="2mm" text-transform="uppercase">
		<fo:block>${uiLabelMap.DAThisOrderNotAvaiable}</fo:block>
	</fo:block>
</#if>
</fo:block>
</#escape>