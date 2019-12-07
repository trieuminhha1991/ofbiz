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
												<a data-toggle="tab" href="#orderoverview-tab">${uiLabelMap.DAOverview}</a>
											</li>
											<li>
												<a data-toggle="tab" href="#orderinfo-tab">${uiLabelMap.DAOrderGeneralInfo}</a>
											</li>
										<#else>
											<li class="active">
												<a data-toggle="tab" href="#orderinfo-tab">${uiLabelMap.DAOrderGeneralInfo}</a>
											</li>
										</#if>
										-->
										
										<li class="active">
											<a data-toggle="tab" href="#orderoverview-tab">${uiLabelMap.DAOverview}</a>
										</li>
										<li>
											<a data-toggle="tab" href="#orderinfo-tab">${uiLabelMap.DAOrderGeneralInfo}</a>
										</li>
										
										<#if orderTerms?has_content>
										<li>
											<a data-toggle="tab" href="#terms-tab">${uiLabelMap.DAOrderTerms}</a>
										</li>
										</#if>
										
										<#if security.hasEntityPermission("PROJECTMGR", "_VIEW", session)>
										<li>
											<a data-toggle="tab" href="#projectAssoOrder-tab">Project Asso</a>
										</li>
										</#if>
										
										<#if (security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL")) || (shipGroups?has_content && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL"))>
										<li>
											<#assign hasActions = false/>
											<a data-toggle="tab" href="#shippinginfo-tab">
												<#if (security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL"))>
													<#assign hasActions = true/>
													${uiLabelMap.OrderActions}</#if>
												<#if (shipGroups?has_content && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL"))>
													<#if hasActions> - </#if>${uiLabelMap.DAShipment}</#if></a>
										</li>
										</#if>
										
										<#if salesReps?has_content>
										<li>
											<a data-toggle="tab" href="#salesreps-tab">${uiLabelMap.OrderSalesReps}</a>
										</li>
										</#if>
										
										<li>
											<a data-toggle="tab" href="#items-tab">${uiLabelMap.DAOrderItem}</a>
										</li>
										
										<#--
										<#if security.hasEntityPermission("DELYS_ORDER", "_APPROVE", session)>
											<li>
												<a data-toggle="tab" href="#balance-tab">${uiLabelMap.DABalance}</a>
											</li>
										</#if>
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
							<#--
							icon-remove open-sans btn btn-warning btn-mini tooltip-warning floatLeftTableContent margin-right3
							icon-remove open-sans btn btn-primary btn-mini tooltip-warning floatLeftTableContent margin-right3
							-->
							<#--
							<#if setOrderCompleteOption>
					          	<a class="open-sans btn btn-primary btn-mini floatLeftTableContent margin-right3" href="javascript:document.OrderCompleteOrder.submit()">${uiLabelMap.OrderCompleteOrder}</a>
					          	<form name="OrderCompleteOrder" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>">
					            	<input type="hidden" name="statusId" value="ORDER_COMPLETED"/>
					            	<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
					          	</form>
					        </#if>
				    		<#if currentStatus.statusId != "ORDER_COMPLETED" && currentStatus.statusId != "ORDER_CANCELLED">
					          	<a class="icon-remove open-sans btn btn-warning btn-mini tooltip-warning floatLeftTableContent margin-right3" href="javascript:document.OrderCancel.submit()">${uiLabelMap.OrderCancelOrder}</a>
					          	<form name="OrderCancel" method="post" action="<@ofbizUrl>changeOrderStatusDis/orderViewDis</@ofbizUrl>">
					            	<input type="hidden" name="statusId" value="ORDER_CANCELLED"/>
					            	<input type="hidden" name="setItemStatus" value="Y"/>
					            	<input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
					            	<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
					            	<input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
					            	<input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
					            	<input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
					          	</form>
					        </#if>
					        <#if currentStatus.statusId == "ORDER_CREATED" || currentStatus.statusId == "ORDER_PROCESSING">
					          	<a class="open-sans icon-check-square-o btn btn-primary btn-mini floatLeftTableContent margin-right3" href="javascript:document.OrderApproveOrder.submit()">${uiLabelMap.OrderApproveOrder}</a>
					          	<form name="OrderApproveOrder" method="post" action="<@ofbizUrl>changeOrderStatusDis/orderViewDis</@ofbizUrl>">
					            	<input type="hidden" name="statusId" value="ORDER_APPROVED"/>
					            	<input type="hidden" name="newStatusId" value="ORDER_APPROVED"/>
					            	<input type="hidden" name="setItemStatus" value="Y"/>
					            	<input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
					            	<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
					            	<input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
					            	<input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
					            	<input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
					          	</form>
					        <#elseif currentStatus.statusId == "ORDER_APPROVED">
					          	<a class=" icon-lock btn btn-primary btn-mini floatLeftTableContent open-sans margin-right3" href="javascript:document.OrderHold.submit()">${uiLabelMap.OrderHold}</a>
					          	<form name="OrderHold" method="post" action="<@ofbizUrl>changeOrderStatusDis/orderViewDis</@ofbizUrl>">
					            	<input type="hidden" name="statusId" value="ORDER_HOLD"/>
					            	<input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
					            	<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
					            	<input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
					            	<input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
					            	<input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
					          	</form>
					        <#elseif currentStatus.statusId == "ORDER_HOLD">
					          	<a class="btn btn-primary btn-mini icon-check-square-o open-sans floatLeftTableContent margin-right3" href="javascript:document.OrderApproveOrder.submit()">${uiLabelMap.OrderApproveOrder}</a>
					          	<form name="OrderApproveOrder" method="post" action="<@ofbizUrl>changeOrderStatusDis/orderViewDis</@ofbizUrl>">
					            	<input type="hidden" name="statusId" value="ORDER_APPROVED"/>
					            	<input type="hidden" name="setItemStatus" value="Y"/>
					            	<input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
					            	<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
					            	<input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
					            	<input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
					            	<input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
					          	</form>
					        </#if>
					        <#if currentStatus.statusId == "ORDER_APPROVED" && orderHeader.orderTypeId == "SALES_ORDER">
					          	<a class="icon-print open-sans btn btn-primary btn-mini floatLeftTableContent margin-right3" href="javascript:document.PrintOrderPickSheet.submit()">${uiLabelMap.FormFieldTitle_printPickSheet}</a>
					          	<form name="PrintOrderPickSheet" method="post" action="<@ofbizUrl>orderPickSheet.pdf</@ofbizUrl>" target="_BLANK">
					            	<input type="hidden" name="facilityId" value="${storeFacilityId?if_exists}"/>
					            	<input type="hidden" name="orderId" value="${orderHeader.orderId?if_exists}"/>
					            	<input type="hidden" name="maxNumberOfOrdersToPrint" value="1"/>
					          	</form>
					        </#if>
							-->
							<#if security.hasPermission("DIS_ORDERMGR_UPDATE", session)>
								<#if setOrderCompleteOption>
						          	<a href="javascript:document.OrderCompleteOrder.submit()" data-rel="tooltip" title="${uiLabelMap.OrderCompleteOrder}" data-placement="bottom" class="button-action"><i class="fa fa-star"></i></a>
					              	<form name="OrderCompleteOrder" method="post" action="<@ofbizUrl>changeOrderStatus</@ofbizUrl>" style="position:absolute;">
						            	<input type="hidden" name="statusId" value="ORDER_COMPLETED"/>
						            	<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
						          	</form>
						        </#if>
					    		<#if currentStatus.statusId != "ORDER_COMPLETED" && currentStatus.statusId != "ORDER_CANCELLED">
						          	<a href="javascript:document.OrderCancel.submit()" data-rel="tooltip" title="${uiLabelMap.DACancelStatus}" data-placement="bottom" class="button-action"><i class="fa fa-times-circle-o"></i></a>
					              	<form name="OrderCancel" method="post" action="<@ofbizUrl>changeOrderStatusDis/orderViewDis</@ofbizUrl>" style="position:absolute;">
						            	<input type="hidden" name="statusId" value="ORDER_CANCELLED"/>
						            	<input type="hidden" name="setItemStatus" value="Y"/>
						            	<input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
						            	<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
						            	<input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
						            	<input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
						            	<input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
						          	</form>
						        </#if>
						        <#if currentStatus.statusId == "ORDER_CREATED" || currentStatus.statusId == "ORDER_PROCESSING">
						          	<a href="javascript:document.OrderApproveOrder.submit()" data-rel="tooltip" title="${uiLabelMap.DAActionApprove}" data-placement="bottom" class="button-action"><i class="icon-check-square-o open-sans"></i></a>
				              		<form name="OrderApproveOrder" method="post" action="<@ofbizUrl>changeOrderStatusDis/orderViewDis</@ofbizUrl>" style="position:absolute;">
						            	<input type="hidden" name="statusId" value="ORDER_APPROVED"/>
						            	<input type="hidden" name="newStatusId" value="ORDER_APPROVED"/>
						            	<input type="hidden" name="setItemStatus" value="Y"/>
						            	<input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
						            	<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
						            	<input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
						            	<input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
						            	<input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
						          	</form>
						        <#elseif currentStatus.statusId == "ORDER_APPROVED">
						          	<a href="javascript:document.OrderHold.submit()" data-rel="tooltip" title="${uiLabelMap.DAHoldOrder}" data-placement="bottom" class="button-action"><i class="icon-lock open-sans"></i></a>
					              	<form name="OrderHold" method="post" action="<@ofbizUrl>changeOrderStatusDis/orderViewDis</@ofbizUrl>" style="position:absolute;">
						            	<input type="hidden" name="statusId" value="ORDER_HOLD"/>
						            	<input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
						            	<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
						            	<input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
						            	<input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
						            	<input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
						          	</form>
						        <#elseif currentStatus.statusId == "ORDER_HOLD">
						          	<a href="javascript:document.OrderApproveOrder.submit()" data-rel="tooltip" title="${uiLabelMap.DAActionApprove}" data-placement="bottom" class="button-action"><i class="icon-check-square-o open-sans"></i></a>
				              		<form name="OrderApproveOrder" method="post" action="<@ofbizUrl>changeOrderStatusDis/orderViewDis</@ofbizUrl>" style="position:absolute;">
						            	<input type="hidden" name="statusId" value="ORDER_APPROVED"/>
						            	<input type="hidden" name="setItemStatus" value="Y"/>
						            	<input type="hidden" name="workEffortId" value="${workEffortId?if_exists}"/>
						            	<input type="hidden" name="orderId" value="${orderId?if_exists}"/>
						            	<input type="hidden" name="partyId" value="${assignPartyId?if_exists}"/>
						            	<input type="hidden" name="roleTypeId" value="${assignRoleTypeId?if_exists}"/>
						            	<input type="hidden" name="fromDate" value="${fromDate?if_exists}"/>
						          	</form>
						        </#if>
						    </#if>
		        
							<#if orderHeader?has_content>
								<#if security.hasPermission("DIS_ORDERMGR_UPDATE", session) && orderHeader.statusId != "ORDER_CANCELLED" && orderHeader.statusId != "ORDER_COMPLETED" 
									&& (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL")>
									<a href="<@ofbizUrl>editOrderItemsSales?${paramString}</@ofbizUrl>" data-rel="tooltip" title="${uiLabelMap.DAEditStatus}" data-placement="bottom" class="button-action"><i class="icon-edit open-sans"></i></a>
								</#if>
								<a href="<@ofbizUrl>orderpr.pdf?orderId=${orderId}</@ofbizUrl>" target="_blank" data-rel="tooltip" title="${uiLabelMap.DAExportToPDF}" data-placement="bottom" class="button-action"><i class="fa-file-pdf-o"></i></a>
								<a href="javascript:void(0);" id="print-order-btn" data-rel="tooltip" title="${uiLabelMap.DAPrint}" data-placement="bottom" class="button-action"><i class="icon-print open-sans"></i></a>
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

			<div class="widget-body" style="margin-top: -12px !important">
				<div class="widget-main padding-4">
					<div style="position:relative">
						<div id="info_loader" style="overflow: hidden; position: fixed; display: none; left: 50%; top: 50%; z-index: 900;" class="jqx-rc-all jqx-rc-all-olbius">
							<div style="z-index: 99999; position: relative; width: auto; height: 33px; padding: 5px; font-family: verdana; font-size: 12px; color: #767676; border-color: #898989; border-width: 1px; border-style: solid; background: #f6f6f6; border-collapse: collapse;" class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
								<div style="float: left;">
									<div style="float: left; overflow: hidden; width: 32px; height: 32px;" class="jqx-grid-load"></div>
									<span style="margin-top: 10px; float: left; display: block; margin-left: 5px;">${uiLabelMap.DALoading}...</span>
								</div>
							</div>
						</div>
					</div>
					<div class="tab-content overflow-visible" style="padding:8px 0">
					