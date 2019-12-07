<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqx.utils.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/logresources/js/logisticsCommon.js?v=1.0.5"></script>
<style type="text/css">
	.bootbox {
		z-index: 990009 !important;
	}
	.modal-backdrop {
		z-index: 890009 !important;
	}
	.loading-container {
		z-index: 999999 !important;
	}
</style>
<script>
	var listProductSelected = [];
	var deliveryTypeId = "DELIVERY_SALES";
</script>
<#include "deliveryPrepareScript.ftl">
<script>

	var url = "jqxGeneralServicer?sname=getListDelivery&deliveryTypeId="+deliveryTypeId;
	var gridResult = '#jqxgridDelivery';
	var listProductSelected = [];
	var searchDescription = "${StringUtil.wrapString(uiLabelMap.BLSearchSalesliveryByProduct)}";
	
	var extendToolbar = function(container){
		var str = "<div id='productSearch' class='pull-right margin-top5'><div id='jqxgridListProduct' style='margin-top: 4px;'></div></div>";
		container.append(str);
	}
</script>

<script type="text/javascript" src="/logresources/js/searchProduct.js?v=1.0.5"></script>