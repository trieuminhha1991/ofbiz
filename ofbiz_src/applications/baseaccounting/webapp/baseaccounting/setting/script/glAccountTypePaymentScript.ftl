<script type="text/javascript" language="Javascript">
	<#--get data from server freemaker-->
	<#assign listPM = delegator.findByAnd("PaymentType",null,null,false)/>
	<#assign listGlAccountType = delegator.findByAnd("GlAccountType",null,["description"],false) !>
	
	if(typeof(uiLabelMap) == 'undefined') var uiLabelMap = {};
	uiLabelMap.BACCpaymentTypeId = '${StringUtil.wrapString(uiLabelMap.BACCPaymentTypeId)}';
	uiLabelMap.description = '${StringUtil.wrapString(uiLabelMap.Description)}';
	var listPM = [
		<#list listPM as pm>
		{
			paymentTypeId : '${pm.paymentTypeId?if_exists}' ,
			description : "<span class='custom-style-word'>${StringUtil.wrapString(pm.get("description",locale)?if_exists)}</span>"
		},
		</#list>
	];	
	var dataPT = new Array();
	dataPT = [
		<#list listPaymentType as acc>
			{
				'paymentTypeId' : '${acc.paymentTypeId?if_exists}',
				'description' : "<span class='custom-style-word'> [ ${StringUtil.wrapString(acc.paymentTypeId?default(''))} ]" + " - " + "${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
	var dataGLAT = new Array();
	dataGLAT = [
		<#list listGlAccountType as acc>
			{
				'glAccountTypeId' : '${acc.glAccountTypeId?if_exists}',
				'description' : "<span class='custom-style-word'>${StringUtil.wrapString(acc.get('description',locale)?default(''))}</span>"
			},
		</#list>	
		]
</script>