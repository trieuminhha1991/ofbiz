<#assign listProduct = []/>
<#assign dataField = "[
				{ name: 'partyId', type: 'string' },
				{ name: 'partyCode', type: 'string' },
				{ name: 'fullName', type: 'string' },
           	"/>
<#if listProduct?exists>
	<#list listProduct as product>TotalRows
		<#assign dataField = dataField + "
				{ name: 'prodCode_${product.productId}', type: 'number', formatter: 'integer'},
   			"/>
	</#list>
</#if>
<#assign dataField = dataField + "]"/>

<#assign uiLabelMapPartyId = '${uiLabelMap.BSEmployeeId}'>

<#assign columnlist = "
				{text: '${uiLabelMap.BSCustomerId}', datafield: 'partyCode', width: '24%', pinned: true, editable: false},
				{text: '${uiLabelMap.BSCustomerName}', datafield: 'fullName', width: '24%', pinned: true, editable: false},
		 	"/>
<#if listProduct?exists>
	<#list listProduct as product>
		<#assign columnlist = columnlist + "
				{text: '${product.productCode}', dataField: 'prodCode_${product.productId}', width: '120px', cellsalign: 'right', cellClassName: cellClassCommon, editable: true},
			"/>
	</#list>
</#if>

<div id="jqxInventoryCustomer" class="jqx-tree-grid-olbius"></div>

<#assign contextMenuSsdncItemId = "ctxmnussdnc">
<div id='contextMenu_${contextMenuSsdncItemId}' style="display:none">
    <ul>
        <li id="${contextMenuSsdncItemId}_expand"><i class="fa fa-expand"></i>${StringUtil.wrapString(uiLabelMap.BSExpand)}</li>
        <li id="${contextMenuSsdncItemId}_expandAll"><i class="fa fa-expand"></i>${StringUtil.wrapString(uiLabelMap.BSExpandAll)}</li>
        <li id="${contextMenuSsdncItemId}_collapse"><i class="fa fa-compress"></i>${StringUtil.wrapString(uiLabelMap.BSCollapse)}</li>
        <li id="${contextMenuSsdncItemId}_collapseAll"><i class="fa fa-compress"></i>${StringUtil.wrapString(uiLabelMap.BSCollapseAll)}</li>
        <li id="${contextMenuSsdncItemId}_refesh"><i class="fa fa-refresh"></i>${StringUtil.wrapString(uiLabelMap.BSRefresh)}</li>
    </ul>
</div>
<script type="text/javascript">
    var filterObjData = new Object();
    $(function(){
        OlbInventoryCustomerMTNew.init();
    });
    var OlbInventoryCustomerMTNew = (function(){
        var grid;
        var init = function(){
            initElement();
            initElementComplex();
            initEvent();
        };
        var initElement = function(){
            jOlbUtil.contextMenu.create($("#contextMenu_${contextMenuSsdncItemId}"));
        };
        var initElementComplex = function(){
            var datafields = ${dataField};
            var columns = [${columnlist}];
            var configProductList = {
                datafields: datafields,
                columns: columns,
                width: '100%',
                height: 'auto',

                editable: false,
                editmode: 'click',
                sortable: true,
                pageable: true,
                pagesize: 10,
                filterable: true,
                showfilterrow: true,
                useUtilFunc: false,
                useUrl: true,
                url: "",
                showdefaultloadelement: true,
                autoshowloadelement: true,
                showtoolbar: false,
                clearfilteringbutton: false,
                selectionmode: 'multiplecellsadvanced',
                showgroupsheader: true,
                showdefaultloadelement: true,
                autoshowloadelement: true,
                columnsresize: true,
                showtoolbar:true,
                isSaveFormData: true,
                formData: filterObjData,
                rendertoolbarconfig: {
					<#assign customcontrol1 = "fa fa-file-excel-o@${uiLabelMap.BSExportExcel}@javascript: void(0);@OlbInventoryCustomerMTNew.exportExcelInventoryCus()">
                    customcontrol1: "${StringUtil.wrapString(customcontrol1)}",
                },
            };
            grid = new OlbGrid($("#jqxInventoryCustomer"), null, configProductList, []);
        };
        var exportExcelInventoryCus = function(){
            var url = OlbInventoryCustomerMTFind.getUrlExportExcel();
            if (OlbCore.isEmpty(grid) || !grid.isExistData()){
                OlbCore.alert.error("${uiLabelMap.BSNoDataToExport}");
                return false;
            }
            var form = document.createElement("form");
            form.setAttribute("method", "POST");
            form.setAttribute("action", "exportInventoryCustomerExcelMT");
            var hiddenField0 = document.createElement("input");
            hiddenField0.setAttribute("type", "hidden");
            hiddenField0.setAttribute("name", "url");
            hiddenField0.setAttribute("value", url);
            form.appendChild(hiddenField0);
            console.log(filterObjData);
            if (OlbCore.isNotEmpty(filterObjData) && OlbCore.isNotEmpty(filterObjData.data)) {
                $.each(filterObjData.data, function(key, value) {
                    var hiddenField1 = document.createElement("input");
                    hiddenField1.setAttribute("type", "hidden");
                    hiddenField1.setAttribute("name", key);
                    hiddenField1.setAttribute("value", value);
                    form.appendChild(hiddenField1);
                });
            }
            document.body.appendChild(form);
            form.submit();
        };
        var initEvent = function(){
            $("#jqxInventoryCustomer").on('contextmenu', function () {
                return false;
            });
            $("#jqxInventoryCustomer").on('rowClick', function (event) {
                var args = event.args;
                if (args.originalEvent.button == 2) {
                    var scrollTop = $(window).scrollTop();
                    var scrollLeft = $(window).scrollLeft();
                    $("#contextMenu_${contextMenuSsdncItemId}").jqxMenu('open', parseInt(event.args.originalEvent.clientX) + 5 + scrollLeft, parseInt(event.args.originalEvent.clientY) + 5 + scrollTop);
                    return false;
                }
            });
            $("#contextMenu_${contextMenuSsdncItemId}").on('itemclick', function (event) {
                var args = event.args;
                // var tmpKey = $.trim($(args).text());
                var tmpId = $(args).attr('id');
                var idGrid = "#jqxInventoryCustomer";
                var rowData;
                var id;
                var selection = $(idGrid).jqxTreeGrid('getSelection');
                if (selection.length > 0) rowData = selection[0];
                if (rowData) id = rowData.partyId;
                switch(tmpId) {
                    case "${contextMenuSsdncItemId}_refesh": {
                        $(idGrid).jqxTreeGrid('updateBoundData');
                        break;
                    };
                    case "${contextMenuSsdncItemId}_expandAll": {
                        $(idGrid).jqxTreeGrid('expandAll', true);
                        break;
                    };
                    case "${contextMenuSsdncItemId}_collapseAll": {
                        $(idGrid).jqxTreeGrid('collapseAll', true);
                        break;
                    };
                    case "${contextMenuSsdncItemId}_expand": {
                        if(id) $(idGrid).jqxTreeGrid('expandRow', id);
                        break;
                    };
                    case "${contextMenuSsdncItemId}_collapse": {
                        if(id) $(idGrid).jqxTreeGrid('collapseRow', id);
                        break;
                    };
                    default: break;
                }
            });
            $("#jqxInventoryCustomer").on("cellendedit", function (event) {
                var args = event.args;
                var columnName = args.datafield;
                if (columnName && columnName.substring(0, 9) == "prodCode_") {
                    var rowBoundIndex = args.rowindex;
                    var data = $("#jqxInventoryCustomer").jqxGrid("getrowdata", rowBoundIndex);
                    if (data && data.partyId) {
                        var partyId = data.partyId;
                        var productId = columnName.substring(9, columnName.length);
                        var oldValue = args.oldvalue;
                        var newValue = args.value;
                        var itemId = "" + partyId + "@" + productId;
                        if (typeof(productListMap[itemId]) != 'undefined') {
                            var itemValue = productListMap[itemId];
                            itemValue.qtyInInventory = newValue;
                            productListMap[itemId] = itemValue;
                        } else {
                            var itemValue = {};
                            itemValue.partyId = partyId;
                            itemValue.productId = productId;
                            itemValue.qtyInInventory = newValue;
                            productListMap[itemId] = itemValue;
                        }
                    }
                }
            });
        };
        return {
            init: init,
            exportExcelInventoryCus: exportExcelInventoryCus,
        }
    }());
</script>