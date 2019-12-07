<div class="row-fluid">
	<div class="span12">
		<div class="widget-box transparent" id="recent-box">
			<div class="widget-header">
				<#if orderHeader?has_content>
				<h4 class="lighter smaller" style="margin-top: 10px;">
					${uiLabelMap.DAId}: <a href="<@ofbizUrl>orderview?orderId=${orderId}</@ofbizUrl>" title="<#if orderHeader.orderName?has_content>${orderHeader.orderName?if_exists}</#if>">${orderId}</a> 
            		${externalOrder?if_exists} &nbsp;<<<a href="<@ofbizUrl>order.pdf?orderId=${orderId}</@ofbizUrl>" target="_blank">PDF</a>&nbsp;>>
				</h4>
				</#if>
				
				<div class="widget-toolbar no-border">
					<ul class="nav nav-tabs" id="recent-tab">
						<#if orderHeader?has_content>
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
							
							<#if orderTerms?has_content>
							<li>
								<a data-toggle="tab" href="#terms-tab">${uiLabelMap.OrderOrderTerms}</a>
							</li>
							</#if>
							
							<li>
								<a data-toggle="tab" href="#payment-tab">${uiLabelMap.AccountingPaymentInformation}</a>
							</li>
							
							<#if security.hasEntityPermission("PROJECTMGR", "_VIEW", session)>
							<li>
								<a data-toggle="tab" href="#projectAssoOrder-tab">Project Asso</a>
							</li>
							</#if>
							
							<#if displayParty?has_content || orderContactMechValueMaps?has_content>
							<li>
								<a data-toggle="tab" href="#contactinfo-tab">${uiLabelMap.OrderContactInformation}</a>
							</li>
							</#if>
							
							<#if (security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL")) || (shipGroups?has_content && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL"))>
							<li>
								<a data-toggle="tab" href="#shippinginfo-tab">
									<#if (security.hasEntityPermission("ORDERMGR", "_UPDATE", session) && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL"))>${uiLabelMap.OrderActions}</#if>
									<#if (shipGroups?has_content && (!orderHeader.salesChannelEnumId?exists || orderHeader.salesChannelEnumId != "POS_SALES_CHANNEL"))> ${uiLabelMap.OrderShipmentInformation}</#if></a>
							</li>
							</#if>
							
							<#if salesReps?has_content>
							<li>
								<a data-toggle="tab" href="#salesreps-tab">${uiLabelMap.OrderSalesReps}</a>
							</li>
							</#if>
							
							<li>
								<a data-toggle="tab" href="#items-tab">${uiLabelMap.OrderOrderItems}</a>
							</li>
							
							<#if security.hasEntityPermission("DELYS_ORDER", "_APPROVE", session)>
								<li>
									<a data-toggle="tab" href="#balance-tab">${uiLabelMap.DABalance}</a>
								</li>
							</#if>
							
							<li>
								<a data-toggle="tab" href="#notes-tab">${uiLabelMap.OrderNotes}</a>
							</li>
						</#if>
						<#if inProcess?exists>
							<li>
								<a data-toggle="tab" href="#transitions-tab">${uiLabelMap.OrderProcessingTransitions}</a>
							</li>
						</#if>
					</ul>
				</div>
			</div>

			<div class="widget-body">
				<div class="widget-main padding-4">
					<div class="tab-content padding-8 overflow-visible">
					
					
					
					
					
					