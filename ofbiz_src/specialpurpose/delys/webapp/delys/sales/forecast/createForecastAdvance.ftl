
<div class="row-fluid">
    <div class="span12 widget-container-span">
        <div class="widget-box transparent">
            <div class="widget-header">
                <h4>${uiLabelMap.DASalesForecast}: 
	            	<#if customTimePeriodId?exists>
	            		<#assign customTimeParent = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : customTimePeriodId}, false)!/>
                		${customTimeParent.periodName?if_exists}
                	</#if>
                	(<a id="percentClick" href="#modal-table" role="button" class="green" data-toggle="modal">0%</a>)
                	&nbsp;&nbsp;&nbsp;
                	<button onClick="javascript:onSubmitFormCreateUpdateSalesForecast()"><i class="icon-save"></i>${uiLabelMap.CommonSave}</button>
                	<button onClick="javascript:window.location.href='createForecastAdvance?customTimePeriodId=${customTimePeriodId?if_exists}';"><i class="fa-refresh open-sans"></i>&nbsp;${uiLabelMap.CommonReset}</button>
                </h4>
                <div class="widget-toolbar no-border">
                    <ul class="nav nav-tabs" id="myTab2">
                        <#list listSalesForecastAndItems as salesForecastPartyItem>
							<li <#if salesForecastPartyItem_index == 0> class="active"</#if> style="margin-top:0; margin-bottom:-1px; padding-top:0 !important; padding-bottom:0 !important">
								<a data-toggle="tab" href="#${salesForecastPartyItem_index}-tab" onClick="javascript:onActiveTab('${salesForecastPartyItem_index}')">${salesForecastPartyItem.internalPartyIds}</a>
							</li>
						</#list>
						<li style="margin-top:0; margin-bottom:-1px; padding-top:0 !important; padding-bottom:0 !important" <#if !listSalesForecastAndItems?exists || !(listSalesForecastAndItems?size > 0)>class="active"</#if>>
							<a id="active-tab-plus" data-toggle="tab" href="#plus-forecast-tab"><i class="fa fa-plus-circle blue"></i></a>
						</li>
                    </ul>
                </div>
            </div>
            <div class="widget-body">
                <div class="widget-main padding-12 no-padding-left no-padding-right">
                	<div id="tab-content2">
					    <div id="tab-pane2">
					    </div>
					</div>
                	<div id="tab-content" class="tab-content padding-4">
                		<#if listSalesForecastAndItems?exists && (listSalesForecastAndItems?size > 0)>
                    		<#list listSalesForecastAndItems as salesForecastPartyItem>
	                    		<#if salesForecastPartyItem_index == 0>
	                    			<input id="tabActiveDefault" type="hidden" value="${salesForecastPartyItem_index}"/>
	                    		</#if>
	                    		<input id="tabActiveInput_${salesForecastPartyItem_index}" type="hidden" value="0"/>
								<div id="${salesForecastPartyItem_index}-tab" class="tab-pane<#if salesForecastPartyItem_index == 0> active</#if>">
								    <form id="formCreateUpdateSalesForecast_${salesForecastPartyItem_index}" name="formCreateUpdateSalesForecast_${salesForecastPartyItem_index}" method="POST" action="<@ofbizUrl>createUpdateForecastAdvance</@ofbizUrl>">    
								        <input name="customTimePeriodId" type="hidden" value="${customTimePeriodId?if_exists}"/>
								        <input name="internalPartyId_${salesForecastPartyItem_index}" type="hidden" value="${salesForecastPartyItem.internalPartyIds?if_exists}"/>
								        <input name="organizationPartyId_${salesForecastPartyItem_index}" type="hidden" value="${salesForecastPartyItem.organizationPartyId?if_exists}"/>
								        <input name="currencyUomId_${salesForecastPartyItem_index}" type="hidden" value="${currencyUomId?if_exists}"/>
								        <table id="sale-forecast" class="table table-striped table-bordered table-hover">
								            <thead>
								                <tr class="sf-product">
								                	<td class="sf-months" colspan="1" rowspan="2" style="text-align:center; text-transform: uppercase;">${uiLabelMap.DAMonth}</td>
								                	<#list listProduct as productItem>
								                		<#assign categoryId = productItem.categoryId!>
								                		<#assign productList = productItem.productList!>
									                    <td <#if categoryId?exists && productList?exists>colspan="${productList?size}" </#if>rowspan="1" style="text-align:center">
									                    	${categoryId?if_exists}
									                    	<#--
									                    	<#if categoryId?exists>
									                    		<#assign categoryHeader = delegator.findOne("ProductCategory", {"productCategoryId" : categoryId}, false)!/>
									                    		${categoryHeader.categoryName?default(categoryHeader.categoryId)}
									                    	</#if>
									                    	-->
								                    	</td>
								                	</#list>
								                	<td class="sf-value" rowspan="2" style="text-align:center">VALUE (1,000d)</td>
								                	<td class="sf-years" colspan="1" rowspan="2" style="text-align:center">NAM</td>
								                </tr>
								                <tr class="sf-product-child">
								                	<#list listProduct as productItem>
								                		<#assign categoryId = productItem.categoryId!>
								                		<#assign productList = productItem.productList!>
								                		<#if productList?exists>
								                		<#list productList as item>
								                			<td style="text-align:center">${item.internalName}</td>
								                		</#list>
								                		</#if>
								                	</#list>
								                </tr>
								            </thead>
								            <tbody>
								            <#list salesForecastPartyItem.forecastAndItems as forecastAndItem>
								            	<#assign forecast = forecastAndItem.forecast>
								            	<#assign forecastItems = forecastAndItem.forecastItems>
								            	<#assign ayoForecastItems = forecastAndItem.ayoForecastItems>
								            	<#assign percentItems = forecastAndItem.percentItems>
								            	
								            	<#assign customTimePeriod = delegator.findOne("CustomTimePeriod", {"customTimePeriodId" : forecastAndItem.forecast.customTimePeriodId}, false)!>
								                <input name="customTimePeriodId_${salesForecastPartyItem_index}_${forecastAndItem_index}" type="hidden" value="${customTimePeriod.customTimePeriodId?if_exists}"/>
								                <tr class="sf-current-year<#if "SALES_QUARTER" == customTimePeriod.periodTypeId> quarter-row<#elseif "SALES_YEAR" == customTimePeriod.periodTypeId> sf-total-row</#if>">
								                    <td colspan="1" rowspan="3" class="sf-month<#if "SALES_QUARTER" == customTimePeriod.periodTypeId> quarter<#elseif "SALES_YEAR" == customTimePeriod.periodTypeId> sf-total</#if>" style="text-align:center !important">
								                    	${customTimePeriod.periodName?if_exists}
								                    </td>
								                    <#list ayoForecastItems as ayoForecastItem>
								                    	<td>
									                    	<#if ayoForecastItem?exists && ayoForecastItem?has_content>
									                    		<#--
									                    		<input id="ayoSalesForecastId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${ayoForecastItem_index}" name="ayoSalesForecastId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${ayoForecastItem_index}" type="hidden" value="${ayoForecastItem.salesForecastId?if_exists}"/>
									                    		<input id="ayoSalesForecastDetailId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${ayoForecastItem_index}" name="ayoSalesForecastDetailId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${ayoForecastItem_index}" type="hidden" value="${ayoForecastItem.salesForecastDetailId?if_exists}"/>
									                    		-->
									                    		<input id="ayoProductId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${ayoForecastItem_index}" name="ayoProductId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${ayoForecastItem_index}" type="hidden" value="${ayoForecastItem.productId?if_exists}"/>
									                    		<input id="ayoForecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${ayoForecastItem_index}" name="ayoForecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${ayoForecastItem_index}" type="hidden" value="${ayoForecastItem.quantity?default(0)}"/>
									        					<div id="ayoForecastDiv_${salesForecastPartyItem_index}_${forecastAndItem_index}_${ayoForecastItem_index}">
									        						${ayoForecastItem.quantity?string(",##0")}
									        					</div>
									        				<#else>
									        					<input id="ayoForecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${ayoForecastItem_index}" name="ayoForecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${ayoForecastItem_index}" type="hidden" value="0"/>
									        					<div id="ayoForecastDiv_${salesForecastPartyItem_index}_${forecastAndItem_index}_${ayoForecastItem_index}">-</div>
									        				</#if>
								                    	</td>
								                    </#list>
										        	<td>#</td>
										        	<td style="text-align:center">${forecastAndItem.ayoYear?if_exists}</td>
								                </tr>
								                <tr class="sf-percent<#if "SALES_QUARTER" == customTimePeriod.periodTypeId> quarter-row<#elseif "SALES_YEAR" == customTimePeriod.periodTypeId> sf-total-row</#if>">
								                	<#list percentItems as percentItem>
								                		<td>
									                    	<#if percentItem?exists && percentItem?has_content>
									                    		<input id="percentInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${percentItem_index}" type="hidden" value="${percentItem?default(0)}"/>
									                    		<div id="percentDiv_${salesForecastPartyItem_index}_${forecastAndItem_index}_${percentItem_index}">
									        						${percentItem}%
									        					</div>
									        				<#else>
									        					<input id="percentInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${percentItem_index}" type="hidden" value="0"/>
									                    		<div id="percentDiv_${salesForecastPartyItem_index}_${forecastAndItem_index}_${percentItem_index}">-</div>
									        				</#if>
								                    	</td>
								                	</#list>
				                                    <td>#</td>
				                                    <td style="text-align:center">%</td>
				                                </tr>
				                                <#if "SALES_QUARTER" == customTimePeriod.periodTypeId>
				                                	<tr class="sf-adjusted quarter-row">
					                                	<#list forecastItems as forecastItem>
					                                		<td>
										                    	<#if forecastItem?exists && forecastItem?has_content>
										                    		<#--
										                    		<input id="forecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecastItem.quantity?default(0)}"/>
										                    		-->
										                    		<input id="salesForecastId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="salesForecastId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecast.salesForecastId?if_exists}"/>
										                    		<input id="salesForecastDetailId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="salesForecastDetailId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecastItem.salesForecastDetailId?if_exists}"/>
										                    		<input id="productId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="productId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecastItem.productId?if_exists}"/>
										                    		<input id="quantity_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="quantity_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecastItem.quantity?if_exists}"/>
										                    		<div id="forecastDiv_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}">
										        						${forecastItem.quantity?string(",##0")}
										        					</div>
										        				<#else>
										        					<input id="forecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="forecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="0"/>
									                    			<div id="forecastDiv_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}">0</div>
										        				</#if>
										        				</div>
									                    	</td>
					                                	</#list>
					                                    <td>#</td>
					                                    <td style="text-align:center">${forecastAndItem.year?if_exists}</td>
					                                </tr>
				                                <#elseif "SALES_YEAR" == customTimePeriod.periodTypeId>
				                                	<tr class="sf-adjusted sf-total-row">
					                                	<#list forecastItems as forecastItem>
					                                		<td>
					                                			<#if forecastItem?exists && forecastItem?has_content>
										        					<#--
										        					<input id="forecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="forecastItem.quantity?default(0)"/>
										        					-->
									                    			<input id="salesForecastId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="salesForecastId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecast.salesForecastId?if_exists}"/>
										                    		<input id="salesForecastDetailId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="salesForecastDetailId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecastItem.salesForecastDetailId?if_exists}"/>
										                    		<input id="productId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="productId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecastItem.productId?if_exists}"/>
										                    		<input id="quantity_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="quantity_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecastItem.quantity?if_exists}"/>
										                    		<div id="forecastDiv_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}">
										        						${forecastItem.quantity?string(",##0")}
										        					</div>
										        				<#else>
										        					<input id="forecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="forecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="0"/>
									                    			<div id="forecastDiv_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}">0</div>
										        				</#if>
										        				</div>
									                    	</td>
					                                	</#list>
					                                    <td>#</td>
					                                    <td style="text-align:center">${forecastAndItem.year?if_exists}</td>
					                                </tr>
				                                <#else>
				                                	<tr class="sf-adjusted">
					                                	<#list forecastItems as forecastItem>
					                                		<td>
					                                			<input id="salesForecastId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="salesForecastId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecast.salesForecastId?if_exists}"/>
									                    		<input id="salesForecastDetailId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="salesForecastDetailId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecastItem.salesForecastDetailId?if_exists}"/>
									                    		<input id="productId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="productId_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecastItem.productId?if_exists}"/>
									                    		<input id="quantity_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="quantity_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="hidden" value="${forecastItem.quantity?if_exists}"/>
										                    	<#if forecastItem?exists && forecastItem?has_content>
										        					<input id="forecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="forecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="text" value="${forecastItem.quantity}"/>
										        				<#else>
										        					<input id="forecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" name="forecastInput_${salesForecastPartyItem_index}_${forecastAndItem_index}_${forecastItem_index}" type="text"/>
										        				</#if>
									                    	</td>
					                                	</#list>
					                                    <td>#</td>
					                                    <td style="text-align:center">${forecastAndItem.year?if_exists}</td>
					                                </tr>
				                                </#if>
								         	</#list>
								            </tbody>
								        </table>
									</form>
								</div>
							</#list>
							<div id="plus-forecast-tab" class="tab-pane">
								<form method="POST" action="<@ofbizUrl>createSalesForecastAdvance</@ofbizUrl>" name="createSalesForecast" class="form-horizontal basic-custom-form">
	                    			<#if parameters.customTimePeriodId?exists && listPeriodThisAndChildren?exists && listPeriodThisAndChildren?has_content>
	                    				<#assign firstPeriodMiniId = listPeriodThisAndChildren.get(0)/>
	                    				<input type="hidden" name="customTimePeriodId" value="${firstPeriodMiniId}"/>
	                    			</#if>
	                    			<div class="row">
										<div class="span6">
											<div class="control-group">
												<label class="control-label" for="organizationPartyId">${uiLabelMap.DAFormFieldTitle_organizationPartyId} <span style="color:red">*</span></label>
												<div class="controls">
													<div class="span12">
														<@htmlTemplate.lookupField formName="createSalesForecast" name="organizationPartyId" id="organizationPartyId" fieldFormName="LookupPartyName"/>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label" for="internalPartyId">${uiLabelMap.DAFormFieldTitle_internalPartyId} <span style="color:red">*</span></label>
												<div class="controls">
													<div class="span12">
														<@htmlTemplate.lookupField formName="createSalesForecast" name="internalPartyId" id="internalPartyId" fieldFormName="LookupPartyName"/>
													</div>
												</div>
											</div>
											<div class="control-group">
												<#assign defaultCurrencyUomId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "currency.uom.id.default")>
												<label class="control-label" for="currencyUomId">${uiLabelMap.DACurrencyUomId} <span style="color:red">*</span></label>
												<div class="controls">
													<div class="span12">
														<select name="currencyUomId" id="currencyUomId">
											              	<option value=""></option>
											              	<#list currencies as currency>
											              	<option value="${currency.uomId}" <#if defaultCurrencyUomId?default('') == currency.uomId>selected="selected"</#if>>
											              		${currency.uomId}
										              		</option>
											              	</#list>
											            </select>
													</div>
												</div>
											</div>
											<#if !(parameters.customTimePeriodId?exists && listPeriodThisAndChildren?exists && listPeriodThisAndChildren?has_content)>
			                    				<div class="control-group">
													<label class="control-label" for="customTimePeriodId">${uiLabelMap.DAFormFieldTitle_customTimePeriodId} <span style="color:red">*</span></label>
													<div class="controls">
														<div class="span12">
															<select name="customTimePeriodId" id="customTimePeriodId">
												              	<option value=""></option>
												              	<#list customTimePeriods as period>
												              	<option value="${period.customTimePeriodId}">
												              		${period.periodName}
											              		</option>
												              	</#list>
												            </select>
														</div>
													</div>
												</div>
			                    			</#if>
											<div class="control-group">
												<label class="control-label"></label>
												<div class="controls">
													<div class="span12">
														<button type="submit" class="btn btn-small btn-primary"><i class="icon-ok open-sans"></i>${uiLabelMap.CommonSubmit}</button>
													</div>
												</div>
											</div>
										</div>
									</div>
	                    		</form>
							</div>
						<#else>
	                    	<div id="plus-forecast-tab" class="tab-pane<#if !listSalesForecastAndItems?exists || !(listSalesForecastAndItems?size > 0)> active</#if>">
								<form method="POST" action="<@ofbizUrl>createSalesForecastAdvance</@ofbizUrl>" name="createSalesForecast" class="form-horizontal basic-custom-form">
	                    			<#if parameters.customTimePeriodId?exists && listPeriodThisAndChildren?exists && listPeriodThisAndChildren?has_content>
	                    				<#assign firstPeriodMiniId = listPeriodThisAndChildren.get(0)/>
	                    				<input type="hidden" name="customTimePeriodId" value="${firstPeriodMiniId}"/>
	                    			</#if>
	                    			<div class="row">
										<div class="span6">
											<div class="control-group">
												<label class="control-label" for="organizationPartyId">${uiLabelMap.DAFormFieldTitle_organizationPartyId} <span style="color:red">*</span></label>
												<div class="controls">
													<div class="span12">
														<@htmlTemplate.lookupField formName="createSalesForecast" name="organizationPartyId" id="organizationPartyId" fieldFormName="LookupPartyName"/>
													</div>
												</div>
											</div>
											<div class="control-group">
												<label class="control-label" for="internalPartyId">${uiLabelMap.DAFormFieldTitle_internalPartyId} <span style="color:red">*</span></label>
												<div class="controls">
													<div class="span12">
														<@htmlTemplate.lookupField formName="createSalesForecast" name="internalPartyId" id="internalPartyId" fieldFormName="LookupPartyName"/>
													</div>
												</div>
											</div>
											<div class="control-group">
												<#assign defaultCurrencyUomId = Static["org.ofbiz.base.util.UtilProperties"].getPropertyValue("general.properties", "currency.uom.id.default")>
												<label class="control-label" for="currencyUomId">${uiLabelMap.DACurrencyUomId} <span style="color:red">*</span></label>
												<div class="controls">
													<div class="span12">
														<select name="currencyUomId" id="currencyUomId">
											              	<option value=""></option>
											              	<#list currencies as currency>
											              	<option value="${currency.uomId}" <#if defaultCurrencyUomId?default('') == currency.uomId>selected="selected"</#if>>
											              		${currency.uomId}
										              		</option>
											              	</#list>
											            </select>
													</div>
												</div>
											</div>
											<#if !(parameters.customTimePeriodId?exists && listPeriodThisAndChildren?exists && listPeriodThisAndChildren?has_content)>
			                    				<div class="control-group">
													<label class="control-label" for="customTimePeriodId">${uiLabelMap.DAFormFieldTitle_customTimePeriodId} <span style="color:red">*</span></label>
													<div class="controls">
														<div class="span12">
															<select name="customTimePeriodId" id="customTimePeriodId">
												              	<option value=""></option>
												              	<#list customTimePeriods as period>
												              	<option value="${period.customTimePeriodId}">
												              		${period.periodName}
											              		</option>
												              	</#list>
												            </select>
														</div>
													</div>
												</div>
			                    			</#if>
											<div class="control-group">
												<label class="control-label"></label>
												<div class="controls">
													<div class="span12">
														<button type="submit" class="btn btn-small btn-primary"><i class="icon-ok open-sans"></i>${uiLabelMap.CommonSubmit}</button>
													</div>
												</div>
											</div>
										</div>
									</div>
	                    		</form>
							</div>
	                    </#if>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="modal-table" class="modal hide fade" tabindex="-1">
	<div class="modal-header no-padding">
		<div class="table-header">
			<button type="button" class="close" data-dismiss="modal">&times;</button>
			Set percent for all Sales forecast items
		</div>
	</div>

	<div class="modal-body no-padding">
		<div class="row-fluid">
			<div class="form-horizontal basic-custom-form form-small">
				<div class="control-group">
					<label class="control-label" for="percentNumber">${uiLabelMap.DAPercent}</label>
					<div class="controls">
						<div class="span12">
							<input type="text" size="30" name="percentNumber" id="percentNumber" />
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="modal-footer">
		<button class="btn btn-small btn-danger pull-left" data-dismiss="modal">
			<i class="icon-remove"></i>
			Close
		</button>
		<div class="pagination pull-right no-margin">
			<button class="btn btn-small btn-primary pull-left" onClick="javascript:onResetPercent();">
				<i class="icon-ok"></i>
				Ok
			</button>
		</div>
	</div>
</div>
<style type="text/css">
	#tab-content2{
		border: none 0px red; 
		overflow-x: scroll; 
		overflow-y:hidden;
		height: 20px;
		position:fixed;
		bottom:0;
		right:20px;
		z-index:500;
		opacity: 0.5;
	}
	#tab-content2:hover{
		opacity: 1;
	}
	#tab-content{overflow:hidden}
	#tab-pane2{ height: 20px;}
</style>
<script type="text/javascript" src="/delys/images/js/bootbox.min.js"></script>
<script type="text/javascript" src="/delys/images/js/NumberFormat.js"></script>
<script type="text/javascript">
	var listForecastInput = [];
	var body = $("html, body");
	$(document).ready(function(){
		init();
		$(window).resize(function(){
			init();
		});
		$('#modal-table').on('show.bs.modal', function (e) {
		  	setTimeout(function() {$("#percentNumber").focus()}, 1000);
		});
		$('#percentNumber').live('keyup',function(e){
	     	var p = e.which;
	     	if(p==13){
	         	onResetPercent();
	     	}
	 	});
	 	$('input[id^="forecastInput_"]').live('keyup',function(e){
	     	var p = e.which;
	     	if(p==13){
	     		var element = $("#" + e.currentTarget.id);
	     		if (element != null) {
	     			updateThisInputs(element);
	         		focusForecastInputBelow(element);
	     		}
	     	}
	 	});
	 	$('input[id^="forecastInput_"]').blur(function(){
	 		updateThisInputs($(this));
	 	});
	});
	function focusForecastInputBelow(thisElement) {
		var idElementProcess = thisElement.attr("id");
		var idElementProcessSplit = idElementProcess.split("_");
		var idElementProcessParseInt = parseInt(idElementProcessSplit[2]);
		idElementProcessSplit[2] = idElementProcessParseInt + 1;
		var idElementNext = idElementProcessSplit.join("_");
		//var loop = true;
		if ($("#" + idElementNext).length > 0) {
			$("#" + idElementNext).select();
			//$("#" + idElementNext).focus();
		}
		if ($("#" + idElementNext).length <= 0) {
			idElementProcessParseInt = parseInt(idElementProcessSplit[2]);
			idElementProcessSplit[2] = idElementProcessParseInt + 1;
			idElementNext = idElementProcessSplit.join("_");
			if ($("#" + idElementNext).length > 0) {
				$("#" + idElementNext).select();
				//$("#" + idElementNext).focus();
			}
		}
		if ($("#" + idElementNext).length <= 0) {
			idElementProcessParseInt = parseInt(idElementProcessSplit[3]);
			idElementProcessSplit[2] = 0;
			idElementProcessSplit[3] = idElementProcessParseInt + 1;
			idElementNext = idElementProcessSplit.join("_");
			if ($("#" + idElementNext).length > 0) {
				$("#" + idElementNext).select();
				//$("#" + idElementNext).focus();
			}
		}
		//var currentScroll = $(document).scrollTop();
        //body.animate({scrollTop: currentScroll + 100}, '500', 'swing');
		//var inputs = $(this).closest('form').find(':input');
  		//listForecastInput.eq(listForecastInput.index(thisElement)+1).focus();
	}
	function updateThisInputs(thisElement) {
		var valueInputChange = thisElement.val();
 		var idInputChange = thisElement.attr("id");
 		var subIdInputChange = idInputChange.substring(13, idInputChange.length);
		var ayoForecastInputProcess = parseFloat($("#ayoForecastInput" + subIdInputChange).val());
		var valueInputChangeFloat = parseFloat(valueInputChange);
		if (isNaN(valueInputChangeFloat)) {
			valueInputChangeFloat = 0;
		}
		if (!isNaN(valueInputChangeFloat) && !isNaN(ayoForecastInputProcess) && ayoForecastInputProcess > 0) {
			var percentInputProcess = (valueInputChangeFloat / ayoForecastInputProcess) * 100;
			percentInputProcess = Number((percentInputProcess).toFixed(2));
			$("#percentInput" + subIdInputChange).val(percentInputProcess);
			$("#percentDiv" + subIdInputChange).html(FormatNumberBy3(percentInputProcess, ",", ".") + "%");
		}
	}
	function init() {
		var width = $("#tab-content").width();
		$("#tab-pane2").css("width", $("#sale-forecast").width() + "px");
		$("#tab-content2").css("width", "40%");
		$("#tab-content2").scroll(function(){
        	$("#tab-content").scrollLeft($("#tab-content2").scrollLeft());
	    });
	    $("#tab-content").scroll(function(){
	        $("#tab-content2").scrollLeft($("#tab-content").scrollLeft());
	    });
	}
	function onResetPercent() {
		$("#modal-table").modal("hide");
		bootbox.confirm("${uiLabelMap.DAThisChangeWillApplyForSalesForesastTableInThisScreen}", function(result){
			if(result){
				resetPercent();
			} else {
				$("#modal-table").modal("show");
			}
		});
	}
	function resetPercent() {
		var percentNumberVal = $("#percentNumber").val();
		var tabActiveCurrent = $("#tabActiveDefault").val();
		if (percentNumberVal == null || percentNumberVal == "") {
			percentNumberVal = 0;
		}
		$("#percentClick").text(percentNumberVal + "%");
		$("#tabActiveInput_" + tabActiveCurrent).val(percentNumberVal);
		$('div[id^="percentDiv_' + tabActiveCurrent + '"]').html(percentNumberVal + "%");
		$('input[id^="percentInput_' + tabActiveCurrent + '"]').val(percentNumberVal);
		//var listForecast = $('div[id^="forecastInput_"]');
		
		//console.log(listForecastInput.length);
		var listForecastInput = $('input[id^="forecastInput_"]');
		for (var i = 0; i < listForecastInput.length; i++) {
			var objectProcess = listForecastInput[i];
			var idObjectProcess = $(objectProcess).attr("id");
			var subIdProcess = idObjectProcess.substring(13, idObjectProcess.length);
			var ayoForecastInputProcess = parseFloat($("#ayoForecastInput" + subIdProcess).val());
			var percentInputProcess = parseFloat($("#percentInput" + subIdProcess).val());
			if (isNaN(ayoForecastInputProcess)) {ayoForecastInputProcess = 0;}
			if (isNaN(percentInputProcess)) {percentInputProcess = 0;}
			var valueAfterCaculate = (ayoForecastInputProcess * percentInputProcess / 100);
			$("#forecastInput" + subIdProcess).val(valueAfterCaculate);
			<#if locale == "vi">
				$("#forecastDiv" + subIdProcess).html(FormatNumberBy3(valueAfterCaculate, ",", "."));
			<#else>
				$("#forecastDiv" + subIdProcess).html(valueAfterCaculate);
			</#if>
		}
	}
	function onActiveTab(text) {
		$("#tabActiveDefault").val(text);
		$("#percentClick").text($("#tabActiveInput_" + text).val() + "%");
		$('input[id^="forecastInput_' + text + '_"]').each(function(){
			if (listForecastInput.indexOf($(this).attr("id")) == -1) {
				listForecastInput.push($(this).attr("id"));
			}
			//$(this).text()
		});
	}
	function onSubmitFormCreateUpdateSalesForecast() {
		var tabActiveCurrent = $("#tabActiveDefault").val();
		$("#formCreateUpdateSalesForecast_" + tabActiveCurrent).submit();
	}
</script>