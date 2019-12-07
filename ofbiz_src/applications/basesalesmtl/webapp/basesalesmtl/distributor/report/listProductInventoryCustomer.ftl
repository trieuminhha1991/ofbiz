<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/logresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/logresources/js/popup_extend_grid.js"></script>
<script>
//Prepare for product data
<#assign ownerPartyId = Static["com.olbius.basehr.util.PartyUtil"].getCurrentOrganization(delegator, userLogin.userLoginId)/>
<#assign products = delegator.findList("Product", null, null, null, null, false)>
var productData = [
	<#if products?exists>
		<#list products as item>
			{
				productId: "${item.productCode?if_exists}",
				productName: "${StringUtil.wrapString(item.get("productName", locale)?if_exists)}"
			},
		</#list>
	</#if>
];

var mapProducData = {
	<#if products?exists>
		<#list products as item>
			<#assign s1 = StringUtil.wrapString(item.get("productName", locale)?if_exists)/>
			"${item.productId?if_exists}": "${s1}",
		</#list>
	</#if>	
};

<#assign productCategorys = delegator.findList("ProductCategory", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("productCategoryTypeId", "CATALOG_CATEGORY"), null, null, null, false)>
var listProductCategorys = [
	<#if productCategorys?exists>
		<#list productCategorys as item>
			{
				productCategoryId: "${item.productCategoryId?if_exists}",
				categoryName: "${StringUtil.wrapString(item.get("categoryName", locale)?if_exists)}"
			},
		</#list>
	</#if>
];

var sourceTimePeriod = [
                        { text: "${StringUtil.wrapString(uiLabelMap.CommonToday)}", value: "TODAY" },
                        { text: "${StringUtil.wrapString(uiLabelMap.WorkEffortThisWeek)}", value: "THISWEEK" },
                        { text: "${StringUtil.wrapString(uiLabelMap.WorkEffortThisMonth)}", value: "THISMONTH" },
                        { text: "${StringUtil.wrapString(uiLabelMap.CommonOptions)}", value: "OPTIONS" }
                ];
</script>
<script type="text/javascript" id="test">
$( document ).ready(function() {
	loadProduct();
});

function loadProduct(){
	var listProductDataSource = [];	
	var listProductCategoryDataSource = [];
	for(var x in productData){
		var productDataSource = {
			text: productData[x].productName,
			value: productData[x].productId,
		}
		listProductDataSource.push(productDataSource);
	}
	
	for(var i in listProductCategorys){
		var productCategorys = {
			text: listProductCategorys[i].categoryName,
			value: listProductCategorys[i].productCategoryId,
		}
		listProductCategoryDataSource.push(productCategorys);
	}
	bindingDataToReport(listProductDataSource, listProductCategoryDataSource);
}

function bindingDataToReport(listProductDataSource, listProductCategoryDataSource){
	var config = {
			filterable: true,
			showfilterrow: true,
            title: ' ${uiLabelMap.BSInventoryReport}',
            service: 'productInventoryCustomer',
            columns: [
						{ text: '${uiLabelMap.BSDate}', datafield: 'date', type: 'olapDate', align: 'center', width: 150,
							cellsrenderer: function (row, column, value) {
								value = value?value.timeStampToTimeOlbius():value;
						        return '<div>' + value + '</div>';
						    }
						},
						{ text: '${uiLabelMap.BSCustomerId}', datafield: 'partyCode', type: 'string', width: 150 },
						{ text: '${uiLabelMap.BSCustomerName}', datafield: 'partyName', type: 'string', width: 250 },
						{ text: '${uiLabelMap.ProductCode}', datafield: 'productCode', type: 'string', width: 150 },
						{ text: '${uiLabelMap.ProductName}', datafield: 'productName', type: 'string', width: 250 },
						{ text: '${uiLabelMap.BSProductCatalogs}', datafield: 'categoryName', type: 'string', width: 200},
						{ text: '${uiLabelMap.BSQuantity}', datafield: 'quantity', type: 'number', width: 150, filtertype: 'number',
							cellsrenderer: function (row, column, value) {
						        return '<div class=\"text-right\">' + value.toLocaleString(locale) + '</div>';
						    }
						}
	            ]
	        };
	
	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);
    var configPopup = [
		{
		    action : 'addJqxGridMultil',
		    params : [{
		        id : 'productId',
		        title1: '${StringUtil.wrapString(uiLabelMap.ProductCode)}',
		        title2: '${StringUtil.wrapString(uiLabelMap.ProductName)}',  
		        label : '${StringUtil.wrapString(uiLabelMap.POProduct)}',
		        data : listProductDataSource,
		        value: []
		    }]
		},
		{
		    action : 'addJqxGridMultil',
		    params : [{
		        id : 'categoryId',
		        title1: '${StringUtil.wrapString(uiLabelMap.CategoryId)}',
		        title2: '${StringUtil.wrapString(uiLabelMap.BSCategoryName)}',  
		        label : '${StringUtil.wrapString(uiLabelMap.BSProductCatalogs)}',
		        data : listProductCategoryDataSource,
		        value: []
		    }]
		},
        {
        	action : 'addDropDownList',
        	params : [{
        		id : 'timePeriod',
        		label : '${StringUtil.wrapString(uiLabelMap.CommonTime)}',
        		data : sourceTimePeriod,
        		index: 3
        	}],
        	event: function(popup) {
                popup.onEvent('timePeriod', 'select', function(event){
                    var args = event.args;
                    var item = popup.item('timePeriod', args.index);
                    var filter = item.value;
                    if(filter != 'OPTIONS') {
                        popup.hide('from_date');
                        popup.hide('thru_date');
                    } else {
                    	popup.show('from_date');
                        popup.show('thru_date');
                    }
                    popup.resize();
                });
            }
        },
        {
		    action : 'addDateTimeInput',
			params : [{
			    id : 'from_date',
			    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
			    value: OLBIUS.dateToString(currentFirstDay)
			}],
			before: 'thru_date'
		},
		{
		    action : 'addDateTimeInput',
			params : [{
			    id : 'thru_date',
			    label : '${StringUtil.wrapString(uiLabelMap.olap_thruDate)}',
			    value: OLBIUS.dateToString(cur_date)
			}],
			after: 'from_date'
		}
    ];

    var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'jqGetListProductInventoryCustomerOlap', true);

    testGrid.funcUpdate(function (oLap) {
        oLap.update({
            'fromDate': oLap.val('from_date'),
            'thruDate': oLap.val('thru_date'),
            'productId': oLap.val('productId'),
            'categoryId': oLap.val('categoryId'),
            'timePeriod': oLap.val('timePeriod')
        }, oLap.val('dateType'));
    });
    
    testGrid.init(function () {
        testGrid.runAjax();
    });
}
</script>