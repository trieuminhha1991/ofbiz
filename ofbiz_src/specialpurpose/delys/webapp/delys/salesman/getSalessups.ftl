<select name="salessupID" onchange="ajaxUpdateArea('routeId', 'getRoutes', jQuery('#filtercustomerform').serialize());">
	<option value="">&nbsp</option>
			<#list parameters.salessupList as salessup>
				<option value="${salessup.partyId}">${salessup.groupName?if_exists}</option>
			</#list>
</select>