<select name="asmId" onchange="ajaxUpdateArea('routeId', 'getRoutesEmpl', jQuery('#filterEmpl').serialize());">
	<option value="NA"></option>
	<#list parameters.listASMsEmpl as asm>
		<option value="${asm.partyIdTo}">${asm.groupName?if_exists}</option>
	</#list>
</select>