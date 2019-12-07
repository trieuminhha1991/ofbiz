
<#assign shoppingCartProductStore = "NA">
<#if shoppingCart?exists>
	<#assign shoppingCartProductStore = shoppingCart.getProductStoreId()?default("NA")>
</#if>
<#if security.hasEntityPermission("ORDERMGR", "_CREATE", session)>
	<div class="widget-header widget-header-blue widget-header-flat">
		<h5><b>${uiLabelMap.DACreateNewOrder}</b> <i class="fa-angle-double-right"></i> ${uiLabelMap.DAStepOne}: ${uiLabelMap.DAInputInfoOrder}</h5>
	</div>
	<div class="widget-body">
		<div class="widget-main">
			<div class="row-fluid">
				<form class="form-horizontal basic-custom-form" id="initOrderEntry" name="initOrderEntry" method="post" action="<@ofbizUrl>initPayPromoOrderEntry</@ofbizUrl>" style="display: block;">
					<input type="hidden" name="salesChannelEnumId" value="WEB_SALES_CHANNEL"/>
					<input type="hidden" name="originOrderId" value="${parameters.originOrderId?if_exists}"/>
			      	<input type="hidden" name="finalizeMode" value="type"/>
			      	<input type="hidden" name="orderMode" value="SALES_ORDER"/>
					<div class="row">
						<div class="span6">
							<div class="control-group">
								<label class="control-label" for="orderId">${uiLabelMap.DAOrderId}:</label>
								<div class="controls">
									<div class="span12">
										<input type="text" name="orderId" id="orderId" class="span12" value="${parameters.orderId?if_exists}">
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="orderName">${uiLabelMap.DAOrderName}:</label>
								<div class="controls">
									<div class="span12">
										<input type="text" name="orderName" id="orderName" class="span12" value="${parameters.orderName?if_exists}">
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="productStoreId">${uiLabelMap.DAProductStore}:</label>
								<div class="controls">
									<div class="span12">
										<select name="productStoreId" id="productStoreId" class="span12" <#if sessionAttributes.orderMode?exists> disabled</#if>>
											<#assign currentStore = shoppingCartProductStore>
					                		<#if defaultProductStore?has_content>
						                   		<option value="${defaultProductStore.productStoreId}">${defaultProductStore.storeName?if_exists}</option>
						                   		<option value="${defaultProductStore.productStoreId}">----</option>
						                	</#if>
							                <#list productStores as productStore>
						                  		<option value="${productStore.productStoreId}"<#if productStore.productStoreId == currentStore> selected="selected"</#if>>${productStore.storeName?if_exists}</option>
						                	</#list>
										</select>
										<#if sessionAttributes.orderMode?exists>
											<span class="help-inline tooltipob">${uiLabelMap.OrderCannotBeChanged}</span>
										</#if>
									</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="correspondingPoId">${uiLabelMap.DAPONumber}:</label>
								<div class="controls">
									<div class="span12">
										<input type="text" name="correspondingPoId" id="correspondingPoId" class="span12" value="${parameters.correspondingPoId?if_exists}"/>
									</div>
								</div>
							</div>
						</div><!-- .span6 -->
						<div class="span6">
							<div class="control-group">
								<label class="control-label" for="currencyUomId">${uiLabelMap.DACurrencyUomId} <span style="color:red">*</span>:</label>
								<div class="controls">
									<div class="span12">
										<select name="currencyUomId" id="currencyUomId">
							              	<#list currencies as currency>
							              		<option value="${currency.uomId}" <#if currencyUomId?default('') == currency.uomId>selected="selected"</#if>>
							              			${currency.uomId}
							              		</option>
							              	</#list>
							            </select>
									</div>
								</div>
							</div>
							<#--
							<div class="control-group">
								<label class="control-label" for="customerId">${uiLabelMap.DAUserLoginId} <span style="color:red">*</span>:</label>
								<div class="controls">
									<div class="span12">
										<@htmlTemplate.lookupField name="userLoginId" id="userLoginId_sales" value="${parameters.userLogin.userLoginId}" 
											formName="initOrderEntry" fieldFormName="LookupUserLoginAndPartyDetails"/>
									</div>
								</div>
							</div>
							-->
							<div class="control-group">
								<label class="control-label" for="partyId">${uiLabelMap.DACustomer} <span style="color:red">*</span>:</label>
								<div class="controls">
									<div class="span12">
										<#--#partyId-->
										<@htmlTemplate.lookupField name="partyId" id="partyId" value='${parameters.partyId?if_exists}' 
											formName="initOrderEntry" fieldFormName="LookupCustomerNameDistributor"/>
									</div>
								</div>
							</div>
							<#--
							<div class="control-group">
								<label class="control-label" for="workEffortId">${uiLabelMap.WorkEffortWorkEffortId}:</label>
								<div class="controls">
									<div class="span12">
										<@htmlTemplate.lookupField name="workEffortId" id="workEffortId" formName="initOrderEntry" fieldFormName="LookupWorkEffort"/>
									</div>
								</div>
							</div>
							-->
							 
					  		<div class="control-group">
								<label class="control-label" for="desiredDeliveryDate">${uiLabelMap.DADesiredDeliveryDate} <span style="color:red">*</span>:</label>
								<div class="controls">
									<div class="span12">
										<@htmlTemplate.renderDateTimeField name="desiredDeliveryDate" id="desiredDeliveryDate" event="" action="" 
											value="${parameters.desiredDeliveryDate?if_exists}" className="" alert="" 
											title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" dateType="date" 
											shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
											timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" 
											isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="" />
	        						</div>
								</div>
							</div>
							
							<#-- 
							<div class="control-group">
								<label class="control-label" for="shipAfterDate">${uiLabelMap.OrderShipAfterDateDefault}:</label>
								<div class="controls">
									<div class="span12">
										<@htmlTemplate.renderDateTimeField name="shipAfterDate" event="" action="" value="" className="" alert="" 
											title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="shipAfterDate1" dateType="date" 
											shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" 
											timeDropdown="" timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" 
											isTwelveHour="" ampmName="" amSelected="" pmSelected="" compositeType="" formName="" />
	        						</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="shipBeforeDate">${uiLabelMap.OrderShipBeforeDateDefault}:</label>
								<div class="controls">
									<div class="span12">
										<@htmlTemplate.renderDateTimeField name="shipBeforeDate" event="" action="" value="" className="" 
											alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" maxlength="30" id="shipBeforeDate1" dateType="date" 
											shortDateInput=false timeDropdownParamName="" defaultDateTimeString="" localizedIconTitle="" timeDropdown="" 
											timeHourName="" classString="" hour1="" hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" 
											amSelected="" pmSelected="" compositeType="" formName=""/>
	        						</div>
								</div>
							</div>
							<div class="control-group">
								<label class="control-label" for="currencyUomId">${uiLabelMap.ProductChooseCatalog}:</label>
								<div class="controls">
									<div class="span12">
										<select name='CURRENT_CATALOG_ID'>
							              	<option value=""></option>
							            </select>
									</div>
								</div>
							</div>
							-->
							
						</div><!-- .span6 -->
					</div>
					
					<hr/>
						
					<div class="row-fluid wizard-actions">
						<button class="btn btn-prev btn-small" disabled="disabled"><i class="icon-arrow-left"></i> Prev</button>
						<button class="btn btn-success btn-next btn-small" type="submit" data-last="Finish ">Next <i class="icon-arrow-right icon-on-right"></i></button>
					</div>
				</form>
			</div><!-- .row-fluid -->
		</div>
	</div>
<#else>
<div class="widget-body">	 
	<div class="widget-main">
		<div class="alert alert-info">${uiLabelMap.DAQuotationUpdatePermissionError}</div>
	</div>
</div>
</#if>
