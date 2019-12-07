<#include "component://basecrm/webapp/basecrm/callcenter/ftLlibraryVariable.ftl"/>
<@jqGridMinimumLib/>
<@jqOlbCoreLib hasGrid=true hasValidator=true/>
<#assign facilities = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", userLogin.get("partyId"))), null, null, null, false) />
<#assign returnReasons = delegator.findList("ReturnReason", null, null, null, null, false) />
<#assign uomList = delegator.findByAnd("Uom", {"uomTypeId" : "PRODUCT_PACKING"}, null, false)/>

<script type="text/javascript">
    var dataSelected = {};
    var uomData = [<#if uomList?exists><#list uomList as uomItem>{
        uomId: '${uomItem.uomId}', description: '${StringUtil.wrapString(uomItem.get("description", locale))}'
    },</#list></#if>];
    var facilities = [<#if facilities?exists><#list facilities as item>{
        facilityId: "${item.facilityId?if_exists}", facilityName: "${StringUtil.wrapString(item.facilityName?if_exists)}"
    },</#list></#if>];
    var returnReasons = [<#if returnReasons?exists><#list returnReasons as item>{
        returnReasonId: "${item.returnReasonId?if_exists}", description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
    },</#list></#if>];
    var mapReturnReason = {<#if returnReasons?exists><#list returnReasons as item>
        "${item.returnReasonId?if_exists}": "${StringUtil.wrapString(item.get("description", locale)?if_exists)}",
    </#list></#if>};

    var parentProductIds = {};
    var cellClass${gridProductItemsId} = function (row, columnfield, value) {
        var data = $('#${gridProductItemsId}').jqxGrid('getrowdata', row);
        var returnValue = "";
        if (typeof(data) != 'undefined') {
            if (typeof(data.parentProductId) != 'undefined' && typeof(data.colorCode) != 'undefined') {
                var parentProductId = data.parentProductId;
                if (parentProductId != null && !(/^\s*$/.test(parentProductId))) {
                    if (typeof(parentProductIds[parentProductId]) == 'undefined') {
                        var newColor = '' + data.colorCode;//''+(0x1000000+(Math.random())*0xffffff).toString(16).substr(1,6);
                        var className = newColor.replace("#", "");
                        parentProductIds[parentProductId] = "background-" + className;
                        $("<style type='text/css'> .background-" + className + "{background-color:" + newColor + " !important} </style>").appendTo("head");
                        returnValue += "background-" + className;
                    } else {
                        returnValue += parentProductIds[parentProductId];
                    }
                }
            }
            if (typeof(data.productAvailable) != 'undefined') {
                if (data.productAvailable == 'true') {
                    returnValue += " row-cell-success";
                } else if (data.productAvailable == 'false') {
                    returnValue += " row-cell-error";
                }
            }
            return returnValue;
        }
    }
    if (productOrderMap == undefined) var productOrderMap = {};
    <#if shoppingCart?exists>
        <#assign orderItems = shoppingCart.makeOrderItems()>
        <#if orderItems?exists>
            <#list orderItems as orderItem>
                <#if (orderItem.productId?exists) && ("PRODPROMO_ORDER_ITEM" == orderItem.orderItemTypeId || !(orderItem.isPromo?exists) || orderItem.isPromo?string == "N")>
                if (typeof(productOrderMap['${orderItem.productId?default('_NA_')}@${orderItem.quantityUomId?default('_NA_')}']) != 'undefined') {
                    var itemValue = productOrderMap['${orderItem.productId?default('_NA_')}@${orderItem.quantityUomId?default('_NA_')}'];
                    <#if "PRODPROMO_ORDER_ITEM" == orderItem.orderItemTypeId>
                        itemValue.quantityReturnPromo = '${orderItem.alternativeQuantity?default(orderItem.quantity)}';
                    <#else>
                        itemValue.quantity = '${orderItem.alternativeQuantity?default(orderItem.quantity)}';
                    </#if>
                    productOrderMap['${orderItem.productId?default('_NA_')}@${orderItem.quantityUomId?default('_NA_')}'] = itemValue;
                } else {
                    productOrderMap['${orderItem.productId?default('_NA_')}@${orderItem.quantityUomId?default('_NA_')}'] = {
                        productId : '${orderItem.productId?default("")}',
                        quantityUomId : '${orderItem.quantityUomId?default("")}',
                        <#if "PRODPROMO_ORDER_ITEM" == orderItem.orderItemTypeId>
                            quantityReturnPromo : '${orderItem.alternativeQuantity?default(orderItem.quantity)}',
                        <#else>
                            quantity : '${orderItem.alternativeQuantity?default(orderItem.quantity)}',
                        </#if>
                    };
                }
                </#if>
            </#list>
        </#if>
    </#if>

    var uiLabelMap = {
        BSAreYouSureYouWantToReceiveReturn : "${uiLabelMap.BSAreYouSureYouWantToReceiveReturn}",
        BSYouNotYetChooseRecord : "${uiLabelMap.BSYouNotYetChooseRecord}?"
    };
</script>
<script src="/salesmtlresources/js/distributor/requirement/receiveReturn.js?v=1.0.0"></script>