<div class="widget-box olbius-extra ">
	<div class="widget-header widget-header-small header-color-blue2">
		<h6>${uiLabelMap.DACreateNewOrder} --- ${uiLabelMap.DAStepOne}: ${uiLabelMap.DAInputInfoOrder}</h6>
        <div class="widget-toolbar">
        	<a href="#" data-action="collapse"><i class="icon-chevron-up"></i></a>
        </div>
	</div>
	
    <div class="widget-body">
    	<div class="widget-body-inner">
   			<div class="widget-main">
   				<#assign currencies = delegator.findByAnd('Uom', {'uomTypeId', 'CURRENCY_MEASURE'}, null, true) />
				<#assign cart = Static["org.ofbiz.order.shoppingcart.ShoppingCartEvents"].getCartObject(request) />
   				<form action="<@ofbizUrl>dainitorderentry</@ofbizUrl>" name="salesCommonEntryForm" method="post" class="form-horizontal" >
					<input type='hidden' name='finalizeMode' value='type'/>
      				<input type='hidden' name='orderMode' value='SALES_ORDER'/>
					<div class="span5">
						
						<div class="control-group">
							<label class="control-label" for="orderId">${uiLabelMap.DAProductStore}:</label>
							<div class="controls">
								<#--<select name="productStoreId"<#if sessionAttributes.orderMode?exists> disabled</#if>>-->
				                <select name="productStoreId">
					                <#assign currentStore = shoppingCartProductStore>
					                <#if defaultProductStore?has_content>
					                   <option value="${defaultProductStore.productStoreId}">${defaultProductStore.storeName?if_exists}</option>
					                   <option value="${defaultProductStore.productStoreId}">----</option>
					                </#if>
					                <#list productStores as productStore>
					                  	<option value="${productStore.productStoreId}"<#if productStore.productStoreId == currentStore> selected="selected"</#if>>${productStore.storeName?if_exists}</option>
					                </#list>
				              	</select>
				              	<#--<#if sessionAttributes.orderMode?exists>${uiLabelMap.OrderCannotBeChanged}</#if>-->
							</div>
						</div>
						
						
						
						<div class="control-group">
							<label class="control-label" for="orderId">${uiLabelMap.DAOrderId}:</label>
							<div class="controls">
								<div>
									<input type="text" name="orderId" id="orderId" value="${cart.getOrderId()?if_exists}" size="25">
								</div>
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label" for="orderName">${uiLabelMap.DAOrderName}:</label>
							<div class="controls">
								<div>
									<input type="text" name="orderName" id="orderName" value="${cart.getOrderName()?if_exists}" size="25">
								</div>
							</div>
						</div>
		
						<div class="control-group">
							<label class="control-label" for="currencyUomId">${uiLabelMap.DACurrency}:</label>
							<div class="controls">
								
								<#if cart?exists>
									<#assign currencyUomId = cart.getCurrency() />
								<#else>
									<#assign currencyUomId = "" />
								</#if>
								<select id="currencyUomId" name="currencyUomId" class="select2" data-placeholder="VND">
									<option value=""></option>
									<#list currencies as currency>
									<option value="${currency.uomId}" <#if currencyUomId?default('') == currency.uomId>selected="selected"</#if>>
										${currency.uomId}</option>
									</#list>
								</select>
								
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label" for="createOrderDate_i18n">${uiLabelMap.DACreateOrderDate}:</label>
							<div class="controls">
								
								<#assign orderDate = cart.getOrderDate()?if_exists>
								<#if orderDate?exists && orderDate?has_content>
								<#else>
									<#assign orderDate = .now?date?string("yyyy-MM-dd HH:mm:ss.SSS")>
								</#if>
								<@htmlTemplate.renderDateTimeField name="createOrderDate" id="createOrderDate" event="" action="" 
									value="${orderDate?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" 
									maxlength="30" dateType="date" shortDateInput=false timeDropdownParamName="" 
									defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" 
									hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" 
									compositeType="" formName=""/>
								<div>
									<input type="checkbox" name="useAsDefaultDesiredDeliveryDate" value="true">
									<span class="lbl" style="color:#000"> ${uiLabelMap.DASetDefaultValueForOrder}</span>
								</div>
							</div>
						</div>
						
						<div class="control-group" style="width:100%">
							<label class="control-label" for="shipInDate_i18n">${uiLabelMap.DADesiredDeliveryDate}:</label>
							<div class="controls">
								<@htmlTemplate.renderDateTimeField name="shipInDate" id="shipInDate" event="" action="" 
									value="${cart.getShipBeforeDate()?if_exists}" className="" alert="" title="Format: yyyy-MM-dd HH:mm:ss.SSS" size="25" 
									maxlength="30" dateType="date" shortDateInput=false timeDropdownParamName="" 
									defaultDateTimeString="" localizedIconTitle="" timeDropdown="" timeHourName="" classString="" hour1="" 
									hour2="" timeMinutesName="" minutes="" isTwelveHour="" ampmName="" amSelected="" pmSelected="" 
									compositeType="" formName=""/>
								<div>
									<input type="checkbox" name="useAsDefaultDesiredDeliveryDate" value="true">
									<span class="lbl" style="color:#000"> ${uiLabelMap.DASetDefaultValueForOrder}</span>
								</div>
							</div>
						</div>
					</div><!-- .span5 -->
					
					<div class="span5">
						<div class="control-group">
							<label class="control-label" for="userLoginId">${uiLabelMap.DAPersonCreate} :</label>
							<div class="controls">
								<input type="hidden" name="userLoginId" id="userLoginId" value="${parameters.userLogin.userLoginId}" />
								<input type="text" value="${parameters.userLogin.userLoginId}" disabled="disabled" />
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label" for="partyId_customer">${uiLabelMap.DADistributor} <span style="color:#F00">*</span>:</label>
							<div class="controls">
								<@htmlTemplate.lookupField value="${cart.getEndUserCustomerPartyId()?if_exists}" formName="salesCommonEntryForm"
									name="partyId_customer" id="partyId_customer" fieldFormName="LookupCustomerName" />
							</div>
						</div>
						
						<div class="control-group">
							<label class="control-label" for="partyId_sup">${uiLabelMap.DASup}:</label>
							<div class="controls">
								<@htmlTemplate.lookupField value="" formName="salesCommonEntryForm"
									name="partyId_sup" id="partyId_sup" fieldFormName="LookupUserLoginAndPartyDetails" />
							</div>
						</div>
					</div><!-- .span5 -->
					<div style="clear:both"></div>
					
					<div class="row-fluid wizard-actions">
						<button class="btn btn-small disabled btn-primary">
							<i class="icon-angle-left"></i> Quay lai</button>
						<button type="submit" class="btn btn-small btn-primary" onclick="window.location='Tao don hang moi - step 2.html'">
							Tiep tuc <i class="icon-angle-right icon-on-right"></i></button>
					</div>
				</form>
   				
   				
   				<script language="javascript">
				    $(document).ready(function() {
				    	
					});
					$(document).ready(function() {
				    	 /*$('input[name^="partyId_customer"]').blur(function() {
				    	   
				    	    if ($('#search-options').is(':visible')){
				    			$('#search-options').hide();
				    		}else{
				    			$('#search-options').show();
				    		}
				    		
				    	    
				    	});*/
					});
				</script>
   				
   				
   				
   				<hr></hr>
				<div class="form-horizontal desc" style="padding-top:10px; padding-bottom:10px">
					<h6 style="font-weight:bold">${uiLabelMap.DADebtDistributor}</h6>
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DANameDistributor}: </label>
						<div class="controls">
							<div class="span6">
								...
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DAAddressDistributor}:</label>
						<div class="controls">
							<div class="span6">
								...
							</div>
						</div>
					</div>
					<div class="control-group">
						<label class="control-label" for="email">${uiLabelMap.DADebt}:</label>
						<div class="controls">
							<div class="span6">
								...
							</div>
						</div>
					</div>
				</div>
				<div style="clear:both"></div>
   				
    		</div><!-- .widget-main -->
    	</div>
    </div><!-- .widget-body -->
</div>