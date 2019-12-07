<script type="text/javascript">
jQuery(document).ready( function() {
	<#if 0 < tasks.size()>
		<#list 0..tasks.size() - 1 as i>
			if(document.getElementById('MyTasks_o_${i}')){
				var paramMap = {};
				paramMap.partyId = "MyTasks_partyId_o_${i}";
				paramMap.workEffortId = "MyTasks_projectId_o_${i}";
				jQuery('#MyTasks_partyId_o_${i}').change(function(e, data){
					getDependentDropdownValuesCustom('getRoleTypeTaskParties', paramMap, 'MyTasks_roleTypeId_o_${i}', 'roleTypeList', 'roleTypeId', 'description', '_none_', '','','', '', '');
				});
				getDependentDropdownValuesCustom('getRoleTypeTaskParties', paramMap, 'MyTasks_roleTypeId_o_${i}', 'roleTypeList', 'roleTypeId', 'description', '_none_', '','','', '', '');
			}
		</#list>
	</#if>
});
</script>