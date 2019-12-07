<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#-- 
Extend from showcart.ftl file
-->

<script language="JavaScript" type="text/javascript">
    function showQohAtp() {
        document.qohAtpForm.productId.value = document.quickaddform.add_product_id.value;
        document.qohAtpForm.submit();
    }
    <#--
    function quicklookupGiftCertificate() {
        window.location='AddGiftCertificate';
    }
    -->
</script>

<#assign target="getProductInventoryAvailable">

<div class="widget-box olbius-extra">
	<div class="widget-box transparent no-bottom-border">
    <div class="widget-body">
    <div class="widget-body-inner"> 
	<div class="widget-main">
		<div class="row-fluid">
			<div>
	          	<#if quantityOnHandTotal?exists && availableToPromiseTotal?exists && (productId)?exists>
	            	<ul>
	              		<li><label>${uiLabelMap.ProductQuantityOnHand}</label>: ${quantityOnHandTotal}</li>
	              		<li><label>${uiLabelMap.ProductAvailableToPromise}</label>: ${availableToPromiseTotal}</li>
	            	</ul>
	          	</#if>
	        </div>
			<form name="qohAtpForm" method="post" action="<@ofbizUrl>${target}</@ofbizUrl>">
                <input type="hidden" name="facilityId" value="${facilityId?if_exists}"/>
                <input type="hidden" name="productId"/>
                <input type="hidden" id="ownerPartyId" name="ownerPartyId" value="${shoppingCart.getBillToCustomerPartyId()?if_exists}" />
            </form>
			
			<form class="form-horizontal basic-custom-form form-decrease-padding" id="quickaddform" name="quickaddform" method="post" action="<@ofbizUrl>addItem</@ofbizUrl>" style="display: block;">
				<div class="control-group" style="margin-bottom:0 !important">
					<label class="control-label" for="add_product_id" style="text-align:left">${uiLabelMap.ProductProductId}:</label>
					<div class="controls">
						<div class="span12">
							<#--
							<#assign fieldFormName="LookupProduct">
							<@htmlTemplate.lookupField formName="quickaddform" name="add_product_id" id="add_product_id" fieldFormName="${fieldFormName}"/>
							
							<span class="help-inline tooltipob">
								<a href="<@ofbizUrl>LookupBulkAddProductsAndCatalog</@ofbizUrl>" class="btn btn-mini btn-primary" tabindex="-1" style="margin-bottom:5px">
									${uiLabelMap.DAQuickLookupProduct} <i class="fa-plus open-sans icon-on-right"></i>
								</a>
							</span>
							-->
							<span class="help-inline tooltipob">
								<a href="<@ofbizUrl>LookupBulkAddOrderItem</@ofbizUrl>" class="btn btn-mini btn-primary" tabindex="-1" style="margin-bottom:5px">
									${uiLabelMap.DAQuickLookupOrderItem} <i class="fa-plus open-sans icon-on-right"></i>
								</a>
							</span>
	                      	<#--
	                      	<a href="javascript:quicklookup(document.quickaddform.add_product_id)" class="btn btn-mini btn-primary">${uiLabelMap.DAQuickLookup} <i class="fa-plus open-sans icon-on-right"></i></a>
	                      	<a href="javascript:quicklookupGiftCertificate()" class="open-sans btn btn-mini btn-primary">${uiLabelMap.OrderAddGiftCertificate}</a>-->
						</div>
					</div>
				</div>
				<#--
				<div class="control-group" id="quickAddFormQuatity" style="margin-bottom:0 !important">
					<label class="control-label" for="quantity" style="text-align:left">${uiLabelMap.OrderQuantity}:</label>
					<div class="controls">
						<div class="span12">
							<input type="text" size="6" name="quantity" id="quantity" value=""/>
							<span class="help-inline tooltipob">
								<button class="btn btn-primary btn-mini open-sans" type="submit" style="margin-bottom:3px">
									<i class="icon-ok"></i>${uiLabelMap.DAAddToOrder}
								</button>
							</span>
						</div>
					</div>
				</div>
				-->
				<#--
				<div class="control-group">
					<label class="control-label" for="quantity">${uiLabelMap.CommonComment}:</label>
					<div class="controls">
						<div class="span12">
							<input type="text" size="25" name="itemComment" value="${defaultComment?if_exists}" />
	                      	<span class="help-inline tooltipob" style="margin-bottom:0">
	                      		<label style="margin-top:inherit !important; font-size:13px">
									<input type="checkbox" name="useAsDefaultComment" value="true"<#if useAsDefaultComment?exists> checked="checked"</#if> />
									<span class="lbl"> ${uiLabelMap.OrderUseDefaultComment}</span>
						  		</label>
						  	</span>
                    	</div>
					</div>
				</div>
				-->
				
			</form>
		</div><!-- .row-fluid -->
	</div><!-- .widget-main -->
	</div><!-- .widget-body-inner -->
	</div><!-- .widget-body -->
	</div><!-- .widget-box transparent no-bottom-border -->
</div><!-- .widget-box olbius-extra -->

<script language="JavaScript" type="text/javascript">
  document.quickaddform.add_product_id.focus();
</script>
