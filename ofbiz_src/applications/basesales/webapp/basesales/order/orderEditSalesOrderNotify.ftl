<#if editLogAllow?exists && editLogAllow>
	<#if orderHeader?exists>
		<#if orderHeader.statusId?exists>
			<#if orderHeader.statusId == "ORDER_COMPLETED">
				<div class="alert alert-info">
					${uiLabelMap.BSThisOrderWasCompleted}
				</div>
			<#elseif orderHeader.statusId == "ORDER_CANCELLED">
				<div class="alert alert-info">
					${uiLabelMap.BSThisOrderWasCancelled}
				</div>
			</#if>
		</#if>
	</#if>
<#else>
	<div class="alert alert-info">
		<div>${uiLabelMap.BSOrderStatusIs}: ${orderStatusMgs?if_exists}.</div>
		<div>${uiLabelMap.BSCannotEditThisOrder}</div>
	</div>
</#if>