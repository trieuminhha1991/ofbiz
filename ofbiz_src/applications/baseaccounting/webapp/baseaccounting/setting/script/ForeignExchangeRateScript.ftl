<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/ett/olb.dropdownbutton.js"></script>
<@jqOlbCoreLib hasGrid=true hasValidator=true/>
<script type="text/javascript" language="Javascript">
    var luData = new Array();
    luData = [
    <#if listUom?exists && listUom?has_content>
    	<#list listUom as e>
    	<#if e?exists && e.uomId == "USD" || e.uomId == "EUR" || e.uomId == "VND">
    	{
    		'uomId' : '${e.uomId?if_exists}',
    		'description' : "${StringUtil.wrapString(e.get("description",locale)?default(""))}"
    	},
    	</#if>
    	</#list>
    </#if>
    ]
    
    var leData = new Array();
    leData = [
    <#if listEnum?exists && listEnum?has_content>
    	<#list listEnum as e>
    	{
    		'enumId' : '${e.enumId?if_exists}',
    		'description' : "${StringUtil.wrapString(e.get("description",locale)?default(""))}"
    	},
    	</#list>
    </#if>
    ]
    
    var bankData = new Array();
    bankData = [
    <#if listBank?exists && listBank?has_content>
    	<#list listBank as e>
    	{
    		'bankId' : '${e.bankId?if_exists}',
    		'bankName' : "${StringUtil.wrapString(e.bankName?default(""))}"
    	},
    	</#list>
    </#if>
    ];
    
    var uiLabelMap = {};
    uiLabelMap.PleaseChooseAcc = "${StringUtil.wrapString(uiLabelMap.BACCPleaseChooseAcc?default(''))}";
    uiLabelMap.BACCBankId = "${StringUtil.wrapString(uiLabelMap.BACCBankId?default(''))}";
    uiLabelMap.BACCBankName = "${StringUtil.wrapString(uiLabelMap.BACCBankName?default(''))}";
    uiLabelMap.BACCShortName = "${StringUtil.wrapString(uiLabelMap.BACCShortName?default(''))}";

</script>