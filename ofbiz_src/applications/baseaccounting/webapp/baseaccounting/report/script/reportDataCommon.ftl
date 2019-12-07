<#--===================================Prepare Data=====================================================-->
<script>
	//Prepare for customTimePeriod
	<#assign listGlAccounts = delegator.findList("GlAccount", null, null, null, null, false)>

	var glAccountData = [
 		<#list listGlAccounts as item>
 			{
 				<#assign description = StringUtil.wrapString(item.accountName + " [" + item.accountCode + "]") />
 				glAccountId : '${item.glAccountId}',
 				description : '${description}',
 			},
 		</#list>
 	]
	
	listCustomTimePeriodsMonthDataSource = [
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
	
	listCustomTimePeriodsQuaterDataSource = [
	   {
		   text: "${uiLabelMap.BACCFirstQuarter}",
		   value: 1
	   },
	   {
		   text: "${uiLabelMap.BACCSecondQuarter}",
		   value: 2
	   },
	   {
		   text: "${uiLabelMap.BACCThirdQuarter}",
		   value: 3
	   },
	   {
		   text: "${uiLabelMap.BACCFourthQuarter}",
		   value: 4
	   }
	];
	
	listCustomTimePeriodsYearDataSource = [
	   {
		   text: "2015",
		   value: 2015
	   },
	   {
		   text: "2016",
		   value: 2016
	   }
	];
	
	var filterData = [
	          		{'text': '${StringUtil.wrapString(uiLabelMap.KChannel)}', 'value': 'channel'},
	          		{'text': '${StringUtil.wrapString(uiLabelMap.KProductStore)}', 'value': 'productstore'},
	          	];
	          	
  	var filterData2 = [
  		{'text': '${StringUtil.wrapString(uiLabelMap.KSalesVolume)}', 'value': 'salesvolume'},
  		{'text': '${StringUtil.wrapString(uiLabelMap.KSalesValue)}', 'value': 'salesvalue'},
  		{'text': '${StringUtil.wrapString(uiLabelMap.KOrderVolume)}', 'value': 'ordervolume'},
  	];
</script>
<#--===================================/Prepare Data=====================================================-->