<script type="text/javascript">
    var cellClassCommon = function (row, dataField, cellText, rowData) {
        if (OlbCore.isNotEmpty(rowData["totalEmployee"])) {
            return "background-cancel-ze";
        }
    };
    var productListMap = {};
</script>
<style type="text/css">
    #jqxCatalogCategoryGrid .chkbox.jqx-widget.jqx-checkbox{
        margin-top:6px !important;
    }
    .background-root0 {
        color: #459A0D !important;
        background-color: #B4EC90 !important;
        border-color: #82CC52 !important;
        font-weight: bold !important;
    //background-color: rgba(105, 189, 51, 0.28) !important;
    //border-color: rgba(104, 188, 49, 0.48) !important;
    }
    #statusbarjqxSalesStatement {
        width: 0 !important;
    }
</style>
<div id="container"
     style="background-color: transparent; overflow: auto; position:fixed; top:0; right:0; z-index: 99999; width:auto">
</div>
<div id="jqxNotification" style="margin-bottom:5px">
    <div id="notificationContent"></div>
</div>
<div class="row-fluid">
    <div class="span12">
        <div class="form-horizontal form-window-content-custom">
            <div class="row-fluid">
                <div class="span6">
                    <div class='row-fluid'>
                        <div class='span5'>
                            <label>${uiLabelMap.BSRangeDate}</label>
                        </div>
                        <div class="span7">
                            <div class="row-fluid">
                                <div class="span12">
                                    <div id="changeDateTypeId"></div>
                                </div>
                            </div>
                            <div class="row-fluid">
                                <div class="span6">
                                    <div id="fromDate"></div>
                                </div>
                                <div class="span6">
                                    <div id="thruDate"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div><!-- .span6 -->
                <div class="span6">
                    <div class="row-fluid">
                        <div class="span6">
                            <div class="pull-left">
                                <button type="button" id="btnFindInventoryAgentGT" class="btn btn-small btn-primary"><i class="fa fa-search"></i>&nbsp;${uiLabelMap.BSActionFind}</button>
                            </div>
                        </div>
                        <div class="span6">
                            <div class="widget-toolbar no-border">
                                <div id='jqxCatalogCategoryId' style='display:inline-block; vertical-align: middle;'></div>
                                <button type="button" id="openFilterCatalogCategory" class="btn btn-mini btn-primary"  style='display:inline-block'><i class="fa fa-bars"></i></button>
                            </div>
                        </div>
                    </div>
                </div><!-- .span6 -->
            </div><!-- .row-fluid -->
        </div>
    </div>
</div>
<div style="position:relative">
    <div id="loader_page_common" class="jqx-rc-all jqx-rc-all-olbius loader-page-common-custom">
        <div class="jqx-rc-all jqx-rc-all-olbius jqx-fill-state-normal jqx-fill-state-normal-olbius">
            <div>
                <div class="jqx-grid-load"></div>
                <span>${uiLabelMap.BSLoading}...</span>
            </div>
        </div>
    </div>
</div>
<div id="alterpopupWindowFilter" style="display:none">
    <div>${uiLabelMap.BSFilterOption}</div>
    <div class='form-window-container'>
        <div class='form-window-content'>
            <div class="row-fluid">
                <div class="span12 form-window-content-custom">
                    <div id="jqxCatalogCategoryGrid"></div>
                </div>
            </div>
        </div>
        <div class="form-action">
            <div class="pull-right form-window-content-custom">
                <button id="wf_alterSave" class='btn btn-primary form-action-button'><i class='fa-check'></i> ${uiLabelMap.CommonSave}</button>
                <button id="wf_alterCancel" class='btn btn-danger form-action-button'><i class='fa-remove'></i> ${uiLabelMap.CommonCancel}</button>
            </div>
        </div>
    </div>
</div>
<div class="row-fluid">
    <div class="span12">
		<#include "viewInventoryCustomerGTDetailGrid.ftl"/>
    </div>
</div><!-- .row-fluid -->


<@jqGridMinimumLib />
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcore.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdata2.full.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxbuttons.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxscrollbar.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdatatable.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtreegrid2.full.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxlistbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownlist.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcheckbox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcolorpicker.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxinput.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxdropdownbutton.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxnotification2.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxcombobox.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxtree.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/aceadmin/assets/js/fuelux/fuelux.wizard.min.js"></script>
<script src="/hrresources/js/shim.js" type="text/javascript"></script>
<script src="/hrresources/js/jszip.js" type="text/javascript"></script>
<script src="/hrresources/js/xlsx.js" type="text/javascript"></script>
<script src="/aceadmin/assets/js/spin.min.js"></script>
<script type="text/javascript" src="/hrresources/js/CommonFunction.js"></script>
<@jqOlbCoreLib hasGrid=true hasTreeGrid=true hasDropDownButton=true hasDropDownList=true hasComboBox=true/>

<script type="text/javascript">
    //var urlSNameFindProdPrice = "JQGetListProductSalesPriceChange";

    $(function(){
        OlbInventoryCustomerGTFind.init();
    });
    var OlbInventoryCustomerGTFind = (function(){
        var changeDateTypeDDL;
        var agentsChainDDB;
        var localDataCatalogCategory = [];
        var productCatalogCategoryDDL;
        var catalogIds;
        var categoryIds;
        var currentListProductIds;
        var currentListProducts;
        var urlForExportExcel;

        var init = function(){
            initElement();
            initComplexElement();
            initEvent();
            initData();
        };
        var initData = function () {
            // get local storage
            var localStorageSaveJson = localStorage.getItem('statement_new_clog_cate');
            if (OlbCore.isNotEmpty(localStorageSaveJson)) {
                var localStorageSave = JSON.parse(localStorageSaveJson);
                var localCatalogIds = localStorageSave.catalogIds;
                var localCategoryIds = localStorageSave.categoryIds;
                if (OlbCore.isNotEmpty(localCatalogIds)) {
                    $.each(localCatalogIds, function (key, item) {
                        $("#jqxCatalogCategoryGrid").jqxTree('checkItem', 'CLOG_' + item, true);
                    });
                }
                if (OlbCore.isNotEmpty(localCategoryIds)) {
                    $.each(localCategoryIds, function (key, item) {
                        $("#jqxCatalogCategoryGrid").jqxTree('checkItem', $("#CATE_" + item)[0], true);
                    });
                }
                updateDataGridCategory();
            } else {
                updateGridInventCusSource();
            }
        };
        var initElement = function(){
            jOlbUtil.windowPopup.create($("#alterpopupWindowFilter"), {width: 820, height: 460, cancelButton: $("#wf_alterCancel")});
            jOlbUtil.notification.create($("#container"), $("#jqxNotification"));
            jOlbUtil.dateTimeInput.create("#fromDate", {width: '100%', allowNullDate: true, value: null, showFooter: true, formatString: 'dd/MM/yyyy', disabled: true});
            jOlbUtil.dateTimeInput.create("#thruDate", {width: '100%', allowNullDate: true, value: null, showFooter: true, formatString: 'dd/MM/yyyy', disabled: true});
        };
        var initComplexElement = function(){
            var changeDateTypeData = [
                {typeId: "LASTCHECK", description: "${uiLabelMap.BSLastCheck}"},
            ];
            var configChangeDateType = {
                width: '100%',
                placeHolder: "${uiLabelMap.BSClickToChoose}",
                useUrl: false,
                key: 'typeId',
                value: 'description',
                autoDropDownHeight: true,
                addNullItem: true,
            }
            changeDateTypeDDL = new OlbDropDownList($("#changeDateTypeId"), changeDateTypeData, configChangeDateType, ["LASTCHECK"]);

            var configCatalogCategoryList = {
                datatype: 'array',
                width: '220px',
                placeHolder: "${StringUtil.wrapString(uiLabelMap.BSNoItem)}",
                useUrl: false,
                key: 'id',
                value: 'label',
                autoDropDownHeight: false,
                indexSelected: 0
            };
            productCatalogCategoryDDL = new OlbDropDownList($("#jqxCatalogCategoryId"), productCatalogCategoryDDL, configCatalogCategoryList, []);
            var source = {
                datatype: "json",
                datafields: [
                    {name: 'id'},
                    {name: 'parentId'},
                    {name: 'dataType'},
                    {name: 'label'},
                    {name: 'value'},
                    {name: 'expanded'}
                ],
                root: "results",
                id: 'id',
                url: 'listCatalogAndCategory',
                async: false
            };
            var dataAdapter = new $.jqx.dataAdapter(source);
            dataAdapter.dataBind();
            var records = dataAdapter.getRecordsHierarchy('id', 'parentId', 'items', [{
                name: 'value',
                map: 'value'
            }, {name: 'label', map: 'label'}]);
            $('#jqxCatalogCategoryGrid').jqxTree({
                theme: 'energyblue',
                source: records,
                height: '360px',
                width: '770px',
                hasThreeStates: true,
                checkboxes: true,
            });
        };
        var initEvent = function(){
            $("#btnFindInventoryAgentGT").on("click", function(){
                var changeDateTypeId = changeDateTypeDDL.getValue();
                var fromDate;
                var thruDate;
                if (typeof($('#fromDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#fromDate').jqxDateTimeInput('getDate') != null) {
                    fromDate = $('#fromDate').jqxDateTimeInput('getDate').getTime();
                }
                if (typeof($('#thruDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#thruDate').jqxDateTimeInput('getDate') != null) {
                    thruDate = $('#thruDate').jqxDateTimeInput('getDate').getTime();
                }

                var infoCustomerAndDate = "";
                if (OlbCore.isNotEmpty(changeDateTypeId)) infoCustomerAndDate += "&changeDateTypeId=" + changeDateTypeId;
                if (OlbCore.isNotEmpty(fromDate)) infoCustomerAndDate += "&fromDate=" + fromDate;
                if (OlbCore.isNotEmpty(thruDate)) infoCustomerAndDate += "&thruDate=" + thruDate;

                updateJqxInventoryCustomer(catalogIds, categoryIds, infoCustomerAndDate);
            });

            changeDateTypeDDL.selectListener(function(itemData){
                var changeDateTypeId = itemData.value;
                if (OlbCore.isNotEmpty(changeDateTypeId)) {
                    $("#fromDate").jqxDateTimeInput("disabled", true);
                    $("#thruDate").jqxDateTimeInput("disabled", true);
                } else {
                    $("#fromDate").jqxDateTimeInput("disabled", false);
                    $("#thruDate").jqxDateTimeInput("disabled", false);
                }
            });
            $('#wf_alterSave').click(function () {
                updateAllDataGrid();
            });
            $('#openFilterCatalogCategory').click(function () {
                $("#alterpopupWindowFilter").jqxWindow("open");
            });
            productCatalogCategoryDDL.selectListener(function (itemData, index) {
                var changeDateTypeId = changeDateTypeDDL.getValue();
                var fromDate;
                var thruDate;
                if (typeof($('#fromDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#fromDate').jqxDateTimeInput('getDate') != null) {
                    fromDate = $('#fromDate').jqxDateTimeInput('getDate').getTime();
                }
                if (typeof($('#thruDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#thruDate').jqxDateTimeInput('getDate') != null) {
                    thruDate = $('#thruDate').jqxDateTimeInput('getDate').getTime();
                }

                var infoCustomerAndDate = "";
                if (OlbCore.isNotEmpty(changeDateTypeId)) infoCustomerAndDate += "&changeDateTypeId=" + changeDateTypeId;
                if (OlbCore.isNotEmpty(fromDate)) infoCustomerAndDate += "&fromDate=" + fromDate;
                if (OlbCore.isNotEmpty(thruDate)) infoCustomerAndDate += "&thruDate=" + thruDate;
                if (index != undefined) {
                    if (index > 0) {
                        var originalItem = itemData.originalItem;
                        if ("CATE" == originalItem.type) updateJqxInventoryCustomer([], [originalItem.id], infoCustomerAndDate);
                        else if ("CLOG" == originalItem.type) updateJqxInventoryCustomer([originalItem.id], [], infoCustomerAndDate);
                    } else {
                        updateJqxInventoryCustomer(catalogIds, categoryIds, infoCustomerAndDate);
                    }
                }
            });
        };
        var updateDataGridCategory = function () {
            var str = "";
            var items = $('#jqxCatalogCategoryGrid').jqxTree('getCheckedItems');
            catalogIds = new Array();
            categoryIds = new Array();
            localDataCatalogCategory = [];
            localDataCatalogCategory[0] = {"id": "_NA_", "label": "${uiLabelMap.BSNoItem}", "type": "_NA_"};
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                var idArr = item.id.split("_");
                if (idArr != undefined) {
                    if ("CLOG" == idArr[0]) {
                        catalogIds.push(item.value);
                        localDataCatalogCategory.push({'id': item.value, 'label': item.label, 'type': 'CLOG'});
                    } else if ("CATE" == idArr[0]) {
                        categoryIds.push(item.value);
                        localDataCatalogCategory.push({'id': item.value, 'label': item.label, 'type': 'CATE'});
                    }
                }
            }
            updateJqxDDLCatalogCategory(catalogIds, categoryIds, localDataCatalogCategory);
        };
        var updateAllDataGrid = function () {
            var str = "";
            var items = $('#jqxCatalogCategoryGrid').jqxTree('getCheckedItems');
            catalogIds = new Array();
            categoryIds = new Array();
            localDataCatalogCategory = [];
            localDataCatalogCategory[0] = {"id": "_NA_", "label": "${uiLabelMap.BSNoItem}", "type": "_NA_"};
            for (var i = 0; i < items.length; i++) {
                var item = items[i];
                var idArr = item.id.split("_");
                if (idArr != undefined) {
                    if ("CLOG" == idArr[0]) {
                        catalogIds.push(item.value);
                        localDataCatalogCategory.push({'id': item.value, 'label': item.label, 'type': 'CLOG'});
                    } else if ("CATE" == idArr[0]) {
                        categoryIds.push(item.value);
                        localDataCatalogCategory.push({'id': item.value, 'label': item.label, 'type': 'CATE'});
                    }
                }
            }
            var changeDateTypeId = changeDateTypeDDL.getValue();
            var fromDate;
            var thruDate;
            if (typeof($('#fromDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#fromDate').jqxDateTimeInput('getDate') != null) {
                fromDate = $('#fromDate').jqxDateTimeInput('getDate').getTime();
            }
            if (typeof($('#thruDate').jqxDateTimeInput('getDate')) != 'undefined' && $('#thruDate').jqxDateTimeInput('getDate') != null) {
                thruDate = $('#thruDate').jqxDateTimeInput('getDate').getTime();
            }

            var infoCustomerAndDate = "";
            if (OlbCore.isNotEmpty(changeDateTypeId)) infoCustomerAndDate += "&changeDateTypeId=" + changeDateTypeId;
            if (OlbCore.isNotEmpty(fromDate)) infoCustomerAndDate += "&fromDate=" + fromDate;
            if (OlbCore.isNotEmpty(thruDate)) infoCustomerAndDate += "&thruDate=" + thruDate;

            updateJqxDDLCatalogCategory(catalogIds, categoryIds, localDataCatalogCategory);
            updateJqxInventoryCustomer(catalogIds, categoryIds, infoCustomerAndDate);
        };
        var updateJqxDDLCatalogCategory = function (catalogIds, categoryIds, localData) {
            if (!OlbCore.isNotEmpty(catalogIds)) catalogIds = [];
            if (!OlbCore.isNotEmpty(categoryIds)) categoryIds = [];
            var catalogSize = catalogIds.length;
            var categorySize = categoryIds.length;
            var labelRoot = "";
            if (catalogSize > 0) labelRoot += catalogSize + " ${uiLabelMap.BSCatalog}, ";
            if (categorySize > 0) labelRoot += categorySize + " ${uiLabelMap.BSCategory}";
            localData[0] = {"id": "_NA_", "label": labelRoot, "type": "_NA_"};
            productCatalogCategoryDDL.updateSource(null, localData, function () {
                productCatalogCategoryDDL.selectItem(null, 0);
            });
            localStorage.setItem('statement_new_clog_cate', JSON.stringify({
                "catalogIds": catalogIds,
                "categoryIds": categoryIds
            }));
        };
        var updateJqxInventoryCustomer = function (catalogIds, categoryIds, infoCustomerAndDate) {
            $.ajax({
                type: 'POST',
                url: 'getListProductIdNameAdvanceByCatalogOrCategory',
                data: {
                    catalogIds: JSON.stringify(catalogIds),
                    categoryIds: JSON.stringify(categoryIds)
                },
                beforeSend: function () {
                    $("#loader_page_common").show();
                },
                success: function (data) {
                    jOlbUtil.processResultDataAjax(data, function (data, errorMessage) {
                                $("#btnPrevWizard").removeClass("disabled");
                                $("#btnNextWizard").removeClass("disabled");
                                $('#container').empty();
                                $('#jqxNotification').jqxNotification({template: 'info'});
                                $("#jqxNotification").html(errorMessage);
                                $("#jqxNotification").jqxNotification("open");
                                return false;
                            }, function () {
                                if (data.listProductIds != undefined && data.listProductIds != null && data.listProductIds.length > 0) {
                                    currentListProducts = data.listProductIds;
                                    currentListProducts = sortByProductCode(currentListProducts);
                                    updateJqxCustomerInventoryAll(currentListProducts, infoCustomerAndDate);
                                } else {
                                    updateGridInventCusSource();
                                }
                                $("#alterpopupWindowFilter").jqxWindow("close");
                            }
                    );
                },
                error: function (data) {
                    alert("Send request is error");
                },
                complete: function (data) {
                    $("#loader_page_common").hide();
                },
            });
        };
        var sortByProductCode = function (products) {
            var productIds = [];
            var resultList = [];
            $.each(products, function (_, v) {
                productIds.push(v.productCode);
            });
            productIds.sort();
            $.each(productIds, function (_, aProductId) {
                $.each(products, function (_, aProduct) {
                    if (aProduct.productCode == aProductId) {
                        resultList.push(aProduct);
                    }
                });
            });
            return resultList;
        };
        var updateJqxCustomerInventoryAll = function (productIdsListMap, infoCustomerAndDate) {
            var columnListEdit = new Array();
            var datafieldListEdit = new Array();
            datafieldListEdit.push({name: 'partyId', type: 'string'});
            datafieldListEdit.push({name: 'partyCode', type: 'string'});
            datafieldListEdit.push({name: 'fullName', type: 'string'});
            datafieldListEdit.push({name: 'createdBy', type: 'string'});
            datafieldListEdit.push({name: 'fromDate', type: 'date'});

            columnListEdit.push({
                text: '${uiLabelMap.BSCustomerId}',
                datafield: 'partyCode',
                width: '12%',
                pinned: true,
                editable: false
            });
            columnListEdit.push({
                text: '${uiLabelMap.BSCustomerName}',
                datafield: 'fullName',
                width: '14%',
                pinned: true,
                editable: false
            });
            columnListEdit.push({
                text: '${uiLabelMap.BSSalesExecutive}',
                datafield: 'createdBy',
                width: '14%',
                pinned: true,
                editable: false
            });
            columnListEdit.push({
                text: '${uiLabelMap.InventoryDate}',
                datafield: 'fromDate',
                width: '14%',
                pinned: true,
                editable: false,
                cellsformat: 'dd/MM/yyyy HH:mm:ss',
                filtertype:'range'
            });
            var productIds = new Array();
            var productItem;
            var productId;
            var productCode;
            var productName;
            var quantityUomIdDesc;
            var columnLabel;
            var count = 0;
            for (var i = 0; i < productIdsListMap.length; i++) {
                productId = null;
                productCode = null;
                quantityUomId = null;
                productItem = productIdsListMap[i];
                productId = productItem.productId;
                productCode = productItem.productCode;
                productName = productItem.productName;
                quantityUomIdDesc = productItem.quantityUomIdDesc;
                // columnLabel = "" + productCode + "\n" + productName + " (" + quantityUomIdDesc + ")";
                columnLabel = "" + productName + " (" + quantityUomIdDesc + ")";
                if (productId != null) {
                    productIds.push(productId);
                    datafieldListEdit.push({name: 'prodCode_' + productId, type: 'number', formatter: 'integer'});
                    columnListEdit.push({
                        text: columnLabel,
                        dataField: 'prodCode_' + productId,
                        width: '150px',
                        cellsalign: 'right',
                        cellClassName: cellClassCommon,
                        editable: true,
                        columntype: 'numberinput',
                        filterable: false,
                        sortable: false,
                        cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
                            var rowData = $("#jqxInventoryCustomer").jqxGrid('getrowdata', row);
                            var itemId = "" + rowData.partyId + "@" + columnfield;
                            itemId = itemId.replace("prodCode_", "");
                            if (typeof(productListMap[itemId]) != 'undefined') {
                                var itemValue = productListMap[itemId];
                                return '<span class="align-right">' + itemValue.qtyInInventory + '</span>';
                            } else {
                                return '<span class="align-right">' + value + '</span>';
                            }
                        },
                        initeditor: function (row, cellvalue, editor) {
                            editor.jqxNumberInput({decimalDigits: 0});
                        },
                        validation: function (cell, value) {
                            if (value < 0) {
                                return {result: false, message: '${uiLabelMap.BSQuantityMustBeGreaterThanZero}'};
                            }
                            return true;
                        }
                    });
                }
            }
            currentListProductIds = productIds;
            var productIdsStr = JSON.stringify(productIds);
            $("#jqxInventoryCustomer").jqxGrid('columns', columnListEdit);
            updateGridInventCusSource(datafieldListEdit, productIdsStr, infoCustomerAndDate);
        };
        var updateGridInventCusSource = function (datafieldListEdit, productIdsStr, infoCustomerAndDate) {
            var tmpSource = jQuery("#jqxInventoryCustomer").jqxGrid('source');
            if (tmpSource) {
                if (datafieldListEdit != null && typeof(datafieldListEdit) !== "undefined") tmpSource._source.datafields = datafieldListEdit;
                var urlNew = "jqxGeneralServicer?sname=JQGetListInventoryCusAndProdGT";
                if (productIdsStr != null && typeof(productIdsStr)!== "undefined") urlNew += "&productIds=" + productIdsStr;
                tmpSource._source.url = urlNew;
                if(infoCustomerAndDate != null && typeof(infoCustomerAndDate) !== "undefined") urlNew += infoCustomerAndDate;
                tmpSource._source.url = urlNew;
                urlForExportExcel = urlNew;
                $("#jqxInventoryCustomer").jqxGrid('source', tmpSource);
            }
        };
        var processRowData = function (data) {
            var dataList = [];
            $.each(data, function (key, value) {
                if (typeof(value) != 'undefined') {
                    var prodItem = {
                        partyId: value.partyId,
                        productId: typeof(value.productId) != 'undefined' ? value.productId : '',
                        qtyInInventory: typeof(value.qtyInInventory) != 'undefined' ? value.qtyInInventory : 0,
                    };
                    dataList.push(prodItem);
                }
            });
            return dataList;
        };
        var getListProducts = function () {
            if (OlbCore.isNotEmpty(currentListProducts)) return currentListProducts;
            return [];
        };
        var getObj = function() {
            return {
                changeDateTypeDDL: changeDateTypeDDL,
                agentsChainDDB: agentsChainDDB
            }
        };
        var getUrlExportExcel = function(){
            if (OlbCore.isNotEmpty(urlForExportExcel)) {
                return urlForExportExcel;
            }
            return "";
        };
        return {
            init: init,
            getUrlExportExcel: getUrlExportExcel,
            getObj: getObj
        };
    }());
    uiLabelMap.CommonChooseFile = "${StringUtil.wrapString(uiLabelMap.CommonChooseFile)}";
    uiLabelMap.BSConfirmUploadCustomerqtyInInventory = "${StringUtil.wrapString(uiLabelMap.BSConfirmUploadCustomerqtyInInventory)}";
    uiLabelMap.ColumnDataInSystem = "${StringUtil.wrapString(uiLabelMap.ColumnDataInSystem)}";
    uiLabelMap.ColumnDataInImportFile = "${StringUtil.wrapString(uiLabelMap.ColumnDataInImportFile)}";
    uiLabelMap.FieldRequired = "${StringUtil.wrapString(uiLabelMap.FieldRequired)}";
    uiLabelMap.CommonSubmit = "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}";
    uiLabelMap.CommonClose = "${StringUtil.wrapString(uiLabelMap.CommonClose)}";
    uiLabelMap.wgupdatesuccess = "${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}";
    uiLabelMap.ColumnMapAuto = "${StringUtil.wrapString(uiLabelMap.ColumnMapAuto)}";
    uiLabelMap.BSCommonReset = "${StringUtil.wrapString(uiLabelMap.BSCommonReset)}";
    uiLabelMap.JoinColumnDataExcel = "${StringUtil.wrapString(uiLabelMap.JoinColumnDataExcel)}";
    uiLabelMap.BSCustomerId = "${StringUtil.wrapString(uiLabelMap.BSCustomerId)}";
    uiLabelMap.BSCustomerName = "${StringUtil.wrapString(uiLabelMap.BSCustomerName)}";
</script>
