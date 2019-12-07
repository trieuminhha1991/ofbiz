<#assign listTaxCategory = delegator.findByAnd("ProductCategory", {"productCategoryTypeId", "TAX_CATEGORY"}, null, false)!/>
<#assign listWeightUom = delegator.findByAnd("Uom", {"uomTypeId", "WEIGHT_MEASURE"}, null, false)!/>
<#assign listCurrencyUom = delegator.findByAnd("Uom", {"uomTypeId", "CURRENCY_MEASURE"}, null, false)!/>
<#assign currentCurrencyUomId = Static["com.olbius.basesales.util.SalesUtil"].getCurrentCurrencyUom(delegator)!/>
<#assign listProductFeatureType = delegator.findByAnd("ProductFeatureType", null, null, false)!/>
<#assign quantityUomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, true)!/>
<style type="text/css">
    #horizontalScrollBarjqxGridAlterUom {
        visibility: hidden !important;
    }
</style>
<script type="text/javascript">
    var taxCategoryData = [
    <#if listTaxCategory?has_content>
        <#list listTaxCategory as item>
            {
                productCategoryId: "${item.productCategoryId}",
                categoryName: "${StringUtil.wrapString(item.categoryName?if_exists)}"
            },
        </#list>
    </#if>];
    var productTypeId = "FINISHED_GOOD";
    <#if productTypeId?exists>
    productTypeId = "${productTypeId}";
    </#if>

    var quantityUomData = [
    <#if quantityUomList?has_content>
        <#list quantityUomList as item>
            {
                uomId: "${item.uomId}",
                abbreviation: "${item.abbreviation?if_exists}",
                description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
            },
        </#list>
    </#if>];
    var currencyUomData = [
    <#if listCurrencyUom?has_content>
        <#list listCurrencyUom as item>
            {
                uomId: "${item.uomId}",
                abbreviation: "${item.abbreviation?if_exists}",
                description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
            },
        </#list>
    </#if>];
    var featureTypeData = [
    <#if listProductFeatureType?has_content>
        <#list listProductFeatureType as item>
            {
                productFeatureTypeId: "${item.productFeatureTypeId}",
                description: "${StringUtil.wrapString(item.description?if_exists)}"
            },
        </#list>
    </#if>];
</script>
<#assign updateMode = false/>
<#if product?exists>
    <#assign updateMode = true/>
</#if>
<#if !copyMode?exists><#assign copyMode = false/></#if>

<#assign priceDecimalDigits = 2>
<div id="form-product-info" class="row-fluid">
    <div class="span12">
        <div class="row-fluid form-horizontal form-window-content-custom content-align-left">
            <div class="span5">
                <div class="row-fluid">
                    <div class="span4">
                        <label>${uiLabelMap.BSProductId}</label>
                    </div>
                    <div class="span8">
                        <input class="span12" type="text" id="productCode" value=""/>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="span4">
                        <label class="required">${uiLabelMap.BSProductName}</label>
                    </div>
                    <div class="span8">
                        <input class="span12" type="text" id="productName" value=""/>
                    </div>
                </div>

                <div class="row-fluid">
                    <div class="span4">
                        <label class="required">${uiLabelMap.BSTaxProductCategory}</label>
                    </div>
                    <div class="span8">
                        <div id="taxProductCategoryId"></div>
                    </div>
                </div>

            </div><!--.span4-->
            <div class="span5">
                <div class="row-fluid">
                    <div class="span4">
                        <label>${uiLabelMap.BSCurrencyUomId}</label>
                    </div>
                    <div class="span8">
                        <div id="currencyUomId"></div>
                    </div>
                </div>
                <div class="row-fluid">
                    <div class="span4">
                        <label>${uiLabelMap.BSAbbreviateName}</label>
                    </div>
                    <div class="span8">
                        <input class="span12" type="text" id="internalName" value=""/>
                    </div>
                </div>
            </div><!--.span4-->
        </div>
    </div>
</div>
<#assign permitUpdatePurUom = false>
<#if hasOlbPermission("MODULE", "PRODUCTPO_EDIT_PURUOM", "")><#assign permitUpdatePurUom = true></#if>

<script type="text/javascript">
    $(function () {
        OlbProductNewInfo.init();

        setTimeout(function () {
            $("#productCode").focus();
        }, 100);
    });
    var OlbProductNewInfo = (function () {
        var productVirtualTypeDDL;
        var parentProductDDB;
        var primaryProductCategoryCBB;
        var productCategoriesCCB;
        var brandNameCBB;
        var taxProductCategoryDDL;
        var quantityUomDDL;
        var currencyUomCBB;
        var validatorVAL;
        var featureTypeIdsCBB;
        var salesUomDDL;
        var purchaseUomDDL;

        var init = function () {
            initElement();
            initElementComplex();
            initEvent();
            initValidateForm();

            initUpdateMode();
        };

        var resetPopoverOtherUom = function () {
            salesUomDDL.clearAll();
            purchaseUomDDL.clearAll();
            $("#extendOtherUom > i").removeClass("blue");
        };
        var setValuePopoverOtherUom = function (salesUomId, purchaseUomId) {
            var iSalesUomIdSelected = salesUomId;
            var iPurchaseUomIdSelected = purchaseUomId;
            var iUomAvailableData = [
                {uomId: "", description: "---"},
            ];

            var iAlterUomData = getAlterUomDataList();
            if (iAlterUomData) {
                for (var x in iAlterUomData) {
                    var jItem = iAlterUomData[x];
                    var jUomId = jItem.uomFromId;
                    if (jUomId) {
                        for (var i = 0; i < uomData.length; i++) {
                            if (jUomId == uomData[i].uomId) {
                                iUomAvailableData.push({
                                    "uomId": jUomId,
                                    "description": uomData[i].description
                                });
                            }
                        }
                    }
                }
            }

            salesUomDDL.updateSource(null, iUomAvailableData, function () {
                salesUomDDL.selectItem([iSalesUomIdSelected]);
            });
            purchaseUomDDL.updateSource(null, iUomAvailableData, function () {
                purchaseUomDDL.selectItem([iPurchaseUomIdSelected]);
            });

            if (OlbCore.isNotEmpty(iSalesUomIdSelected) || OlbCore.isNotEmpty(iPurchaseUomIdSelected)) {
                var iTarget = $("#extendOtherUom > i");
                if (!iTarget.hasClass("blue")) {
                    iTarget.addClass("blue");
                }
            } else {
                $("#extendOtherUom > i").removeClass("blue");
            }
        };
        var initUpdateMode = function () {
        <#assign virtualProductType = "PROD_FINISH"/>
        <#if updateMode>
            <#if product.productTypeId == "AGGREGATED">
                <#assign virtualProductType = "PROD_CONFIG"/>
            <#else>
                <#if product.isVirtual == "Y">
                    <#assign virtualProductType = "PROD_VIRTUAL"/>
                </#if>
                <#if product.isVariant == "Y">
                    <#assign virtualProductType = "PROD_VARIANT"/>
                </#if>
            </#if>
        </#if>
        <#if virtualProductType == "PROD_VARIANT" && parentProductId?exists>
            $.ajax({
                type: "POST",
                url: "getChildProductFeaturesOptionAjax",
                data: {
                    "parentProductId": "${parentProductId}",
                    "productId": "${product.productId}"
                },
                beforeSend: function () {
                    $("#loader_page_common").show();
                },
                success: function (data) {
                    jOlbUtil.processResultDataAjax(data, "default", "default", function () {
                        $("#variant-feature-container").html(data);
                    });
                },
                error: function (data) {
                    alert("Send request is error");
                },
                complete: function (data) {
                    $("#loader_page_common").hide();
                }
            });
        </#if>

        <#if updateMode>
            $("#productCode").jqxInput("val", "${StringUtil.wrapString(product.productCode?if_exists)}");
            $("#productName").jqxInput("val", "${StringUtil.wrapString(product.productName?if_exists)}");
            $("#internalName").jqxInput("val", "${StringUtil.wrapString(product.internalName?if_exists)}");

            <#if featureTypeIds?has_content>
                <#list featureTypeIds as featureTypeId>
                    featureTypeIdsCBB.selectItem(["${featureTypeId}"]);
                </#list>
            </#if>
            <#if parentProductId?exists>
                parentProductDDB.getGrid().bindingCompleteListener(function () {
                    parentProductDDB.selectItem(["${parentProductId}"]);
                }, true);
            </#if>
            <#if product.primaryProductCategoryId?exists>
                primaryProductCategoryCBB.selectItem(["${product.primaryProductCategoryId}"]);
            </#if>
            <#if productCategoryIds?has_content>
                <#list productCategoryIds as productCategoryId>
                    productCategoriesCCB.selectItem(["${productCategoryId}"]);
                </#list>
            </#if>
            <#if productCategoryTaxIds?has_content>
                <#list productCategoryTaxIds as productCategoryId>
                    taxProductCategoryDDL.selectItem(["${productCategoryId}"]);
                </#list>
            </#if>
            <#if product.brandName?exists>
                brandNameCBB.selectItem(["${product.brandName}"]);
            </#if>
            <#if product.weightUomId?exists>
                weightUomDDL.selectItem(["${product.weightUomId}"]);
            </#if>
            <#if currencyUomId?exists>
                currencyUomCBB.selectItem(["${currencyUomId}"]);
            </#if>

        var iSalesUomId = <#if product.salesUomId?exists>"${product.salesUomId}"<#else>""</#if>;
        var iPurchaseUomId = <#if product.purchaseUomId?exists>"${product.purchaseUomId}"<#else>""</#if>;
            setValuePopoverOtherUom(iSalesUomId, iPurchaseUomId);
        <#else>
            <#if parameters.productCategoryId?exists>
                primaryProductCategoryCBB.selectItem(["${parameters.productCategoryId}"]);
            </#if>
        </#if>
        };
        var initElement = function () {
            jOlbUtil.input.create("#productCode", {width: '94%'});
            jOlbUtil.input.create("#productName", {width: '94%'});
            jOlbUtil.input.create("#internalName", {width: '94%'});

            setTimeout(function () {

            }, 50);
        };
        var initElementComplex = function () {
            if (typeof(uiLabelMap) == "undefined") var uiLabelMap = {};
            uiLabelMap.BSClickToChoose = "${StringUtil.wrapString(uiLabelMap.BSClickToChoose)}";
            uiLabelMap.BSProductId = "${StringUtil.wrapString(uiLabelMap.BSProductId)}";
            uiLabelMap.BSProductName = "${StringUtil.wrapString(uiLabelMap.BSProductName)}";
            var configTaxCategory = {
                width: '100%',
                height: 25,
                key: "productCategoryId",
                value: "categoryName",
                displayDetail: true,
                dropDownWidth: 400,
                autoDropDownHeight: 'auto',
                multiSelect: false,
                placeHolder: uiLabelMap.BSClickToChoose,
                useUrl: false,
                url: '',
                showClearButton: true,
            };
            taxProductCategoryDDL = new OlbDropDownList($("#taxProductCategoryId"), taxCategoryData, configTaxCategory, []);

            var configCurrencyUom = {
                width: 150,
                height: 25,
                key: "uomId",
                value: "description",
                displayDetail: false,
                dropDownWidth: 200,
                autoDropDownHeight: false,
                multiSelect: false,
                placeHolder: uiLabelMap.BSClickToChoose,
                useUrl: false,
                url: '',
                renderer: function (index, label, value) {
                    var datasource = $("#currencyUomId").jqxComboBox("source");
                    if (datasource) {
                        var datarecords = datasource.records;
                        if (datarecords) {
                            var datarecord = datarecords[index];
                            if (datarecord) {
                                return label + " [" + datarecord.abbreviation + "]";
                            }
                        }
                    }
                    return label;
                },
            };
            currencyUomCBB = new OlbComboBox($("#currencyUomId"), currencyUomData, configCurrencyUom, ["${currentCurrencyUomId?if_exists}"]);
            var alterUomData = [
            <#if configPackingAppls?has_content>
                <#list configPackingAppls as item>
                    {
                        productId: "${item.productId}",
                        uomFromId: "${item.uomFromId}",
                        uomToId: "${item.uomToId}",
                        fromDate: "${item.fromDate}",
                        thruDate: "${item.thruDate?if_exists}",
                        quantityConvert: "${item.quantityConvert?if_exists}",
                        price: "${item.price?if_exists}",
                        barcode: "${item.barcode?if_exists}",
                    },
                </#list>
            </#if>];
            if (alterUomData.length > 0) {
                $("#containerGridAlterUom").css("visibility", "visible");
            }
        };
        var initEvent = function () {
            $("#btnEditDescription").on("click", function () {
                $("#alterpopupWindowDescriptionEdit").jqxWindow("open");
            });
            $("#we_alterSave").on("click", function () {
            <#--
            if (typeof(processValue) != 'undefined') {
                var hasBegin = /^(<div><div>){1}(.)*$/.test(processValue);
                var hasEnd = /^(.)*(<\/div>(.)+<\/div>){1}$/.test(processValue);
                if (hasBegin && hasEnd) {
                    processValue = processValue.substring(5, processValue.length - 7);
                }
            } else {
                processValue = "";
            }
            -->
                $("#descriptionTxt").html(processValue);
                $("#alterpopupWindowDescriptionEdit").jqxWindow("close");
            });
            $("#we_alterCancel").on("click", function () {
                $("#alterpopupWindowDescriptionEdit").jqxWindow("close");
            });

            $("#quickAddNewUom").on("click", function () {
                if (typeof(OlbPageProdPackingUomNew) != "undefined") {
                    OlbPageProdPackingUomNew.openWindowProdUomNew();
                }
            });

            $("#wn_ppu_alterSave").on("click", function () {
                if (!OlbPageProdPackingUomNew.getValidator().validate()) return false;

                var dataMap = OlbPageProdPackingUomNew.getValue();
                $.ajax({
                    type: 'POST',
                    url: "createProductUom",
                    data: dataMap,
                    beforeSend: function () {
                        $("#loader_page_common").show();
                    },
                    success: function (data) {
                        jOlbUtil.processResultDataAjax(data, function (data, errorMessage) {
                                    $('#container').empty();
                                    $('#jqxNotification').jqxNotification({template: 'info'});
                                    $("#jqxNotification").html(errorMessage);
                                    $("#jqxNotification").jqxNotification("open");
                                    return false;
                                }, function () {
                                    $('#container').empty();
                                    $('#jqxNotification').jqxNotification({template: 'info'});
                                    $("#jqxNotification").html("${StringUtil.wrapString(uiLabelMap.wgupdatesuccess)}");
                                    $("#jqxNotification").jqxNotification("open");
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
            });

            $("#btnAddAlterUom").on("click", function () {
                OlbPageAlterUom.openWindowNew();
            });
        };
        var initValidateForm = function () {
            var extendRules = [];
            var mapRules = [
                {input: '#productCode', type: 'validCannotSpecialCharactor'},
                {input: '#productName', type: 'validInputNotNull'},
                {input: '#taxProductCategoryId', type: 'validObjectNotNull', objType: 'dropDownList'},
            ];
            validatorVAL = new OlbValidator($('#form-product-info'), mapRules, extendRules, {position: 'bottom'});
        <#--{input: '#productCode', type: 'validInputNotNull'},-->
        };
        var getValidator = function () {
            return validatorVAL;
        };
        var clearNumberInput = function (element) {
            jOlbUtil.numberInput.clear(element);
        };

        var getObj = function () {
            return {
                "productVirtualTypeDDL": productVirtualTypeDDL,
                "parentProductDDB": parentProductDDB,
                "primaryProductCategoryCBB": primaryProductCategoryCBB,
                "productCategoriesCCB": productCategoriesCCB,
                "brandNameCBB": brandNameCBB,
                "taxProductCategoryDDL": taxProductCategoryDDL,
                "weightUomDDL": weightUomDDL,
                "quantityUomDDL": quantityUomDDL,
                "currencyUomCBB": currencyUomCBB,
                "featureTypeIdsCBB": featureTypeIdsCBB,
                "salesUomDDL": salesUomDDL,
                "purchaseUomDDL": purchaseUomDDL,
            };
        };
        var getValue = function () {
            // miss: displayColor, feature
            // miss: dayN, shelflife, taxInPrice, uomFromId1, quantityConvert1, thruDate1

            var dataMap = {};
            dataMap.currencyUomId = currencyUomCBB.getValue();
            dataMap.productTypeId = productTypeId;
            dataMap.quantityUomId = 'EA';
            dataMap.weightUomId = 'WT_g';
            dataMap.weight = 10;
            dataMap.productWeight = 10;
            dataMap.originGeoId = 'VNM';

            var taxProductCategoryId = taxProductCategoryDDL.getValue();
            if (taxProductCategoryId == null) taxProductCategoryId = "";
            dataMap.taxProductCategoryId = taxProductCategoryId;

            dataMap.productCode = $("#productCode").val();
            dataMap.productName = $("#productName").val();
            dataMap.internalName = $("#internalName").val();

            dataMap.productDefaultPrice = 0;
            dataMap.productListPrice = 0;
            dataMap.isPriceIncludedVat = "Y";

            var isVirtual = "N";
            var isVariant = "N";
            dataMap.isVirtual = isVirtual;
            dataMap.isVariant = isVariant;

            var featureIds = new Array();
            $('[id^="featureProduct_"]').each(function (i, obj) {
                var featureId = $(obj).jqxDropDownList('getSelectedItem');
                if (OlbCore.isNotEmpty(featureId)) {
                    featureIds.push(featureId.value);
                }
            });
            dataMap.featureIds = featureIds;
            return dataMap;
        };
        return {
            init: init,
            getObj: getObj,
            getValidator: getValidator,
            clearNumberInput: clearNumberInput,
            resetPopoverOtherUom: resetPopoverOtherUom,
            getValue: getValue,
        };
    }());
</script>