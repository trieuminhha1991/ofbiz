<#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL")>
<div id="actions-tab" class="tab-pane">
	<div class="row-fluid">
		<div class="span12">
			<h4 class="smaller green" style="display:inline-block">
				${uiLabelMap.OrderActions}
			</h4>
				<div>
					<ul class="unstyled spaced">
	    				<#if security.hasEntityPermission("FACILITY", "_CREATE", session) && ((orderHeader.statusId == "ORDER_APPROVED") || (orderHeader.statusId == "ORDER_SENT"))>
	      					<#-- Special shipment options -->
	      					<#if orderHeader.orderTypeId == "SALES_ORDER">
	      						<#if isLog>
	        					<li>
						            <form name="quickShipOrder" method="post" action="<@ofbizUrl>quickShipOrderSales</@ofbizUrl>">
						              	<input type="hidden" name="orderId" value="${orderId}"/>
						            </form>
	        						<a href="javascript:document.quickShipOrder.submit()" class="btn btn-mini btn-primary icon-forward open-sans">${uiLabelMap.DAQuickShipEntireOrder}</a>
	    						</li>
	    						</#if>
	      					<#else> <#-- PURCHASE_ORDER -->
	        					<span>&nbsp;<#if orderHeader.orderTypeId == "PURCHASE_ORDER">${uiLabelMap.ProductDestinationFacility}</#if></span>
	        					<#if ownedFacilities?has_content>
	          						<#if !allShipments?has_content>
	          							<#if isLog>
		          							<li>
		                 						<form action="/facility/control/quickShipPurchaseOrder?externalLoginKey=${externalLoginKey}" method="post">
							                       	<input type="hidden" name="initialSelected" value="Y"/>
							                       	<input type="hidden" name="orderId" value="${orderId}"/>
		                   							<#-- destination form (/facility/control/ReceiveInventory) wants purchaseOrderId instead of orderId, so we set it here as a workaround -->
		                   							<input type="hidden" name="purchaseOrderId" value="${orderId}"/>
		                  							<select name="facilityId">
		                    							<#list ownedFacilities as facility>
		                      								<option value="${facility.facilityId}">${facility.facilityName}</option>
		                    							</#list>
		                  							</select>
		                  							<button type="submit" class="btn btn-small btn-primary">
		                  	 							${uiLabelMap.OrderQuickReceivePurchaseOrder}
		              								</button>
		                 						</form>
		              						</li>
						                  	<li>
						                    	<form name="receivePurchaseOrderForm" action="/facility/control/quickShipPurchaseOrder?externalLoginKey=${externalLoginKey}" method="post">
							                      	<input type="hidden" name="initialSelected" value="Y"/>
							                      	<input type="hidden" name="orderId" value="${orderId}"/>
							                      	<input type="hidden" name="purchaseOrderId" value="${orderId}"/>
							                      	<input type="hidden" name="partialReceive" value="Y"/>
							                      	<select name="facilityId">
							                        	<#list ownedFacilities as facility>
							                          		<option value="${facility.facilityId}">${facility.facilityName}</option>
							                        	</#list>
							                      	</select>
					                      		</form>
						                      	<i class="icon-caret-right blue"></i>
						                      	<a href="javascript:document.receivePurchaseOrderForm.submit()" class="btn btn-mini btn-primary">${uiLabelMap.CommonReceive}</a>
						                  	</li>
						            	</#if>
	          						<#else>
	          							<#if isLog>
						                  	<li>
					                    		<form name="receiveInventoryForm" action="/facility/control/ReceiveInventory" method="post">
							                      	<input type="hidden" name="initialSelected" value="Y"/>
							                      	<input type="hidden" name="purchaseOrderId" value="${orderId?if_exists}"/>
							                      	<select name="facilityId">
							                        	<#list ownedFacilities as facility>
							                          		<option value="${facility.facilityId}">${facility.facilityName}</option>
							                        	</#list>
							                      	</select>
						                    	</form>
							                    <i class="icon-caret-right blue"></i>
							                    <a href="javascript:document.receiveInventoryForm.submit()" class="btn btn-mini btn-primary">${uiLabelMap.OrderQuickReceivePurchaseOrder}</a>
						                  	</li>
						                  	<li>
						                    	<form name="partialReceiveInventoryForm" action="/facility/control/ReceiveInventory" method="post">
						                      		<input type="hidden" name="initialSelected" value="Y"/>
						                      		<input type="hidden" name="purchaseOrderId" value="${orderId?if_exists}"/>
						                      		<input type="hidden" name="partialReceive" value="Y"/>
						                      		<select name="facilityId">
						                        		<#list ownedFacilities as facility>
						                           			<option value="${facility.facilityId}">${facility.facilityName}</option>
						                         		</#list>
						                       		</select>
						                    	</form>
						                    	<i class="icon-caret-right blue"></i>
						                    	<a href="javascript:document.partialReceiveInventoryForm.submit()" class="btn btn-mini btn-primary">${uiLabelMap.CommonReceive}</a>
						                  	</li>
					                  	</#if>
	          						</#if>
					              	<#if orderHeader.statusId != "ORDER_COMPLETED">
					              		<#if isLog>
						                  	<li>
							                    <form action="/ordermgr/control/completePurchaseOrder?externalLoginKey=${externalLoginKey}" method="post">
							                     	<input type="hidden" name="orderId" value="${orderId}"/>
						                    		<select name="facilityId">
						                      			<#list ownedFacilities as facility>
						                        			<option value="${facility.facilityId}">${facility.facilityName}</option>
						                      			</#list>
						                    		</select>
						                    		<button type="submit" class="btn btn-small btn-primary">
						                    			${uiLabelMap.OrderForceCompletePurchaseOrder}
						                    		</button>
						                    	</form>
						                  	</li>
						            	</#if>
					              	</#if>
	        					</#if>
	      					</#if>
	    				</#if>
	    				<#-- Refunds/Returns for Sales Orders and Delivery Schedules -->
	    				<#if orderHeader.statusId != "ORDER_COMPLETED" && orderHeader.statusId != "ORDER_CANCELLED">
	    					<#if isLog>
								<li><a href="<@ofbizUrl>orderDeliveryScheduleInfo?orderId=${orderId}</@ofbizUrl>"><i class="icon-eye-open open-sans"></i>${uiLabelMap.OrderViewEditDeliveryScheduleInfo}</a></li>
	    					</#if>
	    				</#if>
	    				<#if security.hasEntityPermission("ORDERMGR", "_RETURN", session) && orderHeader.statusId == "ORDER_COMPLETED">
	      					<#if returnableItems?has_content>
	      						<#if isSalesAdmin>
		        					<li>
							            <form name="quickRefundOrder" method="post" action="<@ofbizUrl>quickRefundOrder</@ofbizUrl>">
							              	<input type="hidden" name="orderId" value="${orderId}"/>
							              	<input type="hidden" name="receiveReturn" value="true"/>
							              	<input type="hidden" name="returnHeaderTypeId" value="${returnHeaderTypeId}"/>
							            </form>
		        						<a href="javascript:document.quickRefundOrder.submit()" class="btn btn-mini btn-primary"><i class="icon-forward open-sans"></i>${uiLabelMap.DAQuickRefundEntireOrder}</a>
		    						</li>
						            <li>
						            	<form name="quickreturn" method="post" action="<@ofbizUrl>quickReturn</@ofbizUrl>">
						              		<input type="hidden" name="orderId" value="${orderId}"/>
						              		<input type="hidden" name="party_id" value="${partyId?if_exists}"/>
					              			<input type="hidden" name="returnHeaderTypeId" value="${returnHeaderTypeId}"/>
						              		<input type="hidden" name="needsInventoryReceive" value="${needsInventoryReceive?default("N")}"/>
						            	</form>
						            	<a href="javascript:document.quickreturn.submit()"><i class="icon-plus-sign open-sans"></i>${uiLabelMap.OrderCreateReturn}</a>
						            </li>
						    	</#if>
	      					</#if>
	    				</#if>
	
	    				<#if orderHeader?has_content && orderHeader.statusId != "ORDER_CANCELLED">
	      					<#if orderHeader.statusId != "ORDER_COMPLETED">
					            <#-- <li><a href="/ordermgr/control/cancelOrderItem?${paramString}" class="buttontext">${uiLabelMap.OrderCancelAllItems}</a></li> 
					            <li><a href="<@ofbizUrl>editOrderItems?${paramString}</@ofbizUrl>" target="_blank"><i class="icon-edit open-sans"></i>${uiLabelMap.DAEditOrderItems}</a></li>-->
					            <#if security.hasPermission("ORDERMGR_UPDATE", session)><#-- target="_blank"-->
					            	<li><a href="<@ofbizUrl>editOrderItemsSales?${paramString}</@ofbizUrl>"><i class="icon-edit open-sans"></i>${uiLabelMap.DAEditOrderItems}</a></li>
					            </#if>
					            <#if isLog>
		        					<li>
							            <form name="createOrderItemShipGroup" method="post" action="<@ofbizUrl>createOrderItemShipGroup</@ofbizUrl>">
							              	<input type="hidden" name="orderId" value="${orderId}"/>
							            </form>
		        						<a href="javascript:document.createOrderItemShipGroup.submit()"><i class="icon-plus-sign open-sans"></i>${uiLabelMap.DACreateShipGroup}</a>
		        					</li>
		        				</#if>
	      					</#if>
	      					<#if isLog>
	      						<li><a href="<@ofbizUrl>loadCartFromOrder</@ofbizUrl>?${paramString}&amp;finalizeMode=init"><i class="icon-plus-sign open-sans"></i>${uiLabelMap.DACreateNewOrder}</a></li>
	      					</#if>
	      					<#if orderHeader.statusId == "ORDER_COMPLETED">
	        					<#if isDistributor || isSalesAdmin>
	        						<li><a href="<@ofbizUrl>loadCartForReplacementOrder</@ofbizUrl>?${paramString}"><i class="icon-plus-sign open-sans"></i>${uiLabelMap.OrderCreateReplacementOrder}</a></li>
	      						</#if>
	      					</#if>
	    				</#if>
	    				<li>
	    					<a href="<@ofbizUrl>orderHistory</@ofbizUrl>?orderId=${orderId}">
	    						<i class="open-sans fa-history"></i>&nbsp;${uiLabelMap.DAViewOrderHistory}
							</a>
							<#--${screens.render("component://delys/widget/sales/OrderScreens.xml#QuickCheckoutAjaxSales")}-->
						</li>
	  				</ul>
			  	</div>
		</div><!--.span12-->
	</div><!--.row-fluid-->
</div><!--#actions-tab-->
<script type="text/javascript">
	$('[data-rel=tooltip]').tooltip();
</script>
</#if>