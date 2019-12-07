<h5 class="lighter block green">${uiLabelMap.DAListSortByOrder}</h5>
<#-- Start show items in cart -->
<#if (deliveryCartSize > 0)>
	<form method="post" action="<@ofbizUrl>modifyDeliveryCart</@ofbizUrl>" name="cartForm" style="margin: 0;">
		<input type="hidden" name="removeSelected" value="false"/>
		<table cellspacing="0" cellpadding="1" border="0" class="table table-striped dataTable table-hover table-bordered">
			<thead>
				<tr><#--align="center" nowrap="nowrap"-->
					<th rowspan="2">${uiLabelMap.DANo}</th>
					<th align="center" class="align-center" rowspan="2">${uiLabelMap.DAOrderId}</th>
					<th align="center" class="align-center" rowspan="2">${uiLabelMap.DAOrderSeqId}</th>
					<th align="center" class="align-center" rowspan="2">${uiLabelMap.DAInventoryItemId}</th>
					<th align="center" class="align-center" colspan="3">${uiLabelMap.DAProduct}</th>
					<th align="center" class="align-center" rowspan="2">${uiLabelMap.DAUnit}</th>
					<th align="center" class="align-center" rowspan="2">${uiLabelMap.DAQuantity}</th>
					<th align="center" class="align-center" rowspan="2">${uiLabelMap.DAPackingPerTray}</th>
					<th align="center" class="align-center" rowspan="2">${uiLabelMap.DASumTray}</th>
					<th align="center" class="align-center" rowspan="2">${uiLabelMap.DASumWeightPerPacking} (${uiLabelMap.DAUomKg})</th>
					<th align="center" class="align-center" rowspan="2">${uiLabelMap.DASumWeight} (${uiLabelMap.DAUomKg})</th>
					<th align="center" class="align-center" rowspan="2">${uiLabelMap.DASum} (${uiLabelMap.DAUomKg})</th>
					
					<#--if not page view delivery requirement then display-->
					<#if !viewPageId?exists>
					<th align="center" class="align-center" rowspan="2">
			          	<label>
							<input type="checkbox" name="selectAll" value="0" onclick="javascript:toggleAll(this);" />
							<span class="lbl"></span>
						</label>
      				</th>
      				</#if>
				</tr>
				<tr>
					<td>${uiLabelMap.DABarcode}</td>
					<td nowrap>${uiLabelMap.DAProductName}</td>
					<td>${uiLabelMap.DAExpireDate}</td>
				</tr>
			</thead>

			<tbody>
				<#assign itemsFromList = false>
				<#assign sumWeightAll = 0>
				<#assign cartLineIndex = 0>
    			<#list listItemByOrder as itemOrder>
    				<#assign size = itemOrder.listValue?size>
					<#assign itemIsFirst = true>
					<#assign sumWeightAll = 0>
					<#assign sameColor = true>
    				<#list itemOrder.listValue as cartLine>
			          	<#assign cartLineIndex = cartLineIndex + 1>
		          		<tr valign="top">
		          			<td>${cartLineIndex}</td>
        					<#if itemIsFirst>
        						<td rowspan="${size?if_exists}">${cartLine.orderId?if_exists}</td>
    						</#if>
        					<td>${cartLine.orderItemSeqId?if_exists}</td>
        					<td>${cartLine.fromInventoryItemId?if_exists}</td>
        					<td>${cartLine.barcode?if_exists}</td>
        					<td nowrap><#--${cartLine.productId?if_exists}:--> ${cartLine.itemDescription?if_exists}</td>
        					<td>
        						<#if cartLine.expireDate?exists>
        							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(cartLine.expireDate, "dd/MM/yyyy", locale, timeZone)!}
        						</#if>
        					</td>
        					<td>
        						<#if cartLine.quantityUomId?exists>
        							${cartLine.quantityUomId?if_exists}
        						<#else>
        							${cartLine.productUomIdProd?if_exists}
        						</#if>
        					</td>
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
		        					<#if cartLine.productId?exists && cartLine.weightUomId?exists && cartLine.weightUomId != "WT_kg" && cartLine.weight?exists>
										<#assign sumWeightConverted = dispatcher.runSync("convertUom", Static["org.ofbiz.base.util.UtilMisc"].toMap("uomId", cartLine.weightUomId, "uomIdTo", "WT_kg", "originalValue", cartLine.weight))>
										<#assign sumWeightValueConverted = sumWeightConverted.get("convertedValue") />
									<#else>
										<#assign sumWeightValueConverted = cartLine.weight/>
									</#if>
									<#if cartLine.productUomIdProd?exists && cartLine.quantityUomId?exists && (cartLine.productUomIdProd != cartLine.quantityUomId)>
										<#assign quantityPackingConvertToDefault = 1/>
										<#if cartLine.productUomIdProd?exists && cartLine.productPackingUomId?exists>
											<#assign resultValue3 = dispatcher.runSync("getConvertPackingNumber", {"productId" : cartLine.productId, "uomFromId" : cartLine.productPackingUomId, "uomToId" : cartLine.productUomIdProd, "userLogin" : userLogin})>
											<#if (resultValue3?exists) && (resultValue3.convertNumber?exists)>
												<#assign quantityPackingConvertToDefault = resultValue3.convertNumber/>
											</#if>
										</#if>
										<#assign sumWeightValueConverted = sumWeightValueConverted.multiply(quantityPackingConvertToDefault)/>
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
        					<td>${Static["org.ofbiz.base.util.UtilFormatOut"].formatDecimalNumber(sumWeightAll, "#0.00", locale)}</td>
        					<#--if not page view delivery requirement then display-->
							<#if !viewPageId?exists>
	        					<#if itemIsFirst>
	        						<td rowspan="${size?if_exists}">
		        						<div>
											<input type="checkbox" name="selectedItem" value="${itemOrder_index}" onclick="javascript:checkToggle(this);"/>
											<span class="lbl"></span>
		            					</div>
		        					</td>
		        				</#if>
	        				</#if>
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