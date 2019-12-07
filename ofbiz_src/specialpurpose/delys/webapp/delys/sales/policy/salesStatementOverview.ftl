<#if security.hasPermission("SALES_POLICY_ADMIN", session)>
	<#assign hasApproved = true>
<#else>
	<#assign hasApproved = false>
</#if>
<#if salesStatementSelected?exists>
	<#assign currentStatusId = salesStatementSelected.statusId?if_exists>
	<div class="row-fluid">
		<div class="form-horizontal basic-custom-form form-decrease-padding" id="updateQuotation" name="updateQuotation" style="display: block;">
			<div class="row margin_left_10 row-desc">
				<div class="span6">
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DASalesStatementId}:</label>
						<div class="controls-desc">
							<b>${salesStatementSelected.salesId?if_exists}</b>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DASalesStatementName}:</label>
						<div class="controls-desc">
							${salesStatementSelected.salesName?if_exists}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DAStatus}:</label>
						<div class="controls-desc">
							<#if currentStatusId?exists && currentStatusId?has_content>
								<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
								<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
			                </#if>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DASalesStatementType}:</label>
						<div class="controls-desc">
							<#assign salesType = salesStatementSelected.getRelatedOne("SalesStatementType", false)!/>
							<#if salesType?exists>
								${salesType.description?if_exists}
							</#if>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DAFormFieldTitle_organizationPartyId}:</label>
						<div class="controls-desc">
							${salesStatementSelected.organizationPartyId?if_exists}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DAFormFieldTitle_internalPartyId}:</label>
						<div class="controls-desc">
							${salesStatementSelected.internalPartyId?if_exists}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DACurrencyUomId}:</label>
						<div class="controls-desc">
							${salesStatementSelected.currencyUomId?if_exists}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DAFormFieldTitle_salesForecastId}:</label>
						<div class="controls-desc">
							${salesStatementSelected.salesForecastId?if_exists}
						</div>
					</div>
					<#--
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.CommonDescription}:</label>
						<div class="controls-desc">
							${quotationSelected.description?if_exists}
						</div>
					</div>
					-->
				</div>
				<div class="span6">
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DAFromDate}:</label>
						<div class="controls-desc">
							<#if salesStatementSelected.fromDate?exists>${salesStatementSelected.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DAThroughDate}:</label>
						<div class="controls-desc">
							<#if salesStatementSelected.thruDate?exists>${salesStatementSelected.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DAParentSalesStatementId}:</label>
						<div class="controls-desc">
							${salesStatementSelected.parentSalesId?if_exists}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DAFormFieldTitle_quotaAmount}:</label>
						<div class="controls-desc">
							${salesStatementSelected.quotaAmount?if_exists}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DAFormFieldTitle_forecastAmount}:</label>
						<div class="controls-desc">
							${salesStatementSelected.forecastAmount?if_exists}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DAFormFieldTitle_bestCaseAmount}:</label>
						<div class="controls-desc">
							${salesStatementSelected.bestCaseAmount?if_exists}
						</div>
					</div>
					<div class="control-group">
						<label class="control-label-desc">${uiLabelMap.DAFormFieldTitle_closedAmount}:</label>
						<div class="controls-desc">
							${salesStatementSelected.closedAmount?if_exists}
						</div>
					</div>
					
				</div><!--.span6-->
			</div><!--.row-->
		</div>
		
		<div class="row-fluid">
			<div style="text-align:right">
				<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.DAProductListInQuotation}</b></h5>
				<#if hasApproved && (salesStatementSelected.statusId == "SALES_SM_CREATED")>
					<span class="widget-toolbar none-content">
						<a class="btn btn-primary btn-mini" href="javascript:document.StatementAccept.submit()" 
							style="font-size:13px; padding:0 8px">
							<i class="icon-ok open-sans">${uiLabelMap.DAApproveAccept}</i></a>
		              	<a class="btn btn-primary btn-mini" href="javascript:document.StatementCancel.submit()" 
		              		style="font-size:13px; padding:0 8px">
							<i class="icon-remove open-sans">${uiLabelMap.DAApproveCancel}</i></a>
						<form name="StatementAccept" method="post" action="<@ofbizUrl>changeSalesStatementStatus</@ofbizUrl>">
		                	<input type="hidden" name="statusId" value="SALES_SM_ACCEPTED">
			                <input type="hidden" name="salesId" value="${salesStatementSelected.salesId?if_exists}">
		              	</form>
						<form name="StatementCancel" method="post" action="<@ofbizUrl>changeSalesStatementStatus</@ofbizUrl>">
		                	<input type="hidden" name="statusId" value="SALES_SM_CANCELLED">
			                <input type="hidden" name="salesId" value="${salesStatementSelected.salesId?if_exists}">
			                <input type="hidden" name="changeReason" id="changeReason" value="" />
		              	</form>
					</span>
				</#if>
			</div>
		</div>
		<div class="row-fluid">
			<div id="list-product-price-rules">
				<#if salesStatementItems?exists && salesStatementItems?has_content>
					<table class="table table-striped table-bordered table-hover">
						<thead>
							<tr>
								<th rowspan="2">${uiLabelMap.DANo}</th>
								<th rowspan="2" class="center">${uiLabelMap.DASalesStatementItemId}</th>
								<th rowspan="2" class="center">${uiLabelMap.DAProduct}</th>
								<th rowspan="2" class="center">${uiLabelMap.DAProductCategory}</th>
								<th colspan="2" class="center">${uiLabelMap.DAQuantity}</th>
								<th colspan="2" class="center">${uiLabelMap.DAAmount}</th>
								<th rowspan="2" class="center">${uiLabelMap.DAFormFieldTitle_quantityUomId}</th>
							</tr>
							<tr>
								<th>${uiLabelMap.DATarget}</th>
								<th>${uiLabelMap.DAActual}</th>
								
								<th>${uiLabelMap.DATarget}</th>
								<th>${uiLabelMap.DAActual}</th>
							</tr>
						</thead>
						<tbody>
						<#list salesStatementItems as salesStatementItem>
							<tr>
								<td>${salesStatementItem_index + 1}</td>
								<td>${salesStatementItem.salesItemSeqId?if_exists}</td>
								<td>${salesStatementItem.productId?if_exists}</td>
								<td>${salesStatementItem.productCategoryId?if_exists}</td>
								<td>${salesStatementItem.quantity?if_exists}</td>
								<td>
									${salesStatementItem.quantityActual?if_exists}
									<#--<#assign resultValue =Static["com.olbius.olap.StaticSalesOrderItem"].getToltalQuantityAmountProductSaled(delegator,'${salesStatementSelected.internalPartyId?if_exists}', '${salesStatementSelected.organizationPartyId?if_exists}','${salesStatementSelected.fromDate?if_exists}','${salesStatementSelected.thruDate?if_exists}','${salesStatementItem.productId?if_exists}','${salesStatementSelected.salesTypeId?if_exists}') />
										${resultValue.quantity?if_exists}-->
								</td>
								<td>
									<#if salesStatementItem.amount?exists>
										<@ofbizCurrency amount=salesStatementItem.amount isoCode=salesStatementSelected.currencyUomId/>
									</#if>
								</td>
								<td>
									<#if salesStatementItem.amountActual?exists>
										<@ofbizCurrency amount=salesStatementItem.amountActual isoCode=salesStatementSelected.currencyUomId/>
									</#if>
									<#--<#if resultValue.amount?exists>
										<@ofbizCurrency amount=resultValue.amount isoCode=salesStatementSelected.currencyUomId/>
									</#if>-->
								</td>
								<td>
									<#assign quantityUom = salesStatementItem.getRelatedOne("Uom", false)!/>
									<#if quantityUom?exists>
										${quantityUom.description?if_exists}
									</#if>
								</td>
							</tr>
						</#list>
						</tbody>
					</table>
				<#else>
					<div class="alert alert-info">${uiLabelMap.DANoSalesStatementItemsToDisplay}</div>
				</#if>
			</div>
		</div><!--.row-fluid-->
	</div>
</#if>