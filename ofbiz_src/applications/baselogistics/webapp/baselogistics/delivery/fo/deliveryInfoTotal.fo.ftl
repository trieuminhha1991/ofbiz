<fo:table margin-bottom="5px">
		<fo:table-column column-width="100pt"/>
		<fo:table-column column-width="300pt"/>
		<fo:table-column column-width="90pt"/>
		<fo:table-column column-width="100pt"/>
		<fo:table-column/>
		<fo:table-column/>
		<fo:table-body>
	 		<fo:table-cell>
	 			<fo:block>${uiLabelMap.ProductPromotion}:</fo:block>
			</fo:table-cell>
			<fo:table-cell>
				<fo:block>
					<#if discountAmountTotal?has_content && discountAmountTotal &gt; 0 > 
						${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(discountAmountTotal, "#,##0", locale)}
					</#if>
				</fo:block>
			</fo:table-cell>
			<#assign checkShipCharges = false>
			<#list orderHeaderAdjustments as x>
				<#if x.orderAdjustmentTypeId?has_content && x.orderAdjustmentTypeId== "SHIPPING_CHARGES">
					<#assign amount = Static["org.ofbiz.base.util.UtilFormatOut"].formatCurrency(x.amount.abs()?if_exists, orderHeader.currencyUom?if_exists?default('VND'), locale)>
					<#assign checkShipCharges = true>
				</#if>
			</#list>
			<#if checkShipCharges == true>
				<fo:table-cell>
					<fo:block>${uiLabelMap.TransportCost}:</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block font-weight="bold" text-align="left" font-size="12" margin-top="-2px">
						${amount?if_exists}
					</fo:block>
				</fo:table-cell>
			<#else>
				<fo:table-cell>
					<fo:block>
					</fo:block>
				</fo:table-cell>
				<fo:table-cell>
					<fo:block>
					</fo:block>
				</fo:table-cell>
			</#if>
		 </fo:table-body>
	</fo:table>
	
	<#-- <#assign labelPm = uiLabelMap.PaymentMethod>
	<fo:block margin-top="3px"><fo:inline font-weight="bold">${labelPm?upper_case}: </fo:inline> <fo:inline>${paymentMethodDescription?if_exists?upper_case}</fo:inline></fo:block>
	-->
	<fo:block padding="3pt">
		<fo:inline font-weight="bold" font-size="100%">${uiLabelMap.DeliveryUpperCase}: </fo:inline> 
	</fo:block>
	<fo:block padding="3pt">${uiLabelMap.ShippingAddress}: 
		<fo:inline font-style="italic">
			${customerAddress?if_exists}
		</fo:inline>
	</fo:block>
	<#if shipAfterDate?exists && shipBeforeDate?exists>
		<fo:block padding="3pt">${uiLabelMap.DatetimeDelivery}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipAfterDate?if_exists, "dd/MM/yyyy HH:mm", locale, timeZone)!} ${uiLabelMap.LogTo} ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(shipBeforeDate?if_exists, "dd/MM/yyyy HH:mm", locale, timeZone)!}</fo:block>
	<#else>
		<fo:block padding="3pt">${uiLabelMap.DatetimeDelivery}: ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate?if_exists, "dd/MM/yyyy HH:mm", locale, timeZone)!} ${uiLabelMap.LogTo} ${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(desiredDeliveryDate?if_exists, "dd/MM/yyyy HH:mm", locale, timeZone)!}</fo:block>
	</#if>
	<fo:block padding="3pt"><fo:inline> ${StringUtil.wrapString(uiLabelMap.Notes)}: </fo:inline><fo:inline font-weight="bold" font-size="12"> ${shippingInstructions?if_exists}</fo:inline></fo:block>