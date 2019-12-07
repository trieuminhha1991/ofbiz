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

<#assign facilitys = delegator.findList("Facility", Static["org.ofbiz.entity.condition.EntityCondition"].makeCondition(Static["org.ofbiz.base.util.UtilMisc"].toMap("facilityTypeId", "WAREHOUSE")), null, null, null, false)>
var faciData = new Array();
<#list facilitys as item>
	var row = {};
	<#assign facilityName = StringUtil.wrapString(item.facilityName?if_exists)/>
	<#assign checkDistributor = Static["com.olbius.basesales.util.SalesPartyUtil"].isDistributor(delegator, item.facilityId?if_exists)/>
	<#if checkDistributor>
		row['facilityId'] = '${item.facilityId?if_exists}';
		row['facilityName'] = '${facilityName?if_exists}';
		faciData[${item_index}] = row;
	</#if>
</#list>


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
</script>
<script type="text/javascript" id="test">
$( document ).ready(function() {
	loadProduct();
});

function loadProduct(){
	var listProductDataSource = [];	
	var listFacilityDataSource = [];
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
	for(var i in listProductCategorys){
		var productCategorys = {
			text: listProductCategorys[i].categoryName,
			value: listProductCategorys[i].productCategoryId,
		}
		listProductCategoryDataSource.push(productCategorys);
	}
	bindingDataToReport(listProductDataSource, listFacilityDataSource, listProductCategoryDataSource);
}

function bindingDataToReport(listProductDataSource, listFacilityDataSource, listProductCategoryDataSource){
	var config = {
            title: ' ${uiLabelMap.LogInventoryReportDistributors}',
            service: 'facilityInventory',
            columns: [
				{ text: '${uiLabelMap.LogFacilityName}', datafield: 'facilityId', width: 180 },
                { text: '${uiLabelMap.LogDate}', datafield: 'date', type: 'olapDate', width: 150,  align: 'center' },
                { text: '${uiLabelMap.ProductCode}', datafield: 'productCode', width: 150, type: 'string' },
                { text: '${uiLabelMap.ProductName}', datafield: 'productName', width: 350, type: 'string' }, 
                { text: '${uiLabelMap.ProductManufactureDate}', datafield: 'datetimeManufactured', width: 150, type: 'string' },
                { text: '${uiLabelMap.ExpireDate}', datafield: 'expireDate', width: 150, type: 'string',  cellsalign: 'right' }, 
                { text: '${uiLabelMap.Lifetime}', datafield: 'dateLife', width: 200, type: 'string', cellsalign: 'right' }, 
                {  text: '${uiLabelMap.QuantityOnHandTotal}', datafield: 'inventoryTotal', width: 200, type: 'number', cellsalign: 'right' },
                {  text: '${uiLabelMap.AvailableToPromiseTotal}', datafield: 'availableToPromiseTotal', width: 250, type: 'number', cellsalign: 'right' },
                {  text: '${uiLabelMap.QuantityUomId}', datafield: 'currencyId', width: 150, cellsalign: 'right',
                	cellsrenderer: function (row, columnfield, value, defaulthtml, columnproperties) {
		    		  if(value){
		    			  return '<span>' + mapUomData[value] + '</span>';
		    		  }
		          	}
                },
                { text: '${uiLabelMap.BLCategoryProduct}', datafield: 'categoryName', width: 300 },
            ]
        };
	
	var dateCurrent = new Date();
	var currentFirstDay = new Date(dateCurrent.getFullYear(), dateCurrent.getMonth(), dateCurrent.getDate() -1);
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
                id : 'dateType',
                label : '${StringUtil.wrapString(uiLabelMap.CommonPeriod)}',
                data : date_type_source,
                index: 0
            }]
        },
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
    ];

    var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'jqGetListInventoryReportDistributorOlap', true);

    testGrid.funcUpdate(function (oLap) {
        oLap.update({
            'dateType': oLap.val('dateType'),
            'fromDate': oLap.val('from_date'),
            'thruDate': oLap.val('thru_date'),
            'productId': oLap.val('productId'),
            'facilityId': oLap.val('facilityId'),
            'categoryId': oLap.val('categoryId'),
            'checkNPP': "NPP_TRUE",
        }, oLap.val('dateType'));
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
        	var checkNPP = "NPP_TRUE";
        	window.location.href = "exportInventoryOlapLogToPdf?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&productId=" + productIdInput + "&facilityId=" + facilityIdInput + "&checkNPP=" + checkNPP + "&categoryId=" + categoryIdInput;
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
        	var checkNPP = "NPP_TRUE";
        	window.location.href = "exportInventoryOlapLogToExcel?dateType=" + dateTypeInput + "&fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&productId=" + productIdInput + "&facilityId=" + facilityIdInput + "&checkNPP=" + checkNPP + "&categoryId=" + categoryIdInput;
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