<div style="text-align: right">
	<span class="widget-toolbar none-content">
		<a title="" href="/delys/control/getPurchaseImportOrders?statusId=ORDER_APPROVED"><i class="fa-list"></i>${uiLabelMap.orderList}</a>
		<a title="" href="/delys/control/getPurchaseDeliverys?deliveryType=DELIVERY_PURCHASE&orderId=${parameters.orderId?if_exists}&countryGeoId=${parameters.countryGeoId?if_exists}"><i class="fa-file-text-o"></i>${uiLabelMap.Receipt}</a>
	</span>
	<hr style="color: #4383b4; margin-top: -3px;"/>
</div>
<h3 style="text-align:center;font-weight:bold; padding:0;line-height:20px">
	${uiLabelMap.PurchaseOrder}
</h3>
<div class="row-fluid">
	<div class="form-horizontal basic-custom-form form-size-mini form-decrease-padding">
		<div class="row margin_left_10 row-desc">
			<div class="span2">
			</div>
			<div class="span4">
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.OrderId}:</label>
					<div class="controls-desc">
						<span><b><i>${orderHeader.orderId?if_exists}</i></b></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.OrderName}:</label>
					<div class="controls-desc">
						<#if orderHeader.orderName?has_content>
							<span><b>${orderHeader.orderName?if_exists}</b></span>
						<#else>
							<span><b>_NA_</b></span>
						</#if>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.OrderDate}:</label>
					<div class="controls-desc">
						<span><b><#if orderHeader.orderDate?exists>
							${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(orderHeader.orderDate, "dd/MM/yyyy", locale, timeZone)!}
						</#if></b></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.Supplier}:</label>
					<div class="controls-desc">
						<span><b>
							<#assign partyByRole = delegator.findList("OrderRole", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", orderHeader.orderId, "roleTypeId", "BILL_FROM_VENDOR")), null, null, null, false) />
							<#if partyByRole?has_content>
								<#assign supplierId = partyByRole[0].partyId/>
								<#assign party = delegator.findOne("PartyNameView", {"partyId" : supplierId}, true)>
		            			${party.groupName?if_exists} ${party.firstName?if_exists} ${party.middleName?if_exists} ${party.lastName?if_exists}
			            	<#else>
			            		_NA_
			            	</#if>
						</b></span>
					</div>
				</div>
			</div>
			<div class="span6">
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.ReceiveDate}:</label>
					<div class="controls-desc">
						<span>
							<b>
								<#if listOrderItem[0].estimatedDeliveryDate?exists>
				            		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(listOrderItem[0].estimatedDeliveryDate, "dd/MM/yyyy", locale, timeZone)!}
				            	</#if>
							</b>
						</span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.FacilityToReceive}:</label>
					<div class="controls-desc"><b>
						<span><b>
							<#if orderHeader.originFacilityId?has_content>
			            		<#assign fac = delegator.findOne("Facility", {"facilityId" : orderHeader.originFacilityId}, true)>
			            		${fac.facilityName?if_exists}
			            	<#else>
			            		_NA_
			            	</#if>
					    </b></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.ReceiveStatus}:</label>
					<div class="controls-desc"><b>
						<span><b>
							<#if orderHeader.statusId == "ORDER_COMPLETED">
								${uiLabelMap.Received}
							<#else>
								${uiLabelMap.NotReceive}
							</#if>
					    </b></span>
					</div>
				</div>
				<div class="control-group">
					<label class="control-label-desc">${uiLabelMap.PurchaseOrderedBy}:</label>
					<div class="controls-desc"><b>
						<span><b>
							<#assign partyList = delegator.findList("PartyAndUserLogin", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("userLoginId", orderHeader.createdBy)), null, null, null, false) />
							<#if partyList?has_content>
								<#assign partyId = partyList[0].partyId/>
								<#assign party = delegator.findOne("PartyNameView", {"partyId" : partyId}, true)>
		            			${party.groupName?if_exists} ${party.firstName?if_exists} ${party.middleName?if_exists} ${party.lastName?if_exists}
			            	<#else>
			            		_NA_
			            	</#if>
					    </b></span>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>
	<h4 calss="widget-header" style="color: #4383b4">${uiLabelMap.DetailListProduct}</h4>
<div class="form-horizontal basic-custom-form" style="display: block;">
	<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered">
		<thead>
			<tr style="font-weight: bold;">
				<td rowspan="2">${uiLabelMap.DANo}</td>
				<td colspan="3" align="center" class="center">${uiLabelMap.Product}</td>
				<td rowspan="2" class="align-center">${uiLabelMap.DAQuantityUomId}</td>
				<td rowspan="2" align="center" class="center">${uiLabelMap.OrderQuantity}</td>
				<td rowspan="2" align="center" class="center">${uiLabelMap.QuantityActualReceipt}</td>
			  	<td rowspan="2" align="right" class="align-center">${uiLabelMap.unitPrice}</td>
			</tr>
			<tr style="font-weight: bold;">
				<td colspan="2" class="center">
					${uiLabelMap.ProductProductId} - ${uiLabelMap.ProductProductName}
				</td>
				<td align="center" class="center">
					${uiLabelMap.ExpireDate}
				</td>
			</tr>
		</thead>
		<tbody>
			<#list listOrderItem as item>
				<tr style="font-weight: initial;">
				 	<td style="text-align:left">
		            	<span>${item.orderItemSeqId?if_exists}</span>
		            </td>
		            <td style="text-align:left" colspan="2">
		            	${item.productId?if_exists} - ${item.productName?if_exists}
		            </td>
		            <td style="text-align:center">
			            <#if item.expireDate?exists>
		            		${Static["org.ofbiz.base.util.UtilFormatOut"].formatDateTime(item.expireDate, "dd/MM/yyyy", locale, timeZone)!}
		            	</#if>
		            </td>
		            <td style="text-align:left">
		            	<#if item.productUomId?has_content>
		            		<#assign uom = delegator.findOne("Uom", {"uomId" : item.productUomId}, true)>
		            		${uom.description?if_exists}
		            	<#else>
		            		_NA_
		            	</#if>
		            </td>
		            <td style="text-align:left">
		            	${item.quantity?string.number}
		            </td>
		            <td style="text-align:left">
		            	<#assign quantityReceived = Static["com.olbius.util.ProductUtil"].getQuantityReceied(delegator, item.orderId, item.orderItemSeqId)/>
		            	${quantityReceived?string.number}
		            </td>
		            <td style="text-align:left">
		            	<@ofbizCurrency amount=item.unitPrice isoCode=orderHeader.currencyUom/>
		            </td>
	            </tr>
			</#list>
		</tbody>
	</table>
</div>
<h4 calss="widget-header" style="color: #4383b4">${uiLabelMap.costList}</h4>
<div class="form-horizontal basic-custom-form" style="display: block;">
	<table cellspacing="0" cellpadding="1" border="0" class="table table-bordered">
		<thead>
			<tr style="font-weight: bold;">
				<td>${uiLabelMap.DANo}</td>
				<td class="align-center">${uiLabelMap.invoiceItemTypeId}</td>
				<td class="center">${uiLabelMap.costPriceTemporary}</td>
				<td class="center">${uiLabelMap.costPriceActual}</td>
			</tr>
		</thead>
		<tbody>
		</tbody>
	</table>
</div>