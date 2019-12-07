<select name="routeID">
		<option value="">&nbsp</option>
		<#list parameters.routeList as route>
			<option value="${route.partyId}">${route.groupName?if_exists}</option>
		</#list>
</select>