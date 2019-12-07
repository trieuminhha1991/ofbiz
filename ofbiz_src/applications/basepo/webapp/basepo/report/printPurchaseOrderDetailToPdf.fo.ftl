<#escape x as x?xml>
<fo:block>
<#if orderHeader?exists>
	<fo:block text-align="center" margin-bottom="0.2cm">
		<fo:block text-transform="uppercase" font-size="18pt" font-weight="700">
			${uiLabelMap.POOrderFormTitle}
		</fo:block>
	</fo:block>
	
	<fo:block>
		<fo:table border-color="black" border-style="solid" border-width="0">
			<fo:table-column column-width="9.5cm"/>
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell padding="1mm">
						<fo:block color="red">${(our.groupName)?if_exists}</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="1mm">
						<fo:block>
							${uiLabelMap.DAOrderId}: <fo:inline font-size="9pt">${(orderHeader.orderId)?if_exists}</fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell padding="1mm">
						<fo:block >${uiLabelMap.Address}: <fo:inline font-size="9pt">${(our.companyAddress)?if_exists}</fo:inline></fo:block>
					</fo:table-cell>
					<fo:table-cell padding="1mm">
						<fo:block>${uiLabelMap.CreatedDate}:
							<fo:inline font-size="9pt">
								<#if orderHeader.orderDate?exists>
									${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
								</#if>
							</fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell padding="1mm">
						<fo:block >${uiLabelMap.BLTaxId}: <fo:inline font-size="9pt">${(our.taxIdCompany)?if_exists}</fo:inline></fo:block>
					</fo:table-cell>
					<fo:table-cell padding="1mm">
						<fo:block>
							<fo:block color="red">${uiLabelMap.BSShipAfterDate}:
								<fo:inline font-size="9pt" color="black">
									<#if shipAfterDate?exists>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipAfterDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
									</#if>
								</fo:inline>
							</fo:block>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell padding="1mm">
						<fo:block >
							${uiLabelMap.BSPhone}: <fo:inline font-size="9pt">${(our.contactNumber)?if_exists}</fo:inline>
							<fo:inline>${uiLabelMap.Fax}:</fo:inline>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="1mm">
						<fo:block>
							<fo:block color="red">${uiLabelMap.BSShipBeforeDate}:
								<fo:inline font-size="9pt" color="black">
									<#if shipBeforeDate?exists>
										${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipBeforeDate, "dd/MM/yyyy HH:mm:ss", locale, timeZone)!}
									</#if>
								</fo:inline>
							</fo:block>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</fo:block>
	
	<fo:block margin-top="10px">
		<fo:table border-color="black" border-style="solid" border-width="0">
			<fo:table-column column-width="9.5cm"/>
			<fo:table-column/>
			<fo:table-body>
				<fo:table-row>
					<fo:table-cell padding="1mm">
						<fo:block color="#000033" font-weight="bold">${uiLabelMap.PODeliveryLocation}</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="1mm">
						<fo:block color="#000033" font-weight="bold">${uiLabelMap.Supplier}</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell padding="1mm">
						<fo:block>
							${uiLabelMap.Facility}: <fo:inline font-size="9pt">${(our.facilityName)?if_exists}</fo:inline>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="1mm">
						<fo:block>
							${uiLabelMap.RoleSeller}: <fo:inline font-size="9pt">${customerFullName?if_exists}</fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell padding="1mm">
						<fo:block>
							${uiLabelMap.FacilityId}: <fo:inline font-size="9pt">
							<#if our.facilityCode?exists>
								${(our.facilityCode)?if_exists}
							<#else>
								${(our.facilityId)?if_exists}
							</#if>
							</fo:inline>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="1mm">
						<fo:block>
							${uiLabelMap.SupplierId}: <fo:inline font-size="9pt">${customerIdOrCode?if_exists}</fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell padding="1mm">
						<fo:block>
							${uiLabelMap.Address}: <fo:inline font-size="9pt">${(our.facilityAddress)?if_exists}</fo:inline>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="1mm">
						<fo:block>
							${uiLabelMap.Address}: <fo:inline font-size="9pt">${(our.supplierAddress)?if_exists}</fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
				<fo:table-row>
					<fo:table-cell padding="1mm">
						<fo:block>
							${uiLabelMap.BPContactInfo}: <fo:inline font-size="9pt">${(our.facilityContactNumber)?if_exists}</fo:inline>
						</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="1mm">
						<fo:block>
							${uiLabelMap.BPContactInfo}: <fo:inline font-size="9pt">${(our.supplierContactNumber)?if_exists}</fo:inline>
						</fo:block>
					</fo:table-cell>
				</fo:table-row>
			</fo:table-body>
		</fo:table>
	</fo:block>

	<fo:block>
		<fo:block text-align="right" font-style="italic" font-size="10pt">${uiLabelMap.BSCalculateUomId}: ${currencyUomId?if_exists}. ${uiLabelMap.POUnitPriceBeforeVAT}</fo:block>
		<fo:table border-color="black" border-style="solid" border-width="1pt">
			<fo:table-column column-width="0.9cm"/>
			<fo:table-column column-width="2.1cm"/>
			<fo:table-column column-width="4.5cm"/>
			<fo:table-column column-width="1.1cm"/>
			<fo:table-column column-width="1.3cm"/>
			<fo:table-column column-width="1.7cm"/>
			<fo:table-column column-width="2.4cm"/>
			<fo:table-column column-width="2.05cm"/>
			<fo:table-column/>
			<fo:table-header>
				<fo:table-row border-color="black" background-color="#DDDDDD">
					<fo:table-cell padding="2mm 1mm" text-align="left" display-align="center" border="1pt solid black">
						<fo:block font-weight="bold">${uiLabelMap.BSSTT}</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="2mm 1mm" text-align="left" display-align="center" border="1pt solid black">
						<fo:block font-weight="bold">${uiLabelMap.ProductCodeSum}</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="2mm 1mm" text-align="left" display-align="center" border="1pt solid black">
						<fo:block font-weight="bold">${uiLabelMap.BSProductName}</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="2mm 1mm" text-align="left" display-align="center" border="1pt solid black">
						<fo:block font-weight="bold">${uiLabelMap.BLPackingForm}</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="2mm 1mm" text-align="left" display-align="center" border="1pt solid black">
						<fo:block font-weight="bold">${uiLabelMap.BSPurchaseUomId}</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="2mm 1mm" text-align="left" display-align="center" border="1pt solid black">
						<fo:block font-weight="bold">${uiLabelMap.BSQuantity}</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="2mm 1mm" text-align="left" display-align="center" border="1pt solid black">
						<fo:block font-weight="bold">${uiLabelMap.UnitPrice}</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="2mm 1mm" text-align="left" display-align="center" border="1pt solid black">
						<fo:block font-weight="bold">${uiLabelMap.BSAdjustment}</fo:block>
					</fo:table-cell>
					<fo:table-cell padding="2mm 1mm" text-align="left" display-align="center" border="1pt solid black">
						<fo:block font-weight="bold">${uiLabelMap.BSItemTotal}</fo:block>
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
							<!-- edit by hoanm
									<#if itemLine.supplierProductId?has_content>
										${itemLine.supplierProductId}
									<#elseif productId?exists>
										${itemLine.productCode?default(productId)}
									<#elseif itemLine.orderItemType?exists>
										${itemLine.orderItemType.description}
									</#if> -->
									
									<#if productId?exists>
										${itemLine.productCode?default(productId)}
									<#elseif itemLine.orderItemType?exists>
										${itemLine.orderItemType.description}
									</#if>
									
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding="1mm" text-align="left" border="1pt solid black">
								<fo:block>
									<#if productId?exists>
										${itemLine.itemDescription?default(itemLine.productName)}
									</#if>
									<#if itemLine.comments?exists>
										- <fo:inline>(${itemLine.comments})</fo:inline>
									</#if>
									<#if "PRODPROMO_ORDER_ITEM" == itemLine.orderItemTypeId>
										- (<fo:inline font-weight="bold">${uiLabelMap.BSProductReturnPromo}</fo:inline>)
									</#if>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
								<fo:block>
									<#if itemLine.packing?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.packing, "#,##0", locale)}</#if>
								</fo:block>
							</fo:table-cell>
							
							<fo:table-cell padding="1mm" text-align="center" border="1pt solid black">
				                <fo:block>
				                	${itemLine.quantityUomDescription?if_exists}
				            	</fo:block>
				            </fo:table-cell>
							
							<fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
								<fo:block>
									<#if itemLine.requireAmount?has_content && itemLine.requireAmount == 'Y'>
										<#if itemLine.selectedAmount?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.selectedAmount, "#,##0.00", locale)}</#if>
									<#else>
										<#if itemLine.quantity?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.quantity, "#,##0", locale)}</#if>
									</#if>
								</fo:block>
							</fo:table-cell>
							<fo:table-cell padding="1mm" text-align="right" border="1pt solid black">
								<fo:block><#-- Unit price -->
									<#if itemLine.requireAmount?has_content && itemLine.requireAmount == 'Y'>
										<#if itemLine.unitPriceBeVAT?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.unitPriceBeVAT/itemLine.selectedAmount, "#,##0.00", locale)}</#if>									
									<#else>
										<#if itemLine.unitPriceBeVAT?exists>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(itemLine.unitPriceBeVAT, "#,##0.00", locale)}</#if>
									</#if>
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
	
	<fo:block margin-top="10px">
	<fo:table border-color="black" border-style="solid" border-width="0">
		<fo:table-column column-width="2cm"/>
		<fo:table-column/>
		<fo:table-body>
			<fo:table-row>
				<fo:table-cell padding="1mm">
					<fo:block color="#000033" font-weight="bold">${uiLabelMap.Notes}</fo:block>
				</fo:table-cell>
				<fo:table-cell padding="1mm">
				</fo:table-cell>
			</fo:table-row>
		</fo:table-body>
	</fo:table>
	</fo:block>
	
</#if>
</fo:block>
</#escape>