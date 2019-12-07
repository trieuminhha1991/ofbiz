<h5 class="lighter block green">${uiLabelMap.DAListSortByFacility}</h5>
<#-- Start show items in cart -->
<#if (deliveryCartSize > 0)>
	<form method="post" action="<@ofbizUrl>modifyCart</@ofbizUrl>" name="" style="margin: 0;">
		<input type="hidden" name="removeSelected" value="false"/>
		<table cellspacing="0" cellpadding="1" border="0" class="table table-striped dataTable table-hover table-bordered">
			<thead>
				<tr><#--align="center" nowrap="nowrap"-->
					<th rowspan="2">${uiLabelMap.DANo}</th>
					<th align="center" rowspan="2">${uiLabelMap.DAInventory}</th>
					<th align="center" rowspan="2">${uiLabelMap.DAOrderId}</th>
					<th align="center" colspan="3">${uiLabelMap.DAProduct}</th>
					<th align="center" rowspan="2">${uiLabelMap.DAUnit}</th>
					<th align="center" rowspan="2">${uiLabelMap.DAQuantity}</th>
					<th align="center" rowspan="2">${uiLabelMap.DAPackingPerTray}</th>
					<th align="center" rowspan="2">${uiLabelMap.DASumTray}</th>
					<th align="center" rowspan="2">${uiLabelMap.DASumWeightPerPacking} (${uiLabelMap.DAUomKg})</th>
					<th align="center" rowspan="2">${uiLabelMap.DASumWeight} (${uiLabelMap.DAUomKg})</th>
					<th align="center" rowspan="2">${uiLabelMap.DASum} (${uiLabelMap.DAUomKg})</th>
				</tr>
				<tr>
					<td>${uiLabelMap.DABarcode}</td>
					<td>${uiLabelMap.DAProductName}</td>
					<td>${uiLabelMap.DAExpireDate}</td>
				</tr>
			</thead>

			<tbody>
				<#assign cartLineIndex = 0>
				<#list listItemByFacility as itemFacility>
					<#assign size = itemFacility.listValue?size>
					<#assign itemIsFirst = true>
					<#assign sumWeightAll = 0>
					<#assign sameColor = true>
					<#list itemFacility.listValue as cartLine>
						<#assign cartLineIndex = cartLineIndex + 1>
		          		<tr valign="top">
		          			<td>${cartLineIndex}</td>
        					<#if itemIsFirst>
        						<td rowspan="${size?if_exists}">${itemFacility.facilityId?if_exists}</td>
    						</#if>
        					<td>${cartLine.orderId?if_exists}</td>
        					<td>${cartLine.barcode?if_exists}</td>
        					<td nowrap>${cartLine.productId?if_exists}: ${cartLine.itemDescription?if_exists}</td>
        					<td>
        						<#if cartLine.expireDate?exists>
        							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(cartLine.expireDate, "dd/MM/yyyy", locale, timeZone)!}
        						</#if>
        					</td>
        					<td>${cartLine.productPackingUomDescription?if_exists}</td>
        					<td>${cartLine.quantity?if_exists}</td>
        					<td>${cartLine.quantityProductPackingPerTray?if_exists}</td>
        					<td>
        						<#if cartLine.quantity?exists && cartLine.quantityProductPackingPerTray?exists>
	        						<#assign packingPerTray = cartLine.quantity / cartLine.quantityProductPackingPerTray>
	        						${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(packingPerTray, "#0.00", locale)}
        						</#if>
        					</td>
        					<td>
        						<#assign sumWeightValueConverted = 0 />
        						<#if cartLine.weight?exists>
		        					<#if cartLine.productId?exists && cartLine.quantityProductPacking?exists && cartLine.weightUomId?exists && cartLine.weightUomId != "WT_kg" && cartLine.weight?exists>
		        						<#assign weightValue = cartLine.weight * cartLine.quantityProductPacking>
										<#assign sumWeightConverted = dispatcher.runSync("convertUom", Static["org.ofbiz.base.util.UtilMisc"].toMap("uomId", cartLine.weightUomId, "uomIdTo", "WT_kg", "originalValue", weightValue))>
										<#assign sumWeightValueConverted = sumWeightConverted.get("convertedValue") />
									<#elseif cartLine.quantityProductPacking?exists>
										<#assign sumWeightValueConverted = cartLine.weight * cartLine.quantityProductPacking/>
									</#if>
									${sumWeightValueConverted}
	        					</#if>
							</td>
        					<td>
								<#if sumWeightValueConverted?exists>
									<#assign sumWeightRow = cartLine.quantity * sumWeightValueConverted>
									<#assign sumWeightAll = sumWeightAll + sumWeightRow>
									${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(sumWeightRow, "#0.00", locale)}
								</#if>
							</td>
    						<td class="align-right" style="background-color:#f5f5f5; <#if sameColor && !itemIsFirst>border-top:0</#if>">
    							<#if !cartLine_has_next>
    								<#assign sameColor = false>
    								${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(sumWeightAll, "#0.00", locale)}
    							</#if>
							</td>
          				</tr>
          				<#assign itemIsFirst = false>
        			</#list>
				</#list>

			</tbody>

	        <#--
	        <tfoot>
	        	<tr>
		          	<td colspan="6" align="right" valign="bottom">
		            	<div><b>${uiLabelMap.OrderCartTotal}:</b></div>
		          	</td>
		          	<td align="right" valign="bottom" colspan="2">
		            	<div><b></b></div>
		          	</td>
		        </tr>
	        </tfoot>
	        -->
	  	</table>
	</form>
<#else>
	<div class="alert alert-info open-sans">${uiLabelMap.DANoOrderInProposal}</div>
</#if>
<#-- End show items in cart -->
