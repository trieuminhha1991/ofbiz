<script type="text/javascript" src="/crmresources/js/miscUtil.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxeditor.js"></script>
<script type="text/javascript" src="/aceadmin/jqw/jqwidgets/jqxvalidator.js"></script> 
<script type="text/javascript" src="/aceadmin/assets/js/bootbox.min.js"></script>
<script type="text/javascript" src="/logresources/js/popup.extend.js"></script>
<script type="text/javascript" src="/logresources/js/popup_extend_grid.js"></script>
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

var mapProducData = {
	<#if products?exists>
		<#list products as item>
			<#assign s1 = StringUtil.wrapString(item.get("productName", locale)?if_exists)/>
			"${item.productId?if_exists}": "${s1}",
		</#list>
	</#if>	
};

<#assign facilitys = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE", "ownerPartyId", ownerPartyId)), null, null, null, false)>
var faciData = new Array();
<#list facilitys as item>
	var row = {};
	<#assign facilityName = StringUtil.wrapString(item.facilityName?if_exists)/>
	row['facilityId'] = '${item.facilityId?if_exists}';
	row['facilityName'] = '${facilityName?if_exists}';
	faciData[${item_index}] = row;
</#list>

function getDescriptionByFacilityId(facilityId) {
	for ( var x in faciData) {
		if (facilityId == faciData[x].facilityId) {
			return faciData[x].facilityName;
		}
	}
}

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
var mapEnumerationData = {
		<#if enumerations?exists>
			<#list enumerations as item>
				<#assign s1 = StringUtil.wrapString(item.get("description", locale)?if_exists)/>
				"${item.enumId?if_exists}": "${s1}",
			</#list>
		</#if>	
};

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
var mapProductStoresData = {
		<#if productStores?exists>
			<#list productStores as item>
				<#assign s1 = StringUtil.wrapString(item.get("storeName", locale)?if_exists)/>
				"${item.productStoreId?if_exists}": "${s1}",
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
var mapProductCategoryData = {
		<#if productCategorys?exists>
			<#list productCategorys as item>
				"${item.productCategoryId?if_exists}": "${StringUtil.wrapString(item.get("categoryName", locale)?if_exists)}",
			</#list>
		</#if>	
};

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
	var listEnumerationDataSource = [];
	var listProductCategoryDataSource = [];
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
	for(var i in listEnumerations){
		var enumDataSource = {
			text: listEnumerations[i].description,
			value: listEnumerations[i].enumId,
		}
		listEnumerationDataSource.push(enumDataSource);
	}
	for(var i in listProductCategorys){
		var productCategorys = {
			text: listProductCategorys[i].categoryName,
			value: listProductCategorys[i].productCategoryId,
		}
		listProductCategoryDataSource.push(productCategorys);
	}
	bindingDataToReport(listProductDataSource, listFacilityDataSource, listEnumerationDataSource, listProductCategoryDataSource);
}

function bindingDataToReport(listProductDataSource, listFacilityDataSource, listEnumerationDataSource, listProductCategoryDataSource){
	var config = {
            title: ' ${uiLabelMap.LogReportExportWarehouse}',
            service: 'facilityInventory',
            columns: [
						{ text: '${uiLabelMap.SequenceId}', sortable: false, filterable: false, editable: false, pinned: true,
							  groupable: false, draggable: false, resizable: false,
							  datafield: '', columntype: 'number', width: 50,
							  cellsrenderer: function (row, column, value) {
								  return '<div style=margin:4px;>' + (value + 1) + '</div>';
							  }
						},  
						{ text: '${uiLabelMap.ActualExportedDate}', datafield: 'date', type: 'olapDate',  align: 'center', cellsalign: 'left', width: 200},
						{ text: '${uiLabelMap.LogDateTimeRequirement}', datafield: 'fullDeliveryDate', type: 'string',  align: 'center', cellsalign: 'left', width: 180,},
						{ text: '${uiLabelMap.LogFacilityName}', datafield: 'facilityName', width: 150, 
						},
						{ text: '${uiLabelMap.DmsSalesChannel}', datafield: 'enumId', type: 'string', width: 150, 
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							  if(value){
								  if(mapEnumerationData[value] != undefined){
									  return '<span>' + mapEnumerationData[value] + '</span>';
								  }else{
									  if(mapProductStoresData[value] != undefined){
										  return '<span>' + mapProductStoresData[value] + '</span>';
									  }else{
										  return '<span>' + value + '</span>';
									  }
								  }
							  }
							}
						},
						{ text: '${uiLabelMap.DeliveryId}', datafield: 'deliveryId', type: 'string',  width: 120, 
						},
						{ text: '${uiLabelMap.ProductCode}', datafield: 'productId', width: 120, type: 'string',  
						},
						{ text: '${uiLabelMap.ProductName}', datafield: 'productName', width: 350, type: 'string', 
						},
						{ text: '${uiLabelMap.ProductManufactureDate}', datafield: 'datetimeManufactured', type: 'string',  align: 'center', cellsalign: 'left', width: 150,},
						{ text: '${uiLabelMap.ProductExpireDate}', datafield: 'expireDate', type: 'string',  align: 'center', cellsalign: 'left', width: 150,},
						{ text: '${uiLabelMap.Batch}', datafield: 'lotId', type: 'string',  align: 'center', cellsalign: 'left', width: 100,},
						{  text: '${uiLabelMap.LogExportQuantity}', datafield: 'quantityOnHandTotal', width: 120, type: 'number', cellsalign: 'right',
						},
						{ text: '${uiLabelMap.QuantityUomId}', datafield: 'uomId',  width: 120, 
							cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
							  if(value){
								  return '<span>' + mapUomData[value] + '</span>';
							  }
							}
						},
						{ text: '${uiLabelMap.BLCategoryProduct}', datafield: 'categoryName',  width: 300, 
						},
	            ], 
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
	            value: [],
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
        /*{
	        action : 'addJqxGridMultil',
	        params : [{
	            id : 'enumId',
	            title1: '${StringUtil.wrapString(uiLabelMap.LogCodeChannel)}',
	            title2: '${StringUtil.wrapString(uiLabelMap.SalesChannel)}',  
	            label : '${StringUtil.wrapString(uiLabelMap.DmsSalesChannel)}',
	            data : listEnumerationDataSource,
	            value: []
	        }]
        },*/
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

    var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'jqGetListExporteWarehouseReportOlap', true);

    testGrid.funcUpdate(function (oLap) {
        oLap.update({
            'dateType': oLap.val('dateType'),
            'fromDate': oLap.val('from_date'),
            'thruDate': oLap.val('thru_date'),
            'productId': oLap.val('productId'),
            'facilityId': oLap.val('facilityId'),
            /*'enumId': oLap.val('enumId'),*/
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
        	var enumIdInput = oLap.val('enumId');
        	var categoryIdInput = oLap.val('categoryId');
        	var dateFromDate = new Date(fromDateInput);
        	var dateThruDate = new Date(thruDateInput);
        	var dateFrom = dateFromDate.getTime();
        	var thruFrom = dateThruDate.getTime();
        	window.location.href = "exportExportWarehouseOlapLogToPdf?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&productId=" + productIdInput + "&facilityId=" + facilityIdInput + "&enumId=" + enumIdInput + "&categoryId=" + categoryIdInput;
    	}
    	else{
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
        	var enumIdInput = oLap.val('enumId');
        	var categoryIdInput = oLap.val('categoryId');
        	var dateFromDate = new Date(fromDateInput);
        	var dateThruDate = new Date(thruDateInput);
        	var dateFrom = dateFromDate.getTime();
        	var thruFrom = dateThruDate.getTime();
        	window.location.href = "exportExportWarehouseOlapLogToExcel?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&productId=" + productIdInput + "&facilityId=" + facilityIdInput + "&enumId=" + enumIdInput + "&categoryId=" + categoryIdInput;
    	}
    	else{
    		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
    		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
    		    "class" : "btn-small btn-primary width60px",
    		    }]
    		   );
    	}
    });
    
}
</script>


<script type="text/javascript" id="exportReportPieChart"> 
$(function () {
	var optionFilterDataType = [
	      /*{
	    	  text: '${uiLabelMap.DmsSalesChannel}',
	    	  value: "FILTER_CHANEL" 
	      },*/
	      {
	    	  text: '${uiLabelMap.DmsProductCatalogs}',
	    	  value: "FILTER_CATALOG" 
	      },
	]
    var config = {
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: '${StringUtil.wrapString(uiLabelMap.LogRateExportWarehouse)}'
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
	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), 1);
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
    var exportReportPieChart = OLBIUS.oLapChart('exportReportPieChart', config, configPopup, 'exportWarehouseReportPieChart', true, true, OLBIUS.defaultPieFunc);

    exportReportPieChart.funcUpdate(function(oLap) {
		
        oLap.update({
            'fromDate': oLap.val('from_date'),
            'thruDate': oLap.val('thru_date'),
            'filterTypeId': oLap.val('filterTypeId'),
        });
    });

    exportReportPieChart.init(function () {
    	exportReportPieChart.runAjax();
    });
});

</script>