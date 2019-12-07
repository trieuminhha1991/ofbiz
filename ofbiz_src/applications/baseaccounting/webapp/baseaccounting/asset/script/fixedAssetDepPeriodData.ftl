<script>
	monthPeriodData = [
		{
			   text: "${uiLabelMap.BACCJanuary}",
			   value: 1
		},
		{
			   text: "${uiLabelMap.BACCFebruary}",
			   value: 2
		},
		{
			   text: "${uiLabelMap.BACCMarch}",
			   value: 3
		},
		{
			   text: "${uiLabelMap.BACCApril}",
			   value: 4
		},
		{
			   text: "${uiLabelMap.BACCMay}",
			   value: 5
		},
		{
			   text: "${uiLabelMap.BACCJune}",
			   value: 6
		},
		{
			   text: "${uiLabelMap.BACCJuly}",
			   value: 7
		},
		{
			   text: "${uiLabelMap.BACCAugust}",
			   value: 8
		},
		{
			   text: "${uiLabelMap.BACCSeptember}",
			   value: 9
		},
		{
			   text: "${uiLabelMap.BACCOctober}",
			   value: 10
		},
		{
			   text: "${uiLabelMap.BACCNovember}",
			   value: 11
		},
		{
			   text: "${uiLabelMap.BACCDecember}",
			   value: 12
		}
	];
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; //January is 0!
	var yyyy = today.getFullYear();
	
	<#assign currentOrgId = Static["com.olbius.basehr.util.MultiOrganizationUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
	var currentOrgId = '${currentOrgId?if_exists}';
	
	var booleanData = [
   		{
   			   description: "${uiLabelMap.BACCOpen}",
   			   value: 'N'
   		},
   		{
   			   description: "${uiLabelMap.BACCClose}",
   			   value: 'Y'
   		},
	]
</script>