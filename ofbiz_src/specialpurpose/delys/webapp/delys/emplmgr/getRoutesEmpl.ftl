<select name="routeId">
	<option value="NA"></option>
	<#list parameters.listRoutesEmpl as route>
		<option value="${route.partyIdTo}">${route.groupName?if_exists}</option>
	</#list> 
</select> 