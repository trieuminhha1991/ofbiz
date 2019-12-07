<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/logresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/logresources/js/popup_extend_grid.js"></script>
<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script>
var sourceTimePeriod = [
                        { text: "${StringUtil.wrapString(uiLabelMap.CommonToday)}", value: "TODAY" },
                        { text: "${StringUtil.wrapString(uiLabelMap.WorkEffortThisWeek)}", value: "THISWEEK" },
                        { text: "${StringUtil.wrapString(uiLabelMap.WorkEffortThisMonth)}", value: "THISMONTH" },
                        { text: "${StringUtil.wrapString(uiLabelMap.CommonOptions)}", value: "OPTIONS" }
                ];
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

<#assign facilitys = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", ownerPartyId)), null, null, null, false)>
var faciData = new Array();
<#list facilitys as item>
	var row = {};
	<#assign facilityName = StringUtil.wrapString(item.facilityName?if_exists)/>
	row['facilityId'] = '${item.facilityId?if_exists}';
	row['facilityName'] = '${facilityName?if_exists}';
	faciData[${item_index}] = row;
</#list>

<#assign enumerations = delegator.findList("Enumeration", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition("enumTypeId", "SALES_METHOD_CHANNEL"), null, null, null, false)>
var listEnumerations = [
					<#if enumerations?exists>
						<#list enumerations as item>
							{
								enumId: "${item.enumId?if_exists}",
								description: "${StringUtil.wrapString(item.get("description", locale)?if_exists)}"
							},
						</#list>
					</#if>
                      ];

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
<#assign productStores = delegator.findList("ProductStore", null, null, null, null, false)>
var listProductStores = [
					<#if productStores?exists>
						<#list productStores as item>
							{
								productStoreId: "${item.productStoreId?if_exists}",
								storeName: "${StringUtil.wrapString(item.get("storeName", locale)?if_exists)}"
							},
						</#list>
					</#if>
                      ];

<#assign uoms = delegator.findList("Uom", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("uomTypeId", "PRODUCT_PACKING")), null, null, null, false)>
var uomData = [
	<#if uoms?exists>
		<#list uoms as item>
			{
				uomId: "${item.uomId?if_exists}",
				<#assign s = StringUtil.wrapString(item.get("description", locale)?if_exists)/>
			description: "${s}"
			},
		</#list>
	</#if>
];

var mapUomData = {
		<#if uoms?exists>
			<#list uoms as item>
				<#assign s1 = StringUtil.wrapString(item.get("description", locale)?if_exists)/>
				"${item.uomId?if_exists}": "${s1}",
			</#list>
		</#if>	
};
</script>
<script type="text/javascript" id="test">
$( document ).ready(function() {
	loadProduct();
});

function loadProduct(){
	var listProductDataSource = [];	
	var listFacilityDataSource = [];
	var listProductCategoryDataSource =[];
	var listEnumerationDataSource = [];
	for(var x in productData){
		var productDataSource = {
			text: productData[x].productName,
			value: productData[x].productId,
		}
		listProductDataSource.push(productDataSource);
	}
	for(var i in faciData){
		var facilityDataSource = {
			text: faciData[i].facilityName,
			value: faciData[i].facilityId,
		}
		listFacilityDataSource.push(facilityDataSource);
	}
	for(var i in listProductCategorys){
		var productCategorys = {
			text: listProductCategorys[i].categoryName,
			value: listProductCategorys[i].productCategoryId,
		}
		listProductCategoryDataSource.push(productCategorys);
	}
	for(var i in listEnumerations){
		var enumDataSource = {
			text: listEnumerations[i].description,
			value: listEnumerations[i].enumId,
		}
		listEnumerationDataSource.push(enumDataSource);
	}
	bindingDataToReport(listProductDataSource, listFacilityDataSource, listProductCategoryDataSource, listEnumerationDataSource);
}

function bindingDataToReport(listProductDataSource, listFacilityDataSource, listProductCategoryDataSource, listEnumerationDataSource){
	var config = {
            title: ' ${uiLabelMap.LogReportWareHourse}',
            service: 'facilityInventory',
            columns: [
                { text: '${uiLabelMap.ReceivedDate}', datafield: 'date', type: 'olapDate', width: 180,
                	cellsrenderer: function (row, column, value) {
                		value = value?new Date(value).toTimeOlbius():value;
				        return '<div>' + value + '</div>';
				    }
				},
                { text: '${uiLabelMap.ProductId}', datafield: 'productId', width: 150 },
                { text: '${uiLabelMap.ProductName}', datafield: 'productName', width: 250 },
		      	{ text: '${uiLabelMap.ProductManufactureDate}', datafield: 'datetimeManufactured', width: 150,
                	cellsrenderer: function (row, column, value) {
                		if (value) {
                			value = value.time?new Date(value.time).toTimeOlbius():value;
						}
				        return '<div>' + value + '</div>';
				    }
		      	},
				{ text: '${uiLabelMap.ExpireDate}', datafield: 'expireDate',  align: 'center', width: 150,
		      		cellsrenderer: function (row, column, value) {
                		if (value) {
                			value = value.time?new Date(value.time).toTimeOlbius():value;
						}
				        return '<div>' + value + '</div>';
				    }
				},
				{ text: '${uiLabelMap.Batch}', datafield: 'lotId',  align: 'center', width: 100 },
                { text: '${uiLabelMap.ReceivedQuantity}', datafield: 'quantityOnHandTotal', width: 120, type: 'number', cellsalign: 'right' },
  		      	{ text: '${uiLabelMap.QuantityUomId}', datafield: 'uomId', width: 150, cellsalign: 'right',
					cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
						if(value) {
							return '<span>' + mapUomData[value] + '</span>';
						}
					}
                },
  		      	{ text: '${uiLabelMap.LogFacilityName}', datafield: 'facilityName', width: 200 },
  		      	{ text: '${uiLabelMap.BLCategoryProduct}', datafield: 'categoryName',  width: 200 }
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
	            id : 'facilityId',
	            title1: '${StringUtil.wrapString(uiLabelMap.FacilityId)}',
	            title2: '${StringUtil.wrapString(uiLabelMap.LogFacilityName)}',  
	            label : '${StringUtil.wrapString(uiLabelMap.LogWarehouse)}',
	            data : listFacilityDataSource,
	            value: []
	        }]
        },
        {
        	action : 'addJqxGridMultil',
	        params : [{
	        	id : 'categoryId',
	        	title1: '${StringUtil.wrapString(uiLabelMap.productCategoryId)}',
	            title2: '${StringUtil.wrapString(uiLabelMap.CategoryName)}',  
	            label : '${StringUtil.wrapString(uiLabelMap.DmsProductCatalogs)}',
	            data : listProductCategoryDataSource,
	            value: []
	        }]
        },
        /*{
            action : 'addDropDownListMultil',
            params : [{
                id : 'enumId',
                label : '${StringUtil.wrapString(uiLabelMap.LogPurchaseChannels)}',
                data : [{text:'${StringUtil.wrapString(uiLabelMap.FullOptionFilter)}', value: null}].concat(listEnumerationDataSource),
                index: 0,
            }]
        },*/
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
		},
    ];

    var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'jqGetListReceiveWarehouseReportOlap', true);

    testGrid.funcUpdate(function (oLap) {
        oLap.update({
            'dateType': oLap.val('dateType'),
            'fromDate': oLap.val('from_date'),
            'thruDate': oLap.val('thru_date'),
            'productId': oLap.val('productId'),
            'facilityId': oLap.val('facilityId'),
            'categoryId': oLap.val('categoryId'),
            'timePeriod': oLap.val('timePeriod')
        });
    });
    
    testGrid.init(function () {
        testGrid.runAjax();
    }, function(oLap){
    	var dataAll = oLap.getAllData();
    	if(dataAll.length != 0){
    		var dateTypeInput = oLap.val('dateType');
        	var fromDateInput = oLap.val('from_date');
        	var thruDateInput = oLap.val('thru_date');
        	var productIdInput = oLap.val('productId');
        	var facilityIdInput = oLap.val('facilityId');
        	var categoryIdInput = oLap.val('categoryId');
        	var dateFromDate = new Date(fromDateInput);
        	var dateThruDate = new Date(thruDateInput);
        	var dateFrom = dateFromDate.getTime();
        	var thruFrom = dateThruDate.getTime();
        	window.location.href = "exportReceiveWarehouseOlapLogToPdf?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&productId=" + productIdInput + "&facilityId=" + facilityIdInput + "&categoryId=" + categoryIdInput;
    	}else{
    		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
    		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
    		    "class" : "btn-small btn-primary width60px",
    		    }]
    		   );
    	}
    }, function(oLap){
    	var dataAll = oLap.getAllData();
    	if(dataAll.length != 0){
    		var dateTypeInput = oLap.val('dateType');
        	var fromDateInput = oLap.val('from_date');
        	var thruDateInput = oLap.val('thru_date');
        	var productIdInput = oLap.val('productId');
        	var facilityIdInput = oLap.val('facilityId');
        	var categoryIdInput = oLap.val('categoryId');
        	var dateFromDate = new Date(fromDateInput);
        	var dateThruDate = new Date(thruDateInput);
        	var dateFrom = dateFromDate.getTime();
        	var thruFrom = dateThruDate.getTime();
        	window.location.href = "exportReceiveWarehouseOlapLogToExcel?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&productId=" + productIdInput + "&facilityId=" + facilityIdInput + "&categoryId=" + categoryIdInput;
    	}else{
    		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
    		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
    		    "class" : "btn-small btn-primary width60px",
    		    }]
    		   );
    	}
    });
    
}
</script>


<script type="text/javascript" id="receiveReportPieChart"> 
$(function () {
	var optionFilterDataType = [
	    {
		  text: '${uiLabelMap.DmsProductCatalogs}',
		  value: "FILTER_CATALOG" 
        },                     
	]
	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);
    var config = {
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: '${StringUtil.wrapString(uiLabelMap.LogRateReceiveWarehouse)}'
        },
        tooltip: {
            pointFormat: '<b>{point.percentage:.1f}%: {point.y}</b>'
        },
        series: [{
            type: 'pie'
        }],
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    format: '<b>{point.name}</b>: {point.percentage:.1f} %',
                    style: {
                        color: (Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black'
                    }
                }
            }
        }
    };

    var configPopup = [
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
        },
        {
            action : 'addDropDownList',
            params : [{
                id : 'filterTypeId',
                label : '${StringUtil.wrapString(uiLabelMap.KStatistic)}',
                data : optionFilterDataType,
                index: 0
            }],
        },
    ];
    var receiveReportPieChart = OLBIUS.oLapChart('receiveReportPieChart', config, configPopup, 'receiveWarehouseReportPieChart', true, true, OLBIUS.defaultPieFunc);

    receiveReportPieChart.funcUpdate(function(oLap) {
		
        oLap.update({
            'fromDate': oLap.val('from_date'),
            'thruDate': oLap.val('thru_date'),
            'filterTypeId': oLap.val('filterTypeId'),
        });
    });

    receiveReportPieChart.init(function () {
    	receiveReportPieChart.runAjax();
    });
});
</script>