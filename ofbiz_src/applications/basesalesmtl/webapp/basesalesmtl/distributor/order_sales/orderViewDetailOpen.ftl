<#if orderHeader?exists && orderHeader.salesMethodChannelEnumId?exists>
	<#assign salesMethodChannelEnumId = orderHeader.salesMethodChannelEnumId />
</#if>
<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<@jqOlbCoreLib hasDropDownButton=true hasComboBox=true/>
<script>
	var prev = document.referrer;
	if(prev.indexOf("newSalesOrder")){
		localStorage.setItem('createOrderSuccess', true);
	}
</script>
<#include "orderViewDetailOpenScript.ftl"/>
<#assign currentStatusId = orderHeader.statusId?default("")/>
<#assign classStep1 = ""/>
<#assign classStep2 = ""/>
<#assign classStep3 = ""/>
<#assign classStep4 = ""/>
<#if "ORDER_CREATED" == currentStatusId>
	<#assign classStep1 = "complete"/>
	<#assign classStep2 = "active"/>
<#elseif "ORDER_APPROVED" == currentStatusId>
	<#assign classStep1 = "complete"/>
	<#assign classStep2 = "complete"/>
	<#assign classStep3 = "active"/>
<#elseif "ORDER_IN_TRANSIT" == currentStatusId>
	<#assign classStep1 = "complete"/>
	<#assign classStep2 = "complete"/>
	<#assign classStep3 = "complete"/>
	<#assign classStep4 = "active"/>
<#elseif "ORDER_COMPLETED" == currentStatusId>
	<#assign classStep1 = "complete"/>
	<#assign classStep2 = "complete"/>
	<#assign classStep3 = "complete"/>
	<#assign classStep4 = "complete"/>
</#if>
<div class="row-fluid margin-bottom10">
	<div id="fuelux-wizard" class="row-fluid hide" data-target="#step-container">
		<ul class="wizard-steps">
			<li data-target="#step1" class="${classStep1}">
				<span class="step">1</span>
				<span class="title">${uiLabelMap.BSStepCreate}</span>
			</li>

			<li data-target="#step2" class="${classStep2}">
				<span class="step"><i class="fa fa-check-square-o"></i></span>
				<span class="title">${uiLabelMap.BSStepApprove}</span>
			</li>

			<li data-target="#step3" class="${classStep3}">
				<span class="step"><i class="fa fa-truck fa-flip-horizontal"></i></span>
				<span class="title">${uiLabelMap.BSStepInPackingAndShipping}</span>
			</li>

			<li data-target="#step4" class="${classStep4}">
				<span class="step"><i class="fa fa-thumbs-o-up"></i></span>
				<span class="title">${uiLabelMap.BSStepComplete}</span>
			</li>
		</ul>
	</div>
</div>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script type="text/javascript">
	$(function(){
		var $validation = false;
		$('#fuelux-wizard').ace_wizard().on('change' , function(e, info){
			return false;
		}).on('finished', function(e) {
			return false;
		}).on('stepclick', function(e){
			return false;
		});
	});
</script>
<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header" style="border-bottom:none">
				<div style="width:100%; border-bottom: 1px solid #c5d0dc"><#--widget-toolbar no-border-->
					<div class="row-fluid">
						<div class="span10">
							<div class="tabbable">
								<ul class="nav nav-tabs" id="recent-tab"><#-- nav-tabs-menu-->
									<#if orderHeader?has_content>
										<#-- 
										<#if security.hasEntityPermission("DELYS_ORDER", "_APPROVE", session)>
											<li class="active">
												<a data-toggle="tab" href="#orderoverview-tab">${uiLabelMap.BSOverview}</a>
											</li>
											<li>
												<a data-toggle="tab" href="#orderinfo-tab">${uiLabelMap.BSOrderGeneralInfo}</a>
											</li>
										<#else>
											<li class="active">
												<a data-toggle="tab" href="#orderinfo-tab">${uiLabelMap.BSOrderGeneralInfo}</a>
											</li>
										</#if>
										-->
										
										<li<#if !activeTab?exists || activeTab == "" || activeTab == "orderoverview-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#orderoverview-tab">${uiLabelMap.BSOverview}</a>
										</li>
										
										<li<#if activeTab?exists && activeTab == "orderinfo-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#orderinfo-tab">${uiLabelMap.BSOrderGeneralInfo}</a>
										</li>
										
										<#--
										<li<#if activeTab?exists && activeTab == "payment-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#payment-tab">${uiLabelMap.BSPayment}</a>
										</li>
										-->
										
										<#-- TODO
										
										<#if orderTerms?has_content>
										<li>
											<a data-toggle="tab" href="#terms-tab">${uiLabelMap.BSOrderTerms}</a>
										</li>
										</#if>
										
										<li>
											<a data-toggle="tab" href="#payment-tab">${uiLabelMap.BSPayment}</a>
										</li>
										
										<#if security.hasEntityPermission("PROJECTMGR", "_VIEW", session)>
										<li>
											<a data-toggle="tab" href="#projectAssoOrder-tab">Project Asso</a>
										</li>
										</#if>
										-->
										
										<#--<#if displayParty?has_content || orderContactMechValueMaps?has_content>
										<li>
											<a data-toggle="tab" href="#contactinfo-tab">${uiLabelMap.BSContact}</a>
										</li>
										</#if>
										
										<#if !orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL">
											<#if security.hasEntityPermission("SALESORDER_DIS", "_UPDATE", session)>
												<li<#if activeTab?exists && activeTab == "shippinginfo-tab"> class="active"</#if>>
													<a data-toggle="tab" href="#shippinginfo-tab">// #actions-tab
														${uiLabelMap.OrderActions}<#if shipGroups?has_content> - ${uiLabelMap.BSShipment}</#if>
													</a>
												</li>
											</#if>
										</#if>
										-->
										
										<#-- TODO
										<#if salesReps?has_content>
										<li>
											<a data-toggle="tab" href="#salesreps-tab">${uiLabelMap.OrderSalesReps}</a>
										</li>
										</#if>
										
										-->
										<#if security.hasPermission("DELIVERY_VIEW", session)>
											<#if security.hasPermission("LOGISTICS_VIEW", session)>
												<li<#if activeTab?exists && activeTab == "deliveries-tab"> class="active"</#if>>
													<a data-toggle="tab" href="#deliveries-tab">${uiLabelMap.DeliveryNote}</a>
												</li>
											<#elseif hasOlbPermission("MODULE", "DIS_SALESORDER_VIEW", "")>
												<li<#if activeTab?exists && activeTab == "deliverydis-tab"> class="active"</#if>>
													<a data-toggle="tab" href="#deliverydis-tab">${uiLabelMap.DeliveryDoc}</a>
												</li>
											</#if>
										</#if>
										<li<#if activeTab?exists && activeTab == "items-tab"> class="active"</#if>>
											<a data-toggle="tab" href="#items-tab">${uiLabelMap.BSOrderItem}</a>
										</li> 
										<#--
										<#if security.hasEntityPermission("DELYS_ORDER", "_APPROVE", session)>
											<li>
												<a data-toggle="tab" href="#balance-tab">${uiLabelMap.BSBalance}</a>
											</li>
										</#if>
										-->
										
										<#--
										<li>
											<a data-toggle="tab" href="#notes-tab">${uiLabelMap.OrderNotes}</a>
										</li>
										-->
									</#if>
									<#if inProcess?exists>
										<li>
											<a data-toggle="tab" href="#transitions-tab">${uiLabelMap.OrderProcessingTransitions}</a>
										</li>
									</#if>
								</ul>
							</div><!--.tabbable-->
						</div>
						<div class="span2" style="height:34px; text-align:right">
							<#--list buttons action to order-->
								<#if orderHeader.externalId?has_content>
							       <#assign externalOrder = "(" + orderHeader.externalId + ")"/>
							    </#if>
							    <#assign orderType = orderHeader.getRelatedOne("OrderType", false)/>
							    
								<#if isOwnerDistributor>
									<#if orderHeader.orderTypeId == "SALES_ORDER" && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL")>
										<#if ((orderHeader.statusId == "ORDER_APPROVED") || (orderHeader.statusId == "ORDER_SENT"))>
	      									<#if hasOlbPermission("MODULE", "DIS_SALESORDER_QUICKSHIP", "")>
												<a href="javascript:document.totalQuickShipOrder.submit();" data-rel="tooltip" title="${uiLabelMap.DAQuickShipEntireOrder}" data-placement="bottom" class="button-action"><i class="fa-plane open-sans"></i></a>
							              		<form name="totalQuickShipOrder" method="post" action="<@ofbizUrl>quickShipSalesOrderDis</@ofbizUrl>" style="position:absolute;">
									                <input type="hidden" name="orderId" value="${orderId}"/>
								              	</form>
							              	</#if>
						              	</#if>
									</#if>
									<#if currentStatus.statusId == "ORDER_CREATED" || currentStatus.statusId == "ORDER_PROCESSING">
										<#if hasOlbPermission("MODULE", "DIS_SALESORDER_APPROVE", "")>
										<a href="javascript:document.OrderApproveOrder.submit()" data-rel="tooltip" title="${uiLabelMap.BSAccept}" data-placement="bottom" class="button-action"><i class="icon-check-square-o open-sans"></i></a>
					              		<form name="OrderApproveOrder" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>" style="position:absolute;">
							                <input type="hidden" name="statusId" value="ORDER_APPROVED"/>
							                <input type="hidden" name="newStatusId" value="ORDER_APPROVED"/>
							                <input type="hidden" name="setItemStatus" value="Y"/>
							                <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
							                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
							                <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
							                <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
							                <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
							                <input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
						              	</form>
						              	</#if>
						            <#elseif currentStatus.statusId == "ORDER_HOLD">
						            	<#if hasOlbPermission("MODULE", "DIS_SALESORDER_APPROVE", "")>
						            	<a href="javascript:document.OrderApproveOrder.submit()" data-rel="tooltip" title="${uiLabelMap.BSAccept}" data-placement="bottom" class="button-action"><i class="icon-check-square-o open-sans"></i></a>
						              	<form name="OrderApproveOrder" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>" style="position:absolute;">
						                	<input type="hidden" name="statusId" value="ORDER_APPROVED"/>
						                	<input type="hidden" name="setItemStatus" value="Y"/>
						                	<input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
						                	<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
						                	<input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
						                	<input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
						                	<input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
						                	<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
						              	</form>
						              	</#if>
						            <#elseif currentStatus.statusId == "ORDER_APPROVED">
						            	<#if hasOlbPermission("MODULE", "DIS_SALESORDER_HOLD", "")>
										<a href="javascript:document.OrderHold.submit()" data-rel="tooltip" title="${uiLabelMap.BSHoldOrder}" data-placement="bottom" class="button-action"><i class="icon-lock open-sans"></i></a>
						              	<form name="OrderHold" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>" style="position:absolute;">
							                <input type="hidden" name="statusId" value="ORDER_HOLD"/>
							                <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
							                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
							                <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
							                <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
							                <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
							                <input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
						              	</form>
						              	</#if>
					              	<#elseif currentStatus.statusId == "ORDER_COMPLETED">
					              		<#assign cond1 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("orderId", Static["org.ofbiz.entity.condition.EntityOperator"].EQUALS, orderHeader.orderId?if_exists)/>
					              		<#assign cond2 = Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusId", Static["org.ofbiz.entity.condition.EntityOperator"].IN, ["DLV_EXPORTED", "DLV_DELIVERED"])/>
					              		<#assign listCond = []/>
					              		<#assign listCond = listCond + [cond1]/>
					              		<#assign listCond = listCond + [cond2]/>
					              		<#assign deliveriesExportedOrDelivered = delegator.findList("Delivery", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(listCond), null, null, null, false) />
					              		<#if deliveriesExportedOrDelivered?has_content>
					              			<#list deliveriesExportedOrDelivered as dlv>
					              				<a href="deliveryAndExport.pdf?deliveryId=${dlv.deliveryId?if_exists}" data-rel="tooltip" title="${uiLabelMap.DeliveryDoc}" data-placement="bottom" class="button-action"><i class="fa-file-pdf-o open-sans"></i></a>&#32;
											</#list>
										</#if>
									</#if>
									<#if currentStatus.statusId != "ORDER_COMPLETED" && currentStatus.statusId != "ORDER_CANCELLED" && currentStatus.statusId != "ORDER_IN_TRANSIT">
										<#if hasOlbPermission("MODULE", "DIS_SALESORDER_CANCEL", "")>
										<a href="javascript:changeOrderStatus('CANCEL')" data-rel="tooltip" title="${uiLabelMap.BSCancel}" data-placement="bottom" class="button-action"><i class="fa fa-times-circle-o"></i></a>
						              	<form name="OrderCancel" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>" style="position:absolute;">
							                <input type="hidden" name="statusId" value="ORDER_CANCELLED"/>
							                <input type="hidden" name="setItemStatus" value="Y"/>
							                <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
							                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
							                <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
							                <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
							                <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
							                <input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
							                <input type="hidden" name="changeReason" value="" />
						              	</form>
						              	</#if>
						            </#if>
									<#if setOrderCompleteOption>
										<#if hasOlbPermission("MODULE", "DIS_SALESORDER_COMPLETE", "")>
										<a href="javascript:document.OrderCompleteOrder.submit()" data-rel="tooltip" title="${uiLabelMap.BSCompleteOrder}" data-placement="bottom" class="button-action"><i class="fa fa-star"></i></a>
						              	<form name="OrderCompleteOrder" method="post" action="<@ofbizUrl>changeOrderStatusDis</@ofbizUrl>" style="position:absolute;">
							                <input type="hidden" name="statusId" value="ORDER_COMPLETED"/>
							                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
							                <input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
						              	</form>
						              	</#if>
						            </#if>
								<#else>
									<#if currentStatus.statusId != "ORDER_COMPLETED" && currentStatus.statusId != "ORDER_CANCELLED" && currentStatus.statusId != "ORDER_IN_TRANSIT">
										<#if hasOlbPermission("MODULE", "DIS_SALESORDER_CANCEL", "")>
										<a href="javascript:changeOrderStatus('CANCEL')" data-rel="tooltip" title="${uiLabelMap.BSCancel}" data-placement="bottom" class="button-action"><i class="fa fa-times-circle-o"></i></a>
						              	<form name="OrderCancel" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>" style="position:absolute;">
							                <input type="hidden" name="statusId" value="ORDER_CANCELLED"/>
							                <input type="hidden" name="setItemStatus" value="Y"/>
							                <input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
							                <input type="hidden" name="orderId" value="${orderId?if_exists}"/>
							                <input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
							                <input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
							                <input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
							                <input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
							                <input type="hidden" name="changeReason" value="" />
						              	</form>
						              	</#if>
						            </#if>
								</#if>
					            <#-- TODO
								<#if currentStatus.statusId == "ORDER_CREATED">
									<a href="javascript:document.OrderSupApprove.submit()" data-rel="tooltip" title="${uiLabelMap.BSActionApprove}" data-placement="bottom" class="button-action"><i class="icon-check-square-o open-sans"></i></a>
									<form name="OrderSupApprove" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>" style="position: absolute;">
						                <input type="hidden" name="orderId" value="${orderId?if_exists}" />
										<input type="hidden" name="statusId" value="ORDER_SUPAPPROVED" />
										<input type="hidden" name="setItemStatus" value="Y" />
										<input type="hidden" name="changeReason" value="" />
										<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
									</form>
								<#elseif currentStatus.statusId == "ORDER_SUPAPPROVED">
									<a href="javascript:document.OrderSadApprove.submit()" data-rel="tooltip" title="${uiLabelMap.BSActionApprove}" data-placement="bottom" class="button-action"><i class="icon-check-square-o open-sans"></i></a>
									<form name="OrderSadApprove" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>" style="position:absolute;">
						                <input type="hidden" name="orderId" value="${orderId}" />
										<input type="hidden" name="statusId" value="ORDER_SADAPPROVED" />
										<input type="hidden" name="setItemStatus" value="Y" />
										<input type="hidden" name="changeReason" value="" />
										<input type="hidden" name="ntfId" value="${parameters.ntfId?if_exists}">
									</form>
								</#if>
								<#if isChiefAccountant?exists && isChiefAccountant>
						            <#if currentStatus.statusId == "ORDER_PROCESSING" 
						            			|| (currentStatus.statusId == "ORDER_NPPAPPROVED" && salesMethodChannelEnumId?exists && salesMethodChannelEnumId == "SALES_GT_CHANNEL")
						            			|| (currentStatus.statusId == "ORDER_SADAPPROVED" && salesMethodChannelEnumId?exists && salesMethodChannelEnumId != "SALES_GT_CHANNEL")>
										// approve
						            <#elseif currentStatus.statusId == "ORDER_APPROVED">
										// hold
						            <#elseif currentStatus.statusId == "ORDER_HOLD">
						              	// approve
						            </#if>
						            // complete
								</#if>
							<#if orderHeader?has_content>
								<#if security.hasPermission("DELYS_ORDER_UPDATE", session) && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED">
									<#if (isDistributor && isPlacingCustomer && "ORDER_CREATED" == currentStatus.statusId) 
											|| (isSalesSup && "ORDER_CREATED" == currentStatus.statusId)
											|| (isChiefAccountant) 
											|| (isSalesAdmin && ("ORDER_CREATED" == currentStatus.statusId 
																	|| "ORDER_SUPAPPROVED" == currentStatus.statusId 
																	|| "ORDER_NPPAPPROVED" == currentStatus.statusId 
																	|| "ORDER_SADAPPROVED" == currentStatus.statusId))>
										<a href="<@ofbizUrl>editOrderItemsSales?${paramString}</@ofbizUrl>" data-rel="tooltip" title="${uiLabelMap.BSEditStatus}" data-placement="bottom" class="button-action"><i class="icon-edit open-sans"></i></a>
									</#if>
								</#if>
								<a href="<@ofbizUrl>orderpr.pdf?orderId=${orderId}</@ofbizUrl>" target="_blank" data-rel="tooltip" title="${uiLabelMap.BSExportToPDF}" data-placement="bottom" class="button-action"><i class="fa-file-pdf-o"></i></a>
								<a href="javascript:void(0);" id="print-order-btn" data-rel="tooltip" title="${uiLabelMap.BSPrint}" data-placement="bottom" class="button-action"><i class="icon-print open-sans"></i></a>
							</#if>
					    	-->
					    	<#if orderHeader?has_content>
					    		<#if orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED" &&  orderHeader.statusId != "ORDER_IN_TRANSIT">
									<#if hasOlbEntityPermission("SALESORDER", "UPDATE")>
						    		<a href="<@ofbizUrl>editSalesOrder?${paramString}</@ofbizUrl>" data-rel="tooltip" 
						    			title="${uiLabelMap.BSEdit}" data-placement="left" class="button-action">
						    			<i class="icon-edit open-sans"></i>
						    		</a>
						    		</#if>
						    	</#if>
					    		<#if security.hasEntityPermission("ORDER_HOLD", "_UPDATE", session) && orderHeader.statusId == "ORDER_APPROVED" && enablePendingFunction == true>
					    			<a id="holdOrderId" href="javascript:SalesFunctionObj.showEditPendingPopup('${orderId}')" data-rel="tooltip" 
						    			title="${uiLabelMap.Pending}" data-placement="left" class="button-action">
						    			<i class="icon-lock open-sans"></i>
						    		</a>
					    		</#if>
					    		<#if security.hasEntityPermission("ORDER_HOLD", "_UPDATE", session) && orderHeader.statusId == "ORDER_HOLD" && enablePendingFunction == true>
					    			<a id="approveOrderId" href="javascript:SalesFunctionObj.approveHoldOrder('${orderId}')" data-rel="tooltip" 
						    			title="${uiLabelMap.Approve}" data-placement="left" class="button-action">
						    			<i class="icon-check open-sans"></i>
						    		</a>
					    		</#if>
					    		<#assign deliveryExported = delegator.findList("Delivery", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", orderHeader.orderId?if_exists, "statusId", "DLV_EXPORTED")), null, null, null, false) />
 					    		<#assign returnItemByOrder = delegator.findList("ReturnItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", orderHeader.orderId?if_exists)), null, null, null, false) />
					    		<#if security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && (orderHeader.statusId == "ORDER_COMPLETED" || orderHeader.statusId != "ORDER_IN_TRANSIT") && !returnItemByOrder?has_content && deliveryExported?has_content>
 					    			<a id="returnOrderId" href="javascript:SalesFunctionObj.showReturnOrderPopup('${orderId}')" data-rel="tooltip" 
						    			title="${uiLabelMap.ReceiveReturn}" data-placement="left" class="button-action">
						    			<i class="icon-history open-sans"></i>
						    		</a>
					    		</#if>
					    		<#assign deliveryCreated = delegator.findList("Delivery", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", orderHeader.orderId?if_exists)), null, null, null, false) />
					    		<#assign orderItemAssoc = delegator.findList("OrderItemAssoc", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("orderId", orderHeader.orderId?if_exists)), null, null, null, false) />
					    		<#if security.hasEntityPermission("LOGISTICS", "_VIEW", session) && security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && orderHeader.statusId == "ORDER_APPROVED" && !deliveryCreated?has_content && !orderItemAssoc?has_content && shipFromVendor == true>
					    			<a id="goToProviderId" href="javascript:SalesFunctionObj.showChooseProviderPopup('${orderId}')" data-rel="tooltip" 
						    			title="${uiLabelMap.ShipFromProvider}" data-placement="left" class="button-action">
						    			<i class="fa-share open-sans"></i>
						    		</a>
					    		</#if>
					    	</#if>
						</div>
					</div>
					<script type="text/javascript">
						$('[data-rel=tooltip]').tooltip();
					</script>
					<style type="text/css">
						.button-action {
							font-size:18px; padding:0 0 0 8px;
						}
					</style>
				</div>
			</div>
			<#--   
			<div>
				${screens.render("component://baselogistics/widget/DeliveryScreens.xml#SalesDeliveryFunction")}
			</div>
			-->
			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div class="tab-content overflow-visible" style="padding:8px 0">
					