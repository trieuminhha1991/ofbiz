<#assign currentStatusId = productQuotation.statusId?if_exists>
<div class="row-fluid">
	<div class="form-horizontal form-window-content-custom label-text-left content-description" style="margin:10px">
		<div class="row-fluid">
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSQuotationId}:</label>
					</div>
					<div class="div-inline-block">
						<span><i>${productQuotation.productQuotationId?if_exists}</i></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSQuotationName}:</label>
					</div>
					<div class="div-inline-block">
						<span>${productQuotation.quotationName?if_exists}</span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block" style="width:75px; vertical-align: top">
						<label style="line-height: 20px;">${uiLabelMap.BSStatus}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<#if currentStatusId?exists && currentStatusId?has_content>
								<#assign currentStatus = delegator.findOne("StatusItem", {"statusId" : currentStatusId}, true)>
								<#if currentStatus.statusCode?has_content>${currentStatus.get("description",locale)}</#if>
			                </#if>
						</span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSDescription}:</label>
					</div>
					<div class="div-inline-block">
						<span>${productQuotation.description?if_exists}</span>
					</div>
				</div>
				<#--
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSSalesChannel}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<#if productQuotation.salesMethodChannelEnumId?exists>
								<#assign salesMethodChannel = delegator.findOne("Enumeration", {"enumId" : productQuotation.salesMethodChannelEnumId}, false)/>
								<#if salesMethodChannel?exists>
									${salesMethodChannel.get("description", locale)}
								</#if>
							</#if>
						</span>
					</div>
				</div>
				-->
			</div><!--.span6-->
			<div class="span6">
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSCurrencyUomId}:</label>
					</div>
					<div class="div-inline-block">
						<span>${productQuotation.currencyUomId?if_exists}</span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSFromDate}:</label>
					</div>
					<div class="div-inline-block">
						<span><#if productQuotation.fromDate?exists>${productQuotation.fromDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></span>
					</div>
				</div>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSThruDate}:</label>
					</div>
					<div class="div-inline-block">
						<span><#if productQuotation.thruDate?exists>${productQuotation.thruDate?string("dd/MM/yyyy - HH:mm:ss")}<#else>___</#if></span>
					</div>
				</div>
				<#--<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSPartyApply}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
								<#list roleTypesSelected as roleTypeSelected>
									<li style="margin-bottom: 0; margin-top:0">
										<i class="icon-user green"></i>
										<#if roleTypeSelected.description?exists>${roleTypeSelected.description}<#else>${roleTypeSelected.roleTypeId}</#if>
									</li>
								</#list>
							</ul>
						</span>
					</div>
				</div>
				<#if listPartyIdApply?exists && listPartyIdApply?size &gt; 0>
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSPartyIdApply}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
								<#list listPartyIdApply as partyIdSelected>
									<li style="margin-bottom: 0; margin-top:0">
										<i class="icon-user green"></i>
										${partyIdSelected}
									</li>
								</#list>
							</ul>
						</span>
					</div>
				</div>
				</#if>-->
				<div class="row-fluid">
					<div class="div-inline-block">
						<label>${uiLabelMap.BSSalesChannel}:</label>
					</div>
					<div class="div-inline-block">
						<span>
							<#if productStoreAppls?exists>
								<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
									<#list productStoreAppls as itemProductStoreAppl>
										<#assign productStore = itemProductStoreAppl.getRelatedOne("ProductStore", true)/>
										<li style="margin-bottom: 0; margin-top:0">
											<#--<i class="icon-angle-right green"></i>-->
											<#if productStore.storeName?exists>${productStore.storeName}<#else>${productStore.productStoreId}</#if>
										</li>
										<#if itemProductStoreAppl_index &gt; 1 && productStoreAppls?size &gt; 2>
											<a href="javascript:void(0)" id="showProductStoreViewMore">${uiLabelMap.BSViewMore} (${productStoreAppls?size - 3})</a>
											<#break/>
										</#if>
									</#list>
								</ul>
								<div style="display:none" id="productStoreViewMore">
									<ul class="unstyled spaced2" style="margin: 0 0 0 15px;">
										<#list productStoreAppls as itemProductStoreAppl>
											<#assign productStore = itemProductStoreAppl.getRelatedOne("ProductStore", true)/>
											<li style="margin-bottom: 0; margin-top:0">
												<i class="icon-angle-right green"></i>
												<#if productStore.storeName?exists>${productStore.storeName}<#else>${productStore.productStoreId}</#if>
											</li>
										</#list>
									</ul>
								</div>
							</#if>
						</span>
					</div>
				</div>
			</div><!--.span6-->
		</div><!--.row-fluid-->
	</div><!--.form-horizontal-->
</div>
<div class="row-fluid">
	<div class="span12">
		<div style="text-align:right">
			<h5 class="lighter block green" style="float:left"><b>${uiLabelMap.BSListProduct}</b></h5>
		</div>
		<div style="clear:both"></div>
		
		<div>
			<#--
			<#assign columnlist="{ text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '180px', editable:false},
								 { text: '${uiLabelMap.DAProductName}', dataField: 'productName', editable:false}," />
			<#if productQuotation.salesChannel == "SALES_MT_CHANNEL">
				<#assign columnlist = columnlist + "{ text: '${uiLabelMap.DAPriceToCustomer}<br />${uiLabelMap.DAParenthesisBeforeVAT}', dataField: 'priceToDist', width: '180px', cellsalign: 'right', filterable:false, sortable:false},
								 { text: '${uiLabelMap.DAPriceToConsumer}<br/>${uiLabelMap.DAParenthesisAfterVAT}', dataField: 'priceToConsumer', width: '180px', cellsalign: 'right', filterable:false, sortable:false}"/>
			<#else>
				<#assign columnlist = columnlist + "{ text: '${uiLabelMap.DAPriceToDistributor}<br />${uiLabelMap.DAParenthesisBeforeVAT}', dataField: 'priceToDist', width: '180px', cellsalign: 'right', filterable:false, sortable:false},
								 { text: '${uiLabelMap.DATheMarketPriceOfDistributor}<br/>${uiLabelMap.DAParenthesisAfterVAT}', dataField: 'priceToMarket', width: '180px', cellsalign: 'right', filterable:false, sortable:false},
								 { text: '${uiLabelMap.DAPricesProposalForConsumer}<br/>${uiLabelMap.DAParenthesisAfterVAT}', dataField: 'priceToConsumer', width: '180px', cellsalign: 'right', filterable:false, sortable:false}"/>
			</#if> 
			
			-->
			<#assign dataField = "[
						{name: 'productId', type: 'string'},
						{name: 'productCode', type: 'string'},
	               		{name: 'productName', type: 'string'},
	               		{name: 'quantityUomId', type: 'string'},
	               		{name: 'taxPercentage', type: 'number'}, 
	               		{name: 'listPrice', type: 'number', formatter: 'float'},
                	]"/>
			<#assign columnlist = "
						{text: '${uiLabelMap.BSProductId}', dataField: 'productCode', width: '16%'},
						{text: '${uiLabelMap.BSProductName}', dataField: 'productName'},
						{text: '${uiLabelMap.BSPacking}', dataField: 'quantityUomId', width: '8%',   
						 	cellsrenderer: function(row, column, value){
	    						for (var i = 0 ; i < uomData.length; i++){
	    							if (value == uomData[i].uomId){
	    								return '<span title = ' + uomData[i].description +'>' + uomData[i].description + '</span>';
	    							}
	    						}
	    						return '<span title=' + value +'>' + value + '</span>';
							}
						},
						{text: '${uiLabelMap.BSTax}', dataField: 'taxPercentage', width: '7%', cellsalign: 'right', cellsformat: 'p'},
						{text: '${uiLabelMap.BSBeforeVAT}', dataField: 'listPrice', width: '14%', cellsalign: 'right', cellsformat: 'c2', columnGroup: 'PriceToCustomerColGroup', 
						 	cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgridQuotationItems').jqxGrid('getrowdata', row);
						 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
						 		if (value != '') {
					   				returnVal += formatcurrency(value, $('currencyUomId').val()) + '</div>';
					   				return returnVal;
					   			}
					   			returnVal += value + '</div>';
				   				return returnVal;
						 	}
						},
						{text: '${uiLabelMap.BSAfterVAT}', dataField: 'listPriceAfterVAT', width: '14%', cellsalign: 'right', cellsformat: 'c2', columnGroup: 'PriceToCustomerColGroup', 
						 	cellsrenderer: function(row, column, value){
						 		var data = $('#jqxgridQuotationItems').jqxGrid('getrowdata', row);
						 		var returnVal = '<div class=\"innerGridCellContent align-right\">';
						 		if (typeof(data) != 'undefined') {
						 			var listPrice = data.listPrice;
						 			var taxPercentage = data.taxPercentage;
							 		if (typeof(listPrice) != 'undefined' && typeof(taxPercentage) != 'undefined') {
							 			var valueVAT = listPrice + listPrice*taxPercentage/100;
						   				returnVal += formatcurrency(valueVAT, $('currencyUomId').val()) + '</div>';
						   				return returnVal;
						   			}
						 		}
					   			returnVal += value + '</div>';
				   				return returnVal;
						 	}
						}
					"/>
			<#assign columngrouplist = "{text: '${uiLabelMap.BSPriceToDistributor}', align: 'center', name: 'PriceToDistributorColGroup'},
										{text: '${uiLabelMap.BSPriceToCustomer}', align: 'center', name: 'PriceToCustomerColGroup'}" />
			<@jqGrid id="jqxgridQuotationItems" clearfilteringbutton="false" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
					viewSize="30" showtoolbar="false" editmode="click" selectionmode="checkbox" columngrouplist=columngrouplist 
					url="jqxGeneralServicer?sname=JQGetListProductQuotationRules&productQuotationId=${productQuotation.productQuotationId?if_exists}&pagesize=0" virtualmode="false"/>
		</div>
	</div>
</div>