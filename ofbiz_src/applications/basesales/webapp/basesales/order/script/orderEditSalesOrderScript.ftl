<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<@jqOlbCoreLib hasGrid=true hasDropDownList=true hasValidator=true/>
<script type="text/javascript">
	<#assign idGridJQ = "jqxEditSO"/>
	<#assign addType = "popup"/>
	<#assign alternativeAddPopup="alterpopupWindow"/>
	
	function renderJqxTitle(){
		<#if titleProperty?has_content && (!customTitleProperties?exists || customTitleProperties == "")>
	        <@renderJqxTitle titlePropertyTmp=titleProperty id=idGridJQ/>
	        return jqxheader;
	    <#elseif customTitleProperties?exists && customTitleProperties != "">
	        <@renderJqxTitle titlePropertyTmp=customTitleProperties id=idGridJQ/>
	        return jqxheader;
	    </#if>
	    return "";
	}
	var dataCreateAddRowButton = null;
	var addType = "${addType}";
	<#if addType != "popup">
		<#if addinitvalue !="">
	        dataCreateAddRowButton = {${primaryColumn}: '${addinitvalue}'}
	    <#else>
	    	dataCreateAddRowButton = ${primaryColumn}
	    </#if>  
	</#if>
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.BSShipGroupSeqId = "${StringUtil.wrapString(uiLabelMap.BSShipGroupSeqId)}";
	uiLabelMap.BSCustomerId = "${StringUtil.wrapString(uiLabelMap.BSCustomerId)}";
	uiLabelMap.BSProductId = "${StringUtil.wrapString(uiLabelMap.BSProductId)}";
	uiLabelMap.BSProductName = "${StringUtil.wrapString(uiLabelMap.BSProductName)}";
	uiLabelMap.BSUom = "${StringUtil.wrapString(uiLabelMap.BSUom)}";
	uiLabelMap.BSUnitPrice = "${StringUtil.wrapString(uiLabelMap.BSUnitPrice)}";
	uiLabelMap.BSQuantity = "${StringUtil.wrapString(uiLabelMap.BSQuantity)}";
	uiLabelMap.BSQuantityMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.BSQuantityMustBeGreaterThanZero)}";
	uiLabelMap.accAddNewRow = "${StringUtil.wrapString(uiLabelMap.accAddNewRow)}";
	uiLabelMap.BSYouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.BSYouNotYetChooseProduct)}!";
	uiLabelMap.BSRefresh = "${StringUtil.wrapString(uiLabelMap.BSRefresh)}!";
	uiLabelMap.BSDeleteOrderItem = "${StringUtil.wrapString(uiLabelMap.BSDeleteOrderItem)}";
	uiLabelMap.BSThisOrderHasOnlyOneOrderItemIfDeleteThisOrderItemThen = "${StringUtil.wrapString(uiLabelMap.BSThisOrderHasOnlyOneOrderItemIfDeleteThisOrderItemThen)}";
	uiLabelMap.BSAreYouSureYouWantToCancelThisOrderItem = "${StringUtil.wrapString(uiLabelMap.BSAreYouSureYouWantToCancelThisOrderItem)}";
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	uiLabelMap.BSAddedManually = "${StringUtil.wrapString(uiLabelMap.BSAddedManually)}";
	var idGridJQ = "${idGridJQ}";
	var currencyUomId = <#if currencyUomId?exists>"${currencyUomId}"<#else>""</#if>
	var alternativeAddPopup = <#if alternativeAddPopup?exists>"${alternativeAddPopup}"<#else>""</#if>
	var orderId = <#if orderHeader.orderId?exists>"${orderHeader.orderId}"<#else>""</#if>
	
	jOlbUtil.setUiLabelMap("wgupdatesuccess", uiLabelMap.wgupdatesuccess);
</script>
<script type="text/javascript" src="/salesresources/js/order/orderEdit.js"></script>