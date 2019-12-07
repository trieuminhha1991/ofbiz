<#assign productFeatureTypes = Static["com.olbius.basepo.product.ProductUtils"].getProductFeatureTypes(delegator) />

<#assign listQuantityUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "PRODUCT_PACKING"), null, null, null, false) />
<#assign listWeightUom = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("uomTypeId", "WEIGHT_MEASURE"), null, null, null, false) />
<style type="text/css">
.bootbox{
    z-index: 990009 !important;
}
.modal-backdrop{
    z-index: 890009 !important;
}
.loading-container{
	z-index: 999999 !important;
}
</style>
<script>
	var gridSelecting;
	$(document).ready(function() {
		if(getCookie().checkContainValue("newProduct")){
			deleteCookie("newProduct");
			$("#jqxNotificationNested").jqxNotification("closeLast");
			$("#jqxNotificationNested").jqxNotification({ template: "info"});
			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgaddsuccess)}");
			$("#jqxNotificationNested").jqxNotification("open");
		}
		if(getCookie().checkContainValue("updateProduct")){
			deleteCookie("updateProduct");
			$("#jqxNotificationNested").jqxNotification("closeLast");
			$("#jqxNotificationNested").jqxNotification({ template: "info"});
			$("#notificationContentNested").text("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
			$("#jqxNotificationNested").jqxNotification("open");
		}
		$("#jqxNotificationNested").jqxNotification({ theme: "olbius", opacity: 0.9, autoClose: true, template: "info" });
		
		var contextMenu = $("#contextMenu").jqxMenu({ theme: "olbius", width: 230, autoOpenPopup: false, mode: "popup"});
		contextMenu.on("itemclick", function (event) {
			var args = event.args;
			var itemId = $(args).attr("id");
			var rowIndexSelected = $("#jqxgrid").jqxGrid("getSelectedRowindex");
			var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowIndexSelected);
			switch (itemId) {
			case "viewProductDetails":
				if (rowIndexSelected == -1) {
					rowIndexSelected = gridSelecting.jqxGrid("getSelectedRowindex");
					rowData = gridSelecting.jqxGrid("getrowdata", rowIndexSelected);
				}
				window.location.href = "viewProduct?productId=" + rowData.productId;
				break;
			case "addNewProduct":
				if (rowIndexSelected == -1) {
					rowIndexSelected = gridSelecting.jqxGrid("getSelectedRowindex");
					rowData = gridSelecting.jqxGrid("getrowdata", rowIndexSelected);
				}
				if (rowData) {
					$("#txtProductIdFrom").text(rowData.productName);
					AddProductVariant.open(rowData);
				}
				break;
			case "viewContent":
				if (rowIndexSelected == -1) {
					rowIndexSelected = gridSelecting.jqxGrid("getSelectedRowindex");
					rowData = gridSelecting.jqxGrid("getrowdata", rowIndexSelected);
				}
				window.location.href = "ListProductContent?productId=" + rowData.productId;
				break;
			case "addContent":
				if (rowIndexSelected == -1) {
					rowIndexSelected = gridSelecting.jqxGrid("getSelectedRowindex");
					rowData = gridSelecting.jqxGrid("getrowdata", rowIndexSelected);
				}
				window.location.href = "ContentEditorEngine?productId=" + rowData.productId  + "&type=PRODUCT";
				break;
			case "viewComments":
				CommentTree.load(rowData.productId, "true");
				break;
			case "view":
				window.open("/baseecommerce/control/product?product_id=" + rowData.productId, "_blank");
				break;
			case "configProduct":
				window.open("ConfigProductAndCategories?productId=" + rowData.productId, "_blank");
				break;
			case "addSimilarProduct":
				if (rowIndexSelected == -1) {
					rowIndexSelected = gridSelecting.jqxGrid("getSelectedRowindex");
					rowData = gridSelecting.jqxGrid("getrowdata", rowIndexSelected);
				}
				if (rowData) {
					window.open("newProduct?productIdOrg=" + rowData.productId, "_blank");
				}
				break;
			default:
				break;
			}
		});
		contextMenu.on("shown", function () {
			<#if security.hasEntityPermission("CATALOG", "_CREATE", session)>
			<#if hasOlbPermission("MODULE", "PRODUCTPO_NEW", "")>
				var rowIndexSelected = $("#jqxgrid").jqxGrid("getSelectedRowindex");
				var rowData = $("#jqxgrid").jqxGrid("getrowdata", rowIndexSelected);
				if (rowIndexSelected == -1) {
					rowIndexSelected = gridSelecting.jqxGrid("getSelectedRowindex");
					rowData = gridSelecting.jqxGrid("getrowdata", rowIndexSelected);
				}
				var isVirtual = rowData.isVirtual;
				if (isVirtual == "Y") {
					contextMenu.jqxMenu("disable", "addNewProduct", false);
				} else {
					contextMenu.jqxMenu("disable", "addNewProduct", true);
				}
			</#if>
			</#if>
		});
		$("body").on("click", function() {
			if (contextMenu) {
				contextMenu.jqxMenu("close");
			}
		});
		
		contextMenu.jqxMenu("disable", "viewProductDetails", true);
		contextMenu.jqxMenu("disable", "addNewProduct", true);
		<#if hasOlbPermission("MODULE", "PRODUCTPO_VIEW", "")>
			contextMenu.jqxMenu("disable", "viewProductDetails", false);
		</#if>
		<#if security.hasEntityPermission("CATALOG", "_CREATE", session)>
		<#if hasOlbPermission("MODULE", "PRODUCTPO_NEW", "")>
			contextMenu.jqxMenu("disable", "addNewProduct", false);
		</#if>
		</#if>
	});

	var listQuantityUom = [<#if listQuantityUom?exists><#list listQuantityUom as item>{
		uomId: "${item.uomId?if_exists}", description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapQuantityUom = {<#if listQuantityUom?exists><#list listQuantityUom as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
	</#list></#if>};
	var listWeightUom = [<#if listWeightUom?exists><#list listWeightUom as item>{
		uomId: "${item.uomId?if_exists}", description: "${StringUtil.wrapString(item.get("description", locale))}"
	},</#list></#if>];
	var mapWeightUom = {<#if listWeightUom?exists><#list listWeightUom as item>
		"${item.uomId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
	</#list></#if>};
	var mapProductFeatureType = {<#if productFeatureTypes?exists><#list productFeatureTypes as item>
		"${item.productFeatureTypeId?if_exists}": "${StringUtil.wrapString(item.get("description", locale))}",
	</#list></#if>};
	var productFeatureTypes = [<#if productFeatureTypes?exists><#list productFeatureTypes as item>
    	"${item.productFeatureTypeId?if_exists}",
    </#list></#if>];
	
</script>