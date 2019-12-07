<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxwindow.js"></script>
<script type="text/javascript" src="/salesresources/js/promotion/promotionCodeNewPopup.js"></script>
<script type="text/javascript">
	if (uiLabelMap == undefined) var uiLabelMap = {};
	uiLabelMap.ClickToChoose = "${StringUtil.wrapString(uiLabelMap.ClickToChoose)}";
	uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
	uiLabelMap.BSQuantityMustBeGreaterThanZero = "${StringUtil.wrapString(uiLabelMap.BSQuantityMustBeGreaterThanZero)}";
	
	var dataYesNoChoose = [
		{id : "N", description : "${StringUtil.wrapString(uiLabelMap.BSChNo)}"},
		{id : "Y", description : "${StringUtil.wrapString(uiLabelMap.BSChYes)}"}
	];
	
	var dataPromoCodeLayout = [
		{id : "smart", description : "${StringUtil.wrapString(uiLabelMap.ProductPromoLayoutSmart)}"},
		{id : "normal", description : "${StringUtil.wrapString(uiLabelMap.ProductPromoLayoutNormal)}"},
		{id : "sequence", description : "${StringUtil.wrapString(uiLabelMap.ProductPromoLayoutSeqNum)}"},
	];
</script>