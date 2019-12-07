<script type="text/javascript" src="../../../salesresources/js/popup.extend.js"></script>
<script type="text/javascript">
	var placeHolder = "${StringUtil.wrapString(uiLabelMap.BSPleaseChoose)}";
	var filterPlaceHolder = "${StringUtil.wrapString(uiLabelMap.BSLookingFor)}";
	<#assign productStore = delegator.findList("ProductStore", null, null, null, null, false)>
	var productStore = [
	    <#list productStore as productStoreL>
	    {
	    	productStoreId : "${productStoreL.productStoreId}",
	    	storeName: "${StringUtil.wrapString(productStoreL.get("storeName", locale))}"
	    },
	    </#list>	
	];
	var listPSDataSource = [];
  	for(var x in productStore){
    	var productStoreDataSource = {
     		text: productStore[x].storeName,
     		value: productStore[x].productStoreId,
    	}
    listPSDataSource.push(productStoreDataSource);
   	} 
   	
	var productStoreData2 = [{'text': "${StringUtil.wrapString(uiLabelMap.BSMAllObject)}", 'value': null}];
	<#if productStore?exists>
		<#list productStore as productStoreL >
			productStoreData2.push({ 'value': "${productStoreL.productStoreId?if_exists}", 'text': "${StringUtil.wrapString(productStoreL.storeName)?if_exists}"});
		</#list>
	</#if>

	var sortByData = [
		{'text': '${StringUtil.wrapString(uiLabelMap.BSNo)}', 'value': null},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSProductStore)}', 'value': 'stoIdSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSProductId)}', 'value': 'proIdSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSProductName)}', 'value': 'proNameSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', 'value': 'quaSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', 'value': 'totSort'},
		{'text': '${StringUtil.wrapString(uiLabelMap.BSCategory)}', 'value': 'cateSort'}
	];
	
	<#assign statusItem = delegator.findByAnd("StatusItem", Static["org.ofbiz.base.util.UtilMisc"].toMap("statusTypeId", "ORDER_STATUS"), null, false)>
	var statusItem = [
	    <#list statusItem as statusItemL>
	    {
	    	statusId : "${statusItemL.statusId}",
	    	description: "${StringUtil.wrapString(statusItemL.get("description", locale))}"
	    },
	    </#list>	
	];
   	
   	var statusItemData = [];
	<#if statusItem?exists>
		<#list statusItem as statusItemL >
			statusItemData.push({ 'value': '${statusItemL.statusId?if_exists}', 'text': '${StringUtil.wrapString(statusItemL.get("description", locale))?if_exists}'});
		</#list>
	</#if>

	<#assign categoryList = delegator.findByAnd("ProductCategory", Static["org.ofbiz.base.util.UtilMisc"].toMap("productCategoryTypeId", "CATALOG_CATEGORY"), null, false)!>
	var categoryList = [
	    <#list categoryList as categoryL>
	    {
	    	productCategoryId : "${categoryL.productCategoryId}",
	    	categoryName: "${StringUtil.wrapString(categoryL.get("categoryName", locale))}"
	    },
	    </#list>	
	];
	
	var categoryData= [{'text': '${StringUtil.wrapString(uiLabelMap.BSAllObject)}', 'value': null}];
	<#if categoryList?exists>
		<#list categoryList as categoryL >
			categoryData.push({ 'value': '${categoryL.productCategoryId?if_exists}', 'text': '${StringUtil.wrapString(categoryL.get("categoryName", locale))?if_exists}'});
		</#list>
	</#if>
</script>

<style>
	.olbiusChartContainer{
		margin-top: 50px!important;
	}
</style>

<script id="test">
$(function(){
        var config = {
            title: '${StringUtil.wrapString(uiLabelMap.BSRevenueProductProductStore)}',
            columns: [
            	{ text: '${uiLabelMap.BSNo2}', sortable: false, filterable: false, editable: false, pinned: true, groupable: false, draggable: false, resizable: false,
		    	  datafield: 'stt', columntype: 'number', width: '3%',
		    	  cellsrenderer: function (row, column, value) {
		    		  return '<div style=margin:4px;>' + (value + 1) + '</div>';
		    	  }
			 	},   
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductStore)}', datafield: 'productStoreName', type: 'string', width: '15%', cellsalign: 'left'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}', datafield: 'category', type: 'string', width: '15%'},
             	{ text: '${StringUtil.wrapString(uiLabelMap.BSSalesChannel)}', datafield: 'agencyId', type: 'string', width: '15%',hidden: true},
                { text: '${StringUtil.wrapString(uiLabelMap.BSCategory)}', datafield: 'agencyName', type: 'string', width: '15%', hidden: true},
                { text: '${StringUtil.wrapString(uiLabelMap.BSPercent)}', datafield: 'percent', type: 'string', width: '10%', hidden: true},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductId)}', datafield: 'productId', type: 'string', width: '15%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSProductName)}', datafield: 'productName', type: 'string', width: '29%'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSQuantity)}', datafield: 'quantity1', type: 'number', width: '10%', cellsformat: 'n2', cellsalign: 'right'},
                { text: '${StringUtil.wrapString(uiLabelMap.BSUom)}', datafield: 'unit', type: 'string', width: '10%', filterable: false},
                { text: '${StringUtil.wrapString(uiLabelMap.BSValueTotal)}', datafield: 'total1', type: 'number', width: '13%', cellsformat: 'f0', cellsalign: 'right'}
            ]
        };

        var configPopup = [
        	{
                action : 'addDropDownListMultil',
                params : [{
                    id : 'productStore',
                    label : '${StringUtil.wrapString(uiLabelMap.BSProductStore)}',
                    data : productStoreData2,
                    index: 0
                }]
            },
            {
                action : 'addDropDownListMultil',
                params : [{
                    id : 'category',
                    label : '${StringUtil.wrapString(uiLabelMap.BSCategoryId)}',
                    data : categoryData,
                    index: 0
                }]
            },
            {
                action : 'addDateTimeInput',
                params : [{
                    id : 'from_date',
                    label : '${StringUtil.wrapString(uiLabelMap.olap_fromDate)}',
                    value: OLBIUS.dateToString(past_date)
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
	            action : 'addDropDownListMultil',
	            params : [{
	                id : 'orderStatus',
	                label : '${StringUtil.wrapString(uiLabelMap.BSStatus)}',
	                data : statusItemData,
	                index: 5
	            }]
	        },
	        {
		        action : 'addDropDownList',
		        params : [{
		            id : 'sortId',
		            label : '${StringUtil.wrapString(uiLabelMap.BSMSortBy)}',
		            data : sortByData,
		            index: 0
		        }]
		    },
        ];

        var testGrid = OLBIUS.oLapGrid('test', config, configPopup, 'evaluateTurnoverSM', true);

        testGrid.funcUpdate(function (oLap) {
            oLap.update({
                'fromDate': oLap.val('from_date'),
                'thruDate': oLap.val('thru_date'),
                'productStore': oLap.val('productStore'),
                'sortId': oLap.val('sortId'),
                'category': oLap.val('category'),
                'orderStatus': oLap.val('orderStatus')
            });
        });

        testGrid.init(function () {
            testGrid.runAjax();
        }, false, function(oLap){
        	var dataAll = oLap.getAllData();
        	if(dataAll.length != 0){
            	var fromDateInput = oLap.val('from_date');
            	var thruDateInput = oLap.val('thru_date');
            	var dateFromDate = new Date(fromDateInput);
            	var dateThruDate = new Date(thruDateInput);
            	var dateFrom = dateFromDate.getTime();
            	var thruFrom = dateThruDate.getTime();
            	var sortIdInput = oLap.val('sortId');
            	var orderStatus =  oLap.val('orderStatus');
            	var productStoreInput = oLap.val('productStore');
            	var categoryInput = oLap.val('category');
            	
            	window.location.href = "exportTurnoverPPSSMToExcel?fromDate=" + dateFrom + "&thruDate=" + thruFrom + "&sortId=" + sortIdInput + "&productStore=" + productStoreInput + "&orderStatus=" + orderStatus + "&category=" + categoryInput;
        	}else{
        		bootbox.dialog("${StringUtil.wrapString(uiLabelMap.ReportCheckNotData)}", [{
        		    "label" : "${StringUtil.wrapString(uiLabelMap.CommonSubmit)}",
        		    "class" : "btn-small btn-primary width60px",
        		    }]
        		   );
        	}
    	});
    });
</script>
