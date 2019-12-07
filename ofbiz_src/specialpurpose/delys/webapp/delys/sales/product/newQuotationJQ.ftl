<#--
<style type="text/css">
	input.jqx-combobox-input-olbius {
		padding-left:5px !important;
	}
</style>
-->
<#assign selectedSalesChannel = parameters.salesChannel?default("SALES_GT_CHANNEL")>
<#assign isAdvanceInterface = parameters.advanceInterface?default(false)>
<#assign isSalesAdminManager = Static["com.olbius.util.SalesPartyUtil"].isSalesAdminManagerEmployee(userLogin, delegator)/>
<#assign roleTypes = Static["com.olbius.util.SalesPartyUtil"].getListGVRoleMemberDescendantInGroup("DELYS_ROLE", delegator)/>
<#if isSalesAdminManager>
	<#assign roleTypesGT = Static["com.olbius.util.SalesPartyUtil"].getListGVRoleMemberDescendantInGroup("SALES_GT_CHANNEL", delegator)/>
	<#assign roleTypesMT = Static["com.olbius.util.SalesPartyUtil"].getListGVRoleMemberDescendantInGroup("SALES_MT_CHANNEL", delegator)/>
</#if>

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
	
	<#assign shoppingCart = Static['org.ofbiz.order.shoppingcart.ShoppingCartEvents'].getCartObject(request)>
	<#if shoppingCart?exists && shoppingCart?has_content>
		<#assign currencyUomId = shoppingCart.getCurrency()>
	</#if>
	<#assign selectedCurrency = parameters.currencyUomId?default(currencyUomId?default('VND'))>
	var localDataCurrency = new Array();
	<#list currencies as dataItem>
		<#assign description = StringUtil.wrapString(dataItem.get("description", locale)) />
		var row = {};
		row['uomId'] = '${dataItem.uomId}';
		row['description'] = "${description}";
		localDataCurrency[${dataItem_index}] = row;
	</#list>
	
	var roleTypeData = new Array();
	<#list roleTypes as roleTypeItem>
		var row = {};
		row['roleTypeId'] = '${roleTypeItem.roleTypeId}';
		row['description'] = '${StringUtil.wrapString(roleTypeItem.get("description", locale))}';
		roleTypeData[${roleTypeItem_index}] = row;
	</#list>
	
	<#if isSalesAdminManager>
		var roleTypeDataGT = new Array();
		<#list roleTypesGT as roleTypeItem>
			var row = {};
			row['roleTypeId'] = '${roleTypeItem.roleTypeId}';
			row['description'] = '${StringUtil.wrapString(roleTypeItem.get("description", locale))}';
			roleTypeDataGT[${roleTypeItem_index}] = row;
		</#list>
		
		var roleTypeDataMT = new Array();
		<#list roleTypesMT as roleTypeItem>
			var row = {};
			row['roleTypeId'] = '${roleTypeItem.roleTypeId}';
			row['description'] = '${StringUtil.wrapString(roleTypeItem.get("description", locale))}';
			roleTypeDataMT[${roleTypeItem_index}] = row;
		</#list>
	</#if>
</script>

<script type="text/javascript">
	var dataSelected = new Array();
	
	//productListStr=N|OLBIUS|monte_schoko_55g_4|SUIBLO|21000|SUIBLO||SUIBLO||OLBIUS|monte_vani_55g_4|SUIBLO|20000|SUIBLO||SUIBLO|
	var productListStrPara = "${StringUtil.wrapString(parameters.productListStr?default(""))}";
	var productQuotaArray = new Array();
	if ("" != productListStrPara) {
		var productListStrPara = productListStrPara.substring(1);
		var productListStrParaLine = productListStrPara.split("|OLBIUS|");
		for (var i = 0; i < productListStrParaLine.length; i++) {
			var dataLineStr = productListStrParaLine[i];
			if ("" != dataLineStr) {
				var dataLineArray = dataLineStr.split("|SUIBLO|");
				var dataRow = {};
				if (dataLineArray != null) {
					dataRow["productId"] = dataLineArray[0];
					dataRow["priceToDist"] = dataLineArray[1];
					dataRow["priceToMarket"] = dataLineArray[2];
					dataRow["priceToConsumer"] = dataLineArray[3];
					dataRow["priceToDistAfterVAT"] = dataLineArray[4];
				}
				productQuotaArray.push(dataRow);
			}
		}
	}
</script>
<div class="row-fluid">
	<h4 id="step-title" class="header smaller blue span3" style="margin-bottom:0 !important; border-bottom: none; margin-top:3px !important; padding-bottom:0">${uiLabelMap.DACreateQuotationForProduct}</h4>
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
	<div style="clear:both"></div>
	<hr style="margin: 8px 0" />

	<div class="step-content row-fluid position-relative" id="step-container">
		<div class="step-pane active" id="step1">
			<form class="form-horizontal basic-custom-form form-size-mini" id="createQuotation" name="createQuotation" method="post" action="<@ofbizUrl>createQuotation</@ofbizUrl>">
				<div class="row-fluid">
					<div class="span6">
						<div class="control-group">
							<label class="control-label" for="productQuotationId">${uiLabelMap.DAQuotationId}</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="productQuotationId" id="productQuotationId" class="span12" maxlength="20" value="${parameters.productQuotationId?if_exists}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label required" for="quotationName">${uiLabelMap.DAQuotationName}</label>
							<div class="controls">
								<div class="span12">
									<input type="text" name="quotationName" id="quotationName" class="span12" maxlength="100" value="${parameters.quotationName?if_exists}">
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="description">${uiLabelMap.CommonDescription}</label>
							<div class="controls">
								<div class="span12">
									<textarea id="description" name="description" class="autosize-transition span12" style="resize: vertical">${parameters.description?if_exists}</textarea>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label required" for="salesChannel">${uiLabelMap.DASalesChannel}</label>
							<div class="controls">
								<div class="span12">
									<select name="salesChannel" id="salesChannel" class="span12 input-less-small">
										<#list salesMethodChannels as salesMethodChannel>
											<option value="${salesMethodChannel.enumId}" <#if salesMethodChannel.enumId == selectedSalesChannel>selected="selected"</#if>>${salesMethodChannel.get("description", locale)}</option>
										</#list>
						            </select>
						            <div class="control-group" style="display:inline-block">
										<label class="control-label required" for="currencyUomId" style="width: 60px !important; color:#393939">${uiLabelMap.DAAbbCurrency}</label>
										<div class="controls" style="margin-left:80px !important">
											<div class="span12" style="text-align:right">
												<#--
												<select name="currencyUomId" id="currencyUomId" class="input-mini chzn-select" data-placeholder="${uiLabelMap.DAChooseACurrency}...">
									              	<option value=""></option>
									              	<#assign selectedCurrency = parameters.currencyUomId?default(currencyUomId?default(''))>
									              	<#list currencies as currency>
										              	<option value="${currency.uomId}" <#if selectedCurrency == currency.uomId>selected="selected"</#if> />${currency.uomId}
									              	</#list>
									            </select>
												-->
									            <div id="currencyUomId" name="currencyUomId"></div>
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
							<label class="control-label required" for="partyRoleTypesApply2">${uiLabelMap.DAPartyApply}</label>
							<div class="controls">
								<div class="span12">
									<div id="containPartyRoleTypesApply" <#if isAdvanceInterface?string == "false">style="display:none"</#if>>
										<div>
											<div style="display:inline-block">
												<div id="partyRoleTypesApply2"></div>
											</div>
											<span class="help-inline" title="${uiLabelMap.DARoleIdOfPartyGroupApplyPriceToCustomer}" data-rel="tooltip" data-placement="top" style="vertical-align: top"><i class="icon-info-sign" style="color:#4b89aa;font-size:15px;vertical-align:middle"></i></span>
										</div>
										
										<div>
											<input type="text" id="partyRoleTypesApplyMarket" name="partyRoleTypesApplyMarket" value="DELYS_CUSTOMER_GT" class="span12" style="width:220px"/>
											<span class="help-inline" title="${uiLabelMap.DAPartyApplyPriceToMarket}" data-rel="tooltip" data-placement="top" style="vertical-align: top"><i class="icon-info-sign" style="color:#4b89aa;font-size:15px;vertical-align:middle"></i></span>
										</div>
										
										<div style="margin-top:5px">
											<div style="display:inline-block">
												<div id="partyIdsApply2"></div>
											</div>
											<span class="help-inline" title="${uiLabelMap.DAPartyIdApplyPriceToCustomer}" data-rel="tooltip" data-placement="top" style="vertical-align: top"><i class="icon-info-sign" style="color:#4b89aa;font-size:15px;vertical-align:middle"></i></span>
										</div>
										<#--<select multiple="" class="chzn-select" name="partyRoleTypesApply" id="partyRoleTypesApply" data-placeholder="${uiLabelMap.DAChooseAObjectApply}...">
											<option value="" />
											<#if parameters.partyRoleTypesApply?exists && parameters.partyRoleTypesApply?is_string>
												<#assign partyRoleTypesApplies = ["${parameters.partyRoleTypesApply}"]/>
											<#else>
												<#assign partyRoleTypesApplies = parameters.partyRoleTypesApply?default([])/>
											</#if>
											<#list roleTypes as roleType>
												<option value="${roleType.roleTypeId}" 
												<#list partyRoleTypesApplies as partyRoleTypesApply>
													<#if partyRoleTypesApply == roleType.roleTypeId>
														selected="selected" 
													</#if>
												</#list>
												/>${roleType.get('description', locale)} [${roleType.roleTypeId}]
											</#list>
										</select>-->
									</div>
									<input type="text" id="partyRoleTypesApplyDefault" name="partyRoleTypesApplyDafult" value="<#if selectedSalesChannel == "SALES_MT_CHANNEL">CUSTOMER_MT<#else>DELYS_DISTRIBUTOR</#if>" readonly="readonly" class="span12" style="width:220px; <#if isAdvanceInterface?string == "true">display:none</#if>"/>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label required" for="fromDate">${uiLabelMap.DAFromDate}</label>
							<div class="controls">
								<div class="span12">
									<div id="fromDate"></div>
									<#--<@htmlTemplate.renderDateTimeField name="fromDate" id="fromDate" value="${parameters.fromDate?if_exists}" event="" action="" className="" alert="" 
										title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
										timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
										classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
										pmSelected="" compositeType="" formName=""/>-->
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="thruDate">${uiLabelMap.DAThroughDate}</label>
							<div class="controls">
								<div class="span12">
									<div id="thruDate"></div>
									<#--<@htmlTemplate.renderDateTimeField name="thruDate" id="thruDate" value="${parameters.thruDate?if_exists}" event="" action="" className="" alert="" 
										title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" shortDateInput=false 
										timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" 
										classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" 
										pmSelected="" compositeType="" formName=""/>-->
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label" for="advanceInterface">${uiLabelMap.DAAdvanceInterface}</label>
							<div class="controls">
								<div class="span12">
									<div style="display:inline-block">
										<div id='advanceInterface'></div>
									</div>
									<div style="display:inline-block; margin-left:25px; vertical-align:top; margin-top:-5px">
										<input type="button" id="copyToMarket" title="${uiLabelMap.DACopyPriceDistToPriceMarket}" data-rel="tooltip" data-placement="top" value="${uiLabelMap.DACopy}"/>
									</div>
								</div>
							</div>
						</div>
					</div><!--.span6-->
				</div><!--.row-fluid-->
			</form>
			<div style="clear:both"></div>
			<div class="row-fluid" style="margin-top:10px">
				<div class="span12">
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
										 },
										 { text: '${uiLabelMap.DABeforeVAT}', dataField: 'priceToDist', width: '12%', cellsalign: 'right', filterable:false, sortable:false, cellsformat: 'c2', columnGroup: 'PriceToCustomerColGroup', 
										 	cellsrenderer: function(row, column, value){
										 		var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (data != undefined && data != null && value == \"\") {
						    						for(i = 0 ; i < productQuotaArray.length; i++){
						    							if (data.productId == productQuotaArray[i].productId){
						    								var currentValue = productQuotaArray[i].priceToDist;
						    								data.priceToDist = currentValue;
						    								if (currentValue != \"\") {
						    									returnVal += formatcurrency(currentValue, $(\"currencyUomId\").val());
						    								}
						    								returnVal += '</div>';
									   						return returnVal;
						    							}
						    						}
									   			} else if (value != \"\") {
									   				returnVal += formatcurrency(value, $(\"currencyUomId\").val()) + '</div>';
									   				return returnVal;
									   			}
									   			returnVal += value + '</div>';
								   				return returnVal;
										 	}
										 },
										 { text: '${uiLabelMap.DAAfterVAT}', dataField: 'priceToDistAfterVAT', width: '12%', cellsalign: 'right', filterable:false, sortable:false, cellsformat: 'c2', columnGroup: 'PriceToCustomerColGroup', 
										 	cellsrenderer: function(row, column, value){
										 		var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (data != undefined && data != null && value == \"\") {
						    						for(i = 0 ; i < productQuotaArray.length; i++){
						    							if (data.productId == productQuotaArray[i].productId){
						    								var currentValue = productQuotaArray[i].priceToDistAfterVAT;
						    								data.priceToDistAfterVAT = currentValue;
						    								if (currentValue != \"\") {
						    									returnVal += formatcurrency(currentValue, $(\"currencyUomId\").val());
						    								}
						    								returnVal += '</div>';
									   						return returnVal;
						    							}
						    						}
									   			} else if (value != \"\") {
									   				returnVal += formatcurrency(value, $(\"currencyUomId\").val()) + '</div>';
									   				return returnVal;
									   			}
									   			returnVal += value + '</div>';
								   				return returnVal;
										 	}
										 },
										 { text: '${uiLabelMap.DATheMarketPriceOfDistributor}<br/>${uiLabelMap.DAParenthesisAfterVAT}', dataField: 'priceToMarket', width: '12%', cellsalign: 'right', 
										 	filterable:false, sortable:false, cellsformat: 'c2', 
										 	cellsrenderer: function(row, column, value){
										 		var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (data != undefined && data != null && value == \"\") {
						    						for(i = 0 ; i < productQuotaArray.length; i++){
						    							if (data.productId == productQuotaArray[i].productId){
						    								var currentValue = productQuotaArray[i].priceToMarket;
						    								data.priceToMarket = currentValue;
						    								if (currentValue != \"\") {
						    									returnVal += formatcurrency(currentValue, $(\"currencyUomId\").val());
						    								}
						    								returnVal += '</div>';
									   						return returnVal;
						    							}
						    						}
									   			} else if (value != \"\") {
									   				returnVal += formatcurrency(value, $(\"currencyUomId\").val()) + '</div>';
									   				return returnVal;
									   			}
									   			returnVal += value + '</div>';
								   				return returnVal;
										 	}
										 },
										 { text: '${uiLabelMap.DAPricesProposalForConsumer}<br/>${uiLabelMap.DAParenthesisAfterVAT}', dataField: 'priceToConsumer', width: '12%', cellsalign: 'right', 
										 	filterable:false, sortable:false, cellsformat: 'c2', 
										 	cellsrenderer: function(row, column, value){
										 		var data = $('#jqxgridProd').jqxGrid('getrowdata', row);
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (data != undefined && data != null && value == \"\") {
						    						for(i = 0 ; i < productQuotaArray.length; i++){
						    							if (data.productId == productQuotaArray[i].productId){
						    								var currentValue = productQuotaArray[i].priceToConsumer;
						    								data.priceToConsumer = currentValue;
						    								if (currentValue != \"\") {
						    									returnVal += formatcurrency(currentValue, $(\"currencyUomId\").val());
						    								}
						    								returnVal += '</div>';
									   						return returnVal;
						    							}
						    						}
									   			} else if (value != \"\") {
									   				returnVal += formatcurrency(value, $(\"currencyUomId\").val()) + '</div>';
									   				return returnVal;
									   			}
									   			returnVal += value + '</div>';
								   				return returnVal;
										 	}
										 }
					              		"/>
					<#assign columngrouplist = "{ text: '${uiLabelMap.DAPriceToDistributor}', align: 'center', name: 'PriceToDistributorColGroup' },
												{ text: '${uiLabelMap.DAPriceToCustomer}', align: 'center', name: 'PriceToCustomerColGroup' }" />
					
					<@jqGrid id="jqxgridProd" clearfilteringbutton="true" editable="true" alternativeAddPopup="alterpopupWindow" columnlist=columnlist dataField=dataField
							viewSize="30" showtoolbar="false" editmode="click" selectionmode="checkbox" columngrouplist=columngrouplist 
							url="jqxGeneralServicer?sname=JQGetListProductAndTaxByCatalog&catalogId=${currentCatalogId?if_exists}"/>
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
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAPartyApply}:</label>
							<div class="controls-desc">
								<span id="strPartyRoleTypesApply"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAAbbPartyApplyPriceToMarket}:</label>
							<div class="controls-desc">
								<span id="strPartyRoleTypesApplyPriceMarket"></span>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label-desc">${uiLabelMap.DAPartyIdApplyPriceToCustomer}:</label>
							<div class="controls-desc">
								<span id="strPartyIdsApply"></span>
							</div>
						</div>
						
					</div><!--.span6-->
				</div><!--.row-fluid-->
				
				<div class="row-fluid">
					<div class="span12">
						<div id="jqxgridProdSelected" style="width: 100%">
						</div>
						<#assign columnlist2Dist ="[{ text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '16%', editable:false},
										 { text: '${uiLabelMap.DAProductName}', dataField: 'productName', editable:false},
										 { text: '${uiLabelMap.DATax}', dataField: 'taxPercentage', editable:false, width: '7%', cellsalign: 'right', cellsformat: 'p'},
										 { text: '${uiLabelMap.DAAbbPackingSpecification}', dataField: 'productPackingUomId', width: '7%', cellsalign: 'center',  
										 	cellsrenderer: function(row, column, value){
					    						for (var i = 0 ; i < uomData.length; i++){
					    							if (value == uomData[i].uomId){
					    								return '<span title = ' + uomData[i].description +'>' + uomData[i].description + '</span>';
					    							}
					    						}
					    						return '<span title=' + value +'>' + value + '</span>';
											}
										 },
										 { text: '${uiLabelMap.DABeforeVAT}', dataField: 'priceToDist', width: '12%', cellsalign: 'right', cellsformat: 'c2', columnGroup: 'PriceToCustomerColGroup', 
										 	cellsrenderer: function(row, column, value){
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (value != \"\") {
									   				returnVal += formatcurrency(value, $(\"currencyUomId\").val()) + '</div>';
									   				return returnVal;
									   			}
									   			returnVal += value + '</div>';
								   				return returnVal;
										 	}
										 },
										 { text: '${uiLabelMap.DAAfterVAT}', dataField: 'priceToDistAfterVAT', width: '12%', cellsalign: 'right', cellsformat: 'c2', columnGroup: 'PriceToCustomerColGroup', 
										 	cellsrenderer: function(row, column, value){
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (value != \"\") {
									   				returnVal += formatcurrency(value, $(\"currencyUomId\").val()) + '</div>';
									   				return returnVal;
									   			}
									   			returnVal += value + '</div>';
								   				return returnVal;
										 	}
										 },
										 { text: '${uiLabelMap.DATheMarketPriceOfDistributor}<br/>${uiLabelMap.DAParenthesisAfterVAT}', dataField: 'priceToMarket', width: '12%', cellsalign: 'right', cellsformat: 'c2', 
										 	cellsrenderer: function(row, column, value){
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (value != \"\") {
									   				returnVal += formatcurrency(value, $(\"currencyUomId\").val()) + '</div>';
									   				return returnVal;
									   			}
									   			returnVal += value + '</div>';
								   				return returnVal;
										 	}
										 },
										 { text: '${uiLabelMap.DAPricesProposalForConsumer}<br/>${uiLabelMap.DAParenthesisAfterVAT}', dataField: 'priceToConsumer', width: '12%', cellsalign: 'right', cellsformat: 'c2', 
										 	cellsrenderer: function(row, column, value){
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (value != \"\") {
									   				returnVal += formatcurrency(value, $(\"currencyUomId\").val()) + '</div>';
									   				return returnVal;
									   			}
									   			returnVal += value + '</div>';
								   				return returnVal;
										 	}
										 }]
					              		"/>
					    <#assign columnlist2SuperMarket ="[{ text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '16%', editable:false},
										 { text: '${uiLabelMap.DAProductName}', dataField: 'productName', editable:false},
										 { text: '${uiLabelMap.DATax}', dataField: 'taxPercentage', editable:false, width: '7%', cellsalign: 'right', cellsformat: 'p'},
										 { text: '${uiLabelMap.DAAbbPackingSpecification}', dataField: 'productPackingUomId', width: '7%', cellsalign: 'center',  
										 	cellsrenderer: function(row, column, value){
					    						for (var i = 0 ; i < uomData.length; i++){
					    							if (value == uomData[i].uomId){
					    								return '<span title = ' + uomData[i].description +'>' + uomData[i].description + '</span>';
					    							}
					    						}
					    						return '<span title=' + value +'>' + value + '</span>';
											}
										 },
										 { text: '${uiLabelMap.DABeforeVAT}', dataField: 'priceToDist', width: '12%', cellsalign: 'right', cellsformat: 'c2', columnGroup: 'PriceToCustomerColGroup', 
										 	cellsrenderer: function(row, column, value){
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (value != \"\") {
									   				returnVal += formatcurrency(value, $(\"currencyUomId\").val()) + '</div>';
									   				return returnVal;
									   			}
									   			returnVal += value + '</div>';
								   				return returnVal;
										 	}
										 },
										 { text: '${uiLabelMap.DAAfterVAT}', dataField: 'priceToDistAfterVAT', width: '12%', cellsalign: 'right', cellsformat: 'c2', columnGroup: 'PriceToCustomerColGroup', 
										 	cellsrenderer: function(row, column, value){
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (value != \"\") {
									   				returnVal += formatcurrency(value, $(\"currencyUomId\").val()) + '</div>';
									   				return returnVal;
									   			}
									   			returnVal += value + '</div>';
								   				return returnVal;
										 	}
										 },
										 { text: '${uiLabelMap.DAPriceToConsumer}<br/>${uiLabelMap.DAParenthesisAfterVAT}', dataField: 'priceToConsumer', width: '12%', cellsalign: 'right', cellsformat: 'c2', 
										 	cellsrenderer: function(row, column, value){
										 		var returnVal = '<div style=\"overflow: hidden; text-overflow: ellipsis; padding-bottom: 2px; text-align: right; margin-right: 2px; margin-left: 4px; margin-top: 4px;\">';
										 		if (value != \"\") {
									   				returnVal += formatcurrency(value, $(\"currencyUomId\").val()) + '</div>';
									   				return returnVal;
									   			}
									   			returnVal += value + '</div>';
								   				return returnVal;
										 	}
										 }]
					              		"/>
					              		
						<script type="text/javascript">
							$(document).ready(function () {
								var sourceSuccess = {
										localdata: dataSelected,
										dataType: "array",
										datafields: ${dataField}
									};
								var dataAdapterSuccess = new jQuery.jqx.dataAdapter(sourceSuccess);
								/*var renderDiff = function (row, column, value) {
									var data = $('#jqxgridProdSelected').jqxGrid('getrowdata', row);
									var diffQuantity = data.realQuantity - data.quantityOnHandTotal;
									return '<span style = \"margin-left: 10px\"' + '>' + diffQuantity + '</span>';
							    }
								var renderTotal = function (row, column, value) {
									var data = $('#jqxgridProdSelected').jqxGrid('getrowdata', row);
									var total = (data.realQuantity - data.quantityOnHandTotal)*data.unitCost;
									return '<span style = \"margin-left: 10px\"' + '>' + total + '</span>';
							    }*/
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
							        columns: ${columnlist2Dist},
							        columngroups: [
							        	{ text: '${uiLabelMap.DAPriceToCustomer}', align: 'center', name: 'PriceToCustomerColGroup' }
							        ]
								});
							});
						</script>
						
						<#--
						<#assign dataFieldTwo="[{ name: 'productId', type: 'string' },
					               		{ name: 'productName', type: 'string' },
					               		{ name: 'priceToDist', type: 'number', formatter: 'integer'},
					               		{ name: 'priceToMarket', type: 'number', formatter: 'integer'},
					               		{ name: 'priceToConsumer', type: 'number', formatter: 'integer'}
					                	]"/>
						<#assign columnlistTwo="{ text: '${uiLabelMap.DAProductId}', dataField: 'productId', width: '180px'},
										 { text: '${uiLabelMap.DAProductName}', dataField: 'productName'},
										 { text: '${uiLabelMap.DAPriceToDistributor}', dataField: 'priceToDist', width: '180px', cellsalign: 'right'},
										 { text: '${uiLabelMap.DATheMarketPriceOfDistributor}', dataField: 'priceToMarket', width: '180px', cellsalign: 'right'},
										 { text: '${uiLabelMap.DAPricesProposalForConsumer}', dataField: 'priceToConsumer', width: '180px', cellsalign: 'right'}
					              		"/>
						<@jqGrid id="jqxgridProdSelected" clearfilteringbutton="true" editable="false" alternativeAddPopup="alterpopupWindow" columnlist=columnlistTwo dataField=dataFieldTwo
								viewSize="30" showtoolbar="false" editmode="click" filtersimplemode="false" 
								url="jqxGeneralServicer?sname=jqxgridProdSelected"/>
						-->
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

<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxswitchbutton.js"></script>
<script src="/delys/images/js/fuelux/fuelux.wizard.min.js"></script>
<script src="/delys/images/js/bootbox.min.js"></script>
<script src="/delys/images/js/select2.min.js"></script>
<script type="text/javascript">
	var alterData = null;
	
	function loadMtInterface() {
		$("#partyRoleTypesApplyDefault").val("CUSTOMER_MT");
		$("#jqxgridProd").jqxGrid("hidecolumn", "priceToMarket");
		$('#jqxgridProd').jqxGrid('setcolumnproperty', 'priceToDist', 'columngroup', 'PriceToCustomerColGroup');
		$('#jqxgridProd').jqxGrid('setcolumnproperty', 'priceToDistAfterVAT', 'columngroup', 'PriceToCustomerColGroup');
		$('#jqxgridProd').jqxGrid('setcolumnproperty', 'priceToConsumer', 'text', '${uiLabelMap.DAPriceToConsumer}<br/>${uiLabelMap.DAParenthesisAfterVAT}');
	}
	
	function loadGtInterface() {
		$("#partyRoleTypesApplyDefault").val("DELYS_DISTRIBUTOR");
		$("#jqxgridProd").jqxGrid("showcolumn", "priceToMarket");
		$('#jqxgridProd').jqxGrid('setcolumnproperty', 'priceToDist', 'columngroup', 'PriceToDistributorColGroup');
		$('#jqxgridProd').jqxGrid('setcolumnproperty', 'priceToDistAfterVAT', 'columngroup', 'PriceToDistributorColGroup');
		$('#jqxgridProd').jqxGrid('setcolumnproperty', 'priceToConsumer', 'text', '${uiLabelMap.DAPricesProposalForConsumer}<br/>${uiLabelMap.DAParenthesisAfterVAT}');
	}
	
	function reloadRoleType() {
	    if ("SALES_GT_CHANNEL" == $('#salesChannel').val()) {
		    var sourceRoleTypeGT = {localdata: roleTypeDataGT};
		    var dataAdapterRoleTypeGT = new $.jqx.dataAdapter(sourceRoleTypeGT, {
		        	formatData: function (data) {
		                if ($("#partyRoleTypesApply2").jqxComboBox('searchString') != undefined) {
		                    data.searchKey = $("#partyRoleTypesApply2").jqxComboBox('searchString');
		                    return data;
		                }
		            }
		        }
		    );
		    $("#partyRoleTypesApply2").jqxComboBox({source: dataAdapterRoleTypeGT});
	    } else if ("SALES_MT_CHANNEL" == $('#salesChannel').val()) {
	    	var sourceRoleTypeMT = {localdata: roleTypeDataMT};
		    var dataAdapterRoleTypeMT = new $.jqx.dataAdapter(sourceRoleTypeMT, {
		        	formatData: function (data) {
		                if ($("#partyRoleTypesApply2").jqxComboBox('searchString') != undefined) {
		                    data.searchKey = $("#partyRoleTypesApply2").jqxComboBox('searchString');
		                    return data;
		                }
		            }
		        }
		    );
		    $("#partyRoleTypesApply2").jqxComboBox({source: dataAdapterRoleTypeMT});
	    }
	}
	
	$(document).ready(function () {
		$('[data-rel=tooltip]').tooltip();
	
		$(".select2").css('width','150px').select2({allowClear:true})
		.on('change', function(){
			$(this).closest('form').validate().element($(this));
		}); 
		
		$(".chzn-select").chosen({allow_single_deselect:true , no_results_text: "${uiLabelMap.DANoSuchState}"});
		
		<#if selectedSalesChannel?exists && "SALES_MT_CHANNEL" == selectedSalesChannel>
			$(window).load(function () {
				loadMtInterface();
			});
		</#if>
		
		$("#salesChannel").change(function(){
			if ("SALES_MT_CHANNEL" == $('#salesChannel').val()) {
				loadMtInterface();
			} else if ("SALES_GT_CHANNEL" == $('#salesChannel').val()) {
				loadGtInterface();
			}
			reloadRoleType();
		});
		
		$("#copyToMarket").jqxButton({ width: '90', height: '28px', theme: theme});
		$("#percentagePriceToConsumer").jqxNumberInput({ 
			width: '218px', digits: 3, symbolPosition: 'right', symbol: '%', spinButtons: true 
		}).keyup(function(evn){
     		if(evn.keyCode === 189){
     			var newValue = Math.abs($('#percentagePriceToConsumer').val());
         		$('#percentagePriceToConsumer').jqxNumberInput('setDecimal', newValue);
     		}
	 	});
		$("#fromDate").jqxDateTimeInput({width: '218px', height: '25px', allowNullDate: true, value: null, formatString: 'dd/MM/yyyy HH:mm:ss'});<#--yyyy-MM-dd HH:mm:ss-->
		$("#thruDate").jqxDateTimeInput({width: '218px', height: '25px', allowNullDate: true, value: null, formatString: 'dd/MM/yyyy HH:mm:ss'});
		<#if isAdvanceInterface?exists && isAdvanceInterface?string == "true">
			$('#advanceInterface').jqxSwitchButton({ height: 25, width: 71, checked: true });
		<#else>
			$('#advanceInterface').jqxSwitchButton({ height: 25, width: 71, checked: false });
		</#if>
		
		<#if parameters.fromDate?exists>
		$('#fromDate').jqxDateTimeInput('setDate', '${StringUtil.wrapString(parameters.fromDate)}');
		</#if>
		<#if parameters.thruDate?exists>
		$('#thruDate').jqxDateTimeInput('setDate', '${StringUtil.wrapString(parameters.thruDate)}');
		</#if>
		
		$("#copyToMarket").on('click', function () {
	     	var datas = $("#jqxgridProd").jqxGrid("getrows");
	     	if (datas && datas.length > 0) {
	     		for (var i = 0; i < datas.length; i++) {
	     			var data = datas[i];
	     			if (data && data.priceToDistAfterVAT) {
	     				var priceToDistAfterVAT = data.priceToDistAfterVAT;
		     			$('#jqxgridProd').jqxGrid('setcellvalue', i, 'priceToMarket', priceToDistAfterVAT);
	     			}
	     		}
	     	}
	    });
		
		$('#advanceInterface').bind('unchecked', function (event) {
			$("#containPartyRoleTypesApply").css("display", "block");
			$("#partyRoleTypesApplyDefault").css("display", "none");
			$("#partyRoleTypesApply2").jqxComboBox({ showArrow: true });
		});
		$('#advanceInterface').bind('checked', function (event) {
			$("#containPartyRoleTypesApply").css("display", "none");
			$("#partyRoleTypesApplyDefault").css("display", "block");
		});
		<#--
		$('#advanceInterface').on('click', function () {
			var advanceInterface = $("#advanceInterface").val();
			if (advanceInterface) {
				$("#containPartyRoleTypesApply").css("display", "block");
				$("#partyRoleTypesApplyDefault").css("display", "none");
			} else {
				$("#containPartyRoleTypesApply").css("display", "none");
				$("#partyRoleTypesApplyDefault").css("display", "block");
			}
		});
		-->
		
		var sourceCurrency = {
			localdata: localDataCurrency,
	        datatype: "array",
	        datafields: [
	            { name: 'uomId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterCurrency = new $.jqx.dataAdapter(sourceCurrency, {
	            formatData: function (data) {
	                if ($("#currencyUomId").jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#currencyUomId").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $("#currencyUomId").jqxComboBox({
	        width: 80,
	        placeHolder: " ${StringUtil.wrapString(uiLabelMap.DAChooseACurrency)}",
	        dropDownWidth: 280,
	        height: 25,
	        source: dataAdapterCurrency,
	        remoteAutoComplete: false,
	        autoDropDownHeight: false,               
	        displayMember: "uomId",
	        valueMember: "uomId",
	        renderer: function (index, label, value) {
	            var item = dataAdapterCurrency.records[index];
	            if (item != null) {
	                var label = item.uomId;
	                return label;
	            }
	            return "";
	        },
	        renderSelectedItem: function(index, item)
	        {
	            var item = dataAdapterCurrency.records[index];
	            if (item != null) {
	                var label = item.uomId;
	                return label;
	            }
	            return "";
	        },
	        search: function (searchString) {
	            dataAdapterCurrency.dataBind();
	        }
	    });
	    $("#currencyUomId").jqxComboBox('selectItem', 'VND');
	    
	    var sourceRoleType = {
			localdata: roleTypeData,
	        datatype: "array",
	        datafields: [
	            { name: 'roleTypeId' },
	            { name: 'description' }
	        ]
	    };
	    var dataAdapterRoleType = new $.jqx.dataAdapter(sourceRoleType, {
	        	formatData: function (data) {
	                if ($("#partyRoleTypesApply2").jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#partyRoleTypesApply2").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $("#partyRoleTypesApply2").jqxComboBox({source: dataAdapterRoleType, multiSelect: true, width: 218, height: 25, dropDownWidth: 280, 
	    	placeHolder: "${StringUtil.wrapString(uiLabelMap.DAChooseRoleIdOfPartyGroupApply)}", 
	    	displayMember: "description", 
	    	valueMember: "roleTypeId", 
	    	renderer: function (index, label, value) {
                    var valueStr = label + " [" + value + "]";
                    return valueStr;
                },
            renderSelectedItem: function(index, item) {
	            var item = dataAdapterRoleType.records[index];
	            if (item != null) {
	                var label = item.description;
	                return label;
	            }
	            return "";
	        },
            search: function (searchString) {
	            dataAdapterRoleType.dataBind();
	        }
	    });
	    
	    <#if isSalesAdminManager>
	    	$(window).load(function () {
				reloadRoleType();
			});
	    </#if>
	    
	    <#if parameters.partyRoleTypesApply?exists>
		    <#if parameters.partyRoleTypesApply?is_collection>
		    	<#assign partyRoleTypesApplies = parameters.partyRoleTypesApply/>
			    <#list roleTypes as roleType>
					<#list partyRoleTypesApplies as partyRoleTypesApply>
						<#if partyRoleTypesApply == roleType.roleTypeId>
							$("#partyRoleTypesApply2").jqxComboBox('selectItem',"${partyRoleTypesApply}");
						</#if>
					</#list>
				</#list>
		    <#else>
				<#assign partyRoleTypesApply = parameters.partyRoleTypesApply/>
			    <#list roleTypes as roleType>
					<#if partyRoleTypesApply == roleType.roleTypeId>
						$("#partyRoleTypesApply2").jqxComboBox('selectItem',"${partyRoleTypesApply}");
					</#if>
				</#list>
		    </#if>
	    </#if>
	    
        var sourcePartyIdsApply = {
			datatype: "json",
	        datafields: [
	            { name: 'partyId' },
                { name: 'firstName' },
                { name: 'middleName' },
                { name: 'lastName' },
                { name: 'groupName' }
	        ],
	        data: {},
	        type: "POST",
	        root: "listParty",
	        contentType: 'application/x-www-form-urlencoded',
	        url: "getPartiesJson"
	    };
	    var dataAdapterPartyIdsApply = new $.jqx.dataAdapter(sourcePartyIdsApply, {
	            formatData: function (data) {
	                if ($("#partyIdsApply2").jqxComboBox('searchString') != undefined) {
	                    data.searchKey = $("#partyIdsApply2").jqxComboBox('searchString');
	                    return data;
	                }
	            }
	        }
	    );
	    $("#partyIdsApply2").jqxComboBox({
	        width: 218,
	        placeHolder: " ${StringUtil.wrapString(uiLabelMap.DAChoosePartyIdApply)}",
	        dropDownWidth: 218,
	        height: 25,
	        source: dataAdapterPartyIdsApply,
	        remoteAutoComplete: true,
	        autoDropDownHeight: true,               
	        displayMember: "partyId",
	        valueMember: "partyId",
	        renderer: function (index, label, value) {
	            var item = dataAdapterPartyIdsApply.records[index];
	            if (item != null) {
	                var label = item.partyId;
	                return label;
	            }
	            return "";
	        },
	        renderSelectedItem: function(index, item)
	        {
	            var item = dataAdapterPartyIdsApply.records[index];
	            if (item != null) {
	                var label = item.partyId;
	                return label;
	            }
	            return "";
	        },
	        search: function (searchString) {
	            dataAdapterPartyIdsApply.dataBind();
	        }
	    });
	    
	    <#if parameters.partyIdsApply?exists>
		    <#if parameters.partyIdsApply?is_collection>
		    	<#assign partyIdsApplies = parameters.partyIdsApply/>
				<#list partyIdsApplies as partyIdsApply>
					$("#partyIdsApply2").jqxComboBox('selectItem',"${partyIdsApply}");
				</#list>
		    <#else>
				<#assign partyIdsApply = parameters.partyIdsApply/>
				$("#partyIdsApply2").jqxComboBox('selectItem',"${partyIdsApply}");
		    </#if>
	    </#if>
		
		$("#jqxgridProd").on("cellEndEdit", function (event) {
	    	var args = event.args;
	    	if (args.datafield == "priceToDist") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProd").jqxGrid("getrowdata", rowBoundIndex);
		    	if (data && data.taxPercentage) {
		    		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (newValue == undefined || newValue == null || (/^\s*$/.test(newValue))) {
				   		var valueCal = "";
			    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'priceToDistAfterVAT', valueCal);
		    			$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'priceToConsumer', valueCal);
			   		} else {
			   		 
			   			var taxPercentage = data.taxPercentage;
				   		var valueCal = newValue * (1 + taxPercentage / 100);
			    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'priceToDistAfterVAT', valueCal);
			    		var percentagePriceToConsumer = $("#percentagePriceToConsumer").val();
		    			var percentagePriceToConsumerVal = valueCal * ((100 + percentagePriceToConsumer) / 100);
		    			$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'priceToConsumer', percentagePriceToConsumerVal);
			   		}
		    	}
	    	} else if (args.datafield == "priceToDistAfterVAT") {
	    		var rowBoundIndex = args.rowindex;
		    	var data = $("#jqxgridProd").jqxGrid("getrowdata", rowBoundIndex);
		    	if (data && data.taxPercentage) {
		    		var oldValue = args.oldvalue;
			   		var newValue = args.value;
			   		if (newValue == undefined || newValue == null || (/^\s*$/.test(newValue))) {
			   			var valueCal = "";
			    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'priceToDist', valueCal);
		    			$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'priceToConsumer', valueCal);
			   		} else {
			   			var taxPercentage = data.taxPercentage;
				   		var valueCal = newValue * (100 / (100 + taxPercentage));
			    		$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'priceToDist', valueCal);
			    		
			    		var percentagePriceToConsumer = $("#percentagePriceToConsumer").val();
		    			var percentagePriceToConsumerVal = newValue * ((100 + percentagePriceToConsumer) / 100);
		    			$('#jqxgridProd').jqxGrid('setcellvalue', rowBoundIndex, 'priceToConsumer', percentagePriceToConsumerVal);
			   		}
		    	}
	    	}
    	});
    	
    	$('#percentagePriceToConsumer').on('change', function (event) {
	     	var value = event.args.value;
	     	var datas = $("#jqxgridProd").jqxGrid("getrows");
	     	var percentagePriceToConsumer = $("#percentagePriceToConsumer").val();
	     	if (datas && datas.length > 0) {
	     		for (var i = 0; i < datas.length; i++) {
	     			var data = datas[i];
	     			if (data && data.priceToDistAfterVAT) {
	     				var priceToDistAfterVAT = data.priceToDistAfterVAT;
		     			var percentagePriceToConsumerVal = priceToDistAfterVAT * ((100 + percentagePriceToConsumer) / 100);
		     			$('#jqxgridProd').jqxGrid('setcellvalue', i, 'priceToConsumer', percentagePriceToConsumerVal);
	     			}
	     		}
	     	}
	 	});
	 	
	 	$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			if ((info.step == 1) && (info.direction == "next")) {
				if (!$('#advanceInterface').val()) {
					// is simple interfact
					$("#partyRoleTypesApply2").jqxComboBox('selectItem',$("#partyRoleTypesApplyDefault").val());
					//$("#partyRoleTypesApply2").val($("#partyRoleTypesApplyDefault").val());
				}
				var contentMessages = document.getElementById('content-messages');
				if (contentMessages != undefined) contentMessages.parentNode.removeChild(contentMessages);
				if(!$('#createQuotation').jqxValidator('validate')) return false;
				$('#container').empty();
				$("#step-title").html("${uiLabelMap.DAConfirmation}");
				
		        var selectedRowIndexes = $('#jqxgridProd').jqxGrid('selectedrowindexes');
				if (selectedRowIndexes.length <= 0) {
					var message0 = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> <span class='message-content-alert-danger'>${uiLabelMap.DAYouNotYetChooseProduct}!</span>";
					bootbox.dialog(message0, [{
						"label" : "OK",
						"class" : "btn-mini btn-primary width60px",
						}]
					);
					return false;
				}
				dataSelected = new Array();
				$('#container').empty();
				var message = "<i class='fa-times-circle open-sans icon-modal-alert-danger'></i> ";
				message += "<span class='message-content-alert-danger'>${uiLabelMap.DAExistProductHaveNotPriceIs}: ";
				var hasMessage = false;
				var isFirst = true;
				for(var index in selectedRowIndexes) {
					var data = $('#jqxgridProd').jqxGrid('getrowdata', selectedRowIndexes[index]);
					var row = {};
					row["productId"] = data.productId;
					row["productName"] = data.productName;
					var priceToDist = data.priceToDist;
					if (priceToDist == undefined || priceToDist == null || (/^\s*$/.test(priceToDist))) {
						hasMessage = true;
						if (!isFirst) message += ", ";
						message += data.productId;
						isFirst = false;
					} else {
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
				}
				message += "</span>";
				if (hasMessage) {
					bootbox.dialog(message, [{
						"label" : "OK",
						"class" : "btn-mini btn-primary width60px",
						}]
					);
					//.addClass("alert alert-danger");
					return false;
				}
				
				$("#strProductQuotationId").text($("#productQuotationId").val());
				$("#strQuotationName").text($("#quotationName").val());
				$("#strDescription").text($("#description").val());
				$("#strSalesChannel").text($("#salesChannel").val());
				$("#strCurrencyUomId").text($("#currencyUomId").val());
				$("#strFromDate").text($("#fromDate").val());
				$("#strThruDate").text($("#thruDate").val());
				if ($('#advanceInterface').val()) {
					var partyRoleTypesApplyMarketDisplay = "";
					var partyRoleTypesApplyMarketVar = $("#partyRoleTypesApplyMarket").val();
					<#list roleTypes as roleType>
						if ("${roleType.roleTypeId}" == partyRoleTypesApplyMarketVar) {
							<#if roleType.description?exists>partyRoleTypesApplyMarketDisplay += "${StringUtil.wrapString(roleType.get("description", locale))}";<#else>partyRoleTypesApplyMarketDisplay += "${roleType.roleTypeId}";</#if>
						}
					</#list>
					$("#strPartyRoleTypesApplyPriceMarket").html(partyRoleTypesApplyMarketDisplay);
					
					var strPartyIdsApply2 = "";
					var strpartyIdsApply2Var = $("#partyIdsApply2").jqxComboBox('getSelectedItem');
					if (strpartyIdsApply2Var != undefined && strpartyIdsApply2Var != null && strpartyIdsApply2Var != "") {
						strPartyIdsApply2 += strpartyIdsApply2Var.label;
					}
					$("#strPartyIdsApply").html(strPartyIdsApply2);
				}
				
				var partyApply = $("#partyRoleTypesApply2").jqxComboBox('getSelectedItems');
				var strValue = ""; //<ul class=\"unstyled spaced2\" style=\"margin: 0 0 0 15px;\">
				var isFirst = true;
				if (partyApply != undefined && partyApply != null && !(/^\s*$/.test(partyApply))) {
					for (var i = 0; i < partyApply.length; i++) {
						var partyApplyItem = partyApply[i];
						<#list roleTypes as roleType>
							var itemRoleType = "${roleType.roleTypeId}";
							if (itemRoleType == partyApplyItem.value) {
								//strValue += "<li style=\"margin-bottom: 0; margin-top:0\"><i class=\"icon-user green\"></i>";
								if (!isFirst) strValue += ", ";
								<#if roleType.description?exists>strValue += "${StringUtil.wrapString(roleType.get("description", locale))}";<#else>strValue += "${roleType.roleTypeId}";</#if>
								//strValue += "<\/li>";
								isFirst = false;
							}
						</#list>
					}
				}
				<#-- old ace select
				var partyApply = $("#partyRoleTypesApply").val();
				var strValue = ""; //<ul class=\"unstyled spaced2\" style=\"margin: 0 0 0 15px;\">
				var isFirst = true;
				if (Object.prototype.toString.call(partyApply).slice(8, -1) === 'String') {
					<#list roleTypes as roleType>
						var itemRoleType = "${roleType.roleTypeId}";
						if (itemRoleType == partyApply) {
							<#if roleType.description?exists>strValue += "${roleType.description}";<#else>strValue += "${roleType.roleTypeId}";</#if>
						}
					</#list>
				} else {
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
				}
				-->
				
				
				//strValue += ""; <\/ul>
				$("#strPartyRoleTypesApply").html(strValue);
				var sourceSuccessTwo = {
					localdata: dataSelected,
					dataType: "array",
					datafields: ${dataField}
			   	}
				if ("SALES_MT_CHANNEL" == $('#salesChannel').val()) {
					var columnsSuccessTwo = ${columnlist2SuperMarket};
				} else {
					var columnsSuccessTwo = ${columnlist2Dist};
				}
				var dataAdapter = new $.jqx.dataAdapter(sourceSuccessTwo);
                $("#jqxgridProdSelected").jqxGrid({ source: dataAdapter, columns: columnsSuccessTwo });
			} else if ((info.step == 2) && (info.direction == "previous")) {
				alterData = null;
				$("#step-title").html("${uiLabelMap.DACreateQuotationForProduct}");
			}
		}).on('finished', function(e) {
			bootbox.confirm("${uiLabelMap.DAAreYouSureCreate}", function(result) {
				if(result) {
					if (!$('#advanceInterface').val()) {
						// is simple interfact
						$("#partyRoleTypesApply2").jqxComboBox('selectItem',$("#partyRoleTypesApplyDefault").val());
						//$("#partyRoleTypesApply").val($("#partyRoleTypesApplyDefault").val());
					}
					var formSend = document.getElementById('createQuotation');
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
		            
		            var advanceInterface = $("#advanceInterface").val();
		            var hiddenField1 = document.createElement("input");
		            hiddenField1.setAttribute("type", "hidden");
		            hiddenField1.setAttribute("name", "advanceInterface");
		            hiddenField1.setAttribute("value", advanceInterface);
		            formSend.appendChild(hiddenField1);
		            
		            if (!advanceInterface || ("SALES_MT_CHANNEL" == $('#salesChannel').val())) {
		            	$("#partyRoleTypesApplyMarket").val("");
		            }
		            
		            var partyApply = $("#partyRoleTypesApply2").jqxComboBox('getSelectedItems');
					if (partyApply != undefined && partyApply != null && !(/^\s*$/.test(partyApply))) {
						for (var i = 0; i < partyApply.length; i++) {
							var partyApplyItem = partyApply[i];
						 	var hiddenField2 = document.createElement("input");
				            hiddenField2.setAttribute("type", "hidden");
				            hiddenField2.setAttribute("name", "partyRoleTypesApply");
				            hiddenField2.setAttribute("value", partyApplyItem.value);
				            formSend.appendChild(hiddenField2);
						}
					}
					
					var partyIdsApply = $("#partyIdsApply2").jqxComboBox('getSelectedItem');
					if (partyIdsApply != undefined && partyIdsApply != null && !(/^\s*$/.test(partyIdsApply))) {
						var hiddenField2 = document.createElement("input");
			            hiddenField2.setAttribute("type", "hidden");
			            hiddenField2.setAttribute("name", "partyIdsApply");
			            hiddenField2.setAttribute("value", partyIdsApply.value);
			            formSend.appendChild(hiddenField2);
			            
						/*for (var i = 0; i < partyIdsApply.length; i++) {
							var partyIdApplyItem = partyIdsApply[i];
						 	var hiddenField2 = document.createElement("input");
				            hiddenField2.setAttribute("type", "hidden");
				            hiddenField2.setAttribute("name", "partyIdsApply");
				            hiddenField2.setAttribute("value", partyIdApplyItem.value);
				            formSend.appendChild(hiddenField2);
						}*/
					}
					
					if ($("#fromDate").jqxDateTimeInput('getDate') != null) {
						var fromDate = $("#fromDate").jqxDateTimeInput('getDate').getTime();
			            var hiddenField3 = document.createElement("input");
			            hiddenField3.setAttribute("type", "hidden");
			            hiddenField3.setAttribute("name", "fromDate");
			            hiddenField3.setAttribute("value", fromDate);
			            formSend.appendChild(hiddenField3);
					}
					
					if ($("#thruDate").jqxDateTimeInput('getDate') != null) {
						var thruDate = $("#thruDate").jqxDateTimeInput('getDate').getTime();
			            var hiddenField4 = document.createElement("input");
			            hiddenField4.setAttribute("type", "hidden");
			            hiddenField4.setAttribute("name", "thruDate");
			            hiddenField4.setAttribute("value", thruDate);
			            formSend.appendChild(hiddenField4);
					}
		            
				    formSend.submit();
				}
			});
		});
		
		$('#createQuotation').jqxValidator({
        	rules: [
				{input: '#productQuotationId', message: '${uiLabelMap.DAThisFieldMustNotByContainSpecialCharacter}', action: 'blur', rule: 
        			function (input, commit) {
        				var value = $(input).val();
						if(!(/^\s*$/.test(value)) && !(/^[a-zA-Z0-9_]+$/.test(value))){
							return false;
						}
						return true;
					}
				},
        		{input: '#currencyUomId', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
        			function (input, commit) {
						if($('#currencyUomId').val() == null || $('#currencyUomId').val() == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#quotationName', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($('#quotationName').val() == null || $('#quotationName').val() == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#partyRoleTypesApply2', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($("#partyRoleTypesApply2").jqxComboBox('getSelectedItems') == null || $("#partyRoleTypesApply2").jqxComboBox('getSelectedItems') == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#salesChannel', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($('#salesChannel').val() == null || $('#salesChannel').val() == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#fromDate', message: '${uiLabelMap.DAThisFieldIsRequired}', action: 'blur', rule: 
					function (input, commit) {
						if($('#fromDate').jqxDateTimeInput('getDate') == null || $('#fromDate').jqxDateTimeInput('getDate') == ''){
							return false;
						}
						return true;
					}
				},
				{input: '#fromDate', message: '${StringUtil.wrapString(uiLabelMap.DARequiredValueGreaterThanOrEqualToToday)}', action: 'blur', rule: 
					function (input, commit) {
						var now = new Date();
						now.setHours(0,0,0,0);
		        		if(input.jqxDateTimeInput('getDate') < now){
		        			return false;
		        		}
		        		return true;
		    		}
				},
				{input: '#thruDate', message: '${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherFromDate)}', action: 'blur', rule: 
					function (input, commit) {
						var fromDate = $('#fromDate').jqxDateTimeInput('getDate');
		        		if(input.jqxDateTimeInput('getDate') != null && input.jqxDateTimeInput('getDate') != undefined && (input.jqxDateTimeInput('getDate') < fromDate)){
		        			return false;
		        		}
		        		return true;
		    		}
				},
        	]
        });
        
		<#--
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
		$('#createQuotation').validate({
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
					validateToDay:"${StringUtil.wrapString(uiLabelMap.HrolbiusRequiredValueGreatherToDay)}"
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
		-->
	})
</script>
