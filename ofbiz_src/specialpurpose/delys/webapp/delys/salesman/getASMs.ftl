 <select name="asmID" onchange="ajaxUpdateArea('SalessupId', 'getSalessups', jQuery('#filtercustomerform').serialize());">
	<option value="">&nbsp</option>
			<#list parameters.asmList as asm>
				<option value="${asm.partyId}">${asm.groupName?if_exists}</option>
			</#list>
</select>