<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/logresources/js/logisticsCommon.js"></script>
<style type="text/css">
.step-pane {
    min-height: 20px !important;
}
</style>
<script type="text/javascript">	
	var listProductSelected = [];
	
	<#assign companyGroup = delegator.findOne("PartyGroup", {"partyId" : "company"}, false)/>
	
	var companyName = "${companyGroup.groupName?if_exists}";
	
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.AreYouSureCreate = "${StringUtil.wrapString(uiLabelMap.AreYouSureCreate)}";
	uiLabelMap.YouNotYetChooseProduct = "${StringUtil.wrapString(uiLabelMap.YouNotYetChooseProduct)}";
	uiLabelMap.LogYes = "${StringUtil.wrapString(uiLabelMap.LogYes)}";
	uiLabelMap.LogNO = "${StringUtil.wrapString(uiLabelMap.LogNO)}";
	uiLabelMap.QuantityNotEnoghForLost = "${StringUtil.wrapString(uiLabelMap.QuantityNotEnoghForLost)}";
	uiLabelMap.AreYouSurePrint = "${StringUtil.wrapString(uiLabelMap.AreYouSurePrint)}";
	uiLabelMap.CommonCancel = "${StringUtil.wrapString(uiLabelMap.CommonCancel)}";
	uiLabelMap.OK = "${StringUtil.wrapString(uiLabelMap.OK)}";
	
	uiLabelMap.or = "${StringUtil.wrapString(uiLabelMap.or)}";
	uiLabelMap.CompanyName = "${StringUtil.wrapString(uiLabelMap.CompanyName)}";
	uiLabelMap.ProductId = "${StringUtil.wrapString(uiLabelMap.ProductId)}";
	uiLabelMap.ProductName = "${StringUtil.wrapString(uiLabelMap.ProductName)}";
	uiLabelMap.UnitPrice = "${StringUtil.wrapString(uiLabelMap.UnitPrice)}";
	uiLabelMap.Width = "${StringUtil.wrapString(uiLabelMap.Width)}";
	uiLabelMap.Height = "${StringUtil.wrapString(uiLabelMap.Height)}";
</script>
<script type="text/javascript" src="/logresources/js/util/StringUtil.js"></script>
<script type="text/javascript" src="/logresources/js/util/DateUtil.js"></script>
<script type="text/javascript" src="/logresources/js/product/printBarCodeProductsTemplate.js"></script>