<#escape x as x?xml>
<fo:block>
<#if orderHeader?exists>
	<fo:block text-align="center" margin-bottom="0.5cm">
		<#if hasOlbPermission("MODULE", "DISTRIBUTOR", "ADMIN")>
			<fo:block text-transform="uppercase" font-size="18pt" font-weight="700">
				${uiLabelMap.BSPurchaseOrder}
			</fo:block>
		<#else>
			<fo:block text-transform="uppercase" font-size="18pt" font-weight="700">
				${uiLabelMap.BSSalesOrderFormTitle}
			</fo:block>
		</#if>
		<#--
		<fo:block font-style="italic" font-size="10pt">
			${uiLabelMap.BSDate}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(nowTimestamp, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
		</fo:block>
		<fo:block font-style="italic" font-size="10pt">
			${uiLabelMap.BSId}: ${orderHeader.orderId?if_exists}
		</fo:block>-->
	</fo:block>
	
	<fo:block>
		<fo:table border-color="black" border-style="solid" border-width="0">
			<fo:table-column column-width="1.5cm"/>
			<fo:table-column column-width="1.3cm"/>
			<fo:table-column column-width="0.5cm"/>
			<fo:table-column column-width="0.8cm"/>
			<fo:table-column column-width="5cm"/>
		    <fo:table-column column-width="3.3cm"/>
		    <fo:table-column column-width="0.5cm"/>
		    <fo:table-column/>
		    <fo:table-body>
		    	<fo:table-row>
		        	<fo:table-cell padding="1mm" number-columns-spanned="2">
		                <fo:block padding-bottom="1mm">${uiLabelMap.BSCustomerName}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" font-weight="700" number-columns-spanned="6">
		                <fo:block padding-bottom="1mm">${customerFullName?if_exists}</fo:block>
		            </fo:table-cell>
		        </fo:table-row>
		    	<fo:table-row>
		        	<fo:table-cell padding="1mm">
		                <fo:block padding-bottom="2mm">${uiLabelMap.BSAddress}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" number-columns-spanned="7">
		                <fo:block padding-bottom="2mm">
		                	<#if shippingAddressList?has_content>
							<fo:list-block><#-- space-after="5mm"-->
								<#list shippingAddressList as shippingAddressFullName>
								<fo:list-item>
								  	<fo:list-item-label>
								  	</fo:list-item-label>
								  	<fo:list-item-body>
								       	<fo:block>${shippingAddressFullName}</fo:block>
								  	</fo:list-item-body>
								</fo:list-item>
								</#list>
							</fo:list-block>
							</#if>
		                </fo:block>
		            </fo:table-cell>
		        </fo:table-row>
		        <fo:table-row>
		        	<fo:table-cell padding="1mm" number-columns-spanned="2">
		                <fo:block>${uiLabelMap.BSCustomerId}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" number-columns-spanned="3">
		                <fo:block>${customerIdOrCode?if_exists}</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm">
		                <fo:block>${uiLabelMap.BSDesiredDeliveryDate}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" number-columns-spanned="2">
		                <fo:block>
		                	<#if desiredDeliveryDate?exists>
								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
							</#if>
		                </fo:block>
		            </fo:table-cell>
		        </fo:table-row>
		        <fo:table-row>
		        	<fo:table-cell padding="1mm" number-columns-spanned="2">
		                <fo:block>${uiLabelMap.BSOrderDate}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" number-columns-spanned="3">
		                <fo:block>
							<#if orderHeader.orderDate?exists>
								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
							</#if>
						</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm">
		                <fo:block>${uiLabelMap.BSShipAfterDate}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" number-columns-spanned="2">
		                <fo:block>
		                	<#if shipAfterDate?exists>
								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipAfterDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
							</#if>
		                </fo:block>
		            </fo:table-cell>
		        </fo:table-row>
		        <fo:table-row>
		        	<fo:table-cell padding="1mm" number-columns-spanned="2">
		                <fo:block>${uiLabelMap.BSAgreementCode}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" number-columns-spanned="3">
		                <fo:block>
							${agreementCode?if_exists}
						</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm">
		                <fo:block>${uiLabelMap.BSShipBeforeDate}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" number-columns-spanned="2">
		                <fo:block>
		                	<#if shipBeforeDate?exists>
								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipBeforeDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
							</#if>
		                </fo:block>
		            </fo:table-cell>
		        </fo:table-row>
		        <fo:table-row>
		        	<fo:table-cell padding="1mm" number-columns-spanned="2">
		                <fo:block>${uiLabelMap.BSPSSalesChannel}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" number-columns-spanned="3">
		                <fo:block>
							<#if productStore?exists>${productStore.storeName?if_exists}</#if>
						</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm" number-columns-spanned="2">
		                <fo:block>${uiLabelMap.BSSalesExecutive}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm">
		                <fo:block>${salesExecutiveFullName?if_exists}</fo:block>
		            </fo:table-cell>
		        </fo:table-row>
		        <fo:table-row>
		        	<fo:table-cell padding="1mm" number-columns-spanned="4">
		                <fo:block>${uiLabelMap.BSPaymentMethod}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm">
		                <fo:block>
							${paymentMethodTypeName?if_exists}
						</fo:block>
		            </fo:table-cell>
		        	<#if callcenterFullName?exists>
		        	<fo:table-cell padding="1mm" number-columns-spanned="2">
		                <fo:block>${uiLabelMap.BSCallcenter}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm">
		                <fo:block>${callcenterFullName?if_exists}</fo:block>
		            </fo:table-cell>
		        	</#if>
					<#if salesadminFullName?exists>
		        	<fo:table-cell padding="1mm" number-columns-spanned="2">
		                <fo:block>${uiLabelMap.BSSalesAdmin}:</fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm">
		                <fo:block>${salesadminFullName?if_exists}</fo:block>
		            </fo:table-cell>
		        	</#if>
					<#if !callcenterFullName?exists && !salesadminFullName?exists>
					<fo:table-cell padding="1mm" number-columns-spanned="2">
		                <fo:block></fo:block>
		            </fo:table-cell>
		        	<fo:table-cell padding="1mm">
		                <fo:block></fo:block>
		            </fo:table-cell>
					</#if>
		        </fo:table-row>
		    </fo:table-body>
		</fo:table>
	</fo:block>
	
	<fo:block>
		<fo:block text-align="right" font-style="italic" font-size="10pt">${uiLabelMap.BSCalculateUomId}: ${currencyUomId?if_exists}</fo:block>
		<fo:table border-color="black" border-style="solid" border-width="1pt">
			<fo:table-column column-width="1cm"/>
			<fo:table-column column-width="2.8cm"/>
			<fo:table-column/>
			<fo:table-column column-width="0.7cm"/>
		    <fo:table-column column-width="1.3cm"/>
		    <fo:table-column column-width="1.3cm"/>
		    <fo:table-column column-width="2.6cm"/>
		    <fo:table-column column-width="2.9cm"/>
		    <fo:table-column column-width="3.1cm"/>
		    <fo:table-header>
		    	<fo:table-row border-color="black" background-color="#DDDDDD">
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.BSSTT}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.BSProductId}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black" number-columns-spanned="2">
		            	<fo:block font-weight="bold">${uiLabelMap.BSProductName}</fo:block>
		        	</fo:table-cell>
		            <#--<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">-->
		            	<#--<fo:block font-weight="bold">${uiLabelMap.BSProdPromo}</fo:block>-->
		        	<#--</fo:table-cell>-->
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.BSUom}</fo:block>
		        	</fo:table-cell>
		        	<fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.BSQuantity}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.BSPriceBeforeVAT}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.BSAdjustment}</fo:block>
		        	</fo:table-cell>
		            <fo:table-cell padding="2mm 1mm" text-align="center" display-align="center" border="1pt solid black">
		            	<fo:block font-weight="bold">${uiLabelMap.BSItemTotal} ${uiLabelMap.BSParenthesisBeforeVAT}</fo:block>
		        	</fo:table-cell>
		        </fo:table-row>
		    </fo:table-header>
		    <fo:table-body>
	    		<#list listItemLine as itemLine>
	    			<#assign itemType = itemLine.orderItem.getRelatedOne("OrderItemType", false)?if_exists>
	    			<#assign productId = itemLine.productId?if_exists/>
    				<#assign product = itemLine.product?if_exists/>
	    			<fo:table-row>
			        	<fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
			                <fo:block>${itemLine_index + 1}</fo:block>
			            </fo:table-cell>
			            <#if productId?exists && productId == "shoppingcart.CommentLine">
			            	<fo:table-cell padding="1mm" text-align="center" border="1pt solid black" number-columns-spanned="8">
				                <fo:block>${itemLine.itemDescription?if_exists}</fo:block>
				            </fo:table-cell>
			            <#else>
			            	<fo:table-cell padding="1mm" text-align="left" border="1pt solid black">
				                <fo:block>
				                	<#if itemLine.supplierProductId?has_content>
                                        ${itemLine.supplierProductId}
                                    <#elseif productId?exists>
                                         ${itemLine.productCode?default(productId)}
                                    <#elseif itemLine.orderItemType?exists>
                                        ${itemLine.orderItemType.description}
                                    </#if>
				                </fo:block>
				            </fo:table-cell>
			            	<fo:table-cell padding="1mm" text-align="left" border="1pt solid black" number-columns-spanned="2">
				                <fo:block>
				                	<#if itemLine.supplierProductId?has_content>
                                        ${itemLine.itemDescription?if_exists}
                                    <#elseif productId?exists>
                                        ${itemLine.itemDescription?if_exists}
                                        <#if (product.salesDiscontinuationDate)?exists && Static["org.ofbiz.base.util.UtilDateTime"].nowTimestamp().after(product.salesDiscontinuationDate)>
                                        	<fo:block font-color="red">
                                        		${uiLabelMap.OrderItemDiscontinued}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(product.salesDiscontinuationDate, "", locale, timeZone)!}
                                        	</fo:block>
                                        </#if>
                                    <#else>
                                        ${itemLine.itemDescription?if_exists}
                                    </#if>
									<#if itemLine.comments?exists>
										 - <fo:inline>(${itemLine.comments})</fo:inline>
									</#if>
									<#if "PRODPROMO_ORDER_ITEM" == itemLine.orderItemTypeId>
										 - (<fo:inline font-weight="bold">${uiLabelMap.BSProductReturnPromo}</fo:inline>)
									</#if>
				                </fo:block>
				            </fo:table-cell>
				            <#--<fo:table-cell padding="1mm" text-align="center" border="1pt solid black">-->
				                <#--<fo:block>-->
				                	<#--${itemLine.isPromo?if_exists}-->
				            	<#--</fo:block>-->
				            <#--</fo:table-cell>-->
				            <fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
				                <fo:block>
				                	${itemLine.quantityUomDescription?if_exists}
				            	</fo:block>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
				                <fo:block>
									<#if itemLine.quantity?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.quantity, "#,##0", locale)}</#if>
				            	</fo:block>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
				                <fo:block><#-- Unit price -->
									<#if itemLine.unitPriceBeVAT?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.unitPriceBeVAT, "#,##0.00", locale)}</#if>
				            	</fo:block>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
				                <fo:block><#-- Adjustment -->
									<#if itemLine.adjustment?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.adjustment, "#,##0.00", locale)}</#if>
				            	</fo:block>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
				                <fo:block><#-- Sub total before VAT -->
									<#if itemLine.subTotalBeVAT?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.subTotalBeVAT, "#,##0.00", locale)}</#if>
				            	</fo:block>
				            </fo:table-cell>
				            <#-- Unit price after VAT - new column-->
			            </#if>
			    	</fo:table-row>
	    		</#list>

				<#-- display tax prices sum -->
				<#list listTaxTotal as taxTotalItem>
			    	<fo:table-row>
			    		<fo:table-cell padding="1mm" text-align="right" border="1pt solid black" number-columns-spanned="8">
			                <fo:block>
			                	<#if taxTotalItem.description?exists>${StringUtil.wrapString(taxTotalItem.description)}</#if>
			            	</fo:block>
			            </fo:table-cell>
			    		<fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
			                <fo:block>
			                	<#if taxTotalItem.amount?exists && taxTotalItem.amount &lt; 0>
									(${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(-taxTotalItem.amount, "#,##0.00", locale)})
								<#elseif taxTotalItem.amount?exists>
									${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxTotalItem.amount, "#,##0.00", locale)}
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
		                	<fo:table-cell padding="1mm" text-align="right" border="1pt solid black" number-columns-spanned="8">
				                <fo:block>
				                	<#assign adjPrinted = false>
		                            <#if orderHeaderAdjustment.comments?has_content>${orderHeaderAdjustment.comments}<#assign adjPrinted = true></#if>
		                            <#if orderHeaderAdjustment.description?has_content>${orderHeaderAdjustment.description}<#assign adjPrinted = true></#if>
		                            <#if !adjPrinted><span>${adjustmentType.get("description", locale)}</span></#if>
				            	</fo:block>
				            </fo:table-cell>
				    		<fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
				                <fo:block>
				                	<#if (adjustmentAmount &lt; 0)>
                                		<#assign adjustmentAmountNegative = -adjustmentAmount>
		                            	(${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(adjustmentAmountNegative, "#,##0.00", locale)})
		                            <#else>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(adjustmentAmount, "#,##0.00", locale)}
		                            </#if>
				            	</fo:block>
				            </fo:table-cell>
				    	</fo:table-row>
	                </#if>
		    	</#list>

				<#-- subtotal -->
				<fo:table-row>
                	<fo:table-cell padding="1mm" text-align="right" border="1pt solid black" number-columns-spanned="8">
		                <fo:block font-weight="bold" padding-top="1mm">
		                	${uiLabelMap.BSOrderItemsSubTotal}
		            	</fo:block>
		            </fo:table-cell>
		    		<fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block padding-top="1mm">
		                	<#if (orderSubTotal &lt; 0)>
                        		<#assign orderSubTotalNegative = -orderSubTotal>
                        		(${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(orderSubTotalNegative, "#,##0.00", locale)})
                        	<#else>
								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(orderSubTotal, "#,##0.00", locale)}
                    		</#if>
		            	</fo:block>
		            </fo:table-cell>
		    	</fo:table-row>
				
				<#-- other adjustments -->
				<fo:table-row>
                	<fo:table-cell padding="1mm" text-align="right" border="1pt solid black" number-columns-spanned="8">
		                <fo:block font-weight="bold" padding-top="1mm">
		                	${uiLabelMap.BSTotalOrderAdjustments}
		            	</fo:block>
		            </fo:table-cell>
		    		<fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block padding-top="1mm">
		                	<#if (otherAdjAmount &lt; 0)>
                        		<#assign otherAdjAmountNegative = -otherAdjAmount>
								(${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(otherAdjAmountNegative, "#,##0.00", locale)})
							<#else>
								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(otherAdjAmount, "#,##0.00", locale)}
							</#if>
		            	</fo:block>
		            </fo:table-cell>
		    	</fo:table-row>

				<#-- tax adjustments -->
				<fo:table-row>
                	<fo:table-cell padding="1mm" text-align="right" border="1pt solid black" number-columns-spanned="8">
		                <fo:block font-weight="bold" padding-top="1mm">
		                	${uiLabelMap.BSTotalSalesTax}
		            	</fo:block>
		            </fo:table-cell>
		    		<fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block padding-top="1mm">
		                	<#if (taxAmount &lt; 0)>
                        		<#assign taxAmountNegative = -taxAmount>
			            		(${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxAmountNegative, "#,##0.00", locale)})
			            	<#else>
			            		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(taxAmount, "#,##0.00", locale)}
			            	</#if>
		            	</fo:block>
		            </fo:table-cell>
		    	</fo:table-row>
				
				<#-- shipping adjustments -->
				<fo:table-row>
                	<fo:table-cell padding="1mm" text-align="right" border="1pt solid black" number-columns-spanned="8">
		                <fo:block font-weight="bold" padding-top="1mm">
		                	${uiLabelMap.BSTotalShippingAndHandling}
		            	</fo:block>
		            </fo:table-cell>
		    		<fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block padding-top="1mm">
							<#if shippingAmount?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(shippingAmount, "#,##0.00", locale)}</#if>
		            	</fo:block>
		            </fo:table-cell>
		    	</fo:table-row>
				
				<#-- grand total -->
				<fo:table-row>
                	<fo:table-cell padding="1mm" text-align="right" border="1pt solid black" number-columns-spanned="8">
		                <fo:block font-weight="bold" text-transform="uppercase" padding-top="2mm">
		                	${uiLabelMap.BSTotalAmountPayment}
		            	</fo:block>
		            </fo:table-cell>
		    		<fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
		                <fo:block font-weight="bold" padding-top="2mm">
		                	<#if (grandTotal &lt; 0)>
                        		<#assign grandTotalNegative = -grandTotal>
                        		<#assign accountOneValue = -grandTotal />
			            		(${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotalNegative, "#,##0.00", locale)})
			            	<#else>
			            		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(grandTotal, "#,##0.00", locale)}
			            	</#if>
		            	</fo:block>
		            </fo:table-cell>
		    	</fo:table-row>
		    </fo:table-body>
		</fo:table>
	</fo:block>
</#if>
</fo:block>
</#escape>