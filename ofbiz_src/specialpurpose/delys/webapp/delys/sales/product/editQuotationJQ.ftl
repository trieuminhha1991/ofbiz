<style type="text/css">
	#currencyUomId, #currencyUomId_chzn {
		pointer-events:none;
		opacity: 0.5 !important;
	}
</style>
<#assign currentStatusId = quotationSelected.statusId?if_exists>
<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>
<script type="text/javascript">
	var uomData = new Array();
	<#list uomList as uomItem>
		<#assign description = StringUtil.wrapString(uomItem.get("description", locale)) />
		var row = {};
		row['uomId'] = '${uomItem.uomId}';
		row['description'] = "${description?default('')}";
		uomData[${uomItem_index}] = row;
	</#list>
</script>
<script type="text/javascript">
	var dataSelected = new Array();
</script>
<#if currentStatusId?exists && currentStatusId == "QUOTATION_CREATED">
<div class="row-fluid">
	<h4 id="step-title" class="header smaller blue span3" style="margin-bottom:0 !important; border-bottom: none; margin-top:3px !important; padding-bottom:0">
		${uiLabelMap.DAEditQuotation} (${quotationSelected.productQuotationId})</h4>
	<div id="fuelux-wizard" class="row-fluid hide span8" data-target="#step-container">
		<ul class="wizard-steps wizard-steps-mini">
			<li data-target="#step1" class="active">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.DAGeneralInformation}" data-placement="bottom">1</span>
			</li>
			<li data-target="#step2">
				<span class="step" data-rel="tooltip" title="${uiLabelMap.DAConfirmation}" data-placement="bottom">2</span>
			</li>
		</ul>
	</div>
	<div class="span1 align-right" style="padding-top:5px">
		<a href="<@ofbizUrl>viewQuotation?productQuotationId=${quotationSelected.productQuotationId}</@ofbizUrl>" data-rel="tooltip" title="${uiLabelMap.DAViewQuotation}" data-placement="bottom" class="no-decoration"><i class="fa fa-arrow-circle-left" style="font-size:16pt"></i></a>
        <a href="<@ofbizUrl>newQuotation</@ofbizUrl>" data-rel="tooltip" title="${uiLabelMap.DANewQuotation}" data-placement="bottom" class="no-decoration padding-left8"><i class="fa fa-plus-circle" style="font-size:16pt"></i></a>
	</div>
	<div style="clear:both"></div>
	<hr style="margin: 8px 0" />

	<div class="step-content row-fluid position-relative" id="step-container">
		<div class="step-pane active" id="step1">
			<form class="form-horizontal basic-custom-form form-size-mini" id="updateQuotation" name="updateQuotation" method="post" action="<@ofbizUrl>updateQuotation</@ofbizUrl>">
				<div class="row-fluid">
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for="productQuotationId">${uiLabelMap.DAQuotationId}</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="productQuotationId" id="productQuotationId" class="span12 input-small" maxlength="20" readonly="true" value="${quotationSelected.productQuotationId?if_exists}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label required" for="quotationName">${uiLabelMap.DAQuotationName}</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="quotationName" id="quotationName" class="span12" maxlength="100" value="${quotationSelected.quotationName?if_exists}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="description">${uiLabelMap.CommonDescription}</label>
							<div class="controls">
								<div class="span12">
									<textarea id="description" name="description" class="autosize-transition span12" style="resize: vertical">${quotationSelected.description?if_exists}</textarea>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label required" for="salesChannel">${uiLabelMap.DASalesChannel}</label>
							<div class="controls">
								<div class="span12">
									<select name="salesChannel" id="salesChannel" disabled="disabled" class="span12 input-less-small" style="opacity: 0.7 !important; background-color:#f5f5f5">
										<#list salesMethodChannels as salesMethodChannel>
											<option value="${salesMethodChannel.enumId}" <#if quotationSelected.salesChannel?exists && quotationSelected.salesChannel == salesMethodChannel.enumId>selected="selected"</#if>>${salesMethodChannel.get("description", locale)}</option>
										</#list>
						            </select>
						            <div class="control-group" style="display:inline-block">
						            	<#if quotationSelected.currencyUomId?exists>
											<#assign currencyUomId = quotationSelected.currencyUomId>
										</#if>
										<label class="control-label required" for="currencyUomId" style="width: 60px !important; color:#393939">${uiLabelMap.DAAbbCurrency}</label>
										<div class="controls" style="margin-left:80px !important">
											<div class="span12" style="text-align:right">
												<select name="currencyUomId" id="currencyUomId" class="chzn-select" data-placeholder="${uiLabelMap.DAChooseACurrency}..." style="width:80px">
									              	<option value=""></option>
									              	<#list currencies as currency>
										              	<option value="${currency.uomId}" <#if currencyUomId?default('VND') == currency.uomId>selected="selected"</#if> />${currency.uomId}
									              	</#list>
									            </select>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div><!--.span6-->
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for="percentagePriceToConsumer" title="${uiLabelMap.DAProportionPricesProposalForConsumer}" data-rel="tooltip" data-placement="bottom">${uiLabelMap.DAAbbProportionPricesProposalForConsumer}</label>
							<div class="controls">
								<div class="span12">
									<div id='percentagePriceToConsumer'></div>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label required" for="partyRoleTypesApply">${uiLabelMap.DAPartyApply}</label>
							<div class="controls">
								<div class="span12">
									<div>
										<select multiple="" disabled="disabled" class="chzn-select" name="partyRoleTypesApply" id="partyRoleTypesApply" data-placeholder="${uiLabelMap.DAChooseAObjectApply}...">
											<option value="" />
											<#list roleTypes as roleType>
												<#list roleTypesSelected as roleTypeSelected>
													<#if roleType.roleTypeId == roleTypeSelected.roleTypeId>
														<option value="${roleType.roleTypeId}" selected />${roleType.description}
													<#else>
														<option value="${roleType.roleTypeId}" />${roleType.description}
													</#if>
												</#list>
											</#list>
										</select>
										<span class="help-inline" title="${uiLabelMap.DARoleIdOfPartyGroupApplyPriceToCustomer}" data-rel="tooltip" data-placement="top" style="vertical-align: top"><i class="icon-info-sign" style="color:#4b89aa;font-size:15px;vertical-align:middle"></i></span>
									</div>
									<#if listRoleTypeMarket?exists && listRoleTypeMarket?size &gt; 0>
									<div>
										<div style="display:inline-block">
											<select multiple="" disabled="disabled" class="chzn-select" name="partyRoleTypesApplyMarket" id="partyRoleTypesApplyMarket" data-placeholder="${uiLabelMap.DAChooseAObjectApply}...">
												<option value="" />
												<#list roleTypes as roleType>
													<#list listRoleTypeMarket as roleTypeSelected>
														<#if roleType.roleTypeId == roleTypeSelected.roleTypeId>
															<option value="${roleType.roleTypeId}" selected />${roleType.description}
														<#else>
															<option value="${roleType.roleTypeId}" />${roleType.description}
														</#if>
													</#list>
												</#list>
											</select>
										</div>
										<span class="help-inline" title="${uiLabelMap.DAPartyApplyPriceToMarket}" data-rel="tooltip" data-placement="top" style="vertical-align: top"><i class="icon-info-sign" style="color:#4b89aa;font-size:15px;vertical-align:middle"></i></span>
									</div>
									</#if>
									<#if listPartyIdApply?exists && listPartyIdApply?size &gt; 0>
									<div>
										<div style="display:inline-block">
											<input type="text" id="partyIdsApply" readonly="readonly" name="partyIdsApply" value="<#list listPartyIdApply as partyIdSelected>${partyIdSelected}<#if partyIdSelected_has_next>, </#if></#list>" class="span12 disabled" style="width:220px"/>
										</div>
										<span class="help-inline" title="${uiLabelMap.DAPartyIdApplyPriceToCustomer}" data-rel="tooltip" data-placement="top" style="vertical-align: top"><i class="icon-info-sign" style="color:#4b89aa;font-size:15px;vertical-align:middle"></i></span>
									</div>
									</#if>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label required" for="fromDate">${uiLabelMap.DAFromDate}</label>
							<div class="controls">
								<div class="span12">
									<@htmlTemplate.renderDateTimeField name="fromDate" id="fromDate" value="${quotationSelected.fromDate?if_exists}" event="" action="" className="" alert="" 
										title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
										timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
										classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
										pmSelected="" compositeType="" formName=""/>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="thruDate">${uiLabelMap.DAThroughDate}</label>
							<div class="controls">
								<div class="span12">
									<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDate" value="${quotationSelected.thruDate?if_exists}" event="" action="" className="" alert="" 
										title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
										timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
										classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
										pmSelected="" compositeType="" formName=""/>
								</div>
							</div>
						</div>
					</div><!--.span6-->
				</div><!--.row-fluid-->
			</form>
			<div class="row-fluid">
				<div class="span12">
					<div id="jqxPanel" style="width:400px;">
						<input type="button" id="jqxButtonAddNewRow" value="${uiLabelMap.DAAddNewRow}"/>
						<input type="button" id="copyToMarket" title="${uiLabelMap.DACopyPriceDistToPriceMarket}" data-rel="tooltip" data-placement="top" value="${uiLabelMap.DACopy}"/>
						<!--<input type="button" value="${uiLabelMap.newInPayment}" id='jqxButton1' />-->
					</div>
					<#assign dataField="[{ name: 'productId', type: 'string' },
					               		{ name: 'productName', type: 'string' },
					               		{ name: 'productPackingUomId', type: 'string' },
					               		{ name: 'taxPercentage', type: 'number'}, 
					               		{ name: 'priceToDist', type: 'number', formatter: 'float'},
					               		{ name: 'priceToDistAfterVAT', type: 'number', formatter: 'float'},
					               		{ name: 'priceToMarket', type: 'number', formatter: 'float'},
					               		{ name: 'priceToConsumer', type: 'number', formatter: 'float'},
					                	]"/>
					<#assign columnlist="{ text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '16%', editable:false},
										 { text: '${uiLabelMap.DAProductName}', dataField: 'productName', editable:false},
										 { text: '${uiLabelMap.DATax}', dataField: 'taxPercentage', editable:false, width: '7%', cellsalign: 'right', cellsformat: 'p'},
										 { text: '${uiLabelMap.DAAbbPackingSpecification}', dataField: 'productPackingUomId', width: '7%', editable:false, filtertype: 'checkedlist', cellsalign: 'center',  
										 	cellsrenderer: function(row, column, value){
					    						for (var i = 0 ; i < uomData.length; i++){
					    							if (value == uomData[i].uomId){
					    								return '<span title = ' + uomData[i].description +'>' + uomData[i].description + '</span>';
					    							}
					    						}
					    						return '<span title=' + value +'>' + value + '</span>';
											},
											createfilterwidget: function (column, columnElement, widget) {
								   				var filterBoxAdapter2 = new $.jqx.dataAdapter(uomData, {autoBind: true});
								                var uniqueRecords2 = filterBoxAdapter2.records;
								   				uniqueRecords2.splice(0, 0, '(${StringUtil.wrapString(uiLabelMap.filterselectallstring)})');
								   				widget.jqxDropDownList({ source: uniqueRecords2, displayMember: 'description', valueMember : 'uomId', renderer: function (index, label, value) {
													for(i = 0; i < uomData.length; i++){
														if(uomData[i].uomId == value){
															return uomData[i].description;
														}
													}
												    return value;
												}});
												//widget.jqxDropDownList('checkAll');
								   			}
										 }," />
					<#if quotationSelected.salesChannel == "SALES_MT_CHANNEL">
						<#assign columnlist = columnlist + "{ text: '${uiLabelMap.DAPriceToCustomer}<br />${uiLabelMap.DAParenthesisBeforeVAT}', dataField: 'priceToDist', width: '12%', cellsalign: 'right', filterable:false, sortable:false},
										 { text: '${uiLabelMap.DAPriceToConsumer}<br/>${uiLabelMap.DAParenthesisAfterVAT}', dataField: 'priceToConsumer', width: '12%', cellsalign: 'right', filterable:false, sortable:false}"/>
					<#else>
						<#assign columnlist = columnlist + "{ text: '${uiLabelMap.DABeforeVAT}', dataField: 'priceToDist', width: '12%', cellsalign: 'right', filterable:false, sortable:false, cellsformat: 'c2', columnGroup: 'PriceToCustomerColGroup',
											cellsrenderer: function(row, column, value){
										 		var data = $('#jqxgridQuotationItems').jqxGrid('getrowdata', row);
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (data != undefined && data != null) {
										 			if (data.priceToDist != undefined) {
										 				if (value != \"\") {
										 					returnVal += formatcurrency(value, '${currencyUomId}');
										 				}
										 			}
										 		}
										 		returnVal += '</div>';
						   						return returnVal;
										 	}
										 }, 
										 { text: '${uiLabelMap.DAAfterVAT}', dataField: 'priceToDistAfterVAT', width: '12%', cellsalign: 'right', filterable:false, sortable:false, cellsformat: 'c2', columnGroup: 'PriceToCustomerColGroup', 
										 	cellsrenderer: function(row, column, value){
										 		var data = $('#jqxgridQuotationItems').jqxGrid('getrowdata', row);
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (data != undefined && data != null) {
										 			if (data.priceToDist != undefined) {
										 				var valueCal;
										 				if (data.taxPercentage != undefined) {
										 					valueCal = data.priceToDist + data.priceToDist * data.taxPercentage / 100;
										 				} else {
										 					valueCal = data.priceToDist;
										 				}
										 				if (valueCal != \"\") {
										 					data.priceToDistAfterVAT = valueCal;
										 					returnVal += formatcurrency(valueCal, '${currencyUomId}');
										 				}
										 			}
										 		}
										 		returnVal += '</div>';
						   						return returnVal;
										 	}
										 }, 
										 { text: '${uiLabelMap.DATheMarketPriceOfDistributor}<br/>${uiLabelMap.DAParenthesisAfterVAT}', dataField: 'priceToMarket', width: '12%', cellsalign: 'right', filterable:false, sortable:false, cellsformat: 'c2',
										 	cellsrenderer: function(row, column, value){
										 		var data = $('#jqxgridQuotationItems').jqxGrid('getrowdata', row);
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (data != undefined && data != null) {
										 			if (data.priceToDist != undefined) {
										 				if (value != \"\") {
										 					returnVal += formatcurrency(value, '${currencyUomId}');
										 				}
										 			}
										 		}
										 		returnVal += '</div>';
						   						return returnVal;
										 	}
										 },
										 { text: '${uiLabelMap.DAPricesProposalForConsumer}<br/>${uiLabelMap.DAParenthesisAfterVAT}', dataField: 'priceToConsumer', width: '12%', cellsalign: 'right', filterable:false, sortable:false, cellsformat: 'c2', 
										 	cellsrenderer: function(row, column, value){
										 		var data = $('#jqxgridQuotationItems').jqxGrid('getrowdata', row);
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (data != undefined && data != null) {
										 			if (data.priceToDist != undefined) {
										 				if (value != \"\") {
										 					returnVal += formatcurrency(value, '${currencyUomId}');
										 				}
										 			}
										 		}
										 		returnVal += '</div>';
						   						return returnVal;
										 	}
										 }"/>
					</#if>
					<#assign columngrouplist = "{ text: '${uiLabelMap.DAPriceToDistributor}', align: 'center', name: 'PriceToDistributorColGroup' },
												{ text: '${uiLabelMap.DAPriceToCustomer}', align: 'center', name: 'PriceToCustomerColGroup' }" />
					<@jqGrid id="jqxgridQuotationItems" clearfilteringbutton="true" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
							viewSize="30" showtoolbar="false" editmode="click" selectionmode="checkbox" columngrouplist=columngrouplist 
							url="jqxGeneralServicer?sname=JQGetListProductQuotationRulesAndTax&productQuotationId=${quotationSelected.productQuotationId?if_exists}"/>
				</div>
			</div>
		</div><!--.step1-->

		<div class="step-pane" id="step2">
			<div class="form-horizontal basic-custom-form form-size-mini form-decrease-padding">
				<div class="row margin_left_10 row-desc">
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAQuotationId}:</label>
							<div class="controls-desc">
								<span id="strProductQuotationId"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAQuotationName}:</label>
							<div class="controls-desc">
								<span id="strQuotationName"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.CommonDescription}:</label>
							<div class="controls-desc">
								<span id="strDescription"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DASalesChannel}:</label>
							<div class="controls-desc">
								<span id="strSalesChannel"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DACurrencyUomId}:</label>
							<div class="controls-desc">
								<span id="strCurrencyUomId"></span>
							</div>
						</div>
					</div><!--.span6-->
					<div class="span6">
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAPartyApply}:</label>
							<div class="controls-desc">
								<span id="strPartyRoleTypesApply"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAFromDate}:</label>
							<div class="controls-desc">
								<span id="strFromDate"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAThroughDate}:</label>
							<div class="controls-desc">
								<span id="strThruDate"></span>
							</div>
						</div>
						<#if listRoleTypeMarket?exists && listRoleTypeMarket?size &gt; 0>
		    			<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAAbbPartyApplyPriceToMarket}:</label>
							<div class="controls-desc">
								<span id="strPartyRoleTypesApplyPriceMarket"></span>
							</div>
						</div>
						</#if>
						<#if listPartyIdApply?exists && listPartyIdApply?size &gt; 0>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAPartyIdApplyPriceToCustomer}:</label>
							<div class="controls-desc">
								<span id="strPartyIdsApply"></span>
							</div>
						</div>
						</#if>
					</div><!--.span6-->
				</div><!--.row-fluid-->
				
				<div class="row-fluid">
					<div class="span12">
						<div id="jqxgridProdSelected" style="width: 100%">
						</div>
						<script type="text/javascript">
							$(document).ready(function () {
								var sourceSuccess = {
										localdata: dataSelected,
										dataType: "array",
										datafields:${dataField},
									};
								var dataAdapterSuccess = new jQuery.jqx.dataAdapter(sourceSuccess);
								jQuery("#jqxgridProdSelected").jqxGrid({
									width: '100%',
									source:dataAdapterSuccess,
									pageable: true,
							        autoheight: true,
							        sortable: false,
							        altrows: true,
							        showaggregates: false,
							        showstatusbar: false,
							        enabletooltips: true,
							        editable: false,
							        selectionmode: 'singlerow',
							        columns:[${columnlist}],
							        columngroups:[${columngrouplist}]
								});
							});
						</script>
					</div>
				</div>
			</div>
		</div><!--.step2-->
	</div>
	
	<hr />
	<div class="row-fluid wizard-actions">
		<button class="btn btn-small btn-prev">
			<i class="icon-arrow-left"></i>
			${uiLabelMap.DAPrev}
		</button>
		<button class="btn btn-small btn-success btn-next" data-last="${uiLabelMap.DAFinish}" id="btnNextWizard">
			${uiLabelMap.DANext}
			<i class="icon-arrow-right icon-on-right"></i>
		</button>
	</div>
</div>

<form id="alterpopupWindowform" name="alterpopupWindowform" class="form-horizontal form-table-block" method="post" action="<@ofbizUrl>updateQuotationRule</@ofbizUrl>">
	<div id="alterpopupWindow" style="display:none">
		<div>${uiLabelMap.accCreateNew}</div>
		<div class="form-horizontal form-table-block">
			<input type="hidden" id="productName" name="productName" value=""/>
	    	<div class="row-fluid">
	    		<div class="span12">
	    			<div class="control-group">
	    				<label class="control-label required" for="productId">${uiLabelMap.DAProduct}</label>
	    				<div class="controls">
    						<div id="jqxdropdownbuttonProduct">
					       	 	<div id="jqxgridProduct"></div>
					       	</div>
	    				</div>
	    			</div>
	    			<#if quotationSelected.salesChannel == "SALES_MT_CHANNEL">
		    			<div class="control-group">
		    				<label class="control-label required" for="priceToDist">${uiLabelMap.DAPriceToCustomer} ${uiLabelMap.DAParenthesisBeforeVAT}</label>
		    				<div class="controls">
	    						<input type="text" name="priceToDist" id="priceToDist" value="" class="span12">
		    				</div>
		    			</div>
		    			<div class="control-group">
		    				<label class="control-label" for="priceToConsumer">${uiLabelMap.DAPriceToConsumer} ${uiLabelMap.DAParenthesisAfterVAT}</label>
		    				<div class="controls">
    							<input type="text" name="priceToConsumer" id="priceToConsumer" class="span12"/>
		    				</div>
		    			</div>
	    			<#else>
		    			<div class="control-group">
		    				<label class="control-label required" for="priceToDist">${uiLabelMap.DAPriceToDistributor} ${uiLabelMap.DAParenthesisBeforeVAT}</label>
		    				<div class="controls">
	    						<input type="text" name="priceToDist" id="priceToDist" value="" class="span12">
		    				</div>
		    			</div>
		    			<div class="control-group">
		    				<label class="control-label" for="priceToMarket">${uiLabelMap.DATheMarketPriceOfDistributor} ${uiLabelMap.DAParenthesisAfterVAT}</label>
		    				<div class="controls">
	    						<input type="text" name="priceToMarket" id="priceToMarket" class="span12"/>
		    				</div>
		    			</div>
		    			<div class="control-group">
		    				<label class="control-label" for="priceToConsumer">${uiLabelMap.DAPricesProposalForConsumer} ${uiLabelMap.DAParenthesisAfterVAT}</label>
		    				<div class="controls">
	    						<input type="text" name="priceToConsumer" id="priceToConsumer" class="span12"/>
		    				</div>
		    			</div>
	    			</#if>
	    		</div>
	    	</div><!--.row-fluid-->
	    	<div class="row-fluid">
	    		<div class="span12">
	    			<div class="control-group">
	    				<label class="control-label"></label>
	    				<div class="controls">
    						<input type="button" id="alterSave4" value="${uiLabelMap.CommonSave}"/>
							<input type="button" id="alterCancel4" value="${uiLabelMap.CommonCancel}"/>
	    				</div>
	    			</div>
	    		</div>
	    	</div>
	    	<div class="control-group" style="margin:0 !important; font-size:9pt;height: 25px;">
				<span style="color:#666"><i>(${uiLabelMap.DAThisPriceApplyFor1Packing})</i></span>
			</div>
	    </div>
	</div>		
</form>	
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>

<script src="/delys/images/js/fuelux/fuelux.wizard.min.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>
<script src="/delys/images/js/select2.min.js"></script>
<script type="text/javascript">
	var alterData = null;
	var productIds = [];
	$(function() {
		$("#priceToDist").jqxInput({width: 200, height: 25});
		<#if quotationSelected.salesChannel != "SALES_MT_CHANNEL">$("#priceToMarket").jqxInput({width: 200, height: 25});</#if>
		$("#priceToConsumer").jqxInput({width: 200, height: 25});
		$("#copyToMarket").jqxButton({ width: '90', height: '24px', theme: theme});
		$("#percentagePriceToConsumer").jqxNumberInput({ width: '218px', digits: 3, symbolPosition: 'right', symbol: '%', spinButtons: true });
		
		$("#jqxgridQuotationItems").on("cellEndEdit", function (event) {
	    	var args = event.args;
	    	if (args.datafield == "priceToDist") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridQuotationItems").jqxGrid("getrowdata", rowBoundIndex);
		    	if (data && data.taxPercentage) {
		    		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (newValue == undefined || newValue == null || (/^\s*$/.test(newValue))) {
				   		var valueCal = "";
			    		$('#jqxgridQuotationItems').jqxGrid('setcellvalue', rowBoundIndex, 'priceToDistAfterVAT', valueCal);
		    			$('#jqxgridQuotationItems').jqxGrid('setcellvalue', rowBoundIndex, 'priceToConsumer', valueCal);
			   		} else {
			   			var taxPercentage = data.taxPercentage;
				   		var valueCal = newValue * (1 + taxPercentage / 100);
			    		$('#jqxgridQuotationItems').jqxGrid('setcellvalue', rowBoundIndex, 'priceToDistAfterVAT', valueCal);
			    		
			    		var percentagePriceToConsumer = $("#percentagePriceToConsumer").val();
		    			var percentagePriceToConsumerVal = valueCal * ((100 + percentagePriceToConsumer) / 100);
		    			$('#jqxgridQuotationItems').jqxGrid('setcellvalue', rowBoundIndex, 'priceToConsumer', percentagePriceToConsumerVal);
			   		}
		    	}
	    	} else if (args.datafield == "priceToDistAfterVAT") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridQuotationItems").jqxGrid("getrowdata", rowBoundIndex);
		    	if (data && data.taxPercentage) {
		    		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (newValue == undefined || newValue == null || (/^\s*$/.test(newValue))) {
			   			var valueCal = "";
			    		$('#jqxgridQuotationItems').jqxGrid('setcellvalue', rowBoundIndex, 'priceToDist', valueCal);
		    			$('#jqxgridQuotationItems').jqxGrid('setcellvalue', rowBoundIndex, 'priceToConsumer', valueCal);
			   		} else {
			   			var taxPercentage = data.taxPercentage;
				   		var valueCal = newValue * (100 / (100 + taxPercentage));
			    		$('#jqxgridQuotationItems').jqxGrid('setcellvalue', rowBoundIndex, 'priceToDist', valueCal);
			    		
			    		var percentagePriceToConsumer = $("#percentagePriceToConsumer").val();
		    			var percentagePriceToConsumerVal = newValue * ((100 + percentagePriceToConsumer) / 100);
		    			$('#jqxgridQuotationItems').jqxGrid('setcellvalue', rowBoundIndex, 'priceToConsumer', percentagePriceToConsumerVal);
			   		}
		    	}
	    	}
    	});
		
		$("#copyToMarket").on('click', function () {
	     	var datas = $("#jqxgridQuotationItems").jqxGrid("getrows");
	     	if (datas && datas.length > 0) {
	     		for (var i = 0; i < datas.length; i++) {
	     			var data = datas[i];
	     			if (data && data.priceToDistAfterVAT) {
	     				var priceToDistAfterVAT = data.priceToDistAfterVAT;
		     			$('#jqxgridQuotationItems').jqxGrid('setcellvalue', i, 'priceToMarket', priceToDistAfterVAT);
	     			}
	     		}
	     	}
	    });
	    
	    $('#percentagePriceToConsumer').on('change', function (event) {
	     	var value = event.args.value;
	     	var datas = $("#jqxgridQuotationItems").jqxGrid("getrows");
	     	var percentagePriceToConsumer = $("#percentagePriceToConsumer").val();
	     	if (datas && datas.length > 0) {
	     		for (var i = 0; i < datas.length; i++) {
	     			var data = datas[i];
	     			if (data && data.priceToDistAfterVAT) {
	     				var priceToDistAfterVAT = data.priceToDistAfterVAT;
		     			var percentagePriceToConsumerVal = priceToDistAfterVAT * ((100 + percentagePriceToConsumer) / 100);
		     			$('#jqxgridQuotationItems').jqxGrid('setcellvalue', i, 'priceToConsumer', percentagePriceToConsumerVal);
	     			}
	     		}
	     	}
	 	});
		
	// ==============================================================================================================
		$("#jqxButtonAddNewRow").jqxButton({ width: '150', theme: theme});
	    $("#alterCancel4").jqxButton({theme: theme});
	    $("#alterSave4").jqxButton({theme: theme});
	    
	    $("#alterpopupWindow").jqxWindow({width: 900, resizable: false, isModal: true, autoOpen: false, cancelButton: $("#alterCancel4"), modalOpacity: 0.7, theme:theme});
	    $("#jqxdropdownbuttonProduct").jqxDropDownButton({ theme: theme, width: 200, height: 25});
	    
	    // Product JQX Dropdown
	    var sourceP2 =
	    {
	        datafields:[{name: 'productId', type: 'string'},
	            		{name: 'productName', type: 'string'},
	            		{name: 'productTypeId', type: 'string'}
        			],
	        cache: false,
	        root: 'results',
	        datatype: "json",
	        updaterow: function (rowid, rowdata) {
	            // synchronize with the server - send update command   
	        },
	        beforeprocessing: function (data) {
	            sourceP2.totalrecords = data.TotalRows;
	        },
	        filter: function () {
	            // update the grid and send a request to the server.
	            $("#jqxgridProduct").jqxGrid('updatebounddata');
	        },
	        pager: function (pagenum, pagesize, oldpagenum) {
	            // callback called when a page or page size is changed.
	        },
	        sort: function () {
	            $("#jqxgridProduct").jqxGrid('updatebounddata');
	        },
	        sortcolumn: 'productId',
			sortdirection: 'asc',
	        type: 'POST',
	        data: {
		        noConditionFind: 'Y',
		        conditionsFind: 'N',
		        productIds: productIds,
		        productQuotationId: '${quotationSelected.productQuotationId?if_exists}'
		    },
		    pagesize:5,
	        contentType: 'application/x-www-form-urlencoded',
	        url: 'jqxGeneralServicer?sname=JQGetListProductSA',
	    };
	    var dataAdapterP2 = new $.jqx.dataAdapter(sourceP2,
	    {
	    	autoBind: true,
	    	formatData: function (data) {
		    	if (data.filterscount) {
	                var filterListFields = "";
	                for (var i = 0; i < data.filterscount; i++) {
	                    var filterValue = data["filtervalue" + i];
	                    var filterCondition = data["filtercondition" + i];
	                    var filterDataField = data["filterdatafield" + i];
	                    var filterOperator = data["filteroperator" + i];
	                    filterListFields += "|OLBIUS|" + filterDataField;
	                    filterListFields += "|SUIBLO|" + filterValue;
	                    filterListFields += "|SUIBLO|" + filterCondition;
	                    filterListFields += "|SUIBLO|" + filterOperator;
	                }
	                data.filterListFields = filterListFields;
	            }
	            return data;
	        },
	        loadError: function (xhr, status, error) {
	            alert(error);
	        },
	        downloadComplete: function (data, status, xhr) {
	                if (!sourceP2.totalRecords) {
	                    sourceP2.totalRecords = parseInt(data["odata.count"]);
	                }
	        }, 
	        beforeLoadComplete: function (records) {
	        	for (var i = 0; i < records.length; i++) {
	        		if(typeof(records[i])=="object"){
	        			for(var key in records[i]) {
	        				var value = records[i][key];
	        				if(value != null && typeof(value) == "object" && typeof(value) != null){
	        					//var date = new Date(records[i][key]["time"]);
	        					//records[i][key] = date;
	        				}
	        			}
	        		}
	        	}
	        }
	    });
	    $("#jqxdropdownbuttonProduct").jqxDropDownButton({ theme: theme, width: 200, height: 25});
	    $("#jqxgridProduct").jqxGrid({
	    	width:610,
	        source: dataAdapterP2,
	        filterable: true,
	        showfilterrow: true,
	        virtualmode: true, 
	        sortable:true,
	        theme: theme,
	        editable: false,
	        autoheight:true,
	        pageable: true,
	        rendergridrows: function(obj){
				return obj.data;
			},
	        columns: [{text: '${uiLabelMap.DAProductId}', datafield: 'productId', width:'180px'},
	          			{text: '${uiLabelMap.DAProductName}', datafield: 'productName', width:'250px'},
	          			{text: '${uiLabelMap.DAProductTypeId}', datafield: 'productTypeId', width:'180px'}
	        		]
	    });
	    
	    $("#jqxgridProduct").on('rowselect', function (event) {
	        var args = event.args;
	        var row = $("#jqxgridProduct").jqxGrid('getrowdata', args.rowindex);
	        var dropDownContent = '<div style=\"position: relative; margin-left: 3px; margin-top: 5px;\">'+ row['productId'] +'</div>';
	        $('#jqxdropdownbuttonProduct').jqxDropDownButton('setContent', dropDownContent);
	        $('#productName').val(row['productName']);
	    });
	    
	    $("#jqxButtonAddNewRow").on('click', function () {
			$('#alterpopupWindow').jqxWindow('open');
	    });
	    
	    // update the edited row when the user clicks the 'Save' button.
	    $("#alterSave4").click(function () {
	    	if($('#alterpopupWindowform').jqxValidator('validate')){
		    	var row;
		        row = { productId:$('#jqxdropdownbuttonProduct').val(),
		        		productName:$('#productName').val(),
		        		priceToDist:$('#priceToDist').val(),
		        		<#if quotationSelected.salesChannel != "SALES_MT_CHANNEL">priceToMarket:$('#priceToMarket').val(),</#if>
		        		priceToConsumer:$('#priceToConsumer').val()
		        	  };
			   	$("#jqxgridQuotationItems").jqxGrid('addRow', null, row, "first");
		        // select the first row and clear the selection.
		        $("#jqxgridQuotationItems").jqxGrid('clearSelection');                        
		        $("#jqxgridQuotationItems").jqxGrid('selectRow', 0);  
		        $("#alterpopupWindow").jqxWindow('close');
		        productIds[productIds.length] = row["productId"];
		        
		        // reset value on window
				$('#jqxdropdownbuttonProduct').val("");
				$('#productName').val("");
				$('#priceToDist').val("");
				<#if quotationSelected.salesChannel != "SALES_MT_CHANNEL">$('#priceToMarket').val("");</#if>
				$('#priceToConsumer').val("");
				$("#jqxgridProduct").jqxGrid('updatebounddata');
				$("#jqxgridProduct").jqxGrid('clearSelection');
	        }else{
	        	return;
	        }
	    });
	    $('#alterpopupWindowform').jqxValidator({
	        rules: [
	        	{input: '#priceToDist', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
        			function (input, commit) {
        				var value = $(input).val();
						if(value == null || /^\s*$/.test(value)){
							return false;
						}
						return true;
					}
				},
	           	{input: '#priceToDist', message: '${uiLabelMap.DANotValidateDataOnlyNumber}', action: 'blur', 
	           		rule: function (input) {
	           			var value = $(input).val();
	           			if (isNaN(value)) return false;
	           			else return true;
	           		}
           		}, 
           		<#if quotationSelected.salesChannel != "SALES_MT_CHANNEL">
           		{input: '#priceToMarket', message: '${uiLabelMap.DANotValidateDataOnlyNumber}', action: 'blur', 
	           		rule: function (input) {
	           			var value = $(input).val();
	           			if (isNaN(value)) return false;
	           			else return true;
	           		}
           		}, </#if>
           		{input: '#priceToConsumer', message: '${uiLabelMap.DANotValidateDataOnlyNumber}', action: 'blur', 
	           		rule: function (input) {
	           			var value = $(input).val();
	           			if (isNaN(value)) return false;
	           			else return true;
	           		}
           		}, 
	           	{input: "#jqxdropdownbuttonProduct", message: "${uiLabelMap.DAThisFieldIsRequired}", action: 'blur', 
           			rule: function (input, commit) {
        				var value = $(input).val();
                        return value != "";
                    }
               	}]
	    });
	    
	    //================================================================================================================
		$('[data-rel=tooltip]').tooltip();
	
		$(".select2").css('width','150px').select2({allowClear:true})
		.on('change', function(){
			$(this).closest('form').validate().element($(this));
		}); 
		
		$(".chzn-select").chosen({allow_single_deselect:true , no_results_text: "${uiLabelMap.DANoSuchState}"});
		
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if ((info.step == 1) && (info.direction == "next")) {
				if(!$('#updateQuotation').valid()) return false;
				$('#container').empty();
				$("#step-title").html("${uiLabelMap.DAConfirmation}");
				
		        var selectedRowIndexes = $('#jqxgridQuotationItems').jqxGrid('selectedrowindexes');
				if (selectedRowIndexes.length <= 0) {
					bootbox.dialog("${uiLabelMap.DAYouNotYetChooseProduct}!", [{
						"label" : "OK",
						"class" : "btn-small btn-primary",
						}]
					);
					return false;
				}
				dataSelected = new Array();
				$('#container').empty();
				for(var index in selectedRowIndexes) {
					var data = $('#jqxgridQuotationItems').jqxGrid('getrowdata', selectedRowIndexes[index]);
					var row = {};
					row["productId"] = data.productId;
					row["productName"] = data.productName;
					if (data.priceToDist != undefined) {
						row["priceToDist"] = data.priceToDist;
					} else {
						row["priceToDist"] = "";
					}
					if (data.priceToMarket != undefined) {
						row["priceToMarket"] = data.priceToMarket;
					} else {
						row["priceToMarket"] = "";
					}
					if (data.priceToConsumer != undefined) {
						row["priceToConsumer"] = data.priceToConsumer;
					} else {
						row["priceToConsumer"] = "";
					}
					if (data.priceToDistAfterVAT != undefined) {
						row["priceToDistAfterVAT"] = data.priceToDistAfterVAT;
					} else {
						row["priceToDistAfterVAT"] = "";
					}
					if (data.taxPercentage != undefined) {
						row["taxPercentage"] = data.taxPercentage;
					} else {
						row["taxPercentage"] = "";
					}
					if (data.productPackingUomId != undefined) {
						row["productPackingUomId"] = data.productPackingUomId;
					} else {
						row["productPackingUomId"] = "";
					}
					dataSelected[index] = row;
				}
				
				$("#strProductQuotationId").text($("#productQuotationId").val());
				$("#strQuotationName").text($("#quotationName").val());
				$("#strDescription").text($("#description").val());
				$("#strSalesChannel").text($("#salesChannel").val());
				$("#strCurrencyUomId").text($("#currencyUomId").val());
				$("#strFromDate").text($("#fromDate").val());
				$("#strThruDate").text($("#thruDate").val());
				
				<#if listRoleTypeMarket?exists && listRoleTypeMarket?size &gt; 0>
	    			$("#strPartyRoleTypesApplyPriceMarket").html($("#partyRoleTypesApplyMarket").val());
				</#if>
				<#if listPartyIdApply?exists && listPartyIdApply?size &gt; 0>
					$("#strPartyIdsApply").html($("#partyIdsApply").val());
				</#if>
				
				var partyApply = $("#partyRoleTypesApply").val();
				var strValue = ""; //<ul class=\"unstyled spaced2\" style=\"margin: 0 0 0 15px;\">
				var isFirst = true;
				for (var i = 0; i < partyApply.length; i++) {
					<#list roleTypes as roleType>
						var itemRoleType = "${roleType.roleTypeId}";
						if (itemRoleType == partyApply[i]) {
							//strValue += "<li style=\"margin-bottom: 0; margin-top:0\"><i class=\"icon-user green\"></i>";
							if (!isFirst) strValue += ", ";
							<#if roleType.description?exists>strValue += "${roleType.description}";<#else>strValue += "${roleType.roleTypeId}";</#if>
							//strValue += "<\/li>";
							isFirst = false;
						}
					</#list>
				}
				//strValue += ""; <\/ul>
				$("#strPartyRoleTypesApply").html(strValue);
				var sourceSuccessTwo = {
					localdata: dataSelected,
					dataType: "array",
					datafields:${dataField}
				};
				var dataAdapter = new $.jqx.dataAdapter(sourceSuccessTwo);
                $("#jqxgridProdSelected").jqxGrid({ source: dataAdapter });
			} else if ((info.step == 2) && (info.direction == "previous")) {
				alterData = null;
				$("#step-title").html("${uiLabelMap.DACreateQuotationForProduct}");
			}
		}).on('finished', function(e) {
			bootbox.confirm("${uiLabelMap.DAAreYouSureSave}", function(result) {
				if(result) {
					var formSend = document.getElementById('updateQuotation');
					var prodSelectedRows = $('#jqxgridProdSelected').jqxGrid('getrows');
					var prodSelectedListStr = "N";
					for (var i = 0; i < prodSelectedRows.length; i++) {
						var itemSelected = prodSelectedRows[i];
						prodSelectedListStr += "|OLBIUS|";
						if (itemSelected.productId != undefined) prodSelectedListStr += itemSelected.productId;
				        prodSelectedListStr += "|SUIBLO|";
				        if (itemSelected.priceToDist != undefined) prodSelectedListStr += itemSelected.priceToDist;
				        prodSelectedListStr += "|SUIBLO|";
				        if (itemSelected.priceToMarket != undefined) prodSelectedListStr += itemSelected.priceToMarket;
				        prodSelectedListStr += "|SUIBLO|";
				        if (itemSelected.priceToConsumer != undefined) prodSelectedListStr += itemSelected.priceToConsumer;
				        prodSelectedListStr += "|SUIBLO|";
				        if (itemSelected.priceToDistAfterVAT != undefined) prodSelectedListStr += itemSelected.priceToDistAfterVAT;
				        prodSelectedListStr += "|SUIBLO|";
				        if (itemSelected.taxPercentage != undefined) prodSelectedListStr += itemSelected.taxPercentage;
					}
				    var hiddenField = document.createElement("input");
		            hiddenField.setAttribute("type", "hidden");
		            hiddenField.setAttribute("name", "productListStr");
		            hiddenField.setAttribute("value", prodSelectedListStr);
		            formSend.appendChild(hiddenField);
				    formSend.submit();
				}
			});
		});
		
		$.validator.addMethod("greaterThan", function(value, element, params) {
			if (value == null || /^\s*$/.test(value)) {
				return true;
			} else {
	        	return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= Date.parseExact($(params).val(),"dd/MM/yyyy HH:mm:ss");
	        }
		},'Must be greater than');
		$.validator.addMethod('validateToDay',function(value,element){
			var now = new Date();
			now.setHours(0,0,0,0);
			return Date.parseExact(value,"dd/MM/yyyy HH:mm:ss") >= now;
		},'Greather than today');
		$('#updateQuotation').validate({
			errorElement: 'span',
			errorClass: 'help-inline',
			focusInvalid: false,
			rules: {
				currencyUomId: {
					required: true
				},
				partyRoleTypesApply: {
					required: true
				},
				quotationName: {
					required: true
				},
				fromDate_i18n:{
					validateToDay:true
				},
				thruDate_i18n:{
					greaterThan:'#fromDate_i18n'
				}
			},
			messages: {
				currencyUomId: {
					required: "${uiLabelMap.DAThisFieldIsRequired}"
				},
				partyRoleTypesApply: {
					required: "${uiLabelMap.DAThisFieldIsRequired}"
				},
				quotationName: {
					required: "${uiLabelMap.DAThisFieldIsRequired}"
				},
				fromDate_i18n:{
					validateToDay:"${StringUtil.wrapString(uiLabelMap.DARequiredValueGreaterThanOrEqualToToday)}"
				},
				thruDate_i18n:{
					greaterThan:'${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}'
				}
			},
			invalidHandler: function (event, validator) { //display error alert on form submit   
				$('.alert-error', $('.login-form')).show();
			},
			highlight: function (e) {
				$(e).closest('.control-group').removeClass('info').addClass('error');
			},
			unhighlight: function(element, errorClass) {
	    		var parentControls = $(element).closest(".controls");
	    		if (parentControls != undefined) {
	    			parentControls.find("ul.chzn-choices").css("border", "1px solid #64a6bc");
	    		}
	    	},
			success: function (e) {
				$(e).closest('.control-group').removeClass('error').addClass('info');
				$(e).remove();
			},
			errorPlacement: function (error, element) {
				var parentControls = element.closest(".controls");
				if (parentControls != undefined) {
					error.appendTo(parentControls);
					parentControls.find("ul.chzn-choices").css("border", "1px solid #f09784");
				}
			}
		});
	})
</script>
<#else>
	<div class="alert alert-info">${uiLabelMap.DAQuotationUpdatePermissionError}</div>
</#if>
