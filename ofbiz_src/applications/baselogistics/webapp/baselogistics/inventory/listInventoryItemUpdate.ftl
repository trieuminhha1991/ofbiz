<script>
if (uiLabelMap == undefined) var uiLabelMap = {};
uiLabelMap.CannotGreaterThanActualQuantityOnHand = "${StringUtil.wrapString(uiLabelMap.CannotGreaterThanActualQuantityOnHand)}";
uiLabelMap.NumberGTZ = "${StringUtil.wrapString(uiLabelMap.NumberGTZ)}";
uiLabelMap.InventoryGood = "${StringUtil.wrapString(uiLabelMap.InventoryGood)}";
uiLabelMap.Copy = "${StringUtil.wrapString(uiLabelMap.Copy)}";

<#assign statuses = delegator.findList("StatusItem", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("statusTypeId", "INV_NON_SER_STTS"), null, null, null, false)/>
var statusData2 = [];
<#list statuses as item>
	var row = {};
	<#assign descStatus = StringUtil.wrapString(item.get('description', locale))>
	row['statusId'] = "${item.statusId}";
	row['description'] = "${descStatus?if_exists}";
	statusData2[${item_index}] = row;
</#list>
var tmpRow = {};
tmpRow['statusId'] = 'good';
tmpRow['description'] = uiLabelMap.InventoryGood;
statusData2.push(tmpRow);

var cellClass = function (row, columnfield, value) {
	var data = $('#jqxgridInventoryItemUpdate').jqxGrid('getrowdata', row);
	if (typeof(data) != 'undefined') {
		if (typeof(data.idParent) != 'undefined') {
			return "background-prepare";
		}
	}
}

</script>
<div id='contextMenuUpdate' class="hide">
	<ul>
	    <li><i class="fa fa-files-o"></i>${StringUtil.wrapString(uiLabelMap.Copy)}</li>       
	</ul>
</div>
<script type="text/javascript" src="/logresources/js/inventory/listInventoryItemUpdate.js"></script>