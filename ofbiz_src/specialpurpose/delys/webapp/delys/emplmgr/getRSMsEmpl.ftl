 <select name="rsmId" onchange="ajaxUpdateArea('asmId', 'getASMsEmpl', jQuery('#filterEmpl').serialize());">
 	<option value="NA"></option>
	<#list parameters.listRSMsEmpl as rsm>
		<option value="${rsm.partyIdTo}">${rsm.groupName?if_exists}</option>
	</#list>
</select>