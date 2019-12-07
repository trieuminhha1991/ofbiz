<#if orderHeader?has_content>
				<h4 class="lighter smaller" style="margin-top: 10px;">
					<#assign orderType = orderHeader.getRelatedOne("OrderType", false)/>
					${orderType?if_exists.get("description", locale)?default(uiLabelMap.OrderOrder)} <i class="fa-angle-right"></i> ${uiLabelMap.DAId}: <a href="<@ofbizUrl>orderViewDis?orderId=${orderId}</@ofbizUrl>" title="<#if orderHeader.orderName?has_content>${orderHeader.orderName?if_exists}</#if>">${orderId}</a> 
            		${externalOrder?if_exists} &nbsp;<a href="<@ofbizUrl>orderpr.pdf?orderId=${orderId}</@ofbizUrl>" target="_blank" data-rel="tooltip" title="${uiLabelMap.DAExportToPDF}" data-placement="bottom"><i class="fa-file-pdf-o"></i></a>
				</h4>
				</#if>
				<script type="text/javascript">
					$('[data-rel=tooltip]').tooltip();
				</script>
				<div class="widget-toolbar no-border">
					<ul class="nav nav-tabs nav-tabs-menu" id="recent-tab">
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
				</div>